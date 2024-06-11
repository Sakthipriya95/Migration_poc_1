/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.tulservice.exception.ToolUsageLoggingException;
import com.bosch.calcomp.tulservice.internal.model.ToolUsageStat;
import com.bosch.calcomp.tulservice.model.ToolLogData;
import com.bosch.calcomp.tulservice.utils.MessageConstants;
import com.bosch.calcomp.tulservice.utils.OfflineDataFileHandler;
import com.bosch.calcomp.tulservice.utils.ToolUsageStatUtils;


/**
 * @author GDH9COB
 */
public class ToolUsageLoggerClient {

  /**
   * The default connection timeout for cantacting Tul server
   */
  private static final int CONNECTION_TIMEOUT = 5000;
  /**
   * The default read timeout for cantacting Tul server
   */
  private static final int READ_TIMEOUT = 5000;

  private ILoggerAdapter logger;

  private String serviceUrl = null;


  /**
  *
  */
  public ToolUsageLoggerClient(ILoggerAdapter logger) {
    this.serviceUrl = ToolUsageStatUtils.getResponseUrl(logger);
    this.logger=logger;
  }


  /**
   * @param toolData ToolLogData
   * @return true if success
   */
  public boolean postToolUsageData(final ToolLogData toolData) {

    ToolUsageStat toolUsageStat = getDataWithPreFilledToolUsageStat();

    toolUsageStat.setJobID(toolData.getJobID());
    toolUsageStat.setUsername(toolData.getUsername());
    toolUsageStat.setTool(toolData.getTool());
    toolUsageStat.setToolVersion(toolData.getToolVersion());
    toolUsageStat.setToolCategory(toolData.getToolCategory());
    toolUsageStat.setArtifactInfo(toolData.getArtifactInfo());
    toolUsageStat.setComponent(toolData.getComponent());
    toolUsageStat.setEvent(toolData.getEvent());
    toolUsageStat.setFeature(toolData.getFeature());
    toolUsageStat.setMisc(toolData.getMisc());
    toolUsageStat.setSchemaVer(MessageConstants.SCHEMA_VERSION_SPEC);
    setTestFlag(toolUsageStat);
    OfflineDataFileHandler.setFileLocation(toolUsageStat);
    String json = ToolUsageStatUtils.constructJSONString(Arrays.asList(toolUsageStat));
    boolean isPostToolUsageDataSuccessful = postToolUsageStatJSON(json);
    try {
      OfflineDataScheduler.getInstance().scheduleTimer(isPostToolUsageDataSuccessful, toolUsageStat.getFilePath(), json,
          true, this.logger);
    }
    catch (IOException e) {
      throw new ToolUsageLoggingException("Error handling offline datas", e);
    }
    return isPostToolUsageDataSuccessful;
  }

  //isTestSystem - will be used by caltools and icdm
  //isTestEnv - will be used for python scripts
  private void setTestFlag(final ToolUsageStat toolUsageStat) {
    String isTestSystem = System.getProperty(MessageConstants.IS_TEST_PROP_KEY);
    String isTestEnv = System.getenv(MessageConstants.IS_TEST_PROP_KEY);
    if(("true".equalsIgnoreCase(isTestSystem) || "false".equalsIgnoreCase(isTestSystem)))
    {
      toolUsageStat.setIsTest(isTestSystem);
    }
    else if(("true".equalsIgnoreCase(isTestEnv) || "false".equalsIgnoreCase(isTestEnv)))
    {
      toolUsageStat.setIsTest(isTestEnv);
    }
    else if(isTestSystem==null &&isTestEnv==null)
    {
      toolUsageStat.setIsTest(MessageConstants.IS_TEST_FLAG_DEFAULT_VALUE);
    }
    else
    {
      throw new ToolUsageLoggingException("Invalid TUL isTest property. Should be set to either true or false.");
    }
  }

  /**
   * Provides the prefilled ToolUsageStat
   *
   * @return ToolUsageStat
   */
  private ToolUsageStat getDataWithPreFilledToolUsageStat() {
    return ToolUsageStatUtils.getInstance().createPreFilledToolUsageStat();
  }

  /**
   * Method to call POST method with ToolUsageStat data
   *
   * @param json JSON String
   * @return true/false
   * @throws ToolUsageLoggingException Exception
   */
  public boolean postToolUsageStatJSON(final String json) {

    if (json.isEmpty()) {
      throw new ToolUsageLoggingException("ToolUsageStat data is not provided");
    }

    try {
      this.logger.debug("Connecting to TUL URL - {}", this.serviceUrl);
      URLConnection urlCon = null;

      // Create URL object with the target URI
      URL tulService = new URL(this.serviceUrl);
      // Invoke openConnection method to get HttpURLConnection
      urlCon = tulService.openConnection();


      if (urlCon instanceof HttpURLConnection) {
        HttpURLConnection httpCon = (HttpURLConnection) urlCon;

        // Set request method property as POST
        httpCon.setRequestMethod(MessageConstants.HTTP_METHOD_POST);

        // Set Request Content-Type Header
        httpCon.setRequestProperty(MessageConstants.CONTENT_TYPE, MessageConstants.CONTENT_TYPE_JSON_UTF8);
        // Set Response type that is acceptable
        httpCon.setRequestProperty(MessageConstants.ACCEPT, MessageConstants.DATA_TYPE_JSON);
        httpCon.setConnectTimeout(CONNECTION_TIMEOUT);
        httpCon.setReadTimeout(READ_TIMEOUT);

        // send request body in POST method
        httpCon.setDoOutput(true);
        // httpCon.connect()

        this.logger.debug("Tool Usage Data: {}", json);

        // convert string to bytes and write in HttpURLConnection outputstream
        try (OutputStream os = httpCon.getOutputStream()) {
          byte[] input = json.getBytes(StandardCharsets.UTF_8);
          os.write(input, 0, input.length);
        }

        // Get response code
        if (httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
          this.logger.info("ToolUsageData logged into TUL successfully");
          return true;
        }

        // Response was not HTTP_OK. Throw error message
        throwErrorMessage(httpCon);
      }
    }
    catch (IOException e) {
      this.logger.error("Error occured while logging ToolUsageData in TUL client "+ e.getMessage());
      return false;
    }
    return false;
  }

  /**
   * @param httpCon
   */
  private void throwErrorMessage(final HttpURLConnection httpCon) throws IOException {

    try (InputStreamReader isr = new InputStreamReader(httpCon.getErrorStream());
        BufferedReader bufr = new BufferedReader(isr);) {
      String inputLine = bufr.readLine();
      if (inputLine != null) {
        String errorMsg = inputLine.replace("<faultstring>", " ").replace("</faultstring>", "");
        throw new IOException(errorMsg);
      }
    }
  }


}
