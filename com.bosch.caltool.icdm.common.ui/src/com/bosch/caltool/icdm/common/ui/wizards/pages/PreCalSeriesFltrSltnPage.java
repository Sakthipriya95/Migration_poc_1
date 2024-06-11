/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards.pages;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldataphy.AtomicValuePhy;
import com.bosch.calmodel.caldataphy.CalDataPhyValue;
import com.bosch.caltool.apic.ws.client.ParameterStatisticCallbackHandler;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.DefaultSeriesStatisticsFilter;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.ISeriesStatisticsFilter;
import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFilterBaseComponents;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFilterFunction;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFilterParameter;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParamInfo;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.precal.PreCalDataWizardDataHandler;
import com.bosch.caltool.icdm.client.bo.ss.SeriesStatisticsInfo;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.jobs.PreCalDataRetrieveJob;
import com.bosch.caltool.icdm.common.ui.listeners.GridTableEditingSupport;
import com.bosch.caltool.icdm.common.ui.sorters.CDFXportBCTabSorter;
import com.bosch.caltool.icdm.common.ui.sorters.CDFXportFCTabSorter;
import com.bosch.caltool.icdm.common.ui.sorters.CDFXportParamTabSorter;
import com.bosch.caltool.icdm.common.ui.sorters.CDFXportSysCnstTabSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.CDFXportBCFilter;
import com.bosch.caltool.icdm.common.ui.table.filters.CDFXportFCFilter;
import com.bosch.caltool.icdm.common.ui.table.filters.CDFXportParamFilter;
import com.bosch.caltool.icdm.common.ui.table.filters.CDFXportSysCnstFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizard;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstantValues;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;

/**
 * icdm-879 This page is to make filter criteria selection for Series Statistics.
 *
 * @author mkl2cob
 */
public class PreCalSeriesFltrSltnPage extends WizardPage {


  /** Column width of max value. */
  private static final int COLWIDTH_MAX_VAL = 100;

  /** Parameter col tooltip buffer intial size. */
  private static final int PARAM_TOOLTIP_SIZE = 80;

  /** sysconsts col width. */
  private static final int SYSCONST_COL_WIDTH = 400;

  /** value col width. */
  private static final int VAL_COL_WIDTH = 200;

  /** param col width. */
  private static final int PARAM_COL_WIDTH = 150;

  /** minimum col width. */
  private static final int MIN_COL_WIDTH = 100;

  /** class col width. */
  private static final int CLASS_COL_WIDTH = 100;

  /** unit col width. */
  private static final int UNIT_COL_WIDTH = 70;

  /** Colour code for blue. */
  private static final int COLOR_BLUE = 0;

  /** The Constant COLOR_GREEN. */
  private static final int COLOR_GREEN = 127;

  /** The Constant COLOR_RED. */
  private static final int COLOR_RED = 255;

  /** The Constant COL_WIDTH_1. */
  private static final int COL_WIDTH_1 = 250;

  /** Col width. */
  private static final int COL_WIDTH = 150;

  /** Delete confirmation. */
  private static final String DELETE_CONFIRMATION = "Delete Confirmation";

  /** tooltip string builder initial size. */
  private static final int TOOLTIP_INIT_SIZE = 70;

  /** constant for ok pressed in a dialog. */
  private static final int OK_PRESSED = 0;

  /** Bc name col width. */
  private static final int BC_NAME_COL_SIZE = 250;

  /** Bc version col width. */
  private static final int BC_VER_COL_SIZE = 150;

  /** Message for Parameters table. */
  private static final String PARAMETERS_MSG = "Use Drag & Drop to add Parameters";

  /** Message for System constants table. */
  private static final String SYS_CONST_MSG = "Use Drag & Drop to add System Constants";

  /** Message for BC table. */
  private static final String BC_MSG = "Use Drag & Drop to add Base Components";

  /** Message for FC table. */
  private static final String FC_MSG = "Use Drag & Drop to add Function";

  /** Button instance. */
  private Button recomValue;

  /** Button instance. */
  private Button filterValue;

  /** GridTableViewer instance. */
  private GridTableViewer paramTabViewer;

  /** GridTableViewer instance. */
  private GridTableViewer fcTabViewer;

  /** GridTableViewer instance. */
  private GridTableViewer sysCnstTabViewer;

  /** filterParamTxt Text instance. */
  private Text filterParamTxt;

  /** Text instance. */
  private Text filterSysCnstTxt;

  /** Text instance. */
  private Text filterFCtTxt;

  /** CDFXportSysCnstTabSorter instance. */
  private CDFXportSysCnstTabSorter sysCnstSorter;

  /** CDFXportParamTabSorter instance. */
  private CDFXportParamTabSorter paramTableSorter;

  /** SortedSet of A2LFilterBaseComponents objects. */
  private final SortedSet<A2LFilterBaseComponents> bcs = new TreeSet<>();

  /** CDFXportFCTabSorter instance. */
  private CDFXportFCTabSorter fcTableSorter;

  /** SortedSet of A2LFilterParameter objs. */
  private final SortedSet<A2LFilterParameter> params = new TreeSet<>();

  /** SortedSet of A2LFilterFunction objs. */
  private final SortedSet<A2LFilterFunction> fcs = new TreeSet<>();

  /** SortedSet of A2LBaseComponents objs. */
  private SortedSet<A2LBaseComponents> a2lBC;

  /** SortedSet of A2LSystemConstantValues objs. */
  private final SortedSet<A2LSystemConstantValues> sysConts = new TreeSet<>();

  /** hasComplexValue flag. */
  private boolean hasComplexValue;

  /** Color instance. */
  private final Color orange = new Color(Display.getCurrent(), COLOR_RED, COLOR_GREEN, COLOR_BLUE);

  /** CDFXportParamFilter instance. */
  private CDFXportParamFilter paramTabFilter;

  /** CDFXportSysCnstFilter instance. */
  private CDFXportSysCnstFilter sysCnstFilter;

  /** CDFXportFCFilter instance. */
  private CDFXportFCFilter fcFilter;

  /** Action instance. */
  private Action deleteRowAction;

  /** TabFolder. */
  private TabFolder tabFolder;

  /** RetrieveJob instance. */
  private PreCalDataRetrieveJob retrieveJob;

  /** Text instance. */
  private Text filterBCTxt;

  /** CDFXportBCFilter instance. */
  private CDFXportBCFilter bcTabFilter;

  /** CDFXportBCTabSorter instance. */
  private CDFXportBCTabSorter bcTableSorter;

  /** GridTableViewer instance. */
  private GridTableViewer bcTabViewer;

  /** thread sleep time constant. */
  private static final int THREAD_SLEEP = 1000;

  /** index. */
  private static final int TAB_INDEX_3 = 3;

  /** index. */
  private static final int TAB_INDEX_1 = 1;

  /** index. */
  private static final int TAB_INDEX_2 = 2;

  /** index. */
  private static final int TAB_INDEX_0 = 0;

  /** Job cancel success message one. */
  private static final String SUCC_JOB_CAN1 = "true: Web-service call getParameterStatisticsExt for session";

  /** Job cancel success message two. */
  private static final String SUCC_JOB_CAN2 = "successfully cancelled after user request";

  /** Job cancel failure message one. */
  private static final String FAIL_JOB_CAN1 = "false: Web-service call getParameterStatisticsExt for session";

  /** Job cancel failure message two. */
  private static final String FAIL_JOB_CAN2 = "couldn't be cancelled after user request.";

  /**
   * Instantiates a new series fltr sltn page.
   *
   * @param pageName page Name
   * @param a2lFileName A2l File Name
   */
  public PreCalSeriesFltrSltnPage(final String pageName, final String a2lFileName) {
    super(pageName);
    setTitle("Get Pre-Calibration Data for : " + a2lFileName);
    setDescription("Please select the data to be fetched and filter criteria");
  }

  /**
   * @return the A2LEditorDataHandler
   */
  private A2LFileInfoBO getA2lFileInfoBO() {
    return getDataHandler().getA2lFileInfoBO();
  }

  /**
   * @return the paramList
   */
  private List<A2LParameter> getParamList() {
    return getDataHandler().getParamList();
  }

  /**
   * Gets the filter value.
   *
   * @return the filterValue
   */
  public Button getFilterValue() {
    return this.filterValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    final Composite myComp = new Composite(parent, SWT.NONE);
    final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
    createDataFecthGrp(myComp, toolkit);
    setControl(myComp);

  }


  /**
   * Creates the data fecth grp.
   *
   * @param myComp Composite
   * @param toolkit FormToolkit
   */
  private void createDataFecthGrp(final Composite myComp, final FormToolkit toolkit) {

    final GridLayout gridLayout = new GridLayout();
    // ICDM-880
    final Section sectionDataSrc = toolkit.createSection(myComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionDataSrc.setText("Data to be fetched");
    sectionDataSrc.getDescriptionControl().setEnabled(false);
    sectionDataSrc.setLayout(gridLayout);
    final GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    sectionDataSrc.setLayoutData(gridData);

    final Composite dataComp = toolkit.createComposite(sectionDataSrc, SWT.NONE);
    dataComp.setLayout(gridLayout);
    dataComp.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.recomValue = new Button(dataComp, SWT.CHECK);
    this.recomValue.setText("Most Frequent Value");
    this.recomValue.setSelection(true);
    this.recomValue.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        getContainer().updateButtons();
      }
    });

    LabelUtil.getInstance().createEmptyLabel(dataComp);
    LabelUtil.getInstance().createEmptyLabel(dataComp);

    sectionDataSrc.setClient(dataComp);

    createFltrCriteriaGrp(myComp, gridLayout, toolkit);
  }

  /**
   * ICDM-879 creating group for filter group.
   *
   * @param myComp Composite
   * @param gridLayout GridLayout
   * @param toolkit FormToolkit
   */
  private void createFltrCriteriaGrp(final Composite myComp, final GridLayout gridLayout, final FormToolkit toolkit) {

    myComp.setLayout(gridLayout);
    myComp.setLayoutData(new GridData());
    createSection(myComp, toolkit);
  }


  /**
   * This method initializes section for filter criteria.
   *
   * @param myComp Group
   * @param toolkit FormToolkit
   */
  private void createSection(final Composite myComp, final FormToolkit toolkit) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final GridLayout gridLayout = new GridLayout();

    final Section section = toolkit.createSection(myComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    section.setText("Filter Criteria");
    section.setDescription(BC_MSG);


    final GridData sectionGridData = new GridData();
    sectionGridData.grabExcessHorizontalSpace = true;
    sectionGridData.grabExcessVerticalSpace = true;

    sectionGridData.horizontalAlignment = GridData.FILL;
    sectionGridData.verticalAlignment = GridData.FILL;

    section.setLayout(gridLayout);

    final Composite compForTabs = toolkit.createComposite(section, SWT.NONE);
    compForTabs.setLayout(gridLayout);
    compForTabs.setLayoutData(gridData);

    this.tabFolder = new TabFolder(compForTabs, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BOTTOM | SWT.NONE);
    this.tabFolder.setLayout(gridLayout);
    this.tabFolder.setLayoutData(gridData);
    // ICDM-1011
    final TabItem bcTabItem = new TabItem(this.tabFolder, SWT.NONE);

    bcTabItem.setText("BC");
    bcTabItem.setControl(createBCTab(this.tabFolder, gridLayout));


    final TabItem fcTabItem = new TabItem(this.tabFolder, SWT.NONE);
    fcTabItem.setText("FC");
    fcTabItem.setControl(createFCTab(this.tabFolder, gridLayout));

    final TabItem paramTabItem = new TabItem(this.tabFolder, SWT.NONE);

    paramTabItem.setText("Parameter");
    paramTabItem.setControl(createParamTab(this.tabFolder, gridLayout));

    final TabItem sysCnstTabItem = new TabItem(this.tabFolder, SWT.NONE);
    sysCnstTabItem.setText("System Constants");
    sysCnstTabItem.setControl(createSysConstTab(this.tabFolder, gridLayout));
    this.tabFolder.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        IStructuredSelection selection = null;
        if (PreCalSeriesFltrSltnPage.this.tabFolder.getSelectionIndex() == TAB_INDEX_1) {
          section.setDescription(FC_MSG);
          selection = (IStructuredSelection) PreCalSeriesFltrSltnPage.this.fcTabViewer.getSelection();
        }
        else if (PreCalSeriesFltrSltnPage.this.tabFolder.getSelectionIndex() == TAB_INDEX_2) {
          section.setDescription(PARAMETERS_MSG);
          selection = (IStructuredSelection) PreCalSeriesFltrSltnPage.this.paramTabViewer.getSelection();
        }
        // ICDM-1011
        else if (PreCalSeriesFltrSltnPage.this.tabFolder.getSelectionIndex() == TAB_INDEX_0) {
          section.setDescription(BC_MSG);
          selection = (IStructuredSelection) PreCalSeriesFltrSltnPage.this.bcTabViewer.getSelection();
        }
        else if (PreCalSeriesFltrSltnPage.this.tabFolder.getSelectionIndex() == TAB_INDEX_3) {
          section.setDescription(SYS_CONST_MSG);
          selection = (IStructuredSelection) PreCalSeriesFltrSltnPage.this.sysCnstTabViewer.getSelection();
        }
        enableDisableDelAction(selection);
      }
    });

    section.setLayoutData(sectionGridData);
    section.setClient(compForTabs);
    setDropSupport();
    createToolBarAction(section);
  }


  /**
   * Enable disable del action.
   *
   * @param selection IStructuredSelection from TabViewer
   */
  private void enableDisableDelAction(final IStructuredSelection selection) {

    PreCalSeriesFltrSltnPage.this.deleteRowAction
        .setEnabled(!((selection != null) && (selection.getFirstElement() == null)));

  }

  /**
   * This method creates Section ToolBar actions.
   *
   * @param section Section
   */
  private void createToolBarAction(final Section section) {

    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(section);

    addDeleteValueActionToSection(toolBarManager);

    toolBarManager.update(true);

    section.setTextClient(toolbar);
  }

  /**
   * creates delete user icon in the toolbar and handles the action.
   *
   * @param toolBarManager the tool bar manager
   */
  private void addDeleteValueActionToSection(final ToolBarManager toolBarManager) {
    // Create an action to delete the parameter or system constants
    this.deleteRowAction = new Action("Delete", SWT.NONE) {

      // ICDM-886
      @Override
      public void run() {
        IStructuredSelection selection = null;
        if (PreCalSeriesFltrSltnPage.this.tabFolder.getSelectionIndex() == TAB_INDEX_1) {
          delFCs();
          selection = (IStructuredSelection) PreCalSeriesFltrSltnPage.this.fcTabViewer.getSelection();
        }
        else if (PreCalSeriesFltrSltnPage.this.tabFolder.getSelectionIndex() == TAB_INDEX_2) {
          delParams();
          selection = (IStructuredSelection) PreCalSeriesFltrSltnPage.this.paramTabViewer.getSelection();
        }
        else if (PreCalSeriesFltrSltnPage.this.tabFolder.getSelectionIndex() == TAB_INDEX_3) {
          delSysConsts();
          selection = (IStructuredSelection) PreCalSeriesFltrSltnPage.this.sysCnstTabViewer.getSelection();
        }
        // ICDM-1011
        else if (PreCalSeriesFltrSltnPage.this.tabFolder.getSelectionIndex() == TAB_INDEX_0) {
          delBcs();
          selection = (IStructuredSelection) PreCalSeriesFltrSltnPage.this.bcTabViewer.getSelection();
        }
        enableDisableDelAction(selection);
      }
    };
    // Set the image for delete the user
    this.deleteRowAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    this.deleteRowAction.setEnabled(false);
    toolBarManager.add(this.deleteRowAction);
  }

  /**
   * ICDM-886 deleting params.
   */
  protected void delParams() {
    IStructuredSelection sel;
    sel = (IStructuredSelection) PreCalSeriesFltrSltnPage.this.paramTabViewer.getSelection();
    final SortedSet<A2LFilterParameter> delSysParam = new TreeSet<>();
    if (sel != null) {
      final Iterator<?> selParams = sel.iterator();
      while (selParams.hasNext()) {
        final A2LFilterParameter sysParam = (A2LFilterParameter) selParams.next();
        delSysParam.add(sysParam);
      }
      // if single value delete directly
      if (!delSysParam.isEmpty() && (delSysParam.size() == 1)) {
        PreCalSeriesFltrSltnPage.this.params.removeAll(delSysParam);
      }
      // if multiple values confirm before deleting
      else if (!delSysParam.isEmpty() && (delSysParam.size() > 1)) {
        final MessageDialog dialog = new MessageDialog(getShell(), DELETE_CONFIRMATION, null,
            "Do you really want to delete the selected parameters from the filter criteria?",
            MessageDialog.QUESTION_WITH_CANCEL, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL },
            0);
        dialog.open();
        final int btnSel = dialog.getReturnCode();
        if (btnSel == 0) {
          PreCalSeriesFltrSltnPage.this.params.removeAll(delSysParam);
        }
      }
      PreCalSeriesFltrSltnPage.this.paramTabViewer.setInput(PreCalSeriesFltrSltnPage.this.params);
    }

  }

  /**
   * Creates Group for system constants tab.
   *
   * @param parent the parent
   * @param gridLayout the grid layout
   * @return Created Tab Group
   */
  private Control createSysConstTab(final TabFolder parent, final GridLayout gridLayout) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final Composite filterSysCnstComp = new Composite(parent, SWT.NONE);
    filterSysCnstComp.setLayout(gridLayout);
    filterSysCnstComp.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.filterSysCnstTxt = new Text(filterSysCnstComp, SWT.SINGLE | SWT.BORDER);
    this.filterSysCnstTxt.setLayoutData(gridData);
    createSysCnstFilterTxt();
    this.sysCnstFilter = new CDFXportSysCnstFilter();

    this.sysCnstSorter = new CDFXportSysCnstTabSorter();

    this.sysCnstTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(filterSysCnstComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI, gridData);

    createSysCnsntTabColumns();

    this.sysCnstTabViewer.setComparator(this.sysCnstSorter);
    this.sysCnstTabViewer.addFilter(this.sysCnstFilter);
    this.sysCnstTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    return filterSysCnstComp;
  }

  /**
   * creates columns for the System constant table.
   */
  private void createSysCnsntTabColumns() {
    createSysConstName();
    createValColumn();
    // ICDM-886
    ColumnViewerToolTipSupport.enableFor(this.sysCnstTabViewer, ToolTip.NO_RECREATE);

    this.sysCnstTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) PreCalSeriesFltrSltnPage.this.sysCnstTabViewer.getSelection();
        if (selection != null) {
          PreCalSeriesFltrSltnPage.this.deleteRowAction.setEnabled(true);
        }
      }
    });


  }


  /**
   * creates columns for the BC table.
   */
  private void createFCTabColumns() {
    createFCName();
    createFuncVersionColumn();

    ColumnViewerToolTipSupport.enableFor(this.fcTabViewer, ToolTip.NO_RECREATE);

    this.fcTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) PreCalSeriesFltrSltnPage.this.fcTabViewer.getSelection();
        if (selection != null) {
          PreCalSeriesFltrSltnPage.this.deleteRowAction.setEnabled(true);
        }
      }
    });


  }

  /**
   * creates the column for System Constant Value.
   */
  private void createValColumn() {
    final GridViewerColumn valueColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.sysCnstTabViewer, "Value", VAL_COL_WIDTH);
    valueColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        // ICDM-886
        if (element instanceof A2LSystemConstantValues) {
          return ((A2LSystemConstantValues) element).getValue();
        }
        return "";
      }

      @Override
      public Color getBackground(final Object element) {
        if (!getDataHandler().getSysConts().contains(element)) {
          return PreCalSeriesFltrSltnPage.this.orange;
        }
        return null;
      }
    });

    valueColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(valueColumn.getColumn(), 0, this.sysCnstSorter, this.sysCnstTabViewer));

  }


  /**
   * creates the column for bc version.
   */
  private void createFuncVersionColumn() {
    final GridViewerColumn vrsnColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.fcTabViewer, "Version", COL_WIDTH);
    vrsnColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {

        if (element instanceof A2LFilterFunction) {
          return null == (((A2LFilterFunction) element).getFunctionVersion()) ? "*"
              : ((A2LFilterFunction) element).getFunctionVersion();
        }
        return "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Image getImage(final Object element) {
        return ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_12X12);
      }

      @Override
      public Color getBackground(final Object element) {
        if (!getDataHandler().getFCs().contains(((A2LFilterFunction) element).getA2lFunction())) {
          return PreCalSeriesFltrSltnPage.this.orange;
        }
        return null;
      }
    });
    vrsnColumn.setEditingSupport(new GridTableEditingSupport(this.fcTabViewer, CommonUIConstants.COLUMN_INDEX_1));
    vrsnColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        vrsnColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_1, this.fcTableSorter, this.fcTabViewer));

  }

  /**
   * creates the column for System Constant Name.
   */
  private void createSysConstName() {

    final GridViewerColumn sysCnstNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.sysCnstTabViewer, "System Constant Name", SYSCONST_COL_WIDTH);
    sysCnstNameColumn.setLabelProvider(new ColumnLabelProvider() {

      // ICDM-886
      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {

        if (element instanceof A2LSystemConstantValues) {
          return ((A2LSystemConstantValues) element).getSysconName();
        }
        return "";

      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        String longName = ((A2LSystemConstantValues) element).getSysconName();
        if (CommonUtils.isEmptyString(longName)) {
          longName = "Long Name is not available";
        }
        String valDesc = ((A2LSystemConstantValues) element).getValueDescription();
        if (CommonUtils.isEmptyString(valDesc)) {
          valDesc = "Value Description not available";
        }
        StringBuilder toolTip = new StringBuilder(PARAM_TOOLTIP_SIZE);
        toolTip.append("LongName of System constant :").append(longName).append("\nValue Description :")
            .append(valDesc);
        if (!getDataHandler().getSysConts().contains(element)) {
          toolTip.append("\nThis system constant is not a part of a2l that has been used to start the export");

        }
        return toolTip.toString();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getBackground(final Object element) {
        if (!getDataHandler().getSysConts().contains(element)) {
          return PreCalSeriesFltrSltnPage.this.orange;
        }
        return null;
      }
    });

    sysCnstNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(sysCnstNameColumn.getColumn(), 1, this.sysCnstSorter, this.sysCnstTabViewer));
  }

  /**
   * creates the column for Bc Name.
   */
  private void createFCName() {

    final GridViewerColumn fcNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.fcTabViewer, "FC Name", COL_WIDTH_1);
    fcNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * long name length
       */
      private static final int LONG_NAME_LENGTH = 95;

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {

        if (element instanceof A2LFilterFunction) {
          return ((A2LFilterFunction) element).getName();
        }
        return "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        String longName = ((A2LFilterFunction) element).getLongIdentifier();
        if (longName.isEmpty()) {
          longName = "Long Name is not available";
        }
        final StringBuilder toolTip = new StringBuilder(LONG_NAME_LENGTH);
        toolTip.append("LongName of Function :").append(longName);


        if (!getDataHandler().getFCs().contains(((A2LFilterFunction) element).getA2lFunction())) {
          toolTip.append("\nThis function is not a part of a2l that has been used to start the export");
        }
        return toolTip.toString();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getBackground(final Object element) {
        if (!getDataHandler().getFCs().contains(((A2LFilterFunction) element).getA2lFunction())) {
          return PreCalSeriesFltrSltnPage.this.orange;
        }
        return null;
      }
    });

    fcNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(fcNameColumn.getColumn(), 0, this.fcTableSorter, this.fcTabViewer));

  }

  /**
   * Creates Group for BC tab.
   *
   * @param parent the parent
   * @param gridLayout GridLayout
   * @return Created Tab Group
   */
  private Control createFCTab(final TabFolder parent, final GridLayout gridLayout) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final Composite filterFCComp = new Composite(parent, SWT.NONE);
    filterFCComp.setLayout(gridLayout);
    filterFCComp.setLayoutData(gridData);

    this.filterFCtTxt = new Text(filterFCComp, SWT.SINGLE | SWT.BORDER);
    this.filterFCtTxt.setLayoutData(gridData);
    createFCFilterTxt();
    this.fcFilter = new CDFXportFCFilter();

    this.fcTableSorter = new CDFXportFCTabSorter();

    this.fcTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(filterFCComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI, gridData);
    createFCTabColumns();

    this.fcTabViewer.setComparator(this.fcTableSorter);
    this.fcTabViewer.addFilter(this.fcFilter);
    this.fcTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    return filterFCComp;
  }

  /**
   * Creates Group for parameter tab.
   *
   * @param parent the parent
   * @param gridLayout GridLayout
   * @return Created Tab Group
   */
  private Control createBCTab(final TabFolder parent, final GridLayout gridLayout) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final Composite filterBCComp = new Composite(parent, SWT.NONE);
    filterBCComp.setLayout(gridLayout);
    filterBCComp.setLayoutData(gridData);

    this.filterBCTxt = new Text(filterBCComp, SWT.SINGLE | SWT.BORDER);
    this.filterBCTxt.setLayoutData(gridData);
    createBCFilterTxt();
    this.bcTabFilter = new CDFXportBCFilter();

    this.bcTableSorter = new CDFXportBCTabSorter();

    this.bcTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(filterBCComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI, gridData);
    createBCTabColumns();

    this.bcTabViewer.setComparator(this.bcTableSorter);
    this.bcTabViewer.addFilter(this.bcTabFilter);
    this.bcTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    return filterBCComp;
  }


  /**
   * Creates Group for parameter tab.
   *
   * @param parent the parent
   * @param gridLayout GridLayout
   * @return Created Tab Group
   */
  private Control createParamTab(final TabFolder parent, final GridLayout gridLayout) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final Composite filterParamComp = new Composite(parent, SWT.NONE);
    filterParamComp.setLayout(gridLayout);
    filterParamComp.setLayoutData(gridData);

    this.filterParamTxt = new Text(filterParamComp, SWT.SINGLE | SWT.BORDER);
    this.filterParamTxt.setLayoutData(gridData);
    createParamFilterTxt();
    this.paramTabFilter = new CDFXportParamFilter();

    this.paramTableSorter = new CDFXportParamTabSorter();

    this.paramTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(filterParamComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI, gridData);
    createTabColumns();

    this.paramTabViewer.setComparator(this.paramTableSorter);
    this.paramTabViewer.addFilter(this.paramTabFilter);
    this.paramTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    return filterParamComp;
  }

  /**
   * ICDM-886 Drag listener to add the data dropped in the table.
   */
  private void setDropSupport() {
    final Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    final DropTarget target =
        new DropTarget(this.tabFolder, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
    target.setTransfer(transferTypes);
    target.addDropListener(new DropTargetAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void dragEnter(final DropTargetEvent event) {

        if (event.detail == DND.DROP_DEFAULT) {
          event.detail = DND.DROP_COPY;
        }
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void dragOperationChanged(final DropTargetEvent event) {
        if (event.detail == DND.DROP_DEFAULT) {
          event.detail = DND.DROP_COPY;
        }
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void drop(final DropTargetEvent event) {
        PreCalSeriesFltrSltnPage.this.hasComplexValue = false;
        if (event.data == null) {
          event.detail = DND.DROP_NONE;
          return;
        }
        final Object dragData = event.data;
        final IStructuredSelection strucSelec = (StructuredSelection) dragData;
        final Iterator<?> selections = strucSelec.iterator();
        getSelElements(selections);
        bcsDropped();
        fcsDropped();
        paramDropped();
        sysConstDropped();
        // if the param value type is complex , it is not set
        complexValDropped();
      }


    });
  }


  /**
   * ICDM-886 get the selected param/sys contants.
   *
   * @param selections the selections
   */
  private void getSelElements(final Iterator<?> selections) {
    while (selections.hasNext()) {
      final Object sel = selections.next();
      if (sel instanceof A2LParamInfo) {
        PreCalSeriesFltrSltnPage.this.tabFolder.setSelection(TAB_INDEX_2);
        final A2LParamInfo paramInfo = (A2LParamInfo) sel;
        final A2LParameter a2lParam = paramInfo.getA2lParam();
        if ("VALUE".equalsIgnoreCase(a2lParam.getType())) {
          final A2LFilterParameter a2lFilParam = new A2LFilterParameter(a2lParam);
          // set param's a2l name
          a2lFilParam.setParamA2LName(paramInfo.getA2lFileName());
          // if param has value already, it should be set as min and max value
          isParamValAvialable(a2lParam, a2lFilParam);
          PreCalSeriesFltrSltnPage.this.params.add(a2lFilParam);
        }
        else {
          PreCalSeriesFltrSltnPage.this.hasComplexValue = true;
        }
      }
      else if (sel instanceof A2LSystemConstantValues) {
        PreCalSeriesFltrSltnPage.this.tabFolder.setSelection(TAB_INDEX_3);
        final A2LSystemConstantValues a2lSysConst = (A2LSystemConstantValues) sel;
        PreCalSeriesFltrSltnPage.this.sysConts.add(a2lSysConst);
      }
      else if (sel instanceof Function) {
        PreCalSeriesFltrSltnPage.this.tabFolder.setSelection(TAB_INDEX_1);
        final Function func = (Function) sel;
        A2LFilterFunction filterFunc = new A2LFilterFunction(func);
        PreCalSeriesFltrSltnPage.this.fcs.add(filterFunc);
      }
      // ICDM-935
      else if (sel instanceof SeriesStatisticsInfo) {
        final CalData calData = ((SeriesStatisticsInfo) sel).getCalData();
        final String droppedParamType = calData.getCalDataPhy().getType();
        final String droppedParamVal = calData.getCalDataPhy().getSimpleDisplayValue();
        if ("VALUE".equalsIgnoreCase(droppedParamType)) {
          final A2LFilterParameter a2lFilParam = new A2LFilterParameter(calData);
          PreCalSeriesFltrSltnPage.this.params.remove(a2lFilParam);
          a2lFilParam.setParamA2LName(null);
          a2lFilParam.setMinValue(droppedParamVal);
          a2lFilParam.setMaxValue(droppedParamVal);
          a2lFilParam.setParamClass(((SeriesStatisticsInfo) sel).getClassName());
          PreCalSeriesFltrSltnPage.this.params.add(a2lFilParam);
        }
        else {
          PreCalSeriesFltrSltnPage.this.hasComplexValue = true;
        }
      }
      // ICDM-1011
      else if (sel instanceof A2LBaseComponents) {
        PreCalSeriesFltrSltnPage.this.tabFolder.setSelection(TAB_INDEX_0);
        A2LBaseComponents baseCmp = (A2LBaseComponents) sel;
        final A2LFilterBaseComponents filterBC = new A2LFilterBaseComponents(baseCmp);
        if (PreCalSeriesFltrSltnPage.this.bcs.contains(filterBC)) {
          PreCalSeriesFltrSltnPage.this.bcs.remove(filterBC);
        }
        if (CommonUtils.isNotNull(baseCmp.getBcVersion())) {
          filterBC.setBcVersion(baseCmp.getBcVersion());
        }
        PreCalSeriesFltrSltnPage.this.bcs.add(filterBC);
      }
    }
  }

  /**
   * creates the columns of BC table viewer.
   */
  // ICDM-1011
  private void createBCTabColumns() {
    // ICDM-886
    ColumnViewerToolTipSupport.enableFor(this.bcTabViewer, ToolTip.NO_RECREATE);

    this.a2lBC = getA2lFileInfoBO().getA2lBCInfo();

    createBCColumn();

    createVerColumn();

    this.bcTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) PreCalSeriesFltrSltnPage.this.bcTabViewer.getSelection();
        if (selection != null) {
          PreCalSeriesFltrSltnPage.this.deleteRowAction.setEnabled(true);
        }
      }
    });
  }

  /**
   * creates unit column.
   */
  private void createBCColumn() {
    final GridViewerColumn bcColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.bcTabViewer, "BC Name", BC_NAME_COL_SIZE);
    bcColumn.setLabelProvider(new ColumnLabelProvider() {

      // ICDM-886
      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        String name = "";
        if (element instanceof A2LFilterBaseComponents) {
          name = ((A2LFilterBaseComponents) element).getBcName();
        }
        return name;

      }


      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        A2LFilterBaseComponents selBC = (A2LFilterBaseComponents) element;
        StringBuilder toolTip = bcToolTip(selBC);
        if (bcFrmDiffA2l(selBC)) {
          toolTip.append("\nThis BC is not a part of the A2L that has been used to start the export");
        }
        return toolTip.toString();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getBackground(final Object element) {
        A2LFilterBaseComponents selBC = (A2LFilterBaseComponents) element;
        if (bcFrmDiffA2l(selBC)) {
          return PreCalSeriesFltrSltnPage.this.orange;
        }
        return null;

      }

    });
    bcColumn.setEditingSupport(new GridTableEditingSupport(this.bcTabViewer, CommonUIConstants.COLUMN_INDEX_1));
    bcColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(bcColumn.getColumn(), 0, this.bcTableSorter, this.bcTabViewer));
  }

  /**
   * Checks whether the dragged bc is from same a2l file used to start the export or not.
   *
   * @param selBC bc selected
   * @return true if BC is from diff a2l
   */
  private boolean bcFrmDiffA2l(final A2LFilterBaseComponents selBC) {
    boolean flag = false;
    if (!PreCalSeriesFltrSltnPage.this.a2lBC.contains(selBC.getA2lBC())) {
      flag = true;
    }
    Iterator<A2LBaseComponents> bcIterator = PreCalSeriesFltrSltnPage.this.a2lBC.iterator();
    while (bcIterator.hasNext()) {
      A2LBaseComponents baseComp = bcIterator.next();
      if (baseComp.getBcName().equalsIgnoreCase(selBC.getBcName()) &&
          (!CommonUtils.isEmptyString(baseComp.getBcVersion()) &&
              !baseComp.getBcVersion().equalsIgnoreCase(selBC.getBcVersion()))) {
        flag = true;
      }
    }

    return flag;

  }

  /**
   * creates unit column.
   */
  private void createVerColumn() {
    final GridViewerColumn verColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.bcTabViewer, "Version", BC_VER_COL_SIZE);
    verColumn.setLabelProvider(new ColumnLabelProvider() {

      // ICDM-886
      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        String version = "";
        if (element instanceof A2LFilterBaseComponents) {
          version = ((A2LFilterBaseComponents) element).getBcVersion();
        }
        return version;

      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Image getImage(final Object element) {
        return ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_12X12);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        A2LFilterBaseComponents selBC = (A2LFilterBaseComponents) element;
        StringBuilder toolTip = bcToolTip(selBC);
        if (bcFrmDiffA2l(selBC)) {
          toolTip.append("\nThis BC is not a part of the A2L that has been used to start the export");
        }
        return toolTip.toString();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getBackground(final Object element) {
        A2LFilterBaseComponents selBC = (A2LFilterBaseComponents) element;
        if (bcFrmDiffA2l(selBC)) {
          return PreCalSeriesFltrSltnPage.this.orange;
        }
        return null;
      }

    });
    verColumn.setEditingSupport(new GridTableEditingSupport(this.bcTabViewer, CommonUIConstants.COLUMN_INDEX_2));
    verColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(verColumn.getColumn(), 1, this.bcTableSorter, this.bcTabViewer));
  }

  /**
   * ICDM-879 creates the columns of access rights table viewer.
   */
  private void createTabColumns() {
    // ICDM-886
    ColumnViewerToolTipSupport.enableFor(this.paramTabViewer, ToolTip.NO_RECREATE);

    createParamColumn();

    createUnitColumn();

    createClassColumn();

    createMinValColumn();

    createMaxValColumn();


    this.paramTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) PreCalSeriesFltrSltnPage.this.paramTabViewer.getSelection();
        if (selection != null) {
          PreCalSeriesFltrSltnPage.this.deleteRowAction.setEnabled(true);
        }
      }
    });
  }


  /**
   * create parameter name column.
   */
  private void createParamColumn() {
    final GridViewerColumn paramColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.paramTabViewer, "Parameter", PARAM_COL_WIDTH);
    paramColumn.setLabelProvider(new ColumnLabelProvider() {

      // ICDM-886
      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        String name = "";
        if (element instanceof A2LFilterParameter) {
          name = ((A2LFilterParameter) element).getParamName();
        }
        return name;
      }


      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        // ICDM-935
        if (((A2LFilterParameter) element).getA2lParam() == null) {
          return "This parameter is from series statistics";
        }
        String funcName = ((A2LFilterParameter) element).getFuncName();
        String paramLN = ((A2LFilterParameter) element).getLongName();
        StringBuilder toolTip = new StringBuilder(PARAM_TOOLTIP_SIZE);
        toolTip.append("Function :").append(funcName).append("\nParameter Long Name :").append(paramLN);
        if (!getParamList().contains(((A2LFilterParameter) element).getA2lParam())) {
          toolTip.append("\nThis parameter is not part of the export");

        }
        if ((((A2LFilterParameter) element).getParamA2LName() != null) && !((A2LFilterParameter) element)
            .getParamA2LName().equalsIgnoreCase(getA2lFileInfoBO().getA2lFileInfo().getFileName())) {
          toolTip.append("\nA2lfile name:").append(((A2LFilterParameter) element).getParamA2LName());
        }
        return toolTip.toString();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getBackground(final Object element) {
        return setParamColor(element);
      }
    });
    paramColumn.setEditingSupport(new GridTableEditingSupport(this.paramTabViewer, CommonUIConstants.COLUMN_INDEX_0));
    paramColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(paramColumn.getColumn(), 0, this.paramTableSorter, this.paramTabViewer));
  }

  /**
   * Sets the param color.
   *
   * @param element the element
   * @return the color
   */
  /*
   * Defines the background color of the parameter in the parm table
   */
  private Color setParamColor(final Object element) {
    A2LParameter a2lParam = ((A2LFilterParameter) element).getA2lParam();
    boolean flag = false;
    if (a2lParam == null) {
      for (A2LParameter param : getParamList()) {
        if (param.getName().equalsIgnoreCase(((A2LFilterParameter) element).getParamName())) {
          flag = true;
          break;
        }
      }
    }
    else if (getParamList().contains(a2lParam)) {
      flag = true;
    }
    if (flag) {
      return null;
    }
    return PreCalSeriesFltrSltnPage.this.orange;
  }

  /**
   * creates unit column.
   */
  private void createUnitColumn() {
    final GridViewerColumn unitColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.paramTabViewer, "Unit", UNIT_COL_WIDTH);
    unitColumn.setLabelProvider(new ColumnLabelProvider() {

      // ICDM-886
      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        String unit = "";
        if (element instanceof A2LFilterParameter) {
          unit = ((A2LFilterParameter) element).getParamUnit();
        }
        return unit;

      }

      @Override
      /**
       * {@inheritDoc}
       */
      public Color getBackground(final Object element) {
        return setParamColor(element);
      }
    });
    unitColumn.setEditingSupport(new GridTableEditingSupport(this.paramTabViewer, CommonUIConstants.COLUMN_INDEX_1));
    unitColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(unitColumn.getColumn(), 1, this.paramTableSorter, this.paramTabViewer));
  }

  /**
   * creates parameter class column.
   */
  private void createClassColumn() {
    final GridViewerColumn paramClassColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.paramTabViewer, "Class", CLASS_COL_WIDTH);
    paramClassColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      /**
       * {@inheritDoc}
       */
      public String getText(final Object element) {
        // ICDM-886
        if (element instanceof A2LFilterParameter) {
          return ((A2LFilterParameter) element).getParamClass();
        }
        return "";

      }

      @Override
      /**
       * {@inheritDoc}
       */
      public Color getBackground(final Object element) {
        return setParamColor(element);
      }
    });
    paramClassColumn
        .setEditingSupport(new GridTableEditingSupport(this.paramTabViewer, CommonUIConstants.COLUMN_INDEX_2));
    paramClassColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        paramClassColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_2, this.paramTableSorter, this.paramTabViewer));
  }

  /**
   * creates minimum value column.
   */
  private void createMinValColumn() {
    final GridViewerColumn minValColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.paramTabViewer, "Min Value", MIN_COL_WIDTH);
    minValColumn.setLabelProvider(new ColumnLabelProvider() {

      // ICDM-886
      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof A2LFilterParameter) {
          return ((A2LFilterParameter) element).getMinValue();
        }
        return "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Image getImage(final Object element) {
        A2LFilterParameter a2lParam = (A2LFilterParameter) element;
        if ((a2lParam.getCalData() != null) && (a2lParam.getCalData().getCalDataPhy().isText())) {
          return null;
        }
        return ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_12X12);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getBackground(final Object element) {
        return setParamColor(element);
      }
    });

    minValColumn.setEditingSupport(new GridTableEditingSupport(this.paramTabViewer, CommonUIConstants.COLUMN_INDEX_3));
    minValColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        minValColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_3, this.paramTableSorter, this.paramTabViewer));
  }


  /**
   * creates maximum value column.
   */
  private void createMaxValColumn() {
    final GridViewerColumn maxValColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.paramTabViewer, "Max Value", COLWIDTH_MAX_VAL);
    maxValColumn.setLabelProvider(new ColumnLabelProvider() {

      // ICDM-886
      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {

        if (element instanceof A2LFilterParameter) {
          return ((A2LFilterParameter) element).getMaxValue();
        }
        return "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Image getImage(final Object element) {
        A2LFilterParameter a2lParam = (A2LFilterParameter) element;
        if ((a2lParam.getCalData() != null) && (a2lParam.getCalData().getCalDataPhy().isText())) {
          return null;
        }
        return ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_12X12);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getBackground(final Object element) {
        return setParamColor(element);
      }
    });
    maxValColumn.setEditingSupport(new GridTableEditingSupport(this.paramTabViewer, CommonUIConstants.COLUMN_INDEX_4));
    maxValColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        maxValColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_4, this.paramTableSorter, this.paramTabViewer));
  }

  /**
   * fetch all the Recommended Values.
   *
   * @param shell shell
   */
  public void nextPressed(final Shell shell) {
    this.retrieveJob = new PreCalDataRetrieveJob("Retrieving Data", System.currentTimeMillis(), this) {

      /**
       * {@inheritDoc}
       */
      @Override
      protected IStatus run(final IProgressMonitor monitor) {
        // ICdm-976 pass the shell Object
        ISeriesStatisticsFilter[] filter =
            fetchFilterParam(PreCalSeriesFltrSltnPage.this.params, PreCalSeriesFltrSltnPage.this.sysConts,
                PreCalSeriesFltrSltnPage.this.bcs, PreCalSeriesFltrSltnPage.this.fcs);
        return fetchRecommendedVal(getParamList(), filter, monitor, shell);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      protected void canceling() {
        super.canceling();
        getContainer().updateButtons();
      }

    };
    com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
    this.retrieveJob.schedule();
  }

  /**
   * ICDM-900.
   *
   * @param params2 selected params for filter
   * @param sysConts2 selected sysConsts for filter
   * @param bcSet selected base components for the filter
   * @param fcSet selected functions
   * @return array of min,max and sysconsts
   */
  protected ISeriesStatisticsFilter[] fetchFilterParam(final SortedSet<A2LFilterParameter> params2,
      final SortedSet<A2LSystemConstantValues> sysConts2, final SortedSet<A2LFilterBaseComponents> bcSet,
      final SortedSet<A2LFilterFunction> fcSet) {
    int size = (params2.size()) + (sysConts2.size()) + (params2.size()) + bcSet.size() + fcSet.size();
    ISeriesStatisticsFilter[] filter = new ISeriesStatisticsFilter[size];
    int filterIndex = 0;
    for (A2LFilterParameter a2lParam : params2) {
      // min value
      filterIndex = addMinVal(filter, filterIndex, a2lParam);
      // max value
      filterIndex = addMaxVal(filter, filterIndex, a2lParam);

    }
    filterIndex = addSysConst(sysConts2, filter, filterIndex);
    // ICDM-1011
    filterIndex = addBC(bcSet, filter, filterIndex);
    addFunction(fcSet, filter, filterIndex);

    return filter;

  }


  /**
   * Adds the BC.
   *
   * @param bcs1 base components selected for filter
   * @param filter array to store val
   * @param filterIndex array index
   * @return the int
   */
  private int addBC(final SortedSet<A2LFilterBaseComponents> bcs1, final ISeriesStatisticsFilter[] filter,
      final int filterIndex) {
    int fltrIndx = filterIndex;
    for (A2LFilterBaseComponents bc : bcs1) {
      filter[fltrIndx] =
          new DefaultSeriesStatisticsFilter(bc.getBcName(), ISeriesStatisticsFilter.DataType.BASE_COMPONENTS_FILTER,
              ISeriesStatisticsFilter.ValueType.EQUALS_VALUE, new AtomicValuePhy(bc.getBcVersion()));
      fltrIndx++;
    }
    return fltrIndx;
  }

  /**
   * ICDM-900.
   *
   * @param sysConts2 sys conts selected for filter
   * @param filter array to store val
   * @param filterIndex array index
   * @return the int
   */
  private int addSysConst(final SortedSet<A2LSystemConstantValues> sysConts2, final ISeriesStatisticsFilter[] filter,
      final int filterIndex) {
    int fltrIndx = filterIndex;
    for (A2LSystemConstantValues sysConst : sysConts2) {
      filter[fltrIndx] = new DefaultSeriesStatisticsFilter(sysConst.getSysconName(),
          ISeriesStatisticsFilter.DataType.SYSTEM_CONSTANTS_FILTER, ISeriesStatisticsFilter.ValueType.EQUALS_VALUE,
          new AtomicValuePhy(sysConst.getValue()));
      fltrIndx++;
    }
    return fltrIndx;
  }

  /**
   * ICDM-900.
   *
   * @param fcSet sys conts selected for filter
   * @param filter array to store val
   * @param filterIndex array index
   * @return the int
   */
  private int addFunction(final SortedSet<A2LFilterFunction> fcSet, final ISeriesStatisticsFilter[] filter,
      final int filterIndex) {
    int fltrIndx = filterIndex;
    for (A2LFilterFunction fc : fcSet) {
      filter[fltrIndx] = new DefaultSeriesStatisticsFilter(fc.getA2lFunction().getName(),
          ISeriesStatisticsFilter.DataType.FUNCTION_FILTER, ISeriesStatisticsFilter.ValueType.EQUALS_VALUE,
          new AtomicValuePhy(fc.getFunctionVersion()));
      fltrIndx++;
    }
    return fltrIndx;
  }

  /**
   * Adds the max val.
   *
   * @param filter the filter
   * @param filterIndex the filter index
   * @param a2lParam the a 2 l param
   * @return the int
   */
  private int addMaxVal(final ISeriesStatisticsFilter[] filter, final int filterIndex,
      final A2LFilterParameter a2lParam) {
    int fltrIndx = filterIndex;
    String maxVal;
    CalData maxCalData;
    AtomicValuePhy val;
    String paramName = a2lParam.getParamName();
    String maxValue = a2lParam.getMaxValue();
    if ("-".equals(maxValue)) {
      val = new AtomicValuePhy("");
    }
    // ICDM-934
    else if (CommonUtils.isValidDouble(maxValue)) {
      String paramUnit = a2lParam.getParamUnit();
      maxVal = CalDataUtil.createDCMStringForNumber(paramName, paramUnit, maxValue);
      maxCalData = CalDataUtil.dcmToCalData(maxVal, paramName, CDMLogger.getInstance());
      CalDataPhyValue calMaxVal = (CalDataPhyValue) (maxCalData.getCalDataPhy());
      val = calMaxVal.getAtomicValuePhy();
    }
    else {
      val = new AtomicValuePhy(maxValue);
    }
    filter[fltrIndx] = new DefaultSeriesStatisticsFilter(paramName, ISeriesStatisticsFilter.DataType.PARAMETER_FILTER,
        ISeriesStatisticsFilter.ValueType.MAX_VALUE, val);
    fltrIndx++;
    return fltrIndx;
  }

  /**
   * Adds the min val.
   *
   * @param filter the filter
   * @param filterIndex the filter index
   * @param a2lParam the a 2 l param
   * @return the int
   */
  private int addMinVal(final ISeriesStatisticsFilter[] filter, final int filterIndex,
      final A2LFilterParameter a2lParam) {
    int fltrIndx = filterIndex;
    String minVal;
    CalData minCalData;
    AtomicValuePhy val;
    String paramName = a2lParam.getParamName();
    String minValue = a2lParam.getMinValue();
    if ("-".equals(minValue)) {
      val = new AtomicValuePhy("");
    }
    // ICDM-934
    else if (CommonUtils.isValidDouble(minValue)) {
      String paramUnit = a2lParam.getParamUnit();
      minVal = CalDataUtil.createDCMStringForNumber(paramName, paramUnit, minValue);
      minCalData = CalDataUtil.dcmToCalData(minVal, paramName, CDMLogger.getInstance());
      CalDataPhyValue calminVal = (CalDataPhyValue) (minCalData.getCalDataPhy());
      val = calminVal.getAtomicValuePhy();
    }
    else {
      val = new AtomicValuePhy(minValue);
    }
    filter[fltrIndx] = new DefaultSeriesStatisticsFilter(paramName, ISeriesStatisticsFilter.DataType.PARAMETER_FILTER,
        ISeriesStatisticsFilter.ValueType.MIN_VALUE, val);
    fltrIndx++;
    return fltrIndx;
  }

  /**
   * Check min max val.
   *
   * @return true if min value is valid, i.e, a number and less than max value
   */
  public boolean checkMinMaxVal() {
    boolean valChkFlag = true;
    GridItem[] gridItems = this.paramTabViewer.getGrid().getItems();
    for (GridItem gridItem : gridItems) {
      A2LFilterParameter a2lParam = (A2LFilterParameter) gridItem.getData();
      String maxValue = a2lParam.getMaxValue();
      String minValue = a2lParam.getMinValue();
      // ICDM-934
      if (!"-".equalsIgnoreCase(maxValue) && !"-".equalsIgnoreCase(minValue) && CommonUtils.isValidDouble(maxValue) &&
          CommonUtils.isValidDouble(minValue) && (Double.parseDouble(maxValue) < Double.parseDouble(minValue))) {
        valChkFlag = false;
        break;
      }
    }
    return valChkFlag;
  }

  /**
   * Fetch recommended val.
   *
   * @param parameterList param List Method to clear the map and fetch the data from the Parametres
   * @param filter array contains min,max and sys constant values from filter criteria
   * @param monitor the monitor
   * @param shell the shell
   * @return the i status
   */
  private IStatus fetchRecommendedVal(final List<A2LParameter> parameterList, final ISeriesStatisticsFilter[] filter,
      final IProgressMonitor monitor, final Shell shell) {
    // Call the webservice to fetch data for the Series stats
    getDataHandler().getCalDataMap().clear();
    IStatus status = fetchDataForSeriesStat(parameterList, filter, monitor);
    // The below check can be optimised further like status can be set
    // for webservice fail/insufficient data within job
    if (Status.CANCEL_STATUS.equals(status)) {
      return status;
    }
    final PreCalDataExportWizard wizard = getWizard();
    final PreCalRecommendedValuesPage recomValPage = (PreCalRecommendedValuesPage) wizard.getNextPage(this);
    // ICdm-976 pass the shell Object
    recomValPage.setInput(true, shell);
    recomValPage.setAddedJob(true);
    getDataHandler().setFlagChanged(false);
    // ICdm-976 move the code for Showing the warning dialog to the end
    if (getDataHandler().getCalDataMap().isEmpty()) {
      CDMLogger.getInstance().warnDialog(
          "Export cannot be done since series statistics data not found for any of the parameters selected!", null,
          Activator.PLUGIN_ID);
    }
    if (getDataHandler().isWsExpOccured()) {
      CDMLogger.getInstance().errorDialog(getDataHandler().getWsException().getMessage(),
          getDataHandler().getWsException(), Activator.PLUGIN_ID);
    }
    return status;
  }

  /**
   * For series Stat get the data from the webservice.
   *
   * @param parameterList the parameter list
   * @param filter array contains min,max and sys constant values from filter criteria
   * @param wizard call the webservice and get the data.
   * @param monitor the monitor
   * @return the i status
   */
  private IStatus fetchDataForSeriesStat(final List<A2LParameter> parameterList, final ISeriesStatisticsFilter[] filter,
      final IProgressMonitor monitor) {
    try {

      // For now Hard coded as ICDM_02_SERVER Icdm-758
      final APICWebServiceClient apicWsClient = new APICWebServiceClient();
      String[] paramNames = new String[parameterList.size()];
      for (int i = 0; i < parameterList.size(); i++) {
        paramNames[i] = parameterList.get(i).getName();
      }
      ParameterStatisticCallbackHandler callbackHandler = null;
      // ICDM-900
      if (filter.length == 0) {
        callbackHandler = (ParameterStatisticCallbackHandler) apicWsClient.getParameterStatisticsExtAsync(paramNames);
      }
      else {
        callbackHandler =
            (ParameterStatisticCallbackHandler) apicWsClient.getParameterStatisticsExtAsync(paramNames, filter);
      }

      int prevTimeWrkd = 0;
      int timeWrkd = 0;
      int diff = 0;
      StringBuilder taskName = new StringBuilder("Fetching Caldata information...");
      while (!callbackHandler.isParameterAvailable() && !callbackHandler.isBroken()) {
        // Icdm-990 Logging the Information once in a second Changes made using a String Builder
        if (monitor.isCanceled()) {
          return setJobCancelMessage(apicWsClient, callbackHandler);
        }
        timeWrkd = (int) apicWsClient.getStatusForAsyncExecutionResponse(callbackHandler.getSessionID());
        taskName.append(timeWrkd).append("% completed");
        String taskStr = taskName.toString();
        monitor.setTaskName(taskStr);
        diff = timeWrkd - prevTimeWrkd;
        monitor.worked(diff);
        CDMLogger.getInstance().debug(taskStr);
        prevTimeWrkd = timeWrkd;
        // Icdm-990 Logging the Information once in a second
        Thread.sleep(THREAD_SLEEP);
        taskName.delete(ApicConstants.TASK_STR_START_IDX, taskName.length());
      }
      if (callbackHandler.isBroken()) {
        throw callbackHandler.getParameterException();
      }

      for (CalData calData : callbackHandler.getFiles()) {
        getDataHandler().getCalDataMap().put(calData.getShortName(), calData);
      }
    }
    // Icdm-976 Catch the Exception and set the exception Occured flag.
    catch (Exception ex) {
      getDataHandler().setWsException(ex);
    }
    finally {
      monitor.done();
    }
    return Status.OK_STATUS;

  }

  /**
   * ICdm-1038 set the message for Job cancel.
   *
   * @param apicWsClient the apic ws client
   * @param callbackHandler the callback handler
   * @return the Cancel status as Cancel
   * @throws Exception the exception
   */
  private IStatus setJobCancelMessage(final APICWebServiceClient apicWsClient,
      final ParameterStatisticCallbackHandler callbackHandler)
      throws Exception {

    final StringBuilder wsCanMess = new StringBuilder();
    if (apicWsClient.cancelSession(callbackHandler.getSessionID())) {
      wsCanMess.append(SUCC_JOB_CAN1);
      fillSessionIdInfo(callbackHandler, wsCanMess);
      wsCanMess.append(SUCC_JOB_CAN2);
      CDMLogger.getInstance().info(wsCanMess.toString());
    }
    else {
      wsCanMess.append(FAIL_JOB_CAN1);
      fillSessionIdInfo(callbackHandler, wsCanMess);
      wsCanMess.append(FAIL_JOB_CAN2);
      CDMLogger.getInstance().info(wsCanMess.toString());
    }
    return Status.CANCEL_STATUS;
  }

  /**
   * ICdm-1038 Fill the session id in the message.
   *
   * @param callbackHandler the callback handler
   * @param wsCanMess the ws can mess
   */
  private void fillSessionIdInfo(final ParameterStatisticCallbackHandler callbackHandler,
      final StringBuilder wsCanMess) {
    wsCanMess.append(ApicConstants.EMPTY_SPACE).append(ApicConstants.LEFT_ARROW).append(callbackHandler.getSessionID())
        .append(ApicConstants.RIGHT_ARROW).append(ApicConstants.EMPTY_SPACE);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {
    boolean flag = false;
    if (this.recomValue.getSelection()) {
      flag = true;
    }
    return flag;
  }

  /**
   * This method creates filter text.
   */
  private void createParamFilterTxt() {
    final GridData gridData = getFilterTxtGridData();
    this.filterParamTxt.setLayoutData(gridData);
    this.filterParamTxt.setMessage(CommonUIConstants.TEXT_FILTER);
    this.filterParamTxt.addModifyListener((ModifyListener) (event -> {
      final String text = PreCalSeriesFltrSltnPage.this.filterParamTxt.getText().trim();
      PreCalSeriesFltrSltnPage.this.paramTabFilter.setFilterText(text);
      PreCalSeriesFltrSltnPage.this.paramTabViewer.refresh();
    }));
  }

  /**
   * This method creates filter text.
   */
  private void createBCFilterTxt() {
    final GridData gridData = getFilterTxtGridData();
    this.filterBCTxt.setLayoutData(gridData);
    this.filterBCTxt.setMessage(CommonUIConstants.TEXT_FILTER);
    this.filterBCTxt.addModifyListener((ModifyListener) (event -> {
      final String text = PreCalSeriesFltrSltnPage.this.filterBCTxt.getText().trim();
      PreCalSeriesFltrSltnPage.this.bcTabFilter.setFilterText(text);
      PreCalSeriesFltrSltnPage.this.bcTabViewer.refresh();
    }));
  }

  /**
   * This method creates filter text.
   */
  private void createFCFilterTxt() {
    final GridData gridData = getFilterTxtGridData();
    this.filterFCtTxt.setLayoutData(gridData);
    this.filterFCtTxt.setMessage(CommonUIConstants.TEXT_FILTER);
    this.filterFCtTxt.addModifyListener((ModifyListener) (event -> {
      final String text = PreCalSeriesFltrSltnPage.this.filterFCtTxt.getText().trim();
      PreCalSeriesFltrSltnPage.this.fcFilter.setFilterText(text);
      PreCalSeriesFltrSltnPage.this.fcTabViewer.refresh();
    }));
  }

  /**
   * ICDM-886 {@inheritDoc}
   */
  @Override
  public IWizardPage getNextPage() {
    if (checkMinMaxVal()) {
      return super.getNextPage();
    }
    CDMLogger.getInstance().infoDialog(
        "Please check the min and max values. Max value should be greater than or equal to min value",
        com.bosch.caltool.icdm.common.ui.Activator.PLUGIN_ID);
    return getWizard().getPage("Select the Source1");
  }


  /**
   * This method creates filter text.
   */
  private void createSysCnstFilterTxt() {
    final GridData gridData = getFilterTxtGridData();
    this.filterSysCnstTxt.setLayoutData(gridData);
    this.filterSysCnstTxt.setMessage(CommonUIConstants.TEXT_FILTER);
    this.filterSysCnstTxt.addModifyListener((ModifyListener) (event -> {
      final String text = PreCalSeriesFltrSltnPage.this.filterSysCnstTxt.getText().trim();
      PreCalSeriesFltrSltnPage.this.sysCnstFilter.setFilterText(text);
      PreCalSeriesFltrSltnPage.this.sysCnstTabViewer.refresh();
    }));
  }


  /**
   * This method returns filter text GridData object.
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
   * ICCDM-886 check if param already has value , if so it is set as min and max.
   *
   * @param a2lParam a2lparam
   * @param a2lFilParam a2lfilterparam
   */
  private void isParamValAvialable(final A2LParameter a2lParam, final A2LFilterParameter a2lFilParam) {
    if (a2lParam.getCalData() != null) {
      String a2lParamVal = a2lParam.getCalData().getCalDataPhy().getSimpleDisplayValue();
      if (!CommonUtils.isEmptyString(a2lParamVal)) {
        a2lFilParam.setMinValue(a2lParamVal);
        a2lFilParam.setMaxValue(a2lParamVal);
      }
    }
  }

  /**
   * ICDM-886 deleting system constants.
   */
  private void delSysConsts() {
    IStructuredSelection sel;
    sel = (IStructuredSelection) PreCalSeriesFltrSltnPage.this.sysCnstTabViewer.getSelection();
    SortedSet<A2LSystemConstantValues> delSysConst = new TreeSet<>();
    if (sel != null) {
      Iterator<?> selSysConts = sel.iterator();
      while (selSysConts.hasNext()) {
        A2LSystemConstantValues sysConst = (A2LSystemConstantValues) selSysConts.next();
        delSysConst.add(sysConst);
      }
      // if single value delete directly
      if (!delSysConst.isEmpty() && (delSysConst.size() == 1)) {
        PreCalSeriesFltrSltnPage.this.sysConts.removeAll(delSysConst);
      }
      // if multiple values confirm before deleting
      else if (!delSysConst.isEmpty() && (delSysConst.size() > 1)) {
        MessageDialog dialog = new MessageDialog(getShell(), DELETE_CONFIRMATION, null,
            "Do you really want to delete the selected system constants from the filter criteria?",
            MessageDialog.QUESTION_WITH_CANCEL, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL },
            0);
        dialog.open();
        int btnSel = dialog.getReturnCode();
        if (btnSel == 0) {
          PreCalSeriesFltrSltnPage.this.sysConts.removeAll(delSysConst);
        }
      }
      PreCalSeriesFltrSltnPage.this.sysCnstTabViewer.setInput(PreCalSeriesFltrSltnPage.this.sysConts);
    }
  }

  /**
   * ICDM-1011 deleting base components.
   */
  private void delBcs() {
    IStructuredSelection sel;
    sel = (IStructuredSelection) PreCalSeriesFltrSltnPage.this.bcTabViewer.getSelection();
    SortedSet<A2LFilterBaseComponents> delBC = new TreeSet<>();
    if (sel != null) {
      Iterator<?> selBcs = sel.iterator();
      while (selBcs.hasNext()) {
        A2LFilterBaseComponents baseCmp = (A2LFilterBaseComponents) selBcs.next();
        delBC.add(baseCmp);
      }
      // if single value delete directly
      if (!delBC.isEmpty() && (delBC.size() == 1)) {
        PreCalSeriesFltrSltnPage.this.bcs.removeAll(delBC);
      }
      // if multiple values confirm before deleting
      else if (!delBC.isEmpty() && (delBC.size() > 1)) {
        MessageDialog dialog = new MessageDialog(getShell(), DELETE_CONFIRMATION, null,
            "Do you really want to delete the selected base components from the filter criteria?",
            MessageDialog.QUESTION_WITH_CANCEL, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL },
            0);
        dialog.open();
        int btnSel = dialog.getReturnCode();
        if (btnSel == OK_PRESSED) {
          PreCalSeriesFltrSltnPage.this.bcs.removeAll(delBC);
        }
      }
      PreCalSeriesFltrSltnPage.this.bcTabViewer.setInput(PreCalSeriesFltrSltnPage.this.bcs);
    }
  }

  /**
   * This method sets the dropped bc's in the table.
   */
  private void bcsDropped() {
    if (!PreCalSeriesFltrSltnPage.this.bcs.isEmpty() &&
        (PreCalSeriesFltrSltnPage.this.tabFolder.getSelectionIndex() == 0)) {
      PreCalSeriesFltrSltnPage.this.bcTabViewer.setInput(PreCalSeriesFltrSltnPage.this.bcs);
      GridItem[] items = PreCalSeriesFltrSltnPage.this.bcTabViewer.getGrid().getItems();
      for (GridItem gridItem : items) {
        gridItem.setToolTipText(CommonUIConstants.COLUMN_INDEX_0, CommonUIConstants.EMPTY_STRING);
        gridItem.setToolTipText(CommonUIConstants.COLUMN_INDEX_1, CommonUIConstants.EMPTY_STRING);
      }
    }
  }

  /**
   * Deleting the FC.
   */
  private void delFCs() {
    IStructuredSelection sel;
    sel = (IStructuredSelection) PreCalSeriesFltrSltnPage.this.fcTabViewer.getSelection();
    SortedSet<A2LFilterFunction> delFunc = new TreeSet<>();
    if (sel != null) {
      Iterator<?> selFunc = sel.iterator();
      while (selFunc.hasNext()) {
        A2LFilterFunction func = (A2LFilterFunction) selFunc.next();
        delFunc.add(func);
      }
      // if single value delete directly
      if (!delFunc.isEmpty() && (delFunc.size() == 1)) {
        PreCalSeriesFltrSltnPage.this.fcs.removeAll(delFunc);
      }
      // if multiple values confirm before deleting
      else if (!delFunc.isEmpty() && (delFunc.size() > 1)) {
        MessageDialog dialog = new MessageDialog(getShell(), DELETE_CONFIRMATION, null,
            "Do you really want to delete the selected function from the filter criteria?",
            MessageDialog.QUESTION_WITH_CANCEL, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL },
            0);
        dialog.open();
        int btnSel = dialog.getReturnCode();
        if (btnSel == 0) {
          PreCalSeriesFltrSltnPage.this.fcs.removeAll(delFunc);
        }
      }
      PreCalSeriesFltrSltnPage.this.fcTabViewer.setInput(PreCalSeriesFltrSltnPage.this.fcs);
    }
  }


  /**
   * This method sets the dropped fcs in the table.
   */
  private void fcsDropped() {
    if (!PreCalSeriesFltrSltnPage.this.fcs.isEmpty() &&
        (PreCalSeriesFltrSltnPage.this.tabFolder.getSelectionIndex() == TAB_INDEX_1)) {
      PreCalSeriesFltrSltnPage.this.fcTabViewer.setInput(PreCalSeriesFltrSltnPage.this.fcs);
      GridItem[] items = PreCalSeriesFltrSltnPage.this.fcTabViewer.getGrid().getItems();
      for (GridItem gridItem : items) {
        gridItem.setToolTipText(CommonUIConstants.COLUMN_INDEX_0, CommonUIConstants.EMPTY_STRING);
        gridItem.setToolTipText(CommonUIConstants.COLUMN_INDEX_1, CommonUIConstants.EMPTY_STRING);
      }
    }
  }

  /**
   * This method sets the dropped parameters in the table.
   */
  private void paramDropped() {
    if (!PreCalSeriesFltrSltnPage.this.params.isEmpty() &&
        (PreCalSeriesFltrSltnPage.this.tabFolder.getSelectionIndex() == TAB_INDEX_2)) {
      PreCalSeriesFltrSltnPage.this.paramTabViewer.setInput(PreCalSeriesFltrSltnPage.this.params);
      GridItem[] items = PreCalSeriesFltrSltnPage.this.paramTabViewer.getGrid().getItems();
      for (GridItem gridItem : items) {
        gridItem.setToolTipText(CommonUIConstants.COLUMN_INDEX_0, CommonUIConstants.EMPTY_STRING);
      }
    }
  }

  /**
   * This method sets the dropped sysConsts in the table.
   */
  private void sysConstDropped() {
    if (!PreCalSeriesFltrSltnPage.this.sysConts.isEmpty() &&
        (PreCalSeriesFltrSltnPage.this.tabFolder.getSelectionIndex() == TAB_INDEX_3)) {
      PreCalSeriesFltrSltnPage.this.sysCnstTabViewer.setInput(PreCalSeriesFltrSltnPage.this.sysConts);
      GridItem[] items = PreCalSeriesFltrSltnPage.this.sysCnstTabViewer.getGrid().getItems();
      for (GridItem gridItem : items) {
        gridItem.setToolTipText(CommonUIConstants.COLUMN_INDEX_0, CommonUIConstants.EMPTY_STRING);
      }

    }
  }

  /**
   * This method checks for Parameters of Complex type or value type.
   */
  private void complexValDropped() {
    if (PreCalSeriesFltrSltnPage.this.hasComplexValue) {
      CDMLogger.getInstance().infoDialog(
          "Only VALUE type parameters are supported for Filter Criteria! \nOther type parameters are skipped.",
          com.bosch.caltool.icdm.common.ui.Activator.PLUGIN_ID);
    }
  }


  /**
   * Gets the params.
   *
   * @return the params filtered params
   */
  public SortedSet<A2LFilterParameter> getParams() {
    return this.params;
  }

  /**
   * Gets the sys conts.
   *
   * @return the sysConts filtered sysConsts
   */
  public SortedSet<A2LSystemConstantValues> getSysConts() {
    return this.sysConts;
  }


  /**
   * Gets the retrieve job.
   *
   * @return the retrieveJob
   */
  public PreCalDataRetrieveJob getRetrieveJob() {
    return this.retrieveJob;
  }

  /**
   * Bc tool tip.
   *
   * @param selBC the sel BC
   * @return the string builder
   */
  private StringBuilder bcToolTip(final A2LFilterBaseComponents selBC) {
    String longName = selBC.getBcLongNmae();
    if (CommonUtils.isEmptyString(longName)) {
      longName = "Not available";
    }
    String state = selBC.getBcState();
    if (CommonUtils.isEmptyString(state)) {
      state = "Not available";
    }
    StringBuilder toolTip = new StringBuilder(TOOLTIP_INIT_SIZE);
    toolTip.append("Long Name of BC :").append(longName.trim()).append("\nState :").append(state.trim());
    return toolTip;
  }


  /**
   * Gets the fcs.
   *
   * @return the fcs
   */
  public SortedSet<A2LFilterFunction> getFcs() {
    return this.fcs;
  }

  @Override
  public PreCalDataExportWizard getWizard() {
    return (PreCalDataExportWizard) super.getWizard();
  }

  private PreCalDataWizardDataHandler getDataHandler() {
    return getWizard().getDataHandler();
  }

}
