package fragrant.components.mapviewer.core;

import javax.swing.table.DefaultTableModel;
import java.awt.Dialog.ModalityType;
import java.util.List;
import javax.swing.*;
import java.awt.*;

import net.minecraft.item.ItemStack;

public class Dialog {
    private JDialog chestContentsDialog;
    private JDialog helpDialog;

    public void createHelpDialog(Component parent) {
        helpDialog = new JDialog((Frame) null, "How to Use", ModalityType.MODELESS);
        helpDialog.setSize(400, 500);

        JTextArea helpText = new JTextArea();
        helpText.setEditable(false);
        helpText.setWrapStyleWord(true);
        helpText.setLineWrap(true);
        helpText.setText(
                """
                        Map Viewer Instructions:

                        Basic Controls:
                        - W: Move north
                        - S: Move south
                        - A: Move west
                        - D: Move east
                        - Q: Move up
                        - Z: Move down

                        Mouse Controls:
                        - Mouse hover: Display block information
                        - Click on chest: Show chest contents

                        View Settings:
                        - Zoom: Adjust display size
                        - Depth range: Adjust the visible depth range

                        Seed Operations:
                        1. Enter seed value
                        2. Click the Load button
                        3. Select dimension (world)

                        Other:
                        - Crosshair in the center: Current position
                        - Red cross mark: Spawn point
                        - Coordinate information: Displayed at the top left of the screen""");

        helpDialog.add(new JScrollPane(helpText));
        helpDialog.setLocationRelativeTo(parent);
    }

    public void createChestContentsDialog(Component parent) {
        chestContentsDialog = new JDialog((Frame) null, "Chest Contents", ModalityType.MODELESS);
        chestContentsDialog.setSize(400, 300);
        chestContentsDialog.setLocationRelativeTo(parent);
    }

    public void showChestDialog(int x, int y, int z, List<ItemStack> contents, Component parent) {
        if (contents == null || contents.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "No chest contents found at this location",
                    "Empty Chest",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "Item", "Count" }, 0);

        for (ItemStack item : contents) {
            model.addRow(new Object[] {
                    item.getName().getString(),
                    item.getCount()
            });
        }

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);

        chestContentsDialog.getContentPane().removeAll();
        chestContentsDialog.getContentPane().add(new JScrollPane(table));
        chestContentsDialog.setTitle(String.format("Chest Contents at X:%d Y:%d Z:%d", x, y, z));
        chestContentsDialog.setVisible(true);
    }

    public void showHelpDialog() {
        if (helpDialog == null) return;
        helpDialog.setVisible(true);
    }
}