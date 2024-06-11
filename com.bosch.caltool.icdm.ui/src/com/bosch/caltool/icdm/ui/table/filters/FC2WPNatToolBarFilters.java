/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPMappingResult;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPRelvPTType;
import com.bosch.caltool.icdm.ui.editors.CompareFC2WPRowObject;
import com.bosch.caltool.icdm.ui.editors.pages.FC2WPNatFormPage;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author gge6cob
 */
public class FC2WPNatToolBarFilters extends AbstractViewerFilter {

  /**
   * Result isFcInSdom Flag initially True since the tool bar action is checked
   */
  private boolean isFcInSdomFlag = true;
  /**
   * Result isFcNotInSdom Flag initially True since the tool bar action is checked
   */
  private boolean isFcNotInSdomFlag = true;
  /**
   * Result isInICDM Flag initially True since the tool bar action is checked
   */
  private boolean isInICDMA2LFlag = true;
  /**
   * Result notInICDM Flag initially True since the tool bar action is checked
   */
  private boolean isNotInICDMA2LFlag = true;

  private boolean isDeletedFlag = true;
  private boolean isNotDeletedFlag = true;

  private boolean isContactAssigned = true;
  private boolean isContactNotAssigned = true;

  private boolean isWPAssigned = true;
  private boolean isWPNotAssigned = true;

  private boolean isRevelantPTtype = true;
  private boolean isNotRevelantPTtype = true;

  private Set<FC2WPRelvPTType> relevantPTtypeSet = null;
  private FC2WPMappingResult handler;

  private boolean isDifferent = true;
  private boolean isNotDifferent = true;

  private boolean isWpDifferent = true;
  private boolean isWpNotDifferent = true;

  private boolean isContactDifferent = true;
  private boolean isContactNotDifferent = true;

  private boolean isAgreedDifferent = true;
  private boolean isAgreedNotDifferent = true;

  private boolean isInIcdmA2l = true;
  private boolean isNotInIcdmA2l = true;
  private boolean hasRelevantPTType = true;
  private boolean hasNoRelevantPTType = true;

  private boolean isFcWithParams = true;
  private boolean isFcWithoutParams = true;


  /**
   * @return the isNotInIcdmA2l
   */
  public boolean isNotInIcdmA2l() {
    return this.isNotInIcdmA2l;
  }


  /**
   * @param isNotInIcdmA2l the isNotInIcdmA2l to set
   */
  public void setNotInIcdmA2l(final boolean isNotInIcdmA2l) {
    this.isNotInIcdmA2l = isNotInIcdmA2l;
  }


  /**
   * @return the hasNoRelevantPTType
   */
  public boolean isHasNoRelevantPTType() {
    return this.hasNoRelevantPTType;
  }


  /**
   * @param hasNoRelevantPTType the hasNoRelevantPTType to set
   */
  public void setHasNoRelevantPTType(final boolean hasNoRelevantPTType) {
    this.hasNoRelevantPTType = hasNoRelevantPTType;
  }


  /**
   * @return the relevantPTtypeSet
   */
  public Set<FC2WPRelvPTType> getRelevantPTtypeSet() {
    return this.relevantPTtypeSet;
  }


  /**
   * @param relevantPTtypeSet the relevantPTtypeSet to set
   */
  public void setRelevantPTtypeSet(final Set<FC2WPRelvPTType> relevantPTtypeSet) {
    this.relevantPTtypeSet = relevantPTtypeSet;
  }

  private final FC2WPNatFormPage fc2wpNatFormPage2;
  List<String> ptTypeList = new ArrayList<>();

  /**
   * Constructor.
   *
   * @param fc2wpNatFormPage2 the handler
   */
  public FC2WPNatToolBarFilters(final FC2WPNatFormPage fc2wpNatFormPage2) {
    super();
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT);
    this.fc2wpNatFormPage2 = fc2wpNatFormPage2;
  }


  /**
   *
   */
  private void getRelPTtypeList() {
    this.ptTypeList.clear();
    getRelevantPTtypeSet().forEach(t -> this.ptTypeList.add(t.getPtTypeName()));
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setFilterText(final String filterText) {
    setFilterText(filterText, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (FC2WPNatToolBarFilters.this.fc2wpNatFormPage2.isCompareEditor()) {
      CompareFC2WPRowObject compRowObj = (CompareFC2WPRowObject) element;
      if (!compareFC2WPRowObj(compRowObj)) {
        return false;
      }
    }
    else {
      if (element instanceof CompareFC2WPRowObject) {
        FC2WPNatToolBarFilters.this.handler =
            ((CompareFC2WPRowObject) element).getColumnDataMapper().getColumnIndexFC2WPMapResult().get(3);
        Map<String, FC2WPMapping> fc2wpVal = FC2WPNatToolBarFilters.this.fc2wpNatFormPage2.getMappingDetailsMap()
            .get(FC2WPNatToolBarFilters.this.handler);
        FC2WPMapping fnElement = fc2wpVal.get(((CompareFC2WPRowObject) element).getFuncName());
        if (null != getRelevantPTtypeSet()) {
          getRelPTtypeList();
        }
        if (!checkFnElement(fnElement)) {
          return false;
        }
      }
    }
    return true;
  }


  /**
   * @param fnElement
   */
  private boolean checkFnElement(final FC2WPMapping fnElement) {
    // check if the checkInICDMFlagflag filter matches
    if (!checkInICDMA2LFlag(fnElement)) {
      return false;
    }
    // check if the checkIsFcInSdomFlag filter matches
    if (!checkIsFcInSdomFlag(fnElement)) {
      return false;
    }
    // check if the checkIsDeleted filter matches
    if (!checkIsDeletedFlag(fnElement)) {
      return false;
    }

    // check if the checkIsWPAssigned filter matches
    if (!checkIsWPAssigned(fnElement)) {
      return false;
    }

    // check if the checkIsContactAssigned filter matches
    if (!checkIsContactAssigned(fnElement)) {
      return false;
    }

    // check if the checkIsRelevantPTtype filter matches
    if (!checkIsRelevantPTtype(fnElement)) {
      return false;
    }

    // check if the checkIsFcWithParams filter matches
    return checkIsFcWithParamsFlag(fnElement);
  }


  /**
   * @param compRowObj
   */
  private boolean compareFC2WPRowObj(final CompareFC2WPRowObject compRowObj) {
    if (!checkIsDifferent(compRowObj)) {
      return false;
    }
    if (!checkIsWPDifferent(compRowObj)) {
      return false;
    }
    if (!checkIsContactDifferent(compRowObj)) {
      return false;
    }
    if (!checkIsAgreedDifferent(compRowObj)) {
      return false;
    }
    if (!checkIsInIcdmA2l(compRowObj)) {
      return false;
    }
    if (!checkIsFcInSdom(compRowObj)) {
      return false;
    }
    if (!checkhasRelevantPTType(compRowObj)) {
      return false;
    }
    return checkIsFcWithParams(compRowObj);
  }


  /**
   * @param fnElement
   * @return
   */
  private boolean checkIsFcWithParamsFlag(final FC2WPMapping fnElement) {
    /**
     * isFcWithParams flag Action Unchecked then do not show fcs with parameters <br>
     * isFcWithoutParams flag Action Unchecked then do not show functions not having parameters
     */
    return !((!isFcWithParams() && fnElement.isFcWithParams()) ||
        (!isFcWithoutParams() && !fnElement.isFcWithParams()));
  }


  /**
   * @param compRowObj
   * @return
   */
  private boolean checkIsFcWithParams(final CompareFC2WPRowObject compRowObj) {
    return !((!isFcWithParams() && compRowObj.getIsFcWithParams()) ||
        (!isFcWithoutParams() && !compRowObj.getIsFcWithParams()));
  }


  /**
   * @param compRowObj
   * @return
   */
  private boolean checkIsFcInSdom(final CompareFC2WPRowObject compRowObj) {
    return !((!isFcInSdomFlag() && compRowObj.getIsFcInSdom()) ||
        (!isFcNotInSdomFlag() && !compRowObj.getIsFcInSdom()));
  }


  /**
   * @param compRowObj
   * @return
   */
  private boolean checkhasRelevantPTType(final CompareFC2WPRowObject compRowObj) {
    return !((!this.hasRelevantPTType && ((boolean) (compRowObj.hasRelevantPTType(this.relevantPTtypeSet)))) ||
        (!this.hasNoRelevantPTType && !((boolean) (compRowObj.hasRelevantPTType(this.relevantPTtypeSet)))));
  }


  /**
   * @param compRowObj
   * @return
   */
  private boolean checkIsInIcdmA2l(final CompareFC2WPRowObject compRowObj) {
    return !((!this.isInIcdmA2l && ((boolean) (compRowObj.getIsInIcdmA2l()))) ||
        (!this.isNotInIcdmA2l && !((boolean) (compRowObj.getIsInIcdmA2l()))));
  }


  /**
   * @param compRowObj
   * @return
   */
  private boolean checkIsAgreedDifferent(final CompareFC2WPRowObject compRowObj) {
    return !((!this.isAgreedDifferent && (boolean) (compRowObj.getIsAgreedDiff())) ||
        (!this.isAgreedNotDifferent && !(boolean) (compRowObj.getIsAgreedDiff())));
  }


  /**
   * @param compRowObj
   * @return
   */
  private boolean checkIsContactDifferent(final CompareFC2WPRowObject compRowObj) {
    return !((!this.isContactDifferent && ((boolean) (compRowObj.getContactDiff()))) ||
        (!this.isContactNotDifferent && !((boolean) (compRowObj.getContactDiff()))));
  }


  /**
   * @param compRowObj
   * @return
   */
  private boolean checkIsWPDifferent(final CompareFC2WPRowObject compRowObj) {
    return !((!this.isWpDifferent && ((boolean) (compRowObj.getWpDiff()))) ||
        (!this.isWpNotDifferent && !((boolean) (compRowObj.getWpDiff()))));
  }


  /**
   * @param compRowObj
   * @return
   */
  private boolean checkIsDifferent(final CompareFC2WPRowObject compRowObj) {
    return !((!this.isDifferent && ((boolean) (compRowObj.getComputedIsDiff()))) ||
        (!this.isNotDifferent && !((boolean) (compRowObj.getComputedIsDiff()))));
  }


  /**
   * @param fnElement
   * @return
   */
  private boolean checkIsRelevantPTtype(final FC2WPMapping fnElement) {

    /**
     * isRevelantPTtype flag Action Unchecked then do not show items that have relevant PT-Type <br>
     * isRevelantPTtype flag Action Unchecked then do not show items that have irrelevant PT-Type
     */
    return !((!this.isRevelantPTtype &&
        this.handler.getIsRelevantPTtype(fnElement.getFunctionName(), this.ptTypeList)) ||
        (!isNotRevelantPTtype() && !this.handler.getIsRelevantPTtype(fnElement.getFunctionName(), this.ptTypeList)));
  }

  /**
   * @return the isContactAssigned
   */
  public boolean isContactAssigned() {
    return this.isContactAssigned;
  }


  /**
   * @return the isContactNotAssigned
   */
  public boolean isContactNotAssigned() {
    return this.isContactNotAssigned;
  }

  /**
   * @param fnElement
   * @return
   */
  private boolean checkIsContactAssigned(final FC2WPMapping fnElement) {
    /**
     * isContactNotAssigned flag Action Unchecked then do not show items that have valid contacts <br>
     * isContactNotAssigned flag Action Unchecked then do not show items that have empty contacts
     */
    return !((!this.isContactAssigned && this.handler.getIsContactAssigned(fnElement.getFunctionName())) ||
        (!isContactNotAssigned() && !this.handler.getIsContactAssigned(fnElement.getFunctionName())));
  }

  /**
   * @param fnElement
   * @return
   */
  private boolean checkIsFcInSdomFlag(final FC2WPMapping fnElement) {
    /**
     * isFcInSdom flag Action Unchecked then do not show parameters with fcs in sdom <br>
     * isNotInICDM flag Action Unchecked then do not show parameters not having history
     */

    return !((!isFcInSdomFlag() && fnElement.isFcInSdom()) || (!isFcNotInSdomFlag() && !fnElement.isFcInSdom()));
  }

  /**
   * @param fnElement
   * @return
   */
  private boolean checkInICDMA2LFlag(final FC2WPMapping fnElement) {
    /**
     * isInICDM flag Action Unchecked then do not show parameters having history <br>
     * isNotInICDM flag Action Unchecked then do not show parameters not having history
     */
    return !((!this.isInICDMA2LFlag && fnElement.isUsedInIcdm()) ||
        (!isNotInICDMA2LFlag() && !fnElement.isUsedInIcdm()));
  }


  /**
   * @param fnElement
   * @return
   */
  private boolean checkIsDeletedFlag(final FC2WPMapping fnElement) {
    /**
     * isDeletedFlag flag Action Unchecked then do not show items that are deleted <br>
     * isDeletedFlag flag Action Unchecked then do not show items that are not deleted
     */
    return !((!this.isDeletedFlag && fnElement.isDeleted()) || (!isNotDeletedFlag() && !fnElement.isDeleted()));
  }


  /**
   * @param fnElement
   * @return
   */
  private boolean checkIsWPAssigned(final FC2WPMapping fnElement) {
    /**
     * isWPAssigned flag Action Unchecked then do not show items that are have non-empty workpackage <br>
     * isWPAssigned flag Action Unchecked then do not show items that have empty workpackage
     */
    return !((!this.isWPAssigned && this.handler.getIsWPAssigned(fnElement.getFunctionName())) ||
        (!isWPNotAssigned() && !this.handler.getIsWPAssigned(fnElement.getFunctionName())));
  }

  public Matcher getToolBarMatcher() {
    return new FC2WPMappingColumnFilterMatcher<FC2WPMapping>();
  }


  /**
   * @return the isNotInICDMA2LFlag
   */
  public boolean isNotInICDMA2LFlag() {
    return this.isNotInICDMA2LFlag;
  }


  /**
   * @return the isInICDMA2LFlag
   */
  public boolean isInICDMA2LFlag() {
    return this.isInICDMA2LFlag;
  }

  /**
   * @param isNotInICDMA2LFlag the isNotInICDMA2LFlag to set
   */
  public void setNotInICDMA2LFlag(final boolean isNotInICDMA2LFlag) {
    this.isNotInICDMA2LFlag = isNotInICDMA2LFlag;
  }


  /**
   * @return the isDeletedFlag
   */
  public boolean isDeletedFlag() {
    return this.isDeletedFlag;
  }


  /**
   * @param isDeletedFlag the isDeletedFlag to set
   */
  public void setDeletedFlag(final boolean isDeletedFlag) {
    this.isDeletedFlag = isDeletedFlag;
  }


  /**
   * @return the isNotDeletedFlag
   */
  public boolean isNotDeletedFlag() {
    return this.isNotDeletedFlag;
  }


  /**
   * @param isNotDeletedFlag the isNotDeletedFlag to set
   */
  public void setNotDeletedFlag(final boolean isNotDeletedFlag) {
    this.isNotDeletedFlag = isNotDeletedFlag;
  }

  /**
   * @param isContactAssigned the isContactAssigned to set
   */
  public void setContactAssigned(final boolean isContactAssigned) {
    this.isContactAssigned = isContactAssigned;
  }

  /**
   * @param isContactNotAssigned the isContactNotAssigned to set
   */
  public void setContactNotAssigned(final boolean isContactNotAssigned) {
    this.isContactNotAssigned = isContactNotAssigned;
  }

  /**
   * @return the isWPAssigned
   */
  public boolean isWPAssigned() {
    return this.isWPAssigned;
  }


  /**
   * @param isWPAssigned the isWPAssigned to set
   */
  public void setWPAssigned(final boolean isWPAssigned) {
    this.isWPAssigned = isWPAssigned;
  }


  /**
   * @return the isWPNotAssigned
   */
  public boolean isWPNotAssigned() {
    return this.isWPNotAssigned;
  }


  /**
   * @param isWPNotAssigned the isWPNotAssigned to set
   */
  public void setWPNotAssigned(final boolean isWPNotAssigned) {
    this.isWPNotAssigned = isWPNotAssigned;
  }


  /**
   * @return the isRevelantPTtype
   */
  public boolean isRevelantPTtype() {
    return this.isRevelantPTtype;
  }


  /**
   * @param isRevelantPTtype the isRevelantPTtype to set
   */
  public void setIsRevelantPTtype(final boolean isRevelantPTtype) {
    this.isRevelantPTtype = isRevelantPTtype;
  }


  /**
   * @return the isNotRevelantPTtype
   */
  public boolean isNotRevelantPTtype() {
    return this.isNotRevelantPTtype;
  }


  /**
   * @param isNotRevelantPTtype the isNotRevelantPTtype to set
   */
  public void setNotRevelantPTtype(final boolean isNotRevelantPTtype) {
    this.isNotRevelantPTtype = isNotRevelantPTtype;
  }


  /**
   * @param isInICDMA2LFlag the isInICDMA2LFlag to set
   */
  public void setInICDMA2LFlag(final boolean isInICDMA2LFlag) {
    this.isInICDMA2LFlag = isInICDMA2LFlag;
  }


  /**
   * @return the isDifferent
   */
  public boolean isDifferent() {
    return this.isDifferent;
  }


  /**
   * @param isDifferent the isDifferent to set
   */
  public void setDifferent(final boolean isDifferent) {
    this.isDifferent = isDifferent;
  }


  /**
   * @param isNotDifferent the isNotDifferent to set
   */
  public void setNotDifferent(final boolean isNotDifferent) {
    this.isNotDifferent = isNotDifferent;
  }


  /**
   * @return the isNotDifferent
   */
  public boolean isNotDifferent() {
    return this.isNotDifferent;
  }


  /**
   * @return the isWpDifferent
   */
  public boolean isWpDifferent() {
    return this.isWpDifferent;
  }


  /**
   * @return the isWpNotDifferent
   */
  public boolean isWpNotDifferent() {
    return this.isWpNotDifferent;
  }


  /**
   * @return the isContactDifferent
   */
  public boolean isContactDifferent() {
    return this.isContactDifferent;
  }


  /**
   * @return the isContactNotDifferent
   */
  public boolean isContactNotDifferent() {
    return this.isContactNotDifferent;
  }


  /**
   * @return the isAgreedDifferent
   */
  public boolean isAgreedDifferent() {
    return this.isAgreedDifferent;
  }


  /**
   * @return the isAgreedNotDifferent
   */
  public boolean isAgreedNotDifferent() {
    return this.isAgreedNotDifferent;
  }


  /**
   * @return the isInIcdmA2l
   */
  public boolean isInIcdmA2l() {
    return this.isInIcdmA2l;
  }


  /**
   * @param isFcInSdomFlag the isFcInSdomFlag to set
   */
  public void setFcInSdomFlag(final boolean isFcInSdomFlag) {
    this.isFcInSdomFlag = isFcInSdomFlag;
  }


  /**
   * @return the isFcInSdomFlag
   */
  public boolean isFcInSdomFlag() {
    return this.isFcInSdomFlag;
  }


  /**
   * @return the isFcNotInSdomFlag
   */
  public boolean isFcNotInSdomFlag() {
    return this.isFcNotInSdomFlag;
  }


  /**
   * @param isFcNotInSdomFlag the isFcNotInSdomFlag to set
   */
  public void setFcNotInSdomFlag(final boolean isFcNotInSdomFlag) {
    this.isFcNotInSdomFlag = isFcNotInSdomFlag;
  }


  /**
   * @return the hasRelevantPTType
   */
  public boolean isHasRelevantPTType() {
    return this.hasRelevantPTType;
  }


  /**
   * @param hasRelevantPTType the hasRelevantPTType to set
   */
  public void setHasRelevantPTType(final boolean hasRelevantPTType) {
    this.hasRelevantPTType = hasRelevantPTType;
  }


  /**
   * @param isWpDifferent the isWpDifferent to set
   */
  public void setWpDifferent(final boolean isWpDifferent) {
    this.isWpDifferent = isWpDifferent;
  }


  /**
   * @param isWpNotDifferent the isWpNotDifferent to set
   */
  public void setWpNotDifferent(final boolean isWpNotDifferent) {
    this.isWpNotDifferent = isWpNotDifferent;
  }


  /**
   * @param isContactDifferent the isContactDifferent to set
   */
  public void setContactDifferent(final boolean isContactDifferent) {
    this.isContactDifferent = isContactDifferent;
  }


  /**
   * @param isContactNotDifferent the isContactNotDifferent to set
   */
  public void setContactNotDifferent(final boolean isContactNotDifferent) {
    this.isContactNotDifferent = isContactNotDifferent;
  }


  /**
   * @param isAgreedDifferent the isAgreedDifferent to set
   */
  public void setAgreedDifferent(final boolean isAgreedDifferent) {
    this.isAgreedDifferent = isAgreedDifferent;
  }


  /**
   * @param isAgreedNotDifferent the isAgreedNotDifferent to set
   */
  public void setAgreedNotDifferent(final boolean isAgreedNotDifferent) {
    this.isAgreedNotDifferent = isAgreedNotDifferent;
  }


  /**
   * @param isInIcdmA2l the isInIcdmA2l to set
   */
  public void setInIcdmA2l(final boolean isInIcdmA2l) {
    this.isInIcdmA2l = isInIcdmA2l;
  }


  /**
   * @return the isFcWithoutParams
   */
  public boolean isFcWithoutParams() {
    return this.isFcWithoutParams;
  }

  /**
   * @param isFcWithoutParams the isFcWithoutParams to set
   */
  public void setIsFcWithoutParams(final boolean isFcWithoutParams) {
    this.isFcWithoutParams = isFcWithoutParams;
  }


  /**
   * @return the isFcWithParams
   */
  public boolean isFcWithParams() {
    return this.isFcWithParams;
  }

  /**
   * @param isFcWithParams the isFcWithParams to set
   */
  public void setIsFcWithParams(final boolean isFcWithParams) {
    this.isFcWithParams = isFcWithParams;
  }

  private class FC2WPMappingColumnFilterMatcher<E> implements Matcher<E> {

    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof CompareFC2WPRowObject) {
        return selectElement(element);
      }
      return true;
    }
  }
}
