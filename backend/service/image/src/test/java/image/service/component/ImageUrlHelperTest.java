package image.service.component;

import image.entity.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.ReflectionTestUtils.setField;


/**
 * 단위 테스트 - ImageUrlHelper
 *
 * 이미지 저장 경로에 대해 퍼블릭 URL을 생성하는 책임을 검증합니다.
 * nginx prefix 설정과 결합 결과가 의도한 URL로 생성되는지 확인합니다.
 */
@DisplayName("ImageUrlHelper 공개 URL 생성 테스트")
class ImageUrlHelperTest {

    private final ImageUrlHelper helper = new ImageUrlHelper();

    @BeforeEach
    void setup() {
        setField(helper, "publicUrlPrefix", "https://static.test.com");
    }

    @Test
    @DisplayName("URL prefix와 storedPath가 잘 연결되는지 검증")
    void generatePublicUrl_shouldConcatenatePrefixAndPath() {
        Image image = Image.builder()
                .storedPath("/POST/2025/06/21/test.webp")
                .build();

        String result = helper.generatePublicUrl(image);

        assertEquals("https://static.test.com/POST/2025/06/21/test.webp", result);
    }
}