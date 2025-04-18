package com.snapserve.bookingservice.config;

import com.snapserve.bookingservice.exception.ExternalServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        return switch (status) {
            case NOT_FOUND ->
                    new ExternalServiceException("Resource not found in user-service. (Triggered at: " + methodKey + ")");
            case BAD_REQUEST ->
                    new ExternalServiceException("Bad request to user-service. Check request payload. (Triggered at: " + methodKey + ")");
            case INTERNAL_SERVER_ERROR ->
                    new ExternalServiceException("User-service internal server error. (Triggered at: " + methodKey + ")");
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }
}
