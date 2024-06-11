/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.pages.QuestionnaireDetailsSection;
import com.bosch.caltool.cdr.ui.sorters.QuestionnaireNameTableSorter;
import com.bosch.caltool.cdr.ui.table.filters.QuestionnaireNameTableFilters;
import com.bosch.caltool.cdr.ui.views.providers.QuestionnaireNameTableLabelProvider;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.model.wp.WorkPkg;
import com.bosch.caltool.icdm.model.wp.WorkPkgInput;
import com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivisionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionAttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.QuestionnaireServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireResponseServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * The Class QuestionnaireNameSelDialog.
 *
 * @author bru2cob
 */
public class QuestionnaireNameSelDialog extends AbstractDialog {

  /** Composite instance. */
  private Composite composite;

  /** Composite instance. */
  private Composite top;

  /** FormToolkit instance. */
  private FormToolkit formToolkit;

  /** Section instance. */
  private Section section;

  /** Form instance. */
  private Form form;

  /** Filter text instance. */
  private Text filterTxt;

  /** Filter instance. */
  private QuestionnaireNameTableFilters filters;

  /** GridTableViewer instance for selection of variant or workpackage. */
  private GridTableViewer wrkPkgTableViewer;

  /** instance for Columns sortting. */
  private QuestionnaireNameTableSorter tabSorter;

  /** Constant string. */
  private static final String SELECT_WORK_PACKAGE = "Select Work Package";

  /** selected attr value. */
  private AttributeValue selectedAttrVal;

  /** Selected icdm wp. */
  private WorkPkg selectedWp;

  /** Instance of questionnaireDetailsSection. */
  private final QuestionnaireDetailsSection questionnaireDetailsSection;

  /** sorted set of icdm wp's. */
  private final SortedSet<WorkPkg> inputWps = new TreeSet<>();

  /** The work pkg resp. */
  private Map<Long, String> workPkgResp = new ConcurrentHashMap<>();

  /** The all workpackages. */
  private Map<Long, WorkPkg> allWorkpackages;

  /** The all qnaire set. */
  private TreeSet<Questionnaire> allQnaireSet;

  private final List<Long> availableWorkpackageIdList = new ArrayList<>();


  private final DefineQuestionnaireDialog defineQuestionnaireDialog;

  /** The Div related workpackages. */
  private final Map<Long, WorkPkg> divWpMap = new ConcurrentHashMap<>();


  /**
   * Gets the work pkg resp.
   *
   * @return the workPkgResp
   */
  public Map<Long, String> getWorkPkgResp() {
    return this.workPkgResp;
  }


  /**
   * Instantiates a new questionnaire name sel dialog.
   *
   * @param parentShell the parent shell
   * @param questionnaireDetailsSection the questionnaire details section
   * @param selectedAttrVal the selected attr val
   * @param defineQuestionnaireDialog define qnaire dialog to add qnaire response
   */
  public QuestionnaireNameSelDialog(final Shell parentShell,
      final QuestionnaireDetailsSection questionnaireDetailsSection, final AttributeValue selectedAttrVal,
      final DefineQuestionnaireDialog defineQuestionnaireDialog) {
    super(parentShell);
    this.selectedAttrVal = selectedAttrVal;
    this.questionnaireDetailsSection = questionnaireDetailsSection;
    this.defineQuestionnaireDialog = defineQuestionnaireDialog;
    getAllWorkpackage();
  }

  /**
   * Sets the Dialog Resizable.
   *
   * @param newShellStyle the new shell style
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM | SWT.MAX);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }


  /**
   * Creates the dialog's contents.
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);

    // Set the title
    setTitle("List of Work Packages");

    // Set the message
    setMessage("For these workpackages questionnaires can be defined. Please choose one.",
        IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(SELECT_WORK_PACKAGE);
    super.configureShell(newShell);
    super.setHelpAvailable(true);
  }

  /**
   * Creates the gray area.
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    // create composite
    createComposite();
    return this.top;
  }

  /**
   * This method initializes composite.
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    // create section
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes formToolkit.
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * This method initializes section.
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "List of Work Packages");
    // create form
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form.
   */
  private void createForm() {

    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(new GridLayout());

    // Invokde GridColumnViewer sorter
    this.tabSorter = new QuestionnaireNameTableSorter(this);

    // Add filters to the TableViewer
    this.filters = new QuestionnaireNameTableFilters(this);

    // Create Filter text
    createFilterTxt();


    // Create new users grid tableviewer
    createVariantWorkpackageGridTabViewer();

    // set filter
    this.wrkPkgTableViewer.addFilter(this.filters);
    // set table sorter
    this.wrkPkgTableViewer.setComparator(this.tabSorter);


    // Set ContentProvider and LabelProvider to addNewUserTableViewer
    this.wrkPkgTableViewer.setContentProvider(ArrayContentProvider.getInstance());


    // Set input to the addNewUserTableViewer
    setTabViewerInput();


    // Add selection listener to the addNewUserTableViewer
    addTableSelectionListener();


    // Adds double click selection listener to the variantTableViewer
    addDoubleClickListener();

    // set form layout
    this.form.getBody().setLayout(new GridLayout());

  }


  /**
   * Gets the work pkg map for div.
   *
   * @param divValId divValId
   * @param wps the wps
   * @return the map of work package and value as resp name
   */
  protected Map<Long, String> getWorkPkgMapResourceForDiv(final Long divValId, final Map<Long, WorkPkg> wps) {
    Map<Long, String> workPkgRspMap = new ConcurrentHashMap<>();
    Set<WorkPackageDivision> wpDiv = getWorkpackageDivision(divValId);
    for (WorkPackageDivision dbWpDiv : wpDiv) {
      long wpId = dbWpDiv.getWpId();
      if ((wps != null) && wps.containsKey(wpId)) {
        workPkgRspMap.put(wps.get(wpId).getId(), dbWpDiv.getWpResource() != null ? dbWpDiv.getWpResource() : "");
      }
    }
    return workPkgRspMap;
  }

  // ICDM-2646
  /**
   * Gets the icdm work package map for div.
   *
   * @param divValId the div val id
   * @return Map of Workpackages for the division
   */
  public Map<Long, WorkPkg> getIcdmWorkPackageMapForDiv(final Long divValId) {
    // Key - WpId ; Value - WorkPackage
    ConcurrentMap<Long, WorkPkg> icdmWorkPackageMapForDiv = new ConcurrentHashMap<>();
    Set<WorkPackageDivision> wpDivSet = getWorkpackageDivision(divValId);

    // Review 237201
    for (WorkPackageDivision dbWpDiv : wpDivSet) {
      long wpId = dbWpDiv.getWpId();
      if (this.allWorkpackages.containsKey(wpId) &&
          (!CommonUtilConstants.CODE_YES.equals(dbWpDiv.getDeleted()) && !this.allWorkpackages.get(wpId).isDeleted())) {
        icdmWorkPackageMapForDiv.put(wpId, this.allWorkpackages.get(wpId));
        this.divWpMap.put(dbWpDiv.getId(), this.allWorkpackages.get(wpId));
      }
    }
    return icdmWorkPackageMapForDiv;
  }

  /**
   * Gets the all workpackage.
   *
   * @return the all workpackage
   */
  private void getAllWorkpackage() {
    try {
      Set<WorkPkg> temp = new WorkPackageServiceClient().findAll();
      this.allWorkpackages = temp.stream().collect(Collectors.toMap(WorkPkg::getId, wp -> wp));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Gets the workpackage division.
   *
   * @param divId the div id
   * @return the workpackage division
   */
  private Set<WorkPackageDivision> getWorkpackageDivision(final long divId) {
    try {
      return new WorkPackageDivisionServiceClient().getWPDivisionsByByDivID(divId, false);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return new HashSet<>();
  }


  /**
   * This method defines the activities to be performed when double clicked on the table.
   */
  private void addDoubleClickListener() {
    this.wrkPkgTableViewer.addDoubleClickListener(doubleclickevent -> Display.getDefault().asyncExec(this::okPressed));
  }

  /**
   * This method sets the input to the addNewUserTableViewer.
   */
  private void setTabViewerInput() {
    try {
      if (null != this.defineQuestionnaireDialog) {
        /** All qnaire with atleast one question set. */
        TreeSet<Questionnaire> qnaireWithQuestionSet =
            new TreeSet<>(new QuestionnaireServiceClient().getAll(false, false).values());
        PidcVersionAttribute divisionAttribute = new PidcVersionAttributeServiceClient()
            .getQnaireConfigAttribute(this.defineQuestionnaireDialog.getPidcVersion().getId());
        this.defineQuestionnaireDialog.setDivId(divisionAttribute.getValueId());
        this.selectedAttrVal = new AttributeValueServiceClient().getById(divisionAttribute.getValueId());
        // Calling the method to load divWpMap
        getIcdmWorkPackageMapForDiv(divisionAttribute.getValueId());
        HashSet<WorkPkg> existingWpWithQnaire = getExistingWpWithQnaire(qnaireWithQuestionSet);
        List<Long> availableWpId = fetchAvailableQnaireforWpResp();
        existingWpWithQnaire.removeIf(workPkg -> availableWpId.contains(workPkg.getId()));
        this.inputWps.addAll(existingWpWithQnaire);
      }
      else {
        // when used in name sel dialog , display wp's not mapped to that division
        // ICDM-2646
        SortedSet<WorkPkg> wpSet = new TreeSet<>(
            getIcdmWorkPackageMapForDiv(QuestionnaireNameSelDialog.this.selectedAttrVal.getId()).values());
        getAllQuestionnaires();
        wpSet.removeAll(getExistingWpWithQnaire(this.allQnaireSet));
        this.inputWps.addAll(wpSet);
      }

      WorkPkgInput workPkgInput = new WorkPkgInput();
      workPkgInput.setDivValId(this.selectedAttrVal.getId());
      workPkgInput.setWorkPkgSet(this.inputWps);
      this.workPkgResp = new CDRHandler().getWorkPackageResponseMap(workPkgInput);
      // set table viewer input
      this.wrkPkgTableViewer.setInput(this.inputWps);

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param wpSet
   * @param allQnaireSet
   * @return
   */
  private HashSet<WorkPkg> getExistingWpWithQnaire(final TreeSet<Questionnaire> qnaireSet) {
    HashSet<WorkPkg> existngWpList = new HashSet<>();

    for (Questionnaire quest : qnaireSet) {
      if (quest.getDivName().equals(QuestionnaireNameSelDialog.this.selectedAttrVal.getName()) &&
          this.divWpMap.containsKey(quest.getWpDivId())) {
        existngWpList.add(this.divWpMap.get(quest.getWpDivId()));
      }
    }

    return existngWpList;
  }

  /**
   * Gets the all questionnaires.
   *
   * @return the all questionnaires
   */
  private void getAllQuestionnaires() {
    try {
      this.allQnaireSet = new TreeSet<>(new QuestionnaireServiceClient().getAll(false, true).values());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * This method adds selection listener to the addNewUserTableViewer.
   */
  private void addTableSelectionListener() {
    this.wrkPkgTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        final IStructuredSelection selection =
            (IStructuredSelection) QuestionnaireNameSelDialog.this.wrkPkgTableViewer.getSelection();
        QuestionnaireNameSelDialog.this.selectedWp = (WorkPkg) selection.getFirstElement();
      }
    });
  }


  /**
   * This method creates filter text.
   */
  private void createFilterTxt() {
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "type filter text");
    this.filterTxt.addModifyListener(event -> {
      String text = QuestionnaireNameSelDialog.this.filterTxt.getText().trim();
      QuestionnaireNameSelDialog.this.filters.setFilterText(text);
      QuestionnaireNameSelDialog.this.wrkPkgTableViewer.refresh();

    });
    this.filterTxt.setFocus();

  }

  /**
   * This method creates the variantTableViewer.
   */
  private void createVariantWorkpackageGridTabViewer() {
    if (null == this.questionnaireDetailsSection) {
      this.wrkPkgTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
          SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
          GridDataUtil.getInstance().getHeightHintGridData(200));
    }
    else {
      this.wrkPkgTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
          SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
          GridDataUtil.getInstance().getHeightHintGridData(200));
    }
    // 261043- Resize the Work pkg dialog
    // Create GridViewerColumns
    createGridViewerColumns();
  }


  /**
   * This method adds Columns to the variantTableViewer.
   */
  private void createGridViewerColumns() {
    // create group col
    GridViewerColumn groupColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.wrkPkgTableViewer, "Work Package Resource", 150);
    // Add column selection listener
    groupColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        groupColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_0, this.tabSorter, this.wrkPkgTableViewer));
    groupColumn.setLabelProvider(new QuestionnaireNameTableLabelProvider(ApicUiConstants.COLUMN_INDEX_0, this));

    // create name col
    GridViewerColumn workPkgColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.wrkPkgTableViewer, "Work Package Name", 270);
    // Add column selection listener
    workPkgColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        workPkgColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_1, this.tabSorter, this.wrkPkgTableViewer));
    workPkgColumn.setLabelProvider(new QuestionnaireNameTableLabelProvider(ApicUiConstants.COLUMN_INDEX_1, this));

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    boolean isAvailable = false;
    if (null != this.defineQuestionnaireDialog) {
      isAvailable = createQnaireResponse();
    }
    else {
      this.questionnaireDetailsSection.setEngNameText(this.selectedWp.getWpNameEng());
      this.questionnaireDetailsSection.setSelectedWp(this.selectedWp);
      this.questionnaireDetailsSection.setSelectedAttrVal(this.selectedAttrVal);
      Set<WorkPackageDivision> wpDivSet = getWorkpackageDivision(this.selectedAttrVal.getId());
      for (WorkPackageDivision wpDiv : wpDivSet) {
        if (wpDiv.getWpId().longValue() == this.selectedWp.getId().longValue()) {
          this.questionnaireDetailsSection.setSelectedWpDiv(wpDiv);
        }
      }
    }
    if (!isAvailable) {
      super.okPressed();
    }
  }

  /**
   *
   */
  private boolean createQnaireResponse() {
    final IStructuredSelection selection = (IStructuredSelection) this.wrkPkgTableViewer.getSelection();
    boolean isAvailable = false;
    if (!selection.isEmpty()) {
      SortedSet<RvwQnaireResponse> qnaireRespSet = new TreeSet<>();
      final List<WorkPkg> icdmWps = selection.toList();
      final Map<RvwQnaireResponse, WorkPkg> qnaireRespWpMap = new HashMap<>();
      List<Long> availableWpId = fetchAvailableQnaireforWpResp();
      for (WorkPkg wp : icdmWps) {
        if (availableWpId.contains(wp.getId())) {
          isAvailable = true;
          break;
        }
        final WorkPkg icdmWp = wp;
        RvwQnaireResponse qnaireResp = new RvwQnaireResponse();
        qnaireResp.setName(icdmWp.getName());
        qnaireResp.setPidcVersId(this.defineQuestionnaireDialog.getPidcVersion().getId());
        qnaireResp.setVariantId(this.defineQuestionnaireDialog.getPidcVariant().getId());
        qnaireResp.setA2lRespId(this.defineQuestionnaireDialog.getA2lResp().getId());
        qnaireResp.setA2lWpId(this.defineQuestionnaireDialog.getA2lWp().getId());
        qnaireRespWpMap.put(qnaireResp, wp);
        qnaireRespSet.add(qnaireResp);
      }
      if (!isAvailable) {
        this.defineQuestionnaireDialog.getQnaireRespWpMap().putAll(qnaireRespWpMap);
        this.defineQuestionnaireDialog.getWorkPkgSet().addAll(new TreeSet<>(icdmWps));
        this.defineQuestionnaireDialog.getAllQnaireResponseSet().addAll(qnaireRespSet);
        this.defineQuestionnaireDialog.getqNaireTableViewer()
            .setInput(this.defineQuestionnaireDialog.getAllQnaireResponseSet());
      }
      else {
        CDMLogger.getInstance().errorDialog(
            "Questionnaire response is already available for the selected workpackage(s)", Activator.PLUGIN_ID);
      }
    }
    return isAvailable;
  }


  /**
   * @return
   */
  private List<Long> fetchAvailableQnaireforWpResp() {
    if (this.availableWorkpackageIdList.isEmpty()) {
      try {
        Set<Long> qnaireRespId =
            this.defineQuestionnaireDialog.getQnaireRespMap().get(this.defineQuestionnaireDialog.getSelWpRespName())
                .stream().filter(qnairRespId -> CommonUtils.isNotEqual(qnairRespId, ApicConstants.SIMP_QUES_RESP_ID))
                .collect(Collectors.toSet());
        // QnaireRespId will be empty if there is only Simplified Qnaire
        return CommonUtils.isNotEmpty(qnaireRespId)
            ? new RvwQnaireResponseServiceClient().getWorkpackageId(qnaireRespId) : this.availableWorkpackageIdList;
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
    return this.availableWorkpackageIdList;
  }


}
