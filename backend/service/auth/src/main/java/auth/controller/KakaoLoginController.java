package auth.controller;


import auth.dto.KakaoUserInfoResponseDto;
import auth.entity.User;
import auth.service.Oauth.SocialService;
import auth.service.Oauth.kakao.KakaoService;
import auth.service.eventPublish.EventPublish;
import dto.auth.response.LoginResponse;
import eurm.ProviderList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import resposne.BaseResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao")
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final SocialService OauthService;
    private EventPublish eventPublish;





    @GetMapping("/callback")
    public ResponseEntity<BaseResponse<?>> callback(
            @RequestParam("code") String code) {


        String kakaoAccessToken = kakaoService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(kakaoAccessToken);

        User user = OauthService.createUser(ProviderList.Kakao, userInfo.kakaoAccount.email);
        eventPublish.createUserEvent(user.getId(), user.getEmail());
        LoginResponse loginResponse = OauthService.login( ProviderList.Kakao,user);
        return ResponseEntity.ok(BaseResponse.success(loginResponse));

    }


}