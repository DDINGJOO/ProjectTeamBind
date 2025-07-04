package dto.operationhours.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporaryHolidayResponse {

    private Long id;
    private LocalDate holidayDate;
    private String reason;
}
