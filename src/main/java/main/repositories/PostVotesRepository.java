package main.repositories;

import main.model.PostVotes;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVotesRepository extends CrudRepository<PostVotes, Integer>
{
    Optional<PostVotes> findByPostIdAndUserId(Integer postId, Integer userId);
}
