package bff.client.bandroom.studio;

import dto.studio.request.StudioCreateRequest;
import dto.studio.request.StudioUpdateRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import resposne.BaseResponse;


@Component
public class StudioClient {


    private final String BASE_URI = "/api/studio/v1";
    private final WebClient webClient;
    public StudioClient(@Qualifier("studioWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ResponseEntity<BaseResponse<?>>> getStudio(Long studioId) {
        return webClient.get()
                .uri(ub -> {
                    ub = ub.path(BASE_URI)
                            .queryParam("studioId", studioId);
                    return ub.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<?>>() {})
                .map(body -> {
                    if (!body.isSuccess()) {
                        return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(body);
                    }
                    return ResponseEntity.ok(body);
                });
    }

    public Mono<ResponseEntity<BaseResponse<?>>> getStudios(Long bandRoomId)
    {
        return webClient.get()
                .uri(ub -> {
                    ub = ub.path(BASE_URI+"/studios")
                            .queryParam("bandRoomId", bandRoomId);
                    return ub.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<?>>() {})
                .map(body -> {
                    if (!body.isSuccess()) {
                        return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(body);
                    }
                    return ResponseEntity.ok(body);
                });
    }

    public Mono<ResponseEntity<BaseResponse<?>>> updateStudio(StudioUpdateRequest req)
    {
        return post(req,BASE_URI+"/update");
    }

    public Mono<ResponseEntity<BaseResponse<?>>> createStudio(StudioCreateRequest req)
    {
        return post(req,BASE_URI+"/create");
    }

    public Mono<ResponseEntity<BaseResponse<?>>> deleteStudio(Long studioId)
    {
        return webClient.delete()
                .uri(ub -> {
                    ub = ub.path(BASE_URI)
                            .queryParam("studioId", studioId);
                    return ub.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<?>>() {})
                .map(body -> {
                    if (!body.isSuccess()) {
                        return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(body);
                    }
                    return ResponseEntity.ok(body);
                });
    }

    public Mono<ResponseEntity<BaseResponse<?>>> deleteStudios(Long bandRoomId)
    {
        return webClient.delete()
                .uri(ub -> {
                    ub = ub.path(BASE_URI+"/delete")
                            .queryParam("bandRoomId", bandRoomId);
                    return ub.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<?>>() {})
                .map(body -> {
                    if (!body.isSuccess()) {
                        return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(body);
                    }
                    return ResponseEntity.ok(body);
                });
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


}
