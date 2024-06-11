/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.element.DataElement;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.a2l.precal.PreCalDataWizardDataHandler;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.jobs.CDFFileExportJob;
import com.bosch.caltool.icdm.common.ui.jobs.PreCalDataRetrieveJob;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.services.IA2lParamTable;
import com.bosch.caltool.icdm.common.ui.wizards.pages.PreCalAttrValWizardPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.PreCalCDRFltrSltnPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.PreCalRecommendedValuesPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.PreCalRuleSetSltnPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.PreCalSeriesFltrSltnPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.PreCalSourceSelectionPage;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.precal.PRECAL_SOURCE_TYPE;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.ExternalLinkServiceClient;


/**
 * The Class ExportCDFWizard.
 *
 * @author rgo7cob Icdm-697 Export Wizard created
 */
public class PreCalDataExportWizard extends Wizard {

  /**
   * The cdf src sel page.
   */
  private PreCalSourceSelectionPage dataSrcSelPage;

  /**
   * The cdr filter sel page.
   */
  private PreCalCDRFltrSltnPage cdrFilterSelPage;

  /**
   * The series fltr sel page.
   */
  private PreCalSeriesFltrSltnPage seriesFltrSelPage;

  /**
   * The attr val page.
   */
  private PreCalAttrValWizardPage attrValPage;

  /**
   * The rule set sel page.
   */
  private PreCalRuleSetSltnPage ruleSetSelPage;

  /**
   * RecommendedValuesPage instance.
   */
  private PreCalRecommendedValuesPage recommValPage;

  /**
   * The a 2 l nat table.
   */
  private final IA2lParamTable a2lNatTable;

  /**
   * Wizard's data handler
   */
  private final PreCalDataWizardDataHandler dataHandler;

  /**
   * Instantiates a new export CDF wizard.
   *
   * @param paramList the param list new
   * @param a2lFileInfoBO the a 2 l editor data handler
   * @param pidcA2LBO the pidc A 2 LBO
   * @param a2lNatTable the a 2 l nat table
   */
  public PreCalDataExportWizard(final List<A2LParameter> paramList, final A2LFileInfoBO a2lFileInfoBO,
      final PidcA2LBO pidcA2LBO, final IA2lParamTable a2lNatTable) {
    super();
    // ICDM-1975
    this.a2lNatTable = a2lNatTable;
    this.dataHandler = new PreCalDataWizardDataHandler(paramList, pidcA2LBO, a2lFileInfoBO);
  }

  /**
   * @return the dataHandler
   */
  public PreCalDataWizardDataHandler getDataHandler() {
    return this.dataHandler;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void addPages() {
    setNeedsProgressMonitor(true);
    // iCDM-902
    setWindowTitle("Get Pre-Calibration Data");

    String a2lFileName = getDataHandler().getPidcA2LBO().getA2LFileName();

    this.dataSrcSelPage = new PreCalSourceSelectionPage("Select the Source", a2lFileName);
    addPage(this.dataSrcSelPage);

    this.ruleSetSelPage = new PreCalRuleSetSltnPage("Select the Rule Set", a2lFileName);
    addPage(this.ruleSetSelPage);

    this.cdrFilterSelPage = new PreCalCDRFltrSltnPage("Select the Source", a2lFileName);
    addPage(this.cdrFilterSelPage);

    this.seriesFltrSelPage = new PreCalSeriesFltrSltnPage("Select the Source1", a2lFileName);
    addPage(this.seriesFltrSelPage);

    this.attrValPage = new PreCalAttrValWizardPage("Dependent Attributes", a2lFileName);
    addPage(this.attrValPage);

    this.recommValPage = new PreCalRecommendedValuesPage("Recommended Values", a2lFileName);
    addPage(this.recommValPage);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFinish() {
    // Icdm-697 Export Wizard see if the last page is completed
    return this.recommValPage.isPageComplete();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performFinish() {

    // For other source types, the history filling is done at the server side
    Map<String, CalData> calDataMapToUse =
        getDataHandler().getPreCalSourceType() == PRECAL_SOURCE_TYPE.SERIES_STATISTICS
            ? fillHistory(getDataHandler().getCalDataMap()) : getDataHandler().getCalDataMap();

    if (PreCalDataExportWizard.this.getDataHandler().isLoadToA2lSelected()) {
      this.a2lNatTable.addPreCalibDataToA2l(calDataMapToUse);
      return true;
    }

    // Icdm-697 Export Wizard call the Job for creating CDF file
    if (FileIOUtil.checkIfFileIsLocked(this.recommValPage.getFileSelect())) {
      setFileUsedErrorMessage();
      this.recommValPage.setPageComplete(false);
      return false;
    }

    this.recommValPage.setErrorMessage(null);
    this.recommValPage.setPageComplete(true);

    final boolean isFileValid = CommonUtils.isWritableFilePath(this.recommValPage.getFileSelect(), ".cdfx");
    if (isFileValid) {

      final Job cdfxExportJob =
          new CDFFileExportJob(new MutexRule(), calDataMapToUse, this.recommValPage.getFileSelect(), "*.cdf", true);
      cdfxExportJob.schedule();
      return true;
    }

    CDMLogger.getInstance().errorDialog("File Path and name is invalid.Please provide a proper file name !",
        Activator.PLUGIN_ID);
    this.recommValPage.setPageComplete(false);
    return false;

  }


  /**
   * Fill history.
   *
   * @param calDtaMap the cal data map
   * @return the map
   */
  private Map<String, CalData> fillHistory(final Map<String, CalData> calDtaMap) {
    Map<String, CalData> calDataWithHisMap = new ConcurrentHashMap<>();
    String path;
    try {
      path = new ExternalLinkServiceClient().getLinkInfo(getDataHandler().getPidcA2LBO().getPidcVersion())
          .getDisplayText();
      path = path.substring(path.indexOf(':') + 1).trim();

      String[] projInfo = path.split("->");
      for (CalData calDataObj : calDtaMap.values()) {
        CalData paramCheckedValClone = calDataObj.clone();
        if (paramCheckedValClone.getCalDataHistory() == null) {
          paramCheckedValClone.setCalDataHistory(new CalDataHistory());
        }
        String paramName = calDataObj.getShortName();
        getReviewHistEntry(paramName, projInfo, paramCheckedValClone);
        calDataWithHisMap.put(paramName, paramCheckedValClone);
      }
    }
    catch (ApicWebServiceException | CloneNotSupportedException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return calDataWithHisMap;
  }


  /**
   * Gets the review hist entry.
   *
   * @param paramName the param name
   * @param projInfo the proj info
   * @param paramCheckedValClone the param checked val clone
   * @return the review hist entry
   */
  private HistoryEntry getReviewHistEntry(final String paramName, final String[] projInfo,
      final CalData paramCheckedValClone) {
    HistoryEntry historyEntry = paramCheckedValClone.getCalDataHistory().getHistoryEntryList().get(0);
    historyEntry
        .setProject(getDataElement(projInfo[0] + "--->" + getDataHandler().getPidcA2LBO().getPidcVersion().getName()));

    String a2lLabel = getDataHandler().getPidcA2LBO().getPidcA2l().getSdomPverVarName() + " : " +
        getDataHandler().getPidcA2LBO().getPidcA2l().getName();
    historyEntry.setProgramIdentifier(getDataElement(a2lLabel));
    for (A2LParameter a2lParam : getDataHandler().getParamList()) {
      setTargetInfo(paramName, historyEntry, a2lParam);
    }
    return historyEntry;
  }


  /**
   * Sets the target info.
   *
   * @param paramName the param name
   * @param historyEntry the history entry
   * @param a2lParam the a 2 l param
   */
  private void setTargetInfo(final String paramName, final HistoryEntry historyEntry, final A2LParameter a2lParam) {
    if (a2lParam.getName().equals(paramName)) {
      StringBuilder targetInfoStr = new StringBuilder();
      targetInfoStr.append(a2lParam.getPclassString());
      String result = targetInfoStr.toString();
      historyEntry.setTargetVariant(getDataElement(result));
    }
  }

  /**
   * Gets the data element.
   *
   * @param value the value
   * @return the data element
   */
  private DataElement getDataElement(final String value) {
    DataElement dataElement = new DataElement();
    dataElement.setValue(CommonUtils.checkNull(value));

    return dataElement;
  }

  /**
   * iCDM-1455 <br>
   * Set appropriate error message.
   */
  private void setFileUsedErrorMessage() {
    CDMLogger.getInstance().errorDialog(
        "File could be already used by another process. Please provide a different file name with VALID path in order to export.",
        Activator.PLUGIN_ID);
    this.recommValPage.setPageComplete(false);
  }

  /**
   * {@inheritDoc} ICDM-879 get different next pages for different btn selection
   */
  @Override
  public IWizardPage getNextPage(final IWizardPage currentPage) {
    IWizardPage wizardPage = super.getNextPage(currentPage);
    if (currentPage instanceof PreCalSourceSelectionPage) {
      if (((PreCalSourceSelectionPage) currentPage).getCdrRuleSel().getSelection()) {
        wizardPage = this.cdrFilterSelPage;
      }
      else if (((PreCalSourceSelectionPage) currentPage).getSeriesStatSel().getSelection()) {
        wizardPage = this.seriesFltrSelPage;
      }
      else if (((PreCalSourceSelectionPage) currentPage).getRuleSetSel().getSelection()) {
        wizardPage = this.ruleSetSelPage;
      }
    }
    if (currentPage instanceof PreCalCDRFltrSltnPage) {
      if (getDataHandler().getProjAttrValDetails().getAttrMap().isEmpty()) {
        this.attrValPage.fetchRecomValuesJob(getShell());
        wizardPage = this.recommValPage;
      }
      else {
        wizardPage = this.attrValPage;
      }

    }
    if ((currentPage instanceof PreCalSeriesFltrSltnPage) || (currentPage instanceof PreCalAttrValWizardPage)) {
      wizardPage = this.recommValPage;
    }

    return wizardPage;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performCancel() {
    PreCalDataRetrieveJob retrieveJob = null;
    if (this.dataSrcSelPage.getSeriesStatSel().getSelection()) {
      retrieveJob = this.seriesFltrSelPage.getRetrieveJob();
    }
    if (retrieveJob != null) {
      retrieveJob.cancel();
      // retrieveJob.getResult()==null means the job is still running
      while (retrieveJob.getResult() == null) {
        // TODO:Show Busy indicator if required
      }
    }
    return true;
  }

}
