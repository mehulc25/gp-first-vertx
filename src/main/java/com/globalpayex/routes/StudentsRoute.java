package com.globalpayex.routes;

import com.globalpayex.dao.StudentDao;
import com.globalpayex.services.StudentService;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class StudentsRoute {

    private static final Logger logger = LoggerFactory.getLogger(StudentsRoute.class);

    private static MongoClient mongoClient;

    private static StudentService studentService;

    public static Router init(Router router, Vertx vertx, JsonObject config) {
        mongoClient = MongoClient.createShared(vertx, config);
        studentService = new StudentService(new StudentDao(vertx, config));

        router.get("/students").handler(StudentsRoute::getAllStudents);
        router.get("/students/:studentId").handler(StudentsRoute::getStudent);
        router.post("/students")
                .handler(routingContext -> createNewStudent(routingContext, vertx));
        return router;
    }

    private static void createNewStudent(RoutingContext routingContext, Vertx vertx) {
        JsonObject requestJson = routingContext.body().asJsonObject();

        try {
            studentService.registerStudent(requestJson)
                    .onSuccess(studentId -> {
                        requestJson.put("_id", studentId);
                        vertx
                                .eventBus()
                                .publish("new.student",
                                        new JsonObject().put("_id", studentId));
                        routingContext
                                .response()
                                .putHeader("Content-Type", "application/json")
                                .setStatusCode(201)
                                .end(requestJson.encode());
                    })
                    .onFailure(exception -> logger.error("error in saving student {}", exception.getMessage()));
        } catch (Exception e) {
            routingContext
                    .response()
                    .putHeader("Content-Type", "application/json")
                    .setStatusCode(400)
                    .end(e.getMessage());
        }
    }

    private static void getStudent(RoutingContext routingContext) {
        String studentId = routingContext.pathParam("studentId");
        JsonObject query = new JsonObject()
                .put("_id", new JsonObject().put("$oid", studentId));
        Future<JsonObject> future = mongoClient.findOne("students", query, null);
        future.onSuccess(studentObject -> {
            if (studentObject == null) {
                routingContext
                        .response()
                        .setStatusCode(404)
                        .end("student not found");
            }
           else {
                JsonObject responseJson = mapDbToResponseJson(studentObject);
                routingContext
                        .response()
                        .putHeader("Content-Type", "application/json")
                        .end(responseJson.encode());
            }
        });
        future.onFailure(exception -> {
            logger.error("error in fetching student {}", exception.getMessage());
            routingContext
                    .response()
                    .setStatusCode(500)
                    .end("Server error");
        });
    }

    private static void getAllStudents(RoutingContext routingContext) {
        List<String> genderQp = routingContext.queryParam("gender");
        List<String> countryQp = routingContext.queryParam("country");

        JsonObject query = new JsonObject();
        JsonArray orConditions = new JsonArray();
        if (genderQp.size() > 0) {
            orConditions.add(new JsonObject().put("gender", genderQp.get(0)));
            // query.put("gender", genderQp.get(0));
        }
        if (countryQp.size() > 0) {
            orConditions.add(new JsonObject().put("address.country", countryQp.get(0)));
            // query.put("address.country", countryQp.get(0));
        }

        if (!orConditions.isEmpty()) {
            query.put("$or", orConditions);
        }

        Future<List<JsonObject>> future = mongoClient
                .find("students", query);

        future.onSuccess(studentJsonObjects -> {
           logger.info("students {}", studentJsonObjects);
           List<JsonObject> responseJson = studentJsonObjects
                   .stream()
                   .map(StudentsRoute::mapDbToResponseJson)
                   .collect(Collectors.toList());
           JsonArray responseData = new JsonArray(responseJson);
           routingContext
                   .response()
                   .putHeader("Content-Type", "application/json")
                   .end(responseData.encode());
        });
        future.onFailure(exception -> {
            logger.error("error in fetching students {}", exception.getMessage());
            routingContext
                    .response()
                    .setStatusCode(500)
                    .end("Server error");
        });
    }

    private static JsonObject mapDbToResponseJson(JsonObject dbJson) {
        JsonObject responseJson = new JsonObject();
        /* responseJson.put("_id", dbJson
                .getJsonObject("_id")
                .getString("$oid")); */ // useObjectId - false
        responseJson.put("_id", dbJson.getString("_id"));
        responseJson.put("username", dbJson.getString("username"));
        responseJson.put("gender", dbJson.getString("gender"));
        responseJson.put("email", dbJson.getString("email"));
        return responseJson;
    }
}
