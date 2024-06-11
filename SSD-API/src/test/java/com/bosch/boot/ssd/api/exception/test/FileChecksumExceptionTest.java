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

import com.bosch.boot.ssd.api.exception.FileChecksumException;

/**
 * @author TUD1COB
 *
 */
public class FileChecksumExceptionTest {

    /**
     * 
     */
    @Test
    public void testConstructorWithMessage() {
        String errorMessage = "Checksum calculation failed";
        FileChecksumException exception = new FileChecksumException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

    /**
     * 
     */
    @Test
    public void testConstructorWithMessageAndCause() {
        String errorMessage = "Checksum calculation failed";
        Throwable cause = new RuntimeException("IO error");
        FileChecksumException exception = new FileChecksumException(errorMessage, cause);
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
