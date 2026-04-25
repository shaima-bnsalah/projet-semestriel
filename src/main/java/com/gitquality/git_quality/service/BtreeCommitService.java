package com.gitquality.git_quality.service;

// 🟢 Vérifie bien cet import exact :
import com.gitquality.git_quality.btree.BTree;
import com.gitquality.git_quality.model.CommitInfo;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class BtreeCommitService {

    private BTree tree;
    private final BtreePersistence persistence;

    public BtreeCommitService(BtreePersistence persistence) {
        this.persistence = persistence;
        // On charge l'arbre avec un degré de 3
        this.tree = persistence.load(3); 
    }

    public void addCommit(CommitInfo commit) {
        tree.insert(commit.getSha(), commit);
        try {
            persistence.save(tree);
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde BTree Commit: " + e.getMessage());
        }
    }

    public List<CommitInfo> getAllCommits() {
        return tree.getAll();
    }
}