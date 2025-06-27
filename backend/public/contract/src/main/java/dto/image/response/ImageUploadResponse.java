package dto.image.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageUploadResponse {
    private Long referenceId;
    private Long id;
}