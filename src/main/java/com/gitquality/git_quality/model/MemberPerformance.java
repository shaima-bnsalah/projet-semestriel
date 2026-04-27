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
    private String author;          // 1
    private int commitCount;        // 2
    private int linesAdded;         // 3
    private int linesDeleted;       // 4
    private int filesModified;      // 5
    private String lastCommitDate;  // 6
    private double score;           // 7
    private String rank;            // 8
    private List<DailyActivity> history = new ArrayList<>(); // 9
    private String branchName;      // 10 🟢
}