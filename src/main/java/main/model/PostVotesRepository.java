package main.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVotesRepository extends CrudRepository<PostVotes, Integer>
{
}
