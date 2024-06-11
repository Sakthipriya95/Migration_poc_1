/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.model.a2l.ParameterClass;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author jvi6cob
 */
public class ParamNatToolBarFilter {

  // ICDM-2439
  private boolean complianceFlag = true;
  private boolean nonComplianceFlag = true;
  private boolean rivetFlag = true;

  /**
   * Black List Flag initially True since the tool bar action is checked
   */
  private boolean blackListFlag = true;
  /**
   * Non Black List Flag initially True since the tool bar action is checked
   */
  private boolean nonBlackListFlag = true;
  /**
   * QSSD Flag initially True since the tool bar action is checked
   */
  private boolean qSSDFlag = true;
  /**
   * Non QSSD Flag initially True since the tool bar action is checked
   */
  private boolean nonQSSDFlag = true;
  private boolean screwFlag = true;
  private boolean yesFlag = true;
  private boolean noFlag = true;

  private boolean nailFlag = true;

  private boolean unDefinedFlag = true;

  private boolean withStatusFlag = true;

  private boolean withoutStatusFlag = true;

  private boolean withValueFlag = true;

  private boolean withoutValueFlag = true;

  // ICDM-841
  private boolean withLabParam = true;

  private boolean withoutLabParam = true;

  private boolean readOnlyParam = true;

  private boolean notReadOnlyParam = true;

  private boolean depnParamFlag = true;

  private boolean notDepnFlag = true;


  /**
   * @return the withoutLabParam flag
   */
  public boolean isWithoutLabParam() {
    return this.withoutLabParam;
  }


  /**
   * @param withoutLabParam the withoutLabParam to set
   */
  public void setWithoutLabParam(final boolean withoutLabParam) {
    this.withoutLabParam = withoutLabParam;
  }


  /**
   * @return the withLabParam flag
   */
  public boolean isWithLabParam() {
    return this.withLabParam;
  }


  /**
   * @param withLabParam the withLabParam to set
   */
  public void setWithLabParam(final boolean withLabParam) {
    this.withLabParam = withLabParam;
  }


  /**
   * @return the unDefined
   */
  public boolean isWithStatus() {
    return this.withStatusFlag;
  }

  /**
   * @return the unDefined
   */
  public boolean isWithoutStatus() {
    return this.withoutStatusFlag;
  }

  /**
   * @return the unDefined
   */
  public boolean isWithValue() {
    return this.withValueFlag;
  }

  /**
   * @return the unDefined
   */
  public boolean isWithoutValue() {
    return this.withoutValueFlag;
  }


  /**
   * @return the unDefined
   */
  public boolean isUnDefined() {
    return this.unDefinedFlag;
  }


  /**
   * @return the rivetFlag
   */
  public boolean isRivetFlag() {
    return this.rivetFlag;
  }


  /**
   * @return the nailFlag
   */
  public boolean isNailFlag() {
    return this.nailFlag;
  }


  /**
   * @return the noFlag
   */
  public boolean isNoFlag() {
    return this.noFlag;
  }


  /**
   * @return the yesFlag
   */
  public boolean isYesFlag() {
    return this.yesFlag;
  }


  /**
   * @return the screwFlag
   */
  public boolean isScrewFlag() {
    return this.screwFlag;
  }


  /**
   * @param rivetFlag rivetFlag
   */
  public void setRivetFlag(final boolean rivetFlag) {
    this.rivetFlag = rivetFlag;

  }

  /**
   * @param nailFlag nailFlag
   */
  public void setNailFlag(final boolean nailFlag) {
    this.nailFlag = nailFlag;

  }

  /**
   * @param screwFlag screwFlag
   */
  public void setScrewFlag(final boolean screwFlag) {
    this.screwFlag = screwFlag;

  }

  private class A2lToolBarParamMatcher<E> implements Matcher<E> {


    /** {@inheritDoc} */
    public boolean matches(final E element) {
      if (element instanceof A2LParameter) {
        return selectElement(element);
      }
      return true;
    }
  }

  /**
   * {@inheritDoc}
   */
  // @Override
  protected boolean selectElement(final Object element) {

    // ICDM-2439
    /**
     * Compliance filter Action Unchecked then do not show compliance parameters
     */
    if (!isComplianceFlag() && ((A2LParameter) element).isComplianceParam()) {
      return false;
    }

    if (!isBlackListFlag() && ((A2LParameter) element).isBlackList()) {
      return false;
    }

    if (!isReadOnlyParam() && ((A2LParameter) element).getCharacteristic().isReadOnly()) {
      return false;
    }
    if (!isqSSDFlag() && ((A2LParameter) element).isQssdParameter()) {
      return false;
    }
    if (!this.depnParamFlag && ((A2LParameter) element).getCharacteristic().isDependentCharacteristic()) {
      return false;
    }
    if (!isNotReadOnlyParam() && !((A2LParameter) element).getCharacteristic().isReadOnly()) {
      return false;
    }
    if (!this.notDepnFlag && !((A2LParameter) element).getCharacteristic().isDependentCharacteristic()) {
      return false;
    }
    // ICDM-2439
    /**
     * Non Compliance filter Action Unchecked then do not show compliance parameters
     */
    if (!isNonComplianceFlag() && !((A2LParameter) element).isComplianceParam()) {
      return false;
    }
    if (!isNonBlackListFlag() && !((A2LParameter) element).isBlackList()) {
      return false;
    }
    if (!isNonQSSDFlag() && !((A2LParameter) element).isQssdParameter()) {
      return false;
    }
    /**
     * Screw Action Unchecked then do not show Screw Class
     */
    if (!isScrewFlag() && ParameterClass.SCREW.equals(((A2LParameter) element).getPclass())) {
      return false;
    }
    /**
     * Nail Action Unchecked then do not show Nail Class
     */
    if (!isNailFlag() && ParameterClass.NAIL.equals(((A2LParameter) element).getPclass())) {
      return false;
    }
    /**
     * //ICDM-916 stat Rivet Rivet Action Unchecked then do not show Rivet Class
     */
    if (!isRivetFlag() && (ParameterClass.RIVET.equals(((A2LParameter) element).getPclass()) ||
        ParameterClass.STATRIVET.equals(((A2LParameter) element).getPclass()))) {
      return false;
    }

    /**
     * Code Yes Action Unchecked then do not show Code Yes
     */
    if (!isYesFlag() && (((A2LParameter) element).isCodeWord())) {
      return false;
    }
    /**
     * Code No Action Unchecked then do not show Code No
     */
    if (!isNoFlag() && !((A2LParameter) element).isCodeWord()) {
      return false;
    }
    /**
     * Class Undefined flag to remove Undefined Class types
     */
    if (!isUnDefined() && (null == ((A2LParameter) element).getPclass())) {
      return false;
    }

    /**
     * Status flag Unchecked then do not show parameters with status loaded from external source
     */
    if (!isWithStatus() &&
        ((null != ((A2LParameter) element).getStatus()) && !(((A2LParameter) element).getStatus().isEmpty()))) {
      return false;
    }

    /**
     * NoStatus flag Unchecked then do not show parameters without status loaded from external source
     */
    if (!isWithoutStatus() &&
        ((null == ((A2LParameter) element).getStatus()) || (((A2LParameter) element).getStatus().isEmpty()))) {
      return false;
    }

    /**
     * Value flag Unchecked then do not show parameters with value loaded from external source
     */
    if (!isWithValue() && (null != ((A2LParameter) element).getCalData()) &&
        (null != ((A2LParameter) element).getCalData().getCalDataPhy())) {
      return false;
    }

    /**
     * NoValue flag Unchecked then do not show parameters without value loaded from external source
     */
    if (!isWithoutValue() && ((null == ((A2LParameter) element).getCalData()) ||
        (null == ((A2LParameter) element).getCalData().getCalDataPhy()))) {
      return false;
    }

    // ICDM-841
    /**
     * isWithLabParam flag unchecked then do not show labels in LAB file
     */
    if (!isWithLabParam() && (((A2LParameter) element).isLABParam())) {
      return false;
    }

    /**
     * isWithoutLabParam flag unchecked then do not show labels not in LAB file
     */
    if (!isWithoutLabParam() && !(((A2LParameter) element).isLABParam())) {
      return false;
    }

    return true;
  }


  /**
   * @param yesFlag yesFlag
   */
  public void setYesFlag(final boolean yesFlag) {
    this.yesFlag = yesFlag;

  }


  /**
   * @param noFlag noFlag
   */
  public void setNoFlag(final boolean noFlag) {
    this.noFlag = noFlag;

  }

  /**
   * {@inheritDoc}
   */
  // @Override
  public void setFilterText(final String filterText) {
    // TO-DO
  }


  /**
   * @param unDefinedFlag unDefined
   */
  public void setUndefinedFlag(final boolean unDefinedFlag) {
    this.unDefinedFlag = unDefinedFlag;

  }

  /**
   * @param statusFlag unDefined
   */
  public void setWithStatusFlag(final boolean statusFlag) {
    this.withStatusFlag = statusFlag;

  }

  /**
   * @param woStatusFlag
   */
  public void setWithoutStatusFlag(final boolean woStatusFlag) {
    this.withoutStatusFlag = woStatusFlag;
  }

  /**
   * @param withValueFlag
   */
  public void setWithValueFlag(final boolean withValueFlag) {
    this.withValueFlag = withValueFlag;
  }

  /**
   * @param withValueFlag
   */
  public void setWithoutValueFlag(final boolean withoutValueFlag) {
    this.withoutValueFlag = withoutValueFlag;
  }

  /**
   * @return
   */
  public Matcher getToolBarMatcher() {
    return new A2lToolBarParamMatcher<A2LParameter>();
  }

  // ICDM-2439
  /**
   * @return the complianceFlag
   */
  public boolean isComplianceFlag() {
    return this.complianceFlag;
  }


  /**
   * @return the notReadOnlyParam
   */
  public boolean isNotReadOnlyParam() {
    return this.notReadOnlyParam;
  }


  /**
   * @return the readOnlyParam
   */
  public boolean isReadOnlyParam() {
    return this.readOnlyParam;
  }


  /**
   * @param readOnlyParam the readOnlyParam to set
   */
  public void setReadOnlyParam(final boolean readOnlyParam) {
    this.readOnlyParam = readOnlyParam;
  }


  /**
   * @param complianceFlag the complianceFlag to set
   */
  public void setComplianceFlag(final boolean complianceFlag) {
    this.complianceFlag = complianceFlag;
  }

  // ICDM-2439
  /**
   * @return the nonComplianceFlag
   */
  public boolean isNonComplianceFlag() {
    return this.nonComplianceFlag;
  }

  // ICDM-2439
  /**
   * @param nonComplianceFlag the nonComplianceFlag to set
   */
  public void setNonComplianceFlag(final boolean nonComplianceFlag) {
    this.nonComplianceFlag = nonComplianceFlag;
  }


  /**
   * @param notReadOnly notReadOnly
   */
  public void setNotReadOnlyParam(final boolean notReadOnly) {
    this.notReadOnlyParam = notReadOnly;

  }


  /**
   * @param blackListFlag the blackListFlag to set
   */
  public void setBlackListFlag(final boolean blackListFlag) {
    this.blackListFlag = blackListFlag;
  }


  /**
   * @param nonBlackListFlag the nonBlackListFlag to set
   */
  public void setNonBlackListFlag(final boolean nonBlackListFlag) {
    this.nonBlackListFlag = nonBlackListFlag;
  }


  /**
   * @return the blackListFlag
   */
  public boolean isBlackListFlag() {
    return this.blackListFlag;
  }


  /**
   * @return the nonBlackListFlag
   */
  public boolean isNonBlackListFlag() {
    return this.nonBlackListFlag;
  }


  /**
   * @return boolean
   */
  public boolean isqSSDFlag() {
    return this.qSSDFlag;
  }


  /**
   * @param qSSDFlag boolean
   */
  public void setqSSDFlag(final boolean qSSDFlag) {
    this.qSSDFlag = qSSDFlag;
  }


  /**
   * @return boolean
   */
  public boolean isNonQSSDFlag() {
    return this.nonQSSDFlag;
  }


  /**
   * @param nonQSSDFlag boolean
   */
  public void setNonQSSDFlag(final boolean nonQSSDFlag) {
    this.nonQSSDFlag = nonQSSDFlag;
  }


  /**
   * @param depnParamFlag boolean
   */
  public void setDepnParamFlag(final boolean depnParamFlag) {
    this.depnParamFlag = depnParamFlag;
  }


  /**
   * @param notDepnFlag boolean
   */
  public void setNotDepnParamFlag(final boolean notDepnFlag) {
    this.notDepnFlag = notDepnFlag;
  }
}
