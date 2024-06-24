package com.globalpayex;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WinnerGreeter {

    public static final List<String> students = Arrays.asList(
            "mehul", "jane", "jill", "rahul", "priyanka"
    );

    public static void main(String[] args) throws InterruptedException {
        var random = new Random();
        String winner = students.get(random.nextInt(students.size()));
        System.out.println("And");
        Thread.sleep(1000);
        System.out.println("the");
        Thread.sleep(1000);
        System.out.println("winner");
        Thread.sleep(1000);
        System.out.println("is");
        Thread.sleep(5000);
        System.out.println(winner);
    }
}
