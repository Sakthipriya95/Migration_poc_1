/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.vector.easee.application.cdmdomainservice;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;


/**
 * @author imi2si
 */
public class CDMDomainServiceClient {

  /**
   * @author hef2fe
   */
  public enum CDMDomainnWebService {
    /**
     * local server
     */
    // ICDM-218
    PROD_SERVER("PROD_SERVER");


    private String literal;

    /**
     * @return String
     */
    // ICDM-218
    public String getLiteral() {
      return this.literal;
    }

    /**
     * This method returns the APICWsServer
     * 
     * @param literl defines server type
     * @return APICWsServer
     */
    // ICDM-218
    public static CDMDomainnWebService getAPICWsServer(final String literl) {
      for (CDMDomainnWebService apicWsServer : CDMDomainnWebService.values()) {
        if (apicWsServer.literal.equals(literl)) {
          return apicWsServer;
        }
      }
      return null;
    }

    // ICDM-218
    CDMDomainnWebService(final String literl) {
      this.literal = literl;
    }
  }

  private static final String DEFAULT_SESSION = "DUMMY";

  private CDMDomainServiceStub stub = null;


  /**
   * @param apicWsServer
   */
  public CDMDomainServiceClient(final CDMDomainnWebService cdmWsServer) {

    String endpoint = "";

    switch (cdmWsServer) {
      case PROD_SERVER:
        endpoint = "https://rb-dgscdmproweb.de.bosch.com:12943/DGS_CDM_PRO_WEBSRV/services/CDMDomainService";

        break;

      default:
        break;
    }

    try {
      this.stub = new CDMDomainServiceStub(endpoint);
      // Get service client options
      // ICDM-218
      Options op = this.stub._getServiceClient().getOptions();
      op.setManageSession(true);
      // APIC Web service session time out 30 minutes
      Integer time = new Integer(30 * 60 * 1000);
      // Set session timeout
      op.setProperty(HTTPConstants.SO_TIMEOUT, time);
      // Set connection timeout
      op.setProperty(HTTPConstants.CONNECTION_TIMEOUT, time);
      op.setProperty(HTTPConstants.CHUNKED, "false");
      this.stub._getServiceClient().setOptions(op);
    }
    catch (AxisFault e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.SearchObjectsByExprResponseType searchObjectsByExpr(

      final com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.SearchObjectsByExprRequestType searchObjectsByExprRequestType0)


  throws java.rmi.RemoteException, SearchObjectsByExpr_faultMsg {
    return this.stub.searchObjectsByExpr(searchObjectsByExprRequestType0);
  }


}