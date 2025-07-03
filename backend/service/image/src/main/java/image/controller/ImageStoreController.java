package image.controller;


import eurm.ImageVisibility;
import eurm.ResourceCategory;
import exception.excrptions.ImageException;
import image.service.upload.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import resposne.BaseResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/image/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public class ImageStoreController {
    private final ImageUploadService imageUploadService;

    @PostMapping("/image")
    public ResponseEntity<BaseResponse<?>> imageStore(
            @RequestPart("file") MultipartFile file,
            @RequestParam("category") ResourceCategory category,
            @RequestParam("uploaderId") Long uploaderId,
            @RequestParam("visibility") ImageVisibility visibility,
            @RequestParam(value = "isThumbnail", defaultValue = "false") Boolean isThumbnail
    ) {
        try {
            var response = imageUploadService.upload(file, category, uploaderId, visibility, isThumbnail);
            return ResponseEntity.ok(BaseResponse.success(response));
        } catch (ImageException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
    }

    @PostMapping("/images")
    public ResponseEntity<BaseResponse<?>> imagesStore(
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam("category") ResourceCategory category,
            @RequestParam("uploaderId") Long uploaderId,
            @RequestParam("visibility") ImageVisibility visibility
    ) {
        try {
            var response = imageUploadService.uploadImages(files, category, uploaderId, visibility);
            return ResponseEntity.ok(BaseResponse.success(response));
        } catch (ImageException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(e.getErrorCode()));
        }
    }
}