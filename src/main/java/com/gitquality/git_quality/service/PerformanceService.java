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

    public MemberPerformance processGitData(String author, int newCommits, int newAdded, int newDeleted, int newFiles, String lastDate) {
        
        MemberPerformance existingPerf = perfTree.search(author);
        String today = java.time.LocalDate.now().toString();
        
        if (existingPerf != null) {
            existingPerf.setCommitCount(existingPerf.getCommitCount() + newCommits);
            existingPerf.setLinesAdded(existingPerf.getLinesAdded() + newAdded);
            existingPerf.setLinesDeleted(existingPerf.getLinesDeleted() + newDeleted);
            existingPerf.setFilesModified(existingPerf.getFilesModified() + newFiles);
            existingPerf.setLastCommitDate(lastDate);

            double totalScore = (existingPerf.getCommitCount() * 10.0) 
                              + (existingPerf.getFilesModified() * 5.0)
                              + (existingPerf.getLinesAdded() * 0.1) 
                              - (existingPerf.getLinesDeleted() * 0.05);
            
            existingPerf.setScore(totalScore);
            existingPerf.setRank((totalScore > 500) ? "EXPERT" : (totalScore > 200) ? "PRO" : "JUNIOR");

            updateHistory(existingPerf, today, newCommits, newAdded);
            
            saveData(); 
            return existingPerf;

        } else {
            MemberPerformance newPerf = new MemberPerformance(author, newCommits, newAdded, newDeleted, newFiles, lastDate, 0.0, "JUNIOR", new ArrayList<>());
            
            double totalScore = (newCommits * 10.0) + (newFiles * 5.0) + (newAdded * 0.1) - (newDeleted * 0.05);
            newPerf.setScore(totalScore);
            newPerf.setRank((totalScore > 500) ? "EXPERT" : (totalScore > 200) ? "PRO" : "JUNIOR");
            
            updateHistory(newPerf, today, newCommits, newAdded);

            perfTree.insert(author, newPerf);
            
            saveData(); 
            return newPerf;
        }
    }

    private void updateHistory(MemberPerformance perf, String date, int commits, int added) {
        DailyActivity todayActivity = perf.getHistory().stream()
                .filter(a -> a.getDate().equals(date))
                .findFirst().orElse(null);

        if (todayActivity != null) {
            todayActivity.setLinesAdded(todayActivity.getLinesAdded() + added);
            todayActivity.setDailyScore(todayActivity.getDailyScore() + (commits * 5.0 + added * 0.1));
        } else {
            perf.getHistory().add(new DailyActivity(date, 1, added, (commits * 5.0 + added * 0.1)));
        }
    }

    public List<MemberPerformance> getLeaderboard() {
        return perfTree.getAll();
    }

    private void saveData() {
        try {
            objectMapper.writeValue(new File(FILE_PATH), perfTree.getAll());
        } catch (IOException e) {
            System.out.println("Erreur de sauvegarde : " + e.getMessage());
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