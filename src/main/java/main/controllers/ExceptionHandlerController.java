package main.controllers;

import main.CustomException.BadRequestException;
import main.CustomException.CustomNotFoundException;
import main.security.UserAuthenticationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler{

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<?> badRequestException(BadRequestException e) {
        return ResponseEntity.status(400).body(e.getErrors());
    }

    @ExceptionHandler(UserAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void unauthorized(){}

    @ExceptionHandler(CustomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void notFoundException(){}

}

