package com.bosch.caltool.apic.ui.dialogs;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeSuperGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * This class provides a dialog to add a new Super Group ICDM-139
 */
public class AddAttributeSuperGroupDialog extends AbstractAttributeGroupDialog {

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param attOutlinePage instance
   */
  public AddAttributeSuperGroupDialog(final Shell parentShell) {
    super(parentShell, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Add Super Group");
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
    setTitle("Add Super Group");
    // Set the message
    setMessage("Enter Super Group Details", IMessageProvider.INFORMATION);
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
    AttrSuperGroup superGrp = new AttrSuperGroup();
    // english name and description are mandatory
    superGrp.setDescriptionEng(descEnglish);
    // check is made for german name and description for showing the name in the german language prespective //
    if ((descGermany != null) && (descGermany.trim().length() > 0)) {
      superGrp.setDescriptionGer(descGermany);
    }
    else {
      superGrp.setDescriptionGer(null);
    }
    superGrp.setNameEng(nameEng);
    if ((nameGer != null) && (nameGer.trim().length() > 0)) {
      superGrp.setNameGer(nameGer);
    }
    else {
      superGrp.setNameGer(null);
    }
    AttributeSuperGroupServiceClient client = new AttributeSuperGroupServiceClient();
    try {
      client.create(superGrp);

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

  }

  @Override
  public void showSuperGroup(final GridData txtGrid, final Form form, final FormToolkit formToolkit) {
    // Not applicable
  }

  /**
   * ICDM-929 {@inheritDoc}
   */
  @Override
  protected void addNewLinkAction(final ToolBarManager toolBarManager) {
    // Not applicable
  }

  /**
   * ICDM-929 {@inheritDoc}
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