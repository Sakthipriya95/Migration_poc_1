/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author dmo5cob Test method for {@link ProjectAttributesUpdationServiceClient}
 */
public class ProjectAttributesUpdationServiceClientTest extends AbstractRestClientTest {


  // PIDC :test_muthu (version 1)
  // icdm:pidvid,1677573856

  /**
   * 
   */
  private static final String RESPONSE_SHOULD_NOT_BE_NULL = "Response should not be null";
  /**
   *
   */
  private static final String INVALID_INPUT_FOR_PROJECT_ATTRIBUTES_UPDATE =
      "Invalid input for project attributes update.";
  /**
   *
   */
  private static final String ATTR_LOG_MSG = "Attribute : {}";
  /**
   *
   */
  private static final String ADD_TO_VISIBLE_VARATTR_SET = "Attribute added to visible variant attribute set:";
  /**
   * Attribute AC Installed
   */
  private static final long AC_ATTR_ID = 391L;
  /**
   * Pidc -test_muthu,variant-AT_120kW,pidc link-icdm:pidvid,1677573856
   */
  private static final long PIDC_VARIANT_ID = 2141546480L;

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testNullInputForProjectAttrUpdateService() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(INVALID_INPUT_FOR_PROJECT_ATTRIBUTES_UPDATE);
    ProjectAttributesUpdationServiceClient servClient = new ProjectAttributesUpdationServiceClient();
    servClient.updatePidcAttrs(null);
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testNullInputForProjectAttrUpdateServiceInvalidVersion() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(INVALID_INPUT_FOR_PROJECT_ATTRIBUTES_UPDATE);
    ProjectAttributesUpdationModel projectAttributesUpdationModel = new ProjectAttributesUpdationModel();
    projectAttributesUpdationModel.setPidcVersion(null);
    new ProjectAttributesUpdationServiceClient().updatePidcAttrs(projectAttributesUpdationModel);
  }

  /**
   * Test to create , update project attribute with new attribute value
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateUpdateNewValAttr() throws ApicWebServiceException {

    ProjectAttributesUpdationServiceClient servClient = new ProjectAttributesUpdationServiceClient();

    ProjectAttributesUpdationModel model = new ProjectAttributesUpdationModel();
    PidcVersion pidcVersion = new PidcVersionServiceClient().getById(1507932231L);
    model.setPidcVersion(pidcVersion);

    Attribute attr = new Attribute();
    attr.setNameEng("Junit_attr1_" + getRunId());
    attr.setDescriptionEng("Junit_attr1");
    attr.setEadmName("Junit_attr1_" + getRunId());
    attr.setAttrGrpId(519L);
    attr.setExternal(false);
    attr.setMandatory(false);
    attr.setValueTypeId(1L);

    // create attribute
    AttributeServiceClient attrClient = new AttributeServiceClient();
    Attribute createdAttr = attrClient.create(attr);
    assertNotNull("Response should not be null ", createdAttr);

    PidcVersionAttribute verAttr = new PidcVersionAttribute();
    verAttr.setAttrId(createdAttr.getId());
    verAttr.setValue("Junit_attrVal_" + getRunId());
    verAttr.setValueType("TEXT");
    verAttr.setUsedFlag(ApicConstants.CODE_YES);
    verAttr.setPidcVersId(1507932231L);
    // test to create pidc attr with new value
    model.getPidcAttrsToBeCreatedwithNewVal().put(verAttr.getAttrId(), verAttr);
    ProjectAttributesUpdationModel modelAfterCreation = servClient.updatePidcAttrs(model);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, modelAfterCreation);


    // update pidc version - set new value , AtChildLevel to true
    PidcVersionAttribute verAttrExisting =
        modelAfterCreation.getPidcAttrsToBeCreatedwithNewVal().get(createdAttr.getId());
    verAttrExisting.setAtChildLevel(true);
    verAttrExisting.setValue("Junit_attrValUpdated_" + getRunId());
    verAttrExisting.setValueType("TEXT");
    model.getPidcAttrsToBeCreatedwithNewVal().clear();
    model.getPidcAttrsToBeUpdatedwithNewVal().put(verAttrExisting.getAttrId(), verAttrExisting);

    ProjectAttributesUpdationModel modelAfterUpdation = servClient.updatePidcAttrs(model);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, modelAfterUpdation);
  }

  /**
   * Test to create and update project attribute , variant attribute aand subvariant attribute
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateUpdateDleteVerVarSubvarAttr() throws ApicWebServiceException {

    ProjectAttributesUpdationServiceClient servClient = new ProjectAttributesUpdationServiceClient();

    ProjectAttributesUpdationModel model = new ProjectAttributesUpdationModel();
    PidcVersion pidcVersion = new PidcVersionServiceClient().getById(1677573856L);
    model.setPidcVersion(pidcVersion);

    Attribute attr = new Attribute();
    attr.setNameEng("Junit_attr2_" + getRunId());
    attr.setDescriptionEng("Junit_attr2");
    attr.setEadmName("Junit_attr2_" + getRunId());
    attr.setAttrGrpId(519L);
    attr.setExternal(false);
    attr.setMandatory(false);
    attr.setValueTypeId(1L);

    // create attribute
    AttributeServiceClient attrClient = new AttributeServiceClient();
    Attribute createdAttr = attrClient.create(attr);
    assertNotNull("Response should not be null ", createdAttr);

    PidcVersionAttribute verAttr = new PidcVersionAttribute();
    verAttr.setAttrId(createdAttr.getId());
    verAttr.setValueId(2049525710L);
    verAttr.setUsedFlag(ApicConstants.CODE_YES);
    verAttr.setPidcVersId(1677573856L);
    verAttr.setCanMoveDown(true);
    // test to create pidc attr with existing value
    model.getPidcAttrsToBeCreated().put(verAttr.getAttrId(), verAttr);
    ProjectAttributesUpdationModel modelAfterCreation = servClient.updatePidcAttrs(model);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, modelAfterCreation);

    // test to update version attribute - set IsVariant to Yes
    PidcVersionAttribute verAttrExisting = modelAfterCreation.getPidcAttrsToBeCreated().values().iterator().next();
    verAttrExisting.setAtChildLevel(true);

    // create pidc variant attr for variant AT_120KW
    PidcVariantAttribute varAttr1 = new PidcVariantAttribute();
    varAttr1.setAttrId(createdAttr.getId());
    varAttr1.setValueId(2148618719L);
    varAttr1.setVariantId(2141546480L);
    varAttr1.setUsedFlag("Y");
    varAttr1.setPidcVersionId(1677573856L);

    // create pidc variant attr for variant AT_180KW
    PidcVariantAttribute varAttr2 = new PidcVariantAttribute();
    varAttr2.setAttrId(createdAttr.getId());
    varAttr2.setValueId(null);
    varAttr2.setVariantId(2176532078L);
    varAttr2.setUsedFlag("Y");
    varAttr2.setPidcVersionId(1677573856L);

    Map<Long, PidcVariantAttribute> varAttrMap1 = new HashMap<>();
    varAttrMap1.put(varAttr1.getAttrId(), varAttr1);

    Map<Long, PidcVariantAttribute> varAttrMap2 = new HashMap<>();
    varAttrMap2.put(varAttr2.getAttrId(), varAttr2);

    model.getPidcVarAttrsToBeCreated().put(2141546480L, varAttrMap1);
    model.getPidcVarAttrsToBeCreated().put(2176532078L, varAttrMap2);
    model.getPidcAttrsToBeCreated().clear();
    model.getPidcAttrsToBeUpdated().put(verAttrExisting.getAttrId(), verAttrExisting);

    ProjectAttributesUpdationModel modelAfterUpdation = servClient.updatePidcAttrs(model);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, modelAfterUpdation);


    // to update pidc variant attribute

    Map<Long, PidcVariantAttribute> varAttrMapToUpdate =
        modelAfterUpdation.getPidcVarAttrsToBeCreated().get(2141546480L);
    PidcVariantAttribute varAttrCreated = varAttrMapToUpdate.get(createdAttr.getId());
    varAttrCreated.setAtChildLevel(true);

    // to create subvariant attribute
    PidcSubVariantAttribute subVarAttrToCreate = new PidcSubVariantAttribute();
    subVarAttrToCreate.setVariantId(2141546480L);
    subVarAttrToCreate.setSubVariantId(2145525629L);
    subVarAttrToCreate.setAttrId(createdAttr.getId());
    subVarAttrToCreate.setValueId(null);
    subVarAttrToCreate.setUsedFlag(ApicConstants.CODE_YES);
    subVarAttrToCreate.setPidcVersionId(1677573856L);

    Map<Long, PidcSubVariantAttribute> subVarAttrMap = new HashMap<>();
    subVarAttrMap.put(createdAttr.getId(), subVarAttrToCreate);

    model.getPidcAttrsToBeUpdated().clear();
    model.getPidcVarAttrsToBeCreated().clear();
    model.getPidcVarAttrsToBeUpdated().put(2141546480L, varAttrMapToUpdate);
    model.getPidcVarsToBeUpdated().put(2141546480L, new PidcVariantServiceClient().get(2141546480L));
    model.getPidcSubVarAttrsToBeCreated().put(2145525629L, subVarAttrMap);
    // update variant attribute and create subvariant attribute
    ProjectAttributesUpdationModel modelAfterSubvarCreation = servClient.updatePidcAttrs(model);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, modelAfterSubvarCreation);


    Map<Long, PidcVariantAttribute> varAttrMapAfterUpdation =
        modelAfterSubvarCreation.getPidcVarAttrsToBeUpdated().get(2141546480L);

    // update subvariant attribute
    Map<Long, PidcSubVariantAttribute> subVarAttrMapToUpdate =
        modelAfterSubvarCreation.getPidcSubVarAttrsToBeCreated().get(2145525629L);
    PidcSubVariantAttribute subVarAttr = subVarAttrMapToUpdate.values().iterator().next();
    subVarAttr.setValueId(2173934228L);
    model.getPidcVarAttrsToBeUpdated().clear();
    model.getPidcVarsToBeUpdated().clear();
    model.getPidcSubVarAttrsToBeCreated().clear();
    model.getPidcSubVarAttrsToBeUpdated().put(2145525629L, subVarAttrMapToUpdate);
    model.getPidcSubVarsToBeUpdated().put(2145525629L, new PidcSubVariantServiceClient().get(2145525629L));

    ProjectAttributesUpdationModel modelAfterSubVarUpdation = servClient.updatePidcAttrs(model);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, modelAfterSubVarUpdation);

    // delete variant and subvariant attribute
    model.getPidcVariantAttributeDeletedMap().put(2141546480L, varAttrMapAfterUpdation);
    model.getPidcSubVariantAttributeDeletedMap().put(2145525629L,
        modelAfterSubVarUpdation.getPidcSubVarAttrsToBeUpdated().get(2145525629L));
    model.getPidcSubVarAttrsToBeUpdated().clear();
    model.getPidcSubVarsToBeUpdated().clear();
    ProjectAttributesUpdationModel modelAfterDeletion = servClient.updatePidcAttrs(model);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, modelAfterDeletion);

  }

  /**
   * Test for adding visible pidc version attributes
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testVisibileAttrs() throws ApicWebServiceException {

    ProjectAttributesUpdationServiceClient servClient = new ProjectAttributesUpdationServiceClient();

    ProjectAttributesUpdationModel model = new ProjectAttributesUpdationModel();
    PidcVersion pidcVersion = new PidcVersionServiceClient().getById(1677573856L);
    model.setPidcVersion(pidcVersion);
    // update version attribute with value 'PS-EC(formerly DGS_EC), so that some attributes become invsible
    PidcVersionAttribute versionAttribute =
        new PidcVersionAttributeServiceClient().getPidcVersionAttribute(1677573856L, 787372416L);
    versionAttribute.setValueId(787372417L);
    model.getPidcAttrsToBeUpdated().put(versionAttribute.getAttrId(), versionAttribute);
    ProjectAttributesUpdationModel modelAfterUpdation = servClient.updatePidcAttrs(model);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, modelAfterUpdation);
    // Invisible attributes for attribute 'iCDM Questionnaire Config' , if updated with attribute value 'PS-EC(formerly
    // DGS_EC) are 'EA-Number - CAL 1' , 'EA-Number - CAL 2' and 'EA-Number - SW 1'
    // validate visible attribute set
    Set<Long> visibleAttrSet = new HashSet<>(Arrays.asList(573L, 789794127L, 789794128L));
    LOG.info("Attributes added to visible attribute set:");
    for (PidcVersionAttribute versAttr : modelAfterUpdation.getPidcVersVisibleAttrSet()) {
      if (visibleAttrSet.contains(versAttr.getAttrId())) {
        LOG.info(ATTR_LOG_MSG, versAttr.getName());
      }
    }
  }

  /**
   * Test for adding invisible pidc version attributes
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testInvisibileAttrs() throws ApicWebServiceException {

    ProjectAttributesUpdationServiceClient servClient = new ProjectAttributesUpdationServiceClient();

    ProjectAttributesUpdationModel model = new ProjectAttributesUpdationModel();
    PidcVersion pidcVersion = new PidcVersionServiceClient().getById(1677573856L);
    model.setPidcVersion(pidcVersion);
    // update version attribute with null value , so that some attributes become invsible
    PidcVersionAttribute versionAttribute =
        new PidcVersionAttributeServiceClient().getPidcVersionAttribute(1677573856L, 787372416L);
    versionAttribute.setValueId(null);

    model.getPidcAttrsToBeUpdated().put(versionAttribute.getAttrId(), versionAttribute);
    ProjectAttributesUpdationModel modelAfterUpdation = servClient.updatePidcAttrs(model);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, modelAfterUpdation);

    // Invisible attributes for attribute 'iCDM Questionnaire Config' , if updated with attribute value 'PS-EC(formerly
    // DGS_EC) are 'EA-Number - CAL 1' , 'EA-Number - CAL 2' and 'EA-Number - SW 1'
    // validate invisible attribute set
    Set<Long> invisibleAttrSet = new HashSet<>(Arrays.asList(573L, 789794127L, 789794128L));
    LOG.info("Attributes added to invisible attribute set:");
    for (PidcVersionAttribute versAttr : modelAfterUpdation.getPidcVersInvisibleAttrSet()) {
      if (invisibleAttrSet.contains(versAttr.getAttrId())) {
        LOG.info(ATTR_LOG_MSG, versAttr.getName());
      }
    }
  }

  /**
   * Test for adding visible variant attributes
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testVisibileVarAttrs() throws ApicWebServiceException {

    ProjectAttributesUpdationServiceClient servClient = new ProjectAttributesUpdationServiceClient();

    ProjectAttributesUpdationModel model = new ProjectAttributesUpdationModel();
    PidcVersion pidcVersion = new PidcVersionServiceClient().getById(1677573856L);
    model.setPidcVersion(pidcVersion);
    // setting the attributes enabled by dep attribute as invisible before the test; to validate visibility of variant
    // attribute
    PidcVariantAttribute depVariantAttribute =
        new PidcVariantAttributeServiceClient().getVarAttrsForVariant(PIDC_VARIANT_ID).get(AC_ATTR_ID);
    // to update pidc variant attribute 'AC' - set used flag to 'NO' ,so that attribute 'AC Cooling Liquid' becomes
    // invisible

    depVariantAttribute.setUsedFlag(ApicConstants.CODE_NO);
    Map<Long, PidcVariantAttribute> depVarAttrMapToUpdate = new HashMap<>();
    depVarAttrMapToUpdate.put(depVariantAttribute.getAttrId(), depVariantAttribute);
    model.getPidcVarAttrsToBeUpdated().put(PIDC_VARIANT_ID, depVarAttrMapToUpdate);
    servClient.updatePidcAttrs(model);
    // to update pidc variant attribute 'AC' - set used flag to 'Yes' ,so that attribute 'AC Cooling Liquid' becomes
    // visible

    PidcVariantAttribute variantAttribute =
        new PidcVariantAttributeServiceClient().getVarAttrsForVariant(PIDC_VARIANT_ID).get(AC_ATTR_ID);
    // test whether attribute enabled by dependent attribute is getting added to visible map
    variantAttribute.setUsedFlag(ApicConstants.CODE_YES);
    Map<Long, PidcVariantAttribute> varAttrMapToUpdate = new HashMap<>();
    varAttrMapToUpdate.put(variantAttribute.getAttrId(), variantAttribute);

    model.getPidcVarAttrsToBeUpdated().put(PIDC_VARIANT_ID, varAttrMapToUpdate);
    ProjectAttributesUpdationModel modelAfterUpdation = servClient.updatePidcAttrs(model);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, modelAfterUpdation);

    // validate visible variant attribute map
    LOG.info(ADD_TO_VISIBLE_VARATTR_SET);
    for (PidcVariantAttribute varsAttr : modelAfterUpdation.getVariantVisbleAttributeMap().get(2141546480L)) {
      if (varsAttr.getAttrId() == 1022789066L) {
        LOG.info(ATTR_LOG_MSG, varsAttr.getName());
      }
    }
  }

  /**
   * Test for adding invisible variant attributes
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testInvisibileVarAttrs() throws ApicWebServiceException {

    ProjectAttributesUpdationServiceClient servClient = new ProjectAttributesUpdationServiceClient();

    ProjectAttributesUpdationModel model = new ProjectAttributesUpdationModel();
    PidcVersion pidcVersion = new PidcVersionServiceClient().getById(1677573856L);
    model.setPidcVersion(pidcVersion);

    // to update pidc variant attribute 'AC' - set used flag to 'No' ,so that attribute 'AC Cooling Liquid' becomes
    // invisible

    PidcVariantAttribute variantAttribute =
        new PidcVariantAttributeServiceClient().getVarAttrsForVariant(2141546480L).get(391L);
    variantAttribute.setUsedFlag(ApicConstants.CODE_NO);
    Map<Long, PidcVariantAttribute> varAttrMapToUpdate = new HashMap<>();
    varAttrMapToUpdate.put(variantAttribute.getAttrId(), variantAttribute);

    model.getPidcVarAttrsToBeUpdated().put(2141546480L, varAttrMapToUpdate);
    ProjectAttributesUpdationModel modelAfterUpdation = servClient.updatePidcAttrs(model);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, modelAfterUpdation);

    // validate invisible variant attribute set
    LOG.info("Attribute added to invisible variant attribute set:");
    for (PidcVariantAttribute varsAttr : modelAfterUpdation.getVariantInvisbleAttributeMap().get(2141546480L)) {
      if (varsAttr.getAttrId() == 1022789066L) {
        LOG.info(ATTR_LOG_MSG, varsAttr.getName());
      }
    }
  }

  /**
   * Test for adding visible subvariant attributes
   *
   * @throws ApicWebServiceException exception
   */
  // @Test
  public void testVisibileSubVarAttrs() throws ApicWebServiceException {

    ProjectAttributesUpdationServiceClient servClient = new ProjectAttributesUpdationServiceClient();

    ProjectAttributesUpdationModel model = new ProjectAttributesUpdationModel();
    PidcVersion pidcVersion = new PidcVersionServiceClient().getById(1677573856L);
    model.setPidcVersion(pidcVersion);

    // to update pidc subvariant attribute 'Legislation Character' - set used flag to 'Yes' ,so that following
    // attributes becomes visible
    // 'Legislation Cycle'
    // 'Legislation Engine'
    // 'Legislation Implementation date: new types'
    // 'Legislation Implementation date: new vehicles'
    // 'Legislation Last date of Registration'
    // 'Legislation Vehicle Category and Class'

    PidcSubVariantAttribute subvariantAttribute =
        new PidcSubVariantAttributeServiceClient().getSubVarAttrForSubVar(2145525629L).get(787613266L);
    subvariantAttribute.setUsedFlag(ApicConstants.CODE_YES);
    subvariantAttribute.setValueId(787913065L);
    Map<Long, PidcSubVariantAttribute> subvarAttrMapToUpdate = new HashMap<>();
    subvarAttrMapToUpdate.put(subvariantAttribute.getAttrId(), subvariantAttribute);

    model.getPidcSubVarAttrsToBeUpdated().put(2145525629L, subvarAttrMapToUpdate);
    model.getPidcSubVarsToBeUpdated().put(2145525629L, new PidcSubVariantServiceClient().get(2145525629L));
    ProjectAttributesUpdationModel modelAfterUpdation = servClient.updatePidcAttrs(model);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, modelAfterUpdation);

    // Invisible attributes for subvariant attribute 'Legislation Character' , if 'Used flag'-Yes
    Set<Long> visibleAttrSet =
        new HashSet<>(Arrays.asList(1420134667L, 1420134666L, 1420134670L, 1420134668L, 1420134671L, 1420134669L));
    // validate visible variant attribute map
    LOG.info(ADD_TO_VISIBLE_VARATTR_SET);
    for (PidcSubVariantAttribute subvarAttr : modelAfterUpdation.getSubVariantInvisbleAttributeMap().get(2145525629L)) {
      if (visibleAttrSet.contains(subvarAttr.getAttrId())) {
        LOG.info(ATTR_LOG_MSG, subvarAttr.getName());
      }
    }
  }

  /**
   * Test for adding invisible subvariant attributes
   *
   * @throws ApicWebServiceException exception
   */
  // @Test
  public void testInvisibileSubVarAttrs() throws ApicWebServiceException {

    ProjectAttributesUpdationServiceClient servClient = new ProjectAttributesUpdationServiceClient();

    ProjectAttributesUpdationModel model = new ProjectAttributesUpdationModel();
    PidcVersion pidcVersion = new PidcVersionServiceClient().getById(1677573856L);
    model.setPidcVersion(pidcVersion);

    // to update pidc subvariant attribute 'Legislation Character' - set used flag to 'Yes' ,so that following
    // attributes becomes visible
    // 'Legislation Cycle'
    // 'Legislation Engine'
    // 'Legislation Implementation date: new types'
    // 'Legislation Implementation date: new vehicles'
    // 'Legislation Last date of Registration'
    // 'Legislation Vehicle Category and Class'

    PidcSubVariantAttribute subvariantAttribute =
        new PidcSubVariantAttributeServiceClient().getSubVarAttrForSubVar(2145525629L).get(787613266L);
    subvariantAttribute.setUsedFlag(ApicConstants.CODE_NO);
    subvariantAttribute.setValueId(null);
    Map<Long, PidcSubVariantAttribute> subvarAttrMapToUpdate = new HashMap<>();
    subvarAttrMapToUpdate.put(subvariantAttribute.getAttrId(), subvariantAttribute);

    model.getPidcSubVarAttrsToBeUpdated().put(2145525629L, subvarAttrMapToUpdate);
    model.getPidcSubVarsToBeUpdated().put(2145525629L, new PidcSubVariantServiceClient().get(2145525629L));
    ProjectAttributesUpdationModel modelAfterUpdation = servClient.updatePidcAttrs(model);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, modelAfterUpdation);

    // Invisible attributes for subvariant attribute 'Legislation Character' , if 'Used flag'-Yes
    Set<Long> visibleAttrSet =
        new HashSet<>(Arrays.asList(1420134667L, 1420134666L, 1420134670L, 1420134668L, 1420134671L, 1420134669L));
    // validate visible variant attribute map
    LOG.info(ADD_TO_VISIBLE_VARATTR_SET);
    for (PidcSubVariantAttribute subvarAttr : modelAfterUpdation.getSubVariantVisbleAttributeMap().get(2145525629L)) {
      if (visibleAttrSet.contains(subvarAttr.getAttrId())) {
        LOG.info(ATTR_LOG_MSG, subvarAttr.getName());
      }
    }
  }


}

