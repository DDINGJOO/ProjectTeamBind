package dto.operationhours.response;

import eurm.MonthlyWeekPattern;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationScheduleResponse {

    private Long id;
    private Long roomId;
    private Long studioId;
    private DayOfWeek dayOfWeek;
    private LocalTime openTime;
    private LocalTime closeTime;
    private MonthlyWeekPattern monthlyWeekPattern;
    private boolean active;
    private long price;

    private List<TemporaryHolidayResponse> temporaryHolidays;
}
