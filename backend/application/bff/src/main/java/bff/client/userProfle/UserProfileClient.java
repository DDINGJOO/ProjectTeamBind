package bff.client.userProfle;


import bff.client.ClientMethodFactory;
import bff.client.impl.ClientMethodFactoryImpl;
import dto.PageResult;
import dto.userprofile.condition.ProfileSearchCondition;
import dto.userprofile.request.UserProfileUpdateRequest;
import dto.userprofile.response.UserProfileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import resposne.BaseResponse;

import java.util.Optional;


@Slf4j
@Component
public class UserProfileClient {
    private static final String BASE_URI = "/api/user-profile/v1";
    private final WebClient webClient;
    private final ClientMethodFactory clientMethod;

    public UserProfileClient(@Qualifier("userProfileWebClient") WebClient webClient) {

        this.webClient = webClient;
        this.clientMethod = new ClientMethodFactoryImpl(webClient);
    }

    public Mono<ResponseEntity<BaseResponse<PageResult<UserProfileResponse>>>> searchProfiles(
            ProfileSearchCondition condition,
            Pageable pageable
    ) {
        return webClient.get()
                .uri(ub -> {
                    ub = ub.path(BASE_URI + "/list")
                            .queryParamIfPresent("nickname", Optional.ofNullable(condition.getNickname()))
                            .queryParamIfPresent("location", Optional.ofNullable(condition.getCity()))
                            .queryParam("page", pageable.getPageNumber())
                            .queryParam("size", pageable.getPageSize());
                    if (condition.getInterest() != null) {
                        org.springframework.web.util.UriBuilder finalUb = ub;
                        condition.getInterest().forEach(i ->
                                finalUb.queryParam("interests", i.name())
                        );
                    }
                    if (condition.getGenre() != null) {
                        org.springframework.web.util.UriBuilder finalUb1 = ub;
                        condition.getGenre().forEach(g ->
                                finalUb1.queryParam("genres", g.name())
                        );
                    }
                    org.springframework.web.util.UriBuilder finalUb2 = ub;
                    pageable.getSort().forEach(o ->
                            finalUb2.queryParam("sort", o.getProperty() + "," + o.getDirection())
                    );
                    return ub.build();
                })
                .retrieve()
                // PageResult<T> 로 매핑
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<PageResult<UserProfileResponse>>>() {
                })
                .map(body -> {
                    if (!body.isSuccess()) {
                        return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(body);
                    }
                    return ResponseEntity.ok(body);
                });
    }

    public Mono<ResponseEntity<BaseResponse<?>>> getProfile(Long userId) {
        String uri = BASE_URI + "/profile?userId=" + userId;
        return clientMethod.get(uri);
    }


    public Mono<ResponseEntity<BaseResponse<?>>> checkNickName(String nickname) {
        String uri = BASE_URI + "/checkNickName?nickname=" + nickname;
        return clientMethod.get(uri);
    }


    public Mono<ResponseEntity<BaseResponse<?>>> updateProfile(UserProfileUpdateRequest request) {
        return clientMethod.post(request, BASE_URI);
    }


    public Mono<ResponseEntity<BaseResponse<?>>> softDeleteUser(
            Long userId
    ) {
        return clientMethod.post(BASE_URI + "/deleteUser?userId=" + userId);
    }
}