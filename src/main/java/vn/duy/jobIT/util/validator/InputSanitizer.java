package vn.duy.jobIT.util.validator;

import org.springframework.web.util.HtmlUtils;

/**
 * Utility class for sanitizing user inputs to prevent XSS attacks
 */
public class InputSanitizer {

    private InputSanitizer() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Sanitize string input by escaping HTML characters
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(input.trim());
    }

    /**
     * Remove all HTML tags from input
     */
    public static String stripHtml(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("<[^>]*>", "").trim();
    }

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Validate phone number (basic validation)
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        String phoneRegex = "^[0-9+\\-\\s()]{10,15}$";
        return phone.matches(phoneRegex);
    }
}
