/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
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
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.reorder.command.MultiColumnReorderCommand;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.FocusMatrixRemarksAction;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCSearchAction;
import com.bosch.caltool.apic.ui.dialogs.FocusMatrixEditDialog;
import com.bosch.caltool.apic.ui.editors.PIDCEditorInput;
import com.bosch.caltool.apic.ui.table.filters.AllFMColumnFilterMatcher;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.PIDCPageEditUtil;
import com.bosch.caltool.apic.ui.views.FocusMatrixEditView;
import com.bosch.caltool.apic.ui.views.providers.FocusMatrixEditConfiguration;
import com.bosch.caltool.apic.ui.views.providers.FocusMatrixInputToColumnConverter;
import com.bosch.caltool.apic.ui.views.providers.FocusMatrixNatToolTip;
import com.bosch.caltool.apic.ui.views.providers.FocusmatrixLabelAccumulator;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionAttributeBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixAttributeClientBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDetails;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUsecaseItemFilterTxt;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixVersionClientBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.table.filters.OutlineUCNatFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.ws.rest.client.apic.FocusMatrixServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.wbutils.WorkbenchUtils;

/**
 * @author mkl2cob
 */
public class FocusMatrixPageNATTableSection {

  private static final int SIZE_ONE = 1;

  /**
   * Static cols in the page ( 4 columns)
   */
  private static final int STATIC_COL_INDEX = 4;

  /**
   * comparator label
   */
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";
  /**
   * Constant defining the type filter text
   */
  private static final String TYPE_FILTER_TEXT = "type filter text";

  /**
   * Width of other columns
   */
  private static final int OTHER_COLUMN_WIDTH = 150;
  /**
   * width of descritpion column
   */
  private static final int DESCRIPTION_COLUMN_WIDTH = 150;
  /**
   * width of attributes column
   */
  private static final int ATTRIBUTE_COLUMN_WIDTH = 150;

  /**
   * width of value column
   */
  private static final Integer VALUE_COLUMN_WIDTH = 100;
  /**
   * constant for y coordiante 10
   */
  private static final int YCOORDINATE_TEN = 10;
  /**
   * constant for x coordinate 10
   */
  private static final int XCOORDINATE_TEN = 10;

  /**
   * constant for left mouse click
   */
  protected static final int LEFT_MOUSE_CLICK = 1;
  /**
   * width of remark column
   */
  private static final int REMARK_COLUMN_WIDTH = 50;

  /**
   * Column number for comments
   */
  public static final int REMARK_COL_NUMBER = 3; // used in other class

  /**
   * ScrolledComposite instance
   */
  private ScrolledComposite scrollComp;

  /**
   * right form
   */
  private Form form;
  /**
   * filter text
   */
  private Text filterTxt;
  /**
   * Input set for NAT table
   */
  private SortedSet<FocusMatrixAttributeClientBO> focusMatrixNatInputs;
  /**
   * count for number of rows in table
   */
  private int totTableRowCount;
  /**
   * map to store header strings
   */
  private Map<Integer, String> propertyToLabelMap;
  /**
   * FocusMatrixInputToColumnConverter instance
   */
  private FocusMatrixInputToColumnConverter natInputToColumnConverter;
  /**
   * grid layer of NAT table
   */
  private CustomFilterGridLayer<FocusMatrixAttributeClientBO> ucFilterGridLayer;
  /**
   * NAT table instance
   */
  private CustomNATTable natTable;
  /**
   * RowSelectionProvider
   */
  private RowSelectionProvider<FocusMatrixAttributeClientBO> selectionProvider;
  /**
   * FocusMatrix instance
   */
  private FocusMatrixVersionClientBO model;


  /**
   * AbstractUseCaseItem instance
   */
  private IUseCaseItemClientBO selectedUcItem;
  /**
   * AllFMColumnFilterMatcher
   */
  private AllFMColumnFilterMatcher<FocusMatrixAttributeClientBO> allColumnFilterMatcher;
  /**
   * OutlineUCNatFilter instance
   */
  private OutlineUCNatFilter outLineNatFilter;

  /**
   * FocusMatrixPage instance
   */
  private final FocusMatrixPage focusMatrixPage;

  /**
   * ColorCodeSelectionDialog instance
   */
  private FocusMatrixEditDialog colorCodeSelectionDialog;

  /**
   * Section instance
   */
  private Section sectionRight;


  /**
   * @return the sectionRight
   */
  public Section getSectionRight() {
    return this.sectionRight;
  }

  /**
   * @param focusMatrixPageNew FocusMatrixPage
   */
  public FocusMatrixPageNATTableSection(final FocusMatrixPage focusMatrixPageNew) {
    this.focusMatrixPage = focusMatrixPageNew;
  }

  /**
   * This method initializes CompositeTwo
   */
  public void createRightComposite() {
    // create scroll composite in the right side
    this.scrollComp = new ScrolledComposite(this.focusMatrixPage.getMainComposite(), SWT.H_SCROLL | SWT.V_SCROLL);
    this.scrollComp.setLayout(new GridLayout());
    final Composite compositeTwo = new Composite(this.scrollComp, SWT.NONE);

    createRightSection(compositeTwo);

    compositeTwo.setLayout(new GridLayout());
    compositeTwo.setLayoutData(GridDataUtil.getInstance().getGridData());


    this.scrollComp.setContent(compositeTwo);
    this.scrollComp.setExpandHorizontal(true);
    this.scrollComp.setExpandVertical(true);
    this.scrollComp.setDragDetect(true);
    // create the control listener for scrolling
    this.scrollComp.addControlListener(new ControlAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void controlResized(final ControlEvent event) {
        Rectangle rect = FocusMatrixPageNATTableSection.this.scrollComp.getClientArea();
        FocusMatrixPageNATTableSection.this.scrollComp.setMinSize(compositeTwo.computeSize(rect.width, SWT.DEFAULT));
      }
    });
  }

  /**
   * create the right section
   *
   * @param compositeTwo
   */
  private void createRightSection(final Composite compositeTwo) {
    this.sectionRight = this.focusMatrixPage.getFormToolkit().createSection(compositeTwo,
        Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionRight.setText("Attributes");
    this.sectionRight.setLayout(new GridLayout());
    this.sectionRight.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.sectionRight.getDescriptionControl().setEnabled(false);
    createForm(this.sectionRight);
    this.sectionRight.setClient(this.form);
  }

  /**
   * @param sectionRight
   * @param formToolkit2 FormToolkit
   */
  private void createForm(final Section sectionRight) {
    this.form = this.focusMatrixPage.getFormToolkit().createForm(sectionRight);
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());

    this.form.getBody().setLayout(new GridLayout());
    // create filter text and add modify listener
    this.filterTxt = TextUtil.getInstance().createFilterText(this.focusMatrixPage.getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), TYPE_FILTER_TEXT);

    addModifyListenerForFilterTxt();

    try {
      createFocusMatrixTableViewer();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    // save the total row count for status bar
    this.totTableRowCount = this.ucFilterGridLayer.getRowHeaderLayer().getPreferredRowCount();
  }

  /**
   * creating the NAT table viewer
   *
   * @throws ApicWebServiceException
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void createFocusMatrixTableViewer() throws ApicWebServiceException {

    Map<Integer, Integer> columnWidthMap = createModelHeaderForTable();

    IConfigRegistry configRegistry = createLayerAndTable(columnWidthMap);


    // ICDM-1629
    this.ucFilterGridLayer.getFilterStrategy()
        .setToolBarFilterMatcher(this.focusMatrixPage.getToolBarFilters().getToolBarMatcher());

    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.natTable.addConfiguration(new CustomNatTableStyleConfiguration());
    this.natTable.addConfiguration(new FilterRowCustomConfiguration() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        super.configureRegistry(configRegistry);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });
    // add sort configuration
    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable.addConfiguration(getCustomComparatorConfiguration(this.ucFilterGridLayer.getColumnHeaderDataLayer()));
    // creating header menu configuration
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      @Override
      protected PopupMenuBuilder createColumnHeaderMenu(final NatTable natTable) {
        return super.createColumnHeaderMenu(natTable).withStateManagerMenuItemProvider();
      }
    });
    this.natTable.addConfiguration(new FocusMatrixEditConfiguration());

    // Group columns programmatically
    groupColumns();

    DataLayer bodyDataLayer = this.ucFilterGridLayer.getDummyDataLayer();
    IRowDataProvider<FocusMatrixAttributeClientBO> bodyDataProvider =
        (IRowDataProvider<FocusMatrixAttributeClientBO>) bodyDataLayer.getDataProvider();
    final FocusmatrixLabelAccumulator focusmatrixLabelAccumulator =
        new FocusmatrixLabelAccumulator(bodyDataLayer, bodyDataProvider);
    bodyDataLayer.setConfigLabelAccumulator(focusmatrixLabelAccumulator);


    // adding right click menu
    // TODO based on need
    addRightClickMenu();
    this.natTable.configure();

    // get the reference to the SelectionLayer
    SelectionLayer selectionLayer = this.ucFilterGridLayer.getBodyLayer().getSelectionLayer();
    // select cell with column position 2 and row position 0
    selectionLayer.setSelectedCell(STATIC_COL_INDEX, 0);
    // freeze the first two columns
    this.natTable.doCommand(new FreezeSelectionCommand());
    this.ucFilterGridLayer.registerCommandHandler(
        new FocusMatrixUpdateCommandHandler(this.focusMatrixPage.getDataHandler(), this.ucFilterGridLayer));
    this.selectionProvider = new RowSelectionProvider<FocusMatrixAttributeClientBO>(
        this.ucFilterGridLayer.getBodyLayer().getSelectionLayer(), this.ucFilterGridLayer.getBodyDataProvider(), false);

    // The below method is required to enable tootltip only for cells which contain not fully visible content
    attachToolTip(this.natTable);
    addMouseListener();

    this.focusMatrixPage.getSite().setSelectionProvider(this.selectionProvider);

  }

  /**
   * Mouse listener to the table viewer
   */
  private void addMouseListener() {
    this.natTable.addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(final MouseEvent mouseEvent) {
        // TODO Auto-generated method stub

      }

      @Override
      public void mouseDown(final MouseEvent mouseEvent) {

        PIDCEditorInput editorInput = FocusMatrixPageNATTableSection.this.focusMatrixPage.getEditorInput();
        boolean canModify = false;
        try {
          canModify = editorInput.getFmDataHandler().getSelFmVersion().isModifiable();
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
        }
        if (canModify) {
          ApicDataBO apicBo = new ApicDataBO();
          CurrentUserBO currUser = new CurrentUserBO();
          // ICDM-2487 P1.27.101
          try {
            if (!apicBo.isPidcUnlockedInSession(getSelectedPidcVersion()) &&
                (currUser.hasApicWriteAccess() && currUser.hasNodeWriteAccess(getSelectedPidcVersion().getPidcId()))) {
              final PIDCActionSet pidcActionSet = new PIDCActionSet();
              pidcActionSet.showUnlockPidcDialog(getSelectedPidcVersion());
            }
          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }

          // ICDM-2354
          if (apicBo.isPidcUnlockedInSession(getSelectedPidcVersion())) {
            // close the pop up
            if (FocusMatrixPageNATTableSection.this.colorCodeSelectionDialog != null) {
              if ((FocusMatrixPageNATTableSection.this.colorCodeSelectionDialog.getShell() != null) &&
                  !FocusMatrixPageNATTableSection.this.colorCodeSelectionDialog.getShell().isDisposed() &&
                  !FocusMatrixPageNATTableSection.this.colorCodeSelectionDialog.getErrorDialogDisplayed() &&
                  !FocusMatrixPageNATTableSection.this.colorCodeSelectionDialog.close()) {
                return;
              }
            }

            if (mouseEvent.button == LEFT_MOUSE_CLICK) {

              ILayerCell cell = FocusMatrixPageNATTableSection.this.natTable.getCellByPosition(
                  FocusMatrixPageNATTableSection.this.natTable.getColumnPositionByX(mouseEvent.x),
                  FocusMatrixPageNATTableSection.this.natTable.getRowPositionByY(mouseEvent.y));
              if (cell != null) {// cell is null when clicking empty area in nattable
                LabelStack configLabels = cell.getConfigLabels();
                if (cell.getColumnIndex() == CommonUIConstants.COLUMN_INDEX_3) {
                  DataLayer bodyDataLayer = FocusMatrixPageNATTableSection.this.ucFilterGridLayer.getDummyDataLayer();
                  IRowDataProvider<FocusMatrixAttributeClientBO> bodyDataProvider =
                      (IRowDataProvider<FocusMatrixAttributeClientBO>) bodyDataLayer.getDataProvider();
                  // get the row object out of the dataprovider
                  FocusMatrixAttributeClientBO rowObject = bodyDataProvider.getRowObject(cell.getRowIndex());
                  FocusMatrixRemarksAction fmRemarksAction =
                      new FocusMatrixRemarksAction(FocusMatrixPageNATTableSection.this.focusMatrixPage, rowObject);
                  fmRemarksAction.run();
                }
                else if (configLabels.hasLabel(CDRConstants.TICK) ||
                    configLabels.hasLabel("MAPPED_FROM_FOCUS_MATRIX")) {
                  Point mappedRec = FocusMatrixPageNATTableSection.this.natTable.getDisplay().map(
                      FocusMatrixPageNATTableSection.this.natTable.getParent(), null,
                      new Point(mouseEvent.x, mouseEvent.y));

                  mappedRec.y = mappedRec.y - (7 * cell.getBounds().height);

                  // get the preference from store
                  String pref = PlatformUI.getPreferenceStore().getString(CommonUtils.FM_EDIT_VIEW);
                  boolean viewNeeded = CommonUtils.isEqual(pref, ApicConstants.CODE_YES);

                  showDialogOrViewBasedOnPref(mappedRec, viewNeeded);
                }
                else {
                  FocusMatrixEditView fmEditview =
                      (FocusMatrixEditView) WorkbenchUtils.getView(FocusMatrixEditView.PART_ID);
                  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(fmEditview);
                }
              }
            }
          }
        }
        else {
          CDMLogger.getInstance().info(ApicUiConstants.EDIT_NOT_ALLOWED, Activator.PLUGIN_ID);
        }


      }

      /**
       * @param mappedRec
       * @param viewNeeded
       */
      private void showDialogOrViewBasedOnPref(final Point mappedRec, final boolean viewNeeded) {
        if (viewNeeded) {
          try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .showView("com.bosch.caltool.apic.ui.views.FocusMatrixEditView");
            FocusMatrixEditView fmEditview = (FocusMatrixEditView) WorkbenchUtils.getView(FocusMatrixEditView.PART_ID);
            fmEditview.setValuesInView(FocusMatrixPageNATTableSection.this.focusMatrixPage.fetchSelectedUcItems(),
                mappedRec, FocusMatrixPageNATTableSection.this.natTable);
          }
          catch (PartInitException exp) {
            CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }
        }
        else {
          FocusMatrixPageNATTableSection.this.colorCodeSelectionDialog =
              new FocusMatrixEditDialog(FocusMatrixPageNATTableSection.this.focusMatrixPage.fetchSelectedUcItems(),
                  mappedRec, FocusMatrixPageNATTableSection.this.natTable);
          FocusMatrixPageNATTableSection.this.colorCodeSelectionDialog.open();
        }
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void mouseDoubleClick(final MouseEvent mouseEvent) {
        // TODO Auto-generated method stub

      }

    });
  }

  /**
   * @param columnWidthMap Map<Integer, Integer>
   * @return IConfigRegistry
   */
  private IConfigRegistry createLayerAndTable(final Map<Integer, Integer> columnWidthMap) {
    // instantiate the input to column converter
    this.natInputToColumnConverter = new FocusMatrixInputToColumnConverter();
    IConfigRegistry configRegistry = new ConfigRegistry();

    // A Custom Filter Grid Layer is constructed
    this.ucFilterGridLayer =
        new CustomFilterGridLayer<FocusMatrixAttributeClientBO>(configRegistry, this.focusMatrixNatInputs,
            columnWidthMap, new CustomFocusMatrixColumnPropertyAccessor<FocusMatrixAttributeClientBO>(),
            new CustomFocusMatrixColumnHeaderDataProvider(), getUCComparator(0), this.natInputToColumnConverter,
            this.focusMatrixPage, null, true, true, false);

    // ICDM-1596
    this.outLineNatFilter =
        new OutlineUCNatFilter(false, this.focusMatrixPage.getEditorInput().getOutlineDataHandler());
    this.ucFilterGridLayer.getFilterStrategy().setOutlineNatFilterMatcher(this.outLineNatFilter.getUcOutlineMatcher());

    // all column filter matcher
    this.allColumnFilterMatcher = new AllFMColumnFilterMatcher<FocusMatrixAttributeClientBO>();
    this.ucFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    // creating the NAT table
    this.natTable = new CustomNATTable(
        this.form.getBody(), SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED |
            SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        this.ucFilterGridLayer, false, getClass().getSimpleName());
    return configRegistry;
  }

  /**
   * get the comparator for the table
   *
   * @return Comparator<FocusMatrixAttribute>
   */
  private static Comparator<FocusMatrixAttributeClientBO> getUCComparator(final int columnNum) {

    return new Comparator<FocusMatrixAttributeClientBO>() {

      /**
       * @param focusMatrixNatInput1 FocusMatrixAttribute
       * @param focusMatrixNatInput2 FocusMatrixAttribute
       * @return int compare result
       */
      @Override
      public int compare(final FocusMatrixAttributeClientBO focusMatrixNatInput1,
          final FocusMatrixAttributeClientBO focusMatrixNatInput2) {

        com.bosch.caltool.icdm.client.bo.fm.FocusMatrixAttributeClientBO.SortColumn sortCol =
            FocusMatrixAttributeClientBO.SortColumn.getType(columnNum);
        return focusMatrixNatInput1.compareTo(focusMatrixNatInput2, sortCol, columnNum);
      }
    };
  }

  /**
   * @return Map<Integer, Integer> column width map
   * @throws ApicWebServiceException
   */
  private Map<Integer, Integer> createModelHeaderForTable() throws ApicWebServiceException {


    this.propertyToLabelMap = new HashMap<Integer, String>();
    this.propertyToLabelMap.put(0, "Attribute");
    this.propertyToLabelMap.put(1, "Description");
    this.propertyToLabelMap.put(2, "Value");
    this.propertyToLabelMap.put(3, "Remark");

    // The below map is used by NatTable to Map Columns with their respective widths
    // Width is based on pixels
    ConcurrentMap<Integer, Integer> columnWidthMap = new ConcurrentHashMap<Integer, Integer>();
    columnWidthMap.put(0, ATTRIBUTE_COLUMN_WIDTH);
    columnWidthMap.put(1, DESCRIPTION_COLUMN_WIDTH);
    columnWidthMap.put(2, VALUE_COLUMN_WIDTH);
    columnWidthMap.put(3, REMARK_COLUMN_WIDTH);

    if (!CommonUtils.isEqual(this.model, getSelectedFmVersion())) {
      FocusMatrixPageNATTableSection.this.model = getSelectedFmVersion();
    }

    this.focusMatrixNatInputs = this.model.getFocusMatrixAttrsSet();


    if (null != this.model.getSelectedUCItemsList()) {
      for (int i = STATIC_COL_INDEX; i < (this.model.getSelectedUCItemsList().size() + STATIC_COL_INDEX); i++) {
        this.propertyToLabelMap.put(i, this.model.getSelectedUCItemsList().get(i - STATIC_COL_INDEX).getName());
        columnWidthMap.put(i, OTHER_COLUMN_WIDTH);
      }
    }
    return columnWidthMap;
  }

  /**
   * @return selected Focus matrix version
   */
  // ICDM-2612
  private FocusMatrixVersionClientBO getSelectedFmVersion() {
    return getFocusMatrixPage().getEditorInput().getFmDataHandler().getSelFmVersion();
  }


  /**
   * Enables tootltip only for cells which contain not fully visible content
   *
   * @param natTableObj NatTable
   */
  private void attachToolTip(final NatTable natTableObj) {
    // Icdm-1208- Custom tool tip for Nat table.
    DefaultToolTip toolTip = new FocusMatrixNatToolTip(natTableObj, new String[0], this);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(XCOORDINATE_TEN, YCOORDINATE_TEN));
  }

  /**
   * This method adds right click menu for tableviewer
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(final IMenuManager manager) {
        String flag = "";
        String linkStr = "";// ICDM-1740

        SelectionLayer selectionLayer =
            FocusMatrixPageNATTableSection.this.ucFilterGridLayer.getBodyLayer().getSelectionLayer();
        PositionCoordinate[] selectedCellPositions = selectionLayer.getSelectedCellPositions();

        if (null != selectedCellPositions) {

          List<IProjectAttribute> listOfAttrs = new ArrayList<>();
          for (PositionCoordinate positionCoordinate : selectedCellPositions) {
            int columnIndex = selectionLayer.getColumnIndexByPosition(positionCoordinate.columnPosition);
            FocusMatrixAttributeClientBO fmAttr = FocusMatrixPageNATTableSection.this.ucFilterGridLayer
                .getBodyDataProvider().getRowObject(positionCoordinate.rowPosition);

            if ((selectedCellPositions.length == SIZE_ONE) && (columnIndex > CommonUIConstants.COLUMN_INDEX_3)) {
              // Fetch selected Cell Row object
              com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem fmUseCaseItem =
                  fmAttr.getFocusmatrixUseCaseItem(columnIndex - 4);

              flag = "EMPTY";
              if (fmUseCaseItem.isMapped(fmAttr.getAttribute())) {
                flag = "FM";
              }
              linkStr = fmUseCaseItem.getLink(fmAttr.getAttribute());

            }
            else if (columnIndex == CommonUIConstants.COLUMN_INDEX_2) {
              PidcVersionAttributeBO pidcAttr = fmAttr.getPidcAttr();
              listOfAttrs.add(pidcAttr.getProjectAttr());
            }
            else if (((selectedCellPositions.length == SIZE_ONE) &&
                (columnIndex == CommonUIConstants.COLUMN_INDEX_3))) {
              FocusMatrixRemarksAction fmRemarksAction =
                  new FocusMatrixRemarksAction(FocusMatrixPageNATTableSection.this.focusMatrixPage, fmAttr);
              menuMgr.add(fmRemarksAction);
            }
          }
          addRelevantForFocuMatrixActions(menuMgr, flag);
          addLinkAction(menuMgr, linkStr);
          addPIDCSearchAction(menuMgr, listOfAttrs);

        }
      }

      /**
       * @param menuMgr MenuManager
       * @param linkStr String
       */
      private void addLinkAction(final MenuManager menuMgr, final String linkStr) {
        // ICDM-1740
        if (!CommonUtils.isEmptyString(linkStr)) {
          menuMgr.add(new Separator());
          CommonActionSet actionSet = new CommonActionSet();
          actionSet.stringAsLinkAction(menuMgr, linkStr);
        }
      }

      /**
       * @param menuMgr
       * @param listOfAttrs
       */
      private void addPIDCSearchAction(final MenuManager menuMgr, final List<IProjectAttribute> listOfAttrs) {
        if (!listOfAttrs.isEmpty()) {
          final PIDCSearchAction pidcSearchAction = new PIDCSearchAction(listOfAttrs,
              ((PIDCEditorInput) FocusMatrixPageNATTableSection.this.focusMatrixPage.getEditor().getEditorInput())
                  .getSelectedPidcVersion(),
              FocusMatrixPageNATTableSection.this.focusMatrixPage.getEditorInput().getFmDataHandler()
                  .getPidcDataHandler());
          menuMgr.add(pidcSearchAction);
        }
      }

      /**
       * This method marks an usecase item as FM relevant/unrelevant
       *
       * @param menuMngr
       * @param flag
       */
      private void addRelevantForFocuMatrixActions(final MenuManager menuMngr, final String flag) {
        if (!flag.isEmpty()) {
          if ("EMPTY".equals(flag)) {
            menuMngr.add(relevantForFocusMatrixAction(false));
          }
          else if (("FM").equals(flag)) {
            menuMngr.add(relevantForFocusMatrixAction(true));
          }
        }

      }

    });
    final Menu menu = menuMgr.createContextMenu(this.natTable.getShell());
    this.natTable.setMenu(menu);
    // Register menu for extension.
    this.focusMatrixPage.getSite().registerContextMenu(menuMgr, this.selectionProvider);

  }


  private IAction relevantForFocusMatrixAction(final boolean deleteFlag) {
    Action relevantAction = new Action() {

      /**
       * {@inheritDoc} on clicking the edit option, display the color selection dialog
       */
      @Override
      public void run() {

        PIDCActionSet pidcAction = new PIDCActionSet();

        Map<FocusMatrixAttributeClientBO, List<FocusMatrixUseCaseItem>> fetchSelectedUcItems =
            FocusMatrixPageNATTableSection.this.focusMatrixPage.fetchSelectedUcItems();

        if (fetchSelectedUcItems.size() == 1) {

          // for single update
          FocusMatrixAttributeClientBO fmAttribute = fetchSelectedUcItems.keySet().iterator().next();

          try {
            if (!FocusMatrixPageNATTableSection.this.model.getFMDataHandler().getSelFmVersion().isModifiable()) {
              // if the user does not have privilege to modify the focus matrix
              pidcAction.showEditNotAllowedDialog(true);
              return;
            }

            // Code moved for Sonar Qube fix
            List<FocusMatrixUseCaseItem> focusMatrixItem = fetchSelectedUcItems.get(fmAttribute);
            FocusMatrixUseCaseItem focusMatrixUseCaseItem = focusMatrixItem.get(0);

            if (!fmAttribute.isFocusMatrixApplicable()) {
              // mark fm flag for pidc version attribute
              PIDCPageEditUtil pidcPageEditUtil =
                  new PIDCPageEditUtil(FocusMatrixPageNATTableSection.this.model.getFMDataHandler().getPidcVersionBO());
              pidcPageEditUtil.editFMRelevantFlag(fmAttribute.getPidcAttr().getPidcVersAttr(), true,
                  FocusMatrixPageNATTableSection.this.model.getFMDataHandler());
            }

            // WebService Call
            Map<Long, FocusMatrixDetails> attrFmMap = focusMatrixUseCaseItem.getFocusMatrixVersion()
                .getFocusMatrixItemMap().get(fmAttribute.getAttribute().getId());

            IUseCaseItemClientBO useCaseItem = focusMatrixUseCaseItem.getUseCaseItem();
            Long useCaseItemId = useCaseItem.getID();
            FocusMatrixDetails existingFocusMatrix = attrFmMap == null ? null : attrFmMap.get(useCaseItemId);

            FocusMatrixServiceClient fmClient = new FocusMatrixServiceClient();

            FocusMatrixVersionClientBO fmVers = getSelectedFmVersion();
            Long fmUcpaId = focusMatrixUseCaseItem.getAttributeMapping().get(fmAttribute.getAttribute());

            callWebService(deleteFlag, fmAttribute, useCaseItem, useCaseItemId, existingFocusMatrix, fmClient, fmVers,
                fmUcpaId);
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
          }

        }
      }
    };

    relevantAction.setEnabled(true);
    if (deleteFlag) {
      relevantAction.setText("Not relevant for Focus matrix");
    }
    else {
      relevantAction.setText("Relevant for Focus Matrix");
    }
    // add edit image icon
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.TICK_COLUMN_16X16);
    final ImageDescriptor imageDescDel = ImageManager.getImageDescriptor(ImageKeys.TICK_DELETE_16X16);
    if (deleteFlag) {
      relevantAction.setImageDescriptor(imageDescDel);
    }
    else {
      relevantAction.setImageDescriptor(imageDesc);
    }
    return relevantAction;
  }

  /**
   * @param deleteFlag
   * @param fmAttribute
   * @param useCaseItem
   * @param useCaseItemId
   * @param existingFocusMatrix
   * @param fmClient
   * @param fmVers
   * @param fmUcpaId
   * @throws ApicWebServiceException
   */
  private void callWebService(final boolean deleteFlag, final FocusMatrixAttributeClientBO fmAttribute,
      final IUseCaseItemClientBO useCaseItem, final Long useCaseItemId, final FocusMatrixDetails existingFocusMatrix,
      final FocusMatrixServiceClient fmClient, final FocusMatrixVersionClientBO fmVers, final Long fmUcpaId)
      throws ApicWebServiceException {

    FocusMatrix focusMatrix;
    if ((existingFocusMatrix == null) && !deleteFlag) {
      createFocusMtrxNew(fmAttribute, useCaseItem, useCaseItemId, fmClient, fmVers, fmUcpaId);
    }
    else if (existingFocusMatrix != null) {
      focusMatrix = existingFocusMatrix.getFocusMatrix();
      FocusMatrix fcsMatrixClone = focusMatrix.clone();
      fcsMatrixClone.setIsDeleted(deleteFlag);
      fmClient.update(fcsMatrixClone);
    }
  }

  /**
   * @param fmAttribute
   * @param useCaseItem
   * @param useCaseItemId
   * @param fmClient
   * @param fmVers
   * @param fmUcpaId
   * @throws ApicWebServiceException
   */
  private void createFocusMtrxNew(final FocusMatrixAttributeClientBO fmAttribute,
      final IUseCaseItemClientBO useCaseItem, final Long useCaseItemId, final FocusMatrixServiceClient fmClient,
      final FocusMatrixVersionClientBO fmVers, final Long fmUcpaId)
      throws ApicWebServiceException {
    FocusMatrix fcsMtrx = new FocusMatrix();
    fcsMtrx.setFmVersId(fmVers.getFmVersion().getId());
    fcsMtrx.setAttrId(fmAttribute.getAttribute().getId());
    if (useCaseItem instanceof UseCaseSectionClientBO) {
      UseCaseSectionClientBO obj = (UseCaseSectionClientBO) useCaseItem;
      fcsMtrx.setSectionId(obj.getUseCaseSection().getId());
      fcsMtrx.setUseCaseId(obj.getUseCaseSection().getUseCaseId());
    }
    if (useCaseItem instanceof UsecaseClientBO) {
      fcsMtrx.setUseCaseId(useCaseItemId);
    }

    fcsMtrx.setUcpaId(fmUcpaId);
    fcsMtrx.setIsDeleted(false);
    fmClient.create(fcsMtrx);
  }

  /**
   * grouping columns with selected item as group name for all mappable & focus relevant items below it
   */
  private void groupColumns() {

    if (null != this.selectedUcItem) {

      if (!((this.selectedUcItem instanceof UseCaseSectionClientBO) &&
          (this.selectedUcItem.getChildUCItems().isEmpty()))) { // do
        // not
        // group
        // columns
        // for
        // leaf
        // use
        // case
        // sections
        List<Integer> selectedPositions = new ArrayList<Integer>();
        int[] fullySelectedColumns = new int[this.propertyToLabelMap.size() - STATIC_COL_INDEX];
        // store the indices in an integer array
        for (int colIndex = STATIC_COL_INDEX; colIndex < (this.propertyToLabelMap.size()); colIndex++) {
          selectedPositions.add(colIndex);
          fullySelectedColumns[colIndex - STATIC_COL_INDEX] = colIndex;
        }

        ColumnGroupModel columnGroupModel = this.ucFilterGridLayer.getColumnGroupModel();
        columnGroupModel.addColumnsIndexesToGroup(this.selectedUcItem.getName(), fullySelectedColumns);
        SelectionLayer selectionLayer = this.ucFilterGridLayer.getBodyLayer().getSelectionLayer();
        if (!selectedPositions.isEmpty()) {
          selectionLayer.doCommand(
              new MultiColumnReorderCommand(selectionLayer, selectedPositions, selectedPositions.get(0).intValue()));
        }
      }
    }

  }


  /**
   * @param columnHeaderDataLayer AbstractLayer
   * @return IConfiguration
   */
  private IConfiguration getCustomComparatorConfiguration(final AbstractLayer columnHeaderDataLayer) {

    return new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Add label accumulator
        ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
        columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);

        // Register labels for first two columns
        labelAccumulator.registerColumnOverrides(0, CUSTOM_COMPARATOR_LABEL + 0);

        labelAccumulator.registerColumnOverrides(1, CUSTOM_COMPARATOR_LABEL + 1);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getUCComparator(0), NORMAL,
            CUSTOM_COMPARATOR_LABEL + 0);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getUCComparator(1), NORMAL,
            CUSTOM_COMPARATOR_LABEL + 1);

        for (int col_index = 2; col_index < FocusMatrixPageNATTableSection.this.propertyToLabelMap
            .size(); col_index++) {
          labelAccumulator.registerColumnOverrides(col_index, CUSTOM_COMPARATOR_LABEL + col_index);
          configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getUCComparator(col_index),
              NORMAL, CUSTOM_COMPARATOR_LABEL + col_index);
        }


        // Register null comparator to disable sort
      }
    };
  }


  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent modifyEvent) {

        String text = FocusMatrixPageNATTableSection.this.filterTxt.getText().trim();
        FocusMatrixPageNATTableSection.this.allColumnFilterMatcher.setFilterText(text, true);
        FocusMatrixPageNATTableSection.this.ucFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
        FocusMatrixPageNATTableSection.this.ucFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                FocusMatrixPageNATTableSection.this.ucFilterGridLayer.getSortableColumnHeaderLayer()));
        FocusMatrixPageNATTableSection.this.focusMatrixPage.setStatusBarMessage(false);

      }
    });
  }

  /**
   * column property accessor
   *
   * @author mkl2cob
   * @param <T>
   */
  class CustomFocusMatrixColumnPropertyAccessor<T> implements IColumnAccessor<T> {


    /**
     * This method has been overridden so that it returns the passed row object. The above behavior is required for use
     * of custom comparators for sorting which requires the Row object to be passed without converting to a particular
     * column String value {@inheritDoc}
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDataValue(final T type, final int columnIndex) {
      return type;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataValue(final T sysConstNatModel, final int columnIndex, final Object newValue) {
      // TODO:
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {
      return FocusMatrixPageNATTableSection.this.propertyToLabelMap.size();
    }


  }

  /**
   * Column header data provider class
   *
   * @author mkl2cob
   */
  class CustomFocusMatrixColumnHeaderDataProvider implements IDataProvider {


    /**
     * @param columnIndex int
     * @return String column header label
     */
    public String getColumnHeaderLabel(final int columnIndex) {
      String string = FocusMatrixPageNATTableSection.this.propertyToLabelMap.get(columnIndex);

      return string == null ? "" : string;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {
      return FocusMatrixPageNATTableSection.this.propertyToLabelMap.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowCount() {
      return 1;
    }

    /**
     * This class does not support multiple rows in the column header layer.
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDataValue(final int columnIndex, final int rowIndex) {
      return getColumnHeaderLabel(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataValue(final int columnIndex, final int rowIndex, final Object newValue) {
      throw new UnsupportedOperationException();
    }

  }

  /**
   * @return the ucFilterGridLayer
   */
  public CustomFilterGridLayer<FocusMatrixAttributeClientBO> getUcFilterGridLayer() {
    return this.ucFilterGridLayer;
  }

  /**
   * @author mkl2cob
   */
  private class FilterRowCustomConfiguration extends AbstractRegistryConfiguration {

    /**
     * @author jvi6cob
     */
    private final class FilterDisplayConverter extends DisplayConverter {

      /**
       * {@inheritDoc}
       */
      @Override
      public Object displayToCanonicalValue(final Object displayValue) {
        // TODO Auto-generated method stub
        return null;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Object canonicalToDisplayValue(final Object canonicalValue) {
        // TODO Auto-generated method stub
        if (canonicalValue instanceof FocusMatrixUsecaseItemFilterTxt) {
          FocusMatrixUsecaseItemFilterTxt canVal = (FocusMatrixUsecaseItemFilterTxt) canonicalValue;
          return canVal.getFilterTxt();
        }
        return canonicalValue;
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureRegistry(final IConfigRegistry configRegistry) {
      // override the default filter row configuration for painter
      configRegistry.registerConfigAttribute(CELL_PAINTER,
          new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);


      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 0);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);
      configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultDisplayConverter() {

        /**
         * {@inheritDoc}
         */
        @Override
        public Object canonicalToDisplayValue(final Object canonicalValue) {
          if (canonicalValue instanceof FocusMatrixUsecaseItemFilterTxt) {
            return "";
          }
          return super.canonicalToDisplayValue(canonicalValue);
        }
      }, NORMAL);

      List<String> comboList =
          Arrays.asList("Red", "Orange", "Green", "Yellow", "White", "<ANY COLOR>", "<NOT MAPPED>");
      for (int i = STATIC_COL_INDEX; i < FocusMatrixPageNATTableSection.this.propertyToLabelMap.size(); i++) {
        // register a combo box cell editor for the gender column in the filter row
        // the label is set automatically to the value of
        // FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + column position
        ICellEditor comboBoxCellEditor = new ComboBoxCellEditor(comboList);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
            FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + i);
        // Configure Filter Display Converter
        configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
            new FilterDisplayConverter(), NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + i);
      }

    }
  }


  /**
   * @return the colorCodeSelectionDialog
   */
  public FocusMatrixEditDialog getColorCodeSelectionDialog() {
    return this.colorCodeSelectionDialog;
  }


  /**
   * @return the focusMatrixPage
   */
  public FocusMatrixPage getFocusMatrixPage() {
    return this.focusMatrixPage;
  }


  /**
   * @return the model
   */
  public FocusMatrixVersionClientBO getModel() {
    return this.model;
  }

  /**
   * @return
   */
  // ICDM-2612
  private PidcVersion getSelectedPidcVersion() {
    return FocusMatrixPageNATTableSection.this.focusMatrixPage.getEditorInput().getSelectedPidcVersion();
  }


  /**
   * @return the selectedUcItem
   */
  public IUseCaseItemClientBO getSelectedUcItem() {
    return this.selectedUcItem;
  }


  /**
   * @return the propertyToLabelMap
   */
  public Map<Integer, String> getPropertyToLabelMap() {
    return this.propertyToLabelMap;
  }


  /**
   * @return the natTable
   */
  public CustomNATTable getNatTable() {
    return this.natTable;
  }


  /**
   * @param selected the selectedUcItem to set
   */
  public void setSelectedUcItem(final IUseCaseItemClientBO selected) {
    this.selectedUcItem = selected;
  }


  /**
   * @param ucFilterGridLayer the ucFilterGridLayer to set
   */
  public void setUcFilterGridLayer(final CustomFilterGridLayer<FocusMatrixAttributeClientBO> ucFilterGridLayer) {
    this.ucFilterGridLayer = ucFilterGridLayer;
  }


  /**
   * @return the filterTxt
   */
  public Text getFilterTxt() {
    return this.filterTxt;
  }


  /**
   * @return the totTableRowCount
   */
  public int getTotTableRowCount() {
    return this.totTableRowCount;
  }


  /**
   * @param totTableRowCount the totTableRowCount to set
   */
  public void setTotTableRowCount(final int totTableRowCount) {
    this.totTableRowCount = totTableRowCount;
  }


  /**
   * @return the form
   */
  public Form getForm() {
    return this.form;
  }


  /**
   * @return the outLineNatFilter
   */
  public OutlineUCNatFilter getOutLineNatFilter() {
    return this.outLineNatFilter;
  }

  /**
   * @param selectedFmVersion FocusMatrixVersionClientBO
   */
  public void setModel(final FocusMatrixVersionClientBO selectedFmVersion) {
    this.model = selectedFmVersion;
  }


}