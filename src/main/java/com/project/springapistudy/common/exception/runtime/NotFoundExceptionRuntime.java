package com.project.springapistudy.common.exception.runtime;

import com.project.springapistudy.common.exception.RuntimeBaseException;
import org.springframework.http.HttpStatus;

public class NotFoundExceptionRuntime extends RuntimeBaseException {

    @Override
    public String getMessage() {
        return "조회된 데이터가 없습니다.";
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

}
