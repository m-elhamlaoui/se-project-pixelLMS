package lms.pixel.backend.auth;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lms.pixel.backend.Exceptions.InvalidTokenException;;

@Component
public class TokenStore {
    private final Map<String, Integer> tokenMap = new ConcurrentHashMap<>();

    public void storeToken(Integer userid, String token) {
        tokenMap.put(token, userid);
    }

    public Integer getUseridByToken(String token) throws InvalidTokenException {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (!tokenMap.containsKey(token)) {
            throw new InvalidTokenException(token);
        }

        return tokenMap.get(token);
    }

    public void removeToken(String token) throws InvalidTokenException {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!tokenMap.containsKey(token)) {
            throw new InvalidTokenException(token);
        }   

        tokenMap.remove(token);
    }
}
