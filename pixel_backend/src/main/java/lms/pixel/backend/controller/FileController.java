package lms.pixel.backend.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lms.pixel.backend.config.PropertiesConfig;
import lms.pixel.backend.exceptions.NotFoundException;
import lms.pixel.backend.model.myFile;
import lms.pixel.backend.utils.FileValidator;
import lms.pixel.backend.utils.FileStorageManager;
import lms.pixel.backend.repository.FileRepository;
import lms.pixel.backend.security.PermissionChecker;

import java.util.List;

@RestController
@RequestMapping("api/file")
public class FileController {

    private final FileRepository fileRepository;
    private final PermissionChecker permissionChecker;
    private final FileValidator fileValidator;
    private final FileStorageManager fileStorageManager;

    public FileController(FileRepository fileRepository, PermissionChecker permissionChecker, 
                          FileValidator fileValidator, FileStorageManager fileStorageManager, PropertiesConfig propertiesConfig) {
        this.fileRepository = fileRepository;
        this.permissionChecker = permissionChecker;
        this.fileValidator = fileValidator;

        this.fileStorageManager = fileStorageManager;
        fileStorageManager.setPath(propertiesConfig.getFileStoragePath());
    }

    @PostMapping("/")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("attachToId") int attachToId,
            @RequestParam("foreignKeyType") String foreignKeyType,
            @RequestHeader("Authorization") String token) {

        int userId = permissionChecker.getUseridByToken(token);

        String validationError = fileValidator.validate(file);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }

        String filePath = fileStorageManager.storeFile(file);
        if (filePath == null) {
            return ResponseEntity.status(500).body("Failed to store the file");
        }

        fileRepository.attach(filePath, userId, attachToId, foreignKeyType);
        return ResponseEntity.ok("File uploaded successfully");
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<myFile> getFileEntity(@PathVariable("fileId") int fileId) {
        myFile file = fileRepository.getFileById(fileId);
        return file != null ? ResponseEntity.ok(file) : ResponseEntity.notFound().build();
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<myFile>> getFilesByCourse(@PathVariable("courseId") int courseId) {
        return ResponseEntity.ok(fileRepository.getFilesByCourse(courseId));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<myFile>> getFilesByTask(@PathVariable("taskId") int taskId) {
        return ResponseEntity.ok(fileRepository.getFilesByTask(taskId));
    }

    @GetMapping("/message/{messageId}")
    public ResponseEntity<List<myFile>> getFilesByMessage(@PathVariable("messageId") int messageId) {
        return ResponseEntity.ok(fileRepository.getFilesByMessage(messageId));
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<myFile>> getFilesByProfile(@PathVariable("profileId") int profileId) {
        return ResponseEntity.ok(fileRepository.getFilesByProfile(profileId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<myFile>> getFilesByUser(@PathVariable("userId") int userId) {
        return ResponseEntity.ok(fileRepository.getFilesByUser(userId));
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") int fileId) throws NotFoundException {
        return fileStorageManager.getDownloadableFile(fileId, fileRepository);
    }
}
