package userProfile.dto.response;


import lombok.Builder;
import userProfile.config.Genre;
import userProfile.config.Instrument;
import userProfile.config.Location;
import userProfile.entity.UserGenre;
import userProfile.entity.UserInterest;
import userProfile.entity.UserProfile;

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


    public static UserProfileResponse from(UserProfile profile) {
        return UserProfileResponse.builder()
                .userId(profile.getUserId())
                .nickName(profile.getNickname())
                .profileImageUrl(profile.getProfileImageUrl())

                .location(profile.getLocation())
                .gender(profile.getGender())
                .instruments(profile.getUserInterests().stream()
                        .map(UserInterest::getInterest)
                        .toList())
                .genres(profile.getUserGenres().stream()
                        .map(UserGenre::getGenre)
                        .toList())
                .build();
    }
}
