package fragrant;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.Duration;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.time.Instant;
import java.awt.Color;
import java.awt.Font;

import javax.swing.SwingUtilities;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;

import fragrant.components.mapviewer.core.MapViewerPanel;
import fragrant.memory.SearchConditionStorage;
import fragrant.components.NoiseResultPanel;
import fragrant.components.SearchPanel;
import fragrant.settings.SettingsDialog;
import fragrant.settings.AppSettings;

public class MainUI extends JFrame {
    private final Timer statusUpdateTimer;

    private final SearchPanel searchPanel;
    private final NoiseResultPanel resultPanel;

    private final JPanel statusPanel;
    private JLabel seedCountLabel;
    private JLabel speedLabel;
    private JLabel elapsedTimeLabel;

    private long processedSeeds = 0;
    private Instant startTime;

    public MainUI() {
        setTitle("Noise Sampler GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        searchPanel = new SearchPanel(this);
        resultPanel = new NoiseResultPanel();
        statusPanel = createStatusPanel();
        MapViewerPanel mapViewerPanel = new MapViewerPanel();

        JTabbedPane rightTabbedPane = new JTabbedPane();
        rightTabbedPane.setOpaque(true);

        rightTabbedPane.addTab("Results", resultPanel);
        rightTabbedPane.addTab("Map Viewer", mapViewerPanel);

        JSplitPane mainSplitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                searchPanel,
                rightTabbedPane);
        mainSplitPane.setOpaque(true);
        mainSplitPane.setResizeWeight(0.5);
        mainSplitPane.setContinuousLayout(true);
        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setDividerSize(8);

        UIManager.put("SplitPane.oneTouchButtonSize", 8);
        UIManager.put("SplitPane.oneTouchButtonOffset", 3);
        UIManager.put("SplitPane.centerOneTouchButtons", true);

        searchPanel.setMinimumSize(new Dimension(100, 0));
        rightTabbedPane.setMinimumSize(new Dimension(100, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(e -> showSettings());
        topPanel.add(settingsButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(mainSplitPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        statusUpdateTimer = new Timer(1000, e -> updateStatus());

        applySettings();

        pack();
        setSize(1200, 800);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                long startSeed = 0;
                try {
                    startSeed = Long.parseLong(searchPanel.getStartSeedField().getText());
                } catch (NumberFormatException ignored) {
                }
                SearchConditionStorage.saveDefaultConditions(
                        MainUI.this,
                        searchPanel,
                        searchPanel.getSearchConditions(),
                        searchPanel.getHeightConditions(),
                        searchPanel.getBiomeConditions(),
                        startSeed);
            }
        });
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        seedCountLabel = new JLabel("Seeds: 0");
        speedLabel = new JLabel("Speed: 0 seeds/s");
        elapsedTimeLabel = new JLabel("Time: 00:00:00");

        panel.add(seedCountLabel);
        panel.add(speedLabel);
        panel.add(elapsedTimeLabel);

        return panel;
    }

    private void showSettings() {
        SettingsDialog dialog = new SettingsDialog(this);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                applySettings();
            }
        });
        dialog.setVisible(true);
    }

    private void applySettings() {
        String theme = AppSettings.getTheme();
        boolean isDark = theme.equals("dark") ||
                (theme.equals("system") &&
                        UIManager.getSystemLookAndFeelClassName().contains("Dark"));

        applyTheme(isDark);
        updateGlobalFontSize(AppSettings.getFontSize());
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void applyTheme(boolean isDark) {
        try {
            UIManager.setLookAndFeel(isDark ? new FlatDarkLaf() : new FlatLightLaf());
            
            Color splitPaneBackground = isDark ? new Color(60, 63, 65) : new Color(230, 230, 230);
            Color dividerDraggingColor = isDark ? new Color(88, 91, 93) : new Color(180, 180, 180);
            Color tabBackground = isDark ? new Color(43, 43, 43) : new Color(242, 242, 242);
            
            UIManager.put("SplitPane.background", splitPaneBackground);
            UIManager.put("SplitPaneDivider.draggingColor", dividerDraggingColor);
            UIManager.put("TabbedPane.background", tabBackground);
            UIManager.put("TabbedPane.selectedBackground", tabBackground);
            UIManager.put("TabbedPane.unselectedBackground", tabBackground);
            UIManager.put("TabbedPane.contentAreaColor", tabBackground);
            
            resultPanel.updatePopupMenuTheme(isDark);
            
            SwingUtilities.invokeLater(() -> updateComponentColors(this, isDark));
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void updateComponentColors(Container container, boolean isDark) {
        Color background = isDark ? new Color(60, 63, 65) : new Color(230, 230, 230);
        
        for (Component comp : container.getComponents()) {
            if (comp instanceof JSplitPane) {
                comp.setBackground(background);
            }
            if (comp instanceof JTabbedPane) {
                comp.setBackground(isDark ? new Color(43, 43, 43) : new Color(242, 242, 242));
            }
            if (comp instanceof Container) {
                updateComponentColors((Container) comp, isDark);
            }
        }
    }

    private void updateGlobalFontSize(int size) {
        Font baseFont = new Font(Font.SANS_SERIF, Font.PLAIN, size);
        UIManager.put("defaultFont", baseFont);

        Font boldFont = baseFont.deriveFont(Font.BOLD);

        UIManager.put("Label.font", baseFont);
        UIManager.put("TextField.font", baseFont);
        UIManager.put("TextArea.font", baseFont);
        UIManager.put("Button.font", baseFont);
        UIManager.put("ComboBox.font", baseFont);
        UIManager.put("CheckBox.font", baseFont);
        UIManager.put("RadioButton.font", baseFont);
        UIManager.put("TitledBorder.font", boldFont);

        UIManager.put("InternalFrame.titleFont", baseFont);
        UIManager.put("MenuBar.font", baseFont);
        UIManager.put("MenuItem.font", baseFont);
        UIManager.put("Menu.font", baseFont);

        UIManager.put("FrameTitle.font", null);
        UIManager.put("InternalFrameTitlePane.font", null);

        SwingUtilities.invokeLater(() -> {
            updateComponentFonts(searchPanel, baseFont);
            updateComponentFonts(resultPanel, baseFont);
            updateComponentFonts(statusPanel, baseFont);
        });
    }

    private void updateComponentFonts(Container container, Font font) {
        for (Component comp : container.getComponents()) {
            if (!(comp instanceof JFrame)) {
                comp.setFont(font);
            }
            if (comp instanceof Container) {
                updateComponentFonts((Container) comp, font);
            }
        }
    }

    public void startSearch() {
        processedSeeds = 0;
        startTime = Instant.now();
        statusUpdateTimer.start();
    }

    public void stopSearch() {
        statusUpdateTimer.stop();
    }

    public void incrementProcessedSeeds() {
        processedSeeds++;
    }

    private void updateStatus() {
        if (startTime != null) {
            Duration elapsed = Duration.between(startTime, Instant.now());
            long seconds = elapsed.getSeconds();
            double speed = seconds > 0 ? (double) processedSeeds / seconds : 0;

            seedCountLabel.setText(String.format("Seeds: %,d", processedSeeds));
            speedLabel.setText(String.format("Speed: %.1f seeds/s", speed));
            elapsedTimeLabel.setText(String.format("Time: %02d:%02d:%02d",
                    seconds / 3600, (seconds % 3600) / 60, seconds % 60));
        }
    }

    public NoiseResultPanel getResultPanel() {
        return resultPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String theme = AppSettings.getTheme();
            boolean isDark = theme.equals("dark") ||
                    (theme.equals("system") &&
                            UIManager.getSystemLookAndFeelClassName().contains("Dark"));

            try {
                UIManager.setLookAndFeel(isDark ? new FlatDarkLaf() : new FlatLightLaf());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new MainUI().setVisible(true);
        });
    }
}