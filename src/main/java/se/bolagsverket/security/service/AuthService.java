package se.bolagsverket.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.bolagsverket.error.ErrorType;
import se.bolagsverket.error.UserException;
import se.bolagsverket.security.dto.AuthResponse;
import se.bolagsverket.security.dto.LoginRequest;
import se.bolagsverket.security.dto.RegisterRequest;
import se.bolagsverket.security.modal.User;
import se.bolagsverket.security.repo.UserRepository;
import se.bolagsverket.security.utils.JwtUtil;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed: username '{}' already exists", request.getUsername());
            throw new UserException(ErrorType.ALREADY_EXISTS, "Username '" + request.getUsername() + "' is already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setHashPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        log.info("User registered: id={}, username={}", user.getId(), user.getUsername());

        return "Successfully created user: " + user.getId();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.warn("Login failed: bad credentials for username '{}'", request.getUsername());
            throw new UserException(ErrorType.BAD_CREDENTIALS, "Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserException(ErrorType.NOT_FOUND, "User '" + request.getUsername() + "' not found"));

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        log.info("User logged in: id={}, username={}", user.getId(), user.getUsername());

        return new AuthResponse(token);
    }
}