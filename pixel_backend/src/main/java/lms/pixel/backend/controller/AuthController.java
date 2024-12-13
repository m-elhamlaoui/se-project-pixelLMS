package lms.pixel.backend.controller;

import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import lms.pixel.backend.auth.LoginDTO;
import lms.pixel.backend.auth.TokenStore;
import lms.pixel.backend.model.User;
import lms.pixel.backend.repository.IUserRepository;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("security/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final TokenStore tokenStore;
    private final IUserRepository userRepository;

    public AuthController(TokenStore tokenStore, IUserRepository userRepository) {
        this.tokenStore = tokenStore;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(HttpServletRequest request ,@RequestBody LoginDTO user) {
        try {
            User userFromDb = userRepository.getUserByEmail(user.getEmail());
            if (userFromDb == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            String password = userRepository.getPassword(userFromDb.getUserid());
            String hashed = userRepository.hashPassword(user.getPassword());
            if (userFromDb != null && password.equals(hashed)) {
                String token = UUID.randomUUID().toString();
                tokenStore.storeToken(userFromDb.getUserid(), token);
                logger.info("User logged in: {}", userFromDb.getName());
                return ResponseEntity.ok(token);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            logger.error("Login error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        try {
            tokenStore.removeToken(token);
            return ResponseEntity.ok("Logged out");
        } catch (Exception e) {
            logger.error("Logout error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    } 
}
