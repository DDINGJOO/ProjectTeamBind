package bff.client;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import resposne.BaseResponse;

public interface ClientMethodFactory {


    Mono<ResponseEntity<BaseResponse<?>>> post(Object req, String uri);
    Mono<ResponseEntity<BaseResponse<?>>> post(String uri);
    Mono<ResponseEntity<BaseResponse<?>>> get(String uri);
    Mono<ResponseEntity<BaseResponse<?>>> delete(String uri);

}
