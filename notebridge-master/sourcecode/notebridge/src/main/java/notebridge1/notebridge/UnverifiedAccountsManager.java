package notebridge1.notebridge;

import notebridge1.notebridge.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public enum UnverifiedAccountsManager {
    INSTANCE;
    private HashMap<String, User> unverifiedUsers = new HashMap<>();
    private HashSet<User> teacher = new HashSet<>();

    public String generateUserToken(User user) {
        // Delete previous copies of unverified users
        for (String key: unverifiedUsers.keySet()) {
            if(unverifiedUsers.get(key).equals(user)) unverifiedUsers.remove(key);
        }
        String token = UUID.randomUUID().toString();
        unverifiedUsers.put(token, user);
        return token;
    }

    public boolean validateUserToken(String token) {
        return unverifiedUsers.containsKey(token);
    }

    public User getUserForToken(String token) {
        return unverifiedUsers.get(token);
    }

    public void removeTokenUser(String token) {
        unverifiedUsers.remove(token);
    }
    
    public boolean isTeacher(User user) {
        return teacher.contains(user);
    }
    
    public void addTeacher(User user) {
        teacher.add(user);
    }

    public void removeTeacher(User user) {
        teacher.remove(user);
    }
}
