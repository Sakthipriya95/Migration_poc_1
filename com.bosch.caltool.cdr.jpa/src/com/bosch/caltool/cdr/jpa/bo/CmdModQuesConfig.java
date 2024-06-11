/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionConfig;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QUESTION_CONFIG_TYPE;


/**
 * ICDM-1954 This command is to change the question configurations
 *
 * @author mkl2cob
 */
public class CmdModQuesConfig extends AbstractCDRCommand {

  private static final String QUES_CONFIG_ENTITY_ID = "QUES_CONFIG_ENTITY_ID";
  /**
   * link configuration
   */
  QUESTION_CONFIG_TYPE oldLinkConfig;
  /**
   * result configuration
   */
  QUESTION_CONFIG_TYPE oldResultConfig;
  /**
   * measurement configuration
   */
  QUESTION_CONFIG_TYPE oldMeasurementConfig;
  /**
   * series configuration
   */
  QUESTION_CONFIG_TYPE oldSeriesConfig;
  /**
   * open points configuration
   */
  QUESTION_CONFIG_TYPE oldOpnPointsConfig;
  /**
   * remark configuration
   */
  QUESTION_CONFIG_TYPE oldRemarkConfig;
  /**
   * measure configuration
   */
  QUESTION_CONFIG_TYPE oldMeausreConfig;
  /**
   * responsible configuration
   */
  QUESTION_CONFIG_TYPE oldResponsibleConfig;
  /**
   * completion date configuration
   */
  QUESTION_CONFIG_TYPE oldCompletionDateConfig;
  /**
   * link configuration
   */
  QUESTION_CONFIG_TYPE linkConfig;
  /**
   * result configuration
   */
  QUESTION_CONFIG_TYPE resultConfig;
  /**
   * measurement configuration
   */
  QUESTION_CONFIG_TYPE measurementConfig;
  /**
   * series configuration
   */
  QUESTION_CONFIG_TYPE seriesConfig;
  /**
   * open points configuration
   */
  QUESTION_CONFIG_TYPE opnPointsConfig;
  /**
   * remark configuration
   */
  QUESTION_CONFIG_TYPE remarkConfig;
  // ICDM-2191
  /**
   * measure configuration
   */
  QUESTION_CONFIG_TYPE measureConfig;
  /**
   * responsible configuration
   */
  QUESTION_CONFIG_TYPE responsibleConfig;
  /**
   * completion date configuration
   */
  QUESTION_CONFIG_TYPE completionDateConfig;

  /**
   * TQuestion
   */
  private TQuestion dbQues;


  /**
   * QuestionConfig
   */
  private QuestionConfig quesConfig;

  private final TransactionSummary summaryData = new TransactionSummary(this);
  private TQuestionConfig dbQuesConfig;

  /**
   * Construtor for INSERT
   *
   * @param dataProvider CDRDataProvider
   */
  protected CmdModQuesConfig(final CDRDataProvider dataProvider) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * Construtor for UPDATE
   *
   * @param dataProvider CDRDataProvider
   * @param quesConfig QuestionConfig
   */
  public CmdModQuesConfig(final CDRDataProvider dataProvider, final QuestionConfig quesConfig) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.UPDATE;
    this.quesConfig = quesConfig;
    if (null != this.quesConfig) {
      setOldValues();
    }
  }


  /**
   * set the old values
   */
  private void setOldValues() {
    this.oldLinkConfig = this.quesConfig.getLink();
    this.oldMeasurementConfig = this.quesConfig.getMeasurement();
    this.oldOpnPointsConfig = this.quesConfig.getOpenPoints();
    this.oldRemarkConfig = this.quesConfig.getRemark();
    this.oldResultConfig = this.quesConfig.getResult();
    this.oldSeriesConfig = this.quesConfig.getSeries();
    this.oldMeausreConfig = this.quesConfig.getMeasure();
    this.oldResponsibleConfig = this.quesConfig.getResponsible();
    this.oldCompletionDateConfig = this.quesConfig.getCompletionDate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    switch (this.commandMode) {
      case INSERT:
      case UPDATE:
      case DELETE:
      default:
        // Do nothing
        break;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    this.dbQuesConfig = new TQuestionConfig();
    // set the config items
    setNewConfig(this.dbQuesConfig);
    this.dbQues.setTQuestionConfig(this.dbQuesConfig);
    this.dbQuesConfig.setTQuestion(this.dbQues);
    getEntityProvider().registerNewEntity(this.dbQuesConfig);
    this.dbQues.setTQuestionConfig(this.dbQuesConfig);
    this.quesConfig = new QuestionConfig(getDataProvider(), this.dbQuesConfig.getQconfigId());
    Question question = getDataCache().getQuestion(this.dbQues.getQId());
    if (question != null) {
      // when heading is changed to question
      question.setQuestionConfig(this.quesConfig);
    }
    setUserDetails(COMMAND_MODE.INSERT, this.dbQuesConfig, QUES_CONFIG_ENTITY_ID);
    getChangedData().put(this.dbQues.getQId(),
        new ChangedData(ChangeType.INSERT, this.dbQues.getQId(), TQuestionConfig.class, DisplayEventSource.COMMAND));

  }

  /**
   * @param dbQuesConfig TQuestionConfig
   */
  private void setNewConfig(final TQuestionConfig dbQuesConfig) {
    if (null != this.resultConfig) {
      dbQuesConfig.setResult(this.resultConfig.getDbType());
    }
    if (null != this.linkConfig) {
      dbQuesConfig.setLink(this.linkConfig.getDbType());
    }
    if (null != this.measurementConfig) {
      dbQuesConfig.setMeasurement(this.measurementConfig.getDbType());
    }
    if (null != this.remarkConfig) {
      dbQuesConfig.setRemark(this.remarkConfig.getDbType());
    }
    if (null != this.seriesConfig) {
      dbQuesConfig.setSeries(this.seriesConfig.getDbType());
    }
    if (null != this.opnPointsConfig) {
      dbQuesConfig.setOpenPoints(this.opnPointsConfig.getDbType());
    }
    // ICDM-2191
    if (null != this.measureConfig) {
      dbQuesConfig.setMeasure(this.measureConfig.getDbType());
    }
    if (null != this.responsibleConfig) {
      dbQuesConfig.setResponsible(this.responsibleConfig.getDbType());
    }
    if (null != this.completionDateConfig) {
      dbQuesConfig.setCompletionDate(this.completionDateConfig.getDbType());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {

    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.quesConfig.getID(), TQuestionConfig.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.quesConfig.getObjectDetails());
    this.dbQuesConfig = getEntityProvider().getDbQuestionConfig(this.quesConfig.getID());
    validateStaleData(this.dbQuesConfig);
    // update the name and description
    setNewConfig(this.dbQuesConfig);
    setUserDetails(COMMAND_MODE.UPDATE, this.dbQuesConfig, QUES_CONFIG_ENTITY_ID);
    getChangedData().put(this.quesConfig.getID(), chdata);


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return isStringChanged(this.oldLinkConfig.getDbType(),
        this.linkConfig == null ? this.oldLinkConfig.getDbType() : this.linkConfig.getDbType()) ||
        isStringChanged(this.oldMeasurementConfig.getDbType(),
            this.measurementConfig == null ? this.oldMeasurementConfig.getDbType()
                : this.measurementConfig.getDbType()) ||
        isStringChanged(this.oldOpnPointsConfig.getDbType(),
            this.opnPointsConfig == null ? this.oldOpnPointsConfig.getDbType() : this.opnPointsConfig.getDbType()) ||
        isStringChanged(this.oldRemarkConfig.getDbType(),
            this.remarkConfig == null ? this.oldRemarkConfig.getDbType() : this.remarkConfig.getDbType()) ||
        isStringChanged(this.oldResultConfig.getDbType(),
            this.resultConfig == null ? this.oldResultConfig.getDbType() : this.resultConfig.getDbType()) ||
        isStringChanged(this.oldSeriesConfig.getDbType(),
            this.seriesConfig == null ? this.oldSeriesConfig.getDbType() : this.seriesConfig.getDbType()) ||
        isStringChanged(this.oldMeausreConfig.getDbType(),
            this.measureConfig == null ? this.oldMeausreConfig.getDbType() : this.measureConfig.getDbType()) ||
        isStringChanged(this.oldResponsibleConfig.getDbType(),
            this.responsibleConfig == null ? this.oldResponsibleConfig.getDbType()
                : this.responsibleConfig.getDbType()) ||
        isStringChanged(this.oldCompletionDateConfig.getDbType(), this.completionDateConfig == null
            ? this.oldCompletionDateConfig.getDbType() : this.completionDateConfig.getDbType());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", "Question Configuration" + getPrimaryObjectIdentifier());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {

    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();

    switch (this.commandMode) {
      case INSERT:
        caseCmsIns(detailsList);
        break;
      case UPDATE:
        caseCmdUpd(detailsList);
        break;
      case DELETE:
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
  private void caseCmdUpd(final SortedSet<TransactionSummaryDetails> detailsList) {
    addTransactionSummaryDetails(detailsList, this.oldLinkConfig.getUiType(),
        this.linkConfig == null ? this.oldLinkConfig.getUiType() : this.linkConfig.getUiType(), "Link");
    addTransactionSummaryDetails(detailsList, this.oldResultConfig.getUiType(),
        this.resultConfig == null ? this.oldResultConfig.getUiType() : this.resultConfig.getUiType(), "Result");
    addTransactionSummaryDetails(detailsList, this.oldMeasurementConfig.getUiType(),
        this.measurementConfig == null ? this.oldMeasurementConfig.getUiType() : this.measurementConfig.getUiType(),
        "Measurement");
    addTransactionSummaryDetails(detailsList, this.oldSeriesConfig.getUiType(),
        this.seriesConfig == null ? this.oldSeriesConfig.getUiType() : this.seriesConfig.getUiType(), "Series");
    addTransactionSummaryDetails(detailsList, this.oldOpnPointsConfig.getUiType(),
        this.opnPointsConfig == null ? this.oldOpnPointsConfig.getUiType() : this.opnPointsConfig.getUiType(),
        "Open points");
    addTransactionSummaryDetails(detailsList, this.oldRemarkConfig.getUiType(),
        this.remarkConfig == null ? this.oldRemarkConfig.getUiType() : this.remarkConfig.getUiType(), "Remarks");
    addTransactionSummaryDetails(detailsList, this.oldMeausreConfig.getUiType(),
        this.measureConfig == null ? this.oldMeausreConfig.getUiType() : this.measureConfig.getUiType(), "Measure");
    addTransactionSummaryDetails(detailsList, this.oldResponsibleConfig.getUiType(),
        this.responsibleConfig == null ? this.oldResponsibleConfig.getUiType() : this.responsibleConfig.getUiType(),
        "Responsible");
    addTransactionSummaryDetails(detailsList, this.oldCompletionDateConfig.getUiType(),
        this.completionDateConfig == null ? this.oldCompletionDateConfig.getUiType()
            : this.completionDateConfig.getUiType(),
        "Completion Date");
  }

  /**
   * @param detailsList
   */
  private void caseCmsIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    final TransactionSummaryDetails details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    switch (this.commandMode) {
      case INSERT:
      case UPDATE:
      case DELETE:
      default:
        // Do nothing
        break;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.dbQuesConfig == null ? null : this.dbQuesConfig.getQconfigId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Question Configuration";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.dbQues == null ? (this.quesConfig == null ? null : this.quesConfig.getQuestion().getName())
        : this.dbQues.getQNameEng();

  }


  /**
   * @param linkConfig the linkConfig to set
   */
  public void setLinkConfig(final QUESTION_CONFIG_TYPE linkConfig) {
    this.linkConfig = linkConfig;
  }


  /**
   * @param resultConfig the resultConfig to set
   */
  public void setResultConfig(final QUESTION_CONFIG_TYPE resultConfig) {
    this.resultConfig = resultConfig;
  }


  /**
   * @param measurementConfig the measurementConfig to set
   */
  public void setMeasurementConfig(final QUESTION_CONFIG_TYPE measurementConfig) {
    this.measurementConfig = measurementConfig;
  }


  /**
   * @param seriesConfig the seriesConfig to set
   */
  public void setSeriesConfig(final QUESTION_CONFIG_TYPE seriesConfig) {
    this.seriesConfig = seriesConfig;
  }


  /**
   * @param opnPointsConfig the opnPointsConfig to set
   */
  public void setOpnPointsConfig(final QUESTION_CONFIG_TYPE opnPointsConfig) {
    this.opnPointsConfig = opnPointsConfig;
  }


  /**
   * @param remarkConfig the remarkConfig to set
   */
  public void setRemarkConfig(final QUESTION_CONFIG_TYPE remarkConfig) {
    this.remarkConfig = remarkConfig;
  }

  /**
   * @param dbQues the dbQues to set
   */
  public void setDbQues(final TQuestion dbQues) {
    this.dbQues = dbQues;
  }


  /**
   * @param measureConfig the measureConfig to set
   */
  public void setMeasureConfig(final QUESTION_CONFIG_TYPE measureConfig) {
    this.measureConfig = measureConfig;
  }


  /**
   * @param responsibleConfig the responsibleConfig to set
   */
  public void setResponsibleConfig(final QUESTION_CONFIG_TYPE responsibleConfig) {
    this.responsibleConfig = responsibleConfig;
  }


  /**
   * @param completionDateConfig the completionDateConfig to set
   */
  public void setCompletionDateConfig(final QUESTION_CONFIG_TYPE completionDateConfig) {
    this.completionDateConfig = completionDateConfig;
  }

}
