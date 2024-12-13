package lms.pixel.backend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStorageConfig {
    @Value("${file.storage.path}")
    private String fileStoragePath;

    public String getFileStoragePath() {
        return fileStoragePath;
    }
}
