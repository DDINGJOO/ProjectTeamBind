package outbox.entity;


import dataserializer.DataSerializer;
import event.CustomEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import primaryIdProvider.Snowflake;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_outbox")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEventEntity {

    @Id
    private Long id;

    private String type;

    @Lob
    private String payload;

    private LocalDateTime createdAt;

    private boolean sent;
    private int retryCount;
    private String lastErrorMessage;
    private LocalDateTime lastAttemptAt;
    private boolean dead;

    public static OutboxEventEntity from(CustomEvent event, Snowflake snowflake) {
        return new OutboxEventEntity(
                snowflake.nextId(),
                event.name(),
                DataSerializer.serialize(event).orElseThrow(),
                LocalDateTime.now(),
                false,
                0,
                null,
                null,
                false
        );
    }

    public void markSuccess() {
        this.sent = true;
    }

    public void markFailure(String errorMessage) {
        this.retryCount++;
        this.lastErrorMessage = errorMessage;
        this.lastAttemptAt = LocalDateTime.now();
        if (retryCount >= 5) {
            this.dead = true;
        }
    }

    public boolean isReadyToSend() {
        return !sent && !dead;
    }
}