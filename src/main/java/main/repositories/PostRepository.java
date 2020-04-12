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
import java.util.Optional;

@Transactional
@Repository
public interface PostRepository extends PagingAndSortingRepository<Post,Integer>
{

    @Query(value = "Select distinct p from Post p where p.isActive = :active " +
                    "AND p.moderationStatus = :ms AND p.time < curtime()")
    Page<Post> findDistinctByActiveAndModerationStatus(Byte active, ModerationStatus ms, Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE is_active = :active AND moderation_status = :ms " +
                   "AND time < curtime() AND :date = Date(time)", nativeQuery = true)
    Page<Post> findAllPostsByDate(Byte active, String ms, String date, Pageable pageable);

    Optional<Post> findByIdAndIsActiveAndModerationStatus(Integer id, Byte active, ModerationStatus ms);

    @Query(value = " SELECT DISTINCT p.* FROM posts p " +
            "JOIN tag2post tp ON p.id = tp.post_id " +
            "JOIN tags t ON t.id = tp.tag_id " +
            "WHERE p.is_active = :active AND p.moderation_status = :ms" +
            " AND p.time < curtime() AND :tag = t.name"
            , nativeQuery = true)
    Page<Post> findAllPostsByTag(Byte active, String ms, String tag, Pageable paging);

    @Query(value = "Select p from Post p where p.isActive = :active " +
            "AND p.moderationStatus = :ms AND p.time < curtime() AND p.text like %:query% OR p.title like %:query%")
    Page<Post> findPostBySearch(Byte active, ModerationStatus ms, String query, Pageable paging);

    @Query(value ="select date(p.time), count(p) from Post p where p.time < curtime() and" +
            " YEAR(p.time) = :year group by date(p.time)")
    List<String> findCountPostForCalendar(Integer year);

    @Query(value="select year(p.time) from Post p WHERE  p.time < curtime() group by year(p.time)")
    List<Integer> findAllYearWithPosts();

    @Query(value = "SELECT p FROM Post p")
    List<Post> findAllStatistics();

    @Query(value = "SELECT p FROM Post p where p.user.id = :id")
    List<Post> findAllStatisticsById(Integer id);

    @Query(value = "Select p from Post p where p.user.id = :userId and isActive = :query")
    Page<Post> findMyPosts(Integer userId, byte query, Pageable pageable);

    @Query(value = "Select p from Post p where p.user.id = :userId and isActive = :query and moderationStatus = :status")
    Page<Post> findMyPosts(Integer userId, byte query, ModerationStatus status, Pageable pageable);

    @Query(value = "SELECT * FROM posts where is_active = 1 and moderation_status = :query and moderator_id = :userId",
    nativeQuery = true)
    Page<Post> findMyModerationPosts(Integer userId, String query, Pageable pageable);

    @Query(value = "SELECT p FROM Post p where isActive = 1 and moderationStatus = 'NEW'")
    Page<Post> findModerationNewPosts(Pageable pageable);

}