/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;


/**
 * This class is used as Label provider for the RvwQnaireResponseOutlinepage
 *
 * @author svj7cob
 */
public class OutlineQnaireRespTreeViewLabelProvider extends StyledCellLabelProvider implements ILabelProvider {


  /**
   * Constant message for Answer that doesnt allow to finish WP
   */
  private static final String ANSWERED_DOESN_T_ALLOWED_TO_FINISH_WP = "Answered but doesn't allow to finish WP";

  private final QnaireRespEditorDataHandler dataHandler;

  /**
   * @param dataHandler
   */
  public OutlineQnaireRespTreeViewLabelProvider(final QnaireRespEditorDataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }


  /**
   * This method update if the user clicks
   */
  @Override
  public void update(final ViewerCell cell) {
    // Get the element
    final Object element = cell.getElement();
    Image nodeImage = null;
    // checks if the element is the instance of RvwQnaireResponse
    if (element instanceof RvwQnaireResponse) {
      RvwQnaireResponse qResponse = (RvwQnaireResponse) element;
      cell.setText("Questionnaire Response : " + qResponse.getName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.QUESTIONARE_ICON_16X16);
    }
    // checks if the element is the instance of RvwQnaireAnswer
    else if (element instanceof RvwQnaireAnswer) {
      RvwQnaireAnswer rvwAnswer = (RvwQnaireAnswer) element;
      String qnaireName = this.dataHandler.getQuestionNumberWithNameByLanguage(rvwAnswer.getQuestionId());

      // checking if the node is heading
      if (this.dataHandler.checkHeading(rvwAnswer)) {
        String title = qnaireName;
        Set<Long> resultSet = new HashSet<>();
        this.dataHandler.checkAllQuestionAnswered(rvwAnswer, resultSet);

        // Green tick if all questions are answered positive
        nodeImage =
            ImageManager.getDecoratedImage(ImageKeys.HEADING_ICON_16X16, ImageKeys.ALL_8X8, IDecoration.BOTTOM_RIGHT);
        qnaireName = title + " (answered, all positive)";
        // Yellow tick if atleast one negative
        if (resultSet.contains(-1L) && !resultSet.contains(2L)) {
          nodeImage = ImageManager.getDecoratedImage(ImageKeys.HEADING_ICON_16X16, ImageKeys.ALL_YELLOW_8X8,
              IDecoration.BOTTOM_RIGHT);
          qnaireName = title + " (answered, atleast one negative)";
        }

        // No indication if atleast one question is not answered
        else if (resultSet.contains(2L)) {
          nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.HEADING_ICON_16X16);
          qnaireName = this.dataHandler.getQuestionNumberWithNameByLanguage(rvwAnswer.getQuestionId());
        }
      }
      else {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.QUESTION_ICON_16X16);

        String rvwAnswerResult = rvwAnswer.getResult();
        nodeImage = setNodeImageBasedOnSelResult(nodeImage, rvwAnswer, rvwAnswerResult);
      }
      cell.setText(qnaireName);
    }
    // set the image
    cell.setImage(nodeImage);
    // update the parent update method
    super.update(cell);
  }


  /**
   * @param nodeImage
   * @param rvwAnswer
   * @param rvwAnswerResult
   * @return
   */
  private Image setNodeImageBasedOnSelResult(Image nodeImage, final RvwQnaireAnswer rvwAnswer,
      final String rvwAnswerResult) {
    if (!this.dataHandler.checkResultIsMandatory(rvwAnswer.getQuestionId())) {
      if (this.dataHandler.checkMandatoryItemsFilled(rvwAnswer)) {
        ImageKeys icon = CommonUtils.isEqual((CDRConstants.QS_ASSESMENT_TYPE.NEGATIVE).getDbType(), rvwAnswerResult)
            ? ImageKeys.YELLOW_MARK_8X8 : isAnsNeutral(rvwAnswerResult);
        nodeImage = ImageManager.getDecoratedImage(ImageKeys.QUESTION_ICON_16X16, icon, IDecoration.BOTTOM_RIGHT);
      }
    }
    else {
      if ((CDRConstants.QS_ASSESMENT_TYPE.POSITIVE).getDbType().equals(rvwAnswerResult)) {
        nodeImage = ImageManager.getDecoratedImage(ImageKeys.QUESTION_ICON_16X16, ImageKeys.QR_GREEN_MARK_8X8,
            IDecoration.BOTTOM_RIGHT);
      }
      else if ((CDRConstants.QS_ASSESMENT_TYPE.NEUTRAL).getDbType().equals(rvwAnswerResult)) {
        nodeImage = ImageManager.getDecoratedImage(ImageKeys.QUESTION_ICON_16X16, ImageKeys.GREY_MARK_8X8,
            IDecoration.BOTTOM_RIGHT);
      }
      else if ((CDRConstants.QS_ASSESMENT_TYPE.NEGATIVE).getDbType().equals(rvwAnswerResult)) {
        nodeImage = ImageManager.getDecoratedImage(ImageKeys.QUESTION_ICON_16X16, ImageKeys.YELLOW_MARK_8X8,
            IDecoration.BOTTOM_RIGHT);
      }
    }
    return nodeImage;
  }


  /**
   * @param rvwAnswerResult
   * @return
   */
  private ImageKeys isAnsNeutral(final String rvwAnswerResult) {
    return CommonUtils.isEqual((CDRConstants.QS_ASSESMENT_TYPE.NEUTRAL).getDbType(), rvwAnswerResult)
        ? ImageKeys.GREY_MARK_8X8 : ImageKeys.QR_GREEN_MARK_8X8;
  }


  /**
   * Not applicable
   */
  @Override
  public String getText(final Object element) {
    return "";
  }

  /**
   * Not applicable
   */
  @Override
  public Image getImage(final Object element) {
    return null;
  }

  /**
   * gets the tool tip text
   */
  @Override
  public String getToolTipText(final Object element) {
    String tooltip = "";
    // checking the node is the IBasicObject
    if (element instanceof RvwQnaireResponse) {
      // gets the tooltip
      tooltip = this.dataHandler.getQnaireRespVersName();
    }
    else if (element instanceof RvwQnaireAnswer) {
      RvwQnaireAnswer rvwAnswer = (RvwQnaireAnswer) element;
      StringBuilder toolTipSb = new StringBuilder("Name : ");
      toolTipSb.append(this.dataHandler.getNameByLanguage(rvwAnswer.getQuestionId()));

      if (this.dataHandler.checkHeading(rvwAnswer)) {
        Set<Long> resultSet = new HashSet<>();
        this.dataHandler.checkAllQuestionAnswered(rvwAnswer, resultSet);
        if (!resultSet.contains(2L)) {
          toolTipSb.append("\n");
          toolTipSb.append("Status : ");
          toolTipSb.append("All questions of this heading have been answered");
        }
      }
      else {
        toolTipSb.append("\n");
        toolTipSb.append("Status : ");
        constructStatusMessage(rvwAnswer, toolTipSb);
      }
      tooltip = toolTipSb.toString();
    }
    return tooltip;
  }


  /**
   * @param rvwAnswer
   * @param toolTipSb
   */
  private void constructStatusMessage(RvwQnaireAnswer rvwAnswer, StringBuilder toolTipSb) {
    if (!this.dataHandler.checkResultIsMandatory(rvwAnswer.getQuestionId())) {
      // if the result is not relevent then check has to be done
      // to validate whether all other mandatory fields are filled
      if (this.dataHandler.checkMandatoryItemsFilled(rvwAnswer)) {
        // check is done to update the message if the input answer is doent allowed to finish WP
        if (!rvwAnswer.isResultAlwFinishWPFlag()) {
          toolTipSb.append(ANSWERED_DOESN_T_ALLOWED_TO_FINISH_WP);
        }else {
          toolTipSb.append("Answered Positive");
        }
      }
      else {
        // incomplete is added if the mandatory items are not filled
        toolTipSb.append("Not Answered");
      }
    }
    else {
      String rvwAnswerResult = rvwAnswer.getResult();
      // check is done to update the message if the input answer is doent allowed to finish WP
      if (!rvwAnswer.isResultAlwFinishWPFlag()) {
        toolTipSb.append(ANSWERED_DOESN_T_ALLOWED_TO_FINISH_WP);
      }else {
        setTooltipStatus(toolTipSb, rvwAnswerResult);
      }

    }
  }


  /**
   * @param toolTipSb
   * @param rvwAnswerResult
   */
  private void setTooltipStatus(StringBuilder toolTipSb, String rvwAnswerResult) {
    if ((CDRConstants.QS_ASSESMENT_TYPE.POSITIVE).getDbType().equals(rvwAnswerResult)) {
      toolTipSb.append("Answered Positive");
    }
    else if ((CDRConstants.QS_ASSESMENT_TYPE.NEUTRAL).getDbType().equals(rvwAnswerResult)) {
      toolTipSb.append("Answered Neutral");
    }
    else if ((CDRConstants.QS_ASSESMENT_TYPE.NEGATIVE).getDbType().equals(rvwAnswerResult)) {
      toolTipSb.append("Answered Negative");
    }
    else {
      toolTipSb.append("Not Answered");
    }
  }
}
