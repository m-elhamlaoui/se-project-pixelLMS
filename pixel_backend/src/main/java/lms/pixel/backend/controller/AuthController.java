package lms.pixel.backend.controller;

import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import lms.pixel.backend.Exceptions.InvalidCredentialsException;
import lms.pixel.backend.Exceptions.InvalidTokenException;

import lms.pixel.backend.auth.LoginDTO;
import lms.pixel.backend.auth.TokenStore;

import lms.pixel.backend.model.User;

import lms.pixel.backend.repository.IUserRepository;

import lms.pixel.backend.utils.PermissionChecker;

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
    private final PermissionChecker permissionChecker;

    public AuthController(TokenStore tokenStore, PermissionChecker permissionChecker) {
        this.tokenStore = tokenStore;
        this.permissionChecker = permissionChecker;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(HttpServletRequest request ,@RequestBody LoginDTO user) {
        try {
            String token = permissionChecker.ValidateCreds(user);
            logger.info("User logged in: {}", user.getEmail());
            return ResponseEntity.ok(token);
        } catch (InvalidCredentialsException e) {
            logger.error("Invalid credentials: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        try {
            tokenStore.removeToken(token);
            return ResponseEntity.ok("Logged out");
        } catch (InvalidTokenException e) {
            logger.error("Invalid token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        } catch (Exception e) {
            logger.error("Logout error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    } 
}
