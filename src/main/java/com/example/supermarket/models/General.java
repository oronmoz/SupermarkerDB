package com.example.supermarket.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class General {
    public static final int MAX_STR_LEN = 255;

    public static String getStrExactLength(String msg) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(msg);
        return scanner.nextLine().trim();
    }

    public static String getDynStr(String str) {
        return str;  // In Java, strings are already dynamically allocated
    }

    public static String getsStrFixSize(int size, String msg) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(msg);
        String input = scanner.nextLine();
        return input.length() > size ? input.substring(0, size) : input;
    }

    public static List<String> splitCharsToWords(String str, String del) {
        List<String> words = new ArrayList<>();
        for (String word : str.split(del)) {
            words.add(word.trim());
        }
        return words;
    }

    public static float getPositiveFloat(String msg) {
        Scanner scanner = new Scanner(System.in);
        float val;
        do {
            System.out.println(msg);
            val = scanner.nextFloat();
        } while (val < 0);
        return val;
    }

    public static int getPositiveInt(String msg) {
        Scanner scanner = new Scanner(System.in);
        int val;
        do {
            System.out.println(msg);
            val = scanner.nextInt();
        } while (val < 0);
        return val;
    }

    public static int countCharInString(String str, char tav) {
        return (int) str.chars().filter(ch -> ch == tav).count();
    }

    public static <T> void generalArrayFunction(List<T> arr, Consumer<T> func) {
        arr.forEach(func);
    }

    public static boolean checkEmptyString(String str) {
        return str.trim().isEmpty();
    }

    public static void printMessage(String... words) {
        for (String word : words) {
            System.out.print(word + " ");
        }
        System.out.println();
    }
}