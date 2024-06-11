/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.wp.WorkPkg;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.wizards.pages.FCWorkPackageCreationWizPage;
import com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * This class provides UI to add value dependency information
 */
public class AddEditWorkPackageDialog extends AbstractDialog {

  /**
   * No. of columns in the form
   */
  private static final int FORM_COL_COUNT = 2;
  /**
   * Button instance for save
   */
  private Button saveBtn;
  /**
   * Button instance for cancel
   */
  Button cancelBtn;
  /**
   * Composite instance for the dialog
   */
  protected Composite composite;
  /**
   * FormToolkit instance
   */
  protected FormToolkit formToolkit;
  /**
   * Section instance
   */
  protected Section section;
  /**
   * Form instance
   */
  protected Form form;
  /**
   * Top composite
   */
  protected Composite top;
  /**
   * Attribute name text
   */
  private Text wpEngNameText;
  /**
   * Attribute value text
   */
  private Text wpGermanNameText;
  /**
   * Text for adding comment when a value is edited
   */
  protected Text descriptionEng;
  private final WorkPkg selWp;

  private Text descriptionGer;
  private boolean isUpdate;
  private final FCWorkPackageCreationWizPage wpCreationPage;
  /**
   * the field to make the control of Content display of text box and the message label by the user
   */
  protected TextBoxContentDisplay textBoxEngContentDisplay;
  private TextBoxContentDisplay textBoxGerContentDisplay;
  /**
   * the maximum length for the comment
   */
  private static final int MAX_LENGTH = 4000;

  /**
   * Constructor
   *
   * @param parentShell reference of parent Shell
   * @param wpCreationPage reference of attributesPage
   * @param selWp WorkPkg
   */
  public AddEditWorkPackageDialog(final Shell parentShell, final FCWorkPackageCreationWizPage wpCreationPage,
      final WorkPkg selWp) {
    super(parentShell);
    if (selWp == null) {
      this.isUpdate = false;
    }
    else {
      this.isUpdate = true;
    }
    this.wpCreationPage = wpCreationPage;
    this.selWp = selWp;
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

    // Set the title
    setTitle("Create a WorkPackage");

    // Set the message
    setMessage("");

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    // ICDM-153
    super.setHelpAvailable(true);
    newShell.setText("FC2WP");
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    if (this.isUpdate) {
      setValuesToFields();
    }
    this.saveBtn.setEnabled(false);
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
   * Set existing values to feilds
   */
  public void setValuesToFields() {
    this.wpEngNameText.setText(CommonUtils.checkNull(this.selWp.getWpNameEng()));
    this.wpGermanNameText.setText(CommonUtils.checkNull(this.selWp.getWpNameGer()));
    this.descriptionEng.setText(CommonUtils.checkNull(this.selWp.getWpDescEng()));
    this.descriptionGer.setText(CommonUtils.checkNull(this.selWp.getWpDescGer()));
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Enter the workpackage details");

    createForm();
    this.section.getDescriptionControl().setEnabled(false);
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = FORM_COL_COUNT;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    createControls();
  }

  /**
   *
   */
  protected void createControls() {

    GridData gridData = getTextFieldGridData();
    getFormToolkit().createLabel(this.form.getBody(), "Name (English)* :");
    this.wpEngNameText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.wpEngNameText.setLayoutData(gridData);
    this.wpEngNameText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent modifyevent) {
        checkSaveBtnEnable();
      }
    });

    getFormToolkit().createLabel(this.form.getBody(), "Name (German) :");
    this.wpGermanNameText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.wpGermanNameText.setLayoutData(gridData);
    this.wpGermanNameText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent modifyevent) {
        checkSaveBtnEnable();
      }
    });

    getFormToolkit().createLabel(this.form.getBody(), "Description (English) :");
    GridData commentGridData = getTextFieldGridData();
    commentGridData.grabExcessVerticalSpace = false;
    commentGridData.heightHint = 40;
    this.textBoxEngContentDisplay = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, commentGridData, true, MAX_LENGTH);
    this.descriptionEng = this.textBoxEngContentDisplay.getText();

    getFormToolkit().createLabel(this.form.getBody(), "");
    this.descriptionEng.setLayoutData(commentGridData);
    this.descriptionEng.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent modifyevent) {
        checkSaveBtnEnable();
      }
    });

    getFormToolkit().createLabel(this.form.getBody(), "Description (German) :");
    this.textBoxGerContentDisplay = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, commentGridData, true, MAX_LENGTH);
    this.descriptionGer = this.textBoxGerContentDisplay.getText();

    this.descriptionGer.setLayoutData(commentGridData);
    this.descriptionGer.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent modifyevent) {
        checkSaveBtnEnable();
      }
    });
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
    this.saveBtn.setEnabled(validateTextFields());
  }

  /**
   * Validates the text fields before enabling the save button
   *
   * @return boolean
   */
  private boolean validateTextFields() {
    return !this.wpEngNameText.getText().isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    WorkPackageServiceClient servClient = new WorkPackageServiceClient();
    try {
      WorkPkg wpNew;
      if (this.isUpdate) {
        WorkPkg wpToUpd = this.selWp.clone();
        wpToUpd.setWpNameEng(this.wpEngNameText.getText());
        wpToUpd.setWpNameGer(this.wpGermanNameText.getText());
        wpToUpd.setWpDescEng(this.descriptionEng.getText());
        wpToUpd.setWpDescGer(this.descriptionGer.getText());
        wpNew = servClient.update(wpToUpd);
      }
      else {
        WorkPkg wpToCre = new WorkPkg();
        wpToCre.setWpNameEng(this.wpEngNameText.getText());
        wpToCre.setWpNameGer(this.wpGermanNameText.getText());
        wpToCre.setWpDescEng(this.descriptionEng.getText());
        wpToCre.setWpDescGer(this.descriptionGer.getText());
        wpNew = servClient.create(wpToCre);
      }
      Set<WorkPkg> wpSet = servClient.findAll();
      this.wpCreationPage.setTableInput(wpSet);
      for (WorkPkg selWpKg : wpSet) {
        if (selWpKg.getName().equalsIgnoreCase(wpNew.getWpNameEng())) {
          GridTableViewerUtil.getInstance().setSelection(this.wpCreationPage.getWpTabViewer(), selWpKg);
          break;
        }
      }

    }
    catch (ApicWebServiceException | CloneNotSupportedException excep) {
      CDMLogger.getInstance().errorDialog(excep.getMessage(), excep, Activator.PLUGIN_ID);
    }
    super.okPressed();
  }
}
