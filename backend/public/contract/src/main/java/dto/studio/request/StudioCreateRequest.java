package dto.studio.request;



public record StudioCreateRequest(
        Long bandRoomId,
        String name,
        String description
) {
}
