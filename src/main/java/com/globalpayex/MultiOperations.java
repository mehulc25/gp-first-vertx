package com.globalpayex;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class MultiOperations {

    private static final Logger logger = LoggerFactory.getLogger(MultiOperations.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("enter a: ");
            int a = scanner.nextInt();
            System.out.println("enter b: ");
            int b = scanner.nextInt();

            vertx.setTimer(4000, id -> logger.info("addition is {}", a + b));
            vertx.setTimer(2000, id -> logger.info("multiplication is {}", a * b));
        }
    }
}
