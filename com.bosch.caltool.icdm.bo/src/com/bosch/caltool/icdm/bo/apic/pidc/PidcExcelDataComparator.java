/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.bo.user.ApicAccessRightLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcImportCompareData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcImportExcelData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectImportAttr;

/**
 * @author dja7cob Class to compare the excel import data with pidc
 */
public class PidcExcelDataComparator extends AbstractSimpleBusinessObject {

  /**
   * Change comment for invalid attribute edit
   */
  public static final String QNAIRE_CONFIG_ATTR_EDIT_NOT_ALLOWED =
      "This project has Review Questionnaire Response(s). Edit of attribute not allowed : ";
  /**
   * Change comment for deleted attribute
   */
  public static final String ATTR_NOT_FOUND = "Attribute not found in Existing PIDCard";

  /**
   * Change comment for invalid attribute edit
   */
  public static final String INVALID_MOD_EDIT_NOT_ALLOWED = "Invalid Modification(Edit not Allowed)";

  /**
   * Change comment for invalid attribute edit - value type mismatch
   */
  public static final String INVALID_MOD_VALUE_TYPE = "Invalid Modification(Edit not Allowed)";

  /**
   * Change comment for invalid spec link edit
   */
  public static final String INVALID_SPEC_EDIT_NOT_ALLOWED = "Invalid Modification(Spec Edit not Allowed)";

  /**
   * Change comment for attr for wich new value is added
   */
  public static final String NEWLY_ADDED_VALUE = "Newly Added Attribute Value";

  /**
   * Change comment for deleted / rejected attribute values
   */
  public static final String INVALID_MOD_DEL_REJ = "Attribute Value Deleted/Rejected";

  /**
   * Change comment for invalid used flag and value combination in excel
   */
  public static final String INVALID_USEDFLAG_VAL_COMBO = "Invalid Used-Flag,Value Combination";

  /**
   * Pidc version level attr comparison data
   */
  private final Map<Long, ProjectImportAttr<PidcVersionAttribute>> verAttrImportData = new HashMap<>();

  /**
   * Pidc version level attr comparison data -invalid attributes
   */
  private final Map<Long, ProjectImportAttr<PidcVersionAttribute>> invalidVerAttrImportData = new HashMap<>();
  /**
   * Pidc version level attr comparison data -valid attributes
   */
  private final Map<Long, ProjectImportAttr<PidcVersionAttribute>> validVerAttrImportData = new HashMap<>();

  /**
   * Pidc variant level attr comparison data
   */
  private final Map<Long, Map<Long, ProjectImportAttr<PidcVariantAttribute>>> varAttrImportData = new HashMap<>();

  /**
   * Pidc variant level attr comparison data -invalid attributes
   */
  private final Map<Long, Map<Long, ProjectImportAttr<PidcVariantAttribute>>> invalidVarAttrImportData =
      new HashMap<>();
  /**
   * Pidc variant level attr comparison data -valid attributes
   */
  private final Map<Long, Map<Long, ProjectImportAttr<PidcVariantAttribute>>> validVarAttrImportData = new HashMap<>();

  /**
   * Pidc variant level attr comparison data
   */
  private final Map<Long, Map<Long, ProjectImportAttr<PidcSubVariantAttribute>>> subvarAttrImportData = new HashMap<>();

  /**
   * Pidc variant level attr comparison data -invalid attributes
   */
  private final Map<Long, Map<Long, ProjectImportAttr<PidcSubVariantAttribute>>> invalidSubvarAttrImportData =
      new HashMap<>();
  /**
   * Pidc variant level attr comparison data -valid attributes
   */
  private final Map<Long, Map<Long, ProjectImportAttr<PidcSubVariantAttribute>>> validSubvarAttrImportData =
      new HashMap<>();

  private final ServiceData serviceData;

  private final PidcImportValidator pidcImportValidator;

  /**
   * @param serviceData instance
   */
  public PidcExcelDataComparator(final ServiceData serviceData) {
    super(serviceData);
    this.serviceData = serviceData;
    this.pidcImportValidator = new PidcImportValidator(serviceData);
  }

  /**
   * @param pidcImportExcelData excel input
   * @return PidcImportCompareData
   */
  public PidcImportCompareData compExcelDataWithPidc(final PidcImportExcelData pidcImportExcelData) {
    PidcImportCompareData pidcImportCompare = new PidcImportCompareData();
    PidcVersion selPidcVer = pidcImportExcelData.getPidcVersion();
    try {
      ProjectAttributeLoader pidcAttrLoader = new ProjectAttributeLoader(this.serviceData);
      // load the project attribute model
      PidcVersionAttributeModel pidcVerAttrModel = pidcAttrLoader.createModel(selPidcVer.getId(), LOAD_LEVEL.L6_ALL);
      compareVersionLevelAttr(pidcVerAttrModel.getPidcVersAttrMap(), pidcImportExcelData.getPidcImportAttrMap(),
          selPidcVer);
      PidcExcelVarDataComparator varSubvarcomparator = new PidcExcelVarDataComparator(this, this.serviceData);
      varSubvarcomparator.compareVariantLevelAttr(pidcVerAttrModel.getAllVariantAttributeMap(),
          pidcImportExcelData.getVarImportAttrMap());
      varSubvarcomparator.compareSubvariantLevelAttr(pidcVerAttrModel, pidcImportExcelData.getSubvarImportAttrMap());
      setCompareData(pidcImportCompare);
    }
    catch (IcdmException exp) {
      getLogger().error(exp.getMessage(), exp);
    }
    return pidcImportCompare;
  }

  /**
   * @param pidcImportCompare
   */
  private void setCompareData(final PidcImportCompareData pidcImportCompare) {
    pidcImportCompare.setVerAttrImportData(this.verAttrImportData);
    pidcImportCompare.setVarAttrImportData(this.varAttrImportData);
    pidcImportCompare.setSubvarAttrImportData(this.subvarAttrImportData);
  }

  /**
   * @param pidcVersAttrMap
   * @param pidcImportAttrMap
   * @param selPidcVer
   * @throws IcdmException
   * @throws InvalidInputException
   */
  private void compareVersionLevelAttr(final Map<Long, PidcVersionAttribute> pidcVersAttrMap,
      final Map<Long, PidcVersionAttribute> pidcImportAttrMap, final PidcVersion selPidcVer)
      throws IcdmException {
    AttributeValueLoader attrvalLoader = new AttributeValueLoader(getServiceData());
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    for (Entry<Long, PidcVersionAttribute> excelPidcVerAttr : pidcImportAttrMap.entrySet()) {

      PidcVersionAttribute pidcVerAttr = pidcVersAttrMap.get(excelPidcVerAttr.getKey());
      if (null != pidcVerAttr) {
        if (null != pidcVerAttr.getValueId()) {
          pidcVerAttr.setValue(attrvalLoader.getDataObjectByID(pidcVerAttr.getValueId()).getNameRaw());
        }
        if (this.pidcImportValidator.isAttrFieldChanged(excelPidcVerAttr.getValue(), pidcVerAttr)) {
          compVerAttr(excelPidcVerAttr, pidcVerAttr);
        }
      }
      else {

        if ((null != excelPidcVerAttr.getValue().getAttrId()) &&
            (null != attrLoader.getDataObjectByID(excelPidcVerAttr.getValue().getAttrId())) &&
            (null != excelPidcVerAttr.getValue().getUsedFlag()) &&
            !excelPidcVerAttr.getValue().getUsedFlag().equals(PROJ_ATTR_USED_FLAG.NEW_ATTR.getDbType())) {

          ProjectImportAttr<PidcVersionAttribute> pidcVerImpAttr = new ProjectImportAttr<>();
          pidcVerImpAttr.setAttr(attrLoader.getDataObjectByID(excelPidcVerAttr.getValue().getAttrId()));
          pidcVerImpAttr.setExcelAttr(excelPidcVerAttr.getValue());

          PidcVersionAttribute compAttr = excelPidcVerAttr.getValue().clone();
          compAttr.setUsedFlag(excelPidcVerAttr.getValue().getUsedFlag());
          compAttr.setValue(excelPidcVerAttr.getValue().getValue());
          compAttr.setPartNumber(excelPidcVerAttr.getValue().getPartNumber());
          compAttr.setSpecLink(excelPidcVerAttr.getValue().getSpecLink());
          compAttr.setAdditionalInfoDesc(excelPidcVerAttr.getValue().getAdditionalInfoDesc());
          pidcVerImpAttr.setCompareAttr(compAttr);
          pidcVerImpAttr.getCompareAttr()
              .setName(attrLoader.getDataObjectByID(excelPidcVerAttr.getValue().getAttrId()).getName());
          pidcVerImpAttr.getCompareAttr()
              .setDescription(attrLoader.getDataObjectByID(excelPidcVerAttr.getValue().getAttrId()).getDescription());
          pidcVerImpAttr.getCompareAttr().setPidcVersId(selPidcVer.getId());
          pidcVerImpAttr.getCompareAttr().setAtChildLevel(false);
          pidcVerImpAttr.getCompareAttr().setAttrHidden(false);
          pidcVerImpAttr.getCompareAttr().setTransferToVcdm(false);
          pidcVerImpAttr.getCompareAttr().setFocusMatrixApplicable(false);

          PidcVersionAttribute pidcAttr = pidcVerImpAttr.getCompareAttr().clone();
          pidcVerImpAttr.setPidcAttr(pidcAttr);
          pidcVerImpAttr.getPidcAttr()
              .setName(attrLoader.getDataObjectByID(excelPidcVerAttr.getValue().getAttrId()).getName());
          pidcVerImpAttr.getPidcAttr()
              .setDescription(attrLoader.getDataObjectByID(excelPidcVerAttr.getValue().getAttrId()).getDescription());
          pidcVerImpAttr.getPidcAttr().setPidcVersId(selPidcVer.getId());
          pidcVerImpAttr.getPidcAttr().setAtChildLevel(false);
          pidcVerImpAttr.getPidcAttr().setAttrHidden(false);
          pidcVerImpAttr.getPidcAttr().setTransferToVcdm(false);
          pidcVerImpAttr.getPidcAttr().setFocusMatrixApplicable(false);
          pidcVerImpAttr.getPidcAttr().setUsedFlag(PROJ_ATTR_USED_FLAG.NEW_ATTR.getDbType());
          pidcVerImpAttr.getPidcAttr().setValue(null);

          pidcVerImpAttr.setCreateAttr(true);
          pidcVerImpAttr.setNewlyAddedVal(null != excelPidcVerAttr.getValue().getValue());
          this.verAttrImportData.put(excelPidcVerAttr.getValue().getAttrId(), pidcVerImpAttr);
        }
        else {
          ProjectImportAttr<PidcVersionAttribute> pidcVerImpAttr = new ProjectImportAttr<>();
          pidcVerImpAttr.setExcelAttr(excelPidcVerAttr.getValue());
          pidcVerImpAttr.setPidcAttr(null);
          setImportValidity(pidcVerImpAttr, false, ATTR_NOT_FOUND);
        }
      }
    }
  }


  /**
   * @param excelPidcVerAttr
   * @param pidcVersionAttribute
   * @throws IcdmException
   * @throws InvalidInputException
   */
  private void compVerAttr(final Entry<Long, PidcVersionAttribute> excelPidcVerAttr,
      final PidcVersionAttribute pidcVersionAttribute)
      throws IcdmException {
    ProjectImportAttr<PidcVersionAttribute> pidcVerImpAttr = new ProjectImportAttr<>();
    if (!isValidAttrModification(excelPidcVerAttr.getValue(), pidcVersionAttribute, pidcVerImpAttr)) {
      this.invalidVerAttrImportData.put(excelPidcVerAttr.getKey(), pidcVerImpAttr);
    }
    else {
      this.validVerAttrImportData.put(excelPidcVerAttr.getKey(), pidcVerImpAttr);
    }
    this.verAttrImportData.put(excelPidcVerAttr.getKey(), pidcVerImpAttr);
  }

  /**
   * @param excelAttr
   * @param pidcAttr
   * @param pidcVerImpAttr
   * @return
   * @throws IcdmException
   * @throws InvalidInputException
   */
  private boolean isValidAttrModification(final PidcVersionAttribute excelAttr, final PidcVersionAttribute pidcAttr,
      final ProjectImportAttr<PidcVersionAttribute> pidcVerImpAttr)
      throws IcdmException {
    pidcVerImpAttr.setExcelAttr(excelAttr);
    pidcVerImpAttr.setPidcAttr(pidcAttr);
    PidcVersionAttribute compAttr = pidcAttr.clone();
    CommonUtils.shallowCopy(compAttr, pidcAttr);
    setExcelFieldsForCompAttr(compAttr, excelAttr);
    pidcVerImpAttr.setCompareAttr(compAttr);

    // Get the attribute object
    Attribute attr = getAttrObj(pidcAttr);
    if (null != attr) {
      pidcVerImpAttr.setAttr(attr);
      // Validate selected pidc version
      if (!isSelPidcValid(pidcVerImpAttr, attr, pidcAttr)) {
        return false;
      }
      // Validate imported excel file
      if (!isExcelValid(pidcVerImpAttr, excelAttr, pidcAttr, attr)) {
        return false;
      }
      setImportDataFields(excelAttr, pidcVerImpAttr, attr);
      return true;

    }
    return false;

  }

  /**
   * @param compAttr
   * @param excelAttr
   */
  private void setExcelFieldsForCompAttr(final PidcVersionAttribute compAttr, final PidcVersionAttribute excelAttr) {
    compAttr.setUsedFlag(excelAttr.getUsedFlag());
    compAttr.setValue(excelAttr.getValue());
    compAttr.setPartNumber(excelAttr.getPartNumber());
    compAttr.setSpecLink(excelAttr.getSpecLink());
    compAttr.setAdditionalInfoDesc(excelAttr.getAdditionalInfoDesc());
  }

  /**
   * @param pidcVerImpAttr
   * @param excelAttr
   * @param pidcAttr
   * @param attr
   * @return
   */
  private boolean isExcelValid(final ProjectImportAttr<PidcVersionAttribute> pidcVerImpAttr,
      final PidcVersionAttribute excelAttr, final PidcVersionAttribute pidcAttr, final Attribute attr) {
    // Check whether spec link of the attribute is modified and is allowed
    if (this.pidcImportValidator.isInvalidSpecLinkEdit(excelAttr, pidcAttr)) {
      setImportValidity(pidcVerImpAttr, false, INVALID_SPEC_EDIT_NOT_ALLOWED);
      return false;
    }

    // Check whether the value type in excel is same as in pidc
    if (this.pidcImportValidator.isValueTypeValid(excelAttr, attr)) {
      if (!this.pidcImportValidator.isModifiedValueValid(excelAttr, attr)) {
        setImportValidity(pidcVerImpAttr, false, INVALID_MOD_EDIT_NOT_ALLOWED);
        return false;
      }
    }
    else {
      setImportValidity(pidcVerImpAttr, false, INVALID_MOD_VALUE_TYPE);
      return false;
    }

    // Check whether the attr value is deleted or rejected
    if (this.pidcImportValidator.isAttrValDelRejected(pidcAttr.getValueId())) {
      setImportValidity(pidcVerImpAttr, false, INVALID_MOD_DEL_REJ);
      return false;
    }

    // Check whether used flag value combination is valid
    if (this.pidcImportValidator.isUsedFlagValComboInValid(excelAttr, pidcAttr, attr)) {
      setImportValidity(pidcVerImpAttr, false, INVALID_USEDFLAG_VAL_COMBO);
      pidcVerImpAttr.setNewlyAddedVal(false);
      return false;
    }

    setAttrCreationStatus(excelAttr, pidcAttr, pidcVerImpAttr);
    return true;
  }

  /**
   * @param excelAttr excel attribute
   * @param pidcAttr pidc version attribute
   * @param pidcVerImpAttr attr imported
   */
  public void setAttrCreationStatus(final PidcVersionAttribute excelAttr, final PidcVersionAttribute pidcAttr,
      final ProjectImportAttr<PidcVersionAttribute> pidcVerImpAttr) {
    if (pidcAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getDbType()) &&
        !excelAttr.getUsedFlag().equals(pidcAttr.getUsedFlag())) {
      pidcVerImpAttr.setCreateAttr(true);
    }
  }

  /**
   * @param pidcVerImpAttr
   * @param pidcAttr
   * @param attr
   * @return
   * @throws IcdmException
   */
  private boolean isSelPidcValid(final ProjectImportAttr<PidcVersionAttribute> pidcVerImpAttr, final Attribute attr,
      final PidcVersionAttribute pidcAttr)
      throws IcdmException {
    // Check whether the attribute is deleted in pidc
    if (attr.isDeleted()) {
      setImportValidity(pidcVerImpAttr, false,
          QNAIRE_CONFIG_ATTR_EDIT_NOT_ALLOWED + new AttributeLoader(this.serviceData).getQnaireConfigAttr().getName());
      return false;
    }

    // Check whether the attribute is a division attribute and its value is used in review
    if (this.pidcImportValidator.isQnaireConfigAttr(pidcAttr) && this.pidcImportValidator.isQnaireConfigAttrUsedInRvw(pidcAttr)) {
      setImportValidity(pidcVerImpAttr, false,
          QNAIRE_CONFIG_ATTR_EDIT_NOT_ALLOWED + new AttributeLoader(this.serviceData).getQnaireConfigAttr().getName());
      return false;
    }

    // Check whether the user is allowed to edit the attribute
    if (this.pidcImportValidator.isAttrEditNotAllowed(pidcAttr)) {
      setImportValidity(pidcVerImpAttr, false, INVALID_MOD_EDIT_NOT_ALLOWED);
      return false;
    }
    return true;
  }

  /**
   * @param pidcVerImpAttr
   * @param comment
   * @param isValid
   */
  private void setImportValidity(final ProjectImportAttr<PidcVersionAttribute> pidcVerImpAttr, final boolean isValid,
      final String comment) {
    pidcVerImpAttr.setValidImport(isValid);
    pidcVerImpAttr.setComment(comment);
  }

  /**
   * @param excelAttr
   * @param pidcVerImpAttr
   * @param attr
   * @throws DataException
   * @throws InvalidInputException
   */
  private void setImportDataFields(final PidcVersionAttribute excelAttr,
      final ProjectImportAttr<PidcVersionAttribute> pidcVerImpAttr, final Attribute attr)
      throws DataException {
    pidcVerImpAttr.setValidImport(true);

    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    if ((null != excelAttr.getValue()) && !excelAttr.getValue().isEmpty()) {
      List<AttributeValue> matchedValList = new ArrayList<>();

      attrValLoader.getAttrValues(attr.getId()).values().forEach(val -> {
        if (val.getNameRaw().trim().equals(excelAttr.getValue().trim())) {
          matchedValList.add(val);
        }
      });

      if (!matchedValList.isEmpty()) {
        setDataForExistingAttrVal(pidcVerImpAttr, matchedValList);
      }
      else {
        setDataForNewAttrVal(pidcVerImpAttr);
      }
    }
    else {
      pidcVerImpAttr.setNewlyAddedVal(false);
    }
  }

  /**
   * @param pidcVerImpAttr
   * @param matchedValList
   */
  private void setDataForExistingAttrVal(final ProjectImportAttr<PidcVersionAttribute> pidcVerImpAttr,
      final List<AttributeValue> matchedValList) {
    pidcVerImpAttr.setNewlyAddedVal(false);
    pidcVerImpAttr.getCompareAttr().setValueId(matchedValList.get(0).getId());
    pidcVerImpAttr.setCleared((null != matchedValList.get(0).getClearingStatus()) &&
        matchedValList.get(0).getClearingStatus().equals(ApicConstants.CODE_YES));
  }

  /**
   * @param pidcVerImpAttr
   * @throws DataException
   */
  private void setDataForNewAttrVal(final ProjectImportAttr<PidcVersionAttribute> pidcVerImpAttr) throws DataException {
    pidcVerImpAttr.setNewlyAddedVal(true);
    pidcVerImpAttr.setComment(NEWLY_ADDED_VALUE);
    ApicAccessRightLoader apicAccessLoader = new ApicAccessRightLoader(getServiceData());
    pidcVerImpAttr.setCleared(apicAccessLoader.getAccessRightsCurrentUser().isApicWrite());
  }


  /**
   * @param pidcAttr
   * @return
   */
  private Attribute getAttrObj(final PidcVersionAttribute pidcAttr) {
    AttributeLoader attrLoader = new AttributeLoader(this.serviceData);
    try {
      return attrLoader.getDataObjectByID(pidcAttr.getAttrId());
    }
    catch (DataException e) {
      getLogger().error(e.getMessage(), e);
    }
    return null;
  }


  /**
   * @return the verAttrImportData
   */
  public Map<Long, ProjectImportAttr<PidcVersionAttribute>> getVerAttrImportData() {
    return this.verAttrImportData;
  }

  /**
   * @return the invalidVerAttrImportData
   */
  public Map<Long, ProjectImportAttr<PidcVersionAttribute>> getInvalidVerAttrImportData() {
    return this.invalidVerAttrImportData;
  }

  /**
   * @return the validVerAttrImportData
   */
  public Map<Long, ProjectImportAttr<PidcVersionAttribute>> getValidVerAttrImportData() {
    return this.validVerAttrImportData;
  }

  /**
   * @return the varAttrImportData
   */
  public Map<Long, Map<Long, ProjectImportAttr<PidcVariantAttribute>>> getVarAttrImportData() {
    return this.varAttrImportData;
  }

  /**
   * @return the invalidVarAttrImportData
   */
  public Map<Long, Map<Long, ProjectImportAttr<PidcVariantAttribute>>> getInvalidVarAttrImportData() {
    return this.invalidVarAttrImportData;
  }

  /**
   * @return the validVarAttrImportData
   */
  public Map<Long, Map<Long, ProjectImportAttr<PidcVariantAttribute>>> getValidVarAttrImportData() {
    return this.validVarAttrImportData;
  }

  /**
   * @return the pidcImportValidator
   */
  public PidcImportValidator getPidcImportValidator() {
    return this.pidcImportValidator;
  }

  /**
   * @return the validSubvarAttrImportData
   */
  public Map<Long, Map<Long, ProjectImportAttr<PidcSubVariantAttribute>>> getValidSubvarAttrImportData() {
    return this.validSubvarAttrImportData;
  }

  /**
   * @return the suvvarAttrImportData
   */
  public Map<Long, Map<Long, ProjectImportAttr<PidcSubVariantAttribute>>> getSubvarAttrImportData() {
    return this.subvarAttrImportData;
  }

  /**
   * @return the invalidSubvarAttrImportData
   */
  public Map<Long, Map<Long, ProjectImportAttr<PidcSubVariantAttribute>>> getInvalidSubvarAttrImportData() {
    return this.invalidSubvarAttrImportData;
  }
}
