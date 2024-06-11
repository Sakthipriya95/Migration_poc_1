/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.caltool.apic.ui.editors.compare.PidcNattableRowObject;
import com.bosch.caltool.apic.ui.editors.pages.PIDCAttrPage;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.uc.IUseCaseItem;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author mga1cob
 */
// ICDM-107
public class PIDCPageToolBarFilters extends AbstractViewerFilter {

  /**
   * Defines pidc attribute not defined flag filter is selected or not
   */
  // ICDM-278
  private boolean attrUsedNotDefined = true /* By default this flag will switched on */;
  /**
   * Defines pidc attribute not used flag filter is selected or not
   */
  private boolean attrNotUsedFlag = true /* By default this flag will switched on */;
  /**
   * Defines pidc attribute used flag filter is selected or not
   */
  private boolean attrUsedFlag = true /* By default this flag will switched on */;
  /**
   * Defines pidc attribute value defined filter is selected or not
   */
  private boolean definedSel = true /* By default this flag will switched on */;
  /**
   * Defines pidc attribute value not defined filter is selected or not
   */
  private boolean notDefinedSel = true /* By default this flag will switched on */;
  /**
   * Defines pidc attribute variant filter is selected or not
   */
  private boolean variantSel = true /* By default this flag will switched on */;
  /**
   * Defines pidc attribute non-variant filter is selected or not
   */
  private boolean nonVariantSel = true /* By default this flag will switched on */;
  /**
   * Defines pidc attribute non-mandatory filter is selected or not
   */
  private boolean attrNonMandatorySel = true /* By default this flag will switched on */;
  /**
   * Defines pidc attribute mandatory filter is selected or not
   */
  private boolean attrMandatorySel = true /* By default this flag will switched on */;

  /**
   * Defines pidc attribute Dependent filter is selected or not
   */
  private boolean attrDepen = true;/* By default this flag will switched on */
  /**
   * Defines pidc attribute Dependent filter is selected or not
   */
  private boolean attrNonDep = true;/* By default this flag will switched on */
  private boolean attrValNotClr = true;/* By default this flag will switched on */
  private boolean attrValClr = true;/* By default this flag will switched on */
  private boolean structSel = true;/* By default this flag will switched on */
  private boolean nonStructSel = true;/* By default this flag will switched on */
  private boolean allAttrVisible = true; /* By default this flag will be switched on */
  private boolean newAttr = true;/* By default this flag will switched on */
  // ICDM-2625
  private boolean attrGrouped = true;/* By default this flag will switched on */
  private boolean attrNotGrouped = true;/* By default this flag will switched on */

  private boolean usecaseAndMantoryAttr = true;

  private boolean nonUsecaseAndMantoryAttr = true;

  private boolean quotationRelevantUcAttr = true;

  private boolean quotationNotRelevantUcAttr = true;

  private AbstractProjectObjectBO projectBO;

  private final PIDCAttrPage pidcAttrPage;

  /**
   * @param projectObject Project Object
   * @param pidcAttrPage as input for filtering
   */
  public PIDCPageToolBarFilters(final IProjectObject projectObject, final PIDCAttrPage pidcAttrPage) {
    super();
    this.projectBO = pidcAttrPage.getProjectObjectBO();
    this.pidcAttrPage = pidcAttrPage;
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFilterText(final String filterText) {
    // ICDM-278
    setFilterText(filterText, false);
  }


  private class PIDCToolBarParamMatcher<E> implements Matcher<E> {

    /** Singleton instance of TrueMatcher. */


    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof PidcNattableRowObject) {
        return selectElement(element);
      }
      return true;
    }
  }

  /**
   * @return Matcher
   */
  public Matcher getToolBarMatcher() {
    return new PIDCToolBarParamMatcher<PidcNattableRowObject>();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {

    PidcNattableRowObject rowObject = (PidcNattableRowObject) element;

    if (!filterQuotRelevantAttributes(rowObject)) {
      return false;
    }
    if (!filterAllProjectUsecaseAndMandatoryAttributes(rowObject)) {
      return false;
    }
    if (!filterUsedFlagVal(rowObject)) {
      return false;
    }
    if (!filterNotDefFlagVal(rowObject)) {
      return false;
    }
    if (!filterDefinedVal(rowObject)) {
      return false;
    }
    if (!filterVariantVal(rowObject)) {
      return false;
    }
    if (!filterRefDepVal(rowObject)) {
      return false;
    }


    if (!filterMandatValues(rowObject)) {
      return false;
    }
    if (!isAllfiltersOn()) {
      return false;
    }

    if (!filterStructAttrValues(rowObject)) {
      return false;
    }
    if (!filterInvisibleAttributes(rowObject)) {
      return false;
    }
    if (!filterNotClearedValues(rowObject)) {
      return false;
    }
    // ICDM-2625
    if (!isAttrGrouped(rowObject)) {
      return false;
    }
    return true;
  }


  /**
   * @param pidcAttr
   */
  private boolean filterNotClearedValues(final PidcNattableRowObject compareRowObject) {

    // Filter condition for Not cleared Values
    if (!isAttrValNotClr()) {
      if (compareRowObject.getProjectAttributeHandler().getProjectAttr().isAtChildLevel() &&
          checkInVariants(compareRowObject.getProjectAttributeHandler().getProjectAttr())) {
        return false;
      }
      if (compareRowObject.getProjectAttributeHandler().isValueInvalid()) {
        return false;
      }
    }
    // Filter condition for cleared Values

    if (!isAttrValClr()) {
      if (compareRowObject.getProjectAttributeHandler().getProjectAttr().isAtChildLevel()) {
        return checkInVariants(compareRowObject.getProjectAttributeHandler().getProjectAttr());
      }
      if ((compareRowObject.getProjectAttributeHandler().getProjectAttr().getValueId() == null) ||
          (!compareRowObject.getProjectAttributeHandler().isValueInvalid())) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param pidcAttr
   */
  private boolean checkInVariants(final IProjectAttribute pidcAttr) {
    for (PidcVariant varObj : this.projectBO.getPidcDataHandler().getVariantMap().values()) {
      PidcVariantBO variantHandler =
          new PidcVariantBO(this.projectBO.getPidcDataHandler().getPidcVersionInfo().getPidcVersion(), varObj,
              this.projectBO.getPidcDataHandler());

      PidcVariantAttribute varAttr =
          this.projectBO.getPidcDataHandler().getVariantAttributeMap().get(varObj.getId()).get(pidcAttr.getAttrId());

      if ((null != varAttr) && varAttr.isAtChildLevel()) {

        for (PidcSubVariant subVarObj : this.projectBO.getPidcDataHandler().getSubVariantMap().values()) {

          PidcSubVariantBO subVarHandler =
              new PidcSubVariantBO(this.projectBO.getPidcDataHandler().getPidcVersionInfo().getPidcVersion(), subVarObj,
                  this.projectBO.getPidcDataHandler());

          PidcSubVariantAttribute subVarAttr = this.projectBO.getPidcDataHandler().getSubVariantAttributeMap()
              .get(subVarObj.getId()).get(varAttr.getAttrId());

          if ((null != subVarAttr) && (new PidcSubVariantAttributeBO(subVarAttr, subVarHandler).isValueInvalid())) {
            return true;
          }
        }
      }
      else if ((null != varAttr) && (new PidcVariantAttributeBO(varAttr, variantHandler).isValueInvalid())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param nattableRowObject
   * @return true if nattableRowObject is part of any project use case or is a mandatory attribute
   */
  private boolean filterAllProjectUsecaseAndMandatoryAttributes(final PidcNattableRowObject nattableRowObject) {

    if (!this.usecaseAndMantoryAttr && isMappedToUCOrMandatory(nattableRowObject)) {
      return false;
    }

    if (!this.nonUsecaseAndMantoryAttr && !isMappedToUCOrMandatory(nattableRowObject)) {
      return false;
    }
    return true;
  }

  /**
   * @param rowObject
   * @return true if nattableRowObject is part of any project use case and is quotation relevant
   */
  private boolean filterQuotRelevantAttributes(final PidcNattableRowObject rowObject) {
    IUseCaseItemClientBO ucItemBo = this.pidcAttrPage.getUcItem();
    // case if UC items are not selected in outline view
    if (!this.pidcAttrPage.isUcItemSelected()) {
      if (!this.quotationRelevantUcAttr && isQuotationRelevant(rowObject)) {
        return false;
      }
      if (!this.quotationNotRelevantUcAttr && !isQuotationRelevant(rowObject)) {
        return false;
      }
    } // case if UC items are selected in outline view
    else {
      if (!this.quotationRelevantUcAttr && !this.quotationNotRelevantUcAttr) {
        return false;
      }
      if (!this.quotationRelevantUcAttr) {
        return !filterQuotationRelevantViaOutlineSelection(ucItemBo, rowObject);
      }
      if (!this.quotationNotRelevantUcAttr) {
        return filterQuotationRelevantViaOutlineSelection(ucItemBo, rowObject);
      }
    }

    return true;
  }

  /**
   * @param ucItemBo
   */
  private boolean filterQuotationRelevantViaOutlineSelection(final IUseCaseItemClientBO ucItemBo,
      final PidcNattableRowObject rowObject) {
    IUseCaseItem ucItem = ucItemBo.getUcItem();
    Map<Long, Set<Long>> quotationRelevantUcAttrIdMap =
        this.projectBO.getPidcDataHandler().getProjectUsecaseModel().getQuotationRelevantUcAttrIdMap();
    if (ucItem instanceof UseCaseSection) {
      UseCaseSection useCaseSection = (UseCaseSection) ucItem;
      UseCaseSectionClientBO usecaseClientBo = this.pidcAttrPage.getUseCaseSectionClientBo();
      SortedSet<UseCaseSectionClientBO> useCaseSectionSet = usecaseClientBo.getChildSectionSet(true);
      return checkUsecaseSectionInQuotationRelAttrMap(rowObject, quotationRelevantUcAttrIdMap, useCaseSection,
          useCaseSectionSet);
    }
    else if (ucItem instanceof UseCase) {
      UseCase useCase = (UseCase) ucItem;
      return checkUsecaseInQuotationRelMap(rowObject, quotationRelevantUcAttrIdMap, useCase.getId(),
          this.pidcAttrPage.getUseCaseClientBo());
    }
    else if (ucItem instanceof UseCaseGroup) {
      UseCaseGroupClientBO caseGroupClientBO = this.pidcAttrPage.getUseCaseGroupClientBo();

      if (CommonUtils.isNotEmpty(caseGroupClientBO.getChildUcgBOSet())) {
        Set<UseCaseGroupClientBO> caseGroupClientBOSet = new HashSet<>(caseGroupClientBO.getChildUcgBOSet());
        for (UseCaseGroupClientBO caseGroupClientBOLcl : caseGroupClientBOSet) {
          if (checkQuotationRelevantForUsecaseCliBo(
              new HashSet<UsecaseClientBO>(caseGroupClientBOLcl.getChildUcBOSet()), quotationRelevantUcAttrIdMap,
              rowObject)) {
            return true;
          }
        }
      }
      else if (CommonUtils.isNotEmpty(caseGroupClientBO.getChildUcBOSet())) {
        return checkQuotationRelevantForUsecaseCliBo(new HashSet<UsecaseClientBO>(caseGroupClientBO.getChildUcBOSet()),
            quotationRelevantUcAttrIdMap, rowObject);
      }
    }
    return false;
  }

  /**
   * @param rowObject
   * @param quotationRelevantUcAttrIdMap
   * @param useCaseSection
   * @param useCaseSectionSet
   */
  private boolean checkUsecaseSectionInQuotationRelAttrMap(final PidcNattableRowObject rowObject,
      final Map<Long, Set<Long>> quotationRelevantUcAttrIdMap, final UseCaseSection useCaseSection,
      final SortedSet<UseCaseSectionClientBO> useCaseSectionSet) {
    if (CommonUtils.isNotEmpty(useCaseSectionSet)) {
      for (UseCaseSectionClientBO useCaseSectionClientBO : useCaseSectionSet) {
        if (checkUsecaseSectionInQuotationRelAttrMap(rowObject, quotationRelevantUcAttrIdMap,
            useCaseSectionClientBO.getUseCaseSection(), useCaseSectionClientBO.getChildSectionSet(true))) {
          return true;
        }
      }
    }
    else {
      return checkQuotationRelUcAttrMap(quotationRelevantUcAttrIdMap, rowObject, useCaseSection.getId());
    }
    return false;
  }

  /**
   * @param rowObject
   * @param quotationRelevantUcAttrIdMap
   * @param useCase
   * @param caseClientBO
   */
  private boolean checkUsecaseInQuotationRelMap(final PidcNattableRowObject rowObject,
      final Map<Long, Set<Long>> quotationRelevantUcAttrIdMap, final long usecaseId,
      final UsecaseClientBO caseClientBO) {
    SortedSet<UseCaseSectionClientBO> useCaseSectionSet = caseClientBO.getUseCaseSectionSet(false);
    if (CommonUtils.isNotEmpty(useCaseSectionSet)) {
      for (UseCaseSectionClientBO useCaseSectionClientBO : useCaseSectionSet) {
        if (checkUsecaseSectionInQuotationRelAttrMap(rowObject, quotationRelevantUcAttrIdMap,
            useCaseSectionClientBO.getUseCaseSection(), useCaseSectionClientBO.getChildSectionSet(true))) {
          return true;
        }
      }
    }
    else {
      return checkQuotationRelUcAttrMap(quotationRelevantUcAttrIdMap, rowObject, usecaseId);
    }
    return false;
  }

  private boolean checkQuotationRelevantForUsecaseCliBo(final Set<UsecaseClientBO> caseClientBOSet,
      final Map<Long, Set<Long>> quotationRelevantUcAttrIdMap, final PidcNattableRowObject rowObject) {
    for (UsecaseClientBO useCaseClientBo : caseClientBOSet) {
      if (checkUsecaseInQuotationRelMap(rowObject, quotationRelevantUcAttrIdMap, useCaseClientBo.getUseCase().getId(),
          useCaseClientBo)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param quotationRelevantUcAttrIdMap
   * @param rowObject
   * @param useCaseClientBo
   */
  private boolean checkQuotationRelUcAttrMap(final Map<Long, Set<Long>> quotationRelevantUcAttrIdMap,
      final PidcNattableRowObject rowObject, final long useCaseId) {
    if (quotationRelevantUcAttrIdMap.containsKey(useCaseId)) {
      Set<Long> attrSet = quotationRelevantUcAttrIdMap.get(useCaseId);
      return attrSet.contains(rowObject.getAttribute().getId());
    }
    return false;
  }


  private boolean isQuotationRelevant(final PidcNattableRowObject rowObject) {
    return this.projectBO.getPidcDataHandler().getProjectUsecaseModel().getQuotationRelevantUcAttrIdSet()
        .contains(rowObject.getAttribute().getId());
  }

  private boolean isMappedToUCOrMandatory(final PidcNattableRowObject nattableRowObject) {
    return this.projectBO.getPidcDataHandler().getProjectUsecaseModel().getProjectUsecaseAttrIdSet().contains(
        nattableRowObject.getAttribute().getId()) || nattableRowObject.getProjectAttributeHandler().isMandatory();
  }

  /**
   * @param pidcAttr
   * @return
   */
  private boolean filterStructAttrValues(final PidcNattableRowObject compareRowObject) {

    if (!this.structSel &&
        ((compareRowObject.getProjectAttributeHandler().getProjectAttr() instanceof PidcSubVariantAttribute) ||
            (compareRowObject.getProjectAttributeHandler().getProjectAttr() instanceof PidcVariantAttribute)) &&
        (compareRowObject.getProjectAttributeHandler().isStructuredAttr())) {
      return false;
    }
    if (!this.nonStructSel &&
        ((compareRowObject.getProjectAttributeHandler().getProjectAttr() instanceof PidcSubVariantAttribute) ||
            (compareRowObject.getProjectAttributeHandler().getProjectAttr() instanceof PidcVariantAttribute)) &&
        (!compareRowObject.getProjectAttributeHandler().isStructuredAttr())) {
      return false;
    }
    return true;
  }


  /**
   * @param pidcAttr
   */
  private boolean filterVariantVal(final PidcNattableRowObject compareRowObject) {
    // If the PIDC attribute non variant filter is selected
    if (!this.nonVariantSel && !compareRowObject.getProjectAttributeHandler().getProjectAttr().isAtChildLevel()) {
      return false;
    }
    // If the PIDC attribute variant filter is selected
    if (!this.variantSel && compareRowObject.getProjectAttributeHandler().getProjectAttr().isAtChildLevel()) {
      return false;
    }
    return true;
  }


  /**
   * @param pidcAttr
   */
  private boolean filterDefinedVal(final PidcNattableRowObject compareRowObject) {

    // is Defined condition needs to be checked only at the selected level - pidc/var/subvar
    boolean isValueDefined = compareRowObject.getProjectAttributeHandler()
        .isDefined(compareRowObject.getProjectAttributeHandler().getProjectAttr());


    // If the PIDC attribute value not defined filter is selected
    if (!this.notDefinedSel && !isValueDefined) {
      return false;
    }
    // If the PIDC attribute value defined filter is selected
    if (!this.definedSel && isValueDefined) {
      return false;
    }
    return true;
  }


  /**
   * @param Compare Row Object
   */
  private boolean filterUsedFlagVal(final PidcNattableRowObject compareRowObject) {
    // If the PIDC attribute used filter is selected
    if (!isAttrUsedFlag() && ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType()
        .equals(compareRowObject.getProjectAttributeHandler().getIsUsed())) {
      return false;
    }
    // If the PIDC attribute not used filter is selected
    if (!isAttrNotUsedFlag() && ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType()
        .equals(compareRowObject.getProjectAttributeHandler().getIsUsed())) {
      return false;
    }

    return true;
  }


  /**
   * @param attr
   */
  private boolean filterMandatValues(final PidcNattableRowObject compareRowObject) {
    // If the PIDC attribute mandatory filter is selected
    if (!isAttrMandatorySel() && compareRowObject.getProjectAttributeHandler().isMandatory()) {
      return false;
    }
    // If the PIDC attribute not mandatory filter is selected
    if (!isAttrNonMandatorySel() && !compareRowObject.getProjectAttributeHandler().isMandatory()) {
      return false;
    }
    return true;
  }

  // ICDM-2625
  /**
   * @param compareRowObject
   * @return
   */
  private boolean isAttrGrouped(final PidcNattableRowObject compareRowObject) {

    if (!isAttrGrouped() && (compareRowObject.getAttribute().isGroupedAttr())) {
      return false;
    }
    if (!isAttrNotGrouped() && !(compareRowObject.getAttribute().isGroupedAttr())) {
      return false;
    }
    return true;
  }


  /**
   * @param pidcAttr
   * @return true by default and false if the action is unselected.
   */
  private boolean filterNotDefFlagVal(final PidcNattableRowObject compareRowObject) {

    // If the PIDC attribute not defined filter is selected
    if (!isAttrUsedNotDefined() && ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()
        .equals(compareRowObject.getProjectAttributeHandler().getIsUsed())) {
      return false;
    }
    // If the PIDC attribute not defined filter is selected
    if (!this.newAttr && ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getUiType()
        .equals(compareRowObject.getProjectAttributeHandler().getIsUsed())) {
      return false;
    }
    return true;
  }

  /**
   *
   */
  private boolean isAllfiltersOn() {
    // If the PIDC attribute all filters are off
    return !(validateUsedFlag() && validateSel() && validateVarSel() && validateBoolean());
  }

  /**
   * @return
   */
  private boolean validateBoolean() {
    return !isAttrMandatorySel() && !isAttrNonMandatorySel();
  }

  /**
   * @return
   */
  private boolean validateVarSel() {
    return !this.nonVariantSel && !this.variantSel;
  }

  /**
   * @return
   */
  private boolean validateSel() {
    return !isAttrUsedNotDefined() && !this.notDefinedSel && !this.definedSel;
  }

  /**
   * @return
   */
  private boolean validateUsedFlag() {
    return !isAttrUsedFlag() && !isAttrNotUsedFlag();
  }

  /**
   * filter Reference
   *
   * @param pidcAttr
   */
  private boolean filterRefDepVal(final PidcNattableRowObject compareRowObj) {

    // Predefined filter for attributes which are controlling the Visibility
    if (!isAttrDepen() && (compareRowObj.getProjectAttributeHandler().hasAttrDependencies())) {// icdm-1604
      return false;
    }

    // Icdm-478 Predefined filter for attributes which are controlling the Visibility
    if (!isAttrNonDep() && !(compareRowObj.getProjectAttributeHandler().hasAttrDependencies())) {// icdm-1604
      return false;
    }
    return true;
  }


  /**
   * @param pidcAttr
   */
  private boolean filterInvisibleAttributes(final PidcNattableRowObject compareRowObject) {
    if (!isAllAttrVisible()) {
      Set<IProjectAttribute> iprojectAttrs = compareRowObject.getColumnDataMapper().getIpidcAttrs();
      boolean isAllIPIDCAttrVisible = false;
      for (IProjectAttribute iprojectAttr : iprojectAttrs) {
        if (isVisible(iprojectAttr.getAttrId())) {
          isAllIPIDCAttrVisible = true;
          break;
        }
      }
      return isAllIPIDCAttrVisible;
    }
    return true;
  }

  /**
   * @param attrId
   * @return
   */
  private boolean isVisible(final Long attrId) {
    if (this.projectBO instanceof PidcVersionBO) {
      return !this.projectBO.getPidcDataHandler().getPidcVersInvisibleAttrSet().contains(attrId);
    }
    else if (this.projectBO instanceof PidcVariantBO) {
      Set<Long> set = this.projectBO.getPidcDataHandler().getVariantInvisbleAttributeMap()
          .get(this.projectBO.getProjectObject().getId());
      if (null != set) {
        return !set.contains(attrId);
      }
    }
    else if (this.projectBO instanceof PidcSubVariantBO) {
      return !this.projectBO.getPidcDataHandler().getSubVariantInvisbleAttributeMap()
          .get(this.projectBO.getProjectObject().getId()).contains(attrId);
    }
    return true;
  }


  /**
   * @param notDefinedSel the notDefinedSel to set
   */
  public void setNotDefinedSel(final boolean notDefinedSel) {
    this.notDefinedSel = notDefinedSel;
  }

  /**
   * @return the notDefinedSel
   */
  public boolean isNotDefinedSel() {
    return this.notDefinedSel;
  }

  /**
   * @param nonVariantSel the nonVariantSel to set
   */
  public void setNonVariantSel(final boolean nonVariantSel) {
    this.nonVariantSel = nonVariantSel;
  }

  /**
   * @return the nonVariantSel
   */
  public boolean isNonVariantSel() {
    return this.nonVariantSel;
  }


  /**
   * @param attrNotDefinedFlag the attrNotDefinedFlag to set
   */
  public void setAttrNotDefinedFlag(final boolean attrNotDefinedFlag) {
    setAttrUsedNotDefined(attrNotDefinedFlag);
  }

  /**
   * @return the attrNotUsedFlag
   */
  public boolean isAttrNotUsedFlag() {
    return this.attrNotUsedFlag;
  }

  /**
   * @param attrNotUsedFlag the attrNotUsedFlag to set
   */
  public void setAttrNotUsedFlag(final boolean attrNotUsedFlag) {
    this.attrNotUsedFlag = attrNotUsedFlag;
  }

  /**
   * @return the attrUsedFlag
   */
  public boolean isAttrUsedFlag() {
    return this.attrUsedFlag;
  }

  /**
   * @param attrUsedFlag the attrUsedFlag to set
   */
  public void setAttrUsedFlag(final boolean attrUsedFlag) {
    this.attrUsedFlag = attrUsedFlag;
  }

  /**
   * @return the attrUsedNotDefined
   */
  public boolean isAttrUsedNotDefined() {
    return this.attrUsedNotDefined;
  }

  /**
   * @param attrUsedNotDefined the attrUsedNotDefined to set
   */
  public void setAttrUsedNotDefined(final boolean attrUsedNotDefined) {
    this.attrUsedNotDefined = attrUsedNotDefined;
  }

  /**
   * @return the attrMandatorySel
   */
  public boolean isAttrMandatorySel() {
    return this.attrMandatorySel;
  }

  /**
   * @param attrMandatorySel the attrMandatorySel to set
   */
  public void setAttrMandatorySel(final boolean attrMandatorySel) {
    this.attrMandatorySel = attrMandatorySel;
  }

  /**
   * @return the definedSel
   */
  // ICDM-278
  public boolean isDefinedSel() {
    return this.definedSel;
  }

  /**
   * @param definedSel the definedSel to set
   */
  // ICDM-278
  public void setDefinedSel(final boolean definedSel) {
    this.definedSel = definedSel;
  }

  /**
   * @return the variantSel
   */
  // ICDM-278
  public boolean isVariantSel() {
    return this.variantSel;
  }

  /**
   * @param variantSel the variantSel to set
   */
  // ICDM-278
  public void setVariantSel(final boolean variantSel) {
    this.variantSel = variantSel;
  }

  /**
   * @return the attrNonMandatorySel
   */
  public boolean isAttrNonMandatorySel() {
    return this.attrNonMandatorySel;
  }

  /**
   * @param attrNonMandatorySel the attrNonMandatorySel to set
   */
  public void setAttrNonMandatorySel(final boolean attrNonMandatorySel) {
    this.attrNonMandatorySel = attrNonMandatorySel;
  }

  // Icdm-478 Predefined filter for attributes which are controlling the Visibility
  /**
   * @param attrDepen attrDepen
   */
  public void setAttrDependency(final boolean attrDepen) {
    this.attrDepen = attrDepen;

  }

  // Icdm-478 Predefined filter for attributes which are controlling the Visibility
  /**
   * @return the attrDepen
   */
  public boolean isAttrDepen() {
    return this.attrDepen;
  }

  /**
   * @param attrNonDep attrNonDep
   */
  public void setAttrNonDep(final boolean attrNonDep) {
    this.attrNonDep = attrNonDep;

  }


  /**
   * @return the attrNonDep
   */
  public boolean isAttrNonDep() {
    return this.attrNonDep;
  }


  /**
   * Icdm-832 set the Not clear value filter
   *
   * @param attrValNotClr attrValNotClr
   */
  public void setNotClearSel(final boolean attrValNotClr) {
    this.attrValNotClr = attrValNotClr;

  }

  /**
   * Icdm-832 set the clear value filter
   *
   * @param attrValClr attrValClr
   */
  public void setClearSel(final boolean attrValClr) {
    this.attrValClr = attrValClr;

  }

  /**
   * @param structSel structSel action selected
   */
  public void setStructSel(final boolean structSel) {
    this.structSel = structSel;
  }

  /**
   * @param nonStructSel nonStructSel action selected
   */
  public void setNotStructSel(final boolean nonStructSel) {
    this.nonStructSel = nonStructSel;
  }

  /**
   * @param allAttrVisible allattr action selected
   */
  public void setAllAttrVisible(final boolean allAttrVisible) {
    this.allAttrVisible = allAttrVisible;
  }


  /**
   * @param newAttr flag
   */
  public void setNewAttrVisible(final boolean newAttr) {
    this.newAttr = newAttr;
  }

  // ICDM-2625
  /**
   * @return the attrGrouped
   */
  public boolean isAttrGrouped() {
    return this.attrGrouped;
  }

  // ICDM-2625
  /**
   * @param attrGrouped the attrGrouped to set
   */
  public void setAttrGrouped(final boolean attrGrouped) {
    this.attrGrouped = attrGrouped;
  }

  // ICDM-2625
  /**
   * @return the attrNotGrouped
   */
  public boolean isAttrNotGrouped() {
    return this.attrNotGrouped;
  }

  // ICDM-2625
  /**
   * @param attrNotGrouped the attrNotGrouped to set
   */
  public void setAttrNotGrouped(final boolean attrNotGrouped) {
    this.attrNotGrouped = attrNotGrouped;
  }

  /**
   * ICDM-479
   *
   * @return allattrvisible
   */
  public boolean isAllAttrVisible() {
    return this.allAttrVisible;
  }


  /**
   * @return the attrValClr
   */
  public boolean isAttrValClr() {
    return this.attrValClr;
  }

  /**
   * @return the attrValNotClr
   */
  public boolean isAttrValNotClr() {
    return this.attrValNotClr;
  }


  /**
   * @return the projectBO
   */
  public AbstractProjectObjectBO getProjectBO() {
    return this.projectBO;
  }


  /**
   * @param projectBO the projectBO to set
   */
  public void setProjectBO(final AbstractProjectObjectBO projectBO) {
    this.projectBO = projectBO;
  }


  /**
   * @return the usecaseAndMantoryAttr
   */
  public boolean isUsecaseAndMantoryAttr() {
    return this.usecaseAndMantoryAttr;
  }


  /**
   * @param usecaseAndMantoryAttr the usecaseAndMantoryAttr to set
   */
  public void setUsecaseAndMantoryAttr(final boolean usecaseAndMantoryAttr) {
    this.usecaseAndMantoryAttr = usecaseAndMantoryAttr;
  }


  /**
   * @return the nonUsecaseAndMantoryAttr
   */
  public boolean isNonUsecaseAndMantoryAttr() {
    return this.nonUsecaseAndMantoryAttr;
  }


  /**
   * @param nonUsecaseAndMantoryAttr the nonUsecaseAndMantoryAttr to set
   */
  public void setNonUsecaseAndMantoryAttr(final boolean nonUsecaseAndMantoryAttr) {
    this.nonUsecaseAndMantoryAttr = nonUsecaseAndMantoryAttr;
  }


  /**
   * @return the quotationRelevantUcAttr
   */
  public boolean isQuotationRelevantUcAttr() {
    return this.quotationRelevantUcAttr;
  }


  /**
   * @param quotationRelevantUcAttr the quotationRelevantUcAttr to set
   */
  public void setQuotationRelevantUcAttr(final boolean quotationRelevantUcAttr) {
    this.quotationRelevantUcAttr = quotationRelevantUcAttr;
  }


  /**
   * @return the quotationNotRelevantUcAttr
   */
  public boolean isQuotationNotRelevantUcAttr() {
    return this.quotationNotRelevantUcAttr;
  }


  /**
   * @param quotationNotRelevantUcAttr the quotationNotRelevantUcAttr to set
   */
  public void setQuotationNotRelevantUcAttr(final boolean quotationNotRelevantUcAttr) {
    this.quotationNotRelevantUcAttr = quotationNotRelevantUcAttr;
  }


}
