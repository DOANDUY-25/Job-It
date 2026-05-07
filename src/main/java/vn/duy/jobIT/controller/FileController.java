package vn.duy.jobIT.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.duy.jobIT.domain.res.files.UploadFileResponse;
import vn.duy.jobIT.service.FileService;
import vn.duy.jobIT.util.annotation.ApiMessage;
import vn.duy.jobIT.util.error.StorageException;
import vn.duy.jobIT.util.validator.FileValidator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping(path = "${apiPrefix}/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    @Value("${duy.upload-file.base-uri}")
    private String baseURI;

    @PostMapping("")
    @ApiMessage("Upload single file")
    public ResponseEntity<UploadFileResponse> upload(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder
    ) throws URISyntaxException, IOException, StorageException {
        // Validate file using FileValidator
        String validationError = FileValidator.getValidationError(file);
        if (validationError != null) {
            throw new StorageException(validationError);
        }

        // Sanitize folder name to prevent path traversal
        String sanitizedFolder = FileValidator.sanitizeFilename(folder);
        if (sanitizedFolder == null || sanitizedFolder.isEmpty()) {
            throw new StorageException("Invalid folder name");
        }

        this.fileService.createUploadFolder(baseURI + sanitizedFolder);
        return ResponseEntity.ok().body(this.fileService.store(file, sanitizedFolder));
    }

    @GetMapping("/files")
    @ApiMessage("Download a file")
    public ResponseEntity<Resource> download(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder)
            throws StorageException, URISyntaxException, FileNotFoundException {
        if (fileName == null || folder == null) {
            throw new StorageException("Missing required params : (fileName or folder) in query params.");
        }

        // Sanitize inputs to prevent path traversal attacks
        String sanitizedFileName = FileValidator.sanitizeFilename(fileName);
        String sanitizedFolder = FileValidator.sanitizeFilename(folder);

        if (sanitizedFileName == null || sanitizedFolder == null) {
            throw new StorageException("Invalid file name or folder name");
        }

        // check file exist (and not a directory)
        long fileLength = this.fileService.getFileLength(sanitizedFileName, sanitizedFolder);
        if (fileLength == 0) {
            throw new StorageException("File with name = " + sanitizedFileName + " not found.");
        }

        // download a file
        InputStreamResource resource = this.fileService.getResource(sanitizedFileName, sanitizedFolder);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sanitizedFileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
