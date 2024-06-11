/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;

import com.bosch.caltool.apic.ui.editors.pages.NodeAccessRightsPage;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.editors.pages.CaldataAnalyzerPage;

/**
 * @author pdh2cob
 */
public class CaldataAnalyzerEditor extends AbstractFormEditor {

  /**
   * Defines CaldataAnalyzerEditor id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.icdm.ui.editors.CaldataAnalyzerEditor";

  private CaldataAnalyzerPage caldataAnalyzerPage;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    try {
      // Add pages to the editor form
      this.caldataAnalyzerPage = new CaldataAnalyzerPage(this, "Calibration Data Analyzer");
      addPage(this.caldataAnalyzerPage);

      addPage(new NodeAccessRightsPage(this, getEditorInput().getNodeAccessBO()));

    }
    catch (PartInitException exep) {
      CDMLogger.getInstance().warn(exep.getMessage(), exep, Activator.PLUGIN_ID);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CaldataAnalyzerEditorInput getEditorInput() {
    return (CaldataAnalyzerEditorInput) super.getEditorInput();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSave(final IProgressMonitor iprogressmonitor) {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSaveAs() {
    // Not applicable
  }


  @Override
  public void dispose() {

    for (String outputDir : this.caldataAnalyzerPage.getOutputDirectoryList()) {
      if (outputDir != null) {

        File folder = new File(outputDir);
        if (folder.exists() && (folder.listFiles().length > 0)) {
          try {
            // Delete child files
            for (File file : folder.listFiles()) {
              FileUtils.forceDelete(file);
            }
            // Delete folder
            FileUtils.forceDelete(folder);

            // Delete zip file downloaded (web service response)
            File zipFile = new File(outputDir + ApicConstants.ZIP_FILE_EXT);
            if (zipFile.exists()) {
              FileUtils.forceDelete(zipFile);
            }

            CDMLogger.getInstance().info(outputDir + " deleted!");

          }
          catch (IOException e) {
            CDMLogger.getInstance().info(e.getMessage(), e, Activator.PLUGIN_ID);
          }
        }
      }
    }
    super.dispose();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSaveAsAllowed() {
    // Not applicable
    return false;
  }

}
