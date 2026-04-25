package com.gitquality.git_quality.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class CommitInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sha;
    private String author;
    private String email;
    private long timestamp;
    private String message;
    private int linesAdded;
    private int linesDeleted;
    private List<String> changedFiles;
}