package com.farmacia.taller.v1.controller;

import com.farmacia.taller.v1.model.Role;
import com.farmacia.taller.v1.model.Usuario;
import com.farmacia.taller.v1.repository.UsuarioRepository;
import com.farmacia.taller.v1.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // DTOs definidos de manera concisa mediante Java Records
    public record AuthRequest(String username, String password, Role role) {
        public AuthRequest(String username, String password) {
            this(username, password, null);
        }
    }
    public record AuthResponse(String token) {}

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        Role assignedRole = request.role() != null ? request.role() : Role.ADMIN;
        var user = Usuario.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password())) // Siempre encriptar con BCrypt
                .role(assignedRole) // Por defecto en este ejercicio registramos con ADMIN salvo que se especifique
                .build();
        
        usuarioRepository.save(user);
        
        var jwtToken = jwtService.generateToken(Map.of("role", user.getRole().name()), user);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

    @PostMapping("/register-user")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody AuthRequest request) {
        var user = Usuario.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
        
        usuarioRepository.save(user);
        
        var jwtToken = jwtService.generateToken(Map.of("role", user.getRole().name()), user);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // AuthenticationManager valida las credenciales; si fallan, arroja BadCredentialsException
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        
        var user = usuarioRepository.findByUsername(request.username())
                .orElseThrow();
        
        var jwtToken = jwtService.generateToken(Map.of("role", user.getRole().name()), user);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }
}
