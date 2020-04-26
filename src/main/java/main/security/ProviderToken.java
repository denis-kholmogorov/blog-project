package main.security;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class ProviderToken {


    private final Map<String, Integer> tokens = new HashMap<>();

    public String createToken(String sessionId, Integer userId){
        tokens.put(sessionId, userId);
        log.info("Создан токен для пользователя с id {}",userId);
        return sessionId;
    }

    public Integer getAuthUserIdBySession(String sessionId){
        if (tokens.containsKey(sessionId)) {
            return tokens.get(sessionId);
        }
        log.warn("Токен не валиден");
        throw new UserAuthenticationException("Token is invalid");
    }

    public Integer getUserIdBySession(String sessionId){
        if (tokens.containsKey(sessionId)) {
            return tokens.get(sessionId);
        }
       return null;
    }

   public boolean validateToken(String sessionId){
        if(tokens.containsKey(sessionId)){
            return true;
        }
        return false;
    }

    public boolean deleteToken(String sessionId){
        Integer userId = tokens.remove(sessionId);
        log.info("Пользователь с id {} покинул сайт", userId);
        return true;
    }


}
