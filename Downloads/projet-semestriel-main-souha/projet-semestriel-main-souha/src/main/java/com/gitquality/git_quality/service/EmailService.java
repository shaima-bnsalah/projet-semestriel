package com.gitquality.git_quality.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async; // 🟢 Ajouté
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async // 🟢 Ajouté : La méthode s'exécute sans faire attendre l'utilisateur
    public void sendCode(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("bensalahshaima2@gmail.com"); // 🟢 Ton email
            message.setTo(toEmail);
            message.setSubject("Code de vérification Git Quality");
            message.setText("Voici votre code : " + code);
            mailSender.send(message);
            System.out.println(">>> Email envoyé en arrière-plan !");
        } catch (Exception e) {
            System.err.println("Échec envoi mail : " + e.getMessage());
        }
    }
}