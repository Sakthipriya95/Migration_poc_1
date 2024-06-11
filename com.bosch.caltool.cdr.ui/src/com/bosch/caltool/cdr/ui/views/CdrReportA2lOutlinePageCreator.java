package com.bosch.caltool.cdr.ui.views;

import com.bosch.caltool.cdr.ui.editors.CdrReportEditorInput;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;


/**
 * Cdr a2l Page creator
 *
 * @author rgo7cob
 */
public class CdrReportA2lOutlinePageCreator implements IPageCreator {

  /**
   * editorInput field
   */
  private final CdrReportEditorInput editorInput;

  /**
   * @param editorInput editorInput
   */
  public CdrReportA2lOutlinePageCreator(final CdrReportEditorInput editorInput) {
    this.editorInput = editorInput;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AbstractPage createPage() {
    return new CdrReportOutlinePage(this.editorInput);
  }

}