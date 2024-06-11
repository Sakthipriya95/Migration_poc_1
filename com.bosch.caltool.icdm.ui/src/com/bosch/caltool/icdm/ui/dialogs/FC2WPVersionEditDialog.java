/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPDefBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.editors.pages.FC2WPVersionsPage;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;


/**
 * This class represents the dialog to insert/edit questionnaire version contents
 *
 * @author bru2cob
 */
public class FC2WPVersionEditDialog extends AbstractDialog {

  
  private static final String ERROR_IN_INVOKING_THREAD_MSG = "Error in invoking thread to open progress bar for FC2WP version:";
  /**
   * version size string
   */
  private static final int VERSION_SIZE_1 = 1;
  /**
   * version string
   */
  private static final String VERSION_STR = "  Version ";
  /**
   * top composite
   */
  private Composite top;
  /**
   * save button
   */
  private Button saveBtn;
  /**
   * FormToolkit
   */
  private final FormToolkit formToolkit;
  /**
   * name text
   */
  private StyledText nameTxt;
  /**
   * big decimal for latest major version number
   */
  private Long latestMajorVersionNum;
  /**
   * big decimal for latest minor version number
   */
  private Long latestMinorVersionNum;
  /**
   * minor version number
   */
  private int minorVersionNum;
  /**
   * major version number
   */
  private int majorVersionNum;
  /**
   * active button
   */
  private Button activeBtn;
  /**
   * text for German description
   */
  private TextBoxContentDisplay txtDescGer;
  /**
   * text for English Description
   */
  private TextBoxContentDisplay txtDescEng;

  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();
  /**
   * Decorator for the Combo.
   */
  private ControlDecoration descEngDec;
  /**
   * true if the dialog is for edit
   */
  private final boolean isEdit;

  /** The selected version. */
  private final FC2WPVersion selectedVersion;

  /** All versions. */
  private final Set<FC2WPVersion> allVersions;
  private StyledText txtArchRelSDOM;

  final Long fc2wpDefID;

  /**
   * FC2WPVersionsPage
   **/
  private final FC2WPVersionsPage versionsPage;

  /**
   * Creating version.
   *
   * @param parentShell parent shell
   * @param formToolkit FormToolkit
   * @param fc2wpDefID the fc 2 wp def ID
   * @param selectedVersion the selected version
   * @param fc2wpDefBo fc2wpDefBo
   * @param isEdit the is edit
   */
  public FC2WPVersionEditDialog(final Shell parentShell, final FormToolkit formToolkit, final Long fc2wpDefID,
      final FC2WPVersion selectedVersion, final FC2WPDefBO fc2wpDefBo, final FC2WPVersionsPage versionsPage,
      final boolean isEdit) {
    super(parentShell);
    this.formToolkit = formToolkit;
    this.isEdit = isEdit;
    this.selectedVersion = selectedVersion;
    this.versionsPage = versionsPage;
    this.allVersions = fc2wpDefBo.getAllVersions();
    this.fc2wpDefID = fc2wpDefID;
  }

  /**
   * create contents
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("FC2WP Version");
    // Set the message
    setMessage("Edit the details and click Save button");
    return contents;
  }

  /**
   * configure the shell and set the title
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell name
    if (this.isEdit) {
      newShell.setText("Edit Version");
    }
    else {
      newShell.setText("Create New Version");
    }
    newShell.setSize(530, 590);
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
    createComposite();
    return this.top;
  }

  /**
   * create Composite
   */
  private void createComposite() {
    Composite composite = this.formToolkit.createComposite(this.top);
    GridLayout layout = new GridLayout();
    composite.setLayout(layout);
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    createSection(composite);
  }

  /**
   * @param composite Composite
   */
  private void createSection(final Composite composite) {
    Section section = this.formToolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    section.setText("Version Details");
    section.setLayoutData(GridDataUtil.getInstance().getGridData());
    section.setLayout(new GridLayout());
    section.getDescriptionControl().setEnabled(false);
    createForm(section);
  }

  /**
   * @param section
   */
  private void createForm(final Section section) {
    Form form = this.formToolkit.createForm(section);
    GridLayout layout = new GridLayout();
    // divide the dialog into 2 columns
    layout.numColumns = 2;
    form.getBody().setLayout(layout);
    form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    createNameFields(form.getBody());
    createImapctFields(form.getBody());
    createActiveFields(form.getBody());
    createArchRelSDOMFields(form.getBody());
    createDescEngFields(form.getBody());
    createDescGerFields(form.getBody());
    section.setClient(form);
  }

  /**
   * @param body
   */
  private void createArchRelSDOMFields(final Composite composite) {
    LabelUtil.getInstance().createEmptyLabel(composite);
    LabelUtil.getInstance().createEmptyLabel(composite);
    LabelUtil.getInstance().createLabel(composite, "Architecture Release (SDOM)");
    this.txtArchRelSDOM = new StyledText(composite, SWT.BORDER);
    GridData createGridData = new GridData();
    createGridData.heightHint = 20;
    createGridData.widthHint = 180;
    this.txtArchRelSDOM.setLayoutData(createGridData);
    GridLayout layout = new GridLayout();
    this.txtArchRelSDOM.setLayout(layout);
  }

  /**
   * @param composite Composite
   */
  private void createDescGerFields(final Composite composite) {
    LabelUtil.getInstance().createLabel(composite, "Description(German)");
    this.txtDescGer = new TextBoxContentDisplay(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, 4000,
        GridDataUtil.getInstance().getGridData());
  }

  /**
   * @param composite Composite
   */
  private void createDescEngFields(final Composite composite) {
    LabelUtil.getInstance().createEmptyLabel(composite);
    LabelUtil.getInstance().createEmptyLabel(composite);
    LabelUtil.getInstance().createLabel(composite, "Description(English)");
    this.txtDescEng = new TextBoxContentDisplay(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, 4000,
        GridDataUtil.getInstance().getGridData());
    this.descEngDec = new ControlDecoration(this.txtDescEng, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.descEngDec, "");
    this.txtDescEng.getText().addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        // enable or disable save button based on its content
        enableDiasableSaveBtn();
      }
    });
    LabelUtil.getInstance().createEmptyLabel(composite);
  }

  /**
   * @param composite Composite
   */
  private void createActiveFields(final Composite composite) {
    LabelUtil.getInstance().createLabel(composite, "Active");
    this.activeBtn = new Button(composite, SWT.CHECK);
    if (!this.isEdit && (VERSION_STR + "0.1").equals(this.nameTxt.getText())) {
      // for first version , set active check box as true
      this.activeBtn.setSelection(true);
    }
    if (this.isEdit && this.selectedVersion.isWorkingSet()) {
      // active version cannot be changed for working set
      this.activeBtn.setEnabled(false);
    }


    this.activeBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {

        boolean confirm = FC2WPVersionEditDialog.this.versionsPage.checkActiveVersion(
            FC2WPVersionEditDialog.this.selectedVersion, FC2WPVersionEditDialog.this.activeBtn.getSelection());

        if (confirm) {
          FC2WPVersionEditDialog.this.activeBtn.setSelection(FC2WPVersionEditDialog.this.activeBtn.getSelection());
        }
        else {
          FC2WPVersionEditDialog.this.activeBtn.setSelection(!FC2WPVersionEditDialog.this.activeBtn.getSelection());
        }
      }

    });
  }

  /**
   * @param composite Composite
   */
  private void createImapctFields(final Composite composite) {
    LabelUtil.getInstance().createLabel(composite, "Impact");
    Composite btnComp = this.formToolkit.createComposite(composite);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    btnComp.setLayout(layout);
    btnComp.setLayoutData(GridDataUtil.getInstance().createGridData());
    Button minorBtn = new Button(btnComp, SWT.RADIO);
    minorBtn.setText("Minor");
    minorBtn.setSelection(true);
    minorBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        FC2WPVersionEditDialog.this.majorVersionNum = FC2WPVersionEditDialog.this.latestMajorVersionNum.intValue();
        FC2WPVersionEditDialog.this.minorVersionNum = ((FC2WPVersionEditDialog.this.latestMinorVersionNum) == null ? 0
            : FC2WPVersionEditDialog.this.latestMinorVersionNum.intValue()) + 1;
        FC2WPVersionEditDialog.this.nameTxt.setText(VERSION_STR + FC2WPVersionEditDialog.this.latestMajorVersionNum +
            "." + FC2WPVersionEditDialog.this.minorVersionNum);
      }
    });

    Button majorBtn = new Button(btnComp, SWT.RADIO);
    majorBtn.setText("Major");
    majorBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        FC2WPVersionEditDialog.this.majorVersionNum = FC2WPVersionEditDialog.this.latestMajorVersionNum.intValue() + 1;
        FC2WPVersionEditDialog.this.minorVersionNum = 0;
        FC2WPVersionEditDialog.this.nameTxt.setText(VERSION_STR + FC2WPVersionEditDialog.this.majorVersionNum + "." +
            FC2WPVersionEditDialog.this.minorVersionNum);
      }
    });

    // if it is edit, disable the impact fields
    if (this.isEdit) {
      minorBtn.setEnabled(false);
      minorBtn.setSelection(false);
      majorBtn.setEnabled(false);
    }
  }

  /**
   * create name fields
   *
   * @param composite Composite
   */
  private void createNameFields(final Composite composite) {
    LabelUtil.getInstance().createLabel(composite, "Version Name");
    this.nameTxt = new StyledText(composite, SWT.BORDER);
    // initialising major and minor version numbers
    FC2WPVersion latestVersion = getLatestVersion();
    this.latestMajorVersionNum = latestVersion.getMajorVersNo();
    this.latestMinorVersionNum = latestVersion.getMinorVersNo();
    // major version number is got from the latest version
    this.majorVersionNum = this.latestMajorVersionNum.intValue();
    // minor version number is got from the latest version +1 or if its null , then 0
    this.minorVersionNum = ((this.latestMinorVersionNum) == null ? 0 : this.latestMinorVersionNum.intValue()) + 1;
    // initialising major and minor version numbers
    this.nameTxt.setText(VERSION_STR + this.latestMajorVersionNum + "." + this.minorVersionNum);

    GridData createGridData = new GridData();
    createGridData.heightHint = 20;
    createGridData.widthHint = 180;
    this.nameTxt.setLayoutData(createGridData);
    GridLayout layout = new GridLayout();
    this.nameTxt.setLayout(layout);
    this.nameTxt.setEnabled(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    // creating save and cancel buttons
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    setValuesToFields();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * enable /disable save button based on mandatory field
   */
  public void enableDiasableSaveBtn() {
    if (CommonUtils.isEmptyString(this.txtDescEng.getText().getText().trim())) {
      this.saveBtn.setEnabled(false);
      // show that the field is required
      this.decorators.showReqdDecoration(this.descEngDec, "");
    }
    else {
      this.saveBtn.setEnabled(true);
      // do not show the control decoration
      this.decorators.showErrDecoration(this.descEngDec, null, false);
    }
  }


  /**
   * set values to fields in case of edit
   */
  public void setValuesToFields() {
    if (this.isEdit && (this.selectedVersion != null)) {
      // initialise values from selected version
      this.nameTxt.setText(this.selectedVersion.getVersionName());
      if (this.selectedVersion.isActive()) {
        // if the version is an active version
        this.activeBtn.setSelection(true);
      }
      this.txtDescEng.getText().setText(this.selectedVersion.getDescEng());
      if (!CommonUtils.isEmptyString(this.selectedVersion.getDescGer())) {
        this.txtDescGer.getText().setText(this.selectedVersion.getDescGer());
      }
      if (!CommonUtils.isEmptyString(this.selectedVersion.getArchReleaseSdom())) {
        this.txtArchRelSDOM.setText(this.selectedVersion.getArchReleaseSdom());
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if (this.isEdit) {
      // command for update
      updateVersionWS();
    }
    else {
      FC2WPVersion versn = new FC2WPVersion();
      // set active flags and descriptions
      versn.setName(this.nameTxt.getText());
      versn.setActive(this.activeBtn.getSelection());
      versn.setDescEng(this.txtDescEng.getText().getText());
      versn.setDescGer(this.txtDescGer.getText().getText());
      versn.setArchReleaseSdom(this.txtArchRelSDOM.getText());
      versn.setMajorVersNo(new Long(this.majorVersionNum));
      versn.setMinorVersNo(new Long(this.minorVersionNum));
      versn.setFcwpDefId(this.fc2wpDefID);
      try {
        saveActionWithProgressBar(versn);
      }
      catch (InvocationTargetException e) {
        CDMLogger.getInstance().error(
            ERROR_IN_INVOKING_THREAD_MSG + versn.getId(), e, Activator.PLUGIN_ID);
      }
    }
    super.okPressed();
  }

  /**
   * Add new version Web Service call.
   */
  private void addVersionWS(final FC2WPVersion versn) {

    CDMLogger.getInstance().debug("Updating details of FC2WP version using APIC web server... ");

    // create a webservice client
    final FC2WPVersionServiceClient client = new FC2WPVersionServiceClient();
    try {
      client.create(versn);
      CDMLogger.getInstance().debug("FC2WP version :" + versn.getId() + " has been updated");
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error("Error in updating details of FC2WP version: " + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }
  }

  /**
   * Update version Web Service call.
   */
  private void updateVersionWS() {

    FC2WPVersion versn = this.selectedVersion;
    // set active flags and descriptions
    versn.setActive(this.activeBtn.getSelection());
    versn.setDescEng(this.txtDescEng.getText().getText());
    versn.setDescGer(this.txtDescGer.getText().getText());

    CDMLogger.getInstance().debug("Updating details of FC2WP version using APIC web server... ");
    // create a webservice client
    final FC2WPVersionServiceClient client = new FC2WPVersionServiceClient();
    try {
      client.update(versn);
      CDMLogger.getInstance().debug("FC2WP version :" + versn.getId() + " has been updated");
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error("Error in updating details of FC2WP version: " + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }
  }

  /**
   * @return the working set of a FC2WPVersion
   */
  public FC2WPVersion getWorkingSet() {
    for (FC2WPVersion version : this.allVersions) {
      if ((version.getMajorVersNo() == 0L) && (version.getMinorVersNo() == null)) {
        // if major version is zero and minor version is null
        return version;
      }
    }
    return null;
  }

  /**
   * @return latest version
   */
  public FC2WPVersion getLatestVersion() {
    FC2WPVersion latestVersion = getWorkingSet();
    synchronized (this) {

      CDMLogger.getInstance().debug("Finding latest version of FC2WP : ");
      int numOfVers = this.allVersions.size();
      Long higheshMajor = 0L;
      Long highestMinor = 0L;
      // iterate and find out the highest major number
      for (FC2WPVersion versn : this.allVersions) {
        if (ApicUtil.compareLong(versn.getMajorVersNo(), higheshMajor) > 0) {
          higheshMajor = versn.getMajorVersNo();
        }
      }
      // iterate and find out the highest major for the identified highest major number
      for (FC2WPVersion versn : this.allVersions) {
        if (numOfVers == VERSION_SIZE_1) {
          latestVersion = versn;
          break;
        }
        if (CommonUtils.isNull(versn.getMinorVersNo())) {
          continue;
        }
        else if (CommonUtils.isEqual(versn.getMajorVersNo(), higheshMajor) &&
            (ApicUtil.compareLong(versn.getMinorVersNo(), highestMinor) >= 0)) {
          highestMinor = versn.getMinorVersNo();
          latestVersion = versn;
        }
      }
      CDMLogger.getInstance().debug("Latest FC2WP Version is : " + latestVersion.getId());
    }
    return latestVersion;
  }

  /**
   * Save action with progress bar.
   */
  public void saveActionWithProgressBar(final FC2WPVersion versn) throws InvocationTargetException {
    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
        try {
          dialog.run(true, true, new IRunnableWithProgress() {

            @Override
            public void run(final IProgressMonitor monitor) {

              monitor.beginTask("Creating new Version of FC2WP...", 100);
              monitor.worked(20);
              addVersionWS(versn);
              monitor.worked(100);
              monitor.done();
            }
          });
        }
        catch (InvocationTargetException e) {
          CDMLogger.getInstance()
              .error(ERROR_IN_INVOKING_THREAD_MSG + versn.getId(), e);
        }
        catch (InterruptedException e) {
          CDMLogger.getInstance()
              .error(ERROR_IN_INVOKING_THREAD_MSG + versn.getId(), e);
        }

      }
    });
  }

}
