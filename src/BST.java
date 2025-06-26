import java.util.ArrayList;
import java.util.List;

public class BST {
    public static class Node {
        public int key;
        public Node left, right;
        public Node(int k) {
            key = k;
            left = right = null;
        }
    }

    Node root;

    public void insert(int k) {
        root = insertRec(root, k);
    }

    private Node insertRec(Node node, int k) {
        if (node == null) {
            return new Node(k);
        }
        if (k < node.key) {
            node.left = insertRec(node.left, k);
        } else {
            node.right = insertRec(node.right, k);
        }
        return node;
    }

    public boolean contains(int k) {
        return containsRec(root, k);
    }

    private boolean containsRec(Node node, int k) {
        if (node == null) return false;
        if (node.key == k) return true;
        return (k < node.key)
                ? containsRec(node.left, k)
                : containsRec(node.right, k);
    }

    public Node getRoot() {
        return root;
    }

    public List<Integer> getPostorderList() {
        List<Integer> out = new ArrayList<>();
        postorderRec(root, out);
        return out;
    }

    private void postorderRec(Node node, List<Integer> out) {
        if (node == null) return;
        postorderRec(node.left, out);
        postorderRec(node.right, out);
        out.add(node.key);
    }
}
