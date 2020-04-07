package main.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {



    public RedirectView handlerNotFound(Exception e){
        RedirectView rv = new RedirectView("/");
        return rv;
    }

    @ExceptionHandler(RuntimeException.class)
    public RedirectView handleErrors(HttpServletRequest request, Exception e){
        log.info(e.getClass().getName() + " Ошибка");
        RedirectView rv = new RedirectView("/");
        return rv;
    }
}