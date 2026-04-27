package com.gitquality.git_quality.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class DailyActivity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String date;
    private int commits;
    private int linesAdded;
    private int linesDeleted;
    private double dailyScore;
    // 🟢 Nouveau : Liste des messages pour le tableau
    private List<String> commitMessages = new ArrayList<>(); 
}