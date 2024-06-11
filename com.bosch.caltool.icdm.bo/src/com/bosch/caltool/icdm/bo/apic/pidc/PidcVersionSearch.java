/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchCondition;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchInput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchResult;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;

/**
 * @author bne4cob
 */
class PidcVersionSearch extends AbstractSimpleBusinessObject implements Callable<PidcSearchResult> {

  private final PidcVersion pidcVers;
  private final PidcSearchInput input;
  private ServiceData localServiceData;

  private final boolean useNewSrvDta;
  private Set<Long> booleanAttrIds = new HashSet<>();

  PidcVersionSearch(final ServiceData refServiceData, final PidcScout scout, final PidcVersion pidcVers,
      final boolean useNewSrvDta) {

    super(refServiceData);

    this.useNewSrvDta = useNewSrvDta;
    this.pidcVers = pidcVers;
    this.input = scout.getSearchInput();
    this.booleanAttrIds = scout.getBooleanAttrIds();
  }

  /**
   * {@inheritDoc}
   *
   * @throws IcdmException error while running search
   */
  @Override
  public PidcSearchResult call() throws IcdmException {
    getLogger().debug("Checking PIDC Version '{}' STARTED", this.pidcVers.getId());

    PidcSearchResult pidcSearchResult;

    if (this.useNewSrvDta) {
      // If use new service data is true, create a new service data object for searching
      try (ServiceData sdata = new ServiceData()) {
        super.getServiceData().copyTo(sdata, true);
        this.localServiceData = sdata;
        pidcSearchResult = doRunSearch();
      }
    }
    else {
      pidcSearchResult = doRunSearch();
    }

    getLogger().debug("Checking PIDC Version '{}' COMPLETED.", this.pidcVers.getId());

    return pidcSearchResult;
  }


  private PidcSearchResult doRunSearch() throws IcdmException {
    PidcSearchResult pidcSearchResult = createSearchResult();

    PidcVersionAttributeModel pidcVersAttrModel = null;
    boolean isMatchInPidcVersion = false;
    boolean isEmptySearch = this.input.getSearchConditions().isEmpty();

    if (!isEmptySearch) {
      pidcVersAttrModel = new ProjectAttributeLoader(getServiceData()).createModel(this.pidcVers.getId(),
          LOAD_LEVEL.L5_SUBVAR_ATTRS, false);
      isMatchInPidcVersion = isMatchInPidcVersion(pidcVersAttrModel, pidcSearchResult);
    }

    if (isEmptySearch || isMatchInPidcVersion) {
      getLogger().debug("PIDC Version '{}' - Match found.", this.pidcVers.getId());

      pidcSearchResult.setPidcVersion(this.pidcVers);

      if (pidcVersAttrModel == null) {
        // Model is not created yet. fetch structure directly from pidc version attribute loader
        pidcSearchResult.setPidc(new PidcLoader(getServiceData()).getDataObjectByID(this.pidcVers.getPidcId()));
        pidcSearchResult.setLevelAttrMap(
            new PidcVersionAttributeLoader(getServiceData()).getStructureAttributes(this.pidcVers.getId()));
      }
      else {
        // Model is already created, use it to retrieve structure attributes
        pidcSearchResult.setPidc(pidcVersAttrModel.getPidc());
        PidcVersionInfo pidcVersWithStruct =
            (new PidcVersionLoader(getServiceData())).getPidcVersionWithStructureAttributes(pidcVersAttrModel);
        if (CommonUtils.isNotNull(pidcVersWithStruct)) {
          pidcSearchResult.setLevelAttrMap(pidcVersWithStruct.getLevelAttrMap());
        }
        else {
          pidcSearchResult = null;
        }
      }

      if (pidcSearchResult != null) {
        fillAdditionalDetails(pidcVersAttrModel, pidcSearchResult);
      }
    }
    else {
      pidcSearchResult = null;
    }

    return pidcSearchResult;
  }


  /**
   * @param pidcVersAttrModel as PidcVersionAttributeModel
   * @param pidcSearchResult as PidcSearchResult
   * @return
   * @throws IcdmException
   */
  private boolean isMatchInPidcVersion(final PidcVersionAttributeModel pidcVersAttrModel,
      final PidcSearchResult pidcSearchResult)
      throws IcdmException {

    boolean isPidcMatching = true;

    for (PidcSearchCondition condition : this.input.getSearchConditions()) {
      Attribute attr = pidcVersAttrModel.getAttribute(condition.getAttributeId());

      int attrLvl = attr.getLevel() == null ? 0 : attr.getLevel().intValue();
      switch (attrLvl) {

        case ApicConstants.PROJECT_NAME_ATTR:
          isPidcMatching = checkProjObjNameMatch(condition, pidcVersAttrModel.getPidc().getNameValueId());
          break;

        case ApicConstants.VARIANT_CODE_ATTR:
          isPidcMatching = checkVarNameMatch(condition, pidcVersAttrModel, pidcSearchResult);
          break;

        case ApicConstants.SUB_VARIANT_CODE_ATTR:
          isPidcMatching = checkSubVarNameMatch(condition, pidcVersAttrModel, pidcSearchResult);
          break;

        default:
          isPidcMatching = isMatchingInPidcVersionAttr(condition, pidcVersAttrModel, pidcSearchResult);
          break;
      }

      if (!isPidcMatching) {
        break;
      }
    }
    if (!isPidcMatching) {
      getLogger().debug("PIDC Version '{}' - NOT a match.", this.pidcVers.getId());
    }
    return isPidcMatching;
  }


  /**
   * @param con as PidcSearchCondition
   * @param pidcVersAttrModel as PidcVersionAttributeModel
   * @param pidcSearchResult as PidcSearchResult
   * @return boolean
   */
  private boolean checkSubVarNameMatch(final PidcSearchCondition con, final PidcVersionAttributeModel pidcVersAttrModel,
      final PidcSearchResult pidcSearchResult) {

    boolean ret = false;

    Collection<PidcSubVariant> subVarCol = pidcVersAttrModel.getSubVariantMap().values();
    if (subVarCol.isEmpty() && ApicConstants.USED_NO_DISPLAY.equals(con.getUsedFlag())) {
      ret = true;
    }
    else {
      for (PidcSubVariant svar : subVarCol) {
        if (checkProjObjNameMatch(con, svar.getNameValueId())) {
          pidcSearchResult.addSubVariant(svar);
          ret = true;
          // Continue with remaining sub variants, since same name can be added
          // with different variants in same pidc version
        }
      }
    }
    return ret;
  }

  /**
   * @param con as PidcSearchCondition
   * @param pidcVersAttrModel as PidcVersionAttributeModel
   * @param pidcSearchResult as PidcSearchResult
   * @return boolean
   */
  private boolean checkVarNameMatch(final PidcSearchCondition con, final PidcVersionAttributeModel pidcVersAttrModel,
      final PidcSearchResult pidcSearchResult) {

    boolean ret = false;

    Collection<PidcVariant> variantCol = pidcVersAttrModel.getVariantMap().values();
    if (variantCol.isEmpty() && ApicConstants.USED_NO_DISPLAY.equals(con.getUsedFlag())) {
      ret = true;
    }
    else {
      for (PidcVariant var : variantCol) {
        if (checkProjObjNameMatch(con, var.getNameValueId())) {
          pidcSearchResult.addVariant(var);
          ret = true;
          break;
        }
      }
    }

    return ret;
  }

  /**
   * Verifies whether the name is a match
   *
   * @param condition search condition
   * @param nameValueID Attribute value ID for the name
   * @return true, if name matched
   */
  private boolean checkProjObjNameMatch(final PidcSearchCondition condition, final Long nameValueID) {
    String usedFlag = condition.getUsedFlag();
    if (ApicConstants.USED_NO_DISPLAY.equals(usedFlag) || ApicConstants.USED_NOTDEF_DISPLAY.equals(usedFlag)) {
      return false;
    }

    return ApicConstants.USED_YES_DISPLAY.equals(usedFlag) || condition.getAttributeValueIds().contains(nameValueID);
  }

  /**
   * Checks whether the condition is matching in project and child attributes
   *
   * @param condition search condition
   * @param pidcVersAttrModel PidcVersionAttributeModel
   * @param pidcSearchResult as PidcSearchResult
   * @return true, if match is found
   * @throws DataException
   */
  private boolean isMatchingInPidcVersionAttr(final PidcSearchCondition condition,
      final PidcVersionAttributeModel pidcVersAttrModel, final PidcSearchResult pidcSearchResult)
      throws DataException {

    boolean isPidcMatching;
    PidcVersionAttribute pidcAttr = pidcVersAttrModel.getPidcVersAttr(condition.getAttributeId());
    if (pidcAttr == null) {
      // Attribute is not visible due to dependency
      isPidcMatching = false;
    }
    else if (pidcAttr.isAtChildLevel()) {
      // Search variants if attribute definition is at variant level
      isPidcMatching = isMatchingInVarAttrs(condition, pidcVersAttrModel, pidcSearchResult);
    }
    else {
      isPidcMatching = isConditionSatisfied(condition, pidcAttr);
    }

    return isPidcMatching;

  }

  /**
   * Matches the search condition on variants and sub variants of a PIDC
   *
   * @param condition search condtion PidcSearchCondition
   * @param pidcVersAttrModel PidcVersionAttributeModel
   * @param pidcSearchResult as PidcSearchResult
   * @return true, if a match is found for the given search condtion on any variant or sub variant
   * @throws DataException
   */
  private boolean isMatchingInVarAttrs(final PidcSearchCondition condition,
      final PidcVersionAttributeModel pidcVersAttrModel, final PidcSearchResult pidcSearchResult)
      throws DataException {

    boolean isMatchingVar = false;

    for (Entry<Long, PidcVariant> varEntry : pidcVersAttrModel.getVariantMap().entrySet()) {
      PidcVariantAttribute varAttr =
          pidcVersAttrModel.getVariantAttribute(varEntry.getKey(), condition.getAttributeId());
      if (varAttr == null) {
        // Attribute is not visible due to dependency
        continue;
      }
      if (varAttr.isAtChildLevel()) {
        // Search sub variants if attribute definition is at sub-variant level
        if (isMatchingInSVarAttrs(varEntry.getKey(), condition, pidcVersAttrModel, pidcSearchResult)) {
          isMatchingVar = true;
        }
      }
      else if (isConditionSatisfied(condition, varAttr)) {
        isMatchingVar = true;
        pidcSearchResult.addVariant(varEntry.getValue());
      }
    }
    return isMatchingVar;
  }

  /**
   * Matches the search condition on sub-variants of a variant
   *
   * @param variant variant
   * @param condition search condition
   * @param pidcVersAttrModel PidcVersionAttributeModel
   * @param pidcSearchResult as PidcSearchResult
   * @return if a match is found
   * @throws DataException
   */
  private boolean isMatchingInSVarAttrs(final Long variantId, final PidcSearchCondition condition,
      final PidcVersionAttributeModel pidcVersAttrModel, final PidcSearchResult pidcSearchResult)
      throws DataException {

    boolean ismatchingInSVars = false;

    for (Entry<Long, PidcSubVariant> sVarEntry : pidcVersAttrModel.getSubVariantMap(variantId).entrySet()) {
      PidcSubVariantAttribute svarAttr =
          pidcVersAttrModel.getSubVariantAttribute(sVarEntry.getKey(), condition.getAttributeId());
      if (svarAttr == null) {
        // Attribute is not visible due to dependency
        continue;
      }
      if (isConditionSatisfied(condition, svarAttr)) {
        ismatchingInSVars = true;
        pidcSearchResult.addSubVariant(sVarEntry.getValue());
      }
    }
    return ismatchingInSVars;
  }

  /**
   * Verifies the search condtion on the given project attribute.
   *
   * @param condition search condition
   * @param projAttr project attribute defined in the current level, i.e. isVariant() is false
   * @param isBooleanType
   * @param user
   * @return true, if project attribute is a match
   * @throws DataException
   */
  private boolean isConditionSatisfied(final PidcSearchCondition condition, final IProjectAttribute projAttr)
      throws DataException {

    // check if its a boolean type attribute
    if (this.booleanAttrIds.contains(condition.getAttributeId())) {
      return checkValueMatchForBooleanType(condition, projAttr);
    }

    // If used flag is set in the condtion, match for used flag
    if (checkUsedFlag(condition, projAttr)) {
      return true;
    }
    // Check the project attribute value is any one of the values in the search condtion
    if (CommonUtils.isNull(condition.getAttributeValueIds())) {
      return false;
    }

    Long valueId = projAttr.getValueId();
    if (valueId == null) {
      // Value ID is null, either due to value not set or value is hidden to current user
      return false;
    }

    return condition.getAttributeValueIds().contains(valueId);

  }

  /**
   * @param condition
   * @param projAttr
   * @return
   * @throws DataException
   */
  private boolean checkValueMatchForBooleanType(final PidcSearchCondition condition, final IProjectAttribute projAttr)
      throws DataException {

    // For boolean type attributes used flag = Yes should be considered as value=true
    // used flag = No should be considered as value = False
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    if (condition.getUsedFlag() == null) {
      String usedFlag = projAttr.getUsedFlag();
      for (Long valueId : condition.getAttributeValueIds()) {
        String attrValueName = attrValLoader.getDataObjectByID(valueId).getName();
        if ((CommonUtils.isEqual(usedFlag, PROJ_ATTR_USED_FLAG.YES.getDbType()) &&
            CommonUtils.isEqual(attrValueName, ApicConstants.BOOLEAN_TRUE_STRING)) ||
            (CommonUtils.isEqual(usedFlag, PROJ_ATTR_USED_FLAG.NO.getDbType()) &&
                CommonUtils.isEqual(attrValueName, ApicConstants.BOOLEAN_FALSE_STRING))) {
          return true;
        }
      }
    }
    else {
      return compareUsedFlag(condition, projAttr);
    }

    return false;
  }


  /**
   * Match search conditions with used flag
   *
   * @param condition condition
   * @param projAttr project attribute
   * @return true, if match found
   */
  private boolean checkUsedFlag(final PidcSearchCondition condition, final IProjectAttribute projAttr) {

    if (!CommonUtils.isEmptyString(condition.getUsedFlag())) {
      if (AbstractProjectAttributeLoader.isDetailsHiddenToCurrentUser(projAttr)) {
        return false;
      }
      return compareUsedFlag(condition, projAttr);
    }
    return false;
  }

  /**
   * @param condition
   * @param projAttr
   * @return
   */
  private boolean compareUsedFlag(final PidcSearchCondition condition, final IProjectAttribute projAttr) {
    String flagDbType = ApicConstants.PROJ_ATTR_USED_FLAG.getDbType(condition.getUsedFlag());
    return CommonUtils.isEqual(flagDbType, projAttr.getUsedFlag()) ||
        (CommonUtils.isEqual(flagDbType, ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType()) &&
            CommonUtils.isEqual(projAttr.getUsedFlag(), ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getDbType()));
  }

  /**
   * @return new instance of PidcSearchResult with PidcVersion set
   */
  private PidcSearchResult createSearchResult() {
    PidcSearchResult ret = new PidcSearchResult();
    ret.setPidcVersion(this.pidcVers);
    return ret;
  }

  /**
   * @param pidcVersAttrModel as PidcVersionAttributeModel
   * @param pidcSearchResult as PidcSearchResult
   * @throws IcdmException
   */
  private void fillAdditionalDetails(final PidcVersionAttributeModel pidcVersAttrModel,
      final PidcSearchResult pidcSearchResult)
      throws IcdmException {

    if (this.input.isSearchReviews()) {
      pidcSearchResult
          .setReviewResultsFound(new PidcA2lLoader(getServiceData()).hasDataReviewResults(this.pidcVers.getId()));
    }

    if (this.input.isSearchA2lFiles()) {
      pidcSearchResult.setA2lFilesMapped(pidcSearchResult.isReviewResultsFound() || hasA2lFiles());
    }

    if (this.input.isSearchFocusMatrix()) {
      pidcSearchResult.setFocusMatrixDefined(hasFocusMatrixDefinition(pidcVersAttrModel));
    }
  }

  /**
   * @param pidcVersAttrModel as PidcVersionAttributeModel
   * @return boolean true / false based on condtion
   * @throws IcdmException
   */
  private boolean hasFocusMatrixDefinition(final PidcVersionAttributeModel pidcVersAttrModel) throws IcdmException {
    PidcVersionAttributeModel pModel = pidcVersAttrModel;
    if (pModel == null) {
      pModel = new ProjectAttributeLoader(getServiceData()).createModel(this.pidcVers.getId(), LOAD_LEVEL.L1_PROJ_ATTRS,
          false);
    }
    return new FocusMatrixLoader(getServiceData()).hasFocusMatrixForPidcVersion(this.pidcVers.getId(), pModel);
  }

  /**
   * @return
   */
  private boolean hasA2lFiles() {
    return new PidcA2lLoader(getServiceData()).hasA2lFiles(this.pidcVers.getId());
  }

  /**
   * @return service data
   */
  @Override
  public ServiceData getServiceData() {
    return this.localServiceData == null ? super.getServiceData() : this.localServiceData;
  }

}
