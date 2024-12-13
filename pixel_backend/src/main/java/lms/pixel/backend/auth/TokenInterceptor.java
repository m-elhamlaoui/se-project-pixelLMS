package lms.pixel.backend.auth;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lms.pixel.backend.utils.PermissionChecker;


@Component
public class TokenInterceptor implements HandlerInterceptor {

    private final PermissionChecker permChecker;

    public TokenInterceptor(PermissionChecker permChecker) {
        this.permChecker = permChecker;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        
        if (!permChecker.isAuthorized(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        
        return true;
    }
}
