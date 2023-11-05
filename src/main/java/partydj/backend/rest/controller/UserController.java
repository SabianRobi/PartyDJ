package partydj.backend.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.response.UserResponse;
import partydj.backend.rest.service.UserService;
import partydj.backend.rest.transformer.UserTransformer;
import partydj.backend.rest.validation.UserValidator;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private UserTransformer userTransformer;

    @GetMapping("/api/v1/user/{userId}")
    public UserResponse get(@PathVariable final int userId) {
        User user =  userService.findById(userId);
        userValidator.validateOnGet(user);
        return userTransformer.transformUserToUserResponse(user);
    }
}
