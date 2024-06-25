package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiOperationsVerticle4 extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(MultiOperationsVerticle4.class);

    private void performAddition(int a, int b) {
        logger.info("addition is {}", a + b);
    }

    private void performMultiplication(int a, int b) {
        logger.info("multiplication is {}", a * b);
    }

    private int computeFiboSeries(int n) {
        int a = 0;
        int b = 1;
        logger.info("{} where n = {}", a, n);
        logger.info("{} where n = {}", b, n);

        int i = 2;
        int c = 0;
        while (i < n) {
            c = a + b;
            logger.info("{} where n = {}", c, n);

            // deliberate blocking (delay)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            a = b;
            b = c;
            i++;
        }

        return c;
    }

    private void readFile(String filePath) {
        // non blockin IO
        OpenOptions options = new OpenOptions()
                .setCreate(false)
                .setRead(true);
        Future<AsyncFile> readFileFuture = vertx.fileSystem().open(filePath, options);
        readFileFuture.onSuccess(asyncFile -> {
            asyncFile.handler(buffer -> System.out.println(buffer))
                    .exceptionHandler(exception -> logger.error("error reading file {}",
                            exception.getMessage()));
        });
        readFileFuture.onFailure(exception -> logger.error("error operning file {}",
                exception.getMessage()));
    }

    @Override
    public void start() throws Exception {
        int a = config().getInteger("a");
        int b = config().getInteger("b");

        // scheduled on event loop thread
        vertx.setTimer(1000, id -> this.readFile("build.grad"));
        vertx.setTimer(1000, id -> this.performAddition(a, b));
        vertx.setTimer(1000, id -> this.performMultiplication(a, b));

        // schedule blocking operation on worker thread
        vertx.executeBlocking(
                () -> this.computeFiboSeries(a),
                ar -> {
                    // ar -- AsyncResult
                    if (ar.succeeded()) {
                        int r = ar.result();
                        logger.info("blocking operation result is {}", r);
                    }
                });
        vertx.executeBlocking(
                () -> this.computeFiboSeries(b),
                ar -> {
                    if (ar.succeeded()) {
                        int r = ar.result();
                        logger.info("blocking operation result is {}", r);
                    }
                });

        /* vertx.executeBlocking(() -> {
            this.computeFiboSeries(a);
            return 0;
        });
        vertx.executeBlocking(() -> {
            this.computeFiboSeries(b);
            return 0;
        }); */
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("a", 10).put("b", 5));
        vertx.deployVerticle(new MultiOperationsVerticle4(), options);
    }
}
