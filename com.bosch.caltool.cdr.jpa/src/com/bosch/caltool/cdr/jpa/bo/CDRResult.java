/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.calcomp.externallink.ILinkableObject;
import com.bosch.caltool.apic.jpa.bo.A2LFile;
import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.apic.jpa.bo.IcdmFile;
import com.bosch.caltool.apic.jpa.bo.NodeAccessRight;
import com.bosch.caltool.apic.jpa.bo.PIDCVariant;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;
import com.bosch.caltool.apic.jpa.bo.PIDCard;
import com.bosch.caltool.cdr.jpa.bo.shapereview.ShapeReviewResult;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSet;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwAttrValue;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFile;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParticipant;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResultsSecondary;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwVariant;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.CDR_SOURCE_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_LOCK_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;


/**
 * @author bne4cob
 */
@Deprecated
public class CDRResult extends AbstractCdrObject implements Comparable<CDRResult>, ILinkableObject {


  /**
   * CDRResult Status
   */
  public static final String FLD_REVIEW_STATUS = "CDR_RESULT_STATUS";

  /**
   * CDRResult FC2WP_ID
   */
  public static final String FLD_RVW_FC2WP_ID = "CDR_RESULT_FC2WP_ID";

  /**
   * CDRResult GROUP_NAME
   */
  public static final String FLD_RVW_GRP_NAME = "CDR_RESULT_GROUP_NAME";

  /**
   * CDRResult Description
   */
  public static final String FLD_RVW_DESC = "CDR_RESULT_DESCRIPTION";

  /**
   * ADDITIONAL_FILES constant
   */
  private static final String ADDITIONAL_FILES = "ADDITIONAL_FILES";
  /**
   * Cdr Review Source Type
   */
  // Icdm-729
  public static final String FLD_RVW_SRC_TYPE = "CDR_RESULT_SOURCE_TYPE";

  /**
   * Cdr Review lock Type
   */
  // Icdm-729
  public static final String FLD_RVW_LCK_TYPE = "CDR_RESULT_LOCK_TYPE";
  /**
   * FLD_RVW_RVW_TYPE constant
   */
  private static final String FLD_RVW_RVW_TYPE = "CDR_RESULT_REVIEW_TYPE";
  /**
   * constant for PIDC_VERSION
   */
  public static final String FLD_PIDC_VERSION_ID = "PIDC_VERSION_ID";
  /**
   * Tooltip buffer initial size
   */
  private static final int TOOLTIP_INITIAL_SIZE = 50;
  /**
   * Map of CDR Result Functions
   */
  private Map<Long, CDRResultFunction> resFuncMap;

  /**
   * Map of CDR Result Parameters
   */
  private Map<Long, CDRResultParameter> resParamMap;

  /**
   * Participants of this review
   */
  protected Map<Long, CDRParticipant> participantMap;
  /**
   * Icdm-1214 - new Map for Review Attr Values
   */
  private final Map<Long, CDRReviewAttrValue> rvwAttrValMap = new ConcurrentHashMap<>();

  /**
   * Icdm-1214 - new Map for Review Variants
   */
  private final Map<Long, CDRReviewVariant> rvwVarMap = new ConcurrentHashMap<>();
  /**
   * linked variant to cdr result.
   */
  private PIDCVariant linkedVar;
  /**
   * Main variant
   */
  private PIDCVariant firstVariant;

  /**
   * secondary results
   */
  private SortedSet<CDRSecondaryResult> secondaryResSet;

  private ShapeReviewResult srResult;

  private Map<String, CDRResultParameter> resultParamNameMap;

  /**
   * Constructor
   *
   * @param dataProvider the data provider
   * @param objID resultID
   */
  protected CDRResult(final CDRDataProvider dataProvider, final Long objID) {
    super(dataProvider, objID);
    dataProvider.getDataCache().getAllCDRResults().put(objID, this);
  }


  /**
   * @return the linkedVar
   */
  public PIDCVariant getLinkedVar() {
    return this.linkedVar;
  }


  /**
   * @return the seconadry Result Set.
   */
  public SortedSet<CDRSecondaryResult> getSecondaryResults() {
    if (CommonUtils.isNullOrEmpty(this.secondaryResSet)) {
      fillSecondaryResult();
    }
    return this.secondaryResSet;
  }

  /**
   * @return the secondary Result reloaded
   */
  public SortedSet<CDRSecondaryResult> getSecondaryResReloaded() {
    fillSecondaryResult();
    return this.secondaryResSet;
  }


  /**
   *
   */
  private void fillSecondaryResult() {
    this.secondaryResSet = new TreeSet<>();
    Set<TRvwResultsSecondary> tRvwResultsSecondaries =
        getEntityProvider().getDbCDRResult(getID()).getTRvwResultsSecondaries();
    if (tRvwResultsSecondaries != null) {
      for (TRvwResultsSecondary tRvwResultsSecondary : tRvwResultsSecondaries) {
        CDRSecondaryResult result = new CDRSecondaryResult(getDataProvider(), tRvwResultsSecondary.getSecReviewId());
        this.secondaryResSet.add(result);
      }

    }
  }

  /**
   * @return the Project ID card of this review
   */
  public PIDCVersion getPidcVersion() {
    // TODO change this
    final Long pidcVersID = getEntityProvider().getDbCDRResult(getID()).getTPidcA2l().getTPidcVersion().getPidcVersId();
    PIDCVersion pidcVersion = null;
    // Use the new method to make sure that the entity is not null.
    try {
      pidcVersion = getApicDataProvider().getPidcVersion(pidcVersID, true);
    }
    catch (DataException dataException) {
      CDMLogger.getInstance().error(CommonUtils.concatenate(dataException, dataException.getMessage()));
    }

    return pidcVersion;
  }

  /**
   * @return the Variant used for this review
   */
  public PIDCVariant getVariant() {
    if (this.firstVariant == null) {
      Timestamp createdDate = null;
      if (CommonUtils.isNotNull(getEntityProvider().getDbCDRResult(getID()).getTRvwVariants())) {
        Iterator<TRvwVariant> iterator = getEntityProvider().getDbCDRResult(getID()).getTRvwVariants().iterator();
        while (iterator.hasNext()) {
          TRvwVariant dbRvwVar = iterator.next();
          if ((createdDate == null) || createdDate.after(dbRvwVar.getCreatedDate())) {
            createdDate = dbRvwVar.getCreatedDate();
            this.firstVariant = getVarFromRvwVar(dbRvwVar);
          }
        }
      }
    }
    return this.firstVariant;
  }

  /**
   * @param dbRvwVar
   * @return
   */
  private PIDCVariant getVarFromRvwVar(final TRvwVariant dbRvwVar) {
    final Long variantID = dbRvwVar.getTabvProjectVariant().getVariantId();
    return getApicDataProvider().getPidcVaraint(variantID);
  }

  /**
   * @return the A2L file
   */
  public A2LFile getA2LFile() {
    TPidcA2l tPidcA2l = getEntityProvider().getDbCDRResult(getID()).getTPidcA2l();
    return getApicDataProvider().getA2lFile(tPidcA2l.getPidcA2lId(), tPidcA2l.getMvTa2lFileinfo().getId());
  }

  /**
   * ICDM 658
   *
   * @return Description
   */
  @Override
  public String getDescription() {
    if (getEntityProvider().getDbCDRResult(getID()).getDescription() != null) {
      return getEntityProvider().getDbCDRResult(getID()).getDescription();
    }
    return "";
  }

  /**
   * @return the Work package name, if the work package is based on groups
   */
  public String getGroupWorkPackageName() {
    return getEntityProvider().getDbCDRResult(getID()).getGrpWorkPkg();
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
      wpName = getDataCache().getDataProvider().getWorkPackageDivision(getFC2WPID()).getName();
    }
    return wpName;
  }

  /**
   * @return the FC2WP ID if the Work packacage is resolved based on FC to WP mapping
   */
  public Long getFC2WPID() {
    TWorkpackageDivision tWorkpackageDivision = getEntityProvider().getDbCDRResult(getID()).getTWorkpackageDivision();
    return tWorkpackageDivision == null ? null : tWorkpackageDivision.getWpDivId();
  }

  /**
   * @return fc2wp workpackage name
   */
  public String getFc2WpName() {
    String wpName = null;
    if (getFC2WPID() != null) {
      wpName = getDataCache().getDataProvider().getWorkPackageDivision(getFC2WPID()).getName();
    }
    return wpName;
  }

  /**
   * @return the Status of this review DB type((O)Open, (I)In-Progress, (C)Closed)
   */
  public CDRConstants.REVIEW_STATUS getStatus() {
    return CDRConstants.REVIEW_STATUS.getType(getEntityProvider().getDbCDRResult(getID()).getRvwStatus());
  }

  /**
   * @return the Status of the review UI Type ( Open, In-Progress, Closed)
   */
  public String getStatusUIType() {
    return CDRConstants.REVIEW_STATUS.getType(getEntityProvider().getDbCDRResult(getID()).getRvwStatus()).getUiType();
  }

  /**
   * @return the review status enum.Method has been added to use the enumeration directly.
   */
  public REVIEW_STATUS getReviewStatus() {
    return CDRConstants.REVIEW_STATUS.getType(getEntityProvider().getDbCDRResult(getID()).getRvwStatus());
  }

  /**
   * @return the Db value of the lock status. Y or N.
   */
  public REVIEW_LOCK_STATUS getLockStatus() {
    // get the lock status from DB as string and then return the enumeration.
    final String lockStatus = getEntityProvider().getDbCDRResult(getID()).getLockStatus();
    return CDRConstants.REVIEW_LOCK_STATUS.getType(lockStatus);
  }

  /**
   * @return whether this review is a Delta review or not
   */
  public boolean isDeltaReview() {
    if (getEntityProvider().getDbCDRResult(getID()).getTRvwResult() == null) {
      return getDeltaReviewType() != null;
    }
    return true;

  }


  /**
   * Checks if all params are Reviewed iCDM-665
   *
   * @return true if all params are reviewed
   */
  public boolean isAllParamsReviewed() {
    // check if all params are reviewed
    Collection<CDRResultParameter> params = getParametersMap().values();
    if (params.isEmpty()) {
      return false;
    }
    for (CDRResultParameter param : params) {
      if (!param.isReviewed()) {
        return false;
      }
    }
    return true;
  }

  /**
   * If this review is a delta review, return the parent review result object
   *
   * @return true if delta review
   */
  public CDRResult getParentReview() {
    final TRvwResult parentResult = getEntityProvider().getDbCDRResult(getID()).getTRvwResult();
    CDRResult parentRes = null;
    if (parentResult != null) {
      try {
        parentRes = getDataCache().getCDRResult(parentResult.getResultId(), true);
      }
      catch (DataException dataExp) {
        CDMLogger.getInstance().error(dataExp.getMessage(), dataExp);
      }
    }
    return parentRes;
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
          parentParam = iterator.next().getParentParam();
        }

        if (null == parentParam) {
          return "";
        }
        StringBuilder parentRvwText = new StringBuilder(40);
        String pidcName = parentParam.getReviewResult().getPidcVersion().getPidcName();
        PIDCVariant variant = parentParam.getReviewResult().getVariant();
        parentRvwText.append("<Multiple Reviews of PIDC -").append(pidcName).append(" and Variant - ");
        if (null == variant) {
          parentRvwText.append("<NO-VARIANT>");
        }
        else {
          parentRvwText.append(variant.getName());
        }
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
   * @return the created user
   */
  @Override
  public final String getCreatedUser() {
    return getEntityProvider().getDbCDRResult(getID()).getCreatedUser();
  }

  /**
   * @return the created date
   */
  @Override
  public final Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCDRResult(getID()).getCreatedDate());
  }

  /**
   * @return the modified date
   */
  @Override
  public final Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCDRResult(getID()).getModifiedDate());
  }

  /**
   * @return the modified user
   */
  @Override
  public final String getModifiedUser() {
    return getEntityProvider().getDbCDRResult(getID()).getModifiedUser();
  }


  /**
   * @return the map of result functions
   */
  protected final Map<Long, CDRResultFunction> getResFunctionMap() {
    if (this.resFuncMap == null) {
      this.resFuncMap = new HashMap<Long, CDRResultFunction>();
      CDRResultFunction cdrResFun;
      final Set<TRvwFunction> tRvwFunctions = getEntityProvider().getDbCDRResult(getID()).getTRvwFunctions();
      if (tRvwFunctions != null) {
        for (TRvwFunction dbResFun : tRvwFunctions) {
          cdrResFun = getDataCache().getCDRResultFunctionMap().get(dbResFun.getRvwFunId());
          if (cdrResFun == null) {
            cdrResFun = new CDRResultFunction(getDataCache().getDataProvider(), dbResFun.getRvwFunId()); // NOPMD
          }

          this.resFuncMap.put(cdrResFun.getID(), cdrResFun);
        }
      }
    }
    return this.resFuncMap;
  }

  /**
   * @return the set of functions used for this review
   */
  public final SortedSet<CDRResultFunction> getFunctions() {
    return new TreeSet<CDRResultFunction>(getResFunctionMap().values());
  }

  /**
   * @return the Map of parameters used for this review
   */
  protected Map<Long, CDRResultParameter> getParametersMap() {
    if (this.resParamMap == null) {
      this.resParamMap = new HashMap<Long, CDRResultParameter>();
      this.resultParamNameMap = new HashMap<>();
      CDRResultParameter cdrResParam;
      final Set<TRvwParameter> tRvwParameters = getEntityProvider().getDbCDRResult(getID()).getTRvwParameters();
      if (tRvwParameters != null) {
        for (TRvwParameter dbResParam : tRvwParameters) {
          cdrResParam = getDataCache().getAllCDRResultParameters().get(dbResParam.getRvwParamId());
          if (cdrResParam == null) {
            cdrResParam = new CDRResultParameter(getDataCache().getDataProvider(), dbResParam.getRvwParamId()); // NOPMD
          }
          this.resParamMap.put(cdrResParam.getID(), cdrResParam);
          this.resultParamNameMap.put(cdrResParam.getName(), cdrResParam);
        }
      }
    }

    return this.resParamMap;
  }


  /**
   * @return the resultParamNameMap
   */
  public Map<String, CDRResultParameter> getResultParamNameMap() {
    return this.resultParamNameMap;
  }


  /**
   * @return the set of parameters used for this review
   */
  public final SortedSet<CDRResultParameter> getParameters() {
    return new TreeSet<CDRResultParameter>(getParametersMap().values());
  }

  /**
   * @return the map of CDR participants
   */
  protected Map<Long, CDRParticipant> getParticipantMap() {
    if (this.participantMap == null) {
      this.participantMap = new HashMap<Long, CDRParticipant>();
      final Set<TRvwParticipant> tRvwParticipants = getEntityProvider().getDbCDRResult(getID()).getTRvwParticipants();
      if (tRvwParticipants != null) {
        for (TRvwParticipant dbParticipant : tRvwParticipants) {
          this.participantMap.put(dbParticipant.getParticipantId(), new CDRParticipant( // NOPMD
              getDataCache().getDataProvider(), dbParticipant.getParticipantId()));
        }
      }
    }
    return this.participantMap;
  }

  /**
   * ICDM-808 returns the CDRParticipant instance for the given ApicUser
   *
   * @param user ApicUser
   * @param userType participant type
   * @return CDRParticipant
   */
  // ICDM-1729
  public CDRParticipant getCDRParticipant(final ApicUser user, final CDRConstants.REVIEW_USER_TYPE userType) {
    CDRParticipant returnParti = null;
    for (CDRParticipant parti : getParticipantMap().values()) {
      if ((parti.getUser().getUserID().longValue() == user.getUserID().longValue()) &&
          (parti.getParticipationType() == userType)) {
        returnParti = parti;
        break;
      }
    }
    return returnParti;
  }


  /**
   * @return the calibration engineer
   */
  public ApicUser getCalibrationEngineer() {
    ApicUser calibrationEngr = null;
    for (CDRParticipant participant : getParticipantMap().values()) {
      if (participant.getParticipationType() == CDRConstants.REVIEW_USER_TYPE.CAL_ENGINEER) {
        calibrationEngr = participant.getUser();
      }
    }

    return calibrationEngr;
  }

  /**
   * @return the auditor
   */
  public ApicUser getAuditor() {
    ApicUser auditor = null;
    for (CDRParticipant participant : getParticipantMap().values()) {
      if (participant.getParticipationType() == CDRConstants.REVIEW_USER_TYPE.AUDITOR) {
        auditor = participant.getUser();
      }
    }

    return auditor;

  }

  /**
   * @return the sorted set of other participants
   */
  public SortedSet<ApicUser> getOtherParticipants() {
    final SortedSet<ApicUser> othrUsrSet = new TreeSet<ApicUser>();
    for (CDRParticipant participant : getParticipantMap().values()) {
      if (participant.getParticipationType() == CDRConstants.REVIEW_USER_TYPE.ADDL_PARTICIPANT) {
        othrUsrSet.add(participant.getUser());
      }
    }
    return othrUsrSet;
  }

  /**
   * @return rule file which was used to execute Check SSD
   */
  public SortedSet<IcdmFile> getRuleFile() {
    final SortedSet<IcdmFile> inputFilesSet = new TreeSet<IcdmFile>();
    for (TRvwFile dbFile : getEntityProvider().getDbCDRResult(getID()).getTRvwFiles()) {
      if (CDRConstants.REVIEW_FILE_TYPE.RULE.getDbType().equals(dbFile.getFileType())) {
        inputFilesSet.add(
            getApicDataProvider().getIcdmFile(CDRConstants.CDR_FILE_NODE_ID, dbFile.getTabvIcdmFile().getFileId()));
      }
    }
    return inputFilesSet;
  }

  /**
   * @return the set of input files
   */
  public SortedSet<IcdmFile> getInputFiles() {
    final SortedSet<IcdmFile> inputFilesSet = new TreeSet<IcdmFile>();
    for (TRvwFile dbFile : getEntityProvider().getDbCDRResult(getID()).getTRvwFiles()) {
      if (CDRConstants.REVIEW_FILE_TYPE.INPUT.getDbType().equals(dbFile.getFileType())) {
        // Icdm-543
        inputFilesSet.add(
            getApicDataProvider().getIcdmFile(CDRConstants.CDR_FILE_NODE_ID, dbFile.getTabvIcdmFile().getFileId()));
        // by
        // bne4cob
        // on
        // 1/30/14 11:24 AM
      }
    }
    return inputFilesSet;
  }

  /**
   * @return the set of Lab/Fun files files Icdm-729 get the Lab fun files From DB
   */
  public SortedSet<IcdmFile> getLabFunFiles() {
    final SortedSet<IcdmFile> labFunFilesSet = new TreeSet<IcdmFile>();
    for (TRvwFile dbFile : getEntityProvider().getDbCDRResult(getID()).getTRvwFiles()) {
      if (CDRConstants.REVIEW_FILE_TYPE.FUNCTION_FILE.getDbType().equals(dbFile.getFileType()) ||
          (CDRConstants.REVIEW_FILE_TYPE.LAB_FILE.getDbType().equals(dbFile.getFileType()))) {
        // Icdm-543
        labFunFilesSet.add(
            getApicDataProvider().getIcdmFile(CDRConstants.CDR_FILE_NODE_ID, dbFile.getTabvIcdmFile().getFileId()));
      }
    }
    return labFunFilesSet;
  }

  /**
   * @return the set of output files
   */
  public SortedSet<IcdmFile> getOutputFiles() {
    final SortedSet<IcdmFile> outputFilesSet = new TreeSet<IcdmFile>();
    for (TRvwFile dbFile : getEntityProvider().getDbCDRResult(getID()).getTRvwFiles()) {
      if (CDRConstants.REVIEW_FILE_TYPE.OUTPUT.getDbType().equals(dbFile.getFileType())) {
        // Icdm-543
        outputFilesSet.add(
            getApicDataProvider().getIcdmFile(CDRConstants.CDR_FILE_NODE_ID, dbFile.getTabvIcdmFile().getFileId()));
        // by
        // bne4cob
        // on 1/30/14 11:24
        // AM
      }
    }
    return outputFilesSet;
  }

  /**
   * @return the set of additional files
   */
  public SortedSet<IcdmFile> getAdditionalFiles() {
    final SortedSet<IcdmFile> addlFilesSet = new TreeSet<IcdmFile>();
    for (TRvwFile dbFile : getEntityProvider().getDbCDRResult(getID()).getTRvwFiles()) {
      if (CDRConstants.REVIEW_FILE_TYPE.RVW_ADDL_FILE.getDbType().equals(dbFile.getFileType())) {
        // Icdm-612 Change in geeting the file Id
        addlFilesSet.add(
            getApicDataProvider().getIcdmFile(CDRConstants.CDR_FILE_NODE_ID, dbFile.getTabvIcdmFile().getFileId()));
      }
    }
    return addlFilesSet;
  }

  /**
   * @return the Source Type of this review result
   */
  // Icdm-729
  public CDRConstants.CDR_SOURCE_TYPE getCDRSourceType() {
    return CDRConstants.CDR_SOURCE_TYPE.getType(getEntityProvider().getDbCDRResult(getID()).getSourceType());
  }

  /**
   * Retuns true, if user is calibration engineer or auditor for this review
   */
  @Override
  public boolean isModifiable() {
    boolean isModifiable = false;
    final Long currentUserID = getApicDataProvider().getCurrentUser().getID();
    // Allow editing, only if the current user is assigned as calibration engineer or auditor for this review
    // ICDM-1746 Extending the modifying privilege to the user who created the review

    if (((getCalibrationEngineer() != null) &&
        (currentUserID.longValue() == getCalibrationEngineer().getID().longValue())) ||
        ((getAuditor() != null) && (currentUserID.longValue() == getAuditor().getID().longValue())) ||
        CommonUtils.isEqual(getCreatedUser(), getApicDataProvider().getCurrentUser().getUserName()) ||
        canOtherParticipantsModify()) {
      isModifiable = true;
    }
    return isModifiable;
  }


  /**
   * @returns true if the current user can modify other participants
   */
  public boolean canModifyOtherParticipants() {
    return getApicDataProvider().getCurrentUser().getUserName().equals(getCreatedUser()) ||
        (getApicDataProvider().getCurrentUser().getID().longValue() == getAuditor().getID().longValue()) ||
        (getApicDataProvider().getCurrentUser().getID().longValue() == getCalibrationEngineer().getID().longValue());
  }


  /**
   * @param currUser
   * @return
   */
  public boolean canOtherParticipantsModify() {
    final Long currentUserID = getApicDataProvider().getCurrentUser().getID();
    for (ApicUser otherUser : getOtherParticipants()) {
      if (otherUser.getID().longValue() == currentUserID.longValue()) {
        return true;
      }
    }
    return false;
  }


  /**
   * Checks whether the current user can lock the review result
   *
   * @return true if the current user has APIC_WRITE or Calibration Engineer or Auditor
   */
  public boolean canLockResult() {
    return getApicDataProvider().getCurrentUser().hasApicWriteAccess() || isModifiable();
  }

  /**
   * Checks whether the current user can unlock the review result
   *
   * @return true if the current user has APIC_WRITE or the Owner of PIDC
   */
  public boolean canUnLockResult() {
    if (getApicDataProvider().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }

    NodeAccessRight currentUserRights = getPidcVersion().getPidc().getCurrentUserAccessRights();
    if (currentUserRights == null) {
      return false;
    }
    return currentUserRights.isOwner();
  }

  /**
   * @return true if the review is locked
   */
  public boolean isResultLocked() {
    return CDRConstants.REVIEW_LOCK_STATUS.YES == getLockStatus();
  }

  /**
   * Icdm-923 restrictions to export CDR files allow only if Apic Write or cal eng or auditor
   *
   * @return if the files reviewed is downloadable
   */
  public boolean canDownloadFiles() {
    NodeAccessRight curUserAccRight = getPidcVersion().getPidc().getCurrentUserAccessRights();
    return isModifiable() || getApicDataProvider().getCurrentUser().hasApicWriteAccess() ||
        ((curUserAccRight != null) && curUserAccRight.isOwner());
  }

  /**
   * @return name of this result Icdm-543
   */
  @Override
  public String getName() {
    // ICDM 653
    final StringBuilder resultName =
        new StringBuilder(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_05, getCreatedDate()));
    resultName.append(" - ");
    resultName.append(getA2LFile().getSdomPverVariant());

    if (getVariant() != null) {
      resultName.append(" - ");
      resultName.append(getVariant().getVariantName());
    }
    if (!"".equals(getDescription())) {
      resultName.append(" - ");
      resultName.append(getDescription());
    }
    return resultName.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDRResult other) {
    // TODO:Comparison by getName() needs to be implemented
    return ApicUtil.compareLong(getID(), other.getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * {@inheritDoc} return object details in Map
   */
  @Override
  public Map<String, String> getObjectDetails() {
    final Map<String, String> objDetails = new HashMap<String, String>();

    objDetails.put(FLD_REVIEW_STATUS, getStatus().getDbType());
    // When mapping is FC2WP_ID mapping Name can be same for different ID's
    objDetails.put(FLD_RVW_FC2WP_ID, String.valueOf(getFC2WPID()));
    objDetails.put(FLD_RVW_GRP_NAME, getGroupWorkPackageName());
    objDetails.put(FLD_RVW_DESC, getDescription());
    // Icdm-729 add the Source Type for DCN
    objDetails.put(FLD_RVW_SRC_TYPE, getCDRSourceType().getDbType());
    // Icdm-877 put the Review Type in the Object Details for the DCN
    objDetails.put(FLD_RVW_RVW_TYPE, getReviewTypeStr());
    objDetails.put(FLD_RVW_LCK_TYPE, getLockStatus().getUiType());
    objDetails.put(FLD_PIDC_VERSION_ID, getPidcVersion().getID().toString());
    final StringBuilder fileIdsBuffer = new StringBuilder();
    for (IcdmFile icdmFile : getAdditionalFiles()) {
      fileIdsBuffer.append(icdmFile.getID());
    }
    objDetails.put(ADDITIONAL_FILES, fileIdsBuffer.toString());

    return objDetails;
  }


  /**
   * Icdm-1214
   *
   * @return the Rvw Attr Values
   */
  public Map<Long, CDRReviewAttrValue> getReviewAttrValMap() {
    final Set<TRvwAttrValue> tRvwValues = getEntityProvider().getDbCDRResult(getID()).getTRvwAttrValue();

    if (tRvwValues != null) {
      for (TRvwAttrValue tRvwVal : tRvwValues) {
        // Check if the Item is already present in the map if not create a new Object
        if (this.rvwAttrValMap.get(tRvwVal.getRvwAttrvalId()) == null) {
          final CDRReviewAttrValue rvwAttrVal = new CDRReviewAttrValue(getDataProvider(), tRvwVal.getRvwAttrvalId());
          this.rvwAttrValMap.put(tRvwVal.getRvwAttrvalId(), rvwAttrVal);
        }
      }
    }
    return this.rvwAttrValMap;
  }

  /**
   * Icdm-1214
   *
   * @return the Rvw Attr Values
   */
  public Map<Long, CDRReviewVariant> getReviewVarMap() {
    if (!isDeleted()) {
      final Set<TRvwVariant> tRvwVars = getEntityProvider().getDbCDRResult(getID()).getTRvwVariants();
      // if the variants are available and the review Var map is empty fill it.
      if ((CommonUtils.isNotNull(tRvwVars)) && this.rvwVarMap.isEmpty()) {
        for (TRvwVariant tRvwVar : tRvwVars) {
          // Check if the Item is already present in the map if not create a new Object
          if (this.rvwVarMap.get(tRvwVar.getRvwVarId()) == null) {
            final CDRReviewVariant rvwVar = new CDRReviewVariant(getDataProvider(), tRvwVar.getRvwVarId());
            this.rvwVarMap.put(tRvwVar.getRvwVarId(), rvwVar);
          }
        }
      }
    }
    // return the map.
    return this.rvwVarMap;
  }

  /**
   * @return the Attr Val Set for Display puropse
   */
  public final SortedSet<CDRReviewAttrValue> getRvwAttrValSet() {
    return new TreeSet<>(getReviewAttrValMap().values());
  }

  /**
   * @return immediate child reviews(delta reviews) of this review
   */
  public Set<CDRResult> getChildReviews() {
    final Set<CDRResult> childResults = new HashSet<CDRResult>();
    final Set<TRvwResult> tRvwResults = getEntityProvider().getDbCDRResult(getID()).getTRvwResults();
    if (tRvwResults != null) {
      for (TRvwResult tRvwResult : tRvwResults) {
        childResults.add(getDataProvider().getCDRResult(tRvwResult.getResultId()));
      }
    }
    return childResults;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.CDR_RESULT;
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
    return CDRConstants.REVIEW_TYPE.getType(getEntityProvider().getDbCDRResult(getID()).getReviewType());
  }


  /**
   * @return if the Current user can delete the Review
   */
  public boolean canUsrDelReview() {
    // Allow editing, only if the current user is calibration engineer or an administrator(APIC_WRITE) or created user
    // ICDM-979
    // Icdm-1049 even created user can delete a review if it is a test review.
    final ApicUser curUser = getApicDataProvider().getCurrentUser();
    return curUser.hasApicWriteAccess() ||
        ((getCalibrationEngineer() != null) && curUser.equals(getCalibrationEngineer())) ||
        (ApicUtil.compare(getCreatedUser(), curUser.getName()) == 0);
  }

  /**
   * Checks whether this result can be deleted. All the below conditions should be satisfied<br>
   * a) Review type is 'Test' <br>
   * b) sufficient access rights <br>
   * c) no delta reviews
   *
   * @return true/false
   */
  public boolean canDelete() {
    if ((getReviewType() != CDRConstants.REVIEW_TYPE.TEST) && (REVIEW_STATUS.OPEN != getReviewStatus())) {
      return false;
    }
    // ICDM-1458
    if (hasChildAttachments()) {
      return false;
    }
    if (hasAttachments()) {
      return false;
    }

    if (canUsrDelReview() || isCurrUsrPidcOwner()) {
      return getChildReviews().isEmpty();
    }

    return false;
  }

  /**
   * @return true if the user is pidc owner of the result. Used for deleting the review if the current user is PIDC
   *         owner.
   */
  private boolean isCurrUsrPidcOwner() {
    PIDCard pidc = getPidcVersion().getPidc();
    NodeAccessRight rights = pidc.getCurrentUserAccessRights();
    return CommonUtils.isNotNull(rights) && rights.isOwner();
  }

  /**
   * ICDM-1458
   *
   * @return true if there are files attached to the result
   */
  public boolean hasAttachments() {
    if (CommonUtils.isEmptySet(getAdditionalFiles())) {
      return false;
    }

    return true;
  }

  /**
   * ICDM-1458
   *
   * @return true if there are files attached to the result parameter
   */
  public boolean hasChildAttachments() {
    for (CDRResultParameter param : getParameters()) {
      if (CommonUtils.isNotEmpty(param.getAttachments())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return true if the result is deleted from DB and available in the Cache
   */
  public boolean isDeleted() {
    return getEntityProvider().getDbCDRResult(getID()) == null;
  }

  /**
   * @return the Rule set
   */
  public RuleSet getRuleSet() {
    RuleSet ruleSet = null;
    TRuleSet tRuleSet = getEntityProvider().getDbCDRResult(getID()).gettRuleSet();
    if (tRuleSet != null) {
      SortedSet<RuleSet> allRuleSets = getDataProvider().getAllRuleSets(false);
      for (RuleSet ruleSet2 : allRuleSets) {
        if (ruleSet2.getID().equals(tRuleSet.getRsetId())) {
          ruleSet = getDataProvider().getRuleSet(tRuleSet.getRsetId());
        }
      }

    }
    return ruleSet;

  }

  // ICDM-1724
  /**
   * Review type,Status and lock status are used in tooltip {@inheritDoc}
   */
  @Override
  public String getToolTip() {
    StringBuilder toolTip = new StringBuilder(TOOLTIP_INITIAL_SIZE);
    toolTip.append("\nReview Type : ").append(getReviewTypeStr()).append("\nStatus : ").append(getStatusUIType())
        .append("\nLock Status : ").append(getLockStatus().getUiType());
    return super.getToolTip().concat(toolTip.toString());
  }


  /**
   * @return the MoniCa file set
   */
  public SortedSet<IcdmFile> getMonicaFiles() {
    final SortedSet<IcdmFile> monicaFile = new TreeSet<IcdmFile>();
    for (TRvwFile dbFile : getEntityProvider().getDbCDRResult(getID()).getTRvwFiles()) {
      if (CDRConstants.REVIEW_FILE_TYPE.MONICA_FILE.getDbType().equals(dbFile.getFileType())) {
        monicaFile.add(
            getApicDataProvider().getIcdmFile(CDRConstants.CDR_FILE_NODE_ID, dbFile.getTabvIcdmFile().getFileId()));
      }
    }
    return monicaFile;
  }

  /**
   * @param variant variant
   */
  public void setLinkedVar(final PIDCVariant variant) {
    this.linkedVar = variant;

  }

  /**
   * Type of delta review
   *
   * @return DELTA_REVIEW_TYPE enum
   */
  public DELTA_REVIEW_TYPE getDeltaReviewType() {
    return DELTA_REVIEW_TYPE.getType(getEntityProvider().getDbCDRResult(getID()).getDeltaReviewType());
  }


  /**
   * @return the srResult
   */
  public ShapeReviewResult getSrResult() {
    return this.srResult;
  }


  /**
   * @param srResult the srResult to set
   */
  public void setSrResult(final ShapeReviewResult srResult) {
    this.srResult = srResult;
  }

  /**
   * @return the path of A2L file in the PIDC Tree
   */
  // ICDM-1649
  public String getExtendedPath() {
    String parentName =
        getCDRSourceType() == CDR_SOURCE_TYPE.WORK_PACKAGE ? getWorkPackageName() : getGroupWorkPackageName();

    return getPidcVersion().getPidcVersionPath() + getPidcVersion().getName() + "->" +
        parentName.replace("<", "").replace(">", "") + "->";
  }

}
