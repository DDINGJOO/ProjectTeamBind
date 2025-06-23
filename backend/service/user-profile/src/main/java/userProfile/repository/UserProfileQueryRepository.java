package userProfile.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import userProfile.config.Genre;
import userProfile.config.Instrument;
import userProfile.config.Location;
import userProfile.dto.condition.ProfileSearchCondition;
import userProfile.dto.response.UserProfileResponse;
import userProfile.entity.UserProfile;

import java.time.LocalDateTime;
import java.util.List;



public interface UserProfileQueryRepository {
    Page<UserProfile> searchProfilesDsl(
            String nickname,
            Location location,
            List<Instrument> interests,
            List<Genre> genres,
            Pageable pageable
    );


    Page<UserProfile> search(ProfileSearchCondition condition, Pageable pageable);

    List<UserProfileResponse> findBatchProfiles(LocalDateTime updatedAfter);

}

//Evnet ->?
