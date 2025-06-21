package image.service;


import exception.excrptions.ImageException;
import image.config.eurm.ImageStatus;
import image.config.eurm.ImageVisibility;
import image.config.eurm.ResourceCategory;
import image.dto.request.ImageConfirmRequest;
import image.dto.response.ImageResponse;
import image.dto.response.ImageUploadResponse;
import image.entity.Image;
import image.repository.ImageRepository;
import image.service.component.ImageStatusChanger;
import image.service.component.ImageUrlHelper;
import image.service.component.ImageValidator;
import image.service.image_store_impl.LocalImageStorage;
import image.service.util.ImageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import primaryIdProvider.Snowflake;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static exception.error_code.image.ImageErrorCode.*;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final int THUMBNAIL_SIZE = 256;

    private final ImageRepository imageRepository;
    private final LocalImageStorage imageStorage;
    private final Snowflake snowflake;
    private final ImageValidator validator;
    private final ImageStatusChanger statusChanger;
    private final ImageUrlHelper imageUrlHelper;

    public ImageUploadResponse upload(MultipartFile file,
                                      ResourceCategory category,
                                      String uploaderId,
                                      ImageVisibility visibility,
                                      Boolean isThumbnail) {

        String uuid = UUID.randomUUID().toString();
        String webpFileName = uuid + ".webp";
        String datePath = LocalDateTime.now().toLocalDate().toString().replace("-", "/");
        String storedPath = "/" + category.name() + "/" + datePath + "/" + webpFileName;

        try {
            byte[] webpBytes = Boolean.TRUE.equals(isThumbnail)
                    ? ImageUtil.toWebpThumbnail(file, THUMBNAIL_SIZE, THUMBNAIL_SIZE, 0.8f)
                    : ImageUtil.toWebp(file, 0.8f);
            imageStorage.store(webpBytes, storedPath);
        } catch (Exception e) {
            throw new ImageException(IMAGE_PROCESSING_ERROR);
        }

        Image image = Image.builder()
                .id(snowflake.nextId())
                .uuidName(webpFileName)
                .originalName(file.getOriginalFilename())
                .storedPath(storedPath)
                .contentType("image/webp")
                .fileSize(file.getSize())
                .category(category)
                .isThumbnail(isThumbnail)
                .uploaderId(uploaderId)
                .status(ImageStatus.TEMP)
                .visibility(visibility)
                .createdAt(LocalDateTime.now())
                .build();

        imageRepository.save(image);

        return ImageUploadResponse.builder()
                .id(image.getId())
                .build();
    }

    public List<ImageUploadResponse> uploadImages(List<MultipartFile> files,
                                                  ResourceCategory category,
                                                  String uploaderId,
                                                  ImageVisibility visibility) {
        List<ImageUploadResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            responses.add(upload(file, category, uploaderId, visibility, false));
        }
        return responses;
    }

    public List<ImageResponse> getImageUrls(ResourceCategory category, String referenceId) {
        List<Image> images = imageRepository.findByCategoryAndReferenceId(category, referenceId);
        if (images.isEmpty()) {
            throw new ImageException(IMAGE_NOT_FOUND);
        }

        return images.stream()
                .filter(image -> image.getStatus() == ImageStatus.CONFIRMED)
                .map(image -> ImageResponse.builder()
                        .referenceId(referenceId)
                        .isThumbnail(image.isThumbnail())
                        .url(imageUrlHelper.generatePublicUrl(image))
                        .build())
                .toList();
    }

    public void confirmImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageException(IMAGE_NOT_FOUND));
        statusChanger.changeStatus(image, ImageStatus.TEMP, ImageStatus.CONFIRMED, IMAGE_NOT_TEMP);
    }

    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageException(IMAGE_NOT_FOUND));
        statusChanger.changeStatus(image, null, ImageStatus.PENDING_DELETE, IMAGE_ALREADY_PENDING_DELETE);
    }

    public void markAsConfirmed(ResourceCategory category, String referenceId) {
        List<Image> images = imageRepository.findByCategoryAndReferenceId(category, referenceId);
        statusChanger.changeStatusBulk(images, ImageStatus.CONFIRMED);
        imageRepository.saveAll(images);
    }

    public void markAsPendingDeleteExceptTemp(ResourceCategory category, String referenceId) {
        List<Image> images = imageRepository.findByCategoryAndReferenceId(category, referenceId);
        if (images.isEmpty()) {
            throw new ImageException(IMAGE_NOT_FOUND);
        }

        List<Image> targets = images.stream()
                .filter(img -> img.getStatus() != ImageStatus.TEMP)
                .toList();

        statusChanger.changeStatusBulk(targets, ImageStatus.PENDING_DELETE);
        imageRepository.saveAll(targets);
    }

    @Transactional
    public void confirmImages(ImageConfirmRequest request, String currentUserId) {
        List<Image> images = imageRepository.findAllById(request.getImageIds());
        if (images.size() != request.getImageIds().size()) {
            throw new ImageException(IMAGE_NOT_FOUND);
        }

        for (Image image : images) {
            validator.validateUser(image, currentUserId);
            validator.validateTempStatus(image);
            validator.validateCategory(image, request.getCategory());

            image.setReferenceId(request.getReferenceId());
            statusChanger.changeStatus(image, ImageStatus.TEMP, ImageStatus.CONFIRMED, IMAGE_NOT_TEMP);
        }

        imageRepository.saveAll(images);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void deleteExpiredImages() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(1);

        List<Image> expired = new ArrayList<>();
        expired.addAll(imageRepository.findByStatusAndCreatedAtBefore(ImageStatus.TEMP, cutoff));
        expired.addAll(imageRepository.findByStatusAndPendingDeleteAtBefore(ImageStatus.PENDING_DELETE, cutoff));
        expired.addAll(imageRepository.findByStatus(ImageStatus.REJECTED));

        for (Image image : expired) {
            imageStorage.delete(image.getStoredPath());
            imageRepository.delete(image);
        }
    }
}