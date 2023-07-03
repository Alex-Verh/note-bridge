package notebridge1.notebridge;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * This class contains functions related to the webapp's security:
 * Data sanitizing, XSS protection, CSRF protection
 */
public class Security {

    public static final String CSRF_COOKIE_NAME = "X-CSRF-TOKEN";

    // Minimum 6 characters, 1 number, 1 lowercase and 1 uppercase letter, no whitespace
    public static final String password_reg = "^((?=\\S*?[A-Z])(?=\\S*?[a-z])(?=\\S*?[0-9])[^\\s]{6,})$";

    // When processed email needs to be lowercased
    // xxxxx@xxx.xxx
    public static final String email_reg = "^([\\w-]+(?:\\.[\\w-]+)*)@((?:[\\w-]+\\.)*\\w[\\w-]{0,66})\\.([a-z]{2,6}(?:\\.[a-z]{2})?)$";

    // Only letters, numbers and '-' are allowed
    public static final String name_reg = "^[-a-zA-Z0-9 ]+$";
    public static final String message_lesson_description_reg = "^[-a-zA-Z0-9 .,?!]+$";
    public static final String mark_reg = "^[0-9.]+$";
    public static final String price_reg = "^[0-9]+$";

    public static boolean validatePassword(String password){
        return password.matches(password_reg)
                && password.length() >= 6
                && password.length() <= 30;
    }

    public static boolean validateEmail(String email){
        return email.matches(email_reg);
    }

    public static boolean validateName(String name){
        return name.matches(name_reg)
                && name.length() >= 3
                && name.length() <= 40;
    }

    public static boolean validateTopic(String topic){
        return topic.matches(name_reg)
                && topic.length() >= 1
                && topic.length() <= 20;
    }
    public static boolean validateLessonDescription(String description){
        return description.matches(message_lesson_description_reg)
                && description.length() >= 1
                && description.length() <= 120;
    }
    public static boolean validateMark(String mark){
        return mark.matches(mark_reg)
                && mark.length() >= 1
                && mark.length() <= 4
                && Float.parseFloat(mark) >= 1
                && Float.parseFloat(mark) <= 10;
    }
    public static boolean validatePrice(String price){

        return price.matches(price_reg)
                && price.length() >= 1
                && price.length() <= 5;
    }

    public static boolean validateCityCountry(String text){
        return text.matches(name_reg)
                && text.length() >= 1
                && text.length() <= 50;
    }

    public static boolean validateZip(String text){
        return text.matches(name_reg)
                && text.length() >= 1
                && text.length() <= 10;
    }

    public static boolean validateMessage(String message){
        return message.matches(message_lesson_description_reg)
                && message.length() >= 1
                && message.length() <= 120;
    }

    public static String removeTags(String string){
        Document document = Jsoup.parse(string);
        return document.text();
    }

    public static String generateCsrfToken() {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    public static void storeCsrfToken(HttpServletRequest req, HttpServletResponse resp, String csrfToken) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }

        session.setAttribute(CSRF_COOKIE_NAME, csrfToken);

        Cookie cookie = new Cookie(CSRF_COOKIE_NAME, csrfToken);
        cookie.setHttpOnly(false);

//        cookie.setSecure(true);   // PREVIDER DOES NOT PROVIDE A SECURE CONNECTION, THUS THE COOKIE WILL NOT BE SENT
        cookie.setMaxAge(-1);   // valid until session end
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

    public static boolean isValidCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String sessionCsrf = (String) session.getAttribute(CSRF_COOKIE_NAME);
        if (sessionCsrf == null) {
            return false;
        }

        String requestCsrf = request.getHeader(CSRF_COOKIE_NAME);
        return sessionCsrf.equals(requestCsrf);
    }

    public static boolean isValidCsrfToken(HttpServletRequest request, String requestCsrf) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String sessionCsrf = (String) session.getAttribute(CSRF_COOKIE_NAME);
        if (sessionCsrf == null) {
            return false;
        }

        return sessionCsrf.equals(requestCsrf);
    }
}
