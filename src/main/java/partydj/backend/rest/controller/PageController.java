package partydj.backend.rest.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: REMOVE THIS FILE WHEN FRONTEND COMES ALIVE

@RestController
public class PageController {
    @GetMapping("/")
    public PageResponse get() {
        return new PageResponse("main page");
    }

    @GetMapping("/login")
    public PageResponse getLogin() {
        return new PageResponse("login page");
    }

    @GetMapping("/protected")
    public String getProtected(final Authentication auth) {
        return auth.getName() + ", " + auth.getAuthorities();
    }

    @GetMapping("/error")
    public PageResponse getErrorPage() {
        return new PageResponse("Something went wrong. Error page :(");
    }

    @Getter
    @Setter
    @Builder
    public static class PageResponse {
        private String message;
    }
}
