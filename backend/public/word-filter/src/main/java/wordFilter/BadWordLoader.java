package wordFilter;


import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@Getter
public class BadWordLoader {
    private final Set<String> badWords = new HashSet<>();

    @PostConstruct
    public void load() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(
                        getClass().getResourceAsStream("/badwords.txt"))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    badWords.add(trimmed.toLowerCase());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("금지어 로딩 실패", e);
        }
    }
}
