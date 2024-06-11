package com.bosch.caltool.apic.ui.dialogs;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.views.AttributesOutlinePage;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttrGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * This class provides a dialog to edit Attribute Group ICDM-139
 */
public class EditAttributeGroupDialog extends AbstractAttributeGroupDialog {

  /**
   * AttributesPage instance
   */
  private final AttributesOutlinePage attOutlinePage;
  /**
   * AttrGroup instance
   */
  private final AttrGroup attrGroup;

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param attOutlinePage instance
   * @param attrGroup instance
   */
  public EditAttributeGroupDialog(final Shell parentShell, final AttributesOutlinePage attOutlinePage,
      final AttrGroup attrGroup) {
    super(parentShell, true);
    this.attOutlinePage = attOutlinePage;
    this.attrGroup = attrGroup;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Edit Group");
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
    setTitle("Edit Group");
    // Set the message
    setMessage("This is to edit Group Details", IMessageProvider.INFORMATION);
    setExistingValues(this.attrGroup);
    return contents;
  }

  private void setExistingValues(final AttrGroup attrGroup) {
    this.nameEngText.setText(attrGroup.getNameEng());
    this.nameGermText.setText(null == attrGroup.getNameGer() ? "" : attrGroup.getNameGer());
    this.descEngText.setText(null == attrGroup.getDescriptionEng() ? "" : attrGroup.getDescriptionEng());
    this.descGermText.setText(null == attrGroup.getDescriptionGer() ? "" : attrGroup.getDescriptionGer());
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
   * This method edits a record in TABV_ATTR_GROUPS table
   *
   * @param nameEng
   * @param descEng
   * @param nameGer
   * @param descGer
   */
  private void addToCommandStack(final String nameEng, final String descEnglish, final String nameGer,
      final String descGermany) {

    AttrGroup group = setModifiedAttributeDetails(nameEng, descEnglish, nameGer, descGermany);
    AttrGroupServiceClient client = new AttrGroupServiceClient();
    try {

      AttrGroup groupCopy = group.clone();
      CommonUtils.shallowCopy(groupCopy, group);
      AttrGroup updated = client.update(group);
      CommonUtils.shallowCopy(group, updated);

      this.attOutlinePage.getViewer().refresh();

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param nameEng
   * @param descEng
   * @param nameGer
   * @param descGer
   */
  private AttrGroup setModifiedAttributeDetails(final String nameEng, final String descEng, final String nameGer,
      final String descGer) {
    AttrGroup group = this.attrGroup;
    // english name and description are mandatory
    group.setDescriptionEng(descEng);
    // check is made for german name and description for showing the name in
    // the german language prespective //
    if ((descGer != null) && (descGer.trim().length() > 0)) {
      group.setDescriptionGer(descGer);
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
    return group;
  }

  @Override
  public void showSuperGroup(final GridData txtGrid, final Form form, final FormToolkit formToolkit) {

    this.superGrpNameLbl = formToolkit.createLabel(form.getBody(), "Super Group Name");
    this.superGrpNameTxt = formToolkit.createText(form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.superGrpNameTxt.setEnabled(false);
    this.superGrpNameTxt.setLayoutData(txtGrid);
    formToolkit.createLabel(form.getBody(), "");

  }

  /**
   * ICDM-930 creates add new link icon in the toolbar and handles the action
   *
   * @param toolBarManager ToolBarManager
   */
  @Override
  protected void addNewLinkAction(final ToolBarManager toolBarManager) {
    // TODO
  }

  /**
   * ICDM-930 creates delete link icon in the toolbar and handles the action
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
    // TODO
  }

}
