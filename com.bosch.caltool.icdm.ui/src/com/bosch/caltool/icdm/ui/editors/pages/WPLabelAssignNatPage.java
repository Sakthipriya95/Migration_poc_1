/*
 * Copyright(c)Robert Bosch GmbH.All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataToClipboardCommand;
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
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.command.ShowRowInViewportCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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

import com.bosch.calcomp.labfunwriter.LabFunWriterConstants.OUTPUT_FILE_TYPE;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO.SortColumns;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.table.filters.A2LOutlineNatFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ICDMClipboard;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingUpdateModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.action.LabFunExportAction;
import com.bosch.caltool.icdm.ui.action.OpenParamEditDialogAction;
import com.bosch.caltool.icdm.ui.action.WPLabelAssignToolBarFilterActionSet;
import com.bosch.caltool.icdm.ui.action.WpRespActionSet;
import com.bosch.caltool.icdm.ui.dialogs.ShowWpRespListDialog;
import com.bosch.caltool.icdm.ui.dialogs.UpdateWpParamDetailsDialog;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditor;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditorInput;
import com.bosch.caltool.icdm.ui.editors.pages.natsupport.WPLabelAssignNatPageEditConfiguration;
import com.bosch.caltool.icdm.ui.editors.pages.natsupport.WPLabelAssignNatPageLabelAccumulator;
import com.bosch.caltool.icdm.ui.table.filters.WPLabelAssignColumnFilterMatcher;
import com.bosch.caltool.icdm.ui.table.filters.WPLabelAssignToolBarFilter;
import com.bosch.caltool.icdm.ui.util.IMessageConstants;
import com.bosch.caltool.icdm.ui.util.Messages;
import com.bosch.caltool.icdm.ui.views.A2LDetailsPage;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpParamMappingServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomColumnHeaderLayerConfiguration;
import com.bosch.caltool.nattable.CustomColumnHeaderStyleConfiguration;
import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomGroupByDataLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.wbutils.WorkbenchUtils;

/**
 * Nattable Class.
 *
 * @author apj4cob
 */
public class WPLabelAssignNatPage extends AbstractGroupByNatFormPage implements ISelectionListener {

  /**
   *
   */
  private static final String TITLE = "Work Package Parameter Mapping - ";
  /**
   * Title Separator
   */
  private static final String STR_ARROW = " >> ";

  /** The Constant WP_HEADER_LABEL. */
  private static final String WP_HEADER_LABEL = "Workpackage";

  /** The Constant RESP_HEADER_LABEL. */
  private static final String RESP_HEADER_LABEL = "Responsibility";

  /** The Constant NAME_AT_CUSTOMER_LABEL. */
  private static final String NAME_AT_CUSTOMER_LABEL = "Name at Customer";


  /** The nat table. */
  private CustomNATTable natTable;

  /** The wp label assign filter matcher. */
  private WPLabelAssignColumnFilterMatcher wpLabelAssignFilterMatcher;

  /** The property to label map. */
  private Map<Integer, String> propertyToLabelMap;

  /** The column width map. */
  private Map<Integer, Integer> columnWidthMap;

  /** The custom filter grid layer. */
  private CustomFilterGridLayer customFilterGridLayer;

  /** The outline nat filter. */
  private A2LOutlineNatFilter outlineNatFilter;

  /** The group by header layer. */
  private GroupByHeaderLayer groupByHeaderLayer;
  /** The selection provider. */
  private RowSelectionProvider<A2LWpParamInfo> selectionProvider;

  /** The Constant CUSTOM_COMPARATOR_LABEL. */
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";
  /** The composite. */
  private Composite composite;

  /**
   * CommandState instance
   */
  CommandState expReportService = new CommandState();
  /** The reset state. */
  private boolean resetState;
  /** The section. */
  private Section section;

  /** The base form. */
  private Form form;
  /** Editor instance. */
  private final A2LContentsEditor editor;

  /** Non scrollable form. */
  private Form nonScrollableForm;
  /** Filter text instance. */
  private Text filterTxt;
  /**
   * totTableRowCount contains the Total number of rows set to NatTable Viewer.Used to update the StatusBar Message
   */
  private int totTableRowCount;

  /** The tool bar manager. */
  private ToolBarManager toolBarManager;

  /** The a 2 l contents editor input. */
  private final A2LContentsEditorInput a2lContentsEditorInput;

  /** The a 2 l WP info BO. */
  private final A2LWPInfoBO a2lWPInfoBO;

  /** The tool bar filters. */
  private WPLabelAssignToolBarFilter toolBarFilters;

  /** Selected cell's column position. */
  protected int selectedColPostn;

  /** Selected cell's row position. */
  protected int selectedRowPostn;

  /** The selected obj. */
  protected Object selectedObj;
  /** retain Selection Object. */
  private Object retainSelObj;


  /** The tool bar action set. */
  private WPLabelAssignToolBarFilterActionSet toolBarActionSet;

  /** wp list dialog. */
  private ShowWpRespListDialog wpListDialog;
  private IConfigRegistry configRegistry;

  CommonActionSet actionSet = new CommonActionSet();

  private Object lastMadeStructSel;
  private WpRespActionSet importAction;
  /**
   * value is true if refresh needed
   */
  protected boolean isRefreshNeeded;

  /**
   * Instantiates a new WP label assign nat page.
   *
   * @param formEditor FormEditor
   * @param a2lWPInfoBo A2LWPInfoBO
   */
  public WPLabelAssignNatPage(final FormEditor formEditor, final A2LWPInfoBO a2lWPInfoBo) {
    super(formEditor, "Parameters", "WP-Parameter Assignment");
    this.editor = (A2LContentsEditor) formEditor;
    this.a2lContentsEditorInput = (A2LContentsEditorInput) (this.editor.getEditorInput());
    this.a2lWPInfoBO = a2lWPInfoBo;
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-249
  @Override
  public void createPartControl(final Composite parent) {
    // ICDM-249
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
    FormToolkit formToolkit; // @jve:decl-index=0:visual-constraint=""
    formToolkit = managedForm.getToolkit();
    createComposite(formToolkit);
    // add listeners
    getSite().getPage().addSelectionListener(this);
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
   * Creates the composite.
   *
   * @param toolkit This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {
    GridData gridData = getGridData();
    this.composite = this.nonScrollableForm.getBody();
    this.composite.setLayout(new GridLayout());
    if ((this.a2lWPInfoBO.getWorkingSet() != null)) {
      // Set Content provider for the tree
      PIDCDetailsViewPart pidcDetailsView = (PIDCDetailsViewPart) WorkbenchUtils.getView(PIDCDetailsViewPart.VIEW_ID);
      if (null != pidcDetailsView) {
        Page page = pidcDetailsView.getEditorPageMap().get(this.editor);
        if (page instanceof A2LDetailsPage) {
          A2LDetailsPage a2lDetailsPage = (A2LDetailsPage) page;
          a2lDetailsPage.setWpLabelAssignPage(this);
        }
      }
    }
    createSection(toolkit);
    this.composite.setLayoutData(gridData);
  }


  /**
   * Creates the section.
   *
   * @param toolkit FormToolkit
   */
  public void createSection(final FormToolkit toolkit) {
    GridData gridData = getGridData();
    this.section = toolkit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    setTitle();
    this.section.setExpanded(true);
    // ICDM-183
    this.section.getDescriptionControl().setEnabled(false);
    createForm(toolkit);
    this.section.setLayoutData(gridData);
    this.section.setClient(this.form);
  }


  /**
   * @return A2lWpDefinitionVersion Name
   */
  public String getSelWpDefVrsnName() {
    A2lWpDefnVersion a2lWpDefinitionVersion = this.a2lWPInfoBO.getA2lWpDefnVersMap()
        .get(this.a2lWPInfoBO.getA2lWpParamMappingModel().getSelectedWpDefnVersionId());
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
   * Configure columns.
   */
  private void configureColumns() {
    this.propertyToLabelMap = new HashMap<>();
    this.columnWidthMap = new HashMap<>();
    this.propertyToLabelMap.put(0, "");
    this.columnWidthMap.put(0, 80);
    this.propertyToLabelMap.put(1, "Parameter");
    this.columnWidthMap.put(1, 225);
    this.propertyToLabelMap.put(2, "Function Name");
    this.columnWidthMap.put(2, 100);
    this.propertyToLabelMap.put(3, "Function Version");
    this.columnWidthMap.put(3, 80);
    this.propertyToLabelMap.put(4, "BC");
    this.columnWidthMap.put(4, 80);
    this.propertyToLabelMap.put(5, WP_HEADER_LABEL);
    this.columnWidthMap.put(5, 225);
    this.propertyToLabelMap.put(6, "Inherited from WP");
    this.columnWidthMap.put(6, 40);

    this.propertyToLabelMap.put(7, "Responsibility Type");
    this.columnWidthMap.put(7, 60);
    this.propertyToLabelMap.put(8, RESP_HEADER_LABEL);
    this.columnWidthMap.put(8, 100);
    this.propertyToLabelMap.put(9, NAME_AT_CUSTOMER_LABEL);
    this.columnWidthMap.put(9, 120);
    this.propertyToLabelMap.put(10, "Created Date");
    this.columnWidthMap.put(10, 120);
    this.propertyToLabelMap.put(11, "Modified Date");
    this.columnWidthMap.put(11, 120);
    this.propertyToLabelMap.put(12, "Created User");
    this.columnWidthMap.put(12, 120);

  }

  /**
   * Attach tool tip.
   *
   * @param natTableObj the nat table obj
   */
  private void attachToolTip(final CustomNATTable natTableObj) {
    DefaultToolTip toolTip = new SimpleNatTableToolTip(natTableObj);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));
  }

  /**
   * The Class SimpleNatTableToolTip.
   */
  private class SimpleNatTableToolTip extends DefaultToolTip {

    /** The nat table obj. */
    private final NatTable natTableObj;

    /**
     * Instantiates a new simple nat table tool tip.
     *
     * @param natTable the nat table
     */
    public SimpleNatTableToolTip(final NatTable natTable) {
      super(natTable, ToolTip.NO_RECREATE, false);
      this.natTableObj = natTable;
    }

    /**
     * {@inheritDoc}
     */
    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.window.ToolTip#getToolTipArea(org.eclipse.swt.widgets.Event) Implementation here means the
     * tooltip is not redrawn unless mouse hover moves outside of the current cell (the combination of
     * ToolTip.NO_RECREATE style and override of this method).
     */
    @Override
    protected Object getToolTipArea(final Event event) {
      int col = this.natTableObj.getColumnPositionByX(event.x);
      int row = this.natTableObj.getRowPositionByY(event.y);

      return new Point(col, row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getText(final Event event) {
      int col = this.natTableObj.getColumnPositionByX(event.x);
      int row = this.natTableObj.getRowPositionByY(event.y);
      ILayerCell cellByPosition = this.natTableObj.getCellByPosition(col, row);
      Object rowObject = WPLabelAssignNatPage.this.customFilterGridLayer.getBodyDataProvider()
          .getRowObject(cellByPosition.getRowIndex());
      if ((rowObject instanceof A2LWpParamInfo) && (col == 1)) {
        CommonDataBO dataBo = new CommonDataBO();
        A2LWpParamInfo paramInfo = (A2LWpParamInfo) rowObject;
        String virtualRecordToolTip = null;
        String blackListToolTip = null;
        StringBuilder toolTip = new StringBuilder();
        try {
          virtualRecordToolTip =
              dataBo.getMessage(CDRConstants.PARAM, CDRConstants.VIRTUAL_WP_PARAM_MAPPING_REC_TOOLTIP);
          blackListToolTip = dataBo.getMessage(CDRConstants.PARAM, CDRConstants.BLACK_LIST_TOOLTIP);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
        appendToTooltip(paramInfo, virtualRecordToolTip, blackListToolTip, toolTip);
        if (toolTip.length() > 0) {
          return toolTip.substring(0, toolTip.length() - 1);
        }
      }
      return (String) cellByPosition.getDataValue();
    }

    /**
     * @param paramInfo
     * @param virtualRecordToolTip
     * @param blackListToolTip
     * @param toolTip
     */
    private void appendToTooltip(final A2LWpParamInfo paramInfo, final String virtualRecordToolTip,
        final String blackListToolTip, final StringBuilder toolTip) {
      if (paramInfo.isComplianceParam()) {
        toolTip.append(ApicConstants.COMPLIANCE_PARAM).append("\n");
      }
      if (paramInfo.isBlackList()) {
        toolTip.append(blackListToolTip).append("\n");
      }
      if (paramInfo.isQssdParameter()) {
        toolTip.append(ApicConstants.QSSD_PARAM).append("\n");
      }
      if (paramInfo.isReadOnly()) {
        toolTip.append(ApicConstants.READ_ONLY_PARAM).append("\n");
      }
      if (paramInfo.isDependentParameter()) {
        toolTip.append(ApicConstants.DEPENDENT_PARAM).append("\n");
        paramInfo.getDepCharNames().forEach(charName -> toolTip.append(charName).append("\n"));
      }
      if (paramInfo.getA2lWpParamMappingId() == null) {
        toolTip.append(virtualRecordToolTip).append("\n");
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldCreateToolTip(final Event event) {
      int col = this.natTableObj.getColumnPositionByX(event.x);
      int row = this.natTableObj.getRowPositionByY(event.y);
      ILayerCell cellByPosition = this.natTableObj.getCellByPosition(col, row);

      if (cellByPosition == null) {
        return false;
      }

      Object rowObject = WPLabelAssignNatPage.this.customFilterGridLayer.getBodyDataProvider()
          .getRowObject(cellByPosition.getRowIndex());
      if (rowObject instanceof A2LWpParamInfo) {
        A2LWpParamInfo paramInfo = (A2LWpParamInfo) rowObject;
        if ((col == 1) &&
            (paramInfo.isBlackList() || (paramInfo.getA2lWpParamMappingId() == null) || paramInfo.isComplianceParam() ||
                paramInfo.isQssdParameter() || paramInfo.isReadOnly() || paramInfo.isDependentParameter())) {
          return true;
        }
      }
      if (!(cellByPosition.getDataValue() instanceof String)) {
        return false;
      }
      String cellValue = (String) cellByPosition.getDataValue();
      if ((cellValue == null) || cellValue.isEmpty()) {
        return false;
      }
      Rectangle currentBounds = cellByPosition.getBounds();
      cellByPosition.getLayer().getPreferredWidth();

      GC gcObj = new GC(this.natTableObj);
      Point size = gcObj.stringExtent(cellValue);

      return currentBounds.width < size.x;
    }
  }

  /**
   * Gets the custom comparator configuration.
   *
   * @param columnHeaderDataLayer the column header data layer
   * @return the custom comparator configuration
   */
  private IConfiguration getCustomComparatorConfiguration(final AbstractLayer columnHeaderDataLayer) {

    return new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry confRegistry) {
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
        labelAccumulator.registerColumnOverrides(10, CUSTOM_COMPARATOR_LABEL + 10);
        labelAccumulator.registerColumnOverrides(11, CUSTOM_COMPARATOR_LABEL + 11);
        confRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lWpParamComparator(SortColumns.SORT_PARAM_TYPE_COMPLIANCE), NORMAL, CUSTOM_COMPARATOR_LABEL + 0);
        confRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lWpParamComparator(SortColumns.SORT_PARAM_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + 1);

        confRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lWpParamComparator(SortColumns.SORT_FUNC_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + 2);

        confRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lWpParamComparator(SortColumns.SORT_FUNC_VERSION), NORMAL, CUSTOM_COMPARATOR_LABEL + 3);

        confRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lWpParamComparator(SortColumns.SORT_BC_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + 4);

        confRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lWpParamComparator(SortColumns.SORT_WP_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + 5);

        confRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lWpParamComparator(SortColumns.SORT_WP_INHERITED), NORMAL, CUSTOM_COMPARATOR_LABEL + 6);

        confRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lWpParamComparator(SortColumns.SORT_RESP_TYPE), NORMAL, CUSTOM_COMPARATOR_LABEL + 7);

        confRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lWpParamComparator(SortColumns.SORT_RESP_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + 8);

        confRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lWpParamComparator(SortColumns.SORT_WP_CUST_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + 9);

        confRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lWpParamComparator(SortColumns.SORT_CREATED_DATE), NORMAL, CUSTOM_COMPARATOR_LABEL + 10);

        confRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lWpParamComparator(SortColumns.SORT_MODIFIED_DATE), NORMAL, CUSTOM_COMPARATOR_LABEL + 11);


      }
    };
  }

  /**
   * Gets the a 2 l wp param comparator.
   *
   * @param sortColName SortColumns
   * @return Comparator
   */
  public Comparator<A2LWpParamInfo> getA2lWpParamComparator(final SortColumns sortColName) {
    return (final A2LWpParamInfo param1, final A2LWpParamInfo param2) -> this.a2lWPInfoBO.compareTo(param1, param2,
        sortColName);
  }

  /**
   * Creates the table.
   */
  private void createTable() {
    this.configRegistry = new ConfigRegistry();
    GroupByModel groupByModel = new GroupByModel();
    this.totTableRowCount = this.a2lWPInfoBO.getA2lWParamInfoMap().size();
    WPLabelAssignNatColumnConverter wpLabelAssignColumnConverter =
        new WPLabelAssignNatColumnConverter(this.a2lWPInfoBO);
    configureColumns();
    // iCDM-848, Select cols to be hidden by default
    List<Integer> colsToHide = new ArrayList<>();
    colsToHide.add(10);
    colsToHide.add(11);
    colsToHide.add(12);
    SortedSet<A2LWpParamInfo> a2lWpParamInfoSet = new TreeSet<>();
    if ((this.a2lWPInfoBO.getSelectedA2lVarGroup() != null) &&
        (null != this.a2lWPInfoBO.getA2lWParamInfoForVarGrp().get(this.a2lWPInfoBO.getSelectedA2lVarGroup().getId()))) {
      a2lWpParamInfoSet.addAll(
          this.a2lWPInfoBO.getA2lWParamInfoForVarGrp().get(this.a2lWPInfoBO.getSelectedA2lVarGroup().getId()).values());
    }
    else {
      a2lWpParamInfoSet.addAll(this.a2lWPInfoBO.getA2lWParamInfoMap().values());
    }
    // Custom Grid Filter Layer
    OpenParamEditDialogAction mouseDoubleClkAction = new OpenParamEditDialogAction(this);
    this.customFilterGridLayer =
        new CustomFilterGridLayer<A2LWpParamInfo>(this.configRegistry, a2lWpParamInfoSet, this.propertyToLabelMap,
            this.columnWidthMap, getA2lWpParamComparator(SortColumns.SORT_PARAM_NAME), wpLabelAssignColumnConverter,
            this, mouseDoubleClkAction, groupByModel, colsToHide, false, true, null, null, false);
    // Outline Filter
    A2LFile a2lFile = ((A2LContentsEditorInput) this.editor.getEditorInput()).getA2lFile();
    this.outlineNatFilter = new A2LOutlineNatFilter(this.customFilterGridLayer, a2lFile,
        this.a2lContentsEditorInput.getA2lWPInfoBO(), null);
    this.outlineNatFilter.setWpType(this.a2lContentsEditorInput.getA2lFileInfoBO().getMappingSourceID());
    this.customFilterGridLayer.getFilterStrategy()
        .setOutlineNatFilterMatcher(this.outlineNatFilter.getOutlineMatcher());
    // ToolBar Filter
    setToolBarFilters(new WPLabelAssignToolBarFilter(this.a2lWPInfoBO));
    this.customFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(getToolBarFilters().getToolBarMatcher());
    // NatTable Column Filter
    this.wpLabelAssignFilterMatcher = new WPLabelAssignColumnFilterMatcher(this.a2lWPInfoBO);
    this.customFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.wpLabelAssignFilterMatcher);
    // Group By coumn header
    CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
    this.groupByHeaderLayer = new GroupByHeaderLayer(groupByModel, this.customFilterGridLayer,
        this.customFilterGridLayer.getColumnHeaderDataProvider());
    compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, this.groupByHeaderLayer, 0, 0);
    compositeGridLayer.setChildLayer("Grid", this.customFilterGridLayer, 0, 1);
    // Nat Table
    this.natTable = new CustomNATTable(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        this.customFilterGridLayer, false, this.getClass().getSimpleName(), this.propertyToLabelMap);
    try {
      this.natTable.setProductVersion(new CommonDataBO().getIcdmVersion());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    this.natTable.setLayoutData(GridDataUtil.getInstance().getGridData());
    // Add Configurations
    this.natTable.addConfiguration(new GroupByHeaderMenuConfiguration(this.natTable, this.groupByHeaderLayer));
    // Configuration for copy-paste keyboard shortcut
    this.natTable.addConfiguration(new WPLabelAssignCopyPasteConfig(
        this.customFilterGridLayer.getBodyLayer().getSelectionLayer(), this.natTable.getInternalCellClipboard(), this));
    // Edit Configuration
    WPLabelAssignNatPageEditConfiguration wpLabelAssignEditConfig =
        new WPLabelAssignNatPageEditConfiguration(this.customFilterGridLayer);
    this.natTable.addConfiguration(wpLabelAssignEditConfig);
    // Style Configuration for Column filter
    this.natTable.addConfiguration(new FilterRowCustomConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry2) {
        super.configureRegistry(configRegistry2);
        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configRegistry2.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });
    addRightClickMenu();
    this.natTable.addMouseListener(new MouseEventListener());
    // Sorting configuration
    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable
        .addConfiguration(getCustomComparatorConfiguration(this.customFilterGridLayer.getColumnHeaderDataLayer()));

    // Configuration for Column header Context Menu
    addHeaderMenuConfiguration();

    // Column Header Style Configuration
    CustomColumnHeaderStyleConfiguration columnHeaderStyleConfiguration = new CustomColumnHeaderStyleConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry2) {
        // configure the painter
        WPLabelAssignNatPage.this.configRegistry.registerConfigAttribute(CELL_PAINTER, this.cellPaintr, NORMAL,
            GridRegion.COLUMN_HEADER);
        WPLabelAssignNatPage.this.configRegistry.registerConfigAttribute(CELL_PAINTER, this.cellPaintr, NORMAL,
            GridRegion.CORNER);

        // configure whether to render grid lines or not
        // e.g. for the BeveledBorderDecorator the rendering of the grid lines should be disabled
        WPLabelAssignNatPage.this.configRegistry.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES,
            this.rendrGridLines, NORMAL, GridRegion.COLUMN_HEADER);
        WPLabelAssignNatPage.this.configRegistry.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES,
            this.rendrGridLines, NORMAL, GridRegion.CORNER);

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

        WPLabelAssignNatPage.this.configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
            NORMAL, GridRegion.COLUMN_HEADER);
        WPLabelAssignNatPage.this.configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
            NORMAL, GridRegion.CORNER);
      }
    };
    this.customFilterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));
    this.natTable.setConfigRegistry(this.configRegistry);
    CustomNatTableStyleConfiguration natTabConfig = new CustomNatTableStyleConfiguration();
    this.natTable.addConfiguration(natTabConfig);

    // Added a workaround to maintain selection in nattable
    // Reason :After setting row selection in nattable via selection listenr
    // due to some reason nattable automatically triggred a event
    // which removes the selection from nattable
    this.natTable.addPaintListener(paintevent -> {
      if (WPLabelAssignNatPage.this.isRefreshNeeded && (WPLabelAssignNatPage.this.selectedObj != null)) {
        WPLabelAssignNatPage.this.selectionProvider
            .setSelection(new StructuredSelection(WPLabelAssignNatPage.this.selectedObj));
        WPLabelAssignNatPage.this.isRefreshNeeded = false;
      }

    });

    // Column Chooser Configuration
    CustomDefaultBodyLayerStack bodyLayer = this.customFilterGridLayer.getBodyLayer();
    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.customFilterGridLayer.getColumnHeaderLayer(), this.customFilterGridLayer.getColumnHeaderDataLayer(),
            null, null);
    this.natTable.registerCommandHandler(columnChooserCommandHandler);
    DataLayer rowHeaderDataLayer = this.customFilterGridLayer.getColumnHeaderDataLayer();
    rowHeaderDataLayer.setColumnsResizableByDefault(true);
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

    DataLayer bodyDataLayer = this.customFilterGridLayer.getBodyDataLayer();
    this.selectionProvider = new RowSelectionProvider<>(this.customFilterGridLayer.getBodyLayer().getSelectionLayer(),
        this.customFilterGridLayer.getBodyDataProvider(), false);
    IRowDataProvider<A2LWpParamInfo> bodyDataProvider =
        (IRowDataProvider<A2LWpParamInfo>) bodyDataLayer.getDataProvider();
    // Label Accumulator
    WPLabelAssignNatPageLabelAccumulator wpLabelAssignAccumulator =
        new WPLabelAssignNatPageLabelAccumulator(bodyDataLayer, bodyDataProvider, this.a2lWPInfoBO);
    bodyDataLayer.setConfigLabelAccumulator(wpLabelAssignAccumulator);
    addSelectionListenerForTable();
    // Enable tootltip - only for cells which contain not fully visible content
    attachToolTip(this.natTable);
    // Add Drop Support
    setDropSupport();
  }

  /**
   *
   */
  private void addSelectionListenerForTable() {
    this.selectionProvider.addSelectionChangedListener(event -> {
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
      this.expReportService.setExportService(true);
      if ((selection != null) && (selection.isEmpty())) {
        this.importAction.getEditAction().setEnabled(false);
        this.selectionProvider.setSelection(new StructuredSelection(WPLabelAssignNatPage.this.selectedObj));
        WPLabelAssignNatPage.this.isRefreshNeeded = true;
      }
      else {
        if ((selection != null) && (selection.getFirstElement() instanceof A2LWpParamInfo)) {
          WPLabelAssignNatPage.this.selectedObj = selection.getFirstElement();
          Long wpRespId = ((A2LWpParamInfo) WPLabelAssignNatPage.this.selectedObj).getWpRespId();
          // enable edit option only single param mapping selection
          this.importAction.getEditAction().setEnabled((selection.size() == 1) && CommonUtils.isNotNull(wpRespId) &&
              WPLabelAssignNatPage.this.a2lWPInfoBO.isParamEditAllowed());
        }
      }
    });
  }

  /**
   *
   */
  private void addHeaderMenuConfiguration() {
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(WPLabelAssignNatPage.this.natTable)
                .withColumnChooserMenuItem().withMenuItemProvider((natTable1, popupMenu) -> {
                  MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
                  menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
                  menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
                  menuItem.setEnabled(true);
                  menuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                      WPLabelAssignNatPage.this.resetState = true;
                      WPLabelAssignNatPage.this.reconstructNatTable();
                    }
                  });
                }).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });
  }

  private void setDropSupport() {
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.natTable.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new DropTargetListener() {

      @Override
      public void dragEnter(final DropTargetEvent event) {
        event.detail = DND.DROP_COPY | DND.DROP_MOVE;
        event.currentDataType = event.dataTypes[0];
      }

      @Override
      public void dragOperationChanged(final DropTargetEvent arg0) {
        // No changes
      }

      @Override
      public void dragOver(final DropTargetEvent event) {
        event.detail = DND.DROP_COPY | DND.DROP_MOVE;
      }

      @Override
      public void drop(final DropTargetEvent event) {
        // get selected element(WP) from dialog
        StructuredSelection structuredSelection = (StructuredSelection) event.data;
        final Object selectedElement = structuredSelection.getFirstElement();

        // update WP in a2l wp param info
        updateWP(selectedElement);

      }

      @Override
      public void dropAccept(final DropTargetEvent event) {
        // No changes
      }

      @Override
      public void dragLeave(final DropTargetEvent droptargetevent) {
        // No changes
      }
    });
  }


  /**
   * @param selectedElement
   */
  private void updateWP(final Object selectedElement) {
    // get selected rows in nattable (A2lWPParamInfo)
    IStructuredSelection a2lWpParamInfoSelection =
        (IStructuredSelection) WPLabelAssignNatPage.this.selectionProvider.getSelection();
    List<A2LWpParamInfo> selectedA2LWpParamInfoList = a2lWpParamInfoSelection.toList();
    if (selectedElement instanceof A2lWpResponsibility) {
      // element selected in ShowWPList Dialog
      A2lWpResponsibility selectedA2lWpResponsibility = (A2lWpResponsibility) selectedElement;
      this.a2lWPInfoBO.updateWpParamMapping(selectedA2LWpParamInfoList, selectedA2lWpResponsibility, true);
    }

  }


  /**
   * Gets the selection provider.
   *
   * @return the selectionProvider
   */
  public RowSelectionProvider<A2LWpParamInfo> getSelectionProvider() {
    return this.selectionProvider;
  }


  /**
   * @param selectedObj the selectedObj to set
   */
  public void setSelectedObj(final Object selectedObj) {
    this.selectedObj = selectedObj;
  }


  /**
   * @return the selectedObj
   */
  public Object getSelectedObj() {
    return this.selectedObj;
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
      CDMLogger.getInstance().warn("Failed to load WP Parameter Assignment nat table state", ioe, Activator.PLUGIN_ID);
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
      CDMLogger.getInstance().warn("Failed to save WP Parameter Assignment nat table state", ioe, Activator.PLUGIN_ID);
    }

  }

  /**
   * The listener interface for receiving mouseEvent events. The class that is interested in processing a mouseEvent
   * event implements this interface, and the object created with that class is registered with a component using the
   * component's <code>addMouseEventListener<code> method. When the mouseEvent event occurs, that object's appropriate
   * method is invoked.
   *
   * @author dmo5cob
   */
  private final class MouseEventListener implements MouseListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseUp(final MouseEvent mouseevent) {
      // NA

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDown(final MouseEvent mouseevent) {

      WPLabelAssignNatPage.this.selectedColPostn = LayerUtil.convertColumnPosition(WPLabelAssignNatPage.this.natTable,
          WPLabelAssignNatPage.this.natTable.getColumnPositionByX(mouseevent.x),
          WPLabelAssignNatPage.this.customFilterGridLayer.getColumnHeaderDataLayer());
      WPLabelAssignNatPage.this.selectedRowPostn = LayerUtil.convertRowPosition(WPLabelAssignNatPage.this.natTable,
          WPLabelAssignNatPage.this.natTable.getRowPositionByY(mouseevent.y),
          WPLabelAssignNatPage.this.customFilterGridLayer.getBodyDataLayer());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDoubleClick(final MouseEvent mouseevent) {
      // NA
    }
  }

  /**
   * The Class FilterRowCustomConfiguration.
   */
  private static class FilterRowCustomConfiguration extends AbstractRegistryConfiguration {

    /** The double display converter. */
    final DefaultDoubleDisplayConverter doubleDisplayConverter = new DefaultDoubleDisplayConverter();

    /**
     * Gets the ignorecase comparator.
     *
     * @return the ignorecase comparator
     */
    private static Comparator<?> getIgnorecaseComparator() {
      return (final String obj1, final String obj2) -> obj1.compareToIgnoreCase(obj2);
    }


    /**
     * {@inheritDoc}
     */
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

      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 0);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 3);

    }
  }

  /**
   * This method creates filter text.
   */
  private void createFilterTxt() {
    GridData gridData = getFilterTxtGridData();
    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      String text = WPLabelAssignNatPage.this.filterTxt.getText().trim();
      WPLabelAssignNatPage.this.wpLabelAssignFilterMatcher.setFilterText(text, true);
      WPLabelAssignNatPage.this.customFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
      WPLabelAssignNatPage.this.customFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(WPLabelAssignNatPage.this.customFilterGridLayer.getSortableColumnHeaderLayer()));
      setStatusBarMessage(false);
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
   * This method adds right click menu for tableviewer.
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {
      IStructuredSelection selection =
          (IStructuredSelection) WPLabelAssignNatPage.this.selectionProvider.getSelection();
      final Object firstElement = selection.getFirstElement();
      if ((firstElement != null) && (selection.size() != 0) &&
          ((this.a2lWPInfoBO.getA2lWpParamMappingModel() != null) &&
              (this.a2lWPInfoBO.getA2lWpParamMappingModel().getSelectedWpDefnVersionId() != null))) {
        editPar2WP(selection, mgr);
        showWP(selection, mgr);

        menuMgr.add(new Separator());
        createCopyAction(selection, mgr);
        createPasteRespAction(selection, mgr);

        menuMgr.add(new Separator());
        createTakeOverAction(selection, mgr);

        menuMgr.add(new Separator());
        createResetRespAction(selection, mgr);

        menuMgr.add(new Separator());
        addExportAsLabAction(menuMgr, selection);
        addExportAsFunAction(menuMgr, selection);
      }
      menuMgr.add(new Separator());
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
  private void addExportAsFunAction(final MenuManager menuMgr, final IStructuredSelection selection) {

    Set<String> functions = new HashSet<>();
    for (Object object : selection.toList()) {
      if (object instanceof A2LWpParamInfo) {
        functions.add(((A2LWpParamInfo) object).getFuncName());
      }
    }
    menuMgr.add(new LabFunExportAction(null, functions, OUTPUT_FILE_TYPE.FUN, null));
  }

  /**
   * @param menuMgr
   * @param selection
   */
  private void addExportAsLabAction(final MenuManager menuMgr, final IStructuredSelection selection) {
    Set<String> labelSet = new HashSet<>();
    for (Object object : selection.toList()) {
      if (object instanceof A2LWpParamInfo) {
        labelSet.add(((A2LWpParamInfo) object).getParamName());
      }
    }

    menuMgr.add(new LabFunExportAction(labelSet, null, OUTPUT_FILE_TYPE.LAB, null));
  }

  /**
   * Creates the reset resp action.
   *
   * @param selection the selection
   * @param mgr the mgr
   */
  private void createResetRespAction(final IStructuredSelection selection, final IMenuManager mgr) {

    List<A2LWpParamInfo> a2lWpParamInfoList = selection.toList();
    final Action resetRespAction = new Action() {

      @Override
      public void run() {
        A2lWpParamMappingServiceClient client = new A2lWpParamMappingServiceClient();
        A2lWpParamMappingUpdateModel updateModel = new A2lWpParamMappingUpdateModel();
        for (A2LWpParamInfo a2lWpParamInfo : a2lWpParamInfoList) {
          A2lWpParamMapping updateObject = WPLabelAssignNatPage.this.a2lWPInfoBO.getA2lWpParamMappingModel()
              .getA2lWpParamMapping().get(a2lWpParamInfo.getA2lWpParamMappingId());

          // Get WP defintion version related model and overwrite to existing row details - IMP : need to implement else
          // case
          if (updateObject.getWpDefnVersionId().longValue() == WPLabelAssignNatPage.this.a2lWPInfoBO.getA2lWpDefnModel()
              .getSelectedWpDefnVersionId()) {

            updateA2lWpParamMapping(updateModel, updateObject);
          }
        }
        try {
          client.updateA2lWpParamMapping(updateModel,
              WPLabelAssignNatPage.this.a2lWPInfoBO.getPidcA2lBo().getPidcA2l());
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }

      }
    };
    resetRespAction.setText("Reset Responsibility to\"Inherit from WP definition\"");
    resetRespAction.setEnabled(false);
    for (A2LWpParamInfo a2lWpParamInfo : a2lWpParamInfoList) {
      if (!a2lWpParamInfo.isWpRespInherited() && WPLabelAssignNatPage.this.a2lWPInfoBO.isA2lWpParamMapModifiable() &&
          !isDefaultMapping(((A2LWpParamInfo) selection.getFirstElement()).getWpRespId())) {
        resetRespAction.setEnabled(true);
        break;
      }
    }
    mgr.add(resetRespAction);
  }

  /**
   * @param updateModel
   * @param updateObject
   */
  private void updateA2lWpParamMapping(final A2lWpParamMappingUpdateModel updateModel,
      final A2lWpParamMapping updateObject) {
    // if variant group level wp param mapping is set to InheritResp='Y' then,
    // delete the var grp level wp param mapping object
    if (WPLabelAssignNatPage.this.a2lWPInfoBO.getSelectedA2lVarGroup() == null) {
      // set resp to null
      updateObject.setParA2lRespId(null);
      updateObject.setWpRespInherited(true);
      updateModel.getA2lWpParamMappingToBeUpdated().put(updateObject.getId(), updateObject);

      boolean confirm = false;
      // check for var grp mappings
      Set<A2lWpResponsibility> wpRespSet = WPLabelAssignNatPage.this.a2lWPInfoBO.getA2lWpDefnModel()
          .getA2lWpRespNodeMergedMap().get(WPLabelAssignNatPage.this.a2lWPInfoBO.getA2lWpDefnModel().getWpRespMap()
              .get(updateObject.getWpRespId()).getName());
      Map<Long, A2lWpParamMapping> varLevelMappings = new HashMap<>();

      // means there are var grp level wp resps
      addVarGrpLevelMapping(updateObject, wpRespSet, varLevelMappings);

      if (CommonUtils.isNotEmpty(varLevelMappings)) {
        confirm = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Reset Responsibility",
            "There are variant group mappings available for this parameter. Do you want to inherit completely?" +
                "This will delete all variant group mappings. Click 'Yes' to proceed");
        if (confirm) {
          updateModel.getA2lWpParamMappingToBeDeleted().putAll(varLevelMappings);
        }
      }

    }
    else {
      updateObject.setParA2lRespId(null);
      updateObject.setWpRespInherited(true);
      updateModel.getA2lWpParamMappingToBeUpdated().put(updateObject.getId(), updateObject);
    }
  }

  /**
   * @param updateObject
   * @param wpRespSet
   * @param varLevelMappings
   */
  private void addVarGrpLevelMapping(final A2lWpParamMapping updateObject, final Set<A2lWpResponsibility> wpRespSet,
      final Map<Long, A2lWpParamMapping> varLevelMappings) {
    if (CommonUtils.isNotEmpty(wpRespSet) && (wpRespSet.size() > 1)) {
      for (A2lWpResponsibility a2lWpResponsibility : wpRespSet) {
        if (a2lWpResponsibility.getVariantGrpId() != null) {
          Map<Long, A2lWpParamMapping> paramMappingsMap = WPLabelAssignNatPage.this.a2lWPInfoBO.getParamWpRespResolver()
              .getParamMappingAtVarLevel().get(a2lWpResponsibility.getVariantGrpId());
          if (CommonUtils.isNotEmpty(paramMappingsMap) &&
              CommonUtils.isNotNull(paramMappingsMap.get(updateObject.getParamId()))) {
            varLevelMappings.put(paramMappingsMap.get(updateObject.getParamId()).getId(),
                paramMappingsMap.get(updateObject.getParamId()));
          }
        }
      }
    }
  }

  private boolean isDefaultMapping(final Long wpRespId) {
    return this.a2lWPInfoBO.getA2lWpDefnModel().getWpRespMap().get(wpRespId).getName()
        .equals(ApicConstants.DEFAULT_A2L_WP_NAME);
  }

  /**
   * Creates the take over action.
   *
   * @param selection the selection
   * @param mgr the mgr
   */
  private void createTakeOverAction(final IStructuredSelection selection, final IMenuManager mgr) {
    IMenuManager subMenu = new MenuManager("Take over for");
    subMenu.setRemoveAllWhenShown(true);
    if (selection.getFirstElement() instanceof A2LWpParamInfo) {
      A2LWpParamInfo rowObject = (A2LWpParamInfo) selection.getFirstElement();
      subMenu.setVisible((selection.size() == 1) && isMenuVisible(rowObject));

      subMenu.addMenuListener(imenumanager -> {
        imenumanager.add(takeOverForFunction(rowObject));
        imenumanager.add(takeOverForFilteredItem(rowObject));
      });
    }
    mgr.add(subMenu);
  }

  /**
   * Menu is visible if, 1. var grp is selected in details view and var grp level record is selected in editor 2.
   * default level is selected in details view and default level record is selected in editor
   *
   * @param rowObject
   * @return
   */
  private boolean isMenuVisible(final A2LWpParamInfo rowObject) {
    return ((rowObject.getVariantGroupId() == null) && (this.a2lWPInfoBO.getSelectedA2lVarGroup() == null)) ||
        ((rowObject.getVariantGroupId() != null) && (this.a2lWPInfoBO.getSelectedA2lVarGroup() != null));
  }

  /**
   * Take over for function.
   *
   * @param rowObject the selection
   * @return the action
   */
  private Action takeOverForFunction(final A2LWpParamInfo rowObject) {
    final Action functionAction = new Action() {

      @Override
      public void run() {
        if (!MessageDialogUtils.getConfirmMessageDialog("Take Over For Function",
            "Press OK to continue to take over Workpackage and Responsibility for all parameters for the function : " +
                rowObject.getFuncName())) {
          return;
        }
        WPLabelAssignNatPage.this.a2lWPInfoBO.takeOverForFunction(rowObject.getFuncName(), rowObject);
      }
    };

    functionAction.setText("Function");
    functionAction.setEnabled(WPLabelAssignNatPage.this.a2lWPInfoBO.isParamEditAllowed());
    return functionAction;
  }

  /**
   * Take over for filtered item.
   *
   * @param rowObject the row object
   * @return the action
   */
  private Action takeOverForFilteredItem(final A2LWpParamInfo rowObject) {
    final Action recFilterSelAction = new Action() {

      @Override
      public void run() {
        List<A2LWpParamInfo> filteredList = new ArrayList<>();
        filteredList.addAll(WPLabelAssignNatPage.this.customFilterGridLayer.getBodyDataProvider().getList());
        // Selected row object should be removed from filtered list
        filteredList.remove(rowObject);
        Set<Long> paramIdSet = new HashSet<>();
        filteredList.forEach(e -> paramIdSet.add(e.getParamId()));

        if (!MessageDialogUtils.getConfirmMessageDialog("Take Over For All Filtered Records",
            "Press OK to continue to take over selected Workpackage and Responsibility for all filtered parameters . No of parameters to be updated : " +
                (paramIdSet.size()))) {
          return;
        }
        WPLabelAssignNatPage.this.a2lWPInfoBO.takeOverForFilteredItems(rowObject, paramIdSet);
      }
    };

    recFilterSelAction.setText("All Records in filtered selection");
    recFilterSelAction.setEnabled(WPLabelAssignNatPage.this.a2lWPInfoBO.isParamEditAllowed());
    return recFilterSelAction;
  }

  /**
   * Show WP.
   *
   * @param mgr IMenuManager
   */
  private void showWP(final IStructuredSelection selection, final IMenuManager mgr) {

    List<A2LWpParamInfo> paramInfoList = selection.toList();

    List<A2lWpParamMapping> paramMappingList = new ArrayList<>();


    final Action showWPAction = new Action() {

      @Override
      public void run() {

        for (A2LWpParamInfo a2lWpParamInfo : paramInfoList) {
          paramMappingList.add(WPLabelAssignNatPage.this.a2lWPInfoBO.getA2lWpParamMappingObject(a2lWpParamInfo));
        }

        WPLabelAssignNatPage.this.wpListDialog =
            new ShowWpRespListDialog(WPLabelAssignNatPage.this.composite.getShell(),
                WPLabelAssignNatPage.this.a2lWPInfoBO.getA2lWpParamMappingModel().getSelectedWpDefnVersionId(),
                WPLabelAssignNatPage.this.a2lWPInfoBO, false, paramMappingList);


        // if ok pressed, update selected nattable rows with selected WP
        if ((WPLabelAssignNatPage.this.wpListDialog.open() == IDialogConstants.OK_ID) &&
            showConfirmation(paramMappingList)) {
          WPLabelAssignNatPage.this.updateWP(WPLabelAssignNatPage.this.wpListDialog.getA2lWpResp());
        }

      }
    };
    showWPAction.setText("Set Work Package");
    showWPAction.setEnabled(WPLabelAssignNatPage.this.a2lWPInfoBO.canShowAvailWps());
    mgr.add(showWPAction);

  }

  private boolean showConfirmation(final List<A2lWpParamMapping> paramMappingList) {
    boolean confirm = true;
    List<String> paramForDialog1 = new ArrayList<>();
    List<String> paramForDialog2 = new ArrayList<>();
    for (A2lWpParamMapping a2lWpParamMapping : paramMappingList) {

      // checking if existing selection of wp has variant group customizations
      Set<A2lWpResponsibility> wpRespSet = WPLabelAssignNatPage.this.a2lWPInfoBO.getA2lWpDefnModel()
          .getA2lWpRespNodeMergedMap().get(WPLabelAssignNatPage.this.a2lWPInfoBO.getA2lWpDefnModel().getWpRespMap()
              .get(a2lWpParamMapping.getWpRespId()).getName());

      // if assigned wp has var grp customizations and only virtual records are present, show dialog
      if ((WPLabelAssignNatPage.this.a2lWPInfoBO.getSelectedA2lVarGroup() == null) &&
          CommonUtils.isNotEmpty(wpRespSet) && (wpRespSet.size() > 1) &&
          !WPLabelAssignNatPage.this.a2lWPInfoBO.paramHasVgLevelRecords(a2lWpParamMapping.getParamId())) {
        paramForDialog1.add(a2lWpParamMapping.getName());
      }

      // check if actual records are available in vg level, show dialog
      if ((WPLabelAssignNatPage.this.a2lWPInfoBO.getSelectedA2lVarGroup() == null) &&
          WPLabelAssignNatPage.this.a2lWPInfoBO.paramHasVgLevelRecords(a2lWpParamMapping.getParamId())) {
        paramForDialog2.add(a2lWpParamMapping.getName());
      }
    }


    if (!paramForDialog1.isEmpty()) {
      confirm = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Workpackage selection",
          "Existing workpackage(s) assigned to " + String.join(",", paramForDialog1) +
              ", has variant group mappings. Do you " +
              "want to continue with new workpackage selection? Click 'Yes' to proceed.");
    }
    if (confirm && !paramForDialog2.isEmpty()) {
      confirm = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Workpackage selection",
          "Parameter(s) " + String.join(",", paramForDialog2) +
              ", has variant group level mappings available. Do you " +
              "want to continue with new workpackage selection? Click 'Yes' to proceed.");
    }
    return confirm;
  }

  /**
   * Edits the par 2 WP.
   *
   * @param mgr the mgr
   */
  private void editPar2WP(final IStructuredSelection selection, final IMenuManager mgr) {
    final Action editPar2WPAction = new Action() {

      @Override
      public void run() {
        if (WPLabelAssignNatPage.this.a2lWPInfoBO.isParamEditAllowed()) {
          A2lVariantGroup selectedA2lVarGroup = WPLabelAssignNatPage.this.a2lWPInfoBO.getSelectedA2lVarGroup();
          A2LWpParamInfo paramInfo = (A2LWpParamInfo) WPLabelAssignNatPage.this.selectedObj;


          try {
            WpRespActionSet wpRespActionSet = new WpRespActionSet();
            paramInfo = wpRespActionSet.createWpRespAndMapping(selectedA2lVarGroup, paramInfo,
                WPLabelAssignNatPage.this.a2lWPInfoBO);

            UpdateWpParamDetailsDialog editPar2WPDialog =
                new UpdateWpParamDetailsDialog(WPLabelAssignNatPage.this.composite.getShell(), paramInfo,
                    WPLabelAssignNatPage.this.a2lWPInfoBO.getA2lWpParamMappingModel().getSelectedWpDefnVersionId(),
                    WPLabelAssignNatPage.this.a2lWPInfoBO);
            editPar2WPDialog.open();
          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }

        }
      }
    };
    editPar2WPAction.setText("Edit");
    Long wpRespId = ((A2LWpParamInfo) WPLabelAssignNatPage.this.selectedObj).getWpRespId();
    if (CommonUtils.isNull(wpRespId)) {
      editPar2WPAction.setEnabled(false);
    }
    else {
      // enable edit option only for single selection of param mapping
      editPar2WPAction
          .setEnabled((selection.size() == 1) && WPLabelAssignNatPage.this.a2lWPInfoBO.isParamEditAllowed());
    }
    mgr.add(editPar2WPAction);
  }

  /**
   * Creates the copy action.
   *
   * @param selection the selection
   * @param mgr the mgr
   */
  private void createCopyAction(final IStructuredSelection selection, final IMenuManager mgr) {
    A2LWpParamInfo rowObject = (A2LWpParamInfo) selection.getFirstElement();
    final Action copyAction = new Action() {

      @Override
      public void run() {
        CopyDataToClipboardCommand copyCommand = new CopyDataToClipboardCommand("\t",
            System.getProperty("line.separator"), WPLabelAssignNatPage.this.getNatTable().getConfigRegistry());
        WPLabelAssignNatPage.this.getNatTable().doCommand(copyCommand);
        final A2LWpParamInfo copiedObj = (A2LWpParamInfo) (selection.getFirstElement());
        ICDMClipboard.getInstance().setCopiedObject(copiedObj);

      }
    };
    copyAction.setText("Copy");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EXPORT_DATA_16X16);
    copyAction.setImageDescriptor(imageDesc);
    copyAction.setEnabled((selection.size() == 1) && isMenuVisible(rowObject) && (rowObject.getWpRespId() != null));
    mgr.add(copyAction);
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
        setCopiedA2lWpParamMappingField(selection);
        WPLabelAssignNatPage.this.getNatTable().refresh();

      }
    };
    pasteAction.setText("Paste");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PASTE_16X16);
    pasteAction.setImageDescriptor(imageDesc);
    if (CommonUtils.isNull(ICDMClipboard.getInstance().getCopiedObject())) {
      pasteAction.setEnabled(false);
    }
    else {
      pasteAction.setEnabled(WPLabelAssignNatPage.this.a2lWPInfoBO.isParamEditAllowed());
    }
    mgr.add(pasteAction);
  }

  /**
   * Sets the A2lWpParamMapping fields to be pasted.
   *
   * @param selection IStructuredSelection
   */
  public void setCopiedA2lWpParamMappingField(final IStructuredSelection selection) {
    /*
     * List of row objects selected for pasting the copied Workpackage ,Responsibility,Name at Customer or
     * A2lWpParamMapping Object
     */
    @SuppressWarnings("unchecked")
    ArrayList<A2LWpParamInfo> rowObjToUpdate = new ArrayList<>(selection.toList());
    if (rowObjToUpdate.isEmpty()) {
      CDMLogger.getInstance().errorDialog("No items to paste !", Activator.PLUGIN_ID);
      return;
    }
    // take contents from the internal clipboard
    final Object copiedObject = ICDMClipboard.getInstance().getCopiedObject();
    // to paste Workpackage
    final A2LWpParamInfo copiedA2LWpParamInfo = (A2LWpParamInfo) copiedObject;
    if ((null != WPLabelAssignNatPage.this.getCustomFilterGridLayer().getColumnHeaderDataLayer().getDataProvider()
        .getDataValue(WPLabelAssignNatPage.this.selectedColPostn, 0)) &&
        (WPLabelAssignNatPage.this.getCustomFilterGridLayer().getColumnHeaderDataLayer().getDataProvider()
            .getDataValue(WPLabelAssignNatPage.this.selectedColPostn, 0)
            .equals(WPLabelAssignNatPage.getWpHeaderLabel()))) {
      invokeCommandToSaveWP(rowObjToUpdate, copiedA2LWpParamInfo);
    }
    // to paste Responsibility
    else if ((null != WPLabelAssignNatPage.this.getCustomFilterGridLayer().getColumnHeaderDataLayer().getDataProvider()
        .getDataValue(WPLabelAssignNatPage.this.selectedColPostn, 0)) &&
        (WPLabelAssignNatPage.this.getCustomFilterGridLayer().getColumnHeaderDataLayer().getDataProvider()
            .getDataValue(WPLabelAssignNatPage.this.selectedColPostn, 0)
            .equals(WPLabelAssignNatPage.getRespHeaderLabel()))) {
      invokeCommandToSaveResp(rowObjToUpdate, copiedA2LWpParamInfo);
    }
    // to paste Name at Customer
    else if ((null != WPLabelAssignNatPage.this.getCustomFilterGridLayer().getColumnHeaderDataLayer().getDataProvider()
        .getDataValue(WPLabelAssignNatPage.this.selectedColPostn, 0)) &&
        (WPLabelAssignNatPage.this.getCustomFilterGridLayer().getColumnHeaderDataLayer().getDataProvider()
            .getDataValue(WPLabelAssignNatPage.this.selectedColPostn, 0)
            .equals(WPLabelAssignNatPage.getNameAtCustomerLabel()))) {
      // take contents from the internal clipboard
      ILayerCell[][] copiedCells = getNatTable().getInternalCellClipboard().getCopiedCells();
      String copiedCellVal = getCopiedCellVal(copiedCells);
      invokeCommandToSaveNameAtCust(rowObjToUpdate, copiedA2LWpParamInfo, copiedCellVal);
    }
    // to paste entire A2lWpParamMapping object
    else {
      invokeCommandToSaveA2lWpParam(rowObjToUpdate, copiedA2LWpParamInfo);
    }
  }

  /**
   * @param copiedCells ILayerCell
   * @return String
   */
  public String getCopiedCellVal(final ILayerCell[][] copiedCells) {
    String copiedCellVal = null;
    if (null != copiedCells) {
      for (ILayerCell[] iLayerCells : copiedCells) {
        for (ILayerCell iLayerCell : iLayerCells) {
          if (null != iLayerCell.getDataValue()) {
            copiedCellVal = (iLayerCell.getDataValue().toString());
          }
        }
      }
    }
    return copiedCellVal;
  }

  /**
   * @param rowObjToUpdate
   * @param copiedA2LWpParamInfo
   * @param copiedCellVal
   */
  private void invokeCommandToSaveNameAtCust(final ArrayList<A2LWpParamInfo> rowObjToUpdate,
      final A2LWpParamInfo copiedRow, final String copiedCellVal) {
    Map<Long, A2lWpParamMapping> a2lWpParamMapToUpdate = new HashMap<>();
    A2lWpParamMapping a2lWpParamClone = null;
    Set<A2lWpParamMapping> a2lWpParamSetToCreate = new HashSet<>();
    // invoke command to save into db
    for (A2LWpParamInfo a2lWpParamInfo : rowObjToUpdate) {
      if (a2lWpParamInfo.getA2lWpParamMappingId() == null) {
        A2lWpParamMapping paramMappingToCreate = new A2lWpParamMapping();
        // create A2lWpParamMapping object if parameter is not mapped to workpackage
        paramMappingToCreate.setParamId(a2lWpParamInfo.getParamId());
        paramMappingToCreate.setWpRespId(copiedRow.getWpRespId());

        paramMappingToCreate.setWpNameCust(copiedCellVal);
        // set Inherite Fields
        setInheritedField(copiedRow, paramMappingToCreate);
        a2lWpParamSetToCreate.add(paramMappingToCreate);
      }
      else {
        A2lWpParamMapping a2lWpParamMappingToPaste = this.a2lWPInfoBO.getA2lWpParamMappingModel().getA2lWpParamMapping()
            .get(a2lWpParamInfo.getA2lWpParamMappingId());
        a2lWpParamClone = a2lWpParamMappingToPaste.clone();
        a2lWpParamClone.setWpNameCust(copiedCellVal);
        a2lWpParamMapToUpdate.put(a2lWpParamClone.getId(), a2lWpParamClone);
      }
    }
    updateParamMappings(a2lWpParamMapToUpdate, a2lWpParamSetToCreate);

  }

  /**
   * Invoke command to save A 2 l wp param.
   *
   * @param rowObjToUpdate the row obj to update
   * @param copiedRow the copied row
   */
  private void invokeCommandToSaveA2lWpParam(final List<A2LWpParamInfo> rowObjToUpdate,
      final A2LWpParamInfo copiedRow) {
    Map<Long, A2lWpParamMapping> a2lWpParamMapToUpdate = new HashMap<>();
    A2lWpParamMapping a2lWpParamClone = null;
    Set<A2lWpParamMapping> a2lWpParamSetToCreate = new HashSet<>();
    // invoke command to save into db
    for (A2LWpParamInfo a2lWpParamInfo : rowObjToUpdate) {
      if (a2lWpParamInfo.getA2lWpParamMappingId() == null) {
        A2lWpParamMapping paramMappingToCreate = new A2lWpParamMapping();
        // create A2lWpParamMapping object if parameter is not mapped to workpackage
        createA2lWpParam(copiedRow, a2lWpParamInfo, paramMappingToCreate);
        // Inherited Fields
        paramMappingToCreate.setWpRespInherited(true);
        paramMappingToCreate.setWpNameCustInherited(true);
        a2lWpParamSetToCreate.add(paramMappingToCreate);
      }
      else {
        A2lWpParamMapping a2lWpParamMappingToPaste = this.a2lWPInfoBO.getA2lWpParamMappingModel().getA2lWpParamMapping()
            .get(a2lWpParamInfo.getA2lWpParamMappingId());
        a2lWpParamClone = updateA2lWpParam(copiedRow, a2lWpParamMappingToPaste);
        a2lWpParamMapToUpdate.put(a2lWpParamClone.getId(), a2lWpParamClone);
      }
    }
    updateParamMappings(a2lWpParamMapToUpdate, a2lWpParamSetToCreate);
  }

  /**
   * Invoke command to save workpackage
   *
   * @param rowObjToUpdate the row obj to update
   * @param copiedRow the copied row
   */
  private void invokeCommandToSaveWP(final List<A2LWpParamInfo> rowObjToUpdate, final A2LWpParamInfo copiedRow) {
    Map<Long, A2lWpParamMapping> a2lWpParamMapToUpdate = new HashMap<>();
    A2lWpParamMapping a2lWpParamClone = null;
    Set<A2lWpParamMapping> a2lWpParamSetToCreate = new HashSet<>();
    // invoke command to save into db
    for (A2LWpParamInfo a2lWpParamInfo : rowObjToUpdate) {
      if (a2lWpParamInfo.getA2lWpParamMappingId() == null) {
        A2lWpParamMapping paramMappingToCreate = new A2lWpParamMapping();
        // create A2lWpParamMapping object if parameter is not mapped to workpackage
        paramMappingToCreate.setParamId(a2lWpParamInfo.getParamId());
        paramMappingToCreate.setWpRespId(copiedRow.getWpRespId());
        a2lWpParamSetToCreate.add(paramMappingToCreate);
      }
      else {
        A2lWpParamMapping a2lWpParamMappingToPaste = this.a2lWPInfoBO.getA2lWpParamMappingModel().getA2lWpParamMapping()
            .get(a2lWpParamInfo.getA2lWpParamMappingId());
        a2lWpParamClone = a2lWpParamMappingToPaste.clone();
        a2lWpParamClone.setWpRespId(copiedRow.getWpRespId());
        a2lWpParamMapToUpdate.put(a2lWpParamClone.getId(), a2lWpParamClone);
      }
    }
    updateParamMappings(a2lWpParamMapToUpdate, a2lWpParamSetToCreate);

  }

  private void updateParamMappings(final Map<Long, A2lWpParamMapping> a2lWpParamMapToUpdate,
      final Set<A2lWpParamMapping> a2lWpParamSetToCreate) {
    try {

      if (CommonUtils.isNotEmpty(a2lWpParamMapToUpdate) || CommonUtils.isNotEmpty(a2lWpParamSetToCreate)) {
        A2lWpParamMappingServiceClient client = new A2lWpParamMappingServiceClient();
        A2lWpParamMappingUpdateModel updateModel = new A2lWpParamMappingUpdateModel();
        updateModel.getA2lWpParamMappingToBeCreated().addAll(a2lWpParamSetToCreate);
        updateModel.getA2lWpParamMappingToBeUpdated().putAll(a2lWpParamMapToUpdate);
        client.updateA2lWpParamMapping(updateModel, this.a2lWPInfoBO.getPidcA2lBo().getPidcA2l());

      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * Invoke command to save Responsibility.
   *
   * @param rowObjToUpdate the row obj to update
   * @param copiedRow the copied row
   */
  private void invokeCommandToSaveResp(final List<A2LWpParamInfo> rowObjToUpdate, final A2LWpParamInfo copiedRow) {
    Map<Long, A2lWpParamMapping> a2lWpParamMapToUpdate = new HashMap<>();
    A2lWpParamMapping a2lWpParamClone = null;
    Set<A2lWpParamMapping> a2lWpParamSetToCreate = new HashSet<>();
    // invoke command to save into db
    for (A2LWpParamInfo a2lWpParamInfo : rowObjToUpdate) {
      if (a2lWpParamInfo.getA2lWpParamMappingId() == null) {
        A2lWpParamMapping paramMappingToCreate = new A2lWpParamMapping();
        // create A2lWpParamMapping object if parameter is not mapped to workpackage
        paramMappingToCreate.setParamId(a2lWpParamInfo.getParamId());
        paramMappingToCreate.setWpRespId(copiedRow.getWpRespId());

        paramMappingToCreate.setParA2lRespId(copiedRow.getA2lRespId());

        // set inherited fields
        setInheritedField(copiedRow, paramMappingToCreate);
        a2lWpParamSetToCreate.add(paramMappingToCreate);
      }
      else {
        A2lWpParamMapping a2lWpParamMappingToPaste = this.a2lWPInfoBO.getA2lWpParamMappingModel().getA2lWpParamMapping()
            .get(a2lWpParamInfo.getA2lWpParamMappingId());
        a2lWpParamClone = a2lWpParamMappingToPaste.clone();
        a2lWpParamClone.setParA2lRespId(copiedRow.getA2lRespId());

        a2lWpParamMapToUpdate.put(a2lWpParamClone.getId(), a2lWpParamClone);
      }
    }
    updateParamMappings(a2lWpParamMapToUpdate, a2lWpParamSetToCreate);
  }

  /**
   * @param copiedRow A2LWpParamInfo
   * @param a2lWpParamMappingToPaste A2lWpParamMapping corresponding to the rows where paste operation is to be
   *          performed
   * @return A2lWpParamMapping object that holds edited values
   */
  public A2lWpParamMapping updateA2lWpParam(final A2LWpParamInfo copiedRow,
      final A2lWpParamMapping a2lWpParamMappingToPaste) {
    A2lWpParamMapping a2lWpParamClone;
    a2lWpParamClone = a2lWpParamMappingToPaste.clone();
    a2lWpParamClone.setWpRespId(copiedRow.getWpRespId());

    a2lWpParamClone.setParA2lRespId(copiedRow.getA2lRespId());

    a2lWpParamClone.setWpNameCust(copiedRow.getWpNameCust());
    a2lWpParamClone.setWpNameCustInherited(true);
    a2lWpParamClone.setWpRespInherited(true);
    return a2lWpParamClone;
  }

  /**
   * @param copiedRow A2LWpParamInfo corresponding to the row where copy opeartion is performed
   * @param a2lWpParamInfo corresponding to the row where paste is performed
   * @param paramMappingToCreate A2lWpParamMapping
   */
  public void createA2lWpParam(final A2LWpParamInfo copiedRow, final A2LWpParamInfo a2lWpParamInfo,
      final A2lWpParamMapping paramMappingToCreate) {
    paramMappingToCreate.setParamId(a2lWpParamInfo.getParamId());
    paramMappingToCreate.setWpRespId(copiedRow.getWpRespId());

    paramMappingToCreate.setParA2lRespId(copiedRow.getA2lRespId());

    paramMappingToCreate.setWpNameCust(copiedRow.getWpNameCust());
  }

  /**
   * @param copiedA2lWpParamInfo A2LWpParamInfo
   * @param paramMappingToCreate A2lWpParamMapping
   */
  public void setInheritedField(final A2LWpParamInfo copiedA2lWpParamInfo,
      final A2lWpParamMapping paramMappingToCreate) {
    // same wp inherited
    if (copiedA2lWpParamInfo.getWpRespId().equals(paramMappingToCreate.getWpRespId())) {
      paramMappingToCreate.setWpRespInherited(true);
      paramMappingToCreate.setWpNameCustInherited(true);
    }
    else {
      paramMappingToCreate.setWpRespInherited(false);
      paramMappingToCreate.setWpNameCustInherited(false);
    }
  }

  /**
   * Creates the tool bar action.
   */
  private void createToolBarAction() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = this.toolBarManager.createControl(this.section);
    final Separator separator = new Separator();
    addHelpAction(this.toolBarManager);
    this.toolBarActionSet = new WPLabelAssignToolBarFilterActionSet(this);
    this.toolBarManager.add(separator);
    this.toolBarActionSet.complianceFilterAction(getToolBarFilters(), this.toolBarManager);
    this.toolBarActionSet.nonComplianceFilterAction(getToolBarFilters(), this.toolBarManager);
    this.toolBarManager.add(separator);
    this.toolBarActionSet.qSSDFilterAction(getToolBarFilters(), this.toolBarManager);
    this.toolBarActionSet.nonQSSDFilterAction(getToolBarFilters(), this.toolBarManager);
    this.toolBarManager.add(separator);
    this.toolBarActionSet.readOnlyAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarActionSet.notReadOnlyAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);
    this.toolBarActionSet.dependentParamAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarActionSet.notdependentParamAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);
    this.toolBarActionSet.isWPNotAssignedFilter(getToolBarFilters(), this.toolBarManager);
    this.toolBarActionSet.isWPAssignedFilter(getToolBarFilters(), this.toolBarManager);
    this.toolBarManager.add(separator);
    this.toolBarActionSet.responsibleNotAssignedFilter(getToolBarFilters(), this.toolBarManager);
    this.toolBarActionSet.responsibleInheritedFilter(getToolBarFilters(), this.toolBarManager);
    this.toolBarActionSet.responsibleAssignedFilter(getToolBarFilters(), this.toolBarManager);
    this.toolBarManager.add(separator);
    this.toolBarActionSet.nameAtCustomerNotAssignedFilter(getToolBarFilters(), this.toolBarManager);
    this.toolBarActionSet.nameAtCustomerInheritedFilter(getToolBarFilters(), this.toolBarManager);
    this.toolBarActionSet.nameAtCustomerAssignedFilter(getToolBarFilters(), this.toolBarManager);
    this.toolBarManager.add(separator);
    this.toolBarActionSet.variantGroupAssignedFilter(getToolBarFilters(), this.toolBarManager);
    this.toolBarActionSet.hideVariantGroupAssignedFilter(getToolBarFilters(), this.toolBarManager);
    this.toolBarManager.add(separator);
    this.toolBarManager.add(separator);
    // Filter for the rows with lables in LAB files uploaded
    this.toolBarActionSet.createWithLABParamAction(getToolBarFilters(), this.toolBarManager);
    // Filter for the rows without lables in LAB files uploaded
    this.toolBarActionSet.createWithoutLABParamAction(getToolBarFilters(), this.toolBarManager);

    this.importAction = new WpRespActionSet();
    Long pidcVersionId = this.a2lContentsEditorInput.getPidcVersion().getId();
    Long a2lFileId = this.a2lContentsEditorInput.getA2lFile().getId();
    A2lWpParamMappingModel a2lWpParamMappingModel = this.a2lWPInfoBO.getA2lWpParamMappingModel();
    Long selectedWpDefnVersionId =
        a2lWpParamMappingModel != null ? a2lWpParamMappingModel.getSelectedWpDefnVersionId() : null;

    this.form.getToolBarManager().add(
        this.importAction.par2WPAssignmentAction(this.a2lWPInfoBO, this.a2lWPInfoBO.getPidcA2lBo().getPidcVersion()));
    this.form.getToolBarManager().add(separator);
    this.form.getToolBarManager().add(this.importAction.copyVersToWorkingSetAction(this.a2lWPInfoBO));
    this.form.getToolBarManager().add(this.importAction.addActiveVersionAction(this.a2lWPInfoBO));

    this.form.getToolBarManager().add(separator);

    this.form.getToolBarManager().add(this.importAction.createImportFromA2lGroupsAction(this.a2lWPInfoBO));
    this.form.getToolBarManager().add(this.importAction.createImportWpRespFromFC2WPAction(pidcVersionId, a2lFileId,
        selectedWpDefnVersionId, true, this.a2lWPInfoBO));
    this.form.getToolBarManager().add(this.importAction.createWpsFromFunctionsAction(this.a2lWPInfoBO));

    this.form.getToolBarManager().add(this.importAction.createImportWpRespFromExcelAction(pidcVersionId, a2lFileId,
        selectedWpDefnVersionId, this.a2lWPInfoBO));
    this.form.getToolBarManager().add(separator);

    this.form.getToolBarManager().add(this.importAction.createLabAction(this));
    this.form.getToolBarManager().add(this.importAction.createClearLabAction(this));

    this.form.getToolBarManager().add(separator);

    this.form.getToolBarManager().update(true);

    this.form.getToolBarManager().add(separator);

    this.form.getToolBarManager().add(this.importAction.restWorkSplitAction(this.a2lWPInfoBO));

    addResetAllFiltersAction();

    this.form.getToolBarManager().add(this.importAction.createEditAction(this.composite, this));
    this.form.getToolBarManager().update(true);
    this.form.setToolBarVerticalAlignment(SWT.TOP);
    this.section.setTextClient(toolbar);
  }


  /**
   * Add reset filter button ICDM-1207.
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.customFilterGridLayer);
    addResetFiltersAction();
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
    setRetainSelObj(this.selectedObj);

    if (this.natTable != null) {
      refreshBasedOnStructureViewSelection();
    }
    this.customFilterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);

    // Refreshes the Group By model if any after loading/clearing data from CDFX/VCDM
    final CustomGroupByDataLayer bodyDataLayer = this.customFilterGridLayer.getBodyDataLayer();
    if (bodyDataLayer.getTreeRowModel().hasChildren(0)) {
      Display.getDefault().syncExec(() -> {
        bodyDataLayer.killCache();
        bodyDataLayer.updateTree();
        setStatusBarMessage(getGroupByHeaderLayer(), false);
      });
    }
    if (null != this.selectedObj) {
      setSelectedObj(this.a2lWPInfoBO.getA2lWParamInfoMap().get(((A2LWpParamInfo) this.selectedObj).getParamId()));
      setRetainSelObj(this.selectedObj);
    }
    retainSelObj();
    toggleToolbar();
  }

  /**
   *
   */
  private void refreshBasedOnStructureViewSelection() {

    if ((this.a2lWPInfoBO.getSelectedA2lVarGroup() != null) &&
        (null != this.a2lWPInfoBO.getA2lWParamInfoForVarGrp().get(this.a2lWPInfoBO.getSelectedA2lVarGroup().getId()))) {
      this.customFilterGridLayer.getEventList().clear();
      this.customFilterGridLayer.getEventList().addAll(
          this.a2lWPInfoBO.getA2lWParamInfoForVarGrp().get(this.a2lWPInfoBO.getSelectedA2lVarGroup().getId()).values());
    }
    else {
      this.customFilterGridLayer.getEventList().clear();
      this.customFilterGridLayer.getEventList().addAll(this.a2lWPInfoBO.getA2lWParamInfoSet());
    }
    this.natTable.redraw();
  }


  /**
   *
   */
  private void toggleToolbar() {


    boolean wpInfoModifiable = this.a2lWPInfoBO.isEditable();
    if (this.importAction != null) {
      this.importAction.getImportWpRespFromExcel().setEnabled(wpInfoModifiable);
      this.importAction.getImportWpRespFromFC2WP().setEnabled(wpInfoModifiable);
      this.importAction.getCreateWpFromFuncsAction().setEnabled(wpInfoModifiable);
      this.importAction.getPar2wpAction().setEnabled(wpInfoModifiable);
      this.importAction.getImportWpParamFromA2lGrps().setEnabled(this.a2lWPInfoBO.canImportGroups());
      this.importAction.getAddActiveVersAction()
          .setEnabled(wpInfoModifiable && this.a2lWPInfoBO.getPidcA2lBo().getPidcA2l().isWorkingSetModified());
      this.importAction.getImportLABAction().setEnabled(wpInfoModifiable);
      this.importAction.getCopyVersToWokingSet().setEnabled(wpInfoModifiable);
      this.importAction.getResetWorkSplitAction().setEnabled(wpInfoModifiable);
    }
  }


  /**
   * Reconstruct nat table.
   */
  public void reconstructNatTable() {
    setRetainSelObj(this.retainSelObj);
    this.natTable.dispose();
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
    if (this.natTable != null) {
      this.natTable.doCommand(new StructuralRefreshCommand());
      this.natTable.doCommand(new VisualRefreshCommand());
      this.natTable.refresh();
    }
    if (null != this.selectedObj) {
      setSelectedObj(this.a2lWPInfoBO.getA2lWParamInfoMap().get(((A2LWpParamInfo) this.selectedObj).getParamId()));
      setRetainSelObj(this.selectedObj);
    }
    retainSelObj();
  }

  /**
   *
   */
  public void retainSelObj() {
    boolean setSelection = false;
    if (this.retainSelObj != null) {
      this.selectionProvider.setSelection(new StructuredSelection(this.retainSelObj));
      setSelectedObj(this.retainSelObj);
      setSelection = true;
      this.natTable.setFocus();
    }
    // setting the default selection to the first row of tableviewer
    if (!setSelection && (this.natTable != null) && (this.natTable.getRowCount() > 0)) {
      this.natTable.doCommand(new ShowRowInViewportCommand(this.customFilterGridLayer.getBodyLayer().getViewportLayer(),
          this.selectedRowPostn));
      final IStructuredSelection selection = (IStructuredSelection) getSelectionProvider().getSelection();
      if ((selection != null) && (!selection.isEmpty())) {
        final Object element = selection.getFirstElement();
        setSelectedObj(element);
      }
    }
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
   * input for status line.
   *
   * @param outlineSelection flag set according to selection made in viewPart or editor.
   */
  // ICDM-343
  @Override
  public void setStatusBarMessage(final boolean outlineSelection) {
    this.editor.updateStatusBar(outlineSelection, this.totTableRowCount,
        this.customFilterGridLayer != null ? this.customFilterGridLayer.getRowHeaderLayer().getPreferredRowCount() : 0);
  }

  /**
   * Gets the nat table.
   *
   * @return the natTable
   */
  public NatTable getNatTable() {
    return this.natTable;
  }

  /**
   * Gets the custom filter grid layer.
   *
   * @return the customFilterGridLayer
   */
  public CustomFilterGridLayer getCustomFilterGridLayer() {
    return this.customFilterGridLayer;
  }


  /**
   * Gets the selected col postn.
   *
   * @return the selectedColPostn
   */
  public int getSelectedColPostn() {
    return this.selectedColPostn;
  }

  /**
   * Gets the selected row postn.
   *
   * @return the selectedRowPostn
   */
  public int getSelectedRowPostn() {
    return this.selectedRowPostn;
  }

  /**
   * Gets the wp header label.
   *
   * @return String
   */
  public static String getWpHeaderLabel() {
    return WP_HEADER_LABEL;
  }

  /**
   * Gets the resp header label.
   *
   * @return String
   */
  public static String getRespHeaderLabel() {
    return RESP_HEADER_LABEL;
  }

  /**
   * @return the nameAtCustomerLabel
   */
  public static String getNameAtCustomerLabel() {
    return NAME_AT_CUSTOMER_LABEL;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (part instanceof OutlineViewPart)) {
      outlineSelectionListener(selection);
    }
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (part instanceof PIDCDetailsViewPart) &&
        (selection instanceof IStructuredSelection) && !selection.isEmpty() &&
        (((IStructuredSelection) selection).size() == CommonUIConstants.SINGLE_SELECTION)) {
      final Object selectedNode = ((IStructuredSelection) selection).getFirstElement();
      structureViewListener(selectedNode);
    }
  }

  /**
   * @param selectedNode Object
   */
  public void structureViewListener(final Object selectedNode) {
    if (this.lastMadeStructSel == null) {
      this.lastMadeStructSel = selectedNode;
    }

    else {
      if (this.lastMadeStructSel.equals(selectedNode)) {
        return;
      }
      this.lastMadeStructSel = selectedNode;
    }

    // A2l Variant Group Node is selected
    if (selectedNode instanceof A2lVariantGroup) {
      setTitle();
      this.a2lWPInfoBO.setSelectedA2lVarGroup((A2lVariantGroup) selectedNode);
      this.actionSet.refreshOutlinePages(false);
      refreshNatTable();
      this.a2lWPInfoBO.getSelectedA2lVarGroup().getId();
      this.toolBarActionSet.getIsVariantGroupAssignedAction().setEnabled(true);
      this.toolBarActionSet.getHideVariantGroupAssignedAction().setEnabled(true);
    }
    // Pidc Variant ,A2lWpDefinitionVersion or top level node (<DEFAULT> node) is selected
    if ((selectedNode instanceof PidcVariant) || (selectedNode instanceof String) ||
        (selectedNode instanceof A2lWpDefnVersion)) {
      setTitle();
      if (!(selectedNode instanceof PidcVariant)) {
        this.a2lWPInfoBO.setSelectedA2lVarGroup(null);
      }
      this.actionSet.refreshOutlinePages(false);
      refreshNatTable();
      this.toolBarActionSet.getIsVariantGroupAssignedAction().setEnabled(false);
      this.toolBarActionSet.getHideVariantGroupAssignedAction().setEnabled(false);
    }
    if (this.editor.getActivePage() == 3) {
      setStatusBarMessage(getGroupByHeaderLayer(), true);
    }
  }

  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMessage(false);
  }

  /**
   * Selection listener implementation for selections on outlineFilter.
   *
   * @param selection the selection
   */
  private void outlineSelectionListener(final ISelection selection) {
    this.outlineNatFilter.a2lOutlineSelectionListener(selection);
    // ICDM-859
    if (this.editor.getActivePage() == 3) {
      setStatusBarMessage(getGroupByHeaderLayer(), true);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    getSite().getPage().removeSelectionListener(this);
    super.dispose();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return ((A2LContentsEditorInput) this.editor.getEditorInput()).getDataHandler();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    this.a2lWPInfoBO.formVirtualRecords();
    refreshUIElements();
    Map<IModelType, Map<Long, ChangeData<?>>> consChangeData = dce.getConsChangeData();
    for (IModelType modelType : consChangeData.keySet()) {
      if (modelType == MODEL_TYPE.A2L_WP_DEFN_VERSION) {
        // refresh details page if there is a copy of PAR2WP
        refreshDetailsPage();
        break;
      }
    }
    if (this.editor.getActivePage() == 3) {
      setStatusBarMessage(false);
    }
  }

  /**
   * refresh UI Element
   */
  public void refreshUIElements() {
    if (CommonUtils.isNotNull(this.section)) {
      setTitle();
      setFormMsg();
    }
    if (CommonUtils.isNotNull(this.natTable)) {
      this.importAction.getEditAction().setEnabled((null != this.selectedObj) && this.a2lWPInfoBO.isParamEditAllowed());
      if (this.a2lWPInfoBO.isReconstructA2lWpParamMapApplicable()) {
        this.a2lWPInfoBO.constructA2lWpParamRespMap();
      }
      refreshNatTable();
      this.actionSet.refreshOutlinePages(false);
    }
  }

  /**
   * Gets the a 2 l WP info BO.
   *
   * @return the a2lWPInfoBO
   */
  public A2LWPInfoBO getA2lWPInfoBO() {
    return this.a2lWPInfoBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setActive(final boolean active) {
    if (this.editor.getActivePage() == 3) {
      this.a2lWPInfoBO.setCurrentPage(this.editor.getActivePage());
    }
    this.actionSet.refreshOutlinePages(true);
    refreshDetailsPage();
  }

  /**
   * refresh details page
   */
  private void refreshDetailsPage() {
    final PIDCDetailsViewPart viewPart = (PIDCDetailsViewPart) WorkbenchUtils.getView(PIDCDetailsViewPart.VIEW_ID);
    if ((viewPart != null) && (viewPart.getCurrentPage() instanceof A2LDetailsPage)) {
      A2LDetailsPage detailsPage = (A2LDetailsPage) viewPart.getCurrentPage();
      detailsPage.populateData(this.a2lWPInfoBO.getA2lWpParamMappingModel().getSelectedWpDefnVersionId());
      detailsPage.getAddVarGrpButton().setEnabled(this.a2lWPInfoBO.isWPInfoModifiable());
    }
  }

  /**
   * @param retainSelObj the retainSelObj to set
   */
  public void setRetainSelObj(final Object retainSelObj) {
    this.retainSelObj = retainSelObj;
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


  /**
   * @return the toolBarActionSet
   */
  public WPLabelAssignToolBarFilterActionSet getToolBarActionSet() {
    return this.toolBarActionSet;
  }


  /**
   * @param toolBarActionSet the toolBarActionSet to set
   */
  public void setToolBarActionSet(final WPLabelAssignToolBarFilterActionSet toolBarActionSet) {
    this.toolBarActionSet = toolBarActionSet;
  }

  /**
   * @return the toolBarFilters
   */
  public WPLabelAssignToolBarFilter getToolBarFilters() {
    return this.toolBarFilters;
  }

  /**
   * @param toolBarFilters the toolBarFilters to set
   */
  public void setToolBarFilters(final WPLabelAssignToolBarFilter toolBarFilters) {
    this.toolBarFilters = toolBarFilters;
  }
}
