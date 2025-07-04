package bff.dto.userprofile.response;


import eurm.City;
import eurm.Genre;
import eurm.Instrument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserProfileResponse {
    Long userId;
    String nickName;
    String profileImageUrl;
    String gender;
    List<Instrument> instruments;
    List<Genre> genres;
    City city;
}
