package main.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiGeneralController
{
    @GetMapping("/api/init")
    InitClass init(){
        InitClass init = new InitClass("Devpub", "Рассказы разработчиков",
                "+7 915 003-08-11", "dendiesel88@yandex.ru", "Холмогоров Денис", "2020" );
        return init;
    }
}
