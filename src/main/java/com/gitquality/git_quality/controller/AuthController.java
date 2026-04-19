package com.gitquality.git_quality.controller;

import com.gitquality.git_quality.model.User;
import com.gitquality.git_quality.service.AuthService;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/test")
    public String test() {
        return "Le controller AuthController est bien actif !";
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            String token = authService.register(req.getUsername(), req.getEmail(), req.getPassword());
            return ResponseEntity.ok(new AuthResponse(token, "Inscription réussie !"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            String token = authService.login(req.getEmail(), req.getPassword());
            return ResponseEntity.ok(new AuthResponse(token, "Connexion réussie !"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        try {
            String message = authService.resetPassword(req.getEmail(), req.getNewPassword());
            return ResponseEntity.ok(new AuthResponse(null, message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(@RequestParam String email) {
        try {
            authService.deleteUser(email);
            return ResponseEntity.ok("Compte supprimé avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/verify-email")
public ResponseEntity<?> verifyEmail(@RequestBody VerifyRequest req) {
    try {
        String message = authService.verifyCode(req.getEmail(), req.getCode());
        return ResponseEntity.ok(new AuthResponse(null, message));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}


    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ForgotPasswordRequest {
        private String email;
        private String newPassword;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private String message;
    }
    @Data @NoArgsConstructor @AllArgsConstructor
public static class VerifyRequest {
    private String email;
    private String code;
}
}