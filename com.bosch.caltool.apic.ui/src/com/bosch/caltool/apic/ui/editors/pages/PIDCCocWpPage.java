/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommand;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.menu.AbstractHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCCocWpActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCCocWpToolbarActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCSessionLockAction;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.editors.PIDCEditorInput;
import com.bosch.caltool.apic.ui.editors.compare.PIDCCompareNatTableCheckBoxCellEditor;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.PidcCoCWpNattableColumnConverter;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.PidcCoCWpNattableColumnFilterMatcher;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.PidcCoCWpNattableEditConfiguration;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.PidcCoCWpNattableLabelAccumulator;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.PidcCoCWpNattableNattoolTip;
import com.bosch.caltool.apic.ui.table.filters.PIDCCocWpToolBarFilters;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcCocWPDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcCocWpBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.cocwp.CoCWPUsedFlag;
import com.bosch.caltool.icdm.model.apic.cocwp.IProjectCoCWP;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWpData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * @author PDH2COB
 */
public class PIDCCocWpPage extends AbstractGroupByNatFormPage implements ISelectionListener {

  /**
   *
   */
  private static final String COC_WORK_PACKAGES = "CoC Work Packages";

  private static final String CHECK_BOX_CONFIG_LABEL = "checkBox";
  private static final String CHECK_BOX_EDITOR_CNG_LBL = "checkBoxEditor";

  /** Non scrollable form. */
  private Form nonScrollableForm;

  private Composite composite;

  private Composite innerComposite;

  private Section section;

  private Section innerSection;

  private Form form;

  private Form innerForm;

  private Text filterTxt;

  private final PIDCEditor editor;

  private final PidcVersion pidcVersion;

  private final PidcCocWpBO cocWpBO;

  private CustomNATTable nattable;

  private Map<Integer, String> propertyToLabelMap;

  private Map<Integer, Integer> columnWidthMap;

  private CustomFilterGridLayer<IProjectCoCWP> customFilterGridLayer;

  private PidcCoCWpNattableColumnFilterMatcher columnFilterMatcher;

  private SortedSet<IProjectCoCWP> cocWpSet;

  private RowSelectionProvider<IProjectCoCWP> rowSelectionProvider;

  private final PidcVersionBO pidcVersionBO;

  private static final String VAR_LEVEL_COL_NAME = "On Variant Level";

  private static final String SUBVAR_LEVEL_COL_NAME = "On Sub-Variant Level";

  private PIDCCocWpToolbarActionSet toolBarActionSet;

  private ToolBarManager toolbarManager;

  private PIDCCocWpToolBarFilters toolBarFilters;

  /**
   * Map to hold the filter action text and its changed state (checked - true or false)
   */
  private final Map<String, Boolean> toolBarFilterStateMap = new ConcurrentHashMap<>();

  /**
   * Check box columns in nattable
   */
  private static final int[] checkBoxColumns = {
      ApicConstants.COC_WP_UNDEFINED_COLUMN_INDEX,
      ApicConstants.COC_WP_YES_COLUMN_INDEX,
      ApicConstants.COC_WP_NO_COLUMN_INDEX,
      ApicConstants.COC_WP_LEVEL_COLUMN_INDEX };

  private PidcVariant selectedPidcVariant;

  private PidcVersion selectedPidcVersion;

  /**
   * Selected PIDCSubVariant instance
   */
  private PidcSubVariant selectedPidcSubVariant;
  /**
   * Defines PIDC details variant tree node is selected or not
   */
  private boolean isVarNodeSelected;

  /**
   * Defines PIDC details sub-variant tree node is selected or not
   */
  private boolean isSubVarNodeSelected;

  /**
   * last selection object
   */
  private Object lastSelection;

  /**
   * Title Separator
   */
  private static final String STR_ARROW = " >> ";

  private final PidcDataHandler pidcDataHandler;

  /**
  *
  */
  private PIDCSessionLockAction pidcLockAction;
  /**
   * field to store the value of the click event
   */
  private int clickEventVal;
  /**
   * Left mouse click = true
   */
  protected static final int LEFT_MOUSE_CLICK = 1;

  private boolean resetState;


  /**
   * @param editor - PIDC Editor
   */
  public PIDCCocWpPage(final FormEditor editor) {
    super(editor, COC_WORK_PACKAGES, COC_WORK_PACKAGES);
    this.editor = (PIDCEditor) editor;
    this.pidcVersionBO = this.editor.getEditorInput().getPidcVersionBO();
    this.pidcVersion = this.pidcVersionBO.getPidcVersion();

    this.cocWpBO = this.editor.getEditorInput().getPidcCocWpBO();
    this.pidcDataHandler = this.editor.getEditorInput().getPidcVersionBO().getPidcDataHandler();
    this.selectedPidcVersion = ((PIDCEditorInput) editor.getEditorInput()).getSelectedPidcVersion();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {

    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    this.nonScrollableForm.setText(this.pidcVersion.getName().replace("&", "&&"));

    // Create Lock/Unlock icon at top right corner of Coc WP page
    final ToolBarManager toolBarManager = (ToolBarManager) this.nonScrollableForm.getToolBarManager();
    this.pidcLockAction = new PIDCSessionLockAction(this.selectedPidcVersion);
    toolBarManager.add(this.pidcLockAction);
    toolBarManager.update(true);

    ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    FormToolkit formToolkit;
    formToolkit = managedForm.getToolkit();
    createComposite(formToolkit);
    getSite().getPage().addSelectionListener(this);
    PIDCAttrPage pidcAttrPage = this.editor.getPidcPage();
    if (pidcAttrPage.isVaraintNodeSelected()) {
      setCocVariantInfo(pidcAttrPage.getSelectedPidcVariant());
    }
    if (pidcAttrPage.isSubVaraintNodeSelected()) {
      setCocSubVariantInfo(pidcAttrPage.getSelectedPidcSubVariant());
    }
  }


  /**
   * Creates the composite.
   *
   * @param toolkit This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = this.nonScrollableForm.getBody();
    this.composite.setLayout(new GridLayout());

    createSection(toolkit);
    this.composite.setLayoutData(gridData);
  }

  private void createInnerComposite(final FormToolkit toolkit) {

    GridData gridData = GridDataUtil.getInstance().getGridData();

    this.innerComposite = this.form.getBody();
    this.innerComposite.setLayout(new GridLayout());

    createInnerSection(toolkit);

    this.innerComposite.setLayoutData(gridData);
  }

  /**
   * Creates the section.
   *
   * @param toolkit FormToolkit
   */
  public void createSection(final FormToolkit toolkit) {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.section = toolkit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText(COC_WORK_PACKAGES);
    this.section.setExpanded(true);
    this.section.setDescription(getDescriptionForSection());
    createForm(toolkit);
    this.section.setLayoutData(gridData);
    this.section.setClient(this.form);
  }

  private String getDescriptionForSection() {
    return "Work Packages relevant for " + getCocWpTitle();
  }

  public CustomNATTable getNatTable() {
    return this.nattable;
  }

  /**
   * Creates the form.
   *
   * @param toolkit This method initializes form
   */
  private void createForm(final FormToolkit toolkit) {

    this.form = toolkit.createForm(this.section);

    // create composite for workpackages section
    createInnerComposite(toolkit);

    this.form.getBody().setLayout(new GridLayout());
  }

  /**
   * @param toolkit
   */
  public void createInnerSection(final FormToolkit toolkit) {
    GridData gridData = GridDataUtil.getInstance().getGridData();

    this.innerSection = toolkit.createSection(this.innerComposite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.innerSection.setText("Work Packages");
    this.innerSection.setExpanded(true);
    this.innerSection.setLayoutData(gridData);

    createInnerForm(toolkit);
    this.innerSection.setClient(this.innerForm);
  }

  private void createInnerForm(final FormToolkit toolkit) {
    this.innerForm = toolkit.createForm(this.innerSection);
    this.innerForm.getBody().setLayout(new GridLayout());

    this.filterTxt = toolkit.createText(this.innerForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    createFilterTxt();

    this.toolBarActionSet = new PIDCCocWpToolbarActionSet(this.customFilterGridLayer, this);

    createTable();

    createToolBarAction();
  }

  private void attachToolTip() {
    DefaultToolTip toolTip = new PidcCoCWpNattableNattoolTip(this.nattable, new String[0]);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));
  }


  private void configureColumns() {
    this.propertyToLabelMap = new HashMap<>();
    this.columnWidthMap = new HashMap<>();

    this.propertyToLabelMap.put(ApicConstants.COC_WP_COLUMN_INDEX, "Work Package");
    this.columnWidthMap.put(ApicConstants.COC_WP_COLUMN_INDEX, 350);

    this.propertyToLabelMap.put(ApicConstants.COC_WP_UNDEFINED_COLUMN_INDEX, ApicConstants.USED_NOTDEF_DISPLAY);
    this.columnWidthMap.put(ApicConstants.COC_WP_UNDEFINED_COLUMN_INDEX, 50);

    this.propertyToLabelMap.put(ApicConstants.COC_WP_YES_COLUMN_INDEX, ApicConstants.USED_YES_COL_NAME);
    this.columnWidthMap.put(ApicConstants.COC_WP_YES_COLUMN_INDEX, 50);

    this.propertyToLabelMap.put(ApicConstants.COC_WP_NO_COLUMN_INDEX, ApicConstants.USED_NO_COL_NAME);
    this.columnWidthMap.put(ApicConstants.COC_WP_NO_COLUMN_INDEX, 50);

    this.propertyToLabelMap.put(ApicConstants.COC_WP_LEVEL_COLUMN_INDEX, VAR_LEVEL_COL_NAME);
    this.columnWidthMap.put(ApicConstants.COC_WP_LEVEL_COLUMN_INDEX, 120);

  }

  private Comparator<IProjectCoCWP> getNattableComparator(final int columnNumber) {
    return (final IProjectCoCWP obj1, final IProjectCoCWP obj2) -> {
      int compareResult = 0;

      switch (columnNumber) {
        case 0:
          compareResult = ApicUtil.compare(obj1.getName(), obj2.getName());
          break;
        case 1:
          boolean undefinedBoolean1 = getUsedFlagData(obj1.getUsedFlag(), CoCWPUsedFlag.NOT_DEFINED.getDbType());
          boolean undefinedBoolean2 = getUsedFlagData(obj2.getUsedFlag(), CoCWPUsedFlag.NOT_DEFINED.getDbType());
          compareResult = ApicUtil.compareBoolean(undefinedBoolean1, undefinedBoolean2);
          break;
        case 2:
          boolean yesBoolean1 = getUsedFlagData(obj1.getUsedFlag(), CoCWPUsedFlag.YES.getDbType());
          boolean yesBoolean2 = getUsedFlagData(obj2.getUsedFlag(), CoCWPUsedFlag.YES.getDbType());
          compareResult = ApicUtil.compareBoolean(yesBoolean1, yesBoolean2);
          break;
        case 3:
          boolean noBoolean1 = getUsedFlagData(obj1.getUsedFlag(), CoCWPUsedFlag.NO.getDbType());
          boolean noBoolean2 = getUsedFlagData(obj2.getUsedFlag(), CoCWPUsedFlag.NO.getDbType());
          compareResult = ApicUtil.compareBoolean(noBoolean1, noBoolean2);
          break;
        case 4:
          compareResult = ApicUtil.compareBoolean(obj1.isAtChildLevel(), obj2.isAtChildLevel());
          break;
        default:
          break;
      }
      return compareResult;
    };
  }


  /**
   * @param usedFlagStr
   * @param flagString
   * @return
   */
  private Boolean getUsedFlagData(final String usedFlagStr, final String cocWPUsedFlagRef) {
    if (CommonUtils.isEqual(usedFlagStr, cocWPUsedFlagRef)) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  private void createToolBarAction() {

    this.toolbarManager = new ToolBarManager(SWT.FLAT);
    this.toolBarActionSet = new PIDCCocWpToolbarActionSet(this.customFilterGridLayer, this);
    final Separator separator = new Separator();

    this.toolBarActionSet.showAllCocWpAction(this.toolbarManager, this.toolBarFilters);

    this.toolbarManager.add(separator);

    this.toolBarActionSet.newCocWpFilter(this.toolbarManager, this.toolBarFilters);
    this.toolBarActionSet.usedNotDefinedFilterAction(this.toolbarManager, this.toolBarFilters);
    this.toolBarActionSet.usedFilterAction(this.toolbarManager, this.toolBarFilters);
    this.toolBarActionSet.notUsedFilterAction(this.toolbarManager, this.toolBarFilters);

    this.toolbarManager.add(separator);

    this.toolBarActionSet.variantFilterAction(this.toolbarManager, this.toolBarFilters);
    this.toolBarActionSet.pidcLevelFilterAction(this.toolbarManager, this.toolBarFilters);

    addResetAllFiltersAction();

    this.toolbarManager.update(true);

    Composite toolbarComposite = this.editor.getToolkit().createComposite(this.innerSection);
    toolbarComposite.setBackground(null);

    this.toolbarManager.createControl(toolbarComposite);

    this.innerSection.setTextClient(toolbarComposite);
  }

  /**
   * Add reset filter button
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.customFilterGridLayer);
    addResetFiltersAction();
  }


  private void createTable() {
    IConfigRegistry configRegistry = new ConfigRegistry();

    configureColumns();

    loadCocWpDetails();

    PidcCoCWpNattableColumnConverter columnConverter = new PidcCoCWpNattableColumnConverter();

    this.customFilterGridLayer = new CustomFilterGridLayer<>(configRegistry, this.cocWpSet, this.propertyToLabelMap,
        this.columnWidthMap, getNattableComparator(0), columnConverter, this, null, new GroupByModel(), null, false,
        true, null, null, false);

    this.toolBarFilters = new PIDCCocWpToolBarFilters();

    this.columnFilterMatcher = new PidcCoCWpNattableColumnFilterMatcher();

    this.customFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.columnFilterMatcher);
    this.nattable = new CustomNATTable(this.innerForm.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        this.customFilterGridLayer, false, getClass().getSimpleName(), this.propertyToLabelMap);

    this.customFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());

    this.nattable.setConfigRegistry(configRegistry);

    this.nattable.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.nattable.addConfiguration(new SingleClickSortConfiguration());

    this.nattable.addConfiguration(new CustomNatTableStyleConfiguration());

    this.nattable.addConfiguration(new PidcCoCWpNattableEditConfiguration());

    this.nattable.addConfiguration(new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry confRegistry) {
        // Shade the row to be slightly darker than the blue background. final
        Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        confRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);

        registerConfigLabelsOnColumns((ColumnOverrideLabelAccumulator) PIDCCocWpPage.this.customFilterGridLayer
            .getBodyDataLayer().getConfigLabelAccumulator());
        registerCheckBoxEditor(confRegistry);

      }

    });

    this.nattable.addConfiguration(new AbstractHeaderMenuConfiguration(this.nattable) {

      @Override
      protected PopupMenuBuilder createColumnHeaderMenu(final NatTable natTable) {
        return super.createColumnHeaderMenu(natTable).withHideColumnMenuItem().withShowAllColumnsMenuItem()
            .withAutoResizeSelectedColumnsMenuItem().withColumnRenameDialog().withClearAllFilters()
            .withMenuItemProvider((natTbl, popupMenu) -> {
              MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
              menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
              menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
              menuItem.setEnabled(true);
              menuItem.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent event) {
                  PIDCCocWpPage.this.resetState = true;
                  PIDCCocWpPage.this.reconstructNatTable();
                }
              });
            });
      }
    });

    DataLayer bodyDataLayer = this.customFilterGridLayer.getBodyDataLayer();

    IRowDataProvider<IProjectCoCWP> bodyDataProvider =
        (IRowDataProvider<IProjectCoCWP>) bodyDataLayer.getDataProvider();

    this.rowSelectionProvider = new RowSelectionProvider<>(
        this.customFilterGridLayer.getBodyLayer().getSelectionLayer(), bodyDataProvider, false);

    PidcCoCWpNattableLabelAccumulator labelAccumulator =
        new PidcCoCWpNattableLabelAccumulator(this.customFilterGridLayer);
    bodyDataLayer.setConfigLabelAccumulator(labelAccumulator);

    // add config label for column header group data
    this.customFilterGridLayer.getColumnGroupHeaderLayer().setConfigLabelAccumulator(
        new ColumnOverrideLabelAccumulator(this.customFilterGridLayer.getColumnHeaderDataLayer()) {

          @Override
          public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition,
              final int rowPosition) {
            super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
            addConfigLabelForHeader(configLabels, rowPosition);
          }


        });

    // add config label for column header data
    this.customFilterGridLayer.getColumnHeaderDataLayer().setConfigLabelAccumulator(
        new ColumnOverrideLabelAccumulator(this.customFilterGridLayer.getColumnHeaderDataLayer()) {

          @Override
          public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition,
              final int rowPosition) {
            super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
            addConfigLabelForHeader(configLabels, rowPosition);
          }

        });

    this.nattable.configure();

    // Load the saved state of NAT table
    loadState();

    this.nattable.addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent event) {
        saveState();
      }

      @Override
      public void focusGained(final FocusEvent event) {
        // NA

      }
    });
    this.nattable.addDisposeListener(event -> saveState());

    addRightClickOption();

    addMouseListener();

    attachToolTip();

    groupColumns();

  }

  /**
  *
  */
  private void reconstructNatTable() {


    this.nattable.dispose();
    this.propertyToLabelMap.clear();

    this.customFilterGridLayer = null;
    if (this.toolbarManager != null) {
      this.toolbarManager.removeAll();
    }
    if (this.form.getToolBarManager() != null) {
      this.form.getToolBarManager().removeAll();
    }
    createTable();
    // First the form's body is repacked and then the section is repacked
    // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable
    this.form.getBody().pack();
    this.section.layout();

    if (!this.filterTxt.getText().isEmpty()) {
      this.filterTxt.setText(this.filterTxt.getText());
    }

    if (this.nattable != null) {
      this.nattable.doCommand(new StructuralRefreshCommand());
      this.nattable.doCommand(new VisualRefreshCommand());
      this.nattable.refresh();
    }
  }

  /**
   * @param configLabels
   * @param rowPosition
   */
  private void addConfigLabelForHeader(final LabelStack configLabels, final int rowPosition) {
    // enable column header styling (font..)
    int rowCount = PIDCCocWpPage.this.customFilterGridLayer.getColumnHeaderDataProvider().getRowCount();
    if ((rowPosition < rowCount)) {
      configLabels.addLabel(ApicUiConstants.COC_WP_HEADER_LABEL);
    }
  }

  /**
   * Mouse listener to the table viewer
   */
  private void addMouseListener() {
    this.nattable.addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(final MouseEvent mouseEvent) {
        // NA
      }

      @Override
      public void mouseDown(final MouseEvent mouseEvent) {
        if (mouseEvent.button == LEFT_MOUSE_CLICK) {
          // store the event
          PIDCCocWpPage.this.clickEventVal = LEFT_MOUSE_CLICK;
        }
      }

      @Override
      public void mouseDoubleClick(final MouseEvent mouseEvent) {
        // NA
      }

    });
  }


  private void registerCheckBoxEditor(final IConfigRegistry configRegistry) {

    for (int columnIndex : checkBoxColumns) {
      Style cellStyleUC = new Style();
      cellStyleUC.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
      configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleUC, NORMAL,
          CHECK_BOX_CONFIG_LABEL + "_" + columnIndex);
      configRegistry.registerConfigAttribute(CELL_PAINTER, new CheckBoxPainter(), NORMAL,
          CHECK_BOX_CONFIG_LABEL + "_" + columnIndex);
      configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
          new DefaultBooleanDisplayConverter(), NORMAL, CHECK_BOX_CONFIG_LABEL + "_" + columnIndex);

      PIDCCompareNatTableCheckBoxCellEditor checkBoxCellEditor = new PIDCCompareNatTableCheckBoxCellEditor() {

        /**
         * {@inheritDoc}
         */
        @Override
        protected Control activateCell(final Composite parentForActivateCell, final Object originalCanonicalValue) {
          IStructuredSelection selection =
              (IStructuredSelection) PIDCCocWpPage.this.rowSelectionProvider.getSelection();

          if (!selection.isEmpty()) {

            List<IProjectCoCWP> selIProjectCoCWPList = selection.toList();
            checkboxEditor(columnIndex, selIProjectCoCWPList);
          }
          return super.activateCell(parentForActivateCell, originalCanonicalValue);
        }


      };
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
          DisplayMode.EDIT, CHECK_BOX_CONFIG_LABEL + "_" + columnIndex);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, checkBoxCellEditor, DisplayMode.EDIT,
          CHECK_BOX_EDITOR_CNG_LBL + "_" + columnIndex);
    }

  }

  /**
   * Method for editing columns with check box
   *
   * @param checkboxColName
   * @param selIProjectCoCWPList
   */
  private void checkboxEditor(final int checkboxColIndex, final List<IProjectCoCWP> selIProjectCoCWPList) {

    final PIDCActionSet pidcActionSet = new PIDCActionSet();

    // if PIDC is unlocked,user has write access and the selected CoC Wp is not a deleted one,then update the check box
    // selection based on user input
    if (PIDCCocWpPage.this.clickEventVal == LEFT_MOUSE_CLICK) {
      // clickEventValue is set to 0 in order to prevent the message dialog to appear again due to mouse
      // up/down event
      PIDCCocWpPage.this.clickEventVal = 0;
      if (pidcActionSet.checkPIDCLocked(PIDCCocWpPage.this.selectedPidcVersion)) {
        boolean isUserHasWriteAccess = pidcActionSet.checkUserHasWriteAccess(PIDCCocWpPage.this.selectedPidcVersion);
        if (isUserHasWriteAccess && (selIProjectCoCWPList.size() == 1) && !selIProjectCoCWPList.get(0).isDeleted()) {
          performCheckBoxActions(checkboxColIndex, selIProjectCoCWPList);
        }
        else {
          StringBuilder errorMessage = new StringBuilder(
              isUserHasWriteAccess ? "The selected CoC WorkPackage is a deleted one." : "User has No Write Access.");
          errorMessage.append(" Cannot edit this WorkPackage");
          CDMLogger.getInstance().errorDialog(errorMessage.toString(), Activator.PLUGIN_ID);
        }
      }
    }
  }

  /**
   * @param checkboxColIndex
   * @param selIProjectCoCWPList
   */
  private void performCheckBoxActions(final int checkboxColIndex, final List<IProjectCoCWP> selIProjectCoCWPList) {
    if (PIDCCocWpPage.this.cocWpBO.isUsedFlagCol(checkboxColIndex)) {
      PIDCCocWpPage.this.cocWpBO.editUsedFlagViaChkBox(checkboxColIndex, selIProjectCoCWPList);
    }
    else if ((checkboxColIndex == ApicConstants.COC_WP_LEVEL_COLUMN_INDEX)) {
      IProjectCoCWP iProjectCoCWP = selIProjectCoCWPList.get(0);
      if (iProjectCoCWP instanceof PidcVersCocWp) {
        PidcVersCocWp pidcVersCocWp = (PidcVersCocWp) iProjectCoCWP;
        pidcVersionCheckBoxActions(pidcVersCocWp);
      }
      else if (iProjectCoCWP instanceof PidcVariantCocWp) {
        PidcVariantCocWp pidcVariantCocWp = (PidcVariantCocWp) iProjectCoCWP;
        pidcVariantCheckBoxActions(pidcVariantCocWp);
      }
    }
  }

  /**
   * Check box actions for 1. Moving Coc Wp from Variant level to Sub Variant level, 2. Moving Coc Wp from Sub variant
   * level to Variant level
   *
   * @param iProjectCoCWP
   */
  private void pidcVariantCheckBoxActions(final PidcVariantCocWp pidcVariantCocWp) {
    // If Coc WP is already moved from Variant to sub variant, move back to Variant level
    if (pidcVariantCocWp.isAtChildLevel()) {
      this.cocWpBO.moveFromSubVariantToVariant(pidcVariantCocWp, getSelectedPidcVariant());
    }
    else if (this.cocWpBO.isSubVariantAvailForGivenVar(getSelectedPidcVariant().getId())) {
      this.cocWpBO.moveFromVariantToSubVariant(pidcVariantCocWp);
    }
  }

  /**
   * Check box actions for 1. Moving Coc Wp from PIDC level to Variant level, 2. Moving Coc Wp from variant level to
   * PIDC level
   *
   * @param iProjectCoCWP
   */
  private void pidcVersionCheckBoxActions(final PidcVersCocWp pidcVersCocWp) {
    // If Coc WP is already moved from PIDC to variant, move back to PIDC level
    if (pidcVersCocWp.isAtChildLevel()) {
      this.cocWpBO.moveFromVariantToPidc(pidcVersCocWp.getWPDivId());
    }
    else if (this.cocWpBO.isVariantAvailable()) {
      this.cocWpBO.moveFromPidcToVariant(pidcVersCocWp);
    }
  }

  /**
   *
   */
  private void addRightClickOption() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);

    final PIDCCocWpActionSet actionSet = new PIDCCocWpActionSet(this.cocWpBO, this);
    menuMgr.addMenuListener(mgr -> {
      final IStructuredSelection selection = (IStructuredSelection) this.rowSelectionProvider.getSelection();
      if (selection != null) {
        final Object firstElement = selection.getFirstElement();
        createRightClickMenuAction(menuMgr, actionSet, selection, firstElement);
      }
    });

    final Menu menu = menuMgr.createContextMenu(this.nattable.getShell());
    this.nattable.setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.rowSelectionProvider);
  }

  /**
  *
  */
  private void loadCocWpDetails() {
    PidcVersCocWpData cocWpData = null;
    boolean activePage = false;
    if (this.editor.getActivePageInstance() instanceof PIDCCocWpPage) {
      activePage = true;
    }
    if (this.cocWpBO.validateQnaireConfigAttrValueInPidcVersion(activePage)) {
      cocWpData = this.cocWpBO.getPidcVersCocWpData();
    }
    this.cocWpSet = new TreeSet<>();
    if (cocWpData != null) {
      if (this.isVarNodeSelected) {
        Map<Long, PidcVariantCocWp> variantCocWpMap =
            cocWpData.getPidcVarCocWpMap().get(getSelectedPidcVariant().getId());
        if (CommonUtils.isNotEmpty(variantCocWpMap)) {
          this.cocWpSet = new TreeSet<>(variantCocWpMap.values());
        }
      }
      else if (this.isSubVarNodeSelected) {
        Map<Long, PidcSubVarCocWp> subVariantCocWpMap =
            cocWpData.getPidcSubVarCocWpMap().get(getSelectedPidcSubVariant().getId());
        if (CommonUtils.isNotEmpty(subVariantCocWpMap)) {
          this.cocWpSet = new TreeSet<>(subVariantCocWpMap.values());
        }
      }
      // If selected Node is PIDC node
      else {
        this.cocWpSet = new TreeSet<>(cocWpData.getPidcVersCocWpMap().values());
      }
    }
  }

  /**
   * @param menuMgr
   * @param actionSet
   * @param selection
   * @param firstElement
   */
  private void createRightClickMenuAction(final MenuManager menuMgr, final PIDCCocWpActionSet actionSet,
      final IStructuredSelection selection, final Object firstElement) {
    if (firstElement instanceof IProjectCoCWP) {
      IProjectCoCWP iProjectCoCWP = (IProjectCoCWP) firstElement;
      if (!selection.isEmpty()) {

        actionSet.addUsedFlagYesMenu(menuMgr, selection);
        actionSet.addUsedFlagNoMenu(menuMgr, selection);
        actionSet.addUsedFlagNotDefinedMenu(menuMgr, selection);
        menuMgr.add(new Separator());

        if (iProjectCoCWP instanceof PidcVersCocWp) {
          actionSet.addMoveToVariantMenu(menuMgr, selection);
        }
        else if (iProjectCoCWP instanceof PidcVariantCocWp) {
          actionSet.addMoveToPidcMenuFromVariant(menuMgr, selection);
          menuMgr.add(new Separator());
          actionSet.addMoveToSubvariantMenuFromVariant(menuMgr, selection);
        }
        else if (iProjectCoCWP instanceof PidcSubVarCocWp) {
          actionSet.addMoveToVariantMenuFromSubVariant(menuMgr, selection);
        }
      }
    }
  }


  /**
   * Method to add checkboxes to columns
   *
   * @param columnLabelAccumulator
   */
  private void registerConfigLabelsOnColumns(final ColumnOverrideLabelAccumulator columnLabelAccumulator) {

    for (int checkBoxColumn : checkBoxColumns) {
      columnLabelAccumulator.registerColumnOverrides(checkBoxColumn, CHECK_BOX_EDITOR_CNG_LBL + "_" + checkBoxColumn,
          CHECK_BOX_CONFIG_LABEL + "_" + checkBoxColumn);
    }

  }

  /**
   * Load saved state of NAT table
   */
  private void loadState() {
    try {
      if (this.resetState) {
        this.nattable.resetState();
      }
      this.nattable.loadState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().error(ioe.getMessage(), ioe, Activator.PLUGIN_ID);
    }

  }

  private void saveState() {
    try {
      this.nattable.saveState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().error(ioe.getMessage(), ioe, Activator.PLUGIN_ID);
    }
  }

  private void groupColumns() {
    ColumnGroupModel columnGroupModel;
    columnGroupModel = this.customFilterGridLayer.getColumnGroupModel();
    columnGroupModel.addColumnsIndexesToGroup("Used", ApicConstants.COC_WP_UNDEFINED_COLUMN_INDEX,
        ApicConstants.COC_WP_YES_COLUMN_INDEX, ApicConstants.COC_WP_NO_COLUMN_INDEX);
    columnGroupModel.setColumnGroupCollapseable(ApicConstants.COC_WP_UNDEFINED_COLUMN_INDEX, false);
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
   *
   */
  private void createFilterTxt() {
    GridData gridData = getFilterTxtGridData();
    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      final String text = PIDCCocWpPage.this.filterTxt.getText().trim();
      PIDCCocWpPage.this.columnFilterMatcher.setFilterText(text, true);
      PIDCCocWpPage.this.customFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
      PIDCCocWpPage.this.customFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(PIDCCocWpPage.this.customFilterGridLayer.getSortableColumnHeaderLayer()));
      setStatusBarMessage(false);
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if (CommonUtils.isEqual(getSite().getPage().getActiveEditor(), getEditor()) &&
        (part instanceof PIDCDetailsViewPart)) {
      pidcCocTreeViewerSelListener(selection);
    }

  }

  /**
   * pidc details tree listener
   *
   * @param selection
   */
  private void pidcCocTreeViewerSelListener(final ISelection selection) {
    if ((selection instanceof IStructuredSelection) && !selection.isEmpty() &&
        (((IStructuredSelection) selection).size() == CommonUIConstants.SINGLE_SELECTION)) {
      final Object first = ((IStructuredSelection) selection).getFirstElement();
      pidcDetailsNodeSelection(first);
    }

  }

  /**
   * @param first node object
   */
  private void pidcDetailsNodeSelection(final Object first) {
    if (!CommonUtils.isEqual(this.lastSelection, first)) {
      Long editorPidcVersId = this.selectedPidcVersion.getId();
      if ((first instanceof PidcVersion) && (editorPidcVersId.equals(((PidcVersion) first).getId()))) {
        setCocPIDCInfo();
      }
      else if ((first instanceof PidcVariant) ||
          ((first instanceof PIDCDetailsNode) && ((PIDCDetailsNode) first).isVariantNode())) {
        setCocVariantInfo(first);
      }
      else if ((first instanceof PidcSubVariant) &&
          (editorPidcVersId.equals(((PidcSubVariant) first).getPidcVersionId()))) {
        setCocSubVariantInfo(first);
      }
    }

    this.lastSelection = first;
    this.section.layout();
    if (this.editor.isCocWpPage()) {
      this.editor.updateStatusBar(false, true, this.cocWpSet.size(),
          CommonUtils.isNotNull(this.customFilterGridLayer)
              ? this.customFilterGridLayer.getRowHeaderLayer().getPreferredRowCount()
              : ApicConstants.COC_WP_EMPTY_ROW_COUNT);
    }

  }

  /**
   * This method sets PIDC information on selection PIDC details treeviewer
   *
   * @param first
   */
  private void setCocPIDCInfo() {

    setVarNodeSelected(false);
    setSubVarNodeSelected(false);
    this.nonScrollableForm.setText(getCocWpTitle());
    this.section.setDescription(getDescriptionForSection());

    this.customFilterGridLayer.getColumnHeaderLayer().renameColumnIndex(ApicConstants.COC_WP_LEVEL_COLUMN_INDEX,
        VAR_LEVEL_COL_NAME);

    refreshNatTable();

  }

  /**
   * @return Title of the page
   */
  private String getCocWpTitle() {


    String strTitle = this.pidcVersion.getName();
    if (this.isVarNodeSelected) {
      strTitle = this.pidcVersion.getName() + " " + STR_ARROW + this.selectedPidcVariant.getName();
    }
    if (this.isSubVarNodeSelected) {
      strTitle = this.pidcVersion.getName() + " " + STR_ARROW + this.selectedPidcVariant.getName() + STR_ARROW +
          this.selectedPidcSubVariant.getName();
    }

    return strTitle;
  }


  /**
   * This method sets PIDC Variant information on selection PIDC details treeviewer
   *
   * @param first
   */
  private void setCocVariantInfo(final Object first) {


    PidcVariant selPIDCVariant = null;
    if (first instanceof PidcVariant) {
      selPIDCVariant = (PidcVariant) first;
    }
    else if ((first instanceof PIDCDetailsNode) && ((PIDCDetailsNode) first).isVariantNode()) {
      PIDCDetailsNode node = (PIDCDetailsNode) first;
      selPIDCVariant = node.getPidcVariant();
    }
    if (selPIDCVariant != null) {
      String titleText = this.pidcVersion.getName() + " " + STR_ARROW + selPIDCVariant.getName();
      this.nonScrollableForm.setText(titleText);

      setSelectedPidcVariant(selPIDCVariant);
      setVarNodeSelected(true);
      setSubVarNodeSelected(false);
      this.section.setDescription(getDescriptionForSection());

      this.customFilterGridLayer.getColumnHeaderLayer().renameColumnIndex(ApicConstants.COC_WP_LEVEL_COLUMN_INDEX,
          SUBVAR_LEVEL_COL_NAME);

      refreshNatTable();
    }
  }

  /**
   * This method sets PIDC Sub-Variant information on selection PIDC details treeviewer
   *
   * @param first
   */
  private void setCocSubVariantInfo(final Object first) {
    final PidcSubVariant selPIDCSubVariant = (PidcSubVariant) first;
    PidcVariant pidcVariant = this.pidcDataHandler.getVariantMap().get(selPIDCSubVariant.getPidcVariantId());

    setSelectedPidcVariant(pidcVariant);
    String titleText =
        this.pidcVersion.getName() + " " + STR_ARROW + pidcVariant.getName() + STR_ARROW + selPIDCSubVariant.getName();
    this.nonScrollableForm.setText(titleText);

    setVarNodeSelected(false);
    setSelectedPidcSubVariant(selPIDCSubVariant);
    setSubVarNodeSelected(true);


    this.section.setDescription(getDescriptionForSection());
    refreshNatTable();

  }

  /**
   * refresh the table
   */
  private void refreshNatTable() {
    if (this.nattable != null) {
      this.customFilterGridLayer.getEventList().clear();
      loadCocWpDetails();
      this.customFilterGridLayer.getEventList().addAll(this.cocWpSet);
      this.nattable.doCommand(new StructuralRefreshCommand());
      this.nattable.doCommand(new VisualRefreshCommand());
      this.nattable.refresh();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.toolbarManager;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setStatusBarMessage(final boolean outlineSelection) {
    this.editor.updateStatusBar(this.cocWpSet.size(), CommonUtils.isNotNull(this.customFilterGridLayer)
        ? this.customFilterGridLayer.getRowHeaderLayer().getPreferredRowCount() : ApicConstants.COC_WP_EMPTY_ROW_COUNT);
  }


  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMessage(outlineSelection);
    this.editor.updateStatusBar(this.cocWpSet.size(), CommonUtils.isNotNull(this.customFilterGridLayer)
        ? this.customFilterGridLayer.getRowHeaderLayer().getPreferredRowCount() : ApicConstants.COC_WP_EMPTY_ROW_COUNT);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    refreshNatTable();
    if (CommonUtils.isNotNull(getPidcLockAction())) {
      getPidcLockAction().setActionProperties();
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
  public PidcCocWPDataHandler getDataHandler() {
    return ((PIDCEditorInput) getEditorInput()).getCocWPDataHandler();
  }

  /**
   * @return the rowSelectionProvider
   */
  public RowSelectionProvider<IProjectCoCWP> getRowSelectionProvider() {
    return this.rowSelectionProvider;
  }


  /**
   * @param rowSelectionProvider the rowSelectionProvider to set
   */
  public void setRowSelectionProvider(final RowSelectionProvider<IProjectCoCWP> rowSelectionProvider) {
    this.rowSelectionProvider = rowSelectionProvider;
  }


  /**
   * @return the selectedPidcVariant
   */
  public PidcVariant getSelectedPidcVariant() {
    return this.selectedPidcVariant;
  }


  /**
   * @param selectedPidcVariant the selectedPidcVariant to set
   */
  public void setSelectedPidcVariant(final PidcVariant selectedPidcVariant) {
    this.selectedPidcVariant = selectedPidcVariant;
  }


  /**
   * @return the selectedPidcVersion
   */
  public PidcVersion getSelectedPidcVersion() {
    return this.selectedPidcVersion;
  }


  /**
   * @param selectedPidcVersion the selectedPidcVersion to set
   */
  public void setSelectedPidcVersion(final PidcVersion selectedPidcVersion) {
    this.selectedPidcVersion = selectedPidcVersion;
  }


  /**
   * @return the selectedPidcSubVariant
   */
  public PidcSubVariant getSelectedPidcSubVariant() {
    return this.selectedPidcSubVariant;
  }


  /**
   * @param selectedPidcSubVariant the selectedPidcSubVariant to set
   */
  public void setSelectedPidcSubVariant(final PidcSubVariant selectedPidcSubVariant) {
    this.selectedPidcSubVariant = selectedPidcSubVariant;
  }


  /**
   * @return the isVarNodeSelected
   */
  public boolean isVarNodeSelected() {
    return this.isVarNodeSelected;
  }


  /**
   * @param isVarNodeSelected the isVarNodeSelected to set
   */
  public void setVarNodeSelected(final boolean isVarNodeSelected) {
    this.isVarNodeSelected = isVarNodeSelected;
  }


  /**
   * @return the isSubVarNodeSelected
   */
  public boolean isSubVarNodeSelected() {
    return this.isSubVarNodeSelected;
  }


  /**
   * @param isSubVarNodeSelected the isSubVarNodeSelected to set
   */
  public void setSubVarNodeSelected(final boolean isSubVarNodeSelected) {
    this.isSubVarNodeSelected = isSubVarNodeSelected;
  }


  /**
   * @return the pidcLockAction
   */
  public PIDCSessionLockAction getPidcLockAction() {
    return this.pidcLockAction;
  }

  /**
   * @return the toolBarFilterStateMap
   */
  public Map<String, Boolean> getToolBarFilterStateMap() {
    return this.toolBarFilterStateMap;
  }
}
