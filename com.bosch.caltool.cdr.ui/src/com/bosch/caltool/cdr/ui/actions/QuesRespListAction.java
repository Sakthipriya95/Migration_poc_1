/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.dialogs.QnaireRespListDialog;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;

/**
 * @author mkl2cob
 */
public class QuesRespListAction extends Action {

  /**
   * display name of action
   */
  private static final String QNAIRE_LIST = "Questionnaire Responses";

  /**
   * ReviewResultClientBO
   */
  private final ReviewResultClientBO resultData;

  /**
   * @param reviewResultParamListPage ReviewResultParamListPage
   */
  public QuesRespListAction(final ReviewResultParamListPage reviewResultParamListPage) {
    this.resultData = reviewResultParamListPage.getResultData();
    setProperties();
  }


  /**
   * set properties
   */
  private void setProperties() {
    ImageDescriptor imageDesc;
    // if result is locked then display the Unlock Image
    imageDesc = ImageManager.getImageDescriptor(ImageKeys.QUESTIONARE_ICON_16X16);
    setText(QNAIRE_LIST);
    // Always set enabled true.
    setEnabled(true);
    setImageDescriptor(imageDesc);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    QnaireRespListDialog qnaireRespListDialog = new QnaireRespListDialog(Display.getCurrent().getActiveShell(),
        this.resultData.getResponse().getQnaireDataForRvwSet(), this.resultData.getResultBo(), false, true);
    qnaireRespListDialog.open();
  }
}
