package com.souha;

import com.souha.service.GitAnalyseur;
import com.souha.model.StatUtilisateur;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        System.out.println(" Git Stats Analyser");
        System.out.print("Enter local path or GitHub URL: ");
        String input = scanner.nextLine().trim();
        scanner.close();

        GitAnalyseur analyser = new GitAnalyseur();
        Map<String, StatUtilisateur> stats = analyser.analyser(input);

        System.out.println(" Results ");
        for (StatUtilisateur stat : stats.values()) {

            System.out.println(stat);
        }
        System.out.println("Total authors found: " + stats.size());
    }
}