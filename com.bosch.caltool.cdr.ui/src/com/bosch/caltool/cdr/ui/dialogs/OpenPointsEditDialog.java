/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.editors.pages.QnaireAnswerEditComposite;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.CalendarDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author bru2cob
 */
public class OpenPointsEditDialog extends AbstractDialog {

  /**
   *
   */
  private static final String RESPONSIBLE = "Responsible:";
  /**
   *
   */
  private static final String RESPONSIBLE_MAND = "Responsible *:";
  /**
   * Button instance for save
   */
  protected Button saveBtn;

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
   * the top of form
   */
  private Composite top;
  /**
   * Composite instance for the dialog
   */
  private Composite composite;

  private final QnaireRespEditorDataHandler dataHandler;

  private final QnaireAnswerEditComposite questionResponseComposite;


  private String openPointStr;


  private String measureStr;
  private Long responsibleId;
  private String dateStr;
  private boolean resultStatus;
  private final OpenPointsData opData;
  private Label measureLabel;
  private Label responsibleLabel;
  private Label dateLabel;
  private String responsibleName;
  private TextBoxContentDisplay measure;
  private Button userSelBtn;

  /**
   * @param parentShell Shell
   * @param ansObj QnaireRespEditorDataHandler
   * @param questionResponseComposite QnaireAnswerEditComposite
   * @param openPointData OpenPointsData
   */
  public OpenPointsEditDialog(final Shell parentShell, final QnaireRespEditorDataHandler ansObj,
      final QnaireAnswerEditComposite questionResponseComposite, final OpenPointsData openPointData) {
    super(parentShell);
    this.dataHandler = ansObj;
    this.questionResponseComposite = questionResponseComposite;
    this.opData = openPointData;
    if (null != this.opData) {
      this.dateStr = this.opData.getDate();
      this.measureStr = this.opData.getMeasures();
      this.openPointStr = this.opData.getOpenPoint();
      this.responsibleId = this.opData.getResponsibleId();
      this.responsibleName = this.opData.getResponsibleName();
      this.resultStatus = this.opData.isResult();
    }
  }

  /**
   * create contents
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Add Open Point");
    // Set the message
    setMessage("Add the details and click on Save button");
    return contents;
  }

  /**
   * configure the shell and set the title
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // calling parent
    newShell.setText("Add Open Point");
    super.configureShell(newShell);
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Apply", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

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
  private void createComposite() {
    GridData gridData = GridDataUtil.getInstance().getGridData();
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
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    GridData gridData = GridDataUtil.getInstance().createGridData();
    gridData.heightHint = 40;
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.CENTER;
    gridData.grabExcessVerticalSpace = true;
    // ICDM-2485
    createOpenPointsUI(gridData);
    createMeasuresUI(gridData);
    createResponsibleUI(gridLayout);
    createDateUI(gridLayout);
    createCompletedUI();
  }

  /**
   * creates Open Points in ui
   */
  private void createOpenPointsUI(final GridData gridData) {

    // create open point field if it is relevant for qnaire
    if (this.dataHandler.showQnaireVersOpenPoints()) {
      // ICDM-2188
      LabelUtil.getInstance().createLabel(this.form.getBody(),
          CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_OPEN_POINTS) + "* :");

      final TextBoxContentDisplay openPoint = new TextBoxContentDisplay(this.form.getBody(),
          SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, gridData, true, 4000);
      if (null != this.opData) {
        openPoint.getText().setText(CommonUtils.isNull(this.opData.getOpenPoint()) ? "" : this.opData.getOpenPoint());
      }
      openPoint.getText().addModifyListener((final ModifyEvent event) -> {
        OpenPointsEditDialog.this.openPointStr = openPoint.getText().getText();
        checkSaveBtnEnable();
      });
      getFormToolkit().createLabel(this.form.getBody(), "");
      getFormToolkit().createLabel(this.form.getBody(), "");
      getFormToolkit().createLabel(this.form.getBody(), "");

      // Disable if the open point field is not relevant for question
      if (!this.dataHandler.showOpenPoints(this.questionResponseComposite.getRvwAnsObj().getQuestionId())) {
        openPoint.setEnabled(false);
        openPoint.setEditable(false);
      }
    }
  }

  /**
   * creates Measures in ui
   */
  private void createMeasuresUI(final GridData gridData) {
    // create measure ui if it is relevant for questionnaire
    if (this.dataHandler.showQnaireVersMeasures()) {
      createMeasureField(gridData);
    }
  }

  /**
   * @param gridData
   */
  private void createMeasureField(final GridData gridData) {
    if (this.questionResponseComposite.isMeasuresMandatory()) {
      // ICDM-2188
      this.measureLabel = LabelUtil.getInstance().createLabel(this.form.getBody(),
          CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_MEASURE) + "* :");
    }
    else {
      this.measureLabel = LabelUtil.getInstance().createLabel(this.form.getBody(),
          CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_MEASURE) + " :");
    }

    this.measure = new TextBoxContentDisplay(this.form.getBody(), SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL,
        gridData, true, 4000);
    if (null != this.opData) {
      this.measure.getText().setText(CommonUtils.isNull(this.opData.getMeasures()) ? "" : this.opData.getMeasures());
    }
    this.measure.getText().addModifyListener((final ModifyEvent event) -> {
      OpenPointsEditDialog.this.measureStr = OpenPointsEditDialog.this.measure.getText().getText();
      if (OpenPointsEditDialog.this.questionResponseComposite.isMeasuresMandatory() &&
          OpenPointsEditDialog.this.measureStr.isEmpty()) {
        // ICDM-2188
        OpenPointsEditDialog.this.measureLabel.setText(
            CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_MEASURE) + "* :");
      }
      else {
        OpenPointsEditDialog.this.measureLabel.setText(
            CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_MEASURE) + " :");
      }
      checkSaveBtnEnable();
    });
    getFormToolkit().createLabel(this.form.getBody(), "");
    getFormToolkit().createLabel(this.form.getBody(), "");
    getFormToolkit().createLabel(this.form.getBody(), "");

    // Disable Measures Field if it is not relevant for question
    if (!this.dataHandler.showMeasures(this.questionResponseComposite.getRvwAnsObj().getQuestionId())) {
      this.measure.setEditable(false);
      this.measure.setEnabled(false);
    }
  }


  /**
   * creates Responsible in ui
   */
  private void createResponsibleUI(final GridLayout gridLayout) {

    // create responsible ui if it is relevant for Qnaire
    if (this.dataHandler.showQnaireVersResponsible()) {
      setResponsibleLabel();
      Composite resComp = getFormToolkit().createComposite(this.form.getBody());
      resComp.setLayout(gridLayout);
      resComp.setLayoutData(GridDataUtil.getInstance().getGridData());

      final Text responsible = getFormToolkit().createText(resComp, null, SWT.SINGLE | SWT.BORDER);
      responsible.setLayoutData(GridDataUtil.getInstance().getGridData());
      responsible.setEditable(false);
      if (null != this.opData) {
        responsible.setText(CommonUtils.isNull(this.opData.getResponsibleId()) ? "" : this.opData.getResponsibleName());
      }
      responsible.addModifyListener((final ModifyEvent event) -> {
        if (OpenPointsEditDialog.this.questionResponseComposite.isResponsibleMandatory() &&
            responsible.getText().isEmpty()) {
          OpenPointsEditDialog.this.responsibleLabel.setText(RESPONSIBLE_MAND);
        }
        else {
          OpenPointsEditDialog.this.responsibleLabel.setText(RESPONSIBLE);
        }
        checkSaveBtnEnable();
      });

      // create user button
      createResponsibleUserButton(resComp, responsible);

      getFormToolkit().createLabel(this.form.getBody(), "");

      // Disable Measures Field if it is not relevant for question
      if (!this.dataHandler.showResponsible(this.questionResponseComposite.getRvwAnsObj().getQuestionId())) {
        this.userSelBtn.setEnabled(false);
        responsible.setEnabled(false);
        responsible.setEditable(false);
      }
    }
  }

  /**
   * @param resComp
   * @param responsible
   */
  private void createResponsibleUserButton(final Composite resComp, final Text responsible) {
    this.userSelBtn = new Button(resComp, SWT.NONE);
    this.userSelBtn.setLayoutData(new GridData());
    this.userSelBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.BOSCH_RESPONSIBLE_ICON_16X16));
    this.userSelBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final ResponsibleUserSelectionDialog selResponsibleDialog = new ResponsibleUserSelectionDialog(
            Display.getCurrent().getActiveShell(), "Select User", "Select Responsible", "Select User", "Select", false);
        selResponsibleDialog.setSelectedUser(null);
        selResponsibleDialog.open();
        User selectedUser = selResponsibleDialog.getSelectedUser();
        if (selectedUser != null) {
          final String selUserName = selectedUser.getDescription();
          if (!"null,null".equalsIgnoreCase(selUserName)) {
            OpenPointsEditDialog.this.responsibleId = selectedUser.getId();
            OpenPointsEditDialog.this.responsibleName = selUserName;
            responsible.setText(selUserName);
          }
        }
      }
    });
  }

  /**
   *
   */
  private void setResponsibleLabel() {
    if (this.questionResponseComposite.isResponsibleMandatory()) {
      this.responsibleLabel = LabelUtil.getInstance().createLabel(this.form.getBody(), RESPONSIBLE_MAND);
    }
    else {
      this.responsibleLabel = LabelUtil.getInstance().createLabel(this.form.getBody(), RESPONSIBLE);
    }
  }

  /**
   * creates date in ui
   */
  private void createDateUI(final GridLayout gridLayout) {

    // create Date ui if it is relevant for Qnaire
    if (this.dataHandler.showQnaireVersCompletionDate()) {
      createDateField(gridLayout);
    }
  }

  /**
   * @param gridLayout
   */
  private void createDateField(final GridLayout gridLayout) {
    if (this.questionResponseComposite.isDateMandatory()) {

      // ICDM-2188
      this.dateLabel = LabelUtil.getInstance().createLabel(this.form.getBody(),
          CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_DATE) + "* :");
    }
    else {
      this.dateLabel = LabelUtil.getInstance().createLabel(this.form.getBody(),
          CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_DATE) + " :");
    }
    Composite dateComp = getFormToolkit().createComposite(this.form.getBody());
    dateComp.setLayout(gridLayout);
    dateComp.setLayoutData(GridDataUtil.getInstance().getGridData());

    final Text date = getFormToolkit().createText(dateComp, null, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    date.setLayoutData(GridDataUtil.getInstance().getGridData());
    date.setEditable(false);
    if (null != this.opData) {
      date.setText(CommonUtils.isNull(this.opData.getDateUIString()) ? "" : this.opData.getDateUIString());
    }
    date.addModifyListener((final ModifyEvent event) -> {

      OpenPointsEditDialog.this.dateStr = date.getText();
      if (OpenPointsEditDialog.this.questionResponseComposite.isDateMandatory() &&
          OpenPointsEditDialog.this.dateStr.isEmpty()) {
        // ICDM-2188
        OpenPointsEditDialog.this.dateLabel.setText(
            CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_DATE) + "* :");
      }
      else {
        OpenPointsEditDialog.this.dateLabel.setText(
            CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_DATE) + " :");
      }
      checkSaveBtnEnable();
    });
    Button dateSelBtn = new Button(dateComp, SWT.NONE);
    dateSelBtn.setLayoutData(new GridData());
    dateSelBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.CALENDAR_16X16));
    dateSelBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        CalendarDialog calDailog = new CalendarDialog();
        calDailog.addCalendarDialog(OpenPointsEditDialog.this.form.getBody(), date, DateFormat.DATE_FORMAT_09);
      }
    });
    getFormToolkit().createLabel(this.form.getBody(), "");
    // Disable date Field if it is not relevant for question
    if (!this.dataHandler.showCompletionDate(this.questionResponseComposite.getRvwAnsObj().getQuestionId())) {
      dateSelBtn.setEnabled(false);
      date.setEnabled(false);
      date.setEditable(false);
    }
  }

  /**
   * creates completed ui
   */
  private void createCompletedUI() {
    LabelUtil.getInstance().createLabel(this.form.getBody(),
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_STATUS) + " :");
    final Button resultChkBox = new Button(this.form.getBody(), SWT.CHECK);
    resultChkBox.setLayoutData(GridDataUtil.getInstance().getGridData());
    if (null != this.opData) {
      resultChkBox.setSelection(this.opData.isResult());
    }
    resultChkBox.addSelectionListener(new SelectionAdapter() {

      /**
       * Enable/disable save button
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        OpenPointsEditDialog.this.resultStatus = resultChkBox.getSelection();
        checkSaveBtnEnable();
      }
    });

    getFormToolkit().createLabel(this.form.getBody(), "");
    getFormToolkit().createLabel(this.form.getBody(), "");
    getFormToolkit().createLabel(this.form.getBody(), "");
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
   * Validates the text fields before enabling the save button
   *
   * @return boolean
   */
  private boolean validateTextFields() {

    // only in case of edit dialog
    boolean openPointNotValid = CommonUtils.isEmptyString(this.openPointStr);
    boolean measuresNotValid =
        this.questionResponseComposite.isMeasuresMandatory() && CommonUtils.isEmptyString(this.measureStr);
    boolean responsibleNotValid =
        this.questionResponseComposite.isResponsibleMandatory() && CommonUtils.isNull(this.responsibleId);
    boolean dateNotValid = this.questionResponseComposite.isDateMandatory() && CommonUtils.isEmptyString(this.dateStr);
    boolean saveDisable = openPointNotValid || (measuresNotValid || responsibleNotValid || dateNotValid);

    return !saveDisable;
  }

  /**
   * @return the openPointStr
   */
  public String getOpenPointStr() {
    return this.openPointStr;
  }


  /**
   * @return the measureStr
   */
  public String getMeasureStr() {
    return this.measureStr;
  }


  /**
   * @return the responsibleStr
   */
  public Long getResponsibleId() {
    return this.responsibleId;
  }


  /**
   * @return the dateStr
   */
  public String getDateStr() {
    return this.dateStr;
  }


  /**
   * @return the resultStatus
   */
  public boolean isResultStatus() {
    return this.resultStatus;
  }

  /**
   * if returns true, user can maximize or minimize the dialog.
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if (null == this.opData) {
      OpenPointsData openPointsData = new OpenPointsData(null);
      openPointsData.setDate(this.dateStr);
      openPointsData.setMeasures(this.measureStr);
      openPointsData.setOpenPoint(this.openPointStr);
      openPointsData.setResponsibleId(this.responsibleId);
      openPointsData.setResponsibleName(this.responsibleName);
      openPointsData.setResult(this.resultStatus);
      // openPointsData.set
      openPointsData.setOprType(CommonUIConstants.CHAR_CONSTANT_FOR_ADD);
      List<OpenPointsData> input =
          (List<OpenPointsData>) this.questionResponseComposite.getOpenPointsTabViewer().getInput();
      if (null == input) {
        input = new ArrayList<>();
      }
      input.add(openPointsData);
      this.questionResponseComposite.getOpenPointsTabViewer().setInput(input);
      this.questionResponseComposite.getOpenPointsTabViewer().refresh();
    }
    else {
      this.opData.setDate(this.dateStr);
      this.opData.setMeasures(this.measureStr);
      this.opData.setOpenPoint(this.openPointStr);
      this.opData.setResponsibleId(this.responsibleId);
      this.opData.setResponsibleName(this.responsibleName);
      this.opData.setResult(this.resultStatus);
      if (this.opData.getAnsOpenPointObj() == null) {
        this.opData.setOprType(CommonUIConstants.CHAR_CONSTANT_FOR_ADD);
      }
      else {
        this.opData.setOprType(CommonUIConstants.CHAR_CONSTANT_FOR_EDIT);
      }
      this.questionResponseComposite.getOpenPointsTabViewer()
          .setInput(this.questionResponseComposite.getOpenPointsTabViewer().getInput());
      this.questionResponseComposite.getOpenPointsTabViewer().refresh();
    }
    clearValues();
    super.okPressed();
  }

  /**
   *
   */
  private void clearValues() {
    this.dateStr = null;
    this.measureStr = null;
    this.openPointStr = null;
    this.responsibleId = null;
    this.responsibleName = null;
    this.resultStatus = false;
  }
}
