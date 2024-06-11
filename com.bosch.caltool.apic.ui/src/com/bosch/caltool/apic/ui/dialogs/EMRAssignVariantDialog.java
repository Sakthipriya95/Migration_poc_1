/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.apic.ui.wizards.EMRAssignVariantComposite;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;

/**
 * @author gge6cob
 */
public class EMRAssignVariantDialog extends AbstractDialog {

  private FormToolkit formToolkit;
  /**
   * emr file id's list
   */
  Set<Long> emrFileIds = new HashSet<>();

  /**
   * @param parentShell parent
   * @param emrFileIds input ids
   */
  public EMRAssignVariantDialog(final Shell parentShell, final Set<Long> emrFileIds) {
    super(parentShell);
    this.emrFileIds = emrFileIds;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, true);

    getButton(IDialogConstants.OK_ID).setVisible(false);
  }

  /**
   * @param emrFileIds EMR fileIds
   */
  public void setEmrFileIds(final Set<Long> emrFileIds) {
    this.emrFileIds = emrFileIds;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Codex Measurement Program - Assigned Variants");
    // Set the message
    setMessage("Please select and assign PIDC variants to the Emission standard procedures from the uploaded file(s).");
    return contents;
  }

  /**
   * configure the shell and set the title
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell name
    newShell.setText("Select variant");
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
    ScrolledComposite scrolledComp = new ScrolledComposite(parent, SWT.V_SCROLL);
    // create composite on parent comp
    EMRAssignVariantComposite emrAssignCompositeClass = new EMRAssignVariantComposite(this.emrFileIds, true);
    Composite workAreaComp = emrAssignCompositeClass.createComposite(scrolledComp, getFormToolkit(), null);
    workAreaComp.layout(true);
    scrolledComp.setContent(workAreaComp);
    scrolledComp.setExpandHorizontal(true);
    scrolledComp.setExpandVertical(true);
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    scrolledComp.setLayoutData(gridData);
    scrolledComp.setMinSize(workAreaComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    return parent;
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
  protected boolean isResizable() {
    return true;
  }
}