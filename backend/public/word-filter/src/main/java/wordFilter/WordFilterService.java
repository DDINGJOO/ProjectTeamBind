package wordFilter;

import exception.error_code.wordFilter.WordFilterErrorCode;
import exception.excrptions.WordFilterException;
import org.springframework.stereotype.Service;

@Service
public class WordFilterService {

    private final BadWordLoader badWordLoader;

    public WordFilterService(BadWordLoader badWordLoader) {
        this.badWordLoader = badWordLoader;
    }

    public boolean containsBadWord(String input) {
        if (input == null || input.isBlank()) return false;
        String lower = input.toLowerCase();
        return badWordLoader.getBadWords().stream().anyMatch(lower::contains);
    }

    public void validate(String input) {
        if (containsBadWord(input)) {
            throw new WordFilterException(WordFilterErrorCode.BAD_WORD_DETECTED);
        }
    }
}
