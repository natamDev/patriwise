package com.finmate.domain.service;

import com.finmate.domain.model.User;
import com.finmate.domain.port.UserRepository;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class AuthService {

    private static final long TOKEN_EXPIRY_SECONDS = 60 * 60 * 24 * 7L; // 7 days

    private final UserRepository repository;

    public AuthService(UserRepository repository) {
        this.repository = repository;
    }

    public String register(String email, String password) {
        if (repository.existsByEmail(email)) {
            throw new IllegalStateException("Un compte existe déjà avec cet email.");
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setCreatedAt(LocalDateTime.now());
        User saved = repository.save(user);
        return generateToken(saved);
    }

    public String login(String email, String password) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect."));
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect.");
        }
        return generateToken(user);
    }

    private String generateToken(User user) {
        return Jwt.issuer("finmate")
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .expiresIn(TOKEN_EXPIRY_SECONDS)
                .sign();
    }
}
