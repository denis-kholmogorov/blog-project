package main.DTOEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import main.model.Post;

import java.util.Calendar;
import java.util.List;

@Data
public class AllStatisticsBlogDto
{
    Integer posts = 0;

    Integer likes = 0;

    Integer dislikes = 0;

    Integer views = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    Calendar firstPublication = null;

    public AllStatisticsBlogDto(List<Post> posts)
    {
        posts.forEach(post -> {
            this.posts++;
            this.likes = this.likes + post.getLikesUsers().size();
            this.dislikes = this.getDislikes() + post.getDisLikesUsers().size();
            this.views = this.views + post.getViewCount();

            if(this.firstPublication == null)
            {
                this.firstPublication = post.getTime();
            }
            if(this.firstPublication.after(post.getTime()))
            {
                this.firstPublication = post.getTime();
            }
        });
    }


}
