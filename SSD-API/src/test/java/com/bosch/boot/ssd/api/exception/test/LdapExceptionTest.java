/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.exception.test;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bosch.boot.ssd.api.exception.LdapException;
/**
 * @author TUD1COB
 *
 */
public class LdapExceptionTest {

    /**
     * 
     */
    @Test
    public void testConstructorWithMessage() {
        String errorMessage = "LDAP operation failed";
        LdapException exception = new LdapException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

    /**
     * 
     */
    @Test
    public void testConstructorWithMessageAndCause() {
        String errorMessage = "LDAP operation failed";
        Throwable cause = new RuntimeException("Connection error");
        LdapException exception = new LdapException(errorMessage, cause);
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
