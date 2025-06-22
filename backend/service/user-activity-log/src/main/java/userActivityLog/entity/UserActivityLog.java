package userActivityLog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity_log", indexes = {
        @Index(name = "idx_user_timestamp", columnList = "userId, timestamp"),
        @Index(name = "idx_url_path", columnList = "urlPath"),
        @Index(name = "idx_action_type", columnList = "actionType")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityLog {

    @Id
    private Long id;

    private Long userId;

    private String actionType;

    private LocalDateTime timestamp;

    private String urlPath;

    private boolean success;

    private Long durationMillis;

    private String clientInfo;

    private String failureReason;

    @Lob
    private String extraPayload; // Object → JSON string 저장
}