package com.gitquality.git_quality.git_engine; // 🟢 On adapte juste l'adresse

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

    // Garde la logique de Souha pour filtrer par ID utilisateur
    public List<Commit> getCommitsByUserId(int userId) {
        List<Commit> result = new ArrayList<>();
        for (Commit c : commits) {
            if (c.getUserId() == userId) {
                result.add(c);
            }
        }
        return result;
    }

    // Garde la logique de Souha pour filtrer par date
    public List<Commit> getCommitsByDate(String date) {
        List<Commit> result = new ArrayList<>();
        for (Commit c : commits) {
            if (c.getDate().equals(date)) {
                result.add(c);
            }
        }
        return result;
    }

    // Garde la logique de Souha pour chercher un Hash
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
        sb.append(" Git History (").append(commits.size()).append(" commits) \n");
        for (Commit c : commits) {
            sb.append("-----------------------------\n");
            sb.append(c.toString());
        }
        return sb.toString();
    }
}