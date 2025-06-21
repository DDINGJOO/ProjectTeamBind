package outbox.config;

public enum OutboxStatus {
    PENDING,
    SENT,
    FAILED,
    DEAD
}