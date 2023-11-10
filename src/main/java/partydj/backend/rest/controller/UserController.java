package partydj.backend.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.request.SaveUserRequest;
import partydj.backend.rest.domain.request.UpdateUserRequest;
import partydj.backend.rest.domain.response.UserResponse;
import partydj.backend.rest.mapper.UserMapper;
import partydj.backend.rest.service.UserService;
import partydj.backend.rest.validation.UserValidator;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private UserMapper userMapper;

    // Register
    @PostMapping
    public UserResponse save(final SaveUserRequest userRequest) {
        userValidator.validateOnPost(userRequest);
        User user = userMapper.mapSaveUserRequestToUser(userRequest);
        User savedUser = userService.save(user);
        return userMapper.mapUserToUserResponse(savedUser);
    }

    // Update
    @PatchMapping("/{userId}")
    public UserResponse update(final UpdateUserRequest userRequest, @PathVariable final int userId) {
        User user = userService.findById(userId);
        userValidator.validateOnPatch(userRequest, user);

        User updatedUser = userService.update(user, userRequest);
        return userMapper.mapUserToUserResponse(updatedUser);
    }

    // Listing user infos
    @GetMapping("/{userId}")
    public UserResponse get(@PathVariable final int userId) {
        User user = userService.findById(userId);
        userValidator.validateOnGet(user);
        return userMapper.mapUserToUserResponse(user);
    }

    // Delete
    @DeleteMapping("/{userId}")
    public UserResponse delete(@PathVariable final int userId) {
        User user = userService.findById(userId);
        userValidator.validateOnDelete(user);
        userService.delete(user);
        return userMapper.mapUserToUserResponse(user);
    }
}
