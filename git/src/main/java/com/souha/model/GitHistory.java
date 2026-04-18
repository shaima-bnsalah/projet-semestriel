package com.souha.model;

import java.util.ArrayList;
import java.util.List;

public class GitHistory {

    private List<Commit> commits = new ArrayList<>();

    public void addCommit(Commit commit) {
        commits.add(commit);
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public List<Commit> getCommitsByUserId(int userId) {
        List<Commit> result = new ArrayList<>();
        for (Commit c : commits) {
            if (c.getUserId() == userId) {
                result.add(c);
            }
        }
        return result;
    }

    public List<Commit> getCommitsByDate(String date) {
        List<Commit> result = new ArrayList<>();
        for (Commit c : commits) {
            if (c.getDate().equals(date)) {
                result.add(c);
            }
        }
        return result;
    }

    public Commit getCommitByHash(String hash) {
        for (Commit c : commits) {
            if (c.getHash().equals(hash)) {
                return c;
            }
        }
        return null;
    }

    public int getTotalCommits() {
        return commits.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Git History (").append(commits.size()).append(" commits) ===\n");
        for (Commit c : commits) {
            sb.append("-----------------------------\n");
            sb.append(c.toString());
        }
        return sb.toString();
    }
}