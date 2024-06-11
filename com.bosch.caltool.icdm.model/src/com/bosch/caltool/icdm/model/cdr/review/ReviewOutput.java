/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;

/**
 * @author bru2cob This class provides data for the review summary page
 */
public class ReviewOutput {

  /**
   * Pidc version
   */
  private PidcVersion pidcVersion;
  /**
   * pidc variant name
   */
  private String pidcVariantName;
  /**
   * a2l file name
   */
  private String a2lFileName;
  /**
   * wp group name
   */
  private String workPackageGroupName;
  /**
   * calibration engineer name
   */
  private String calEngineerName;
  /**
   * auditor name
   */
  private String auditorName;
  /**
   * Number of reviewed functions
   */
  private Integer noOfReviewedFunctions;
  /**
   * Number of reviewed params
   */
  private Integer noOfReviewedParam;
  /**
   * Params not reviewed in a2l
   */
  private Integer paramsNotReviewedInA2l;
  /**
   * params not reviewed in rule set
   */
  private Integer paramsNotRvwdInRuleset;
  /**
   * params not reiviewed without rule
   */
  private Integer paramsNotRvwdWithoutRule;
  /**
   * Result object
   */
  private CDRReviewResult cdrResult;
  private boolean deltaReviewValid;
  /**
   * Rvw variant obj to enable linking with pidc tree
   */
  private RvwVariant rvwVariant;
  /**
   * List of Review Participants
   */
  private List<RvwParticipant> rvwParticipantsList;
  /**
   * Review Created User
   */
  private String rvwCreatedUser;

  /**
   * Map of A2lWpResponsibilityStatus after Wp status updation. Key - A2lWpResponsibilityStatus id, value -
   * A2lWpResponsibilityStatus
   */
  private Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusAfterUpdate = new HashMap<>();

  /**
   * Map of A2lWpResponsibilityStatus before Wp status updation. Key - A2lWpResponsibilityStatus id, value -
   * A2lWpResponsibilityStatus
   */
  private Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusBeforeUpdate = new HashMap<>();
  /**
   * List of newly created A2lWpResponsibilityStatus entries in T_A2L_WP_RESPONSIBILITY_STATUS table
   */
  private List<A2lWpResponsibilityStatus> listOfNewlyCreatedA2lWpRespStatus = new ArrayList<>();

  /**
   * @return the rvwVariant
   */
  public RvwVariant getRvwVariant() {
    return this.rvwVariant;
  }


  /**
   * @param rvwVariant the rvwVariant to set
   */
  public void setRvwVariant(final RvwVariant rvwVariant) {
    this.rvwVariant = rvwVariant;
  }


  /**
   * @return the deltaReviewValid
   */
  public boolean isDeltaReviewValid() {
    return this.deltaReviewValid;
  }


  /**
   * @param deltaReviewValid the deltaReviewValid to set
   */
  public void setDeltaReviewValid(final boolean deltaReviewValid) {
    this.deltaReviewValid = deltaReviewValid;
  }

  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }

  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }

  /**
   * @return the pidcVariantName
   */
  public String getPidcVariantName() {
    return this.pidcVariantName;
  }

  /**
   * @param pidcVariantName the pidcVariantName to set
   */
  public void setPidcVariantName(final String pidcVariantName) {
    this.pidcVariantName = pidcVariantName;
  }

  /**
   * @return the a2lFileName
   */
  public String getA2lFileName() {
    return this.a2lFileName;
  }

  /**
   * @param a2lFileName the a2lFileName to set
   */
  public void setA2lFileName(final String a2lFileName) {
    this.a2lFileName = a2lFileName;
  }

  /**
   * @return the workPackageGroupName
   */
  public String getWorkPackageGroupName() {
    return this.workPackageGroupName;
  }

  /**
   * @param workPackageGroupName the workPackageGroupName to set
   */
  public void setWorkPackageGroupName(final String workPackageGroupName) {
    this.workPackageGroupName = workPackageGroupName;
  }

  /**
   * @return the calEngineerName
   */
  public String getCalEngineerName() {
    return this.calEngineerName;
  }

  /**
   * @param calEngineerName the calEngineerName to set
   */
  public void setCalEngineerName(final String calEngineerName) {
    this.calEngineerName = calEngineerName;
  }

  /**
   * @return the auditorName
   */
  public String getAuditorName() {
    return this.auditorName;
  }

  /**
   * @param auditorName the auditorName to set
   */
  public void setAuditorName(final String auditorName) {
    this.auditorName = auditorName;
  }

  /**
   * @return the noOfReviewedFunctions
   */
  public Integer getNoOfReviewedFunctions() {
    return this.noOfReviewedFunctions;
  }

  /**
   * @param noOfReviewedFunctions the noOfReviewedFunctions to set
   */
  public void setNoOfReviewedFunctions(final Integer noOfReviewedFunctions) {
    this.noOfReviewedFunctions = noOfReviewedFunctions;
  }

  /**
   * @return the noOfReviewedParam
   */
  public Integer getNoOfReviewedParam() {
    return this.noOfReviewedParam;
  }

  /**
   * @param noOfReviewedParam the noOfReviewedParam to set
   */
  public void setNoOfReviewedParam(final Integer noOfReviewedParam) {
    this.noOfReviewedParam = noOfReviewedParam;
  }

  /**
   * @return the paramsNotReviewedInA2l
   */
  public Integer getParamsNotReviewedInA2l() {
    return this.paramsNotReviewedInA2l;
  }

  /**
   * @param paramsNotReviewedInA2l the paramsNotReviewedInA2l to set
   */
  public void setParamsNotReviewedInA2l(final Integer paramsNotReviewedInA2l) {
    this.paramsNotReviewedInA2l = paramsNotReviewedInA2l;
  }

  /**
   * @return the paramsNotRvwdInRuleset
   */
  public Integer getParamsNotRvwdInRuleset() {
    return this.paramsNotRvwdInRuleset;
  }

  /**
   * @param paramsNotRvwdInRuleset the paramsNotRvwdInRuleset to set
   */
  public void setParamsNotRvwdInRuleset(final Integer paramsNotRvwdInRuleset) {
    this.paramsNotRvwdInRuleset = paramsNotRvwdInRuleset;
  }

  /**
   * @return the paramsNotRvwdWithoutRule
   */
  public Integer getParamsNotRvwdWithoutRule() {
    return this.paramsNotRvwdWithoutRule;
  }

  /**
   * @param paramsNotRvwdWithoutRule the paramsNotRvwdWithoutRule to set
   */
  public void setParamsNotRvwdWithoutRule(final Integer paramsNotRvwdWithoutRule) {
    this.paramsNotRvwdWithoutRule = paramsNotRvwdWithoutRule;
  }

  /**
   * @return the cdrResult
   */
  public CDRReviewResult getCdrResult() {
    return this.cdrResult;
  }

  /**
   * @param cdrResult the cdrResult to set
   */
  public void setCdrResult(final CDRReviewResult cdrResult) {
    this.cdrResult = cdrResult;
  }


  /**
   * @return the rvwParticipantsList
   */
  public List<RvwParticipant> getRvwParticipantsList() {
    return this.rvwParticipantsList;
  }


  /**
   * @param rvwParticipantsList the rvwParticipantsList to set
   */
  public void setRvwParticipantsList(final List<RvwParticipant> rvwParticipantsList) {
    this.rvwParticipantsList = rvwParticipantsList;
  }


  /**
   * @return the rvwCreatedUser
   */
  public String getRvwCreatedUser() {
    return this.rvwCreatedUser;
  }


  /**
   * @param rvwCreatedUser the rvwCreatedUser to set
   */
  public void setRvwCreatedUser(final String rvwCreatedUser) {
    this.rvwCreatedUser = rvwCreatedUser;
  }


  /**
   * @return the a2lWpResponsibilityAfterUpdate
   */
  public Map<Long, A2lWpResponsibilityStatus> getA2lWpResponsibilityAfterUpdate() {
    return this.a2lWpRespStatusAfterUpdate;
  }


  /**
   * @param a2lWpResponsibilityAfterUpdate the a2lWpResponsibilityAfterUpdate to set
   */
  public void setA2lWpResponsibilityAfterUpdate(
      final Map<Long, A2lWpResponsibilityStatus> a2lWpResponsibilityAfterUpdate) {
    this.a2lWpRespStatusAfterUpdate = a2lWpResponsibilityAfterUpdate;
  }


  /**
   * @return the a2lWpResponsibilityBeforeUpdate
   */
  public Map<Long, A2lWpResponsibilityStatus> getA2lWpResponsibilityBeforeUpdate() {
    return this.a2lWpRespStatusBeforeUpdate;
  }


  /**
   * @param a2lWpResponsibilityBeforeUpdate the a2lWpResponsibilityBeforeUpdate to set
   */
  public void setA2lWpResponsibilityBeforeUpdate(
      final Map<Long, A2lWpResponsibilityStatus> a2lWpResponsibilityBeforeUpdate) {
    this.a2lWpRespStatusBeforeUpdate = a2lWpResponsibilityBeforeUpdate;
  }


  /**
   * @return the listOfNewlyCreatedA2lWpRespStatus
   */
  public List<A2lWpResponsibilityStatus> getListOfNewlyCreatedA2lWpRespStatus() {
    return this.listOfNewlyCreatedA2lWpRespStatus;
  }


  /**
   * @param listOfNewlyCreatedA2lWpRespStatus the listOfNewlyCreatedA2lWpRespStatus to set
   */
  public void setListOfNewlyCreatedA2lWpRespStatus(
      final List<A2lWpResponsibilityStatus> listOfNewlyCreatedA2lWpRespStatus) {
    this.listOfNewlyCreatedA2lWpRespStatus = listOfNewlyCreatedA2lWpRespStatus;
  }

}
