package bff.controller;

import bff.client.auth.ImageClient;
import bff.client.auth.UserProfileClient;
import bff.dto.userprofile.request.UserProfileUpdateRequestFromClient;
import exception.error_code.bff.BffErrorCode;
import exception.excrptions.BffException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import resposne.BaseResponse;

@RestController
@RequestMapping("/api/user-profile/v1")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    private final UserProfileClient userProfileClient;
    private final ImageClient imageClient;

    @PutMapping("/update")
    public Mono<ResponseEntity<?>> userProfileUpdate(
            Authentication authentication,
            @RequestBody UserProfileUpdateRequestFromClient req) {

        // 1) JWT 에서 추출한 userId 검증
        String userId = authentication.getName();
        if (!userId.equals(req.getUserId().toString())) {
            throw new BffException(BffErrorCode.NOT_MATCHED_TOKEN);
        }

        // 2) 두 API 호출을 동시에 날리고, 둘 다 ResponseEntity<BaseResponse<...>> 을 받음
        Mono<ResponseEntity<BaseResponse<?>>> profileMono =
                userProfileClient.updateProfile(req.toUserProfileUpdateRequest(req));

        Mono<ResponseEntity<BaseResponse<?>>> imageMono =
                imageClient.confirmImage(req.getThumbnailId());

        // 3) zip → flatMap 으로 결과 꺼내서, 실패 케이스가 있으면 그 JSON(body) 그대로 리턴
        return Mono
                .zip(profileMono, imageMono)
                .flatMap(tuple -> {
                    BaseResponse<?> profBody = tuple.getT1().getBody();
                    BaseResponse<?> imgBody  = tuple.getT2().getBody();

                    // 프로필 호출이 실패(success==false) 했으면 그 body 그대로 리턴
                    if (profBody != null && !profBody.isSuccess()) {
                        return Mono.just(ResponseEntity.ok(profBody));
                    }
                    // 이미지 확인 호출이 실패했으면 그 body 그대로 리턴
                    if (imgBody != null && !imgBody.isSuccess()) {
                        return Mono.just(ResponseEntity.ok(imgBody));
                    }
                    // 둘 다 성공했으면 빈 결과
                    return Mono.just(ResponseEntity.ok(BaseResponse.success()));
                });
    }
}
