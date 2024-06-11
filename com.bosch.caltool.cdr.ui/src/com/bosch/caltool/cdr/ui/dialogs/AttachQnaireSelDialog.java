/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author dja7cob
 */
public class AttachQnaireSelDialog extends AbstractDialog {

  private Composite top;
  private Composite composite;
  private FormToolkit formToolkit;
  private Section section;
  private Form form;
  private Button okBtn;
  private final List<PidcVariant> selVariants;
  private Button linkExisting;
  private Button createNew;
  private boolean isLinkExistingQnaire;

  /**
   * @param parentShell
   * @param selVariants
   */
  public AttachQnaireSelDialog(final Shell parentShell, final List<PidcVariant> selVariants) {
    super(parentShell);
    this.selVariants = selVariants;
  }

  @Override
  protected boolean isResizable() {
    return false;
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
    setTitle("Attach Review Questionnaires");

    StringBuilder stringBuilder = new StringBuilder();
    this.selVariants.forEach(var -> stringBuilder.append(var.getName()).append(", "));

    // Set the message
    setMessage("Attach Review Questionnaires to variant(s) : " + stringBuilder.toString(),
        IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Attach Review Questionnaires");
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
    createComposite();
    return this.top;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
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
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "Attach Questionnaire(s)");
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(false);
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);

    this.linkExisting = new Button(this.form.getBody(), SWT.RADIO);
    this.linkExisting.setText("Link Existing Review Questionnaire(s)");
    this.linkExisting.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    this.linkExisting.setSelection(true);
    this.linkExisting.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        // NA
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // NA
      }
    });

    this.createNew = new Button(this.form.getBody(), SWT.RADIO);
    this.createNew.setText("Create New");
    this.createNew.setSelection(false);
    this.createNew.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    this.createNew.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        // NA
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // NA
      }
    });

    this.form.getBody().setLayout(new GridLayout());
  }


  /**
   * Creates the buttons for the button bar
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
    this.okBtn.setEnabled(true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if (this.okBtn.getEnabled()) {
      setLinkExistingQnaire(this.linkExisting.getSelection());
      super.okPressed();
    }
  }

  /**
   * @return the isLinkExistingQnaire
   */
  public boolean isLinkExistingQnaire() {
    return this.isLinkExistingQnaire;
  }

  /**
   * @param isLinkExistingQnaire the isLinkExistingQnaire to set
   */
  public void setLinkExistingQnaire(final boolean isLinkExistingQnaire) {
    this.isLinkExistingQnaire = isLinkExistingQnaire;
  }
}
