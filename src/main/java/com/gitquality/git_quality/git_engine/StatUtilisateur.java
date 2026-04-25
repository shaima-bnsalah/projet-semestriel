package com.gitquality.git_quality.git_engine;

import lombok.Data;

@Data
public class StatUtilisateur {
    private int userId;
    private String author;
    private int commitCount;
    private int linesAdded;
    private int linesDeleted;
    private int filesModified;
    private String lastCommitDate;

    public StatUtilisateur(String author) {
        this.author = author;
        this.commitCount = 0;
        this.linesAdded = 0;
        this.linesDeleted = 0;
        this.filesModified = 0;
        this.lastCommitDate = "";
    }

   public void commitAdd(int added, int deleted, int files, String date) {
    this.commitCount++;
    this.linesAdded += added;
    this.linesDeleted += deleted;
    this.filesModified += files;
    
    if (this.lastCommitDate == null || this.lastCommitDate.isEmpty()) {
        this.lastCommitDate = date;
    }
}
}