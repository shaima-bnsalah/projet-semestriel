package com.gitqa.btree;

import com.gitqa.model.CommitInfo;
import java.io.Serializable;

public class BTreeNode implements Serializable {

    int         t;
    String[]    keys;
    CommitInfo[] values;
    BTreeNode[] children;
    int         keyCount;
    boolean     isLeaf;

    public BTreeNode(int t, boolean isLeaf) {
        this.t        = t;
        this.isLeaf   = isLeaf;
        this.keyCount = 0;

        this.keys     = new String[2 * t - 1];
        this.values   = new CommitInfo[2 * t - 1];

        this.children = new BTreeNode[2 * t];
    }
}
