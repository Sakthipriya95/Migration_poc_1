/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * @author elm1cob
 */
public class ResponsibilitySection {

  private Section respSection;

  private Composite composite;

  private final FormToolkit formToolKit;

  private Text userName;

  private Label userNameLabel;

  private Shell shell;

  private final boolean wpDefnFlag;

  private Button inheritWpRespBtn;

  private Group respTypeRadioGrp;

  private Long wpRespId;

  private A2lWpParamMapping selParamMapping;

  private A2lWpResponsibility selRespPalObj;

  private final A2LWPInfoBO a2lWpInfoBo;

  private Button bosch;

  private Button boschDept;

  private Button customer;

  private Button others;

  private Button searchButton;

  private Button clearUserButton;

  private A2lResponsibility a2lResp;

  private A2LWpParamInfo a2lParamInfo;

  private User boschUser;
  /**
  *
  */
  public static final String ENTER_BOSCH_RESP = "Bosch User";
  /**
  *
  */
  public static final String ENTER_OTHER_RESP = "Other";
  /**
   * String constant for Bosch Department Radio button
   */
  public static final String ENTER_BOSCH_DEPT_RESP = "Bosch Group/Department";
  /**
  *
  */
  public static final String ENTER_CUSTOMER_RESP = "Customer";

  /**
   * @param composite Composite
   * @param formToolKit FormToolKit
   * @param isWpDefnPage boolean
   * @param selectObject Object
   * @param wpInfoBo A2lWpInfoBo
   */
  public ResponsibilitySection(final Composite composite, final FormToolkit formToolKit, final boolean isWpDefnPage,
      final Object selectObject, final A2LWPInfoBO wpInfoBo) {
    super();
    this.composite = composite;
    this.formToolKit = formToolKit;
    this.wpDefnFlag = isWpDefnPage;
    if (selectObject != null) {
      setSelectedObject(selectObject);
    }
    this.a2lWpInfoBo = wpInfoBo;
  }


  private void setSelectedObject(final Object selection) {
    if (selection instanceof A2lWpParamMapping) {
      this.selParamMapping = (A2lWpParamMapping) selection;
      this.wpRespId = this.selParamMapping.getParA2lRespId();
    }
    else if (selection instanceof A2lWpResponsibility) {
      this.selRespPalObj = (A2lWpResponsibility) selection;
      this.wpRespId = this.selRespPalObj.getA2lRespId();
    }
  }

  /**
   * This method intializes section
   *
   * @param currentShell Shell
   * @param wpParamInfo A2LWpParamInfo
   */
  public void createRespSection(final Shell currentShell, final A2LWpParamInfo wpParamInfo) {
    GridData gridData5 = GridDataUtil.getInstance().getGridData();
    this.respSection =
        this.formToolKit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.respSection.setExpanded(true);
    this.respSection.setLayoutData(gridData5);
    this.respSection.getDescriptionControl().setEnabled(false);
    this.shell = currentShell;
    this.a2lParamInfo = wpParamInfo;
    createRespForm();
    this.respSection.setClient(this.composite);
    this.respSection.setText("Set Responsibility");

  }

  /**
   * This method intializes section
   *
   * @param currentShell Shell
   */
  public void createRespSection(final Shell currentShell) {
    GridData gridData5 = GridDataUtil.getInstance().getGridData();
    this.respSection =
        this.formToolKit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.respSection.setExpanded(true);
    this.respSection.setLayoutData(gridData5);
    this.respSection.getDescriptionControl().setEnabled(false);
    this.shell = currentShell;

    createRespForm();
    this.respSection.setClient(this.composite);
    this.respSection.setText("Set Responsibility");

  }

  /**
   * This method creates responsibility form
   */
  private void createRespForm() {

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.composite = this.formToolKit.createComposite(this.respSection, SWT.NONE);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(gridData);

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 4;

    if (!this.wpDefnFlag) {
      this.inheritWpRespBtn = new Button(this.composite, SWT.CHECK | SWT.RIGHT);
      this.inheritWpRespBtn.setText("Inherit from Work Package definition");
      this.inheritWpRespBtn.setEnabled(true);

    }

    this.respTypeRadioGrp = new Group(this.composite, SWT.READ_ONLY);
    this.respTypeRadioGrp.setLayout(gridLayout);
    this.respTypeRadioGrp.setLayoutData(gridData);

    addBoschRespButton(this.respTypeRadioGrp);
    addBoschDeptRespButton(this.respTypeRadioGrp);
    addCustomerRespButton(this.respTypeRadioGrp);
    addOthersButton(this.respTypeRadioGrp);

    createUserNameForm(gridLayout);

    this.respSection.setClient(this.composite);
  }

  /**
   * @param createSetResp
   * @param gridLayout
   */
  private void createUserNameForm(final GridLayout gridLayout) {

    Composite userSelect = this.formToolKit.createComposite(this.composite, SWT.NONE);
    userSelect.setLayout(gridLayout);
    userSelect.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.userNameLabel = this.formToolKit.createLabel(userSelect, "User Name ");
    this.userName = this.formToolKit.createText(userSelect, null, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
    this.userName.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.userName.setEnabled(false);

    this.searchButton = new Button(userSelect, SWT.PUSH);
    this.searchButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.PIDC_SEARCH_16X16));
    this.searchButton.setToolTipText("Search User Name");
    this.searchButton.setEnabled(false);

    this.clearUserButton = new Button(userSelect, SWT.PUSH);
    this.clearUserButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.CLEAR_HIST_16X16));
    this.clearUserButton.setToolTipText("Clear");
    this.clearUserButton.setEnabled(false);

  }

  /**
   * Open Bosch or nonBosch Users dialog
   */
  public void openUsersDialog() {
    if (this.bosch.getSelection()) {
      this.boschUser = boschUserRespDialog();
    }
    else if (this.boschDept.getSelection()) {
      this.a2lResp = selectWpRespUserDetails(WpRespType.RB);
    }
    else if (this.customer.getSelection()) {
      this.a2lResp = selectWpRespUserDetails(WpRespType.CUSTOMER);
    }
    else if (this.others.getSelection()) {
      this.a2lResp = selectWpRespUserDetails(WpRespType.OTHERS);
    }
  }


  /**
   * @param inheritFlag boolean
   */
  public void inheritWpResp(final boolean inheritFlag) {
    this.respTypeRadioGrp.setEnabled(!inheritFlag);
    Control[] children = this.respTypeRadioGrp.getChildren();
    for (Control child : children) {
      ((Button) child).setSelection(false);
    }
  }

  /**
   * enable user search option
   *
   * @param flag boolean
   */
  public void enableUserSearch(final boolean flag) {
    this.userName.setEnabled(flag);
    this.searchButton.setEnabled(flag);
    this.userNameLabel.setEnabled(flag);
  }

  /**
   * @param respGrp
   */
  private void addBoschRespButton(final Group respGrp) {
    this.bosch = new Button(respGrp, SWT.RADIO);
    this.bosch.setText(ENTER_BOSCH_RESP);
    this.bosch.setEnabled(true);
  }

  /**
   * @param respGrp
   */
  private void addOthersButton(final Group respGrp) {
    this.others = new Button(respGrp, SWT.RADIO);
    this.others.setText(ENTER_OTHER_RESP);
    this.others.setEnabled(true);
  }


  /**
   * @param respGrp
   */
  private void addBoschDeptRespButton(final Group respGrp) {
    this.boschDept = new Button(respGrp, SWT.RADIO);
    this.boschDept.setText(ENTER_BOSCH_DEPT_RESP);
    this.boschDept.setEnabled(true);
  }

  /**
   * @param respGrp
   */
  private void addCustomerRespButton(final Group respGrp) {

    this.customer = new Button(respGrp, SWT.RADIO);
    this.customer.setText(ENTER_CUSTOMER_RESP);
    this.customer.setEnabled(true);
  }


  /**
   * Add bosch user dialog method
   *
   * @param wpRespTypeObj
   */
  private A2lResponsibility selectWpRespUserDetails(final WpRespType wpRespType) {
    AddWpRespNonBoschDialog respDialog;
    if (null != this.a2lWpInfoBo) {
      respDialog = new AddWpRespNonBoschDialog(Display.getCurrent().getActiveShell(),
          ResponsibilitySection.this.a2lWpInfoBo, wpRespType, getA2lRespObj());


      respDialog.open();
      if (respDialog.getSelectedA2lResp() != null) {
        // changed from getAliasName() to getName()
        String name = respDialog.getSelectedA2lResp().getName();
        this.userName.setText(name != null ? name : "");
        return respDialog.getSelectedA2lResp();
      }
    }
    return null;
  }

  /**
   * Add bosch user dialog method
   */
  private User boschUserRespDialog() {

    AddUserResponsibleDialog addBoshUserAsRespDialog =
        new AddUserResponsibleDialog(Display.getCurrent().getActiveShell(), this.a2lWpInfoBo);
    addBoshUserAsRespDialog.open();
    User selectedUser = addBoshUserAsRespDialog.getSelectedUser();
    if (selectedUser != null) {
      String name = selectedUser.getDescription();
      this.userName.setText(name != null ? name : "");
      return selectedUser;
    }
    return null;
  }

  private A2lResponsibility getA2lRespObj() {
    if ((this.a2lParamInfo != null) && !this.a2lParamInfo.isWpRespInherited()) {
      this.a2lResp =
          this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(this.a2lParamInfo.getA2lRespId());
    }
    else if (this.selRespPalObj != null) {
      this.a2lResp = this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(this.wpRespId);
    }
    else if ((this.selParamMapping != null) && !this.selParamMapping.isWpRespInherited()) {
      this.a2lResp = this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(this.wpRespId);
    }

    return this.a2lResp;
  }

  /**
   * @return the bosch
   */
  public Button getBoschButton() {
    return this.bosch;
  }

  /**
   * @return the bosch department button
   */
  public Button getBoschDeptButton() {
    return this.boschDept;
  }

  /**
   * @return the customer
   */
  public Button getCustomerButton() {
    return this.customer;
  }

  /**
   * @return the others
   */
  public Button getOthersButton() {
    return this.others;
  }

  /**
   * @return the userName
   */
  public Text getUserNameTextBox() {
    return this.userName;
  }


  /**
   * @param boschUser the boschUser to set
   */
  public void setBoschUser(final User boschUser) {
    this.boschUser = boschUser;
  }


  /**
   * @return the inheritWpRespBtn
   */
  public Button getInheritWpRespBtn() {
    return this.inheritWpRespBtn;
  }

  /**
   * @return the a2lResp
   */
  public A2lResponsibility getA2lResp() {
    return this.a2lResp;
  }


  /**
   * @return the searchButton
   */
  public Button getSearchButton() {
    return this.searchButton;
  }

  /**
   * @return the clearUserButton
   */
  public Button getClearUserButton() {
    return this.clearUserButton;
  }

  /**
   * @return the boschUser
   */
  public User getBoschUser() {
    return this.boschUser;
  }

  /**
   * Dialog Resize
   */
  public void resizeDialog() {
    this.composite.layout(true, true);
    this.shell.redraw();
  }

  /**
   * check if responsibility value is entered
   *
   * @return boolean
   */
  public boolean isRespSelected() {
    return (isInheritWpResp() || isWpRespSelected());

  }


  /**
   * @return
   */
  private boolean isWpRespSelected() {
    return this.customer.getSelection() || isBoschRespSelected() || this.others.getSelection();
  }


  /**
   * @return
   */
  private boolean isBoschRespSelected() {
    return this.bosch.getSelection() || this.boschDept.getSelection();
  }


  /**
   * @return
   */
  private boolean isInheritWpResp() {
    return (this.inheritWpRespBtn != null) && this.inheritWpRespBtn.getSelection();
  }

  /**
   * Reset Radio buttons
   */
  public void resetRadioButtons() {
    this.bosch.setSelection(false);
    this.bosch.setText(ENTER_BOSCH_RESP);
    this.boschDept.setSelection(false);
    this.boschDept.setText(ENTER_BOSCH_DEPT_RESP);
    this.customer.setSelection(false);
    this.customer.setText(ENTER_CUSTOMER_RESP);
    this.others.setSelection(false);
    this.others.setText(ENTER_OTHER_RESP);
  }


  /**
   * @return the respSection
   */
  public Section getRespSection() {
    return this.respSection;
  }

  /**
   * @return the userNameLabel
   */
  public Label getUserNameLabel() {
    return this.userNameLabel;
  }

}
