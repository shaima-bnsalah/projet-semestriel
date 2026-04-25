package com.gitquality.git_quality.btree;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BTree<K extends Comparable<K>, V> implements Serializable {
    private static final long serialVersionUID = 1L;
    private BTreeNode<K, V> root;
    private final int t;
    private int size;

  public BTree(int t) {
    this.t = t;
    this.root = new BTreeNode<>(t, true);
}
public BTree() {
    this(3); 
}


    public void insert(K key, V value) {
        BTreeNode<K, V> r = root;
        if (r.keyCount == 2 * t - 1) {
            BTreeNode<K, V> s = new BTreeNode<>(t, false);
            root = s;
            s.children[0] = r;
            splitChild(s, 0, r);
            insertNonFull(s, key, value);
        } else {
            insertNonFull(r, key, value);
        }
        size++;
    }

    private void insertNonFull(BTreeNode<K, V> x, K key, V value) {
        int i = x.keyCount - 1;
        if (x.isLeaf) {
            while (i >= 0 && key.compareTo(x.keys[i]) < 0) {
                x.keys[i + 1] = x.keys[i];
                x.values[i + 1] = x.values[i];
                i--;
            }
            x.keys[i + 1] = key;
            x.values[i + 1] = value;
            x.keyCount++;
        } else {
            while (i >= 0 && key.compareTo(x.keys[i]) < 0) i--;
            i++;
            if (x.children[i].keyCount == 2 * t - 1) {
                splitChild(x, i, x.children[i]);
                if (key.compareTo(x.keys[i]) > 0) i++;
            }
            insertNonFull(x.children[i], key, value);
        }
    }

    private void splitChild(BTreeNode<K, V> x, int i, BTreeNode<K, V> y) {
        BTreeNode<K, V> z = new BTreeNode<>(t, y.isLeaf);
        z.keyCount = t - 1;
        for (int j = 0; j < t - 1; j++) {
            z.keys[j] = y.keys[j + t];
            z.values[j] = y.values[j + t];
        }
        if (!y.isLeaf) {
            for (int j = 0; j < t; j++) z.children[j] = y.children[j + t];
        }
        y.keyCount = t - 1;
        for (int j = x.keyCount; j >= i + 1; j--) x.children[j + 1] = x.children[j];
        x.children[i + 1] = z;
        for (int j = x.keyCount - 1; j >= i; j--) {
            x.keys[j + 1] = x.keys[j];
            x.values[j + 1] = x.values[j];
        }
        x.keys[i] = y.keys[t - 1];
        x.values[i] = y.values[t - 1];
        x.keyCount++;
    }

    public V search(K key) {
        return search(root, key);
    }

    private V search(BTreeNode<K, V> x, K key) {
        int i = 0;
        while (i < x.keyCount && key.compareTo(x.keys[i]) > 0) i++;
        if (i < x.keyCount && key.equals(x.keys[i])) return x.values[i];
        if (x.isLeaf) return null;
        return search(x.children[i], key);
    }

    public List<V> getAll() {
        List<V> list = new ArrayList<>();
        traverse(root, list);
        return list;
    }

    private void traverse(BTreeNode<K, V> x, List<V> list) {
        int i;
        for (i = 0; i < x.keyCount; i++) {
            if (!x.isLeaf) traverse(x.children[i], list);
            list.add(x.values[i]);
        }
        if (!x.isLeaf) traverse(x.children[i], list);
    }
}