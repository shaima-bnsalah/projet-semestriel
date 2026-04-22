package com.gitquality.git_quality.git_engine;

import java.util.ArrayList;
import java.util.List;

public class GitHistory {
    private List<Commit> commits = new ArrayList<>();

    public void addCommit(Commit commit) { commits.add(commit); }
    public List<Commit> getCommits() { return commits; }
    public int getTotalCommits() { return commits.size(); }
}