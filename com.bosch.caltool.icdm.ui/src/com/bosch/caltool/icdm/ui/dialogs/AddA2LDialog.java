/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.apic.ui.dialogs.OverwriteA2lAssignmentDialog;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.CopyPar2WpFromA2lInput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.SdomPVER;
import com.bosch.caltool.icdm.ui.jobs.A2LFileUploaderJob;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LFileInfoServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.SdomPverServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.ui.validators.Validator;


/**
 * @author bru2cob
 */
public class AddA2LDialog extends AbstractDialog {

  /**
   * sdompver instance
   */
  private final SdomPVER sdomPver;

  /**
   * pidc info
   */
  private PidcVersion pidcVersion;

  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();

  /**
   * Composite instance for the dialog
   */
  private Composite composite;
  /**
   * save button
   */
  protected Button saveBtn;
  /**
   * cancel button
   */
  protected Button cancelBtn;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;
  /**
   * browse button image
   */
  private Image browseButtonImage;
  /**
   * Composite instance
   */
  private Composite top;

  /**
   * a2l name text
   */
  private Text a2lNameText;
  /**
   * var revisions
   */
  private Combo varRevCombo;
  /**
   * variant name
   */
  private Text variantsText;
  /**
   * selc variant
   */
  private String selVariant;

  /**
   * mandatory dec for a2lname
   */
  protected ControlDecoration a2lNameDesc;
  /**
   * mandatory dec for variant
   */
  protected ControlDecoration variantsDesc;
  /**
   * mandatory dec for var rev
   */
  protected ControlDecoration varRevComboDesc;
  /**
   * selc a2l file
   */
  protected String fileSelected;

  private SortedSet<String> pverNameList;

  /**
   * Section instance
   */
  private Section par2WpSection;
  /**
   * Form instance
   */
  private Form par2WpForm;

  private Combo a2lCombo;

  private boolean overWriteAllAssignments;

  private boolean canOverWrite;

  private static final String PAR2WP_ASSIGNMENT = "Par2WP Assignment";

  /**
   * Key - pver name+pver var + a2l file name Value - pidcA2l
   */
  Map<String, PidcA2l> pidcA2lMap = new HashMap<>();
  Map<String, A2lWpDefnVersion> wpDefVerMap = new HashMap<>();
  private Combo wpDefVerCombo;

  /**
   * Button to check for deriving WP RESP from functions
   */
  private Button funcDeriveChkBtn;

  // check whether the dialog called from upload A2l File
  private boolean fromUploadA2L = true;

  private A2LWPInfoBO a2lWPInfoBO;

  /**
   * @return the selVariant
   */
  public String getSelVariant() {
    return this.selVariant;
  }


  /**
   * @param selVariant the selVariant to set
   */
  public void setSelVariant(final String selVariant) {
    this.selVariant = selVariant;
  }


  /**
   * @param parentShell shell
   * @param sdomPver selc sdompver
   */
  public AddA2LDialog(final Shell parentShell, final SdomPVER sdomPver) {
    super(parentShell);

    this.sdomPver = sdomPver;

  }

  /**
   * @param parentShell shell
   * @param pidcVersion pidc version
   * @param fromUploadA2L whether from upload A2L or not
   */
  public AddA2LDialog(final Shell parentShell, final PidcVersion pidcVersion, final A2LWPInfoBO a2lWPInfoBO,
      final boolean fromUploadA2L) {
    super(parentShell);
    this.fromUploadA2L = fromUploadA2L;
    this.pidcVersion = pidcVersion;
    this.sdomPver = null;
    this.a2lWPInfoBO = a2lWPInfoBO;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(this.fromUploadA2L ? "Upload A2L" : AddA2LDialog.PAR2WP_ASSIGNMENT);
    super.configureShell(newShell);
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);

    // Set the title
    setTitle(this.fromUploadA2L ? "Upload A2L" : AddA2LDialog.PAR2WP_ASSIGNMENT);
    // Set the message
    setMessage(this.fromUploadA2L ? "Enter A2L File Details" : "Enter Details", IMessageProvider.INFORMATION);
    return contents;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {

    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, this.fromUploadA2L ? "Upload" : "OK", true);
    this.saveBtn.setEnabled(false);
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    // ICDM-1456
    // if the variant name does not exist upload the file
    if (this.fromUploadA2L) {
      if (checkPverName(this.sdomPver.getPverName()) ||
          isNewVariant(getA2LFilePVERVariants(this.sdomPver.getPverName()))) {
        String sourceWpDefVer = this.wpDefVerCombo.getText();
        Long sourceWpDefVerId = null;
        if ((sourceWpDefVer != null) && !sourceWpDefVer.isEmpty()) {
          sourceWpDefVerId = this.wpDefVerMap.get(sourceWpDefVer).getId();
        }

        final A2LFileUploaderJob job = new A2LFileUploaderJob(new MutexRule(), this.a2lNameText.getText(),
            this.sdomPver, this.variantsText.getText(), Long.parseLong(this.varRevCombo.getText()), sourceWpDefVerId,
            this.funcDeriveChkBtn.getSelection());
        com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
        job.schedule();
      }
      else {
        CDMLogger.getInstance().infoDialog("PVER Variant name already exists", Activator.PLUGIN_ID);
      }
    }
    else {
      // from A2lPage

      copyA2lMappings();


    }
    super.okPressed();
  }


  /**
   * copy the Wp and parameter to destination A2l
   */
  private void copyA2lMappings() {
    String sourceName = AddA2LDialog.this.a2lCombo.getText();
    Map<String, CopyPar2WpFromA2lInput> a2lWithPar2WpMap = new HashMap<>();
    String sourceWpDefVer = this.wpDefVerCombo.getText();

    if (((sourceWpDefVer != null) && !sourceWpDefVer.isEmpty()) && (sourceName != null) && (this.a2lWPInfoBO != null)) {
      // fetch a2l file details and version selected detail
      Long sourceWpDefVerId = this.wpDefVerMap.get(sourceWpDefVer).getId();
      Long a2lFileId = this.a2lWPInfoBO.getPidcA2lBo().getPidcA2l().getId();
      String a2lfileName = this.a2lWPInfoBO.getPidcA2lBo().getPidcA2l().getName();

      CopyPar2WpFromA2lInput inputData = new CopyPar2WpFromA2lInput();

      inputData.setDescPidcA2lId(a2lFileId);
      inputData.setSourceWpDefVersId(sourceWpDefVerId);
      inputData.setDerivateFromFunc(this.funcDeriveChkBtn.getSelection());

      List<CopyPar2WpFromA2lInput> inputDataList = new ArrayList<>();

      a2lWithPar2WpMap.put(a2lfileName, inputData);
      inputDataList.add(inputData);

      if (!a2lWithPar2WpMap.isEmpty()) {

        // open the callOverwriteAssignments dialog
        boolean cancel = callOverwriteAssignmentsDialog(a2lWithPar2WpMap, inputDataList);
        if (!cancel) {
          // progress bar
          invokeService(inputDataList);


        }
      }

    }
  }


  /**
   * @param inputDataList call the copyA2lWpResp service
   */
  private void invokeService(final List<CopyPar2WpFromA2lInput> inputDataList) {
    ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Copying contents from source to destination A2L..", 100);
        monitor.worked(50);
        try {
          A2lWpDefinitionVersionServiceClient serverClient = new A2lWpDefinitionVersionServiceClient();
          // copy the data
          serverClient.copyA2lWpResp(inputDataList);
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
   * @param a2lWithPar2WpMap
   * @param inputDataList
   * @return overwrite assignment dialog to ask what needs to be overridden
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

    Map<String, Boolean> dialogData = new HashMap<>();
    // set default value as false
    dialogData.put("canOverWrite", false);
    dialogData.put("overWriteAllAssignments", false);

    OverwriteA2lAssignmentDialog overWriteDialog =
        new OverwriteA2lAssignmentDialog(Display.getCurrent().getActiveShell(), dialogData, filesWithAssignmets);
    overWriteDialog.open();
    cancelPressed = overWriteDialog.isCancelPressed();
    // set key value from the overriden values from this object
    setCanOverWrite(dialogData.get("canOverWrite"));
    setOverWriteAllAssignments(dialogData.get("overWriteAllAssignments"));

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


  /**
   * @param pverName
   * @return
   */
  private Set<String> getA2LFilePVERVariants(final String pverName) {
    SortedSet<String> pverVariants = new TreeSet<>();
    try {
      pverVariants = new A2LFileInfoServiceClient().getA2LFilePVERVars(pverName);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error getting list of A2l-SDOM-Pver-variants : " + e.getMessage(), e,
          Activator.PLUGIN_ID);
    }
    return pverVariants;
  }


  // ICDM-1456
  /**
   * Check whether the sdom pver name already exists
   *
   * @param sdomPverName sdom pver name
   * @return true if sdomname exists in MVSdomPver
   */
  private boolean checkPverName(final String pverName) {
    if (!CommonUtils.isNotEmpty(this.pverNameList)) {
      try {
        this.pverNameList = new SdomPverServiceClient().getAllPverNames();
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog("Error getting list of SDOM-Pver : " + e.getMessage(), e,
            Activator.PLUGIN_ID);
      }
    }
    return this.pverNameList.contains(pverName.toUpperCase(Locale.getDefault()));
  }


  // ICDM-1456
  /**
   * Checks whether the variant entered is not already available for the sdompver name
   *
   * @param sdomPvers
   */
  private boolean isNewVariant(final Set<String> sdomPvers) {
    String varName = this.variantsText.getText();
    Iterator<String> sdoms = sdomPvers.iterator();
    while (sdoms.hasNext()) {
      if (sdoms.next().equalsIgnoreCase(varName)) {
        return false;
      }
    }
    return true;

  }


  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    createComposite();
    return this.top;

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
   * This method initializes composite
   */
  private void createComposite() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    // if not from upload A2l dont show this section
    if (this.fromUploadA2L) {
      createSection();

      this.section.getDescriptionControl().setEnabled(false);
    }
    // create par2wp assignment section
    createPar2WpSection();
    this.composite.setLayoutData(gridData);
  }

  /**
  *
  */
  private void createPar2WpSection() {
    this.par2WpSection =
        SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), AddA2LDialog.PAR2WP_ASSIGNMENT);
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

    new Label(this.par2WpForm.getBody(), SWT.NONE).setText("Take over from another A2L in iCDM : ");
    this.a2lCombo = new Combo(this.par2WpForm.getBody(), SWT.READ_ONLY | SWT.BORDER);
    this.a2lCombo.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.a2lCombo.add("");
    this.a2lCombo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

    new Label(this.par2WpForm.getBody(), SWT.NONE).setText("Select source a2l wp definition version : ");
    this.wpDefVerCombo = new Combo(this.par2WpForm.getBody(), SWT.READ_ONLY | SWT.BORDER);
    this.wpDefVerCombo.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.wpDefVerCombo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    this.wpDefVerCombo.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        checkSaveBtnEnable();
      }
    });
    this.a2lCombo.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        String sourceName = AddA2LDialog.this.a2lCombo.getText();

        if ((null != sourceName) && !sourceName.isEmpty()) {
          A2lWpDefinitionVersionServiceClient client = new A2lWpDefinitionVersionServiceClient();
          try {
            Map<Long, A2lWpDefnVersion> wpDefVers =
                client.getWPDefnVersForPidcA2l(AddA2LDialog.this.pidcA2lMap.get(sourceName).getId());
            AddA2LDialog.this.wpDefVerCombo.removeAll();
            AddA2LDialog.this.wpDefVerMap.clear();
            // To sort items in Combo Box
            SortedSet<Object> comboBoxItems = new TreeSet<>();
            for (A2lWpDefnVersion wpDef : wpDefVers.values()) {
              comboBoxItems.add(wpDef.getName());
              AddA2LDialog.this.wpDefVerMap.put(wpDef.getName(), wpDef);
            }
            // set the sorted items in Combo Box
            AddA2LDialog.this.wpDefVerCombo.setItems(comboBoxItems.toArray(new String[0]));

          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
          }

        }
        else {
          AddA2LDialog.this.wpDefVerCombo.removeAll();
          AddA2LDialog.this.wpDefVerMap.clear();
        }
        checkSaveBtnEnable();
      }
    });

    new Label(this.par2WpForm.getBody(), SWT.NONE).setText("Derive WP/Resp for new labels from function");
    this.funcDeriveChkBtn = new Button(this.par2WpForm.getBody(), SWT.CHECK);
    this.funcDeriveChkBtn.setToolTipText(CommonUiUtils.getMessage("A2L_COPY", "TOOLTIP_DERIVE_FROM_FUNCTION"));

    Collection<PidcA2lFileExt> pidcA2lFiles;
    PidcA2lServiceClient client = new PidcA2lServiceClient();
    try {
      if (this.fromUploadA2L) {
        pidcA2lFiles = client.getAllA2lByPidc(this.sdomPver.getPidcVersion().getPidcId()).values();
        // To sort items in Combo Box
        SortedSet<Object> comboBoxItems = new TreeSet<>();
        for (PidcA2lFileExt pidcA2lExt : pidcA2lFiles) {
          if ((null != pidcA2lExt.getPidcA2l()) && pidcA2lExt.getPidcA2l().isWpParamPresentFlag()) {

            A2LFile a2lFile = pidcA2lExt.getA2lFile();
            PidcVersion pidcVersionDetail = pidcA2lExt.getPidcVersion();

            String comboVal = a2lFile.getSdomPverName() + " : " + a2lFile.getSdomPverVariant() + " : " +
                a2lFile.getFilename() + " ; " + pidcVersionDetail.getVersionName();
            comboBoxItems.add(comboVal);
            this.pidcA2lMap.put(comboVal, pidcA2lExt.getPidcA2l());
          }

        }
        this.a2lCombo.setItems(comboBoxItems.toArray(new String[0]));
      }
      else {
        fillA2lComboViewer();
      }

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @throws ApicWebServiceException
   */
  private void fillA2lComboViewer() throws ApicWebServiceException {


    PidcA2lServiceClient client = new PidcA2lServiceClient();
    Collection<PidcA2lFileExt> pidcA2lFiles = client.getAllA2lByPidc(this.pidcVersion.getPidcId()).values();

    SortedSet<String> comboBoxItems = new TreeSet<>();
    // Key - A2l file name, Value - Count
    Map<String, Integer> a2lFileNameCountMap = new HashMap<>();
    if (pidcA2lFiles != null) {
      for (PidcA2lFileExt pidcA2lExt : pidcA2lFiles) {
        if ((null != pidcA2lExt.getPidcA2l()) && pidcA2lExt.getPidcA2l().isWpParamPresentFlag()) {
          A2LFile a2lFile = pidcA2lExt.getA2lFile();
          PidcVersion pidcVesrionData = pidcA2lExt.getPidcVersion();
          if (!this.fromUploadA2L && (this.a2lWPInfoBO != null) &&
              (this.a2lWPInfoBO.getA2lFileInfoBo().getA2lFileId().equals(a2lFile.getId()))) {
            // if from A2l page skip the current A2l in the list
            continue;
          }
          PidcA2l pidcA2l = pidcA2lExt.getPidcA2l();
          String comboVal = pidcA2l.getSdomPverName() + " : " + pidcA2l.getSdomPverVarName() + " : " +
              pidcA2l.getName() + " ; " + pidcVesrionData.getVersionName();

          comboVal = frameComboBox(comboBoxItems, a2lFileNameCountMap, comboVal);
          comboBoxItems.add(comboVal);
          this.pidcA2lMap.put(comboVal, pidcA2l);
        }
      }
    }
    this.a2lCombo.setItems(comboBoxItems.toArray(new String[0]));
  }


  /**
   * @param comboBoxItems
   * @param a2lFileNameCountMap
   * @param comboVal
   * @return
   */
  private String frameComboBox(final SortedSet<String> comboBoxItems, final Map<String, Integer> a2lFileNameCountMap,
      String comboVal) {
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
    return comboVal;
  }


  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Enter the details");
    createForm();

    this.section.setClient(this.form);
  }


  /**
   * This method initializes form
   */
  private void createForm() {
    final String pverName = this.sdomPver.getPverName();
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    this.browseButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON);
    final GridData txtGrid = GridDataUtil.getInstance().getTextGridData();
    // a2l file detail
    createA2lControl(txtGrid);
//     sdom detail
    createSdomControl(pverName, txtGrid);
    // variants detail
    createVarControl(pverName, txtGrid);
    // var revision detail
    createVarRevControl(txtGrid);
  }


  /**
   * @param gridData
   */
  private void createVarRevControl(final GridData gridData) {
    getFormToolkit().createLabel(this.form.getBody(), "Variants Revision:");
    this.varRevCombo = new Combo(this.form.getBody(), SWT.READ_ONLY);
    this.varRevCombo.setLayoutData(gridData);
    this.varRevComboDesc = new ControlDecoration(this.varRevCombo, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.varRevComboDesc, IUtilityConstants.MANDATORY_MSG);
    this.varRevCombo.addModifyListener(event -> {
      if (AddA2LDialog.this.varRevCombo.getSelectionIndex() == -1) {
        AddA2LDialog.this.decorators.showReqdDecoration(AddA2LDialog.this.varRevComboDesc,
            IUtilityConstants.MANDATORY_MSG);
      }
      else {
        Validator.getInstance().validateNDecorate(AddA2LDialog.this.varRevComboDesc, AddA2LDialog.this.varRevCombo,
            true);
      }
      checkSaveBtnEnable();
    });
    createEmptyLabel();

  }


  /**
   * @param pverName pver name
   * @param txtGrid gridData
   */
  private void createVarControl(final String pverName, final GridData txtGrid) {
    getFormToolkit().createLabel(this.form.getBody(), "PVER Variants:");
    this.variantsText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.variantsText.setLayoutData(txtGrid);
    this.variantsDesc = new ControlDecoration(this.variantsText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.variantsDesc, IUtilityConstants.MANDATORY_MSG);
    // ICDM-1456
    // if SdomPverName does not exist in MvSdomPver table , it should be possible to manuaaly enter the variant
    this.variantsText.setEditable(!checkPverName(pverName));

    this.variantsText.addModifyListener(event -> {
      String variantName = AddA2LDialog.this.variantsText.getText();
      if (variantName.isEmpty()) {
        AddA2LDialog.this.varRevCombo.removeAll();
        AddA2LDialog.this.varRevCombo.setEnabled(true);
      }
      else {
        if (isNewVariant(getPVERVariants(AddA2LDialog.this.sdomPver.getPverName()))) {
          AddA2LDialog.this.varRevCombo.add("0");
          AddA2LDialog.this.varRevCombo.setText("0");
          AddA2LDialog.this.varRevCombo.setEnabled(false);
        }
        else {
          setVariantsRevision(pverName, variantName);
        }
      }
      Validator.getInstance().validateNDecorate(AddA2LDialog.this.variantsDesc, AddA2LDialog.this.variantsText, true,
          false);
      checkSaveBtnEnable();
    });

    final Button varBrowseBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    varBrowseBtn.setEnabled(

        checkPverName(pverName));
    varBrowseBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final AddPverVariantDialog pverVarDialog =
            new AddPverVariantDialog(Display.getDefault().getActiveShell(), getPVERVariants(pverName));
        pverVarDialog.open();
        final String selectedVar = pverVarDialog.getSelectedVar();
        if (selectedVar != null) {
          AddA2LDialog.this.variantsText.setText(selectedVar);
          AddA2LDialog.this.setSelVariant(selectedVar);
        }
      }
    });
    varBrowseBtn.setImage(this.browseButtonImage);
    varBrowseBtn.setToolTipText("Select pver variant");
  }


  /**
   * iCDM-756 <br>
   * Fetches PVer variants for the SDOM PVer name
   *
   * @param pverName sdom name
   * @return list of pver variant names
   */
  protected Set<String> getPVERVariants(final String pverName) {
    SortedSet<String> pverVariants = null;
    try {
      pverVariants = new SdomPverServiceClient().getPverVariantNames(pverName);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error getting list of SDOM-Pver-variants : " + e.getMessage(), e,
          Activator.PLUGIN_ID);
    }
    return pverVariants;
  }


  /**
   * @param pverName
   * @param txtGrid gridData
   */
  private void createSdomControl(final String pverName, final GridData txtGrid) {
    getFormToolkit().createLabel(this.form.getBody(), "SDOM PVER:");
    final Text pverText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    pverText.setLayoutData(txtGrid);
    pverText.setText(pverName);
    pverText.setEditable(false);
    pverText.setEnabled(false);
    createEmptyLabel();
  }


  /**
   * create a2l info
   *
   * @param txtGrid gridData
   */
  private void createA2lControl(final GridData txtGrid) {
    getFormToolkit().createLabel(this.form.getBody(), "A2L File:");
    this.a2lNameText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);

    this.a2lNameText.setLayoutData(txtGrid);
    this.a2lNameText.setEditable(false);
    this.a2lNameDesc = new ControlDecoration(this.a2lNameText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.a2lNameDesc, IUtilityConstants.MANDATORY_MSG);
    this.a2lNameText.addModifyListener(event -> {
      if ((AddA2LDialog.this.a2lNameText.getText() != null) &&
          !"".equalsIgnoreCase(AddA2LDialog.this.a2lNameText.getText())) {
        Validator.getInstance().validateNDecorate(AddA2LDialog.this.a2lNameDesc, AddA2LDialog.this.a2lNameText, true,
            false);
      }
      checkSaveBtnEnable();
    });
    final Button browseBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    browseBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.MULTI);
        fileDialog.setText("Upload A2L File");
        fileDialog.setFilterExtensions(new String[] { "*.a2l" });
        AddA2LDialog.this.fileSelected = fileDialog.open();
        if (AddA2LDialog.this.fileSelected != null) {
          AddA2LDialog.this.a2lNameText.setText(AddA2LDialog.this.fileSelected);
        }
      }
    });
    browseBtn.setImage(this.browseButtonImage);
    browseBtn.setToolTipText("Select a2l file");
  }

  /**
   * checks whether the save button can be enabled or not
   */
  protected void checkSaveBtnEnable() {
    this.saveBtn.setEnabled(validateFields());
  }

  /**
   * @return
   */
  private boolean validateFields() {
    // validation
    if (!this.fromUploadA2L) {
      return (((this.a2lCombo.getText() == null) || this.a2lCombo.getText().isEmpty()) ||
          ((this.wpDefVerCombo.getText() != null) && !this.wpDefVerCombo.getText().isEmpty()));

    }
    boolean isValid = false;
    if (!"".equalsIgnoreCase(this.a2lNameText.getText()) && !"".equalsIgnoreCase(this.variantsText.getText()) &&
        (this.varRevCombo.getSelectionIndex() != -1) &&
        (((this.a2lCombo.getText() == null) || this.a2lCombo.getText().isEmpty()) ||
            ((this.wpDefVerCombo.getText() != null) && !this.wpDefVerCombo.getText().isEmpty()))) {
      isValid = true;
    }
    return isValid;
  }


  /**
   * to insert empty label in the layout
   */
  private void createEmptyLabel() {
    getFormToolkit().createLabel(this.form.getBody(), "");
  }

  // ICDM-1456
  /**
   * @param pverName
   * @param selectedVar
   */
  private void setVariantsRevision(final String pverName, final String selectedVar) {
    final TreeSet<Long> sdomPvers = (TreeSet<Long>) getPVERVarRevisions(pverName, selectedVar);
    AddA2LDialog.this.varRevCombo.setEnabled(true);
    if (sdomPvers != null) {
      AddA2LDialog.this.varRevCombo.removeAll();
      if (sdomPvers.size() == 1) {
        String varRev = sdomPvers.first().toString();
        AddA2LDialog.this.varRevCombo.add(varRev);
        AddA2LDialog.this.varRevCombo.setText(varRev);

      }
      else {
        for (Long pver : sdomPvers.descendingSet()) {
          AddA2LDialog.this.varRevCombo.add(pver.toString());
        }
      }
    }
  }


  /**
   * iCDM-756 <br>
   * Gets the SDOM PVer object for the sdom name and variant
   *
   * @param sdomPverName sdom name
   * @param variant variant name
   * @return SDOMPver set
   */
  private SortedSet<Long> getPVERVarRevisions(final String pverName, final String selectedVar) {
    SortedSet<Long> pverVarRevs = null;
    try {
      pverVarRevs = new SdomPverServiceClient().getPverVariantVersions(pverName, selectedVar);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error getting list of SDOM-Pver-variant-revisions : " + e.getMessage(), e,
          Activator.PLUGIN_ID);
    }
    return pverVarRevs;
  }

}
