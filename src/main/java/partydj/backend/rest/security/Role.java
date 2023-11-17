package partydj.backend.rest.security;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_NORMAL;
    @Override
    public String getAuthority() {
        return name();
    }
}
