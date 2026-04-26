package com.gitqa.btree;

import com.gitqa.model.CommitInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BTree implements Serializable {

    static final long serialVersionUID = 1L;

    private BTreeNode root;
    private final int t;
    private int size;

    public BTree(int t) {
        this.t    = t;
        this.root = new BTreeNode(t, true);
        this.size = 0;
    }

    public int size()        { return size; }
    public BTreeNode getRoot() { return root; }



    // C — CREATE (insert)


    public void insert(String key, CommitInfo value) {
        BTreeNode r = root;

        if (r.keyCount == 2 * t - 1) {
            BTreeNode newRoot = new BTreeNode(t, false);
            newRoot.children[0] = r;
            root = newRoot;
            splitChild(newRoot, 0, r);
            insertNonFull(newRoot, key, value);
        } else {
            insertNonFull(r, key, value);
        }
        size++;
    }

    private void insertNonFull(BTreeNode node, String key, CommitInfo value) {
        int i = node.keyCount - 1;

        if (node.isLeaf) {

            while (i >= 0 && key.compareTo(node.keys[i]) < 0) {
                node.keys[i + 1]   = node.keys[i];
                node.values[i + 1] = node.values[i];
                i--;
            }
            node.keys[i + 1]   = key;
            node.values[i + 1] = value;
            node.keyCount++;
        } else {

            while (i >= 0 && key.compareTo(node.keys[i]) < 0) {
                i--;
            }
            i++;
            if (node.children[i].keyCount == 2 * t - 1) {
                splitChild(node, i, node.children[i]);
                if (key.compareTo(node.keys[i]) > 0) {
                    i++;
                }
            }
            insertNonFull(node.children[i], key, value);
        }
    }

    private void splitChild(BTreeNode parent, int i, BTreeNode fullChild) {
        BTreeNode rightSibling = new BTreeNode(t, fullChild.isLeaf);
        rightSibling.keyCount = t - 1;


        for (int j = 0; j < t - 1; j++) {
            rightSibling.keys[j]   = fullChild.keys[j + t];
            rightSibling.values[j] = fullChild.values[j + t];
        }
        if (!fullChild.isLeaf) {
            for (int j = 0; j < t; j++) {
                rightSibling.children[j] = fullChild.children[j + t];
            }
        }
        fullChild.keyCount = t - 1;


        for (int j = parent.keyCount; j >= i + 1; j--) {
            parent.children[j + 1] = parent.children[j];
        }
        parent.children[i + 1] = rightSibling;


        for (int j = parent.keyCount - 1; j >= i; j--) {
            parent.keys[j + 1]   = parent.keys[j];
            parent.values[j + 1] = parent.values[j];
        }

        parent.keys[i]   = fullChild.keys[t - 1];
        parent.values[i] = fullChild.values[t - 1];
        parent.keyCount++;
    }



    // R — READ (search, contains, getAll, rangeByDate)


    public CommitInfo search(String key) {
        return searchNode(root, key);
    }

    private CommitInfo searchNode(BTreeNode node, String key) {
        if (node == null) return null;

        int i = 0;
        while (i < node.keyCount && key.compareTo(node.keys[i]) > 0) {
            i++;
        }

        if (i < node.keyCount && key.equals(node.keys[i])) {
            return node.values[i];
        }

        if (node.isLeaf) return null;

        return searchNode(node.children[i], key);
    }

    public boolean contains(String key) {
        return search(key) != null;
    }

    public List<CommitInfo> getAll() {
        List<CommitInfo> all = new ArrayList<>();
        traverseNode(root, all);
        return all;
    }

    private void traverseNode(BTreeNode node, List<CommitInfo> result) {
        for (int i = 0; i < node.keyCount; i++) {
            if (!node.isLeaf) {
                traverseNode(node.children[i], result);
            }
            result.add(node.values[i]);
        }
        if (!node.isLeaf) {
            traverseNode(node.children[node.keyCount], result);
        }
    }

    public List<CommitInfo> rangeByDate(long fromEpoch, long toEpoch) {
        List<CommitInfo> result = new ArrayList<>();
        List<CommitInfo> all = getAll();
        for (int i = 0; i < all.size(); i++) {
            CommitInfo c = all.get(i);
            if (c.getTimestamp() >= fromEpoch && c.getTimestamp() <= toEpoch) {
                result.add(c);
            }
        }
        return result;
    }



    // U — UPDATE


    public boolean update(String key, CommitInfo newValue) {
        return updateNode(root, key, newValue);
    }

    private boolean updateNode(BTreeNode node, String key, CommitInfo newValue) {
        if (node == null) return false;

        int i = 0;
        while (i < node.keyCount && key.compareTo(node.keys[i]) > 0) {
            i++;
        }

        if (i < node.keyCount && key.equals(node.keys[i])) {
            node.values[i] = newValue;
            return true;
        }

        if (node.isLeaf) return false;

        return updateNode(node.children[i], key, newValue);
    }



    // D — DELETE


    public boolean delete(String key) {
        if (root == null || root.keyCount == 0) return false;

        boolean deleted = deleteFromNode(root, key);


        if (root.keyCount == 0 && !root.isLeaf) {
            root = root.children[0];
        }

        if (deleted) size--;
        return deleted;
    }

    private boolean deleteFromNode(BTreeNode node, String key) {
        int i = 0;
        while (i < node.keyCount && key.compareTo(node.keys[i]) > 0) {
            i++;
        }


        if (i < node.keyCount && key.equals(node.keys[i])) {
            if (node.isLeaf) {
                removeFromLeaf(node, i);
                return true;
            } else {
                return deleteFromInternalNode(node, i);
            }
        }


        if (node.isLeaf) return false;
        boolean isLastChild = (i == node.keyCount);


        if (node.children[i].keyCount < t) {
            fillChild(node, i);

            if (isLastChild && i > node.keyCount) {
                i--;
            }
        }

        return deleteFromNode(node.children[i], key);
    }


    private void removeFromLeaf(BTreeNode node, int i) {
        for (int j = i + 1; j < node.keyCount; j++) {
            node.keys[j - 1]   = node.keys[j];
            node.values[j - 1] = node.values[j];
        }
        node.keys[node.keyCount - 1]   = null;
        node.values[node.keyCount - 1] = null;
        node.keyCount--;
    }


    private boolean deleteFromInternalNode(BTreeNode node, int i) {
        String key = node.keys[i];

        if (node.children[i].keyCount >= t) {

            CommitInfo pred = getPredecessor(node, i);
            node.keys[i]   = pred.getSha();
            node.values[i] = pred;
            return deleteFromNode(node.children[i], pred.getSha());

        } else if (node.children[i + 1].keyCount >= t) {

            CommitInfo succ = getSuccessor(node, i);
            node.keys[i]   = succ.getSha();
            node.values[i] = succ;
            return deleteFromNode(node.children[i + 1], succ.getSha());

        } else {

            merge(node, i);
            return deleteFromNode(node.children[i], key);
        }
    }


    private CommitInfo getPredecessor(BTreeNode node, int i) {
        BTreeNode cur = node.children[i];
        while (!cur.isLeaf) {
            cur = cur.children[cur.keyCount];
        }
        return cur.values[cur.keyCount - 1];
    }


    private CommitInfo getSuccessor(BTreeNode node, int i) {
        BTreeNode cur = node.children[i + 1];
        while (!cur.isLeaf) {
            cur = cur.children[0];
        }
        return cur.values[0];
    }


    private void fillChild(BTreeNode node, int i) {
        if (i > 0 && node.children[i - 1].keyCount >= t) {
            borrowFromPrev(node, i);
        } else if (i < node.keyCount && node.children[i + 1].keyCount >= t) {
            borrowFromNext(node, i);
        } else {

            if (i < node.keyCount) {
                merge(node, i);
            } else {
                merge(node, i - 1);
            }
        }
    }


    private void borrowFromPrev(BTreeNode node, int i) {
        BTreeNode child   = node.children[i];
        BTreeNode sibling = node.children[i - 1];


        for (int j = child.keyCount - 1; j >= 0; j--) {
            child.keys[j + 1]   = child.keys[j];
            child.values[j + 1] = child.values[j];
        }
        if (!child.isLeaf) {
            for (int j = child.keyCount; j >= 0; j--) {
                child.children[j + 1] = child.children[j];
            }
        }


        child.keys[0]   = node.keys[i - 1];
        child.values[0] = node.values[i - 1];
        if (!child.isLeaf) {
            child.children[0] = sibling.children[sibling.keyCount];
        }


        node.keys[i - 1]   = sibling.keys[sibling.keyCount - 1];
        node.values[i - 1] = sibling.values[sibling.keyCount - 1];
        sibling.keys[sibling.keyCount - 1]   = null;
        sibling.values[sibling.keyCount - 1] = null;

        child.keyCount++;
        sibling.keyCount--;
    }


    private void borrowFromNext(BTreeNode node, int i) {
        BTreeNode child   = node.children[i];
        BTreeNode sibling = node.children[i + 1];


        child.keys[child.keyCount]   = node.keys[i];
        child.values[child.keyCount] = node.values[i];
        if (!child.isLeaf) {
            child.children[child.keyCount + 1] = sibling.children[0];
        }


        node.keys[i]   = sibling.keys[0];
        node.values[i] = sibling.values[0];


        for (int j = 1; j < sibling.keyCount; j++) {
            sibling.keys[j - 1]   = sibling.keys[j];
            sibling.values[j - 1] = sibling.values[j];
        }
        if (!sibling.isLeaf) {
            for (int j = 1; j <= sibling.keyCount; j++) {
                sibling.children[j - 1] = sibling.children[j];
            }
            sibling.children[sibling.keyCount] = null;
        }
        sibling.keys[sibling.keyCount - 1]   = null;
        sibling.values[sibling.keyCount - 1] = null;

        child.keyCount++;
        sibling.keyCount--;
    }


    private void merge(BTreeNode node, int i) {
        BTreeNode leftChild  = node.children[i];
        BTreeNode rightChild = node.children[i + 1];


        leftChild.keys[t - 1]   = node.keys[i];
        leftChild.values[t - 1] = node.values[i];


        for (int j = 0; j < rightChild.keyCount; j++) {
            leftChild.keys[t + j]   = rightChild.keys[j];
            leftChild.values[t + j] = rightChild.values[j];
        }
        if (!leftChild.isLeaf) {
            for (int j = 0; j <= rightChild.keyCount; j++) {
                leftChild.children[t + j] = rightChild.children[j];
            }
        }


        for (int j = i + 1; j < node.keyCount; j++) {
            node.keys[j - 1]   = node.keys[j];
            node.values[j - 1] = node.values[j];
        }
        for (int j = i + 2; j <= node.keyCount; j++) {
            node.children[j - 1] = node.children[j];
        }
        node.keys[node.keyCount - 1]   = null;
        node.values[node.keyCount - 1] = null;
        node.children[node.keyCount]   = null;

        leftChild.keyCount += rightChild.keyCount + 1;
        node.keyCount--;
    }
}
