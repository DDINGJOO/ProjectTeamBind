package image.dto.request;

import image.config.eurm.ResourceCategory;
import lombok.Getter;

import java.util.List;

@Getter
public class ImageConfirmRequest {
    private ResourceCategory category;
    private String referenceId;
    private List<Long> imageIds;
}