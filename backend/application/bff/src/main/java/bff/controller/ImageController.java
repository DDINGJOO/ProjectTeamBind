package bff.controller;

import bff.client.image.ImageClient;
import dto.image.request.ImageConfirmRequest;
import exception.error_code.bff.BffErrorCode;
import exception.excrptions.BffException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import resposne.BaseResponse;

@RestController
@RequestMapping(path = "/api/image/v1",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Image", description = "이미지 확정·삭제 API")
public class ImageController {
    private final ImageClient imageClient;

    @Operation(
            summary = "이미지 확정 요청",
            description = "업로더 본인만 이미지 확정 가능",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(path = "/confirm",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BaseResponse<?>>> confirmImage(
            Authentication authentication,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "확정할 이미지 ID 목록과 업로더 ID",
                    required = true
            ) ImageConfirmRequest req
    ) {
        String userIdFromToken = authentication.getName();
        if (!userIdFromToken.equals(req.getUploaderId().toString())) {
            throw new BffException(BffErrorCode.NOT_MATCHED_TOKEN);
        }
        log.info("이미지 확정 요청<Token.UserId> ID={}, <Request.UserId>={}",
                userIdFromToken, req.getUploaderId());
        return imageClient.confirmImages(req);
    }

    @Operation(
            summary = "이미지 삭제 요청",
            description = "업로더 본인만 이미지 삭제 가능",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping(path = "/{imageId}")
    public Mono<ResponseEntity<BaseResponse<?>>> deleteImage(
            Authentication authentication,
            @Parameter(description = "삭제 요청자 ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "삭제할 이미지 ID", required = true)
            @PathVariable Long imageId
    ) {
        String userIdFromToken = authentication.getName();
        if (!userIdFromToken.equals(userId.toString())) {
            throw new BffException(BffErrorCode.NOT_MATCHED_TOKEN);
        }
        log.info("이미지 삭제 요청<Token.UserId> ID={}, <Request.UserId>={}",
                userIdFromToken, userId);
        return imageClient.deleteImage(imageId);
    }
}