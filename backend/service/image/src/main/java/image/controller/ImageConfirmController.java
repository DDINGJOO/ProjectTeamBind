package image.controller;


import dto.image.request.ImageConfirmRequest;
import exception.excrptions.ImageException;
import image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resposne.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image/v1")
public class ImageConfirmController {
    private final ImageService imageService;

    @PostMapping("/confirms")
    public ResponseEntity<BaseResponse<?>> confirmImage(
            @RequestBody ImageConfirmRequest req
    ) {
        try {

            imageService.confirmImages(req);
            return ResponseEntity.ok(BaseResponse.success());
        }
        catch (ImageException e)
        {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<BaseResponse<?>> confirmImage(
            @RequestParam Long imageId
    ){
        try{
            imageService.confirmImage(imageId);
            return ResponseEntity.ok(BaseResponse.success());
        }catch (ImageException e)
            {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
            }
    }
}
