package userProfile.repository;


import dto.userprofile.condition.ProfileSearchCondition;
import dto.userprofile.response.UserProfileResponse;
import eurm.City;
import eurm.Genre;
import eurm.Instrument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import userProfile.entity.UserProfile;

import java.time.LocalDateTime;
import java.util.List;



public interface UserProfileRepositoryCustom  {
    Page<UserProfile> searchProfilesDsl(
            String nickname,
            City city,
            List<Instrument> interests,
            List<Genre> genres,
            Pageable pageable
    );


    Page<UserProfile> search(ProfileSearchCondition condition, Pageable pageable);

    List<UserProfileResponse> findBatchProfiles(LocalDateTime updatedAfter);

}

//Evnet ->?
