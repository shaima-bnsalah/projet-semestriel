package com.gitquality.git_quality.controller;import com.gitquality.service.AuthService;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        System.out.println(">>> REGISTER APPELÉ : " + req.getEmail());
        try {
            String token = authService.register(
                req.getUsername(),
                req.getEmail(),
                req.getPassword()
            );
            return ResponseEntity.ok(new AuthResponse(token, "Inscription réussie !"));
        } catch (Exception e) {
            System.out.println(">>> ERREUR : " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        System.out.println(">>> LOGIN APPELÉ : " + req.getEmail());
        try {
            String token = authService.login(req.getEmail(), req.getPassword());
            return ResponseEntity.ok(new AuthResponse(token, "Connexion réussie !"));
        } catch (Exception e) {
            System.out.println(">>> ERREUR : " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ Classes INTERNES au controller
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class RegisterRequest {
        private String username;
        private String email;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class AuthResponse {
        private String token;
        private String message;
    }
}