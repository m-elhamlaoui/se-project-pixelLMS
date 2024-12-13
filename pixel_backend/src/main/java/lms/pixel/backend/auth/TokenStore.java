package lms.pixel.backend.auth;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStore {
    private final Map<String, Integer> tokenMap = new ConcurrentHashMap<>();

    public void storeToken(Integer userid, String token) {
        tokenMap.put(token, userid);
    }

    public Integer getUseridByToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return tokenMap.get(token);
    }

    public void removeToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        tokenMap.remove(token);
    }
}
