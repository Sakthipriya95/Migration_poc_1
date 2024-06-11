/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.differences.textoutputs;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.FocusMatrixColorCode;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.textoutput.adapter.IcdmPidcLogAdapter;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangedAttrType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangedSubVarType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangedVariantType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsResponseType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcFocusMatrixType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcFocusMatrixVersType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;


/**
 * @author imi2si
 */
public class IcdmPidcLogOutputNew extends AbstractPidcLogOutputNew {

  /**
   *
   */
  private static final String DELETED_NO = "Deleted = No";
  /**
   *
   */
  private static final String DELETED_YES = "Deleted = Yes";
  /**
   * String constant for Relevant to focus matrix
   */
  private static final String RELEVANT_TO_FOCUS_MATRIX = "Relevant to Focus Matrix?";
  /**
   * varaint coding code attr id
   */
  private static final Long VARIANT_CODING_CODE = 3011L;
  /**
   * varaint coded attr id
   */
  private static final Long VARIANT_CODED_ATTR_ID = 126L;
  /**
   *
   */
  private static final String CHANGE_NUMBER_STR = " with change number ";
  /**
   * Constant for Sub variant
   */
  private static final String SUB_VARIANT = "Sub-Variant: ";
  /**
   * Constant for variant
   */
  private static final String VARIANT = "Variant: ";

  SortedSet<IcdmPidcLogAdapter> resultSet = new TreeSet<>();

  /**
   * @param logger
   * @param servData
   * @param diff
   * @param language
   * @param string
   */
  public IcdmPidcLogOutputNew(final ILoggerAdapter logger, final ServiceData servData, final PidcDiffsResponseType diff,
      final String language) {
    super(logger, servData, diff, language);
  }

  /**
   * {@inheritDoc}
   *
   * @throws IcdmException
   */
  @Override
  public void createWsResponse() throws IcdmException {
    this.logger.debug("Start creating IcdmPidcLogOutput for PIDC-ID: " + this.diff.getPidcId());
    addPidcAttribute();
    addVarAttribute();
    addSvarAttribute();
    addVariants();
    addSubVariants();

    // ICDM-2614
    addFMVersAttribute();

    // iCDM-2614
    addFMAttribute();
    this.logger.debug("End creating IcdmPidcLogOutput for PIDC-ID: " + this.diff.getPidcId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<IcdmPidcLogAdapter> getWsResponse() {

    return this.resultSet;
  }

  public void addVariants() throws IcdmException {

    for (PidcChangedVariantType variant : this.diff.getPidcChangedVariantTypeList()) {

      // If an ID is not existing any longer in the db, but still existing in the history, continue with next item. If
      // just the ID is existing, no additional information are available.
      if (!super.isVarExisting(variant.getVariantId())) {
        continue;
      }
      PidcVariant locVar = super.getPidcVaraint(variant.getVariantId());


      if ((!CommonUtils.isEqual(variant.getOldValueId(), variant.getNewValueId())) &&
          ApicConstants.CODE_NO.equals(variant.getNewIsDeleted())) {

        IcdmPidcLogAdapter adapter = null;
        if (((null != variant.getOldValueId()) && !equalsLong(variant.getOldValueId(), variant.getNewValueId())) &&
            ApicConstants.CODE_NO.equals(variant.getOldIsDeleted())) {
          AttributeValue oldAttrValue = super.getAttrValue(variant.getOldValueId());
          AttributeValue newAttrValue = super.getAttrValue(variant.getNewValueId());
          adapter = new IcdmPidcLogAdapter(VARIANT + locVar.getName(), super.getAttribute(VARIANT_CODED_ATTR_ID),
              "Variant Renamed", null != oldAttrValue ? oldAttrValue.getValueEng() : variant.getOldTextValueEng(),
              null != newAttrValue ? newAttrValue.getValueEng() : variant.getNewTextValueEng(),
              variant.getModifiedUser(), getUserName(variant.getModifiedUser()), variant.getModifiedDate(),
              variant.getNewChangeNumber(), variant.getPidcVersion(), this.diff.getPidcId(), variant.getVariantId(),
              null, false, variant.getPidcVersChangeNumber());
        }
        else if (ApicConstants.CODE_YES.equals(variant.getOldIsDeleted())) {
          adapter = new IcdmPidcLogAdapter(VARIANT + locVar.getName(), super.getAttribute(VARIANT_CODED_ATTR_ID),
              "Variant undeleted", DELETED_YES, DELETED_NO, variant.getModifiedUser(),
              getUserName(variant.getModifiedUser()), variant.getModifiedDate(), variant.getNewChangeNumber(),
              variant.getPidcVersion(), this.diff.getPidcId(), variant.getVariantId(), null, false,
              variant.getPidcVersChangeNumber());
        }
        else {
          AttributeValue newAttrValue = super.getAttrValue(variant.getNewValueId());
          String newvariantName = null != newAttrValue ? newAttrValue.getValueEng() : locVar.getName();
          adapter = new IcdmPidcLogAdapter(VARIANT + newvariantName, super.getAttribute(VARIANT_CODED_ATTR_ID),
              "Variant created", "", newvariantName, variant.getModifiedUser(), getUserName(variant.getModifiedUser()),
              variant.getModifiedDate(), variant.getNewChangeNumber(), variant.getPidcVersion(), this.diff.getPidcId(),
              variant.getVariantId(), null, false, variant.getPidcVersChangeNumber());
        }
        this.resultSet.add(adapter);
      }
      else if (!CommonUtils.isEqual(variant.getOldIsDeleted(), variant.getNewIsDeleted())) {

        // get the PIDC id from the Active Version
        IcdmPidcLogAdapter adapter = new IcdmPidcLogAdapter(VARIANT + locVar.getName(),
            super.getAttribute(VARIANT_CODED_ATTR_ID), "Variant deleted", DELETED_NO, DELETED_YES,
            variant.getModifiedUser(), getUserName(variant.getModifiedUser()), variant.getModifiedDate(),
            variant.getNewChangeNumber(), variant.getPidcVersion(), this.diff.getPidcId(), variant.getVariantId(), null,
            false, variant.getPidcVersChangeNumber());

        this.resultSet.add(adapter);
      }
    }
  }

  public void addSubVariants() throws IcdmException {

    for (PidcChangedVariantType variant : this.diff.getPidcChangedVariantTypeList()) {

      for (PidcChangedSubVarType sVariant : variant.getChangedSubVariantList()) {

        // If an ID is not existing any longer in the db, but still existing in the history, continue with next item. If
        // just the ID is existing, no additional information are available.
        if (!super.isSubVarExisting(sVariant.getSubVariantId())) {
          continue;
        }

        PidcSubVariant locSvar = super.getPidcSubVaraint(sVariant.getSubVariantId());


        if ((!CommonUtils.isEqual(sVariant.getOldValueId(), sVariant.getNewValueId())) &&
            ApicConstants.CODE_NO.equals(sVariant.getNewIsDeleted())) {
          IcdmPidcLogAdapter adapter = null;
          if (!equalsAttr(sVariant.getOldTextValueEng(), sVariant.getNewTextValueEng()) ||
              !equalsAttr(sVariant.getOldTextValueGer(), sVariant.getNewTextValueGer())) {
            adapter = new IcdmPidcLogAdapter(SUB_VARIANT + locSvar.getName(), super.getAttribute(VARIANT_CODING_CODE),
                "Sub-Variant Renamed", sVariant.getOldTextValueEng(), sVariant.getNewTextValueEng(),
                sVariant.getModifiedUser(), getUserName(sVariant.getModifiedUser()), sVariant.getModifiedDate(),
                sVariant.getNewChangeNumber(), sVariant.getPidcVersion(), this.diff.getPidcId(),
                locSvar.getPidcVariantId(), locSvar.getId(), false, sVariant.getPidcVersChangeNumber());
          }
          else if (ApicConstants.CODE_YES.equals(sVariant.getOldIsdeleted())) {
            adapter = new IcdmPidcLogAdapter(SUB_VARIANT + locSvar.getName(), super.getAttribute(VARIANT_CODING_CODE),
                "Sub-Variant Undeleted", DELETED_YES, DELETED_NO, sVariant.getModifiedUser(),
                getUserName(sVariant.getModifiedUser()), sVariant.getModifiedDate(), sVariant.getNewChangeNumber(),
                sVariant.getPidcVersion(), this.diff.getPidcId(), locSvar.getPidcVariantId(), locSvar.getId(), false,
                sVariant.getPidcVersChangeNumber());
          }
          else {
            adapter = new IcdmPidcLogAdapter(SUB_VARIANT + locSvar.getName(), super.getAttribute(VARIANT_CODING_CODE),
                "Sub-Variant created", "", locSvar.getName(), sVariant.getModifiedUser(),
                getUserName(sVariant.getModifiedUser()), sVariant.getModifiedDate(), sVariant.getNewChangeNumber(),
                sVariant.getPidcVersion(), this.diff.getPidcId(), locSvar.getPidcVariantId(), locSvar.getId(), false,
                sVariant.getPidcVersChangeNumber());
          }
          this.resultSet.add(adapter);
        }
        else if (!CommonUtils.isEqual(sVariant.getOldIsdeleted(), sVariant.getNewIsDeleted())) {
          // get the PIDC id from the Active Version
          IcdmPidcLogAdapter adapter = new IcdmPidcLogAdapter(SUB_VARIANT + locSvar.getName(),
              super.getAttribute(VARIANT_CODING_CODE), "Sub-Variant deleted", DELETED_NO, DELETED_YES,
              sVariant.getModifiedUser(), getUserName(sVariant.getModifiedUser()), sVariant.getModifiedDate(),
              sVariant.getNewChangeNumber(), sVariant.getPidcVersion(), this.diff.getPidcId(),
              locSvar.getPidcVariantId(), locSvar.getId(), false, sVariant.getPidcVersChangeNumber());

          this.resultSet.add(adapter);
        }
      }
    }
  }

  public void addPidcAttribute() throws IcdmException {

    for (PidcChangedAttrType attribute : this.diff.getPidcChangedAttrTypeList()) {
      this.logger.debug(
          "Adding Attribute on PIDC level: " + attribute.getAttrId() + CHANGE_NUMBER_STR + attribute.getChangeNumber());
      add(attribute, "PIDC", this.diff.getPidcId(), null, null, false);
    }

  }

  // ICDM-2614
  public void addFMVersAttribute() throws IcdmException {

    for (PidcFocusMatrixVersType attribute : this.diff.getPidcFocusMatrixVersTypeList()) {
      this.logger.debug("Adding Focus Matrix Version on PIDC level: " + attribute.getFmVersId());
      addFMVersionEntries(attribute, "Focus Matrix Version", this.diff.getPidcId(), null, null);
    }
  }

  // ICDM-2614
  public void addFMAttribute() throws IcdmException {

    for (PidcFocusMatrixType attribute : this.diff.getPidcFocusMatrixTypeList()) {
      this.logger.debug("Adding Focus Matrix on PIDC level: " + attribute.getAttrId());
      addFMEntries(attribute, "Focus Matrix", this.diff.getPidcId(), null, null);
    }
  }

  public void addVarAttribute() throws IcdmException {

    for (PidcChangedVariantType variant : this.diff.getPidcChangedVariantTypeList()) {
      // If an ID is not existing any longer in the db, but still existing in the history, continue with next item. If
      // just the ID is existing, no additional information are available.
      if (!super.isVarExisting(variant.getVariantId())) {
        continue;
      }

      PidcVariant locVar = super.getPidcVaraint(variant.getVariantId());


      for (PidcChangedAttrType attribute : variant.getChangedAttrList()) {
        this.logger.debug("Adding Attribute on Variant level: " + attribute.getAttrId() + CHANGE_NUMBER_STR +
            attribute.getChangeNumber());
        add(attribute, VARIANT + locVar.getName(), this.diff.getPidcId(), variant.getVariantId(), null, true);
      }
    }
  }

  public void addSvarAttribute() throws IcdmException {

    for (PidcChangedVariantType variant : this.diff.getPidcChangedVariantTypeList()) {

      for (PidcChangedSubVarType sVariant : variant.getChangedSubVariantList()) {

        // If an ID is not existing any longer in the db, but still existing in the history, continue with next item. If
        // just the ID is existing, no additional information are available.
        if (!super.isSubVarExisting(sVariant.getSubVariantId())) {
          continue;
        }

        PidcSubVariant locSvar = super.getPidcSubVaraint(sVariant.getSubVariantId());

        for (PidcChangedAttrType attribute : sVariant.getChangedAttrList()) {
          this.logger.debug("Adding Attribute on Sub-Variant level: " + attribute.getAttrId() + CHANGE_NUMBER_STR +
              attribute.getChangeNumber());
          add(attribute, SUB_VARIANT + locSvar.getName(), this.diff.getPidcId(), variant.getVariantId(),
              sVariant.getSubVariantId(), false);
        }
      }
    }
  }

  private void addToResultset(final PidcChangedAttrType type, final String level, final String changedItemStr,
      final String oldValueStr, final String newValueStr, final AttributeValue oldAttrValue,
      final AttributeValue newAttrValue, final Long pidcId, final Long varId, final Long sVarId)
      throws IcdmException {
    IcdmPidcLogAdapter adapter;
    // ICDM-1407
    if (((null == type.getAttrId()) || (type.getAttrId() == 0)) && (type.getPidcAction() != null)) {
      adapter = new IcdmPidcLogAdapter(level,
          super.getAttribute(new AttributeLoader(this.serviceData).getAllLevelAttributes()
              .get(Long.valueOf(ApicConstants.VCDM_APRJ_NAME_ATTR)).getId()),
          changedItemStr, oldAttrValue, newAttrValue, oldValueStr, newValueStr, type.getModifyUser(),
          getUserName(type.getModifyUser()), type.getModifyDate(), type.getChangeNumber(), type.getPidcVers(), pidcId,
          varId, sVarId, true, false, type.getPidcVersChangeNum(), null, null, null, null);
    }
    else {

      if (!super.isAttributeExisting(type.getAttrId())) {
        return;
      }

      adapter = new IcdmPidcLogAdapter(level, super.getAttribute(type.getAttrId()), changedItemStr, oldAttrValue,
          newAttrValue, oldValueStr, newValueStr, type.getModifyUser(), getUserName(type.getModifyUser()),
          type.getModifyDate(), type.getChangeNumber(), type.getPidcVers(), pidcId, varId, sVarId, true, false,
          type.getPidcVersChangeNum(), null, null, null, null);
    }
    this.resultSet.add(adapter);
  }


  private void add(final PidcChangedAttrType attr, final String level, final Long pidcId, final Long varId,
      final Long sVarId, final boolean fromSub)
      throws IcdmException {
    addValue(attr, level, pidcId, varId, sVarId);
    addClearingStatus(attr, level, pidcId, varId, sVarId);
    addDescription(attr, level, pidcId, varId, sVarId);
    addPartNumber(attr, level, pidcId, varId, sVarId);
    addSpecLink(attr, level, pidcId, varId, sVarId);
    addUsed(attr, level, pidcId, varId, sVarId);
    // ICDM-2279
    addFocusMatrix(attr, level, pidcId, varId, sVarId);
    // ICDM-2279
    addTransferVcdm(attr, level, pidcId, varId, sVarId);
    addLevel(attr, level, pidcId, varId, sVarId, fromSub);
    // ICDM-1407
    addPidcAction(attr, level, pidcId, varId, sVarId);
  }

  // ICDM-2614
  /**
   * @param attr attr
   * @param level level
   * @param pidcId the project id card id
   * @param varId the variant id
   * @param sVarId the sub-variant id
   * @throws IcdmException
   */
  private void addFMVersionEntries(final PidcFocusMatrixVersType attr, final String level, final Long pidcId,
      final Long varId, final Long sVarId)
      throws IcdmException {
    addFmVersionName(attr, level, pidcId, varId, sVarId);
    addFmVersionLink(attr, level, pidcId, varId, sVarId);
    addFmVersionRvwUser(attr, level, pidcId, varId, sVarId);
    addFmVersionRvwDate(attr, level, pidcId, varId, sVarId);
    addFmVersionRemark(attr, level, pidcId, varId, sVarId);
    addFmVersionRvwStatus(attr, level, pidcId, varId, sVarId);
  }

  // ICDM-2614
  private void addFMEntries(final PidcFocusMatrixType attr, final String level, final Long pidcId, final Long varId,
      final Long sVarId)
      throws IcdmException {
    addColor(attr, level, pidcId, varId, sVarId);
    addFmLink(attr, level, pidcId, varId, sVarId);
    addComments(attr, level, pidcId, varId, sVarId);
    addRelevancy(attr, level, pidcId, varId, sVarId);
  }

  // ICDM-2614
  /**
   * @param attr attr
   * @param level level
   * @param pidcId the project id card id
   * @param varId the variant id
   * @param sVarId the sub-variant id
   * @throws IcdmException
   */
  private void addFmVersionLink(final PidcFocusMatrixVersType attr, final String level, final Long pidcId,
      final Long varId, final Long sVarId)
      throws IcdmException {
    if (!equalsLink(attr)) {
      addFMVersionToResultset(attr, level, "Version Link", attr.getOldFmVersLink(), attr.getNewFmVersLink(), null, null,
          pidcId, varId, sVarId);
    }
  }

  // ICDM-2614
  /**
   * @param attr attr
   * @param level level
   * @param pidcId the project id card id
   * @param varId the variant id
   * @param sVarId the sub-variant id
   * @throws IcdmException
   */
  private void addFmVersionName(final PidcFocusMatrixVersType attr, final String level, final Long pidcId,
      final Long varId, final Long sVarId)
      throws IcdmException {
    if (!equalsFmVersionName(attr)) {
      addFMVersionToResultset(attr, level, "Version Name", attr.getOldFmVersName(), attr.getNewFmVersName(), null, null,
          pidcId, varId, sVarId);
    }
  }

  // ICDM-2614
  /**
   * @param attr attr
   * @param level level
   * @param pidcId the project id card id
   * @param varId the variant id
   * @param sVarId the sub-variant id
   * @throws IcdmException
   */
  private void addFmVersionRvwUser(final PidcFocusMatrixVersType attr, final String level, final Long pidcId,
      final Long varId, final Long sVarId)
      throws IcdmException {
    if (!equalsFmVersionRvwUser(attr)) {
      addFMVersionToResultset(attr, level, "Version Review User", attr.getOldFmVersRvwUser(),
          attr.getNewFmVersRvwUser(), null, null, pidcId, varId, sVarId);
    }
  }

  // ICDM-2614
  /**
   * @param attr attr
   * @param level level
   * @param pidcId the project id card id
   * @param varId the variant id
   * @param sVarId the sub-variant id
   * @throws IcdmException
   */
  private void addFmVersionRvwDate(final PidcFocusMatrixVersType attr, final String level, final Long pidcId,
      final Long varId, final Long sVarId)
      throws IcdmException {
    if (!equalsFmVersionRvwDate(attr)) {
      addFMVersionToResultset(attr, level, "Version Review Date", attr.getOldFmVersRvwDate(),
          attr.getNewFmVersRvwDate(), null, null, pidcId, varId, sVarId);
    }
  }

  // ICDM-2614
  /**
   * @param attr attr
   * @param level level
   * @param pidcId the project id card id
   * @param varId the variant id
   * @param sVarId the sub-variant id
   * @throws IcdmException
   */
  private void addFmVersionRvwStatus(final PidcFocusMatrixVersType attr, final String level, final Long pidcId,
      final Long varId, final Long sVarId)
      throws IcdmException {
    if (!equalsFmVersionRvwStatus(attr)) {
      addFMVersionToResultset(attr, level, "Version Review Status", attr.getOldFmVersStatus(),
          attr.getNewFmVersStatus(), null, null, pidcId, varId, sVarId);
    }
  }

  // ICDM-2614
  /**
   * @param attr attr
   * @param level level
   * @param pidcId the project id card id
   * @param varId the variant id
   * @param sVarId the sub-variant id
   * @throws IcdmException
   */
  private void addFmVersionRemark(final PidcFocusMatrixVersType attr, final String level, final Long pidcId,
      final Long varId, final Long sVarId)
      throws IcdmException {
    if (!equalsFmVersionRemark(attr)) {
      addFMVersionToResultset(attr, level, "Version Remark", attr.getOldRemark(), attr.getNewRemark(), null, null,
          pidcId, varId, sVarId);
    }
  }


  // ICDM-2614
  private void addColor(final PidcFocusMatrixType attr, final String level, final Long pidcId, final Long varId,
      final Long sVarId)
      throws IcdmException {
    if (!equalsColor(attr)) {
      addFMToResultset(attr, level, "Color code",
          FocusMatrixColorCode.getColor(attr.getOldFmColorCode()).getDisplayColorTxt(),
          FocusMatrixColorCode.getColor(attr.getNewFmColorCode()).getDisplayColorTxt(), null, null, pidcId, varId,
          sVarId);
    }
  }

  // ICDM-2614
  private void addFmLink(final PidcFocusMatrixType attr, final String level, final Long pidcId, final Long varId,
      final Long sVarId)
      throws IcdmException {
    if (!equalsLink(attr)) {
      addFMToResultset(attr, level, "Link", attr.getOldFmLink(), attr.getNewFmLink(), null, null, pidcId, varId,
          sVarId);
    }
  }

  // ICDM-2614
  private void addComments(final PidcFocusMatrixType attr, final String level, final Long pidcId, final Long varId,
      final Long sVarId)
      throws IcdmException {
    if (!equalsComments(attr)) {
      addFMToResultset(attr, level, "Comments", attr.getOldComments(), attr.getNewComments(), null, null, pidcId, varId,
          sVarId);
    }
  }

  // ICDM-2614
  private void addRelevancy(final PidcFocusMatrixType attr, final String level, final Long pidcId, final Long varId,
      final Long sVarId)
      throws IcdmException {
    if ((null == attr.getOldDeletedFlag()) && (null == attr.getNewDeletedFlag())) {
      addFMToResultset(attr, level, RELEVANT_TO_FOCUS_MATRIX, "N", "Y", null, null, pidcId, varId, sVarId);
    }
    else if ((null == attr.getOldDeletedFlag()) && ("Y".equals(attr.getNewDeletedFlag()))) {
      addFMToResultset(attr, level, RELEVANT_TO_FOCUS_MATRIX, "Y", "N", null, null, pidcId, varId, sVarId);
    }
    else if (((null == attr.getOldDeletedFlag()) && ("N".equals(attr.getNewDeletedFlag()))) ||
        (("N".equals(attr.getOldDeletedFlag())) && ("N".equals(attr.getNewDeletedFlag())))) {
      // Dont't display according to relevancy logic
    }
    else {
      addFMToResultset(attr, level, RELEVANT_TO_FOCUS_MATRIX, getRelevancyCheckFlag(attr.getOldDeletedFlag()),
          getRelevancyCheckFlag(attr.getNewDeletedFlag()), null, null, pidcId, varId, sVarId);
    }
  }

  // ICDM-2614
  /**
   * If deleted Flag is Yes/No in Focusmatrix, then the corresponding Relevant to Focus Matrix is No/Yes
   *
   * @param deletedFlag the deleted flag
   * @return values for Relevancy
   */
  private static String getRelevancyCheckFlag(final String deletedFlag) {
    if ((null == deletedFlag) || CommonUtilConstants.CODE_YES.equals(deletedFlag)) {
      return "N";
    }
    else if (CommonUtilConstants.CODE_NO.equals(deletedFlag)) {
      return "Y";
    }
    return deletedFlag;
  }


  // ICDM-2614
  private boolean equalsLink(final PidcFocusMatrixVersType attr) {
    return equalsAttr(attr.getOldFmVersLink(), attr.getNewFmVersLink());
  }

  // ICDM-2614
  /**
   * @param attr attr
   * @return true if old & new fm version name are equal
   */
  private boolean equalsFmVersionName(final PidcFocusMatrixVersType attr) {
    return equalsAttr(attr.getOldFmVersName(), attr.getNewFmVersName());
  }

  // ICDM-2614
  /**
   * @param attr attr
   * @return true if old & new fm version review user are equal
   */
  private boolean equalsFmVersionRvwUser(final PidcFocusMatrixVersType attr) {
    return equalsAttr(attr.getOldFmVersRvwUser(), attr.getNewFmVersRvwUser());
  }

  // ICDM-2614
  /**
   * @param attr attr
   * @return true if old & new fm version review date are equal
   */
  private boolean equalsFmVersionRvwDate(final PidcFocusMatrixVersType attr) {
    return equalsAttr(attr.getOldFmVersRvwDate(), attr.getNewFmVersRvwDate());
  }

  // ICDM-2614
  /**
   * @param attr attr
   * @return true if old & new fm version review status are equal
   */
  private boolean equalsFmVersionRvwStatus(final PidcFocusMatrixVersType attr) {
    return equalsAttr(attr.getOldFmVersStatus(), attr.getNewFmVersStatus());
  }

  // ICDM-2614
  /**
   * @param attr attr
   * @return true if old & new fm version remarks are equal
   */
  private boolean equalsFmVersionRemark(final PidcFocusMatrixVersType attr) {
    return equalsAttr(attr.getOldRemark(), attr.getNewRemark());
  }


  // ICDM-2614
  private boolean equalsColor(final PidcFocusMatrixType attr) {
    return equalsAttr(attr.getOldFmColorCode(), attr.getNewFmColorCode());
  }

  // ICDM-2614
  private boolean equalsLink(final PidcFocusMatrixType attr) {
    return equalsAttr(attr.getOldFmLink(), attr.getNewFmLink());
  }

  // ICDM-2614
  private boolean equalsComments(final PidcFocusMatrixType attr) {
    return equalsAttr(attr.getOldComments(), attr.getNewComments());
  }


  // ICDM-2614
  private void addFMVersionToResultset(final PidcFocusMatrixVersType type, final String level,
      final String changedItemStr, final String oldValueStr, final String newValueStr,
      final AttributeValue oldAttrValue, final AttributeValue newAttrValue, final Long pidcId, final Long varId,
      final Long sVarId)
      throws IcdmException {
    /* focus matrix attribute id = 249309 */
    Attribute attribute = super.getAttribute(249309L);

    IcdmPidcLogAdapter adapter = new IcdmPidcLogAdapter(level, attribute, changedItemStr, oldAttrValue, newAttrValue,
        oldValueStr, newValueStr, type.getFmVersModifiedUser(), getUserName(type.getFmVersModifiedUser()),
        type.getFmVersModifiedDate(), type.getChangeNumber() /* version-Id */, type.getPidcVersId(), pidcId, varId,
        sVarId, false, true, type.getPidcVersChangenumber(), null, null, null, type.getFmVersId());

    this.resultSet.add(adapter);
  }

  // ICDM-2614
  private void addFMToResultset(final PidcFocusMatrixType type, final String level, final String changedItemStr,
      final String oldValueStr, final String newValueStr, final AttributeValue oldAttrValue,
      final AttributeValue newAttrValue, final Long pidcId, final Long varId, final Long sVarId)
      throws IcdmException {

    Attribute attribute = super.getAttribute(type.getAttrId());

    IcdmPidcLogAdapter adapter = new IcdmPidcLogAdapter(level, attribute, changedItemStr, oldAttrValue, newAttrValue,
        oldValueStr, newValueStr, type.getModifiedUser(), getUserName(type.getModifiedUser()), type.getModifiedDate(),
        type.getChangeNumber() /* version-Id */, type.getPidcVersId(), pidcId, varId, sVarId, false, true,
        type.getPidcVersChangeNumber(), type.getSectionId(), type.getUseCaseId(), type.getFmId(),
        type.getFmVersionId());

    this.resultSet.add(adapter);
  }


  /**
   * @param attr
   * @param level
   * @param pidcId
   * @param varId
   * @param sVarId
   * @throws IcdmException
   */
  private void addPidcAction(final PidcChangedAttrType attr, final String level, final Long pidcId, final Long varId,
      final Long sVarId)
      throws IcdmException {
    if (attr.getPidcAction() != null) {
      addToResultset(attr, level, "PIDC Action - VCDM Transfer", null, attr.getPidcAction(), null, null, pidcId, varId,
          sVarId);
    }
  }

  /**
   * @param attr
   * @param level
   * @param pidcId
   * @param varId
   * @param sVarId
   * @param fromSub
   * @throws IcdmException
   */
  private void addLevel(final PidcChangedAttrType attr, final String level, final Long pidcId, final Long varId,
      final Long sVarId, final boolean fromSub)
      throws IcdmException {
    if (!CommonUtils.isEmptyString(attr.getLevel())) {
      if (fromSub && "Moved to Variant level".equals(attr.getLevel())) {
        attr.setLevel("Moved to Sub-Variant level");
      }
      else if (fromSub && "Moved from variant level".equals(attr.getLevel())) {
        attr.setLevel("Moved from Sub-Variant level");
      }
      addToResultset(attr, level, "Maintanence", null, attr.getLevel(), null, null, pidcId, varId, sVarId);
    }

  }

  private boolean equalsValue(final PidcChangedAttrType attr) {
    return CommonUtils.isEqual(attr.getNewValueId(), attr.getOldValueId());
  }

  private void addValue(final PidcChangedAttrType attr, final String level, final Long pidcId, final Long varId,
      final Long sVarId)
      throws IcdmException {
    if (!equalsValue(attr)) {

      this.logger.debug(
          "AddingValue Change on Level: " + level + "; PIDC-ID/Var-ID/SVar-ID: " + pidcId + "/" + varId + "/" + sVarId);

      AttributeValue oldValue = super.getAttrValue(attr.getOldValueId());
      AttributeValue newValue = super.getAttrValue(attr.getNewValueId());
      String oldValueStr;
      if (CommonUtils.isNotNull(this.language) && "GERMAN".equalsIgnoreCase(this.language)) {
        String oldVal;
        if (null != oldValue) {
          oldVal = oldValue.getValueGer() == null ? oldValue.getValueEng() : oldValue.getValueGer();
        }
        else {
          oldVal = "";
        }
        oldValueStr = oldVal;
      }
      else {
        oldValueStr = oldValue == null ? "" : oldValue.getValueEng();
      }

      String newValueStr;
      if (CommonUtils.isNotNull(newValue)) {
        if (CommonUtils.isNotNull(this.language) && "GERMAN".equalsIgnoreCase(this.language)) {
          newValueStr = newValue.getValueGer() == null ? newValue.getValueEng() : newValue.getValueGer();
        }
        else {
          newValueStr = newValue.getValueEng() == null ? "" : newValue.getValueEng();
        }
      }

      else {
        newValueStr = "";
      }

      addToResultset(attr, level, "Attribute Value", oldValueStr, newValueStr, null, null, pidcId, varId, sVarId);
    }
  }

  private boolean equalsDescription(final PidcChangedAttrType attr) {
    return equalsAttr(attr.getNewDesc(), attr.getOldDesc());
  }


  private void addDescription(final PidcChangedAttrType attr, final String level, final Long pidcId, final Long varId,
      final Long sVarId)
      throws IcdmException {
    if (!equalsDescription(attr)) {
      addToResultset(attr, level, "Description", attr.getOldDesc(), attr.getNewDesc(), null, null, pidcId, varId,
          sVarId);
    }
  }


  private boolean equalsPartNumber(final PidcChangedAttrType attr) {
    return equalsAttr(attr.getNewPartNumber(), attr.getOldPartNumber());
  }

  private void addPartNumber(final PidcChangedAttrType attr, final String level, final Long pidcId, final Long varId,
      final Long sVarId)
      throws IcdmException {
    if (!equalsPartNumber(attr)) {
      addToResultset(attr, level, "Part Number", attr.getOldPartNumber(), attr.getNewPartNumber(), null, null, pidcId,
          varId, sVarId);
    }
  }

  private boolean equalsSpecLink(final PidcChangedAttrType attr) {
    return equalsAttr(attr.getNewSpecLink(), attr.getOldSpecLink());
  }

  private void addSpecLink(final PidcChangedAttrType attr, final String level, final Long pidcId, final Long varId,
      final Long sVarId)
      throws IcdmException {
    if (!equalsSpecLink(attr)) {
      addToResultset(attr, level, "Spec. Link", attr.getOldSpecLink(), attr.getNewSpecLink(), null, null, pidcId, varId,
          sVarId);
    }
  }

  private boolean equalsUsed(final PidcChangedAttrType attr) {
    return equalsAttr(attr.getNewUsed(), attr.getOldUsed());
  }

  // ICDM-2279
  private boolean equalsFocusMatrix(final PidcChangedAttrType attr) {
    return equalsAttr(attr.getNewFocusMatrix(), attr.getOldFocusMatrix());
  }

  // ICDM-2279
  private boolean equalsTransferVcdm(final PidcChangedAttrType attr) {
    return equalsAttr(attr.getNewTranferVcdm(), attr.getOldTranferVcdm());
  }

  private void addUsed(final PidcChangedAttrType attr, final String level, final Long pidcId, final Long varId,
      final Long sVarId)
      throws IcdmException {
    if (!equalsUsed(attr)) {
      addToResultset(attr, level, "Used Flag", attr.getOldUsed(), attr.getNewUsed(), null, null, pidcId, varId, sVarId);
    }
  }

  // ICDM-2279
  private void addFocusMatrix(final PidcChangedAttrType attr, final String level, final Long pidcId, final Long varId,
      final Long sVarId)
      throws IcdmException {
    if (!equalsFocusMatrix(attr)) {
      addToResultset(attr, level, "Focus Matrix Flag", attr.getOldFocusMatrix(), attr.getNewFocusMatrix(), null, null,
          pidcId, varId, sVarId);
    }
  }

  // ICDM-2279
  private void addTransferVcdm(final PidcChangedAttrType attr, final String level, final Long pidcId, final Long varId,
      final Long sVarId)
      throws IcdmException {
    if (!equalsTransferVcdm(attr)) {
      addToResultset(attr, level, "Transfer to vCDM Flag", attr.getOldTranferVcdm(), attr.getNewTranferVcdm(), null,
          null, pidcId, varId, sVarId);
    }
  }

  private boolean equalsClearingStatus(final PidcChangedAttrType attr) {
    return equalsAttr(attr.getNewValueIdClearingStatus(), attr.getOldValueIdClearingStatus());
  }

  private void addClearingStatus(final PidcChangedAttrType attr, final String level, final Long pidcId,
      final Long varId, final Long sVarId)
      throws IcdmException {
    if (!equalsClearingStatus(attr) && equalsLong(attr.getOldValueId(), attr.getNewValueId())) {
      addToResultset(attr, level, "Clearing Status", attr.getOldValueIdClearingStatus(),
          attr.getNewValueIdClearingStatus(), null, null, pidcId, varId, sVarId);
    }
  }

  private boolean equalsAttr(final String oldValue, final String newValue) {
    // Both null
    if ((oldValue == null) && (newValue == null)) {
      return true;
    }
    else if ((oldValue == null) || (newValue == null)) {
      return false;
    }
    else {
      return oldValue.equalsIgnoreCase(newValue);
    }
  }

  private boolean equalsLong(final Long oldValueId, final Long newValueId) {
    // Both null
    if ((oldValueId == null) && (newValueId == null)) {
      return true;
    }
    else if ((oldValueId == null) || (newValueId == null)) {
      return false;
    }
    else {
      return oldValueId.equals(newValueId);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int prime = 31;
    int result = super.hashCode();
    result = (prime * result) + ((this.resultSet == null) ? 0 : this.resultSet.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (null == obj) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    IcdmPidcLogOutputNew other = (IcdmPidcLogOutputNew) obj;
    if (this.resultSet == null) {
      if (other.resultSet != null) {
        return false;
      }
    }
    else if (!this.resultSet.equals(other.resultSet)) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "IcdmPidcLogOutput [resultSet=" + this.resultSet + ", diff=" + this.diff + "]";
  }
}
