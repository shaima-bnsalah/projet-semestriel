package com.gitquality.git_quality.controller;

import com.gitquality.git_quality.service.AuthService;
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

    @GetMapping("/test")
    public String test() {
        return "Le controller AuthController est bien actif !";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        System.out.println(">>> REQUETE REGISTER RECUE POUR : " + req.getEmail());
        try {
            String token = authService.register(
                req.getUsername(),
                req.getEmail(),
                req.getPassword()
            );
            return ResponseEntity.ok(new AuthResponse(token, "Inscription réussie !"));
        } catch (Exception e) {
            System.out.println(">>> ERREUR REGISTER : " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        System.out.println(">>> REQUETE LOGIN RECUE POUR : " + req.getEmail());
        try {
            String token = authService.login(req.getEmail(), req.getPassword());
            return ResponseEntity.ok(new AuthResponse(token, "Connexion réussie !"));
        } catch (Exception e) {
            System.out.println(">>> ERREUR LOGIN : " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/forgot-password")
public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req) {
    System.out.println(">>> REINIT MOT DE PASSE POUR : " + req.getEmail());
    try {
        String message = authService.resetPassword(req.getEmail(), req.getNewPassword());
        return ResponseEntity.ok(new AuthResponse(null, message));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private String message;
    }
    @Data
@NoArgsConstructor
@AllArgsConstructor
public static class ForgotPasswordRequest {
    private String email;
    private String newPassword;
}
}  