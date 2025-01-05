package lms.pixel.backend.utils;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lms.pixel.backend.exceptions.NotFoundException;
import lms.pixel.backend.model.myFile;
import lms.pixel.backend.repository.FileRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileStorageManager {
    private String fileStoragePath;

    public void setPath(String fileStoragePath) {
        this.fileStoragePath = fileStoragePath;
    }

    public String storeFile(MultipartFile file) {
        try {
            File storageDir = new File(fileStoragePath);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            String filePath = fileStoragePath + File.separator + file.getOriginalFilename();
            try (OutputStream os = new FileOutputStream(filePath)) {
                os.write(file.getBytes());
            }

            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResponseEntity<Resource> getDownloadableFile(int fileId, FileRepository fileRepository) throws NotFoundException {
        myFile file = fileRepository.getFileById(fileId);

        if (file == null) {
            throw new NotFoundException();
        }

        try {
            Path filePath = Paths.get(file.getPath()).normalize();
            Resource resource = new FileSystemResource(filePath);

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
