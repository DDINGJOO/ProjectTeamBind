package bandRoom.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "band_rooms")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BandRoom {
    @Id
    @Column(name ="room_id" , nullable = false, unique = true)
    private Long RoomId;


    @Column(name ="name", nullable = false, unique = true)
    private String RoomName;


    @Column(name ="owner_id", nullable = false)
    private Long OwnerId;


    @Column(name = "owner_nickname", nullable = false)
    private String OwnerNickname;

    @Lob
    private String RoomDescription;
    private String ShortRoomDescription;


    private boolean ParkingAble;
    private String parkingDescription;


    private String Address; //from Address 모듈 with Kafka
    private String AddressDescription;


    private String phone;

    private boolean isClosed = true;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "RoomId")
    private List<Image> userInterests;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
