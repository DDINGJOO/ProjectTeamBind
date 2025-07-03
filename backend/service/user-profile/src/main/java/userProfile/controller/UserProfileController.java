package userProfile.controller;


import dto.userprofile.condition.ProfileSearchCondition;
import dto.userprofile.request.UserProfileUpdateRequest;
import exception.excrptions.UserProfileException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resposne.BaseResponse;
import userProfile.service.UserProfileService;



@RestController
@RequestMapping("/api/user-profile/v1")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;


    @PostMapping()
    public ResponseEntity<BaseResponse<?>> upDateProfile(@RequestBody UserProfileUpdateRequest request) {
        try{
            userProfileService.updateProfile(request.getUserId(),request);
            return  ResponseEntity.ok(BaseResponse.success());
        }catch (UserProfileException e){
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponse<?>> searchProfiles(
            @ModelAttribute ProfileSearchCondition condition,
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        try {
            var profiles = userProfileService.searchProfiles(condition, pageable);
            return ResponseEntity.ok(BaseResponse.success(profiles));
        } catch (UserProfileException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
    }



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
