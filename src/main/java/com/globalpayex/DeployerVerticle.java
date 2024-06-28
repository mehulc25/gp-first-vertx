package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeployerVerticle extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(DeployerVerticle.class);

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
        vertx.deployVerticle(new StatisticsVerticle(), options);
        vertx.deployVerticle(new EmailVerticle(), options);
        logger.info("Verticles deployed!!");
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new DeployerVerticle());
    }
}
