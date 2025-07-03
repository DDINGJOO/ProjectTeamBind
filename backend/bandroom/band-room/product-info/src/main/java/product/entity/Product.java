package product.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name ="product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    Long id;
    Long roomId;

    String name;
    Long price;
    Long stock;
    String description;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY  // 필요하면 EAGER 로 바꿔도 되고, 아래 join fetch 로도 해결 가능
    )
    private List<Image> images;
}
