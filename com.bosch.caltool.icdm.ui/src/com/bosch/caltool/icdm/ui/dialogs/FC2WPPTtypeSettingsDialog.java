/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.PTType;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPRelvPTType;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.editors.FC2WPEditorInput;
import com.bosch.caltool.icdm.ui.table.filters.FC2WPNatToolBarFilters;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPRelevantPTTypeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.PTTypeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;

/**
 * The Class FC2WPPTtypeSettingsDialog.
 *
 * @author gge6cob
 */
public class FC2WPPTtypeSettingsDialog extends AbstractDialog {

  /** FormToolkit instance. */
  private FormToolkit formToolkit;

  /** Composite instance. */
  private Composite composite;


  /** The viewer. */
  private CustomGridTableViewer viewer;

  private final FC2WPNatToolBarFilters toolBarFilters;

  private final SortedSet<FC2WPRelvPTType> wsRelevantTypes;

  private final Action relevantPtTypeFiltrAction;


  private final FC2WPEditorInput editorInput;


  /**
   * Instantiates a new fc2wp PTtype preference dialog.
   *
   * @param parentShell parentShell
   * @param toolBarFilters the tool bar filters
   * @param relevantAction the tool bar action
   * @param editorInput the editor input
   * @param wsRelevantTypes the ws relevant types
   */
  public FC2WPPTtypeSettingsDialog(final Shell parentShell, final FC2WPNatToolBarFilters toolBarFilters,
      final Action relevantAction, final IEditorInput editorInput, final SortedSet<FC2WPRelvPTType> wsRelevantTypes) {
    super(parentShell);
    this.editorInput = (FC2WPEditorInput) editorInput;
    this.toolBarFilters = toolBarFilters;

    // To trigger filter based on selection
    this.relevantPtTypeFiltrAction = relevantAction;

    // Relevant PTtype data
    this.wsRelevantTypes = wsRelevantTypes;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("PT-type Preference Settings");
    super.configureShell(newShell);
  }

  /**
   * This method initializes formToolkit.
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
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("PT-type Preference Settings");

    // Set the message
    setMessage("Relevant Powertrain types for " + this.editorInput.getFc2wpVersion().getName(),
        IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.composite = getFormToolkit().createComposite(parent);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    gridLayout.makeColumnsEqualWidth = true;
    this.composite.setLayout(gridLayout);
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    // create form
    createForm();
    return this.composite;
  }


  /**
   * create the form.
   */
  private void createForm() {
    /** The form. */
    Form form;
    form = getFormToolkit().createForm(this.composite);
    form.setLayout(new GridLayout());
    form.setLayoutData(GridDataUtil.getInstance().getGridData());

    // create the control to choose PowerTrain types
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    gridLayout.makeColumnsEqualWidth = true;
    form.getBody().setLayout(gridLayout);
    form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());

    this.viewer =
        new CustomGridTableViewer(form.getBody(), SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
    this.viewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.viewer.getGrid().setLinesVisible(true);
    this.viewer.getGrid().setHeaderVisible(true);

    this.viewer.setContentProvider(new ArrayContentProvider());
    SortedSet<PTType> wsSortedPTtypes;

    // Input data
    wsSortedPTtypes = getAllPTTypeData();

    if (CommonUtils.isNotEmpty(this.wsRelevantTypes)) {
      this.toolBarFilters.setRelevantPTtypeSet(this.wsRelevantTypes);
    }

    // Create Table columns
    createTableColumns();

    if (CommonUtils.isNotEmpty(wsSortedPTtypes)) {
      this.viewer.setInput(wsSortedPTtypes);
    }
  }

  /**
   * Gets the all PT-type data. Progress
   *
   * @return the all PT-type data
   */
  private SortedSet<PTType> getAllPTTypeData() {

    Set<PTType> allPtTypeSet;
    SortedSet<PTType> sortedPTtypes = null;

    CDMLogger.getInstance().debug("Starting loading All PT-types using APIC web server... ");

    // create a webservice client
    final PTTypeServiceClient ptWebServiceClient = new PTTypeServiceClient();
    try {
      // Load the contents using webservice calls
      allPtTypeSet = ptWebServiceClient.getAllPTtypes();

      // Sort the set
      if (CommonUtils.isNotEmpty(allPtTypeSet)) {
        sortedPTtypes = new TreeSet<>((e1, e2) -> e1.getPtTypeName().compareTo(e2.getPtTypeName()));
        sortedPTtypes.addAll(allPtTypeSet);
        CDMLogger.getInstance().debug("All PT-types for FC2WP Data with ptypes :" + allPtTypeSet.size());
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error("Error loading All PT-types for FC-WP Data. " + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }
    return sortedPTtypes;
  }

  /**
   * Creates the table columns.
   */
  private void createTableColumns() {
    createNameCol();
    createDescCol();

    final GridViewerColumn chkBoxColumn = createCheckBoxCol();

    addChkBoxEditingSupport(chkBoxColumn);
  }

  /**
   * @param chkBoxColumn
   */
  private void addChkBoxEditingSupport(final GridViewerColumn chkBoxColumn) {
    chkBoxColumn.setEditingSupport(new CheckEditingSupport(chkBoxColumn.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        final boolean isChecked = ((Boolean) arg1).booleanValue();
        if (arg0 instanceof PTType) {
          if (isChecked) {
            executeWhenChecked(chkBoxColumn, arg0, isChecked);
          }
          else {
            executeWhenUnchecked(arg0);
          }
          /*
           * Trigger Natpage toolbar-filter event
           */
          FC2WPPTtypeSettingsDialog.this.relevantPtTypeFiltrAction.run();
        }
      }

      /**
       * @param arg0
       */
      private void executeWhenUnchecked(final Object arg0) {
        // delete the relevant PT type
        final FC2WPRelevantPTTypeServiceClient fc2wpDefClient = new FC2WPRelevantPTTypeServiceClient();
        try {
          FC2WPRelvPTType relType = null;
          if (null != FC2WPPTtypeSettingsDialog.this.wsRelevantTypes) {
            for (FC2WPRelvPTType fc2wpRelvPTType : FC2WPPTtypeSettingsDialog.this.wsRelevantTypes) {
              if (fc2wpRelvPTType.getFcwpDefId()
                  .equals(FC2WPPTtypeSettingsDialog.this.editorInput.getFc2wpDef().getId()) &&
                  fc2wpRelvPTType.getPtTypeId().equals(((PTType) arg0).getPtTypeId())) {
                relType = fc2wpRelvPTType;
                break;
              }
            }
          }
          if (null != relType) {
            // Load the contents using webservice calls
            fc2wpDefClient.delete(relType);
            FC2WPPTtypeSettingsDialog.this.wsRelevantTypes.remove(relType);

          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error("Error while changing relevant PT Type. " + exp.getMessage(), exp,
              Activator.PLUGIN_ID);
        }
      }

      /**
       * @param chkBoxColm
       * @param arg0
       * @param isChecked
       */
      private void executeWhenChecked(final GridViewerColumn chkBoxColm, final Object arg0, final boolean isChecked) {
        GridItem item = chkBoxColm.getColumn().getParent().getItem(0);
        item.setChecked(isChecked);
        final FC2WPRelevantPTTypeServiceClient fc2wpDefClient = new FC2WPRelevantPTTypeServiceClient();
        try {
          FC2WPRelvPTType relType = new FC2WPRelvPTType();
          relType.setFcwpDefId(FC2WPPTtypeSettingsDialog.this.editorInput.getFc2wpDef().getId());
          relType.setPtTypeId(((PTType) arg0).getPtTypeId());
          relType.setPtTypeDesc(((PTType) arg0).getPtTypeDesc());
          relType.setPtTypeName(((PTType) arg0).getPtTypeName());
          // Load the contents using webservice calls
          FC2WPRelvPTType ptRelType = fc2wpDefClient.create(relType);
          if ((null != ptRelType) && CommonUtils.isNotNull(FC2WPPTtypeSettingsDialog.this.wsRelevantTypes)) {
            FC2WPPTtypeSettingsDialog.this.wsRelevantTypes.add(ptRelType);
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error("Error while creating relevant PT Type. " + exp.getMessage(), exp,
              Activator.PLUGIN_ID);
        }
      }
    });
  }

  /**
   * @return
   */
  private GridViewerColumn createCheckBoxCol() {
    final GridViewerColumn chkBoxColumn = new GridViewerColumn(this.viewer, SWT.CHECK | SWT.CENTER);
    chkBoxColumn.getColumn().setWidth(150);
    chkBoxColumn.getColumn().setText("Relevant?");
    chkBoxColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        Object element = cell.getElement();

        GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
        if (element instanceof PTType) {
          PTType pttype = (PTType) element;
          boolean idExists = false;
          if (FC2WPPTtypeSettingsDialog.this.wsRelevantTypes != null) {
            idExists = checkIfIdExists(pttype, idExists);
          }
          if (idExists) {
            gridItem.setChecked(cell.getVisualIndex(), true);
          }
        }
        // Check Write access
        if (!FC2WPPTtypeSettingsDialog.this.editorInput.getFC2WPDefBO().canModifyAccessRights()) {
          gridItem.setCheckable(cell.getVisualIndex(), false);
        }
        else {
          gridItem.setCheckable(cell.getVisualIndex(), true);
        }
      }

      /**
       * @param pttype
       * @param idExists
       * @return
       */
      private boolean checkIfIdExists(final PTType pttype, final boolean idExists) {
        for (FC2WPRelvPTType fc2wpRelvPTType : FC2WPPTtypeSettingsDialog.this.wsRelevantTypes) {
          if (fc2wpRelvPTType.getPtTypeId().equals(pttype.getPtTypeId())) {
            return true;

          }
        }
        return idExists;
      }
    });
    return chkBoxColumn;
  }

  /**
   *
   */
  private void createDescCol() {
    GridViewerColumn descColumn = new GridViewerColumn(this.viewer, SWT.NONE);
    descColumn.getColumn().setText("Description");
    descColumn.getColumn().setWidth(150);
    descColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        PTType p = (PTType) element;
        return p.getPtTypeDesc();
      }

    });
  }

  /**
   *
   */
  private void createNameCol() {
    GridViewerColumn nameColumn = new GridViewerColumn(this.viewer, SWT.NONE);
    nameColumn.getColumn().setText("PowerTrain Type");
    nameColumn.getColumn().setWidth(100);
    nameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        PTType p = (PTType) element;
        return p.getPtTypeName();
      }
    });
  }

  /*
   * create dialog buttons
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    super.createButtonsForButtonBar(parent);
    // Check Write access
    if (!FC2WPPTtypeSettingsDialog.this.editorInput.getFC2WPDefBO().canModifyAccessRights() &&
        (getButton(IDialogConstants.OK_ID) != null)) {
      getButton(IDialogConstants.OK_ID).setEnabled(false);
    }
  }

  /**
   * after clicking ok in dialog
   */
  @Override
  protected void okPressed() {
    close();
  }
}
