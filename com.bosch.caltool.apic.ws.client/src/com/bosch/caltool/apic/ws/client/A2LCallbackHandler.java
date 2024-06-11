/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client;

import com.bosch.caltool.apic.ws.client.APICStub.A2LFileDataResponseType;


/**
 * @author imi2si
 */
public class A2LCallbackHandler extends APICCallbackHandler {

  private A2LFileDataResponseType a2lFileResponse;
  private final String sessionID;
  private Exception a2lException;

  public A2LCallbackHandler(final String sessionID) {
    super();
    this.sessionID = sessionID;
  }

  @Override
  public void receiveResultloadA2LFileData(
      final com.bosch.caltool.apic.ws.client.APICStub.LoadA2LFileDataResponse result) {
    this.a2lFileResponse = result.getLoadA2LFileDataResponse();
  }

  @Override
  public void receiveErrorgetParameterStatisticsExt(final java.lang.Exception e) {
    this.a2lException = e;
  }

  public boolean isA2LFileInfoAvailable() {
    if (this.a2lFileResponse != null) {
      return true;
    }
    else {
      return false;
    }
  }

  public boolean isBroken() {
    if (this.a2lException != null) {
      return true;
    }
    else {
      return false;
    }
  }

  public Exception getA2LException() {

    return this.a2lException;
  }


  public long getA2LFileID() {

    return this.a2lFileResponse.getA2LFileId();
  }

  public String getSessionID() {
    return this.sessionID;
  }
}
