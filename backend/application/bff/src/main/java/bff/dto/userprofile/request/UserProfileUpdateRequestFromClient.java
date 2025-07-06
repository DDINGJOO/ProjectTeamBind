package bff.dto.userprofile.request;

import dto.image.request.ImageConfirmRequest;
import dto.userprofile.request.UserProfileUpdateRequest;
import eurm.Genre;
import eurm.Instrument;
import eurm.ResourceCategory;
import lombok.*;

import java.util.List;



@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpdateRequestFromClient {
    private Long userId;
    private List<Instrument> instruments;
    private List<Genre> genres;
    private String nickname;
    private String introduction;
    private Boolean smsAgreement;
    private String gender;
    private String location; // TODO : 프로필 설정전에 합의
    private String phoneNumber;
    private List<Long> imageIds;

    public UserProfileUpdateRequest toUserProfileUpdateRequest(UserProfileUpdateRequestFromClient fromUserProfileUpdateRequest) {
        return UserProfileUpdateRequest.builder()
                .gender(fromUserProfileUpdateRequest.getGender())
                .userId(fromUserProfileUpdateRequest.getUserId())
                .instruments(fromUserProfileUpdateRequest.getInstruments())
                .nickname(fromUserProfileUpdateRequest.getNickname())
                .location(fromUserProfileUpdateRequest.getLocation())
                .snsAgreement(fromUserProfileUpdateRequest.getSmsAgreement())
                .phoneNumber(fromUserProfileUpdateRequest.getPhoneNumber())
                .introduction(fromUserProfileUpdateRequest.getIntroduction())
                .build();
    }

    public ImageConfirmRequest toImageConfirmRequest(UserProfileUpdateRequestFromClient fromUserProfileUpdateRequest) {

        return ImageConfirmRequest.builder()
                .uploaderId(fromUserProfileUpdateRequest.getUserId())
                .referenceId(fromUserProfileUpdateRequest.getUserId())
                .imageIds(imageIds)
                .category(ResourceCategory.PROFILE)
                .build();
    }
}
