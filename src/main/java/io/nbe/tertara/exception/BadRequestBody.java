package io.nbe.tertara.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestBody extends RuntimeException {
    public BadRequestBody(String message) {
        super(message);
    }

    public BadRequestBody(String message, Throwable cause) {
        super(message, cause);
    }
}
