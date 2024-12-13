package lms.pixel.backend.Exceptions;

public class NotFoundException extends Exception {
    public NotFoundException() {
        super("Entity you looking for was not found");
    }
}
