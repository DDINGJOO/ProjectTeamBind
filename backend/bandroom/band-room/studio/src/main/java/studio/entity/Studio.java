package studio.entity;


import eurm.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "studio")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Studio {
    @Id
    Long id;
    Long bandRoomId;
    String name;
    String description;


    Status status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    @OneToMany(
            mappedBy = "studio",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY  // 필요하면 EAGER 로 바꿔도 되고, 아래 join fetch 로도 해결 가능
    )
    private List<Image> images;
}
