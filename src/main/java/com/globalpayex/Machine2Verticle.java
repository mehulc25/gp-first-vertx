package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Machine2Verticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(Machine2Verticle.class);

    @Override
    public void start() throws Exception {
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("port", 8083)
                        .put("connection_string", "")
                        .put("db_name", "college_db")
                        .put("useObjectId", true)
                        .put("emailHostname", "smtp.gmail.com")
                        .put("emailPort", 587)
                        .put("emailUsername", "")
                        .put("emailPassword", "")
                );
        vertx.deployVerticle(new StatisticsVerticle(), options);
        vertx.deployVerticle(new EmailVerticle(), options);
        logger.info("Machine 2 verticles deployed!!");
    }

    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions())
                .onSuccess(vertx -> {
                    vertx.deployVerticle(new Machine2Verticle());
                })
                .onFailure(
                        exception -> logger.error("error in deploying machine2 {}",
                                exception.getMessage()));
    }
}
