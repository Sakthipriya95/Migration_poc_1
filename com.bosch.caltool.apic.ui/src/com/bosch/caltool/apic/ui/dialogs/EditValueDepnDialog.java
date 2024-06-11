/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.List;
import java.util.SortedSet;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.DeletionActions;
import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttrNValueDependencyServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * This class provides UI to edit value dependency
 */

public class EditValueDepnDialog extends ValueDepnDialog {

  /**
   * selected attribute value dependency
   */
  private final AttrNValueDependency selectedAttrValueDep;

  /**
   * AttributesPage
   */
  private final AttributesPage attributesPage;

  /**
   * list of attribute value
   */
  private final List<AttributeValue> selectedVal;

  /**
   * The Parameterized Constructor
   *
   * @param parentShell instance
   * @param attributesPage instance
   */
  public EditValueDepnDialog(final Shell parentShell, final AttributesPage attributesPage) {
    super(parentShell, attributesPage);
    this.selectedAttrValueDep = attributesPage.getAttrHandler().getSelectedAttrValDep();
    this.attributesPage = attributesPage;
    this.selectedVal = attributesPage.getAttrHandler().getSelectedValues();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Edit Value Dependency");
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
    setTitle("Edit Value Dependency");

    // Set the message
    setMessage("This is to edit a value dependency", IMessageProvider.INFORMATION);
    setExistingValues(this.selectedAttrValueDep);
    return contents;
  }

  /**
   * @param selectedAttrDep2
   */
  private void setExistingValues(final AttrNValueDependency selectedAttrDep) {
    String[] items = this.comboDepnAttrName.getItems();
    for (String item : items) {
      // iterate through all the attr in the combo
      if ((selectedAttrDep != null) && item.equalsIgnoreCase(selectedAttrDep.getName())) {
        // get the selected index of the combo
        int index = this.comboDepnAttrName.indexOf(item);
        this.comboDepnAttrName.select(index);
        this.comboDepnAttrName.setEnabled(false); // ICDM-119
        this.comboDepnAttrVal.setFocus(); // ICDM-183
        break;
      }
    }
    if (null != selectedAttrDep) {
      this.comment.setText(selectedAttrDep.getChangeComment() == null ? "" : selectedAttrDep.getChangeComment());
    }
    int index = this.comboDepnAttrName.getSelectionIndex();
    // get selected attr depn name from the list
    this.selDepnAttrName = this.comboDepnAttrName.getItem(index);
    boolean attrSet = false;
    for (com.bosch.caltool.icdm.model.apic.attr.Attribute attribute : this.attributes) {

      // iterate through all attributes to get the selected attribute
      if (attribute.getName().equalsIgnoreCase(this.selDepnAttrName)) {
        attrSet = true;
        setValues(attribute);
        break;
      }
    }
    if (!attrSet) {
      // if the attr value set is empty
      this.comboDepnAttrVal.removeAll();
      this.comboDepnAttrVal.add("No active values are avaliable!");
      this.comboDepnAttrVal.select(0);
    }
    if (this.selectedAttrValueDep.getValue() != null) {
      this.comboDepnAttrVal.select(this.comboDepnAttrVal.indexOf(this.selectedAttrValueDep.getValue()));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    // get the selected attibute
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
    // get the selected attribute value
    int attrValIndex = this.comboDepnAttrVal.getSelectionIndex();
    String selDepnAttrVal = this.comboDepnAttrVal.getItem(attrValIndex);
    SortedSet<com.bosch.caltool.icdm.model.apic.attr.AttributeValue> listAttrVals = null;
    if (selectedDepnAttr != null) {
      // create attribute client bo
      AttributeClientBO bo = new AttributeClientBO(selectedDepnAttr);
      listAttrVals = bo.getAttrValues();
    }

    com.bosch.caltool.icdm.model.apic.attr.AttributeValue dependAttrVal = null;
    if ((listAttrVals != null) && !"No active values are avaliable!".equalsIgnoreCase(selDepnAttrVal) &&
        !selDepnAttrVal.equalsIgnoreCase(ApicConstants.DEFAULT_COMBO_SELECT)) {
      for (com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue : listAttrVals) {
        if (selDepnAttrVal.equalsIgnoreCase(attributeValue.getName())) {
          dependAttrVal = attributeValue;
          break;
        }
      }
      this.comboDepnAttrVal.select(0);
    }
    // update
    addToCommandStack(this.selectedAttrValueDep, selectedDepnAttr, changeComment, dependAttrVal);

    super.okPressed();
  }

  /* *//**
        * @param selectedAttr
        * @param selectedDepnAttr
        * @param changeComment
        * @param dependAttrVal
        */

  private void addToCommandStack(final AttrNValueDependency selectedAttrValDep,
      final com.bosch.caltool.icdm.model.apic.attr.Attribute selectedDepnAttr, final String changeComment,
      final com.bosch.caltool.icdm.model.apic.attr.AttributeValue dependAttrVal) {
    if (null != selectedAttrValDep) {
      AttrNValueDependencyServiceClient client = new AttrNValueDependencyServiceClient();
      try {
        final Long depenAttrID = selectedDepnAttr.getId();

        selectedAttrValDep.setDependentAttrId(depenAttrID);
        // Populate the dependent ValueID, handled ICDM-93
        if (null != dependAttrVal) {
          selectedAttrValDep.setDependentValueId(dependAttrVal.getId());
        }
        else {
          selectedAttrValDep.setDependentValueId(null);
        }
        selectedAttrValDep.setChangeComment(changeComment);
        // call web service to update attr val dependency
        client.update(selectedAttrValDep);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
      AttributeValueClientBO valBO = new AttributeValueClientBO(this.selectedVal.get(0));
      this.attributesPage.getValDepTabViewer().setInput(valBO.getValueDependencies(true));
      // ICDM-119
      // if dependent attribute value is null this means that the dependency is based on used flag.
      // In this case the add attribute action should be disabled since only one (not deleted) dependency can exist
      // for such an attribute
      if (null == dependAttrVal) {
        final DeletionActions delAction = new DeletionActions(this.attributesPage);
        delAction.deleteExistingDepWithValues(this.selectedVal.get(0));
        this.attributesPage.getActionSet().getAddValueDepAction().setEnabled(false);
      }
      else {
        this.attributesPage.getActionSet().getAddValueDepAction().setEnabled(true);
      }
    }
  }

}
