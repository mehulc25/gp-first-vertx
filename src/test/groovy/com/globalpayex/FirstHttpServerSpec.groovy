package com.globalpayex

import groovy.json.JsonSlurper
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.util.concurrent.AsyncConditions
import spock.util.concurrent.BlockingVariable

@Stepwise
class FirstHttpServerSpec extends Specification {

    Vertx vertx

    DeploymentOptions options

    @Shared
    String newStudentId

    def setup() {
        vertx = Vertx.vertx()
        def asyncConditions = new AsyncConditions(1)
        options = new DeploymentOptions()
            .setConfig(new JsonObject()
                    .put("port", 8083)
                    .put("connection_string", "mongodb+srv://admin:admin123@cluster0.sf5orgc.mongodb.net/")
                    .put("db_name", "college_db")
                    .put("useObjectId", true)
            );
        vertx.deployVerticle("com.globalpayex.FirstHttpServer", options)
            .onSuccess(deploymentId -> asyncConditions.evaluate{
                assert deploymentId != ''
            })

        asyncConditions.await(5)
    }

    def cleanup() {
        vertx.close()
    }

    // end to end tests
    // integration tests
    def "test the POST endpoint for creating a student"() {
        given:
        def endpoint = '/students'
        def student = new JsonObject()
            .put("username", "test 789")
            .put("password", "abc 789")
            .put("email", "test3@gmail.com")
            .put("gender", "f")
        def studentRequest = Json.encodePrettily(student)
        def actualResponseCode = new BlockingVariable<Integer>(5)
        def actualNewStudentId = new BlockingVariable<String>(5)

        when:
        vertx.createHttpClient()
            .request(HttpMethod.POST, 8083, "localhost", endpoint)
                .onSuccess {request ->
                    request.send(studentRequest)
                        .onSuccess {response ->
                            response.bodyHandler {buffer ->
                                def jsonResponse = buffer.toJsonObject()
                                this.newStudentId = jsonResponse.getString("_id")
                                actualNewStudentId.set(jsonResponse.getString('_id'))
                            }
                            actualResponseCode.set(response.statusCode())
                        }
                        .onFailure {
                            println it.getMessage()
                            actualResponseCode.set(-1)
                        }
                }
        then:
        actualResponseCode.get() == 201
        actualNewStudentId.get() != ''
    }

    def "test the get endpoint for the above created student"() {
        given:
        def endpoint = "/students/${this.newStudentId}"
        def actualResponseCode = new BlockingVariable<Integer>(5)
        def actualStudentId = new BlockingVariable<String>(5)

        when:
        vertx.createHttpClient()
            .request(HttpMethod.GET, 8083, "localhost", endpoint)
                .onSuccess {request ->
                    request.send()
                        .onSuccess {response ->
                            actualResponseCode.set response.statusCode()
                            response.handler {
                                def responseJson = it.toJsonObject()
                                actualStudentId.set(responseJson.getString("_id"))
                            }
                        }
                        .onFailure {
                            println it.getMessage()
                            actualResponseCode.set(500)
                        }
                }

        then:
        actualResponseCode.get() == 200
        actualStudentId.get() == this.newStudentId
    }

    def "test the get all students endpoint"() {
        given:
        def endpoint = '/students'
        def actualResponseCode = new BlockingVariable<Integer>(5)
        def actualNewStudentFound = new BlockingVariable<Boolean>(5)

        when:
        vertx.createHttpClient()
            .request(HttpMethod.GET, 8083, 'localhost', endpoint)
            .onSuccess {request ->
                request.send()
                    .onSuccess{response ->
                        response.handler {
                           println it.toString()

                            actualResponseCode.set response.statusCode()
                        }
                    }
                    .onFailure {
                        println it.getMessage()
                        it.printStackTrace()
                        actualResponseCode.set 500
                    }
            }

        then:
        actualResponseCode.get() == 200
        // actualNewStudentFound.get() == true
    }

    /* def "test the deployment of the first http server verticle"() {
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
    } */
}
