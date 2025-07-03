package dto.userprofile.response;


import eurm.City;
import eurm.Genre;
import eurm.Instrument;
import lombok.Builder;

import java.util.List;


@Builder

public record UserProfileResponse(
        Long userId,
        String nickName,
        String profileImageUrl,
        String gender,
        List<Instrument> instruments,
        List<Genre> genres,
        City city
) {


}
