/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.ResponsibilitySection;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingUpdateModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author elm1cob
 */
public class UpdateWpParamDetailsDialog extends AbstractDialog {


  private Composite top;

  private FormToolkit formToolkit;

  private Composite composite;
  /**
   * Left composite
   */
  private Composite parameterComposite;
  /**
   * Right composite
   */
  private Composite workPackageComposite;

  private Section parameterSection;
  /**
   * Text field for Work Package
   */
  protected Text workPackage;
  /**
   * Text field for customer name
   */
  protected Text wpNameAtCustTxt;
  /**
   * Text field for Parameter Name
   */
  protected Text parameterName;
  /**
   * Text field for Function Name
   */
  protected Text functionName;
  /**
   * Text field for Function Version
   */
  protected Text functionVersion;
  /**
   * Text field for BC
   */
  protected Text bc;
  /**
   * Section for Work Package
   */
  private Section wpSection;
  /**
   * Section for Responsibility
   */
  private ResponsibilitySection respSection;
  /**
   * Form for Parameter Details
   */
  private Form paramaterForm;
  /**
   * Form for Work package
   */
  private Form wpForm;
  /**
   * Save button
   */
  private Button saveButton;

  private Button clearWpNameBtn;

  private Button clearWpNameAtCustBtn;

  private final A2LWpParamInfo a2lWpParamInfo;

  private final A2LWPInfoBO a2lWpInfoBo;

  private final Long selWpVersID;
  /**
   * Dialog for list of workpackages
   */
  private ShowWpRespListDialog wpListDialog;
  /**
   * Selected A2lparammapping object
   */
  private final A2lWpParamMapping selA2lParam;


  private A2lWpResponsibility selWorkPackage;

  private Button searchWpNameAtCustBtn;

  private Button inheritWpNameAtCustCheckBtn;

  private String oldWpName;

  private A2lResponsibility a2lResp;

  private boolean respInheritedInitially;

  /**
   * @param shell Shell
   * @param wpParamInfo A2LWpParamInfo
   * @param wpVersId Long
   * @param wPInfoBO A2LWPInfoBO
   */
  public UpdateWpParamDetailsDialog(final Shell shell, final A2LWpParamInfo wpParamInfo, final Long wpVersId,
      final A2LWPInfoBO wPInfoBO) {
    super(shell);
    this.a2lWpParamInfo = wpParamInfo.clone();
    this.selWpVersID = wpVersId;
    this.a2lWpInfoBo = wPInfoBO;
    this.selWorkPackage = wPInfoBO.getWpRespPalForA2lWpMapping(this.a2lWpParamInfo);
    this.selA2lParam = wPInfoBO.getA2lWpParamMappingObject(wpParamInfo).clone();
    this.respInheritedInitially = false;
    if (this.selA2lParam.isWpRespInherited()) {
      this.respInheritedInitially = true;
    }
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
    setTitle("Edit Work Package Responsibility");
    setMessage("Select work package of the parameter. Optionally, customize the responsibility",
        IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Work package - Parameter Mapping");
    super.configureShell(newShell);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    createComposite();
    parent.layout(true, true);
    return this.top;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE | SWT.MAX);
  }

  /**
   * This method initializes composite
   *
   * @param formToolkit
   * @param managedForm
   */
  private void createComposite() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.makeColumnsEqualWidth = true;
    this.composite = getFormToolkit().createComposite(this.top);
    createParameterComposite();
    createWorkPkgComposite();
    this.composite.setLayout(gridLayout);
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes composite1
   */
  private void createParameterComposite() {
    this.parameterComposite = getFormToolkit().createComposite(this.composite);
    this.parameterComposite.setLayout(new GridLayout());
    createParamSection();
    this.parameterComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes composite2
   */
  private void createWorkPkgComposite() {
    GridData gridData2 = GridDataUtil.getInstance().getGridData();
    this.workPackageComposite = getFormToolkit().createComposite(this.composite);
    this.workPackageComposite.setLayout(new GridLayout());
    createWpSection();
    createResponsibilitySection();
    this.workPackageComposite.setLayoutData(gridData2);
  }

  /**
   * This method initializes section1
   */
  private void createParamSection() {
    GridData gridData3 = GridDataUtil.getInstance().getGridData();
    this.parameterSection =
        getFormToolkit().createSection(this.parameterComposite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.parameterSection.setExpanded(true);
    this.parameterSection.setText("Parameter details");
    this.parameterSection.setLayoutData(gridData3);
    this.parameterSection.getDescriptionControl().setEnabled(false);
    createParameterForm();
    this.parameterSection.setClient(this.paramaterForm);
  }

  /**
   *
   */
  private void createParameterForm() {
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 4;

    this.paramaterForm = getFormToolkit().createForm(this.parameterSection);
    this.paramaterForm.getBody().setLayout(gridLayout);
    final GridData txtGrid = GridDataUtil.getInstance().getTextGridData();

    getFormToolkit().createLabel(this.paramaterForm.getBody(), "Parameter Name  ");
    this.parameterName =
        getFormToolkit().createText(this.paramaterForm.getBody(), null, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
    this.parameterName.setLayoutData(txtGrid);

    this.parameterName.setText(this.a2lWpParamInfo.getParamName());

    getFormToolkit().createLabel(this.paramaterForm.getBody(), "Function Name   ");
    this.functionName =
        getFormToolkit().createText(this.paramaterForm.getBody(), null, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
    this.functionName.setLayoutData(txtGrid);

    this.functionName.setText(null == this.a2lWpParamInfo.getFuncName() ? "" : this.a2lWpParamInfo.getFuncName());

    getFormToolkit().createLabel(this.paramaterForm.getBody(), "Function Version   ");
    this.functionVersion =
        getFormToolkit().createText(this.paramaterForm.getBody(), null, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
    this.functionVersion.setLayoutData(txtGrid);

    this.functionVersion
        .setText(null == this.a2lWpParamInfo.getFunctionVer() ? "" : this.a2lWpParamInfo.getFunctionVer());

    getFormToolkit().createLabel(this.paramaterForm.getBody(), "BC   ");
    this.bc = getFormToolkit().createText(this.paramaterForm.getBody(), null, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
    this.bc.setLayoutData(txtGrid);

    this.bc.setText(null == this.a2lWpParamInfo.getBcName() ? "" : this.a2lWpParamInfo.getBcName());
  }

  /**
   * This method initializes section2
   */
  private void createWpSection() {
    GridData gridData4 = GridDataUtil.getInstance().getGridData();
    this.wpSection =
        getFormToolkit().createSection(this.workPackageComposite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.wpSection.setExpanded(true);
    this.wpSection.getDescriptionControl().setEnabled(false);
    createWpForm();
    this.wpSection.setText("Work Package");
    this.wpSection.setLayoutData(gridData4);
    this.wpSection.setClient(this.wpForm);
  }

  /**
   * Workpackage form
   */
  private void createWpForm() {
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 4;
    this.wpForm = getFormToolkit().createForm(this.wpSection);
    this.wpForm.getBody().setLayout(gridLayout);
    final GridData txtGrid = GridDataUtil.getInstance().getTextGridData();

    getFormToolkit().createLabel(this.wpForm.getBody(), "Work Package  ");
    this.workPackage =
        getFormToolkit().createText(this.wpForm.getBody(), null, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
    this.workPackage.setLayoutData(txtGrid);
    this.workPackage.setFocus();

    Button searchWPButton = new Button(this.wpForm.getBody(), SWT.PUSH);
    searchWPButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.PIDC_SEARCH_16X16));
    searchWPButton.setEnabled(true);
    searchWPButton.setToolTipText("Search Work Package");

    this.clearWpNameBtn = new Button(this.wpForm.getBody(), SWT.PUSH);
    this.clearWpNameBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.CLEAR_HIST_16X16));
    this.clearWpNameBtn.setToolTipText("Clear");
    this.clearWpNameBtn.setEnabled(false);

    if (this.selWorkPackage != null) {
      String wpName = this.selWorkPackage.getName();
      this.workPackage.setText(wpName == null ? "" : wpName);
      this.clearWpNameBtn.setEnabled(true);
      this.oldWpName = wpName;
    }

    this.workPackage.addModifyListener(event -> this.clearWpNameBtn.setEnabled(true));

    searchWPButton.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {

        List<A2lWpParamMapping> selectedParamMapping = new ArrayList<>();
        selectedParamMapping.add(UpdateWpParamDetailsDialog.this.selA2lParam);

        UpdateWpParamDetailsDialog.this.wpListDialog =
            new ShowWpRespListDialog(Display.getCurrent().getActiveShell(), UpdateWpParamDetailsDialog.this.selWpVersID,
                UpdateWpParamDetailsDialog.this.a2lWpInfoBo, false, selectedParamMapping);

        if (UpdateWpParamDetailsDialog.this.wpListDialog.open() == OK) {
          UpdateWpParamDetailsDialog.this.selWorkPackage = getWpListDialog().getA2lWpResp();

          // checking if existing selection of wp has variant group customizations
          Set<A2lWpResponsibility> wpRespSet = UpdateWpParamDetailsDialog.this.a2lWpInfoBo.getA2lWpDefnModel()
              .getA2lWpRespNodeMergedMap().get(UpdateWpParamDetailsDialog.this.a2lWpInfoBo.getA2lWpDefnModel()
                  .getWpRespMap().get(UpdateWpParamDetailsDialog.this.selA2lParam.getWpRespId()).getName());

          boolean confirm = true;
          if ((UpdateWpParamDetailsDialog.this.a2lWpInfoBo.getSelectedA2lVarGroup() == null) &&
              CommonUtils.isNotEmpty(wpRespSet) && (wpRespSet.size() > 1) &&
              !UpdateWpParamDetailsDialog.this.a2lWpInfoBo
                  .paramHasVgLevelRecords(UpdateWpParamDetailsDialog.this.selA2lParam.getParamId())) {
            confirm = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Workpackage selection",
                "Existing workpackage has variant group mappings. Do you " +
                    "want to continue with new workpackage selection? Click 'Yes' to proceed.");
          }

          if ((UpdateWpParamDetailsDialog.this.a2lWpInfoBo.getSelectedA2lVarGroup() == null) &&
              UpdateWpParamDetailsDialog.this.a2lWpInfoBo
                  .paramHasVgLevelRecords(UpdateWpParamDetailsDialog.this.selA2lParam.getParamId())) {
            confirm = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Workpackage selection",
                "The parameter has variant group level mappings available. Do you " +
                    "want to continue with new workpackage selection? Click 'Yes' to proceed.");
          }


          if ((UpdateWpParamDetailsDialog.this.selWorkPackage != null) && confirm) {
            UpdateWpParamDetailsDialog.this.workPackage
                .setText(UpdateWpParamDetailsDialog.this.selWorkPackage.getName());
            UpdateWpParamDetailsDialog.this.selA2lParam
                .setWpRespId(UpdateWpParamDetailsDialog.this.selWorkPackage.getId());

            getWpCustName();
            UpdateWpParamDetailsDialog.this.clearWpNameAtCustBtn
                .setEnabled(!UpdateWpParamDetailsDialog.this.inheritWpNameAtCustCheckBtn.getSelection());
            UpdateWpParamDetailsDialog.this.checkSaveBtnEnable();
            UpdateWpParamDetailsDialog.this.enableDisable();
          }
        }
      }
    });

    this.clearWpNameBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        UpdateWpParamDetailsDialog.this.workPackage.setText("");
        UpdateWpParamDetailsDialog.this.clearWpNameBtn.setEnabled(false);
        UpdateWpParamDetailsDialog.this.wpNameAtCustTxt.setText("");
        UpdateWpParamDetailsDialog.this.clearWpNameAtCustBtn.setEnabled(false);
        UpdateWpParamDetailsDialog.this.checkSaveBtnEnable();
        setMessage("Please select a workpackage", IMessageProvider.INFORMATION);
      }
    });

    createWpNameAtCustForm();
  }


  /**
   * This method initializes section2
   */
  private void createResponsibilitySection() {
    this.respSection = new ResponsibilitySection(this.workPackageComposite, getFormToolkit(), false, this.selA2lParam,
        this.a2lWpInfoBo);
    this.respSection.createRespSection(getShell(), this.a2lWpParamInfo);
    prepopulateResp();
    addRespButtonListeners();
    enableDisable();
  }


  /**
   * Radio button listeners for Responsibility section
   */
  private void addRespButtonListeners() {
    boschBtnListener();
    boschDeptBtnListener();
    customerBtnListener();
    otherBtnListener();
    inheritBtnListener();
    respUserBtnListeners();
  }

  private void respUserBtnListeners() {
    this.respSection.getSearchButton().addSelectionListener(new SelectionAdapter() {

      /**
       * re {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        UpdateWpParamDetailsDialog.this.respSection.openUsersDialog();
        String newUserName = UpdateWpParamDetailsDialog.this.respSection.getUserNameTextBox().getText();
        boolean flag = newUserName.equals(UpdateWpParamDetailsDialog.this.respSection.getUserNameTextBox().getText());
        UpdateWpParamDetailsDialog.this.saveButton.setEnabled(flag);
      }
    });

    this.respSection.getClearUserButton().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        UpdateWpParamDetailsDialog.this.respSection.getUserNameTextBox().setText("");
        UpdateWpParamDetailsDialog.this.respSection.getClearUserButton().setEnabled(false);
        UpdateWpParamDetailsDialog.this.saveButton.setEnabled(true);
      }
    });
  }

  /**
   * inherit check box listener
   */
  private void inheritBtnListener() {
    this.respSection.getInheritWpRespBtn().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        Button btn = (Button) e.getSource();
        if (btn.getSelection()) {
          UpdateWpParamDetailsDialog.this.respSection.enableUserSearch(false);
          UpdateWpParamDetailsDialog.this.respSection.getClearUserButton().setEnabled(false);
          UpdateWpParamDetailsDialog.this.respSection.getOthersButton().setEnabled(!btn.getSelection());
          UpdateWpParamDetailsDialog.this.respSection.getCustomerButton().setEnabled(!btn.getSelection());
          UpdateWpParamDetailsDialog.this.respSection.getBoschDeptButton().setEnabled(!btn.getSelection());
          UpdateWpParamDetailsDialog.this.respSection.getBoschButton().setEnabled(!btn.getSelection());
        }
        else {
          UpdateWpParamDetailsDialog.this.respSection.getUserNameTextBox().setText("");
          UpdateWpParamDetailsDialog.this.respSection.enableUserSearch(false);
          UpdateWpParamDetailsDialog.this.respSection.getOthersButton().setEnabled(true);
          UpdateWpParamDetailsDialog.this.respSection.getCustomerButton().setEnabled(true);
          UpdateWpParamDetailsDialog.this.respSection.getBoschDeptButton().setEnabled(true);
          UpdateWpParamDetailsDialog.this.respSection.getBoschButton().setEnabled(true);
        }
        inheritedResp(btn.getSelection());


        UpdateWpParamDetailsDialog.this.checkSaveBtnEnable();
        displayInfoForRespChange(btn.getSelection());

      }
    });

  }


  private void displayInfoForRespChange(final boolean wpRespInherited) {

    A2lWpResponsibility selectedWpResp =
        this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().get(this.selA2lParam.getWpRespId());


    // if pidc level is selected in structure view, if pidc level wp resp is selected in update param dialog,
    // Inherit is set to 'No' and if variant group customizations are available for selected wp resp
    // show info message to user
    if ((this.a2lWpInfoBo.getSelectedA2lVarGroup() == null) && !wpRespInherited &&
        (selectedWpResp.getVariantGrpId() == null)) {

      Set<A2lWpResponsibility> wpRespSet = this.a2lWpInfoBo.getA2lWpDefnModel().getA2lWpRespNodeMergedMap()
          .get(this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().get(this.selA2lParam.getWpRespId()).getName());

      // if size>1 it means that variant group customizations are available in addition to pidc level wp resps
      if (CommonUtils.isNotEmpty(wpRespSet) && (wpRespSet.size() > 1)) {
        MessageDialogUtils.getInfoMessageDialog("Set Responsibility",
            "Variant groups are available for the selected workpackage!");
      }
    }
  }

  /**
   * checks whether the selected A2lResponsibility object is a Bosch Dept Record
   */
  private boolean isBoschDeptRecord() {
    return "R".equals(this.a2lResp.getRespType()) && !isBoschUserRecord() && !isCustOrOtherRecord();
  }

  /**
   * checks whether the selected A2lResponsibility object is a Bosch User Record
   */
  private boolean isBoschUserRecord() {
    return (this.a2lResp.getUserId() != null) || ((this.a2lResp.getLDepartment() == null) && !isCustOrOtherRecord()) ||
        ("R".equals(this.a2lResp.getRespType()) &&
            ("RB".equals(this.a2lResp.getLDepartment()) || this.a2lResp.getLDepartment().contains("bosch.com")));
  }

  /**
   * checks whether the selected A2lResponsibility object is a Customer or Other Record
   */
  private boolean isCustOrOtherRecord() {
    return "C".equals(this.a2lResp.getRespType()) || "O".equals(this.a2lResp.getRespType());
  }

  /**
   * Others radio button listener
   */
  private void otherBtnListener() {
    this.respSection.getOthersButton().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (UpdateWpParamDetailsDialog.this.respSection.getOthersButton().getSelection()) {
          UpdateWpParamDetailsDialog.this.respSection.getUserNameTextBox().setText("");
          UpdateWpParamDetailsDialog.this.respSection.enableUserSearch(true);
          UpdateWpParamDetailsDialog.this.respSection.getClearUserButton().setEnabled(false);
          if ("O".equals(UpdateWpParamDetailsDialog.this.a2lResp.getRespType()) &&
              !UpdateWpParamDetailsDialog.this.respInheritedInitially) {
            UpdateWpParamDetailsDialog.this.respSection.getUserNameTextBox().setText(
                UpdateWpParamDetailsDialog.this.a2lWpInfoBo.getLFullName(UpdateWpParamDetailsDialog.this.a2lResp));
            UpdateWpParamDetailsDialog.this.respSection.getClearUserButton().setEnabled(true);
          }
          UpdateWpParamDetailsDialog.this.respSection.enableUserSearch(true);
          checkSaveBtnEnable();
        }
      }
    });
  }

  /**
   * Customer Radio button listener
   */
  private void customerBtnListener() {
    this.respSection.getCustomerButton().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (UpdateWpParamDetailsDialog.this.respSection.getCustomerButton().getSelection()) {
          UpdateWpParamDetailsDialog.this.respSection.getUserNameTextBox().setText("");
          UpdateWpParamDetailsDialog.this.respSection.enableUserSearch(true);
          UpdateWpParamDetailsDialog.this.respSection.getClearUserButton().setEnabled(false);
          if ("C".equals(UpdateWpParamDetailsDialog.this.a2lResp.getRespType()) &&
              !UpdateWpParamDetailsDialog.this.respInheritedInitially) {
            UpdateWpParamDetailsDialog.this.respSection.getUserNameTextBox().setText(
                UpdateWpParamDetailsDialog.this.a2lWpInfoBo.getLFullName(UpdateWpParamDetailsDialog.this.a2lResp));
            UpdateWpParamDetailsDialog.this.respSection.getClearUserButton().setEnabled(true);
          }
          UpdateWpParamDetailsDialog.this.respSection.enableUserSearch(true);
          UpdateWpParamDetailsDialog.this.checkSaveBtnEnable();
        }
      }
    });

  }

  /**
   * Bosch radio button listener
   */
  private void boschBtnListener() {
    this.respSection.getBoschButton().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (UpdateWpParamDetailsDialog.this.respSection.getBoschButton().getSelection()) {
          UpdateWpParamDetailsDialog.this.respSection.getUserNameTextBox().setText("");
          UpdateWpParamDetailsDialog.this.respSection.enableUserSearch(true);
          UpdateWpParamDetailsDialog.this.respSection.getClearUserButton().setEnabled(false);
          if (isBoschUserRecord() && !UpdateWpParamDetailsDialog.this.respInheritedInitially) {
            fillUserName();
            UpdateWpParamDetailsDialog.this.respSection.getClearUserButton().setEnabled(true);
          }
          UpdateWpParamDetailsDialog.this.respSection.enableUserSearch(true);
          checkSaveBtnEnable();
        }
      }
    });
  }

  /**
   * Bosch Department/Group radio button listener
   */
  private void boschDeptBtnListener() {
    this.respSection.getBoschDeptButton().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (UpdateWpParamDetailsDialog.this.respSection.getBoschDeptButton().getSelection()) {
          UpdateWpParamDetailsDialog.this.respSection.getUserNameTextBox().setText("");
          UpdateWpParamDetailsDialog.this.respSection.enableUserSearch(true);
          UpdateWpParamDetailsDialog.this.respSection.getClearUserButton().setEnabled(false);
          if (isBoschDeptRecord() && !UpdateWpParamDetailsDialog.this.respInheritedInitially) {
            UpdateWpParamDetailsDialog.this.respSection.getUserNameTextBox()
                .setText(UpdateWpParamDetailsDialog.this.a2lResp.getAliasName());
            UpdateWpParamDetailsDialog.this.respSection.getClearUserButton().setEnabled(true);
          }
          UpdateWpParamDetailsDialog.this.respSection.enableUserSearch(true);
          UpdateWpParamDetailsDialog.this.checkSaveBtnEnable();
        }
      }
    });
  }

  /**
   * @return the wpListDialog
   */
  public ShowWpRespListDialog getWpListDialog() {
    return this.wpListDialog;
  }


  private void inheritedResp(final boolean inheritFlag) {
    this.selA2lParam.setWpRespInherited(inheritFlag);
    UpdateWpParamDetailsDialog.this.respSection.inheritWpResp(inheritFlag);
    if (UpdateWpParamDetailsDialog.this.saveButton != null) {
      UpdateWpParamDetailsDialog.this.saveButton.setEnabled(inheritFlag);
    }
  }

  private void setRespWOUserName(final String userType) {
    A2lResponsibility a2lResp =
        UpdateWpParamDetailsDialog.this.a2lWpInfoBo.getA2lResponsibilityModel().getDefaultA2lRespMap().get(userType);
    if (a2lResp != null) {
      UpdateWpParamDetailsDialog.this.selA2lParam.setParA2lRespId(a2lResp.getId());
    }
    else if (WpRespType.OTHERS.getCode().equals(userType)) {
      A2lResponsibility defaultOther = this.a2lWpInfoBo.createDefaultResponsible(WpRespType.OTHERS);
      UpdateWpParamDetailsDialog.this.selA2lParam.setParA2lRespId(defaultOther.getId());
    }
  }


  private void setA2lRespValue(final A2lResponsibility selectedA2lResp) {
    if (CommonUtils.isNotNull(selectedA2lResp)) {
      this.selA2lParam.setParA2lRespId(selectedA2lResp.getId());
    }
  }

  /**
   * Add bosch user dialog method
   */
  private void setRespUserValue() {
    setA2lRespValue(this.respSection.getA2lResp());
  }

  private void prepopulateResp() {
    this.respSection.resetRadioButtons();
    boolean wpRespInherited = this.selA2lParam.isWpRespInherited();
    // Check Inherited
    if (wpRespInherited) {
      this.respSection.getInheritWpRespBtn().setSelection(true);
      inheritedResp(true);
      A2lWpResponsibility a2lWpResp =
          this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().get(this.selA2lParam.getWpRespId());
      this.a2lResp =
          this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(a2lWpResp.getA2lRespId());
    }
    else {
      this.a2lResp = this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap()
          .get(this.selA2lParam.getParA2lRespId());
    }
    WpRespType respType = A2lResponsibilityCommon.getRespType(this.a2lResp);

    switch (respType) {
      case RB:
        setBoschOrBoschDeptRadioBtn();
        break;
      case CUSTOMER:
        setCustomerRadioButton(this.a2lResp);
        break;
      case OTHERS:
        setOtherRespRadioButton(this.a2lResp);
        break;
      default:
        break;
    }
    this.respSection.enableUserSearch(!wpRespInherited);
    if (this.selA2lParam.isWpRespInherited()) {
      this.respSection.getClearUserButton().setEnabled(false);
    }
    else {
      this.respSection.getClearUserButton()
          .setEnabled(!CommonUtils.isEmptyString(this.respSection.getUserNameTextBox().getText()));
    }
    this.respSection.resizeDialog();
  }

  /**
   * 
   */
  private void setBoschOrBoschDeptRadioBtn() {
    if (isBoschDeptRecord()) {
      setBoschDeptRadioBtn(this.a2lResp);
    }
    else {
      setBoschRadioButton(this.a2lResp);
    }
  }

  private void enableDisable() {
    boolean enable = !isAssignedToDefaultWP() && this.a2lWpInfoBo.isParamLevelMappingAllowed();
    if (!this.a2lWpInfoBo.isParamLevelMappingAllowed()) {
      this.respSection.getRespSection().setDescription(
          "Please click on the \"Allow modifying responsibles on Parameter level\" in  WP page to modify the responsible");
    }
    else {
      this.respSection.getRespSection().setDescription("");
    }
    this.inheritWpNameAtCustCheckBtn.setEnabled(enable);
    enableCustNameBtns(enable && !this.selA2lParam.isWpNameCustInherited());
    enableDisableRespSection(enable);
    if (this.saveButton != null) {
      this.saveButton.setEnabled(!this.oldWpName.equals(this.selWorkPackage.getName()));
    }
  }

  /**
   * @param enable
   */
  private void enableDisableRespSection(final boolean enable) {
    this.respSection.getInheritWpRespBtn().setEnabled(enable);
    this.respSection.getBoschButton().setEnabled(enable && !this.selA2lParam.isWpRespInherited());
    this.respSection.getBoschDeptButton().setEnabled(enable && !this.selA2lParam.isWpRespInherited());
    this.respSection.getCustomerButton().setEnabled(enable && !this.selA2lParam.isWpRespInherited());
    this.respSection.getOthersButton().setEnabled(enable && !this.selA2lParam.isWpRespInherited());
    this.respSection.getUserNameLabel().setEnabled(enable && !this.selA2lParam.isWpRespInherited());
    this.respSection.enableUserSearch(enable && !this.selA2lParam.isWpRespInherited());
  }

  /**
   * set other radio button
   */
  private void setOtherRespRadioButton(final A2lResponsibility a2lResp) {
    this.respSection.getOthersButton().setSelection(true);
    this.respSection.getUserNameTextBox().setText(this.a2lWpInfoBo.getLFullName(a2lResp));
  }


  /**
   * @param a2lResp
   */
  private void setCustomerRadioButton(final A2lResponsibility a2lResp) {
    if ((null != a2lResp) &&
        this.a2lWpInfoBo.getA2lResponsibilityModel().getDefaultA2lRespMap().containsKey(a2lResp.getRespType())) {
      this.respSection.getCustomerButton().setSelection(true);
      this.respSection.getUserNameTextBox().setText(this.a2lWpInfoBo.getLFullName(a2lResp));
    }
    else {
      this.respSection.getCustomerButton().setSelection(true);
    }
  }

  /**
   * @param a2lResp
   */
  private void setBoschRadioButton(final A2lResponsibility a2lResp) {
    if ((null != a2lResp) &&
        this.a2lWpInfoBo.getA2lResponsibilityModel().getDefaultA2lRespMap().containsKey(a2lResp.getRespType())) {
      this.respSection.getBoschButton().setSelection(true);
      fillUserName();
    }
    else {
      this.respSection.getBoschButton().setSelection(true);
    }
  }

  /**
   *
   */
  private void fillUserName() {
    if (this.a2lResp.getUserId() != null) {
      User boschUser = this.a2lWpInfoBo.getA2lResponsibilityModel().getUserMap().get(this.a2lResp.getUserId());
      this.respSection.setBoschUser(boschUser);
      this.respSection.getUserNameTextBox().setText(boschUser.getDescription());
    }
    else {
      this.respSection.getUserNameTextBox().setText(this.a2lResp.getName());
    }
  }

  /**
   * @param a2lResp
   */
  private void setBoschDeptRadioBtn(final A2lResponsibility a2lResp) {
    this.respSection.getBoschDeptButton().setSelection(true);
    this.respSection.getUserNameTextBox().setText(a2lResp.getAliasName());
  }


  /**
   * Name at Wp customer form
   */
  private void createWpNameAtCustForm() {

    final GridData txtGrid = GridDataUtil.getInstance().getTextGridData();

    Composite formBody = this.wpForm.getBody();
    getFormToolkit().createLabel(formBody, "Inherit from Work Package definition");

    this.inheritWpNameAtCustCheckBtn = new Button(formBody, SWT.CHECK | SWT.RIGHT);
    this.inheritWpNameAtCustCheckBtn.setEnabled(true);

    getFormToolkit().createLabel(formBody, "");
    getFormToolkit().createLabel(formBody, "");

    getFormToolkit().createLabel(formBody, "Name at Customer ");
    this.wpNameAtCustTxt = getFormToolkit().createText(formBody, null, SWT.SINGLE | SWT.BORDER);
    this.wpNameAtCustTxt.setLayoutData(txtGrid);
    getWpCustName();
    this.searchWpNameAtCustBtn = new Button(formBody, SWT.PUSH);
    this.clearWpNameAtCustBtn = new Button(formBody, SWT.PUSH);

    this.searchWpNameAtCustBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.PIDC_SEARCH_16X16));
    this.searchWpNameAtCustBtn.setEnabled(true);
    this.searchWpNameAtCustBtn.setToolTipText("Search Work Package Name at Customer");

    this.clearWpNameAtCustBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.CLEAR_HIST_16X16));
    this.clearWpNameAtCustBtn.setToolTipText("Clear");
    this.clearWpNameAtCustBtn.setEnabled(false);

    if (!this.a2lWpParamInfo.isWpNameInherited()) {
      String wpNameCust = this.a2lWpParamInfo.getWpNameCust();
      this.wpNameAtCustTxt.setText(wpNameCust == null ? "" : wpNameCust);
      this.clearWpNameAtCustBtn.setEnabled(true);
    }
    else {
      enableCustNameBtns(false);
      this.inheritWpNameAtCustCheckBtn.setSelection(true);
    }

    this.wpNameAtCustTxt.addModifyListener(event -> {
      this.clearWpNameAtCustBtn.setEnabled(true);
      this.saveButton.setEnabled(true);
    });

    this.inheritWpNameAtCustCheckBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {

        if (UpdateWpParamDetailsDialog.this.inheritWpNameAtCustCheckBtn.getSelection()) {
          UpdateWpParamDetailsDialog.this.enableCustNameBtns(false);
          UpdateWpParamDetailsDialog.this.selA2lParam.setWpNameCust(null);
          UpdateWpParamDetailsDialog.this.selA2lParam.setWpNameCustInherited(true);
          getWpCustName();
          UpdateWpParamDetailsDialog.this.clearWpNameAtCustBtn.setEnabled(false);
        }
        else {
          UpdateWpParamDetailsDialog.this.enableCustNameBtns(true);
          UpdateWpParamDetailsDialog.this.selA2lParam.setWpNameCustInherited(false);
          UpdateWpParamDetailsDialog.this.wpNameAtCustTxt.setText("");
        }
        UpdateWpParamDetailsDialog.this.checkSaveBtnEnable();
      }
    });

    this.searchWpNameAtCustBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        List<A2lWpParamMapping> selectedParamMapping = new ArrayList<>();
        selectedParamMapping.add(UpdateWpParamDetailsDialog.this.selA2lParam);

        UpdateWpParamDetailsDialog.this.wpListDialog =
            new ShowWpRespListDialog(Display.getCurrent().getActiveShell(), UpdateWpParamDetailsDialog.this.selWpVersID,
                UpdateWpParamDetailsDialog.this.a2lWpInfoBo, true, selectedParamMapping);
        UpdateWpParamDetailsDialog.this.wpListDialog.open();
        IStructuredSelection selection = getWpListDialog().getSelection();
        if (null != selection) {
          String wpNameCust2 = (String) selection.getFirstElement();
          UpdateWpParamDetailsDialog.this.wpNameAtCustTxt.setText(wpNameCust2);
          UpdateWpParamDetailsDialog.this.selA2lParam.setWpNameCust(wpNameCust2);
          UpdateWpParamDetailsDialog.this.checkSaveBtnEnable();
        }
      }
    });

    this.clearWpNameAtCustBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        UpdateWpParamDetailsDialog.this.wpNameAtCustTxt.setText("");
        UpdateWpParamDetailsDialog.this.selA2lParam.setWpNameCust(null);
        UpdateWpParamDetailsDialog.this.clearWpNameAtCustBtn.setEnabled(false);
        UpdateWpParamDetailsDialog.this.checkSaveBtnEnable();
      }

    });

  }

  /**
   * enable customer name form buttongs
   */
  private void enableCustNameBtns(final boolean flag) {
    this.wpNameAtCustTxt.setEnabled(flag);
    this.clearWpNameAtCustBtn.setEnabled(flag);
    this.searchWpNameAtCustBtn.setEnabled(flag);
  }

  private void checkSaveBtnEnable() {
    this.saveButton
        .setEnabled(!CommonUtils.isEmptyString(this.workPackage.getText()) && this.respSection.isRespSelected());
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
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    // Save button
    this.saveButton = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveButton.setEnabled(false);
    // Cancel button
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * @return true if the label is assigned to default work package
   */
  private boolean isAssignedToDefaultWP() {
    return this.selWorkPackage.getName().equals(ApicConstants.DEFAULT_A2L_WP_NAME);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if (validateValues()) {
      setValueForNameAtCust();

      A2lWpResponsibility selectedWpResp =
          this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().get(this.selA2lParam.getWpRespId());

      // if pidc level is selected in structure view
      if (this.a2lWpInfoBo.getSelectedA2lVarGroup() == null) {
        updatePidcLevelSelection(selectedWpResp);
      }
      // var grp level is selected in structure view
      else {
        updateVarGrpLevelSelection();
      }
      super.okPressed();
    }
  }


  /**
   * update the selected parameter assignment
   */
  private void updateVarGrpLevelSelection() {

    if (!this.a2lWpParamInfo.isWpRespInherited() && this.selA2lParam.isWpRespInherited()) {
      A2lWpParamMappingUpdateModel model = new A2lWpParamMappingUpdateModel();
      model.getA2lWpParamMappingToBeUpdated().put(this.selA2lParam.getId(), this.selA2lParam);
      this.a2lWpInfoBo.updateMappings(model);
    }
    else {
      updateParamInfoObject(this.selA2lParam);
      List<A2LWpParamInfo> paramInfoList = new ArrayList<>();
      paramInfoList.add(this.a2lWpParamInfo);
      this.a2lWpInfoBo.updateWpParamMapping(paramInfoList, this.selWorkPackage, false);
    }
  }

  /**
   * @param selectedWpResp
   */
  private void updatePidcLevelSelection(final A2lWpResponsibility selectedWpResp) {
    // if pidc level is selected in structure view, and
    // if selected wp is at pidc level and
    // if respInherit flag is changed from N to Y
    // and if variant level mappings are present for the parameter
    // show info dialog to user

    boolean confirm = true;
    Map<Long, A2lWpParamMapping> varLevelMappings = new HashMap<>();

    // this.selA2lParam contains changes done in this dialog
    if ((selectedWpResp.getVariantGrpId() == null) && !this.a2lWpParamInfo.isWpRespInherited() &&
        this.selA2lParam.isWpRespInherited()) {

      Set<A2lWpResponsibility> wpRespSet =
          this.a2lWpInfoBo.getA2lWpDefnModel().getA2lWpRespNodeMergedMap().get(selectedWpResp.getName());

      // means there are var grp level wp resps
      if (CommonUtils.isNotEmpty(wpRespSet) && (wpRespSet.size() > 1)) {

        for (A2lWpResponsibility a2lWpResponsibility : wpRespSet) {

          if (a2lWpResponsibility.getVariantGrpId() != null) {

            Map<Long, A2lWpParamMapping> paramMappingsMap = this.a2lWpInfoBo.getParamWpRespResolver()
                .getParamMappingAtVarLevel().get(a2lWpResponsibility.getVariantGrpId());

            if (CommonUtils.isNotEmpty(paramMappingsMap) &&
                CommonUtils.isNotNull(paramMappingsMap.get(this.selA2lParam.getParamId()))) {
              varLevelMappings.put(paramMappingsMap.get(this.selA2lParam.getParamId()).getId(),
                  paramMappingsMap.get(this.selA2lParam.getParamId()));
            }
          }
        }
      }

      if (CommonUtils.isNotEmpty(varLevelMappings)) {
        confirm = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Inherit Responsibility",
            "There are variant group mappings available for this parameter. Do you want to inherit completely?" +
                "This will delete all variant group mappings. Click 'Yes' to proceed");
      }

    }

    updateParamInfoObject(this.selA2lParam);

    List<A2LWpParamInfo> paramInfoList = new ArrayList<>();
    paramInfoList.add(this.a2lWpParamInfo);

    if (confirm) {
      A2lWpParamMappingUpdateModel updateModel = new A2lWpParamMappingUpdateModel();

      if (selectedWpResp.getVariantGrpId() != null) {
        this.a2lWpInfoBo.createCreateList(paramInfoList, this.selWorkPackage, updateModel, false);
      }
      // update pidc level maping record with INherit resp to 'Y'
      this.a2lWpInfoBo.createUpdateList(paramInfoList, this.selWorkPackage, updateModel, false);
      // delete var grp mapping records
      updateModel.getA2lWpParamMappingToBeDeleted().putAll(varLevelMappings);
      this.a2lWpInfoBo.updateMappings(updateModel);

    }
    else {
      // update in DB
      this.a2lWpInfoBo.updateWpParamMapping(paramInfoList, this.selWorkPackage, false);
    }

  }


  private void setValueForNameAtCust() {
    if (((!this.inheritWpNameAtCustCheckBtn.getSelection()) && ((this.selA2lParam.getWpNameCust() != null) &&
        !this.selA2lParam.getWpNameCust().equals(this.wpNameAtCustTxt.getText()))) ||
        ((this.selA2lParam.getWpNameCust() == null) && (null != this.wpNameAtCustTxt.getText()))) {
      this.selA2lParam.setWpNameCust(this.wpNameAtCustTxt.getText());
    }
  }


  private void updateParamInfoObject(final A2lWpParamMapping selWpParam) {
    this.a2lWpParamInfo.setA2lRespId(selWpParam.getParA2lRespId());
    this.a2lWpParamInfo.setWpNameCust(selWpParam.getWpNameCust());
    this.a2lWpParamInfo.setWpNameInherited(selWpParam.isWpNameCustInherited());
    this.a2lWpParamInfo.setWpRespId(selWpParam.getWpRespId());
    this.a2lWpParamInfo.setWpRespInherited(selWpParam.isWpRespInherited());
  }

  /**
   * Validate the entered values
   */
  private boolean validateValues() {
    if (!this.respSection.isRespSelected()) {
      MessageDialogUtils.getInfoMessageDialog("Select a responsibility", "Please select a value for responsibility");
      this.saveButton.setEnabled(false);
      return false;
    }

    if (CommonUtils.isEmptyString(this.respSection.getUserNameTextBox().getText())) {
      if (this.respSection.getBoschButton().getSelection() || this.respSection.getBoschDeptButton().getSelection()) {
        setRespWOUserName(WpRespType.RB.getCode());
      }
      if (this.respSection.getCustomerButton().getSelection()) {
        setRespWOUserName(WpRespType.CUSTOMER.getCode());
      }
      if (this.respSection.getOthersButton().getSelection()) {
        setRespWOUserName(WpRespType.OTHERS.getCode());
      }
      return true;
    }

    if (this.respSection.getBoschButton().getSelection() &&
        !WpRespType.RB.getDispName().equals(this.respSection.getUserNameTextBox().getText())) {
      A2lResponsibility a2lResp = this.a2lWpInfoBo.createBoschResponsible(this.respSection.getBoschUser());
      this.selA2lParam.setParA2lRespId(a2lResp.getId());
    }
    else {
      setRespUserValue();
    }
    return true;
  }

  /**
   * Get Wp Customer name from Wp Param Assignment Page
   */
  private void getWpCustName() {
    String nameAtCust = UpdateWpParamDetailsDialog.this.selWorkPackage.getWpNameCust();
    UpdateWpParamDetailsDialog.this.wpNameAtCustTxt.setText(nameAtCust == null ? "" : nameAtCust);
  }


}
