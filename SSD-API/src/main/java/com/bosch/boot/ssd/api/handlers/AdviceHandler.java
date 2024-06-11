/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.handlers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.bosch.boot.ssd.api.exception.FileChecksumException;
import com.bosch.boot.ssd.api.exception.FileStorageException;
import com.bosch.boot.ssd.api.exception.ParameterInvalidException;
import com.bosch.boot.ssd.api.exception.ResourceNotFoundException;
import com.bosch.boot.ssd.api.model.io.RestError;
import com.bosch.boot.ssd.api.model.io.RestResponse;

/**
 * @author GDH9COB
 */
@ControllerAdvice(basePackages = "com.bosch.boot.ssd.api")
public class AdviceHandler implements ResponseBodyAdvice<Object> {

  private static final Logger logger = LoggerFactory.getLogger(AdviceHandler.class);



  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public RestResponse numberFormatExceptionHandler(Exception e) {
    logger.error("Exception: ", e);
    RestResponse response = new RestResponse(false);
    response.setError(new RestError(400, e.getMessage() == null ? "Null Object Error" : e.getMessage()));

    return response;
  }

  @ExceptionHandler(IOException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public RestResponse ioException(Exception e) {
    logger.error("IOException: ", e);
    RestResponse response = new RestResponse(false);
    response.setError(new RestError(3, e.getMessage()));
    return response;
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public RestResponse resourceNotFoundException(ResourceNotFoundException e) {
    logger.error("ResourceNotFoundException: ", e);
    RestResponse response = new RestResponse(false);
    response.setError(new RestError(404, e.getMessage()));
    return response;
  }

  @ExceptionHandler(ParameterInvalidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public RestResponse invalidParameterException(ParameterInvalidException e) {
    logger.error("ParameterInvalidException: ", e);
    RestResponse response = new RestResponse(false);
    response.setError(new RestError(400, e.getMessage()));
    return response;
  }

  @ExceptionHandler(FileChecksumException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public RestResponse fileCheckSumException(FileChecksumException e) {
    logger.error("FileChecksumException: ", e);
    RestResponse response = new RestResponse(false);
    response.setError(new RestError(204, e.getMessage()));
    return response;
  }

  @ExceptionHandler(FileStorageException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public RestResponse fileStorageException(FileStorageException e) {
    logger.error("FileStorageException: ", e);
    RestResponse response = new RestResponse(false);
    response.setError(new RestError(422, e.getMessage()));
    return response;
  }

  @ExceptionHandler(Exception.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public RestResponse genericExceptionHandler(Exception e) {
    logger.error("Exception: ", e);
    RestResponse response = new RestResponse(false);
    response.setError(new RestError(500, e.getMessage() == null ? "Null Object Error" : e.getMessage()));
    return response;
  }

  @Override
  public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(Object object, MethodParameter methodParameter, MediaType mediaType,
      Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest,
      ServerHttpResponse serverHttpResponse) {

    if (object instanceof byte[] || object instanceof ByteArrayResource || object instanceof ResponseEntity ||
        object instanceof Resource) {
      return object;
    }
    return object instanceof RestResponse ? object : new RestResponse(object);
  }

}
