/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.Set;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.DeletionActions;
import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttrNValueDependencyServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * This class provides UI to add attributes dependency information
 */
public class EditAttributeDepnDialog extends AttributeDepnDialog {

  // Instance of selected attr dependency
  private final AttrNValueDependency selectedAttrDep;
  // Instance of attributes page
  private final AttributesPage attributesPage;


  /**
   * The Parameterized Constructor
   *
   * @param parentShell instance
   * @param attributesPage instance
   */

  public EditAttributeDepnDialog(final Shell parentShell, final AttributesPage attributesPage) {
    super(parentShell, attributesPage.getAttrHandler().getSelectedAttributes());
    this.selectedAttrDep = attributesPage.getAttrHandler().getSelectedAttrDep();
    this.attributesPage = attributesPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // set the shell title
    newShell.setText("Edit Attribute Dependency");
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
    Control contents = super.createContents(parent);

    // Set the title
    setTitle("Edit Attribute Dependency");

    // Set the message
    setMessage("This is to edit an attribute dependency", IMessageProvider.INFORMATION);
    // Set the existing values
    setExistingValues(this.selectedAttrDep);
    return contents;
  }

  /**
   * @param selectedAttrDep
   */
  private void setExistingValues(final AttrNValueDependency selectedAttrDep) {
    if (selectedAttrDep != null) {
      String[] items = this.comboDepnAttrName.getItems();
      for (String item : items) {
        if (item.equalsIgnoreCase(selectedAttrDep.getName())) {
          int index = this.comboDepnAttrName.indexOf(item);
          this.comboDepnAttrName.select(index);
          this.comboDepnAttrName.setEnabled(false); // ICDM-119
          this.comboDepnAttrVal.setFocus(); // ICDM-183
          break;
        }
      }
      // get selected value index and set the appropriate value
      int index = this.comboDepnAttrName.getSelectionIndex();
      this.selDepnAttrName = this.comboDepnAttrName.getItem(index);
      boolean attrSet = false;
      for (com.bosch.caltool.icdm.model.apic.attr.Attribute attribute : this.attributes) {
        if (attribute.getName().equalsIgnoreCase(this.selDepnAttrName)) {
          attrSet = true;
          setValues(attribute);
          break;
        }
      }
      if (!attrSet) {
        this.comboDepnAttrVal.removeAll();
        this.comboDepnAttrVal.add(NO_ACTIVE_VALS_ARE_AVAILABLE);
        this.comboDepnAttrVal.select(0);
      }
      this.comboDepnAttrVal.select(this.comboDepnAttrVal.indexOf(selectedAttrDep.getValue()));
      // set existing comment text
      this.comment.setText(selectedAttrDep.getChangeComment() == null ? "" : selectedAttrDep.getChangeComment());
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    int attrIndex = this.comboDepnAttrName.getSelectionIndex();
    String attr = this.comboDepnAttrName.getItem(attrIndex);
    String changeComment = this.comment.getText();
    com.bosch.caltool.icdm.model.apic.attr.Attribute selectedDepnAttr = null;
    for (com.bosch.caltool.icdm.model.apic.attr.Attribute attribute : this.attributes) {
      if (attr.equalsIgnoreCase(attribute.getName())) {
        selectedDepnAttr = attribute;
        break;
      }
    }
    int attrValIndex = this.comboDepnAttrVal.getSelectionIndex();
    String selDepnAttrVal = this.comboDepnAttrVal.getItem(attrValIndex);
    Set<AttributeValue> listAttrVals = null;
    if (selectedDepnAttr != null) {
      listAttrVals = new AttributeClientBO(selectedDepnAttr).getAttrValues();
    }
    AttributeValue dependAttrVal = null;
    if ((listAttrVals != null) && !"No active values are avaliable!".equalsIgnoreCase(selDepnAttrVal) &&
        !selDepnAttrVal.equalsIgnoreCase(ApicConstants.DEFAULT_COMBO_SELECT)) {
      // Default field selected
      for (AttributeValue attributeValue : listAttrVals) {
        if (selDepnAttrVal.equalsIgnoreCase(attributeValue.getName())) {
          dependAttrVal = attributeValue;
          break;
        }
      }

    }
    // update
    // Add commands
    addToCommandStack(this.selectedAttr.get(0), selectedDepnAttr, dependAttrVal, changeComment);
    super.okPressed();
  }

  /**
   * @param selAttr
   * @param selectedDepnAttr
   * @param dependAttrVal
   * @param changeComment
   */
  private void addToCommandStack(final Attribute selAttr, final Attribute selectedDepnAttr,
      final AttributeValue dependAttrVal, final String changeComment) {
    if (null != selectedDepnAttr) {

      AttrNValueDependencyServiceClient client = new AttrNValueDependencyServiceClient();
      try {
        final Long depenAttrID = selectedDepnAttr.getId();

        this.selectedAttrDep.setDependentAttrId(depenAttrID);
        // Populate the dependent ValueID, handled ICDM-93
        if (null != dependAttrVal) {
          this.selectedAttrDep.setDependentValueId(dependAttrVal.getId());
        }
        else {
          this.selectedAttrDep.setDependentValueId(null);
        }
        this.selectedAttrDep.setChangeComment(changeComment);
        client.update(this.selectedAttrDep);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
      AttributeClientBO attrBO = new AttributeClientBO(selAttr);
      this.attributesPage.getAttrDepTabViewer().setInput(attrBO.getAttrDependencies(true));
      // ICDM-119
      // if dependent attribute value is null this means that the dependency is based on used flag.
      // In this case the add attribute action should be disabled since only one (not deleted) dependency can exist
      // for such an attribute
      if (null == dependAttrVal) {
        final DeletionActions delAction = new DeletionActions(this.attributesPage);
        // delete existing dependencies
        delAction.deleteExistingDepWithValues(selAttr);
        this.attributesPage.getActionSet().getAddAttrDepAction().setEnabled(false);
      }
      else {
        this.attributesPage.getActionSet().getAddAttrDepAction().setEnabled(true);
      }


    }
  }
}
