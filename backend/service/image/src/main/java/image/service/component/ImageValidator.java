package image.service.component;

import eurm.ImageStatus;
import eurm.ResourceCategory;
import exception.error_code.image.ImageErrorCode;
import exception.excrptions.ImageException;
import image.entity.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageValidator {

    public void validateUser(Image image, String currentUserId) {
        if (!image.getUploaderId().equals(currentUserId)) {
            throw new ImageException(ImageErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    public void validateTempStatus(Image image) {
        if (image.getStatus() != ImageStatus.TEMP) {
            throw new ImageException(ImageErrorCode.IMAGE_NOT_TEMP);
        }
    }

    public void validateCategory(Image image, ResourceCategory expectedCategory) {
        if (image.getCategory() != expectedCategory) {
            throw new ImageException(ImageErrorCode.INVALID_IMAGE_CATEGORY);
        }
    }
}
