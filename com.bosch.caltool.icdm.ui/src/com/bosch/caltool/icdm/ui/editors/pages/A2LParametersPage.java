/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.columnChooser.command.DisplayColumnChooserCommandHandler;
import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommand;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;
import org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseClickAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;

import com.bosch.calcomp.labfunwriter.LabFunWriterConstants.OUTPUT_FILE_TYPE;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.apic.ui.dialogs.PasswordDialog;
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParamInfo;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter.SortColumns;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.vcdm.VcdmCalDataBO;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.actions.CDMCommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dialogs.CalDataViewerDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.DSTSelectionDialog;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.jobs.CDFFileExportJob;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.services.IA2lParamTable;
import com.bosch.caltool.icdm.common.ui.table.filters.A2LOutlineNatFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizard;
import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizardDialog;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstantValues;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;
import com.bosch.caltool.icdm.model.vcdm.VCDMDSTRevision;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewActionSet;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.action.LabFunExportAction;
import com.bosch.caltool.icdm.ui.action.ParamNatToolBarActionSet;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditor;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditorInput;
import com.bosch.caltool.icdm.ui.editors.pages.natsupport.ParamPageLabelAccumulator;
import com.bosch.caltool.icdm.ui.editors.pages.natsupport.ParamSsdClassConfiguration;
import com.bosch.caltool.icdm.ui.table.filters.AllColumnFilterMatcher;
import com.bosch.caltool.icdm.ui.table.filters.ParamNatToolBarFilter;
import com.bosch.caltool.icdm.ui.util.IMessageConstants;
import com.bosch.caltool.icdm.ui.util.Messages;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.labfunparser.exception.ParserException;
import com.bosch.caltool.labfunparser.textparser.InputFileParser;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;
import com.bosch.caltool.nattable.CustomColumnHeaderLayerConfiguration;
import com.bosch.caltool.nattable.CustomColumnHeaderStyleConfiguration;
import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomGroupByDataLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * @author jvi6cob
 */
public class A2LParametersPage extends AbstractGroupByNatFormPage implements ISelectionListener, IA2lParamTable {

  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Base Form instance
   */
  private Form baseForm;

  /**
   * Filter text instance
   */
  private Text filterTxt;
  private A2LOutlineNatFilter outlineNatFilter;

  /**
   * Editor instance
   */
  private final A2LContentsEditor editor;
  /**
   * Non scrollable form
   */
  private ScrolledForm nonScrollableForm;
  private ParamNatToolBarActionSet toolBarActionSet;
  private ParamNatToolBarFilter toolBarFilters;
  /**
   * totTableRowCount contains the Total number of rows set to NatTable Viewer.Used to update the StatusBar Message
   */
  private int totTableRowCount;
  private CustomFilterGridLayer paramFilterGridLayer;
  private CustomNATTable natTable;
  private RowSelectionProvider<A2LSystemConstantValues> selectionProvider;
  private AllColumnFilterMatcher<A2LParameter> allColumnFilterMatcher;
  private SortedSet<A2LParameter> sortedParamSet;
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  // List to store selected files/DSTs for data import
  private final List<String> selDataSources = new ArrayList<>();

  // ICDM-841
  // List to store selected files
  private final List<String> selFilesList = new ArrayList<>();
  // LAB file labels set
  private final Set<String> labels = new TreeSet<>();
  // Action for clearing data loaded from file/DST
  private Action clearDataAction;
  // Action for clearing the LAB files loaded.
  private Action clearLABAction;
  private ToolBarManager toolBarManager;
  /**
   * Nat table selection size
   */
  private static final int NAT_TABLE_SEL_SIZE = 1;

  private boolean isPreCalibDataLoaded;
  private GroupByHeaderLayer groupByHeaderLayer;
  private boolean resetState;
  private Map<Integer, String> propertyToLabelMap;
  private final A2LContentsEditorInput a2lContentsEditorInput;

  private SortedSet<A2LParameter> selectedA2lParams;

  /**
   * The Parameterized Constructor
   *
   * @param editor instance
   * @param a2lDataProvider instance
   */
  public A2LParametersPage(final FormEditor editor) {
    super(editor, "ParametersPage", /* "Nat" + */Messages.getString("ParametersFormPage.label")); //$NON-NLS-1$ //$NON-NLS-2$
    this.editor = (A2LContentsEditor) editor;
    this.a2lContentsEditorInput = (A2LContentsEditorInput) (editor.getEditorInput());
  }

  // ICDM-249
  @Override
  public void createPartControl(final Composite parent) {

    // ICDM-249
    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = this.editor.getToolkit().createScrolledForm(parent);
    // instead of editor.getToolkit().createScrolledForm(parent); in superclass
    // formToolkit is obtained from managed form to create form within section
    ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
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
    FormToolkit formToolkit = managedForm.getToolkit();
    createComposite(formToolkit);
    // add listeners
    getSite().getPage().addSelectionListener(this);
  }


  /**
   * @param toolkit This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {
    this.composite = this.nonScrollableForm.getBody();
    this.composite.setLayout(new GridLayout());
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    createSection(toolkit);
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * @param toolkit This method initializes section
   */
  private void createSection(final FormToolkit toolkit) {
    this.section = toolkit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("Parameters");
    this.section.setExpanded(true);
    // ICDM-183
    this.section.getDescriptionControl().setEnabled(false);
    createForm(toolkit);
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.baseForm);
  }

  /**
   * @param toolkit This method initializes form
   */

  private void createForm(final FormToolkit toolkit) {
    this.baseForm = toolkit.createForm(this.section);
    this.filterTxt = toolkit.createText(this.baseForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    createFilterTxt();

    this.baseForm.getBody().setLayout(new GridLayout());
    createTable();
  }

  /**
   *
   */
  private void createTable() {

    Map<String, A2LParameter> a2lParamMap = this.a2lContentsEditorInput.getA2lFileInfoBO().getA2lParamMap(null);


    this.sortedParamSet = new TreeSet<>(a2lParamMap.values());


    this.totTableRowCount = this.sortedParamSet.size();
    this.propertyToLabelMap = new HashMap<>();
    this.propertyToLabelMap.put(0, "");
    this.propertyToLabelMap.put(1, "FC");
    this.propertyToLabelMap.put(2, "Parameter");
    this.propertyToLabelMap.put(3, "Type");
    this.propertyToLabelMap.put(4, "Unit");
    this.propertyToLabelMap.put(5, "Class");
    this.propertyToLabelMap.put(6, "Codeword");
    this.propertyToLabelMap.put(7, "Long Name");
    this.propertyToLabelMap.put(8, "Value");
    this.propertyToLabelMap.put(9, "Status");

    // The below map is used by NatTable to Map Columns with their respective widths
    Map<Integer, Integer> columnWidthMap = new HashMap<>();
    columnWidthMap.put(0, 15);
    columnWidthMap.put(1, 15);
    columnWidthMap.put(2, 10);
    columnWidthMap.put(3, 10);
    columnWidthMap.put(4, 10);
    columnWidthMap.put(5, 10);
    columnWidthMap.put(6, 10);
    columnWidthMap.put(7, 15);
    columnWidthMap.put(8, 10);
    columnWidthMap.put(9, 10);

    // NatInputToColumnConverter is used to convert A2LSystemConstantValues (which is given as input to nattable viewer)
    // to the respective column values
    AbstractNatInputToColumnConverter natInputToColumnConverter = new ParamNatInputToColumnConverter();
    IConfigRegistry configRegistry = new ConfigRegistry();

    GroupByModel groupByModel = new GroupByModel();
    // A Custom Filter Grid Layer is constructed
    this.paramFilterGridLayer = new CustomFilterGridLayer(configRegistry, this.sortedParamSet, this.propertyToLabelMap,
        columnWidthMap, getParamComparator(SortColumns.SORT_CHAR_DEFFUNC), natInputToColumnConverter, this,
        new CalDataViewerNatMouseClickAction(), groupByModel, null, true, true, null, null, false);
    A2LFile a2lFile = ((A2LContentsEditorInput) this.editor.getEditorInput()).getA2lFile();

    this.outlineNatFilter =
        new A2LOutlineNatFilter(this.paramFilterGridLayer, a2lFile, this.a2lContentsEditorInput.getA2lWPInfoBO(), null);
    this.outlineNatFilter.setWpType(this.a2lContentsEditorInput.getA2lFileInfoBO().getMappingSourceID());
    this.paramFilterGridLayer.getFilterStrategy().setOutlineNatFilterMatcher(this.outlineNatFilter.getOutlineMatcher());

    this.toolBarFilters = new ParamNatToolBarFilter();
    this.paramFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());

    this.allColumnFilterMatcher = new AllColumnFilterMatcher<>(this.a2lContentsEditorInput.getA2lFileInfoBO());

    this.paramFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);

    this.groupByHeaderLayer = new GroupByHeaderLayer(groupByModel, this.paramFilterGridLayer,
        this.paramFilterGridLayer.getColumnHeaderDataProvider());
    compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, this.groupByHeaderLayer, 0, 0);
    compositeGridLayer.setChildLayer("Grid", this.paramFilterGridLayer, 0, 1);

    // ICDM-2439
    // add the label accumulator
    DataLayer bodyDataLayer = this.paramFilterGridLayer.getBodyDataLayer();
    IRowDataProvider<A2LParameter> bodyDataProvider = (IRowDataProvider<A2LParameter>) bodyDataLayer.getDataProvider();
    final ParamPageLabelAccumulator paramPageLabelAccumulator =
        new ParamPageLabelAccumulator(bodyDataLayer, bodyDataProvider);
    bodyDataLayer.setConfigLabelAccumulator(paramPageLabelAccumulator);

    A2LParametersPage.this.paramFilterGridLayer.getBodyDataLayer().getTreeRowModel().registerRowGroupModelListener(
        () -> A2LParametersPage.this.setStatusBarMessage(A2LParametersPage.this.groupByHeaderLayer, false));

    this.natTable = new CustomNATTable(this.baseForm.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        compositeGridLayer, false, this.getClass().getSimpleName(), this.propertyToLabelMap);

    try {
      this.natTable.setProductVersion(new CommonDataBO().getIcdmVersion());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }


    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(getGridData());
    this.natTable.addConfiguration(new GroupByHeaderMenuConfiguration(this.natTable, this.groupByHeaderLayer));
    this.natTable.addConfiguration(new CustomNatTableStyleConfiguration());
    this.natTable.addConfiguration(new FilterRowCustomConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry confRegistry) {
        super.configureRegistry(confRegistry);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        confRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });


    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(A2LParametersPage.this.natTable)
                .withColumnChooserMenuItem().withMenuItemProvider((natTbl, popupMenu) -> {
                  MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
                  menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
                  menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
                  menuItem.setEnabled(true);
                  menuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                      A2LParametersPage.this.resetState = true;
                      A2LParametersPage.this.reconstructNatTable();
                    }
                  });
                }).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });
    // ICDM-2439
    // add the edit configuration which will give images for ssd class column
    this.natTable.addConfiguration(new ParamSsdClassConfiguration());

    CustomColumnHeaderStyleConfiguration columnHeaderStyleConfiguration = new CustomColumnHeaderStyleConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry confRegistry) {
        // configure the painter
        confRegistry.registerConfigAttribute(CELL_PAINTER, this.cellPaintr, NORMAL, GridRegion.COLUMN_HEADER);
        confRegistry.registerConfigAttribute(CELL_PAINTER, this.cellPaintr, NORMAL, GridRegion.CORNER);

        // configure whether to render grid lines or not
        // e.g. for the BeveledBorderDecorator the rendering of the grid lines should be disabled
        confRegistry.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, this.rendrGridLines, NORMAL,
            GridRegion.COLUMN_HEADER);
        confRegistry.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, this.rendrGridLines, NORMAL,
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

        confRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, NORMAL,
            GridRegion.COLUMN_HEADER);
        confRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, NORMAL, GridRegion.CORNER);
      }
    };
    this.paramFilterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));

    this.paramFilterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));

    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable
        .addConfiguration(getCustomComparatorConfiguration(this.paramFilterGridLayer.getColumnHeaderDataLayer()));
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      @Override
      protected PopupMenuBuilder createColumnHeaderMenu(final NatTable natTbl) {
        return super.createColumnHeaderMenu(natTbl).withStateManagerMenuItemProvider();
      }
    });

    addRightClickMenu();

    createToolBarAction();
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
    this.natTable.addDisposeListener(event -> saveState());

    CustomDefaultBodyLayerStack bodyLayer = this.paramFilterGridLayer.getBodyLayer();
    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.paramFilterGridLayer.getColumnHeaderLayer(), this.paramFilterGridLayer.getColumnHeaderDataLayer(),
            null, null);
    this.natTable.registerCommandHandler(columnChooserCommandHandler);
    this.paramFilterGridLayer.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.natTable));

    this.selectionProvider = new RowSelectionProvider<>(this.paramFilterGridLayer.getBodyLayer().getSelectionLayer(),
        this.paramFilterGridLayer.getBodyDataProvider(), false);

    this.selectionProvider.addSelectionChangedListener(event -> {

      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      if (selection.getFirstElement() instanceof A2LParameter) {
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
    });

    // The below method is required to enable tootltip only for cells which contain not fully visible content
    attachToolTip(this.natTable);

    getSite().setSelectionProvider(this.selectionProvider);


    setDragSupport();
  }

  private class CalDataViewerNatMouseClickAction implements IMouseClickAction {

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final NatTable natTableObject, final MouseEvent event) {
      CompositeLayer compositeLayer = (CompositeLayer) natTableObject.getLayer();
      CustomFilterGridLayer customFilterGridLayer =
          (CustomFilterGridLayer) compositeLayer.getChildLayerByRegionName("Grid");
      final SelectionLayer selectionLayer = customFilterGridLayer.getBodyLayer().getSelectionLayer();
      int rowPosition =
          LayerUtil.convertRowPosition(natTableObject, natTableObject.getRowPositionByY(event.y), selectionLayer);
      Object rowObject = A2LParametersPage.this.paramFilterGridLayer.getBodyDataProvider().getRowObject(rowPosition);
      if (rowObject instanceof A2LParameter) {
        A2LParameter selectedParameter = (A2LParameter) rowObject;
        if (selectedParameter.getCalData() != null) {
          A2LContentsEditorInput a2lContEditorInput = (A2LContentsEditorInput) getEditorInput();
          final CalDataViewerDialog calDataViewerDialog = new CalDataViewerDialog(Display.getCurrent().getActiveShell(),
              selectedParameter.getCalData(), selectedParameter.getName(), a2lContEditorInput.getName());
          calDataViewerDialog.open();
        }
        else {
          MessageDialogUtils.getInfoMessageDialog("Info", "No Data available for the selected parameter");
        }
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExclusive() {
      return true;
    }

  }

  /**
   * Icdm-587
   */
  private void createToolBarAction() {

    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = this.toolBarManager.createControl(this.section);

    this.toolBarActionSet = new ParamNatToolBarActionSet(this, this.paramFilterGridLayer);

    final Separator separator = new Separator();

    // ICDM-2439
    // Filter For compliance parameters
    this.toolBarActionSet.complianceFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    // Filter For non compliance parameters
    this.toolBarActionSet.nonComplianceFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);

    // Filter For the compliance parameters
    this.toolBarActionSet.blackListFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    // Filter For the non compliance parameters
    this.toolBarActionSet.nonBlackListFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);

    // filter for read only parameters
    this.toolBarActionSet.readOnlyAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarActionSet.notReadOnlyAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);

    // filter for dependent attributes
    this.toolBarActionSet.depnParamAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarActionSet.notDepnParamAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);

    // Filter For the QSSD parameters
    this.toolBarActionSet.qSSDFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    // Filter For the non QSSD parameters
    this.toolBarActionSet.nonQSSDFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);


    // Filter For the Rivert - Class Type
    this.toolBarActionSet.rivetFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter For the Nail - Class Type
    this.toolBarActionSet.nailFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter For the Screw - Class Type
    this.toolBarActionSet.screwFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.classUndefinedFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);
    this.toolBarManager.add(separator);

    // Filter For the Yes - Code Type
    this.toolBarActionSet.codeYesFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter For the No - Code Type
    this.toolBarActionSet.codeNoFilterAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarManager.add(separator);
    this.toolBarManager.add(separator);
    // Filter for the rows with value loaded from source
    this.toolBarActionSet.createWithValueFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter for the rows without value loaded from source
    this.toolBarActionSet.createWithoutValueFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);
    this.toolBarManager.add(separator);
    // Filter for the rows with status loaded from source
    this.toolBarActionSet.createWithStatusFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter for the rows without status loaded from source
    this.toolBarActionSet.createWithoutStatusFilterAction(this.toolBarManager, this.toolBarFilters);
    // ICDM-841
    this.toolBarManager.add(separator);
    this.toolBarManager.add(separator);
    // Filter for the rows with lables in LAB files uploaded
    this.toolBarActionSet.createWithLABParamAction(this.toolBarManager, this.toolBarFilters);
    // Filter for the rows without lables in LAB files uploaded
    this.toolBarActionSet.createWithoutLABParamAction(this.toolBarManager, this.toolBarFilters);

    this.baseForm.getToolBarManager().add(exportToCdfx(false));

    loadCalibrationValFromFileAction();

    loadCalibrationValuesFromVcdmAction();

    clearAllCalibrationValuesAction();

    // ICDM-841
    this.baseForm.getToolBarManager().add(separator);
    this.baseForm.getToolBarManager().add(separator);

    loadLabFileAction();

    clearLabFileAction();

    this.baseForm.getToolBarManager().update(true);
    this.baseForm.setToolBarVerticalAlignment(SWT.TOP);

    this.toolBarManager.update(true);
    addResetAllFiltersAction();
    this.section.setTextClient(toolbar);
  }

  /**
   *
   */
  private void clearLabFileAction() {
    this.clearLABAction = new Action() {

      @Override
      public void run() {
        clearLABFiles();
        setFilterStatus();
      }
    };
    this.clearLABAction.setText("Clear LAB File");
    final ImageDescriptor clearLAB = ImageManager.getImageDescriptor(ImageKeys.CLEAR_LAB_16X16);
    this.clearLABAction.setImageDescriptor(clearLAB);
    this.clearLABAction.setEnabled(false);
    this.baseForm.getToolBarManager().add(this.clearLABAction);
  }

  /**
   *
   */
  private void loadLabFileAction() {
    final Action importLABAction = new Action() {

      @Override
      public void run() {
        importLABFile();
        setFilterStatus();
      }
    };
    importLABAction.setText("Load LAB File");
    final ImageDescriptor importLAB = ImageManager.getImageDescriptor(ImageKeys.UPLOAD_LAB_16X16);
    importLABAction.setImageDescriptor(importLAB);
    this.baseForm.getToolBarManager().add(importLABAction);
  }

  /**
   *
   */
  private void clearAllCalibrationValuesAction() {
    this.clearDataAction = new Action() {

      @Override
      public void run() {
        clearData();
        A2LParametersPage.this.isPreCalibDataLoaded = false;
        setFilterStatusForDataLoad();
      }
    };
    this.clearDataAction.setText("Clear all calibration values");
    final ImageDescriptor imageDescClearData = ImageManager.getImageDescriptor(ImageKeys.CLEAR_PARAM_16X16);
    this.clearDataAction.setImageDescriptor(imageDescClearData);
    this.baseForm.getToolBarManager().add(this.clearDataAction);
  }

  /**
   *
   */
  private void loadCalibrationValuesFromVcdmAction() {
    final Action importFromDSTAction = new Action() {

      @Override
      public void run() {
        // Icdm-515 Get the User info Details and Check if SSO is enabled
        CurrentUserBO currentUserBO = new CurrentUserBO();
        try {
          new LdapAuthenticationWrapper().getUserDetails(currentUserBO.getUserName());
        }
        catch (LdapException | ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
        try {
          // SSO is enabled and the password dialog to be opened
          if (!currentUserBO.hasPassword()) {
            final PasswordDialog passwordDialog = new PasswordDialog(Display.getDefault().getActiveShell());
            passwordDialog.open();
          }
          // Check password again
          if (currentUserBO.hasPassword()) {
            importDataSetsFromVCDM();
            setFilterStatusForDataLoad();
          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    };
    importFromDSTAction.setText("Load calibration values from vCDM");
    final ImageDescriptor imageDescDST = ImageManager.getImageDescriptor(ImageKeys.VCDM_16X16);
    importFromDSTAction.setImageDescriptor(imageDescDST);
    this.baseForm.getToolBarManager().add(importFromDSTAction);
  }

  /**
   *
   */
  private void loadCalibrationValFromFileAction() {
    final Action importDataFromFileAction = new Action() {

      @Override
      public void run() {
        importDataFromCDFX();
        setFilterStatusForDataLoad();
      }
    };
    importDataFromFileAction.setText("Load calibration values from FILE");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.IMPORT_PARAM_16X16);
    importDataFromFileAction.setImageDescriptor(imageDesc);
    this.baseForm.getToolBarManager().add(importDataFromFileAction);
  }

  /**
   *
   */
  private Action exportToCdfx(final boolean isSelectionFromContextMenu) {

    Action exportLabelsAsCDFxAction = new Action() {

      @Override
      public void run() {
        Map<String, CalData> cdfCalDataObjects = new HashMap<>();

        SortedSet<A2LParameter> a2lParams =
            isSelectionFromContextMenu ? new TreeSet<>(A2LParametersPage.this.selectedA2lParams)
                : new TreeSet<>(A2LParametersPage.this.sortedParamSet);

        for (A2LParameter a2lParameter : a2lParams) {
          CalData calDataObj = a2lParameter.getCalData();
          if (null != calDataObj) {
            addToCalDataObjectsToExport(cdfCalDataObjects, calDataObj);
          }
        }
        if (cdfCalDataObjects.size() > 0) {
          String fileName = "CDFX_Export_Report";
          final FileDialog saveFileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
          saveFileDialog.setText("Save");
          // change the default file format to cdfx
          String[] filterExt = { "*.cdfx", "*.cdf", "*.*" };
          saveFileDialog.setFilterExtensions(filterExt);
          saveFileDialog.setFilterIndex(0);
          saveFileDialog.setFileName(fileName);
          saveFileDialog.setOverwrite(true);
          String fileSelected = saveFileDialog.open();
          if (fileSelected != null) {
            String[] filePathSplittedArr = fileSelected.split("\\\\");
            String userFileName = filePathSplittedArr[filePathSplittedArr.length - 1];
            userFileName = userFileName.replaceAll("[^.a-zA-Z0-9]+", "_");
            saveFileDialog.setFileName(userFileName);
            String userFilePath = constructFilePath(filePathSplittedArr, userFileName);
            Job compareJob = new CDFFileExportJob(new MutexRule(), cdfCalDataObjects, userFilePath, filterExt[0], true);
            CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
            compareJob.schedule();
          }
        }
        else {
          CDMLogger.getInstance().infoDialog("Caldata not available for any of the parameters", Activator.PLUGIN_ID);
        }
      }
    };
    exportLabelsAsCDFxAction.setText("Export data as CDFx");
    final ImageDescriptor imageDescExportAsCDFx = ImageManager.getImageDescriptor(ImageKeys.EXPORT_DATA_16X16);
    exportLabelsAsCDFxAction.setImageDescriptor(imageDescExportAsCDFx);
    return exportLabelsAsCDFxAction;
  }

  /**
   * @param cdfCalDataObjects
   * @param calDataObj
   */
  private void addToCalDataObjectsToExport(final Map<String, CalData> cdfCalDataObjects, final CalData calDataObj) {
    String calDataName = calDataObj.getShortName();
    // Icdm-797 null values for the Unit in caldataphy Obj
    if ((calDataObj.getCalDataPhy() != null) &&
        ((calDataObj.getCalDataPhy().getUnit() == null) || "null".equals(calDataObj.getCalDataPhy().getUnit()))) {
      calDataObj.getCalDataPhy().setUnit("");
    }
    cdfCalDataObjects.put(calDataName, calDataObj);
  }

  /**
   * @param filePathSplittedArr
   * @param userFileName
   * @return
   */
  private String constructFilePath(final String[] filePathSplittedArr, final String userFileName) {
    StringBuilder filePath = new StringBuilder();
    for (int i = 0; i <= (filePathSplittedArr.length - 2); i++) {
      filePath.append(filePathSplittedArr[i]).append("\\");
    }
    filePath.append(userFileName);
    return filePath.toString();
  }

  /**
   * ICDM-841 Clear the loaded LAB files
   */
  protected void clearLABFiles() {
    Runnable busyRunnable = () -> {
      A2LParametersPage.this.toolBarActionSet.getWithLABParamAction().setChecked(true);
      A2LParametersPage.this.toolBarActionSet.getWithoutLABParamAction().setChecked(true);
      A2LParametersPage.this.toolBarActionSet.getWithLABParamAction().run();
      A2LParametersPage.this.toolBarActionSet.getWithoutLABParamAction().run();
      A2LParametersPage.this.selFilesList.clear();
      A2LParametersPage.this.labels.clear();
      Map<String, A2LParameter> tableInput = getTableInput();
      for (A2LParameter existingA2lParameter : tableInput.values()) {
        existingA2lParameter.setLABParam(false);
      }
      A2LParametersPage.this.refreshNatTable();
    };
    BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), busyRunnable);

  }

  /**
   * ICDM-841 import lab file
   */
  protected void importLABFile() {
    final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.MULTI);
    fileDialog.setText("Import LAB file");
    fileDialog.setFilterExtensions(new String[] { "*.lab" });
    fileDialog.setFilterNames(new String[] { "LAB File(*.lab)" });
    final String selectedFile = fileDialog.open();
    if (selectedFile != null) {
      runImportLABFile(fileDialog);
    }

  }

  /**
   * @param fileDialog
   */
  private void runImportLABFile(final FileDialog fileDialog) {
    Runnable busyRunnable = () -> {
      try {
        Map<String, A2LParameter> paramMap = new HashMap<>();
        InputFileParser fileParser;
        String filterPath = fileDialog.getFilterPath();
        String[] filesNames = fileDialog.getFileNames();
        File selFile;
        for (String fileName : filesNames) {
          selFile = new File(filterPath, fileName);
          String filePath = selFile.getAbsolutePath();
          fileParser = new InputFileParser(ParserLogger.getInstance(), filePath);
          fileParser.parse();
          List<String> fileLabels = fileParser.getLabels();

          // get labels from functions
          List<String> funcList = fileParser.getFunctions();
          if (CommonUtils.isNotEmpty(funcList)) {
            funcList.stream().forEach(func -> fileLabels
                .addAll(this.a2lContentsEditorInput.getA2lFileInfoBO().getParamListfromFunction(func)));
          }

          // get labels from groups
          List<String> grpList = fileParser.getGroups();
          if (CommonUtils.isNotEmpty(grpList)) {
            fileLabels.addAll(this.a2lContentsEditorInput.getA2lFileInfoBO().getParamListFromGroups(grpList));
          }

          A2LParametersPage.this.selFilesList.add(fileName);
          A2LParametersPage.this.labels.addAll(fileLabels);
        }
        setlabelForExistingA2lParam(paramMap);
        // ICDM-1140
        A2LParametersPage.this.toolBarActionSet.getWithoutLABParamAction().setChecked(false);
        A2LParametersPage.this.toolBarActionSet.getWithoutLABParamAction().run();
      }
      catch (ParserException e) {
        CDMLogger.getInstance().error("Error while importing Lab File. " + e.getMessage(), e, Activator.PLUGIN_ID);
      }
    };
    BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), busyRunnable);
  }

  /**
   * @param paramMap
   */
  private void setlabelForExistingA2lParam(final Map<String, A2LParameter> paramMap) {
    if (!A2LParametersPage.this.labels.isEmpty()) {
      Map<String, A2LParameter> tableInput = A2LParametersPage.this.getTableInput();
      for (A2LParameter existingA2lParam : tableInput.values()) {
        if (A2LParametersPage.this.labels.contains(existingA2lParam.getName())) {
          paramMap.put(existingA2lParam.getName(), existingA2lParam);
          existingA2lParam.setLABParam(true);
        }
      }

    }
  }

  private void clearData() {
    Runnable busyRunnable = () -> {
      A2LParametersPage.this.toolBarActionSet.getWithoutValueFilterAction().setChecked(true);
      A2LParametersPage.this.toolBarActionSet.getWithoutStatusFilterAction().setChecked(true);

      A2LParametersPage.this.toolBarActionSet.getWithoutValueFilterAction().run();
      A2LParametersPage.this.toolBarActionSet.getWithoutStatusFilterAction().run();
      // Reset all parameters to clear data from imported CDFX or dataset
      Map<String, A2LParameter> tableInput = getTableInput();
      for (A2LParameter existingA2lParameter : tableInput.values()) {
        existingA2lParameter.setCalData(null);
        existingA2lParameter.setStatus(null);
      }
      A2LParametersPage.this.refreshNatTable();
      A2LParametersPage.this.selDataSources.clear();
      CDMLogger.getInstance().info("Data cleared from parameters", Activator.PLUGIN_ID);
    };
    BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), busyRunnable);


  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    this.filterTxt.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      String text = A2LParametersPage.this.filterTxt.getText().trim();
      A2LParametersPage.this.allColumnFilterMatcher.setFilterText(text, true);
      A2LParametersPage.this.paramFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
      A2LParametersPage.this.paramFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(A2LParametersPage.this.paramFilterGridLayer.getSortableColumnHeaderLayer()));
      setStatusBarMessage(A2LParametersPage.this.groupByHeaderLayer, false);
    });
  }

  /**
   * input for status line
   *
   * @param outlineSelection flag set according to selection made in viewPart or editor.
   */
  // ICDM-343
  @Override
  public void setStatusBarMessage(final boolean outlineSelection) {
    this.editor.updateStatusBar(outlineSelection, this.totTableRowCount,
        this.paramFilterGridLayer.getRowHeaderLayer().getPreferredRowCount());
  }

  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (part instanceof PIDCDetailsViewPart)) {
      new CommonActionSet().refreshOutlinePages(true);
    }
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (part instanceof OutlineViewPart)) {
      selectionListener(selection);
    }
  }

  /**
   * Selection listener implementation for selections on outlineFilter
   *
   * @param selection
   */
  private void selectionListener(final ISelection selection) {
    this.outlineNatFilter.a2lOutlineSelectionListener(selection);
    // ICDM-859
    if (this.editor.getActivePage() == 5) {
      setStatusBarMessage(getGroupByHeaderLayer(), true);
    }
  }

  @Override
  public void dispose() {
    getSite().getPage().removeSelectionListener(this);
    super.dispose();
  }


  /**
   * This method adds right click menu for tableviewer
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {
      IStructuredSelection selection = (IStructuredSelection) A2LParametersPage.this.selectionProvider.getSelection();
      final Object firstElement = selection.getFirstElement();
      if ((firstElement != null) && (selection.size() != 0)) {
        // 1. The below condition prevents context menu on Group Summary header when only Group summary header is
        // selected
        // 2. when group summary header is selected along with mix of A2lParameters the Context menu is
        // allowed .In the second case the group summary header is handled in the respective actions
        // 3. But when user selects two group summary headers the context menu is enabled.
        // this needs to be handled without performance hit (parsing through the selection to find Group headers
        // needs to be avoided)
        if (!(firstElement instanceof A2LParameter) && (selection.size() == 1)) {
          return;
        }
        CommonActionSet actionSet = new CommonActionSet();
        ReviewActionSet reviewActionSet = new ReviewActionSet();
        actionSet.setAddMultipleParamsToScratchPadAction(menuMgr, selection,
            A2LParametersPage.this.a2lContentsEditorInput.getPidcA2lBO().getA2LFileName());
        // ICDM-218
        // Add seperator bewteen Add to Scratchpad and Show Series statistics menu actions
        menuMgr.add(new Separator());
        final CDMCommonActionSet cdmActionSet = new CDMCommonActionSet();
        // Icdm-697 Disable the action if more than one value is selected
        boolean enableMenu = true;
        // Add Show Series statistics menu action
        if (selection.size() > NAT_TABLE_SEL_SIZE) {
          enableMenu = true;
        }

        cdmActionSet.addShowSeriesStatisticsMenuAction(menuMgr, selection.toList(), enableMenu /* To enable action */);
        cdmActionSet.addShowReviewDataMenuAction(menuMgr, firstElement, enableMenu, null);
        // Icdm-1318 new Context menu for Showing review Rules.
        if (selection.size() == NAT_TABLE_SEL_SIZE) {
          reviewActionSet.addReviewParamEditor(menuMgr, firstElement, null, null, true);
        }

        menuMgr.add(new Separator());
        // ICDM-886

        getPreCalibrationData(menuMgr, selection);

        menuMgr.add(new Separator());
        exportCalDataAsCdfxAction(menuMgr, selection);

        menuMgr.add(new Separator());
        addExportAsLabAction(menuMgr, selection);

        menuMgr.add(new Separator());
        final Action openCalViewerAction = createOpenCalDataViewerAction(selection);
        menuMgr.add(openCalViewerAction);

        // Collapse All Action
        menuMgr.add(new Separator());
        final Action collapseAllAction = createCollapseAllAction();
        menuMgr.add(collapseAllAction);

      }
    });

    final Menu menu = menuMgr.createContextMenu(this.natTable.getShell());
    this.natTable.setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.selectionProvider);


  }

  /**
   * @param menuMgr
   * @param selection
   */
  private void exportCalDataAsCdfxAction(final MenuManager menuMgr, final IStructuredSelection selection) {

    this.selectedA2lParams = new TreeSet<>();

    for (Object object : selection.toList()) {

      if (object instanceof A2LParameter) {
        this.selectedA2lParams.add((A2LParameter) object);
      }
    }

    menuMgr.add(exportToCdfx(true));
  }

  /**
   * @param menuMgr
   * @param selection
   */
  private void addExportAsLabAction(final MenuManager menuMgr, final IStructuredSelection selection) {
    Set<String> labelSet = new HashSet<>();
    for (Object object : selection.toList()) {
      if (object instanceof A2LParameter) {
        labelSet.add(((A2LParameter) object).getName());
      }
    }

    menuMgr.add(new LabFunExportAction(labelSet, null, OUTPUT_FILE_TYPE.LAB, null));
  }

  /**
   * @param menuMgr menu Manager
   * @param selection selection
   */
  public void getPreCalibrationData(final MenuManager menuMgr, final IStructuredSelection selection) {
    final Action exportCdfxAction = new Action() {

      @Override
      public void run() {
        List<A2LParameter> paramList = new ArrayList<>();
        for (Object object : selection.toList()) {
          if (object instanceof A2LParameter) {
            paramList.add((A2LParameter) object);
          }
        }
        // Icdm-976 getting the Default shell
        Shell parent = Display.getCurrent().getActiveShell();
        IA2lParamTable paramTable = A2LParametersPage.this;
        PreCalDataExportWizard exportCDFWizard =
            new PreCalDataExportWizard(paramList, A2LParametersPage.this.a2lContentsEditorInput.getA2lFileInfoBO(),
                A2LParametersPage.this.a2lContentsEditorInput.getPidcA2lBO(), paramTable);


        PreCalDataExportWizardDialog exportCDFWizardDialog = new PreCalDataExportWizardDialog(parent, exportCDFWizard);
        exportCDFWizardDialog.create();
        exportCDFWizardDialog.open();
      }

    };
    // iCDM-902
    exportCdfxAction.setText("Get Pre-Calibration Data");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EXPORT_DATA_16X16);
    exportCdfxAction.setImageDescriptor(imageDesc);
    menuMgr.add(exportCdfxAction);
  }

  /**
   * @return
   */
  private Action createCollapseAllAction() {
    Action collapseAllAction = new Action() {

      @Override
      public void run() {
        A2LParametersPage.this.paramFilterGridLayer.getBodyLayer().getTreeLayer().collapseAll();
      }

    };
    // iCDM-902
    collapseAllAction.setEnabled(true);
    collapseAllAction.setText("Collapse All Groups");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COLLAPSE_NAT_16X16);
    collapseAllAction.setImageDescriptor(imageDesc);
    return collapseAllAction;
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

  public static Comparator<A2LParameter> getParamComparator(final A2LParameter.SortColumns sortColumns) {
    return (final A2LParameter param1, final A2LParameter param2) -> param1.compareTo(param2, sortColumns);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    if (this.editor.getActivePage() == 5) {
      super.updateStatusBar(outlineSelection);
      setStatusBarMessage(this.groupByHeaderLayer, outlineSelection);
    }
  }

  private IConfiguration getCustomComparatorConfiguration(final AbstractLayer columnHeaderDataLayer) {

    return new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Add label accumulator
        ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
        columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);

        // Register labels
        labelAccumulator.registerColumnOverrides(0, CUSTOM_COMPARATOR_LABEL + 0);

        labelAccumulator.registerColumnOverrides(1, CUSTOM_COMPARATOR_LABEL + 1);

        labelAccumulator.registerColumnOverrides(2, CUSTOM_COMPARATOR_LABEL + 2);

        labelAccumulator.registerColumnOverrides(3, CUSTOM_COMPARATOR_LABEL + 3);

        labelAccumulator.registerColumnOverrides(4, CUSTOM_COMPARATOR_LABEL + 4);

        labelAccumulator.registerColumnOverrides(5, CUSTOM_COMPARATOR_LABEL + 5);

        labelAccumulator.registerColumnOverrides(6, CUSTOM_COMPARATOR_LABEL + 6);

        labelAccumulator.registerColumnOverrides(7, CUSTOM_COMPARATOR_LABEL + 7);

        labelAccumulator.registerColumnOverrides(8, CUSTOM_COMPARATOR_LABEL + 8);

        // ICDM-2439
        labelAccumulator.registerColumnOverrides(9, CUSTOM_COMPARATOR_LABEL + 9);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_TYPE_COMPLIANCE), NORMAL, CUSTOM_COMPARATOR_LABEL + 0);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_CHAR_DEFFUNC), NORMAL, CUSTOM_COMPARATOR_LABEL + 1);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_CHAR_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + 2);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_CHAR_TYPE), NORMAL, CUSTOM_COMPARATOR_LABEL + 3);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_CHAR_UNIT), NORMAL, CUSTOM_COMPARATOR_LABEL + 4);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_CHAR_CLASS), NORMAL, CUSTOM_COMPARATOR_LABEL + 5);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_CHAR_CODE), NORMAL, CUSTOM_COMPARATOR_LABEL + 6);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_CHAR_LONGNAME), NORMAL, CUSTOM_COMPARATOR_LABEL + 7);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_CHAR_VALUE), NORMAL, CUSTOM_COMPARATOR_LABEL + 8);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_CHAR_STATUS), NORMAL, CUSTOM_COMPARATOR_LABEL + 9);

        // Register null comparator to disable sort
      }
    };
  }

  /**
   * Enables tootltip only for cells which contain not fully visible content
   *
   * @param natTableObj
   */
  private void attachToolTip(final NatTable natTableObj) {
    DefaultToolTip toolTip = new ExampleNatTableToolTip(natTableObj);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));
  }

  private class ExampleNatTableToolTip extends DefaultToolTip {

    private final NatTable natTable;

    public ExampleNatTableToolTip(final NatTable natTable) {
      super(natTable, ToolTip.NO_RECREATE, false);
      this.natTable = natTable;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.window.ToolTip#getToolTipArea(org.eclipse.swt.widgets.Event) Implementation here means the
     * tooltip is not redrawn unless mouse hover moves outside of the current cell (the combination of
     * ToolTip.NO_RECREATE style and override of this method).
     */
    @Override
    protected Object getToolTipArea(final Event event) {
      int col = this.natTable.getColumnPositionByX(event.x);
      int row = this.natTable.getRowPositionByY(event.y);

      return new Point(col, row);
    }

    @Override
    protected String getText(final Event event) {
      int col = this.natTable.getColumnPositionByX(event.x);
      // ICDM-2487 P1.27.122
      int tooltipRow = LayerUtil.convertRowPosition(this.natTable, this.natTable.getRowPositionByY(event.y),
          A2LParametersPage.this.paramFilterGridLayer.getBodyDataLayer());
      Object rowObject = A2LParametersPage.this.paramFilterGridLayer.getBodyDataProvider().getRowObject(tooltipRow);

      if (rowObject instanceof A2LParameter) {
        StringBuilder toolTip = new StringBuilder();
        A2LParameter a2lParam = (A2LParameter) rowObject;
        if (col == 1) {
          addA2lParamTooltip(toolTip, a2lParam);
        }
        if (toolTip.length() > 0) {
          return toolTip.substring(0, toolTip.length() - 1);
        }
      }
      int row = this.natTable.getRowPositionByY(event.y);
      ILayerCell cellByPosition = this.natTable.getCellByPosition(col, row);
      return (String) cellByPosition.getDataValue();
    }

    /**
     * @param toolTip
     * @param a2lParam
     */
    private void addA2lParamTooltip(final StringBuilder toolTip, final A2LParameter a2lParam) {
      if (a2lParam.isComplianceParam()) {
        toolTip.append(ApicConstants.COMPLIANCE_PARAM).append("\n");
      }
      if (!a2lParam.isInCalMemory()) {
        toolTip.append(ApicConstants.PARAM_NOT_IN_CALMEMORY).append("\n");
      }
      if (a2lParam.getCharacteristic().isReadOnly()) {
        toolTip.append(ApicConstants.READ_ONLY_PARAM).append("\n");
      }
      if (a2lParam.isBlackList()) {
        CommonDataBO dataBo = new CommonDataBO();
        try {
          toolTip.append(dataBo.getMessage(CDRConstants.PARAM, CDRConstants.BLACK_LIST_TOOLTIP)).append("\n");
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
      }
      if (a2lParam.isQssdParameter()) {
        toolTip.append(ApicConstants.QSSD_PARAM).append("\n");
      }
      if (a2lParam.getCharacteristic().isDependentCharacteristic()) {
        toolTip.append(ApicConstants.DEPENDENT_PARAM).append("\n");
        List<String> charNamesList =
            Arrays.asList(a2lParam.getCharacteristic().getDependentCharacteristic().getCharacteristicName());
        charNamesList.forEach(charName -> toolTip.append(charName).append("\n"));
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldCreateToolTip(final Event event) {
      int col = this.natTable.getColumnPositionByX(event.x);
      int row = this.natTable.getRowPositionByY(event.y);
      ILayerCell cellByPosition = this.natTable.getCellByPosition(col, row);
      if (isNotValidCell(cellByPosition)) {
        return false;
      }
      // get the row position
      // ICDM-2487 P1.27.122
      int tooltipRow = LayerUtil.convertRowPosition(this.natTable, this.natTable.getRowPositionByY(event.y),
          A2LParametersPage.this.paramFilterGridLayer.getBodyDataLayer());

      Object rowObject = A2LParametersPage.this.paramFilterGridLayer.getBodyDataProvider().getRowObject(tooltipRow);
      String cellValue = (String) cellByPosition.getDataValue();
      if (rowObject instanceof A2LParameter) {
        A2LParameter a2lParam = (A2LParameter) rowObject;
        if (isComplianceParamCell(col, cellValue, a2lParam)) {
          return true;
        }
        if (isInCalMemoryCell(col, a2lParam)) {
          return true;
        }
        if (isBlackListCell(col, a2lParam)) {
          return true;
        }
        if (isReadOnlyCell(col, a2lParam)) {
          return true;
        }
        if (isDependentCharacteristicCell(col, a2lParam)) {
          return true;
        }
        if (isQssdParameterCell(col, a2lParam)) {
          return true;
        }
      }

      if (CommonUtils.isEmptyString(cellValue)) {
        return false;
      }
      Rectangle currentBounds = cellByPosition.getBounds();
      cellByPosition.getLayer().getPreferredWidth();

      GC gcObj = new GC(this.natTable);
      Point size = gcObj.stringExtent(cellValue);
      return currentBounds.width < size.x;
    }

    /**
     * @param col
     * @param a2lParam
     * @return
     */
    private boolean isQssdParameterCell(final int col, final A2LParameter a2lParam) {
      return (col == 1) && a2lParam.isQssdParameter();
    }

    /**
     * @param col
     * @param a2lParam
     * @return
     */
    private boolean isDependentCharacteristicCell(final int col, final A2LParameter a2lParam) {
      return (col == 1) && a2lParam.getCharacteristic().isDependentCharacteristic();
    }

    /**
     * @param col
     * @param a2lParam
     * @return
     */
    private boolean isReadOnlyCell(final int col, final A2LParameter a2lParam) {
      return (col == 1) && a2lParam.getCharacteristic().isReadOnly();
    }

    /**
     * @param col
     * @param a2lParam
     * @return
     */
    private boolean isBlackListCell(final int col, final A2LParameter a2lParam) {
      return (col == 1) && a2lParam.isBlackList();
    }

    /**
     * @param col
     * @param a2lParam
     * @return
     */
    private boolean isInCalMemoryCell(final int col, final A2LParameter a2lParam) {
      return (col == 1) && !a2lParam.isInCalMemory();
    }

    /**
     * @param col
     * @param cellValue
     * @param a2lParam
     * @return
     */
    private boolean isComplianceParamCell(final int col, final String cellValue, final A2LParameter a2lParam) {
      return (col == 1) && (cellValue != null) && a2lParam.isComplianceParam();
    }

    /**
     * @param cellByPosition
     * @return
     */
    private boolean isNotValidCell(final ILayerCell cellByPosition) {
      return (cellByPosition == null) || (cellByPosition.getDataValue() == null) ||
          !(cellByPosition.getDataValue() instanceof String);
    }
  }

  private static class FilterRowCustomConfiguration extends AbstractRegistryConfiguration {

    final DefaultDoubleDisplayConverter doubleDisplayConverter = new DefaultDoubleDisplayConverter();

    @Override
    public void configureRegistry(final IConfigRegistry configRegistry) {
      // override the default filter row configuration for painter
      configRegistry.registerConfigAttribute(CELL_PAINTER,
          new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 0);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 3);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 4);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 5);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 6);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 7);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 8);


      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 0);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 3);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 4);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 5);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 6);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 7);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 8);
    }

    private static Comparator<String> getIgnorecaseComparator() {
      return (obj1, obj2) -> obj1.compareToIgnoreCase(obj2);
    }
  }


  /**
   * Returns the input object used by the NatTable
   *
   * @return
   */
  private Map<String, A2LParameter> getTableInput() {
    return this.a2lContentsEditorInput.getA2lFileInfoBO().getA2lParamMap(null);

  }

  /**
   * Refreshes the NatTable.</br>
   * </br>
   * <b><i>StructuralRefreshCommand</b></i> refreshes all the layers because of which the sorting order might
   * change</br>
   * <b><i>VisualRefreshCommand</b></i> is used instead but this results in incorrect values when predefined filters are
   * applied. Need to find how to refresh specific layers
   */
  public void refreshNatTable() {
    // To refresh the predefined filter if parameters page is loaded with values from CDFX file with predefined filter
    // applied
    // Prefined filter refresh can be further enhanced by checking if any predefined filter is enabled while importing
    // data from CDFX
    A2LParametersPage.this.paramFilterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
    // Refreshes the Group By model if any after loading/clearing data from CDFX/VCDM
    final CustomGroupByDataLayer bodyDataLayer = A2LParametersPage.this.paramFilterGridLayer.getBodyDataLayer();
    if (bodyDataLayer.getTreeRowModel().hasChildren(0)) {
      Display.getDefault().syncExec(() -> {
        bodyDataLayer.killCache();
        bodyDataLayer.updateTree();
        setStatusBarMessage(getGroupByHeaderLayer(), false);
      });
    }
    // Refresh the natTable
    this.natTable.doCommand(new VisualRefreshCommand());
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
    if (this.baseForm.getToolBarManager() != null) {
      this.baseForm.getToolBarManager().removeAll();
    }
    createTable();
    // First the form's body is repacked and then the section is repacked
    // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable
    this.baseForm.getBody().pack();
    this.section.layout();

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
   *
   */
  private void importDataFromCDFX() {
    final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.MULTI);
    fileDialog.setText("Import from source");
    fileDialog.setFilterExtensions(new String[] { "*.cdfx", "*.cdf", "*.cdfx;*.cdf" });// ICDM-1131
    fileDialog
        .setFilterNames(new String[] { "CDFx File(*.cdfx)", "CDF File(*.cdf)", "All CDFx/CDF files(*.cdfx,*.cdf)" });
    String selectedFile = fileDialog.open();
    if (selectedFile != null) {

      final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
      try {
        dialog.run(true, true, monitor -> {
          monitor.beginTask("Processing selected files...", IProgressMonitor.UNKNOWN);
          final String[] fileNames = fileDialog.getFileNames();
          final CaldataFileParserHandler parserHandler = new CaldataFileParserHandler(ParserLogger.getInstance(), null);
          // Parsing the input files selected
          try {
            final Map<String, CalData> calDataMap = new HashMap<>();
            monitor.subTask("Converting data from selected files");
            // The below for loop overwrites the parameters when multiple files are selected in the file selection
            // dialog
            for (String fileName : fileNames) {
              calDataMap
                  .putAll(parserHandler.getCalDataObjects(fileDialog.getFilterPath() + File.separator + fileName));
            }
            checkIfProgressMonitorCancelled(monitor);
            monitor.subTask("Populating data from selected files");
            addValuesToTable(calDataMap);
          }
          catch (InterruptedException e1) {
            CDMLogger.getInstance().info(e1.getLocalizedMessage(), Activator.PLUGIN_ID);
            monitor.done();
            Thread.currentThread().interrupt();
            return;
          }
          catch (IcdmException e2) {
            CDMLogger.getInstance().error("Error while importing data into Parameters", e2, Activator.PLUGIN_ID);
            monitor.done();
            return;
          }
          CDMLogger.getInstance().info("Data from file(s) " + Arrays.toString(fileNames) + "  loaded successfully",
              Activator.PLUGIN_ID);
          A2LParametersPage.this.selDataSources.addAll(Arrays.asList(fileNames));
          monitor.done();
        });
      }
      catch (InvocationTargetException | InterruptedException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        Thread.currentThread().interrupt();
      }

    }
  }


  /**
   * Loads the CalData from VCDM to Parameters Page </br>
   * Need to extract common method for load from file and this method
   *
   * @param vcdmdstRevision VCDMDSTRevision
   * @param selectedDstTreePath String
   */
  private void loadCalDataFromVCDM(final VCDMDSTRevision vcdmdstRevision, final String selectedDstTreePath) {
    final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
    try {
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Processing selected DST...", IProgressMonitor.UNKNOWN);

        try {
          monitor.subTask("Fetching DST data from vCDM");

          Map<String, CalData> vcdmCalDataMap = new VcdmCalDataBO().fetchCalDataFromVcdm(vcdmdstRevision,
              this.a2lContentsEditorInput.getA2lFileContents(), this.a2lContentsEditorInput.getA2lFile().getId(),
              selectedDstTreePath);

          checkIfProgressMonitorCancelled(monitor);
          monitor.subTask("Populating DST data from vCDM");
          addValuesToTable(vcdmCalDataMap);
        }
        catch (InterruptedException exp1) {
          CDMLogger.getInstance().info(exp1.getLocalizedMessage(), Activator.PLUGIN_ID);
          Thread.currentThread().interrupt();
          monitor.done();
          return;
        }
        catch (IcdmException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
          monitor.done();
          return;
        }

        CDMLogger.getInstance().info("Data from vCDM loaded successfully for DST : " + selectedDstTreePath,
            Activator.PLUGIN_ID);
        A2LParametersPage.this.selDataSources.add(selectedDstTreePath);

        monitor.done();
      });
    }
    catch (InvocationTargetException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   *
   */
  private void importDataSetsFromVCDM() {

    List<VCDMApplicationProject> dataSetModels = getvCDMDatasets();
    DSTSelectionDialog dstSelectionDialog = new DSTSelectionDialog(Display.getDefault().getActiveShell(), dataSetModels,
        this.a2lContentsEditorInput.getPidcA2lBO().getA2LFileName());
    dstSelectionDialog.open();
    if (dstSelectionDialog.getReturnCode() == 0) {
      loadCalDataFromVCDM(dstSelectionDialog.getVcdmdstRevision(), dstSelectionDialog.getSelectedTreePath());
    }
  }

  /**
   * @return
   */
  private List<VCDMApplicationProject> getvCDMDatasets() {
    Long vcdmA2lFileID = this.a2lContentsEditorInput.getPidcA2lBO().getA2lFileBO().getVCDMA2LFileID();
    A2LFileInfoBO dataHandler = this.a2lContentsEditorInput.getA2lFileInfoBO();
    return dataHandler.getvCDMDatasets(vcdmA2lFileID);
  }

  /**
   * ICDM-841 set the filter status based on LAB file loading/clearing
   */
  private void setFilterStatus() {
    StringBuilder toolTip = new StringBuilder();
    toolTip.append("Clear data");
    if (CommonUtils.isNotEmpty(this.selFilesList)) {
      A2LParametersPage.this.toolBarActionSet.getWithLABParamAction().setEnabled(true);
      A2LParametersPage.this.toolBarActionSet.getWithoutLABParamAction().setEnabled(true);
      toolTip.append(" from : \n");
      for (String fileName : this.selFilesList) {
        toolTip.append(fileName + "\n");
      }
      A2LParametersPage.this.clearLABAction.setEnabled(true);
      A2LParametersPage.this.clearLABAction.setToolTipText(toolTip.toString());
    }
    else {
      A2LParametersPage.this.toolBarActionSet.getWithLABParamAction().setEnabled(false);
      A2LParametersPage.this.toolBarActionSet.getWithoutLABParamAction().setEnabled(false);
      A2LParametersPage.this.clearLABAction.setToolTipText("Clear LAB file");
      A2LParametersPage.this.clearLABAction.setEnabled(false);
    }
  }

  /**
   * ICDM-841 set the filter status based on LAB file loading/clearing
   */
  private void setFilterStatusForDataLoad() {
    StringBuilder toolTip = new StringBuilder();
    toolTip.append("Clear all calibration values");
    if (!this.selDataSources.isEmpty() || this.isPreCalibDataLoaded) {
      A2LParametersPage.this.toolBarActionSet.getWithStatusFilterAction().setEnabled(true);
      A2LParametersPage.this.toolBarActionSet.getWithoutStatusFilterAction().setEnabled(true);
      A2LParametersPage.this.toolBarActionSet.getWithValueFilterAction().setEnabled(true);
      A2LParametersPage.this.toolBarActionSet.getWithoutValueFilterAction().setEnabled(true);
      toolTip.append(" from");
      for (String dataSourceName : this.selDataSources) {
        toolTip.append("\n" + dataSourceName);
      }
      if (this.isPreCalibDataLoaded) {
        toolTip.append("\n Pre calibration data");
      }
      this.clearDataAction.setToolTipText(toolTip.toString());
    }
    else {
      A2LParametersPage.this.toolBarActionSet.getWithStatusFilterAction().setEnabled(false);
      A2LParametersPage.this.toolBarActionSet.getWithoutStatusFilterAction().setEnabled(false);
      A2LParametersPage.this.toolBarActionSet.getWithValueFilterAction().setEnabled(false);
      A2LParametersPage.this.toolBarActionSet.getWithoutValueFilterAction().setEnabled(false);

      this.clearDataAction.setToolTipText(toolTip.toString());
    }
  }


  /**
   * @param monitor
   * @throws InterruptedException
   */
  private void checkIfProgressMonitorCancelled(final IProgressMonitor monitor) throws InterruptedException {
    if (monitor.isCanceled()) {
      throw new InterruptedException("Import data Operation was cancelled");
    }
  }

  /**
   * ICDM-866 drage listener added to have the a2l file name
   */
  private void setDragSupport() {
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.natTable.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new DragSourceListener() {

      @Override
      public void dragStart(final DragSourceEvent event) {
        // Drag can be canceled based on selection.This can be implemented once all draggable items are identified
        event.doit = false;
        ISelection currentSelection = null;
        if (A2LParametersPage.this.selectionProvider != null) {
          currentSelection = A2LParametersPage.this.selectionProvider.getSelection();
        }
        if ((currentSelection != null) && !currentSelection.isEmpty()) {
          event.doit = true;
        }
        // If drag is started from a NatTable
        // The below checks ensure the drag source area is confined to the nat body layer
        SelectionLayer selectionLayer = A2LParametersPage.this.paramFilterGridLayer.getBodyLayer().getSelectionLayer();
        if (((selectionLayer != null) && (selectionLayer.getSelectionModel().getSelectedRowCount() == 0)) ||
            ((A2LParametersPage.this.natTable != null) &&
                (A2LParametersPage.this.natTable.getRegionLabelsByXY(event.x, event.y) != null) &&
                !A2LParametersPage.this.natTable.getRegionLabelsByXY(event.x, event.y).hasLabel(GridRegion.BODY))) {
          event.doit = false;
        }
      }

      @Override
      public void dragSetData(final DragSourceEvent event) {
        selectDataForDrag();
      }

      @Override
      public void dragFinished(final DragSourceEvent event) {
        // TO-DO
      }
    });

  }

  private void selectDataForDrag() {
    if (A2LParametersPage.this.editor.getA2lFile() != null) {
      IStructuredSelection sel = (IStructuredSelection) A2LParametersPage.this.selectionProvider.getSelection();
      List<A2LParamInfo> selParams = new ArrayList<>();
      Iterator<?> selParam = sel.iterator();
      A2LParamInfo a2lParamInfo;
      while (selParam.hasNext()) {
        Object selectionElement = selParam.next();
        if (selectionElement instanceof A2LParameter) {
          A2LParameter a2lParam = (A2LParameter) selectionElement;
          a2lParamInfo =
              new A2LParamInfo(a2lParam, A2LParametersPage.this.a2lContentsEditorInput.getPidcA2lBO().getA2LFileName());
          selParams.add(a2lParamInfo);
        }
      }
      StructuredSelection structuredSelection = new StructuredSelection(selParams);
      LocalSelectionTransfer.getTransfer().setSelection(structuredSelection);
    }
  }


  /**
   * @param firstElement
   * @return
   */
  private Action createOpenCalDataViewerAction(final IStructuredSelection selection) {
    final Object firstElement = selection.getFirstElement();
    final A2LParameter selectedParameter =
        (A2LParameter) ((firstElement instanceof A2LParameter) ? firstElement : null);

    final Action openCalViewerAction = new Action() {

      @Override
      public void run() {
        A2LContentsEditorInput a2lContentsEditorInp = (A2LContentsEditorInput) getEditorInput();
        final CalDataViewerDialog calDataViewerDialog = new CalDataViewerDialog(Display.getCurrent().getActiveShell(),
            selectedParameter.getCalData(), selectedParameter.getName(), a2lContentsEditorInp.getName());
        // Task 234466 include T/G viewer V1.9.0
        calDataViewerDialog.setCharacteristicsMap(
            A2LParametersPage.this.a2lContentsEditorInput.getA2lFileInfoBO().getCharacteristicsMap());
        calDataViewerDialog.open();
      }

    };
    // iCDM-902
    openCalViewerAction
        .setEnabled((selection.size() == 1) && (selectedParameter != null) && (selectedParameter.getCalData() != null));
    openCalViewerAction.setText("Show in Table/Graph Viewer");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.TABLE_GRAPH_16X16);
    openCalViewerAction.setImageDescriptor(imageDesc);
    return openCalViewerAction;
  }

  /**
   * Add reset filter button ICDM-1207
   */
  private void addResetAllFiltersAction() {

    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.paramFilterGridLayer);
    addResetFiltersAction();
  }

  /**
   *
   */
  private void executeFilters() {
    Display.getDefault().syncExec(() -> {
      if (A2LParametersPage.this.toolBarActionSet.getWithValueFilterAction().isChecked()) {
        A2LParametersPage.this.toolBarActionSet.getWithValueFilterAction().run();
      }
      if (A2LParametersPage.this.toolBarActionSet.getWithoutValueFilterAction().isChecked()) {
        A2LParametersPage.this.toolBarActionSet.getWithoutValueFilterAction().run();
      }

      if (A2LParametersPage.this.toolBarActionSet.getWithStatusFilterAction().isChecked()) {
        A2LParametersPage.this.toolBarActionSet.getWithStatusFilterAction().run();
      }

      if (A2LParametersPage.this.toolBarActionSet.getWithoutStatusFilterAction().isChecked()) {
        A2LParametersPage.this.toolBarActionSet.getWithoutStatusFilterAction().run();
      }
    });
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
      CDMLogger.getInstance().warn("Failed to load A2L Parameters nat table state", ioe, Activator.PLUGIN_ID);
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
      CDMLogger.getInstance().warn("Failed to save A2L Parameters nat table state", ioe, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param calDataMap
   */
  private void addValuesToTable(final Map<String, CalData> calDataMap) {
    Map<String, A2LParameter> tableInput = A2LParametersPage.this.getTableInput();

    for (Entry<String, CalData> calDataEntry : calDataMap.entrySet()) {
      A2LParameter parameterToUpdate = tableInput.get(calDataEntry.getKey());
      if (parameterToUpdate != null) {
        // Set CalData retrieved from file
        parameterToUpdate.setCalData(calDataEntry.getValue());
      }
    }
    executeFilters();
    A2LParametersPage.this.refreshNatTable();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addPreCalibDataToA2l(final Map<String, CalData> calDataMap) {

    if (A2LParametersPage.this.editor.getActivePageInstance() instanceof A2LParametersPage) {
      addValuesToTable(calDataMap);
    }
    else {
      A2LParametersPage.this.editor.createPage(4);
      addValuesToTable(calDataMap);
      CDMLogger.getInstance().infoDialog("Pre-calibrated values are populated in Parameters page", Activator.PLUGIN_ID);
      A2LParametersPage.this.editor.setActivePage(getId());

    }
    A2LParametersPage.this.isPreCalibDataLoaded = true;
    A2LParametersPage.this.setFilterStatusForDataLoad();
  }

  /**
   * @return CustomGroupByHeaderLayer
   */
  public GroupByHeaderLayer getGroupByHeaderLayer() {
    return this.groupByHeaderLayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setActive(final boolean active) {
    if (this.editor.getActivePage() == 5) {
      this.a2lContentsEditorInput.getA2lWPInfoBO().setCurrentPage(this.editor.getActivePage());
    }
    new CommonActionSet().refreshOutlinePages(true);
  }

}
