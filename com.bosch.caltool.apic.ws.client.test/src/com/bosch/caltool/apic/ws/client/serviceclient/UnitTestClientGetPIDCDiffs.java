/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.bosch.caltool.apic.ws.client.APICStub.GetPidcDiffsResponse;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardChangedAttributeType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardInfoType;


/**
 * @author imi2si
 */


/*
 * Golden Rules Unit Testing 1. Überlege, welche Klasse und Methode geschrieben werden soll. Lege Quellcode für die
 * Klasse und Variab-len/Methoden/Konstruktoren an, sodass sich die Compilationsheit übersetzen lässt. 2. Schreibe die
 * API-Dokumentation für die Methoden/Konstruktoren und überlege, welche Parameter, Rückgaben, Ausnahmen nötig sind. 3.
 * Teste die API an einem Beispiel, ob sich die Klasse mit Eigenschaften „natürlich“ anfühlt. Falls nötig wechsele zu
 * Punkt 1 und passe die Eigenschaften an. 4. Implementiere eine Testklasse. 5. Implementiere die Logik des eigentlichen
 * Programms. 6. Gibt es durch die Implementierung neue Dinge, die ein Testfall testen sollte? Wenn ja, erweitere den
 * Testfall. 7. Führe die Tests aus und wiederhole Schritt 5 bis alles fehlerfrei läuft. Achtung: private methods
 * sollten eher über eine andere public method getestet werden. Wenn zuviele private methods getestet werden müssen,
 * deutet das auf ein Problem in der Klasse hin, d.h. die Klasse hat zuviele Aufgaben, die aufgesplitet werden müssen
 */

@RunWith(Parameterized.class)
// Means, that the test cases considere the parameters, defined unter @Parameters public static Collection values()
public class UnitTestClientGetPIDCDiffs extends AbstractSoapClientTest {

  private static APICWebServiceClient apicWsClient;

  private final Long pidcID;

  // Constructor that sets the file for the test cases
  /**
   * @param pidc Project Id Card Info
   */
  public UnitTestClientGetPIDCDiffs(final ProjectIdCardInfoType pidc) {
    // Set the file(s) the test should be performed for
    super();
    this.pidcID = pidc.getId();
  }

  /**
   * Set the parameters for the file name. The parameters are passed to the constructor. The first dimension of the
   * array is the testcase, the second the parameters passed to the constructor .In this case two testcases with three
   * parameters each
   *
   * @return collection of pidcs
   * @throws Exception error while retrieving pidcs
   */
  @Parameters
  public static List<ProjectIdCardInfoType[]> values() throws Exception {

    AbstractSoapClientTest.initialize();
    ProjectIdCardInfoType[] pidc = new APICWebServiceClient().getAllPidc();

    int maxPidcs = 1;// change this to max of pidc.length, to expand the test to multiple pidcs
    ProjectIdCardInfoType[][] args = new ProjectIdCardInfoType[maxPidcs][];
    for (int loop = 0; loop < maxPidcs; loop++) {
      args[loop] = new ProjectIdCardInfoType[1];
      args[loop][0] = pidc[loop];
    }

    return Arrays.asList(args);

  }

  // Runs once before the tests are started (must be static)
  /**
   * Initialize WS client
   */
  @BeforeClass
  public static void beforeClass() {
    apicWsClient = new APICWebServiceClient();
  }

  /**
   *
   */
  @Test
  public void testGetWSResultWOError() {
    try {

      GetPidcDiffsResponse response = UnitTestClientGetPIDCDiffs.apicWsClient.getPidcDiffs(this.pidcID, 0L, -1L);
      assertNotNull(response);

    }
    catch (Exception e) {
      fail("testGetWSResultWOError failed : PIDC " + this.pidcID + " " + e.getMessage());
    }

  }

  /**
   * Test Attribute IDs Wo Zero
   */
  @Test
  public void testAttributeIDsWoZero() {
    GetPidcDiffsResponse response = null;
    try {
      response = UnitTestClientGetPIDCDiffs.apicWsClient.getPidcDiffs(this.pidcID, 0L, -1L);
    }
    catch (Exception e) {
      fail("testAttributeIDsWoZero failed : PIDC " + this.pidcID + " " + e.getMessage());
    }
    if (response == null) {
      return;
    }
    ProjectIdCardChangedAttributeType[] attr = response.getGetPidcDiffsResponse().getChangedAttributes();

    if (attr != null) {
      for (ProjectIdCardChangedAttributeType entry : attr) {
        if (entry.getNewValueID() == -1) {
          LOG.debug("New Value ID: " + entry.getNewValueID());
          LOG.debug("Old Value ID: " + entry.getOldValueID());
        }
        assertTrue("PIDC : " + this.pidcID + "; Attribute Id: " + entry.getAttrID(), entry.getNewValueID() != 0);
        assertTrue("PIDC: " + this.pidcID + "; Attribute ID: " + entry.getAttrID(), entry.getOldValueID() != 0);
      }
    }
  }

  /**
   * Test Attributes Change Flag
   */
  @Test
  public void testAttributesChangeFlag() {
    GetPidcDiffsResponse response = null;
    try {
      response = UnitTestClientGetPIDCDiffs.apicWsClient.getPidcDiffs(this.pidcID, 0L, -1L);
    }
    catch (Exception e) {
      fail("testAttributesChangeFlag : PIDC " + this.pidcID + " failed " + e.getMessage());
    }

    if (response == null) {
      return;
    }

    ProjectIdCardChangedAttributeType[] attr = response.getGetPidcDiffsResponse().getChangedAttributes();

    if (attr != null) {
      for (ProjectIdCardChangedAttributeType entry : attr) {
        assertTrue(
            "Pidc: " + this.pidcID + "; Attribute id: " + entry.getAttrID() + "; Value: " + entry.getNewValueID() +
                ";Tracker: " + entry.isNewValueIDSpecified(),
            ((entry.getNewValueID() != java.lang.Long.MIN_VALUE) && entry.isNewValueIDSpecified()) ||
                ((entry.getNewValueID() == java.lang.Long.MIN_VALUE) && !entry.isNewValueIDSpecified()));

        assertTrue(
            "PIDC: " + this.pidcID + "; Attribute ID: " + entry.getAttrID() + "; Value: " + entry.getOldValueID() +
                ";Tracker: " + entry.isOldValueIDSpecified(),
            ((entry.getOldValueID() == java.lang.Long.MIN_VALUE) && !entry.isOldValueIDSpecified()) ||
                ((entry.getOldValueID() != java.lang.Long.MIN_VALUE) && entry.isOldValueIDSpecified()));
      }
    }
  }


}