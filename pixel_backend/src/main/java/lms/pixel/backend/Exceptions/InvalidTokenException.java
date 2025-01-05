package lms.pixel.backend.exceptions;

public class InvalidTokenException extends Exception {
    public InvalidTokenException(String token) {
        super("Invalid token: " + token);
    }
}
