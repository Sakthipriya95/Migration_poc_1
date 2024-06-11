package com.bosch.caltool.usecase.ui.dialogs;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseServiceClient;
import com.bosch.caltool.usecase.ui.Activator;

/**
 * This class provides a dialog to edit Attribute Super Group ICDM-298
 */
@SuppressWarnings("unused")
public class EditUseCaseDialog extends AbstractUseCaseDialog {

  /**
   * AttributesOutlinePage instance
   */


  /**
   * UseCaseGroup instance
   */
  private final UsecaseClientBO useCaseClientBO;


  private final TreeViewer viewer;


  private SortedSet<LinkData> linkDataCollection;

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param selectedObject UsecaseClientBO
   * @param viewer viewer
   */
  public EditUseCaseDialog(final Shell parentShell, final UsecaseClientBO selectedObject, final TreeViewer viewer) {
    super(parentShell, true, true);
    this.useCaseClientBO = selectedObject;
    this.viewer = viewer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Edit Use Case");
    super.configureShell(newShell);
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);

    // Set the title
    setTitle("Edit Use Case");
    // Set the message
    setMessage("This is to edit Use Case Details", IMessageProvider.INFORMATION);
    setExistingValues(this.useCaseClientBO);
    return contents;
  }

  private void setExistingValues(final UsecaseClientBO useCaseBO) {

    this.nameEngText.setText(useCaseBO.getUseCase().getNameEng());
    this.nameGermText.setText(CommonUtils.checkNull(useCaseBO.getUseCase().getNameGer()));
    this.descEngText.setText(useCaseBO.getUseCase().getDescEng());
    this.descGermText.setText(CommonUtils.checkNull(useCaseBO.getUseCase().getDescGer()));

    // ICDM-1558

    this.chkBtnFocusMatrixRelev.setSelection(useCaseBO.getUseCase().getFocusMatrixYn());


    this.linkDataCollection = new TreeSet<LinkData>();
    LinkServiceClient linkServiceClient = new LinkServiceClient();
    Set<Long> nodesWithLink = null;
    try {
      nodesWithLink = linkServiceClient.getNodesWithLink(MODEL_TYPE.USE_CASE);
      boolean hasLinks = nodesWithLink.contains(useCaseBO.getUseCase().getId());
      if (hasLinks) {
        Map<Long, com.bosch.caltool.icdm.model.general.Link> allLinksByNode = null;
        allLinksByNode = linkServiceClient.getAllLinksByNode(useCaseBO.getUseCase().getId(), MODEL_TYPE.USE_CASE);
        for (Link link : allLinksByNode.values()) {
          this.linkDataCollection.add(new LinkData(link));
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    this.linksTabViewer.setInput(this.linkDataCollection);
    this.linksTabViewer.refresh();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    final String nameEng = this.nameEngText.getText().trim();
    final String nameGer = this.nameGermText.getText().trim();
    final String descEnglish = this.descEngText.getText().trim();
    final String descGermany = this.descGermText.getText().trim();

    addToCommandStack(nameEng, descEnglish, nameGer, descGermany);
    super.okPressed();
  }

  /**
   * This method edits a record in TABV_USE_CASES table
   *
   * @param nameEng
   * @param descEng
   * @param nameGer
   * @param descGer
   */
  private void addToCommandStack(final String nameEng, final String descEnglish, final String nameGer,
      final String descGermany) {
    UseCaseServiceClient ucServiceClient = new UseCaseServiceClient();
    UseCase useCase = this.useCaseClientBO.getUseCase();

    UseCase clonedUseCase = useCase.clone();
    clonedUseCase.setNameEng(nameEng);
    clonedUseCase.setNameGer(nameGer);
    clonedUseCase.setDescEng(descEnglish);
    clonedUseCase.setDescGer(descGermany);
    clonedUseCase.setFocusMatrixYn(this.chkBtnFocusMatrixRelev.getSelection());
    try {
      UseCase updatedUsecase = ucServiceClient.update(clonedUseCase);
      CommonUtils.shallowCopy(useCase, updatedUsecase);
      this.viewer.refresh();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    CommonUiUtils.getInstance().createMultipleLinkService((SortedSet<LinkData>) this.linksTabViewer.getInput(),
        this.useCaseClientBO.getUseCase().getId(), MODEL_TYPE.USE_CASE);
    this.linksTabViewer.refresh();
  }


  /**
   * creates add new link icon in the toolbar and handles the action
   *
   * @param toolBarManager ToolBarManager
   */
  @Override
  protected void addNewLinkAction(final ToolBarManager toolBarManager) {
    CommonActionSet cmnActionSet = new CommonActionSet();

    // Create an action to add new link
    this.newLinkAction = cmnActionSet.addNewLinkAction(this.linksTabViewer);
    toolBarManager.add(this.newLinkAction);
  }

  /**
   * creates delete link icon in the toolbar and handles the action
   *
   * @param toolBarManager ToolBarManager
   */
  @Override
  protected void addDeleteLinkActionToSection(final ToolBarManager toolBarManager) {
    CommonActionSet cmnActionSet = new CommonActionSet();
    // Create an action to delete the link
    this.deleteLinkAction =
        cmnActionSet.addDeleteLinkActionToSection(toolBarManager, this.linksTabViewer, this.editLinkAction);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addEditLinkAction(final ToolBarManager toolBarManager) {

    CommonActionSet cmnActionSet = new CommonActionSet();
    // Create an action to add new link
    this.editLinkAction = cmnActionSet.addEditLinkAction(toolBarManager, this.linksTabViewer);


  }

  /**
   * Validates save button enable or disable
   */
  @Override
  protected void checkSaveBtnEnable() {
    super.checkSaveBtnEnable();

    if (this.saveBtn != null) {
      this.saveBtn.setEnabled(this.saveBtn.getEnabled() || this.linksChanged);
    }
  }
}
