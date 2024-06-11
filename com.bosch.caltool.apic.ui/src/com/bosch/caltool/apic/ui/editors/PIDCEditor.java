package com.bosch.caltool.apic.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.editors.pages.A2LFilePage;
import com.bosch.caltool.apic.ui.editors.pages.EMRNatPage;
import com.bosch.caltool.apic.ui.editors.pages.FocusMatrixPage;
import com.bosch.caltool.apic.ui.editors.pages.NodeAccessRightsPage;
import com.bosch.caltool.apic.ui.editors.pages.PIDCAttrPage;
import com.bosch.caltool.apic.ui.editors.pages.PIDCCocWpPage;
import com.bosch.caltool.apic.ui.editors.pages.PidcVersionsPage;
import com.bosch.caltool.apic.ui.editors.pages.ResponsibilityPage;
import com.bosch.caltool.apic.ui.editors.pages.RiskEvaluationPage;
import com.bosch.caltool.apic.ui.editors.pages.WorkPackagesPage;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.views.PIDCPageCreator;
import com.bosch.caltool.apic.ui.views.PidcDetailsPageCreator;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.IEditorWithStructure;
import com.bosch.caltool.icdm.common.ui.views.IStructurePageCreator;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.IEditable;
import com.bosch.caltool.icdm.ws.rest.client.apic.FocusMatrixServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcRmDefClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author adn1cob
 */
public class PIDCEditor extends AbstractFormEditor implements IEditable, IEditorWithStructure {

  /**
   * Defines PIDCEditor id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.apic.ui.editors.PIDCEditor";
  /**
   * PIDCEditorInput instance
   */
  private PIDCEditorInput editorInput;
  // Instance of PIDC page
  private PIDCAttrPage pidcPage;
  // Instance of versions page
  private PidcVersionsPage versionsPage;
  // Instance of PIDC version
  private PidcVersion pidcVersion;
  // Instance of emr nat page
  private EMRNatPage emrPage;
  // Instance of Pidc CoC WP page
  private PIDCCocWpPage pidcCoCWpPage;

  private SelectionProviderMediator selectionProviderMediator = new SelectionProviderMediator();

  /**
   * CommandState instance
   */
  CommandState expReportService = new CommandState();

  /**
   * RiskEvaluationPage instance
   */
  private RiskEvaluationPage riskEvalPage;

  // Instance of NodeAccessRightsPage
  private NodeAccessRightsPage nodeAccessPage;

  /**
   * Work packages page
   */
  private WorkPackagesPage workPackgsPage;


  /**
   * pidc page number
   */
  private int pidcPageIndex = 0;
  /**
   * versions page number
   */
  private int versionsPageIndex = 1;

  /**
   * a2l file page number
   */
  private int a2lPageIndex = 2;

  /**
   * matrix page
   */
  private int matrixPageIndex = 3;
  /**
   * page number with risk eval page
   */
  private int riskPageIndex = 4;
  /**
   * page number with emr page
   */
  private int emrPageIndex = 5;

  /**
   * Work packages page
   */
  private int workPkgsPageIndex = 6;
  /**
   * A2l Resp page index
   */
  private int a2lRespIndex = 7;

  /**
   * COC WP page index
   */
  private int cocWpPageIndex = 8;
  /**
   * rights page number with focus matrix page
   */
  private int rightsPageIndex = 9;
  /**
   * A2l Responsibility Page instance
   */
  private ResponsibilityPage a2lRespPage;


  private PIDCPageCreator pidcOutlinePageCreator;
  /**
   * stores boolean value: true if focus matrix tab is enabled and should be displayed else false
   */
  boolean enableFocusmatrix = true;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    try {
      // Check if Empty risk evaluation sheet to be shown
      boolean enableRm = isPidcRmApplicable(this.pidcVersion.getId());
      // Check if Focus Matrix sheet is to be shown
      this.enableFocusmatrix = isFocusMatrixApplicable(this.pidcVersion.getId());

      // Add pages to the editor form
      this.pidcPage = new PIDCAttrPage(this, this.editorInput.getPidcVersionBO().getPidcDataHandler(),
          this.editorInput.isNotShowWarnDailog());
      this.versionsPage = new PidcVersionsPage(this, this.pidcVersion);
      if (enableRm) {
        this.riskEvalPage = new RiskEvaluationPage(this, this.pidcVersion);
      }
      this.nodeAccessPage = new NodeAccessRightsPage(this, this.editorInput.getNodeAccessBO());
      this.nodeAccessPage.setEditStatus(this);
      A2LFilePage a2lFilePage = new A2LFilePage(this, this.pidcVersion);
      this.pidcCoCWpPage = new PIDCCocWpPage(this);

      FocusMatrixPage matrixPage = null;
      if (this.enableFocusmatrix) {
        matrixPage = new FocusMatrixPage(this);
      }

      this.emrPage = new EMRNatPage(this, this.editorInput);
      this.workPackgsPage = new WorkPackagesPage(this, this.editorInput);
      this.a2lRespPage = new ResponsibilityPage(this, this.editorInput);
      // Add all pages to the editor
      addPage(this.pidcPage);
      addPage(this.versionsPage);
      addPage(a2lFilePage);

      if (this.enableFocusmatrix) {
        addPage(matrixPage);
      }

      if (enableRm) {
        addPage(this.riskEvalPage);
      }

      addPage(this.emrPage);
      addPage(this.workPackgsPage);
      addPage(this.a2lRespPage);
      addPage(this.pidcCoCWpPage);
      addPage(this.nodeAccessPage);

      // Add page index
      int pageIndex = 0;
      this.pidcPageIndex = pageIndex++;
      this.versionsPageIndex = pageIndex++;
      this.a2lPageIndex = pageIndex++;

      if (this.enableFocusmatrix) {
        this.matrixPageIndex = pageIndex++;
      }

      if (enableRm) {
        this.riskPageIndex = pageIndex++;
      }

      this.emrPageIndex = pageIndex++;
      this.workPkgsPageIndex = pageIndex++;
      this.a2lRespIndex = pageIndex++;
      this.cocWpPageIndex = pageIndex++;
      this.rightsPageIndex = pageIndex;
    }
    catch (PartInitException exep) {
      CDMLogger.getInstance().warn(exep.getMessage(), exep, Activator.PLUGIN_ID);
    }
  }

  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
    if (input instanceof PIDCEditorInput) {
      this.editorInput = (PIDCEditorInput) input;
      this.pidcVersion = this.editorInput.getPidcVersionBO().getPidcVersion();

      // ICDM-208
      setEditorPartName(this.pidcVersion.getName());
      // Added for outline view
      this.pidcOutlinePageCreator = new PIDCPageCreator(getEditorInput());
      super.init(site, input);
    }
  }


  /**
   * outline page for pidc editor
   */
  @Override
  public PIDCPageCreator getOutlinePageCreator() {
    OutLineViewDataHandler dataHandler = new OutLineViewDataHandler(this.pidcVersion);
    this.editorInput.setOutlineDataHandler(dataHandler);
    return this.pidcOutlinePageCreator;
  }

  @Override
  public boolean isDirty() {
    return false;
  }

  @Override
  public boolean isSaveAsAllowed() {
    return true;
  }

  @Override
  public void setFocus() {
    this.pidcPage.setStatusBarMessage(false, false);
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
    this.pidcPage.initializeEditorStatusLineManager(this.pidcPage.getPidcAttrTabViewer());
    // initialize the editor statuslinemanager when focus is set to editor
    setStatus();
    this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    // ICDM-796
    // selected PIDC version is valid
    // disable export button if PIDC is deleted
    this.expReportService.setExportService(!this.pidcVersion.isDeleted());
    // iCDM-530
    IViewPart viewPartObj = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView(ApicUiConstants.OUTLINE_TREE_VIEW);

    if (viewPartObj instanceof OutlineViewPart) {
      ((OutlineViewPart) viewPartObj).setTitleTooltip("Filter attributes in Project ID Card");
    }

  }

  /**
   * Sets status according to the page
   */
  public void setStatus() {
    // Set status for respective pages
    if ((getActivePage() == this.versionsPageIndex) || (getActivePage() == this.rightsPageIndex)) {
      final IStatusLineManager statusLine = getEditorSite().getActionBars().getStatusLineManager();
      statusLine.setMessage("");
    }
    else if ((getActivePage() == this.riskPageIndex) && (this.riskEvalPage != null)) {
      this.riskEvalPage.setStatusBarMessage(false);
    }
    else if (getActivePage() == this.emrPageIndex) {
      this.emrPage.setStatusBarMessage(false);
    }
    else if (getActivePage() == this.a2lRespIndex) {
      this.a2lRespPage.setStatusBarMessage(false);
    }
    else if (getActivePage() == this.workPkgsPageIndex) {
      this.workPackgsPage.setStatusBarMessage(false);
    }
    else if (CommonUtils.isEqual(getActivePage(), this.cocWpPageIndex)) {
      this.pidcCoCWpPage.setStatusBarMessage(false);
    }
  }

  /**
   * Updating the status bar
   *
   * @param outlineSelection flag set according to selection made in viewPart or editor.
   * @param totalItemCount total attributes in the table
   * @param filteredItemCount filtered attributes in the table
   */
  public void updateStatusBar(final boolean outlineSelection, final boolean pidcDetailSelection,
      final int totalItemCount, final int filteredItemCount) {

    final StringBuilder buf = new StringBuilder("Displaying : ");
    buf.append(filteredItemCount).append(" out of ").append(totalItemCount);

    IStatusLineManager statusLine;
    IViewPart viewPart = null;
    // Updation of status based on selection in view part
    if (outlineSelection) {
      viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView("com.bosch.caltool.icdm.common.ui.views.OutlineViewPart");
    }
    else if (pidcDetailSelection) {
      viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView(ApicUiConstants.PID_DETAILS_TREE_VIEW);
    }
    if (viewPart != null) {
      final IViewSite viewPartSite = (IViewSite) viewPart.getSite();
      statusLine = viewPartSite.getActionBars().getStatusLineManager();
    } // Updation of status based on selection in editor
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
   * Updating the status bar
   *
   * @param totalItemCount total attributes in the table
   * @param filteredItemCount filtered attributes in the table
   */
  // ICDM-343
  public void updateStatusBar(final int totalItemCount, final int filteredItemCount) {
    // display the total no of attributes along with filtered attributes
    final StringBuilder buf = new StringBuilder("Displaying : ");
    buf.append(filteredItemCount).append(" out of ").append(totalItemCount);
    IStatusLineManager statusLine;
    // Updation of status based on selection in editor
    statusLine = getEditorSite().getActionBars().getStatusLineManager();
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
   * ICDM-865 method to disable the export icon when editor is closed {@inheritDoc}
   */
  @Override
  public boolean isSaveOnCloseNeeded() {
    // Check whether save is required
    this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    // disable the export button
    this.expReportService.setExportService(false);
    return super.isSaveOnCloseNeeded();
  }


  public void setEditorPartName(final String partName) {
    setPartName(partName);
  }


  /**
   * @return the selectionProviderMediator
   */
  public SelectionProviderMediator getSelectionProviderMediator() {
    return this.selectionProviderMediator;
  }


  /**
   * @param selectionProviderMediator the selectionProviderMediator to set
   */
  public void setSelectionProviderMediator(final SelectionProviderMediator selectionProviderMediator) {
    this.selectionProviderMediator = selectionProviderMediator;
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
  public void dispose() {
    // Get active page
    if (null != PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()) {
      IViewReference[] views = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
      for (IViewReference view : views) {
        if (view.getId().equals(ApicUiConstants.PIDC_HISTORY_VIEW_ID) && (null != view.getSecondaryId()) &&
            view.getSecondaryId().equals(this.pidcVersion.getName())) {
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(view);
        }
      }
    }
    super.dispose();
  }


  /**
   * @return PIDCPage
   */
  public PIDCAttrPage getPidcPage() {
    return this.pidcPage;
  }


  /**
   * @return VersionsPage
   */
  public PidcVersionsPage getVersionsPage() {
    return this.versionsPage;
  }

  /**
   * @return the nodeAccessPage
   */
  public NodeAccessRightsPage getNodeAccessPage() {
    return this.nodeAccessPage;
  }

  /**
   * @return the pidcCoCWpPage
   */
  public PIDCCocWpPage getPidcCoCWpPage() {
    return this.pidcCoCWpPage;
  }

  /**
   * Checks if is risk evaluation applicable in case the sheet is empty.
   *
   * @param pidcVersId the pidc vers id
   * @return true, if is risk evaluation applicable
   * @throws ApicWebServiceException the apic web service exception
   */
  private boolean isPidcRmApplicable(final Long pidcVersId) {
    try {
      String canDisplayEmptyRm = new CommonDataBO().getParameterValue(CommonParamKey.RISK_EVALUATION_ENABLED);
      if (CommonUtils.isNotEmptyString(canDisplayEmptyRm) && canDisplayEmptyRm.equals(CommonUtilConstants.CODE_NO)) {
        return !(new PidcRmDefClient().isPidcRmEmpty(pidcVersId));
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(
          "Error ocurred while checking if Risk Evaluation sheet is emtpy : " + e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return true;
  }

  /**
   * Checks if the Focus matrix should be displayed for the PIDC
   *
   * @param PIDC Version ID
   * @return true if Focus Matrix tab is to be shown else false
   */
  private boolean isFocusMatrixApplicable(final Long pidcVersId) {
    try {
      // if FOCUS_MATRIX_ENABLED is 'Y', the focus matrix tab is shown regardless of the focus matrix entries
      // if FOCUS_MATRIX_ENABLED is 'N', the focus matrix tab is to be hidden if there are no attributes for the Focus
      // Matrix and there is only 1 Focus matrix version[Working Set]
      String canDisplayEmptyFm = new CommonDataBO().getParameterValue(CommonParamKey.FOCUS_MATRIX_ENABLED);
      if (CommonUtils.isNotEmptyString(canDisplayEmptyFm) && canDisplayEmptyFm.equals(CommonUtilConstants.CODE_NO)) {
        return !(new FocusMatrixServiceClient().isFocusMatrixEmpty(pidcVersId));
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error("Error ocurred while checking if Focus Matrix sheet is emtpy : " + e.getMessage(),
          e, Activator.PLUGIN_ID);
    }
    return true;
  }


  /**
   * @return the editorInput
   */
  @Override
  public PIDCEditorInput getEditorInput() {
    return this.editorInput;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canEditRights() {
    ApicDataBO apicDataBO = new ApicDataBO();
    // check whether PIDC is unlocked for the session
    if (!apicDataBO.isPidcUnlockedInSession(this.pidcVersion)) {
      PIDCActionSet actionSet = new PIDCActionSet();
      // Show the unlock PIDC dialog
      boolean unlocked = actionSet.showUnlockPidcDialog(this.pidcVersion);
      this.nodeAccessPage.getDataHandler().setCanEditFlag(unlocked);
    }
    return apicDataBO.isPidcUnlockedInSession(this.pidcVersion);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IStructurePageCreator getStrucurePageCreator(final PIDCDetailsViewPart viewPart) {
    // create PIDC details page
    return new PidcDetailsPageCreator(this, viewPart);
  }

  /**
   * Check if the current PIDC page is PIDC Attributes page
   *
   * @return boolean
   */
  public boolean isAttributePage() {
    return (CommonUtils.isEqual(getActivePage(), this.pidcPageIndex));
  }


  /**
   * Check if the current PIDC page is Coc WP page
   *
   * @return boolean
   */
  public boolean isCocWpPage() {
    return (CommonUtils.isEqual(getActivePage(), this.cocWpPageIndex));

  }


  /**
   * @return the cocWpPage
   */
  public PIDCCocWpPage getCocWpPage() {
    return this.pidcCoCWpPage;
  }


  /**
   * @param cocWpPage the cocWpPage to set
   */
  public void setCocWpPage(final PIDCCocWpPage cocWpPage) {
    this.pidcCoCWpPage = cocWpPage;
  }


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }

  /**
   * @return true if focus matrix is enabled else false
   */
  public boolean isFocusmatrixEnabled() {
    return this.enableFocusmatrix;
  }
}
