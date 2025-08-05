package iuh.fit.fe.service;

import iuh.fit.fe.dto.ApiResponse;
import iuh.fit.fe.dto.request.IntrospectRequest;
import iuh.fit.fe.dto.response.IntrospectResponse;
import iuh.fit.fe.repository.AuthenticationClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    AuthenticationClient authenticationClient;
    public Mono<ApiResponse<IntrospectResponse>> introspect(IntrospectRequest request) {
        return authenticationClient.introspect(request.builder()
                        .token(request.getToken())
                .build());
    }

}
