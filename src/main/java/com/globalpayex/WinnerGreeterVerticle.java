package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WinnerGreeterVerticle extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(WinnerGreeterVerticle.class);

    public static final List<String> students = Arrays.asList(
            "mehul", "jane", "jill", "rahul", "priyanka"
    );

    @Override
    public void start() throws Exception {
        logger.info("verticle start");

        vertx.setTimer(1000, id -> logger.info("and"));
        vertx.setTimer(2000, id -> logger.info("the"));
        vertx.setTimer(3000, id -> logger.info("winner"));
        vertx.setTimer(4000, id -> logger.info("is"));
        vertx.setTimer(9000, this::handleWinner);

        logger.info("timers initialized in the verticle");
    }

    private void handleWinner(Long aLong) {
        var random = new Random();
        String winner = students.get(random.nextInt(students.size()));
        logger.info(winner);
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        // vertx.deployVerticle(new WinnerGreeterVerticle()); // deploy 1 instance of the verticle
        DeploymentOptions options = new DeploymentOptions()
                .setInstances(2);
        vertx.deployVerticle("com.globalpayex.WinnerGreeterVerticle", options);
    }
}
