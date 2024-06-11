package com.bosch.caltool.usecase.ui.views;

import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;
import com.bosch.caltool.usecase.ui.editors.UseCaseEditorInput;

/*
 * UseCaseOutlinePageCreator
 */
/**
 * @author adn1cob
 */
public class UseCaseOutlinePageCreator implements IPageCreator {

  private final UseCaseEditorInput editorInput;

  /**
   * @param editorInput editorInput
   * @param dataHandler
   */
  public UseCaseOutlinePageCreator(final UseCaseEditorInput editorInput) {
    this.editorInput = editorInput;

  }

  /**
   * Creates a OUTLINE page for Usecase
   */
  @Override
  public AbstractPage createPage() {
    return new UseCaseOutlinePage(this.editorInput.getOutlineDataHandler());
  }

  /**
   * Returns the editor input
   *
   * @return UseCaseEditorInput
   */
  public UseCaseEditorInput getEditorInput() {
    return this.editorInput;
  }

}