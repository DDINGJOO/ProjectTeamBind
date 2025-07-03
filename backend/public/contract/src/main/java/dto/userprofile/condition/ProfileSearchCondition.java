package dto.userprofile.condition;

import eurm.City;
import eurm.Genre;
import eurm.Instrument;
import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSearchCondition {
    private String nickname;
    private City city;
    private List<Instrument> interest;
    private List<Genre> genre;
    private String gender;
}
