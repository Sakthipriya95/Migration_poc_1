/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient.ss;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import com.bosch.calmodel.caldataphy.CalDataPhyValue;
import com.bosch.caltool.apic.ws.client.ParameterStatisticCallbackHandler;
import com.bosch.caltool.apic.ws.client.facade.WsSeriesStatisticsAsync;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.DefaultSeriesStatisticsFilter;
import com.bosch.caltool.apic.ws.client.serviceclient.AbstractSoapClientTest;


/**
 * @author imi2si
 */
public abstract class AbstractSeriesStatisticsTest extends AbstractSoapClientTest {

  /**
   * The web service client
   */
  protected WsSeriesStatisticsAsync webServiceClient;

  /**
   * Reads parameters out of an parameter file
   */
  private final ParameterLoader paramLoader = new ParameterLoader();

  protected final void addParamPropFile(final String parameterKey) {
    this.webServiceClient.addParameters(this.paramLoader.getParameters(parameterKey));
  }

  protected final void addParameters(final String... parameters) {
    this.webServiceClient.addParameters(parameters);
  }

  protected final void addFilters(final DefaultSeriesStatisticsFilter... filters) {
    this.webServiceClient.addFilters(filters);
  }

  protected final void setWsName(final String name) {
    this.webServiceClient.setWsName(name);
  }

  protected final void runService() {
    this.webServiceClient.start();
  }

  protected final void runServiceAndWait() {
    this.webServiceClient.start();
    this.webServiceClient.waitForCompletion();
  }

  protected final void waitForCompletion() {
    this.webServiceClient.waitForCompletion();
  }

  protected final void wait(final int seconds) {
    try {
      Thread.sleep(seconds * 1000);
    }
    catch (InterruptedException e) {
      LOG.error("Error in wait method", e);
    }
  }

  protected final WsSeriesStatisticsAsync getSeriesStatisticsWs() {
    return this.webServiceClient;
  }

  protected final ParameterStatisticCallbackHandler getSeriesStatisticsCallback() {
    return this.webServiceClient.getCallbackHandler();
  }

  /**
   * Checks, if the result by the web service equals to the excepted result for a passed parameter.
   *
   * @param parameter the parameter in the web service result to check
   * @param exceptedResult the excepted result in the web service
   */
  protected void assertResult(final String parameter, final String exceptedResult) {
    String webServiceResult = "NullObject";

    try {
      CalDataPhyValue calDataPhyResult =
          (CalDataPhyValue) getSeriesStatisticsCallback().getFile(parameter).getCalDataPhy();
      webServiceResult = calDataPhyResult.getAtomicValuePhy().getSValue();
    }
    catch (ClassNotFoundException | IOException | NullPointerException e) {
      LOG.error("Exception analyzing the result", e);
    }

    assertTrue("Result of WebService (" + webServiceResult + ") differs from excepted result (" + exceptedResult + ")",
        webServiceResult.equalsIgnoreCase(exceptedResult));
  }
}
