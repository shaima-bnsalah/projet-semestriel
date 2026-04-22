package com.souha.model;

public class StatUtilisateur {

    private int userId;
    private String author;
    private int commitCount;
    private int linesAdded;
    private int linesDeleted;
    private int filesModified;
    private String lastCommitDate;

    public StatUtilisateur(int userId, String author) {
        this.userId = userId;
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
        this.lastCommitDate = date;
    }


    public int getUserId()            { return userId; }
    public String getAuthor()         { return author; }
    public int getCommitCount()       { return commitCount; }
    public int getLinesAdded()        { return linesAdded; }
    public int getLinesDeleted()      { return linesDeleted; }
    public int getFilesModified()     { return filesModified; }
    public String getLastCommitDate() { return lastCommitDate; }
}