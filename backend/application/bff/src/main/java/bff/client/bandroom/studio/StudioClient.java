package bff.client.bandroom.studio;

import bff.client.ClientMethodFactory;
import bff.client.impl.ClientMethodFactoryImpl;
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
    private final ClientMethodFactory clientMethod;
    public StudioClient(@Qualifier("studioWebClient") WebClient webClient) {
        this.clientMethod = new ClientMethodFactoryImpl(webClient);

    }



    public Mono<ResponseEntity<BaseResponse<?>>> getStudio(Long studioId) {
        String uri = BASE_URI+"/studio?studioId="+studioId;
        return clientMethod.get(uri);
    }

    public Mono<ResponseEntity<BaseResponse<?>>> getStudios(Long bandRoomId)
    {

        String uri = BASE_URI+"/studios?bandRoomId="+bandRoomId;
        return clientMethod.get(uri);

    }

    public Mono<ResponseEntity<BaseResponse<?>>> updateStudio(StudioUpdateRequest req)
    {
        return clientMethod.post(req,BASE_URI+"/update");
    }

    public Mono<ResponseEntity<BaseResponse<?>>> createStudio(StudioCreateRequest req)
    {
        return clientMethod.post(req,BASE_URI+"/create");
    }

    public Mono<ResponseEntity<BaseResponse<?>>> deleteStudio(Long studioId)
    {
        String uri = BASE_URI+"/delete?studioId="+studioId;
        return clientMethod.delete(uri);
    }

    public Mono<ResponseEntity<BaseResponse<?>>> deleteStudios(Long bandRoomId)
    {
        String uri = BASE_URI+"/delete?bandRoomId="+bandRoomId;
        return clientMethod.delete(uri);
    }



}
