/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author HNU1COB
 */
public class AttrNValueDependencyServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  // Dependency ID : 1941
  // of Attribute : 1940 Knock Sensor - No.
  // to Attribute : 1935 Knock Sensor - used, Value : 1937 installed
  // TABV_ATTR_DEPENDENCIES
  private static final Long DEP_ID = 1941L;
  private static final Long INV_DEP_ID = -1941L;
  private static final long ATTR_ID = 1495497768L;
  private static final long INV_ATTR_ID = -1495497768L;
  private static final long ATTR_VAL_ID = 1495524185L;
  private static final long INV_ATTR_VAL_ID = -1495524185L;

  /**
   * Test method for {@link AttrNValueDependencyServiceClient#getById(Long)} test. Expects missing dependency error
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    AttrNValueDependencyServiceClient service = new AttrNValueDependencyServiceClient();
    AttrNValueDependency ret = service.getById(DEP_ID);
    assertNotNull("Response should not be null", ret);
    testOutput(ret);
  }


  /**
   * Test method for {@link AttrNValueDependencyServiceClient#getById(Long)} test. Expects missing dependency error
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    AttrNValueDependencyServiceClient service = new AttrNValueDependencyServiceClient();
    this.thrown.expectMessage("Attribute/Value Dependency with ID '" + INV_DEP_ID + "' not found");
    service.getById(INV_DEP_ID);
    fail("Expected exception is not thrown");

  }

  /**
   * Test method for {@link AttrNValueDependencyServiceClient#create(AttrNValueDependency)}
   * {@link AttrNValueDependencyServiceClient#update(AttrNValueDependency)}
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testCreateUpdate() throws ApicWebServiceException {
    Attribute input = new Attribute();
    input.setNameEng("Z-Junit_" + getRunId());
    input.setDescriptionEng("Z-Junit_");
    input.setEadmName("Z_Junit_" + getRunId());
    input.setAttrGrpId(519L);
    input.setExternal(false);
    input.setMandatory(false);
    input.setValueTypeId(1L);
    // create Attribute
    AttributeServiceClient servClient = new AttributeServiceClient();
    Attribute ret = servClient.create(input);
    assertNotNull("Created object should not be null ", ret);


    AttrNValueDependency obj = new AttrNValueDependency();
    obj.setAttributeId(ret.getId());
    obj.setDependentAttrId(1265L);
    obj.setDependentValueId(1453194378L);
    obj.setChangeComment(null);
    obj.setDeleted(false);
    // create AttrNValueDependency
    AttrNValueDependencyServiceClient service = new AttrNValueDependencyServiceClient();
    AttrNValueDependency createdObject = service.create(obj);
    // validate create
    assertNotNull("Response should not be null", createdObject);
    assertEquals("Dependency Name is equal", "ABS", createdObject.getName());
    assertEquals("Dependency Description is equal", "Antilock Braking System (ABS) installed",
        createdObject.getDescription());
    assertEquals("Dependency AttributeId is equal", ret.getId(), createdObject.getAttributeId());
    assertEquals("Dependency Value is equal", "FALSE", createdObject.getValue());
    assertEquals("Dependency DependentAttrId is equal", Long.valueOf(1265), createdObject.getDependentAttrId());
    assertEquals("Dependency DependentValueId is equal", Long.valueOf(1453194378), createdObject.getDependentValueId());
    assertFalse("Dependency Deleted is equal", createdObject.isDeleted());
    assertNull("Dependency ChangeComment is null", obj.getChangeComment());
    assertEquals("Version is equal", Long.valueOf(1), createdObject.getVersion());
    // update
    createdObject.setDependentAttrId(1836L);
    AttrNValueDependency updatedObject = service.update(createdObject);
    // validate update
    Long version = createdObject.getVersion() + 1;
    assertEquals("Dependency Attribute Id is equal", Long.valueOf(1836), updatedObject.getDependentAttrId());
    assertNotNull("Modified User is not null", updatedObject.getModifiedUser());
    assertEquals("Version is equal", version, updatedObject.getVersion());

  }

  /**
   * Test method for {@link AttrNValueDependencyServiceClient#getDependenciesByAttribute(Long)} test. Expects missing
   * dependency error
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetDependenciesByAttribute() throws ApicWebServiceException {
    AttrNValueDependencyServiceClient service = new AttrNValueDependencyServiceClient();
    Set<AttrNValueDependency> ret = service.getDependenciesByAttribute(ATTR_ID);
    assertNotNull("Response should not be null", ret);
    for (AttrNValueDependency attdep : ret) {
      if (attdep.getId() == DEP_ID) {
        testOutput(attdep);
      }
    }
  }

  /**
   * Test method for {@link AttrNValueDependencyServiceClient#getDependenciesByAttribute(Long)} test. Expects missing
   * dependency error
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetDependenciesByAttributeNegative() throws ApicWebServiceException {
    AttrNValueDependencyServiceClient service = new AttrNValueDependencyServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    service.getDependenciesByAttribute(INV_ATTR_ID);
    fail("Expected exception not thrown");

  }


  /**
   * Test method for {@link AttrNValueDependencyServiceClient#getDependenciesByValue(Long)} test. Expects missing
   * dependency error
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetDependenciesByValue() throws ApicWebServiceException {
    AttrNValueDependencyServiceClient service = new AttrNValueDependencyServiceClient();
    Set<AttrNValueDependency> ret = service.getDependenciesByValue(ATTR_VAL_ID);
    assertNotNull("Response should not be null", ret);
    for (AttrNValueDependency attdep : ret) {
      if (attdep.getId() == DEP_ID) {
        testOutput(attdep);
      }
    }
  }

  /**
   * Test method for {@link AttrNValueDependencyServiceClient#getDependenciesByValue(Long)} test. Expects missing
   * dependency error
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetDependenciesByValueNegative() throws ApicWebServiceException {
    AttrNValueDependencyServiceClient service = new AttrNValueDependencyServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    service.getDependenciesByValue(INV_ATTR_VAL_ID);
    fail("Expected exception not thrown");
  }


  /**
   * Test method for {@link AttrNValueDependencyServiceClient#getValDepnForLvlAttrValues()}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetValDepnForLvlAttrValues() throws ApicWebServiceException {
    Map<Long, Map<Long, Set<Long>>> retMap = new AttrNValueDependencyServiceClient().getValDepnForLvlAttrValues();
    LOG.info("service response size = {}", retMap.size());
    assertFalse("service response available", retMap.isEmpty());
  }

  /**
   * @param dep
   */
  private void testOutput(final AttrNValueDependency dep) {
    assertEquals("Dependency ID is equal", Long.valueOf(1941), dep.getId());
    assertEquals("Dependency Name is equal", "Knock Sensor - used", dep.getName());
    assertEquals("Dependency Description is equal", "Knock Sensor installed", dep.getDescription());
    assertEquals("Dependency AttributeId is equal", Long.valueOf(1940), dep.getAttributeId());
    assertEquals("Dependency ValueId is equal", null, dep.getValueId());
    assertEquals("Dependency Value is equal", "TRUE", dep.getValue());
    assertEquals("Dependency DependentAttrId is equal", Long.valueOf(1935), dep.getDependentAttrId());
    assertEquals("Dependency DependentValueId is equal", Long.valueOf(1937), dep.getDependentValueId());
    assertFalse("Dependency Deleted is equal", dep.isDeleted());
    assertEquals("Dependency ChangeComment is equal", null, dep.getChangeComment());
  }


}

