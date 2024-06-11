/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.AddAttributeDepnDialog;
import com.bosch.caltool.apic.ui.dialogs.AddAttributeDialog;
import com.bosch.caltool.apic.ui.dialogs.AddUserAsValueDialog;
import com.bosch.caltool.apic.ui.dialogs.AddValueDepnDialog;
import com.bosch.caltool.apic.ui.dialogs.AddValueDialog;
import com.bosch.caltool.apic.ui.dialogs.EditAttributeDepnDialog;
import com.bosch.caltool.apic.ui.dialogs.EditAttributeDialog;
import com.bosch.caltool.apic.ui.dialogs.EditValueDepnDialog;
import com.bosch.caltool.apic.ui.dialogs.EditValueDialog;
import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.apic.ui.jobs.AttributesExportJob;
import com.bosch.caltool.apic.ui.table.filters.AttrPageToolBarFilters;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.excel.ExcelConstants;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO.CLEARING_STATUS;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;


/**
 * @author dmo5cob
 */
public class AttributesActionSet {

  /**
   * Action to edit attribute
   */
  private Action actnEditAttr;
  /**
   * Action to delete attribute
   */
  private Action actnDeleteAttr;
  /**
   * Action to add attribute
   */
  private Action actnAddAttr;
  /**
   * Action to add attribute dependency
   */
  private Action actnAddAttrDep;
  /**
   * Action to edit attribute dependency
   */
  private Action actnEditAttrDep;
  /**
   * Action to delete attribute dependency
   */
  private Action actnDeleteAttrDep;
  /**
   * Action to undelete attribute dependency
   */
  private Action actnUndeleteAttrDep;
  /**
   * Action to add value
   */
  private Action actnAddValue;
  /**
   * Action to edit value
   */
  private Action actnEditVal;
  /**
   * Action to delete value
   */
  private Action actnDeleteValue;
  /**
   * Action to undelete value
   */
  private Action actnUndeleteValue;
  /**
   * Action to add value dependency
   */
  private Action actnAddValueDep;
  /**
   * Action to edit value dependency
   */
  private Action actnEditValDep;
  /**
   * Action to delete value dependency
   */
  private Action actnDeleteValueDepn;
  /**
   * Action to undelete value dependency
   */
  private Action actnUndeleteValueDepn;

  /**
   * Attributes Page
   */
  private final AttributesPage attributesPage;
  /**
   * Action to attribute value - to inclearing
   */
  private Action inClearAction;
  /**
   * Action to attribute value cleared
   */
  private Action clearedAction;
  /**
   * Status line manager
   */
  private final IStatusLineManager statusLineManager;
  /**
   * Change to Not cleared value using this Action
   */
  private Action notClearAction;

  /**
   * The parameterized constructor
   *
   * @param attributesPage instance
   * @param statusLineManager statusLineManager for count display
   */
  public AttributesActionSet(final AttributesPage attributesPage, final IStatusLineManager statusLineManager) {
    this.statusLineManager = statusLineManager;
    this.attributesPage = attributesPage;
  }

  /**
   * @return the inClearAction
   */
  public Action getInClearAction() {
    return this.inClearAction;
  }


  /**
   * @return the notClearingAction
   */
  public Action getNotClearAction() {
    return this.notClearAction;
  }

  /**
   * @return the clearedAction
   */
  public Action getClearedAction() {
    return this.clearedAction;
  }

  /**
   * @param toolBarManager This method defines the action for deleting an attribute
   */
  public void deleteAttrAction(final ToolBarManager toolBarManager) {


    this.actnDeleteAttr = new Action("Delete Attribute") {

      @Override
      public void run() {
        try {
          final DeletionActions delAction = new DeletionActions(AttributesActionSet.this.attributesPage);
          delAction.deleteAttribute();
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    };
    // Image for add action
    this.actnDeleteAttr.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    toolBarManager.add(this.actnDeleteAttr);
  }

  /**
   * @param toolBarManager This method defines the action for deleting an attribute dependency
   */
  public void deleteAttrDepAction(final ToolBarManager toolBarManager) {


    this.actnDeleteAttrDep = new Action(ApicUiConstants.DELETE_ACTION) {

      @Override
      public void run() {
        final DeletionActions delAction = new DeletionActions(AttributesActionSet.this.attributesPage);
        delAction.deleteAttributeDependency(ApicUiConstants.DELETE_ACTION);
      }
    };
    // Image for add action
    this.actnDeleteAttrDep.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    toolBarManager.add(this.actnDeleteAttrDep);
  }

  /**
   * @param toolBarManager This method defines the action for undeleting an attribute dependency
   */
  public void undeleteAttrDepAction(final ToolBarManager toolBarManager) {
    this.actnUndeleteAttrDep = new Action(ApicUiConstants.UN_DELETE_ACTION) {

      @Override
      public void run() {


        final DeletionActions delAction = new DeletionActions(AttributesActionSet.this.attributesPage);
        delAction.deleteAttributeDependency(ApicUiConstants.UN_DELETE_ACTION);
      }
    };
    // Image for add action
    this.actnUndeleteAttrDep.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UNDELETE_16X16));

    toolBarManager.add(this.actnUndeleteAttrDep);
  }

  /**
   * @param toolBarManager This method defines the action for deleting a value
   */
  public void deleteValueAction(final ToolBarManager toolBarManager) {
    this.actnDeleteValue = new Action(ApicUiConstants.DELETE_ACTION) {

      @Override
      public void run() {


        final DeletionActions delAction = new DeletionActions(AttributesActionSet.this.attributesPage);
        delAction.deleteValue(ApicUiConstants.DELETE_ACTION);
      }
    };
    // Image for add action
    this.actnDeleteValue.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));

    toolBarManager.add(this.actnDeleteValue);
  }

  /**
   * @param toolBarManager This method defines the action for deleting a value
   */
  public void unDeleteValueAction(final ToolBarManager toolBarManager) {
    this.actnUndeleteValue = new Action(ApicUiConstants.UN_DELETE_ACTION) {

      @Override
      public void run() {

        final DeletionActions delAction = new DeletionActions(AttributesActionSet.this.attributesPage);
        delAction.deleteValue(ApicUiConstants.UN_DELETE_ACTION);
      }
    };
    // Image for add action
    this.actnUndeleteValue.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UNDELETE_16X16));

    toolBarManager.add(this.actnUndeleteValue);
  }

  /**
   * @param toolBarManager This method defines the action for deleting a value dependency
   */
  public void deleteValueDepnAction(final ToolBarManager toolBarManager) {
    this.actnDeleteValueDepn = new Action(ApicUiConstants.DELETE_ACTION) {

      @Override
      public void run() {

        final DeletionActions delAction = new DeletionActions(AttributesActionSet.this.attributesPage);
        delAction.deleteValueDependency(ApicUiConstants.DELETE_ACTION);
      }
    };
    // Image for add action
    this.actnDeleteValueDepn.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));

    toolBarManager.add(this.actnDeleteValueDepn);
  }

  /**
   * @param toolBarManager This method defines the action for deleting a value dependency
   */
  public void undeleteValueDepnAction(final ToolBarManager toolBarManager) {
    this.actnUndeleteValueDepn = new Action(ApicUiConstants.UN_DELETE_ACTION) {

      @Override
      public void run() {

        final DeletionActions delAction = new DeletionActions(AttributesActionSet.this.attributesPage);
        delAction.deleteValueDependency(ApicUiConstants.UN_DELETE_ACTION);
      }
    };
    // Image for undelete action
    this.actnUndeleteValueDepn.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UNDELETE_16X16));

    toolBarManager.add(this.actnUndeleteValueDepn);
  }

  /**
   * This method initiates the attribute editing from Attributes table section
   *
   * @param toolBarManager instance
   */
  public void createAttrEditAction(final ToolBarManager toolBarManager) {
    this.actnEditAttr = new Action("Edit Attribute") {

      @Override
      public void run() {
        final EditAttributeDialog editAttrDialog =
            new EditAttributeDialog(Display.getDefault().getActiveShell(), AttributesActionSet.this.attributesPage);
        editAttrDialog.open();
      }
    };
    // Image for add action
    this.actnEditAttr.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));

    toolBarManager.add(this.actnEditAttr);
  }

  /**
   * @param toolBarManager This method defines the action for editing an attribute dependency
   */
  public void createAttrDependencyEditAction(final ToolBarManager toolBarManager) {


    this.actnEditAttrDep = new Action("Edit Attribute Dependency") {

      @Override
      public void run() {
        final EditAttributeDepnDialog editAttrDialog =
            new EditAttributeDepnDialog(Display.getDefault().getActiveShell(), AttributesActionSet.this.attributesPage);
        editAttrDialog.open();
      }
    };
    // Image for add action
    this.actnEditAttrDep.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    toolBarManager.add(this.actnEditAttrDep);
  }

  /**
   * @param toolBarManager This method defines the action for editing a value
   */
  public void createValueEditAction(final ToolBarManager toolBarManager) {
    // Action to edit value
    this.actnEditVal = new Action("Edit Value") {

      @Override
      public void run() {
        final EditValueDialog editValueDialog =
            new EditValueDialog(Display.getDefault().getActiveShell(), AttributesActionSet.this.attributesPage);
        editValueDialog.open();
      }
    };
    // Image for add action
    this.actnEditVal.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));

    toolBarManager.add(this.actnEditVal);
  }

  /**
   * @param toolBarManager This method defines the action for editing a value dependency
   */
  public void createValueDepnEditAction(final ToolBarManager toolBarManager) {
    // Action to edit value dependency
    this.actnEditValDep = new Action("Edit Value Dependency") {

      @Override
      public void run() {
        final EditValueDepnDialog editValueDialog =
            new EditValueDepnDialog(Display.getDefault().getActiveShell(), AttributesActionSet.this.attributesPage);
        editValueDialog.open();
      }
    };
    // Image for add action
    this.actnEditValDep.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));

    toolBarManager.add(this.actnEditValDep);
  }

  /**
   * This method defines the action for adding an attribute
   *
   * @param toolBarManager instance
   */
  public void createAttrAddAction(final ToolBarManager toolBarManager) {

    this.actnAddAttr = new Action("Add Attribute") {

      @Override
      public void run() {
        final AddAttributeDialog addAttrDialog =
            new AddAttributeDialog(Display.getDefault().getActiveShell(), AttributesActionSet.this.attributesPage);
        addAttrDialog.open();

      }
    };
    // Image for add action
    this.actnAddAttr.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));

    toolBarManager.add(this.actnAddAttr);
  }

  /**
   * This method adds an attribute depenedency.
   *
   * @param toolBarManager instance
   * @param attrPage Attributes Page
   */
  public void createAttrDependencyAddAction(final ToolBarManager toolBarManager, final AttributesPage attrPage) {
    // Action to add attr dependency
    this.actnAddAttrDep = new Action("Add Attribute Dependency") {

      @Override
      public void run() {
        final AddAttributeDepnDialog addAttrDialog = new AddAttributeDepnDialog(Display.getDefault().getActiveShell(),
            AttributesActionSet.this.attributesPage.getAttrHandler().getSelectedAttributes(),
            AttributesActionSet.this.attributesPage.getAttrDepTabViewer(), attrPage);
        addAttrDialog.open();
      }
    };
    // Image for add action
    this.actnAddAttrDep.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));

    toolBarManager.add(this.actnAddAttrDep);
  }

  /**
   * This method adds a value.
   *
   * @param toolBarManager instance
   */
  public void createValueAddAction(final ToolBarManager toolBarManager) {

    this.actnAddValue = new Action("Add Value") {

      @Override
      public void run() {
        // ICDM-108,ICDM-2580
        if (AttributesActionSet.this.attributesPage.getAttrHandler().getSelectedAttributes().get(0).getValueType()
            .equals(AttributeValueType.ICDM_USER.toString())) {
          AddUserAsValueDialog addUserDialog = new AddUserAsValueDialog(Display.getCurrent().getActiveShell(), false,
              AttributesActionSet.this.attributesPage.getAttrHandler(),
              AttributesActionSet.this.attributesPage.getAttrHandler().getSelectedAttributes().get(0));
          addUserDialog.open();
        }
        else {
          openAddValDialog();
        }
      }
    };
    // Image for add action
    this.actnAddValue.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));

    toolBarManager.add(this.actnAddValue);
  }

  /**
   * This method invokes the 'Add Value' dialog from Values table section
   */
  private void openAddValDialog() {
    Set<String> existingAttrValues = AttributesActionSet.this.attributesPage.getAttrHandler()
        .getValueStringsForValidation(AttributesActionSet.this.attributesPage.getValueTableViewer().getInput(), null);
    final AddValueDialog addValDialog = new AddValueDialog(Display.getDefault().getActiveShell(),
        AttributesActionSet.this.attributesPage.getAttrHandler().getSelectedAttributes().get(0),
        AttributesActionSet.this.attributesPage.getValueTableViewer(), null, false /* Paste action - ICDM-150 */, null,
        existingAttrValues);
    addValDialog.open();
  }


  /**
   * This method adds a value dependency.
   *
   * @param toolBarManager instance
   */
  public void createValueDepnAddAction(final ToolBarManager toolBarManager) {
    // Action to add value dependency
    this.actnAddValueDep = new Action("Add Value Dependency") {

      @Override
      public void run() {
        final AddValueDepnDialog addValueDialog =
            new AddValueDepnDialog(Display.getDefault().getActiveShell(), AttributesActionSet.this.attributesPage);
        addValueDialog.open();
      }
    };
    // Image for add action
    this.actnAddValueDep.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));

    toolBarManager.add(this.actnAddValueDep);
  }


  /**
   * @return the editAttrAction
   */
  public Action getEditAttrAction() {
    return this.actnEditAttr;
  }

  /**
   * @return the deleteAttrAction
   */
  public Action getDeleteAttrAction() {
    return this.actnDeleteAttr;
  }

  /**
   * @return the addAttrAction
   */
  public Action getAddAttrAction() {
    return this.actnAddAttr;
  }

  /**
   * @return the addAttrDepAction
   */
  public Action getAddAttrDepAction() {
    return this.actnAddAttrDep;
  }

  /**
   * @return the editAttrDepAction
   */
  public Action getEditAttrDepAction() {
    return this.actnEditAttrDep;
  }

  /**
   * @return the deleteAttrDepAction
   */
  public Action getDeleteAttrDepAction() {
    return this.actnDeleteAttrDep;
  }

  /**
   * @return the addValueAction
   */
  public Action getAddValueAction() {
    return this.actnAddValue;
  }

  /**
   * @return the editValAction
   */
  public Action getEditValAction() {
    return this.actnEditVal;
  }

  /**
   * @return the deleteValueAction
   */
  public Action getDeleteValueAction() {
    return this.actnDeleteValue;
  }

  /**
   * @return the addValueDepAction
   */
  public Action getAddValueDepAction() {
    return this.actnAddValueDep;
  }

  /**
   * @return the editValDepAction
   */
  public Action getEditValDepAction() {
    return this.actnEditValDep;
  }

  /**
   * @return the deleteValueDepnAction
   */
  public Action getDeleteValueDepnAction() {
    return this.actnDeleteValueDepn;
  }

  /**
   * @return the undeleteValueAction
   */
  public Action getUndeleteValueAction() {
    return this.actnUndeleteValue;
  }

  /**
   * @return the undeleteAttrDepAction
   */
  public Action getUndeleteAttrDepAction() {
    return this.actnUndeleteAttrDep;
  }

  /**
   * @return the undeleteValueDepnAction
   */
  public Action getUndeleteValueDepnAction() {
    return this.actnUndeleteValueDepn;
  }

  /**
   * Icdm-831 Changes for New Button move to inclearing
   *
   * @param toolBarManager toolBarManager
   */
  public void moveToInClearingAction(final ToolBarManager toolBarManager) {
    this.inClearAction = new Action("Move value(s) to IN CLEARING") {

      @Override
      public void run() {
        AttributeValueServiceClient client = new AttributeValueServiceClient();
        final List<AttributeValue> selectedValues =
            AttributesActionSet.this.attributesPage.getAttrHandler().getSelectedValues();
        AttributeValueClientBO bo = new AttributeValueClientBO(selectedValues.get(0));
        for (AttributeValue attributeValue : selectedValues) {
//          selected attribute values will be moved to in_clearing status
          attributeValue.setClearingStatus(CLEARING_STATUS.IN_CLEARING.getDBText());
          try {
            attributeValue = client.update(attributeValue);
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }
          // update the table viewer
          setValuesTableInput(bo, attributeValue);
        }

      }
    };
    // Image for add action
    this.inClearAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.IN_CLEARING_16X16));
    toolBarManager.add(this.inClearAction);

  }

  /**
   * Icdm-831 Changes for New Button move to cleared
   *
   * @param toolBarManager toolBarManager
   */
  public void moveToClearedAction(final ToolBarManager toolBarManager) {
    // Action to move values to cleared state
    this.clearedAction = new Action("Move value(s) to CLEARED") {

      @Override
      public void run() {
        AttributeValueServiceClient client = new AttributeValueServiceClient();
        final List<AttributeValue> selectedValues =
            AttributesActionSet.this.attributesPage.getAttrHandler().getSelectedValues();
        AttributeValueClientBO bo = new AttributeValueClientBO(selectedValues.get(0));
        for (AttributeValue attributeValue : selectedValues) {

          attributeValue.setClearingStatus(CLEARING_STATUS.CLEARED.getDBText());
          try {
            attributeValue = client.update(attributeValue);
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }
          // udpate the table viewer with updated value
          setValuesTableInput(bo, attributeValue);
        }
      }
    };
    // Image for add action
    this.clearedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CLEAR_16X16));
    toolBarManager.add(this.clearedAction);

  }

  /**
   * Filter In clearing and Not cleared Values In UI Icdm-831
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param attrTableViewer attrTableViewer
   */
  public void filterUnclearedValues(final ToolBarManager toolBarManager, final AttrPageToolBarFilters toolBarFilters,
      final GridTableViewer attrTableViewer) {

    String grpName = "PIDC_EDITOR";
    String name = "NOT_CLEARED_VAL_FILTER";

    // Action to filter not cleared values
    Action filterNotClearAction = new Action(CommonUiUtils.getMessage(grpName, name), SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrWithNotClr(isChecked());
        final CustomGridTableViewer customGridTableViewer = (CustomGridTableViewer) attrTableViewer;
        customGridTableViewer.setStatusLineManager(AttributesActionSet.this.statusLineManager);
        AttributesActionSet.this.attributesPage.getAttrTableViewer().refresh();
        setAttrValandDepTable();
        AttributesActionSet.this.attributesPage.getAttrTableViewer().refresh();
      }
    };
    filterNotClearAction.setChecked(true);
    // Image for add action
    filterNotClearAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CLEAR_FILTER_16X16));
    toolBarManager.add(filterNotClearAction);

    // Adding the default state to filters map
    this.attributesPage.addToToolBarFilterMap(filterNotClearAction, filterNotClearAction.isChecked());

  }

  /**
   * Filter In clearing and Not cleared Values In UI
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param attrTableViewer attrTableViewer
   */
  public void filterClearedValues(final ToolBarManager toolBarManager, final AttrPageToolBarFilters toolBarFilters,
      final GridTableViewer attrTableViewer) {

    String grpName = "PIDC_EDITOR";
    String name = "CLEARED_VAL_FILTER";

    // Action to filter cleared values
    Action filterClearAction = new Action(CommonUiUtils.getMessage(grpName, name), SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrWithoutNotClr(isChecked());
        final CustomGridTableViewer customGridTableViewer = (CustomGridTableViewer) attrTableViewer;
        customGridTableViewer.setStatusLineManager(AttributesActionSet.this.statusLineManager);
        AttributesActionSet.this.attributesPage.getAttrTableViewer().refresh();
        setAttrValandDepTable();
        AttributesActionSet.this.attributesPage.getAttrTableViewer().refresh();

      }
    };
    filterClearAction.setChecked(true);
    // Image for add action
    filterClearAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CLEAR_16X16));
    toolBarManager.add(filterClearAction);

    // Adding the default state to filters map
    this.attributesPage.addToToolBarFilterMap(filterClearAction, filterClearAction.isChecked());

  }


  /**
   * This method sets the value for attributes table, attribute dependency table and values table
   *
   * @param attrWithNotClearVal
   */
  private void setAttrValandDepTable() {
    final GridTableViewer attrTableViewer = AttributesActionSet.this.attributesPage.getAttrTableViewer();
    final GridTableViewer attrDepTableViewer = AttributesActionSet.this.attributesPage.getAttrDepTabViewer();
    final GridTableViewer valTableViewer = AttributesActionSet.this.attributesPage.getValueTableViewer();
    // clear the table input for values and attribute dependency tables
    attrDepTableViewer.setInput(null);
    valTableViewer.setInput(null);
    attrDepTableViewer.setSelection(null);
    valTableViewer.setSelection(null);

    if (attrTableViewer.getGrid().getItems().length > 0) {
      final GridItem item = attrTableViewer.getGrid().getItem(0);
      if (item != null) {
        final Object data = item.getData();
        // Check if obejct is of type attribute
        if (data instanceof Attribute) {
          final Attribute attr = (Attribute) data;
          AttributeClientBO bo = new AttributeClientBO(attr);
          attrTableViewer.getGrid().setSelection(0);
          final SortedSet<AttrNValueDependency> listAttrDependency = bo.getAttrDependencies(true/* includeDeleted */);
          attrDepTableViewer.setInput(listAttrDependency);
          attrDepTableViewer.refresh();
          final SortedSet<AttributeValue> attrValues = bo.getAttrValues();
          valTableViewer.setInput(attrValues);
          valTableViewer.refresh();
        }
      }
    }
  }

  /**
   * Icdm-897 Set to not cleared value action
   *
   * @param toolBarManager toolBarManager
   */
  public void moveToNotClearedAction(final ToolBarManager toolBarManager) {
    // Make values as not cleared
    this.notClearAction = new Action("Move value(s) to NOT CLEARED") {

      @Override
      public void run() {
        AttributeValueServiceClient client = new AttributeValueServiceClient();

        final List<AttributeValue> selectedValues =
            AttributesActionSet.this.attributesPage.getAttrHandler().getSelectedValues();
        AttributeValueClientBO bo = new AttributeValueClientBO(selectedValues.get(0));
        for (AttributeValue attributeValue : selectedValues) {
          attributeValue.setClearingStatus(CLEARING_STATUS.NOT_CLEARED.getDBText());
          try {
            attributeValue = client.update(attributeValue);

          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }
          // update the table viewer with the udpated value
          setValuesTableInput(bo, attributeValue);
        }
      }


    };
    // Image for add action
    this.notClearAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CLEAR_FILTER_16X16));
    toolBarManager.add(this.notClearAction);

  }

  /**
   * This method is used to refresh the UI with the updated value
   *
   * @param bo
   * @param attributeValue
   */
  private void setValuesTableInput(final AttributeValueClientBO bo, final AttributeValue attributeValue) {
//    attribute values table will be reset after an action is performed
    SortedSet<AttributeValue> values =
        AttributesActionSet.this.attributesPage.getSelectedValuesMap().get(bo.getAttribute());
    if (null != values) {
      if (values.contains(attributeValue)) {
        values.remove(attributeValue);
      }
      values.add(attributeValue);
    }
    AttributesActionSet.this.attributesPage.getValueTableViewer().setInput(values);
  }

  /**
   * This method creates export action
   */
  // ICDM-893
  public void attrsExportAction() {

    FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
    fileDialog.setText("Save Report");
    fileDialog.setFilterExtensions(ExcelConstants.FILTER_EXCEL_EXTN_WITH_STAR);
    fileDialog.setFilterNames(ExcelConstants.FILTER_NAMES);
    fileDialog.setFilterIndex(0);
    fileDialog.setOverwrite(true);
    fileDialog.setFileName("Attributes");
    // Open the dialog to choose the file path
    final String fileSelected = fileDialog.open();
    final String fileExtn = ExcelConstants.FILTER_EXTNS[fileDialog.getFilterIndex()];
    CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
    ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Saving file ...", 100);
        if (null != fileSelected) {
          // Export attributes
          Job job = new AttributesExportJob(new MutexRule(), fileSelected, fileExtn, true);
          job.schedule();
          monitor.worked(50);
          // after exporting open the exported excel file
          new CommonActionSet().openExcelFile(fileSelected);
          monitor.worked(100);
        }

        monitor.done();

      });
    }
    catch (InvocationTargetException | InterruptedException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }
}
