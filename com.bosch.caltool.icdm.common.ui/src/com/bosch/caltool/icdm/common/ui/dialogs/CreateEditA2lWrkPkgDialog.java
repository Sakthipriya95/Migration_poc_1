/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
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
import com.bosch.caltool.icdm.client.bo.apic.WorkPkgResponsibilityBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.actions.UpdateWpRespToolBarActionSet;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWorkPackageServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * @author nip4cob
 */
public class CreateEditA2lWrkPkgDialog extends AbstractDialog {

  private FormToolkit formToolkit;
  private Form form;
  private Section section;
  private final boolean isCreate;
  private Text wpNameText;
  private Text nameAtCustText;
  private Text desc;
  private A2LWPInfoBO a2lWpInfoBo;
  private final A2lWorkPackage a2lWpToEdit;
  private UpdateWpRespToolBarActionSet updateWpRespToolBarActionSet;
  private WorkPkgResponsibilityBO wpRespBo;
  private boolean isPidcEditorWp = false;
  private Button saveBtn;

  private A2lWorkPackage currentWorkpackage;


  /**
   * @param parentShell shell
   * @param isCreate - true for create operation, else false
   * @param a2lWpInfoBo - PidcVersion id
   * @param selectedWP - WP to be edited
   * @param updateWpRespToolBarActionSet toolbaractionset
   */
  public CreateEditA2lWrkPkgDialog(final Shell parentShell, final boolean isCreate, final A2LWPInfoBO a2lWpInfoBo,
      final A2lWorkPackage selectedWP, final UpdateWpRespToolBarActionSet updateWpRespToolBarActionSet) {
    super(parentShell);
    this.isCreate = isCreate;
    this.a2lWpInfoBo = a2lWpInfoBo;
    this.a2lWpToEdit = selectedWP;
    this.updateWpRespToolBarActionSet = updateWpRespToolBarActionSet;
  }

  /**
   * @param parentShell shell
   * @param isCreate - true for create operation, else false
   * @param selectedWP - WP to be edited
   * @param pidcVers selected Pidcversion
   */
  public CreateEditA2lWrkPkgDialog(final Shell parentShell, final boolean isCreate, final A2lWorkPackage selectedWP,
      final WorkPkgResponsibilityBO wrkPkgRespBo) {
    super(parentShell);
    this.isCreate = isCreate;
    this.a2lWpToEdit = selectedWP;
    this.wpRespBo = wrkPkgRespBo;
    this.isPidcEditorWp = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Title modified
    newShell.setText("Work Package Details");
    // shell size removed to check for resolution issue
    super.configureShell(newShell);
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
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    setTitle("Create Work Package");
    String warningMsg = null;
    try {
      warningMsg = new CommonDataBO().getMessage("A2L", "INVALID_WP_RESP_ERROR");
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    setMessage(
        "Changing/adding Work Packages will be effective for all variant groups in the current A2L\n" + warningMsg);
    return contents;
  }

  @Override
  protected Control createDialogArea(final Composite parent) {
    final Composite top = (Composite) super.createDialogArea(parent);
    top.setLayout(new GridLayout());

    GridData gridData = GridDataUtil.getInstance().getGridData();
    top.setLayoutData(gridData);
    gridData.minimumWidth = 100;
    gridData.minimumHeight = 100;
    createSection(getFormToolkit(), top);
    return top;
  }


  /**
   * @param toolkit
   * @param composite
   */
  private void createSection(final FormToolkit toolkit, final Composite composite) {
    this.section = toolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("Work Package");
    this.section.getDescriptionControl().setEnabled(false);
    createForm(toolkit);
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * @param toolkit
   */
  private void createForm(final FormToolkit toolkit) {

    this.form = toolkit.createForm(this.section);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.form.getBody().setLayout(gridLayout);

    this.formToolkit.createLabel(this.form.getBody(), "Work Package Name *");

    this.wpNameText = this.formToolkit.createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.wpNameText.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.wpNameText.setTextLimit(255);

    this.formToolkit.createLabel(this.form.getBody(), "Name of WP at Customer");

    this.nameAtCustText = this.formToolkit.createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.nameAtCustText.setTextLimit(4000);
    this.nameAtCustText.setLayoutData(GridDataUtil.getInstance().getTextGridData());

    this.formToolkit.createLabel(this.form.getBody(), "Description");
    this.desc = this.formToolkit.createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.desc.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    if (!this.isCreate) {
      this.wpNameText.setText(this.a2lWpToEdit.getName());
      this.nameAtCustText.setText(this.a2lWpToEdit.getNameCustomer() == null ? "" : this.a2lWpToEdit.getNameCustomer());
      this.desc.setText(this.a2lWpToEdit.getDescription() == null ? "" : this.a2lWpToEdit.getDescription());
    }
    this.wpNameText.addModifyListener(event -> enableDisableOkBtn());
    this.nameAtCustText.addModifyListener(event -> enableDisableOkBtn());
    this.desc.addModifyListener(event -> enableDisableOkBtn());

  }


  /**
  *
  */
  private void enableDisableOkBtn() {
    if (this.isCreate) {
      this.saveBtn.setEnabled(CommonUtils.isNotEmptyString(this.wpNameText.getText()));
    }
    else {
      this.saveBtn.setEnabled(isEditDone());
    }
  }

  /**
   * @return
   */
  private boolean isEditDone() {
    if (CommonUtils.isEmptyString(this.wpNameText.getText())) {
      return false;
    }
    return !(this.a2lWpToEdit.getName().equals(this.wpNameText.getText()) && isCustNameSame() && isDescSame());
  }

  /**
   * @return
   */
  private boolean isDescSame() {
    return (this.a2lWpToEdit.getDescription() == null ? "" : this.a2lWpToEdit.getDescription())
        .equals(this.desc.getText());
  }

  /**
   * @return
   */
  private boolean isCustNameSame() {
    return (this.a2lWpToEdit.getNameCustomer() == null ? "" : this.a2lWpToEdit.getNameCustomer())
        .equals(this.nameAtCustText.getText());
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


  @Override
  public void okPressed() {
    boolean closeDialog;
    if (this.isCreate) {
      closeDialog = createWp();
    }
    else {
      closeDialog = updateWp();
    }
    if (closeDialog) {
      super.okPressed();
    }
  }

  /**
   *
   */
  private boolean updateWp() {
    boolean closeDialog = false;
    if (isValidWpName()) {
      closeDialog = true;
      A2lWorkPackage updatedA2lWp = this.a2lWpToEdit.clone();
      updatedA2lWp.setName(this.wpNameText.getText().trim());
      updatedA2lWp.setNameCustomer(this.nameAtCustText.getText());
      updatedA2lWp.setDescription(this.desc.getText());
      try {
        A2lWorkPackage updatedWp = new A2lWorkPackageServiceClient().update(updatedA2lWp);
        refreshWpInPidc(updatedWp);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        closeDialog = false;
      }
    }
    return closeDialog;
  }

  /**
   *
   */
  private boolean createWp() {
    boolean closeDialog = false;
    if (validateWpName()) {
      closeDialog = true;
      A2lWorkPackage wpPalToCreate = new A2lWorkPackage();
      wpPalToCreate.setName(this.wpNameText.getText().trim());
      wpPalToCreate.setNameCustomer(this.nameAtCustText.getText());
      wpPalToCreate.setDescription(this.desc.getText());
      if (this.a2lWpInfoBo != null) {
        wpPalToCreate.setPidcVersId(this.a2lWpInfoBo.getPidcA2lBo().getPidcVersion().getId());
      }
      else {
        wpPalToCreate.setPidcVersId(this.wpRespBo.getPidcVersObj().getId());
      }
      try {
        A2lWorkPackage createdWp = new A2lWorkPackageServiceClient().create(wpPalToCreate);
        refreshWpInPidc(createdWp);

      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        closeDialog = false;
      }
    }
    return closeDialog;
  }

  /**
   * return false if the entered work package name is already present in the PidcVersion Level
   */
  private boolean validateWpName() {
    List<A2lWorkPackage> wps;
    if (!this.isPidcEditorWp) {
      wps = new ArrayList<>(this.a2lWpInfoBo.getAllWpMapppedToPidcVers().values());
    }
    else {
      wps = new ArrayList<>(this.wpRespBo.getWorkPackages().values());
    }
    if (!this.isCreate) {
      // for edit remove the validation for wp name as it is done in previous step itself
      wps.remove(this.a2lWpToEdit);
    }
    for (A2lWorkPackage wp : wps) {
      if (wp.getName().equals(this.wpNameText.getText().trim())) {
        CDMLogger.getInstance().infoDialog(
            "The entered Workpackage name is already present!  Please try with some other name", Activator.PLUGIN_ID);
        return false;
      }
    }
    return true;
  }

  private boolean checkEnteredValues() {
    return this.a2lWpToEdit.getName().equals(this.wpNameText.getText().trim()) && isSameCustomer() && isSameDesc();
  }

  /**
   * @return
   */
  private boolean isSameDesc() {
    return (this.a2lWpToEdit.getDescription() != null) &&
        this.a2lWpToEdit.getDescription().equals(this.desc.getText().trim());
  }

  /**
   * @return
   */
  private boolean isSameCustomer() {
    return (this.a2lWpToEdit.getNameCustomer() != null) &&
        this.a2lWpToEdit.getNameCustomer().equals(this.nameAtCustText.getText().trim());
  }


  private boolean isValidWpName() {
    if (!this.isCreate) {
      if (checkEnteredValues()) {
        return false;
      }
      return validateWpName();
    }
    return true;
  }

  /**
   * @param workPackage
   */
  private void refreshWpInPidc(final A2lWorkPackage workPackage) {
    if (!this.isPidcEditorWp) {
      this.updateWpRespToolBarActionSet.getUpdateWpRespDialog().getComoboViewerInput().put(workPackage.getId(),
          workPackage);
      this.updateWpRespToolBarActionSet.getUpdateWpRespDialog().setInputForCombo();
      this.updateWpRespToolBarActionSet.getUpdateWpRespDialog().getWorkPackageCombo()
          .setSelection(new StructuredSelection(workPackage));
      this.updateWpRespToolBarActionSet.getUpdateWpRespDialog().enableSave(true);
      setCurrentWorkpackage(workPackage);
    }
    else {
      this.wpRespBo.getWorkPackages().put(workPackage.getId(), workPackage);
      setCurrentWorkpackage(workPackage);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }


  /**
   * @return the currentWorkpackage
   */
  public A2lWorkPackage getCurrentWorkpackage() {
    return this.currentWorkpackage;
  }


  /**
   * @param currentWorkpackage the currentWorkpackage to set
   */
  public void setCurrentWorkpackage(final A2lWorkPackage currentWorkpackage) {
    this.currentWorkpackage = currentWorkpackage;
  }

}
