package dto.image.request;


import eurm.ResourceCategory;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageConfirmRequest {
    private ResourceCategory category;
    private Long uploaderId;
    private Long referenceId;
    private List<Long> imageIds;
}