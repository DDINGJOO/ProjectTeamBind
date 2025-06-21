package image.service.component;

import exception.excrptions.ImageException;
import image.config.eurm.ImageStatus;
import image.config.eurm.ResourceCategory;
import image.entity.Image;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 단위 테스트 - ImageValidator
 *
 * 이 테스트는 이미지 유효성 검증기인 {@link ImageValidator}의 주요 검증 메서드를 테스트합니다.
 * 유저 일치 여부, TEMP 상태 여부, 카테고리 일치 여부를 검사하며,
 * ImageService의 확정 흐름에서 선행 조건을 만족시키는 데 사용됩니다.
 */
@DisplayName("ImageValidator 유효성 검증 테스트")
class ImageValidatorTest {

    private final ImageValidator validator = new ImageValidator();

    @Test
    @DisplayName("업로더 불일치 시 예외 발생")
    void validateUser_shouldThrow_whenUploaderMismatch() {
        Image image = Image.builder().uploaderId("userA").build();

        assertThrows(ImageException.class, () -> validator.validateUser(image, "userB"));
    }

    @Test
    @DisplayName("TEMP 상태가 아니면 예외 발생")
    void validateTempStatus_shouldThrow_whenNotTemp() {
        Image image = Image.builder().status(ImageStatus.CONFIRMED).build();

        assertThrows(ImageException.class, () -> validator.validateTempStatus(image));
    }

    @Test
    @DisplayName("카테고리 불일치 시 예외 발생")
    void validateCategory_shouldThrow_whenCategoryMismatch() {
        Image image = Image.builder().category(ResourceCategory.POST).build();

        assertThrows(ImageException.class, () -> validator.validateCategory(image, ResourceCategory.PROFILE));
    }

    @Test
    @DisplayName("모든 조건이 유효하면 예외 없음")
    void allValidations_shouldPass_whenCorrect() {
        Image image = Image.builder()
                .uploaderId("userA")
                .status(ImageStatus.TEMP)
                .category(ResourceCategory.POST)
                .build();

        assertDoesNotThrow(() -> {
            validator.validateUser(image, "userA");
            validator.validateTempStatus(image);
            validator.validateCategory(image, ResourceCategory.POST);
        });
    }
}
