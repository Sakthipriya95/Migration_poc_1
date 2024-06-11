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

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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

import com.bosch.caltool.apic.ui.dialogs.PasswordDialog;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.DataAssessmentDownloadAction;
import com.bosch.caltool.cdr.ui.editors.DataAssessmentReportEditorInput;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.DataAssessmentBaselineColumnFilterMatcher;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.DataAssessmentBaselinesEditConfiguration;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.DataAssessmentBaselinesNattableLabelAccumulator;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.FilterRowCustomConfiguration;
import com.bosch.caltool.icdm.client.bo.cdr.DataAssessmentSharePointUploadBO;
import com.bosch.caltool.icdm.client.bo.cdr.DataAssmntReportDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.editors.AbstractNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.FILE_ARCHIVAL_STATUS;
import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;
import com.bosch.caltool.icdm.model.cdr.DataAssessSharePointUploadInputModel;
import com.bosch.caltool.icdm.ws.rest.client.cdr.DaDataAssessmentDownloadServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.DataAssessmentSharePointUploadServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;
import com.bosch.caltool.nattable.CustomColumnHeaderLayerConfiguration;
import com.bosch.caltool.nattable.CustomColumnHeaderStyleConfiguration;
import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author ajk2cob
 */
public class DataAssessmentBaselinesPage extends AbstractNatFormPage implements ISelectionListener {

  /**
   *
   */
  private static final String SHARE_POINT_UPLOAD_CONFIRM_MSG =
      "The selected Data Assessment file will be upload to the SharePoint URL configured, You will be notified once the process is completed.";

  private static final String SHARE_POINT_ARCHIVAL_ONLY_FOR_SERIES_RELEASE =
      "SharePoint Archival is allowed only for Data Assessment Baselines with Series release.";
  /**
   *
   */
  private static final String SHARE_POINT_UPLOAD = "SharePoint Upload";
  /**
   * Editor instance
   */
  private final FormEditor editor;
  /**
   * Data Assessment Report DataHandler
   */
  private final DataAssmntReportDataHandler dataAssmntReportDataHandler;
  /**
   * Non scrollable baselineForm
   */
  private Form nonScrollableForm;
  /**
   * Baseline section
   */
  private Section baselineSection;
  /**
   * Baseline form
   */
  private Form baselineForm;
  /**
   * Filter text
   */
  private Text filterTxt;
  /**
   * Nattable labels
   */
  private Map<Integer, String> propertyToLabelMap;
  /**
   * Nattable column width
   */
  Map<Integer, Integer> columnWidthMap = new HashMap<>();
  /**
   * Baseline filter grid layer
   */
  private CustomFilterGridLayer baselinesFilterGridLayer;
  /**
   * Baseline nattable
   */
  private CustomNATTable natTable;
  /**
   * Baseline column filter matcher
   */
  private DataAssessmentBaselineColumnFilterMatcher<DaDataAssessment> allColumnFilterMatcher;

  private static final int COLUMN_NUM_BASELINE_NAME = 0;

  private static final int COLUMN_NUM_BASELINE_REMARKS = 1;

  private static final int COLUMN_NUM_BASELINE_PIDC_VARIANT = 2;

  private static final int COLUMN_NUM_BASELINE_CREATED_ON = 3;

  private static final int COLUMN_NUM_BASELINE_ASSESSMENT_TYPE = 4;

  private static final int COLUMN_NUM_BASELINE_OPEN_ARCHIEVED_FILE = 5;

  private static final int COLUMN_NUM_BASELINE_ARCHIEVED_FILE_STATUS = 6;

  private static final int COLUMN_NUM_BASELINE_ARCHIEVE_IN_SHAREPOINT = 7;

  private static final String BASELINES = "Baselines";

  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  /**
   * Data assessment baselines page, Constructor.
   *
   * @param editor editor
   */
  public DataAssessmentBaselinesPage(final FormEditor editor) {
    super(editor, "dataAssessmentBaselines", BASELINES);
    this.editor = editor;
    this.dataAssmntReportDataHandler =
        ((DataAssessmentReportEditorInput) editor.getEditorInput()).getDataAssmntReportDataHandler();
  }

  @Override
  public void createPartControl(final Composite parent) {
    // create an non-scrollable form on which widgets are built
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    this.nonScrollableForm.setText(BASELINES);
    // instead of editor.getToolkit().createScrolledForm(parent); in superclass
    // formToolkit is obtained from managed form to create form within baselineSection
    ManagedForm mform = new ManagedForm(parent);
    FormToolkit formToolkit = mform.getToolkit();
    // create composite for the baselines section
    Composite composite = this.nonScrollableForm.getBody();
    composite.setLayout(new GridLayout());
    // create baselines section
    this.baselineSection = formToolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.baselineSection.setExpanded(true);
    this.baselineSection.setText(BASELINES);
    this.baselineSection.setDescription("Overview of all baselines of the data assessment for this A2L and variant");
    this.baselineSection.getDescriptionControl().setEnabled(false);
    this.baselineSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    // create baseline form
    createBaselineForm(formToolkit);

    this.baselineSection.setClient(this.baselineForm);
  }

  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   * This method creates the baseline form
   */
  private void createBaselineForm(final FormToolkit toolkit) {
    this.baselineForm = toolkit.createForm(this.baselineSection);
    this.baselineForm.getBody().setLayout(new GridLayout());

    createFilterTxt(toolkit);
    createTable();
    addMouseListener();
  }

  /**
   * This method creates filter text for nattable
   */
  private void createFilterTxt(final FormToolkit toolkit) {
    this.filterTxt = TextUtil.getInstance().createFilterText(toolkit, this.baselineForm.getBody(),
        getFilterTxtGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      String text = DataAssessmentBaselinesPage.this.filterTxt.getText().trim();
      DataAssessmentBaselinesPage.this.allColumnFilterMatcher.setFilterText(text, true);
      DataAssessmentBaselinesPage.this.baselinesFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
      DataAssessmentBaselinesPage.this.baselinesFilterGridLayer.getSortableColumnHeaderLayer()
          .fireLayerEvent(new FilterAppliedEvent(
              DataAssessmentBaselinesPage.this.baselinesFilterGridLayer.getSortableColumnHeaderLayer()));
    });
  }

  /**
   * This method creates nattable
   */
  private void createTable() {
    Set<DaDataAssessment> dataAssmntBaselines =
        new HashSet<>(this.dataAssmntReportDataHandler.getDataAssessmentReport().getDataAssmntBaselines());
    AbstractNatInputToColumnConverter natInputToColumnConverter = new DataAssessmentBaselineNatInputToColumnConverter();
    IConfigRegistry configRegistry = new ConfigRegistry();

    createNatTableColumns();

    this.baselinesFilterGridLayer =
        new CustomFilterGridLayer(configRegistry, dataAssmntBaselines, this.propertyToLabelMap, this.columnWidthMap,
            getCompareDaBaselinesComparator(2), natInputToColumnConverter, this, null, true, true);

    this.allColumnFilterMatcher = new DataAssessmentBaselineColumnFilterMatcher<>();
    this.baselinesFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    this.natTable = new CustomNATTable(this.baselineForm.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL,
        this.baselinesFilterGridLayer, false, this.getClass().getSimpleName(), this.propertyToLabelMap);

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
    this.natTable
        .addConfiguration(getCustomComparatorConfiguration(this.baselinesFilterGridLayer.getColumnHeaderDataLayer()));
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

    this.baselinesFilterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));

    // Registry Configuration class for data assessment editor which assigns style based on the config labels registered
    // on the cells
    this.natTable.addConfiguration(new DataAssessmentBaselinesEditConfiguration());

    // Used for registration/addition of labels for a given column.
    DataLayer bodyDataLayer = this.baselinesFilterGridLayer.getDummyDataLayer();
    final DataAssessmentBaselinesNattableLabelAccumulator baselinesLabelAccumulator =
        new DataAssessmentBaselinesNattableLabelAccumulator(bodyDataLayer);
    bodyDataLayer.setConfigLabelAccumulator(baselinesLabelAccumulator);

    CustomDefaultBodyLayerStack bodyLayer = this.baselinesFilterGridLayer.getBodyLayer();
    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.baselinesFilterGridLayer.getColumnHeaderLayer(),
            this.baselinesFilterGridLayer.getColumnHeaderDataLayer(), null, null);
    this.natTable.registerCommandHandler(columnChooserCommandHandler);
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
    this.natTable.addDisposeListener(event ->
    // save state
    saveState());

    this.baselinesFilterGridLayer.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.natTable));

    RowSelectionProvider<DaDataAssessment> selectionProvider =
        new RowSelectionProvider<>(this.baselinesFilterGridLayer.getBodyLayer().getSelectionLayer(),
            this.baselinesFilterGridLayer.getBodyDataProvider(), false);

    selectionProvider.addSelectionChangedListener(event -> {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      if (selection.getFirstElement() instanceof DaDataAssessment) {
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

    // The below method is required to enable tootltip only for cells which contain not fully visible content
    attachToolTip();
    getSite().setSelectionProvider(selectionProvider);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    refreshUiData();
  }

  /**
   * to Refresh UI data
   */
  public void refreshUiData() {
    if (CommonUtils.isNotNull(this.baselinesFilterGridLayer)) {
      Set<DaDataAssessment> dataAssmntBaselines =
          new HashSet<>(this.dataAssmntReportDataHandler.getDataAssessmentReport().getDataAssmntBaselines());
      this.baselinesFilterGridLayer.getEventList().clear();
      this.baselinesFilterGridLayer.getEventList().addAll(dataAssmntBaselines);
      refreshNatTable();
    }
  }

  /**
   * refresh the table
   */
  private void refreshNatTable() {
    if (CommonUtils.isNotNull(this.natTable)) {
      this.natTable.doCommand(new StructuralRefreshCommand());
      this.natTable.doCommand(new VisualRefreshCommand());
      this.natTable.refresh();
      updateStatusBar(false);
    }
  }

  /**
   * Mouse listener to the table viewer
   */
  private void addMouseListener() {
    this.natTable.addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(final MouseEvent mouseEvent) {
        // NA
      }

      @Override
      public void mouseDown(final MouseEvent mouseEvent) {
        // NA
      }

      @Override
      public void mouseDoubleClick(final MouseEvent mouseEvent) {
//        Left double click
        if (mouseEvent.button == 1) {
          leftMouseDoubleClickAction(mouseEvent);
        }
      }
    });
  }

  /**
   * @param mouseEvent
   */
  private void leftMouseDoubleClickAction(final MouseEvent mouseEvent) {
    ILayerCell cell = this.natTable.getCellByPosition(this.natTable.getColumnPositionByX(mouseEvent.x),
        this.natTable.getRowPositionByY(mouseEvent.y));
    if (cell != null) {// cell is null when clicking empty area in nattable
      LabelStack configLabels = cell.getConfigLabels();
      if (isConfigLblHasBaselineHyperLinkLbl(configLabels)) {
        int row = LayerUtil.convertRowPosition(this.natTable, this.natTable.getRowPositionByY(mouseEvent.y),
            ((CustomFilterGridLayer<DaDataAssessment>) this.natTable.getLayer()).getDummyDataLayer());
        Object rowObject = this.baselinesFilterGridLayer.getBodyDataProvider().getRowObject(row);
        dataAssessmentLeftClickAction(configLabels, rowObject);
      }
    }
  }

  /**
   * @param cell
   * @param rowObject
   */
  private void dataAssessmentLeftClickAction(final LabelStack configLabels, final Object rowObject) {
    if (rowObject instanceof DaDataAssessment) {
      final DaDataAssessment compareRowObject = (DaDataAssessment) rowObject;
      // Link to open Baseline in editordata
      if (configLabels.hasLabel(CDRConstants.BASELINE_HYPERLINK)) {
        openBaselineById(compareRowObject);
      }
      // Link to Download baseline
      if (configLabels.hasLabel(CDRConstants.FILE_HYPERLINK)) {
        boolean availableForDownload = checkFileAvailability(compareRowObject.getId());
        if (availableForDownload) {
          new DataAssessmentDownloadAction(compareRowObject);
        }
      }
      // Link to Archive files to Sharepoint
      if (configLabels.hasLabel(CDRConstants.SHARE_POINT_UPLOAD)) {
        dataAssessmentShareUploadFiles(compareRowObject.getId(), compareRowObject.getTypeOfAssignment(),
            compareRowObject.getBaselineName());
      }

    }
  }

  private void dataAssessmentShareUploadFiles(final Long dataAssessmentId, final String type,
      final String baselineName) {
    if (checkSharePointUploadConditions(dataAssessmentId, type)) {
      Long pidcVersionId = this.dataAssmntReportDataHandler.getDataAssessmentReport().getPidcVersId();
      CurrentUserBO currentUserBO = new CurrentUserBO();
      try {
        if (!currentUserBO.hasPassword()) {
          new PasswordDialog(Display.getDefault().getActiveShell()).open();
        }
        DataAssessmentSharePointUploadBO sharePointBo =
            new DataAssessmentSharePointUploadBO(dataAssessmentId, pidcVersionId, true);
        sharePointBo.checkAndLoadPidcSharePointUrl();
        DataAssessSharePointUploadInputModel inputModel = sharePointBo.getDataAssessmentToSharePointInput();
        // input Model will be null if any of the validation fails
        if (inputModel != null) {
          new DataAssessmentSharePointUploadServiceClient().uploadFileToSharePoint(inputModel);
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(CommonUIConstants.EXCEPTION + e.getMessage(), Activator.PLUGIN_ID);
      }
    }


  }

  private boolean checkSharePointUploadConditions(final Long dataAssessmentId, final String type) {
    if (!CDRConstants.TYPE_OF_ASSESSMENT.SERIES_RELEASE.getDbType().equals(type)) {
      CDMLogger.getInstance().infoDialog(SHARE_POINT_ARCHIVAL_ONLY_FOR_SERIES_RELEASE, Activator.PLUGIN_ID);
      return false;
    }
    return checkFileAvailability(dataAssessmentId) && MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
        SHARE_POINT_UPLOAD, SHARE_POINT_UPLOAD_CONFIRM_MSG);
  }

  /**
   * Method to check if the baseline file is available for download
   *
   * @param dataAssessmentId Data assessment ID
   * @return file availabilit status
   */
  private boolean checkFileAvailability(final Long dataAssessmentId) {
    boolean fileAvailable = false;

    try {
      // Checking the file archival status from T_DA_FILES table
      String fileStatus = new DaDataAssessmentDownloadServiceClient().checkFileAvailability(dataAssessmentId);
      CommonDataBO commonBo = new CommonDataBO();

      if (fileStatus != null) {
        // File creation completed successfully and hence file is available
        if (FILE_ARCHIVAL_STATUS.COMPLETED.getDbType().equalsIgnoreCase(fileStatus)) {
          fileAvailable = true;
        }
        // File creation is still in progress and hence file is not available
        else if (FILE_ARCHIVAL_STATUS.IN_PROGRESS.getDbType().equalsIgnoreCase(fileStatus)) {
          CDMLogger.getInstance().infoDialog(
              commonBo.getMessage(CDRConstants.DATA_ASSESSMENT, CDRConstants.FILE_STATUS_IN_PROGRESS),
              Activator.PLUGIN_ID);
        }
        // File creation failed and hence file is not available
        else if (FILE_ARCHIVAL_STATUS.FAILED.getDbType().equalsIgnoreCase(fileStatus)) {
          CDMLogger.getInstance().infoDialog(
              commonBo.getMessage(CDRConstants.DATA_ASSESSMENT, CDRConstants.FILE_STATUS_FAILED), Activator.PLUGIN_ID);
        }
        // File status could not be retrieved and hence file is not available for download
        else {
          CDMLogger.getInstance().infoDialog(
              commonBo.getMessage(CDRConstants.DATA_ASSESSMENT, CDRConstants.FILE_STATUS_NOT_AVAILABLE),
              Activator.PLUGIN_ID);
        }
      }
      // File status could not be retrieved and hence file is not available for download
      else {
        CDMLogger.getInstance().infoDialog(
            commonBo.getMessage(CDRConstants.DATA_ASSESSMENT, CDRConstants.FILE_STATUS_NOT_AVAILABLE),
            Activator.PLUGIN_ID);
      }

    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(CommonUIConstants.EXCEPTION + ex.getMessage(), Activator.PLUGIN_ID);
    }

    return fileAvailable;
  }

  /**
   * @param compareRowObject
   */
  private void openBaselineById(final DaDataAssessment compareRowObject) {
    try {
      if (CommonUtils.isNotNull(compareRowObject.getId()) &&
          !new DataAssmntBaselineHandler(compareRowObject.getId()).open()) {
        MessageDialogUtils.getInfoMessageDialog("Baseline failed to open",
            "Baseline failed to open. Please try again later.");
      }
    }
    catch (Exception exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  private boolean isConfigLblHasBaselineHyperLinkLbl(final LabelStack configLabels) {
    return configLabels.hasLabel(CDRConstants.BASELINE_HYPERLINK) ||
        configLabels.hasLabel(CDRConstants.FILE_HYPERLINK) || configLabels.hasLabel(CDRConstants.SHARE_POINT_UPLOAD);
  }

  private Map<Integer, Integer> createNatTableColumns() {
    this.propertyToLabelMap = new HashMap<>();
    return configureColumnsNATTable();
  }

  /**
   * Configure columns NAT table.
   *
   * @param columnWidthMap the column width map
   */
  private Map<Integer, Integer> configureColumnsNATTable() {
    this.propertyToLabelMap.put(COLUMN_NUM_BASELINE_NAME, "Name");
    this.propertyToLabelMap.put(COLUMN_NUM_BASELINE_REMARKS, "Remarks");
    this.propertyToLabelMap.put(COLUMN_NUM_BASELINE_PIDC_VARIANT, "Variant");
    this.propertyToLabelMap.put(COLUMN_NUM_BASELINE_CREATED_ON, "Created On");
    this.propertyToLabelMap.put(COLUMN_NUM_BASELINE_ASSESSMENT_TYPE, "Type of Assessment");
    this.propertyToLabelMap.put(COLUMN_NUM_BASELINE_OPEN_ARCHIEVED_FILE, "Open Archieved File");
    this.propertyToLabelMap.put(COLUMN_NUM_BASELINE_ARCHIEVED_FILE_STATUS, "File Generation Status");
    this.propertyToLabelMap.put(COLUMN_NUM_BASELINE_ARCHIEVE_IN_SHAREPOINT, "Archive in SharePoint");

    this.columnWidthMap.put(COLUMN_NUM_BASELINE_NAME, 35);
    this.columnWidthMap.put(COLUMN_NUM_BASELINE_REMARKS, 50);
    this.columnWidthMap.put(COLUMN_NUM_BASELINE_PIDC_VARIANT, 20);
    this.columnWidthMap.put(COLUMN_NUM_BASELINE_CREATED_ON, 35);
    this.columnWidthMap.put(COLUMN_NUM_BASELINE_ASSESSMENT_TYPE, 25);
    this.columnWidthMap.put(COLUMN_NUM_BASELINE_OPEN_ARCHIEVED_FILE, 40);
    this.columnWidthMap.put(COLUMN_NUM_BASELINE_ARCHIEVED_FILE_STATUS, 25);
    this.columnWidthMap.put(COLUMN_NUM_BASELINE_ARCHIEVE_IN_SHAREPOINT, 20);


    return this.columnWidthMap;
  }

  /**
   * custom comparator
   *
   * @param columnNum
   * @return
   */
  private Comparator<DaDataAssessment> getCompareDaBaselinesComparator(final int columnNum) {
    return (final DaDataAssessment cmpRowObj1, final DaDataAssessment cmpRowObj2) -> {
      int ret = 0;
      switch (columnNum) {
        case COLUMN_NUM_BASELINE_NAME:
          ret = ApicUtil.compare(cmpRowObj1.getBaselineName(), cmpRowObj2.getBaselineName());
          break;
        case COLUMN_NUM_BASELINE_REMARKS:
          ret = ApicUtil.compare(cmpRowObj1.getDescription(), cmpRowObj2.getDescription());
          break;
        case COLUMN_NUM_BASELINE_PIDC_VARIANT:
          ret = ApicUtil.compare(cmpRowObj1.getVariantName(), cmpRowObj2.getVariantName());
          break;
        case COLUMN_NUM_BASELINE_CREATED_ON:
          ret = ApicUtil.compare(cmpRowObj2.getCreatedDate(), cmpRowObj1.getCreatedDate());
          break;
        case COLUMN_NUM_BASELINE_ASSESSMENT_TYPE:
          ret = ApicUtil.compare(
              DataAssmntReportDataHandler.getTypeOfAssessmentDisplayText(cmpRowObj1.getTypeOfAssignment()),
              DataAssmntReportDataHandler.getTypeOfAssessmentDisplayText(cmpRowObj2.getTypeOfAssignment()));
          break;
        case COLUMN_NUM_BASELINE_OPEN_ARCHIEVED_FILE:
          ret = ApicUtil.compare(DataAssmntReportDataHandler.getFileName(cmpRowObj1),
              DataAssmntReportDataHandler.getFileName(cmpRowObj2));
          break;
        case COLUMN_NUM_BASELINE_ARCHIEVED_FILE_STATUS:
          ret = ApicUtil.compare(CDRConstants.FILE_ARCHIVAL_STATUS.getTypeByDbCode(cmpRowObj1.getFileArchivalStatus()),
              CDRConstants.FILE_ARCHIVAL_STATUS.getTypeByDbCode(cmpRowObj2.getFileArchivalStatus()));
          break;
        default:
          break;
      }
      return ret;
    };
  }

  /**
   * custom comparator registry
   *
   * @param columnHeaderDataLayer
   * @return
   */
  private IConfiguration getCustomComparatorConfiguration(final AbstractLayer columnHeaderDataLayer) {
    return new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Add label accumulator
        ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
        columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);
        for (int i = 0; i < (DataAssessmentBaselinesPage.this.propertyToLabelMap.size()); i++) {
          labelAccumulator.registerColumnOverrides(i, CUSTOM_COMPARATOR_LABEL + i);
          configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
              getCompareDaBaselinesComparator(i), NORMAL, CUSTOM_COMPARATOR_LABEL + i);
        }
      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart arg0, final ISelection arg1) {
    // unimplemented method
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return null;
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
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.dataAssmntReportDataHandler;
  }

  /**
   * Load saved state of NAT table.
   */
  private void loadState() {
    try {
      this.natTable.loadState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to load Data assessment baselines nat table state", ioe,
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
      CDMLogger.getInstance().warn("Failed to save Data assessment baselines nat table state", ioe,
          Activator.PLUGIN_ID);
    }
  }

  /**
   * Enables tootltip only for cells which contain not fully visible content.
   */
  private void attachToolTip() {
    DefaultToolTip toolTip = new ExampleNatTableToolTip(this.natTable);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));
  }

  /**
   * The Class ExampleNatTableToolTip.
   */
  private class ExampleNatTableToolTip extends DefaultToolTip {

    /** The nat table. */
    private final NatTable natTable;

    /**
     * Instantiates a new example nat table tool tip.
     *
     * @param natTable the nat table
     */
    public ExampleNatTableToolTip(final NatTable natTable) {
      super(natTable, ToolTip.NO_RECREATE, false);
      this.natTable = natTable;
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
      int col = this.natTable.getColumnPositionByX(event.x);
      int row = this.natTable.getRowPositionByY(event.y);

      return new Point(col, row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getText(final Event event) {
      int col = this.natTable.getColumnPositionByX(event.x);
      int row = this.natTable.getRowPositionByY(event.y);
      ILayerCell cellByPosition = this.natTable.getCellByPosition(col, row);
      return (String) cellByPosition.getDataValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldCreateToolTip(final Event event) {
      int col = this.natTable.getColumnPositionByX(event.x);
      int row = this.natTable.getRowPositionByY(event.y);
      ILayerCell cellByPosition = this.natTable.getCellByPosition(col, row);
      if ((cellByPosition == null) || (cellByPosition.getDataValue() == null) ||
          !(cellByPosition.getDataValue() instanceof String)) {
        return false;
      }
      String cellValue = (String) cellByPosition.getDataValue();
      if ((cellValue == null) || cellValue.isEmpty()) {
        return false;
      }
      Rectangle currentBounds = cellByPosition.getBounds();
      cellByPosition.getLayer().getPreferredWidth();

      GC gcObj = new GC(this.natTable);
      Point size = gcObj.stringExtent(cellValue);
      return (currentBounds.width < size.x);
    }
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
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMsgAndStatHdr();
  }

  /**
   * input for status line
   */
  public void setStatusBarMsgAndStatHdr() {

    int totalItemCount = this.dataAssmntReportDataHandler.getDataAssessmentReport().getDataAssmntBaselines().size();
    int filteredItemCount = this.baselinesFilterGridLayer.getRowHeaderLayer().getPreferredRowCount();
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

}
