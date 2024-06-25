package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiboSeriesVerticle extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(FiboSeriesVerticle.class);

    @Override
    public void start() throws Exception {
        JsonObject config = config();
        int n1 = config.getInteger("n1");
        int n2 = config.getInteger("n2");
        // compute fibo series based on n

        vertx.setTimer(2000, id -> this.computeFiboSeries(n1));
        vertx.setTimer(2000, id -> this.computeFiboSeries(n2));
    }

    private void computeFiboSeries(int n) {
        int a = 0;
        int b = 1;
        logger.info("{} where n = {}", a, n);
        logger.info("{} where n = {}", b, n);

        int i = 2;
        while (i < n) {
            int c = a + b;
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
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        JsonObject config = new JsonObject()
                .put("n1", 100)
                .put("n2", 200);
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(config)
                .setThreadingModel(ThreadingModel.WORKER); // worker verticle
        vertx.deployVerticle(new FiboSeriesVerticle(), options);
    }
}
