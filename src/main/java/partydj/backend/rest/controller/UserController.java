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
import partydj.backend.rest.validation.UserValidator;
import partydj.backend.rest.validation.constraint.Name;

@RestController
@RequestMapping(value = "/api/v1/user", produces = "application/json")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

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
                               @PathVariable @Name final String givenUsername,
                               final Authentication auth,
                               final UserPrincipal userPrincipal) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final User updatedUser = userService.update(givenUsername, loggedInUser, userRequest);

        // Update logged in user infos
        userPrincipal.setUser(updatedUser);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                userPrincipal, auth.getCredentials(), auth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return userMapper.mapUserToUserResponse(updatedUser);
    }

    // Listing user infos
    @GetMapping("/{username}")
    public UserResponse get(@PathVariable final String username) {
        User toGetUser = userService.findByUsername(username);

        userValidator.validateOnGet(toGetUser);

        return userMapper.mapUserToUserResponse(toGetUser);
    }

    // Delete
    @DeleteMapping("/{username}")
    public UserResponse delete(@PathVariable final String username, final Authentication auth,
                               final HttpServletRequest request) {
        User loggedInUser = userService.findByUsername(auth.getName());
        User toBeDeletedUser = userService.findByUsername(username);

        userValidator.validateOnDelete(toBeDeletedUser, loggedInUser);

        userService.delete(toBeDeletedUser);

        // Log out the user
        try {
            request.logout();
        } catch (ServletException ignored) {
        }

        return userMapper.mapUserToUserResponse(toBeDeletedUser);
    }
}
