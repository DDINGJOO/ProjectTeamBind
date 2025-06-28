package event.events;

import event.CustomEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserProfileImageUpdateEvent implements CustomEvent {
    private Long userId;
    private String profileImageUrl;

    @Override
    public String name() {
        return "UserProfileImageUpdate";
    }

    @Override
    public String getTopic() {
        return "user.profile.image.update";
    }
}