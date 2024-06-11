/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;

import org.junit.Test;

import com.bosch.caltool.apic.ws.client.APICStub.AllPidcDiffResponse;
import com.bosch.caltool.apic.ws.client.APICStub.AttrDiffType;
import com.bosch.caltool.apic.ws.client.APICStub.Attribute;
import com.bosch.caltool.apic.ws.client.APICStub.GetPidcDiffForVersionResponse;
import com.bosch.caltool.apic.ws.client.APICStub.GetPidcDiffsResponse;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardChangedAttributeType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardInfoType;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;
import com.bosch.caltool.icdm.ws.rest.client.InitializationProperties;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author ELM1COB
 */
public class TestPidcDifferences extends AbstractSoapClientTest {


  /**
   *
   */
  private static final String HISTORY_ENTRY_DATE = "history entry date = {}";
  /**
   *
   */
  private static final String HISTORY_ENTRY_RETRIEVED = "History entry retrieved";
  /**
   *
   */
  private static final String DATE_FORMAT = "dd.MM.yy HH:mm";
  private final APICWebServiceClient stub = new APICWebServiceClient();

  /**
   * Test getAllPidcDiffs service
   *
   * @throws Exception service error
   */
  @Test
  public void testGetAllPidcDiffs() throws Exception {
    ProjectIdCardInfoType[] allPidc = this.stub.getAllPidc();

    // Service is obsolete
    this.thrown.expectMessage("service is no longer available. Contact");
    AllPidcDiffResponse apr = this.stub.getAllPidcDiffs(Long.valueOf(allPidc[0].getId()), 0L, -1L);
    fail("exception was not thrown for the test. Service retrurned instead " + apr);
  }

  /**
   * Test getPidcAttrDiffReportForVersion service
   *
   * @throws Exception service error
   */
  @Test
  public void attrdiffresver() throws Exception {

    AttrDiffType[] pidcAttrDiffReportForVersion =
        this.stub.getPidcAttrDiffReportForVersion(2335L, 0L, -1L, "English", 773510615L, this.stub.login());

    assertNotNull(pidcAttrDiffReportForVersion);


    LOG.info("attribute change item: {}", pidcAttrDiffReportForVersion.length);
    LOG.info("\nattribute chnage item: {}", pidcAttrDiffReportForVersion[0].getChangedItem());

    LOG.info("\nattribute New value: {}", pidcAttrDiffReportForVersion[0].getNewAttributeValue());

    LOG.info("\nattribute old value: {}", pidcAttrDiffReportForVersion[0].getNewAttributeValue());

  }

  /**
   * Test getPidcDiffForVersion service
   *
   * @throws Exception service error
   */
  @Test
  public void testpidcdiffver() throws Exception {
    // Service is obsolete
    this.thrown.expectMessage("service is no longer available");
    GetPidcDiffForVersionResponse pidcDiffForVersion = this.stub.getPidcDiffForVersion(2335L, 0L, -1L, 773510615L);
    fail("exception was not thrown. Service retrurned instead " + pidcDiffForVersion);
  }

  /**
   * Shows an example implementation of the PIDC-Differences Report for the iCDM client. <br>
   * <b>Usage notes</b>:
   * <ul>
   * <li>Define PIDC-ID, old and new change number</li>
   * <li>Create a web service client object</li>
   * <li>Call the web service operation {@link APICWebServiceClient#getPidcAttrDiffReport(Long, Long, Long)
   * getPidcAttrDiffReport(pidcId, oldChangeNumber, newChangeNumber)}</li>
   * <li>The result will be returned as array</li>
   * </ul>
   * <br>
   * The result will be an array of class AttrDiffType with these attributes:
   * <ul>
   * <li>PIDC-Version: {@link AttrDiffType#getPidcVersion() AttrDiffType.getPidcVersion()} This is the PIDC Version
   * which can be linked with the PIDC Version of the PIDC-Attributes editor.</li>
   * <li>PIDC-ID: {@link AttrDiffType#getPidcId() AttrDiffType.getPidcId()} The ID of the PIDC.</li>
   * <li>PIDC-Variant-ID: {@link AttrDiffType#getVariantId() AttrDiffType.getVariantId()}. The variant ID. If the ID
   * equals Long.MIN_VALUE, the ID should be considered as empty. In this case it is a change entry of the PIDC level
   * only.</li>
   * <li>PIDC-Sub-Variant-ID: {@link AttrDiffType#getSubVariantId() AttrDiffType.getSubVariantId()} The sub-variant ID.
   * If the ID equals Long.MIN_VALUE, the ID should be considered as empty. In this case it is a change entry of the
   * PIDC variant or PIDC level only.</li>
   * <li>Level: {@link AttrDiffType#getLevel() AttrDiffType.getLevel()} A text description of the change level. The web
   * service returns a string in the form <PIDC or Var or SVar>: <Name of variant or sub-variant. Empty for PIDC
   * level></li>
   * <li>Attribute Name: {@link Attribute#getNameE() AttrDiffType.getAttribute().getNameE()} Returns the name of the
   * attribute. The complete attribute object, not just the name is being returned.</li>
   * <li>Changed Item: {@link AttrDiffType#getChangedItem() AttrDiffType.getChangedItem()} The web service returns a
   * string in the form <PIDC or Var or SVar>: <Name of variant or sub-variant. Empty if PIDC level></li>
   * <li>Old value: {@link AttrDiffType#getOldValue() AttrDiffType.getOldValue()} The old value as text
   * representation</li>
   * <li>New value: {@link AttrDiffType#getNewValue() AttrDiffType.getNewValue()} The new value as text
   * representation</li>
   * <li>Modified By: {@link AttrDiffType#getModifiedName() AttrDiffType.getModifiedName()} The name of the user that
   * made the change</li>
   * <li>Modified On: {@link AttrDiffType#getModifiedOn() AttrDiffType.getModifiedOn()} A date object. The date on which
   * this modification happended</li>
   * <li>Version: {@link AttrDiffType#getVersionId() AttrDiffType.getVersionId()} The Change number. This is an
   * ascending number that shows the order of the changes.</li>
   * </ul>
   *
   * @author imi2si
   * @throws Exception service error
   */
  @Test
  public void testGetPidcAttrDiffReport() throws Exception {
    // Service is obsolete
    this.thrown.expectMessage("service is no longer available");
    AttrDiffType[] differences = this.stub.getPidcAttrDiffReport(758553267L, 0L, -1L, "English");
    fail("exception was not thrown. Service retrurned instead " + differences.length);
  }


  public void responseShouldRightTimezoneWinterTimeIST() throws Exception {
    SimpleDateFormat x = new SimpleDateFormat(DATE_FORMAT);
    // 758553267L, 1769L, 49L
    ProjectIdCardChangedAttributeType historyEntry = getHistoryEntry(767627017L, 302L, 8L, "Asia/Calcutta");
    assertNotNull(HISTORY_ENTRY_RETRIEVED, historyEntry);
    if (null != historyEntry) {
      String date = x.format(historyEntry.getModifyDate().getTime());
      LOG.info(HISTORY_ENTRY_DATE, date);
      // time in IST
      assertTrue("Excepted date: 28.11.14 18:20, got " + date, date.equalsIgnoreCase("28.11.14 18:20"));
    }
  }

  public void responseShouldRightTimezoneSummerTimeIST() throws Exception {
    SimpleDateFormat x = new SimpleDateFormat(DATE_FORMAT);
    ProjectIdCardChangedAttributeType historyEntry = getHistoryEntry(758569767L, 521L, 892L, "Asia/Calcutta");
    assertNotNull(HISTORY_ENTRY_RETRIEVED, historyEntry);
    if (null != historyEntry) {
      String date = x.format(historyEntry.getModifyDate().getTime());
      LOG.info(HISTORY_ENTRY_DATE, date);
      // time in IST
      assertTrue("Excepted date: 13.08.14 15:52, got " + date, date.equalsIgnoreCase("13.08.14 15:52"));
    }
  }

  public void responseShouldRightTimezoneWinterTimeGerman() throws Exception {
    SimpleDateFormat x = new SimpleDateFormat(DATE_FORMAT);
    // 758553267L, 1769L, 49L
    ProjectIdCardChangedAttributeType historyEntry = getHistoryEntry(767627017L, 302L, 8L, "Europe/Berlin");
    assertNotNull(HISTORY_ENTRY_RETRIEVED, historyEntry);
    if (null != historyEntry) {
      String date = x.format(historyEntry.getModifyDate().getTime());
      LOG.info(HISTORY_ENTRY_DATE, date);
      // time in German
      assertTrue("Excepted date: 28.11.14 13:50, got " + date, date.equalsIgnoreCase("28.11.14 13:50"));
    }

  }

  public void responseShouldRightTimezoneSummerTimeGerman() throws Exception {
    SimpleDateFormat x = new SimpleDateFormat(DATE_FORMAT);
    ProjectIdCardChangedAttributeType historyEntry = getHistoryEntry(758569767L, 521L, 892L, "Europe/Berlin");
    assertNotNull(HISTORY_ENTRY_RETRIEVED, historyEntry);
    if (null != historyEntry) {
      String date = x.format(historyEntry.getModifyDate().getTime());
      LOG.info(HISTORY_ENTRY_DATE, date);
      // time in German
      assertTrue("Excepted date: 13.08.14 12:22, got " + date, date.equalsIgnoreCase("13.08.14 12:22"));
    }

  }

  public void responseShouldRightTimezoneWinterTimeUTC() throws Exception {
    SimpleDateFormat x = new SimpleDateFormat(DATE_FORMAT);
    // 758553267L, 1769L, 49L
    ProjectIdCardChangedAttributeType historyEntry = getHistoryEntry(767627017L, 302L, 8L, "UTC");
    assertNotNull(HISTORY_ENTRY_RETRIEVED, historyEntry);

    if (null != historyEntry) {
      String date = x.format(historyEntry.getModifyDate().getTime());
      LOG.info(HISTORY_ENTRY_DATE, date);
      // time in UTC
      assertTrue("Excepted date: 28.11.14 12:50, got " + date, date.equalsIgnoreCase("28.11.14 12:50"));
    }
  }

  public void responseShouldRightTimezoneSummerTimeUTC() throws Exception {
    SimpleDateFormat x = new SimpleDateFormat(DATE_FORMAT);
    ProjectIdCardChangedAttributeType historyEntry = getHistoryEntry(758569767L, 521L, 892L, "UTC");
    assertNotNull(HISTORY_ENTRY_RETRIEVED, historyEntry);
    if (null != historyEntry) {
      String date = x.format(historyEntry.getModifyDate().getTime());
      LOG.info(HISTORY_ENTRY_DATE, date);

      // time in UTC
      assertTrue("Excepted date: 13.08.14 10:22, got " + date, date.equalsIgnoreCase("13.08.14 10:22"));
    }

  }

  private ProjectIdCardChangedAttributeType getHistoryEntry(final Long pidcId, final Long attrId,
      final Long pidcVersion, final Long oldChangeNumber, final Long newChangeNumber, final String timeZone)
      throws Exception {

    GetPidcDiffsResponse response = getDiffs(pidcId, oldChangeNumber, newChangeNumber, timeZone);

    for (ProjectIdCardChangedAttributeType entry : response.getGetPidcDiffsResponse().getChangedAttributes()) {
      if ((entry.getAttrID() == attrId.longValue()) && (entry.getChangeNumber() == pidcVersion.longValue())) {
        return entry;
      }
    }

    return null;
  }

  private ProjectIdCardChangedAttributeType getHistoryEntry(final Long pidcId, final Long attrId,
      final Long pidcVersion, final String timeZone)
      throws Exception {
    return getHistoryEntry(pidcId, attrId, pidcVersion, 0L, -1L, timeZone);
  }

  private GetPidcDiffsResponse getDiffs(final Long pidcId, final Long oldChangeNumber, final Long newChangeNumber,
      final String timeZone)
      throws Exception {
    LOG.debug("Getting pidc diffs for PIDC {}, old change num = {}, new change num = {}", pidcId, oldChangeNumber,
        newChangeNumber);
    return createClientForTimeZone(timeZone).getPidcDiffs(pidcId, oldChangeNumber, newChangeNumber);

  }

  private APICWebServiceClient createClientForTimeZone(final String timeZoneId) throws ApicWebServiceException {
    ClientConfiguration config = new ClientConfiguration(ClientConfiguration.getDefault());
    InitializationProperties props = new InitializationProperties();
    props.setTimezone(timeZoneId);
    config.initialize(props);
    return new APICWebServiceClient(config);
  }

}
