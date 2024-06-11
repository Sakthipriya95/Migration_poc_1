/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.pidcrequestor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bosch.caltool.apic.ws.client.APICStub.Attribute;
import com.bosch.caltool.apic.ws.client.APICStub.SuperGroupType;
import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.pidcrequestor.test.PIDCRequestorLogger;

public class PIDCRequestorJUnitTest {

  private final PIDCRequestor req = new PIDCRequestor(PIDCRequestorLogger.INSTANCE.getLogger());
  private final APICWebServiceClient apicWsClient = new APICWebServiceClient();

  // Constructor that sets the file for the test cases
  public PIDCRequestorJUnitTest() {}

  // Runs once before the tests are started (must be static)
  @BeforeClass
  public static void beforeClass() {
    // E.g. Provide database access, load files, reference a webservice object
  }

  // Runs before each method
  @Before
  public void beforeMethodCall() {

    // stuff that happens before each method
  }


  /**
   * Tests if all the attributes can be recieved over the web service interface If the local array of attributes is
   * null, the webservice has not delivered any data
   */
  @Test
  public void testGetAttributesThroughWebservice() {
    assertNotNull(getAttributesThroughWebservice());
  }

  /**
   * Tests if all the attributes can be recieved over the PIDC class. If the local array of attributes is null, the
   * webservice has not delivered any data.
   */
  @Test
  public void testGetAttributesThroughPIDCRequestor() {
    assertNotNull(getAttributesThroughRequestor());
  }

  @Test
  public void testGetAllAttributesWoDeletedEqual() {
    int loopWithoutDeletedApic = 0;

    Attribute attrOfRequestor[] = getAttributesThroughRequestor();
    Attribute attrOfWebservice[] = getAttributesThroughWebservice();

    // Get the number of not deleted attributes and compare them with getAllAttributesWoDeleted
    // Compare the array with the original attribute array of the webservice. The number of the
    // webservice array excluding the deleted marked objects must match the records of getAllAttributesWoDeleted
    if ((attrOfWebservice != null) && (attrOfRequestor != null)) {
      for (Attribute element : attrOfWebservice) {
        if (!element.getIsDeleted()) {
          loopWithoutDeletedApic++;
        }
      }
      assertTrue(loopWithoutDeletedApic == attrOfRequestor.length);
    }
  }

  /**
   * Compares the number of records without deleted ones of the webservice with the length of the array that is returned
   * by method getAllAttrGroupsWoDeleted.
   */
  @Test
  public void testGetAllGroupsWoDeletedEqual() {
    int loopWithoutDeletedApic = 0;

    SuperGroupType groupsOfRequestor[] = getGroupsThroughRequestor();
    SuperGroupType groupsOfWebservice[] = getGroupsThroughWebservice();

    // Count the entries of the array groupsOfWebservice which are not marked as deleted to compare them against
    // the size of the groupsOfRequestor-array which already contains just not deleted groups
    if ((groupsOfRequestor != null) && (groupsOfWebservice != null)) {

      for (SuperGroupType element : groupsOfWebservice) {
        if (!element.getIsDeleted()) {
          loopWithoutDeletedApic++;
        }
      }
      // loopWithoutDeletedApic holds the number of groups of the webservice without deleted. Must match
      // the length of array groupsOfRequestor that contains just not deleted groups
      assertTrue(loopWithoutDeletedApic == groupsOfRequestor.length);
    }
  }

  private Attribute[] getAttributesThroughWebservice() {
    Attribute attrOfWebservice[] = null;

    try {
      attrOfWebservice = this.apicWsClient.getAllAttributes();
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }

    return attrOfWebservice;
  }

  private Attribute[] getAttributesThroughRequestor() {
    Attribute attrOfPIDCRequestor[] = null;
    Method method = null;

    try {
      method = PIDCRequestor.class.getDeclaredMethod("getAllAttributesWoDeleted", null);
      method.setAccessible(true);
      Object o = method.invoke(this.req, null);
      attrOfPIDCRequestor = (Attribute[]) o;
    }
    catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
    return attrOfPIDCRequestor;
  }

  private SuperGroupType[] getGroupsThroughWebservice() {
    SuperGroupType groupsOfWebservice[] = null;

    try {
      groupsOfWebservice = this.apicWsClient.getAttrGroups();
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
    return groupsOfWebservice;
  }

  private SuperGroupType[] getGroupsThroughRequestor() {
    SuperGroupType groupsOfPIDCRequestor[] = null;
    Method method = null;

    try {
      method = PIDCRequestor.class.getDeclaredMethod("getAllAttrGroupsWoDeleted", null);
      method.setAccessible(true);
      Object o = method.invoke(this.req, null);
      groupsOfPIDCRequestor = (SuperGroupType[]) o;
    }
    catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
    return groupsOfPIDCRequestor;
  }
}
