package dto.userprofile.request;

import lombok.Getter;

@Getter
public class UserProfileUpdateRequest {

    private String nickname;
    private String introduction;
    private String profileImageUrl;
    private String gender;
    private String location;
    private Long phoneNumber;
}