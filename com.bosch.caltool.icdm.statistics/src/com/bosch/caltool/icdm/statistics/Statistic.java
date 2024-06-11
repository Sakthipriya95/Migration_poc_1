/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardInfoType;
import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient;
import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient.APICWsServer;
import com.bosch.caltool.apic.ws.client.serviceclient.CachedAPICWebService;
import com.bosch.caltool.icdm.statistics.adapter.AbstractIcdmMetaData;
import com.bosch.caltool.icdm.statistics.adapter.AbstractIcdmStatistics;
import com.bosch.caltool.icdm.statistics.adapter.IcdmMetaDataWebServiceAdapter;
import com.bosch.caltool.icdm.statistics.adapter.IcdmStatisticsWebServiceAdapter;
import com.bosch.caltool.icdm.statistics.filter.CustomerFilter;
import com.bosch.caltool.icdm.statistics.filter.IStatisticFilter;
import com.bosch.caltool.icdm.statistics.output.AbstractStatisticOutput;
import com.bosch.caltool.icdm.statistics.output.ExcelStatisticOutput;
import com.bosch.caltool.icdm.statistics.output.StringStatisticOutput;


/**
 * @author imi2si
 */
public class Statistic {


  private final APICWebServiceClient client;
  private final ILoggerAdapter logger;
  private final List<IStatisticFilter> filters;

  /**
   * The main method, which also shows examples for using the class
   *
   * @param args command line arguments
   */
  public static void main(final String[] args) throws IOException, FileNotFoundException {

    ILoggerAdapter logger = new Log4JLoggerAdapterImpl(LogManager.getLogger(Statistic.class));

    final CachedAPICWebService apicWs = new CachedAPICWebService(APICWsServer.LOCAL_SERVER);

    Statistic statistic = new Statistic(apicWs, logger);
    statistic.addFilter(new CustomerFilter("X_Testcustomer", false, apicWs, logger));
    statistic.getExcelStatisticOutput("C:\\Temp\\TestStatisticsOutput.xlsx");
  }

  /**
   * Creates a default statistic class with a given webservice client object
   *
   * @param client the web service client object
   */
  public Statistic(final APICWebServiceClient client) {
    this(client, new Log4JLoggerAdapterImpl(LogManager.getLogger(Statistic.class)));
  }

  /**
   * Creates a default statistic object with access on the given webservice server
   *
   * @param server the web service server
   */
  public Statistic(final APICWebServiceClient.APICWsServer server) {
    this(server, new Log4JLoggerAdapterImpl(LogManager.getLogger(Statistic.class)));
  }

  /**
   * Creates a default statistic class with a given webservice client object
   *
   * @param client the web service client object
   * @param logger the logger
   */
  public Statistic(final APICWebServiceClient client, final ILoggerAdapter logger) {
    this.logger = logger;
    this.client = client;
    this.filters = new ArrayList<>();
  }

  /**
   * Creates a default statistic object with access on the given webservice server
   *
   * @param server the web service server
   * @param logger the logger
   */
  public Statistic(final APICWebServiceClient.APICWsServer server, final ILoggerAdapter logger) {
    this.logger = logger;
    this.client = new APICWebServiceClient(server);
    this.filters = new ArrayList<>();
  }

  /**
   * Creates an Excelsheet based on the gathered statistics.
   */
  public void getExcelStatisticOutput(final String filename) throws IOException, FileNotFoundException {
    this.logger.info("Start generating excel file");

    AbstractStatisticOutput output;

    output =
        new ExcelStatisticOutput(filename, createMetaData(), createStatistics().toArray(new AbstractIcdmStatistics[0]));
    output.createOutput();

    this.logger.info("End generating excel file");
  }

  /**
   * Creates the statistic as String object
   *
   * @return the Statistics as String output
   */
  public String getStringStatisticOutput() throws IOException, FileNotFoundException {
    AbstractStatisticOutput output =
        new StringStatisticOutput(createMetaData(), createStatistics().toArray(new AbstractIcdmStatistics[0]));

    output.createOutput();

    this.logger.info((String) output.getOutput());

    return (String) output.getOutput();
  }

  /**
   * Adds a new filter to the statistic. Call this method before getting the output with the get...Output() methods
   *
   * @param filter an filter object
   */
  public void addFilter(final IStatisticFilter filter) {
    this.filters.add(filter);
  }

  /**
   * Creates a metadata object that represents data that not belongs to one PIDC, for example information about
   * attributes
   *
   * @return an AbstractIcdmMetaData object
   */
  private AbstractIcdmMetaData createMetaData() {
    try {
      return new IcdmMetaDataWebServiceAdapter(this.client);
    }
    catch (Exception e) {
      this.logger.error("Error when creating statistics", e);
      return null;
    }
  }

  /**
   * Creates the statistics with the given web service object considering the filters. Called within the get...Output()
   * methods
   *
   * @return a set that contains statistical information for all the PIDCs
   */
  private Set<AbstractIcdmStatistics> createStatistics() {
    Set<AbstractIcdmStatistics> pidcStatistics = new TreeSet<>(new Comparator<AbstractIcdmStatistics>() {

      @Override
      public int compare(final AbstractIcdmStatistics obj1, final AbstractIcdmStatistics obj2) {

        int comparisonResult = obj1.getPidcName().compareToIgnoreCase(obj2.getPidcName());

        /*
         * If the names are equal, return -1, to ensure the PIDCs with the same name are not filtered out. Another
         * suitable member is not available for comparison
         */
        return comparisonResult == 0 ? -1 : comparisonResult;
      }
    });

    try {

      ProjectIdCardInfoType[] allPidc = this.client.getAllPidc();

      pidc: for (ProjectIdCardInfoType pidc : allPidc) {
        for (IStatisticFilter filter : this.filters) {
          if (!filter.filter(pidc)) {

            this.logger.debug("Filtered out: " + pidc.getName() + " is not considered for report");
            continue pidc;
          }
        }

        this.logger.debug("Create statistics for " + pidc.getName());
        pidcStatistics.add(new IcdmStatisticsWebServiceAdapter(this.client, pidc, this.logger));
      }
    }
    catch (Exception e) {
      this.logger.error("Error when creating statistics", e);
    }

    return pidcStatistics;
  }

}
