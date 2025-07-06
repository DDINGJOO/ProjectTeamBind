package bff.controller;

import bff.client.image.ImageClient;
import bff.client.userProfle.UserProfileClient;
import bff.dto.userprofile.request.UserProfileUpdateRequestFromClient;
import dto.PageResult;
import dto.userprofile.condition.ProfileSearchCondition;
import dto.userprofile.response.UserProfileResponse;
import eurm.City;
import eurm.Genre;
import eurm.Instrument;
import exception.error_code.bff.BffErrorCode;
import exception.excrptions.BffException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import resposne.BaseResponse;

import java.util.List;

@RestController
@RequestMapping(path = "/api/user-profile/v1",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "UserProfile", description = "유저 프로필 조회·수정 API")
public class UserProfileController {

    private final UserProfileClient userProfileClient;
    private final ImageClient imageClient;

    @Operation(
            summary = "유저 프로필 업데이트",
            description = "로그인된 본인만 자신의 프로필과 이미지를 동시에 업데이트합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/update",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BaseResponse<?>>> userProfileUpdate(
            Authentication authentication,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "UserProfileUpdateRequestFromClient DTO",
                    required = true
            ) UserProfileUpdateRequestFromClient req
    ) {
        String userId = authentication.getName();
        if (!userId.equals(req.getUserId().toString())) {
            throw new BffException(BffErrorCode.NOT_MATCHED_TOKEN);
        }

        Mono<ResponseEntity<BaseResponse<?>>> profileMono =
                userProfileClient.updateProfile(req.toUserProfileUpdateRequest(req));
        Mono<ResponseEntity<BaseResponse<?>>> imageMono =
                imageClient.confirmImages(req.toImageConfirmRequest(req));

        return Mono.zip(profileMono, imageMono)
                .flatMap(tuple -> {
                    BaseResponse<?> prof = tuple.getT1().getBody();
                    BaseResponse<?> img  = tuple.getT2().getBody();

                    if (prof != null && !prof.isSuccess()) {
                        return Mono.just(ResponseEntity.ok(prof));
                    }
                    if (img != null && !img.isSuccess()) {
                        return Mono.just(ResponseEntity.ok(img));
                    }
                    return Mono.just(ResponseEntity.ok(BaseResponse.success()));
                });
    }

    @GetMapping("/my")
    public Mono<ResponseEntity<BaseResponse<?>>> myProfile(
            Authentication authentication
    ){
        String userId = authentication.getName();
        return userProfileClient.getProfile(Long.valueOf(userId));
    }

    @GetMapping("/nickname-validate")
    public Mono<ResponseEntity<BaseResponse<?>>> nicknameValidate(
            @RequestParam String nickname

    ){
        return userProfileClient.checkNickName(nickname);
    }




    @GetMapping()
    public Mono<ResponseEntity<BaseResponse<?>>> userProfile(
            Authentication authentication,
            @RequestParam Long userId
    ){
        String userIdFromToken = authentication.getName();
        if (!userIdFromToken.equals(userId.toString())) {
            throw new BffException(BffErrorCode.NOT_MATCHED_TOKEN);
        }
        return userProfileClient.getProfile(userId);
    }

    @Operation(
            summary = "유저 프로필 리스트 조회",
            description = "닉네임, 지역, 악기, 장르 조건으로 페이징된 유저 프로필 목록을 반환합니다."
    )

    @GetMapping("/list")
    public Mono<ResponseEntity<BaseResponse<PageResult<UserProfileResponse>>>> userProfileList(
            @Parameter(description = "닉네임(부분 검색)", required = false)
            @RequestParam(required = false) String nickname,

            @Parameter(description = "도시 필터", required = false)
            @RequestParam(required = false) City location,

            @Parameter(description = "관심 악기 리스트", required = false)
            @RequestParam(required = false) List<Instrument> interests,

            @Parameter(description = "선호 장르 리스트", required = false)
            @RequestParam(required = false) List<Genre> genres,

            @Parameter(description = "페이지 번호 (0부터 시작)", required = false)
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", required = false)
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "정렬 기준 필드", required = false)
            @RequestParam(defaultValue = "updatedAt") String sortBy,

            @Parameter(description = "정렬 방향 (ASC or DESC)", required = false)
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        ProfileSearchCondition condition = ProfileSearchCondition.builder()
                .nickname(nickname)
                .city(location)
                .interest(interests)
                .genre(genres)
                .build();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return userProfileClient.searchProfiles(condition, pageable);
    }
}