package mk.ukim.team38.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mk.ukim.team38.backend.dto.AuthResponse;
import mk.ukim.team38.backend.dto.LoginRequest;
import mk.ukim.team38.backend.dto.RegisterRequest;
import mk.ukim.team38.backend.dto.UpdateProfileRequest;
import mk.ukim.team38.backend.dto.UserProfileResponse;
import mk.ukim.team38.backend.model.User;
import mk.ukim.team38.backend.security.AuthenticatedUserService;
import mk.ukim.team38.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticatedUserService authenticatedUserService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        userService.register(request)
                );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                userService.login(request)
        );
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        User user =
                authenticatedUserService.getCurrentUser();

        return ResponseEntity.ok(
                userService.getProfile(user)
        );
    }

    @PutMapping("/me")
    public ResponseEntity<AuthResponse> updateCurrentUser(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        User user =
                authenticatedUserService.getCurrentUser();

        return ResponseEntity.ok(
                userService.updateProfile(
                        user,
                        request
                )
        );
    }
}