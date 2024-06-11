package com.bosch.caltool.icdm.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.IEditorWithStructure;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;
import com.bosch.caltool.icdm.common.ui.views.IStructurePageCreator;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.ui.editors.pages.A2LBCPage;
import com.bosch.caltool.icdm.ui.editors.pages.A2LBasicInfoPage;
import com.bosch.caltool.icdm.ui.editors.pages.A2LFCPage;
import com.bosch.caltool.icdm.ui.editors.pages.A2LParametersPage;
import com.bosch.caltool.icdm.ui.editors.pages.A2LSysConstPage;
import com.bosch.caltool.icdm.ui.editors.pages.A2lWPDefinitionPage;
import com.bosch.caltool.icdm.ui.editors.pages.WPLabelAssignNatPage;
import com.bosch.caltool.icdm.ui.util.IUIConstants;
import com.bosch.caltool.icdm.ui.views.A2LPageCreator;
import com.bosch.caltool.icdm.ui.views.A2lDetailsPageCreator;
import com.bosch.rcputils.wbutils.WorkbenchUtils;

/**
 * Form Editor class for A2L file contents.
 *
 * @author adn1cob
 */
public class A2LContentsEditor extends AbstractFormEditor implements IEditorWithStructure {


  /**
   * Unique ID for this editor class.
   */
  public static final String PART_ID = "com.bosch.caltool.icdm.ui.editors.A2LContentsEditor";
  /**
   * A2LContentsEditorInput instance
   */
  private A2LContentsEditorInput editorInput;

  private A2LBCPage bcFormPage;
  private A2LFCPage fcFormPage;
  // ICDM-859
  private A2LSysConstPage sysConstNatFormPage;

  private SelectionProviderMediator selProvMdtor = new SelectionProviderMediator();
  private A2LParametersPage prmNatFormPage;

  private A2lWPDefinitionPage a2lWpDefnPage;

  /**
   * @return the prmNatFormPage
   */
  public A2LParametersPage getPrmNatFormPage() {
    return this.prmNatFormPage;
  }

  // ICDM-886
  private A2LFile a2lFile;

  /**
   * CommandState instance
   */
  CommandState expReportService = new CommandState();
  private WPLabelAssignNatPage wpLabelAssignPage;
  private PIDCDetailsViewPart pidcDetailsViewPart;

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
   */
  @Override
  protected void addPages() {
    try {
      // iCDM-1241, a2l file needs to be passed
      final A2LBasicInfoPage a2lInfoFormPage =
          new A2LBasicInfoPage(this, this.editorInput.getA2lFileContents(), this.editorInput.getPidcA2lBO());
      this.bcFormPage = new A2LBCPage(this);
      this.fcFormPage = new A2LFCPage(this);
      // ICDM-859
      this.prmNatFormPage = new A2LParametersPage(this);
      this.sysConstNatFormPage = new A2LSysConstPage(this);

      // create wp defn page
      this.a2lWpDefnPage = new A2lWPDefinitionPage(this, this.editorInput.getA2lWPInfoBO());

      this.wpLabelAssignPage = new WPLabelAssignNatPage(this, this.editorInput.getA2lWPInfoBO());
      // Add pages to the editor form
      addPage(a2lInfoFormPage);
      // iCDM-360, modified constructor
      addPage(this.bcFormPage);
      // Pal Tool
      addPage(this.a2lWpDefnPage);
      addPage(this.wpLabelAssignPage);

      addPage(this.fcFormPage);
      // Commenting as part of ICDM-859
      addPage(this.prmNatFormPage);
      addPage(this.sysConstNatFormPage);
    }
    catch (PartInitException exp) {
      CDMLogger.getInstance().warn(exp.getMessage(), exp);
    }
  }


  /**
   * @param newPageIndex Page Index
   */
  public void createPage(final int newPageIndex) {
    pageChange(newPageIndex);
  }

  @Override
  public void doSave(final IProgressMonitor monitor) {
    // Not applicable
  }

  @Override
  public void doSaveAs() {
    // Not applicable
  }

  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
    this.editorInput = (A2LContentsEditorInput) input;
    this.a2lFile = this.editorInput.getA2lFile();
    setPartName(input.getName());
    super.init(site, this.editorInput);
    createDetailsViewPart();
  }


  /**
   * @throws PartInitException
   */
  private final void createDetailsViewPart() throws PartInitException {

    this.pidcDetailsViewPart = (PIDCDetailsViewPart) WorkbenchUtils.getView(PIDCDetailsViewPart.VIEW_ID);
    if (this.pidcDetailsViewPart == null) {
      // Outline view should know what pages to show in outline
      // if the outline view is closed open it progamatically
      // Then open Outline programatically
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(PIDCDetailsViewPart.VIEW_ID);
      this.pidcDetailsViewPart = (PIDCDetailsViewPart) WorkbenchUtils.getView(PIDCDetailsViewPart.VIEW_ID);
    }
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(PIDCDetailsViewPart.VIEW_ID);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected IPageCreator getOutlinePageCreator() {
    return new A2LPageCreator(this.editorInput);
  }

  @Override
  public boolean isDirty() {
    return false;
  }

  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
    this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    // excel export should be enabled only if variant group or default variant group is selected and WP Label Assignment
    // Page is active
    this.expReportService.setExportService(this.wpLabelAssignPage.isActive());
    setStatus();
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();

    // icdm-530
    IViewPart viewPartObj =
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(IUIConstants.OUTLINE_TREE_VIEW);
    if ((null != viewPartObj) && (viewPartObj instanceof OutlineViewPart)) {
      ((OutlineViewPart) viewPartObj).setTitleTooltip("Filter Base Components, Functions, Work packages in A2L Editor");
    }
  }

  /**
   * Status of the focussed page is set
   */
  // ICDM-343
  public void setStatus() {
    if (getActivePage() == 0) {
      final IStatusLineManager statusLine = getEditorSite().getActionBars().getStatusLineManager();
      statusLine.setMessage("");
    }
    if (getActivePage() == 1) {
      this.bcFormPage.setStatusBarMessage(false);
    }
    else if (getActivePage() == 2) {
      this.a2lWpDefnPage.setStatusBarMessage(false);
    }
    else if (getActivePage() == 3) {
      this.wpLabelAssignPage.setStatusBarMessage(this.wpLabelAssignPage.getGroupByHeaderLayer(), false);
    }
    else if (getActivePage() == 4) {
      this.fcFormPage.setStatusBarMessage(false);
    }
    // ICDM-859
    else if (getActivePage() == 5) {
      this.prmNatFormPage.setStatusBarMessage(this.prmNatFormPage.getGroupByHeaderLayer(), false);
    }
    else if (getActivePage() == 6) {
      this.sysConstNatFormPage.setStatusBarMessage(false);
    }
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
          .findView(IUIConstants.OUTLINE_TREE_VIEW).getSite();
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
   * @return the selectionProviderMediator
   */
  public SelectionProviderMediator getSelectionProviderMediator() {
    return this.selProvMdtor;
  }


  /**
   * @param selProvMedtor the selectionProviderMediator to set
   */
  public void setSelectionProviderMediator(final SelectionProviderMediator selProvMedtor) {
    this.selProvMdtor = selProvMedtor;
  }

  /**
   * ICDM-886
   *
   * @return the a2lFile
   */
  public A2LFile getA2lFile() {
    return this.a2lFile;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public IStructurePageCreator getStrucurePageCreator(final PIDCDetailsViewPart viewPart) {
    return new A2lDetailsPageCreator(this, viewPart);
  }

}
