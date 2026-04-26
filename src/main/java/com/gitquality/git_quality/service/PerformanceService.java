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

    public MemberPerformance processGitData(String author, int totalCommits, int totalAdded, int totalDeleted, int totalFiles, String lastDate) {
        
        MemberPerformance existingPerf = perfTree.search(author);
        String today = java.time.LocalDate.now().toString();
        
        double commitPoints = Math.min(8.0, totalCommits * 0.2);
        double linePoints   = Math.min(8.0, totalAdded * 0.002);
        double filePoints   = Math.min(4.0, totalFiles * 0.2);
        double penalty      = totalDeleted * 0.001;

        double finalNote = commitPoints + linePoints + filePoints - penalty;
        if (finalNote < 0) finalNote = 0;
        if (finalNote > 20) finalNote = 20;

        double newScore = Math.round(finalNote * 100.0) / 100.0;
        String newRank = (newScore >= 16) ? "EXPERT" : (newScore >= 10) ? "PRO" : "JUNIOR";

        if (existingPerf != null) {
            double scoreDifference = newScore - existingPerf.getScore();
            int linesDifference = totalAdded - existingPerf.getLinesAdded();

            existingPerf.setCommitCount(totalCommits);
            existingPerf.setLinesAdded(totalAdded);
            existingPerf.setLinesDeleted(totalDeleted);
            existingPerf.setFilesModified(totalFiles);
            existingPerf.setLastCommitDate(lastDate);
            existingPerf.setScore(newScore);
            existingPerf.setRank(newRank);

            if (scoreDifference > 0 || linesDifference > 0) {
                updateHistory(existingPerf, today, linesDifference, scoreDifference);
            }
            
            saveData(); 
            return existingPerf;

        } else {
            MemberPerformance newPerf = new MemberPerformance(author, totalCommits, totalAdded, totalDeleted, totalFiles, lastDate, newScore, newRank, new ArrayList<>());
            updateHistory(newPerf, today, totalAdded, newScore);
            perfTree.insert(author, newPerf);
            saveData(); 
            return newPerf;
        }
    }

    public void clearAllData() {
        this.perfTree = new BTree<>(3);
        File file = new File(FILE_PATH);
        if (file.exists()) file.delete();
    }

    private void updateHistory(MemberPerformance perf, String date, int linesDiff, double scoreDiff) {
        DailyActivity todayActivity = perf.getHistory().stream()
                .filter(a -> a.getDate().equals(date))
                .findFirst().orElse(null);

        if (todayActivity != null) {
            todayActivity.setLinesAdded(todayActivity.getLinesAdded() + linesDiff);
            todayActivity.setDailyScore(Math.round((todayActivity.getDailyScore() + scoreDiff) * 100.0) / 100.0);
        } else {
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
                this.perfTree = new BTree<>(3); 
                for (MemberPerformance p : list) {
                    perfTree.insert(p.getAuthor(), p);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}