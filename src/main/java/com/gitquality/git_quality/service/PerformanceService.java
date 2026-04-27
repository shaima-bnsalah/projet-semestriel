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

    private BTree<MemberPerformance> perfTree = new BTree<>(3);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String FILE_PATH = "performance_data.json";

    @PostConstruct
    public void init() { 
        loadData(); 
    }

    /**
     * Traite les données Git et met à jour les statistiques.
     * Correction : On utilise "lastDate" (date du commit) au lieu de la date du jour système.
     */
    public MemberPerformance processGitData(String author, int totalCommits, int totalAdded, int totalDeleted, int totalFiles, String lastDate, List<String> messages, String branchName) {
        
        MemberPerformance existingPerf = perfTree.search(author);
        
        // --- ALGORITHME DE SCORE SUR 20 ---
        double commitPts = Math.min(8.0, totalCommits * 0.2);
        double linePts   = Math.min(8.0, totalAdded * 0.002);
        double filePts   = Math.min(4.0, totalFiles * 0.2);
        double penalty   = totalDeleted * 0.001;
        double score20   = Math.max(0, Math.min(20, commitPts + linePts + filePts - penalty));
        double newScore  = Math.round(score20 * 100.0) / 100.0;
        String newRank   = (newScore >= 16) ? "EXPERT" : (newScore >= 10) ? "PRO" : "JUNIOR";

        // Si le membre n'existe pas encore, on l'initialise
        if (existingPerf == null) {
            existingPerf = new MemberPerformance(author, 0, 0, 0, 0, lastDate, 0.0, "JUNIOR", new ArrayList<>(), branchName);
        }

        // Calcul des différences pour l'historique journalier
        int linesDiff = totalAdded - existingPerf.getLinesAdded();
        int delDiff   = totalDeleted - existingPerf.getLinesDeleted();
        double scoreDiff = newScore - existingPerf.getScore();

        // Mise à jour des stats globales
        existingPerf.setCommitCount(totalCommits);
        existingPerf.setLinesAdded(totalAdded);
        existingPerf.setLinesDeleted(totalDeleted);
        existingPerf.setFilesModified(totalFiles);
        existingPerf.setLastCommitDate(lastDate);
        existingPerf.setScore(newScore);
        existingPerf.setRank(newRank);
        existingPerf.setBranchName(branchName);

        // ✅ CORRECTION ICI : On passe "lastDate" à updateHistory pour enregistrer la date réelle du commit
        updateHistory(existingPerf, lastDate, linesDiff, delDiff, scoreDiff, messages);
        
        perfTree.insert(author, existingPerf);
        saveData();
        return existingPerf;
    }

    /**
     * Met à jour l'historique pour une date spécifique.
     */
    private void updateHistory(MemberPerformance perf, String date, int linesDiff, int delDiff, double scoreDiff, List<String> msgs) {
        // On cherche si une activité existe déjà pour CETTE date précise
        DailyActivity activityForDate = perf.getHistory().stream()
                .filter(a -> a.getDate().equals(date))
                .findFirst()
                .orElse(null);

        if (activityForDate != null) {
            // Si elle existe, on incrémente (pour les commits multiples le même jour)
            activityForDate.setLinesAdded(activityForDate.getLinesAdded() + linesDiff);
            activityForDate.setLinesDeleted(activityForDate.getLinesDeleted() + delDiff);
            activityForDate.setDailyScore(Math.round((activityForDate.getDailyScore() + scoreDiff) * 100.0) / 100.0);
            
            // On ajoute les nouveaux messages s'ils ne sont pas déjà présents
            for (String m : msgs) {
                if (!activityForDate.getCommitMessages().contains(m)) {
                    activityForDate.getCommitMessages().add(m);
                }
            }
        } else {
            // Sinon, on crée une nouvelle entrée pour cette date
            perf.getHistory().add(new DailyActivity(date, 1, linesDiff, delDiff, scoreDiff, new ArrayList<>(msgs)));
        }
    }

    public List<MemberPerformance> getLeaderboard() { 
        return perfTree.getAll(); 
    }

    public void clearAllData() {
        this.perfTree = new BTree<>(3);
        File f = new File(FILE_PATH);
        if (f.exists()) f.delete();
    }

    private void saveData() {
        try { 
            objectMapper.writeValue(new File(FILE_PATH), perfTree.getAll()); 
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde JSON: " + e.getMessage());
        }
    }

    private void loadData() {
        File f = new File(FILE_PATH);
        if (f.exists()) {
            try {
                List<MemberPerformance> list = objectMapper.readValue(f, new TypeReference<List<MemberPerformance>>() {});
                this.perfTree = new BTree<>(3);
                for (MemberPerformance p : list) { 
                    perfTree.insert(p.getAuthor(), p); 
                }
            } catch (IOException e) {
                System.err.println("Erreur chargement JSON: " + e.getMessage());
            }
        }
    }
}