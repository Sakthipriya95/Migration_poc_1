/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.listeners;

import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;


/**
 * Action listener for File import button for reference value in Rule Info Section. Shows a file selection dialog, to
 * get the file from which the cal data is to be imported.
 * <p>
 * If the parameter value is found in the file, then it is set to the reference value field. Otherwise an error message
 * is displayed
 *
 * @author bne4cob
 */
public final class RuleInfoImportFileBtnSelListener extends SelectionAdapter {

  /**
   * ruleInfoSection
   */
  private final RuleInfoSection ruleInfoSection;

  /**
   * Constructor
   *
   * @param ruleInfoSection RuleInfoSection
   */
  public RuleInfoImportFileBtnSelListener(final RuleInfoSection ruleInfoSection) {
    super();
    this.ruleInfoSection = ruleInfoSection;
  }

  /**
   * Get the file, using a file selection dialog and get the cal data object from the file
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void widgetSelected(final SelectionEvent event) {
    FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
    fileDialog.setText("Import from File");
    // ICDM-1131
    String[] fileNames = new String[] {
        "DCM Files (*.DCM)",
        "PaCo Files (*.XML)",
        "CDFx Files (*.CDFX)",
        "All Calibration Data Files (*.DCM, *.XML, *.CDFX)" };
    String[] fileExtns = new String[] { "*.DCM", "*.xml", "*.CDFx", "*.DCM;*.xml;*.CDFx" };

    // Get the eclipse preference store
    IPreferenceStore preference = PlatformUI.getPreferenceStore();

    // ICDM-1187
    // Retrieve the last used file extension from the preference store, and set it in the file dialog
    CommonUtils.swapArrayElement(fileExtns, preference.getString(CommonUtils.REFVALUE_IMPORT_FILE_EXTN), 0);
    CommonUtils.swapArrayElement(fileNames, preference.getString(CommonUtils.REFVALUE_IMPORT_FILE_NAME), 0);

    fileDialog.setFilterNames(fileNames);
    fileDialog.setFilterExtensions(fileExtns);

    String selectedFile = fileDialog.open();

    // Store the selected preference ICDM-1187
    preference.setValue(CommonUtils.REFVALUE_IMPORT_FILE_EXTN, fileExtns[fileDialog.getFilterIndex()]);
    preference.setValue(CommonUtils.REFVALUE_IMPORT_FILE_NAME, fileNames[fileDialog.getFilterIndex()]);

    if (selectedFile != null) {
      // unset the flag to know that the reference value is dragged
      this.ruleInfoSection.setRefValTyped(false);
      try {
        // Parsing the input files selected
        CaldataFileParserHandler parserHandler = new CaldataFileParserHandler(ParserLogger.getInstance(), null);
        Map<String, CalData> calDataMap = parserHandler.getCalDataObjects(selectedFile);

        IParameter param = this.ruleInfoSection.getSelectedParam();
        CalData calData = calDataMap.get(param.getName());

        // If caldata is available in the file, then set it to the reference value. Else, show error message
        if ((calData != null) && calData.getCalDataPhy().getType().equals(param.getType())) {
          this.ruleInfoSection.setRefValCalDataObj(calData);
          this.ruleInfoSection.getRefValueTxt().setText(calData.getCalDataPhy().getSimpleDisplayValue());
          this.ruleInfoSection.displayTableGraph(this.ruleInfoSection.getRefValueTxt(), calData, true);
          this.ruleInfoSection.getRefCalAttr().setCalData(this.ruleInfoSection.getRefValCalDataObj());


          this.ruleInfoSection.getRefValueTxt().setText(calData.getCalDataPhy().getSimpleDisplayValue());
          this.ruleInfoSection.setValues();
          this.ruleInfoSection.onRefValTextModification();
        }
        else {
          CDMLogger.getInstance().infoDialog(
              "The parameter " + param.getName() + " is not available in the selected file", Activator.PLUGIN_ID);
        }
      }
      catch (IcdmException exp) {
        CDMLogger.getInstance().errorDialog("Error occured while parsing the files to be imported !" + exp.getMessage(),
            exp, Activator.PLUGIN_ID);
      }
    }

    // set the flag to further modification through typing
    this.ruleInfoSection.setRefValTyped(true);
  }

}
