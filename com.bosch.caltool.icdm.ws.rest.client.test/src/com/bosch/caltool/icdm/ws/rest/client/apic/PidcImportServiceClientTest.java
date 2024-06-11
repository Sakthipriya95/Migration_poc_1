/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcImportCompareData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcImportExcelData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectImportAttr;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author pdh2cob
 */
public class PidcImportServiceClientTest extends AbstractRestClientTest {


  /**
   *
   */
  private static final String CREATED_USER_ID = "PDH2COB";

  /**
   *
   */
  private static final String CREATED_USER_IS_EQUAL2 = "Created User is equal";

  /**
   *
   */
  private static final String PIDC_VERS_ID_IS_EQUAL = "Pidc_Vers_Id is equal";

  /**
   *
   */
  private static final String VALUE_ID_IS_EQUAL = "Value_Id is equal";

  /**
   *
   */
  private static final String USED_FLAG_IS_EQUAL = "Used Flag is equal";

  /**
   *
   */
  private static final String VALUE_IS_EQUAL = "Value is equal";

  /**
   *
   */
  private static final String VALUE_TYPE_ID_IS_EQUAL = "value type id is equal";

  /**
   *
   */
  private static final String CREATED_DATE_IS_NOT_NULL = "CreatedDate is not null";

  /**
   *
   */
  private static final String CREATED_USER_IS_EQUAL = "CreatedUser is equal";

  /**
   *
   */
  private static final String CHARID_IS_EQUAL = "charid is equal";

  /**
   *
   */
  private static final String ATTR_GROUP_ID_IS_EQUAL = "Attr Group id is equal";

  /**
   *
   */
  private static final String ATTR_NAME_IS_EQUAL = "Attr name is equal";

  /**
   *
   */
  private static final String ATTR_ID_IS_EQUAL = "Attr_Id is equal";
  /**
   *
   */
  private static final String COMMENT_IS_EQUAL = "Comment is equal";

  /**
   * Model for pidc version details
   */
  private PidcVersionWithDetails pidcVersionWithDetails;

  /**
   * Pidc Version ID
   */
  public static final Long PIDC_VERSION_ID = 2737947831L;

  /**
   * PIDC Level attribute id
   */
  public static final Long ECU_TYPE_ATTR_ID = 758860766L;

  /**
   * Sub Variant level attribute id
   */
  public static final Long DRIVE_TRAIN_ATTR_ID = 83L;

  /**
   * Pidc version attribute id
   */
  public static final Long CYLINDER_NO_ATTR_ID = 48L;

  /**
   * Variant level attribute id
   */
  public static final Long DIAG_LEGISLATION_ATTR_ID = 973L;

  /**
   * Variant level attrbute id
   */
  public static final Long LEGISLATION_CYCLE_ATTR_ID = 1420134666L;


  /**
   * @throws ApicWebServiceException exception from service
   */
  @Before
  public void init() throws ApicWebServiceException {
    PidcVersionServiceClient pidcVersionServiceClient = new PidcVersionServiceClient();
    this.pidcVersionWithDetails = pidcVersionServiceClient.getPidcVersionWithDetails(PIDC_VERSION_ID);

  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testValidateExcelDataWithChanges() throws ApicWebServiceException {

    PidcImportExcelData pidcImportExcelData = new PidcImportExcelData();

    pidcImportExcelData.setPidcVersion(this.pidcVersionWithDetails.getPidcVersionInfo().getPidcVersion());

    Map<Long, PidcVersionAttribute> versAttrMap = new HashMap<>();

    versAttrMap.put(ECU_TYPE_ATTR_ID, (PidcVersionAttribute) getProjectAttribute(
        this.pidcVersionWithDetails.getPidcVersionAttributeMap().get(ECU_TYPE_ATTR_ID)));

    PidcVersionAttribute versAttr = (PidcVersionAttribute) getProjectAttribute(
        this.pidcVersionWithDetails.getPidcVersionAttributeMap().get(CYLINDER_NO_ATTR_ID));
    if (null != versAttr) {
      versAttr.setValue("5");
    }
    versAttrMap.put(CYLINDER_NO_ATTR_ID, versAttr);

    pidcImportExcelData.setPidcImportAttrMap(versAttrMap);

    Map<Long, Map<Long, PidcVariantAttribute>> varAttrMap = new HashMap<>();

    Map<Long, Map<Long, Map<Long, PidcSubVariantAttribute>>> subvarImportAttrMap = new HashMap<>();

    for (PidcVariant variant : this.pidcVersionWithDetails.getPidcVariantMap().values()) {
      if (!variant.isDeleted()) {

        // fill variant attribute map
        Map<Long, PidcVariantAttribute> attrMap = new HashMap<>();
        attrMap.put(DIAG_LEGISLATION_ATTR_ID, (PidcVariantAttribute) getProjectAttribute(this.pidcVersionWithDetails
            .getPidcVariantAttributeMap().get(variant.getId()).get(DIAG_LEGISLATION_ATTR_ID)));
        attrMap.put(LEGISLATION_CYCLE_ATTR_ID, (PidcVariantAttribute) getProjectAttribute(this.pidcVersionWithDetails
            .getPidcVariantAttributeMap().get(variant.getId()).get(LEGISLATION_CYCLE_ATTR_ID)));

        varAttrMap.put(variant.getNameValueId(), attrMap);

        Map<Long, Map<Long, PidcSubVariantAttribute>> subvarAttrMap = getSubVarAttrMap(variant.getId());

        if (CommonUtils.isNotEmpty(subvarAttrMap)) {
          subvarImportAttrMap.put(variant.getNameValueId(), subvarAttrMap);
        }
      }
    }
    pidcImportExcelData.setVarImportAttrMap(varAttrMap);
    pidcImportExcelData.setSubvarImportAttrMap(subvarImportAttrMap);

    PidcImportCompareData compareData = new PidcImportServiceClient().validateExcelData(pidcImportExcelData);

    assertNotNull(compareData);
    assertFalse(compareData.getVerAttrImportData().isEmpty());
    validateCompareData(compareData);
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testValidateExcelDataWithVarAttrChanges() throws ApicWebServiceException {

    PidcImportExcelData pidcImportExcelData = new PidcImportExcelData();

    pidcImportExcelData.setPidcVersion(this.pidcVersionWithDetails.getPidcVersionInfo().getPidcVersion());

    Map<Long, PidcVersionAttribute> versAttrMap = new HashMap<>();

    versAttrMap.put(ECU_TYPE_ATTR_ID, (PidcVersionAttribute) getProjectAttribute(
        this.pidcVersionWithDetails.getPidcVersionAttributeMap().get(ECU_TYPE_ATTR_ID)));

    PidcVersionAttribute versAttr = (PidcVersionAttribute) getProjectAttribute(
        this.pidcVersionWithDetails.getPidcVersionAttributeMap().get(CYLINDER_NO_ATTR_ID));
    if (null != versAttr) {
      versAttr.setValue("5");
    }
    versAttrMap.put(CYLINDER_NO_ATTR_ID, versAttr);

    pidcImportExcelData.setPidcImportAttrMap(versAttrMap);

    Map<Long, Map<Long, PidcVariantAttribute>> varAttrMap = new HashMap<>();

    Map<Long, Map<Long, Map<Long, PidcSubVariantAttribute>>> subvarImportAttrMap = new HashMap<>();

    for (PidcVariant variant : this.pidcVersionWithDetails.getPidcVariantMap().values()) {
      if (!variant.isDeleted()) {

        // fill variant attribute map
        Map<Long, PidcVariantAttribute> attrMap = new HashMap<>();
        PidcVariantAttribute varAttr = (PidcVariantAttribute) getProjectAttribute(this.pidcVersionWithDetails
            .getPidcVariantAttributeMap().get(variant.getId()).get(DIAG_LEGISLATION_ATTR_ID));
        if (varAttr != null) {
          varAttr.setValue("");
        }

        attrMap.put(DIAG_LEGISLATION_ATTR_ID, varAttr);
        attrMap.put(LEGISLATION_CYCLE_ATTR_ID, (PidcVariantAttribute) getProjectAttribute(this.pidcVersionWithDetails
            .getPidcVariantAttributeMap().get(variant.getId()).get(LEGISLATION_CYCLE_ATTR_ID)));

        varAttrMap.put(variant.getNameValueId(), attrMap);

        Map<Long, Map<Long, PidcSubVariantAttribute>> subvarAttrMap = getSubVarAttrMap(variant.getId());

        if (CommonUtils.isNotEmpty(subvarAttrMap)) {
          subvarImportAttrMap.put(variant.getNameValueId(), subvarAttrMap);
        }
      }
    }
    pidcImportExcelData.setVarImportAttrMap(varAttrMap);
    pidcImportExcelData.setSubvarImportAttrMap(subvarImportAttrMap);

    PidcImportCompareData compareData = new PidcImportServiceClient().validateExcelData(pidcImportExcelData);

    assertNotNull(compareData);
    assertFalse(compareData.getVerAttrImportData().isEmpty());
    validateVarAttrCompareData(compareData);
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  public void testValidateExcelDataWithSubVarAttrChanges() throws ApicWebServiceException {

    PidcImportExcelData pidcImportExcelData = new PidcImportExcelData();

    pidcImportExcelData.setPidcVersion(this.pidcVersionWithDetails.getPidcVersionInfo().getPidcVersion());

    Map<Long, PidcVersionAttribute> versAttrMap = new HashMap<>();

    versAttrMap.put(ECU_TYPE_ATTR_ID, (PidcVersionAttribute) getProjectAttribute(
        this.pidcVersionWithDetails.getPidcVersionAttributeMap().get(ECU_TYPE_ATTR_ID)));

    PidcVersionAttribute versAttr = (PidcVersionAttribute) getProjectAttribute(
        this.pidcVersionWithDetails.getPidcVersionAttributeMap().get(CYLINDER_NO_ATTR_ID));
    if (versAttr != null) {
      versAttr.setValue("5");
    }
    versAttrMap.put(CYLINDER_NO_ATTR_ID, versAttr);

    pidcImportExcelData.setPidcImportAttrMap(versAttrMap);

    Map<Long, Map<Long, PidcVariantAttribute>> varAttrMap = new HashMap<>();

    Map<Long, Map<Long, Map<Long, PidcSubVariantAttribute>>> subvarImportAttrMap = new HashMap<>();

    for (PidcVariant variant : this.pidcVersionWithDetails.getPidcVariantMap().values()) {
      if (!variant.isDeleted()) {

        // fill variant attribute map
        Map<Long, PidcVariantAttribute> attrMap = new HashMap<>();
        PidcVariantAttribute varAttr = (PidcVariantAttribute) getProjectAttribute(this.pidcVersionWithDetails
            .getPidcVariantAttributeMap().get(variant.getId()).get(DIAG_LEGISLATION_ATTR_ID));

        attrMap.put(DIAG_LEGISLATION_ATTR_ID, varAttr);
        attrMap.put(LEGISLATION_CYCLE_ATTR_ID, (PidcVariantAttribute) getProjectAttribute(this.pidcVersionWithDetails
            .getPidcVariantAttributeMap().get(variant.getId()).get(LEGISLATION_CYCLE_ATTR_ID)));

        varAttrMap.put(variant.getNameValueId(), attrMap);

        Map<Long, Map<Long, PidcSubVariantAttribute>> subvarAttrMap = getSubVarAttrMap(variant.getId());

        if (CommonUtils.isNotEmpty(subvarAttrMap)) {

          subvarAttrMap.values().iterator().next().get(DRIVE_TRAIN_ATTR_ID).setValue("4WD");

          subvarImportAttrMap.put(variant.getNameValueId(), subvarAttrMap);
        }
      }
    }
    pidcImportExcelData.setVarImportAttrMap(varAttrMap);
    pidcImportExcelData.setSubvarImportAttrMap(subvarImportAttrMap);

    PidcImportCompareData compareData = new PidcImportServiceClient().validateExcelData(pidcImportExcelData);

    assertNotNull(compareData);
    assertFalse(compareData.getVerAttrImportData().isEmpty());
    validateSubVarAttrCompareData(compareData);
  }


  private void validateSubVarAttrCompareData(final PidcImportCompareData compareData) {

    assertFalse(compareData.getSubvarAttrImportData().isEmpty());
    ProjectImportAttr<PidcSubVariantAttribute> subVarAttrImport =
        compareData.getSubvarAttrImportData().get(2917596382L).get(DRIVE_TRAIN_ATTR_ID);
    assertTrue(subVarAttrImport.isValidImport());
    assertFalse(subVarAttrImport.isNewlyAddedVal());
    assertFalse(subVarAttrImport.isCreateAttr());
    assertTrue(subVarAttrImport.isCleared());
    assertEquals(COMMENT_IS_EQUAL, null, subVarAttrImport.getComment());

    validateAttributeSubVar(subVarAttrImport.getAttr());
    validateCompareSubVarAttr(subVarAttrImport.getCompareAttr());
    validateExcelSubvarAttribute(subVarAttrImport.getExcelAttr());
    validateSubVarAttr(subVarAttrImport.getPidcAttr());

  }


  private void validateVarAttrCompareData(final PidcImportCompareData compareData) {

    assertFalse(compareData.getVarAttrImportData().isEmpty());
    ProjectImportAttr<PidcVariantAttribute> varAttrImport =
        compareData.getVarAttrImportData().get(2796874778L).get(DIAG_LEGISLATION_ATTR_ID);
    assertTrue(varAttrImport.isValidImport());
    assertTrue(varAttrImport.isNewlyAddedVal());
    assertFalse(varAttrImport.isCreateAttr());
    assertTrue(varAttrImport.isCleared());
    assertEquals(COMMENT_IS_EQUAL, "Newly Added Attribute Value", varAttrImport.getComment());
    validateAttributeVar(varAttrImport.getAttr());
    validateCompareVarAttr(varAttrImport.getCompareAttr());
    validateExcelVarAttribute(varAttrImport.getExcelAttr());
    validateVarAttr(varAttrImport.getPidcAttr());

  }


  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testValidateExcelDataNoChanges() throws ApicWebServiceException {
    PidcImportExcelData pidcImportExcelData = new PidcImportExcelData();

    pidcImportExcelData.setPidcVersion(this.pidcVersionWithDetails.getPidcVersionInfo().getPidcVersion());

    Map<Long, PidcVersionAttribute> versAttrMap = new HashMap<>();

    versAttrMap.put(ECU_TYPE_ATTR_ID, (PidcVersionAttribute) getProjectAttribute(
        this.pidcVersionWithDetails.getPidcVersionAttributeMap().get(ECU_TYPE_ATTR_ID)));
    versAttrMap.put(CYLINDER_NO_ATTR_ID, (PidcVersionAttribute) getProjectAttribute(
        this.pidcVersionWithDetails.getPidcVersionAttributeMap().get(CYLINDER_NO_ATTR_ID)));

    pidcImportExcelData.setPidcImportAttrMap(versAttrMap);

    Map<Long, Map<Long, PidcVariantAttribute>> varAttrMap = new HashMap<>();

    Map<Long, Map<Long, Map<Long, PidcSubVariantAttribute>>> subvarImportAttrMap = new HashMap<>();

    for (PidcVariant variant : this.pidcVersionWithDetails.getPidcVariantMap().values()) {
      if (!variant.isDeleted()) {

        // fill variant attribute map
        Map<Long, PidcVariantAttribute> attrMap = new HashMap<>();
        attrMap.put(DIAG_LEGISLATION_ATTR_ID, (PidcVariantAttribute) getProjectAttribute(this.pidcVersionWithDetails
            .getPidcVariantAttributeMap().get(variant.getId()).get(DIAG_LEGISLATION_ATTR_ID)));
        attrMap.put(LEGISLATION_CYCLE_ATTR_ID, (PidcVariantAttribute) getProjectAttribute(this.pidcVersionWithDetails
            .getPidcVariantAttributeMap().get(variant.getId()).get(LEGISLATION_CYCLE_ATTR_ID)));

        varAttrMap.put(variant.getNameValueId(), attrMap);

        Map<Long, Map<Long, PidcSubVariantAttribute>> subvarAttrMap = getSubVarAttrMap(variant.getId());

        if (CommonUtils.isNotEmpty(subvarAttrMap)) {
          subvarImportAttrMap.put(variant.getNameValueId(), subvarAttrMap);
        }
      }
    }

    pidcImportExcelData.setVarImportAttrMap(varAttrMap);
    pidcImportExcelData.setSubvarImportAttrMap(subvarImportAttrMap);

    PidcImportCompareData compareData = new PidcImportServiceClient().validateExcelData(pidcImportExcelData);

    assertNotNull(compareData);
    assertTrue(compareData.getVerAttrImportData().isEmpty());
    assertTrue(compareData.getVarAttrImportData().isEmpty());
    assertTrue(compareData.getSubvarAttrImportData().isEmpty());
  }


  /**
   * @throws ApicWebServiceException exception from service
   */
  public void testValidateExcelDataNullInput() throws ApicWebServiceException {
    new PidcImportServiceClient().validateExcelData(null);
  }


  /**
   * @throws ApicWebServiceException exception from service
   */
  public void testValidateExcelDataEmptyInput() throws ApicWebServiceException {
    new PidcImportServiceClient().validateExcelData(new PidcImportExcelData());
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  public void testValidateExcelDataNullPidcVersion() throws ApicWebServiceException {

    PidcImportExcelData pidcImportExcelData = new PidcImportExcelData();

    pidcImportExcelData.setPidcVersion(null);

    new PidcImportServiceClient().validateExcelData(pidcImportExcelData);
  }


  /**
   * @param compareData output compare data
   */
  private void validateCompareData(final PidcImportCompareData compareData) {
    assertFalse(compareData.getVerAttrImportData().isEmpty());
    ProjectImportAttr<PidcVersionAttribute> versAttrImport =
        compareData.getVerAttrImportData().get(CYLINDER_NO_ATTR_ID);
    assertTrue(versAttrImport.isValidImport());
    assertFalse(versAttrImport.isNewlyAddedVal());
    assertFalse(versAttrImport.isCreateAttr());
    assertTrue(versAttrImport.isCleared());
    assertEquals(COMMENT_IS_EQUAL, null, versAttrImport.getComment());
    validateAttribute(versAttrImport.getAttr());
    validateCompareAttr(versAttrImport.getCompareAttr());
    validateExcelAttribute(versAttrImport.getExcelAttr());
    validatePidcAttr(versAttrImport.getPidcAttr());
  }

  private void validateAttribute(final Attribute attribute) {
    assertEquals(ATTR_ID_IS_EQUAL, CYLINDER_NO_ATTR_ID, attribute.getId());
    assertEquals(ATTR_NAME_IS_EQUAL, "Cylinder (No.)", attribute.getName());
    assertEquals(ATTR_GROUP_ID_IS_EQUAL, Long.valueOf(47), attribute.getAttrGrpId());
    assertEquals(CHARID_IS_EQUAL, Long.valueOf(767172065), attribute.getCharacteristicId());
    assertEquals(CREATED_USER_IS_EQUAL, "hef2fe", attribute.getCreatedUser());
    assertNotNull(CREATED_DATE_IS_NOT_NULL, attribute.getCreatedDate());
    assertEquals(VALUE_TYPE_ID_IS_EQUAL, Long.valueOf(2), attribute.getValueTypeId());
  }

  private void validateAttributeVar(final Attribute attribute) {
    assertEquals(ATTR_ID_IS_EQUAL, DIAG_LEGISLATION_ATTR_ID, attribute.getId());
    assertEquals(ATTR_NAME_IS_EQUAL, "Diagnosis Legislation", attribute.getName());
    assertEquals(ATTR_GROUP_ID_IS_EQUAL, Long.valueOf(957), attribute.getAttrGrpId());
    assertEquals(CHARID_IS_EQUAL, Long.valueOf(795258665), attribute.getCharacteristicId());
    assertEquals(CREATED_USER_IS_EQUAL, "tbd2si", attribute.getCreatedUser());
    assertNotNull(CREATED_DATE_IS_NOT_NULL, attribute.getCreatedDate());
    assertEquals(VALUE_TYPE_ID_IS_EQUAL, Long.valueOf(1), attribute.getValueTypeId());
  }


  private void validateAttributeSubVar(final Attribute attribute) {
    assertEquals(ATTR_ID_IS_EQUAL, DRIVE_TRAIN_ATTR_ID, attribute.getId());
    assertEquals(ATTR_NAME_IS_EQUAL, "Drivetrain", attribute.getName());
    assertEquals(ATTR_GROUP_ID_IS_EQUAL, Long.valueOf(82), attribute.getAttrGrpId());
    assertEquals(CHARID_IS_EQUAL, Long.valueOf(768842415), attribute.getCharacteristicId());
    assertEquals(CREATED_USER_IS_EQUAL, "hef2fe", attribute.getCreatedUser());
    assertNotNull(CREATED_DATE_IS_NOT_NULL, attribute.getCreatedDate());
    assertEquals(VALUE_TYPE_ID_IS_EQUAL, Long.valueOf(1), attribute.getValueTypeId());
  }

  private void validateExcelSubvarAttribute(final PidcSubVariantAttribute pidcSubVariantAttribute) {
    assertEquals(ATTR_ID_IS_EQUAL, DRIVE_TRAIN_ATTR_ID, pidcSubVariantAttribute.getAttrId());
    assertEquals(VALUE_IS_EQUAL, "4WD", pidcSubVariantAttribute.getValue());
    assertEquals(USED_FLAG_IS_EQUAL, "Y", pidcSubVariantAttribute.getUsedFlag());
  }

  private void validateExcelAttribute(final PidcVersionAttribute pidcVersionAttribute) {
    assertEquals(ATTR_ID_IS_EQUAL, CYLINDER_NO_ATTR_ID, pidcVersionAttribute.getAttrId());
    assertEquals(VALUE_IS_EQUAL, "5", pidcVersionAttribute.getValue());
    assertEquals(USED_FLAG_IS_EQUAL, "Y", pidcVersionAttribute.getUsedFlag());
  }

  private void validateExcelVarAttribute(final PidcVariantAttribute pidcVariantAttribute) {
    assertEquals(ATTR_ID_IS_EQUAL, DIAG_LEGISLATION_ATTR_ID, pidcVariantAttribute.getAttrId());
    assertEquals(VALUE_IS_EQUAL, "", pidcVariantAttribute.getValue());
    assertEquals(USED_FLAG_IS_EQUAL, "Y", pidcVariantAttribute.getUsedFlag());
  }

  /**
   * @param pidcVersionAttribute pidc version attribute
   */
  private void validateCompareAttr(final PidcVersionAttribute pidcVersionAttribute) {
    assertEquals(ATTR_ID_IS_EQUAL, CYLINDER_NO_ATTR_ID, pidcVersionAttribute.getAttrId());
    assertEquals(VALUE_ID_IS_EQUAL, Long.valueOf(74), pidcVersionAttribute.getValueId());
    assertEquals(VALUE_IS_EQUAL, "5", pidcVersionAttribute.getValue());
    assertEquals(USED_FLAG_IS_EQUAL, "Y", pidcVersionAttribute.getUsedFlag());
    assertEquals(CREATED_USER_IS_EQUAL2, CREATED_USER_ID, pidcVersionAttribute.getCreatedUser());
    assertEquals(PIDC_VERS_ID_IS_EQUAL, PIDC_VERSION_ID, pidcVersionAttribute.getPidcVersId());
  }


  /**
   * @param pidcVersionAttribute pidc version attribute
   */
  private void validateCompareSubVarAttr(final PidcSubVariantAttribute pidcSubVariantAttribute) {
    assertEquals(ATTR_ID_IS_EQUAL, DRIVE_TRAIN_ATTR_ID, pidcSubVariantAttribute.getAttrId());
    // assertEquals("Value_Id is equal", Long.valueOf(779394321), pidcSubVariantAttribute.getValueId());
    assertEquals(VALUE_IS_EQUAL, "4WD", pidcSubVariantAttribute.getValue());
    assertEquals(USED_FLAG_IS_EQUAL, "Y", pidcSubVariantAttribute.getUsedFlag());
    assertEquals(CREATED_USER_IS_EQUAL2, CREATED_USER_ID, pidcSubVariantAttribute.getCreatedUser());
    assertEquals(PIDC_VERS_ID_IS_EQUAL, PIDC_VERSION_ID, pidcSubVariantAttribute.getPidcVersionId());
  }


  /**
   * @param pidcVariantAttribute pidc version attribute
   */
  private void validateCompareVarAttr(final PidcVariantAttribute pidcVariantAttribute) {
    assertEquals(ATTR_ID_IS_EQUAL, DIAG_LEGISLATION_ATTR_ID, pidcVariantAttribute.getAttrId());
    assertEquals(VALUE_IS_EQUAL, "", pidcVariantAttribute.getValue());
    assertEquals(USED_FLAG_IS_EQUAL, "Y", pidcVariantAttribute.getUsedFlag());
    assertEquals(CREATED_USER_IS_EQUAL2, CREATED_USER_ID, pidcVariantAttribute.getCreatedUser());
  }

  private void validatePidcAttr(final PidcVersionAttribute pidcVersionAttribute) {
    assertEquals(ATTR_ID_IS_EQUAL, CYLINDER_NO_ATTR_ID, pidcVersionAttribute.getAttrId());
    assertEquals(VALUE_ID_IS_EQUAL, Long.valueOf(73), pidcVersionAttribute.getValueId());
    assertEquals(VALUE_IS_EQUAL, "4", pidcVersionAttribute.getValue());
    assertEquals(USED_FLAG_IS_EQUAL, "Y", pidcVersionAttribute.getUsedFlag());
    assertEquals(CREATED_USER_IS_EQUAL2, CREATED_USER_ID, pidcVersionAttribute.getCreatedUser());
    assertEquals(PIDC_VERS_ID_IS_EQUAL, PIDC_VERSION_ID, pidcVersionAttribute.getPidcVersId());
  }

  private void validateVarAttr(final PidcVariantAttribute pidcVariantAttribute) {
    assertEquals(ATTR_ID_IS_EQUAL, DIAG_LEGISLATION_ATTR_ID, pidcVariantAttribute.getAttrId());
    assertEquals(VALUE_ID_IS_EQUAL, Long.valueOf(975), pidcVariantAttribute.getValueId());
    assertEquals(VALUE_IS_EQUAL, "EOBD EU5", pidcVariantAttribute.getValue());
    assertEquals(USED_FLAG_IS_EQUAL, "Y", pidcVariantAttribute.getUsedFlag());
    assertEquals(CREATED_USER_IS_EQUAL2, CREATED_USER_ID, pidcVariantAttribute.getCreatedUser());
    assertEquals(PIDC_VERS_ID_IS_EQUAL, PIDC_VERSION_ID, pidcVariantAttribute.getPidcVersionId());
  }


  private void validateSubVarAttr(final PidcSubVariantAttribute pidcSubVariantAttribute) {
    assertEquals(ATTR_ID_IS_EQUAL, DRIVE_TRAIN_ATTR_ID, pidcSubVariantAttribute.getAttrId());
    // assertEquals("Value_Id is equal", Long.valueOf(975), pidcSubVariantAttribute.getValueId());
    // assertEquals("Value is equal", "4WD", pidcSubVariantAttribute.getValue());
    assertEquals(USED_FLAG_IS_EQUAL, "Y", pidcSubVariantAttribute.getUsedFlag());
    assertEquals(CREATED_USER_IS_EQUAL2, CREATED_USER_ID, pidcSubVariantAttribute.getCreatedUser());
    assertEquals(PIDC_VERS_ID_IS_EQUAL, PIDC_VERSION_ID, pidcSubVariantAttribute.getPidcVersionId());
  }


  /**
   * @param projAttr
   * @return project attribute with minimum details, as only these details are available in excel
   */
  private IProjectAttribute getProjectAttribute(final IProjectAttribute projAttr) {
    if (projAttr instanceof PidcVersionAttribute) {
      PidcVersionAttribute versAttr = new PidcVersionAttribute();
      versAttr.setAttrId(projAttr.getAttrId());
      versAttr.setValue(projAttr.getValue());
      versAttr.setValueId(projAttr.getValueId());
      versAttr.setUsedFlag("Y");
      return versAttr;
    }
    else if (projAttr instanceof PidcVariantAttribute) {
      PidcVariantAttribute varAttr = new PidcVariantAttribute();
      varAttr.setAttrId(projAttr.getAttrId());
      varAttr.setValue(projAttr.getValue());
      varAttr.setValueId(projAttr.getValueId());
      varAttr.setUsedFlag("Y");
      return varAttr;
    }
    else if (projAttr instanceof PidcSubVariantAttribute) {
      PidcSubVariantAttribute subvarAttr = new PidcSubVariantAttribute();
      subvarAttr.setAttrId(projAttr.getAttrId());
      subvarAttr.setValueId(projAttr.getValueId());
      subvarAttr.setValue(projAttr.getValue());
      subvarAttr.setUsedFlag("Y");
      return subvarAttr;
    }
    return null;
  }


  /**
   * @param variantId variant id
   * @return Map-key->variant name value id, value->Map-key->Attr id, value->pidc sub var attr
   */
  private Map<Long, Map<Long, PidcSubVariantAttribute>> getSubVarAttrMap(final Long variantId) {
    Map<Long, Map<Long, PidcSubVariantAttribute>> subvarAttrMap = new HashMap<>();
    for (Long subvariantId : this.pidcVersionWithDetails.getPidcSubVariantAttributeMap().keySet()) {

      if (this.pidcVersionWithDetails.getPidcSubVariantMap().get(subvariantId).getPidcVariantId().equals(variantId)) {
        Map<Long, PidcSubVariantAttribute> attrMap = new HashMap<>();

        if (CommonUtils.isNotEmpty(this.pidcVersionWithDetails.getPidcSubVariantAttributeMap().get(subvariantId))) {
          PidcSubVariantAttribute subVarAttr = (PidcSubVariantAttribute) getProjectAttribute(
              this.pidcVersionWithDetails.getPidcSubVariantAttributeMap().get(subvariantId).get(DRIVE_TRAIN_ATTR_ID));
          attrMap.put(DRIVE_TRAIN_ATTR_ID, subVarAttr);
          subvarAttrMap.put(this.pidcVersionWithDetails.getPidcSubVariantMap().get(subvariantId).getNameValueId(),
              attrMap);
        }
      }
    }
    return subvarAttrMap;
  }

}
