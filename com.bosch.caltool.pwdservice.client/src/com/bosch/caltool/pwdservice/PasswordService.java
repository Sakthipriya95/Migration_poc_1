/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.pwdservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;
import com.bosch.caltool.pwdservice.exception.PasswordServiceException;


/**
 * Service class to get the password from the webservice.
 *
 * @author VAU3COB
 */
public class PasswordService {

  /**
   * Available Password service servers
   */
  private static final String[] SERVER_BASE_URLS = {
      "https://si-cdm01.de.bosch.com",
      "https://si-cdm01.de.bosch.com:8543",
      "https://si-cdm02.de.bosch.com",
      "https://si-cdm02.de.bosch.com:8143",
      "https://si-cdm05.de.bosch.com",
      "https://si-cdm05.de.bosch.com:8943" };

  private static final ILoggerAdapter LOGGER = new Log4JLoggerAdapterImpl(LogManager.getLogger(PasswordService.class));

  private static int currentServerIndex = 0;

  /**
   * Service client
   */
  public PasswordService() {}

  /**
   * @param secured HTTPS or HTTP
   */
  @Deprecated
  public PasswordService(final boolean secured) {}

  /**
   * @param key to fetch the password.
   * @return password
   * @throws PasswordNotFoundException Password is not available.
   * @throws PasswordServiceException Password Service is not available
   */
  public String getPassword(final String key) throws PasswordNotFoundException {

    LOGGER.debug("Input Key : {}", key);

    boolean isIndexReset = false;
    int prevState = currentServerIndex;
    int serverIndex = currentServerIndex;

    do {
      String currentBaseUrl = SERVER_BASE_URLS[serverIndex];
      LOGGER.info(" Attempting server {}", currentBaseUrl);

      StringBuilder pwdUrl = new StringBuilder();
      // service url
      pwdUrl.append(currentBaseUrl).append("/PwdService/services/PwdWs/GetPassword?passwordName=").append(key);

      // Get password as result
      String result = doGetPassword(pwdUrl.toString());
      if (result != null) {
        currentServerIndex = serverIndex;
        return result;
      }
      // Result was unavailable with current URL so attempting next URL
      // to update the Server Index, to start from initial server
      if ((serverIndex + 1) >= SERVER_BASE_URLS.length) {
        serverIndex = -1;
        isIndexReset = true;
      }

      // to break when initial server is reached
      if (isIndexReset && ((prevState) == (serverIndex + 1))) {
        break;
      }
      serverIndex++;
    }
    while (serverIndex < SERVER_BASE_URLS.length);

    // all servers down, set server to initial state
    currentServerIndex = 0;
    // Throw Runtime exception if all the servers are down.
    LOGGER.error("Password Service not available");
    throw new PasswordServiceException("Password Service not available");
  }

  /**
   * @param url serviceurl to get the password.
   * @throws IOException
   * @throws PasswordNotFoundException Password is not available.
   */
  private String doGetPassword(final String url) throws PasswordNotFoundException {
    try {
      URL passwordService = new URL(url);
      HttpURLConnection httpCon = (HttpURLConnection) passwordService.openConnection();
      httpCon.connect();
      if (httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
        LOGGER.debug("Server returned OK");
        return readPassword(httpCon);
      }
      if (httpCon.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
        LOGGER.warn("Password Service: " + SERVER_BASE_URLS[currentServerIndex] + " is not available in this server.");
        return null;
      }
      throwErrorMessage(httpCon);

    }
    catch (IOException e) {
      LOGGER.warn("Server: " + SERVER_BASE_URLS[currentServerIndex] + " not available.", e);
    }
    return null;
  }

  /**
   * @param httpCon
   * @return
   * @throws IOException
   */
  private String readPassword(final HttpURLConnection httpCon) throws IOException {

    try (InputStreamReader isr = new InputStreamReader(httpCon.getInputStream());
        BufferedReader bufr = new BufferedReader(isr);) {

      String inputLine = bufr.readLine();
      if ((inputLine) != null) {
        return inputLine
            .replace("<ns4:PasswordResponse xmlns:ns4=\"http://pwdservice.caltool.bosch.com\"><password>", "")
            .replace("</password></ns4:PasswordResponse>", "");
      }

    }

    return null;
  }

  private void throwErrorMessage(final HttpURLConnection httpCon) throws IOException, PasswordNotFoundException {

    int respCode = httpCon.getResponseCode();

    if (respCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
      try (InputStreamReader isr = new InputStreamReader(httpCon.getErrorStream());
          BufferedReader bufr = new BufferedReader(isr);) {
        String inputLine = bufr.readLine();
        if (inputLine != null) {
          String erMsg = inputLine.replace("<faultstring>", "").replace("</faultstring>", "");
          LOGGER.warn(erMsg);
          throw new PasswordNotFoundException(erMsg);
        }
      }
    }
  }


}