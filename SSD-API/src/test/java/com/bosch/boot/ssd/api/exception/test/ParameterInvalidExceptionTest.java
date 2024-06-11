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

import com.bosch.boot.ssd.api.exception.ParameterInvalidException;

/**
 * @author TUD1COB
 *
 */
public class ParameterInvalidExceptionTest {

    /**
     * 
     */
    @Test
    public void testDefaultConstructor() {
        ParameterInvalidException exception = new ParameterInvalidException();
        assertEquals("Input parameters missing", exception.getMessage());
    }

    /**
     * 
     */
    @Test
    public void testConstructorWithMessage() {
        String errorMessage = "Invalid input parameters";
        ParameterInvalidException exception = new ParameterInvalidException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

    /**
     * 
     */
    @Test
    public void testConstructorWithMessageAndCause() {
        String errorMessage = "Invalid input parameters";
        Throwable cause = new RuntimeException("Parameter validation failed");
        ParameterInvalidException exception = new ParameterInvalidException(errorMessage, cause);
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
