/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client;

/**
 * @author bne4cob
 */
enum ICDM_WS_SERVER {
                     /**
                      * local server - local host
                      */
                     LOCAL_SERVER("LOCAL_SERVER", "http://localhost:8099/com.bosch.caltool.apic.ws"),
                     /**
                      * iCDM Server 01 - PRO server for iCDM client
                      */
                     ICDM_01_SERVER("ICDM_01_SERVER", "http://si-cdm01.de.bosch.com:8180/APIC_WS2"),
                     /**
                      * iCDM Server 02 (non iCDM)
                      */
                     ICDM_02_SERVER("ICDM_02_SERVER", "http://si-cdm01.de.bosch.com:8280/APIC_WS2"),
                     /**
                      * iCDM Server 05 (non iCDM)
                      */
                     ICDM_05_SERVER("ICDM_05_SERVER", "http://si-cdm01.de.bosch.com:8580/APIC_WS2"),
                     /**
                      * iCDM Server 06 - BETA testing
                      */
                     ICDM_06_SERVER("ICDM_06_SERVER", "http://si-cdm01.de.bosch.com:8680/APIC_WS2"),
                     /**
                      * iCDM Server 07 - DEV server
                      */
                     ICDM_07_SERVER("ICDM_07_SERVER", "http://si-cdm02.de.bosch.com:8780/APIC_WS2"),
                     /**
                      * iCDM Server 08 - PRO server for external clients
                      */
                     ICDM_08_SERVER("ICDM_08_SERVER", "http://si-cdm01.de.bosch.com:8880/APIC_WS2"),
                     /**
                      * iCDM Server 09 - Jenkins build test server
                      */
                     ICDM_09_SERVER("ICDM_09_SERVER", "http://si-cdm02.de.bosch.com:8980/APIC_WS2"),
                     /**
                      * iCDM Server 01 - Jenkins build test server
                      */
                     ICDM_DEV_SERVER("ICDM_DEV_SERVER", "https://si-cdm05.de.bosch.com:8143/APIC_WS2");


  private String literal;
  private String baseUri;

  /**
   * @return String
   */
  // ICDM-218
  public String getLiteral() {
    return this.literal;
  }

  /**
   * This method returns the ICDM_WS_SERVER represented by literal
   *
   * @param literal server type or null
   * @return APICWsServer
   */
  // ICDM-218
  public static ICDM_WS_SERVER getServer(final String literal) {
    for (ICDM_WS_SERVER apicWsServer : ICDM_WS_SERVER.values()) {
      if (apicWsServer.literal.equals(literal)) {
        return apicWsServer;
      }
    }
    return null;
  }

  // ICDM-218
  ICDM_WS_SERVER(final String literl, final String baseUri) {
    this.literal = literl;
    this.baseUri = baseUri;
  }

  /**
   * @return the baseUri
   */
  public String getBaseUri() {
    return this.baseUri;
  }
}