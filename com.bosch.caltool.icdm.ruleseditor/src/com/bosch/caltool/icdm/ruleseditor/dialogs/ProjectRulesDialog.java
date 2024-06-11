/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author bru2cob
 */
public class ProjectRulesDialog extends AbstractDialog {

  /**
   * Number of cols in the main form
   */
  private static final int FORM_COLS = 2;
  /**
   * Form instance
   */
  private Form form;
  /**
   * Add flag
   */
  private final boolean addFlag;
  /**
   * Title text
   */
  private String strTitle;
  /**
   * Name eng text
   */
  private Text nameText;
  /**
   * desc eng text
   */
  private Text descEngText;
  /**
   * german eng text
   */
  private Text descGerText;
  /**
   * Control decoration instance
   */
  private ControlDecoration nameDecor;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * Section instance
   */
  private Section section;
  /**
   * Add new user button instance
   */
  private Button saveBtn;

  /**
   * @param activeShell
   * @param addFlag
   */
  public ProjectRulesDialog(final Shell activeShell, final boolean addFlag) {
    super(activeShell);
    this.addFlag = addFlag;

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

    setTitle(this.strTitle);
    setMessage("Enter the details", IMessageProvider.INFORMATION);

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // set the title
    if (this.addFlag) {
      this.strTitle = "Add a Project Specific Rules set";
    }
    else {
      this.strTitle = "Edit a Project Specific Rules set";
    }
    newShell.setText("Project Specific Rules");
    super.configureShell(newShell);
    super.setHelpAvailable(true);
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
    // create composite
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
    createSection();
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
   * This method initializes section
   */
  private void createSection() {

    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), this.strTitle);
    // create form
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {

    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    Decorators decorators = new Decorators();
    this.form = getFormToolkit().createForm(this.section);
    GridLayout layout = new GridLayout();
    this.form.getBody().setLayout(layout);
    layout.numColumns = FORM_COLS;
    getFormToolkit().createLabel(this.form.getBody(), "Name");
    this.nameText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.nameText.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.nameText.setFocus();

    this.nameDecor = new ControlDecoration(this.nameText, SWT.LEFT | SWT.TOP);
    decorators.showReqdDecoration(this.nameDecor, "This field is mandatory.");


    this.nameText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        checkSaveBtnEnable();
      }
    });


    getFormToolkit().createLabel(this.form.getBody(), "Description(English)");
    this.descEngText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.descEngText.setLayoutData(GridDataUtil.getInstance().getTextGridData());

    ControlDecoration descDecor = new ControlDecoration(this.descEngText, SWT.LEFT | SWT.TOP);
    decorators.showReqdDecoration(descDecor, "This field is mandatory.");
    this.descEngText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        checkSaveBtnEnable();
      }
    });

    getFormToolkit().createLabel(this.form.getBody(), "Description(German)");
    this.descGerText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.descGerText.setLayoutData(GridDataUtil.getInstance().getTextGridData());

    this.descGerText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        checkSaveBtnEnable();
      }
    });

  }


  /**
   * This method validates text fields
   *
   * @return boolean
   */
  private boolean validateFields() {
    String nameCp = this.nameText.getText();
    String descEng = this.descEngText.getText();
    return !CommonUtils.isEmptyString(nameCp) && !CommonUtils.isEmptyString(descEng);

  }

  /**
   * Validates save button enable or disable
   */
  private void checkSaveBtnEnable() {
    if (validateFields() && (this.saveBtn != null)) {
      this.saveBtn.setEnabled(true);
    }
    else if (this.saveBtn != null) {
      this.saveBtn.setEnabled(false);
    }
  }

}
