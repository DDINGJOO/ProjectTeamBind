package bff.controller;


import bff.client.auth.ImageClient;
import dto.image.request.ImageConfirmRequest;
import exception.error_code.auth.AuthErrorCode;
import exception.excrptions.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import resposne.BaseResponse;

@RestController
@RequestMapping("/api/image/v1")
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    private final ImageClient  imageClient;

    public Mono<ResponseEntity<BaseResponse<?>>> confirmImage(
            Authentication authentication,
            @RequestBody  ImageConfirmRequest req)
    {
        String userIdFromToken = authentication.getName();
        if (!userIdFromToken.equals(req.getUploaderId().toString())) {
            throw new AuthException(AuthErrorCode.USER_NOT_FOUND);
        }
        log.info("이미지 확정 요청<Token.UserId> ID={}, <Request.UserId>={}", userIdFromToken, req.getUploaderId());
        return imageClient.confirmImage(req);
    }


    public Mono<ResponseEntity<BaseResponse<?>>> deleteImage(
            Authentication authentication,
            @RequestParam Long userId,
            @RequestParam  Long imageId) {
        String userIdFromToken = authentication.getName();

        log.info("이미지 삭제 요청<Token.UserId> ID={}, <Request.UserId>={}", userIdFromToken, userId);
        return imageClient.deleteImage(imageId);
    }
}
