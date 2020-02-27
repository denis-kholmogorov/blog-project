package main.DTOEntity;

import lombok.Data;

@Data


public class UserLoginDto
{
    Integer id;
    String name;
    String photo;
    String email;
    boolean moderation;
    Integer moderationCount;
    boolean settings;

    public UserLoginDto(Integer id, String name, String photo, String email, byte moderation, Integer moderationCount) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.email = email;

        this.moderationCount = moderationCount;
        if(moderation == (byte)1 )
        {
            this.moderation = true;
            this.settings = true;
        }else {
            this.moderation = false;
            this.settings = false;
        }
    }



}
