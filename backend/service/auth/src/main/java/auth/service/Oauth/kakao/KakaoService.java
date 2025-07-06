package auth.service.Oauth.kakao;


import auth.dto.KakaoTokenResponseDto;
import auth.dto.KakaoUserInfoResponseDto;
import auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    private final AuthService authService;
    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;
    private String clientId;
    @Value("${kakao.redirect_uri_pc}")
    private String REDIRECT_URL;



    @Autowired
    public KakaoService(@Value("${kakao.client_id}") String clientId, AuthService authService) {
        this.clientId = clientId;
        this.authService = authService;

        KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
        KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    }

    public String getAccessTokenFromKakao(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", REDIRECT_URL);
        formData.add("code", code);

        KakaoTokenResponseDto tokenResponse = WebClient.create(KAUTH_TOKEN_URL_HOST)
                .post()
                .uri("/oauth/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(formData)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.error(" Kakao token request failed - 4xx error");
                    return Mono.error(new RuntimeException("Invalid Parameter"));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error(" Kakao token request failed - 5xx error");
                    return Mono.error(new RuntimeException("Internal Server Error"));
                })
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

        log.info(" AccessToken: {}", tokenResponse.getAccessToken());
        return tokenResponse.getAccessToken();
    }


    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("Access token is null or empty.");
        }

        KakaoUserInfoResponseDto userInfo = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.error(" Failed to retrieve user info - 4xx error. Possibly invalid accessToken?");
                    return Mono.error(new RuntimeException("Invalid Parameter"));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error(" Kakao Server Error");
                    return Mono.error(new RuntimeException("Internal Server Error"));
                })
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

        log.info(" Kakao User Info -> id: {}, nickname: {}",
                userInfo.getId(),
                userInfo.getKakaoAccount().getProfile().getNickName());

        return userInfo;
    }



}