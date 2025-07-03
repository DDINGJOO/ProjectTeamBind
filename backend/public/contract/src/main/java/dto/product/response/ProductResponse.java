package dto.product.response;

import dto.image.response.SimpleImageResponse;
import lombok.Builder;

import java.util.List;


@Builder
public record ProductResponse(
        Long productId,
        Long bandRoomId,
        String name,
        String description,
        Long stock,
        Long price,
        List<SimpleImageResponse> images
) {
}
