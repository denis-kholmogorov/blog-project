package main.repositories;

import main.DTOEntity.PostDto;
import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends PagingAndSortingRepository<Post,Integer>
{
    @Query(value = "SELECT COUNT(p) FROM Post p")
        Integer findAllCountPosts();

    @Query(value = "SELECT * FROM posts " +
                   "WHERE is_active = 1 and moderation_status = 'ACCEPTED' " +
                   "ORDER BY time DESC",
            nativeQuery = true)
    Page<Post> findAllPostsSortRecent(Pageable paging);

   /* @Query(value = "SELECT p.* FROM posts p " +
                   "INNER JOIN post_comments pc ON p.id = pc.post_id " +
                   "WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' " +
                   "GROUP BY pc.post_id",
            nativeQuery = true)
    Page<Post> findAllPostsSortComments(Pageable paging);*/

    @Query(value = "SELECT p.* FROM posts p " +
                   "INNER JOIN post_votes pv ON p.id = pv.post_id " +
                   "WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' " +
                   "GROUP BY pv.post_id",
            nativeQuery = true)
    Page<Post> findAllPostsSortLikes(Pageable paging);

    @Query(value = "SELECT * FROM posts " +
                   "WHERE is_active = 1 and moderation_status = 'ACCEPTED' " +
                   "ORDER BY time",
            nativeQuery = true)
    Page<Post> findAllPostsSortEarly(Pageable paging);

    /*@Query(value = "SELECT new main.DTOEntity.PostDto(p.id, p.time, p.user, p.title, p.text, count(pv.id)," +
            " count(pc.id), p.viewCount)" +
            " FROM Post p " +
            "INNER JOIN PostComments pc ON p.id = pc.post " +
            "INNER JOIN PostVotes pv ON p.id = pv.post " +
            "WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED'" +
            "GROUP BY pc.post")
    Page<PostDto> findAllPostsSortComments(Pageable paging);*/

   /* @Query(value = "SELECT new main.DTOEntity.PostDto(p, count(pv.id)," +
            " count(pc.id))" +
            " FROM Post p " +
            "INNER JOIN PostComments pc ON p.id = pc.post " +
            "INNER JOIN PostVotes pv ON p.id = pv.post " +
            "WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED'" +
            "GROUP BY pc.post ORDER BY count(pc.id) DESC ")
    Page<PostDto> findAllPostsSortComments(Pageable paging);*/

    @Query(value = "SELECT new main.DTOEntity.PostDto(p," +
            " count(pc.id))" +
            " FROM Post p " +
            "INNER JOIN PostComments pc ON p.id = pc.post " +
            "WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED'" +
            "GROUP BY pc.post ORDER BY count(pc.id) DESC ")
    Page<PostDto> findAllPostsSortComments(Pageable paging);

}
