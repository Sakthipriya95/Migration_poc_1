/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author pdh2cob
 */
public class JsonUtil {

  /**
  *
  */
  private static final String FILE_SEPERATOR = "\\";


  /**
   * @param jsonStream jsonStream
   * @param classType classType
   * @return the parse file Java model
   * @throws InvalidInputException InvalidInputException
   */
  public <T> T toModel(final InputStream jsonStream, final Class<T> classType) throws InvalidInputException {
    T input = null;
    try {
      ObjectMapper mapper = new ObjectMapper();
      input = mapper.readValue(jsonStream, classType);

    }
    catch (IOException exception) {
      throw new InvalidInputException("Invalid JSON input", exception);
    }

    return input;
  }

  /**
   * @param jsonFile - input json file to parse
   * @param classType classType
   * @return the parse file Java model
   * @throws InvalidInputException InvalidInputException
   */
  public static <T> T toModel(final File jsonFile, final Class<T> classType) throws InvalidInputException {
    T input = null;
    try {
      ObjectMapper mapper = new ObjectMapper();
      input = mapper.readValue(jsonFile, classType);

    }
    catch (IOException exception) {
      throw new InvalidInputException("Invalid JSON input", exception);
    }

    return input;
  }

  /**
   * create the dummy Json file
   *
   * @param classObj classObj
   * @param outputPath output Path
   * @param fileName file name to store
   * @throws IcdmException IoException
   */
  public static <T> void toJsonFile(final T classObj, final String outputPath, final String fileName)
      throws IcdmException {

    ObjectMapper mapper = new ObjectMapper();

    try {
      // Writing to a file
      mapper.writeValue(new File(outputPath + FILE_SEPERATOR + fileName), classObj);
    }
    catch (IOException exp) {
      throw new IcdmException("Error when creating JSON file", exp);
    }

  }

}
