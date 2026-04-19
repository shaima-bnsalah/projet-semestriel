package com.gitqa.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class CommitInfo implements Serializable {
    private final String sha;
    private final String author;
    private final String email;
    private final long   timestamp;
    private final String message;
    private final int    linesAdded;
    private final int    linesDeleted;
    private final List<String> changedFiles;

    public CommitInfo(String sha, String author, String email, long timestamp,
                      String message, int linesAdded, int linesDeleted,
                      List<String> changedFiles) {
        this.sha          = sha;
        this.author       = author;
        this.email        = email;
        this.timestamp    = timestamp;
        this.message      = message;
        this.linesAdded   = linesAdded;
        this.linesDeleted = linesDeleted;
        this.changedFiles = Collections.unmodifiableList(changedFiles);
    }
    public String getsha()           { return sha; }
    public String getAuthor()        { return author; }
    public String getEmail()         { return email; }
    public long   getTimestamp()     { return timestamp; }
    public String getMessage()       { return message; }
    public int    getLinesAdded()    { return linesAdded; }
    public int    getLinesDeleted()  { return linesDeleted; }
    public List<String> getChangedFiles() { return changedFiles; }
}
