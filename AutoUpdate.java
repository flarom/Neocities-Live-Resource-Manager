import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AutoUpdate {
    private final String user;
    private final String pass;
    private final String srce;
    private final String trgt;
    private String lastHash;

    public AutoUpdate(String user, String pass, String srce, String trgt) {
        this.user = user;
        this.pass = pass;
        this.srce = srce;
        this.trgt = trgt;
        this.lastHash = getFileHash();
        Logger.createSession();
        Logger.createEventLogFile();
    }

    public void startMonitoring() {
        Logger.logEvent("Started monitoring file '" + getFileName(srce) + "' and uploading to '" + trgt + "'");
        System.out.println("\u001B[90m" + "Hit Ctrl+C or end this terminal session to stop monitoring\n" + "\u001B[0m");
        System.out.println(Logger.getSystime() + "Started monitoring file '" + getFileName(srce) +"' and uploading to '" + trgt +"'");
        while (true) {
            try {
                Thread.sleep(Settings.getSettingInt("loop_time", 60000));
                if (checkFileChanged()) {
                    Logger.logEvent("File changed! Uploading...");
                    System.out.println(Logger.getSystime() + "\u001B[32m" + "File changed! Uploading..." + "\u001B[0m");
                    if (upload()) {
                        Logger.logEvent("Upload successful!");
                        System.out.println(Logger.getSystime() + "\u001B[32m" + "Upload successful!" + "\u001B[0m");
                        Logger.logFile(srce, "." + getFileExtension(getFileName(srce)));
                    } else {
                        Logger.logEvent("Upload failed.");
                        System.out.println(Logger.getSystime() + "\u001B[31m" + "Upload failed." + "\u001B[0m");
                    }
                }
            } catch (InterruptedException e) {
                Logger.logEvent("Monitoring stopped.");
                return;
            }
        }
    }

    private boolean checkFileChanged() {
        String currentHash = getFileHash();
        if (currentHash != null && !currentHash.equals(lastHash)) {
            lastHash = currentHash;
            return true;
        }
        return false;
    }

    private String getFileHash() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(Paths.get(srce));
            byte[] digest = md.digest(fileBytes);
            return Base64.getEncoder().encodeToString(digest);
        } catch (IOException | NoSuchAlgorithmException e) {
            Logger.logEvent("Error generating file hash: " + e.getMessage());
            return null;
        }
    }

    private boolean upload() {
        try {
            File file = new File(srce);
            if (!file.exists()) return false;

            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            URL url = new URL("https://neocities.org/api/upload");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).getBytes()));
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (OutputStream output = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)) {

                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"").append(trgt).append("\"; filename=\"").append(file.getName()).append("\"\r\n");
                writer.append("Content-Type: application/octet-stream\r\n\r\n").flush();

                Files.copy(file.toPath(), output);
                output.flush();

                writer.append("\r\n").append("--").append(boundary).append("--").append("\r\n").flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return true;
            } else {
                Logger.logEvent("Upload failed: Response code " + responseCode);
                return false;
            }
        } catch (IOException e) {
            Logger.logEvent("Upload error: " + e.getMessage());
            return false;
        }
    }

    private String getFileName(String filepath) {
        return new File(filepath).getName();
    }
    private String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i+1);
        }
        return ".hash";
    }
}
