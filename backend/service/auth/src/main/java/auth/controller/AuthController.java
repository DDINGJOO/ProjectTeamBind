package auth.controller;


import auth.dto.request.LoginRequest;
import auth.dto.request.SignUpRequest;
import auth.service.AuthService;
import exception.excrptions.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resposne.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/v1/")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<?>> signup(SignUpRequest req)
    {
        try {
            authService.registerUser(req);
        }catch (AuthException e)
        {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
        return ResponseEntity.ok(BaseResponse.success());
    }


    @GetMapping("/login")
    public ResponseEntity<BaseResponse<?>> login(LoginRequest req)
    {
        try{
            authService.login(req);
        } catch (AuthException e)
        {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
        return ResponseEntity.ok(BaseResponse.success());

    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<BaseResponse<?>> withdraw(
            @RequestParam Long userId
    )
    {
        try{
            authService.deleteUser(userId);
        }
        catch (AuthException e){
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
            }
        return  ResponseEntity.ok(BaseResponse.success());
    }
}
