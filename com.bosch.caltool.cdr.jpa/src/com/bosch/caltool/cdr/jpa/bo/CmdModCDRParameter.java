/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.ParameterClass;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * ICDM-473 This class is to modify the review rules & properties of parameter
 *
 * @author mkl2cob
 */
public class CmdModCDRParameter extends AbstractCDRCommand {

  /**
   * Entity ID for setting user details
   */
  private static final String PARAM_ENTITY_ID = "PARAM_ENTITY_ID";

  /**
   * the DB Parameter to be modified
   */

  private final Long modifiedDbParamID;

  /**
   * old and new code words
   */

  private boolean oldCodeWord;
  private boolean newCodeWord;

  /**
   * old and new bitwise rule
   */
  private boolean oldBitWise;
  private boolean newBitWise;


  /**
   * old and new parameter class types
   */

  private ParameterClass oldParamClass;
  private ParameterClass newParamClass;


  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Old hint value ICdm-1055
   */
  private String oldHint;

  /**
   * new Hint Value ICDM-1055
   */
  private String newHint;

  /**
   * Constructor
   *
   * @param dataProvider ApicDataProvider
   * @param cdrParam CDRFuncParameter
   */
  public CmdModCDRParameter(final CDRDataProvider dataProvider, final CDRFuncParameter cdrParam) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.UPDATE;
    this.modifiedDbParamID = cdrParam.getID();
    setParamProperties(cdrParam);
  }

  /**
   * This method sets the old & new values of param
   *
   * @param cdrParam CDRFuncParameter
   */
  private void setParamProperties(final CDRFuncParameter cdrParam) {
    this.oldCodeWord = cdrParam.isCodeWord();
    this.newCodeWord = this.oldCodeWord;

    this.oldParamClass = cdrParam.getpClass();
    this.newParamClass = this.oldParamClass;
    // Set the Hint Value
    this.oldHint = cdrParam.getParamHint();
    this.newHint = this.oldHint;

    this.oldBitWise = cdrParam.isBitWise();
    this.newBitWise = this.oldBitWise;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    // not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    // not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeUpdateCommand() throws CommandException {
    // DB instance for parameter

    final TParameter modifiedparam = getEntityProvider().getDbFunctionParameter(this.modifiedDbParamID);
    // Icdm-510 Dcn for the Function Parametres
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.modifiedDbParamID, TParameter.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(
        getDataProvider().getFunctionParameter(modifiedparam.getName(), modifiedparam.getPtype()).getObjectDetails());
    // Check for any parallel changes
    validateStaleData(modifiedparam);
    // Update modified data
    setNewParameterFields(modifiedparam);
    getChangedData().put(this.modifiedDbParamID, chdata);
    // Update modified data
    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, modifiedparam, PARAM_ENTITY_ID);
  }

  /**
   * This method updates the database with the new parameter values
   *
   * @param modifiedparam TParameter
   */
  private void setNewParameterFields(final TParameter modifiedparam) {

    if (this.newParamClass == null) {
      modifiedparam.setPclass(null);
    }
    else {
      modifiedparam.setPclass(this.newParamClass.getShortText());
    }
    if (this.newCodeWord) {
      modifiedparam.setIscodeword(ApicConstants.YES);
    }
    else {
      modifiedparam.setIscodeword(ApicConstants.CODE_NO);
    }
    if (this.newBitWise) {
      modifiedparam.setIsbitwise(ApicConstants.YES);
    }
    else {
      modifiedparam.setIsbitwise(ApicConstants.CODE_NO);
    }
    // Set new Hint Value
    modifiedparam.setHint(this.newHint);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoUpdateCommand() throws CommandException {
    // DB instance for parameter
    final TParameter modifiedparam = getEntityProvider().getDbFunctionParameter(this.modifiedDbParamID);
    // Icdm-474 Dcn for the Function Parametres
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.modifiedDbParamID, TParameter.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(
        getDataProvider().getFunctionParameter(modifiedparam.getName(), modifiedparam.getPtype()).getObjectDetails());
    // Check for any parallel changes
    validateStaleData(modifiedparam);
    // Update modified data
    setOldParameterFields(modifiedparam);
    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, modifiedparam, PARAM_ENTITY_ID);
    getChangedData().put(this.modifiedDbParamID, chdata);
  }

  /**
   * This method updates the db with the old parameter values
   *
   * @param modifiedparam TParameter
   */
  private void setOldParameterFields(final TParameter modifiedparam) {
    if (this.oldParamClass == null) {
      modifiedparam.setPclass(null);
    }
    else {
      modifiedparam.setPclass(this.oldParamClass.getShortText());
    }
    if (this.oldCodeWord) {
      modifiedparam.setIscodeword(ApicConstants.YES);
    }
    else {
      modifiedparam.setIscodeword(ApicConstants.CODE_NO);
    }
    if (this.oldBitWise) {
      modifiedparam.setIsbitwise(ApicConstants.YES);
    }
    else {
      modifiedparam.setIsbitwise(ApicConstants.CODE_NO);
    }
    if (this.oldHint == null) {
      modifiedparam.setHint(null);
    }
    else {
      modifiedparam.setHint(this.oldHint);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    // not applicable

  }

  /**
   * {@inheritDoc} returns true if the class or isCodeWord of the parameter is changed
   */
  @Override
  protected final boolean dataChanged() {
    return (this.oldParamClass != this.newParamClass) || (this.oldCodeWord != this.newCodeWord) ||
        (this.oldBitWise != this.newBitWise) || (!CommonUtils.isEqual(this.oldHint, this.newHint));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getString() {
    return super.getString("", getPrimaryObjectIdentifier());
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
        caseCmdIns(detailsList);
        break;
      case UPDATE:
        caseCmdUpd(detailsList);
        break;
      case DELETE: // no details section necessary in case of delete (parent row is sufficient in transansions view)
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
    String oldVal = "";
    String newVal = "";
    if (null != this.oldParamClass) {
      oldVal = this.oldParamClass.getText();
    }
    if (null != this.newParamClass) {
      newVal = this.newParamClass.getText();
    }
    // Transaction Summary for Hint Column.
    addTransactionSummaryDetails(detailsList, this.oldHint, this.newHint, "Hint");
    addTransactionSummaryDetails(detailsList, oldVal, newVal, "Class");
    // Pmd changes made
    addTransactionSummaryDetails(detailsList, String.valueOf(this.oldCodeWord), String.valueOf(this.newCodeWord),
        "Codeword");
    addTransactionSummaryDetails(detailsList, String.valueOf(this.oldBitWise), String.valueOf(this.newBitWise),
        "BitWise");
  }

  /**
   * @param detailsList
   */
  private void caseCmdIns(final SortedSet<TransactionSummaryDetails> detailsList) {
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
  protected void doPostCommit() {
    // not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final Long getPrimaryObjectID() {
    return this.modifiedDbParamID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getPrimaryObjectType() {
    return "Parameter";
  }

  /**
   * @param newCodeWord the newCodeWord to set
   */
  public final void setCodeWord(final boolean newCodeWord) {
    this.newCodeWord = newCodeWord;
  }

  /**
   * @param newParamClass the newParamClass to set
   */
  public final void setParamClass(final ParameterClass newParamClass) {
    this.newParamClass = newParamClass;
  }

  /**
   * @param newHint the newParamHint to Set Icdm-1055
   */
  public final void setParamHint(final String newHint) {
    this.newHint = newHint;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    String objectIdentifier = "";
    // Pmd changes made
    if (this.commandMode == COMMAND_MODE.UPDATE) {
      final TParameter modifiedParam = getEntityProvider().getDbFunctionParameter(this.modifiedDbParamID);
      if (modifiedParam != null) {
        if (getDataProvider().getApicDataProvider().getLanguage().equals(Language.ENGLISH)) {
          objectIdentifier = modifiedParam.getName();
        }
        else {
          objectIdentifier = modifiedParam.getLongnameGer();
        }
      }
    }
    else {
      objectIdentifier = " INVALID!";
    }

    return objectIdentifier;
  }

  /**
   * @param selection
   */
  public void setBitWise(final boolean newBitWise) {
    this.newBitWise = newBitWise;

  }


}
