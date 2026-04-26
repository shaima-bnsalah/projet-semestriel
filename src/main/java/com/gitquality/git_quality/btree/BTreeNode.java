package com.gitquality.git_quality.btree;

import java.io.Serializable;

public class BTreeNode<V> implements Serializable {

    static final long serialVersionUID = 1L;

    int            t;
    String[]       keys;
    V[]            values;
    BTreeNode<V>[] children;
    int            keyCount;
    boolean        isLeaf;

    @SuppressWarnings("unchecked")
    public BTreeNode(int t, boolean isLeaf) {
        this.t        = t;
        this.isLeaf   = isLeaf;
        this.keyCount = 0;
        this.keys     = new String[2 * t - 1];
        this.values   = (V[]) new Serializable[2 * t - 1];
        this.children = (BTreeNode<V>[]) new BTreeNode[2 * t];
    }
}
