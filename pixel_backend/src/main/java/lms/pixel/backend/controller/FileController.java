package lms.pixel.backend.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lms.pixel.backend.Exceptions.NotFoundException;
import lms.pixel.backend.model.myFile;
import lms.pixel.backend.repository.FileRepository;
import lms.pixel.backend.utils.FileStorageConfig;
import lms.pixel.backend.utils.PermissionChecker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.io.File;
import java.nio.file.Files;

@RestController
@RequestMapping("api/file")
public class FileController {
    private final FileRepository fileRepository;
    private final PermissionChecker permissionChecker;
    private final FileStorageConfig fileStorageConfig;

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

    public FileController(FileRepository fileRepository, PermissionChecker permissionChecker, FileStorageConfig fileStorageConfig) {
        this.fileRepository = fileRepository;
        this.permissionChecker = permissionChecker;
        this.fileStorageConfig = fileStorageConfig;
    }

    @PostMapping("/")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("attachToId") int attachToId,
            @RequestParam("foreignKeyType") String foreignKeyType,
            @RequestHeader("Authorization") String token) {

        int userId = permissionChecker.getUseridByToken(token);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        
        String contentType = file.getContentType();
        if (!ALLOWED_MIME_TYPES.contains(contentType)) {
            return ResponseEntity.badRequest().body("File type not allowed");
        }

        try {
            File storageDir = new File(fileStorageConfig.getFileStoragePath());
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.contains("..")) {
                return ResponseEntity.badRequest().body("Invalid file name");
            }
            String filePath = fileStorageConfig.getFileStoragePath() + File.separator + originalFilename;
            try (OutputStream os = new FileOutputStream(filePath)) {
                os.write(file.getBytes());
            }
            fileRepository.attach(filePath, userId, attachToId, foreignKeyType);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file");
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<myFile> getFileEntity(@PathVariable("fileId") int fileId) {
        myFile file = fileRepository.getFileById(fileId);
        return file != null ? ResponseEntity.ok(file) : ResponseEntity.notFound().build();
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<myFile>> getFilesByCourse(@PathVariable("courseId") int courseId) {
        List<myFile> files = fileRepository.getFilesByCourse(courseId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<myFile>> getFilesByTask(@PathVariable("taskId") int taskId) {
        List<myFile> files = fileRepository.getFilesByTask(taskId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/message/{messageId}")
    public ResponseEntity<List<myFile>> getFilesByMessage(@PathVariable("messageId") int messageId) {
        List<myFile> files = fileRepository.getFilesByMessage(messageId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<myFile>> getFilesByProfile(@PathVariable("profileId") int profileId) {
        List<myFile> files = fileRepository.getFilesByProfile(profileId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<myFile>> getFilesByUser(@PathVariable("userId") int userId) {
        List<myFile> files = fileRepository.getFilesByUser(userId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") int fileId) throws NotFoundException {
        myFile file = fileRepository.getFileById(fileId);

        if (file == null) {
            throw new NotFoundException();
        }
        File fileToDownload = new File(file.getPath());
        if (!fileToDownload.exists()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = Paths.get(file.getPath()).normalize();
            Resource resource = new FileSystemResource(filePath);
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/json";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
