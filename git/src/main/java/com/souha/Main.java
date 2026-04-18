package com.souha;
import com.souha.model.GitHistory;
import com.souha.model.StatUtilisateur;
import com.souha.service.GitAnalyseur;

import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter local repo path");
        String path = scanner.nextLine().trim();
        scanner.close();

        GitAnalyseur analyser = new GitAnalyseur();
        Map<Integer, StatUtilisateur> stats = analyser.analyser(path);

        System.out.println("Stats by User ID ");
        for (StatUtilisateur stat : stats.values()) {
            System.out.println(stat);
        }

        GitHistory history = analyser.getHistory();
        System.out.println("\n" + history);
    }
}