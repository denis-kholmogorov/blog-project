package main.DTOEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModerationDecisionDto
{
    Integer post_id;

    String decision;
}

