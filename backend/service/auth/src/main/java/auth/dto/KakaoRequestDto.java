package auth.dto;

import lombok.Data;

@Data
public class KakaoRequestDto {
    private String code;
    private String redirectUri;

    // Getter & Setter 또는 @Data
}