package com.globalpayex;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WinnerGreeterAsync {

    private static final Logger logger = LoggerFactory.getLogger(WinnerGreeterAsync.class);

    public static final List<String> students = Arrays.asList(
            "mehul", "jane", "jill", "rahul", "priyanka"
    );

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.setTimer(1000, id -> logger.info("and"));
        vertx.setTimer(2000, id -> logger.info("the"));
        vertx.setTimer(3000, id -> logger.info("winner"));
        vertx.setTimer(4000, id -> logger.info("is"));
        vertx.setTimer(9000, WinnerGreeterAsync::handleWinner);
        logger.info("all timers scheduled");
    }

    private static void handleWinner(Long aLong) {
        var random = new Random();
        String winner = students.get(random.nextInt(students.size()));
        logger.info(winner);
    }
}
