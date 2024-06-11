/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.pages.DetailsPage;
import com.bosch.caltool.icdm.ruleseditor.pages.ListPage;
import com.bosch.caltool.icdm.ws.rest.client.a2l.RuleSetParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.ui.validators.Validator;


/**
 * @author bru2cob
 */
public class ImportRuleSetParamFromA2LDialog extends AbstractDialog {

  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();

  /**
   * Composite instance for the dialog
   */
  private Composite composite;
  /**
   * save button
   */
  protected Button saveBtn;
  /**
   * cancel button
   */
  protected Button cancelBtn;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;
  /**
   * browse button image
   */
  private Image browseButtonImage;
  /**
   * Composite instance
   */
  private Composite top;

  /**
   * a2l name text
   */
  private Text a2lNameText;

  /**
   * mandatory dec for a2lname
   */
  protected ControlDecoration a2lNameDesc;

  /**
   * selc a2l file
   */
  protected String fileSelected;

  /**
   * editor instance
   */
  private final ReviewParamEditor reviewParamEditor;

  private final RuleSet ruleSet;

  private final AbstractFormPage formPage;

  /**
   * @param parentShell shell
   * @param sdomPver selc sdompver
   */
  public ImportRuleSetParamFromA2LDialog(final Shell parentShell, final ReviewParamEditor reviewParamEditor,
      final RuleSet ruleSet, final AbstractFormPage formPage) {
    super(parentShell);
    this.reviewParamEditor = reviewParamEditor;
    this.ruleSet = ruleSet;
    this.formPage = formPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Import Parameters from A2L");
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
    final Control contents = super.createContents(parent);

    // Set the title
    setTitle("Import Parameters from A2L");
    // Set the message
    setMessage("Enter A2L File Details", IMessageProvider.INFORMATION);
    return contents;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {

    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Import", true);
    this.saveBtn.setEnabled(false);
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    try {
      final ProgressMonitorDialog progressDlg = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
      progressDlg.run(true, true, monitor -> {
        try {
          Set<RuleSetParameter> createRuleSetParamUsingA2l =
              new RuleSetParameterServiceClient().createRuleSetParamUsingA2l(this.ruleSet.getId(), this.fileSelected);
          for (RuleSetParameter ruleSetParameter : createRuleSetParamUsingA2l) {
            this.reviewParamEditor.getEditorInput().getParamDataProvider().getParamRulesOutput().getParamMap()
                .put(ruleSetParameter.getName(), ruleSetParameter);
          }
          CDMLogger.getInstance()
              .infoDialog("Import parameter from A2L is successful.\nTotal Parameters imported from a2l is " +
                  createRuleSetParamUsingA2l.size(), Activator.PLUGIN_ID);
        }
        catch (ApicWebServiceException | IOException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      });
    }
    catch (InvocationTargetException | InterruptedException exp) {
      Thread.currentThread().interrupt();
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    super.okPressed();
    refreshPage(this.formPage);
  }

  /**
   * @param abstractFormPage abstractFormPage
   */
  private void refreshPage(final AbstractFormPage abstractFormPage) {
    if (abstractFormPage instanceof ListPage) {
      ListPage page = (ListPage) abstractFormPage;
      page.refreshListPage();
      page.getParamTabSec().getParamTab().updateStatusBar(false);
    }
    if (abstractFormPage instanceof DetailsPage) {
      DetailsPage page = (DetailsPage) abstractFormPage;
      page.getEditor().refreshSelectedParamRuleData();
      page.getFcTableViewer().refresh();
      page.setStatusBarMessage(page.getFcTableViewer());
      ListPage listPage = page.getEditor().getListPage();
      listPage.refreshListPage();
    }
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
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
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(gridData);
    this.section.getDescriptionControl().setEnabled(false);
  }


  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Enter the details");
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    this.browseButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON);
    final GridData txtGrid = GridDataUtil.getInstance().getTextGridData();
    // a2l file detail
    createA2lControl(txtGrid);
  }


  /**
   * create a2l info
   *
   * @param txtGrid gridData
   */
  private void createA2lControl(final GridData txtGrid) {
    getFormToolkit().createLabel(this.form.getBody(), "A2L File:");
    this.a2lNameText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);

    this.a2lNameText.setLayoutData(txtGrid);
    this.a2lNameText.setEditable(false);
    this.a2lNameDesc = new ControlDecoration(this.a2lNameText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.a2lNameDesc, IUtilityConstants.MANDATORY_MSG);
    this.a2lNameText.addModifyListener(event -> {
      if ((ImportRuleSetParamFromA2LDialog.this.a2lNameText.getText() != null) &&
          !"".equalsIgnoreCase(ImportRuleSetParamFromA2LDialog.this.a2lNameText.getText())) {
        Validator.getInstance().validateNDecorate(ImportRuleSetParamFromA2LDialog.this.a2lNameDesc,
            ImportRuleSetParamFromA2LDialog.this.a2lNameText, true, false);
      }
      checkSaveBtnEnable();
    });
    final Button browseBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    browseBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.MULTI);
        fileDialog.setText("Select A2L File");
        fileDialog.setFilterExtensions(new String[] { "*.a2l" });
        ImportRuleSetParamFromA2LDialog.this.fileSelected = fileDialog.open();
        if (ImportRuleSetParamFromA2LDialog.this.fileSelected != null) {
          ImportRuleSetParamFromA2LDialog.this.a2lNameText.setText(ImportRuleSetParamFromA2LDialog.this.fileSelected);
        }
      }
    });
    browseBtn.setImage(this.browseButtonImage);
    browseBtn.setToolTipText("Select a2l file");
  }

  /**
   * checks whether the save button can be enabled or not
   */
  protected void checkSaveBtnEnable() {
    this.saveBtn.setEnabled(validateFields());
  }

  /**
   * @return
   */
  private boolean validateFields() {
    return !"".equalsIgnoreCase(this.a2lNameText.getText());
  }


}
