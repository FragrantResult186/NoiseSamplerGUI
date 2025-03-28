package fragrant.search;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.SpinnerNumberModel;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.BoxLayout;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fragrant.components.SearchPanel;

import nl.jellejurre.seedchecker.*;
import org.jetbrains.annotations.NotNull;

public class HeightSearchCondition extends JPanel {
    private final JSpinner minXSpinner, maxXSpinner, minZSpinner, maxZSpinner, minHeightSpinner, maxHeightSpinner;
    private final SearchPanel parentPanel;
    private static final int TARGET_LEVEL = 5;

    public HeightSearchCondition(SearchPanel parent) {
        this.parentPanel = parent;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Height Condition"));

        minXSpinner = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        maxXSpinner = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        minZSpinner = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        maxZSpinner = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        minHeightSpinner = new JSpinner(new SpinnerNumberModel(-64, -64, 256, 1));
        maxHeightSpinner = new JSpinner(new SpinnerNumberModel(256, -64, 256, 1));

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        addComponent(fieldsPanel, "Min X:", minXSpinner, gbc, 0);
        addComponent(fieldsPanel, "Max X:", maxXSpinner, gbc, 1);
        addComponent(fieldsPanel, "Min Z:", minZSpinner, gbc, 2);
        addComponent(fieldsPanel, "Max Z:", maxZSpinner, gbc, 3);
        addComponent(fieldsPanel, "Min Height:", minHeightSpinner, gbc, 4);
        addComponent(fieldsPanel, "Max Height:", maxHeightSpinner, gbc, 5);
        JPanel buttonPanel = getJPanel();

        add(fieldsPanel);
        add(buttonPanel);
    }

    private @NotNull JPanel getJPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton upButton = new JButton("↑");
        JButton downButton = new JButton("↓");
        JButton deleteButton = new JButton("Delete");

        upButton.addActionListener(e -> parentPanel.moveConditionUp(this));
        downButton.addActionListener(e -> parentPanel.moveConditionDown(this));
        deleteButton.addActionListener(e -> parentPanel.removeHeightCondition(this));

        buttonPanel.add(upButton);
        buttonPanel.add(downButton);
        buttonPanel.add(deleteButton);
        return buttonPanel;
    }

    private void addComponent(JPanel panel, String label, JComponent component, GridBagConstraints gbc, int gridy) {
        gbc.gridy = gridy;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(component, gbc);
    }

    public boolean checkCondition(SeedChecker checker) {
        SeedChecker optimizedChecker = new SeedChecker(
            checker.getSeed(),
            TARGET_LEVEL
        );
        
        int minX = (Integer) minXSpinner.getValue();
        int maxX = (Integer) maxXSpinner.getValue();
        int minZ = (Integer) minZSpinner.getValue();
        int maxZ = (Integer) maxZSpinner.getValue();
        int minHeight = (Integer) minHeightSpinner.getValue();
        int maxHeight = (Integer) maxHeightSpinner.getValue();

        if (minX > maxX || minZ > maxZ) {
            return false;
        }

        try {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    int highestY = -64;
                    for (int y = 256; y >= -64; y--) {
                        var blockState = optimizedChecker.getBlockState(x, y, z);
                        if (!blockState.isAir()) {
                            highestY = y;
                            break;
                        }
                    }

                    if (highestY >= minHeight && highestY <= maxHeight) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            optimizedChecker.clearMemory();
        }

        return false;
    }

    public JSpinner getMinXSpinner() { return minXSpinner; }
    public JSpinner getMaxXSpinner() { return maxXSpinner; }
    public JSpinner getMinZSpinner() { return minZSpinner; }
    public JSpinner getMaxZSpinner() { return maxZSpinner; }
    public JSpinner getMinHeightSpinner() { return minHeightSpinner; }
    public JSpinner getMaxHeightSpinner() { return maxHeightSpinner; }
}