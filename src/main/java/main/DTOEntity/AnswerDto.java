package main.DTOEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.DTOEntity.PostDtoInterface.AnswerDtoInterface;


@AllArgsConstructor
@NoArgsConstructor
public class AnswerDto implements AnswerDtoInterface {

    @Setter
    @Getter
    boolean result;



}
