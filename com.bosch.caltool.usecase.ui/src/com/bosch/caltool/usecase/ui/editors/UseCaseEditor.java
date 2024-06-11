package com.bosch.caltool.usecase.ui.editors;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;

import com.bosch.caltool.apic.ui.editors.pages.NodeAccessRightsPage;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.usecase.ui.Activator;
import com.bosch.caltool.usecase.ui.editors.pages.UseCaseNatAttributesPage;
import com.bosch.caltool.usecase.ui.util.IUIConstants;
import com.bosch.caltool.usecase.ui.views.UseCaseOutlinePageCreator;


/**
 * @author adn1cob
 */
public class UseCaseEditor extends AbstractFormEditor {

  /**
   * Defines UseCaseEditor id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.usecase.ui.editors.UseCaseEditor";

  private UseCaseNatAttributesPage natAttrPage;
  private NodeAccessRightsPage nodeAccessRightsPage;
  private final SelectionProviderMediator selectionProviderMediator = new SelectionProviderMediator();

  // ICDM-796
  /*
   * CommandState instance
   */
  private CommandState expReportService = new CommandState();


  /**
   * @return the natAttrPage
   */
  public UseCaseNatAttributesPage getNatAttrPage() {
    return this.natAttrPage;
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

  /**
   * @return NodeAccessRightsPage
   */
  public NodeAccessRightsPage getRightsPage() {
    return this.nodeAccessRightsPage;
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
    if (input instanceof UseCaseEditorInput) {
      final UsecaseClientBO selUseCase = ((UseCaseEditorInput) input).getSelectedUseCase();
      setPartName(selUseCase.getName());

      super.init(site, input);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IPageCreator getOutlinePageCreator() {
   OutLineViewDataHandler dataHandler = new OutLineViewDataHandler(null,getEditorInput().getAttrGroupModel());
    getEditorInput().setOutlineDataHandler(dataHandler);
    return new UseCaseOutlinePageCreator(getEditorInput());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UseCaseEditorInput getEditorInput() {
    return (UseCaseEditorInput) super.getEditorInput();
  }

  @Override
  public boolean isDirty() {
    // Not applicable
    return false;
  }

  @Override
  public boolean isSaveAsAllowed() {
    // Not applicable
    return false;
  }


  @Override
  public void setFocus() {
    // To prevent
    // "java.lang.RuntimeException: WARNING: Prevented recursive attempt to activate part
    // org.eclipse.ui.views.PropertySheet while still in the middle of activating part"
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
    // ICDM-796
    // ICDM-865
    this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    this.expReportService.setExportService(true);

    if (getActivePage() == 0) {
      this.natAttrPage.setStatusBarMessage(false, false);
    }
    // iCDM-530
    IViewPart viewPartObj =
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(IUIConstants.OUTLINE_TREE_VIEW);
    if ((viewPartObj instanceof OutlineViewPart)) {
      ((OutlineViewPart) viewPartObj).setTitleTooltip("Filter use case attributes on selection");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    try {
      this.natAttrPage = new UseCaseNatAttributesPage(this, getEditorInput().getSelectedUseCase());
      this.nodeAccessRightsPage = new NodeAccessRightsPage(this, getEditorInput().getNodeAccessBO());
      addPage(this.natAttrPage);
      addPage(this.nodeAccessRightsPage);
    }
    catch (PartInitException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param partName part name
   */
  public void setEditorPartName(final String partName) {
    super.setPartName(partName);
  }

  /**
   * @return the selectionProviderMediator
   */
  public SelectionProviderMediator getSelectionProviderMediator() {
    return this.selectionProviderMediator;
  }

  /**
   * @param outlineSelection
   * @param useCaseDetailsSelection
   * @param totalItemCount
   * @param filteredItemCount
   */
  public void updateStatusBar(final boolean outlineSelection, final boolean useCaseDetailsSelection,
      final int totalItemCount, final int filteredItemCount) {

    final StringBuilder buf = new StringBuilder("Displaying : ");
    buf.append(filteredItemCount).append(" out of ").append(totalItemCount).append(" records ");
    IStatusLineManager statusLine;
    // Updation of status based on selection in view part
    if (outlineSelection) {
      final IViewSite viewPartSite = (IViewSite) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView(IUIConstants.OUTLINE_TREE_VIEW).getSite();
      statusLine = viewPartSite.getActionBars().getStatusLineManager();
    }
    else if (useCaseDetailsSelection) {
      final IViewSite viewPartSite = (IViewSite) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView(IUIConstants.USECASE_DETAILS_TREE_VIEW).getSite();
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
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return getEditorInput();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    Map<IModelType, Map<Long, ChangeData<?>>> consChangeData = dce.getConsChangeData();
    for (Entry<IModelType, Map<Long, ChangeData<?>>> changedDataMap : consChangeData.entrySet()) {
      if (changedDataMap.getKey() == MODEL_TYPE.USE_CASE) {
        // if there is a change with usecase
        final UsecaseClientBO selUseCase = getEditorInput().getSelectedUseCase();
        String name = selUseCase.getName();
        setPartName(name);
        Form partControl = (Form) this.nodeAccessRightsPage.getPartControl();
        if (null != partControl) {
          partControl.setText(com.bosch.caltool.icdm.common.util.CommonUtils.concatenate(name, " - ", "Access Rights"));
        }
      }
    }
  }

}
