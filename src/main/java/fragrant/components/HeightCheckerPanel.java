package fragrant.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;

import nl.jellejurre.seedchecker.*;

public class HeightCheckerPanel extends JPanel {
    private final JTextField seedField;
    private final JTextField xCoordField;
    private final JTextField zCoordField;
    private final JLabel resultLabel;
    private SeedChecker currentChecker;

    public HeightCheckerPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Height Checker"));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Seed:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        seedField = new JTextField(20);
        inputPanel.add(seedField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("X:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        xCoordField = new JTextField(20);
        inputPanel.add(xCoordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Z:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        zCoordField = new JTextField(20);
        inputPanel.add(zCoordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton checkButton = new JButton("Check Height");
        checkButton.addActionListener(e -> checkHeight());
        inputPanel.add(checkButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        resultLabel = new JLabel(" ");
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        inputPanel.add(resultLabel, gbc);

        add(inputPanel, BorderLayout.NORTH);
    }

    private void checkHeight() {
        try {
            long seed = Long.parseLong(seedField.getText().trim());
            int x = Integer.parseInt(xCoordField.getText().trim());
            int z = Integer.parseInt(zCoordField.getText().trim());

            if (currentChecker == null || currentChecker.getSeed() != seed) {
                currentChecker = new SeedChecker(seed);
            }

            int surfaceY = -64;
            boolean foundSurface = false;

            for (int y = 256; y >= -64; y--) {
                if (!currentChecker.getBlockState(x, y, z).isAir()) {
                    surfaceY = y;
                    foundSurface = true;
                    break;
                }
            }

            if (foundSurface) {
                String blockName = currentChecker.getBlockState(x, surfaceY, z).getBlock().toString();
                resultLabel.setText(String.format("Surface Height: Y=%d (%s)", surfaceY, blockName));
            } else {
                resultLabel.setText("No surface found at this location");
            }

        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter valid numbers");
        } catch (Exception e) {
            resultLabel.setText("Error: " + e.getMessage());
        }
    }

    public void setSeed(long seed) {
        seedField.setText(String.valueOf(seed));
    }
}