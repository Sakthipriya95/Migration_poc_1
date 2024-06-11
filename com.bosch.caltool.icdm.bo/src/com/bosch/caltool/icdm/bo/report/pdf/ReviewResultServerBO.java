/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.pdf;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_LOCK_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.ReviewResultEditorData;
import com.bosch.caltool.icdm.model.cdr.RvwAttrValue;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.model.cdr.RvwResultWPandRespModel;
import com.bosch.caltool.icdm.model.user.User;


/**
 * @author msp5cob
 */
public class ReviewResultServerBO {

  private final ReviewResultBO reviewResultBO;


  /**
   * @param reviewResultBO Main BO class
   */
  public ReviewResultServerBO(final ReviewResultBO reviewResultBO) {
    this.reviewResultBO = reviewResultBO;
  }


  /**
   * @return the reviewResultClientBO
   */
  public ReviewResultBO getReviewResultBO() {
    return this.reviewResultBO;
  }

  /**
   * @return the Db value of the lock status. Y or N.
   */
  public REVIEW_LOCK_STATUS getLockStatus() {
    // get the lock status from DB as string and then return the enumeration.
    final String lockStatus = getCDRResult().getLockStatus();
    return CDRConstants.REVIEW_LOCK_STATUS.getType(lockStatus);
  }


  /**
   * @return the review type as string
   */
  public String getReviewTypeStr() {
    return getReviewType().getUiType();
  }

  /**
   * @return the review type
   */
  public REVIEW_TYPE getReviewType() {
    return CDRConstants.REVIEW_TYPE.getType(getCDRResult().getReviewType());
  }

  /**
   * @return whether this review is a Delta review or not
   */
  public boolean isDeltaReview() {
    if (getCDRResult().getOrgResultId() == null) {
      return getDeltaReviewType() != null;
    }
    return true;

  }

  /**
   * Type of delta review
   *
   * @return DELTA_REVIEW_TYPE enum
   */
  public DELTA_REVIEW_TYPE getDeltaReviewType() {
    return DELTA_REVIEW_TYPE.getType(getCDRResult().getDeltaReviewType());
  }


  /**
   * @return the parent CDR result object
   */
  public CDRReviewResult getCDRResult() {
    return getResponse().getReviewResult();
  }


  /**
   * @return parent review result, if present
   */
  public CDRReviewResult getParentReview() {
    return getResponse().getParentReviewResult();
  }


  /**
   * @return Map key - rvw param ID, value rvw parameter
   */
  public Map<Long, CDRResultParameter> getParentParametersMap() {
    return getResponse().getParentParamMap();
  }

  /**
   * @return the set of additional files
   */
  public SortedSet<RvwFile> getAdditionalFiles() {
    final SortedSet<RvwFile> addlFilesSet = new TreeSet<>();
    List<RvwFile> fileList = getResponse().getIcdmFiles().get(CDRConstants.REVIEW_FILE_TYPE.RVW_ADDL_FILE.getDbType());
    if (null != fileList) {
      for (RvwFile icdmFile : fileList) {
        addlFilesSet.add(icdmFile);
      }
    }
    return addlFilesSet;
  }

  /**
   * @return the set of Lab/Fun files files Icdm-729 get the Lab fun files From DB
   */
  public SortedSet<RvwFile> getLabFunFiles() {
    final SortedSet<RvwFile> labFunFilesSet = new TreeSet<>();
    List<RvwFile> fileList = getResponse().getIcdmFiles().get(CDRConstants.REVIEW_FILE_TYPE.FUNCTION_FILE.getDbType());
    if (null != fileList) {
      for (RvwFile icdmFile : fileList) {
        labFunFilesSet.add(icdmFile);
      }
    }
    fileList = getResponse().getIcdmFiles().get(CDRConstants.REVIEW_FILE_TYPE.LAB_FILE.getDbType());
    if (null != fileList) {
      for (RvwFile icdmFile : fileList) {
        labFunFilesSet.add(icdmFile);
      }
    }
    return labFunFilesSet;
  }

  /**
   * @return the set of output files
   */
  public SortedSet<RvwFile> getOutputFiles() {
    final SortedSet<RvwFile> outputFilesSet = new TreeSet<>();
    List<RvwFile> fileList = getResponse().getIcdmFiles().get(CDRConstants.REVIEW_FILE_TYPE.OUTPUT.getDbType());
    if (null != fileList) {
      for (RvwFile icdmFile : fileList) {
        outputFilesSet.add(icdmFile);
      }
    }
    return outputFilesSet;
  }


  /**
   * @return rule file which was used to execute Check SSD
   */
  public SortedSet<RvwFile> getRuleFile() {
    final SortedSet<RvwFile> ruleFilesSet = new TreeSet<>();
    List<RvwFile> fileList = getResponse().getIcdmFiles().get(CDRConstants.REVIEW_FILE_TYPE.RULE.getDbType());
    if (null != fileList) {
      for (RvwFile icdmFile : fileList) {
        ruleFilesSet.add(icdmFile);
      }
    }
    return ruleFilesSet;
  }

  /**
   * @return the set of input files
   */
  public SortedSet<RvwFile> getInputFiles() {
    final SortedSet<RvwFile> inputFilesSet = new TreeSet<>();
    List<RvwFile> fileList = getResponse().getIcdmFiles().get(CDRConstants.REVIEW_FILE_TYPE.INPUT.getDbType());
    if (null != fileList) {
      for (RvwFile icdmFile : fileList) {
        inputFilesSet.add(icdmFile);
      }
    }
    return inputFilesSet;
  }


  /**
   * @return map of Key - Responsible Name and Value - A2lWorkPackage Object
   */
  public Map<String, Set<RvwResultWPandRespModel>> getRespAndA2lWPMap() {
    return getResponse().getA2lWpMap();
  }

  /**
   * @return map of Key - Responsible Name and Value - A2lWorkPackage Object
   */
  public Map<Long, A2lResponsibility> getA2lResponsibilityMap() {
    return getResponse().getA2lResponsibilityMap();
  }

  /**
   * Icdm-1214
   *
   * @return the Rvw Attr Values
   */
  public Map<Long, RvwAttrValue> getReviewAttrValMap() {
    return getResponse().getAttrValMap();
  }

  /**
   * @return the Attr Val Set for Display puropse
   */
  public final SortedSet<RvwAttrValue> getRvwAttrValSet() {
    return new TreeSet<>(getReviewAttrValMap().values());
  }

  /**
   * @return the Variant used for this review
   */
  public PidcVariant getVariant() {
    return getResponse().getFirstVariant();
  }

  /**
   * @return the Project ID card of this review
   */
  public PidcVersion getPidcVersion() {
    return getResponse().getPidcVers();
  }

  /**
   * @return the Status of this review DB type((O)Open, (I)In-Progress, (C)Closed)
   */
  public CDRConstants.REVIEW_STATUS getStatus() {
    return CDRConstants.REVIEW_STATUS.getType(getCDRResult().getRvwStatus());
  }

  /**
   * @return the Status of the review UI Type ( Open, In-Progress, Closed)
   */
  public String getStatusUIType() {
    return CDRConstants.REVIEW_STATUS.getType(getCDRResult().getRvwStatus()).getUiType();
  }

  /**
   * @return user that created this review
   */
  public final User getCreatedUser() {
    return getResponse().getCreatedUser();
  }

  /**
   * @return the calibration engineer
   */
  public RvwParticipant getCalibrationEngineer() {
    RvwParticipant calibrationEngr = null;
    for (RvwParticipant participant : getParticipantMap().values()) {
      if (getReviewResultBO().getParticipationType(participant) == CDRConstants.REVIEW_USER_TYPE.CAL_ENGINEER) {
        calibrationEngr = participant;
      }
    }

    return calibrationEngr;
  }

  /**
   * @return the auditor
   */
  public RvwParticipant getAuditor() {
    RvwParticipant auditor = null;
    for (RvwParticipant participant : getParticipantMap().values()) {
      if (getReviewResultBO().getParticipationType(participant) == CDRConstants.REVIEW_USER_TYPE.AUDITOR) {
        auditor = participant;
      }
    }

    return auditor;

  }

  /**
   * @return the sorted set of other participants
   */
  public SortedSet<RvwParticipant> getOtherParticipants() {
    final SortedSet<RvwParticipant> othrUsrSet = new TreeSet<>();
    for (RvwParticipant participant : getParticipantMap().values()) {
      if (getReviewResultBO().getParticipationType(participant) == CDRConstants.REVIEW_USER_TYPE.ADDL_PARTICIPANT) {
        othrUsrSet.add(participant);
      }
    }
    return othrUsrSet;
  }


  /**
   * @return the map of CDR participants
   */
  protected Map<Long, RvwParticipant> getParticipantMap() {
    return getResponse().getParticipantsMap();
  }

  /**
   * Returns name after resolving Group based or Workpackage based configuration
   *
   * @return the name of WP/Group
   */
  public String getWorkPackageName() {
    String wpName;
    if (getFC2WPID() == null) {
      wpName = getGroupWorkPackageName();
    }
    else {
      wpName = getResponse().getWpDivision().getWpName();
    }
    return wpName;
  }

  /**
   * @return the FC2WP ID if the Work packacage is resolved based on FC to WP mapping
   */
  public Long getFC2WPID() {
    return getCDRResult().getWpDivId();
  }

  /**
   * @return the Work package name, if the work package is based on groups
   */
  public String getGroupWorkPackageName() {
    return getCDRResult().getGrpWorkPkg();
  }


  /**
   * @return the set of parameters used for this review
   */
  public final SortedSet<CDRResultParameter> getParameters() {
    return new TreeSet<>(getParametersMap().values());
  }

  /**
   * @return the Map of parameters used for this review
   */
  public Map<Long, CDRResultParameter> getParametersMap() {
    return getResponse().getParamMap();
  }


  /**
   * If this review is a delta review, return the parent review s name If this review is a delta review based on project
   * data , return MULTIPLE_REVIEWS
   *
   * @return true if delta review
   */
  public String getBaseReviewInfo() {

    DELTA_REVIEW_TYPE deltaReviewType = getDeltaReviewType();

    if (null != deltaReviewType) {
      if (deltaReviewType == DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW) {
        CDRResultParameter parentParam = null;
        Iterator<CDRResultParameter> iterator = getParameters().iterator();
        while ((parentParam == null) && iterator.hasNext()) {
          parentParam = getReviewResultBO().getParentParam(iterator.next());
        }

        if (null == parentParam) {
          return "";
        }
        StringBuilder parentRvwText = new StringBuilder(40);
        String variantName = (getResponse().getProjDeltaParentVariantName() == null) ? "<NO-VARIANT>"
            : getResponse().getProjDeltaParentVariantName();
        parentRvwText.append("<Multiple Reviews of PIDC -").append(getResponse().getProjDeltaParentPidcVersName())
            .append(" and Variant - "); // Append pidc name

        parentRvwText.append(variantName);

        parentRvwText.append(">");
        return parentRvwText.toString();

      }
      else if ((deltaReviewType == DELTA_REVIEW_TYPE.DELTA_REVIEW) && (null != getParentReview())) {
        return getParentReview().getName();
      }
    }
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * @return a2l file name
   */
  public String getA2lFileName() {
    return getResponse().getA2lFileName();
  }


  /**
   * /**
   *
   * @return the response
   */
  private ReviewResultEditorData getResponse() {
    return getReviewResultBO().getResponse();
  }

}

