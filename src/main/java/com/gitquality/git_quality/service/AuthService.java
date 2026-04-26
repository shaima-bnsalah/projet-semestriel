package com.gitquality.git_quality.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitquality.git_quality.btree.BTree;
import com.gitquality.git_quality.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthService {

    private BTree<User> userTree = new BTree<>(3);
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String FILE_PATH = "users.json";

    public AuthService(JwtService jwtService, BCryptPasswordEncoder encoder, EmailService emailService) {
        this.jwtService = jwtService;
        this.encoder = encoder;
        this.emailService = emailService;
    }

    @PostConstruct
    public void init() {
        loadDataFromFile();
    }

    public String register(String username, String email, String password) {
        if (userTree.search(email) != null) {
            throw new RuntimeException("Email déjà utilisé !");
        }
        String code = String.format("%06d", new Random().nextInt(999999));
        User user = new User(UUID.randomUUID().toString(), username, email, encoder.encode(password), code, false);
        userTree.insert(email, user);
        saveDataToFile();
        try {
            emailService.sendCode(email, code);
        } catch (Exception e) {
            System.out.println("Erreur Email. CODE DE SECOURS : " + code);
        }
        return "Code envoyé.";
    }

    public String login(String email, String password) {
        User user = userTree.search(email);
        if (user == null) throw new RuntimeException("Utilisateur non trouvé !");
        if (!user.isVerified()) throw new RuntimeException("Veuillez d'abord vérifier votre email !");
        if (!encoder.matches(password, user.getPassword())) throw new RuntimeException("Mot de passe incorrect !");
        return jwtService.generateToken(email);
    }

    public String verifyCode(String email, String code) {
        User user = userTree.search(email);
        if (user == null) throw new RuntimeException("Utilisateur non trouvé");
        if (user.getVerificationCode() != null && user.getVerificationCode().equals(code)) {
            user.setVerified(true);
            user.setVerificationCode(null);
            userTree.insert(email, user);
            saveDataToFile();
            return "Compte vérifié !";
        } else {
            throw new RuntimeException("Code incorrect.");
        }
    }

    // 🟢 MÉTHODE RÉ-AJOUTÉE
    public String resetPassword(String email, String newPassword) {
        User user = userTree.search(email);
        if (user == null) throw new RuntimeException("Utilisateur non trouvé !");
        user.setPassword(encoder.encode(newPassword));
        userTree.insert(email, user);
        saveDataToFile(); 
        return "Mot de passe mis à jour !";
    }

    // 🟢 MÉTHODE RÉ-AJOUTÉE
    public List<User> getAllUsers() {
        return userTree.getAll();
    }

    public void deleteUser(String email) {
        boolean deleted = userTree.delete(email);
        if (!deleted) throw new RuntimeException("Utilisateur non trouvé");
        saveDataToFile();
    }

    private void saveDataToFile() {
        try { objectMapper.writeValue(new File(FILE_PATH), userTree.getAll()); } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadDataFromFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                List<User> users = objectMapper.readValue(file, new TypeReference<List<User>>() {});
                for (User user : users) { userTree.insert(user.getEmail(), user); }
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
}