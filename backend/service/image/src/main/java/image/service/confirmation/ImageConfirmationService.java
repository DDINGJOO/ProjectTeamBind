package image.service.confirmation;

import dto.image.request.ImageConfirmRequest;
import dto.image.response.ImageResponse;
import eurm.ImageStatus;
import exception.excrptions.ImageException;
import image.entity.Image;
import image.repository.ImageRepository;
import image.service.component.ImageStatusChanger;
import image.service.component.ImageValidator;
import image.service.eventPublish.impl.EventPublishIImpl;
import image.service.lifecycle.ImageLifecycleService;
import image.service.query.ImageQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static exception.error_code.image.ImageErrorCode.IMAGE_NOT_FOUND;
import static exception.error_code.image.ImageErrorCode.IMAGE_NOT_TEMP;


@Service
@RequiredArgsConstructor
public class ImageConfirmationService {
    private final ImageRepository imageRepository;
    private final ImageValidator validator;
    private final ImageStatusChanger statusChanger;
    private final ImageLifecycleService  imageLifecycleService;
    private final ImageQueryService  imageQueryService;
    private final EventPublishIImpl  eventPublish;

    @Transactional
    public void confirmImages(ImageConfirmRequest request) {
        List<Image> images = imageRepository.findAllById(request.getImageIds());
        if (images.size() != request.getImageIds().size()) {
            throw new ImageException(IMAGE_NOT_FOUND);
        }

        for (Image image : images) {
            validator.validateUser(image, request.getUploaderId());
            validator.validateTempStatus(image);
            validator.validateCategory(image, request.getCategory());

            image.setReferenceId(request.getReferenceId());
            statusChanger.changeStatus(image, ImageStatus.TEMP, ImageStatus.CONFIRMED, IMAGE_NOT_TEMP);
        }

        imageRepository.saveAll(images);
    }




    @Transactional
    public void changeImages(ImageConfirmRequest request)
    {
        List<Image> oldImages = imageRepository.findAllById(request.getImageIds());
        if(!oldImages.isEmpty())
        {
            oldImages.forEach(image -> {
                if(request.getImageIds().stream().noneMatch((imageId -> imageId.equals(image.getId()))))
                {
                    imageLifecycleService.deleteImage(image.getId());
                }
            });
        }
        confirmImages(request);
        List<ImageResponse>response = imageQueryService.getImages(request.getCategory(), request.getReferenceId());
        eventPublish.ImageUpdateSelector(response);
    }
}
