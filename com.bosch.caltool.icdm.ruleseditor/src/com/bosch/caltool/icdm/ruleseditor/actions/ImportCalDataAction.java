/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.wizards.CalDataFileImpWizardDialog;
import com.bosch.caltool.icdm.common.ui.wizards.CalDataFileImportWizard;
import com.bosch.caltool.icdm.common.ui.wizards.CalDataFileImportWizardData;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;


/**
 * @author dmr1cob
 */
public class ImportCalDataAction extends Action {


  private final ReviewParamEditorInput editorInput;
  private final ParamCollectionDataProvider paramColDataProvider;
  private final ParamCollection importObj;


  /**
   * ReviewParamEditorInput is passed as input so that the data changes in editor are handled in caldata import process
   *
   * @param importObj {@link ParamCollection}
   * @param paramColDataProvider {@link ParamCollectionDataProvider}
   * @param editorInput {@link ReviewParamEditorInput}
   * @param selFuncVersion
   */
  public ImportCalDataAction(final ParamCollection importObj, final ParamCollectionDataProvider paramColDataProvider,
      final ReviewParamEditorInput editorInput) {

    this.editorInput = editorInput;
    this.paramColDataProvider = paramColDataProvider;
    this.importObj = importObj;
    setText("Import Calibration Data");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.DCM_UPLOAD_28X30);
    setImageDescriptor(imageDesc);
  }


  @Override
  public void run() {
    if (this.paramColDataProvider.isModifiable(this.importObj)) {

      if (this.importObj instanceof Function) {
        Function func = (Function) this.importObj;
        if (this.paramColDataProvider.isBigFunction(func) && (this.editorInput.getParamRulesOutput() == null)) {
          CDMLogger.getInstance().errorDialog("Please select a function version and then perform Import process",
              Activator.PLUGIN_ID);
          return;
        }


      }

      CalDataFileImportWizardData wizardData = new CalDataFileImportWizardData(this.importObj);
      wizardData.setFuncVersion(this.editorInput.getCdrFuncVersion());
      wizardData.setParamColDataProvider(this.paramColDataProvider);
      wizardData.setParamRulesOutput(this.editorInput.getParamRulesOutput());
      CalDataFileImportWizard wizard = new CalDataFileImportWizard(wizardData);
      CalDataFileImpWizardDialog importCalDataWizardDialog =
          new CalDataFileImpWizardDialog(Display.getDefault().getActiveShell(), wizard);
      importCalDataWizardDialog.create();
      importCalDataWizardDialog.open();

    }
    else {
      CDMLogger.getInstance().infoDialog("User does not have sufficient access rights!", Activator.PLUGIN_ID);
    }
  }


}
