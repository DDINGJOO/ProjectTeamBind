package bff.client.impl;

import bff.client.ClientMethodFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import resposne.BaseResponse;

public class ClientMethodFactoryImpl implements ClientMethodFactory {
    private final WebClient webClient;

    public ClientMethodFactoryImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<ResponseEntity<BaseResponse<?>>> post(Object req, String uri) {
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
    @Override
    public Mono<ResponseEntity<BaseResponse<?>>> post(String uri) {
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

    @Override
    public Mono<ResponseEntity<BaseResponse<?>>> get(String uri) {
        return webClient.get()
                .uri(uri)
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

    @Override
    public Mono<ResponseEntity<BaseResponse<?>>> delete(String uri) {
        return webClient.delete()
                .uri(uri)
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
}
