package bandroominfo.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Address {

    @Id
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "room_id", nullable = false)
    BandRoom bandRoom;

    String addressDetail;
    String addressDescription;

}
