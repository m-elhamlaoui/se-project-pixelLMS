package lms.pixel.backend.Exceptions;

public class InvalidTokenException extends Exception {
    public InvalidTokenException(String token) {
        super("Invalid token: " + token);
    }
}
