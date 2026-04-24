package com.gitqa;

import com.gitqa.btree.BTree;
import com.gitqa.model.CommitInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TEST {
    public static void main(String[] args) {
        System.out.println("BTree Test \n");

        BTree tree = new BTree(50);
        int N = 10_000;
        Random rnd = new Random(42);
        List<String> sampleKeys = new ArrayList<>();

        // 1: Insert speed
        long start = System.nanoTime();
        for (int i = 0; i < N; i++) {
            String sha = String.format("%016x%016x%08x",
                    rnd.nextLong() & Long.MAX_VALUE,
                    rnd.nextLong() & Long.MAX_VALUE,
                    rnd.nextInt() & 0x7fffffff);
            CommitInfo commit = new CommitInfo(
                    sha, "Author" + i, "author@test.com",
                    System.currentTimeMillis(), "Test commit " + i,
                    10, 5, List.of("File.java"));
            tree.insert(sha, commit);

            if (sampleKeys.size() < 5 && i % (N / 5) == 0) {
                sampleKeys.add(sha);
            }
        }
        long insertMs = (System.nanoTime() - start) / 1_000_000;
        System.out.printf("Inserted %,d commits in %d ms%n", N, insertMs);

        // 2: Search speed
        start = System.nanoTime();
        for (String key : sampleKeys) {
            CommitInfo found = tree.search(key);
            if (found == null) {
                throw new AssertionError("Key not found: " + key);
            }
        }
        long searchMs = (System.nanoTime() - start) / 1_000_000;
        System.out.printf("Searched %d commits in %d ms%n", sampleKeys.size(), searchMs);

        // 3: Sorted order verification
        List<CommitInfo> all = tree.getAll();
        boolean isSorted = true;
        for (int i = 1; i < all.size(); i++) {
            if (all.get(i).getsha().compareTo(all.get(i - 1).getsha()) < 0) {
                isSorted = false;
                break;
            }
        }
        System.out.println("In-order traversal is sorted: " + isSorted);
        System.out.println("Total commits in tree: " + tree.size());

        // 4: Tree height check
        double maxHeight = Math.log(N) / Math.log(50);
        System.out.printf("Maximum expected tree height: %.1f levels%n", maxHeight);

        // 5: Update
        String updateKey = sampleKeys.get(0);
        CommitInfo updated = new CommitInfo(
                updateKey, "UpdatedAuthor", "updated@test.com",
                System.currentTimeMillis(), "Updated message",
                99, 99, List.of("Updated.java"));
        boolean updateOk = tree.update(updateKey, updated);
        CommitInfo afterUpdate = tree.search(updateKey);
        System.out.println("Update returned: " + updateOk
                + ", new author: " + afterUpdate.getAuthor());

        // 6: Delete
        int sizeBefore = tree.size();
        boolean deleteOk = tree.delete(updateKey);
        int sizeAfter = tree.size();
        System.out.println("Delete returned: " + deleteOk
                + ", size " + sizeBefore + " -> " + sizeAfter
                + ", contains key after delete: " + tree.contains(updateKey));
    }
}