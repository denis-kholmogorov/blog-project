package main.controllers;

import main.CustomException.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler
{

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> unauthorizedException() {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> notFoundException(BadRequestException e){
        return ResponseEntity.badRequest().body(e.getErrors());
    }


}
