package com.betochimas.historical_conflicts_api.auth;

import com.betochimas.historical_conflicts_api.auth.dto.AuthResponse;
import com.betochimas.historical_conflicts_api.auth.dto.LoginRequest;
import com.betochimas.historical_conflicts_api.auth.dto.RegisterRequest;
import com.betochimas.historical_conflicts_api.config.DuplicateUserException;
import com.betochimas.historical_conflicts_api.config.InvalidCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUserException("username", request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("email", request.getEmail());
        }
        UserEntity user = new UserEntity(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER);
        UserEntity saved = userRepository.save(user);
        JwtService.IssuedToken issued = jwtService.issue(saved.getUsername(), saved.getRole());
        return new AuthResponse(issued.token(), issued.expiresAt());
    }

    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            // Reuse the generic exception so a disabled account is indistinguishable from a bad password.
            throw new InvalidCredentialsException();
        }
        JwtService.IssuedToken issued = jwtService.issue(user.getUsername(), user.getRole());
        return new AuthResponse(issued.token(), issued.expiresAt());
    }
}
