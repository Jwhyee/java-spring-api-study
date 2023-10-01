package com.project.springapistudy.common.handler;

import lombok.Getter;

@Getter
class ResponseData {
    String detail;
    String method;
    String instance;
    int status;

    ResponseData(String detail, String method, String instance, int status) {
        this.detail = detail;
        this.method = method;
        this.instance = instance;
        this.status = status;
    }

}
