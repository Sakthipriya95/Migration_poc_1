/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.vector.easee.application.cdmsessionservice;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;

import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.security.Decryptor;
import com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LoginResponseType;
import com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LogoutResponseType;


/**
 * @author imi2si
 */
public class CDMSessionWebServiceClient {


  /**
   * Easse web service domain
   */
  private static final String EASEE_SERVICE_DOMAAIN_NAME = "EASEEService.DOMAAIN_NAME";

  /**
   * Easse web service password
   */
  private static final String EASEE_SERVICE_USER_PASSWORD = "EASEEService.USER_PASS";

  /**
   * Easse web service user name
   */
  private static final String EASEE_SERVICE_USER_NAME = "EASEEService.USER_NAME";


  /**
   * @author hef2fe
   */
  public enum CDMSessionWebService {
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
    public static CDMSessionWebService getAPICWsServer(final String literl) {
      for (CDMSessionWebService apicWsServer : CDMSessionWebService.values()) {
        if (apicWsServer.literal.equals(literl)) {
          return apicWsServer;
        }
      }
      return null;
    }

    // ICDM-218
    CDMSessionWebService(final String literl) {
      this.literal = literl;
    }
  }

  private static final String DEFAULT_SESSION = "DUMMY";

  private CDMSessionServiceStub stub = null;


  /**
   * @param apicWsServer
   */
  public CDMSessionWebServiceClient(final CDMSessionWebService cdmWsServer) {

    String endpoint = "";

    switch (cdmWsServer) {
      case PROD_SERVER:
        endpoint = "https://rb-dgscdmproweb.de.bosch.com:12943/DGS_CDM_PRO_WEBSRV/services/CDMSessionService";

        break;

      default:
        break;
    }

    try {
      this.stub = new CDMSessionServiceStub(endpoint);
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

  public com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LoginResponseType login()


      throws java.rmi.RemoteException, Login_faultMsg {
    CDMSessionServiceStub.LoginRequestType loginRequest = new CDMSessionServiceStub.LoginRequestType();

    System.out.println("using cacerts file: " + System.getProperty("javax.net.ssl.trustStore"));

    // Icdm-1488- Get the Values from message.properties file.
    // Set the User name
    loginRequest.setUserName(Messages.getString(EASEE_SERVICE_USER_NAME));
    // Set the pass word use the decryptor
    String eassePass =
        Decryptor.getInstance().decrypt(Messages.getString(EASEE_SERVICE_USER_PASSWORD), CDMLogger.getInstance());
    loginRequest.setPassWord(eassePass);
    // Set the domain name
    loginRequest.setDomainName(Messages.getString(EASEE_SERVICE_DOMAAIN_NAME));

    LoginResponseType res = this.stub.login(loginRequest);

    return res;
  }

  public com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LogoutResponseType logout(

      final String sesHandle)


          throws java.rmi.RemoteException, Logout_faultMsg {
    CDMSessionServiceStub.LogoutRequestType req = new CDMSessionServiceStub.LogoutRequestType();
    req.setSesHandle(sesHandle);
    LogoutResponseType res = this.stub.logout(req);

    return res;
  }


}