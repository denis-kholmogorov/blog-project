package main.DTOEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.Tag;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDtoId extends PostDto{

    @JsonIgnore
    private String announce;

    @JsonIgnore
    private Integer commentCounts;

    private String text = super.getAnnounce();

    private List<PostCommentsDto> comments;

    private Set<String> tags;


}
