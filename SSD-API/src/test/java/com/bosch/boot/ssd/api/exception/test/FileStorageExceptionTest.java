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

import com.bosch.boot.ssd.api.exception.FileStorageException;

/**
 * @author TUD1COB
 *
 */
public class FileStorageExceptionTest {

    /**
     * 
     */
    @Test
    public void testConstructorWithMessage() {
        String errorMessage = "Error storing the file";
        FileStorageException exception = new FileStorageException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

    /**
     * 
     */
    @Test
    public void testConstructorWithMessageAndCause() {
        String errorMessage = "Error storing the file";
        Throwable cause = new RuntimeException("IO error");
        FileStorageException exception = new FileStorageException(errorMessage, cause);
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}

