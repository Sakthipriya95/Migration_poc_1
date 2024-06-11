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
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
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
import com.bosch.caltool.icdm.model.cda.SystemConstantFilter;
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
public class CaldataAnalyzerSysConFilterWizardPage extends WizardPage {

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
  private List<SystemConstantFilter> systemConstants = new ArrayList<>();
  /**
   * GridTableViewer instance
   */
  private GridTableViewer systemConstantTabViewer;

  private CaldataAnalyzerWizardFilter filter;

  private final MenuManager menuMgr = new MenuManager();

  private Menu menu;

  static int SYSTEM_CONSTANT_NAME_COLUMN = 0;

  static int SYSTEM_CONSTANT_VALUE_COLUMN = 1;

  static int NOT_COLUMN = 1;

  private CellEditor[] editors;

  /**
   * @param pageName
   */
  public CaldataAnalyzerSysConFilterWizardPage(final String pageName) {
    super(pageName);
    setTitle("System Constant filter");
    setDescription("Add necessary system constants for filtering.");
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
    sectionDataSrc.setText("Add system constants here");
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

    this.systemConstantTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(dataComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI, gridData);
    gridData.heightHint = 285;
    gridData.widthHint = 400;
    this.systemConstantTabViewer.getGrid().setLayoutData(gridData);

    // set sorter
    this.sorter = new CaldataAnalyzerFilterSorter();
    this.systemConstantTabViewer.setComparator(this.sorter);

    // create columns
    createSystemConstantNameColumn();
    createSystemConstantValueColumn();
    createNotColumn();

    // create editing support
    this.editors = new CellEditor[1];
    TextCellEditor textEditor = new TextCellEditor(this.systemConstantTabViewer.getGrid());
    this.editors[0] = textEditor;
    this.systemConstantTabViewer.setCellEditors(this.editors);

    this.systemConstantTabViewer.addFilter(this.filter);
    this.systemConstantTabViewer.setContentProvider(new CaldataAnalyzerFilterContentProvider(this));
    this.systemConstantTabViewer.setInput(this.systemConstants);


    this.systemConstantTabViewer.getGrid().addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        CaldataAnalyzerSysConFilterWizardPage.this.actionSet.getDeleteAction().setEnabled(true);
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionevent) {


      }
    });
    this.systemConstantTabViewer.getGrid().addKeyListener(new KeyListener() {

      @Override
      public void keyPressed(final KeyEvent arg0) {
        if (arg0.keyCode == SWT.DEL) {
          CaldataAnalyzerSysConFilterWizardPage.this.actionSet.deleteFilterItems();
        }
      }

      @Override
      public void keyReleased(final KeyEvent arg0) {
        // TODO Auto-generated method stub
      }
    });
    // add right click, toolbar actions
    addActions(sectionDataSrc);

    // addKeyListener
    addKeyListener();

  }


  private void addActions(final Section sectionDataSrc) {
    createToolbarAction(sectionDataSrc);
    addRightClickMenu();
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

  private void addRightClickMenu() {


    this.menuMgr.setRemoveAllWhenShown(true);
    this.menuMgr.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(final IMenuManager arg0) {
        CaldataAnalyzerSysConFilterWizardPage.this.actionSet.setPasteAction(arg0);
        CaldataAnalyzerSysConFilterWizardPage.this.actionSet.setDeleteAction(arg0);
      }
    });
    // create menu
    this.menu = this.menuMgr.createContextMenu(this.systemConstantTabViewer.getGrid());
    this.systemConstantTabViewer.getGrid().setMenu(this.menu);


  }


  /**
   * Key listener for paste actions
   */
  private void addKeyListener() {
    this.systemConstantTabViewer.getGrid().addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(final KeyEvent event) {
        if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.keyCode == 'v')) {
          CaldataAnalyzerSysConFilterWizardPage.this.systemConstants
              .addAll((Collection<? extends SystemConstantFilter>) CaldataAnalyzerSysConFilterWizardPage.this.actionSet
                  .getDataFromClipBoard());
          CaldataAnalyzerSysConFilterWizardPage.this.systemConstantTabViewer
              .setInput(CaldataAnalyzerSysConFilterWizardPage.this.systemConstants);
          CaldataAnalyzerSysConFilterWizardPage.this.systemConstantTabViewer.refresh();
        }
      }
    });
  }


  /**
   * Method to create system constant name column
   */
  private void createSystemConstantNameColumn() {
    final GridViewerColumn systemConstantNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.systemConstantTabViewer, "System Constant", 400);
    systemConstantNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        SystemConstantFilter systemConstant = (SystemConstantFilter) element;
        return systemConstant.getSystemConstantName();
      }

    });

    // Add column selection listener
    systemConstantNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        systemConstantNameColumn.getColumn(), SYSTEM_CONSTANT_NAME_COLUMN, this.sorter, this.systemConstantTabViewer));

  }


  private void createSystemConstantValueColumn() {
    final GridViewerColumn systemConstantValueColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.systemConstantTabViewer, "Value", 120);
    systemConstantValueColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        SystemConstantFilter systemConstant = (SystemConstantFilter) element;
        return systemConstant.getSystemConstantValue();
      }

    });


    systemConstantValueColumn.setEditingSupport(new EditingSupport(systemConstantValueColumn.getViewer()) {

      @Override
      protected void setValue(final Object element, final Object value) {
        SystemConstantFilter systemConstant = (SystemConstantFilter) element;
        String sysConValue = (String) value;
        systemConstant.setSystemConstantValue(sysConValue);
        CaldataAnalyzerSysConFilterWizardPage.this.systemConstantTabViewer.refresh();
      }

      @Override
      protected Object getValue(final Object arg0) {
        SystemConstantFilter systemConstant = (SystemConstantFilter) arg0;
        return systemConstant.getSystemConstantValue();
      }

      @Override
      protected CellEditor getCellEditor(final Object arg0) {
        return CaldataAnalyzerSysConFilterWizardPage.this.editors[0];
      }

      @Override
      protected boolean canEdit(final Object arg0) {
        return true;
      }
    });


    // Add column selection listener
    systemConstantValueColumn.getColumn().addSelectionListener(
        GridTableViewerUtil.getInstance().getSelectionAdapter(systemConstantValueColumn.getColumn(),
            SYSTEM_CONSTANT_VALUE_COLUMN, this.sorter, this.systemConstantTabViewer));

  }


  private void createNotColumn() {
    final GridViewerColumn notColumn =
        GridViewerColumnUtil.getInstance().createGridViewerCheckStyleColumn(this.systemConstantTabViewer, "Not", 115);
    notColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        SystemConstantFilter syscon = null;
        if (element instanceof SystemConstantFilter) {
          syscon = (SystemConstantFilter) element;
        }
        final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
        if ((syscon != null) && syscon.isInverseFlag()) {
          gridItem.setChecked(cell.getVisualIndex(), true);
        }
      };

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return "";
      }
    });

    notColumn.setEditingSupport(new CheckEditingSupport(notColumn.getViewer()) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void setValue(final Object element, final Object value) {
        SystemConstantFilter filter = (SystemConstantFilter) element;
        boolean inverseFlag = (boolean) value;
        filter.setInverseFlag(inverseFlag);
      }
    });

    // Add column selection listener
    notColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(notColumn.getColumn(), NOT_COLUMN, this.sorter, this.systemConstantTabViewer));


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
        final String text = CaldataAnalyzerSysConFilterWizardPage.this.filterText.getText().trim();
        CaldataAnalyzerSysConFilterWizardPage.this.filter.setFilterText(text);
        CaldataAnalyzerSysConFilterWizardPage.this.systemConstantTabViewer.refresh();
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
   * @return the systemConstantTabViewer
   */
  public GridTableViewer getSystemConstantTabViewer() {
    return this.systemConstantTabViewer;
  }


  /**
   * @param systemConstantTabViewer the systemConstantTabViewer to set
   */
  public void setSystemConstantTabViewer(final GridTableViewer systemConstantTabViewer) {
    this.systemConstantTabViewer = systemConstantTabViewer;
  }


  /**
   * @return the systemConstants
   */
  public List<SystemConstantFilter> getSystemConstants() {
    return this.systemConstants;
  }


  /**
   * @param systemConstants the systemConstants to set
   */
  public void setSystemConstants(final List<SystemConstantFilter> systemConstants) {
    this.systemConstants = systemConstants;
  }


}
