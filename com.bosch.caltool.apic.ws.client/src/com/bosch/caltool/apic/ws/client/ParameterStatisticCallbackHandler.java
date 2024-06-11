/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.axis2.databinding.types.HexBinary;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.apic.ws.client.APICStub.GetParameterStatisticsExtResponseType;


/**
 * @author imi2si
 */
public class ParameterStatisticCallbackHandler extends APICCallbackHandler {

  GetParameterStatisticsExtResponseType parameter;
  Exception statisticsExtException;
  String sessionID;

  public ParameterStatisticCallbackHandler(final String sessionID) {
    super();
    this.sessionID = sessionID;
  }

  @Override
  public void receiveResultgetParameterStatisticsExt(
      final com.bosch.caltool.apic.ws.client.APICStub.GetParameterStatisticsExtResponse result) {
    this.parameter = result.getGetParameterStatisticsExtResponse();
  }

  @Override
  public void receiveErrorgetParameterStatisticsExt(final java.lang.Exception e) {
    this.statisticsExtException = e;
  }

  public boolean isParameterAvailable() {
    if (this.parameter != null) {
      return true;
    }
    else {
      return false;
    }
  }

  public boolean isBroken() {
    if (this.statisticsExtException != null) {
      return true;
    }
    else {
      return false;
    }
  }

  public Exception getParameterException() {

    return this.statisticsExtException;
  }


  /**
   * @return the sessionID
   */
  public String getSessionID() {
    return this.sessionID;
  }

  public CalData[] getFiles() throws ClassNotFoundException, IOException {
    HexBinary[] results = this.parameter.getParameterStatistics();

    CalData[] calDataObjects = new CalData[results.length];

    for (int i = 0; i < results.length; i++) {

      ByteArrayInputStream bais = new ByteArrayInputStream(results[i].getBytes());

      calDataObjects[i] = (CalData) new ObjectInputStream(bais).readObject();
    }

    return calDataObjects;
  }

  public CalData getFile(final String parameterName) throws ClassNotFoundException, IOException {

    for (CalData calData : getFiles()) {
      if (calData.getShortName().equalsIgnoreCase(parameterName)) {
        return calData;
      }
    }

    return new CalData() {

      {
        setLongName("NullObject");
        setShortName("NullObject");
      }
    };
  }
}


/*
 * public class MyVillaCallBackHandler extends VillaCallbackHandler { String x = new String(); public void
 * receiveResultgetFileInformation( com.bosch.caltool.villa.ws.VillaStub.GetFileInformationResponse result) { x =
 * result.getOut(); } public String getFileInformation() { return x; } public void
 * receiveErrorgetFileInformation(java.lang.Exception e) { } }
 */