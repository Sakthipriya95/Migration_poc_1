/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog.response.factory;

import com.bosch.caltool.apic.ws.applicationlog.response.NullResponse;
import com.bosch.caltool.apic.ws.applicationlog.response.Response;


/**
 * Factory class for all Sub-Classes of Response. For each subclass there's one method which accespts an web service
 * request object and returns an Response object with the String representation of the request.
 *
 * @author imi2si
 * @since 1.19
 */
public final class ResponseFactory {

  /**
   * @return an Response which returns an empty object
   */
  public static Response getResponse() {
    return new NullResponse();
  }

  /**
   * Private constructor. An instantiation of this class makes no sense.
   */
  private ResponseFactory() {}
}
