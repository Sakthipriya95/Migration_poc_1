/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.exception.test;


import static org.junit.Assert.assertEquals;

/**
 * @author TUD1COB
 *
 */
import org.junit.Test;

import com.bosch.boot.ssd.api.exception.ResourceNotFoundException;

/**
 * @author TUD1COB
 *
 */
public class ResourceNotFoundExceptionTest {

    /**
     * 
     */
    @Test
    public void testDefaultConstructor() {
        ResourceNotFoundException exception = new ResourceNotFoundException();
        assertEquals("Resource not found", exception.getMessage());
    }

    /**
     * 
     */
    @Test
    public void testConstructorWithMessage() {
        String errorMessage = "Resource not found: id=123";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

    /**
     * 
     */
    @Test
    public void testConstructorWithMessageAndCause() {
        String errorMessage = "Resource not found: id=123";
        Throwable cause = new RuntimeException("Database connection error");
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage, cause);
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
