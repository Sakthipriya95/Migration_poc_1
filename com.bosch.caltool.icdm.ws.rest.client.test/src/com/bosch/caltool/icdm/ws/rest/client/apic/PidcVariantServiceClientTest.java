/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantsInputData;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectObjectWithAttributes;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author DMR1COB
 */
// TABV_PROJECT_VARIANTS

public class PidcVariantServiceClientTest extends AbstractRestClientTest {

  private final static Long PIDC_VERSION_ID = 773513215L;
  private final static Long A2L_FILE_ID = 973735001L;

  private final static Long PIDC_VARIANT_ID = 768397972L;

  private static final long PIDC_VERS_ID_ACTIVE = 773510915L;// X_Test_002_P866_EA288 : Version 4

  private final static Long INV_PIDC_VERSION_ID = -773513215L;
  private final static Long INV_A2L_FILE_ID = -973735001L;
  private final static Long INV_PIDC_VARIANT_ID = -768397972L;

  private static final String EXPECTED_EXCEPTION_NOT_THROWN = "Expected exception not thron";


  /**
   * Test method for {@link PidcVariantServiceClient#get(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGet() throws ApicWebServiceException {
    PidcVariantServiceClient servClient = new PidcVariantServiceClient();
    PidcVariant ret = servClient.get(PIDC_VARIANT_ID);
    assertNotNull("Response should not be null", ret);
    testOutput(ret);
  }

  /**
   * Test method for {@link PidcVariantServiceClient#create(PidcVariantData)} ,
   * {@link PidcVariantServiceClient#update(PidcVariantData)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateUpdate() throws ApicWebServiceException {
    PidcVariantServiceClient servClient = new PidcVariantServiceClient();
    PidcVariantData pidcVariantData = new PidcVariantData();

    AttributeValue attributeValue = new AttributeValue();
    attributeValue.setDescriptionEng("Junit_PidcVar_CreUpd_" + getRunId());
    attributeValue.setAttributeId(126l);
    attributeValue.setTextValueEng("Junit_PidcVar_CreUpd_" + getRunId());
    attributeValue.setDeleted(false);
    attributeValue.setChangeComment(null);
    attributeValue.setCharacteristicValueId(null);
    attributeValue.setClearingStatus("R");

    AttributeValueServiceClient valueServiceClient = new AttributeValueServiceClient();

    PidcVersionServiceClient versionServiceClient = new PidcVersionServiceClient();
    pidcVariantData.setPidcVersion(versionServiceClient.getById(773515365l));
    pidcVariantData.setVarNameAttrValue(attributeValue);
    // Invoke create method
    PidcVariantData createdObj = servClient.create(pidcVariantData);
    // validate create
    assertNotNull("Created object not null", createdObj);
    PidcVariant createdPidcVariant = createdObj.getDestPidcVar();
    assertEquals("Pidc Version Id is equal", Long.valueOf(773515365l), createdPidcVariant.getPidcVersionId());
    assertEquals("Description is equal", "Junit_PidcVar_CreUpd_" + getRunId(), createdPidcVariant.getDescription());
    assertEquals("Pidc variant name is equal", "Junit_PidcVar_CreUpd_" + getRunId(), createdPidcVariant.getName());

    Long attrValId = createdObj.getDestPidcVar().getNameValueId();
    AttributeValue value = valueServiceClient.getById(attrValId);
    value.setDescriptionEng("Junit_PidcVar_CreUpd_Updated_" + getRunId());
    value.setTextValueEng("Junit_PidcVar_CreUpd_Updated_" + getRunId());
    createdObj.setPidcVersion(versionServiceClient.getById(773515365l));
    createdObj.setVarNameAttrValue(value);
    createdObj.setSrcPidcVar(createdPidcVariant);
    createdObj.setNameUpdated(true);
    // invoke update method
    PidcVariantData updatedPidcVariantData = servClient.update(createdObj);
    // validate update
    assertNotNull("Updated object not null", updatedPidcVariantData);
    PidcVariant updatedPidcVariant = updatedPidcVariantData.getDestPidcVar();
    assertEquals("Description is equal", "Junit_PidcVar_CreUpd_Updated_" + getRunId(),
        updatedPidcVariant.getDescription());
    assertEquals("Pidc version name is equal", "Junit_PidcVar_CreUpd_Updated_" + getRunId(),
        updatedPidcVariant.getName());
  }

  /**
   * Test method for {@link PidcVariantServiceClient#get(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetNegative() throws ApicWebServiceException {
    PidcVariantServiceClient servClient = new PidcVariantServiceClient();
    this.thrown.expectMessage("PIDC Variant with ID '" + INV_PIDC_VARIANT_ID + "' not found");
    servClient.get(INV_PIDC_VARIANT_ID);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }


  /**
   * test output data
   *
   * @param pvariant
   */
  private void testOutput(final PidcVariant pvariant) {
    assertEquals("Variant Id is equal", Long.valueOf(768397972), pvariant.getId());
    assertEquals("ValueId is equal", Long.valueOf(768455934), pvariant.getNameValueId());
    assertNotNull("CreatedDate is not null", pvariant.getCreatedDate());
    assertEquals("CreatedUser is equal", "RHJ2SI", pvariant.getCreatedUser());
    assertEquals("PidcVersId is equal", Long.valueOf(773513215), pvariant.getPidcVersionId());
  }


  /**
   * Test method for {@link PidcVariantServiceClient#getPidcVarForPidcVersAndA2l(Long,Long)}
   *
   * @throws ApicWebServiceException exception PIDC_VERSION_ID = 773513215L,A2L_FILE_ID = 973735001L =>
   *           Textvalue_eng(SDOM_PVER_NAME) = ME17921NG5 =>VALUE_ID =770166516
   */
  @Test
  public void testGetPidcVarForPidcVersAndA2l() throws ApicWebServiceException {
    PidcVariantServiceClient servClient = new PidcVariantServiceClient();
    SortedSet<PidcVariant> retSet = servClient.getPidcVarForPidcVersAndA2l(PIDC_VERSION_ID, A2L_FILE_ID);
    assertFalse("PIDC Variant list not null or empty", (retSet == null) || retSet.isEmpty());
    for (PidcVariant pvariant : retSet) {
      if (pvariant.getId() == 768397972L) {
        testOutput(pvariant);
      }
    }
  }

  /**
   * Test method for {@link PidcVariantServiceClient#getPidcVarForPidcVersAndA2l(Long,Long)}
   *
   * @throws ApicWebServiceException exception PIDC_VERSION_ID = 773513215L,A2L_FILE_ID = 973735001L =>
   *           Textvalue_eng(SDOM_PVER_NAME) = ME17921NG5 =>VALUE_ID =770166516
   */
  @Test
  public void testGetPidcVarForPidcVersAndA2lNegative() throws ApicWebServiceException {
    PidcVariantServiceClient servClient = new PidcVariantServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    servClient.getPidcVarForPidcVersAndA2l(INV_PIDC_VERSION_ID, INV_A2L_FILE_ID);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Test method for {@link PidcVariantServiceClient#getA2lMappedVariants(Long)}
   * <p>
   * test for PIDC with variants, when SDOM PVER is defined at PIDC level
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetA2lMappedVariantsAtPidcLevel() throws ApicWebServiceException {
    PidcVariantServiceClient servClient = new PidcVariantServiceClient();

    // A2L File: AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788
    // (V1)->MMD114A0CC1788->MMD114A0CC1788_MD00_withGroups.A2L
    // icdm:a2lid,1328585266-2189855001
    Map<Long, PidcVariant> retMap = servClient.getA2lMappedVariants(1328585266L);

    assertFalse("PIDC Variant MAP not null or empty", (retMap == null) || retMap.isEmpty());
    LOG.info("PIDC Variant list size = {}", retMap.size());
    PidcVariant variant = retMap.values().iterator().next();
    LOG.info("First PIDC Variant = {}, {}", variant.getId(), variant.getName());
  }

  /**
   * Test method for {@link PidcVariantServiceClient#getA2lMappedVariants(Long)}
   * <p>
   * test for PIDC with variants, when SDOM PVER is defined at variant level
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetA2lMappedVariantsAtVariantLevel() throws ApicWebServiceException {
    PidcVariantServiceClient servClient = new PidcVariantServiceClient();

    // A2L File: X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_002_P866_EA288 (Version
    // 4)->M1764VDAC866->DA761.A2L
    // icdm:a2lid,773712865-204758713
    Map<Long, PidcVariant> retMap = servClient.getA2lMappedVariants(773712865L);

    assertFalse("PIDC Variant MAP not null or empty", (retMap == null) || retMap.isEmpty());
    LOG.info("PIDC Variant list size = {}", retMap.size());
    PidcVariant variant = retMap.values().iterator().next();
    LOG.info("First PIDC Variant = {}, {}", variant.getId(), variant.getName());
  }

  /**
   * Test method for {@link PidcVariantServiceClient#getVariantsWithAttrs(Long, java.util.Set) }
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetVariantsWithAttrs() throws ApicWebServiceException {

    // PIDC Version: X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_002_P866_EA288 (Version 4)
    // Link icdm:pidvid,773510915

    Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> retMap = new PidcVariantServiceClient()
        .getVariantsWithAttrs(773510915L, new HashSet<>(Arrays.asList(83L, 84L, 85L, 224L, 243L, 731L)));

    assertFalse("Ret MAP not null or empty", (retMap == null) || retMap.isEmpty());
    LOG.info("PIDC Variant list size = {}", retMap.size());

    ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> varObj = retMap.values().iterator().next();
    PidcVariant var = varObj.getProjectObject();
    assertNotNull("variant not null", var);
    LOG.info("First PIDC Variant = {}, {}", var.getId(), var.getName());

    Map<Long, PidcVariantAttribute> varAttrMap = varObj.getProjectAttrMap();
    assertFalse((varAttrMap == null) || varAttrMap.isEmpty());
    LOG.info("PidcVariantAttribute map size = {}", varAttrMap.size());

    PidcVariantAttribute firstVarAttr = varAttrMap.values().iterator().next();
    LOG.info("First PIDC Variant attribute = {}, {}", firstVarAttr.getId(), firstVarAttr.getName());

  }

  /**
   * Test method for {@link PidcVariantServiceClient#hasVariant(Long, boolean)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testHasPidcVariants() throws ApicWebServiceException {
    boolean ret = new PidcVariantServiceClient().hasVariant(PIDC_VERS_ID_ACTIVE, false);
    LOG.info("Has pidc variants for pidc version {} = {} ", PIDC_VERS_ID_ACTIVE, ret);
    assertTrue("Has pidc variants for pidc version " + PIDC_VERS_ID_ACTIVE, ret);
  }


  /**
   * Test method for {@link PidcVariantServiceClient#getVariantsForVersion(Long, boolean)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetPidcVarForVer() throws ApicWebServiceException {
    Map<Long, PidcVariant> retMap = new PidcVariantServiceClient().getVariantsForVersion(PIDC_VERS_ID_ACTIVE, false);
    LOG.info("service response size = {}", retMap.size());
    assertFalse("service response available", retMap.isEmpty());
  }

  /**
   * Test method for {@link PidcVariantServiceClient#getVariantsForVersion(Long,boolean)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetVariantsForVersionNegative() throws ApicWebServiceException {
    PidcVariantServiceClient servClient = new PidcVariantServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    servClient.getVariantsForVersion(INV_PIDC_VERSION_ID, false);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Test method for {@link PidcVariantServiceClient#getA2lMappedVariants(Long)}
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetA2lMappedVariantsNegative() throws ApicWebServiceException {
    PidcVariantServiceClient servClient = new PidcVariantServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC A2L File with ID '-1328585266' not found");
    servClient.getA2lMappedVariants(-1328585266L);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Test method for {@link PidcVariantServiceClient#hasReviews(PidcVariant)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testHasReviews() throws ApicWebServiceException {
    PidcVariantServiceClient client = new PidcVariantServiceClient();
    PidcVariant pidcVariant = client.get(768398040l);
    boolean ret = client.hasReviews(pidcVariant);
    LOG.info("Has reviews for pidc variant {} = {} ", 768398040, ret);
    assertFalse("Has reviews for pidc variant " + 768398040, ret);
  }

  /**
   * Test method for {@link PidcVariantServiceClient#hasReviews(PidcVariant)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetPidcVariantsInputData() throws ApicWebServiceException {
    PidcVariantServiceClient client = new PidcVariantServiceClient();
    List<Long> elementIdList = new ArrayList<>();
    elementIdList.add(1293613521L);
    elementIdList.add(1537160237L);
    PidcVariantsInputData inputData = client.getPidcVariantsInputData(elementIdList);
    assertTrue(null != inputData);
  }


  /**
   * Test {@link PidcVariantServiceClient#getSdomPverMappedVariants(Long, String)}
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testGetSdomPverMappedVariants() throws ApicWebServiceException {
    PidcVariantServiceClient client = new PidcVariantServiceClient();
    Map<Long, PidcVariant> pidcVarMap = client.getSdomPverMappedVariants(PIDC_VERSION_ID, "ME17921N00");
    assertFalse("Variants are not present", pidcVarMap.isEmpty());
    assertTrue(pidcVarMap.values().stream().anyMatch(pidcvar -> pidcvar.getName().equals("NFE-EU3-16-82kW-CVT")));
    Set<Entry<Long, PidcVariant>> pidcs = pidcVarMap.entrySet();
    for (Entry<Long, PidcVariant> pidcVar : pidcs) {
      assertEquals(PIDC_VERSION_ID, pidcVar.getValue().getPidcVersionId());
    }

  }

  /**
   * Test {@link PidcVariantServiceClient#getSdomPverMappedVariants(Long, String)} with empty pvername
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testGetSdomPverMappedVariantsNegative01() throws ApicWebServiceException {
    PidcVariantServiceClient client = new PidcVariantServiceClient();
    Map<Long, PidcVariant> pidcVarMap = client.getSdomPverMappedVariants(PIDC_VERSION_ID, "");
    assertTrue(pidcVarMap.isEmpty());
  }

  /**
   * Test {@link PidcVariantServiceClient#getSdomPverMappedVariants(Long, String)} with PVER name as null
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testGetSdomPverMappedVariantsNegative02() throws ApicWebServiceException {
    PidcVariantServiceClient client = new PidcVariantServiceClient();
    Map<Long, PidcVariant> pidcVarMap;
    pidcVarMap = client.getSdomPverMappedVariants(PIDC_VERSION_ID, null);
    assertTrue(pidcVarMap.isEmpty());
  }


  /**
   * Test {@link PidcVariantServiceClient#getSdomPverMappedVariants(Long, String)} with null inputs
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testGetSdomPverMappedVariantsNegative03() throws ApicWebServiceException {
    PidcVariantServiceClient client = new PidcVariantServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("A2L file is not mapped to any PIDC version");
    client.getSdomPverMappedVariants(null, null);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test {@link PidcVariantServiceClient#getSdomPverMappedVariants(Long, String)} with null PIDC version
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testGetSdomPverMappedVariantsNegative04() throws ApicWebServiceException {
    PidcVariantServiceClient client = new PidcVariantServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("A2L file is not mapped to any PIDC version");
    client.getSdomPverMappedVariants(null, "ME17921N00");
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test {@link PidcVariantServiceClient#getSdomPverMappedVariants(Long, String)} with invalid PIDC version
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testGetSdomPverMappedVariantsNegative05() throws ApicWebServiceException {
    PidcVariantServiceClient client = new PidcVariantServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    client.getSdomPverMappedVariants(7735132L, "ME17921N00");
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

}
