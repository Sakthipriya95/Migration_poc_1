package com.bosch.caltool.usecase.ui.dialogs;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseGroupServiceClient;
import com.bosch.caltool.usecase.ui.Activator;

/**
 * This class provides a dialog to edit Attribute Super Group ICDM-297
 */
@SuppressWarnings("unused")
public class EditUseCaseGroupDialog extends AbstractUseCaseDialog {

  /**
   * AttributesOutlinePage instance
   */


  /**
   * UseCaseGroup instance
   */
  private final UseCaseGroupClientBO ucGrp;

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param ucGrp ucGrp
   */
  public EditUseCaseGroupDialog(final Shell parentShell, final UseCaseGroupClientBO ucGrp) {
    super(parentShell, false, false);
    this.ucGrp = ucGrp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Edit UseCase Group");
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
    setTitle("Edit UseCase Group");
    // Set the message
    setMessage("This is to edit UseCase Group Details", IMessageProvider.INFORMATION);
    setExistingValues(this.ucGrp);
    return contents;
  }

  private void setExistingValues(final UseCaseGroupClientBO ucGrp2) {
    this.nameEngText.setText(ucGrp2.getUseCaseGroup().getNameEng());
    this.nameGermText.setText(CommonUtils.checkNull(ucGrp2.getUseCaseGroup().getNameGer()));
    this.descEngText.setText(CommonUtils.checkNull(ucGrp2.getUseCaseGroup().getDescEng()));
    this.descGermText.setText(CommonUtils.checkNull(ucGrp2.getUseCaseGroup().getDescGer()));
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
    UseCaseGroupServiceClient ucGrpServiceClient = new UseCaseGroupServiceClient();
    UseCaseGroup useCaseGroup = this.ucGrp.getUseCaseGroup();

    UseCaseGroup clonedUCGroup = useCaseGroup.clone();
    clonedUCGroup.setNameEng(nameEng);
    clonedUCGroup.setNameGer(nameGer);
    clonedUCGroup.setDescEng(descEnglish);
    clonedUCGroup.setDescGer(descGermany);
    try {
      ucGrpServiceClient.update(clonedUCGroup);
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
