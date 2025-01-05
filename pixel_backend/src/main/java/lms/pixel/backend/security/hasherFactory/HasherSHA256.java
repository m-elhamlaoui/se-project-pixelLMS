package lms.pixel.backend.security.hasherFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HasherSHA256 implements IHasher {
    @Override
    public String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verify(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }
        
        return hash(password).equals(hashedPassword);
    }
    
}
