package product.consume;

import dataserializer.DataSerializer;
import event.events.ImageUpdateEvent.ProductImageUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import product.entity.Image;
import product.entity.Product;
import product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Component
public class ProductImageUpdateConsumer {

    private final ProductRepository productRepository;

    @KafkaListener(
            topics = "product.image.update",
            groupId = "product-consumer-group"
    )
    public void consume(String message) {
        try {
            log.info("Received raw PRODUCT IMAGE UPDATE EVENT JSON: {}", message);
            ProductImageUpdateEvent event = DataSerializer
                    .deserialize(message, ProductImageUpdateEvent.class)
                    .orElseThrow(() -> new IllegalArgumentException("ProductImageUpdateEvent 역직렬화 실패"));

            Product product = productRepository.findById(event.getReferenceId()).orElseThrow(() -> new IllegalArgumentException("NOT FOUND STUDIO ID"));
            List<Image> images = new ArrayList<>();
            event.getImages().forEach(
                    (e,k)-> images.add(
                            Image.builder()
                                    .id(e)
                                    .url(k)
                                    .product(product)
                                    .build()
                    )
            );
            product.setImages(images);
            productRepository.save(product);

        }
        catch (Exception e) {
            log.error("Error while processing raw IMAGE UPDATE EVENT JSON: {}", message, e);
    }

    }
}
