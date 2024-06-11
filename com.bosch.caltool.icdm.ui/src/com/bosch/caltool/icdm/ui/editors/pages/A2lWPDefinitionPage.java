/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
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
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditBindings;
import org.eclipse.nebula.widgets.nattable.edit.config.RenderErrorHandling;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellEditorMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.Page;

import com.bosch.caltool.apic.ui.editors.compare.PidcNattableRowObject;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.table.filters.A2LOutlineNatFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.action.A2lWpActionSet;
import com.bosch.caltool.icdm.ui.action.OpenEditWpRespDialogAction;
import com.bosch.caltool.icdm.ui.action.WpRespActionSet;
import com.bosch.caltool.icdm.ui.dialogs.SetWpRespDialog;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditor;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditorInput;
import com.bosch.caltool.icdm.ui.editors.pages.natsupport.A2lWPDefinitionColumnConverter;
import com.bosch.caltool.icdm.ui.editors.pages.natsupport.A2lWPDefinitionColumnFilterMatcher;
import com.bosch.caltool.icdm.ui.editors.pages.natsupport.A2lWPDefinitionEditConfiguration;
import com.bosch.caltool.icdm.ui.editors.pages.natsupport.A2lWPDefinitionLabelAccumulator;
import com.bosch.caltool.icdm.ui.editors.pages.natsupport.A2lWPDefinitionNattoolTip;
import com.bosch.caltool.icdm.ui.editors.pages.natsupport.A2lWpDefnCopyConfig;
import com.bosch.caltool.icdm.ui.table.filters.A2lWPDefToolBarFilter;
import com.bosch.caltool.icdm.ui.util.IMessageConstants;
import com.bosch.caltool.icdm.ui.util.IUIConstants;
import com.bosch.caltool.icdm.ui.util.Messages;
import com.bosch.caltool.icdm.ui.views.A2LDetailsPage;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomColumnHeaderLayerConfiguration;
import com.bosch.caltool.nattable.CustomColumnHeaderStyleConfiguration;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.wbutils.WorkbenchUtils;


/**
 * @author pdh2cob
 */
public class A2lWPDefinitionPage extends AbstractGroupByNatFormPage implements ISelectionListener {

  /**
   *
   */
  private static final String TITLE = "Work Package Definition - ";

  /** The composite. */
  private Composite composite;

  /** The section. */
  private Section section;

  /** The base form. */
  private Form form;
  /** Editor instance. */
  private final A2LContentsEditor editor;

  /** Non scrollable form. */
  private Form nonScrollableForm;
  private final A2LWPInfoBO a2lWPInfoBO;

  private Text filterTxt;

  private Button allowParamMappingButton;

  private static final int LEFT_MOUSE_CLICK = 1;

  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  private static final String STR_ARROW = " >> ";

  private CustomNATTable a2lwpdefinitionNattable;
  private A2lWPDefinitionColumnFilterMatcher a2lwpdefinitionNattableColumnFilterMatcher;
  private Map<Integer, String> propertyToLabelMap;
  private Map<Integer, Integer> columnWidthMap;
  private CustomFilterGridLayer customFilterGridLayer;

  private RowSelectionProvider<A2lWpResponsibility> selectionProvider;
  private A2lWpResponsibility selectedRow;

  /**
   * Map to hold the filter action text and its changed state (checked - true or false)
   */
  private final Map<String, Boolean> toolBarFilterStateMap = new ConcurrentHashMap<>();

  private final IConfigRegistry configRegistry = new ConfigRegistry();

  private final TextCellEditor textCellEditor = new TextCellEditor();

  /**
   * the cell editor mouse event matcher for the ui binding when double clicking on a cell
   */
  private final CellEditorMouseEventMatcher cellEditorMouseEventMatcher = new CellEditorMouseEventMatcher();

  private A2lWpActionSet a2lWpActionSet;

  private A2LOutlineNatFilter a2lOutlineNatFilter;
  /**
   * totTableRowCount contains the Total number of rows set to NatTable Viewer.Used to update the StatusBar Message
   */
  private int totTableRowCount;
  /** The reset state. */
  private boolean resetState;

  CommonActionSet actionSet = new CommonActionSet();

  private ToolBarManager toolBarManager;

  private A2lWPDefToolBarFilter toolbarFilter;

  /** The group by header layer. */
  private GroupByHeaderLayer groupByHeaderLayer;
  protected boolean isRefreshNeeded;

  /**
   * @param editor - instance of a2l editor
   * @param a2lWPInfoBO - bo class that contains data for a2l wp definition
   */
  public A2lWPDefinitionPage(final FormEditor editor, final A2LWPInfoBO a2lWPInfoBO) {
    super(editor, "wpDefinition", "WP");
    this.editor = (A2LContentsEditor) editor;
    this.a2lWPInfoBO = a2lWPInfoBO;
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-249
  @Override
  public void createPartControl(final Composite parent) {

    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
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
    /** The form toolkit. */
    FormToolkit formToolkit = managedForm.getToolkit();
    createComposite(formToolkit);
    // add listeners
    getSite().getPage().addPostSelectionListener(this);
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
    if ((this.a2lWPInfoBO.getWorkingSet() != null)) {
      // Set Content provider for the tree
      PIDCDetailsViewPart pidcDetailsView = (PIDCDetailsViewPart) WorkbenchUtils.getView(PIDCDetailsViewPart.VIEW_ID);
      if (null != pidcDetailsView) {
        Page page = pidcDetailsView.getEditorPageMap().get(this.editor);
        if (page instanceof A2LDetailsPage) {
          A2LDetailsPage a2lDetailsPage = (A2LDetailsPage) page;
          a2lDetailsPage.setWpDefPage(this);
        }
      }
    }

    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    createSection(toolkit);
    this.composite.setLayoutData(gridData);
  }

  /**
   * Creates the section.
   *
   * @param toolkit This method initializes section
   */
  private void createSection(final FormToolkit toolkit) {

    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.section = toolkit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    setTitle();
    this.section.setExpanded(true);

    this.section.getDescriptionControl().setEnabled(false);

    createForm(toolkit);
    this.section.setLayoutData(gridData);

    this.section.setClient(this.form);
  }

  /**
   * @return A2lWpDefinitionVersion Name
   */
  public String getSelWpDefVrsnName() {
    A2lWpDefnVersion a2lWpDefinitionVersion =
        this.a2lWPInfoBO.getA2lWpDefnVersMap().get(this.a2lWPInfoBO.getA2lWpDefnModel().getSelectedWpDefnVersionId());
    String wpDefnVers = "";
    StringBuilder wpDefVrsName = new StringBuilder(wpDefnVers);
    if (a2lWpDefinitionVersion.isActive()) {
      wpDefnVers = "(Active) ";
    }
    wpDefVrsName.append(wpDefnVers).append(a2lWpDefinitionVersion.getName());

    return wpDefVrsName.toString();
  }


  /**
   * Creates the form.
   *
   * @param toolkit This method initializes form
   */
  private void createForm(final FormToolkit toolkit) {
    this.form = toolkit.createForm(this.section);
    setFormMsg();
    createCheckBoxForAllowingParamMapping();
    this.filterTxt = toolkit.createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    createFilterTxt();
    this.form.getBody().setLayout(new GridLayout());
    createTable();
  }

  /**
   * sets the info msg
   */
  private void setFormMsg() {
    if (this.a2lWPInfoBO.isWorkingSet(this.a2lWPInfoBO.getA2lWpDefnModel().getSelectedWpDefnVersionId()) &&
        this.a2lWPInfoBO.getPidcA2lBo().getPidcA2l().isWorkingSetModified()) {
      this.form.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.WARNING_16X16));
      this.form.setText(
          "Changes have been done in the working set are not reflected in any version. Please create a new version after you finished changes on the working set.");
      this.form.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    }
    else {
      this.form.setImage(null);
      this.form.setText("");
    }
  }


  /**
   * Method to add create, edit and delete, load actions and VG filter
   */
  private void createToolbarAction() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar secToolbar = this.toolBarManager.createControl(this.section);
    this.a2lWpActionSet = new A2lWpActionSet(this.a2lWPInfoBO, this);
   
    // par2wp icon
    this.a2lWpActionSet.par2WPAssignmentAction((ToolBarManager) this.form.getToolBarManager(),
        this.a2lWPInfoBO.getPidcA2lBo().getPidcVersion());

    this.form.getToolBarManager().add(new Separator());

    this.a2lWpActionSet.variantGroupWpFilter(this.toolbarFilter, this.toolBarManager);
    this.a2lWpActionSet.otherVariantGroupWpFilter(this.toolbarFilter, this.toolBarManager);
    this.a2lWpActionSet.showNotAtVGLevelFilter(this.toolbarFilter, this.toolBarManager);


    WpRespActionSet importAction = new WpRespActionSet();

    this.form.getToolBarManager().add(importAction.copyVersToWorkingSetAction(this.a2lWPInfoBO));
    this.a2lWpActionSet.addActiveVersionAction((ToolBarManager) this.form.getToolBarManager());


    this.form.getToolBarManager().add(new Separator());

    this.a2lWpActionSet.loadFromA2lGroupsAction((ToolBarManager) this.form.getToolBarManager());
    this.a2lWpActionSet.loadFromFC2WPAction((ToolBarManager) this.form.getToolBarManager());
    this.a2lWpActionSet.createWpsFromFunctionsAction((ToolBarManager) this.form.getToolBarManager());
    this.a2lWpActionSet.loadFromExternalFileAction((ToolBarManager) this.form.getToolBarManager());

    this.form.getToolBarManager().add(new Separator());

    this.a2lWpActionSet.addA2lWpRespAction((ToolBarManager) this.form.getToolBarManager());
    this.a2lWpActionSet.editA2lWpRespAction((ToolBarManager) this.form.getToolBarManager());
    this.a2lWpActionSet.deleteA2lWpRespAction((ToolBarManager) this.form.getToolBarManager(),
        this.a2lWPInfoBO.getPidcA2lBo().getPidcA2l());

    this.form.getToolBarManager().add(new Separator());

    this.a2lWpActionSet.createResetWorkSplitAction((ToolBarManager) this.form.getToolBarManager());

    this.form.getToolBarManager().update(true);
    this.form.setToolBarVerticalAlignment(SWT.TOP);

    addResetAllFiltersAction();

    this.toolBarManager.update(true);
    this.section.setTextClient(secToolbar);
  }


  /**
   * Parameter selection
   */
  private void setParamMappingSelection() {
    if ((this.allowParamMappingButton != null) &&
        (this.a2lWPInfoBO.getA2lWpDefnModel().getSelectedWpDefnVersionId() != null)) {
      this.allowParamMappingButton.setSelection(this.a2lWPInfoBO.getA2lWpDefnVersMap()
          .get(this.a2lWPInfoBO.getA2lWpDefnModel().getSelectedWpDefnVersionId()).isParamLevelChgAllowedFlag());
    }
  }


  private void createCheckBoxForAllowingParamMapping() {
    this.allowParamMappingButton = new Button(this.form.getBody(), SWT.CHECK | SWT.RIGHT);
    this.allowParamMappingButton
        .setText("Allow modifying responsibles on Parameter level (One WP has different responsibilities)");
    this.allowParamMappingButton.setEnabled(this.a2lWPInfoBO.isEditable());
    setParamMappingSelection();
    this.allowParamMappingButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        A2lWPDefinitionPage.this.a2lWPInfoBO.getA2lWpDefnModel()
            .setParamMappingAllowed(A2lWPDefinitionPage.this.allowParamMappingButton.getSelection());
        A2lWpDefnVersion a2lWpDefnVers = A2lWPDefinitionPage.this.a2lWPInfoBO.getWorkingSet();
        a2lWpDefnVers.setParamLevelChgAllowedFlag(A2lWPDefinitionPage.this.allowParamMappingButton.getSelection());

        try {
          List<A2lWpDefnVersion> versionList = new ArrayList<>();
          versionList.add(a2lWpDefnVers);
          new A2lWpDefinitionVersionServiceClient().update(versionList);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    });
  }


  /**
   *
   */
  public void refreshUIElements() {
    if (null != this.section) {
      setTitle();
      setFormMsg();
    }
    if (this.allowParamMappingButton != null) {
      this.allowParamMappingButton.setEnabled(this.a2lWPInfoBO.isEditable());
    }
    setParamMappingSelection();
    refreshNattable(IUIConstants.COLUMN_INDEX_WP);
    if (this.toolBarManager != null) {
      this.toolBarManager.removeAll();
      this.form.getToolBarManager().removeAll();
      createToolbarAction();
      // First the form's body is repacked and then the section is repacked
      // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable
      this.form.getBody().pack();
      this.section.layout();
    }
  }


  /**
   * Add reset filter button
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.customFilterGridLayer);
    addResetFiltersAction();
  }


  /**
   * This method creates filter text.
   */
  private void createFilterTxt() {
    GridData gridData = getFilterTxtGridData();
    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      String text = A2lWPDefinitionPage.this.filterTxt.getText().trim();
      A2lWPDefinitionPage.this.a2lwpdefinitionNattableColumnFilterMatcher.setFilterText(text, true);
      A2lWPDefinitionPage.this.customFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
      A2lWPDefinitionPage.this.customFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(A2lWPDefinitionPage.this.customFilterGridLayer.getSortableColumnHeaderLayer()));
      setStatusBarMessage(false);
    });
  }

  private void createTable() {
    GroupByModel groupByModel = new GroupByModel();

    configureColumns();
    A2lWPDefinitionColumnConverter a2lwpdefinitionNattableColumnConverter =
        new A2lWPDefinitionColumnConverter(this.a2lWPInfoBO);

    Map<Long, A2lWpResponsibility> a2lWpRespMap = this.a2lWPInfoBO.getA2lWpDefnModel().getWpRespMap();
    // Duplicate work package names, mapped to diffrent variant groups should also be displayed
    List<A2lWpResponsibility> a2lWorkPackageList = new ArrayList<>(a2lWpRespMap.values());
    Collections.sort(a2lWorkPackageList);
    this.totTableRowCount = a2lWorkPackageList.size();

    // iCDM-848, Select cols to be hidden by default
    List<Integer> colsToHide = new ArrayList<>();
    colsToHide.add(IUIConstants.COLUMN_INDEX_CREATED_DATE);
    colsToHide.add(IUIConstants.COLUMN_INDEX_MODIFIED_DATE);
    colsToHide.add(IUIConstants.COLUMN_INDEX_CREATED_USER);

    OpenEditWpRespDialogAction mouseDoubleClkAction = new OpenEditWpRespDialogAction(this.a2lWPInfoBO, this);
    this.customFilterGridLayer = new CustomFilterGridLayer<A2lWpResponsibility>(this.configRegistry, a2lWorkPackageList,
        this.propertyToLabelMap, this.columnWidthMap, getNattableComparator(0), a2lwpdefinitionNattableColumnConverter,
        this, mouseDoubleClkAction, groupByModel, colsToHide, false, true, null, null, false);


    this.a2lwpdefinitionNattableColumnFilterMatcher =
        new A2lWPDefinitionColumnFilterMatcher<A2lWpResponsibility>(this.a2lWPInfoBO);

    this.customFilterGridLayer.getFilterStrategy()
        .setAllColumnFilterMatcher(this.a2lwpdefinitionNattableColumnFilterMatcher);


    this.a2lwpdefinitionNattable = new CustomNATTable(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        this.customFilterGridLayer, false, getClass().getSimpleName(), this.propertyToLabelMap);

    // Outline Filter
    A2LFile a2lFile = ((A2LContentsEditorInput) this.editor.getEditorInput()).getA2lFile();
    A2LContentsEditorInput a2lContentsEditorInput = (A2LContentsEditorInput) (this.editor.getEditorInput());
    this.a2lOutlineNatFilter =
        new A2LOutlineNatFilter(this.customFilterGridLayer, a2lFile, a2lContentsEditorInput.getA2lWPInfoBO(), null);
    this.customFilterGridLayer.getFilterStrategy()
        .setOutlineNatFilterMatcher(this.a2lOutlineNatFilter.getOutlineMatcher());
    // Toolbar Filter
    this.toolbarFilter = new A2lWPDefToolBarFilter(this.a2lWPInfoBO);
    this.customFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolbarFilter.getToolBarMatcher());
    this.a2lwpdefinitionNattable.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.a2lwpdefinitionNattable.setConfigRegistry(this.configRegistry);
    this.a2lwpdefinitionNattable.addConfiguration(new CustomNatTableStyleConfiguration());
    // Configuration for Column header Context Menu
    this.a2lwpdefinitionNattable.addConfiguration(new HeaderMenuConfiguration(this.a2lwpdefinitionNattable) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(A2lWPDefinitionPage.this.a2lwpdefinitionNattable)
                .withColumnChooserMenuItem().withMenuItemProvider((natTable1, popupMenu) -> {
                  MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
                  menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
                  menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
                  menuItem.setEnabled(true);
                  menuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                      A2lWPDefinitionPage.this.resetState = true;
                      reconstructNatTable();
                    }
                  });
                }).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });
    this.a2lwpdefinitionNattable.addConfiguration(new HeaderMenuConfiguration(this.a2lwpdefinitionNattable) {

      @Override
      protected PopupMenuBuilder createColumnHeaderMenu(final NatTable natTable1) {
        return super.createColumnHeaderMenu(natTable1).withStateManagerMenuItemProvider();
      }
    });
    // Custom table header style
    CustomColumnHeaderStyleConfiguration columnHeaderStyleConfiguration = new CustomColumnHeaderStyleConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // configure the painter
        configRegistry.registerConfigAttribute(CELL_PAINTER, this.cellPaintr, NORMAL, GridRegion.COLUMN_HEADER);
        configRegistry.registerConfigAttribute(CELL_PAINTER, this.cellPaintr, NORMAL, GridRegion.CORNER);

        // configure whether to render grid lines or not
        // e.g. for the BeveledBorderDecorator the rendering of the grid lines should be disabled
        configRegistry.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, this.rendrGridLines, NORMAL,
            GridRegion.COLUMN_HEADER);
        configRegistry.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, this.rendrGridLines, NORMAL,
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

        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, NORMAL,
            GridRegion.COLUMN_HEADER);
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, NORMAL, GridRegion.CORNER);
      }
    };

    this.customFilterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));

    this.customFilterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));

    this.a2lwpdefinitionNattable.addConfiguration(new SingleClickSortConfiguration());
    this.a2lwpdefinitionNattable
        .addConfiguration(getCustomComparatorConfiguration(this.customFilterGridLayer.getColumnHeaderDataLayer()));


    this.a2lwpdefinitionNattable.addConfiguration(new A2lWPDefinitionEditConfiguration());

    // settting background color for filter row
    this.a2lwpdefinitionNattable.addConfiguration(new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Shade the row to be slightly darker than the blue background. final
        Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);

        // override the default filter row configuration for painter
        configRegistry.registerConfigAttribute(CELL_PAINTER,
            new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);


        // register column overrides to add image to user responsible column
        ((ColumnOverrideLabelAccumulator) A2lWPDefinitionPage.this.customFilterGridLayer.getBodyDataLayer()
            .getConfigLabelAccumulator()).registerColumnOverrides(IUIConstants.COLUMN_INDEX_SET_RESP,
                IUIConstants.CONFIG_LABEL_SET_RESPONSIBLE);

        registerEditableColumns();


      }
    });


    // double click on nattable to be editable
    this.a2lwpdefinitionNattable.addConfiguration(new DefaultEditBindings() {

      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
        uiBindingRegistry.unregisterSingleClickBinding(A2lWPDefinitionPage.this.cellEditorMouseEventMatcher);
        uiBindingRegistry.registerDoubleClickBinding(A2lWPDefinitionPage.this.cellEditorMouseEventMatcher,
            new MouseEditAction());
      }
    });


    // adding configuration for copy, paste action through keyboard shortcuts in nattable
    this.a2lwpdefinitionNattable
        .addConfiguration(new A2lWpDefnCopyConfig(this.customFilterGridLayer.getBodyLayer().getSelectionLayer(),
            this.a2lwpdefinitionNattable.getInternalCellClipboard(), this));


    DataLayer bodyDataLayer = this.customFilterGridLayer.getBodyDataLayer();

    // unregister existing default update command handler
    this.customFilterGridLayer.getBodyDataLayer().unregisterCommandHandler(UpdateDataCommand.class);

    // register custom command handler
    this.customFilterGridLayer.getBodyDataLayer().registerCommandHandler(new A2lWpRespUpdateDataCommandHandler(
        this.customFilterGridLayer, this.a2lWPInfoBO.getPidcA2lBo().getPidcA2l()));

    IRowDataProvider<A2lWpResponsibility> bodyDataProvider =
        (IRowDataProvider<A2lWpResponsibility>) bodyDataLayer.getDataProvider();

    A2lWPDefinitionLabelAccumulator a2lwpdefinitionNattableLabelAccumulator =
        new A2lWPDefinitionLabelAccumulator(bodyDataLayer, bodyDataProvider, this.a2lWPInfoBO);

    bodyDataLayer.setConfigLabelAccumulator(a2lwpdefinitionNattableLabelAccumulator);

    this.selectionProvider = new RowSelectionProvider<>(this.customFilterGridLayer.getBodyLayer().getSelectionLayer(),
        this.customFilterGridLayer.getBodyDataProvider(), false);

    // adding selection provider
    addSelectionToNattable();
    this.a2lwpdefinitionNattable.configure();

    loadState();

    // Added a workaround to maintain selection in nattable
    // Reason :After setting row selection in nattable via selection listenr
    // due to some reason nattable automatically triggred a event
    // which removes the selection from nattable
    this.a2lwpdefinitionNattable.addPaintListener(paintevent -> {
      if (A2lWPDefinitionPage.this.isRefreshNeeded && (A2lWPDefinitionPage.this.selectedRow != null)) {
        A2lWPDefinitionPage.this.selectionProvider
            .setSelection(new StructuredSelection(A2lWPDefinitionPage.this.selectedRow));
        A2lWPDefinitionPage.this.isRefreshNeeded = false;
      }
    });

    // add listeners to save state
    this.a2lwpdefinitionNattable.addFocusListener(new FocusListener() {

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
    this.a2lwpdefinitionNattable.addDisposeListener(event ->
    // save state
    saveState());

    attachToolTip();
    addMouseListener();
    addRightClickOption();
    createToolbarAction();
  }

  private IConfiguration getCustomComparatorConfiguration(final AbstractLayer columnHeaderDataLayer) {

    return new AbstractRegistryConfiguration() {

      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Add label accumulator
        ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
        columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);

        // Register labels
        labelAccumulator.registerColumnOverrides(IUIConstants.COLUMN_INDEX_WP,
            CUSTOM_COMPARATOR_LABEL + IUIConstants.COLUMN_INDEX_WP);
        // Register labels
        labelAccumulator.registerColumnOverrides(IUIConstants.COLUMN_INDEX_VAR_GROUP,
            CUSTOM_COMPARATOR_LABEL + IUIConstants.COLUMN_INDEX_VAR_GROUP);

        labelAccumulator.registerColumnOverrides(IUIConstants.COLUMN_INDEX_RESP_TYPE,
            CUSTOM_COMPARATOR_LABEL + IUIConstants.COLUMN_INDEX_RESP_TYPE);

        labelAccumulator.registerColumnOverrides(IUIConstants.COLUMN_INDEX_RESP,
            CUSTOM_COMPARATOR_LABEL + IUIConstants.COLUMN_INDEX_RESP);

        labelAccumulator.registerColumnOverrides(IUIConstants.COLUMN_INDEX_WP_CUST,
            CUSTOM_COMPARATOR_LABEL + IUIConstants.COLUMN_INDEX_WP_CUST);

        labelAccumulator.registerColumnOverrides(IUIConstants.COLUMN_INDEX_CREATED_DATE,
            CUSTOM_COMPARATOR_LABEL + IUIConstants.COLUMN_INDEX_CREATED_DATE);


        labelAccumulator.registerColumnOverrides(IUIConstants.COLUMN_INDEX_MODIFIED_DATE,
            CUSTOM_COMPARATOR_LABEL + IUIConstants.COLUMN_INDEX_MODIFIED_DATE);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getNattableComparator(IUIConstants.COLUMN_INDEX_VAR_GROUP), NORMAL,
            CUSTOM_COMPARATOR_LABEL + IUIConstants.COLUMN_INDEX_VAR_GROUP);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getNattableComparator(IUIConstants.COLUMN_INDEX_WP), NORMAL,
            CUSTOM_COMPARATOR_LABEL + IUIConstants.COLUMN_INDEX_WP);


        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getNattableComparator(IUIConstants.COLUMN_INDEX_RESP_TYPE), NORMAL,
            CUSTOM_COMPARATOR_LABEL + IUIConstants.COLUMN_INDEX_RESP_TYPE);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getNattableComparator(IUIConstants.COLUMN_INDEX_RESP), NORMAL,
            CUSTOM_COMPARATOR_LABEL + IUIConstants.COLUMN_INDEX_RESP);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getNattableComparator(IUIConstants.COLUMN_INDEX_WP_CUST), NORMAL,
            CUSTOM_COMPARATOR_LABEL + IUIConstants.COLUMN_INDEX_WP_CUST);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getNattableComparator(IUIConstants.COLUMN_INDEX_CREATED_DATE), NORMAL,
            CUSTOM_COMPARATOR_LABEL + IUIConstants.COLUMN_INDEX_CREATED_DATE);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getNattableComparator(IUIConstants.COLUMN_INDEX_MODIFIED_DATE), NORMAL,
            CUSTOM_COMPARATOR_LABEL + IUIConstants.COLUMN_INDEX_MODIFIED_DATE);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getNattableComparator(IUIConstants.COLUMN_INDEX_CREATED_USER), NORMAL,
            CUSTOM_COMPARATOR_LABEL + IUIConstants.COLUMN_INDEX_CREATED_USER);

      }

    };

  }

  /**
   * @param editable - true for editable, false for not editable
   */
  private void registerEditableColumns() {

    // register error handler to display error messages
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.VALIDATION_ERROR_HANDLER,
        new RenderErrorHandling(), DisplayMode.EDIT, IUIConstants.CONFIG_LABEL_WP_NAME);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.VALIDATION_ERROR_HANDLER,
        new RenderErrorHandling(), DisplayMode.EDIT, IUIConstants.CONFIG_LABEL_WP_NAME_CUST);

    A2lWPDefinitionPage.this.textCellEditor.setErrorDecorationEnabled(true);
    A2lWPDefinitionPage.this.textCellEditor.setDecorationPositionOverride(SWT.RIGHT | SWT.TOP);


  }


  /**
   *
   */
  private void addSelectionToNattable() {

    this.selectionProvider.addSelectionChangedListener(event -> {
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();

      if ((selection != null) && (selection.isEmpty())) {
        A2lWPDefinitionPage.this.a2lWpActionSet.getDeleteA2lWpResp().setEnabled(false);
        A2lWPDefinitionPage.this.a2lWpActionSet.getEditA2lWpResp().setEnabled(false);
        A2lWPDefinitionPage.this.isRefreshNeeded = true;
      }
      else {
        if ((selection != null) && (selection.getFirstElement() instanceof A2lWpResponsibility)) {
          this.selectedRow = (A2lWpResponsibility) selection.getFirstElement();
          if (this.a2lWPInfoBO.isMappedToSelectedVarGrp(this.selectedRow) &&
              !this.selectedRow.getName().equals(ApicConstants.DEFAULT_A2L_WP_NAME)) {
            A2lWPDefinitionPage.this.a2lWpActionSet.getDeleteA2lWpResp().setEnabled(this.a2lWPInfoBO.isEditable());
            A2lWPDefinitionPage.this.a2lWpActionSet.getEditA2lWpResp().setEnabled(this.a2lWPInfoBO.isEditable());
          }
          else {
            A2lWPDefinitionPage.this.a2lWpActionSet.getDeleteA2lWpResp().setEnabled(false);
            A2lWPDefinitionPage.this.a2lWpActionSet.getEditA2lWpResp().setEnabled(false);
          }
        }
      }

    });
  }


  /**
   * Context menu actions
   */
  private void addRightClickOption() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(menuManager -> {
      final IStructuredSelection selection =
          (IStructuredSelection) A2lWPDefinitionPage.this.selectionProvider.getSelection();

      if (!selection.isEmpty()) {
        if (!this.selectedRow.getName().equals(ApicConstants.DEFAULT_A2L_WP_NAME)) {
          if (this.a2lWPInfoBO.isMappedToSelectedVarGrp(this.selectedRow)) {
            addActionSetForvarGroupWp(menuMgr, selection);
          }
          else {
            A2lWPDefinitionPage.this.a2lWpActionSet.addCustomizationAction(selection, menuMgr);
            A2lWPDefinitionPage.this.a2lWpActionSet.removeCustomizationAction(selection, menuMgr);
          }
          menuMgr.add(new Separator());
        }
        A2lWPDefinitionPage.this.a2lWpActionSet.getPreCalibrationData(menuMgr, selection,
            this.editor.getPrmNatFormPage());

        menuMgr.add(new Separator());
        A2lWPDefinitionPage.this.a2lWpActionSet.addExportParamsAsLabAction(menuMgr, selection);


      }
    });

    final Menu menu = menuMgr.createContextMenu(this.a2lwpdefinitionNattable.getShell());
    this.a2lwpdefinitionNattable.setMenu(menu);

    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.selectionProvider);
  }

  /**
   * @param menuMgr
   * @param selection
   */
  private void addActionSetForvarGroupWp(final MenuManager menuMgr, final IStructuredSelection selection) {
    A2lWPDefinitionPage.this.a2lWpActionSet.setBoschResponsibleAction(menuMgr, selection);
    A2lWPDefinitionPage.this.a2lWpActionSet.setCustomerResponsibleAction(menuMgr,
        A2lWPDefinitionPage.this.a2lwpdefinitionNattable.getShell(), selection);
    A2lWPDefinitionPage.this.a2lWpActionSet.setOtherResponsibleAction(menuMgr, selection);
    menuMgr.add(new Separator());
    if (selection.size() == CommonUIConstants.SINGLE_SELECTION) {
      A2lWPDefinitionPage.this.a2lWpActionSet.addCopyAction(selection, menuMgr);
    }
    A2lWPDefinitionPage.this.a2lWpActionSet.addPasteAction(selection, menuMgr);

    if (selection.size() == CommonUIConstants.SINGLE_SELECTION) {
      A2lWPDefinitionPage.this.a2lWpActionSet.addTakeOverAction(selection, menuMgr);
    }
    menuMgr.add(new Separator());
    A2lWPDefinitionPage.this.a2lWpActionSet.addCustomizationAction(selection, menuMgr);
    A2lWPDefinitionPage.this.a2lWpActionSet.removeCustomizationAction(selection, menuMgr);
  }

  /**
   * Method to create columns for nattable
   */
  private void configureColumns() {
    this.propertyToLabelMap = new HashMap<>();

    this.columnWidthMap = new HashMap<>();

    this.propertyToLabelMap.put(IUIConstants.COLUMN_INDEX_WP, "Work Package");

    this.columnWidthMap.put(IUIConstants.COLUMN_INDEX_WP, 300);

    this.propertyToLabelMap.put(IUIConstants.COLUMN_INDEX_VAR_GROUP, "Variant Group");

    this.columnWidthMap.put(IUIConstants.COLUMN_INDEX_VAR_GROUP, 300);

    this.propertyToLabelMap.put(IUIConstants.COLUMN_INDEX_RESP_TYPE, "Responsibility Type");

    this.columnWidthMap.put(IUIConstants.COLUMN_INDEX_RESP_TYPE, 300);

    this.propertyToLabelMap.put(IUIConstants.COLUMN_INDEX_RESP, "Responsibility");

    this.columnWidthMap.put(IUIConstants.COLUMN_INDEX_RESP, 300);

    this.propertyToLabelMap.put(IUIConstants.COLUMN_INDEX_WP_CUST, "Name at Customer");

    this.columnWidthMap.put(IUIConstants.COLUMN_INDEX_WP_CUST, 300);

    this.propertyToLabelMap.put(IUIConstants.COLUMN_INDEX_SET_RESP, "");

    this.columnWidthMap.put(IUIConstants.COLUMN_INDEX_SET_RESP, 50);

    this.propertyToLabelMap.put(IUIConstants.COLUMN_INDEX_CREATED_DATE, "Created Date");

    this.columnWidthMap.put(IUIConstants.COLUMN_INDEX_CREATED_DATE, 100);

    this.propertyToLabelMap.put(IUIConstants.COLUMN_INDEX_MODIFIED_DATE, "Modified Date");

    this.columnWidthMap.put(IUIConstants.COLUMN_INDEX_MODIFIED_DATE, 100);

    this.propertyToLabelMap.put(IUIConstants.COLUMN_INDEX_CREATED_USER, "Created User");

    this.columnWidthMap.put(IUIConstants.COLUMN_INDEX_CREATED_USER, 100);


  }

  private Comparator<A2lWpResponsibility> getNattableComparator(final int columnNumber) {

    return ((final A2lWpResponsibility obj1, final A2lWpResponsibility obj2) -> {
      int ret = 0;
      switch (columnNumber) {
        case IUIConstants.COLUMN_INDEX_WP:
          ret = ModelUtil.compare(obj1.getName(), obj2.getName());
          if (ret == 0) {
            ret = sortVarGroupCol(obj1, obj2);
          }
          break;
        case IUIConstants.COLUMN_INDEX_VAR_GROUP:
          ret = sortVarGroupCol(obj1, obj2);
          break;
        case IUIConstants.COLUMN_INDEX_RESP_TYPE:
          String respType1 = this.a2lWPInfoBO.getRespTypeName(obj1);
          String respType2 = this.a2lWPInfoBO.getRespTypeName(obj2);
          ret = ModelUtil.compare(respType1, respType2);
          break;
        case IUIConstants.COLUMN_INDEX_RESP:
          ret = compareResp(obj1, obj2);
          break;
        case IUIConstants.COLUMN_INDEX_WP_CUST:
          ret = ModelUtil.compare(obj1.getWpNameCust(), obj2.getWpNameCust());
          break;

        case 10:
          // to sort in descending order, that is to display all wp resp that are mapped in var grp first
          ret = ModelUtil.compare(obj2.getVariantGrpId(), obj1.getVariantGrpId());
          break;
        default:
          ret = obj1.compareTo(obj2);
          break;
      }
      return ret;
    });

  }

  /**
   * @param obj1
   * @param obj2
   * @param ret
   * @return
   */
  private int compareResp(final A2lWpResponsibility obj1, final A2lWpResponsibility obj2) {
    String resp1 = getResp(obj1);
    String resp2 = getResp(obj2);
    if ((resp1 != null) && (resp2 != null)) {
      return ModelUtil.compare(resp1, resp2);
    }
    return 0;
  }

  /**
   * @param obj1
   * @param obj2
   * @return
   */
  private int sortVarGroupCol(final A2lWpResponsibility obj1, final A2lWpResponsibility obj2) {
    String varGrp1 = "";
    String varGrp2 = "";
    Map<Long, A2lVariantGroup> a2lVariantGrpMap = this.a2lWPInfoBO.getDetailsStrucModel().getA2lVariantGrpMap();
    if ((obj1.getVariantGrpId() != null) && (a2lVariantGrpMap != null) &&
        (a2lVariantGrpMap.get(obj1.getVariantGrpId()) != null)) {
      varGrp1 = a2lVariantGrpMap.get(obj1.getVariantGrpId()).getName();
    }
    if ((obj2.getVariantGrpId() != null) && (a2lVariantGrpMap != null) &&
        (a2lVariantGrpMap.get(obj2.getVariantGrpId()) != null)) {
      varGrp2 = a2lVariantGrpMap.get(obj2.getVariantGrpId()).getName();
    }
    return ModelUtil.compare(varGrp1, varGrp2);
  }

  private String getResp(final A2lWpResponsibility a2lWpResponsibility) {
    if (null != a2lWpResponsibility.getA2lRespId()) {
      A2lResponsibility a2lResp = this.a2lWPInfoBO.getA2lResponsibilityModel().getA2lResponsibilityMap()
          .get(a2lWpResponsibility.getA2lRespId());
      return a2lResp.getName();
    }
    return null;
  }

  /**
   * Method to attach tool tip to nattable
   */
  private void attachToolTip() {
    DefaultToolTip toolTip = new A2lWPDefinitionNattoolTip(this.a2lwpdefinitionNattable, new String[0]);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));
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
   * Add mouse click listener in nattable
   */
  private void addMouseListener() {

    this.a2lwpdefinitionNattable.addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(final MouseEvent mouseEvent) {
        // Implemetation not needed currently
      }

      @Override
      public void mouseDown(final MouseEvent mouseEvent) {
        if (mouseEvent.button == LEFT_MOUSE_CLICK) {
          ILayerCell cell = A2lWPDefinitionPage.this.a2lwpdefinitionNattable.getCellByPosition(
              A2lWPDefinitionPage.this.a2lwpdefinitionNattable.getColumnPositionByX(mouseEvent.x),
              A2lWPDefinitionPage.this.a2lwpdefinitionNattable.getRowPositionByY(mouseEvent.y));


          if (cell != null) {// cell is null when clicking empty area in nattable
            LabelStack configLabels = cell.getConfigLabels();
            if (!configLabels.hasLabel(IUIConstants.CONFIG_LABEL_SET_RESPONSIBLE_DISABLED) &&
                configLabels.hasLabel(IUIConstants.CONFIG_LABEL_SET_RESPONSIBLE)) {
              @SuppressWarnings("unchecked")
              final int col = LayerUtil.convertColumnPosition(A2lWPDefinitionPage.this.a2lwpdefinitionNattable,
                  A2lWPDefinitionPage.this.a2lwpdefinitionNattable.getColumnPositionByX(mouseEvent.x),
                  ((CustomFilterGridLayer<PidcNattableRowObject>) A2lWPDefinitionPage.this.a2lwpdefinitionNattable
                      .getLayer()).getBodyDataLayer());

              @SuppressWarnings("unchecked")
              int row = LayerUtil.convertRowPosition(A2lWPDefinitionPage.this.a2lwpdefinitionNattable,
                  A2lWPDefinitionPage.this.a2lwpdefinitionNattable.getRowPositionByY(mouseEvent.y),
                  ((CustomFilterGridLayer<PidcNattableRowObject>) A2lWPDefinitionPage.this.a2lwpdefinitionNattable
                      .getLayer()).getBodyDataLayer());
              Object rowObject = A2lWPDefinitionPage.this.customFilterGridLayer.getBodyDataProvider().getRowObject(row);
              openWpRespDialog(col, rowObject);
            }
          }
        }
      }

      /**
       * @param col
       * @param rowObject
       */
      private void openWpRespDialog(final int col, final Object rowObject) {
        if ((col == IUIConstants.COLUMN_INDEX_SET_RESP) && (rowObject instanceof A2lWpResponsibility) &&
            A2lWPDefinitionPage.this.a2lWPInfoBO.isEditable() &&
            A2lWPDefinitionPage.this.a2lWPInfoBO.isMappedToSelectedVarGrp(getSelectedRow())) {
          SetWpRespDialog dialog = new SetWpRespDialog(A2lWPDefinitionPage.this.a2lwpdefinitionNattable.getShell(),
              (A2lWpResponsibility) rowObject, A2lWPDefinitionPage.this.a2lWPInfoBO);
          dialog.open();
        }
      }

      @Override
      public void mouseDoubleClick(final MouseEvent mouseEvent) {
        // Implemetation not needed currently
      }

    });
  }

  /**
   * @return the a2lWPInfoBO
   */
  public A2LWPInfoBO getA2lWPInfoBO() {
    return this.a2lWPInfoBO;
  }


  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    refreshUIElements();
    addSelRowForWpRespUpdate();
  }


  private void addSelRowForWpRespUpdate() {
    if (this.selectedRow != null) {
      A2lWpResponsibility wpRespModified =
          this.a2lWPInfoBO.getA2lWpDefnModel().getWpRespMap().get(this.selectedRow.getId());

      if (wpRespModified != null) {
        this.selectedRow = wpRespModified;
        A2lWPDefinitionPage.this.isRefreshNeeded = true;
      }
    }
  }


  /**
   * Refresh nattable
   */
  private void refreshNattable(final int sortColumn) {

    if (this.a2lwpdefinitionNattable != null) {
      this.customFilterGridLayer.getEventList().clear();
      this.customFilterGridLayer.getEventList().addAll(this.a2lWPInfoBO.getA2lWpDefnModel().getWpRespMap().values());
      this.customFilterGridLayer.getEventList().sort(getNattableComparator(sortColumn));
      this.a2lwpdefinitionNattable.refresh();
      if (this.editor.getActivePage() == 2) {
        setStatusBarMessage(false);
      }
      this.a2lWpActionSet.getImportFromA2lGrpVGAction()
          .setEnabled(this.a2lWPInfoBO.canImportGroups() && this.a2lWPInfoBO.isDefaultLevel());
    }
  }

  /**
   * Reconstruct nat table.
   */
  public void reconstructNatTable() {

    this.a2lwpdefinitionNattable.dispose();
    this.propertyToLabelMap.clear();
    this.customFilterGridLayer = null;
    if (this.toolBarManager != null) {
      this.toolBarManager.removeAll();
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
    if (this.a2lwpdefinitionNattable != null) {
      this.a2lwpdefinitionNattable.doCommand(new StructuralRefreshCommand());
      this.a2lwpdefinitionNattable.doCommand(new VisualRefreshCommand());
      this.a2lwpdefinitionNattable.refresh();
    }
  }

  @Override
  public IClientDataHandler getDataHandler() {
    return ((A2LContentsEditorInput) getEditorInput()).getDataHandler();

  }


  /**
   * Gets the group by header layer.
   *
   * @return CustomGroupByHeaderLayer
   */
  public GroupByHeaderLayer getGroupByHeaderLayer() {
    return this.groupByHeaderLayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setStatusBarMessage(final boolean outlineSelection) {
    this.totTableRowCount = this.a2lWPInfoBO.getA2lWpDefnModel().getWpRespMap().values().size();
    this.editor.updateStatusBar(outlineSelection, this.totTableRowCount,
        this.customFilterGridLayer != null ? this.customFilterGridLayer.getRowHeaderLayer().getPreferredRowCount() : 0);
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
   * Load saved state of NAT table.
   */
  private void loadState() {
    try {
      if (this.resetState) {
        this.a2lwpdefinitionNattable.resetState();
      }
      this.a2lwpdefinitionNattable.loadState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to load WP Definition nat table state", ioe, Activator.PLUGIN_ID);
    }

  }

  /**
   * Save current state for the NAT table.
   */
  private void saveState() {
    try {
      this.a2lwpdefinitionNattable.saveState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to save WP Definition nat table state", ioe, Activator.PLUGIN_ID);
    }

  }


  /**
   * @return the customFilterGridLayer
   */
  public CustomFilterGridLayer getCustomFilterGridLayer() {
    return this.customFilterGridLayer;
  }


  /**
   * @return the a2lwpdefinitionNattable
   */
  public CustomNATTable getA2lwpdefinitionNattable() {
    return this.a2lwpdefinitionNattable;
  }

  /**
   * @return the toolBarFilterStateMap
   */
  public Map<String, Boolean> getToolBarFilterStateMap() {
    return this.toolBarFilterStateMap;
  }


  /**
   * @return the selectedRow
   */
  public A2lWpResponsibility getSelectedRow() {
    return this.selectedRow;
  }


  /**
   * @param selectedRow the selectedRow to set
   */
  public void setSelectedRow(final A2lWpResponsibility selectedRow) {
    this.selectedRow = selectedRow;
  }


  /**
   * @return the selectionProvider
   */
  public RowSelectionProvider<A2lWpResponsibility> getSelectionProvider() {
    return this.selectionProvider;
  }


  /**
   * @return the a2lWpActionSet
   */
  public A2lWpActionSet getA2lWpActionSet() {
    return this.a2lWpActionSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (part instanceof PIDCDetailsViewPart)) {
      a2lStructViewSelectionListener(selection);
    }
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (part instanceof OutlineViewPart)) {
      outlineSelectionListener(selection);
    }
  }

  private void outlineSelectionListener(final ISelection selection) {
    this.a2lOutlineNatFilter.a2lOutlineSelectionListener(selection);
    if (this.editor.getActivePage() == 2) {
      setStatusBarMessage(true);
    }
  }

  private void a2lStructViewSelectionListener(final ISelection selection) {
    if ((selection instanceof IStructuredSelection) && !selection.isEmpty() &&
        (((IStructuredSelection) selection).size() == CommonUIConstants.SINGLE_SELECTION)) {
      final Object selectedNode = ((IStructuredSelection) selection).getFirstElement();
      if (selectedNode instanceof A2lVariantGroup) {
        setTitle();
        this.a2lWPInfoBO.setSelectedA2lVarGroup((A2lVariantGroup) selectedNode);
        this.a2lWpActionSet.getOtherVariantGroupWpAction().setChecked(true);
        this.a2lWpActionSet.getIsVariantGroupWpAction().setEnabled(true);
        this.a2lWpActionSet.getOtherVariantGroupWpAction().setEnabled(true);

        this.a2lWpActionSet.getNotMappedToVGAction().setEnabled(true);
        // Trigger Natpage toolbar-filter event
        this.a2lWpActionSet.getOtherVariantGroupWpAction().runWithEvent(new Event());
      }
      else if (selectedNode instanceof PidcVariant) {
        setTitle();
        // True For Default WP case,False for Variants under VG node
        this.a2lWPInfoBO.setNotAssignedVarGrp(null == this.a2lWPInfoBO.getSelectedA2lVarGroup());
        this.toolbarFilter.setOtherVariantGrp(true);
        this.a2lWpActionSet.getOtherVariantGroupWpAction().setChecked(true);
        this.a2lWpActionSet.getIsVariantGroupWpAction().setEnabled(false);
        this.a2lWpActionSet.getOtherVariantGroupWpAction().setEnabled(false);
        this.a2lWpActionSet.getNotMappedToVGAction().setEnabled(false);
      }
      // Pidc Variant ,A2lWpDefinitionVersion or top level node is selected
      if ((selectedNode instanceof String) || (selectedNode instanceof A2lWpDefnVersion)) {
        setTitle();
        this.a2lWPInfoBO.setSelectedA2lVarGroup(null);
        this.toolbarFilter.setOtherVariantGrp(true);
        this.a2lWpActionSet.getOtherVariantGroupWpAction().setChecked(true);
        this.a2lWpActionSet.getIsVariantGroupWpAction().setEnabled(false);
        this.a2lWpActionSet.getOtherVariantGroupWpAction().setEnabled(false);
        this.a2lWpActionSet.getNotMappedToVGAction().setEnabled(false);
      }
      // Sorted based on WP name same as Parameter page
      refreshNattable(IUIConstants.COLUMN_INDEX_WP);
      this.actionSet.refreshOutlinePages(false);
    }
  }


  @Override
  public void setActive(final boolean active) {
    if (this.editor.getActivePage() == 2) {
      this.a2lWPInfoBO.setCurrentPage(this.editor.getActivePage());
    }
    this.actionSet.refreshOutlinePages(true);

    final PIDCDetailsViewPart viewPart = (PIDCDetailsViewPart) WorkbenchUtils.getView(PIDCDetailsViewPart.VIEW_ID);
    if (viewPart != null) {
      A2LDetailsPage detailsPage = (A2LDetailsPage) viewPart.getCurrentPage();
      detailsPage.getAddVarGrpButton().setEnabled(this.a2lWPInfoBO.isWPInfoModifiable());
    }

  }

  private void setTitle() {
    if (CommonUtils.isNotNull(this.a2lWPInfoBO.getSelectedA2lVarGroup()) ||
        (!this.a2lWPInfoBO.isNotAssignedVarGrp() && CommonUtils.isNotNull(this.a2lWPInfoBO.getSelectedA2lVarGroup()))) {
      this.section
          .setText(TITLE + getSelWpDefVrsnName() + STR_ARROW + this.a2lWPInfoBO.getSelectedA2lVarGroup().getName());
    }
    else {
      this.section.setText(TITLE + getSelWpDefVrsnName());
    }
  }

  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMessage(false);
  }
}

