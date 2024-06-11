/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog.request;


/**
 * Represents an empty output as Dummy object. Can be used if there's no more specific class for a request object.
 * 
 * @author imi2si
 * @since 1.19
 */
public class NullRequest extends AbstractRequest {

  /**
   * {@inheritDoc}
   */
  @Override
  protected final String getSingleRequestString(final Object requestObject) {
    // TODO Auto-generated method stub
    return null;
  }

}
