package com.gitquality.git_quality.btree;

import java.io.Serializable;

public class BTreeNode<K extends Comparable<K>, V> implements Serializable {

    private static final long serialVersionUID = 1L;

    Object[]            keys;
    Object[]            values;
    BTreeNode<K, V>[]   children;
    int                 keyCount;
    boolean             isLeaf;

    @SuppressWarnings("unchecked")
    public BTreeNode(int t, boolean isLeaf) {
        this.isLeaf   = isLeaf;
        this.keyCount = 0;
        this.keys     = new Object[2 * t - 1];
        this.values   = new Object[2 * t - 1];
        this.children = (BTreeNode<K, V>[]) new BTreeNode[2 * t];
    }

    @SuppressWarnings("unchecked")
    K key(int i) { return (K) keys[i]; }

    @SuppressWarnings("unchecked")
    V value(int i) { return (V) values[i]; }
}