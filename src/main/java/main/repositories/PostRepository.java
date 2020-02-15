package main.repositories;

import main.model.ModerationStatus;
import main.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface PostRepository extends PagingAndSortingRepository<Post,Integer>
{

    @Transactional(readOnly = true)
    @Query(value = "Select distinct p from Post p where p.isActive = :active " +
                    "and p.moderationStatus = :moderationStatus and p.time < curtime()")
    List<Post> findDistinctByActiveAndModerationStatus(Byte active, ModerationStatus moderationStatus, Pageable pageable);

    @Transactional(readOnly = true)
    @Query(value = "SELECT * FROM posts WHERE is_active = :active AND moderation_status = :moderationStatus " +
                   "AND time < curtime() AND :date = Date(time)", nativeQuery = true)
    List<Post> findAllPostsByDate(Byte active, String moderationStatus, String date, Pageable pageable);

    @Transactional(readOnly = true)
    @Query(value = " SELECT DISTINCT p.* FROM posts p " +
            "JOIN tag2post tp ON p.id = tp.post_id " +
            "JOIN tags t ON t.id = tp.tag_id " +
            "WHERE p.is_active = :active AND p.moderation_status = :moderationStatus" +
            " AND p.time < curtime() AND :tag = t.name"
            , nativeQuery = true)
    List<Post> findAllPostsByTag(Byte active, String moderationStatus, String tag, Pageable paging);

}