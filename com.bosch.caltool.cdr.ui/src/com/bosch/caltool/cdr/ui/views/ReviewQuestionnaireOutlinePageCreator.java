/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views;

import com.bosch.caltool.cdr.ui.editors.ReviewQuestionaireEditorInput;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;


/**
 * @author dmo5cob
 */
public class ReviewQuestionnaireOutlinePageCreator implements IPageCreator {

  /**
   * Instance of ReviewQuestionaireEditorInput
   */
  private final ReviewQuestionaireEditorInput editorInput;

  /**
   * Constructor
   */
  public ReviewQuestionnaireOutlinePageCreator(final ReviewQuestionaireEditorInput editorInput) {
    this.editorInput = editorInput;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AbstractPage createPage() {
    return new ReviewQuestionnaireOutlinePage(this.editorInput);
  }

}
