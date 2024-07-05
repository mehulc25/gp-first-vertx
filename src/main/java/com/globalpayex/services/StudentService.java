package com.globalpayex.services;

import com.globalpayex.dao.StudentDao;
import com.globalpayex.exceptions.NotFoundException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class StudentService {

    private StudentDao studentDao;

    public StudentService(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    public Future<String> registerStudent(JsonObject requestData) throws Exception {
        if (requestData.getString("password").length() < 5) {
            throw new Exception("password must be minimum 5 chars long");
        }

        if (!requestData.getString("gender").equals("m") && !requestData.getString("gender").equals("f")) {
            throw new Exception("gender must be either m or f");
        }

        Future<String> future = this.studentDao.insert(requestData);
        return future;
    }

    public Future<JsonObject> getStudentById(String studentId) {
        Promise<JsonObject> promise = Promise.promise();
        this.studentDao.findById(studentId)
                .onSuccess(studentObj -> {
                    if (studentObj == null) {
                        promise.fail(new NotFoundException("student with id not found"));
                    } else {
                        promise.complete(studentObj);
                    }
                })
                .onFailure(exception -> promise.fail(exception));
        return promise.future();
    }
}
