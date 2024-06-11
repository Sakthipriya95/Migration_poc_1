/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.AliasActionSet;
import com.bosch.caltool.apic.ui.dialogs.AddEditAliasDialog;
import com.bosch.caltool.apic.ui.sorter.AliasAttrSorter;
import com.bosch.caltool.apic.ui.table.filters.AliasAttrFilter;
import com.bosch.caltool.apic.ui.table.filters.AliasAttrToolBarFilter;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.client.bo.apic.AliasDefEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.AliasDetail;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.ws.rest.client.apic.AliasDetailServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.text.TextUtil;


/**
 * PIDC search results section
 *
 * @author bru2cob
 */
public class AliasAttributeSection {


  /**
   * Pidc name column width
   */
  private static final int PIDC_NAME_COL_WIDTH = 200;

  /**
   * column widht-120
   */
  private static final int ATTR_LEVEL_WIDTH = 120;
  /**
   * Section to display pidc search result
   */
  private Section resultSection;
  /**
   * Form to display pidc search result
   */
  private Form resultForm;
  /**
   * Filter text for results table
   */
  private Text aliasFilterTxt;

  /**
   * Sorter for pidcs
   */
  private AliasAttrSorter aliasAttrSorter;
  /**
   * Search page instance
   */
  private final AliasDetailsPage detailsPage;
  /**
   * Table which displays the pidc search results
   */
  private CustomGridTableViewer attrAliasTable;
  private AliasAttrToolBarFilter toolBarFilters;

  private Action createAliasAction;

  private Action editAliasAction;

  private Action deleteAliasAction;

  private Attribute selectedAttr;


  private AliasAttrFilter aliasAttrFilter;

  /**
   * @param detailsPage pidc search page instance
   */
  public AliasAttributeSection(final AliasDetailsPage detailsPage) {
    super();
    this.detailsPage = detailsPage;
  }

  /**
   * @param selectedAttr the selectedAttr to set
   */
  public void setSelectedAttr(final Attribute selectedAttr) {
    this.selectedAttr = selectedAttr;
  }


  /**
   * @return the resultTable
   */
  public CustomGridTableViewer getResultTable() {
    return this.attrAliasTable;
  }

  /**
   * @return data handler
   */
  private AliasDefEditorDataHandler getDataHandler() {
    return this.detailsPage.getDataHandler();
  }

  /**
   * Create the section to display the search results
   */
  public void createResultsSection() {
    this.resultSection = this.detailsPage.getFormToolkit().createSection(this.detailsPage.getSashForm(),
        Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.resultSection.setText("Attribute Alias");
    this.resultSection.setExpanded(true);
    this.resultSection.getDescriptionControl().setEnabled(false);
    createResultForm();
    this.resultSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.resultSection.setClient(this.resultForm);

  }

  /**
   * Create result form in the section
   */
  private void createResultForm() {


    this.resultForm = this.detailsPage.getFormToolkit().createForm(this.resultSection);
    this.resultForm.getBody().setLayout(new GridLayout());
    this.resultForm.setLayoutData(GridDataUtil.getInstance().getGridData());
    // Create Filter text
    this.aliasFilterTxt =
        TextUtil.getInstance().createFilterText(this.detailsPage.getFormToolkit(), this.resultForm.getBody(),
            GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    createResultTable();
    createToolBarAction();
    if (!getDataHandler().isModifiable()) {
      enableDisableButtons(false, false, false);
    }
  }


  /**
   * result table which holds the search results
   */
  private void createResultTable() {
    this.aliasAttrSorter = new AliasAttrSorter(getDataHandler());
    this.attrAliasTable = new CustomGridTableViewer(this.resultForm.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
    // initialise the status bar
    this.detailsPage.initializeEditorStatusLineManager(this.attrAliasTable);

    this.attrAliasTable.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());

    this.attrAliasTable.getGrid().setLinesVisible(true);
    this.attrAliasTable.getGrid().setHeaderVisible(true);

    this.resultForm.getBody().setLayout(new GridLayout());
    addResultFilters();
    addModifyListenerForFilterTxt();
    createTabColumns();
    this.attrAliasTable.setContentProvider(ArrayContentProvider.getInstance());
    this.attrAliasTable.setInput(getDataHandler().getAttributes());

    // ICDM-1213
    this.attrAliasTable.addSelectionChangedListener(evt -> {
      IStructuredSelection sel = (IStructuredSelection) evt.getSelection();
      Attribute attr = (Attribute) sel.getFirstElement();
      if (attr != null) {
        AliasAttributeSection.this.selectedAttr = attr;
        toggleButtons(attr);
      }
      AliasAttributeSection.this.detailsPage.changeTabInp();
    });

    invokeResultColumnSorter();
    // ICDM-1158
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.attrAliasTable.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes,
        new CustomDragListener(this.attrAliasTable));

  }

  /* *//**
        * Add sorter for the table columns
        */
  private void invokeResultColumnSorter() {
    this.attrAliasTable.setComparator(this.aliasAttrSorter);
  }

  /**
   * Add filters to the result table
   */
  private void addResultFilters() {
    this.aliasAttrFilter = new AliasAttrFilter(getDataHandler());
    this.attrAliasTable.addFilter(this.aliasAttrFilter);
  }

  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {

    this.aliasFilterTxt.addModifyListener(evt -> {
      final String text = AliasAttributeSection.this.aliasFilterTxt.getText().trim();
      AliasAttributeSection.this.aliasAttrFilter.setFilterText(text);
      AliasAttributeSection.this.attrAliasTable.refresh();
    });
  }

  /**
   * Create pidc search results table
   */
  private void createTabColumns() {
    createNameCol();
    createAttrValueTypeCol();
    createAliasNameCol();
  }

  /**
   * Creates the level columns
   */
  private void createAliasNameCol() {

    final GridViewerColumn aliasName = new GridViewerColumn(this.attrAliasTable, SWT.NONE);
    aliasName.getColumn().setText("Alias");
    aliasName.getColumn().setWidth(ATTR_LEVEL_WIDTH);

    aliasName.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        Attribute attr = (Attribute) element;
        return getDataHandler().getAttrAliasName(attr.getId());
      }
    });
    // Add column selection listener
    aliasName.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(aliasName.getColumn(), 2, this.aliasAttrSorter, this.attrAliasTable));
  }


  /**
   * Creates the level columns
   */
  private void createAttrValueTypeCol() {

    final GridViewerColumn aliasName = new GridViewerColumn(this.attrAliasTable, SWT.NONE);
    aliasName.getColumn().setText("Value Type");
    aliasName.getColumn().setWidth(ATTR_LEVEL_WIDTH);

    aliasName.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        Attribute attr = (Attribute) element;
        return attr.getValueType();
      }
    });
    // Add column selection listener
    aliasName.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(aliasName.getColumn(), 1, this.aliasAttrSorter, this.attrAliasTable));
  }


  /**
   * Create name column for the result table
   */
  private void createNameCol() {
    final GridViewerColumn nameColumn = new GridViewerColumn(this.attrAliasTable, SWT.NONE);
    nameColumn.getColumn().setText("Attribute Name");
    nameColumn.getColumn().setWidth(PIDC_NAME_COL_WIDTH);
    nameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof Attribute) {
          Attribute attr = (Attribute) element;
          return attr.getName();
        }
        return "";
      }


    });
    // Add column selection listener
    nameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(nameColumn.getColumn(), 0, this.aliasAttrSorter, this.attrAliasTable));

  }

  /**
   * Icdm-1283 tool bar action set for PIDC with A2l filea and with review. This method creates Section ToolBar actions
   */
  private void createToolBarAction() {


    this.toolBarFilters = new AliasAttrToolBarFilter(getDataHandler());
    this.attrAliasTable.addFilter(this.toolBarFilters);
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.resultSection);
    final Separator separator = new Separator();

    createAddAttrAliasAction(toolBarManager);
    editAddAttrAliasAction(toolBarManager);
    deleteAddAttrAliasAction(toolBarManager);
    AliasActionSet actionSet = new AliasActionSet();

    toolBarManager.add(separator);
    actionSet.attrWithAlias(toolBarManager, this.toolBarFilters, this.attrAliasTable);
    actionSet.createAttrWithoutAliasAction(toolBarManager, this.toolBarFilters, this.attrAliasTable);
    toolBarManager.update(true);
    this.resultSection.setTextClient(toolbar);
  }


  /**
   * @param toolBarManager
   * @param toolBarFilters2
   * @param resultTable2
   */
  private void deleteAddAttrAliasAction(final ToolBarManager toolBarManager) {
    this.deleteAliasAction = new Action("Delete Attribute Alias") {

      @Override
      public void run() {
        AliasDetail aliasDetail = getDataHandler().getAttrAlias(AliasAttributeSection.this.selectedAttr.getId());
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
    toolBarManager.add(this.deleteAliasAction);

  }


  /**
   * @param toolBarManager
   * @param toolBarFilters2
   * @param resultTable2
   */
  private void editAddAttrAliasAction(final ToolBarManager toolBarManager) {
    this.editAliasAction = new Action("Edit Attribute Alias") {

      @Override
      public void run() {
        AliasDetail aliasDetail = getDataHandler().getAttrAlias(AliasAttributeSection.this.selectedAttr.getId());
        final AddEditAliasDialog dialog = new AddEditAliasDialog(Display.getDefault().getActiveShell(),
            getDataHandler().getAliasDef(), AliasAttributeSection.this.selectedAttr, null, aliasDetail);
        dialog.open();
      }
    };
    // Image for add action
    this.editAliasAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));

    toolBarManager.add(this.editAliasAction);

  }


  /**
   * @param toolBarManager
   * @param toolBarFilters2
   * @param resultTable2
   */
  private void createAddAttrAliasAction(final ToolBarManager toolBarManager) {
    this.createAliasAction = new Action("Add Attribute Alias") {

      @Override
      public void run() {
        final AddEditAliasDialog dialog = new AddEditAliasDialog(Display.getDefault().getActiveShell(),
            getDataHandler().getAliasDef(), AliasAttributeSection.this.selectedAttr, null, null);
        dialog.open();
      }
    };
    // Image for add action
    this.createAliasAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));

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
    AliasAttributeSection.this.createAliasAction.setEnabled(add);
    AliasAttributeSection.this.editAliasAction.setEnabled(edit);
    AliasAttributeSection.this.deleteAliasAction.setEnabled(del);
  }


  /**
   * @param attr
   */
  void toggleButtons(final Attribute attr) {
    if (getDataHandler().isModifiable()) {
      if (attr != null) {
        AliasDetail aliasDetail = getDataHandler().getAttrAlias(attr.getId());
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
   * Refresh the section
   */
  public void refresh() {
    this.attrAliasTable.setInput("");
    this.attrAliasTable.setInput(getDataHandler().getAttributes());
    this.attrAliasTable.refresh();
    if (this.selectedAttr != null) {
      this.attrAliasTable.setSelection(new StructuredSelection(this.selectedAttr), true);
    }
    toggleButtons(this.selectedAttr);
  }
}
