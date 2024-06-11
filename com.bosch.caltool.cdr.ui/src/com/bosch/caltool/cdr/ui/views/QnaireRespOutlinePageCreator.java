/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views;

import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;


/**
 * @author svj7cob
 */
// iCDM-1991
public class QnaireRespOutlinePageCreator implements IPageCreator {

  /**
   * the editor input for the questionnaire response
   */
  private final QnaireRespEditorDataHandler dataHandler;

  /**
   * the outline page for the questionnaire response
   */
  private QnaireRespOutlinePage questResponseOutlinePage;

  /**
   * @param editorInput the editor input for the questionnaire response
   */
  public QnaireRespOutlinePageCreator(final QnaireRespEditorDataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }


  /**
   * @return the page
   */
  public QnaireRespOutlinePage getQuestResponseOutlinePage() {
    return this.questResponseOutlinePage;
  }


  /**
   * This method createPage after the initialization done
   */
  @Override
  public AbstractPage createPage() {
    this.questResponseOutlinePage = new QnaireRespOutlinePage(this.dataHandler);
    return this.questResponseOutlinePage;
  }

}
