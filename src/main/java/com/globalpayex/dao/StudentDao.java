package com.globalpayex.dao;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class StudentDao {

    private MongoClient mongoClient;

    public StudentDao(Vertx vertx, JsonObject config) {
        this.mongoClient = MongoClient.createShared(vertx, config);
    }

    public Future<String> insert(JsonObject data) {
        Future<String> future = mongoClient.insert("students", data);
        return future;
    }

    public Future<JsonObject> findById(String studentId) {
        JsonObject query = new JsonObject()
                .put("_id", new JsonObject().put("$oid", studentId));
        Future<JsonObject> future = mongoClient.findOne("students", query, null);
        return future;
    }
}
