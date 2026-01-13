package db;

import model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStorage {
    private static final Map<String, User> sessionStore = new ConcurrentHashMap<>();

    public static void addSession(String sid, User user) {
        sessionStore.put(sid, user);
    }

    public static User findUserBySid(String sid) {
        if (sid == null || sid.isEmpty()) return null;
        return sessionStore.getOrDefault(sid, null);
    }

    public static void removeSession(String sid) {
        sessionStore.remove(sid);
    }
}
