package partydj.backend.rest.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.request.UserRequest;
import partydj.backend.rest.domain.response.UserResponse;
import partydj.backend.rest.mapper.UserMapper;
import partydj.backend.rest.security.UserPrincipal;
import partydj.backend.rest.service.UserService;
import partydj.backend.rest.validation.constraint.Name;

@RestController
@RequestMapping(value = "/api/v1/user", produces = "application/json")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    // Register
    @PostMapping(consumes = "application/x-www-form-urlencoded")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse save(@Valid final UserRequest userRequest) {
        final User savedUser = userService.register(userRequest);

        return userMapper.mapUserToUserResponse(savedUser);
    }

    // Update
    @PutMapping(value = "/{givenUsername}", consumes = "application/x-www-form-urlencoded")
    public UserResponse update(@Valid final UserRequest userRequest,
                               @PathVariable("givenUsername") @Name final String toBeUpdatedUsername,
                               final Authentication auth,
                               final UserPrincipal userPrincipal) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final User updatedUser = userService.update(loggedInUser, toBeUpdatedUsername, userRequest);

        // Update logged in user infos
        userPrincipal.setUser(updatedUser);
        final Authentication newAuth = new UsernamePasswordAuthenticationToken(
                userPrincipal, auth.getCredentials(), auth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return userMapper.mapUserToUserResponse(updatedUser);
    }

    // Listing user infos
    @GetMapping("/{username}")
    public UserResponse get(@PathVariable @Name final String username) {
        final User toGetUser = userService.findByUsername(username);

        return userMapper.mapUserToUserResponse(toGetUser);
    }

    // Delete
    @DeleteMapping("/{username}")
    public UserResponse delete(@PathVariable("username") @Name final String toBeDeletedUsername,
                               final Authentication auth,
                               final HttpServletRequest request) {
        final User loggedInUser = userService.findByUsername(auth.getName());
        final User deletedUser = userService.delete(loggedInUser, toBeDeletedUsername);

        // Log out the user
        try {
            request.logout();
        } catch (final ServletException ignored) {
        }

        return userMapper.mapUserToUserResponse(deletedUser);
    }
}
