package main.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.management.relation.Role;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ProviderToken {

    @Autowired
    private UserDetailsService userDetailsService;

    private final Map<String, String> tokens = new HashMap<>();

    public String createToken(String email, HttpServletRequest req){
        String idSession = req.getSession().getId();
        tokens.put(idSession, email);
        return idSession;
    }

    public String resolveToken(HttpServletRequest req){
        String bearerToken = req.getHeader("Authorization");
        log.info("token name " + bearerToken);
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getAuthentification(String token){
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmail(String token){
        String email = tokens.get(token);
        return email;
    }

    public boolean validateToken(String token){
        try{
            if(tokens.containsKey(token)){
                return true;
            }
            return false;
        }catch (UserAuthenticationException e){
            throw new UserAuthenticationException("Token is invalid");
        }
    }


}
