package partydj.backend.rest.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.request.SaveUserRequest;
import partydj.backend.rest.domain.request.UpdateUserRequest;
import partydj.backend.rest.domain.response.UserResponse;
import partydj.backend.rest.mapper.UserMapper;
import partydj.backend.rest.security.UserPrincipal;
import partydj.backend.rest.service.UserService;
import partydj.backend.rest.validation.UserValidator;

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
    public UserResponse save(final SaveUserRequest userRequest) {
        userValidator.validateOnPost(userRequest);

        User user = userMapper.mapSaveUserRequestToUser(userRequest);
        User savedUser = userService.register(user);

        return userMapper.mapUserToUserResponse(savedUser);
    }

    // Update
    @PatchMapping(value = "/{userId}", consumes = "application/x-www-form-urlencoded")
    public UserResponse update(final UpdateUserRequest userRequest, @PathVariable final int userId,
                               final Authentication auth, final UserPrincipal userPrincipal) {
        User loggedInUser = userService.findByUsername(auth.getName());
        User toBeUpdatedUser = userService.findById(userId);

        userValidator.validateOnPatch(userRequest, toBeUpdatedUser, loggedInUser);

        User updatedUser = userService.update(toBeUpdatedUser, userRequest);

        // Update logged in user infos
        userPrincipal.setUser(updatedUser);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                userPrincipal, auth.getCredentials(), auth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return userMapper.mapUserToUserResponse(updatedUser);
    }

    // Listing user infos
    @GetMapping("/{userId}")
    public UserResponse get(@PathVariable final int userId) {
        User toGetUser = userService.findById(userId);

        userValidator.validateOnGet(toGetUser);

        return userMapper.mapUserToUserResponse(toGetUser);
    }

    // Delete
    @DeleteMapping("/{userId}")
    public UserResponse delete(@PathVariable final int userId, final Authentication auth,
                               final HttpServletRequest request) {
        User loggedInUser = userService.findByUsername(auth.getName());
        User toBeDeletedUser = userService.findById(userId);

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
