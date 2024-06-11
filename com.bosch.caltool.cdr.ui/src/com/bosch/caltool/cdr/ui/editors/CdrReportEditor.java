/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.pages.CdrReportListPage;
import com.bosch.caltool.cdr.ui.editors.pages.CdrReportStatisticsPage;
import com.bosch.caltool.cdr.ui.views.CdrReportA2lOutlinePageCreator;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * ICDM-1697
 *
 * @author mkl2cob
 */
public class CdrReportEditor extends AbstractFormEditor {

  /**
   * data review report page
   */
  private CdrReportListPage dataReportPage;

  /**
   * Defines PIDCEditor id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.cdr.ui.editors.CdrReportEditor";

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {

    try {
      this.dataReportPage = new CdrReportListPage(this, "", "Review Report");
      // ICDM-2329
      // Page to display the Calibration Data Review Statistics
      CdrReportStatisticsPage cdrStatisticsPage = new CdrReportStatisticsPage(this, "", "Review Statistics");
      addPage(this.dataReportPage);
      // Add statistics page to the Report page
      addPage(cdrStatisticsPage);
    }
    catch (PartInitException exep) {
      CDMLogger.getInstance().warn(exep.getMessage(), exep, Activator.PLUGIN_ID);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
    setPartName(input.getName());
    super.init(site, input);
    String userNotificationMsg = getEditorInput().getUserNotificationMsg();
    if (CommonUtils.isNotEmptyString(userNotificationMsg)) {
      MessageDialogUtils.getInfoMessageDialog("WP Finished Status Reset Info", userNotificationMsg);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IPageCreator getOutlinePageCreator() {
    return new CdrReportA2lOutlinePageCreator(getEditorInput());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CdrReportEditorInput getEditorInput() {
    return (CdrReportEditorInput) super.getEditorInput();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSave(final IProgressMonitor monitor) {
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
   * Status of the focussed page is set
   */
  // ICDM-343
  public void setStatus() {
    if (getActivePage() == 0) {
      this.dataReportPage.setStatusBarMessage(false);
    }
  }

  // ICDM-1703
  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    CommandState expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    expReportService.setExportService(true);
    super.setFocus();
    setStatus();
  }
}
