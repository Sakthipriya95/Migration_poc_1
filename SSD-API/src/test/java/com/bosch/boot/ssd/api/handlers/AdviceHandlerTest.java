
package com.bosch.boot.ssd.api.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import com.bosch.boot.ssd.api.exception.FileChecksumException;
import com.bosch.boot.ssd.api.exception.FileStorageException;
import com.bosch.boot.ssd.api.exception.ParameterInvalidException;
import com.bosch.boot.ssd.api.exception.ResourceNotFoundException;
import com.bosch.boot.ssd.api.model.io.RestResponse;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AdviceHandlerTest {

  @InjectMocks
  private AdviceHandler adviceHandler;

  @Mock
  private MethodParameter methodParameter;

  @Mock
  private ServerHttpRequest serverHttpRequest;

  @Mock
  private ServerHttpResponse serverHttpResponse;

  @Mock
  Resource object;

  /**
   *
   */
  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Test NumberFormatException Handler
   */
  @Test
  public void testNumberFormatExceptionHandler() {
    NumberFormatException exception = new NumberFormatException("Invalid number format");
    RestResponse response = this.adviceHandler.numberFormatExceptionHandler(exception);
    assertEquals(false, response.isSuccess());
    assertEquals(400, response.getError().getCode());
    assertEquals("Invalid number format", response.getError().getTxt());
  }

  /**
   * Test NumberFormatException Handler with null
   */
  @Test
  public void testNumberFormatExceptionHandlerWithNull() {
    NumberFormatException exception = new NumberFormatException();
    RestResponse response = this.adviceHandler.numberFormatExceptionHandler(exception);
    assertEquals(false, response.isSuccess());
    assertEquals(400, response.getError().getCode());
    assertEquals("Null Object Error", response.getError().getTxt());
  }

  /**
   * Test IOException
   */
  @Test
  public void testIoException() {
    IOException exception = new IOException("File not found");
    RestResponse response = this.adviceHandler.ioException(exception);
    assertEquals(false, response.isSuccess());
    assertEquals(3, response.getError().getCode());
    assertEquals("File not found", response.getError().getTxt());
  }

  /**
   * Test ResourceNotFoundException
   */
  @Test
  public void testResourceNotFoundException() {
    ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");
    RestResponse response = this.adviceHandler.resourceNotFoundException(exception);
    assertEquals(false, response.isSuccess());
    assertEquals(404, response.getError().getCode());
    assertEquals("Resource not found", response.getError().getTxt());
  }

  /**
   * Test InvalidParameterException
   */
  @Test
  public void testInvalidParameterException() {
    ParameterInvalidException exception = new ParameterInvalidException("Invalid parameter");
    RestResponse response = this.adviceHandler.invalidParameterException(exception);
    assertEquals(false, response.isSuccess());
    assertEquals(400, response.getError().getCode());
    assertEquals("Invalid parameter", response.getError().getTxt());
  }

  /**
   * Test FileCheckSumException
   */
  @Test
  public void testFileChecksumException() {
    FileChecksumException exception = new FileChecksumException("Checksum mismatch");
    RestResponse response = this.adviceHandler.fileCheckSumException(exception);
    assertEquals(false, response.isSuccess());
    assertEquals(204, response.getError().getCode());
    assertEquals("Checksum mismatch", response.getError().getTxt());
  }

  /**
   * Test FileStorageException
   */
  @Test
  public void testFileStorageException() {
    FileStorageException exception = new FileStorageException("Error storing file");
    RestResponse response = this.adviceHandler.fileStorageException(exception);
    assertEquals(false, response.isSuccess());
    assertEquals(422, response.getError().getCode());
    assertEquals("Error storing file", response.getError().getTxt());
  }

  /**
   * Test GenericExceptionhandler
   */
  @Test
  public void testGenericExceptionHandler() {
    Exception exception = new Exception("Generic exception");
    RestResponse response = this.adviceHandler.genericExceptionHandler(exception);
    assertEquals(false, response.isSuccess());
    assertEquals(500, response.getError().getCode());
    assertEquals("Generic exception", response.getError().getTxt());
  }


  /**
   * Test GenericExceptionhandler with null
   */
  @Test
  public void testGenericExceptionHandlerWithNull() {
    Exception exception = new Exception();
    RestResponse response = this.adviceHandler.genericExceptionHandler(exception);
    assertEquals(false, response.isSuccess());
    assertEquals(500, response.getError().getCode());
    assertEquals("Null Object Error", response.getError().getTxt());
  }

  /**
   * Test Supports method
   */
  @Test
  public void testSupports() {
    AdviceHandler adviceHandlertestSupports = new AdviceHandler();
    boolean result = adviceHandlertestSupports.supports(null, null);
    assertTrue(result);
  }


  /**
   * Test BeforeBodyWrite method
   */
  @Test
  public void testBeforeBodyWrite() {
    RestResponse restResponse = new RestResponse(true);
    MediaType mediaType = MediaType.APPLICATION_JSON;
    Class<? extends HttpMessageConverter<?>> converterType = null;
    Object result = this.adviceHandler.beforeBodyWrite(restResponse, this.methodParameter, mediaType, converterType,
        this.serverHttpRequest, this.serverHttpResponse);
    assertEquals(restResponse, result);
  }

  /**
   * Test beforeBodyWrite method With ByteArray
   */
  @Test
  public void testBeforeBodyWrite_WithByteArray() {
    byte[] byteObject = new byte[] { 1, 2, 3 };
    MediaType mediaType = MediaType.APPLICATION_JSON;
    Class<? extends HttpMessageConverter<?>> converterType = null;
    Object result = this.adviceHandler.beforeBodyWrite(byteObject, this.methodParameter, mediaType, converterType,
        this.serverHttpRequest, this.serverHttpResponse);
    assertEquals(byteObject, result);
  }

  /**
   * Test beforeBodyWrite method with ByteArrayResource
   */
  @Test
  public void testBeforeBodyWrite_WithByteArrayResource() {
    ByteArrayResource byteArrayObject = new ByteArrayResource(new byte[] { 1, 2, 3 });
    MediaType mediaType = MediaType.APPLICATION_JSON;
    Class<? extends HttpMessageConverter<?>> converterType = null;
    Object result = this.adviceHandler.beforeBodyWrite(byteArrayObject, this.methodParameter, mediaType, converterType,
        this.serverHttpRequest, this.serverHttpResponse);
    assertEquals(byteArrayObject, result);
  }

  /**
   * Test beforeBodyWrite method with ResponseEntity
   */
  @Test
  public void testBeforeBodyWrite_WithResponseEntity() {
    ResponseEntity<Object> responseEntityObject = ResponseEntity.ok().body("Response Entity");
    MediaType mediaType = MediaType.APPLICATION_JSON;
    Class<? extends HttpMessageConverter<?>> converterType = null;
    Object result = this.adviceHandler.beforeBodyWrite(responseEntityObject, this.methodParameter, mediaType,
        converterType, this.serverHttpRequest, this.serverHttpResponse);
    assertEquals(responseEntityObject, result);
  }

  /**
   * Test beforeBodyWrite method with Resource
   */
  @Test
  public void testBeforeBodyWrite_WithResource() {
    MediaType mediaType = MediaType.APPLICATION_JSON;
    Class<? extends HttpMessageConverter<?>> converterType = null;
    Object result = this.adviceHandler.beforeBodyWrite(this.object, this.methodParameter, mediaType, converterType,
        this.serverHttpRequest, this.serverHttpResponse);
    assertEquals(this.object, result);
  }

  /**
   * Test beforeBodyWrite method with Rest Response
   */
  @Test
  public void testBeforeBodyWrite_WithRestResponse() {
    RestResponse restResponseobject = new RestResponse(true);
    MediaType mediaType = MediaType.APPLICATION_JSON;
    Class<? extends HttpMessageConverter<?>> converterType = null;
    Object result = this.adviceHandler.beforeBodyWrite(restResponseobject, this.methodParameter, mediaType,
        converterType, this.serverHttpRequest, this.serverHttpResponse);
    assertEquals(restResponseobject, result);
  }
}
