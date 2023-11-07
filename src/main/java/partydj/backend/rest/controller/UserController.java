package partydj.backend.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.request.SaveUserRequest;
import partydj.backend.rest.domain.response.UserResponse;
import partydj.backend.rest.service.UserService;
import partydj.backend.rest.mapper.UserMapper;
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

    @PostMapping
    public UserResponse save(final SaveUserRequest userRequest) {
        User user = userMapper.mapUserRequestToUser(userRequest);
        userValidator.validateOnPost(user);
        User savedUser = userService.save(user);
        return userMapper.mapUserToUserResponse(savedUser);
    }

    @GetMapping("/{userId}")
    public UserResponse get(@PathVariable final int userId) {
        User user =  userService.findById(userId);
        userValidator.validateOnGet(user);
        return userTransformer.transformUserToUserResponse(user);
        User user = userService.findById(userId);
        return userMapper.mapUserToUserResponse(user);
    }
}
