package com.gitquality.git_quality.btree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BTree<K extends Comparable<K>, V> implements Serializable {

    private static final long serialVersionUID = 1L;

    private BTreeNode<K, V> root;
    private final int t;
    private int size;

    public BTree() { this(3); }

    public BTree(int t) {
        this.t = t;
        this.root = new BTreeNode<>(t, true);
    }

    public int size() { return size; }
    public BTreeNode<K, V> getRoot() { return root; }

    private static <K extends Comparable<K>, V> int findKeyIndex(BTreeNode<K, V> node, K key) {
        int lo = 0, hi = node.keyCount;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (node.key(mid).compareTo(key) < 0) lo = mid + 1;
            else                                  hi = mid;
        }
        return lo;
    }

    public void insert(K key, V value) {
        if (update(key, value)) return;

        BTreeNode<K, V> r = root;
        if (r.keyCount == 2 * t - 1) {
            BTreeNode<K, V> newRoot = new BTreeNode<>(t, false);
            newRoot.children[0] = r;
            root = newRoot;
            splitChild(newRoot, 0, r);
            insertNonFull(newRoot, key, value);
        } else {
            insertNonFull(r, key, value);
        }
        size++;
    }

    private void insertNonFull(BTreeNode<K, V> node, K key, V value) {
        int pos = findKeyIndex(node, key);
        if (node.isLeaf) {
            int shift = node.keyCount - pos;
            if (shift > 0) {
                System.arraycopy(node.keys,   pos, node.keys,   pos + 1, shift);
                System.arraycopy(node.values, pos, node.values, pos + 1, shift);
            }
            node.keys[pos]   = key;
            node.values[pos] = value;
            node.keyCount++;
        } else {
            if (node.children[pos].keyCount == 2 * t - 1) {
                splitChild(node, pos, node.children[pos]);
                if (key.compareTo(node.key(pos)) > 0) pos++;
            }
            insertNonFull(node.children[pos], key, value);
        }
    }

    private void splitChild(BTreeNode<K, V> parent, int i, BTreeNode<K, V> fullChild) {
        BTreeNode<K, V> newChild = new BTreeNode<>(t, fullChild.isLeaf);
        newChild.keyCount = t - 1;

        System.arraycopy(fullChild.keys,   t, newChild.keys,   0, t - 1);
        System.arraycopy(fullChild.values, t, newChild.values, 0, t - 1);
        if (!fullChild.isLeaf) {
            System.arraycopy(fullChild.children, t, newChild.children, 0, t);
        }

        fullChild.keyCount = t - 1;

        if (parent.keyCount - i > 0) {
            System.arraycopy(parent.children, i + 1, parent.children, i + 2, parent.keyCount - i);
            System.arraycopy(parent.keys,     i,     parent.keys,     i + 1, parent.keyCount - i);
            System.arraycopy(parent.values,   i,     parent.values,   i + 1, parent.keyCount - i);
        }
        parent.children[i + 1] = newChild;
        parent.keys[i]   = fullChild.key(t - 1);
        parent.values[i] = fullChild.value(t - 1);
        parent.keyCount++;
    }

    public V search(K key) {
        BTreeNode<K, V> node = root;
        while (node != null) {
            int i = findKeyIndex(node, key);
            if (i < node.keyCount && key.equals(node.key(i))) return node.value(i);
            if (node.isLeaf) return null;
            node = node.children[i];
        }
        return null;
    }

    public boolean contains(K key) {
        return search(key) != null;
    }

    private void traverseNode(BTreeNode<K, V> node, List<V> result) {
        for (int i = 0; i < node.keyCount; i++) {
            if (!node.isLeaf) traverseNode(node.children[i], result);
            result.add(node.value(i));
        }
        if (!node.isLeaf) traverseNode(node.children[node.keyCount], result);
    }

    public List<V> getAll() {
        List<V> all = new ArrayList<>(size);
        traverseNode(root, all);
        return all;
    }

    public boolean update(K key, V newValue) {
        BTreeNode<K, V> node = root;
        while (node != null) {
            int i = findKeyIndex(node, key);
            if (i < node.keyCount && key.equals(node.key(i))) {
                node.values[i] = newValue;
                return true;
            }
            if (node.isLeaf) return false;
            node = node.children[i];
        }
        return false;
    }

    public boolean delete(K key) {
        if (root == null || root.keyCount == 0) return false;
        boolean deleted = deleteFromNode(root, key);

        if (root.keyCount == 0 && !root.isLeaf) {
            root = root.children[0];
        }
        if (deleted) size--;
        return deleted;
    }

    private boolean deleteFromNode(BTreeNode<K, V> node, K key) {
        int i = findKeyIndex(node, key);

        if (i < node.keyCount && key.equals(node.key(i))) {
            if (node.isLeaf) {
                removeFromLeaf(node, i);
                return true;
            }
            return deleteFromInternalNode(node, i);
        }

        if (node.isLeaf) return false;

        boolean isLastChild = (i == node.keyCount);
        if (node.children[i].keyCount < t) {
            fillChild(node, i);
            if (isLastChild && i > node.keyCount) i--;
        }

        return deleteFromNode(node.children[i], key);
    }

    private void removeFromLeaf(BTreeNode<K, V> node, int i) {
        int tail = node.keyCount - i - 1;
        if (tail > 0) {
            System.arraycopy(node.keys,   i + 1, node.keys,   i, tail);
            System.arraycopy(node.values, i + 1, node.values, i, tail);
        }
        node.keys[node.keyCount - 1]   = null;
        node.values[node.keyCount - 1] = null;
        node.keyCount--;
    }

    private boolean deleteFromInternalNode(BTreeNode<K, V> node, int i) {
        K key = node.key(i);

        if (node.children[i].keyCount >= t) {
            BTreeNode<K, V> cur = node.children[i];
            while (!cur.isLeaf) cur = cur.children[cur.keyCount];
            K predKey = cur.key(cur.keyCount - 1);
            V predVal = cur.value(cur.keyCount - 1);
            node.keys[i]   = predKey;
            node.values[i] = predVal;
            return deleteFromNode(node.children[i], predKey);

        } else if (node.children[i + 1].keyCount >= t) {
            BTreeNode<K, V> cur = node.children[i + 1];
            while (!cur.isLeaf) cur = cur.children[0];
            K succKey = cur.key(0);
            V succVal = cur.value(0);
            node.keys[i]   = succKey;
            node.values[i] = succVal;
            return deleteFromNode(node.children[i + 1], succKey);

        } else {
            merge(node, i);
            return deleteFromNode(node.children[i], key);
        }
    }

    private void fillChild(BTreeNode<K, V> node, int i) {
        if (i > 0 && node.children[i - 1].keyCount >= t) {
            borrowFromPrev(node, i);
        } else if (i < node.keyCount && node.children[i + 1].keyCount >= t) {
            borrowFromNext(node, i);
        } else {
            if (i < node.keyCount) merge(node, i);
            else                   merge(node, i - 1);
        }
    }

    private void borrowFromPrev(BTreeNode<K, V> node, int i) {
        BTreeNode<K, V> child   = node.children[i];
        BTreeNode<K, V> sibling = node.children[i - 1];

        System.arraycopy(child.keys,   0, child.keys,   1, child.keyCount);
        System.arraycopy(child.values, 0, child.values, 1, child.keyCount);
        if (!child.isLeaf) {
            System.arraycopy(child.children, 0, child.children, 1, child.keyCount + 1);
        }

        child.keys[0]   = node.key(i - 1);
        child.values[0] = node.value(i - 1);
        if (!child.isLeaf) {
            child.children[0] = sibling.children[sibling.keyCount];
            sibling.children[sibling.keyCount] = null;
        }

        node.keys[i - 1]   = sibling.key(sibling.keyCount - 1);
        node.values[i - 1] = sibling.value(sibling.keyCount - 1);
        sibling.keys[sibling.keyCount - 1]   = null;
        sibling.values[sibling.keyCount - 1] = null;

        child.keyCount++;
        sibling.keyCount--;
    }

    private void borrowFromNext(BTreeNode<K, V> node, int i) {
        BTreeNode<K, V> child   = node.children[i];
        BTreeNode<K, V> sibling = node.children[i + 1];

        child.keys[child.keyCount]   = node.key(i);
        child.values[child.keyCount] = node.value(i);
        if (!child.isLeaf) {
            child.children[child.keyCount + 1] = sibling.children[0];
        }

        node.keys[i]   = sibling.key(0);
        node.values[i] = sibling.value(0);

        System.arraycopy(sibling.keys,   1, sibling.keys,   0, sibling.keyCount - 1);
        System.arraycopy(sibling.values, 1, sibling.values, 0, sibling.keyCount - 1);
        sibling.keys[sibling.keyCount - 1]   = null;
        sibling.values[sibling.keyCount - 1] = null;
        if (!sibling.isLeaf) {
            System.arraycopy(sibling.children, 1, sibling.children, 0, sibling.keyCount);
            sibling.children[sibling.keyCount] = null;
        }

        child.keyCount++;
        sibling.keyCount--;
    }

    private void merge(BTreeNode<K, V> node, int i) {
        BTreeNode<K, V> leftChild  = node.children[i];
        BTreeNode<K, V> rightChild = node.children[i + 1];

        leftChild.keys[t - 1]   = node.key(i);
        leftChild.values[t - 1] = node.value(i);

        System.arraycopy(rightChild.keys,   0, leftChild.keys,   t, rightChild.keyCount);
        System.arraycopy(rightChild.values, 0, leftChild.values, t, rightChild.keyCount);
        if (!leftChild.isLeaf) {
            System.arraycopy(rightChild.children, 0, leftChild.children, t, rightChild.keyCount + 1);
        }

        int tail = node.keyCount - i - 1;
        if (tail > 0) {
            System.arraycopy(node.keys,     i + 1, node.keys,     i,     tail);
            System.arraycopy(node.values,   i + 1, node.values,   i,     tail);
            System.arraycopy(node.children, i + 2, node.children, i + 1, tail);
        }
        node.keys[node.keyCount - 1]   = null;
        node.values[node.keyCount - 1] = null;
        node.children[node.keyCount]   = null;

        leftChild.keyCount += rightChild.keyCount + 1;
        node.keyCount--;
    }
}