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
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.widgets.nattable.columnChooser.command.DisplayColumnChooserCommandHandler;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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

import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.DataAssessmentWPPageToolbarActionSet;
import com.bosch.caltool.cdr.ui.editors.DataAssessmentReportEditor;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.DataAssessmentWorkpackageEditConfiguration;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.DataAssessmentWorkpackageNattableLabelAccumulator;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.DataAssessmentWorkpackageToolTip;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.DataAssessmentWprkpackageColumnFilterMatcher;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.FilterRowCustomConfiguration;
import com.bosch.caltool.cdr.ui.table.filters.DataAssessmentWPToolBarFilters;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.editors.AbstractNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.DaWpResp;
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
public class DataAssessmentWorkPackagesPage extends AbstractNatFormPage implements ISelectionListener {

  /**
   * Editor Instance
   */
  private final DataAssessmentReportEditor editor;
  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;

  /**
   * Work package state section
   */
  private Section wpStateSection;
  /**
   * Work package state form
   */
  private Form wpStateForm;
  /**
   * Work package state filter
   */
  private Text filterTxt;
  /**
   * Work package state nattable label
   */
  private Map<Integer, String> propertyToLabelMap;
  /**
   * Work package state nattable column width
   */
  Map<Integer, Integer> columnWidthMap = new HashMap<>();
  /**
   * Work package filter gridLayer
   */
  private CustomFilterGridLayer dataAssessmentWPFilterGridLayer;
  /**
   * Work package state nattable
   */
  private CustomNATTable natTable;
  /**
   * Work package state all column filter matcher
   */
  private DataAssessmentWprkpackageColumnFilterMatcher<DaWpResp> allColumnFilterMatcher;

  /**
   * ToolBarManager instance
   */
  private ToolBarManager toolbarManager;
  /**
   * DataAssessmentWPToolBarFilters instance
   */
  private DataAssessmentWPToolBarFilters toolBarFilters;
  /**
   * Map to hold the filter action text and its changed state (checked - true or false)
   */
  private final Map<String, Boolean> toolBarFilterStateMap = new ConcurrentHashMap<>();

  /**
   * row count
   */
  private int totTableRowCount;


  public static final int COLUMN_NUM_WORK_PACKAGE = 0;

  public static final int COLUMN_NUM_RESPONSIBLE = 1;

  public static final int COLUMN_NUM_RESPONSIBLE_TYPE = 2;

  public static final int COLUMN_NUM_OVERALL_WP = 3;

  public static final int COLUMN_NUM_WP_FINISHED = 4;

  public static final int COLUMN_NUM_QNAIRE_ANSWERED_BASELINED = 5;

  public static final int COLUMN_NUM_PARAMETER_REVIEWED = 6;

  public static final int COLUMN_NUM_HEX_FILE_EQUAL_TO_REVIEWS = 7;

  /**
   * column count
   */
  private static final int COLUMN_COUNT = 8;

  /**
   * custom comparator label
   */
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  /**
   * Data assessment work packages page, Constructor.
   *
   * @param editor editor
   */
  public DataAssessmentWorkPackagesPage(final FormEditor editor) {
    super(editor, "dataAssessmentWorkPackages", "Work Packages");
    this.editor = (DataAssessmentReportEditor) editor;
  }

  @Override
  public void createPartControl(final Composite parent) {
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    this.nonScrollableForm.setText("Work Packages");

    ManagedForm mform = new ManagedForm(parent);
    FormToolkit formToolkit = mform.getToolkit();
    createWPStateSection(formToolkit);
  }

  private void createWPStateSection(final FormToolkit formToolkit) {
    Composite composite = this.nonScrollableForm.getBody();
    composite.setLayout(new GridLayout());

    this.wpStateSection = formToolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.wpStateSection.setExpanded(true);
    this.wpStateSection.setText("Work Package State");
    this.wpStateSection
        .setDescription("Overview of all work package and responsibility combinations in this A2L and variant");
    this.wpStateSection.getDescriptionControl().setEnabled(false);
    this.wpStateSection.setLayoutData(GridDataUtil.getInstance().getGridData());

    createWPStateForm(formToolkit);
//    Adding toolbar in the form level
    createWPStateToolbar();
    this.wpStateSection.setClient(this.wpStateForm);
  }

  private void createWPStateForm(final FormToolkit toolkit) {
    this.wpStateForm = toolkit.createForm(this.wpStateSection);
    this.wpStateForm.getBody().setLayout(new GridLayout());


    createFilterTxt(toolkit);
    createTable();
  }

  private void createTable() {

    Set<DaWpResp> workpackageDetails = new HashSet<>(
        this.editor.getEditorInput().getDataAssmntReportDataHandler().getDataAssessmentReport().getDataAssmntWps());

    this.totTableRowCount = workpackageDetails.size();
    AbstractNatInputToColumnConverter natInputToColumnConverter = new DataAssessmentWPNatInputToColumnConverter();

    IConfigRegistry configRegistry = new ConfigRegistry();

    createNatTableColumns();
    this.dataAssessmentWPFilterGridLayer = new CustomFilterGridLayer(configRegistry, workpackageDetails,
        this.columnWidthMap, new CustomDaWorkPackagePropertyAccessor<>(), new DaWorkPackageHdrDataProvider(),
        getNameComparator(), natInputToColumnConverter, this, null, true, true, false);

    this.allColumnFilterMatcher = new DataAssessmentWprkpackageColumnFilterMatcher<>();
    this.dataAssessmentWPFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    this.natTable = new CustomNATTable(this.wpStateForm.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL,
        this.dataAssessmentWPFilterGridLayer, false, this.getClass().getSimpleName(), this.propertyToLabelMap);

    this.toolBarFilters = new DataAssessmentWPToolBarFilters();
    this.dataAssessmentWPFilterGridLayer.getFilterStrategy()
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
        getCustomComparatorConfiguration(this.dataAssessmentWPFilterGridLayer.getColumnHeaderDataLayer()));
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

    this.dataAssessmentWPFilterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));

    // Registry Configuration class for data assessment editor which assigns style based on the config labels registered
    // on the cells
    this.natTable.addConfiguration(new DataAssessmentWorkpackageEditConfiguration());

    // Used for registration/addition of labels for a given column.
    DataLayer bodyDataLayer = this.dataAssessmentWPFilterGridLayer.getDummyDataLayer();
    final DataAssessmentWorkpackageNattableLabelAccumulator workpackageLabelAccumulator =
        new DataAssessmentWorkpackageNattableLabelAccumulator(bodyDataLayer);
    bodyDataLayer.setConfigLabelAccumulator(workpackageLabelAccumulator);

    CustomDefaultBodyLayerStack bodyLayer = this.dataAssessmentWPFilterGridLayer.getBodyLayer();
    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.dataAssessmentWPFilterGridLayer.getColumnHeaderLayer(),
            this.dataAssessmentWPFilterGridLayer.getColumnHeaderDataLayer(), null, null);
    this.natTable.registerCommandHandler(columnChooserCommandHandler);
    this.natTable.configure();

    attachToolTip();

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

    this.dataAssessmentWPFilterGridLayer
        .registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.natTable));

    RowSelectionProvider<DaWpResp> selectionProvider =
        new RowSelectionProvider<>(this.dataAssessmentWPFilterGridLayer.getBodyLayer().getSelectionLayer(),
            this.dataAssessmentWPFilterGridLayer.getBodyDataProvider(), false);

    selectionProvider.addSelectionChangedListener(event -> {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      if (selection.getFirstElement() instanceof DaWpResp) {
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


    getSite().setSelectionProvider(selectionProvider);

    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.natTable.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new CustomDragListener(selectionProvider,
        this.dataAssessmentWPFilterGridLayer.getBodyLayer().getSelectionLayer(), this.natTable));

  }

  /**
  *
  */
  private void attachToolTip() {
    DefaultToolTip toolTip = new DataAssessmentWorkpackageToolTip(this.natTable, new String[0], this);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));
  }

  /**
   * column property accessor
   *
   * @param <T>
   */
  class CustomDaWorkPackagePropertyAccessor<T> implements IColumnAccessor<T> {

    /**
     * This method has been overridden so that it returns the passed row object. The above behavior is required for use
     * of custom comparators for sorting which requires the Row object to be passed without converting to a particular
     * column String value {@inheritDoc}
     */
    @Override
    public Object getDataValue(final T type, final int columnIndex) {
      return type;
    }


    @Override
    public void setDataValue(final T sysConstNatModel, final int columnIndex, final Object newValue) {
      // implementation not required currently
    }

    @Override
    public int getColumnCount() {
      return DataAssessmentWorkPackagesPage.this.propertyToLabelMap.size();
    }
  }

  /**
   * Column header data provider class
   */
  class DaWorkPackageHdrDataProvider implements IDataProvider {

    /**
     * @param columnIndex int
     * @return String column header label
     */
    public String getColumnHeaderLabel(final int columnIndex) {
      String string = DataAssessmentWorkPackagesPage.this.propertyToLabelMap.get(columnIndex);

      return string == null ? "" : string;
    }

    @Override
    public int getColumnCount() {
      return DataAssessmentWorkPackagesPage.this.propertyToLabelMap.size();
    }

    @Override
    public int getRowCount() {
      return 1;
    }

    /**
     * This class does not support multiple rows in the column header layer.
     */
    @Override
    public Object getDataValue(final int columnIndex, final int rowIndex) {
      return getColumnHeaderLabel(columnIndex);
    }

    @Override
    public void setDataValue(final int columnIndex, final int rowIndex, final Object newValue) {
      throw new UnsupportedOperationException();
    }

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
  private Comparator<DaWpResp> getParamComparator(final int columnNum) {
    return (compParam1, compParam2) -> {
      int ret = 0;

      switch (columnNum) {
        case CommonUIConstants.WORK_PACKAGE:
          ret = ApicUtil.compare(compParam1.getA2lWpName(), compParam2.getA2lWpName());
          break;
        case CommonUIConstants.RESPONSIBLE:
          ret = ApicUtil.compare(compParam1.getA2lRespName(), compParam2.getA2lRespName());
          break;
        case CommonUIConstants.RESPONSIBLE_TYPE:
          ret = ApicUtil.compare(compParam1.getA2lRespType(), compParam2.getA2lRespType());
          break;
        case CommonUIConstants.OVERALL_WORK_PACKAGE:
          ret = ApicUtil.compare(compParam1.getWpReadyForProductionFlag(), compParam2.getWpReadyForProductionFlag());
          break;
        case CommonUIConstants.WORK_PACKAGE_FINISHED:
          ret = ApicUtil.compare(compParam1.getWpFinishedFlag(), compParam2.getWpFinishedFlag());
          break;
        case CommonUIConstants.QUESTIONNAIRE_ANSWERED_AND_BASELINED:
          ret = ApicUtil.compare(compParam1.getQnairesAnsweredFlag(), compParam2.getQnairesAnsweredFlag());
          break;
        case CommonUIConstants.PARAMETER_OF_RB_RESPONSIBILITY_REVIEWED:
          ret = ApicUtil.compare(compParam1.getParameterReviewedFlag(), compParam2.getParameterReviewedFlag());
          break;
        case CommonUIConstants.HEX_FILE_EQUAL_TO_REVIEWED_DATA:
          ret = ApicUtil.compare(compParam1.getHexRvwEqualFlag(), compParam2.getHexRvwEqualFlag());
          break;
        default:
          break;
      }
      return ret;
    };
  }

  private void createWPStateToolbar() {
    final Separator separator = new Separator();
    this.toolbarManager = (ToolBarManager) this.wpStateForm.getToolBarManager();
    DataAssessmentWPPageToolbarActionSet toolBarActionSet =
        new DataAssessmentWPPageToolbarActionSet(this.dataAssessmentWPFilterGridLayer, this);

    toolBarActionSet.overallWPProdReadyFilterAction(this.toolbarManager, this.toolBarFilters);
    toolBarActionSet.overallWPNotProdReadyFilterAction(this.toolbarManager, this.toolBarFilters);
    this.toolbarManager.add(separator);

    toolBarActionSet.wpFinishedFilterAction(this.toolbarManager, this.toolBarFilters);
    toolBarActionSet.wpNotFinishedFilterAction(this.toolbarManager, this.toolBarFilters);
    this.toolbarManager.add(separator);

    toolBarActionSet.qnaireAnsweredAndBaselinedFilterAction(this.toolbarManager, this.toolBarFilters);
    toolBarActionSet.qnaireNotAnsweredAndBaselinedFilterAction(this.toolbarManager, this.toolBarFilters);
    this.toolbarManager.add(separator);

    toolBarActionSet.parameterReviewedFilterAction(this.toolbarManager, this.toolBarFilters);
    toolBarActionSet.parameterNotReviewedFilterAction(this.toolbarManager, this.toolBarFilters);
    this.toolbarManager.add(separator);

    toolBarActionSet.hexDataReviewsEqualFilterAction(this.toolbarManager, this.toolBarFilters);
    toolBarActionSet.hexDataReviewsNotEqualFilterAction(this.toolbarManager, this.toolBarFilters);
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
    getRefreshComponentSet().add(this.dataAssessmentWPFilterGridLayer);
    addResetFiltersAction();
  }


  private Map<Integer, Integer> createNatTableColumns() {
    this.propertyToLabelMap = new HashMap<>();
    return configureColumnsNATTable();
  }

  /**
   * Configure columns NAT table.
   *
   * @param propertyToLabelMap2 the property to label map 2
   * @param columnWidthMap the column width map
   */
  private Map<Integer, Integer> configureColumnsNATTable() {
    this.propertyToLabelMap.put(COLUMN_NUM_WORK_PACKAGE, "Work Package");
    this.propertyToLabelMap.put(COLUMN_NUM_RESPONSIBLE, "Responsible");
    this.propertyToLabelMap.put(COLUMN_NUM_RESPONSIBLE_TYPE, "Responsible Type");
    this.propertyToLabelMap.put(COLUMN_NUM_OVERALL_WP, IMessageConstants.OVERALL_WP_READY_FOR_PRODUCTION);
    this.propertyToLabelMap.put(COLUMN_NUM_WP_FINISHED, IMessageConstants.WP_FINISHED);
    this.propertyToLabelMap.put(COLUMN_NUM_QNAIRE_ANSWERED_BASELINED, IMessageConstants.QNAIRE_ANSWERED_AND_BASELINED);
    this.propertyToLabelMap.put(COLUMN_NUM_PARAMETER_REVIEWED, IMessageConstants.PARAMETER_REVIEWED);
    this.propertyToLabelMap.put(COLUMN_NUM_HEX_FILE_EQUAL_TO_REVIEWS, IMessageConstants.HEX_DATA_REVIEW_EQUAL);

    this.columnWidthMap.put(COLUMN_NUM_WORK_PACKAGE, 90);
    this.columnWidthMap.put(COLUMN_NUM_RESPONSIBLE, 70);
    this.columnWidthMap.put(COLUMN_NUM_RESPONSIBLE_TYPE, 100);
    this.columnWidthMap.put(COLUMN_NUM_OVERALL_WP, 140);
    this.columnWidthMap.put(COLUMN_NUM_WP_FINISHED, 130);
    this.columnWidthMap.put(COLUMN_NUM_QNAIRE_ANSWERED_BASELINED, 140);
    this.columnWidthMap.put(COLUMN_NUM_PARAMETER_REVIEWED, 150);
    this.columnWidthMap.put(COLUMN_NUM_HEX_FILE_EQUAL_TO_REVIEWS, 130);

    return this.columnWidthMap;
  }

  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.toolbarManager;
  }

  /**
   * Gets the name comparator.
   *
   * @return the name comparator
   */
  public static Comparator<DaWpResp> getNameComparator() {
    return (final DaWpResp val1, final DaWpResp val2) -> ModelUtil.compare(val1.getA2lWpName(), val2.getA2lWpName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart arg0, final ISelection arg1) {
    // unimplemented method
  }

  /**
   * This method creates filter text.
   */
  private void createFilterTxt(final FormToolkit toolkit) {
    GridData gridData = getFilterTxtGridData();
    this.filterTxt = TextUtil.getInstance().createFilterText(toolkit, this.wpStateForm.getBody(),
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      String text = DataAssessmentWorkPackagesPage.this.filterTxt.getText().trim();
      DataAssessmentWorkPackagesPage.this.allColumnFilterMatcher.setFilterText(text, true);
      DataAssessmentWorkPackagesPage.this.dataAssessmentWPFilterGridLayer.getFilterStrategy()
          .applyFilterInAllColumns(text);
      DataAssessmentWorkPackagesPage.this.dataAssessmentWPFilterGridLayer.getSortableColumnHeaderLayer()
          .fireLayerEvent(new FilterAppliedEvent(
              DataAssessmentWorkPackagesPage.this.dataAssessmentWPFilterGridLayer.getSortableColumnHeaderLayer()));
    });
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
      CDMLogger.getInstance().warn("Failed to load Data assessment work package nat table state", ioe,
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
      CDMLogger.getInstance().warn("Failed to save Data assessment work package nat table state", ioe,
          Activator.PLUGIN_ID);
    }

  }

  /**
   * @return the toolBarFilterStateMap
   */
  public Map<String, Boolean> getToolBarFilterStateMap() {
    return this.toolBarFilterStateMap;
  }

  /**
   * @return the nattable
   */
  public CustomNATTable getNatTable() {
    return this.natTable;
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
    int filteredItemCount = this.dataAssessmentWPFilterGridLayer.getRowHeaderLayer().getPreferredRowCount();
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
   * @return the dataAssessmentWPFilterGridLayer
   */
  public CustomFilterGridLayer getDataAssessmentWPFilterGridLayer() {
    return this.dataAssessmentWPFilterGridLayer;
  }
}
