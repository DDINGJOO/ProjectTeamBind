package dto.product.request;


public record ProductCreateRequest(
        Long bandRoomId,
        String name,
        String description,
        Long stock,
        Long price
) {
}
