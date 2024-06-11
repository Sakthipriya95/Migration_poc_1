/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.datamodel.core.IBasicObject;
import com.bosch.caltool.icdm.client.bo.framework.BasicObjectUtils;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireDefBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;


/**
 * Label provider for outline view of questionnaire
 *
 * @author dmo5cob
 */
public class OutlineQuestionnareTreeViewLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

  private final QnaireDefBO qnaireDefBo;

  /**
   * @param qnaireDefBo QnaireDefBo
   */
  public OutlineQuestionnareTreeViewLabelProvider(final QnaireDefBO qnaireDefBo) {
    this.qnaireDefBo = qnaireDefBo;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void update(final ViewerCell cell) {
    // Get the element
    final Object element = cell.getElement();
    Image nodeImage = null;
    // check if selected node is questionnaire version
    if (element instanceof QuestionnaireVersion) {
      // set questionnaire name
      cell.setText(this.qnaireDefBo.getNameForPart());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.QUESTION_RESPONSE_ICON_16X16);
    }
    // check if selected node is question
    else if (element instanceof Question) {
      Question question = (Question) element;
      // set the question name
      cell.setText(this.qnaireDefBo.getQuestionNumberWithName(question.getId()));
      // set the icon based on heading or not
      if (question.getHeadingFlag()) {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.HEADING_ICON_16X16);
      }
      else {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.QUESTION_ICON_16X16);
      }
    }

    // set the image
    cell.setImage(nodeImage);
    super.update(cell);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Image getImage(final Object element) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText(final Object element) {
    String tooltip = null;
    // get the object tooltip
    if (element instanceof IBasicObject) {
      tooltip = BasicObjectUtils.getToolTip((IBasicObject) element);
    }
    return tooltip;
  }
}
