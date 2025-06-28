package userProfile.service;


import dto.userprofile.condition.ProfileSearchCondition;
import dto.userprofile.request.UserProfileUpdateRequest;
import dto.userprofile.response.UserProfileResponse;
import eurm.Location;
import eurm.UpdatableProfileColumn;
import exception.error_code.userProfile.NickNameFilterErrorCode;
import exception.error_code.userProfile.UserProfileErrorCode;
import exception.excrptions.UserProfileException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import userProfile.entity.UserGerne;
import userProfile.entity.UserInstrument;
import userProfile.entity.UserProfile;
import userProfile.entity.UserProfileHistory;
import userProfile.repository.*;
import wordFilter.WordFilterService;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileRepositoryCustom userProfileQueryRepository;
    private final UserGenreRepository userGenreRepository;
    private final UserInterestRepository userInterestRepository;
    private final UserProfileHistoryRepository historyRepository;
    private final WordFilterService wordFilterService;



    @Transactional
    public void softDelete(Long userId) {
        UserProfile profile = findActiveProfile(userId);
        if (profile.isDeleted()) throw new UserProfileException(UserProfileErrorCode.USER_PROFILE_ALREADY_DELETED);
        profile.setDeletedAt(LocalDateTime.now());
    }

    @Transactional
    public void hardDelete(Long userId) {
        var userProfile  = userProfileRepository.findById(userId).orElseThrow(
                () -> new UserProfileException(UserProfileErrorCode.USER_PROFILE_NOT_FOUND)
        );
        userInterestRepository.deleteByUserProfile(userProfile);
        userGenreRepository.deleteByUserProfile(userProfile);
        userProfileRepository.deleteById(userId);
    }

    @Transactional
    public void updateProfile(Long userId, UserProfileUpdateRequest request) {
        UserProfile profile = findActiveProfile(userId);

        if (request.getNickname() != null && !request.getNickname().equals(profile.getNickname())) {
            validateNickname(request.getNickname());
            saveHistory(userId, UpdatableProfileColumn.NICKNAME, profile.getNickname(), request.getNickname());
            profile.setNickname(request.getNickname());
        }

        if (request.getIntroduction() != null && !request.getIntroduction().equals(profile.getIntroduction())) {
            saveHistory(userId, UpdatableProfileColumn.INTRODUCTION, profile.getIntroduction(), request.getIntroduction());
            profile.setIntroduction(request.getIntroduction());
        }


        if (request.getGender() != null && !request.getGender().equals(profile.getGender())) {
            saveHistory(userId, UpdatableProfileColumn.GENDER, profile.getGender(), request.getGender());
            profile.setGender(request.getGender());
        }

        if (request.getLocation() != null) {
            saveHistory(userId, UpdatableProfileColumn.LOCATION,
                    profile.getLocation() != null ? profile.getLocation().name() : null,
                    request.getLocation());
            profile.setLocation(Location.valueOf(request.getLocation()));
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(profile.getPhoneNumber())) {
            saveHistory(userId, UpdatableProfileColumn.PHONE_NUMBER,
                    profile.getPhoneNumber() != null ? profile.getPhoneNumber().toString() : null,
                    request.getPhoneNumber().toString());
            profile.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getInstruments() != null && !request.getInstruments().isEmpty()) {
            saveHistory(userId, UpdatableProfileColumn.INSTRUMENT,
                    profile.getUserInstruments().toString(),
                    request.getInstruments().toString());

            request.getInstruments().forEach(instrument -> {
                userInterestRepository.save(
                        UserInstrument.builder()
                                .userProfile(profile)
                                .instrument(instrument)
                                .build()
                );
            });
        }

        if (request.getGenres() != null && !request.getGenres().isEmpty()) {
            saveHistory(userId, UpdatableProfileColumn.INSTRUMENT,
                    profile.getUserInstruments().toString(),
                    request.getInstruments().toString());

            request.getGenres().forEach(genre -> {
                userGenreRepository.save(
                        UserGerne.builder()
                                .userProfile(profile)
                                .genre(genre)
                                .build()
                );
            });
        }
        profile.setUpdatedAt(LocalDateTime.now());
    }


    public UserProfileResponse getProfile(Long userId) {
        UserProfile profile = findActiveProfile(userId);
        return from(profile);
    }


    public Page<UserProfileResponse> searchProfiles(ProfileSearchCondition condition, Pageable pageable) {
        Page<UserProfile> profiles = userProfileQueryRepository.search(condition, pageable);

        List<UserProfileResponse> responseList = profiles.getContent().stream()
                .map(this::from)
                .toList();

        return new PageImpl<>(responseList, pageable, profiles.getTotalElements());
    }

    public List<UserProfileHistory> getChangeHistories(Long userId) {
        return historyRepository.findAllByUserIdOrderByChangedAtDesc(userId);
    }

    private UserProfile findActiveProfile(Long userId) {
        return userProfileRepository.findById(userId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new UserProfileException(UserProfileErrorCode.USER_PROFILE_NOT_FOUND));
    }

    private void validateNickname(String nickname) {
        if (nickname.length() < 2) throw new UserProfileException(NickNameFilterErrorCode.NICKNAME_TOO_SHORT);
        if (nickname.length() > 20) throw new UserProfileException(NickNameFilterErrorCode.NICKNAME_TOO_LONG);
        if (nickname.contains(" ")) throw new UserProfileException(NickNameFilterErrorCode.NICKNAME_CONTAINS_SPACES);
        wordFilterService.validate(nickname);
    }

    private void saveHistory(Long userId, UpdatableProfileColumn column, String oldValue, String newValue) {
        historyRepository.save(UserProfileHistory.of(
                userId,
                column,
                oldValue,
                newValue,
                "USER"
        ));
    }

    private UserProfileResponse from(UserProfile profile) {
        return UserProfileResponse.builder()
                .profileImageUrl(profile.getProfileImageUrl())
                .nickName(profile.getNickname())
                .userId(profile.getUserId())
                .gender(profile.getGender())
                .location(profile.getLocation())
                .instruments(profile.getUserInstruments().stream()
                        .map(UserInstrument::getInstrument)
                        .toList())
                .genres(profile.getUserGerne().stream()
                        .map(UserGerne::getGenre)
                        .toList())
                .build();
    }
}