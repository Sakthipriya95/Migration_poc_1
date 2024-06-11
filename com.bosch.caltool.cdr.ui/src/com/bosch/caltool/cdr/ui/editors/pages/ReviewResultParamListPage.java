/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;


import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.columnChooser.command.DisplayColumnChooserCommandHandler;
import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommand;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditBindings;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.event.StructuralRefreshEvent;
import org.eclipse.nebula.widgets.nattable.painter.cell.ComboBoxPainter;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellEditorMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.AcceptShapeCheckResultAction;
import com.bosch.caltool.cdr.ui.actions.CDFXFileExportAction;
import com.bosch.caltool.cdr.ui.actions.CdrActionSet;
import com.bosch.caltool.cdr.ui.actions.QuesRespListAction;
import com.bosch.caltool.cdr.ui.actions.ReviewCommentContextMenu;
import com.bosch.caltool.cdr.ui.actions.ReviewResultLockUnLockAction;
import com.bosch.caltool.cdr.ui.actions.ReviewResultMarkWorkPackageAsFinishedAction;
import com.bosch.caltool.cdr.ui.actions.ReviewResultNATActionSet;
import com.bosch.caltool.cdr.ui.actions.ReviewResultNATToolBarActionSet;
import com.bosch.caltool.cdr.ui.actions.ReviewResultOpenDecimalPreferencesWindowAction;
import com.bosch.caltool.cdr.ui.actions.ReviewScoreContextMenu;
import com.bosch.caltool.cdr.ui.dialogs.QnaireRespListDialog;
import com.bosch.caltool.cdr.ui.dialogs.SecondaryResultConsDialog;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditor;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditorInput;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.CDRResultNatToolTip;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.ResultColHeaderLabelAccumulator;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.ResultParamNatInputToColConverter;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.ResultValidationEditConfiguration;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.ReviewResultColumnFilterMatcher;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.ReviewResultLabelAccumulator;
import com.bosch.caltool.cdr.ui.table.filters.CdrTreeViewerFilter;
import com.bosch.caltool.cdr.ui.table.filters.ReviewResultToolBarFilters;
import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultBO;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO.SortColumns;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.actions.CDMCommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.natsupport.CustomSortFilterGroupEnabler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QS_STATUS_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.SR_ACCEPTED_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RvwCommentTemplate;
import com.bosch.caltool.icdm.model.cdr.RvwParametersSecondary;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespStatusData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespVersionData;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewActionSet;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleActionSet;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireRespVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;
import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomGroupByDataLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * NAT table implementation for CDR Review Result summary page
 *
 * @author adn1cob
 */
public class ReviewResultParamListPage extends AbstractGroupByNatFormPage implements ISelectionListener {

  /**
   * Total number of columns in the page . Should be changed on addition of columns
   */
  private static final int COLUMN_COUNT = 34;
  /**
   *
   */
  private static final String COMMENT_HEADER_LABEL = "Comment";
  /**
   * Review result comment header label
   */
  private static final String REVIEW_RESULT_COMMENT_HEADER_LABEL = "Comment / Reason for Rule Violation";
  /**
   * Form tool kit instance
   */
  private FormToolkit formToolkit;
  /**
   * Non scrollable form instance
   */
  private Form nonScrollableForm;
  /**
   * Main composite instance
   */
  private SashForm mainComposite;
  /**
   * Composite two instance
   */
  private Composite composite;
  /**
   * Tree viewer instance
   */
  private TreeViewer viewer;
  /**
   * Table section instance
   */
  private Section tableSection;

  /**
   * Table form instance
   */
  private Form tableForm;
  /**
   * Tool bar filters instance
   */
  private ReviewResultToolBarFilters toolBarFilters;
  /**
   * Filter text instance
   */
  private Text filterTxt;
  /**
   * editor instance
   */
  private final ReviewResultEditor editor;
  /**
   * CDR Result instance
   */
  private final CDRReviewResult cdrResult;
  /**
   * Nat table filter matcher
   */
  private ReviewResultColumnFilterMatcher<CDRResultParameter> allColumnFilterMatcher;
  /**
   * Row selection provide
   */
  private RowSelectionProvider<CDRResultParameter> selectionProvider;
  /**
   * NAT table grid layer
   */
  private CustomFilterGridLayer paramFilterGridLayer;
  /**
   * Nat table instance
   */
  private CustomNATTable natTable;
  /**
   * Total table row count
   */
  private int totTableRowCount;
  /**
   * sorted input
   */
  private SortedSet<CDRResultParameter> sortedParamSet;
  /**
   * CUSTOM_COMPARATOR Label
   */
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  /**
   * Tool bar manager instance
   */
  private ToolBarManager toolBarManager;
  /**
   * Tree filter
   */
  private CdrTreeViewerFilter treeViewerFilter;

  /**
   * Column header configuration
   */
  private ResultColHeaderLabelAccumulator columnLabelAccumulator;
  private ReviewResultLockUnLockAction lockAction;

  // Column numbers
  /**
   * Column to display Ssd class type
   */
  public static final int SSD_CLASS_COL_NUMBER = 0;
  public static final int PARAMETER_COL_NUMBER = 1;
  public static final int LONG_NAME_COL_NUMBER = 2;
  // 496338 - Add WP, Resp columns in NAT table in Review Result Editor
  public static final int WORKPACKAGE_COL_NUMBER = 3;

  public static final int RESPONSIBILITY_COL_NUMBER = 4;

  public static final int RESP_TYPE_COL_NUMBER = 5;

  public static final int TYPE_COL_NUMBER = 6;


  public static final int CLASS_COL_NUMBER = 7;
  public static final int CODEWORD_COL_NUMBER = 8;
  /**
   * Bitwise Column number
   */
  public static final int BITWISE_COL_NUMBER = 9;
  public static final int PARAM_HINT_COL_NUMBER = 10;
  public static final int FC_NAME_COL_NUMBER = 11;
  /**
   * Column number for lower limit value
   */
  public static final int LOWER_LIMIT_COL_NUMBER = 12;
  /**
   * Column number for reference value
   */
  public static final int REF_VAL_COL_NUMBER = 13; // used in other class
  /**
   * Column number for reference value unit
   */
  // ICDM-2151
  public static final int REF_VAL_UNIT_COL_NUMBER = 14;
  /**
   * Maturity level column number
   */
  public static final int MATURITY_LEVEL_COL_NUMBER = 15;
  /**
   * Match Ref Flag i.e. exact match column number which is Dcm2ssd in review rule
   */
  public static final int EXACT_MATCH_COL_NUMBER = 16;
  /**
   * Column number for upper limit value
   */
  public static final int UPPER_LIMIT_COL_NUMBER = 17;
  /**
   * Column number for bitlimit
   */
  public static final int BIT_LIMIT_COL_NUMBER = 18;
  /**
   * Column number for review method-Automatic and manual which is ready for series
   */
  public static final int READY_FOR_SERIES_COL_NUMBER = 19;
  /**
   * Column number for check value
   */
  public static final int CHECK_VALUE_COL_NUMBER = 20;
  /**
   * Column number for check value unit
   */
  // ICDM-2151
  public static final int CHECK_VALUE_UNIT_COL_NUMBER = 21;
  /**
   * Column number for imported value.
   */
  public static final int IMP_VALUE_COL_NUMBER = 22;
  /**
   * Column number for result
   */
  public static final int RESULT_COL_NUMBER = 23;

  // Task 231286
  /**
   * Column number for secondary result
   */
  public static final int SEC_RESULT_COL_NUMBER = 24;

  /**
   * Column number for score
   */
  public static final int SCORE_COL_NUMBER = 25; // used in other class
  /**
   * Column number for score description
   */
  public static final int SCORE_DESC_COL_NUMBER = 26;
  /**
   * Column number for comments
   */
  public static final int COMMENT_COL_NUMBER = 27; // used in other class
  /**
   * Column number for CDFX status number
   */
  public static final int CDFX_STATUS_COL_NUMBER = 28;
  /**
   * Column number for CDFX user
   */
  public static final int CDFX_USER_COL_NUMBER = 29;
  /**
   * Column number for CDFX Date
   */
  public static final int CDFX_DATE_COL_NUMBER = 30;
  /**
   * Column number for CDFX WP
   */
  public static final int CDFX_WP_COL_NUMBER = 31;
  /**
   * Column number for CDFX Project
   */
  public static final int CDFX_PROJECT_COL_NUMBER = 32;
  /**
   * Column CDFX target variant column
   */
  public static final int CDFX_TARGET_VAR_COL_NUMBER = 33;
  /**
   * Column CDFX test object
   */
  public static final int CDFX_TEST_OBJ_COL_NUMBER = 34;
  /**
   * Column CDFX Program ID
   */
  public static final int CDFX_PROGRAM_ID_COL_NUMBER = 35;
  /**
   * Column CDFX DATA
   */
  public static final int CDFX_DATA_COL_NUMBER = 36;
  /**
   * Column for CDFX remark
   */
  public static final int CDFX_REMARK_COL_NUMBER = 37;
  /**
   * Column for ARC Realeased Flag
   */
  public static final int ARC_RELEASED_COL_NUMBER = 38;
  /**
   * Column number for Parent Reference Value
   */
  public static final int PARENT_REF_VALUE_COL_NUMBER = 39;
  /**
   * Column number for Parent check value
   */
  public static final int PARENT_CHECK_VALUE_COL_NUMBER = 40;


  private Map<Integer, String> propertyToLabelMap;
  private GroupByHeaderLayer groupByHeaderLayer;
  private boolean resetState;

  /**
   * the cell editor mouse event matcher for the ui binding when double clicking on a cell
   */
  // 226387
  private final CellEditorMouseEventMatcher cellEditorMouseEventMatcher = new CellEditorMouseEventMatcher();
  /**
   * Selected cell's column position
   */
  protected int selectedColPostn;
  /**
   * Selected cell's row position
   */
  protected int selectedRowPostn;


  /**
   * @return the tableSection
   */
  public Section getTableSection() {
    return this.tableSection;
  }

  ReviewResultClientBO resultData;

  /**
   * flag to know whether all questionnaire responses are filled
   */
  private boolean quesNotFilled;
  /**
   * questionnaire status label
   */
  private Label quesStatusLbl;
  private Composite quesStatusComp;
  private Link openQnaireRespdialogLink;

  private ReviewResultOpenDecimalPreferencesWindowAction rvwResultDecimalPrefAction;


  /**
   * @return the resultData
   */
  public ReviewResultClientBO getResultData() {
    return this.resultData;
  }

  /**
   * Constructor
   *
   * @param editor editor instance
   */
  public ReviewResultParamListPage(final FormEditor editor) {
    super(editor, "ReviewResult", "Review Results");
    this.editor = (ReviewResultEditor) editor;
    this.cdrResult = this.editor.getEditorInput().getReviewResult();
    this.resultData = this.editor.getEditorInput().getResultData();
  }

  @Override
  public void createPartControl(final Composite parent) {

    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);

    this.nonScrollableForm.setText("Review Result");
    this.nonScrollableForm.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
    createOrUpdateQuesStatusLbl();

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;

    this.mainComposite = new SashForm(this.nonScrollableForm.getBody(), SWT.HORIZONTAL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(gridData);

    final ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
    if (CommonUtils.isNotNull(this.resultData.getResultBo().getDeltaReviewType())) {
      openWarnMessageDialog();
    }

  }

  /**
   * @return true if the review is monica review / false for other reviews
   */
  public boolean isMonicaReview() {
    return CDRConstants.CDR_SOURCE_TYPE.MONICA_FILE.getUIType()
        .equals(getResultData().getResultBo().getCDRResult().getGrpWorkPkg());
  }

  /**
   * create or update questionnaire response status in title
   */
  private void createOrUpdateQuesStatusLbl() {
    REVIEW_TYPE reviewType = getResultData().getResultBo().getReviewType();
    // Questionnaire status is applicable for Start and Official Reviews
    if ((reviewType != REVIEW_TYPE.TEST) && !isMonicaReview()) {
      try {
        GridLayout quesStatusGrid = new GridLayout(2, false);
        if ((CommonUtils.isNull(this.quesStatusComp)) && (CommonUtils.isNull(this.quesStatusLbl)) &&
            CommonUtils.isNull(this.openQnaireRespdialogLink)) {
          this.quesStatusComp = new Composite(this.nonScrollableForm.getBody(), SWT.NONE);
          this.quesStatusComp.setLayout(quesStatusGrid);
          this.quesStatusLbl = LabelUtil.getInstance().createLabel(this.quesStatusComp, "");
          this.openQnaireRespdialogLink = new Link(this.quesStatusComp, SWT.NONE);
          this.openQnaireRespdialogLink.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(final SelectionEvent arg0) {
              QnaireRespListDialog qnaireRespListDialog = new QnaireRespListDialog(
                  Display.getCurrent().getActiveShell(), getResultData().getResponse().getQnaireDataForRvwSet(),
                  getResultData().getResultBo(), true, true);
              qnaireRespListDialog.open();
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent arg0) {
              // NA
            }
          });
        }
        if (areQnairesNotFilled(getResultData().getResponse().getQnaireDataForRvwSet())) {
          this.quesStatusComp.setLayout(quesStatusGrid);
          setQnaireNotAnswdWarnMsg();
          return;
        }
        this.quesStatusLbl.setText("");
        this.openQnaireRespdialogLink.setText("");
      }
      catch (ApicWebServiceException ex) {
        CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @throws ApicWebServiceException
   */
  private void setQnaireNotAnswdWarnMsg() throws ApicWebServiceException {
    quesStatusLabel(new CommonDataBO().getMessage("REVIEW_RESULT", "QUES_MSG_IN_RVW_RESULT"));
    this.openQnaireRespdialogLink.setText(CdrUIConstants.SHOW_UNFILLED_QNAIRES_HYPERLINK);
  }

  /**
   * @param introText2
   * @throws ApicWebServiceException
   */
  private void quesStatusLabel(final String introText2) {
    this.quesStatusLbl.setText(introText2);
    this.quesStatusLbl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    Font boldFont =
        new Font(this.quesStatusLbl.getDisplay(), new FontData(GUIHelper.DEFAULT_FONT.toString(), 10, SWT.LINE_SOLID));
    this.quesStatusLbl.setFont(boldFont);
  }


  /**
   * @param qnaireDataForRvwSet
   * @return
   */
  private boolean areQnairesNotFilled(final Set<QnaireRespStatusData> qnaireDataForRvwSet) {
    this.quesNotFilled = false;
    for (QnaireRespStatusData qnaireData : qnaireDataForRvwSet) {
      QS_STATUS_TYPE typeByDbCode = CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(qnaireData.getStatus());
      if (typeByDbCode == QS_STATUS_TYPE.NOT_ANSWERED) {
        this.quesNotFilled = true;
        break;
      }
    }

    return this.quesNotFilled;
  }

  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   *
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    createInnerComposites();
    getSite().getPage().addSelectionListener(this);
  }

  /**
   * This method initializes composite
   */
  private void createInnerComposites() {
    // Create composites
    createComposite();
  }

  /**
   * Method creates composite two controls
   */
  private void createComposite() {
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;
    gridData1.grabExcessVerticalSpace = true;
    gridData1.verticalAlignment = GridData.FILL;
    this.composite = new Composite(this.mainComposite, SWT.NONE);
    this.composite.setLayout(new GridLayout());
    createTableViewerSection();
    this.composite.setLayoutData(gridData1);
  }


  /**
   * Set the selected function in the editor input
   */
  protected void setSelectedFunction() {
    final IStructuredSelection selection = (IStructuredSelection) ReviewResultParamListPage.this.viewer.getSelection();
    if ((selection != null) && (!selection.isEmpty())) {
      final Object element = selection.getFirstElement();
      if (element instanceof CDRResultFunction) {
        final CDRResultFunction selectedFunc = (CDRResultFunction) element;
        ReviewResultParamListPage.this.editor.getEditorInput().setSelectedFunc(selectedFunc);
        ReviewResultParamListPage.this.editor.getEditorInput().setCdrReviewResultParam(null);
      }
    }

  }


  /**
   * Create table viewer section
   */
  private void createTableViewerSection() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.tableSection = SectionUtil.getInstance().createSection(this.composite, this.formToolkit);
    this.tableSection.setLayoutData(gridData);
    createTableForm(this.formToolkit);
    this.tableSection.setClient(this.tableForm);
    this.tableSection.getDescriptionControl().setEnabled(false);

  }

  /**
   * Set the selected parameter in the editor input
   */
  protected void setSelectedParam() {
    // Get CDR Result parameter
    IStructuredSelection selection =
        (IStructuredSelection) ReviewResultParamListPage.this.selectionProvider.getSelection();
    if ((selection != null) && (!selection.isEmpty())) {
      final Object element = selection.getFirstElement();
      if (element instanceof CDRResultParameter) {
        final CDRResultParameter selectedParam = (CDRResultParameter) element;
        ReviewResultParamListPage.this.editor.getEditorInput().setCdrReviewResultParam(selectedParam);
        ReviewResultParamListPage.this.editor.getEditorInput().setResultFunction(null);
      }
    }

  }

  /**
   * Create NAT Table form
   *
   * @param toolkit toolkit instance
   */
  private void createTableForm(final FormToolkit toolkit) {
    // create table form
    this.tableForm = toolkit.createForm(this.tableSection);
    // create filter text
    this.filterTxt = toolkit.createText(this.tableForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    createFilterTxt();
    // get parameters
    this.tableForm.getBody().setLayout(new GridLayout());
    this.sortedParamSet = this.resultData.getResultBo().getParameters();
    this.totTableRowCount = this.sortedParamSet.size();
    // ICDM-2389
    openLockMessageDialog();
    createTable();


  }


  /**
   *
   */
  private void createTable() {
    this.propertyToLabelMap = new HashMap<>();
    Map<Integer, Integer> columnWidthMap = new HashMap<>();

    // Configure columns
    configureColumnsNATTable(this.propertyToLabelMap, columnWidthMap);

    // NatInputToColumnConverter is used to convert A2LSystemConstantValues (which is given as input to nattable viewer)
    // to the respective column values
    AbstractNatInputToColumnConverter natInputToColumnConverter = new ResultParamNatInputToColConverter(this.editor);
    final IConfigRegistry configRegistry = new ConfigRegistry();
    // Group by model
    GroupByModel groupByModel = new GroupByModel();
    // iCDM-848, Select cols to be hidden by default
    List<Integer> colsToHide = new ArrayList<>();
    colsToHide.add(LONG_NAME_COL_NUMBER);
    colsToHide.add(WORKPACKAGE_COL_NUMBER);
    colsToHide.add(RESPONSIBILITY_COL_NUMBER);
    colsToHide.add(RESP_TYPE_COL_NUMBER);
    colsToHide.add(TYPE_COL_NUMBER);
    colsToHide.add(CLASS_COL_NUMBER);
    colsToHide.add(CODEWORD_COL_NUMBER);
    colsToHide.add(PARAM_HINT_COL_NUMBER);
    colsToHide.add(FC_NAME_COL_NUMBER);
    colsToHide.add(IMP_VALUE_COL_NUMBER);
    colsToHide.add(CDFX_USER_COL_NUMBER);
    colsToHide.add(CDFX_DATE_COL_NUMBER);
    colsToHide.add(CDFX_WP_COL_NUMBER);
    colsToHide.add(CDFX_PROJECT_COL_NUMBER);
    colsToHide.add(CDFX_TARGET_VAR_COL_NUMBER);
    colsToHide.add(CDFX_TEST_OBJ_COL_NUMBER);
    colsToHide.add(CDFX_PROGRAM_ID_COL_NUMBER);
    colsToHide.add(CDFX_DATA_COL_NUMBER);
    colsToHide.add(CDFX_REMARK_COL_NUMBER);
    colsToHide.add(SCORE_DESC_COL_NUMBER);
    colsToHide.add(MATURITY_LEVEL_COL_NUMBER);
    colsToHide.add(READY_FOR_SERIES_COL_NUMBER);
    colsToHide.add(EXACT_MATCH_COL_NUMBER);
    colsToHide.add(ARC_RELEASED_COL_NUMBER);
    colsToHide.add(PARENT_REF_VALUE_COL_NUMBER);
    colsToHide.add(PARENT_CHECK_VALUE_COL_NUMBER);

    // A Custom Filter Grid Layer is constructed
    this.paramFilterGridLayer = new CustomFilterGridLayer(configRegistry, this.sortedParamSet, this.propertyToLabelMap,
        columnWidthMap, getParamComparator(SortColumns.SORT_PARAM_NAME), natInputToColumnConverter, this, null,
        groupByModel, colsToHide, false, true, null, null, true);

    // Enable coloring for params using label accumalator
    DataLayer bodyDataLayer = this.paramFilterGridLayer.getBodyDataLayer();
    ReviewResultLabelAccumulator cellLabelAccumulator = new ReviewResultLabelAccumulator(bodyDataLayer, this);
    cellLabelAccumulator.setModifiable(this.resultData.getResultBo().isModifiable());
    bodyDataLayer.setConfigLabelAccumulator(cellLabelAccumulator);
    // Col header configuration
    this.columnLabelAccumulator =
        new ResultColHeaderLabelAccumulator(this.paramFilterGridLayer.getColumnHeaderDataLayer(), this);
    this.paramFilterGridLayer.getColumnHeaderDataLayer().setConfigLabelAccumulator(this.columnLabelAccumulator);


    // Enable Tool bar filters
    this.toolBarFilters = new ReviewResultToolBarFilters(this.resultData);
    this.paramFilterGridLayer.getComboGlazedListsFilterStrategy()
        .setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());
    // Enable column filters
    this.allColumnFilterMatcher = new ReviewResultColumnFilterMatcher<>(this);
    this.paramFilterGridLayer.getComboGlazedListsFilterStrategy()
        .setAllColumnFilterMatcher(this.allColumnFilterMatcher);
    // Enable tree filter
    this.treeViewerFilter = new CdrTreeViewerFilter(getResultData(), getDataHandler(), getParamFilterGridLayer());
    this.paramFilterGridLayer.getComboGlazedListsFilterStrategy()
        .setOutlineNatFilterMatcher(this.treeViewerFilter.getOutlineMatcher());
    // to consider row added due to ColumnGroupHeaderLayer in header region for calculating height set this flag to
    // false
    this.paramFilterGridLayer.getColumnGroupHeaderLayer().setCalculateHeight(false);

    // Composite grid layer
    CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
    this.groupByHeaderLayer = new GroupByHeaderLayer(groupByModel, this.paramFilterGridLayer,
        this.paramFilterGridLayer.getColumnHeaderDataProvider());
    compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, this.groupByHeaderLayer, 0, 0);
    compositeGridLayer.setChildLayer("Grid", this.paramFilterGridLayer, 0, 1);


    // Status bar message listener
    ReviewResultParamListPage.this.paramFilterGridLayer.getBodyDataLayer().getTreeRowModel()
        .registerRowGroupModelListener(() -> ReviewResultParamListPage.this
            .setStatusBarMessage(ReviewResultParamListPage.this.groupByHeaderLayer, false));

    // Create NAT table
    this.natTable = new CustomNATTable(this.tableForm.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        compositeGridLayer, false, this.getClass().getSimpleName(), this.propertyToLabelMap);

    try {
      this.natTable.setProductVersion(new CommonDataBO().getIcdmVersion());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    this.natTable.setComboBoxFilterHeaderEnabled(true);
    // Add configurations

    this.natTable.setLayoutData(getGridData());
    this.natTable.addConfiguration(new GroupByHeaderMenuConfiguration(this.natTable, this.groupByHeaderLayer));
    this.natTable.addConfiguration(new CustomNatTableStyleConfiguration());
    this.natTable.addConfiguration(new ResultValidationEditConfiguration());
    this.natTable.addConfiguration(new ReviewResultCopyPasteConfiguration(
        this.paramFilterGridLayer.getBodyLayer().getSelectionLayer(), this.natTable.getInternalCellClipboard(), this));


    this.natTable.addConfiguration(new SingleClickSortConfiguration());


    this.natTable.addConfiguration(getCustomComparatorConfiguration());

    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),

            new PopupMenuAction(super.createColumnHeaderMenu(ReviewResultParamListPage.this.natTable)
                .withColumnChooserMenuItem().withMenuItemProvider((final NatTable natTbl, final Menu popupMenu) -> {
                  MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
                  menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
                  menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
                  menuItem.setEnabled(true);
                  menuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                      ReviewResultParamListPage.this.resetState = true;
                      ReviewResultParamListPage.this.reconstructNatTable();
                    }
                  });
                }).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });
    this.natTable.addMouseListener(new MouseEventListener());
    addRightClickMenu();

    // Add configuration for editing comments directly in the table
    this.natTable.addConfiguration(new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        registerEditableComments(configRegistry);

      }

    });

    // 226387 - double click on nattable to be editable
    this.natTable.addConfiguration(new DefaultEditBindings() {

      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        // call for explicit code of default behaviour of nattable i.e sort, column filter, grouping
        CustomSortFilterGroupEnabler customSortFilterGroupEnabler = new CustomSortFilterGroupEnabler(uiBindingRegistry,
            true, ReviewResultParamListPage.this.natTable, ReviewResultParamListPage.this.paramFilterGridLayer);
        // flag to check whether nat page is in review result editor
        customSortFilterGroupEnabler.setRvwResultEditor(true);
        customSortFilterGroupEnabler.setComboRowFilterEnabled(true);
        uiBindingRegistry.registerFirstSingleClickBinding(ReviewResultParamListPage.this.cellEditorMouseEventMatcher,
            customSortFilterGroupEnabler);
        uiBindingRegistry.registerDoubleClickBinding(ReviewResultParamListPage.this.cellEditorMouseEventMatcher,
            new CustomMouseEditAction());
      }
    });

    this.natTable
        .addConfiguration(new ReviewScoreComboBoxEditConfiguration(this.resultData.getResultBo().getReviewType()));

    // ICDM-2671, adding one more parameter in the Rvw Result Update Data Command Handler
    this.paramFilterGridLayer.getBodyDataLayer().registerCommandHandler(new ReviewResultUpdateDataCommandHandler(
        this.paramFilterGridLayer, this.editor, this.resultData.getResultBo().getReviewType()));
    // ICDM-2439
    this.natTable.addConfiguration(new RevResultSsdClassConfiguration());
    createToolBarAction();
    this.natTable.setConfigRegistry(configRegistry);
    // Configure NAT table
    this.natTable.configure();
    // Load the saved state of NAT table
    loadState();

    // Column chooser configuration
    CustomDefaultBodyLayerStack bodyLayer = this.paramFilterGridLayer.getBodyLayer();

    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.paramFilterGridLayer.getColumnHeaderLayer(), this.paramFilterGridLayer.getColumnHeaderDataLayer(),
            null, null);
    this.natTable.registerCommandHandler(columnChooserCommandHandler);

    // add listeners to save state
    this.natTable.addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent event) {
        // save state on focus lost to maintain state for other result editors
        saveState();
      }

      @Override
      public void focusGained(final FocusEvent event) {
        // no implementation at the moment

      }
    });

    // Save the current state of the nat table before disposing
    this.natTable.addDisposeListener(event ->
    // Stop
    saveState());

    createRowSelectionProvider();

    attachToolTip(this.natTable);

  }

  /**
   *
   */
  private void createRowSelectionProvider() {
    this.selectionProvider = new RowSelectionProvider<>(this.paramFilterGridLayer.getBodyLayer().getSelectionLayer(),
        this.paramFilterGridLayer.getBodyDataProvider(), false);

    this.selectionProvider.addSelectionChangedListener(event -> {

      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      if ((selection != null) && (selection.getFirstElement() != null)) {
        if (selection.getFirstElement() instanceof CDRResultParameter) {
          final CDRResultParameter selectedParam = (CDRResultParameter) selection.getFirstElement();
          ReviewResultParamListPage.this.editor.getEditorInput().setCdrReviewResultParam(selectedParam);
          ReviewResultParamListPage.this.editor.getEditorInput().setResultFunction(null);

          // iCDM-1408
          // case-1 : no dialog had opened, if reviewResultEditor.getCalDataViewerDialog() is null, this condition
          // satifies
          // case-2 : Dialog already opened and still in that position, if
          // reviewResultEditor.getCalDataViewerDialog().getShell() is not null, this condition satifies
          ReviewResultNATActionSet reviewActionSet = new ReviewResultNATActionSet();
          ReviewResultEditor reviewResultEditor = (ReviewResultEditor) getEditor();
          // ICDM-2304
          if ((null != reviewResultEditor.getSynchCalDataViewerDialog()) &&
              (null != reviewResultEditor.getSynchCalDataViewerDialog().getShell())) {
            reviewActionSet.showTableGraphAction(selectedParam, ReviewResultParamListPage.this.editor, true);
          }
        }
        else {
          ReviewResultParamListPage.this.editor.getEditorInput().setResultFunction(null);
          ReviewResultParamListPage.this.editor.getEditorInput().setCdrReviewResultParam(null);
        }
        updateStatusBar(false);
      }
      // maintain selection while traversing between pages
      if ((selection != null) && selection.isEmpty() &&
          (ReviewResultParamListPage.this.editor.getEditorInput().getCdrReviewResultParam() != null)) {
        ReviewResultParamListPage.this.selectionProvider.setSelection(
            new StructuredSelection(ReviewResultParamListPage.this.editor.getEditorInput().getCdrReviewResultParam()));
      }
    }

    );
  }

  private class CustomMouseEditAction extends MouseEditAction {

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final NatTable natTable, final MouseEvent event) {
      int rowPosition = natTable.getRowPositionByY(event.y);
      // check if the row is not a header row
      if (rowPosition > 2) {
        super.run(natTable, event);
      }
    }

  }


  /**
   * opens the dialog after checking the review status is closed & lock status is false
   */
  // ICDM-2389
  private void openLockMessageDialog() {
    if ((this.resultData.getResultBo().getStatus() == CDRConstants.REVIEW_STATUS.CLOSED) &&
        (!this.resultData.getResultBo().isResultLocked())) {
      ReviewResultNATActionSet reviewResultNATActionSet = new ReviewResultNATActionSet();
      // ICDM-2389
      reviewResultNATActionSet.openLockMessageDialog(Display.getCurrent().getActiveShell(), true, this.resultData);
    }
  }

  /**
   * opens the warning dialog after delta review
   */
  private void openWarnMessageDialog() {
    StringBuilder params = new StringBuilder();
    Map<Long, CDRResultParameter> paramMap = this.resultData.getResultBo().getParametersMap();
    for (CDRResultParameter param : paramMap.values()) {
      if (CommonUtils.isEqualIgnoreCase(param.getRvwComment(), ApicConstants.LABEL_NOT_RELEVANT_COMMENT)) {
        params.append(param.getName()).append("\n");
      }
    }
    if (CommonUtils.isNotEqual(params.length(), 0)) {
      CDMLogger.getInstance().warnDialog(
          "The following labels commented as ‘not relevant for current release and still to be calibrated’ :\n" +
              params.toString().trim(),
          Activator.PLUGIN_ID);
    }
  }

  /**
   * @param propertyToLabelMap
   * @param columnWidthMap
   */
  private void configureColumnsNATTable(final Map<Integer, String> propertyToLabelMap,
      final Map<Integer, Integer> columnWidthMap) {
    propertyToLabelMap.put(SSD_CLASS_COL_NUMBER, "SSD Class Type");// 0
    // propertyToLabelMap.put(SHAPE_CHECK_COL_NUMBER, "Shape Check");// 1
    propertyToLabelMap.put(PARAMETER_COL_NUMBER, "Parameter");// 2
    propertyToLabelMap.put(LONG_NAME_COL_NUMBER, "Long Name"); // to hide
    // 496338 - Add WP, Resp columns in NAT table in Review Result Editor
    propertyToLabelMap.put(WORKPACKAGE_COL_NUMBER, "Work Package");
    propertyToLabelMap.put(RESPONSIBILITY_COL_NUMBER, "Responsibility"); // to hide
    propertyToLabelMap.put(RESP_TYPE_COL_NUMBER, "Responsibility Type");// to hide
    propertyToLabelMap.put(TYPE_COL_NUMBER, "Type");// to hide
    propertyToLabelMap.put(CLASS_COL_NUMBER, "Class"); // to hide
    propertyToLabelMap.put(CODEWORD_COL_NUMBER, "Code Word"); // to hide
    propertyToLabelMap.put(BITWISE_COL_NUMBER, "Bitwise"); // to hide
    propertyToLabelMap.put(PARAM_HINT_COL_NUMBER, "Parameter Hint"); // to hide , 8
    propertyToLabelMap.put(FC_NAME_COL_NUMBER, "FC Name"); // to hide
    propertyToLabelMap.put(LOWER_LIMIT_COL_NUMBER, "Lower Limit");
    propertyToLabelMap.put(REF_VAL_COL_NUMBER, "Reference Value");
    // ICDM-2151
    propertyToLabelMap.put(REF_VAL_UNIT_COL_NUMBER, "Ref Val Unit");// 12
    propertyToLabelMap.put(MATURITY_LEVEL_COL_NUMBER, "Ref Val Maturity Level");
    // add exact match col
    propertyToLabelMap.put(EXACT_MATCH_COL_NUMBER, "Exact Match");
    propertyToLabelMap.put(UPPER_LIMIT_COL_NUMBER, "Upper Limit");// 13
    propertyToLabelMap.put(BIT_LIMIT_COL_NUMBER, "Bitwise Limit");
    propertyToLabelMap.put(READY_FOR_SERIES_COL_NUMBER, "Ready for series");
    // add ready for series
    propertyToLabelMap.put(CHECK_VALUE_COL_NUMBER, "Checked Value");// 15
    propertyToLabelMap.put(PARENT_REF_VALUE_COL_NUMBER, "Parent Reference Value");
    propertyToLabelMap.put(PARENT_CHECK_VALUE_COL_NUMBER, "Parent Checked Value");

    // ICDM-2151
    propertyToLabelMap.put(CHECK_VALUE_UNIT_COL_NUMBER, "Checked Value's Unit");// 16
    propertyToLabelMap.put(IMP_VALUE_COL_NUMBER, "Imported Value"); // to hide
    propertyToLabelMap.put(RESULT_COL_NUMBER, "Result");// 17

    // 231286
    propertyToLabelMap.put(SEC_RESULT_COL_NUMBER, "Secondary Result");// 231286-new column
    propertyToLabelMap.put(SCORE_COL_NUMBER, "Score");
    propertyToLabelMap.put(SCORE_DESC_COL_NUMBER, "Score Description");
    propertyToLabelMap.put(COMMENT_COL_NUMBER, REVIEW_RESULT_COMMENT_HEADER_LABEL);
    // ICDM-2288
    propertyToLabelMap.put(CDFX_STATUS_COL_NUMBER, "CDFX_Status");
    propertyToLabelMap.put(CDFX_USER_COL_NUMBER, "CDFX_User");// 22
    propertyToLabelMap.put(CDFX_DATE_COL_NUMBER, "CDFX_Date");
    propertyToLabelMap.put(CDFX_WP_COL_NUMBER, "CDFX_Work Package");
    propertyToLabelMap.put(CDFX_PROJECT_COL_NUMBER, "CDFX_Project");
    propertyToLabelMap.put(CDFX_TARGET_VAR_COL_NUMBER, "CDFX_Target\nVariant");
    propertyToLabelMap.put(CDFX_TEST_OBJ_COL_NUMBER, "CDFX_Test Object");// 27
    propertyToLabelMap.put(CDFX_PROGRAM_ID_COL_NUMBER, "CDFX_Program Identifier");
    propertyToLabelMap.put(CDFX_DATA_COL_NUMBER, "CDFX_Data Identifier");
    propertyToLabelMap.put(CDFX_REMARK_COL_NUMBER, "CDFX_Remark");
    propertyToLabelMap.put(ARC_RELEASED_COL_NUMBER, "ARC Released");


    // The below map is used by NatTable to Map Columns with their respective widths
    columnWidthMap.put(SSD_CLASS_COL_NUMBER, 6 * 6);
    columnWidthMap.put(PARAMETER_COL_NUMBER, 15 * 15);
    columnWidthMap.put(LONG_NAME_COL_NUMBER, 10 * 15);
    columnWidthMap.put(WORKPACKAGE_COL_NUMBER, 10 * 15);
    columnWidthMap.put(RESPONSIBILITY_COL_NUMBER, 10 * 15);
    columnWidthMap.put(CLASS_COL_NUMBER, 10 * 5);
    columnWidthMap.put(CODEWORD_COL_NUMBER, 10 * 5);
    columnWidthMap.put(BITWISE_COL_NUMBER, 10 * 5);
    columnWidthMap.put(PARAM_HINT_COL_NUMBER, 10 * 15);
    columnWidthMap.put(FC_NAME_COL_NUMBER, 10 * 5);
    columnWidthMap.put(LOWER_LIMIT_COL_NUMBER, 10 * 5);
    columnWidthMap.put(REF_VAL_COL_NUMBER, 10 * 5);
    columnWidthMap.put(PARENT_REF_VALUE_COL_NUMBER, 15 * 10);
    columnWidthMap.put(PARENT_CHECK_VALUE_COL_NUMBER, 15 * 10);
    columnWidthMap.put(REF_VAL_UNIT_COL_NUMBER, 10 * 5);
    columnWidthMap.put(MATURITY_LEVEL_COL_NUMBER, 15 * 15);
    columnWidthMap.put(EXACT_MATCH_COL_NUMBER, 10 * 5);
    columnWidthMap.put(UPPER_LIMIT_COL_NUMBER, 10 * 5);
    columnWidthMap.put(BIT_LIMIT_COL_NUMBER, 10 * 5);
    columnWidthMap.put(READY_FOR_SERIES_COL_NUMBER, 10 * 5);
    columnWidthMap.put(CHECK_VALUE_COL_NUMBER, 15 * 8);
    columnWidthMap.put(CHECK_VALUE_UNIT_COL_NUMBER, 10 * 5);
    columnWidthMap.put(IMP_VALUE_COL_NUMBER, 15 * 5);
    columnWidthMap.put(RESULT_COL_NUMBER, 10 * 5);
    columnWidthMap.put(SCORE_COL_NUMBER, 10 * 8);

    // 231286
    columnWidthMap.put(SEC_RESULT_COL_NUMBER, 15 * 10);
    columnWidthMap.put(SCORE_DESC_COL_NUMBER, 15 * 15);
    columnWidthMap.put(COMMENT_COL_NUMBER, 15 * 15);
    columnWidthMap.put(CDFX_STATUS_COL_NUMBER, 15 * 15);
    columnWidthMap.put(CDFX_USER_COL_NUMBER, 15 * 15);
    columnWidthMap.put(CDFX_DATE_COL_NUMBER, 15 * 15);
    columnWidthMap.put(CDFX_WP_COL_NUMBER, 15 * 15);
    columnWidthMap.put(CDFX_PROJECT_COL_NUMBER, 15 * 15);
    columnWidthMap.put(CDFX_TARGET_VAR_COL_NUMBER, 10 * 5);
    columnWidthMap.put(CDFX_TEST_OBJ_COL_NUMBER, 15 * 15);
    columnWidthMap.put(CDFX_PROGRAM_ID_COL_NUMBER, 15 * 15);
    columnWidthMap.put(CDFX_DATA_COL_NUMBER, 15 * 15);
    columnWidthMap.put(CDFX_REMARK_COL_NUMBER, 15 * 15);
    columnWidthMap.put(ARC_RELEASED_COL_NUMBER, 10 * 5);

  }

  /**
   * Load saved state of NAT table
   */
  private void loadState() {
    try {
      if (this.resetState) {
        this.natTable.resetState();
      }
      this.natTable.loadState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to load CDR Result nat table state", ioe, Activator.PLUGIN_ID);
    }

  }


  /**
   * Save current state for the NAT table
   */
  private void saveState() {
    try {
      this.natTable.saveState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to save CDR Result nat table state", ioe, Activator.PLUGIN_ID);
    }

  }

  /**
   * Add right click context menu items
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {
      final IStructuredSelection selection =
          (IStructuredSelection) ReviewResultParamListPage.this.selectionProvider.getSelection();
      final Object firstElement = selection.getFirstElement();
      if (!(firstElement instanceof CDRResultParameter) && (selection.size() == 1)) {
        return;
      }
      // ICDM-826
      if (selection.size() > 1) {
        menuForMultiSelect(menuMgr, mgr, selection);
      }
      else {
        menuForSingleSelect(menuMgr, selection);
      }
    });
    final Menu menu = menuMgr.createContextMenu(this.natTable.getShell());
    this.natTable.setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.selectionProvider);
  }


  /**
   * Menu to be shown when multiple rows are selected
   *
   * @param menuManagr MenuManager
   * @param mgr IMenuManager
   * @param selection IStructuredSelection
   */
  private void menuForMultiSelect(final MenuManager menuManagr, final IMenuManager mgr,
      final IStructuredSelection selection) {
    ReviewScoreContextMenu contextMenu = new ReviewScoreContextMenu();
    contextMenu.setReviewScoreMenu(selection, menuManagr, this.cdrResult, ReviewResultParamListPage.this);
    final ReviewResultNATActionSet reviewActionSet = new ReviewResultNATActionSet();
    mgr.add(new Separator());

    // Task 236308
    reviewActionSet.updateMultiSecdyResAsChecked(menuManagr, selection, this);
    reviewActionSet.updateMultiSecdyResAsReset(menuManagr, selection, this);
    mgr.add(new Separator());
    // Set ARC Release Necessary
    reviewActionSet.setARCReleaseNecessary(selection, menuManagr, ReviewResultParamListPage.this);
    reviewActionSet.setReviewComments(selection, menuManagr, ReviewResultParamListPage.this);
    // set review comment context menu
    new ReviewCommentContextMenu(ReviewResultParamListPage.this).setReviewCommentMenu(menuManagr, selection);
    reviewActionSet.takeOverFromCdfx(selection, menuManagr, ReviewResultParamListPage.this);
    addCopyAndPasteActions(menuManagr, selection, reviewActionSet);
  }

  /**
   * @param menuManagr
   * @param selection
   * @param firstElement
   * @param reviewActionSet
   */
  private void addCopyAndPasteActions(final MenuManager menuManagr, final IStructuredSelection selection,
      final ReviewResultNATActionSet reviewActionSet) {
    if ((null != ReviewResultParamListPage.this.paramFilterGridLayer.getColumnHeaderDataLayer().getDataProvider()
        .getDataValue(ReviewResultParamListPage.this.selectedColPostn, 0)) &&
        (ReviewResultParamListPage.this.paramFilterGridLayer.getColumnHeaderDataLayer().getDataProvider()
            .getDataValue(ReviewResultParamListPage.this.selectedColPostn, 0).equals(getCommentHeaderLabel()))) {
      reviewActionSet.createCopyAction(menuManagr, ReviewResultParamListPage.this);
      reviewActionSet.pasteCommentsActionNew(menuManagr, selection, ReviewResultParamListPage.this);
    }
  }


  /**
   * Menu actions for single selection
   *
   * @param menuManagr menu
   * @param mgr Imgr
   * @param selection selection
   */
  private void menuForSingleSelect(final MenuManager menuManagr, final IStructuredSelection selection) {
    final CDMCommonActionSet cdmActionSet = new CDMCommonActionSet();
    final CommonActionSet cdrActionSet = new CommonActionSet();


    final Object firstElement = selection.getFirstElement();
    if ((firstElement != null) && (!selection.isEmpty())) {
      final ReviewResultNATActionSet reviewActionSet = new ReviewResultNATActionSet();
      ReviewActionSet reviewEditorActionSet = new ReviewActionSet();
      if (firstElement instanceof CDRResultFunction) {
        reviewEditorActionSet.addReviewParamEditorNew(menuManagr, firstElement, null,
            ReviewResultParamListPage.this.resultData.getCDRFunction((CDRResultFunction) firstElement), true);
      }
      else if (firstElement instanceof CDRResultParameter) {
        menuForCdrResultParameter(menuManagr, selection, cdmActionSet, cdrActionSet, firstElement, reviewActionSet,
            reviewEditorActionSet);
      }
      // Collapse All Action
      menuManagr.add(new Separator());
      final Action collapseAllAction = createCollapseAllAction();
      menuManagr.add(collapseAllAction);
    }
  }

  /**
   * @param menuManagr
   * @param selection
   * @param cdmActionSet
   * @param cdrActionSet
   * @param firstElement
   * @param reviewActionSet
   * @param reviewEditorActionSet
   */
  private void menuForCdrResultParameter(final MenuManager menuManagr, final IStructuredSelection selection,
      final CDMCommonActionSet cdmActionSet, final CommonActionSet cdrActionSet, final Object firstElement,
      final ReviewResultNATActionSet reviewActionSet, final ReviewActionSet reviewEditorActionSet) {
    // Icdm-521
    // Using the CDM common Action Set available in common UI
    List<Object> selectedObj = new ArrayList<>();
    selectedObj.add(firstElement);
    CDRResultParameter cdrResultParameter = (CDRResultParameter) firstElement;
    // ICDM-1280
    reviewActionSet.addCreateRuleAction(menuManagr, firstElement,
        ReviewResultParamListPage.this.resultData
            .getCDRFunction(ReviewResultParamListPage.this.resultData.getFunction(cdrResultParameter)),
        true, ReviewResultParamListPage.this.resultData.getResultBo().getRuleSet());
    menuManagr.add(new Separator());
    // Add Show Series statistics menu action
    cdmActionSet.addShowSeriesStatisticsMenuAction(menuManagr, selectedObj, true);


    // opens the parent review result editor
    // iCDM-2186

    if (ReviewResultParamListPage.this.resultData.getResultBo().isDeltaReview()) {
      CdrActionSet cdrAtnSet = new CdrActionSet();
      cdrAtnSet.openParentReviewResult(menuManagr, firstElement, ReviewResultParamListPage.this.resultData);
    }

    menuManagr.add(new Separator());
    // Icdm-697 Disable the action if more than one value is selected
    cdmActionSet.addShowReviewDataMenuAction(menuManagr, firstElement, true, null);

    reviewEditorActionSet.addActionForRulesEditor(menuManagr, firstElement,
        ReviewResultParamListPage.this.resultData
            .getCDRFunction(ReviewResultParamListPage.this.resultData.getFunction(cdrResultParameter)),
        ReviewResultParamListPage.this.resultData.getResultBo().getRuleSet());
    ReviewScoreContextMenu contextMenu = new ReviewScoreContextMenu();
    contextMenu.setReviewScoreMenu(selection, menuManagr, this.cdrResult, ReviewResultParamListPage.this);
    menuManagr.add(new Separator());
    // Set ARC Release Necessary
    reviewActionSet.setARCReleaseNecessary(selection, menuManagr, ReviewResultParamListPage.this);
    reviewActionSet.setReviewComments(selection, menuManagr, ReviewResultParamListPage.this);
    // set review comment context menu
    new ReviewCommentContextMenu(ReviewResultParamListPage.this).setReviewCommentMenu(menuManagr, selection);

    reviewActionSet.takeOverFromCdfx(selection, menuManagr, ReviewResultParamListPage.this);
    menuManagr.add(new Separator());
    reviewActionSet.showUnSynchronizedTableGraph(menuManagr, firstElement, ReviewResultParamListPage.this.editor);
    reviewActionSet.showSynchronizedTableGraph(menuManagr, firstElement, ReviewResultParamListPage.this.editor);
    menuManagr.add(new Separator());
    // Task 231287
    reviewActionSet.showSecondaryResultDetails(menuManagr, firstElement, ReviewResultParamListPage.this.resultData);
    // Task 236308
    reviewActionSet.updateSecondaryResult(menuManagr, firstElement, this);
    // // ICDM-1348
    menuManagr.add(new Separator());
    cdrActionSet.copyParamNameToClipboardAction(menuManagr, ((CDRResultParameter) firstElement).getName());


    addCopyAndPasteActions(menuManagr, selection, reviewActionSet);

    ReviewRuleActionSet reviewRuleAction = new ReviewRuleActionSet();

    ParamCollection paramCol = ReviewResultParamListPage.this.resultData
        .getCDRFunction(ReviewResultParamListPage.this.resultData.getFunction(cdrResultParameter));
    if (ReviewResultParamListPage.this.resultData.getResultBo().getRuleSet() != null) {
      paramCol = ReviewResultParamListPage.this.resultData.getResultBo().getRuleSet();
    }
    menuManagr.add(new Separator());

    reviewRuleAction.showRuleHistoryFromResult(menuManagr, paramCol, cdrResultParameter,
        IMessageConstants.OPEN_RULE_HISTORY, false, false);

    if (ReviewResultParamListPage.this.resultData.isComplianceParameter(cdrResultParameter)) {
      reviewRuleAction.showRuleHistoryFromResult(menuManagr, paramCol, cdrResultParameter,
          IMessageConstants.OPEN_COMPLI_RULE_HISTORY, true, false);
    }
    if (ReviewResultParamListPage.this.resultData.isQssdParameter(cdrResultParameter)) {
      reviewRuleAction.showRuleHistoryFromResult(menuManagr, paramCol, cdrResultParameter,
          IMessageConstants.OPEN_QSSD_RULE_HISTORY, false, true);
    }
    menuManagr.add(new Separator());
    // if shape check has been performed , show the follwing menu items
    if (null != cdrResultParameter.getSrResult()) {
      reviewRuleAction.showShapeCheckResult(menuManagr, cdrResultParameter, IMessageConstants.OPEN_SHAPE_CHECK_RESULT);
      boolean acceptFlag = true;
      if (SR_ACCEPTED_FLAG.YES.getUiType().equals(cdrResultParameter.getSrAcceptedFlag())) {
        acceptFlag = false;
      }
      new AcceptShapeCheckResultAction(menuManagr, cdrResultParameter, acceptFlag,
          ReviewResultParamListPage.this.resultData, ReviewResultParamListPage.this);
    }
  }


  // Task-729547
  /**
   * Register rules for checkbox
   *
   * @param configRegistry
   * @param dataProvider
   */
  private void registerEditableComments(final IConfigRegistry configRegistry) {
    // combo box values
    List<String> comboList = new ArrayList<>();

    // get the comments to be set in combo
    Set<RvwCommentTemplate> rvwCmntTemplateSet = null;
    try {
      rvwCmntTemplateSet = new CommonDataBO().getAllRvwCommentTemplate();

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    if (rvwCmntTemplateSet != null) {
      // Only default Comments are available in combobox
      for (RvwCommentTemplate rvwCommentTemplate : rvwCmntTemplateSet) {
        comboList.add(rvwCommentTemplate.getName());
      }

    }


    // set the combobox config for comment
    getComboStyle(configRegistry, "COMBO_COMMENT", IEditableRule.ALWAYS_EDITABLE, comboList);
    // set the combobox config for comment readonly
    getComboStyle(configRegistry, "COMBO_COMMENT_READONLY", IEditableRule.NEVER_EDITABLE, comboList);

  }

  // To display comments in the combobox
  /**
   * @param configRegistry
   * @param string
   * @param alwaysEditable
   * @param comboList
   */
  private void getComboStyle(final IConfigRegistry configRegistry, final String label,
      final IEditableRule alwaysEditable, final List<String> comboList) {

    ComboBoxCellEditor comboBoxCellEditor = new ComboBoxCellEditor(comboList, 10);
    comboBoxCellEditor.setFreeEdit(true);

    // register the cell properties
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, DisplayMode.EDIT,
        label);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new ComboBoxPainter(), DisplayMode.NORMAL,
        label);

    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, alwaysEditable, DisplayMode.NORMAL,
        label);

  }

  /**
   * Create filter text
   */
  private void createFilterTxt() {
    this.filterTxt.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.filterTxt.setMessage("type filter text");
    this.filterTxt.addModifyListener(event -> {
      String text = ReviewResultParamListPage.this.filterTxt.getText().trim();
      ReviewResultParamListPage.this.allColumnFilterMatcher.setFilterText(text, true);
      ReviewResultParamListPage.this.paramFilterGridLayer.getComboGlazedListsFilterStrategy()
          .applyFilterInAllColumns(text);

      ReviewResultParamListPage.this.paramFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(ReviewResultParamListPage.this.paramFilterGridLayer.getSortableColumnHeaderLayer()));

      setStatusBarMessage(ReviewResultParamListPage.this.groupByHeaderLayer, false);
    });
  }

  /**
   * Get custom comparator configuration
   *
   * @param columnHeaderDataLayer
   * @return IConfiguration
   */
  private IConfiguration getCustomComparatorConfiguration() {

    return new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Register labels
        for (int i = 0; i < COLUMN_COUNT; i++) {
          ReviewResultParamListPage.this.columnLabelAccumulator.registerColumnOverrides(i, CUSTOM_COMPARATOR_LABEL + i);
        }

        // Register column attributes
        // ICDM-2439
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_TYPE_COMPLIANCE), NORMAL,
            CUSTOM_COMPARATOR_LABEL + SSD_CLASS_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + PARAMETER_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_LONG_NAME), NORMAL,
            CUSTOM_COMPARATOR_LABEL + LONG_NAME_COL_NUMBER);
        // 496338 - Add WP, Resp columns in NAT table in Review Result Editor
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_WORKPACKAGE), NORMAL, CUSTOM_COMPARATOR_LABEL + WORKPACKAGE_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_RESPONSIBILITY), NORMAL,
            CUSTOM_COMPARATOR_LABEL + RESPONSIBILITY_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_TYPE), NORMAL, CUSTOM_COMPARATOR_LABEL + RESP_TYPE_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_TYPE), NORMAL, CUSTOM_COMPARATOR_LABEL + TYPE_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_CLASS), NORMAL, CUSTOM_COMPARATOR_LABEL + CLASS_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_CODEWORD), NORMAL, CUSTOM_COMPARATOR_LABEL + CODEWORD_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_BITWISE), NORMAL, CUSTOM_COMPARATOR_LABEL + BITWISE_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_HINT), NORMAL, CUSTOM_COMPARATOR_LABEL + PARAM_HINT_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_FUNC_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + FC_NAME_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_LOWER_LIMIT), NORMAL, CUSTOM_COMPARATOR_LABEL + LOWER_LIMIT_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_REFERENCE_VALUE), NORMAL, CUSTOM_COMPARATOR_LABEL + REF_VAL_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARENT_REF_VALUE), NORMAL,
            CUSTOM_COMPARATOR_LABEL + PARENT_REF_VALUE_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARENT_CHECK_VALUE), NORMAL,
            CUSTOM_COMPARATOR_LABEL + PARENT_CHECK_VALUE_COL_NUMBER);
        // ICDM-2151
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_REFERENCE_VALUE_UNIT), NORMAL,
            CUSTOM_COMPARATOR_LABEL + REF_VAL_UNIT_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_UPPER_LIMIT), NORMAL, CUSTOM_COMPARATOR_LABEL + UPPER_LIMIT_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_BITWISE), NORMAL, CUSTOM_COMPARATOR_LABEL + BIT_LIMIT_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_CHECK_VALUE), NORMAL, CUSTOM_COMPARATOR_LABEL + CHECK_VALUE_COL_NUMBER);
        // ICDM-2151
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_CHECK_VALUE_UNIT), NORMAL,
            CUSTOM_COMPARATOR_LABEL + CHECK_VALUE_UNIT_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_IMP_VALUE), NORMAL, CUSTOM_COMPARATOR_LABEL + IMP_VALUE_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_RESULT), NORMAL, CUSTOM_COMPARATOR_LABEL + RESULT_COL_NUMBER);
        // Task 231286
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_SEC_RESULT), NORMAL, CUSTOM_COMPARATOR_LABEL + SEC_RESULT_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_SCORE), NORMAL, CUSTOM_COMPARATOR_LABEL + SCORE_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_SCORE), NORMAL, CUSTOM_COMPARATOR_LABEL + SCORE_DESC_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_COMMENT), NORMAL, CUSTOM_COMPARATOR_LABEL + COMMENT_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_STATUS), NORMAL, CUSTOM_COMPARATOR_LABEL + CDFX_STATUS_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_USER), NORMAL, CUSTOM_COMPARATOR_LABEL + CDFX_USER_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_DATE), NORMAL, CUSTOM_COMPARATOR_LABEL + CDFX_DATE_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_WP), NORMAL, CUSTOM_COMPARATOR_LABEL + CDFX_WP_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PROJECT), NORMAL, CUSTOM_COMPARATOR_LABEL + CDFX_PROJECT_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PROJVAR), NORMAL, CUSTOM_COMPARATOR_LABEL + CDFX_TARGET_VAR_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_TESTOBJ), NORMAL, CUSTOM_COMPARATOR_LABEL + CDFX_TEST_OBJ_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PGMIDENTIFIER), NORMAL,
            CUSTOM_COMPARATOR_LABEL + CDFX_PROGRAM_ID_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_DATAIDENTIFIER), NORMAL,
            CUSTOM_COMPARATOR_LABEL + CDFX_DATA_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_REMARK), NORMAL, CUSTOM_COMPARATOR_LABEL + CDFX_REMARK_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_MATURITY_LEVEL), NORMAL,
            CUSTOM_COMPARATOR_LABEL + MATURITY_LEVEL_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_READY_FOR_SERIES), NORMAL,
            CUSTOM_COMPARATOR_LABEL + READY_FOR_SERIES_COL_NUMBER);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_EXACT_MATCH), NORMAL, CUSTOM_COMPARATOR_LABEL + EXACT_MATCH_COL_NUMBER);
      }
    };
  }

  /**
   * Create toolbar action
   */
  private void createToolBarAction() {

    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    this.rvwResultDecimalPrefAction = new ReviewResultOpenDecimalPreferencesWindowAction();
    final ToolBar toolbar = this.toolBarManager.createControl(this.tableSection);
    // NAT table action set
    ReviewResultNATToolBarActionSet toolBarActionSet =
        new ReviewResultNATToolBarActionSet(this, this.paramFilterGridLayer);

    final Separator separator = new Separator();

    // ICDM-2439
    // Filter For the compliance parameters
    toolBarActionSet.complianceFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    // Filter For the non compliance parameters
    toolBarActionSet.nonComplianceFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);

    // Filter For the black list parameters
    toolBarActionSet.blackListFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    // Filter For the non black list parameters
    toolBarActionSet.nonBlackListFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);

    // Filter For the read only parameters
    toolBarActionSet.readOnlyFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    // Filter For the non read only parameters
    toolBarActionSet.notReadOnlyFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);

    // Filter For the dependent parameters
    toolBarActionSet.dependentFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    // Filter For the non dependent parameters
    toolBarActionSet.notDependentFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);

    // Filter For the QSSD parameters
    toolBarActionSet.qSSDFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    // Filter For the non QSSD parameters
    toolBarActionSet.nonQSSDFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);

    // Filter For the Axis point - Value Type
    toolBarActionSet.axisPointFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter for the Ascii - Value Type
    toolBarActionSet.asciiFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter For the Value Block - Value Type
    toolBarActionSet.valueBlockFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter for the Value- Value Type
    toolBarActionSet.valueFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter For the Curve - Value Type
    toolBarActionSet.curveFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter for the Map Value Type
    toolBarActionSet.mapFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    // Filter For the Rivet - Class Type
    toolBarActionSet.rivetFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter For the Nail - Class Type
    toolBarActionSet.nailFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter For the Screw - Class Type
    toolBarActionSet.screwFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter For the Undefined - Class Type
    toolBarActionSet.undefinedClassFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    toolBarActionSet.showCompli(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showOk(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showResultNotOk(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showResultUndefined(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    // Task 236307
    toolBarActionSet.showSecResOk(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showSecResNoOk(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showSecResNA(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showSecResChecked(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    toolBarActionSet.showReviewed(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showNotReviewed(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    // Filter for parameter with change marker
    toolBarActionSet.hasChangeMarkerAction(this.toolBarManager, this.toolBarFilters);
    // Filter for parameter with no change marker
    toolBarActionSet.hasNoChangeMarkerAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    // ICDM-1197
    // Filter for parameter with history
    toolBarActionSet.hasHistoryAction(this.toolBarManager, this.toolBarFilters);
    // Filter for parameter without history
    toolBarActionSet.hasNoHistoryAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarManager.update(true);
    addResetAllFiltersAction();
    this.tableSection.setTextClient(toolbar);

    ToolBarManager toolBarformManager = (ToolBarManager) this.tableForm.getToolBarManager();
    if ((this.resultData.getResultBo().getReviewType() != REVIEW_TYPE.TEST) && !isMonicaReview()) {
      // display the action only for start and official reviews
      QuesRespListAction quesAction = new QuesRespListAction(this);
      toolBarformManager.add(quesAction);
      toolBarformManager.add(separator);
    }
    this.lockAction = new ReviewResultLockUnLockAction(this, this.tableForm,
        ((ReviewResultEditorInput) (getEditorInput())).getResultData());
    toolBarformManager.add(this.lockAction);
    toolBarformManager.add(separator);
    // action to mark a wp as finished
    toolBarformManager.add(new ReviewResultMarkWorkPackageAsFinishedAction(this,
        ((ReviewResultEditorInput) (getEditorInput())).getResultData()));
    toolBarformManager.add(separator);
    toolBarformManager.add(this.rvwResultDecimalPrefAction);
    toolBarformManager.add(separator);
    toolBarActionSet.importValueFromFileAction(toolBarformManager, this);
    toolBarActionSet.clearImportedValueAction(toolBarformManager, this);
    toolBarformManager.add(separator);
    CDFXFileExportAction cdfxExportAction = new CDFXFileExportAction(this);
    toolBarformManager.add(cdfxExportAction);
    this.tableForm.getToolBarManager().update(true);
  }

  /**
   * Add reset filter button ICDM-1207
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.paramFilterGridLayer);
    addResetFiltersAction();
    refreshNatTable();
    ReviewResultParamListPage.this.updateStatusBar(true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMessage(this.groupByHeaderLayer, false);
  }

  /**
   * @return This method defines GridData
   */
  private GridData getGridData() {

    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    return gridData;
  }


  /**
   * Set status bar message
   *
   * @param outlineSelection selection
   */
  @Override
  public void setStatusBarMessage(final boolean outlineSelection) {
    this.editor.updateStatusBar(outlineSelection, this.totTableRowCount,
        this.paramFilterGridLayer != null ? this.paramFilterGridLayer.getRowHeaderLayer().getPreferredRowCount() : 0);
  }

  /**
   * Get param comparator
   *
   * @param sortColumns SortColumns
   * @return Comparator
   */
  public Comparator<CDRResultParameter> getParamComparator(final SortColumns sortColumns) {
    if (sortColumns.equals(SortColumns.SORT_IMP_VALUE)) {
      return new ImportColumnComparator();
    }

    return (param1, param2) -> ReviewResultParamListPage.this.resultData.compareTo(param1, param2, sortColumns);
  }

  /**
   * @author dmo5cob
   */
  private final class MouseEventListener implements MouseListener {

    @Override
    public void mouseUp(final MouseEvent mouseevent) {
      // NA

    }

    @Override
    public void mouseDown(final MouseEvent mouseevent) {


      ReviewResultParamListPage.this.selectedColPostn =
          LayerUtil.convertColumnPosition(ReviewResultParamListPage.this.natTable,
              ReviewResultParamListPage.this.natTable.getColumnPositionByX(mouseevent.x),
              ReviewResultParamListPage.this.paramFilterGridLayer.getColumnHeaderDataLayer());
      ReviewResultParamListPage.this.selectedRowPostn =
          LayerUtil.convertRowPosition(ReviewResultParamListPage.this.natTable,
              ReviewResultParamListPage.this.natTable.getRowPositionByY(mouseevent.y),
              ReviewResultParamListPage.this.paramFilterGridLayer.getBodyDataLayer());

    }

    @Override
    public void mouseDoubleClick(final MouseEvent mouseEvent) {

      CustomFilterGridLayer customFilterGridLayer = ReviewResultParamListPage.this.paramFilterGridLayer;
      final SelectionLayer selectionLayer = customFilterGridLayer.getBodyLayer().getSelectionLayer();
      int rowPosition = LayerUtil.convertRowPosition(ReviewResultParamListPage.this.natTable,
          ReviewResultParamListPage.this.natTable.getRowPositionByY(mouseEvent.y), selectionLayer);

      int columnPosition = LayerUtil.convertColumnPosition(ReviewResultParamListPage.this.natTable,
          ReviewResultParamListPage.this.natTable.getColumnPositionByX(mouseEvent.x),
          getCustomFilterGridLayer().getColumnHeaderLayer().getBaseLayer());
      Object rowObject = getCustomFilterGridLayer().getBodyDataProvider().getRowObject(rowPosition);
      // Double click on Result column should open the secondary result dialog
      if ((columnPosition == ReviewResultParamListPage.RESULT_COL_NUMBER) &&
          (rowObject instanceof CDRResultParameter)) {
        IStructuredSelection selection = (IStructuredSelection) getSelectionProvider().getSelection();
        CDRResultParameter cdrResultParameter = (CDRResultParameter) selection.getFirstElement();
        if (getSelectedColPostn() == ReviewResultParamListPage.RESULT_COL_NUMBER) {
          Map<Long, RvwParametersSecondary> secondaryResParams =
              getResultData().getSecondaryResParams(cdrResultParameter);
          if ((secondaryResParams == null) || secondaryResParams.isEmpty()) {
            MessageDialogUtils.getWarningMessageDialog("Secondary Result of " + cdrResultParameter.getName(),
                CDRConstants.NO_SECONDARY_RESULT);
          }
          else {
            SortedSet<RvwParametersSecondary> secondaryParamSet = new TreeSet<>();
            secondaryParamSet.addAll(secondaryResParams.values());
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            SecondaryResultConsDialog dialog =
                new SecondaryResultConsDialog(shell, secondaryParamSet, cdrResultParameter.getName(), getResultData());
            dialog.open();
          }
        }
      }
    }
  }

  /**
   * Comparator used for Imported value column. A separate implementation is needed as the imported value is not stored
   * against a CDRResultParameter object.
   */
  private class ImportColumnComparator implements Comparator<CDRResultParameter> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final CDRResultParameter param1, final CDRResultParameter param2) {
      String result1 = "";
      String result2 = "";
      String paramType1 =
          ReviewResultParamListPage.this.editor.getEditorInput().getResultData().getFunctionParameter(param1).getType();
      String paramType2 =
          ReviewResultParamListPage.this.editor.getEditorInput().getResultData().getFunctionParameter(param2).getType();

      Map<CDRResultParameter, CalData> values = getResultData().getImportValueMap();
      if (values != null) {
        CalData importedValue1 = values.get(param1);
        result1 = (importedValue1 != null) ? importedValue1.getCalDataPhy().getSimpleDisplayValue() : "";

        CalData importedValue2 = values.get(param2);
        result2 = (importedValue2 != null) ? importedValue2.getCalDataPhy().getSimpleDisplayValue() : "";

        if (CommonUtils.isEqual(paramType1, paramType2)) {
          // 287828
          com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType paramType =
              CDRConstants.ParameterType.valueOf(paramType1);
          if ((importedValue1 != null) && (importedValue2 != null) &&
              canUseDoubleCompare(importedValue1, importedValue2, paramType)) {
            return importedValue1.getCalDataPhy().compareTo(importedValue2.getCalDataPhy(),
                com.bosch.calmodel.caldataphy.CalDataPhy.SortColumns.SIMPLE_DISPLAY_VALUE);

          }
        }
      }

      return ApicUtil.compare(result1, result2);
    }

    /**
     * @param importedValue1 CalData
     * @param importedValue2 CalData
     * @param paramType ParameterType
     * @return
     */
    private boolean canUseDoubleCompare(final CalData importedValue1, final CalData importedValue2,
        final ParameterType paramType) {
      return (paramType == ParameterType.VALUE) && validateImportedVal(importedValue1, importedValue2);
    }

    /**
     * @param importedValue2
     * @param importedValue1
     * @return
     */
    private boolean validateImportedVal(final CalData importedValue1, final CalData importedValue2) {
      return (null != importedValue1) && (null != importedValue2) && (null != importedValue1.getCalDataPhy()) &&
          (null != importedValue2.getCalDataPhy());
    }


  }


  /**
   * Refreshes the NatTable.</br>
   * </br>
   * <b><i>StructuralRefreshCommand</b></i> refreshes all the layers because of which the sorting order might
   * change</br>
   * <b><i>VisualRefreshCommand</b></i> is used instead but this results in incorrect values when predefined filters are
   * applied. TODO: Need to find how to refresh specific layers
   */
  public void refreshNatTable() {
    ReviewResultParamListPage.this.paramFilterGridLayer.getComboGlazedListsFilterStrategy()
        .applyToolBarFilterInAllColumns(false);
    final CustomGroupByDataLayer bodyDataLayer = ReviewResultParamListPage.this.paramFilterGridLayer.getBodyDataLayer();
    if (bodyDataLayer.getTreeRowModel().hasChildren(0)) {
      Display.getDefault().syncExec(() -> {
        bodyDataLayer.killCache();
        bodyDataLayer.updateTree();
        updateStatusBar(true);
      });
    }

    // Refresh the natTable
    this.natTable.doCommand(new VisualRefreshCommand());

  }

  /**
   * Enables tootltip only for cells which contain not fully visible content
   *
   * @param natTableObj NATTable
   */
  private void attachToolTip(final NatTable natTableObj) {
    // Icdm-1208- Custom tool tip for Nat table.
    DefaultToolTip toolTip = new CDRResultNatToolTip(natTableObj, new String[0], this);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));
  }

  /**
   * @return the cdrResult
   */
  public CDRReviewResult getCdrResult() {
    return this.cdrResult;
  }

  /**
   * @return the reviewResultTabViewer
   */
  public CustomFilterGridLayer getReviewResultTabViewer() {
    return this.paramFilterGridLayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if ((null != getSite().getPage().getActiveEditor()) &&
        (getSite().getPage().getActiveEditor().equals(getEditor())) && (part instanceof OutlineViewPart)) {
      selectionListener(selection);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    if (getPartControl() != null) {
      this.lockAction.update();
    }
    // to refresh combo box column filter items when there is any change in score or comments,so that newly added items
    // are also reflected in combo box column filter in remote client
    if (null != this.natTable) {
      refreshColFilters(null);
    }
    // update questionnaire status
    createOrUpdateQuesStatusLbl();
    try {
      if (CommonUtils.isNotEmpty(getResultData().getResponse().getQnaireDataForRvwSet())) {
        refreshRvwQnaireRespVersion();
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    Map<IModelType, Map<Long, ChangeData<?>>> consChangeData = dce.getConsChangeData();
    for (Entry<IModelType, Map<Long, ChangeData<?>>> entry : consChangeData.entrySet()) {
      if ((entry.getKey() == MODEL_TYPE.VARIANT)) {
        this.editor.setEditorPartName(this.editor.getEditorInput().getResultData().getResultBo().getEditorResultName());
      }
      else if (CommonUtils.isEqual(entry.getKey(), MODEL_TYPE.USER_PREFERENCE) &&
          CommonUtils.isNotNull(this.rvwResultDecimalPrefAction)) {
        this.rvwResultDecimalPrefAction.setTooltipForOpenDecimalPreferencesWindowAction();
        this.editor.setDecimalPref();
      }
    }
  }

  /**
   * @throws ApicWebServiceException
   */
  private void refreshRvwQnaireRespVersion() throws ApicWebServiceException {

    // Existing Review Qnaire resp version data set
    final Set<QnaireRespStatusData> rvwQnaireRespDataSet = getResultData().getResponse().getQnaireDataForRvwSet();

    // Get the latest revision numbers for all the qnaire resps
    Map<Long, QnaireRespVersionData> rvwQnaireRespVersDataMap =
        new RvwQnaireRespVersionServiceClient().getLatestRvwQnaireRespVersion(rvwQnaireRespDataSet);

    for (QnaireRespStatusData qnaireResp : rvwQnaireRespDataSet) {
      QnaireRespVersionData qnaireRespVersionData = rvwQnaireRespVersDataMap.get(qnaireResp.getQuesRespId());
      Long revNum = qnaireRespVersionData.getRevisionNum();
      // check if the existing rev number is matching with the latest rev number, if both are different then update the
      // rev number along with qnaire version name & qnaire resp names.
      if (!CommonUtils.isEqual(qnaireResp.getRevisionNum(), revNum)) {
        // Updating the existing qnaire data set
        qnaireResp.setRevisionNum(revNum);
        qnaireResp.setVersionName(qnaireRespVersionData.getVersionName());
        qnaireResp.setQnaireRespName(qnaireRespVersionData.getQnaireRespName());
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
   * @param selection Icdm-549
   */
  private void selectionListener(final ISelection selection) {
    if (!(((IStructuredSelection) selection).getFirstElement() instanceof CDRResultParameter)) /* ICDM 599 */ {
      this.treeViewerFilter.treeSelectionListener(selection);
      setStatusBarMessage(true);
    }
  }


  /**
   * @return the ruleFilterGridLayer
   */
  @SuppressWarnings("rawtypes")
  public CustomFilterGridLayer getCustomFilterGridLayer() {
    return this.paramFilterGridLayer;
  }

  /**
   * @return
   */
  private Action createCollapseAllAction() {
    Action collapseAllAction = new Action() {

      @Override
      public void run() {
        ReviewResultParamListPage.this.paramFilterGridLayer.getBodyLayer().getTreeLayer().collapseAll();
      }

    };
    collapseAllAction.setEnabled(true);
    collapseAllAction.setText("Collapse All Groups");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COLLAPSE_NAT_16X16);
    collapseAllAction.setImageDescriptor(imageDesc);
    return collapseAllAction;
  }


  /**
   * @return the natTable
   */
  public CustomNATTable getNatTable() {
    return this.natTable;
  }


  /**
   * @return the selectionProvider
   */
  public RowSelectionProvider<CDRResultParameter> getSelectionProvider() {
    return this.selectionProvider;
  }

  /**
   *
   */
  public void reconstructNatTable() {

    this.natTable.dispose();
    this.propertyToLabelMap.clear();

    this.paramFilterGridLayer = null;
    if (this.toolBarManager != null) {
      this.toolBarManager.removeAll();
    }
    if (this.tableForm.getToolBarManager() != null) {
      this.tableForm.getToolBarManager().removeAll();
    }
    createTable();
    // First the form's body is repacked and then the section is repacked
    // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable
    this.tableForm.getBody().pack();
    this.tableSection.layout();

    if (!this.filterTxt.getText().isEmpty()) {
      this.filterTxt.setText(this.filterTxt.getText());
    }

    if (this.natTable != null) {
      this.natTable.doCommand(new StructuralRefreshCommand());
      this.natTable.doCommand(new VisualRefreshCommand());
      this.natTable.refresh();
    }
  }


  /**
   * Method to refresh column filters to reflect the newly set score/comment and already applied col filters state would
   * be maintained
   *
   * @param oldValuesList list of old values
   */
  public void refreshColFilters(final List<Object> oldValuesList) {
    HashMap<Integer, Object> copy = new HashMap<>();
    List<Integer> oldColValues = new ArrayList<>();
    oldColValues.addAll(this.paramFilterGridLayer.getBodyLayer().getColumnReorderLayer().getColumnIndexOrder());
    Map<Integer, Object> originalMap = this.paramFilterGridLayer.getComboBoxFilterRowHeaderLayer()
        .getFilterRowDataLayer().getFilterRowDataProvider().getFilterIndexToObjectMap();

    // list of params displayed in the table
    List list = this.paramFilterGridLayer.getBodyDataProvider().getList();

    /**
     * if its string it would be SELECT_ALL meaning filter is not set for that col. if col filter is set then it would
     * be list of values selected in the filter
     */


    handleRefreshForCommentColumn(oldValuesList, copy, originalMap, list);

    handleRefreshForScoreColumn(oldValuesList, copy, originalMap, list);

    handleRefreshForResultColumn(oldValuesList, copy, originalMap, list);

    /**
     * If fiter is applied in any other column maintain the filterlist
     */
    for (Entry<Integer, Object> entrySet : originalMap.entrySet()) {
      if ((originalMap.get(entrySet.getKey()) instanceof ArrayList) && !validateColNum(entrySet.getKey())) {
        List<Object> value = new ArrayList<>((ArrayList) originalMap.get(entrySet.getKey()));
        copy.put(entrySet.getKey(), value);
      }
    }
    // do structural refresh to refresh the col filters to display the newly set value
    this.paramFilterGridLayer.getBodyDataLayer().fireLayerEvent(new StructuralRefreshEvent(
        this.paramFilterGridLayer.getComboBoxFilterRowHeaderLayer().getFilterRowDataLayer()));

    // add the map containing the filtered cols to the new map - maintians the old filter state before refresh
    originalMap.putAll(copy);

    // apply the filter
    this.paramFilterGridLayer.getComboBoxFilterRowHeaderLayer().getFilterRowDataLayer().getFilterRowDataProvider()
        .setFilterIndexToObjectMap(originalMap);

    ReviewResultParamListPage.this.paramFilterGridLayer.getComboGlazedListsFilterStrategy().applyFilter(originalMap);
    this.paramFilterGridLayer.getBodyLayer().getColumnReorderLayer().getColumnIndexOrder().clear();
    this.paramFilterGridLayer.getBodyLayer().getColumnReorderLayer().getColumnIndexOrder().addAll(oldColValues);
    this.paramFilterGridLayer.getBodyLayer().getColumnReorderLayer().reorderColumnPosition(1, 1);
  }

  /**
   * @param oldValuesList
   * @param copy
   * @param originalMap
   * @param list
   */
  private void handleRefreshForResultColumn(final List<Object> oldValuesList, final HashMap<Integer, Object> copy,
      final Map<Integer, Object> originalMap, final List list) {
    if (originalMap.get(SEC_RESULT_COL_NUMBER) instanceof ArrayList) {
      Set<Object> displayedColValues = new HashSet<>();
      List<Object> value = new ArrayList<>((ArrayList) originalMap.get(SEC_RESULT_COL_NUMBER));

      for (Object param : list) {
        displayedColValues.add(this.resultData.getCustomSecondaryResult((CDRResultParameter) param));
      }
      if (oldValuesList != null) {
        value.removeAll(oldValuesList);
      }
      displayedColValues.addAll(value);
      List<Object> newFilterList = new ArrayList<>(displayedColValues);
      copy.put(SEC_RESULT_COL_NUMBER, newFilterList);
    }
  }

  /**
   * @param oldValuesList
   * @param copy
   * @param originalMap
   * @param list
   */
  private void handleRefreshForScoreColumn(final List<Object> oldValuesList, final HashMap<Integer, Object> copy,
      final Map<Integer, Object> originalMap, final List list) {
    if (originalMap.get(SCORE_COL_NUMBER) instanceof ArrayList) {
      Set<Object> displayedColValues = new HashSet<>();
      List<Object> value = new ArrayList<>((ArrayList) originalMap.get(SCORE_COL_NUMBER));

      for (Object param : list) {
        displayedColValues.add(this.resultData.getScoreUIType((CDRResultParameter) param));
      }

      if (oldValuesList != null) {
        value.removeAll(oldValuesList);
      }
      displayedColValues.addAll(value);
      List<Object> newFilterList = new ArrayList<>(displayedColValues);
      copy.put(SCORE_COL_NUMBER, newFilterList);
    }
  }

  /**
   * @param oldValuesList
   * @param copy
   * @param originalMap
   * @param list
   */
  private void handleRefreshForCommentColumn(final List<Object> oldValuesList, final HashMap<Integer, Object> copy,
      final Map<Integer, Object> originalMap, final List list) {
    /**
     * if score or comment is set , the filter should maintain already filtered values if any along with new values
     */
    if (originalMap.get(COMMENT_COL_NUMBER) instanceof ArrayList) {
      List<Object> value = new ArrayList<>((ArrayList) originalMap.get(COMMENT_COL_NUMBER));

      Set<Object> displayedColValues = new HashSet<>();
      for (Object param : list) {
        if (((CDRResultParameter) param).getRvwComment() == null) {
          displayedColValues.add("");
        }
        else {
          displayedColValues.add(((CDRResultParameter) param).getRvwComment());
        }
      }
      if (oldValuesList != null) {
        value.removeAll(oldValuesList);
      }
      displayedColValues.addAll(value);
      List<Object> newFilterList = new ArrayList<>(displayedColValues);
      copy.put(COMMENT_COL_NUMBER, newFilterList);
    }
  }


  /**
   * @param colNumber
   * @return
   */
  private boolean validateColNum(final Integer colNumber) {
    return (colNumber == ReviewResultParamListPage.IMP_VALUE_COL_NUMBER) ||
        (colNumber == ReviewResultParamListPage.COMMENT_COL_NUMBER) ||
        (colNumber == ReviewResultParamListPage.SCORE_COL_NUMBER) ||
        (colNumber == ReviewResultParamListPage.SEC_RESULT_COL_NUMBER);
  }

  /**
   * @return the filterTxt
   */
  public Text getFilterTxt() {
    return this.filterTxt;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReviewResultBO getDataHandler() {
    return this.editor.getEditorInput().getDataHandler();
  }

  /**
   * @return CustomGroupByHeaderLayer
   */
  public GroupByHeaderLayer getGroupByHeaderLayer() {
    return this.groupByHeaderLayer;
  }


  /**
   * @return the commentHeaderLabel
   */
  public static String getCommentHeaderLabel() {
    return COMMENT_HEADER_LABEL;
  }


  /**
   * @return the selectedColPostn
   */
  public int getSelectedColPostn() {
    return this.selectedColPostn;
  }


  /**
   * @return the selectedRowPostn
   */
  public int getSelectedRowPostn() {
    return this.selectedRowPostn;
  }


  /**
   * @return the paramFilterGridLayer
   */
  public CustomFilterGridLayer getParamFilterGridLayer() {
    return this.paramFilterGridLayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setActive(final boolean active) {
    if (this.editor.getEditorInput().getCdrReviewResultParam() != null) {
      getSelectionProvider()
          .setSelection(new StructuredSelection(this.editor.getEditorInput().getCdrReviewResultParam()));
    }
    else if ((null != this.resultData.getResultBo().getParameters()) &&
        !this.resultData.getResultBo().getParameters().isEmpty()) {
      getSelectionProvider()
          .setSelection(new StructuredSelection(this.resultData.getResultBo().getParameters().first()));
    }
    super.setActive(active);
  }


  /**
   * @return the treeViewerFilter
   */
  public CdrTreeViewerFilter getTreeViewerFilter() {
    return this.treeViewerFilter;
  }


  /**
   * @param treeViewerFilter the treeViewerFilter to set
   */
  public void setTreeViewerFilter(final CdrTreeViewerFilter treeViewerFilter) {
    this.treeViewerFilter = treeViewerFilter;
  }

  /**
   * @return hidden column names
   */
  public List<String> getHiddenColumns() {
    CustomDefaultBodyLayerStack bodyLayer = this.paramFilterGridLayer.getBodyLayer();
    Collection<Integer> hiddenColCollection = bodyLayer.getColumnHideShowLayer().getHiddenColumnIndexes();
    List<String> hiddenColNames = new ArrayList<>();
    for (Integer hiddenCol : hiddenColCollection) {
      hiddenColNames.add(this.propertyToLabelMap.get(hiddenCol).replace("\n", " "));
    }
    return hiddenColNames;

  }

  /**
   * @return set of attribute IDs visible in the nat table
   */
  public Set<Long> getFilteredCdrParamIds() {
    return (Set<Long>) this.paramFilterGridLayer.getBodyDataProvider().getList().stream()
        .map(row -> ((CDRResultParameter) row).getId()).collect(Collectors.toSet());
  }


  /**
   * @return the quesNotFilled
   */
  public boolean isQuesNotFilled() {
    return this.quesNotFilled;
  }

}
