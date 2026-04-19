package com.gitquality.git_quality.btree;
import java.util.ArrayList;
import java.util.List;

public class BTreeNode<K extends Comparable<K>, V> {
    List<K> keys = new ArrayList<>();
    List<V> values = new ArrayList<>();
    List<BTreeNode<K, V>> children = new ArrayList<>();
    boolean isLeaf = true;
}