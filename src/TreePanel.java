import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TreePanel extends JPanel {
    Main.TreeType type;
    Object root;

    private List<Object> stages;
    private int stageIndex;
    private Timer stageTimer;

    private List<Integer> travList;
    private int travIndex;
    private Timer travTimer;

    private int highlightKey = -1;
    private Runnable onComplete;

    public void setTree(Main.TreeType t, Object r) {
        stopTimers();
        type = t;
        root = r;
        highlightKey = -1;
        repaint();
    }

    public void animateStages(Main.TreeType t, List<Object> s, int delay) {
        stopTimers();
        type = t;
        stages = s;
        stageIndex = 0;
        highlightKey = -1;
        stageTimer = new Timer(delay, e -> {
            if (stageIndex >= stages.size()) {
                stageTimer.stop();
            } else {
                root = stages.get(stageIndex++);
                repaint();
            }
        });
        stageTimer.setInitialDelay(delay);
        stageTimer.start();
    }

    public void animateTraversal(
            Main.TreeType t,
            Object r,
            List<Integer> seq,
            int delay,
            Runnable callback
    ) {
        stopTimers();
        type = t;
        root = r;
        travList = seq;
        travIndex = 0;
        highlightKey = -1;
        onComplete = callback;
        travTimer = new Timer(delay, e -> {
            if (travIndex >= travList.size()) {
                travTimer.stop();
                highlightKey = -1;
                repaint();
                if (onComplete != null) onComplete.run();
            } else {
                highlightKey = travList.get(travIndex++);
                repaint();
            }
        });
        travTimer.setInitialDelay(delay);
        travTimer.start();
    }

    private void stopTimers() {
        if (stageTimer != null && stageTimer.isRunning()) stageTimer.stop();
        if (travTimer  != null && travTimer.isRunning())  travTimer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (root == null) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2));
        int w = getWidth();
        switch (type) {
            case BST:
                drawCircleTree((BST.Node) root, w/2, 60, w/4, g2, new Color(50,50,50));
                break;
            case AVL:
                drawCircleTree((AVLTree.Node) root, w/2, 60, w/4, g2, new Color(34,139,34));
                break;
            case RBT:
                drawCircleRBT((RBTree.Node) root, w/2, 60, w/4, g2);
                break;
            case BTREE:
                drawBTree((BTree.Node) root, 0, w, 60, g2);
                break;
        }
    }

    private void drawCircleTree(BST.Node n, int x, int y, int xo,
                                Graphics2D g, Color fill) {
        if (n.left != null) {
            int cx = x - xo, cy = y + 60;
            connectCircles(g, x, y, cx, cy, 15);
            drawCircleTree(n.left, cx, cy, xo/2, g, fill);
        }
        if (n.right != null) {
            int cx = x + xo, cy = y + 60;
            connectCircles(g, x, y, cx, cy, 15);
            drawCircleTree(n.right, cx, cy, xo/2, g, fill);
        }
        drawCircleNode(g, x, y, 15, n.key, fill);
    }

    private void drawCircleTree(AVLTree.Node n, int x, int y, int xo,
                                Graphics2D g, Color fill) {
        if (n.left != null) {
            int cx = x - xo, cy = y + 60;
            connectCircles(g, x, y, cx, cy, 15);
            drawCircleTree(n.left, cx, cy, xo/2, g, fill);
        }
        if (n.right != null) {
            int cx = x + xo, cy = y + 60;
            connectCircles(g, x, y, cx, cy, 15);
            drawCircleTree(n.right, cx, cy, xo/2, g, fill);
        }
        drawCircleNode(g, x, y, 15, n.key, fill);
    }

    private void drawCircleNode(Graphics2D g, int x, int y, int r,
                                int key, Color fill) {
        Color bg = (highlightKey == key ? Color.ORANGE : fill);
        g.setColor(bg);
        g.fillOval(x - r, y - r, 2*r, 2*r);
        g.setColor(Color.WHITE);
        g.drawOval(x - r, y - r, 2*r, 2*r);
        String s = String.valueOf(key);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s,
                x - fm.stringWidth(s)/2,
                y + fm.getAscent()/2 - 2
        );
    }

    private void connectCircles(Graphics2D g,
                                int x1, int y1, int x2, int y2, int r) {
        double dx = x2 - x1, dy = y2 - y1;
        double d  = Math.hypot(dx, dy);
        if (d < 1) return;
        double ux = dx/d, uy = dy/d;
        int sx = (int)Math.round(x1 + ux*r);
        int sy = (int)Math.round(y1 + uy*r);
        int ex = (int)Math.round(x2 - ux*r);
        int ey = (int)Math.round(y2 - uy*r);
        g.setColor(Color.DARK_GRAY);
        g.drawLine(sx, sy, ex, ey);
    }

    private void drawCircleRBT(RBTree.Node n, int x, int y, int xo,
                               Graphics2D g) {
        if (n == RBTree.NIL) return;
        int cy = y + 60;
        if (n.left != RBTree.NIL) {
            int cx = x - xo;
            connectCircles(g, x, y, cx, cy, 15);
            drawCircleRBT(n.left, cx, cy, xo/2, g);
        }
        if (n.right != RBTree.NIL) {
            int cx = x + xo;
            connectCircles(g, x, y, cx, cy, 15);
            drawCircleRBT(n.right, cx, cy, xo/2, g);
        }
        Color fill = n.color ? Color.RED : Color.BLACK;
        drawCircleNode(g, x, y, 15, n.key, fill);
    }

    private int computeSubtreeWidth(BTree.Node n, int slotW) {
        if (n == null) return slotW;
        int total = 0;
        for (int i = 0; i <= n.n; i++) {
            total += computeSubtreeWidth(n.C[i], slotW);
        }
        return Math.max(n.n * slotW, total);
    }

    private void drawBTree(BTree.Node n, int ax, int aw, int y,
                           Graphics2D g) {
        if (n == null) return;
        int slotW = 60, h = 30, vgap = 60;
        int[] cW = new int[n.n + 1], cX = new int[n.n + 1];
        int totalW = 0;
        for (int i = 0; i <= n.n; i++) {
            cW[i] = computeSubtreeWidth(n.C[i], slotW);
            totalW += cW[i];
        }
        int startX = ax + (aw - totalW)/2;
        for (int i = 0, x = startX; i <= n.n; i++) {
            cX[i] = x + cW[i]/2;
            x += cW[i];
        }
        int boxW = n.n * slotW;
        int left  = cX[0], right = cX[n.n];
        int center= (left + right)/2;
        int boxX  = center - boxW/2;
        g.setColor(Color.WHITE);
        g.fillRect(boxX, y, boxW, h);
        for (int i = 0; i < n.n; i++) {
            if (highlightKey == n.keys[i]) {
                g.setColor(Color.YELLOW);
                g.fillRect(boxX + i*slotW, y, slotW, h);
            }
        }
        g.setColor(Color.BLACK);
        g.drawRect(boxX, y, boxW, h);
        for (int i = 1; i < n.n; i++) {
            int sx = boxX + i*slotW;
            g.drawLine(sx, y, sx, y + h);
        }
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.BLACK);
        for (int i = 0; i < n.n; i++) {
            String s = String.valueOf(n.keys[i]);
            int tx = boxX + i*slotW + (slotW - fm.stringWidth(s))/2;
            int ty = y + (h - fm.getHeight())/2 + fm.getAscent();
            g.drawString(s, tx, ty);
        }
        if (!n.leaf) {
            for (int i = 0; i <= n.n; i++) {
                if (n.C[i] != null) {
                    int px = boxX + i*slotW;
                    connectCircles(g,
                            px, y + h,
                            cX[i], y + h + vgap,
                            slotW/4
                    );
                    drawBTree(n.C[i],
                            startX + sum(cW,0,i),
                            cW[i],
                            y + h + vgap,
                            g);
                }
            }
        }
    }

    private int sum(int[] arr, int start, int end) {
        int s = 0;
        for (int i = start; i < end; i++) s += arr[i];
        return s;
    }
}
