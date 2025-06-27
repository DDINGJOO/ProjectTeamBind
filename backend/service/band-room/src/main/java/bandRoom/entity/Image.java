package bandRoom.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private BandRoom bandroom;



}
