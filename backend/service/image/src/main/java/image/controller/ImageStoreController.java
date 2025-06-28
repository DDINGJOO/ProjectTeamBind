package image.controller;


import eurm.ImageVisibility;
import eurm.ResourceCategory;
import exception.excrptions.ImageException;
import image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import resposne.BaseResponse;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/image/v1")
public class ImageStoreController {
    private final ImageService imageService;


    @PostMapping("/image")
    public ResponseEntity<BaseResponse<?>> imageStore(
            MultipartFile file,
            ResourceCategory category,
            Long uploaderId,
            ImageVisibility visibility,
            Boolean isThumbnail)
    {

        try {
            var response = imageService.upload(file, category, uploaderId, visibility, isThumbnail);
            return ResponseEntity.ok(BaseResponse.success(response));
        }
        catch (ImageException e)
        {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
    }

    @PostMapping("/images")
    public ResponseEntity<BaseResponse<?>> imagesStore(
            List<MultipartFile> file,
            ResourceCategory category,
            Long uploaderId,
            ImageVisibility visibility)
    {
        try{
            var response = imageService.uploadImages(file, category, uploaderId, visibility);
            return ResponseEntity.ok(BaseResponse.success(response));
        }catch (ImageException e)
        {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
    }




}
