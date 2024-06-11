/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.wizards.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.cda.FunctionFilter;
import com.bosch.caltool.icdm.ui.action.CaldataAnalyzerFilterActionSet;
import com.bosch.caltool.icdm.ui.sorters.CaldataAnalyzerFilterSorter;
import com.bosch.caltool.icdm.ui.table.filters.CaldataAnalyzerWizardFilter;
import com.bosch.caltool.icdm.ui.views.providers.CaldataAnalyzerFilterContentProvider;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author pdh2cob
 */
public class CaldataAnalyzerFunctionFilterWizardPage extends WizardPage {

  /**
   * Toolbar manager
   */
  private ToolBarManager toolBarManager;
  /**
   * Text instance
   */
  private Text filterText;

  private CaldataAnalyzerFilterActionSet actionSet;

  private AbstractViewerSorter sorter;

  // Function list
  private List<FunctionFilter> functions = new ArrayList<>();
  /**
   * GridTableViewer instance
   */
  private GridTableViewer functionTabViewer;

  private CaldataAnalyzerWizardFilter filter;

  private final MenuManager menuMgr = new MenuManager();

  private Menu menu;

  private static int FUNCTION_NAME_COLUMN = 0;

  private CellEditor[] editors;

  /**
   * @param pageName
   */
  public CaldataAnalyzerFunctionFilterWizardPage(final String pageName) {
    super(pageName);
    setTitle("Function filter");
    setDescription("Add necessary functions for filtering.");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
    createFilterSection(composite, toolkit);
    setControl(composite);
  }

  /**
   * @param composite
   * @param toolkit
   */
  private void createFilterSection(final Composite composite, final FormToolkit toolkit) {

    final GridLayout gridLayout = new GridLayout();
    final Section sectionDataSrc =
        toolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionDataSrc.setText("Add functions here");
    sectionDataSrc.getDescriptionControl().setEnabled(false);
    sectionDataSrc.setLayout(gridLayout);
    final GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.FILL;
    sectionDataSrc.setLayoutData(gridData);

    final Composite dataComp = toolkit.createComposite(sectionDataSrc);
    dataComp.setLayout(gridLayout);
    dataComp.setLayoutData(gridData);
    sectionDataSrc.setClient(dataComp);

    composite.setLayout(gridLayout);
    composite.setLayoutData(new GridData());

    this.filterText = new Text(dataComp, SWT.SINGLE | SWT.BORDER);
    this.filterText.setLayoutData(gridData);
    createFilterTxt();

    this.functionTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(dataComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI, gridData);
    gridData.heightHint = 285;
    gridData.widthHint = 400;
    this.functionTabViewer.getGrid().setLayoutData(gridData);

    // set sorter
    this.sorter = new CaldataAnalyzerFilterSorter();
    this.functionTabViewer.setComparator(this.sorter);

    // create columns
    createFunctionNameColumn();
    createFunctionVersionColumn();

    // create editing support
    this.editors = new CellEditor[1];
    TextCellEditor textEditor = new TextCellEditor(this.functionTabViewer.getGrid());
    this.editors[0] = textEditor;
    this.functionTabViewer.setCellEditors(this.editors);

    this.functionTabViewer.addFilter(this.filter);
    this.functionTabViewer.setContentProvider(new CaldataAnalyzerFilterContentProvider(this));
    this.functionTabViewer.setInput(this.functions);


    this.functionTabViewer.getGrid().addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        CaldataAnalyzerFunctionFilterWizardPage.this.actionSet.getDeleteAction().setEnabled(true);
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionevent) {
        // TODO Auto-generated method stub

      }
    });
    this.functionTabViewer.getGrid().addKeyListener(new KeyListener() {

      @Override
      public void keyPressed(final KeyEvent arg0) {
        if (arg0.keyCode == SWT.DEL) {
          CaldataAnalyzerFunctionFilterWizardPage.this.actionSet.deleteFilterItems();
        }
      }

      @Override
      public void keyReleased(final KeyEvent arg0) {
        // TODO Auto-generated method stub
      }
    });
    // add right click, toolbar actions
    addActions(sectionDataSrc);

    // add key listener for paste action
    addKeyListener();


  }

  /**
   * create parameter name column
   */
  private void createFunctionNameColumn() {
    final GridViewerColumn functionNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.functionTabViewer, "Function", 500);
    functionNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        FunctionFilter function = (FunctionFilter) element;
        return function.getFunctionName();
      }

    });

    // Add column selection listener
    functionNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        functionNameColumn.getColumn(), FUNCTION_NAME_COLUMN, this.sorter, this.functionTabViewer));

  }


  private void createFunctionVersionColumn() {
    final GridViewerColumn functionVersionColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.functionTabViewer, "Version", 135);
    functionVersionColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        FunctionFilter function = (FunctionFilter) element;
        return function.getFunctionVersion();
      }

    });


    functionVersionColumn.setEditingSupport(new EditingSupport(functionVersionColumn.getViewer()) {

      @Override
      protected void setValue(final Object element, final Object value) {
        FunctionFilter function = (FunctionFilter) element;
        String functionVersion = (String) value;
        function.setFunctionVersion(functionVersion);
        CaldataAnalyzerFunctionFilterWizardPage.this.functionTabViewer.refresh();
      }

      @Override
      protected Object getValue(final Object arg0) {
        FunctionFilter function = (FunctionFilter) arg0;
        return function.getFunctionVersion();
      }

      @Override
      protected CellEditor getCellEditor(final Object arg0) {
        return CaldataAnalyzerFunctionFilterWizardPage.this.editors[0];
      }

      @Override
      protected boolean canEdit(final Object arg0) {
        return true;
      }
    });


    // Add column selection listener
    functionVersionColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        functionVersionColumn.getColumn(), FUNCTION_NAME_COLUMN, this.sorter, this.functionTabViewer));

  }


  private void addActions(final Section sectionDataSrc) {
    createToolbarAction(sectionDataSrc);
    addRightClickMenu();
  }

  private void addRightClickMenu() {

    this.menuMgr.setRemoveAllWhenShown(true);
    this.menuMgr.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(final IMenuManager arg0) {
        CaldataAnalyzerFunctionFilterWizardPage.this.actionSet.setPasteAction(arg0);
        CaldataAnalyzerFunctionFilterWizardPage.this.actionSet.setDeleteAction(arg0);
      }
    });
    // create menu
    this.menu = this.menuMgr.createContextMenu(this.functionTabViewer.getGrid());
    this.functionTabViewer.getGrid().setMenu(this.menu);
  }


  /**
   * Key listener for paste actions
   */
  private void addKeyListener() {
    this.functionTabViewer.getGrid().addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(final KeyEvent event) {
        if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.keyCode == 'v')) {
          CaldataAnalyzerFunctionFilterWizardPage.this.functions
              .addAll((Collection<? extends FunctionFilter>) CaldataAnalyzerFunctionFilterWizardPage.this.actionSet
                  .getDataFromClipBoard());
          CaldataAnalyzerFunctionFilterWizardPage.this.functionTabViewer
              .setInput(CaldataAnalyzerFunctionFilterWizardPage.this.functions);
          CaldataAnalyzerFunctionFilterWizardPage.this.functionTabViewer.refresh();
        }
      }
    });
  }

  private void createToolbarAction(final Section section) {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = this.toolBarManager.createControl(section);
    this.actionSet = new CaldataAnalyzerFilterActionSet(this);
    this.actionSet.setPasteAction(this.menuMgr);
    this.actionSet.setDeleteAction(this.menuMgr);
    this.toolBarManager.update(true);
    section.setTextClient(toolbar);
  }


  /**
   * This method creates filter text
   */
  private void createFilterTxt() {

    this.filter = new CaldataAnalyzerWizardFilter();

    final GridData gridData = getFilterTxtGridData();
    this.filterText.setLayoutData(gridData);
    this.filterText.setMessage(CommonUIConstants.TEXT_FILTER);
    this.filterText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        final String text = CaldataAnalyzerFunctionFilterWizardPage.this.filterText.getText().trim();
        CaldataAnalyzerFunctionFilterWizardPage.this.filter.setFilterText(text);
        CaldataAnalyzerFunctionFilterWizardPage.this.functionTabViewer.refresh();
      }
    });
  }


  /**
   * This method returns filter text GridData object
   *
   * @return GridData
   */
  private GridData getFilterTxtGridData() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }


  /**
   * @return the toolBarManager
   */
  public ToolBarManager getToolBarManager() {
    return this.toolBarManager;
  }


  /**
   * @param toolBarManager the toolBarManager to set
   */
  public void setToolBarManager(final ToolBarManager toolBarManager) {
    this.toolBarManager = toolBarManager;
  }


  /**
   * @return the functionTabViewer
   */
  public GridTableViewer getFunctionTabViewer() {
    return this.functionTabViewer;
  }


  /**
   * @param functionTabViewer the functionTabViewer to set
   */
  public void setFunctionTabViewer(final GridTableViewer functionTabViewer) {
    this.functionTabViewer = functionTabViewer;
  }


  /**
   * @return the functions
   */
  public List<FunctionFilter> getFunctions() {
    return this.functions;
  }


  /**
   * @param functions the functions to set
   */
  public void setFunctions(final List<FunctionFilter> functions) {
    this.functions = functions;
  }


}
