package fragrant.components.mapviewer.ui;

import javax.swing.*;
import java.awt.*;

public class LoadingSpinner extends JPanel {
    private float angle = 0;
    private final Timer timer;

    public LoadingSpinner() {
        timer = new Timer(50, e -> {
            angle += 0.3F;
            if (angle >= 2 * Math.PI) {
                angle = 0;
            }
            repaint();
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        timer.start();
    }

    @Override
    public void removeNotify() {
        timer.stop();
        super.removeNotify();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 4;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        g2d.rotate(angle, getWidth() / 2.0, getHeight() / 2.0);

        for (int i = 0; i < 8; i++) {
            float alpha = Math.max(0.2f, 1.0f - (i * 0.1f));
            g2d.setColor(new Color(0, 0, 0, (int) (alpha * 255)));
            g2d.fillOval(x + size / 2 + (int) ((double) size / 3 * Math.cos(i * Math.PI / 4)),
                    y + size / 2 + (int) ((double) size / 3 * Math.sin(i * Math.PI / 4)),
                    size / 8, size / 8);
        }

        g2d.dispose();
    }
}