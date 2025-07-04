package dto.userprofile.request;

import eurm.Genre;
import eurm.Instrument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserProfileUpdateRequest {

    private Long userId;
    private List<Instrument> instruments;
    private List<Genre> genres;
    private String nickname;
    private String introduction;
    private boolean snsAgreement;
    private String gender;
    private String location;
    private String phoneNumber;
}