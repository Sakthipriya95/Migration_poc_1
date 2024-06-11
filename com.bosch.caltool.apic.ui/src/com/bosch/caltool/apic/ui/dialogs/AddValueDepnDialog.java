/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.List;
import java.util.Set;
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
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttrNValueDependencyServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * This class provides UI to add value dependency information
 */

public class AddValueDepnDialog extends ValueDepnDialog {

  /**
   * List of AttributeValue objects
   */
  private final List<AttributeValue> selectedVals;
  /**
   * AttributesPage instance
   */
  private final AttributesPage attributesPage;

  /**
   * The Parameterized Constructor
   *
   * @param parentShell instance
   * @param attributesPage instance
   */
  public AddValueDepnDialog(final Shell parentShell, final AttributesPage attributesPage) {
    super(parentShell, attributesPage);

    this.selectedVals = attributesPage.getAttrHandler().getSelectedValues();
    this.attributesPage = attributesPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Add Value Dependency");
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
    setTitle("Add Value Dependency");

    // Set the message
    setMessage("This is to add a new  value dependency", IMessageProvider.INFORMATION);
    // ICDM-119
    setExistingValues(this.selectedVals);
    return contents;
  }

  /**
   * @param selectedValues //ICDM-119
   */
  private void setExistingValues(final List<AttributeValue> selectedValues) {
    // Check if there is a value selected
    if ((CommonUtils.isNotEmpty(selectedValues)) && (selectedValues.size() == 1)) {// ICDM-257
      // If selected value has dependency
      AttributeValue attributeValue = selectedValues.get(0);
      AttributeValueClientBO bo = new AttributeValueClientBO(attributeValue);
      SortedSet<AttrNValueDependency> valueDependencies = bo.getValueDependencies(false);
      if (!valueDependencies.isEmpty()) {
        AttrNValueDependency attrValDepn = valueDependencies.first();
        this.comboDepnAttrName.removeAll();
        this.comboDepnAttrName.add(attrValDepn.getName());
        this.comboDepnAttrName.select(0);

        int index = this.comboDepnAttrName.getSelectionIndex();
        this.selDepnAttrName = this.comboDepnAttrName.getItem(index);
        boolean attrSet = false;
        // iterate over each attr
        for (Attribute attribute : this.attributes) {
          // check attr name
          if (attribute.getName().equalsIgnoreCase(this.selDepnAttrName)) {
            attrSet = true;
            setValues(attribute);
            break;
          }
        }
        // check if value is set
        if (!attrSet) {
          this.comboDepnAttrVal.removeAll();
          this.comboDepnAttrVal.add("No active values are avaliable!");
          this.comboDepnAttrVal.select(0);
        }
        // if DependencyValue is present
        if (!CommonUtils.isEmptyString(attrValDepn.getValue())) {
          this.comboDepnAttrVal.select(this.comboDepnAttrVal.indexOf(attrValDepn.getValue()));
        }
      }
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
    Attribute selectedDepnAttr = null;
    // get selected depn attribute
    for (Attribute attribute : this.attributes) {
      if (attr.equalsIgnoreCase(attribute.getName())) {
        selectedDepnAttr = attribute;
        break;
      }
    }
    int attrValIndex = this.comboDepnAttrVal.getSelectionIndex();
    String selDepnAttrVal = this.comboDepnAttrVal.getItem(attrValIndex);
    Set<AttributeValue> listAttrVals = null;
    if (selectedDepnAttr != null) {
      AttributeClientBO bo = new AttributeClientBO(selectedDepnAttr);
      listAttrVals = bo.getAttrValues();
    }

    AttributeValue dependAttrVal = null;
    if ((listAttrVals != null) && !"No active values are avaliable!".equalsIgnoreCase(selDepnAttrVal) &&
        !selDepnAttrVal.equalsIgnoreCase(ApicConstants.DEFAULT_COMBO_SELECT)) {
      for (AttributeValue attributeValue : listAttrVals) {
        if (selDepnAttrVal.equalsIgnoreCase(attributeValue.getName())) {
          dependAttrVal = attributeValue;
          break;
        }
      }
    }
    // Inserts new record
    addToCommandStack(this.selectedVals, selectedDepnAttr, dependAttrVal, changeComment);
    super.okPressed();
  }

  /**
   * @param selValue
   * @param selectedDepnAttr
   * @param dependAttrVal
   * @param changeComment
   */
  private void addToCommandStack(final List<AttributeValue> selValuesList, final Attribute selectedDepnAttr,
      final AttributeValue dependAttrVal, final String changeComment) {
    if (hasConflictingDepns(selValuesList, selectedDepnAttr)) {
      // ICDM-340
      CDMLogger.getInstance().warnDialog("Conflicting Dependencies Found !", Activator.PLUGIN_ID);
    }
    else {
      // iterate over each value
      for (AttributeValue selValue : selValuesList) {
        // if dependency is present
        if (null != selectedDepnAttr) {
          AttrNValueDependency depn = new AttrNValueDependency();
          depn.setValueId(selValue.getId());
          depn.setDependentAttrId(selectedDepnAttr.getId());
          if (null != dependAttrVal) {
            depn.setDependentValueId(dependAttrVal.getId());
          }
          depn.setChangeComment(changeComment);
          AttrNValueDependencyServiceClient client = new AttrNValueDependencyServiceClient();
          try {
            client.create(depn);
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }
          // If no errors found then update the tableviewer
          AttributeValueClientBO attrBO = new AttributeValueClientBO(selValue);
          this.attributesPage.getValDepTabViewer().setInput(attrBO.getValueDependencies(true));
          // ICDM-119
          // if dependent attribute value is null this means that the dependency is based on used flag.
          // In this case the add action should be disabled since only one (not deleted) dependency can exist
          // for such a value
          // And also the preexisting dependencies with values should be deleted.
          if (dependAttrVal == null) {
            final DeletionActions delAction = new DeletionActions(this.attributesPage);
            delAction.deleteExistingDepWithValues(selValue);
            this.attributesPage.getActionSet().getAddValueDepAction().setEnabled(false);
          }
          else {
            this.attributesPage.getActionSet().getAddValueDepAction().setEnabled(true);
          }


          this.attributesPage.getValDepTabViewer().refresh();
        }
      }
    }
  }


  /**
   * @param selValuesList
   * @param selectedDepnAttr
   * @param dependAttrVal ICDM-257
   */
  private boolean hasConflictingDepns(final List<AttributeValue> selValuesList, final Attribute selectedDepnAttr) {
    for (AttributeValue attributeValue : selValuesList) {
      AttributeValueClientBO bo = new AttributeValueClientBO(attributeValue);
      SortedSet<AttrNValueDependency> valueDependencies = bo.getValueDependencies(false);
      if (!valueDependencies.isEmpty()) {
        AttrNValueDependency attrValDepn = valueDependencies.first();
        if ((!attrValDepn.getName().equalsIgnoreCase(selectedDepnAttr.getName())) ||
            (attrValDepn.getName().equalsIgnoreCase(selectedDepnAttr.getName()) &&
                (CommonUtils.isEmptyString(attrValDepn.getValue())))) {
          return true;
        }

      }
    }
    return false;
  }
}
