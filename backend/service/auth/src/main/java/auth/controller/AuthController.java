package auth.controller;


import auth.service.AuthService;
import auth.service.UserWithdrawService;
import dto.auth.request.LoginRequest;
import dto.auth.request.PasswordChangeRequest;
import dto.auth.request.SignUpRequest;
import dto.auth.response.LoginResponse;
import exception.excrptions.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resposne.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/v1")
public class AuthController {

    private final AuthService authService;
    private final UserWithdrawService userWithdrawService;


    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<?>> signup(
            @RequestBody SignUpRequest req)
    {
        try {
            authService.registerUser(req);
        }catch (AuthException e)
        {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }


        return ResponseEntity.ok(BaseResponse.success());
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest req) {
        try {
            LoginResponse loginResponse = authService.login(req);
            return ResponseEntity.ok(BaseResponse.success(loginResponse));
        } catch (AuthException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
    }
    @PostMapping("/changePassword")
    public ResponseEntity<BaseResponse<?>> changePassword(
            @RequestBody PasswordChangeRequest req
            )
    {
        try{
            authService.changePassword(req.userId(),req.newPassword(), req.newPasswordConfirm());
        }
        catch (AuthException e){
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }

        return ResponseEntity.ok(BaseResponse.success());

    }

    @PostMapping("/withdraw")
    public ResponseEntity<BaseResponse<?>> withdraw(
            @RequestParam Long userId,
            @RequestParam String reason
    )
    {
        try{
            userWithdrawService.withdraw(userId, reason);
        }
        catch (AuthException e){
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
            }
        return  ResponseEntity.ok(BaseResponse.success());
    }
}
