/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.widgets.nattable.columnChooser.command.DisplayColumnChooserCommandHandler;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;

import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.calcomp.externallink.process.LinkProcessor;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.DataAssessmentQuestionnaireToolBarActionSet;
import com.bosch.caltool.cdr.ui.editors.DataAssessmentQnaireResultsNatToolTip;
import com.bosch.caltool.cdr.ui.editors.DataAssessmentReportEditor;
import com.bosch.caltool.cdr.ui.editors.DataAssessmentReportEditorInput;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.DataAssessmentQnaireNattableLabelAccumulator;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.DataAssessmentQnaireResColumnFilterMatcher;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.DataAssessmentQnaireResultsEditConfiguration;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.FilterRowCustomConfiguration;
import com.bosch.caltool.cdr.ui.table.filters.DataAssessmentQnaireToolBarFilters;
import com.bosch.caltool.icdm.client.bo.cdr.DataAssmntReportDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.editors.AbstractNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentQuestionnaires;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;
import com.bosch.caltool.nattable.CustomColumnHeaderLayerConfiguration;
import com.bosch.caltool.nattable.CustomColumnHeaderStyleConfiguration;
import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author ajk2cob
 */
public class DataAssessmentQuestionnaireResultsPage extends AbstractNatFormPage implements ISelectionListener {

  /**
   * Editor instance
   */
  private final DataAssessmentReportEditor editor;
  /**
   * Data Assessment Report DataHandler
   */
  private final DataAssmntReportDataHandler dataAssmntReportDataHandler;
  /**
   * Non scrollable questionnaireResultsForm
   */
  private Form nonScrollableForm;
  /**
   * Questionnaire results section
   */
  private Section questionnaireResultsSection;
  /**
   * Questionnaire results form
   */
  private Form questionnaireResultsForm;
  /**
   * Filter text
   */
  private Text filterTxt;
  /**
   * Nattable labels
   */
  private Map<Integer, String> propertyToLabelMap;
  /**
   * Nattable column width
   */
  Map<Integer, Integer> columnWidthMap = new HashMap<>();
  /**
   * Questionnaire results filter grid layer
   */
  private CustomFilterGridLayer questionnaireResultsFilterGridLayer;
  /**
   * Questionnaire results nattable
   */
  private CustomNATTable natTable;
  /**
   * Nattable data
   */
  Set<DataAssessmentQuestionnaires> questionnaireDetails;
  /**
   * Questionnaire results column filter matcher
   */
  private DataAssessmentQnaireResColumnFilterMatcher<DataAssessmentQuestionnaires> allColumnFilterMatcher;

  /**
   * Map to hold the filter action text and its changed state (checked - true or false)
   */
  private final Map<String, Boolean> toolBarFilterStateMap = new ConcurrentHashMap<>();
  /**
   * ToolBarManager instance
   */
  private ToolBarManager toolbarManager;

  /**
   * DataAssessmentQnaireToolBarFilters instance
   */
  private DataAssessmentQnaireToolBarFilters toolBarFilters;

  /**
   * row count
   */
  private int totTableRowCount;

  public static final int COLUMN_NUM_QUESTIONNAIRE_RESULTS_WORKPACKAGE = 0;

  public static final int COLUMN_NUM_QUESTIONNAIRE_RESULTS_RESP_NAME = 1;

  public static final int COLUMN_NUM_QUESTIONNAIRE_RESULTS_RESP_TYEP = 2;

  public static final int COLUMN_NUM_QUESTIONNAIRE_RESULTS_QUES_NAME = 3;

  public static final int COLUMN_NUM_QUESTIONNAIRE_RESULTS_ASSESSMENT_PROD_READY = 4;

  public static final int COLUMN_NUM_QUESTIONNAIRE_RESULTS_BASELINE_EXISTING = 5;

  public static final int COLUMN_NUM_QUESTIONNAIRE_RESULTS_POSITIVE = 6;

  public static final int COLUMN_NUM_QUESTIONNAIRE_RESULTS_NEGATIVE = 7;

  public static final int COLUMN_NUM_QUESTIONNAIRE_RESULTS_NEUTRAL = 8;

  public static final int COLUMN_NUM_QUESTIONNAIRE_RESULTS_VERSION_NAME = 9;

  public static final int COLUMN_NUM_QUESTIONNAIRE_RESULTS_REVIEWED_BY = 10;

  public static final int COLUMN_NUM_QUESTIONNAIRE_RESULTS_REVIEWED_ON = 11;

  public static final int COLUMN_NUM_QUESTIONNAIRE_RESULTS_LINK = 12;

  /**
   * column count
   */
  private static final int COLUMN_COUNT = 13;

  /**
   * custom comparator label
   */
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  /**
   * /** Data assessment questionnaire results page, Constructor.
   *
   * @param editor editor
   */
  public DataAssessmentQuestionnaireResultsPage(final FormEditor editor) {
    super(editor, "dataAssessmentQuestionnaireResults", "Questionnaire Results");
    this.editor = (DataAssessmentReportEditor) editor;
    this.dataAssmntReportDataHandler =
        ((DataAssessmentReportEditorInput) editor.getEditorInput()).getDataAssmntReportDataHandler();
  }

  @Override
  public void createPartControl(final Composite parent) {
    // create an non-scrollable form on which widgets are built
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    this.nonScrollableForm.setText("Questionnaire Results");
    // instead of editor.getToolkit().createScrolledForm(parent); in superclass
    // formToolkit is obtained from managed form to create form within questionnaireResultsSection
    ManagedForm mform = new ManagedForm(parent);
    FormToolkit formToolkit = mform.getToolkit();
    // create composite for the questionnaire results section
    Composite composite = this.nonScrollableForm.getBody();
    composite.setLayout(new GridLayout());
    // create questionnaire results section
    this.questionnaireResultsSection =
        formToolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.questionnaireResultsSection.setExpanded(true);
    this.questionnaireResultsSection.setText("Questionnaires");
    this.questionnaireResultsSection.setDescription("Overview of all questionnaires in this A2L and variant");
    this.questionnaireResultsSection.getDescriptionControl().setEnabled(false);
    this.questionnaireResultsSection.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.questionnaireResultsForm = formToolkit.createForm(this.questionnaireResultsSection);
    this.questionnaireResultsForm.getBody().setLayout(new GridLayout());

    // create questionnaire results form
    createQuestionnaireResultsForm(formToolkit);

    // Adding toolbar in the form level
    createQuestionnaireStateToolbar();


    this.questionnaireResultsSection.setClient(this.questionnaireResultsForm);
  }

  private void createQuestionnaireStateToolbar() {
    final Separator separator = new Separator();
    this.toolbarManager = (ToolBarManager) this.questionnaireResultsForm.getToolBarManager();
    DataAssessmentQuestionnaireToolBarActionSet toolBarActionSet =
        new DataAssessmentQuestionnaireToolBarActionSet(this.questionnaireResultsFilterGridLayer, this);

    toolBarActionSet.qnaireReadyForProdFilerAction(this.toolbarManager, this.toolBarFilters);
    toolBarActionSet.qnaireNotReadyForProdFilerAction(this.toolbarManager, this.toolBarFilters);
    this.toolbarManager.add(separator);
    toolBarActionSet.qnaireBaselinedFilerAction(this.toolbarManager, this.toolBarFilters);
    toolBarActionSet.qnaireNotBaseLinedFilerAction(this.toolbarManager, this.toolBarFilters);
    this.toolbarManager.add(separator);
    toolBarActionSet.respTypeRobertBoschFilerAction(this.toolbarManager, this.toolBarFilters);
    toolBarActionSet.respTypeCustomerFilerAction(this.toolbarManager, this.toolBarFilters);
    toolBarActionSet.respTypeOtherFilerAction(this.toolbarManager, this.toolBarFilters);
    addResetAllFiltersAction();

    this.toolbarManager.update(true);
  }

  /**
   * Add reset filter button
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.questionnaireResultsFilterGridLayer);
    addResetFiltersAction();
  }

  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   * This method creates the questionnaire results form
   */
  private void createQuestionnaireResultsForm(final FormToolkit toolkit) {
    createFilterTxt(toolkit);
    createTable();
    addMouseListener();
  }

  /**
   * This method creates filter text for nattable
   */
  private void createFilterTxt(final FormToolkit toolkit) {
    this.filterTxt = TextUtil.getInstance().createFilterText(toolkit, this.questionnaireResultsForm.getBody(),
        getFilterTxtGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      String text = DataAssessmentQuestionnaireResultsPage.this.filterTxt.getText().trim();
      DataAssessmentQuestionnaireResultsPage.this.allColumnFilterMatcher.setFilterText(text, true);
      DataAssessmentQuestionnaireResultsPage.this.questionnaireResultsFilterGridLayer.getFilterStrategy()
          .applyFilterInAllColumns(text);
      DataAssessmentQuestionnaireResultsPage.this.questionnaireResultsFilterGridLayer.getSortableColumnHeaderLayer()
          .fireLayerEvent(
              new FilterAppliedEvent(DataAssessmentQuestionnaireResultsPage.this.questionnaireResultsFilterGridLayer
                  .getSortableColumnHeaderLayer()));
    });
  }

  /**
   * This method creates nattable
   */
  private void createTable() {
    this.questionnaireDetails =
        new HashSet<>(this.dataAssmntReportDataHandler.getDataAssessmentReport().getDataAssmntQnaires());
    this.totTableRowCount = this.questionnaireDetails.size();
    AbstractNatInputToColumnConverter natInputToColumnConverter =
        new DataAssessmentQnaireResNatInputToColumnConverter();
    IConfigRegistry configRegistry = new ConfigRegistry();

    createNatTableColumns();

    this.questionnaireResultsFilterGridLayer =
        new CustomFilterGridLayer(configRegistry, this.questionnaireDetails, this.propertyToLabelMap,
            this.columnWidthMap, getParamComparator(0), natInputToColumnConverter, this, null, true, true);

    this.allColumnFilterMatcher = new DataAssessmentQnaireResColumnFilterMatcher<>();
    this.questionnaireResultsFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    this.natTable = new CustomNATTable(this.questionnaireResultsForm.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL,
        this.questionnaireResultsFilterGridLayer, false, this.getClass().getSimpleName(), this.propertyToLabelMap);

    this.toolBarFilters = new DataAssessmentQnaireToolBarFilters();
    this.questionnaireResultsFilterGridLayer.getFilterStrategy()
        .setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());

    try {
      this.natTable.setProductVersion(new CommonDataBO().getIcdmVersion());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    this.natTable.setLayoutData(getGridData());
    this.natTable.addConfiguration(new CustomNatTableStyleConfiguration());
    this.natTable.addConfiguration(new FilterRowCustomConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configReg) {
        super.configureRegistry(configReg);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configReg.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });
    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable.addConfiguration(
        getCustomComparatorConfiguration(this.questionnaireResultsFilterGridLayer.getColumnHeaderDataLayer()));
    this.natTable.setConfigRegistry(configRegistry);

    // Custom table header style
    CustomColumnHeaderStyleConfiguration columnHeaderStyleConfiguration = new CustomColumnHeaderStyleConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configReg) {
        // configure the painter
        configReg.registerConfigAttribute(CELL_PAINTER, this.cellPaintr, NORMAL, GridRegion.COLUMN_HEADER);
        configReg.registerConfigAttribute(CELL_PAINTER, this.cellPaintr, NORMAL, GridRegion.CORNER);

        // configure whether to render grid lines or not
        // e.g. for the BeveledBorderDecorator the rendering of the grid lines should be disabled
        configReg.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, this.rendrGridLines, NORMAL,
            GridRegion.COLUMN_HEADER);
        configReg.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, this.rendrGridLines, NORMAL,
            GridRegion.CORNER);

        // configure the normal style
        Style cellStyle = new Style();
        cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.bacgrndColor);
        cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, this.foregrndColor);
        cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_BACKGROUND_COLOR, this.gradintBgColor);
        cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_FOREGROUND_COLOR, this.gradintFgColor);
        cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, this.horizontalAlign);
        cellStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, this.verticalAlign);
        cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, this.bordrStyle);
        cellStyle.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.getFont(new FontData("Segoe UI", 9, SWT.NONE)));

        configReg.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, NORMAL, GridRegion.COLUMN_HEADER);
        configReg.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, NORMAL, GridRegion.CORNER);
      }
    };

    this.questionnaireResultsFilterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));

    // Registry Configuration class for data assessment editor which assigns style based on the config labels registered
    // on the cells
    this.natTable.addConfiguration(new DataAssessmentQnaireResultsEditConfiguration());

    // Used for registration/addition of labels for a given column.
    DataLayer bodyDataLayer = this.questionnaireResultsFilterGridLayer.getDummyDataLayer();
    final DataAssessmentQnaireNattableLabelAccumulator qnaireResLabelAccumulator =
        new DataAssessmentQnaireNattableLabelAccumulator(bodyDataLayer);
    bodyDataLayer.setConfigLabelAccumulator(qnaireResLabelAccumulator);

    CustomDefaultBodyLayerStack bodyLayer = this.questionnaireResultsFilterGridLayer.getBodyLayer();
    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.questionnaireResultsFilterGridLayer.getColumnHeaderLayer(),
            this.questionnaireResultsFilterGridLayer.getColumnHeaderDataLayer(), null, null);
    this.natTable.registerCommandHandler(columnChooserCommandHandler);
    this.natTable.configure();

    // Load the saved state of NAT table
    loadState();

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
    // save state
    saveState());

    this.questionnaireResultsFilterGridLayer
        .registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.natTable));

    RowSelectionProvider<DataAssessmentQuestionnaires> selectionProvider =
        new RowSelectionProvider<>(this.questionnaireResultsFilterGridLayer.getBodyLayer().getSelectionLayer(),
            this.questionnaireResultsFilterGridLayer.getBodyDataProvider(), false);

    selectionProvider.addSelectionChangedListener(event -> {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      if (selection.getFirstElement() instanceof DataAssessmentQuestionnaires) {
        IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView(com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.PROPERTIES_VIEW);
        if (viewPart != null) {
          PropertySheet propertySheet = (PropertySheet) viewPart;
          IPropertySheetPage page = (IPropertySheetPage) propertySheet.getCurrentPage();
          if (page != null) {
            page.selectionChanged(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(), selection);
          }
        }
      }
      updateStatusBar(false);
    });

    // Group columns programmatically
    groupColumns();

    // The below method is required to enable tootltip only for cells which contain not fully visible content
    attachToolTip();
    getSite().setSelectionProvider(selectionProvider);
  }

  /**
   * Mouse listener to the table viewer
   */
  private void addMouseListener() {
    this.natTable.addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(final MouseEvent mouseEvent) {
        // NA
      }

      @Override
      public void mouseDown(final MouseEvent mouseEvent) {
        // NA
      }

      @Override
      public void mouseDoubleClick(final MouseEvent mouseEvent) {
//        Left double click
        if (mouseEvent.button == 1) {
          leftMouseDoubleClickAction(mouseEvent);
        }
      }
    });
  }

  /**
   * @param mouseEvent
   */
  private void leftMouseDoubleClickAction(final MouseEvent mouseEvent) {
    ILayerCell cell = this.natTable.getCellByPosition(this.natTable.getColumnPositionByX(mouseEvent.x),
        this.natTable.getRowPositionByY(mouseEvent.y));
    if (cell != null) {// cell is null when clicking empty area in nattable
      LabelStack configLabels = cell.getConfigLabels();
      if (isConfigLblHasHyperLinkLbl(configLabels)) {
        int row = LayerUtil.convertRowPosition(this.natTable, this.natTable.getRowPositionByY(mouseEvent.y),
            ((CustomFilterGridLayer<DataAssessmentQuestionnaires>) this.natTable.getLayer()).getDummyDataLayer());
        Object rowObject = this.questionnaireResultsFilterGridLayer.getBodyDataProvider().getRowObject(row);
        if (rowObject instanceof DataAssessmentQuestionnaires) {
          final DataAssessmentQuestionnaires compareRowObject = (DataAssessmentQuestionnaires) rowObject;
          openQnaireRespByUrl(compareRowObject);
        }
      }
    }
  }

  /**
   * @param compareRowObject
   */
  private void openQnaireRespByUrl(final DataAssessmentQuestionnaires compareRowObject) {
    try {
      if (CommonUtils.isNotNull(compareRowObject.getQnaireBaselineLink())) {
        LinkProcessor linkOpener = new LinkProcessor(compareRowObject.getQnaireBaselineLink().getUrl().trim());
        if (linkOpener.openLink()) {
          PlatformUI.getWorkbench().getIntroManager()
              .closeIntro(PlatformUI.getWorkbench().getIntroManager().getIntro());
        }
      }
    }
    catch (ExternalLinkException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  private boolean isConfigLblHasHyperLinkLbl(final LabelStack configLabels) {
    boolean isConfigLblHasEditableLbl = true;
    return configLabels.hasLabel(ApicConstants.CONFIG_LABEL_HYPERLINK) ? isConfigLblHasEditableLbl
        : !isConfigLblHasEditableLbl;
  }

  private Map<Integer, Integer> createNatTableColumns() {
    this.propertyToLabelMap = new HashMap<>();
    return configureColumnsNATTable();
  }

  /**
   * Configure columns NAT table.
   *
   * @param columnWidthMap the column width map
   */
  private Map<Integer, Integer> configureColumnsNATTable() {
    this.propertyToLabelMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_WORKPACKAGE, "Work Package");
    this.propertyToLabelMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_RESP_NAME, "Responsible");
    this.propertyToLabelMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_RESP_TYEP, "Responsible Type");
    this.propertyToLabelMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_QUES_NAME, "Questionnaire");
    this.propertyToLabelMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_ASSESSMENT_PROD_READY, "Ready for Production");
    this.propertyToLabelMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_BASELINE_EXISTING, "Baseline Existing");
    this.propertyToLabelMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_POSITIVE, "Positive");
    this.propertyToLabelMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_NEGATIVE, "Negative");
    this.propertyToLabelMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_NEUTRAL, "Neutral");
    this.propertyToLabelMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_VERSION_NAME, "Version Name");
    this.propertyToLabelMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_REVIEWED_BY, "Reviewed By");
    this.propertyToLabelMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_REVIEWED_ON, "Reviewed On");
    this.propertyToLabelMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_LINK, "Link");

    this.columnWidthMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_WORKPACKAGE, 50);
    this.columnWidthMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_RESP_NAME, 50);
    this.columnWidthMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_RESP_TYEP, 30);
    this.columnWidthMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_QUES_NAME, 50);
    this.columnWidthMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_ASSESSMENT_PROD_READY, 15);
    this.columnWidthMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_BASELINE_EXISTING, 15);
    this.columnWidthMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_POSITIVE, 15);
    this.columnWidthMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_NEGATIVE, 15);
    this.columnWidthMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_NEUTRAL, 15);
    this.columnWidthMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_VERSION_NAME, 30);
    this.columnWidthMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_REVIEWED_BY, 50);
    this.columnWidthMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_REVIEWED_ON, 20);
    this.columnWidthMap.put(COLUMN_NUM_QUESTIONNAIRE_RESULTS_LINK, 40);

    return this.columnWidthMap;
  }

  /**
   * @param columnHeaderDataLayer
   * @return
   */
  private IConfiguration getCustomComparatorConfiguration(final DefaultColumnHeaderDataLayer columnHeaderDataLayer) {
    return new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Add label accumulator
        ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
        columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);

        for (int col_index = 0; col_index < (COLUMN_COUNT); col_index++) {
          labelAccumulator.registerColumnOverrides(col_index, CUSTOM_COMPARATOR_LABEL + col_index);
          configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getParamComparator(col_index),
              NORMAL, CUSTOM_COMPARATOR_LABEL + col_index);
        }


      }
    };

  }

  /**
   * get the comparator for the table
   *
   * @param cdrReportData
   * @param cdrReportData CdrReportData
   * @return Comparator<FocusMatrixAttribute>
   */
  private Comparator<DataAssessmentQuestionnaires> getParamComparator(final int columnNum) {
    return (compParam1, compParam2) -> {
      int ret = 0;

      switch (columnNum) {
        case CommonUIConstants.Q_WORK_PACKAGE:
          ret = ApicUtil.compare(compParam1.getA2lWpName(), compParam2.getA2lWpName());
          break;
        case CommonUIConstants.Q_RESPONSIBLE:
          ret = ApicUtil.compare(compParam1.getA2lRespName(), compParam2.getA2lRespName());
          break;
        case CommonUIConstants.Q_RESPONSIBLE_TYPE:
          ret = ApicUtil.compare(compParam1.getA2lRespType(), compParam2.getA2lRespType());
          break;
        case CommonUIConstants.Q_QUESTIONNAIRE:
          ret = ApicUtil.compare(compParam1.getQnaireRespName(), compParam2.getQnaireRespName());
          break;
        case CommonUIConstants.Q_READY_FOR_PRODUCTION:
          ret = ApicUtil.compareBoolean(compParam1.isQnaireReadyForProd(), compParam2.isQnaireReadyForProd());
          break;
        case CommonUIConstants.Q_BASELINE_EXISTING:
          ret = ApicUtil.compareBoolean(compParam1.isQnaireBaselineExisting(), compParam2.isQnaireBaselineExisting());
          break;
        case CommonUIConstants.Q_POSITIVE:
          ret = ApicUtil.compareInt(compParam1.getQnairePositiveAnsCount(), compParam2.getQnairePositiveAnsCount());
          break;
        case CommonUIConstants.Q_NEGATIVE:
          ret = ApicUtil.compareInt(compParam1.getQnaireNegativeAnsCount(), compParam2.getQnaireNegativeAnsCount());
          break;
        case CommonUIConstants.Q_NEUTRAL:
          ret = ApicUtil.compareInt(compParam1.getQnaireNeutralAnsCount(), compParam2.getQnaireNeutralAnsCount());
          break;
        case CommonUIConstants.Q_VERSION_NAME:
          ret = ApicUtil.compare(compParam1.getQnaireRespVersName(), compParam2.getQnaireRespVersName());
          break;
        case CommonUIConstants.Q_REVIEWED_BY:
          ret = ApicUtil.compare(compParam1.getQnaireReviewedUser(), compParam2.getQnaireReviewedUser());
          break;
        case CommonUIConstants.Q_REVIEWED_ON:
          ret = ApicUtil.compare(compParam1.getQnaireReviewedDate(), compParam2.getQnaireReviewedDate());
          break;
        case CommonUIConstants.Q_RESULTS_LINK:
          ret = ApicUtil.compare(compParam1.getQnaireBaselineLink().getDisplayText(),
              compParam2.getQnaireBaselineLink().getDisplayText());
          break;
        default:
          break;
      }
      return ret;
    };
  }

  /**
   * Gets the name comparator.
   *
   * @return the name comparator
   */
  public static Comparator<DataAssessmentQuestionnaires> getNameComparator() {
    return (final DataAssessmentQuestionnaires val1, final DataAssessmentQuestionnaires val2) -> ModelUtil
        .compare(val1.getA2lWpName(), val2.getA2lWpName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart arg0, final ISelection arg1) {
    // unimplemented method
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.toolbarManager;
  }

  /**
   * Gets the grid data.
   *
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
   * Load saved state of NAT table.
   */
  private void loadState() {
    try {
      this.natTable.loadState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to load data assessment questionnaire results nat table state", ioe,
          Activator.PLUGIN_ID);
    }
  }

  /**
   * Save current state for the NAT table.
   */
  private void saveState() {
    try {
      this.natTable.saveState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to save data assessment questionnaire results nat table state", ioe,
          Activator.PLUGIN_ID);
    }
  }

  /**
   * This method returns filter text GridData object.
   *
   * @return GridData
   */
  private GridData getFilterTxtGridData() {
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }

  /**
   * Method to group nattable columns
   */
  private void groupColumns() {
    int[] baselineColumns = new int[] {
        COLUMN_NUM_QUESTIONNAIRE_RESULTS_BASELINE_EXISTING,
        COLUMN_NUM_QUESTIONNAIRE_RESULTS_POSITIVE,
        COLUMN_NUM_QUESTIONNAIRE_RESULTS_NEGATIVE,
        COLUMN_NUM_QUESTIONNAIRE_RESULTS_NEUTRAL,
        COLUMN_NUM_QUESTIONNAIRE_RESULTS_VERSION_NAME,
        COLUMN_NUM_QUESTIONNAIRE_RESULTS_REVIEWED_BY,
        COLUMN_NUM_QUESTIONNAIRE_RESULTS_REVIEWED_ON,
        COLUMN_NUM_QUESTIONNAIRE_RESULTS_LINK };
    ColumnGroupModel columnGroupModel = this.questionnaireResultsFilterGridLayer.getColumnGroupModel();
    columnGroupModel.addColumnsIndexesToGroup("Baseline", baselineColumns);
    columnGroupModel.setColumnGroupCollapseable(COLUMN_NUM_QUESTIONNAIRE_RESULTS_BASELINE_EXISTING, false);
  }

  /**
   * Enables tootltip only for cells which contain not fully visible content.
   */
  private void attachToolTip() {
    DefaultToolTip toolTip = new DataAssessmentQnaireResultsNatToolTip(this.natTable, new String[0], this);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));
  }


  /**
   * @return the toolBarFilterStateMap
   */
  public Map<String, Boolean> getToolBarFilterStateMap() {
    return this.toolBarFilterStateMap;
  }


  /**
   * @return the natTable
   */
  public CustomNATTable getNatTable() {
    return this.natTable;
  }


  /**
   * @param natTable the natTable to set
   */
  public void setNatTable(final CustomNATTable natTable) {
    this.natTable = natTable;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMsgAndStatHdr();
  }

  /**
   * input for status line
   */
  public void setStatusBarMsgAndStatHdr() {

    int totalItemCount = this.totTableRowCount;
    int filteredItemCount = this.questionnaireResultsFilterGridLayer.getRowHeaderLayer().getPreferredRowCount();
    final StringBuilder buf = new StringBuilder(40);
    buf.append("Displaying : ").append(filteredItemCount).append(" out of ").append(totalItemCount).append(" records ");
    IStatusLineManager statusLine;

    // get the status line manager from the editor
    statusLine = this.editor.getEditorSite().getActionBars().getStatusLineManager();
    if (totalItemCount == filteredItemCount) {
      statusLine.setErrorMessage(null);
      statusLine.setMessage(buf.toString());
    }
    else {
      // show the message in red if the count is not equal
      statusLine.setErrorMessage(buf.toString());
    }
    statusLine.update(true);

  }


  /**
   * @return the questionnaireResultsFilterGridLayer
   */
  public CustomFilterGridLayer getQuestionnaireResultsFilterGridLayer() {
    return this.questionnaireResultsFilterGridLayer;
  }

}
