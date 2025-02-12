package fragrant;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.Duration;
import java.awt.Component;
import java.awt.Container;
import java.time.Instant;
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

import fragrant.components.mapviewer.MapViewerPanel;
import fragrant.components.HeightCheckerPanel;
import fragrant.memory.SearchConditionStorage;
import fragrant.components.NoiseResultPanel;
import fragrant.components.NoiseSearchPanel;
import fragrant.settings.SettingsDialog;
import fragrant.settings.AppSettings;

public class MainUI extends JFrame {
    private final Timer statusUpdateTimer;

    private final NoiseSearchPanel searchPanel;
    private final NoiseResultPanel resultPanel;
    private HeightCheckerPanel heightCheckerPanel;
    private final MapViewerPanel mapViewerPanel;

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

        searchPanel = new NoiseSearchPanel(this);
        resultPanel = new NoiseResultPanel();
        heightCheckerPanel = new HeightCheckerPanel();
        statusPanel = createStatusPanel();

        resultPanel.setHeightCheckerPanel(heightCheckerPanel);

        JSplitPane rightSplitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                resultPanel,
                heightCheckerPanel);
        rightSplitPane.setResizeWeight(0.7);

        mapViewerPanel = new MapViewerPanel();

        JTabbedPane rightTabbedPane = new JTabbedPane();
        rightTabbedPane.addTab("Results", resultPanel);
        rightTabbedPane.addTab("Height Checker", heightCheckerPanel);
        rightTabbedPane.addTab("Map Viewer", mapViewerPanel);

        JSplitPane mainSplitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                searchPanel,
                rightTabbedPane);
        mainSplitPane.setResizeWeight(0.5);

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
            resultPanel.updatePopupMenuTheme(isDark);
        } catch (Exception ex) {
            ex.printStackTrace();
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

    public void updateMapViewer(long seed) {
        mapViewerPanel.updateSeed(seed);
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