/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldatacomparison.CalDataAttributes;
import com.bosch.calmodel.caldatacomparison.CalDataComparison;
import com.bosch.calmodel.caldataphyutils.exception.CalDataTableGraphException;
import com.bosch.caltool.icdm.client.bo.caldataimport.CalDataImporterHandler;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.wizards.CalDataFileImportWizard;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;


/**
 * @author mkl2cob
 */
public class CompareRulesPageTableGraph {

  /**
   * Old ref value col label
   */
  private static final String OLD_REF_VAL = "Old  Value";

  /**
   * New ref value col label
   */
  private static final String NEW_REF_VAL = "New  Value";
  /**
   * wizard page
   */
  private final CompareRuleImpWizardPage wizardPage;

  /**
   * @param compareRuleImpWizardPage
   */
  public CompareRulesPageTableGraph(final CompareRuleImpWizardPage wizardPage) {
    this.wizardPage = wizardPage;
  }

  /**
   * Populate table/graph ui
   *
   * @param compObj
   */
  protected void populateTableGraph() {
    // To avoid hanging when tableGraph is filled for the first time when application is started
    Runnable busyRunnable = new Runnable() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        try {
          int graphColor = 0;
          CalDataComparison calDataComparison = new CalDataComparison();
          CalDataFileImportWizard wizard =
              (CalDataFileImportWizard) CompareRulesPageTableGraph.this.wizardPage.getWizard();

          CalDataImporterHandler calDataImportHandler = wizard.getCalDataImportHandler();
          CalData oldRefValueCalData = null;
          if (null != CompareRulesPageTableGraph.this.wizardPage.getSelCompObj().getOldRule()) {


            // old refvalue
            oldRefValueCalData =
                calDataImportHandler.getOldRefValue(CompareRulesPageTableGraph.this.wizardPage.getSelCompObj());


            if (null != oldRefValueCalData) {
              // set the CalDataAttributes for the comparison object
              CalDataAttributes oldCalAttr = new CalDataAttributes(oldRefValueCalData, graphColor);
              graphColor++;
              oldCalAttr.setShowDifferenceIndicator(true);
              String oldLabelPrefix = OLD_REF_VAL;
              oldCalAttr.setLabelPrefix(" (" + oldLabelPrefix + ") ");
              calDataComparison.addCalDataAttr(oldCalAttr);
            }
          }
          // New refvalue
          CalData newRefValueCalData =
              calDataImportHandler.getNewRefValue(CompareRulesPageTableGraph.this.wizardPage.getSelCompObj());

          if (null == newRefValueCalData) {
            // get the value from text field
            newRefValueCalData =
                CompareRulesPageTableGraph.this.wizardPage.getRuleDtlsSection().getCalDataFromTextField();
          }
          if (null != newRefValueCalData) {
            // if newRefValueCalData is not null, get the CalDataAttributes
            CalDataAttributes newCalAttr = new CalDataAttributes(newRefValueCalData, graphColor);
            newCalAttr.setShowDifferenceIndicator(true);
            String newLabelPrefix = NEW_REF_VAL;
            newCalAttr.setLabelPrefix(" (" + newLabelPrefix + ") ");
            calDataComparison.addCalDataAttr(newCalAttr);
          }
          // if both old and new values are null, clear the graph
          if ((null == oldRefValueCalData) && (null == newRefValueCalData)) {
            CompareRulesPageTableGraph.this.wizardPage.getCalDataTableGraphComposite().clearTableGraph();
          }
          else {
            // fill table/graph if either old or new value is present
            CompareRulesPageTableGraph.this.wizardPage.getCalDataTableGraphComposite()
                .fillTableAndGraph(calDataComparison);

          }
        }
        catch (CalDataTableGraphException | DataException excep) {
          CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
        }

      }
    };
    BusyIndicator.showWhile(Display.getDefault(), busyRunnable);
  }

  /**
   * ICDM-2180
   *
   * @param list List
   */
  public void populateMultiTableGraph(final List list) {
    // To avoid hanging when tableGraph is filled for the first time when application is started
    Runnable busyRunnable = new Runnable() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        try {
          List<CalData> calDataArrayList = new ArrayList<>();

          // TODO show the big decimal values alse3
          for (Object selection : list) {
            CalDataImportComparisonModel compObj = (CalDataImportComparisonModel) selection;
            CalDataFileImportWizard wizard =
                (CalDataFileImportWizard) CompareRulesPageTableGraph.this.wizardPage.getWizard();
            CalDataImporterHandler calDataImportHandler = wizard.getCalDataImportHandler();
            CalData oldRefValueCalData = null;
            if (null != compObj.getOldRule()) {
              // old refvalue
              try {
                oldRefValueCalData = calDataImportHandler.getOldRefValue(compObj);
              }
              catch (DataException e) {
                CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
              }
            }
            if (oldRefValueCalData != null) {
              // add old reference value
              calDataArrayList.add(oldRefValueCalData);
            }
            CalData newRefValueCalData = null;
            if (null != compObj.getNewRule()) {
              try {
                newRefValueCalData = calDataImportHandler.getNewRefValue(compObj);
              }
              catch (DataException e) {
                CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
              }
            }
            if (newRefValueCalData != null) {
              // add new reference value
              calDataArrayList.add(newRefValueCalData);
            }
          }
          CalData[] calDataList = calDataArrayList.toArray(new CalData[calDataArrayList.size()]);

          if (CommonUtils.isNotEmpty(calDataList)) {
            CompareRulesPageTableGraph.this.wizardPage.getCalDataTableGraphComposite().fillTableAndGraph(calDataList);
          }

        }
        catch (CalDataTableGraphException excep) {
          CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
        }
      }
    };
    BusyIndicator.showWhile(Display.getDefault(), busyRunnable);

  }
}
