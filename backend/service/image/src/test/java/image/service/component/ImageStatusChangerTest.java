package image.service.component;

import eurm.ImageStatus;
import exception.error_code.image.ImageErrorCode;
import exception.excrptions.ImageException;
import image.entity.Image;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 단위 테스트 - ImageStatusChanger
 *
 * 이미지의 상태 변경 처리 로직을 검증합니다.
 * - from 상태 조건 불일치 시 예외
 * - 상태 전환 성공 여부
 * - 일괄 상태 변경 처리
 */
@DisplayName("ImageStatusChanger 상태 전환 테스트")
class ImageStatusChangerTest {

    private final ImageStatusChanger changer = new ImageStatusChanger();

    @Test
    @DisplayName("정상적인 상태 전이 시 성공")
    void changeStatus_shouldUpdateStatus_whenValid() {
        Image image = Image.builder().status(ImageStatus.TEMP).build();

        changer.changeStatus(image, ImageStatus.TEMP, ImageStatus.CONFIRMED, ImageErrorCode.IMAGE_NOT_TEMP);

        assertEquals(ImageStatus.CONFIRMED, image.getStatus());
    }

    @Test
    @DisplayName("상태 전이 조건 불일치 시 예외 발생")
    void changeStatus_shouldThrow_whenStatusMismatch() {
        Image image = Image.builder().status(ImageStatus.CONFIRMED).build();

        ImageException exception = assertThrows(ImageException.class, () ->
                changer.changeStatus(image, ImageStatus.TEMP, ImageStatus.PENDING_DELETE, ImageErrorCode.IMAGE_NOT_TEMP)
        );

        assertEquals(ImageErrorCode.IMAGE_NOT_TEMP, exception.getErrorCode());
    }

    @Test
    @DisplayName("일괄 상태 전이 정상 작동")
    void changeStatusBulk_shouldUpdateAllStatuses() {
        Image image1 = Image.builder().status(ImageStatus.TEMP).build();
        Image image2 = Image.builder().status(ImageStatus.TEMP).build();

        changer.changeStatusBulk(List.of(image1, image2), ImageStatus.PENDING_DELETE);

        assertEquals(ImageStatus.PENDING_DELETE, image1.getStatus());
        assertEquals(ImageStatus.PENDING_DELETE, image2.getStatus());
    }
}