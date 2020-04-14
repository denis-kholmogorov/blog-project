package main.DTOEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.Calendar;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentsDto
{
    private Integer id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private Calendar time;

    private UserDtoPhoto user;

    private String comment;

}
