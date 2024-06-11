/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.adapter;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.caltool.apic.ws.client.APICStub.Attribute;
import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient;


/**
 * @author imi2si
 */
public class IcdmMetaDataWebServiceAdapter extends AbstractIcdmMetaData {

  /**
   * Logger for this class
   */
  private static ILoggerAdapter LOG =
      new Log4JLoggerAdapterImpl(LogManager.getLogger(IcdmMetaDataWebServiceAdapter.class));

  /**
   * The APIC web service client that delivers that fetches the information for the statistics
   */
  private final APICWebServiceClient client;

  /**
   * Constructs a new adapter for the given webservice client and the PIDC
   *
   * @param client the APICWebServiceClient that is used to fetch attribute details
   * @param pidc the ProjectIdCardInfoType that has all information for the adapter
   * @throws Exception when web service calls can't be executed
   */
  public IcdmMetaDataWebServiceAdapter(final APICWebServiceClient client) throws Exception {
    this(client, new Log4JLoggerAdapterImpl(LogManager.getLogger(IcdmMetaDataWebServiceAdapter.class)));
  }

  /**
   * Constructs a new adapter for the given webservice client and the PIDC
   *
   * @param client the APICWebServiceClient that is used to fetch attribute details
   * @param pidc the ProjectIdCardInfoType that has all information for the adapter
   * @param logger the ILoggerAdapter used to log messages
   * @throws Exception when web service calls can't be executed
   */
  public IcdmMetaDataWebServiceAdapter(final APICWebServiceClient client, final ILoggerAdapter logger)
      throws Exception {
    this.client = client;
    IcdmMetaDataWebServiceAdapter.LOG = logger;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPidcNoOfMandAttributes() {
    int mandatoryAttributes = 0;

    try {
      Attribute[] attributes = this.client.getAllAttributes();

      for (Attribute attribute : attributes) {
        if (attribute.getIsMandatory()) {
          mandatoryAttributes++;
        }
      }
      return mandatoryAttributes;
    }
    catch (Exception e) {
      LOG.error("Error when fetching PIDC attributes", e);
      return mandatoryAttributes;
    }
  };

}
