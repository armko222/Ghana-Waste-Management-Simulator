package com.dcit308.wasteops.structures;

/**
 * Self-balancing binary search tree (rotation + recolouring), or a
 * clearly documented simplified balanced-tree alternative if the team
 * agrees that trade-off. Implements SearchTreeADT so it can be compared
 * directly against Issue #6's plain BST using identical method calls.
 *
 * Owned by Issue #7.
 */
public class RedBlackTree<K extends Comparable<K>, V> implements SearchTreeADT<K, V> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    private class Node {
        K key;
        V value;
        Node left, right, parent;
        boolean color = RED;
        Node (K key, V value){
            this.key = key;
            this.value = value;
        }
    }
    private Node root;
    private int treeSize = 0;

    @Override
    public void insert(K key, V value) {
        if (key == null) return;
        Node z = new Node(key, value);
        Node y = null;
        Node x = root;
        while (x !=null){
            y=x;
            int cmp = key.compareTo(x.key);
            if (cmp<0){
                x=x.left;
            }
            else if (cmp>0){
                x=x.right;
            }
            else{x.value = value;
            return;}
        }
    z.parent = y;
    if (y == null){
        root = z;
    }
    else if(key.compareTo(y.key) < 0){
        y.left = z;
    }
    else{y.right = z;}
fixInsert(z);
treeSize++;
    }
    
    

    @Override
    public V search(K key) {
        if (key == null) return null;
        Node current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
                }
                else if (cmp > 0) {
                    current = current.right;
                }
                else{return current.value;}
    }
        return null;
    }

    @Override
    public int height() {
        return getHeight(root);
    }
    private int getHeight (Node node){
        if (node == null) return 0;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    @Override
    public int size() {
        return treeSize; 
    }
private void fixInsert(Node k) {
        while (k.parent != null && k.parent.color == RED) {
            if (k.parent == k.parent.parent.left) {
                Node u = k.parent.parent.right;
                if (u != null && u.color == RED) {
                    k.parent.color = BLACK;
                    u.color = BLACK;
                    k.parent.parent.color = RED;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.right) {
                        k = k.parent;
                        rotateLeft(k);
                    }
                    k.parent.color = BLACK;
                    k.parent.parent.color = RED;
                    rotateRight(k.parent.parent);
                }
            } else {
                Node u = k.parent.parent.left;
                if (u != null && u.color == RED) {
                    k.parent.color = BLACK;
                    u.color = BLACK;
                    k.parent.parent.color = RED;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.left) {
                        k = k.parent;
                        rotateRight(k);
                    }
                    k.parent.color = BLACK;
                    k.parent.parent.color = RED;
                    rotateLeft(k.parent.parent);
                }
            }
            if (k == root) break;
        }
        root.color = BLACK;
    }

    private void rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left = x;
        x.parent = y;
    }

    private void rotateRight(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != null) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.right) x.parent.right = y;
        else x.parent.left = y;
        y.right = x;
        x.parent = y;
    }
}
