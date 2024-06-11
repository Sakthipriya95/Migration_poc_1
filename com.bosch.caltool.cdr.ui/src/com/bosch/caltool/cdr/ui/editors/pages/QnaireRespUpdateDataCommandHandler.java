/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.CellVisualChangeEvent;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QS_RESULT_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_RESP_RESULT;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_RESP_RESULT_FINISHED;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_RESP_RESULT_NO;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_RESP_RESULT_YES;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_RESP_SERIES_MEASURE;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerDummy;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * Questionnaire response update command handler
 *
 * @author bru2cob
 */
public class QnaireRespUpdateDataCommandHandler extends AbstractLayerCommandHandler<UpdateDataCommand> {

  /**
   * question filter grid layer instance
   */
  private final CustomFilterGridLayer<Object> questionsFilterGridLayer;

  private MessageDialog infoMessageDialog;

  /**
   * series maturity
   */
  private String seriesString;
  /**
   * Measurement
   */
  private String measuremntString;
  /**
   * remarks
   */
  private String remarksStr;
  /**
   * result
   */
  private String resultStr;
  /**
   * selected ans obj
   */
  private RvwQnaireAnswer selectedRvwQnaireAns;
  private final QnaireRespEditorDataHandler dataHandler;

  /**
   * Instantiates a new qnaire resp update data command handler.
   *
   * @param questionsFilterGridLayer the questions filter grid layer
   * @param dataHandler the data handler
   */
  public QnaireRespUpdateDataCommandHandler(final CustomFilterGridLayer<Object> questionsFilterGridLayer,
      final QnaireRespEditorDataHandler dataHandler) {
    this.questionsFilterGridLayer = questionsFilterGridLayer;
    this.dataHandler = dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<UpdateDataCommand> getCommandClass() {
    return UpdateDataCommand.class;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean doCommand(final UpdateDataCommand command) {
    try {
      int rowPosition = command.getRowPosition();

      int columnPosition = command.getColumnPosition();


      // get the data provider
      final IDataProvider dataProvider = this.questionsFilterGridLayer.getBodyDataProvider();
      // get the old and the new values
      Object oldValue = dataProvider.getDataValue(columnPosition, rowPosition);
      Object newValue = command.getNewValue();
      // if both values are not null and not same , execute the command
      if (CommonUtils.isNotNull(oldValue) && !CommonUtils.isEmptyString(oldValue.toString())
          ? !oldValue.equals(newValue)
          : (CommonUtils.isNotNull(newValue) && !CommonUtils.isEmptyString(newValue.toString()))) {
        // set the new value in UI
        dataProvider.setDataValue(columnPosition, rowPosition, newValue);

        // set the new value in db
        Object rowObject = this.questionsFilterGridLayer.getBodyDataProvider().getRowObject(rowPosition);
        if ((rowObject instanceof RvwQnaireAnswer) || (rowObject instanceof RvwQnaireAnswerDummy)) {
          setCommandFields(columnPosition, newValue, rowObject);
        }
      }
      // once new value is set , fire the event
      this.questionsFilterGridLayer
          .fireLayerEvent(new CellVisualChangeEvent(this.questionsFilterGridLayer, columnPosition, rowPosition));
      applyColumnFilter();
      return true;
    }
    catch (UnsupportedOperationException e) {
      CDMLogger.getInstance().errorDialog("Failed to update value to: " + command.getNewValue(), e,
          Activator.PLUGIN_ID);

      return false;
    }
  }

  /**
   * Applies the filter
   */
  private void applyColumnFilter() {
    this.questionsFilterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
    this.questionsFilterGridLayer.getSortableColumnHeaderLayer()
        .fireLayerEvent(new FilterAppliedEvent(this.questionsFilterGridLayer.getSortableColumnHeaderLayer()));
  }

  /**
   * @param columnPosition
   * @param newValue
   * @param rowObject
   */
  private void setCommandFields(final int columnPosition, final Object newValue, final Object rowObject) {
    this.selectedRvwQnaireAns = (RvwQnaireAnswer) rowObject;
    boolean isResultChanged = false;
    // Setting null to avoid data taken over from process since variable defined in global
    this.remarksStr = null;
    this.resultStr = null;
    this.seriesString = null;
    this.measuremntString = null;
    Object value = newValue;
    if (this.dataHandler.isModifiable()) {
      if (value == null) {
        value = "";
      }
      if (columnPosition == ApicUiConstants.COLUMN_INDEX_8) {
        final QS_RESULT_TYPE qsPositiveRes = QS_RESULT_TYPE.getTypeByDbCode(
            this.dataHandler.getQuestion(this.selectedRvwQnaireAns.getQuestionId()).getPositiveResult());
        if (qsPositiveRes == QS_RESULT_TYPE.YES) {
          this.resultStr = QUESTION_RESP_RESULT_YES.getTypeFromUI(value.toString()).getDbType();
        }
        else if (qsPositiveRes == QS_RESULT_TYPE.NO) {
          this.resultStr = QUESTION_RESP_RESULT_NO.getTypeFromUI(value.toString()).getDbType();
        }
        else {
          this.resultStr = QUESTION_RESP_RESULT_FINISHED.getTypeFromUI(value.toString()).getDbType();
        }
      }
      else if (columnPosition == ApicUiConstants.COLUMN_INDEX_7) {
        isResultChanged =
            chechResultFlag(this.dataHandler.isRemarksMandatory(this.selectedRvwQnaireAns), value.toString());
        this.remarksStr = value.toString();
      }
      else if (columnPosition == ApicUiConstants.COLUMN_INDEX_3) {
        isResultChanged =
            chechResultFlag(this.dataHandler.isSeriesMandatory(this.selectedRvwQnaireAns), value.toString());
        this.seriesString = QUESTION_RESP_SERIES_MEASURE.getTypeFromUI(value.toString()).getDbType();
      }
      else if (columnPosition == ApicUiConstants.COLUMN_INDEX_4) {
        isResultChanged =
            chechResultFlag(this.dataHandler.isMeasurementMandatory(this.selectedRvwQnaireAns), value.toString());
        this.measuremntString = QUESTION_RESP_SERIES_MEASURE.getTypeFromUI(value.toString()).getDbType();
      }
      if (isResultChanged) {
        createMsgDialogIfNew(
            "Since all the mandatory field is not set or open points if any are not closed ,the result is reset to 'To be done' from 'Finished'");

      }
      if ((columnPosition != ApicUiConstants.COLUMN_INDEX_8) || ((columnPosition == ApicUiConstants.COLUMN_INDEX_8) &&
          ((QUESTION_RESP_RESULT.getTypeFromUI(value.toString()) != QUESTION_RESP_RESULT.FINISHED) || canSave()))) {
        updateReviewQnaireAnswer();
      }
      else {
        // result cant be set to 'finished' if all mandatory feilds are not set
        createMsgDialogIfNew(
            "To set result to 'Finished' all the mandatory fields should be set and open points if any should be closed");
      }
    }
    else {
      CDMLogger.getInstance().error(IMessageConstants.INSUFFICIENT_PRIVILEDGE_MSG, Activator.PLUGIN_ID);
    }
  }


  /**
   * Creates message dialog for the first time and return back the thread without creating the dialog for the
   * consecutive times
   *
   * @param msg the info message to be displayed
   */
  private void createMsgDialogIfNew(final String msg) {
    if ((null != this.infoMessageDialog) && (null != this.infoMessageDialog.getShell()) &&
        !this.infoMessageDialog.getShell().isDisposed()) {
      return;
    }
    CDMLogger.getInstance().info(msg, Activator.PLUGIN_ID);
    Shell infoShell = new Shell();
    this.infoMessageDialog =
        new MessageDialog(infoShell, "Information", null, msg, MessageDialog.INFORMATION, new String[] { "OK" }, 0);
    this.infoMessageDialog.open();
  }

  /**
   * @param remarksStr
   * @param resultStr
   * @param seriesString
   * @param measuremntString
   */
  private void updateReviewQnaireAnswer() {
    RvwQnaireAnswer cmdRvwQAns = this.selectedRvwQnaireAns.clone();
    //setting qnaire resp version id to RvwQnaireAnswer
    if (CommonUtils.isNull(cmdRvwQAns.getQnaireRespVersId())) {
      cmdRvwQAns.setQnaireRespVersId(this.dataHandler.getQnaireRespModel().getRvwQnrRespVersion().getId());
    }
    if (this.remarksStr != null) {
      cmdRvwQAns.setRemark(this.remarksStr);
    }
    if (this.measuremntString != null) {
      cmdRvwQAns.setMeasurement(this.measuremntString);
    }
    if (this.resultStr != null) {
      cmdRvwQAns.setResult(this.resultStr);
    }
    if (this.seriesString != null) {
      cmdRvwQAns.setSeries(this.seriesString);
    }
    this.dataHandler.updateRvwQnaireAns(cmdRvwQAns);
    this.dataHandler.updateQnaireRespVersStatus();
  }

  /**
   * If a mandatory feild is changed to not defined and if result is finished , it has to be changed to 'to be done'
   *
   * @param isMandatoryField isMandatoryField
   * @param value new value
   * @return
   */
  private boolean chechResultFlag(final boolean isMandatoryField, final String value) {
    boolean isResultChanged = false;
    String dbPositiveAns;
    final QS_RESULT_TYPE qsPositiveRes = QS_RESULT_TYPE
        .getTypeByDbCode(this.dataHandler.getQuestion(this.selectedRvwQnaireAns.getQuestionId()).getPositiveResult());
    if (qsPositiveRes == QS_RESULT_TYPE.YES) {
      dbPositiveAns = QUESTION_RESP_RESULT_YES.YES.getDbType();
    }
    else if (qsPositiveRes == QS_RESULT_TYPE.NO) {
      dbPositiveAns = QUESTION_RESP_RESULT_NO.NO.getDbType();
    }
    else {
      dbPositiveAns = QUESTION_RESP_RESULT_FINISHED.FINISHED.getDbType();
    }

    // null check for result str added, because at the initial condition it would be null
    if (isMandatoryField &&
        (QUESTION_RESP_SERIES_MEASURE.getTypeFromUI(value) == QUESTION_RESP_SERIES_MEASURE.NOT_DEFINED) &&
        (null != this.resultStr) && this.resultStr.equalsIgnoreCase(dbPositiveAns)) {
      this.resultStr = QUESTION_RESP_RESULT.TO_BE_DONE.getDbType();
      isResultChanged = true;
    }
    return isResultChanged;
  }

  /**
   * @return true is all mandatory feilds are validated
   */
  public boolean canSave() {

    return !(isSeriesOrMeasNotValid() || isOpenPointsNotValid() || isLinksOrRemarksNotValid());
  }

  /**
   * @return
   */
  private boolean isLinksOrRemarksNotValid() {
    Set<Link> links = this.dataHandler.getLinks(this.selectedRvwQnaireAns);
    boolean linkNotValid =
        this.dataHandler.isLinkMandatory(this.selectedRvwQnaireAns) && ((links == null) || links.isEmpty());
    boolean remarksNotValid = this.dataHandler.isRemarksMandatory(this.selectedRvwQnaireAns) &&
        CommonUtils.isEmptyString(this.selectedRvwQnaireAns.getRemark());

    return linkNotValid || remarksNotValid;
  }

  /**
   * @return
   */
  private boolean isOpenPointsNotValid() {
    boolean openPointNotValid = false;
    if (!this.dataHandler.getOpenPointsList(this.selectedRvwQnaireAns).isEmpty()) {
      for (RvwQnaireAnswerOpl selOp : this.dataHandler.getOpenPointsList(this.selectedRvwQnaireAns).values()) {
        if (!this.dataHandler.isResultSet(selOp)) {
          openPointNotValid = true;
          break;
        }
      }
    }
    return openPointNotValid;
  }

  /**
   * @return
   */
  private boolean isSeriesOrMeasNotValid() {
    boolean seriesNotValid = this.dataHandler.isSeriesMandatory(this.selectedRvwQnaireAns) &&
        CommonUtils.isEmptyString(this.selectedRvwQnaireAns.getSeries());
    boolean measurementNotValid = this.dataHandler.isMeasurementMandatory(this.selectedRvwQnaireAns) &&
        CommonUtils.isEmptyString(this.selectedRvwQnaireAns.getMeasurement());

    return seriesNotValid || measurementNotValid;
  }
}
