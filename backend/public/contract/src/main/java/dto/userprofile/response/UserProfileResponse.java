package dto.userprofile.response;


import eurm.Genre;
import eurm.Instrument;
import eurm.Location;
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
        Location location
) {


}
