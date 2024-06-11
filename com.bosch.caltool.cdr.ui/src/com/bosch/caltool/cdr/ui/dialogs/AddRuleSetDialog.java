/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;


// TODO: Auto-generated Javadoc
/**
 * The Class AddRuleSetDialog.
 *
 * @author gge6cob
 */
public class AddRuleSetDialog extends AbstractDialog {

  /** The create ruleset. */
  private final String CREATE_RULESET = "Create RuleSet";

  /** The create ruleset msg. */
  private final String CREATE_RULESET_MSG = "Create a new RuleSet";

  /** The form toolkit. */
  private FormToolkit formToolkit;

  /** The top. */
  private Composite top;

  /** The composite. */
  private Composite composite;

  /** The section. */
  private Section section;

  /** The form. */
  private Form form;

  /** The name txt. */
  private Text nameTxt;

  /** The no of col. */
  private final int NO_OF_COL = 3;

  /** The dec name txt. */
  private ControlDecoration decNameTxt;

  /** The desc eng txt. */
  private Control descEngTxt;

  /** desc length. */
  private static final int DESC_LEN = 4000;

  /** the height size for the description field. */
  private static final int HEIGHT_FOR_DESC_FIELD = 40;

  /**
   * Instantiates a new adds the rule set dialog.
   *
   * @param parentShell the parent shell
   * @param dialog the dialog
   */
  public AddRuleSetDialog(final Shell parentShell) {
    super(parentShell);
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
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new org.eclipse.swt.layout.GridLayout());
    this.top.setLayoutData(GridDataUtil.getInstance().getGridData());
    return this.top;
  }

  /**
   * Creates the dialog's contents.
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    Control content = super.createContents(parent);
    // Set the title
    setTitle(this.CREATE_RULESET);

    // Set the message
    setMessage(this.CREATE_RULESET_MSG);

    createComposite();
    return content;
  }

  /**
   * Creates the composite.
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    createSection();
  }

  /**
   * Creates the section.
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Enter the Ruleset details", false);
    createForm();
    this.section.setClient(this.form);
  }


  /**
   * Creates the form.
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = this.NO_OF_COL;

    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getTextGridData());

    GridData txtGridData = GridDataUtil.getInstance().getTextGridData();

    getFormToolkit().createLabel(this.form.getBody(), ApicConstants.EMPTY_STRING);
    getFormToolkit().createLabel(this.form.getBody(), "Name :");
    this.nameTxt = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.nameTxt.setLayoutData(txtGridData);
    this.decNameTxt = new ControlDecoration(this.nameTxt, SWT.LEFT | SWT.TOP);
    CdrUIConstants.DECORATOR.showReqdDecoration(this.decNameTxt, IUtilityConstants.MANDATORY_MSG);

    getFormToolkit().createLabel(this.form.getBody(), ApicConstants.EMPTY_STRING);
    getFormToolkit().createLabel(this.form.getBody(), "Description (English)");
    TextBoxContentDisplay textBoxObj1 = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, DESC_LEN, getDescGridData());
    this.descEngTxt = textBoxObj1.getText();
    ControlDecoration descEngDec = new ControlDecoration(this.descEngTxt, SWT.LEFT | SWT.TOP);
    CdrUIConstants.DECORATOR.showReqdDecoration(descEngDec, IUtilityConstants.MANDATORY_MSG);
    textBoxObj1.setLayoutData(getDescGridData());

    getFormToolkit().createLabel(this.form.getBody(), ApicConstants.EMPTY_STRING);
    getFormToolkit().createLabel(this.form.getBody(), "Description (German)");
    TextBoxContentDisplay textBoxObj2 = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, DESC_LEN, getDescGridData());
    textBoxObj2.setLayoutData(getDescGridData());
  }

  /**
   * Gets the desc grid data.
   *
   * @return the desc text
   */
  private GridData getDescGridData() {
    GridData descGridData = GridDataUtil.getInstance().getTextGridData();
    descGridData.heightHint = HEIGHT_FOR_DESC_FIELD;
    return descGridData;
  }
}
