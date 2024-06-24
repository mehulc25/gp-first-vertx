package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiOperationsVerticle extends AbstractVerticle {
    private int a = 10;
    private int b = 5;

    private final Logger logger = LoggerFactory.getLogger(MultiOperationsVerticle.class);

    @Override
    public void start() throws Exception {
        vertx.setTimer(5000, id1 -> {
           int result = a + b;
           logger.info("addition is {}", result);

           vertx.setTimer(3000, id2 -> {
               int mulResult = (a * b) + result;
               logger.info("multiplication is {}", mulResult);
           });
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MultiOperationsVerticle());
    }
}
