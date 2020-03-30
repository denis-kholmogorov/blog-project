package main.CustomException;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@Data
public class BadRequestException extends RuntimeException
{
    private HashMap<String, String> errors = new HashMap<>(1);

    public BadRequestException(String message){
        errors.put("message",message);
    }





}
