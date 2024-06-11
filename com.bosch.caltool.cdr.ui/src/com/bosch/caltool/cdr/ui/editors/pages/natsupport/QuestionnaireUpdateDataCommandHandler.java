/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.layer.event.CellVisualChangeEvent;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireDefBO;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QUESTION_CONFIG_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionConfig;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionUpdationData;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * @author DMO5COB
 */
public class QuestionnaireUpdateDataCommandHandler extends AbstractLayerCommandHandler<UpdateDataCommand> {

  /**
   *
   */
  private static final int RESULT_COL_INDEX = 11;
  /**
   * Date col index
   */
  private static final int DATE_COL_INDEX = 10;
  /**
   * Responsible col index
   */
  private static final int RESPONSIBLE_COL_INDEX = 9;
  /**
   * Measure col index
   */
  private static final int MEASURE_COL_INDEX = 8;
  /**
   * Open point col index
   */
  private static final int OPEN_PNT_COL_INDEX = 7;
  /**
   * Remark col index
   */
  private static final int REMARK_COL_INDEX = 6;
  /**
   * Link col index
   */
  private static final int LINK_COL_INDEX = 5;
  /**
   * Series col index
   */
  private static final int SERIES_COL_INDEX = 4;
  /**
   * Measurement col index
   */
  private static final int MEASUREMENT_COL_INDEX = 3;
  private final CustomFilterGridLayer<Question> questionsFilterGridLayer;
  private final QnaireDefBO qnaireDefBo;

  /**
   * @param questionsFilterGridLayer CustomFilterGridLayer<Question> instance
   * @param qnaireDefBo QnaireDefBo
   */
  public QuestionnaireUpdateDataCommandHandler(final CustomFilterGridLayer<Question> questionsFilterGridLayer,
      final QnaireDefBO qnaireDefBo) {
    this.questionsFilterGridLayer = questionsFilterGridLayer;
    this.qnaireDefBo = qnaireDefBo;
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


      final IDataProvider dataProvider = this.questionsFilterGridLayer.getBodyDataProvider();
      Object oldValue = dataProvider.getDataValue(columnPosition, rowPosition);
      Object newValue = command.getNewValue();

      if (((oldValue != null) && !CommonUtils.isEmptyString(oldValue.toString())) ? !oldValue.equals(newValue)
          : ((null != newValue) && !CommonUtils.isEmptyString(newValue.toString()))) {
        dataProvider.setDataValue(columnPosition, rowPosition, newValue);
        this.questionsFilterGridLayer
            .fireLayerEvent(new CellVisualChangeEvent(this.questionsFilterGridLayer, columnPosition, rowPosition));


        Object rowObject = this.questionsFilterGridLayer.getBodyDataProvider().getRowObject(rowPosition);
        if (rowObject instanceof Question) {

          Question selectedQues = (Question) rowObject;
          if (!(this.qnaireDefBo.isWorkingSet())) {
            CDMLogger.getInstance().error(CdrUIConstants.WORKING_SET_MSG, Activator.PLUGIN_ID);
          }
          else {
            if (!this.qnaireDefBo.isModifiable()) {
              CDMLogger.getInstance().error(IMessageConstants.INSUFFICIENT_PRIVILEDGE_MSG, Activator.PLUGIN_ID);
            }
            else {
              QuestionConfig questionConfig =
                  this.qnaireDefBo.getQnaireDefModel().getQuestionConfigMap().get(selectedQues.getId());
              if (columnPosition == MEASUREMENT_COL_INDEX) {
                questionConfig.setMeasurement(QUESTION_CONFIG_TYPE.getDbType(newValue.toString()).getDbType());
              }
              else if (columnPosition == SERIES_COL_INDEX) {
                questionConfig.setSeries(QUESTION_CONFIG_TYPE.getDbType(newValue.toString()).getDbType());
              }
              else if (columnPosition == LINK_COL_INDEX) {
                questionConfig.setLink(QUESTION_CONFIG_TYPE.getDbType(newValue.toString()).getDbType());
              }
              else if (columnPosition == REMARK_COL_INDEX) {
                questionConfig.setRemark(QUESTION_CONFIG_TYPE.getDbType(newValue.toString()).getDbType());
              }
              else if (columnPosition == OPEN_PNT_COL_INDEX) {
                questionConfig.setOpenPoints(QUESTION_CONFIG_TYPE.getDbType(newValue.toString()).getDbType());
              }
              else if (columnPosition == MEASURE_COL_INDEX) {
                questionConfig.setMeasure(QUESTION_CONFIG_TYPE.getDbType(newValue.toString()).getDbType());
              }
              else if (columnPosition == RESPONSIBLE_COL_INDEX) {
                questionConfig.setResponsible(QUESTION_CONFIG_TYPE.getDbType(newValue.toString()).getDbType());
              }
              else if (columnPosition == DATE_COL_INDEX) {
                questionConfig.setCompletionDate(QUESTION_CONFIG_TYPE.getDbType(newValue.toString()).getDbType());
              }
              else if (columnPosition == RESULT_COL_INDEX) {
                questionConfig.setResult(QUESTION_CONFIG_TYPE.getDbType(newValue.toString()).getDbType());
              }
              QuestionUpdationData updateModel = new QuestionUpdationData();
              updateModel.setQuestion(selectedQues);
              updateModel.setQuestionConfig(questionConfig);
              this.qnaireDefBo.updateQuestion(updateModel);
            }
          }
        }
      }
      return true;
    }
    catch (UnsupportedOperationException e) {
      CDMLogger.getInstance().errorDialog("Failed to update value to: " + command.getNewValue(), e,
          Activator.PLUGIN_ID);

      return false;
    }
  }
}
