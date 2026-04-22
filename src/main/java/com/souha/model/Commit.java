package com.souha.model;

public class Commit {

    private String hash;
    private int userId;
    private String author;
    private String date;
    private int linesAdded;
    private int linesDeleted;
    private int filesModified;

    public Commit(String hash, int userId, String author, String date,
                  int linesAdded, int linesDeleted, int filesModified) {
        this.hash = hash;
        this.userId = userId;
        this.author = author;
        this.date = date;
        this.linesAdded = linesAdded;
        this.linesDeleted = linesDeleted;
        this.filesModified = filesModified;
    }


    public String getHash()        { return hash; }
    public int getUserId()         { return userId; }
    public String getAuthor()      { return author; }
    public String getDate()        { return date; }
    public int getLinesAdded()     { return linesAdded; }
    public int getLinesDeleted()   { return linesDeleted; }
    public int getFilesModified()  { return filesModified; }
}