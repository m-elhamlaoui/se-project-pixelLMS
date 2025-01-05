package lms.pixel.backend.security.hasherFactory;

public interface IHasher {
    String hash(String password);
    boolean verify(String password, String hashedPassword);
}
