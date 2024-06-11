/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.List;
import java.util.SortedSet;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.DeletionActions;
import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
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
public class AddAttributeDepnDialog extends AttributeDepnDialog {

  /**
   * GridTableViewer instance
   */
  private final GridTableViewer viewer;
  /**
   * AttributesPage instance
   */
  private final AttributesPage attributesPage;

  /**
   * The Parameterized Constructor
   *
   * @param parentShell instance
   * @param selectedAttr instance
   * @param viewer instance
   * @param attributesPage instance
   */
  public AddAttributeDepnDialog(final Shell parentShell, final List<Attribute> selectedAttr,
      final GridTableViewer viewer, final AttributesPage attributesPage) {
    super(parentShell, selectedAttr);
    this.viewer = viewer;
    this.attributesPage = attributesPage;
  }

  /**
   * {@inheritDoc} set tiltle of shell
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set the title
    newShell.setText("Add Attribute Dependency");
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
    setTitle("Add Attribute Dependency");

    // Set the message
    setMessage("This is to add a new attribute dependency", IMessageProvider.INFORMATION);
    // ICDM-119
    setExistingValues(this.selectedAttr);

    return contents;
  }

  /**
   * @param selectedAttrDep //ICDM-119
   */
  // This method populates the existing value in the UI
  private void setExistingValues(final List<Attribute> selectedAttrList) {

    if (CommonUtils.isNotEmpty(selectedAttrList) && (selectedAttrList.size() == 1)) {
      AttributeClientBO bo = new AttributeClientBO(selectedAttrList.get(0));
      // add the dependent values to the list
      if (!bo.getAttrDependencies(false).isEmpty()) {
        AttrNValueDependency attrDep = bo.getAttrDependencies(false).first();
        this.comboDepnAttrName.removeAll();
        this.comboDepnAttrName.add(attrDep.getName());
        this.comboDepnAttrName.select(0);
        int index = this.comboDepnAttrName.getSelectionIndex();
        this.selDepnAttrName = this.comboDepnAttrName.getItem(index);
        boolean attrSet = false;
        for (Attribute attribute : this.attributes) {
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
        this.comboDepnAttrVal.select(this.comboDepnAttrVal.indexOf(attrDep.getValue()));
      }
    }
  }


  /**
   * {@inheritDoc} ok pressed
   */
  @Override
  protected void okPressed() {
    int attrIndex = this.comboDepnAttrName.getSelectionIndex();
    String attr = this.comboDepnAttrName.getItem(attrIndex);
    String changeComment = this.comment.getText();
    Attribute selectedDepnAttr = null;

    // get the selected attribute
    for (Attribute attribute : this.attributes) {
      if (attr.equalsIgnoreCase(attribute.getName())) {
        selectedDepnAttr = attribute;
        break;
      }
    }
    int attrValIndex = this.comboDepnAttrVal.getSelectionIndex();
    String selDepnAttrVal = this.comboDepnAttrVal.getItem(attrValIndex);
    SortedSet<com.bosch.caltool.icdm.model.apic.attr.AttributeValue> setAttrVals = null;

    if (selectedDepnAttr != null) {
      setAttrVals = new AttributeClientBO(selectedDepnAttr).getAttrValues();
    }

    com.bosch.caltool.icdm.model.apic.attr.AttributeValue dependAttrVal = null;
    if ((setAttrVals != null) && !"No active values are avaliable!".equalsIgnoreCase(selDepnAttrVal) &&
        !selDepnAttrVal.equalsIgnoreCase(ApicConstants.DEFAULT_COMBO_SELECT)) {

      for (com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue : setAttrVals) {
        if (selDepnAttrVal.equalsIgnoreCase(attributeValue.getName())) {
          dependAttrVal = attributeValue;
          break;
        }
      }
    }

    // Inserts new record
    addToCommandStack(this.selectedAttr, selectedDepnAttr, dependAttrVal, changeComment);

    super.okPressed();
  }

  /**
   * @param selectedAttr
   * @param selectedDepnAttr
   * @param dependAttrVal
   * @param changeComment
   */
  private void addToCommandStack(final List<Attribute> selAttrs, final Attribute selectedDepnAttr,
      final AttributeValue dependAttrVal, final String changeComment) {

    if (hasConflictingDepns(selAttrs, selectedDepnAttr)) {
      // ICDM-340
      CDMLogger.getInstance().warnDialog("Conflicting Dependencies Found !", Activator.PLUGIN_ID);
    }
    else {
      for (Attribute selAttr : selAttrs) {
        if (null != selectedDepnAttr) {

          AttrNValueDependency depn = new AttrNValueDependency();
          depn.setAttributeId(selAttr.getId());
          depn.setDependentAttrId(selectedDepnAttr.getId());
          if (null != dependAttrVal) {
            depn.setDependentValueId(dependAttrVal.getId());
          }
          depn.setChangeComment(changeComment);
          // create the new dependent attribute
          AttrNValueDependencyServiceClient client = new AttrNValueDependencyServiceClient();
          try {
            client.create(depn);
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }
          enableDisableAddAttDepAction(dependAttrVal, selAttr);
        }
        // refresh ui after creation
        this.viewer.refresh();
      }
    }
  }

  /**
   * @param dependAttrVal
   * @param selAttr
   */
  private void enableDisableAddAttDepAction(final AttributeValue dependAttrVal, Attribute selAttr) {
    // If no errors found then update the tableviewer
    AttributeClientBO attrBO = new AttributeClientBO(selAttr);
    this.viewer.setInput(attrBO.getAttrDependencies(true));
    // ICDM-119 if dependent attribute value is null this means that the dependency is based on used flag.
    // In this case the add attribute action should be disabled since only one (not deleted) dependency can
    // exist for such an attribute
    // And also the preexisting dependencies with values should be deleted.
    if (dependAttrVal == null) {
      final DeletionActions delAction = new DeletionActions(this.attributesPage);
      delAction.deleteExistingDepWithValues(selAttr);
      this.attributesPage.getActionSet().getAddAttrDepAction().setEnabled(false);
    }
    else {
      this.attributesPage.getActionSet().getAddAttrDepAction().setEnabled(true);

    }
  }

  /**
   * @param selAttrList
   * @param selectedDepnAttr
   * @param dependAttrVal
   * @return
   */
  // This method checks if there are any conflicting dependencies
  private boolean hasConflictingDepns(final List<Attribute> selAttrList, final Attribute selectedDepnAttr) {

    for (Attribute attribute : selAttrList) {
      AttributeClientBO bo = new AttributeClientBO(attribute);

      if (!bo.getAttrDependencies(false).isEmpty()) {
        AttrNValueDependency attrDepn = bo.getAttrDependencies(false).first();
        if ((!attrDepn.getName().equalsIgnoreCase(selectedDepnAttr.getName())) ||
            (attrDepn.getName().equalsIgnoreCase(selectedDepnAttr.getName()) &&
                (!CommonUtils.isNotEmptyString(attrDepn.getValue())))) {

          return true;
        }
      }
    }
    return false;


  }
}
