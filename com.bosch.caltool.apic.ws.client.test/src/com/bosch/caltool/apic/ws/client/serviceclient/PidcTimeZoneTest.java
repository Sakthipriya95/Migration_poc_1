/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertNotSame;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardType;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;
import com.bosch.caltool.icdm.ws.rest.client.InitializationProperties;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author imi2si
 */
public class PidcTimeZoneTest extends AbstractSoapClientTest {


  private final TimeZoneChecker timeZone = new TimeZoneChecker(LOG);

  public void timeZoneShouldBeConsidered() throws Exception {

    final APICWebServiceClient stub = new APICWebServiceClient();
    ProjectIdCardType pidc = getPidc(stub, 758553267L);

    this.timeZone.assertDate(this.timeZone.createDate(2014, 4, 3, 4, 48, 33),
        pidc.getProjectIdCardDetails().getCreateDate().getTime(), stub.getClientTimeZone());

  }


  @Test
  public void manualTimeZoneShouldBeConsidered() throws Exception {

    SimpleDateFormat dateFormater = new SimpleDateFormat();
    Date dbDate = this.timeZone.createDate(2013, 10, 9, 15, 41, 14);
    ProjectIdCardType pidc;

    APICWebServiceClient stub = createClientForTimeZone("UTC");

    pidc = getPidc(stub, 759209676L);
    Date wsResult1 = pidc.getProjectIdCardDetails().getCreateDate().getTime();
    dateFormater.setTimeZone(TimeZone.getTimeZone(stub.getClientTimeZone()));
    LOG.info("Result for timezone UTC:" + dateFormater.format(wsResult1));

    stub = createClientForTimeZone("Europe/Moscow");
    pidc = getPidc(stub, 759209676L);
    Date wsResult2 = pidc.getProjectIdCardDetails().getCreateDate().getTime();
    dateFormater.setTimeZone(TimeZone.getTimeZone(stub.getClientTimeZone()));
    LOG.info("Result for timezone Europe/Moscow:" + dateFormater.format(wsResult2));

    assertNotSame(dateFormater.format(wsResult1), dateFormater.format(wsResult2));

  }

  @Test
  public void calendarShouldIncludeRightTimezone() throws Exception {

    SimpleDateFormat dateFormater = new SimpleDateFormat();
    Date dbDate = this.timeZone.createDate(2013, 10, 9, 15, 41, 14);
    ProjectIdCardType pidc;
    APICWebServiceClient stub;

    stub = createClientForTimeZone("UTC");
    pidc = getPidc(stub, 766243235L);
    int timeZone1 = pidc.getProjectIdCardDetails().getCreateDate().get(Calendar.ZONE_OFFSET);
    LOG.info("Returned  Created Date: {}, TimeZone of webservice: {}, sent to webservice: {}",
        pidc.getProjectIdCardDetails().getCreateDate().getTime(), timeZone1, stub.getClientTimeZone());

    stub = createClientForTimeZone("Etc/GMT+5");
    pidc = getPidc(stub, 766243235L);
    int timeZone2 = pidc.getProjectIdCardDetails().getCreateDate().get(Calendar.ZONE_OFFSET);

    LOG.info("Returned  Created Date: {}, TimeZone of webservice: {}, sent to webservice: {}",
        pidc.getProjectIdCardDetails().getCreateDate().getTime(), timeZone2, stub.getClientTimeZone());

    assertNotSame(timeZone1, timeZone2);

  }

  private APICWebServiceClient createClientForTimeZone(final String timeZoneId) throws ApicWebServiceException {
    ClientConfiguration config = new ClientConfiguration(ClientConfiguration.getDefault());
    InitializationProperties props = new InitializationProperties();
    props.setTimezone(timeZoneId);
    config.initialize(props);
    return new APICWebServiceClient(config);
  }

  private ProjectIdCardType getPidc(final APICWebServiceClient stub, final Long pidcId) throws Exception {
    return stub.getPidcDetails(pidcId);
  }

}
