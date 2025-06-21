package image.service.component;


import image.entity.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageUrlHelper {

    @Value("${image.upload.nginx.url}")
    private String publicUrlPrefix;

    public String generatePublicUrl(Image image) {
        return publicUrlPrefix + image.getStoredPath();
    }
}