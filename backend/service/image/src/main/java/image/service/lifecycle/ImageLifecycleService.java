package image.service.lifecycle;

import eurm.ImageStatus;
import eurm.ResourceCategory;
import exception.excrptions.ImageException;
import image.entity.Image;
import image.repository.ImageRepository;
import image.service.component.ImageStatusChanger;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static exception.error_code.image.ImageErrorCode.IMAGE_ALREADY_PENDING_DELETE;
import static exception.error_code.image.ImageErrorCode.IMAGE_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class ImageLifecycleService {
    private final ImageRepository imageRepository;
    private final ImageStatusChanger statusChanger;




    @Transactional
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageException(IMAGE_NOT_FOUND));
        statusChanger.changeStatus(image, null, ImageStatus.PENDING_DELETE, IMAGE_ALREADY_PENDING_DELETE);
        imageRepository.save(image);
    }



    @Transactional
    public void markAllAsPendingDelete(ResourceCategory category, Long referenceId) {
        List<Image> images = imageRepository.findByCategoryAndReferenceId(category, referenceId);
        if (images.isEmpty()) {
            throw new ImageException(IMAGE_NOT_FOUND);
        }


        statusChanger.changeStatusBulk(images, ImageStatus.PENDING_DELETE);
        imageRepository.saveAll(images);
    }



}
