package com.gitquality.git_quality.btree;
import java.io.Serializable;

public class BTreeNode<K, V> implements Serializable {
    private static final long serialVersionUID = 1L;
    K[] keys;
    V[] values;
    BTreeNode<K, V>[] children;
    int keyCount;
    boolean isLeaf;

    @SuppressWarnings("unchecked")
    public BTreeNode(int t, boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keyCount = 0;
        
        // 🟢 LA CORRECTION EST ICI : On utilise new Comparable au lieu de new Object
        this.keys = (K[]) new Comparable[2 * t - 1]; 
        
        this.values = (V[]) new Object[2 * t - 1];
        this.children = new BTreeNode[2 * t];
    }
}