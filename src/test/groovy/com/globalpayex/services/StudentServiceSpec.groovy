package com.globalpayex.services

import com.globalpayex.dao.StudentDao
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import spock.lang.Specification
import spock.util.concurrent.BlockingVariable

class StudentServiceSpec extends Specification {
    def "test register student when the password is less than 5 characters"() {
        given:
        def studentDao = Stub(StudentDao)
        def studentService = new StudentService(studentDao)
        def student = new JsonObject()
            .put("password", '123')

        when:
        studentService.registerStudent(student)

        then:
        Exception e = thrown()
        e.message == 'password must be minimum 5 chars long'
    }

    def "test register student when the gender is not proper value"() {
        given:
        def studentDao = Stub(StudentDao)
        def studentService = new StudentService(studentDao)
        def student = new JsonObject()
                .put("password", '1234567')
                .put("gender", 'x')

        when:
        studentService.registerStudent(student)

        then:
        Exception e = thrown()
        e.message == 'gender must be either m or f'
    }

    def "test register student when all values are proper"() {
        given:
        def studentDao = Stub(StudentDao)
        studentDao.insert(_) >> {
            Promise<String> promise = Promise.promise()
            promise.complete("3245-abc-123")
            return promise.future()
        }

        def studentService = new StudentService(studentDao)
        def student = new JsonObject()
                .put("password", '1234567')
                .put("gender", 'm')
        def actualStudentId = new BlockingVariable<String>()

        when:
        studentService.registerStudent(student)
            .onSuccess {
                actualStudentId.set it
            }
            .onFailure {
                println it.message
                actualStudentId.set ''
            }

        then:
        actualStudentId.get() == '3245-abc-123'
    }










}
