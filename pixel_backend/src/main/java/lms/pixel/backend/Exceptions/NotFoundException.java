package lms.pixel.backend.exceptions;

public class NotFoundException extends Exception {
    public NotFoundException() {
        super("Entity you looking for was not found");
    }
}
