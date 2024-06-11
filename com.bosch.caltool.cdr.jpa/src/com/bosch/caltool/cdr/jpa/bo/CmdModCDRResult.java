/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.FilenameUtils;

import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.a2l.jpa.A2LEditorDataProvider;
import com.bosch.caltool.apic.jpa.bo.A2LFile;
import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.AttributeValueModel;
import com.bosch.caltool.apic.jpa.bo.IcdmFile;
import com.bosch.caltool.apic.jpa.bo.PIDCVariant;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;
import com.bosch.caltool.apic.jpa.rules.bo.CDRRuleUtil;
import com.bosch.caltool.cdr.jpa.bo.review.ReviewRuleSetData;
import com.bosch.caltool.cdr.jpa.bo.shapereview.ShapeReviewResult;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.CDR_SOURCE_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_FILE_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_LOCK_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_USER_TYPE;
import com.bosch.caltool.monicareportparser.data.MonitoringToolOutput;
import com.bosch.ssd.icdm.model.CDRRule;

/**
 * @author bne4cob
 */
public class CmdModCDRResult extends AbstractCDRCommand {

  /**
   * Review result BO created/being updated
   */
  private CDRResult reviewResult;

  /**
   * A2L file used
   */
  private A2LFile a2lFile;

  /**
   * Review description
   */
  private String description;

  /**
   * PIDC version
   */
  private PIDCVersion pidcVer;

  /**
   * Variant, if applicable, else null
   */
  private PIDCVariant pidcVariant;

  /**
   * Input files
   */
  private final Set<String> inputFiles = new HashSet<>();


  /**
   * Generated output files
   */
  private final Set<String> outputFiles = new HashSet<>();

  /**
   * Group Work package name, for Work package based review, if work package is Group based in A2L file
   */
  private String grpWorkPkg;

  /**
   * FC2WP, if work package is based on FC2WP in A2L file
   */
  private WorkPackageDivision fcToWp;

  /**
   * Parent result for delta review
   */
  private CDRResult parentResult;

  /**
   * Set of functions selected, for WP based review
   */
  private final SortedSet<CDRFunction> functionSet = new TreeSet<>();

  /**
   * Child commands
   */
  private final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * Review function entity ID
   */
  private static final String ENTITY_ID = "RVW_RES";


  /** Map containing Label Name and corresponding CheckSSDResultParam */
  private Map<String, CheckSSDResultParam> checkSSDResultParamMap;

  /**
   * Selected participants, set of icdm users
   */
  private final SortedSet<ApicUser> selParticipants = new TreeSet<>();
  /**
   * Selected participants, set of icdm users
   */
  private ApicUser selAuditor;
  /**
   * Selected participants, set of icdm users
   */
  private ApicUser selCalEngineer;

  /**
   * Map containing CDRFunction ID and applicable parameters
   */
  private Map<Long, Set<CDRFuncParameter>> funcParamMap;
  // ICDM-1720
  /**
   * Map containing params and file corresponding to it
   */
  private Map<String, String> paramFilesMap;


  /**
   * Rule file
   */
  private String ruleFile;

  /**
   * Defines the status of the review (Open, In-Progress, Closed)
   */
  private final REVIEW_STATUS rvwStatus;

  /**
   * old review status
   */
  private REVIEW_STATUS oldReviewStatus;

  /**
   * Old description
   */
  private String oldDescription;
  /**
   * delta review
   */
  private DELTA_REVIEW_TYPE deltaReviewType;
  /**
   * Old variant
   */
  private PIDCVariant oldPidcVariant;

  /**
   * Old value of group WP
   */
  private String oldGrpWorkPkg;

  /**
   * Old value of FC2WP
   */
  private WorkPackageDivision oldFcToWp;

  /**
   * Old CDR source type
   */
  private CDR_SOURCE_TYPE oldCdrSourceType;

  /**
   * Location of fun/lab file
   */
  private String funLabFileLoc;

  /**
   * Source type
   */
  private CDR_SOURCE_TYPE cdrSourceType;

  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Old review type
   */
  private REVIEW_TYPE oldreviewType;

  /**
   * Icdm-874 Review Type Test or official
   */
  private REVIEW_TYPE reviewType;

  /**
   * Icdm-875 flag for update only Review Type
   */
  private boolean updateResReviewType;

  /**
   * Icdm-877 Store the Result name before delete
   */

  private String resultName;

  /**
   * SSD rules identified for review
   */
  private Map<String, List<CDRRule>> ssdRules;

  // Icdm-1214
  /**
   * new Set for Attr Value Model used in Review.
   */
  private Set<AttributeValueModel> attrValModSet;

  /**
   * boolean to indicate status update
   */
  private boolean updateStatusAlone;

  private RuleSet ruleSet;

  private boolean lockStatus;

  private boolean oldLockStatus;

  // ICDM-1720
  private A2LEditorDataProvider a2lEditorDP;

  /**
   * File name and the corresponding icdm file object
   */
  private final Map<String, IcdmFile> filesCreatedMap = new HashMap<String, IcdmFile>();

  // ICDM-1780
  /**
   *
   */
  private Set<QuestionnaireVersion> questnaireVersSet;

  /**
   * MoniCa file path
   */
  private String monicaFilePath;

  /**
   * calDataMap
   */
  private Map<String, CalData> calDataMap;
  /**
   * characteristics Map
   */
  private Map<String, Characteristic> a2lcharMap;

  /**
   * MoniCa Output
   */
  private MonitoringToolOutput monicaOutput;
  private boolean offReviewType;

  private boolean startReviewType;

  private boolean onlyLockedOffReview;

  private boolean onlyLockedStartResults;
  /**
   * Selected PID card of the a2l file slected
   */
  private PIDCVersion sourcePidcVer;

  /**
   * Selected PIDC variant from the pid card
   */
  private PIDCVariant sourcePIDCVariant;

  /**
   * ICDM-2440 check ssd result map for compliance parameters
   */
  private Map<String, CheckSSDResultParam> checkSSDCompliParamMap;

  /**
   * ICDM-2440 check ssd files for compliance parameters
   */
  private final Set<String> compliCheckSSDFiles = new TreeSet<>();

  /**
   * ICDM-2440 list of rules for compliance parameters
   */
  private Map<String, List<CDRRule>> ssdRulesForCompliParam;


  /**
   * rule file for compliance
   */
  private String compliSSDFilePath;

  /**
   * ICDM-2579 flag to indicate delta review
   */
  private boolean deltaReviewValid;

  /**
   * true if the command is called from cancel operation
   */
  private boolean isFinish;

  /**
   * secondary rule set data list
   */
  private List<ReviewRuleSetData> secRuleSetDataList;

  private Map<String, RESULT_FLAG> secResultMap;

  /**
   * Set<String>
   */
  private Set<String> paramsWithNoCompliRules;

  /**
   * ssd rel id
   */
  private Long ssdRelID;

  private final ShapeReviewResult shapeReviewResult;

  private boolean errorinSSDFile;

  /**
   * @param dataProvider data provider
   * @param rvwStatus STATUS of review
   * @param sourceType source type enum
   * @param shapeReviewResult
   */
  public CmdModCDRResult(final CDRDataProvider dataProvider, final CDRConstants.REVIEW_STATUS rvwStatus,
      final CDR_SOURCE_TYPE sourceType, final ShapeReviewResult shapeReviewResult) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
    this.rvwStatus = rvwStatus;
    this.cdrSourceType = sourceType;
    this.shapeReviewResult = shapeReviewResult;
  }

  /**
   * @param dataProvider CDRDataProvider
   * @param parentResult CDRResult
   * @param rvwStatus STATUS of review
   * @param sourceType source type enum
   * @param shapeReviewResult
   */
  public CmdModCDRResult(final CDRDataProvider dataProvider, final CDRResult parentResult,
      final DELTA_REVIEW_TYPE deltaReviewType, final CDRConstants.REVIEW_STATUS rvwStatus,
      final CDR_SOURCE_TYPE sourceType, final ShapeReviewResult shapeReviewResult) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
    this.parentResult = parentResult;
    this.rvwStatus = rvwStatus;
    this.cdrSourceType = sourceType;
    this.deltaReviewType = deltaReviewType;
    this.shapeReviewResult = shapeReviewResult;
  }

  /**
   * Constructor for UPDATE CDRResult
   *
   * @param dataProvider CDRDataProvider
   * @param resultToUpdate CDRResult to be UPDATED
   * @param parentResultToUpdate CDRResult
   * @param rvwStatus ReviewStatus
   * @param sourceType sourceType
   * @param updateResReviewType updateResReviewType
   */
  public CmdModCDRResult(final CDRDataProvider dataProvider, final CDRResult resultToUpdate,
      final CDRResult parentResultToUpdate, final DELTA_REVIEW_TYPE deltaReviewType,
      final CDRConstants.REVIEW_STATUS rvwStatus, final CDR_SOURCE_TYPE sourceType, final boolean updateResReviewType,
      final ShapeReviewResult shapeReviewResult) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.UPDATE;
    this.reviewResult = resultToUpdate;
    this.parentResult = parentResultToUpdate;
    this.rvwStatus = rvwStatus;
    this.cdrSourceType = sourceType;
    this.updateResReviewType = updateResReviewType;
    // initialize command with current values
    setAttributeFieldsToCommand(this.reviewResult);
    this.updateStatusAlone = false;
    this.reviewType = resultToUpdate.getReviewType();
    this.deltaReviewType = deltaReviewType;
    this.shapeReviewResult = shapeReviewResult;
  }

  /**
   * ICDM-1424 UPDATE review status
   *
   * @param dataProvider CDRDataProvider
   * @param resultToUpdate CDRResult
   * @param rvwStatus CDRConstants.REVIEW_STATUS
   * @param shapeReviewResult2
   */
  public CmdModCDRResult(final CDRDataProvider dataProvider, final CDRResult resultToUpdate,
      final CDRConstants.REVIEW_STATUS rvwStatus, final boolean lockStatus, final ShapeReviewResult shapeReviewResult) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.UPDATE;
    this.pidcVer = resultToUpdate.getPidcVersion();
    this.reviewResult = resultToUpdate;
    this.rvwStatus = rvwStatus;
    this.oldReviewStatus = resultToUpdate.getStatus();
    this.updateStatusAlone = true;
    this.updateResReviewType = true;
    this.oldLockStatus = resultToUpdate.isResultLocked();
    this.lockStatus = lockStatus;
    this.shapeReviewResult = shapeReviewResult;
  }

  // Icdm-877
  /**
   * Consrtuctor for Delete Result Delete the Review Result
   *
   * @param dataProvider CDRDataProvider
   * @param resultToUpdate resultToUpdate
   * @param shapeReviewResult2
   */
  public CmdModCDRResult(final CDRDataProvider dataProvider, final CDRResult resultToUpdate,
      final ShapeReviewResult shapeReviewResult) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.DELETE;
    this.reviewResult = resultToUpdate;
    this.rvwStatus = null;
    this.resultName = this.reviewResult.getName();
    this.shapeReviewResult = shapeReviewResult;
  }


  /**
   * @param cdrSourceType the cdrSourceType to set
   */
  public void setCdrSourceType(final CDR_SOURCE_TYPE cdrSourceType) {
    this.cdrSourceType = cdrSourceType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    if (this.commandMode == COMMAND_MODE.INSERT) {
      this.childCmdStack.rollbackAll(getExecutionMode());
      getDataCache().getAllCDRResults().remove(this.reviewResult.getID());

      final String wpName =
          this.grpWorkPkg == null ? this.fcToWp == null ? "" : this.fcToWp.getName() : this.grpWorkPkg;
      getDataCache().getResults(this.pidcVer, wpName).remove(this.reviewResult);

    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    TRvwResult dbRvwResult = createResult();

    createSecondaryResult();
    createRvwFunctions();
    // iCDM-2355
    if (REVIEW_STATUS.OPEN == this.rvwStatus) {
      // iCDM-2355
      // for delta review
      if (null != this.deltaReviewType) {
        if (this.inputFiles != null) {
          writeCdrFile();
        }
      }
    }
    else {
      createInputFiles();
    }
    createRvwParams();
    createSecRvwParams();
    createFunLabFiles();
    createMonicaFiles();
    createRuleFile();
    createOutputFiles();
    createOutputFilesForCompliParams();// ICDM-2440
    createPartipants();
    // Set status of the review result, based on the reviewed flag of all result params
    updateCDRReviewStatus();
    createRvwAttrValue();
    createRvwVaraint();
    // ICDM-1780
    createRvwQuestionnaires(this.questnaireVersSet);

    // ICDM-2579
    checkForValidDeltaReview(dbRvwResult);

  }


  /**
   *
   */
  private void createSecRvwParams() throws CommandException {


    // Check if parent result present
    if ((this.parentResult != null) && (this.parentResult.getSecondaryResults() != null)) {


      // get Parent Secondary Result.
      SortedSet<CDRSecondaryResult> parentSecResults = this.parentResult.getSecondaryResults();

      for (CDRSecondaryResult parentSecRes : parentSecResults) {
        // Get the parent secondary params
        Map<Long, CDRResParamSecondary> parentSecParams = parentSecRes.getSecondaryResParams();

        List<String> parentParmasList = createParentPramList(parentSecParams);

        List<CDRResultParameter> newlyAddedParams =
            getNewlyAddedParams(parentParmasList, this.reviewResult.getParametersMap());
        for (CDRResParamSecondary parentSecParam : parentSecParams.values()) {

          if ((this.reviewResult.getSecondaryResults() != null) && (this.secRuleSetDataList != null)) {
            // get the new Secondary Result

            // Iterate the rule set Data
            for (ReviewRuleSetData ruleSetData : this.secRuleSetDataList) {
              CDRSecondaryResult secondaryResult = getDataCache().getRuleDataMap().get(ruleSetData);
              if (isSameParentSecResult(parentSecRes, ruleSetData, secondaryResult)) {

                // get the Result param
                CmdModRvwSecondaryParam cmdSecResult;

                CDRResultParameter resParam =
                    this.reviewResult.getResultParamNameMap().get(parentSecParam.getResultParameter().getName());

                // If the parent and new Params are same then do insertion for Delta
                if (resParam != null
                /* && CommonUtils.isEqual(parentSecParam.getResultParameter().getName(), resParam.getName()) */) {
                  // If the rule set data contains rules.
                  if (ruleSetData.getCdrRules() != null) {
                    List<CDRRule> list = ruleSetData.getCdrRules().get(resParam.getName());
                    if (list != null) {
                      cmdSecResult = new CmdModRvwSecondaryParam(getDataProvider(), secondaryResult,
                          ruleSetData.getCheckSSDResParamMap().get(resParam.getName()), resParam, parentSecParam,
                          list.get(0));
                    }
                    else {
                      cmdSecResult = new CmdModRvwSecondaryParam(getDataProvider(), secondaryResult,
                          ruleSetData.getCheckSSDResParamMap().get(resParam.getName()), resParam, parentSecParam, null);
                    }

                  }
                  // If the rule set data does not contains rules.
                  else {
                    if (ruleSetData.getCheckSSDResParamMap() != null) {
                      cmdSecResult = new CmdModRvwSecondaryParam(getDataProvider(), secondaryResult,
                          ruleSetData.getCheckSSDResParamMap().get(resParam.getName()), resParam, parentSecParam, null);
                    }
                    else {
                      cmdSecResult = new CmdModRvwSecondaryParam(getDataProvider(), secondaryResult, null, resParam,
                          parentSecParam, null);
                    }
                  }

                  this.childCmdStack.addCommand(cmdSecResult);
                  checkCommandStatus(cmdSecResult);
                }

              }
            }

          }


        }

        // Flow for newly added params.
        for (CDRResultParameter newResParam : newlyAddedParams) {
          if ((this.reviewResult.getSecondaryResults() != null) && (this.secRuleSetDataList != null)) {
            for (ReviewRuleSetData ruleSetData : this.secRuleSetDataList) {

              CDRSecondaryResult secondaryResult = getDataCache().getRuleDataMap().get(ruleSetData);
              if (isSameParentSecResult(parentSecRes, ruleSetData, secondaryResult)) {
                CmdModRvwSecondaryParam cmdSecResult;
                if (ruleSetData.getCdrRules() != null) {
                  List<CDRRule> list = ruleSetData.getCdrRules().get(newResParam.getName());
                  if (list != null) {
                    cmdSecResult = new CmdModRvwSecondaryParam(getDataProvider(), secondaryResult, newResParam,
                        ruleSetData.getCheckSSDResParamMap().get(newResParam.getName()), list.get(0));
                  }
                  else {
                    cmdSecResult = new CmdModRvwSecondaryParam(getDataProvider(), secondaryResult, newResParam,
                        ruleSetData.getCheckSSDResParamMap().get(newResParam.getName()), null);
                  }

                }
                else {
                  if (ruleSetData.getCheckSSDResParamMap() != null) {
                    cmdSecResult = new CmdModRvwSecondaryParam(getDataProvider(), secondaryResult, newResParam,
                        ruleSetData.getCheckSSDResParamMap().get(newResParam.getName()), null);
                  }
                  else {
                    cmdSecResult =
                        new CmdModRvwSecondaryParam(getDataProvider(), secondaryResult, newResParam, null, null);
                  }
                }

                this.childCmdStack.addCommand(cmdSecResult);
                checkCommandStatus(cmdSecResult);
              }

            }
          }

        }


      }
      addNewResults();


    }

    else {
      storeSecRvwParams(this.reviewResult.getSecondaryResults(), this.secRuleSetDataList);
    }

  }


  /**
   * @param parentParmasList
   * @param parametersMap
   * @return
   */
  private List<CDRResultParameter> getNewlyAddedParams(final List<String> parentParmasList,
      final Map<Long, CDRResultParameter> parametersMap) {

    List<CDRResultParameter> newlyAddedParams = new ArrayList<>();
    for (CDRResultParameter cdrResParam : parametersMap.values()) {
      if (!parentParmasList.contains(cdrResParam.getName())) {
        newlyAddedParams.add(cdrResParam);
      }
    }
    return newlyAddedParams;

  }

  /**
   * @param parentSecRes
   * @param ruleSetData
   * @param secondaryResult
   * @return teur if the Source and Rule set of parent and current secondary Result are same.
   */
  private boolean isSameParentSecResult(final CDRSecondaryResult parentSecRes, final ReviewRuleSetData ruleSetData,
      final CDRSecondaryResult secondaryResult) {
    return CommonUtils.isEqual(secondaryResult.getRuleSet(), ruleSetData.getRuleSet()) &&
        (CommonUtils.isEqual(parentSecRes.getRuleSet(), ruleSetData.getRuleSet())) &&
        CommonUtils.isEqual(secondaryResult.getSource(), ruleSetData.getSource()) &&
        (CommonUtils.isEqual(parentSecRes.getSource(), ruleSetData.getSource()));
  }

  /**
   * @throws CommandException
   */
  private void addNewResults() throws CommandException {
    Set<CDRSecondaryResult> newResults = new HashSet<>();

    for (CDRSecondaryResult cdrSecondaryResult : this.reviewResult.getSecondaryResults()) {
      boolean alreadyInserted = false;

      for (CDRSecondaryResult parentSecResult : this.parentResult.getSecondaryResults()) {
        // current Secondary -- result - Parent and Parent Seconadry -- Result id are not same
        if (CommonUtils.isEqual(cdrSecondaryResult.getSource(), parentSecResult.getSource()) &&
            CommonUtils.isEqual(cdrSecondaryResult.getRuleSet(), parentSecResult.getRuleSet())) {
          alreadyInserted = true;
        }
      }
      if (!alreadyInserted) {
        newResults.add(cdrSecondaryResult);
      }
    }

    storeSecRvwParams(newResults, this.secRuleSetDataList);
  }

  /**
   * @param parentSecParams
   * @return
   */
  private List<String> createParentPramList(final Map<Long, CDRResParamSecondary> parentSecParams) {

    List<String> parentParamList = new ArrayList<>();
    for (CDRResParamSecondary resParamSec : parentSecParams.values()) {
      parentParamList.add(resParamSec.getResultParameter().getName());
    }
    return parentParamList;
  }

  /**
   * @param secRuleSetDataList2
   * @param sortedSet
   * @param secondaryParamsAdded
   * @throws CommandException
   */
  private void storeSecRvwParams(final Set<CDRSecondaryResult> curResuts,
      final List<ReviewRuleSetData> reviewRuleSetData)
      throws CommandException {
    if ((CommonUtils.isNotEmpty(curResuts)) && (CommonUtils.isNotEmpty(reviewRuleSetData))) {
      // Iterate result

      // Input data
      for (ReviewRuleSetData ruleSetData : this.secRuleSetDataList) {
        CDRSecondaryResult secondaryResult = getDataCache().getRuleDataMap().get(ruleSetData);
        // make sure both thr ule sets are equal
        if (secondaryResult.getSource() == ruleSetData.getSource()) {
          // Result params
          for (CDRResultParameter resParam : this.reviewResult.getParametersMap().values()) {
            CmdModRvwSecondaryParam cmdSecResult;
            // If rule is is null
            if (ruleSetData.getCdrRules() != null) {
              List<CDRRule> list = ruleSetData.getCdrRules().get(resParam.getName());
              if (list != null) {
                cmdSecResult = new CmdModRvwSecondaryParam(getDataProvider(), secondaryResult, resParam,
                    ruleSetData.getCheckSSDResParamMap().get(resParam.getName()), list.get(0));
              }
              else {
                cmdSecResult = new CmdModRvwSecondaryParam(getDataProvider(), secondaryResult, resParam,
                    ruleSetData.getCheckSSDResParamMap().get(resParam.getName()), null);
              }
            }
            // Rule is null
            else {
              // checkssd param is not null
              if (ruleSetData.getCheckSSDResParamMap() != null) {
                cmdSecResult = new CmdModRvwSecondaryParam(getDataProvider(), secondaryResult, resParam,
                    ruleSetData.getCheckSSDResParamMap().get(resParam.getName()), null);
              }
              else {
                cmdSecResult = new CmdModRvwSecondaryParam(getDataProvider(), secondaryResult, resParam, null, null);
              }
            }
            this.childCmdStack.addCommand(cmdSecResult);
            checkCommandStatus(cmdSecResult);

          }

        }

      }

    }

  }


  /**
   * @throws CommandException delete the sec review Params.
   */
  private void deleteSecRvwParams() throws CommandException {

    if (this.reviewResult.getSecondaryResults() != null) {

      for (CDRSecondaryResult secondaryResult : this.reviewResult.getSecondaryResults()) {
        secondaryResult.getSecondaryResParams();
        if (secondaryResult.getSecondaryResParams() != null) {
          for (CDRResParamSecondary secondaryParam : secondaryResult.getSecondaryResParams().values()) {
            CmdModRvwSecondaryParam cmdSecResult = new CmdModRvwSecondaryParam(getDataProvider(),
                secondaryParam.getResultParameter(), secondaryResult, secondaryParam);
            this.childCmdStack.addCommand(cmdSecResult);
            checkCommandStatus(cmdSecResult);

          }
        }
      }
    }

  }

  /**
   * create secondary results
   */
  private void createSecondaryResult() throws CommandException {
    if (this.secRuleSetDataList != null) {
      for (ReviewRuleSetData ruleSetData : this.secRuleSetDataList) {
        CmdModCDRSeconadryResult cmdSecResult =
            new CmdModCDRSeconadryResult(getDataProvider(), this.reviewResult, ruleSetData.getRuleSet());
        cmdSecResult.setSsdRelID(ruleSetData.getSsdReleaseID());
        cmdSecResult.setSSdFileReview(ruleSetData.isSSDFileReview());
        cmdSecResult.setSSDVersionID(ruleSetData.getSsdVersionID());
        cmdSecResult.setRuleSetData(ruleSetData);
        this.childCmdStack.addCommand(cmdSecResult);
        checkCommandStatus(cmdSecResult);
      }
    }
  }

  /**
   * create secondary results
   */
  private void deleteSecondaryResult() throws CommandException {
    if (this.reviewResult.getSecondaryResults() != null) {
      for (CDRSecondaryResult secResult : this.reviewResult.getSecondaryResults()) {
        CmdModCDRSeconadryResult cmdSecResult = new CmdModCDRSeconadryResult(getDataProvider(), secResult);
        this.childCmdStack.addCommand(cmdSecResult);
        checkCommandStatus(cmdSecResult);
      }
    }
    this.reviewResult.getSecondaryResReloaded();
  }

  /**
   * ICDM-2579 check if the delta review is valid and make the necessary changes
   *
   * @param dbRvwResult
   */
  private void checkForValidDeltaReview(final TRvwResult dbRvwResult) {
    // if it is a delta review but not valid
    if (this.isFinish && (null != this.deltaReviewType) && !this.deltaReviewValid) {
      // set the parent result id to null
      dbRvwResult.setTRvwResult(null);
      // ICDM-609
      if (this.parentResult != null) {
        Set<CDRResult> childReviewsInCache = getDataCache().getCDRResult(this.parentResult.getID()).getChildReviews();
        if (null != childReviewsInCache) {
          childReviewsInCache.remove(this.reviewResult);
        }
        // Newly Added for associating the Parent and the child Review
        final TRvwResult parentDb = getEntityProvider().getDbCDRResult(this.parentResult.getID());
        Set<TRvwResult> tRvwResults = parentDb.getTRvwResults();
        if (null != tRvwResults) {
          tRvwResults.remove(dbRvwResult);
        }
      }
      // set the delta review type to null
      dbRvwResult.setDeltaReviewType(null);
    }
  }

  /**
   * get the list of input file in the wizard and store it in the database via cmdmodcdrfile
   *
   * @throws CommandException
   */
  private void writeCdrFile() throws CommandException {
    if (null != this.inputFiles) {
      for (String inputFilePath : this.inputFiles) {
        String path = FilenameUtils.getFullPath(inputFilePath);
        if (System.getProperty("java.io.tmpdir").equals(path)) {
          runCreateCdrFileCommand(inputFilePath, REVIEW_FILE_TYPE.INPUT);
        }
      }
    }
  }

  /**
   * create review variant in table
   */
  private void createRvwVaraint() throws CommandException {
    if (this.pidcVariant != null) {
      CmdModRvwVariant cmdModCDRRvwVar = new CmdModRvwVariant(getDataProvider(), this.reviewResult, this.pidcVariant);
      this.childCmdStack.addCommand(cmdModCDRRvwVar);
      checkCommandStatus(cmdModCDRRvwVar);
    }
  }

  /**
   * creating review questionnaires
   *
   * @param questnaireVersSet Set of QuestionnaireVersion
   * @throws CommandException
   */
  private void createRvwQuestionnaires(final Collection<QuestionnaireVersion> questnaireVersSet)
      throws CommandException {
    if (questnaireVersSet != null) {
//      for (QuestionnaireVersion qnaire : questnaireVersSet) {
//        CmdModRvwQuestionnaire cmdRvwQnaire = new CmdModRvwQuestionnaire(getDataProvider(), qnaire, this.reviewResult);
//        this.childCmdStack.addCommand(cmdRvwQnaire);
//      }
    }
  }

  // Icdm-1214
  /**
   * new Creation of Review Attr Value
   */
  private void createRvwAttrValue() throws CommandException {

    // Icdm-1805-changes for Not storing the Values
    ConcurrentMap<Attribute, CDRReviewAttrValue> existingReviewAttrMap = new ConcurrentHashMap<>();
    for (CDRReviewAttrValue attrValue : this.reviewResult.getReviewAttrValMap().values()) {
      existingReviewAttrMap.put(attrValue.getAttribute(), attrValue);
    }

    ConcurrentMap<Attribute, AttributeValueModel> newAttrvalMap = new ConcurrentHashMap<>();
    if (!CommonUtils.isNull(this.attrValModSet)) {
      for (AttributeValueModel attrValue : this.attrValModSet) {
        newAttrvalMap.put(attrValue.getAttribute(), attrValue);
      }
    }
    if (CommonUtils.isNullOrEmpty(this.attrValModSet)) {
      // Delete Attr Values existing in DB but not in Current CDRReviewResult Data
      for (CDRReviewAttrValue attrValue : existingReviewAttrMap.values()) {
        if (attrValue.getAttrValue() != null) {
          CmdModRvwAttrValue cmdModRvwAttr = new CmdModRvwAttrValue(getDataProvider(), this.reviewResult, attrValue);
          this.childCmdStack.addCommand(cmdModRvwAttr);
          checkCommandStatus(cmdModRvwAttr);
        }
      }
    }
    else {
      // Delete Attr Values existing in DB but not in Current CDRReviewResult Data
      for (CDRReviewAttrValue attrValue : existingReviewAttrMap.values()) {
        if (newAttrvalMap.get(attrValue.getAttribute()) == null) {
          CmdModRvwAttrValue cmdModRvwAttr = new CmdModRvwAttrValue(getDataProvider(), this.reviewResult, attrValue);
          this.childCmdStack.addCommand(cmdModRvwAttr);
          checkCommandStatus(cmdModRvwAttr);
        }
      }
      // Add newly added Attr values if any during cancel operation
      for (AttributeValueModel attrValModel : this.attrValModSet) {
        if ((existingReviewAttrMap.get(attrValModel.getAttribute()) == null) && (attrValModel.getAttrValue() != null)) {
          CmdModRvwAttrValue cmdModRvwAttrVal = new CmdModRvwAttrValue(getDataProvider(), this.reviewResult,
              attrValModel.getAttrValue(), attrValModel.getAttribute());
          this.childCmdStack.addCommand(cmdModRvwAttrVal);
          checkCommandStatus(cmdModRvwAttrVal);
        }

      }

    }
  }

  // Icdm-729
  /**
   * Command Changes for creating Lab\Fun files create the Fun /lab files
   */
  private void createFunLabFiles() throws CommandException {
    if (this.funLabFileLoc != null) {
      // changed contains to end with
      if (this.funLabFileLoc.endsWith(".fun")) {
        runCreateCdrFileCommand(this.funLabFileLoc, REVIEW_FILE_TYPE.FUNCTION_FILE);
      }
      // changed contains to end with
      else if (this.funLabFileLoc.endsWith(".lab")) {
        runCreateCdrFileCommand(this.funLabFileLoc, REVIEW_FILE_TYPE.LAB_FILE);
      }
    }

  }

  /**
   * @throws CommandException
   */
  private void createPartipants() throws CommandException {

    if (this.selParticipants != null) {
      for (ApicUser apicUser : this.selParticipants) {
        runCreateParticipantCmd(apicUser, REVIEW_USER_TYPE.ADDL_PARTICIPANT);
      }
    }

    if (this.selAuditor != null) {
      runCreateParticipantCmd(this.selAuditor, REVIEW_USER_TYPE.AUDITOR);
    }

    if (this.selCalEngineer != null) {
      runCreateParticipantCmd(this.selCalEngineer, REVIEW_USER_TYPE.CAL_ENGINEER);
    }

  }


  /**
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void updatePartipants() throws CommandException {
    ConcurrentMap<CDRConstants.REVIEW_USER_TYPE, Map<Long, CDRParticipant>> existingCdrParticipantsMap =
        new ConcurrentHashMap<>();
    for (CDRParticipant cdrParticipant : this.reviewResult.getParticipantMap().values()) {
      Map<Long, CDRParticipant> participantType = existingCdrParticipantsMap.get(cdrParticipant.getParticipationType());
      if (participantType == null) {
        participantType = new HashMap<>();
        existingCdrParticipantsMap.put(cdrParticipant.getParticipationType(), participantType);
      }
      participantType.put(cdrParticipant.getUser().getUserID(), cdrParticipant);
    }
    // Participants
    if (this.selParticipants != null) {

      Map<Long, CDRParticipant> addParticipants =
          existingCdrParticipantsMap.get(CDRConstants.REVIEW_USER_TYPE.ADDL_PARTICIPANT);
      ConcurrentMap<Long, ApicUser> selPartcipntsMap = new ConcurrentHashMap<>();

      // Add new participants
      for (ApicUser apicUser : this.selParticipants) {
        if ((addParticipants == null) || (addParticipants.get(apicUser.getUserID()) == null)) {
          runCreateParticipantCmd(apicUser, REVIEW_USER_TYPE.ADDL_PARTICIPANT);
        }
        selPartcipntsMap.put(apicUser.getUserID(), apicUser);
      }

      // Delete invalid participants
      if (addParticipants != null) {
        for (Entry<Long, CDRParticipant> exstingPartcipntEntry : addParticipants.entrySet()) {
          if (selPartcipntsMap.get(exstingPartcipntEntry.getKey()) == null) {
            // ApicUser null since it is not required during delete
            runUpdateDeleteParticipantCommand(exstingPartcipntEntry.getValue(), null, true);
          }
        }
      }

    }

    // Auditor Update existing ApicUser with new User if user is changed during cancel operation
    if (this.selAuditor != null) {

      if (this.reviewResult.getAuditor() == null) {
        runCreateParticipantCmd(this.selAuditor, REVIEW_USER_TYPE.AUDITOR);
      }
      else if (!this.reviewResult.getAuditor().getUserID().equals(this.selAuditor.getUserID())) {
        Map<Long, CDRParticipant> auditorMap = existingCdrParticipantsMap.get(CDRConstants.REVIEW_USER_TYPE.AUDITOR);
        runUpdateDeleteParticipantCommand(auditorMap.get(this.reviewResult.getAuditor().getUserID()), this.selAuditor,
            false);
      }
    }

    // TODO: Discuss if the above changes are applicable to Calibration Engineer
    if (this.selCalEngineer != null) {
      if (this.reviewResult.getCalibrationEngineer() == null) {
        runCreateParticipantCmd(this.selCalEngineer, REVIEW_USER_TYPE.CAL_ENGINEER);
      }
      else if (!this.reviewResult.getCalibrationEngineer().getUserID().equals(this.selCalEngineer.getUserID())) {
        Map<Long, CDRParticipant> calEngineerMap =
            existingCdrParticipantsMap.get(CDRConstants.REVIEW_USER_TYPE.CAL_ENGINEER);
        runUpdateDeleteParticipantCommand(calEngineerMap.get(this.reviewResult.getCalibrationEngineer().getUserID()),
            this.selCalEngineer, false);

      }
    }


  }


  /**
   * Create and execute command to create participant
   *
   * @param user apic user
   * @param type type of participation
   * @throws CommandException from executing command
   */
  private void runCreateParticipantCmd(final ApicUser user, final REVIEW_USER_TYPE type) throws CommandException {
    CmdModCDRParticipant cmdParticipant = new CmdModCDRParticipant(getDataProvider(), this.reviewResult, user, type);
    this.childCmdStack.addCommand(cmdParticipant);
    checkCommandStatus(cmdParticipant);
  }

  /**
   * @param participant CDRParticipant
   * @param isDelete true/false
   * @throws CommandException
   */
  private void runUpdateDeleteParticipantCommand(final CDRParticipant participant, final ApicUser user,
      final boolean isDelete)
      throws CommandException {
    CmdModCDRParticipant cmdModPartcipnt =
        new CmdModCDRParticipant(getDataProvider(), this.reviewResult, participant, user, isDelete);
    this.childCmdStack.addCommand(cmdModPartcipnt);
    checkCommandStatus(cmdModPartcipnt);
  }

  /**
   * @param cdrCommand
   * @throws CommandException
   */
  private void checkCommandStatus(final AbstractCDRCommand cdrCommand) throws CommandException {
    if (cdrCommand.getErrorCause() != ERROR_CAUSE.NONE) {
      throw new CommandException(cdrCommand.getErrorMessage());
    }
  }


  /**
   * Create the rule file record
   */
  private void createRuleFile() throws CommandException {
    if (this.ruleFile != null) {
      runCreateCdrFileCommand(this.ruleFile, REVIEW_FILE_TYPE.RULE);
    }
    if (this.compliSSDFilePath != null) {
      runCreateCdrFileCommand(this.compliSSDFilePath, REVIEW_FILE_TYPE.RULE);
    }

    if (this.secRuleSetDataList != null) {
      for (ReviewRuleSetData ruleSetData : this.secRuleSetDataList) {
        String ssdFilePath = ruleSetData.getSsdFilePath();
        if (ssdFilePath != null) {
          runCreateCdrFileCommand(ssdFilePath, REVIEW_FILE_TYPE.RULE);
        }

      }

    }
  }

  /**
   * @throws CommandException
   */
  private void createOutputFiles() throws CommandException {
    if (this.outputFiles == null) {
      return;
    }
    for (String outputFilePath : this.outputFiles) {
      File file = new File(outputFilePath);
      if (file.length() != 0) {
        runCreateCdrFileCommand(outputFilePath, REVIEW_FILE_TYPE.OUTPUT);
      }
    }

  }

  /**
   * @throws CommandException
   */
  private void createOutputFilesForCompliParams() throws CommandException {
    if (this.compliCheckSSDFiles == null) {
      return;
    }
    for (String outputFilePath : this.compliCheckSSDFiles) {
      int size = 0;
      URL url = null;
      try {
        url = new URL("file:/" + outputFilePath);
        InputStream openStream = url.openStream();
        size = openStream.available();
        if (size != 0) {
          runCreateCdrFileCommand(outputFilePath, REVIEW_FILE_TYPE.OUTPUT);
        }
      }
      catch (IOException exp) {
        getDataProvider().getLogger().warn(exp.getMessage(), exp);
        throw new CommandException(exp.getMessage());
      }
    }

  }

  /**
   * @throws CommandException
   */
  private void createInputFiles() throws CommandException {
    if (this.inputFiles != null) {
      for (String inputFilePath : this.inputFiles) {
        runCreateCdrFileCommand(inputFilePath, REVIEW_FILE_TYPE.INPUT);
      }
    }

  }

  /**
   * @throws CommandException create MoniCa file
   */
  private void createMonicaFiles() throws CommandException {
    if (CommonUtils.isNotNull(this.monicaFilePath)) {
      runCreateCdrFileCommand(this.monicaFilePath, REVIEW_FILE_TYPE.MONICA_FILE);
    }
  }

  /**
   * @param inputFilePath
   * @throws CommandException
   */
  private void runCreateCdrFileCommand(final String inputFilePath, final REVIEW_FILE_TYPE fileType)
      throws CommandException {
    CmdModCDRFile cmdModCDRFile = new CmdModCDRFile(getDataProvider(), this.reviewResult, fileType);
    cmdModCDRFile.setFile(inputFilePath);
    this.childCmdStack.addCommand(cmdModCDRFile);
    checkCommandStatus(cmdModCDRFile);
    this.filesCreatedMap.put(inputFilePath, cmdModCDRFile.getIcdmFile());
  }

  /**
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void updateInputFiles() throws CommandException {


    SortedSet<IcdmFile> existingInputFiles = this.reviewResult.getInputFiles();

    ConcurrentMap<String, IcdmFile> existingInputFilesMap = new ConcurrentHashMap<>();
    for (IcdmFile icdmFile : existingInputFiles) {
      existingInputFilesMap.put(icdmFile.getFileName(), icdmFile);
    }

    // Add newly added Input Files if any during cancel operation
    for (String inputFilePath : this.inputFiles) {
      if (existingInputFilesMap.get(inputFilePath) == null) {
        runCreateCdrFileCommand(inputFilePath, REVIEW_FILE_TYPE.INPUT);
      }
    }

    // Delete Input Files existing in DB but not in Current CDRReviewResult Data
    for (Entry<String, IcdmFile> existingInputFilesMapEntry : existingInputFilesMap.entrySet()) {
      if (!this.inputFiles.contains(existingInputFilesMapEntry.getKey())) {
        IcdmFile icdmFile = existingInputFilesMapEntry.getValue();
        final CmdModCDRFile cmdFile =
            new CmdModCDRFile(getDataProvider(), this.reviewResult, icdmFile, true, REVIEW_FILE_TYPE.INPUT);
        this.childCmdStack.addCommand(cmdFile);
        checkCommandStatus(cmdFile);

      }
    }
  }


  /**
   * @throws CommandException
   */
  @SuppressWarnings("null")
  private void createRvwParams() throws CommandException {

    Map<Long, CDRResultParameter> cdrFuncIDResultParamMap = null;
    if (this.parentResult != null) {
      SortedSet<CDRResultParameter> parentResultParams = this.parentResult.getParameters();
      cdrFuncIDResultParamMap = new HashMap<>();
      for (CDRResultParameter cdrResultParameter : parentResultParams) {
        cdrFuncIDResultParamMap.put(cdrResultParameter.getFunctionParameter().getID(), cdrResultParameter);
      }
    }
    SortedSet<CDRResultFunction> cdrResultFunctions = this.reviewResult.getFunctions();
    Map<Long, CDRResultParameter> parentParamsMap = new HashMap<Long, CDRResultParameter>();
    if ((null != this.deltaReviewType) && this.deltaReviewType.equals(DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW)) {
      // fetch the parent params
      ParentRvwParamLoaderInput parentParamLoadInput = new ParentRvwParamLoaderInput();
      if (this.sourcePidcVer == null) {
        parentParamLoadInput.setProjectId(this.pidcVer.getPidc().getID());
      }
      else {
        parentParamLoadInput.setProjectId(this.sourcePidcVer.getPidc().getID());
      }

      if (CommonUtils.isNotNull(this.sourcePIDCVariant)) {
        parentParamLoadInput.setVariantId(this.sourcePIDCVariant.getID());
      }
      else if (CommonUtils.isNotNull(this.pidcVariant)) {
        parentParamLoadInput.setVariantId(this.pidcVariant.getID());
      }

      parentParamLoadInput.setConsiderOfficial(this.offReviewType);
      parentParamLoadInput.setConsiderOfficialLockd(this.onlyLockedOffReview);
      parentParamLoadInput.setConsiderStart(this.startReviewType);
      parentParamLoadInput.setConsiderStartLocked(this.onlyLockedStartResults);
      Set<CDRFuncParameter> cdrFuncParametersSet = new TreeSet<CDRFuncParameter>();
      for (CDRResultFunction cdrResultFunction : cdrResultFunctions) {
        Set<CDRFuncParameter> paramSet = this.funcParamMap.get(cdrResultFunction.getCDRFunction().getID());
        // handle the case for Non relevant params
        if (paramSet == null) {
          paramSet = getParamSetForFunction(cdrResultFunction);
        }
        cdrFuncParametersSet.addAll(paramSet);
      }
      parentParamLoadInput.setParamSet(cdrFuncParametersSet);
      ParentRvwParameterLoader parentRvwLoader = new ParentRvwParameterLoader(getDataProvider());
      parentParamsMap = parentRvwLoader.fetchParentParameters(parentParamLoadInput);
    }


    if (this.funcParamMap != null) {
      for (CDRResultFunction cdrResultFunction : cdrResultFunctions) {
        Set<CDRFuncParameter> cdrFuncParameters = this.funcParamMap.get(cdrResultFunction.getCDRFunction().getID());
        // handle the case for Non relevant params
        if (cdrFuncParameters == null) {
          cdrFuncParameters = getParamSetForFunction(cdrResultFunction);
        }
        if (cdrFuncParameters != null) {

          for (CDRFuncParameter cdrFuncParameter : cdrFuncParameters) {
            CDRRule rule = null;
            CheckSSDResultParam checkSSDResultParam = null;
            CDRRule compliRule = null;
            CheckSSDResultParam checkSSDCompliParam = null;
            if (CommonUtils.isNull(this.monicaFilePath)) {
              if (null != this.ssdRules) {
                rule = CDRRuleUtil.assertSingleRule(this.ssdRules, cdrFuncParameter.getName());
              }
              if (null != this.checkSSDResultParamMap) {
                checkSSDResultParam = this.checkSSDResultParamMap.get(cdrFuncParameter.getName());
              }
              // ICDM-2440
              if (null != this.ssdRulesForCompliParam) {
                compliRule = CDRRuleUtil.assertSingleRule(this.ssdRulesForCompliParam, cdrFuncParameter.getName());
              }
              if (null != this.checkSSDCompliParamMap) {
                checkSSDCompliParam = this.checkSSDCompliParamMap.get(cdrFuncParameter.getName());
              }
            }
            CmdModCDRResultParam cmdModCDRResultParam;
            if (null == this.deltaReviewType) {
              cmdModCDRResultParam = new CmdModCDRResultParam(getDataProvider(), cdrResultFunction, cdrFuncParameter,
                  checkSSDResultParam, rule, compliRule, checkSSDCompliParam,
                  this.shapeReviewResult.getParamResultMap().get(cdrFuncParameter.getName()));


            }
            else if (this.deltaReviewType.equals(DELTA_REVIEW_TYPE.DELTA_REVIEW) &&
                CommonUtils.isNotEmpty(cdrFuncIDResultParamMap)) {
              CDRResultParameter parentCDRParam = cdrFuncIDResultParamMap.get(cdrFuncParameter.getID());
              cmdModCDRResultParam = new CmdModCDRResultParam(getDataProvider(), cdrResultFunction, cdrFuncParameter,
                  checkSSDResultParam, parentCDRParam, DELTA_REVIEW_TYPE.DELTA_REVIEW, rule, compliRule,
                  checkSSDCompliParam, this.shapeReviewResult.getParamResultMap().get(cdrFuncParameter.getName()));
              // ICDM-2579
              if (!this.deltaReviewValid && (null != parentCDRParam)) {
                // if there is a parent cdr parameter for any parameter , then the review is a valid delta review
                this.deltaReviewValid = true;
              }
            }
            else {

              cmdModCDRResultParam = new CmdModCDRResultParam(getDataProvider(), cdrResultFunction, cdrFuncParameter,
                  checkSSDResultParam, parentParamsMap.get(cdrFuncParameter.getID()),
                  DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW, rule, compliRule, checkSSDCompliParam,
                  this.shapeReviewResult.getParamResultMap().get(cdrFuncParameter.getName()));
              // ICDM-2579
              if (!this.deltaReviewValid && (null != parentParamsMap.get(cdrFuncParameter.getID()))) {
                // if there is a parent cdr parameter for any parameter , then the review is a valid delta review
                this.deltaReviewValid = true;
              }
            }
            cmdModCDRResultParam.setSecResultMap(this.secResultMap);
            cmdModCDRResultParam
                .setIcdmFile(this.filesCreatedMap.get(this.paramFilesMap.get(cdrFuncParameter.getName())));
            cmdModCDRResultParam.setCaldataObj(this.calDataMap.get(cdrFuncParameter.getName()));
            if (CommonUtils.isNotNull(this.monicaFilePath)) {
              cmdModCDRResultParam.setMonicaOutput(this.monicaOutput.getParamInfoMap().get(cdrFuncParameter.getName()));
            }
            // ICDM-1785
            cmdModCDRResultParam.setCharacteristicsObj(this.a2lcharMap.get(cdrFuncParameter.getName()));

            // Task 237698
            if ((null != this.paramsWithNoCompliRules) &&
                this.paramsWithNoCompliRules.contains(cdrFuncParameter.getName())) {
              cmdModCDRResultParam.setNoRuleForCompli(true);
            }

            // cmdModCDRResultParam.setErrorInSSDFile(this.errorinSSDFile);

            this.childCmdStack.addCommand(cmdModCDRResultParam);
            checkCommandStatus(cmdModCDRResultParam);
          }
        }
      }
    }

    // TODO: Ascii,Axis pts

  }

  /**
   * @param cdrResultFunction
   * @return
   */
  private Set<CDRFuncParameter> getParamSetForFunction(final CDRResultFunction cdrResultFunction) {
    Set<CDRFuncParameter> paramSet = null;
    // get all the function parameter
    List<CDRFunction> cdrFunList = getDataProvider().getCDRFunList(cdrResultFunction.getName());
    // get the one matching
    for (CDRFunction cdrFunction : cdrFunList) {
      paramSet = this.funcParamMap.get(cdrFunction.getID());
      if (paramSet != null) {
        break;
      }
    }
    return paramSet;
  }

  /**
   * @throws CommandException
   */
  private void createRvwFunctions() throws CommandException {
    if (this.functionSet != null) {
      for (Object cdrFunction : this.functionSet) {
        CmdModCDRRvwFunction cmdModCDRRvwFunction =
            new CmdModCDRRvwFunction(getDataProvider(), this.reviewResult, (CDRFunction) cdrFunction, this.a2lEditorDP);
        this.childCmdStack.addCommand(cmdModCDRRvwFunction);
        checkCommandStatus(cmdModCDRRvwFunction);
      }

    }
  }

  /**
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void updateRvwFunctions() throws CommandException {
    SortedSet<CDRResultFunction> existingCdrResultFunctions = this.reviewResult.getFunctions();
    ConcurrentMap<CDRFunction, CDRResultFunction> existingCdrResultFunctionMap =
        new ConcurrentHashMap<CDRFunction, CDRResultFunction>();

    for (CDRResultFunction cdrResultFunction : existingCdrResultFunctions) {
      existingCdrResultFunctionMap.put(cdrResultFunction.getCDRFunction(), cdrResultFunction);
    }

    if ((this.functionSet == null) || this.functionSet.isEmpty()) {
      // Delete functions existing in DB but not in Current CDRReviewResult Data
      for (Entry<CDRFunction, CDRResultFunction> existingCdrFunctionEntry : existingCdrResultFunctionMap.entrySet()) {
        // TODO:CmdModCDRRvwFunction delete
        CmdModCDRRvwFunction cmdModCDRRvwFunction =
            new CmdModCDRRvwFunction(getDataProvider(), this.reviewResult, existingCdrFunctionEntry.getValue(), false);
        this.childCmdStack.addCommand(cmdModCDRRvwFunction);
        checkCommandStatus(cmdModCDRRvwFunction);
      }
    }
    else {
      // Add newly added Functions if any during cancel operation
      for (Object cdrFunction : this.functionSet) {
        if (existingCdrResultFunctionMap.get(cdrFunction) == null) {
          CmdModCDRRvwFunction cmdModCDRRvwFunction = new CmdModCDRRvwFunction(getDataProvider(), this.reviewResult,
              (CDRFunction) cdrFunction, this.a2lEditorDP);
          this.childCmdStack.addCommand(cmdModCDRRvwFunction);
          checkCommandStatus(cmdModCDRRvwFunction);
        }
      }

      // Delete functions existing in DB but not in Current CDRReviewResult Data
      for (Entry<CDRFunction, CDRResultFunction> existingCdrFunctionEntry : existingCdrResultFunctionMap.entrySet()) {
        if (!this.functionSet.contains(existingCdrFunctionEntry.getKey())) {
          // TODO:CmdModCDRRvwFunction delete
          CmdModCDRRvwFunction cmdModCDRRvwFunction = new CmdModCDRRvwFunction(getDataProvider(), this.reviewResult,
              existingCdrFunctionEntry.getValue(), false);
          this.childCmdStack.addCommand(cmdModCDRRvwFunction);
          checkCommandStatus(cmdModCDRRvwFunction);
        }
      }
    }
  }

  /**
   * Create result entity
   */
  private TRvwResult createResult() {
    final TRvwResult dbRvwRes = new TRvwResult();
    dbRvwRes.setTPidcA2l(getEntityProvider().getDbPidcA2l(this.a2lFile.getPidcA2l().getPidcA2lId()));

    if (CommonUtils.isNotNull(this.ruleSet)) {
      dbRvwRes.settRuleSet(getEntityProvider().getDbRuleSet(this.ruleSet.getID()));
    }
    else {
      dbRvwRes.settRuleSet(null);
    }
    if (this.fcToWp != null) {
      dbRvwRes.setTWorkpackageDivision(getEntityProvider().getDbWrkPkgDiv(this.fcToWp.getID()));
    }
    dbRvwRes.setGrpWorkPkg(this.grpWorkPkg);

    if (this.cdrSourceType == CDR_SOURCE_TYPE.MONICA_FILE) {
      dbRvwRes.setGrpWorkPkg(CDR_SOURCE_TYPE.MONICA_FILE.getUIType());
    }
    // ICDM 658
    dbRvwRes.setDescription(this.description);

    dbRvwRes.setRvwStatus(this.rvwStatus.getDbType());

    if (this.parentResult != null) {
      dbRvwRes.setTRvwResult(getEntityProvider().getDbCDRResult(this.parentResult.getID()));
    }
    // ICDM-2183
    if (this.deltaReviewType == null) {
      dbRvwRes.setDeltaReviewType(null);
    }
    else {
      dbRvwRes.setDeltaReviewType(this.deltaReviewType.getDbType());
    }

    // Icdm-729 Set the Source Type
    if (this.cdrSourceType == null) {
      dbRvwRes.setSourceType(CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getDbType());
    }
    else {
      dbRvwRes.setSourceType(this.cdrSourceType.getDbType());
    }
    // Icdm-874 Set the Review Type Db value

    dbRvwRes.setReviewType(this.reviewType.getDbType());

    setUserDetails(COMMAND_MODE.INSERT, dbRvwRes, ENTITY_ID);

    getEntityProvider().registerNewEntity(dbRvwRes);

    this.reviewResult = new CDRResult(getDataProvider(), dbRvwRes.getResultId());
    this.reviewResult.setSrResult(this.shapeReviewResult);

    getDataCache().getAllCDRResults().put(this.reviewResult.getID(), this.reviewResult);
    final String wpName = this.grpWorkPkg == null ? this.fcToWp == null ? "" : this.fcToWp.getName() : this.grpWorkPkg;
    getDataCache().getResults(this.pidcVer, wpName).add(this.reviewResult);

    // ICDM-609
    if (this.parentResult != null) {
      getDataCache().getCDRResult(this.parentResult.getID()).getChildReviews().add(this.reviewResult);
      // Newly Added for associating the Parent and the child Review
      final TRvwResult parentDb = getEntityProvider().getDbCDRResult(this.parentResult.getID());
      final Set<TRvwResult> childResult = new HashSet<TRvwResult>();
      childResult.add(dbRvwRes);
      parentDb.setTRvwResults(childResult);
    }

    getChangedData().put(this.reviewResult.getID(),
        new ChangedData(ChangeType.INSERT, this.reviewResult.getID(), TRvwResult.class, DisplayEventSource.COMMAND));

    return dbRvwRes;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    TRvwResult dbResult = updateResult();

    // Icdm-875 if only Review Type to be updated no need to call other updates
    if (!this.updateResReviewType) {
      deleteSecondaryResult();
      // make sure the Order is correct.
      getEntityProvider().getEm().flush();
      createSecondaryResult();

      updateRvwFunctions();
      // iCDM-2355
      updateInputFiles();

      // Icdm-729 Update the Fun lab Files
      updateFunLabFiles();
      updatePartipants();

      createRvwParams();
      createSecRvwParams();
      createRuleFile();
      createOutputFiles();
      createOutputFilesForCompliParams();// ICDM-2440
      createRvwAttrValue();
      // ICDM-1980
      updateRvwQnaires();


      // ICDM-2579
      checkForValidDeltaReview(dbResult);
    }

  }

  /**
   * insert new questionnaires and delete removed questionnaires
   *
   * @throws CommandException
   */
  private void updateRvwQnaires() throws CommandException {

//    SortedSet<ReviewQuestionnaire> allReviewQNaires = this.reviewResult.getAllRvwQnaires();
//
//    Map<QuestionnaireVersion, ReviewQuestionnaire> qnaireVersMap = createQnaireRvwQnaire(allReviewQNaires);
//    // find the questionnaires that are to be inserted
//    Set<QuestionnaireVersion> qnaireVersToBeModfed =
//        CommonUtils.getDifference(this.questnaireVersSet, qnaireVersMap.keySet());
//    // insert the new ones
//    createRvwQuestionnaires(qnaireVersToBeModfed);
//
//    // find the questionnaires that are to be deleted
//    qnaireVersToBeModfed = CommonUtils.getDifference(qnaireVersMap.keySet(), this.questnaireVersSet);
//    // delete the removed ones
//    deleteRvwQuestionnaires(qnaireVersToBeModfed, qnaireVersMap);

  }


  /**
   * CommandException- In case of parallel changes detected icdm-943
   */
  private void updateFunLabFiles() throws CommandException {
    SortedSet<IcdmFile> existingFunLabFiles = this.reviewResult.getLabFunFiles();
    ConcurrentMap<String, IcdmFile> existingLabFunFilesMap = new ConcurrentHashMap<>();
    if (existingFunLabFiles != null) {
      for (IcdmFile icdmFile : existingFunLabFiles) {
        existingLabFunFilesMap.put(icdmFile.getFileName(), icdmFile);
      }

    }
    // Add newly added Fun lab files if any during cancel operation
    if (this.funLabFileLoc != null) {
      final File file = new File(this.funLabFileLoc);
      final String fileName = file.getName();
      if (existingLabFunFilesMap.get(fileName) == null) {
        // changed contains to end with
        if (this.funLabFileLoc.endsWith(".fun")) {
          runCreateCdrFileCommand(this.funLabFileLoc, REVIEW_FILE_TYPE.FUNCTION_FILE);
        }
        // changed contains to end with
        else if (this.funLabFileLoc.endsWith(".lab")) {
          runCreateCdrFileCommand(this.funLabFileLoc, REVIEW_FILE_TYPE.LAB_FILE);
        }
      }
    }


    // Delete Lab Fun Files existing in DB but not in Current CDRReviewResult Data
    for (Entry<String, IcdmFile> existingInputFilesMapEntry : existingLabFunFilesMap.entrySet()) {
      if (((this.funLabFileLoc == null) || this.funLabFileLoc.isEmpty()) ||
          ((this.funLabFileLoc != null) && !this.funLabFileLoc.contains(existingInputFilesMapEntry.getKey()))) {
        REVIEW_FILE_TYPE fileType = null;
        // Extra Condition added to get the file type Icdm-729
        if (this.funLabFileLoc.endsWith(".fun") || existingInputFilesMapEntry.getKey().endsWith(".fun")) {
          fileType = REVIEW_FILE_TYPE.FUNCTION_FILE;
        }

        // Extra Condition added to get the file type Icdm-729
        else if (this.funLabFileLoc.endsWith(".lab") || existingInputFilesMapEntry.getKey().endsWith(".lab")) {
          fileType = REVIEW_FILE_TYPE.LAB_FILE;
        }

        final CmdModCDRFile cmdFile = new CmdModCDRFile(getDataProvider(), this.reviewResult,
            existingInputFilesMapEntry.getValue(), true, fileType);
        this.childCmdStack.addCommand(cmdFile);
        checkCommandStatus(cmdFile);
      }
    }

  }

  /**
   * @return
   */
  private TRvwResult updateResult() throws CommandException {

    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.reviewResult.getID(), TRvwResult.class, DisplayEventSource.COMMAND);
    Map<String, String> oldObjectDetails = getDataCache().getCDRResult(this.reviewResult.getID()).getObjectDetails();
    chdata.setOldDataDetails(oldObjectDetails);

    final TRvwResult dbRvwRes = getEntityProvider().getDbCDRResult(this.reviewResult.getID());

    if (this.ruleSet != null) {
      dbRvwRes.settRuleSet(getEntityProvider().getDbRuleSet(this.ruleSet.getID()));
    }

    if (!this.updateStatusAlone) {
      if (this.reviewResult.getReviewType() == REVIEW_TYPE.START) {
        dbRvwRes.setReviewType(this.reviewType.getDbType());
      }

      if (this.reviewType == REVIEW_TYPE.TEST) {

//        if (this.reviewResult.getAllRvwQnaires().isEmpty()) {
//          dbRvwRes.setReviewType(this.reviewType.getDbType());
//        }
//        else {
//          SortedSet<QuestionnaireResponse> responsesToDelete = new TreeSet<>();
//          AbstractProjectObject<?> projObj = this.reviewResult.getVariant();
//          if (projObj == null) {
//            projObj = this.reviewResult.getPidcVersion();
//          }
////          SortedSet<ReviewQuestionnaire> allReviewQNaires = this.reviewResult.getAllRvwQnaires();
////          Map<QuestionnaireVersion, ReviewQuestionnaire> qnaireVersMap = createQnaireRvwQnaire(allReviewQNaires);
////          checkQuestionnaireResponse(responsesToDelete);
////          StringBuilder questionareNames = new StringBuilder();
////          for (QuestionnaireResponse resp : responsesToDelete) {
////            questionareNames.append(resp.getName()).append("\n");
////          }
//          boolean isDelete = !responsesToDelete.isEmpty() && MessageDialogUtils
//              .getConfirmMessageDialog("Delete result ?", "This is the last review attached to the questionnaires:\n" +
//                  questionareNames.toString() + "Do you want to delete ?");
//          if (responsesToDelete.isEmpty() || isDelete) {
//            // delete the questionnaires linked
//
//            // delete the removed ones
//            for (QuestionnaireVersion qnaireVers : qnaireVersMap.keySet()) {
//              // create and call the delete command
//              CmdModRvwQuestionnaire cmdRvwQnaire =
//                  new CmdModRvwQuestionnaire(getDataProvider(), qnaireVersMap.get(qnaireVers));
//              this.childCmdStack.addCommand(cmdRvwQnaire);
//
//
//            }
//
//            // delete the questionnaire responses
//            for (QuestionnaireResponse qnaireVers : responsesToDelete) {
//              // create and call the delete command
//              CmdModQuestionnaireResponse cmdResp =
//                  new CmdModQuestionnaireResponse(getDataProvider(), qnaireVers, projObj);
//              this.childCmdStack.addCommand(cmdResp);
//
//
//            }
//            // Icdm-874 set the Review Type
//            dbRvwRes.setReviewType(this.reviewType.getDbType());
//          }
//        }
      }

      // ICDM 658
      dbRvwRes.setDescription(this.description);

      // Icdm-875 if the update is only for Review Type do not update any thing else
      if (!this.updateResReviewType) {
        dbRvwRes.setTPidcA2l(getEntityProvider().getDbPidcA2l(this.a2lFile.getPidcA2l().getPidcA2lId()));
        if (this.grpWorkPkg == null) {
          if (this.fcToWp == null) {
            dbRvwRes.setGrpWorkPkg(this.grpWorkPkg);
          }
          else {
            dbRvwRes.setTWorkpackageDivision(getEntityProvider().getDbWrkPkgDiv(this.fcToWp.getID()));
          }
        }
        else {
          dbRvwRes.setGrpWorkPkg(this.grpWorkPkg);
        }


        if (this.cdrSourceType == null) {
          dbRvwRes.setSourceType(CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getDbType());
        }
        else {
          dbRvwRes.setSourceType(this.cdrSourceType.getDbType());
        }


        if (this.parentResult != null) {
          dbRvwRes.setTRvwResult(getEntityProvider().getDbCDRResult(this.parentResult.getID()));
        }
        // ICDM-2183
        if (this.deltaReviewType == null) {
          dbRvwRes.setDeltaReviewType(null);
        }
        else {
          dbRvwRes.setDeltaReviewType(this.deltaReviewType.getDbType());
        }
      }

    }
    updateCDRReviewStatus();
    if (this.lockStatus) {
      dbRvwRes.setLockStatus(REVIEW_LOCK_STATUS.YES.getDbType());
    }
    else if (this.lockStatus == this.oldLockStatus) {
      dbRvwRes.setLockStatus(dbRvwRes.getLockStatus());
    }
    else {
      dbRvwRes.setLockStatus(null);
    }

    getChangedData().put(this.reviewResult.getID(), chdata);
    setUserDetails(COMMAND_MODE.UPDATE, dbRvwRes, ENTITY_ID);
    return dbRvwRes;
  }

  /**
   * @param responsesToDelete
   * @param projObj
   */
  private void checkQuestionnaireResponse(final SortedSet<QuestionnaireResponse> responsesToDelete) {
//    SortedSet<ReviewQuestionnaire> rvwQs = this.reviewResult.getDispReviewQNaires();
//    SortedSet<QuestionnaireResponse> qsResponses = new TreeSet<QuestionnaireResponse>();
//    for (ReviewQuestionnaire selQs : rvwQs) {
//      qsResponses.add(selQs.getRvwQuestionnaireResponse());
//    }
//    for (QuestionnaireResponse respo : qsResponses) {
//      respo.setRefreshChildren();
//      if (respo.getRvwResults().size() == 1) {
//        responsesToDelete.add(respo);
//      }
//    }
  }

  /**
   * Update the status of the review (Open, In-Progress, Closed)
   */
  private void updateCDRReviewStatus() {
    TRvwResult dbRvwRes = getEntityProvider().getDbCDRResult(this.reviewResult.getID());

    // ICDM-1746
    if (CommonUtils.isEqual(dbRvwRes.getRvwStatus(), REVIEW_STATUS.OPEN.getDbType()) &&
        ((this.rvwStatus == REVIEW_STATUS.IN_PROGRESS) || (this.rvwStatus == REVIEW_STATUS.CLOSED))) {
      // change the created user to the current user if he finishes the review
      dbRvwRes.setCreatedUser(getDataProvider().getApicDataProvider().getCurrentUser().getUserName());
    }

    if (!this.rvwStatus.equals(CDRConstants.REVIEW_STATUS.OPEN) && this.reviewResult.isAllParamsReviewed()) {
      dbRvwRes.setRvwStatus(CDRConstants.REVIEW_STATUS.CLOSED.getDbType());
    }
    else {
      dbRvwRes.setRvwStatus(this.rvwStatus.getDbType());
    }
  }

  /**
   * {@inheritDoc} Icdm -877 do the delete Command
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    final ChangedData chdata =
        new ChangedData(ChangeType.DELETE, this.reviewResult.getID(), TRvwResult.class, DisplayEventSource.COMMAND);
    Map<String, String> oldObjectDetails = getDataCache().getCDRResult(this.reviewResult.getID()).getObjectDetails();
    chdata.setOldDataDetails(oldObjectDetails);

    deleteRuleFile();
    deleteOutputFile();
    deleteFunLabFiles();
    deleteParticipants();
    deleteSecRvwParams();
    deleteRvwParams();
    deleteInputFiles();
    deleteMonicaFile();
    deleteRVWFunctions();
    deleteRvwAttrValues();
    deleteRvwVaraints();
    deleteSecondaryResult();
    deleteResult();
    getChangedData().put(this.reviewResult.getID(), chdata);
  }

  /**
   * delete the review variants.
   */
  private void deleteRvwVaraints() throws CommandException {
    final ConcurrentMap<Long, CDRReviewVariant> rvwVarMap =
        new ConcurrentHashMap<>(this.reviewResult.getReviewVarMap());

    for (Entry<Long, CDRReviewVariant> rvwVar : rvwVarMap.entrySet()) {
      CmdModRvwVariant cmdModRvwVariant = new CmdModRvwVariant(getDataProvider(), rvwVar.getValue());
      this.childCmdStack.addCommand(cmdModRvwVariant);
      checkCommandStatus(cmdModRvwVariant);
    }
  }

  /**
   * ICdm-1214 delete the Review Attr Values
   */
  private void deleteRvwAttrValues() throws CommandException {
    final ConcurrentMap<Long, CDRReviewAttrValue> rvwAttrValMap =
        new ConcurrentHashMap<>(this.reviewResult.getReviewAttrValMap());

    for (Entry<Long, CDRReviewAttrValue> rvwAttrVal : rvwAttrValMap.entrySet()) {
      CmdModRvwAttrValue cmdModCDRParticipant =
          new CmdModRvwAttrValue(getDataProvider(), this.reviewResult, rvwAttrVal.getValue());
      this.childCmdStack.addCommand(cmdModCDRParticipant);
      checkCommandStatus(cmdModCDRParticipant);
    }

  }

  /**
   * @throws CommandException Get the List of participants associated with the and delete them
   */
  private void deleteParticipants() throws CommandException {
    final ConcurrentMap<Long, CDRParticipant> participantMap =
        new ConcurrentHashMap<>(this.reviewResult.getParticipantMap());

    for (CDRParticipant participant : participantMap.values()) {
      runUpdateDeleteParticipantCommand(participant, null, true);
    }

  }

  /**
   * Delete the Result finally
   */
  private void deleteResult() {
    final TRvwResult dbRvwRes = getEntityProvider().getDbCDRResult(this.reviewResult.getID());
    // Get the parent and delete the relationship
    if (dbRvwRes.getTRvwResult() != null) {
      dbRvwRes.getTRvwResult().getTRvwResults().remove(dbRvwRes);
    }
    getEntityProvider().deleteEntity(dbRvwRes);
  }

  /**
   * @throws CommandException Get the list of Output files and delete them
   */
  private void deleteOutputFile() throws CommandException {
    final SortedSet<IcdmFile> outputFileSet = this.reviewResult.getOutputFiles();
    for (IcdmFile icdmFile : outputFileSet) {
      final CmdModCDRFile cmdFile =
          new CmdModCDRFile(getDataProvider(), this.reviewResult, icdmFile, true, REVIEW_FILE_TYPE.OUTPUT);
      this.childCmdStack.addCommand(cmdFile);
      checkCommandStatus(cmdFile);

    }

  }


  /**
   * @throws CommandException Get the Rule file and delete it
   */
  private void deleteRuleFile() throws CommandException {
    final SortedSet<IcdmFile> ssdRuleFile = this.reviewResult.getRuleFile();
    if (CommonUtils.isNotEmpty(ssdRuleFile)) {
      for (IcdmFile icdmFile : ssdRuleFile) {
        final CmdModCDRFile cmdFile =
            new CmdModCDRFile(getDataProvider(), this.reviewResult, icdmFile, true, REVIEW_FILE_TYPE.RULE);
        this.childCmdStack.addCommand(cmdFile);
        checkCommandStatus(cmdFile);
      }
    }

  }

  /**
   * @throws CommandException Get the Rule file and delete it
   */
  private void deleteMonicaFile() throws CommandException {
    final SortedSet<IcdmFile> monicaFile = this.reviewResult.getMonicaFiles();

    if (!monicaFile.isEmpty()) {
      final CmdModCDRFile cmdFile = new CmdModCDRFile(getDataProvider(), this.reviewResult, monicaFile.first(), true,
          REVIEW_FILE_TYPE.MONICA_FILE);
      this.childCmdStack.addCommand(cmdFile);
      checkCommandStatus(cmdFile);
    }

  }

  /**
   * @throws CommandException delete the Review params
   */
  private void deleteRvwParams() throws CommandException {
    ConcurrentMap<Long, CDRResultParameter> parametersMap =
        new ConcurrentHashMap<>(this.reviewResult.getParametersMap());

    for (CDRResultParameter cdrResParam : parametersMap.values()) {

      CmdModCDRResultParam command = new CmdModCDRResultParam(getDataProvider(), cdrResParam, true, false);
      this.childCmdStack.addCommand(command);
      checkCommandStatus(command);
    }
  }

  /**
   *
   */
  private void deleteFunLabFiles() throws CommandException {

    final SortedSet<IcdmFile> labFunFiles = this.reviewResult.getLabFunFiles();
    for (IcdmFile icdmFile : labFunFiles) {
      REVIEW_FILE_TYPE fileType = null;
      if (icdmFile.getFileName().endsWith(".fun")) {
        fileType = REVIEW_FILE_TYPE.FUNCTION_FILE;
      }
      else if (icdmFile.getFileName().endsWith(".lab")) {
        fileType = REVIEW_FILE_TYPE.LAB_FILE;
      }
      final CmdModCDRFile cmdFile = new CmdModCDRFile(getDataProvider(), this.reviewResult, icdmFile, true, fileType);
      this.childCmdStack.addCommand(cmdFile);
      checkCommandStatus(cmdFile);
    }

  }

  /**
   * @throws CommandException Get the Input files and delete it
   */
  private void deleteInputFiles() throws CommandException {
    final SortedSet<IcdmFile> inpFiles = this.reviewResult.getInputFiles();
    for (IcdmFile icdmFile : inpFiles) {
      final CmdModCDRFile cmdFile =
          new CmdModCDRFile(getDataProvider(), this.reviewResult, icdmFile, true, REVIEW_FILE_TYPE.INPUT);
      this.childCmdStack.addCommand(cmdFile);
      checkCommandStatus(cmdFile);
    }

  }

  /**
   * CommandException -In case of parallel changes detected icdm-943
   */
  private void deleteRVWFunctions() throws CommandException {
    SortedSet<CDRResultFunction> functions = this.reviewResult.getFunctions();
    for (CDRResultFunction cdrResultFunction : functions) {
      CmdModCDRRvwFunction cmdModCDRRvwFunction =
          new CmdModCDRRvwFunction(getDataProvider(), this.reviewResult, cdrResultFunction, false);
      this.childCmdStack.addCommand(cmdModCDRRvwFunction);
      checkCommandStatus(cmdModCDRRvwFunction);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {

    this.childCmdStack.undoAll();

    final TRvwResult dbRvwRes = getEntityProvider().getDbCDRResult(this.reviewResult.getID());
    getEntityProvider().deleteEntity(dbRvwRes);
    getDataCache().getAllCDRResults().remove(this.reviewResult.getID());

    final String wpName = this.grpWorkPkg == null ? this.fcToWp.getName() : this.grpWorkPkg;
    getDataCache().getResults(this.pidcVer, wpName).remove(this.reviewResult);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // No implementation required
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    createResult();
    createRvwFunctions();
    createRvwParams();
    createInputFiles();
    createMonicaFiles();
    createFunLabFiles();
    createRuleFile();
    createOutputFiles();
    createOutputFilesForCompliParams();// ICDM-2440
    createPartipants();
    // Set status of the review result, based on the reviewed flag of all result params
    updateCDRReviewStatus();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    // ICDM-723
    SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();

    if (this.commandMode == COMMAND_MODE.INSERT) {
      createInsertTxnSummary(detailsList);
    }
    else if (this.commandMode == COMMAND_MODE.UPDATE) {
      createUpdateTxnSummary(detailsList);
    }
    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
  }

  /**
   * create Update Transaction Summary
   *
   * @param detailsList parent level summary
   */
  private void createUpdateTxnSummary(final SortedSet<TransactionSummaryDetails> detailsList) {
    String oldVal;
    String newVal;
    addTransactionSummaryDetails(detailsList, this.oldDescription, this.description, "Description");
    addTransactionSummaryDetails(detailsList, this.oldReviewStatus.getUiType(), this.rvwStatus.getUiType(),
        "Review Status");
    oldVal = "";
    newVal = "";
    if (null != this.oldPidcVariant) {
      oldVal = this.oldPidcVariant.getVariantName();
    }
    if (null != this.pidcVariant) {
      newVal = this.pidcVariant.getVariantName();
    }
    addTransactionSummaryDetails(detailsList, oldVal, newVal, "PIDC Variant");
    oldVal = "";
    newVal = "";
    if (null != this.oldCdrSourceType) {
      oldVal = this.oldCdrSourceType.getUIType();
    }
    if (null != this.cdrSourceType) {
      newVal = this.cdrSourceType.getUIType();
    }
    addTransactionSummaryDetails(detailsList, oldVal, newVal, "Source Type");
    addTransactionSummaryDetails(detailsList, this.oldGrpWorkPkg, this.grpWorkPkg, "Group name");
    oldVal = "";
    newVal = "";
    if (null != this.oldreviewType) {
      oldVal = this.oldreviewType.getUiType();
    }
    if (null != this.reviewType) {
      newVal = this.reviewType.getUiType();
    }
    addTransactionSummaryDetails(detailsList, oldVal, newVal, "Review Type");
    oldVal = "";
    newVal = "";
    if (null != this.oldFcToWp) {
      oldVal = this.oldFcToWp.getName();
    }
    if (null != this.fcToWp) {
      newVal = this.fcToWp.getName();
    }
    addTransactionSummaryDetails(detailsList, oldVal, newVal, "WorkPackage name");
  }

  /**
   * create Insert Transaction Summary
   *
   * @param detailsList parent level summary
   */
  private void createInsertTxnSummary(final SortedSet<TransactionSummaryDetails> detailsList) {
    String oldVal;
    String newVal;
    TransactionSummaryDetails details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
    addTransactionSummaryDetails(detailsList, this.oldDescription, this.description, "Description");
    addTransactionSummaryDetails(detailsList, "", this.rvwStatus.getUiType(), "Review Status");
    oldVal = "";
    newVal = "";
    if (null != this.oldPidcVariant) {
      oldVal = this.oldPidcVariant.getVariantName();
    }
    if (null != this.pidcVariant) {
      newVal = this.pidcVariant.getVariantName();
    }
    addTransactionSummaryDetails(detailsList, oldVal, newVal, "PIDC Variant");

    newVal = "";
    if (null != this.cdrSourceType) {
      newVal = this.cdrSourceType.toString();
    }
    addTransactionSummaryDetails(detailsList, "", newVal, "Source Type");

    addTransactionSummaryDetails(detailsList, this.oldGrpWorkPkg, this.grpWorkPkg, "Group name");
    oldVal = "";
    newVal = "";
    if (null != this.oldFcToWp) {
      oldVal = this.oldFcToWp.getName();
    }
    if (null != this.fcToWp) {
      newVal = this.fcToWp.getName();
    }

    addTransactionSummaryDetails(detailsList, "", this.reviewType.getUiType(), "Review Type");
    addTransactionSummaryDetails(detailsList, oldVal, newVal, "WorkPackage name");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    // Not application for insert only command
    if (this.commandMode == COMMAND_MODE.UPDATE) {
      return true;
    }
    return false;
  }

  /**
   * @param modReviewResult CDRResult
   */
  private void setAttributeFieldsToCommand(final CDRResult modReviewResult) {
    this.oldReviewStatus = modReviewResult.getStatus();
    this.oldDescription = modReviewResult.getDescription();
    this.oldPidcVariant = modReviewResult.getVariant();
    if (null != modReviewResult.getFC2WPID()) {
      this.oldFcToWp = getDataProvider().getWorkPackageDivision(modReviewResult.getFC2WPID());
    }
    this.oldGrpWorkPkg = modReviewResult.getGroupWorkPackageName();
    this.oldCdrSourceType = modReviewResult.getCDRSourceType();
    this.oldreviewType = modReviewResult.getReviewType();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", "Review Result :" + getPrimaryObjectIdentifier());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // Icdm-741 check removed for the parent result null
    // No need to Refresh the tree only for Review Type update
    getDataCache().reloadPIDCResults(this.pidcVer);
    getDataCache().reloadResults(this.pidcVer);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.reviewResult == null ? null : this.reviewResult.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "CDR Result";
  }

  /**
   * @param a2lFile the a2lFile to set
   */
  public void setA2lFile(final A2LFile a2lFile) {
    this.a2lFile = a2lFile;
  }

  /**
   * @param pidcVer the pidc version to set
   */
  public void setPidcVersion(final PIDCVersion pidcVer) {
    this.pidcVer = pidcVer;
  }

  /**
   * @param pidcVariant the pidcVariant to set
   */
  public void setPidcVariant(final PIDCVariant pidcVariant) {
    this.pidcVariant = pidcVariant;
  }

  /**
   * Set the rule file
   *
   * @param file file
   */
  public void setRuleFile(final String file) {
    this.ruleFile = file;
  }


  /**
   * @param outputFiles the outputFiles to set
   */
  public void setOutputFiles(final Set<String> outputFiles) {
    this.outputFiles.clear();
    if (outputFiles != null) {
      this.outputFiles.addAll(outputFiles);
    }
  }

  /**
   * @param grpWorkPkg the grpWorkPkg to set
   */
  public void setGrpWorkPkg(final String grpWorkPkg) {
    this.grpWorkPkg = grpWorkPkg;
  }

  /**
   * @param fcToWp the fcToWp to set
   */
  public void setFcToWp(final WorkPackageDivision fcToWp) {
    this.fcToWp = fcToWp;
  }

  /**
   * @param parentResult the parentResult to set
   */
  public void setParentResult(final CDRResult parentResult) {
    this.parentResult = parentResult;
  }

  /**
   * @param functionSet the functionSet to set
   */
  public void setFunctionSet(final SortedSet<CDRFunction> functionSet) {
    if (this.functionSet != null) {
      this.functionSet.clear();
      if (functionSet != null) {
        this.functionSet.addAll(functionSet);
      }
    }
  }

  /**
   * @param checkSSDResultParamMap the checkSSDResultParamMap to set
   */
  public void setCheckSSDResultParamMap(final Map<String, CheckSSDResultParam> checkSSDResultParamMap) {
    this.checkSSDResultParamMap = checkSSDResultParamMap;
  }

  /**
   * @param selParticipants the selParticipants to set
   */
  public void setSelParticipants(final SortedSet<ApicUser> selParticipants) {
    this.selParticipants.clear();
    if (selParticipants != null) {
      this.selParticipants.addAll(selParticipants);
    }
  }

  /**
   * @param selAuditor the selAuditor to set
   */
  public void setSelAuditor(final ApicUser selAuditor) {
    this.selAuditor = selAuditor;
  }

  /**
   * @param selCalEngineer the selCalEngineer to set
   */
  public void setSelCalEngineer(final ApicUser selCalEngineer) {
    this.selCalEngineer = selCalEngineer;
  }

  /**
   * @param reviewFuncParamMap the reviewFuncParamMap to set
   */
  public void setReviewFuncParamMap(final Map<Long, Set<CDRFuncParameter>> reviewFuncParamMap) {
    this.funcParamMap = reviewFuncParamMap;
  }


  /**
   * @return the reviewResult
   */
  public CDRResult getReviewResult() {
    return this.reviewResult;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    // Icdm-877 for delete the result name is stored before the delete
    if (this.resultName != null) {
      return this.resultName;
    }
    return this.reviewResult.getName();
  }

  /**
   * Icdm-729
   *
   * @param funLabFilePath fun lab File path
   */
  public void setFunLabFile(final String funLabFilePath) {
    this.funLabFileLoc = funLabFilePath;

  }

  /**
   * Icdm-874
   *
   * @param reviewType Test or official
   */
  public void setReviewType(final REVIEW_TYPE reviewType) {
    this.reviewType = reviewType;

  }

  /**
   * @param ssdRules ssdRules
   */
  public void setSsdRuleMap(final Map<String, List<CDRRule>> ssdRules) {
    this.ssdRules = ssdRules;

  }

  /**
   * @param attrValModSet set the Attr Value Model Set
   */
  public void setAttrValModSet(final Set<AttributeValueModel> attrValModSet) {
    this.attrValModSet = attrValModSet;

  }

  /**
   * @param attrValModelSet add attr val Model
   */
  public void addAttrValModel(final Set<AttributeValueModel> attrValModelSet) {
    if (!CommonUtils.isNullOrEmpty(attrValModelSet)) {
      if (this.attrValModSet == null) {
        this.attrValModSet = new HashSet<>();
      }
      this.attrValModSet.addAll(attrValModelSet);
    }
  }

  /**
   * @param ruleSet set the Rule set if used in the Review.
   */
  public void setRuleSet(final RuleSet ruleSet) {
    this.ruleSet = ruleSet;
  }

  // ICDM-1720
  /**
   * @param a2lEditorDP
   */
  public void setA2LEditorDp(final A2LEditorDataProvider a2lEditorDP) {
    this.a2lEditorDP = a2lEditorDP;

  }

  /**
   * @return the paramFilesMap
   */
  public Map<String, String> getParamFilesMap() {
    return this.paramFilesMap;
  }


  /**
   * @param paramFilesMap the paramFilesMap to set
   */
  public void setParamFilesMap(final Map<String, String> paramFilesMap) {
    this.paramFilesMap = paramFilesMap;
  }

  /**
   * @param qnaireVersSet set of questionnaire versions
   */
  public void setQuestionnaireVersions(final Set<QuestionnaireVersion> qnaireVersSet) {
    this.questnaireVersSet = qnaireVersSet;
  }

  /**
   * @param monicaFilePath monicaFilePath
   */
  public void setMonicaFilePath(final String monicaFilePath) {
    this.monicaFilePath = monicaFilePath;

  }

  /**
   * @param calDataMap calDataMap
   */
  public void setCaldataMap(final Map<String, CalData> calDataMap) {
    this.calDataMap = calDataMap;

  }

  /**
   * @param monicaOutput monicaOutput
   */
  public void setMonicaOutput(final MonitoringToolOutput monicaOutput) {
    this.monicaOutput = monicaOutput;

  }

  /**
   * @param charMap Map<String, Characteristic>
   */
  public void setCharacteristicsMap(final Map<String, Characteristic> charMap) {
    this.a2lcharMap = charMap;

  }


  /**
   * @param offReviewType the offReviewType to set
   */
  public void setOffReviewType(final boolean offReviewType) {
    this.offReviewType = offReviewType;
  }


  /**
   * @param startReviewType the startReviewType to set
   */
  public void setStartReviewType(final boolean startReviewType) {
    this.startReviewType = startReviewType;
  }


  /**
   * @param onlyLockedOffReview the onlyLockedOffReview to set
   */
  public void setOnlyLockedOffReview(final boolean onlyLockedOffReview) {
    this.onlyLockedOffReview = onlyLockedOffReview;
  }


  /**
   * @param onlyLockedStartResults the onlyLockedStartResults to set
   */
  public void setOnlyLockedStartResults(final boolean onlyLockedStartResults) {
    this.onlyLockedStartResults = onlyLockedStartResults;
  }

  /**
   * @param sourcePIDCVariant
   */
  public void setSourcePIDCVariant(final PIDCVariant sourcePIDCVariant) {
    this.sourcePIDCVariant = sourcePIDCVariant;

  }

  /**
   * @param sourcePidcVer
   */
  public void setSourcePidcVer(final PIDCVersion sourcePidcVer) {
    this.sourcePidcVer = sourcePidcVer;

  }

  /**
   * @param checkSSDCompliParamMap
   */
  public void setCheckSSDCompliParamMap(final Map<String, CheckSSDResultParam> checkSSDCompliParamMap) {
    this.checkSSDCompliParamMap = checkSSDCompliParamMap;
  }

  /**
   * @param compliCheckSSDFiles
   */
  public void setOutputCompliFiles(final Set<String> compliCheckSSDFiles) {
    this.compliCheckSSDFiles.clear();
    if (compliCheckSSDFiles != null) {
      this.compliCheckSSDFiles.addAll(compliCheckSSDFiles);
    }
  }

  /**
   * @param ssdRulesForCompliParam
   */
  public void setSSDRuleForCompliParam(final Map<String, List<CDRRule>> ssdRulesForCompliParam) {
    this.ssdRulesForCompliParam = ssdRulesForCompliParam;
  }

  /**
   * @param compliSSDFilePath
   */
  public void setCompliRuleFilePath(final String compliSSDFilePath) {
    this.compliSSDFilePath = compliSSDFilePath;
  }

  /**
   * @return the deltaReviewValid
   */
  public boolean isDeltaReviewValid() {
    return this.deltaReviewValid;
  }

  /**
   * @param isCancel
   */
  public void setIsFinishOperation(final boolean isFinish) {
    this.isFinish = isFinish;
  }

  /**
   * @param ruleSetDataList ruleSetDataList
   */
  public void setSecondaryRuleSetList(final List<ReviewRuleSetData> ruleSetDataList) {
    this.secRuleSetDataList = ruleSetDataList;
  }


  /**
   * @param inpFiles input file set add method for input files
   */
  public void setInputFiles(final Set<String> inpFiles) {
    this.inputFiles.clear();
    if (inpFiles != null) {
      this.inputFiles.addAll(inpFiles);
    }
  }

  /**
   * @param secResultMap secResultMap
   */
  public void setSecResultMap(final Map<String, RESULT_FLAG> secResultMap) {
    this.secResultMap = secResultMap;
  }

  /**
   * @param paramsWithNoRules Set<String>
   */
  public void setParamsWithNoCompliRules(final Set<String> paramsWithNoRules) {
    this.paramsWithNoCompliRules = paramsWithNoRules;
  }

  /**
   * @param ssdRelID the ssdRelID to set
   */
  public void setSsdRelID(final Long ssdRelID) {
    this.ssdRelID = ssdRelID;
  }

  /**
   * @param errorinSSDFile errorinSSDFile
   */
  public void setErrorinSSDFile(final boolean errorinSSDFile) {
    this.errorinSSDFile = errorinSSDFile;

  }
}
