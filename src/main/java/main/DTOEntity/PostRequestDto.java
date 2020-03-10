package main.DTOEntity;

import lombok.Data;

@Data
public class PostRequestDto
{

    private String time;

    private byte active;

    private String title;

    private String text;

    private String[] tags;

}
