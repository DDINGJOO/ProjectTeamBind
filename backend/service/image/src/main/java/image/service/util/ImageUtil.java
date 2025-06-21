package image.service.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtil {
    static {
        ImageIO.scanForPlugins(); // 플러그인 자동 등록 (생략해도 되는 경우도 많음)
    }

    public static byte[] toWebp(MultipartFile file, float quality) throws IOException {
        BufferedImage input = ImageIO.read(file.getInputStream());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 품질 옵션 커스텀하려면 WebPWriteParam 활용
        ImageIO.write(input, "webp", os);
        return os.toByteArray();
    }

    public static byte[] toWebpThumbnail(MultipartFile file, int width, int height, float quality) throws IOException {
        BufferedImage input = ImageIO.read(file.getInputStream());
        if (input == null) throw new IOException("이미지 포맷을 읽을 수 없습니다");
        BufferedImage thumb = resize(input, width, height);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(thumb, "webp", os);
        return os.toByteArray();
    }

    // 아래는 간단한 리사이즈 util (썸네일용)
    private static BufferedImage resize(BufferedImage img, int width, int height) {
        BufferedImage output = new BufferedImage(width, height, img.getType());
        output.getGraphics().drawImage(img, 0, 0, width, height, null);
        return output;
    }
}