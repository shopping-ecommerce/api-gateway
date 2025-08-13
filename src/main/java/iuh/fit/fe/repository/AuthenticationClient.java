package iuh.fit.fe.repository;

import iuh.fit.fe.dto.ApiResponse;
import iuh.fit.fe.dto.request.IntrospectRequest;
import iuh.fit.fe.dto.response.IntrospectResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface AuthenticationClient {
    @PostExchange(url = "/authentication/introspect",contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request);
}
