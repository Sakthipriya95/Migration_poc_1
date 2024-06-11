/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.user.ApicAccessRightLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectImportAttr;

/**
 * @author dja7cob
 */
public class PidcExcelVarDataComparator extends AbstractSimpleBusinessObject {

  /**
   * pidcExcelDataComparator
   */
  private final PidcExcelDataComparator pidcExcelDataComparator;

  /**
   * Pidc excel import validator
   */
  private final PidcImportValidator pidcImportValidator;

  /**
   * @param pidcExcelDataComparator instance
   * @param serviceData instance
   */
  public PidcExcelVarDataComparator(final PidcExcelDataComparator pidcExcelDataComparator,
      final ServiceData serviceData) {
    super(serviceData);
    this.pidcExcelDataComparator = pidcExcelDataComparator;
    this.pidcImportValidator = new PidcImportValidator(serviceData);
  }

  /**
   * @param pidcVarAttrMap variant attribute map in existing pidc
   * @param excelVarAttrMap variant attribute map from imported excel
   * @throws DataException Exception
   */
  public void compareVariantLevelAttr(final Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrMap,
      final Map<Long, Map<Long, PidcVariantAttribute>> excelVarAttrMap)
      throws DataException {

    Map<Long, Long> nameValIdVarIdMap =
        new PidcVariantLoader(getServiceData()).getVarIdsForNameValIds(pidcVarAttrMap.keySet());

    excelVarAttrMap.entrySet().stream().forEach(entry -> {
      if (null != nameValIdVarIdMap.get(entry.getKey())) {
        compareVarAttrs(entry.getValue(), pidcVarAttrMap.get(nameValIdVarIdMap.get(entry.getKey())));
      }
    });

  }

  /**
   * @param pidcVerAttrModel
   * @param excelSubvarAttrMap
   * @param map2
   * @param map
   * @throws DataException
   */
  public void compareSubvariantLevelAttr(final PidcVersionAttributeModel pidcVerAttrModel,
      final Map<Long, Map<Long, Map<Long, PidcSubVariantAttribute>>> excelSubvarAttrMap)
      throws DataException {
    Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubVariantAttrMap = pidcVerAttrModel.getAllSubVariantAttrMap();
    Map<Long, PidcVariant> variantsMap = pidcVerAttrModel.getVariantMap();
    Map<Long, Long> varNameValIdMap =
        new PidcVariantLoader(getServiceData()).getVarIdsForNameValIds(variantsMap.keySet());

    excelSubvarAttrMap.entrySet().stream().forEach(entry -> {
      if (null != varNameValIdMap.get(entry.getKey())) {
        Map<Long, Long> subvarNameValIdMap;
        try {
          subvarNameValIdMap = new PidcSubVariantLoader(getServiceData())
              .getVarIdsForNameValIds(pidcVerAttrModel.getSubVariantIdSet(varNameValIdMap.get(entry.getKey())));
          entry.getValue().entrySet().stream().forEach(entrySubvar -> {
            if (null != subvarNameValIdMap.get(entrySubvar.getKey())) {
              compareSubvarAttrs(entrySubvar.getValue(),
                  pidcSubVariantAttrMap.get(subvarNameValIdMap.get(entrySubvar.getKey())));
            }
          });
        }
        catch (DataException e) {
          getLogger().error(e.getMessage(), e);
        }
      }
    });


  }

  /**
   * @param value
   * @param map
   */
  private void compareSubvarAttrs(final Map<Long, PidcSubVariantAttribute> excelSubvarAttr,
      final Map<Long, PidcSubVariantAttribute> pidcSubvarAttr) {
    AttributeValueLoader valLoader = new AttributeValueLoader(getServiceData());
    excelSubvarAttr.entrySet().stream().forEach(excelSubvarEntry -> {
      PidcSubVariantAttribute pidcSubVariantAttribute = pidcSubvarAttr.get(excelSubvarEntry.getKey());
      if (CommonUtils.isNotNull(pidcSubVariantAttribute)) {
        compareSubvarAttribute(valLoader, excelSubvarEntry, pidcSubVariantAttribute);
      }
      else {
        ProjectImportAttr<PidcSubVariantAttribute> pidcVarImpAttr = new ProjectImportAttr<>();
        pidcVarImpAttr.setExcelAttr(excelSubvarEntry.getValue());
        pidcVarImpAttr.setPidcAttr(null);
        setSubvarAttrImportValidity(pidcVarImpAttr, false, PidcExcelDataComparator.ATTR_NOT_FOUND);
      }
    });
  }

  /**
   * @param valLoader
   * @param excelSubvarEntry
   * @param pidcSubVariantAttribute
   */
  private void compareSubvarAttribute(final AttributeValueLoader valLoader,
      final Entry<Long, PidcSubVariantAttribute> excelSubvarEntry,
      final PidcSubVariantAttribute pidcSubVariantAttribute) {
    try {
      if (CommonUtils.isNotNull(pidcSubVariantAttribute.getValueId())) {
        pidcSubVariantAttribute
            .setValue(valLoader.getDataObjectByID(pidcSubVariantAttribute.getValueId()).getNameRaw());
      }
      if (this.pidcImportValidator.isAttrFieldChanged(excelSubvarEntry.getValue(), pidcSubVariantAttribute)) {
        compareSubvarAttr(excelSubvarEntry.getValue(), pidcSubVariantAttribute);
      }
    }
    catch (IcdmException e) {
      getLogger().error(e.getMessage(), e);
    }
  }


  /**
   * @param value
   * @param pidcSubVariantAttribute
   * @throws IcdmException
   * @throws InvalidInputException
   */
  private void compareSubvarAttr(final PidcSubVariantAttribute excelSubvarAttr,
      final PidcSubVariantAttribute pidcSubVarAttr)
      throws IcdmException {
    ProjectImportAttr<PidcSubVariantAttribute> pidcSubvarImpAttr = new ProjectImportAttr<>();
    if (!isValidSubvarAttrModification(excelSubvarAttr, pidcSubVarAttr, pidcSubvarImpAttr)) {
      addAttrToSubvarMap(pidcSubVarAttr, pidcSubvarImpAttr,
          this.pidcExcelDataComparator.getInvalidSubvarAttrImportData());
    }
    else {
      addAttrToSubvarMap(pidcSubVarAttr, pidcSubvarImpAttr,
          this.pidcExcelDataComparator.getValidSubvarAttrImportData());
    }
    addAttrToSubvarMap(pidcSubVarAttr, pidcSubvarImpAttr, this.pidcExcelDataComparator.getSubvarAttrImportData());
  }

  /**
   * @param excelSubvarAttr
   * @param pidcSubVarAttr
   * @param pidcVarImpAttr
   * @return
   * @throws IcdmException
   * @throws InvalidInputException
   */
  private boolean isValidSubvarAttrModification(final PidcSubVariantAttribute excelSubvarAttr,
      final PidcSubVariantAttribute pidcSubVarAttr, final ProjectImportAttr<PidcSubVariantAttribute> pidcVarImpAttr)
      throws IcdmException {
    pidcVarImpAttr.setExcelAttr(excelSubvarAttr);
    pidcVarImpAttr.setPidcAttr(pidcSubVarAttr);
    PidcSubVariantAttribute compAttr = pidcSubVarAttr.clone();
    CommonUtils.shallowCopy(compAttr, pidcSubVarAttr);
    setExcelFieldsForCompAttr(compAttr, excelSubvarAttr);
    pidcVarImpAttr.setCompareAttr(compAttr);

    // Get the attribute object
    Attribute attr = getSubvarAttrObj(pidcSubVarAttr);
    if (null != attr) {
      pidcVarImpAttr.setAttr(attr);

      // Validate selected pidc version
      if (!isSelPidcSubVarValid(pidcVarImpAttr, attr, pidcSubVarAttr)) {
        return false;
      }
      // Validate imported excel file
      if (!isExcelSubvarValid(pidcVarImpAttr, excelSubvarAttr, pidcSubVarAttr, attr)) {
        return false;
      }
      setImportDataFields(excelSubvarAttr, pidcVarImpAttr, attr);
      return true;
    }
    return false;

  }

  /**
   * @param compAttr
   * @param excelSubvarAttr
   */
  private void setExcelFieldsForCompAttr(final PidcSubVariantAttribute compAttr,
      final PidcSubVariantAttribute excelSubvarAttr) {
    compAttr.setUsedFlag(excelSubvarAttr.getUsedFlag());
    compAttr.setValue(excelSubvarAttr.getValue());
    compAttr.setPartNumber(excelSubvarAttr.getPartNumber());
    compAttr.setSpecLink(excelSubvarAttr.getSpecLink());
    compAttr.setAdditionalInfoDesc(excelSubvarAttr.getAdditionalInfoDesc());
  }

  /**
   * @param excelSubvarAttr
   * @param pidcVarImpAttr
   * @param attr
   * @throws DataException
   * @throws InvalidInputException
   */
  private void setImportDataFields(final PidcSubVariantAttribute excelSubvarAttr,
      final ProjectImportAttr<PidcSubVariantAttribute> pidcVarImpAttr, final Attribute attr)
      throws DataException {
    pidcVarImpAttr.setValidImport(true);

    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    if (null != excelSubvarAttr.getValue()) {
      List<AttributeValue> matchedValList = attrValLoader.getAttrValues(attr.getId()).values().stream()
          .filter(val -> val.getNameRaw().trim().equals(excelSubvarAttr.getValue().trim()))
          .collect(Collectors.toList());
      if ((null != matchedValList) && !matchedValList.isEmpty()) {
        pidcVarImpAttr.setNewlyAddedVal(false);
        pidcVarImpAttr.getCompareAttr().setValueId(matchedValList.get(0).getId());
        pidcVarImpAttr.setCleared((null != matchedValList.get(0).getClearingStatus()) &&
            matchedValList.get(0).getClearingStatus().equals(ApicConstants.CODE_YES));
      }
      else {
        pidcVarImpAttr.setNewlyAddedVal(true);
        ApicAccessRightLoader apicAccessLoader = new ApicAccessRightLoader(getServiceData());
        pidcVarImpAttr.setCleared(apicAccessLoader.getAccessRightsCurrentUser().isApicWrite());
        pidcVarImpAttr.setComment(PidcExcelDataComparator.NEWLY_ADDED_VALUE);
      }
    }
    else {
      pidcVarImpAttr.setNewlyAddedVal(false);
    }
  }

  /**
   * @param pidcSubVarImpAttr
   * @param excelSubvarAttr
   * @param pidcSubVarAttr
   * @param attr
   * @return
   */
  private boolean isExcelSubvarValid(final ProjectImportAttr<PidcSubVariantAttribute> pidcSubVarImpAttr,
      final PidcSubVariantAttribute excelSubvarAttr, final PidcSubVariantAttribute pidcSubVarAttr,
      final Attribute attr) {
    // Check whether spec link of the attribute is modified and is allowed
    if (this.pidcImportValidator.isInvalidSpecLinkEdit(excelSubvarAttr, pidcSubVarAttr)) {
      setSubvarAttrImportValidity(pidcSubVarImpAttr, false, PidcExcelDataComparator.INVALID_SPEC_EDIT_NOT_ALLOWED);
      return false;
    }

    // Check whether the value type in excel is same as in pidc
    if (this.pidcImportValidator.isValueTypeValid(excelSubvarAttr, attr)) {
      if (!this.pidcImportValidator.isModifiedValueValid(excelSubvarAttr, attr)) {
        setSubvarAttrImportValidity(pidcSubVarImpAttr, false, PidcExcelDataComparator.INVALID_MOD_EDIT_NOT_ALLOWED);
        return false;
      }
    }
    else {
      setSubvarAttrImportValidity(pidcSubVarImpAttr, false, PidcExcelDataComparator.INVALID_MOD_VALUE_TYPE);
      return false;
    }

    // Check whether the attr value is deleted or rejected
    if (this.pidcImportValidator.isAttrValDelRejected(pidcSubVarAttr.getValueId())) {
      setSubvarAttrImportValidity(pidcSubVarImpAttr, false, PidcExcelDataComparator.INVALID_MOD_DEL_REJ);
      return false;
    }

    // Check whether used flag value combination is valid
    if (this.pidcImportValidator.isUsedFlagValComboInValid(excelSubvarAttr, pidcSubVarAttr, attr)) {
      setSubvarAttrImportValidity(pidcSubVarImpAttr, false, PidcExcelDataComparator.INVALID_USEDFLAG_VAL_COMBO);
      pidcSubVarImpAttr.setNewlyAddedVal(false);
      return false;
    }
    setAttrCreationStatus(excelSubvarAttr, pidcSubVarAttr, pidcSubVarImpAttr);
    return true;
  }

  /**
   * @param pidcVarImpAttr
   * @param attr
   * @param pidcSubVarAttr
   * @return
   * @throws IcdmException
   */
  private boolean isSelPidcSubVarValid(final ProjectImportAttr<PidcSubVariantAttribute> pidcSubvarImpAttr,
      final Attribute attr, final PidcSubVariantAttribute pidcSubVarAttr)
      throws IcdmException {
    // Check whether the attribute is deleted in pidc
    if (attr.isDeleted()) {
      setSubvarAttrImportValidity(pidcSubvarImpAttr, false,
          PidcExcelDataComparator.QNAIRE_CONFIG_ATTR_EDIT_NOT_ALLOWED +
              new AttributeLoader(getServiceData()).getQnaireConfigAttr().getName());
      return false;
    }

    // Check whether the attribute is a division attribute and its value is used in review
    if (this.pidcImportValidator.isQnaireConfigAttr(pidcSubVarAttr) &&
        this.pidcImportValidator.isQnaireConfigAttrUsedInRvw(pidcSubVarAttr)) {
      setSubvarAttrImportValidity(pidcSubvarImpAttr, false,
          PidcExcelDataComparator.QNAIRE_CONFIG_ATTR_EDIT_NOT_ALLOWED +
              new AttributeLoader(getServiceData()).getQnaireConfigAttr().getName());
      return false;
    }

    // Check whether the user is allowed to edit the attribute
    if (this.pidcImportValidator.isAttrEditNotAllowed(pidcSubVarAttr)) {
      setSubvarAttrImportValidity(pidcSubvarImpAttr, false, PidcExcelDataComparator.INVALID_MOD_EDIT_NOT_ALLOWED);
      return false;
    }
    return true;
  }

  /**
   * @param pidcSubVarAttr
   * @return
   */
  private Attribute getSubvarAttrObj(final PidcSubVariantAttribute pidcSubVarAttr) {
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    try {
      return attrLoader.getDataObjectByID(pidcSubVarAttr.getAttrId());
    }
    catch (DataException e) {
      getLogger().error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * @param pidcSubVarAttr
   * @param pidcVarImpAttr
   * @param invalidSubvarAttrImportData
   */
  private void addAttrToSubvarMap(final PidcSubVariantAttribute pidcSubVarAttr,
      final ProjectImportAttr<PidcSubVariantAttribute> pidcSubvarImpAttr,
      final Map<Long, Map<Long, ProjectImportAttr<PidcSubVariantAttribute>>> importMap) {
    if (importMap.containsKey(pidcSubVarAttr.getSubVariantId())) {
      importMap.get(pidcSubVarAttr.getSubVariantId()).put(pidcSubVarAttr.getAttrId(), pidcSubvarImpAttr);
    }
    else {
      Map<Long, ProjectImportAttr<PidcSubVariantAttribute>> varImpAttrMap = new HashMap<>();
      varImpAttrMap.put(pidcSubVarAttr.getAttrId(), pidcSubvarImpAttr);
      importMap.put(pidcSubVarAttr.getSubVariantId(), varImpAttrMap);
    }
  }

  /**
   * @param pidcVarImpAttr
   * @param b
   * @param attrNotFound
   */
  private void setSubvarAttrImportValidity(final ProjectImportAttr<PidcSubVariantAttribute> pidcSubvarImpAttr,
      final boolean isValid, final String comment) {
    pidcSubvarImpAttr.setValidImport(isValid);
    pidcSubvarImpAttr.setComment(comment);
  }

  /**
   * @param excelVarAttrMap
   * @param pidcVarAttrMap
   */
  private void compareVarAttrs(final Map<Long, PidcVariantAttribute> excelVarAttrMap,
      final Map<Long, PidcVariantAttribute> pidcVarAttrMap) {
    AttributeValueLoader valLoader = new AttributeValueLoader(getServiceData());
    excelVarAttrMap.entrySet().stream().forEach(excelVarEntry -> {
      PidcVariantAttribute varAttr = pidcVarAttrMap.get(excelVarEntry.getKey());
      if (CommonUtils.isNotNull(varAttr)) {
        compareVarAttribute(valLoader, excelVarEntry, varAttr);
      }
      else {
        ProjectImportAttr<PidcVariantAttribute> pidcVarImpAttr = new ProjectImportAttr<>();
        pidcVarImpAttr.setExcelAttr(excelVarEntry.getValue());
        pidcVarImpAttr.setPidcAttr(null);
        setImportValidity(pidcVarImpAttr, false, PidcExcelDataComparator.ATTR_NOT_FOUND);
      }
    });

  }

  /**
   * @param valLoader
   * @param excelVarEntry
   * @param varAttr
   */
  private void compareVarAttribute(final AttributeValueLoader valLoader,
      final Entry<Long, PidcVariantAttribute> excelVarEntry, final PidcVariantAttribute varAttr) {
    try {
      if (CommonUtils.isNotNull(varAttr.getValueId())) {
        varAttr.setValue(valLoader.getDataObjectByID(varAttr.getValueId()).getNameRaw());
      }
      if (this.pidcImportValidator.isAttrFieldChanged(excelVarEntry.getValue(), varAttr)) {
        compareVarAttr(excelVarEntry.getValue(), varAttr);
      }
    }
    catch (IcdmException e) {
      getLogger().error(e.getMessage(), e);
    }
  }


  /**
   * @param pidcVarImpAttr
   * @param isValid
   * @param comment
   */
  private void setImportValidity(final ProjectImportAttr<PidcVariantAttribute> pidcVarImpAttr, final boolean isValid,
      final String comment) {
    pidcVarImpAttr.setValidImport(isValid);
    pidcVarImpAttr.setComment(comment);
  }

  /**
   * @param excelAttr imported variant attribute
   * @param varAttr pidc variant attribute
   * @throws IcdmException
   */
  private void compareVarAttr(final PidcVariantAttribute excelAttr, final PidcVariantAttribute varAttr)
      throws IcdmException {
    ProjectImportAttr<PidcVariantAttribute> pidcVarImpAttr = new ProjectImportAttr<>();
    if (!isValidAttrModification(excelAttr, varAttr, pidcVarImpAttr)) {
      addAttrToMap(varAttr, pidcVarImpAttr, this.pidcExcelDataComparator.getInvalidVarAttrImportData());
    }
    else {
      addAttrToMap(varAttr, pidcVarImpAttr, this.pidcExcelDataComparator.getValidVarAttrImportData());
    }
    addAttrToMap(varAttr, pidcVarImpAttr, this.pidcExcelDataComparator.getVarAttrImportData());
  }

  /**
   * @param varAttr
   * @param pidcVarImpAttr
   * @param importMap
   */
  private void addAttrToMap(final PidcVariantAttribute varAttr,
      final ProjectImportAttr<PidcVariantAttribute> pidcVarImpAttr,
      final Map<Long, Map<Long, ProjectImportAttr<PidcVariantAttribute>>> importMap) {
    if (importMap.containsKey(varAttr.getVariantId())) {
      importMap.get(varAttr.getVariantId()).put(varAttr.getAttrId(), pidcVarImpAttr);
    }
    else {
      Map<Long, ProjectImportAttr<PidcVariantAttribute>> varImpAttrMap = new HashMap<>();
      varImpAttrMap.put(varAttr.getAttrId(), pidcVarImpAttr);
      importMap.put(varAttr.getVariantId(), varImpAttrMap);
    }
  }

  /**
   * @param excelAttr
   * @param varAttr
   * @param pidcVarImpAttr
   * @return
   * @throws IcdmException
   */
  private boolean isValidAttrModification(final PidcVariantAttribute excelAttr, final PidcVariantAttribute varAttr,
      final ProjectImportAttr<PidcVariantAttribute> pidcVarImpAttr)
      throws IcdmException {
    pidcVarImpAttr.setExcelAttr(excelAttr);
    pidcVarImpAttr.setPidcAttr(varAttr);
    PidcVariantAttribute compAttr = varAttr.clone();
    CommonUtils.shallowCopy(compAttr, varAttr);
    setExcelFieldsForCompAttr(compAttr, excelAttr);
    pidcVarImpAttr.setCompareAttr(compAttr);

    // Get the attribute object
    Attribute attr = getAttrObj(varAttr);
    if (null != attr) {
      pidcVarImpAttr.setAttr(attr);

      // Validate selected pidc version
      if (!isSelPidcValid(pidcVarImpAttr, attr, varAttr)) {
        return false;
      }
      // Validate imported excel file
      if (!isExcelValid(pidcVarImpAttr, excelAttr, varAttr, attr)) {
        return false;
      }
      setImportDataFields(excelAttr, pidcVarImpAttr, attr);
      return true;
    }
    return false;

  }

  /**
   * @param compAttr
   * @param excelAttr
   */
  private void setExcelFieldsForCompAttr(final PidcVariantAttribute compAttr, final PidcVariantAttribute excelAttr) {
    compAttr.setUsedFlag(excelAttr.getUsedFlag());
    compAttr.setValue(excelAttr.getValue());
    compAttr.setPartNumber(excelAttr.getPartNumber());
    compAttr.setSpecLink(excelAttr.getSpecLink());
    compAttr.setAdditionalInfoDesc(excelAttr.getAdditionalInfoDesc());
  }

  /**
   * @param excelAttr @param pidcVarImpAttr @param attr @throws DataException @throws
   */
  private void setImportDataFields(final PidcVariantAttribute excelAttr,
      final ProjectImportAttr<PidcVariantAttribute> pidcVarImpAttr, final Attribute attr)
      throws DataException {
    pidcVarImpAttr.setValidImport(true);

    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    if (null != excelAttr.getValue()) {
      List<AttributeValue> matchedValList = attrValLoader.getAttrValues(attr.getId()).values().stream()
          .filter(val -> val.getNameRaw().trim().equals(excelAttr.getValue().trim())).collect(Collectors.toList());
      if ((null != matchedValList) && !matchedValList.isEmpty()) {
        pidcVarImpAttr.setNewlyAddedVal(false);
        pidcVarImpAttr.getCompareAttr().setValueId(matchedValList.get(0).getId());
        pidcVarImpAttr.setCleared((null != matchedValList.get(0).getClearingStatus()) &&
            matchedValList.get(0).getClearingStatus().equals(ApicConstants.CODE_YES));
      }
      else {
        pidcVarImpAttr.setNewlyAddedVal(true);
        ApicAccessRightLoader apicAccessLoader = new ApicAccessRightLoader(getServiceData());
        pidcVarImpAttr.setCleared(apicAccessLoader.getAccessRightsCurrentUser().isApicWrite());
        pidcVarImpAttr.setComment(PidcExcelDataComparator.NEWLY_ADDED_VALUE);
      }
    }
    else {
      pidcVarImpAttr.setNewlyAddedVal(false);
    }
  }

  /**
   * @param pidcVarImpAttr
   * @param excelAttr
   * @param varAttr
   * @param attr
   * @return
   */
  private boolean isExcelValid(final ProjectImportAttr<PidcVariantAttribute> pidcVarImpAttr,
      final PidcVariantAttribute excelAttr, final PidcVariantAttribute varAttr, final Attribute attr) {
    // Check whether spec link of the attribute is modified and is allowed
    if (this.pidcImportValidator.isInvalidSpecLinkEdit(excelAttr, varAttr)) {
      setImportValidity(pidcVarImpAttr, false, PidcExcelDataComparator.INVALID_SPEC_EDIT_NOT_ALLOWED);
      return false;
    }

    // Check whether the value type in excel is same as in pidc
    if (this.pidcImportValidator.isValueTypeValid(excelAttr, attr)) {
      if (!this.pidcImportValidator.isModifiedValueValid(excelAttr, attr)) {
        setImportValidity(pidcVarImpAttr, false, PidcExcelDataComparator.INVALID_MOD_EDIT_NOT_ALLOWED);
        return false;
      }
    }
    else {
      setImportValidity(pidcVarImpAttr, false, PidcExcelDataComparator.INVALID_MOD_VALUE_TYPE);
      return false;
    }

    // Check whether the attr value is deleted or rejected
    if (this.pidcImportValidator.isAttrValDelRejected(varAttr.getValueId())) {
      setImportValidity(pidcVarImpAttr, false, PidcExcelDataComparator.INVALID_MOD_DEL_REJ);
      return false;
    }

    // Check whether used flag value combination is valid
    if (this.pidcImportValidator.isUsedFlagValComboInValid(excelAttr, varAttr, attr)) {
      setImportValidity(pidcVarImpAttr, false, PidcExcelDataComparator.INVALID_USEDFLAG_VAL_COMBO);
      pidcVarImpAttr.setNewlyAddedVal(false);
      return false;
    }

    setAttrCreationStatus(excelAttr, varAttr, pidcVarImpAttr);
    return true;
  }

  /**
   * @param excelAttr
   * @param varAttr
   * @param pidcVarImpAttr
   */
  private void setAttrCreationStatus(final PidcVariantAttribute excelAttr, final PidcVariantAttribute varAttr,
      final ProjectImportAttr<PidcVariantAttribute> pidcVarImpAttr) {
    if (varAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getDbType()) &&
        !excelAttr.getUsedFlag().equals(varAttr.getUsedFlag())) {
      pidcVarImpAttr.setCreateAttr(true);
    }
  }

  /**
   * @param excelAttr
   * @param varAttr
   * @param pidcVarImpAttr
   */
  private void setAttrCreationStatus(final PidcSubVariantAttribute excelAttr, final PidcSubVariantAttribute subvarAttr,
      final ProjectImportAttr<PidcSubVariantAttribute> pidcSubVarImpAttr) {
    if (subvarAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getDbType()) &&
        !excelAttr.getUsedFlag().equals(subvarAttr.getUsedFlag())) {
      pidcSubVarImpAttr.setCreateAttr(true);
    }
  }

  /**
   * @param pidcVarImpAttr
   * @param attr
   * @param varAttr
   * @return
   * @throws IcdmException
   */
  private boolean isSelPidcValid(final ProjectImportAttr<PidcVariantAttribute> pidcVarImpAttr, final Attribute attr,
      final PidcVariantAttribute varAttr)
      throws IcdmException {
    // Check whether the attribute is deleted in pidc
    if (attr.isDeleted()) {
      setImportValidity(pidcVarImpAttr, false, PidcExcelDataComparator.QNAIRE_CONFIG_ATTR_EDIT_NOT_ALLOWED +
          new AttributeLoader(getServiceData()).getQnaireConfigAttr().getName());
      return false;
    }

    // Check whether the attribute is a division attribute and its value is used in review
    if (this.pidcImportValidator.isQnaireConfigAttr(varAttr) &&
        this.pidcImportValidator.isQnaireConfigAttrUsedInRvw(varAttr)) {
      setImportValidity(pidcVarImpAttr, false, PidcExcelDataComparator.QNAIRE_CONFIG_ATTR_EDIT_NOT_ALLOWED +
          new AttributeLoader(getServiceData()).getQnaireConfigAttr().getName());
      return false;
    }

    // Check whether the user is allowed to edit the attribute
    if (this.pidcImportValidator.isAttrEditNotAllowed(varAttr)) {
      setImportValidity(pidcVarImpAttr, false, PidcExcelDataComparator.INVALID_MOD_EDIT_NOT_ALLOWED);
      return false;
    }
    return true;
  }

  /**
   * @param varAttr
   * @return
   */
  private Attribute getAttrObj(final PidcVariantAttribute varAttr) {
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    try {
      return attrLoader.getDataObjectByID(varAttr.getAttrId());
    }
    catch (DataException e) {
      getLogger().error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * @return the pidcExcelDataComparator
   */
  public PidcExcelDataComparator getPidcExcelDataComparator() {
    return this.pidcExcelDataComparator;
  }


}
