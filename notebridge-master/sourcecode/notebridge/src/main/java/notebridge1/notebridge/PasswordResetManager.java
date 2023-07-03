package notebridge1.notebridge;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum PasswordResetManager {
    INSTANCE;
    private final Map<String, String> passwordResetTokens = new HashMap<>(); // Simulating storage for tokens and corresponding email addresses
    public String generatePasswordResetToken(String email) {
        for (String key: passwordResetTokens.keySet()) {
            if(passwordResetTokens.get(key).equals(email)) passwordResetTokens.remove(key);
        }
        String token = UUID.randomUUID().toString();
        passwordResetTokens.put(token, email);
        return token;
    }

    public boolean validatePasswordResetToken(String token) {
        return passwordResetTokens.containsKey(token);
    }

    public String getEmailForToken(String token) {
        return passwordResetTokens.get(token);
    }

    public void removeTokenEmail(String token) {
        passwordResetTokens.remove(token);
    }
}