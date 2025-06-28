package dto.image.request;


import eurm.ResourceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ImageConfirmRequest {
    private ResourceCategory category;
    private Long uploaderId;
    private Long referenceId;
    private List<Long> imageIds;
}