package com.project.springapistudy.common.handler;

import com.project.springapistudy.common.exception.RuntimeBaseException;
import com.project.springapistudy.common.exception.runtime.NotFoundExceptionRuntime;
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

    @ExceptionHandler(RuntimeBaseException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseData runtimeBaseException(RuntimeBaseException e, HttpServletRequest request) {
        return handleException(e, request, e.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData methodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        return handleValidationException(e, request);
    }

    private ResponseData handleException(Exception e, HttpServletRequest request, HttpStatus status) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        return new ResponseData(e.getMessage(), method, uri, status.value());
    }

    private ResponseData handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        BindingResult bindingResult = e.getBindingResult();
        List<String> collect = bindingResult.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return new ResponseData(collect.toString(), method, uri, HttpStatus.BAD_REQUEST.value());
    }

}
