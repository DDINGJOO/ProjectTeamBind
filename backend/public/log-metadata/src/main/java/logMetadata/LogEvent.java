package logMetadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LogEvent {
    private String userId;                // 유저 식별자
    private LogActionType actionType;    // 로그인, 페이지 조회 등
    private LogMetadata metadata;        // 부가정보 (아래 설명 참고)
    private Object payload;              // 상세 행위에 대한 유연한 정보 (예: 조회한 게시글 ID)
}