/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ICP1COB
 */
public class JacksonHelper {

  private JacksonHelper() {}

  private static ObjectMapper mapper = null;

  /**
   * @return Object Mapper
   */
  public static ObjectMapper getJacksonObjectMapper() {
    if (mapper == null) {
      mapper = new ObjectMapper();
      mapper.setSerializationInclusion(Include.NON_NULL);
    }
    return mapper;
  }
}
