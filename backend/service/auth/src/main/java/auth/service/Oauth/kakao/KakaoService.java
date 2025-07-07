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
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;
    private String clientId;
    @Value("${kakao.redirect_uri_pc}")
    private String REDIRECT_URL;



    @Autowired
    public KakaoService(@Value("${kakao.client_id}") String clientId, AuthService authService) {
        this.clientId = clientId;

        KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
        KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    }

    public String getAccessTokenFromKakao(String code) {
        // 1) 폼 데이터 준비
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type",   "authorization_code");
        formData.add("client_id",    clientId);
        formData.add("redirect_uri", REDIRECT_URL);
        formData.add("code",         code);
        // ▶ client_secret 필요하면 여기에도 추가하세요:
        // formData.add("client_secret", kakaoClientSecret);

        // 2) 요청 & 에러 로깅
        KakaoTokenResponseDto tokenResponse = WebClient.create(KAUTH_TOKEN_URL_HOST)
                .post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                // 반드시 BodyInserters.fromFormData 로 보내야 x-www-form-urlencoded 로 인코딩됩니다
                .body(BodyInserters.fromFormData(formData))
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        // 에러일 때 바디를 String 으로 받아서 원본 메시지를 로그에 남기고
                        return response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Kakao 토큰 요청 실패: status={}, body={}",
                                            response.statusCode(), body);
                                    return Mono.error(new RuntimeException("Kakao 토큰 에러: " + body));
                                });
                    }
                    // 정상 응답일 때 DTO 로 변환
                    return response.bodyToMono(KakaoTokenResponseDto.class);
                })
                .block();  // 블로킹 호출

        log.info("AccessToken: {}", tokenResponse.getAccessToken());
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
                userInfo.getId());

        return userInfo;
    }



}