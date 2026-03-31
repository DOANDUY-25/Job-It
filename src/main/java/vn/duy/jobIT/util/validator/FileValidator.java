package vn.duy.jobIT.util.validator;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Validator for file uploads to prevent malicious files
 */
public class FileValidator {

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    private FileValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Validate image file
     */
    public static boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            return false;
        }

        return file.getSize() <= MAX_FILE_SIZE;
    }

    /**
     * Validate document file (PDF, DOC, DOCX)
     */
    public static boolean isValidDocument(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_DOCUMENT_TYPES.contains(contentType.toLowerCase())) {
            return false;
        }

        return file.getSize() <= MAX_FILE_SIZE;
    }

    /**
     * Sanitize filename to prevent path traversal attacks
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null) {
            return null;
        }
        
        // Remove path separators and special characters
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Check if file extension matches content type
     */
    public static boolean isExtensionMatchingContentType(String filename, String contentType) {
        if (filename == null || contentType == null) {
            return false;
        }

        String extension = getFileExtension(filename).toLowerCase();
        String type = contentType.toLowerCase();

        return switch (extension) {
            case "jpg", "jpeg" -> type.equals("image/jpeg") || type.equals("image/jpg");
            case "png" -> type.equals("image/png");
            case "gif" -> type.equals("image/gif");
            case "pdf" -> type.equals("application/pdf");
            case "doc" -> type.equals("application/msword");
            case "docx" -> type.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            default -> false;
        };
    }

    private static String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1) : "";
    }
}
