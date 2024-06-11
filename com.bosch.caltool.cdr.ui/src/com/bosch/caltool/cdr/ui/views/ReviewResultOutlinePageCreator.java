package com.bosch.caltool.cdr.ui.views;


import org.eclipse.jface.viewers.ISelection;

import com.bosch.caltool.cdr.ui.editors.ReviewResultEditorInput;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;

public class ReviewResultOutlinePageCreator implements IPageCreator {

  private final ReviewResultEditorInput editorInput;

  private ReviewResultOutlinePage reviewResultOutlinePage;

  public ReviewResultOutlinePageCreator(final ReviewResultEditorInput editorInput) {
    this.editorInput = editorInput;
  }

  @Override
  public AbstractPage createPage() {
    this.reviewResultOutlinePage = new ReviewResultOutlinePage(this.editorInput);
    return this.reviewResultOutlinePage;
  }

  /**
   * @return ISelection
   */
  public ISelection getOutlinePageSelection() {
    // get the selection from outline page
    if ((getReviewResultOutlinePage() != null) && (getReviewResultOutlinePage().getViewer() != null)) {
      return getReviewResultOutlinePage().getViewer().getSelection();
    }
    return null;
  }


  /**
   * @return the reviewResultOutlinePage
   */
  public ReviewResultOutlinePage getReviewResultOutlinePage() {
    return this.reviewResultOutlinePage;
  }


  /**
   * @param reviewResultOutlinePage the reviewResultOutlinePage to set
   */
  public void setReviewResultOutlinePage(final ReviewResultOutlinePage reviewResultOutlinePage) {
    this.reviewResultOutlinePage = reviewResultOutlinePage;
  }

}