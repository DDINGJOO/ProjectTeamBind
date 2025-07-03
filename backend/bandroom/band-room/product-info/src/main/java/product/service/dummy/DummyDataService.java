package product.service.dummy;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import product.entity.Image;
import product.entity.Product;
import product.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class DummyDataService {

    private final ProductRepository productRepository;

    @Transactional
    public void initDummyData() {
        // 이미 생성된 데이터가 있으면 스킵
        if (productRepository.count() > 0) {
            return;
        }

        List<Product> products = new ArrayList<>();
        long productId = 1, imageId = 1;

        for (long roomId = 1; roomId <= 100; roomId++) {
            for (int p = 0; p < 10; p++) {
                Product product = Product.builder()
                        .id(productId)
                        .roomId(roomId)
                        .name("Dummy Product " + productId)
                        .price(10_000L)
                        .stock(100L)
                        .description("Description for product " + productId)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .images(new ArrayList<>())
                        .build();

                for (int i = 1; i <= 5; i++) {
                    Image img = Image.builder()
                            .id(imageId++)
                            .url("https://via.placeholder.com/300?text=Prod"
                                    + productId + "-Img" + i)
                            .product(product)
                            .build();
                    product.getImages().add(img);
                }

                products.add(product);
                productId++;
            }
        }

        productRepository.saveAll(products);
    }
}