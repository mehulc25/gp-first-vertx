package com.globalpayex;

import com.globalpayex.entities.Book;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class FirstHttpServer extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(FirstHttpServer.class);

    @Override
    public void start() throws Exception {
        // dummy database
        List<Book> books = Arrays.asList(
                new Book(1, "book 1", 900, 1000),
                new Book(2, "prog in java", 850, 950),
                new Book(3, "scala programming", 920, 1500)
        );

        Router router = Router.router(vertx);
        router.get("/books").handler(routingContext -> {
            JsonArray data = new JsonArray(books);
            routingContext
                    .response()
                    .putHeader("Content-Type", "application/json") // MIME type
                    .end(data.encode());
        });

        Future<HttpServer> serverFuture = vertx.createHttpServer()
                .requestHandler(router)
                .listen(config().getInteger("port"));
        serverFuture.onSuccess(
                httpServer -> logger.info("Server running on port {}", httpServer.actualPort())
        );
        serverFuture.onFailure(
                exception -> logger.error("Canont start server {}", exception.getMessage())
        );
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("port", 8083));
        vertx.deployVerticle(new FirstHttpServer(), options);
    }
}
