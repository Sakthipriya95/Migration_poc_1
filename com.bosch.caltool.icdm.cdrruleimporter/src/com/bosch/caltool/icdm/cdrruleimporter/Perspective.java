package com.bosch.caltool.icdm.cdrruleimporter;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

  public void createInitialLayout(final IPageLayout layout) {
    layout.setEditorAreaVisible(false);

    layout.addStandaloneView("com.bosch.caltool.icdm.cdrruleimporter.views.SampleView", true, IPageLayout.TOP, 0.95f,
        layout.getEditorArea());
  }
}
