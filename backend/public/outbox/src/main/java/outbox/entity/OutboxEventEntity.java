package outbox.entity;


import dataserializer.DataSerializer;
import event.CustomEvent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import outbox.config.OutboxStatus;
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

    private String eventType;

    @Lob
    private String payload;

    private String topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status; // SENT, PENDING, FAILED

    private int retryCount;

    private String lastErrorMessage;

    private LocalDateTime lastAttemptAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static OutboxEventEntity from(CustomEvent event, Snowflake snowflake) {
        return new OutboxEventEntity(
                snowflake.nextId(),
                event.getClass().getSimpleName(),                   // eventType (e.g. UserRegisteredEvent)
                DataSerializer.serialize(event).orElseThrow(),     // payload
                event.getTopic(),                                   // topic (e.g. "user.registered")
                OutboxStatus.PENDING,                               // status
                0,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public void markSuccess() {
        this.status = OutboxStatus.SENT;
        this.updatedAt = LocalDateTime.now();
    }

    public void markFailure(String errorMessage) {
        this.retryCount++;
        this.status = OutboxStatus.FAILED;
        this.lastErrorMessage = errorMessage;
        this.lastAttemptAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (retryCount >= 5) {
            this.status = OutboxStatus.DEAD;
        }
    }

    public boolean isReadyToSend() {
        return this.status == OutboxStatus.PENDING;
    }
}