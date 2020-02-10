import java.util.Scanner;

public class Pushes {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder settingsBuilder = new StringBuilder();
        for (int i = 0; i < 6 ; i++) {
            settingsBuilder.append(scanner.nextLine());
            settingsBuilder.append("\n");
        }
        settingsBuilder.setLength(settingsBuilder.length() - 1);
        Settings.parseEntrySettings(settingsBuilder.toString());
        int pushAmount = Integer.parseInt(scanner.nextLine());
        PushParser pushParser = new PushParser();
        PushHandler pushHandler = new PushHandler();
        for (int i = 0; i < pushAmount; i++) {
            int pushParams = Integer.parseInt(scanner.nextLine());
            
        }
    }
}
