/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author AND4COB
 */
public class AttrGroupServiceClientTest extends AbstractRestClientTest {


  private final static Long ATTRGROUP_ID = 312219L;
  private final static Long INVALID_ATTRGROUP_ID = -100L;

  /**
   * Test method for {@link AttrGroupServiceClient#getById(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    AttrGroupServiceClient attrGrServClient = new AttrGroupServiceClient();
    AttrGroup ret = attrGrServClient.getById(ATTRGROUP_ID);
    assertFalse("Response should not be null", (ret == null));
    testOutput(ret);
  }

  /**
   * @param ret
   */
  private void testOutput(final AttrGroup ret) {
    assertEquals("Group_Name_Eng is equal", "Engine Spec - Fluids", ret.getNameEng());
    assertEquals("Group_Name_Ger is equal", "Motor Spez - Flüssigkeiten", ret.getNameGer());
    assertEquals("Group_Desc_Eng is equal", "Engine Specifications - Fluids", ret.getDescriptionEng());
    assertEquals("Group_Desc_Ger is equal", "Motor Spezifikationen - Flüssigkeiten", ret.getDescriptionGer());
    assertEquals("Super_Group_Id is equal", Long.valueOf(46), ret.getSuperGrpId());
  }


  /**
   * Testing with some invalid attrgroup_id (negative test case)
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    AttrGroupServiceClient attrGrServClient = new AttrGroupServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Attribute Group with ID '" + INVALID_ATTRGROUP_ID + "' not found");
    attrGrServClient.getById(INVALID_ATTRGROUP_ID);
    fail("Expected exception not thrown");
  }


  /**
   * Test method for {@link AttrGroupServiceClient#getAll()}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAll() throws ApicWebServiceException {
    AttrGroupServiceClient attrGrServClient = new AttrGroupServiceClient();
    Map<Long, AttrGroup> retMap = attrGrServClient.getAll();
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    AttrGroup ret = retMap.get(ATTRGROUP_ID);
    assertNotNull("Response should not be null", ret);
    testOutput(ret);
  }

  /**
   * Test method for {@link AttrGroupServiceClient#create(AttrGroup)}, {@link AttrGroupServiceClient#update(AttrGroup)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateUpdateDeleteAttr() throws ApicWebServiceException {
    AttrGroupServiceClient servClient = new AttrGroupServiceClient();

    AttrGroup attrGroup = new AttrGroup();
    attrGroup.setNameEng("Junit_" + getRunId() + " testCreateUpdateDeleteAttr");
    attrGroup.setSuperGrpId(1051016L);
    attrGroup.setNameGer("Junit 1");
    attrGroup.setDescriptionEng("Junit 1");
    attrGroup.setDescriptionGer("Junit 1");
    // attrGroup.setDeleted(false); by default it is false

    // invoke create method
    AttrGroup createdObj = servClient.create(attrGroup);
    LOG.info("Attribute Group NameEng after creation : {}", createdObj.getNameEng());
    LOG.info("Deleted flag after creation : {}", createdObj.getDeletedFlag());

    // validate create
    assertNotNull("Created Object is not null", createdObj);
    assertEquals("Attribute Group NameEng is equal", "Junit_" + getRunId() + " testCreateUpdateDeleteAttr",
        createdObj.getNameEng());
    assertFalse("Deleted flag is false", createdObj.isDeleted());

    // invoke update method
    createdObj.setNameEng("Junit_" + getRunId() + " Updated");
    createdObj.setDeleted(true);
    AttrGroup updatedObj = servClient.update(createdObj);
    LOG.info("Attribute Group NameEng after updation : {}", createdObj.getNameEng());

    // validate update
    assertNotNull("Updated object is not null", updatedObj);
    assertEquals("Attribute Group NameEng is equal", "Junit_" + getRunId() + " Updated", updatedObj.getNameEng());
    assertTrue("Deleted flag is true", updatedObj.isDeleted());
  }
}
