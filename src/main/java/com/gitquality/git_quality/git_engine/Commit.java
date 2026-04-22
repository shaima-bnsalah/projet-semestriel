package com.gitquality.git_quality.git_engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Commit {
    private String hash;
    private int userId;
    private String author;
    private String date;
    private int added;
    private int deleted;
    private int files;
}