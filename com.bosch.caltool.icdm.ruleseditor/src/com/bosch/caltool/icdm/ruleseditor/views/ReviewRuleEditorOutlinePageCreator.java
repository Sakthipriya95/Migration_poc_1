/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.views;

import org.eclipse.jface.viewers.ISelection;

import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;


/**
 * ICDM-2265 outline page creator for review rules editor
 *
 * @author mkl2cob
 */
public class ReviewRuleEditorOutlinePageCreator implements IPageCreator {

  /**
   * ReviewParamEditorInput
   */
  private final ReviewParamEditorInput reviewParamEditorInput;
  /**
   * ReviewRulesOutlinePage
   */
  private ReviewRuleEditorOutlinePage reviewRulesOutlinePage;
  private final ReviewParamEditor reviewParamEditor;

  /**
   * Constructor
   *
   * @param reviewParamEditorInput ReviewParamEditorInput
   * @param reviewParamEditor
   */
  public ReviewRuleEditorOutlinePageCreator(final ReviewParamEditorInput reviewParamEditorInput,
      final ReviewParamEditor reviewParamEditor) {
    this.reviewParamEditorInput = reviewParamEditorInput;
    this.reviewParamEditor = reviewParamEditor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AbstractPage createPage() {
    this.reviewRulesOutlinePage = new ReviewRuleEditorOutlinePage(this.reviewParamEditorInput, this.reviewParamEditor);
    return this.reviewRulesOutlinePage;
  }

  /**
   * @return ISelection
   */
  public ISelection getOutlinePageSelection() {
    // get the selection from outline page
    if ((this.reviewRulesOutlinePage != null) && (this.reviewRulesOutlinePage.getViewer() != null)) {
      return this.reviewRulesOutlinePage.getViewer().getSelection();
    }
    return null;
  }


  /**
   * @return the reviewRulesOutlinePage
   */
  public ReviewRuleEditorOutlinePage getReviewRulesOutlinePage() {
    return this.reviewRulesOutlinePage;
  }


}
