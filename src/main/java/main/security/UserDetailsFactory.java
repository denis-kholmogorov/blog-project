package main.security;

import lombok.NoArgsConstructor;
import main.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

@NoArgsConstructor
public final class UserDetailsFactory
{
   public static UserDetailsImpl create(User user){
       return new UserDetailsImpl(
               user.getId(),
               user.getName(),
               user.getEmail(),
               user.getPassword(),
               user.getIsModerator(),
               user.getCode(),
               user.getRegTime()

       );


   }
}
