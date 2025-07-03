package auth.controller;


import auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/confirm/v1")
public class ConfirmController {

    private final AuthService authService;

    @PostMapping("/email")
    public void confirmEmail(
            @RequestParam Long userId,
            @RequestParam String code
    ) {
        authService.confirmedEmail(userId,code);
    }
}
