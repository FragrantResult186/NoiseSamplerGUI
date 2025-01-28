package fragrant.settings;

import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends JDialog {
    private JSpinner fontSizeSpinner;
    private JToggleButton lightThemeButton;
    private JToggleButton darkThemeButton;
    
    public SettingsDialog(Frame owner) {
        super(owner, "Settings", true);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        gbc.gridy = 0;
        mainPanel.add(createThemePanel(), gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(Box.createVerticalStrut(25), gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 20, 20, 20);
        mainPanel.add(createFontPanel(), gbc);
        
        JPanel buttonPanel = createButtonPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        loadCurrentSettings();
        setMinimumSize(new Dimension(300, 250));
        pack();
        setLocationRelativeTo(owner);
    }
    
    private JPanel createThemePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Theme"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        ButtonGroup themeGroup = new ButtonGroup();
        lightThemeButton = new JToggleButton("Light");
        darkThemeButton = new JToggleButton("Dark");
        
        Dimension buttonSize = new Dimension(80, 30);
        lightThemeButton.setPreferredSize(buttonSize);
        darkThemeButton.setPreferredSize(buttonSize);
        
        themeGroup.add(lightThemeButton);
        themeGroup.add(darkThemeButton);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 10);
        panel.add(lightThemeButton, gbc);
        
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(darkThemeButton, gbc);
        
        return panel;
    }
    
    private JPanel createFontPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Font Size"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(
            AppSettings.getFontSize(), 8, 24, 1));
        fontSizeSpinner.setPreferredSize(new Dimension(80, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        panel.add(fontSizeSpinner, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        Dimension buttonSize = new Dimension(80, 30);
        saveButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);
        
        saveButton.addActionListener(e -> {
            saveSettings();
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(saveButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void loadCurrentSettings() {
        String currentTheme = AppSettings.getTheme();
        if ("dark".equals(currentTheme)) {
            darkThemeButton.setSelected(true);
        } else {
            lightThemeButton.setSelected(true);
        }
    }
    
    private void saveSettings() {
        AppSettings.setTheme(darkThemeButton.isSelected() ? "dark" : "light");
        AppSettings.setFontSize((Integer)fontSizeSpinner.getValue());
    }
}