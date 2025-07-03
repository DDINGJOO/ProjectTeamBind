package image.controller;


import eurm.ResourceCategory;
import exception.excrptions.ImageException;
import image.service.lifecycle.ImageLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import resposne.BaseResponse;

@RestController
@RequestMapping("/api/image/v1")
@RequiredArgsConstructor
public class ImageController {
    private final ImageLifecycleService imageService;



    @PostMapping("/deleteAll")
    public ResponseEntity<BaseResponse<?>> deleteImage(
            @RequestParam ResourceCategory category,
            @RequestParam  Long referenceId
    ) {
        try {
            imageService.markAllAsPendingDelete(category, referenceId);
            return  ResponseEntity.ok(BaseResponse.success());
        }
        catch (ImageException e)
        {
            return  ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<BaseResponse<?>> deleteImage(
            @RequestParam  Long imageId
    ) {
        try {
            imageService.deleteImage(imageId);
            return  ResponseEntity.ok(BaseResponse.success());
        }
        catch (ImageException e)
        {
            return  ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
    }
}
