package com.souha.model;

public class StatUtilisateur {

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

    public void commitAdd(int adds, int del, int files, String date) {
        this.commitCount++;
        this.linesAdded += adds;
        this.linesDeleted += del;
        this.filesModified += files;
        this.lastCommitDate = date;
    }

    @Override
    public String toString() {
        return "Author         : " + author         + "\n" +
                "Commits        : " + commitCount    + "\n" +
                "Lines added    : " + linesAdded     + "\n" +
                "Lines deleted  : " + linesDeleted   + "\n" +
                "Files modified : " + filesModified  + "\n" +
                "Last commit    : " + lastCommitDate + "\n";
    }

    public String getAuthor()         { return author; }
    public int getCommitCount()       { return commitCount; }
    public int getLinesAdded()        { return linesAdded; }
    public int getLinesDeleted()      { return linesDeleted; }
    public int getFilesModified()     { return filesModified; }
    public String getLastCommitDate() { return lastCommitDate; }
}