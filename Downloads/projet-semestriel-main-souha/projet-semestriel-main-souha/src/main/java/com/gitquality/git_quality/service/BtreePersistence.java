package com.gitquality.git_quality.service;

import com.gitquality.git_quality.btree.BTree;

import java.io.*;

public class BTreePersistence<V extends Serializable> {

    private final String filePath;

    public BTreePersistence(String filePath) {
        this.filePath = filePath;
    }


    public void save(BTree<V> tree) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(filePath)))) {
            oos.writeObject(tree);
        }
        System.out.println("[Saved] Tree written to " + filePath);
    }


    @SuppressWarnings("unchecked")
    public BTree<V> load() throws IOException, ClassNotFoundException {
        File f = new File(filePath);
        if (!f.exists()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(f)))) {
            return (BTree<V>) ois.readObject();
        }
    }


    public BTree<V> loadOrCreate(int degree) {
        try {
            BTree<V> loaded = load();
            if (loaded != null) {
                System.out.println("[Loaded] Restored tree from disk.");
                return loaded;
            }
        } catch (Exception e) {
            System.err.println("[Warning] Could not load index: " + e.getMessage());
        }
        System.out.println("[Fresh] Starting with empty tree.");
        return new BTree<>(degree);
    }
}
