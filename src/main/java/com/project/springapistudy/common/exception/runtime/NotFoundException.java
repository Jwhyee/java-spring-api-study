package com.project.springapistudy.common.exception.runtime;

import com.project.springapistudy.common.exception.BaseException;

public class NotFoundException extends BaseException {

    @Override
    public String getMessage() {
        return "조회된 데이터가 없습니다.";
    }

}
