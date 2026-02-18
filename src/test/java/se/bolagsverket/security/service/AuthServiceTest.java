package se.bolagsverket.security.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import se.bolagsverket.security.dto.AuthResponse;
import se.bolagsverket.security.dto.LoginRequest;
import se.bolagsverket.security.dto.RegisterRequest;
import se.bolagsverket.security.modal.User;
import se.bolagsverket.security.repo.UserRepository;
import se.bolagsverket.security.utils.JwtUtil;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_withValidRequest_savesUserAndReturnsMessage() {
        RegisterRequest request = new RegisterRequest("user1", "password123");

        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");

        String result = authService.register(request);

        assertNotNull(result);
        assertTrue(result.contains("Successfully created user"));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_withValidCredentials_returnsJwtToken() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("user1");
        user.setHashPassword("hashed");

        LoginRequest request = new LoginRequest("user1", "password123");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(userId, "user1")).thenReturn("mocked.jwt.token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertFalse(response.getAccessToken().isBlank());
        assertEquals("mocked.jwt.token", response.getAccessToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(userId, "user1");
    }
}