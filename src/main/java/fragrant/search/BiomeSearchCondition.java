package fragrant.search;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.SpinnerNumberModel;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fragrant.components.SearchPanel;

import nl.jellejurre.biomesampler.minecraft.Biome;
import nl.jellejurre.biomesampler.BiomeSampler;

public class BiomeSearchCondition extends JPanel {
    private final JComboBox<Biome> biomeCombo;
    private final JSpinner minXSpinner, maxXSpinner, minZSpinner, maxZSpinner;
    private final JComboBox<String> conditionTypeCombo;

    public BiomeSearchCondition(SearchPanel parentPanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Biome Condition"));

        biomeCombo = new JComboBox<>(Biome.values());

        minXSpinner = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        maxXSpinner = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        minZSpinner = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        maxZSpinner = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));

        conditionTypeCombo = new JComboBox<>(new String[] { "All coordinates match", "Any coordinate matches" });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton upButton = new JButton("↑");
        JButton downButton = new JButton("↓");
        JButton deleteButton = new JButton("Delete");
        upButton.addActionListener(e -> parentPanel.moveConditionUp(this));
        downButton.addActionListener(e -> parentPanel.moveConditionDown(this));
        deleteButton.addActionListener(e -> parentPanel.removeBiomeCondition(this));

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        addComponent(fieldsPanel, "Biome:", biomeCombo, gbc, 0);
        addComponent(fieldsPanel, "Min X:", minXSpinner, gbc, 1);
        addComponent(fieldsPanel, "Max X:", maxXSpinner, gbc, 2);
        addComponent(fieldsPanel, "Min Z:", minZSpinner, gbc, 3);
        addComponent(fieldsPanel, "Max Z:", maxZSpinner, gbc, 4);
        addComponent(fieldsPanel, "Condition:", conditionTypeCombo, gbc, 5);

        buttonPanel.add(upButton);
        buttonPanel.add(downButton);
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

    public boolean checkCondition(BiomeSampler sampler) {
        Biome targetBiome = (Biome) biomeCombo.getSelectedItem();
        boolean requireAll = conditionTypeCombo.getSelectedIndex() == 0;

        int minX = (Integer) minXSpinner.getValue();
        int maxX = (Integer) maxXSpinner.getValue();
        int minZ = (Integer) minZSpinner.getValue();
        int maxZ = (Integer) maxZSpinner.getValue();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Biome biome = sampler.getBiomeFromBlockPos(x, 0, z);
                boolean matches = biome == targetBiome;

                if (requireAll) {
                    if (!matches) {
                        return false;
                    }
                } else {
                    if (matches) {
                        return true;
                    }
                }
            }

        }

        return requireAll;
    }

    public void setValues(Biome biome, int minX, int maxX, int minZ, int maxZ, int conditionTypeIndex) {
        biomeCombo.setSelectedItem(biome);
        minXSpinner.setValue(minX);
        maxXSpinner.setValue(maxX);
        minZSpinner.setValue(minZ);
        maxZSpinner.setValue(maxZ);
        conditionTypeCombo.setSelectedIndex(conditionTypeIndex);
    }

    public Biome getBiome() { return (Biome) biomeCombo.getSelectedItem(); }
    public int getMinX() { return (Integer) minXSpinner.getValue(); }
    public int getMaxX() { return (Integer) maxXSpinner.getValue(); }
    public int getMinZ() { return (Integer) minZSpinner.getValue(); }
    public int getMaxZ() { return (Integer) maxZSpinner.getValue(); }
    public int getConditionTypeIndex() { return conditionTypeCombo.getSelectedIndex(); }
}