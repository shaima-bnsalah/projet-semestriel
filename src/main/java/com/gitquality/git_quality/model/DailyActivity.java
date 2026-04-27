package com.gitquality.git_quality.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class DailyActivity implements Serializable {
    private String date;
    private int commits;
    private int linesAdded;
    private int linesDeleted;
    private double dailyScore;
    private List<String> commitMessages = new ArrayList<>(); 
    private String branchName; // 🟢 AJOUTER CECI
}