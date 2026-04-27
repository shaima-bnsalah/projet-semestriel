package com.gitquality.git_quality.git_engine;
import java.util.*;

public class StatUtilisateur {
    private String author;
    private int commitCount;
    private int linesAdded;
    private int linesDeleted;
    private int filesModified;
    private String lastCommitDate;
    
    private Map<String, DailyInfo> dailyStats = new HashMap<>();

    public StatUtilisateur(String author) { this.author = author; }

    public void commitAdd(int adds, int del, int files, String date, String msg, String branch) {
        this.commitCount++;
        this.linesAdded += adds;
        this.linesDeleted += del;
        this.filesModified += files;
        this.lastCommitDate = date;

        // 🟢 Groupement par DATE uniquement
        dailyStats.putIfAbsent(date, new DailyInfo());
        DailyInfo info = dailyStats.get(date);
        info.adds += adds;
        info.dels += del;
        info.msgs.add(msg);
    }

    // Getters standards
    public String getAuthor() { return author; }
    public int getCommitCount() { return commitCount; }
    public int getLinesAdded() { return linesAdded; }
    public int getLinesDeleted() { return linesDeleted; }
    public int getFilesModified() { return filesModified; }
    public String getLastCommitDate() { return lastCommitDate; }
    public Map<String, DailyInfo> getDailyStats() { return dailyStats; }

    public static class DailyInfo {
        public int adds = 0;
        public int dels = 0;
        public List<String> msgs = new ArrayList<>();
    }
}