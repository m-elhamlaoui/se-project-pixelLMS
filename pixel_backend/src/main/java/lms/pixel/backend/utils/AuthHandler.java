package lms.pixel.backend.utils;

import lms.pixel.backend.exceptions.InvalidCredentialsException;
import lms.pixel.backend.exceptions.InvalidTokenException;
import lms.pixel.backend.model.LoginDTO;
import lms.pixel.backend.model.User;
import lms.pixel.backend.repository.UserRepository;
import lms.pixel.backend.security.hasherFactory.HashFactory;
import lms.pixel.backend.security.hasherFactory.IHasher;
import lms.pixel.backend.security.TokenRegistry;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class AuthHandler {

    private final UserRepository userRepository;
    private final TokenRegistry tokenStore;

    public AuthHandler(UserRepository userRepository, TokenRegistry tokenStore) {
        this.userRepository = userRepository;
        this.tokenStore = tokenStore;
    }

    public String ValidateCreds(LoginDTO user) throws InvalidCredentialsException {

        User userFromDb = userRepository.getUserByEmail(user.getEmail());
        if (userFromDb == null) {
            throw new InvalidCredentialsException();
        }
        String password = userRepository.getPassword(userFromDb.getUserid());

        IHasher hasher = HashFactory.getHasher("SHA256");
        Boolean valid = hasher.verify(user.getPassword(), password);

        if (!valid) {
            throw new InvalidCredentialsException();
        }

        String token = UUID.randomUUID().toString();
        tokenStore.storeToken(userFromDb.getUserid(), token);
        return token;

    }

    public int getUseridByToken(String token){
        try {
            return tokenStore.getUseridByToken(token);
        } catch (InvalidTokenException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    public boolean isAuthorized(String token){
        if (token == null || getUseridByToken(token) == -1) {
            return false;
        }
        return true;
    }

    public void invalidateToken(String token) throws InvalidTokenException {
        tokenStore.removeToken(token);
    }
}
