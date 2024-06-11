/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateUtil;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;
import com.bosch.caltool.icdm.model.apic.pidc.ExternalPidcVersionWithAttributes;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationRespData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTypeV2;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithAttributes;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithAttributesV2;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectObjectWithAttributes;
import com.bosch.caltool.icdm.model.uc.ProjectUsecaseModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;


/**
 * @author bne4cob
 */
public class PidcServiceClientTest extends AbstractRestClientTest {


  // X_Test_002_P866_EA288 : 2747L

  // PIDC Version: X_Testcustomer->Diesel Engine->PC - Passenger Car->BCU Gen 1->X_Test_ICDM_RBEI_01 (Version 1)
  // URL:icdm:pidvid,773515265

  /**
   *
   */
  private static final String LOG_OUTPUT_SEPARATOR = "=========================";
  /**
   *
   */
  private static final String TAB_STRING = "\t{}\t{}\t{}\t{}\t{}";
  /**
   *
   */
  private static final String ATTR_DETAILS = "\tAttr ID\tAttr Name\tProj Attr ID\tUsed Flag\tValue";
  /**
   *
   */
  private static final String TOTAL_PROJCT_ATTRIBUTES = "Total project attributes = {}";
  /**
   *
   */
  private static final String SIZE = "Size : {}";
  /**
   *
   */
  private static final String NOT_NULL_RESPONSE = "Response should not be null";
  /**
   *
   */
  private static final String NOT_NULL_EMPTY_RESPONSE = "Response should not be null or empty";
  /**
   *
   */
  private static final String NAME_IS_EQUAL = "Name is equal";
  /**
   *
   */
  private static final String JUNIT_PIDC = "Junit_Pidc";
  /**
   *
   */
  private static final String DESC_ENG_IS_EQUAL = "Desc_Eng is equal";
  private static final Long PIDC_ID = 760420017L;
  private static final Long INVALID_ID = -100L;
  private static final Long ATTR_VAL_ID = 787372417L;

  /**
   * Negative test with empty ucid set Test retrieval of pidc with attributes(active version)
   * {@link PidcServiceClient#getPidcWithAttributes(Long, Set)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcWithAttrNegative() throws ApicWebServiceException {
    PidcServiceClient servClient = new PidcServiceClient();

    PidcVersionWithAttributes ret = servClient.getPidcWithAttributes(PIDC_ID, new HashSet<>());

    assertNotNull(NOT_NULL_EMPTY_RESPONSE, ret);
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, ret.getPidcVersionInfo());

    testOutput(ret.getPidcVersionInfo());

    LOG.info(TOTAL_PROJCT_ATTRIBUTES, ret.getPidcAttributeMap().size());
    LOG.info(LOG_OUTPUT_SEPARATOR);
    LOG.info(ATTR_DETAILS);

    for (Entry<Long, PidcVersionAttribute> entry : ret.getPidcAttributeMap().entrySet()) {
      PidcVersionAttribute projAttr = entry.getValue();
      LOG.info(TAB_STRING, entry.getKey(), projAttr.getName(), projAttr.getId(), projAttr.getUsedFlag(),
          projAttr.getValue());
    }


  }

  /**
   * Test retrieval of pidc with attributes(active version) for specific use cases
   * {@link PidcServiceClient#getPidcWithAttributes(Long, Set)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcWithAttributes() throws ApicWebServiceException {
    PidcServiceClient servClient = new PidcServiceClient();

    Set<Long> ucIdSet = new HashSet<>();
    ucIdSet.add(772259515L);// USECASE : Air System Model ASMod Diesel
    ucIdSet.add(772259565L);// USECASE : Applikationsdatenverwaltung Calibration Data Management
    PidcVersionWithAttributes ret = servClient.getPidcWithAttributes(PIDC_ID, ucIdSet);

    assertNotNull(NOT_NULL_EMPTY_RESPONSE, ret);
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, ret.getPidcVersionInfo());

    testOutput(ret.getPidcVersionInfo());

    LOG.info(TOTAL_PROJCT_ATTRIBUTES, ret.getPidcAttributeMap().size());
    LOG.info(LOG_OUTPUT_SEPARATOR);
    LOG.info(ATTR_DETAILS);

    for (Entry<Long, PidcVersionAttribute> entry : ret.getPidcAttributeMap().entrySet()) {
      PidcVersionAttribute projAttr = entry.getValue();
      LOG.info(TAB_STRING, entry.getKey(), projAttr.getName(), projAttr.getId(), projAttr.getUsedFlag(),
          projAttr.getValue());
    }

    Map<Long, PidcVersionAttribute> pidcAttributeMap = ret.getPidcAttributeMap();

    assertNotNull(NOT_NULL_EMPTY_RESPONSE, pidcAttributeMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, pidcAttributeMap.isEmpty());

    PidcVersionAttribute pidcVersionAttribute = pidcAttributeMap.get(230670L);// 230670L = Assignment SW2CAL (technical)
    assertNotNull(NOT_NULL_RESPONSE, pidcVersionAttribute);
    testPidcVersionAttribute(pidcVersionAttribute);

    Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> variantMap = ret.getVariantMap();

    assertNotNull(NOT_NULL_EMPTY_RESPONSE, variantMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, variantMap.isEmpty());

    ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> projObjWithAttr = variantMap.get(760420048L);
    IProjectObject projectObject = projObjWithAttr.getProjectObject();
    assertNotNull(NOT_NULL_RESPONSE, projectObject);
    testProjectObject(projectObject);

    Map<Long, ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute>> subVariantMap =
        ret.getSubVariantMap();

    assertNotNull(NOT_NULL_EMPTY_RESPONSE, subVariantMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, subVariantMap.isEmpty());

    ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute> projObjWithAttrNew =
        subVariantMap.get(774402419L);
    IProjectObject projectObjectNew = projObjWithAttrNew.getProjectObject();
    assertNotNull(NOT_NULL_RESPONSE, projectObject);
    testProjectObjectNew(projectObjectNew);

    Map<Long, Set<Long>> varWithSubVarIds = ret.getVarWithSubVarIds();

    assertNotNull(NOT_NULL_EMPTY_RESPONSE, varWithSubVarIds);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, varWithSubVarIds.isEmpty());

    LOG.info(SIZE, varWithSubVarIds.size());

    Map<Long, Attribute> attributeMap = ret.getAttributeMap();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, attributeMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, attributeMap.isEmpty());
    Attribute attribute = attributeMap.get(2225L);
    assertNotNull(NOT_NULL_RESPONSE, attribute);
    testAttribute(attribute);

    Map<Long, AttributeValue> attributeValueMap = ret.getAttributeValueMap();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, attributeValueMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, attributeValueMap.isEmpty());
    AttributeValue attributeVal = attributeValueMap.get(2227L);
    assertNotNull(NOT_NULL_RESPONSE, attributeVal);
    testAttributeValue(attributeVal);
  }

  /**
   * Test method for {@link PidcServiceClient#getExternalPidcWithAttributes(Long, Set) }
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void testGetExternalPidcWithAttributes() throws ApicWebServiceException {
    PidcServiceClient servClient = new PidcServiceClient();
    Set<Long> ucIdSet = new HashSet<>();
    ucIdSet.add(772259515L);// USECASE : Air System Model ASMod Diesel
    ucIdSet.add(772259565L);// USECASE : Applikationsdatenverwaltung Calibration Data Management

    ExternalPidcVersionWithAttributes ret = servClient.getExternalPidcWithAttributes(PIDC_ID, ucIdSet);

    assertNotNull(NOT_NULL_EMPTY_RESPONSE, ret);
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, ret.getPidcVersionInfo());

    testOutput(ret.getPidcVersionInfo());

    LOG.info(TOTAL_PROJCT_ATTRIBUTES, ret.getPidcAttributeMap().size());
    LOG.info(LOG_OUTPUT_SEPARATOR);
    LOG.info(ATTR_DETAILS);

    for (Entry<Long, PidcVersionAttribute> entry : ret.getPidcAttributeMap().entrySet()) {
      PidcVersionAttribute projAttr = entry.getValue();
      LOG.info(TAB_STRING, entry.getKey(), projAttr.getName(), projAttr.getId(), projAttr.getUsedFlag(),
          projAttr.getValue());
    }

    Map<Long, PidcVersionAttribute> pidcAttributeMap = ret.getPidcAttributeMap();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, pidcAttributeMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, pidcAttributeMap.isEmpty());

    PidcVersionAttribute pidcVersionAttribute = pidcAttributeMap.get(230670L);
    assertNotNull(NOT_NULL_RESPONSE, pidcVersionAttribute);
    testPidcVersionAttribute(pidcVersionAttribute);

    Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> variantMap = ret.getVariantMap();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, variantMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, variantMap.isEmpty());

    ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> projObjWithAttr = variantMap.get(760420048L);
    IProjectObject projectObject = projObjWithAttr.getProjectObject();
    assertNotNull(NOT_NULL_RESPONSE, projectObject);
    testProjectObject(projectObject);

    Map<Long, ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute>> subVariantMap =
        ret.getSubVariantMap();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, subVariantMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, subVariantMap.isEmpty());
    ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute> projObjWithAttrNew =
        subVariantMap.get(774402419L);
    IProjectObject projectObjectNew = projObjWithAttrNew.getProjectObject();
    assertNotNull(NOT_NULL_RESPONSE, projectObject);
    testProjectObjectNew(projectObjectNew);

    Map<Long, Set<Long>> varWithSubVarIds = ret.getVarWithSubVarIds();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, varWithSubVarIds);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, varWithSubVarIds.isEmpty());
    LOG.info(SIZE, varWithSubVarIds.size());

    Map<Long, Attribute> attributeMap = ret.getAttributeMap();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, attributeMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, attributeMap.isEmpty());
    Attribute attribute = attributeMap.get(2225L);
    assertNotNull(NOT_NULL_RESPONSE, attribute);
    testAttribute(attribute);

    Map<Long, AttributeValue> attributeValueMap = ret.getAttributeValueMap();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, attributeValueMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, attributeValueMap.isEmpty());
    AttributeValue attributeVal = attributeValueMap.get(2227L);
    assertNotNull(NOT_NULL_RESPONSE, attributeVal);
    testAttributeValue(attributeVal);
  }

  /**
   * @param projectObjectNew
   */
  private void testProjectObjectNew(final IProjectObject projectObjectNew) {
    assertEquals(NAME_IS_EQUAL, "X_Test_IR01V01_SV02", projectObjectNew.getName());
    assertNotNull("Created Date is not null", projectObjectNew.getCreatedDate());
  }

  /**
   * @param attributeVal
   */
  private void testAttributeValue(final AttributeValue attributeVal) {
    assertEquals("Attribute_Id is equal", Long.valueOf(2225), attributeVal.getAttributeId());
    assertEquals(DESC_ENG_IS_EQUAL, "Diesel Engine", attributeVal.getDescriptionEng());
    assertEquals("Desc_Ger is equal", "Dieselmotor", attributeVal.getDescriptionGer());
  }

  /**
   * Test method for {@link PidcServiceClient#update(Pidc) }
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testUpdate() throws ApicWebServiceException {

    PidcServiceClient servClient = new PidcServiceClient();

    Pidc pidc = servClient.getById(PIDC_ID);
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, pidc);

    Pidc pidcBkp = new Pidc();
    CommonUtils.shallowCopy(pidcBkp, pidc);
    pidc.setAprjId(100L);
    pidc.setVcdmTransferDate(DateUtil.getCurrentUtcTime().toString());
    pidc.setVcdmTransferUser("RGO7COB");

    Pidc updatedPidc = servClient.update(pidc);
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, updatedPidc);
    assertEquals("update vcdm tfr user", "RGO7COB", updatedPidc.getVcdmTransferUser());

    // Revert the change for future tests
    updatedPidc.setAprjId(pidcBkp.getAprjId());
    updatedPidc.setVcdmTransferDate(pidcBkp.getVcdmTransferDate());
    updatedPidc.setVcdmTransferUser(pidcBkp.getVcdmTransferUser());

    Pidc reverted = servClient.update(updatedPidc);
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, reverted);
    assertEquals("update vcdm tfr user revert", pidcBkp.getVcdmTransferUser(), updatedPidc.getVcdmTransferUser());
  }

  /**
   * Test method for {@link PidcServiceClient#getByNameValueId(Long) }
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetByNameValueId() throws ApicWebServiceException {
    // Value ID - 4482 : PIDC = TEST KAI MED17 RQ1 PROD
    Pidc pidc = new PidcServiceClient().getByNameValueId(4482L);
    assertNotNull(NOT_NULL_RESPONSE, pidc);

    LOG.info("PIDC Name = {}", pidc.getName());
    assertEquals("Verify name of pidc ", "TEST KAI MED17 RQ1 PROD", pidc.getName());
  }

  /**
   * Test method for {@link PidcServiceClient#getByNameValueId(Long) }
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetByNameValueIdInvalid() throws ApicWebServiceException {
    Pidc pidc = new PidcServiceClient().getByNameValueId(-100L);

    assertNull("Response should   be null ", pidc);
  }

  /**
   * Test output
   *
   * @param reportData output object
   */
  private void testOutput(final PidcVersionInfo pidcVersWithDet) {
    LOG.info("PIDC Version = " + pidcVersWithDet.getPidcVersion().getName());
    assertFalse("attributes not empty",
        (pidcVersWithDet.getLevelAttrMap() == null) || pidcVersWithDet.getLevelAttrMap().isEmpty());
  }

  private void testOutput(final PidcTypeV2 pidcTypeV2) {
    LOG.info("PIDC Name = " + pidcTypeV2.getPidcInfoTypeV2().getName());
    assertFalse("attributes not empty", (pidcTypeV2.getAttributeWithValueTypeV2List() == null) ||
        (pidcTypeV2.getPidcInfoTypeV2() == null) || (pidcTypeV2.getPidcVariantTypeV2List() == null));
  }

  /**
   * Test method for {@link PidcServiceClient#getPidcWithAttributesV2(Long, Long, Set)}.
   *
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testGetPidcWithAttributesV2Negative() throws ApicWebServiceException {
    PidcServiceClient servClient = new PidcServiceClient();

    PidcVersionWithAttributesV2 ret = servClient.getPidcWithAttributesV2(PIDC_ID, null, new HashSet<>());

    assertNotNull(NOT_NULL_EMPTY_RESPONSE, ret);
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, ret.getPidcVersionInfo());

    testOutput(ret.getPidcVersionInfo());

    LOG.info(TOTAL_PROJCT_ATTRIBUTES, ret.getPidcAttributeMap().size());
    LOG.info(LOG_OUTPUT_SEPARATOR);
    LOG.info(ATTR_DETAILS);

    for (Entry<Long, PidcVersionAttribute> entry : ret.getPidcAttributeMap().entrySet()) {
      PidcVersionAttribute projAttr = entry.getValue();
      LOG.info(TAB_STRING, entry.getKey(), projAttr.getName(), projAttr.getId(), projAttr.getUsedFlag(),
          projAttr.getValue());
    }
  }

  /**
   * Test method for {@link PidcServiceClient#getPidcWithAttributesV2(Long, Long, Set)}.
   *
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testGetPidcWithAttributesV2() throws ApicWebServiceException {
    PidcServiceClient servClient = new PidcServiceClient();

    Set<Long> ucIdSet = new HashSet<>();
    ucIdSet.add(772259515L);// USECASE : Air System Model ASMod Diesel
    ucIdSet.add(772259565L);// USECASE : Applikationsdatenverwaltung Calibration Data Management
    PidcVersionWithAttributesV2 ret = servClient.getPidcWithAttributesV2(PIDC_ID, null, ucIdSet);

    assertNotNull(NOT_NULL_EMPTY_RESPONSE, ret);
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, ret.getPidcVersionInfo());

    testOutput(ret.getPidcVersionInfo());

    LOG.info(TOTAL_PROJCT_ATTRIBUTES, ret.getPidcAttributeMap().size());
    LOG.info(LOG_OUTPUT_SEPARATOR);
    LOG.info(ATTR_DETAILS);

    for (Entry<Long, PidcVersionAttribute> entry : ret.getPidcAttributeMap().entrySet()) {
      PidcVersionAttribute projAttr = entry.getValue();
      LOG.info(TAB_STRING, entry.getKey(), projAttr.getName(), projAttr.getId(), projAttr.getUsedFlag(),
          projAttr.getValue());
    }

    Map<Long, PidcVersionAttribute> pidcAttributeMap = ret.getPidcAttributeMap();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, pidcAttributeMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, pidcAttributeMap.isEmpty());
    PidcVersionAttribute pidcVersionAttribute = pidcAttributeMap.get(230670L);
    assertNotNull(NOT_NULL_RESPONSE, pidcVersionAttribute);
    testPidcVersionAttribute(pidcVersionAttribute);

    Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> variantMap = ret.getVariantMap();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, variantMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, variantMap.isEmpty());
    ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> projObjWithAttr = variantMap.get(760420048L);
    IProjectObject projectObject = projObjWithAttr.getProjectObject();
    assertNotNull(NOT_NULL_RESPONSE, projectObject);
    testProjectObject(projectObject);

    Map<Long, ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute>> subVariantMap =
        ret.getSubVariantMap();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, subVariantMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, subVariantMap.isEmpty());
    ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute> projObjWithAttrNew =
        subVariantMap.get(774402419L);
    IProjectObject projectObjectNew = projObjWithAttrNew.getProjectObject();
    assertNotNull(NOT_NULL_RESPONSE, projectObject);
    testProjectObjectNew(projectObjectNew);

    Map<Long, Set<Long>> varWithSubVarIds = ret.getVarWithSubVarIds();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, varWithSubVarIds);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, varWithSubVarIds.isEmpty());
    LOG.info(SIZE, varWithSubVarIds.size());

    Map<Long, Attribute> attributeMap = ret.getAttributeMap();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, attributeMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, attributeMap.isEmpty());
    Attribute attribute = attributeMap.get(2225L);
    assertNotNull(NOT_NULL_RESPONSE, attribute);
    testAttribute(attribute);

    Map<Long, AttributeValue> attributeValueMap = ret.getAttributeValueMap();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, attributeValueMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, attributeValueMap.isEmpty());
    AttributeValue attributeVal = attributeValueMap.get(2227L);
    assertNotNull(NOT_NULL_RESPONSE, attributeVal);
    testAttributeValue(attributeVal);

    Map<Long, Characteristic> characteristicMap = ret.getCharacteristicMap();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, characteristicMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, characteristicMap.isEmpty());
    Characteristic characteristic = characteristicMap.get(789778815L);
    assertNotNull(NOT_NULL_RESPONSE, characteristic);
    testCharacteristic(characteristic);
  }

  /**
   * @param characteristic
   */
  private void testCharacteristic(final Characteristic characteristic) {
    assertEquals("CharNameEng is equal", "ProjInfo", characteristic.getCharNameEng());
    assertEquals("DescEng is equal",
        "project information, generally, administrative; also used for identification of 3rd party supplier",
        characteristic.getDescEng());
    assertEquals("DescGer is equal",
        "Projektinformationen, allgemein, administrativ; wird auch genutzt, um Lieferanten zu identifizieren",
        characteristic.getDescGer());
    assertEquals("Created User is equal", "DGS_ICDM", characteristic.getCreatedUser());
  }

  /**
   * @param attribute
   */
  private void testAttribute(final Attribute attribute) {
    assertEquals(DESC_ENG_IS_EQUAL, "ECU used for powertrain component", attribute.getDescriptionEng());
    assertEquals("Desc_Ger is equal", "Steuergerät genutzt für definierte Komponente im Triebstrang",
        attribute.getDescriptionGer());
    assertEquals("Created User is equal", "hef2fe", attribute.getCreatedUser());
  }

  /**
   * @param projectObject
   */
  private void testProjectObject(final IProjectObject projectObject) {
    assertEquals(NAME_IS_EQUAL, "X_Test_IR01_V02", projectObject.getName());
    assertEquals("Description is equal", "Test Variant 02 for ICDM dev", projectObject.getDescription());
    assertNotNull("Created Date is not null", projectObject.getCreatedDate());
  }

  /**
   * @param pidcVersionAttribute
   */
  private void testPidcVersionAttribute(final PidcVersionAttribute pidcVersionAttribute) {
    assertEquals(NAME_IS_EQUAL, "Assignment SW2CAL (technical)", pidcVersionAttribute.getName());
    assertEquals("Pidc_Vers_Id is equal", Long.valueOf(773515265), pidcVersionAttribute.getPidcVersId());
  }

  /**
   * Test method for {@link PidcServiceClient#getProjectUsecaseModel(Long)}.
   *
   * @throws ApicWebServiceException - exception from web service
   */
  @Test
  public void testGetProjectUsecaseMappedAttributes() throws ApicWebServiceException {
    PidcServiceClient client = new PidcServiceClient();
    Set<Long> attrIdSet = client.getProjectUsecaseModel(790699167L).getProjectUsecaseAttrIdSet();
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, attrIdSet);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, attrIdSet.isEmpty());
    assertTrue(attrIdSet.contains(1265L));
  }

  /**
   * Negative Test method for {@link PidcServiceClient#getProjectUsecaseModel(Long)}.
   *
   * @throws ApicWebServiceException - exception from web service
   */
  @Test
  public void testGetProjectUsecaseMappedAttributesNegative() throws ApicWebServiceException {
    PidcServiceClient client = new PidcServiceClient();
    ProjectUsecaseModel model = client.getProjectUsecaseModel(INVALID_ID);
    assertTrue("Set must be empty", model.getProjectUsecaseAttrIdSet().isEmpty());
  }


  /**
   * Test method for {@link PidcServiceClient#getPidcTypeV2(Long, Long, Set)}.
   *
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testGetPidcTypeV2Negative() throws ApicWebServiceException {
    PidcServiceClient servClient = new PidcServiceClient();
    PidcTypeV2 ret = servClient.getPidcTypeV2(PIDC_ID, null, new HashSet<>());

    assertFalse(NOT_NULL_EMPTY_RESPONSE, (ret == null));
    testOutput(ret);
    LOG.info(LOG_OUTPUT_SEPARATOR);
    LOG.info(ATTR_DETAILS);

  }


  /**
   * Test method for {@link PidcServiceClient#getPidcTypeV2(Long, Long, Set)}.
   *
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testGetPidcTypeV2() throws ApicWebServiceException {
    PidcServiceClient servClient = new PidcServiceClient();

    Set<Long> ucIdSet = new HashSet<>();
    ucIdSet.add(772259515L);// USECASE : Air System Model ASMod Diesel
    ucIdSet.add(772259565L);// USECASE : Applikationsdatenverwaltung Calibration Data Management
    PidcTypeV2 ret = servClient.getPidcTypeV2(PIDC_ID, null, ucIdSet);

    assertFalse(NOT_NULL_EMPTY_RESPONSE, (ret == null));
    testOutput(ret);
    LOG.info(LOG_OUTPUT_SEPARATOR);
    LOG.info(ATTR_DETAILS);

  }

  /**
   * Test method for {@link PidcServiceClient#getPidcUsersUsingAttrValue(Long)}.
   *
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testGetPidcUsersUsingAttrValue() throws ApicWebServiceException {
    PidcServiceClient servClient = new PidcServiceClient();

    Map<String, Map<String, Map<String, Long>>> pidcUsersMap = servClient.getPidcUsersUsingAttrValue(ATTR_VAL_ID);
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, pidcUsersMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, pidcUsersMap.isEmpty());

    Map<String, Map<String, Long>> pidcMap = pidcUsersMap.get("AEW2FE");
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, pidcMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, pidcMap.isEmpty());

    Map<String, Long> pidcVersionsMap = pidcMap.get("HONDA XE1B 2SV-R");
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, pidcVersionsMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, pidcVersionsMap.isEmpty());
    assertEquals("pidc version id is equal", Long.valueOf(773511065), pidcVersionsMap.get("Version 2"));
  }

  /**
   * Negative Test method for {@link PidcServiceClient#getPidcUsersUsingAttrValue(Long)}.
   *
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testGetPidcUsersUsingAttrValueNegative() throws ApicWebServiceException {
    PidcServiceClient servClient = new PidcServiceClient();

    Map<String, Map<String, Map<String, Long>>> pidcUsersMap = servClient.getPidcUsersUsingAttrValue(INVALID_ID);
    assertTrue("Response should be empty", pidcUsersMap.isEmpty());
  }

  /**
   * Test method for {@link PidcServiceClient#createPidc(PidcCreationData)}}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreatePidc() throws ApicWebServiceException {
    PidcServiceClient serviceClient = new PidcServiceClient();

    Pidc newPidc = new Pidc();
    newPidc.setNameEng(JUNIT_PIDC + getRunId());
    newPidc.setDescEng(JUNIT_PIDC + getRunId());
    newPidc.setDescGer(null);

    PidcVersion pidcVersion = new PidcVersion();
    pidcVersion.setVersionName("Junit_Test_Version_" + getRunId());
    pidcVersion.setVersDescEng("Junit_Test_Version");

    PidcCreationData creationData = new PidcCreationData();
    creationData.setPidc(newPidc);
    creationData.setAllVersionSet(new HashSet<>(Arrays.asList(pidcVersion)));
    creationData.setOwner(new UserServiceClient().getCurrentApicUser());
    creationData.getStructAttrValMap().put(2232l, 2233l);
    creationData.getStructAttrValMap().put(2225l, 2227l);
    creationData.getStructAttrValMap().put(36l, 782110518l);
    creationData.getStructAttrValMap().put(197l, 2271l);

    PidcCreationRespData createdRespData = serviceClient.createPidc(creationData);
    assertNotNull("Created object is  not null", createdRespData);

    // validate create
    assertEquals("Name_Eng is equal", JUNIT_PIDC + getRunId(), createdRespData.getPidc().getNameEng());
    assertEquals(DESC_ENG_IS_EQUAL, JUNIT_PIDC + getRunId(), createdRespData.getPidc().getDescEng());
  }

}

