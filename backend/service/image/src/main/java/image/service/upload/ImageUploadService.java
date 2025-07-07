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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

        String uuid         = UUID.randomUUID().toString();
        String webpFileName = uuid + ".webp";
        String datePath     = LocalDateTime.now().toLocalDate().toString().replace("-", "/");
        String storedPath   = "/" + category.name() + "/" + datePath + "/" + webpFileName;

        byte[] webpBytes;
        try {
            // ① WebP 변환
            webpBytes = Boolean.TRUE.equals(isThumbnail)
                    ? ImageUtil.toWebpThumbnail(file, THUMBNAIL_SIZE, THUMBNAIL_SIZE, 0.8f)
                    : ImageUtil.toWebp(file, 0.8f);

            // ② 변환 결과 로그
            log.info("WebP 변환: inputSize={} → outputSize={}",
                    file.getSize(), webpBytes != null ? webpBytes.length : -1);

            // ③ 결과 검증
            if (webpBytes == null || webpBytes.length == 0) {
                log.error("WebP 변환 실패: 반환된 바이트가 없습니다.");
                throw new ImageException(IMAGE_PROCESSING_ERROR);
            }

            // ④ 파일 저장
            imageStorage.store(webpBytes, storedPath);

        } catch (ImageException e) {
            throw e;  // ImageException 은 그대로 던지고
        } catch (Exception e) {
            log.error("이미지 처리 중 예외 발생", e);
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