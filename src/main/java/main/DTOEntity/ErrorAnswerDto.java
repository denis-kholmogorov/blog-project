package main.DTOEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.DTOEntity.PostDtoInterface.AnswerDtoInterface;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class ErrorAnswerDto implements AnswerDtoInterface
{
    @Setter
    @Getter
    boolean result;

    @Setter
    @Getter
    Map<String, String> errors = new HashMap<>();

}
