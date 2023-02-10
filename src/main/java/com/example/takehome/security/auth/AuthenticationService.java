package com.example.takehome.security.auth;

import com.example.takehome.security.config.JwtService;
import com.example.takehome.security.user.Role;
import com.example.takehome.security.user.User;
import com.example.takehome.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        return
            repository.findByEmail(request.getEmail())
                .map(user ->
                    AuthenticationResponse
                        .builder()
                        .msg("User already exist")
                        .build())
                .orElseGet(() -> {
                    final var user = User.builder()
                        .firstname(request.getFirstname())
                        .lastname(request.getLastname())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(Role.USER)
                        .build();
                    repository.save(user);
                    final var jwtToken = jwtService.generateToken(user);
                    return AuthenticationResponse.builder()
                        .token(jwtToken)
                        .build();
                });
  }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()));

        final var user = repository
            .findByEmail(request.getEmail())
            .orElseThrow();

        final var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
  }
}
