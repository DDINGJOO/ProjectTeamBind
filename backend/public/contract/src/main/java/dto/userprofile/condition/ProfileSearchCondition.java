package dto.userprofile.condition;

import eurm.Genre;
import eurm.Instrument;
import eurm.Location;
import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSearchCondition {
    private String nickname;
    private Location location;
    private List<Instrument> interest;
    private List<Genre> genre;
    private String gender;
}
