/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class AttributeSuperGroupServiceClientTest extends AbstractRestClientTest {

  private static final Long ATTR_SUPER_GRP_ID = 46L;
  private static final Long ATTR_GRP_ID = 772437616L;

  private static final Long INVALID_ID = -100L;

  /**
   * Test attribute group model retrieval {@link AttributeSuperGroupServiceClient#getAttrGroupModel()}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAttrGroupModel() throws ApicWebServiceException {
    AttributeSuperGroupServiceClient servClient = new AttributeSuperGroupServiceClient();

    AttrGroupModel attrGroupModel = servClient.getAttrGroupModel();
    assertNotNull("Response should not be null", attrGroupModel);

    Map<Long, AttrSuperGroup> allSuperGroupMap = attrGroupModel.getAllSuperGroupMap();
    assertFalse("Super groups should not be null or empty", (allSuperGroupMap == null) || (allSuperGroupMap.isEmpty()));

    AttrSuperGroup attrSuperGroup = allSuperGroupMap.get(ATTR_SUPER_GRP_ID);
    assertNotNull("Response should not be null", attrSuperGroup);
    testAttrSuperGroup(attrSuperGroup);

    Map<Long, AttrGroup> allGroupMap = attrGroupModel.getAllGroupMap();
    assertFalse("Groups should not be null or empty", (allGroupMap == null) || (allGroupMap.isEmpty()));

    AttrGroup attrGroup = allGroupMap.get(ATTR_GRP_ID);
    assertNotNull("Response should not be null", attrGroup);
    testAttrGroup(attrGroup);
  }

  /**
   * @param attrGroup
   */
  private void testAttrGroup(final AttrGroup attrGroup) {
    assertEquals("Group_Name_Eng is equal", "ECU - General", attrGroup.getNameEng());
    assertEquals("group_Name_Ger is equal", "SG - generell", attrGroup.getNameGer());
    assertEquals("Group_Desc_Eng is equal", "general information about ECU", attrGroup.getDescriptionEng());
    assertEquals("Group_Desc_Ger is equal", "generelle Informationen zum Steuergerät", attrGroup.getDescriptionGer());
    assertEquals("Super_group_Id is equal", Long.valueOf(105), attrGroup.getSuperGrpId());
  }

  /**
   * @param attrSuperGroup
   */
  private void testAttrSuperGroup(final AttrSuperGroup attrSuperGroup) {
    assertEquals("Group_Name_Eng is equal", "Engine (ICE)", attrSuperGroup.getNameEng());
    assertEquals("group_Name_Ger is equal", "Motor (Verbrenner)", attrSuperGroup.getNameGer());
    assertEquals("Group_Desc_Eng is equal", "engine related attributes for ICE", attrSuperGroup.getDescriptionEng());
    assertEquals("Group_Desc_Ger is equal", "motorspezifische Attribute für Verbrennungsmotor",
        attrSuperGroup.getDescriptionGer());
    assertEquals("Created User is equal", "hef2fe", attrSuperGroup.getCreatedUser());
  }

  /**
   * Test retrieval of AttrSuperGroup with given id {@link AttributeSuperGroupServiceClient#get(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGet() throws ApicWebServiceException {
    AttributeSuperGroupServiceClient servClient = new AttributeSuperGroupServiceClient();
    AttrSuperGroup attrSuperGroup = servClient.get(ATTR_SUPER_GRP_ID);
    assertNotNull("Response should not be null", attrSuperGroup);
    testAttrSuperGroup(attrSuperGroup);
  }

  /**
   * Negative Test method for {@link AttributeSuperGroupServiceClient#get(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetNegative() throws ApicWebServiceException {
    AttributeSuperGroupServiceClient servClient = new AttributeSuperGroupServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Attribute Super Group with ID '" + INVALID_ID + "' not found");
    servClient.get(INVALID_ID);
    fail("Expected exception not thrown");
  }


  /**
   * Test Method for {@link AttributeSuperGroupServiceClient#create(AttrSuperGroup)},
   * {@link AttributeSuperGroupServiceClient#update(AttrSuperGroup)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateUpdate() throws ApicWebServiceException {
    AttributeSuperGroupServiceClient servClient = new AttributeSuperGroupServiceClient();

    AttrSuperGroup attrSuperGroup = new AttrSuperGroup();
    attrSuperGroup.setNameEng("JUnit_" + getRunId() + " testCreateUpdate");
    attrSuperGroup.setNameGer("JUnit 1");
    attrSuperGroup.setDescriptionEng("JUnit Test Description");
    attrSuperGroup.setDescriptionGer("JUnit Test Description");

    // invoke create
    AttrSuperGroup createdObj = servClient.create(attrSuperGroup);
    assertNotNull("Created Object is not null", createdObj);

    // validate create
    assertEquals("Name_Eng is equal", "JUnit_" + getRunId() + " testCreateUpdate", createdObj.getNameEng());

    createdObj.setNameEng("JUnit_" + getRunId() + " Updated");
    // invoke update
    AttrSuperGroup updatedObj = servClient.update(createdObj);
    assertNotNull("Response should not be null");
    // validate update
    assertEquals("Updated Name_Eng is equal", "JUnit_" + getRunId() + " Updated", updatedObj.getNameEng());
  }
}
