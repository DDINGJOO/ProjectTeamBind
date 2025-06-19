package auth.controller;


import auth.dto.request.LoginRequest;
import auth.dto.request.SignUpRequest;
import auth.service.AuthService;
import exception.excrptions.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/v1/")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(SignUpRequest req)
    {
        try {
            authService.registerUser(req);
        }catch (AuthException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return  ResponseEntity.ok().build();
    }


    @GetMapping("/login")
    public ResponseEntity<?> login(LoginRequest req)
    {
        try{
            authService.login(req);
        } catch (AuthException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return   ResponseEntity.ok().build();
    }

}
