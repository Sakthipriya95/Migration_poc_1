/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.pages.QuestionnaireDetailsSection;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.ws.rest.client.cdr.QnaireVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.QuestionnaireServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;

/**
 * @author svj7cob
 */
// iCDM-1968
public class QuestionnaireDetailsDialog extends AbstractDialog {


  /**
   * formToolkit the form tool kit
   */
  private FormToolkit formToolkit;

  /**
   * folder tab folder
   */
  private TabFolder tabFolder;
  /**
   * the Questionare composite
   */
  private Composite columnConfigComposite;

  /**
   * the detail Section
   */
  private final QuestionnaireDetailsSection detailsSection;

  /**
   * the save button
   */
  private Button saveBtn;
  /**
   * the measurement relevant check
   */
  private Button measmtRelevantCheck;
  /**
   * the measurement hide check
   */
  private Button measmtHideCheck;
  /**
   * the series relevant check
   */
  private Button seriesRelevantCheck;


  /**
   * the series hide Check
   */
  private Button seriesHideCheck;


  /**
   * the link relevant Check
   */
  private Button linkRelevantCheck;


  /**
   * the line hide check
   */
  private Button linkHideCheck;


  /**
   * the remark relevant check
   */
  private Button remarkRelevantCheck;


  /**
   * the remark hide check
   */
  private Button remarkHideCheck;


  /**
   * open relevant check
   */
  private Button openRelevantCheck;


  /**
   * open hide check
   */
  private Button openHideCheck;

  /**
   * measures Relavent check
   */
  private Button measuresRelaventCheck;
  /**
   * measures Hide check
   */
  private Button measuresHideCheck;
  /**
   * date Relavent check
   */
  private Button dateRelaventCheck;
  /**
   * date hide check
   */
  private Button dateHideCheck;
  /**
   * responsible relavent check
   */
  private Button responsibleRelaventCheck;
  /**
   * responsible hide check
   */
  private Button responsibleHideCheck;

  /**
   * result relevant check
   */
  private Button resultRelevantCheck;


  /**
   * the result hide check
   */
  private Button resultHideCheck;

  /**
   * the questionare version
   */
  private final QuestionnaireVersion questionnaireVersion;

  /**
   * the questionare
   */
  private final Questionnaire questionnaire;

  /**
   * flag to check the fields of questionare
   */
  private boolean isQuestFieldsChgedFlag;

  /**
   * flag to check the fields of questionare version
   */
  private boolean isQuestVersFieldsChgedFlag;

  private final WorkPackageDivision workPackageDetail;

  /**
   * font for the column names
   */
  private static final String SEGOE_FONT = "Segoe UI";

  /**
   * the questionnaire details dialog's width
   */
  private static final int QSTN_DETAILS_DIALOG_WIDTH = 900;
  /**
   * the questionnaire details dialog's height
   */
  private static final int QUSTN_DETAILS_DIALOG_HEIGHT = 650;
  /**
   * the questionnaire version dialog grid layout number columns
   */
  private static final int QUSTN_VERS_COLUM_NO = 3;
  /**
   * the questionnaire version horizontal spacing
   */
  private static final int QUSTN_VERS_HOR_SPACING = 65;
  /**
   * the questionnaire version font height
   */
  private static final int QUSTN_VERS_FONT_HEIGHT = 10;


  /**
   * This constructor used to create Questionnaire Details Dialog, consists of Questionare and Questionare Version in
   * seperate tab items.
   *
   * @param parentShell instance
   * @param questionnaire the questionnaire
   * @param workPackageDetail the work package detail
   * @param questionnaireVersion the questionnaire version
   */
  public QuestionnaireDetailsDialog(final Shell parentShell, final Questionnaire questionnaire,
      final WorkPackageDivision workPackageDetail, final QuestionnaireVersion questionnaireVersion) {
    super(parentShell);
    this.detailsSection = new QuestionnaireDetailsSection();
    this.questionnaireVersion = questionnaireVersion;
    this.questionnaire = questionnaire;
    this.workPackageDetail = workPackageDetail;
  }

  /**
   * This shell configures the Text and Size of the dialog
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Title modified
    newShell.setText("Edit Questionnaire Details");

    // setting the size of the shell
    int frameX = newShell.getSize().x - newShell.getClientArea().width;
    int frameY = newShell.getSize().y - newShell.getClientArea().height;
    newShell.setSize(QSTN_DETAILS_DIALOG_WIDTH + frameX, QUSTN_DETAILS_DIALOG_HEIGHT + frameY);
    super.configureShell(newShell);
  }

  /**
   * By default returns true
   */
  @Override
  protected boolean isResizable() {
    // if true, the user can resize the dialog
    return true;
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {

    final Composite composite = new Composite(parent, SWT.None);
    initializeDialogUnits(composite);

    parent.setLayout(new GridLayout());
    parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    composite.setLayout(new GridLayout());
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.tabFolder = new TabFolder(composite, SWT.NONE);
    GridLayout layout = new GridLayout();
    this.tabFolder.setLayout(layout);
    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.FILL;
    this.tabFolder.setLayoutData(gridData);

    // tab-1
    createQuestNaireDetailsComposite();

    // tab-2
    createColumnConfigComposite(gridData, layout);
    // create buttons
    createButtonBar(parent);
    return composite;
  }


  /**
   * creates the questionnaire details composite
   */
  private void createQuestNaireDetailsComposite() {

    final TabItem questionareTabItem = new TabItem(this.tabFolder, SWT.NONE);
    questionareTabItem.setText("Questionnaire Details");
    Composite questNaireDetailsComposite = this.detailsSection.createComposite(this.tabFolder);
    questNaireDetailsComposite.setLayout(new GridLayout());

    // populate the data for the questionnaire from the database
    populateQuestionareSection();

    // adding the listener for the data changes into all the fields
    addListenerToQuestionareFields();

    questionareTabItem.setControl(questNaireDetailsComposite);
  }

  /**
   * creates the column configuration composite
   *
   * @param layout layout
   * @param gridData gridData
   */
  private void createColumnConfigComposite(final GridData gridData, final GridLayout layout) {
    final TabItem questionareVersionTabItem = new TabItem(this.tabFolder, SWT.NONE);
    questionareVersionTabItem.setText("Column Configuration");
    this.columnConfigComposite = new Composite(this.tabFolder, SWT.None);
    this.columnConfigComposite.setLayoutData(gridData);
    this.columnConfigComposite.setLayout(layout);
    this.columnConfigComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    createQuestionareVersionSection();

    // populate the data for the questionnaire version from the database
    populateQuestionareVersionSection();

    // adding the listener for the data changes into all the fields
    addListenerToQuestionareVersionFields();

    questionareVersionTabItem.setControl(this.columnConfigComposite);
  }

  /**
   * add Listener To Questionare fields
   */
  private void addListenerToQuestionareFields() {
    // getting the modification listener for data changes
    ModifyListener modifyListener = getModifyListener();

    // adding the listener for the data changes into all the fields
    this.detailsSection.getEngNameText().addModifyListener(modifyListener);
    this.detailsSection.getDescEngTxt().addModifyListener(modifyListener);
    this.detailsSection.getGerName().addModifyListener(modifyListener);
    this.detailsSection.getDescGerTxt().addModifyListener(modifyListener);
  }

  /**
   * add Listener To Questionare Version Fields
   */
  private void addListenerToQuestionareVersionFields() {

    // getting the selection listener for data changes
    SelectionListener selectionListener = getSelectionListener();

    // adding the same listener to all the fields
    this.measmtRelevantCheck.addSelectionListener(selectionListener);
    this.seriesRelevantCheck.addSelectionListener(selectionListener);
    this.linkRelevantCheck.addSelectionListener(selectionListener);
    this.remarkRelevantCheck.addSelectionListener(selectionListener);
    this.openRelevantCheck.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (QuestionnaireDetailsDialog.this.openRelevantCheck.getSelection()) {
          QuestionnaireDetailsDialog.this.measuresRelaventCheck.setEnabled(true);
          QuestionnaireDetailsDialog.this.responsibleRelaventCheck.setEnabled(true);
          QuestionnaireDetailsDialog.this.dateRelaventCheck.setEnabled(true);
        }
        else {
          QuestionnaireDetailsDialog.this.measuresRelaventCheck.setEnabled(false);
          QuestionnaireDetailsDialog.this.responsibleRelaventCheck.setEnabled(false);
          QuestionnaireDetailsDialog.this.dateRelaventCheck.setEnabled(false);
          QuestionnaireDetailsDialog.this.measuresRelaventCheck.setSelection(false);
          QuestionnaireDetailsDialog.this.responsibleRelaventCheck.setSelection(false);
          QuestionnaireDetailsDialog.this.dateRelaventCheck.setSelection(false);
        }
      }
    });
    this.measuresRelaventCheck.addSelectionListener(selectionListener);
    this.responsibleRelaventCheck.addSelectionListener(selectionListener);
    this.dateRelaventCheck.addSelectionListener(selectionListener);
    this.resultRelevantCheck.addSelectionListener(selectionListener);


    this.measmtHideCheck.addSelectionListener(selectionListener);
    this.seriesHideCheck.addSelectionListener(selectionListener);
    this.linkHideCheck.addSelectionListener(selectionListener);
    this.remarkHideCheck.addSelectionListener(selectionListener);
    this.openHideCheck.addSelectionListener(selectionListener);
    this.measuresHideCheck.addSelectionListener(selectionListener);
    this.responsibleHideCheck.addSelectionListener(selectionListener);
    this.dateHideCheck.addSelectionListener(selectionListener);
    this.resultHideCheck.addSelectionListener(selectionListener);
    this.detailsSection.getEquiGenQuesChkBtn().addSelectionListener(selectionListener);
    this.detailsSection.getNoNegativeAnswersChkBox().addSelectionListener(selectionListener);

  }


  /**
   * This method listens to the change in the questionare version all fields checkbox
   *
   * @return SelectionListener
   */
  private SelectionListener getSelectionListener() {
    return new SelectionListener() {

      /**
       * Method to validate the mandatory fields and then enable the Save button
       */
      @Override
      public void widgetSelected(final SelectionEvent exp) {

        // enabling the save button based on the validation
        checkSaveBtnEnable();
      }

      /**
       * This method not needed for any implementation. This method required to avoid error "The type new
       * SelectionListener(){} must implement the inherited abstract method "
       */
      @Override
      public void widgetDefaultSelected(final SelectionEvent exp) {
        // Not needed
      }

    };
  }

  /**
   * This method listens to the change in the questionare all fields text boxes
   *
   * @return ModifyListener
   */
  private ModifyListener getModifyListener() {
    return exp -> checkSaveBtnEnable();
  }

  /**
   * This method populate Questionare Section fields from data base
   */
  private void populateQuestionareSection() {
    Questionnaire questionare = this.questionnaire;
    this.detailsSection.getEngNameText().setText(questionare.getNameEng());
    if (null != questionare.getNameGer()) {
      this.detailsSection.getGerName().setText(questionare.getNameGer());
    }
    if (null != questionare.getDescEng()) {
      this.detailsSection.getDescEngTxt().setText(questionare.getDescEng());
    }
    if (null != questionare.getDescGer()) {
      this.detailsSection.getDescGerTxt().setText(questionare.getDescGer());
    }
    if (null != questionare.getWpDivId()) {
      this.detailsSection.getDivsionCombo().setText(this.workPackageDetail.getDivName());
    }
    this.detailsSection.getDivsionCombo().setEnabled(false);
  }

  /**
   * This method populate Questionare fields from data base
   */
  private void populateQuestionareVersionSection() {

    QuestionnaireVersion qnaireVersion = this.questionnaireVersion;
    // Relevant flag part
    this.measmtRelevantCheck.setSelection(getBooleanType(qnaireVersion.getMeasurementRelevantFlag()));
    this.seriesRelevantCheck.setSelection(getBooleanType(qnaireVersion.getSeriesRelevantFlag()));
    this.linkRelevantCheck.setSelection(getBooleanType(qnaireVersion.getLinkRelevantFlag()));
    this.remarkRelevantCheck.setSelection(getBooleanType(qnaireVersion.getRemarkRelevantFlag()));
    this.openRelevantCheck.setSelection(getBooleanType(qnaireVersion.getOpenPointsRelevantFlag()));
    this.measuresRelaventCheck.setSelection(getBooleanType(qnaireVersion.getMeasureRelaventFlag()));
    this.responsibleRelaventCheck.setSelection(getBooleanType(qnaireVersion.getResponsibleRelaventFlag()));
    this.dateRelaventCheck.setSelection(getBooleanType(qnaireVersion.getCompletionDateRelaventFlag()));
    this.resultRelevantCheck.setSelection(getBooleanType(qnaireVersion.getResultRelevantFlag()));

    // Hidden flag part
    this.measmtHideCheck.setSelection(getBooleanType(qnaireVersion.getMeasurementHiddenFlag()));
    this.seriesHideCheck.setSelection(getBooleanType(qnaireVersion.getSeriesHiddenFlag()));
    this.linkHideCheck.setSelection(getBooleanType(qnaireVersion.getLinkHiddenFlag()));
    this.remarkHideCheck.setSelection(getBooleanType(qnaireVersion.getRemarksHiddenFlag()));
    this.openHideCheck.setSelection(getBooleanType(qnaireVersion.getOpenPointsHiddenFlag()));
    this.measuresHideCheck.setSelection(getBooleanType(qnaireVersion.getMeasureHiddenFlag()));
    this.responsibleHideCheck.setSelection(getBooleanType(qnaireVersion.getResponsibleHiddenFlag()));
    this.dateHideCheck.setSelection(getBooleanType(qnaireVersion.getCompletionDateHiddenFlag()));
    this.resultHideCheck.setSelection(getBooleanType(qnaireVersion.getResultHiddenFlag()));

    // populate "General Questionnaire not required" check box
    this.detailsSection.getEquiGenQuesChkBtn().setSelection(getBooleanType(qnaireVersion.getGenQuesEquivalent()));
    this.detailsSection.getNoNegativeAnswersChkBox().setSelection(getBooleanType(qnaireVersion.getNoNegativeAnsAllowedFlag()));

  }

  /**
   * This method converts the String input 'Y' and 'N' to true and false correspondingly
   *
   * @param yesOrNoString 'Y' or 'N'
   * @return true or false
   */
  private static Boolean getBooleanType(final String yesOrNoString) {
    if (yesOrNoString == null) {
      return false;
    }
    else if (CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue().equals(yesOrNoString)) {
      return true;
    }
    return false;
  }

  /**
   * This method converts the boolean input true and false to 'Y' and 'N' correspondingly
   *
   * @param flag true or false
   * @return 'Y' or 'N'
   */
  private static String getStringType(final boolean flag) {
    if (flag) {
      return CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue();
    }
    return CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue();
  }

  /**
   * This method creates Questionare Version Section
   */
  private void createQuestionareVersionSection() {
    Section questionareVersionSection =
        getFormToolkit().createSection(this.columnConfigComposite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    questionareVersionSection.setText("Column Configuration");
    questionareVersionSection.setDescription("");
    questionareVersionSection.setExpanded(true);
    questionareVersionSection.getDescriptionControl().setEnabled(true);

    GridLayout layout = new GridLayout(QUSTN_VERS_COLUM_NO, true);
    layout.horizontalSpacing = QUSTN_VERS_HOR_SPACING;
    questionareVersionSection.setLayout(layout);
    questionareVersionSection.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true));


    final Form questionareVersionForm = this.formToolkit.createForm(questionareVersionSection);
    questionareVersionForm.getBody().setLayout(layout);
    questionareVersionForm.setLayoutData(new GridData(SWT.LEFT, SWT.FILL | SWT.CENTER, true, true));
    questionareVersionForm.setLayout(layout);

    Composite composite = questionareVersionForm.getBody();
    composite.setLayout(layout);
    composite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true));

    Font boldFont = new Font(composite.getDisplay(), SEGOE_FONT, QUSTN_VERS_FONT_HEIGHT, SWT.BOLD);
    LabelUtil.getInstance().createLabel(composite, "Parameter").setFont(boldFont);
    LabelUtil.getInstance().createLabel(composite, "Relevant").setFont(boldFont);
    LabelUtil.getInstance().createLabel(composite, "Hide").setFont(boldFont);

    LabelUtil.getInstance().createLabel(composite,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_MEASURABLE_Y_N));
    this.measmtRelevantCheck = new Button(composite, SWT.CHECK);
    this.measmtHideCheck = new Button(composite, SWT.CHECK);

    LabelUtil.getInstance().createLabel(composite,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_SERIES_MAT_Y_N));
    this.seriesRelevantCheck = new Button(composite, SWT.CHECK);
    this.seriesHideCheck = new Button(composite, SWT.CHECK);

    LabelUtil.getInstance().createLabel(composite, "Link");
    this.linkRelevantCheck = new Button(composite, SWT.CHECK);
    this.linkHideCheck = new Button(composite, SWT.CHECK);

    LabelUtil.getInstance().createLabel(composite,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_REMARK));
    this.remarkRelevantCheck = new Button(composite, SWT.CHECK);
    this.remarkHideCheck = new Button(composite, SWT.CHECK);
    // ICDM-2188
    LabelUtil.getInstance().createLabel(composite,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_OPEN_POINTS));
    this.openRelevantCheck = new Button(composite, SWT.CHECK);
    this.openHideCheck = new Button(composite, SWT.CHECK);

    LabelUtil.getInstance().createLabel(composite,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_MEASURE));
    this.measuresRelaventCheck = new Button(composite, SWT.CHECK);
    this.measuresHideCheck = new Button(composite, SWT.CHECK);

    LabelUtil.getInstance().createLabel(composite,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_RESPONSIBLE));
    this.responsibleRelaventCheck = new Button(composite, SWT.CHECK);
    this.responsibleHideCheck = new Button(composite, SWT.CHECK);

    LabelUtil.getInstance().createLabel(composite,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_DATE));
    this.dateRelaventCheck = new Button(composite, SWT.CHECK);
    this.dateHideCheck = new Button(composite, SWT.CHECK);

    LabelUtil.getInstance().createLabel(composite,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_RESULT));
    this.resultRelevantCheck = new Button(composite, SWT.CHECK);
    this.resultHideCheck = new Button(composite, SWT.CHECK);

    questionareVersionSection.setClient(questionareVersionForm);
  }

  /**
   * creates buttons for the button bar
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", false);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * get the form tool kit
   *
   * @return formtool kit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * This method will be invoked after clicking the Save button
   */
  @Override
  protected void okPressed() {
    if (this.isQuestFieldsChgedFlag) {
      updateQnaire();
    }
    if (this.isQuestVersFieldsChgedFlag) {
      updateQnaireVersion();
    }
    super.okPressed();
  }

  /**
   * Checks if atleast Tab item changed from the original
   *
   * @return
   */
  private boolean isAtleastOneTabChanged() {
    // validates the field of questionnaire changed
    checkIfQuestionareFieldsChanged();

    // validates the field of questionnaire version changed
    checkIfQuestionareVersionFieldsChanged();

    return this.isQuestFieldsChgedFlag || this.isQuestVersFieldsChgedFlag;
  }

  /**
   * Check for the save button enabling
   */
  public void checkSaveBtnEnable() {
    // validating the field changes from Mandatory fields and one tab changes in the dialog
    this.saveBtn.setEnabled(checkIfMandatoryFieldsValid() && isAtleastOneTabChanged());
  }

  /**
   * This method validates text & combo fields
   *
   * @return boolean
   */
  private boolean checkIfMandatoryFieldsValid() {
    // validate for eng name ,desc eng and version desc
    return !(CommonUtils.isEmptyString(this.detailsSection.getEngNameText().getText()) ||
        CommonUtils.isEmptyString(this.detailsSection.getDescEngTxt().getText()));
  }

  /**
   * check If Questionare Fields Changed
   *
   * @return boolean
   */
  private boolean checkIfQuestionareFieldsChanged() {
    if (getNameDescriptionEngCheck() || getNameDescriptionGerCheck() || getGenQuesRequiredCheck()|| getNoNegativeAnswersAllowedCheck()) {
      this.isQuestFieldsChgedFlag = true;
    }
    else {
      this.isQuestFieldsChgedFlag = false;
    }
    return this.isQuestFieldsChgedFlag;
  }

  /**
   * @return
   */
  private boolean getGenQuesRequiredCheck() {
    return (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getGenQuesEquivalent()),
        this.detailsSection.getEquiGenQuesChkBtn().getSelection()));
  }
  
  private boolean getNoNegativeAnswersAllowedCheck() {
    return (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getNoNegativeAnsAllowedFlag()),
        this.detailsSection.getNoNegativeAnswersChkBox().getSelection()));
  }

  /**
   * check If Questionare Fields Eng Changed
   *
   * @return true if changed
   */
  private boolean getNameDescriptionEngCheck() {
    return (CommonUtils.isNotEqual(this.questionnaire.getNameEng(), this.detailsSection.getEngNameText().getText())) ||
        (CommonUtils.isNotEqual(this.questionnaire.getDescEng(), this.detailsSection.getDescEngTxt().getText()));
  }

  /**
   * check If Questionare Fields Ger Changed
   *
   * @return true if changed
   */
  private boolean getNameDescriptionGerCheck() {
    return (CommonUtils.isNotEqual(this.questionnaire.getNameGer(), this.detailsSection.getGerName().getText())) ||
        (CommonUtils.isNotEqual(this.questionnaire.getDescGer(), this.detailsSection.getDescGerTxt().getText()));
  }

  /**
   * check If Questionare Version Fields Changed
   *
   * @return boolean
   */
  private boolean checkIfQuestionareVersionFieldsChanged() {
    checkIfRelevantFieldsChanged();
    if (!this.isQuestVersFieldsChgedFlag) {
      checkIfHiddenFieldsChanged();
    }
    return this.isQuestVersFieldsChgedFlag;
  }

  /**
   * check if hidden flags are changed
   */
  private boolean checkIfHiddenFieldsChanged() {
    if (getAllHiddenFieldsFlagCheck()) {
      this.isQuestVersFieldsChgedFlag = true;
    }
    else {
      this.isQuestVersFieldsChgedFlag = false;
    }
    return this.isQuestVersFieldsChgedFlag;
  }

  private boolean getAllHiddenFieldsFlagCheck() {
    return checkMeasSerLinkRemFlags() || checkOpnptMeasResCompFlags() ||
        (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getResultHiddenFlag()),
            this.resultHideCheck.getSelection()));
  }

  /**
   * @return
   */
  private boolean checkOpnptMeasResCompFlags() {
    return (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getOpenPointsHiddenFlag()),
        this.openHideCheck.getSelection())) ||
        (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getMeasureHiddenFlag()),
            this.measuresRelaventCheck.getSelection())) ||
        (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getResponsibleHiddenFlag()),
            this.responsibleRelaventCheck.getSelection())) ||
        (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getCompletionDateHiddenFlag()),
            this.dateRelaventCheck.getSelection()));
  }

  /**
   * @return
   */
  private boolean checkMeasSerLinkRemFlags() {
    return CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getMeasurementHiddenFlag()),
        this.measmtHideCheck.getSelection()) ||
        (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getSeriesHiddenFlag()),
            this.seriesHideCheck.getSelection())) ||
        (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getLinkHiddenFlag()),
            this.linkHideCheck.getSelection())) ||
        (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getRemarksHiddenFlag()),
            this.remarkHideCheck.getSelection()));
  }

  /**
   * check if the relevant fields are changed
   */
  private boolean checkIfRelevantFieldsChanged() {
    if (getAllRelevantFieldsFlagCheck()) {
      this.isQuestVersFieldsChgedFlag = true;
    }
    else {
      this.isQuestVersFieldsChgedFlag = false;
    }
    return this.isQuestVersFieldsChgedFlag;
  }


  private boolean getAllRelevantFieldsFlagCheck() {
    return checkMeasSerLinkRemRelevant() || checkOpnptMeasRespCompRelevant() ||
        (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getResultRelevantFlag()),
            this.resultRelevantCheck.getSelection()));
  }

  /**
   * @return
   */
  private boolean checkOpnptMeasRespCompRelevant() {
    return (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getOpenPointsRelevantFlag()),
        this.openRelevantCheck.getSelection())) ||
        (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getMeasureRelaventFlag()),
            this.measuresRelaventCheck.getSelection())) ||
        (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getResponsibleRelaventFlag()),
            this.responsibleRelaventCheck.getSelection())) ||
        (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getCompletionDateRelaventFlag()),
            this.dateRelaventCheck.getSelection()));
  }

  /**
   * @return
   */
  private boolean checkMeasSerLinkRemRelevant() {
    return CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getMeasurementRelevantFlag()),
        this.measmtRelevantCheck.getSelection()) ||
        (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getSeriesRelevantFlag()),
            this.seriesRelevantCheck.getSelection())) ||
        (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getLinkRelevantFlag()),
            this.linkRelevantCheck.getSelection())) ||
        (CommonUtils.isNotEqual(getBooleanType(this.questionnaireVersion.getRemarkRelevantFlag()),
            this.remarkRelevantCheck.getSelection()));
  }

  /**
   * Update Command for Question Version
   */
  private void updateQnaireVersion() {
    QuestionnaireVersion qVersToUpdate;
    qVersToUpdate = this.questionnaireVersion.clone();
    // set updated values
    qVersToUpdate.setMeasurementRelevantFlag(getStringType(this.measmtRelevantCheck.getSelection()));
    qVersToUpdate.setSeriesRelevantFlag(getStringType(this.seriesRelevantCheck.getSelection()));
    qVersToUpdate.setLinkRelevantFlag(getStringType(this.linkRelevantCheck.getSelection()));
    qVersToUpdate.setRemarkRelevantFlag(getStringType(this.remarkRelevantCheck.getSelection()));
    qVersToUpdate.setOpenPointsRelevantFlag(getStringType(this.openRelevantCheck.getSelection()));
    qVersToUpdate.setMeasureRelaventFlag(getStringType(this.measuresRelaventCheck.getSelection()));
    qVersToUpdate.setResponsibleRelaventFlag(getStringType(this.responsibleRelaventCheck.getSelection()));
    qVersToUpdate.setCompletionDateRelaventFlag(getStringType(this.dateRelaventCheck.getSelection()));
    qVersToUpdate.setResultRelevantFlag(getStringType(this.resultRelevantCheck.getSelection()));

    qVersToUpdate.setMeasurementHiddenFlag(getStringType(this.measmtHideCheck.getSelection()));
    qVersToUpdate.setSeriesHiddenFlag(getStringType(this.seriesHideCheck.getSelection()));
    qVersToUpdate.setLinkHiddenFlag(getStringType(this.linkHideCheck.getSelection()));
    qVersToUpdate.setRemarksHiddenFlag(getStringType(this.remarkHideCheck.getSelection()));
    qVersToUpdate.setOpenPointsHiddenFlag(getStringType(this.openHideCheck.getSelection()));
    qVersToUpdate.setMeasureHiddenFlag(getStringType(this.measuresHideCheck.getSelection()));
    qVersToUpdate.setResponsibleHiddenFlag(getStringType(this.responsibleHideCheck.getSelection()));
    qVersToUpdate.setCompletionDateHiddenFlag(getStringType(this.dateHideCheck.getSelection()));
    qVersToUpdate.setResultHiddenFlag(getStringType(this.resultHideCheck.getSelection()));
    qVersToUpdate.setGenQuesEquivalent(getStringType(this.detailsSection.getEquiGenQuesChkBtn().getSelection()));
    qVersToUpdate.setNoNegativeAnsAllowedFlag(getStringType(this.detailsSection.getNoNegativeAnswersChkBox().getSelection()));

    try {
      QuestionnaireVersion updatedQVersion = new QnaireVersionServiceClient().update(qVersToUpdate);
      CommonUtils.shallowCopy(this.questionnaireVersion, updatedQVersion);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * Update Command for Questionare
   */
  private void updateQnaire() {
    Questionnaire qnaireToUpdate;
    if (this.isQuestFieldsChgedFlag) {
      qnaireToUpdate = this.questionnaire.clone();
      // set edited fields in cloned object
      qnaireToUpdate.setDescEng(this.detailsSection.getDescEngTxt().getText());
      qnaireToUpdate.setDescGer(this.detailsSection.getDescGerTxt().getText());
      Questionnaire updatedQnaire;
      try {
        updatedQnaire = new QuestionnaireServiceClient().update(qnaireToUpdate);
        CommonUtils.shallowCopy(this.questionnaire, updatedQnaire);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }


}
