package io.nbe.tertara.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflitException extends RuntimeException {

    public ConflitException(String message) {
        super(message);
    }

    public ConflitException(String message, Throwable cause) {
        super(message, cause);
    }
}
