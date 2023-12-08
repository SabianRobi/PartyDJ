package partydj.backend.rest.domain.error;

import lombok.Getter;

@Getter
public class NotUniqueException extends RuntimeException {
    private final String key;

    public NotUniqueException(final String key, final String message) {
        super(message);
        this.key = key;
    }
}
