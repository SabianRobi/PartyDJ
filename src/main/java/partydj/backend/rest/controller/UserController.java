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
        User user = userMapper.mapSaveUserRequestToUser(userRequest);
        userValidator.validateOnPost(user);
        User savedUser = userService.save(user);
        return userMapper.mapUserToUserResponse(savedUser);
    }

    // Update
    @PatchMapping("/{userId}")
    public UserResponse update(final UpdateUserRequest userRequest, @PathVariable final int userId) {
        User updatedUserInfos = userMapper.mapUpdateUserRequestToUser(userRequest);
        userValidator.validateOnUpdate(updatedUserInfos);

        User user = userService.findById(userId);
        userValidator.validateOnGetAndDelete(user);

        User updatedUser = userService.update(user, updatedUserInfos);
        return userMapper.mapUserToUserResponse(updatedUser);
    }

    // Listing user infos
    @GetMapping("/{userId}")
    public UserResponse get(@PathVariable final int userId) {
        User user = userService.findById(userId);
        userValidator.validateOnGetAndDelete(user);
        return userMapper.mapUserToUserResponse(user);
    }

    // Delete
    @DeleteMapping("/{userId}")
    public UserResponse delete(@PathVariable final int userId) {
        User user = userService.findById(userId);
        userValidator.validateOnGetAndDelete(user);
        userService.delete(user);
        return userMapper.mapUserToUserResponse(user);
    }
}