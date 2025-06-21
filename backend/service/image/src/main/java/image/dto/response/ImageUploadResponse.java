package image.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageUploadResponse {
    private String referenceId;
    private Long id;
}