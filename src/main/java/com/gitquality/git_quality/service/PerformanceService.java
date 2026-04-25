package com.gitquality.git_quality.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitquality.git_quality.btree.BTree;
import com.gitquality.git_quality.model.MemberPerformance;
import com.gitquality.git_quality.model.DailyActivity; 
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PerformanceService {

    private BTree<String, MemberPerformance> perfTree = new BTree<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String FILE_PATH = "performance_data.json";

    @PostConstruct
    public void init() {
        loadData();
    }

    public MemberPerformance processGitData(String author, int totalCommits, int totalAdded, int totalDeleted, int totalFiles, String lastDate) {
        
        MemberPerformance existingPerf = perfTree.search(author);
        String today = java.time.LocalDate.now().toString();
        
        // On calcule le score basé sur les chiffres reçus (qui sont des totaux)
        double newScore = (totalCommits * 10.0) + (totalFiles * 5.0) + (totalAdded * 0.1) - (totalDeleted * 0.05);
        String newRank = (newScore > 500) ? "EXPERT" : (newScore > 200) ? "PRO" : "JUNIOR";

        if (existingPerf != null) {
            // 🟢 CALCUL DE LA DIFFÉRENCE POUR L'HISTORIQUE DU JOUR
            // Si le score a augmenté depuis la dernière analyse, on enregistre la progression
            double scoreDifference = newScore - existingPerf.getScore();
            int linesDifference = totalAdded - existingPerf.getLinesAdded();

            // 🟢 MISE À JOUR : On REMPLACE les anciennes valeurs par les nouvelles (on n'ajoute plus)
            existingPerf.setCommitCount(totalCommits);
            existingPerf.setLinesAdded(totalAdded);
            existingPerf.setLinesDeleted(totalDeleted);
            existingPerf.setFilesModified(totalFiles);
            existingPerf.setLastCommitDate(lastDate);
            existingPerf.setScore(newScore);
            existingPerf.setRank(newRank);

            // On ne met à jour l'historique que s'il y a eu un changement réel
            if (scoreDifference > 0 || linesDifference > 0) {
                updateHistory(existingPerf, today, linesDifference, scoreDifference);
            }
            
            saveData(); 
            return existingPerf;

        } else {
            // 🔵 PREMIÈRE FOIS : Création normale
            MemberPerformance newPerf = new MemberPerformance(author, totalCommits, totalAdded, totalDeleted, totalFiles, lastDate, newScore, newRank, new ArrayList<>());
            updateHistory(newPerf, today, totalAdded, newScore);
            perfTree.insert(author, newPerf);
            saveData(); 
            return newPerf;
        }
    }

    private void updateHistory(MemberPerformance perf, String date, int linesDiff, double scoreDiff) {
        DailyActivity todayActivity = perf.getHistory().stream()
                .filter(a -> a.getDate().equals(date))
                .findFirst().orElse(null);

        if (todayActivity != null) {
            // On ajoute la différence au jour actuel
            todayActivity.setLinesAdded(todayActivity.getLinesAdded() + linesDiff);
            todayActivity.setDailyScore(todayActivity.getDailyScore() + scoreDiff);
        } else {
            // Nouveau jour
            perf.getHistory().add(new DailyActivity(date, 1, linesDiff, scoreDiff));
        }
    }

    public List<MemberPerformance> getLeaderboard() {
        return perfTree.getAll();
    }

    private void saveData() {
        try {
            objectMapper.writeValue(new File(FILE_PATH), perfTree.getAll());
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde : " + e.getMessage());
        }
    }

    private void loadData() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                List<MemberPerformance> list = objectMapper.readValue(file, new TypeReference<List<MemberPerformance>>() {});
                this.perfTree = new BTree<>(); 
                for (MemberPerformance p : list) {
                    perfTree.insert(p.getAuthor(), p);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}