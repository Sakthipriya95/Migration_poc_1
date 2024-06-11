/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.Matchers;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author HNU1COB
 */

public class AttributeValueServiceClientTest extends AbstractRestClientTest {


  /**
   * test atrr_id
   */
  private static final long ATTR_ID = 1421900271L; // attr id that has predefined validity
  private static final long INVALID_ATTR_ID = -1421900271L;
  /**
   * Attribute - Project ID
   */
  private static final long ATTR_ID_WITHOUT_FEATURE = 40L;
  private static final long ATTR_VAL_ID = 1421900272L; // attr val id that has predefined validity
  private static final long INVALID_ATTR_VAL_ID = -1421900272L;
  private static final long PIDC_VER_ID = 1421900259L;
  private static final long INVAL_PIDC_VER_ID = -1421900259L;
  private static final long ATTR_ID_DEP = 1495497768L; // attr id that has value dependency
  private static final long INVALID_ATTR_ID_DEP = -1495497768L;
  private static final long ATTR_VAL_ID_DEP = 1495524185L; // attr val that has value dependency
  /**
   * Attribute Name - User - 2 Attribute Value - "Phone : +91(80)6783-4661 Email :Abdul.Hareef@de.bosch.com"
   */
  private static final long USER_ATTR_VAL_ID = 1534815328L; // attr val id for iCDM user

  /**
   * test method for {@link AttributeValueServiceClient#getValuesByAttribute(Long)} input for service client: attr id,
   * output form service client: Map< attr id, Map<attr id , AttributeValue>>
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetValuesByAttribute() throws ApicWebServiceException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    Map<Long, Map<Long, AttributeValue>> retMap = serviceClient.getValuesByAttribute(ATTR_ID);
    assertFalse("Response should not be null or empty", retMap.isEmpty());
    Map<Long, AttributeValue> ret = retMap.get(ATTR_ID);
    AttributeValue retatt = ret.get(ATTR_VAL_ID);
    testOutput(retatt);
  }


  /**
   * test method for {@link AttributeValueServiceClient#getValuesByAttribute(Long)}.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetValuesByAttributeNegative() throws ApicWebServiceException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("BAD_INPUT"));
    this.thrown.expectMessage("ID '" + INVALID_ATTR_ID + "' is invalid for Attribute");
    serviceClient.getValuesByAttribute(INVALID_ATTR_ID);
    fail("Expected Exception not thrown");
  }

  /**
   * test method for {@link AttributeValueServiceClient#getValuesByAttributeValue(Long)} input for sevice client:
   * attr_val_id , output from service client: Map<predefined validity id, PredefinedValidity>
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetValuesByAttributeValue() throws ApicWebServiceException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    // valid id
    Map<Long, PredefinedValidity> retMap = serviceClient.getValuesByAttributeValue(ATTR_VAL_ID);
    assertFalse("Response should not be null or empty", retMap.isEmpty());
    PredefinedValidity retPredefinedValidity = retMap.get(1421900274L);
    testOutput(retPredefinedValidity);
  }

  /**
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetValuesByAttributeValueNegative1() throws ApicWebServiceException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    Map<Long, PredefinedValidity> retMap = serviceClient.getValuesByAttributeValue(2205455550L);
    assertNotNull(retMap);

  }


  /**
   * @throws ApicWebServiceException
   */
  @Test
  public void testGetLevelAttrValUsedStatus() throws ApicWebServiceException {

    List<Long> attrValList = new ArrayList<>();
    attrValList.add(772400021L);
    attrValList.add(772400017L);
    attrValList.add(759945615L);

    boolean result = new AttributeValueServiceClient().getLevelAttrValuesUsedStatus(attrValList);

    assertTrue(result);
  }

  /**
   * @throws ApicWebServiceException
   */
  @Test
  public void testGetLevelAttrValUsedStatusEmpty() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Attribute value list must not be empty");
    new AttributeValueServiceClient().getLevelAttrValuesUsedStatus(new ArrayList<>());
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * @throws ApicWebServiceException
   */
  @Test
  public void testGetLevelAttrValUsedStatusNegative() throws ApicWebServiceException {
    List<Long> attrValList = new ArrayList<>();
    attrValList.add(123L);

    boolean result = new AttributeValueServiceClient().getLevelAttrValuesUsedStatus(attrValList);
    assertFalse(result);
  }


  /**
   * @param obj
   */
  private void testOutput(final PredefinedValidity obj) {
    assertEquals("Predefined Validity Id is equal", Long.valueOf(1421900274), obj.getId());
    assertEquals("Group Attribute Id is equal", Long.valueOf(1421900272), obj.getGrpAttrValId());
    assertEquals("Validity Value Id is equal", Long.valueOf(4136), obj.getValidityValueId());
    assertEquals("Created User is equal", "DJA7COB", obj.getCreatedUser());
    assertNotNull("Created Date is not null", obj.getCreatedDate());
    assertEquals("Validity Attribute Id is equal", Long.valueOf(36), obj.getValidityAttrId());

  }

  /**
   * test method for {@link AttributeValueServiceClient#getValuesByAttributeValue(Long)}.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetValuesByAttributeValueNegative() throws ApicWebServiceException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    serviceClient.getValuesByAttributeValue(INVALID_ATTR_VAL_ID);
    fail("Expected Exception is not thrown");
  }

  /**
   * test method for {@link AttributeValueServiceClient#getValueByAttribute(Long,Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetValueByAttribute() throws ApicWebServiceException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    AttributeValue ret = serviceClient.getValueByAttribute(PIDC_VER_ID, ATTR_ID);
    assertNotNull("Response should not be null", ret);
    testOutput(ret);
  }

  /**
   * test method for {@link AttributeValueServiceClient#getValueByAttribute(Long,Long)}.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetValueByAttributeNegative() throws ApicWebServiceException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    AttributeValue ret = serviceClient.getValueByAttribute(INVAL_PIDC_VER_ID, INVALID_ATTR_ID);
    assertNull("Response should be null", ret);
  }

  /**
   * test method for {@link AttributeValueServiceClient#getValueDependecyMap(Long)} input for service client: attr id,
   * output from service client: map <value id , Set<AttrNValueDependency>
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetValueDependecyMap() throws ApicWebServiceException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    Map<Long, Set<AttrNValueDependency>> retMap = serviceClient.getValueDependecyMap(ATTR_ID_DEP);
    assertFalse("Response should not be empty", retMap.isEmpty());
    if (retMap.containsKey(ATTR_VAL_ID_DEP)) {
      Set<AttrNValueDependency> retSet = retMap.get(ATTR_VAL_ID_DEP);
      assertFalse("Response Set should not be empty", retSet.isEmpty());
      for (AttrNValueDependency attdep : retSet) {
        assertEquals("Attr dependency id is equal", Long.valueOf(1495524186), attdep.getId());
        assertNull("Attr id is equal", attdep.getAttributeId());
        assertEquals("Attr value id is null", Long.valueOf(1495524185), attdep.getValueId());
        assertEquals("Dependent attr id is equal", Long.valueOf(769955022), attdep.getDependentAttrId());
        assertEquals("Dependent value id is equal", Long.valueOf(1165617198), attdep.getDependentValueId());
        assertNotNull("Created Date is not null", attdep.getCreatedDate());
        assertEquals("Created User is equal", "BRU2COB", attdep.getCreatedUser());
        assertNull("Change comment is null", attdep.getChangeComment());
      }

    }

  }

  /**
   * test method for {@link AttributeValueServiceClient#getValueDependecyMap(Long)}.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetValueDependecyMapNegative() throws ApicWebServiceException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '" + INVALID_ATTR_ID_DEP + "' is invalid for Attribute");
    serviceClient.getValueDependecyMap(INVALID_ATTR_ID_DEP);
    fail("Expected excpetion not thrown");
  }


  /**
   * test method for {@link AttributeValueServiceClient#getById(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    AttributeValue ret = serviceClient.getById(ATTR_VAL_ID);
    assertNotNull("Response should not be null", ret);
    testOutput(ret);
  }

  /**
   * test method for {@link AttributeValueServiceClient#getById(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByIdUser() throws ApicWebServiceException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    AttributeValue ret = serviceClient.getById(USER_ATTR_VAL_ID);
    assertNotNull("Response should not be null", ret);
    assertEquals("Other value (NT ID) is equal", "ABE2CA", ret.getOtherValue());
  }

  /**
   * @param obj
   */
  private void testOutput(final AttributeValue obj) {

    assertEquals("Value Id is equal", Long.valueOf(1421900272L), obj.getId());
    assertEquals("Attribute Id is equal", Long.valueOf(1421900271L), obj.getAttributeId());
    assertEquals("Value Desc is equal", "desc", obj.getDescription());
    assertNull("Value Desc Ger is null", obj.getDescriptionGer());
    assertNull("Num value is null", obj.getNumValue());
    assertNull("Date Value is null", obj.getDateValue());
    assertEquals("Text Value is equal", "val 1", obj.getTextValueEng());
    assertNull("Text Value Ger is null", obj.getTextValueGer());
    assertNull("Boolean value is null", obj.getBoolvalue());
    assertNull("Other value is null", obj.getOtherValue());
    assertNotNull("Created Date is not null", obj.getCreatedDate());
    assertEquals("Created User is equal", "DJA7COB", obj.getCreatedUser());
    assertNull("Characteristic value id is null", obj.getCharacteristicValueId());
    assertNull("Change comment is null", obj.getChangeComment());

  }

  /**
   * test method for {@link AttributeValueServiceClient#getById(Long)}.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Attribute Value with ID '" + INVALID_ATTR_VAL_ID + "' not found");
    serviceClient.getById(INVALID_ATTR_VAL_ID);
    fail("Expected exception not thrown");
  }

  /**
   * test method for {@link AttributeValueServiceClient#create(AttributeValue)},
   * {@link AttributeValueServiceClient#update(AttributeValue)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateUpdate() throws ApicWebServiceException {
    AttributeValue obj = new AttributeValue();
    obj.setAttributeId(126l);
    obj.setDescriptionEng("Junit_Attr" + getRunId());
    obj.setTextValueEng("Junit_Attr" + getRunId());
    obj.setBoolvalue(null);
    obj.setOtherValue(null);
    obj.setNumValue(null);
    obj.setDateValue(null);
    obj.setDeleted(false);
    obj.setClearingStatus("R");
    obj.setChangeComment(null);
    obj.setCharacteristicValueId(null);
    // create
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    AttributeValue createdObject = serviceClient.create(obj);
    // validate
    assertNotNull("Created Object is null", createdObject);
    assertEquals("Attribute Id is equal", Long.valueOf(126), createdObject.getAttributeId());
    assertEquals("'Attribute value description is equal", "Junit_Attr" + getRunId(), createdObject.getDescriptionEng());
    assertEquals("Textvalue English is equal", "Junit_Attr" + getRunId(), createdObject.getTextValueEng());
    assertEquals("Clearing status is equal", "R", createdObject.getClearingStatus());
    // update
    createdObject.setDescriptionEng("Junit_Updated_" + getRunId());
    AttributeValue updatedObject = serviceClient.update(createdObject);
    // validate
    assertNotNull("Created Object is null", updatedObject);
    assertEquals("'Attribute value description is equal", "Junit_Updated_" + getRunId(),
        updatedObject.getDescriptionEng());
  }


  /**
   * test method for {@link AttributeValueServiceClient#updatePidcName(AttributeValue)},
   * {@link AttributeValueServiceClient#deleteUnDelPidc(AttributeValue)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testUpdateDeletePidcName() throws ApicWebServiceException {
    AttributeValue obj = new AttributeValue();
    obj.setAttributeId(41l);
    obj.setDescriptionEng("Junit_PIDC_" + getRunId());
    obj.setTextValueEng("Junit_PIDC_" + getRunId());
    obj.setBoolvalue(null);
    obj.setOtherValue(null);
    obj.setNumValue(null);
    obj.setDateValue(null);
    obj.setDeleted(false);
    obj.setClearingStatus(CLEARING_STATUS.NOT_CLEARED.getDBText());
    obj.setChangeComment(null);
    obj.setCharacteristicValueId(null);
    // create PIDC attribute value
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    AttributeValue createdObject = serviceClient.create(obj);
    // validate
    assertNotNull("Created Object is null", createdObject);
    assertEquals("Attribute Id is equal", Long.valueOf(41), createdObject.getAttributeId());
    assertEquals("'Attribute value description is equal", "Junit_PIDC_" + getRunId(),
        createdObject.getDescriptionEng());
    assertEquals("Textvalue English is equal", "Junit_PIDC_" + getRunId(), createdObject.getTextValueEng());
    assertEquals("Clearing status is equal", "N", createdObject.getClearingStatus());
    // update PIDC
    createdObject.setDescriptionEng("Junit_Updated_PIDC_" + getRunId());
    createdObject.setDescriptionEng("Junit_Updated_PIDC_" + getRunId());
    AttributeValue updatedObject = serviceClient.updatePidcName(createdObject);
    // validate
    assertNotNull("Created Object is null", updatedObject);
    assertEquals("'Attribute value description is equal", "Junit_Updated_PIDC_" + getRunId(),
        updatedObject.getDescriptionEng());
    // delete
    updatedObject.setDeleted(true);
    serviceClient.deleteUnDelPidc(updatedObject);
  }

  /**
   * test method for {@link AttributeValueServiceClient#getFeatureMappedAttrVal(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetFeatureMappedAttrVal() throws ApicWebServiceException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    SortedSet<AttributeValue> retSet = serviceClient.getFeatureMappedAttrVal(2225L);
    assertFalse("Response should not be empty", retSet.isEmpty());
    for (AttributeValue attrValue : retSet) {
      LOG.info("Attribute value id for attribute {} is {}", attrValue.getAttributeId(), attrValue.getId());
      LOG.info("Attribute value: {}", attrValue.getTextValueEng());
    }
  }


  /**
   * test method for {@link AttributeValueServiceClient#getFeatureMappedAttrVal(Long)}.Negative test
   *
   * @throws ApicWebServiceException 'Attribute ID not found'
   */
  @Test
  public void testGetFeatureMappedAttrValNegative1() throws ApicWebServiceException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("DATA_NOT_FOUND"));

    serviceClient.getFeatureMappedAttrVal(INVALID_ATTR_ID_DEP);
    fail("Expected exception is not thrown");
  }

  /**
   * test method for {@link AttributeValueServiceClient#getFeatureMappedAttrVal(Long)}.Negative test
   *
   * @throws ApicWebServiceException 'Feature not mapped to this attribute'
   */
  @Test
  public void testGetFeatureMappedAttrValNegative2() throws ApicWebServiceException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("FEAVAL.FEATURE_VAL_MISSING"));

    serviceClient.getFeatureMappedAttrVal(ATTR_ID_WITHOUT_FEATURE);
    fail("Expected exception is not thrown");
  }


  /**
   * test method for {@link AttributeValueServiceClient#getMailContent(String)}
   *
   * @throws ApicWebServiceException exception
   * @throws IOException exception
   */
  @Test
  public void testGetMailContent() throws ApicWebServiceException, IOException {
    AttributeValueServiceClient serviceClient = new AttributeValueServiceClient();
    byte[] ret = serviceClient.getMailContent(CommonUtils.getSystemUserTempDirPath());
    Map<String, byte[]> filesUnZipped = ZipUtils.unzip(ret);
    String mailTemplate = new String(filesUnZipped.get(ApicConstants.DEL_ATTRVAL_MAIL_TEMPLATE));
    assertNotNull("Response should not be empty", mailTemplate);
  }

}
