/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service;

import java.io.File;
import java.io.IOException;

import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author rgo7cob
 */
public final class JsonWriter {


  /**
   *
   */
  private static final String FILE_SEPERATOR = "\\";

  /**
   * private Consrtuctor added
   */
  private JsonWriter() {
    super();
  }

  /**
   * create the dummy Json file
   *
   * @param classObj classObj
   * @param outputPath output Path
   * @param fileName file name to store
   * @throws IcdmException IoException
   */
  public static <T> void createJsonFile(final T classObj, final String outputPath, final String fileName)
      throws IcdmException {

    ObjectMapper mapper = new ObjectMapper();

    try {
      // Writing to a file
      mapper.writeValue(new File(outputPath + FILE_SEPERATOR + fileName), classObj);
    }
    catch (IOException exp) {
      throw new IcdmException("Error when creating JSON File", exp);
    }

  }


}