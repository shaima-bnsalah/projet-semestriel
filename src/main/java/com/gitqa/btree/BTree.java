package com.gitqa.btree;

import com.gitqa.model.CommitInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BTree implements Serializable {

    private static final long serialVersionUID = 1L;

    private BTreeNode root;
    private final int t;
    private int size;

    public BTree(int t) {
        this.t = t;
        this.root = new BTreeNode(t, true);
    }

    public int size() { return size; }
    public BTreeNode getRoot() { return root; }

    // Smallest index i in [0, keyCount] with node.keys[i] >= key (or keyCount if all are <).
    private static int findKeyIndex(BTreeNode node, String key) {
        int lo = 0, hi = node.keyCount;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (node.keys[mid].compareTo(key) < 0) lo = mid + 1;
            else                                   hi = mid;
        }
        return lo;
    }

    // 1 : CREATE

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
                if (key.compareTo(node.keys[pos]) > 0) pos++;
            }
            insertNonFull(node.children[pos], key, value);
        }
    }

    private void splitChild(BTreeNode parent, int i, BTreeNode fullChild) {
        BTreeNode newChild = new BTreeNode(t, fullChild.isLeaf);
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
        parent.keys[i]   = fullChild.keys[t - 1];
        parent.values[i] = fullChild.values[t - 1];
        parent.keyCount++;
    }

    // 2 : READ (search + getAll + rangeByDate)

    public CommitInfo search(String key) {
        BTreeNode node = root;
        while (node != null) {
            int i = findKeyIndex(node, key);
            if (i < node.keyCount && key.equals(node.keys[i])) return node.values[i];
            if (node.isLeaf) return null;
            node = node.children[i];
        }
        return null;
    }

    public boolean contains(String key) {
        return search(key) != null;
    }

    private void traverseNode(BTreeNode node, List<CommitInfo> result) {
        for (int i = 0; i < node.keyCount; i++) {
            if (!node.isLeaf) traverseNode(node.children[i], result);
            result.add(node.values[i]);
        }
        if (!node.isLeaf) traverseNode(node.children[node.keyCount], result);
    }

    public List<CommitInfo> getAll() {
        List<CommitInfo> all = new ArrayList<>(size);
        traverseNode(root, all);
        return all;
    }

    public List<CommitInfo> rangeByDate(long fromEpoch, long toEpoch) {
        List<CommitInfo> result = new ArrayList<>();
        collectRange(root, fromEpoch, toEpoch, result);
        return result;
    }

    private void collectRange(BTreeNode node, long from, long to, List<CommitInfo> result) {
        if (node == null) return;
        for (int i = 0; i < node.keyCount; i++) {
            if (!node.isLeaf) collectRange(node.children[i], from, to, result);
            long ts = node.values[i].getTimestamp();
            if (ts >= from && ts <= to) result.add(node.values[i]);
        }
        if (!node.isLeaf) collectRange(node.children[node.keyCount], from, to, result);
    }

    // 3 : UPDATE

    public boolean update(String key, CommitInfo newValue) {
        BTreeNode node = root;
        while (node != null) {
            int i = findKeyIndex(node, key);
            if (i < node.keyCount && key.equals(node.keys[i])) {
                node.values[i] = newValue;
                return true;
            }
            if (node.isLeaf) return false;
            node = node.children[i];
        }
        return false;
    }

    // 4 : DELETE

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
        int i = findKeyIndex(node, key);

        if (i < node.keyCount && key.equals(node.keys[i])) {
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

    private void removeFromLeaf(BTreeNode node, int i) {
        int tail = node.keyCount - i - 1;
        if (tail > 0) {
            System.arraycopy(node.keys,   i + 1, node.keys,   i, tail);
            System.arraycopy(node.values, i + 1, node.values, i, tail);
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
        while (!cur.isLeaf) cur = cur.children[cur.keyCount];
        return cur.values[cur.keyCount - 1];
    }

    private CommitInfo getSuccessor(BTreeNode node, int i) {
        BTreeNode cur = node.children[i + 1];
        while (!cur.isLeaf) cur = cur.children[0];
        return cur.values[0];
    }

    private void fillChild(BTreeNode node, int i) {
        if (i > 0 && node.children[i - 1].keyCount >= t) {
            borrowFromPrev(node, i);
        } else if (i < node.keyCount && node.children[i + 1].keyCount >= t) {
            borrowFromNext(node, i);
        } else {
            if (i < node.keyCount) merge(node, i);
            else                   merge(node, i - 1);
        }
    }

    private void borrowFromPrev(BTreeNode node, int i) {
        BTreeNode child   = node.children[i];
        BTreeNode sibling = node.children[i - 1];

        System.arraycopy(child.keys,   0, child.keys,   1, child.keyCount);
        System.arraycopy(child.values, 0, child.values, 1, child.keyCount);
        if (!child.isLeaf) {
            System.arraycopy(child.children, 0, child.children, 1, child.keyCount + 1);
        }

        child.keys[0]   = node.keys[i - 1];
        child.values[0] = node.values[i - 1];
        if (!child.isLeaf) {
            child.children[0] = sibling.children[sibling.keyCount];
            sibling.children[sibling.keyCount] = null;
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

    private void merge(BTreeNode node, int i) {
        BTreeNode leftChild  = node.children[i];
        BTreeNode rightChild = node.children[i + 1];

        leftChild.keys[t - 1]   = node.keys[i];
        leftChild.values[t - 1] = node.values[i];

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
        node.keys[node.keyCount - 1]       = null;
        node.values[node.keyCount - 1]     = null;
        node.children[node.keyCount]       = null;

        leftChild.keyCount += rightChild.keyCount + 1;
        node.keyCount--;
    }
}