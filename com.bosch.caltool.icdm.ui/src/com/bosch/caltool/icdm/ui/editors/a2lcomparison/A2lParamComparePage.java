/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.a2lcomparison;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommand;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.config.NullComparator;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.freeze.command.FreezeSelectionCommand;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.editors.compare.CompareRowObject;
import com.bosch.caltool.apic.ui.editors.compare.PIDCCompareNatTableCheckBoxCellEditor;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.a2l.A2LCompareHandler;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.client.bo.a2l.A2lParamCompareRowObject;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.common.ui.editors.AbstractNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.caltool.icdm.ui.dialogs.CompareA2lDialog;
import com.bosch.caltool.icdm.ui.util.IUIConstants;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author bru2cob
 */
public class A2lParamComparePage extends AbstractNatFormPage implements ISelectionListener {

  private final A2LCompareEditor a2lParamCompareEditor;
  private A2LCompareHandler paramCompareHandler;
  /**
   * Composite instance for base layout
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
   * Filter text instance
   */
  private Text filterTxt;
  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;

  private CustomFilterGridLayer a2lCompFilterGridLayer;
  private CustomNATTable natTable;
  private Map<Integer, String> propertyToLabelMap;
  private final Map<Integer, Integer> columnWidthMap = new HashMap<>();

  private A2lParamCmpColumnFilterMatcher<A2lParamCompareRowObject> allColumnFilterMatcher;
  private int totTableRowCount;
  private static final int CHECKBOX_COLUMN_WIDTH = 40;
  private static final int BALL_COLUMN_WIDTH = 60;

  private static final int PARAMETER_COLUMN_WIDTH = 150;
  private final SortedSet<A2lParamCompareRowObject> compareRowObjects = new TreeSet<>();
  private static final String CHECK_BOX_CONFIG_LABEL = "checkBox";
  private static final String CHECK_BOX_EDITOR_CNG_LBL = "checkBoxEditor";
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  /** The tool bar manager. */
  private ToolBarManager toolBarManager;
  /** The tool bar filters. */
  private A2lCompareToolBarFilters toolBarFilters;
  /**
   * Map of UI column index as key and A2lWpDefnVersion as value to be used in grouped col header name to get a2l wp def
   * versn name.
   */
  private final Map<Integer, A2lWpDefnVersion> colA2lWpDefMap = new HashMap<>();
  /**
   * Map of UI column index as key and A2lVariantGroup as value to be used in grouped col header name to get a2l variant
   * grp name.
   */
  private final Map<Integer, A2lVariantGroup> colVarGrpMap = new HashMap<>();

  /**
   * @param a2lParamCompareEditor
   * @param paramCompareHandler
   */
  public A2lParamComparePage(final A2LCompareEditor a2lParamCompareEditor,
      final A2LCompareHandler paramCompareHandler) {
    super(a2lParamCompareEditor, "A2L.Compare", "Parameters");
    this.a2lParamCompareEditor = a2lParamCompareEditor;
    this.paramCompareHandler = paramCompareHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = this.a2lParamCompareEditor.getToolkit().createForm(parent);

    this.nonScrollableForm.setText("A2L Parameters Compare");

    // instead of editor.getToolkit().createScrolledForm(parent); in superclass
    // formToolkit is obtained from managed form to create form within section
    ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
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
    FormToolkit formToolkit = managedForm.getToolkit();
    createComposite(formToolkit);

    // add listeners
    getSite().getPage().addPostSelectionListener(this);
  }

  /**
   * This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {
    this.composite = this.nonScrollableForm.getBody();
    this.composite.setLayout(new GridLayout());
    createSection(toolkit);

    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes section
   */
  private void createSection(final FormToolkit toolkit) {
    this.section = SectionUtil.getInstance().createSection(this.composite, toolkit, "Parameters");
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(false);

    createForm(toolkit);

    // Code Change made for fixing the number of attributes coming as zero
    this.section.setClient(this.form);
  }

  /**
   * This method initializes scrolledForm
   */
  private void createForm(final FormToolkit toolkit) {
    this.form = toolkit.createForm(this.section);

    this.filterTxt = TextUtil.getInstance().createFilterText(toolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.form.getBody().setLayout(new GridLayout());
    addModifyListenerForFilterTxt();
    createNatTable();

    this.totTableRowCount = this.a2lCompFilterGridLayer.getRowHeaderLayer().getPreferredRowCount();
  }

  /**
   *
   */
  private void createNatTable() {

    this.propertyToLabelMap = new LinkedHashMap<>();
    this.propertyToLabelMap.put(IUIConstants.A2LCMP_ICON_COL_INDEX, "");
    this.propertyToLabelMap.put(IUIConstants.A2LCMP_PARAM_COL_INDEX, "Parameter");
    this.propertyToLabelMap.put(IUIConstants.A2LCMP_DIFF_COL_INDEX, "Diff");

    // The below map is used by NatTable to Map Columns with their respective widths
    // Width is based on pixels

    this.columnWidthMap.put(IUIConstants.A2LCMP_ICON_COL_INDEX, BALL_COLUMN_WIDTH);
    this.columnWidthMap.put(IUIConstants.A2LCMP_PARAM_COL_INDEX, PARAMETER_COLUMN_WIDTH);
    this.columnWidthMap.put(IUIConstants.A2LCMP_DIFF_COL_INDEX, CHECKBOX_COLUMN_WIDTH);


    // fill attributes information for table
    fillNattableRowObjects();

    A2lParamCmpInputToColConverter a2lInputConverter = new A2lParamCmpInputToColConverter();
    IConfigRegistry configRegistry = new ConfigRegistry();

    // A Custom Filter Grid Layer is constructed
    this.a2lCompFilterGridLayer =
        new CustomFilterGridLayer<A2lParamCompareRowObject>(configRegistry, this.compareRowObjects, this.columnWidthMap,
            new CustomA2lParamCompareColumnPropertyAccessor<>(this.propertyToLabelMap),
            new CustomA2lParamCmpHeaderDataProvider(this.propertyToLabelMap), getA2lParamCompareComparator(0),
            a2lInputConverter, this, null, true, true, true);

    this.allColumnFilterMatcher = new A2lParamCmpColumnFilterMatcher<>();
    this.a2lCompFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    this.natTable = new CustomNATTable(
        this.form.getBody(), SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED |
            SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        this.a2lCompFilterGridLayer, false, getClass().getSimpleName());
    this.toolBarFilters = new A2lCompareToolBarFilters();
    this.a2lCompFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());
    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.natTable.addConfiguration(new CustomNatTableStyleConfiguration());
    this.natTable.addConfiguration(new FilterRowCustomConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        super.configureRegistry(configRegistry);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });
    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable
        .addConfiguration(getCustomComparatorConfiguration(this.a2lCompFilterGridLayer.getColumnHeaderDataLayer()));
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(
                super.createColumnHeaderMenu(A2lParamComparePage.this.natTable).withColumnChooserMenuItem()
                    .withMenuItemProvider((natTable1, popupMenu) -> resetMenuItem(popupMenu)).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });

    // Group columns programmatically
    groupColumns();
    this.natTable.addConfiguration(new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {

        ((ColumnOverrideLabelAccumulator) A2lParamComparePage.this.a2lCompFilterGridLayer.getDummyDataLayer()
            .getConfigLabelAccumulator()).registerColumnOverrides(IUIConstants.A2LCMP_DIFF_COL_INDEX,
                CHECK_BOX_EDITOR_CNG_LBL + "_" + IUIConstants.A2LCMP_DIFF_COL_INDEX,
                CHECK_BOX_CONFIG_LABEL + "_" + IUIConstants.A2LCMP_DIFF_COL_INDEX);
        registerCheckBoxEditor(configRegistry);

      }
    });

    this.natTable.addMouseListener(new A2lCompareMouseEventListener(this.natTable, this.a2lCompFilterGridLayer));
    this.natTable.addConfiguration(new A2lParamCompareEditConfiguration());

    DataLayer bodyDataLayer = this.a2lCompFilterGridLayer.getDummyDataLayer();
    IRowDataProvider<A2lParamCompareRowObject> bodyDataProvider =
        (IRowDataProvider<A2lParamCompareRowObject>) bodyDataLayer.getDataProvider();
    final A2lParamCompareLabelAccumulator a2lParamCmpLabelAccumulator =
        new A2lParamCompareLabelAccumulator(bodyDataLayer, bodyDataProvider);
    bodyDataLayer.setConfigLabelAccumulator(a2lParamCmpLabelAccumulator);
    createToolBarAction();
    this.natTable.configure();
    // get the reference to the SelectionLayer
    SelectionLayer selectionLayer = this.a2lCompFilterGridLayer.getBodyLayer().getSelectionLayer();
    // select cell with column position 3 and row position 0
    selectionLayer.setSelectedCell(3, 0);
    // freeze the first two columns
    this.natTable.doCommand(new FreezeSelectionCommand());
    // reset the selection to first row first column
    selectionLayer.setSelectedCell(0, 0);
    this.a2lCompFilterGridLayer.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.natTable));
    RowSelectionProvider<CompareRowObject> selectionProvider =
        new RowSelectionProvider<>(this.a2lCompFilterGridLayer.getBodyLayer().getSelectionLayer(),
            this.a2lCompFilterGridLayer.getBodyDataProvider(), false);

    // The below method is required to enable tootltip only for cells which contain not fully visible content
    attachToolTip();
    setDropSupport();
    getSite().setSelectionProvider(selectionProvider);
  }

  /**
   *
   */
  private void createToolBarAction() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = this.toolBarManager.createControl(this.section);
    final Separator separator = new Separator();
    addHelpAction(this.toolBarManager);
    A2lCompareToolBarActionSet toolBarActionSet = new A2lCompareToolBarActionSet(this);
    toolBarActionSet.addParamDiffFilterAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.addParamNotDiffFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);
    toolBarActionSet.addComplianceFilterAction(this.toolBarFilters, this.toolBarManager);
    toolBarActionSet.addNonComplianceFilterAction(this.toolBarFilters, this.toolBarManager);
    this.toolBarManager.add(separator);
    // Filter For the compliance parameters
    toolBarActionSet.addBlackListFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter For the non compliance parameters
    toolBarActionSet.addNonBlackListFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    toolBarActionSet.addReadOnlyAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.addNotReadOnlyAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);
    toolBarActionSet.addQSSDFilterAction(this.toolBarFilters, this.toolBarManager);
    toolBarActionSet.addNonQSSDFilterAction(this.toolBarFilters, this.toolBarManager);
    this.toolBarManager.add(separator);
    addResetAllFiltersAction();

    this.toolBarManager.update(true);
    this.form.setToolBarVerticalAlignment(SWT.TOP);
    this.section.setTextClient(toolbar);

  }

  /**
   * Add reset filter button
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.a2lCompFilterGridLayer);
    addResetFiltersAction();
  }

  /**
   * @param configRegistry
   */
  private void registerCheckBoxEditor(final IConfigRegistry configRegistry) {
    Style cellStyleAny = new Style();
    cellStyleAny.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    cellStyleAny.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_LIGHT_SHADOW);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleAny, NORMAL,
        CHECK_BOX_CONFIG_LABEL + "_" + IUIConstants.A2LCMP_DIFF_COL_INDEX);

    configRegistry.registerConfigAttribute(CELL_PAINTER, new CheckBoxPainter(), NORMAL,
        CHECK_BOX_CONFIG_LABEL + "_" + IUIConstants.A2LCMP_DIFF_COL_INDEX);
    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultBooleanDisplayConverter(),
        NORMAL, CHECK_BOX_CONFIG_LABEL + "_" + IUIConstants.A2LCMP_DIFF_COL_INDEX);
    PIDCCompareNatTableCheckBoxCellEditor checkBoxCellEditorDiff = new PIDCCompareNatTableCheckBoxCellEditor();
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, checkBoxCellEditorDiff, NORMAL,
        CHECK_BOX_EDITOR_CNG_LBL + "_" + IUIConstants.A2LCMP_DIFF_COL_INDEX);

  }

  /**
   *
   */
  private void setDropSupport() {
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.natTable.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new DropTargetAdapter() {

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("rawtypes")
      @Override
      public void drop(final DropTargetEvent event) {
        if (event.data == null) {
          event.detail = DND.DROP_NONE;
          return;
        }
        StructuredSelection structuredSelection = (StructuredSelection) event.data;
        Object selElement = structuredSelection.getFirstElement();
        if (CommonUtils.isNotNull(selElement)) {
          List<PidcA2l> selPidcA2lList = getSelPidcA2lList(structuredSelection);
          if (CommonUtils.isNotEmpty(selPidcA2lList)) {
            CompareA2lDialog compA2lDialog =
                new CompareA2lDialog(selPidcA2lList, null, Display.getCurrent().getActiveShell());
            compA2lDialog.setA2lParamComparePage(A2lParamComparePage.this);
            compA2lDialog.open();
          }
        }
      }
    });
  }


  /**
   * @param structuredSelection
   * @return
   */
  private List<PidcA2l> getSelPidcA2lList(final StructuredSelection structuredSelection) {
    List<PidcA2l> selPidcA2lList = new ArrayList<>();
    for (Object sel : structuredSelection.toList()) {
      if ((sel instanceof PidcTreeNode) && (((PidcTreeNode) sel).getPidcA2l() != null)) {
        selPidcA2lList.add(((PidcTreeNode) sel).getPidcA2l());
      }
    }
    return selPidcA2lList;
  }

  /**
   * @param natTable2
   */
  private void attachToolTip() {
    DefaultToolTip toolTip = new A2lCompareNatTableToolTip(this.natTable, this.a2lCompFilterGridLayer);
    toolTip.setPopupDelay(0);
    toolTip.activate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.paramCompareHandler;
  }

  /**
  *
  */
  private void refreshTable() {
    this.a2lCompFilterGridLayer.getEventList().clear();
    fillNattableRowObjects();
    this.a2lCompFilterGridLayer.getEventList().addAll(this.compareRowObjects);
    if (this.natTable != null) {
      this.natTable.doCommand(new StructuralRefreshCommand());
      this.natTable.doCommand(new VisualRefreshCommand());
      this.natTable.refresh();
    }
    setStatusBarMessage(false);
  }

  /**
   *
   */
  private void groupColumns() {
    int columnGroupCounter = 0;
    int[] groupGroupSelectedColumns = new int[6];
    List<Integer> groupGroupSelectedColumnsList = new ArrayList<>(6);
    ColumnGroupModel columnGroupModel = this.a2lCompFilterGridLayer.getColumnGroupModel();
    for (int colIndex = 3; colIndex < this.propertyToLabelMap.size(); colIndex++) {
      groupGroupSelectedColumns[columnGroupCounter] = colIndex;
      groupGroupSelectedColumnsList.add(colIndex);
      ++columnGroupCounter;
      String headerNameStr = null;
      if (columnGroupCounter == 6) {
        headerNameStr = getHeaderName(this.colA2lWpDefMap.get(colIndex).getId(), this.colVarGrpMap.get(colIndex));
        // Add group
        columnGroupModel.addColumnsIndexesToGroup(headerNameStr, groupGroupSelectedColumns);
        columnGroupModel.setColumnGroupCollapseable(groupGroupSelectedColumns[0], true);
        columnGroupModel.setStaticColumnIndexesByGroup(headerNameStr, new int[] { groupGroupSelectedColumns[3] });
        // reset
        columnGroupCounter = 0;
        groupGroupSelectedColumnsList.clear();
      }
    }
  }

  /**
   * @param a2lWpDefVersId
   * @param next
   * @return
   */
  private String getHeaderName(final Long a2lWpDefVersId, final A2lVariantGroup a2lVarGrp) {
    final StringBuilder headerName = new StringBuilder();
    A2lWpDefnVersion a2lWpDefVer =
        this.paramCompareHandler.getA2lWpInfoMap().get(a2lWpDefVersId).getA2lWpDefnVersMap().get(a2lWpDefVersId);
    String a2lFileName =
        this.paramCompareHandler.getA2lWpInfoMap().get(a2lWpDefVersId).getPidcA2lBo().getPidcA2l().getName();
    headerName.append(a2lFileName);
    headerName.append("-");
    headerName.append(a2lWpDefVer.getName());
    // add var grp name if selected
    if (CommonUtils.isNotNull(a2lVarGrp)) {
      headerName.append("-");
      headerName.append(a2lVarGrp.getName());
    }
    return headerName.toString();
  }

  /**
   * Resets menu reset state.
   *
   * @param popupMenu menu
   */
  public void resetMenuItem(final Menu popupMenu) {
    MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
    menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
    menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
    menuItem.setEnabled(true);
  }

  private IConfiguration getCustomComparatorConfiguration(final AbstractLayer columnHeaderDataLayer) {

    return new AbstractRegistryConfiguration() {

      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Add label accumulator
        ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
        columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);

        // Register labels
        labelAccumulator.registerColumnOverrides(0, CUSTOM_COMPARATOR_LABEL + 0);

        labelAccumulator.registerColumnOverrides(1, CUSTOM_COMPARATOR_LABEL + 1);

        labelAccumulator.registerColumnOverrides(2, CUSTOM_COMPARATOR_LABEL + 2);


        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, new NullComparator(), NORMAL,
            CUSTOM_COMPARATOR_LABEL + 0);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getA2lParamCompareComparator(1),
            NORMAL, CUSTOM_COMPARATOR_LABEL + 1);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getA2lParamCompareComparator(2),
            NORMAL, CUSTOM_COMPARATOR_LABEL + 2);


        for (int i = 3; i < (A2lParamComparePage.this.propertyToLabelMap.size()); i++) {

          labelAccumulator.registerColumnOverrides(i, CUSTOM_COMPARATOR_LABEL + i);
          configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getA2lParamCompareComparator(i),
              NORMAL, CUSTOM_COMPARATOR_LABEL + i);
        }

      }
    };
  }


  /**
   * @param columnNum
   * @return
   */
  public static Comparator<A2lParamCompareRowObject> getA2lParamCompareComparator(final int columnNum) {

    return (cmpRowObj1, cmpRowObj2) -> {

      int result = 0;
      switch (columnNum) {

        case IUIConstants.A2LCMP_ICON_COL_INDEX:
          break;
        case IUIConstants.A2LCMP_PARAM_COL_INDEX:
          result = ModelUtil.compare(cmpRowObj1.getParamName(), cmpRowObj2.getParamName());
          break;
        case IUIConstants.A2LCMP_DIFF_COL_INDEX:
          result = ModelUtil.compare(cmpRowObj1.isComputedDiff(), cmpRowObj2.isComputedDiff());
          break;
        default:
          result = compareA2LParams(cmpRowObj1, cmpRowObj2, columnNum);
      }
      return result;
    };
  }


  private static int compareA2LParams(final A2lParamCompareRowObject cmpRowObj1,
      final A2lParamCompareRowObject cmpRowObj2, final int columnNum) {
    String flagString = cmpRowObj1.getA2lColumnDataMapper().getColumnIndexFlagMap().get(columnNum);
    int result = 0;
    switch (flagString) {
      case IUIConstants.FUNCTION_NAME:
        result = ModelUtil.compare(cmpRowObj1.getFuncName(columnNum), cmpRowObj2.getFuncName(columnNum));
        break;
      case IUIConstants.FUNCTION_VERS:
        result = ModelUtil.compare(cmpRowObj1.getFuncVers(columnNum), cmpRowObj2.getFuncVers(columnNum));
        break;
      case IUIConstants.BC:
        result = ModelUtil.compare(cmpRowObj1.getBcName(columnNum), cmpRowObj2.getBcName(columnNum));
        break;
      case IUIConstants.WORK_PACKAGE:
        result = ModelUtil.compare(cmpRowObj1.getWpName(columnNum), cmpRowObj2.getWpName(columnNum));
        break;
      case IUIConstants.RESPONSIBILTY:
        result = ModelUtil.compare(cmpRowObj1.getRespName(columnNum), cmpRowObj2.getRespName(columnNum));
        break;
      case IUIConstants.NAME_AT_CUSTOMER:
        result = ModelUtil.compare(cmpRowObj1.getNameAtCust(columnNum), cmpRowObj2.getNameAtCust(columnNum));
        break;
      default:
        break;
    }
    return result;
  }


  /**
   *
   */
  private void fillNattableRowObjects() {
    SortedSet<A2LWpParamInfo> a2lWpParamInfoSet = new TreeSet<>();
    Map<Long, A2LWPInfoBO> a2lWpInfoBoMap = this.paramCompareHandler.getA2lWpInfoMap();
    Map<Long, Long> a2lWpDefVarGrpMap = new HashMap<>();
    Map<A2lWpDefnVersion, Set<A2lVariantGroup>> a2lVarGrpSet = this.paramCompareHandler.getA2lWpDefVarGrpMap();
    for (Entry<A2lWpDefnVersion, Set<A2lVariantGroup>> a2lVarGrpEntrySet : a2lVarGrpSet.entrySet()) {
      A2LWPInfoBO a2lWpInfoBo = a2lWpInfoBoMap.get(a2lVarGrpEntrySet.getKey().getId());
      // fill param info set based on the variant group level chosen
      fillA2lWpParamInfoSet(a2lWpParamInfoSet, a2lVarGrpEntrySet, a2lWpInfoBo);
    }
    boolean updatePropertyToLabelMap = true;
    for (A2LWpParamInfo a2lWpParamInfo : a2lWpParamInfoSet) {
      // create new compare row object
      A2lParamCompareRowObject compareRowObj =
          new A2lParamCompareRowObject(this.paramCompareHandler.getA2lWpInfoMap(), a2lWpDefVarGrpMap);
      compareRowObj.setParamName(a2lWpParamInfo.getParamName());
      compareRowObj.setParamId(a2lWpParamInfo.getParamId());
      int columnCounter = 2;
      setCompRowObjData(a2lVarGrpSet, updatePropertyToLabelMap, compareRowObj, columnCounter);
      updatePropertyToLabelMap = false;
      this.compareRowObjects.add(compareRowObj);
    }
  }

  /**
   * @param a2lVarGrpSet
   * @param updatePropertyToLabelMap
   * @param compareRowObj
   * @param columnCounter
   */
  private void setCompRowObjData(final Map<A2lWpDefnVersion, Set<A2lVariantGroup>> a2lVarGrpSet,
      final boolean updatePropertyToLabelMap, final A2lParamCompareRowObject compareRowObj, int columnCounter) {
    for (Entry<A2lWpDefnVersion, Set<A2lVariantGroup>> a2lVarGrpEntrySet : a2lVarGrpSet.entrySet()) {
      for (A2lVariantGroup selA2LVariantGroup : a2lVarGrpEntrySet.getValue()) {
        compareRowObj.addA2lColumnData(a2lVarGrpEntrySet.getKey().getId(), selA2LVariantGroup);
        if (updatePropertyToLabelMap) {
          columnCounter = updatePropToLabelMap(columnCounter);
          this.colA2lWpDefMap.put(columnCounter, a2lVarGrpEntrySet.getKey());
          this.colVarGrpMap.put(columnCounter, selA2LVariantGroup);
        }
      }
      if ((a2lVarGrpEntrySet.getValue().size() == 1) && CommonUtils.isNullOrEmpty(a2lVarGrpEntrySet.getValue())) {
        compareRowObj.addA2lColumnData(a2lVarGrpEntrySet.getKey().getId(), null);
        if (updatePropertyToLabelMap) {
          columnCounter = updatePropToLabelMap(columnCounter);
          this.colA2lWpDefMap.put(columnCounter, a2lVarGrpEntrySet.getKey());
          this.colVarGrpMap.put(columnCounter, a2lVarGrpEntrySet.getValue().iterator().next());
        }
      }
    }
  }

  /**
   * @param a2lWpParamInfoSet a2l wp param info collection
   * @param a2lVarGrpEntrySet Entry<A2lWpDefnVersion, Set<A2lVariantGroup>>
   * @param a2lWpInfoBo a2l Wp Info Bo
   */
  private void fillA2lWpParamInfoSet(final SortedSet<A2LWpParamInfo> a2lWpParamInfoSet,
      final Entry<A2lWpDefnVersion, Set<A2lVariantGroup>> a2lVarGrpEntrySet, final A2LWPInfoBO a2lWpInfoBo) {
    for (A2lVariantGroup selA2LA2lVariantGroup : a2lVarGrpEntrySet.getValue()) {
      if (selA2LA2lVariantGroup == null) {
        a2lWpParamInfoSet.addAll(a2lWpInfoBo.getA2lWParamInfoMap().values());
      }
      else {
        a2lWpParamInfoSet.addAll(a2lWpInfoBo.getA2lWParamInfoForVarGrp().get(selA2LA2lVariantGroup.getId()).values());
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    Map<IModelType, Map<Long, ChangeData<?>>> consChangeData = dce.getConsChangeData();
    for (IModelType type : consChangeData.keySet()) {
      if ((type == MODEL_TYPE.A2L_WP_DEFN_VERSION) || (type == MODEL_TYPE.A2L_VARIANT_GROUP)) {
        reconstructNatTable();
      }
      else {
        refreshTable();
      }
    }
  }

  /**
   * @param columnCounter
   * @return
   */
  private int updatePropToLabelMap(final int columnCounter) {
    int counter = columnCounter;
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, IUIConstants.FUNCTION_NAME);
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, IUIConstants.FUNCTION_VERS);
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, IUIConstants.BC);
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, IUIConstants.WORK_PACKAGE);
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, IUIConstants.RESPONSIBILTY);
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, IUIConstants.NAME_AT_CUSTOMER);
    return counter;
  }

  /**
   * input for status line
   *
   * @param outlineSelection flag set according to selection made in viewPart or editor.
   */
  public void setStatusBarMessage(final boolean outlineSelection) {
    this.totTableRowCount = this.compareRowObjects.size();
    this.a2lParamCompareEditor.updateStatusBar(outlineSelection, this.totTableRowCount,
        this.a2lCompFilterGridLayer != null ? this.a2lCompFilterGridLayer.getRowHeaderLayer().getPreferredRowCount()
            : 0);
  }

  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMessage(false);
  }

  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(modifyEvent -> {
      final String text = A2lParamComparePage.this.filterTxt.getText().trim();
      A2lParamComparePage.this.allColumnFilterMatcher.setFilterText(text, true);
      A2lParamComparePage.this.a2lCompFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
      A2lParamComparePage.this.a2lCompFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(A2lParamComparePage.this.a2lCompFilterGridLayer.getSortableColumnHeaderLayer()));
      setStatusBarMessage(false);
      A2lParamComparePage.this.natTable.refresh();
    }


    );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart arg0, final ISelection arg1) {
    // Not yet Implemented

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
  *
  */
  public void reconstructNatTable() {
    this.natTable.dispose();
    this.toolBarManager.dispose();
    this.propertyToLabelMap.clear();
    this.a2lCompFilterGridLayer = null;
    this.compareRowObjects.clear();
    this.paramCompareHandler = this.a2lParamCompareEditor.getEditorInput().getParamCompareHandler();
    createNatTable();
    createToolBarAction();
    this.totTableRowCount = this.a2lCompFilterGridLayer.getRowHeaderLayer().getPreferredColumnCount();
    ((A2LCompareEditor) getEditor()).firePropChange(PROP_TITLE);
    // First the form's body is repacked and then the section is repacked
    // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable
    this.form.getBody().pack();
    this.section.layout();
    setStatusBarMessage(false);
  }


  /**
   * @param selVerVarGrpMap Map<A2lWpDefnVersion, A2lVariantGroup>
   */
  public void addToExistingCompareEditor(final Map<A2lWpDefnVersion, Set<A2lVariantGroup>> selVerVarGrpMap) {
    Map<A2lWpDefnVersion, Set<A2lVariantGroup>> commonDefns = new HashMap<>();
    findExistingWpVarComb(selVerVarGrpMap, commonDefns);
    if (!commonDefns.isEmpty()) {
      StringBuilder existingDefns = new StringBuilder();
      existingDefns.append("The following combination(s) already exists. So they are not added to compare editor");
      existingDefnVarGrpName(commonDefns, existingDefns);
      MessageDialogUtils.getInfoMessageDialog("Info", existingDefns.toString());
    }
    this.a2lParamCompareEditor.getEditorInput().getA2lCompareJob().schedule();
  }

  /**
   * @param commonDefns
   * @param existingDefns
   */
  private void existingDefnVarGrpName(final Map<A2lWpDefnVersion, Set<A2lVariantGroup>> commonDefns,
      final StringBuilder existingDefns) {
    for (Entry<A2lWpDefnVersion, Set<A2lVariantGroup>> existingComb : commonDefns.entrySet()) {
      if (existingComb.getValue().size() == 1) {
        existingDefns.append("\n" + existingComb.getKey().getName() + "->");
        A2lVariantGroup varGrp = existingComb.getValue().iterator().next();
        if (varGrp == null) {
          existingDefns.append("Default");
        }
        else {
          existingDefns.append(varGrp.getName());
        }
      }
      else {
        for (A2lVariantGroup a2lVarGrp : existingComb.getValue()) {
          getExistingDefns(existingDefns, existingComb, a2lVarGrp);
        }
        existingDefns.deleteCharAt(existingDefns.length() - 1);
      }
      existingDefns.append("\n");
    }
  }

  /**
   * @param existingDefns
   * @param existingComb
   * @param a2lVarGrp
   */
  private void getExistingDefns(final StringBuilder existingDefns,
      final Entry<A2lWpDefnVersion, Set<A2lVariantGroup>> existingComb, final A2lVariantGroup a2lVarGrp) {
    existingDefns.append("\n" + existingComb.getKey().getName() + "->");
    if (a2lVarGrp == null) {
      existingDefns.append("Default ,");
    }
    else {
      existingDefns.append(a2lVarGrp.getName()).append(" ,");
    }
  }

  /**
   * @param selVerVarGrpMap
   * @param commonDefns
   */
  private void findExistingWpVarComb(final Map<A2lWpDefnVersion, Set<A2lVariantGroup>> selVerVarGrpMap,
      final Map<A2lWpDefnVersion, Set<A2lVariantGroup>> commonDefns) {
    Map<A2lWpDefnVersion, Set<A2lVariantGroup>> a2lWpVarMap =
        this.a2lParamCompareEditor.getEditorInput().getA2lCompareJob().getA2lWpDefVerVarGrpMap();
    for (Entry<A2lWpDefnVersion, Set<A2lVariantGroup>> entrySet : selVerVarGrpMap.entrySet()) {
      Set<A2lVariantGroup> varaintGrpSet = a2lWpVarMap.get(entrySet.getKey());
      Set<A2lVariantGroup> entryValue = entrySet.getValue();
      if (this.a2lParamCompareEditor.getEditorInput().getA2lCompareJob().getA2lWpDefVerVarGrpMap()
          .containsKey(entrySet.getKey())) {
        addValsToWpVarMap(commonDefns, a2lWpVarMap, entrySet, varaintGrpSet, entryValue);
      }
      else {
        addNewValuesToWpVarMap(a2lWpVarMap, entrySet, entryValue);
      }
    }
  }

  /**
   * @param commonDefns
   * @param a2lWpVarMap
   * @param entrySet
   * @param varaintGrpSet
   * @param entryValue
   */
  private void addValsToWpVarMap(final Map<A2lWpDefnVersion, Set<A2lVariantGroup>> commonDefns,
      final Map<A2lWpDefnVersion, Set<A2lVariantGroup>> a2lWpVarMap,
      final Entry<A2lWpDefnVersion, Set<A2lVariantGroup>> entrySet, final Set<A2lVariantGroup> varaintGrpSet,
      final Set<A2lVariantGroup> entryValue) {
    for (A2lVariantGroup a2lVariantGroup : entryValue) {
      if (varaintGrpSet.contains(a2lVariantGroup)) {
        commonDefns.put(entrySet.getKey(), entryValue);
      }
      else {
        addNewValuesToWpVarMap(a2lWpVarMap, entrySet, entryValue);
      }
    }
  }

  /**
   * @param a2lWpVarMap
   * @param entrySet
   * @param entryValue
   */
  private void addNewValuesToWpVarMap(final Map<A2lWpDefnVersion, Set<A2lVariantGroup>> a2lWpVarMap,
      final Entry<A2lWpDefnVersion, Set<A2lVariantGroup>> entrySet, final Set<A2lVariantGroup> entryValue) {
    if (a2lWpVarMap.containsKey(entrySet.getKey())) {
      a2lWpVarMap.get(entrySet.getKey()).addAll(entryValue);
    }
    else {
      this.a2lParamCompareEditor.getEditorInput().getA2lCompareJob().getA2lWpDefVerVarGrpMap().put(entrySet.getKey(),
          entryValue);
    }
  }


  /**
   * @return the a2lCompFilterGridLayer
   */
  public CustomFilterGridLayer getA2lCompFilterGridLayer() {
    return this.a2lCompFilterGridLayer;
  }


  /**
   * @return the natTable
   */
  public CustomNATTable getNatTable() {
    return this.natTable;
  }


  /**
   * @return the propertyToLabelMap
   */
  public final Map<Integer, String> getPropertyToLabelMap() {
    return this.propertyToLabelMap;
  }

  /**
   * @return the compareRowObjects
   */
  public final SortedSet<A2lParamCompareRowObject> getCompareRowObjects() {
    return this.compareRowObjects;
  }


  /**
   * @return the paramCompareHandler
   */
  public final A2LCompareHandler getParamCompareHandler() {
    return this.paramCompareHandler;
  }
}
