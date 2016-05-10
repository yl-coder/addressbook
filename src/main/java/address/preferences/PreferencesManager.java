package address.preferences;

import address.events.EventManager;
import address.events.FileNameChangedEvent;

import java.io.File;

/**
 * Manages saving/retrieving of preferences in the registry.
  */
public class PreferencesManager {

    public static final String REGISTER_FILE_PATH = "address-book-filePath1";
    private static PreferencesManager instance;

    private static String appTitle = "";

    public static PreferencesManager getInstance(){
        if (instance == null){
            instance = new PreferencesManager();
        }

        return instance;
    }

    public static void setAppTitle(String appTitle) {
        PreferencesManager.appTitle = appTitle;
    }

    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     *
     */
    public File getPersonFilePath() {
        java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(PreferencesManager.class);
        String filePath = prefs.get(PreferencesManager.appTitle + "/" + REGISTER_FILE_PATH, null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            System.out.println("file path not found ");
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     * @param file the file or null to remove the path
     */
    public void setFilePath(File file) {
        java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(PreferencesManager.class);
        String key = PreferencesManager.appTitle + "/" + REGISTER_FILE_PATH;
        if (file != null) {
            prefs.put(key, file.getPath());
        } else {
            prefs.remove(key);
        }

        EventManager.getInstance().post(new FileNameChangedEvent(file));
    }
}
