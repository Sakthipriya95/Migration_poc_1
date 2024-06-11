package com.bosch.caltool.icdm.common.ui.views;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldatacomparison.CalDataAttributes;
import com.bosch.calmodel.caldatacomparison.CalDataComparison;
import com.bosch.calmodel.caldataphy.CalDataPhy.SortColumns;
import com.bosch.calmodel.caldataphyutils.CalDataTableGraphComposite;
import com.bosch.calmodel.caldataphyutils.exception.CalDataTableGraphException;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewDetail;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewParameter;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResult;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.ss.CalDataType;
import com.bosch.caltool.icdm.client.bo.ss.SeriesStatisticsInfo;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.providers.ReviewDetailsContentProvider;
import com.bosch.caltool.icdm.common.ui.providers.ReviewDetailsLabelProvider;
import com.bosch.caltool.icdm.common.ui.sorters.ReviewDataResultTableSorter;
import com.bosch.caltool.icdm.common.ui.sorters.ReviewPIDCDetailsTableSorter;
import com.bosch.caltool.icdm.common.ui.utils.CalDataUtil;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.views.data.ReviewResultTableData;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.ReviewParamResponse;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.CustomTreeViewer;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author bru2cob
 */
public class ReviewDataViewPart extends AbstractViewPart {

  /**
   * Defines constant for Select a rule, in combo
   */
  private static final String TAG_SELECT_A_RULE = "<Select a Rule>";

  /**
   * FormToolkit instance
   */
  private FormToolkit toolkit;
  /**
   * Text instance for lower limit value
   */
  private StyledText txtLowerLmtValue;
  /**
   * Text instance for UPPER limit value
   */
  private StyledText txtUpperLmtValue;
  /**
   * Defines text field widthhint
   */
  private static final int TEXT_FIELD_WIDTHHINT = 90;
  /**
   * Instance of form
   */
  private Form scrolledForm;
  /**
   * Defines review data analyzer composite to split into number of columns
   */
  private static final int RVW_DATA_COMP_COL_COUNT = 5;

  private static final int TAB_VIEWER_VERICAL_SPAN = 6;
  /**
   * Defines statistic values grid tableviewer heighthint
   */
  private static final int TAB_VIEWER_HEIGHTHINT = 120;
  /**
   * Defines statistic values grid tableviewer horizontal span layout data
   */
  private static final int TAB_VIEWER_HOR_SPAN = 4;

  /** Table Graph Composite instance */
  private CalDataTableGraphComposite calDataTableGraphComposite;
  /**
   * Defines datasets gridviewer column width
   */
  private static final int GRIDVIEWERCOL_WIDTH = 100;

  /**
   * CDR rule instance
   */
  private ReviewRule selectedRule;


  /**
   * Text instance for unit value
   */
  private StyledText txtUnitValue;
  private StyledText txtHintValue;
  private StyledText txtMethodValue;
  private StyledText txtRefValue;
  /* iCDM-713 */
  private ReviewDataResultTableSorter resTableSorter;
  private GridTableViewer reviewResultGridTableViewer;

  private CustomTreeViewer pidcTreeTableView;
  private ReviewPIDCDetailsTableSorter resPidcTableSorter;
  private Combo comboRule;

  /**
   * Graph compare count
   */
  private int graphCmpCount;

  /**
   * calData map for graph compare
   */
  private final Map<String, Map<CalData, Integer>> calDataMap = new ConcurrentHashMap<String, Map<CalData, Integer>>();
  /**
   * list of caldata objects from dataset table
   */
  private final Map<CalData, Integer> dataSetCalDt = new HashMap<CalData, Integer>();


  private final Map<String, Integer> graphColMap = new ConcurrentHashMap<String, Integer>();
  private Button refBtn;
  private Image graphBtnImage;
  private String selectedDepenStr;
  private final Map<String, ReviewRule> depRuleMap = new ConcurrentHashMap<String, ReviewRule>();
  private SashForm mainComposite;
  /**
   * Review result with statistics
   */
  protected ReviewParameter reviewResult;
  /**
   * Indicates if any exception has occured
   */
  protected boolean exceptionOccured;
  /**
   * defines graph tool tip
   */
  private static final String GRAPH_TOOL_TIP = "Display graph and table";
  /**
   * define constant for check value
   */
  private static final String CHECK_VAL_STR = "CheckValue";
  /**
   * Unit width hint
   */
  private static final int UNIT_WIDTH_HINT = 100;
  /**
   * graph column width
   */
  private static final int GRAPH_COL_WIDTH = 40;
  /**
   * review result column width
   */
  private static final int COL_WIDTH_RVW_RES = 85;
  /**
   * rule section width hint
   */
  private static final int RULE_SECTION_WIDTH_HINT = 300;
  /**
   * color index for white
   */
  private static final int COLOR_INDEX_WHITE = 255;
  /**
   * Define size for minimum rules
   */
  private static final int RULES_MIN_SIZE = 1;
  /**
   * total number of graph can be shown in T/G weiwer
   */
  private static final int GRAPH_CNT_MAX = 9;
  /**
   * Label height hint
   */
  private static final int HINT_LABEL_HEIGHT = 100;
  /**
   * Label width hint
   */
  private static final int HINT_LABEL_WIDTH = 300;
  /**
   * Graph view column width
   */
  private static final int GRAPH_VIEW_COL_WIDTH = 70;
  /**
   * Sno column width
   */
  private static final int SNO_COL_WIDTH = 40;


  @Override
  public void createPartControl(final Composite parent) {
    addHelpAction();
    // Create toolkit
    this.toolkit = new FormToolkit(parent.getDisplay());

    // Create scrolled form
    createScrolledForm(parent);

    this.graphBtnImage = new Image(null,
        SeriesStatisticsViewPart.class.getClassLoader().getResourceAsStream("/icons/graph_compare.gif"));
    // Create scrolled form

    // Create review info ui section
    final Section reviewDataSection = createReviewdataSection();

    // Create review info ui composite
    final Composite reviewdataComp = this.toolkit.createComposite(reviewDataSection);

    reviewDataSection.setDescription("Select a rule from drop down to see rule details");

    reviewDataSection.setClient(reviewdataComp);

    // Set layout to caldataComp
    final GridLayout layout = new GridLayout();
    layout.numColumns = RVW_DATA_COMP_COL_COUNT;
    reviewdataComp.setLayout(layout);

    // Create ui controls on review data section
    createReviewDataUIControls(reviewdataComp);

    // -----------------------------------------------------

    // Create review result section
    final Section reviewResultSection = createReviewResultSection();

    // Create review result composite
    final Composite reviewResultComp = this.toolkit.createComposite(reviewResultSection);

    reviewResultSection.setClient(reviewResultComp);

    reviewResultSection.setDescription("Select a row to see Review details");

    reviewResultComp.setLayout(new GridLayout());

    // Create ui controls on review result section
    createReviewResultUIControls(reviewResultComp);

    // -----------------------------------------------------

    final Section pidcDetailsSection = SectionUtil.getInstance().createSection(reviewResultComp, this.toolkit,
        GridDataUtil.getInstance().getGridData(), "Review Details", false);

    final Composite pidcDetComp = this.toolkit.createComposite(pidcDetailsSection);

    pidcDetailsSection
        .setDescription("Displays details of selected review. Right click to open ProjectID card , Review Result");
    pidcDetailsSection.setClient(pidcDetComp);

    pidcDetComp.setLayout(new GridLayout());

    createPidcDetailsUIControls(pidcDetComp);

    // -----------------------------------------------------
    // add right click menu options for this table
    addRightClickMenu();

    // Create graph/table section
    final Section graphSection = createGraphSection();

    // Create graph/table composite
    Composite graphComp = this.toolkit.createComposite(graphSection);
    graphSection.setClient(graphComp);

    // Set layout to graph/table composite
    graphComp.setLayout(new GridLayout());
    // Create table graph composite structure
    this.calDataTableGraphComposite = new CalDataTableGraphComposite(graphComp, this.scrolledForm.getHorizontalBar(),
        this.scrolledForm.getVerticalBar(), CDMLogger.getInstance());

    // -----------------------------------------------------
    // set weights for view part
    this.mainComposite.setWeights(new int[] { 1, 1, 1 });

  }


  /**
   * Create review result grid table
   *
   * @param reviewResultComp
   */
  private void createReviewResultUIControls(final Composite reviewResultComp) {
    // create sorter
    this.resTableSorter = new ReviewDataResultTableSorter();

    // Create review result GridTableViewer
    this.reviewResultGridTableViewer =
        GridTableViewerUtil.getInstance().createGridTableViewer(reviewResultComp,
            GridDataUtil.getInstance().createHeightHintGridData(GridData.BEGINNING, false, false,
                TAB_VIEWER_HOR_SPAN, /*
                                      * Horizontal span
                                      */
                TAB_VIEWER_HEIGHTHINT, TAB_VIEWER_VERICAL_SPAN /* Vertical span */));

    this.reviewResultGridTableViewer.setContentProvider(ArrayContentProvider.getInstance());

    // Create GridTableViewer columns
    createGridViewerColumns(this.reviewResultGridTableViewer);
    // set comparatorr
    this.reviewResultGridTableViewer.setComparator(this.resTableSorter);
    // set input and refresh
    this.reviewResultGridTableViewer.setInput("");
    this.reviewResultGridTableViewer.refresh();
    // add listerner to fill pidc details
    this.reviewResultGridTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) ReviewDataViewPart.this.reviewResultGridTableViewer.getSelection();
        if ((selection != null) && (selection.getFirstElement() != null)) {
          ReviewResultTableData reviewRes = (ReviewResultTableData) selection.getFirstElement();
          // get pidc details
          Set<ReviewDetail> pidcDetails = reviewRes.getReviewResult().getReviewDetails();
          // set input and refresh
          ReviewDataViewPart.this.pidcTreeTableView.setInput(pidcDetails);
          ReviewDataViewPart.this.pidcTreeTableView.refresh();
        }
      }
    });
    addResultRightClickMenu();
  }

  /**
   * Add right lick context menu options
   */
  private void addResultRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(final IMenuManager menuManagr) {
        final IStructuredSelection selection =
            (IStructuredSelection) ReviewDataViewPart.this.reviewResultGridTableViewer.getSelection();

        if (!selection.isEmpty() && (selection.getFirstElement() instanceof ReviewResultTableData)) {
          ReviewResult reviewRes = ((ReviewResultTableData) selection.getFirstElement()).getReviewResult();
          ReviewDetail reviewDet = reviewRes.getReviewDetails().iterator().next();
          if (reviewDet != null) {

            CommonActionSet actionSet = new CommonActionSet();
            // open review result
            actionSet.openRulesEditor(menuManagr, reviewDet);
          }
        }
      }
    });
    // create context menu
    final Menu menu = menuMgr.createContextMenu(this.reviewResultGridTableViewer.getControl());
    this.reviewResultGridTableViewer.getControl().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.reviewResultGridTableViewer);
  }

  /**
   * Add right lick context menu options
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(final IMenuManager mgr) {
        final IStructuredSelection selection =
            (IStructuredSelection) ReviewDataViewPart.this.pidcTreeTableView.getSelection();
        final Object firstElement = selection.getFirstElement();
        if ((firstElement != null) && (!selection.isEmpty()) && (firstElement instanceof ReviewDetail)) {
          ReviewDetail result = (ReviewDetail) firstElement;
          CommonActionSet actionSet = new CommonActionSet();
          // add open pidc option
          actionSet.openPidcFromReviewResult(menuMgr, result);
          // add open review result option
          actionSet.openReviewResult(menuMgr, result);
        }
      }
    });
    // create context menu
    final Menu menu = menuMgr.createContextMenu(this.pidcTreeTableView.getControl());
    this.pidcTreeTableView.getControl().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.pidcTreeTableView);
  }

  /**
   * Creates data for the table viewer
   *
   * @param paramName
   * @param reviewStatistics
   * @return
   */
  private Set<ReviewResultTableData> createReviewResultData(final ReviewParameter revParam, final String paramName,
      final String varCodedName) {
    // create a sorted set
    Set<ReviewResult> reviewResSet = new TreeSet<ReviewResult>(new Comparator<ReviewResult>() {

      @Override
      public int compare(final ReviewResult param1, final ReviewResult param2) {

        // if the size of the getNumberOfRecords is equal, the record would be filtered out. That's why the comparison
        // has to return -1 in case of equality. The comperator has the only meaning to order the records
        int comparisonResult = ApicUtil.compareLong(param2.getNumberOfRecords(), param1.getNumberOfRecords());
        if (comparisonResult == 0) {
          CalData cdpy1 = param1.getCheckedValue();
          CalData cdpy2 = param2.getCheckedValue();
          if (cdpy1 == null) {
            comparisonResult = cdpy2 == null ? 0 : 1;
          }
          else {
            comparisonResult = cdpy1.getCalDataPhy().compareTo(cdpy2.getCalDataPhy(), SortColumns.SIMPLE_DISPLAY_VALUE);
          }
        }
        // In case of complex data types, the simple display value could be same even if the Caldataphy objects are
        // different
        return comparisonResult == 0 ? -1 : comparisonResult;

      }

    });

    Set<ReviewResult> uniqueResultMap = getUniqueResults(revParam.getReviewResults());
    modifyUniqueResults(uniqueResultMap, revParam.getReviewResults(), paramName);
    if (CommonUtils.isNotNull(varCodedName) && ApicUtil.isVariantCoded(varCodedName)) {
      mapPidcToReviewDetails(uniqueResultMap);
    }
    for (ReviewResult uniqueResult : uniqueResultMap) {
      reviewResSet.add(uniqueResult);
    }

    Set<ReviewResultTableData> rvwResDataSet = new HashSet<ReviewResultTableData>();
    // create result table data
    int serialIndex = 0;
    for (ReviewResult rvwRes : reviewResSet) {
      serialIndex++;
      ReviewResultTableData resData = new ReviewResultTableData(rvwRes, serialIndex);
      rvwResDataSet.add(resData);
    }
    return rvwResDataSet;
  }


  /**
   * @param uniqueResultMap uniqueResultMap
   */
  private void mapPidcToReviewDetails(final Set<ReviewResult> uniqueResultMap) {
    Map<String, ReviewDetail> reviewDetailMap = new ConcurrentHashMap<>();
    for (ReviewResult uniqResult : uniqueResultMap) {
      for (ReviewDetail detail : uniqResult.getReviewDetails()) {
        String key = detail.getPidcName() + uniqResult.getCheckedValue() + detail.getVariantName() +
            detail.getDateOfReviewString();
        if (reviewDetailMap.get(key) == null) {
          reviewDetailMap.put(key, detail);
          ReviewDetail reviewDetailCopy = ReviewDetail.createReviewDetailCopy(detail);
          detail.addChildDetail(reviewDetailCopy);
          reviewDetailCopy.setHasParent(true);
        }
        else {
          reviewDetailMap.get(key).addChildDetail(detail);
          detail.setHasParent(true);
        }

      }
    }
  }

  /**
   * @param uniqueResults
   * @param oldResult
   * @param paramName
   */
  private void modifyUniqueResults(final Set<ReviewResult> uniqueResultMap, final Set<ReviewResult> oldResult,
      final String paramName) {
    // uniqueResultMap --- Map with Entry as Alt_facAltDes_MAP[0] and with the Review Result set.(In this case two
    // Values)
    for (ReviewResult uniqResultEntry : uniqueResultMap) {
      // Review Results response from the Webs serive - here 4 Values.Alt_facAltDes_MAP[0]
      // ,Alt_facAltDes_MAP[1],Alt_facAltDes_MAP[2],Alt_facAltDes_MAP[3]
      for (ReviewResult wsRevResult : oldResult) {
        // If the Alt_facAltDes_MAP[0] is already added the same review details need not be added
        // Iterate through the Results of the map if the Param is diff to add the Review details
        ReviewResult uniqResult = uniqResultEntry;
        if (uniqResult.getCheckedValue() == null) {
          continue;
        }
        // iCDM-2071
        CalData uniqueResCalData = com.bosch.caltool.icdm.common.util.CalDataUtil
            .createCopy(uniqResult.getCheckedValue(), CDMLogger.getInstance());

        // If the Cal data phy obj is same add the Review details.
        if (wsRevResult.getCheckedValue() == null) {
          continue;
        }
        // iCDM-2071
        CalData wsCaldata = com.bosch.caltool.icdm.common.util.CalDataUtil.createCopy(wsRevResult.getCheckedValue(),
            CDMLogger.getInstance());
        wsCaldata.getCalDataPhy().setName(paramName);
        uniqueResCalData.getCalDataPhy().setName(paramName);
        if (uniqueResCalData.getCalDataPhy().equals(wsCaldata.getCalDataPhy())) {
          // this says that the Cal data obj's are same and the details are grouped
          uniqResult.addReviewDetails(wsRevResult.getReviewDetails());
        }
      }
    }

  }


  /**
   * @param oldResults oldResults
   * @param paramName
   * @return the Unique Results based on the Caldataphy Objcet.
   */
  private Set<ReviewResult> getUniqueResults(final Set<ReviewResult> oldResults) {
    List<ReviewResult> resultList = new ArrayList<>();
    resultList.addAll(oldResults);

    Set<ReviewResult> uniqueResultSet = new HashSet<>();
    for (int count = 0; count < resultList.size(); count++) {


      CalData tempCalData = resultList.get(count).getCheckedValue();
      if (tempCalData == null) {
        uniqueResultSet.add(resultList.get(count));
        continue;
      }
      boolean isSame = false;
      if (count < (resultList.size() - 1)) {
        isSame = compareResultObjects(resultList, count, tempCalData);
      }
      if (!isSame) {
        uniqueResultSet.add(resultList.get(count));
      }
    }

    return uniqueResultSet;
  }


  /**
   * @param resultList
   * @param count
   * @param tempCalData
   * @param isSame
   * @return
   */
  private boolean compareResultObjects(final List<ReviewResult> resultList, final int count,
      final CalData tempCalData) {
    boolean paramSame = false;
    for (int i = count + 1; i < resultList.size(); i++) {
      CalData calData = resultList.get(i).getCheckedValue();
      if (calData == null) {
        break;
      }
      BitSet checkBitSet = ApicUtil.getBiSetForParamName();
      checkBitSet.and(calData.getCalDataPhy().equalsExt(tempCalData.getCalDataPhy()));
      if (checkBitSet.isEmpty()) {
        paramSame = true;
        break;
      }
    }
    return paramSame;
  }

  /**
   * Get review statistics of the parameter
   *
   * @param paramName paramName
   * @param paramIds
   */
  private void getReviewStatistics(final String paramName, Map<String, ReviewParamResponse> reviewDataResponse) {
    // create new progress monitor
    final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
    try {
      dialog.run(true, true, new IRunnableWithProgress() {

        @Override
        public void run(final IProgressMonitor monitor) {
          monitor.beginTask("Fetching review statistics...", IProgressMonitor.UNKNOWN);
          // call WS to fetch review statistics
          callApicWebService(monitor, paramName,reviewDataResponse);
        }

      });
    }
    catch (InvocationTargetException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    catch (InterruptedException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param monitor
   * @param paramName paramName
   * @param paramIds
   */
  private void callApicWebService(final IProgressMonitor monitor, final String paramName, Map<String, ReviewParamResponse> reviewDataResponse) {

    // call WS
    getResultsFromWebService(paramName,reviewDataResponse);

    monitor.done();

    // check for cancel
    checkIfCancelled(monitor);
    // clear table/graph
    clearTablesAsync();
  }

  /**
   * Call webservice to fetch review results
   *
   * @param apicWsServer
   * @param paramName paramName
   * @param paramIds
   */
  private void getResultsFromWebService(final String paramName, Map<String, ReviewParamResponse> reviewDataResponse) {

    try {

      // get review result
      ReviewDataViewPart.this.reviewResult = new ReviewParameter(reviewDataResponse, paramName);
      // check if statistics are available
      if ((ReviewDataViewPart.this.reviewResult == null) ||
          ReviewDataViewPart.this.reviewResult.getReviewResults().isEmpty()) {
        CDMLogger.getInstance().infoDialog("No Review Results available for the selected parameter",
            Activator.PLUGIN_ID);
        // consider this also as exception case for futher validation
        ReviewDataViewPart.this.exceptionOccured = true;
      }
    }

    catch (Exception exception) {
      CDMLogger.getInstance().errorDialog("Exception occured while fetching review statistics from WebService",
          exception, Activator.PLUGIN_ID);
      ReviewDataViewPart.this.exceptionOccured = true;
    }
  }


  /**
   * Check if progress is cancelled
   *
   * @param monitor
   */
  private void checkIfCancelled(final IProgressMonitor monitor) {
    // check if monitor is cancelled
    if (monitor.isCanceled()) {
      try {
        throw new InterruptedException("Fetching review statistics selected parameter was cancelled");
      }
      catch (InterruptedException e) {
        CDMLogger.getInstance().info(e.getLocalizedMessage(), Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * Clear the old data, run in a UI thread
   */
  private void clearTablesAsync() {
    Display.getDefault().asyncExec(new Runnable() {

      @Override
      public void run() {
        if (ReviewDataViewPart.this.exceptionOccured) {
          // clear old review statistics
          ReviewDataViewPart.this.reviewResultGridTableViewer.setInput("");
          ReviewDataViewPart.this.reviewResultGridTableViewer.refresh();
          // clear pidc table
          ReviewDataViewPart.this.pidcTreeTableView.setInput("");
          ReviewDataViewPart.this.pidcTreeTableView.refresh();
        }

      }
    });
  }

  /**
   * @param reviewResultGridTableViewer
   */
  private void createGridViewerColumns(final GridTableViewer gridTableViewer) {
    // create sno column
    createSNoColViewer(gridTableViewer);
    // Creates reviews gridviewer column
    createReviewsColViewer(gridTableViewer);
    // Creates check value gridviewer column
    createChkValColViewer(gridTableViewer);
    // create unit column
    createUnitColViewer(gridTableViewer);
    // create in graph column
    createInGraphColViewer(gridTableViewer);
  }

  /**
   * Create s.no cloumn
   *
   * @param reviewResultGridTableViewer
   */
  private void createSNoColViewer(final GridTableViewer gridTableViewer) {
    // create sno column
    final GridViewerColumn sNoColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer, "S.No", SNO_COL_WIDTH);

    sNoColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ReviewResultTableData) {
          return String.valueOf(((ReviewResultTableData) element).getSerialIndex());
        }
        return null;
      }
    });
    // Add selection listener to dataset column
    sNoColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        sNoColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_0, this.resTableSorter, gridTableViewer));


  }

  /**
   * Create review column
   *
   * @param reviewResultGridTableViewer
   */
  private void createReviewsColViewer(final GridTableViewer gridTableViewer) {
    // create review count column
    final GridViewerColumn reviewsColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer,
        "No of Reviews", GRIDVIEWERCOL_WIDTH /* Column width */);
    reviewsColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ReviewResultTableData) {
          return String.valueOf(((ReviewResultTableData) element).getReviewResult().getNumberOfRecords());
        }
        return null;
      }
    });
    // Add selection listener to dataset column
    reviewsColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        reviewsColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_1, this.resTableSorter, gridTableViewer));

  }

  /**
   * Create check value column
   *
   * @param reviewResultGridTableViewer
   */
  private void createChkValColViewer(final GridTableViewer gridTableViewer) {
    // create check value column
    final GridViewerColumn chkValColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer,
        CommonUIConstants.CHECK_VALUE, GRIDVIEWERCOL_WIDTH /* Column width */);
    chkValColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ReviewResultTableData) {
          return String.valueOf(((ReviewResultTableData) element).getReviewResult().getCheckedValueString());
        }
        return null;
      }
    });
    // Add selection listener to dataset column
    chkValColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        chkValColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_2, this.resTableSorter, gridTableViewer));


  }


  private void createUnitColViewer(final GridTableViewer gridTableViewer) {
    // create unit column
    final GridViewerColumn unitColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer, "Unit", UNIT_WIDTH_HINT);
    unitColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ReviewResultTableData) {
          return CommonUtils.checkNull(((ReviewResultTableData) element).getReviewResult().getUnit());
        }
        return null;
      }
    });
    // Add selection listener to dataset column
    unitColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        unitColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_3, this.resTableSorter, gridTableViewer));

  }

  private void createInGraphColViewer(final GridTableViewer gridTableViewer) {
    // create in graph column
    final GridColumn graphCol = new GridColumn(gridTableViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    graphCol.setWidth(GRAPH_COL_WIDTH);
    graphCol.setSummary(false);
    final GridViewerColumn graphViewerCol = new GridViewerColumn(gridTableViewer, graphCol);
    graphViewerCol.getColumn().setText("In Graph");
    graphViewerCol.getColumn().setWidth(GRAPH_VIEW_COL_WIDTH);
    graphViewerCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        if (element instanceof ReviewResultTableData) {
          final ReviewResultTableData item = (ReviewResultTableData) element;
          if (item.isInGraph()) {
            final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), true);
          }
          else {
            final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), false);
          }
        }
      }

    });


    graphViewerCol.setEditingSupport(new CheckEditingSupport(graphViewerCol.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        if (arg0 instanceof ReviewResultTableData) {
          ReviewResult rvwResult = ((ReviewResultTableData) arg0).getReviewResult();
          CalData calData = rvwResult.getCheckedValue();
          Integer rowCount = ((ReviewResultTableData) arg0).getSerialIndex();
          if (((Boolean) arg1).booleanValue()) {
            ((ReviewResultTableData) arg0).setInGraph(true);
            shwDataSetGraph(graphViewerCol, arg0, calData, rowCount);
          }
          else {
            ((ReviewResultTableData) arg0).setInGraph(false);
            clearDataSetGraph(calData);
          }
        }
      }

    });
    graphViewerCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        graphViewerCol.getColumn(), CommonUIConstants.COLUMN_INDEX_4, this.resTableSorter, gridTableViewer));
  }

  /**
   * PIDC Details table UI controls
   *
   * @param pidcComposite
   */
  private void createPidcDetailsUIControls(final Composite pidcComposite) {
    // create sorter
    this.resPidcTableSorter = new ReviewPIDCDetailsTableSorter();


    this.pidcTreeTableView =
        new CustomTreeViewer(pidcComposite, SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    initializeEditorStatusLineManager(this.pidcTreeTableView);

    this.pidcTreeTableView.getTree().setHeaderVisible(true);
    this.pidcTreeTableView.getTree().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.pidcTreeTableView.setAutoExpandLevel(1);


    this.pidcTreeTableView.setContentProvider(new ReviewDetailsContentProvider());
    this.pidcTreeTableView.setLabelProvider(new ReviewDetailsLabelProvider());

    // Create GridTableViewer columns
    createPidcDetGridViewerColumns(this.pidcTreeTableView);
    // enable tooltip
    ColumnViewerToolTipSupport.enableFor(this.pidcTreeTableView, ToolTip.NO_RECREATE);
    // set comparator
    this.pidcTreeTableView.setComparator(this.resPidcTableSorter);
    // set input and refresh
    this.pidcTreeTableView.setInput("");
    this.pidcTreeTableView.refresh();
  }

  private void createPidcDetGridViewerColumns(final CustomTreeViewer pidcTableTreeView) {
    // create pidc col
    createPidcColViewer(pidcTableTreeView);
    // Create variant gridviewer column
    createVariantColViewer(pidcTableTreeView);
    // Creates date gridviewer column
    createDateColViewer(pidcTableTreeView);
    // create review result column
    createReviewResultColViewer(pidcTableTreeView);
    // create comment
    createCommentColViewer(pidcTableTreeView);
  }

  /**
   * Status updation based on selection in the view part
   *
   * @param viewer selected table viewer
   */
  private void initializeEditorStatusLineManager(final Viewer viewer) {
    if (viewer instanceof CustomGridTableViewer) {
      CustomGridTableViewer customGridTableViewer = (CustomGridTableViewer) viewer;
      IStatusLineManager editorStatus = getViewSite().getActionBars().getStatusLineManager();
      customGridTableViewer.setStatusLineManager(editorStatus);
    }
    if (viewer instanceof CustomTreeViewer) {
      CustomTreeViewer customTreeViewer = (CustomTreeViewer) viewer;
      IStatusLineManager editorStatus = getViewSite().getActionBars().getStatusLineManager();
      customTreeViewer.setStatusLineManager(editorStatus);
    }

  }


  /**
   * @param pidcTableTreeView
   */
  private void createPidcColViewer(final CustomTreeViewer pidcTableTreeView) {

    final TreeColumn pidcNameCol = new TreeColumn(pidcTableTreeView.getTree(), SWT.LEFT);
    pidcTableTreeView.getTree().setLinesVisible(true);
    pidcNameCol.setAlignment(SWT.LEFT);
    pidcNameCol.setText("PIDC Name");
    pidcNameCol.setWidth(GRIDVIEWERCOL_WIDTH);
    // create pidc name
    // Add selection listener to dataset column
    pidcNameCol.addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(pidcNameCol,
        CommonUIConstants.COLUMN_INDEX_0, this.resPidcTableSorter, pidcTableTreeView));

  }

  /**
   * @param pidcTableTreeView
   */
  private void createVariantColViewer(final CustomTreeViewer pidcTableTreeView) {

    final TreeColumn varNameCol = new TreeColumn(pidcTableTreeView.getTree(), SWT.LEFT);
    pidcTableTreeView.getTree().setLinesVisible(true);
    varNameCol.setAlignment(SWT.LEFT);
    varNameCol.setText("Variant");
    varNameCol.setWidth(GRIDVIEWERCOL_WIDTH);


    // Add selection listener to dataset column
    varNameCol.addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(varNameCol,
        CommonUIConstants.COLUMN_INDEX_1, this.resPidcTableSorter, pidcTableTreeView));

  }

  /**
   * @param pidcTableTreeView
   */
  private void createDateColViewer(final CustomTreeViewer pidcTableTreeView) {
    // create date of review
    final TreeColumn dateRvwCol = new TreeColumn(pidcTableTreeView.getTree(), SWT.LEFT);
    pidcTableTreeView.getTree().setLinesVisible(true);
    dateRvwCol.setAlignment(SWT.LEFT);
    dateRvwCol.setText("Review Date");
    dateRvwCol.setWidth(TEXT_FIELD_WIDTHHINT);
    // Add selection listener to dataset column
    dateRvwCol.addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(dateRvwCol,
        CommonUIConstants.COLUMN_INDEX_2, this.resPidcTableSorter, pidcTableTreeView));

  }

  /**
   * @param pidcTableTreeView
   */
  private void createReviewResultColViewer(final CustomTreeViewer pidcTableTreeView) {


    final TreeColumn rvwResCol = new TreeColumn(pidcTableTreeView.getTree(), SWT.LEFT);
    pidcTableTreeView.getTree().setLinesVisible(true);
    rvwResCol.setAlignment(SWT.LEFT);
    rvwResCol.setText("Review Result");
    rvwResCol.setWidth(COL_WIDTH_RVW_RES);
    // create review result

    // Add selection listener to dataset column
    rvwResCol.addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(rvwResCol,
        CommonUIConstants.COLUMN_INDEX_3, this.resPidcTableSorter, pidcTableTreeView));

  }

  /**
   * @param pidcTableTreeView
   */
  private void createCommentColViewer(final CustomTreeViewer pidcTableTreeView) {

    final TreeColumn commentCol = new TreeColumn(pidcTableTreeView.getTree(), SWT.LEFT);
    pidcTableTreeView.getTree().setLinesVisible(true);
    commentCol.setAlignment(SWT.LEFT);
    commentCol.setText("Comment");
    commentCol.setWidth(GRIDVIEWERCOL_WIDTH);

    // Add selection listener to dataset column
    commentCol.addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(commentCol,
        CommonUIConstants.COLUMN_INDEX_4, this.resPidcTableSorter, pidcTableTreeView));

  }

  /**
   * Create review section
   *
   * @return
   */
  private Section createReviewResultSection() {
    // Creates review result Section
    return createSection(CommonUIConstants.REVIEW_RESULT_TITLE, false);
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
   * This method creates section
   *
   * @param sectionName defines section name
   * @param descControlEnable defines description control enable or not
   * @return Section instance
   */
  private Section createSection(final String sectionName, final boolean descControlEnable) {
    return SectionUtil.getInstance().createSection(this.mainComposite, this.toolkit,
        GridDataUtil.getInstance().getGridData(), sectionName, descControlEnable);
  }

  /**
   * Create review data ui control
   *
   * @param reviewdataComp
   */
  private void createReviewDataUIControls(final Composite reviewdataComp) {

    // create drop down based on condition
    createRuleSelectionSection(reviewdataComp);

    // create lower limit
    createLowerLimitUIControls(reviewdataComp);
    createEmptySpaceControl(reviewdataComp);
    // create method
    createMethodUIControls(reviewdataComp);
    // create upper limit
    createUpperLimitUIControls(reviewdataComp);
    createEmptySpaceControl(reviewdataComp);
    // create unit limit
    createUnitUIControls(reviewdataComp);
    // create reference value
    createRefValUIControls(reviewdataComp);
    // create two empty label to fill colums
    createEmptySpaceControl(reviewdataComp);
    createEmptySpaceControl(reviewdataComp);
    // create hint
    createHintUIControls(reviewdataComp);

  }

  /**
   * @param reviewdataComp
   */
  private void createRuleSelectionSection(final Composite reviewdataComp) {
    createLblControl(reviewdataComp, "Rule : ", "");
    this.comboRule = new Combo(reviewdataComp, SWT.READ_ONLY);
    this.comboRule.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        int index = ReviewDataViewPart.this.comboRule.getSelectionIndex();
        ReviewDataViewPart.this.selectedDepenStr = ReviewDataViewPart.this.comboRule.getItem(index);
        ReviewDataViewPart.this.selectedRule =
            ReviewDataViewPart.this.depRuleMap.get(ReviewDataViewPart.this.selectedDepenStr);
        resetUIControls(false);
        // refill data
        fillValuesReviewData();
      }
    });
    GridData gridData = new GridData();
    gridData.horizontalSpan = TAB_VIEWER_HOR_SPAN;
    gridData.widthHint = RULE_SECTION_WIDTH_HINT;
    gridData.grabExcessHorizontalSpace = true;
    this.comboRule.setLayoutData(gridData);
  }

  /**
   * @param reviewdataComp
   */
  private void createMethodUIControls(final Composite reviewdataComp) {
    // create ready for series controls
    String labelName;
    try {
      labelName = new CommonDataBO().getMessage("CDR_RULE", "READY_FOR_SERIES");
      createLblControl(reviewdataComp, labelName, "");
      this.txtMethodValue = createStyledText(reviewdataComp);
      this.txtMethodValue.setEditable(false);
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }


  }

  /**
   * @param reviewdataComp
   */
  private void createHintUIControls(final Composite reviewdataComp) {
    // create hint controls
    createLblControl(reviewdataComp, CommonUIConstants.HINT_VALUE, "");
    // ICDM-759
    this.txtHintValue = new StyledText(reviewdataComp,
        SWT.MULTI | SWT.BORDER | SWT.SCROLL_LINE | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    final GridData gridData = new GridData();
    gridData.horizontalSpan = TAB_VIEWER_HOR_SPAN;
    gridData.heightHint = HINT_LABEL_HEIGHT;
    gridData.widthHint = HINT_LABEL_WIDTH;
    gridData.grabExcessHorizontalSpace = true;
    this.txtHintValue.setLayoutData(gridData);
    this.txtHintValue.setEditable(false);


  }

  /**
   * @param reviewdataComp
   */
  private void createRefValUIControls(final Composite reviewdataComp) {
    // create ref value controls
    createLblControl(reviewdataComp, CommonUIConstants.REF_VALUE, "");
    this.txtRefValue = createStyledText(reviewdataComp);
    this.txtRefValue.setEditable(false);
    createRefValBtnControl(reviewdataComp);
    /**
     * Add drag listener
     */
    int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
    final DragSource source = new DragSource(this.txtRefValue, operations);
    source.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer() });
    source.addDragListener(new DragSourceListener() {

      @Override
      public void dragStart(final DragSourceEvent event) {
        String value = ReviewDataViewPart.this.txtRefValue.getText();
        if (("".equals(value) || "n.a.".equalsIgnoreCase(value)) || value.endsWith("%")) {
          event.doit = false;
        }
        else {
          event.doit = true;
        }
      }

      @Override
      public void dragSetData(final DragSourceEvent event) {

        CalData calData = getRefValForRule(ReviewDataViewPart.this.selectedRule);
        if (null != calData) {
          // iCDM-2071

          try {
            CalData calDataObject = CalDataUtil.getCalDataHistoryDetails(new CurrentUserBO().getUserName(), calData,
                "Reference Value", ReviewDataViewPart.this.selectedRule, null);
            event.data = calDataObject;
            final SeriesStatisticsInfo calDataProvider = new SeriesStatisticsInfo(calDataObject, CalDataType.REF_VALUE);
            calDataProvider.setDataSetName(ReviewDataViewPart.this.selectedRule.getDependenciesForDisplay());
            StructuredSelection structuredSelection = new StructuredSelection(calDataProvider);
            LocalSelectionTransfer.getTransfer().setSelection(structuredSelection);
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
          }
        }
      }

      @Override
      public void dragFinished(final DragSourceEvent event) {
        // TODO:
      }
    });


  }

  /**
   * @param reviewdataComp
   */
  private void createUnitUIControls(final Composite reviewdataComp) {
    // create UNIT controls
    createLblControl(reviewdataComp, CommonUIConstants.UNIT_VALUE, "");
    this.txtUnitValue = createStyledText(reviewdataComp);
    this.txtUnitValue.setEditable(false);

  }

  /**
   * @param reviewdataComp
   */
  private void createUpperLimitUIControls(final Composite reviewdataComp) {
    // create upper limit controls
    createLblControl(reviewdataComp, CommonUIConstants.UPPER_LIMIT_VALUE, "");
    this.txtUpperLmtValue = createStyledText(reviewdataComp);
    this.txtUpperLmtValue.setEditable(false);

  }

  /**
   * @param reviewdataComp
   */
  private void createLowerLimitUIControls(final Composite reviewdataComp) {
    // create lower limit controls
    createLblControl(reviewdataComp, CommonUIConstants.LOWER_LIMIT_VALUE, "");
    this.txtLowerLmtValue = createStyledText(reviewdataComp);
    this.txtLowerLmtValue.setEditable(false);

  }

  /**
   * @param reviewdataComp
   */
  private StyledText createStyledText(final Composite reviewdataComp) {
    final StyledText styledTxt = new StyledText(reviewdataComp, SWT.SINGLE | SWT.BORDER);
    styledTxt.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(TEXT_FIELD_WIDTHHINT));
    return styledTxt;
  }

  /**
   * This method create Label instance for review info values
   *
   * @param reviewdataComp
   * @param lblName
   */
  private void createLblControl(final Composite reviewdataComp, final String lblName, final String toolTip) {
    final Label createLabel = LabelUtil.getInstance().createLabel(this.toolkit, reviewdataComp, lblName);
    createLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    createLabel.setToolTipText(toolTip);
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
   * This method creates scrolled form
   *
   * @param parent
   */
  private void createScrolledForm(final Composite parent) {
    // Create form
    this.scrolledForm = this.toolkit.createForm(parent);
    // Set decorate form heading to form
    this.toolkit.decorateFormHeading(this.scrolledForm);
    // Set layout to scrolledForm
    final GridLayout gridLayout = new GridLayout();
    gridLayout.makeColumnsEqualWidth = false;
    gridLayout.numColumns = CommonUIConstants.COLUMN_INDEX_3;
    this.scrolledForm.getBody().setLayout(gridLayout);
    // Set grid layout data to scrolledForm
    this.scrolledForm.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    // Create a SASH form as main composite
    this.mainComposite = new SashForm(this.scrolledForm.getBody(), SWT.HORIZONTAL | SWT.H_SCROLL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(GridDataUtil.getInstance().getGridData());

  }


  /**
   * Fill the reivew details and statistical information
   *
   * @param rules list of rules
   * @param paramIds parameter id
   * @param paramName
   * @param varCodedParamName
   */
  public void fillUIControls(final List<ReviewRule> rules, final String paramName,
      final String varCodedParamName, Map<String, ReviewParamResponse> reviewDataResponse) {
    // initialize members
    initializeMembers();
    // set RULE combo values
    setComboValues(rules);
    // fill review data
    fillValuesReviewData();
    // get review statistics
    getReviewStatistics(paramName, reviewDataResponse);
    // clear if exception
    if (!this.exceptionOccured) {
      if (this.reviewResult == null) {
        // clear table
        this.reviewResultGridTableViewer.setInput("");
      }
      else {
        Set<ReviewResultTableData> reviewStatData =
            createReviewResultData(this.reviewResult, paramName, varCodedParamName);
        this.reviewResultGridTableViewer.setInput(reviewStatData);
      }
      this.reviewResultGridTableViewer.refresh();
      // always refresh pidc details
      this.pidcTreeTableView.setInput("");
      this.pidcTreeTableView.refresh();
    }

  }


  /**
   * Initialize before filling data in UI
   *
   * @param rules
   * @param paramIds
   */
  private void initializeMembers() {
    clearTableGraph();
    this.selectedRule = null;
    this.exceptionOccured = false;
    this.comboRule.removeAll();
    resetUIControls(true);
    // get previous maintained selection
    this.refBtn.setSelection(this.refBtn.getSelection());
  }


  /**
   * Method to resolve the Select Rule combo values
   *
   * @param rules
   */
  private void setComboValues(final List<ReviewRule> rules) {
    if (rules.size() == RULES_MIN_SIZE) {
      Object rule = rules.get(0);
      this.selectedRule = (ReviewRule) rule;
      if ((this.selectedRule.getDependenciesForDisplay() == null) ||
          this.selectedRule.getDependenciesForDisplay().trim().isEmpty()) {
        this.comboRule.add(TAG_SELECT_A_RULE);
        this.comboRule.select(this.comboRule.indexOf(TAG_SELECT_A_RULE));
      }
      else {
        this.comboRule.add(this.selectedRule.getDependenciesForDisplay());
        this.comboRule.select(this.comboRule.indexOf(this.selectedRule.getDependenciesForDisplay()));
      }
      this.comboRule.setEnabled(false);
    }
    else {
      this.comboRule.add(TAG_SELECT_A_RULE);
      for (Object rule : rules) {
        ReviewRule cdrRule = (ReviewRule) rule;
        if (CommonUtils.isNotNull(cdrRule.getDependenciesForDisplay()) &&
            !cdrRule.getDependenciesForDisplay().trim().isEmpty()) {
          this.depRuleMap.put(cdrRule.getDependenciesForDisplay(), cdrRule);
          this.comboRule.add(cdrRule.getDependenciesForDisplay());
        }
        else {
          String depStr = getAttrValString(cdrRule.getDependencyList());
          this.depRuleMap.put(depStr, cdrRule);
          this.comboRule.add(depStr);
        }
      }
      this.comboRule.select(0);
      this.comboRule.setEnabled(true);
    }
  }


  /**
   * @param result
   * @param attrValSet
   * @param depen
   * @return
   */
  private static String getAttrValString(final SortedSet<AttributeValueModel> attrValSet) {
    String result = "";
    StringBuilder depen = new StringBuilder();
    for (AttributeValueModel attrVal : attrValSet) {
      // iCDM-1317
      depen.append(attrVal.getAttr().getName()).append("  --> ").append(attrVal.getAttr().getName()).append("  ;  ");
    }
    if (!CommonUtils.isEmptyString(depen.toString())) {
      result = depen.substring(0, depen.length() - 4).trim();
    }
    return result;
  }


  /**
   * Fill values in review data
   */
  private void fillValuesReviewData() {
    if (CommonUtils.isNotNull(this.selectedRule)) {
      if (!this.txtLowerLmtValue.isDisposed() && (this.selectedRule.getLowerLimit() != null)) {
        this.txtLowerLmtValue.setText(this.selectedRule.getLowerLimit().toString());
      }
      if (!this.txtMethodValue.isDisposed() && (this.selectedRule.getReviewMethod() != null)) {
        if (ApicConstants.READY_FOR_SERIES.YES.dbType.equals(this.selectedRule.getReviewMethod())) {
          this.txtMethodValue.setText(ApicConstants.READY_FOR_SERIES.YES.uiType);
        }
        else if (ApicConstants.READY_FOR_SERIES.NO.dbType.equals(this.selectedRule.getReviewMethod())) {
          this.txtMethodValue.setText(ApicConstants.READY_FOR_SERIES.NO.uiType);
        }
      }
      if (!this.txtUpperLmtValue.isDisposed() && (this.selectedRule.getUpperLimit() != null)) {
        this.txtUpperLmtValue.setText(this.selectedRule.getUpperLimit().toString());
      }
      if (!this.txtUnitValue.isDisposed() && (this.selectedRule.getUnit() != null)) {
        this.txtUnitValue.setText(this.selectedRule.getUnit());
      }
      if (!this.txtRefValue.isDisposed() && (this.selectedRule.getRefValueDispString() != null)) {
        this.txtRefValue.setText(this.selectedRule.getRefValueDispString());
        // ICDM-1194
        RuleMaturityLevel maturityLevel = RuleMaturityLevel.getIcdmMaturityLvlEnumForSsdText(this.selectedRule.getMaturityLevel());
        this.txtRefValue
            .setBackground(com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils.getMaturityLevelColor(maturityLevel));
        this.txtRefValue.setToolTipText(maturityLevel.getICDMMaturityLevel());
      }
      if (!this.txtHintValue.isDisposed() && (this.selectedRule.getHint() != null)) {
        this.txtHintValue.setText(this.selectedRule.getHint());
      }
      if (this.refBtn.getSelection()) {


        shwGraph(getRefValForRule(this.selectedRule));
      }
      displayTableGraphCompare(this.calDataMap);
    }
  }


  /**
   * @param rule
   * @return
   * @throws ClassNotFoundException
   * @throws IOException
   */
  private CalData getRefValForRule(final ReviewRule rule) {
    CalData caldataObj = null;
    try {
      caldataObj = com.bosch.caltool.icdm.common.util.CalDataUtil.getCalDataObj(rule.getRefValueCalData());
    }
    catch (ClassNotFoundException | IOException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    return caldataObj;
  }

  /**
   * Reference value controls
   *
   * @param caldataComp
   */
  private void createRefValBtnControl(final Composite caldataComp) {
    this.refBtn = new Button(caldataComp, SWT.TOGGLE);
    this.refBtn.setToolTipText(GRAPH_TOOL_TIP);
    this.refBtn.setImage(this.graphBtnImage);
    this.refBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent exception) {
        if (ReviewDataViewPart.this.refBtn.getSelection()) {
          if (ReviewDataViewPart.this.graphCmpCount < GRAPH_CNT_MAX) {
            if (CommonUtils.isNotNull(ReviewDataViewPart.this.selectedRule)) {
              ReviewDataViewPart.this.shwGraph(getRefValForRule(ReviewDataViewPart.this.selectedRule));
            }
          }
          else {
            ReviewDataViewPart.this.refBtn.setSelection(false);
            showGraphClearMessage();
          }
        }
        else {
          ReviewDataViewPart.this.calDataMap.remove(CommonUIConstants.REF_VALUE);
          ReviewDataViewPart.this.graphCmpCount--;
        }
        displayTableGraphCompare(ReviewDataViewPart.this.calDataMap);
      }
    });
  }

  /**
   * Show graph
   *
   * @param calData
   */
  private void shwGraph(final CalData calData) {
    if (calData != null) {
      Map<CalData, Integer> calDtList = new ConcurrentHashMap<CalData, Integer>();
      calDtList.put(calData, 0);
      if (this.calDataMap.get(CommonUIConstants.REF_VALUE) == null) {
        this.graphCmpCount++;
      }
      this.calDataMap.put(CommonUIConstants.REF_VALUE, calDtList);
    }
  }

  /**
   * Display the graph for selected dataset
   *
   * @param graphViewerCol
   * @param arg0
   * @param calDataPhy
   * @param calDataObj
   * @param dataSetCount2
   */
  private void shwDataSetGraph(final GridViewerColumn graphViewerCol, final Object arg0, final CalData calDataObj,
      final Integer dataSetCount2) {
    if (this.graphCmpCount < GRAPH_CNT_MAX) {
      this.dataSetCalDt.put(calDataObj, dataSetCount2);
      this.calDataMap.put(CHECK_VAL_STR, this.dataSetCalDt);
      this.graphCmpCount++;
      displayTableGraphCompare(this.calDataMap);
    }
    else {
      graphViewerCol.getViewer().update(arg0, null);
      showGraphClearMessage();
    }
  }

  private void showGraphClearMessage() {
    CDMLogger.getInstance().infoDialog(
        "Maximum limit for graph comparison reached.Please unselect to compare new values", Activator.PLUGIN_ID);

  }

  /**
   * clear the graph for selected dataset
   *
   * @param calDataPhy
   * @param calDataObj
   * @param dataSetCount2
   */
  private void clearDataSetGraph(final CalData calDataObj) {
    Map<CalData, Integer> dataSetCalData = this.calDataMap.get(CHECK_VAL_STR);
    if (dataSetCalData != null) {
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
      if (!dataSetCalData.isEmpty()) {
        this.calDataMap.put(CHECK_VAL_STR, this.dataSetCalDt);
      }
      else {
        this.calDataMap.remove(CHECK_VAL_STR);
      }
      this.graphCmpCount--;
      displayTableGraphCompare(this.calDataMap);
    }
  }


  /**
   * This method creates review information UI form section
   *
   * @return Section
   */
  private Section createReviewdataSection() {
    // Creates review info Section
    return createSection(CommonUIConstants.RULE_DATA_TITLE, false);
  }

  @Override
  public void setFocus() {
    // TODO Auto-generated method stub

  }

  /**
   * clears the UI data
   */
  public void resetUIControls(final boolean clearGraph) {


    if (!this.txtLowerLmtValue.isDisposed()) {
      this.txtLowerLmtValue.setText("");
    }
    if (!this.txtMethodValue.isDisposed()) {
      this.txtMethodValue.setText("");
    }
    if (!this.txtUpperLmtValue.isDisposed()) {
      this.txtUpperLmtValue.setText("");
    }
    if (!this.txtUnitValue.isDisposed()) {
      this.txtUnitValue.setText("");
    }
    if (!this.txtRefValue.isDisposed()) {
      this.txtRefValue.setText("");
      this.txtRefValue.setBackground(GUIHelper.getColor(COLOR_INDEX_WHITE, COLOR_INDEX_WHITE, COLOR_INDEX_WHITE));
      this.txtRefValue.setToolTipText("");
    }
    if (!this.txtHintValue.isDisposed()) {
      this.txtHintValue.setText("");
    }
    if (clearGraph) {
      clearTableGraph();
    }

    if (this.refBtn.getSelection()) {
      if (CommonUtils.isNotNull(this.selectedRule)) {
        shwGraph(getRefValForRule(this.selectedRule));
      }
      else {
        this.calDataMap.remove(CommonUIConstants.REF_VALUE);
        this.graphCmpCount--;
        displayTableGraphCompare(this.calDataMap);
      }
    }
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
        // clear table graph component
        this.graphColMap.clear();
        this.calDataTableGraphComposite.clearTableGraph();
      }
      else {
        StringBuilder labelPrefix = new StringBuilder();
        Iterator<Entry<String, Map<CalData, Integer>>> calDtIterator = calDataMp.entrySet().iterator();
        createObjectsComparison(calComp, labelPrefix, calDtIterator);
        this.calDataTableGraphComposite.fillTableAndGraph(calComp);
      }
    }
    catch (CalDataTableGraphException calDataGraphExptn) {
      CDMLogger.getInstance().error("Text values not supported in Graph", calDataGraphExptn, Activator.PLUGIN_ID);
    }

  }


  /**
   * Create objects with graph color for comparison
   *
   * @param calComp
   * @param labelPrefix
   * @param calDtIterator
   */
  private void createObjectsComparison(final CalDataComparison calComp, final StringBuilder labelPrefix,
      final Iterator<Entry<String, Map<CalData, Integer>>> calDtIterator) {

    while (calDtIterator.hasNext()) {
      Entry<String, Map<CalData, Integer>> thisEntry = calDtIterator.next();
      Map<CalData, Integer> calDataList = thisEntry.getValue();
      Iterator<Entry<CalData, Integer>> dataSetValues = calDataList.entrySet().iterator();
      createCalDataAttributes(calComp, labelPrefix, thisEntry, dataSetValues);
    }
  }


  /**
   * @param calComp
   * @param labelPrefix
   * @param graphColor
   * @param thisEntry
   * @param dataSetValues
   * @return
   */
  private int createCalDataAttributes(final CalDataComparison calComp, final StringBuilder labelPrefix,
      final Entry<String, Map<CalData, Integer>> thisEntry, final Iterator<Entry<CalData, Integer>> dataSetValues) {
    int graphColor = 0;
    while (dataSetValues.hasNext()) {
      labelPrefix.append(thisEntry.getKey());
      Entry<CalData, Integer> selDataSet = dataSetValues.next();
      CalData calData = selDataSet.getKey();
      if (selDataSet.getValue() != 0) {
        // Icdm-674 create the Serial Index Column for the series statistics view part table
        labelPrefix.append(" Id :");
        labelPrefix.append(" ");
        labelPrefix.append(selDataSet.getValue());
      }
      // ICDM-1037 - Label color Changes
      CalDataAttributes calAttr;
      // mapKey has the entry key
      String mapKey = thisEntry.getKey();
      if (CHECK_VAL_STR.equals(mapKey)) {
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
      labelPrefix.setLength(0);
      calComp.addCalDataAttr(calAttr);

    }
    return graphColor;
  }


  /**
   * Clear table and graph
   */
  public void clearTableGraph() {
    this.dataSetCalDt.clear();
    this.calDataMap.clear();
    this.graphCmpCount = 0;
    this.calDataTableGraphComposite.clearTableGraph();
  }


  /**
   * @return the scrolledForm
   */
  public Form getScrolledForm() {
    return this.scrolledForm;
  }

}
