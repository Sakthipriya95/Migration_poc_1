/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.ui.views.providers;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.comppkg.ui.editors.pages.ComponentDetailsPage;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.propertysource.AbstractDataObjectPropertySource;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;

// ICDM-528
/**
 * Properties view source class to display properties of CompPkgBcFc
 *
 * @author mkl2cob
 */
public class CompPkgBCFCPropertiesViewSource extends AbstractDataObjectPropertySource<CompPkgFc> {

  /**
   * Constructor
   *
   * @param compPkgBcFc instance of CompPkgBcFc for which properties have to be displayed
   */
  public CompPkgBCFCPropertiesViewSource(final CompPkgFc compPkgBcFc) {
    super(compPkgBcFc);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public final Object getStrPropertyValue(final String objID) {
    // No specific implementation required. Only common fields are sufficient.
    return null;
  }

  /**
   * @return ComponentPackageEditorInput
   */
  public ComponentDetailsPage getEditorPage() {
    final IWorkbenchWindow[] wbWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
    if (wbWindows.length == 0) {
      return null;
    }
    final IEditorReference[] editorRefArr = wbWindows[0].getActivePage().getEditorReferences();
    for (IEditorReference editor : editorRefArr) {

      if (editor.getPart(false) instanceof AbstractFormEditor) {
        for (Object obj : ((AbstractFormEditor) editor.getPart(false)).getPages()) {
          if (obj instanceof ComponentDetailsPage) {
            ComponentDetailsPage compDetPage = (ComponentDetailsPage) obj;
            CompPkgBc selection = compDetPage.getSelCompPkgBC();
            if ((selection != null) &&
                CommonUtils.isEqual(compDetPage.getSelCompPkgBC().getId(), getDataObject().getCompBcId())) {
              return compDetPage;
            }
          }
        }

      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getTitle() {
    ComponentDetailsPage compDetPage = getEditorPage();
    if (CommonUtils.isNotNull(compDetPage) && CommonUtils.isNotNull(compDetPage.getSelCompPkgBC())) {
      return "FUNC : " + getDataObject().getName() + " - BC : " + compDetPage.getSelCompPkgBC().getBcName() +
          " - COMPPKG : " + compDetPage.getSelectedCmpPkg().getName();

    }
    return null;

  }
}
