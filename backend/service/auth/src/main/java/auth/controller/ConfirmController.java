package auth.controller;


import auth.service.AuthService;
import exception.excrptions.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import resposne.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/v1")
public class ConfirmController {

    private final AuthService authService;

    @PostMapping("/email")
    public ResponseEntity<BaseResponse<?>> confirmEmail(
            @RequestParam Long userId,
            @RequestParam String code
    ) {
        try{
            authService.confirmedEmail(userId,code);
            return ResponseEntity.ok(BaseResponse.success());
        } catch (AuthException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
    }
}
