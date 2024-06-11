/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog.response;


/**
 * Represents an empty output as Dummy object. Can be used if there's no more specific class for a request object.
 *
 * @author imi2si
 * @since 1.19
 */
public class NullResponse extends Response {

  /**
   * {@inheritDoc}
   */
  @Override
  protected final String getSingleResponseString(final Object responseObject) {
    return null;
  }

}
