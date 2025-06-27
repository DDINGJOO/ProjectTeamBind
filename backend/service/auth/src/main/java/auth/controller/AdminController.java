package auth.controller;


import auth.service.UserRoleGrantService;
import eurm.UserRoleType;
import exception.excrptions.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import resposne.BaseResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/api/auth/v1")
public class AdminController {

    private final UserRoleGrantService userRoleGrantService;



    @PostMapping("/grantRole")
    public ResponseEntity<BaseResponse<?>> grantRole(
            @RequestParam Long grantedId,
            @RequestParam Long granterId,
            @RequestParam UserRoleType roleId
    )
    {
        try {
            userRoleGrantService.grantRole(grantedId, granterId, roleId);
        }
        catch (AuthException e)
        {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
        return ResponseEntity.ok(BaseResponse.success());
    }
}
