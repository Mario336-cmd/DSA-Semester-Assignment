import java.util.ArrayList;
import java.util.List;

public class BTree {
    // Default to 2–3 tree (max 2 keys per node); change to 3 for 3–4 tree, etc.
    public static int MAX_KEYS = 2;

    public static class Node {
        int[]  keys;
        Node[] C;
        int    n;
        boolean leaf;

        public Node() {
            keys = new int[MAX_KEYS + 1];
            C    = new Node[MAX_KEYS + 2];
            n    = 0;
            leaf = true;
        }
    }

    private Node root;

    public BTree() {
        root = new Node();
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node r) {
        root = r;
    }

    public void insert(int k) {
        SplitResult r = insertRec(root, k);
        if (r != null) {
            Node s = new Node();
            s.leaf    = false;
            s.n       = 1;
            s.keys[0] = r.key;
            s.C[0]    = r.left;
            s.C[1]    = r.right;
            root = s;
        }
    }

    private SplitResult insertRec(Node node, int k) {
        if (node.leaf) {
            int i = node.n - 1;
            while (i >= 0 && k < node.keys[i]) {
                node.keys[i+1] = node.keys[i];
                i--;
            }
            node.keys[i+1] = k;
            node.n++;
            if (node.n <= MAX_KEYS) return null;
            return splitNode(node);
        } else {
            int i = 0;
            while (i < node.n && k > node.keys[i]) i++;
            SplitResult r = insertRec(node.C[i], k);
            if (r == null) return null;
            for (int j = node.n - 1; j >= i; j--) {
                node.keys[j+1] = node.keys[j];
            }
            node.keys[i] = r.key;
            for (int j = node.n; j >= i+1; j--) {
                node.C[j+1] = node.C[j];
            }
            node.C[i]   = r.left;
            node.C[i+1] = r.right;
            node.n++;
            if (node.n <= MAX_KEYS) return null;
            return splitNode(node);
        }
    }

    private SplitResult splitNode(Node node) {
        int midIndex = node.n / 2;
        int midKey   = node.keys[midIndex];
        Node left    = new Node();
        Node right   = new Node();
        left.leaf  = node.leaf;
        right.leaf = node.leaf;
        left.n     = midIndex;
        for (int j = 0; j < midIndex; j++) {
            left.keys[j] = node.keys[j];
        }
        right.n = node.n - midIndex - 1;
        for (int j = 0; j < right.n; j++) {
            right.keys[j] = node.keys[midIndex + 1 + j];
        }
        if (!node.leaf) {
            for (int j = 0; j <= left.n; j++) {
                left.C[j] = node.C[j];
            }
            for (int j = 0; j <= right.n; j++) {
                right.C[j] = node.C[midIndex + 1 + j];
            }
        }
        return new SplitResult(midKey, left, right);
    }

    private static class SplitResult {
        int key;
        Node left, right;
        SplitResult(int key, Node l, Node r) {
            this.key   = key;
            this.left  = l;
            this.right = r;
        }
    }

    public List<Integer> getSearchPath(int k) {
        List<Integer> path = new ArrayList<>();
        searchPath(root, k, path);
        return path;
    }

    private void searchPath(Node node, int k, List<Integer> path) {
        int i = 0;
        while (i < node.n && k > node.keys[i]) i++;
        if (i < node.n) path.add(node.keys[i]);
        else if (node.n > 0) path.add(node.keys[node.n - 1]);
        if (node.leaf) return;
        searchPath(node.C[i], k, path);
    }
}
