package bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebFluxSecurity
public class WebClientConfig {
    @Bean
    public WebClient authWebClient(WebClient.Builder builder,
                                   @Value("${service.auth.base-url}") String url) {
        return builder
                .baseUrl(url)
                .build();
    }

    @Bean
    public WebClient imageWebClient(WebClient.Builder builder,
                                   @Value("${service.image.base-url}") String url) {
        return builder
                .baseUrl(url)
                .build();
    }

    @Bean
    public WebClient userProfileWebClient(WebClient.Builder builder,
                                    @Value("${service.user-profile.base-url}") String url) {
        return builder
                .baseUrl(url)
                .build();
    }


    @Bean
    public WebClient productWebClient(WebClient.Builder builder,
                                          @Value("${service.band-room.product}") String url) {
        return builder
                .baseUrl(url)
                .build();
    }



}