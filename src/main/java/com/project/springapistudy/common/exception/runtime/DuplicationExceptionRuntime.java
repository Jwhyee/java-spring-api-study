package com.project.springapistudy.common.exception.runtime;

import com.project.springapistudy.common.exception.RuntimeBaseException;
import org.springframework.http.HttpStatus;

public class DuplicationExceptionRuntime extends RuntimeBaseException {

    @Override
    public String getMessage() {
        return "이미 등록된 메뉴입니다.";
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

}
