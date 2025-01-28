package fragrant.search;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.util.Map;

import javax.swing.SpinnerNumberModel;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fragrant.components.NoiseSearchPanel;

import nl.kallestruik.noisesampler.NoiseSampler;
import nl.kallestruik.noisesampler.NoiseType;

public class NoiseSearchCondition extends JPanel {
    private final JComboBox<NoiseType> noiseTypeCombo;
    private final JSpinner minXSpinner, maxXSpinner, minYSpinner, maxYSpinner, minZSpinner, maxZSpinner;
    private final JSpinner thresholdSpinner;
    private final JComboBox<String> conditionTypeCombo;
    private final JComboBox<String> thresholdConditionCombo;

    public NoiseType getNoiseType() {
        return (NoiseType) noiseTypeCombo.getSelectedItem();
    }
    
    public int getMinX() {
        return (Integer) minXSpinner.getValue();
    }
    
    public int getMaxX() {
        return (Integer) maxXSpinner.getValue();
    }
    
    public int getMinY() {
        return (Integer) minYSpinner.getValue();
    }
    
    public int getMaxY() {
        return (Integer) maxYSpinner.getValue();
    }
    
    public int getMinZ() {
        return (Integer) minZSpinner.getValue();
    }
    
    public int getMaxZ() {
        return (Integer) maxZSpinner.getValue();
    }
    
    public double getThreshold() {
        return (Double) thresholdSpinner.getValue();
    }
    
    public int getThresholdConditionIndex() {
        return thresholdConditionCombo.getSelectedIndex();
    }
    
    public int getConditionTypeIndex() {
        return conditionTypeCombo.getSelectedIndex();
    }
    
    public void setValues(NoiseType noiseType, int minX, int maxX, int minY, int maxY,
                         int minZ, int maxZ, double threshold, 
                         int thresholdConditionIndex, int conditionTypeIndex) {
        noiseTypeCombo.setSelectedItem(noiseType);
        minXSpinner.setValue(minX);
        maxXSpinner.setValue(maxX);
        minYSpinner.setValue(minY);
        maxYSpinner.setValue(maxY);
        minZSpinner.setValue(minZ);
        maxZSpinner.setValue(maxZ);
        thresholdSpinner.setValue(threshold);
        thresholdConditionCombo.setSelectedIndex(thresholdConditionIndex);
        conditionTypeCombo.setSelectedIndex(conditionTypeIndex);
    }
    
    public NoiseSearchCondition(NoiseSearchPanel parentPanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Noise Condition"));

        noiseTypeCombo = new JComboBox<>(NoiseType.values());

        minXSpinner = new JSpinner(new SpinnerNumberModel(0, -1000, 1000, 1));
        maxXSpinner = new JSpinner(new SpinnerNumberModel(0, -1000, 1000, 1));
        minYSpinner = new JSpinner(new SpinnerNumberModel(0, -1000, 1000, 1));
        maxYSpinner = new JSpinner(new SpinnerNumberModel(0, -1000, 1000, 1));
        minZSpinner = new JSpinner(new SpinnerNumberModel(0, -1000, 1000, 1));
        maxZSpinner = new JSpinner(new SpinnerNumberModel(0, -1000, 1000, 1));

        thresholdSpinner = new JSpinner(new SpinnerNumberModel(0.0, -1000.0, 1000.0, 0.1));

        thresholdConditionCombo = new JComboBox<>(new String[]{"Threshold or above", "Threshold or below"});
        conditionTypeCombo = new JComboBox<>(new String[]{"All coordinates must match", "At least one coordinate must match"});

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> parentPanel.removeCondition(this));

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        addComponent(fieldsPanel, "Noise Type:", noiseTypeCombo, gbc, 0);
        addComponent(fieldsPanel, "Min X:", minXSpinner, gbc, 1);
        addComponent(fieldsPanel, "Max X:", maxXSpinner, gbc, 2);
        addComponent(fieldsPanel, "Min Y:", minYSpinner, gbc, 3);
        addComponent(fieldsPanel, "Max Y:", maxYSpinner, gbc, 4);
        addComponent(fieldsPanel, "Min Z:", minZSpinner, gbc, 5);
        addComponent(fieldsPanel, "Max Z:", maxZSpinner, gbc, 6);
        addComponent(fieldsPanel, "Threshold:", thresholdSpinner, gbc, 7);
        addComponent(fieldsPanel, "Threshold Condition:", thresholdConditionCombo, gbc, 8);
        addComponent(fieldsPanel, "Condition:", conditionTypeCombo, gbc, 9);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(deleteButton);

        add(fieldsPanel);
        add(buttonPanel);
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

    public boolean checkCondition(NoiseSampler sampler) {
        NoiseType noiseType = (NoiseType) noiseTypeCombo.getSelectedItem();
        double threshold = (Double) thresholdSpinner.getValue();
        boolean requireAll = conditionTypeCombo.getSelectedIndex() == 0;
        boolean isGreaterOrEqual = thresholdConditionCombo.getSelectedIndex() == 0;

        int minX = (Integer) minXSpinner.getValue();
        int maxX = (Integer) maxXSpinner.getValue();
        int minY = (Integer) minYSpinner.getValue();
        int maxY = (Integer) maxYSpinner.getValue();
        int minZ = (Integer) minZSpinner.getValue();
        int maxZ = (Integer) maxZSpinner.getValue();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Map<NoiseType, Double> noiseValues = sampler.queryNoise(x, y, z, noiseType);
                    double value = noiseValues.get(noiseType);

                    boolean meetsThreshold = isGreaterOrEqual ? value >= threshold : value <= threshold;

                    if (requireAll) {
                        if (!meetsThreshold) {
                            return false;
                        }
                    } else {
                        if (meetsThreshold) {
                            return true;
                        }
                    }
                }
            }
        }

        return requireAll;
    }
}
