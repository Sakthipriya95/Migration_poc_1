/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.editors.pages.A2LFilePage;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.providers.ComboViewerContentPropsalProvider;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.CopyPar2WpFromA2lInput;
import com.bosch.caltool.icdm.model.apic.pidc.PIDCVersionDummy;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTakeOverA2lWrapper;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.SdomPverServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

// ICDM-1438
/**
 * This is the dialog class to map a2l files to a particular pidc version
 *
 * @author bru2cob
 */
public class A2LFileVersionDialog extends AbstractDialog {

  /**
   * Info message for user while selecting a2l file for take over
   */
  private static final String PAR2WP_DESC =
      "Only A2L with workpackage param mapping are displayed for take over from A2L selection";
  /**
   * Value col width
   */
  private static final int VERSION_COL_WIDTH = 200;
  /**
   * Value table height hint
   */
  private static final int VERSION_TAB_HEIGHT_HINT = 200;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Section instance
   */
  private Section verSection;
  /**
   * Section instance
   */
  private Section par2WpSection;
  /**
   * Form instance
   */
  private Form par2WpForm;
  /**
   * Form instance
   */
  private Form verForm;
  /**
   * GridTableViewer instance for add values
   */
  private GridTableViewer versionTabViewer;
  /**
   * Instance of a2l file page
   */
  private final A2LFilePage a2lFilePage;
  /**
   * Constant for dummy ID value
   */
  public static final Long VALUE_ID = 0L;
  /**
   * Key - pver name+pver var + a2l file name Value - pidcA2l
   */
  private final Map<String, PidcA2l> pidcA2lMap = new HashMap<>();
  private PidcA2l sourceA2l;
  private ComboViewer a2lComboViewer;
  private Combo wpDefVerCombo;
  private final Map<String, A2lWpDefnVersion> wpDefVerMap = new HashMap<>();
  private boolean overWriteAllAssignments;
  private boolean canOverWrite;
  /**
   * Add new user button instance
   */
  private Button okBtn;

  private PidcVersion selPidcVersion;
  /**
   * Button to check for deriving WP RESP from functions
   */
  private Button funcDeriveChkBtn;

  /**
   * @param shell
   * @param a2lFilePage
   */
  public A2LFileVersionDialog(final Shell shell, final A2LFilePage a2lFilePage) {
    // calling parent constructor
    super(shell);
    this.a2lFilePage = a2lFilePage;
  }

  /**
   * create contents
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    if (this.a2lFilePage.isMappingsAvailable()) {
      setTitle("Select Source A2L File to Copy Mappings");
    }
    else {
      setTitle("Select PIDC Version");
    }
    // Set the message
    setMessage("Version Name : " + this.a2lFilePage.getPidcVersion().getName(), IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * configure the shell and set the title
   */
  @Override
  protected void configureShell(final Shell newShell) {
    if (this.a2lFilePage.isMappingsAvailable()) {
      newShell.setText("Select Source A2L File");
    }
    else {
      // Set shell name
      newShell.setText("Select Version");
    }
    // calling parent
    super.configureShell(newShell);

  }

  /**
   * Creates the gray area
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    // create composite on parent comp
    createComposite();
    return this.top;
  }


  /**
   * This method initializes composite
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    // create section
    createVersionSection();
    // create par2wp assignment section
    createPar2WpSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   *
   */
  private void createPar2WpSection() {
    this.par2WpSection = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "Par2WP Assignment");
    this.par2WpSection.setDescription(PAR2WP_DESC);
    this.par2WpSection.getDescriptionControl().setEnabled(false);
    // create form
    createPar2WpForm();
    this.par2WpSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.par2WpSection.setClient(this.par2WpForm);

  }

  /**
   * This method initializes form
   */
  private void createPar2WpForm() {
    this.par2WpForm = getFormToolkit().createForm(this.par2WpSection);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    // set the layout
    this.par2WpForm.getBody().setLayout(gridLayout);

    new Label(this.par2WpForm.getBody(), SWT.NONE).setText("Take over from A2L");
    this.a2lComboViewer = new ComboViewer(this.par2WpForm.getBody(), SWT.DROP_DOWN);
    new Label(this.par2WpForm.getBody(), SWT.NONE).setText("Select Source A2L WP Definition Version");

    this.wpDefVerCombo = new Combo(this.par2WpForm.getBody(), SWT.READ_ONLY | SWT.BORDER);
    this.wpDefVerCombo.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.wpDefVerCombo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    fillA2lComboViewer();
    Collection<PidcA2lFileExt> pidcA2lFiles = this.a2lFilePage.getA2lFileMappingTab().getPidcA2lFiles();
    // To sort items in Combo Box
    SortedSet<Object> comboBoxItems = new TreeSet<>();
    for (PidcA2lFileExt pidcA2lExt : pidcA2lFiles) {
      if ((null != pidcA2lExt.getPidcA2l()) && pidcA2lExt.getPidcA2l().isWpParamPresentFlag() &&
          !this.a2lFilePage.getSelA2LFile().contains(pidcA2lExt)) {

        PidcA2l pidcA2l = pidcA2lExt.getPidcA2l();
        String comboVal = pidcA2l.getSdomPverName() + " : " + pidcA2l.getSdomPverVarName() + " : " + pidcA2l.getName();
        comboBoxItems.add(comboVal);
        this.pidcA2lMap.put(comboVal, pidcA2lExt.getPidcA2l());
      }
    }

    new Label(this.par2WpForm.getBody(), SWT.NONE).setText("Derive WP/Resp for new labels from function");
    this.funcDeriveChkBtn = new Button(this.par2WpForm.getBody(), SWT.CHECK);
    this.funcDeriveChkBtn.setToolTipText(CommonUiUtils.getMessage("A2L_COPY", "TOOLTIP_DERIVE_FROM_FUNCTION"));
  }

  /**
   *
   */
  private void fillA2lComboViewer() {
    this.a2lComboViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.a2lComboViewer.addSelectionChangedListener(selectionChangedEvent -> {
      IStructuredSelection selection = (IStructuredSelection) selectionChangedEvent.getSelection();
      String sourceName = (String) selection.getFirstElement();
      if ((null != sourceName) && !sourceName.isEmpty()) {
        populateWPDefVersCombo(sourceName);
      }
      else {
        A2LFileVersionDialog.this.wpDefVerCombo.removeAll();
        A2LFileVersionDialog.this.wpDefVerMap.clear();
      }
    });
    Collection<PidcA2lFileExt> pidcA2lFiles = this.a2lFilePage.getA2lFileMappingTab().getPidcA2lFiles();

    SortedSet<String> comboBoxItems = new TreeSet<>();


    // Key - A2l file name, Value - Count
    Map<String, Integer> a2lFileNameCountMap = new HashMap<>();
    for (PidcA2lFileExt pidcA2lExt : pidcA2lFiles) {
      if ((null != pidcA2lExt.getPidcA2l()) && pidcA2lExt.getPidcA2l().isWpParamPresentFlag() &&
          !this.a2lFilePage.getSelA2LFile().contains(pidcA2lExt)) {

        PidcVersion pidcVesrionData = pidcA2lExt.getPidcVersion();
        PidcA2l pidcA2l = pidcA2lExt.getPidcA2l();
        String comboVal = pidcA2l.getSdomPverName() + " : " + pidcA2l.getSdomPverVarName() + " : " + pidcA2l.getName() +
            " ; " + pidcVesrionData.getVersionName();
        

        if (a2lFileNameCountMap.containsKey(comboVal)) {
          a2lFileNameCountMap.put(comboVal, a2lFileNameCountMap.get(comboVal) + 1);
          // This will execute for the first duplicate value
          if (comboBoxItems.contains(comboVal)) {
            comboBoxItems.remove(comboVal);
            PidcA2l tempPidcA2l = this.pidcA2lMap.remove(comboVal);
            // Rename already added name by appending 1
            String combaValWithCount = comboVal + "(" + 1 + ")";
            comboBoxItems.add(combaValWithCount);
            this.pidcA2lMap.put(combaValWithCount, tempPidcA2l);
          }
          comboVal = comboVal + "(" + a2lFileNameCountMap.get(comboVal) + ")";
        }
        else {
          a2lFileNameCountMap.put(comboVal, 1);
        }
        comboBoxItems.add(comboVal);
        this.pidcA2lMap.put(comboVal, pidcA2l);
      }
    }

    this.a2lComboViewer.setInput(comboBoxItems);
    setA2lComboContentProposal();
  }

  /**
   * Creates the buttons for the button bar
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
    this.okBtn.setEnabled(this.a2lFilePage.isMappingsAvailable());
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  private void setA2lComboContentProposal() {
    IContentProposalProvider provider =
        new ComboViewerContentPropsalProvider(this.a2lComboViewer.getCombo().getItems());
    ContentProposalAdapter adapter =
        new ContentProposalAdapter(this.a2lComboViewer.getCombo(), new ComboContentAdapter(), provider, null, null);
    adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
    adapter.setProposalPopupFocus();
    adapter.addContentProposalListener(arg0 -> {
      String sourceName = arg0.getContent();
      if (CommonUtils.isNotEmptyString(sourceName)) {
        populateWPDefVersCombo(sourceName);
      }
      else {
        A2LFileVersionDialog.this.wpDefVerCombo.removeAll();
        A2LFileVersionDialog.this.wpDefVerMap.clear();
      }
    });
  }

  /**
   * @param sourceName
   */
  private void populateWPDefVersCombo(final String sourceName) {
    A2lWpDefinitionVersionServiceClient client = new A2lWpDefinitionVersionServiceClient();
    try {
      Map<Long, A2lWpDefnVersion> wpDefVers =
          client.getWPDefnVersForPidcA2l(A2LFileVersionDialog.this.pidcA2lMap.get(sourceName).getId());
      A2LFileVersionDialog.this.wpDefVerCombo.removeAll();
      A2LFileVersionDialog.this.wpDefVerMap.clear();
      // To sort items in Combo Box
      SortedSet<Object> comboBoxItems = new TreeSet<>();
      for (A2lWpDefnVersion wpDef : wpDefVers.values()) {
        comboBoxItems.add(wpDef.getName());
        A2LFileVersionDialog.this.wpDefVerMap.put(wpDef.getName(), wpDef);
      }
      // set the sorted items in Combo Box
      A2LFileVersionDialog.this.wpDefVerCombo.setItems(comboBoxItems.toArray(new String[0]));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * This method initializes section
   */
  private void createVersionSection() {
    this.verSection = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "List of Versions");
    this.verSection.getDescriptionControl().setEnabled(false);
    // create form
    createForm();
    this.verSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.verSection.setClient(this.verForm);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.verForm = getFormToolkit().createForm(this.verSection);

    // Create values grid tableviewer
    createVersionGridTabViewer();
    if (!this.a2lFilePage.isMappingsAvailable()) {
      // add double click listener
      addDoubleClickListener();
    }
    // set the layout
    this.verForm.getBody().setLayout(new GridLayout());

  }

  /**
   * This method defines the activities to be performed when double clicked on the table
   */
  private void addDoubleClickListener() {
    this.versionTabViewer.addDoubleClickListener(event -> Display.getDefault().asyncExec(this::okPressed));
  }

  /**
   * Create versions grid table viewer
   */
  private void createVersionGridTabViewer() {

    int style;
    style = SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE;
    this.versionTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.verForm.getBody(), style,
        GridDataUtil.getInstance().getHeightHintGridData(VERSION_TAB_HEIGHT_HINT));

    // set table content provider
    this.versionTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    this.versionTabViewer.addSelectionChangedListener(evnt -> {
      final IStructuredSelection selection =
          (IStructuredSelection) A2LFileVersionDialog.this.versionTabViewer.getSelection();
      if (!selection.isEmpty()) {
        A2LFileVersionDialog.this.selPidcVersion = (PidcVersion) selection.getFirstElement();
        A2LFileVersionDialog.this.okBtn.setEnabled(true);
      }
    });

    // create verison col
    createVersionCol();
    // create description col
    createDescriptionCol();
    // set table input
    setTableInput();


  }

  /**
   * Set table input
   */
  private void setTableInput() {
    try {


      // add dummy version , used to delete the mapping
      PIDCVersionDummy dummyVersion = new PIDCVersionDummy(this.a2lFilePage.getHandler().getPidc());

      SortedSet<PidcVersion> pidcVersions = new TreeSet<>();
      pidcVersions.add(dummyVersion);


      // get pidc versions list from pidc versions page
      PIDCEditor editor = (PIDCEditor) this.a2lFilePage.getEditor();
      Map<Long, PidcVersion> pidcVerMap = editor.getVersionsPage().getDataHandler().getPidcVerMap();


      // changes to use Pidc id instead of version ids . the pidc version map is still needed
      Map<Long, SortedSet<String>> sdomPverNameMap =
          new SdomPverServiceClient().getSdomPverByPidc(this.a2lFilePage.getPidcVersion().getPidcId());

      getPidcVersionsFromPidcId(pidcVersions, pidcVerMap, sdomPverNameMap);

      this.versionTabViewer.setInput(pidcVersions);
      if (this.a2lFilePage.isMappingsAvailable()) {
        this.selPidcVersion = pidcVerMap.get(this.a2lFilePage.getSelA2LFile().get(0).getPidcVersion().getId());
        GridTableViewerUtil.getInstance().setSelection(this.versionTabViewer, this.selPidcVersion);
        this.versionTabViewer.getGrid().setEnabled(false);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param pidcVersions
   * @param pidcVerMap
   * @param sdomPverNameMap
   */
  private void getPidcVersionsFromPidcId(final SortedSet<PidcVersion> pidcVersions,
      final Map<Long, PidcVersion> pidcVerMap, final Map<Long, SortedSet<String>> sdomPverNameMap) {
    for (PidcVersion pidcVersion : pidcVerMap.values()) {
      SortedSet<String> sdomPverNameSet = sdomPverNameMap.get(pidcVersion.getId());
      for (String pverName : sdomPverNameSet) {
        for (PidcA2lFileExt a2lFileData : this.a2lFilePage.getSelA2LFile()) {
          if (CommonUtils.isEqual(pverName, a2lFileData.getA2lFile().getSdomPverName())) {
            pidcVersions.add(pidcVersion);
            break;
          }
        }
      }
    }
  }


  /**
   * Create description column
   */
  private void createDescriptionCol() {
    final GridViewerColumn descriptionColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.versionTabViewer, "Description", VERSION_COL_WIDTH);
    descriptionColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        // casting Pidc version to get the version description
        PidcVersion pidcVersion = (PidcVersion) element;
        return pidcVersion.getDescription();
      }
    });
  }

  /**
   * Create version name column
   */
  private void createVersionCol() {
    final GridViewerColumn versionColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.versionTabViewer, "PIDC Versions", VERSION_COL_WIDTH);
    versionColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        // casting Pidc version to get the version name
        PidcVersion pidcVersion = (PidcVersion) element;
        return pidcVersion.getVersionName();
      }
    });
  }

  /**
   * This method initializes formToolkit
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
   * after clicking ok in dialog
   */
  @Override
  protected void okPressed() {

    List<PidcA2lFileExt> tempListOfA2lFiles = new ArrayList<>(this.a2lFilePage.getSelA2LFile());
    for (PidcA2lFileExt a2lFileData : tempListOfA2lFiles) {
      if ((a2lFileData.getPidcA2l() == null) && (this.selPidcVersion instanceof PIDCVersionDummy)) {
        this.a2lFilePage.getSelA2LFile().remove(a2lFileData);
      }
    }
    if (!(this.selPidcVersion instanceof PIDCVersionDummy) && !validateA2lVersionMapping(this.selPidcVersion)) {
      return;
    }
    String sourceWpDefVer = this.wpDefVerCombo.getText();
    IStructuredSelection sel = (IStructuredSelection) this.a2lComboViewer.getSelection();
    String sourceA2lFileName = (String) sel.getFirstElement();

    if (CommonUtils.isNotEmptyString(sourceA2lFileName) && ((sourceWpDefVer == null) || sourceWpDefVer.isEmpty())) {
      CDMLogger.getInstance().errorDialog("Please select the wp defition version of the source a2l!",
          Activator.PLUGIN_ID);
      return;
    }

    // check for empty selections, when no versions assigned
    if (this.selPidcVersion instanceof PIDCVersionDummy) {
      if (sourceA2lFileName != null) {
        this.a2lFilePage.getSelA2LFile().clear();
        this.a2lFilePage.getSelA2LFile().addAll(tempListOfA2lFiles);
        CDMLogger.getInstance().infoDialog("Please select a valid pidc version to copy the mappings!",
            Activator.PLUGIN_ID);
        return;
      }
      updatePidcVersion();
    }
    else {
      updatePidcVersion();
    }

    this.a2lFilePage.getA2lFileMappingTab().getA2lTabViewer()
        .setSelection(this.a2lFilePage.getA2lFileMappingTab().getA2lTabViewer().getSelection());

    super.okPressed();
  }


  /**
   * This method validates the scenario - Select a2l files of different pidcversions and try to map it to a single
   * pidcversion
   *
   * @param selcVersion
   */
  private boolean validateA2lVersionMapping(final PidcVersion selcVersion) {
    Set<String> selectedSdomPvers = new HashSet<>();
    for (PidcA2lFileExt a2lFileData : this.a2lFilePage.getSelA2LFile()) {
      selectedSdomPvers.add(a2lFileData.getA2lFile().getSdomPverName());
    }
    Set<String> selPIDCVersionSDOMVerSet = new HashSet<>();
    try {
      selPIDCVersionSDOMVerSet.addAll(new SdomPverServiceClient().getAllPverNamesForPidcVersion(selcVersion.getId()));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    if (selPIDCVersionSDOMVerSet.containsAll(selectedSdomPvers)) {
      return true;
    }

    Set<String> invalidSDOMPvers = new HashSet<>(selectedSdomPvers);
    selectedSdomPvers.retainAll(selPIDCVersionSDOMVerSet);
    invalidSDOMPvers.removeAll(selectedSdomPvers);

    // A2lFiles from these SDOMPvers cannot be mapped to this version
    StringBuilder errorMsg = new StringBuilder();
    errorMsg.append('[');
    for (String string : invalidSDOMPvers) {
      errorMsg.append(string).append(',');
    }
    errorMsg.replace(0, errorMsg.length(), errorMsg.substring(0, errorMsg.length() - 1)).append(']');
    if (!invalidSDOMPvers.isEmpty()) {
      MessageDialogUtils.getErrorMessageDialog("Invalid Mapping", "The selected A2l Files from the PVER Names : " +
          errorMsg.toString() + " cannot be mapped to this version : " + selcVersion.getName());
    }
    return false;
  }

  /**
   * add to command stack & refresh the table viewer
   *
   * @param selcVersion
   */
  private void updatePidcVersion() {

    Set<PidcA2l> pidcA2lsToUpdate = new HashSet<>();
    Set<PidcA2l> pidcA2lsToCreate = new HashSet<>();

    for (PidcA2lFileExt a2lFileData : this.a2lFilePage.getSelA2LFile()) {
      if (a2lFileData.getPidcA2l() == null) {
        PidcA2l pidcA2l = new PidcA2l();
        pidcA2l.setProjectId(this.selPidcVersion.getPidcId());
        pidcA2l.setPidcVersId(this.selPidcVersion.getId());
        pidcA2l.setSdomPverName(a2lFileData.getA2lFile().getSdomPverName());
        pidcA2l.setSdomPverRevision(a2lFileData.getA2lFile().getSdomPverRevision());
        pidcA2l.setA2lFileId(a2lFileData.getA2lFile().getId());
        pidcA2l.setActive(true);
        pidcA2l.setWorkingSetModified(true);
        pidcA2lsToCreate.add(pidcA2l);
      }
      else {
        PidcA2l pidcA2l = a2lFileData.getPidcA2l();
        pidcA2l.setActive(this.selPidcVersion.getId() != null);
        pidcA2l.setPidcVersId(this.selPidcVersion.getId());
        pidcA2lsToUpdate.add(pidcA2l);
      }
    }
    IStructuredSelection sel = (IStructuredSelection) this.a2lComboViewer.getSelection();
    String sourceA2lFileName = (String) sel.getFirstElement();

    if (!pidcA2lsToUpdate.isEmpty()) {
      Set<PidcA2l> newPidcA2lSet = this.a2lFilePage.getHandler().updatePidcA2l(pidcA2lsToUpdate);
      copyA2lMappings(newPidcA2lSet);
    }
    if (!pidcA2lsToCreate.isEmpty() && ((sourceA2lFileName == null) || sourceA2lFileName.isEmpty())) {
      this.a2lFilePage.getHandler().createPidcA2l(pidcA2lsToCreate);
    }
    else if (!pidcA2lsToCreate.isEmpty() && (sourceA2lFileName != null) && !sourceA2lFileName.isEmpty()) {
      String sourceWpDefVer = this.wpDefVerCombo.getText();
      PidcTakeOverA2lWrapper pidcTakeOverA2lWrapper = new PidcTakeOverA2lWrapper();
      pidcTakeOverA2lWrapper.setSourceWpDefVersId(this.wpDefVerMap.get(sourceWpDefVer).getId());
      pidcTakeOverA2lWrapper.setPidcA2lsToCreate(pidcA2lsToCreate);
      pidcTakeOverA2lWrapper.setDeriveFromFunc(this.funcDeriveChkBtn.getSelection());
      callTakeOverServiceWithProgress(pidcTakeOverA2lWrapper);
    }

  }

  /**
   * @param pidcTakeOverA2lWrapper
   */
  private void callTakeOverServiceWithProgress(final PidcTakeOverA2lWrapper pidcTakeOverA2lWrapper) {
    ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Copying contents from source to destination A2L..", 100);
        monitor.worked(50);
        try {
          new A2lWpDefinitionVersionServiceClient().createPidcA2landTakeOverFromA2l(pidcTakeOverA2lWrapper);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
        monitor.worked(100);
      });
    }
    catch (InvocationTargetException | InterruptedException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      Thread.currentThread().interrupt();
    }

  }


  /**
   * @param newPidcA2lSet
   */
  private void copyA2lMappings(final Set<PidcA2l> newPidcA2lSet) {
    IStructuredSelection sel = (IStructuredSelection) this.a2lComboViewer.getSelection();
    String sourceA2lFileName = (String) sel.getFirstElement();
    Map<String, CopyPar2WpFromA2lInput> a2lWithPar2WpMap = new HashMap<>();
    if (CommonUtils.isNotEmptyString(sourceA2lFileName)) {
      String sourceWpDefVer = this.wpDefVerCombo.getText();

      A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();
      try {
        List<CopyPar2WpFromA2lInput> inputDataList = new ArrayList<>();
        for (PidcA2l pidcA2l : newPidcA2lSet) {

          CopyPar2WpFromA2lInput inputData = new CopyPar2WpFromA2lInput();
          inputData.setDescPidcA2lId(pidcA2l.getId());

          inputData.setSourceWpDefVersId(this.wpDefVerMap.get(sourceWpDefVer).getId());
          // If pidc has assignments already add to map
          if (pidcA2l.isWpParamPresentFlag()) {
            a2lWithPar2WpMap.put(pidcA2l.getName(), inputData);
          }
          inputData.setDerivateFromFunc(this.funcDeriveChkBtn.getSelection());
          inputDataList.add(inputData);
        }
        boolean cancelPressed = false;
        if (!a2lWithPar2WpMap.isEmpty()) {
          cancelPressed = callOverwriteAssignmentsDialog(a2lWithPar2WpMap, inputDataList);

        }
        if (!cancelPressed) {
          callCopyServiceWithProgress(servClient, inputDataList);
        }
      }
      catch (InvocationTargetException | InterruptedException excep) {
        CDMLogger.getInstance().errorDialog(excep.getMessage(), excep, Activator.PLUGIN_ID);
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * @param a2lWithPar2WpMap
   * @param inputDataList
   * @return
   */
  private boolean callOverwriteAssignmentsDialog(final Map<String, CopyPar2WpFromA2lInput> a2lWithPar2WpMap,
      final List<CopyPar2WpFromA2lInput> inputDataList) {
    boolean cancelPressed;
    StringBuilder fileNamesWithPar2WP = new StringBuilder();
    for (String fileName : a2lWithPar2WpMap.keySet()) {
      fileNamesWithPar2WP.append(fileName).append('\n');
    }
    String filesWithAssignmets = "Par2WP Assigments exist for the A2L \n" +
        fileNamesWithPar2WP.toString().substring(0, fileNamesWithPar2WP.toString().length() - 1);

    OverwriteA2lAssignmentDialog overWriteDialog = new OverwriteA2lAssignmentDialog(
        Display.getCurrent().getActiveShell(), A2LFileVersionDialog.this, filesWithAssignmets);
    overWriteDialog.open();
    cancelPressed = overWriteDialog.isCancelPressed();
    if (isCanOverWrite()) {
      for (CopyPar2WpFromA2lInput data : a2lWithPar2WpMap.values()) {
        if (isOverWriteAllAssignments()) {
          data.setOverWriteAssigments(true);
        }
        else {
          data.setOverOnlyDefaultAssigments(true);
        }
      }
    }
    else {
      inputDataList.removeAll(a2lWithPar2WpMap.values());
    }
    return cancelPressed;
  }

  /**
   * @param servClient
   * @param inputDataList
   * @throws ApicWebServiceException
   * @throws InterruptedException
   * @throws InvocationTargetException
   */
  private void callCopyServiceWithProgress(final A2lWpDefinitionVersionServiceClient servClient,
      final List<CopyPar2WpFromA2lInput> inputDataList)
      throws InvocationTargetException, InterruptedException {
    ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    dialog.run(true, true, monitor -> {
      monitor.beginTask("Copying contents from source to destination A2L..", 100);
      monitor.worked(50);
      try {
        servClient.copyA2lWpResp(inputDataList);
      }
      catch (ApicWebServiceException exp) {
        if ("COPY_A2L.COPY_MAPPINGS_INVALID".equals(exp.getErrorCode())) {
          CDMLogger.getInstance().infoDialog(exp.getMessage(), Activator.PLUGIN_ID);
        }
        else {
          CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
      }
      monitor.worked(100);
    });
  }


  /**
   * @return the sourceA2l
   */
  public PidcA2l getSourceA2l() {
    return this.sourceA2l;
  }


  /**
   * @param sourceA2l the sourceA2l to set
   */
  public void setSourceA2l(final PidcA2l sourceA2l) {
    this.sourceA2l = sourceA2l;
  }


  /**
   * @return the overWriteAllAssignments
   */
  public boolean isOverWriteAllAssignments() {
    return this.overWriteAllAssignments;
  }


  /**
   * @param overWriteAllAssignments the overWriteAllAssignments to set
   */
  public void setOverWriteAllAssignments(final boolean overWriteAllAssignments) {
    this.overWriteAllAssignments = overWriteAllAssignments;
  }


  /**
   * @return the canOverWrite
   */
  public boolean isCanOverWrite() {
    return this.canOverWrite;
  }


  /**
   * @param canOverWrite the canOverWrite to set
   */
  public void setCanOverWrite(final boolean canOverWrite) {
    this.canOverWrite = canOverWrite;
  }

}
