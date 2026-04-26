package com.gitquality.git_quality.btree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BTree<V extends Serializable> implements Serializable {

    static final long serialVersionUID = 1L;

    private BTreeNode<V> root;
    private final int t;
    private int size;

    // Default constructor (degree 3) — used by PerformanceService
    public BTree() {
        this(3);
    }

    public BTree(int t) {
        this.t    = t;
        this.root = new BTreeNode<>(t, true);
        this.size = 0;
    }

    public int size()            { return size; }
    public BTreeNode<V> getRoot() { return root; }



    // C — CREATE (insert)


    public void insert(String key, V value) {
        BTreeNode<V> r = root;

        if (r.keyCount == 2 * t - 1) {
            BTreeNode<V> newRoot = new BTreeNode<>(t, false);
            newRoot.children[0] = r;
            root = newRoot;
            splitChild(newRoot, 0, r);
            insertNonFull(newRoot, key, value);
        } else {
            insertNonFull(r, key, value);
        }
        size++;
    }

    private void insertNonFull(BTreeNode<V> node, String key, V value) {
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

    private void splitChild(BTreeNode<V> parent, int i, BTreeNode<V> fullChild) {
        BTreeNode<V> rightSibling = new BTreeNode<>(t, fullChild.isLeaf);
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



    // R — READ (search, contains, getAll)


    public V search(String key) {
        return searchNode(root, key);
    }

    private V searchNode(BTreeNode<V> node, String key) {
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

    public List<V> getAll() {
        List<V> all = new ArrayList<>();
        traverseNode(root, all);
        return all;
    }

    private void traverseNode(BTreeNode<V> node, List<V> result) {
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



    // U — UPDATE


    public boolean update(String key, V newValue) {
        return updateNode(root, key, newValue);
    }

    private boolean updateNode(BTreeNode<V> node, String key, V newValue) {
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

    private boolean deleteFromNode(BTreeNode<V> node, String key) {
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


    private void removeFromLeaf(BTreeNode<V> node, int i) {
        for (int j = i + 1; j < node.keyCount; j++) {
            node.keys[j - 1]   = node.keys[j];
            node.values[j - 1] = node.values[j];
        }
        node.keys[node.keyCount - 1]   = null;
        node.values[node.keyCount - 1] = null;
        node.keyCount--;
    }


    private boolean deleteFromInternalNode(BTreeNode<V> node, int i) {
        String key = node.keys[i];

        if (node.children[i].keyCount >= t) {

            // Get predecessor key+value directly from the node structure (no CommitInfo-specific calls)
            String predKey   = getPredecessorKey(node, i);
            V      predValue = getPredecessorValue(node, i);
            node.keys[i]   = predKey;
            node.values[i] = predValue;
            return deleteFromNode(node.children[i], predKey);

        } else if (node.children[i + 1].keyCount >= t) {

            // Get successor key+value directly from the node structure
            String succKey   = getSuccessorKey(node, i);
            V      succValue = getSuccessorValue(node, i);
            node.keys[i]   = succKey;
            node.values[i] = succValue;
            return deleteFromNode(node.children[i + 1], succKey);

        } else {

            merge(node, i);
            return deleteFromNode(node.children[i], key);
        }
    }


    // Returns the key of the in-order predecessor (rightmost key in left subtree)
    private String getPredecessorKey(BTreeNode<V> node, int i) {
        BTreeNode<V> cur = node.children[i];
        while (!cur.isLeaf) {
            cur = cur.children[cur.keyCount];
        }
        return cur.keys[cur.keyCount - 1];
    }

    // Returns the value of the in-order predecessor
    private V getPredecessorValue(BTreeNode<V> node, int i) {
        BTreeNode<V> cur = node.children[i];
        while (!cur.isLeaf) {
            cur = cur.children[cur.keyCount];
        }
        return cur.values[cur.keyCount - 1];
    }

    // Returns the key of the in-order successor (leftmost key in right subtree)
    private String getSuccessorKey(BTreeNode<V> node, int i) {
        BTreeNode<V> cur = node.children[i + 1];
        while (!cur.isLeaf) {
            cur = cur.children[0];
        }
        return cur.keys[0];
    }

    // Returns the value of the in-order successor
    private V getSuccessorValue(BTreeNode<V> node, int i) {
        BTreeNode<V> cur = node.children[i + 1];
        while (!cur.isLeaf) {
            cur = cur.children[0];
        }
        return cur.values[0];
    }


    private void fillChild(BTreeNode<V> node, int i) {
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


    private void borrowFromPrev(BTreeNode<V> node, int i) {
        BTreeNode<V> child   = node.children[i];
        BTreeNode<V> sibling = node.children[i - 1];


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


    private void borrowFromNext(BTreeNode<V> node, int i) {
        BTreeNode<V> child   = node.children[i];
        BTreeNode<V> sibling = node.children[i + 1];


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


    private void merge(BTreeNode<V> node, int i) {
        BTreeNode<V> leftChild  = node.children[i];
        BTreeNode<V> rightChild = node.children[i + 1];


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
