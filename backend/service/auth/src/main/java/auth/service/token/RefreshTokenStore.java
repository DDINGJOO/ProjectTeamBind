package auth.service.token;

import java.time.Duration;
import java.util.Optional;

public interface RefreshTokenStore {
    void save(String userId, String deviceId, String token, Duration ttl);
    Optional<String> find(String userId, String deviceId);
    void remove(String userId, String deviceId);
    void removeAll(String userId);
    boolean isValid(String userId, String deviceId, String token);
}