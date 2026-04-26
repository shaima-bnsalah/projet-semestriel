package com.gitquality.git_quality.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable; 
import java.util.ArrayList;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class MemberPerformance implements Serializable { 
    private static final long serialVersionUID = 1L;
    private String author;
    private int commitCount;
    private int linesAdded;
    private int linesDeleted;
    private int filesModified;
    private String lastCommitDate;
    private double score;
    private String rank;
    private List<DailyActivity> history = new ArrayList<>();
}