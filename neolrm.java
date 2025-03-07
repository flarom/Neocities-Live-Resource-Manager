import java.io.Console;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;

public class neolrm {
    
    private static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        String user;
        String pass;
        String srce;
        String trgt;

        login: while (true) {
            // ask for username
            if (args.length >= 1) user = args[0];
            else user = getInputString("\nNeocities username:", Settings.getSetting("history_user", ""));
            
            // ask for password
            if (args.length >= 2) pass = args[1];
            else pass = getInputPassword("\nPassword:\n");
            
            // check if acount exists
            if (!checkLoginExists(user, pass)) { System.out.println("\u001B[31mError: Login incorrect!\u001B[0m"); continue login; } 
           
            System.out.println("\u001B[32m" + "Successfully logged in!" + "\u001B[0m");
            break;
        }

        // ask for soruce
        source: while (true) {
            if (args.length >= 3) srce = args[2];
            else srce = getInputString("\nSource file path:", Settings.getSetting("history_source", ""));

            // check if source exists
            if (!checkFileExists(srce)) { System.out.println("\u001B[31mError: File '" + srce + "' does not exist.\u001B[0m"); continue source; }

            break;
        }

        // ask for target
        if (args.length >= 4) trgt = args[3];
        else trgt = getInputString("\nTarget file path:", Settings.getSetting("history_target", ""));

        // save settings
        Settings.setSetting("history_user", user);
        Settings.setSetting("history_source", srce);
        Settings.setSetting("history_target", trgt);

        //clear screen
        System.out.print("\033[H\033[2J");
        System.out.flush();

        // start monitoring
        AutoUpdate updt = new AutoUpdate(user, pass, srce, trgt);
        updt.startMonitoring();
    }

    /**
     * Get a text input
     * @param message Message used to expecify what text input should be inserted
     * @param autoComplete String to be return, in the case of the user returning ""
     * @return Text input inserted by the user, or the autoComplete
     */
    private static String getInputString(String message, String autoComplete) {
        System.out.println(message + (autoComplete.isEmpty() ? "" : " \u001B[90m(ENTER: " + autoComplete + ")\u001B[0m"));
        String input = scan.nextLine().trim();
        
        if (input.isEmpty()) {
            System.out.println(autoComplete);
            return autoComplete;
        }

        return input;
    }

    /**
     * Get a password
     * @param message Message used to expecify what password should be inserted
     * @return The password inserted by the user
     */
    private static String getInputPassword(String message) {
        Console console = System.console();
        if (console != null) {
            return new String(console.readPassword(message));
        } else {
            System.out.println(message);
            return new Scanner(System.in).nextLine().trim();
        }
    }

    /**
     * Checks if a filepath exists
     * @param filepath The filepath to be verifyed
     * @return True if the filepath exists
     */
    private static boolean checkFileExists(String filepath) {
        File file = new File(filepath);
        return file.exists() && file.isFile();
    }

    /**
     * Check if a Neocities login exists
     * @param user Neocities username
     * @param pass Neocities password
     * @return True if login exists
     */
    private static boolean checkLoginExists(String user, String pass) {
        try {
            URL url = new URL("https://neocities.org/api/info");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String encodedAuth = Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return true;
            } else {
                System.out.println("Neocities API response: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Error connecting to Neocities: " + e.getMessage());
        }
        return false;
    }
}
