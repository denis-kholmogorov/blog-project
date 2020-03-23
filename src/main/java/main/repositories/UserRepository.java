package main.repositories;

import main.DTOEntity.UserLoginDto;
import main.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>
{
    @Query("SELECT u FROM User u where u.email = :email")
    Optional<User> findByEmail(String email);

    Optional<User> findById(Integer id);

    @Query("SELECT count(p) FROM Post p where moderatorId = :id")
    Integer findCountModerationPostsById(Integer id);

    Optional<User> findByCode(String code);
}
