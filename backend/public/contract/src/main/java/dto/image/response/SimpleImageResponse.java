package dto.image.response;

import lombok.Builder;


@Builder
public record SimpleImageResponse(
        Long id,
        String url
) {
}

