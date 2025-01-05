package lms.pixel.backend.security.hasherFactory;

public class HashFactory {

    private static final String DEFAULT = "SHA256";
    
    private HashFactory() {
        // Prevent instantiation
    }

    public static IHasher getHasher(String algorithm) {
        if (algorithm.equals("SHA256")) {
            return new HasherSHA256();
        }
        else if (algorithm.equals("MD5")) {
            return new HasherMD5();
        } else {
            return null;
        }
    }

    public static IHasher getHasher() {
        return getHasher(DEFAULT);
    }
    
}
