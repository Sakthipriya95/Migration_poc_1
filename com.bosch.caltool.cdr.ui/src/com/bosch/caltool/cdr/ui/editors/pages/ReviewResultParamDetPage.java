/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldatacomparison.CalDataAttributes;
import com.bosch.calmodel.caldatacomparison.CalDataComparison;
import com.bosch.calmodel.caldataphyutils.CalDataTableGraphComposite;
import com.bosch.calmodel.caldataphyutils.exception.CalDataTableGraphException;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.ReviewParamToolBarActionSet;
import com.bosch.caltool.cdr.ui.actions.ReviewResultNATActionSet;
import com.bosch.caltool.cdr.ui.actions.ReviewResultToolBarActionSet;
import com.bosch.caltool.cdr.ui.dialogs.CommentDialog;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditor;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditorInput;
import com.bosch.caltool.cdr.ui.sorters.RvwResultParameterSorter;
import com.bosch.caltool.cdr.ui.table.filters.CdrTreeViewerFilter;
import com.bosch.caltool.cdr.ui.table.filters.ReviewResultToolBarFilters;
import com.bosch.caltool.cdr.ui.views.providers.ReviewDetPageColumnLabelProvider;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.client.bo.cdr.DataReviewScoreUtil;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultBO;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.actions.CDMCommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.utils.ParamTypeColor;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewActionSet;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleActionSet;
import com.bosch.caltool.icdm.ruleseditor.table.filters.ReviewResultParameterFilter;
import com.bosch.caltool.icdm.ruleseditor.utils.Messages;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRResultParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwFileServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * Second page of ReviewResult editor
 *
 * @author mkl2cob Icdm-543
 */
public class ReviewResultParamDetPage extends AbstractFormPage implements ISelectionListener {

  /**
   *
   */
  private static final String DISPLAY_GRAPH_AND_TABLE = "Display graph and table";
  /**
   *
   */
  private static final int TWO_EMPTY_FILLERS = 2;

  private static final int THREE_EMPTY_FILTERS = 3;
  /**
   * Filler label limit
   */
  private static final int FILLER_LABEL_LIMIT = 4;
  /**
   * Editor instance
   */
  private final ReviewResultEditor editor;
  private FormToolkit formToolkit;
  private Form nonScrollableForm;
  private SashForm mainComposite;
  private Composite compositeOne;
  private Composite compositeTwo;
  private Composite graphComp;

  private Section sectionParamProp;
  private Form formParamProperties;
  private Form formAssessment;
  private Button refButton;
  private Section sectionLeft;
  private Form leftForm;
  private StyledText hintTxtArea;
  private Button codeWordChkBox;
  private Button bitwiseChkBox;
  private StyledText nameTxt;
  private StyledText longNameTxt;

  // Icdm-502 Tool bar Action Set and Filter
  private ReviewParamToolBarActionSet toolBarActionSet;
  private Parameter selectedParam;
  private StyledText lowerLimitTxt;
  private StyledText upperLimitTxt;
  private StyledText refValueTxt;
  private Button manual;
  private Button automatic;
  private Button chkValButton;


  private TreeViewer viewer;

  /**
   * Return code for Ok pressed
   */
  private static final int CODE_FOR_OK = 0;


  /**
   * browse button image
   */
  private Image browseButtonImage;

  // ICDM 599 Toolbar filters for tree view
  private ReviewResultToolBarFilters toolBarFilters;

  /** Table Graph Composite instance */
  private CalDataTableGraphComposite tableGraphComp;

  /** Map which holds the listener(SWT.Activate) instance for the Text */
  private final Map<org.eclipse.swt.widgets.Scrollable, Map<Integer, Listener>> textListenerMap = new HashMap<>();

  private TextBoxContentDisplay commentTxt;
  /**
   * Rvw comment text size in the dialg
   */
  private static final int RVW_COMMENT_TEXT_SIZE = 4000;


  private StyledText resultTxt;
  private StyledText chkValText;
  private Combo scoreComboBox;
  private StyledText parChkValText;

  private CDRResultParameter cdrResultParameter;

  private final DataReviewScoreUtil dataRvwUtilObj;
  /**
   * unit value
   */
  protected String selectedUnit;
  private StyledText classTxt;
  private StyledText unitRefTxt;
  private StyledText unitChkTxt;
  private final CDRReviewResult cdrResult;
  private Button readyForSeries;

  private Composite reviewDataComp;
  private Composite ruleDetComp;
  private Composite assesmentSecComposite;
  private Group groupOneComp;

  private GridTableViewer filesTabViewer;

  private Button addButton;
  private Button deleteButton;

  private List<RvwFile> selectedIcdmFile = new ArrayList<>();
  private ScrolledComposite scrollComp;
  private ScrolledComposite assesmentSecScrollComp;
  private ScrolledComposite tblGraphScrollComp;
  private Button downloadButton;
  private Button btnParamRefValMatch;
  // IcDm-660
  private SashForm rvwSashForm;
  private SashForm rightSectSash;

  /**
   * ICDM-1186 edit button
   */
  private Button edit;
  private ToolBarManager toolBarManager;
  private Button parChkValButton;

  // ICDM-1320
  private final Map<CalData, ParamTypeColor> calDataColorMap = new LinkedHashMap<>();
  private StyledText parRefValText;


  private Button parRefValButton;
  private StyledText bitwiseLimitTxt;
  /**
   * Defines text field widthhint
   */
  private static final int TEXT_FIELD_WIDTHHINT_1 = 120;

  private static final int TEXT_FIELD_WIDTHHINT_2 = 350;

  private static final int TEXT_AREA_WIDTHHINT = 400;

  private static final int TEXT_AREA_HEIGHTHINT = 50;

  private CustomGridTableViewer paramTableViewer;

  /**
   * Param name col width
   */
  private static final int NAME_COL_WIDTH = 300;
  /**
   * Param type col width
   */
  private static final int TYPE_COL_WIDTH = 25;
  private static final int INDICATOR_COL_WIDTH = 40;

  private RvwResultParameterSorter paramTabSorter;

  private Text filterTxt;

  private ReviewResultParameterFilter resultParameterFilter;

  private final ParameterDataProvider paramData;

  private CdrTreeViewerFilter treeViewerFilter;

  private final ReviewResultClientBO resultData;
  private List<DATA_REVIEW_SCORE> scoreEnums;

  /**
   * @param editor ReviewResultEditor
   * @param id Id
   * @param title title
   */
  public ReviewResultParamDetPage(final ReviewResultEditor editor) {
    super(editor, null, Messages.getString("ParamDetailsPage.label")); //$NON-NLS-1$
    this.editor = editor;
    this.cdrResult = this.editor.getEditorInput().getReviewResult();
    this.resultData = this.editor.getEditorInput().getResultData();
    this.paramData = new ParameterDataProvider(null);
    this.dataRvwUtilObj = DataReviewScoreUtil.getInstance();
  }

  /**
   * @return the viewer
   */
  public TreeViewer getViewer() {
    return this.viewer;
  }

  /**
   * @return the comment text box to the Tool bar action set
   */
  public TextBoxContentDisplay getCommentTxt() {
    return this.commentTxt;
  }

  /**
   * @return the parChkValText
   */
  public StyledText getParChkValText() {
    return this.parChkValText;
  }


  /**
   * @return the Reviewed Check box to the Tool bar action set
   */
  public Combo getReviewedComboBox() {
    return this.scoreComboBox;
  }

  /**
   * @return the parRefValText
   */
  public StyledText getParRefValText() {
    return this.parRefValText;
  }

  /**
   * @return the Cdr Result Parameter
   */
  public CDRResultParameter getCdrResultParameter() {
    return this.cdrResultParameter;
  }

  @Override
  public void createPartControl(final Composite parent) {

    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.setText("Parameter Details");
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;

    this.browseButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.COMPARE_GRAPH_16X16);

    this.mainComposite = new SashForm(this.nonScrollableForm.getBody(), SWT.HORIZONTAL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(gridData);

    final ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);

    enableDisableReviewFields(false);
    enableDisableReviewResultFields(false);
  }

  @Override
  public Control getPartControl() {
    // set the decimal preference for the table/graph
    if (CommonUtils.isNotNull(this.editor)) {
      this.editor.setDecimalPref();
    }
    if (CommonUtils.isNotNull(this.tableGraphComp)) {
      this.tableGraphComp.clearTableGraph();
    }

    return this.nonScrollableForm;
  }

  /**
   *
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    createBottomComposites();
  }

  /**
   * If no parameter is selected then clear the fields
   */
  private void clearAllFields() {
    this.cdrResultParameter = null;
    this.nameTxt.setText("");
    this.longNameTxt.setText("");
    this.codeWordChkBox.setSelection(false);
    this.bitwiseChkBox.setSelection(false);
    this.lowerLimitTxt.setText("");
    this.upperLimitTxt.setText("");
    this.refValueTxt.setText("");
    this.btnParamRefValMatch.setSelection(false);
    this.hintTxtArea.setText("");
    enableDisableReviewFields(false);

    this.codeWordChkBox.setEnabled(false);
    this.chkValText.setText("");
    if (null != this.parChkValText) {
      this.parChkValText.setText("");
    }
    if (null != this.parRefValText) {
      this.parRefValText.setText("");
    }
    this.classTxt.setText("");
    this.unitRefTxt.setText("");
    this.unitChkTxt.setText("");
    this.hintTxtArea.setEditable(false);
    this.resultTxt.setText("");
    this.scoreComboBox.setText(this.dataRvwUtilObj.getScoreDisplayExt(DATA_REVIEW_SCORE.S_0));
    this.commentTxt.getText().setText("");
    enableDisableReviewResultFields(false);
    this.readyForSeries.setSelection(false);
    // ICDM-1096
    ReviewResultParamDetPage.this.tableGraphComp.clearTableGraph();

  }

  /**
   * method to enable/disable review result fields in parameter details page
   */
  private void enableDisableReviewResultFields(final boolean enable) {
    this.edit.setEnabled(enable);
    this.commentTxt.setEnabled(enable);
    this.scoreComboBox.setEnabled(enable);
  }

  /**
   * This method initializes composite
   */
  private void createBottomComposites() {

    createLeftComposite();

    createRightComposite();

    this.mainComposite.setWeights(new int[] { 1, 5 });
  }


  /**
   * This method initializes compositeOne
   */
  private void createLeftComposite() {

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.compositeOne = new Composite(this.mainComposite, SWT.NONE);

    final GridLayout gridLayout = new GridLayout();

    createLeftSection(this.formToolkit);

    this.compositeOne.setLayout(gridLayout);
    this.compositeOne.setLayoutData(gridData);

  }

  /**
   * This method initializes CompositeTwo
   */
  private void createRightComposite() {

    this.compositeTwo = new Composite(this.mainComposite, SWT.NONE);
    this.compositeTwo.setLayout(new GridLayout());
    this.compositeTwo.setLayoutData(GridDataUtil.getInstance().getGridData());

    createSections();
  }

  /**
   * @param toolkit This method initializes sectionTwo
   * @param gridLayout
   */
  private void createLeftSection(final FormToolkit toolkit) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.sectionLeft = toolkit.createSection(this.compositeOne, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionLeft.setText("Parameters");
    this.sectionLeft.setExpanded(true);
    this.sectionLeft.getDescriptionControl().setEnabled(false);
    createLeftFormTable(toolkit);
    this.sectionLeft.setLayoutData(gridData);
    this.sectionLeft.setClient(this.leftForm);
  }


  private void createLeftFormTable(final FormToolkit toolkit) {
    final GridData gridData = new GridData();
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    this.leftForm = toolkit.createForm(this.sectionLeft);

    this.leftForm.getBody().setLayout(new GridLayout());

    this.filterTxt = toolkit.createText(this.leftForm.getBody(), null, SWT.SINGLE | SWT.BORDER);

    createFilterTxt();

    final GridLayout gridLayout = new GridLayout();

    this.paramTabSorter = new RvwResultParameterSorter();

    this.paramTableViewer = new CustomGridTableViewer(this.leftForm.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);


    initializeEditorStatusLineManager(this.paramTableViewer);
    this.paramTableViewer.getGrid().setLayoutData(gridData);

    this.paramTableViewer.getGrid().setLinesVisible(true);
    this.paramTableViewer.getGrid().setHeaderVisible(true);

    this.leftForm.getBody().setLayout(gridLayout);
    createTabColumns();

    this.paramTableViewer.setContentProvider(ArrayContentProvider.getInstance());

    paramTableListeners();
    invokeColumnSorter();

    this.toolBarActionSet =
        new ReviewParamToolBarActionSet(getEditorSite().getActionBars().getStatusLineManager(), this);


    this.paramTableViewer.setInput(this.resultData.getResultBo().getParameters());

    // adding context menu
    // Icdm-543
    addRightClickMenu();

    // ICDM-1348
    com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils.getInstance()
        .addKeyListenerToCopyNameFromViewer(this.paramTableViewer);
    final ReviewResultEditor reviewResultEditor = (ReviewResultEditor) getEditor();

    addParamTableFilters(reviewResultEditor);

    final SelectionProviderMediator selectionProviderMediator = reviewResultEditor.getSelectionProviderMediator();
    selectionProviderMediator.addViewer(this.paramTableViewer);
    getSite().setSelectionProvider(selectionProviderMediator);

    getSite().getPage().addSelectionListener(this);

  }

  /**
   * @param reviewResultEditor
   */
  private void addParamTableFilters(final ReviewResultEditor reviewResultEditor) {
    // ICDM 599
    this.resultParameterFilter = new ReviewResultParameterFilter();
    this.paramTableViewer.addFilter(this.resultParameterFilter);


    this.toolBarFilters = new ReviewResultToolBarFilters(reviewResultEditor.getEditorInput().getResultData());
    this.paramTableViewer.addFilter(this.toolBarFilters);

    // Added outline filter
    this.treeViewerFilter = new CdrTreeViewerFilter(getResultData(), getDataHandler(), null);
    this.paramTableViewer.addFilter(this.treeViewerFilter);
  }

  /**
   *
   */
  private void paramTableListeners() {
    this.paramTableViewer.addSelectionChangedListener(event -> {
      final IStructuredSelection selection =
          (IStructuredSelection) ReviewResultParamDetPage.this.paramTableViewer.getSelection();
      if ((selection != null) && (selection.getFirstElement() != null)) {
        ReviewResultParamDetPage.this.cdrResultParameter = null;
        if (selection.getFirstElement() instanceof CDRResultParameter) {
          ReviewResultParamDetPage.this.calDataColorMap.clear();
          setDetails((CDRResultParameter) selection.getFirstElement());
          // Icdm-548
          setSelectedParam();
          enableRvwRsltFields();
          setContentForFilesTab((CDRResultParameter) selection.getFirstElement());
          setGraphTable();
        }
        else {
          clearAllFields();
          // Icdm-548
          ReviewResultParamDetPage.this.editor.getEditorInput().setResultFunction(null);
          ReviewResultParamDetPage.this.editor.getEditorInput().setCdrReviewResultParam(null);
          setContentForFilesTab(null);
        }
      }
    });


    this.paramTableViewer.getGrid().addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent fLost) {
        // No Implemantation
      }

      @Override
      public void focusGained(final FocusEvent fGained) {
        setStatusBarMessage(ReviewResultParamDetPage.this.paramTableViewer);
      }
    });
  }

  /**
   *
   */
  private void enableRvwRsltFields() {
    if (!ReviewResultParamDetPage.this.resultData.getResultBo().isResultLocked()) {
      if (ReviewResultParamDetPage.this.resultData.getResultBo().isModifiable()) {
        enableDisableReviewResultFields(true);
      }
      if (ReviewResultParamDetPage.this.resultData.getResultBo().canDownloadFiles()) {
        ReviewResultParamDetPage.this.addButton.setEnabled(true);
      }
    }
  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    final GridData gridData = getFilterTxtGridData();
    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      final String text = ReviewResultParamDetPage.this.filterTxt.getText().trim();
      ReviewResultParamDetPage.this.resultParameterFilter.setFilterText(text);
      ReviewResultParamDetPage.this.paramTableViewer.refresh();
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
   * Add sorter for the table columns
   */
  private void invokeColumnSorter() {
    this.paramTableViewer.setComparator(this.paramTabSorter);
  }


  /**
   * @param selection
   */
  private void filterOutlineSelection(final ISelection selection) {
    if (!(((IStructuredSelection) selection).getFirstElement() instanceof CDRResultParameter)) /* ICDM 599 */ {
      this.treeViewerFilter.treeSelectionListener(selection);
      if (!this.paramTableViewer.getGrid().isDisposed()) {
        this.paramTableViewer.refresh();
        Collection<?> items = (Collection<?>) this.paramTableViewer.getInput();
        int totalItemCount = items.size();
        int filteredItemCount = this.paramTableViewer.getGrid().getItemCount();
        this.editor.updateStatusBar(true, totalItemCount, filteredItemCount);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if ((null != getSite().getPage().getActiveEditor()) &&
        (getSite().getPage().getActiveEditor().equals(getEditor())) && (part instanceof OutlineViewPart)) {
      filterOutlineSelection(selection);
    }
  }

  /**
   * Defines the columns of the TableViewer
   */
  private void createTabColumns() {

    // ICDM-2439
    final GridViewerColumn ssdClassColumn = new GridViewerColumn(this.paramTableViewer, SWT.NONE);
    ssdClassColumn.getColumn().setText("");
    ssdClassColumn.getColumn().setWidth(INDICATOR_COL_WIDTH);
    ColumnViewerToolTipSupport.enableFor(this.paramTableViewer, ToolTip.NO_RECREATE);
    ssdClassColumn
        .setLabelProvider(new ReviewDetPageColumnLabelProvider(this.resultData, CommonUIConstants.COLUMN_INDEX_0) {

          /**
           * {@inheritDoc}
           */
          @Override
          public String getToolTipText(final Object element) {

            if (element instanceof CDRResultParameter) {
              CDRResultParameter resultParameter = (CDRResultParameter) element;
              StringBuilder toolTipText = new StringBuilder();
              appendToolTipText(resultParameter, toolTipText);
              if (toolTipText.length() > 0) {
                return toolTipText.substring(0, toolTipText.length() - 1);
              }
            }
            return "";
          }


        });

    final GridViewerColumn typeColumn = new GridViewerColumn(this.paramTableViewer, SWT.NONE);
    typeColumn.getColumn().setText("");
    typeColumn.getColumn().setWidth(TYPE_COL_WIDTH);
    typeColumn
        .setLabelProvider(new ReviewDetPageColumnLabelProvider(this.resultData, CommonUIConstants.COLUMN_INDEX_1));
    typeColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(typeColumn.getColumn(), 1, this.paramTabSorter, this.paramTableViewer));

    final GridViewerColumn paramNameColumn = new GridViewerColumn(this.paramTableViewer, SWT.NONE);
    paramNameColumn.getColumn().setText("Parameter");
    paramNameColumn.getColumn().setWidth(NAME_COL_WIDTH);
    paramNameColumn
        .setLabelProvider(new ReviewDetPageColumnLabelProvider(this.resultData, CommonUIConstants.COLUMN_INDEX_2) {

          /**
           * {@inheritDoc}
           */
          @Override

          public String getToolTipText(final Object element) {
            return "FC Name: " + ((CDRResultParameter) element).getFuncName() + "\n" + "Long Name: " +
                ((CDRResultParameter) element).getDescription();

          }
        }


        );

    // Add column selection listener
    paramNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(paramNameColumn.getColumn(), 1, this.paramTabSorter, this.paramTableViewer));


  }

  /**
   * @param resultParameter
   * @param toolTipText
   */
  private void appendToolTipText(final CDRResultParameter resultParameter, final StringBuilder toolTipText) {
    if (ReviewResultParamDetPage.this.resultData.isComplianceParameter(resultParameter)) {
      toolTipText.append(ApicConstants.COMPLIANCE_PARAM).append("\n");
    }
    if (ReviewResultParamDetPage.this.resultData.isReadOnly(resultParameter)) {
      toolTipText.append(ApicConstants.READ_ONLY_PARAM).append("\n");
    }
    if (ReviewResultParamDetPage.this.resultData.isQssdParameter(resultParameter)) {
      toolTipText.append(ApicConstants.QSSD_PARAM).append("\n");
    }
    if (ReviewResultParamDetPage.this.resultData.isBlackList(resultParameter)) {
      CommonDataBO dataBo = new CommonDataBO();
      try {
        toolTipText.append(dataBo.getMessage(CDRConstants.PARAM, CDRConstants.BLACK_LIST_TOOLTIP)).append("\n");
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * @param cdrResultParam cdrResultParam
   */
  protected void setContentForFilesTab(final CDRResultParameter cdrResultParam) {
    if (cdrResultParam != null) {
      final SortedSet<RvwFile> attachments = this.resultData.getAttachments(cdrResultParam.getId());
      this.filesTabViewer.setInput(attachments);
    }
    else {
      this.filesTabViewer.setInput(null);
    }
  }

  private void addListener(final StyledText nameTxt2, final CalData calData) {

    // Listens the activation of the text fields
    final Listener activateListener = event -> {
      if (null != calData) {
        String value = nameTxt2.getText().trim();
        // Following is additional check, were the trigger for
        // displaying values is not applicable
        if (!(("".equals(value) || "n.a.".equalsIgnoreCase(value)) || "%".endsWith(value))) {
          nameTxt2.setSelection(0, value.length());
          nameTxt2.showSelection();
        }
      }
    };

    nameTxt2.addListener(SWT.CURSOR_APPSTARTING, activateListener);

    Map<Integer, Listener> listenerMap = this.textListenerMap.computeIfAbsent(nameTxt2, k -> new HashMap<>());

    listenerMap.put(SWT.CURSOR_APPSTARTING, activateListener);

    final Listener[] deActivateListeners = nameTxt2.getListeners(SWT.Deactivate);

    if (deActivateListeners.length == 0) {
      nameTxt2.addListener(SWT.Deactivate, event -> {
        // revert selection
        nameTxt2.setSelection(0);
        nameTxt2.showSelection();
      });
    }

  }

  private void removeListenerFromTextField(final StyledText txt) {
    final Map<Integer, Listener> listenerMap = this.textListenerMap.get(txt);
    if (listenerMap != null) {
      for (Entry<Integer, Listener> listenerMapEntry : listenerMap.entrySet()) {
        txt.removeListener(listenerMapEntry.getKey(), listenerMapEntry.getValue());
      }
    }
  }


  /**
   * set the selected parameter in the editor input
   */
  // Icdm-548
  protected void setSelectedParam() {
    final IStructuredSelection selection =
        (IStructuredSelection) ReviewResultParamDetPage.this.paramTableViewer.getSelection();
    if ((selection != null) && (selection.size() != 0)) {
      final Object element = selection.getFirstElement();
      if (element instanceof CDRResultParameter) {
        final CDRResultParameter selectedParameter = (CDRResultParameter) element;
        ReviewResultParamDetPage.this.editor.getEditorInput().setCdrReviewResultParam(selectedParameter);
        ReviewResultParamDetPage.this.editor.getEditorInput().setSelectedFunc(null);
      }
    }

  }

  /**
   * set the selected function in the editor input
   */
  protected void setSelectedFunction() {
    final IStructuredSelection selection = (IStructuredSelection) ReviewResultParamDetPage.this.viewer.getSelection();
    if ((selection != null) && (selection.size() != 0)) {
      final Object element = selection.getFirstElement();
      if (element instanceof CDRResultFunction) {
        final CDRResultFunction selectedFunc = (CDRResultFunction) element;
        ReviewResultParamDetPage.this.editor.getEditorInput().setSelectedFunc(selectedFunc);
        ReviewResultParamDetPage.this.editor.getEditorInput().setCdrReviewResultParam(null);
      }
    }

  }

  /**
   * Right click context menu for parameters
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {
      final IStructuredSelection selection =
          (IStructuredSelection) ReviewResultParamDetPage.this.paramTableViewer.getSelection();
      final Object firstElement = selection.getFirstElement();
      if ((firstElement != null) && (selection.size() != 0)) {
        final CommonActionSet cdrActionSet = new CommonActionSet();
        ReviewActionSet reviewActionSet = new ReviewActionSet();
        ReviewRuleActionSet reviewRuleActionSet = new ReviewRuleActionSet();
        if (firstElement instanceof CDRResultParameter) {
          // Icdm-521
          // Using the CDM common Action Set available in common UI
          final CDMCommonActionSet cdmActionSet = new CDMCommonActionSet();
          List<Object> selectedObj = new ArrayList<>();
          selectedObj.add(firstElement);

          // Add 'Create Rule using Check Value' context menu option
          new ReviewResultNATActionSet().addCreateRuleAction(menuMgr, firstElement,
              getResultData().getCDRFunction(getResultData().getFunction(this.cdrResultParameter)), true,
              getResultData().getResultBo().getRuleSet());
          mgr.add(new Separator());

          // Add Show Series statistics menu action
          // true - To enable action
          cdmActionSet.addShowSeriesStatisticsMenuAction(menuMgr, selectedObj, true);

          mgr.add(new Separator());
          // Icdm-697 Disable the action if more than one value is selected
          cdmActionSet.addShowReviewDataMenuAction(menuMgr, firstElement, true, null);

          reviewActionSet.addReviewParamEditor(menuMgr, firstElement, null, ReviewResultParamDetPage.this.resultData
              .getCDRFunction(ReviewResultParamDetPage.this.resultData.getFunction((CDRResultParameter) firstElement)),
              true);

          menuMgr.add(new Separator());

          // ICDM-2407

          CDRResultParameter resParam = (CDRResultParameter) firstElement;

          ParamCollection paramCol = ReviewResultParamDetPage.this.resultData
              .getCDRFunction(ReviewResultParamDetPage.this.resultData.getFunction(resParam));
          if (ReviewResultParamDetPage.this.resultData.getResultBo().getRuleSet() != null) {
            paramCol = ReviewResultParamDetPage.this.resultData.getResultBo().getRuleSet();
          }

          reviewRuleActionSet.showRuleHistoryFromResult(menuMgr, paramCol, resParam,
              IMessageConstants.OPEN_RULE_HISTORY, false, false);

          showCompliParamHistory(menuMgr, reviewRuleActionSet, resParam, paramCol);

          // ICDM-1348
          menuMgr.add(new Separator());
          cdrActionSet.copyParamNameToClipboardAction(menuMgr, ((CDRResultParameter) firstElement).getName());
        }
        if (firstElement instanceof CDRResultFunction) {
          reviewActionSet.addReviewParamEditor(menuMgr, firstElement, null,
              ReviewResultParamDetPage.this.resultData.getCDRFunction((CDRResultFunction) firstElement), true);
        }
      }

    });

    final Menu menu = menuMgr.createContextMenu(this.paramTableViewer.getControl());
    this.paramTableViewer.getControl().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.paramTableViewer);

  }

  /**
   * @param menuMgr
   * @param reviewRuleActionSet
   * @param resParam
   * @param paramCol
   */
  private void showCompliParamHistory(final MenuManager menuMgr, final ReviewRuleActionSet reviewRuleActionSet,
      final CDRResultParameter resParam, final ParamCollection paramCol) {
    if (ReviewResultParamDetPage.this.resultData.isComplianceParameter(resParam)) {
      reviewRuleActionSet.showRuleHistoryFromResult(menuMgr, paramCol, resParam,
          IMessageConstants.OPEN_COMPLI_RULE_HISTORY, true, false);
    }
  }


  /**
   * @param cdrResultParameter
   */
  private void setDetails(final CDRResultParameter cdrResultParameter) {

    this.cdrResultParameter = cdrResultParameter;
    this.selectedParam = this.resultData.getFunctionParameter(cdrResultParameter);

    ReviewResultParamDetPage.this.nameTxt
        .setText(this.selectedParam.getName() == null ? "" : this.selectedParam.getName());
    removeListenerFromTextField(this.nameTxt);
    addListener(this.nameTxt, null);

    ReviewResultParamDetPage.this.longNameTxt
        .setText(this.selectedParam.getLongName() == null ? "" : this.selectedParam.getLongName());

    removeListenerFromTextField(this.longNameTxt);
    addListener(this.longNameTxt, null);

    // If the user does not have write access on the function then set fields to non editable
    ReviewResultParamDetPage.this.codeWordChkBox
        .setSelection(ApicConstants.CODE_WORD_YES.equals(this.selectedParam.getCodeWord()));

    ReviewResultParamDetPage.this.bitwiseChkBox
        .setSelection(ApicConstants.CODE_WORD_YES.equals(this.paramData.getBitWiseRule(this.selectedParam)));


    this.classTxt.setText(this.selectedParam.getpClassText() == null ? "" : this.selectedParam.getpClassText());

    removeListenerFromTextField(this.classTxt);
    addListener(this.classTxt, null);

    setReviewResultDetails(cdrResultParameter);
    this.commentTxt.getText()
        .setText(cdrResultParameter.getRvwComment() == null ? "" : cdrResultParameter.getRvwComment());
    this.scoreComboBox.setText(this.dataRvwUtilObj.getScoreDisplayExt(this.resultData.getScore(cdrResultParameter)));

    setScoreComboToolTip(cdrResultParameter);
  }

  /**
   * Sets the tooltip text for score combo box in assessment section.
   *
   * @param cdrResultParameter
   */
  private void setScoreComboToolTip(final CDRResultParameter cdrResultParameter) {
    String modifiedToolTip;
    if (this.resultData.isScoreChanged(cdrResultParameter)) {
      modifiedToolTip = this.resultData.getScoreExtDescription(cdrResultParameter) + "\n" + "Parent Score : " +
          this.resultData.getParentScoreValueString(cdrResultParameter);
    }
    else {
      modifiedToolTip = this.resultData.getScoreExtDescription(cdrResultParameter);
    }
    this.scoreComboBox.setToolTipText(modifiedToolTip);
  }

  /**
   * @param cdrResultParameter
   */
  private void setReviewResultDetails(final CDRResultParameter cdrResultParameter) {
    setLowerUpperBitwiseLimits();
    setReferenceAndCheckValue();

    if (null != this.parChkValText) {
      this.parChkValText.setText(this.resultData.isCheckedValueChanged(cdrResultParameter)
          ? String.valueOf(
              this.editor.sliceNumericValue(this.resultData.getParentCheckedValString(this.cdrResultParameter)))
          : this.resultData.checkParentCheckVal(cdrResultParameter));
      this.parChkValText.setToolTipText(this.resultData.isCheckedValueChanged(cdrResultParameter)
          ? this.resultData.getParentCheckedValString(this.cdrResultParameter)
          : this.resultData.checkParentCheckVal(cdrResultParameter));
      removeListenerFromTextField(this.parChkValText);
      addListener(this.parChkValText, this.resultData.getParentCheckedVal(this.cdrResultParameter));
    }
    this.chkValText.setText(
        String.valueOf(this.editor.sliceNumericValue(this.resultData.getCheckedValueString(cdrResultParameter))));
    this.chkValText.setToolTipText(this.resultData.getCheckedValueString(cdrResultParameter));
    removeListenerFromTextField(this.chkValText);
    addListener(this.chkValText, this.resultData.getCheckedValueObj(cdrResultParameter));
    // ICDM 610
    this.chkValText.setBackground(Display.getCurrent().getSystemColor(
        this.resultData.isCheckedValueChanged(cdrResultParameter) ? SWT.COLOR_YELLOW : SWT.COLOR_WHITE));

    // 494306 - Defect fix - Validate score box in Parameter details page based on review type
    validateScoreEightInCombo();
    Collections.sort(this.scoreEnums);
    this.scoreComboBox.removeAll();
    for (DATA_REVIEW_SCORE score : this.scoreEnums) {
      this.scoreComboBox.add(this.dataRvwUtilObj.getScoreDisplayExt(score));
    }

    this.resultTxt.setText(CommonUtils.checkNull(this.resultData.getResult(cdrResultParameter)));

    // ICDM 610
    this.resultTxt.setBackground(Display.getCurrent()
        .getSystemColor(this.resultData.isResultChanged(cdrResultParameter) ? SWT.COLOR_YELLOW : SWT.COLOR_WHITE));


    removeListenerFromTextField(this.resultTxt);
    addListener(this.resultTxt, null);
    setReadyForSeriesDetails(cdrResultParameter);
    // for delta review add background colour if ready for series flag has changed
    setReadyForSeriesChngFlagBgColor(cdrResultParameter);
    StringBuilder hint = new StringBuilder();
    String paramHint = cdrResultParameter.getParamHint();
    String ruleHint = cdrResultParameter.getHint();
    if (!CommonUtils.isEmptyString(paramHint)) {
      hint.append(paramHint).append("\n\n");
    }
    if (!CommonUtils.isEmptyString(ruleHint)) {
      hint.append(ruleHint);
    }
    this.hintTxtArea.setText(hint.toString() == null ? "" : hint.toString());

    removeListenerFromTextField(this.hintTxtArea);
    addListener(this.hintTxtArea, null);

    this.btnParamRefValMatch.setSelection(this.resultData.isExactMatchRefValue(cdrResultParameter));
    // for delta review add background colour if exact match has changed
    setExactMatchChngFlagBgColor(cdrResultParameter);
  }

  private void setLowerUpperBitwiseLimits() {
    this.lowerLimitTxt.setText(this.cdrResultParameter.getLowerLimit() == null ? ""
        : String.valueOf(this.editor.sliceNumericValue(this.cdrResultParameter.getLowerLimit())));
    this.lowerLimitTxt.setToolTipText(
        this.cdrResultParameter.getLowerLimit() == null ? "" : this.cdrResultParameter.getLowerLimit().toString());
    // ICDM 610
    this.lowerLimitTxt.setBackground(Display.getCurrent().getSystemColor(
        this.resultData.isLowerLimitChanged(this.cdrResultParameter) ? SWT.COLOR_YELLOW : SWT.COLOR_WHITE));
    removeListenerFromTextField(this.lowerLimitTxt);
    addListener(this.lowerLimitTxt, null);
    this.upperLimitTxt.setText(this.cdrResultParameter.getUpperLimit() == null ? ""
        : String.valueOf(this.editor.sliceNumericValue(this.cdrResultParameter.getUpperLimit())));
    this.upperLimitTxt.setToolTipText(
        this.cdrResultParameter.getUpperLimit() == null ? "" : this.cdrResultParameter.getUpperLimit().toString());

    // ICDM 610
    this.upperLimitTxt.setBackground(Display.getCurrent().getSystemColor(
        this.resultData.isUpperLimitChanged(this.cdrResultParameter) ? SWT.COLOR_YELLOW : SWT.COLOR_WHITE));
    removeListenerFromTextField(this.upperLimitTxt);
    addListener(this.upperLimitTxt, null);
    this.bitwiseLimitTxt
        .setText(this.cdrResultParameter.getBitwiseLimit() == null ? "" : this.cdrResultParameter.getBitwiseLimit());
    this.bitwiseLimitTxt.setBackground(Display.getCurrent().getSystemColor(
        this.resultData.isBitwiseLimitChanged(this.cdrResultParameter) ? SWT.COLOR_YELLOW : SWT.COLOR_WHITE));
    removeListenerFromTextField(this.bitwiseLimitTxt);
    addListener(this.bitwiseLimitTxt, null);
  }

  private void setReferenceAndCheckValue() {
    // Icdm-513
    this.unitRefTxt.setText(this.cdrResultParameter.getRefUnit() == null ? "" : this.cdrResultParameter.getRefUnit());
    removeListenerFromTextField(this.unitRefTxt);
    addListener(this.unitRefTxt, null);
    this.unitChkTxt
        .setText(this.cdrResultParameter.getCheckUnit() == null ? "" : this.cdrResultParameter.getCheckUnit());
    removeListenerFromTextField(this.unitChkTxt);
    addListener(this.unitChkTxt, null);


    this.refValueTxt.setText(this.resultData.getRefValueString(this.cdrResultParameter) == null ? ""
        : String.valueOf(this.editor.sliceNumericValue(this.resultData.getRefValueString(this.cdrResultParameter))));
    this.refValueTxt.setToolTipText(this.resultData.getRefValueString(this.cdrResultParameter) == null ? ""
        : this.resultData.getRefValueString(this.cdrResultParameter));

    // ICDM-2563
    this.refValueTxt.setBackground(Display.getCurrent()
        .getSystemColor(this.resultData.isRefValChanged(this.cdrResultParameter) ? SWT.COLOR_YELLOW : SWT.COLOR_WHITE));

    removeListenerFromTextField(this.refValueTxt);
    addListener(this.refValueTxt, this.resultData.getRefValueObj(this.cdrResultParameter));
    // ICDM-1320
    addOrRemoveListenerParentRefVal(this.cdrResultParameter);
  }

  /**
   * @param cdrResultParameter
   */
  private void addOrRemoveListenerParentRefVal(final CDRResultParameter cdrResultParameter) {
    if (null != this.parRefValText) {
      this.parRefValText.setText(this.resultData.isRefValChanged(cdrResultParameter)
          ? String
              .valueOf(this.editor.sliceNumericValue(this.resultData.getParentRefValString(this.cdrResultParameter)))
          : this.resultData.checkParentRefVal(cdrResultParameter));
      this.parRefValText.setToolTipText(this.resultData.getParentRefValString(this.cdrResultParameter));
      removeListenerFromTextField(this.parRefValText);
      addListener(this.parRefValText, this.resultData.getParentRefVal(this.cdrResultParameter));
    }
  }

  /**
   *
   */
  private void validateScoreEightInCombo() {
    if (DATA_REVIEW_SCORE.S_8.getScoreDisplay().equals(
        ReviewResultParamDetPage.this.resultData.getScoreUIType(ReviewResultParamDetPage.this.cdrResultParameter))) {
      // ICDM-2403
      this.scoreEnums.add(DATA_REVIEW_SCORE.S_8);
    }
    else {
      this.scoreEnums.remove(DATA_REVIEW_SCORE.S_8);
    }
  }

  /**
   * @param cdrResultParameter
   */
  private void setReadyForSeriesDetails(final CDRResultParameter cdrResultParameter) {
    if ((this.resultData.getReadyForSeries(cdrResultParameter) != null) &&
        (this.resultData.getReadyForSeries(cdrResultParameter) == ApicConstants.READY_FOR_SERIES.NO)) {
      this.readyForSeries.setSelection(false);

    }
    else {
      this.readyForSeries.setSelection((this.resultData.getReadyForSeries(cdrResultParameter) != null) &&
          (this.resultData.getReadyForSeries(cdrResultParameter) == ApicConstants.READY_FOR_SERIES.YES));
    }
  }

  /**
   * Set Background colour as yellow if the ready for series(review method->automatic,manual) has changed in delta
   * review
   *
   * @param cdrResultParameter
   */
  private void setReadyForSeriesChngFlagBgColor(final CDRResultParameter cdrResultParameter) {
    if ((this.resultData.getReadyForSeries(cdrResultParameter) != null) &&
        this.resultData.isReadyForSeriesFlagChanged(cdrResultParameter)) {
      this.readyForSeries.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
    }
    else {
      this.readyForSeries.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    }
  }

  /**
   * Set Background colour as yellow if the exact match(match ref flag) has changed in delta review
   *
   * @param cdrResultParameter
   */
  private void setExactMatchChngFlagBgColor(final CDRResultParameter cdrResultParameter) {
    if ((this.resultData.getExactMatchUiStr(cdrResultParameter) != null) &&
        this.resultData.isReadyForSeriesFlagChanged(cdrResultParameter)) {
      this.btnParamRefValMatch.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
    }
    else {
      this.btnParamRefValMatch.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    }
  }

  /**
   * @param cdrFuncParam
   */
  private void enableDisableReviewFields(final boolean enable) {
    this.lowerLimitTxt.setEditable(enable);
    this.upperLimitTxt.setEditable(enable);
    this.refValueTxt.setEditable(enable);
    this.hintTxtArea.setEditable(enable);
    this.unitRefTxt.setEditable(enable);
    this.unitChkTxt.setEditable(enable);
  }


  /**
   * This method initializes group
   */
  private void createSections() {
    // IcDm-660
    createRightSectSashForm();
    createsectionParamProperties();
    // IcDm-660
    this.rightSectSash.setWeights(new int[] { 3, 1 });
  }


  /**
   * @param formReviewResults2
   */
  private void createCommentUIControls(final Form grp) {

    createLabelControl(grp.getBody(), "Comment");

    final GridData createGridData = GridDataUtil.getInstance().createGridData(300, 30);
    createGridData.verticalSpan = 2;
    createGridData.verticalAlignment = GridData.FILL;
    createGridData.horizontalAlignment = GridData.FILL;
    createGridData.grabExcessVerticalSpace = false;

    // ICDM-1534(removed code related to this task to make comment editable task id-588370)
    this.commentTxt = new TextBoxContentDisplay(grp.getBody(), SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL,
        RVW_COMMENT_TEXT_SIZE, createGridData);
    this.commentTxt.getText().addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent focusevent) {
        saveCmmntFromTextBox();
      }


      @Override
      public void focusGained(final FocusEvent focusevent) {
        // implementation in focus lost is sufficient

      }
    });
    this.edit = new Button(grp.getBody(), SWT.NONE);
    this.edit.setToolTipText("Edit");
    this.edit.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_16X16));
    this.edit.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        CDRResultParameter rvwParam = ReviewResultParamDetPage.this.getCdrResultParameter();

        CommentDialog commentDialog = new CommentDialog(Display.getCurrent().getActiveShell(), rvwParam, null,
            ReviewResultParamDetPage.this.resultData);
        int isOkPressed = commentDialog.open();
        if (isOkPressed == CODE_FOR_OK) {
          if (!commentDialog.isImpCmtBtnSelected() && !ReviewResultParamDetPage.this.resultData.canChangeComment(
              null == commentDialog.getReviewComments() ? "" : commentDialog.getReviewComments(), rvwParam)) {
            showCannotChngCmntDialog();
          }
          else {
            saveRvwComment(rvwParam, commentDialog);
          }
        }
      }

      /**
       * @param rvwParam
       * @param commentDialog
       */
      private void saveRvwComment(final CDRResultParameter rvwParam, final CommentDialog commentDialog) {
        if (commentDialog.getReviewComments() != null) {
          final String comments = commentDialog.getReviewComments();
          CDRResultParameterServiceClient client = new CDRResultParameterServiceClient();
          saveCommentInDb(rvwParam, client, comments);
        }
        else {
          List<CDRResultParameter> paramList = new ArrayList<>();
          paramList.add(rvwParam);
          ReviewResultNATActionSet actionSet = new ReviewResultNATActionSet();
          actionSet.importReviewComments(commentDialog, paramList, null);
        }
      }

    });

    if (CommonUtils.isNull(this.selectedParam)) {
      this.edit.setEnabled(false);
    }

  }

  /**
  *
  */
  private void saveCmmntFromTextBox() {
    CDRResultParameterServiceClient client = new CDRResultParameterServiceClient();
    CDRResultParameter rvwParam = ReviewResultParamDetPage.this.getCdrResultParameter();
    if (!ReviewResultParamDetPage.this.resultData
        .canChangeComment(null == ReviewResultParamDetPage.this.commentTxt.getText().getText() ? ""
            : ReviewResultParamDetPage.this.commentTxt.getText().getText(), rvwParam)) {
      showCannotChngCmntDialog();
    }
    else {
      if (isCmntChanged(rvwParam)) {
        saveCommentInDb(rvwParam, client, ReviewResultParamDetPage.this.commentTxt.getText().getText());
      }
    }
  }

  /**
   * @return
   */
  private boolean isCmntChanged(final CDRResultParameter resParam) {
    return CommonUtils.isNotEqual(resParam.getRvwComment(), this.commentTxt.getText().getText());
  }

  /**
   * info dialog to indicate,comment cannot be set to null/empty for compli/Q-SSD failed param with score 9
   */
  private void showCannotChngCmntDialog() {
    CDMLogger.getInstance().infoDialog(ApicUiConstants.MSG_CANNOT_CHANGE_COMMENT, Activator.PLUGIN_ID);
  }

  /**
   * @param rvwParam
   * @param client
   * @param comment
   */
  private void saveCommentInDb(final CDRResultParameter rvwParam, final CDRResultParameterServiceClient client,
      final String comment) {
    try {
      List<CDRResultParameter> paramList = new ArrayList<>();
      CDRResultParameter paramClone = rvwParam.clone();
      CommonUtils.shallowCopy(paramClone, rvwParam);
      paramClone.setRvwComment(comment);
      paramList.add(paramClone);
      client.update(paramList);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param parent lable
   * @param style style
   * @return the label of given style
   */
  private Label getNewLabel(final Composite parent, final int style) {
    return new Label(parent, style);
  }

  /**
   * @param formReviewResults2
   */
  private void createisReviewedUIControls(final Form grp) {
    createLabelControl(grp.getBody(), "Score");
    this.scoreComboBox = new Combo(grp.getBody(), SWT.READ_ONLY);
    this.scoreComboBox.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
    fillerLabels(grp.getBody(), 1);
    this.scoreEnums = DATA_REVIEW_SCORE.getApplicableScore(this.resultData.getResultBo().getReviewType());

    this.scoreComboBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        boolean canChangeScoreToNine = ReviewResultParamDetPage.this.resultData.canChangeScoreToNine(
            ReviewResultParamDetPage.this.scoreComboBox.getText(),
            ReviewResultParamDetPage.this.getCdrResultParameter());
        boolean isCompliFail = ReviewResultParamDetPage.this.resultData
            .isCompliFail(ReviewResultParamDetPage.this.getCdrResultParameter());
        boolean isQssdFail =
            ReviewResultParamDetPage.this.resultData.isQssdFail(ReviewResultParamDetPage.this.getCdrResultParameter());
        boolean notFulfilledRules = ReviewResultParamDetPage.this.resultData
            .notFulfilledRules(ReviewResultParamDetPage.this.getCdrResultParameter());
        DATA_REVIEW_SCORE score;
        if ((ReviewResultParamDetPage.this.resultData
            .getCheckedValueString(ReviewResultParamDetPage.this.getCdrResultParameter()) == null) ||
            ReviewResultParamDetPage.this.resultData
                .getCheckedValueString(ReviewResultParamDetPage.this.getCdrResultParameter()).isEmpty()) {
          CDMLogger.getInstance().infoDialog("Parameter without checked value cannot be marked as Reviewed",
              Activator.PLUGIN_ID);
          score =
              ReviewResultParamDetPage.this.resultData.getScore(ReviewResultParamDetPage.this.getCdrResultParameter());
          ReviewResultParamDetPage.this.scoreComboBox
              .setText(DataReviewScoreUtil.getInstance().getScoreDisplayExt(score));
        }
        else if (DataReviewScoreUtil.getInstance().getScoreDisplayExt(DATA_REVIEW_SCORE.S_8)
            .equals(ReviewResultParamDetPage.this.scoreComboBox.getText())) {
          // ICDM-2403
          // checking previous value is not 8 and then throwing error display
          checkPreviousValue();
        }
        else if (DataReviewScoreUtil.getInstance().getScoreDisplayExt(DATA_REVIEW_SCORE.S_9)
            .equals(ReviewResultParamDetPage.this.scoreComboBox.getText()) &&
            ReviewResultParamDetPage.this.resultData
                .isBlackList(ReviewResultParamDetPage.this.getCdrResultParameter())) {
          showDialogForBlackListParam(canChangeScoreToNine, isCompliFail, isQssdFail, notFulfilledRules);
          updateScoreInCombo();
        }
        else if (!canChangeScoreToNine) {
          ReviewResultParamDetPage.this.resultData.checkAndDisplayError(isCompliFail, isQssdFail, notFulfilledRules);
          score =
              ReviewResultParamDetPage.this.resultData.getScore(ReviewResultParamDetPage.this.getCdrResultParameter());
          ReviewResultParamDetPage.this.scoreComboBox
              .setText(DataReviewScoreUtil.getInstance().getScoreDisplayExt(score));
        }
        else {
          updateScoreInCombo();
        }
      }


      /**
       * update score in combo
       */
      private void updateScoreInCombo() {
        try {
          List<CDRResultParameter> paramList = new ArrayList<>();
          CDRResultParameter paramClone = ReviewResultParamDetPage.this.getCdrResultParameter().clone();
          CommonUtils.shallowCopy(paramClone, ReviewResultParamDetPage.this.getCdrResultParameter());
          paramClone.setVersion(ReviewResultParamDetPage.this.getCdrResultParameter().getVersion());
          paramList.add(paramClone);
          paramClone.setReviewScore(ReviewResultParamDetPage.this.getReviewedComboBox().getText()
              .substring(ApicUiConstants.COLUMN_INDEX_0, ApicUiConstants.COLUMN_INDEX_1));
          CDRResultParameterServiceClient client = new CDRResultParameterServiceClient();
          client.update(paramList);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }


    });

  }

  /**
   * @param canChangeScoreToNine
   * @param notFulfilledRules
   * @param isQssdFail
   * @param isCompliFail
   */
  private void showDialogForBlackListParam(final boolean canChangeScoreToNine, final boolean isCompliFail,
      final boolean isQssdFail, final boolean notFulfilledRules) {
    CommonDataBO dataBo = new CommonDataBO();
    StringBuilder strBuilder = new StringBuilder();
    if (!canChangeScoreToNine) {
      ReviewResultParamDetPage.this.resultData.checkAndAppendFailMsg(isCompliFail, isQssdFail, notFulfilledRules, strBuilder);
    }
    try {
      strBuilder.append(dataBo.getMessage(CDRConstants.PARAM, CDRConstants.BLACK_LIST_INFO_SINLGE,
          ReviewResultParamDetPage.this.getCdrResultParameter().getName()));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), Activator.PLUGIN_ID);
    }
    if (canChangeScoreToNine) {
      CDMLogger.getInstance().infoDialog(strBuilder.toString(), Activator.PLUGIN_ID);
    }
    else {
      CDMLogger.getInstance().errorDialog(strBuilder.toString(), Activator.PLUGIN_ID);
    }
  }

  /**
   * check previous values is 8
   */
  private void checkPreviousValue() {
    DATA_REVIEW_SCORE score;
    if (!DATA_REVIEW_SCORE.S_8.getScoreDisplay().equals(
        ReviewResultParamDetPage.this.resultData.getScoreUIType(ReviewResultParamDetPage.this.cdrResultParameter))) {
      CDMLogger.getInstance().errorDialog(
          CommonUtils.concatenate("Setting score to ", DATA_REVIEW_SCORE.S_8.getDbType(), " manually is not allowed"),
          Activator.PLUGIN_ID);
      score = ReviewResultParamDetPage.this.resultData.getScore(ReviewResultParamDetPage.this.getCdrResultParameter());
      ReviewResultParamDetPage.this.scoreComboBox.setText(DataReviewScoreUtil.getInstance().getScoreDisplayExt(score));

    }
  }


  /**
   * Icdm-1186 Save Changes to the Param Details Page
   *
   * @param paramDetailsPage page instance
   */
  public void saveAssessmentData(final ReviewResultParamDetPage paramDetailsPage) {

    try {
      List<CDRResultParameter> paramList = new ArrayList<>();
      CDRResultParameter paramClone = getCdrResultParameter().clone();
      CommonUtils.shallowCopy(paramClone, getCdrResultParameter());
      paramList.add(paramClone);
      paramClone.setRvwComment(paramDetailsPage.getCommentTxt().getText().getText());
      paramClone.setReviewScore(ReviewResultParamDetPage.this.getReviewedComboBox().getText()
          .substring(ApicUiConstants.COLUMN_INDEX_0, ApicUiConstants.COLUMN_INDEX_1));
      CDRResultParameterServiceClient client = new CDRResultParameterServiceClient();
      client.update(paramList);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param formReviewResults2
   */
  private void createChkValControls(final Composite grp) {
    createLabelControl(grp, CommonUIConstants.CHECK_VALUE);
    this.chkValText = createStyledTextField(grp, TEXT_FIELD_WIDTHHINT_1, false);
    this.chkValText.setEditable(false);

    this.toolBarActionSet.dragToScratchPad(new Transfer[] { LocalSelectionTransfer.getTransfer() }, this.chkValText,
        CommonUIConstants.CHECK_VALUE, ReviewResultParamDetPage.this.cdrResult);

  }

  /**
   * This method initializes group1
   */
  private void createsectionParamProperties() {
    // IcDm-660
    this.sectionParamProp =
        this.formToolkit.createSection(this.rightSectSash, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 1;
    this.sectionParamProp.setLayout(gridLayout1);
    this.sectionParamProp.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.sectionParamProp.setText("Review Result");
    this.sectionParamProp.setExpanded(true);

    this.formParamProperties = this.formToolkit.createForm(this.sectionParamProp);
    this.formParamProperties.getBody().setLayout(gridLayout1);
    this.formParamProperties.setLayoutData(GridDataUtil.getInstance().getGridData());

    // ICDM 599
    createToolBarActions();
    createParamPropGroup();
    createReviewDataGroup();
    createAssessmentSection();
    this.sectionParamProp.setClient(this.formParamProperties);
    this.sectionParamProp.getDescriptionControl().setEnabled(false);
  }

  /**
   *
   */
  private void createRuleDetailsComposite() {
    this.scrollComp = new ScrolledComposite(this.groupOneComp, SWT.H_SCROLL | SWT.V_SCROLL);
    this.scrollComp.setLayout(new GridLayout());
    this.ruleDetComp = new Composite(this.scrollComp, SWT.NONE);
    this.ruleDetComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    final GridLayout layout = new GridLayout();
    layout.numColumns = 6;
    layout.verticalSpacing = 10;
    this.ruleDetComp.setLayout(layout);
    this.ruleDetComp.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.scrollComp.setContent(this.ruleDetComp);
    this.scrollComp.setExpandHorizontal(true);
    this.scrollComp.setExpandVertical(true);
    this.scrollComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.scrollComp.setLayout(new GridLayout());
    this.scrollComp.addControlListener(new ControlAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void controlResized(final ControlEvent e) {
        ReviewResultParamDetPage.this.scrollComp
            .setMinSize(ReviewResultParamDetPage.this.ruleDetComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      }
    });
  }

  /**
   * Create Sash form for the right section Icdm-660
   */
  private void createRightSectSashForm() {
    this.rightSectSash = new SashForm(this.compositeTwo, SWT.VERTICAL);
    this.rightSectSash.setLayout(new GridLayout());
    this.rightSectSash.setLayoutData(GridDataUtil.getInstance().getGridData());
  }


  /**
   *
   */
  private void createAssesmentComposite() {
    this.assesmentSecScrollComp = new ScrolledComposite(this.rightSectSash, SWT.H_SCROLL | SWT.V_SCROLL);
    this.assesmentSecScrollComp.setLayout(new GridLayout());
    this.assesmentSecComposite = new Composite(this.assesmentSecScrollComp, SWT.NONE);
    this.assesmentSecComposite.setLayout(new GridLayout());
    this.assesmentSecComposite.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.assesmentSecScrollComp.setContent(this.assesmentSecComposite);
    this.assesmentSecScrollComp.setExpandHorizontal(true);
    this.assesmentSecScrollComp.setExpandVertical(true);
    this.assesmentSecScrollComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.assesmentSecScrollComp.setLayout(new GridLayout());
    this.assesmentSecScrollComp.addControlListener(new ControlAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void controlResized(final ControlEvent e) {
        ReviewResultParamDetPage.this.assesmentSecScrollComp
            .setMinSize(ReviewResultParamDetPage.this.assesmentSecComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      }
    });

  }

  /**
   * ICDM 599 Creates the toolbar actions for filtering the elements in the tree viewer
   */
  private void createToolBarActions() {
    this.toolBarManager = (ToolBarManager) getToolBarManager();

    final ToolBar toolbar = this.toolBarManager.createControl(this.sectionParamProp);

    ReviewResultToolBarActionSet resultToolBarActionSet =
        new ReviewResultToolBarActionSet(getEditorSite().getActionBars().getStatusLineManager(), this);

    final Separator separator = new Separator();

    // ICDM-2439
    // Filter For the compliance parameters
    resultToolBarActionSet.complianceFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    // Filter For the non compliance parameters
    resultToolBarActionSet.nonComplianceFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    this.toolBarManager.add(separator);
    // Filter For the compliance parameters
    resultToolBarActionSet.blackListFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    // Filter For the non compliance parameters
    resultToolBarActionSet.nonBlackListFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    this.toolBarManager.add(separator);
    // Filter For the QSSD parameters
    resultToolBarActionSet.qSSDFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    // Filter For the non QSSD parameters
    resultToolBarActionSet.nonQSSDFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    this.toolBarManager.add(separator);
    // Filter For the Axis point - Value Type
    resultToolBarActionSet.axisPointFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    // Filter for the Ascii - Value Type
    resultToolBarActionSet.asciiFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    // Filter For the Value Block - Value Type
    resultToolBarActionSet.valueBlockFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    // Filter for the Value- Value Type
    resultToolBarActionSet.valueFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    // Filter For the Curve - Value Type
    resultToolBarActionSet.curveFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    // Filter for the Map Value Type
    resultToolBarActionSet.mapFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);

    this.toolBarManager.add(separator);
    this.toolBarManager.add(separator);

    // Filter For the Rivet - Class Type
    resultToolBarActionSet.rivetFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    // Filter For the Nail - Class Type
    resultToolBarActionSet.nailFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    // Filter For the Screw - Class Type
    resultToolBarActionSet.screwFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    // Filter For the Undefined - Class Type
    resultToolBarActionSet.undefinedClassFilterAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);

    this.toolBarManager.add(separator);
    this.toolBarManager.add(separator);

    resultToolBarActionSet.manualFilterAction(this.toolBarManager, this.paramTableViewer);
    resultToolBarActionSet.automaticFilterAction(this.toolBarManager, this.paramTableViewer);
    // Filter For the Undefined - Review Type Icdm-654
    resultToolBarActionSet.reviewUnDefFilterAction(this.toolBarManager, this.paramTableViewer);


    this.toolBarManager.add(separator);
    this.toolBarManager.add(separator);


    resultToolBarActionSet.showOk(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    // Icdm-632
    resultToolBarActionSet.showResultNotOk(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    resultToolBarActionSet.showResultUndefined(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);

    this.toolBarManager.add(separator);
    this.toolBarManager.add(separator);

    resultToolBarActionSet.showReviewed(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    resultToolBarActionSet.showNotReviewed(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);


    // Icdm-807
    this.toolBarManager.add(separator);
    // Filter for parameter with change marker
    resultToolBarActionSet.hasChangeMarkerAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    // Filter for parameter with no change marker
    resultToolBarActionSet.hasNoChangeMarkerAction(this.toolBarManager, this.toolBarFilters, this.paramTableViewer);
    this.toolBarManager.update(true);
    addResetAllFiltersAction();
    this.sectionParamProp.setTextClient(toolbar);

  }


  /**
   * Add reset filter button ICDM-1207
   */
  private void addResetAllFiltersAction() {
    getRefreshComponentSet().add(this.paramTableViewer);
    addResetFiltersAction();
  }

  /**
   * create the assessment section
   */
  private void createAssessmentSection() {
    createAssesmentComposite();
    // IcDm-660
    Section sectionAssesment =
        this.formToolkit.createSection(this.assesmentSecComposite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 9;
    sectionAssesment.setLayout(gridLayout1);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    sectionAssesment.setLayoutData(gridData);
    sectionAssesment.setText("Assessment");
    sectionAssesment.setExpanded(true);
    this.formAssessment = this.formToolkit.createForm(sectionAssesment);
    this.formAssessment.getBody().setLayout(gridLayout1);
    this.formAssessment.setLayoutData(gridData);

    createisReviewedUIControls(this.formAssessment);
    createTableViewerForFileSelection(this.formAssessment);

    sectionAssesment.setClient(this.formAssessment);
    sectionAssesment.getDescriptionControl().setEnabled(false);
  }


  /**
   * Icdm-611 Create table for adding deleting and downloading files
   *
   * @param formAssessment2
   */
  private void createTableViewerForFileSelection(final Form formAssessment2) {

    this.filesTabViewer = GridTableViewerUtil.getInstance().createCustomGridTableViewer(this.formAssessment.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
    initializeEditorStatusLineManager(this.filesTabViewer);

    createTableColums();
    final GridData gridData2 = new GridData();
    gridData2.verticalSpan = 3;
    gridData2.verticalAlignment = GridData.FILL;
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.grabExcessVerticalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;

    this.filesTabViewer.getGrid().setLayoutData(gridData2);
    this.filesTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    this.filesTabViewer.setInput(null);

    createAddDeleteButtons(formAssessment2);

    this.filesTabViewer.addSelectionChangedListener(event -> {

      boolean isToEnableDelBtn = ReviewResultParamDetPage.this.resultData.getResultBo().isModifiable() &&
          !ReviewResultParamDetPage.this.resultData.getResultBo().isResultLocked();
      ReviewResultParamDetPage.this.deleteButton.setEnabled(isToEnableDelBtn);

      if (ReviewResultParamDetPage.this.resultData.getResultBo().canDownloadFiles()) {
        ReviewResultParamDetPage.this.downloadButton.setEnabled(true);
      }
      setSelectedIcdmFiles();
    });


    this.filesTabViewer.addDoubleClickListener(event -> {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      RvwFile rvwFile = (RvwFile) selection.getFirstElement();
      if (rvwFile != null) {
        this.editor.openRvwFile(rvwFile);
      }
    });

  }

  @Override
  public void initializeEditorStatusLineManager(final Viewer viewer1) {
    if (viewer1 instanceof CustomGridTableViewer) {
      final CustomGridTableViewer customGridTableViewer = (CustomGridTableViewer) viewer1;
      final IStatusLineManager statusLineManagerEditorPart = getEditorSite().getActionBars().getStatusLineManager();
      customGridTableViewer.setStatusLineManager(statusLineManagerEditorPart);
    }

  }

  /**
   * @param fileSection2
   */
  private void createAddDeleteButtons(final Form formAssessment2) {
    this.addButton = new Button(formAssessment2.getBody(), SWT.PUSH);
    this.addButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ADD_16X16));
    this.addButton.setToolTipText("Add Files");
    this.addButton.setEnabled(ReviewResultParamDetPage.this.resultData.getResultBo().canDownloadFiles());
    fillerLabels(formAssessment2.getBody(), 4);
    this.addButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        addButtonAction();
      }
    });

    createResultUIControl(formAssessment2.getBody());
    fillerLabels(formAssessment2.getBody(), 1);

    this.deleteButton = new Button(formAssessment2.getBody(), SWT.PUSH);
    this.deleteButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    this.deleteButton.setToolTipText("Delete Files");
    this.deleteButton.setEnabled(false);
    final GridData gridData3 = new GridData();
    this.deleteButton.setLayoutData(gridData3);

    this.deleteButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        deleteButtonAction();
      }
    });
    fillerLabels(formAssessment2.getBody(), 4);
    createCommentUIControls(formAssessment2);
    this.downloadButton = new Button(formAssessment2.getBody(), SWT.PUSH);
    this.downloadButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DOWNLOAD_16X16));
    this.downloadButton.setToolTipText("Download File");
    this.downloadButton.setEnabled(false);
    this.downloadButton.setLayoutData(gridData3);

    this.downloadButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        downloadButtonAction();
      }
    });


  }


  /**
   * download the zip file
   */
  protected void downloadButtonAction() {
    final List<RvwFile> icdmFileList = ReviewResultParamDetPage.this.selectedIcdmFile;
    final DirectoryDialog dirDialog = new DirectoryDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
    dirDialog.setText("Save Attached Files");

    final String dirSelected = dirDialog.open();
    RvwFileServiceClient client = new RvwFileServiceClient();
    if (dirSelected != null) {
      for (RvwFile icdmFile : icdmFileList) {
        try {
          client.downloadEmrFile(icdmFile.getId(), icdmFile.getName(), dirSelected);
        }
        catch (ApicWebServiceException exception) {
          CDMLogger.getInstance().error(exception.getLocalizedMessage(), exception, Activator.PLUGIN_ID);
        }
        CDMLogger.getInstance().info("File Downloaded Successfully in  " + dirSelected, Activator.PLUGIN_ID);
      }


    }
  }


  /**
   *
   */
  protected void deleteButtonAction() {

    final List<RvwFile> icdmFileList = this.selectedIcdmFile;
    Set<RvwFile> filesSet = new HashSet<>(icdmFileList);

    RvwFileServiceClient fileClient = new RvwFileServiceClient();
    try {
      fileClient.delete(filesSet);
    }
    catch (ApicWebServiceException exception) {
      CDMLogger.getInstance().error(exception.getLocalizedMessage(), exception, Activator.PLUGIN_ID);
    }


  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    Map<IModelType, Map<Long, ChangeData<?>>> consChangeData = dce.getConsChangeData();
    if (isWidgetNotDisposed()) {
      if (null != this.viewer) {
        this.viewer.setInput("");
        this.viewer.refresh();
      }
      for (Entry<IModelType, Map<Long, ChangeData<?>>> changeDataEntry : consChangeData.entrySet()) {
        Map<Long, ChangeData<?>> changeDataMap = changeDataEntry.getValue();

        if (changeDataEntry.getKey() == MODEL_TYPE.CDR_RES_PARAMETER) {

          for (Entry<Long, ChangeData<?>> entry : changeDataMap.entrySet()) {
            CDRResultParameter param = (CDRResultParameter) entry.getValue().getNewData();
            setParamFields(param);
          }

          ReviewResultEditorInput editorInput =
              (ReviewResultEditorInput) ReviewResultParamDetPage.this.getEditor().getEditorInput();
          setRvwRsltFromEditor(new CDRHandler(), editorInput);
        }
        else if (changeDataEntry.getKey() == MODEL_TYPE.CDR_RES_FILE) {
          refreshUIforRvwFileModelType(changeDataMap);
        }
        else if (changeDataEntry.getKey() == MODEL_TYPE.CDR_RESULT) {
          refreshUIforDataRvwResultModel(changeDataMap);
        }
        else if (CommonUtils.isEqual(changeDataEntry.getKey(), MODEL_TYPE.USER_PREFERENCE)) {
          refreshUIforUserPreference();
        }
        disableRvwFieldsForLockedRvw();
      }
    }
  }

  /**
   *
   */
  private void refreshUIforUserPreference() {
    if (CommonUtils.isNotNull(this.editor)) {
      this.editor.setDecimalPref();
    }
    if (CommonUtils.isNotNull(this.tableGraphComp)) {
      this.tableGraphComp.clearTableGraph();
      setGraphTable();
    }
    setReviewResultDetails(this.cdrResultParameter);

  }

  /**
   * @param param
   */
  private void setParamFields(final CDRResultParameter param) {
    if ((getPartControl() != null) && (null != param)) {
      this.commentTxt.getText().setText(param.getRvwComment() == null ? "" : param.getRvwComment());
      this.scoreComboBox.setText(this.dataRvwUtilObj.getScoreDisplayExt(this.resultData.getScore(param)));
      setScoreComboToolTip(param);
      this.filesTabViewer.setInput(this.resultData.getAttachments(param.getId()));
      this.filesTabViewer.refresh();
      this.deleteButton.setEnabled(true);
    }
  }

  /**
   * @param handler
   * @param editorInput
   */
  private void setRvwRsltFromEditor(final CDRHandler handler, final ReviewResultEditorInput editorInput) {
    CDRReviewResult cdrReviewResult;
    try {
      cdrReviewResult = handler.getCdrReviewResult(editorInput.getReviewResult().getId());
      editorInput.setReviewResult(cdrReviewResult);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   *
   */
  private void disableRvwFieldsForLockedRvw() {
    if (this.resultData.getResultBo().isResultLocked() && (getPartControl() != null)) {
      this.deleteButton.setEnabled(false);
      enableDisableReviewFields(false);
      enableDisableReviewResultFields(false);
    }
  }

  /**
   * @param changeDataMap
   */
  private void refreshUIforDataRvwResultModel(final Map<Long, ChangeData<?>> changeDataMap) {
    for (ChangeData<?> changeData : changeDataMap.values()) {
      ReviewResultEditorInput editorInput =
          ((ReviewResultEditor) (ReviewResultParamDetPage.this.getEditor())).getEditorInput();
      if ((changeData.getChangeType() == CHANGE_OPERATION.DELETE) &&
          (CommonUtils.isEqual(changeData.getObjId(), editorInput.getReviewResult().getId()))) {

        CDMLogger.getInstance().infoDialog(
            "CDR result '" + editorInput.getReviewResult().getName() + "' is deleted. The editor will be closed.",
            Activator.PLUGIN_ID);

        getEditorSite().getPage().closeEditor(this, false);
      }
    }
  }

  /**
   * @param changeDataMap
   */
  private void refreshUIforRvwFileModelType(final Map<Long, ChangeData<?>> changeDataMap) {
    Long rvwParamId = null;
    for (ChangeData<?> changeData : changeDataMap.values()) {
      if (changeData.getChangeType() == CHANGE_OPERATION.CREATE) {
        rvwParamId = ((RvwFile) changeData.getNewData()).getRvwParamId();
      }
      else if (changeData.getChangeType() == CHANGE_OPERATION.DELETE) {
        rvwParamId = ((RvwFile) changeData.getOldData()).getRvwParamId();
      }
    }
    if ((getPartControl() != null) && (null != rvwParamId)) {
      final SortedSet<RvwFile> attachments = this.resultData.getAttachments(rvwParamId);
      this.filesTabViewer.setInput(attachments);
      this.deleteButton.setEnabled(false);
      this.downloadButton.setEnabled(false);
    }
  }

  /**
   * @return
   */
  private boolean isWidgetNotDisposed() {
    return (null != this.mainComposite) && !this.mainComposite.isDisposed();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReviewResultBO getDataHandler() {
    return this.editor.getEditorInput().getDataHandler();
  }

  /**
   *
   */
  protected void addButtonAction() {

    final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE | SWT.MULTI);
    fileDialog.setText("Add Review files");
    fileDialog.setFilterNames(new String[] { "*.zip" });
    final String path = fileDialog.open();
    final String[] fileNames = fileDialog.getFileNames();

    for (String fileName : fileNames) {
      try {
        if (!isNotDuplicateFile(fileName)) {
          final String appendedPath = getAbsoluteFilePath(path, fileName);
          final File file = new File(appendedPath);
          RvwFile rvwFile = new RvwFile();
          rvwFile.setResultId(ReviewResultParamDetPage.this.cdrResult.getId());
          rvwFile.setRvwParamId(this.cdrResultParameter.getId());
          rvwFile.setFileType(CDRConstants.REVIEW_FILE_TYPE.RVW_PRM_ADDL_FILE.getDbType());
          rvwFile.setFilePath(file.getAbsolutePath());
          rvwFile.setName(file.getName());
          rvwFile.setNodeType(MODEL_TYPE.CDR_RESULT.getTypeCode());
          RvwFileServiceClient fileClient = new RvwFileServiceClient();
          fileClient.create(rvwFile);
        }
        else {
          MessageDialogUtils.getErrorMessageDialog("File Already Attached",
              "The File is allready Attached to the Result.Please select another file to upload");
        }
      }
      catch (ApicWebServiceException exception) {
        CDMLogger.getInstance().error(exception.getLocalizedMessage(), exception, Activator.PLUGIN_ID);
      }
    }


  }


  /**
   * @param fileName
   * @return true if the file is duplicate
   */
  private boolean isNotDuplicateFile(final String fileName) {
    for (RvwFile file : this.resultData.getAttachments(this.cdrResultParameter.getId())) {
      if (ApicUtil.compare(fileName, file.getName()) == 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param fullFilePath
   * @param fileNam
   * @return
   */
  private String getAbsoluteFilePath(final String fullFilePath, final String fileNam) {
    final int idx = fullFilePath.lastIndexOf('\\');
    final StringBuilder appendedPath = new StringBuilder(idx >= 0 ? fullFilePath.substring(0, idx) : fullFilePath);
    appendedPath.append("\\");
    appendedPath.append(fileNam);
    return appendedPath.toString();
  }

  /**
   * Icdm-611 create column for the Files table
   */
  private void createTableColums() {
    GridViewerColumn fileNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.filesTabViewer, "File Name", 200);

    fileNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String fileName = "";
        if (element instanceof RvwFile) {
          fileName = ((RvwFile) element).getName();
        }
        return fileName;
      }
    });
    GridViewerColumn fileCreatedTime;
    fileCreatedTime =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.filesTabViewer, "Attached Date", 150);

    fileCreatedTime.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String createdTime = "";
        if (element instanceof RvwFile) {
          createdTime = ((RvwFile) element).getCreatedDate();
        }
        return createdTime;
      }
    });

  }


  /**
   * create review Data Group
   */
  private void createReviewDataGroup() {

    this.reviewDataComp = new Composite(this.formParamProperties.getBody(), SWT.NONE);
    this.reviewDataComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    // IcDm-660
    createReviewSashForm();
    final GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    this.reviewDataComp.setLayout(layout);
    createGroupThree();
    createGroupFour();
    // IcDm-660
    this.rvwSashForm.setWeights(new int[] { 1, 1 });
  }


  /**
   * create the sash form for the Right section
   */
  private void createReviewSashForm() {
    this.rvwSashForm = new SashForm(this.reviewDataComp, SWT.HORIZONTAL);
    this.rvwSashForm.setLayout(new GridLayout());
    this.rvwSashForm.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * create the Param Property Group
   */
  private void createParamPropGroup() {
    Composite paramComp = new Composite(this.formParamProperties.getBody(), SWT.NONE);

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    paramComp.setLayoutData(gridData);

    final GridLayout layout = new GridLayout();
    layout.numColumns = 7;
    paramComp.setLayout(layout);
    createNameUIControls(paramComp);
    createClassUIControls(paramComp);
    getNewLabel(paramComp, SWT.NONE);
    getNewLabel(paramComp, SWT.NONE);
    createLongNameUIControls(paramComp);
    createCodeWordUIControls(paramComp);
    createBitWiseFlagUIControl(paramComp);
  }

  /**
   * @param paramComp2
   */
  private void createBitWiseFlagUIControl(final Composite paramComp2) {
    createLabelControl(paramComp2, "Bitwise");
    this.bitwiseChkBox = new Button(paramComp2, SWT.CHECK);
    this.bitwiseChkBox.setEnabled(false);

  }


  /**
   * @param group
   */
  private void createNameUIControls(final Composite group) {
    createLabelControl(group, "Name");
    this.nameTxt = createStyledTextField(group, TEXT_FIELD_WIDTHHINT_2, false);
    this.nameTxt.setEditable(false);
  }

  /**
   * @param form
   */
  private void createClassUIControls(final Composite grp) {
    final Label emptyLbl = new Label(grp, SWT.NONE);
    emptyLbl.setText("       ");
    createLabelControl(grp, "Class");
    this.classTxt = createStyledTextField(grp, TEXT_FIELD_WIDTHHINT_1, false);
    this.classTxt.setEditable(false);
  }


  /**
   * @param form
   */
  private void createLongNameUIControls(final Composite grp) {
    createLabelControl(grp, "Long Name");
    this.longNameTxt = createStyledTextField(grp, TEXT_FIELD_WIDTHHINT_2, false);
    this.longNameTxt.setEditable(false);
  }

  /**
   * @param form
   */
  private void createCodeWordUIControls(final Composite grp) {
    fillerLabels(grp, 1);
    createLabelControl(grp, "Codeword");
    this.codeWordChkBox = new Button(grp, SWT.CHECK);
    this.codeWordChkBox.setEnabled(false);
  }

  /**
   * This method initializes groupThree
   *
   * @param layout2s
   */
  private void createGroupThree() {
    // IcDm-660
    this.groupOneComp = new Group(this.rvwSashForm, SWT.NONE);
    this.groupOneComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    // ICDM-895
    this.groupOneComp.setLayout(new GridLayout());
    createRuleDetailsComposite();
    // Create ui controls
    createUIControls(this.ruleDetComp);
    createHintUIControls(this.ruleDetComp);
  }


  /**
   * This method initializes group1
   *
   * @param layout
   */
  private void createGroupFour() {
    // IcDm-660
    Group groupTwo = new Group(this.rvwSashForm, SWT.FILL);
    groupTwo.setLayout(new GridLayout());
    groupTwo.setLayoutData(GridDataUtil.getInstance().getGridData());

    // Create graph/table section
    final Section graphSection = createGraphSection(groupTwo);

    // Create graph/table composite
    createTableGraphComposite(graphSection);

    // Create table graph composite structure
    this.tableGraphComp = new CalDataTableGraphComposite(this.graphComp, null, null, CDMLogger.getInstance());
  }

  /**
   * Method to create Table/Graph composite
   *
   * @param graphSection
   */
  private void createTableGraphComposite(final Section graphSection) {
    this.tblGraphScrollComp = new ScrolledComposite(graphSection, SWT.H_SCROLL | SWT.V_SCROLL);
    this.tblGraphScrollComp.setLayout(new GridLayout());
    graphSection.setClient(this.tblGraphScrollComp);

    this.graphComp = this.formToolkit.createComposite(this.tblGraphScrollComp);
    // Set layout to graph/table composite
    this.graphComp.setLayout(new GridLayout());

    this.tblGraphScrollComp.setContent(this.graphComp);
    this.tblGraphScrollComp.setExpandHorizontal(true);
    this.tblGraphScrollComp.setExpandVertical(true);
    this.tblGraphScrollComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.tblGraphScrollComp.setLayout(new GridLayout());
    this.tblGraphScrollComp.addControlListener(new ControlAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void controlResized(final ControlEvent e) {
        ReviewResultParamDetPage.this.tblGraphScrollComp
            .setMinSize(ReviewResultParamDetPage.this.graphComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      }
    });

  }

  /**
   * This method create Label instance for statisctical values
   *
   * @param caldataComp
   * @param lblName
   */
  private void createLabelControl(final Composite composite, final String lblName) {
    LabelUtil.getInstance().createLabel(this.formToolkit, composite, lblName);
  }

  /**
   * @param grp
   */
  private void createUIControls(final Composite grp) {
    createLowerLimitUIControls(grp);
    fillerLabels(grp, TWO_EMPTY_FILLERS);

    createReadySeriesUIControls(grp);

    createUpperLimitUIControls(grp);
    fillerLabels(grp, TWO_EMPTY_FILLERS); // Icdm-513
    createRefValueExactMatchCtrl(grp);

    createBitWiseUIControls(grp);
    fillerLabels(grp, FILLER_LABEL_LIMIT);

    createRefValUIControls(grp);
    createRefGraphBtnUICtrl(grp);
    createLabelControl(grp, "\t");
    createRefUnitUIControls(grp);

    // ICDM-1320
    if (this.resultData.getResultBo().isDeltaReview()) {
      createParRefValUIControl(grp);
      createParRefGraphBtnUICtrl(grp);
      fillerLabels(grp, THREE_EMPTY_FILTERS);
    }
    createChkValControls(grp);
    createChkGraphBtnUICtrl(grp);
    createLabelControl(grp, "\t");
    createChkUnitUIControls(grp);
    // ICDM-1320
    if (this.resultData.getResultBo().isDeltaReview()) {
      createParCheckValUIControl(grp);
      createParChkGraphBtnUICtrl(grp);
      fillerLabels(grp, THREE_EMPTY_FILTERS);
    }
  }


  /**
   * @param grp
   */
  private void createBitWiseUIControls(final Composite grp) {
    createLabelControl(grp, "BitWise Limit");
    this.bitwiseLimitTxt = createStyledTextField(grp, TEXT_FIELD_WIDTHHINT_1, true);
    geCtrlDec(this.bitwiseLimitTxt);

  }

  /**
   * @param control
   * @param position
   * @return the control decoration
   */
  private ControlDecoration geCtrlDec(final Control control) {
    return new ControlDecoration(control, SWT.LEFT | SWT.TOP);
  }


  /**
   * @param grp
   */
  private void createParCheckValUIControl(final Composite grp) {
    createLabelControl(grp, CommonUIConstants.TXT_PARENT_CHECK_VALUE);
    this.parChkValText = createStyledTextField(grp, TEXT_FIELD_WIDTHHINT_1, false);
    this.parChkValText.setEditable(false);

    this.toolBarActionSet.dragToScratchPad(new Transfer[] { LocalSelectionTransfer.getTransfer() }, this.parChkValText,
        CommonUIConstants.TXT_PARENT_CHECK_VALUE, ReviewResultParamDetPage.this.cdrResult);

  }

  /**
   * @param grp
   */
  private void createParChkGraphBtnUICtrl(final Composite grp) {
    this.parChkValButton = new Button(grp, SWT.TOGGLE);
    this.parChkValButton.setImage(this.browseButtonImage);
    this.parChkValButton.setToolTipText(DISPLAY_GRAPH_AND_TABLE);
    this.parChkValButton.setSelection(true);
    this.parChkValButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (ReviewResultParamDetPage.this.cdrResultParameter != null) {
          CalData parCheckVal = ReviewResultParamDetPage.this.resultData
              .getParentCheckedVal(ReviewResultParamDetPage.this.cdrResultParameter);
          if (null != parCheckVal) {
            setParentCheckVal(parCheckVal);
          }
          displayTableGraphCom();

        }
      }
    });

  }


  /**
   * @param grp
   */
  private void createParRefValUIControl(final Composite grp) {
    createLabelControl(grp, "Parent Reference Value");
    this.parRefValText = createStyledTextField(grp, TEXT_FIELD_WIDTHHINT_1, false);
    this.parRefValText.setEditable(false);

    this.toolBarActionSet.dragToScratchPad(new Transfer[] { LocalSelectionTransfer.getTransfer() }, this.parRefValText,
        "Parent Reference Value", ReviewResultParamDetPage.this.cdrResult);

  }

  /**
   * @param grp
   */
  private void createParRefGraphBtnUICtrl(final Composite grp) {
    this.parRefValButton = new Button(grp, SWT.TOGGLE);
    this.parRefValButton.setImage(this.browseButtonImage);
    this.parRefValButton.setToolTipText(DISPLAY_GRAPH_AND_TABLE);
    this.parRefValButton.setSelection(true);
    this.parRefValButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (ReviewResultParamDetPage.this.cdrResultParameter != null) {
          CalData parRefVal = ReviewResultParamDetPage.this.resultData
              .getParentRefVal(ReviewResultParamDetPage.this.cdrResultParameter);
          if (null != parRefVal) {
            setParRefVal(parRefVal);
          }
          displayTableGraphCom();

        }
      }
    });

  }

  /**
   * @param grp
   */
  private void createRefValueExactMatchCtrl(final Composite grp) {
    this.btnParamRefValMatch = new Button(grp, SWT.CHECK);
    this.btnParamRefValMatch.setToolTipText("Exact match to reference value");
    this.btnParamRefValMatch.setEnabled(false);
    createLabelControl(grp, "Exact match to reference value");
  }


  /**
   * @param grp
   */
  private void createChkGraphBtnUICtrl(final Composite grp) {
    this.chkValButton = new Button(grp, SWT.TOGGLE);
    this.chkValButton.setImage(this.browseButtonImage);
    this.chkValButton.setToolTipText(DISPLAY_GRAPH_AND_TABLE);
    this.chkValButton.setSelection(true);
    this.chkValButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (ReviewResultParamDetPage.this.cdrResultParameter != null) {
          CalData checkVal = ReviewResultParamDetPage.this.resultData
              .getCheckedValueObj(ReviewResultParamDetPage.this.cdrResultParameter);
          if (checkVal != null) {
            if (ReviewResultParamDetPage.this.chkValButton.getSelection()) {
              ReviewResultParamDetPage.this.calDataColorMap.put(checkVal, ParamTypeColor.CHECK_VALUE);
            }
            else {
              ReviewResultParamDetPage.this.calDataColorMap.remove(checkVal);
            }
          }
          displayTableGraphCom();
        }
      }


    });

  }

  // ICDM-1320
  private void displayTableGraphCom() {
    CalDataComparison calComp = new CalDataComparison();
    if (!this.calDataColorMap.isEmpty()) {
      try {
        for (Entry<CalData, ParamTypeColor> entrySet : this.calDataColorMap.entrySet()) {
          CalDataAttributes checkAttr = new CalDataAttributes(entrySet.getKey(), entrySet.getValue().getColorCode());
          checkAttr.setLabelPrefix(" (" + entrySet.getValue().getType() + ") ");
          checkAttr.setShowDifferenceIndicator(false);
          calComp.addCalDataAttr(checkAttr);
        }
        ReviewResultParamDetPage.this.tableGraphComp.fillTableAndGraph(calComp);
      }
      catch (CalDataTableGraphException excep) {
        CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
      }
    }
    else {
      ReviewResultParamDetPage.this.tableGraphComp.clearTableGraph();
    }

  }

  /**
   * @param grp
   */
  private void createRefGraphBtnUICtrl(final Composite grp) {
    this.refButton = new Button(grp, SWT.TOGGLE);
    this.refButton.setImage(this.browseButtonImage);
    this.refButton.setToolTipText(DISPLAY_GRAPH_AND_TABLE);
    this.refButton.setSelection(true);

    this.refButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (ReviewResultParamDetPage.this.cdrResultParameter != null) {
          CalData refVal =
              ReviewResultParamDetPage.this.resultData.getRefValueObj(ReviewResultParamDetPage.this.cdrResultParameter);
          if (refVal != null) {
            if (ReviewResultParamDetPage.this.refButton.getSelection()) {
              ReviewResultParamDetPage.this.calDataColorMap.put(refVal, ParamTypeColor.REF_VALUE);
            }
            else {
              ReviewResultParamDetPage.this.calDataColorMap.remove(refVal);
            }
          }
          displayTableGraphCom();
        }
      }
    });
  }


  /**
   * @param grp
   */
  private void createChkUnitUIControls(final Composite grp) {
    createLabelControl(grp, "Unit");
    this.unitChkTxt = createStyledTextField(grp, TEXT_FIELD_WIDTHHINT_1, true);
    this.unitChkTxt.setEditable(false);

  }

  /**
   * @param grp
   */
  private void createResultUIControl(final Composite grp) {

    createLabelControl(grp, "Check Result");
    final GridData gridData = GridDataUtil.getInstance().createGridData(TEXT_AREA_WIDTHHINT, TEXT_AREA_HEIGHTHINT);
    gridData.verticalAlignment = GridData.FILL;
    gridData.verticalSpan = 2;
    this.resultTxt = createStyledTextField(grp, TEXT_FIELD_WIDTHHINT_1, true);
    this.resultTxt.setEditable(false);
  }

  /**
   * @param grp Ref Unit
   */
  private void createRefUnitUIControls(final Composite grp) {
    createLabelControl(grp, "Unit");
    this.unitRefTxt = createStyledTextField(grp, TEXT_FIELD_WIDTHHINT_1, true);
    this.unitRefTxt.setEditable(false);

  }


  /**
   * @param grp
   */
  private void createHintUIControls(final Composite grp) {
    createLabelControl(grp, "Hint :");
    fillerLabels(grp, 1);
    // ICDM-759
    this.hintTxtArea =
        new StyledText(grp, SWT.BORDER | SWT.MULTI | SWT.Expand | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);

    final GridData createGridData =
        GridDataUtil.getInstance().createGridData(TEXT_AREA_WIDTHHINT, TEXT_AREA_HEIGHTHINT);
    createGridData.grabExcessVerticalSpace = true;
    createGridData.verticalAlignment = GridData.FILL;
    createGridData.horizontalSpan = 6;
    this.hintTxtArea.setLayoutData(createGridData);
    this.hintTxtArea.setEditable(false);

  }

  /**
   * @param comp
   */
  private void createRefValUIControls(final Composite comp) {
    createLabelControl(comp, "Reference Value");
    this.refValueTxt = createStyledTextField(comp, TEXT_FIELD_WIDTHHINT_1, true);

    this.toolBarActionSet.dragToScratchPad(new Transfer[] { LocalSelectionTransfer.getTransfer() }, this.refValueTxt,
        "Reference Value", ReviewResultParamDetPage.this.cdrResult);
  }

  /**
   * @param grp
   */
  private void createReadySeriesUIControls(final Composite grp) {

    this.readyForSeries = new Button(grp, SWT.CHECK);
    this.readyForSeries.setEnabled(false);
    createLabelControl(grp, "Values matching the rule\nare Ready For Series");
  }


  /**
   * This method creates different parameter UI controls
   *
   * @param caldataComp
   */
  private void createLowerLimitUIControls(final Composite comp) {
    createLabelControl(comp, "Lower Limit");
    this.lowerLimitTxt = createStyledTextField(comp, TEXT_FIELD_WIDTHHINT_1, true);

    geCtrlDec(this.lowerLimitTxt);
  }

  /**
   * This method creates different parameter UI controls
   *
   * @param caldataComp
   */
  private void createUpperLimitUIControls(final Composite comp) {
    createLabelControl(comp, "Upper Limit");
    this.upperLimitTxt = createStyledTextField(comp, TEXT_FIELD_WIDTHHINT_1, true);

    geCtrlDec(this.upperLimitTxt);
  }

  /**
   * This method creates Table/Graph UI form section
   *
   * @param graphComp
   * @return Section
   */
  private Section createGraphSection(final Composite graphComp) {
    // Creates Table/Graph Section
    return createSection("Table/Graph", false, graphComp);
  }

  /**
   * This method creates section
   *
   * @param sectionName defines section name
   * @param descControlEnable defines description control enable or not
   * @param group1
   * @return Section instance
   */
  private Section createSection(final String sectionName, final boolean descControlEnable, final Composite group1) {
    return SectionUtil.getInstance().createSection(group1, this.formToolkit, GridDataUtil.getInstance().getGridData(),
        sectionName, descControlEnable);
  }


  /**
   * @param grp
   * @param limit
   */
  private void fillerLabels(final Composite grp, final int limit) {
    for (int i = 0; i < limit; i++) {
      getNewLabel(grp, SWT.NONE);
    }

  }

  /**
   * @return the selectedParam
   */
  public Parameter getSelectedParam() {
    return this.selectedParam;
  }


  /**
   * @return the hint area text
   */
  public StyledText getHintTxtArea() {
    return this.hintTxtArea;
  }


  /**
   * @return the Lower Limit Text
   */
  public StyledText getLowerLimitTxt() {
    return this.lowerLimitTxt;
  }


  /**
   * @return the upper Limit Text
   */
  public StyledText getUpperLimitTxt() {
    return this.upperLimitTxt;
  }


  /**
   * @return the ref val text
   */
  public StyledText getRefValueTxt() {
    return this.refValueTxt;
  }


  /**
   * @return the manual button
   */
  public Button getManual() {
    return this.manual;
  }


  /**
   * @return the automatic button
   */
  public Button getAutomatic() {
    return this.automatic;
  }


  // Icdm-548
  @Override
  public void setActive(final boolean active) {
    this.paramTableViewer.refresh();
    setGridTableSelection();
    super.setActive(active);
  }

  /**
   *
   */
  private void setGridTableSelection() {
    if (this.editor.getEditorInput().getCdrReviewResultParam() != null) {
      this.paramTableViewer
          .setSelection(new StructuredSelection(this.editor.getEditorInput().getCdrReviewResultParam()));
    }
    else if ((null != this.resultData.getResultBo().getParameters()) &&
        !this.resultData.getResultBo().getParameters().isEmpty()) {
      this.paramTableViewer
          .setSelection(new StructuredSelection(this.resultData.getResultBo().getParameters().first()));
    }

    if (this.resultParameterFilter != null) {
      filterOutlineSelection(this.editor.getOutlinePageCreator().getOutlinePageSelection());
    }
  }


  /**
   * Icdm-611 selection of the Icdm file in the table viewer
   */
  private void setSelectedIcdmFiles() {
    final IStructuredSelection selection =
        (IStructuredSelection) ReviewResultParamDetPage.this.filesTabViewer.getSelection();
    ReviewResultParamDetPage.this.selectedIcdmFile = new ArrayList<>();
    if (selection != null) {
      final List<?> list = selection.toList();
      for (Object tabViewerSelection : list) {
        if (tabViewerSelection instanceof RvwFile) {
          ReviewResultParamDetPage.this.selectedIcdmFile.add((RvwFile) tabViewerSelection);
        }

      }

    }
  }

  /**
   * @param comp
   * @param widthHint
   * @param isEditable
   * @return
   */
  private StyledText createStyledTextField(final Composite comp, final int widthHint, final boolean isEditable) {
    StyledText styledTxt = new StyledText(comp, SWT.SINGLE | SWT.BORDER);
    styledTxt.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(widthHint));
    styledTxt.setEditable(isEditable);
    return styledTxt;
  }

  /**
   *
   */
  private void setGraphTable() {
    addChkValToMap();
    addParChkValToMap();
    addParRefValToMap();
    addRefValToMap();
    displayTableGraphCom();
  }


  /**
   * when the editor is opened , if parent ref value is selected , add to grph map
   */
  private void addParRefValToMap() {
    if ((ReviewResultParamDetPage.this.parRefValButton != null) &&
        ReviewResultParamDetPage.this.parRefValButton.getSelection()) {
      CalData parCheckVal =
          ReviewResultParamDetPage.this.resultData.getParentRefVal(ReviewResultParamDetPage.this.cdrResultParameter);
      if ((null != parCheckVal) &&
          ReviewResultParamDetPage.this.resultData.isRefValChanged(ReviewResultParamDetPage.this.cdrResultParameter)) {
        this.calDataColorMap.put(parCheckVal, ParamTypeColor.PARENT_REF_VALUE);
      }
    }
  }


  /**
   * when the editor is opened , if parent chk value is selected , add to grph map
   */
  private void addParChkValToMap() {
    if ((ReviewResultParamDetPage.this.parChkValButton != null) &&
        ReviewResultParamDetPage.this.parChkValButton.getSelection()) {
      CalData parCheckVal = ReviewResultParamDetPage.this.resultData
          .getParentCheckedVal(ReviewResultParamDetPage.this.cdrResultParameter);
      if ((null != parCheckVal) && ReviewResultParamDetPage.this.resultData
          .isCheckedValueChanged(ReviewResultParamDetPage.this.cdrResultParameter)) {
        this.calDataColorMap.put(parCheckVal, ParamTypeColor.PARENT_CHECK_VALUE);
      }
    }
  }


  /**
   * when the editor is opened , if ref val is selected , add to grph map
   */
  private void addRefValToMap() {
    if (ReviewResultParamDetPage.this.refButton.getSelection()) {
      CalData calData2 =
          ReviewResultParamDetPage.this.resultData.getRefValueObj(ReviewResultParamDetPage.this.cdrResultParameter);
      if (calData2 != null) {
        this.calDataColorMap.put(calData2, ParamTypeColor.REF_VALUE);
      }
    }
  }


  /**
   * when the editor is opened , if chk value is selected , add to grph map
   */
  private void addChkValToMap() {
    if (ReviewResultParamDetPage.this.chkValButton.getSelection()) {
      CalData calData1 =
          ReviewResultParamDetPage.this.resultData.getCheckedValueObj(ReviewResultParamDetPage.this.cdrResultParameter);
      if (calData1 != null) {
        this.calDataColorMap.put(calData1, ParamTypeColor.CHECK_VALUE);
      }
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    if (null == this.toolBarManager) {
      this.toolBarManager = new ToolBarManager(SWT.FLAT);
    }
    return this.toolBarManager;
  }


  /**
   * @param parCheckVal
   */
  private void setParentCheckVal(final CalData parCheckVal) {
    if (ReviewResultParamDetPage.this.parChkValButton.getSelection()) {
      if (ReviewResultParamDetPage.this.resultData
          .isCheckedValueChanged(ReviewResultParamDetPage.this.cdrResultParameter)) {
        ReviewResultParamDetPage.this.calDataColorMap.put(parCheckVal, ParamTypeColor.PARENT_CHECK_VALUE);
      }
    }
    else {
      ReviewResultParamDetPage.this.calDataColorMap.remove(parCheckVal);
    }
  }


  /**
   * @param parRefVal
   */
  private void setParRefVal(final CalData parRefVal) {
    if (ReviewResultParamDetPage.this.parRefValButton.getSelection()) {
      if (ReviewResultParamDetPage.this.resultData.isRefValChanged(ReviewResultParamDetPage.this.cdrResultParameter)) {
        ReviewResultParamDetPage.this.calDataColorMap.put(parRefVal, ParamTypeColor.PARENT_REF_VALUE);
      }
    }
    else {
      ReviewResultParamDetPage.this.calDataColorMap.remove(parRefVal);
    }
  }


  /**
   * @return the resultData
   */
  public ReviewResultClientBO getResultData() {
    return this.resultData;
  }


}
