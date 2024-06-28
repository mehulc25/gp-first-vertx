package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Machine1Verticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(Machine1Verticle.class);

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
        vertx.deployVerticle(new FirstHttpServer(), options);
        logger.info("Machine 1 verticle deployed!!");
    }

    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions())
                .onSuccess(vertx -> {
                    vertx.deployVerticle(new Machine1Verticle());
                })
                .onFailure(
                        exception -> logger.error("error in deploying machine1 {}", exception.getMessage()));
    }
}
