package lms.pixel.backend.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileValidator {

    private static final List<String> ALLOWED_MIME_TYPES = List.of(
        "application/pdf",
        "application/zip",
        "application/x-zip-compressed",
        "application/x-zip",
        "application/x-7z-compressed",
        "application/x-rar-compressed",
        "application/x-tar",
        "application/x-gzip",
        "application/x-bzip2"
    );

    public String validate(MultipartFile file) {
        if (file.isEmpty()) {
            return "File is empty";
        }

        String contentType = file.getContentType();
        if (!ALLOWED_MIME_TYPES.contains(contentType)) {
            return "File type not allowed";
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            return "Invalid file name";
        }

        return null;
    }
}
