package auth.service.token;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import token.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenStore store;
    private final JwtTokenProvider provider;

    public void save(String userId, String deviceId, String refreshToken) {
        store.save(userId, deviceId, refreshToken, provider.getRefreshTokenTTL());
    }

    public boolean isValid(String userId, String deviceId, String refreshToken) {
        return store.isValid(userId, deviceId, refreshToken);
    }



    public void delete(String userId, String deviceId) {
        store.remove(userId, deviceId);
    }

    public void deleteAllByUser(String userId) {
        store.removeAll(userId);
    }
}