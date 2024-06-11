/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;


import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
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
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;
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
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
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
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calcomp.externallink.creation.LinkCreator;
import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.CDFXFileExportAction;
import com.bosch.caltool.cdr.ui.actions.CalibrationStatusListExportAction;
import com.bosch.caltool.cdr.ui.actions.CdrReportToolBarActionSet;
import com.bosch.caltool.cdr.ui.actions.DataRvwReportNatMouseClickAction;
import com.bosch.caltool.cdr.ui.actions.ShowRelatedQnaireAction;
import com.bosch.caltool.cdr.ui.editors.CdrReportEditor;
import com.bosch.caltool.cdr.ui.editors.CdrReportEditorInput;
import com.bosch.caltool.cdr.ui.editors.natcolumnfilter.CdrReportAllColFilterMatcher;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.CdrReportLabelAccumulator;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.RvwReportEditConfiguration;
import com.bosch.caltool.cdr.ui.table.filters.CdrReportToolBarFilters;
import com.bosch.caltool.cdr.ui.views.providers.CdrReportInputToColumnConverter;
import com.bosch.caltool.cdr.ui.views.providers.CdrReportNatToolTip;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.ss.CalDataType;
import com.bosch.caltool.icdm.client.bo.ss.SeriesStatisticsInfo;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.editors.AbstractNatFormPage;
import com.bosch.caltool.icdm.common.ui.table.filters.A2LOutlineNatFilter;
import com.bosch.caltool.icdm.common.ui.utils.CalDataUtil;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVarRvwDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.COMPLI_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.caltool.icdm.model.dataassessment.DaCompareHexParam;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextUtil;


/**
 * ICDM-1697
 *
 * @author mkl2cob
 */
public class CdrReportListPage extends AbstractNatFormPage implements ISelectionListener {

  /**
   * column width
   */
  private static final int REVIWED_COL_WIDTH = 50;

  /**
   * column width
   */
  private static final int LATEST_A2L_COL_WIDTH = 80;

  /**
   * column width
   */
  private static final int LATEST_QNAIRE_COL_WIDTH = 100;

  /**
   * column width
   */
  private static final int CW_COL_WIDTH = 25;

  /**
   * column width
   */
  private static final int RESP_COL_WIDTH = 80;

  /**
   * column width
   */
  private static final int FUNC_VERS_COL_WIDTH = 50;

  /**
   * column width
   */
  private static final int FUNC_COLUMN_WIDTH = 100;

  /**
   * column width
   */
  private static final int PTYPE_COL_WIDTH = 80;

  /**
   * latest review column
   */
  private static final int LATEST_REVIEW_COL = 0;

  /**
   * custom comparator label
   */
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  /**
   * non scrollable form
   */
  private Form nonScrollableForm;

  /**
   * CDR Report Editor
   */
  private final CdrReportEditor editor;

  /**
   * Sash form instance
   */
  private SashForm mainComposite;

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * ScrolledComposite instance
   */
  private ScrolledComposite scrollComp;

  /**
   * RowSelectionProvider
   */
  private RowSelectionProvider<A2LParameter> selectionProvider;

  /**
   * Form instance
   */
  private Form form;

  /**
   * filter text
   */
  private Text filterTxt;

  /**
   * column index to label map
   */
  private Map<Integer, String> propertyToLabelMap;

  /**
   * data report filter grid layer
   */
  private CustomFilterGridLayer dataRprtFilterGridLayer;


  /**
   * NAT table instance
   */
  private CustomNATTable natTable;


  /**
   * parameter column width
   */
  private static final Integer PARAMETER_COLUMN_WIDTH = 150;

  // ICDM-2605 Column number incremented
  /**
   * constant for static column index
   */
  private int colIndexToFreeze = -1;
  /**
   * Column index which has dynamic contents based on reviews
   */
  public static final int STATIC_COL_INDEX = 16;

  /**
   * CDRRvwReportToolBarFilters
   */
  private CdrReportToolBarFilters toolBarFilters;

  /**
   * RvwReportAllColFilterMatcher
   */
  private CdrReportAllColFilterMatcher allColumnFilterMatcher;

  /**
   * integer for dynamic column count
   */
  private int dynamicColsCount;

  /**
   * intger for total table row count
   */
  private int totTableRowCount;

  /**
   * A2LOutlineNatFilter
   */
  private A2LOutlineNatFilter outlineNatFilter;

  /** The Constant COL_NM_COMPLI. */
  private static final String COL_NM_COMPLI_TYPE = "Compliance";

  /** The Constant COL_NM_TYPE. */
  private static final String COL_NM_TYPE = "Type";


  /** The Constant COL_INDEX_COMPLI. */
  private static final int COL_INDEX_COMPLI = 0;


  /** The Constant COL_INDEX_TYPE. */
  private static final int COL_INDEX_TYPE = 1;
  /**
   * Selected cell's column position
   */
  protected int selectedColPostn;
  /**
   * Selected cell's row position
   */
  protected int selectedRowPostn;

  private Section section;

  private ToolBarManager toolBarManager;

  /**
   * @param editor FormEditor
   * @param formID String
   * @param title String
   */
  public CdrReportListPage(final FormEditor editor, final String formID, final String title) {
    super(editor, formID, title);
    this.editor = (CdrReportEditor) editor;
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


    if (getCdrReportDataHandler().getCdrReport().getConsiderReviewsOfPrevPidcVers()) {
      this.nonScrollableForm
          .setText(this.editor.getEditorInput().getName() + " \nConsidered Reviews of previous PIDC Versions");
    }
    else {
      this.nonScrollableForm
          .setText(this.editor.getEditorInput().getName() + " \nNot Considered Reviews of previous PIDC Versions");

    }


    final GridLayout gridLayout = new GridLayout();
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;

    // create the main composite
    this.mainComposite = new SashForm(this.nonScrollableForm.getBody(), SWT.HORIZONTAL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(gridData);

    final ManagedForm mform = new ManagedForm(parent);
    // create form content
    createFormContent(mform);

  }

  /**
   * This method initializes CompositeTwo
   */
  public void createComposite() {
    // create scroll composite in the right side
    this.scrollComp = new ScrolledComposite(this.mainComposite, SWT.H_SCROLL | SWT.V_SCROLL);
    this.scrollComp.setLayout(new GridLayout());
    final Composite compositeTwo = new Composite(this.scrollComp, SWT.NONE);
    // create parameter section
    createRightSection(compositeTwo);

    compositeTwo.setLayout(new GridLayout());
    compositeTwo.setLayoutData(GridDataUtil.getInstance().getGridData());


    this.scrollComp.setContent(compositeTwo);
    this.scrollComp.setExpandHorizontal(true);
    this.scrollComp.setExpandVertical(true);
    this.scrollComp.setDragDetect(true);
    // create the control listener for scrolling
    this.scrollComp.addControlListener(new ControlAdapter() {

      @Override
      public void controlResized(final ControlEvent event) {
        Rectangle rect = CdrReportListPage.this.scrollComp.getClientArea();
        CdrReportListPage.this.scrollComp.setMinSize(compositeTwo.computeSize(rect.width, SWT.DEFAULT));
      }
    });
  }

  /**
   * Create parameters section
   *
   * @param compositeTwo
   */
  private void createRightSection(final Composite compositeTwo) {
    this.section = this.formToolkit.createSection(compositeTwo, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("Parameters");
    this.section.setLayout(new GridLayout());
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(false);
    // create form
    createForm(this.section);
    this.section.setClient(this.form);

  }

  // ICDM-2587 (Parent Task ICDM-2412)
  private int getFilteredRowCount() {
    return this.dataRprtFilterGridLayer.getRowHeaderLayer().getPreferredRowCount();
  }

  /**
   * Crate form body
   *
   * @param sectionRight
   */
  private void createForm(final Section sectionRight) {
    this.form = this.formToolkit.createForm(sectionRight);
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());

    this.form.getBody().setLayout(new GridLayout());
    // create filter text
    this.filterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);

    addModifyListenerForFilterTxt();
    CdrReportDataHandler cdrReportData = getEditorInput().getReportData();
    // initialising the filter
    this.toolBarFilters = new CdrReportToolBarFilters(cdrReportData);
    createDataRvwReportTabViewer();
    setDragSupport();
  }

  @Override
  public CdrReportEditorInput getEditorInput() {
    return (CdrReportEditorInput) super.getEditorInput();
  }

  /**
   * @param event
   */
  private void dragStartImpl(final DragSourceEvent event) {
    event.doit = false;
    ISelection currentSelection = null;
    if (CdrReportListPage.this.selectionProvider != null) {
      currentSelection = CdrReportListPage.this.selectionProvider.getSelection();
    }
    if ((currentSelection != null) && !currentSelection.isEmpty()) {
      event.doit = true;
    }
    // If drag is started from a NatTable
    // The below checks ensure the drag source area is confined to the nat body layer
    SelectionLayer selectionLayer = CdrReportListPage.this.dataRprtFilterGridLayer.getBodyLayer().getSelectionLayer();
    if (((selectionLayer != null) && (selectionLayer.getSelectionModel().getSelectedRowCount() == 0)) ||
        validateNATtable(event)) {
      event.doit = false;
    }
  }

  /**
   * @param event
   * @return
   */
  private boolean validateNATtable(final DragSourceEvent event) {
    return (CdrReportListPage.this.natTable != null) &&
        (CdrReportListPage.this.natTable.getRegionLabelsByXY(event.x, event.y) != null) &&
        !CdrReportListPage.this.natTable.getRegionLabelsByXY(event.x, event.y).hasLabel(GridRegion.BODY);
  }

  /**
   * drage listener added
   */
  private void setDragSupport() {

    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.natTable.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new DragSourceListener() {

      @Override
      public void dragStart(final DragSourceEvent event) {
        // TODO: Drag can be canceled based on selection.This can be implemented once all draggable items are identified
        dragStartImpl(event);
      }


      @Override
      public void dragSetData(final DragSourceEvent event) {
        CdrReportEditorInput editorInput = CdrReportListPage.this.editor.getEditorInput();
        if (editorInput.getPidcA2l() != null) {

          IStructuredSelection sel = (IStructuredSelection) CdrReportListPage.this.selectionProvider.getSelection();
          Iterator<?> selParam = sel.iterator();

          while (selParam.hasNext()) {
            Object selectionElement = selParam.next();
            if (selectionElement instanceof A2LParameter) {
              A2LParameter a2lParam = (A2LParameter) selectionElement;
              SelectionLayer selectionLayer =
                  CdrReportListPage.this.dataRprtFilterGridLayer.getBodyLayer().getSelectionLayer();
              PositionCoordinate[] selectedCellPositions = selectionLayer.getSelectedCellPositions();
              if (null != selectedCellPositions) {
                Arrays.asList(selectedCellPositions).stream()
                    .forEach(postCord -> setDraggedData(event, editorInput, a2lParam, postCord));
              }
            }
          }

        }
      }


      @Override
      public void dragFinished(final DragSourceEvent event) {
        // TO-DO
      }
    });

  }

  /**
   * @param event
   * @param editorInput
   * @param a2lParam
   * @param calData
   * @param positionCoordinate
   */
  private void setDraggedData(final DragSourceEvent event, final CdrReportEditorInput editorInput,
      final A2LParameter a2lParam, final PositionCoordinate positionCoordinate) {
    SeriesStatisticsInfo calDataProvider;
    CalData calData = editorInput.getReportData().getParamCheckedVal(a2lParam.getName(),
        positionCoordinate.columnPosition - STATIC_COL_INDEX);
    if (null == calData) {
      if (editorInput.getReportData().isFetchCheckVal()) {
        CDMLogger.getInstance().warnDialog("Check value not available", Activator.PLUGIN_ID);
      }
      else {
        CDMLogger.getInstance().warnDialog("Check values are not loaded", Activator.PLUGIN_ID);
      }
      event.data = null;
      LocalSelectionTransfer.getTransfer().setSelection(null);
    }
    else {
      try {
        CalData calDataObject =
            CalDataUtil.getCalDataHistoryDetails(new CurrentUserBO().getUserName(), calData, "Check Value", null, null);
        event.data = calDataObject;
        calDataProvider = new SeriesStatisticsInfo(calDataObject, CalDataType.CHECK_VALUE);
        final StructuredSelection struSelection = new StructuredSelection(calDataProvider);
        LocalSelectionTransfer.getTransfer().setSelection(struSelection);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * create toolbar and filters
   */
  private void createToolbarFilters() {

    this.toolBarManager = (ToolBarManager) getToolBarManager();
    addHelpAction(this.toolBarManager);
    final Separator separator = new Separator();

    CdrReportToolBarActionSet toolBarActionSet = new CdrReportToolBarActionSet(this.dataRprtFilterGridLayer, this);

    // ICDM-2439
    toolBarActionSet.showComplianceParamsAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    toolBarActionSet.showNonComplianceParamsAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);

    toolBarActionSet.readOnlyAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    toolBarActionSet.notReadOnlyAction(this.toolBarManager, this.toolBarFilters, this.natTable);

    this.toolBarManager.add(separator);
    toolBarActionSet.dependantCharAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    toolBarActionSet.noDependantCharAction(this.toolBarManager, this.toolBarFilters, this.natTable);

    this.toolBarManager.add(separator);
    toolBarActionSet.showQSSDParamsAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    toolBarActionSet.showNonQSSDParamsAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);

    // ICDM-2045
    toolBarActionSet.showMatchLatestFuncVer(this.toolBarManager, this.toolBarFilters);

    toolBarActionSet.showNotMatchLatestFuncVer(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    // ICDM-2585 (Parent Task ICDM-2412)
    toolBarActionSet.showLockedRvws(this.toolBarManager, this.toolBarFilters, this.natTable);
    toolBarActionSet.showUnLockedRvws(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);

    // ICDM-2585 (Parent Task ICDM-2412)
    toolBarActionSet.showStartTypeRvw(this.toolBarManager, this.toolBarFilters, this.natTable);
    toolBarActionSet.showOfficialTypeRvw(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);
    toolBarActionSet.showFullFilledRulesAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showNotFullFilledRulesAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showUnDefinedRulesAction(this.toolBarManager, this.toolBarFilters);

    /** WP_finished filter icons */
    this.toolBarManager.add(separator);
    toolBarActionSet.showWPFinishedAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    toolBarActionSet.showWPNotFinishedAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);

    // ICDM-1818
    CDFXFileExportAction cdfxExportAction = new CDFXFileExportAction(getEditorInput().getReportData());
    CalibrationStatusListExportAction calibrationStatusListExportAction =
        new CalibrationStatusListExportAction(this.editor.getEditorInput(), null, true);

    this.form.getToolBarManager().add(calibrationStatusListExportAction);
    this.form.getToolBarManager().add(cdfxExportAction);

    this.form.getToolBarManager().update(true);
    this.form.setToolBarVerticalAlignment(SWT.TOP);

    this.nonScrollableForm.setToolBarVerticalAlignment(SWT.TOP);
    this.toolBarManager.update(true);
    // ICDM-2141
    addResetAllFiltersAction();
  }


  /**
   * ICDM-2141 Add reset filter button
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.dataRprtFilterGridLayer);
    addResetFiltersAction();
  }

  /**
   * Method returns CDR Report Data Handler
   *
   * @return CdrReportDataHandler
   */
  public CdrReportDataHandler getCdrReportDataHandler() {
    return getEditorInput().getReportData();
  }

  /**
   * create the NAT table viewer
   */
  private void createDataRvwReportTabViewer() {
    // set column names and width for static columns
    ConcurrentMap<Integer, Integer> columnWidthMap = setHeaderNameColWidthForStaticCols();

    // get the cdr report data
    CdrReportDataHandler cdrReportData = getCdrReportDataHandler();

    // get the input for the nat table
    A2LFileInfoBO a2lFileInfoBO = cdrReportData.getA2lEditorDataProvider().getA2lFileInfoBO();
    CdrReport cdrReport = cdrReportData.getCdrReport();

    TreeSet<A2LParameter> dataRvwRprtNatInputs = new TreeSet<>(
        cdrReport.isToGenDataRvwRprtForWPResp() ? a2lFileInfoBO.getA2lParamMapForWPResp(cdrReport).values()
            : a2lFileInfoBO.getA2lParamMap(cdrReport.getParamPropsMap()).values());

    // set column names and width for dynamic columns
    setHeaderNameColWidthForDynamicCols(columnWidthMap, cdrReportData, dataRvwRprtNatInputs);

    // create nat table
    IConfigRegistry configRegistry = createNatTable(columnWidthMap, cdrReportData, dataRvwRprtNatInputs);

    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(GridDataUtil.getInstance().getGridData());

    // initailise all column filter
    this.allColumnFilterMatcher = new CdrReportAllColFilterMatcher(cdrReportData);
    this.dataRprtFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    // ICDM-1702 toolbar filter matcher
    this.dataRprtFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());

    this.natTable.addConfiguration(new CustomNatTableStyleConfiguration());

    this.natTable.addMouseListener(new MouseEventListener());

    this.natTable.addConfiguration(new CdrReportFilterRowConfiguration(this, STATIC_COL_INDEX + this.dynamicColsCount) {

      @Override
      public void configureRegistry(final IConfigRegistry reg) {
        super.configureRegistry(reg);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        reg.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });

    // ICDM-2605
    // register a combo box cell editor for the Responsiblity column in the filter row
    // get the Responsibility list to be set in combo
    List<String> respComboList = new ArrayList<>();
    for (WpRespType wpResp : WpRespType.values()) {
      respComboList.add(wpResp.getDispName());
    }
    respComboList.add(ApicConstants.NOT_DEFINED_PARAM);
    // add combo box cell editor
    ICellEditor comboBoxCellEditor = new ComboBoxCellEditor(respComboList, respComboList.size());
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
        FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.RESP_TYPE_COL_INDEX);

    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable.addConfiguration(
        getCustomComparatorConfiguration(this.dataRprtFilterGridLayer.getColumnHeaderDataLayer(), cdrReportData));

    // add the edit configuration which will give images for type column
    this.natTable.addConfiguration(new RvwReportEditConfiguration(this.dataRprtFilterGridLayer));
    // create header menu right click option
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {


      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),

            new PopupMenuAction(
                super.createColumnHeaderMenu(CdrReportListPage.this.natTable).withStateManagerMenuItemProvider()
                    .withMenuItemProvider((final NatTable natTbl, final Menu popupMenu) -> {
                      MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
                      menuItem.setText(CommonUIConstants.NATTABLE_FREEZE_COLUMN);
                      menuItem.setEnabled(true);
                      menuItem.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(final SelectionEvent event) {

                          CdrReportListPage.this.reconstructNatTable();
                        }
                      });
                    }).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });
    this.natTable.addConfiguration(new FilterRowCustomConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry1) {
        super.configureRegistry(configRegistry1);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configRegistry1.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });

    // add the label accumulator
    DataLayer bodyDataLayer = this.dataRprtFilterGridLayer.getDummyDataLayer();
    IRowDataProvider<A2LParameter> bodyDataProvider = (IRowDataProvider<A2LParameter>) bodyDataLayer.getDataProvider();
    final CdrReportLabelAccumulator rvwReportLabelAccumulator =
        new CdrReportLabelAccumulator(bodyDataLayer, bodyDataProvider, cdrReportData);
    bodyDataLayer.setConfigLabelAccumulator(rvwReportLabelAccumulator);
    // ICDM-1701
    addRightClickMenu();


    this.natTable.configure();
    // get the reference to the SelectionLayer
    SelectionLayer selectionLayer = this.dataRprtFilterGridLayer.getBodyLayer().getSelectionLayer();
    // select cell with column position 3 and row position 0
    selectionLayer.setSelectedCell(this.colIndexToFreeze + 1, 0);

    // freeze the first two columns
    this.natTable.doCommand(new FreezeSelectionCommand());

    this.dataRprtFilterGridLayer.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.natTable));
    this.selectionProvider = new RowSelectionProvider<>(this.dataRprtFilterGridLayer.getBodyLayer().getSelectionLayer(),
        this.dataRprtFilterGridLayer.getBodyDataProvider(), false);


    // attcah tool tip for nat table
    attachToolTip();
    // create tool bar filters
    createToolbarFilters();

    // set the status bar message
    setStatusBarMessage(false);
    // set the selection provider
    getSite().setSelectionProvider(this.selectionProvider);
  }

  /**
  *
  */
  public void reconstructNatTable() {

    this.natTable.dispose();
    this.propertyToLabelMap.clear();

    this.dataRprtFilterGridLayer = null;
    if (this.toolBarManager != null) {
      this.toolBarManager.removeAll();
    }

    if (this.toolBarManager != null) {
      this.toolBarManager.removeAll();
    }

    if (this.form.getToolBarManager() != null) {
      this.form.getToolBarManager().removeAll();
    }
    createDataRvwReportTabViewer();
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
  }

  /**
   * @author dmo5cob
   */
  private final class MouseEventListener implements MouseListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDoubleClick(final MouseEvent mouseEvent) {
      if (CommonUtils.isEqual(mouseEvent.button, 1)) {
        leftMouseDoubleClickAction(mouseEvent);
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDown(final MouseEvent mouseevent) {
      CdrReportListPage.this.selectedColPostn = LayerUtil.convertColumnPosition(CdrReportListPage.this.natTable,
          CdrReportListPage.this.natTable.getColumnPositionByX(mouseevent.x),
          CdrReportListPage.this.dataRprtFilterGridLayer.getColumnHeaderDataLayer());
      CdrReportListPage.this.colIndexToFreeze = CdrReportListPage.this.selectedColPostn;
      CdrReportListPage.this.selectedRowPostn = LayerUtil.convertRowPosition(CdrReportListPage.this.natTable,
          CdrReportListPage.this.natTable.getRowPositionByY(mouseevent.y),
          CdrReportListPage.this.dataRprtFilterGridLayer.getDummyDataLayer());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseUp(final MouseEvent arg0) {
      // NA
    }
  }

  private void leftMouseDoubleClickAction(final MouseEvent mouseEvent) {
    // Get the cell at the mouse click position in the NatTable
    ILayerCell cell = this.natTable.getCellByPosition(this.natTable.getColumnPositionByX(mouseEvent.x),
        this.natTable.getRowPositionByY(mouseEvent.y));

    // Check if the cell exists and if it is in the review result description column index
    if ((CommonUtils.isNotNull(cell)) &&
        (CommonUtils.isEqual(cell.getColumnIndex(), CommonUIConstants.RVW_DESCRIPTION_COL_INDEX))) {

      int row = LayerUtil.convertRowPosition(this.natTable, this.natTable.getRowPositionByY(mouseEvent.y),
          ((CustomFilterGridLayer<DaCompareHexParam>) this.natTable.getLayer()).getDummyDataLayer());

      A2LParameter a2lParam =
          (A2LParameter) CdrReportListPage.this.dataRprtFilterGridLayer.getBodyDataProvider().getRowObject(row);
      String paramName = a2lParam.getName();

      CDRReviewResult reviewResult = getCdrReportDataHandler().getReviewResult(paramName, 0);

      // If a review result is found, open the review result editor
      if (CommonUtils.isNotNull(reviewResult)) {

        new CommonActionSet().openReviewResultEditor(reviewResult, paramName,
            getCdrReportDataHandler().getPidcVariant() != null ? getCdrReportDataHandler().getPidcVariant().getId()
                : null);

      }
    }
  }

  /**
   * @author mkl2cob
   */
  private class FilterRowCustomConfiguration extends AbstractRegistryConfiguration {

    /**
     * @author jvi6cob
     */
    private final class FilterDisplayConverter extends DisplayConverter {

      /** The col name. */
      private final String colName;

      /**
       * Instantiates a new filter display converter.
       *
       * @param colName the col name
       */
      FilterDisplayConverter(final String colName) {
        this.colName = colName;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Object displayToCanonicalValue(final Object displayValue) {
        return null;
      }

      @Override
      public Object canonicalToDisplayValue(final Object canonicalValue) {
        if (this.colName.equalsIgnoreCase(COL_NM_COMPLI_TYPE) && (canonicalValue instanceof String) &&
            ((String) canonicalValue).isEmpty()) {
          return COMPLI_TYPE.NON_COMPLIANCE.getText();
        }

        if (this.colName.equalsIgnoreCase(COL_NM_TYPE) && (canonicalValue instanceof String) &&
            ((String) canonicalValue).trim().isEmpty()) {
          return "NA";
        }
        return canonicalValue;
      }
    }


    @Override
    public void configureRegistry(final IConfigRegistry configRegistry) {
      // override the default filter row configuration for painter
      configRegistry.registerConfigAttribute(CELL_PAINTER,
          new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);

      // enable filter comparator for cols
      for (int index = 0; index <= 15; index++) {
        // register config attr for each col
        configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
            TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + index);
      }

      configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultDisplayConverter() {},
          NORMAL);

      // combo for Compliance /Non Compliance column
      List<String> comboCompliNonCompliList = Arrays
          .asList(CommonUtils.concatenate(COMPLI_TYPE.COMPLIANCE.getText(), " "), COMPLI_TYPE.NON_COMPLIANCE.getText());

      ICellEditor comboBoxCompliNonCompliCellEditor = new ComboBoxCellEditor(comboCompliNonCompliList);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCompliNonCompliCellEditor,
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + COL_INDEX_COMPLI);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
          new FilterDisplayConverter(COL_NM_COMPLI_TYPE), NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + COL_INDEX_COMPLI);

      // combo for Type column
      List<String> comboTypeList =
          Arrays.asList(ParameterType.VALUE.getText(), ParameterType.VAL_BLK.getText(), ParameterType.MAP.getText(),
              ParameterType.CURVE.getText(), ParameterType.ASCII.getText(), ParameterType.AXIS_PTS.getText());

      ICellEditor comboBoxLabelTypeCellEditor = new ComboBoxCellEditor(comboTypeList);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxLabelTypeCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + COL_INDEX_TYPE);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
          new FilterDisplayConverter(COL_NM_TYPE), NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + COL_INDEX_TYPE);


    }
  }

  /**
   * This method adds right click menu for tableviewer
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {
      SelectionLayer selectionLayer = CdrReportListPage.this.dataRprtFilterGridLayer.getBodyLayer().getSelectionLayer();
      PositionCoordinate[] selectedCellPositions = selectionLayer.getSelectedCellPositions();
      if (null != selectedCellPositions) {
        CommonActionSet actionSet = new CommonActionSet();
        PIDCActionSet pidcActionSet = new PIDCActionSet();
        // add the right click menus
        for (PositionCoordinate positionCoordinate : selectedCellPositions) {
          addContextMenuOpts(mgr, selectedCellPositions, actionSet, pidcActionSet, positionCoordinate);
        }
      }
    });
    final Menu menu = menuMgr.createContextMenu(this.natTable.getShell());
    this.natTable.setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.selectionProvider);

  }

  /**
   * Create nattable
   *
   * @param columnWidthMap
   * @param cdrReportData
   * @param dataRvwRprtNatInputs
   * @return
   */
  private IConfigRegistry createNatTable(final ConcurrentMap<Integer, Integer> columnWidthMap,
      final CdrReportDataHandler cdrReportData, final TreeSet<A2LParameter> dataRvwRprtNatInputs) {
    // instantiate the input to column converter
    CdrReportInputToColumnConverter natInputToColumnConverter = new CdrReportInputToColumnConverter(cdrReportData);
    IConfigRegistry configRegistry = new ConfigRegistry();

    // A Custom Filter Grid Layer is constructed
    this.dataRprtFilterGridLayer = new CustomFilterGridLayer<A2LParameter>(configRegistry, dataRvwRprtNatInputs,
        columnWidthMap, new CdrReportColumnPropertyAccessor<A2LParameter>(this),
        new CdrReportColumnHeaderDataProvider(this), new CdrReportColumnComparator(cdrReportData, 1),
        natInputToColumnConverter, this, new DataRvwReportNatMouseClickAction(), true, true, false);

    // add a2l outline filter
    this.outlineNatFilter = new A2LOutlineNatFilter(this.dataRprtFilterGridLayer, getEditorInput().getA2lFile(),
        cdrReportData.getA2lEditorDataProvider().getA2lWpInfoBO(), cdrReportData);
    this.outlineNatFilter.setWpType(this.editor.getEditorInput().getReportData().getA2lEditorDataProvider()
        .getA2lFileInfoBO().getMappingSourceID());
    this.dataRprtFilterGridLayer.getFilterStrategy()
        .setOutlineNatFilterMatcher(this.outlineNatFilter.getOutlineMatcher());
    // creating the NAT table
    this.natTable = new CustomNATTable(
        this.form.getBody(), SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED |
            SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        this.dataRprtFilterGridLayer, false, getClass().getSimpleName());
    return configRegistry;
  }

  /**
   * @param columnWidthMap ConcurrentMap<Integer, Integer>
   * @param cdrReportData CdrReportData
   * @param dataRvwRprtNatInputs TreeSet<A2LParameter>
   */
  private void setHeaderNameColWidthForDynamicCols(final ConcurrentMap<Integer, Integer> columnWidthMap,
      final CdrReportDataHandler cdrReportData, final TreeSet<A2LParameter> dataRvwRprtNatInputs) {
    // initialising the row count
    this.totTableRowCount = dataRvwRprtNatInputs.size();
    this.dynamicColsCount = cdrReportData.getCdrReport().getMaxParamReviewCount();
    for (int i = STATIC_COL_INDEX; i < (this.dynamicColsCount + STATIC_COL_INDEX); i++) {
      if (i == STATIC_COL_INDEX) {
        // keep the header name as latest review for the first dynamic column
        this.propertyToLabelMap.put(i, "Latest Review ");
        columnWidthMap.put(i, PARAMETER_COLUMN_WIDTH);
      }
      else {
        this.propertyToLabelMap.put(i, "Review " + ((i + 1) - STATIC_COL_INDEX));
        columnWidthMap.put(i, PARAMETER_COLUMN_WIDTH - 100);
      }
    }
  }

  /**
   * Create header names
   *
   * @return ConcurrentMap<Integer, Integer>
   */
  private ConcurrentMap<Integer, Integer> setHeaderNameColWidthForStaticCols() {
    // storing the header names
    this.propertyToLabelMap = new HashMap<>();
    this.propertyToLabelMap.put(CommonUIConstants.SSD_CLASS_COL_INDEX, "");
    this.propertyToLabelMap.put(CommonUIConstants.PARAM_TYPE_COL_INDEX, "Type");
    this.propertyToLabelMap.put(CommonUIConstants.PARAM_NAME_COL_INDEX, "Parameter");
    this.propertyToLabelMap.put(CommonUIConstants.FUNC_COL_INDEX, "Function");
    this.propertyToLabelMap.put(CommonUIConstants.FUNC_VERS_COL_INDEX, "Func\nVersion");
    // ICDM-2605
    this.propertyToLabelMap.put(CommonUIConstants.WP_COL_INDEX, "WP");
    this.propertyToLabelMap.put(CommonUIConstants.RESP_TYPE_COL_INDEX, "Responsible Type");
    // ICDM-2605
    this.propertyToLabelMap.put(CommonUIConstants.RESPONSIBILITY_COL_INDEX, "Responsibility");
    this.propertyToLabelMap.put(CommonUIConstants.WP_FINISHED_COL_INDEX, "WP Finished");
    this.propertyToLabelMap.put(CommonUIConstants.CW_COL_INDEX, "CW");
    this.propertyToLabelMap.put(CommonUIConstants.LATEST_A2L_COL_INDEX, "Latest\nA2l Version");
    this.propertyToLabelMap.put(CommonUIConstants.LATEST_FUNC_COL_INDEX, "Latest\nFunc Version");
    this.propertyToLabelMap.put(CommonUIConstants.RVWD_COL_INDEX, "Reviewed");
    this.propertyToLabelMap.put(CommonUIConstants.RVW_COMMENT_COL_INDEX, "Latest Review Comment");
    this.propertyToLabelMap.put(CommonUIConstants.RVW_QNAIRE_STATUS_COL_INDEX, "Questionniare\nStatus");
    this.propertyToLabelMap.put(CommonUIConstants.RVW_DESCRIPTION_COL_INDEX, "Review Description");

    // The below map is used by NatTable to Map Columns with their respective widths
    // Width is based on pixels
    ConcurrentMap<Integer, Integer> columnWidthMap = new ConcurrentHashMap<>();
    columnWidthMap.put(CommonUIConstants.SSD_CLASS_COL_INDEX, PTYPE_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.PARAM_TYPE_COL_INDEX, PTYPE_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.PARAM_NAME_COL_INDEX, PARAMETER_COLUMN_WIDTH);
    columnWidthMap.put(CommonUIConstants.FUNC_COL_INDEX, FUNC_COLUMN_WIDTH);
    columnWidthMap.put(CommonUIConstants.FUNC_VERS_COL_INDEX, FUNC_VERS_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.WP_COL_INDEX, RESP_COL_WIDTH);
    // ICDM-2605
    columnWidthMap.put(CommonUIConstants.RESPONSIBILITY_COL_INDEX, RESP_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.WP_FINISHED_COL_INDEX, LATEST_A2L_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.CW_COL_INDEX, CW_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.LATEST_A2L_COL_INDEX, LATEST_A2L_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.LATEST_FUNC_COL_INDEX, REVIWED_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.RVWD_COL_INDEX, REVIWED_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.RVW_COMMENT_COL_INDEX, LATEST_A2L_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.RVW_QNAIRE_STATUS_COL_INDEX, LATEST_QNAIRE_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.RVW_DESCRIPTION_COL_INDEX, PARAMETER_COLUMN_WIDTH);


    return columnWidthMap;
  }

  /**
   * @param columnHeaderDataLayer DefaultColumnHeaderDataLayer
   * @param cdrReportData CdrReportData
   * @return IConfiguration
   */
  private IConfiguration getCustomComparatorConfiguration(final DefaultColumnHeaderDataLayer columnHeaderDataLayer,
      final CdrReportDataHandler cdrReportData) {


    return new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Add label accumulator
        ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
        columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);
        for (int colIndex = 0; colIndex < (CdrReportListPage.this.dynamicColsCount + STATIC_COL_INDEX); colIndex++) {
          labelAccumulator.registerColumnOverrides(colIndex, CUSTOM_COMPARATOR_LABEL + colIndex);
          configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
              new CdrReportColumnComparator(cdrReportData, colIndex), NORMAL, CUSTOM_COMPARATOR_LABEL + colIndex);
        }


      }
    };

  }

  /**
   * Enables tootltip only for cells which contain not fully visible content
   */
  private void attachToolTip() {
    DefaultToolTip toolTip = new CdrReportNatToolTip(this.natTable, new String[0], this);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));
  }

  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(evt -> {

      String text = CdrReportListPage.this.filterTxt.getText().trim();
      CdrReportListPage.this.allColumnFilterMatcher.setFilterText(text, true);
      CdrReportListPage.this.dataRprtFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
      CdrReportListPage.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(CdrReportListPage.this.dataRprtFilterGridLayer.getSortableColumnHeaderLayer()));
      setStatusBarMessage(false);

    });
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   * creating the content of the form
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    // initiate the form tool kit
    this.formToolkit = managedForm.getToolkit();

    createComposite();
    // add listeners
    getSite().getPage().addSelectionListener(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.nonScrollableForm.getToolBarManager();
  }

  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
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

    // ICDM-214
    this.outlineNatFilter.a2lOutlineSelectionListener(selection);
    // ICDM-859
    if (this.editor.getActivePage() == 0) {
      setStatusBarMessage(true);
    }
  }


  /**
   * @return the dataRprtFilterGridLayer
   */
  public CustomFilterGridLayer<A2LParameter> getDataRprtFilterGridLayer() {
    return this.dataRprtFilterGridLayer;
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
   * input for status line
   *
   * @param outlineSelection flag set according to selection made in viewPart or editor.
   */
  public void setStatusBarMessage(final boolean outlineSelection) {

    int totalItemCount = this.totTableRowCount;
    int filteredItemCount = getFilteredRowCount();
    final StringBuilder buf = new StringBuilder(40);
    buf.append("Displaying : ").append(filteredItemCount).append(" out of ").append(totalItemCount).append(" records ");
    IStatusLineManager statusLine;
    // Updation of status based on selection in view part
    if (outlineSelection) {
      // in case of outline selection
      final IViewSite viewPartSite = (IViewSite) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView(ApicUiConstants.OUTLINE_TREE_VIEW).getSite();
      // get the status line manager from the outline
      statusLine = viewPartSite.getActionBars().getStatusLineManager();
    }
    else {
      // get the status line manager from the editor
      statusLine = this.editor.getEditorSite().getActionBars().getStatusLineManager();
    }
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
   * @param manager
   * @param selectedCellPositions
   * @param actionSet
   * @param pidcActionSet
   * @param positionCoordinate
   */
  private void addContextMenuOpts(final IMenuManager manager, final PositionCoordinate[] selectedCellPositions,
      final CommonActionSet actionSet, final PIDCActionSet pidcActionSet, final PositionCoordinate positionCoordinate) {

    A2LParameter a2lParam = (A2LParameter) CdrReportListPage.this.dataRprtFilterGridLayer.getBodyDataProvider()
        .getRowObject(positionCoordinate.rowPosition);
    String paramName = a2lParam.getName();

    CdrReportDataHandler cdrReportData = getEditorInput().getReportData();
    Separator seperator = new Separator();
    int colIdx = (positionCoordinate.columnPosition - STATIC_COL_INDEX);
    if ((selectedCellPositions.length == 1) && (positionCoordinate.columnPosition > (STATIC_COL_INDEX - 1)) &&
        cdrReportData.hasReviewResult(paramName, colIdx)) {

      // Add Open review result menu item
      CDRReviewResult reviewResult = cdrReportData.getReviewResult(paramName, colIdx);
      // Defect Fix 260937
      actionSet.openReviewResultAction(manager, reviewResult, paramName,
          cdrReportData.getPidcVariant() != null ? cdrReportData.getPidcVariant().getId() : null);
      manager.add(seperator);

      // Add Open PIDC Version menu item
      PidcVersion pidcVers = cdrReportData.getPidcVersion(paramName, colIdx);
      reviewResult.getPidcVersionId();
      pidcActionSet.openPidcFromCDRReport(manager, pidcVers);
      manager.add(seperator);

      // Add Open A2L File menu item
      PidcA2l pidcA2l = cdrReportData.getPidcA2l(paramName, colIdx);
      actionSet.openA2LEditor(manager, pidcA2l);

      // Add show Table Graph menu item
      // ICDM-1723
      actionSet.showTableGraph(manager, cdrReportData.getParamCheckedVal(paramName, colIdx), paramName,
          cdrReportData.isFetchCheckVal(),
          getEditorInput().getReportData().getA2lEditorDataProvider().getA2lFileInfoBO().getCharacteristicsMap());

    }
    else if (positionCoordinate.columnPosition == CommonUIConstants.LATEST_A2L_COL_INDEX) {
      PidcA2l pidcA2l = cdrReportData.getPidcA2l(paramName, LATEST_REVIEW_COL);
      if (null != pidcA2l) {
        // check for latest a2l version column to open the a2l file
        actionSet.openA2LEditor(manager, pidcA2l);
      }
    }
    else if (CommonUtils.isEqual(positionCoordinate.columnPosition, CommonUIConstants.RVW_DESCRIPTION_COL_INDEX)) {

      final PidcVarRvwDetails cdrResult = getResultObjectForExtLink(paramName, cdrReportData);
      final Object linkObj =
          CommonUtils.isNull(cdrResult.getReviewVariant()) ? cdrResult.getReviewResult() : cdrResult.getReviewVariant();
      final Action copyLink = new Action() {

        @Override
        public void run() {
          // ICDM-1649
          try {
            LinkCreator linkCreator = new LinkCreator(linkObj);
            linkCreator.copyToClipBoard();
          }
          catch (ExternalLinkException exp) {
            CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }
        }
      };
      copyLink.setText("Copy Review Result Link");
      copyLink.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CDR_RES_LINK_16X16));

      manager.add(copyLink);
    }
    else {
      String qnaireRespVersStatus = cdrReportData.getQnaireRespVersStatus(paramName, true);
      if ((positionCoordinate.columnPosition == CommonUIConstants.RVW_QNAIRE_STATUS_COL_INDEX) &&
          CommonUtils.isNotEmptyString(qnaireRespVersStatus) &&
          CommonUtils.isNotEqual(qnaireRespVersStatus, CDRConstants.RVW_QNAIRE_STATUS_N_A) &&
          CommonUtils.isNotEqual(qnaireRespVersStatus, CDRConstants.NO_QNAIRE_STATUS)) {
        // Add Show related Questionnaire menu item
        new ShowRelatedQnaireAction(this.mainComposite.getShell(), manager, a2lParam.getParamId(), cdrReportData, null,
            true);
      }
    }
  }

  private PidcVarRvwDetails getResultObjectForExtLink(final String paramName,
      final CdrReportDataHandler cdrReportData) {

    PidcVarRvwDetails pidcVarRvwDetails = new PidcVarRvwDetails();
    pidcVarRvwDetails.setReviewResult(cdrReportData.getReviewResult(paramName, 0));

    return pidcVarRvwDetails;
  }


  /**
   * @return the propertyToLabelMap
   */
  public Map<Integer, String> getPropertyToLabelMap() {
    return this.propertyToLabelMap;
  }
}
