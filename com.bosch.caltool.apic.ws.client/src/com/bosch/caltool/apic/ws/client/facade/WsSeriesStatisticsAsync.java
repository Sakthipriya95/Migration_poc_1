/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.facade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.apic.ws.client.ParameterStatisticCallbackHandler;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.DefaultSeriesStatisticsFilter;
import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;


/**
 * @author imi2si
 */
public class WsSeriesStatisticsAsync extends Thread {

  /**
   * Run time calculation value - 60,000 ms
   */
  private static final int RUNTIME_SIXTY_THOUSAND = 60000;

  /**
   * sleep time thread value - 1000 ms
   */
  private static final int SLEEPTIME_VAL_THOUSAND = 1000;

  /**
   * Logger for this class
   */
  private static final ILoggerAdapter LOG = ClientConfiguration.getDefault().getLogger();

  /**
   * Default Name
   */
  private final static String DEFAULT_NAME = "Async call for series statistics";

  /**
   * The list of filters passed to the web service
   */
  private final List<DefaultSeriesStatisticsFilter> filterList = new ArrayList<>();

  /**
   * The list of parameters passed to the web service
   */
  private final List<String> parameterList = new ArrayList<>();

  /**
   * The webservice client
   */
  private final APICWebServiceClient apicWsClient;

  private String name;
  private long startingTime;
  private boolean isFinished = false;
  private ParameterStatisticCallbackHandler callbackHandler;

  public WsSeriesStatisticsAsync() {
    this(new String[0], DEFAULT_NAME);
  }

  public WsSeriesStatisticsAsync(final String name) {
    this(new String[0], name);
  }

  public WsSeriesStatisticsAsync(final String params[], final String name,
      final DefaultSeriesStatisticsFilter[] filters) {
    addParameters(params);
    addFilters(filters);
    this.name = name;
    this.apicWsClient = new APICWebServiceClient();
  }

  public WsSeriesStatisticsAsync(final String params[], final String name) {
    this(params, name, new DefaultSeriesStatisticsFilter[0]);
  }

  public boolean getFinished() {
    return this.isFinished;
  }

  @Override
  public void run() {
    try {
      this.startingTime = System.currentTimeMillis();

      this.callbackHandler = (ParameterStatisticCallbackHandler) this.apicWsClient.getParameterStatisticsExtAsync(
          this.parameterList.toArray(new String[0]), this.filterList.toArray(new DefaultSeriesStatisticsFilter[0]));

      while (!isParameterAvailable() && !isBroken()) {
        Thread.sleep(SLEEPTIME_VAL_THOUSAND);

        LOG.info("{} (Runtime: {} minutes): Percentage finished {} ", this.name, calcRuntime(),
            getDegOfCompPercentage());
      }

      reportException();
      reportFileNames();
    }
    catch (Exception e) {
      LOG.error("Error while showing web service progress.", e);
    }
    finally {
      this.isFinished = true;
    }
  }

  /**
   * Adds a parameter to the list of parameters. The final list of parameters will be passed to the webservice when
   * calling method runWebService().
   *
   * @param parameters a Varargs list of parameter names
   */
  public void addParameters(final String... parameters) {
    for (String parameter : parameters) {
      this.parameterList.add(parameter);
    }
  }

  /**
   * Adds a filter to the list of filters. The final list of filters will be passed to the webservice when calling
   * method runWebService().
   *
   * @param filters a Varargs list of filters
   */
  public void addFilters(final DefaultSeriesStatisticsFilter... filters) {
    for (DefaultSeriesStatisticsFilter filter : filters) {
      this.filterList.add(filter);
    }
  }

  private long calcRuntime() {
    return (System.currentTimeMillis() - this.startingTime) / RUNTIME_SIXTY_THOUSAND;
  }

  public void waitForCompletion() {
    while (!getFinished()) {
      try {
        Thread.sleep(SLEEPTIME_VAL_THOUSAND);
      }
      catch (InterruptedException e) {
        LOG.error("Exception when waiting for web service to finish", e);
      }
    }
  }

  public double getDegOfCompPercentage() throws Exception {
    try {
      return this.apicWsClient.getStatusForAsyncExecutionResponse(getSessionId());
    }
    catch (Exception exp) {
      LOG.error("Degree of Completion can't be fetched for session" + getSessionId(), exp);
      throw exp;
    }
  }

  private void reportFileNames() {
    if (!this.callbackHandler.isBroken()) {

      try {
        for (CalData element : getFiles()) {
          LOG.info("Recived file: {}", element.getCalDataPhy().getName());
        }
      }
      catch (ClassNotFoundException | IOException e) {
        LOG.error("Error while recieving file names.", e);
      }
    }
  }

  private void reportException() {
    if (this.callbackHandler.isBroken()) {
      LOG.error("Error while recieving data: " + this.callbackHandler.getParameterException().getLocalizedMessage());
    }
  }

  /**
   * @param name the name to set
   */
  public void setWsName(final String name) {
    this.name = name;
  }

  /**
   * @return the name
   */
  public String getWsName() {
    return this.name;
  }

  /**
   * @return the callbackHandler
   */
  public ParameterStatisticCallbackHandler getCallbackHandler() {
    return this.callbackHandler;
  }

  public CalData[] getFiles() throws ClassNotFoundException, IOException {
    return this.callbackHandler.getFiles();
  }

  public String getSessionId() {
    return this.callbackHandler.getSessionID();
  }

  public boolean isBroken() {
    return this.callbackHandler.isBroken();
  }

  public boolean isParameterAvailable() {
    return this.callbackHandler.isParameterAvailable();
  }
}
