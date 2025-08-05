package iuh.fit.fe.controller;

import iuh.fit.fe.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping(value = "/auth", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse authFallback() {
        return ApiResponse.builder()
                .code(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("Authentication service is temporarily unavailable. Please try again later.")
                .build();
    }

    @RequestMapping(value = "/profiles", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse userFallback() {
        return ApiResponse.builder()
                .code(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("User service is currently unavailable. Please try again later.")
                .build();
    }

    @RequestMapping(value = "/files", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse fileFallback() {
        return ApiResponse.builder()
                .code(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("File service is currently unavailable. Please try again later.")
                .build();
    }

    @RequestMapping("/notification")
    public ApiResponse notificationFallback() {
        return ApiResponse.builder()
                .code(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("Notification Service is temporarily unavailable. Please try again later.")
                .build();
    }
}