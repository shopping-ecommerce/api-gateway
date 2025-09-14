package iuh.fit.fe.configuration;

import iuh.fit.fe.repository.AuthenticationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

@Configuration
public class WebClientConfiguration {
    @Bean
    WebClient webClient(
            @Value("${services.url.auth-service}") String authBaseUrl
    ) {
        return WebClient.builder()
                .baseUrl(authBaseUrl) // lấy từ application.yml / biến môi trường
                .build();
    }
    @Bean
    AuthenticationClient authenticationClient(WebClient webClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient)).build();
        return factory.createClient(AuthenticationClient.class);
    }
}
