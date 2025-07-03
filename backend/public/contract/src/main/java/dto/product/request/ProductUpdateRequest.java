package dto.product.request;

public record ProductUpdateRequest(
        String name,
        String description,
        Long productId,
        Long price,
        Long stock

) {
}

