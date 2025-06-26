import java.util.*;

public class AVLTree {
    public static class Node {
        public int key, h;
        public Node left, right, p;
        public Node(int k) {
            key = k;
            h = 1;
            left = right = p = null;
        }
    }

    Node root;

    public void insert(int k) {
        root = insertRec(root, k, null);
    }

    private Node insertRec(Node node, int k, Node parent) {
        if (node == null) {
            Node n = new Node(k);
            n.p = parent;
            return n;
        }
        if (k < node.key) {
            node.left = insertRec(node.left, k, node);
        } else {
            node.right = insertRec(node.right, k, node);
        }
        node.h = 1 + Math.max(height(node.left), height(node.right));
        return balance(node);
    }

    private int height(Node n) {
        return n == null ? 0 : n.h;
    }

    private int balanceFactor(Node n) {
        return height(n.left) - height(n.right);
    }

    public Node rotateLeftSub(Node x) {
        Node y = x.right;
        Node T2 = y.left;
        y.left = x;
        x.right = T2;
        y.p = x.p;
        x.p = y;
        if (T2 != null) T2.p = x;
        x.h = 1 + Math.max(height(x.left), height(x.right));
        y.h = 1 + Math.max(height(y.left), height(y.right));
        Main.avlSnapshots.add(Main.cloneAVL(root));
        return y;
    }

    public Node rotateRightSub(Node y) {
        Node x = y.left;
        Node T2 = x.right;
        x.right = y;
        y.left = T2;
        x.p = y.p;
        y.p = x;
        if (T2 != null) T2.p = y;
        y.h = 1 + Math.max(height(y.left), height(y.right));
        x.h = 1 + Math.max(height(x.left), height(x.right));
        Main.avlSnapshots.add(Main.cloneAVL(root));
        return x;
    }

    private Node balance(Node node) {
        int bf = balanceFactor(node);
        if (bf > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeftSub(node.left);
            }
            node = rotateRightSub(node);
        } else if (bf < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRightSub(node.right);
            }
            node = rotateLeftSub(node);
        }
        return node;
    }

    public void recalcHeights() {
        recalc(root);
    }

    private int recalc(Node n) {
        if (n == null) return 0;
        n.h = 1 + Math.max(recalc(n.left), recalc(n.right));
        return n.h;
    }

    public Node find(Node n, int k) {
        if (n == null) return null;
        if (n.key == k) return n;
        return k < n.key ? find(n.left, k) : find(n.right, k);
    }

    public Node getRoot() {
        return root;
    }
}
