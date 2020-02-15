package main.repositories;

import main.model.ModerationStatus;
import main.model.Post;
import org.springframework.data.domain.Page;
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
    @Query(value = "Select distinct p from Post p where p.isActive = :active and p.moderationStatus = :moderationStatus and p.time < curtime()")
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

    /*@Transactional(readOnly = true)
    @Query(value = "SELECT * FROM posts " +
                   "WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < CURTIME() " +
                   "ORDER BY time DESC",
            nativeQuery = true)
    Page<Post> findAllPostsSortRecent(Pageable paging);

    @Transactional(readOnly = true)
    @Query(value ="SELECT p.* FROM posts p " +
                   "LEFT JOIN post_comments pc ON p.id = pc.post_id " +
                   "WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' AND p.time < CURTIME() " +
                   "GROUP BY p.id ORDER BY COUNT(pc.id) DESC"
            ,nativeQuery = true)
    Page<Post> findAllPostsSortComments(Pageable paging);

    @Transactional(readOnly = true)
    @Query(value = "SELECT p.* FROM posts p " +
                   "LEFT JOIN post_votes pv ON p.id = pv.post_id " +
                   "WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' AND p.time < CURTIME()" +
                   "GROUP BY p.id ORDER BY COUNT(pv.id) DESC",
            nativeQuery = true)
    Page<Post> findAllPostsSortLikes(Pageable paging);

    @Transactional(readOnly = true)
    @Query(value = "SELECT * FROM posts " +
                   "WHERE is_active = 1 and moderation_status = 'ACCEPTED' AND time < CURTIME()" +
                   "ORDER BY time",
            nativeQuery = true)
    Page<Post> findAllPostsSortEarly(Pageable paging);*/
