/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.EditMailDialog;
import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.views.providers.AttributesTableLabelProvider;
import com.bosch.caltool.icdm.client.bo.apic.AttrNValueDependencyClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttrNValueDependencyServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dmo5cob This class includes the methods for deletion actions
 */

public class DeletionActions {


  /**
   * List of selectd attributes
   */
  private final List<Attribute> selectedAttrs;

  /**
   * List of selected values
   */
  private final List<AttributeValue> selectedVals;

  /**
   * Attributes page
   */
  private final AttributesPage attributesPage;

  /**
   * Attribute dependency of sel attr
   */
  private final AttrNValueDependency selectedAttrDep;

  /**
   * dependency of selected val private
   */
  private final AttrNValueDependency selectedValDep;

  /**
   * The parameterized constructor
   *
   * @param attributesPage instance
   */
  public DeletionActions(final AttributesPage attributesPage) {
    // initialise the fields
    this.selectedAttrs = attributesPage.getAttrHandler().getSelectedAttributes();
    this.attributesPage = attributesPage;
    this.selectedVals = attributesPage.getAttrHandler().getSelectedValues();
    this.selectedAttrDep = attributesPage.getAttrHandler().getSelectedAttrDep();
    this.selectedValDep = attributesPage.getAttrHandler().getSelectedAttrValDep();
  }

  /**
   * This method deletes selected attribute.
   *
   * @throws ApicWebServiceException Error during Service Call
   */
  public void deleteAttribute() throws ApicWebServiceException {
    // check if the user has Apic write access
    if (new CurrentUserBO().hasApicWriteAccess()) {
      // delete of attribute is possible only if user has APIC WRITE access
      for (Attribute attr : this.selectedAttrs) {
        AttributeClientBO bo = new AttributeClientBO(attr);
        AttributeServiceClient client = new AttributeServiceClient();
        try {
          attr.setDeleted(true);
          attr = client.update(attr);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }

        final AttributesTableLabelProvider attrTabLabProvider = new AttributesTableLabelProvider(this.attributesPage);
        // Changes the deleted row in the table to red color.
        attrTabLabProvider.getForeground(attr);
        // Refresh the table viewer
        this.attributesPage.getAttrTableViewer().refresh();
        // Disable the actions that are not valid for for deleted attribute
        this.attributesPage.getActionSet().getAddValueDepAction().setEnabled(false);
        this.attributesPage.getActionSet().getEditValDepAction().setEnabled(false);
        this.attributesPage.getActionSet().getDeleteValueDepnAction().setEnabled(false);
        this.attributesPage.getActionSet().getAddValueAction().setEnabled(false);
        this.attributesPage.getActionSet().getEditValAction().setEnabled(false);
        this.attributesPage.getActionSet().getDeleteValueAction().setEnabled(false);
        this.attributesPage.getActionSet().getUndeleteValueAction().setEnabled(false);
        this.attributesPage.getActionSet().getAddAttrDepAction().setEnabled(false);
        this.attributesPage.getActionSet().getDeleteAttrAction().setEnabled(false);
        // Change the deleted row in Values table to red
        refreshAllAtrrTableVals(bo, attrTabLabProvider);
      }
    }
  }

  /**
   * @param bo
   * @param attrTabLabProvider
   */
  private void refreshAllAtrrTableVals(final AttributeClientBO bo,
      final AttributesTableLabelProvider attrTabLabProvider) {
    if (!bo.getAttrValues().isEmpty()) {
      SortedSet<AttributeValue> listAttrVals = bo.getAttrValues();
      for (AttributeValue attributeValue : listAttrVals) {
        // set foreground
        attrTabLabProvider.getForeground(attributeValue);
        this.attributesPage.getValueTableViewer().refresh();
      }
    }
  }


  /**
   * Get the attribute for the given attribute value from the list of selected attributes
   *
   * @param attrValue
   * @return selected attribute for value
   */
  private Attribute getSelectedAttrForValue(final AttributeValue attrValue) {
    if (attrValue != null) {
//      looping through all selected attributes to find attribute using attribute value
      for (Attribute attribute : this.selectedAttrs) {
        if (CommonUtils.isEqual(attrValue.getAttributeId(), attribute.getId())) {
          return attribute;
        }
      }
    }
    return null;
  }

  /**
   * This method deletes selected attribute value.
   *
   * @param strDelete delete/undelete
   */
  public void deleteValue(final String strDelete) {
    Attribute attribute = getSelectedAttrForValue(this.selectedVals.get(0));
    boolean attrUsed = false; // ICDM-740
    if ((attribute != null) && (attribute.getLevel() != 0)) {
      // when the attribute is not null and the level is not 0
      List<Long> valueList = new ArrayList<>();
      this.selectedVals.forEach(val -> valueList.add(val.getId()));
      try {
        attrUsed = new AttributeValueServiceClient().getLevelAttrValuesUsedStatus(valueList);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    if (!attrUsed) {
      // ICDM-2217/ICDM-2300
      // dialog to edit mail
      if (strDelete.equalsIgnoreCase(ApicUiConstants.DELETE_ACTION)) {
        // Send mail if the value is deleetd
        final EditMailDialog editMailDialog =
            new EditMailDialog(Display.getDefault().getActiveShell(), this.attributesPage);
        editMailDialog.getSelectedValsList().addAll(this.selectedVals);
        editMailDialog.open();
      }
      else if (strDelete.equalsIgnoreCase(ApicUiConstants.UN_DELETE_ACTION)) {
        unDeleteSelectedAttrValue();
      }

    }
    else {
      // show info dialog that level attributes cannot be deleted
      CDMLogger.getInstance().infoDialog("Cannot be deleted. Level attribute values are used in project.",
          Activator.PLUGIN_ID);
    }
  }

  /**
   *
   */
  private void unDeleteSelectedAttrValue() {
    for (AttributeValue attrVal : this.selectedVals) {
      // ICDM-257
      attrVal = unDeleteValue(attrVal);
      // Enable or disable actions final
      this.attributesPage.getActionSet().getEditValDepAction().setEnabled(false);
      this.attributesPage.getActionSet().getDeleteValueDepnAction().setEnabled(false);
      this.attributesPage.getActionSet().getDeleteValueAction().setEnabled(true);
      this.attributesPage.getActionSet().getEditValAction().setEnabled(true);
      this.attributesPage.getActionSet().getUndeleteValueAction().setEnabled(false);
      this.attributesPage.getActionSet().getAddValueDepAction().setEnabled(true);
      SortedSet<AttributeValue> values =
          this.attributesPage.getSelectedValuesMap().get(getSelectedAttrForValue(attrVal));
      if (null != values) {
        if (values.contains(attrVal)) {
          values.remove(attrVal);
        }
        values.add(attrVal);
      }
      // refresh the UI table
      this.attributesPage.getValueTableViewer().setInput(values);
      this.attributesPage.getValueTableViewer().refresh();
    }
  }

  /**
   * This method undeletes given attribute value
   *
   * @param attrVal
   */
  private AttributeValue unDeleteValue(final AttributeValue attrVal) {
    try {
      // call the service to undelete attibute value
      AttributeValueServiceClient serClient = new AttributeValueServiceClient();
      attrVal.setDeleted(false);
      if (attrVal.getClearingStatus().equals(CLEARING_STATUS.REJECTED.getDBText())) {
        attrVal.setClearingStatus(CLEARING_STATUS.NOT_CLEARED.getDBText());
      }
      else if (attrVal.getClearingStatus().equals(CLEARING_STATUS.DELETED.getDBText())) {
        attrVal.setClearingStatus(CLEARING_STATUS.CLEARED.getDBText());
      }
      // call the service to undelete the attribute value
      return serClient.update(attrVal);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * This method deletes attr dependency
   *
   * @param strDelete delete/undelete
   */
  public void deleteAttributeDependency(final String strDelete) {
    AttrNValueDependencyClientBO bo = new AttrNValueDependencyClientBO(this.selectedAttrDep);
    // icdm-253
    boolean skip = false;
    AttrNValueDependency depToBeUpdated = this.selectedAttrDep;
    // Undo delete action selected
    if (strDelete.equalsIgnoreCase(ApicUiConstants.UN_DELETE_ACTION)) {
      // Check if undo deleted allowed
      if (bo.isUnDeleteAllowed()) {
        updateDependencyDeletedFlag(depToBeUpdated, false);
        if (bo.getDependencyValue() == null) {
          deleteExistingDepWithValues(bo.getAttribute());
        }
      }
      else {
        // ICDM-340
        // show warn dialog in case undelete is not allowed
        CDMLogger.getInstance().warnDialog("UnDelete not allowed!", Activator.PLUGIN_ID);
        skip = true;
      }
    }
    else {
      // delete action selected
      updateDependencyDeletedFlag(depToBeUpdated, true);
    }

    // Proceed if there is no error
    if (!skip) {
      deleteSelectedAttributeDependency(strDelete, bo);
    }

  }

  /**
   * @param depToBeUpdated
   */
  private void updateDependencyDeletedFlag(final AttrNValueDependency depToBeUpdated, final boolean flag) {
    try {
      // call update service to set deleted flag
      depToBeUpdated.setDeleted(flag);
      (new AttrNValueDependencyServiceClient()).update(depToBeUpdated);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param strDelete
   * @param bo
   */
  private void deleteSelectedAttributeDependency(final String strDelete, final AttrNValueDependencyClientBO bo) {
    AttrNValueDependencyClientBO dbo = new AttrNValueDependencyClientBO(this.selectedAttrDep);
    AttributeClientBO attrBO = new AttributeClientBO(dbo.getAttribute());
    this.attributesPage.getAttrDepTabViewer().setInput(attrBO.getAttrDependencies(true));
    // Delete action selected
    if (strDelete.equalsIgnoreCase(ApicUiConstants.DELETE_ACTION)) {
      this.attributesPage.getActionSet().getDeleteAttrDepAction().setEnabled(false);
      this.attributesPage.getActionSet().getEditAttrDepAction().setEnabled(false);
      this.attributesPage.getActionSet().getUndeleteAttrDepAction().setEnabled(true);
      // if the deleted attr dependency is based on used flag then the add action should be enabled
      if (bo.getDependencyValue() == null) {
        this.attributesPage.getActionSet().getAddAttrDepAction().setEnabled(true);
      }
    }
    else {
      this.attributesPage.getActionSet().getDeleteAttrDepAction().setEnabled(true);
      this.attributesPage.getActionSet().getEditAttrDepAction().setEnabled(true);
      this.attributesPage.getActionSet().getUndeleteAttrDepAction().setEnabled(false);
      if (bo.getDependencyValue() == null) {
        // update the UI table
        this.attributesPage.getActionSet().getAddAttrDepAction().setEnabled(false);
      }
    }
  }

  /**
   * This method deletes the value dependency
   *
   * @param strDelete .
   */
  public void deleteValueDependency(final String strDelete) {
    boolean skip = false;
    AttrNValueDependencyClientBO bo = new AttrNValueDependencyClientBO(this.selectedValDep);
    // icdm-253
    AttrNValueDependency depToBeUpdated = this.selectedValDep;
    // Undo delete action sleected
    if (strDelete.equalsIgnoreCase(ApicUiConstants.UN_DELETE_ACTION)) {
      // check if undo delete is allowed
      if (bo.isUnDeleteAllowedForValDependency()) {
        updateDependencyDeletedFlag(depToBeUpdated, false);
        if (this.selectedValDep.getDependentValueId() == null) {
          // delete existing dependencies
          deleteExistingDepWithValues(this.selectedVals.get(0));
        }
      }
      else {
        // ICDM-340
        // show warn dialog if un delete is not allowed
        CDMLogger.getInstance().warnDialog("UnDelete not allowed!", Activator.PLUGIN_ID);
        skip = true;
      }
    }
    else {
      // call the web service to set the attr val dependency as deleted
      updateDependencyDeletedFlag(depToBeUpdated, true);
    }

    if (!skip) {
      // when the UnDelete is allowed
      deleteSelectedValueDependency(strDelete);
    }
  }

  /**
   * @param strDelete
   */
  private void deleteSelectedValueDependency(final String strDelete) {
    AttributeValueClientBO valBO = new AttributeValueClientBO(this.selectedVals.get(0));
    this.attributesPage.getValDepTabViewer().setInput(valBO.getValueDependencies(true));

    if (strDelete.equalsIgnoreCase(ApicUiConstants.DELETE_ACTION)) {
      // Delete action selected
      this.attributesPage.getActionSet().getDeleteValueDepnAction().setEnabled(false);
      this.attributesPage.getActionSet().getEditValDepAction().setEnabled(false);
      this.attributesPage.getActionSet().getUndeleteValueDepnAction().setEnabled(true);
      // if the deleted value dependency is based on used flag then the add action should be enabled
      if (this.selectedValDep.getDependentValueId() == null) {
        this.attributesPage.getActionSet().getAddValueDepAction().setEnabled(true);
      }
    }
    else {
      // Undelete action selection
      this.attributesPage.getActionSet().getDeleteValueDepnAction().setEnabled(true);
      this.attributesPage.getActionSet().getEditValDepAction().setEnabled(true);
      this.attributesPage.getActionSet().getUndeleteValueDepnAction().setEnabled(false);
      if (this.selectedValDep.getDependentValueId() == null) {
        this.attributesPage.getActionSet().getAddValueDepAction().setEnabled(false);
      }
    }
  }


  /**
   * This method deletes the existing dependencies with values
   *
   * @param selAttr selected Attr
   */
  public void deleteExistingDepWithValues(final Attribute selAttr) {
    AttributeClientBO clientBO = new AttributeClientBO(selAttr);
    SortedSet<AttrNValueDependency> attrDependencies = clientBO.getAttrDependencies(false);
    if (!attrDependencies.isEmpty()) {
      // if there are attribute dependencies
      for (AttrNValueDependency attrDependency : attrDependencies) {
        // iterate the set and delete all dependency
        AttrNValueDependencyClientBO bo = new AttrNValueDependencyClientBO(attrDependency);
        if ((attrDependency != null) && (bo.getDependencyValue() != null)) {
          // update the deleted flag
          updateDependencyDeletedFlag(attrDependency, true);
          // update the UI table
          this.attributesPage.getAttrDepTabViewer().setInput(clientBO.getAttrDependencies(true));
        }
      }
    }
  }


  /**
   * This method deletes the existing dependencies with values
   *
   * @param selValue the selected value
   */
  public void deleteExistingDepWithValues(final AttributeValue selValue) {
    AttributeValueClientBO clientBO = new AttributeValueClientBO(selValue);
    SortedSet<AttrNValueDependency> valueDependencies = clientBO.getValueDependencies(false);
    if (!valueDependencies.isEmpty()) {
      // check all value dependencies
      for (AttrNValueDependency valDependency : valueDependencies) {
        if ((valDependency != null) && (null != valDependency.getDependentValueId())) {
          // update the deleted flag
          updateDependencyDeletedFlag(valDependency, true);
          // update the UI table
          this.attributesPage.getValDepTabViewer().setInput(clientBO.getValueDependencies(true));
        }
      }
    }
  }
}
