package fragrant.settings;

import java.util.prefs.Preferences;
import javax.swing.*;
import java.awt.*;

public class AppSettings {
    private static final Preferences PREFS = Preferences.userNodeForPackage(AppSettings.class);
    private static final String FONT_SIZE = "fontSize";
    private static final String THEME = "theme";
    
    public static int getFontSize() {
        return PREFS.getInt(FONT_SIZE, 12);
    }
    
    public static void setFontSize(int size) {
        PREFS.putInt(FONT_SIZE, size);
        updateGlobalFontSize(size);
    }
    
    public static String getTheme() {
        return PREFS.get(THEME, "light");
    }
    
    public static void setTheme(String theme) {
        PREFS.put(THEME, theme);
    }
    
    private static void updateGlobalFontSize(int size) {
        Font baseFont = new Font(Font.SANS_SERIF, Font.PLAIN, size);
        Font boldFont = baseFont.deriveFont(Font.BOLD);
        
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        defaults.put("defaultFont", baseFont);
        
        String[] components = {
            "Label", "TextField", "TextArea", "Button", 
            "ComboBox", "CheckBox", "RadioButton"
        };
        
        for (String component : components) {
            defaults.put(component + ".font", baseFont);
        }
        
        defaults.put("TitledBorder.font", boldFont);
    }
}