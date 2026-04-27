package com.gitquality.git_quality.git_engine;
import java.util.ArrayList;
import java.util.List;

public class StatUtilisateur {
    private String author;
    private int commitCount;
    private int linesAdded;
    private int linesDeleted;
    private int filesModified;
    private String lastCommitDate;
    private String branchName; // 🟢 AJOUT
    private List<String> messages = new ArrayList<>(); // 🟢 AJOUT

    public StatUtilisateur(String author) {
        this.author = author;
        this.commitCount = 0;
        this.linesAdded = 0;
        this.linesDeleted = 0;
        this.filesModified = 0;
        this.lastCommitDate = "";
    }

    // 🟢 AJOUT : Nouvelle version pour supporter tes graphiques
    public void commitAdd(int adds, int del, int files, String date, String msg, String branch) {
        this.commitCount++;
        this.linesAdded += adds;
        this.linesDeleted += del;
        this.filesModified += files;
        // On garde la date la plus récente (la première rencontrée par JGit)
        if (this.lastCommitDate.equals("")) {
            this.lastCommitDate = date;
            this.branchName = branch;
        }
        this.messages.add(msg);
    }

    public String getAuthor() { return author; }
    public int getCommitCount() { return commitCount; }
    public int getLinesAdded() { return linesAdded; }
    public int getLinesDeleted() { return linesDeleted; }
    public int getFilesModified() { return filesModified; }
    public String getLastCommitDate() { return lastCommitDate; }
    public String getBranchName() { return branchName; } // 🟢
    public List<String> getMessages() { return messages; } // 🟢
}