package dto.studio.request;

import eurm.Status;

public record StudioUpdateRequest(
        String name,
        String description,
        Long studioId,
        Status status

) {
}
