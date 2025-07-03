package image.service.eventPublish;

import dto.image.response.ImageResponse;

import java.util.List;

public interface EventPublish {


    void ImageUpdateSelector(List<ImageResponse> req);
}
