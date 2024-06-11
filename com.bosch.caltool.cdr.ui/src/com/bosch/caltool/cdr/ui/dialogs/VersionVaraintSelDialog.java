/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.ProjectDataSelectionWizardPage;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.PIDCVaraintSelDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * @author rgo7cob
 */
public class VersionVaraintSelDialog extends PIDCVaraintSelDialog {


  private final ProjectDataSelectionWizardPage projSelPage;


  /**
   * Review Comment Dialog
   */
  private static final String TITLE = "Select PIDC Version or Variant";


  /**
   * Shell Width
   */
  private static final int SHELL_WIDTH = 600;


  /**
   * Shell Height
   */
  private static final int SHELL_HEIGHT = 600;

  /**
   * @param parentShell parentShell
   * @param projSelPage projSelPage
   */
  public VersionVaraintSelDialog(final Shell parentShell, final ProjectDataSelectionWizardPage projSelPage) {
    super(parentShell);
    this.projSelPage = projSelPage;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(TITLE);

    // Set the message
    setMessage("Select PIDC/Variant for Delta Review", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setSize(SHELL_WIDTH, SHELL_HEIGHT);
    newShell.setText("Select PIDC/Variant for Delta Review");
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    newShell.setLayout(new GridLayout());
    newShell.setLayoutData(gridData);
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    CDRWizardUIModel cdrWizardUIModel = this.projSelPage.getCalDtReviewWizard().getCdrWizardUIModel();

    try {
      if (CommonUtils.isNull(this.selVar)) {
        this.projSelPage.getCalDtReviewWizard().setHasVaraint(this.selPidcVer.getId(), false);
        // If the Varaint is not selected
        if ((this.selPidcVer.getId() != null) && this.projSelPage.getCalDtReviewWizard().isHasVariant()) {
          CDMLogger.getInstance().errorDialog("Variant is Defined for the PIDC. Please select Variant for Review",
              Activator.PLUGIN_ID);
          PidcTreeNodeHandler pidcTreeNodeHandler = this.projSelPage.getCalDtReviewWizard().getPidcTreeNodeHandler();
          PidcTreeNode pidcTreeNodeForVersion =
              pidcTreeNodeHandler.getPidcVerIdTreenodeMap().get(this.selPidcVer.getId());
          PidcTreeNode pidcTreeNodeForLeafNode =
              pidcTreeNodeHandler.getNodeIdNodeMap().get(pidcTreeNodeForVersion.getParentNodeId());
          this.viewer.setSelection(new StructuredSelection(pidcTreeNodeForLeafNode), true);
          // Set Expansion to the leaf node.
          this.viewer.setExpandedState(pidcTreeNodeForLeafNode, true);
          return;
        }
      }
      // If the selected Version is null get the Version from Variant.
      if (null == this.selPidcVer) {
        this.selPidcVer = new CDRHandler().getPidcVersion(this.selVar.getPidcVersionId());
      }
      // Task 232978


      // To get the SDOMPverName for the Selected Variant
      cdrWizardUIModel.setSelectedReviewPverName(
          new CDRHandler().getSDOMPverName(this.selPidcVer.getId(), this.selVar == null ? 0l : this.selVar.getId()));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    // Check whether the questionnaire config attribute is set for the selected PIDC version
    try {
      new CDRHandler().getQnaireConfigAttribute(this.selPidcVer.getId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      return;
    }


    // Set the Pid card version
    cdrWizardUIModel.setSelectedPidcVerId(this.selPidcVer.getId());

    // Set A2l File as null Since the version is Changed.
    cdrWizardUIModel.setA2lFileId(null);
    cdrWizardUIModel.setPidcA2lId(null);
    // If the version is not selected then the version name must be empty.
    if (CommonUtils.isNull(this.selVar)) {
      this.projSelPage.setVariantName("");
      cdrWizardUIModel.setSelectedPidcVariantId(null);
      cdrWizardUIModel.setSelectedPidcVariantName(null);
      this.projSelPage.getVariantName().setEnabled(false);
      this.projSelPage.getVariantName().setEditable(false);
    }
    else {
      this.projSelPage.setVariantName(this.selVar.getName());
      // Set the Varaint to the Cdr data.
      cdrWizardUIModel.setSelectedPidcVariantId(this.selVar.getId());
      cdrWizardUIModel.setSelectedPidcVariantName(this.selVar.getName());
      this.projSelPage.getVariantName().setEnabled(true);
      this.projSelPage.getVariantName().setEditable(true);
    }


    // Set the Proj id card name.
    this.projSelPage.setProjectName(this.selPidcVer.getName());
    ((CalDataReviewWizard) this.projSelPage.getWizard()).setContentChanged(true);
    super.okPressed();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void setSelectionForTreeViewer() {
    Long selPidcVerId = this.projSelPage.getCalDtReviewWizard().getCdrWizardUIModel().getSelectedPidcVerId();
    if (CommonUtils.isNotNull(selPidcVerId)) {

      this.projSelPage.getCalDtReviewWizard().setPidcTreeNodeHandler(getTreeHandler());

      PidcTreeNode pidcTreeNodeForVersion =
          this.projSelPage.getCalDtReviewWizard().getPidcTreeNodeHandler().getPidcVerIdTreenodeMap().get(selPidcVerId);
      PidcTreeNode pidcTreeNodeForLeafNode = this.projSelPage.getCalDtReviewWizard().getPidcTreeNodeHandler()
          .getNodeIdNodeMap().get(pidcTreeNodeForVersion.getParentNodeId());
      this.viewer.setSelection(new StructuredSelection(pidcTreeNodeForLeafNode), true);
      // Set Expansion to the leaf node.
      this.viewer.setExpandedState(pidcTreeNodeForLeafNode, true);

    }

  }
}
