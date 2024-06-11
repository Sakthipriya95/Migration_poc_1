/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.caldatautils.CalDataComparism.CompareResult;
import com.bosch.calcomp.caldatautils.CompareQuantized;
import com.bosch.calcomp.caldatautils.ItemsToCompare.AvailableItemsForComparison;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.caltool.apic.jpa.bo.CommandErrorCodes;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParametersSecondary;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResultsSecondary;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.monicareportparser.data.ParameterInfo;
import com.bosch.caltool.monicareportparser.data.ParameterInfo.MONICA_REVIEW_STATUS;
import com.bosch.checkssd.reports.reportMessage.ReportMessages;
import com.bosch.checkssd.reports.reportmodel.FormtdRptValModel;
import com.bosch.checkssd.reports.reportmodel.ReportModel;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * CmdModCDRResultParam - Command handles all db operations (INSERT, UPDATE) on result params
 */
// ICDM-560
public class CmdModRvwSecondaryParam extends AbstractCDRCommand {

  /**
   * Error message
   */
  private static final String SAV_CALDATA_ERR = "Error saving Cal data for parameter";
  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);
  /**
   * Parent review result of this review param entry
   */
  private final CDRSecondaryResult secondaryResult;


  /**
   * The created result function object
   */
  private final CDRResultParameter resParam;

  private CDRResParamSecondary secondaryResParam;


  /**
   * @return the resParam
   */
  public CDRResultParameter getResParam() {
    return this.resParam;
  }


  /**
   * Check SSD result
   */
  private CheckSSDResultParam checkSSDResultParam;


  private int changeBitNum;

  private CDRResParamSecondary parentSecondaryResParam;
  private CDRRule rule;


  /**
   * Review parameter entity ID
   */
  private static final String ENTITY_ID = "RVW_PARAMETER_SECONDARY";

  private ParameterInfo monicaParamInfo;

  /**
   * Characteristic object from A2l file
   */
  private Characteristic a2lCharacteristic;


  /**
   * @param dataProvider dataProvider
   * @param secondaryResult secondaryResult
   * @param checkSSDResultParam checkSSDResultParam
   * @param cdrResultParameter cdrResultParameter
   * @param parentSecondaryResParam parentSecondaryResParam
   * @param deltaReviewType deltaReviewType
   * @param rule rule
   */
  public CmdModRvwSecondaryParam(final CDRDataProvider dataProvider, final CDRSecondaryResult secondaryResult,
      final CheckSSDResultParam checkSSDResultParam, final CDRResultParameter cdrResultParameter,
      final CDRResParamSecondary parentSecondaryResParam, final CDRRule rule) {
    super(dataProvider);
    this.secondaryResult = secondaryResult;
    this.resParam = cdrResultParameter;
    this.parentSecondaryResParam = parentSecondaryResParam;
    this.checkSSDResultParam = checkSSDResultParam;
    this.rule = rule;
    this.commandMode = COMMAND_MODE.INSERT;
  }


  /**
   * @param dataProvider dataProvider
   * @param secondaryResult secondaryResult
   * @param cdrResultParameter cdrResultParameter
   * @param checkSSDResultParam checkSSDResultParam
   * @param rule rule
   */
  public CmdModRvwSecondaryParam(final CDRDataProvider dataProvider, final CDRSecondaryResult secondaryResult,
      final CDRResultParameter cdrResultParameter, final CheckSSDResultParam checkSSDResultParam, final CDRRule rule) {
    super(dataProvider);
    this.secondaryResult = secondaryResult;
    this.resParam = cdrResultParameter;
    this.checkSSDResultParam = checkSSDResultParam;
    this.rule = rule;
    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * CmdModCDRResultParam, Constructor for UPDATE
   *
   * @param dataProvider the data provider
   * @param cdrResParam CDRResultParameter to be updated
   * @param delete delete flag
   * @param secondaryResult secondary Result.
   */
  public CmdModRvwSecondaryParam(final CDRDataProvider dataProvider, final CDRResultParameter cdrResParam,
      final CDRSecondaryResult secondaryResult, final CDRResParamSecondary secondaryResParam) {
    super(dataProvider);
    this.secondaryResParam = secondaryResParam;
    this.secondaryResult = secondaryResult;
    this.resParam = cdrResParam;
    this.commandMode = COMMAND_MODE.DELETE;

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {

    final TRvwParametersSecondary dbSeconRvwParam = new TRvwParametersSecondary();

    /* Set Rules */
    setRules(dbSeconRvwParam);

    String isbitwise = this.resParam.getFunctionParameter().isBitWise() ? ApicConstants.YES : ApicConstants.CODE_NO;
    dbSeconRvwParam.setIsbitwise(isbitwise);
    // bitwise flag
    if (this.parentSecondaryResParam != null) {
      if (!compareObjects(this.parentSecondaryResParam.isBitWise(), isbitwise)) {
        this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.BITWISE_FLAG.setFlag(this.changeBitNum);
      }
    }


    // iCDM-577
    /* Set reference value */
    setRefValue(dbSeconRvwParam);

    /* Set Ready for series */
    setReviewMethod(dbSeconRvwParam);

    /* Result info from checkSSD */
    String result = setResultFlag();
    /* Result - OK, Not-ok.. */
    dbSeconRvwParam.setResult(result);
    setLabelRevIds(dbSeconRvwParam);


    // Set Reference value unit
    dbSeconRvwParam.setRefUnit(this.rule == null ? null : this.rule.getUnit());


    /* Changebit */
    if (this.parentSecondaryResParam != null) {
      dbSeconRvwParam.setChangeFlag(this.changeBitNum);
    }


    // ICDM-639
    String matchRefFlag = this.rule != null ? (this.rule.isDcm2ssd() ? ApicConstants.YES : ApicConstants.CODE_NO) : "";
    dbSeconRvwParam.setMatchRefFlag(matchRefFlag);

    /* Set user informations */
    setUserDetails(COMMAND_MODE.INSERT, dbSeconRvwParam, ENTITY_ID);

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(dbSeconRvwParam);

    this.secondaryResParam = new CDRResParamSecondary(getDataProvider(), dbSeconRvwParam.getSecRvwParamId());

    /* Set other references */
    TRvwResultsSecondary dbResSecondary = getEntityProvider().getDbResSecondary(this.secondaryResult.getID());
    dbSeconRvwParam.setTRvwResultsSecondary(dbResSecondary);
    Set<TRvwParametersSecondary> tRvwParametersSecondaries = dbResSecondary.getTRvwParametersSecondaries();
    if (null == tRvwParametersSecondaries) {
      tRvwParametersSecondaries = new HashSet<>();
    }
    tRvwParametersSecondaries.add(dbSeconRvwParam);
    dbResSecondary.setTRvwParametersSecondaries(tRvwParametersSecondaries);

    TRvwParameter dbCDRRvwParameter = getEntityProvider().getDbCDRResParameter(this.resParam.getID());
    dbSeconRvwParam.setTRvwParameter(dbCDRRvwParameter);
    Set<TRvwParametersSecondary> tRvwParametersSecondaries2 = dbCDRRvwParameter.getTRvwParametersSecondaries();
    if (null == tRvwParametersSecondaries2) {
      tRvwParametersSecondaries2 = new HashSet<>();
    }
    tRvwParametersSecondaries2.add(dbSeconRvwParam);
    dbCDRRvwParameter.setTRvwParametersSecondaries(tRvwParametersSecondaries2);

    // adding the CDRResultParameter to parent CDRResultFuntion and CDRResult in entity
    // check if not null
    setValuesToDataModel(dbSeconRvwParam);

    getChangedData().put(dbSeconRvwParam.getSecRvwParamId(), new ChangedData(ChangeType.INSERT,
        dbSeconRvwParam.getSecRvwParamId(), TRvwParameter.class, DisplayEventSource.COMMAND));
  }


  /**
   * @param dbSeconRvwParam
   */
  private void setValuesToDataModel(final TRvwParametersSecondary dbSeconRvwParam) {
    Set<TRvwParametersSecondary> paramSet =
        getEntityProvider().getDbResSecondary(this.secondaryResult.getID()).getTRvwParametersSecondaries();
    if (paramSet == null) {
      paramSet = new HashSet<>();
    }
    paramSet.add(dbSeconRvwParam);

    Set<TRvwParametersSecondary> secondaryParams =
        getEntityProvider().getDbCDRResParameter(this.resParam.getID()).getTRvwParametersSecondaries();


    if (secondaryParams == null) {
      secondaryParams = new HashSet<>();
    }
    secondaryParams.add(dbSeconRvwParam);


    // adding the CDRResultParameter to the secondary result and the review params
    Map<Long, CDRResParamSecondary> reviewResultParametersMap = this.secondaryResult.getSecondaryResParams();
    reviewResultParametersMap.put(this.secondaryResParam.getID(), this.secondaryResParam);

    this.resParam.getSecondaryResParams().put(this.secondaryResParam.getID(), this.secondaryResParam);
  }


  /**
   * @param dbRvwParam
   */
  private void setLabelRevIds(final TRvwParametersSecondary dbRvwParam) {
    if (null != this.rule) {
      dbRvwParam.setLabObjId(this.rule.getRuleId() == null ? null : this.rule.getRuleId().longValue());
      dbRvwParam.setRevId(this.rule.getRevId() == null ? null : this.rule.getRevId().longValue());
    }


  }

  /**
   * @param result
   * @return
   */
  private String setResultFlag() {
    // by default, not reviwed
    CDRConstants.RESULT_FLAG result = CDRConstants.RESULT_FLAG.NOT_REVIEWED;
    // Set proper result from CheckSSD
    if (this.checkSSDResultParam != null) {
      ReportModel reportModel = this.checkSSDResultParam.getReportModel();
      if (reportModel != null) {
        result = setResultFromReport(reportModel);
      }
      else if ((this.rule != null) && this.rule.isRuleComplete() && CommonUtils.isNull(this.monicaParamInfo)) {
        result = CDRConstants.RESULT_FLAG.NOT_OK;
      }
    }
    // Rule is complete and the check ssd is not available
    else if ((this.rule != null) && this.rule.isRuleComplete() && CommonUtils.isNull(this.monicaParamInfo)) {
      result = CDRConstants.RESULT_FLAG.NOT_OK;
    }
    // check or MoniCa type review
    else if (CommonUtils.isNotNull(this.monicaParamInfo)) {
      MONICA_REVIEW_STATUS enumValue = this.monicaParamInfo.getReviewStatus(this.monicaParamInfo.getStatus());
      result = getCdrResultFromMonica(enumValue);
    }
    setResultChangeBit(result.getUiType());


    return result.getDbType();
  }


  /**
   * @param reportModel
   * @return
   */
  private CDRConstants.RESULT_FLAG setResultFromReport(final ReportModel reportModel) {
    CDRConstants.RESULT_FLAG result;
    if ((reportModel.getMessType() == ReportMessages.LOG_MSG) ||
        (reportModel.getMessType() == ReportMessages.WRN_MSG)) {
      result = CDRConstants.RESULT_FLAG.OK;
    }
    else {
      if (reportModel instanceof FormtdRptValModel) {
        FormtdRptValModel formtdRptValModel = (FormtdRptValModel) reportModel;
        if ((formtdRptValModel.getValGE() != null) && !formtdRptValModel.getValGE().isEmpty()) {
          result = CDRConstants.RESULT_FLAG.HIGH;
        }
        else if ((formtdRptValModel.getValLE() != null) && !formtdRptValModel.getValLE().isEmpty()) {
          result = CDRConstants.RESULT_FLAG.LOW;
        }
        else {
          result = CDRConstants.RESULT_FLAG.NOT_OK;
        }
      }
      else {
        result = CDRConstants.RESULT_FLAG.NOT_OK;
      }
    }
    return result;
  }

  /**
   * @param enumValue
   * @return
   */
  private CDRConstants.RESULT_FLAG getCdrResultFromMonica(final MONICA_REVIEW_STATUS enumValue) {
    CDRConstants.RESULT_FLAG result;
    if ((enumValue == MONICA_REVIEW_STATUS.FALIED) || (enumValue == MONICA_REVIEW_STATUS.NOT_OK)) {
      result = CDRConstants.RESULT_FLAG.NOT_OK;
    }
    else if (enumValue == MONICA_REVIEW_STATUS.HIGH) {
      result = CDRConstants.RESULT_FLAG.HIGH;
    }
    else if (enumValue == MONICA_REVIEW_STATUS.LOW) {
      result = CDRConstants.RESULT_FLAG.LOW;
    }
    else if (enumValue == MONICA_REVIEW_STATUS.OK) {
      result = CDRConstants.RESULT_FLAG.OK;
    }
    else {
      result = CDRConstants.RESULT_FLAG.NOT_REVIEWED;
    }
    return result;
  }

  /**
   * @param string
   */
  private void setResultChangeBit(final String uiType) {
    // set the change bit for result flag in delta
    if (this.parentSecondaryResParam != null) {
      if (!compareObjects(this.parentSecondaryResParam.getCommonResult(), uiType)) {
        this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.RESULT.setFlag(this.changeBitNum);
      }
    }

  }


  /**
   * @param dbRvwParam
   */
  private void setReviewMethod(final TRvwParametersSecondary dbRvwParam) {
    // Ready for series to be selected based on Rule defined for this parameter
    if ((this.rule != null) && (this.rule.getReviewMethod() != null)) {
      dbRvwParam.setRvwMethod(this.rule.getReviewMethod());
    }
  }


  /**
   * Set the reference value
   *
   * @param dbRvwParam
   * @throws CommandException
   */
  private void setRefValue(final TRvwParametersSecondary dbRvwParam) throws CommandException {
    dbRvwParam.setRefValue(convertCalDataToZippedByteArr(getRefValue()));
    setRefValChangeMarker();
  }

  /**
   * Icdm-945 Ref value Change Marker
   */
  private void setRefValChangeMarker() {
    if (this.parentSecondaryResParam != null) {
      CalData parentRefValueObject = this.parentSecondaryResParam.getRefValueObj();
      if ((parentRefValueObject != null) && ((this.rule != null) && (getRefValue() != null))) {
        if (!compareObjects(parentRefValueObject.getCalDataPhy(), getRefValue().getCalDataPhy())) {
          this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.REF_VALUE.setFlag(this.changeBitNum);
        }
      }
      else if (!compareObjects(parentRefValueObject, getRefValue())) {
        this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.REF_VALUE.setFlag(this.changeBitNum);
      }
    }
  }

  /**
   * Convert the caldata object to a zipped byte array
   *
   * @param data caldata object
   * @return zipped byte array
   * @throws CommandException on any error during conversion
   */
  // ICDM-2069
  private byte[] convertCalDataToZippedByteArr(final CalData data) throws CommandException {
    if (data == null) {
      return null;
    }
    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStm = new ObjectOutputStream(out);) {

      outputStm.writeObject(data);
      ConcurrentMap<String, byte[]> dataMap = new ConcurrentHashMap<>();
      dataMap.put(CalDataUtil.KEY_CALDATA_ZIP, out.toByteArray());
      return ZipUtils.createZip(dataMap);

    }
    catch (IOException e) {
      getDataProvider().getLogger().error(SAV_CALDATA_ERR, e);
      throw new CommandException(SAV_CALDATA_ERR, CommandErrorCodes.ERR_SERIALIZE_DATA, e);
    }
  }


  /**
   * Get reference value object from RULE for all types (VALUE, CURVE, MAP..)
   *
   * @return CalData object
   */
  private CalData getRefValue() {
    final CDRRule cdrRule = this.rule;

    CDRFuncParameter functionParameter = this.resParam.getFunctionParameter();
    if (cdrRule != null) {
      // VALUE type label
      if ((ApicUtil.compare(functionParameter.getType(), ApicConstants.VALUE_TEXT) == 0) &&
          (cdrRule.getRefValue() != null)) { // ICDM-1253
        // Prepare DCM string for this decimal string and convert to CalData
        return cdrRule.dcmToCalData(CalDataUtil.createDCMStringForNumber(functionParameter.getName(),
            this.rule.getUnit(), cdrRule.getRefValue().toString()), functionParameter.getName());
      } // For Complex type labels, get DCM string
      else if (cdrRule.getRefValueCalData() != null) {
        return cdrRule.getRefValueCalData();
      }
    }
    // default case
    return null;
  }


  /**
   * @param dbRvwParam
   */
  private void setRules(final TRvwParametersSecondary dbRvwParam) {
    BigDecimal lowerLimit =
        this.rule == null ? null : this.rule.getLowerLimit() == null ? null : this.rule.getLowerLimit();
    BigDecimal upperLimit =
        this.rule == null ? null : this.rule.getUpperLimit() == null ? null : this.rule.getUpperLimit();
    String bitWiseLimit = this.rule == null ? null : this.rule.getBitWiseRule();

    dbRvwParam.setLowerLimit(lowerLimit);
    dbRvwParam.setUpperLimit(upperLimit);
    dbRvwParam.setBitwiseLimit(bitWiseLimit);
    if ((this.parentSecondaryResParam != null) && (this.parentSecondaryResParam.getResultParameter() != null)) {
      setChangBit(lowerLimit, upperLimit, bitWiseLimit);
    }
  }


  /**
   * @param lowerLimit
   * @param upperLimit
   * @param bitWiseLimit
   */
  private void setChangBit(final BigDecimal lowerLimit, final BigDecimal upperLimit, final String bitWiseLimit) {
    if ((this.parentSecondaryResParam.getResultParameter().getCheckedValueObj() != null) &&
        (this.parentSecondaryResParam.getResultParameter().getCheckedValueObj().getCalDataPhy() != null)) {
      // Lower limit
      if (!compareObjects(this.parentSecondaryResParam.getLowerLimit(), lowerLimit)) {
        this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.LOWER_LIMT.setFlag(this.changeBitNum);
      }
      // Upper limit
      if (!compareObjects(this.parentSecondaryResParam.getUpperLimit(), upperLimit)) {
        this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.UPPER_LIMIT.setFlag(this.changeBitNum);
      }
      // bitwise rule
      if (this.resParam.getFunctionParameter().isBitWise() &&
          !compareObjects(this.parentSecondaryResParam.getBitwiseLimit(), bitWiseLimit)) {
        this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.BITWISE_LIMIT.setFlag(this.changeBitNum);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // Not implemented now
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    performDelete();
  }

  /**
   * perform delete of entity. also Remove associations.
   */
  private void performDelete() {
    final TRvwParametersSecondary dbSeconRvwParam =
        getEntityProvider().getDbParamSecondary(this.secondaryResParam.getID());

    if ((this.secondaryResult != null) &&
        (getEntityProvider().getDbResSecondary(this.secondaryResult.getID()) != null) &&
        (getEntityProvider().getDbResSecondary(this.secondaryResult.getID()).getTRvwParametersSecondaries() != null)) {
      getEntityProvider().getDbResSecondary(this.secondaryResult.getID()).getTRvwParametersSecondaries()
          .remove(dbSeconRvwParam);
      getDataCache().getCDRResSecondary(this.secondaryResult.getID()).getSecondaryResParams()
          .remove(this.secondaryResParam.getID());
    }

    if ((this.resParam != null) && (getEntityProvider().getDbCDRResParameter(this.secondaryResParam.getID()) != null) &&
        (getEntityProvider().getDbCDRResParameter(this.secondaryResult.getID())
            .getTRvwParametersSecondaries() != null)) {
      getEntityProvider().getDbCDRResParameter(this.resParam.getID()).getTRvwParametersSecondaries()
          .remove(dbSeconRvwParam);
      this.resParam.getSecondaryResParams().remove(this.secondaryResParam.getID());
    }
    getEntityProvider().deleteEntity(dbSeconRvwParam);

    getDataCache().getAllSecRvwPrams().remove(this.secondaryResParam.getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    performDelete();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // Not implemented

  }

  /**
   * {@inheritDoc}
   */

  @Override
  public TransactionSummary getTransactionSummary() {
    // ICDM-723
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      case INSERT:
        getSummaryForInsert(detailsList);
        break;
      default:
        // Do nothing
        break;
    }
    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
  }


  /**
   * @param detailsList
   */
  private void getSummaryForInsert(final SortedSet<TransactionSummaryDetails> detailsList) {
    final TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    // Not Applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", getPrimaryObjectIdentifier());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc} return result id to which the parameter is mapped
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.resParam == null ? null : this.resParam.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "CDR Result Parameter";
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    if (this.commandMode == COMMAND_MODE.INSERT) {
      rollbackForInsert();
    }

    if (this.commandMode == COMMAND_MODE.DELETE) {
      rollBacKForDelete();
    }

  }


  /**
   *
   */
  private void rollBacKForDelete() {
    if ((this.resParam != null) && (this.resParam.getSecondaryResParams() != null)) {
      this.resParam.getSecondaryResParams().put(this.secondaryResParam.getID(), this.secondaryResParam);
    }
    if ((this.secondaryResult != null) && (this.secondaryResult.getSecondaryResParams() != null)) {
      this.secondaryResult.getSecondaryResParams().put(this.secondaryResParam.getID(), this.secondaryResParam);
    }
    getDataCache().getAllSecRvwPrams().put(this.secondaryResParam.getID(), this.secondaryResParam);
  }


  /**
   *
   */
  private void rollbackForInsert() {
    if ((this.resParam != null) && (this.resParam.getSecondaryResParams() != null)) {
      this.resParam.getSecondaryResParams().remove(this.secondaryResParam.getID());
    }
    if ((this.secondaryResult != null) && (this.secondaryResult.getSecondaryResParams() != null)) {
      this.secondaryResult.getSecondaryResParams().remove(this.secondaryResParam.getID());
    }
    getDataCache().getAllSecRvwPrams().remove(this.secondaryResParam.getID());
  }

  /** Method used to custom compare two objects */
  private boolean compareObjects(final Object obj1, final Object obj2) {
    return CommonUtils.isEqual(obj1, obj2);
  }

  /** Need a overloaded compareobjects method since CalDataPhy doesnt override equals(Object) method */
  private boolean compareObjects(final CalDataPhy parentCalDataPhy, final CalDataPhy childCalDataPhy) {
    // ICDM-1785
    CompareResult compareResult =
        CompareQuantized.isEqualForAllItemsExceptExcluded(this.a2lCharacteristic, parentCalDataPhy, childCalDataPhy,
            AvailableItemsForComparison.A2L_UNIT, AvailableItemsForComparison.CAL_DATA_UNITS,
            AvailableItemsForComparison.NO_OF_CHARACTERS, AvailableItemsForComparison.TEXT_BIT);

    return compareResult == CompareResult.EQUAL;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return "Secondary Result Param";
  }


  /**
   * @param a2lcharMap2
   */
  public void setCharacteristicsObj(final Characteristic a2lcharObj) {
    this.a2lCharacteristic = a2lcharObj;

  }
}
