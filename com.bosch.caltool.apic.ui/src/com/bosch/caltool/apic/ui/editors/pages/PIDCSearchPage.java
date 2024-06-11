/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.SortedSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.PIDCSearchEditor;
import com.bosch.caltool.apic.ui.editors.PIDCSearchEditorInput;
import com.bosch.caltool.apic.ui.table.filters.AttrOutlineFilter;
import com.bosch.caltool.icdm.client.bo.apic.AttrRootNode;
import com.bosch.caltool.icdm.client.bo.apic.PIDCScoutResult;
import com.bosch.caltool.icdm.client.bo.apic.PidcSearchEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UserFavUcRootNode;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * ICDM-1135
 *
 * @author bru2cob
 */
public class PIDCSearchPage extends AbstractFormPage implements ISelectionListener {


  /**
   * Search editor page columns
   */
  private static final int SASHFORM_COL = 2;

  /**
   * Searching progress stage 3
   */
  private static final int SEARCH_PROG3 = 90;
  /**
   * Searching progress stage 2
   */
  private static final int SEARCH_PROG2 = 20;
  /**
   * Searching progress stage 1
   */
  private static final int SEARCH_PROG1 = 10;

  /**
   * Sash form size limit1
   */
  private static final int WGHT2 = 4;
  /**
   * Sash form size limit2
   */
  private static final int WGHT1 = 3;

  /**
   * PIDC search editor instace
   */
  private final PIDCSearchEditor editor;
  /**
   * Instance of formtoolkit
   */
  private FormToolkit formToolkit;
  /**
   * Instance of non scrollable form
   */
  private Form nonScrollableForm;
  /**
   * Instance of sash form
   */
  private SashForm sashForm;

  /**
   * @return the sashForm
   */
  public SashForm getSashForm() {
    return this.sashForm;
  }


  /**
   * Outline filter
   */
  private AttrOutlineFilter outlineFilter;


  /**
   * @return the outlineFilter
   */
  public AttrOutlineFilter getOutlineFilter() {
    return this.outlineFilter;
  }


  /**
   * Instance of results section
   */
  private final PIDCSearchResultSection resultSection;

  /**
   * Instance of pidc search editor input
   */
  private final PIDCSearchEditorInput editorInp;
  /**
   * Instance of attributes tree section
   */
  private final PIDCSearchAttrSection attrSection;

  /**
   * @return the attrSection
   */
  public PIDCSearchAttrSection getAttrSection() {
    return this.attrSection;
  }


  /**
   * Instance of attributes tree methods
   */
  private final PIDCSearchAttrTreeUtil attrTreeUtil;


  /**
   * @return the attrTreeUtil
   */
  public PIDCSearchAttrTreeUtil getAttrTreeUtil() {
    return this.attrTreeUtil;
  }

  /**
   * @return the editorInp
   */
  public PIDCSearchEditorInput getEditorInp() {
    return this.editorInp;
  }

  /**
   * @param editor editor instance
   * @param editorId editor id
   * @param title title0..
   */
  public PIDCSearchPage(final FormEditor editor, final String editorId, final String title) {
    super(editor, editorId, title);
    this.editor = (PIDCSearchEditor) editor;
    this.resultSection = new PIDCSearchResultSection(PIDCSearchPage.this);
    this.attrSection = new PIDCSearchAttrSection(PIDCSearchPage.this);
    this.attrTreeUtil = new PIDCSearchAttrTreeUtil(PIDCSearchPage.this);
    this.editorInp = this.editor.getEditorInput();

    getDataHandler().loadData();
    // clear all the map and checkboxes when a editor is opened
    clearAll();
  }

  @Override
  public void createPartControl(final Composite parent) {

    // create a main form
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);
    this.nonScrollableForm.setText("Search Project ID Card");
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = SASHFORM_COL;

    // add a sashform with two columns
    this.sashForm = new SashForm(this.nonScrollableForm.getBody(), SWT.HORIZONTAL);
    this.sashForm.setLayout(gridLayout);
    this.sashForm.setLayoutData(gridData);

    final ManagedForm mform = new ManagedForm(parent);

    // create the form content
    createFormContent(mform);


  }


  /**
   * Create form content
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    // create attrs section
    this.attrSection.createAttributesSection();
    // create pidc search results section
    this.resultSection.createResultsSection();
    // set the width of two section
    this.sashForm.setWeights(new int[] { WGHT1, WGHT2 });
    // add listeners
    getSite().getPage().addSelectionListener(this);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PIDCSearchEditorInput getEditorInput() {
    return getEditorInp();
  }

  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }


  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  public FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }


  /**
   * Clears all the map and checkboxes
   */
  public void clearAll() {
    // clear all the maps
    PIDCSearchPage.this.getDataHandler().clearSelection();

    // set all the checkbox to unchecked
    if (PIDCSearchPage.this.attrSection.getSummaryTreeViewer() != null) {
      PIDCSearchPage.this.attrSection.getSummaryTreeViewer().setAllChecked(false);
      TreeItem[] items = PIDCSearchPage.this.attrSection.getSummaryTreeViewer().getTree().getItems();
      for (TreeItem item : items) {
        // undefined flag
        item.setImage(CommonUIConstants.COLUMN_INDEX_1,
            ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));
        // no flag
        item.setImage(CommonUIConstants.COLUMN_INDEX_2,
            ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));
        // yes flag
        item.setImage(CommonUIConstants.COLUMN_INDEX_3,
            ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));
      }
      // ICDM-1291 refresh the tree viewer
      PIDCSearchPage.this.attrSection.getSummaryTreeViewer().refresh();
    }
    // clear the table displaying the results
    if (this.resultSection.getResultTable() != null) {
      this.resultSection.getResultTable().setInput(null);
      this.resultSection.getResultTable().setSelection(null);
      this.resultSection.getResultTable().refresh();
    }

    // update the status bar msg
    updateCount();

    // disable the excel export button
    // ICDM-1213
    CommandState expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    expReportService.setExportService(false);
  }


  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart iworkbenchpart, final ISelection iselection) {
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (iworkbenchpart instanceof OutlineViewPart)) {
      outLineSelectionListener(iselection);
      // set the state of checkboxes
      PIDCSearchPage.this.attrTreeUtil.setCheckedStateOnRefresh();
      // set the state of used flag
      PIDCSearchPage.this.attrTreeUtil.setUsedFlagStateOnRefresh();
    }

  }


  /**
   * Adds the outline selection listener to the attribute tree viewer
   *
   * @param selection selection
   */
  private void outLineSelectionListener(final ISelection selection) {
    if ((selection != null) && !selection.isEmpty() && (selection instanceof IStructuredSelection)) {
      Object first = ((IStructuredSelection) selection).getFirstElement();
      // Check if selection is SuperGroup
      checkAttrSelection(first);
      if ((first instanceof AttrRootNode) || (first instanceof UseCaseRootNode) ||
          (first instanceof UserFavUcRootNode)) {
        this.outlineFilter.setGroup(false);
        this.outlineFilter.setSuperGroup(false);
        this.outlineFilter.setCommon(true);
        this.outlineFilter.setFilterText("");
      }
      else if (first instanceof IUseCaseItemClientBO) {
        final IUseCaseItemClientBO ucItem = (IUseCaseItemClientBO) first;
        this.outlineFilter.setUseCaseItem(ucItem);
      }
      else if (first instanceof FavUseCaseItemNode) {
        this.outlineFilter.setFavUseCaseItem((FavUseCaseItemNode) first);
      }
    }
    if (!PIDCSearchPage.this.attrSection.getSummaryTreeViewer().getTree().isDisposed()) {
      PIDCSearchPage.this.attrSection.getSummaryTreeViewer().refresh(false);
    }

  }


  /**
   * @param first selected element
   */
  private void checkAttrSelection(final Object first) {
    if (first instanceof AttrSuperGroup) {
      this.outlineFilter.setGroup(false);
      this.outlineFilter.setSuperGroup(true);
      this.outlineFilter.setCommon(false);
      AttrSuperGroup attrSuperGroup = (AttrSuperGroup) first;
      this.outlineFilter.setFilterText(attrSuperGroup.getName());
    }
    else if (first instanceof AttrGroup) {
      this.outlineFilter.setGroup(true);
      this.outlineFilter.setSuperGroup(false);
      AttrGroup attrGroup = (AttrGroup) first;
      this.outlineFilter.setParentSuperGroup(getEditorInput().getOutlineDataHandler().getAttrRootNode()
          .getAttrGroupModel().getAllSuperGroupMap().get(attrGroup.getSuperGrpId()).getName());
      this.outlineFilter.setFilterText(attrGroup.getName());
    }

  }

  /**
   * PIDC search based on selected attrs
   */
  public void pidcSearch() {
    final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
    dialog.create();
    try {
      if (dialog.buttonBar != null) {
        Composite composite = (Composite) dialog.buttonBar;
        for (Control control : composite.getChildren()) {
          control.dispose();
        }
      }
      dialog.run(true, false, this::searchPIDC);
    }
    catch (InvocationTargetException | InterruptedException exception) {
      CDMLogger.getInstance().error(exception.getLocalizedMessage(), exception, Activator.PLUGIN_ID);
    }
  }

  /**
   * Fetches pidcs
   *
   * @param monitor progress monitor
   * @throws DataException exception
   */
  public void searchPIDC(final IProgressMonitor monitor) {
    monitor.beginTask("Fetching Project ID Cards...", IProgressMonitor.UNKNOWN);

    monitor.worked(SEARCH_PROG1);

    monitor.worked(SEARCH_PROG2);

    // get the results based on the search conditions
    final SortedSet<PIDCScoutResult> pidcSearachRes = getDataHandler().runPidcScoutService();
    monitor.worked(SEARCH_PROG3);
    Display.getDefault().syncExec(() -> {
      // set the table input with the returned result
      PIDCSearchPage.this.resultSection.getResultTable().setInput(pidcSearachRes);
      // set the status bar msg
      setStatusBarMessage(PIDCSearchPage.this.resultSection.getResultTable());
    });
    monitor.done();
  }


  /**
   * Update selected attribute count displayed
   */
  public void updateCount() {
    if (PIDCSearchPage.this.attrSection.getCount() != null) {
      PIDCSearchPage.this.attrSection.getCount()
          .setText("Number of attributes selected: " + getDataHandler().getSelAttrCount());
    }
  }


  /**
   * Initializes the outline filter
   */
  public void initializeOutlineFilter() {
    this.outlineFilter = new AttrOutlineFilter(getEditorInput().getOutlineDataHandler());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.nonScrollableForm.getToolBarManager();
  }


  /**
   * @return data handler
   */
  @Override
  public PidcSearchEditorDataHandler getDataHandler() {
    return getEditorInput().getDataHandler();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    this.attrSection.getSummaryTreeViewer().setInput(getDataHandler().getAllAttrsSorted());
    this.attrSection.getSummaryTreeViewer().refresh();

  }

}
