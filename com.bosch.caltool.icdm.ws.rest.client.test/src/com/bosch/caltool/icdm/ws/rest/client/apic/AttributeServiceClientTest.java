/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Test;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrExportModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class AttributeServiceClientTest extends AbstractRestClientTest {

  /**
   * Toyota : 6861L
   */
  private static final Long VALUE_ID = 6861L;
  /**
   * Super Group - Powertrain : 81L
   */
  private static final Long SUPER_GROUP_ID = 81L;
  /**
   * Attribute Customer/Brand : 36L
   */
  private static final Long ATTR_ID = 36L;

  private static final Long UN_CLEARED_ATTR_Id = 973L;

  private static final Long GROUP_ID = 32L;

  private final Long InvalidId = -124321L;

  private static final String MCR_ID1 = "1012325";// "DS-1008462";
  private static final String MCR_ID_2 = "DS-1008462";
  private static final String INVALID_MCR_ID = "invalid";
  private static final String MCR_ID_WITH_NO_ATTR = "DS-1008442";

  private static final long ATTR_ID_FOR_MCR = 249286;// 1046;

  /**
   * Test retrieval of all attributes
   *
   * @throws ApicWebServiceException web server error
   */
  @Test
  public void testGetAll() throws ApicWebServiceException {
    AttributeServiceClient servClient = new AttributeServiceClient();
    Map<Long, Attribute> retMap = servClient.getAll();
    assertFalse("Response should not be null or empty", (retMap == null) || (retMap.isEmpty()));
    Attribute ret = retMap.get(ATTR_ID);
    assertNotNull("Response should not be null", ret);
    testOutput(ret);
  }

  /**
   * @param ret
   */
  /**
   * Test retrieval of attribute with given id
   *
   * @throws ApicWebServiceException web server error
   */
  @Test
  public void testGet() throws ApicWebServiceException {
    AttributeServiceClient servClient = new AttributeServiceClient();
    Attribute ret = servClient.get(ATTR_ID);
    assertFalse("Response should not be null ", (ret == null));
    testOutput(ret);
  }

  /**
   * @throws ApicWebServiceException web servicce error
   */
  @Test
  public void testGetNegative() throws ApicWebServiceException {
    AttributeServiceClient servClient = new AttributeServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Attribute with ID '" + this.InvalidId + "' not found");
    servClient.get(this.InvalidId);
    fail("expected exception not thrown");

  }

  /**
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetUnClearedAttrIds() throws ApicWebServiceException {
    AttributeServiceClient servClient = new AttributeServiceClient();
    List<Long> ret = servClient.getUnClearedAttrIds();
    assertFalse("Response should not be null or empty", (ret == null) || (ret.isEmpty()));
    assertTrue("Value is available ", ret.contains(UN_CLEARED_ATTR_Id));

  }

  /**
   * @throws ApicWebServiceException web server error
   */
  @Test
  public void getMappedAttrs() throws ApicWebServiceException {
    AttributeServiceClient servClient = new AttributeServiceClient();
    SortedSet<Attribute> retSet = servClient.getMappedAttrs();
    assertFalse("Response should not be null or empty", (retSet == null) || (retSet.isEmpty()));
    boolean attrAvailable = false;
    for (Attribute attribute : retSet) {
      if (attribute.getId().equals(ATTR_ID)) {
        attrAvailable = true;
        testOutput(attribute);
        break;
      }
    }
    assertTrue("Attribute is available", attrAvailable);
  }

  /**
   * @throws ApicWebServiceException web server error
   */
  @Test
  public void testGetAttrExportModel() throws ApicWebServiceException {
    AttributeServiceClient servClient = new AttributeServiceClient();
    AttrExportModel ret = servClient.getAttrExportModel();
    assertFalse("Response should not be null ", (ret == null));
    Map<Long, Map<Long, AttributeValue>> retMap = ret.getAllAttrValuesMap();
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    Map<Long, AttributeValue> attrValMap = retMap.get(ATTR_ID);
    assertFalse("Response should not be null or empty", (attrValMap == null) || attrValMap.isEmpty());
    AttributeValue value = attrValMap.get(VALUE_ID);
    testExpOutput(value);

    AttrGroupModel attrGroup = ret.getAttrGroup();
    assertFalse("Response should not be null ", (attrGroup == null));
    Map<Long, AttrSuperGroup> retattrMap = attrGroup.getAllSuperGroupMap();
    assertFalse("Response should not be null or empty", (retattrMap == null) || retattrMap.isEmpty());
    AttrSuperGroup attribute = retattrMap.get(SUPER_GROUP_ID);
    testExpOutput(attribute);

    Map<Long, AttrGroup> map = attrGroup.getAllGroupMap();
    assertFalse("Response should not be null or empty", (map == null) || map.isEmpty());
    AttrGroup group = map.get(GROUP_ID);
    testExpOutput(group);

    Map<Long, Set<Long>> attrMap = attrGroup.getGroupBySuperGroupMap();
    assertFalse("Response should not be null or empty", ((attrMap == null) || attrMap.isEmpty()));
    Set<Long> attrIDSet = attrMap.get(SUPER_GROUP_ID);
    assertTrue(attrIDSet.contains(82L));

  }

  /**
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testGetQuotRelAttrBySingleMcrId() throws ApicWebServiceException {

    AttributeServiceClient servClient = new AttributeServiceClient();
    Set<String> mcrIds = new HashSet<>();
    mcrIds.add(MCR_ID1);
    Map<Long, Attribute> attrMap = servClient.getQuotRelAttrByMcrId(mcrIds);
    assertFalse("Response should not be null or empty", ((attrMap == null) || attrMap.isEmpty()));
    if (null != attrMap) {
      Attribute attr = attrMap.get(ATTR_ID_FOR_MCR);
      testOutputwithMCR(attr);
    }
  }

  /**
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testGetQuotRelAttrByMcrIdList() throws ApicWebServiceException {

    AttributeServiceClient servClient = new AttributeServiceClient();

    Map<Long, Attribute> attrMap =
        servClient.getQuotRelAttrByMcrId(new HashSet<>(Arrays.asList(MCR_ID1, MCR_ID_2, INVALID_MCR_ID)));
    assertFalse("Response should not be null or empty", ((attrMap == null) || attrMap.isEmpty()));
    if (null != attrMap) {
      Attribute attr = attrMap.get(ATTR_ID_FOR_MCR);
      testOutputwithMCR(attr);
    }
  }

  /**
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testGetQuotRelAttrByInvalidMcrId() throws ApicWebServiceException {

    AttributeServiceClient servClient = new AttributeServiceClient();
    Set<String> mcrIds = new HashSet<>();
    mcrIds.add(INVALID_MCR_ID);
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Given mcrIds are invalid");
    servClient.getQuotRelAttrByMcrId(mcrIds);
  }

  /**
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testGetQuotRelAttrByNullasMcrId() throws ApicWebServiceException {

    AttributeServiceClient servClient = new AttributeServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Given mcrIds are invalid");
    servClient.getQuotRelAttrByMcrId(null);
  }

  /**
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testGetQuotRelAttrByEmptyStringasMcrId() throws ApicWebServiceException {

    AttributeServiceClient servClient = new AttributeServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Given mcrIds are invalid");
    Set<String> mcrIds = new HashSet<>();
    mcrIds.add("");
    servClient.getQuotRelAttrByMcrId(mcrIds);
  }

  /**
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testGetQuotRelAttrByMcrIdWithNoAttr() throws ApicWebServiceException {

    AttributeServiceClient servClient = new AttributeServiceClient();
    Set<String> mcrIds = new HashSet<>();
    mcrIds.add(MCR_ID_WITH_NO_ATTR);
    Map<Long, Attribute> attrMap = servClient.getQuotRelAttrByMcrId(mcrIds);
    assertTrue("Response should not be null or empty", ((attrMap == null) || attrMap.isEmpty()));
  }

  /**
   * Validating each instance of the model
   */

  private void testExpOutput(final IModel model) {
    if (model instanceof AttributeValue) {
      AttributeValue value = (AttributeValue) model;
      assertEquals("value id is equal", Long.valueOf(VALUE_ID), value.getId());
      assertEquals("Attribute id is equal", Long.valueOf(36L), value.getAttributeId());
      assertEquals("description eng is equal", "Toyota", value.getDescriptionEng());
      assertEquals("description ger is equal", "Toyota", value.getDescriptionGer());
      assertEquals("CreatedUser is equal", "tbd2si", value.getCreatedUser());
    }
    if (model instanceof AttrSuperGroup) {
      AttrSuperGroup attribute = (AttrSuperGroup) model;
      assertEquals("description eng is equal", "Powertrain", attribute.getDescriptionEng());
      assertEquals("description gre is equal", "Antriebsstrang", attribute.getDescriptionGer());
      assertEquals("Name eng is equal", "Powertrain", attribute.getNameEng());
      assertEquals("Name ger is equal", "Antriebsstrang", attribute.getNameGer());
      assertEquals("CreatedUser is equal", "hef2fe", attribute.getCreatedUser());
      assertEquals("Super group id is equal", Long.valueOf(SUPER_GROUP_ID), attribute.getId());
    }
    if (model instanceof AttrGroup) {
      AttrGroup group = (AttrGroup) model;
      assertEquals("Group id is equal", Long.valueOf(GROUP_ID), group.getId());
      assertEquals("description eng is equal", "project specific information", group.getDescriptionEng());
      assertEquals("description gre is equal", "projekspezifische Informationen", group.getDescriptionGer());
      assertEquals("Name eng is equal", "Project Information", group.getNameEng());
      assertEquals("Name ger is equal", "Projektinformation", group.getNameGer());
    }
  }


  /**
   * @param ret
   */
  private void testOutput(final Attribute ret) {

    assertEquals("attrid is equal", Long.valueOf(36), ret.getId());
    assertEquals("groupid is equal", Long.valueOf(32), ret.getAttrGrpId());
    assertEquals("charid is equal", Long.valueOf(789778815), ret.getCharacteristicId());
    assertEquals("CreatedUser is equal", "hef2fe", ret.getCreatedUser());
    assertNotNull("CreatedDate is not null", ret.getCreatedDate());
    assertEquals("value id is equal", Long.valueOf(1), ret.getValueTypeId());
  }

  /**
   * @param ret
   */
  private void testOutputwithMCR(final Attribute ret) {

    assertEquals("attrid is equal", Long.valueOf(ATTR_ID_FOR_MCR), ret.getId());
    assertNotNull("CreatedUser is not null", ret.getCreatedUser());
    assertNotNull("CreatedDate is not null", ret.getCreatedDate());
  }

  /**
   * Test attribute creation doubt
   *
   * @throws ApicWebServiceException web server error
   */
  @Test
  public void testCreateUpdate() throws ApicWebServiceException {
    Attribute input = new Attribute();
    input.setNameEng("Z-Junit 01" + getRunId());
    input.setDescriptionEng("ZJunit01");
    input.setEadmName("ZJunit01" + getRunId());
    input.setAttrGrpId(32L);
    input.setExternal(false);
    input.setMandatory(false);
    input.setValueTypeId(1L);
    AttributeServiceClient servClient = new AttributeServiceClient();

    // invoke create method
    Attribute Createobj = servClient.create(input);
    assertFalse("Response should not be null ", (Createobj == null));

    // validate created object
    assertEquals("Set Name Eng is equal", "Z-Junit 01" + getRunId(), Createobj.getNameEng());
    assertEquals("Set EadmName is equal", "ZJunit01" + getRunId(), Createobj.getEadmName());
    assertNotNull("setAttrGrpId is equal", Createobj.getAttrGrpId());
    assertNotNull("setDescriptionEng is equal", Createobj.getDescriptionEng());

    Createobj.setNameEng("Z-Junit 01" + getRunId() + "update");
    Createobj.setEadmName("ZJunit01" + getRunId() + "update");
    Createobj.setDescriptionEng("ZJunit01 update");
    // invoke update method
    Attribute Updateobj = servClient.update(Createobj);
    assertFalse("Response should not be null ", (Updateobj == null));

    // validate updated records
    assertEquals("Set Name Eng is equal", "Z-Junit 01" + getRunId() + "update", Updateobj.getNameEng());
    assertEquals("Set EadmName is equal", "ZJunit01" + getRunId() + "update", Updateobj.getEadmName());

  }
}
