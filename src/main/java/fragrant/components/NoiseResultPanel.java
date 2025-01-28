package fragrant.components;

import javax.swing.plaf.basic.BasicMenuItemUI;
import javax.swing.*;

import fragrant.memory.SeedMemoryStorage;
import fragrant.settings.AppSettings;
import fragrant.memory.SeedMemory;

import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class NoiseResultPanel extends JPanel {
    private final DefaultListModel<SeedMemory> seedListModel;
    private final JList<SeedMemory> seedList;
    private final JPopupMenu popupMenu;
    private final JTextField descriptionField;
    private final BlockingQueue<SeedMemory> resultQueue;
    private volatile boolean isProcessing = false;
    private static final int SAVE_INTERVAL = 5000;
    private long lastSaveTime = 0;
    private static final int MAX_BATCH_SIZE = 100;

    public NoiseResultPanel() {
        seedListModel = new DefaultListModel<>();
        resultQueue = new LinkedBlockingQueue<>(1000);
        startResultProcessor();

        setLayout(new BorderLayout());
        seedList = new JList<>(seedListModel);
        seedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setBorder(BorderFactory.createTitledBorder("Search Results"));

        SeedMemoryStorage.ensureStorageDirectory();
        
        loadSavedMemories();

        JPanel inputPanel = new JPanel(new BorderLayout());
        descriptionField = new JTextField();
        inputPanel.add(new JLabel("Description: "), BorderLayout.WEST);
        inputPanel.add(descriptionField, BorderLayout.CENTER);
        
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(seedList), BorderLayout.CENTER);
        
        popupMenu = createPopupMenu();
        setupMouseListener();

        Runtime.getRuntime().addShutdownHook(new Thread(this::saveMemories));
    }

    private void loadSavedMemories() {
        List<SeedMemory> memories = SeedMemoryStorage.loadMemories();
        memories.forEach(seedListModel::addElement);
    }

    private void saveMemories() {
        List<SeedMemory> memories = new ArrayList<>(seedListModel.size());
        for (int i = 0; i < seedListModel.size(); i++) {
            memories.add(seedListModel.getElementAt(i));
        }
        SeedMemoryStorage.saveMemories(memories);
    }

    private void setupMouseListener() {
        seedList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
        });
    }

    public void updatePopupMenuTheme(boolean isDark) {
        Color bgColor = isDark ? new Color(43, 43, 43) : new Color(240, 240, 240);
        Color fgColor = isDark ? new Color(200, 200, 200) : new Color(0, 0, 0);
        
        popupMenu.setBackground(bgColor);
        popupMenu.setForeground(fgColor);
        
        for (Component item : popupMenu.getComponents()) {
            if (item instanceof JMenuItem) {
                JMenuItem menuItem = (JMenuItem) item;
                menuItem.setBackground(bgColor);
                menuItem.setForeground(fgColor);
                menuItem.setUI(new BasicMenuItemUI() {
                    @Override
                    protected void installDefaults() {
                        super.installDefaults();
                        selectionBackground = isDark ? new Color(70, 70, 70) : new Color(200, 200, 200);
                        selectionForeground = fgColor;
                    }
                });
            }
        }
    }

    public void clearResults() {
        seedListModel.clear();
        saveMemories();
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem copySelected = new JMenuItem("Copy Selected");
        copySelected.addActionListener(e -> copySelectedSeeds());
        
        JMenuItem copyAll = new JMenuItem("Copy All");
        copyAll.addActionListener(e -> copyAllSeeds());
        
        JMenuItem editDesc = new JMenuItem("Edit Description");
        editDesc.addActionListener(e -> editDescription());
        
        JMenuItem deleteSelected = new JMenuItem("Delete Selected");
        deleteSelected.addActionListener(e -> deleteSelectedSeeds());
        
        JMenuItem clearAll = new JMenuItem("Clear All");
        clearAll.addActionListener(e -> seedListModel.clear());
        
        menu.add(copySelected);
        menu.add(copyAll);
        menu.addSeparator();
        menu.add(editDesc);
        menu.add(deleteSelected);
        menu.add(clearAll);
        
        return menu;
    }

    private void showPopup(MouseEvent e) {
        if (seedList.getSelectedIndices().length == 0) {
            int index = seedList.locationToIndex(e.getPoint());
            if (index != -1) {
                seedList.setSelectedIndex(index);
            }
        }
        popupMenu.show(seedList, e.getX(), e.getY());
    }

    private void copySelectedSeeds() {
        List<SeedMemory> selectedMemories = seedList.getSelectedValuesList();
        String text = selectedMemories.stream()
            .map(SeedMemory::toString)
            .collect(Collectors.joining("\n"));
        StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }

    private void copyAllSeeds() {
        String text = IntStream.range(0, seedListModel.size())
            .mapToObj(seedListModel::get)
            .map(SeedMemory::toString)
            .collect(Collectors.joining("\n"));
        StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }

    private void editDescription() {
        int selectedIndex = seedList.getSelectedIndex();
        if (selectedIndex == -1) return;
        
        SeedMemory memory = seedListModel.getElementAt(selectedIndex);
        String newDescription = JOptionPane.showInputDialog(
            this,
            "Enter new description:",
            memory.getDescription()
        );
        
        if (newDescription != null) {
            seedListModel.setElementAt(
                new SeedMemory(memory.getSeed(), newDescription),
                selectedIndex
            );
            saveMemories();
        }
    }

    private void deleteSelectedSeeds() {
        int[] selectedIndices = seedList.getSelectedIndices();
        for (int i = selectedIndices.length - 1; i >= 0; i--) {
            seedListModel.remove(selectedIndices[i]);
        }
        saveMemories();
    }

    public void setHeightCheckerPanel(HeightCheckerPanel heightCheckerPanel) {
        seedList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                SeedMemory selected = seedList.getSelectedValue();
                if (selected != null) {
                    heightCheckerPanel.setSeed(selected.getSeed());
                }
            }
        });
    }

    public DefaultListModel<SeedMemory> getSeedListModel() {
        return seedListModel;
    }

    private void startResultProcessor() {
        Thread processor = new Thread(() -> {
            List<SeedMemory> currentBatch = new ArrayList<>();
            
            while (true) {
                try {
                    if (!isProcessing) {
                        Thread.sleep(100);
                        continue;
                    }
                    
                    SeedMemory seed = resultQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (seed != null) {
                        currentBatch.add(seed);
                        
                        if (currentBatch.size() >= MAX_BATCH_SIZE || 
                            (currentBatch.size() > 0 && resultQueue.isEmpty())) {
                            
                            final List<SeedMemory> batchToAdd = new ArrayList<>(currentBatch);
                            SwingUtilities.invokeLater(() -> {
                                if (seedListModel.size() < getMaxResults()) {
                                    for (SeedMemory mem : batchToAdd) {
                                        seedListModel.addElement(mem);
                                    }
                                    
                                    long currentTime = System.currentTimeMillis();
                                    if (currentTime - lastSaveTime > SAVE_INTERVAL) {
                                        saveMemories();
                                        lastSaveTime = currentTime;
                                    }
                                    
                                    if (seedListModel.size() >= getMaxResults()) {
                                        stopProcessing();
                                    }
                                }
                            });
                            currentBatch.clear();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        processor.setDaemon(true);
        processor.start();
    }
    
    private int getMaxResults() {
        return AppSettings.getMaxSeeds();
    }
    
    public void startProcessing() {
        isProcessing = true;
    }
    
    public void stopProcessing() {
        isProcessing = false;
        saveMemories();

        if (seedListModel.size() >= getMaxResults()) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                    "Maximum result count (" + getMaxResults() + ") reached. Search stopped.",
                    "Search Limit",
                    JOptionPane.WARNING_MESSAGE);
            });
        }
    }

    public void addSeed(long seed) {
        if (seedListModel.size() >= getMaxResults()) {
            return;
        }
        
        String description = descriptionField.getText().trim();
        try {
            resultQueue.offer(new SeedMemory(seed, description), 50, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
