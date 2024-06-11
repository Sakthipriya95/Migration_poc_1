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
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgData;
import com.bosch.caltool.icdm.model.comppkg.CompPkgType;

// ICDM-528
/**
 * Properties view source class to display properties of CompPkg
 *
 * @author mkl2cob
 */
public final class CompPkgPropertiesViewSource extends AbstractDataObjectPropertySource<CompPackage> {

  /**
   * Property and key
   */
  private static final String PROP_RULE_ATTRIBUTES = "Rule Attributes";
  /**
   * Property and key
   */
  private static final String PROP_SSD_PARAMETER_CLASS = "SSD Parameter Class";
  /**
   * Property and key
   */
  private static final String PROP_SSD_USE_CASE = "SSD Use Case";
  /**
   * Property and key
   */
  private static final String PROP_SSD_NODE_ID = "SSD Node ID";
  /**
   * Property and key (iCDM-1326)
   */
  private static final String PROP_SSD_VERS_NODE_ID = "SSD Version Node ID";
  /**
   * Property and key
   */
  private static final String PROP_TYPE = "Type";
  /**
   * Property and key
   */
  private static final String PROP_BC_COUNT = "Number of Base Components";

  /**
   * Constructor
   *
   * @param compPkg instance of CompPkg for which properties have to be displayed
   */
  public CompPkgPropertiesViewSource(final CompPackage compPkg) {
    super(compPkg);
  }

  /**
   * @return ComponentPackageEditorInput
   */
  public CompPkgData getCompPkgData() {
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
          return editorInput.getCompPkgData();
        }
        catch (PartInitException e) {
          CDMLogger.getInstance().warn(e.getMessage(), e);
        }
      }
    }
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Object getStrPropertyValue(final String objID) {

    CompPackage compPkg = getDataObject();

    Object retValue = null;

    switch (objID) {
      case PROP_BC_COUNT:
        CompPkgData compPkgData = getCompPkgData();

        retValue = (compPkgData == null) || (compPkgData.getBcSet() == null) ? 0 : compPkgData.getBcSet().size();

        break;

      // ICDM-1229
      case PROP_TYPE:
        retValue = CompPkgType.getType(compPkg.getCompPkgType());
        break;

      case PROP_RULE_ATTRIBUTES:
        // TODO
        break;

      case PROP_SSD_NODE_ID:
        retValue = compPkg.getSsdNodeId();
        break;

      case PROP_SSD_VERS_NODE_ID:
        retValue = compPkg.getSsdNodeId();
        break;

      case PROP_SSD_USE_CASE:
        retValue = compPkg.getSsdUsecase();
        break;

      case PROP_SSD_PARAMETER_CLASS:
        retValue = compPkg.getSsdParamClass();
        break;


      default:
        retValue = "";
    }
    return retValue;

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected String[] getDescFields() {

    // Normal component packages
    String[] propDescFields = new String[] { PROP_BC_COUNT, PROP_TYPE };

    // NE Type component packages
    if (getDataObject().getCompPkgType().equals(CompPkgType.NE.getLiteral())) {
      propDescFields = new String[] {
          PROP_BC_COUNT,
          PROP_TYPE,
          PROP_RULE_ATTRIBUTES,
          PROP_SSD_NODE_ID,
          PROP_SSD_VERS_NODE_ID,
          PROP_SSD_USE_CASE,
          PROP_SSD_PARAMETER_CLASS };
    }

    return propDescFields;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getTitle() {
    return "COMPPKG : " + getDataObject().getName();
  }

}
