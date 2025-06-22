package logMetadata;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class LogEventTest {

    @Test
    @DisplayName("성공 케이스: 상세 payload 포함하여 로그 생성")
    void createLogEvent_withPayload() {
        LogMetadata metadata = LogMetadata.nowKST(true, 150L, "127.0.0.1");

        LogEvent<Map<String, Object>> event = LogEvent.<Map<String, Object>>now(
                1001L,
                LogActionType.VIEW_BANDROOM,
                metadata,
                Map.of("bandroomId", "BR-101")
        );

        assertEquals("VIEW_BANDROOM", event.name());
        assertEquals("activity.log.save", event.getTopic());
        assertEquals("BR-101", event.getPayload().get("bandroomId"));
    }

    @Test
    @DisplayName("payload 없이도 로그 이벤트를 생성할 수 있다")
    void createLogEvent_withoutPayload() {
        LogMetadata metadata = LogMetadata.nowKST(true, 200L, "Mozilla/5.0");

        LogEvent<Void> event = LogEvent.now(2002L, LogActionType.DELETE_POST, metadata, null);

        assertEquals(LogActionType.DELETE_POST, event.getActionType());
        assertNull(event.getPayload());
    }

    @Test
    @DisplayName("실패한 요청의 로그를 생성할 수 있다")
    void createFailureLogEvent() {
        LogMetadata metadata = LogMetadata.builder()
                .timestamp(ZonedDateTime.now(ZoneId.of("Asia/Seoul")))
                .success(false)
                .clientInfo("Chrome")
                .durationMillis(300L)
                .failureReason("POST_NOT_FOUND")
                .build();

        LogEvent<Map<String, Object>> event = LogEvent.<Map<String, Object>>now(
                999L,
                LogActionType.DELETE_POST,
                metadata,
                Map.of("postId", "P-404")
        );

        assertFalse(event.getMetadata().isSuccess());
        assertEquals("POST_NOT_FOUND", event.getMetadata().getFailureReason());
        assertEquals("P-404", event.getPayload().get("postId"));
    }

    @Test
    @DisplayName("context 필드에 추가 정보를 넣을 수 있다")
    void createLogMetadata_withContext() {
        LogMetadata metadata = LogMetadata.builder()
                .timestamp(ZonedDateTime.now(ZoneId.of("Asia/Seoul")))
                .success(true)
                .durationMillis(100L)
                .clientInfo("Edge")
                .context(Map.of("device", "android", "appVersion", "2.1.5"))
                .build();

        assertEquals("android", metadata.getContext().get("device"));
        assertEquals("2.1.5", metadata.getContext().get("appVersion"));
    }

    @Test
    @DisplayName("KST 시간대가 정확히 설정되어야 한다")
    void verifyKSTTimezone() {
        LogMetadata metadata = LogMetadata.nowKST(true, 50L, "mock-agent");
        assertEquals("Asia/Seoul", metadata.getTimestamp().getZone().getId());
    }
}
