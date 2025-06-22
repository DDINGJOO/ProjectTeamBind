package userProfile.dto.condition;

import userProfile.config.Genre;
import userProfile.config.Location;

import javax.sound.midi.Instrument;

public class ProfileSearchCondition {
    private String nickname;
    private Location location;
    private Instrument interest;
    private Genre genre;
    private String gender;
}
