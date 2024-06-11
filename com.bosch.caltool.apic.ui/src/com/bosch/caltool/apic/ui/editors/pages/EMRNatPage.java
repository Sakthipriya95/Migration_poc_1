/**
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseClickAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.editors.PIDCEditorInput;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.EMRNatInputToColConverter;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.EMRNatPageActionSet;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.EMRNatPageConfig;
import com.bosch.caltool.apic.ui.table.filters.EMRColFilterMatcher;
import com.bosch.caltool.apic.ui.table.filters.EMRToolBarFilters;
import com.bosch.caltool.icdm.client.bo.apic.EMRFileBO;
import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.emr.EmrFileMapping;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * The Class EmissionRobustnessPage.
 *
 * @author mkl2cob
 */
public class EMRNatPage extends AbstractGroupByNatFormPage implements ISelectionListener {

  /** The Constant FILE_NAME_COLNUM. */
  public static final int FILE_NAME_COLNUM = 0;

  /** The Constant FILE_DESC_COLNUM. */
  public static final int FILE_DESC_COLNUM = 1;

  /** The Constant VARIANTS_COLNUM. */
  public static final int VARIANTS_COLNUM = 2;

  /** The Constant UPLOAD_DATE_COLNUM. */
  public static final int UPLOAD_DATE_COLNUM = 3;

  /** The Constant IS_DELETED_COLNUM. */
  public static final int IS_DELETED_COLNUM = 4;

  /** The Constant IS_LOADED_WTO_ERRORS_COLNUM. */
  public static final int IS_LOADED_WTO_ERRORS_COLNUM = 5;

  /** The non scrollable form. */
  private Form nonScrollableForm;

  /** The form toolkit. */
  private FormToolkit formToolkit;

  /** The editor. */
  private final PIDCEditor editor;

  /** The main composite. */
  private Composite mainComposite;

  /** The table section. */
  private Section tableSection;

  /** The table form. */
  private Form tableForm;

  /** The filter txt. */
  private Text filterTxt;

  /** The property to label map. */
  private Map<Integer, String> propertyToLabelMap;

  /** The filter grid layer. */
  private CustomFilterGridLayer<EmrFileMapping> filterGridLayer;

  /** The tool bar filters. */
  private EMRToolBarFilters toolBarFilters;

  /** The all column filter matcher. */
  private EMRColFilterMatcher<EmrFileMapping> allColumnFilterMatcher;

  /** The group by header layer. */
  private GroupByHeaderLayer groupByHeaderLayer;

  /** The nat table. */
  private CustomNATTable natTable;

  /** The tool bar manager. */
  private ToolBarManager toolBarManager;
  /** The tool bar & right-click menu action set. */
  private EMRNatPageActionSet actionSet;

  /** The selection provider. */
  private RowSelectionProvider<EmrFileMapping> selectionProvider;

  /** The current selection. */
  protected EmrFileMapping currentSelection;

  private int totalRowCount;

  /**
   * Map to hold the filter action text and its current state (checked - true or false)
   */
  private final Map<String, Boolean> toolBarFilterCurrStateMap = new ConcurrentHashMap<>();

  /**
   * Variant ID of the current variant selected
   */
  private Long currentVariantSelected;
  /**
   * Action : show items relevant to current variant
   */
  private Action showCurrentVariantAction;
  /**
   * Action : download and open emr file
   */
  private Action openExcelFileAction;
  /**
   * Action : modify file description
   */
  private Action modifyFileDescAction;
  /**
   * Action : assign variants
   */
  private Action assignProcToVariantAction;
  /**
   * Action : check upload protocol
   */
  private Action checkUploadProtocolAction;

  private ConfigRegistry configRegistry;

  EMRNatPageConfig natConfig = new EMRNatPageConfig();

  /**
   * current column position
   */
  private int currentColIndex = 0;
  /**
   * current row position
   */
  private int currentRowIndex = 0;
  private final CurrentUserBO currentUserBO = new CurrentUserBO();
  private final PIDCEditorInput editorInput;
  private EMRFileBO emrFileBO;


  /**
   * Instantiates a new emission robustness page.
   *
   * @param pidcEditorNew
   * @param editor the editor
   */
  public EMRNatPage(final FormEditor editor, final IEditorInput editorInput) {
    super(editor, "Emission Robustness", "Emission Robustness");
    this.editor = (PIDCEditor) editor;
    this.editorInput = (PIDCEditorInput) editorInput;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);
    // Page title
    this.nonScrollableForm.setText(
        CommonUtils.concatenate(this.editor.getPartName().replace("&", "&&"), " - ", "Codex Measurement Program"));
    this.natConfig.initializeTitleLabel(this.nonScrollableForm.getBody());
    this.natConfig.createTitleDescription(this.nonScrollableForm.getBody(), isPidcVersionReadable());

    final ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }

  /**
   * @return
   */
  public boolean isPidcVersionReadable() {
    boolean pidcVersionReadable = false;
    try {
      NodeAccess nodeAccessRight = this.currentUserBO.getNodeAccessRight(
          this.editorInput.getPidcVersionBO().getPidcDataHandler().getPidcVersionInfo().getPidcVersion().getPidcId());
      if ((nodeAccessRight != null) && nodeAccessRight.isRead() &&
          (!this.editorInput.getPidcVersionBO().getPidcDataHandler().getPidcVersionInfo().getPidc().isDeleted())) {
        pidcVersionReadable = true;
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return pidcVersionReadable;
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
    createComposite();
    getSite().getPage().addSelectionListener(this);
  }

  /**
   * Creates the composite.
   */
  private void createComposite() {
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessVerticalSpace = true;
    gridData1.verticalAlignment = GridData.FILL;
    this.mainComposite = this.nonScrollableForm.getBody();
    final GridLayout gridLayout = new GridLayout();
    createTableViewerSection();
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(gridData1);
    this.natConfig.setStatusBarMsg(false);
  }


  /**
   * Creates the table viewer section.
   */
  private void createTableViewerSection() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.tableSection = SectionUtil.getInstance().createSection(this.mainComposite, this.formToolkit,
        "Existing Emission Robustness Sheets");
    this.tableSection.setLayoutData(gridData);
    this.tableSection.setExpanded(true);
    createTableForm(this.formToolkit);
    this.tableSection.setClient(this.tableForm);
    this.tableSection.setDescription("See existing Sheets for the PIDC and upload additional ones");
    this.tableSection.getDescriptionControl().setEnabled(false);
  }

  /**
   * Creates the table form.
   *
   * @param toolkit the toolkit
   */
  private void createTableForm(final FormToolkit toolkit) {
    // create table form
    this.tableForm = toolkit.createForm(this.tableSection);
    // create filter text
    this.filterTxt = toolkit.createText(this.tableForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    // get parameters
    this.tableForm.getBody().setLayout(new GridLayout());
    createCustomFilterGridLayer();
    this.natConfig.createFilterTxt(this.filterTxt, this.filterGridLayer, this.groupByHeaderLayer,
        this.allColumnFilterMatcher, this);
    createTable();
  }

  private void createCustomFilterGridLayer() {

    // Retrieve data from BO
    this.emrFileBO = this.editorInput.getEmrFileBO();

    Map<Integer, Integer> columnWidthMap = getNatTableColumns();

    EMRNatInputToColConverter natInputToColumConverter = new EMRNatInputToColConverter();
    this.configRegistry = new ConfigRegistry();

    // Group by Model
    GroupByModel groupByModel = new GroupByModel();

    List<Integer> colsToHide = new ArrayList<>();
    // collection of EmrFileMapping(ui model) to populate nat table
    SortedSet<EmrFileMapping> emrFileMappingSet = new TreeSet<>(this.emrFileBO.getCodexExcelFileResultWS().values());
    this.totalRowCount = emrFileMappingSet.size();
    this.filterGridLayer = new CustomFilterGridLayer<>(this.configRegistry, emrFileMappingSet, this.propertyToLabelMap,
        columnWidthMap, this.natConfig.getColumnComparator(UPLOAD_DATE_COLNUM), natInputToColumConverter, this,
        new NatMouseClickAction(), groupByModel, colsToHide, false, true, null, null, false);

    // Enable Tool bar filters
    this.toolBarFilters = new EMRToolBarFilters(this.editorInput.getSelectedPidcVersion(), this);
    this.filterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());

    // Enable column filters
    this.allColumnFilterMatcher = new EMRColFilterMatcher<>();
    this.filterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    // Composite grid layer
    CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
    this.groupByHeaderLayer =
        new GroupByHeaderLayer(groupByModel, this.filterGridLayer, this.filterGridLayer.getColumnHeaderDataProvider());
    compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, this.groupByHeaderLayer, 0, 0);
    compositeGridLayer.setChildLayer("Grid", this.filterGridLayer, 0, 1);
  }

  /**
   * creates NAT Table.
   */
  private void createTable() {

    // Create NAT table
    this.natTable = new CustomNATTable(this.tableForm.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        this.filterGridLayer, false, this.getClass().getSimpleName(), this.propertyToLabelMap);

    try {
      this.natTable.setProductVersion(new CommonDataBO().getIcdmVersion());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    this.natConfig.addNattableConfig(this, this.natTable, this.filterGridLayer, this.configRegistry,
        this.propertyToLabelMap.size());

    // NAT table action set
    this.actionSet = new EMRNatPageActionSet(this, this.filterGridLayer);

    // Toolbar Actions
    createToolBarAction();

    // get the reference to the SelectionLayer
    SelectionLayer selectionLayer = this.filterGridLayer.getBodyLayer().getSelectionLayer();

    // SelectionProvider
    addSelectionProvider(selectionLayer);

    // RightClick Menu
    addRightClickMenu();

    // Update status
    setStatusBarMessage(true);
  }

  /**
   * @param selectedColPostn Column index
   */
  public void fireRelevantAction(final int selectedColPostn) {
    switch (selectedColPostn) {
      case EMRNatPage.FILE_NAME_COLNUM:
        this.openExcelFileAction.runWithEvent(new Event());
        break;

      case EMRNatPage.FILE_DESC_COLNUM:
        this.modifyFileDescAction.runWithEvent(new Event());
        break;

      case EMRNatPage.VARIANTS_COLNUM:
        this.assignProcToVariantAction.runWithEvent(new Event());
        break;

      case EMRNatPage.IS_LOADED_WTO_ERRORS_COLNUM:
        this.checkUploadProtocolAction.runWithEvent(new Event());
        break;

      default:
        break;
    }
  }


  /**
   * Adds the selection provider.
   *
   * @param selectionLayer the selection layer
   */
  private void addSelectionProvider(final SelectionLayer selectionLayer) {
    this.selectionProvider =
        new RowSelectionProvider<>(selectionLayer, this.filterGridLayer.getBodyDataProvider(), false);
    this.selectionProvider.addSelectionChangedListener(event -> {
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      if ((selection != null) && (selection.getFirstElement() != null)) {
        EMRNatPage.this.currentSelection = (EmrFileMapping) selection.getFirstElement();
        EMRNatPage.this.actionSet.enableDeleteAction(EMRNatPage.this.currentSelection, isModifiable());
      }
    });
  }


  /**
   * Configure columns NAT table.
   *
   * @return the nat table columns
   */
  private Map<Integer, Integer> getNatTableColumns() {

    this.propertyToLabelMap = new HashMap<>();
    this.propertyToLabelMap.put(FILE_NAME_COLNUM, "File Name");
    this.propertyToLabelMap.put(FILE_DESC_COLNUM, "Description");
    this.propertyToLabelMap.put(VARIANTS_COLNUM, "Variant(s)");
    this.propertyToLabelMap.put(UPLOAD_DATE_COLNUM, "Uploaded Date");
    this.propertyToLabelMap.put(IS_DELETED_COLNUM, "Is Deleted");
    this.propertyToLabelMap.put(IS_LOADED_WTO_ERRORS_COLNUM, "Has Errors");

    HashMap<Integer, Integer> columnWidthMap = new HashMap<>();
    columnWidthMap.put(FILE_NAME_COLNUM, 200);
    columnWidthMap.put(FILE_DESC_COLNUM, 200);
    columnWidthMap.put(VARIANTS_COLNUM, 250);
    columnWidthMap.put(UPLOAD_DATE_COLNUM, 150);
    columnWidthMap.put(IS_DELETED_COLNUM, 80);
    columnWidthMap.put(IS_LOADED_WTO_ERRORS_COLNUM, 60);

    return columnWidthMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if (CommonUtils.isEqual(getSite().getPage().getActiveEditor(), getEditor()) &&
        (part instanceof PIDCDetailsViewPart)) {
      pidcDetailsTreeViewerSelListener(selection);
    }
  }

  /**
   * pidc details tree listener
   *
   * @param selection
   */
  private void pidcDetailsTreeViewerSelListener(final ISelection selection) {
    if ((selection instanceof IStructuredSelection) && !selection.isEmpty() &&
        (((IStructuredSelection) selection).size() == CommonUIConstants.SINGLE_SELECTION)) {
      final Object first = ((IStructuredSelection) selection).getFirstElement();
      this.currentVariantSelected = null;
      if (first instanceof PidcVariant) {
        this.currentVariantSelected = ((PidcVariant) first).getId();
      }
      else if ((first instanceof PIDCDetailsNode) && ((PIDCDetailsNode) first).isVariantNode()) {
        PIDCDetailsNode detNode = (PIDCDetailsNode) first;
        this.currentVariantSelected = detNode.getPidcVariant().getId();
      }
      else if (first instanceof PidcSubVariant) {
        this.currentVariantSelected = ((PidcSubVariant) first).getPidcVariantId();
      }
      // Trigger Natpage toolbar-filter event
      this.showCurrentVariantAction.runWithEvent(new Event());
    }
  }

  /**
   * @return selected variant
   */
  public Long getSelectedVariant() {
    return this.currentVariantSelected;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setStatusBarMessage(final boolean outlineSelction) {
    this.totalRowCount = this.emrFileBO.getCodexExcelFileResultWS().size();
    this.natConfig.setStatusBarMsg(outlineSelction);
  }


  /**
   * Initialized for mouse-double click action
   *
   * @param menuManagr menu mgr
   */
  private void initializeMouseActions(final MenuManager menuManagr, final boolean isVariantAvailable) {
    // Assign Variants
    if (isVariantAvailable) {
      this.assignProcToVariantAction = this.actionSet.assignVariantMenuAction(menuManagr, isModifiable());
      menuManagr.add(new Separator());
    }
    else {
      this.assignProcToVariantAction = this.actionSet.assignVariantMenuAction(menuManagr, false);
      menuManagr.add(new Separator());
    }
    // Modify Description
    this.modifyFileDescAction = this.actionSet.modifyDescMenuAction(menuManagr, isModifiable());
    menuManagr.add(new Separator());
    // Open Excel file
    this.openExcelFileAction = this.actionSet.openExcelMenuAction(menuManagr, isModifiable());
    menuManagr.add(new Separator());
    // Check upload protocol
    this.checkUploadProtocolAction = this.actionSet.checkUploadProtocolMenuAction(menuManagr, isModifiable());
    menuManagr.add(new Separator());
  }

  /**
   * Add right click context menu items
   */
  private void addRightClickMenu() {
    final MenuManager menuManagr = new MenuManager();
    initializeMouseActions(menuManagr, false);
    menuManagr.setRemoveAllWhenShown(true);
    menuManagr.addMenuListener(mgr -> {
      final IStructuredSelection selection = (IStructuredSelection) EMRNatPage.this.selectionProvider.getSelection();
      final Object firstElement = selection.getFirstElement();
      if (!(firstElement instanceof EmrFileMapping)) {
        return;
      }
      // Single select
      if (selection.size() == 1) {
        List<Object> selectedObj = new ArrayList<>();
        selectedObj.add(firstElement);
        EmrFileMapping emrfilemap = (EmrFileMapping) firstElement;
        initializeMouseActions(menuManagr, !(emrfilemap.getVariantSet().isEmpty()));
      }
    });
    final Menu menu = menuManagr.createContextMenu(this.natTable.getShell());
    this.natTable.setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuManagr, this.selectionProvider);
  }

  /**
   * Reconstruct nat table.
   */
  public void reconstructNatTable() {
    this.natTable.dispose();
    this.propertyToLabelMap.clear();
    if (this.toolBarManager != null) {
      this.toolBarManager.removeAll();
    }
    if (this.tableForm.getToolBarManager() != null) {
      this.tableForm.getToolBarManager().removeAll();
    }
    if (this.nonScrollableForm.getToolBarManager() != null) {
      this.nonScrollableForm.getToolBarManager().removeAll();
    }
    this.filterGridLayer = null;
    createCustomFilterGridLayer();
    createTable();
    // First the form's body is repacked and then the section is repacked
    // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable
    this.tableForm.getBody().pack();
    this.tableSection.layout();

    if (!this.filterTxt.getText().isEmpty()) {
      this.filterTxt.setText(this.filterTxt.getText());
    }

    if (this.natTable != null) {
      this.natTable.refresh();
    }
    setStatusBarMessage(false);
  }

  /**
   * Creates the tool bar action.
   */
  private void createToolBarAction() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = this.toolBarManager.createControl(this.tableSection);
    final Separator separator = new Separator();

    this.actionSet.isLoadedWithoutErrors(this.toolBarManager, this.toolBarFilters);
    this.actionSet.isLoadedWithErrors(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    this.actionSet.isDeleted(this.toolBarManager, this.toolBarFilters);
    this.actionSet.isNotDeleted(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    this.showCurrentVariantAction =
        this.actionSet.showOnlyItemsOfCurrentVariant(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.update(true);
    this.tableSection.setTextClient(toolbar);

    this.tableForm.getToolBarManager().update(true);

    // Reset action
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.filterGridLayer);
    addResetFiltersAction();

    final ToolBarManager mainToolBarManager = (ToolBarManager) this.nonScrollableForm.getToolBarManager();
    addHelpAction(mainToolBarManager);
    this.actionSet.addNewCodexFile(mainToolBarManager);
    this.actionSet.deleteCodexFile(mainToolBarManager);
    this.actionSet.getAddNewCodexFileAction().setEnabled(isModifiable());
    this.nonScrollableForm.getToolBarManager().update(true);
    updateStatusBar(true);
  }

  /**
   * Checks if is modifiable.
   *
   * @return access boolean
   */
  public boolean isModifiable() {
    if (checkIfUserHasEMRAccess() || isOwner()) {
      return true;
    }
    return isPidcVersionModifiable();
  }

  private boolean checkIfUserHasEMRAccess() {
    boolean isRead = false;
    try {
      Long emrNodeId = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.EMR_NODE_ID));
      isRead = new CurrentUserBO().hasNodeReadAccess(emrNodeId);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return isRead;
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

  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
  }

  /**
   * @return the toolBarFilterCurrStateMap
   */
  public Map<String, Boolean> getToolBarFilterCurrStateMap() {
    return this.toolBarFilterCurrStateMap;
  }


  /**
   * @return current row selected
   */
  public EmrFileMapping getCurrentSelection() {
    return this.currentSelection;
  }

  /**
   * @return total rows
   */
  public int getTotalRowCount() {
    return this.totalRowCount;
  }

  private class NatMouseClickAction implements IMouseClickAction {

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final NatTable natTable1, final MouseEvent event) {

      CustomFilterGridLayer<EmrFileMapping> customFilterGridLayer =
          (CustomFilterGridLayer<EmrFileMapping>) natTable1.getLayer();
      final SelectionLayer selectionLayer = customFilterGridLayer.getBodyLayer().getSelectionLayer();
      EMRNatPage.this.currentRowIndex =
          LayerUtil.convertRowPosition(natTable1, natTable1.getRowPositionByY(event.y), selectionLayer);

      EMRNatPage.this.currentColIndex = LayerUtil.convertColumnPosition(natTable1,
          natTable1.getColumnPositionByX(event.x), customFilterGridLayer.getBodyLayer());
      Object rowObject =
          EMRNatPage.this.filterGridLayer.getBodyDataProvider().getRowObject(EMRNatPage.this.currentRowIndex);
      if (rowObject instanceof EmrFileMapping) {
        fireRelevantAction(EMRNatPage.this.currentColIndex);
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExclusive() {
      return false;
    }
  }


  /**
   * @param currentRowIndex selected row index
   */
  public void setCurrentRowIndex(final int currentRowIndex) {
    this.currentRowIndex = currentRowIndex;
  }

  /**
   * @param currentColIndex selected col index
   */
  public void setCurrentColIndex(final int currentColIndex) {
    this.currentColIndex = currentColIndex;
  }


  /**
   * @return
   */
  public boolean isPidcVersionModifiable() {
    try {
      NodeAccess nodeAccessRight = this.currentUserBO.getNodeAccessRight(
          this.editorInput.getPidcVersionBO().getPidcDataHandler().getPidcVersionInfo().getPidc().getId());
      return (nodeAccessRight != null) && nodeAccessRight.isWrite() &&
          (!this.editorInput.getPidcVersionBO().getPidcDataHandler().getPidcVersionInfo().getPidc().isDeleted());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return false;
  }


  /**
   * @return
   */
  public boolean isOwner() {
    try {
      NodeAccess nodeAccessRight =
          this.currentUserBO.getNodeAccessRight(this.editorInput.getSelectedPidcVersion().getPidcId());
      return (nodeAccessRight != null) && nodeAccessRight.isOwner() &&
          (!this.editorInput.getPidcVersionBO().getPidcDataHandler().getPidcVersionInfo().getPidc().isDeleted());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.editor.getEditorInput().getEmrFileDataHandler();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    if (null != this.natTable) {
      if (dce.getConsChangeData().containsKey(MODEL_TYPE.NODE_ACCESS)) {
        this.natConfig.getPageDescLbl().setVisible(!isPidcVersionReadable());
      }
      this.filterGridLayer.getEventList().clear();
      this.filterGridLayer.getEventList().addAll(new TreeSet<>(this.emrFileBO.getCodexExcelFileResultWS().values()));
      if (this.tableForm.getToolBarManager() != null) {
        this.tableForm.getToolBarManager().removeAll();
      }
      setStatusBarMessage(false);
      this.natTable.refresh();
      if (this.nonScrollableForm.getToolBarManager() != null) {
        this.nonScrollableForm.getToolBarManager().removeAll();
      }
      createToolBarAction();
      // First the form's body is repacked and then the section is repacked
      // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable
      this.tableForm.getBody().pack();
      this.tableSection.layout();
    }
  }
}
