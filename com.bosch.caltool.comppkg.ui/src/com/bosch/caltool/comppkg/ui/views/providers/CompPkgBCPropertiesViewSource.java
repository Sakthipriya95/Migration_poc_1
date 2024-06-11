/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.ui.views.providers;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.comppkg.ui.editors.ComponentPackageEditor;
import com.bosch.caltool.comppkg.ui.editors.ComponentPackageEditorInput;
import com.bosch.caltool.icdm.common.ui.propertysource.AbstractDataObjectPropertySource;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;

// ICDM-528
/**
 * Properties view source class to display properties of CompPkgBc
 *
 * @author mkl2cob
 */
public class CompPkgBCPropertiesViewSource extends AbstractDataObjectPropertySource<CompPkgBc> {

  /**
   * FC Mapping type property and key
   */
  private static final String PROP_FC_MAPPING = "FC Mapping";
  /**
   * Display text indicatiog all fc's are mapped for this bc
   */
  public static final String ALL_FC_MAPPING = "<ALL>";
  /**
   * Display text indicatiog mutiple fc's are mapped for this bc
   */
  public static final String MULTI_FC_MAPPING = "<PARTIAL>";

  /**
   * Constructor
   *
   * @param compPkgBc instance of CompPkgBc for which properties have to be displayed
   */
  public CompPkgBCPropertiesViewSource(final CompPkgBc compPkgBc) {
    super(compPkgBc, PROP_FC_MAPPING);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final Object getStrPropertyValue(final String objID) {
    if (PROP_FC_MAPPING.equals(objID)) {
      String dispStr = ALL_FC_MAPPING;
      if (!getDataObject().getFcList().isEmpty()) {
        dispStr = MULTI_FC_MAPPING;
      }
      return dispStr;
    }

    return null;
  }

  /**
   * @return ComponentPackageEditorInput
   */
  public ComponentPackageEditorInput getEditorInput() {
    final IWorkbenchWindow[] wbWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
    if (wbWindows.length == 0) {
      return null;
    }
    ComponentPackageEditorInput editorInput = null;
    final IEditorReference[] editorRefArr = wbWindows[0].getActivePage().getEditorReferences();
    for (IEditorReference editor : editorRefArr) {
      if (editor.getPart(false) instanceof ComponentPackageEditor) {
        try {
          editorInput = (ComponentPackageEditorInput) editor.getEditorInput();
        }
        catch (PartInitException e) {
          CDMLogger.getInstance().warn(e.getMessage(), e);
        }
        break;
      }
    }
    return editorInput;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getTitle() {

    if (CommonUtils.isNotNull(getEditorInput())) {
      return " BC : " + getDataObject().getBcName() + " - COMPPKG : " + getEditorInput().getSelectedCmpPkg().getName();
    }
    return null;

  }

}
