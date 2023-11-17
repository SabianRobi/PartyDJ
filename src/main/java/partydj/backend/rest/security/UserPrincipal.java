package partydj.backend.rest.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import partydj.backend.rest.domain.User;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    final private User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(user.getUserType() == UserType.ADMIN ? Role.ROLE_ADMIN : Role.ROLE_NORMAL);
        return List.of(Role.ROLE_NORMAL);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
