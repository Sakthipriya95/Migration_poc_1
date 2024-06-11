/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.actions.CdrActionSet;
import com.bosch.caltool.cdr.ui.actions.QuesRespStatusToolBarActionSet;
import com.bosch.caltool.cdr.ui.sorters.QuesRespListTableSorter;
import com.bosch.caltool.cdr.ui.table.filters.QuesRespStatusToolBarFilters;
import com.bosch.caltool.cdr.ui.table.filters.RvwQnaireRespFilter;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultBO;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespStatusData;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author mkl2cob
 */
public class QnaireRespListDialog extends AbstractDialog {

  /**
   *
   */
  private static final String INFORMATION = "Information";
  /**
   *
   */
  private static final String VARIANT = "Variant";
  /**
   * Add new user button instance
   */
  protected Button openBtn;
  /**
   * GridTableViewer instance for add new user
   */
  protected GridTableViewer tabViewer;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;

  /**
   * Width hint for UI controls
   */
  private static final int WIDTH_HINT = 250;
  /**
   * Set<QnaireDataForReview>
   */
  private final Set<QnaireRespStatusData> qnaireDataSet;

  /**
   * filter class to handle logic for type filter
   */
  private RvwQnaireRespFilter qnaireDataFilter;

  /**
   * list of selection
   */
  private List<QnaireRespStatusData> selectedQnaireResponses;
  /**
   * sorter class
   */
  private QuesRespListTableSorter sorters;
  /**
   * show only unfilled questionnaire responses
   */
  private final boolean showOnlyUnfilledQues;
  /**
   * true, if the dialog is opened from CDR Review report
   */
  private boolean isCdrReport = false;
  /**
   * true, if the dialog is opened from Compare Hex with CDFx report
   */
  private boolean isCompHex = false;
  /**
   * true, if the dialog is opened from Review result
   */
  private boolean isRvwResult = false;
  /**
   */
  private ReviewResultBO rvwResultBo = null;

  private String name;
  private CdrReportDataHandler cdrReportHandler;
  private CompHexWithCDFxDataHandler compHexHandler;
  private String paramName;


  /**
   * @param parentShell Shell
   * @param set Set<QnaireDataForReview>
   * @param reviewResultBO ReviewResultBO
   * @param showOnlyUnfilledQues true if filter for unfilled questionnaires need to be applied
   * @param isRvwResult true, if the dialog is opened from Review result
   */
  public QnaireRespListDialog(final Shell parentShell, final Set<QnaireRespStatusData> set,
      final ReviewResultBO reviewResultBO, final boolean showOnlyUnfilledQues, final boolean isRvwResult) {
    super(parentShell);
    // remove Default WP from set and assign the value
    this.qnaireDataSet = new HashSet<>(removeDefaultWPQnaire(set));
    this.showOnlyUnfilledQues = showOnlyUnfilledQues;
    this.rvwResultBo = reviewResultBO;
    this.isRvwResult = isRvwResult;
  }

  /**
   * @param parentShell Shell
   * @param set Set<QnaireDataForReview>
   * @param paramName Parameter name
   * @param cdrReportHandler cdrReportHandler
   * @param compHexHandler compHexHandler
   * @param showOnlyUnfilledQues true if filter for unfilled questionnaires need to be applied
   * @param isCdrReport true, if the dialog is opened from CDR Review report
   * @param isCompHex true, if the dialog is opened from Compare Hex with CDFx report
   */
  public QnaireRespListDialog(final Shell parentShell, final Set<QnaireRespStatusData> set, final String paramName,
      final CdrReportDataHandler cdrReportHandler, final CompHexWithCDFxDataHandler compHexHandler,
      final boolean showOnlyUnfilledQues, final boolean isCdrReport, final boolean isCompHex) {
    super(parentShell);
    // remove Default WP from set and assign the value
    this.qnaireDataSet = new HashSet<>(removeDefaultWPQnaire(set));
    this.showOnlyUnfilledQues = showOnlyUnfilledQues;
    this.isCdrReport = isCdrReport;
    this.isCompHex = isCompHex;
    this.paramName = paramName;
    this.cdrReportHandler = cdrReportHandler;
    this.compHexHandler = compHexHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE | SWT.MAX);
    setBlockOnOpen(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.openBtn = createButton(parent, IDialogConstants.OK_ID, "Open in Editor", false);
    this.openBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // set the title
    if (this.isCdrReport) {
      setTitle("Questionnaire Responses for the CDR Report");
    }
    if (this.isCompHex) {
      setTitle("Questionnaire Responses for Compare HEX with latest reviewed Data");
    }
    if (this.isRvwResult) {
      setTitle("Questionnaire Responses for Review");
    }
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Questionnaire Responses");
    super.configureShell(newShell);
    // ICDM-153
    super.setHelpAvailable(true);
  }

  /**
   * Creates the gray area
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    initializeDialogUnits(parent);
    // create top composite
    this.composite = new Composite(parent, SWT.NONE);
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.composite.setLayout(new GridLayout());
    // create sections
    createInfoSection();
    createQnaireListSection();
    // to update the layouts
    this.composite.layout(true, true);

    return parent;
  }


  /**
   * This method initializes formToolkit
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
   * This method creates the Information section
   */
  private void createInfoSection() {
    GridData gridData = GridDataUtil.getInstance().getGridData();

    Section infoSection =
        getFormToolkit().createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    infoSection.setExpanded(true);
    infoSection.getDescriptionControl().setEnabled(false);

    Composite infoComp = getFormToolkit().createComposite(infoSection);
    infoComp.setLayout(new GridLayout(2, false));
    infoComp.setLayoutData(gridData);

    String textValue = "";
    String variantValue = "";
    if (this.isRvwResult) {
      // review result
      this.name = "Review result";
      textValue = this.rvwResultBo.getCDRResult().getName();
      variantValue = CommonUtils.isNotNull(this.rvwResultBo.getReviewResultClientBO().getVarFromPidTree())
          ? this.rvwResultBo.getReviewResultClientBO().getVarFromPidTree().getVariantName() : CDRConstants.NO_VARIANT;
    }
    else {
      // compareHex or Data review report
      this.name = "Parameter";
      textValue = this.paramName;
      variantValue = CommonUtils.isNotNull(this.cdrReportHandler.getPidcVariant())
          ? this.cdrReportHandler.getPidcVariant().getName() : CDRConstants.NO_VARIANT;
      createA2lNameUIControl(infoComp);
    }
    if (this.isCompHex) {
      // To display Hex or DST name in case of CompareHEx report
      String vcdmDstOrHex = "";
      if (CommonUtils.isEmptyString(this.compHexHandler.getCompHexResponse().getHexFileName())) {
        vcdmDstOrHex = this.compHexHandler.getCompHexResponse().getVcdmDst();
        getFormToolkit().createLabel(infoComp, "Vcdm Dst");
      }
      else {
        vcdmDstOrHex = this.compHexHandler.getCompHexResponse().getHexFileName();
        getFormToolkit().createLabel(infoComp, "Hex File");
      }
      // create control to display hex or vcdm dst
      Text vcdmDstOrHexText = new Text(infoComp, SWT.BORDER);
      vcdmDstOrHexText.setEditable(false);
      vcdmDstOrHexText.setText(vcdmDstOrHex);
      vcdmDstOrHexText.setLayoutData(GridDataUtil.getInstance().getTextGridData());

    }
    // create controls
    createUIControls(infoComp, textValue, variantValue);
    infoSection.setText(INFORMATION);
    infoSection.setLayoutData(gridData);
    infoSection.setLayout(new GridLayout());
    infoSection.setClient(infoComp);
    // set size from the child composite
    infoSection.setSize(infoComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));

  }

  /**
   * @param formComposite
   */
  private void createA2lNameUIControl(final Composite formComposite) {
    getFormToolkit().createLabel(formComposite, "A2L File");
    // control to display A2L File
    Text a2lFileName = new Text(formComposite, SWT.BORDER);
    a2lFileName.setEditable(false);
    a2lFileName.setText(this.cdrReportHandler.getPidcA2l().getName());
    a2lFileName.setLayoutData(GridDataUtil.getInstance().getTextGridData());


  }

  /**
   * @param formComposite
   * @param variantValue
   * @param textValue
   */
  private void createUIControls(final Composite formComposite, final String textValue, final String variantValue) {
    getFormToolkit().createLabel(formComposite, this.name);
    // create control to display review result name in case of review result , else parameter name
    Text rvwResNameText = new Text(formComposite, SWT.BORDER);
    rvwResNameText.setEditable(false);
    rvwResNameText.setText(textValue);
    rvwResNameText.setLayoutData(GridDataUtil.getInstance().getTextGridData());

    getFormToolkit().createLabel(formComposite, VARIANT);
    // create control to display variant name
    Text variantNameText = new Text(formComposite, SWT.BORDER);
    variantNameText.setEditable(false);
    variantNameText.setText(variantValue);
    variantNameText.setLayoutData(GridDataUtil.getInstance().getTextGridData());


  }

  /**
   * This method initializes section
   */
  private void createQnaireListSection() {
    this.section = this.formToolkit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setExpanded(true);
    this.section.setText("List of Questionnaire Responses");
    createForm();
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    this.section.setLayoutData(gridData);
    this.section.setLayout(new GridLayout());
    this.section.getDescriptionControl().setEnabled(false);
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    // Create tableviewer
    createTabViewer();
    // Set ContentProvider and LabelProvider to addNewUserTableViewer
    setTabViewerProviders();
    // Set input to the addNewUserTableViewer
    setTabViewerInput();
    // tool bar actions
    createToolBarAction();
    // Add selection listener to the addNewUserTableViewer
    addTableSelectionListener();
    // Adds double click selection listener to the addNewUserTableViewer
    addDoubleClickListener();
    // Invokde GridColumnViewer sorter
    this.form.getBody().setLayout(new GridLayout());
  }

  /**
   * create tool bar actions
   */
  private void createToolBarAction() {
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.section);
    QuesRespStatusToolBarFilters toolBarFilters = new QuesRespStatusToolBarFilters();
    QuesRespStatusToolBarActionSet toolBarActionSet = new QuesRespStatusToolBarActionSet();
    toolBarActionSet.createPositiveAnsweredFilterAction(toolBarManager, toolBarFilters, this.tabViewer,
        this.showOnlyUnfilledQues);
    toolBarActionSet.createNegativeAnsweredAction(toolBarManager, toolBarFilters, this.tabViewer,
        this.showOnlyUnfilledQues);
    toolBarActionSet.createNotAnsweredFilterAction(toolBarManager, toolBarFilters, this.tabViewer);
    if (!this.isRvwResult) {
      toolBarActionSet.createNotBaselinedQnaireRespFilterAction(toolBarManager, toolBarFilters, this.tabViewer);
    }
    this.tabViewer.addFilter(toolBarFilters);
    toolBarManager.update(true);

    this.section.setTextClient(toolbar);


  }

  /**
   * add double click listener
   */
  private void addDoubleClickListener() {
    this.tabViewer.addDoubleClickListener(value -> openResponseEditors());
  }

  /**
   * open ques response editors for selection
   */
  private void openResponseEditors() {
    CdrActionSet cdrActionSet = new CdrActionSet();
    if (null != this.selectedQnaireResponses) {
      this.selectedQnaireResponses
          .forEach(selection -> cdrActionSet.openQuesRespEditorWithId(selection.getQuesRespId()));
    }
  }

  /**
   * selection listener
   */
  private void addTableSelectionListener() {
    this.tabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) QnaireRespListDialog.this.tabViewer.getSelection();
        if (selection == null) {
          QnaireRespListDialog.this.openBtn.setEnabled(false);
        }
        else {
          QnaireRespListDialog.this.selectedQnaireResponses = selection.toList();
          QnaireRespListDialog.this.openBtn.setEnabled(true);
        }
      }
    });

  }

  /**
   * This method sets the input to the addNewUserTableViewer
   */
  private void setTabViewerInput() {
    this.tabViewer.setInput(this.qnaireDataSet);
  }

  /**
   * @param qnaireDataSet
   * @return filtered data
   */
  private Set<QnaireRespStatusData> removeDefaultWPQnaire(final Set<QnaireRespStatusData> qnaireDataSetInput) {
    return qnaireDataSetInput.stream().filter(data -> !data.getWpName().equals(ApicConstants.DEFAULT_A2L_WP_NAME))
        .collect(Collectors.toSet());
  }

  /**
   * This method sets ContentProvider & LabelProvider to the addNewUserTableViewer
   */
  private void setTabViewerProviders() {
    this.tabViewer.setContentProvider(ArrayContentProvider.getInstance());

  }

  /**
   * This method creates the addNewUserTableViewer
   *
   * @param gridData
   */
  private void createTabViewer() {

    createTypeFilter();
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

    this.tabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, gridData);
    // to display 10 rows
    int itemHeight = this.tabViewer.getGrid().getItemHeight();
    gridData.heightHint = 12 * itemHeight;
    this.sorters = new QuesRespListTableSorter();
    // Create GridViewerColumns
    createGridViewerColumns();
    this.tabViewer.addFilter(this.qnaireDataFilter);
    this.tabViewer.setComparator(this.sorters);
  }

  /**
   * type filter to filter table rows based on qnaire response name
   */
  private void createTypeFilter() {
    this.qnaireDataFilter = new RvwQnaireRespFilter();
    Text filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "type filter text");

    filterTxt.addModifyListener(event -> {
      // when there is a modification in the text filter, set the text to the filter
      final String text = filterTxt.getText().trim();
      this.qnaireDataFilter.setFilterText(text);
      // refresh to filter the table based on table
      this.tabViewer.refresh();
    });
    // set focus to type filter
    filterTxt.setFocus();

  }

  /**
   * create table columns
   */
  private void createGridViewerColumns() {
    createWPNameCol();
    createRespNameCol();
    createQuesNameCol();
    createVariantNameCol();
    createStatusColumn();
    if (!this.isRvwResult) {
      createRevisionColumn();
    }
  }

  /**
   *
   */
  private void createRevisionColumn() {
    final GridViewerColumn revisionColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tabViewer, "Revision:Version Name", 100);

    revisionColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return ((QnaireRespStatusData) element).getRevisionNum() + CDRConstants.REV_COL_COLON +
            ((QnaireRespStatusData) element).getVersionName();
      }
    });

    revisionColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(revisionColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_5, this.sorters, this.tabViewer));

  }

  /**
   * ques response status column
   */
  private void createStatusColumn() {
    ColumnViewerToolTipSupport.enableFor(this.tabViewer, ToolTip.NO_RECREATE);
    final GridViewerColumn statusColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tabViewer, "Status", 50);

    statusColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public Image getImage(final Object element) {
        String status = ((QnaireRespStatusData) element).getStatus();
        if (CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(status) == CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE) {
          return ImageManager.getInstance().getRegisteredImage(ImageKeys.ALL_16X16);
        }
        if ((CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(status) == CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE) ||
            (CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(status) == CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE)) {
          return ImageManager.getInstance().getRegisteredImage(ImageKeys.ORANGE_EXCLAMATION_ICON_16X16);
        }
        return ImageManager.getInstance().getRegisteredImage(ImageKeys.REMOVE_16X16);
      }

      @Override
      public String getToolTipText(final Object element) {
        String status = ((QnaireRespStatusData) element).getStatus();
        return CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(status).getUiType();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return "";
      }
    });
    statusColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(statusColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_4, this.sorters, this.tabViewer));
  }

  /**
   * variant name column
   */
  private void createVariantNameCol() {
    final GridViewerColumn varNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tabViewer, "Linked from Variant", 100);

    varNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return ((QnaireRespStatusData) element).getPrimaryVarName();
      }
    });

    varNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(varNameColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_3, this.sorters, this.tabViewer));
  }

  /**
   * responsibility name column
   */
  private void createRespNameCol() {
    final GridViewerColumn respNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tabViewer, "Responsibility", 200);

    respNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return ((QnaireRespStatusData) element).getRespName();
      }

    });
    respNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(respNameColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_1, this.sorters, this.tabViewer));
  }

  /**
   * workpackage name column
   */
  private void createWPNameCol() {
    final GridViewerColumn wpNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tabViewer, "Workpackage", 100);

    wpNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return ((QnaireRespStatusData) element).getWpName();
      }
    });
    wpNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(wpNameColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_0, this.sorters, this.tabViewer));
  }

  /**
   * questionnaire name column
   */
  private void createQuesNameCol() {
    final GridViewerColumn quesRespNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tabViewer, "Questionnaire Response", WIDTH_HINT);

    quesRespNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return ((QnaireRespStatusData) element).getQnaireRespName();
      }

    });
    quesRespNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        quesRespNameColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_2, this.sorters, this.tabViewer));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    openResponseEditors();
    super.okPressed();
  }
}
