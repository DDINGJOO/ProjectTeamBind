package dto.image.response;

import eurm.ResourceCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageResponse {
    private Long referenceId;
    private ResourceCategory category;
    private String url;
    private Long id;
}