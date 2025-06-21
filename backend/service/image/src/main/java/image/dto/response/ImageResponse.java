package image.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageResponse {
    private String referenceId;
    private String url;
    private Boolean isThumbnail;

}