/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.ReviewResultNATActionSet;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultInfoPage;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamDetPage;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.cdr.ui.views.ReviewResultOutlinePageCreator;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.dialogs.CalDataViewerDialog;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.UserPreference;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwFileServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Editor for Calibration data review perspective- To view the result of review
 *
 * @author mkl2cob
 */
// Icdm-422 Review Result Editor Changes Icdm-543
public class ReviewResultEditor extends AbstractFormEditor {


  /**
   * Defines Review Result Editor id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.cdr.ui.editors.ReviewResultEditor";

  /**
   * key for decimal pref in eclipse preferences
   */
  private static final String PREF_KEY_LIMIT_DECIMALS_VALUE = "limit.decimal.value";

  /**
   * Existing No-limit string literal used in CalDataTableGraphComposite
   */
  private static final String NO_LIMIT = "<No Limit>";

  /**
   * Review result summary page (NAT table implementation) <br>
   * Page showing list of parameters considered for review
   */
  private ReviewResultParamListPage paramListPage;
  /**
   * Page showing details of a single parameter
   */
  private ReviewResultParamDetPage paramDetailsPage;
  /**
   * Page showing General info on review like participants, files etc.
   */
  private ReviewResultInfoPage reviewInfoPage;


  // ICDM-796
  /**
   * CommandState instance
   */
  private CommandState expReportService = new CommandState();

  /**
   * SelectionProviderMediator
   */
  private final SelectionProviderMediator selProvdrMediator = new SelectionProviderMediator();


  /**
   * calDataViewer dialog for the synchronisation bt Review Result editor & the cal data viewer dialog
   */
  // iCDM-1408
  private CalDataViewerDialog synchCalDataViewerDialog;

  // ICDM-2304
  private CalDataViewerDialog unSynchCalDataViewerDialog;

  private ReviewResultOutlinePageCreator reviewResultOutlinePageCreator;

  /**
   * {@inheritDoc} Icdm-548 Init method Overriden
   */
  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
    if (input instanceof ReviewResultEditorInput) {
      setSite(site);
      setInput(input);
      setPartName(input.getName());
      // Added for outline view
      this.reviewResultOutlinePageCreator = new ReviewResultOutlinePageCreator(getEditorInput());
      super.init(site, input);
    }
    else {
      CDMLogger.getInstance().errorDialog("Invalid input type for the review result editor", Activator.PLUGIN_ID);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    try {
      this.paramListPage = new ReviewResultParamListPage(this);
      this.paramDetailsPage = new ReviewResultParamDetPage(this);
      this.reviewInfoPage = new ReviewResultInfoPage(this);
      addPage(this.paramListPage);
      addPage(getParamDetailsPage());
      addPage(getReviewInfoPage());


    }
    catch (PartInitException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  /**
   * @return the reviewResultNatPageNew
   */
  public ReviewResultParamListPage getReviewResultParamListPage() {
    return this.paramListPage;
  }

  /**
   * @return the paramDetailsPage
   */
  public ReviewResultParamDetPage getParamDetailsPage() {
    return this.paramDetailsPage;
  }

  /**
   * @return the reviewInfoPage
   */
  public ReviewResultInfoPage getReviewInfoPage() {
    return this.reviewInfoPage;
  }

  /**
   * ICDM-865 method to disable the export icon when editor is closed {@inheritDoc}
   */
  @Override
  public boolean isSaveOnCloseNeeded() {
    this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    this.expReportService.setExportService(false);
    return super.isSaveOnCloseNeeded();
  }

  // Icdm-548
  /**
   * The get Editor input method overriden
   */
  @Override
  public ReviewResultEditorInput getEditorInput() {
    return (ReviewResultEditorInput) super.getEditorInput();
  }

  /**
   * ICDM-995 If review is complete user is informed through message box
   */
  public void openLockMessageDialog() {
    ReviewResultNATActionSet reviewResultNATActionSet = new ReviewResultNATActionSet();
    reviewResultNATActionSet.openLockMessageDialog(Display.getCurrent().getActiveShell(), false,
        getEditorInput().getResultData());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSaveAsAllowed() {
    // Save not applicable
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
    setStatusMessage();
    // ICDM-796
    // ICDM-865
    this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    this.expReportService.setExportService(true);
  }

  /**
   * Method to set outline selection and filter the review result nattable
   *
   * @param isPidcTreeNode
   */
  public void setReviewResultOutlineSelection(final boolean isPidcTreeNode) {
    if ((null != getOutlinePageCreator().getReviewResultOutlinePage()) &&
        (null != getOutlinePageCreator().getReviewResultOutlinePage().getViewer())) {
      // setting outline selection
      getOutlinePageCreator().getReviewResultOutlinePage().setReviewResultOutlinePageSelection(isPidcTreeNode);
      // Filtering the nattable
      if (isPidcTreeNode) {
        this.paramListPage.getTreeViewerFilter().outlineTreeSelectionForPidcTree(
            getEditorInput().getParentA2lWorkpackage(), getEditorInput().getParentA2lResponsible());
      }
    }
  }

  /**
   * Status of the focussed page is set
   */
  public void setStatusMessage() {
    if (getActivePage() == 0) {
      this.paramListPage.setStatusBarMessage(this.paramListPage.getGroupByHeaderLayer(), false);
    }
    if (getActivePage() == 1) {
      final IStatusLineManager statusLine = getEditorSite().getActionBars().getStatusLineManager();
      statusLine.setErrorMessage(null);
      statusLine.setMessage("");
    }
    if (getActivePage() == 2) {
      final IStatusLineManager statusLine = getEditorSite().getActionBars().getStatusLineManager();
      statusLine.setErrorMessage(null);
      statusLine.setMessage("");
    }
  }

  /**
   * @param quetionRespName
   */
  public void openRvwFile(final RvwFile rvwFile) {
    if (getEditorInput().getResultData().getResultBo().canDownloadFiles() && (rvwFile != null)) {
      try {
        String downloadRvwFile = new RvwFileServiceClient().downloadEmrFile(rvwFile.getId(), rvwFile.getName(),
            CommonUtils.getICDMTmpFileDirectoryPath());
        CommonUiUtils.openFile(downloadRvwFile);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
    else {
      CDMLogger.getInstance().errorDialog("Insufficient Access Rights.", Activator.PLUGIN_ID);
    }
  }

  /**
   * @return the selectionProviderMediator
   */
  public SelectionProviderMediator getSelectionProviderMediator() {
    return this.selProvdrMediator;
  }

  /**
   * @param partName part Name
   */
  public void setEditorPartName(final String partName) {
    setPartName(partName);
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
   * outline page for review result editor
   */
  @Override
  public ReviewResultOutlinePageCreator getOutlinePageCreator() {
    return this.reviewResultOutlinePageCreator;
  }

  /**
   * Updating the status bar
   *
   * @param outlineSelection flag set according to selection made in viewPart or editor.
   * @param totalItemCount total attributes in the table
   * @param filteredItemCount filtered attributes in the table
   */
  // ICDM-343
  public void updateStatusBar(final boolean outlineSelection, final int totalItemCount, final int filteredItemCount) {

    final StringBuilder buf = new StringBuilder("Displaying : ");
    buf.append(filteredItemCount).append(" out of ").append(totalItemCount);
    IStatusLineManager statusLine;
    // Updation of status based on selection in view part
    if (outlineSelection) {
      final IViewSite viewPartSite = (IViewSite) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView("com.bosch.caltool.icdm.common.ui.views.OutlineViewPart").getSite();
      statusLine = viewPartSite.getActionBars().getStatusLineManager();
    }
    // Updation of status based on selection in editor
    else {
      statusLine = getEditorSite().getActionBars().getStatusLineManager();
    }
    if (totalItemCount == filteredItemCount) {
      statusLine.setErrorMessage(null);
      statusLine.setMessage(buf.toString());
    }
    else {
      statusLine.setErrorMessage(buf.toString());
    }
    statusLine.update(true);
  }


  /**
   * @return the calDataViewerDialog
   */
  // iCDM-1408
  // ICDM-2304
  public CalDataViewerDialog getSynchCalDataViewerDialog() {
    return this.synchCalDataViewerDialog;
  }


  /**
   * @param calDataViewerDialog the calDataViewerDialog to set
   */
  // iCDM-1408
  // ICDM-2304
  public void setSynchCalDataViewerDialog(final CalDataViewerDialog calDataViewerDialog) {
    this.synchCalDataViewerDialog = calDataViewerDialog;
  }


  /**
   * @return the unSynchCalDataViewerDialog
   */
  // ICDM-2304
  public CalDataViewerDialog getUnSynchCalDataViewerDialog() {
    return this.unSynchCalDataViewerDialog;
  }


  /**
   * @param unSynchCalDataViewerDialog the unSynchCalDataViewerDialog to set
   */
  // ICDM-2304
  public void setUnSynchCalDataViewerDialog(final CalDataViewerDialog unSynchCalDataViewerDialog) {
    this.unSynchCalDataViewerDialog = unSynchCalDataViewerDialog;
  }

  /**
   * Set the eclipse preference from local preference before opening the table/graph viewer
   */
  public void setDecimalPref() {
    IEclipsePreferences pref = InstanceScope.INSTANCE.getNode(com.bosch.calmodel.caldataphyutils.Activator.PLUGIN_ID);
    String decimalPref = getCurrentDecimalPref();
    if (decimalPref.equals(CDRConstants.DECIMAL_PREF_NO_LIMIT)) {
      decimalPref = NO_LIMIT;
    }
    pref.put(PREF_KEY_LIMIT_DECIMALS_VALUE, decimalPref);
  }

  /**
   * function to trunctate the numeric decimal value based on user preference
   *
   * @param obj
   * @return object
   */
  public Object sliceNumericValue(final Object obj) {
    if (CommonUtils.isNotNull(obj)) {
      try {
        String decimalPref = getCurrentDecimalPref();
        String numString = String.valueOf(obj);
        // do not modify the existing string if user has no limit pref or if it doesn't have any decimal values
        // or if it is not a string
        if (CommonUtils.isEqual(decimalPref, CDRConstants.DECIMAL_PREF_NO_LIMIT) || !numString.contains(".") ||
            !numString.matches("-?\\d+(\\.\\d+)?")) {
          return obj;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String[] numStringArr = numString.split("\\.");
        if (numStringArr.length > 0) {
          stringBuilder.append(numStringArr[0]);
        }
        if (Integer.valueOf(decimalPref) > 0) {
          stringBuilder.append(".");
          stringBuilder.append(numStringArr[1].substring(0, Integer.valueOf(decimalPref)));
        }
        return stringBuilder.toString();
      }
      catch (Exception e) {
        return obj;
      }
    }
    return obj;
  }

  /**
   * @return decimal preference
   */
  private String getCurrentDecimalPref() {
    try {
      String decimalPref = CDRConstants.DECIMAL_PREF_NO_LIMIT;
      CurrentUserBO currentUser = new CurrentUserBO();
      UserPreference userPref = new UserPreference();
      if (CommonUtils.isNotNull(currentUser)) {
        userPref = currentUser.getUserPreference(CDRConstants.DECIMAL_PREF_LIMIT_KEY);
      }
      if (CommonUtils.isNotNull(userPref)) {
        decimalPref = userPref.getUserPrefVal();
      }
      return CommonUtils.isNotNull(decimalPref) ? decimalPref : CDRConstants.DECIMAL_PREF_NO_LIMIT;
    }
    catch (Exception e) {
      CDMLogger.getInstance().error("Error in fetching preference limit for decimal: " + e);
      return CDRConstants.DECIMAL_PREF_NO_LIMIT;
    }
  }

}
