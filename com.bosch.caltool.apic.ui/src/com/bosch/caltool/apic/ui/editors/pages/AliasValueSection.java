/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.AliasActionSet;
import com.bosch.caltool.apic.ui.dialogs.AddEditAliasDialog;
import com.bosch.caltool.apic.ui.sorter.AliasValueSorter;
import com.bosch.caltool.apic.ui.table.filters.AliasValueFilter;
import com.bosch.caltool.apic.ui.table.filters.AliasValueToolBarFilter;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.client.bo.apic.AliasDefEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.AliasDetail;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.ws.rest.client.apic.AliasDetailServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.text.TextUtil;


/**
 * PIDC search attributes tree section
 *
 * @author bru2cob
 */
public class AliasValueSection {

  /**
   * horizontal span
   */
  private static final int SPAN_2 = 2;

  /**
   * Search page instance
   */
  private final AliasDetailsPage detailsPage;

  /**
   * Section to display attrs with value
   */
  private Section attrsSection;

  /**
   * @return the attrsSection
   */
  public Section getAttrsSection() {
    return this.attrsSection;
  }

  /**
   * Form to display the attrs
   */
  private Form attsForm;
  /**
   * Filter text instance
   */
  private Text filterTxt;


  /**
   * Instance of tree viewer for attrs
   */
  private CustomGridTableViewer valueAliasTable;

  /**
   * @return the valueAliasTable
   */
  public CustomGridTableViewer getValueAliasTable() {
    return this.valueAliasTable;
  }

  /**
   * Filtering the attributes
   */
  private AliasValueFilter attrValFilters;

  /**
   * Sorter for attrs
   */
  private AliasValueSorter aliasValSorter;
  /**
   * two columns in attr table
   */
  private static final int ATTR_FORM_COL = 2;
  /**
   * Count of total checked attributes
   */
  private Label count;

  /**
   * attr pre-defined filters
   */
  private AttributeValue selectedVal;
  /**
   * Action to delete alias
   */
  private Action deleteAliasAction;
  /**
   * Action to edit alias
   */
  private Action editAliasAction;
  /**
   * Action to create alias
   */
  private Action createAliasAction;
  /**
   * Instance of selected attribute
   */
  private Attribute selectedAttr;

  /**
   * @param detailsPage pidc search page instance
   */
  public AliasValueSection(final AliasDetailsPage detailsPage) {
    super();
    this.detailsPage = detailsPage;
  }

  /**
   * Label count
   *
   * @return the count
   */
  public Label getCount() {
    return this.count;
  }


  /**
   * Set the count
   *
   * @param count the count to set
   */
  public void setCount(final Label count) {
    this.count = count;
  }

  /**
   * Create the section to display the attrs and value
   */
  public void createAttrValSection() {
    this.attrsSection = this.detailsPage.getFormToolkit().createSection(this.detailsPage.getSashForm(),
        Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.attrsSection.setText("Value Alias");
    this.attrsSection.setExpanded(true);

    // create the attrs form
    createAttrsForm();
    this.attrsSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.attrsSection.setClient(this.attsForm);

  }

  /**
   * Create the attrs form in section
   */
  private void createAttrsForm() {
    this.aliasValSorter = new AliasValueSorter(this.detailsPage.getDataHandler());
    this.attsForm = this.detailsPage.getFormToolkit().createForm(this.attrsSection);
    GridLayout layout = new GridLayout();
    layout.numColumns = ATTR_FORM_COL;
    this.attsForm.getBody().setLayout(layout);
    this.attsForm.setLayoutData(GridDataUtil.getInstance().getGridData());
    /**
     * Create three composites on the attrs form. 1. one comp for count 2.one composite for buttons 3.one composite for
     * attr table
     */
    createAttrTabComp();

    if (!getDataHandler().isModifiable()) {
      enableDisableButtons(false, false, false);
    }
  }

  /**
   * @return data handler
   */
  public AliasDefEditorDataHandler getDataHandler() {
    return this.detailsPage.getDataHandler();
  }

  /**
   * Create attrs table composite
   */
  private void createAttrTabComp() {
    Composite tableComp = new Composite(this.attsForm.getBody(), SWT.NONE);
    tableComp.setLayout(new GridLayout());
    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.horizontalSpan = SPAN_2;
    tableComp.setLayoutData(gridData);
    // Create Filter text
    createFilterTxt(tableComp);
    // Invoke TableViewer Column sorters
    createTableViewer(tableComp);
    // ICDM-1158
    createToolBarAction();
    // add column sorter
    invokeColumnSorter();
  }

  /**
   * Creates the summary tree tableviewer
   *
   * @param attrComp base composite
   */
  private void createTableViewer(final Composite attrComp) {

    this.valueAliasTable =
        new CustomGridTableViewer(attrComp, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);

    this.detailsPage.initializeEditorStatusLineManager(this.valueAliasTable);

    this.valueAliasTable.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());

    this.valueAliasTable.getGrid().setLinesVisible(true);
    this.valueAliasTable.getGrid().setHeaderVisible(true);
    this.valueAliasTable.setContentProvider(ArrayContentProvider.getInstance());


    // add filters
    addFilters();
    // create table viewer columns
    createTabViewerColumns();
    // add selection changed listener
    this.valueAliasTable.addSelectionChangedListener(event -> {
      IStructuredSelection sel = (IStructuredSelection) event.getSelection();
      AttributeValue attrVal = (AttributeValue) sel.getFirstElement();
      if ((attrVal != null) && getDataHandler().isModifiable()) {
        AliasValueSection.this.selectedVal = attrVal;
        AliasDetail aliasDetail = getDataHandler().getValueAlias(attrVal.getId());
        if (aliasDetail == null) {
          enableDisableButtons(true, false, false);
        }
        else {
          enableDisableButtons(false, true, true);
        }
      }
      else {
        enableDisableButtons(false, false, false);
      }

    });

  }


  /**
   * Add filters to the attribute tree
   */
  private void addFilters() {
    // add type filter
    this.attrValFilters = new AliasValueFilter(getDataHandler());
    this.valueAliasTable.addFilter(this.attrValFilters);
  }

  /* *//**
        * Add sorter for the table columns
        */
  private void invokeColumnSorter() {
    this.valueAliasTable.setComparator(this.aliasValSorter);
  }

  /**
   * This method creates filter text
   *
   * @param tableComp
   */
  private void createFilterTxt(final Composite tableComp) {
    this.filterTxt = TextUtil.getInstance().createFilterText(this.detailsPage.getFormToolkit(), tableComp,
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    // add modify listener
    this.filterTxt.addModifyListener(event -> {
      final String text = AliasValueSection.this.filterTxt.getText().trim();
      AliasValueSection.this.attrValFilters.setFilterText(text);
      AliasValueSection.this.valueAliasTable.refresh();
    });

  }


  /**
   * Create attr table coulmns
   */
  private void createTabViewerColumns() {
    createValueNameCol();
    createValueAliasCol();
  }

  /**
   * Create attr used info no column
   */
  private void createValueNameCol() {
    final GridViewerColumn valueColumn = new GridViewerColumn(this.valueAliasTable, SWT.NONE);
    valueColumn.getColumn().setText("Value");
    valueColumn.getColumn().setWidth(200);
    valueColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return ((AttributeValue) element).getNameRaw();
      }


    });
    // Add column selection listener
    valueColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(valueColumn.getColumn(), 0, this.aliasValSorter, this.valueAliasTable));

  }

  /**
   * Create attr unknown column
   */
  private void createValueAliasCol() {
    final GridViewerColumn aliasColumn = new GridViewerColumn(this.valueAliasTable, SWT.NONE);
    aliasColumn.getColumn().setText("Value Alias");
    aliasColumn.getColumn().setWidth(200);
    aliasColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        AttributeValue value = (AttributeValue) element;
        return getDataHandler().getValueAliasName(value.getId());
      }


    });
    // Add column selection listener
    aliasColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(aliasColumn.getColumn(), 1, this.aliasValSorter, this.valueAliasTable));

  }


  /**
   * This method creates Section ToolBar actions
   */
  private void createToolBarAction() {

    AliasValueToolBarFilter toolBarFilters = new AliasValueToolBarFilter(getDataHandler());
    this.valueAliasTable.addFilter(toolBarFilters);
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.attrsSection);
    final Separator separator = new Separator();
    createAddAttrAliasAction(toolBarManager);
    editAddAttrAliasAction(toolBarManager);
    deleteAddAttrAliasAction(toolBarManager);
    toolBarManager.add(separator);
    AliasActionSet actionSet = new AliasActionSet();
    actionSet.valWithAlias(toolBarManager, toolBarFilters, this.valueAliasTable);
    actionSet.valWithoutAlias(toolBarManager, toolBarFilters, this.valueAliasTable);
    toolBarManager.update(true);
    this.attrsSection.setTextClient(toolbar);
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters2
   * @param resultTable2
   */
  private void deleteAddAttrAliasAction(final ToolBarManager toolBarManager) {
    this.deleteAliasAction = new Action("Delete Value Alias") {

      @Override
      public void run() {
        AliasDetail aliasDetail = getDataHandler().getValueAlias(AliasValueSection.this.selectedVal.getId());
        try {
          new AliasDetailServiceClient().delete(aliasDetail);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
        }

      }
    };
    // Image for add action
    this.deleteAliasAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    this.deleteAliasAction.setEnabled(false);
    toolBarManager.add(this.deleteAliasAction);

  }

  /**
   * @param toolBarManager
   * @param toolBarFilters2
   * @param resultTable
   */
  private void editAddAttrAliasAction(final ToolBarManager toolBarManager) {
    this.editAliasAction = new Action("Edit Value Alias") {

      @Override
      public void run() {
        AliasDetail aliasDetail = getDataHandler().getValueAlias(AliasValueSection.this.selectedVal.getId());
        final AddEditAliasDialog dialog = new AddEditAliasDialog(Display.getDefault().getActiveShell(),
            getDataHandler().getAliasDef(), null, AliasValueSection.this.selectedVal, aliasDetail);
        dialog.open();

      }
    };
    // Image for add action
    this.editAliasAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    this.editAliasAction.setEnabled(false);
    toolBarManager.add(this.editAliasAction);

  }


  /**
   * @param toolBarManager
   * @param toolBarFilters2
   * @param resultTable2
   */
  private void createAddAttrAliasAction(final ToolBarManager toolBarManager) {
    this.createAliasAction = new Action("Add Value Alias") {

      @Override
      public void run() {
        final AddEditAliasDialog dialog = new AddEditAliasDialog(Display.getDefault().getActiveShell(),
            getDataHandler().getAliasDef(), null, AliasValueSection.this.selectedVal, null);
        dialog.open();
      }
    };
    // Image for add action
    this.createAliasAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    this.createAliasAction.setEnabled(false);

    toolBarManager.add(this.createAliasAction);

  }

  /**
   * enable disable buttons
   *
   * @param add add button status
   * @param edit edit button status
   * @param del delete button status
   */
  public void enableDisableButtons(final boolean add, final boolean edit, final boolean del) {
    this.createAliasAction.setEnabled(add);
    this.editAliasAction.setEnabled(edit);
    this.deleteAliasAction.setEnabled(del);
  }

  /**
   * @param attr
   */
  void toggleButtons(final AttributeValue val) {
    if (getDataHandler().isModifiable()) {
      if (val != null) {
        AliasDetail aliasDetail = getDataHandler().getValueAlias(val.getId());
        if (aliasDetail == null) {
          enableDisableButtons(true, false, false);
        }
        else {
          enableDisableButtons(false, true, true);
        }
      }
      else {
        enableDisableButtons(false, false, false);
      }
    }
  }

  /**
   * @return the selectedAttr
   */
  public Attribute getSelectedAttr() {
    return this.selectedAttr;
  }

  /**
   * @param selectedAttr the selectedAttr to set
   */
  public void setSelectedAttr(final Attribute selectedAttr) {
    this.selectedAttr = selectedAttr;
  }

  /**
   * Refresh the section
   */
  public void refresh() {

    this.valueAliasTable.setInput("");

    // for text type
    if (CommonUtils.isEqual(this.selectedAttr.getValueType(), AttributeValueType.TEXT.getDisplayText())) {
      this.valueAliasTable.setInput(getDataHandler().getAttributeValues(this.selectedAttr.getId()));
      getAttrsSection().setDescription("");

    }
    // For other type
    else {
      getAttrsSection().getDescriptionControl().setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      getAttrsSection().setDescription("Alias can be created only for text values");
    }

    if (this.selectedVal != null) {
      this.valueAliasTable.setSelection(new StructuredSelection(this.selectedVal), true);
    }

    this.valueAliasTable.refresh();
    toggleButtons(this.selectedVal);
  }


}
