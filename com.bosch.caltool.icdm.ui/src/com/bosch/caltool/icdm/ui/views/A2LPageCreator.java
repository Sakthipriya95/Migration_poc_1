package com.bosch.caltool.icdm.ui.views;

import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditorInput;

public class A2LPageCreator implements IPageCreator {

  private final A2LContentsEditorInput editorInput;

  public A2LPageCreator(final A2LContentsEditorInput editorInput) {
    this.editorInput = editorInput;
  }

  public AbstractPage createPage() {
    return new A2LOutlinePage(this.editorInput);
  }

}