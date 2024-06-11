/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizard;
import com.bosch.caltool.icdm.ruleseditor.wizards.pages.CreateEditRuleWizardPage;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author bru2cob
 */
public class BitWiseLimitConfigDialog extends PopupDialog {

  /**
   * next level composite
   */
  Composite composite;
  /**
   * Form instance
   */
  private Form form;

  /**
   * FormToolkit
   */
  private FormToolkit formToolkit;
  /**
   * The listener installed in order to close the popup.
   */
  private BitwiseRulePopupCloserListener popupCloser;

  /**
   * Instance of edit rule dialog
   */
  private final RuleInfoSection ruleInfoSection;

  /**
   * @return the ruleInfoSection
   */
  public RuleInfoSection getRuleInfoSection() {
    return this.ruleInfoSection;
  }

  /**
   * Instance of bitwiseLimitConfig dialog
   */
  private BitwiseLimitConfig bitwiseConfig;

  /**
   * Section instance
   */
  private Section section;
  /**
   * Bit selected
   */
  private boolean bitSelected;

  /**
   * old bitiwse text value
   */
  private String oldBitWiseText;

  private ReviewRule rule;

  /**
   * @param editRuleDialog instance of EditRuleDialog
   * @param wizardPage
   */
  public BitWiseLimitConfigDialog(final EditRuleDialog editRuleDialog, final CreateEditRuleWizardPage wizardPage) {
    super(new Shell(), SWT.RESIZE | SWT.ON_TOP, true, false, false, false, false, null, null);

    if (null == editRuleDialog) {
      this.ruleInfoSection = wizardPage.getRuleInfoSection();
      AddNewConfigWizard wizard = (AddNewConfigWizard) wizardPage.getWizard();
      if (wizard.getWizardData().isSameBitWise()) {
        this.rule = this.ruleInfoSection.getSelectedCdrRule();
      }
    }
    else {
      this.ruleInfoSection = editRuleDialog.getRuleInfoSection();
      this.rule = this.ruleInfoSection.getSelectedCdrRule();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Color getBackground() {
    // grey background for the pop up dialog
    return Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    Composite top = new Composite(parent, SWT.NONE);
    createComposite(top);
    top.setLayout(new GridLayout());
    top.setLayoutData(GridDataUtil.getInstance().getGridData());
    return top;
  }

  /**
   * create composite for dialog
   *
   * @param top Composite
   */
  private void createComposite(final Composite top) {
    this.composite = getFormToolkit().createComposite(top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(false);

  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Set the bit values");
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(new GridLayout());
    BitwiseLimitTable bitwiseTable = new BitwiseLimitTable(this);
    bitwiseTable.createBitWiseTable();
  }


  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * Closes this popup. This method is extended to remove the control listener.
   *
   * @return <code>true</code> if the window is (or was already) closed, and <code>false</code> if it is still open
   */
  @Override
  public boolean close() {
    if ((getShell() == null) || getShell().isDisposed()) {
      return true;
    }
    if (this.popupCloser != null) {
      this.popupCloser.removeAllListeners();
    }
    boolean ret = super.close();
    if (this.bitSelected) {
      this.ruleInfoSection.getBitwiseRuleTxt().setText(this.bitwiseConfig.getBitValueDisplayString());
    }
    else if (null != this.oldBitWiseText) {
      this.ruleInfoSection.getBitwiseRuleTxt().setText(this.oldBitWiseText);
    }
    return ret;
  }

  /**
   * Opens this ContentProposalPopup. This method is extended in order to add the control listener when the popup is
   * opened and to invoke the secondary popup if applicable.
   *
   * @return the return code
   * @see org.eclipse.jface.window.Window#open()
   */
  @Override
  public int open() {
    int value = super.open();
    if (this.popupCloser == null) {
      this.popupCloser = new BitwiseRulePopupCloserListener(this);
    }
    this.popupCloser.installAllListeners();
    adjustBounds();
    return value;
  }


  /**
   * @return the bitWiseConfig
   */
  public BitwiseLimitConfig getBitWiseConfig() {
    return this.bitwiseConfig;
  }

  /**
   * @param bitwiseText
   */
  public void setOldBitWiseValue(final String bitwiseText) {
    this.oldBitWiseText = bitwiseText;

  }

  /**
   * @param bitSelected the bitSelected to set
   */
  public void setBitSelected(final boolean bitSelected) {
    this.bitSelected = bitSelected;
  }


  /**
   * @return the form
   */
  public Form getForm() {
    return this.form;
  }

  /**
   * Sets input for the light weight pop up dialog
   */
  public void setInput() {
    this.bitwiseConfig = new BitwiseLimitConfig(this.rule);
    this.bitwiseConfig.createBits();
  }

  /**
   * Sets input for the light weight pop up dialog
   */
  public void resetInput() {
    this.bitwiseConfig = new BitwiseLimitConfig(this.rule);
    this.bitwiseConfig.resetBitInput();
  }

  /**
   * @param selectedCdrRule rule
   */
  public void setSelectedRule(final ReviewRule selectedCdrRule) {
    this.rule = selectedCdrRule;
  }

}
