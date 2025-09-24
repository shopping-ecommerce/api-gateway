package iuh.fit.fe.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.fe.dto.ApiResponse;
import iuh.fit.fe.dto.request.IntrospectRequest;
import iuh.fit.fe.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE,makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {
    AuthenticationService authenticationService;
    ObjectMapper objectMapper;

    @NonFinal
    private String[] publicEndpoints = new String[] {
            "/authentication/login-email-password",
            "/authentication/introspect",
            "/profiles/create",
            "/authentication/logout",
            "/authentication/refresh",
            "/file/media/download/.*",
            "/authentication/register",
            "/authentication/verifyOTP",
            "/authentication/verifyFromEmail",
            "/chat/.*",
            "/chat",
            "/product/getProducts",
            "/info/sellers/searchByUserId/.*",
            "/info/sellers/searchBySellerId/.*",
            "/product/searchBySeller/.*",
            "/product/categories",
            "/product/searchByProduct/.*",
            "/product/searchBySizeAndID",
            "/product/searchBySeller/.*",
            "/product/searchByCategory/.*",
            "/product/search",
            "/product/suggest"
    };

    @NonFinal
    @Value("${app.api-prefix}")
    private String apiPrefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Enter AuthenticationFilter");
        if(isPublicEndpoint(exchange.getRequest()))
            return chain.filter(exchange);
        List<String> headers = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(headers)) {
            return unauthenticated(exchange.getResponse());
        }
        String token = headers.getFirst().replace("Bearer ", "");
        log.info("Token: " + token);
        return authenticationService.introspect(IntrospectRequest.builder()
                .token(token)
                .build()).flatMap(apiResponse -> {
                    if (apiResponse.getResult().isValid()) {
                        log.info("Token is valid, proceeding with request");
                        return chain.filter(exchange);
                    } else {
                        log.info("Token is invalid, returning unauthenticated response");
                        return unauthenticated(exchange.getResponse());
                    }
        }).onErrorResume(e -> unauthenticated(exchange.getResponse()));
    }
    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublicEndpoint(ServerHttpRequest path) {
        return Arrays.stream(publicEndpoints).anyMatch(s -> path.getURI().getPath().matches(apiPrefix+s));
    }

    Mono<Void> unauthenticated(ServerHttpResponse response) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(1401)
                .message("Unauthenticated")
                .build();
        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        DataBufferFactory bufferFactory = response.bufferFactory();
        DataBuffer buffer = bufferFactory.wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}