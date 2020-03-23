package main.DTOEntity;

import lombok.Data;
import main.model.Tag;

import java.util.List;
import java.util.Set;

@Data
public class RequestPostDto
{

    private String time;

    private byte active;

    private String title;

    private String text;

    private Set<String> tags;

}
