package com.globalpayex.routes;

import com.globalpayex.FirstHttpServer;
import com.globalpayex.entities.Book;
import io.vertx.core.Vertx;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class AppRouter {

    private static final Logger logger = LoggerFactory.getLogger(AppRouter.class);

    public static Router init(Vertx vertx, JsonObject config) {

        Router router = Router.router(vertx);
        router.post().handler(BodyHandler.create());
        router = BooksRoute.init(router, vertx, config);
        router = StudentsRoute.init(router, vertx, config);

        router.get("/download")
                .handler(routingContext -> handleDownload(routingContext, vertx));

        return router;
    }

    private static void handleDownload(RoutingContext routingContext, Vertx vertx) {
        OpenOptions options = new OpenOptions()
                .setRead(true);
        vertx.fileSystem().open("audio/sample.mp3", options)
                .onSuccess(asyncFile -> {
                    HttpServerResponse response = routingContext.response();
                    response
                            .setStatusCode(200)
                            .putHeader("Content-type", "audio/mp3")
                            .putHeader("Content-Disposition", "attachment")
                            .setChunked(true);

                    asyncFile.pipeTo(response); // internally manages back pressure

                    /* asyncFile.handler(buffer -> {
                        response.write(buffer);
                        if (response.writeQueueFull()) {
                            asyncFile.pause();
                            response.drainHandler(v -> asyncFile.resume());
                        }
                    });

                    asyncFile.endHandler(v -> response.end()); */

                })
                .onFailure(exception -> logger.error("{}", exception.getMessage()));
    }
}
