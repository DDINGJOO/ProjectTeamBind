package image.service.component;


import eurm.ImageStatus;
import exception.error_code.image.ImageErrorCode;
import exception.excrptions.ImageException;
import image.entity.Image;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImageStatusChanger {

    public void changeStatus(Image image, ImageStatus expectedCurrent, ImageStatus to, ImageErrorCode errorIfNotMatch) {
        if (expectedCurrent != null && image.getStatus() != expectedCurrent) {
            throw new ImageException(errorIfNotMatch);
        }
        if (to == ImageStatus.PENDING_DELETE && image.getStatus() == ImageStatus.PENDING_DELETE) {
            throw new ImageException(errorIfNotMatch);
        }
        image.setStatus(to);
    }

    public void changeStatusBulk(List<Image> images, ImageStatus to) {
        for (Image image : images) {
            image.setStatus(to);
        }
    }
}