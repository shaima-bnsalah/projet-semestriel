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
import java.util.UUID;

@Service
public class AuthService {

    private final BTree<String, User> userTree = new BTree<>();
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String FILE_PATH = "users.json";

    public AuthService(JwtService jwtService, BCryptPasswordEncoder encoder) {
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    @PostConstruct
    public void init() {
        loadDataFromFile();
    }

    public String register(String username, String email, String password) {
        if (userTree.search(email) != null) {
            throw new RuntimeException("Email déjà utilisé !");
        }
        User user = new User(UUID.randomUUID().toString(), username, email, encoder.encode(password));
        userTree.insert(email, user);
        
        saveDataToFile(); 
        return jwtService.generateToken(email);
    }

    public String login(String email, String password) {
        User user = userTree.search(email);
        if (user == null) throw new RuntimeException("Utilisateur non trouvé !");
        if (!encoder.matches(password, user.getPassword())) throw new RuntimeException("Mot de passe incorrect !");
        return jwtService.generateToken(email);
    }

    public String resetPassword(String email, String newPassword) {
        User user = userTree.search(email);
        if (user == null) throw new RuntimeException("Utilisateur non trouvé !");
        user.setPassword(encoder.encode(newPassword));
        userTree.insert(email, user);
        
        saveDataToFile(); 
        return "Mot de passe mis à jour avec succès !";
    }

    public List<User> getAllUsers() {
        return userTree.getAll();
    }

    private void saveDataToFile() {
        try {
            List<User> users = userTree.getAll();
            objectMapper.writeValue(new File(FILE_PATH), users);
            System.out.println(">>> Données sauvegardées dans " + FILE_PATH);
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    private void loadDataFromFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                List<User> users = objectMapper.readValue(file, new TypeReference<List<User>>() {});
                for (User user : users) {
                    userTree.insert(user.getEmail(), user);
                }
                System.out.println(">>> " + users.size() + " utilisateurs chargés depuis le fichier.");
            } catch (IOException e) {
                System.out.println("Erreur lors du chargement : " + e.getMessage());
            }
        }
    }
}