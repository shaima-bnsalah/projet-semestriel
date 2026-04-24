package com.gitqa.persistence;
import com.gitqa.btree.BTree;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class BTreePersistence {

    private static final int BUFFER_SIZE = 64 * 1024;

    private final String filePath;

    public BTreePersistence(String filePath) {
        this.filePath = filePath;
    }

    public void save(BTree tree) throws IOException {
        Path target = Paths.get(filePath);
        Path tmp    = Paths.get(filePath + ".tmp");

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(
                        Files.newOutputStream(tmp), BUFFER_SIZE))) {
            oos.writeObject(tree);
        }

        try {
            Files.move(tmp, target,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException atomicFail) {
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        }
        System.out.println("[Saved] Tree written to " + filePath);
    }

    public BTree load() throws IOException, ClassNotFoundException {
        Path p = Paths.get(filePath);
        if (!Files.exists(p)) return null;
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(
                        Files.newInputStream(p), BUFFER_SIZE))) {
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