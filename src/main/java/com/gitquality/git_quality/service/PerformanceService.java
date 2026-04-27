package com.gitquality.git_quality.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitquality.git_quality.btree.BTree;
import com.gitquality.git_quality.git_engine.StatUtilisateur;
import com.gitquality.git_quality.model.MemberPerformance;
import com.gitquality.git_quality.model.DailyActivity; 
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class PerformanceService {

    private BTree<MemberPerformance> perfTree = new BTree<>(3);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String FILE_PATH = "performance_data.json";

    @PostConstruct
    public void init() { loadData(); }

    public MemberPerformance processGitData(String author, int totalCommits, int totalAdded, int totalDeleted, int totalFiles, Map<String, StatUtilisateur.DailyInfo> allDailyStats) {
        
        MemberPerformance existingPerf = perfTree.search(author);
        
        // --- ALGORITHME DE SCORE ÉQUILIBRÉ (Sur 20) ---
        
        // 1. Points Commits : 0.5 pt par commit (Max 8 pts -> atteint à 16 commits)
        double commitPts = Math.min(8.0, totalCommits * 0.5);
        
        // 2. Points Lignes : 1 pt pour 100 lignes (Max 8 pts -> atteint à 800 lignes)
        // On utilise 0.01 pour que 100 lignes = 1 point
        double linePts = Math.min(8.0, totalAdded * 0.01);
        
        // 3. Points Fichiers : 0.5 pt par fichier (Max 4 pts -> atteint à 8 fichiers)
        double filePts = Math.min(4.0, totalFiles * 0.5);
        
        // 4. Pénalité : On pénalise très légèrement les suppressions massives
        double penalty = totalDeleted * 0.0005;

        double scoreCalculated = commitPts + linePts + filePts - penalty;
        double score20 = Math.max(0, Math.min(20, scoreCalculated));
        
        double newScore = Math.round(score20 * 100.0) / 100.0;
        
        // Rangs ajustés
        String newRank = (newScore >= 15) ? "EXPERT" : (newScore >= 10) ? "PRO" : "JUNIOR";

        if (existingPerf == null) {
            String lastDate = allDailyStats.keySet().stream().max(String::compareTo).orElse("");
            existingPerf = new MemberPerformance(); 
            existingPerf.setAuthor(author);
            existingPerf.setLastCommitDate(lastDate);
            existingPerf.setHistory(new ArrayList<>());
        }

        existingPerf.setCommitCount(totalCommits);
        existingPerf.setLinesAdded(totalAdded);
        existingPerf.setLinesDeleted(totalDeleted);
        existingPerf.setFilesModified(totalFiles);
        existingPerf.setScore(newScore);
        existingPerf.setRank(newRank);

        for (Map.Entry<String, StatUtilisateur.DailyInfo> entry : allDailyStats.entrySet()) {
            updateHistory(existingPerf, entry.getKey(), entry.getValue().adds, entry.getValue().dels, entry.getValue().msgs);
        }
        
        perfTree.insert(author, existingPerf);
        saveData();
        return existingPerf;
    }

    private void updateHistory(MemberPerformance perf, String date, int adds, int dels, List<String> msgs) {
        DailyActivity activity = perf.getHistory().stream()
                .filter(a -> a.getDate().equals(date))
                .findFirst()
                .orElse(null);

        if (activity != null) {
            activity.setLinesAdded(adds);
            activity.setLinesDeleted(dels);
            activity.setCommitMessages(new ArrayList<>(msgs));
            activity.setCommits(msgs.size());
        } else {
            DailyActivity newAct = new DailyActivity();
            newAct.setDate(date);
            newAct.setCommits(msgs.size());
            newAct.setLinesAdded(adds);
            newAct.setLinesDeleted(dels);
            newAct.setCommitMessages(new ArrayList<>(msgs));
            newAct.setDailyScore(0.0); 
            perf.getHistory().add(newAct);
        }
    }

    public List<MemberPerformance> getLeaderboard() { return perfTree.getAll(); }

    public void clearAllData() {
        this.perfTree = new BTree<>(3);
        File f = new File(FILE_PATH);
        if (f.exists()) f.delete();
    }

    private void saveData() {
        try { objectMapper.writeValue(new File(FILE_PATH), perfTree.getAll()); } catch (IOException e) {}
    }

    private void loadData() {
        File f = new File(FILE_PATH);
        if (f.exists()) {
            try {
                List<MemberPerformance> list = objectMapper.readValue(f, new TypeReference<List<MemberPerformance>>() {});
                this.perfTree = new BTree<>(3);
                for (MemberPerformance p : list) { perfTree.insert(p.getAuthor(), p); }
            } catch (IOException e) {}
        }
    }
}