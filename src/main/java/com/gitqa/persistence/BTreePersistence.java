package com.gitqa.persistence;
import com.gitqa.btree.BTree;
import java.io.*;

public class BTreePersistence {

    private final String filePath;

    public BTreePersistence(String filePath) {
        this.filePath = filePath;
    }

    public void save(BTree tree) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(filePath)))) {
            oos.writeObject(tree);
        }
        System.out.println("[Saved] Tree written to " + filePath);
    }

    public BTree load() throws IOException, ClassNotFoundException {
        File f = new File(filePath);
        if (!f.exists()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(f)))) {
            return (BTree) ois.readObject();
        }
    }

    public BTree loadOrCreate(int degree) {
        try {
            BTree loaded = load();
            if (loaded != null) {
                System.out.println("[Loaded] Restored tree from disk.");
                return loaded;
            }
        } catch (Exception e) {
            System.err.println("[Warning] Could not load index: " + e.getMessage());
        }
        System.out.println("[Fresh] Starting with empty tree.");
        return new BTree(degree);
    }
}