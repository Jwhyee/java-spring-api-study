package com.project.springapistudy.common.exception;

import org.springframework.http.HttpStatus;

public abstract class RuntimeBaseException extends RuntimeException {
    public abstract String getMessage();

    public abstract HttpStatus getStatus();
}
