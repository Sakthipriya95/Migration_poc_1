/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.ResponsibilitySection;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * @author elm1cob
 */
public class SetWpRespDialog extends AbstractDialog {

  private FormToolkit formToolkit;

  private Composite top;

  private Composite composite;

  private ResponsibilitySection responsibilitySection;

  private Button saveBtn;

  private final A2LWPInfoBO a2lWpInfoBo;

  private A2lWpResponsibility respPalObj;

  private boolean isCustomizeActionFlag;

  private A2lResponsibility a2lResp;

  private WpRespType respType;

  private String description;


  /**
   * @param parentShell Shell
   * @param a2lWpResponsibility - a2lWpResponsibility
   * @param wpInfoBo A2lWpInfoBo
   */
  public SetWpRespDialog(final Shell parentShell, final A2lWpResponsibility a2lWpResponsibility,
      final A2LWPInfoBO wpInfoBo) {
    super(parentShell);
    this.a2lWpInfoBo = wpInfoBo;
    this.respPalObj = a2lWpResponsibility.clone();
  }

  /**
   * @param parentShell Shell
   * @param isCustomizeAction boolean
   * @param wpInfoBo A2lWPInfoBo
   */
  public SetWpRespDialog(final Shell parentShell, final boolean isCustomizeAction, final A2LWPInfoBO wpInfoBo) {
    super(parentShell);
    this.a2lWpInfoBo = wpInfoBo;
    this.isCustomizeActionFlag = isCustomizeAction;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Workpackage Responsibility");
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
    initializeDialogUnits(parent);
    final Control contents = super.createContents(parent);
    // Set the title
    setTitle("Set Responsibility");
    // Set the message
    setMessage("Please select responsibility", IMessageProvider.INFORMATION);
    return contents;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
   */
  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    createComposite();
    return this.top;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createRespSection();
    this.composite.setLayoutData(gridData);
  }

  /**
   * This method initializes section
   */
  private void createRespSection() {
    this.responsibilitySection =
        new ResponsibilitySection(this.composite, getFormToolkit(), true, this.respPalObj, this.a2lWpInfoBo);
    this.responsibilitySection.createRespSection(getShell(), null);
    addRespButtonListeners();
    setRespOption();
  }

  /**
   * Radio button listeners for Responsibility section
   */
  private void addRespButtonListeners() {
    boschBtnListener();
    customerBtnListener();
    otherBtnListener();
    respUserBtnListeners();
  }

  /**
   * Open search user dialog based on the selection
   */
  private void respUserBtnListeners() {
    this.responsibilitySection.getSearchButton().addSelectionListener(new SelectionAdapter() {

      /**
       * re {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        SetWpRespDialog.this.responsibilitySection.openUsersDialog();
        checkSaveEnable();
      }

    });

    this.responsibilitySection.getClearUserButton().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        SetWpRespDialog.this.responsibilitySection.getUserNameTextBox().setText("");
        SetWpRespDialog.this.responsibilitySection.getClearUserButton().setEnabled(false);
        checkSaveEnable();
      }


    });
  }

  /**
   * Others radio button listener
   */
  private void otherBtnListener() {
    this.responsibilitySection.getOthersButton().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (SetWpRespDialog.this.responsibilitySection.getOthersButton().getSelection()) {
          SetWpRespDialog.this.responsibilitySection.getUserNameTextBox().setText("");
          SetWpRespDialog.this.responsibilitySection.enableUserSearch(true);
          checkSaveEnable();
        }
      }
    });
  }

  /**
   * Customer Radio button listener
   */
  private void customerBtnListener() {
    this.responsibilitySection.getCustomerButton().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (SetWpRespDialog.this.responsibilitySection.getCustomerButton().getSelection()) {
          SetWpRespDialog.this.responsibilitySection.getUserNameTextBox().setText("");
          SetWpRespDialog.this.responsibilitySection.enableUserSearch(true);
          checkSaveEnable();

        }
      }
    });

  }

  /**
   * Bosch radio button listener
   */
  private void boschBtnListener() {
    this.responsibilitySection.getBoschButton().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (SetWpRespDialog.this.responsibilitySection.getBoschButton().getSelection()) {
          SetWpRespDialog.this.responsibilitySection.getUserNameTextBox().setText("");
          SetWpRespDialog.this.responsibilitySection.enableUserSearch(true);
          checkSaveEnable();

        }
      }
    });
  }

  /**
   * Check for enabling save
   */
  private void checkSaveEnable() {
    checkforREspTypeAndName();
    this.saveBtn.setEnabled(this.responsibilitySection.isRespSelected() && checkforREspTypeAndName());
  }

  /**
   * Check if the Resp type and name are same
   */
  private boolean checkforREspTypeAndName() {
    String uiText = this.responsibilitySection.getUserNameTextBox().getText();
    if (uiText.isEmpty()) {
      uiText = null;
    }
    boolean descDiff = !CommonUtils.isEqual(uiText, this.description);
    if ((this.respType == WpRespType.RB) && this.responsibilitySection.getBoschButton().getSelection()) {
      return descDiff;
    }
    if ((this.respType == WpRespType.CUSTOMER) && this.responsibilitySection.getCustomerButton().getSelection()) {
      return descDiff;

    }
    if ((this.respType == WpRespType.OTHERS) && this.responsibilitySection.getOthersButton().getSelection()) {
      return descDiff;
    }
    return true;
  }

  /**
   * @param selectedA2lResp
   */
  private void setA2lRespToRespPal(final A2lResponsibility selectedA2lResp) {
    this.a2lResp = selectedA2lResp;
    if ((null != selectedA2lResp) && !this.isCustomizeActionFlag) {
      this.respPalObj.setA2lRespId(selectedA2lResp.getId());
    }
  }


  private void setRespOption() {

    this.responsibilitySection.resetRadioButtons();

    if ((this.respPalObj != null) && (null != this.respPalObj.getA2lRespId())) {

      A2lResponsibility a2lResp =
          this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(this.respPalObj.getA2lRespId());
      this.respType = A2lResponsibilityCommon.getRespType(a2lResp);

      switch (this.respType) {
        case RB:
          setBoschRadioButton(a2lResp);
          break;
        case CUSTOMER:
          setCustomerRadioButton(a2lResp);
          break;
        case OTHERS:
          setOtherRespRadioButton(a2lResp);
          break;
        default:
          break;
      }
    }
    else {
      // Default selection
      this.responsibilitySection.getBoschButton().setSelection(true);
    }
    this.responsibilitySection.enableUserSearch(true);
    this.responsibilitySection.resizeDialog();
  }

  /**
   * Set Others responsible details
   *
   * @param a2lResp
   */
  private void setOtherRespRadioButton(final A2lResponsibility a2lResp) {
    this.responsibilitySection.getOthersButton().setSelection(true);
    this.description = this.a2lWpInfoBo.getLFullName(a2lResp);
    this.responsibilitySection.getUserNameTextBox().setText(this.description);
  }


  /**
   * Set Bosch responsible details
   *
   * @param a2lResp
   */
  private void setBoschRadioButton(final A2lResponsibility a2lResp) {
    this.responsibilitySection.getBoschButton().setSelection(true);
    if (null != a2lResp.getUserId()) {
      this.description =
          this.a2lWpInfoBo.getA2lResponsibilityModel().getUserMap().get(a2lResp.getUserId()).getDescription();
      this.responsibilitySection.getUserNameTextBox().setText(this.description);
    }

  }

  /**
   * Set Customer responsible details
   *
   * @param a2lResp
   */
  private void setCustomerRadioButton(final A2lResponsibility a2lResp) {
    this.responsibilitySection.getCustomerButton().setSelection(true);

    if (!WpRespType.CUSTOMER.getDispName().equals(a2lResp.getName())) {
      this.description = this.a2lWpInfoBo.getLFullName(this.respPalObj);
      this.responsibilitySection.getUserNameTextBox().setText(this.description);

    }
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
   * set Responsibility with out user name
   *
   * @param userType
   */
  private void setRespWOUserName(final String userType) {
    this.a2lResp = SetWpRespDialog.this.a2lWpInfoBo.getA2lResponsibilityModel().getDefaultA2lRespMap().get(userType);
    if (!this.isCustomizeActionFlag) {
      this.respPalObj.setA2lRespId(this.a2lResp.getId());
    }
  }


  private void setA2lRespObj() {
    if (CommonUtils.isEmptyString(this.responsibilitySection.getUserNameTextBox().getText())) {
      if (this.responsibilitySection.getBoschButton().getSelection()) {
        setRespWOUserName(WpRespType.RB.getCode());
      }
      if (this.responsibilitySection.getCustomerButton().getSelection()) {
        setRespWOUserName(WpRespType.CUSTOMER.getCode());
      }
      return;
    }

    if (this.responsibilitySection.getBoschButton().getSelection()) {
      this.a2lResp = this.a2lWpInfoBo.createBoschResponsible(this.responsibilitySection.getBoschUser());
      if (!this.isCustomizeActionFlag) {
        this.respPalObj.setA2lRespId(this.a2lResp.getId());
      }
    }
    else {
      setA2lRespToRespPal(this.responsibilitySection.getA2lResp());
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    setA2lRespObj();
    if (!this.isCustomizeActionFlag) {
      this.a2lWpInfoBo.updateA2lWpRespPal(this.respPalObj);
    }
    super.okPressed();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }


  /**
   * @return the responsibilitySection
   */
  public ResponsibilitySection getResponsibilitySection() {
    return this.responsibilitySection;
  }


  /**
   * @return the a2lResp
   */
  public A2lResponsibility getA2lResp() {
    return this.a2lResp;
  }

}
