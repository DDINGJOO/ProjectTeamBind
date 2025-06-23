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

public class UserCreatedEvent implements CustomEvent {
    private Long userId;
    private String email;

    @Override
    public String name() {
        return "userCreated";
    }

    @Override
    public String getTopic() {
        return "user.created";
    }
}