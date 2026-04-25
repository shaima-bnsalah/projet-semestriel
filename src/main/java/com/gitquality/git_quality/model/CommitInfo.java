package com.gitquality.git_quality.model;

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
    public String getSha()           { return sha; }
    public String getAuthor()        { return author; }
    public String getEmail()         { return email; }
    public long   getTimestamp()     { return timestamp; }
    public String getMessage()       { return message; }
    public int    getLinesAdded()    { return linesAdded; }
    public int    getLinesDeleted()  { return linesDeleted; }
    public List<String> getChangedFiles() { return changedFiles; }
    public String toJson() {
        return "{"
                + "\"sha\":\"" + sha + "\","
                + "\"author\":\"" + author + "\","
                + "\"email\":\"" + email + "\","
                + "\"timestamp\":" + timestamp + ","
                + "\"message\":\"" + message + "\","
                + "\"linesAdded\":" + linesAdded + ","
                + "\"linesDeleted\":" + linesDeleted
                + "}";
    }
    public static CommitInfo fromJson(String json) {
        return new CommitInfo(
                extractField(json, "sha"),
                extractField(json, "author"),
                extractField(json, "email"),
                Long.parseLong(extractField(json, "timestamp")),
                extractField(json, "message"),
                Integer.parseInt(extractField(json, "linesAdded")),
                Integer.parseInt(extractField(json, "linesDeleted")),
                Collections.emptyList()
        );
    }

    private static String extractField(String json, String field) {
        String search = "\"" + field + "\":";
        int start = json.indexOf(search) + search.length();
        // remove quotes if string value
        if (json.charAt(start) == '"') {
            start++;
            int end = json.indexOf('"', start);
            return json.substring(start, end);
        }
        // numeric value
        int end = json.indexOf(',', start);
        if (end == -1) end = json.indexOf('}', start);
        return json.substring(start, end).trim();
    }
}
