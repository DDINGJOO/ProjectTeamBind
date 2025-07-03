package event.events.ImageUpdateEvent;

import event.CustomEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BandRoomImageUpdateEvent implements CustomEvent {
    private Long referenceId;
    private HashMap<Long,String> images;

    @Override
    public String name() {
        return "BandRoomImageUpdateEvent";
    }

    @Override
    public String getTopic() {
        return "band.room.image.update";
    }
}