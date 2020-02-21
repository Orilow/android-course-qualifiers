import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.MissingFormatArgumentException;
import java.util.Scanner;

public class SecondInOneFile {
    public static void main(String[] args) throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Scanner scanner = new Scanner(System.in);
        StringBuilder settingsBuilder = new StringBuilder();

        for (int i = 0; i < 6 ; i++) {
            settingsBuilder.append(scanner.nextLine());
            settingsBuilder.append("\n");
        }
        settingsBuilder.setLength(settingsBuilder.length() - 1);
        Settings.parseEntrySettings(settingsBuilder.toString());
        int pushAmount = Integer.parseInt(scanner.nextLine());
        PushParser parser = new PushParser();
        ArrayList<Push> parsedPushes= new ArrayList<>();

        for (int i = 0; i < pushAmount; i++) {
            int pushParams = Integer.parseInt(scanner.nextLine());
            String[] pushStrings = new String[pushParams];
            for (int j = 0; j < pushParams; j++) {
                pushStrings[j] = scanner.nextLine();
            }
            parsedPushes.add(parser.parseInputStringIntoPush(pushStrings));
        }

        var pushesToShow = parsedPushes.stream().filter(x -> !x.filter()).peek(Push::print).toArray();
        if (pushesToShow.length == 0) {
            System.out.println("-1");
        }
    }

    public String[] getLines(Scanner scanner, int N) {
        var strings = new String[N];
        for (int i = 0; i < N; i++) {
            strings[i] = scanner.nextLine();
        }
        return strings;
    }
}

interface Filterable {
    boolean filter();
}

abstract class Push  implements Filterable{
    public String text;
    public String type;

    public void print() {
        System.out.println(text);
    }
}

final class PushFilters {
    private PushFilters(){}

    public static boolean filterByAge(int age) {
        return age > Settings.age;
    }

    public static  boolean filterByLocation(float x_coord, float y_coord, int radius) {
        double distance = Math.sqrt(Math.pow(Settings.x_coord - x_coord, 2) + Math.pow(Settings.y_coord - y_coord, 2));
        return distance > radius;
    }

    public static boolean filterByOSVersion(int os_version) {
        return Settings.os_version > os_version;
    }

    public static boolean filterByGender(char gender) {
        return Settings.gender != gender;
    }

    public static boolean filterByExpiryDate(long expiry_date) {
        return Settings.time > expiry_date;
    }
}

class PushParser {
    public final static String[] parsableClasses = new String[] {
            LocationAgePush.class.getName(),
            LocationPush.class.getName(),
            AgeSpecificPush.class.getName(),
            TechPush.class.getName(),
            GenderAgePush.class.getName(),
            GenderPush.class.getName()
    };

    public Push parseInputStringIntoPush(String[] input) throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        for (String param: input) {
            if (param.startsWith("type")) {
                String[] keyAndValue = param.split(" ");
                if (Arrays.asList(parsableClasses).contains(keyAndValue[1])) {
                    Class<?> clazz = Class.forName(keyAndValue[1]);
                    Constructor<?> constructor = clazz.getConstructor(String[].class);
                    Object obj = constructor.newInstance(new Object[]{ input });
                    return (Push) obj;
                }
            }
        }

        throw new ClassNotFoundException("No TYPE param in push input");
    }
}

final class Settings {
    public static Long time;
    public static Integer age;
    public static char gender;
    public static Integer os_version;
    public static float x_coord;
    public static float y_coord;

    private Settings() {
    }

    public static void parseEntrySettings(String input) {
        String[] settings = input.split("\n");
        for (String setting: settings) {
            String[] keyAndValue = setting.split(" ");
            switch (keyAndValue[0]) {
                case ("time"):
                    Settings.time = Long.parseLong(keyAndValue[1]);
                    break;
                case ("gender"):
                    Settings.gender = keyAndValue[1].charAt(0);
                    break;
                case ("age"):
                    Settings.age = Integer.parseInt(keyAndValue[1]);
                    break;
                case ("os_version"):
                    Settings.os_version = Integer.parseInt(keyAndValue[1]);
                    break;
                case ("x_coord"):
                    Settings.x_coord = Float.parseFloat(keyAndValue[1]);
                    break;
                case ("y_coord"):
                    Settings.y_coord = Float.parseFloat(keyAndValue[1]);
                    break;
                default:
                    throw new MissingFormatArgumentException("Нерпавильный формат введенных данных");
            }
        }
    }
}

class AgeSpecificPush extends Push {
    public int age;
    public long expiry_date;

    public AgeSpecificPush(String[] input) {
        for (String param: input) {
            String[] kv = param.split(" ");
            switch (kv[0]) {
                case ("expiry_date"):
                    expiry_date = Long.parseLong(kv[1]);
                    break;
                case ("age"):
                    age = Integer.parseInt(kv[1]);
                    break;
                case ("text"):
                    text = kv[1];
                    break;
                case ("type"):
                    type = kv[1];
                    break;
            }
        }
    }

    @Override
    public boolean filter() {
        return PushFilters.filterByAge(age) || PushFilters.filterByExpiryDate(expiry_date);
    }
}

class GenderAgePush extends Push {
    public int age;
    public char gender;

    public GenderAgePush(String[] input) {
        for (String param: input) {
            String[] kv = param.split(" ");
            switch (kv[0]) {
                case ("gender"):
                    gender = kv[1].charAt(0);
                    break;
                case ("age"):
                    age = Integer.parseInt(kv[1]);
                    break;
                case ("text"):
                    text = kv[1];
                    break;
                case ("type"):
                    type = kv[1];
                    break;
            }
        }
    }
    @Override
    public boolean filter() {
        return PushFilters.filterByGender(gender) || PushFilters.filterByAge(age);
    }
}

class GenderPush extends Push {
    public char gender;

    public GenderPush(String[] input) {
        for (String param: input) {
            String[] kv = param.split(" ");
            switch (kv[0]) {
                case ("gender"):
                    gender = kv[1].charAt(0);
                    break;
                case ("text"):
                    text = kv[1];
                    break;
                case ("type"):
                    type = kv[1];
                    break;
            }
        }
    }
    @Override
    public boolean filter() {
        return PushFilters.filterByGender(gender);
    }
}

class LocationAgePush extends Push {
    public float x_coord;
    public float y_coord;
    public int radius;
    public int age;

    public LocationAgePush(String[] input) {
        for (String param: input) {
            String[] kv = param.split(" ");
            switch (kv[0]) {
                case ("x_coord"):
                    x_coord = Float.parseFloat(kv[1]);
                    break;
                case ("y_coord"):
                    y_coord = Float.parseFloat(kv[1]);
                    break;
                case ("radius"):
                    radius = Integer.parseInt(kv[1]);
                    break;
                case ("age"):
                    age = Integer.parseInt(kv[1]);
                    break;
                case ("text"):
                    text = kv[1];
                    break;
                case ("type"):
                    type = kv[1];
                    break;
            }
        }
    }

    @Override
    public boolean filter() {
        return PushFilters.filterByLocation(x_coord, y_coord, radius) || PushFilters.filterByAge(age);
    }
}

class LocationPush extends Push {
    public float x_coord;
    public float y_coord;
    public int radius;
    public long expiry_date;

    public LocationPush(String[] input) {
        for (String param: input) {
            String[] kv = param.split(" ");
            switch (kv[0]) {
                case ("x_coord"):
                    x_coord = Float.parseFloat(kv[1]);
                    break;
                case ("y_coord"):
                    y_coord = Float.parseFloat(kv[1]);
                    break;
                case ("radius"):
                    radius = Integer.parseInt(kv[1]);
                    break;
                case ("expiry_date"):
                    expiry_date = Long.parseLong(kv[1]);
                    break;
                case ("text"):
                    text = kv[1];
                    break;
                case ("type"):
                    type = kv[1];
                    break;
            }
        }
    }

    @Override
    public boolean filter() {
        return PushFilters.filterByLocation(x_coord, y_coord, radius) || PushFilters.filterByExpiryDate(expiry_date);
    }
}

class TechPush extends Push {
    public int os_version;

    public TechPush(String[] input) {
        for (String param: input) {
            String[] kv = param.split(" ");
            switch (kv[0]) {
                case ("os_version"):
                    os_version = Integer.parseInt(kv[1]);
                    break;
                case ("text"):
                    text = kv[1];
                    break;
                case ("type"):
                    type = kv[1];
                    break;
            }
        }
    }

    @Override
    public boolean filter() {
        return PushFilters.filterByOSVersion(os_version);
    }
}

