package com.project.springapistudy.common.handler;

import com.project.springapistudy.common.exception.BaseException;
import com.project.springapistudy.common.exception.runtime.NotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestControllerAdvice
public class ExceptionRestHandler {

    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseData.ApiResult<?> runtimeBaseException(NotFoundException e, HttpServletRequest request) {
        return handleException(e, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData.ApiResult<?> methodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        return handleValidationException(e, request);
    }

    private ResponseData.ApiResult<?> handleException(Exception e, HttpServletRequest request, HttpStatus status) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        return ResponseData.fail(e.getMessage(), method, uri, status.value());
    }

    private ResponseData.ApiResult<?> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        BindingResult bindingResult = e.getBindingResult();
        List<String> collect = bindingResult.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return ResponseData.fail(collect.toString(), method, uri, HttpStatus.BAD_REQUEST.value());
    }

}
