/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidityCreationModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author HNU1COB
 */
public class PredefinedValidityServiceClientTest extends AbstractRestClientTest {

  private final static Long PREDEFINED_VALIDITY_ID = 1421900274L;
  private final static Long GRP_VALUE_ID = 1421900272L;
  private final static Long INVALID_PREDEFINED_VALIDITY_ID = -1L;
  private final static Long INVALID_GRP_VALUE_ID = -1L;

  /**
   * Test method for {@link PredefinedValidityServiceClient#getById(Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    PredefinedValidityServiceClient client = new PredefinedValidityServiceClient();
    PredefinedValidity ret = client.getById(PREDEFINED_VALIDITY_ID);
    assertNotNull("Response should not be null", ret);
    testOutput(ret);
  }

  /**
   * Test method for {@link PredefinedValidityServiceClient#getByValueId(Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByValueId() throws ApicWebServiceException {
    PredefinedValidityServiceClient client = new PredefinedValidityServiceClient();
    Set<PredefinedValidity> retSet = client.getByValueId(GRP_VALUE_ID);
    assertFalse("Response should not be null or empty", ((retSet == null) || (retSet.isEmpty())));
    for (PredefinedValidity validity : retSet) {
      if (validity.getId() == PREDEFINED_VALIDITY_ID) {
        testOutput(validity);
      }
    }

  }

  /**
   * @param obj PredefinedValidity
   */
  public void testOutput(final PredefinedValidity obj) {
    assertEquals("Predefined Validity Id is equal", Long.valueOf(1421900274), obj.getId());
    assertEquals("Group Attribute Id is equal", Long.valueOf(1421900272), obj.getGrpAttrValId());
    assertEquals("Validity Value Id is equal", Long.valueOf(4136), obj.getValidityValueId());
    assertEquals("Created User is equal", "DJA7COB", obj.getCreatedUser());
    assertNotNull("Created Date is not null", obj.getCreatedDate());
    assertEquals("Validity Attribute Id is equal", Long.valueOf(36), obj.getValidityAttrId());
  }

  /**
   * Test method for {@link PredefinedValidityServiceClient#getById(Long)}.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    PredefinedValidityServiceClient client = new PredefinedValidityServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Predefined Validity with ID '" + INVALID_PREDEFINED_VALIDITY_ID + "' not found");
    client.getById(INVALID_PREDEFINED_VALIDITY_ID);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link PredefinedValidityServiceClient#getByValueId(Long)}.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByValueIdNegative() throws ApicWebServiceException {
    PredefinedValidityServiceClient client = new PredefinedValidityServiceClient();
    Set<PredefinedValidity> retSet = client.getByValueId(INVALID_GRP_VALUE_ID);
    assertTrue("Response should  be null or empty", ((retSet == null) || (retSet.isEmpty())));
  }

  /**
   * Test method for {@link PredefinedValidityServiceClient#createValidity(PredefinedValidityCreationModel)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateValidity() throws ApicWebServiceException {
    PredefinedValidityServiceClient client = new PredefinedValidityServiceClient();
    PredefinedValidityCreationModel creationModel = new PredefinedValidityCreationModel();
    PredefinedValidity validity = new PredefinedValidity();
    validity.setGrpAttrValId(1421843580l);
    validity.setValidityAttrId(36l);
    validity.setValidityValueId(782110518l);

    Set<PredefinedValidity> validities = new HashSet<>();
    validities.add(validity);
    creationModel.setValidityToBeCreated(validities);
    Set<PredefinedValidity> createdValidities = client.createValidity(creationModel);
    for (PredefinedValidity createdValidity : createdValidities) {
      assertNotNull("Response should not be null", createdValidity);
      assertEquals("Group Attribute Id is equal", Long.valueOf(1421843580l), createdValidity.getGrpAttrValId());
      assertEquals("Validity Value Id is equal", Long.valueOf(782110518), createdValidity.getValidityValueId());
      assertEquals("Validity Attribute Id is equal", Long.valueOf(36), createdValidity.getValidityAttrId());
    }

  }
}

