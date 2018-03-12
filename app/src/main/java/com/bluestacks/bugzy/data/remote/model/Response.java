package com.bluestacks.bugzy.data.remote.model;

import java.util.List;

public class Response <T> {
    protected T data;
    protected List<Error> errors;

    public Response(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
