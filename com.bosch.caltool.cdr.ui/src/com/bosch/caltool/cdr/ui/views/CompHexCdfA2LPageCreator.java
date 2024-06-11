/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views;

import com.bosch.caltool.cdr.ui.editors.CompHexWithCDFxEditorInput;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;

/**
 * @author mkl2cob
 */
public class CompHexCdfA2LPageCreator implements IPageCreator {

  /**
   * editorInput field
   */
  private final CompHexWithCDFxEditorInput editorInput;

  /**
   * @param editorInput editorInput
   */
  public CompHexCdfA2LPageCreator(final CompHexWithCDFxEditorInput editorInput) {
    this.editorInput = editorInput;
  }

  /**
   * create the page {@inheritDoc}
   */
  public AbstractPage createPage() {
    return new CompHexCdfOutlinePage(this.editorInput);
  }
}
