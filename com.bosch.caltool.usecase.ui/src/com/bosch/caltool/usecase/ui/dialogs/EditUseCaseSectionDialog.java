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

import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseSectionServiceClient;
import com.bosch.caltool.usecase.ui.Activator;

/**
 * This class provides a dialog to edit Attribute Super Group ICDM-299
 */
@SuppressWarnings("unused")
public class EditUseCaseSectionDialog extends AbstractUseCaseDialog {

  /**
   * AttributesOutlinePage instance
   */


  /**
   * UseCaseGroup instance
   */
  private final UseCaseSectionClientBO ucs;


  private final TreeViewer viewer;

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param selectedObject UseCaseSection
   * @param viewer viewer
   */
  public EditUseCaseSectionDialog(final Shell parentShell, final UseCaseSectionClientBO selectedObject,
      final TreeViewer viewer) {
    super(parentShell, true, true);
    this.ucs = selectedObject;
    this.viewer = viewer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Edit UseCase Section");
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
    setTitle("Edit UseCase Section");
    // Set the message
    setMessage("This is to edit UseCase Section Details", IMessageProvider.INFORMATION);
    setExistingValues(this.ucs);
    return contents;
  }

  private void setExistingValues(final UseCaseSectionClientBO ucs2) {

    this.nameEngText.setText(ucs2.getUseCaseSection().getNameEng());
    this.nameGermText.setText(CommonUtils.checkNull(ucs2.getUseCaseSection().getNameGer()));
    this.descEngText.setText(ucs2.getUseCaseSection().getDescEng());
    this.descGermText.setText(CommonUtils.checkNull(ucs2.getUseCaseSection().getDescGer()));
    // ICDM-1558

    this.chkBtnFocusMatrixRelev.setSelection(ucs2.getUseCaseSection().getFocusMatrixYn());

    Set<LinkData> linkDataCollection = new TreeSet<LinkData>();
    LinkServiceClient linkServiceClient = new LinkServiceClient();
    Set<Long> nodesWithLink = null;
    try {
      nodesWithLink = linkServiceClient.getNodesWithLink(MODEL_TYPE.USE_CASE_SECT);
      boolean hasLinks = nodesWithLink.contains(this.ucs.getUseCaseSection().getId());
      if (hasLinks) {
        Map<Long, com.bosch.caltool.icdm.model.general.Link> allLinksByNode = null;
        allLinksByNode =
            linkServiceClient.getAllLinksByNode(this.ucs.getUseCaseSection().getId(), MODEL_TYPE.USE_CASE_SECT);
        for (Link link : allLinksByNode.values()) {
          linkDataCollection.add(new LinkData(link));
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    this.linksTabViewer.setInput(linkDataCollection);
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
   * This method edits a record in TABV_ATTR_SUPER_GROUPS table
   *
   * @param nameEng
   * @param descEng
   * @param nameGer
   * @param descGer
   */
  private void addToCommandStack(final String nameEng, final String descEnglish, final String nameGer,
      final String descGermany) {

    UseCaseSectionServiceClient ucServiceClient = new UseCaseSectionServiceClient();

    UseCaseSection useCaseSection = this.ucs.getUseCaseSection();
    UseCaseSection clonedUseCaseSection = useCaseSection.clone();
    clonedUseCaseSection.setNameEng(nameEng);
    clonedUseCaseSection.setNameGer(nameGer);
    clonedUseCaseSection.setDescEng(descEnglish);
    clonedUseCaseSection.setDescGer(descGermany);
    clonedUseCaseSection.setFocusMatrixYn(this.chkBtnFocusMatrixRelev.getSelection());
    try {
      UseCaseSection updatedUsecaseSection = ucServiceClient.update(clonedUseCaseSection);
      CommonUtils.shallowCopy(useCaseSection, updatedUsecaseSection);
      this.viewer.refresh();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    CommonUiUtils.getInstance().createMultipleLinkService((SortedSet<LinkData>) this.linksTabViewer.getInput(),
        this.ucs.getUseCaseSection().getId(), MODEL_TYPE.USE_CASE_SECT);
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
