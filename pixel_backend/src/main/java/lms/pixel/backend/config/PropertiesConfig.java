package lms.pixel.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertiesConfig {
    @Value("${file.storage.path}")
    private String fileStoragePath;

    public String getFileStoragePath() {
        return fileStoragePath;
    }
}
