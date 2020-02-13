package main.controllers;

import main.DTOEntity.InitDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class ApiGeneralController
{
    @GetMapping("/api/init")

    ResponseEntity<InitDto> init() {

        List<String> list = null;
        try
        {
            list = Files.readAllLines(Paths.get("copyright.txt"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(new InitDto(list.get(0), list.get(1), list.get(2),
                                             list.get(3), list.get(4), list.get(5)));
    }
}
