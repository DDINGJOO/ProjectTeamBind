package bff.client.auth;


import dto.userprofile.request.UserProfileUpdateRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import resposne.BaseResponse;

@Component
public class UserProfileClient {
    private final String BASE_URI = "/api/user-profile/v1";
    private final WebClient webClient;





    public UserProfileClient(@Qualifier("userProfileWebClient") WebClient webClient) {
        this.webClient = webClient;
    }


    public Mono<ResponseEntity<BaseResponse<?>>> updateProfile(UserProfileUpdateRequest request) {
        return post(request,BASE_URI);
    }



    public Mono<ResponseEntity<BaseResponse<?>>> softDeleteUser(
            Long userId
    ) {
        return post(BASE_URI+"/deleteUser?userId="+userId);
    }




    private Mono<ResponseEntity<BaseResponse<?>>> post(Object req, String uri) {
        return webClient.post()
                .uri(uri)
                .bodyValue(req)
                .exchangeToMono(response ->
                        response.bodyToMono(new ParameterizedTypeReference<BaseResponse<?>>() {})
                                .map(body -> {
                                    if (!body.isSuccess()) {
                                        return ResponseEntity
                                                .status(HttpStatus.BAD_REQUEST)
                                                .body(body);
                                    }
                                    return ResponseEntity.ok(body);
                                })
                );
    }

    private Mono<ResponseEntity<BaseResponse<?>>> post(String uri) {
        return webClient.post()
                .uri(uri)
                .exchangeToMono(response ->
                        response.bodyToMono(new ParameterizedTypeReference<BaseResponse<?>>() {})
                                .map(body -> {
                                    if (!body.isSuccess()) {
                                        return ResponseEntity
                                                .status(HttpStatus.BAD_REQUEST)
                                                .body(body);
                                    }
                                    return ResponseEntity.ok(body);
                                })
                );
    }


    /*


    @PostMapping()
    public ResponseEntity<BaseResponse<?>> upDateProfile(@RequestBody UserProfileUpdateRequest request) {
        try{
            userProfileService.updateProfile(request.getUserId(),request);
            return  ResponseEntity.ok(BaseResponse.success());
        }catch (UserProfileException e){
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

     */
}
