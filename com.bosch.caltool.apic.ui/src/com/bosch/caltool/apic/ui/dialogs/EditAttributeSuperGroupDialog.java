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
import com.bosch.caltool.apic.ui.views.AttributesOutlinePage;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeSuperGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * This class provides a dialog to edit Attribute Super Group ICDM-139
 */
public class EditAttributeSuperGroupDialog extends AbstractAttributeGroupDialog {

  /**
   * AttributesOutlinePage instance
   */

  private final AttributesOutlinePage attOutlinePage;

  /**
   * AttrSuperGroup instance
   */
  private final AttrSuperGroup attrSuperGroup;

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param attOutlinePage instance
   * @param attrSuperGroup instance
   */
  public EditAttributeSuperGroupDialog(final Shell parentShell, final AttributesOutlinePage attOutlinePage,
      final AttrSuperGroup attrSuperGroup) {
    super(parentShell, true);
    this.attOutlinePage = attOutlinePage;
    this.attrSuperGroup = attrSuperGroup;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Edit Super Group");
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
    setTitle("Edit Super Group");
    // Set the message
    setMessage("This is to edit Super Group Details", IMessageProvider.INFORMATION);
    setExistingValues(this.attrSuperGroup);
    return contents;
  }

  private void setExistingValues(final AttrSuperGroup attrSuperGroup) {


    this.nameEngText.setText(attrSuperGroup.getNameEng());
    this.nameGermText.setText(null == attrSuperGroup.getNameGer() ? "" : attrSuperGroup.getNameGer());
    this.descEngText.setText(null == attrSuperGroup.getDescriptionEng() ? "" : attrSuperGroup.getDescriptionEng());
    this.descGermText.setText(null == attrSuperGroup.getDescriptionGer() ? "" : attrSuperGroup.getDescriptionGer());
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
    AttrSuperGroup spGrp = this.attrSuperGroup;
    // english name and description are mandatory
    spGrp.setDescriptionEng(descEnglish);
    // check is made for german name and description for showing the name in the german language prespective
    if ((descGermany != null) && (descGermany.trim().length() > 0)) {
      spGrp.setDescriptionGer(descGermany);
    }
    else {
      spGrp.setDescriptionGer(null);
    }
    spGrp.setNameEng(nameEng);
    if ((nameGer != null) && (nameGer.trim().length() > 0)) {
      spGrp.setNameGer(nameGer);
    }
    else {
      spGrp.setNameGer(null);
    }

    AttributeSuperGroupServiceClient client = new AttributeSuperGroupServiceClient();
    try {
      AttrSuperGroup spGrpCopy = spGrp.clone();
      CommonUtils.shallowCopy(spGrpCopy, spGrp);
      AttrSuperGroup updated = client.update(spGrp);
      CommonUtils.shallowCopy(spGrp, updated);
      this.attOutlinePage.getViewer().refresh();

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

  }

  @Override
  public void showSuperGroup(final GridData txtGrid, final Form form, final FormToolkit formToolkit2) {
    // No implementation here
  }

  /**
   * ICDM-929 creates add new link icon in the toolbar and handles the action
   *
   * @param toolBarManager ToolBarManager
   */
  @Override
  protected void addNewLinkAction(final ToolBarManager toolBarManager) {
    // TODO
  }

  /**
   * ICDM-929 creates delete link icon in the toolbar and handles the action
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
