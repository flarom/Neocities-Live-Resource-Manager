import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static String sessionName;
    private static final String LOG_DIR = "logs";

    public static void createLogDirectory() {
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }

    public static void createSession() {
        createLogDirectory();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        sessionName = now.format(formatter);
        
        File sessionDir = new File(LOG_DIR, sessionName);
        if (!sessionDir.exists()) {
            sessionDir.mkdirs();
        }
    }

    public static void createEventLogFile() {
        File logFile = new File(LOG_DIR + "/" + sessionName, sessionName + ".log");
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error creating log file: " + e.getMessage());
        }
    }

    public static void logEvent(String event) {
        if (Settings.getSettingBool("log_events", false) == false) return;
        logToFile(sessionName + ".log", getSystime() + event);
    }

    public static void logFile(String filePath, String fileExtension) {
        if (Settings.getSettingBool("log_events", false) == false) return;
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + fileExtension;
        copyBinaryFile(filePath, LOG_DIR + "/" + sessionName + "/" + fileName);
    }

    private static void logToFile(String fileName, String content) {
        File file = new File(LOG_DIR + "/" + sessionName, fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(content);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to log file: " + e.getMessage());
        }
    }

    private static void copyBinaryFile(String sourcePath, String destPath) {
        File sourceFile = new File(sourcePath);
        File destFile = new File(destPath);

        try (FileInputStream inputStream = new FileInputStream(sourceFile);
             FileOutputStream outputStream = new FileOutputStream(destFile)) {

            byte[] buffer = new byte[4096]; // Buffer de 4KB
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            System.out.println("Error copying file: " + e.getMessage());
        }
    }

    public static String getSystime(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedNow = now.format(formatter);
        return "[" + formattedNow + "] ";
    }
}
