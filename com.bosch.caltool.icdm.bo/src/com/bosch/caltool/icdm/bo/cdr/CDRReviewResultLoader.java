/*
 * \ * \ * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpRespStatusUpdationCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityStatusLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.bo.user.ApicAccessRightLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPkgLoader;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.DateUtil;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFile;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSet;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFile;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParticipant;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResultsSecondary;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwVariant;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwWpResp;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespStatusUpdationModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcReviewDetailsResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTreeRvwVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.cdr.ReviewResultData;
import com.bosch.caltool.icdm.model.cdr.ReviewResultDeleteValidation;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.RvwWpAndRespModel;
import com.bosch.caltool.icdm.model.cdr.WPRespStatusOutputModel;
import com.bosch.caltool.icdm.model.general.IcdmFiles;
import com.bosch.caltool.icdm.model.user.ApicAccessRight;
import com.bosch.caltool.icdm.model.user.User;


/**
 * Loader class for Review Result
 *
 * @author BRU2COB
 */
public class CDRReviewResultLoader extends AbstractBusinessObject<CDRReviewResult, TRvwResult> {

  private static final String PIDC_VER_ID = "pidcverid";

  private static final String RESULT_ID = "resultId";


  /**
   * CDReview Participant types
   */
  private enum REVIEW_USER_TYPE {

                                 /**
                                  * CalibrationEngineer
                                  */
                                 CAL_ENGINEER("C"),
                                 /**
                                  * Auditor
                                  */
                                 AUDITOR("A"),
                                 /**
                                  * AdditionalParticipants
                                  */
                                 ADDL_PARTICIPANT("P");

    final String dbType;

    REVIEW_USER_TYPE(final String dbType) {
      this.dbType = dbType;
    }

    /**
     * Return the type object for the given db type
     *
     * @param dbType db literal of type
     * @return the user type object
     */
    public static REVIEW_USER_TYPE getType(final String dbType) {
      for (REVIEW_USER_TYPE type : REVIEW_USER_TYPE.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return null;
    }
  }


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public CDRReviewResultLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CDR_RESULT, TRvwResult.class);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected CDRReviewResult createDataObject(final TRvwResult entity) throws DataException {
    CDRReviewResult object = new CDRReviewResult();

    setCommonFields(object, entity);

    object.setGrpWorkPkg(entity.getGrpWorkPkg());
    object.setRvwStatus(entity.getRvwStatus());
    object.setOrgResultId(entity.getTRvwResult() == null ? null : entity.getTRvwResult().getResultId());
    object.setDescription(entity.getDescription());
    object.setSourceType(entity.getSourceType());
    object.setReviewType(entity.getReviewType());
    object.setRsetId(entity.gettRuleSet() == null ? null : entity.gettRuleSet().getRsetId());
    object.setPidcA2lId(entity.getTPidcA2l().getPidcA2lId());
    object.setPidcVersionId(entity.getTPidcA2l().getTPidcVersion().getPidcVersId());
    object.setSdomPverVarName(entity.getTPidcA2l().getMvTa2lFileinfo().getSdomPverVariant());
    object.setLockStatus(entity.getLockStatus());
    object.setDeltaReviewType(entity.getDeltaReviewType());
    object.setWpDivId(entity.getTWorkpackageDivision() == null ? null : entity.getTWorkpackageDivision().getWpDivId());
    object.setComments(entity.getComments());

    PidcVariant reviewVariant = getReviewVariant(entity);
    if (null != reviewVariant) {
      object.setPrimaryVariantId(reviewVariant.getId());
      object.setPrimaryVariantName(reviewVariant.getName());
    }
    object.setName(getName(entity, reviewVariant));

    object.setWpDefnVersId(
        entity.gettA2lWpDefnVersion() == null ? null : entity.gettA2lWpDefnVersion().getWpDefnVersId());
    object.setObdFlag(entity.getObdFlag());
    object.setSimpQuesRespValue(entity.getSimpQuesRespFlag());
    object.setSimpQuesRemarks(entity.getSimpQuesRemarks());

    return object;
  }

  /**
   * @return name of this result Icdm-543
   */
  public String getName(final TRvwResult entity, final PidcVariant reviewVariant) {

    // ICDM 653
    StringBuilder resultName = new StringBuilder(
        DateFormat.formatDateToString(timestamp2Date(entity.getCreatedDate()), DateFormat.DATE_FORMAT_05));

    resultName.append(" - ");
    resultName.append(entity.getTPidcA2l().getMvTa2lFileinfo().getSdomPverVariant());

    if (reviewVariant != null) {
      resultName.append(" - ");
      resultName.append(reviewVariant.getName());
    }
    if ((null != entity.getDescription()) && !"".equals(entity.getDescription())) {
      resultName.append(" - ");
      resultName.append(entity.getDescription());
    }
    return resultName.toString();

  }

  /**
   * @param tReviewResult
   * @return
   * @throws DataException
   */
  private ReviewResultData createReviewDataObject(final TRvwResult tReviewResult, final String variantName)
      throws DataException {
    ReviewResultData resultData = new ReviewResultData();

    Map<String, String> reviewUsers = getReviewParticipantsUserName(tReviewResult);
    // To Store Auditor Name in model
    resultData.setAuditor(reviewUsers.get(REVIEW_USER_TYPE.AUDITOR.toString()));
    // To Store Calibration Engineer Name in model
    resultData.setCalEngineer(reviewUsers.get(REVIEW_USER_TYPE.CAL_ENGINEER.toString()));
    // To Store pver name in model
    resultData.setPverName(tReviewResult.getTPidcA2l().getMvTa2lFileinfo().getSdomPverName());
    // To Store rule set name in model
    resultData.setRuleSetName(tReviewResult.gettRuleSet() != null ? tReviewResult.gettRuleSet().getRsetName() : "");
    // To Store pidc variant name in model
    resultData.setPidcVariantName(variantName);
    // To Store parent review in model
    String parentReview = getParentReview(tReviewResult, variantName);
    resultData.setParentReview(parentReview);
    // To Store base review in model
    resultData.setBaseReview(getBaseReview(tReviewResult, variantName, parentReview));
    // To Store scope Name in model
    resultData.setScopeName(getScopeName(tReviewResult));

    resultData.setCdrReviewResult(createDataObject(tReviewResult));


    if (tReviewResult.getTWorkpackageDivision() != null) {
      if (getServiceData().getLanguage().equalsIgnoreCase(Language.ENGLISH.toString())) {
        resultData.setFc2WorkPkgName(tReviewResult.getTWorkpackageDivision().getTWorkpackage().getWpNameE());
      }
      else {
        resultData.setFc2WorkPkgName(tReviewResult.getTWorkpackageDivision().getTWorkpackage().getWpNameG());
      }
      resultData.setFc2wpId(tReviewResult.getTWorkpackageDivision().getWpDivId());
    }
    resultData.setCdrSourceType(CDRConstants.CDR_SOURCE_TYPE.getType(tReviewResult.getSourceType()));
    resultData.setVariantIds(getVarIds(tReviewResult));
    resultData.setPidcVariant(getVariant(tReviewResult));
    resultData.setRvwRltName(getName(tReviewResult, variantName));
    return resultData;
  }


  private boolean hasChildReview(final TRvwResult tReviewResult) {
    Set<TRvwResult> tRvwResults = tReviewResult.getTRvwResults();
    return CommonUtils.isNotEmpty(tRvwResults);
  }

  /**
   * @param resultId review result id
   * @return true if the review result has child review else return false
   */
  public boolean hasChildReview(final Long resultId) {
    TRvwResult tReviewResult = getEntityObject(resultId);
    return hasChildReview(tReviewResult);
  }


  /**
   * @param parentReviewResultId -parent review result id
   * @return The set of child reviews for given review result id
   */
  public Set<TRvwResult> getChildReviewResults(final Long parentReviewResultId) {
    return getEntityObject(parentReviewResultId).getTRvwResults();
  }

  /**
   * @param rvwResultId result id
   * @return validation object
   * @throws DataException error retriving data
   */
  public ReviewResultDeleteValidation reviewResultDeleteValidation(final Long rvwResultId) throws DataException {
    // Validate Rvw Result Id
    validateId(rvwResultId);
    TRvwResult tReviewResult = getEntityObject(rvwResultId);

    boolean hasAddFileAttachments = hasAdditionalFileAttachments(tReviewResult);
    boolean hasParamChildAttachments = hasParamFileAttachments(tReviewResult);
    boolean canUserDelReview = canUsrDelReview(tReviewResult);
    boolean isUsedInCDFXDelivery = isUsedInCDFXDelivery(tReviewResult.getResultId());

    ReviewResultDeleteValidation deleteValidation = new ReviewResultDeleteValidation();
    deleteValidation.setHasAttchments(hasAddFileAttachments);
    deleteValidation.setDeletable(canDeleteReviewResult(tReviewResult, hasAddFileAttachments, hasParamChildAttachments,
        canUserDelReview, isUsedInCDFXDelivery));
    deleteValidation.setCanUsrDelReview(canUserDelReview);
    deleteValidation.setHasChildAttachment(hasParamChildAttachments);
    deleteValidation.settRvwParamSet(getParameterMap(tReviewResult));
    deleteValidation.setHasChildReview(hasChildReview(tReviewResult));
    // To Set the Flag if Review is used in CDFX Delivery

    deleteValidation.setUsedInCDFXDelivery(isUsedInCDFXDelivery);

    return deleteValidation;
  }


  /**
   * Gets the review result delete validation multiple review result in reviewResultIdSet and returns as
   * reviewResultDeleteValidationMap
   *
   * @param rvwResultIdSet reviewresult id set
   * @return Review Result Delete validation map
   * @throws DataException exception while invoking the service
   */
  public Map<Long, ReviewResultDeleteValidation> getMultipleReviewResultDeleteValidation(final Set<Long> rvwResultIdSet)
      throws DataException {
    Map<Long, ReviewResultDeleteValidation> reviewResultDeleteValidationMap = new HashMap<>();
    boolean hasAddFileAttachments;
    boolean hasParamChildAttachments;
    boolean canUserDelReview;
    boolean isUsedInCDFXDelivery;
    TRvwResult tReviewResult;
    ReviewResultDeleteValidation deleteValidation;
    for (Long currentRvwResultId : rvwResultIdSet) {
      validateId(currentRvwResultId);
      tReviewResult = getEntityObject(currentRvwResultId);

      hasAddFileAttachments = hasAdditionalFileAttachments(tReviewResult);
      hasParamChildAttachments = hasParamFileAttachments(tReviewResult);
      canUserDelReview = canUsrDelReview(tReviewResult);
      isUsedInCDFXDelivery = isUsedInCDFXDelivery(tReviewResult.getResultId());

      deleteValidation = new ReviewResultDeleteValidation();
      deleteValidation.setHasAttchments(hasAddFileAttachments);
      deleteValidation.setDeletable(canDeleteReviewResult(tReviewResult, hasAddFileAttachments,
          hasParamChildAttachments, canUserDelReview, isUsedInCDFXDelivery));
      deleteValidation.setCanUsrDelReview(canUserDelReview);
      deleteValidation.setHasChildAttachment(hasParamChildAttachments);
      deleteValidation.settRvwParamSet(getParameterMap(tReviewResult));
      // To Set the Flag if Review is used in CDFX Delivery
      deleteValidation.setUsedInCDFXDelivery(isUsedInCDFXDelivery);
      deleteValidation.setHasChildReview(hasChildReview(tReviewResult));
      reviewResultDeleteValidationMap.put(currentRvwResultId, deleteValidation);
    }
    return reviewResultDeleteValidationMap;
  }


  /**
   * @param rvwResult
   * @return
   * @throws DataException
   */
  private Map<String, String> getReviewParticipantsUserName(final TRvwResult rvwResult) {
    Map<String, String> reviewUsers = new HashMap<>();
    Set<TRvwParticipant> rvwParticipants = rvwResult.getTRvwParticipants();
    UserLoader userLoader = new UserLoader(getServiceData());

    for (TRvwParticipant tRvwResult : rvwParticipants) {
      REVIEW_USER_TYPE rvwUserType = REVIEW_USER_TYPE.getType(tRvwResult.getActivityType());
      TabvApicUser userObject = null;

      if ((null != rvwUserType) && ((rvwUserType == REVIEW_USER_TYPE.AUDITOR) ||
          (rvwUserType == REVIEW_USER_TYPE.CAL_ENGINEER) || (rvwUserType == REVIEW_USER_TYPE.ADDL_PARTICIPANT))) {

        userObject = userLoader.getEntityObject(tRvwResult.getTabvApicUser().getUserId());
        reviewUsers.put(rvwUserType.toString(),
            userObject.getLastname() + ", " + userObject.getFirstname() + " (" + userObject.getDepartment() + ")");
      }

    }

    return reviewUsers;
  }

  /**
   * @param objId
   * @return
   * @throws DataException
   */
  public CDRWizardUIModel fillCDRModelForDeltaReview(final Long objId) throws IcdmException {

    TypedQuery<TRvwResult> typedQuery =
        getEntMgr().createNamedQuery(TRvwResult.GET_RES_BY_REVIEW_ID, TRvwResult.class).setParameter(RESULT_ID, objId);


    return createCDRWizardUIModel(typedQuery.getSingleResult());

  }

  private CDRWizardUIModel createCDRWizardUIModel(final TRvwResult tRvwResult) throws IcdmException {
    RuleSetLoader ruleSetLoader = new RuleSetLoader(getServiceData());
    CDRWizardUIModel cdrWizardUIModel = new CDRWizardUIModel();
    // Local Variables
    Set<TRvwParticipant> tRvwParticipants = tRvwResult.getTRvwParticipants();
    List<String> addParticpantUserFullNames = new ArrayList<>();
    List<String> addParticpantUserNames = new ArrayList<>();
    Set<Long> addParticpantUserIds = new HashSet<>();


    UserLoader userLoader = new UserLoader(getServiceData());
    storeRvwParticipant(cdrWizardUIModel, tRvwParticipants, addParticpantUserFullNames, addParticpantUserNames,
        addParticpantUserIds, userLoader);

    setSsdSWVersion(tRvwResult, cdrWizardUIModel);

    // For Project Data Selection Page
    cdrWizardUIModel.setParticipantUserNameList(addParticpantUserNames);

    cdrWizardUIModel.getParticipantUserFullNameList().addAll(addParticpantUserFullNames);

    cdrWizardUIModel.setSelParticipantsIds(addParticpantUserIds);

    cdrWizardUIModel.setReviewType(tRvwResult.getReviewType());

    String sourceType = tRvwResult.getSourceType();
    cdrWizardUIModel.setSourceType(sourceType);

    cdrWizardUIModel.setDescription(tRvwResult.getDescription());

    cdrWizardUIModel.setA2lWpDefVersId(
        tRvwResult.gettA2lWpDefnVersion() != null ? tRvwResult.gettA2lWpDefnVersion().getWpDefnVersId() : null);

    cdrWizardUIModel.setA2lFileId(tRvwResult.getTPidcA2l().getMvTa2lFileinfo().getId());

    cdrWizardUIModel.setPidcA2lId(tRvwResult.getTPidcA2l().getPidcA2lId());

    cdrWizardUIModel.setSelectedReviewPverName(tRvwResult.getTPidcA2l().getSdomPverName());
    // Check added for cancelled delta review
    setParentRvwRsltId(tRvwResult, cdrWizardUIModel);

    cdrWizardUIModel.setCancelledResultId(tRvwResult.getResultId());

    cdrWizardUIModel.setDeltaReviewType(tRvwResult.getDeltaReviewType());

    cdrWizardUIModel.setSelectedPidcVerId(tRvwResult.getTPidcA2l().getTPidcVersion().getPidcVersId());

    cdrWizardUIModel.setReviewStatus(CDRConstants.REVIEW_STATUS.getType(tRvwResult.getRvwStatus()).getUiType());

    cdrWizardUIModel.setReviewVersion(tRvwResult.getVersion());


    PidcVariant pidcVariant = getReviewVariant(tRvwResult);
    setPidcVariantInfo(tRvwResult, cdrWizardUIModel, pidcVariant);
    // Setting Project Name
    AttributeValue attributeValue = new AttributeValueLoader(getServiceData())
        .getDataObjectByID(tRvwResult.getTPidcA2l().getTabvProjectidcard().getTabvAttrValue().getValueId());
    cdrWizardUIModel.setProjectName(attributeValue.getName());

    // For WorkPackage File Selection Wizard page
    fileSelectionForWP(tRvwResult, cdrWizardUIModel);

    // Setting Primary and Secondary Rule set data
    // For Primary RuleSets
    setPrimaryRuleSet(ruleSetLoader, cdrWizardUIModel, tRvwResult.gettRuleSet());
    // For Secondary Rule Sets
    setSecRuleSet(tRvwResult, ruleSetLoader, cdrWizardUIModel, tRvwResult.getTRvwResultsSecondaries());

    // For Fetching A2l Function
    if (sourceType.equalsIgnoreCase(CDRConstants.CDR_SOURCE_TYPE.A2L_FILE.getDbType())) {
      cdrWizardUIModel.setAvailableFunctions(null);
    }
    else if (sourceType.equalsIgnoreCase(CDRConstants.CDR_SOURCE_TYPE.WP.getDbType())) {
      String mulWpRespName = "";
      List<WpRespModel> selectedWpRespList = new ArrayList<>();
      // For Fetching WorkPackage Name
      fetchWPName(tRvwResult, cdrWizardUIModel, mulWpRespName, selectedWpRespList);
    }

    // For Input & fun/lab Files
    Set<TRvwFile> tRvwFiles = tRvwResult.getTRvwFiles();
    if (tRvwFiles != null) {
      Map<String, byte[]> inputFiles = new HashMap<>();
      Set<String> inputFileNames = new HashSet<>();
      Map<String, byte[]> monicaFiles = new HashMap<>();
      Set<String> monicaFileNames = new HashSet<>();
      for (TRvwFile tRvwFile : tRvwFiles) {
        if (CDRConstants.REVIEW_FILE_TYPE.INPUT.getDbType().equals(tRvwFile.getFileType())) {
          byte[] fileData = getFileBytes(tRvwFile);
          inputFiles.put(tRvwFile.getTabvIcdmFile().getFileName(), fileData);
          inputFileNames.add(tRvwFile.getTabvIcdmFile().getFileName());
        }
        else if (CDRConstants.REVIEW_FILE_TYPE.FUNCTION_FILE.getDbType().equals(tRvwFile.getFileType()) ||
            (CDRConstants.REVIEW_FILE_TYPE.LAB_FILE.getDbType().equals(tRvwFile.getFileType()))) {
          byte[] fileData = getFileBytes(tRvwFile);
          cdrWizardUIModel.setFunLabFilePath(tRvwFile.getTabvIcdmFile().getFileName());
          cdrWizardUIModel.setFunLabFiles(fileData);
        }
        else if (CDRConstants.REVIEW_FILE_TYPE.MONICA_FILE.getDbType().equals(tRvwFile.getFileType())) {
          byte[] fileData = getFileBytes(tRvwFile);
          monicaFiles.put(tRvwFile.getTabvIcdmFile().getFileName(), fileData);
          monicaFileNames.add(tRvwFile.getTabvIcdmFile().getFileName());
        }
      }
      cdrWizardUIModel.setSelectedInputFiles(inputFiles);
      cdrWizardUIModel.setSelFilesPath(inputFileNames);
      cdrWizardUIModel.setSelectedMonicaFiles(monicaFiles);
      cdrWizardUIModel.setSelMonicaFilesPath(monicaFileNames);

    }
    cdrWizardUIModel.setCdrReviewResult(createDataObject(tRvwResult));
    cdrWizardUIModel
        .setParentCDRResultId(tRvwResult.getTRvwResult() == null ? null : tRvwResult.getTRvwResult().getResultId());
    // FOR SSD RULES
    // tRvwResult.getTPidcA2l().
    cdrWizardUIModel.setLabelList(new ArrayList<>());
    // set OBD Flag
    cdrWizardUIModel.setObdFlag(tRvwResult.getObdFlag());

    return cdrWizardUIModel;
  }


  /**
   * @param tRvwResult
   * @param cdrWizardUIModel
   * @param mulWpRespName
   * @param selectedWpRespList
   * @throws DataException
   */
  private void fetchWPName(final TRvwResult tRvwResult, final CDRWizardUIModel cdrWizardUIModel, String mulWpRespName,
      final List<WpRespModel> selectedWpRespList)
      throws DataException {
    if ((tRvwResult.getGrpWorkPkg() != null) && (tRvwResult.getTRvwWpResps() != null)) {
      Set<RvwWpAndRespModel> wpAndRespModelSet = new HashSet<>();
      Set<TRvwWpResp> tRvwWpResps = tRvwResult.getTRvwWpResps();
      for (TRvwWpResp tRvwWpResp : tRvwWpResps) {
        A2lResponsibility a2lResponsibility = new A2lResponsibilityLoader(getServiceData())
            .getDataObjectByID(tRvwWpResp.gettA2lResponsibility().getA2lRespId());

        WpRespModel wpRespModel = new WpRespModel();
        wpRespModel.setWpName(tRvwWpResp.getTA2lWorkPackage().getWpName());
        wpRespModel.setWpRespName(tRvwWpResp.gettA2lResponsibility().getAliasName());
        wpRespModel.setA2lWpId(tRvwWpResp.getTA2lWorkPackage().getA2lWpId());
        wpRespModel.setA2lResponsibility(a2lResponsibility);
        selectedWpRespList.add(wpRespModel);

        RvwWpAndRespModel wpAndRespModel = new RvwWpAndRespModel();
        wpAndRespModel.setA2lRespId(tRvwWpResp.gettA2lResponsibility().getA2lRespId());
        wpAndRespModel.setA2lWpId(tRvwWpResp.getTA2lWorkPackage().getA2lWpId());
        wpAndRespModelSet.add(wpAndRespModel);

        mulWpRespName = CommonUtils.concatenate(mulWpRespName, tRvwWpResp.getTA2lWorkPackage().getWpName()
            .concat(CDRConstants.OPEN_BRACES + a2lResponsibility.getName() + CDRConstants.CLOSE_BRACES), ",");
      }
      if (CDRConstants.MUL_WP.equalsIgnoreCase(tRvwResult.getGrpWorkPkg()) ||
          (tRvwResult.getTRvwWpResps().size() > 1)) {
        cdrWizardUIModel.setWpRespName(null);
        cdrWizardUIModel.setMulWPRespNames(mulWpRespName);
      }
      else {
        Optional<TRvwWpResp> firstTRvwResp = tRvwWpResps.stream().findFirst();
        if (firstTRvwResp.isPresent()) {
          cdrWizardUIModel.setWpRespName(firstTRvwResp.get().getTA2lWorkPackage().getWpName());
          cdrWizardUIModel.setMulWPRespNames(null);
        }
      }
      cdrWizardUIModel.setSelectedWpRespList(selectedWpRespList);
      cdrWizardUIModel.setRvwWpAndRespModelSet(wpAndRespModelSet);
    }
  }


  /**
   * @param tRvwResult
   * @param cdrWizardUIModel
   * @param pidcVariant
   * @throws DataException
   */
  private void setPidcVariantInfo(final TRvwResult tRvwResult, final CDRWizardUIModel cdrWizardUIModel,
      final PidcVariant pidcVariant)
      throws DataException {
    if (null != pidcVariant) {
      cdrWizardUIModel.setSelectedPidcVariantId(pidcVariant.getId());
      cdrWizardUIModel.setSelectedPidcVariantName(pidcVariant.getName());
      RvwVariant rvwVariantByResultNVarId = new RvwVariantLoader(getServiceData())
          .getRvwVariantByResultNVarId(tRvwResult.getResultId(), pidcVariant.getId());

      cdrWizardUIModel.setRvwVariant(rvwVariantByResultNVarId);
    }
  }


  /**
   * @param tRvwResult
   * @param cdrWizardUIModel
   */
  private void setSsdSWVersion(final TRvwResult tRvwResult, final CDRWizardUIModel cdrWizardUIModel) {
    for (TRvwResultsSecondary secondaryRes : tRvwResult.getTRvwResultsSecondaries()) {
      if ((secondaryRes.getSsdVersID() != 0l) && (secondaryRes.getSsdReleaseID() != 0)) {
        cdrWizardUIModel.setSsdReleaseId(secondaryRes.getSsdReleaseID());
        cdrWizardUIModel.setSsdSWVersionId(secondaryRes.getSsdVersID());
      }
      else if (secondaryRes.getSsdVersID() != 0l) {
        cdrWizardUIModel.setSsdSWVersionId(secondaryRes.getSsdVersID());
      }
    }
  }


  /**
   * @param tRvwResult
   * @param cdrWizardUIModel
   */
  private void setParentRvwRsltId(final TRvwResult tRvwResult, final CDRWizardUIModel cdrWizardUIModel) {
    String rvwStatusOpen = CDRConstants.REVIEW_STATUS.OPEN.getUiType();
    String rvwStatusUIType = CDRConstants.REVIEW_STATUS.getType(tRvwResult.getRvwStatus()).getUiType();
    if (rvwStatusUIType.equals(rvwStatusOpen) && (tRvwResult.getTRvwResult() != null)) {
      cdrWizardUIModel.setParentResultId(tRvwResult.getTRvwResult().getResultId());
    }
    else if (!rvwStatusUIType.equals(rvwStatusOpen)) {
      cdrWizardUIModel.setParentResultId(tRvwResult.getResultId());
    }
  }


  /**
   * @param tRvwResult
   * @param cdrWizardUIModel
   */
  private void fileSelectionForWP(final TRvwResult tRvwResult, final CDRWizardUIModel cdrWizardUIModel) {
    if (!tRvwResult.getSourceType().equals(CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType())) {
      Set<TRvwFunction> tRvwFunctions = tRvwResult.getTRvwFunctions();
      SortedSet<String> selReviewFuncs = new TreeSet<>();
      for (TRvwFunction tRvwFunction : tRvwFunctions) {
        selReviewFuncs.add(tRvwFunction.getTFunction().getName());
      }
      cdrWizardUIModel.setSelReviewFuncs(selReviewFuncs);
    }
    else {
      cdrWizardUIModel.setFilesToBeReviewed(true);
    }
  }


  /**
   * @param tRvwResult
   * @param ruleSetLoader
   * @param cdrWizardUIModel
   * @param tRvwResultsSecondaries
   * @throws DataException
   */
  private void setSecRuleSet(final TRvwResult tRvwResult, final RuleSetLoader ruleSetLoader,
      final CDRWizardUIModel cdrWizardUIModel, final Set<TRvwResultsSecondary> tRvwResultsSecondaries)
      throws DataException {
    if (tRvwResultsSecondaries.isEmpty()) {
      cdrWizardUIModel.setCommonRulesSecondary(false);
      cdrWizardUIModel.setSecondaryRuleSetIds(null);
      cdrWizardUIModel.setSecondaryRuleSets(null);
    }
    else {
      Set<Long> secondaryRuleSetIds = new HashSet<>();
      Map<Long, String> secRuleSetMap = new HashMap<>();
      Set<RuleSet> secondaryRuleSets = new HashSet<>();
      if (!CDRConstants.REVIEW_STATUS.getType(tRvwResult.getRvwStatus()).getUiType()
          .equals(CDRConstants.REVIEW_STATUS.OPEN.getUiType())) {
        cdrWizardUIModel.setCommonRulesSecondary(true);
      }

      for (TRvwResultsSecondary tRvwResultsSecondary : tRvwResultsSecondaries) {

        TRuleSet tRuleSet2 = tRvwResultsSecondary.getTRuleSet();
        if (tRuleSet2 != null) {
          secondaryRuleSetIds.add(tRuleSet2.getRsetId());
          secondaryRuleSets.add(ruleSetLoader.createDataObject(tRuleSet2));
          secRuleSetMap.put(tRuleSet2.getRsetId(), tRvwResultsSecondary.getSource());
        }
        else if (CDRConstants.RULE_SOURCE.COMMON_RULES.getDbVal().equalsIgnoreCase(tRvwResultsSecondary.getSource())) {
          secRuleSetMap.put(tRvwResultsSecondary.getSecReviewId(), tRvwResultsSecondary.getSource());

        }
      }
      cdrWizardUIModel.setSecRuleSetMap(secRuleSetMap);
      cdrWizardUIModel.setSecondaryRuleSetIds(secondaryRuleSetIds);
      cdrWizardUIModel.setSecondaryRuleSets(secondaryRuleSets);
    }
  }


  /**
   * @param ruleSetLoader
   * @param cdrWizardUIModel
   * @param tRuleSet
   * @throws DataException
   */
  private void setPrimaryRuleSet(final RuleSetLoader ruleSetLoader, final CDRWizardUIModel cdrWizardUIModel,
      final TRuleSet tRuleSet)
      throws DataException {
    if (CommonUtils.isNotNull(tRuleSet)) {
      setPrimaryRuleSetWithVal(false, tRuleSet.getRsetId(), ruleSetLoader.createDataObject(tRuleSet), cdrWizardUIModel);
    }
    else {
      setPrimaryRuleSetWithVal(true, null, null, cdrWizardUIModel);
    }
  }


  /**
   * @param ruleSetLoader
   * @param cdrWizardUIModel
   * @param tRuleSet
   * @throws DataException
   */
  private void setPrimaryRuleSetWithVal(final boolean isCommonRule, final Long ruleSetId, final RuleSet ruleSetObj,
      final CDRWizardUIModel cdrWizardUIModel) {
    cdrWizardUIModel.setCommonRulesPrimary(isCommonRule);
    cdrWizardUIModel.setPrimaryRuleSetId(ruleSetId);
    cdrWizardUIModel.setPrimaryRuleSet(ruleSetObj);
  }


  /**
   * @param cdrWizardUIModel
   * @param tRvwParticipants
   * @param addParticpantUserFullNames
   * @param addParticpantUserNames
   * @param addParticpantUserIds
   * @param userLoader
   */
  private void storeRvwParticipant(final CDRWizardUIModel cdrWizardUIModel, final Set<TRvwParticipant> tRvwParticipants,
      final List<String> addParticpantUserFullNames, final List<String> addParticpantUserNames,
      final Set<Long> addParticpantUserIds, final UserLoader userLoader) {
    for (TRvwParticipant participant : tRvwParticipants) {
      TabvApicUser userObject = userLoader.getEntityObject(participant.getTabvApicUser().getUserId());
      // To Store Auditor Name in model
      REVIEW_USER_TYPE rvwUserType = REVIEW_USER_TYPE.getType(participant.getActivityType());
      if (rvwUserType == REVIEW_USER_TYPE.AUDITOR) {
        cdrWizardUIModel.setAuditorUserFullName(
            userObject.getLastname() + ", " + userObject.getFirstname() + " (" + userObject.getDepartment() + ")");
        cdrWizardUIModel.setAuditorUserName(userObject.getUsername().toUpperCase());
        cdrWizardUIModel.setSelAuditorId(userObject.getUserId());
      }
      // To Store Calibration Engineer Name in model
      else if (rvwUserType == REVIEW_USER_TYPE.CAL_ENGINEER) {
        cdrWizardUIModel.setCalEngUserFullName(
            userObject.getLastname() + ", " + userObject.getFirstname() + " (" + userObject.getDepartment() + ")");
        cdrWizardUIModel.setCalEngUserName(userObject.getUsername().toUpperCase());
        cdrWizardUIModel.setSelCalEngineerId(userObject.getUserId());
      }
      else if (rvwUserType == REVIEW_USER_TYPE.ADDL_PARTICIPANT) {
        addParticpantUserFullNames
            .add(userObject.getLastname() + ", " + userObject.getFirstname() + " (" + userObject.getDepartment() + ")");
        addParticpantUserNames.add(userObject.getUsername().toUpperCase());
        addParticpantUserIds.add(userObject.getUserId());
      }
    }
  }


  /**
   * @param tRvwFile
   * @return
   * @throws DataException
   */
  private byte[] getFileBytes(final TRvwFile tRvwFile) throws DataException {
    byte[] fileData = null;
    IcdmFilesLoader fileLdr = new IcdmFilesLoader(getServiceData());
    Map<String, byte[]> files = fileLdr.getFiles(tRvwFile.getTabvIcdmFile().getFileId());
    if (CommonUtils.isNotEmpty(files)) {
      for (byte[] value : files.values()) {
        fileData = value;
      }
    }
    return fileData;
  }


  private StringBuilder getParameterMap(final TRvwResult tReviewResult) {

    StringBuilder paramNames = new StringBuilder();
    for (TRvwParameter param : tReviewResult.getTRvwParameters()) {
      if (CommonUtils.isNotEmpty(param.getTRvwFiles())) {
        paramNames.append("\n").append(param.getTParameter().getName());
      }
    }
    return paramNames;
  }


  /**
   * Checks whether this result can be deleted. All the below conditions should be satisfied<br>
   * a) Review type is 'Test' <br>
   * b) sufficient access rights <br>
   * c) no delta reviews
   *
   * @return true/false
   */
  private boolean canDeleteReviewResult(final TRvwResult tReviewResult, final boolean hasAddFileAttachments,
      final boolean hasParamFileAttachments, final boolean canUserDelReview, final boolean isUsedInCDFXDelivery)
      throws DataException {

    if ((CDRConstants.REVIEW_TYPE.getType(tReviewResult.getReviewType()) != CDRConstants.REVIEW_TYPE.TEST) &&
        (REVIEW_STATUS.OPEN != CDRConstants.REVIEW_STATUS.getType(tReviewResult.getRvwStatus()))) {
      return false;
    }
    if (hasParamFileAttachments || isUsedInCDFXDelivery || hasAddFileAttachments) {
      return false;
    }
    if (canUserDelReview || isCurrUsrPidcOwner(tReviewResult)) {
      return tReviewResult.getTRvwResults().isEmpty();
    }
    return false;
  }

  /**
   * @return true if the user is pidc owner of the result. Used for deleting the review if the current user is PIDC
   *         owner.
   * @throws DataException
   */
  private boolean isCurrUsrPidcOwner(final TRvwResult rvwResult) throws DataException {
    NodeAccessLoader nodeAccessLoader = new NodeAccessLoader(getServiceData());
    return nodeAccessLoader.isCurrentUserOwner(rvwResult.getTPidcA2l().getTabvProjectidcard().getProjectId());
  }

  private String getCalibrationEngineer(final TRvwResult rvwResult) {
    String calibrationEngrUserId = null;
    for (TRvwParticipant rvwParticipant : rvwResult.getTRvwParticipants()) {
      REVIEW_USER_TYPE rvwParticipantType = REVIEW_USER_TYPE.getType(rvwParticipant.getActivityType());
      if ((null != rvwParticipantType) && (rvwParticipantType == REVIEW_USER_TYPE.CAL_ENGINEER)) {
        calibrationEngrUserId = rvwParticipant.getTabvApicUser().getUsername();
      }
    }
    return calibrationEngrUserId;
  }

  /**
   * @param tReviewResult
   * @return
   * @throws DataException
   */
  private boolean canUsrDelReview(final TRvwResult tReviewResult) throws DataException {
    ApicAccessRightLoader apicLoader = new ApicAccessRightLoader(getServiceData());
    UserLoader userLoader = new UserLoader(getServiceData());
    final ApicAccessRight accessRightsCurrentUser = apicLoader.getAccessRightsCurrentUser();
    User currentUser = userLoader.getDataObjectCurrentUser();
    // Allow editing, only if the current user is calibration engineer or an administrator(APIC_WRITE) or created user
    // ICDM-979
    // Icdm-1049 even created user can delete a review if it is a test review.
    String calEngUserId = getCalibrationEngineer(tReviewResult);
    return accessRightsCurrentUser.isApicWrite() ||
        ((calEngUserId != null) && currentUser.getName().equals(calEngUserId)) ||
        (ApicUtil.compare(tReviewResult.getCreatedUser(), currentUser.getName()) == 0);
  }

  /**
   * ICDM-1458
   *
   * @return true if there are files attached to the result
   * @throws DataException
   */
  private boolean hasAdditionalFileAttachments(final TRvwResult tReviewResult) {
    return !getAdditionalFiles(tReviewResult).isEmpty();
  }

  /**
   * @param resultId as result
   * @return boolean value
   */
  public boolean isUsedInCDFXDelivery(final long resultId) {
    return !getEntityObject(resultId).gettCDFxDelParamList().isEmpty();
  }

  /**
   * @return the set of additional files
   * @throws DataException
   */
  private Set<TabvIcdmFile> getAdditionalFiles(final TRvwResult tReviewResult) {
    IcdmFilesLoader fileLoader = new IcdmFilesLoader(getServiceData());
    final Set<TabvIcdmFile> addlFilesSet = new HashSet<>();
    for (TRvwFile dbFile : tReviewResult.getTRvwFiles()) {
      if (CDRConstants.REVIEW_FILE_TYPE.RVW_ADDL_FILE.getDbType().equals(dbFile.getFileType())) {
        // Icdm-612 Change in geeting the file Id
        addlFilesSet.add(fileLoader.getEntityObject(dbFile.getTabvIcdmFile().getFileId()));
      }
    }
    return addlFilesSet;
  }


  /**
   * @param tReviewResult entity data
   * @return sortedset of rvwfile
   * @throws DataException error retiving data
   */
  public SortedSet<RvwFile> getReviewFile(final TRvwResult tReviewResult) throws DataException {
    RvwFileLoader fileLoader = new RvwFileLoader(getServiceData());
    final SortedSet<RvwFile> inputFilesSet = new TreeSet<>();
    Set<TRvwFile> tRvwFiles = tReviewResult.getTRvwFiles();
    if (!tRvwFiles.isEmpty()) {
      for (TRvwFile dbFile : tRvwFiles) {
        inputFilesSet.add(fileLoader.getDataObjectByID(dbFile.getRvwFileId()));
      }
    }
    return inputFilesSet;
  }


  /**
   * @param tReviewResult entity data
   * @return sortedset of icdmfiles
   * @throws DataException error retriving data
   */
  public SortedSet<IcdmFiles> getReviewFileData(final TRvwResult tReviewResult) throws DataException {
    IcdmFilesLoader fileLoader = new IcdmFilesLoader(getServiceData());
    final SortedSet<IcdmFiles> inputFilesSet = new TreeSet<>();
    Set<TRvwFile> tRvwFiles = tReviewResult.getTRvwFiles();
    if (!tRvwFiles.isEmpty()) {
      for (TRvwFile dbFile : tRvwFiles) {
        if (checkRvwFileType(dbFile)) {
          inputFilesSet.add(fileLoader.getDataObjectByID(dbFile.getTabvIcdmFile().getFileId()));
        }
      }
    }
    return inputFilesSet;
  }


  /**
   * @param dbFile
   * @return
   */
  private boolean checkRvwFileType(final TRvwFile dbFile) {
    return isValidAddtnalFile(dbFile) || isValidIpOpFile(dbFile) || isValidSplFile(dbFile);
  }


  /**
   * @param dbFile
   * @return
   */
  private boolean isValidIpOpFile(final TRvwFile dbFile) {
    return CDRConstants.REVIEW_FILE_TYPE.RULE.getDbType().equals(dbFile.getFileType()) ||
        CDRConstants.REVIEW_FILE_TYPE.OUTPUT.getDbType().equals(dbFile.getFileType()) ||
        CDRConstants.REVIEW_FILE_TYPE.INPUT.getDbType().equals(dbFile.getFileType());
  }


  /**
   * @param dbFile
   * @return
   */
  private boolean isValidSplFile(final TRvwFile dbFile) {
    return CDRConstants.REVIEW_FILE_TYPE.MONICA_FILE.getDbType().equals(dbFile.getFileType()) ||
        CDRConstants.REVIEW_FILE_TYPE.FUNCTION_FILE.getDbType().equals(dbFile.getFileType()) ||
        CDRConstants.REVIEW_FILE_TYPE.LAB_FILE.getDbType().equals(dbFile.getFileType());
  }


  /**
   * @param dbFile
   * @return
   */
  private boolean isValidAddtnalFile(final TRvwFile dbFile) {
    return CDRConstants.REVIEW_FILE_TYPE.RVW_ADDL_FILE.getDbType().equals(dbFile.getFileType()) ||
        CDRConstants.REVIEW_FILE_TYPE.RVW_PRM_ADDL_FILE.getDbType().equals(dbFile.getFileType());
  }


  /**
   * @param tReviewResult
   * @return true if there are files attached to the result parameter
   * @throws DataException
   */
  private boolean hasParamFileAttachments(final TRvwResult tReviewResult) throws DataException {
    CDRResultParameterLoader cdrResultParameterLoader = new CDRResultParameterLoader(getServiceData());
    Map<Long, CDRResultParameter> byResultObj = cdrResultParameterLoader.getByResultObj(tReviewResult);
    for (CDRResultParameter param : byResultObj.values()) {
      if (CommonUtils.isNotEmpty(hasFileAttachment(param.getId()))) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param parmId
   * @return
   * @throws DataException
   */
  private Map<Long, List<RvwFile>> hasFileAttachment(final Long parmId) throws DataException {
    Map<Long, List<RvwFile>> paramAdditionalFilesMap = new HashMap<>();
    CDRResultParameterLoader cdrResultParameterLoader = new CDRResultParameterLoader(getServiceData());
    RvwFileLoader rvwFileLoader = new RvwFileLoader(getServiceData());

    TRvwParameter entity = cdrResultParameterLoader.getEntityObject(parmId);
    paramAdditionalFilesMap.putAll(rvwFileLoader.getParamFiles(entity));
    return paramAdditionalFilesMap;
  }


  /**
   * @param resultId
   * @return
   * @throws DataException
   */
  private List<Long> getVarIds(final TRvwResult tReviewResult) {
    List<Long> varIds = new ArrayList<>();
    final Set<TRvwVariant> tRvwVars = tReviewResult.getTRvwVariants();
    if (CommonUtils.isNotNull(tRvwVars)) {
      for (TRvwVariant rvwVariant : tRvwVars) {
        varIds.add(rvwVariant.getTabvProjectVariant().getVariantId());
      }
    }
    return varIds;

  }

  /**
   * @param tReviewResult entity object
   * @return the Variant used for this review
   * @throws DataException error while retriving data
   */
  public PidcVariant getVariant(final TRvwResult tReviewResult) throws DataException {
    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
    PidcVariant firstVariant = null;

    Timestamp createdDate = null;
    if (CommonUtils.isNotNull(tReviewResult.getTRvwVariants())) {
      Iterator<TRvwVariant> iterator = tReviewResult.getTRvwVariants().iterator();
      while (iterator.hasNext()) {
        TRvwVariant dbRvwVar = iterator.next();
        if ((createdDate == null) || createdDate.after(dbRvwVar.getCreatedDate())) {
          createdDate = dbRvwVar.getCreatedDate();
          firstVariant = pidcVariantLoader.getDataObjectByID(dbRvwVar.getTabvProjectVariant().getVariantId());
        }
      }
    }
    return firstVariant;
  }


  /**
   * @param entity
   * @return
   * @throws DataException
   */
  public PidcVariant getReviewVariant(final TRvwResult entity) throws DataException {
    PidcVariant reviewVariant = null;
    Timestamp createdDate = null;
    if (CommonUtils.isNotNull(entity.getTRvwVariants())) {
      Iterator<TRvwVariant> iterator = entity.getTRvwVariants().iterator();
      while (iterator.hasNext()) {
        TRvwVariant dbRvwVar = iterator.next();
        if ((createdDate == null) || createdDate.after(dbRvwVar.getCreatedDate())) {
          createdDate = dbRvwVar.getCreatedDate();
          reviewVariant = getVarFromRvwVar(dbRvwVar);
        }
      }
    }
    return reviewVariant;
  }


  /**
   * @param entity TRvwResult
   * @return TRvwVariant
   */
  public TRvwVariant getTRvwVariant(final TRvwResult entity) {
    TRvwVariant tRvwVariant = null;
    Timestamp createdDate = null;
    if (CommonUtils.isNotNull(entity.getTRvwVariants())) {
      Iterator<TRvwVariant> iterator = entity.getTRvwVariants().iterator();
      while (iterator.hasNext()) {
        TRvwVariant dbRvwVar = iterator.next();
        if ((createdDate == null) || createdDate.after(dbRvwVar.getCreatedDate())) {
          createdDate = dbRvwVar.getCreatedDate();
          tRvwVariant = dbRvwVar;
        }
      }
    }
    return tRvwVariant;
  }


  /**
   * @param tReviewResult as input
   * @return count of review result under a specific variant
   */
  public int getReviewResultCount(final TRvwResult tReviewResult) {
    int reviewCount = 0;

    TRvwVariant tRvwVariant = getTRvwVariant(tReviewResult);

    if (CommonUtils.isNotNull(tRvwVariant) && CommonUtils.isNotNull(tRvwVariant.getTabvProjectVariant())) {
      Set<TRvwResult> matchingRvwResultSet = new HashSet<>();
      findRvwResultCountForVariant(tReviewResult, tRvwVariant, matchingRvwResultSet);
      reviewCount = matchingRvwResultSet.size();
    }
    else {
      Set<TRvwResult> noVarRvwResultSet = new HashSet<>();
      for (TPidcA2l tPidcA2l : tReviewResult.getTPidcA2l().getTPidcVersion().getTabvPidcA2ls()) {
        for (TRvwResult tRvwResult : tPidcA2l.getTRvwResults()) {
          if (CommonUtils.isNullOrEmpty(tRvwResult.getTRvwVariants())) {
            noVarRvwResultSet.add(tRvwResult);
          }
        }
      }
      reviewCount = noVarRvwResultSet.size();
    }
    return reviewCount;
  }

  /**
   * @param tReviewResult as exception
   * @return count of Start/Official review result under a specific variant
   */
  public int getOfficialOrStartReviewResultCount(final TRvwResult tReviewResult) {
    int reviewCount = 0;

    TRvwVariant tRvwVariant = getTRvwVariant(tReviewResult);
    if (CommonUtils.isNotNull(tRvwVariant) && CommonUtils.isNotNull(tRvwVariant.getTabvProjectVariant())) {
      Set<TRvwResult> matchingRvwResultSet = new HashSet<>();
      findOfficalOrStartRvwResultCountForVariant(tReviewResult, tRvwVariant, matchingRvwResultSet);
      reviewCount = matchingRvwResultSet.size();
    }
    else {
      Set<TRvwResult> noVarRvwResultSet = new HashSet<>();
      for (TPidcA2l tPidcA2l : tReviewResult.getTPidcA2l().getTPidcVersion().getTabvPidcA2ls()) {
        for (TRvwResult tRvwResult : tPidcA2l.getTRvwResults()) {
          if (CommonUtils.isNullOrEmpty(tRvwResult.getTRvwVariants()) &&
              (CDRConstants.REVIEW_TYPE.START.getDbType().equalsIgnoreCase(tRvwResult.getReviewType()) ||
                  CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType().equalsIgnoreCase(tRvwResult.getReviewType()))) {
            noVarRvwResultSet.add(tRvwResult);
          }
        }
      }
      reviewCount = noVarRvwResultSet.size();
    }
    return reviewCount;
  }


  /**
   * @param tReviewResult
   * @param tRvwVariant
   * @param matchingRvwResultSet
   */
  private void findRvwResultCountForVariant(final TRvwResult tReviewResult, final TRvwVariant tRvwVariant,
      final Set<TRvwResult> matchingRvwResultSet) {
    for (TPidcA2l tPidcA2l : tReviewResult.getTPidcA2l().getTPidcVersion().getTabvPidcA2ls()) {
      for (TRvwResult tRvwResult : tPidcA2l.getTRvwResults()) {
        if (CommonUtils.isNotNull(tRvwResult.getTRvwVariants())) {
          findRvwResultCount(tRvwVariant, matchingRvwResultSet, tRvwResult);
        }
      }
    }
  }

  /**
   * @param tReviewResult
   * @param tRvwVariant
   * @param matchingRvwResultSet
   */
  private void findOfficalOrStartRvwResultCountForVariant(final TRvwResult tReviewResult, final TRvwVariant tRvwVariant,
      final Set<TRvwResult> matchingRvwResultSet) {
    for (TPidcA2l tPidcA2l : tReviewResult.getTPidcA2l().getTPidcVersion().getTabvPidcA2ls()) {
      for (TRvwResult tRvwResult : tPidcA2l.getTRvwResults()) {
        if (CommonUtils.isNotNull(tRvwResult.getTRvwVariants()) &&
            (CDRConstants.REVIEW_TYPE.START.getDbType().equalsIgnoreCase(tRvwResult.getReviewType()) ||
                CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType().equalsIgnoreCase(tRvwResult.getReviewType()))) {
          findRvwResultCount(tRvwVariant, matchingRvwResultSet, tRvwResult);
        }
      }
    }
  }


  /**
   * @param tRvwVariant
   * @param matchingRvwResultSet
   * @param tRvwResult
   */
  private void findRvwResultCount(final TRvwVariant tRvwVariant, final Set<TRvwResult> matchingRvwResultSet,
      final TRvwResult tRvwResult) {
    for (TRvwVariant rvwVariant : tRvwResult.getTRvwVariants()) {
      Long variantId = rvwVariant.getTabvProjectVariant().getVariantId();
      if (variantId.equals(tRvwVariant.getTabvProjectVariant().getVariantId())) {
        matchingRvwResultSet.add(tRvwResult);
        break;
      }
    }
  }

  /**
   * @param dbRvwVar
   * @return
   * @throws DataException
   */
  private PidcVariant getVarFromRvwVar(final TRvwVariant dbRvwVar) throws DataException {
    final Long variantID = dbRvwVar.getTabvProjectVariant().getVariantId();
    PidcVariantLoader varLoader = new PidcVariantLoader(getServiceData());
    return varLoader.getDataObjectByID(variantID);
  }


  /**
   * Get all Review Result records in system
   *
   * @return Map. Key - id, Value - CDRReviewResult object
   * @throws DataException error while retrieving data
   */
  public Map<Long, CDRReviewResult> getAll() throws DataException {
    Map<Long, CDRReviewResult> objMap = new ConcurrentHashMap<>();
    TypedQuery<TRvwResult> tQuery = getEntMgr().createNamedQuery(TRvwResult.GET_ALL, TRvwResult.class);
    List<TRvwResult> dbObj = tQuery.getResultList();
    for (TRvwResult entity : dbObj) {
      objMap.put(entity.getResultId(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * @param reviewId review id
   * @param variantName variant Name
   * @param resultIdList List of result Id
   * @return review result data object
   * @throws DataException exception
   */
  public ReviewResultData getReviewResultById(final long reviewId, final String variantName) throws DataException {
    return createReviewDataObject(getEntityObject(reviewId), variantName);

  }


  /**
   * @param rvwResult
   * @param parentReview
   * @return review result Name
   * @throws DataException
   */
  private String getBaseReview(final TRvwResult rvwResult, final String varName, final String parentReview) {
    String reviewResult = "";
    DELTA_REVIEW_TYPE reviewType = CDRConstants.DELTA_REVIEW_TYPE.getType(rvwResult.getDeltaReviewType());
    if (parentReview == null) {
      if (reviewType == DELTA_REVIEW_TYPE.DELTA_REVIEW) {
        reviewResult = parentReview;
      }
      else {
        // if the result does not have a parent review
        reviewResult = "";
      }
    }
    else {
      // if the result has a parent review
      if (!"".equals(parentReview)) {
        reviewResult = getName(rvwResult, varName);
      }
    }
    return reviewResult;
  }


  /**
   * @param pidcVersId
   * @return
   */
  private boolean checkReviewResultIdWithParent(final Long rvwResultId) {

    final Query typeQue =
        getEntMgr().createNativeQuery("SELECT b.result_id FROM  t_rvw_results a,  t_rvw_parameters b " +
            "WHERE a.result_id = b.result_id  AND a.result_id = ? AND a.DELTA_REVIEW_TYPE = 'P' " +
            "AND b.parent_param_id IS NOT NULL");
    typeQue.setParameter(1, rvwResultId);
    List resultList = typeQue.getResultList();
    return (resultList != null) && !resultList.isEmpty();
  }

  /**
   * @param rvwResult
   * @return
   * @throws DataException
   */
  private String getParentReview(final TRvwResult rvwResult, final String varName) throws DataException {
    DELTA_REVIEW_TYPE reviewType = CDRConstants.DELTA_REVIEW_TYPE.getType(rvwResult.getDeltaReviewType());
    PidcVersionLoader versionLoader = new PidcVersionLoader(getServiceData());
    if (reviewType != null) {
      if (reviewType == CDRConstants.DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW) {
        if (!checkReviewResultIdWithParent(rvwResult.getResultId())) {
          return "";
        }
        StringBuilder parentRvwText = new StringBuilder(40);
        // Loading Pidc name using Pidc Version id
        String pidcName =
            versionLoader.getDataObjectByID(rvwResult.getTPidcA2l().getTPidcVersion().getPidcVersId()).getName();
        // loading variant Name
        parentRvwText.append("<Multiple Reviews of PIDC -").append(pidcName).append(" and Variant - ");
        if (null == varName) {
          parentRvwText.append("<NO-VARIANT>");
        }
        else {
          parentRvwText.append(varName);
        }
        parentRvwText.append(">");
        return parentRvwText.toString();
      }
      else if (reviewType == DELTA_REVIEW_TYPE.DELTA_REVIEW) {
        return getNameForParentReview(rvwResult);
      }
    }

    return ApicConstants.EMPTY_STRING;
  }

  /**
   * @param rvwResult
   * @return resultName
   * @throws DataException
   */
  private String getName(final TRvwResult rvwResult, final String varName) {
    // ICDM 653
    final StringBuilder resultName =
        new StringBuilder(DateUtil.getFormattedDate(DateFormat.DATE_FORMAT_05, rvwResult.getCreatedDate()));
    resultName.append(" - ");
    resultName.append(rvwResult.getTPidcA2l().getMvTa2lFileinfo().getSdomPverVariant());
    if (varName != null) {
      resultName.append(" - ");
      resultName.append(varName);
    }
    String rvwRsltDesc = rvwResult.getDescription();
    if (!"".equals(rvwRsltDesc)) {
      resultName.append(" - ");
      resultName.append(rvwRsltDesc);
    }
    return resultName.toString();
  }


  /**
   * @param rvwResult
   * @return resultName
   * @throws DataException
   */
  private String getNameForParentReview(final TRvwResult rvwResult) throws DataException {

    if (rvwResult.getTRvwResult() != null) {

      TRvwResult parentResultEntity = getEntityObject(rvwResult.getTRvwResult().getResultId());
      PidcVariant varName = null;
      Set<TRvwVariant> tRvwVariants = parentResultEntity.getTRvwVariants();

      if (tRvwVariants != null) {
        Optional<TRvwVariant> trvwVarFirstVal = tRvwVariants.stream().findFirst();
        if (trvwVarFirstVal.isPresent()) {
          TabvProjectVariant tabvProjectVariant = trvwVarFirstVal.get().getTabvProjectVariant();
          varName = new PidcVariantLoader(getServiceData()).getDataObjectByID(tabvProjectVariant.getVariantId());
        }
      }

      return getName(parentResultEntity, varName);
    }

    return "";
  }


  /**
   * @param rvwResult review result object
   * @return scopeName
   */
  private String getScopeName(final TRvwResult rvwResult) {
    String result;
    if (rvwResult.getTWorkpackageDivision() == null) {
      result = rvwResult.getGrpWorkPkg();
    }
    else {
      result = ApicUtil.getLangSpecTxt(getServiceData().getLanguageObj(),
          rvwResult.getTWorkpackageDivision().getTWorkpackage().getWpNameE(),
          rvwResult.getTWorkpackageDivision().getTWorkpackage().getWpNameG(), "");
    }
    if (CommonUtils.isEmptyString(result)) {
      result = CDRConstants.CDR_SOURCE_TYPE.getType(rvwResult.getSourceType()).toString();
    }
    return result;

  }

  /**
   * /**
   *
   * @param pidcVerId pidc version id
   * @return true/false
   */
  public boolean getPidcCdrAvailability(final Long pidcVerId) {
    return !getCdrResList(pidcVerId).isEmpty();
  }

  /**
   * @param pidcVerId pidc version id
   * @return List of rvw results
   */
  public List<TRvwResult> getCdrResList(final Long pidcVerId) {
    TypedQuery<TRvwResult> namedQuery =
        getEntMgr().createNamedQuery(TRvwResult.GET_RES_BY_PIDC_VER_ID, TRvwResult.class);
    namedQuery.setParameter(PIDC_VER_ID, pidcVerId);
    return namedQuery.getResultList();
  }

  /**
   * @param pidcVerId pidc version id
   * @return map of grp work package ,cdr results
   * @throws DataException Exception
   */
  public Map<String, Map<Long, CDRReviewResult>> getCdrResultsForPidcVer(final Long pidcVerId) throws DataException {
    Map<String, Map<Long, CDRReviewResult>> cdrResultsMap = new ConcurrentHashMap<>();
    List<TRvwResult> cdrList = getCdrResList(pidcVerId);
    for (TRvwResult revRes : cdrList) {
      String grpWorkPkg = getGrpWp(revRes);

      if (cdrResultsMap.containsKey(grpWorkPkg) && (null != cdrResultsMap.get(grpWorkPkg))) {
        cdrResultsMap.get(grpWorkPkg).put(revRes.getResultId(), createDataObject(revRes));
      }
      else {
        Map<Long, CDRReviewResult> cdrSet = new HashMap<>();
        cdrSet.put(revRes.getResultId(), createDataObject(revRes));
        cdrResultsMap.put(grpWorkPkg, cdrSet);
      }
    }
    return cdrResultsMap;
  }


  /**
   * @param revRes
   * @return
   * @throws DataException
   */
  private String getGrpWp(final TRvwResult revRes) throws DataException {
    String grpWorkPkg = CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getTreeDispName();

    String rvwRsltSrcType = revRes.getSourceType();

    if (CommonUtils.isNotEmptyString(rvwRsltSrcType)) {
      if (CDRConstants.CDR_SOURCE_TYPE.WORK_PACKAGE.getDbType().equals(rvwRsltSrcType)) {
        if (CommonUtils.isNotEmptyString(revRes.getGrpWorkPkg())) {
          grpWorkPkg = getGrpWorkPkg(revRes);
        }
      }
      else if (CDRConstants.CDR_SOURCE_TYPE.GROUP.getDbType().equals(rvwRsltSrcType)) {
        if (CommonUtils.isNotEmptyString(revRes.getGrpWorkPkg())) {
          grpWorkPkg = revRes.getGrpWorkPkg();
        }
      }
      else if (CDRConstants.CDR_SOURCE_TYPE.WP.getDbType().equals(rvwRsltSrcType)) {
        if (CommonUtils.isNotEmptyString(revRes.getGrpWorkPkg())) {
          grpWorkPkg = revRes.getGrpWorkPkg();
        }
      }
      else {
        grpWorkPkg = CDRConstants.CDR_SOURCE_TYPE.getType(rvwRsltSrcType).getTreeDispName();
      }
    }
    return grpWorkPkg;
  }


  /**
   * @param pidcVerId pidc version id
   * @return Key - VariantName and Value - map with key workpackage Name and value review Result
   * @throws DataException error while retiving data
   */
  public Map<String, PidcTreeRvwVariant> getVarRevResultsForPidcVer(final Long pidcVerId) throws DataException {
    Map<String, PidcTreeRvwVariant> cdrVarResultsMap = new ConcurrentHashMap<>();

    List<TRvwResult> cdrList = getCdrResList(pidcVerId);
    PidcVariantLoader varLoader = new PidcVariantLoader(getServiceData());
    Set<PidcVariant> pidcVarset = new HashSet<>();

    Set<String> varNames = new HashSet<>();
    pidcVarset.addAll(varLoader.getVariants(pidcVerId, false).values());
    for (PidcVariant var : pidcVarset) {
      varNames.add(var.getName());
    }
    for (TRvwResult revRes : cdrList) {
      fillVarRevResMap(cdrVarResultsMap, varLoader, varNames, revRes);
    }
    return cdrVarResultsMap;
  }

  /**
   * @param cdrVarResultsMap
   * @param varLoader
   * @param varNames
   * @param revRes
   * @throws DataException
   */
  private void fillVarRevResMap(final Map<String, PidcTreeRvwVariant> cdrVarResultsMap,
      final PidcVariantLoader varLoader, final Set<String> varNames, final TRvwResult revRes)
      throws DataException {
    String grpWorkPkg = getGrpWp(revRes);
    if ((null != revRes.getTRvwVariants()) && !revRes.getTRvwVariants().isEmpty() && !varNames.isEmpty()) {
      for (TRvwVariant rvwVar : revRes.getTRvwVariants()) {
        PidcVariant variant = varLoader.getDataObjectByID(rvwVar.getTabvProjectVariant().getVariantId());
        if (varNames.contains(variant.getName())) {
          fillVarResults(cdrVarResultsMap, revRes, grpWorkPkg, variant);
        }
        else {
          fillVarResults(cdrVarResultsMap, revRes, grpWorkPkg, createDummyVarObj());
        }
      }
    }
    else {
      fillVarResults(cdrVarResultsMap, revRes, grpWorkPkg, createDummyVarObj());
    }
  }

  /**
   * @return
   */
  private PidcVariant createDummyVarObj() {
    PidcVariant dummyVar = new PidcVariant();
    dummyVar.setId(-1000L);
    dummyVar.setName(ApicConstants.DUMMY_VAR_NODE_NOVAR);
    return dummyVar;
  }


  /**
   * @param cdrVarResultsMap
   * @param revRes
   * @param grpWorkPkg
   * @param variant
   * @throws DataException
   */
  private void fillVarResults(final Map<String, PidcTreeRvwVariant> cdrVarResultsMap, final TRvwResult revRes,
      final String grpWorkPkg, final PidcVariant variant)
      throws DataException {
    if (cdrVarResultsMap.keySet().contains(variant.getName())) {
      varAvailable(cdrVarResultsMap, revRes, grpWorkPkg, variant.getName(), variant);
    }
    else {
      Map<String, Map<Long, CDRReviewResult>> grpWpResMap = new HashMap<>();
      Map<Long, CDRReviewResult> grpWpMap = new HashMap<>();
      grpWpMap.put(revRes.getResultId(), createDataObject(revRes));
      grpWpResMap.put(grpWorkPkg, grpWpMap);
      createTreeRvwVarObj(cdrVarResultsMap, variant, grpWpResMap);
    }
  }

  /**
   * @param cdrVarResultsMap
   * @param revRes
   * @param grpWorkPkg
   * @param varName
   * @param variant
   * @throws DataException
   */
  private void varAvailable(final Map<String, PidcTreeRvwVariant> cdrVarResultsMap, final TRvwResult revRes,
      final String grpWorkPkg, final String varName, final PidcVariant variant)
      throws DataException {
    Map<String, Map<Long, CDRReviewResult>> grpWpResMap = cdrVarResultsMap.get(varName).getGrpWpCdrMap();
    if (grpWpResMap.containsKey(grpWorkPkg)) {
      grpWpResMap.get(grpWorkPkg).put(revRes.getResultId(), createDataObject(revRes));
      createTreeRvwVarObj(cdrVarResultsMap, variant, grpWpResMap);
    }
    else {
      Map<Long, CDRReviewResult> grpWpMap = new HashMap<>();
      grpWpMap.put(revRes.getResultId(), createDataObject(revRes));
      grpWpResMap.put(grpWorkPkg, grpWpMap);
      createTreeRvwVarObj(cdrVarResultsMap, variant, grpWpResMap);
    }
  }

  /**
   * @param cdrVarResultsMap
   * @param variant
   * @param grpWpResMap
   */
  private void createTreeRvwVarObj(final Map<String, PidcTreeRvwVariant> cdrVarResultsMap, final PidcVariant variant,
      final Map<String, Map<Long, CDRReviewResult>> grpWpResMap) {
    PidcTreeRvwVariant treeRvwVar = new PidcTreeRvwVariant();
    treeRvwVar.setRvwVar(variant);
    treeRvwVar.setGrpWpCdrMap(grpWpResMap);
    cdrVarResultsMap.put(variant.getName(), treeRvwVar);
  }

  /**
   * @param rvwResultId
   * @param variantId
   * @return
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  public String getCDRLinkText(final Long rvwResultId, final Long variantId) throws DataException {
    PidcA2lLoader a2lLoader = new PidcA2lLoader(getServiceData());
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
    CDRReviewResult dataObjectByID = getDataObjectByID(rvwResultId);

    Set<Long> pidcIds = new HashSet<>();
    pidcIds.add(getEntityObject(rvwResultId).getTPidcA2l().getTabvProjectidcard().getProjectId());
    Map<Long, Pidc> pidcMap = new PidcLoader(getServiceData()).getDataObjectByID(pidcIds);

    Map<Long, PidcVersionInfo> activeVersionWithStructureAttributes =
        pidcVersionLoader.getActiveVersionWithStructureAttributes(pidcMap.keySet());
    PidcA2l pidcA2l = a2lLoader.getDataObjectByID(dataObjectByID.getPidcA2lId());

    Map<Long, PidcVariant> pidcVariantMap = pidcVariantLoader.getVariants(pidcA2l.getPidcVersId(), false);


    String reviewResult = dataObjectByID.getGrpWorkPkg().replace("<", "").replace(">", "") + "->" +
        dataObjectByID.getCreatedDate() + "-" + dataObjectByID.getSdomPverVarName() + "-" +
        pidcVariantMap.get(variantId).getName() + "-" + dataObjectByID.getDescription();

    StringBuilder cdrUrl = new StringBuilder("");
    for (PidcVersionInfo pidcVersionInfo : activeVersionWithStructureAttributes.values()) {
      for (PidcVersionAttribute pidcVersionAttribute : pidcVersionInfo.getLevelAttrMap().values()) {
        if ("".equals(cdrUrl.toString())) {
          cdrUrl.append(pidcVersionAttribute.getValue());
        }
        else {
          cdrUrl.append("->" + pidcVersionAttribute.getValue());
        }
      }
      cdrUrl.append("->" + pidcVersionInfo.getPidcVersion().getName());
    }
    return "CDR Result:" + cdrUrl.toString() + "->" + reviewResult;
  }


  /**
   * @param rvwResultId
   * @param variantId
   * @return
   * @throws DataException
   */
  public String getCDRLink(final Long rvwResultId, final Long variantId) throws DataException {
    PidcA2lLoader a2lLoader = new PidcA2lLoader(getServiceData());
    CDRReviewResult dataObjectByID = getDataObjectByID(rvwResultId);
    PidcA2l pidcA2l = a2lLoader.getDataObjectByID(dataObjectByID.getPidcA2lId());
    StringBuilder keyID = new StringBuilder();
    keyID.append("icdm:cdrid").append(",").append(dataObjectByID.getId()).append('-').append(pidcA2l.getPidcVersId());
    if (CommonUtils.isNotNull(variantId)) {
      keyID.append('-').append(variantId);
    }
    return keyID.toString();
  }


  /**
   * @param resultId as input
   * @return PidcReviewDetails
   * @throws IcdmException exception
   */
  public PidcReviewDetailsResponse fetchNewRvwResultDetailsForNoVariant(final Long resultId) throws IcdmException {
    validateId(resultId);

    PidcReviewDetailsResponse pidcReviewDetails = new PidcReviewDetailsResponse();

    TRvwResult tRvwResult = getEntityObject(resultId);
    // For No Variant
    long variantId = -1L;
    fillVarWPAndRespMap(pidcReviewDetails, tRvwResult, variantId, true);
    fillOtherSrcTypeResultMap(pidcReviewDetails.getOtherSrcTypeResults(), tRvwResult, variantId);

    pidcReviewDetails.getCdrResultMap().put(tRvwResult.getResultId(), getDataObjectByID(tRvwResult.getResultId()));

    return pidcReviewDetails;
  }

  /**
   * @param rvwVariantId as input
   * @return PidcReviewDetails
   * @throws IcdmException as exception
   */
  public PidcReviewDetailsResponse fetchNewRvwResultDetailsForVariant(final Long rvwVariantId) throws IcdmException {
    RvwVariantLoader rvwVariantLoader = new RvwVariantLoader(getServiceData());
    rvwVariantLoader.validateId(rvwVariantId);

    PidcReviewDetailsResponse pidcReviewDetails = new PidcReviewDetailsResponse();
    Map<Long, Long> varToRvwVarMap = new HashMap<>();

    TRvwVariant trvwVariant = rvwVariantLoader.getEntityObject(rvwVariantId);
    long variantId = trvwVariant.getTabvProjectVariant().getVariantId();
    fillVarWPAndRespMap(pidcReviewDetails, trvwVariant.getTRvwResult(), variantId, true);
    fillOtherSrcTypeResultMap(pidcReviewDetails.getOtherSrcTypeResults(), trvwVariant.getTRvwResult(), variantId);

    pidcReviewDetails.getRvwVariantMap().put(trvwVariant.getRvwVarId(),
        rvwVariantLoader.getDataObjectByID(rvwVariantId));
    pidcReviewDetails.getCdrResultMap().put(trvwVariant.getTRvwResult().getResultId(),
        getDataObjectByID(trvwVariant.getTRvwResult().getResultId()));

    pidcReviewDetails.getPidcVarMap().put(variantId,
        new PidcVariantLoader(getServiceData()).getDataObjectByID(variantId));
    varToRvwVarMap.put(variantId, rvwVariantId);
    return pidcReviewDetails;
  }

  /**
   * Method to fill the PidcReviewDetails Model to display the review result in the pidc tree
   *
   * @param pidcVerId
   * @return PidcReviewDetails as output
   * @throws DataException
   */
  public PidcReviewDetailsResponse fetchPidcReviewResultInfo(final Long pidcVerId) throws IcdmException {
    PidcReviewDetailsResponse pidcReviewDetails = new PidcReviewDetailsResponse();

    TPidcVersion tPidcVersion = new PidcVersionLoader(getServiceData()).getEntityObject(pidcVerId);

    for (TPidcA2l tPidcA2l : tPidcVersion.getTabvPidcA2ls()) {
      for (TRvwResult tRvwResult : tPidcA2l.getTRvwResults()) {
        Map<Long, Long> varToRvwVarMap = new HashMap<>();
        if (!tRvwResult.getTRvwVariants().isEmpty()) {
          // if there are review variants
          for (TRvwVariant trvwVariant : tRvwResult.getTRvwVariants()) {
            long variantId = trvwVariant.getTabvProjectVariant().getVariantId();
            fillVarWPAndRespMap(pidcReviewDetails, tRvwResult, variantId, false);
            fillOtherSrcTypeResultMap(pidcReviewDetails.getOtherSrcTypeResults(), tRvwResult, variantId);
            varToRvwVarMap.put(variantId, trvwVariant.getRvwVarId());
          }
        }
        else {
          // For No Variant
          long variantId = -1l;
          fillVarWPAndRespMap(pidcReviewDetails, tRvwResult, variantId, false);
          fillOtherSrcTypeResultMap(pidcReviewDetails.getOtherSrcTypeResults(), tRvwResult, variantId);

        }
        pidcReviewDetails.getRvwVariantMap().putAll(new RvwVariantLoader(getServiceData()).getByResultObj(tRvwResult));
        pidcReviewDetails.getCdrResultMap().put(tRvwResult.getResultId(), getDataObjectByID(tRvwResult.getResultId()));
      }
    }

    pidcReviewDetails.getA2lWpMap().putAll(new A2lWorkPackageLoader(getServiceData()).getWpByPidcVers(pidcVerId));
    pidcReviewDetails.getPidcVarMap().putAll(new PidcVariantLoader(getServiceData()).getVariants(pidcVerId, true));


    return pidcReviewDetails;
  }


  /**
   * @param tRvwResult
   * @param variantId
   * @param fillWPMap
   * @param varWpMap
   * @param varRespWpMap
   */
  private void fillVarWPAndRespMap(final PidcReviewDetailsResponse pidcReviewDetails, final TRvwResult tRvwResult,
      final long variantId, final boolean fillWPMap)
      throws DataException {
    for (TRvwWpResp trvwWpResp : tRvwResult.getTRvwWpResps()) {

      long a2lRespId = trvwWpResp.gettA2lResponsibility().getA2lRespId();
      long a2lWpId = trvwWpResp.getTA2lWorkPackage().getA2lWpId();
      long resultId = trvwWpResp.getTRvwResult().getResultId();

      pidcReviewDetails.getA2lRespMap().put(a2lRespId,
          new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(a2lRespId));

      if (fillWPMap) {
        // fill a2l workpackage map only if the flag is true
        pidcReviewDetails.getA2lWpMap().put(a2lWpId,
            new A2lWorkPackageLoader(getServiceData()).getDataObjectByID(a2lWpId));
      }

      fillVarRespWpMap(pidcReviewDetails.getVarRespWpMap(), variantId, a2lRespId, a2lWpId, resultId);
    }
  }


  /**
   * @param otherSrcTypeResults
   * @param tRvwResult
   * @param variantId
   * @param a2lWpId
   * @param resultId
   * @throws DataException
   */
  private void fillOtherSrcTypeResultMap(final Map<Long, Map<String, Set<Long>>> otherSrcTypeResults,
      final TRvwResult tRvwResult, final long variantId)
      throws DataException {
    long resultId = tRvwResult.getResultId();
    String srcTypeName = "";
    if (CommonUtils.isNotEmptyString(tRvwResult.getSourceType())) {
      srcTypeName = getSrcTypeName(tRvwResult);
      if (!CDRConstants.CDR_SOURCE_TYPE.WP.getDbType().equals(tRvwResult.getSourceType())) {
        if (otherSrcTypeResults.containsKey(variantId)) {
          fillRvwResultSet(otherSrcTypeResults, variantId, resultId, srcTypeName);
        }
        else {
          Map<String, Set<Long>> srcTypeRevResultMap = new HashMap<>();
          Set<Long> reviewResultSet = new HashSet<>();
          reviewResultSet.add(resultId);
          srcTypeRevResultMap.put(srcTypeName, reviewResultSet);
          otherSrcTypeResults.put(variantId, srcTypeRevResultMap);
        }
      }
    }
  }


  /**
   * @param otherSrcTypeResults
   * @param variantId
   * @param resultId
   * @param srcTypeName
   */
  private void fillRvwResultSet(final Map<Long, Map<String, Set<Long>>> otherSrcTypeResults, final long variantId,
      final long resultId, final String srcTypeName) {
    Map<String, Set<Long>> wpRevResultMap = otherSrcTypeResults.get(variantId);
    if (wpRevResultMap.containsKey(srcTypeName)) {
      Set<Long> reviewResultSet = wpRevResultMap.get(srcTypeName);
      if (!reviewResultSet.contains(resultId)) {
        reviewResultSet.add(resultId);
      }
    }
    else {
      Set<Long> reviewResultSet = new HashSet<>();
      reviewResultSet.add(resultId);
      otherSrcTypeResults.get(variantId).put(srcTypeName, reviewResultSet);
    }
  }


  /**
   * @param tRvwResult
   * @return
   * @throws DataException
   */
  private String getSrcTypeName(final TRvwResult tRvwResult) throws DataException {
    String srcTypeName = CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getTreeDispName();
    if (CommonUtils.isNotEmptyString(tRvwResult.getSourceType())) {
      if (CDRConstants.CDR_SOURCE_TYPE.WORK_PACKAGE.getDbType().equals(tRvwResult.getSourceType())) {
        // workpackage -old
        if (CommonUtils.isNotEmptyString(tRvwResult.getGrpWorkPkg())) {
          srcTypeName = getGrpWorkPkg(tRvwResult);
        }
        else {
          srcTypeName = CDRConstants.MUL_FC2WP;
        }
      }
      else if (CDRConstants.CDR_SOURCE_TYPE.GROUP.getDbType().equals(tRvwResult.getSourceType())) {
        // group-old
        if (CommonUtils.isNotEmptyString(tRvwResult.getGrpWorkPkg())) {
          srcTypeName = tRvwResult.getGrpWorkPkg();
        }
        else {
          srcTypeName = CDRConstants.MUL_GRP;
        }
      }
      else {
        // other source types
        srcTypeName = CDRConstants.CDR_SOURCE_TYPE.getType(tRvwResult.getSourceType()).getTreeDispName();
      }
    }
    return srcTypeName;
  }


  /**
   * @param tRvwResult
   * @return
   * @throws DataException
   */
  private String getGrpWorkPkg(final TRvwResult tRvwResult) throws DataException {
    String srcTypeName;
    if (CDRConstants.FC2WP.equals(tRvwResult.getGrpWorkPkg())) {
      srcTypeName = resolveFc2Wp(tRvwResult);
    }
    else {
      srcTypeName = tRvwResult.getGrpWorkPkg();
    }
    return srcTypeName;
  }

  /**
   * @param varWpMap
   * @param varRespWpMap
   * @param variantId
   * @param a2lRespId
   * @param a2lWpId
   * @param resultId
   */
  private void fillVarRespWpMap(final Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpMap, final long variantId,
      final long a2lRespId, final long a2lWpId, final long resultId) {
    if (varRespWpMap.containsKey(variantId)) {
      Map<Long, Map<Long, Set<Long>>> respAndWpRevResultMap = varRespWpMap.get(variantId);
      if (respAndWpRevResultMap.containsKey(a2lRespId)) {
        Map<Long, Set<Long>> tempWpAndRevResultMap = respAndWpRevResultMap.get(a2lRespId);
        if (tempWpAndRevResultMap.containsKey(a2lWpId)) {
          Set<Long> tempReviewResultId = tempWpAndRevResultMap.get(a2lWpId);
          if (!tempReviewResultId.contains(resultId)) {
            tempReviewResultId.add(resultId);
          }
        }
        else {
          Set<Long> tempReviewResultSet = new HashSet<>();
          tempReviewResultSet.add(resultId);
          respAndWpRevResultMap.get(a2lRespId).put(a2lWpId, tempReviewResultSet);
        }
      }
      else {
        Map<Long, Set<Long>> tempWpAndRevResultMap = new HashMap<>();
        Set<Long> tempReviewResultSet = new HashSet<>();
        tempReviewResultSet.add(resultId);
        tempWpAndRevResultMap.put(a2lWpId, tempReviewResultSet);
        varRespWpMap.get(variantId).put(a2lRespId, tempWpAndRevResultMap);
      }
    }
    else {
      Map<Long, Map<Long, Set<Long>>> tempVarRespWpMap = new HashMap<>();
      Map<Long, Set<Long>> tempWpRevResultMap = new HashMap<>();
      Set<Long> tempReviewResultSet = new HashSet<>();
      tempReviewResultSet.add(resultId);
      tempWpRevResultMap.put(a2lWpId, tempReviewResultSet);
      tempVarRespWpMap.put(a2lRespId, tempWpRevResultMap);
      varRespWpMap.put(variantId, tempVarRespWpMap);
    }
  }


  /**
   * @param tRevRes
   * @return
   * @throws DataException
   */
  public String resolveFc2Wp(final TRvwResult tRevRes) throws DataException {
    if (null == tRevRes.getTWorkpackageDivision()) {
      return "";
    }
    Long wpId = tRevRes.getTWorkpackageDivision().getTWorkpackage().getWpId();
    WorkPkgLoader wpLoader = new WorkPkgLoader(getServiceData());
    return wpLoader.getDataObjectByID(wpId).getName();
  }

  /**
   * @param result CDR Review Result
   * @return the extend path - the tree structure of the CDR Review Result
   * @throws DataException error while retrieving data
   */
  String getPidcTreePath(final CDRReviewResult result) throws DataException {
    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVers = pidcVersLdr.getDataObjectByID(result.getPidcVersionId());

    String parentName = CDRConstants.CDR_SOURCE_TYPE.WORK_PACKAGE.getDbType().equals(result.getSourceType()) &&
        (result.getWpDivId() != null)
            ? new WorkPackageDivisionLoader(getServiceData()).getDataObjectByID(result.getWpDivId()).getName()
            : result.getGrpWorkPkg();


    return pidcVersLdr.getPidcTreePath(pidcVers.getId()) + pidcVers.getName() + "->" +
        parentName.replace("<", "").replace(">", "") + "->";

  }

  /**
   * @param resultId Review Result ID
   * @return extended name of this Review Result
   * @throws DataException error while retrieving data
   */
  public String getExtendedName(final Long resultId) throws DataException {
    CDRReviewResult result = getDataObjectByID(resultId);

    return EXTERNAL_LINK_TYPE.CDR_RESULT.getTypeDisplayText() + ": " + getPidcTreePath(result) + result.getName();
  }


  /**
   * @param wpRespStatusOutputModel
   * @param a2lWPRespStatusBeforeUpdMap
   * @param listOfA2lWPRespStatusToBeCreated
   * @param a2lWpResponsibilityStatusLoader
   * @param varId
   * @param wpRespStatusMap
   * @return
   * @throws DataException
   */
  public A2lWpRespStatusUpdationModel getInputUpdationModel(
      final Map<Long, A2lWpResponsibilityStatus> a2lWPRespStatusBeforeUpdMap,
      final List<A2lWpResponsibilityStatus> listOfA2lWPRespStatusToBeCreated,
      final A2lWpResponsibilityStatusLoader a2lWpResponsibilityStatusLoader, final Long varId,
      final Map<A2lWPRespModel, String> wpRespStatusMap)
      throws DataException {
    for (Entry<A2lWPRespModel, String> wpRespStatusEntrySet : wpRespStatusMap.entrySet()) {

      // Collecting A2lWPResponsibilityStatus Before Update into Map for CNS Refresh
      A2lWPRespModel a2lWPRespModel = wpRespStatusEntrySet.getKey();
      String wpFinStatus = wpRespStatusEntrySet.getValue();

      A2lWpResponsibilityStatus a2lWPRespStatusBeforeUpdate =
          a2lWpResponsibilityStatusLoader.getA2lWpStatusByVarAndWpRespId(varId, a2lWPRespModel.getWpRespId(),
              a2lWPRespModel.isInheritedFlag() ? null : a2lWPRespModel.getA2lRespId());

      if (CommonUtils.isNull(a2lWPRespStatusBeforeUpdate)) {

        A2lWpResponsibilityStatus newA2lWPRespStatus = new A2lWpResponsibilityStatus();
        newA2lWPRespStatus.setVariantId(varId);
        newA2lWPRespStatus.setWpRespId(a2lWPRespModel.getWpRespId());
        // will be set for customized responsibility
        if (!a2lWPRespModel.isInheritedFlag()) {
          newA2lWPRespStatus.setA2lRespId(a2lWPRespModel.getA2lRespId());
        }
        newA2lWPRespStatus.setWpRespFinStatus(wpFinStatus);

        listOfA2lWPRespStatusToBeCreated.add(newA2lWPRespStatus);
      }
      else {
        // Setting the workpackage responsible status in a2lWpResponsibility object
        a2lWPRespStatusBeforeUpdate.setWpRespFinStatus(wpFinStatus);

        a2lWPRespStatusBeforeUpdMap.put(a2lWPRespStatusBeforeUpdate.getId(), a2lWPRespStatusBeforeUpdate);
      }
    }

    A2lWpRespStatusUpdationModel a2lWpRespStatusInptUpdModel = new A2lWpRespStatusUpdationModel();
    a2lWpRespStatusInptUpdModel.setA2lWpRespStatusToBeUpdatedMap(a2lWPRespStatusBeforeUpdMap);
    a2lWpRespStatusInptUpdModel.setA2lWpRespStatusListToBeCreated(listOfA2lWPRespStatusToBeCreated);

    return a2lWpRespStatusInptUpdModel;
  }

  /**
   * @param wpRespStatusOutputModel
   * @param a2lWPRespStatusLoader
   * @param a2lWPRespUpdCmd
   * @throws DataException
   */
  public void setUpdModelOutputValue(final WPRespStatusOutputModel wpRespStatusOutputModel,
      final A2lWpResponsibilityStatusLoader a2lWPRespStatusLoader, final A2lWpRespStatusUpdationCommand a2lWPRespUpdCmd)
      throws DataException {
    A2lWpRespStatusUpdationModel a2lWpRespOutputUpdModel =
        a2lWPRespStatusLoader.getOutputUpdationModel(a2lWPRespUpdCmd.getListOfNewlyCreatedA2lWPRespStatus(),
            a2lWPRespUpdCmd.getA2lWpRespStatusBeforeUpdateMap(), a2lWPRespUpdCmd.getA2lWpRespStatusAfterUpdateMap());
    wpRespStatusOutputModel.setA2lWpRespStatusBeforeUpdMap(a2lWpRespOutputUpdModel.getA2lWpRespStatusToBeUpdatedMap());
    wpRespStatusOutputModel.setA2lWpRespStatusAfterUpdMap(a2lWpRespOutputUpdModel.getA2lWpRespStatusMapAfterUpdate());
    wpRespStatusOutputModel
        .setListOfNewlyCreatedA2lWpRespStatus(a2lWpRespOutputUpdModel.getListOfNewlyCreatedA2lWpRespStatus());
  }


}
