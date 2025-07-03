package dto.studio.response;

import dto.image.response.SimpleImageResponse;
import eurm.Status;
import lombok.Builder;

import java.util.List;

@Builder
public record ProductResponse(
        Long studioId,
        Long bandRoomId,
        String name,
        String description,
        Status status,
        List<SimpleImageResponse> images
) {
}
