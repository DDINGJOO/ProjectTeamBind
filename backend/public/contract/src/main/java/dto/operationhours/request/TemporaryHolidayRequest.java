package dto.operationhours.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporaryHolidayRequest {

    @NotNull
    private LocalDate holidayDate;

    private String reason;
}
