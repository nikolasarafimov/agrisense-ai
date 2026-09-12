package mk.ukim.team38.backend.service;

import lombok.RequiredArgsConstructor;
import mk.ukim.team38.backend.dto.AuthResponse;
import mk.ukim.team38.backend.dto.LoginRequest;
import mk.ukim.team38.backend.dto.RegisterRequest;
import mk.ukim.team38.backend.dto.UpdateProfileRequest;
import mk.ukim.team38.backend.dto.UserProfileResponse;
import mk.ukim.team38.backend.exception.ConflictException;
import mk.ukim.team38.backend.model.User;
import mk.ukim.team38.backend.repository.UserRepository;
import mk.ukim.team38.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(
                    "User with this email already exists."
            );
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        return buildAuthResponse(
                savedUser,
                "Registration successful."
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Invalid email or password."
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "Invalid email or password."
            );
        }

        return buildAuthResponse(
                user,
                "Login successful."
        );
    }

    public UserProfileResponse getProfile(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public AuthResponse updateProfile(
            User user,
            UpdateProfileRequest request
    ) {
        if (
                !user.getEmail().equalsIgnoreCase(request.getEmail())
                        && userRepository.existsByEmail(request.getEmail())
        ) {
            throw new ConflictException(
                    "User with this email already exists."
            );
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        if (
                request.getPassword() != null
                        && !request.getPassword().isBlank()
        ) {
            user.setPassword(
                    passwordEncoder.encode(request.getPassword())
            );
        }

        User savedUser = userRepository.save(user);

        return buildAuthResponse(
                savedUser,
                "Profile updated successfully."
        );
    }

    private AuthResponse buildAuthResponse(
            User user,
            String message
    ) {
        String token = jwtService.generateToken(user);

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                token,
                message
        );
    }
}