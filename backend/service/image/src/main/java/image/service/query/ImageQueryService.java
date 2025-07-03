package image.service.query;


import dto.image.response.ImageResponse;
import eurm.ImageStatus;
import eurm.ResourceCategory;
import exception.excrptions.ImageException;
import image.entity.Image;
import image.repository.ImageRepository;
import image.service.component.ImageUrlHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static exception.error_code.image.ImageErrorCode.IMAGE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ImageQueryService {
    private final ImageRepository imageRepository;
    private final ImageUrlHelper imageUrlHelper;

    public List<ImageResponse> getImages(ResourceCategory category, Long referenceId) {
        List<Image> images = imageRepository.findByCategoryAndReferenceId(category, referenceId);
        if (images.isEmpty()) {
            throw new ImageException(IMAGE_NOT_FOUND);
        }
        {
            return images.stream()
                    .filter(image -> image.getStatus() == ImageStatus.CONFIRMED)
                    .sorted(Comparator.comparing(Image::isThumbnail).reversed())
                    .map(image -> ImageResponse.builder()
                            .referenceId(referenceId)
                            .id(image.getId())
                            .category(image.getCategory())
                            .url(imageUrlHelper.generatePublicUrl(image))
                            .build())
                    .toList();
        }
    }
}
