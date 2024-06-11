/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author bru2cob
 */
public class OverwriteA2lAssignmentDialog extends AbstractDialog {

  /**
   *
   */
  private static final String OVERWRITE_A2L_ASSIGNMENTS = "Overwrite A2L Assignments";

  /**
   * a2l file version dialog
   */
  private final A2LFileVersionDialog a2lDialog;
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
  private Section par2WpSection;
  /**
   * Form instance
   */
  private Form par2WpForm;
  /**
   * over write all radio button
   */
  private Button overwriteRadio;
  /**
   * over only default radio button
   */
  private Button overwriteDefaultWpRadio;
  private final String filesToOverwrite;
  // yes button
  private Button yesRadio;
  private boolean cancelPressed;


  /**
   * Add new user button instance
   */
  private Button okBtn;
  private Map<String, Boolean> dialogData;

  /**
   * @param parentShell parent Shell
   * @param a2lDialog A2LFileVersionDialog
   * @param filesToOverwrite A2L files, in which data is being overwritten
   */
  public OverwriteA2lAssignmentDialog(final Shell parentShell, final A2LFileVersionDialog a2lDialog,
      final String filesToOverwrite) {

    super(parentShell);
    this.a2lDialog = a2lDialog;
    this.filesToOverwrite = filesToOverwrite;
  }

  /**
   * @param parentShell parent Shell
   * @param dialogData data variables in this dialog
   * @param filesToOverwrite A2L files, in which data is being overwritten
   */
  public OverwriteA2lAssignmentDialog(final Shell parentShell, final Map<String, Boolean> dialogData,
      final String filesToOverwrite) {

    super(parentShell);
    this.a2lDialog = null;
    this.dialogData = dialogData;
    this.filesToOverwrite = filesToOverwrite;
  }


  /**
   * create contents
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(OVERWRITE_A2L_ASSIGNMENTS);
    setMessage(this.filesToOverwrite);
    return contents;
  }

  /**
   * configure the shell and set the title
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell name
    newShell.setText(OVERWRITE_A2L_ASSIGNMENTS);
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
    // create par2wp assignment section
    createPar2WpSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
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
   *
   */
  private void createPar2WpSection() {
    this.par2WpSection =
        SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), OVERWRITE_A2L_ASSIGNMENTS);
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

    new Label(this.par2WpForm.getBody(), SWT.NONE)
        .setText("Overwrite the Working Set with the Assignments from the selected A2L?");

    Composite radioComp = getFormToolkit().createComposite(this.par2WpForm.getBody());
    radioComp.setLayout(gridLayout);
    radioComp.setLayoutData(GridDataUtil.getInstance().getGridData());

    // No option
    Button noRadio = new Button(radioComp, SWT.RADIO);
    noRadio.setText("No");
    noRadio.setSelection(true);
    noRadio.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        OverwriteA2lAssignmentDialog.this.okBtn.setEnabled(false);
      }
    });

    // Yes option
    this.yesRadio = new Button(radioComp, SWT.RADIO);
    this.yesRadio.setText("Yes");

    Composite comp = getFormToolkit().createComposite(this.par2WpForm.getBody());
    comp.setLayout(gridLayout);
    comp.setLayoutData(GridDataUtil.getInstance().getGridData());

    // Overwrite all option
    this.overwriteRadio = new Button(comp, SWT.RADIO);
    this.overwriteRadio.setText("Overwrite ALL");
    this.overwriteRadio.setSelection(true);
    this.overwriteRadio.setEnabled(false);

    // Overwrite default only option
    this.overwriteDefaultWpRadio = new Button(comp, SWT.RADIO);
    this.overwriteDefaultWpRadio.setText("Overwrite ONLY Default");
    this.overwriteDefaultWpRadio.setEnabled(false);

    // Add listener to YES option to change state of other options
    this.yesRadio.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        OverwriteA2lAssignmentDialog.this.overwriteRadio.setEnabled(true);
        OverwriteA2lAssignmentDialog.this.overwriteDefaultWpRadio.setEnabled(true);
        OverwriteA2lAssignmentDialog.this.okBtn.setEnabled(true);
      }
    });

    // Add listener to NO option to change state of other options
    noRadio.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        OverwriteA2lAssignmentDialog.this.overwriteRadio.setEnabled(false);
        OverwriteA2lAssignmentDialog.this.overwriteDefaultWpRadio.setEnabled(false);
      }
    });


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    // if dialog object is passed
    if (this.yesRadio.getSelection() && (this.a2lDialog != null)) {
      this.a2lDialog.setCanOverWrite(true);
      this.a2lDialog.setOverWriteAllAssignments(this.overwriteRadio.getSelection());
    }
    else if (this.yesRadio.getSelection() && (this.dialogData != null)) {
      // set variable values
      this.dialogData.put("canOverWrite", true);
      this.dialogData.put("overWriteAllAssignments", this.overwriteRadio.getSelection());
    }

    super.okPressed();
  }

  /**
   * Creates the buttons for the button bar
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, "Proceed", false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void cancelPressed() {
    this.cancelPressed = true;
    super.cancelPressed();
  }


  /**
   * @return the cancelPressed
   */
  public boolean isCancelPressed() {
    return this.cancelPressed;
  }

}
