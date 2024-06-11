/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views;

import com.bosch.caltool.apic.ui.editors.PIDCCompareEditorInput;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;


/**
 * @author bru2cob
 */
public class ComparePIDCOutlinePageCreator implements IPageCreator {

  private final PIDCCompareEditorInput editorInput;

  /**
   * @param editorInput
   */
  public ComparePIDCOutlinePageCreator(final PIDCCompareEditorInput editorInput) {
    this.editorInput = editorInput;
  }

  /**
   * Creates a OUTLINE page for Usecase
   */
  @Override
  public AbstractPage createPage() {

    return new PIDCOutlinePage(this.editorInput.getComparePidcHandler().getCompareObjectsHandlerMap()
        .get(this.editorInput.getCompareObjs().get(0).getId()).getPidcDataHandler().getPidcVersionInfo()
        .getPidcVersion(), null, this.editorInput.getOutlineViewDataHandler(), false);
  }

  /**
   * Returns the editor input
   *
   * @return PIDCCompareEditorInput
   */
  public PIDCCompareEditorInput getEditorInput() {
    return this.editorInput;
  }
}
