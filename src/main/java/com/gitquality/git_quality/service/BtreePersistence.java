package com.gitquality.git_quality.service;

// 🟢 Vérifie bien cet import exact :
import com.gitquality.git_quality.btree.BTree; 
import org.springframework.stereotype.Component;
import java.io.*;

@Component
public class BtreePersistence {
    private static final String FILE_PATH = "commits_index.dat";

    public void save(BTree tree) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(FILE_PATH)))) {
            oos.writeObject(tree);
        }
    }

    public BTree load(int degree) {
        File f = new File(FILE_PATH);
        if (!f.exists()) return new BTree(degree);
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(f)))) {
            return (BTree) ois.readObject();
        } catch (Exception e) {
            return new BTree(degree);
        }
    }
}