/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.ws.rest.client.apic.emr.EmrFileServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;

/**
 * This class is to edit emr file
 *
 * @author gge6cob
 */
public class EMRFileEditDescDialog extends AbstractDialog {

  /**
   * the maximum length for the text,
   */
  private static final int MAX_LENGTH = 4000;
  /**
   * top composite
   */
  private Composite top;
  /**
   * FormToolkit
   */
  private FormToolkit formToolkit;
  /**
   * EmrFile
   */
  private final EmrFile emrFile;
  /**
   * child Composite
   */
  private Composite composite;
  /**
   * section
   */
  private Section section;
  /**
   * Form
   */
  private Form form;
  /**
   * Text
   */
  private Text descText;

  /**
   * Constructor
   *
   * @param parentShell Shell
   * @param emrFile EmrFile
   */
  public EMRFileEditDescDialog(final Shell parentShell, final EmrFile emrFile) {
    super(parentShell);
    this.emrFile = emrFile;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Edit Description");
    // Set the message
    setMessage("Enter a description for the sheet: " + this.emrFile.getName());
    return contents;
  }

  /**
   * configure the shell and set the title
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell name
    newShell.setText("Description for sheet : " + this.emrFile.getName());

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
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    // create composite on parent comp
    createComposite();
    return this.top;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    // create section inside composite
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = getFormToolkit().createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("Description");
    this.section.setDescription("Enter the details.");
    this.section.setExpanded(true);
    this.section.getDescriptionControl().setEnabled(false);
    // create from inside section
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    final GridLayout gridLayout = new GridLayout();
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    // create desc field
    createDescField();
  }

  /**
   * create description field
   */
  private void createDescField() {
    TextBoxContentDisplay boxContentDisplay = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, MAX_LENGTH, getTextAreaGridData());
    this.descText = boxContentDisplay.getText();
    if (CommonUtils.isNotEmptyString(this.emrFile.getDescription())) {
      this.descText.setText(this.emrFile.getDescription());
    }
  }

  /**
   * get GridData for text area
   *
   * @return GridData
   */
  private GridData getTextAreaGridData() {
    GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.grabExcessVerticalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.verticalAlignment = GridData.FILL;
    gridData2.heightHint = 100;
    gridData2.widthHint = 100;
    return gridData2;
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
   * after clicking ok in dialog
   */
  @Override
  protected void okPressed() {
    if (CommonUtils.isNotEqual(this.descText.getText(), this.emrFile.getDescription())) {
      // Cloned to keep the data intact - even if WS fails - original data is not modified
      EmrFile newFile = this.emrFile.clone();
      newFile.setDescription(this.descText.getText());
      EmrFileServiceClient client = new EmrFileServiceClient();
      try {
        // call web service to update the emr file desc
        EmrFile updatedFile = client.updateEmrFileDetails(newFile);
        this.emrFile.setVersion(updatedFile.getVersion());
        this.emrFile.setDescription(updatedFile.getDescription());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    super.okPressed();
  }

  @Override
  protected boolean isResizable() {
    // whether the dialog control can be resized
    return true;
  }
}
