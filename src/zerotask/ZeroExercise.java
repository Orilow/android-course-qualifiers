package zerotask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ZeroExercise {
    public static void main(String[] args) throws IOException {
        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
        String[] aAndB= bi.readLine().split(" ");

        System.out.println(Integer.parseInt(aAndB[0]) + Integer.parseInt(aAndB[1]));
    }
}
