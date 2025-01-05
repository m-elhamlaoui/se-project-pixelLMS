package lms.pixel.backend.exceptions;

public class OperationNotAuthorizedException extends Exception {
    public OperationNotAuthorizedException(String message, int userid) {
        super(message + ", UserID: " + userid);
    }
}
