package com.gitquality.git_quality.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyActivity {
    private String date;
    private int pushes;
    private int linesAdded;
    private double dailyScore;
}