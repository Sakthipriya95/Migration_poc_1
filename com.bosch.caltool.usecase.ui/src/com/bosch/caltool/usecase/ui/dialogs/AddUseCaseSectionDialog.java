package com.bosch.caltool.usecase.ui.dialogs;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseSectionServiceClient;
import com.bosch.caltool.usecase.ui.Activator;

/**
 * This class provides a dialog to add a new Super Group ICDM-299
 */
@SuppressWarnings("unused")
public class AddUseCaseSectionDialog extends AbstractUseCaseDialog {


  /**
   * UsecaseClientBO
   */
  private final UsecaseClientBO useCase;
  /**
   * UseCaseSectionClientBO
   */
  private final UseCaseSectionClientBO useCaseSection;

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param item item
   * @param viewer viewer
   * @param usecaseEditorModel UsecaseEditorModel
   * @param usecaseBO UsecaseClientBO
   */
  public AddUseCaseSectionDialog(final Shell parentShell, final UseCaseSectionClientBO item,
      final UsecaseClientBO usecaseBO) {
    super(parentShell, false, true);
    this.useCaseSection = item;
    this.useCase = usecaseBO;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Add UseCase Section");
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
    setTitle("Add UseCase Section");
    // Set the message
    setMessage("Enter UseCase Section Details", IMessageProvider.INFORMATION);
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


    UseCaseSectionServiceClient ucsServiceClient = new UseCaseSectionServiceClient();
    UseCaseSection usecaseSection = new UseCaseSection();
    usecaseSection.setNameEng(nameEng);
    usecaseSection.setNameGer(nameGer);
    usecaseSection.setDescEng(descEnglish);
    usecaseSection.setDescGer(descGermany);
    usecaseSection.setFocusMatrixYn(this.chkBtnFocusMatrixRelev.getSelection());
    usecaseSection.setUseCaseId(this.useCase.getID());
    if (this.useCaseSection != null) {
      usecaseSection.setParentSectionId(this.useCaseSection.getID());
    }


    try {
      ucsServiceClient.create(usecaseSection);
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