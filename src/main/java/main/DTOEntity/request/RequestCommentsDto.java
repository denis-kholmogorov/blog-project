package main.DTOEntity.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.xml.bind.v2.model.core.ID;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class RequestCommentsDto
{
    @JsonProperty(value = "parent_id")
    Integer parentId;

    @JsonProperty(value = "post_id")
    Integer postId;

    @Size(min = 10, message = "Текст комментария не задан или слишком короткий")
    String text;
}
