package main.DTOEntity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestProfileDto
{
    Integer removePhoto;

    String name;

    String email;

    String password;
}
