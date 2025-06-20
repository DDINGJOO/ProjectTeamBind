package auth.controller;

import auth.dto.request.LogoutRequest;
import auth.service.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import resposne.BaseResponse;

@RestController
@RequestMapping("/api/auth/v1")
@RequiredArgsConstructor
public class LogoutController {

    private final LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<?>> logout(
            @RequestBody LogoutRequest req){


        logoutService.logout(req.accessToken(), req.userId(),req.deviceId());

        return ResponseEntity.ok(BaseResponse.success());
    }

    @PostMapping("/logout-all")
    public ResponseEntity<BaseResponse<?>> logoutAll(
            @RequestBody LogoutRequest req) {

        logoutService.logoutAll(req.accessToken(), req.userId());

        return ResponseEntity.ok(BaseResponse.success());
    }
}
