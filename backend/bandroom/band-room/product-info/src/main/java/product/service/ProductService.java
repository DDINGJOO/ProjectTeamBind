package product.service;


import dto.image.response.SimpleImageResponse;
import dto.product.request.ProductCreateRequest;
import dto.product.request.ProductUpdateRequest;
import dto.product.response.ProductResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import primaryIdProvider.Snowflake;
import product.entity.Image;
import product.entity.Product;
import product.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final Snowflake snowflake;

    @Transactional
    public void create(ProductCreateRequest req) {
        productRepository.save(
                Product.builder()
                        .id(snowflake.nextId())
                        .price(req.price())
                        .stock(req.stock())
                        .name(req.name())
                        .createdAt(LocalDateTime.now())
                        .roomId(req.bandRoomId())
                        .build()
        );
    }


    public ProductResponse getProduct(Long ProductId) {
        try {
            Product product = productRepository.findById(ProductId)
                    .orElseThrow();

            return toResponse(product);
        } catch (Exception e) {
            log.error("Product Not Found");
            return null;
        }
    }

    public List<ProductResponse> getProducts(Long bandRoomId) {
        try {
            List<Product> products = productRepository.findALlByRoomId(bandRoomId);
            List<ProductResponse> productResponseList = new ArrayList<>();
            products.forEach(product -> productResponseList.add(toResponse(product)));
            return productResponseList;
        } catch (Exception e) {
            log.error("Product Not Found");
            return null;
        }
    }

    @Transactional
    public void updateProduct(ProductUpdateRequest req) {
        try {
            Product product = productRepository.findById(req.productId()).orElseThrow();
            product.setName(req.name());
            product.setPrice(req.price());
            product.setPrice(req.price());
            product.setUpdatedAt(LocalDateTime.now());
            product.setStock(req.stock());
            productRepository.save(product);
        } catch (Exception e) {
            log.error("Product Not Found");
        }
    }

    @Transactional
    public void deleteProduct(Long productId) {
        try {
            productRepository.deleteById(productId);
        } catch (Exception e) {
            log.error("Product Not Found");
        }
    }

    @Transactional
    public void deleteAll(Long bandRoomId) {
        try {
            productRepository.deleteByRoomId(bandRoomId);
        } catch (Exception e) {
            log.error("Product Not Found");
        }
    }

    @Transactional
    public boolean sell(Long productId, Long quantity) {
        try {
            Product product = productRepository.findById(productId).orElseThrow();
            if (product.getStock() < quantity) {
                return false;
            }

            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
            return true;

        } catch (Exception e) {
            log.error("Product Not Found");
            return false;
        }
    }


    public Long checkStock(Long productId) {
        try {
            Product product = productRepository.findById(productId).orElseThrow();
            return product.getStock();
        } catch (Exception e) {
            log.error("Product Not Found");
            return null;
        }
    }


    private ProductResponse toResponse(Product product) {
        // studio.getImages() 를 ImageResponse 로 변환해 리스트로 수집
        var imageResponses = product.getImages().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ProductResponse.builder()
                .productId(product.getId())
                .description(product.getDescription())
                .bandRoomId(product.getRoomId())
                .price(product.getPrice())
                .stock(product.getStock())
                .name(product.getName())
                .images(imageResponses)// 여기에 매핑된 List<ImageResponse> 전달
                .build();
    }

    private SimpleImageResponse toResponse(Image image) {
        return SimpleImageResponse.builder()
                .id(image.getId())
                .url(image.getUrl())
                .build();
    }
}



