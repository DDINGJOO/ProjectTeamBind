package eurm;

public enum UpdatableProfileColumn {

    NICKNAME("닉네임"),
    INTRODUCTION("소개글"),
    PROFILE_IMAGE("프로필 이미지"),
    LOCATION("지역"),
    GENDER("성별"),
    PHONE_NUMBER("전화번호"),
    EMAIL("이메일");

    private final String displayName;

    UpdatableProfileColumn(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}