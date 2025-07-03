package image.service.upload;

import dto.image.response.ImageUploadResponse;
import eurm.ImageStatus;
import eurm.ImageVisibility;
import eurm.ResourceCategory;
import exception.excrptions.ImageException;
import image.entity.Image;
import image.repository.ImageRepository;
import image.service.component.ImageStatusChanger;
import image.service.component.ImageUrlHelper;
import image.service.component.ImageValidator;
import image.service.image_store_impl.LocalImageStorage;
import image.service.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import primaryIdProvider.Snowflake;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static exception.error_code.image.ImageErrorCode.IMAGE_PROCESSING_ERROR;


@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private static final int THUMBNAIL_SIZE = 256;

    private final ImageRepository imageRepository;
    private final LocalImageStorage imageStorage;
    private final Snowflake snowflake;
    private final ImageValidator validator;
    private final ImageStatusChanger statusChanger;
    private final ImageUrlHelper imageUrlHelper;

    public ImageUploadResponse upload(MultipartFile file,
                                      ResourceCategory category,
                                      Long uploaderId,
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
                                                  Long uploaderId,
                                                  ImageVisibility visibility) {
        List<ImageUploadResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            responses.add(upload(file, category, uploaderId, visibility, false));
        }
        return responses;
    }

    }