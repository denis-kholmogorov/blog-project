package main.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ProviderToken {

    @Autowired
    private UserDetailsService userDetailsService;

    public Map<String, Integer> getTokens() {
        return tokens;
    }

    private final Map<String, Integer> tokens = new HashMap<>();

    public String createToken(String sessionId, Integer userId){

        tokens.put(sessionId, userId);
        return sessionId;
    }

    public String resolveToken(HttpServletRequest req){

        String sessionId = req.getSession().getId();
        log.info("session id " + sessionId);
        if(sessionId != null){
            return sessionId;
        }
        return null;
    }

    public Authentication getAuthentification(String token){
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUserBySession(token).toString());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Integer getUserBySession(String sessionId){
        if (tokens.containsKey(sessionId)) {
            return tokens.get(sessionId);
        }
        return null;
    }

    public boolean validateToken(String sessionId){
        try{
            if(tokens.containsValue(sessionId)){
                return true;
            }
            return false;
        }catch (UserAuthenticationException e){
            throw new UserAuthenticationException("Token is invalid");
        }
    }

    public boolean deleteToken(String sessionId){
        Integer userId = tokens.remove(sessionId);
        log.info("user {} was logout ", userId);
        return true;
    }


}
