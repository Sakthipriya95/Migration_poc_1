/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.UserPreference;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.OBD_OPTION;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_LOCK_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_USER_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.ReviewResultEditorData;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RvwAttrValue;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.model.cdr.RvwResultWPandRespModel;
import com.bosch.caltool.icdm.model.cdr.RvwUserCmntHistory;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespStatusData;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWorkPackageServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpResponsibilityStatusServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRResultParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwFileServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwParticipantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireRespVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireRespVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireResponseServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwUserCmntHistoryServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bru2cob
 */
public class ReviewResultBO extends AbstractClientDataHandler {

  private final ReviewResultClientBO reviewResultClientBO;

  private String editorReviewResultName;


  /**
   * @return the editorResultName
   */
  public String getEditorResultName() {
    return this.editorReviewResultName;
  }


  /**
   * @param editorResultName the editorResultName to set
   */
  public void setEditorResultName(final String editorResultName) {
    this.editorReviewResultName = editorResultName;
  }


  /**
   * @param reviewResultClientBO Main BO class
   */
  public ReviewResultBO(final ReviewResultClientBO reviewResultClientBO) {
    this.reviewResultClientBO = reviewResultClientBO;
  }


  /**
   * @return the reviewResultClientBO
   */
  public ReviewResultClientBO getReviewResultClientBO() {
    return this.reviewResultClientBO;
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
   * @return true if the review is locked
   */
  public boolean isResultLocked() {
    return CDRConstants.REVIEW_LOCK_STATUS.YES == getLockStatus();
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
   * @return the OBD Label Flag
   */
  public OBD_OPTION getObdFlag() {
    return CDRConstants.OBD_OPTION.getTypeByDbCode(getCDRResult().getObdFlag());
  }

  /**
   * @return the parent CDR result object
   */
  public CDRReviewResult getCDRResult() {
    return getResponse().getReviewResult();
  }

  /**
   * @return the review status enum.Method has been added to use the enumeration directly.
   */
  public REVIEW_STATUS getReviewStatus() {
    return CDRConstants.REVIEW_STATUS.getType(getCDRResult().getRvwStatus());
  }

  /**
   * Checks whether the current user can unlock the review result
   *
   * @return true if the current user has APIC_WRITE or the Owner of PIDC
   */
  public boolean canUnLockResult() {
    try {
      CurrentUserBO currentUser = new CurrentUserBO();
      if (currentUser.hasApicWriteAccess()) {
        return true;
      }
      NodeAccess curUserAccRight = currentUser.getNodeAccessRight(getPidc().getId());
      if (curUserAccRight == null) {
        return false;
      }
      return curUserAccRight.isOwner();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return false;

  }

  /**
   * @return parent review result, if present
   */
  public CDRReviewResult getParentReview() {
    return getResponse().getParentReviewResult();
  }

  /**
   * Retuns true, if user is calibration engineer or auditor for this review
   *
   * @return true if result is modifiable
   */
  public boolean isModifiable() {
    boolean isModifiable = false;
    CurrentUserBO currentUser = new CurrentUserBO();
    try {
      Long curUserId = currentUser.getUserID();

      // Allow editing, only if the current user is assigned as calibration engineer or auditor for this review
      // ICDM-1746 Extending the modifying privilege to the user who created the review
      isModifiable = isCalEngineer(curUserId) || isAuditor(curUserId) || isCreatedUser(currentUser.getUserName()) ||
          isModifiableOtherParticipant(curUserId);

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    return isModifiable;
  }

  /**
   * @param userId
   * @return
   */
  private boolean isCalEngineer(final Long userId) {
    RvwParticipant calEnginer = getCalibrationEngineer();
    return (calEnginer != null) && (userId.longValue() == calEnginer.getUserId().longValue());
  }

  private boolean isAuditor(final Long userId) {
    RvwParticipant auditor = getAuditor();
    return (auditor != null) && (userId.longValue() == auditor.getUserId().longValue());
  }

  private boolean isCreatedUser(final String userNtId) {
    return CommonUtils.isEqual(getCDRResult().getCreatedUser(), userNtId);
  }

  /**
   * @param currUser
   * @return
   */
  private boolean isModifiableOtherParticipant(final Long userId) {
    boolean ret = false;
    for (RvwParticipant otherUser : getOtherParticipants()) {
      if (otherUser.isEditFlag() && (otherUser.getUserId().longValue() == userId.longValue())) {
        ret = true;
        break;
      }
    }
    return ret;

  }

  /**
   * Checks whether the current user can lock the review result
   *
   * @return true if the current user has APIC_WRITE or Calibration Engineer or Auditor
   */
  public boolean canLockResult() {
    try {
      return new CurrentUserBO().hasApicWriteAccess() || isModifiable();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      return false;
    }
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
   * @return the MoniCa file set
   */
  public SortedSet<RvwFile> getMonicaFiles() {
    final SortedSet<RvwFile> monicaFile = new TreeSet<>();
    List<RvwFile> fileList = getResponse().getIcdmFiles().get(CDRConstants.REVIEW_FILE_TYPE.MONICA_FILE.getDbType());
    if (null != fileList) {
      for (RvwFile icdmFile : fileList) {
        monicaFile.add(icdmFile);
      }
    }
    return monicaFile;
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
   * Icdm-923 restrictions to export CDR files allow only if Apic Write or cal eng or auditor
   *
   * @return if the files reviewed is downloadable
   */
  public boolean canDownloadFiles() {

    try {
      CurrentUserBO currentUser = new CurrentUserBO();
      NodeAccess curUserAccRight = currentUser.getNodeAccessRight(getPidc().getId());
      return isModifiable() || currentUser.hasApicWriteAccess() ||
          ((curUserAccRight != null) && curUserAccRight.isWrite());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      return false;
    }
  }

  /**
   * Icdm-1214
   *
   * @return the Rvw Attr Values
   */
  public Map<Long, RvwVariant> getReviewVarMap() {
    if (!isDeleted()) {
      return getResponse().getVariantsMap();
    }
    // return the map.
    return new HashMap<>();
  }

  /**
   * @return map of param id relation with Wp and Resp
   */
  public Map<Long, Map<Long, String>> getParamIdAndWpAndRespMap() {
    return getResponse().getParamIdAndWpAndRespMap();
  }

  /**
   * @return true if the result is deleted from DB and available in the Cache
   */
  public boolean isDeleted() {
    return getCDRResult() == null;
  }

  /**
   * @return the a2l wp pal object for the review result
   */
  public Set<RvwResultWPandRespModel> getA2lWpSet() {
    return getResponse().getA2lWpSet();
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
   * @return the Project ID card of this review
   */
  public Pidc getPidc() {
    return getResponse().getPidc();
  }

  /**
   * @return pidc a2l
   */
  public PidcA2l getPidcA2l() {
    return getResponse().getPidcA2l();
  }

  /**
   * @return the Rule set
   */
  public RuleSet getRuleSet() {
    return getResponse().getRuleSet();
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
      if (getReviewResultClientBO().getParticipationType(participant) == CDRConstants.REVIEW_USER_TYPE.CAL_ENGINEER) {
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
      if (getReviewResultClientBO().getParticipationType(participant) == CDRConstants.REVIEW_USER_TYPE.AUDITOR) {
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
      if (getReviewResultClientBO()
          .getParticipationType(participant) == CDRConstants.REVIEW_USER_TYPE.ADDL_PARTICIPANT) {
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
   * @return true if the current user can modify other participants
   */
  public boolean canModifyOtherParticipants() {
    try {
      CurrentUserBO currentUser = new CurrentUserBO();
      return currentUser.getUserName().equals(getCDRResult().getCreatedUser()) ||
          validateAuditorAndCalEngg(currentUser) || (checkEditAccess(currentUser.getUserID()));
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
      return false;
    }
  }


  /**
   * @param currentUser
   * @return
   * @throws ApicWebServiceException
   */
  private boolean validateAuditorAndCalEngg(final CurrentUserBO currentUser) throws ApicWebServiceException {
    return isCurrUserAuditor(currentUser) || isCurrUserCalEngg(currentUser);
  }


  /**
   * @param currentUser
   * @return
   * @throws ApicWebServiceException
   */
  private boolean isCurrUserCalEngg(final CurrentUserBO currentUser) throws ApicWebServiceException {
    return (null != getCalibrationEngineer()) &&
        (currentUser.getUserID().longValue() == getCalibrationEngineer().getUserId().longValue());
  }


  /**
   * @param currentUser
   * @return
   * @throws ApicWebServiceException
   */
  private boolean isCurrUserAuditor(final CurrentUserBO currentUser) throws ApicWebServiceException {
    return (null != getAuditor()) && (currentUser.getUserID().longValue() == getAuditor().getUserId().longValue());
  }

  /**
   * @param currnetUserId
   * @return true if the current user is an other participant and also has write access
   */
  private boolean checkEditAccess(final Long currnetUserId) {
    for (RvwParticipant otherParti : getOtherParticipants()) {
      if ((Long.compare(otherParti.getUserId(), currnetUserId) == 0) && otherParti.isEditFlag()) {
        return true;
      }
    }
    return false;
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
        // ICDM-2406
        Iterator<CDRResultParameter> iterator = getParameters().iterator();
        while ((parentParam == null) && iterator.hasNext()) {
          parentParam = getReviewResultClientBO().getParentParam(iterator.next());
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
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {

    registerParamValueChange();
    registerRvwFileChange();
    registerRvwParticipantChange();
    registerRvwResultChange();
    registerPidcAccessChange();
    registerPidcAttrChange();
    registerPidcChange();
    registerRvwCmntHistoryChange();
    registerVariant();
    registerUserPreference();


    registerCnsChecker(MODEL_TYPE.A2L_RESPONSIBILITY, chData -> {
      Long pidcId = ((A2lResponsibility) CnsUtils.getModel(chData)).getProjectId();
      return CommonUtils.isEqual(getPidc().getId(), pidcId);
    });
    registerCnsActionLocal(this::refreshA2lResp, MODEL_TYPE.A2L_RESPONSIBILITY);
    registerCnsAction(this::refreshA2lRespForRemoteChanges, MODEL_TYPE.A2L_RESPONSIBILITY);

    // Added to refresh changes in WP (such as changes in WP name) for outline view
    registerCnsChecker(MODEL_TYPE.A2L_WORK_PACKAGE, chData -> {
      Long pidcVersId = ((A2lWorkPackage) CnsUtils.getModel(chData)).getPidcVersId();
      return CommonUtils.isEqual(getPidcVersion().getId(), pidcVersId);
    });
    registerCnsActionLocal(this::refreshA2lWp, MODEL_TYPE.A2L_WORK_PACKAGE);
    registerCnsAction(this::refreshA2lWpForRemoteChanges, MODEL_TYPE.A2L_WORK_PACKAGE);

    // Added to refresh Wp finished Status in Outline view
    registerCnsChecker(MODEL_TYPE.A2L_WP_RESPONSIBILITY_STATUS, chData -> {
      A2lWpResponsibilityStatus wpResp = (A2lWpResponsibilityStatus) CnsUtils.getModel(chData);
      return this.reviewResultClientBO.getResponse().getA2lWpRespSet().stream()
          .anyMatch(model -> model.getA2lWorkPackage().getId().equals(wpResp.getA2lWpId()) &&
              model.getA2lResponsibility().getId().equals(wpResp.getA2lRespId()));
    });
    registerCnsActionLocal(this::refreshWpResp, MODEL_TYPE.A2L_WP_RESPONSIBILITY_STATUS);
    registerCnsAction(this::refreshWpRespForRemoteChanges, MODEL_TYPE.A2L_WP_RESPONSIBILITY_STATUS);


    registerCnsChecker(MODEL_TYPE.RVW_QNAIRE_RESPONSE, chData -> {
      // check if the questionnaire responses belong to the same pidc variant
      Long pidcVersId = ((RvwQnaireResponse) CnsUtils.getModel(chData)).getPidcVersId();
      return CommonUtils.isEqual(getPidcVersion().getId(), pidcVersId);
    });
    registerCnsAction(this::refreshQuesRepsonses, MODEL_TYPE.RVW_QNAIRE_RESPONSE);
    registerCnsChecker(MODEL_TYPE.RVW_QNAIRE_RESP_VARIANT, chData -> {
      // check if the questionnaire responses belong to the same pidc variant
      Long pidcVersId = ((RvwQnaireRespVariant) CnsUtils.getModel(chData)).getPidcVersId();
      return CommonUtils.isEqual(getPidcVersion().getId(), pidcVersId);
    });
    registerCnsAction(this::refreshQuesRespWithVar, MODEL_TYPE.RVW_QNAIRE_RESP_VARIANT);
    registerCnsChecker(MODEL_TYPE.RVW_QNAIRE_RESP_VERSION, chData -> {
      // check if the questionnaire responses belong to the same pidc variant
      RvwQnaireRespVersion qnaireRespVers = (RvwQnaireRespVersion) CnsUtils.getModel(chData);
      Long quesRespId = qnaireRespVers.getQnaireRespId();
      Set<QnaireRespStatusData> qnaireDataForRvwSet = getResponse().getQnaireDataForRvwSet();
      Optional<QnaireRespStatusData> qnaireDataWithStatus = qnaireDataForRvwSet.stream()
          .filter(qnaireData -> qnaireData.getQuesRespId().longValue() == quesRespId.longValue()).findFirst();
      return qnaireDataWithStatus.isPresent() && (qnaireRespVers.getRevNum().longValue() == 0l);
    });
    registerCnsAction(this::refreshQnaireRespVersion, MODEL_TYPE.RVW_QNAIRE_RESP_VERSION);

    registerCnsChecker(MODEL_TYPE.A2L_WP_DEFN_VERSION, chData -> {
      Long pidcA2lId = ((A2lWpDefnVersion) CnsUtils.getModel(chData)).getPidcA2lId();
      return CommonUtils.isEqual(getResponse().getPidcA2l().getId(), pidcA2lId);
    });
    registerCnsAction(this::refreshWpRespForChangeOfDefVersionRemoteChanges, MODEL_TYPE.A2L_WP_DEFN_VERSION);

  }


  /**
   * Register userpreference model changes to CNS. The CNS is triggered through create/update of user_preference
   */
  private void registerUserPreference() {
    // check whether the CNS change is valid or not
    registerCnsChecker(MODEL_TYPE.USER_PREFERENCE, chData -> {
      try {
        CurrentUserBO currentUser = new CurrentUserBO();
        return CommonUtils.isEqual(currentUser.getUserID(), ((UserPreference) chData.getNewData()).getUserId());
      }
      catch (ApicWebServiceException ex) {
        CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
        return false;
      }
    });
    registerCnsAction(this::refreshUserPreferenceForRemoteChanges, MODEL_TYPE.USER_PREFERENCE);
  }

  /**
   *
   */
  private void registerVariant() {
    registerCnsChecker(MODEL_TYPE.VARIANT, chData -> {
      Long pidcId = ((PidcVariant) CnsUtils.getModel(chData)).getPidcVersionId();
      return CommonUtils.isEqual(getPidcVersion().getId(), pidcId);
    });
    registerCnsActionLocal(this::refresPidcVariant, MODEL_TYPE.VARIANT);
    registerCnsAction(this::refreshPidcVariantRemote, MODEL_TYPE.VARIANT);
  }

  private void refresPidcVariant(final ChangeData<?> chData) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
      PidcVariant pidcVariant = (PidcVariant) chData.getNewData();
      if (null == getVariant()) {
        this.reviewResultClientBO.getResponse().setFirstVariant(pidcVariant);
        try {
          this.editorReviewResultName = new CDRReviewResultServiceClient()
              .getById(this.reviewResultClientBO.getResponse().getReviewResult().getId()).getName();
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }
  }

  private void refreshPidcVariantRemote(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      try {
        if (data.getChangeType().equals(CHANGE_OPERATION.CREATE) && (null == getVariant())) {
          PidcVariant pidcVariant = new PidcVariantServiceClient().get(Long.valueOf(data.getObjId()));
          this.reviewResultClientBO.getResponse().setFirstVariant(pidcVariant);
          this.editorReviewResultName = new CDRReviewResultServiceClient()
              .getById(this.reviewResultClientBO.getResponse().getReviewResult().getId()).getName();
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param chDataInfoMap changesInfoMap
   */
  public void refreshWpRespForChangeOfDefVersionRemoteChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      try {
        if (data.getChangeType().equals(CHANGE_OPERATION.CREATE) ||
            data.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
          A2lWpDefnVersion wpDefnVersion = new A2lWpDefinitionVersionServiceClient().get(Long.valueOf(data.getObjId()));
          if (wpDefnVersion.isActive()) {
            refreshWPRespStatus(wpDefnVersion);
          }
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }

  }


  /**
   * @param wpDefnVersion
   * @throws ApicWebServiceException
   */
  private void refreshWPRespStatus(final A2lWpDefnVersion wpDefnVersion) throws ApicWebServiceException {
    Long variantId = null;
    if (null != getVariant()) {
      variantId = this.reviewResultClientBO.getResponse().getFirstVariant().getId();
    }
    Map<Long, Map<Long, A2lWpResponsibilityStatus>> wpRespResponsibilityStatusMap =
        new A2lWpResponsibilityStatusServiceClient().getWpStatusByVarAndWpDefnVersId(variantId,
            (wpDefnVersion.getId()));
    Map<Long, Map<Long, String>> respWPRespStatusMap = new HashMap<>();
    if (CommonUtils.isNotEmpty(wpRespResponsibilityStatusMap)) {
      wpRespResponsibilityStatusMap.entrySet().stream().forEach(e -> {
        if (CommonUtils.isNotEmpty(e.getValue())) {
          e.getValue().values().stream()
              .forEach(wpRespStatus -> respWPRespStatusMap
                  .computeIfAbsent(wpRespStatus.getA2lRespId(), value -> new HashMap<>())
                  .put(wpRespStatus.getA2lWpId(), wpRespStatus.getWpRespFinStatus()));
        }
      });
    }
    if (!CommonUtils.isNullOrEmpty(respWPRespStatusMap)) {
      this.reviewResultClientBO.getResponse().getRespWpFinishedStatusMap().clear();
      this.reviewResultClientBO.getResponse().setRespWpFinishedStatusMap(respWPRespStatusMap);
    }
  }

  private Long getCurrentUserId() {
    try {
      return new CurrentUserBO().getUser().getId();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   *
   */
  private void registerRvwCmntHistoryChange() {

    registerCnsChecker(MODEL_TYPE.RVW_CMNT_HISTORY, chData -> {
      RvwUserCmntHistory cmntHistory = (RvwUserCmntHistory) CnsUtils.getModel(chData);
      return cmntHistory.getRvwCmntUserId().equals(getCurrentUserId());
    });
    registerCnsActionLocal(this::refreshRvwCmntHistoryLocal, MODEL_TYPE.RVW_CMNT_HISTORY);
    registerCnsAction(this::refreshRvwCmntHistory, MODEL_TYPE.RVW_CMNT_HISTORY);
  }

  private void refreshRvwCmntHistory(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    try {
      for (ChangeDataInfo data : chDataInfoMap.values()) {
        if (data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
          Map<Long, RvwUserCmntHistory> rvwCmntHistoryMap = new CommonDataBO().getRvwCommentHistoryForUser();
          rvwCmntHistoryMap.put(data.getObjId(), getRvwCmntHistory(data.getObjId()));

        }

        if (data.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
          Map<Long, RvwUserCmntHistory> rvwCmntHistoryMap = new CommonDataBO().getRvwCommentHistoryForUser();
          rvwCmntHistoryMap.remove(data.getObjId());

        }

      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  private RvwUserCmntHistory getRvwCmntHistory(final Long objectId) {
    try {
      return new RvwUserCmntHistoryServiceClient().get(objectId);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }

  private void refreshRvwCmntHistoryLocal(final ChangeData<?> chData) {

    try {
      if (chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        RvwUserCmntHistory newRvwCmntHist = (RvwUserCmntHistory) chData.getNewData();
        Map<Long, RvwUserCmntHistory> rvwCmntHistoryMap = new CommonDataBO().getRvwCommentHistoryForUser();
        rvwCmntHistoryMap.put(newRvwCmntHist.getId(), newRvwCmntHist);
      }

      else if (chData.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
        RvwUserCmntHistory newRvwCmntHist = (RvwUserCmntHistory) chData.getOldData();
        Map<Long, RvwUserCmntHistory> rvwCmntHistoryMap = new CommonDataBO().getRvwCommentHistoryForUser();
        rvwCmntHistoryMap.remove(newRvwCmntHist.getId());
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param chDataInfoMap Map<Long, ChangeDataInfo>
   */
  private void refreshQnaireRespVersion(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.CREATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
        Set<QnaireRespStatusData> qnaireDataForRvwSet = getResponse().getQnaireDataForRvwSet();
        RvwQnaireRespVersion respVersion;
        try {
          respVersion = new RvwQnaireRespVersionServiceClient().getById(data.getObjId());
          Optional<QnaireRespStatusData> qnaireDataToUpdate = qnaireDataForRvwSet.stream()
              .filter(qnaireData -> qnaireData.getQuesRespId().longValue() == respVersion.getQnaireRespId().longValue())
              .findFirst();

          // if there is this qnaire response data in the set
          if (qnaireDataToUpdate.isPresent()) {
            QnaireRespStatusData qnaireRespStatusData = qnaireDataToUpdate.get();
            qnaireRespStatusData.setStatus(respVersion.getQnaireRespVersStatus());
          }

        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    }
  }

  /**
   * @param chDataInfoMap Map<Long, ChangeDataInfo>
   */
  private void refreshQuesRespWithVar(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    Set<QnaireRespStatusData> qnaireDataForRvwSet = getResponse().getQnaireDataForRvwSet();
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        try {
          // get response objects
          RvwQnaireRespVariant qnaireRespVar =
              new RvwQnaireRespVariantServiceClient().getRvwQnaireRespVariant(data.getObjId());
          Long qnaireRespId = qnaireRespVar.getQnaireRespId();
          // qnaireRespId is Null for Simplified Qnaire
          if (CommonUtils.isNotNull(qnaireRespId)) {
            RvwQnaireResponse quesResp = new RvwQnaireResponseServiceClient().getById(qnaireRespId);
            // check if the response's wp and resp name are part of review result model
            A2lResponsibility a2lResponsibility = getA2lResponsibilityMap().get(qnaireRespVar.getA2lRespId());
            if ((null != a2lResponsibility) && ((getVariant() == null) || (CommonUtils
                .isEqual(getReviewResultClientBO().getVarFromPidTree().getId(), qnaireRespVar.getVariantId())))) {
              addRespToSet(qnaireDataForRvwSet, quesResp, a2lResponsibility);
            }
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
      else if (data.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
        // remove the response from the list
        RvwQnaireRespVariant respVar = (RvwQnaireRespVariant) data.getRemovedData();

        Long qnaireRespId = respVar.getQnaireRespId();
        // qnaireRespId is Null for Simplified Qnaire
        if (CommonUtils.isNotNull(qnaireRespId)) {
          removeRespFromList(qnaireRespId, qnaireDataForRvwSet);
        }
      }
    }
  }

  /**
   * Method to refresh A2lWpResponsibility related changes.
   *
   * @param chData the ch data
   */
  public void refreshWpResp(final ChangeData<?> chData) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
      A2lWpResponsibilityStatus a2lWpResponsibilityStatus = (A2lWpResponsibilityStatus) chData.getNewData();
      addA2lWpResponseStatus(a2lWpResponsibilityStatus);
    }
    else if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
      A2lWpResponsibilityStatus oldWpResp = (A2lWpResponsibilityStatus) chData.getOldData();
      if (CommonUtils.isNotNull(oldWpResp)) {
        Map<Long, String> respWpFinishedStatusMap =
            this.reviewResultClientBO.getResponse().getRespWpFinishedStatusMap().get(oldWpResp.getA2lRespId());
        respWpFinishedStatusMap.put(oldWpResp.getA2lWpId(),
            ((A2lWpResponsibilityStatus) chData.getNewData()).getWpRespFinStatus());
      }
    }
  }


  /**
   * @param a2lWpResponsibilityStatus
   */
  private void addA2lWpResponseStatus(final A2lWpResponsibilityStatus a2lWpResponsibilityStatus) {
    if (this.reviewResultClientBO.getResponse().getRespWpFinishedStatusMap()
        .containsKey(a2lWpResponsibilityStatus.getA2lRespId())) {
      Map<Long, String> wpAndStatusMap = this.reviewResultClientBO.getResponse().getRespWpFinishedStatusMap()
          .get(a2lWpResponsibilityStatus.getA2lRespId());
      wpAndStatusMap.put(a2lWpResponsibilityStatus.getA2lWpId(), a2lWpResponsibilityStatus.getWpRespFinStatus());
    }
    else {
      Map<Long, String> wpAndStatusMap = new HashMap<>();
      wpAndStatusMap.put(a2lWpResponsibilityStatus.getA2lWpId(), a2lWpResponsibilityStatus.getWpRespFinStatus());
      this.reviewResultClientBO.getResponse().getRespWpFinishedStatusMap().put(a2lWpResponsibilityStatus.getA2lRespId(),
          wpAndStatusMap);
    }
  }

  /**
   * Method to refresh A2lWpResponsibility related changes for remote .
   */
  public void refreshWpRespForRemoteChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      try {
        A2lWpResponsibilityStatus a2lWpRespStatusNew =
            new A2lWpResponsibilityStatusServiceClient().get(Long.valueOf(data.getObjId()));
        if (data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
          addA2lWpResponseStatus(a2lWpRespStatusNew);
        }
        else if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
          Map<Long, String> respWpFinishedStatusMap = this.reviewResultClientBO.getResponse()
              .getRespWpFinishedStatusMap().get(a2lWpRespStatusNew.getA2lRespId());
          respWpFinishedStatusMap.put(a2lWpRespStatusNew.getA2lWpId(), a2lWpRespStatusNew.getWpRespFinStatus());
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param chDataInfoMap Map<Long, ChangeDataInfo>
   */
  private void refreshQuesRepsonses(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    Set<QnaireRespStatusData> qnaireDataForRvwSet = getResponse().getQnaireDataForRvwSet();
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      try {
        if (data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
          RvwQnaireResponse qnaireResp = new RvwQnaireResponseServiceClient().getById(Long.valueOf(data.getObjId()));
          createQnaireIfWPRespExists(qnaireDataForRvwSet, qnaireResp);
        }
        else if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
          RvwQnaireResponse qnaireResp = new RvwQnaireResponseServiceClient().getById(Long.valueOf(data.getObjId()));
          if (qnaireResp.isDeletedFlag()) {
            // remove the response from the list
            removeRespFromList(qnaireResp.getId(), qnaireDataForRvwSet);
          }
          else {
            checkForUndeleteOperataion(qnaireResp, qnaireDataForRvwSet);
          }
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }


  }


  /**
   * @param qnaireResp
   * @param qnaireDataForRvwSet
   * @throws ApicWebServiceException
   */
  private void checkForUndeleteOperataion(final RvwQnaireResponse qnaireResp,
      final Set<QnaireRespStatusData> qnaireDataForRvwSet)
      throws ApicWebServiceException {
    if (qnaireDataForRvwSet.stream().noneMatch(data -> data.getQuesRespId().equals(qnaireResp.getId()))) {
      // if the qnaire resp is not there in the set , this is a case of un-delete
      createQnaireIfWPRespExists(qnaireDataForRvwSet, qnaireResp);
    }
  }


  /**
   * @param qnaireDataForRvwSet
   * @param qnaireResp
   * @throws ApicWebServiceException
   */
  private void createQnaireIfWPRespExists(final Set<QnaireRespStatusData> qnaireDataForRvwSet,
      final RvwQnaireResponse qnaireResp)
      throws ApicWebServiceException {
    // check if the response's wp and resp name are part of review result model
    A2lResponsibility a2lResponsibility = getA2lResponsibilityMap().get(qnaireResp.getA2lRespId());
    if ((null != a2lResponsibility) && ((getVariant() == null) || (CommonUtils
        .isEqual(getReviewResultClientBO().getVarFromPidTree().getVariantId(), qnaireResp.getVariantId())))) {
      addRespToSet(qnaireDataForRvwSet, qnaireResp, a2lResponsibility);
    }
  }


  /**
   * @param qnaireDataForRvwSet
   * @param qnaireResp
   * @param a2lResponsibility
   * @throws ApicWebServiceException
   */
  private void addRespToSet(final Set<QnaireRespStatusData> qnaireDataForRvwSet, final RvwQnaireResponse qnaireResp,
      final A2lResponsibility a2lResponsibility)
      throws ApicWebServiceException {
    // check if the variants are same
    Set<RvwResultWPandRespModel> a2lWpRespModel = getRespAndA2lWPMap().get(a2lResponsibility.getName());
    for (RvwResultWPandRespModel wpRespModel : a2lWpRespModel) {
      if (wpRespModel.getA2lWorkPackage().getId().longValue() == qnaireResp.getA2lWpId().longValue()) {
        // wp and resp id found in review result
        // create new data object
        QnaireRespStatusData newqnaireRespData = new QnaireRespStatusData();
        newqnaireRespData.setQuesRespId(qnaireResp.getId());
        newqnaireRespData.setPrimaryVarName(qnaireResp.getPrimaryVarRespWpName());
        newqnaireRespData.setQnaireRespName(qnaireResp.getName());
        newqnaireRespData.setRespName(a2lResponsibility.getName());
        newqnaireRespData.setWpName(wpRespModel.getA2lWorkPackage().getName());
        RvwQnaireRespVersionServiceClient client = new RvwQnaireRespVersionServiceClient();
        Map<Long, RvwQnaireRespVersion> qnaireRespVersionsByRespId =
            client.getQnaireRespVersionsByRespId(qnaireResp.getId());
        RvwQnaireRespVersion workingSet = findWorkingSet(qnaireRespVersionsByRespId);
        if (null != workingSet) {
          newqnaireRespData.setStatus(workingSet.getQnaireRespVersStatus());
        }
        qnaireDataForRvwSet.add(newqnaireRespData);
        break;
      }
    }
  }

  /**
   * @param qnaireRespId
   * @param qnaireDataForRvwSet
   */
  private void removeRespFromList(final Long qnaireRespId, final Set<QnaireRespStatusData> qnaireDataForRvwSet) {
    qnaireDataForRvwSet
        .removeIf((final QnaireRespStatusData data) -> data.getQuesRespId().longValue() == qnaireRespId.longValue());

  }


  /**
   * @param qnaireRespVersionsByRespId
   */
  private RvwQnaireRespVersion findWorkingSet(final Map<Long, RvwQnaireRespVersion> qnaireRespVersionsByRespId) {
    for (RvwQnaireRespVersion respVersion : qnaireRespVersionsByRespId.values()) {
      if (respVersion.getRevNum() == 0) {
        return respVersion;
      }
    }
    return null;
  }


  private void refreshA2lRespForRemoteChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // fetch data using service call for remote refresh
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        try {
          A2lResponsibility a2lResp = new A2lResponsibilityServiceClient().get(Long.valueOf(data.getObjId()));
          A2lResponsibility a2lRespOld = getA2lResponsibilityMap().get(a2lResp.getId());
          getA2lResponsibilityMap().put(data.getObjId(), a2lResp);
          updateA2lRespInReviewResultEditor(a2lResp, a2lRespOld);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }
  }


  /**
   * Method to refresh A2lWpResponsibility related changes.
   *
   * @param chData the ch data
   */
  private void refreshA2lResp(final ChangeData<?> chData) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
        chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
      A2lResponsibility a2lResp = (A2lResponsibility) chData.getNewData();
      A2lResponsibility a2lRespOld = (A2lResponsibility) chData.getOldData();
      updateA2lRespInReviewResultEditor(a2lResp, a2lRespOld);
    }
  }

  /**
   * remote refresh for change in user_preference model
   *
   * @param chDataInfoMap
   */
  private void refreshUserPreferenceForRemoteChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo chData : chDataInfoMap.values()) {
      if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        try {
          new CurrentUserBO().clearCurrentUserCacheADGroupDelete();
        }
        catch (ApicWebServiceException ex) {
          CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
        }
      }
    }
  }

  private void refreshA2lWpForRemoteChanges(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // fetch data using service call for remote refresh
    // Refreshing for Update only
    // since new work package mappings will not applicable for already done review results
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
        try {
          A2lWorkPackage a2lWp = new A2lWorkPackageServiceClient().getById(Long.valueOf(data.getObjId()));

          updateA2lWpForOutline(a2lWp);

        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }
  }

  /**
   * Method to refresh A2lWorkPackage related changes.
   *
   * @param chData the ch data
   */
  private void refreshA2lWp(final ChangeData<?> chData) {
    // Refreshing for Update only
    // since new work package mappings will not applicable for already done review results
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
      A2lWorkPackage a2lWp = (A2lWorkPackage) chData.getNewData();

      updateA2lWpForOutline(a2lWp);

    }
  }

  /**
   * @param a2lWp
   */
  private void updateA2lWpForOutline(final A2lWorkPackage a2lWp) {
    getA2lWpSet().forEach(rvwResultWPandRespModel -> {
      if (rvwResultWPandRespModel.getA2lWorkPackage().getId().equals(a2lWp.getId())) {
        rvwResultWPandRespModel.getA2lWorkPackage().setName(a2lWp.getName());
      }
    });

    getRespAndA2lWPMap().values().forEach(rvwResultWPandRespModelSet -> {
      for (RvwResultWPandRespModel rvwResultWPandRespModel : rvwResultWPandRespModelSet) {
        if (rvwResultWPandRespModel.getA2lWorkPackage().getId().equals(a2lWp.getId())) {
          rvwResultWPandRespModel.getA2lWorkPackage().setName(a2lWp.getName());
        }
      }
    });
  }


  /**
   * @param a2lResp
   * @param a2lRespOld
   */
  private void updateA2lRespInReviewResultEditor(final A2lResponsibility a2lResp, final A2lResponsibility a2lRespOld) {

    if (null != getA2lWpSet()) {
      getA2lWpSet().forEach(wpRespSet -> {
        if ((null != wpRespSet.getA2lResponsibility()) && (null != wpRespSet.getA2lResponsibility().getName())) {
          wpRespSet.setA2lResponsibility(wpRespSet.getA2lResponsibility());
        }
      });
    }
    setWpRespForUpdateA2lResp(a2lResp, a2lRespOld);
    if ((null != a2lRespOld) && getRespAndA2lWPMap().containsKey(a2lRespOld.getName())) {
      Set<RvwResultWPandRespModel> resultWPandRespModels = getRespAndA2lWPMap().get(a2lRespOld.getName());
      resultWPandRespModels.forEach(wpAndRespModel -> wpAndRespModel.setA2lResponsibility(a2lResp));
      getRespAndA2lWPMap().remove(a2lRespOld.getName());
      getRespAndA2lWPMap().put(a2lResp.getName(), resultWPandRespModels);
    }
  }


  /**
   * @param a2lResp
   * @param a2lRespOld
   */
  private void setWpRespForUpdateA2lResp(final A2lResponsibility a2lResp, final A2lResponsibility a2lRespOld) {
    if ((null != getParamIdAndWpAndRespMap())) {
      getParamIdAndWpAndRespMap().entrySet().forEach(wpAndRespMap -> {
        if ((null != wpAndRespMap) && (null != wpAndRespMap.getValue())) {
          wpAndRespMap.getValue().entrySet().forEach(wpResp -> {
            if ((null != wpResp) && (null != a2lRespOld) &&
                CommonUtils.isEqual(wpResp.getValue(), a2lRespOld.getName())) {
              wpResp.setValue(a2lResp.getName());
            }
          });
        }
      });
    }
  }

  /**
   *
   */
  private void registerPidcChange() {
    registerCnsChecker(MODEL_TYPE.PIDC_VERSION, chData -> {
      Long pidcVersId = ((PidcVersion) CnsUtils.getModel(chData)).getId();
      return pidcVersId.equals(getPidcVersion().getId());
    });

  }

  /**
   *
   */
  private void registerPidcAttrChange() {
    registerCnsChecker(MODEL_TYPE.PROJ_ATTR, chData -> {
      Long pidcVersId = ((PidcVersionAttribute) CnsUtils.getModel(chData)).getPidcVersId();

      return pidcVersId.equals(getPidcVersion().getId());
    });
    registerCnsChecker(MODEL_TYPE.VAR_ATTR, chData -> {
      Long pidcVersId = ((PidcVariantAttribute) CnsUtils.getModel(chData)).getPidcVersionId();

      return pidcVersId.equals(getPidcVersion().getId());
    });
  }

  /**
   *
   */
  private void registerPidcAccessChange() {
    registerCnsChecker(MODEL_TYPE.NODE_ACCESS, chData -> {
      Long nodeId = ((NodeAccess) CnsUtils.getModel(chData)).getNodeId();
      return nodeId.equals(getPidcVersion().getPidcId());
    });
  }


  /**
   *
   */
  private void registerRvwResultChange() {
    registerCnsChecker(MODEL_TYPE.CDR_RESULT, chData -> {
      Long resultId = ((CDRReviewResult) CnsUtils.getModel(chData)).getId();
      return resultId.equals(getCDRResult().getId());
    });
    registerCnsAction(this::refreshRvwResult, MODEL_TYPE.CDR_RESULT);
  }

  /**
   *
   */
  private void registerRvwParticipantChange() {
    registerCnsChecker(MODEL_TYPE.CDR_PARTICIPANT, chData -> {
      Long resultId = ((RvwParticipant) CnsUtils.getModel(chData)).getResultId();
      return resultId.equals(getCDRResult().getId());
    });
    registerCnsAction(this::refreshRvwParticipant, MODEL_TYPE.CDR_PARTICIPANT);
  }

  /**
   *
   */
  private void registerRvwFileChange() {
    registerCnsChecker(MODEL_TYPE.CDR_RES_FILE, chData -> {
      Long resultId = ((RvwFile) CnsUtils.getModel(chData)).getResultId();
      return resultId.equals(getCDRResult().getId());
    });
    registerCnsAction(this::refreshReviewFile, MODEL_TYPE.CDR_RES_FILE);
  }


  /**
   *
   */
  private void registerParamValueChange() {
    registerCnsChecker(MODEL_TYPE.CDR_RES_PARAMETER, chData -> {
      Long paramId = ((CDRResultParameter) CnsUtils.getModel(chData)).getId();
      return getParametersMap().containsKey(paramId);
    });
    registerCnsAction(this::refreshParamValues, MODEL_TYPE.CDR_RES_PARAMETER);
    registerCnsActionLocal(this::mergeParamValue, MODEL_TYPE.CDR_RES_PARAMETER);
  }

  private void mergeParamValue(final ChangeData<?> changeData) {
    CDRResultParameter newResultParam = (CDRResultParameter) changeData.getNewData();
    // update the map used in first page
    CDRResultParameter oldResultParam = getParametersMap().get(newResultParam.getId());
    CommonUtils.shallowCopy(oldResultParam, newResultParam);
    // update the map used in second page
    List<CDRResultParameter> paramList = getResponse().getFuncParamMap().get(newResultParam.getRvwFunId());
    for (CDRResultParameter param : paramList) {
      if (param.getId().equals(newResultParam.getId())) {
        CommonUtils.shallowCopy(param, newResultParam);
      }
    }
  }

  private void refreshParamValues(final Map<Long, ChangeDataInfo> chInfoMap) {
    try {
      ChangeDataInfo dataInfo = chInfoMap.values().iterator().next();
      if (!dataInfo.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
        Map<Long, CDRResultParameter> newParamsMap =
            new CDRResultParameterServiceClient().getMultiple(chInfoMap.keySet());
        newParamsMap.values().forEach(nPrm -> CommonUtils.shallowCopy(getParametersMap().get(nPrm.getId()), nPrm));
        for (CDRResultParameter newParam : newParamsMap.values()) {
          // update the map used in second page
          List<CDRResultParameter> paramList = getResponse().getFuncParamMap().get(newParam.getRvwFunId());
          for (CDRResultParameter param : paramList) {
            if (param.getId().equals(newParam.getId())) {
              CommonUtils.shallowCopy(param, newParam);
            }
          }
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  private void refreshRvwResult(final Map<Long, ChangeDataInfo> chInfoMap) {

    try {
      ChangeDataInfo dataInfo = chInfoMap.values().iterator().next();
      if (!dataInfo.getChangeType().equals(CHANGE_OPERATION.DELETE)) {

        Map<Long, CDRReviewResult> newResultsMap = new CDRReviewResultServiceClient().getMultiple(chInfoMap.keySet());
        CDRReviewResult newReviewResult = newResultsMap.get(getCDRResult().getId());
        getCDRResult().setLockStatus(newReviewResult.getLockStatus());
        getCDRResult().setReviewType(newReviewResult.getReviewType());
        getCDRResult().setDescription(newReviewResult.getDescription());
        CommonUtils.shallowCopy(getCDRResult(), newReviewResult);
      }

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  private void refreshReviewFile(final Map<Long, ChangeDataInfo> chInfoMap) {

    try {
      Map<Long, RvwFile> newRvwFileMap = new HashMap<>();
      ChangeDataInfo changeData = chInfoMap.values().iterator().next();
      if (changeData.getChangeType() == CHANGE_OPERATION.CREATE) {
        refreshReviewFileCreate(chInfoMap, newRvwFileMap);
      }

      else if (changeData.getChangeType() == CHANGE_OPERATION.DELETE) {
        refreshReviewFileDelete(chInfoMap);
      }

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param chInfoMap
   */
  private void refreshReviewFileDelete(final Map<Long, ChangeDataInfo> chInfoMap) {
    for (ChangeDataInfo oldChangeData : chInfoMap.values()) {
      RvwFile oldReviewFile = (RvwFile) oldChangeData.getRemovedData();

      if (CDRConstants.REVIEW_FILE_TYPE.RVW_PRM_ADDL_FILE.getDbType().equals(oldReviewFile.getFileType())) {
        getResponse().getParamAdditionalFiles().get(oldReviewFile.getRvwParamId()).remove(oldReviewFile);
      }
      else if (CDRConstants.REVIEW_FILE_TYPE.RVW_ADDL_FILE.getDbType().equals(oldReviewFile.getFileType())) {
        getResponse().getIcdmFiles().get(CDRConstants.REVIEW_FILE_TYPE.RVW_ADDL_FILE.getDbType()).remove(oldReviewFile);
      }
    }
  }


  /**
   * @param chInfoMap
   * @param newRvwFileMap
   * @throws ApicWebServiceException
   */
  private void refreshReviewFileCreate(final Map<Long, ChangeDataInfo> chInfoMap, Map<Long, RvwFile> newRvwFileMap)
      throws ApicWebServiceException {
    if (newRvwFileMap.isEmpty()) {
      newRvwFileMap = new RvwFileServiceClient().getMultiple(chInfoMap.keySet());
    }
    for (RvwFile newReviewFile : newRvwFileMap.values()) {
      if (CDRConstants.REVIEW_FILE_TYPE.RVW_PRM_ADDL_FILE.getDbType().equals(newReviewFile.getFileType())) {
        List<RvwFile> fileList = getResponse().getParamAdditionalFiles().get(newReviewFile.getRvwParamId());
        if (fileList == null) {
          fileList = new ArrayList<>();
        }
        fileList.add(newReviewFile);
        getResponse().getParamAdditionalFiles().put(newReviewFile.getRvwParamId(), fileList);
      }
      else if (CDRConstants.REVIEW_FILE_TYPE.RVW_ADDL_FILE.getDbType().equals(newReviewFile.getFileType())) {
        List<RvwFile> fileList =
            getResponse().getIcdmFiles().get(CDRConstants.REVIEW_FILE_TYPE.RVW_ADDL_FILE.getDbType());
        if (fileList == null) {
          fileList = new ArrayList<>();
        }
        fileList.add(newReviewFile);
        getResponse().getIcdmFiles().put(CDRConstants.REVIEW_FILE_TYPE.RVW_ADDL_FILE.getDbType(), fileList);
      }
    }
  }


  private void refreshRvwParticipant(final Map<Long, ChangeDataInfo> chInfoMap) {
    try {

      ChangeDataInfo changeData = chInfoMap.values().iterator().next();

      if (changeData.getChangeType() == CHANGE_OPERATION.CREATE) {
        refreshRvwParticipantCreate(chInfoMap);
      }
      else if (changeData.getChangeType() == CHANGE_OPERATION.UPDATE) {
        refreshRvwParticipantUpdate(chInfoMap);
      }
      else if (changeData.getChangeType() == CHANGE_OPERATION.DELETE) {
        refreshRvwParticipantDelete(chInfoMap);
      }

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param chInfoMap
   */
  private void refreshRvwParticipantDelete(final Map<Long, ChangeDataInfo> chInfoMap) {
    for (ChangeDataInfo oldChangeData : chInfoMap.values()) {
      getResponse().getParticipantsMap().remove(oldChangeData.getRemovedData().getId());
    }
  }


  /**
   * @param chInfoMap
   * @throws ApicWebServiceException
   */
  private void refreshRvwParticipantUpdate(final Map<Long, ChangeDataInfo> chInfoMap) throws ApicWebServiceException {
    Map<Long, RvwParticipant> newParticipantsMap;
    newParticipantsMap = new RvwParticipantServiceClient().getMultiple(chInfoMap.keySet());
    for (RvwParticipant newParticipant : newParticipantsMap.values()) {
      if (REVIEW_USER_TYPE.AUDITOR.getDbType().equals(newParticipant.getActivityType()) ||
          REVIEW_USER_TYPE.ADDL_PARTICIPANT.getDbType().equals(newParticipant.getActivityType())) {
        getResponse().getParticipantsMap().put(newParticipant.getId(), newParticipant);
      }
    }
  }


  /**
   * @param chInfoMap
   * @throws ApicWebServiceException
   */
  private void refreshRvwParticipantCreate(final Map<Long, ChangeDataInfo> chInfoMap) throws ApicWebServiceException {
    Map<Long, RvwParticipant> newParticipantsMap;
    newParticipantsMap = new RvwParticipantServiceClient().getMultiple(chInfoMap.keySet());
    for (RvwParticipant newParticipant : newParticipantsMap.values()) {
      if (REVIEW_USER_TYPE.ADDL_PARTICIPANT.getDbType().equals(newParticipant.getActivityType())) {
        getResponse().getParticipantsMap().put(newParticipant.getId(), newParticipant);
      }
    }
  }


  /**
   * @return the response
   */
  private ReviewResultEditorData getResponse() {
    return getReviewResultClientBO().getResponse();
  }

  /**
   * @return boolean
   * @throws ApicWebServiceException as exception
   */
  public boolean isUsedInCDFXDelivery() throws ApicWebServiceException {
    return new CDRReviewResultServiceClient().isUsedInCDFXDelivery(getResponse().getReviewResult().getId());
  }

  /**
   * Checks if atleast params are Checked iCDM-665
   *
   * @return true if atleast one param is checked
   */
  public boolean isAtleastOneParamChecked() {
    final Collection<CDRResultParameter> rvwParams = getParametersMap().values();
    // check if all params are reviewed

    if (rvwParams.isEmpty()) {
      return false;
    }

    for (CDRResultParameter param : rvwParams) {
      if (isParamChecked(param)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks if all params are Reviewed
   *
   * @return true if all param is checked
   */

  public boolean isAllParamReviewed() {
    final Collection<CDRResultParameter> rvwParams = getParametersMap().values();
    // check if all params are reviewed
    if (rvwParams.isEmpty()) {
      return false;
    }
    for (CDRResultParameter param : rvwParams) {
      if (!DATA_REVIEW_SCORE.getType(param.getReviewScore()).isChecked()) {
        return false;
      }
    }
    return true;

  }

  /**
   * Checks if all params are Checked or ARC Marked
   *
   * @return true if all param is checked or ARC Marked
   */

  public boolean isAllParamCheckedOrARCReleased() {
    final Collection<CDRResultParameter> rvwParams = getParametersMap().values();
    // check if all params are reviewed
    if (rvwParams.isEmpty()) {
      return false;
    }
    for (CDRResultParameter param : rvwParams) {
      if (!DATA_REVIEW_SCORE.getType(param.getReviewScore()).isChecked() && !param.getArcReleasedFlag()) {
        return false;
      }
    }
    return true;

  }

  /**
   * Checks if all params are Checked
   *
   * @return true if all param is checked
   */

  public boolean checkIfAllParamsAreReviewed() {
    final Collection<CDRResultParameter> rvwParams = getParametersMap().values();
    // check if all params are reviewed
    if (rvwParams.isEmpty()) {
      return false;
    }
    for (CDRResultParameter param : rvwParams) {
      if (!DATA_REVIEW_SCORE.getType(param.getReviewScore()).isReviewed()) {
        return false;
      }
    }
    return true;

  }

  /**
   * @return different msg based on start or official review
   */
  public String checkOfficialOrStartReview() {
    return CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType().equals(getCDRResult().getReviewType())
        ? CDRConstants.WARN_MSG_TO_FILL_UNFILLED_QNAIRE_RESP
        : CDRConstants.WARN_MSG_TO_FILL_UNFILLED_QNAIRE_RESP_FOR_START_REVIEW;
  }

  /**
   * @param param
   * @return
   */
  private boolean isParamChecked(final CDRResultParameter param) {
    DATA_REVIEW_SCORE paramScore = DATA_REVIEW_SCORE.getType(param.getReviewScore());

    // Official reviews - check for scores 8, 9
    if (CommonUtils.isEqual(getCDRResult().getReviewType(), CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType())) {
      return paramScore.isChecked();
    }

    // Non official reviews - check for score 7, 8
    return (paramScore == DATA_REVIEW_SCORE.S_7) || (paramScore == DATA_REVIEW_SCORE.S_8);
  }

  /**
   * Returns all the Parameters with score less then 8 and score more than 0
   *
   * @return List<CDRResultParameter>
   */

  public List<CDRResultParameter> getARCReleaseParams() {
    final Collection<CDRResultParameter> rvwParams = getParametersMap().values();
    List<CDRResultParameter> arcReleaseParams = new ArrayList<>();
    for (CDRResultParameter param : rvwParams) {
      if (!DATA_REVIEW_SCORE.getType(param.getReviewScore()).isChecked() &&
          DATA_REVIEW_SCORE.getType(param.getReviewScore()).isReviewed() && !param.getArcReleasedFlag()) {
        arcReleaseParams.add(param);
      }
    }
    return arcReleaseParams;

  }


  /**
   * Forms a Map of RespType and A2lResponsibility from available Work Package - A2lResponsibility Mapping
   *
   * @return map of Key - String of Resposibility Tyep and Value - Set<A2lResponsibility> of Resposibility
   */
  public Map<String, Set<A2lResponsibility>> getRespTypeAndRespMap() {
    Map<String, Set<A2lResponsibility>> respTypeAndRespMap = new TreeMap<>();

    getRespAndA2lWPMap().keySet().forEach(respName -> {
      for (A2lResponsibility a2lResponsibility : getA2lResponsibilityMap().values()) {
        if (a2lResponsibility.getName().equals(respName)) {
          String key = WpRespType.getType(a2lResponsibility.getRespType()).getDispName();
          if (respTypeAndRespMap.get(key) == null) {
            Set<A2lResponsibility> value = new HashSet<>();
            value.add(a2lResponsibility);
            respTypeAndRespMap.put(key, value);
          }
          else {
            Set<A2lResponsibility> value = respTypeAndRespMap.get(key);
            value.add(a2lResponsibility);
            respTypeAndRespMap.put(key, value);
          }
        }
      }
    });

    return respTypeAndRespMap;
  }

}
