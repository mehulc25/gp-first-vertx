package com.globalpayex

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import spock.lang.Specification
import spock.util.concurrent.BlockingVariable

class FirstHttpServerSpec extends Specification {

    Vertx vertx

    DeploymentOptions options

    def setup() {
        vertx = Vertx.vertx()
        options = new DeploymentOptions()
            .setConfig(new JsonObject()
                    .put("port", 8083)
                    .put("connection_string", "mongodb+srv://admin:admin123@cluster0.sf5orgc.mongodb.net/")
                    .put("db_name", "college_db")
                    .put("useObjectId", true)
            );
    }

    def cleanup() {
        vertx.close()
    }

    def "test the deployment of the first http server verticle"() {
        given:
        def actualDeploymentId = new BlockingVariable<String>()

        when:
        vertx.deployVerticle("com.globalpayex.FirstHttpServer", options)
                .onSuccess(deploymentId -> actualDeploymentId.set(deploymentId))
                .onFailure(exception -> {
                    println exception.getMessage()
                    actualDeploymentId.set('')
                })

        then:
        actualDeploymentId.get().size() > 0
    }
}
