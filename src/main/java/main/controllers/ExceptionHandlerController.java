package main.controllers;

import main.CustomException.BadRequestException;
import main.security.UserAuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequestException(BadRequestException e) {
        return ResponseEntity.status(400).body(e.getErrors());
    }

    @ExceptionHandler(UserAuthenticationException.class)
    public ResponseEntity<?> unauthorized(){
        return ResponseEntity.status(401).body(null);
    }

}

