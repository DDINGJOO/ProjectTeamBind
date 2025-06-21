package image.dto.response;

import image.config.eurm.ResourceCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageResponse {
    private String referenceId;
    private ResourceCategory category;
    private String url;
    private Boolean isThumbnail;

}