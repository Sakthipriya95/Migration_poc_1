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
import org.eclipse.jface.viewers.ColumnLabelProvider;
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
import com.bosch.caltool.icdm.model.cda.ParameterFilterLabel;
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
public class CaldataAnalyzerParamFilterWizardPage extends WizardPage {

  /**
   * Toolbar manager
   */
  private ToolBarManager toolBarManager;
  /**
   * Text instance
   */
  private Text paramFilterText;

  private CaldataAnalyzerFilterActionSet actionSet;

  private AbstractViewerSorter sorter;

  // LAB file labels set
  private List<ParameterFilterLabel> labels = new ArrayList<>();
  /**
   * GridTableViewer instance
   */
  private GridTableViewer paramTabViewer;

  private CaldataAnalyzerWizardFilter paramFilter;

  private final MenuManager menuMgr = new MenuManager();

  private Menu menu;

  private static int PARAM_NAME_COLUMN = 0;

  private static int MUST_EXIST_COLUMN = 1;

  /**
   * @param pageName
   */
  public CaldataAnalyzerParamFilterWizardPage(final String pageName) {
    super(pageName);
    setTitle("Parameters filter");
    setDescription("Add necessary parameters for filtering.");
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
    sectionDataSrc.setText("Add parameters here");
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

    this.paramFilterText = new Text(dataComp, SWT.SINGLE | SWT.BORDER);
    this.paramFilterText.setLayoutData(gridData);
    createParamFilterTxt();

    this.paramTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(dataComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI, gridData);
    gridData.heightHint = 285;
    gridData.widthHint = 400;
    this.paramTabViewer.getGrid().setLayoutData(gridData);

    // set sorter
    this.sorter = new CaldataAnalyzerFilterSorter();
    this.paramTabViewer.setComparator(this.sorter);

    // create columns
    createParamColumn();
    createMustExistColumn();


    this.paramTabViewer.addFilter(this.paramFilter);
    this.paramTabViewer.setContentProvider(new CaldataAnalyzerFilterContentProvider(this));
    this.paramTabViewer.setInput(this.labels);


    this.paramTabViewer.getGrid().addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        CaldataAnalyzerParamFilterWizardPage.this.actionSet.getDeleteAction().setEnabled(true);
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionevent) {
        // TODO Auto-generated method stub

      }
    });
    this.paramTabViewer.getGrid().addKeyListener(new KeyListener() {

      @Override
      public void keyPressed(final KeyEvent arg0) {
        if (arg0.keyCode == SWT.DEL) {
          CaldataAnalyzerParamFilterWizardPage.this.actionSet.deleteFilterItems();
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

  private void addActions(final Section sectionDataSrc) {
    createToolbarAction(sectionDataSrc);
    addRightClickMenu();
  }


  private void addRightClickMenu() {


    this.menuMgr.setRemoveAllWhenShown(true);
    this.menuMgr.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(final IMenuManager arg0) {
        CaldataAnalyzerParamFilterWizardPage.this.actionSet.setPasteAction(arg0);
        CaldataAnalyzerParamFilterWizardPage.this.actionSet.setDeleteAction(arg0);
      }
    });
    // create menu
    this.menu = this.menuMgr.createContextMenu(this.paramTabViewer.getGrid());
    this.paramTabViewer.getGrid().setMenu(this.menu);


  }


  /**
   * Key listener for paste actions
   */
  private void addKeyListener() {
    this.paramTabViewer.getGrid().addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(final KeyEvent event) {
        if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.keyCode == 'v')) {
          CaldataAnalyzerParamFilterWizardPage.this.labels
              .addAll((Collection<? extends ParameterFilterLabel>) CaldataAnalyzerParamFilterWizardPage.this.actionSet
                  .getDataFromClipBoard());
          CaldataAnalyzerParamFilterWizardPage.this.paramTabViewer
              .setInput(CaldataAnalyzerParamFilterWizardPage.this.labels);
          CaldataAnalyzerParamFilterWizardPage.this.paramTabViewer.refresh();
        }
      }
    });
  }

  private void createToolbarAction(final Section section) {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = this.toolBarManager.createControl(section);
    this.actionSet = new CaldataAnalyzerFilterActionSet(this);
    this.actionSet.setImportLabAction();
    this.actionSet.setPasteAction(this.menuMgr);
    this.actionSet.setDeleteAction(this.menuMgr);
    this.toolBarManager.update(true);
    section.setTextClient(toolbar);
  }


  /**
   * This method creates filter text
   */
  private void createParamFilterTxt() {

    this.paramFilter = new CaldataAnalyzerWizardFilter();

    final GridData gridData = getFilterTxtGridData();
    this.paramFilterText.setLayoutData(gridData);
    this.paramFilterText.setMessage(CommonUIConstants.TEXT_FILTER);
    this.paramFilterText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        final String text = CaldataAnalyzerParamFilterWizardPage.this.paramFilterText.getText().trim();
        CaldataAnalyzerParamFilterWizardPage.this.paramFilter.setFilterText(text);
        CaldataAnalyzerParamFilterWizardPage.this.paramTabViewer.refresh();
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
   * create parameter name column
   */
  private void createParamColumn() {
    final GridViewerColumn paramColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.paramTabViewer, "Parameter", 500);
    paramColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        ParameterFilterLabel label = (ParameterFilterLabel) element;
        return label.getLabel();
      }

    });

    // Add column selection listener
    paramColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(paramColumn.getColumn(), PARAM_NAME_COLUMN, this.sorter, this.paramTabViewer));

  }

  private void createMustExistColumn() {
    final GridViewerColumn mustExistColumn =
        GridViewerColumnUtil.getInstance().createGridViewerCheckStyleColumn(this.paramTabViewer, "Must Exist", 135);

    mustExistColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        ParameterFilterLabel label = null;
        if (element instanceof ParameterFilterLabel) {
          label = (ParameterFilterLabel) element;
        }
        final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
        if ((label != null) && label.isMustExist()) {
          gridItem.setChecked(cell.getVisualIndex(), true);
        }
      }
    });

    mustExistColumn.setEditingSupport(new CheckEditingSupport(mustExistColumn.getViewer()) {


      /**
       * {@inheritDoc}
       */
      @Override
      public void setValue(final Object element, final Object value) {
        ParameterFilterLabel label = (ParameterFilterLabel) element;
        boolean mustExist = (boolean) value;
        label.setMustExist(mustExist);
      }
    });

    // Add column selection listener
    mustExistColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(mustExistColumn.getColumn(), MUST_EXIST_COLUMN, this.sorter, this.paramTabViewer));


  }


  /**
   * @return the paramTabViewer
   */
  public GridTableViewer getParamTabViewer() {
    return this.paramTabViewer;
  }


  /**
   * @param paramTabViewer the paramTabViewer to set
   */
  public void setParamTabViewer(final GridTableViewer paramTabViewer) {
    this.paramTabViewer = paramTabViewer;
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
   * @return the labels
   */
  public List<ParameterFilterLabel> getLabels() {
    return this.labels;

  }


  /**
   * @param labels the labels to set
   */
  public void setLabels(final List<ParameterFilterLabel> labels) {
    this.labels = labels;
  }


}
