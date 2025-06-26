import java.util.ArrayList;
import java.util.List;

public class RBTree {
    static final boolean RED = true;
    static final boolean BLACK = false;

    public static class Node {
        int key;
        boolean color;
        Node left, right, p;
        public Node(int k) {
            key = k;
            color = RED;
            left = right = p = NIL;
        }
        public Node(int k, boolean c) {
            this(k);
            color = c;
        }
    }

    public static final Node NIL = new Node(0, BLACK);
    static {
        NIL.left = NIL.right = NIL.p = NIL;
    }

    Node root = NIL;

    public void insert(int key) {
        Node z = new Node(key), y = NIL, x = root;
        while (x != NIL) {
            y = x;
            x = key < x.key ? x.left : x.right;
        }
        z.p = y;
        if (y == NIL) {
            root = z;
        } else if (key < y.key) {
            y.left = z;
        } else {
            y.right = z;
        }
        z.left = NIL;
        z.right = NIL;
        fixUp(z);
    }

    void fixUp(Node z) {
        while (z.p.color == RED) {
            if (z.p == z.p.p.left) {
                Node y = z.p.p.right;
                if (y.color == RED) {
                    z.p.color = BLACK;
                    y.color = BLACK;
                    z.p.p.color = RED;
                    Main.rbtSnapshots.add(Main.cloneRBT(root));
                    z = z.p.p;
                } else {
                    if (z == z.p.right) {
                        z = z.p;
                        leftRotate(z);
                        Main.rbtSnapshots.add(Main.cloneRBT(root));
                    }
                    z.p.color = BLACK;
                    z.p.p.color = RED;
                    Main.rbtSnapshots.add(Main.cloneRBT(root));
                    rightRotate(z.p.p);
                    Main.rbtSnapshots.add(Main.cloneRBT(root));
                }
            } else {
                Node y = z.p.p.left;
                if (y.color == RED) {
                    z.p.color = BLACK;
                    y.color = BLACK;
                    z.p.p.color = RED;
                    Main.rbtSnapshots.add(Main.cloneRBT(root));
                    z = z.p.p;
                } else {
                    if (z == z.p.left) {
                        z = z.p;
                        rightRotate(z);
                        Main.rbtSnapshots.add(Main.cloneRBT(root));
                    }
                    z.p.color = BLACK;
                    z.p.p.color = RED;
                    Main.rbtSnapshots.add(Main.cloneRBT(root));
                    leftRotate(z.p.p);
                    Main.rbtSnapshots.add(Main.cloneRBT(root));
                }
            }
        }
        root.color = BLACK;
        Main.rbtSnapshots.add(Main.cloneRBT(root));
    }

    void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != NIL) y.left.p = x;
        y.p = x.p;
        if (x.p == NIL) {
            root = y;
        } else if (x == x.p.left) {
            x.p.left = y;
        } else {
            x.p.right = y;
        }
        y.left = x;
        x.p = y;
    }

    void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != NIL) y.right.p = x;
        y.p = x.p;
        if (x.p == NIL) {
            root = y;
        } else if (x == x.p.right) {
            x.p.right = y;
        } else {
            x.p.left = y;
        }
        y.right = x;
        x.p = y;
    }

    public Node getRoot() {
        return root;
    }

    public List<Integer> getPostorderList() {
        List<Integer> out = new ArrayList<>();
        post(root, out);
        return out;
    }

    private void post(Node n, List<Integer> L) {
        if (n == NIL) return;
        post(n.left, L);
        post(n.right, L);
        L.add(n.key);
    }
}
