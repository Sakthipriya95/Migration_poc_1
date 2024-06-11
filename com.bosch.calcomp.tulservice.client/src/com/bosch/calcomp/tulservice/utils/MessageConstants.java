/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.utils;

/**
 * Util class for common data user by Tool Logger Client
 * 
 * @author GDH9COB
 */
public class MessageConstants {

  private MessageConstants() {}

  /**
   * HTTP POST Method
   */
  public static final String HTTP_METHOD_POST = "POST";

  /**
   * Request Header Content Type
   */
  public static final String CONTENT_TYPE = "Content-Type";

  /**
   * Data type JSON for Accept and Content Type
   */
  public static final String DATA_TYPE_JSON = "application/json";

  /**
   * Encoding methodology for Data model
   */
  public static final String UTF8_CHAR_ENCODE_TYPE = "utf-8";

  /**
   * Content type JSON with UTF8 encoding
   */
  public static final String CONTENT_TYPE_JSON_UTF8 =
      new StringBuilder(DATA_TYPE_JSON).append("; ").append(UTF8_CHAR_ENCODE_TYPE).toString();

  /**
   * Request Header Accept
   */
  public static final String ACCEPT = "Accept";

  /**
   * 
   */
  public static final String USERNAME = "\"username\"";

  /**
   * 
   */
  public static final String MISC = "\"misc\"";
  /**
   * 
   */
  public static final String SCHEMA_VER = "\"schemaVer\"";
  /**
   * 
   */
  public static final String TEST = "\"isTest\"";
  /**
   * 
   */
  public static final String ARTIFACT_INFO = "\"artifactInfo\"";
  /**
   * 
   */
  public static final String EVENT = "\"event\"";
  /**
   * 
   */
  public static final String JOB_ID = "\"jobID\"";
  /**
   * 
   */
  public static final String FEATURE = "\"feature\"";
  /**
   * 
   */
  public static final String COMPONENT = "\"component\"";
  /**
   * 
   */
  public static final String TOOL_VERSION = "\"toolVersion\"";
  /**
   * 
   */
  public static final String TOOL = "\"tool\"";
  /**
   * 
   */
  public static final String TOOL_CATEGORY = "\"toolCategory\"";
  /**
   * 
   */
  public static final String TIMESTAMP = "\"timestamp\"";
  /**
   * 
   */
  public static final String CONTEXT = "\"context\"";
  /**
   * 
   */
  public static final String TOOL_LANGUAGE = "\"toolLanguage\"";
  /**
   * 
   */
  public static final String MACHINE_TYPE = "\"machine\"";
  /**
   * 
   */
  public static final String NETWORK_TYPE = "\"network\"";
  /**
   * 
   */
  public static final String CPULOAD = "\"cpuLoad\"";
  /**
   * 
   */
  public static final String TOTAL_CORES = "\"totalCores\"";
  /**
   * 
   */
  public static final String USED_CORES = "\"usedCores\"";
  /**
   * 
   */
  public static final String CLOCK_RATE = "\"clockRate\"";
  /**
   * 
   */
  public static final String FREE_RAM = "\"freeRAM\"";
  /**
   * 
   */
  public static final String TOTAL_RAM = "\"totalRAM\"";
  /**
   * 
   */
  public static final String STORAGE_TYPE = "\"storageType\"";
  /**
   * 
   */
  public static final String TOTAL_STORAGE = "\"totalStorage\"";
  /**
   * 
   */
  public static final String FREE_STORAGE = "\"freeStorage\"";
  /**
   * 
   */
  public static final String OS_TYPE = "\"osType\"";
  /**
   * 
   */
  public static final String OS_VERSION = "\"osVersion\"";
  /**
   * 
   */
  public static final String OS_LANGUAGE = "\"osLanguage\"";
  /**
   * 
   */
  public static final String SCHEMA_VERSION_SPEC = "1.4";

  /**
   * 
   */
  public static final String IS_TEST_PROP_KEY = "tul.isTest";

  /**
   * 
   */
  public static final String OFFLINE_DATA_FILE_PATH = "tul.offlineDataFilePath";

  /**
   * 
   */
  public static final String IS_TEST_FLAG_DEFAULT_VALUE = "true";
}
