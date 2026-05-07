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
     * Validate file (both image and document)
     */
    public static boolean isValidFile(MultipartFile file) {
        return isValidImage(file) || isValidDocument(file);
    }

    /**
     * Get detailed validation error message
     */
    public static String getValidationError(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "File is empty, please upload a file";
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return "File size exceeds maximum allowed size of 50MB";
        }

        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        if (contentType == null) {
            return "File content type is missing";
        }

        if (filename != null && !isExtensionMatchingContentType(filename, contentType)) {
            return "File extension does not match content type. Possible file spoofing attempt.";
        }

        List<String> allAllowedTypes = new java.util.ArrayList<>();
        allAllowedTypes.addAll(ALLOWED_IMAGE_TYPES);
        allAllowedTypes.addAll(ALLOWED_DOCUMENT_TYPES);

        if (!allAllowedTypes.contains(contentType.toLowerCase())) {
            return "Invalid file type. Allowed types: images (jpg, png, gif, webp) and documents (pdf, doc, docx)";
        }

        return null; // No error
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
