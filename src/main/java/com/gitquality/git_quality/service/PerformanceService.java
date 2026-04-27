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
    public void init() { loadData(); }

    public MemberPerformance processGitData(String author, int totalCommits, int totalAdded, int totalDeleted, int totalFiles, String lastDate, List<String> messages, String branchName) {
        
        MemberPerformance existingPerf = perfTree.search(author);
        String today = java.time.LocalDate.now().toString();
        
        // ALGORITHME SUR 20
        double commitPts = Math.min(8.0, totalCommits * 0.2);
        double linePts   = Math.min(8.0, totalAdded * 0.002);
        double filePts   = Math.min(4.0, totalFiles * 0.2);
        double penalty   = totalDeleted * 0.001;
        double score20   = Math.max(0, Math.min(20, commitPts + linePts + filePts - penalty));
        double newScore  = Math.round(score20 * 100.0) / 100.0;
        String newRank   = (newScore >= 16) ? "EXPERT" : (newScore >= 10) ? "PRO" : "JUNIOR";

        if (existingPerf == null) {
            // 🟢 CORRECTION ICI : Ajout du 10ème paramètre "branchName"
            existingPerf = new MemberPerformance(author, 0, 0, 0, 0, lastDate, 0.0, "JUNIOR", new ArrayList<>(), branchName);
        }

        int linesDiff = totalAdded - existingPerf.getLinesAdded();
        int delDiff   = totalDeleted - existingPerf.getLinesDeleted();
        double scoreDiff = newScore - existingPerf.getScore();

        existingPerf.setCommitCount(totalCommits);
        existingPerf.setLinesAdded(totalAdded);
        existingPerf.setLinesDeleted(totalDeleted);
        existingPerf.setFilesModified(totalFiles);
        existingPerf.setLastCommitDate(lastDate);
        existingPerf.setScore(newScore);
        existingPerf.setRank(newRank);
        existingPerf.setBranchName(branchName); // Mise à jour de la branche

        updateHistory(existingPerf, today, linesDiff, delDiff, scoreDiff, messages);
        
        perfTree.insert(author, existingPerf);
        saveData();
        return existingPerf;
    }

    private void updateHistory(MemberPerformance perf, String date, int linesDiff, int delDiff, double scoreDiff, List<String> msgs) {
        DailyActivity today = perf.getHistory().stream().filter(a -> a.getDate().equals(date)).findFirst().orElse(null);
        if (today != null) {
            today.setLinesAdded(today.getLinesAdded() + linesDiff);
            today.setLinesDeleted(today.getLinesDeleted() + delDiff);
            today.setDailyScore(Math.round((today.getDailyScore() + scoreDiff) * 100.0) / 100.0);
            today.setCommitMessages(msgs);
        } else {
            perf.getHistory().add(new DailyActivity(date, 1, linesDiff, delDiff, scoreDiff, new ArrayList<>(msgs)));
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