package userProfile.service;


import exception.error_code.userProfile.NickNameFilterErrorCode;
import exception.error_code.userProfile.UserProfileErrorCode;
import exception.excrptions.UserProfileException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import userProfile.config.Location;
import userProfile.config.UpdatableProfileColumn;
import userProfile.dto.request.UserProfileUpdateRequest;
import userProfile.entity.UserProfile;
import userProfile.entity.UserProfileHistory;
import userProfile.repository.UserGenreRepository;
import userProfile.repository.UserInterestRepository;
import userProfile.repository.UserProfileHistoryRepository;
import userProfile.repository.UserProfileRepository;
import wordFilter.WordFilterService;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
//    private final UserProfileQueryRepository userProfileQueryRepository;
    private final UserGenreRepository userGenreRepository;
    private final UserInterestRepository userInterestRepository;
    private final UserProfileHistoryRepository historyRepository;
    private final WordFilterService wordFilterService;

//    @Transactional
//    public void createProfile(UserCreatedEvent event) {
//        if (userProfileRepository.existsById(event.getUserId())) return;
//
//        UserProfile profile = UserProfile.builder()
//                .userId(event.getUserId())
//                .nickname("user_" + event.getUserId().toString().substring(0, 6))
//                .email(event.getEmail())
//                .profileImageUrl(event.getProfileImageUrl())
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        userProfileRepository.save(profile);
//    }

    @Transactional
    public void softDelete(Long userId) {
        UserProfile profile = findActiveProfile(userId);
        if (profile.isDeleted()) throw new UserProfileException(UserProfileErrorCode.USER_PROFILE_ALREADY_DELETED);
        profile.setDeletedAt(LocalDateTime.now());
    }

    @Transactional
    public void hardDelete(Long userId) {
        userInterestRepository.deleteByUserId(userId);
        userGenreRepository.deleteByUserId(userId);
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

        if (request.getProfileImageUrl() != null && !request.getProfileImageUrl().equals(profile.getProfileImageUrl())) {
            saveHistory(userId, UpdatableProfileColumn.PROFILE_IMAGE, profile.getProfileImageUrl(), request.getProfileImageUrl());
            profile.setProfileImageUrl(request.getProfileImageUrl());
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

        profile.setUpdatedAt(LocalDateTime.now());
    }

//    public UserProfileDto getProfile(Long userId) {
//        UserProfile profile = findActiveProfile(userId);
//        return UserProfileDto.from(profile);
//    }
//
//    public Page<UserProfileDto> searchProfiles(ProfileSearchCondition condition, Pageable pageable) {
//        return userProfileQueryRepository.search(condition, pageable);
//    }
//
//    public List<UserProfileDto> findBatchProfiles(LocalDateTime updatedAfter) {
//        return userProfileQueryRepository.findBatchProfiles(updatedAfter);
//    }

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
}