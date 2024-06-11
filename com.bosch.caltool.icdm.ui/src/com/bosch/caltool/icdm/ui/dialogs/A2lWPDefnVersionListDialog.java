/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.action.A2lWPDefnVersionListDialogActionSet;
import com.bosch.caltool.icdm.ui.sorters.A2lWpDefnVersSorter;
import com.bosch.caltool.icdm.ui.util.IUIConstants;
import com.bosch.caltool.icdm.ui.views.A2LDetailsPage;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author pdh2cob
 */
public class A2lWPDefnVersionListDialog extends AbstractDialog {

  private FormToolkit formToolkit;
  private Form form;
  private Section section;
  private GridTableViewer tableViewer;
  private Text filterTxt;
  private final A2lWpDefnVersSorter sorter = new A2lWpDefnVersSorter();
  private WPDefinitionVersionFilter wPDefinitionVersionFilter;
  private final A2LWPInfoBO a2lWPInfoBO;

  private A2lWpDefnVersion selectedA2lWpDefnVersion;

  private A2lWPDefnVersionListDialogActionSet toolbarActionSet;
  private final A2LDetailsPage a2lDetailsPage;
  private final boolean copyFlag;
  /**
   * isOkPressed - to indicate whether ok is pressed
   */
  public boolean isOkPressed = false;

  private static final String COL_VERS_NAME = "Name";
  private static final String COL_VERS_DESC = "Description";
  private static final String COL_VERS_NUMBER = "Version #";
  private static final String COL_STATUS = "Status";
  private static final String COL_ACTIVE = "Active";
  private static final String COL_CREATED_DATE = "Created Date";
  private static final String COL_CREATED_USER = "Created User";

  private static final int WIDTH_VERS_NAME = 180;
  private static final int WIDTH_VERS_DESC = 250;
  private static final int WIDTH_VERS_NUMBER = 70;
  private static final int WIDTH_STATUS = 80;
  private static final int WIDTH_ACTIVE = 55;
  private static final int WIDTH_CREATED_DATE = 120;
  private static final int WIDTH_CREATED_USER = 90;
  private Button okButton;


  /**
   * @param parentShell - calling parent shell
   * @param a2lWPInfoBO instance of bo
   * @param a2lDetailsPage
   */
  public A2lWPDefnVersionListDialog(final Shell parentShell, final A2LWPInfoBO a2lWPInfoBO,
      final A2LDetailsPage a2lDetailsPage, final boolean copyFlag) {
    super(parentShell);
    this.a2lWPInfoBO = a2lWPInfoBO;
    this.a2lDetailsPage = a2lDetailsPage;
    this.copyFlag = copyFlag;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Title modified
    if (this.copyFlag) {
      newShell.setText("Select Workpackage Definition Versions");
    }
    else {
      newShell.setText("Workpackage Definition Versions");
    }
    newShell.setSize(1000, 500);
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.None);
    initializeDialogUnits(composite);

    final GridLayout gridLayout = new GridLayout();
    parent.setLayout(gridLayout);
    parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    composite.setLayout(gridLayout);
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    createSection(getFormToolkit(), gridLayout, composite);
    createButtonBar(parent);
    return composite;
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);
    this.filterTxt.addModifyListener(event -> {
      final String text = A2lWPDefnVersionListDialog.this.filterTxt.getText().trim();
      A2lWPDefnVersionListDialog.this.wPDefinitionVersionFilter.setFilterText(text);
      A2lWPDefnVersionListDialog.this.tableViewer.refresh();
    });
    this.filterTxt.setFocus();
  }

  /**
   * @param toolkit This method initializes sectionOne
   * @param gridLayout
   * @param composite
   */
  private void createSection(final FormToolkit toolkit, final GridLayout gridLayout, final Composite composite) {

    this.section = toolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("Work Package Versions");
    this.section.setExpanded(true);
    this.section.getDescriptionControl().setEnabled(false);
    createForm(toolkit, gridLayout);
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm(final FormToolkit toolkit, final GridLayout gridLayout) {

    this.form = toolkit.createForm(this.section);
    this.form.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.form.getBody().setLayout(gridLayout);
    createFilterTxt();
    this.tableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE,
        GridDataUtil.getInstance().getHeightHintGridData(200));


    // Create GridViewerColumns
    createGridViewerColumns();
    // Add selection listener
    addTableSelectionListener();
    // Add filters to the TableViewer
    addFilters();
    // Invokde GridColumnViewer sorter
    invokeColumnSorter();

    this.tableViewer.setContentProvider(ArrayContentProvider.getInstance());
    if (!this.copyFlag) {
      addToolbarActions();
    }
    this.tableViewer.setInput(this.a2lWPInfoBO.getA2lWpDefnVersMap().values());
    if (this.a2lWPInfoBO.getActiveVers() != null) {
      this.tableViewer.setSelection(new StructuredSelection(this.a2lWPInfoBO.getActiveVers()));
      this.selectedA2lWpDefnVersion = this.a2lWPInfoBO.getActiveVers();
      if (!this.copyFlag) {
        A2lWPDefnVersionListDialog.this.toolbarActionSet.getEditAction().setEnabled(true);
      }
    }


  }

  /**
   * Method to add create, edit and delete, load actions
   */
  private void addToolbarActions() {
    this.toolbarActionSet = new A2lWPDefnVersionListDialogActionSet(this);
    ToolBarManager toolbarManager = (ToolBarManager) this.form.getToolBarManager();
    this.toolbarActionSet.addA2lWpDefnVersionAction(toolbarManager, getShell(), this.a2lDetailsPage);
    this.toolbarActionSet.editA2lWpDefnVersionAction(toolbarManager, getShell(), this.a2lDetailsPage);
    toolbarManager.update(true);

  }


  /**
   * Refresh table
   */
  public void refreshTable() {
    this.tableViewer.refresh();
  }

  /**
   * This method adds filter to table
   */
  public void addFilters() {
    this.wPDefinitionVersionFilter = new WPDefinitionVersionFilter();
    this.tableViewer.addFilter(this.wPDefinitionVersionFilter);
  }

  /**
   * This method adds sorter to table
   */
  public void invokeColumnSorter() {
    this.tableViewer.setComparator(this.sorter);
  }


  /**
   * This method creates gridtable columns
   */
  public void createGridViewerColumns() {
    createVersionNumberColumn();
    createVersionNameColumn();
    createVersionDescColumn();
    createStatusColumn();
    createActiveColumn();
    createCreatedDateColumn();
    createCreatedUserColumn();
  }


  /**
   * Create Version Number column
   */
  private void createVersionNumberColumn() {
    GridViewerColumn versionNumCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tableViewer, COL_VERS_NUMBER, WIDTH_VERS_NUMBER);

    versionNumCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof A2lWpDefnVersion) {
          A2lWpDefnVersion wpDefnVers = (A2lWpDefnVersion) element;
          return wpDefnVers.getVersionNumber().toString();
        }
        return "";
      }
    });

    // add selection listeners
    versionNumCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(versionNumCol.getColumn(), 0, this.sorter, this.tableViewer));
  }


  private void createVersionNameColumn() {
    GridViewerColumn versionNameCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tableViewer, COL_VERS_NAME, WIDTH_VERS_NAME);


    versionNameCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String ret = "";
        if (element instanceof A2lWpDefnVersion) {
          A2lWpDefnVersion wpDefnVers = (A2lWpDefnVersion) element;
          ret = wpDefnVers.getVersionName();
          if (wpDefnVers.isWorkingSet()) {
            ret += " (" + ApicConstants.WORKING_SET_NAME + ")";
          }
        }
        return ret;
      }


    });

    versionNameCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(versionNameCol.getColumn(), 1, this.sorter, this.tableViewer));
  }


  private void createVersionDescColumn() {
    GridViewerColumn versionDescCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tableViewer, COL_VERS_DESC, WIDTH_VERS_DESC);

    versionDescCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof A2lWpDefnVersion) {
          A2lWpDefnVersion wpDefnVers = (A2lWpDefnVersion) element;
          return wpDefnVers.getDescription();
        }
        return "";
      }


    });


    versionDescCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(versionDescCol.getColumn(), 2, this.sorter, this.tableViewer));
  }


  private void createStatusColumn() {
    GridViewerColumn statusCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tableViewer, COL_STATUS, WIDTH_STATUS);


    statusCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof A2lWpDefnVersion) {
          A2lWpDefnVersion wpDefnVers = (A2lWpDefnVersion) element;
          if (wpDefnVers.getVersionNumber() == 0) {
            return IUIConstants.WP_DEFN_STATUS_IN_WORK;
          }
          return IUIConstants.WP_DEFN_STATUS_LOCKED;
        }
        return "";
      }


    });

    statusCol.getColumn().addSelectionListener(
        GridTableViewerUtil.getInstance().getSelectionAdapter(statusCol.getColumn(), 3, this.sorter, this.tableViewer));

  }

  private void createActiveColumn() {
    GridViewerColumn activeCol =
        GridViewerColumnUtil.getInstance().createGridViewerCheckStyleColumn(this.tableViewer, COL_ACTIVE, WIDTH_ACTIVE);


    activeCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        if (element instanceof A2lWpDefnVersion) {
          A2lWpDefnVersion version = (A2lWpDefnVersion) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
          boolean isActive = false;
          if (version.isActive()) {
            isActive = true;
          }
          gridItem.setChecked(cell.getVisualIndex(), isActive);
          if (!A2lWPDefnVersionListDialog.this.copyFlag) {
            gridItem.setCheckable(cell.getVisualIndex(),
                !A2lWPDefnVersionListDialog.this.a2lWPInfoBO.isWorkingSet(((A2lWpDefnVersion) element).getId()));
          }
          else {
            gridItem.setCheckable(cell.getVisualIndex(), false);
          }
        }
      }


    });
    if (!this.copyFlag) {
      addCheckBoxEditingSupport(activeCol);
    }

    activeCol.getColumn().addSelectionListener(
        GridTableViewerUtil.getInstance().getSelectionAdapter(activeCol.getColumn(), 4, this.sorter, this.tableViewer));


  }


  /**
   * @param activeCol
   */
  private void addCheckBoxEditingSupport(final GridViewerColumn activeCol) {
    activeCol.setEditingSupport(new CheckEditingSupport(activeCol.getViewer()) {

      @Override
      public void setValue(final Object element, final Object value) {
        if (element instanceof A2lWpDefnVersion) {
          A2lWpDefnVersion selectedVersion = ((A2lWpDefnVersion) element).clone();
          boolean isActive = (boolean) value;

          if (isActive) {
            selectedVersion.setActive(true);
          }
          else {
            CDMLogger.getInstance().warnDialog("Atleast one Work Package Version should be set active!",
                Activator.PLUGIN_ID);
            refreshTable();
            return;
          }

          A2lWpDefinitionVersionServiceClient client = new A2lWpDefinitionVersionServiceClient();
          try {
            List<A2lWpDefnVersion> versionList = new ArrayList<>();
            versionList.add(selectedVersion);

            // only one active version is possible, so set non active status to other versions
            setNonActiveStatusForOtherVersions(versionList, selectedVersion);

            // update in db and set updated objects in map
            Set<A2lWpDefnVersion> updatedVersionSet = client.update(versionList);
            for (A2lWpDefnVersion a2lWpDefinitionVersion : updatedVersionSet) {
              A2lWPDefnVersionListDialog.this.a2lWPInfoBO.getA2lWpDefnVersMap().put(a2lWpDefinitionVersion.getId(),
                  a2lWpDefinitionVersion);
            }
            // refresh table
            refreshTable();
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
          }
        }

      }


    });
  }


  /**
   * @param versionList version list to be updated
   * @param selectedVersion edited version
   */
  private void setNonActiveStatusForOtherVersions(final List<A2lWpDefnVersion> versionList,
      final A2lWpDefnVersion selectedVersion) {
    for (A2lWpDefnVersion a2lWpDefinitionVersion : A2lWPDefnVersionListDialog.this.a2lWPInfoBO.getA2lWpDefnVersMap()
        .values()) {
      if ((a2lWpDefinitionVersion.getId() == selectedVersion.getId()) || !a2lWpDefinitionVersion.isActive()) {
        continue;
      }
      a2lWpDefinitionVersion.setActive(false);
      versionList.add(a2lWpDefinitionVersion);
    }
  }


  private void createCreatedDateColumn() {
    GridViewerColumn createdDateCol = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tableViewer,
        COL_CREATED_DATE, WIDTH_CREATED_DATE);


    createdDateCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof A2lWpDefnVersion) {
          A2lWpDefnVersion wpDefnVers = (A2lWpDefnVersion) element;
          try {
            return ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_15, wpDefnVers.getCreatedDate(),
                DateFormat.DFLT_DATE_FORMAT);
          }
          catch (ParseException e) {
            CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
          }
        }
        return "";
      }


    });
    createdDateCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(createdDateCol.getColumn(), 5, this.sorter, this.tableViewer));

  }


  private void createCreatedUserColumn() {
    GridViewerColumn createdUserCol = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tableViewer,
        COL_CREATED_USER, WIDTH_CREATED_USER);


    createdUserCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof A2lWpDefnVersion) {
          A2lWpDefnVersion wpDefnVers = (A2lWpDefnVersion) element;
          return wpDefnVers.getCreatedUser();
        }
        return "";
      }


    });

    createdUserCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(createdUserCol.getColumn(), 6, this.sorter, this.tableViewer));

  }

  /**
   * This method adds selection listener
   */
  public void addTableSelectionListener() {
    this.tableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        IStructuredSelection selection =
            (IStructuredSelection) A2lWPDefnVersionListDialog.this.tableViewer.getSelection();
        A2lWPDefnVersionListDialog.this.selectedA2lWpDefnVersion = (A2lWpDefnVersion) selection.getFirstElement();
        if (A2lWPDefnVersionListDialog.this.a2lWPInfoBO.isEditable() && !A2lWPDefnVersionListDialog.this.copyFlag) {
          A2lWPDefnVersionListDialog.this.toolbarActionSet.getEditAction().setEnabled(true);
        }
        // ok button may not be present if the dialog is not opened from the Structure view.
        if (A2lWPDefnVersionListDialog.this.okButton != null) {
          A2lWPDefnVersionListDialog.this.okButton
              .setEnabled(!A2lWPDefnVersionListDialog.this.selectedA2lWpDefnVersion.isWorkingSet());
        }
      }
    });
  }


  /**
   * Text Filter Class
   *
   * @author elm1cob
   */
  private class WPDefinitionVersionFilter extends AbstractViewerFilter {


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean selectElement(final Object element) {

      if (element instanceof A2lWpDefnVersion) {
        A2lWpDefnVersion a2lWpDefinitionVersion = (A2lWpDefnVersion) element;
        if (matchText(a2lWpDefinitionVersion.getVersionNumber().toString())) {
          return true;
        }
        if (matchText(a2lWpDefinitionVersion.getVersionName())) {
          return true;
        }
        if (matchText(a2lWpDefinitionVersion.getDescription())) {
          return true;
        }
        if (matchText(a2lWpDefinitionVersion.getVersionNumber() == 0 ? IUIConstants.WP_DEFN_STATUS_IN_WORK
            : IUIConstants.WP_DEFN_STATUS_LOCKED)) {
          return true;
        }
        try {
          if (matchText(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_15, a2lWpDefinitionVersion.getCreatedDate(),
              DateFormat.DFLT_DATE_FORMAT))) {
            return true;
          }
        }
        catch (ParseException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
        if (matchText(a2lWpDefinitionVersion.getCreatedUser())) {
          return true;
        }
      }

      return false;
    }

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    if (this.copyFlag) {
      this.okButton = createButton(parent, IDialogConstants.OK_ID, "Ok", false);
      this.okButton.setEnabled(true);
      Button cancelButton = createButton(parent, IDialogConstants.CANCEL_ID, "Close", false);
      cancelButton.setEnabled(true);
    }
    else {
      Button closeButton = createButton(parent, IDialogConstants.CANCEL_ID, "Close", false);
      closeButton.setEnabled(true);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    this.isOkPressed = true;
    super.okPressed();

  }

  /**
   * @return the selectedA2lWpDefnVersion
   */
  public A2lWpDefnVersion getSelectedA2lWpDefnVersion() {
    return this.selectedA2lWpDefnVersion;
  }


  /**
   * @param selectedA2lWpDefnVersion the selectedA2lWpDefnVersion to set
   */
  public void setSelectedA2lWpDefnVersion(final A2lWpDefnVersion selectedA2lWpDefnVersion) {
    this.selectedA2lWpDefnVersion = selectedA2lWpDefnVersion;
  }

  /**
   * @param selectedA2lWpDefnVersion the selectedA2lWpDefnVersion to set
   */
  public A2LWPInfoBO getA2lWPInfoBO() {
    return this.a2lWPInfoBO;
  }


}
