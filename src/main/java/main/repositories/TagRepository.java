package main.repositories;

import main.DTOEntity.TagDto;
import main.model.ModerationStatus;
import main.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer>
{
   @Query("SELECT DISTINCT new main.DTOEntity.TagDto (t.name, count(t.name)/(select count(p) from Post p " +
            "where p.isActive = :active and p.moderationStatus = :ms) * 1.000 as weight) " +
            "FROM Tag t JOIN TagToPost tp ON tp.tag_id = t.id JOIN Post p ON p.id = tp.post_id " +
            "WHERE p.isActive = :active and p.moderationStatus = :ms and p.time < curtime() group by t.name order by weight DESC ")
    List<TagDto> findAllTagWithWeight(Byte active, ModerationStatus ms);


    Optional<Tag> findByName(String name);
}
