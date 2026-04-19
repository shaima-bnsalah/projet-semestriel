package com.gitquality.git_quality.btree;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class BTree<K extends Comparable<K>, V> {

    private BTreeNode<K, V> root = new BTreeNode<>();
    private final int t = 2;

    public void insert(K key, V value) {
        BTreeNode<K, V> r = root;
        if (r.keys.size() == 2 * t - 1) {
            BTreeNode<K, V> newRoot = new BTreeNode<>();
            newRoot.isLeaf = false;
            newRoot.children.add(root);
            splitChild(newRoot, 0);
            root = newRoot;
        }
        insertNonFull(root, key, value);
    }

    public V search(K key) {
        return searchNode(root, key);
    }

    public List<V> getAll() {
        List<V> result = new ArrayList<>();
        traverseAll(root, result);
        return result;
    }

    private void insertNonFull(BTreeNode<K, V> node, K key, V value) {
        int i = node.keys.size() - 1;
        if (node.isLeaf) {
            node.keys.add(null);
            node.values.add(null);
            while (i >= 0 && key.compareTo(node.keys.get(i)) < 0) {
                node.keys.set(i + 1, node.keys.get(i));
                node.values.set(i + 1, node.values.get(i));
                i--;
            }
            node.keys.set(i + 1, key);
            node.values.set(i + 1, value);
        } else {
            while (i >= 0 && key.compareTo(node.keys.get(i)) < 0) i--;
            i++;
            if (node.children.get(i).keys.size() == 2 * t - 1) {
                splitChild(node, i);
                if (key.compareTo(node.keys.get(i)) > 0) i++;
            }
            insertNonFull(node.children.get(i), key, value);
        }
    }

    private void splitChild(BTreeNode<K, V> parent, int i) {
        BTreeNode<K, V> full = parent.children.get(i);
        BTreeNode<K, V> newNode = new BTreeNode<>();
        newNode.isLeaf = full.isLeaf;

        parent.keys.add(i, full.keys.get(t - 1));
        parent.values.add(i, full.values.get(t - 1));
        parent.children.add(i + 1, newNode);

        newNode.keys.addAll(full.keys.subList(t, full.keys.size()));
        newNode.values.addAll(full.values.subList(t, full.values.size()));
        full.keys.subList(t - 1, full.keys.size()).clear();
        full.values.subList(t - 1, full.values.size()).clear();

        if (!full.isLeaf) {
            newNode.children.addAll(
                full.children.subList(t, full.children.size())
            );
            full.children.subList(t, full.children.size()).clear();
        }
    }

    private V searchNode(BTreeNode<K, V> node, K key) {
        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) i++;
        if (i < node.keys.size() && key.compareTo(node.keys.get(i)) == 0)
            return node.values.get(i);
        if (node.isLeaf) return null;
        return searchNode(node.children.get(i), key);
    }

    private void traverseAll(BTreeNode<K, V> node, List<V> result) {
        for (int i = 0; i < node.keys.size(); i++) {
            if (!node.isLeaf)
                traverseAll(node.children.get(i), result);
            result.add(node.values.get(i));
        }
        if (!node.isLeaf)
            traverseAll(node.children.get(node.children.size() - 1), result);
    }
}