package dto.operationhours.request;


import eurm.MonthlyWeekPattern;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

// OperationSchedule 생성/수정 요청용 DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationScheduleRequest {

    @NotNull
    private Long roomId;

    private Long studioId;

    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    private LocalTime openTime;

    @NotNull
    private LocalTime closeTime;

    @NotNull
    private MonthlyWeekPattern monthlyWeekPattern;

    @NotNull
    private Boolean active;
}

