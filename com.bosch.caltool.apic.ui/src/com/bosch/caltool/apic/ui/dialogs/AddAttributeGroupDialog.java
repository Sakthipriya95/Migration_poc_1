package com.bosch.caltool.apic.ui.dialogs;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.views.AttributesOutlinePage;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttrGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * This class provides a dialog to add a new Attribute Group ICDM-139
 */
public class AddAttributeGroupDialog extends AbstractAttributeGroupDialog {

  /**
   * AttributesPage instance
   */
  private final AttributesOutlinePage attOutlinePage;
  private final AttrSuperGroup attrSuperGroup;

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param attOutlinePage instance
   * @param attrSuperGroup instance
   * @param attrRootNode AttrRootNode
   * @param ucDataHandler UseCaseDataHandler
   */
  public AddAttributeGroupDialog(final Shell parentShell, final AttributesOutlinePage attOutlinePage,
      final com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup attrSuperGroup) {
    super(parentShell, false);
    this.attOutlinePage = attOutlinePage;
    this.attrSuperGroup = attrSuperGroup;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Add Group");
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
    setTitle("Add Group");
    // Set the message
    setMessage("Enter Group Details", IMessageProvider.INFORMATION);
    setExistingValues(this.attrSuperGroup);
    return contents;
  }

  private void setExistingValues(final AttrSuperGroup attrSuperGroup) {

    this.superGrpNameLbl.setVisible(true);
    this.superGrpNameTxt.setVisible(true);
    this.superGrpNameTxt.setText(attrSuperGroup.getName());

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
    addToCommandStack(nameEng, descEnglish, nameGer, descGermany, this.attrSuperGroup);
    super.okPressed();
  }

  /**
   * This method creates a new record in TABV_ATTR_GROUPS table
   *
   * @param nameEng
   * @param descEng
   * @param nameGer
   * @param descGer
   */
  private void addToCommandStack(final String nameEng, final String descEnglish, final String nameGer,
      final String descGermany, final AttrSuperGroup attrSpGrp) {
    AttrGroup group = new AttrGroup();
    // english name and description are mandatory
    group.setDescriptionEng(descEnglish);
    // check is made for german name and description for showing the name in
    // the german language prespective //
    if ((descGermany != null) && (descGermany.trim().length() > 0)) {
      group.setDescriptionGer(descGermany);
    }
    else {
      group.setDescriptionGer(null);
    }
    group.setNameEng(nameEng);
    if ((nameGer != null) && (nameGer.trim().length() > 0)) {
      group.setNameGer(nameGer);
    }
    else {
      group.setNameGer(null);
    }
    group.setSuperGrpId(attrSpGrp.getId());
    AttrGroupServiceClient client = new AttrGroupServiceClient();
    try {
      client.create(group);

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Overriden Method to Show the Super Group Name
   */
  @Override
  public void showSuperGroup(final GridData txtGrid, final Form form, final FormToolkit formToolkit) {
    this.superGrpNameLbl = formToolkit.createLabel(form.getBody(), "Super Group Name");
    this.superGrpNameTxt = formToolkit.createText(form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.superGrpNameTxt.setEnabled(false);
    this.superGrpNameTxt.setLayoutData(txtGrid);
    formToolkit.createLabel(form.getBody(), "");

  }

  /**
   * This method returns the selected element in the tree
   *
   * @return AttrSuperGroup
   */
  protected AttrSuperGroup getSelSuperGrpValFromTabViewer() {
    AttrSuperGroup attrSuperGrp = null;
    final IStructuredSelection selection = (IStructuredSelection) this.attOutlinePage.getViewer().getSelection();
    if ((selection != null) && (selection.size() != 0)) {
      final Object element = selection.getFirstElement();
      if (element instanceof AttrSuperGroup) {
        attrSuperGrp = (AttrSuperGroup) element;
      }
    }
    return attrSuperGrp;
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-929
  @Override
  protected void addNewLinkAction(final ToolBarManager toolBarManager) {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-929
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