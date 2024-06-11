/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.UserSelectionDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.model.wp.Region;
import com.bosch.caltool.icdm.model.wp.WorkpackageDivisionCdl;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ws.rest.client.a2l.RegionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author apj4cob
 */
public class AddEditWpDivCdlDialog extends AbstractDialog {

  /**
   *
   */
  private static final String ADDCALIBRATIONDOMAINLEAD = "Add Calibration Domain Lead";
  /**
   * Form instance
   */
  protected Form form;
  /**
   * FormToolkit instance
   */
  protected FormToolkit formToolkit;
  /**
   * Button instance for save
   */
  private Button saveBtn;
  /**
   * Button instance for cancel
   */
  Button cancelBtn;
  /**
   * Top composite
   */
  protected Composite top;
  /**
   * Composite instance for the dialog
   */
  protected Composite composite;
  /**
   * Section instance
   */
  protected Section section;
  private Combo regionCombo;
  /**
   * Selected Calibration Domain Lead
   */
  protected User selectedUser;
  private Text cdlText;
  private final boolean addFlag;
  private static final String SELC_USER = "Select User";
  /**
   * No. of columns in the form
   */
  private static final int FORM_COL_COUNT = 3;
  /**
   * CONSTANT FOR "null,null"
   */
  private static final String STR_NULL_NULL = "null,null";
  private static final String EDITCALIBRATIONDOMAINLEAD = "Edit Calibration Domain Lead";
  private final WPDivDetailsDialog wpDetDivDialog;
  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();
  /**
   * Control decoration - value type
   */
  protected ControlDecoration comboValTypeDec;
  private final Map<String, Region> regionComboMap = new HashMap<>();


  /**
   * @param parentShell shell
   * @param addFlag boolean
   * @param wpDetDivDialog WPDetailsDialog
   */
  public AddEditWpDivCdlDialog(final Shell parentShell, final boolean addFlag,
      final WPDivDetailsDialog wpDetDivDialog) {
    super(parentShell);
    this.addFlag = addFlag;
    this.wpDetDivDialog = wpDetDivDialog;
  }

  /**
   *
   */
  private void prepopulate() {

    final IStructuredSelection selection = this.wpDetDivDialog.getCdlTabViewer().getStructuredSelection();
    if ((null != selection) && !selection.isEmpty()) {
      WorkpackageDivisionCdl wpDivCdl = (WorkpackageDivisionCdl) selection.getFirstElement();
      RegionServiceClient client = new RegionServiceClient();
      String reg = null;
      try {
        reg = client.getRegionById(wpDivCdl.getRegionId()).getRegionName();
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
      this.regionCombo.select(this.regionCombo.indexOf(reg));
      this.selectedUser = this.wpDetDivDialog.getUserMap().get(wpDivCdl.getUserId());
      // get user from unique user map and set display name
      this.cdlText.setText(this.wpDetDivDialog.getUserMap().get(wpDivCdl.getUserId()).getDescription());
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
    Control contents = super.createContents(parent);
    String title = this.addFlag ? ADDCALIBRATIONDOMAINLEAD : EDITCALIBRATIONDOMAINLEAD;
    // Set the title
    setTitle(title);
    setMessage("");
    return contents;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    String title = this.addFlag ? ADDCALIBRATIONDOMAINLEAD : EDITCALIBRATIONDOMAINLEAD;
    newShell.setText(title);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Set", true);
    this.saveBtn.setEnabled(false);
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

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
  protected void createComposite() {
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(gridData);
  }

  /**
   * This method initializes section
   */
  private void createSection() {

    this.section = this.addFlag
        ? SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
            GridDataUtil.getInstance().getGridData(), ADDCALIBRATIONDOMAINLEAD)
        : SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
            GridDataUtil.getInstance().getGridData(), EDITCALIBRATIONDOMAINLEAD);
    createForm();
    this.section.getDescriptionControl().setEnabled(false);
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    ControlDecoration cdlTextDec;
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = FORM_COL_COUNT;
    GridData gridData = getTextFieldGridData();
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    new Label(this.form.getBody(), SWT.NONE).setText("Region : ");
    this.regionCombo = new Combo(this.form.getBody(), SWT.READ_ONLY);
    this.regionCombo.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.regionCombo.add(ApicConstants.DEFAULT_COMBO_SELECT, 0);
    this.regionCombo.select(0);
    for (Region reg : this.wpDetDivDialog.getRegionMap().values()) {
      this.regionCombo.add(reg.getRegionName());
      this.regionComboMap.put(reg.getRegionName(), reg);
    }
    this.regionCombo.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        checkSaveBtnEnable();
      }
    });
    new Label(this.form.getBody(), SWT.NONE).setText("");
    getFormToolkit().createLabel(this.form.getBody(), "Calibration Domain Lead :");
    this.cdlText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.cdlText.setLayoutData(gridData);
    this.cdlText.setEditable(false);
    final Button cdlBrowse = new Button(this.form.getBody(), SWT.PUSH);
    cdlBrowse.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));
    cdlBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    cdlBrowse.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final UserSelectionDialog userDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(), SELC_USER,
            "Add New User", SELC_USER, "Select", false, false);
        userDialog.setSelectedUser(null);
        userDialog.setAddDummyUser(true);
        userDialog.open();
        AddEditWpDivCdlDialog.this.selectedUser = userDialog.getSelectedUser();
        if (AddEditWpDivCdlDialog.this.selectedUser != null) {
          final String selUserName = AddEditWpDivCdlDialog.this.selectedUser.getLastName().concat(", ")
              .concat(AddEditWpDivCdlDialog.this.selectedUser.getFirstName())
              .concat(AddEditWpDivCdlDialog.this.selectedUser.getName());
          if (!STR_NULL_NULL.equalsIgnoreCase(selUserName)) {
            AddEditWpDivCdlDialog.this.cdlText.setText(AddEditWpDivCdlDialog.this.selectedUser.getDescription());
            AddEditWpDivCdlDialog.this.wpDetDivDialog.getUserMap().put(AddEditWpDivCdlDialog.this.selectedUser.getId(),
                AddEditWpDivCdlDialog.this.selectedUser);
            checkSaveBtnEnable();
          }
        }
      }
    });

    this.regionCombo.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        checkSaveBtnEnable();
      }
    });
    cdlTextDec = new ControlDecoration(this.cdlText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(cdlTextDec, "This field is mandatory.");
    this.comboValTypeDec = new ControlDecoration(this.regionCombo, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.comboValTypeDec, IUtilityConstants.MANDATORY_MSG);
    if (!this.addFlag) {
      prepopulate();
    }
  }

  /**
   * @return GridData of text field
   */
  protected GridData getTextFieldGridData() {
    GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.verticalAlignment = GridData.CENTER;
    gridData2.grabExcessVerticalSpace = true;
    return gridData2;
  }

  /**
   * Checks if the save button should be enabled
   */
  private void checkSaveBtnEnable() {
    if (null != this.saveBtn) {
      this.saveBtn.setEnabled(validateTextFields());
    }
  }

  /**
   * check for unique region
   */
  private boolean checkDuplicateRegion() {
    int index = AddEditWpDivCdlDialog.this.regionCombo.getSelectionIndex();
    // get it form regionComboMap
    Region reg = this.regionComboMap.get(this.regionCombo.getItem(index).trim());
    if (AddEditWpDivCdlDialog.this.wpDetDivDialog.getUniqueRegionList().contains(reg.getId())) {
      MessageDialogUtils.getErrorMessageDialog("Duplicate Region",
          "Calibration Domain Lead for this region is already assigned.\nPlease choose another region.");
      return false;
    }
    return true;
  }

  /**
   * Validates the text fields before enabling the save button
   *
   * @return boolean
   */
  private boolean validateTextFields() {
    String regItem = this.regionCombo.getItem(this.regionCombo.getSelectionIndex()).trim();
    return !CommonUtils.isEmptyString(regItem) && !ApicConstants.DEFAULT_COMBO_SELECT.equals(regItem) &&
        !CommonUtils.isEmptyString(this.cdlText.getText());

  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  protected FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  @Override
  protected void okPressed() {
    int index = this.regionCombo.getSelectionIndex();
    Region reg = this.regionComboMap.get(this.regionCombo.getItem(index).trim());
    WorkpackageDivisionCdl cdlOld =
        (WorkpackageDivisionCdl) this.wpDetDivDialog.getCdlTabViewer().getStructuredSelection().getFirstElement();
    if (this.addFlag) {
      if (checkDuplicateRegion()) {
        WorkpackageDivisionCdl cdlData = new WorkpackageDivisionCdl();
        cdlData.setRegionId(reg.getId());
        cdlData.setUserId(this.selectedUser.getId());
        @SuppressWarnings("unchecked")
        List<WorkpackageDivisionCdl> input =
            (List<WorkpackageDivisionCdl>) this.wpDetDivDialog.getCdlTabViewer().getInput();
        if (null == input) {
          input = new ArrayList<>();
        }
        // Update TabViewer - add list for final save
        input.add(cdlData);
        this.wpDetDivDialog.getAddCdlList().add(cdlData);
        this.wpDetDivDialog.getCdlTabViewer().setInput(input);
        this.wpDetDivDialog.getCdlTabViewer().refresh();
        this.wpDetDivDialog.getUniqueRegionList().add(cdlData.getRegionId());
      }
      super.okPressed();
    }
    else {
      WorkpackageDivisionCdl cdlNew = new WorkpackageDivisionCdl();
      cdlNew.setRegionId(reg.getId());
      cdlNew.setUserId(this.selectedUser.getId());
      if (cdlOld.getRegionId().equals(cdlNew.getRegionId()) || checkDuplicateRegion()) {
        // Update TabViewer - add list,edit list for final save
        editOpr(cdlOld, cdlNew);
      }
      super.okPressed();
    }
  }

  /**
   * @param cdlOld
   * @param cdlNew
   */
  private void editOpr(final WorkpackageDivisionCdl cdlOld, final WorkpackageDivisionCdl cdlNew) {
    if (cdlOld.getId() != null) {

      // Eligble for update
      this.wpDetDivDialog.getEditCdlList().remove(cdlOld);
      this.wpDetDivDialog.getUniqueRegionList().remove(cdlOld.getRegionId());
      this.wpDetDivDialog.getUniqueRegionList().add(cdlNew.getRegionId());
      cdlOld.setRegionId(cdlNew.getRegionId());
      cdlOld.setUserId(cdlNew.getUserId());
      this.wpDetDivDialog.getEditCdlList().add(cdlOld);
    }
    else {
      // Elgible for Add only
      // Update operation lists
      this.wpDetDivDialog.getAddCdlList().remove(cdlOld);
      this.wpDetDivDialog.getAddCdlList().add(cdlNew);
      this.wpDetDivDialog.getUniqueRegionList().remove(cdlOld.getRegionId());
      this.wpDetDivDialog.getUniqueRegionList().add(cdlNew.getRegionId());
      // Update table list
      cdlOld.setRegionId(cdlNew.getRegionId());
      cdlOld.setUserId(cdlNew.getUserId());
    }
    this.wpDetDivDialog.getCdlTabViewer().setInput(this.wpDetDivDialog.getCdlTabViewer().getInput());
    this.wpDetDivDialog.getCdlTabViewer().refresh();
  }
}

