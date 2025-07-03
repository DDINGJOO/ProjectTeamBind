package image.service;

import dto.image.request.ImageConfirmRequest;
import dto.image.response.ImageResponse;
import eurm.ImageStatus;
import eurm.ResourceCategory;
import image.entity.Image;
import image.repository.ImageRepository;
import image.service.component.ImageStatusChanger;
import image.service.component.ImageUrlHelper;
import image.service.component.ImageValidator;
import image.service.confirmation.ImageConfirmationService;
import image.service.image_store_impl.LocalImageStorage;
import image.service.lifecycle.ImageLifecycleService;
import image.service.query.ImageQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * 단위 테스트 - ImageService
 *
 * 실제 서비스 흐름을 검증합니다:
 * - 이미지 업로드 → 저장
 * - 이미지 확정 시 validator + 상태전환 처리
 * - 이미지 삭제 시 PENDING_DELETE로 전환
 * - CONFIRMED 상태 필터링 후 퍼블릭 URL 생성
 */
@DisplayName("ImageService 비즈니스 흐름 테스트")
class ImageServiceTest {

    @Mock private ImageRepository imageRepository;
    @Mock private LocalImageStorage imageStorage;
    @Mock
    private ImageValidator validator;
    @Mock private ImageStatusChanger statusChanger;
    @Mock private ImageUrlHelper urlHelper;
    @Mock private primaryIdProvider.Snowflake snowflake;

    @InjectMocks
    private ImageConfirmationService  imageConfirmationService;
    @InjectMocks
    private ImageQueryService  imageQueryService;
    @InjectMocks
    private ImageLifecycleService imageLifecycleService;

    ImageServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("CONFIRMED 상태 이미지만 조회하여 URL 반환")
    void getImages_shouldReturnConfirmedImages() {
        Image img = Image.builder()
                .status(ImageStatus.CONFIRMED)
                .storedPath("/img/test.webp")
                .isThumbnail(false)
                .build();

        when(imageRepository.findByCategoryAndReferenceId(ResourceCategory.POST, 123123L))
                .thenReturn(List.of(img));
        when(urlHelper.generatePublicUrl(img)).thenReturn("https://cdn.com/img/test.webp");

        List<ImageResponse> result = imageQueryService.getImages(ResourceCategory.POST, 123123L);

        assertEquals(1, result.size());
        assertEquals("https://cdn.com/img/test.webp", result.get(0).getUrl());
    }

    @Test
    @DisplayName("이미지 확정 시 검증 후 상태 업데이트 및 refId 설정")
    void confirmImages_shouldValidateAndUpdate() {
        Image image = Image.builder()
                .id(1L)
                .status(ImageStatus.TEMP)
                .uploaderId(1231123L)
                .category(ResourceCategory.POST)
                .build();

        ImageConfirmRequest req = new ImageConfirmRequest();
        ReflectionTestUtils.setField(req, "imageIds", List.of(1L));
        ReflectionTestUtils.setField(req, "category", ResourceCategory.POST);
        ReflectionTestUtils.setField(req, "referenceId", 123123L);
        ReflectionTestUtils.setField(req, "uploaderId", 1231123L);

        when(imageRepository.findAllById(List.of(1L))).thenReturn(List.of(image));

        imageConfirmationService.confirmImages(req);

        verify(validator).validateUser(image, 1231123L);
        verify(validator).validateTempStatus(image);
        verify(validator).validateCategory(image, ResourceCategory.POST);
        verify(statusChanger).changeStatus(
                eq(image),
                eq(ImageStatus.TEMP),
                eq(ImageStatus.CONFIRMED),
                eq(exception.error_code.image.ImageErrorCode.IMAGE_NOT_TEMP)
        );
    }

    @DisplayName("이미지 삭제 요청 시 PENDING_DELETE로 전환")
    @Test
    void deleteImage_shouldSetPendingDelete() {
        Image image = Image.builder()
                .id(1L)
                .status(ImageStatus.CONFIRMED)
                .build();

        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

        imageLifecycleService.deleteImage(1L);

        verify(statusChanger).changeStatus(
                any(Image.class),
                isNull(),
                eq(ImageStatus.PENDING_DELETE),
                any()
        );
    }
}