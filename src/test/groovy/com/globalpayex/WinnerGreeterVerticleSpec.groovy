package com.globalpayex

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import spock.lang.Specification
import spock.util.concurrent.BlockingVariable

class WinnerGreeterVerticleSpec extends Specification {

    Vertx vertx

    DeploymentOptions options

    def setup() {
        vertx = Vertx.vertx()
        options = new DeploymentOptions().setInstances(2)
    }

    def cleanup() {
        vertx.close()
    }

    def "test the deployment of the winner greeter verticle with 2 instances"() {
        given:
        def actualDeploymentId = new BlockingVariable<String>()

        when:
        vertx.deployVerticle("com.globalpayex.WinnerGreeterVerticle", options)
            .onSuccess(deploymentId -> actualDeploymentId.set(deploymentId))
            .onFailure(exception -> actualDeploymentId.set(''))

        then:
        actualDeploymentId.get().size() > 0
    }
}
