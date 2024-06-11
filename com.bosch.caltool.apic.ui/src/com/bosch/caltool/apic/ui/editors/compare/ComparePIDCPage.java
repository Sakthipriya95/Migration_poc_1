/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.compare;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommand;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.config.NullComparator;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataToClipboardCommand;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.ICellEditor;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;
import org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.freeze.command.FreezeSelectionCommand;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel.ColumnGroup;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.MenuItemProviders;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.PidcElementAttrValidationBO;
import com.bosch.caltool.apic.ui.actions.ComparePIDCToolBarActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCGroupedAttrActionSet;
import com.bosch.caltool.apic.ui.dialogs.PIDCAttrValueEditDialog;
import com.bosch.caltool.apic.ui.dialogs.PIDCGrpdAttrChangesDialog;
import com.bosch.caltool.apic.ui.editors.PIDCCompareEditor;
import com.bosch.caltool.apic.ui.editors.PIDCCompareEditorInput;
import com.bosch.caltool.apic.ui.table.filters.ComparePIDCOutlineFilter;
import com.bosch.caltool.apic.ui.table.filters.ComparePIDCToolBarFilter;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.apic.ui.util.PIDCPageEditUtil;
import com.bosch.caltool.apic.ui.views.PIDCCompareDetailsPg;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcDetailsLoader;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.apic.ProjectAttributeUtil;
import com.bosch.caltool.icdm.client.bo.apic.ProjectHandlerInit;
import com.bosch.caltool.icdm.client.bo.apic.pidc.PidcAttrValueEditBO;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseRootNode;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.editors.AbstractNatFormPage;
import com.bosch.caltool.icdm.common.ui.providers.ScratchPadDataFetcher;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ICDMClipboard;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ICDMLoggerConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValueAndValidtyModel;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.apic.ProjectAttributesUpdationServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.wbutils.WorkbenchUtils;

/**
 * @author jvi6cob
 */
public class ComparePIDCPage extends AbstractNatFormPage implements ISelectionListener {

  /**
   * Constant to show error when duplicate value is being copied
   */
  private static final String ATTRIBUTE_VALUE_EXISTING = " already existing in target PIDC";

  /**
   * PREFIX of error message string to be shown in the dialog or logged in error log view in case of invalid paste
   * operation
   */
  private static final String PASTE_ERR_MSG_FIRST_LINE = "Paste was not successful on following attribute(s):-\n";


  /**
   * Constant for Adding to editor error
   */
  private static final String CANNOT_ADD_TO_COMPARE_EDITOR = "Cannot add to Compare Editor";
  /**
   * NO. of attributes to be shown in dialog when error list is huge in case of paste opeartion
   */
  private static final int MAX_NUM_OF_ATTR = 3;

  /**
   *
   */
  private static final int FIXED_COL_COUNT = 3;

  /**
   *
   */
  private static final int PIDCNAMES_INTIAL_SIZE = 50;

  /**
   *
   */
  private static final int BALL_COLUMN_WIDTH = 30;

  /**
   *
   */
  private static final String GROUP_GROUP_REGION = "COLUMN_GROUP_GROUP_HEADER";

  private final PIDCCompareEditor editor;

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

  private CompareNatInputToColumnConverter natInputToColumnConverter;

  private static final String CHECK_BOX_CONFIG_LABEL = "checkBox";
  private static final String CHECK_BOX_EDITOR_CNG_LBL = "checkBoxEditor";

  private static final int CHECKBOX_COLUMN_WIDTH = 40;
  private static final int SUMMARY_COLUMN_WIDTH = 65;
  private static final int DESCRIPTION_COLUMN_WIDTH = 150;
  private static final int ATTRIBUTE_COLUMN_WIDTH = 150;
  private ComparePIDCOutlineFilter outLineNatFilter;
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  /**
   *
   */
  private static final int YCOORDINATE_TEN = 10;
  /**
   *
   */
  private static final int XCOORDINATE_TEN = 10;

  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;

  private CustomFilterGridLayer<CompareRowObject> compPIDCFilterGridLayer;
  private CustomNATTable customNatTable;
  private RowSelectionProvider<CompareRowObject> selectionProvider;
  private Map<Integer, String> propertyToLabelMap;
  private int totTableRowCount;

  private SortedSet<CompareRowObject> compareRowObjects;

  private AllComparePIDCColumnFilterMatcher<CompareRowObject> allColumnFilterMatcher;
  /**
   * ToolBarManager instance
   */
  private ToolBarManager toolBarManager;

  private ComparePIDCToolBarActionSet toolBarActionSet;

  private ComparePIDCToolBarFilter toolBarFilters;
  private boolean isVariantsCompare;
  private boolean isSubVarsCompare;


  List<IProjectObject> compareObjs = new ArrayList<>();

  private final Map<Integer, Integer> columnWidthMap = new HashMap<>();

  private final ProjectAttributeUtil compareEditorUtil = new ProjectAttributeUtil();

  /**
   * Map to hold the filter action text and its changed state (checked - true or false)
   */
  private final Map<String, Boolean> toolBarFilterStateMap = new ConcurrentHashMap<>();

  /**
   * selected column position
   */
  private int selectedColPostn;

  /**
   * selected row position
   */
  private int selectedRowPostn;

  private static final String CANNOT_EDIT_ERR_MSG = "Cannot edit this attribute";

  private static final String ATTR_VAL_NOT_AVAIL_AT_SRC = " Value not available at source";

  private static final String ATTR_AT_CHILD_LEVEL_AT_SOURCE = " Source PIDC attribute at child level ";

  /**
   * @param compareEditor
   * @param compareObjList objects to compare
   * @param dataHandlerMap - data handler map
   */
  public ComparePIDCPage(final AbstractFormEditor compareEditor, final List<IProjectObject> compareObjList) {
    super(compareEditor, "PIDC.Compare", "Attributes");
    this.editor = (PIDCCompareEditor) compareEditor;
    this.compareObjs.addAll(compareObjList);
    if (compareObjList.get(0) instanceof PidcVariant) {
      this.isVariantsCompare = true;
    }
    else if (compareObjList.get(0) instanceof PidcSubVariant) {
      this.isSubVarsCompare = true;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    if (this.isVariantsCompare || this.isSubVarsCompare) {
      this.nonScrollableForm.setText(this.compareObjs.get(0).getName());
    }
    else {
      this.nonScrollableForm.setText("Versions Compare");
    }
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
    this.section = SectionUtil.getInstance().createSection(this.composite, toolkit, "Attributes");
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    // ICDM-183
    this.section.getDescriptionControl().setEnabled(false);
    this.section.setDescription(
        "Please use \'copy-paste\' or \'drag & drop\' feature to copy attribute values from one Pidc version/variant/subvariant to another.");

    createForm(toolkit);

    // Code Change made for fixing the number of attributes coming as zero
    this.section.setClient(this.form);
  }

  /**
   * This method initializes scrolledForm
   */
  private void createForm(final FormToolkit toolkit) {
    this.form = toolkit.createForm(this.section);

    this.form.getToolBarManager().update(true);
    this.form.setToolBarVerticalAlignment(SWT.UP);


    this.filterTxt = TextUtil.getInstance().createFilterText(toolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.form.getBody().setLayout(new GridLayout());
    addModifyListenerForFilterTxt();
    createNatTable();
    createToolBarAction();
    this.totTableRowCount = this.compPIDCFilterGridLayer.getRowHeaderLayer().getPreferredRowCount();
  }

  /**
   * Create the PreDefined Filter for the page-
   */
  private void createToolBarAction() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = this.toolBarManager.createControl(this.nonScrollableForm.getHead());
    final Separator separator = new Separator();
    addHelpAction(this.toolBarManager);
    this.toolBarActionSet = new ComparePIDCToolBarActionSet(this.compPIDCFilterGridLayer, this);
    this.toolBarActionSet.pidcAttrInvisibleAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);
    this.toolBarActionSet.pidcAttrDiffFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.pidcAttrNotDiffFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);
    this.toolBarActionSet.pidcAttrDepenFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.pidcAttrNotDepenFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);
    this.toolBarActionSet.pidcAttrMandatoryFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.pidcAttrNonMandatoryFilterAction(this.toolBarManager, this.toolBarFilters);

    addResetAllFiltersAction();

    this.toolBarManager.update(true);
    this.section.setTextClient(toolbar);


  }

  /**
   * Add reset filter button
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.compPIDCFilterGridLayer);
    addResetFiltersAction();
  }

  /**
   *
   */
  private void createNatTable() {

    this.propertyToLabelMap = new LinkedHashMap<>();
    this.propertyToLabelMap.put(0, "");
    this.propertyToLabelMap.put(1, "Attribute");
    this.propertyToLabelMap.put(2, "Description");
    this.propertyToLabelMap.put(3, "Diff");

    // The below map is used by NatTable to Map Columns with their respective widths
    // Width is based on pixels

    this.columnWidthMap.put(0, BALL_COLUMN_WIDTH);
    this.columnWidthMap.put(1, ATTRIBUTE_COLUMN_WIDTH);
    this.columnWidthMap.put(2, DESCRIPTION_COLUMN_WIDTH);
    this.columnWidthMap.put(3, CHECKBOX_COLUMN_WIDTH);

    // fill attributes information for table
    fillNattableRowObjects();

    this.natInputToColumnConverter = new CompareNatInputToColumnConverter();
    IConfigRegistry configRegistry = new ConfigRegistry();

    // A Custom Filter Grid Layer is constructed
    this.compPIDCFilterGridLayer = new CustomFilterGridLayer<>(configRegistry, this.compareRowObjects,
        this.columnWidthMap, new CustomPIDCCompareColumnPropertyAccessor<>(), new CustomPIDCCompareHeaderDataProvider(),
        getComparePIDCComparator(0), this.natInputToColumnConverter, this, null, true, true, true);

    this.outLineNatFilter = new ComparePIDCOutlineFilter(
        ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getPidcVersionBO().getPidcDataHandler(),
        ((PIDCCompareEditorInput) getEditorInput()).getOutlineViewDataHandler());

    this.compPIDCFilterGridLayer.getFilterStrategy()
        .setOutlineNatFilterMatcher(this.outLineNatFilter.getOutlineMatcher());
    this.allColumnFilterMatcher = new AllComparePIDCColumnFilterMatcher<>();
    this.compPIDCFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    this.customNatTable = new CustomNATTable(
        this.form.getBody(), SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED |
            SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        this.compPIDCFilterGridLayer, false, getClass().getSimpleName());
    this.toolBarFilters = new ComparePIDCToolBarFilter();
    this.compPIDCFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());
    this.customNatTable.setConfigRegistry(configRegistry);
    this.customNatTable.setLayoutData(getGridData());
    this.customNatTable.addConfiguration(new CustomNatTableStyleConfiguration());
    this.customNatTable.addConfiguration(new FilterRowCustomConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry localConfigRegistry1) {
        super.configureRegistry(localConfigRegistry1);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        localConfigRegistry1.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });
    this.customNatTable.addConfiguration(new SingleClickSortConfiguration());
    this.customNatTable
        .addConfiguration(getCustomComparatorConfiguration(this.compPIDCFilterGridLayer.getColumnHeaderDataLayer()));
    this.customNatTable.addConfiguration(new HeaderMenuConfiguration(this.customNatTable) {

      @Override
      protected PopupMenuBuilder createColumnHeaderMenu(final NatTable natTable) {
        return super.createColumnHeaderMenu(natTable).withStateManagerMenuItemProvider();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GROUP_GROUP_REGION, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(ComparePIDCPage.this.customNatTable)
                .withMenuItemProvider(openPIDCMenuItemProvider("Open PIDC Version"))/* ICDM- 1622 */
                .withMenuItemProvider(removeColumnMenuItemProvider("Remove")).build()));


        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_GROUP_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(ComparePIDCPage.this.customNatTable)
                .withMenuItemProvider(hideSimilarColumnGroupMenuItemProvider("Hide similar")).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });

    // Group columns programmatically
    groupColumns();


    //
    this.customNatTable.addConfiguration(new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        registerConfigLabelsOnColumns((ColumnOverrideLabelAccumulator) ComparePIDCPage.this.compPIDCFilterGridLayer
            .getDummyDataLayer().getConfigLabelAccumulator());
        registerCheckBoxEditor(configRegistry);
        registerEditableRules(configRegistry);

      }
    });
    this.customNatTable.addMouseListener(new MouseEventListener());
    this.customNatTable.addConfiguration(
        new ComparePIDCEditConfiguration(((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler()));

    DataLayer bodyDataLayer = this.compPIDCFilterGridLayer.getDummyDataLayer();
    IRowDataProvider<CompareRowObject> bodyDataProvider =
        (IRowDataProvider<CompareRowObject>) bodyDataLayer.getDataProvider();
    final ComparePIDCLabelAccumulator compPIDCLabelAccumulator =
        new ComparePIDCLabelAccumulator(bodyDataLayer, bodyDataProvider);
    bodyDataLayer.setConfigLabelAccumulator(compPIDCLabelAccumulator);
    // Configuration for copy-paste keyboard shortcut
    this.customNatTable.addConfiguration(
        new ComparePidcCopyPasteConfig(this.compPIDCFilterGridLayer.getBodyLayer().getSelectionLayer(),
            this.customNatTable.getInternalCellClipboard(), this));
    addRightClickMenu();
    this.customNatTable.configure();
    // get the reference to the SelectionLayer
    SelectionLayer selectionLayer = this.compPIDCFilterGridLayer.getBodyLayer().getSelectionLayer();
    // select cell with column position 3 and row position 0
    selectionLayer.setSelectedCell(4, 0);
    // freeze the first two columns
    this.customNatTable.doCommand(new FreezeSelectionCommand());
    // reset the selection to first row first column
    selectionLayer.setSelectedCell(0, 0);
    this.compPIDCFilterGridLayer
        .registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.customNatTable));
    this.selectionProvider = new RowSelectionProvider<>(this.compPIDCFilterGridLayer.getBodyLayer().getSelectionLayer(),
        this.compPIDCFilterGridLayer.getBodyDataProvider(), false);

    this.selectionProvider.addSelectionChangedListener(event -> {

      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      // TODO: Check if properties view can be activated for nattable without this selection listener approach
      if (selection.getFirstElement() instanceof CompareRowObject) {
        IViewPart propViewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView(CommonUIConstants.PROPERTIES_VIEW);
        if (propViewPart != null) {
          PropertySheet propertySheet = (PropertySheet) propViewPart;
          IPropertySheetPage page = (IPropertySheetPage) propertySheet.getCurrentPage();
          if (page != null) {
            page.selectionChanged(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(), selection);
          }
        }
      }
    });
    // The below method is required to enable tootltip only for cells which contain not fully visible content
    attachToolTip(this.customNatTable);
    setDragSupport();
    setDropSupport();

    getSite().setSelectionProvider(this.selectionProvider);

  }


  private void fillNattableRowObjects() {

    SortedSet<com.bosch.caltool.icdm.model.apic.attr.Attribute> attributesInput = new TreeSet<>();
    // Map of compare obj bo id and it's attributes map
    Map<Long, Map<Long, IProjectAttribute>> projAttrMap = new HashMap<>();
    // populate compare object and it's attributes map
    for (IProjectObject objToCompare : this.compareObjs) {
      AbstractProjectObjectBO abstractProjectObjectBO = ((PIDCCompareEditorInput) getEditorInput())
          .getComparePidcHandler().getCompareObjectsHandlerMap().get(objToCompare.getId());
      projAttrMap.put(objToCompare.getId(), (abstractProjectObjectBO.getAttributesAll()));
    }
    AbstractProjectObjectBO firstCompareObjBo = ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler()
        .getCompareObjectsHandlerMap().get(this.compareObjs.get(0).getId());
    fillCompareObjAttr(attributesInput, projAttrMap, firstCompareObjBo);
    this.compareRowObjects = new TreeSet<>();
    boolean updatePropertyToLabelMap = true;

    //
    for (com.bosch.caltool.icdm.model.apic.attr.Attribute attribute : attributesInput) {
      // If check added to prevent invalid rows for Not required attributes
      if (attribute.isDeleted() || (attribute.getLevel() == ApicConstants.PROJECT_NAME_ATTR) ||
          (attribute.getLevel() == ApicConstants.VARIANT_CODE_ATTR) ||
          (attribute.getLevel() == ApicConstants.SUB_VARIANT_CODE_ATTR)) {
        continue;
      }

      // creatte new compare row object
      CompareRowObject compareRowObject = new CompareRowObject(
          ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getCompareObjectsHandlerMap());
      // set attribute
      compareRowObject.setAttribute(attribute);
      IProjectAttribute projAttribute;
      int columnCounter = 3;
      for (IProjectObject objToCompare : this.compareObjs) {
        // initialize project attribute for project object and attribute
        projAttribute = projAttrMap.get(objToCompare.getId()).get(attribute.getId());
        // add project attribute to column map
        compareRowObject.addProjectAttribute(projAttribute);
        if (updatePropertyToLabelMap) {
          columnCounter = updatePropToLabelMap(this.columnWidthMap, columnCounter);
        }
      }

      updatePropertyToLabelMap = false;
      this.compareRowObjects.add(compareRowObject);
    }
  }

  /**
   * @param attributesInput
   * @param projAttr
   * @param firstCompareObjBo
   */
  private void fillCompareObjAttr(final SortedSet<com.bosch.caltool.icdm.model.apic.attr.Attribute> attributesInput,
      final Map<Long, Map<Long, IProjectAttribute>> projAttr, final AbstractProjectObjectBO firstCompareObjBo) {
    if ((null != this.compareObjs) && (firstCompareObjBo != null)) {

      for (IProjectAttribute ipidcAttr : projAttr.get(this.compareObjs.get(0).getId()).values()) {

        if ((((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getCompareObjectsHandlerMap().values()
            .iterator().next()).getPidcDataHandler().getAttributeMap().get(ipidcAttr.getAttrId()) != null) {
          attributesInput
              .add(((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getCompareObjectsHandlerMap()
                  .values().iterator().next().getPidcDataHandler().getAttributeMap().get(ipidcAttr.getAttrId()));
        }
      }

    }
  }

  /**
   * This method adds right click menu for tableviewer.
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {
      IStructuredSelection selection = (IStructuredSelection) ComparePIDCPage.this.selectionProvider.getSelection();
      CompareRowObject rowObject = (CompareRowObject) selection.getFirstElement();
      if ((rowObject != null) && (selection.size() != 0) && isValColItemSelected(rowObject) &&
          hasPrivilegeToModifySelPidc(rowObject)) {
        createCopyAction(selection, mgr);
        createPasteRespAction(selection, mgr);
      }
      menuMgr.add(new Separator());
    });

    final Menu menu = menuMgr.createContextMenu(this.customNatTable.getShell());
    this.customNatTable.setMenu(menu);

    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.selectionProvider);
  }


  /**
   * Creates the paste resp action.
   *
   * @param selection IStructuredSelection
   * @param mgr IMenuManager
   */
  public void createPasteRespAction(final IStructuredSelection selection, final IMenuManager mgr) {
    final Action pasteAction = new Action() {

      @Override
      public void run() {
        pasteAttrValInTargetPidc(selection);
        ComparePIDCPage.this.customNatTable.refresh();

      }
    };
    pasteAction.setText("Paste");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PASTE_16X16);
    pasteAction.setImageDescriptor(imageDesc);
    pasteAction.setEnabled(CommonUtils.isNotNull(ICDMClipboard.getInstance().getCopiedObject()));
    mgr.add(pasteAction);
  }

  /**
   * @param compareRowObject row object of nat table
   * @return true if items in value column is selected
   */
  public boolean isValColItemSelected(final CompareRowObject compareRowObject) {
    ColumnDataMapper columnDataMapper = compareRowObject.getColumnDataMapper();
    String columnType = columnDataMapper.getColumnIndexFlagMap().get(getSelectedColPostn());
    return ((columnType != null) && ApicConstants.VALUE_TEXT.equals(columnType));
  }

  /**
   * @param compareRowObject row object of nat table
   * @return true if the user has write access rights for the pidc
   */
  public boolean hasPrivilegeToModifySelPidc(final CompareRowObject compareRowObject) {
    ColumnDataMapper columnDataMapper = compareRowObject.getColumnDataMapper();
    IProjectAttribute pidcAttribute = columnDataMapper.getColumnIndexPIDCAttrMap().get(getSelectedColPostn());
    AbstractProjectObjectBO projObjBO = ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler()
        .getCompareObjectsHandlerMap().get(getCompareEditorUtil().getID(pidcAttribute));
    final CurrentUserBO currentUserBO = new CurrentUserBO();
    try {
      NodeAccess nodeAccess = currentUserBO.getNodeAccessRight(projObjBO.getPidcVersion().getPidcId());
      return ((nodeAccess != null) && nodeAccess.isWrite());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * @param copiedObjList list of objects copied
   * @return map key-> attribute id ,value-> project attribute
   *         instance(PidcVersionAttribute/PidcVariantAttribute/PidcSubVariantattribute), corresponding to copied object
   */
  public Map<Long, IProjectAttribute> getCopiedProjAttrMap(final List<?> copiedObjList) {
    Map<Long, IProjectAttribute> copiedAttrIdNProjAttrMap = new HashMap<>();
    for (Object compRowObj : copiedObjList) {
      if (compRowObj instanceof CompareRowObject) {
        Long attrId = ((CompareRowObject) compRowObj).getAttribute().getId();
        IProjectAttribute projAttr = ((CompareRowObject) compRowObj).getColumnDataMapper().getColumnIndexPIDCAttrMap()
            .get(this.selectedColPostn);
        copiedAttrIdNProjAttrMap.put(attrId, projAttr);
      }
    }
    return copiedAttrIdNProjAttrMap;
  }

  /**
   * Creates the copy action.
   *
   * @param selection the selection
   * @param mgr the mgr
   */
  private void createCopyAction(final IStructuredSelection selection, final IMenuManager mgr) {
    final Action copyAction = new Action() {

      @Override
      public void run() {
        CopyDataToClipboardCommand copyCommand = new CopyDataToClipboardCommand("\t",
            System.getProperty("line.separator"), ComparePIDCPage.this.customNatTable.getConfigRegistry());
        ComparePIDCPage.this.customNatTable.doCommand(copyCommand);
        List<?> copiedObjList = selection.toList();
        ICDMClipboard.getInstance().setCopiedObject(ComparePIDCPage.this.getCopiedProjAttrMap(copiedObjList));
      }
    };
    copyAction.setText("Copy");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EXPORT_DATA_16X16);
    copyAction.setImageDescriptor(imageDesc);
    copyAction.setEnabled(true);
    mgr.add(copyAction);
  }

  /**
   * @param colWidthMap
   * @param columnCounter
   * @return
   */
  private int updatePropToLabelMap(final Map<Integer, Integer> colWidthMap, final int columnCounter) {
    int counter = columnCounter;
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, Messages.getString(IMessageConstants.QUESTION_LABEL));
    colWidthMap.put(counter, CHECKBOX_COLUMN_WIDTH);
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, Messages.getString(IMessageConstants.NO_LABEL));
    colWidthMap.put(counter, CHECKBOX_COLUMN_WIDTH);
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, Messages.getString(IMessageConstants.YES_LABEL));
    colWidthMap.put(counter, CHECKBOX_COLUMN_WIDTH);
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, Messages.getString(IMessageConstants.SUMMARY_LABEL));
    colWidthMap.put(counter, SUMMARY_COLUMN_WIDTH);
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, Messages.getString(IMessageConstants.VALUE_LABEL));
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, "Part Number");
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, "Specification");
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, "Modified Date");
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, ApicConstants.CHARACTERISTIC);
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, ApicConstants.CHARVAL);
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, "Comment");
    return counter;
  }

  /**
   * input for status line
   *
   * @param outlineSelection flag set according to selection made in viewPart or editor.
   */
  public void setStatusBarMessage(final boolean outlineSelection) {
    this.editor.updateStatusBar(outlineSelection, this.totTableRowCount,
        this.compPIDCFilterGridLayer.getRowHeaderLayer().getPreferredRowCount());
  }

  private void setDragSupport() {

    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.customNatTable.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new DragSourceListener() {

      private IProjectAttribute selectedPIDCAttribute;

      @Override
      public void dragStart(final DragSourceEvent event) {
        // TODO: Drag can be canceled based on selection.This can be implemented once all draggable items are identified
        invokeDragAction(event);
      }

      /**
       * @param event
       */
      private void invokeDragAction(final DragSourceEvent event) {
        event.doit = false;
        CustomFilterGridLayer<CompareRowObject> customFilterGridLayer =
            (CustomFilterGridLayer<CompareRowObject>) ComparePIDCPage.this.customNatTable.getLayer();
        final SelectionLayer selectionLayer = customFilterGridLayer.getBodyLayer().getSelectionLayer();
        Collection<ILayerCell> selectedCells = selectionLayer.getSelectedCells();
        if (selectedCells.size() == 1) {// Prevent dragging of multiple cells but not working
          ILayerCell selectedSourceCell = selectedCells.iterator().next();
          int rowPosition = selectedSourceCell.getRowPosition();
          int columnPosition = selectedSourceCell.getColumnIndex();
          Object rowObject =
              ComparePIDCPage.this.compPIDCFilterGridLayer.getBodyDataProvider().getRowObject(rowPosition);
          if (rowObject instanceof CompareRowObject) {
            CompareRowObject compareRowObject = (CompareRowObject) rowObject;
            String columnHeader = compareRowObject.getColumnDataMapper().getColumnIndexFlagMap().get(columnPosition);
            if ((columnHeader != null) && columnHeader.equals(ApicConstants.VALUE_TEXT)) {
              this.selectedPIDCAttribute =
                  compareRowObject.getColumnDataMapper().getColumnIndexPIDCAttrMap().get(columnPosition);
              event.doit = true;
            }
          }
        }
      }

      @Override
      public void dragSetData(final DragSourceEvent event) {
        StructuredSelection structuredSelection = new StructuredSelection(this.selectedPIDCAttribute);
        LocalSelectionTransfer.getTransfer().setSelection(structuredSelection);
      }

      @Override
      public void dragFinished(final DragSourceEvent event) {
        // TODO: Can perform validation here but delegated to drop code since validating
      }
    });
  }

  /**
   *
   */
  private void setDropSupport() {
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.customNatTable.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new DropTargetAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void drop(final DropTargetEvent event) {
        if (event.data == null) {
          event.detail = DND.DROP_NONE;
          return;
        }
        StructuredSelection structuredSelection = (StructuredSelection) event.data;
        Object selElement = structuredSelection.getFirstElement();
        String errorMsg = "";
        // ICDM-1620
        if ((selElement instanceof PIDCDetailsNode) && !((PIDCDetailsNode) selElement).isVariantNode()) {
          errorMsg = addToExistingCompareEditor(
              new ArrayList<IProjectObject>(((PIDCDetailsNode) selElement).getVirtualNodeVars()));
        }
        else if (selElement instanceof IProjectAttribute) {
          performDropOfProjAttr(event, selElement);
        }
        else {
          List<IProjectObject> selectedObjs = new ArrayList<>();
          for (Object obj : structuredSelection.toList()) {
            if (obj instanceof PidcTreeNode) {
              PidcTreeNode node = (PidcTreeNode) obj;
              if (PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION.equals(node.getNodeType()) ||
                  PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION.equals(node.getNodeType())) {
                selectedObjs.add(node.getPidcVersion());
              }
            }
            else if (obj instanceof IProjectObject) {
              selectedObjs = structuredSelection.toList();
              break;
            }
          }
          if (CommonUtils.isNotEmpty(selectedObjs)) {
            errorMsg = addToExistingCompareEditor(selectedObjs);
          }
          else {
            errorMsg = addToExistingCompareEditor(structuredSelection.toList());
          }
        }
        if (!errorMsg.isEmpty()) {
          event.detail = DND.DROP_NONE;
        }
      }


    });

  }

  /**
   * @param event
   * @param selElement
   */
  private void performDropOfProjAttr(final DropTargetEvent event, final Object selElement) {
    CustomFilterGridLayer<CompareRowObject> customFilterGridLayer =
        (CustomFilterGridLayer<CompareRowObject>) ComparePIDCPage.this.customNatTable.getLayer();
    Point mappedRec = ComparePIDCPage.this.customNatTable.getDisplay().map(null, ComparePIDCPage.this.customNatTable,
        new Point(event.x, event.y));
    final int col = LayerUtil.convertColumnPosition(ComparePIDCPage.this.customNatTable,
        ComparePIDCPage.this.customNatTable.getColumnPositionByX(mappedRec.x),
        customFilterGridLayer.getDummyDataLayer());
    int row = LayerUtil.convertRowPosition(ComparePIDCPage.this.customNatTable,
        ComparePIDCPage.this.customNatTable.getRowPositionByY(mappedRec.y), customFilterGridLayer.getDummyDataLayer());
    Object rowObject = ComparePIDCPage.this.compPIDCFilterGridLayer.getBodyDataProvider().getRowObject(row);
    if (rowObject instanceof CompareRowObject) {
      final CompareRowObject compareRowObject = (CompareRowObject) rowObject;
      final String columnHeader = compareRowObject.getColumnDataMapper().getColumnIndexFlagMap().get(col);
      final IProjectAttribute targetPIDCAttribute =
          compareRowObject.getColumnDataMapper().getColumnIndexPIDCAttrMap().get(col);
      final IProjectAttribute sourcePIDCAttribute = (IProjectAttribute) selElement;
      // //
      String dragValidErrorMsg = isPIDCAttrDragValid(sourcePIDCAttribute);
      if (dragValidErrorMsg.isEmpty()) {
        /**
         * Opens the input entity in the appropriate editor
         */
        Display.getDefault().asyncExec(
            () -> validateAndPerformDrop(compareRowObject, columnHeader, targetPIDCAttribute, sourcePIDCAttribute));
      }
      else {
        CDMLogger.getInstance().errorDialog(dragValidErrorMsg, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param col
   * @param compareRowObject
   * @param columnHeader
   * @param targetPIDCAttribute
   * @param sourcePIDCAttribute
   */
  private void validateAndPerformDrop(final CompareRowObject compareRowObject, final String columnHeader,
      final IProjectAttribute targetPIDCAttribute, final IProjectAttribute sourcePIDCAttribute) {
    if ((columnHeader != null) && columnHeader.equals(ApicConstants.VALUE_TEXT) &&
        targetPIDCAttribute.getAttrId().equals(sourcePIDCAttribute.getAttrId())) {
      com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue = getAttrVal(sourcePIDCAttribute);

      final PIDCPageEditUtil pageEditUtil = new PIDCPageEditUtil(compareRowObject.getColumnDataMapper(),
          ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getCompareObjectsHandlerMap()
              .get(ComparePIDCPage.this.compareEditorUtil.getID(targetPIDCAttribute)));

      String partNumber = sourcePIDCAttribute.getPartNumber();
      String specLink = sourcePIDCAttribute.getSpecLink();
      String desc = sourcePIDCAttribute.getAdditionalInfoDesc();

      AbstractProjectAttributeBO projAttrHandler = ComparePIDCPage.this.compareEditorUtil.getProjectAttributeHandler(
          targetPIDCAttribute, ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler()
              .getCompareObjectsHandlerMap().get(ComparePIDCPage.this.compareEditorUtil.getID(targetPIDCAttribute)));

      String modErrorMsg = projAttrHandler.isModifiableWithError();
      String visErrorMsg = projAttrHandler.isVisible() ? "" : "Invisible attribute cannot be modified;";

      if (modErrorMsg.isEmpty() && visErrorMsg.isEmpty()) {
        if (checkForVerAttr(targetPIDCAttribute)) {
          // ICDM-2636
          // If the attribute value dragged is from a grouped attribute

          if (isGrpAttr(targetPIDCAttribute) && (getAllPidcGrpAttrVal(attributeValue))) {

            PIDCGrpdAttrChangesDialog dialog =
                new PIDCGrpdAttrChangesDialog(Display.getDefault().getActiveShell(), ComparePIDCPage.this,
                    attributeValue, targetPIDCAttribute, projAttrHandler.getProjectObjectBO().getPidcVersion(), true,
                    ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getPidcVersionBO()
                        .getAllAttrMap(),
                    new AttributeValueClientBO(attributeValue).getPreDefinedAttrValueSet(), null, null);


            dialog.open();
          }
          else {
            try {
              if (Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR))
                  .equals(targetPIDCAttribute.getAttrId()) &&
                  CommonActionSet.isQnaireConfigModifyErrorMessageShown(targetPIDCAttribute,
                      projAttrHandler.getProjectObjectBO())) {
                return;
              }
            }
            catch (ApicWebServiceException exp) {
              CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
            }
            pageEditUtil.editPIDCAttrValue(attributeValue, targetPIDCAttribute, partNumber, specLink, desc);
          }
        }

        else if (checkForVarAttr(targetPIDCAttribute)) {

          PidcVariant pidcVariant =
              ((PidcVariantBO) ((PidcVariantAttributeBO) projAttrHandler).getProjectObjectBO()).getPidcVariant();

          // If the attribute value dragged is from a grouped attribute
          if (projAttrHandler.getProjectObjectBO().getPidcDataHandler().getAttributeMap()
              .get(targetPIDCAttribute.getAttrId()).isGroupedAttr() &&
              (getAllPidcGrpAttrValVar(pidcVariant, attributeValue))) {
            PIDCGrpdAttrChangesDialog dialog =
                new PIDCGrpdAttrChangesDialog(Display.getDefault().getActiveShell(), ComparePIDCPage.this,
                    attributeValue, targetPIDCAttribute, projAttrHandler.getProjectObjectBO().getPidcVersion(), true,
                    ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getPidcVersionBO()
                        .getAllAttrMap(),
                    new AttributeValueClientBO(attributeValue).getPreDefinedAttrValueSet(), null, null);
            dialog.open();
          }
          else {
            pageEditUtil.editValue(attributeValue, targetPIDCAttribute, partNumber, specLink, desc);
          }
        }

        else if (targetPIDCAttribute instanceof PidcSubVariantAttribute) {

          PidcSubVariant pidcSubVariant =
              ((PidcSubVariantBO) ((PidcSubVariantAttributeBO) projAttrHandler).getProjectObjectBO())
                  .getPidcSubVariant();

          // If the attribute value dragged is from a grouped attribute
          if (projAttrHandler.getProjectObjectBO().getPidcDataHandler().getAttributeMap()
              .get(targetPIDCAttribute.getAttrId()).isGroupedAttr() &&
              (getAllPidcGrpAttrValSubVar(pidcSubVariant, attributeValue))) {

            PIDCGrpdAttrChangesDialog dialog =
                new PIDCGrpdAttrChangesDialog(Display.getDefault().getActiveShell(), ComparePIDCPage.this,
                    attributeValue, targetPIDCAttribute, projAttrHandler.getProjectObjectBO().getPidcVersion(), true,
                    ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getPidcVersionBO()
                        .getAllAttrMap(),
                    new AttributeValueClientBO(attributeValue).getPreDefinedAttrValueSet(), null, null);
            dialog.open();
          }
          else {
            pageEditUtil.editValue(attributeValue, targetPIDCAttribute, partNumber, specLink, desc);
          }
        }
        else {
          CDMLogger.getInstance().errorDialog("Invalid Drop: Cannot drop to attribute of this level",
              Activator.PLUGIN_ID);
        }
      }
      else {
        CDMLogger.getInstance().errorDialog("Invalid Drop: " + modErrorMsg + " " + visErrorMsg, Activator.PLUGIN_ID);
      }
    }
    else {
      CDMLogger.getInstance().errorDialog("Invalid Drop: Wrong Target", Activator.PLUGIN_ID);
    }
  }

  private boolean getAllPidcGrpAttrValSubVar(final PidcSubVariant pidcSubVariant,
      final com.bosch.caltool.icdm.model.apic.attr.AttributeValue attrValueSubVar) {
    Map<Long, PidcVersionAttribute> allPidcAttrMap =
        ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getPidcVersionBO().getAllAttrMap();

    Map<Long, PidcSubVariantAttribute> allSubVarAttrMap = ((PIDCCompareEditorInput) getEditorInput())
        .getComparePidcHandler().getCompareObjectsHandlerMap().get(pidcSubVariant.getId()).getAttributesAll();
    AttributeValueClientBO attrValClientBo = new AttributeValueClientBO(attrValueSubVar);

    Set<com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue> predefAttrValSet =
        attrValClientBo.getPreDefinedAttrValueSet();


    if (CommonUtils.isNotEmpty(predefAttrValSet)) {
      for (com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue predefAttrVal : predefAttrValSet) {

        PidcVersionAttribute predefAttrInPidc = allPidcAttrMap.get(predefAttrVal.getPredefinedAttrId());

        if (predefAttrInPidc.isAtChildLevel()) {
          PidcSubVariantAttribute predefAttrVar = allSubVarAttrMap.get(predefAttrVal.getPredefinedAttrId());
          if ((null != predefAttrVar) && (null != predefAttrVal.getPredefinedValueId())) {

            if (predefAttrVar.isAtChildLevel()) {
              return true;
            }
            AbstractProjectAttributeBO projAttrHandler =
                ComparePIDCPage.this.compareEditorUtil.getProjectAttributeHandler(predefAttrVar,
                    ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getCompareObjectsHandlerMap()
                        .get(ComparePIDCPage.this.compareEditorUtil.getID(predefAttrVar)));

            if (null != projAttrHandler.getDefaultValueDisplayName(false)) {
              if (!projAttrHandler.getDefaultValueDisplayName().equalsIgnoreCase(predefAttrVal.getPredefinedValue())) {
                return true;
              }
            }
            else {
              return true;
            }
          }
        }
        else {
          return true;
        }
      }
    }
    return false;
  }

  private boolean getAllPidcGrpAttrValVar(final PidcVariant pidcVariant,
      final com.bosch.caltool.icdm.model.apic.attr.AttributeValue attrValueVar) {

    Map<Long, PidcVersionAttribute> allPidcAttrMap =
        ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getPidcVersionBO().getAllAttrMap();

    Map<Long, PidcVariantAttribute> allVarAttrMap = ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler()
        .getCompareObjectsHandlerMap().get(pidcVariant.getId()).getAttributesAll();
    AttributeValueClientBO attrValClientBo = new AttributeValueClientBO(attrValueVar);

    Set<com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue> predefAttrValSet =
        attrValClientBo.getPreDefinedAttrValueSet();

    if (CommonUtils.isNotEmpty(predefAttrValSet)) {
      for (com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue predefAttrVal : predefAttrValSet) {

        PidcVersionAttribute predefAttrInPidc = allPidcAttrMap.get(predefAttrVal.getPredefinedAttrId());

        if (predefAttrInPidc.isAtChildLevel()) {
          PidcVariantAttribute predefAttrVar = allVarAttrMap.get(predefAttrVal.getPredefinedAttrId());
          if ((null != predefAttrVar) && (null != predefAttrVal.getPredefinedValueId())) {

            if (predefAttrVar.isAtChildLevel()) {
              return true;
            }
            AbstractProjectAttributeBO projAttrHandler =
                ComparePIDCPage.this.compareEditorUtil.getProjectAttributeHandler(predefAttrVar,
                    ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getCompareObjectsHandlerMap()
                        .get(ComparePIDCPage.this.compareEditorUtil.getID(predefAttrVar)));

            if (null != projAttrHandler.getDefaultValueDisplayName(false)) {
              if (!projAttrHandler.getDefaultValueDisplayName().equalsIgnoreCase(predefAttrVal.getPredefinedValue())) {
                return true;
              }
            }
            else {
              return true;
            }
          }
        }
        else {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @param sourcePIDCAttribute
   */
  private String isPIDCAttrDragValid(final IProjectAttribute sourcePIDCAttribute) {
    String errorMsg;
    // Check for PIDC Attribute and not a variant
    // Check for PIDC Variant Attribute and not a sub-variant
    if (checkForVerAttr(sourcePIDCAttribute) || checkForVarAttr(sourcePIDCAttribute) ||
        (sourcePIDCAttribute instanceof PidcSubVariantAttribute)) {
      errorMsg = "";
    }
    else {
      errorMsg = "Cannot drag attribute from this level";
    }
    if (errorMsg.isEmpty() && (sourcePIDCAttribute.getValue() == null)) {
      errorMsg = "Empty attribute value cannot be dragged";
    }
    return errorMsg;
  }

  /**
   * @param sourcePIDCAttribute
   * @return
   */
  private AttributeValue getAttrVal(final IProjectAttribute sourcePIDCAttribute) {
    return ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getCompareObjectsHandlerMap()
        .get(ComparePIDCPage.this.compareEditorUtil.getID(sourcePIDCAttribute)).getPidcDataHandler()
        .getAttributeValueMap().get(sourcePIDCAttribute.getValueId());
  }

  /**
   * @param targetPIDCAttribute
   * @return
   */
  private boolean isGrpAttr(final IProjectAttribute targetPIDCAttribute) {
    return ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getPidcVersionBO().getPidcDataHandler()
        .getAttributeMap().get(targetPIDCAttribute.getAttrId()).isGroupedAttr();
  }

  /**
   * @param targetPIDCAttribute
   * @param attributeValue
   * @param projAttrHandler
   */
  private void openGrpdAttrChangesDialog(final IProjectAttribute targetPIDCAttribute,
      final com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue,
      final AbstractProjectAttributeBO projAttrHandler) {
    // Passing all attribute collection of target pidc as paste will be performed at target pidc
    PIDCGrpdAttrChangesDialog dialog =
        new PIDCGrpdAttrChangesDialog(Display.getDefault().getActiveShell(), ComparePIDCPage.this, attributeValue,
            targetPIDCAttribute, projAttrHandler.getProjectObjectBO().getPidcVersion(), true,
            ((PidcVersionBO) projAttrHandler.getProjectObjectBO()).getAllAttrMap(),
            new AttributeValueClientBO(attributeValue).getPreDefinedAttrValueSet(), null, null);


    dialog.open();
  }

  /**
   * @param sourcePIDCAttribute
   * @return
   */
  private boolean checkForVarAttr(final IProjectAttribute sourcePIDCAttribute) {
    return (sourcePIDCAttribute instanceof PidcVariantAttribute) && !sourcePIDCAttribute.isAtChildLevel();
  }

  /**
   * @param sourcePIDCAttribute
   * @return
   */
  private boolean checkForVerAttr(final IProjectAttribute sourcePIDCAttribute) {
    return (sourcePIDCAttribute instanceof PidcVersionAttribute) && !sourcePIDCAttribute.isAtChildLevel();
  }

  /**
   * @param selectedElement Object
   * @return IProjectObject
   */
  protected IProjectObject getDroppedObj(final Object selectedElement) {
    IProjectObject compareObj = null;
    if (selectedElement instanceof ScratchPadDataFetcher) {
      ScratchPadDataFetcher data = (ScratchPadDataFetcher) selectedElement;
      if (null != data.getPidcVersion()) {
        compareObj = data.getPidcVersion();
      }
      else if (null != data.getPidcVariant()) {
        compareObj = data.getPidcVariant();
      }
      else if (null != data.getPidcSubVariant()) {
        compareObj = data.getPidcSubVariant();
      }
    }
    else if (selectedElement instanceof IProjectObject) {
      compareObj = (IProjectObject) selectedElement;
    }
    else if ((selectedElement instanceof PIDCDetailsNode) && ((PIDCDetailsNode) selectedElement).isVariantNode()) {
      compareObj = ((PIDCDetailsNode) selectedElement).getPidcVariant();
    }
    return compareObj;
  }

  /**
   * Adds the objects to the compare page in compare editor
   *
   * @param newObjsToComp pidcVersions to add
   */
  public void addToCompareEditor(final List<IProjectObject> newObjsToComp) {
    Set<IProjectObject> commonPidcVersions = new HashSet<>(this.compareObjs);
    commonPidcVersions.retainAll(newObjsToComp);
    if (!commonPidcVersions.isEmpty()) {
      StringBuilder commonPIDCNames = new StringBuilder(PIDCNAMES_INTIAL_SIZE);
      for (IProjectObject pidcVersion : commonPidcVersions) {
        commonPIDCNames.append("\n").append(pidcVersion.getName());
      }
      MessageDialogUtils.getInfoMessageDialog(CANNOT_ADD_TO_COMPARE_EDITOR,
          "The following is already present in compare editor :" + commonPIDCNames);
      newObjsToComp.removeAll(commonPidcVersions);
    }
    if (!newObjsToComp.isEmpty()) {
      this.compareObjs.addAll(newObjsToComp);
      // add data handler for new objects
      addToDataHandler(newObjsToComp);
      reconstructNatTable();
      this.customNatTable.doCommand(new StructuralRefreshCommand());
    }
  }


  /**
   * Add data handlers for new project objects
   *
   * @param newCompareObjs list of project object
   */
  public void addToDataHandler(final List<IProjectObject> newCompareObjs) {

    for (IProjectObject iProjectObject : newCompareObjs) {
      PidcDataHandler dataHandler = new PidcDataHandler();
      if ((iProjectObject instanceof PidcVersion) && (((PIDCCompareEditorInput) getEditorInput())
          .getComparePidcHandler().getCompareObjectsHandlerMap().get(iProjectObject.getId()) == null)) {
        PidcDetailsLoader loader = new PidcDetailsLoader(dataHandler);
        dataHandler = loader.loadDataModel(((PidcVersion) iProjectObject).getId());
        ProjectHandlerInit handlerInit = new ProjectHandlerInit((PidcVersion) iProjectObject,
            (PidcVersion) iProjectObject, dataHandler, ApicConstants.LEVEL_PIDC_VERSION);
        ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getCompareObjectsHandlerMap()
            .put(((PidcVersion) iProjectObject).getId(), handlerInit.getProjectObjectBO());
      }
      else if ((iProjectObject instanceof PidcVariant) && (((PIDCCompareEditorInput) getEditorInput())
          .getComparePidcHandler().getCompareObjectsHandlerMap().get(((PidcVariant) iProjectObject).getId()) == null)) {
        ProjectHandlerInit handlerInit = new ProjectHandlerInit(
            ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getPidcVersionBO().getPidcDataHandler()
                .getPidcVersionInfo().getPidcVersion(),
            iProjectObject,
            ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getPidcVersionBO().getPidcDataHandler(),
            ApicConstants.LEVEL_PIDC_VARIANT);
        ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getCompareObjectsHandlerMap()
            .put(((PidcVariant) iProjectObject).getId(), handlerInit.getProjectObjectBO());
      }
      else if ((iProjectObject instanceof PidcSubVariant) &&
          (((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getCompareObjectsHandlerMap()
              .get(((PidcSubVariant) iProjectObject).getId()) == null)) {

        ProjectHandlerInit handlerInit = new ProjectHandlerInit(
            ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getPidcVersionBO().getPidcDataHandler()
                .getPidcVersionInfo().getPidcVersion(),
            iProjectObject,
            ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getPidcVersionBO().getPidcDataHandler(),
            ApicConstants.LEVEL_PIDC_SUB_VARIANT);
        ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getCompareObjectsHandlerMap()
            .put(((PidcSubVariant) iProjectObject).getId(), handlerInit.getProjectObjectBO());
      }
    }
  }


  /**
   * Removes the objects from compare editor
   *
   * @param compObj object to be removed
   */
  public void removeFromCompareEditor(final IProjectObject compObj) {
    // TODO:Check if multiple pidcversions can be removed via toolbar option since multiple group selection is not
    // possible
    if (this.compareObjs.contains(compObj)) {
      if (this.compareObjs.size() > 1) {
        this.compareObjs.remove(compObj);
        reconstructNatTable();
        this.customNatTable.doCommand(new StructuralRefreshCommand());
      }
      else {
        MessageDialogUtils.getInfoMessageDialog(CANNOT_ADD_TO_COMPARE_EDITOR, "Compare editor cannot be empty");
      }
    }
  }


  /**
   *
   */
  private void reconstructNatTable() {
    this.customNatTable.dispose();
    this.toolBarManager.dispose();
    this.propertyToLabelMap.clear();
    this.compPIDCFilterGridLayer = null;
    // TODO:The reconstruction of NatTable needs to be optimised
    createNatTable();
    createToolBarAction();
    this.totTableRowCount = this.compPIDCFilterGridLayer.getRowHeaderLayer().getPreferredRowCount();
    // update the new collection in editor input class
    (this.editor.getEditorInput()).setCompareObjs(this.compareObjs);
    if (this.compareObjs.get(0) instanceof PidcVersion) {
      final PIDCDetailsViewPart viewPart = (PIDCDetailsViewPart) WorkbenchUtils.getView(PIDCDetailsViewPart.VIEW_ID);
      if ((null != viewPart) && (viewPart.getCurrentPage() instanceof PIDCCompareDetailsPg)) {
        PIDCCompareDetailsPg compareDetailsPg = (PIDCCompareDetailsPg) viewPart.getCurrentPage();
        compareDetailsPg.setEditorPidcVer((PidcVersion) this.compareObjs.get(0));
        compareDetailsPg.refreshPage();
      }
    }
    this.editor.firePropChange(PROP_TITLE);
    // First the form's body is repacked and then the section is repacked
    // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable
    this.form.getBody().pack();
    this.section.layout();
    setStatusBarMessage(false);
    executeFilters();
  }

  private void executeFilters() {
    Display.getDefault().syncExec(ComparePIDCPage.this::executeFilterAction);
  }

  /**
   *
   */
  private void executeFilterAction() {
    // run invisible attributes filter action if toggled
    if (ComparePIDCPage.this.toolBarFilterStateMap
        .get(ComparePIDCPage.this.toolBarActionSet.getInvisibleAttrAction().getText()) != null) {
      ComparePIDCPage.this.toolBarActionSet.getInvisibleAttrAction()
          .setChecked(ComparePIDCPage.this.toolBarFilterStateMap
              .get(ComparePIDCPage.this.toolBarActionSet.getInvisibleAttrAction().getText()));
      ComparePIDCPage.this.toolBarActionSet.getInvisibleAttrAction().run();
    }

    // run mandatory attributes filter action if toggled
    if (ComparePIDCPage.this.toolBarFilterStateMap
        .get(ComparePIDCPage.this.toolBarActionSet.getAttrMandtoryAction().getText()) != null) {
      ComparePIDCPage.this.toolBarActionSet.getAttrMandtoryAction()
          .setChecked(ComparePIDCPage.this.toolBarFilterStateMap
              .get(ComparePIDCPage.this.toolBarActionSet.getAttrMandtoryAction().getText()));
      ComparePIDCPage.this.toolBarActionSet.getAttrMandtoryAction().run();
    }

    // run non mandatory attributes filter action if toggled
    if (ComparePIDCPage.this.toolBarFilterStateMap
        .get(ComparePIDCPage.this.toolBarActionSet.getAttrNonMandtoryAction().getText()) != null) {
      ComparePIDCPage.this.toolBarActionSet.getAttrNonMandtoryAction()
          .setChecked(ComparePIDCPage.this.toolBarFilterStateMap
              .get(ComparePIDCPage.this.toolBarActionSet.getAttrNonMandtoryAction().getText()));
      ComparePIDCPage.this.toolBarActionSet.getAttrNonMandtoryAction().run();
    }


    // run dependent attributes filter action if toggled
    if (ComparePIDCPage.this.toolBarFilterStateMap
        .get(ComparePIDCPage.this.toolBarActionSet.getAttrDepAction().getText()) != null) {
      ComparePIDCPage.this.toolBarActionSet.getAttrDepAction().setChecked(ComparePIDCPage.this.toolBarFilterStateMap
          .get(ComparePIDCPage.this.toolBarActionSet.getAttrDepAction().getText()));
      ComparePIDCPage.this.toolBarActionSet.getAttrDepAction().run();
    }

    // run non dependent attributes filter action if toggled
    if (ComparePIDCPage.this.toolBarFilterStateMap
        .get(ComparePIDCPage.this.toolBarActionSet.getAttrNonDepAction().getText()) != null) {
      ComparePIDCPage.this.toolBarActionSet.getAttrNonDepAction().setChecked(ComparePIDCPage.this.toolBarFilterStateMap
          .get(ComparePIDCPage.this.toolBarActionSet.getAttrNonDepAction().getText()));
      ComparePIDCPage.this.toolBarActionSet.getAttrNonDepAction().run();
    }

    // run different filter action if toggled
    if (ComparePIDCPage.this.toolBarFilterStateMap
        .get(ComparePIDCPage.this.toolBarActionSet.getAttrDiffAction().getText()) != null) {
      ComparePIDCPage.this.toolBarActionSet.getAttrDiffAction().setChecked(ComparePIDCPage.this.toolBarFilterStateMap
          .get(ComparePIDCPage.this.toolBarActionSet.getAttrDiffAction().getText()));
      ComparePIDCPage.this.toolBarActionSet.getAttrDiffAction().run();
    }

    // run not different filter action if toggled
    if (ComparePIDCPage.this.toolBarFilterStateMap
        .get(ComparePIDCPage.this.toolBarActionSet.getAttrNotDiffAction().getText()) != null) {
      ComparePIDCPage.this.toolBarActionSet.getAttrNotDiffAction().setChecked(ComparePIDCPage.this.toolBarFilterStateMap
          .get(ComparePIDCPage.this.toolBarActionSet.getAttrNotDiffAction().getText()));
      ComparePIDCPage.this.toolBarActionSet.getAttrNotDiffAction().run();
    }
  }


  @Override
  public void refreshUI(final com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent dce) {
    refreshTable();
  }

  /**
   *
   */
  private void refreshTable() {
    this.compPIDCFilterGridLayer.getEventList().clear();
    fillNattableRowObjects();
    this.compPIDCFilterGridLayer.getEventList().addAll(this.compareRowObjects);
    if (this.customNatTable != null) {
      this.customNatTable.doCommand(new StructuralRefreshCommand());
      this.customNatTable.doCommand(new VisualRefreshCommand());
      this.customNatTable.refresh();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler();
  }

  /**
   *
   */
  private final class MouseEventListener implements MouseListener {

    @Override
    public void mouseUp(final MouseEvent mouseevent) {
      // NA

    }

    @Override
    public void mouseDown(final MouseEvent mouseevent) {
      CustomFilterGridLayer<?> customFilterGridLayer =
          (CustomFilterGridLayer<?>) ComparePIDCPage.this.customNatTable.getLayer();
      final SelectionLayer selectionLayer = customFilterGridLayer.getBodyLayer().getSelectionLayer();
      ComparePIDCPage.this.selectedRowPostn = LayerUtil.convertRowPosition(ComparePIDCPage.this.customNatTable,
          ComparePIDCPage.this.customNatTable.getRowPositionByY(mouseevent.y), selectionLayer);
      ComparePIDCPage.this.selectedColPostn = LayerUtil.convertColumnPosition(ComparePIDCPage.this.customNatTable,
          ComparePIDCPage.this.customNatTable.getColumnPositionByX(mouseevent.x),
          customFilterGridLayer.getDummyDataLayer());
    }

    @Override
    public void mouseDoubleClick(final MouseEvent mouseevent) {

      CustomFilterGridLayer<?> customFilterGridLayer =
          (CustomFilterGridLayer<?>) ComparePIDCPage.this.customNatTable.getLayer();
      final SelectionLayer selectionLayer = customFilterGridLayer.getBodyLayer().getSelectionLayer();
      int rowPosition = LayerUtil.convertRowPosition(ComparePIDCPage.this.customNatTable,
          ComparePIDCPage.this.customNatTable.getRowPositionByY(mouseevent.y), selectionLayer);
      int columnPosition = LayerUtil.convertColumnPosition(ComparePIDCPage.this.customNatTable,
          ComparePIDCPage.this.customNatTable.getColumnPositionByX(mouseevent.x),
          customFilterGridLayer.getDummyDataLayer());
      Object rowObject = ComparePIDCPage.this.compPIDCFilterGridLayer.getBodyDataProvider().getRowObject(rowPosition);
      if (rowObject instanceof CompareRowObject) {
        CompareRowObject compareRowObject = (CompareRowObject) rowObject;
        ColumnDataMapper columnDataMapper = compareRowObject.getColumnDataMapper();
        String columnType = columnDataMapper.getColumnIndexFlagMap().get(columnPosition);
        if ((columnType != null) && ApicConstants.VALUE_TEXT.equals(columnType)) {
          IProjectAttribute pidcAttribute = columnDataMapper.getColumnIndexPIDCAttrMap().get(columnPosition);
          AbstractProjectObjectBO projObjBO = ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler()
              .getCompareObjectsHandlerMap().get(ComparePIDCPage.this.compareEditorUtil.getID(pidcAttribute));

          AbstractProjectAttributeBO projectAttrHandler =
              ComparePIDCPage.this.compareEditorUtil.getProjectAttributeHandler(pidcAttribute, projObjBO);

          boolean isPverNameValidToModify;
          PIDCActionSet actionSet = new PIDCActionSet();
          // check if sdom pver name is set and valid
          isPverNameValidToModify = actionSet.isPverNameEditable(pidcAttribute, projObjBO);

          if (isPverNameValidToModify) {
            validateNEdit(columnDataMapper, pidcAttribute, projObjBO, projectAttrHandler);
          }
        }
      }
    }

    /**
     * @param columnDataMapper
     * @param pidcAttribute
     * @param projObjBO
     * @param projectAttrHandler
     */
    private void validateNEdit(final ColumnDataMapper columnDataMapper, final IProjectAttribute pidcAttribute,
        final AbstractProjectObjectBO projObjBO, final AbstractProjectAttributeBO projectAttrHandler) {
      // check if pidc is unlocked in session

      checkforPIDCSessionLock(projObjBO.getPidcVersion());
      // validation for vcdm aprj attribute

      if (projObjBO.getPidcDataHandler().getAttributeMap().get(pidcAttribute.getAttrId())
          .getLevel() == ApicConstants.VCDM_APRJ_NAME_ATTR) {
        PIDCPageEditUtil pidcPageEditUtil = new PIDCPageEditUtil(projObjBO);
        String aprjValue = pidcAttribute.getValue();

        if (pidcPageEditUtil.findAPRJID(aprjValue) != null) {
          CDMLogger.getInstance().warnDialog(
              "APRJ attribute cannot be modified, since vCDM transfer has already occured with another APRJ. Please contact iCDM Hotline for support.",
              Activator.PLUGIN_ID);
          return;
        }
      }
      Map<IProjectAttribute, IProjectAttribute> predefGrpAttrMap = getPredefAttrMap(projObjBO);

      if (!predefGrpAttrMap.containsKey(pidcAttribute)) {
        editAttrOnDblClk(columnDataMapper, pidcAttribute, projObjBO, projectAttrHandler);
      }
      else {
        CDMLogger.getInstance().infoDialog("Predefined Attribute can not be edited", Activator.PLUGIN_ID);
      }
    }

    /**
     * @param projObjBO
     * @return
     */
    private Map<IProjectAttribute, IProjectAttribute> getPredefAttrMap(final AbstractProjectObjectBO projObjBO) {
      Map<IProjectAttribute, IProjectAttribute> predefGrpAttrMap;
      if (!((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().isVersionCompare()) {
        predefGrpAttrMap = ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getPidcVersionBO()
            .getPredefAttrGrpAttrMap();
      }
      else {
        predefGrpAttrMap = ((PidcVersionBO) projObjBO).getPredefAttrGrpAttrMap();
      }
      return predefGrpAttrMap;
    }

    /**
     * @param columnDataMapper
     * @param pidcAttribute
     * @param projObjBO
     * @param projectAttrHandler
     */
    private void editAttrOnDblClk(final ColumnDataMapper columnDataMapper, final IProjectAttribute pidcAttribute,
        final AbstractProjectObjectBO projObjBO, final AbstractProjectAttributeBO projectAttrHandler) {
      if ((pidcAttribute instanceof PidcVersionAttribute) && !pidcAttribute.isAtChildLevel() &&

          projectAttrHandler.isModifiable() && projectAttrHandler.isVisible()) {

        // ICDM-2493
        boolean error = true;
        try {
          error = CommonActionSet.isQnaireConfigModifyErrorMessageShown(pidcAttribute, projObjBO);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
        }
        if (error) {
          return;
        }

        final PIDCAttrValueEditDialog dialog = new PIDCAttrValueEditDialog(Display.getCurrent().getActiveShell(),
            ComparePIDCPage.this, pidcAttribute, columnDataMapper, true, null, projObjBO);
        dialog.open();
        ComparePIDCPage.this.toolBarActionSet.getAttrDiffAction().run();
        ComparePIDCPage.this.toolBarActionSet.getAttrNotDiffAction().run();
      }

      else if ((pidcAttribute instanceof PidcVariantAttribute) && !pidcAttribute.isAtChildLevel() &&
          projectAttrHandler.isModifiable() && projectAttrHandler.isVisible()) {

        final PIDCAttrValueEditDialog dialog =
            new PIDCAttrValueEditDialog(Display.getCurrent().getActiveShell(), ComparePIDCPage.this, pidcAttribute,
                columnDataMapper, true, (PidcVariant) ComparePIDCPage.this.compareObjs.get(0), projObjBO);
        dialog.open();
        ComparePIDCPage.this.toolBarActionSet.getAttrDiffAction().run();
        ComparePIDCPage.this.toolBarActionSet.getAttrNotDiffAction().run();
      }

      else if ((pidcAttribute instanceof PidcSubVariantAttribute) && projectAttrHandler.isModifiable() &&
          projectAttrHandler.isVisible()) {
        final PIDCAttrValueEditDialog dialog = new PIDCAttrValueEditDialog(Display.getCurrent().getActiveShell(),
            ComparePIDCPage.this, pidcAttribute, columnDataMapper, true, null, projObjBO);
        dialog.open();
        ComparePIDCPage.this.toolBarActionSet.getAttrDiffAction().run();
        ComparePIDCPage.this.toolBarActionSet.getAttrNotDiffAction().run();
      }
      else {
        CDMLogger.getInstance().errorDialog(CANNOT_EDIT_ERR_MSG, Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * @param pidcAttribute
   */
  private void checkforPIDCSessionLock(final PidcVersion pidcVersion) {
    CurrentUserBO currUser = new CurrentUserBO();
    ApicDataBO apicBo = new ApicDataBO();
    apicBo.isPidcUnlockedInSession(pidcVersion);
    try {
      if (!apicBo.isPidcUnlockedInSession(pidcVersion) && currUser.hasNodeWriteAccess(pidcVersion.getPidcId())) {
        final PIDCActionSet pidcActionSet = new PIDCActionSet();
        pidcActionSet.showUnlockPidcDialog(pidcVersion);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   *
   */
  private void groupColumns() {
    boolean usedGrp = true;
    // First level grouping
    int columnGroupCounter = 0;
    int[] usedFlgColumns = new int[4];
    List<Integer> usedFlgColumnsList = new ArrayList<>(4);
    int[] addInfoColumns = new int[6];
    List<Integer> addInfoColumnsList = new ArrayList<>(6);
    ColumnGroupModel columnGroupModel = this.compPIDCFilterGridLayer.getColumnGroupModel();
    int colGrpNameCounter = 0;
    // First level grouping
    for (int colIndex = 4; colIndex < (this.propertyToLabelMap.size()); colIndex++) {
      if (usedGrp) {
        usedFlgColumns[columnGroupCounter] = colIndex;
        usedFlgColumnsList.add(colIndex);
        ++columnGroupCounter;
        if (columnGroupCounter == 4) {
          String colName = getSpacedGroupName(colGrpNameCounter, new StringBuilder("Used"));
          // Add group
          columnGroupModel.addColumnsIndexesToGroup(colName/* "Used " + colIndex */, usedFlgColumns);

          columnGroupModel.setColumnGroupCollapseable(usedFlgColumns[0], true);
          columnGroupModel.setStaticColumnIndexesByGroup(colName,
              new int[] { usedFlgColumns[usedFlgColumns.length - 1] });
          this.compPIDCFilterGridLayer.getColumnGroupHeaderLayer().setGroupAsCollapsed(usedFlgColumns[0]);
          // reset
          columnGroupCounter = 0;
          usedFlgColumnsList.clear();
          usedGrp = false;
          colIndex++;
          colGrpNameCounter++;
        }
      }

      else {
        addInfoColumns[columnGroupCounter] = colIndex;
        addInfoColumnsList.add(colIndex);
        ++columnGroupCounter;
        if (columnGroupCounter == 6) {
          String colName = getSpacedGroupName(colGrpNameCounter, new StringBuilder("Additional Info"));
          columnGroupModel.addColumnsIndexesToGroup(colName, addInfoColumns);
          columnGroupModel.setColumnGroupCollapseable(addInfoColumns[0], true);
          columnGroupModel.setStaticColumnIndexesByGroup(colName,
              new int[] { addInfoColumns[addInfoColumns.length - 1] });
          this.compPIDCFilterGridLayer.getColumnGroupHeaderLayer().setGroupAsCollapsed(addInfoColumns[0]);
          // reset
          columnGroupCounter = 0;
          addInfoColumnsList.clear();
          usedGrp = true;
        }
      }

    }

    // second level grouping
    columnGroupCounter = 0;
    int[] groupGroupSelectedColumns = new int[11];
    List<Integer> groupGroupSelectedColumnsList = new ArrayList<>(11);
    colGrpNameCounter = 0;
    ColumnGroupModel columnGroupGroupModel = this.compPIDCFilterGridLayer.getColumnGroupGroupModel();
    for (int colIndex = 4; colIndex < (this.propertyToLabelMap.size()); colIndex++) {
      groupGroupSelectedColumns[columnGroupCounter] = colIndex;
      groupGroupSelectedColumnsList.add(colIndex);
      ++columnGroupCounter;
      if (columnGroupCounter == 11) {
        // Add group
        columnGroupGroupModel.addColumnsIndexesToGroup(this.compareObjs.get(colGrpNameCounter).getName(),
            groupGroupSelectedColumns);
        columnGroupGroupModel.setColumnGroupCollapseable(groupGroupSelectedColumns[0], true);
        // reset
        columnGroupCounter = 0;
        groupGroupSelectedColumnsList.clear();
        colGrpNameCounter++;
      }
    }
  }

  /**
   * @param colGrpNameCounter
   * @return
   */
  private String getSpacedGroupName(final int colGrpNameCounter, final StringBuilder groupName) {
    int temp = colGrpNameCounter;
    while (temp > 0) {
      groupName.append(" ");
      temp--;
    }
    return groupName.toString();
  }


  /**
   * Enables tootltip only for cells which contain not fully visible content
   *
   * @param natTable
   */
  private void attachToolTip(final NatTable natTable) {
    // Icdm-1208- Custom tool tip for Nat table.
    DefaultToolTip toolTip = new ComparePIDCNatToolTip(natTable, new String[0],
        ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler());
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(XCOORDINATE_TEN, YCOORDINATE_TEN));
  }

  private void registerConfigLabelsOnColumns(final ColumnOverrideLabelAccumulator columnLabelAccumulator) {
    // For Diff column
    columnLabelAccumulator.registerColumnOverrides(3, CHECK_BOX_EDITOR_CNG_LBL + "_3", CHECK_BOX_CONFIG_LABEL + "_3");
    // For Used flag columns
    int counter = 0;
    for (int i = 4; i < (this.propertyToLabelMap.size()); i++) {
      counter++;
      columnLabelAccumulator.registerColumnOverrides(i, CHECK_BOX_EDITOR_CNG_LBL + "_" + i,
          CHECK_BOX_CONFIG_LABEL + "_" + i);
      if (FIXED_COL_COUNT == counter) {
        counter = 0;
        i = i + 8;
      }

    }
  }

  private void registerCheckBoxEditor(final IConfigRegistry configRegistry) {
    // For Diff column
    registerDiffCheckBox(configRegistry);

    // For Used flag columns
    int counter = 0;
    for (int i = 4; i < (this.propertyToLabelMap.size()); i++) {
      counter++;
      Style cellStyleUC = new Style();
      cellStyleUC.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
      configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleUC, NORMAL,
          CHECK_BOX_CONFIG_LABEL + "_" + i);
      configRegistry.registerConfigAttribute(CELL_PAINTER, new CheckBoxPainter(), NORMAL,
          CHECK_BOX_CONFIG_LABEL + "_" + i);
      configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
          new DefaultBooleanDisplayConverter(), NORMAL, CHECK_BOX_CONFIG_LABEL + "_" + i);
      final Integer columnIndex = i;
      PIDCCompareNatTableCheckBoxCellEditor checkBoxCellEditor = new PIDCCompareNatTableCheckBoxCellEditor() {

        /**
         * {@inheritDoc}
         */
        @Override
        protected Control activateCell(final Composite parentComp, final Object originalCanonicalValue) {
          IStructuredSelection selection = (IStructuredSelection) ComparePIDCPage.this.selectionProvider.getSelection();
          final CompareRowObject compareRowObject = (CompareRowObject) selection.getFirstElement();
          final ColumnDataMapper columnDataMapper = compareRowObject.getColumnDataMapper();
          final IProjectAttribute ipidcAttribute = columnDataMapper.getColumnIndexPIDCAttrMap().get(columnIndex);

          AbstractProjectObjectBO projObjBO = ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler()
              .getCompareObjectsHandlerMap().get(ComparePIDCPage.this.compareEditorUtil.getID(ipidcAttribute));
          checkforPIDCSessionLock(projObjBO.getPidcVersion());


          AbstractProjectAttributeBO projAttrHandler =
              ComparePIDCPage.this.compareEditorUtil.getProjectAttributeHandler(ipidcAttribute, projObjBO);

          if (!projAttrHandler.isModifiable() || !projAttrHandler.isVisible()) {
            CDMLogger.getInstance().errorDialog(CANNOT_EDIT_ERR_MSG, Activator.PLUGIN_ID);
            return super.createEditorControlWithoutInversion(parentComp);
          }

          Control control = super.activateCell(parentComp, originalCanonicalValue);
          Runnable busyRunnable = () -> callCheckBoxEditor(columnIndex, columnDataMapper,
              columnDataMapper.getColumnIndexFlagMap().get(columnIndex), ipidcAttribute,
              new PIDCPageEditUtil(columnDataMapper, projObjBO));

          BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), busyRunnable);
          ComparePIDCPage.this.toolBarActionSet.getAttrDiffAction().run();
          ComparePIDCPage.this.toolBarActionSet.getAttrNotDiffAction().run();
          return control;
        }

      };
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, checkBoxCellEditor, NORMAL,
          CHECK_BOX_EDITOR_CNG_LBL + "_" + i);
      if (FIXED_COL_COUNT == counter) {
        counter = 0;
        i = i + 8;
      }
    }

  }


  private void registerEditableRules(final IConfigRegistry configRegistry) {
    // For Diff column
    ((ColumnOverrideLabelAccumulator) this.compPIDCFilterGridLayer.getColumnHeaderDataLayer()
        .getConfigLabelAccumulator()).registerColumnOverrides(3, CUSTOM_COMPARATOR_LABEL + "3");
    configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getCheckBoxColumnComparator(3), NORMAL,
        CUSTOM_COMPARATOR_LABEL + "3");

    // For used flag columns
    int counter = 0;
    for (int i = 4; i < (this.propertyToLabelMap.size()); i++) {
      counter++;
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
          DisplayMode.EDIT, CHECK_BOX_CONFIG_LABEL + "_" + i);
      ((ColumnOverrideLabelAccumulator) this.compPIDCFilterGridLayer.getColumnHeaderDataLayer()
          .getConfigLabelAccumulator()).registerColumnOverrides(i, CUSTOM_COMPARATOR_LABEL + i);
      configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getCheckBoxColumnComparator(i),
          NORMAL, CUSTOM_COMPARATOR_LABEL + i);
      if (FIXED_COL_COUNT == counter) {
        counter = 0;
        i = i + 8;
      }
    }
  }

  /**
   * @param configRegistry
   */
  private void registerDiffCheckBox(final IConfigRegistry configRegistry) {
    Style cellStyleAny = new Style();
    cellStyleAny.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    cellStyleAny.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_LIGHT_SHADOW);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleAny, NORMAL,
        CHECK_BOX_CONFIG_LABEL + "_3");

    configRegistry.registerConfigAttribute(CELL_PAINTER, new CheckBoxPainter(), NORMAL, CHECK_BOX_CONFIG_LABEL + "_3");
    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultBooleanDisplayConverter(),
        NORMAL, CHECK_BOX_CONFIG_LABEL + "_3");
    PIDCCompareNatTableCheckBoxCellEditor checkBoxCellEditorDiff = new PIDCCompareNatTableCheckBoxCellEditor();
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, checkBoxCellEditorDiff, NORMAL,
        CHECK_BOX_EDITOR_CNG_LBL + "_3");

  }


  /**
   * @param i
   * @return
   */
  private static Comparator<CompareRowObject> getCheckBoxColumnComparator(final int columnIndex) {
    return (compareRowObject1, compareRowObject2) -> {

      if (columnIndex == 3) {
        // TODO Try using flag for comparison
        return compareRowObject1.getDiff().compareTo(compareRowObject2.getDiff());
      }

      Boolean isPIDCAttr1Checked = (Boolean) compareRowObject1.getColumnDataMapper().getColumnData(columnIndex);
      Boolean isPIDCAttr2Checked = (Boolean) compareRowObject2.getColumnDataMapper().getColumnData(columnIndex);

      int ret = isPIDCAttr1Checked.compareTo(isPIDCAttr2Checked);
      if (ret == 0) {
        ret = compareRowObject1.getAttribute().compareTo(compareRowObject2.getAttribute(), ApicConstants.SORT_ATTRNAME);
      }
      return ret;

    };

  }


  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(modifyEvent -> {
      final String text = ComparePIDCPage.this.filterTxt.getText().trim();
      ComparePIDCPage.this.allColumnFilterMatcher.setFilterText(text, true);
      ComparePIDCPage.this.compPIDCFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
      ComparePIDCPage.this.compPIDCFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(ComparePIDCPage.this.compPIDCFilterGridLayer.getSortableColumnHeaderLayer()));
      setStatusBarMessage(false);

    });
  }


  /**
   *
   *
   */
  public class CompareNatInputToColumnConverter extends AbstractNatInputToColumnConverter {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getColumnValue(final Object evaluateObj, final int colIndex) {
      Object result = null;
      if (evaluateObj instanceof CompareRowObject) {
        result = getAttributeData((CompareRowObject) evaluateObj, colIndex);
      }
      return result;
    }

    /**
     * @param evaluateObj
     * @param colIndex
     * @return
     */
    private Object getAttributeData(final CompareRowObject compareRowObject, final int colIndex) {
      Object result = null;
      switch (colIndex) {
        case 0:
          break;
        case 1:
          result = compareRowObject.getAttribute().getName();
          break;
        case 2:
          result = compareRowObject.getAttribute().getDescription();
          break;
        case 3:
          result = compareRowObject.getComputedIsDiff();
          break;
        default:
          result = compareRowObject.getColumnDataMapper().getColumnData(colIndex);
          break;
      }
      return result;
    }
  }

  class CustomPIDCCompareHeaderDataProvider implements IDataProvider {


    /**
     * @param columnIndex int
     * @return String
     */
    public String getColumnHeaderLabel(final int columnIndex) {
      String columnHeaderLabel = ComparePIDCPage.this.propertyToLabelMap.get(columnIndex);

      return columnHeaderLabel == null ? "" : columnHeaderLabel;
    }

    @Override
    public int getColumnCount() {
      return ComparePIDCPage.this.propertyToLabelMap.size();
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


  class CustomPIDCCompareColumnPropertyAccessor<T> implements IColumnAccessor<T> {

    /**
     * This method has been overridden so that it returns the passed row object. The above behavior is required for use
     * of custom comparators for sorting which requires the Row object to be passed without converting to a particular
     * column String value {@inheritDoc}
     */
    @Override
    public Object getDataValue(final T compareRowObject, final int columnIndex) {
      return compareRowObject;
    }


    @Override
    public void setDataValue(final T sysConstNatModel, final int columnIndex, final Object newValue) {
      // TODO:
    }

    @Override
    public int getColumnCount() {
      return ComparePIDCPage.this.propertyToLabelMap.size();
    }


  }

  /**
   * @return
   */
  private static Comparator<CompareRowObject> getComparePIDCComparator(final int columnNum) {

    return (cmpRowObj1, cmpRowObj2) -> {
      int ret = 0;
      switch (columnNum) {
        case 0:
          // No compare for ball image column
          break;
        case 1:
          ret = cmpRowObj1.getAttribute().compareTo(cmpRowObj2.getAttribute(), ApicConstants.SORT_ATTRNAME);
          break;
        case 2:
          ret = cmpRowObj1.getAttribute().compareTo(cmpRowObj2.getAttribute(), ApicConstants.SORT_ATTRDESCR);
          break;
        default:
          ret = comparePIDCAttribute(cmpRowObj1, cmpRowObj2, columnNum);
      }
      return ret;


    };
  }

  private static int comparePIDCAttribute(final CompareRowObject cmpRowObj1, final CompareRowObject cmpRowObj2,
      final int col) {
    IProjectAttribute ipidcAttribute1 = cmpRowObj1.getColumnDataMapper().getColumnIndexPIDCAttrMap().get(col);
    IProjectAttribute ipidcAttribute2 = cmpRowObj2.getColumnDataMapper().getColumnIndexPIDCAttrMap().get(col);
    String columnHeader = cmpRowObj1.getColumnDataMapper().getColumnIndexFlagMap().get(col);
    int returnValue;

    AbstractProjectObjectBO projObjBO =
        cmpRowObj1.getColumnDataMapper().getProjectHandlerMap().get(new ProjectAttributeUtil().getID(ipidcAttribute1));

    switch (columnHeader) {
      case ApicConstants.SUMMARY_LABEL:
        returnValue = ipidcAttribute1.getUsedFlag().compareTo(ipidcAttribute2.getUsedFlag());
        break;
      case ApicConstants.VALUE_TEXT:
        returnValue = projObjBO.compare(ipidcAttribute1, ipidcAttribute2, ApicConstants.SORT_VALUE);
        break;
      case ApicConstants.CHARACTERISTIC:
        returnValue = projObjBO.compare(ipidcAttribute1, ipidcAttribute2, ApicConstants.SORT_CHAR);
        break;
      case ApicConstants.PART_NUMBER:
        returnValue = projObjBO.compare(ipidcAttribute1, ipidcAttribute2, ApicConstants.SORT_PART_NUMBER);
        break;
      case ApicConstants.SPECIFICATION:
        returnValue = projObjBO.compare(ipidcAttribute1, ipidcAttribute2, ApicConstants.SORT_SPEC_LINK);
        break;
      case ApicConstants.COMMENT:
        returnValue = projObjBO.compare(ipidcAttribute1, ipidcAttribute2, ApicConstants.SORT_DESC);
        break;
      case ApicConstants.MODIFIED_DATE:
        returnValue = projObjBO.compare(ipidcAttribute1, ipidcAttribute2, ApicConstants.SORT_MODIFIED_DATE);
        break;
      case ApicConstants.CHARVAL:
        returnValue = projObjBO.compare(ipidcAttribute1, ipidcAttribute2, ApicConstants.SORT_CHAR_VAL);
        break;
      default:
        returnValue = getDefaultValue(cmpRowObj1, cmpRowObj2, col);
    }
    return returnValue;
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
   * {@inheritDoc}
   */
  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMessage(outlineSelection);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if (CommonUtils.isEqual(getSite().getPage().getActiveEditor(), getEditor()) && (part instanceof OutlineViewPart) &&
        (getEditor().getActivePage() == 0)) {
      outLineSelectionListener(selection);
      setStatusBarMessage(true);
    }
  }


  /**
   * This method invokes on the selection of Outline Tree node
   *
   * @param selection
   */
  private void outLineSelectionListener(final ISelection selection) {
    if ((selection != null) && !selection.isEmpty() && (selection instanceof IStructuredSelection)) {
      final Object first = ((IStructuredSelection) selection).getFirstElement();
      // Check if selection is SuperGroup
      if (first instanceof com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup) {
        this.outLineNatFilter.setSuperGroup(true);
        final com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup attrSuperGroup =
            (com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup) first;
        this.outLineNatFilter.setSelectedNode(attrSuperGroup.getName());
        this.compPIDCFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.compPIDCFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compPIDCFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      // Check if selection is Group
      else if (first instanceof com.bosch.caltool.icdm.model.apic.attr.AttrGroup) {
        this.outLineNatFilter.setGroup(true);
        final com.bosch.caltool.icdm.model.apic.attr.AttrGroup attrGroup =
            (com.bosch.caltool.icdm.model.apic.attr.AttrGroup) first;
        this.outLineNatFilter.setSelectedNode(attrGroup.getName());
        this.compPIDCFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.compPIDCFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compPIDCFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      // Check if selection is COMMON
      else if ((first instanceof com.bosch.caltool.icdm.client.bo.apic.AttrRootNode) ||
          (first instanceof UseCaseRootNode) ||
          (first instanceof com.bosch.caltool.icdm.client.bo.uc.UserFavUcRootNode)) {
        this.outLineNatFilter.setCommon(true);
        this.outLineNatFilter.setSelectedNode("");
        this.compPIDCFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(true);
        this.compPIDCFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compPIDCFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      else if (first instanceof IUseCaseItemClientBO) {
        this.outLineNatFilter.setUseCaseItem((IUseCaseItemClientBO) first);
        this.compPIDCFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.compPIDCFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compPIDCFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      else if (first instanceof com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode) {
        this.outLineNatFilter.setFavUseCaseItem((com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode) first);
        this.compPIDCFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.compPIDCFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compPIDCFilterGridLayer.getSortableColumnHeaderLayer()));
      }
    }
  }

  private static class FilterRowCustomConfiguration extends AbstractRegistryConfiguration {

    final DefaultDoubleDisplayConverter doubleDisplayConverter = new DefaultDoubleDisplayConverter();

    @Override
    public void configureRegistry(final IConfigRegistry localConfigRegistry) {
      // override the default filter row configuration for painter
      localConfigRegistry.registerConfigAttribute(CELL_PAINTER,
          new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);


      // TODO: Below four lines can be removed. To be checked
      localConfigRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR,
          getIgnorecaseComparator(), NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 0);
      localConfigRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR,
          getIgnorecaseComparator(), NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);


      localConfigRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 0);
      localConfigRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);

      List<String> comboList = Arrays.asList("True", "False");
      // register a combo box cell editor for the Diff column in the filter row
      // the label is set automatically to the value of
      // FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + column position
      ICellEditor comboBoxCellEditor = new ComboBoxCellEditor(comboList);
      localConfigRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 3);

    }

    private static Comparator<?> getIgnorecaseComparator() {
      return (final String str1, final String str2) -> str1.compareToIgnoreCase(str2);
    }
  }


  /**
   * @param cmpRowObj1
   * @param cmpRowObj2
   * @param col
   * @param returnValue
   * @return
   */
  private static int getDefaultValue(final CompareRowObject cmpRowObj1, final CompareRowObject cmpRowObj2,
      final int col) {
    int returnValue = 0;
    Object columnValue1 = cmpRowObj1.getColumnDataMapper().getColumnData(col);
    Object columnValue2 = cmpRowObj2.getColumnDataMapper().getColumnData(col);
    columnValue1 = columnValue1 == null ? "" : columnValue1;
    columnValue2 = columnValue2 == null ? "" : columnValue2;
    if ((columnValue1 instanceof String) && (columnValue2 instanceof String)) {
      returnValue = ((String) columnValue1).compareTo((String) columnValue2);
    }
    return returnValue;
  }

  private IConfiguration getCustomComparatorConfiguration(final AbstractLayer columnHeaderDataLayer) {

    return new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        customComparatorConfiguration(columnHeaderDataLayer, configRegistry);
      }
    };
  }

  /**
   * @param columnHeaderDataLayer
   * @param configRegistry
   */
  private void customComparatorConfiguration(final AbstractLayer columnHeaderDataLayer,
      final IConfigRegistry configRegistry) {
    // Add label accumulator
    ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
    columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);

    // Register labels
    labelAccumulator.registerColumnOverrides(0, CUSTOM_COMPARATOR_LABEL + 0);

    labelAccumulator.registerColumnOverrides(1, CUSTOM_COMPARATOR_LABEL + 1);

    labelAccumulator.registerColumnOverrides(2, CUSTOM_COMPARATOR_LABEL + 2);

    labelAccumulator.registerColumnOverrides(3, CUSTOM_COMPARATOR_LABEL + 3);


    configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, new NullComparator(), NORMAL,
        CUSTOM_COMPARATOR_LABEL + 0);

    configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getComparePIDCComparator(1), NORMAL,
        CUSTOM_COMPARATOR_LABEL + 1);

    configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getComparePIDCComparator(2), NORMAL,
        CUSTOM_COMPARATOR_LABEL + 2);

    configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getComparePIDCComparator(3), NORMAL,
        CUSTOM_COMPARATOR_LABEL + 3);


    for (int i = 4; i < (ComparePIDCPage.this.propertyToLabelMap.size()); i++) {
      String colHeader = ComparePIDCPage.this.propertyToLabelMap.get(i);
      if (CommonUIConstants.EMPTY_STRING.equals(colHeader) ||
          colHeader.equals(Messages.getString(IMessageConstants.QUESTION_LABEL)) ||
          colHeader.equals(Messages.getString(IMessageConstants.NO_LABEL)) ||
          colHeader.equals(Messages.getString(IMessageConstants.YES_LABEL))) {
        continue;
      }
      labelAccumulator.registerColumnOverrides(i, CUSTOM_COMPARATOR_LABEL + i);

      configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getComparePIDCComparator(i), NORMAL,
          CUSTOM_COMPARATOR_LABEL + i);
    }

    // Register null comparator to disable sort
  }

  /**
   * ICDM- 1622
   *
   * @param menuLabel
   * @return
   */
  private IMenuItemProvider openPIDCMenuItemProvider(final String menuLabel) {
    return (final NatTable natTable, final Menu popupMenu) -> {
      MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
      menuItem.setText(menuLabel);
      menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.PIDC_16X16));
      menuItem.setEnabled(true);
      menuItem.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {

          PidcVersion pidcVersionToOpen = ((PIDCCompareEditorInput) ComparePIDCPage.this.getEditorInput())
              .getComparePidcHandler().getCompareObjectsHandlerMap()
              .get(new ProjectAttributeUtil().getID(
                  ComparePIDCPage.this.compareRowObjects.first().getColumnDataMapper().getColumnIndexPIDCAttrMap()
                      .get(LayerUtil.convertColumnPosition(natTable,
                          MenuItemProviders.getNatEventData(event).getColumnPosition(),
                          ComparePIDCPage.this.compPIDCFilterGridLayer.getColumnHeaderDataLayer()))))
              .getPidcVersion();
          new PIDCActionSet().openPIDCEditor(pidcVersionToOpen);
        }
      });
    };
  }

  private IMenuItemProvider removeColumnMenuItemProvider(final String menuLabel) {
    return (final NatTable natTable, final Menu popupMenu) -> {
      MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
      menuItem.setText(menuLabel);
      menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
      menuItem.setEnabled(true);
      menuItem.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          rmvPidcAttrOrVersFromCompEditor(natTable, event);
        }
      });
    };
  }

  /**
   * @param natTable
   * @param event
   */
  private void rmvPidcAttrOrVersFromCompEditor(final NatTable natTable, final SelectionEvent event) {
    NatEventData natEventData = MenuItemProviders.getNatEventData(event);
    int columnPosition = natEventData.getColumnPosition();
    int convertColumnPosition = LayerUtil.convertColumnPosition(natTable, columnPosition,
        ComparePIDCPage.this.compPIDCFilterGridLayer.getColumnHeaderDataLayer());
    LabelStack regionLabels = natEventData.getRegionLabels();
    List<String> labels = regionLabels.getLabels();
    if (labels.contains(GROUP_GROUP_REGION)) {
      if (ComparePIDCPage.this.isVariantsCompare) {
        IProjectAttribute pidcAttr = ComparePIDCPage.this.compareRowObjects.first().getColumnDataMapper()
            .getColumnIndexPIDCAttrMap().get(convertColumnPosition);
        if (pidcAttr instanceof PidcVariantAttribute) {
          PidcVariantAttribute varAttr = (PidcVariantAttribute) pidcAttr;
          removeFromCompareEditor(((PIDCCompareEditorInput) ComparePIDCPage.this.getEditorInput())
              .getComparePidcHandler().getCompareObjectsHandlerMap().get(varAttr.getVariantId()).getPidcDataHandler()
              .getVariantMap().get(varAttr.getVariantId()));
        }
      }
      else if (ComparePIDCPage.this.isSubVarsCompare) {
        IProjectAttribute pidcAttr = ComparePIDCPage.this.compareRowObjects.first().getColumnDataMapper()
            .getColumnIndexPIDCAttrMap().get(convertColumnPosition);
        if (pidcAttr instanceof PidcSubVariantAttribute) {
          PidcSubVariantAttribute subVarAttr = (PidcSubVariantAttribute) pidcAttr;
          removeFromCompareEditor(((PIDCCompareEditorInput) ComparePIDCPage.this.getEditorInput())
              .getComparePidcHandler().getCompareObjectsHandlerMap().get(subVarAttr.getSubVariantId())
              .getPidcDataHandler().getSubVariantMap().get(subVarAttr.getSubVariantId()));
        }
      }
      else {
        PidcVersion pidcVersionToRemove = ((PIDCCompareEditorInput) ComparePIDCPage.this.getEditorInput())
            .getComparePidcHandler().getCompareObjectsHandlerMap()
            .get(new ProjectAttributeUtil().getID(ComparePIDCPage.this.compareRowObjects.first().getColumnDataMapper()
                .getColumnIndexPIDCAttrMap().get(convertColumnPosition)))
            .getPidcVersion();
        removeFromCompareEditor(pidcVersionToRemove);
      }
    }
  }

  private IMenuItemProvider hideSimilarColumnGroupMenuItemProvider(final String menuLabel) {
    return (final NatTable natTable, final Menu popupMenu) -> {
      MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
      menuItem.setText(menuLabel);
      menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.HIDE_SIMILAR_16X16));
      menuItem.setEnabled(true);
      menuItem.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          hideColAction(natTable, event);
        }
      });
    };
  }

  /**
   * @param natTable
   * @param event
   */
  private void hideColAction(final NatTable natTable, final SelectionEvent event) {
    NatEventData natEventData = MenuItemProviders.getNatEventData(event);
    LabelStack regionLabels = natEventData.getRegionLabels();
    List<String> labels = regionLabels.getLabels();
    if (!labels.contains(GROUP_GROUP_REGION) && labels.contains(GridRegion.COLUMN_GROUP_HEADER)) {
      ColumnGroupModel columnGroupModel = ComparePIDCPage.this.compPIDCFilterGridLayer.getColumnGroupModel();
      int columnPositionFromRightClick = natEventData.getColumnPosition();
      int convertColumnPosition = LayerUtil.convertColumnPosition(natTable, columnPositionFromRightClick,
          ComparePIDCPage.this.compPIDCFilterGridLayer.getColumnHeaderDataLayer());
      ColumnGroup columnGroupFromClick = columnGroupModel.getColumnGroupByIndex(convertColumnPosition);
      List<Integer> columnPositionsToHide = new ArrayList<>();
      for (int i = 1; i < ComparePIDCPage.this.compPIDCFilterGridLayer.getColumnHeaderDataLayer()
          .getColumnCount(); i++) {
        int columnPositionAvailable = LayerUtil.convertColumnPosition(
            ComparePIDCPage.this.compPIDCFilterGridLayer.getBodyLayer().getFreezeLayer(), i,
            ComparePIDCPage.this.compPIDCFilterGridLayer.getDummyDataLayer());
        ColumnGroup columnGroup = columnGroupModel.getColumnGroupByIndex(columnPositionAvailable);
        if ((columnGroup != null) && columnGroup.getName().contains(columnGroupFromClick.getName().trim())) {
          columnPositionsToHide.add(columnPositionAvailable);
        }
      }
      ColumnHideShowLayer columnHideShowLayer =
          ComparePIDCPage.this.compPIDCFilterGridLayer.getBodyLayer().getColumnHideShowLayer();
      columnHideShowLayer.hideColumnPositions(columnHideShowLayer.getColumnPositionsByIndexes(columnPositionsToHide));
    }
  }

  int[] toIntArray(final List<Integer> list) {
    int[] ret = new int[list.size()];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = list.get(i);
    }
    return ret;
  }

  /**
   * @return the outLineNatFilter
   */
  public ComparePIDCOutlineFilter getOutLineNatFilter() {
    return this.outLineNatFilter;
  }


  /**
   * Checks whether the drop is valid
   *
   * @param compareObj
   * @param pidVer pidVersion
   * @param pidVar pid variant
   * @param pidSubVar pid sub-variant
   */
  private String isValidDrop(final IProjectObject compareObj) {
    String errorMsg = "";
    if (compareObj instanceof PidcVersion) {
      PidcDetailsLoader loader = new PidcDetailsLoader(new PidcDataHandler());
      PidcDataHandler handler = loader.loadDataModel(compareObj.getId());
      ProjectHandlerInit init = new ProjectHandlerInit((PidcVersion) compareObj, (PidcVersion) compareObj, handler,
          ApicConstants.LEVEL_PIDC_VERSION);
      if (((PidcVersionBO) init.getProjectObjectBO()).isHidden()) {
        errorMsg = "Cannot add the project version as it is hidden";
      }
      else if (ComparePIDCPage.this.isVariantsCompare) {
        errorMsg = "Cannot add the project version to variant compare editor";
      }
      else if (ComparePIDCPage.this.isSubVarsCompare) {
        errorMsg = "Cannot add the project version to sub-variant compare editor";
      }
      else {
        // add to datahandler map
        ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getCompareObjectsHandlerMap()
            .put(compareObj.getId(), init.getProjectObjectBO());
      }
    }
    else if (compareObj instanceof PidcVariant) {
      if (ComparePIDCPage.this.isSubVarsCompare) {
        errorMsg = "Cannot add the variant to sub-variant compare editor";
      }
      else if (ComparePIDCPage.this.isVariantsCompare) {
        if (!((PidcVariant) compareObj).getPidcVersionId()
            .equals(((PidcVariant) ComparePIDCPage.this.compareObjs.get(0)).getPidcVersionId())) {
          errorMsg = "The variant does not belong to the PIDCVersion used in the compare editor";
        }
      }
      else {
        errorMsg = "Cannot add the variant to project compare editor";
      }
    }
    else if (compareObj instanceof PidcSubVariant) {
      if (ComparePIDCPage.this.isVariantsCompare) {
        errorMsg = "Cannot add the sub-variant to variant compare editor";
      }
      else if (ComparePIDCPage.this.isSubVarsCompare) {

        if (!((PidcSubVariant) compareObj).getPidcVariantId()
            .equals(((PidcSubVariant) ComparePIDCPage.this.compareObjs.get(0)).getPidcVariantId())) {
          errorMsg = "The sub-variant does not belong to the variant used in the compare editor";
        }
      }
      else {
        errorMsg = "Cannot add the sub-variant to the project compare editor";
      }
    }
    return errorMsg;
  }

  /**
   * When an insert happens in db the IPIDCAttribute used by nattable is replaced with another IPIDCAttribute with the
   * new pidcAttrID,so this new pidcattribute needs to be updated in the nattable data
   *
   * @param columnIndex
   * @param columnDataMapper
   * @param flagName
   * @param ipidcAttribute
   * @param pidcPageEditUtil
   */
  private void callCheckBoxEditor(final Integer columnIndex, final ColumnDataMapper columnDataMapper,
      final String flagName, final IProjectAttribute ipidcAttribute, final PIDCPageEditUtil pidcPageEditUtil) {
    if (flagName.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType())) {
      pidcPageEditUtil.editProjectAttributeNotDefinedFlag(ipidcAttribute);
    }
    else if (flagName.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) {
      boolean newSelectedUsedValue = !(boolean) columnDataMapper.getColumnData(columnIndex);
      pidcPageEditUtil.editProjectAttributeNotUsedInfo(ipidcAttribute, Boolean.toString(newSelectedUsedValue));
    }
    else if (flagName.equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType())) {
      boolean newSelectedUsedValue = !(boolean) columnDataMapper.getColumnData(columnIndex);
      pidcPageEditUtil.editProjectAttributeUsedInfo(ipidcAttribute, Boolean.toString(newSelectedUsedValue));

    }
  }


  /**
   * @param projectObjects list of project Objects
   * @return error messsage, if any. Else an empty string
   */
  public String addToExistingCompareEditor(final List<?> projectObjects) {
    List<IProjectObject> compObjsToAdd = new ArrayList<>();
    IProjectObject compareObj;
    String errorMsg = ApicConstants.EMPTY_STRING;
    for (Object selectedElement : projectObjects) {
      compareObj = getDroppedObj(selectedElement);
      if (null == compareObj) {
        errorMsg = "Cannot add this item to Compare Editor";
        break;
      }
      errorMsg = isValidDrop(compareObj);
      if (CommonUtils.isEmptyString(errorMsg)) {
        compObjsToAdd.add(compareObj);
      }
      else {
        break;
      }
    }
    if (errorMsg.isEmpty()) {
      addToCompareEditor(compObjsToAdd);
    }
    else {
      MessageDialogUtils.getInfoMessageDialog(CANNOT_ADD_TO_COMPARE_EDITOR, errorMsg);
    }
    return errorMsg;
  }

  // ICDM-2625
  /**
   * @param pidcVersion
   * @param attrValue
   * @return
   */
  private boolean getAllPidcGrpAttrVal(final com.bosch.caltool.icdm.model.apic.attr.AttributeValue attrValue) {
    Map<Long, PidcVersionAttribute> allPidcAttrMap =
        ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getPidcVersionBO().getAllAttrMap();
    AttributeValueClientBO attributeValClientBo = new AttributeValueClientBO(attrValue);
    Set<com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue> predefAttrValSet =
        attributeValClientBo.getPreDefinedAttrValueSet();
    return validateGrpAttrDrop(allPidcAttrMap, predefAttrValSet);
  }

  /**
   * @param allPidcAttrMap
   * @param predefAttrValSet
   * @return
   */
  private boolean validateGrpAttrDrop(final Map<Long, PidcVersionAttribute> allPidcAttrMap,
      final Set<com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue> predefAttrValSet) {
    if (CommonUtils.isNotEmpty(predefAttrValSet)) {
      for (com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue predefAttrVal : predefAttrValSet) {
        PidcVersionAttribute predefAttrPidc = allPidcAttrMap.get(predefAttrVal.getPredefinedAttrId());
        if ((null != predefAttrPidc) && (null != predefAttrVal.getPredefinedValueId())) {
          if (allPidcAttrMap.get(predefAttrPidc.getAttrId()).isAtChildLevel()) {
            return true;
          }
          if (validateDropByPredefAttrDispName(predefAttrVal, predefAttrPidc)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * @param predefAttrVal
   * @param predefAttrPidc
   */
  private boolean validateDropByPredefAttrDispName(
      final com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue predefAttrVal,
      final PidcVersionAttribute predefAttrPidc) {
    AbstractProjectAttributeBO projAttrHandler = ComparePIDCPage.this.compareEditorUtil
        .getProjectAttributeHandler(predefAttrPidc, ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler()
            .getCompareObjectsHandlerMap().get(ComparePIDCPage.this.compareEditorUtil.getID(predefAttrPidc)));
    if (null != projAttrHandler.getDefaultValueDisplayName(false)) {
      if (!projAttrHandler.getDefaultValueDisplayName().equalsIgnoreCase(predefAttrVal.getPredefinedValue())) {
        return true;
      }
    }
    else {
      return true;
    }
    return false;
  }


  /**
   * @return the toolBarFilterStateMap
   */
  public Map<String, Boolean> getToolBarFilterStateMap() {
    return this.toolBarFilterStateMap;
  }


  /**
   * @return RowSelectionProvider<CompareRowObject>
   */
  public RowSelectionProvider<CompareRowObject> getSelectionProvider() {
    return this.selectionProvider;
  }

  /**
   * @return the valueLabel
   */
  public static String getValueLabel() {
    return Messages.getString(IMessageConstants.VALUE_LABEL);
  }


  /**
   * @return the selectedColPostn
   */
  public int getSelectedColPostn() {
    return this.selectedColPostn;
  }


  /**
   * @param selectedColPostn the selectedColPostn to set
   */
  public void setSelectedColPostn(final int selectedColPostn) {
    this.selectedColPostn = selectedColPostn;
  }


  /**
   * @return the selectedRowPostn
   */
  public int getSelectedRowPostn() {
    return this.selectedRowPostn;
  }


  /**
   * @param selectedRowPostn the selectedRowPostn to set
   */
  public void setSelectedRowPostn(final int selectedRowPostn) {
    this.selectedRowPostn = selectedRowPostn;
  }


  /**
   * @return the compareEditorUtil
   */
  public ProjectAttributeUtil getCompareEditorUtil() {
    return this.compareEditorUtil;
  }

  /**
   * @param selection IStructuredSelection
   */
  public void pasteAttrValInTargetPidc(final IStructuredSelection selection) {
    // take contents from the internal clipboard
    final Object copiedObject = ICDMClipboard.getInstance().getCopiedObject();
    Map<Long, IProjectAttribute> copiedAttrIdNPidcAttr = new HashMap<>();
    fillCopiedAttrIdNPidcAttrMap(copiedObject, copiedAttrIdNPidcAttr);
    if (CommonUtils.isNullOrEmpty(copiedAttrIdNPidcAttr)) {
      CDMLogger.getInstance().errorDialog("No PIDC attributes to paste ", Activator.PLUGIN_ID);
      return;
    }
    ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
    CompareRowObject firstCompareRowObjInSelctn = (CompareRowObject) selection.getFirstElement();
    ColumnDataMapper columnDataMapperForFirstSelnObj = firstCompareRowObjInSelctn.getColumnDataMapper();
    // Pidc version/Variant/sub variant id
    Long pidcElementId = columnDataMapperForFirstSelnObj.getColumnIndexProjectObjMap().get(this.selectedColPostn);
    AbstractProjectObjectBO projObjBO = columnDataMapperForFirstSelnObj.getProjectHandlerMap().get(pidcElementId);
    updationModel.setPidcVersion(projObjBO.getPidcVersion());
    // check if pidc is unlocked in session
    checkforPIDCSessionLock(projObjBO.getPidcVersion());
    Map<IProjectAttribute, IProjectAttribute> predefGrpAttrMap = getPredDefinedAttrInPidc(projObjBO);
    StringJoiner errStrinJoiner = new StringJoiner("\n");
    if (!projObjBO.isDeleted()) {
      for (Object rowObj : selection.toList()) {
        if (rowObj instanceof CompareRowObject) {
          CompareRowObject compareRowObject = (CompareRowObject) rowObj;
          ColumnDataMapper columnDataMapper = compareRowObject.getColumnDataMapper();
          IProjectAttribute targetPidcAtr = columnDataMapper.getColumnIndexPIDCAttrMap().get(this.selectedColPostn);

          AbstractProjectAttributeBO projectAttrHandler =
              ComparePIDCPage.this.compareEditorUtil.getProjectAttributeHandler(targetPidcAtr, projObjBO);
          ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getPidcVersionBO().getAttributes();

          String errMsgForPidcAttr =
              new PidcElementAttrValidationBO(projectAttrHandler, predefGrpAttrMap).validatePaste();
          if (CommonUtils.isNotEmptyString(errMsgForPidcAttr)) {
            errStrinJoiner.add(targetPidcAtr.getName() + " -> " + errMsgForPidcAttr);
            continue;
          }

          // set fields in updation model
          setFieldsInPidcUpdationModel(copiedAttrIdNPidcAttr, updationModel, pidcElementId, targetPidcAtr,
              errStrinJoiner, projectAttrHandler);

        }
      }
      if (CommonUtils.isNotEmptyString(errStrinJoiner.toString())) {
        createErrMsg(errStrinJoiner);
      }

      validateNUpdateTargetPidcAttrVal(updationModel);
    }
    else {
      CDMLogger.getInstance().errorDialog("Paste cannot be performed on a deleted PIDC ", Activator.PLUGIN_ID);
    }
  }

  /**
   * @param copiedAttrIdNPidcAttr
   * @param targetPidcAtr
   * @param projectAttrHandler
   * @param updationModel
   * @return
   */
  private boolean validateGrpdAttrUpdate(final Map<Long, IProjectAttribute> copiedAttrIdNPidcAttr,
      final IProjectAttribute targetPidcAtr, final AbstractProjectAttributeBO projectAttrHandler,
      final ProjectAttributesUpdationModel updationModel) {
    if (isGrpAttr(targetPidcAtr)) {
      PidcVersionBO pidcVersionBO = ((PidcVersionBO) projectAttrHandler.getProjectObjectBO());
      PidcAttrValueEditBO valueEditBO = new PidcAttrValueEditBO(pidcVersionBO, targetPidcAtr);
      AttributeValue sourceAttrValue = getAttrVal(copiedAttrIdNPidcAttr.get(targetPidcAtr.getAttrId()));
      PredefinedAttrValueAndValidtyModel model =
          valueEditBO.getPredefinedAttrValueAndValidtyModel(sourceAttrValue.getId());
      Map<Long, Map<Long, PredefinedValidity>> predefinedValidityMap = model.getPredefinedValidityMap();
      Map<Long, Map<Long, PredefinedAttrValue>> newPredefinedAttrValueMap = model.getPredefinedAttrValueMap();
      if (CommonUtils.isNullOrEmpty(newPredefinedAttrValueMap)) {
        Long prevGrpAttrValueId = targetPidcAtr.getValueId();
        if (valueEditBO.hasPredefAttr(prevGrpAttrValueId)) {
          valueEditBO.resetExistingPredAttrVal(updationModel, prevGrpAttrValueId, targetPidcAtr);
          return true;
        }
      }
      else {
        PidcDataHandler pidcDataHandler = pidcVersionBO.getPidcDataHandler();
        if (pidcVersionBO.checkGroupedAttrValueValidity(sourceAttrValue, predefinedValidityMap)) {
          boolean flag = canOpenGrpAttrChangesDialog(targetPidcAtr, pidcVersionBO, valueEditBO, sourceAttrValue,
              newPredefinedAttrValueMap, pidcDataHandler);
          if (flag) {
            openGrpdAttrChangesDialog(targetPidcAtr, sourceAttrValue, projectAttrHandler);
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * @param targetPidcAtr
   * @param pidcVersionBO
   * @param valueEditBO
   * @param sourceAttrValue
   * @param newPredefinedAttrValueMap
   * @param pidcDataHandler
   * @return
   */
  private boolean canOpenGrpAttrChangesDialog(final IProjectAttribute targetPidcAtr, final PidcVersionBO pidcVersionBO,
      final PidcAttrValueEditBO valueEditBO, final AttributeValue sourceAttrValue,
      final Map<Long, Map<Long, PredefinedAttrValue>> newPredefinedAttrValueMap,
      final PidcDataHandler pidcDataHandler) {
    boolean canOpenGrpAttrChangesDialog = false;
    PIDCGroupedAttrActionSet actionSet = new PIDCGroupedAttrActionSet(pidcDataHandler, pidcVersionBO);
    Map<Long, PidcVersionAttribute> allPIDCAttrMap = pidcDataHandler.getPidcVersAttrMap();
    Set<PredefinedAttrValue> preDefinedAttrValueSet =
        actionSet.getPredefinedAttrValSet(sourceAttrValue, newPredefinedAttrValueMap);

    canOpenGrpAttrChangesDialog = actionSet.isallPredefinedAttrInSetInvisible(allPIDCAttrMap, preDefinedAttrValueSet)
        ? canOpenGrpAttrChangesDialog
        : actionSet.checkAllPredefAttrVal(sourceAttrValue, allPIDCAttrMap, targetPidcAtr, newPredefinedAttrValueMap) ||
            valueEditBO.diffWithCurrPredAttr(newPredefinedAttrValueMap, targetPidcAtr, sourceAttrValue);
    return canOpenGrpAttrChangesDialog;
  }

  /**
   * @param errorMap
   */
  private void createErrMsg(final StringJoiner errorStrinMsg) {
    if (errorStrinMsg.toString().split("\n").length > MAX_NUM_OF_ATTR) {
      String[] errStrinDialog = errorStrinMsg.toString().split("\n");
      StringJoiner dialogErrStrMsg = new StringJoiner("\n");
      // create const upper limit of array
      for (int countIndex = 0; countIndex < MAX_NUM_OF_ATTR; countIndex++) {
        dialogErrStrMsg.add(errStrinDialog[countIndex]);
      }
      dialogErrStrMsg.add("...(more)");
      dialogErrStrMsg
          .add("To view the complete list of attributes where paste operation failed, check the Error Log view.");
      MessageDialog.openError(Display.getCurrent().getActiveShell(), ICDMLoggerConstants.DIALOG_TITLE_ERROR,
          PASTE_ERR_MSG_FIRST_LINE + dialogErrStrMsg.toString());
      CDMLogger.getInstance().error(PASTE_ERR_MSG_FIRST_LINE + errorStrinMsg.toString(), Activator.PLUGIN_ID);
    }
    else {
      CDMLogger.getInstance().errorDialog(PASTE_ERR_MSG_FIRST_LINE + errorStrinMsg.toString(), Activator.PLUGIN_ID);
    }
  }

  /**
   * @param copiedObject
   * @param copiedAttrIdNPidcAttr
   */
  @SuppressWarnings("unchecked")
  private void fillCopiedAttrIdNPidcAttrMap(final Object copiedObject,
      final Map<Long, IProjectAttribute> copiedAttrIdNPidcAttr) {
    if (copiedObject instanceof Map<?, ?>) {
      copiedAttrIdNPidcAttr.putAll((Map<? extends Long, ? extends IProjectAttribute>) copiedObject);
    }
  }

  /**
   * @param updationModel
   */
  private void validateNUpdateTargetPidcAttrVal(final ProjectAttributesUpdationModel updationModel) {
    if (isUpdationModelEmpty(updationModel)) {
      return;
    }
    ProjectAttributesUpdationServiceClient upClient = new ProjectAttributesUpdationServiceClient();
    try {
      upClient.updatePidcAttrs(updationModel);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param updationModel
   */
  private boolean isUpdationModelEmpty(final ProjectAttributesUpdationModel updationModel) {
    return (isAttrAvailableForUpdate(updationModel) && isAttrAvailableForCreate(updationModel));
  }

  /**
   * @param updationModel
   * @return
   */
  private boolean isAttrAvailableForCreate(final ProjectAttributesUpdationModel updationModel) {
    return updationModel.getPidcAttrsToBeCreated().isEmpty() && updationModel.getPidcVarAttrsToBeCreated().isEmpty() &&
        updationModel.getPidcSubVarAttrsToBeCreated().isEmpty();
  }

  /**
   * @param updationModel
   * @return
   */
  private boolean isAttrAvailableForUpdate(final ProjectAttributesUpdationModel updationModel) {
    return updationModel.getPidcAttrsToBeUpdated().isEmpty() && updationModel.getPidcVarAttrsToBeUpdated().isEmpty() &&
        updationModel.getPidcSubVarAttrsToBeUpdated().isEmpty();
  }


  /**
   * @param copiedAttrIdNPidcAttrMap
   * @param updationModel
   * @param pidcElementId
   * @param targetPidcAttribute
   * @param errStrinJoiner
   * @param projectAttrHandler
   */
  private void setFieldsInPidcUpdationModel(final Map<Long, IProjectAttribute> copiedAttrIdNPidcAttrMap,
      final ProjectAttributesUpdationModel updationModel, final Long pidcElementId,
      final IProjectAttribute targetPidcAttribute, final StringJoiner errStrinJoiner,
      final AbstractProjectAttributeBO projectAttrHandler) {
    IProjectAttribute sourceIProjectAttribute = copiedAttrIdNPidcAttrMap.get(targetPidcAttribute.getAttrId());
    boolean isGrpdAttrToUpdate =
        validateGrpdAttrUpdate(copiedAttrIdNPidcAttrMap, targetPidcAttribute, projectAttrHandler, updationModel);
    if (isGrpdAttrToUpdate ||
        canSetFieldsToCreateUpdateAttr(targetPidcAttribute, sourceIProjectAttribute, errStrinJoiner)) {
      if (this.isVariantsCompare) {
        PidcVariantAttribute targetPidcVarAttr = (PidcVariantAttribute) targetPidcAttribute;
        setVarAttrFieldsToUpdate(copiedAttrIdNPidcAttrMap, updationModel, pidcElementId, targetPidcAttribute,
            sourceIProjectAttribute, targetPidcVarAttr);
      }
      else if (this.isSubVarsCompare) {
        PidcSubVariantAttribute targetPidcSubVarAttr = (PidcSubVariantAttribute) targetPidcAttribute;
        setSubVarAttrFieldToUpdate(copiedAttrIdNPidcAttrMap, updationModel, pidcElementId, targetPidcAttribute,
            sourceIProjectAttribute, targetPidcSubVarAttr);
      }
      else {
        PidcVersionAttribute targetPidcVersnAttr = (PidcVersionAttribute) targetPidcAttribute;
        setPidcVersnAttrFieldsToUpdate(copiedAttrIdNPidcAttrMap, updationModel, targetPidcAttribute,
            sourceIProjectAttribute, targetPidcVersnAttr);
      }
    }
  }


  /**
   * @param copiedAttrIdNPidcAttrMap
   * @param updationModel
   * @param pidcElementId
   * @param targetPidcAttribute
   * @param sourceIProjectAttribute
   * @param targetPidcVarAttr
   */
  private void setVarAttrFieldsToUpdate(final Map<Long, IProjectAttribute> copiedAttrIdNPidcAttrMap,
      final ProjectAttributesUpdationModel updationModel, final Long pidcElementId,
      final IProjectAttribute targetPidcAttribute, final IProjectAttribute sourceIProjectAttribute,
      final PidcVariantAttribute targetPidcVarAttr) {
    if ((targetPidcAttribute.getId() == null)) {
      Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeCreated = updationModel.getPidcVarAttrsToBeCreated();
      pidcVarAttrsToBeCreated.putIfAbsent(pidcElementId, new HashMap<Long, PidcVariantAttribute>());
      setFiledsInPidcEleAttrToCreate(targetPidcVarAttr, sourceIProjectAttribute);
      pidcVarAttrsToBeCreated.get(pidcElementId).put(targetPidcAttribute.getAttrId(), targetPidcVarAttr);
    }
    else {
      PidcVariantAttribute targetClonedPidcVarAttr = targetPidcVarAttr.clone();
      CommonUtils.shallowCopy(targetClonedPidcVarAttr, targetPidcVarAttr);
      setPidcAttrFieldToUpdate(copiedAttrIdNPidcAttrMap, targetClonedPidcVarAttr);
      Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeUpdated = updationModel.getPidcVarAttrsToBeUpdated();
      pidcVarAttrsToBeUpdated.putIfAbsent(pidcElementId, new HashMap<Long, PidcVariantAttribute>());
      pidcVarAttrsToBeUpdated.get(pidcElementId).put(targetPidcAttribute.getAttrId(), targetClonedPidcVarAttr);

    }
  }

  /**
   * @param copiedAttrIdNPidcAttrMap
   * @param updationModel
   * @param pidcElementId
   * @param targetPidcAttribute
   * @param sourceIProjectAttribute
   * @param targetPidcSubVarAttr
   */
  private void setSubVarAttrFieldToUpdate(final Map<Long, IProjectAttribute> copiedAttrIdNPidcAttrMap,
      final ProjectAttributesUpdationModel updationModel, final Long pidcElementId,
      final IProjectAttribute targetPidcAttribute, final IProjectAttribute sourceIProjectAttribute,
      final PidcSubVariantAttribute targetPidcSubVarAttr) {
    if (targetPidcAttribute.getId() == null) {
      Map<Long, Map<Long, PidcSubVariantAttribute>> pidcVarAttrsToBeCreated =
          updationModel.getPidcSubVarAttrsToBeCreated();
      pidcVarAttrsToBeCreated.putIfAbsent(pidcElementId, new HashMap<Long, PidcSubVariantAttribute>());
      setFiledsInPidcEleAttrToCreate(targetPidcSubVarAttr, sourceIProjectAttribute);
      pidcVarAttrsToBeCreated.get(pidcElementId).put(targetPidcAttribute.getAttrId(), targetPidcSubVarAttr);
    }
    else {
      PidcSubVariantAttribute targetClonedPidcSubVarAttr = targetPidcSubVarAttr.clone();
      CommonUtils.shallowCopy(targetClonedPidcSubVarAttr, targetPidcSubVarAttr);
      setPidcAttrFieldToUpdate(copiedAttrIdNPidcAttrMap, targetClonedPidcSubVarAttr);
      Map<Long, Map<Long, PidcSubVariantAttribute>> pidcVarAttrsToBeUpdated =
          updationModel.getPidcSubVarAttrsToBeUpdated();
      pidcVarAttrsToBeUpdated.putIfAbsent(pidcElementId, new HashMap<Long, PidcSubVariantAttribute>());
      pidcVarAttrsToBeUpdated.get(pidcElementId).put(targetPidcAttribute.getAttrId(), targetClonedPidcSubVarAttr);
    }
  }

  /**
   * @param copiedAttrIdNPidcAttrMap
   * @param updationModel
   * @param targetPidcAttribute
   * @param sourceIProjectAttribute
   * @param targetPidcVersnAttr
   */
  private void setPidcVersnAttrFieldsToUpdate(final Map<Long, IProjectAttribute> copiedAttrIdNPidcAttrMap,
      final ProjectAttributesUpdationModel updationModel, final IProjectAttribute targetPidcAttribute,
      final IProjectAttribute sourceIProjectAttribute, final PidcVersionAttribute targetPidcVersnAttr) {
    if (targetPidcAttribute.getId() == null) {
      Map<Long, PidcVersionAttribute> pidcAttrsToBeCreated = updationModel.getPidcAttrsToBeCreated();
      setFiledsInPidcEleAttrToCreate(targetPidcVersnAttr, sourceIProjectAttribute);
      pidcAttrsToBeCreated.put(targetPidcAttribute.getAttrId(), targetPidcVersnAttr);
    }
    else {
      PidcVersionAttribute targetClonedPidcVersnAttr = targetPidcVersnAttr.clone();
      CommonUtils.shallowCopy(targetPidcVersnAttr, targetClonedPidcVersnAttr);
      setPidcAttrFieldToUpdate(copiedAttrIdNPidcAttrMap, targetClonedPidcVersnAttr);
      updationModel.getPidcAttrsToBeUpdated().put(targetPidcAttribute.getAttrId(), targetClonedPidcVersnAttr);
    }
  }

  /**
   * @param targetPidcVersnAttr
   * @param iProjectAttribute
   */
  private void setFiledsInPidcEleAttrToCreate(final IProjectAttribute targetIProjectAttribute,
      final IProjectAttribute sourceIProjectAttribute) {
    targetIProjectAttribute.setValueId(sourceIProjectAttribute.getValueId());
    targetIProjectAttribute.setValue(sourceIProjectAttribute.getValue());
    targetIProjectAttribute.setPartNumber(sourceIProjectAttribute.getPartNumber());
    targetIProjectAttribute.setSpecLink(sourceIProjectAttribute.getSpecLink());
    targetIProjectAttribute.setAdditionalInfoDesc(sourceIProjectAttribute.getAdditionalInfoDesc());
    targetIProjectAttribute.setAttrHidden(sourceIProjectAttribute.isAttrHidden());
    targetIProjectAttribute.setUsedFlag(sourceIProjectAttribute.getUsedFlag());
  }


  /**
   * @param copiedAttrIdNValId
   * @param targetPidcAttribute
   */
  private void setPidcAttrFieldToUpdate(final Map<Long, IProjectAttribute> copiedAttrIdNValId,
      final IProjectAttribute targetPidcAttribute) {
    IProjectAttribute sourceIProjectAttribute = copiedAttrIdNValId.get(targetPidcAttribute.getAttrId());
    targetPidcAttribute.setValueId(sourceIProjectAttribute.getValueId());
    targetPidcAttribute.setUsedFlag(ApicConstants.CODE_YES);
    targetPidcAttribute.setValue(sourceIProjectAttribute.getValue());
  }

  /**
   * @param targetPidcAttribute
   * @param sourceIProjectAttribute
   * @param errStrinJoiner
   */
  private boolean canSetFieldsToCreateUpdateAttr(final IProjectAttribute targetPidcAttribute,
      final IProjectAttribute sourceIProjectAttribute, final StringJoiner errStrinJoiner) {
    if ((sourceIProjectAttribute == null) || !targetPidcAttribute.getName().equals(sourceIProjectAttribute.getName())) {
      errStrinJoiner.add(targetPidcAttribute.getName() +
          " ->  Attribute at target PIDC do not match with copied attribute(s) at source");
      return false;
    }

    if (null == sourceIProjectAttribute.getValueId()) {
      errStrinJoiner.add(targetPidcAttribute.getName() + " ->  " + ATTR_VAL_NOT_AVAIL_AT_SRC);
      return false;
    }

    if (sourceIProjectAttribute.isAtChildLevel()) {
      errStrinJoiner.add(targetPidcAttribute.getName() + " ->  " + ATTR_AT_CHILD_LEVEL_AT_SOURCE);
      return false;
    }
    if (sourceIProjectAttribute.getValueId().equals(targetPidcAttribute.getValueId())) {
      errStrinJoiner.add(targetPidcAttribute.getName() + " -> with value " + targetPidcAttribute.getValue() +
          ATTRIBUTE_VALUE_EXISTING);
      return false;
    }
    return true;
  }

  /**
   * @param projObjBO
   * @return
   */
  private Map<IProjectAttribute, IProjectAttribute> getPredDefinedAttrInPidc(final AbstractProjectObjectBO projObjBO) {
    Map<IProjectAttribute, IProjectAttribute> predefGrpAttrMap;
    if (!((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().isVersionCompare()) {
      predefGrpAttrMap = ((PIDCCompareEditorInput) getEditorInput()).getComparePidcHandler().getPidcVersionBO()
          .getPredefAttrGrpAttrMap();
    }
    else {
      predefGrpAttrMap = ((PidcVersionBO) projObjBO).getPredefAttrGrpAttrMap();
    }
    return predefGrpAttrMap;
  }
}
