package dto.image.request;


import eurm.ResourceCategory;
import lombok.Getter;

import java.util.List;

@Getter
public class ImageConfirmRequest {
    private ResourceCategory category;
    private Long uploaderId;
    private Long referenceId;
    private List<Long> imageIds;
}