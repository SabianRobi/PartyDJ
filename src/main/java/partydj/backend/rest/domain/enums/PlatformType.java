package partydj.backend.rest.domain.enums;

import java.util.Arrays;
import java.util.Collection;

public enum PlatformType {
    SPOTIFY,
    YOUTUBE;

    public static Collection<String> getPlatformTypes() {
        return Arrays.stream(PlatformType.values()).map(Enum::name).toList();
    }
}
