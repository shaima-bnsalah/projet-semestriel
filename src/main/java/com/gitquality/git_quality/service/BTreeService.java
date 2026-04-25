package com.gitquality.git_quality.service;

import com.gitquality.git_quality.btree.BTree;
import com.gitquality.git_quality.model.CommitInfo;

import java.io.IOException;
import java.util.List;

public class BTreeService {

    private final BTree<String, CommitInfo> tree;
    private final BTreePersistence persistence;

    public BTreeService(String filePath, int degree) {
        this.persistence = new BTreePersistence(filePath);
        this.tree = persistence.loadOrCreate(degree);
    }

    // Backend sends JSON -> store in BTree
    public void insertFromJson(String json) {
        CommitInfo commit = CommitInfo.fromJson(json);
        tree.insert(commit.getSha(), commit);
        try {
            persistence.save(tree);
        } catch (IOException e) {
            System.err.println("[Error] Could not save: " + e.getMessage());
        }
    }

    // Lookup commit -> return JSON
    public String searchAsJson(String sha) {
        CommitInfo result = tree.search(sha);
        if (result == null) return "{\"error\":\"not found\"}";
        return result.toJson();
    }

    // Return all commits as a JSON array
    public String getAllAsJson() {
        List<CommitInfo> all = tree.getAll();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < all.size(); i++) {
            sb.append(all.get(i).toJson());
            if (i < all.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}