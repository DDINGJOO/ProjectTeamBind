package bandroominfo.entity;


import eurm.Status;
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
    private Long roomId;
    @Column(name ="name", nullable = false, unique = true)
    private String roomName;


    @Column(name ="owner_id", nullable = false)
    private Long ownerId;



    @Lob
    private String roomDescription;
    private String shortRoomDescription;


    private boolean isParkable;
    private String parkDescription;



    private Long phoneNumber;

    @OneToMany( mappedBy = "BandRoom",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;
    @OneToOne( mappedBy = "BandRoom",cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;


    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime pendingDeletedAt;
}
