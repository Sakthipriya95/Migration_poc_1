/**
 *
 */
package com.bosch.caltool.apic.ui.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCHistoryActionSet;
import com.bosch.caltool.apic.ui.sorter.PIDCHistoryTableSorter;
import com.bosch.caltool.apic.ui.table.filters.PIDCHistoryFilters;
import com.bosch.caltool.apic.ui.table.filters.PIDCHistoryToolBarFilters;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.apic.ui.views.providers.PIDCHistoryTableLabelProvider;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.views.AbstractViewPart;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.DateUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsForVersType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcChangeHistoryServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseSectionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseServiceClient;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * The view part of the PIDC History page. <br>
 *
 * @author dmo5cob
 */
public class PIDCHistoryViewPart extends AbstractViewPart {

  /**
   * PIDCard version instance
   */
  private PidcVersion pidcVer;
  /**
   * Text instance for tableviewer filter
   */
  private Text filterTxt;
  /**
   * FormToolkit instance
   */
  private FormToolkit toolkit;
  /**
   * GridTableViewer instance
   */
  private GridTableViewer historyTableViewer;
  /**
   * PIDCHistoryFilters instance
   */
  private PIDCHistoryFilters pidcHistoryFilter;

  /**
   * Section instance
   */
  private Section section;
  /**
   * The selected pidc attribute
   */
  private IProjectAttribute selectedPidcAttr;
  /**
   * Defines PIDC details variant tree node is selected or not
   */
  private boolean isVarNodeSelected;
  /**
   * Defines PIDC details sub-variant tree node is selected or not
   */
  // ICDM-121
  private boolean isSubVarNodeSelected;
  /**
   * PIDCHistoryToolBarFilters instance
   */
  private PIDCHistoryToolBarFilters pidcHistoryToolBarFilter;
  /**
   * Selected pidc variant
   */
  private PidcVariant selectedPIDCVariant;
  /**
   * Selected pidc subvariant
   */
  private PidcSubVariant selectedPIDCSubVariant;
  /**
   * PIDCHistoryTableSorter instance
   */
  private final PIDCHistoryTableSorter histryTableSorter = new PIDCHistoryTableSorter();
  /**
   * PIDCHistoryTableLabelProvider instance
   */
  private PIDCHistoryTableLabelProvider pidcHistoryTableLabelProvider;
  /**
   * col width
   */
  private static final int COL_WIDTH_PIDC_VERSION = 80;
  /**
   * col width
   */
  private static final int COL_WIDTH_CHANGE_NO = 35;
  /**
   * col width
   */
  private static final int COL_WIDTH = 150;
  /**
   * col width
   */
  private static final int COL_WIDTH_1 = 250;

  // iCDM-2614
  /**
   * the pidc action set
   */
  private PIDCHistoryActionSet pidcActionSet;
  private Form nonScrollableForm;

  /**
   * key - usecase id, value - UseCase
   */
  private Map<Long, UseCase> useCaseDetails = new HashMap<>();
  /**
   * key - useCaseSection id, value - UseCaseSection
   */
  private Map<Long, UseCaseSection> useCaseSecDetails = new HashMap<>();

  /**
   * @return the selectedPidcAttr
   */
  public IProjectAttribute getSelectedPidcAttr() {
    return this.selectedPidcAttr;
  }

  /**
   * @param selectedPidcAttr the selectedPidcAttr to set
   */
  public void setSelectedPidcAttr(final IProjectAttribute selectedPidcAttr) {
    this.selectedPidcAttr = selectedPidcAttr;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    addHelpAction();
    this.toolkit = new FormToolkit(parent.getDisplay());

    this.nonScrollableForm = this.toolkit.createForm(parent);

    GridLayout gridLayout = new GridLayout();
    this.nonScrollableForm.getBody().setLayout(gridLayout); // Set grid
    // layout data to scrolledForm
    this.nonScrollableForm.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());


    this.section = createSection(ApicUiConstants.PIDC_HISTORY_TITLE, false);
    // Create composite
    final Composite historyComp = this.toolkit.createComposite(this.section);
    historyComp.setLayout(new GridLayout());
    this.section.setClient(historyComp);
    // Get the secondary id of the view
    String secondaryId = getViewSite().getSecondaryId();
    // If the secondary id is null that means that Eclipse opened the view, and not us
    if (secondaryId == null) {
      Label label = new Label(historyComp, SWT.None); // new up a Label widget
      label.setText("There is no relevant part active!");
    }
    else {
      // Build the UI
      fillUseCaseMap();
      createUIControls(historyComp);
      createToolBar();
    }
  }


  /**
   * Creates an actions on view tool bar
   */
  private void createToolBar() {

    final IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
    Separator separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    this.pidcActionSet = new PIDCHistoryActionSet();
    this.pidcActionSet.createAttrsSyncAction(mgr, this, this.pidcHistoryToolBarFilter);
    this.pidcActionSet.createLevelsSyncAction(mgr, this, this.pidcHistoryToolBarFilter);
    this.pidcActionSet.createRefreshAction(mgr, this);
    mgr.add(separator);

    // Add predefined filters to section toolbar
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = toolBarManager.createControl(this.section);
    this.pidcActionSet.createPIDCHistoryChangesAction(toolBarManager, this, this.pidcHistoryToolBarFilter);
    this.pidcActionSet.createPIDCAttrsHistoryChangesAction(toolBarManager, this, this.pidcHistoryToolBarFilter);

    // iCDM-2614
    this.pidcActionSet.createFocusMatrixHistoryChangesAction(toolBarManager, this, this.pidcHistoryToolBarFilter);

    toolBarManager.update(true);
    this.section.setTextClient(toolbar);
  }

  // iCDM-2614
  /**
   * Enables/Disables the pidc and Attr changes based on the input boolean flag
   *
   * @param toBeEnabled toBeEnabled
   */
  public void pidcAndAttrChangesFlag(final boolean toBeEnabled) {
    this.pidcHistoryToolBarFilter.setAttrChangesFlag(toBeEnabled);
    this.pidcActionSet.getPidcAttrsChangesAction().setChecked(toBeEnabled);
    this.pidcHistoryToolBarFilter.setPidcChangesFlag(toBeEnabled);
    this.pidcActionSet.getPidcChangesAction().setChecked(toBeEnabled);
    this.historyTableViewer.refresh();
  }

  /**
   * @return the section
   */
  public Section getSection() {
    return this.section;
  }

  /**
   * This method creates section
   *
   * @param sectionName defines section name
   * @param descControlEnable defines description control enable or not
   * @return Section instance
   */
  private Section createSection(final String sectionName, final boolean descControlEnable) {
    return SectionUtil.getInstance().createSection(this.nonScrollableForm.getBody(), this.toolkit,
        GridDataUtil.getInstance().getGridData(), sectionName, descControlEnable);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    this.nonScrollableForm.setFocus();
    if (null != this.historyTableViewer) {
      this.historyTableViewer.getControl().setFocus();
    }
  }

  /**
   * This method initializes composite
   */
  private void createUIControls(final Composite composite) {
    createFilterTxt(composite);
    createPIDCHistoryTabViewer(composite);
    setTableViewerSorter();
    setTabViewerProviders();
    addFilters();
  }

  /**
   * set comparator
   */
  private void setTableViewerSorter() {
    // set comparatorr
    this.historyTableViewer.setComparator(this.histryTableSorter);
  }

  /**
   * @param title instance
   */
  public void setPartTitle(final String title) {
    super.setPartName(title);
  }

  /**
   * This method adds the filter instance
   */
  private void addFilters() {
    this.pidcHistoryFilter = new PIDCHistoryFilters();
    this.historyTableViewer.addFilter(this.pidcHistoryFilter);


    this.pidcHistoryToolBarFilter = new PIDCHistoryToolBarFilters(this);
    this.historyTableViewer.addFilter(this.pidcHistoryToolBarFilter);
  }

  /**
   * This method sets ContentProvider & LabelProvider to the TableViewer
   */
  private void setTabViewerProviders() {
    this.historyTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.pidcHistoryTableLabelProvider = new PIDCHistoryTableLabelProvider();
    this.historyTableViewer.setLabelProvider(this.pidcHistoryTableLabelProvider);
    this.pidcHistoryTableLabelProvider.setUseCaseDetails(this.useCaseDetails);
    this.pidcHistoryTableLabelProvider.setUseCaseSectionDetails(this.useCaseSecDetails);
  }

  /**
   * This method creates the the TableViewer
   *
   * @param gridData
   */
  private void createPIDCHistoryTabViewer(final Composite comp) {
    this.historyTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(comp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, GridDataUtil.getInstance().getGridData());
    // Create GridViewerColumns
    createGridViewerColumns();
  }

  /**
   * This method adds Columns to the PIDCHistoryTabViewer
   */
  private void createGridViewerColumns() {
    createNumberColumn();
    createPIDCVersionColumn();
    createLevelColumn();
    createAttrNameColumn();
    createChangedItemColumn();
    createOldValueColumn();
    createNewValueColumn();
    createModifiedByColumn();
    createModifiedOnColumn();
  }

  /**
   * version col
   */
  private void createPIDCVersionColumn() {
    GridViewerColumn versionCol = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.historyTableViewer,
        "PIDC Version", COL_WIDTH_PIDC_VERSION);

    // Add selection listener
    versionCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        versionCol.getColumn(), CommonUIConstants.COLUMN_INDEX_1, this.histryTableSorter, this.historyTableViewer));

  }

  /**
   * This method adds number column to the PIDCHistoryTabViewer
   */
  private void createNumberColumn() {
    GridViewerColumn noColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.historyTableViewer,
        "Change No.", COL_WIDTH_CHANGE_NO);

    // Add selection listener
    noColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        noColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_0, this.histryTableSorter, this.historyTableViewer));

  }

  /**
   * This method adds level column to the PIDCHistoryTabViewer
   */
  private void createLevelColumn() {
    GridViewerColumn levelCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.historyTableViewer, "Level", COL_WIDTH);

    // Add selection listener
    levelCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        levelCol.getColumn(), CommonUIConstants.COLUMN_INDEX_2, this.histryTableSorter, this.historyTableViewer));

  }

  /**
   * This method adds user name column to the PIDCHistoryTabViewer
   */
  private void createAttrNameColumn() {
    GridViewerColumn attrName =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.historyTableViewer, "Attribute", COL_WIDTH);
    // Add selection listener
    attrName.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        attrName.getColumn(), CommonUIConstants.COLUMN_INDEX_3, this.histryTableSorter, this.historyTableViewer));

  }

  /**
   * This method adds ChangedItem column to the PIDCHistoryTabViewer
   */
  private void createChangedItemColumn() {
    GridViewerColumn changeItemCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.historyTableViewer, "Changed Item", COL_WIDTH);
    // Add selection listener
    changeItemCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        changeItemCol.getColumn(), CommonUIConstants.COLUMN_INDEX_4, this.histryTableSorter, this.historyTableViewer));


  }

  /**
   * This method adds old value column to the PIDCHistoryTabViewer
   */
  private void createOldValueColumn() {
    GridViewerColumn oldValueCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.historyTableViewer, "Old Value", COL_WIDTH);
    // Add selection listener
    oldValueCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        oldValueCol.getColumn(), CommonUIConstants.COLUMN_INDEX_5, this.histryTableSorter, this.historyTableViewer));

  }

  /**
   * This method adds new value column to the PIDCHistoryTabViewer
   */
  private void createNewValueColumn() {
    GridViewerColumn newValueCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.historyTableViewer, "New Value", COL_WIDTH);
    // Add selection listener
    newValueCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        newValueCol.getColumn(), CommonUIConstants.COLUMN_INDEX_6, this.histryTableSorter, this.historyTableViewer));


  }

  /**
   * This method adds modified by column to the PIDCHistoryTabViewer
   */
  private void createModifiedByColumn() {
    GridViewerColumn modByCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.historyTableViewer, "Modified By", COL_WIDTH_1);
    // Add selection listener
    modByCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        modByCol.getColumn(), CommonUIConstants.COLUMN_INDEX_7, this.histryTableSorter, this.historyTableViewer));

  }

  /**
   * This method adds modified on column to the PIDCHistoryTabViewer
   */
  private void createModifiedOnColumn() {
    GridViewerColumn modOnCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.historyTableViewer, "Modified On", COL_WIDTH);
    // Add selection listener
    modOnCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        modOnCol.getColumn(), CommonUIConstants.COLUMN_INDEX_8, this.histryTableSorter, this.historyTableViewer));

  }


  /**
   * This method creates filter text
   *
   * @param composite
   */
  private void createFilterTxt(final Composite composite) {
    this.filterTxt = TextUtil.getInstance().createFilterText(this.toolkit, composite,
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void modifyText(final ModifyEvent event) {
        final String text = PIDCHistoryViewPart.this.filterTxt.getText().trim();
        PIDCHistoryViewPart.this.pidcHistoryFilter.setFilterText(text);
        PIDCHistoryViewPart.this.historyTableViewer.refresh();
      }
    });
    this.filterTxt.setFocus();
  }

  /**
   * @return the pidCard version
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVer;
  }

  /**
   * @param pidcVer the pidCard version to set
   */
  public void setPidCard(final PidcVersion pidcVer) {
    this.pidcVer = pidcVer;
  }

  /**
   * @return the historyTableViewer
   */
  public GridTableViewer getHistoryTableViewer() {
    return this.historyTableViewer;
  }

  /**
   * @return the isVaraintNodeSelected
   */
  public boolean isVaraintNodeSelected() {
    return this.isVarNodeSelected;
  }

  /**
   * @param isVaraintNodeSelected the isVaraintNodeSelected to set
   */
  public void setVaraintNodeSelected(final boolean isVaraintNodeSelected) {
    this.isVarNodeSelected = isVaraintNodeSelected;
  }

  /**
   * @return the isSubVaraintNodeSelected
   */
  public boolean isSubVaraintNodeSelected() {
    return this.isSubVarNodeSelected;
  }

  /**
   * @param isSubVaraintNodeSelected the isSubVaraintNodeSelected to set
   */
  public void setSubVaraintNodeSelected(final boolean isSubVaraintNodeSelected) {
    this.isSubVarNodeSelected = isSubVaraintNodeSelected;
  }

  /**
   * @param selectedPIDCVariant instance of PIDCVariant
   */
  public void setSelectedPIDCVariant(final PidcVariant selectedPIDCVariant) {
    this.selectedPIDCVariant = selectedPIDCVariant;
  }

  /**
   * @return PIDCVariant instance
   */
  public PidcVariant getSelectedPIDCVariant() {
    return this.selectedPIDCVariant;
  }

  /**
   * @return the selectedPIDCSubVariant
   */
  public PidcSubVariant getSelectedPIDCSubVariant() {
    return this.selectedPIDCSubVariant;
  }

  /**
   * @param selectedPIDCSubVariant the selectedPIDCSubVariant to set
   */
  public void setSelectedPIDCSubVariant(final PidcSubVariant selectedPIDCSubVariant) {
    this.selectedPIDCSubVariant = selectedPIDCSubVariant;
  }


  /**
   * set the title description
   */
  public void setTitleDescription() {
    boolean syncAttrsActionFlag = false;
    boolean syncLevelsActionFlag = false;
    IContributionItem[] items = getViewSite().getActionBars().getToolBarManager().getItems();
    for (IContributionItem iContributionItem : items) {
      if (iContributionItem instanceof ActionContributionItem) {
        ActionContributionItem actionItem = (ActionContributionItem) iContributionItem;
        IAction action = actionItem.getAction();
        if (action.isChecked() && action.getId().equals(ApicUiConstants.SYNCHRONIZE_ATTRIBUTE)) {
          syncAttrsActionFlag = true;
        }
        else if (action.isChecked() && action.getId().equals(ApicUiConstants.SYNCHRONIZE_PIDC_LEVELS)) {
          syncLevelsActionFlag = true;
        }
      }
    }
    StringBuilder descripMsg = new StringBuilder();
    descripMsg.append(ApicUiConstants.CHANGE_HISTORY_FOR_PIDC).append(" : ").append(getPidcVersion().getName());

    if ((null != getSelectedPidcAttr()) && syncAttrsActionFlag) {
      descripMsg = getDescriptionForVarNodes(syncLevelsActionFlag, descripMsg);
      descripMsg.append(" , Attribute").append(" : ").append(getSelectedPidcAttr().getName());
    }
    else {
      descripMsg = getDescriptionForVarNodes(syncLevelsActionFlag, descripMsg);
    }
    getSection().setText(descripMsg.toString());
  }

  /**
   * @param syncLevelsActionFlag
   * @param despMsg
   */
  private StringBuilder getDescriptionForVarNodes(final boolean syncLevelsActionFlag, final StringBuilder despMsg) {
    if (this.isVarNodeSelected && syncLevelsActionFlag) {
      despMsg.append(" , Variant").append(" : ").append(getSelectedPIDCVariant().getName());
    }
    else if (this.isSubVarNodeSelected && syncLevelsActionFlag) {
      despMsg.append(" , Sub-Variant").append(" : ").append(getSelectedPIDCSubVariant().getName());
    }
    return despMsg;
  }

  /**
   * populate history table
   */
  public void populateHistoryTable() {
    this.pidcHistoryTableLabelProvider.setPidcVrsn(this.pidcVer);
    List<com.bosch.caltool.icdm.model.apic.pidc.AttrDiffType> differences = invokeApicWsClient();
    if (null == differences) {
      getHistoryTableViewer().setInput("");
    }
    else {
      getSection().setDescription("Last refresh of history : " +
          ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_08, DateUtil.getCurrentTime()));
      getHistoryTableViewer().setInput(differences);
    }
  }

  /**
   * Fetch the records via webservice call and fill the useCaseDetails and useCaseSecDetails Map
   */
  private void fillUseCaseMap() {
    UseCaseServiceClient servClient = new UseCaseServiceClient();
    UseCaseSectionServiceClient secServClient = new UseCaseSectionServiceClient();

    try {
      this.useCaseDetails = servClient.getAll();
      this.useCaseSecDetails = secServClient.getAll();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  /**
   * @return AttrDiffType
   */
  public List<com.bosch.caltool.icdm.model.apic.pidc.AttrDiffType> invokeApicWsClient() {

    List<com.bosch.caltool.icdm.model.apic.pidc.AttrDiffType> differences = null;

    // Icdm-1451- Client changes for the Web service to pass language.

    String language = new CommonDataBO().getLanguage().getText();
    try {
      PidcChangeHistoryServiceClient changeHistoryClient = new PidcChangeHistoryServiceClient();
      PidcVersionServiceClient pidcServc = new PidcVersionServiceClient();
      PidcVersion pidcVersion = pidcServc.getById(getPidcVersion().getId());
      PidcDiffsForVersType pidcDiffsForVers = new PidcDiffsForVersType();
      pidcDiffsForVers.setPidcId(pidcVersion.getPidcId());
      pidcDiffsForVers.setPidcVersionId(pidcVersion.getId());
      pidcDiffsForVers.setOldPidcChangeNumber(0L);
      pidcDiffsForVers.setNewPidcChangeNumber(pidcVersion.getVersion());
      pidcDiffsForVers.setLanguage(language);
      differences = changeHistoryClient.getPidcAttrDiffForVersion(pidcDiffsForVers);
    }
    catch (Exception e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return differences;
  }

}
