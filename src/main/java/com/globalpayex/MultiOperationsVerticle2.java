package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiOperationsVerticle2 extends AbstractVerticle {

    private int a = 10;
    private int b = 5;

    private final Logger logger = LoggerFactory.getLogger(MultiOperationsVerticle2.class);

    private Future<Integer> performAddition() {
        Promise<Integer> promise = Promise.promise();
        vertx.setTimer(5000, id -> {
            int result = a + b;
            promise.complete(result);
        });
        return promise.future();
    }

    private Future<Integer> performMultiplication(int result) {
        Promise<Integer> promise = Promise.promise();
        vertx.setTimer(3000, id -> {
           int r = (a * b) + result;
           promise.complete(r);
        });
        return promise.future();
    }

    @Override
    public void start() throws Exception {
        Future<Integer> additionFuture = performAddition();
        // additionFuture.onSuccess(additionResult -> logger.info("addition is {}", additionResult));

        Future<Integer> multiplicationFuture = additionFuture
                .compose(additionResult -> {
                    logger.info("addition is {}", additionResult);
                    return performMultiplication(additionResult);
                });
        multiplicationFuture.onSuccess(multiplicationResult ->
                logger.info("multiplication is {}", multiplicationResult));

        logger.info("start initialized");
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MultiOperationsVerticle2());
    }
}
