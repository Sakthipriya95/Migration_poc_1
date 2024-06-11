/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.ExportCDFxInputData;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ruleseditor.dialogs.ExportRulesetAsCDFxDialog;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetRuleServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author UKT1COB
 */
public class ExportRulesetAsCDFxAction extends Action {

  /**
   *
   */
  private static final String EXPORT_NOT_SUCCESSFUL_MSG =
      "Export is not successful, as there is no rule with reference value for any of the parameters";
  private static final String ERROR_WHILE_EXPORTING_CDFX = "Error While Exporting ruleset as CDFx file : ";
  private final ParamCollectionDataProvider paramCollectionDataProvider;
  private final ParamCollection ruleSet;
  private final AbstractFormPage listPage;
  private String exportedFilePath = null;
  private List<IParameter> listParamsInRuleset;

  /**
   * @param listPage list page of rule set
   * @param cdrFunction ParamCollection
   * @param paramCollectionDataProvider data provider
   */
  public ExportRulesetAsCDFxAction(final AbstractFormPage listPage, final ParamCollection cdrFunction,
      final ParamCollectionDataProvider paramCollectionDataProvider) {
    super("Export Ruleset as CDFx");

    this.paramCollectionDataProvider = paramCollectionDataProvider;
    this.ruleSet = cdrFunction;
    this.listPage = listPage;

    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EXPORT_DATA_16X16));
    setEnabled(this.paramCollectionDataProvider.isModifiable(cdrFunction));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {

    ReviewParamEditorInput reviewParamEditorInp = ((ReviewParamEditor) this.listPage.getEditor()).getEditorInput();

    if (CommonUtils.isNotEmpty(reviewParamEditorInp.getParamMap())) {
      this.listParamsInRuleset =
          (List<IParameter>) reviewParamEditorInp.getParamMap().values().stream().collect(Collectors.toList());
      ExportRulesetAsCDFxDialog exportRuleSetAsCdfxDialog = new ExportRulesetAsCDFxDialog(
          Display.getCurrent().getActiveShell(), reviewParamEditorInp.getParamDataProvider(), this.listParamsInRuleset);
      exportRuleSetAsCdfxDialog.open();
      if (exportRuleSetAsCdfxDialog.getReturnCode() == Window.OK) {
        exportRuleset(exportRuleSetAsCdfxDialog);
      }
    }
    else {
      CDMLogger.getInstance().infoDialog(
          "Export is not possible as there is no parameter available for the selected Ruleset", Activator.PLUGIN_ID);
    }
  }

  /**
   * @param exportRuleSetAsCdfxDialog
   * @param ruleSet
   * @param listParamsInRuleset
   * @param set
   */
  private void exportRuleset(final ExportRulesetAsCDFxDialog exportRuleSetAsCdfxDialog) {


    try {
      final ProgressMonitorDialog progressDlg = new CustomProgressDialog(Display.getDefault().getActiveShell());
      progressDlg.run(true, true, monitor -> {

        monitor.beginTask("Exporting Ruleset as CDFx File...", 100);
        monitor.worked(20);
        List<String> paramList = ExportRulesetAsCDFxAction.this.listParamsInRuleset.stream().map(IParameter::getName)
            .collect(Collectors.toList());
        this.exportedFilePath = exportRulesetAsCdfx(createExportInput(paramList, exportRuleSetAsCdfxDialog));
        monitor.worked(100);
        monitor.done();
      });

      showMessageAfterExport();
    }
    catch (InvocationTargetException | InterruptedException exp) {
      Thread.currentThread().interrupt();
      CDMLogger.getInstance().errorDialog("CDFx Export failed due to following exception \n" + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }

  }

  /**
   * @param createExportInput
   * @return
   */
  private String exportRulesetAsCdfx(final ExportCDFxInputData exportInput) {

    try {
      return new RuleSetRuleServiceClient().exportRuleSetCalDataAsCdfx(exportInput);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(ERROR_WHILE_EXPORTING_CDFX + exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    return null;
  }

  /**
   * @param paramList
   * @param attrValueModSet
   * @param userSelFilePath
   * @param ruleSet2
   * @return
   */
  private ExportCDFxInputData createExportInput(final List<String> labelNames,
      final ExportRulesetAsCDFxDialog exportRulesetAsCdfxDialog) {

    ExportCDFxInputData exportInput = new ExportCDFxInputData();
    exportInput.setLabelNames(labelNames);
    exportInput.setAttrValueModSet(new TreeSet<>(exportRulesetAsCdfxDialog.getAttrValueModSet()));
    exportInput.setDestFileDir(exportRulesetAsCdfxDialog.getUserSelDestFileDir());
    exportInput.setDestFileName(exportRulesetAsCdfxDialog.getUserSelDestFileName());

    ReviewRuleParamCol<RuleSet> paramCol = new ReviewRuleParamCol();
    paramCol.setParamCollection((RuleSet) this.ruleSet);
    exportInput.setParamCol(paramCol);

    return exportInput;
  }

  /**
   * @param responseMsg
   * @param userSelFilePath
   */
  private void showMessageAfterExport() {

    if (CommonUtils.isFileAvailable(this.exportedFilePath)) {
      String message = "CDFx file exported successfully to path :" + this.exportedFilePath;
      CommonUiUtils.getInstance().openInfoDialogWithLink(message, this.exportedFilePath);
      // Open Parent directory where the cdfx file is exported
      CommonUiUtils.openFile(new File(this.exportedFilePath).getParent());
    }
    else {
      CDMLogger.getInstance().errorDialog(EXPORT_NOT_SUCCESSFUL_MSG, Activator.PLUGIN_ID);

      // Delete the empty file if file exists
      File file = new File(this.exportedFilePath);
      if (file.exists()) {
        try {
          Files.delete(file.toPath());
        }
        catch (IOException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }
  }

}