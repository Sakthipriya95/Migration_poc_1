package com.bosch.caltool.usecase.ui.dialogs;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseGroupServiceClient;
import com.bosch.caltool.usecase.ui.Activator;

/**
 * This class provides a dialog to add a new Super Group ICDM-297
 */
@SuppressWarnings("unused")
public class AddUseCaseGroupDialog extends AbstractUseCaseDialog {

  /**
   * UseCaseGroupClientBO
   */
  private final UseCaseGroupClientBO ucGrp;

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param ucGrp item
   */
  public AddUseCaseGroupDialog(final Shell parentShell, final UseCaseGroupClientBO ucGrp) {
    super(parentShell, false, false);
    this.ucGrp = ucGrp;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Add UseCase Group");
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
    setTitle("Add UseCase Group");
    // Set the message
    setMessage("Enter UseCase Group Details", IMessageProvider.INFORMATION);
    return contents;
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
   * This method creates a new record in TABV_ATTR_SUPER_GROUPS table
   *
   * @param nameEng
   * @param descEng
   * @param nameGer
   * @param descGer
   */
  private void addToCommandStack(final String nameEng, final String descEnglish, final String nameGer,
      final String descGermany) {

    UseCaseGroup useCaseGroup = new UseCaseGroup();
    useCaseGroup.setNameEng(nameEng);
    useCaseGroup.setNameGer(nameGer);
    useCaseGroup.setDescEng(descEnglish);
    useCaseGroup.setDescGer(descGermany);
    Long parentId = null;
    if (null != this.ucGrp) {
      parentId = this.ucGrp.getUseCaseGroup().getId();
      useCaseGroup.setParentGroupId(parentId);
    }
    UseCaseGroupServiceClient ucGrpServiceClient = new UseCaseGroupServiceClient();
    try {
      ucGrpServiceClient.create(useCaseGroup);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addNewLinkAction(final ToolBarManager toolBarManager) {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addDeleteLinkActionToSection(final ToolBarManager toolBarManager) {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addEditLinkAction(final ToolBarManager toolBarManager) {
    // Not applicable

  }


}