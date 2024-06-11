/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.ws.rest.client.cdr.ReviewServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author ukt1cob
 */
public class SimplifiedGeneralQnaireDialog extends AbstractDialog {

  /**
   *
   */
  private static final String SIMPLIFIED_GENERAL_QNAIRE_TITLE = "Fill the Simplified General Questionnaire response";
  /**
  *
  */
  private static final String TITLE_TXT = "Simplified General Questionnaire";
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Form instance
   */
  private Form form;
  private Button confirmRadioBtn;
  private Button notConfmRadioBtn;
  private String remarks;
  private final CDRReviewResult cdrRvwResult;
  private TextBoxContentDisplay textBoxContentDisplay;
  /**
   * true - called from ReviewResultInfoPage and form should be disabled, false - default value and the form should be
   * enabled
   */
  private boolean isReviewInfoPage = false;

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param cdrReviewResult selected Review Result model
   */
  public SimplifiedGeneralQnaireDialog(final Shell parentShell, final CDRReviewResult cdrReviewResult) {
    super(parentShell);
    this.cdrRvwResult = cdrReviewResult;
  }

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param cdrReviewResult selected Review Result model.
   * @param isReviewInfoPage represents whether the input fields should be enabled or not
   */
  public SimplifiedGeneralQnaireDialog(final Shell parentShell, final CDRReviewResult cdrReviewResult,
      final boolean isReviewInfoPage) {
    super(parentShell);
    this.cdrRvwResult = cdrReviewResult;
    this.isReviewInfoPage = isReviewInfoPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {

    final Control contents = super.createContents(parent);
    // Set title
    setTitle(SIMPLIFIED_GENERAL_QNAIRE_TITLE);

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell name
    newShell.setText(TITLE_TXT);
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
    Composite parentComp = (Composite) super.createDialogArea(parent);
    parentComp.setLayout(new GridLayout());

    // create composite on parent comp
    createComposite(parentComp);

    return parentComp;
  }


  /**
   * This method initializes composite
   *
   * @param parentComp
   */
  private void createComposite(final Composite parentComp) {

    Composite composite = getFormToolkit().createComposite(parentComp);
    composite.setLayout(new GridLayout());
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());

    // create section
    createSection(composite);
  }

  /**
   * This method initializes section
   *
   * @param composite
   */
  private void createSection(final Composite composite) {

    Section section = SectionUtil.getInstance().createSection(composite, getFormToolkit(), "Declaration");

    // create form
    createForm(section);

    section.setLayoutData(GridDataUtil.getInstance().getGridData());
    section.getDescriptionControl().setEnabled(false);
    section.setClient(this.form);
  }

  /**
   * This method initializes form
   *
   * @param section
   */
  private void createForm(final Section section) {

    this.form = getFormToolkit().createForm(section);
    this.form.getBody().setLayout(new GridLayout());

    Composite declarationComp = getFormToolkit().createComposite(this.form.getBody());
    GridLayout layout = new GridLayout(1, false);
    declarationComp.setLayout(layout);

    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
    data.heightHint = 110;
    declarationComp.setLayoutData(data);

    Browser browser = new Browser(declarationComp, SWT.NONE);
    browser.setText(getSimpQnaireDeclaration());
    GridData gridDataBrowser = new GridData(SWT.FILL, SWT.FILL, true, true);
    browser.setLayoutData(gridDataBrowser);

    createQnaireRespField();

    createRemarksField();
    // if the dialogDisabled is set to true, then the form should be disabled and input should not be received
    if (this.isReviewInfoPage) {
      this.form.getBody().setEnabled(false);
    }
  }

  /**
   * @return
   */
  private String getSimpQnaireDeclaration() {

    String htmlContentStr = "";
    try {
      htmlContentStr = FileIOUtil.convertHtmlByteToString(
          new ReviewServiceClient().getSimpQnaireDeclrtnFile(CommonUtils.getSystemUserTempDirPath()));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return htmlContentStr;
  }

  /**
   *
   */
  private void createQnaireRespField() {

    this.confirmRadioBtn = new Button(this.form.getBody(), SWT.RADIO);
    this.confirmRadioBtn.setText("I confirm");
    this.confirmRadioBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        boolean cfmRadioBtnSel = getConfirmRadioBtn().getSelection();
        enableSaveButton(cfmRadioBtnSel);
        SimplifiedGeneralQnaireDialog.this.cdrRvwResult
            .setSimpQuesRespValue(cfmRadioBtnSel ? CommonUtilConstants.CODE_YES : CommonUtilConstants.CODE_NO);
      }
    });

    this.notConfmRadioBtn = new Button(this.form.getBody(), SWT.RADIO);
    this.notConfmRadioBtn.setText("I don't Confirm (remark mandatory)");
    this.notConfmRadioBtn.setSelection(true);
    this.notConfmRadioBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        boolean notCfmRadioBtnSel = getNotConfmRadioBtn().getSelection();
        enableSaveButton(!(notCfmRadioBtnSel && CommonUtils.isEmptyString(getRemarks())));
        SimplifiedGeneralQnaireDialog.this.cdrRvwResult
            .setSimpQuesRespValue(notCfmRadioBtnSel ? CommonUtilConstants.CODE_NO : CommonUtilConstants.CODE_YES);
      }
    });
  }


  /**
   *
   */
  private void createRemarksField() {
    GridData gridData = new GridData();
    gridData.heightHint = 100;
    gridData.widthHint = 400;
    gridData.verticalSpan = 3;

    LabelUtil.getInstance().createEmptyLabel(this.form.getBody());
    LabelUtil.getInstance().createLabel(this.form.getBody(), "Remark : ");
    this.textBoxContentDisplay = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, 4000, gridData);
    this.textBoxContentDisplay.setEnabled(true);
    this.textBoxContentDisplay.getText().addModifyListener(event -> {
      SimplifiedGeneralQnaireDialog.this.remarks = this.textBoxContentDisplay.getText().getText();
      enableSaveButton((!this.isReviewInfoPage) &&
          !(getNotConfmRadioBtn().getSelection() && CommonUtils.isEmptyString(getRemarks())));
    });
  }

  /**
   * @param isToEnable
   */
  private void enableSaveButton(final boolean isToEnable) {
    Button okButton = getButton(IDialogConstants.OK_ID);
    if (CommonUtils.isNotNull(okButton)) {
      okButton.setEnabled(isToEnable);
    }
  }

  /**
   *
   */
  private void setDialogInput() {

    // popualte value from db
    boolean simpQuesRespFlag =
        CommonUtils.isEqual(this.cdrRvwResult.getSimpQuesRespValue(), CommonUtilConstants.CODE_YES);
    if (CommonUtils.isNotNull(simpQuesRespFlag)) {
      this.confirmRadioBtn.setSelection(simpQuesRespFlag);
      this.notConfmRadioBtn.setSelection(!simpQuesRespFlag);
      if (!this.isReviewInfoPage) {
        enableSaveButton(!(getNotConfmRadioBtn().getSelection() && CommonUtils.isEmptyString(getRemarks())));
      }

      SimplifiedGeneralQnaireDialog.this.cdrRvwResult.setSimpQuesRespValue(this.cdrRvwResult.getSimpQuesRespValue());
    }

    String simpQuesRemarks = this.cdrRvwResult.getSimpQuesRemarks();
    this.remarks = CommonUtils.isNotEmptyString(simpQuesRemarks) ? simpQuesRemarks : "";
    this.textBoxContentDisplay.getText().setText(CommonUtils.checkNull(this.remarks));
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
  protected boolean isResizable() {
    return true;
  }


  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, !this.isReviewInfoPage);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);

    setDialogInput();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    super.okPressed();
    this.cdrRvwResult.setSimpQuesRemarks(getRemarks());
    if (CommonUtils.isNull(this.cdrRvwResult.getSimpQuesRespValue())) {
      this.cdrRvwResult.setSimpQuesRespValue(
          getConfirmRadioBtn().getSelection() ? CommonUtilConstants.CODE_YES : CommonUtilConstants.CODE_NO);
    }
  }


  /**
   * @return the confirmRadioBtn
   */
  public Button getConfirmRadioBtn() {
    return this.confirmRadioBtn;
  }


  /**
   * @param confirmRadioBtn the confirmRadioBtn to set
   */
  public void setConfirmRadioBtn(final Button confirmRadioBtn) {
    this.confirmRadioBtn = confirmRadioBtn;
  }


  /**
   * @return the notConfmRadioBtn
   */
  public Button getNotConfmRadioBtn() {
    return this.notConfmRadioBtn;
  }


  /**
   * @param notConfmRadioBtn the notConfmRadioBtn to set
   */
  public void setNotConfmRadioBtn(final Button notConfmRadioBtn) {
    this.notConfmRadioBtn = notConfmRadioBtn;
  }


  /**
   * @return the remarks
   */
  public String getRemarks() {
    return this.remarks;
  }


  /**
   * @param remarks the remarks to set
   */
  public void setRemarks(final String remarks) {
    this.remarks = remarks;
  }


  /**
   * @return the textBoxContentDisplay
   */
  public TextBoxContentDisplay getTextBoxContentDisplay() {
    return this.textBoxContentDisplay;
  }


  /**
   * @param textBoxContentDisplay the textBoxContentDisplay to set
   */
  public void setTextBoxContentDisplay(final TextBoxContentDisplay textBoxContentDisplay) {
    this.textBoxContentDisplay = textBoxContentDisplay;
  }

}
