package bff.client.image;


import dto.image.request.ImageConfirmRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import resposne.BaseResponse;

@Component
public class ImageClient {
    private final String BASE_URI = "/api/image/v1";
    private final WebClient webClient;
    public ImageClient(@Qualifier("imageWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ResponseEntity<BaseResponse<?>>> confirmImages(ImageConfirmRequest req) {
        return post(req, BASE_URI + "/confirms");
    }




    public Mono<ResponseEntity<BaseResponse<?>>> deleteImage(Long imageId) {
        return post(BASE_URI +
                "/delete?" +
                "imageId="+imageId);
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


    public Mono<ResponseEntity<BaseResponse<?>>> getBandRoom(String url)
    {
        return webClient.get()
                .uri("url")
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

}
