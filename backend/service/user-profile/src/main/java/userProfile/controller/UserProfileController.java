package userProfile.controller;


import exception.excrptions.UserProfileException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resposne.BaseResponse;
import userProfile.service.UserProfileService;

@RestController
@RequestMapping("/api/user-profile/v1")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;



    @PostMapping("/deleteUser")
    public void softDeleteUser(
            @RequestParam Long userId ){
        userProfileService.softDelete(userId);
    }

    @GetMapping("")
    public ResponseEntity<BaseResponse<?>> getProfile(
            @RequestParam Long userId
    ){

        try {
            return ResponseEntity.ok(BaseResponse.success(userProfileService.getProfile(userId)));
        }
        catch (UserProfileException e){
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }


    }



}
