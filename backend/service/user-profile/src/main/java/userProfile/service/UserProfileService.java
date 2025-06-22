package userProfile.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import userProfile.entity.UserProfile;
import userProfile.repository.UserGenreRepository;
import userProfile.repository.UserInterestRepository;
import userProfile.repository.UserProfileRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
//    private final UserProfileQueryRepository userProfileQueryRepository;
    private final UserGenreRepository userGenreRepository;
    private final UserInterestRepository userInterestRepository;

    /**
     * Kafka 이벤트 기반 프로필 생성
     */
//    public void createProfileFromEvent(UserCreatedEvent event) {
//        if (userProfileRepository.existsById(event.getUserId())) return;
//
//        UserProfile profile = UserProfile.builder()
//                .userId(event.getUserId())
//                .nickname("user_" + event.getUserId().toString().substring(0, 6))
//                .email(event.getEmail())
//                .profileImageUrl(event.getProfileImageUrl())
//                .build();
//
//        userProfileRepository.save(profile);
//    }

    /**
     * 소프트 삭제 처리 (deletedAt만 기록)
     */
    @Transactional
    public void softDelete(Long userId) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        profile.setDeletedAt(LocalDateTime.now());
    }

    /**
     * 하드 삭제 처리 (연관 관심사/장르 포함 전체 제거)
     */
    @Transactional
    public void hardDelete(Long userId) {
        userInterestRepository.deleteByUserId(userId);
        userGenreRepository.deleteByUserId(userId);
        userProfileRepository.deleteById(userId);
    }

    /**
     * 검색 조건 기반 프로필 조회 (페이징)
     */
//    @Transactional
//    public Page<UserProfileDto> searchProfiles(ProfileSearchCondition condition, Pageable pageable) {
//        return userProfileQueryRepository.search(condition, pageable);
//    }
//
//    /**
//     * 배치용 전체 유저 프로필 추출
//     */
//    public List<UserProfileDto> findBatchProfiles(LocalDateTime updatedAfter) {
//        return userProfileQueryRepository.findBatchProfiles(updatedAfter);
//    }
}