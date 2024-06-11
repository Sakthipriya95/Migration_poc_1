/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.apic.jpa.bo.IcdmFile;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFile;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParametersSecondary;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.COMPLI_RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.SR_ACCEPTED_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.SR_RESULT;


/**
 * @author bne4cob
 */
public class CDRResultParameter extends AbstractCdrObject implements Comparable<CDRResultParameter> { // NOPMD

  private static final String IS_REVIEWED = "IS_REVIEWED";

  private static final String RVW_COMMENT = "RVW_COMMENT";

  private static final String RVW_FILES = "RVW_FILES";


  /**
   * Reference value
   */
  private CalData refValue;

  /**
   * Checked value
   */
  private CalData checkedValue;

  private ConcurrentHashMap<Long, CDRResParamSecondary> seconndaryParamMap;

  /**
   * enum for columns
   */
  public enum SortColumns {
                           /**
                            * Function Name
                            */
                           SORT_FUNC_NAME,
                           /**
                            * Parameter Name
                            */
                           SORT_PARAM_NAME,
                           // iCDM-848
                           /**
                            * Parameter Long Name
                            */
                           SORT_PARAM_LONG_NAME,
                           /**
                            * Parameter Class
                            */
                           SORT_PARAM_CLASS,
                           /**
                            * Parameter Code word
                            */
                           SORT_PARAM_CODEWORD,
                           /**
                            * Parameter Hint
                            */
                           SORT_PARAM_HINT,
                           /**
                            * Lower limit
                            */
                           SORT_LOWER_LIMIT,
                           /**
                            * Upper limit
                            */
                           SORT_UPPER_LIMIT,
                           /**
                            * Check Value
                            */
                           SORT_CHECK_VALUE,

                           /**
                            * Check value's unit
                            */
                           // ICDM-2151
                           SORT_CHECK_VALUE_UNIT,
                           /**
                            * Result
                            */
                           SORT_RESULT,

                           // Task 231286
                           /**
                            * Sec Result
                            */
                           SORT_SEC_RESULT,
                           /**
                            * Ready for series
                            */
                           SORT_READY_FOR_SERIES,
                           /**
                            * Review Flag
                            */
                           SORT_SCORE,
                           /**
                            * Comment
                            */
                           SORT_COMMENT,

                           /**
                            * Reference Value Icdm-851 Show reference Value in Result editor
                            */
                           SORT_REFERENCE_VALUE,

                           /**
                            * Reference value's unit
                            */
                           // ICDM-2151
                           SORT_REFERENCE_VALUE_UNIT,
                           /**
                            * History flag
                            */
                           SORT_HISTORY_FLAG,
                           /**
                            * Is bitwise flag
                            */
                           SORT_PARAM_BITWISE,
                           /**
                            * Bitwise value
                            */
                           SORT_BITWISE,
                           /**
                            * History Status
                            */
                           SORT_STATUS,
                           /**
                            * User name in History block
                            */
                           SORT_USER,
                           /**
                            * Date in History block
                            */
                           SORT_DATE,
                           /**
                            * Work package in History block
                            */
                           SORT_WP,
                           /**
                            * Project in history block
                            */
                           SORT_PROJECT,
                           /**
                            * Project Variant in history block
                            */
                           SORT_PROJVAR,
                           /**
                            * Test object in history block
                            */
                           SORT_TESTOBJ,

                           /**
                            * Program identifier in history block
                            */
                           SORT_PGMIDENTIFIER,
                           /**
                            * Data identifier in history block
                            */
                           SORT_DATAIDENTIFIER,
                           /**
                            * Remark in history block
                            */
                           SORT_REMARK,

                           /***
                            * Import Column. There is no corresponding variable to hold this value in CDRResultParameter
                            * object as imported values are stored against the review result editor.
                            */
                           SORT_IMP_VALUE,

                           /**
                           *
                           */
                           // ICDM-2439
                           SORT_PARAM_TYPE_COMPLIANCE,

                           /**
                           *
                           */
                           SORT_SHAPE_CHECK,
                           /**
                            * Score description
                            */
                           SORT_SCORE_DESCRIPTION,
  }

  /**
   * @param cdrDataProvider dataprovider
   * @param objID id
   */
  protected CDRResultParameter(final CDRDataProvider cdrDataProvider, final Long objID) {
    super(cdrDataProvider, objID);
    cdrDataProvider.getDataCache().getAllCDRResultParameters().put(objID, this);
  }

  /**
   * @return the parameter object
   */
  public CDRFuncParameter getFunctionParameter() {
    // check if parameter is available in cache
    CDRFuncParameter cdrFuncParam = getCDRFuncParameter();
    // if null, then get it from apic data provider
    if (cdrFuncParam == null) {
      final long paramId = getEntityProvider().getDbCDRResParameter(getID()).getTParameter().getId();
      // obj needs to be created, apicDataProvider will create and provide
      cdrFuncParam = getDataProvider().getCDRFuncParameter(paramId);
    }
    return cdrFuncParam;
  }


  /**
   * @return true if the parameter is compliant
   */
  public boolean isComplianceParameter() {
    CDRFuncParameter functionParameter = getFunctionParameter();
    return functionParameter.isComplianceParameter();

  }

  // ICDM-689
  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getObjectDetails() {
    final Map<String, String> objDetails = new HashMap<String, String>();

    objDetails.put(IS_REVIEWED, Boolean.toString(isReviewed()));
    objDetails.put(RVW_COMMENT, getReviewComments());

    StringBuilder fileIdsBuffer = new StringBuilder();
    for (IcdmFile icdmFile : getAttachments()) {
      fileIdsBuffer.append(icdmFile.getID());
    }
    objDetails.put(RVW_FILES, fileIdsBuffer.toString());


    return objDetails;
  }

  /**
   * @return the ready for series as object
   */
  public ApicConstants.READY_FOR_SERIES getReadyForSeries() {
    return ApicConstants.READY_FOR_SERIES.getType(getEntityProvider().getDbCDRResParameter(getID()).getRvwMethod());
  }

  /**
   * @return the param has bitwise rule or not
   */
  public String isBitWise() {
    return getEntityProvider().getDbCDRResParameter(getID()).getIsbitwise();
  }

  /**
   * @return bitwise limit
   */
  public String getBitwiseLimit() {
    return getEntityProvider().getDbCDRResParameter(getID()).getBitwiseLimit();
  }

  /**
   * @return the string representation of Ready for series
   */
  public String getReadyForSeriesStr() {
    return getReadyForSeries().getUiType();
  }

  /**
   * @return lower limit
   */
  public BigDecimal getLowerLimit() {
    return getEntityProvider().getDbCDRResParameter(getID()).getLowerLimit();
  }

  /**
   * @return upper limit
   */
  public BigDecimal getUpperLimit() {
    return getEntityProvider().getDbCDRResParameter(getID()).getUpperLimit();
  }

  /**
   * @return the unit of checked value
   */
  public String getCheckedUnit() {
    return getEntityProvider().getDbCDRResParameter(getID()).getCheckUnit();
  }

  /**
   * @return the reference unit
   */
  public String getReferenceUnit() {
    return getEntityProvider().getDbCDRResParameter(getID()).getRefUnit();
  }


  /**
   * @return the hint of the rule
   */
  public String getHint() {
    StringBuilder hint = new StringBuilder();
    String paramHint = getFunctionParameter().getParamHint();
    String ruleHint = getEntityProvider().getDbCDRResParameter(getID()).getHint();
    if (!CommonUtils.isEmptyString(paramHint)) {
      hint.append(paramHint).append("\n\n");
    }
    if (!CommonUtils.isEmptyString(ruleHint)) {
      hint.append(ruleHint);
    }
    return hint.toString();
  }

  /**
   * @return the latest hitory entry
   */
  public HistoryEntry getLatestHistory() {
    CalData calData = getCheckedValueObj();
    HistoryEntry latestHistoryEntry = null;
    if (calData != null) {

      CalDataHistory calDataHistory = calData.getCalDataHistory();
      if ((calDataHistory != null) && (calDataHistory.getHistoryEntryList() != null) &&
          !calDataHistory.getHistoryEntryList().isEmpty()) {
        // Last Element from the HistoryEntryList is considered as the one with latest timestamp
        latestHistoryEntry = calDataHistory.getHistoryEntryList().get(calDataHistory.getHistoryEntryList().size() - 1);
      }
    }
    return latestHistoryEntry;
  }

  /**
   * @return State in History block
   */
  public String getHistoryState() {
    String result = "";
    if (null != getLatestHistory()) {
      result =
          CommonUtils.checkNull(getLatestHistory().getState() == null ? "" : getLatestHistory().getState().getValue());
    }
    return result;
  }

  /**
   * @return User in History block
   */
  public String getHistoryUser() {
    String result = "";
    if (null != getLatestHistory()) {
      result = CommonUtils
          .checkNull(getLatestHistory().getPerformedBy() == null ? "" : getLatestHistory().getPerformedBy().getValue());
    }
    return result;
  }

  /**
   * @return Date in History block
   */
  public String getHistoryDate() {
    String result = "";
    if (null != getLatestHistory()) {
      result =
          CommonUtils.checkNull(getLatestHistory().getDate() == null ? "" : getLatestHistory().getDate().getValue());
    }
    return result;
  }

  /**
   * @return Context in History block
   */
  public String getHistoryContext() {
    String result = "";
    if (null != getLatestHistory()) {
      result = CommonUtils
          .checkNull(getLatestHistory().getContext() == null ? "" : getLatestHistory().getContext().getValue());
    }
    return result;
  }

  /**
   * @return Project in History block
   */
  public String getHistoryProject() {
    String result = "";
    if (null != getLatestHistory()) {
      result = CommonUtils
          .checkNull(getLatestHistory().getProject() == null ? "" : getLatestHistory().getProject().getValue());
    }
    return result;
  }

  /**
   * @return TargetVariant in History block
   */
  public String getHistoryTargetVariant() {
    String result = "";
    if (null != getLatestHistory()) {
      result = CommonUtils.checkNull(
          getLatestHistory().getTargetVariant() == null ? "" : getLatestHistory().getTargetVariant().getValue());
    }
    return result;
  }

  /**
   * @return TestObject in History block
   */
  public String getHistoryTestObject() {
    String result = "";
    if (null != getLatestHistory()) {
      result = CommonUtils
          .checkNull(getLatestHistory().getTestObject() == null ? "" : getLatestHistory().getTestObject().getValue());
    }
    return result;
  }

  /**
   * @return ProgramIdentifier in History block
   */
  public String getHistoryProgramIdentifier() {
    String result = "";
    if (null != getLatestHistory()) {
      result = CommonUtils.checkNull(getLatestHistory().getProgramIdentifier() == null ? ""
          : getLatestHistory().getProgramIdentifier().getValue());
    }
    return result;
  }

  /**
   * @return DataIdentifier() in History block
   */
  public String getHistoryDataIdentifier() {
    String result = "";
    if (null != getLatestHistory()) {
      result = CommonUtils.checkNull(
          getLatestHistory().getDataIdentifier() == null ? "" : getLatestHistory().getDataIdentifier().getValue());
    }
    return result;
  }

  /**
   * @return Remark() in History block
   */
  public String getHistoryRemark() {
    String result = "";
    if (null != getLatestHistory()) {
      result = CommonUtils
          .checkNull(getLatestHistory().getRemark() == null ? "" : getLatestHistory().getRemark().getValue());
    }
    return result;
  }

  /**
   * @return result
   */
  public String getCommonResult() {
    return getResultEnum().getUiType();
  }

  /**
   * @return result
   */
  public String getCompliResult() {
    COMPLI_RESULT_FLAG compliResultEnum = getCompliResultEnum();
    if (compliResultEnum == null) {
      return "";
    }
    return compliResultEnum.getUiType();
  }


  /**
   * @return the compliance param enum
   */
  public COMPLI_RESULT_FLAG getCompliResultEnum() {
    if (getEntityProvider().getDbCDRResParameter(getID()).getCompliResult() != null) {
      return CDRConstants.COMPLI_RESULT_FLAG
          .getType(getEntityProvider().getDbCDRResParameter(getID()).getCompliResult());
    }
    return null;
  }

  /**
   * @return the review result enum.
   */
  private RESULT_FLAG getResultEnum() {
    return CDRConstants.RESULT_FLAG.getType(getEntityProvider().getDbCDRResParameter(getID()).getResult());
  }

  /**
   * @return the consolidated result
   */
  public String getResult() {

    // If the parameter is compliant and its compli result is not OK, display review result as 'COMPLI'
    COMPLI_RESULT_FLAG compliType =
        CDRConstants.COMPLI_RESULT_FLAG.getType(getEntityProvider().getDbCDRResParameter(getID()).getCompliResult());
    if ((null != getEntityProvider().getDbCDRResParameter(getID()).getCompliResult()) &&
        (CDRConstants.COMPLI_RESULT_FLAG.OK != compliType)) {
      return compliType.getUiType();
    }
    // If the parameter is compliant and its compli result is OK (or) If the parameter is not compliant ,check for Shape
    // review result (or)
    // If the shape review fails, check for the Shape review acceptance flag
    if ((null != getEntityProvider().getDbCDRResParameter(getID()).getSrResult()) &&
        (CDRConstants.SR_RESULT.FAIL == CDRConstants.SR_RESULT
            .getType(getEntityProvider().getDbCDRResParameter(getID()).getSrResult()))) {
      // If the failed Shape review result is not acccepted, display review result as 'SHAPE'
      if ((null == getEntityProvider().getDbCDRResParameter(getID()).getSrAcceptedFlag()) ||
          (CDRConstants.SR_ACCEPTED_FLAG.NO == CDRConstants.SR_ACCEPTED_FLAG
              .getType(getEntityProvider().getDbCDRResParameter(getID()).getSrAcceptedFlag()))) {
        return CDRConstants.RESULT_FLAG.SHAPE.getUiType();
      }
    }
    return getCommonResult();
  }

  /**
   * @return the lab obj id
   */
  public Long getLabObjID() {
    return getEntityProvider().getDbCDRResParameter(getID()).getLabObjId();
  }

  /**
   * @return the lab obj id
   */
  public Long getRevID() {
    return getEntityProvider().getDbCDRResParameter(getID()).getRevId();
  }


  /**
   * @return the lab obj id
   */
  public Long getCompliLabObjID() {
    return getEntityProvider().getDbCDRResParameter(getID()).getCompliLabObjId();
  }

  /**
   * @return the secondary Result
   */
  // Task 236308
  public RESULT_FLAG getSecondaryResEnum() {

    return CDRConstants.RESULT_FLAG.getType(getEntityProvider().getDbCDRResParameter(getID()).getSecondaryResult());
  }

  /**
   * gets the sec rvw result state enum
   *
   * @return the result falg
   */
  // Task 236308
  public RESULT_FLAG getSecondaryResStateEnum() {

    return CDRConstants.RESULT_FLAG
        .getType(getEntityProvider().getDbCDRResParameter(getID()).getSecondaryResultState());
  }

  /**
   * gets theshape check result result enum
   *
   * @return shape check accepted flag
   */
  public SR_ACCEPTED_FLAG getShapeCheckResultEnum() {

    return CDRConstants.SR_ACCEPTED_FLAG.getType(getEntityProvider().getDbCDRResParameter(getID()).getSrAcceptedFlag());
  }

  /**
   * To shown secondary result column in review result editor
   *
   * @return string of custom secondary result in result editor
   */
  // Task 231287
  public String getCustomSecondaryResult() {
    String result = "";
    RESULT_FLAG secondaryResStateEnum = getSecondaryResStateEnum();
    if (RESULT_FLAG.CHECKED == secondaryResStateEnum) {
      result = RESULT_FLAG.CHECKED.getUiType();
    }
    else {
      String secondaryResult = getSecondaryResult();
      switch (secondaryResult) {
        case "OK":
        case "COMPLI":
          result = secondaryResult;
          break;
        case "Not OK":
        case "Low":
        case "High":
          result = "Not OK";
          break;
        case "???":
          result = "N/A";
          break;
        default:
          break;
      }
    }
    return result;
  }

  /**
   * @return the secondary Result
   */
  // Task 236308
  public String getSecondaryResult() {
    if (RESULT_FLAG.CHECKED == getSecondaryResStateEnum()) {
      return RESULT_FLAG.CHECKED.getUiType();
    }
    return getSecondaryResEnum().getUiType();
  }

  /**
   * @return the secondary result state
   */
  // Task 236308
  public String getSecondaryResultState() {
    return getSecondaryResStateEnum().getUiType();
  }


  /**
   * @return the lab obj id
   */
  public Long getCompliRevID() {
    return getEntityProvider().getDbCDRResParameter(getID()).getCompliRevId();
  }

  /**
   * Gets the CDR result for the parameter
   *
   * @return CDRResult
   */
  public CDRResult getReviewResult() {
    final long resultId = getEntityProvider().getDbCDRResParameter(getID()).getTRvwResult().getResultId();
    return getDataCache().getCDRResult(resultId);
  }

  /**
   * @return reviewed flag
   */
  public boolean isReviewed() {
    // ICDM-2307
    return DATA_REVIEW_SCORE.getType(getEntityProvider().getDbCDRResParameter(getID()).getReviewScore()).isReviewed();
  }

  // ICDM-2307
  /**
   * @return Review score
   */
  public DATA_REVIEW_SCORE getScore() {
    return DATA_REVIEW_SCORE.getType(getEntityProvider().getDbCDRResParameter(getID()).getReviewScore());

  }

  /**
   * @return score display string
   */
  public String getScoreUIType() {
    return getScore().getScoreDisplay();
  }

  /**
   * @return true if exact match flag set
   */
  public boolean isExactMatchRefValue() {
    if (ApicConstants.YES.equals(getEntityProvider().getDbCDRResParameter(getID()).getMatchRefFlag())) {
      return true;
    }
    return false;
  }

  /**
   * @return review comments
   */
  public String getReviewComments() {
    return getEntityProvider().getDbCDRResParameter(getID()).getRvwComment();
  }

  // ICDM-1720
  /**
   * @return the input file name in which the parameter is present
   */
  public String getInputFileName() {
    String fileName = "";
    IcdmFile icdmFile = getApicDataProvider().getIcdmFile(CDRConstants.CDR_FILE_NODE_ID,
        getEntityProvider().getDbCDRResParameter(getID()).getTRvwFile().getTabvIcdmFile().getFileId());
    if (null != icdmFile) {
      fileName = icdmFile.getFileName();
    }
    return fileName;
  }

  /**
   * Method to get Reference value object
   *
   * @return actual review output
   */
  public CalData getRefValueObj() {
    if (this.refValue == null) {
      this.refValue = getCDPObj(getEntityProvider().getDbCDRResParameter(getID()).getRefValue());
    }
    return this.refValue;
  }

  /**
   * Method to get CheckedValue object
   *
   * @return actual review output
   */
  public CalData getCheckedValueObj() {
    if (this.checkedValue == null) {
      this.checkedValue = getCDPObj(getEntityProvider().getDbCDRResParameter(getID()).getCheckedValue());
    }
    return this.checkedValue;
  }

  /**
   * Method to convert byte array to CaldataPhy object
   *
   * @return actual review output
   */
  private CalData getCDPObj(final byte[] dbData) {
    try {
      return CalDataUtil.getCalDataObj(dbData);
    }
    catch (ClassNotFoundException | IOException e) {
      getLogger().error("Error reading CalDataPhy object for parameter : " + getFunctionParameter().getName(), e);
    }
    return null;

  }

  /**
   * Return the string representation of reference value
   *
   * @return String
   */
  public String getRefValueString() {
    if (getRefValueObj() != null) {
      return getRefValueObj().getCalDataPhy().getSimpleDisplayValue();
    }
    return "";
  }

  /**
   * Return the string representation of checked value
   *
   * @return String
   */
  public String getCheckedValueString() {
    if (getCheckedValueObj() != null) {
      return getCheckedValueObj().getCalDataPhy().getSimpleDisplayValue();
    }
    return "";
  }

  /**
   * @return files attached to this parameter
   */
  public SortedSet<IcdmFile> getAttachments() {
    final SortedSet<IcdmFile> attachedFilesSet = new TreeSet<IcdmFile>();
    for (TRvwFile dbFile : getEntityProvider().getDbCDRResParameter(getID()).getTRvwFiles()) {
      attachedFilesSet
          .add(getApicDataProvider().getIcdmFile(CDRConstants.CDR_FILE_NODE_ID, dbFile.getTabvIcdmFile().getFileId()));
    }
    return attachedFilesSet;
  }

  /**
   * @return the created user
   */
  @Override
  public final String getCreatedUser() {
    return getEntityProvider().getDbCDRResParameter(getID()).getCreatedUser();
  }

  /**
   * @return the created date
   */
  @Override
  public final Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCDRResParameter(getID()).getCreatedDate());
  }

  /**
   * @return the modified date
   */
  @Override
  public final Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCDRResParameter(getID()).getModifiedDate());
  }

  /**
   * @return the modified user
   */
  @Override
  public final String getModifiedUser() {
    return getEntityProvider().getDbCDRResParameter(getID()).getModifiedUser();
  }

  /**
   * Gets CDR Function Parameter from cache
   *
   * @return
   */
  private CDRFuncParameter getCDRFuncParameter() {
    final String paramName = getEntityProvider().getDbCDRResParameter(getID()).getTParameter().getName();
    final String pType = getEntityProvider().getDbCDRResParameter(getID()).getTParameter().getPtype();
    return getDataProvider().getCDRFuncParameter(paramName, pType);
  }

  /**
   * @return the parent CDR result function object Icdm-548 method made as public
   */
  public CDRResultFunction getFunction() {
    return getCDRResult().getResFunctionMap()
        .get(getEntityProvider().getDbCDRResParameter(getID()).getTRvwFunction().getRvwFunId());
  }


  /**
   * @return the function name of this parameter
   */
  public String getFunctionName() {
    return getFunction().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDRResultParameter other) {
    int compareResult = ApicUtil.compare(getFunctionParameter().getName(), other.getFunctionParameter().getName());
    if (compareResult == 0) {
      compareResult = ApicUtil.compareLong(getID(), other.getID());
    }
    return compareResult;
  }

  /**
   * @param param2 parameter to be compared with
   * @param sortColumn name of the sortColumn
   * @return int
   */
  public int compareTo(final CDRResultParameter param2, final SortColumns sortColumn) { // NOPMD

    int compareResult;

    switch (sortColumn) {

      case SORT_PARAM_TYPE_COMPLIANCE:
        compareResult = ApicUtil.compareBoolean(isComplianceParameter(), param2.isComplianceParameter());
        break;
      case SORT_SHAPE_CHECK:
        compareResult = ApicUtil.compare(getSrResult(), param2.getSrResult());
        break;
      case SORT_FUNC_NAME:
        // comparing the function names
        compareResult = ApicUtil.compare(getFunctionName(), param2.getFunctionName());
        break;
      // iCDM-848
      case SORT_PARAM_LONG_NAME:
        // comparing the parameter long name
        compareResult =
            ApicUtil.compare(getCDRFuncParameter().getLongName(), param2.getCDRFuncParameter().getLongName());
        break;
      case SORT_PARAM_CLASS:
        // comparing the parameter class (screw, nail, revit..)
        compareResult =
            ApicUtil.compare(getCDRFuncParameter().getpClassText(), param2.getCDRFuncParameter().getpClassText());
        break;
      case SORT_PARAM_CODEWORD:
        // comparing the parameter code word ( yes, no)
        compareResult =
            ApicUtil.compare(getCDRFuncParameter().getCodeWord(), param2.getCDRFuncParameter().getCodeWord());
        break;
      case SORT_PARAM_HINT:
        // comparing the parameter hint
        compareResult = ApicUtil.compare(getHint(), param2.getHint());
        break;
      case SORT_PARAM_BITWISE:
        // comparing the parameter hint
        compareResult = ApicUtil.compare(isBitWise(), param2.isBitWise());
        break;
      case SORT_BITWISE:
        // comparing the parameter hint
        compareResult = ApicUtil.compare(getBitwiseLimit(), param2.getBitwiseLimit());
        break;
      case SORT_LOWER_LIMIT:
        // compare the lower limits of the parameters
        compareResult = ApicUtil.compareBigDecimal(getLowerLimit(), param2.getLowerLimit());
        break;

      case SORT_UPPER_LIMIT:
        // comparing the upper limits of the parameters
        compareResult = ApicUtil.compareBigDecimal(getUpperLimit(), param2.getUpperLimit());
        break;

      case SORT_CHECK_VALUE:
        compareResult = caseChkdVal(param2);
        break;

      // ICDM-2151
      case SORT_CHECK_VALUE_UNIT:
        compareResult = ApicUtil.compare(getCheckedUnit(), param2.getCheckedUnit());
        break;

      case SORT_RESULT:
        // comparing the result using string compare
        compareResult = ApicUtil.compare(getResult(), param2.getResult());
        break;

      // Task 236308
      case SORT_SEC_RESULT:
        // comparing the result using string compare
        compareResult = ApicUtil.compare(getCustomSecondaryResult(), param2.getCustomSecondaryResult());
        break;

      case SORT_READY_FOR_SERIES:
        // comparing the revady for series using string compare
        compareResult = ApicUtil.compare(getReadyForSeriesStr(), param2.getReadyForSeriesStr());
        break;

      case SORT_SCORE:
        // review flags are checked using boolean compare
        compareResult = ApicUtil.compare(getScoreUIType(), param2.getScoreUIType());
        break;
      case SORT_SCORE_DESCRIPTION:
        compareResult = ApicUtil.compare(getScoreDescription(), param2.getScoreDescription());
        break;
      case SORT_COMMENT:
        // comparing the comments using string compare
        compareResult = ApicUtil.compare(getReviewComments(), param2.getReviewComments());
        break;
      // Icdm-851 Sort the Reference Values
      case SORT_REFERENCE_VALUE:
        compareResult = ApicUtil.compare(getRefValueString(), param2.getRefValueString());
        break;

      // ICDM-2151
      case SORT_REFERENCE_VALUE_UNIT:
        compareResult = ApicUtil.compare(getReferenceUnit(), param2.getReferenceUnit());
        break;

      case SORT_STATUS:

        compareResult = ApicUtil.compare(getHistoryState(), param2.getHistoryState());

        break;
      case SORT_USER:

        compareResult = ApicUtil.compare(getHistoryUser(), param2.getHistoryUser());

        break;
      case SORT_DATE:

        compareResult = ApicUtil.compare(getHistoryDate(), param2.getHistoryDate());

        break;
      case SORT_WP:

        compareResult = ApicUtil.compare(getHistoryContext(), param2.getHistoryContext());

        break;
      case SORT_PROJECT:

        compareResult = ApicUtil.compare(getHistoryProject(), param2.getHistoryProject());

        break;
      case SORT_PROJVAR:

        compareResult = ApicUtil.compare(getHistoryTargetVariant(), param2.getHistoryTargetVariant());

        break;
      case SORT_TESTOBJ:

        compareResult = ApicUtil.compare(getHistoryTestObject(), param2.getHistoryTestObject());

        break;
      case SORT_PGMIDENTIFIER:

        compareResult = ApicUtil.compare(getHistoryProgramIdentifier(), param2.getHistoryProgramIdentifier());

        break;
      case SORT_DATAIDENTIFIER:

        compareResult = ApicUtil.compare(getHistoryDataIdentifier(), param2.getHistoryDataIdentifier());

        break;
      case SORT_REMARK:

        compareResult = ApicUtil.compare(getHistoryRemark(), param2.getHistoryRemark());

        break;
      default:
        // Compare name
        compareResult = compareTo(param2);
        break;
    }

    // additional compare if both the values are same
    if (compareResult == 0) {
      // compare result is equal, compare the parameter name
      compareResult = compareTo(param2);
    }

    return compareResult;
  }

  /**
   * @param param2
   * @return
   */
  private int caseChkdVal(final CDRResultParameter param2) {
    int compareResult;

    CalData chkVal1 = getCheckedValueObj();
    CalData chkVal2 = param2.getCheckedValueObj();
    if ((null != chkVal1) && (null != chkVal2) && (null != chkVal1.getCalDataPhy()) &&
        (null != chkVal2.getCalDataPhy())) {
      compareResult = chkVal1.getCalDataPhy().compareTo(chkVal2.getCalDataPhy(),
          com.bosch.calmodel.caldataphy.CalDataPhy.SortColumns.SIMPLE_DISPLAY_VALUE);
    }
    else {
      // comparing the checked values
      // In this case string comparison takes place
      compareResult = ApicUtil.compare(getCheckedValueString(), param2.getCheckedValueString());
    }
    return compareResult;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }

    return getID().equals(((CDRResultParameter) obj).getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return getID().intValue();
  }

  /**
   * Indicates whether lower limit has changed from parent review. If this is not a delta review, the method returns
   * false.
   *
   * @return true/false
   */
  public boolean isLowerLimitChanged() {
    if (!getCDRResult().isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.LOWER_LIMT
        .isSet(getEntityProvider().getDbCDRResParameter(getID()).getChangeFlag());
  }

  /**
   * Indicates whether upper limit has changed from parent review. If this is not a delta review, the method returns
   * false.
   *
   * @return true/false
   */
  public boolean isUpperLimitChanged() {
    if (!getCDRResult().isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.UPPER_LIMIT
        .isSet(getEntityProvider().getDbCDRResParameter(getID()).getChangeFlag());
  }

  /**
   * Indicates whether result has changed from parent review. If this is not a delta review, the method returns false.
   *
   * @return true/false
   */
  public boolean isResultChanged() {
    if (!getCDRResult().isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.RESULT
        .isSet(getEntityProvider().getDbCDRResParameter(getID()).getChangeFlag()) ||
        CDRConstants.PARAM_CHANGE_FLAG.COMPLI_RESULT
            .isSet(getEntityProvider().getDbCDRResParameter(getID()).getChangeFlag());
  }

  /**
   * Indicates whether secondary result has changed from parent review. If this is not a delta review, the method
   * returns false.
   *
   * @return true/false
   */
  // Task 231287
  public boolean isSecondaryResultChanged() {
    if (!getCDRResult().isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.SECONDARY_RESULT
        .isSet(getEntityProvider().getDbCDRResParameter(getID()).getChangeFlag());
  }


  /**
   * Icdm-945 Indicates whether Ref value has changed from parent review. If this is not a delta review, the method
   * returns false.
   *
   * @return true/false
   */
  public boolean isRefValChanged() {
    if (!getCDRResult().isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.REF_VALUE
        .isSet(getEntityProvider().getDbCDRResParameter(getID()).getChangeFlag());
  }

  /**
   * Indicates whether checked value has changed from parent review. If this is not a delta review, the method returns
   * false.
   *
   * @return true/false
   */
  public boolean isCheckedValueChanged() {
    if (!getCDRResult().isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.CHECK_VALUE
        .isSet(getEntityProvider().getDbCDRResParameter(getID()).getChangeFlag());
  }

  /**
   * Indicates whether checked value unit is different from reference value unit
   *
   * @return true/false
   */
  // iCDM-2151
  public boolean isCheckValueRefValueUnitDifferent() {
    if (CommonUtils.isEmptyString(getReferenceUnit())) {
      return false;
    }
    return !CommonUtils.isEqual(CommonUtils.checkNull(getCheckedUnit()), getReferenceUnit());
  }

  /**
   * Indicates whether checked value has changed from parent review. If this is not a delta review, the method returns
   * false.
   *
   * @return true/false
   */
  public boolean isBitwiseFlagChanged() {
    if (!getCDRResult().isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.BITWISE_FLAG
        .isSet(getEntityProvider().getDbCDRResParameter(getID()).getChangeFlag());
  }

  /**
   * Indicates whether checked value has changed from parent review. If this is not a delta review, the method returns
   * false.
   *
   * @return true/false
   */
  public boolean isBitwiseLimitChanged() {
    if (!getCDRResult().isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.BITWISE_LIMIT
        .isSet(getEntityProvider().getDbCDRResParameter(getID()).getChangeFlag());
  }

  /**
   * Indicates whether checked value has changed from parent review. If this is not a delta review, the method returns
   * false.
   *
   * @return true/false
   */
  public boolean isScoreChanged() {
    if (!getCDRResult().isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.SCORE_VALUE
        .isSet(getEntityProvider().getDbCDRResParameter(getID()).getChangeFlag());
  }

  /**
   * @return the parent CDR result object
   */
  public CDRResult getCDRResult() {
    return getDataCache().getCDRResult(getEntityProvider().getDbCDRResParameter(getID()).getTRvwResult().getResultId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getFunctionParameter().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.CDR_RES_PARAMETER;
  }

  // ICDM-1197
  /**
   * Checks whether history information is available for the parameter
   *
   * @return true if history is available
   */
  public boolean hasHistory() {
    if (getCheckedValueObj() != null) {
      CalDataHistory calDataHistory = getCheckedValueObj().getCalDataHistory();
      if ((calDataHistory != null) && (calDataHistory.getHistoryEntryList() != null) &&
          !calDataHistory.getHistoryEntryList().isEmpty()) {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getFunctionParameter().getDescription();
  }

  // ICDM-1320
  /**
   * This method returns the checked value display string for the parameter
   *
   * @return checkvalue string
   */
  public String getParentCheckedValString() {
    if (null != getParentCheckedVal()) {
      return getParentCheckedVal().getCalDataPhy().getSimpleDisplayValue();
    }
    return "";
  }

  /**
   * This method returns the checked value for the parameter
   *
   * @return checkvalue string
   */
  public CalData getParentCheckedVal() {

    CDRResultParameter parentParam = getParentParam();
    if (null != parentParam) {
      return parentParam.getCheckedValueObj();
    }
    return null;
  }

  /**
   * This method returns the reference value display string for the parameter
   *
   * @return refvalue string
   */
  public String getParentRefValString() {
    if (null != getParentRefVal()) {
      return getParentRefVal().getCalDataPhy().getSimpleDisplayValue();
    }
    return "";
  }

  /**
   * This method returns the reference value for the parameter
   *
   * @return refvalue
   */
  public CalData getParentRefVal() {
    // ICDM-1940
    CDRResultParameter parentParam = getParentParam();
    if (null != parentParam) {
      return parentParam.getRefValueObj();
    }
    return null;
  }

  // ICDM-1940
  /**
   * @return ParentParam
   */
  public CDRResultParameter getParentParam() {
    if (getCDRResult().isDeltaReview()) {
      // For normal delta review get the parameter from the parent review
      if (getCDRResult().getDeltaReviewType().equals(DELTA_REVIEW_TYPE.DELTA_REVIEW)) {
        return getDeltaReviewParentParam();
      } // For project data delta review get the parameter from the parent param id stored in TRvwParameter table
      else if (getCDRResult().getDeltaReviewType().equals(DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW)) {
        if (null != getEntityProvider().getDbCDRResParameter(getID()).getTRvwParameter()) {
          long rvwParamId = getEntityProvider().getDbCDRResParameter(getID()).getTRvwParameter().getRvwParamId();
          CDRResultParameter rankedParam = getDataCache().getCDRResultParameter(rvwParamId) == null
              ? new CDRResultParameter(getDataProvider(), rvwParamId)
              : getDataCache().getCDRResultParameter(rvwParamId);
          return rankedParam;
        }
      }
    }
    return null;
  }

  /**
   * @return parent param for delta review
   */
  private CDRResultParameter getDeltaReviewParentParam() {
    CDRResult parentReview = getCDRResult().getParentReview();
    for (CDRResultParameter selParam : parentReview.getParametersMap().values()) {
      if (ApicUtil.compare(selParam.getName(), getName()) == 0) {
        return selParam;
      }
    }
    return null;
  }

  /**
   * @return ParentLowerLimitString
   */
  public String getParentLowerLimitString() {
    CDRResultParameter parentParam = getParentParam();
    if ((null != parentParam) && (null != parentParam.getLowerLimit())) {
      return parentParam.getLowerLimit().toString();
    }
    return "";
  }

  /**
   * @return ParentUpperLimitString
   */
  public String getParentUpperLimitString() {
    CDRResultParameter parentParam = getParentParam();
    if ((null != parentParam) && (null != parentParam.getUpperLimit())) {
      return parentParam.getUpperLimit().toString();
    }
    return "";
  }

  /**
   * @return ParentBitwiseValString
   */
  public String getParentBitwiseValString() {
    CDRResultParameter parentParam = getParentParam();
    if ((null != parentParam) && (null != parentParam.isBitWise())) {
      return parentParam.isBitWise().equals(ApicConstants.YES) ? ApicConstants.CODE_WORD_YES
          : ApicConstants.CODE_WORD_NO;
    }
    return "";
  }

  /**
   * @return ParentBitwiseLimitString
   */
  public String getParentBitwiseLimitString() {
    CDRResultParameter parentParam = getParentParam();
    if ((null != parentParam) && (null != parentParam.getBitwiseLimit())) {
      return parentParam.getBitwiseLimit().toString();
    }
    return "";
  }

  /**
   * @return ParentResultValueString
   */
  public String getParentResultValueString() {
    CDRResultParameter parentParam = getParentParam();
    if ((null != parentParam) && (null != parentParam.getResult())) {
      return parentParam.getResult();
    }
    return "";
  }

  // Task 236308
  /**
   * @return the state of parent review review secondary result
   */
  public String getParentSecResultValueString() {
    CDRResultParameter parentParam = getParentParam();
    if ((null != parentParam) && (null != parentParam.getCustomSecondaryResult())) {
      return parentParam.getCustomSecondaryResult();
    }
    return "";
  }

  /**
   * @return ParentResultValueString
   */
  public String getParentCompResultValStr() {
    CDRResultParameter parentParam = getParentParam();
    if ((null != parentParam) && (null != parentParam.getCompliResult())) {
      return parentParam.getCompliResult();
    }
    return "";
  }

  /**
   * @return ParentResultValueString
   */
  public String getParentScoreValueString() {
    CDRResultParameter parentParam = getParentParam();
    if (null != parentParam) {
      return parentParam.getScoreExtDescription();
    }
    return "";
  }

  /**
   * @return score + description of score
   */
  public String getScoreExtDescription() {
    return getScore().getScoreDisplayExt(getDataProvider());
  }

  /**
   * @return description of score
   */
  public String getScoreDescription() {
    return getScore().getDescription(getDataProvider());
  }

  /**
   * @return the map with param id as key and CDRResParamSecondary as value
   */
  public Map<Long, CDRResParamSecondary> getSecondaryResParams() {
    if (CommonUtils.isNullOrEmpty(this.seconndaryParamMap)) {
      this.seconndaryParamMap = new ConcurrentHashMap<>();
      Set<TRvwParametersSecondary> tRvwParametersSecondaries =
          getEntityProvider().getDbCDRResParameter(getID()).getTRvwParametersSecondaries();
      if (tRvwParametersSecondaries != null) {
        for (TRvwParametersSecondary tRvwParametersSecondary : tRvwParametersSecondaries) {
          CDRResParamSecondary secondaryResParam =
              getDataCache().getAllSecRvwPrams().get(tRvwParametersSecondary.getSecRvwParamId());
          if (secondaryResParam == null) {
            secondaryResParam = new CDRResParamSecondary(getDataProvider(), tRvwParametersSecondary.getSecRvwParamId());
          }
          this.seconndaryParamMap.put(tRvwParametersSecondary.getSecRvwParamId(), secondaryResParam);
        }
      }
    }
    return this.seconndaryParamMap;
  }

  /**
   * @return String Shape Alaysis Review result
   */
  public String getSrResult() {
    if (null != getEntityProvider().getDbCDRResParameter(getID()).getSrResult()) {
      SR_RESULT srResultEnum =
          CDRConstants.SR_RESULT.getType(getEntityProvider().getDbCDRResParameter(getID()).getSrResult());
      return srResultEnum.getUiType();
    }
    return null;
  }

  /**
   * @return String Shape Analysis Review Error details
   */
  public String getSrErrorDetails() {
    return getEntityProvider().getDbCDRResParameter(getID()).getSrErrorDetails();
  }

  /**
   * @return String Shape analysis review accepted flag
   */
  public String getSrAcceptedFlag() {
    if (null != getEntityProvider().getDbCDRResParameter(getID()).getSrAcceptedFlag()) {
      SR_ACCEPTED_FLAG srAcceptedEnum =
          CDRConstants.SR_ACCEPTED_FLAG.getType(getEntityProvider().getDbCDRResParameter(getID()).getSrAcceptedFlag());
      return srAcceptedEnum.getUiType();
    }
    return null;
  }

  /**
   * @return String Shape analysis review accepted user
   */
  private String getSrAcceptedUser() {
    return getEntityProvider().getDbCDRResParameter(getID()).getSrAcceptedUser();
  }

  /**
   * @return String Display name os shape analysis review accepted user
   */
  private String getSrAcceptedUserDisplayName() {
    String userName = getSrAcceptedUser();
    // Empty string should not be passed to getApicUser method
    if (CommonUtils.isEmptyString(userName)) {
      return "";
    }
    // Using the user NT ID, find the apic user object
    // Return the display name of the user
    ApicUser user = getApicDataProvider().getApicUser(userName);
    return user == null ? userName : user.getDisplayName();
  }

  /**
   * @return Calendar Shape analysis review accepted date
   */
  private Calendar getSrAcceptedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCDRResParameter(getID()).getSrAcceptedDate());
  }
}
