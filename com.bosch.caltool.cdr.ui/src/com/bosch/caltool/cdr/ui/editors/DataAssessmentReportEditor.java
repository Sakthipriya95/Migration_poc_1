/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.pages.DataAssessmentBaselinesPage;
import com.bosch.caltool.cdr.ui.editors.pages.DataAssessmentCompHexRvwResultsPage;
import com.bosch.caltool.cdr.ui.editors.pages.DataAssessmentOverviewPage;
import com.bosch.caltool.cdr.ui.editors.pages.DataAssessmentQuestionnaireResultsPage;
import com.bosch.caltool.cdr.ui.editors.pages.DataAssessmentWorkPackagesPage;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.CalDataViewerDialog;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * Editor to show the report of data assessment
 *
 * @author ajk2cob
 */
public class DataAssessmentReportEditor extends AbstractFormEditor {

  /**
   * Defines DataAssessmentEditor Id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.cdr.ui.editors.DataAssessmentReportEditor";

  private boolean isBaseline;
  private Long baselineId;
  private DataAssessmentWorkPackagesPage workPackagesPage;
  private DataAssessmentCompHexRvwResultsPage compareHexRvwResultsPage;
  private DataAssessmentQuestionnaireResultsPage questionnaireResultsPage;
  private DataAssessmentBaselinesPage baselinesPage;

  /**
   * OVERVIEW_PAGE_INDEX page number
   */
  private static final int OVERVIEW_PAGE_INDEX = 0;
  /**
   * WORK_PACKAGES_PAGE_INDEX page number
   */
  private static final int WORK_PACKAGES_PAGE_INDEX = 1;

  /**
   * COMPARE_HEXRVW_RESULTS_PAGE_INDEX page number
   */
  private static final int COMPARE_HEXRVW_RESULTS_PAGE_INDEX = 2;
  /**
   * QUESTIONNAIRE_RESULTS_PAGE_INDEX page number
   */
  private static final int QUESTIONNAIRE_RESULTS_PAGE_INDEX = 3;
  /**
   * BASELINES_PAGE_INDEX page number
   */
  private static final int BASELINES_PAGE_INDEX = 4;

  /**
   * the synchronized table graph viewer
   */
  // ICDM-2498
  private CalDataViewerDialog synchCalDataViewerDialog;

  /**
   * the normal table graph viewer
   */
  // ICDM-2498
  private CalDataViewerDialog unSynchCalDataViewerDialog;

  /**
   * {@inheritDoc}
   */
  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
    setPartName(input.getName());
    super.init(site, input);
    this.isBaseline = getEditorInput().isBaseline();
    this.baselineId = getEditorInput().getBaselineId();
    String userNotificationMsg = getEditorInput().getUserNotificationMsg();
    if (CommonUtils.isNotEmptyString(userNotificationMsg)) {
      MessageDialogUtils.getInfoMessageDialog("WP Finished Status Reset Info", userNotificationMsg);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    DataAssessmentOverviewPage overviewPage = new DataAssessmentOverviewPage(this);
    this.workPackagesPage = new DataAssessmentWorkPackagesPage(this);
    this.compareHexRvwResultsPage = new DataAssessmentCompHexRvwResultsPage(this);
    this.questionnaireResultsPage = new DataAssessmentQuestionnaireResultsPage(this);
    this.baselinesPage = new DataAssessmentBaselinesPage(this);
    try {
      addPage(overviewPage);
      addPage(this.workPackagesPage);
      addPage(this.compareHexRvwResultsPage);
      addPage(this.questionnaireResultsPage);
      addPage(this.baselinesPage);
    }
    catch (PartInitException exep) {
      CDMLogger.getInstance().warn(exep.getMessage(), exep, Activator.PLUGIN_ID);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSave(final IProgressMonitor arg0) {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSaveAs() {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSaveAsAllowed() {
    // Not applicable
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setActivePage(final int pageIndex) {
    super.setActivePage(pageIndex);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
    setStatus();
  }

  /**
   * Set status according to the page
   */
  public void setStatus() {
    // Set status for respective pages
    if (getActivePage() == DataAssessmentReportEditor.OVERVIEW_PAGE_INDEX) {
      final IStatusLineManager statusLine = getEditorSite().getActionBars().getStatusLineManager();
      statusLine.setMessage("");
    }
    else if (getActivePage() == DataAssessmentReportEditor.WORK_PACKAGES_PAGE_INDEX) {
      this.workPackagesPage.updateStatusBar(false);
    }
    else if (getActivePage() == DataAssessmentReportEditor.COMPARE_HEXRVW_RESULTS_PAGE_INDEX) {
      this.compareHexRvwResultsPage.updateStatusBar(false);
    }
    else if (getActivePage() == DataAssessmentReportEditor.QUESTIONNAIRE_RESULTS_PAGE_INDEX) {
      this.questionnaireResultsPage.updateStatusBar(false);
    }
    else if (getActivePage() == DataAssessmentReportEditor.BASELINES_PAGE_INDEX) {
      this.baselinesPage.updateStatusBar(false);
    }
  }

  @Override
  public DataAssessmentReportEditorInput getEditorInput() {
    return (DataAssessmentReportEditorInput) super.getEditorInput();
  }

  /**
   * @return the isBaseline
   */
  public boolean isBaseline() {
    return this.isBaseline;
  }

  /**
   * @return the baselineId
   */
  public Long getBaselineId() {
    return this.baselineId;
  }

  /**
   * @return the workPackagesPage
   */
  public DataAssessmentWorkPackagesPage getWorkPackagesPage() {
    return this.workPackagesPage;
  }


  /**
   * @param workPackagesPage the workPackagesPage to set
   */
  public void setWorkPackagesPage(final DataAssessmentWorkPackagesPage workPackagesPage) {
    this.workPackagesPage = workPackagesPage;
  }


  /**
   * @return the compareHexRvwResultsPage
   */
  public DataAssessmentCompHexRvwResultsPage getCompareHexRvwResultsPage() {
    return this.compareHexRvwResultsPage;
  }


  /**
   * @param compareHexRvwResultsPage the compareHexRvwResultsPage to set
   */
  public void setCompareHexRvwResultsPage(final DataAssessmentCompHexRvwResultsPage compareHexRvwResultsPage) {
    this.compareHexRvwResultsPage = compareHexRvwResultsPage;
  }


  /**
   * @return the questionnaireResultsPage
   */
  public DataAssessmentQuestionnaireResultsPage getQuestionnaireResultsPage() {
    return this.questionnaireResultsPage;
  }


  /**
   * @param questionnaireResultsPage the questionnaireResultsPage to set
   */
  public void setQuestionnaireResultsPage(final DataAssessmentQuestionnaireResultsPage questionnaireResultsPage) {
    this.questionnaireResultsPage = questionnaireResultsPage;
  }


  /**
   * @return the baselinesPage
   */
  public DataAssessmentBaselinesPage getBaselinesPage() {
    return this.baselinesPage;
  }


  /**
   * @param baselinesPage the baselinesPage to set
   */
  public void setBaselinesPage(final DataAssessmentBaselinesPage baselinesPage) {
    this.baselinesPage = baselinesPage;
  }

  /**
   * @return the synchCalDataViewerDialog
   */
  public CalDataViewerDialog getSynchCalDataViewerDialog() {
    return this.synchCalDataViewerDialog;
  }


  /**
   * @param synchCalDataViewerDialog the synchCalDataViewerDialog to set
   */
  public void setSynchCalDataViewerDialog(final CalDataViewerDialog synchCalDataViewerDialog) {
    this.synchCalDataViewerDialog = synchCalDataViewerDialog;
  }


  /**
   * @return the unSynchCalDataViewerDialog
   */
  public CalDataViewerDialog getUnSynchCalDataViewerDialog() {
    return this.unSynchCalDataViewerDialog;
  }


  /**
   * @param unSynchCalDataViewerDialog the unSynchCalDataViewerDialog to set
   */
  public void setUnSynchCalDataViewerDialog(final CalDataViewerDialog unSynchCalDataViewerDialog) {
    this.unSynchCalDataViewerDialog = unSynchCalDataViewerDialog;
  }

  @Override
  public IClientDataHandler getDataHandler() {
    return getEditorInput().getDataAssmntReportDataHandler();
  }
}
