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
import partydj.backend.rest.entity.User;
import partydj.backend.rest.entity.request.UserRequest;
import partydj.backend.rest.entity.response.UserResponse;
import partydj.backend.rest.security.UserPrincipal;
import partydj.backend.rest.service.UserService;
import partydj.backend.rest.validation.constraint.Name;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/user", produces = "application/json")
public class UserController {

    @Autowired
    private UserService userService;

    // Register
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse save(@Valid @RequestBody final UserRequest userRequest) {
        return userService.register(userRequest);
    }

    // Update
    @PutMapping(value = "/{givenUsername}", consumes = "application/json")
    public UserResponse update(@Valid @RequestBody final UserRequest userRequest,
                               @PathVariable("givenUsername") @Name final String toBeUpdatedUsername,
                               final Authentication auth,
                               final UserPrincipal userPrincipal) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final UserResponse response = userService.update(loggedInUser, toBeUpdatedUsername, userRequest);

        // Update logged in user infos
        userPrincipal.setUser(loggedInUser);
        final Authentication newAuth = new UsernamePasswordAuthenticationToken(
                userPrincipal, auth.getCredentials(), auth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return response;
    }

    // Listing user infos
    @GetMapping("/{username}")
    public UserResponse get(@PathVariable @Name final String username) {
        final User toGetUser = userService.findByUsername(username);

        return userService.getUserInfo(toGetUser);
    }

    // Delete
    @DeleteMapping("/{username}")
    public UserResponse delete(@PathVariable("username") @Name final String toBeDeletedUsername,
                               final Authentication auth,
                               final HttpServletRequest request) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final UserResponse response = userService.delete(loggedInUser, toBeDeletedUsername);

        // Log out the user
        try {
            request.logout();
        } catch (final ServletException ignored) {
        }

        return response;
    }
}
