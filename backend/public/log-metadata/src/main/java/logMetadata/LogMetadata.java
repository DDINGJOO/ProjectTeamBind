package logMetadata;


import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * 로그 메타데이터 구조 정의 (KST 기준)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogMetadata {

    /**
     * 요청 발생 시각 (KST 기준)
     */
    private ZonedDateTime timestamp;

    /**
     * 클라이언트 IP 또는 User-Agent 파싱 값
     */
    private String clientInfo;

    /**
     * 요청 처리 시간 (ms 단위)
     */
    private Long durationMillis;

    /**
     * 처리 성공 여부
     */
    private boolean success;

    /**
     * 실패 사유 코드 (있다면)
     */
    private String failureReason;

    /**
     * 임의의 추가 정보 (ex. device type, session id 등)
     */
    private Map<String, String> context;

    private String urlPath; //  추가
    private String httpMethod; // (선택) GET/POST 등

    public static LogMetadata nowKST(boolean success, Long duration, String clientInfo) {
        return LogMetadata.builder()
                .timestamp(ZonedDateTime.now(ZoneId.of("Asia/Seoul")))
                .success(success)
                .durationMillis(duration)
                .clientInfo(clientInfo)
                .build();
    }
}