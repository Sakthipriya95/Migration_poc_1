/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calcomp.caldataanalyzer.vo.LabelInfoVO;
import com.bosch.calcomp.caldataanalyzer.vo.LabelValueInfoVO;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldatacomparison.CalDataAttributes;
import com.bosch.calmodel.caldatacomparison.CalDataComparison;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.calmodel.caldataphy.CalDataPhy.SortColumns;
import com.bosch.calmodel.caldataphy.CalDataPhyValue;
import com.bosch.calmodel.caldataphyutils.CalDataTableGraphComposite;
import com.bosch.calmodel.caldataphyutils.exception.CalDataTableGraphException;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.ss.CalDataType;
import com.bosch.caltool.icdm.client.bo.ss.SeriesStatisticsInfo;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.providers.ScratchPadDataFetcher;
import com.bosch.caltool.icdm.common.ui.sorters.ScratchPadSorter;
import com.bosch.caltool.icdm.common.ui.sorters.SeriesStatisticsViewerSorter;
import com.bosch.caltool.icdm.common.ui.utils.CalDataUtil;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.data.SeriesStatisticsTableData;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.tablegraph.ui.histogram.HistogramSample;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.wbutils.WorkbenchUtils;

/**
 * * Icdm-521 Moved the class to common UI This class is responsible to show statistical information for an A2L
 * parameter
 */
// ICDM-206
public class SeriesStatisticsViewPart extends AbstractViewPart {

  /**
   * Count for graph components
   */
  private static final int GRAPH_COMP_COUNT = 10;
  /**
   * COLUMN WIDTH OF GRAPH COLUMN
   */
  private static final int GRAPH_COL_WIDTH = 35;
  /**
   * COLUMN WIDTH OF SERIAL INDEX COLUMN
   */
  private static final int SERIAL_INDEX_COL_WIDTH = 60;
  /**
   * Defines CalData info is not available name
   */
  private static final String CAL_DATA_INFO_IS_NOT_AVAILABLE = "CalData info is not available ";
  /**
   * Defines text field widthhint
   */
  private static final int TEXT_FIELD_WIDTHHINT = 90;
  /**
   * Defines datasets gridviewer column width
   */
  private static final int DTSET_GRDVWRCOL_WIDTH = 80;
  /**
   * Defines unit gridviewer column width
   */
  private static final int UNIT_GRIDVIEWERCOLUMN_WIDTH = 40;
  /**
   * Defines value gridviewer column width
   */
  private static final int VALUE_GRIDVIEWERCOLUMN_WIDTH = 80;
  /**
   * Defines caldata analyzer composite to split into number of columns
   */
  private static final int CLDTA_ANLYZR_CMPSTE_SPLT_COLS = 5;
  /**
   * Defines statistic values grid tableviewer vertical span layout data
   */
  // ICDM-218
  private static final int TBLVWR_GRDDTA_VRTCL_SPAN = 6;
  /**
   * Defines statistic values grid tableviewer horizontal span layout data
   */
  private static final int TBLVWR_GRDDTA_HOR_SPAN = 2;
  /**
   * FormToolkit instance
   */
  private FormToolkit toolkit;
  /**
   * ScrolledForm instance
   */
  // ICDM-218
  private ScrolledForm scrolledForm;
  /**
   * GridTableViewer instance for statisctic values
   */
  private GridTableViewer statValsGridTableViewer;
  /**
   * Text instance for different values
   */
  private StyledText txtDiffValues;
  /**
   * Text instance for minimum value
   */
  private StyledText txtMinValue;
  /**
   * Text instance for used datasets value
   */
  private StyledText txtUsedDatasets;
  /**
   * Text instance for maximum value
   */
  private StyledText txtMaxValue;
  /**
   * Text instance for peak value
   */
  private StyledText txtPeakValue;
  /**
   * Text instance for percentage value
   */
  private StyledText txtPercentageVal;
  /**
   * Text instance for average value
   */
  private StyledText txtAvgValue;
  /**
   * Text instance for median value
   */
  private StyledText txtMedian;
  /**
   * Text instance for lower quatile value
   */
  private StyledText txtLowerQuartile;
  /**
   * Text instance for upper quatile value
   */
  private StyledText txtUpperQuartile;
  /**
   * Defines AbstractViewerSorter for tableviewer columns sorting
   */
  private AbstractViewerSorter statValsTableSorter;
  /**
   * list of caldata objects from dataset table
   */
  private final Map<CalData, Integer> dataSetCalDt = new HashMap<CalData, Integer>();

  /**
   * list of cal data phy value objects to avoid duplication in the table/graph view
   */
  // iCDM-2232
  private final List<CalDataPhyValue> calDataPhyValueList = new ArrayList<>();
  /**
   * LabelInfoVO instance
   */
  private LabelInfoVO labelInfoVO;
  /**
   * Graph compare count
   */
  private static int graphCmpCount;

  /**
   * ICDM-935 param class name
   */
  private String paramClassName;

  private static final String DATASET_STR = "DataSet";

  /**
   * Defines SeriesStatisticsViewPart id
   */
  public static final String SERIES_VIEW_ID = SeriesStatisticsViewPart.class.getName();
  /**
   * CommonActionSet instance
   */
  // ICDM-226
  private final CommonActionSet commonActionSet = new CommonActionSet();

  // iCDM-456
  private static final String GRAPH_TOOL_TIP = "Display graph and table";
  /**
   * graph button image
   */
  private Image graphBtnImage;
  /**
   * calData map for graph compare
   */
  private final Map<String, Map<CalData, Integer>> calDataMap = new ConcurrentHashMap<String, Map<CalData, Integer>>();

  /** Table Graph Composite instance */

  private CalDataTableGraphComposite calDataTableGraphComposite;

  /** Map which holds the listener(SWT.Activate) instance for the Text */
  private final Map<Scrollable, Map<Integer, Listener>> textListenerMap =
      new ConcurrentHashMap<Scrollable, Map<Integer, Listener>>();
  private Button maxBtn;
  private Button peakValBtn;
  private Button lowerQBtn;
  private Button medianBtn;
  private Button upperQBtn;
  private Button minBtn;
  private Button avgBtn;
  /*
   * Action to handle previous search results
   */
  private Action prevResultsAction;
  /*
   * Holds the view parts parent composite
   */
  private Composite parentComposite;

  /**
   * Constant for String buffer Clearing length
   */
  private static final int STRBUF_CLR_LEN = 0;

  private final Map<String, Integer> graphColMap = new ConcurrentHashMap<String, Integer>();

  private final Map<String, Integer> histogramColorMap = new ConcurrentHashMap<String, Integer>();

  private boolean isValueType;

  @Override
  public void createPartControl(final Composite parent) {
    addHelpAction();
    this.parentComposite = parent;
    // ICDM-206
    // Create toolkit
    this.toolkit = new FormToolkit(parent.getDisplay());

    this.graphBtnImage = new Image(null,
        SeriesStatisticsViewPart.class.getClassLoader().getResourceAsStream("/icons/graph_compare.gif"));
    // Create scrolled form
    createScrolledForm(parent);

    // Create caldata analyzer ui section
    final Section calDataSection = createCaldataSection();

    // Create caldata analyzer ui composite
    final Composite caldataComp = this.toolkit.createComposite(calDataSection);

    calDataSection.setClient(caldataComp);

    // Set layout to caldataComp
    final GridLayout layout = new GridLayout();
    layout.numColumns = CLDTA_ANLYZR_CMPSTE_SPLT_COLS;
    caldataComp.setLayout(layout);

    // Create ui controls on caldata analyzer section
    createCalDataAnalyzerUIControls(caldataComp);

    // Create graph/table section
    final Section graphSection = createGraphSection();

    // Create graph/table composite
    Composite graphComp = this.toolkit.createComposite(graphSection);
    graphSection.setClient(graphComp);

    // Set layout to graph/table composite
    graphComp.setLayout(new GridLayout());

    // Create table graph composite structure
    this.calDataTableGraphComposite = new CalDataTableGraphComposite(graphComp, this.scrolledForm.getHorizontalBar(),
        this.scrolledForm.getVerticalBar(), CDMLogger.getInstance(),
        new HashMap<String, HashMap<Integer, List<HistogramSample>>>());


    IActionBars bars = getViewSite().getActionBars();
    IToolBarManager manager = bars.getToolBarManager();
    this.prevResultsAction = new Action("", SWT.MENU) {/* no implementation */};
    this.prevResultsAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SERIES_STAT_RESULT_16X16));
    this.prevResultsAction.setToolTipText("Show search history..");
    this.prevResultsAction.setMenuCreator(new SeriesStatDropDownAction());
    manager.add(this.prevResultsAction);
  }

  /**
   * This method creates UI controls for caldqata analyzer information
   *
   * @param caldataComp
   */
  // ICDM-206
  private void createCalDataAnalyzerUIControls(final Composite caldataComp) {
    // Set contents
    createDiffParamUIControls(caldataComp);

    createMinValUIControls(caldataComp);

    createUsedDatasetUIControls(caldataComp);

    createMaxValUIControls(caldataComp);

    // Icdm-433
    creaetLblControl(caldataComp, CommonUIConstants.STATISTICAL_VALUES,
        "All different values of the parameter in series datasets");

    createEmptySpaceControl(caldataComp);

    createPeakValUIControls(caldataComp);

    createStatValsGridTabViewer(caldataComp);

    createPercentageUIControls(caldataComp);

    createEmptySpaceControl(caldataComp);


    createAvgUIControls(caldataComp);

    createMedianUIControls(caldataComp);


    createLowerQuartileUIControls(caldataComp);

    createUpperQuartileUIControls(caldataComp);

  }

  /**
   * This method creates Statistics Values GridTableViewer
   *
   * @param caldataComp
   */
  private void createStatValsGridTabViewer(final Composite caldataComp) {
    this.statValsTableSorter = new SeriesStatisticsViewerSorter();
    // Create statistics values GridTableViewer
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalSpan = TBLVWR_GRDDTA_HOR_SPAN;
    gridData.verticalSpan = TBLVWR_GRDDTA_VRTCL_SPAN;

    this.statValsGridTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(caldataComp,
        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, gridData);

    this.statValsGridTableViewer.setContentProvider(ArrayContentProvider.getInstance());


    // Create GridTableViewer columns
    createGridViewerColumns(this.statValsGridTableViewer);

    this.statValsGridTableViewer.setInput("");

    invokeColumnSorter(this.statValsTableSorter);

    // ICDM-226
    addRightClickMenu();

    // ICDM -588
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    DragSourceListener dragSourceListener = new DragSourceListener() {

      // ICDM-1381 - Drag more than one Value to Scratch pad.
      List<LabelValueInfoVO> selectedLabel = new ArrayList<>();
      List<Integer> dataSetNum = new ArrayList<>();
      List<SeriesStatisticsInfo> seriesStatInfoList = new ArrayList<>();

      @Override
      public void dragStart(final DragSourceEvent event) {
        event.doit = false;
        IStructuredSelection selection =
            (IStructuredSelection) SeriesStatisticsViewPart.this.statValsGridTableViewer.getSelection();
        if (!selection.isEmpty()) {
          for (Object firstElement : selection.toList()) {
            if (firstElement instanceof SeriesStatisticsTableData) {
              event.doit = true;
              this.selectedLabel.add(((SeriesStatisticsTableData) firstElement).getLabelValueInfo());
              this.dataSetNum.add(((SeriesStatisticsTableData) firstElement).getSerialIndex());
            }
          }
          // Icdm-674 create the Serial Index Column for the series statistics view part table

        }
      }

      @Override
      public void dragSetData(final DragSourceEvent event) {

        for (int index = 0; index < this.selectedLabel.size(); index++) {
          final LabelValueInfoVO lblValInfoVO = this.selectedLabel.get(index);
          final CalDataPhy calDataPhyObj = lblValInfoVO.getCalDataPhy();
          final CalData calDataObj = CalDataUtil.getCalData(calDataPhyObj, CalDataType.VALUE,
              SeriesStatisticsViewPart.this.labelInfoVO, lblValInfoVO, getAPICUser());
          final SeriesStatisticsInfo seriesStatisticsInfo = new SeriesStatisticsInfo(calDataObj, CalDataType.VALUE);
          seriesStatisticsInfo.setDataSetName("DataSet Sl. No" + String.valueOf(this.dataSetNum.get(index)));
          // ICDM-935
          seriesStatisticsInfo.setClassName(getParamClassName());
          this.seriesStatInfoList.add(seriesStatisticsInfo);

        }
        StructuredSelection structuredSelection = new StructuredSelection(this.seriesStatInfoList);
        LocalSelectionTransfer.getTransfer().setSelection(structuredSelection);
        this.selectedLabel = new ArrayList<>();
        this.dataSetNum = new ArrayList<>();
        this.seriesStatInfoList = new ArrayList<>();

      }

      @Override
      public void dragFinished(final DragSourceEvent event) {
        // TODO Auto-generated method stub

      }
    };
    // Cannot use CustomDragListener since this is a case where selection needs to converted(i.e to
    // SeriesStatisticsInfo)
    this.statValsGridTableViewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, dragSourceListener);
  }

  /**
   * This method adds right click menu for tableviewer
   */
  // ICDM-226
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(final IMenuManager mgr) {
        IStructuredSelection selection =
            (IStructuredSelection) SeriesStatisticsViewPart.this.statValsGridTableViewer.getSelection();
        if (!selection.isEmpty()) {
          List<SeriesStatisticsInfo> calDataObjects = new ArrayList<>();
          for (Object selectedVal : selection.toList()) {
            if (selectedVal instanceof SeriesStatisticsTableData) {
              final LabelValueInfoVO lblValInfoVO = ((SeriesStatisticsTableData) selectedVal).getLabelValueInfo();
              final CalDataPhy calDataPhyObj = lblValInfoVO.getCalDataPhy();
              final CalData calDataObj = CalDataUtil.getCalData(calDataPhyObj, CalDataType.VALUE,
                  SeriesStatisticsViewPart.this.labelInfoVO, lblValInfoVO, getAPICUser());
              final SeriesStatisticsInfo calDataProvider = new SeriesStatisticsInfo(calDataObj, CalDataType.VALUE);
              calDataProvider.setDataSetName(
                  "DataSet Sl. No" + String.valueOf(((SeriesStatisticsTableData) selectedVal).getSerialIndex()));
              // ICDM-935
              calDataProvider.setClassName(getParamClassName());
              calDataObjects.add(calDataProvider);
            }

            // Icdm-674 create the Serial Index Column for the series statistics view part table


          }
          SeriesStatisticsViewPart.this.commonActionSet.openMutlipleFromSeriesStat(menuMgr, calDataObjects);
          SeriesStatisticsViewPart.this.commonActionSet.copyToClipBoard(menuMgr, calDataObjects);
        }
      }
    });
    final Menu menu = menuMgr.createContextMenu(this.statValsGridTableViewer.getControl());
    this.statValsGridTableViewer.getControl().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.statValsGridTableViewer);
  }

  /**
   * This method gridviewercolumns for statistics values
   *
   * @param gridTableViewer
   */
  private void createGridViewerColumns(final GridTableViewer gridTableViewer) {

    // Creates serial Index gridviewer column
    // Icdm-674 create the Serial Index Column for the series statistics view part table
    createSerialIndexColViewer(gridTableViewer);
    // Creates datasets gridviewer column
    createDatasetsColViewer(gridTableViewer);

    // Creates unit gridviewer column
    createUnitColViewer(gridTableViewer);

    // Creates value gridviewer column
    createValColViewer(gridTableViewer);

    // Create graph gridviewer column
    createGraphColViewer(gridTableViewer);
  }

  /**
   * @param gridTableViewer //Icdm-674 create the Serial Index Column for the series statistics view part table
   */
  private void createSerialIndexColViewer(final GridTableViewer gridTableViewer) {
    final GridViewerColumn serialColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer,
        CommonUIConstants.SERIALINDEX, SERIAL_INDEX_COL_WIDTH);

    serialColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof SeriesStatisticsTableData) {
          return String.valueOf(((SeriesStatisticsTableData) element).getSerialIndex());
        }
        return null;
      }
    });
    // Add selection listener to dataset column
    serialColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(serialColumn.getColumn(), 0, this.statValsTableSorter, gridTableViewer));

  }

  /**
   * @param gridTableViewer
   */
  private void createGraphColViewer(final GridTableViewer gridTableViewer) {
    final GridColumn graphCol = new GridColumn(gridTableViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    graphCol.setWidth(GRAPH_COL_WIDTH);
    graphCol.setSummary(false);
    final GridViewerColumn graphViewerCol = new GridViewerColumn(gridTableViewer, graphCol);
    graphViewerCol.getColumn().setText("Table/Graph");
    graphViewerCol.getColumn().setWidth(VALUE_GRIDVIEWERCOLUMN_WIDTH);

    graphViewerCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
        if (!(graphCmpCount < GRAPH_COMP_COUNT)) {
          gridItem.setChecked(cell.getVisualIndex(), false);
          showGraphClearMessage();
        }
      }
    });

    graphViewerCol.setEditingSupport(new CheckEditingSupport(graphViewerCol.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        // Icdm-674 create the Serial Index Column for the series statistics view part table
        if (arg0 instanceof SeriesStatisticsTableData) {
          // iCDM-2232
          SeriesStatisticsViewPart.this.displayTbleGraph(graphViewerCol, arg0, arg1);
        }
      }
    });
  }

  /**
   * This method creates maximum value UI controls
   *
   * @param caldataComp
   */
  private void createMaxValUIControls(final Composite caldataComp) {
    // Icdm-433
    creaetLblControl(caldataComp, CommonUIConstants.MAX_VALUE, "The highest value in series datasets");
    this.txtMaxValue = createStyledText(caldataComp);
    createMaxBtnControl(caldataComp);
    // ICDM-226
    final MenuItem scratchPadMenuItem = addRightClickMenuForTxtField(this.txtMaxValue);
    // Add selection listener to menu item
    scratchPadMenuItem.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        // ICDM-226
        if (SeriesStatisticsViewPart.this.labelInfoVO == null) {
          CDMLogger.getInstance().info(CAL_DATA_INFO_IS_NOT_AVAILABLE, Activator.PLUGIN_ID);
        }
        else {
          final CalDataPhy calDataPhy = getCalDataPhy(SeriesStatisticsViewPart.this.labelInfoVO.getMaxValue());
          addCalDataToScratchPad(calDataPhy, SeriesStatisticsViewPart.this.commonActionSet, CalDataType.MAX);
        }
      }
    });

    setDragDrop(this.txtMaxValue, CalDataType.MAX);

    this.txtMaxValue.addMenuDetectListener(new MenuDetectListener() {

      @Override
      public void menuDetected(final MenuDetectEvent menuDetectEvent) {
        if (SeriesStatisticsViewPart.this.labelInfoVO == null) {
          menuDetectEvent.doit = false;
        }
        else {
          final CalDataPhy calDataPhyObj = getCalDataPhy(SeriesStatisticsViewPart.this.labelInfoVO.getMaxValue());
          if (calDataPhyObj == null) {
            menuDetectEvent.doit = false;
          }
          else {
            validateMenuCreation(scratchPadMenuItem, calDataPhyObj, CalDataType.MAX);
          }
        }
      }
    });
  }


  /**
   * This method creates peak value UI controls
   *
   * @param caldataComp
   */
  private void createPeakValUIControls(final Composite caldataComp) {
    // Icdm-433
    creaetLblControl(caldataComp, CommonUIConstants.PEAK_VALUE, "The most frequently used value in series datasets");
    this.txtPeakValue = createStyledText(caldataComp);
    createPkValBtnControl(caldataComp);
    // ICDM-226
    final MenuItem scratchPadMenuItem = addRightClickMenuForTxtField(this.txtPeakValue);
    // Add selection listener to menu item
    scratchPadMenuItem.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        if (SeriesStatisticsViewPart.this.labelInfoVO == null) {
          CDMLogger.getInstance().info(CAL_DATA_INFO_IS_NOT_AVAILABLE, Activator.PLUGIN_ID);
        }
        else {
          final CalDataPhy calDataPhy = getCalDataPhy(SeriesStatisticsViewPart.this.labelInfoVO.getPeakValue());
          addCalDataToScratchPad(calDataPhy, SeriesStatisticsViewPart.this.commonActionSet, CalDataType.PEAK);
        }
      }
    });
    this.txtPeakValue.addMenuDetectListener(new MenuDetectListener() {

      @Override
      public void menuDetected(final MenuDetectEvent menuDetectEvent) {
        if (SeriesStatisticsViewPart.this.labelInfoVO == null) {
          menuDetectEvent.doit = false;
        }
        else {
          final CalDataPhy calDataPhyObj = getCalDataPhy(SeriesStatisticsViewPart.this.labelInfoVO.getPeakValue());
          if (calDataPhyObj == null) {
            menuDetectEvent.doit = false;
          }
          else {
            validateMenuCreation(scratchPadMenuItem, calDataPhyObj, CalDataType.PEAK);
          }
        }
      }
    });


    setDragDrop(this.txtPeakValue, CalDataType.PEAK);

  }


  /**
   * @param caldataComp
   */
  private StyledText createStyledText(final Composite caldataComp) {
    StyledText styledTxt = new StyledText(caldataComp, SWT.SINGLE | SWT.BORDER);
    styledTxt.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(TEXT_FIELD_WIDTHHINT));
    return styledTxt;
  }

  /**
   * @return MenuItem
   */
  // ICDM-226
  private MenuItem addRightClickMenuForTxtField(final StyledText text) {
    // Create menu
    final Menu menu = new Menu(text.getParent());
    // Add menu item to menu
    final MenuItem scratchPadMenuItem = addMenuItem(menu, "Add to Scratch Pad");
    // Set menu to textfield
    text.setMenu(menu);
    return scratchPadMenuItem;
  }

  /**
   * The method return menu item object
   *
   * @param menu
   * @param menuItemName
   */
  // ICDM-226
  private MenuItem addMenuItem(final Menu menu, final String menuItemName) {
    final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
    // Set menu item name
    menuItem.setText(menuItemName);
    // Get scratch pad image
    final Image image = ImageManager.getInstance().getRegisteredImage(ImageKeys.SCRATCH_PAD_16X16);
    // Set scratch pad image to menu item
    menuItem.setImage(image);
    return menuItem;
  }

  /**
   * THis method creates UI controls for percentage
   *
   * @param caldataComp
   */
  private void createPercentageUIControls(final Composite caldataComp) {
    // For empty space
    createEmptySpaceControl(caldataComp);
    this.txtPercentageVal = createStyledText(caldataComp);
    // ICDM-226
    this.txtPercentageVal.addMenuDetectListener(new MenuDetectListener() {

      @Override
      public void menuDetected(final MenuDetectEvent menuDetectEvent) {
        menuDetectEvent.doit = false;
      }
    });
  }

  /**
   * This method creates empty space in UI
   *
   * @param caldataComp
   */
  private void createEmptySpaceControl(final Composite caldataComp) {
    new Label(caldataComp, SWT.NONE);
  }

  /**
   * This method creates average value UI controls
   *
   * @param caldataComp
   */
  private void createAvgUIControls(final Composite caldataComp) {
    // Icdm-433
    creaetLblControl(caldataComp, CommonUIConstants.AVERAGE, "The average value in series datasets");
    this.txtAvgValue = createStyledText(caldataComp);
    createAvgBtnControl(caldataComp);
    // ICDM-226
    final MenuItem scratchPadMenuItem = addRightClickMenuForTxtField(this.txtAvgValue);
    // Add selection listener to menu item
    scratchPadMenuItem.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        // ICDM-226
        if (SeriesStatisticsViewPart.this.labelInfoVO == null) {
          CDMLogger.getInstance().info(CAL_DATA_INFO_IS_NOT_AVAILABLE, Activator.PLUGIN_ID);
        }
        else {
          final CalDataPhy calDataPhy = getCalDataPhy(SeriesStatisticsViewPart.this.labelInfoVO.getAvgValue());
          addCalDataToScratchPad(calDataPhy, SeriesStatisticsViewPart.this.commonActionSet, CalDataType.AVERAGE);
        }
      }
    });
    this.txtAvgValue.addMenuDetectListener(new MenuDetectListener() {

      @Override
      public void menuDetected(final MenuDetectEvent menuDetectEvent) {
        if (SeriesStatisticsViewPart.this.labelInfoVO == null) {
          menuDetectEvent.doit = false;
        }
        else {
          final CalDataPhy calDataPhyObj = getCalDataPhy(SeriesStatisticsViewPart.this.labelInfoVO.getAvgValue());
          if (calDataPhyObj == null) {
            menuDetectEvent.doit = false;
          }
          else {
            validateMenuCreation(scratchPadMenuItem, calDataPhyObj, CalDataType.AVERAGE);
          }
        }
      }
    });


    setDragDrop(this.txtAvgValue, CalDataType.AVERAGE);
  }

  /**
   * create avg graph button
   *
   * @param caldataComp
   */
  private void createAvgBtnControl(final Composite caldataComp) {
    this.avgBtn = new Button(caldataComp, SWT.TOGGLE);
    this.avgBtn.setToolTipText(GRAPH_TOOL_TIP);
    this.avgBtn.setImage(this.graphBtnImage);
    this.avgBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvnt) {
        if (SeriesStatisticsViewPart.this.avgBtn.getSelection()) {
          if (graphCmpCount < GRAPH_COMP_COUNT) {
            shwGraph(CalDataType.AVERAGE, CommonUIConstants.AVERAGE);
          }
          else {
            SeriesStatisticsViewPart.this.avgBtn.setSelection(false);
            showGraphClearMessage();
          }
        }
        else {
          SeriesStatisticsViewPart.this.calDataMap.remove(CommonUIConstants.AVERAGE);
          graphCmpCount--;
        }
        displayTableGraphCompare(SeriesStatisticsViewPart.this.calDataMap);
      }


    });
  }

  private void showGraphClearMessage() {
    CDMLogger.getInstance().infoDialog(
        "Maximum limit for graph comparision reached.Please unselect to compare new values", Activator.PLUGIN_ID);
  }

  /**
   * create max graph button
   *
   * @param caldataComp
   */
  private void createMaxBtnControl(final Composite caldataComp) {
    this.maxBtn = new Button(caldataComp, SWT.TOGGLE);
    this.maxBtn.setToolTipText(GRAPH_TOOL_TIP);
    this.maxBtn.setImage(this.graphBtnImage);
    this.maxBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvnt) {
        if (SeriesStatisticsViewPart.this.maxBtn.getSelection()) {
          if (graphCmpCount < GRAPH_COMP_COUNT) {
            shwGraph(CalDataType.MAX, CommonUIConstants.MAX_VALUE);
          }
          else {
            SeriesStatisticsViewPart.this.maxBtn.setSelection(false);
            showGraphClearMessage();
          }
        }
        else {
          SeriesStatisticsViewPart.this.calDataMap.remove(CommonUIConstants.MAX_VALUE);
          graphCmpCount--;
        }
        displayTableGraphCompare(SeriesStatisticsViewPart.this.calDataMap);
      }
    });
  }

  /**
   * create peak graph button
   *
   * @param caldataComp
   */
  private void createPkValBtnControl(final Composite caldataComp) {
    this.peakValBtn = new Button(caldataComp, SWT.TOGGLE);
    this.peakValBtn.setToolTipText(GRAPH_TOOL_TIP);
    this.peakValBtn.setImage(this.graphBtnImage);
    this.peakValBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvnt) {
        if (SeriesStatisticsViewPart.this.peakValBtn.getSelection()) {
          triggerAfterPeakValBtnCheck();
        }
        else {
          triggerAfterPeakValBtnUncheck();
        }
        displayTableGraphCompare(SeriesStatisticsViewPart.this.calDataMap);
      }
    });
  }

  /**
   *
   */
  // iCDM-2232
  private void triggerAfterPeakValBtnCheck() {
    if (graphCmpCount < GRAPH_COMP_COUNT) {
      shwGraph(CalDataType.PEAK, CommonUIConstants.PEAK_VALUE);
    }
    else {
      SeriesStatisticsViewPart.this.peakValBtn.setSelection(false);
      showGraphClearMessage();
    }
  }

  /**
   *
   */
  // iCDM-2232
  private void triggerAfterPeakValBtnUncheck() {
    SeriesStatisticsViewPart.this.calDataMap.remove(CommonUIConstants.PEAK_VALUE);
    graphCmpCount--;
  }

  /**
   * create lowerQ graph button
   *
   * @param caldataComp
   */
  private void createLwQBtnControl(final Composite caldataComp) {
    this.lowerQBtn = new Button(caldataComp, SWT.TOGGLE);
    this.lowerQBtn.setToolTipText(GRAPH_TOOL_TIP);
    this.lowerQBtn.setImage(this.graphBtnImage);
    this.lowerQBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvnt) {
        if (SeriesStatisticsViewPart.this.lowerQBtn.getSelection()) {
          if (graphCmpCount < GRAPH_COMP_COUNT) {
            shwGraph(CalDataType.LOWER_QUARTILE, CommonUIConstants.LOWER_QUARTILE);
          }
          else {
            SeriesStatisticsViewPart.this.lowerQBtn.setSelection(false);
            showGraphClearMessage();
          }
        }
        else {
          SeriesStatisticsViewPart.this.calDataMap.remove(CommonUIConstants.LOWER_QUARTILE);
          graphCmpCount--;
        }
        displayTableGraphCompare(SeriesStatisticsViewPart.this.calDataMap);
      }
    });

  }

  /**
   * create median graph button
   *
   * @param caldataComp
   */
  private void createMdnBtnControl(final Composite caldataComp) {
    this.medianBtn = new Button(caldataComp, SWT.TOGGLE);
    this.medianBtn.setToolTipText(GRAPH_TOOL_TIP);
    this.medianBtn.setImage(this.graphBtnImage);
    this.medianBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvnt) {
        if (SeriesStatisticsViewPart.this.medianBtn.getSelection()) {
          if (graphCmpCount < GRAPH_COMP_COUNT) {
            shwGraph(CalDataType.MEDIAN, CommonUIConstants.MEDIAN);
          }
          else {
            SeriesStatisticsViewPart.this.medianBtn.setSelection(false);
            showGraphClearMessage();
          }
        }
        else {
          SeriesStatisticsViewPart.this.calDataMap.remove(CommonUIConstants.MEDIAN);
          graphCmpCount--;
        }
        displayTableGraphCompare(SeriesStatisticsViewPart.this.calDataMap);
      }


    });
  }


  /**
   * create upperQ graph button
   *
   * @param caldataComp
   */
  private void createUpQBtnControl(final Composite caldataComp) {
    this.upperQBtn = new Button(caldataComp, SWT.TOGGLE);
    this.upperQBtn.setToolTipText(GRAPH_TOOL_TIP);
    this.upperQBtn.setImage(this.graphBtnImage);
    this.upperQBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvnt) {
        if (SeriesStatisticsViewPart.this.upperQBtn.getSelection()) {
          if (graphCmpCount < GRAPH_COMP_COUNT) {
            shwGraph(CalDataType.UPPER_QUARTILE, CommonUIConstants.UPPER_QUARTILE);
          }
          else {
            SeriesStatisticsViewPart.this.upperQBtn.setSelection(false);
            showGraphClearMessage();
          }
        }
        else {
          SeriesStatisticsViewPart.this.calDataMap.remove(CommonUIConstants.UPPER_QUARTILE);
          graphCmpCount--;
        }
        displayTableGraphCompare(SeriesStatisticsViewPart.this.calDataMap);
      }


    });

  }


  /**
   * create min graph button
   *
   * @param caldataComp
   */
  private void createMinBtnControl(final Composite caldataComp) {
    this.minBtn = new Button(caldataComp, SWT.TOGGLE);
    this.minBtn.setToolTipText(GRAPH_TOOL_TIP);
    this.minBtn.setImage(this.graphBtnImage);
    this.minBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (SeriesStatisticsViewPart.this.minBtn.getSelection()) {
          if (graphCmpCount < GRAPH_COMP_COUNT) {
            shwGraph(CalDataType.MIN, CommonUIConstants.MIN_VALUE);
          }
          else {
            SeriesStatisticsViewPart.this.minBtn.setSelection(false);
            showGraphClearMessage();
          }
        }
        else {
          SeriesStatisticsViewPart.this.calDataMap.remove(CommonUIConstants.MIN_VALUE);
          graphCmpCount--;
        }
        displayTableGraphCompare(SeriesStatisticsViewPart.this.calDataMap);
      }
    });

  }

  /**
   * Display table/graph
   *
   * @param calDataMp calDataMap
   */
  protected void displayTableGraphCompare(final Map<String, Map<CalData, Integer>> calDataMp) {
    try {
      CalDataComparison calComp = new CalDataComparison();

      if (calDataMp.isEmpty()) {
        this.graphColMap.clear();
        SeriesStatisticsViewPart.this.calDataTableGraphComposite.clearColumns();
        SeriesStatisticsViewPart.this.calDataTableGraphComposite.clearHistory();
        if (!this.isValueType) {
          SeriesStatisticsViewPart.this.calDataTableGraphComposite.clearGrapPlotter();
        }
      }
      else {
        StringBuilder labelPrefix = new StringBuilder();
        int graphColor = 0;
        Iterator<Entry<String, Map<CalData, Integer>>> calDtIterator = calDataMp.entrySet().iterator();
        while (calDtIterator.hasNext()) {
          Entry<String, Map<CalData, Integer>> thisEntry = calDtIterator.next();
          Map<CalData, Integer> calDataList = thisEntry.getValue();
          Iterator<Entry<CalData, Integer>> dataSetValues = calDataList.entrySet().iterator();

          while (dataSetValues.hasNext()) {
            labelPrefix.append(thisEntry.getKey());
            Entry<CalData, Integer> selDataSet = dataSetValues.next();
            CalData calData = selDataSet.getKey();
            if (selDataSet.getValue() != 0) {
              // Icdm-674 create the Serial Index Column for the series statistics view part table
              labelPrefix.append("Sl. No");
              labelPrefix.append(" ");
              labelPrefix.append(selDataSet.getValue());
            }
            // ICDM-1037 - Label color Changes
            CalDataAttributes calAttr;
            // mapKey has the entry key
            String mapKey = thisEntry.getKey();
            if (DATASET_STR.equals(mapKey)) {
              // Give the label name as key.
              mapKey = labelPrefix.toString();
            }
            // If the Stat value is not present in the Map
            if (this.graphColMap.get(mapKey) == null) {
              // If the map is already populated then graphColor= MAx of the Key+1
              if (!this.graphColMap.values().isEmpty()) {
                List<Integer> colorIdList = new ArrayList<Integer>(this.graphColMap.values());
                Collections.sort(colorIdList);
                graphColor = colorIdList.get(colorIdList.size() - 1) + 1;
              }
              calAttr = new CalDataAttributes(calData, graphColor);
              this.graphColMap.put(mapKey, Integer.valueOf(graphColor));
            }
            else {
              calAttr = new CalDataAttributes(calData, this.graphColMap.get(mapKey));
            }
            graphColor++;
            calAttr.setShowDifferenceIndicator(true);

            calAttr.setLabelPrefix(" (" + labelPrefix + ") ");

            // Icdm-1036 - Prevent append of old Values if multiple values are added in graph.
            labelPrefix.setLength(STRBUF_CLR_LEN);
            calComp.addCalDataAttr(calAttr);

          }
        }
        SeriesStatisticsViewPart.this.calDataTableGraphComposite.fillTableAndGraph(calComp);
      }
    }
    catch (CalDataTableGraphException calDataGraphException) {
      CDMLogger.getInstance().error("Text values not supported in Graph", calDataGraphException, Activator.PLUGIN_ID);
    }


  }

  /**
   * This method creates median UI controls
   *
   * @param caldataComp
   */
  private void createMedianUIControls(final Composite caldataComp) {
    // Icdm-433
    creaetLblControl(caldataComp, CommonUIConstants.MEDIAN, "50th percentile (cuts data set in half)");
    this.txtMedian = createStyledText(caldataComp);
    createMdnBtnControl(caldataComp);
    // ICDM-226
    final MenuItem scratchPadMenuItem = addRightClickMenuForTxtField(this.txtMedian);
    // Add selection listener to menu item
    scratchPadMenuItem.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        // ICDM-226
        if (SeriesStatisticsViewPart.this.labelInfoVO == null) {
          CDMLogger.getInstance().info(CAL_DATA_INFO_IS_NOT_AVAILABLE, Activator.PLUGIN_ID);
        }
        else {
          final CalDataPhy calDataPhy = getCalDataPhy(SeriesStatisticsViewPart.this.labelInfoVO.getMedianValue());
          addCalDataToScratchPad(calDataPhy, SeriesStatisticsViewPart.this.commonActionSet, CalDataType.MEDIAN);
        }
      }
    });
    this.txtMedian.addMenuDetectListener(new MenuDetectListener() {

      @Override
      public void menuDetected(final MenuDetectEvent menuDetectEvnt) {
        if (SeriesStatisticsViewPart.this.labelInfoVO == null) {
          menuDetectEvnt.doit = false;
        }
        else {
          final CalDataPhy calDataPhyObj = getCalDataPhy(SeriesStatisticsViewPart.this.labelInfoVO.getMedianValue());
          if (calDataPhyObj == null) {
            menuDetectEvnt.doit = false;
          }
          else {
            validateMenuCreation(scratchPadMenuItem, calDataPhyObj, CalDataType.MEDIAN);
          }
        }
      }
    });

    setDragDrop(this.txtMedian, CalDataType.MEDIAN);
  }


  /**
   * This method creates Lower quartile UI controls
   *
   * @param caldataComp
   */
  private void createLowerQuartileUIControls(final Composite caldataComp) {
    // Icdm-433
    creaetLblControl(caldataComp, CommonUIConstants.LOWER_QUARTILE,
        "25th percentile (splits off the lowest 25% of data from the highest 75%)");
    this.txtLowerQuartile = createStyledText(caldataComp);
    createLwQBtnControl(caldataComp);
    // ICDM-226
    final MenuItem scratchPadMenuItem = addRightClickMenuForTxtField(this.txtLowerQuartile);
    // Add selection listener to menu item
    scratchPadMenuItem.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvnt) {
        // ICDM-226
        if (SeriesStatisticsViewPart.this.labelInfoVO == null) {
          CDMLogger.getInstance().info(CAL_DATA_INFO_IS_NOT_AVAILABLE, Activator.PLUGIN_ID);
        }
        else {
          final CalDataPhy lowerCalDataPhy =
              getCalDataPhy(SeriesStatisticsViewPart.this.labelInfoVO.getLowerQuartileValue());
          addCalDataToScratchPad(lowerCalDataPhy, SeriesStatisticsViewPart.this.commonActionSet,
              CalDataType.LOWER_QUARTILE);
        }
      }
    });
    this.txtLowerQuartile.addMenuDetectListener(new MenuDetectListener() {

      @Override
      public void menuDetected(final MenuDetectEvent menuDetectEvent) {
        if (SeriesStatisticsViewPart.this.labelInfoVO == null) {
          menuDetectEvent.doit = false;
        }
        else {
          final CalDataPhy calDataPhyObj =
              getCalDataPhy(SeriesStatisticsViewPart.this.labelInfoVO.getLowerQuartileValue());
          if (calDataPhyObj == null) {
            menuDetectEvent.doit = false;
          }
          else {
            validateMenuCreation(scratchPadMenuItem, calDataPhyObj, CalDataType.LOWER_QUARTILE);
          }
        }
      }
    });

    setDragDrop(this.txtLowerQuartile, CalDataType.LOWER_QUARTILE);
  }


  /**
   * This method creates Quartile UI controls
   *
   * @param caldataComp
   */
  private void createUpperQuartileUIControls(final Composite caldataComp) {
    // Icdm-433
    creaetLblControl(caldataComp, CommonUIConstants.UPPER_QUARTILE,
        "75th percentile (splits off the highest 25% of data from the lowest 75%)");
    this.txtUpperQuartile = createStyledText(caldataComp);
    createUpQBtnControl(caldataComp);
    // ICDM-226
    final MenuItem scratchPadMenuItem = addRightClickMenuForTxtField(this.txtUpperQuartile);
    // Add selection listener to menu item
    scratchPadMenuItem.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvnt) {
        // ICDM-226
        if (SeriesStatisticsViewPart.this.labelInfoVO == null) {
          CDMLogger.getInstance().info(CAL_DATA_INFO_IS_NOT_AVAILABLE, Activator.PLUGIN_ID);
        }
        else {
          final CalDataPhy upperCalDataPhy =
              getCalDataPhy(SeriesStatisticsViewPart.this.labelInfoVO.getUpperQuartileValue());
          addCalDataToScratchPad(upperCalDataPhy, SeriesStatisticsViewPart.this.commonActionSet,
              CalDataType.UPPER_QUARTILE);
        }
      }
    });
    this.txtUpperQuartile.addMenuDetectListener(new MenuDetectListener() {

      @Override
      public void menuDetected(final MenuDetectEvent menuDetectEvent) {
        if (SeriesStatisticsViewPart.this.labelInfoVO == null) {
          menuDetectEvent.doit = false;
        }
        else {
          final CalDataPhy calDataPhyObj =
              getCalDataPhy(SeriesStatisticsViewPart.this.labelInfoVO.getUpperQuartileValue());
          if (calDataPhyObj == null) {
            menuDetectEvent.doit = false;
          }
          else {
            validateMenuCreation(scratchPadMenuItem, calDataPhyObj, CalDataType.UPPER_QUARTILE);
          }
        }
      }
    });

    setDragDrop(this.txtUpperQuartile, CalDataType.UPPER_QUARTILE);
  }


  /**
   * This method creates used datasets UI controls
   *
   * @param caldataComp
   */
  private void createUsedDatasetUIControls(final Composite caldataComp) {
    // Icdm-433
    creaetLblControl(caldataComp, CommonUIConstants.USED_IN_DATASETS, "Number of series datasets using the parameter");
    this.txtUsedDatasets = createStyledText(caldataComp);
    // ICDM-226
    this.txtUsedDatasets.addMenuDetectListener(new MenuDetectListener() {

      @Override
      public void menuDetected(final MenuDetectEvent menuDetectEvent) {
        menuDetectEvent.doit = false;
      }
    });
  }

  /**
   * This method creates minimum value UI controls
   *
   * @param caldataComp
   */
  private void createMinValUIControls(final Composite caldataComp) {
    // Icdm-433
    creaetLblControl(caldataComp, CommonUIConstants.MIN_VALUE, "The lowest value in series datasets");
    this.txtMinValue = createStyledText(caldataComp);
    createMinBtnControl(caldataComp);
    // ICDM-226
    final MenuItem scratchPadMenuItem = addRightClickMenuForTxtField(this.txtMinValue);
    // Add selection listener to menu item
    scratchPadMenuItem.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        // ICDM-226
        if (SeriesStatisticsViewPart.this.labelInfoVO == null) {
          CDMLogger.getInstance().info(CAL_DATA_INFO_IS_NOT_AVAILABLE, Activator.PLUGIN_ID);
        }
        else {
          final CalDataPhy calDataPhy = getCalDataPhy(SeriesStatisticsViewPart.this.labelInfoVO.getMinValue());
          addCalDataToScratchPad(calDataPhy, SeriesStatisticsViewPart.this.commonActionSet, CalDataType.MIN);
        }
      }
    });

    this.txtMinValue.addMenuDetectListener(new MenuDetectListener() {

      @Override
      public void menuDetected(final MenuDetectEvent menuDetectEvnt) {
        if (SeriesStatisticsViewPart.this.labelInfoVO == null) {
          menuDetectEvnt.doit = false;
        }
        else {
          final CalDataPhy calDataPhyObj = getCalDataPhy(SeriesStatisticsViewPart.this.labelInfoVO.getMinValue());
          if (calDataPhyObj == null) {
            menuDetectEvnt.doit = false;
          }
          else {
            validateMenuCreation(scratchPadMenuItem, calDataPhyObj, CalDataType.MIN);
          }
        }
      }
    });

    setDragDrop(this.txtMinValue, CalDataType.MIN);
  }


  private void addListener(final StyledText text, final CalDataPhy calDataPhy) {

    text.setToolTipText("This value can be Dragged");

    // Listens the activation of the text fields
    final Listener activateListener = new Listener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void handleEvent(final Event event) {

        if (null != calDataPhy) {
          String value = text.getText().trim();
          // Following is additional check, were the trigger for
          // displaying values is not applicable
          if (!(("".equals(value) || "n.a.".equalsIgnoreCase(value)) || value.endsWith("%"))) {
            text.setSelection(0, value.length());
            text.showSelection();
          }
        }
      }
    };


    text.addListener(SWT.CURSOR_APPSTARTING, activateListener);


    Map<Integer, Listener> listenerMap = this.textListenerMap.get(text);
    if (listenerMap == null) {
      listenerMap = new ConcurrentHashMap<Integer, Listener>();
      this.textListenerMap.put(text, listenerMap);
    }

    listenerMap.put(SWT.CURSOR_APPSTARTING, activateListener);


    Listener[] deActivateListeners = text.getListeners(SWT.Deactivate);

    if (deActivateListeners.length == 0) {
      text.addListener(SWT.Deactivate, new Listener() {

        @Override
        public void handleEvent(final Event event) {
          text.setSelection(0);
          text.showSelection();
        }
      });
    }

  }

  /**
   * This method creates different parameter UI controls
   *
   * @param caldataComp
   */
  private void createDiffParamUIControls(final Composite caldataComp) {
    // Icdm-433
    creaetLblControl(caldataComp, CommonUIConstants.DIFFERENT_VALUES,
        "Number of different values of the parameter in all series datasets");
    this.txtDiffValues = createStyledText(caldataComp);
    // ICDM-226
    this.txtDiffValues.addMenuDetectListener(new MenuDetectListener() {

      @Override
      public void menuDetected(final MenuDetectEvent menuDetectListener) {
        menuDetectListener.doit = false;
      }
    });
  }

  /**
   * This method create Label instance for statisctical values
   *
   * @param caldataComp
   * @param lblName
   */
  private void creaetLblControl(final Composite caldataComp, final String lblName, final String toolTip) {
    Label createLabel = LabelUtil.getInstance().createLabel(this.toolkit, caldataComp, lblName);
    createLabel.setToolTipText(toolTip);
  }

  /**
   * This method creates Table/Graph UI form section
   *
   * @return Section
   */
  private Section createGraphSection() {
    // Creates Table/Graph Section
    return createSection(CommonUIConstants.TABLE_GRAPH, false);
  }

  /**
   * This method creates CalDataAnalyzer information UI form section
   *
   * @return Section
   */
  private Section createCaldataSection() {
    // Creates CalDataAnalyzer Section
    return createSection(CommonUIConstants.SERIES_STATISTICS_TITLE, false);
  }

  /**
   * This method creates section
   *
   * @param sectionName defines section name
   * @param descControlEnable defines description control enable or not
   * @return Section instance
   */
  private Section createSection(final String sectionName, final boolean descControlEnable) {
    return SectionUtil.getInstance().createSection(this.scrolledForm.getBody(), this.toolkit,
        GridDataUtil.getInstance().getGridData(), sectionName, descControlEnable);
  }

  /**
   * This method creates scrolled form
   *
   * @param parent
   */
  private void createScrolledForm(final Composite parent) {
    // Create scrolled form
    this.scrolledForm = this.toolkit.createScrolledForm(parent);
    // Set decorate form heading to form
    this.toolkit.decorateFormHeading(this.scrolledForm.getForm());
    // Set layout to scrolledForm
    final GridLayout gridLayout = new GridLayout();
    gridLayout.makeColumnsEqualWidth = true;
    gridLayout.numColumns = TBLVWR_GRDDTA_HOR_SPAN;
    this.scrolledForm.getBody().setLayout(gridLayout);
    // Set grid layout data to scrolledForm
    this.scrolledForm.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method create value gridviewer column
   *
   * @param gridTableViewer
   */
  private void createValColViewer(final GridTableViewer gridTableViewer) {
    final GridViewerColumn valColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer,
        CommonUIConstants.VALUE, VALUE_GRIDVIEWERCOLUMN_WIDTH /* Column width */);
    valColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        // Icdm-674 create the Serial Index Column for the series statistics view part table
        if (element instanceof SeriesStatisticsTableData) {
          final LabelValueInfoVO lblValueInfo = ((SeriesStatisticsTableData) element).getLabelValueInfo();
          return lblValueInfo.getCalDataPhy().getSimpleDisplayValue();
        }
        return null;
      }
    });
    // Add selection listener to value column
    valColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        valColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_3, this.statValsTableSorter, gridTableViewer));
  }

  /**
   * Add sorter for the table columns
   */
  private void invokeColumnSorter(final AbstractViewerSorter viewerSorter) {
    this.statValsGridTableViewer.setComparator(viewerSorter);
  }

  /**
   * This method create unit gridviewer column
   *
   * @param gridTableViewer
   */
  private void createUnitColViewer(final GridTableViewer gridTableViewer) {
    final GridViewerColumn unitColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer,
        CommonUIConstants.UNIT, UNIT_GRIDVIEWERCOLUMN_WIDTH /* Column width */);

    unitColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof SeriesStatisticsTableData) {
          final LabelValueInfoVO lblValueInfo = ((SeriesStatisticsTableData) element).getLabelValueInfo();
          return lblValueInfo.getCalDataPhy().getUnit();
        }
        return null;
      }
    });
    // Add selection listener to unit column
    unitColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        unitColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_2, this.statValsTableSorter, gridTableViewer));
  }

  /**
   * This method create datasets gridviewer column
   *
   * @param gridTableViewer
   */
  private void createDatasetsColViewer(final GridTableViewer gridTableViewer) {
    final GridViewerColumn datasetColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer,
        CommonUIConstants.DATASETS, DTSET_GRDVWRCOL_WIDTH);
    datasetColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof SeriesStatisticsTableData) {
          final LabelValueInfoVO lblValueInfo = ((SeriesStatisticsTableData) element).getLabelValueInfo();
          final int size = lblValueInfo.getFileIDList().size();
          return String.valueOf(size);
        }
        return null;
      }
    });
    // Add selection listener to dataset column
    datasetColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(datasetColumn.getColumn(), 1, this.statValsTableSorter, gridTableViewer));
  }


  /**
   * {@inheritDoc} set focus to scrolled form
   */
  @Override
  public void setFocus() {
    this.scrolledForm.setFocus();
  }

  /**
   * {@inheritDoc} dispose the toolkit
   */
  @Override
  public void dispose() {
    this.toolkit.dispose();
    super.dispose();
  }

  /**
   * This method fills Statistics view UI controls
   *
   * @param lblInfoVO instance
   */
  // ICDM-218, 217
  public void fillUIControls(final LabelInfoVO lblInfoVO) {

    if (!this.txtDiffValues.isDisposed()) {
      this.txtDiffValues.setText(String.valueOf(lblInfoVO.getNumberOfValues()));
    }
    if (!this.txtMinValue.isDisposed()) {
      this.txtMinValue.setText(lblInfoVO.getMinValueSimpleDisplay());
      removeListenerFromTextField(this.txtMinValue);
      addListener(this.txtMinValue, lblInfoVO.getMinValue());
    }
    if (!this.txtUsedDatasets.isDisposed()) {
      this.txtUsedDatasets.setText(String.valueOf(lblInfoVO.getSumDataSets()));

      removeListenerFromTextField(this.txtUsedDatasets);
      addListener(this.txtUsedDatasets, null);
    }
    if (!this.txtMaxValue.isDisposed()) {
      this.txtMaxValue.setText(lblInfoVO.getMaxValueSimpleDisplay());

      removeListenerFromTextField(this.txtMaxValue);
      addListener(this.txtMaxValue, lblInfoVO.getMaxValue());
    }
    if (!this.txtPeakValue.isDisposed()) {
      this.txtPeakValue.setText(lblInfoVO.getPeakValueSimpleDisplay());
      removeListenerFromTextField(this.txtPeakValue);
      addListener(this.txtPeakValue, lblInfoVO.getPeakValue());
    }
    if (!this.txtPercentageVal.isDisposed()) {
      this.txtPercentageVal.setText(lblInfoVO.getPeakValuePercentage() + "%");

      removeListenerFromTextField(this.txtPercentageVal);
      addListener(this.txtPercentageVal, null);
    }
    if (!this.txtAvgValue.isDisposed()) {
      this.txtAvgValue.setText(lblInfoVO.getAvgValueSimpleDisplay());

      removeListenerFromTextField(this.txtAvgValue);
      addListener(this.txtAvgValue, lblInfoVO.getAvgValue());
    }
    if (!this.txtMedian.isDisposed()) {
      this.txtMedian.setText(lblInfoVO.getMedianValueSimpleDisplay());

      removeListenerFromTextField(this.txtMedian);
      addListener(this.txtMedian, lblInfoVO.getMedianValue());
    }
    if (!this.txtLowerQuartile.isDisposed()) {
      this.txtLowerQuartile.setText(lblInfoVO.getLowerQuartileValueSimpleDisplay());

      removeListenerFromTextField(this.txtLowerQuartile);
      addListener(this.txtLowerQuartile, lblInfoVO.getLowerQuartileValue());
    }
    if (!this.txtUpperQuartile.isDisposed()) {
      this.txtUpperQuartile.setText(lblInfoVO.getUpperQuartileValueSimpleDisplay());

      removeListenerFromTextField(this.txtUpperQuartile);
      addListener(this.txtUpperQuartile, lblInfoVO.getUpperQuartileValue());
    }
    if (!this.statValsGridTableViewer.getControl().isDisposed()) {
      // Set input to statistical tableviewer
      this.isValueType = false;
      Set<SeriesStatisticsTableData> seriesStatData = createSeriesStatData(lblInfoVO.getValuesMap().values());
      this.statValsGridTableViewer.setInput(seriesStatData);

      // iCDM-2231
      SeriesStatisticsViewPart.this.calDataTableGraphComposite.createGraphValue();
      this.statValsGridTableViewer.refresh();
    }
  }


  /**
   * This method fills the view menu with the previously fetched series statistics data
   */
  public void fillSeriesStatisticsCacheMenu() {
    this.prevResultsAction.getMenuCreator().getMenu(this.parentComposite);
  }

  /**
   * @param values
   * @return //Icdm-674 create the Serial Index Column for the series statistics view part table
   */
  private Set<SeriesStatisticsTableData> createSeriesStatData(final Collection<LabelValueInfoVO> values) {
    Set<LabelValueInfoVO> valueInfoSet = new TreeSet<LabelValueInfoVO>(new Comparator<LabelValueInfoVO>() {

      /**
       * @param param1 LabelValueInfoVO
       * @param param2 LabelValueInfoVO
       * @return compare result integer
       */
      @Override
      public int compare(final LabelValueInfoVO param1, final LabelValueInfoVO param2) {

        // if the size of the getFileIDList is equal, the record would be filtered out. That's why the comparison has
        // to return -1 in case of equality. The comperator has the only meaning to order the records
        int comparisonResult = ApicUtil.compareLong(param2.getFileIDList().size(), param1.getFileIDList().size());
        if (comparisonResult == 0) {
          CalDataPhy cdpy1 = param1.getCalDataPhy();
          CalDataPhy cdpy2 = param2.getCalDataPhy();
          if (cdpy1 == null) {
            comparisonResult = cdpy2 == null ? 0 : 1;
          }
          else {
            comparisonResult = cdpy1.compareTo(cdpy2, SortColumns.SIMPLE_DISPLAY_VALUE);
          }
        }
        // In case of complex data types, the simple display value could be same even if the Caldataphy objects are
        // different
        return comparisonResult == 0 ? -1 : comparisonResult;

      }

    });
    valueInfoSet.addAll(values);

    Set<SeriesStatisticsTableData> seriesStatDataSet = new HashSet<SeriesStatisticsTableData>();
    int serialIndex = 0;
    for (LabelValueInfoVO labelValueInfoVO : valueInfoSet) {
      serialIndex++;
      SeriesStatisticsTableData tableData = new SeriesStatisticsTableData(labelValueInfoVO, serialIndex);
      seriesStatDataSet.add(tableData);

      // ICDM-2231
      loadHistogramSamples(tableData);
    }

    return seriesStatDataSet;
  }

  /**
   * This method add Histogram samples in the Histogram map, if the cal data phy type is VALUE
   *
   * @param tableData
   */
  // iCDM-2231
  private void loadHistogramSamples(final SeriesStatisticsTableData tableData) {
    LabelValueInfoVO dataSetlabelVo = tableData.getLabelValueInfo();
    CalDataPhy calDataPhy = dataSetlabelVo.getCalDataPhy();
    if (ApicConstants.VALUE_TEXT.equals(calDataPhy.getType())) {
      this.isValueType = true;
      final CalData calDataObj = CalDataUtil.getCalData(calDataPhy, CalDataType.VALUE,
          SeriesStatisticsViewPart.this.labelInfoVO, dataSetlabelVo, getAPICUser());
      HistogramSample histogramSample = createHistogramSample(calDataObj, dataSetlabelVo);
      addHistogramIntoMap(histogramSample);
    }
  }


  private void removeListenerFromTextField(final StyledText txtLowerLmtValue2) {
    final Map<Integer, Listener> listenerMap = this.textListenerMap.get(txtLowerLmtValue2);
    if (listenerMap != null) {
      for (Entry<Integer, Listener> listenerMapEntry : listenerMap.entrySet()) {
        txtLowerLmtValue2.removeListener(listenerMapEntry.getKey(), listenerMapEntry.getValue());
      }
    }
  }


  /**
   * Reset controls
   */
  // ICDM-351
  public void resetUIControls() {
    if (!this.txtDiffValues.isDisposed()) {
      this.txtDiffValues.setText(CommonUIConstants.EMPTY_STRING);
    }
    if (!this.txtMinValue.isDisposed()) {
      this.txtMinValue.setText(CommonUIConstants.EMPTY_STRING);
    }
    if (!this.txtUsedDatasets.isDisposed()) {
      this.txtUsedDatasets.setText(CommonUIConstants.EMPTY_STRING);
    }
    if (!this.txtMaxValue.isDisposed()) {
      this.txtMaxValue.setText(CommonUIConstants.EMPTY_STRING);
    }
    if (!this.txtPeakValue.isDisposed()) {
      this.txtPeakValue.setText(CommonUIConstants.EMPTY_STRING);
    }
    if (!this.txtPercentageVal.isDisposed()) {
      this.txtPercentageVal.setText(CommonUIConstants.EMPTY_STRING);
    }
    if (!this.txtAvgValue.isDisposed()) {
      this.txtAvgValue.setText(CommonUIConstants.EMPTY_STRING);
    }
    if (!this.txtMedian.isDisposed()) {
      this.txtMedian.setText(CommonUIConstants.EMPTY_STRING);
    }
    if (!this.txtLowerQuartile.isDisposed()) {
      this.txtLowerQuartile.setText(CommonUIConstants.EMPTY_STRING);
    }
    if (!this.txtUpperQuartile.isDisposed()) {
      this.txtUpperQuartile.setText(CommonUIConstants.EMPTY_STRING);
    }
    if (!this.statValsGridTableViewer.getControl().isDisposed()) {
      // Set input to statistical tableviewer
      this.statValsGridTableViewer.setInput(CommonUIConstants.EMPTY_STRING);
      this.statValsGridTableViewer.refresh();
    }
    // iCDM-1198, supporting changes
    if ((this.scrolledForm != null) && !this.scrolledForm.isDisposed()) {
      // clear the title(label name)
      this.scrolledForm.setText("PAR: ");
    }

    clearTableGraph();

    fillGraphForSelValues();
  }

  /**
   * Clear table and graph
   */
  public void clearTableGraph() {
    // iCDM-1198, supporting changes
    this.dataSetCalDt.clear();
    this.calDataMap.clear();
    SeriesStatisticsViewPart.graphCmpCount = 0;
    this.calDataTableGraphComposite.clearTableGraph();
    this.calDataTableGraphComposite.clearHistogramMap();
    // iCDM-2232
    this.calDataPhyValueList.clear();
    this.histogramColorMap.clear();
    this.peakValBtn.setSelection(false);
  }

  /**
   *
   */
  private void fillGraphForSelValues() {

    if (this.minBtn.getSelection()) {
      shwGraph(CalDataType.MIN, CommonUIConstants.MIN_VALUE);
    }
    if (this.maxBtn.getSelection()) {
      shwGraph(CalDataType.MAX, CommonUIConstants.MAX_VALUE);
    }
    if (this.peakValBtn.getSelection()) {
      shwGraph(CalDataType.PEAK, CommonUIConstants.PEAK_VALUE);
    }
    if (this.avgBtn.getSelection()) {
      shwGraph(CalDataType.AVERAGE, CommonUIConstants.AVERAGE);
    }
    if (this.medianBtn.getSelection()) {
      shwGraph(CalDataType.MEDIAN, CommonUIConstants.MEDIAN);
    }
    if (this.lowerQBtn.getSelection()) {
      shwGraph(CalDataType.LOWER_QUARTILE, CommonUIConstants.LOWER_QUARTILE);
    }
    if (this.upperQBtn.getSelection()) {
      shwGraph(CalDataType.UPPER_QUARTILE, CommonUIConstants.UPPER_QUARTILE);
    }
    displayTableGraphCompare(SeriesStatisticsViewPart.this.calDataMap);
  }

  /**
   * @return the labelInfoVO
   */
  // ICDM-218
  public LabelInfoVO getLabelInfoVO() {
    return this.labelInfoVO;
  }

  /**
   * @param labelInfoVO the labelInfoVO to set
   */
  // ICDM-218
  public void setLabelInfoVO(final LabelInfoVO labelInfoVO) {
    this.labelInfoVO = labelInfoVO;
  }

  /**
   * @return the APIC user name
   */
  // ICDM-226
  private String getAPICUser() {
    String userName = "";

    try {

      userName = new CurrentUserBO().getUserName();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return userName;
  }

  /**
   * @return the CalDataPhy
   */
  // ICDM-226
  private CalDataPhy getCalDataPhy(final CalDataPhy calDataPhy) {
    if (calDataPhy != null) {
      return calDataPhy;
    }
    return null;
  }

  /**
   * This method add the caldata to scratchpad
   *
   * @param calDataPhy
   * @param calDataPhyValue
   * @param commonActSet
   * @param isPeakValue
   * @param calDataPhyValType
   */
  // ICDM-226
  private void addCalDataToScratchPad(final CalDataPhy calDataPhy, final CommonActionSet commonActSet,
      final CalDataType seriesStatisticsType) {
    if (calDataPhy == null) {
      CDMLogger.getInstance().info(CAL_DATA_INFO_IS_NOT_AVAILABLE, Activator.PLUGIN_ID);
    }
    else {
      // Fill the CalData instance with CalDataPhy information & get
      // CalData instance
      final CalData calDataObj = CalDataUtil.getCalData(calDataPhy, seriesStatisticsType,
          SeriesStatisticsViewPart.this.labelInfoVO, null, getAPICUser());
      // Fill CalDataProvider with CalData and CalDataPhy value type
      final SeriesStatisticsInfo calDataProvider = new SeriesStatisticsInfo(calDataObj, seriesStatisticsType);
      // ICDM-935
      calDataProvider.setClassName(getParamClassName());

      // Set Series Statistics Information to action set
      commonActSet.setInstanceObject(calDataProvider);
      // Get ScratchPadViewPart instance
      final ScratchPadViewPart scratchViewPartNew = findScratchPadView();
      if (scratchViewPartNew != null) {
        // Get ScratchPadDataProvider instance
        final ScratchPadDataFetcher scratchPadDataObj = new ScratchPadDataFetcher();

        for (int i = 0; i < scratchViewPartNew.getNodeList().size(); i++) {
          final ScratchPadDataFetcher nodeCheck = scratchViewPartNew.getNodeList().get(i);
          if ((nodeCheck.getSeriesStatsInfo() != null) && (nodeCheck.getSeriesStatsInfo().getCalData() != null)) {
            if ((nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy() != null) &&
                nodeCheck.getSeriesStatsInfo().getCalData().getShortName()
                    .equals(calDataProvider.getCalData().getShortName()) &&
                nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy()
                    .equals(calDataProvider.getCalData().getCalDataPhy())) {

              scratchViewPartNew.getNodeList().remove(nodeCheck);
              break;
            }
          }
        }
        // Set CalDataProvider instance to ScratchPadDataProvider
        scratchPadDataObj.setSeriesStatsInfo(calDataProvider);
        scratchViewPartNew.getNodeList().add(scratchPadDataObj);
        Collections.sort(scratchViewPartNew.getNodeList(), new ScratchPadSorter());
        scratchViewPartNew.getTableViewer().setInput(scratchViewPartNew.getNodeList());
        scratchViewPartNew.getTableViewer().setSelection(new StructuredSelection(scratchPadDataObj), true);
      }
    }
  }

  /**
   * @return ScratchPadViewPart
   */
  private ScratchPadViewPart findScratchPadView() {
    return (ScratchPadViewPart) WorkbenchUtils.getView(ScratchPadViewPart.PART_ID);
  }

  /**
   * This method validates to create right click menu of Add to ScratchPad to enable or disable
   *
   * @param scratchPadMenuItem
   */
  private void validateScratchPadMenuToEnable(final MenuItem scratchPadMenuItem) {

    // ScratchPad menu item always enabled (Henze, 02-AUG-2013)
    scratchPadMenuItem.setEnabled(true);
  }


  /**
   * This method validates to create right click menu of Add to ScratchPad
   *
   * @param scratchPadMenuItem
   * @param calDataPhy
   * @param calDataPhyValType
   */
  private void validateMenuCreation(final MenuItem scratchPadMenuItem, final CalDataPhy calDataPhy,
      final CalDataType seriesStatisticsType) {
    final CalData calDataObj = CalDataUtil.getCalData(calDataPhy, seriesStatisticsType,
        SeriesStatisticsViewPart.this.labelInfoVO, null, getAPICUser());
    final SeriesStatisticsInfo seriesStatsInfo = new SeriesStatisticsInfo(calDataObj, seriesStatisticsType);
    // ICDM-935
    seriesStatsInfo.setClassName(getParamClassName());
    validateScratchPadMenuToEnable(scratchPadMenuItem);
  }


  private void setDragDrop(final StyledText styledText, final CalDataType seriesStatisticsType) {

    Transfer[] types = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;

    final DragSource source = new DragSource(styledText, operations);
    source.setTransfer(types);
    source.addDragListener(new DragSourceListener() {

      @Override
      public void dragStart(final DragSourceEvent event) {
        String value = styledText.getText();
        if (("".equals(value) || "n.a.".equalsIgnoreCase(value)) || value.endsWith("%")) {
          event.doit = false;
        }
        else {
          event.doit = true;
        }
      }

      @Override
      public void dragSetData(final DragSourceEvent event) {
        if (SeriesStatisticsViewPart.this.labelInfoVO != null) {
          final CalDataPhy calDataPhyObj = getCalDataPhy(findCalDataPhy(seriesStatisticsType));
          final CalData calDataObj = CalDataUtil.getCalData(calDataPhyObj, seriesStatisticsType,
              SeriesStatisticsViewPart.this.labelInfoVO, null, getAPICUser());
          event.data = calDataObj;
          final SeriesStatisticsInfo calDataProvider = new SeriesStatisticsInfo(calDataObj, seriesStatisticsType);
          // ICDM-935
          calDataProvider.setClassName(getParamClassName());
          StructuredSelection structuredSelection = new StructuredSelection(calDataProvider);
          LocalSelectionTransfer.getTransfer().setSelection(structuredSelection);
        }
      }

      @Override
      public void dragFinished(final DragSourceEvent event) {
        // TODO:
      }
    });

  }

  /**
   * @param seriesStatisticsType
   * @return
   */
  private CalDataPhy findCalDataPhy(final CalDataType seriesStatisticsType) {
    CalDataPhy calDataPhy = null;
    if (this.labelInfoVO != null) {
      switch (seriesStatisticsType) {
        case AVERAGE:
          calDataPhy = SeriesStatisticsViewPart.this.labelInfoVO.getAvgValue();
          break;

        case LOWER_QUARTILE:
          calDataPhy = SeriesStatisticsViewPart.this.labelInfoVO.getLowerQuartileValue();
          break;

        case MAX:
          calDataPhy = SeriesStatisticsViewPart.this.labelInfoVO.getMaxValue();
          break;

        case MEDIAN:
          calDataPhy = SeriesStatisticsViewPart.this.labelInfoVO.getMedianValue();
          break;

        case MIN:
          calDataPhy = SeriesStatisticsViewPart.this.labelInfoVO.getMinValue();
          break;

        case PEAK:
          calDataPhy = SeriesStatisticsViewPart.this.labelInfoVO.getPeakValue();
          break;

        case UPPER_QUARTILE:
          calDataPhy = SeriesStatisticsViewPart.this.labelInfoVO.getUpperQuartileValue();
          break;
        default:
          break;

      }
    }
    return calDataPhy;
  }


  /**
   * constructs the cal data map for the peak value of non-value type
   *
   * @param seriesStatisticsType
   * @param graphType
   */
  private void shwGraph(final CalDataType seriesStatisticsType, final String graphType) {
    CalDataPhy calDataPhy = findCalDataPhy(seriesStatisticsType);
    if (calDataPhy != null) {
      final CalData calDataObj = CalDataUtil.getCalData(calDataPhy, seriesStatisticsType,
          SeriesStatisticsViewPart.this.labelInfoVO, null, getAPICUser());
      Map<CalData, Integer> calDtList = new ConcurrentHashMap<CalData, Integer>();
      calDtList.put(calDataObj, 0);
      SeriesStatisticsViewPart.this.calDataMap.put(graphType, calDtList);
      graphCmpCount++;
    }
  }

  /**
   * Display the graph for selected dataset
   *
   * @param graphViewerCol
   * @param arg0
   * @param calDataObj
   * @param dataSetCount2
   */
  private void shwDataSetGraph(final GridViewerColumn graphViewerCol, final Object arg0, final CalData calDataObj,
      final Integer dataSetCount2) {
    if (graphCmpCount < GRAPH_COMP_COUNT) {
      SeriesStatisticsViewPart.this.dataSetCalDt.put(calDataObj, dataSetCount2);
      SeriesStatisticsViewPart.this.calDataMap.put(DATASET_STR, SeriesStatisticsViewPart.this.dataSetCalDt);
      graphCmpCount++;
      displayTableGraphCompare(SeriesStatisticsViewPart.this.calDataMap);
    }
    else {
      graphViewerCol.getViewer().update(arg0, null);
    }
  }

  /**
   * clear the graph for selected dataset
   *
   * @param calDataObj
   */
  private void clearDataSetGraph(final CalData calDataObj) {
    Map<CalData, Integer> dataSetCalData = SeriesStatisticsViewPart.this.calDataMap.get(DATASET_STR);
    CalData unCheckedCalData = null;
    // remove the caldata which is unchecked from the caldata list from dataset table
    for (CalData calData : dataSetCalData.keySet()) {
      if (calData.getCalDataPhy().equals(calDataObj.getCalDataPhy())) {
        unCheckedCalData = calData;
        break;
      }
    }
    if (unCheckedCalData != null) {
      dataSetCalData.remove(unCheckedCalData);
    }
    if (dataSetCalData.isEmpty()) {
      SeriesStatisticsViewPart.this.calDataMap.remove(DATASET_STR);
    }
    else {
      SeriesStatisticsViewPart.this.calDataMap.put(DATASET_STR, SeriesStatisticsViewPart.this.dataSetCalDt);
    }
    graphCmpCount--;
    displayTableGraphCompare(SeriesStatisticsViewPart.this.calDataMap);
  }

  /**
   * @return the paramClassName
   */
  public String getParamClassName() {
    return this.paramClassName;
  }


  /**
   * @param paramClassName the paramClassName to set
   */
  public void setParamClassName(final String paramClassName) {
    this.paramClassName = paramClassName;
  }

  /**
   * creates the histogram sample object
   *
   * @param calDataObj
   * @param dataSetlabelVo
   * @return
   */
  // iCDM-2232
  private HistogramSample createHistogramSample(final CalData calDataObj, final LabelValueInfoVO dataSetlabelVo) {
    CalDataPhyValue calDatPhy = (CalDataPhyValue) calDataObj.getCalDataPhy();

    // if the value is not a number, return it as null
    if (!CommonUtils.checkIfNumber(calDatPhy.getAtomicValuePhy().getSValue())) {
      return null;
    }
    double xaxisValue = Double.parseDouble(calDatPhy.getAtomicValuePhy().getSValue());
    double yaxisValue = dataSetlabelVo.getFileIDList().size();
    String unit = "Unit : " + dataSetlabelVo.getCalDataPhy().getUnit();
    return new HistogramSample(xaxisValue, yaxisValue, unit);
  }

  /**
   * adds the given histgram sample in the histogram map
   *
   * @param histogramSample
   */
  // iCDM-2232
  private void addHistogramIntoMap(final HistogramSample histogramSample) {
    if (null != histogramSample) {
      String unitDisplay = histogramSample.getUnit() == null ? "" : histogramSample.getUnit();

      Integer color = getHistogramColor(unitDisplay);
      HashMap<Integer, List<HistogramSample>> colorHistogramListMap =
          this.calDataTableGraphComposite.getHistgrmCollectionMap().get(unitDisplay);
      List<HistogramSample> histogramList;
      if ((colorHistogramListMap == null) || colorHistogramListMap.isEmpty()) {
        colorHistogramListMap = new HashMap<Integer, List<HistogramSample>>();
        histogramList = new ArrayList<>();
      }
      else {
        histogramList = colorHistogramListMap.get(color);
        if ((null == histogramList) || histogramList.isEmpty()) {
          histogramList = new ArrayList<>();
        }
      }
      histogramList.add(histogramSample);
      colorHistogramListMap.put(color, histogramList);
      this.calDataTableGraphComposite.getHistgrmCollectionMap().put(unitDisplay, colorHistogramListMap);
    }
  }

  /**
   * finds the color index for the given unitDisplay
   *
   * @param unitDisplay
   * @return the color index
   */
  // iCDM-2232
  private int getHistogramColor(final String unitDisplay) {
    List<Integer> histogramColorIdList = new ArrayList<Integer>(this.histogramColorMap.values());
    if (histogramColorIdList.isEmpty()) {
      this.histogramColorMap.put(unitDisplay, Integer.valueOf(0));
      return 0;
    }
    Collections.sort(histogramColorIdList);
    int loop = 0;
    int missedNumber = -1;
    for (Integer integer : histogramColorIdList) {
      if (integer != loop) {
        missedNumber = loop;
        break;
      }
      loop++;
    }

    int graphColor;
    if (this.histogramColorMap.get(unitDisplay) == null) {
      if (missedNumber == -1) {
        graphColor = histogramColorIdList.get(histogramColorIdList.size() - 1) + 1;
      }
      else {
        graphColor = missedNumber;
      }
      this.histogramColorMap.put(unitDisplay, Integer.valueOf(graphColor));
    }
    return this.histogramColorMap.get(unitDisplay);
  }

  // iCDM-2232
  private void displayTbleGraph(final GridViewerColumn graphViewerCol, final Object seriesStatisticsTableDataArg,
      final Object checkBoxArg) {

    LabelValueInfoVO dataSetlabelVo = ((SeriesStatisticsTableData) seriesStatisticsTableDataArg).getLabelValueInfo();
    CalDataPhy calDataPhy = dataSetlabelVo.getCalDataPhy();
    Integer rowCount = ((SeriesStatisticsTableData) seriesStatisticsTableDataArg).getSerialIndex();
    final CalData calDataObj = CalDataUtil.getCalData(calDataPhy, CalDataType.VALUE,
        SeriesStatisticsViewPart.this.labelInfoVO, dataSetlabelVo, getAPICUser());
    if (((Boolean) checkBoxArg).booleanValue()) {
      shwDataSetGraph(graphViewerCol, seriesStatisticsTableDataArg, calDataObj, rowCount);
    }
    else {
      clearDataSetGraph(calDataObj);
    }

  }


  /**
   * @return the scrolledForm
   */
  public ScrolledForm getScrolledForm() {
    return this.scrolledForm;
  }


  /**
   * @return the statValsGridTableViewer
   */
  public GridTableViewer getStatValsGridTableViewer() {
    return this.statValsGridTableViewer;
  }


}
