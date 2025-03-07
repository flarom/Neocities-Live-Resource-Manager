import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
    private static final String CONFIG_FILE = "config.properties";

    /**
     * Get a value from a setting as string
     * @param key Setting name
     * @return Setting value
     */
    public static String getSetting(String key, String fallback) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            return props.getProperty(key, "");
        } catch (IOException e) {
            return fallback;
        }
    }

    /**
     * Set a value to a setting
     * @param key Setting name
     * @param value Setting value
     */
    public static void setSetting(String key, String value) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
        } catch (IOException ignored) {}

        props.setProperty(key, value);

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Neocities Live Resource Manager");
        } catch (IOException e) {
            System.out.println("Error saving settings: " + e.getMessage());
        }
    }

    /**
     * Get a value from a setting as boolean
     * @param key Setting name
     * @return Setting value
     */
    public static boolean getSettingBool(String key, boolean fallback) {
        if (getSetting(key, fallback ? "1" : "0").equals("1")) {
            return true;
        }
        return false;
    }

    /**
     * Get a value from as setting as integer
     * @param key Setting name
     * @return Setting value
     */
    public static int getSettingInt(String key, int fallback) {
        try {
            int valueI = Integer.parseInt(getSetting(key, Integer.toString(fallback)));
            return valueI;
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * Set a value to a setting as boolean
     * @param key Setting name
     * @param value Setting value
     */
    public static void setSetting(String key, boolean value) {
        String valueS = "0";

        if(value) valueS = "1";

        setSetting(key, valueS);
    }

    /**
     * Set a value to a setting as integer
     * @param key Setting name
     * @param value Setting value
     */
    public static void setSetting(String key, int value) {
        setSetting(key, Integer.toString(value));
    }
}
