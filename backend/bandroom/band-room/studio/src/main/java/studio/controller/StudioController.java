package studio.controller;


import dto.studio.request.StudioCreateRequest;
import dto.studio.request.StudioUpdateRequest;
import dto.studio.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resposne.BaseResponse;
import studio.service.StudioService;

import java.util.List;

@RestController
@RequestMapping("/api/studio/v1")
@RequiredArgsConstructor
public class StudioController {

    private final StudioService studioService;

    @GetMapping()
    public ResponseEntity<BaseResponse<?>> getStudio(
            @RequestParam Long studioId
    ){
        try{
            ProductResponse response = studioService.getStudio(studioId);
            return ResponseEntity.ok(BaseResponse.success(response));
        } catch (Exception e) {
            //TODO : exception Make
//            return ResponseEntity.badRequest().body(BaseResponse.fail());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/studios")
    public ResponseEntity<BaseResponse<?>> getStudios(
            @RequestParam Long BandRoomId
    ){
        try{
            List<ProductResponse> responses = studioService.getStudios(BandRoomId);
            return ResponseEntity.ok(BaseResponse.success(responses));
        }
        catch (Exception e){
            //TODO
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping()
    public ResponseEntity<BaseResponse<?>> updateStudio(
            @RequestBody StudioUpdateRequest req
    )
    {

        try {
            studioService.updateStudio(req);
            return  ResponseEntity.ok(BaseResponse.success());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping()
    public ResponseEntity<BaseResponse<?>> createStudio(
            @RequestBody StudioCreateRequest req
    )
    {
        studioService.createStudio(req);
        return ResponseEntity.ok(BaseResponse.success());
    }

    @DeleteMapping()
    public ResponseEntity<BaseResponse<?>> deleteStudio(
            @RequestParam Long studioId
    )
    {
        studioService.deleteStudio(studioId);
        return  ResponseEntity.ok(BaseResponse.success());
    }

    @DeleteMapping("/studios")
    public ResponseEntity<BaseResponse<?>> deleteStudios(
            @RequestParam Long BandRoomId
    )
    {
        studioService.deleteAll(BandRoomId);
        return   ResponseEntity.ok(BaseResponse.success());
    }


}
