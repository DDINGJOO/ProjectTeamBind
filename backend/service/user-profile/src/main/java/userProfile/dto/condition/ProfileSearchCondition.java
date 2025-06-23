package userProfile.dto.condition;

import lombok.*;
import userProfile.config.Genre;
import userProfile.config.Instrument;
import userProfile.config.Location;

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
