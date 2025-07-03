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
public class EmailConfirmedRequestEvent implements CustomEvent {
    private Long userId;
    private String email;
    private String code;


    @Override
    public String name() {
        return "EmailConfirmedRequest";
    }

    @Override
    public String getTopic() {
        return "email.confirmed.request";
    }
}