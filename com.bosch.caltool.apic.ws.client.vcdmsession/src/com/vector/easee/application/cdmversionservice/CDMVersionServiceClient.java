/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.vector.easee.application.cdmversionservice;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;

/**
 * @author imi2si
 */
public class CDMVersionServiceClient {

  /**
   * @author hef2fe
   */
  public enum CDMVersionService {
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
    public static CDMVersionService getAPICWsServer(final String literl) {
      for (CDMVersionService apicWsServer : CDMVersionService.values()) {
        if (apicWsServer.literal.equals(literl)) {
          return apicWsServer;
        }
      }
      return null;
    }

    // ICDM-218
    CDMVersionService(final String literl) {
      this.literal = literl;
    }
  }

  private static final String DEFAULT_SESSION = "DUMMY";

  private CDMVersionServiceStub stub = null;


  /**
   * @param apicWsServer
   */
  public CDMVersionServiceClient(final CDMVersionService cdmWsServer) {

    String endpoint = "";

    switch (cdmWsServer) {
      case PROD_SERVER:
        endpoint = "https://rb-dgscdmproweb.de.bosch.com:12943/DGS_CDM_PRO_WEBSRV/services/CDMVersionService";

        break;

      default:
        break;
    }

    try {
      this.stub = new CDMVersionServiceStub(endpoint);
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


  public com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.GetVersionAttributesResponseType getVersionAttributes(

      final com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.GetVersionAttributesRequestType getVersionAttributesRequestType)

  throws java.rmi.RemoteException, GetVersionAttributes_faultMsg {
    return this.stub.getVersionAttributes(getVersionAttributesRequestType);
  }

  public com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.FetchArtifactResponseType fetchArtifact(

      final com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.FetchArtifactRequestType fetchArtifactRequestType10)


  throws java.rmi.RemoteException, FetchArtifact_faultMsg {
    return this.stub.fetchArtifact(fetchArtifactRequestType10);
  }


}