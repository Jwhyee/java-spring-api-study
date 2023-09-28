package com.project.springapistudy.common.exception.runtime;

import com.project.springapistudy.common.exception.BaseException;

public class DuplicationException extends BaseException {

    @Override
    public String getMessage() {
        return "이미 등록된 메뉴입니다.";
    }

}
