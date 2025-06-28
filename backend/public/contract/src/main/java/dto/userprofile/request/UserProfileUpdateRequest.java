package dto.userprofile.request;

import eurm.Genre;
import eurm.Instrument;
import lombok.Getter;

import java.util.List;

@Getter
public class UserProfileUpdateRequest {

    private Long userId;
    private List<Instrument> instruments;
    private List<Genre> genres;
    private String nickname;
    private String introduction;
    private String profileImageUrl;
    private String gender;
    private String location;
    private Long phoneNumber;
}