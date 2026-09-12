package mk.ukim.team38.backend.service;

import lombok.RequiredArgsConstructor;
import mk.ukim.team38.backend.dto.AuthResponse;
import mk.ukim.team38.backend.dto.LoginRequest;
import mk.ukim.team38.backend.dto.RegisterRequest;
import mk.ukim.team38.backend.model.User;
import mk.ukim.team38.backend.repository.UserRepository;
import mk.ukim.team38.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import mk.ukim.team38.backend.dto.UpdateProfileRequest;
import mk.ukim.team38.backend.dto.UserProfileResponse;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(
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
                        () -> new RuntimeException(
                                "Invalid email or password."
                        )
                );

        if (
                !passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                )
        ) {
            throw new RuntimeException(
                    "Invalid email or password."
            );
        }

        return buildAuthResponse(
                user,
                "Login successful."
        );
    }

    public User save(User user) {
        if (
                user.getRole() == null
                        || user.getRole().isBlank()
        ) {
            user.setRole("USER");
        }

        if (
                user.getPassword() != null
                        && !user.getPassword().startsWith("$2")
        ) {
            user.setPassword(
                    passwordEncoder.encode(user.getPassword())
            );
        }

        return userRepository.save(user);
    }

    public User update(
            Long id,
            User userDetails
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found with id: " + id
                        )
                );

        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());

        if (
                userDetails.getPassword() != null
                        && !userDetails.getPassword().isBlank()
        ) {
            user.setPassword(
                    passwordEncoder.encode(
                            userDetails.getPassword()
                    )
            );
        }

        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
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
            throw new RuntimeException(
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
}