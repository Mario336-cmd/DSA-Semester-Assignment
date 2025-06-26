import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    enum TreeType { BST, AVL, RBT, BTREE }

    static List<Object> avlSnapshots = new ArrayList<>();
    static List<Object> rbtSnapshots = new ArrayList<>();
    static List<Object> tttSnapshots = new ArrayList<>();

    private final BST bst = new BST();
    private final AVLTree avl = new AVLTree();
    private final RBTree rbt = new RBTree();
    private final BTree btree = new BTree();
    private final TreePanel treePanel;

    public Main() {
        int[] fullSeq = {7,5,9,4,6,8,13,2,3};
        for (int v : fullSeq) {
            btree.insert(v);
            rbt.insert(v);
        }
        JFrame frame = new JFrame("DSA 2 Trees");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900,600);
        frame.setLocationRelativeTo(null);
        treePanel = new TreePanel();
        frame.add(treePanel, BorderLayout.CENTER);
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(new EmptyBorder(5,5,5,5));
        String[] labels = {
                "Insert 3 (BST)",
                "BST Postorder",
                "AVL Tree Insert",
                "RBT Insert+Postorder",
                "B-Tree Insert+Search 8",
                "Exit"
        };
        for (int i = 0; i < labels.length; i++) {
            JButton btn = createButton(labels[i]);
            final int idx = i;
            btn.addActionListener(e -> onAction(idx));
            toolbar.add(btn);
            if (i < labels.length - 1) toolbar.addSeparator(new Dimension(10,0));
        }
        frame.add(toolbar, BorderLayout.NORTH);
        frame.setVisible(true);
        animateBSTCreation(new int[]{7,5,9,4,6,8,13,2}, 300);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN,14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(75,110,175));
        btn.setOpaque(true);
        btn.setUI(new BasicButtonUI());
        btn.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(95,130,195)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(75,110,175)); }
        });
        return btn;
    }

    private void onAction(int idx) {
        switch (idx) {
            case 0:
                if (bst.contains(3)) {
                    JOptionPane.showMessageDialog(treePanel, "3 is already in the BST.","Info",JOptionPane.INFORMATION_MESSAGE);
                } else {
                    animateBSTCreation(new int[]{7,5,9,4,6,8,13,2,3}, 300);
                }
                break;
            case 1:
                List<Integer> post = bst.getPostorderList();
                treePanel.animateTraversal(TreeType.BST, bst.getRoot(), post, 300, () ->
                        JOptionPane.showMessageDialog(treePanel, "BST postorder: " + post,"Result",JOptionPane.INFORMATION_MESSAGE)
                );
                break;
            case 2:
                animateAVLRotations();
                break;
            case 3:
                int[] seq = {7,5,9,4,6,8,13,2,3};
                animateRBTCreation(seq, 300);
                int totalRbt = rbtSnapshots.size() * 300 + 200;
                new Timer(totalRbt, e -> {
                    List<Integer> rpost = rbt.getPostorderList();
                    treePanel.animateTraversal(TreeType.RBT, rbt.getRoot(), rpost, 300, () ->
                            JOptionPane.showMessageDialog(treePanel, "RBT postorder: " + rpost,"Result",JOptionPane.INFORMATION_MESSAGE)
                    );
                }) {{ setRepeats(false); }}.start();
                break;
            case 4:
                int[] seq2 = {7,5,9,4,6,8,13,2,3};
                animate23TreeCreation(seq2, 300);
                int totalTtt = tttSnapshots.size() * 300 + 200;
                new Timer(totalTtt, e -> {
                    List<Integer> path = btree.getSearchPath(8);
                    treePanel.animateTraversal(TreeType.BTREE, btree.getRoot(), path, 300, () ->
                            JOptionPane.showMessageDialog(treePanel, "B-Tree search path: " + path,"Result",JOptionPane.INFORMATION_MESSAGE)
                    );
                }) {{ setRepeats(false); }}.start();
                break;
            case 5:
                System.exit(0);
                break;
        }
    }

    private void animateBSTCreation(int[] seq, int delay) {
        BST tmp = new BST();
        List<Object> stages = new ArrayList<>();
        for (int v : seq) {
            tmp.insert(v);
            stages.add(cloneBST(tmp.getRoot()));
        }
        bst.root = tmp.getRoot();
        treePanel.animateStages(TreeType.BST, stages, delay);
    }

    private void animateAVLRotations() {
        int[] seq = {7,5,9,4,6,8,13,2,3};
        avlSnapshots.clear();
        AVLTree tmp = new AVLTree();
        for (int v : seq) {
            avlSnapshots.add(cloneAVL(tmp.getRoot()));
            tmp.insert(v);
            avlSnapshots.add(cloneAVL(tmp.getRoot()));
        }
        avl.root = tmp.getRoot();
        treePanel.animateStages(TreeType.AVL, avlSnapshots, 300);
    }

    private void animateRBTCreation(int[] seq, int delay) {
        rbtSnapshots.clear();
        RBTree tmp = new RBTree();
        for (int v : seq) {
            rbtSnapshots.add(cloneRBT(tmp.getRoot()));
            tmp.insert(v);
            rbtSnapshots.add(cloneRBT(tmp.getRoot()));
        }
        rbt.root = tmp.getRoot();
        treePanel.animateStages(TreeType.RBT, rbtSnapshots, delay);
    }

    private void animate23TreeCreation(int[] seq, int delay) {
        tttSnapshots.clear();
        BTree tmp = new BTree();
        for (int v : seq) {
            tttSnapshots.add(clone23(tmp.getRoot()));
            tmp.insert(v);
            tttSnapshots.add(clone23(tmp.getRoot()));
        }
        btree.setRoot(tmp.getRoot());
        treePanel.animateStages(TreeType.BTREE, tttSnapshots, delay);
    }

    public static BST.Node cloneBST(BST.Node n) {
        if (n == null) return null;
        BST.Node c = new BST.Node(n.key);
        c.left = cloneBST(n.left);
        c.right = cloneBST(n.right);
        return c;
    }

    public static AVLTree.Node cloneAVL(AVLTree.Node n) {
        if (n == null) return null;
        AVLTree.Node c = new AVLTree.Node(n.key);
        c.h = n.h;
        c.left = cloneAVL(n.left);
        c.right = cloneAVL(n.right);
        if (c.left != null) c.left.p = c;
        if (c.right != null) c.right.p = c;
        return c;
    }

    public static RBTree.Node cloneRBT(RBTree.Node n) {
        if (n == RBTree.NIL) return RBTree.NIL;
        RBTree.Node c = new RBTree.Node(n.key);
        c.color = n.color;
        c.left = cloneRBT(n.left);
        c.right = cloneRBT(n.right);
        if (c.left != RBTree.NIL) c.left.p = c;
        if (c.right != RBTree.NIL) c.right.p = c;
        return c;
    }

    public static BTree.Node clone23(BTree.Node n) {
        if (n == null) return null;
        BTree.Node c = new BTree.Node();
        c.leaf = n.leaf;
        c.n = n.n;
        System.arraycopy(n.keys, 0, c.keys, 0, n.n);
        for (int i = 0; i <= n.n; i++) {
            c.C[i] = clone23(n.C[i]);
        }
        return c;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
