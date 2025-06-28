package auth.service.token;

// TokenBlacklistStore.java
public interface TokenBlacklistStore {
    void blacklist(String token);
    boolean isBlacklisted(String token);
}