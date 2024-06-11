/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service;

import java.io.IOException;
import java.io.InputStream;

import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author rgo7cob
 */
public class JsonParser {


  /**
   * @param jsonStream jsonStream
   * @param classType classType
   * @return the parse file Java model
   * @throws InvalidInputException InvalidInputException
   */
  public <T> T parseStreamToObject(final InputStream jsonStream, final Class<T> classType)
      throws InvalidInputException {
    T input = null;
    try {
      ObjectMapper mapper = new ObjectMapper();
      input = mapper.readValue(jsonStream, classType);

    }
    catch (IOException e) {
      throw new InvalidInputException("GENERAL.JSON_INVALID", e);
    }

    return input;
  }

}
