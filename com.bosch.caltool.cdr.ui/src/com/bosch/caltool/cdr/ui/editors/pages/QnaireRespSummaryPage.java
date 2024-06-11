/*
 * \ * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.edit.editor.MultiLineTextCellEditor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByTreeFormat;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.resize.command.InitializeAutoResizeRowsCommand;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.sort.ISortModel;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.tree.SortableTreeComparator;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellEditorMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.util.GCFactory;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.QnaireRespSummaryAction;
import com.bosch.caltool.cdr.ui.actions.QnaireRespSummaryDoubleClickAction;
import com.bosch.caltool.cdr.ui.actions.QnaireRespToolBarActionSet;
import com.bosch.caltool.cdr.ui.dialogs.QnaireRespVersionDialog;
import com.bosch.caltool.cdr.ui.editors.QnaireRespEditorInput;
import com.bosch.caltool.cdr.ui.editors.QnaireResponseEditor;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.FilterRowCustomConfiguration;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.QnaireRespColHeaderLabelAccumulator;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.QnaireRespNatInputToColConverter;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.QuestionnaireNatToolTip;
import com.bosch.caltool.cdr.ui.table.filters.QnaireRespOutlineNatFilter;
import com.bosch.caltool.cdr.ui.table.filters.QnaireResponseToolBarFilters;
import com.bosch.caltool.cdr.ui.table.filters.QuestionColumnFilterMatcher;
import com.bosch.caltool.cdr.ui.views.QnaireRespOutlinePageCreator;
import com.bosch.caltool.cdr.ui.views.providers.OutlineQnaireRespTreeViewContentProvider;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler.SortColumns;
import com.bosch.caltool.icdm.common.ui.combo.BasicObjectCombo;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.natsupport.CustomSortFilterGroupEnabler;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomColumnPropertyAccessor;
import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * This will be summary page for question response.
 *
 * @author mkl2cob
 */
public class QnaireRespSummaryPage extends AbstractGroupByNatFormPage implements ISelectionListener {

  /** editor instance. */
  private final QnaireResponseEditor qnaireRespEditor;

  /** non scrollable form. */
  private Form nonScrollableForm;

  /** main sash form. */
  private SashForm mainComposite;

  /** FormToolkit instance. */
  private FormToolkit formToolkit;

  /** composite for table viewer. */
  private Composite composite;

  /** section for nat table. */
  private Section tableSection;

  /** form for nat table. */
  private Form tableForm;

  /** filter text for nat table. */
  private Text filterTxt;

  /** CustomFilterGridLayer. */
  // iCDM-1991
  private CustomFilterGridLayer<Object> questionsFilterGridLayer;

  /** CustomNATTable. */
  private CustomNATTable natTable;

  /** CUSTOM_COMPARATOR Label. */
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  /** ToolBarManager. */
  private ToolBarManager toolBarManager;

  /** RowSelectionProvider. */
  private RowSelectionProvider<?> selectionProvider;

  /** row count of table. */
  private int totTableRowCount;

  /** QuesResponseColHeaderLabelAccumulator. */
  private QnaireRespColHeaderLabelAccumulator columnLabelAccumulator;

  /** QuesResponseToolBarActionSet. */
  private QnaireRespToolBarActionSet toolBarActionSet;

  /** The tool bar filters. */
  private QnaireResponseToolBarFilters toolBarFilters;

  /** REMARK_EDITOR label. */
  private static final String REMARK_CNG_LBL = "remarkEditor";

  /** the nat filter instance for questionnaire response. */
  // iCDM-1991
  private QnaireRespOutlineNatFilter outlineNatFilter;

  /** the question column filter matcher instance for review questionnaire answer. */
  // iCDM-1991
  private QuestionColumnFilterMatcher<Object> allColumnFilterMatcher;

  /** tree view content provider for Outline Questionnaire Response. */
  private final OutlineQnaireRespTreeViewContentProvider qRespCntntProvider;

  /** The current selection. */
  private Object currentSelection;

  /** x-coordinate. */
  private static final int XCOORDINATE_TEN = 10;

  /** y-coordinate. */
  private static final int YCOORDINATE_TEN = 10;

  /** The group by header layer. */
  private GroupByHeaderLayer groupByHeaderLayer;

  /** The rowset. */
  private final HashSet<Integer> rowset = new HashSet<>();

  /** the cell editor mouse event matcher for the ui binding when double clicking on a cell. */
  // 226387
  private final CellEditorMouseEventMatcher cellEditorMouseEventMatcher = new CellEditorMouseEventMatcher();

  /** Data Handler for Questionnaire Response. */
  private final QnaireRespEditorDataHandler dataHandler;

  private boolean resetState;

  private Action languageEng;

  private Action languageGer;

  private String currentLanguage;

  private int selectedCol;

  private BasicObjectCombo<RvwQnaireRespVersion> versionsCombo;

  private Button showDetailsBtn;

  private Button createVersionBtn;

  private Label statusMsgLabel;

  private ConcurrentMap<Integer, String> propertyToLabelMap;

  private Composite leftComp;

  private RvwQnaireResponse curRvwQnResponse;

  private static final String QUES_VERSION_SPLITTER = "  :  ";

  /**
   * Instantiates a new qnaire resp summary page.
   *
   * @param editor FormEditor
   * @param formId String
   * @param title String
   */
  public QnaireRespSummaryPage(final FormEditor editor, final String formId, final String title) {
    super(editor, formId, title);
    this.qnaireRespEditor = (QnaireResponseEditor) editor;
    this.dataHandler = getEditorInput().getQnaireRespEditorDataHandler();
    this.qRespCntntProvider = new OutlineQnaireRespTreeViewContentProvider(this.dataHandler, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    this.currentLanguage = PlatformUI.getPreferenceStore().getString(ApicConstants.LANGUAGE);
    this.dataHandler.setQnaireLanguage(Language.getLanguage(this.currentLanguage).getText());
    this.nonScrollableForm = this.qnaireRespEditor.getToolkit().createForm(parent);
    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);
    ToolBarManager topToolBarManager = (ToolBarManager) this.nonScrollableForm.getToolBarManager();
    createLanguageRadio(topToolBarManager);
    topToolBarManager.add(new Separator());
    addHelpAction(topToolBarManager);
    topToolBarManager.update(true);
    getQnnaireDescription();
    final GridLayout gridLayout = new GridLayout();
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;

    this.mainComposite = new SashForm(this.nonScrollableForm.getBody(), SWT.HORIZONTAL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(gridData);

    final ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);

  }


  /**
   *
   */
  public void getQnnaireDescription() {
    if (QnaireRespSummaryPage.this.dataHandler.getQnaireLanguage() != null) {
      String desc = QnaireRespSummaryPage.this.dataHandler.getNameExtByLanguage();
      QnaireRespSummaryPage.this.nonScrollableForm.setText("Questionnaire Response - " + desc);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    createInnerComposite();
    // iCDM-1991
    getSite().getPage().addSelectionListener(this);
  }

  /**
   * Creates the inner composite.
   */
  private void createInnerComposite() {
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
   * Creates the table viewer section.
   */
  private void createTableViewerSection() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.tableSection = SectionUtil.getInstance().createSection(this.composite, this.formToolkit, "Questions Response");

    this.tableSection.setLayoutData(gridData);

    createTableForm(this.formToolkit);
    this.tableSection.setClient(this.tableForm);
    this.tableSection.getDescriptionControl().setEnabled(false);
  }

  /**
   * Create filter text.
   */
  private void createFilterTxt() {
    this.filterTxt.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.filterTxt.setMessage("type filter text");
    this.filterTxt.addModifyListener(event -> {
      String text = QnaireRespSummaryPage.this.filterTxt.getText().trim();
      QnaireRespSummaryPage.this.allColumnFilterMatcher.setFilterText(text, true);
      QnaireRespSummaryPage.this.questionsFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);

      QnaireRespSummaryPage.this.questionsFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(QnaireRespSummaryPage.this.questionsFilterGridLayer.getSortableColumnHeaderLayer()));

      setStatusBarMessage(QnaireRespSummaryPage.this.groupByHeaderLayer, false);
    });
  }

  private void createTableForm(final FormToolkit toolkit) {
    // create table form
    this.tableForm = toolkit.createForm(this.tableSection);

    this.statusMsgLabel = LabelUtil.getInstance().createLabel(this.tableForm.getBody(), "");

    final GridLayout parentLayout = new GridLayout();
    parentLayout.numColumns = 2;

    final Composite parentComp = new Composite(this.tableForm.getBody(), SWT.NONE);
    parentComp.setLayout(parentLayout);
    parentComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    createVersionComposite(parentComp);

    String selectedQnaireRespVersStatus = this.versionsCombo.getSelectedItem().getQnaireRespVersStatus();
    if (CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType().equals(selectedQnaireRespVersStatus)) {
      getStatusMsgLabel().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
      getStatusMsgLabel().setText(CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getUiType());
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP.getDbType().equals(selectedQnaireRespVersStatus)) {
      getStatusMsgLabel().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
      getStatusMsgLabel().setText(CDRConstants.QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP.getUiType());
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType().equals(selectedQnaireRespVersStatus)) {
      getStatusMsgLabel().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
      getStatusMsgLabel().setText(CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getUiType());
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getDbType().equals(selectedQnaireRespVersStatus)) {
      getStatusMsgLabel().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
      getStatusMsgLabel().setText(CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getUiType());
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType().equals(selectedQnaireRespVersStatus)) {
      getStatusMsgLabel().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
      getStatusMsgLabel().setText(CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getUiType());
    }

    // create filter text
    this.filterTxt = toolkit.createText(this.tableForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    createFilterTxt();
    // get parameters
    this.tableForm.getBody().setLayout(new GridLayout());
    createTable();
  }

  /**
   * @param parentComp
   */
  private void createVersionComposite(final Composite parentComp) {
    final GridLayout mainLayout = new GridLayout(2, false);

    final Composite mainComp = new Composite(parentComp, SWT.NONE);
    mainComp.setLayout(mainLayout);
    GridData mainGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
    mainComp.setLayoutData(mainGridData);

    // creating left and right composite to group controls
    this.leftComp = new Composite(mainComp, SWT.NONE);
    GridLayout gridLayout = new GridLayout(3, false);
    gridLayout.verticalSpacing = 0;
    this.leftComp.setLayout(gridLayout);
    this.leftComp.setLayoutData(mainGridData);
    LabelUtil.getInstance().createLabel(this.leftComp, "Select version : ");


    loadVersionsInCombo();

    final Button loadVersionsBtn = new Button(this.leftComp, SWT.NONE);
    loadVersionsBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.RELOAD_16X16));
    loadVersionsBtn.setToolTipText("Load Version");

    loadVersionsBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        Runnable runnable = () -> loadSelQnaireVersion(true);
        BusyIndicator.showWhile(Display.getDefault(), runnable);
      }
    });

    // right composite
    final Composite rightComp = new Composite(mainComp, SWT.NONE);
    rightComp.setLayout(gridLayout);
    rightComp.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, true));

    this.showDetailsBtn = new Button(rightComp, SWT.NONE);
    this.showDetailsBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.LIST_DETAILS));
    this.showDetailsBtn.setToolTipText("Show Details");
    this.showDetailsBtn.setEnabled(!this.dataHandler.isModifiable());
    this.showDetailsBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        QnaireRespVersionDialog versDialog = new QnaireRespVersionDialog(Display.getDefault().getActiveShell(),
            QnaireRespSummaryPage.this.dataHandler, "Show Questionnaire Response Version Details", "",
            "Show Questionnaire Response Version Details", false);
        versDialog.open();
      }
    });

    // separator between buttons
    Label separator = new Label(rightComp, SWT.SEPARATOR | SWT.VERTICAL);
    GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
    gridData.heightHint = 25;
    separator.setLayoutData(gridData);

    this.createVersionBtn = new Button(rightComp, SWT.NONE);
    this.createVersionBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ADD_16X16));
    this.createVersionBtn.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
    this.createVersionBtn.setToolTipText("Review and Baseline current changes");
    try {
      // enable the button based on questionnaire access rights
      this.createVersionBtn.setEnabled(checkQnaireAccessRights());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    this.createVersionBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        createQnaireRespVersion();
      }

    });
    this.createVersionBtn.setEnabled(this.dataHandler.isModifiable());
  }

  /**
  *
  */
  private void loadSelQnaireVersion(final boolean isNattableReconstructNeeded) {
    RvwQnaireRespVersion rvwQnaireRespVersion = QnaireRespSummaryPage.this.versionsCombo.getSelectedItem();
    QnaireRespSummaryPage.this.dataHandler.populateDataModel(rvwQnaireRespVersion, null);

    // Reset the selection value
    this.currentSelection = null;
    Action addNewVersionAction =
        QnaireRespSummaryPage.this.qnaireRespEditor.getQnaireRespVersionPage().getAddNewVersionAction();
    if (addNewVersionAction != null) {
      addNewVersionAction.setEnabled(QnaireRespSummaryPage.this.dataHandler.isModifiable());
    }

    getQnnaireDescription();
    refreshOutlineTree();
    refreshNatTable(isNattableReconstructNeeded);
    refreshStatusMessage();
    setStatusBarMessage(QnaireRespSummaryPage.this.groupByHeaderLayer, false);
  }

  /**
   * method to call create qnaire resp version dialog
   */
  public void createQnaireRespVersion() {
    QnaireRespVersionDialog versDialog = new QnaireRespVersionDialog(Display.getDefault().getActiveShell(),
        QnaireRespSummaryPage.this.dataHandler, "Create a Questionnaire Response Version",
        "Fill Version and Review Information ", "Baseline - Questionnaire Response", true);
    versDialog.open();
  }

  /**
   * @return true if the user has access to questionnaire / has Apic Write Access
   * @throws ApicWebServiceException as expection
   */
  public boolean checkQnaireAccessRights() throws ApicWebServiceException {
    Long questionnaireId = this.dataHandler.getQnaireDefHandler().getQnaireVersion().getQnaireId();
    CurrentUserBO currentUserBO = new CurrentUserBO();
    NodeAccess curUserAccRight = currentUserBO.getNodeAccessRight(questionnaireId);
    return currentUserBO.hasApicWriteAccess() || ((curUserAccRight != null) && curUserAccRight.isWrite());
  }

  /**
   * @param versionsCombo
   */
  private void loadVersionsInCombo() {
    Collection<RvwQnaireRespVersion> qnaireRespVersions =
        this.dataHandler.getQnaireVersions(this.dataHandler.getQuesResponse().getId());

    if (null == this.versionsCombo) {
      this.versionsCombo = new BasicObjectCombo<RvwQnaireRespVersion>(this.leftComp, SWT.READ_ONLY) {

        /**
         * {@inheritDoc}
         */
        @Override
        protected String getName(final RvwQnaireRespVersion element) {
          try {
            return element.getRevNum() + QUES_VERSION_SPLITTER +
                ApicUtil.formatDate(DateFormat.DATE_FORMAT_09, element.getCreatedDate()) + QUES_VERSION_SPLITTER +
                element.getVersionName();
          }
          catch (ParseException e) {
            CDMLogger.getInstance().error(e.getMessage(), e);
          }
          return element.getName() + QUES_VERSION_SPLITTER + element.getCreatedDate() + QUES_VERSION_SPLITTER +
              element.getVersionName();
        }
      };
    }
    this.versionsCombo.setElements(qnaireRespVersions);
    for (RvwQnaireRespVersion qnaireRespVers : qnaireRespVersions) {
      if (CommonUtils.isEqual(qnaireRespVers.getRevNum(),
          this.dataHandler.getQnaireRespModel().getRvwQnrRespVersion().getRevNum())) {
        // To set Working Set by Default selection in combo
        this.versionsCombo.select(this.versionsCombo.getIndex(qnaireRespVers));
      }
    }
  }


  /**
   * method to set selection in the combo box
   *
   * @param rvwQnaireRespVersion as input for selection
   */
  public void setVersionInCombo(final RvwQnaireRespVersion rvwQnaireRespVersion) {
    // To set Working Set by Default selection in combo
    this.versionsCombo.select(this.versionsCombo.getIndex(rvwQnaireRespVersion));
  }

  private void createLanguageRadio(final ToolBarManager topToolBarManager) {

    // English language action
    this.languageEng = new Action("EN", SWT.RADIO) {


      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        QnaireRespSummaryPage.this.dataHandler.setQnaireLanguage(Language.ENGLISH.getText());
        QnaireRespSummaryPage.this.currentLanguage = Language.ENGLISH.getText();
        setLanguageFields();
        getQnnaireDescription();
        refreshOutlineTree();
        refreshNatTable(false);
      }
    };
    topToolBarManager.add(this.languageEng);
    topToolBarManager.add(new Separator());

    // German language action
    this.languageGer = new Action("DE", SWT.RADIO) {

      @Override
      public void run() {
        QnaireRespSummaryPage.this.dataHandler.setQnaireLanguage(Language.GERMAN.getText());
        QnaireRespSummaryPage.this.currentLanguage = Language.GERMAN.getText();
        setLanguageFields();
        getQnnaireDescription();
        refreshOutlineTree();
        refreshNatTable(false);
      }
    };
    topToolBarManager.add(this.languageGer);
    setLanguageFields();
  }


  /**
   * set default language
   */
  private void setLanguageFields() {
    if (Language.getLanguage(this.currentLanguage) == Language.ENGLISH) {
      this.languageEng.setEnabled(false);
      this.languageGer.setEnabled(true);
    }
    else {
      this.languageGer.setEnabled(false);
      this.languageEng.setEnabled(true);
    }
  }

  /**
   * To refresh Nattable
   *
   * @param isNattableReconstructNeeded as boolean input
   */
  public void refreshNatTable(final boolean isNattableReconstructNeeded) {
    if (this.natTable != null) {
      this.showDetailsBtn.setEnabled(!this.dataHandler.isModifiable());
      this.createVersionBtn.setEnabled(this.dataHandler.isModifiable());

      if (isNattableReconstructNeeded) {
        // reconstructing nat table to show columns relevant to the selected qnaire response version
        reconstructNatTable();
      }
      else {
        // normal refresh for update operation inside same qnaire resp versions
        this.questionsFilterGridLayer.getEventList().clear();
        this.dataHandler.getAllObjects().sort(getQuestionsRspComparator(SortColumns.SORT_QUES_NUMBER));
        this.questionsFilterGridLayer.getEventList().addAll(this.dataHandler.getAllObjects());
        this.totTableRowCount = this.dataHandler.getAllObjects().size();
        this.natTable.doCommand(new StructuralRefreshCommand());
        this.natTable.doCommand(new VisualRefreshCommand());
        this.natTable.refresh();
      }
    }
  }

  /**
   * Refresh the outline view
   */
  public void refreshOutlineTree() {
    QnaireResponseEditor editor = (QnaireResponseEditor) getEditor();
    if (null != PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView(ApicUiConstants.OUTLINE_TREE_VIEW)) {
      editor.getQuestionnaireResOutlinePageCreator().getQuestResponseOutlinePage().getViewer().refresh();
    }
  }

  /**
   * Creates the table form.
   *
   * @param toolkit the toolkit
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void createTable() {
    this.propertyToLabelMap = new ConcurrentHashMap<>();
    ConcurrentMap<Integer, Integer> columnWidthMap = new ConcurrentHashMap<>();
    // Configure columns
    configureColumnsNATTable(this.propertyToLabelMap, columnWidthMap);

    // NatInputToColumnConverter is used to convert A2LSystemConstantValues (which is given as input to nattable viewer)
    // to the respective column values
    QnaireRespNatInputToColConverter natInputToColumnConverter = new QnaireRespNatInputToColConverter(this.dataHandler);
    IConfigRegistry configRegistry = new ConfigRegistry();
    // Group by model
    GroupByModel groupByModel = new GroupByModel();
    // A Custom Filter Grid Layer is constructed
    // Select cols to be hidden by default
    List<Integer> colsToHide = new ArrayList<>();
    addColsToHide(colsToHide);
    List<Object> allObjects = this.dataHandler.getAllObjects();

    this.totTableRowCount = allObjects.size();
    Collections.sort(allObjects, getQuestionsRspComparator(SortColumns.SORT_QUES_NUMBER));


    RvwAnswerQuestionFormat treeFormat =
        new RvwAnswerQuestionFormat(groupByModel, new CustomColumnPropertyAccessor<>(this.totTableRowCount));


    // iCDM-1991
    // getQuestionsRspComparator(SortColumns.SORT_QUES_NUMBER)

    this.questionsFilterGridLayer =
        new CustomFilterGridLayer(configRegistry, allObjects, this.propertyToLabelMap, columnWidthMap, null,
            natInputToColumnConverter, this, new QnaireRespSummaryDoubleClickAction(this, this.dataHandler),
            groupByModel, colsToHide, false, true, treeFormat, null, false);


    // Enable Tool bar filters
    this.toolBarFilters = new QnaireResponseToolBarFilters(this.dataHandler);
    this.questionsFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());

    // Enable column filters
    this.allColumnFilterMatcher = new QuestionColumnFilterMatcher<>(null, this.dataHandler);
    this.questionsFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    this.outlineNatFilter = new QnaireRespOutlineNatFilter(this.questionsFilterGridLayer, this.dataHandler);
    this.questionsFilterGridLayer.getFilterStrategy()
        .setOutlineNatFilterMatcher(this.outlineNatFilter.getOutlineMatcher());
    createLblAccmlatrConfig(configRegistry, groupByModel);

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setActive(final boolean active) {
    if (active) {
      QnaireResponseEditor editor = (QnaireResponseEditor) getEditor();
      QnaireRespOutlinePageCreator questResponseOutlinePageCreator = editor.getQuestionnaireResOutlinePageCreator();
      if ((questResponseOutlinePageCreator != null) &&
          (questResponseOutlinePageCreator.getQuestResponseOutlinePage() != null) && (null != PlatformUI.getWorkbench()
              .getActiveWorkbenchWindow().getActivePage().findView(ApicUiConstants.OUTLINE_TREE_VIEW))) {
        TreeViewer viewer = questResponseOutlinePageCreator.getQuestResponseOutlinePage().getViewer();
        viewer.setContentProvider(this.qRespCntntProvider);
        viewer.refresh();
        viewer.expandAll();
        QnaireResponseEditor responseEditor = (QnaireResponseEditor) getEditor();
        QnaireRespSummaryPage quesSummaryPge = responseEditor.getQuesSummaryPge();
        RvwQnaireAnswer summaryPageSelection = quesSummaryPge.getCurrentSelection();
        if (summaryPageSelection != null) {
          syncWithOutlinePageTreeViewer(true);
        }
      }
    }
    super.setActive(active);
  }

  /**
   * @return visible column names
   */
  public List<String> getVisibleColumns() {
    CustomDefaultBodyLayerStack bodyLayer = this.questionsFilterGridLayer.getBodyLayer();
    Collection<Integer> hiddenColCollection = bodyLayer.getColumnHideShowLayer().getHiddenColumnIndexes();
    List<String> visibleColNames = new ArrayList<>();
    for (Entry<Integer, String> columnLabel : this.propertyToLabelMap.entrySet()) {
      if (!hiddenColCollection.contains(columnLabel.getKey())) {
        visibleColNames.add(columnLabel.getValue());
      }
    }
    return visibleColNames;
  }

  private class RvwAnswerQuestionFormat extends GroupByTreeFormat<Object> {

    /**
     * @param model model
     * @param columnAccessor column accessor
     */
    public RvwAnswerQuestionFormat(final GroupByModel model, final IColumnAccessor<Object> columnAccessor) {
      super(model, columnAccessor);
    }

    private final Map<Long, Object> parentMapping = new HashMap<>();

    private ISortModel sortModel;


    /**
     * {@inheritDoc}
     */
    @Override
    public void setSortModel(final ISortModel model) {
      this.sortModel = model;
    }

    /**
     * Populate path with a list describing the path from a root node to this element. Upon returning, the list must
     * have size >= 1, where the provided element identical to the list's last element. This implementation will use the
     * first object found for a last name as root node by storing it within a map. If there is already an object stored
     * for the lastname of the given element, it will be used as root for the path.
     */
    @Override
    public void getPath(final List<Object> path, final Object element) {
      if (element instanceof RvwQnaireAnswer) {
        RvwQnaireAnswer answer = (RvwQnaireAnswer) element;
        this.parentMapping.put(answer.getId(), element);
      }
      else if (element instanceof RvwQnaireAnswerOpl) {
        RvwQnaireAnswerOpl answerOpl = (RvwQnaireAnswerOpl) element;
        path.add(this.parentMapping.get(answerOpl.getRvwAnswerId()));
      }

      path.add(element);

    }

    /**
     * Simply always return <code>true</code>.
     *
     * @return <code>true</code> if this element can have child elements, or <code>false</code> if it is always a leaf
     *         node.
     */
    @Override
    public boolean allowsChildren(final Object element) {
      return true;
    }

    /**
     * Returns the comparator used to order path elements of the specified depth. If enforcing order at this level is
     * not intended, this method should return <code>null</code>. We do a simple sorting of the last names of the
     * persons to show so the tree nodes are sorted in alphabetical order.
     */
    @Override
    public Comparator<Object> getComparator(final int depth) {
      return new SortableTreeComparator<>(getQuestionsRspComparator(SortColumns.SORT_QUES_NUMBER), this.sortModel);
    }
  }

  /**
   * Creates the lbl accmlatr config.
   *
   * @param configRegistry the config registry
   * @param groupByModel the group by model
   */
  private void createLblAccmlatrConfig(final IConfigRegistry configRegistry, final GroupByModel groupByModel) {
    // Enable coloring for params using label accumalator
    DataLayer bodyDataLayer = this.questionsFilterGridLayer.getBodyDataLayer();
    bodyDataLayer.setConfigLabelAccumulator(new QnaireRespLabelAccumulator(bodyDataLayer, this.dataHandler));
    // Composite grid layer
    CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
    this.groupByHeaderLayer = new GroupByHeaderLayer(groupByModel, this.questionsFilterGridLayer,
        this.questionsFilterGridLayer.getColumnHeaderDataProvider());
    compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, this.groupByHeaderLayer, 0, 0);
    compositeGridLayer.setChildLayer("Grid", this.questionsFilterGridLayer, 0, 1);
    // Status bar message listener
    this.questionsFilterGridLayer.getBodyDataLayer().getTreeRowModel()
        .registerRowGroupModelListener(() -> setStatusBarMessage(QnaireRespSummaryPage.this.groupByHeaderLayer, false));

    // Col header configuration
    this.columnLabelAccumulator =
        new QnaireRespColHeaderLabelAccumulator(this.questionsFilterGridLayer.getColumnHeaderDataLayer(), this);
    this.questionsFilterGridLayer.getColumnHeaderDataLayer().setConfigLabelAccumulator(this.columnLabelAccumulator);

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

    // Add configurations
    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.natTable.addConfiguration(new GroupByHeaderMenuConfiguration(this.natTable, this.groupByHeaderLayer));

    final CustomNatTableStyleConfiguration natTabConfig = new CustomNatTableStyleConfiguration();
    natTabConfig.setEnableAutoResize(true);
    this.natTable.addConfiguration(natTabConfig);

    // adding filter configuration
    addFilterConfiguration();

    this.natTable.addConfiguration(new SingleClickSortConfiguration());

    this.natTable.addConfiguration(

        getCustomComparatorConfiguration());
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      @Override
      protected PopupMenuBuilder createColumnHeaderMenu(final NatTable localNatTable) {
        return super.createColumnHeaderMenu(localNatTable).withColumnChooserMenuItem();
      }
    });
    addMouseListener();
    addRightClickMenu();

    this.natTable.addConfiguration(new QnaireRespComboBoxEditConfiguration());


    // 226387 - double click on nattable to be editable
    addHeaderConfig();

    this.questionsFilterGridLayer.getBodyDataLayer().unregisterCommandHandler(UpdateDataCommand.class);
    this.questionsFilterGridLayer.getBodyDataLayer().registerCommandHandler(
        new QnaireRespUpdateDataCommandHandler(this.questionsFilterGridLayer, this.dataHandler));

    // 226387 - double click on nattable to be editable - added in this configuration
    this.natTable.addConfiguration(new QnaireResptValidationEditConfiguration());

    // Add configuration for editing directly in the table
    this.natTable.addConfiguration(new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry localConfigRegistry) {
        registerEditableComments(localConfigRegistry);
      }
    });

    createToolBarAction();

    // Configure NAT table
    this.natTable.configure();

    // Load the saved state of NAT table
    loadState();

    // Column chooser configuration
    CustomDefaultBodyLayerStack bodyLayer = this.questionsFilterGridLayer.getBodyLayer();
    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.questionsFilterGridLayer.getColumnHeaderLayer(),
            this.questionsFilterGridLayer.getColumnHeaderDataLayer(), null, null);
    this.natTable.registerCommandHandler(columnChooserCommandHandler);


    // Add selection listener
    addSelectionProvider();

    attachToolTip(this.natTable);

    this.natTable.addMouseListener(new MouseListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void mouseDown(final MouseEvent mouseevent) {

        int rowPosition = QnaireRespSummaryPage.this.natTable.getRowPositionByY(mouseevent.y);
        if (rowPosition == 1) {
          QnaireRespSummaryPage.this.rowset.clear();
          CustomNatTableStyleConfiguration natTabConfig1 = new CustomNatTableStyleConfiguration();
          natTabConfig1.setEnableAutoResize(true);
          QnaireRespSummaryPage.this.natTable.addConfiguration(natTabConfig1);
        }

      }

      @Override
      public void mouseDoubleClick(final MouseEvent arg0) {
        // No Implementation Needed

      }

      @Override
      public void mouseUp(final MouseEvent arg0) {
        // No Implementation Needed

      }
    });
    this.natTable.addOverlayPainter((final GC gc, final ILayer layer) -> {
      int count = QnaireRespSummaryPage.this.natTable.getRowCount();
      for (int i = 0; i < count; i++) {
        int pos = QnaireRespSummaryPage.this.natTable.getRowIndexByPosition(i);
        if (!QnaireRespSummaryPage.this.natTable.isRowPositionResizable(i) ||
            QnaireRespSummaryPage.this.rowset.contains(pos)) {
          continue;
        }

        QnaireRespSummaryPage.this.rowset.add(pos);

        InitializeAutoResizeRowsCommand rowCommand = new InitializeAutoResizeRowsCommand(
            QnaireRespSummaryPage.this.natTable, i, QnaireRespSummaryPage.this.natTable.getConfigRegistry(),
            new GCFactory(QnaireRespSummaryPage.this.natTable));
        QnaireRespSummaryPage.this.questionsFilterGridLayer.doCommand(rowCommand);
      }

    });

  }

  /**
  *
  */
  public void reconstructNatTable() {

    this.natTable.dispose();
    this.propertyToLabelMap.clear();

    this.questionsFilterGridLayer = null;
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
    CommandState expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    expReportService.setExportService(true);
  }

  /**
   *
   */
  private void addHeaderConfig() {
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        // call for explicit code of default behaviour of nattable i.e sort, column filter
        // Second Parameter is set to true for Group by Tree Format
        CustomSortFilterGroupEnabler customSortFilterGroupEnabler = new CustomSortFilterGroupEnabler(uiBindingRegistry,
            true, QnaireRespSummaryPage.this.natTable, QnaireRespSummaryPage.this.questionsFilterGridLayer);
        uiBindingRegistry.registerFirstSingleClickBinding(QnaireRespSummaryPage.this.cellEditorMouseEventMatcher,
            customSortFilterGroupEnabler);
        uiBindingRegistry.registerDoubleClickBinding(QnaireRespSummaryPage.this.cellEditorMouseEventMatcher,
            new MouseEditAction());
        IMenuItemProvider menuItemProvider = (naTTable, popupMenu) -> {
          MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
          menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
          menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
          menuItem.setEnabled(true);
          menuItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
              QnaireRespSummaryPage.this.resetState = true;
              QnaireRespSummaryPage.this.refreshNatFilters();
            }
          });
        };
        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(QnaireRespSummaryPage.this.natTable)
                .withColumnChooserMenuItem().withMenuItemProvider(menuItemProvider).build()));
      }
    });


  }


  /**
   * Load saved state of NAT table.
   */
  private void loadState() {
    try {
      if (this.resetState) {
        this.natTable.resetState();
      }
      this.natTable.loadState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to load QnaireResponseEditor nat table state", ioe,
          com.bosch.calcomp.adapter.logger.Activator.PLUGIN_ID);
    }
  }

  /**
   * Adds the selection provider.
   */
  private void addSelectionProvider() {
    this.selectionProvider =
        new RowSelectionProvider<>(this.questionsFilterGridLayer.getBodyLayer().getSelectionLayer(),
            this.questionsFilterGridLayer.getBodyDataProvider(), false);

    this.selectionProvider.addSelectionChangedListener(event -> {

      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      if ((selection != null) && (selection.getFirstElement() != null)) {
        QnaireRespSummaryPage.this.currentSelection = selection.getFirstElement();
      }


      if (checkSelection(event, selection)) {
        QnaireRespSummaryPage.this.selectionProvider
            .setSelection(new StructuredSelection(QnaireRespSummaryPage.this.currentSelection));
      }
    });
  }

  /**
   * @param event
   * @param selection
   * @return
   */
  private boolean checkSelection(final SelectionChangedEvent event, final IStructuredSelection selection) {
    return (selection != null) && selection.isEmpty() &&
        event.getSource().equals(QnaireRespSummaryPage.this.selectionProvider) &&
        (QnaireRespSummaryPage.this.currentSelection != null);
  }

  /**
   * Register editable comments.
   *
   * @param configRegistry the config registry
   */
  private void registerEditableComments(final IConfigRegistry configRegistry) {

    ((ColumnOverrideLabelAccumulator) this.questionsFilterGridLayer.getBodyDataLayer().getConfigLabelAccumulator())
        .registerColumnOverrides(ApicUiConstants.COLUMN_INDEX_7, REMARK_CNG_LBL + "_7");
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, getRemarkMultiLineTextEditor(),
        DisplayMode.NORMAL, REMARK_CNG_LBL + "_7");
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
        DisplayMode.EDIT, REMARK_CNG_LBL + "_7");
  }

  /**
   * Gets the remark multi line text editor.
   *
   * @return the remark multi line text editor
   */
  private MultiLineTextCellEditor getRemarkMultiLineTextEditor() {
    return new MultiLineTextCellEditor(true) {

      /**
       * {@inheritDoc}
       */
      @Override
      public boolean openInline(final IConfigRegistry localConfigRegistry, final List<String> configLabels) {
        final IStructuredSelection selection =
            (IStructuredSelection) QnaireRespSummaryPage.this.selectionProvider.getSelection();
        final RvwQnaireAnswer quesResp = (RvwQnaireAnswer) (selection.getFirstElement());
        if (QnaireRespSummaryPage.this.dataHandler.isModifiable() &&
            QnaireRespSummaryPage.this.dataHandler.showRemarks(quesResp.getQuestionId())) {
          return super.openInline(localConfigRegistry, configLabels);
        }

        return false;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public boolean openMultiEditDialog() {
        final IStructuredSelection selection =
            (IStructuredSelection) QnaireRespSummaryPage.this.selectionProvider.getSelection();
        final RvwQnaireAnswer quesResp = (RvwQnaireAnswer) (selection.getFirstElement());
        if (QnaireRespSummaryPage.this.dataHandler.isModifiable() &&
            QnaireRespSummaryPage.this.dataHandler.showRemarks(quesResp.getQuestionId())) {
          return super.openMultiEditDialog();
        }
        return false;
      }

      @Override
      public Rectangle calculateControlBounds(final Rectangle cellBounds) {

        Point size = getEditorControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);

        // add a listener that increases/decreases the size of the control if the text is modified
        // as the calculateControlBounds method is only called in case of inline editing, this
        // listener shouldn't hurt anybody else
        getEditorControl().addModifyListener(event -> {
          Point point = getEditorControl().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
          Point loc = getEditorControl().getLocation();
          int height = calculateHeight(cellBounds, point);

          getEditorControl().setBounds(loc.x, loc.y, cellBounds.width, height);
        });

        int calculatedHeight = calculateHeight(cellBounds, size);
        return new Rectangle(cellBounds.x, cellBounds.y, cellBounds.width, calculatedHeight);
      }
    };
  }

  /**
   * Calculate height.
   *
   * @param cellBounds the cell bounds
   * @param point the point
   * @return the int
   */
  private int calculateHeight(final Rectangle cellBounds, final Point point) {
    int height = cellBounds.height;
    int width = cellBounds.width;
    if ((point.x > width) && (point.y <= cellBounds.height)) {
      double heightBoundsDiff = ((double) (point.x)) / ((double) width);
      Double ceil = Math.ceil(heightBoundsDiff);
      int ceilInt = (int) heightBoundsDiff;
      Double fracPart = heightBoundsDiff - ceilInt;
      int heightScale = ceil.intValue();
      if ((fracPart > 0.8) && (fracPart <= 1.0)) {
        heightScale += 1.0;
      }
      height *= (heightScale);
    }
    if (point.y > cellBounds.height) { // Indicates user has pressed Alt + Enter
      height += point.y;
    }
    return height;
  }


  /**
   * Enables tootltip only for cells which contain not fully visible content.
   *
   * @param localNatTable the nat table
   */
  private void attachToolTip(final NatTable localNatTable) {
    // Custom tool tip for Nat table.
    DefaultToolTip toolTip = new QuestionnaireNatToolTip(localNatTable, new String[0], this);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(XCOORDINATE_TEN, YCOORDINATE_TEN));
  }

  // ICDM-1987
  /**
   * Gets the custom comparator configuration.
   *
   * @return the custom comparator configuration
   */
  private IConfiguration getCustomComparatorConfiguration() {

    /**
     * column size for the registry configuration
     */
    final int COLUMN_LABEL_SIZE = 9;

    /**
     * registry label number suffix for questionnaire name
     */
    final int REG_LABEL_SUFFIX_FOR_QUS_NAME = 1;
    /**
     * registry label number suffix for questionnaire hint
     */
    final int REG_LABEL_SUFFIX_FOR_QUS_HINT = 2;
    /**
     * registry label number suffix for series field
     */
    final int REG_LABEL_SUFFIX_FOR_SERIES = 3;
    /**
     * registry label number suffix for measurable field
     */
    final int REG_LABEL_SUFFIX_FOR_MEAS = 4;
    /**
     * registry label number suffix for link field
     */
    final int REG_LABEL_SUFFIX_FOR_LINK = 5;
    /**
     * registry label number suffix for open points field
     */
    final int REG_LABEL_SUFFIX_FOR_OPEN_POINT = 6;
    /**
     * registry label number suffix for remark field
     */
    final int REG_LABEL_SUFFIX_FOR_REMARK = 7;
    /**
     * registry label number suffix for result field
     */
    final int REG_LABEL_SUFFIX_FOR_RESULT = 8;

    return new AbstractRegistryConfiguration() {


      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Register labels
        for (int i = 0; i < COLUMN_LABEL_SIZE; i++) {
          QnaireRespSummaryPage.this.columnLabelAccumulator.registerColumnOverrides(i, CUSTOM_COMPARATOR_LABEL + i);
        }

        // Register column attributes
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsRspComparator(SortColumns.SORT_QUES_NUMBER), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 0);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsRspComparator(SortColumns.SORT_QUES_NAME), DisplayMode.NORMAL,
            CUSTOM_COMPARATOR_LABEL + REG_LABEL_SUFFIX_FOR_QUS_NAME);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsRspComparator(SortColumns.SORT_QUES_HINT), DisplayMode.NORMAL,
            CUSTOM_COMPARATOR_LABEL + REG_LABEL_SUFFIX_FOR_QUS_HINT);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsRspComparator(SortColumns.SORT_SERIES), DisplayMode.NORMAL,
            CUSTOM_COMPARATOR_LABEL + REG_LABEL_SUFFIX_FOR_SERIES);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsRspComparator(SortColumns.SORT_MEASURABLE), DisplayMode.NORMAL,
            CUSTOM_COMPARATOR_LABEL + REG_LABEL_SUFFIX_FOR_MEAS);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsRspComparator(SortColumns.SORT_LINK), DisplayMode.NORMAL,
            CUSTOM_COMPARATOR_LABEL + REG_LABEL_SUFFIX_FOR_LINK);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsRspComparator(SortColumns.SORT_OP), DisplayMode.NORMAL,
            CUSTOM_COMPARATOR_LABEL + REG_LABEL_SUFFIX_FOR_OPEN_POINT);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsRspComparator(SortColumns.SORT_REMARK), DisplayMode.NORMAL,
            CUSTOM_COMPARATOR_LABEL + REG_LABEL_SUFFIX_FOR_REMARK);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsRspComparator(SortColumns.SORT_RESULT), DisplayMode.NORMAL,
            CUSTOM_COMPARATOR_LABEL + REG_LABEL_SUFFIX_FOR_RESULT);

      }
    };
  }

  /**
   * Adds the filter configuration.
   */
  // iCDM-1991
  private void addFilterConfiguration() {
    this.natTable.addConfiguration(new FilterRowCustomConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        super.configureRegistry(configRegistry);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, DisplayMode.NORMAL,
            GridRegion.FILTER_ROW);
      }
    });
  }

  /**
   * Adds the right click menu.
   */
  private void addRightClickMenu() {
    final QnaireRespSummaryAction actionSet = new QnaireRespSummaryAction();

    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(menuManagr -> {
      final IStructuredSelection selection =
          (IStructuredSelection) QnaireRespSummaryPage.this.selectionProvider.getSelection();
      final Object firstElement = selection.getFirstElement();
      if ((firstElement instanceof RvwQnaireAnswer)) {
        final RvwQnaireAnswer quesResp = (RvwQnaireAnswer) (selection.getFirstElement());
        if ((null != quesResp) && QnaireRespSummaryPage.this.dataHandler.isModifiable() &&
            QnaireRespSummaryPage.this.dataHandler.isQuestionVisible(quesResp) && (selection.size() == 1)) {
          actionSet.setEditResponseAction(menuManagr, quesResp, this.dataHandler, QnaireRespSummaryPage.this);
        }
        if ((selection.size() > 1)) {

          switch (this.selectedCol) {
            case CommonUIConstants.COLUMN_INDEX_4:
              actionSet.setFlagMenuAction(menuManagr, selection, this.dataHandler, QnaireRespSummaryPage.this);
              break;
            case CommonUIConstants.COLUMN_INDEX_3:
              actionSet.setFlagMenuAction(menuManagr, selection, this.dataHandler, QnaireRespSummaryPage.this);
              break;
            case CommonUIConstants.COLUMN_INDEX_5:
              actionSet.setLinkContextMenuAction(menuManagr, selection, this.dataHandler, this);
              break;
            case CommonUIConstants.COLUMN_INDEX_7:
              actionSet.setCommentContextMenuAction(menuManagr, selection, this.dataHandler, this);
              break;
            default:
              break;
          }
        }
      }
    });

    final Menu menu = menuMgr.createContextMenu(this.natTable.getShell());
    this.natTable.setMenu(menu);

    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.selectionProvider);
  }


  private void addMouseListener() {

    this.natTable.addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(final MouseEvent mouseEvent) {
        // No Implementation Needed

      }

      @Override
      public void mouseDown(final MouseEvent mouseEvent) {
        if (mouseEvent.button == 3) {
          ILayerCell cell = QnaireRespSummaryPage.this.natTable.getCellByPosition(
              QnaireRespSummaryPage.this.natTable.getColumnPositionByX(mouseEvent.x),
              QnaireRespSummaryPage.this.natTable.getRowPositionByY(mouseEvent.y));
          if (cell != null) {// cell is null when clicking empty area in nattable
            QnaireRespSummaryPage.this.selectedCol =
                LayerUtil.convertColumnPosition(QnaireRespSummaryPage.this.natTable,
                    QnaireRespSummaryPage.this.natTable.getColumnPositionByX(mouseEvent.x),
                    QnaireRespSummaryPage.this.questionsFilterGridLayer.getBodyDataLayer());
          }
        }

      }

      @Override
      public void mouseDoubleClick(final MouseEvent mouseEvent) {
        // No Implementation Needed

      }

    });
  }

  /**
   * Adds the cols to hide.
   *
   * @param colsToHide the cols to hide
   */
  private void addColsToHide(final List<Integer> colsToHide) {
    final HashMap<Integer, Boolean> chooseColumnsToHide = new HashMap<>();
    chooseColumnsToHide.put(CommonUIConstants.COLUMN_INDEX_5, false);
    chooseColumnsToHide.put(CommonUIConstants.COLUMN_INDEX_4, false);
    chooseColumnsToHide.put(CommonUIConstants.COLUMN_INDEX_6, false);
    chooseColumnsToHide.put(CommonUIConstants.COLUMN_INDEX_7, false);
    chooseColumnsToHide.put(CommonUIConstants.COLUMN_INDEX_8, false);
    chooseColumnsToHide.put(CommonUIConstants.COLUMN_INDEX_3, false);

    // check relevant flags of answers
    checkAnswerIfRelevant(chooseColumnsToHide);

    // check relevant flags of Questionnaire Versions
    if (Boolean.TRUE.equals(
        !this.dataHandler.showQnaireVersLinks() || !chooseColumnsToHide.get(CommonUIConstants.COLUMN_INDEX_5))) {
      colsToHide.add(CommonUIConstants.COLUMN_INDEX_5);
    }
    if (Boolean.TRUE.equals(
        !this.dataHandler.showQnaireVersMeasurement() || !chooseColumnsToHide.get(CommonUIConstants.COLUMN_INDEX_4))) {
      colsToHide.add(CommonUIConstants.COLUMN_INDEX_4);
    }
    if (Boolean.TRUE.equals(
        !this.dataHandler.showQnaireVersOpenPoints() || !chooseColumnsToHide.get(CommonUIConstants.COLUMN_INDEX_6))) {
      colsToHide.add(CommonUIConstants.COLUMN_INDEX_6);
    }
    if (Boolean.TRUE.equals(
        !this.dataHandler.showQnaireVersRemarks() || !chooseColumnsToHide.get(CommonUIConstants.COLUMN_INDEX_7))) {
      colsToHide.add(CommonUIConstants.COLUMN_INDEX_7);
    }
    if (Boolean.TRUE.equals(
        !this.dataHandler.showQnaireVersResult() || !chooseColumnsToHide.get(CommonUIConstants.COLUMN_INDEX_8))) {
      colsToHide.add(CommonUIConstants.COLUMN_INDEX_8);
    }
    if (Boolean.TRUE.equals(!this.dataHandler.showQnaireVersSeriesMaturity() ||
        !chooseColumnsToHide.get(CommonUIConstants.COLUMN_INDEX_3))) {
      colsToHide.add(CommonUIConstants.COLUMN_INDEX_3);
    }
  }

  /**
   * Check answer if relevant.
   *
   * @param chooseColumnsToHide the choose columns to hide
   */
  private void checkAnswerIfRelevant(final HashMap<Integer, Boolean> chooseColumnsToHide) {

    for (RvwQnaireAnswer rvwQnaireAnswer : this.dataHandler.getAllQuestionAnswers()) {
      if (this.dataHandler.showLinks(rvwQnaireAnswer.getQuestionId())) {
        chooseColumnsToHide.put(CommonUIConstants.COLUMN_INDEX_5, true);
      }
      if (this.dataHandler.showMeasurement(rvwQnaireAnswer.getQuestionId())) {
        chooseColumnsToHide.put(CommonUIConstants.COLUMN_INDEX_4, true);
      }
      if (this.dataHandler.showOpenPoints(rvwQnaireAnswer.getQuestionId())) {
        chooseColumnsToHide.put(CommonUIConstants.COLUMN_INDEX_6, true);
      }
      if (this.dataHandler.showRemarks(rvwQnaireAnswer.getQuestionId())) {
        chooseColumnsToHide.put(CommonUIConstants.COLUMN_INDEX_7, true);
      }
      if (this.dataHandler.showResult(rvwQnaireAnswer.getQuestionId())) {
        chooseColumnsToHide.put(CommonUIConstants.COLUMN_INDEX_8, true);
      }
      if (this.dataHandler.showSeriesMaturity(rvwQnaireAnswer.getQuestionId())) {
        chooseColumnsToHide.put(CommonUIConstants.COLUMN_INDEX_3, true);
      }
    }
  }

  // ICDM-1987
  /**
   * Get param comparator.
   *
   * @param sortColumns SortColumns
   * @return Comparator
   */
  public Comparator<Object> getQuestionsRspComparator(final SortColumns sortColumns) {


    return (final Object o1, final Object o2) -> {

      int compareTo = 0;
      if ((o1 instanceof RvwQnaireAnswer) && (o2 instanceof RvwQnaireAnswer)) {
        compareTo =
            QnaireRespSummaryPage.this.dataHandler.compareTo((RvwQnaireAnswer) o1, (RvwQnaireAnswer) o2, sortColumns);
      }
      else if ((o1 instanceof RvwQnaireAnswerOpl) && (o2 instanceof RvwQnaireAnswerOpl)) {
        compareTo = QnaireRespSummaryPage.this.dataHandler.compareTo((RvwQnaireAnswerOpl) o1, (RvwQnaireAnswerOpl) o2,
            sortColumns);
      }
      return compareTo;

    };
  }

  /**
   * Configure columns NAT table.
   *
   * @param localPropertyToLabelMap the property to label map
   * @param columnWidthMap the column width map
   */
  private void configureColumnsNATTable(final Map<Integer, String> localPropertyToLabelMap,
      final Map<Integer, Integer> columnWidthMap) {

    localPropertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_0, ApicConstants.SERIAL_NO);
    localPropertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_1, ApicConstants.QUESTION_COL_NAME);
    // ICDM-2188
    localPropertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_2,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_HINT));
    localPropertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_3,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_SERIES_MAT_Y_N));
    localPropertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_4,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_MEASURABLE_Y_N));
    localPropertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_5, CDRConstants.LINK_COL_NAME);
    localPropertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_6,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_OPEN_POINTS));
    localPropertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_7,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_REMARK));
    localPropertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_8, CDRConstants.ANSWER_COL_NAME);
    localPropertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_9, ApicConstants.RESULT_COL_NAME);

    // The below map is used by NatTable to Map Columns with their respective widths
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_0, 5 * 10);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_1, 15 * 13);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_2, 15 * 13);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_3, 15 * 9);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_4, 15 * 10);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_5, 15 * 13);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_6, 15 * 13);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_7, 15 * 13);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_8, 15 * 10);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_9, 15 * 10);

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
   * Creates the tool bar action.
   */
  private void createToolBarAction() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = this.toolBarManager.createControl(this.tableSection);
    this.toolBarActionSet = new QnaireRespToolBarActionSet(this, this.questionsFilterGridLayer);

    // shw headings/questions filters
    this.toolBarActionSet.showHeadingsAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.showQuestionsAction(this.toolBarManager, this.toolBarFilters);


    final Separator separator = new Separator();
    this.toolBarManager.add(separator);

    // show positive,negative,neutral,not answered items
    this.toolBarActionSet.showPositiveResultsAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.showNeutralResultsAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.showNegativeResultsAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.showToBeDoneAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarManager.add(separator);

    this.toolBarActionSet.showInvisibleQuestions(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.update(true);
    // ICDM-2141
    addResetAllFiltersAction();
    this.tableSection.setTextClient(toolbar);

  }


  /**
   * Gets the questions filter grid layer.
   *
   * @return the questionsFilterGridLayer
   */
  public CustomFilterGridLayer<Object> getQuestionsFilterGridLayer() {
    return this.questionsFilterGridLayer;
  }

  /**
   * {@inheritDoc}
   */
  // iCDM-1991
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    IEditorPart activeEditor = getSite().getPage().getActiveEditor();
    if (activeEditor instanceof QnaireResponseEditor) {
      QnaireResponseEditor qrEditor = (QnaireResponseEditor) getSite().getPage().getActiveEditor();
      if ((qrEditor == getEditor()) && (part instanceof OutlineViewPart) &&
          qrEditor.getActivePageInstance().equals(this)) {
        selectionListenerFromOutline(selection);
        updateStatusBar(true);
        // ICDM-2096
        StructuredSelection structuredSelection = (StructuredSelection) selection;
        Object selectedElement = structuredSelection.getFirstElement();
        if (selectedElement instanceof RvwQnaireResponse) {
          QnaireRespSummaryPage.this.curRvwQnResponse = (RvwQnaireResponse) selectedElement;
          RvwQnaireAnswer firstQuestionResponse = this.dataHandler.getFirstQuestionResponse();
          QnaireRespSummaryPage.this.currentSelection = firstQuestionResponse;
          setStructuredSelection();
        }
        else if (selectedElement instanceof RvwQnaireAnswer) {
          QnaireRespSummaryPage.this.curRvwQnResponse = null;
          final RvwQnaireAnswer rvwQnaireAnswer = (RvwQnaireAnswer) selectedElement;
          if (this.dataHandler.checkHeading(rvwQnaireAnswer)) {
            RvwQnaireAnswer firstQuestionResponse = this.dataHandler.getFirstQuestionResponse(rvwQnaireAnswer);
            QnaireRespSummaryPage.this.currentSelection = firstQuestionResponse;
            setStructuredSelection();
          }
        }
      }
    }
  }

  /**
   * set structured selection
   */
  private void setStructuredSelection() {
    if (null != this.currentSelection) {
      StructuredSelection strucSelection = new StructuredSelection(this.currentSelection);
      QnaireRespSummaryPage.this.selectionProvider.setSelection(strucSelection);
    }
  }

  /**
   * Selection listener implementation for selections on outlineFilter.
   *
   * @param selection the selection
   */
  // iCDM-1991
  private void selectionListenerFromOutline(final ISelection selection) {
    this.outlineNatFilter.questionOutlineSelectionListener(selection);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMessage(this.groupByHeaderLayer, outlineSelection);
  }

  /**
   * ICDM - 2096 The below method changes the outline content provider and maintains the selection while switching
   * between pages.
   */
  public void modifyContentProviderAndSelection() {
    QnaireResponseEditor editor = (QnaireResponseEditor) getEditor();
    QnaireRespOutlinePageCreator questResponseOutlinePageCreator = editor.getQuestionnaireResOutlinePageCreator();
    if ((questResponseOutlinePageCreator != null) &&
        (questResponseOutlinePageCreator.getQuestResponseOutlinePage() != null) && (null != PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage().findView(ApicUiConstants.OUTLINE_TREE_VIEW))) {
      TreeViewer viewer = questResponseOutlinePageCreator.getQuestResponseOutlinePage().getViewer();
      viewer.setContentProvider(this.qRespCntntProvider);
      viewer.refresh();
      viewer.expandAll();

      // ICDM-2096
      QnaireResponseEditor responseEditor = (QnaireResponseEditor) getEditor();
      QnaireRespDetailsPage quesDetailsPage = responseEditor.getQuesDetailsPage();
      final RvwQnaireAnswer qNaireDetailsPageSelection = quesDetailsPage.getRvwQnaireAnswer();
      if (qNaireDetailsPageSelection != null) {
        this.currentSelection = qNaireDetailsPageSelection;
        syncWithOutlinePageTreeViewer(false);
        StructuredSelection strucSelection = new StructuredSelection(this.currentSelection);
        QnaireRespSummaryPage.this.selectionProvider.setSelection(strucSelection);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QnaireRespEditorInput getEditorInput() {
    return this.qnaireRespEditor.getEditorInput();
  }


  /**
   * Gets the current selection.
   *
   * @return the currentSelection
   */
  public RvwQnaireAnswer getCurrentSelection() {
    if (this.currentSelection instanceof RvwQnaireAnswer) {
      return (RvwQnaireAnswer) this.currentSelection;
    }
    return null;
  }

  /**
   * ICDM-2096 This method is used for synchronisation between Questionnaire Response Details page and the
   * Outline/Filter View part questions. If the user clicks Next or Previous Button, the corresponding question will be
   * selected in the Outline/Filter View part.
   *
   * @param refreshTree the refresh tree
   */
  public void syncWithOutlinePageTreeViewer(final boolean refreshTree) {
    if (null != PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView(ApicUiConstants.OUTLINE_TREE_VIEW)) {
      QnaireResponseEditor qrEditor = (QnaireResponseEditor) getEditor();
      QnaireRespOutlinePageCreator questResponseOutlinePageCreator = qrEditor.getQuestionnaireResOutlinePageCreator();
      TreeViewer outlineViewer = questResponseOutlinePageCreator.getQuestResponseOutlinePage().getViewer();

      if (null != this.curRvwQnResponse) {
        RvwQnaireResponse outlineSelection = this.curRvwQnResponse;
        StructuredSelection newSelection = new StructuredSelection(outlineSelection);
        outlineViewer.setSelection(newSelection, true);
        selectionListenerFromOutline(newSelection);
      }
      else if (this.currentSelection instanceof RvwQnaireAnswer) {
        RvwQnaireAnswer outlineSelection = (RvwQnaireAnswer) this.currentSelection;
        if (!this.dataHandler.checkHeading((RvwQnaireAnswer) this.currentSelection)) {
          outlineSelection = getParentHeading((RvwQnaireAnswer) this.currentSelection);
        }

        // Set selection
        StructuredSelection newSelection = new StructuredSelection(outlineSelection);
        if (refreshTree) {
          questResponseOutlinePageCreator.getQuestResponseOutlinePage().getViewer().refresh();
        }
        outlineViewer.setSelection(newSelection, true);
        selectionListenerFromOutline(newSelection);
      }
    }
  }

  /**
   * ICDM-2096.
   *
   * @param rvwQnaireAns the rvw qnaire ans
   * @return the parent heading
   */
  private RvwQnaireAnswer getParentHeading(final RvwQnaireAnswer rvwQnaireAns) {
    RvwQnaireAnswer parentRvwQnAns = this.dataHandler.getParentQuestion(rvwQnaireAns);
    if (!this.dataHandler.checkHeading(parentRvwQnAns)) {
      parentRvwQnAns = getParentHeading(parentRvwQnAns);
    }
    return parentRvwQnAns;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setStatusBarMessage(final boolean outlineSelction) {
    this.qnaireRespEditor.updateStatusBar(outlineSelction, this.totTableRowCount,
        this.questionsFilterGridLayer.getRowHeaderLayer().getPreferredRowCount());
  }


  /**
   * ICDM-2141 Add reset filter button.
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.questionsFilterGridLayer);
    addResetFiltersAction();
    getResetFiltersAction().enableResetFilterAction();
  }

  /**
   * Gets the group header layer.
   *
   * @return CustomGroupByHeaderLayer
   */
  public GroupByHeaderLayer getGroupHeaderLayer() {
    return this.groupByHeaderLayer;
  }

  /**
  *
  */
  public void refreshNatFilters() {
    if ((this.natTable != null) && (this.toolBarActionSet != null)) {
      /*
       * Trigger Natpage toolbar-filter event
       */
      this.toolBarActionSet.getShowHeadingAction().runWithEvent(new Event());
    }
  }


  /**
   * @return the selectedCol
   */
  public int getSelectedCol() {
    return this.selectedCol;
  }


  /**
   * @param selectedCol the selectedCol to set
   */
  public void setSelectedCol(final int selectedCol) {
    this.selectedCol = selectedCol;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    loadVersionsInCombo();
    loadSelQnaireVersion(false);
    CommandState expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    expReportService.setExportService(true);
  }


  /**
   *
   */
  private void refreshStatusMessage() {
    String qnaireRespVersStatus =
        this.dataHandler.getQnaireRespModel().getRvwQnrRespVersion().getQnaireRespVersStatus();
    if (CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType().equals(qnaireRespVersStatus)) {
      getStatusMsgLabel().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
      getStatusMsgLabel().setText(CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getUiType());
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP.getDbType().equals(qnaireRespVersStatus)) {
      getStatusMsgLabel().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
      getStatusMsgLabel().setText(CDRConstants.QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP.getUiType());
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType().equals(qnaireRespVersStatus)) {
      getStatusMsgLabel().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
      getStatusMsgLabel().setText(CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getUiType());
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getDbType().equals(qnaireRespVersStatus)) {
      getStatusMsgLabel().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
      getStatusMsgLabel().setText(CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getUiType());
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType().equals(qnaireRespVersStatus)) {
      getStatusMsgLabel().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
      getStatusMsgLabel().setText(CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getUiType());
    }
    getStatusMsgLabel().getParent().layout();
  }


  /**
   * @return the statusMsg
   */
  public Label getStatusMsgLabel() {
    return this.statusMsgLabel;
  }
}
