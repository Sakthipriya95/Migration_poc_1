/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.wizards.pages;

import java.util.Collection;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.cda.PlatformFilter;
import com.bosch.caltool.icdm.model.cda.PlatformFilterModel;
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
public class CaldataAnalyzerECUPlatWizardPage extends WizardPage {

  /**
   * Toolbar manager
   */
  private ToolBarManager toolBarManager;

  /**
   * Text instance
   */
  private Text filterText;

  private Button inverseCheckBox;

  private CaldataAnalyzerFilterActionSet actionSet;

  private AbstractViewerSorter sorter;

  /**
   * Platform filter model
   */
  private PlatformFilterModel platformFilterModel = new PlatformFilterModel();

  /**
   * GridTableViewer instance
   */
  private GridTableViewer gridTabViewer;

  private CaldataAnalyzerWizardFilter filter;

  private final MenuManager menuMgr = new MenuManager();

  private Menu menu;

  private static int PLATFORM_COLUMN = 0;


  /**
   * @param pageName
   */
  public CaldataAnalyzerECUPlatWizardPage(final String pageName) {
    super(pageName);
    setTitle("ECU Platform filter");
    setDescription("Add necessary ecu platforms for filtering.");
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
    sectionDataSrc.setText("Add platforms here");
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

    this.gridTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(dataComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI, gridData);
    gridData.heightHint = 285;
    gridData.widthHint = 400;
    this.gridTabViewer.getGrid().setLayoutData(gridData);

    // set sorter
    this.sorter = new CaldataAnalyzerFilterSorter();
    this.gridTabViewer.setComparator(this.sorter);

    // create columns
    createPlatformColumn();

    this.gridTabViewer.addFilter(this.filter);
    this.gridTabViewer.setContentProvider(new CaldataAnalyzerFilterContentProvider(this));
    this.gridTabViewer.setInput(this.platformFilterModel.getPlatformFilterList());


    this.gridTabViewer.getGrid().addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        CaldataAnalyzerECUPlatWizardPage.this.actionSet.getDeleteAction().setEnabled(true);
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionevent) {
        // TODO Auto-generated method stub

      }
    });
    this.gridTabViewer.getGrid().addKeyListener(new KeyListener() {

      @Override
      public void keyPressed(final KeyEvent arg0) {
        if (arg0.keyCode == SWT.DEL) {
          CaldataAnalyzerECUPlatWizardPage.this.actionSet.deleteFilterItems();
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

    // add inverse checkbox
    this.inverseCheckBox = new Button(dataComp, SWT.CHECK);
    this.inverseCheckBox.setText("Inverse filter");
    this.inverseCheckBox.setSelection(this.platformFilterModel.isInverseFlag());
    this.inverseCheckBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        Button btn = (Button) event.getSource();
        CaldataAnalyzerECUPlatWizardPage.this.platformFilterModel.setInverseFlag(btn.getSelection());
      }
    });

  }

  /**
   * Key listener for paste actions
   */
  private void addKeyListener() {
    this.gridTabViewer.getGrid().addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(final KeyEvent event) {
        if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.keyCode == 'v')) {
          CaldataAnalyzerECUPlatWizardPage.this.platformFilterModel.getPlatformFilterList()
              .addAll((Collection<? extends PlatformFilter>) CaldataAnalyzerECUPlatWizardPage.this.actionSet
                  .getDataFromClipBoard());
          CaldataAnalyzerECUPlatWizardPage.this.gridTabViewer
              .setInput(CaldataAnalyzerECUPlatWizardPage.this.platformFilterModel.getPlatformFilterList());
          CaldataAnalyzerECUPlatWizardPage.this.gridTabViewer.refresh();
        }
      }
    });
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
        CaldataAnalyzerECUPlatWizardPage.this.actionSet.setPasteAction(arg0);
        CaldataAnalyzerECUPlatWizardPage.this.actionSet.setDeleteAction(arg0);
      }
    });
    // create menu
    this.menu = this.menuMgr.createContextMenu(this.gridTabViewer.getGrid());
    this.gridTabViewer.getGrid().setMenu(this.menu);
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


  private void createPlatformColumn() {
    final GridViewerColumn platformColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.gridTabViewer, "Platform", 635);
    platformColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        PlatformFilter platform = (PlatformFilter) element;
        return platform.getEcuPlatformName();
      }

    });

    // Add column selection listener
    platformColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(platformColumn.getColumn(), PLATFORM_COLUMN, this.sorter, this.gridTabViewer));

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
        final String text = CaldataAnalyzerECUPlatWizardPage.this.filterText.getText().trim();
        CaldataAnalyzerECUPlatWizardPage.this.filter.setFilterText(text);
        CaldataAnalyzerECUPlatWizardPage.this.gridTabViewer.refresh();
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
   * @return the gridTabViewer
   */
  public GridTableViewer getGridTabViewer() {
    return this.gridTabViewer;
  }


  /**
   * @param gridTabViewer the gridTabViewer to set
   */
  public void setGridTabViewer(final GridTableViewer gridTabViewer) {
    this.gridTabViewer = gridTabViewer;
  }


  /**
   * @return the platformFilterModel
   */
  public PlatformFilterModel getPlatformFilterModel() {
    return this.platformFilterModel;
  }


  /**
   * @param platformFilterModel the platformFilterModel to set
   */
  public void setPlatformFilterModel(final PlatformFilterModel platformFilterModel) {
    this.platformFilterModel = platformFilterModel;
  }


}
