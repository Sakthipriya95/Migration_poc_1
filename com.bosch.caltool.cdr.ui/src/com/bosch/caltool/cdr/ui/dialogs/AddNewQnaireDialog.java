package com.bosch.caltool.cdr.ui.dialogs;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.cdr.ui.editors.ReviewQuestionaireEditor;
import com.bosch.caltool.cdr.ui.editors.ReviewQuestionaireEditorInput;
import com.bosch.caltool.cdr.ui.editors.pages.QuestionnaireDetailsSection;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireCreationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.ws.rest.client.cdr.QuestionnaireServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * This class provides UI to add value
 */
public class AddNewQnaireDialog extends AbstractDialog {

  /**
   * Button instance for save
   */
  protected Button saveBtn;
  /**
   * Button instance for cancel
   */
  Button cancelBtn;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * top comp
   */
  private Composite top;

  /**
   * Text for adding comment when a value is edited
   */
  protected Text gerName;
  /**
   * eng name txt
   */
  private Text engNameText;
  /**
   * desc eng txt
   */
  private Text descEngTxt;
  /**
   * desc get text
   */
  private Text descGerTxt;
  /**
   * parent dialog
   */
  private final FunctionSelectionDialog parentDialog;

  /**
   * the questionnaire details section
   */
  private final QuestionnaireDetailsSection questionnaireDetailSection;
  /**
   * constant for create questionnarie
   */
  public static final String CREATE_QUESTIONARE = "Create Questionnaire";


  /**
   * constant for create new questionnarie
   */
  private static final String NEW_QUESTIONARE = "Create a new Questionnaire";
  /**
   * name must be unique
   */
  private static final String QUESTIONAIRE_NAME_UNIQUE = "Questionnaire name must be unique";

  /**
   * The Parameterized Constructor
   *
   * @param parentShell instance
   * @param dialog dialog
   */
  // ICDM-108
  public AddNewQnaireDialog(final Shell parentShell, final FunctionSelectionDialog dialog) {
    super(parentShell);
    this.parentDialog = dialog;
    // iCDM-1968
    this.questionnaireDetailSection = new QuestionnaireDetailsSection();
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
    setTitle(CREATE_QUESTIONARE);

    // Set the message
    setMessage(CREATE_QUESTIONARE);

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(NEW_QUESTIONARE);
    // ICDM-153
    super.setHelpAvailable(true);
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Create", true);
    // ICDM-112
    this.saveBtn.setEnabled(false);
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
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
    Composite composite = getFormToolkit().createComposite(this.top);
    composite.setLayout(new GridLayout());
    // iCDM-1968
    this.questionnaireDetailSection.createComposite(this.top);
    addListenerToQuestionareFields();
    composite.setLayoutData(gridData);
  }

  /**
   * add Listener To Questionare fields
   */
  private void addListenerToQuestionareFields() {
    ModifyListener modifyListener = getModifyListener();
    this.questionnaireDetailSection.getEngNameText().addModifyListener(modifyListener);
    this.questionnaireDetailSection.getDescEngTxt().addModifyListener(modifyListener);
  }

  /**
   * This method listens to the change in the questionare all fields text boxes
   *
   * @return ModifyListener
   */
  private ModifyListener getModifyListener() {
    return new ModifyListener() {

      /**
       * Action to validate the mandatory fields and then enable the save button
       */
      @Override
      public void modifyText(final ModifyEvent exp) {
        checkSaveBtnEnable();
      }
    };
  }


  /**
   * Check for the save button enabling
   */
  private void checkSaveBtnEnable() {
    this.saveBtn.setEnabled(checkIfMandatoryFieldsValid());
  }

  /**
   * This method validates text & combo fields
   *
   * @return boolean
   */
  private boolean checkIfMandatoryFieldsValid() {
    // validate for eng name ,desc eng and version desc
    return (CommonUtils.isNotEmptyString(this.questionnaireDetailSection.getEngNameText().getText()) &&
        CommonUtils.isNotEmptyString(this.questionnaireDetailSection.getDescEngTxt().getText()));

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    this.engNameText = this.questionnaireDetailSection.getEngNameText();
    this.descEngTxt = this.questionnaireDetailSection.getDescEngTxt();
    this.gerName = this.questionnaireDetailSection.getGerName();
    this.descGerTxt = this.questionnaireDetailSection.getDescGerTxt();
    String engNameTxt = this.engNameText.getText();
    String gerText = this.gerName.getText();
    Map<Long, Questionnaire> allQuestionaresMap = null;
    try {
      allQuestionaresMap = new QuestionnaireServiceClient().getAll(true, true);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.cdr.ui.Activator.PLUGIN_ID);
    }
    if (null != allQuestionaresMap) {
      SortedSet<Questionnaire> allQuestionares = new TreeSet<>();
      for (Questionnaire qnaire : allQuestionaresMap.values()) {
        allQuestionares.add(qnaire);
      }
      // Check for duplicates
      for (Questionnaire questionnaire : allQuestionares) {
        if (engNameTxt.equals(questionnaire.getNameEng()) ||
            (CommonUtils.isNotEmptyString(gerText) && gerText.equals(questionnaire.getNameGer()))) {
          CDMLogger.getInstance().errorDialog(QUESTIONAIRE_NAME_UNIQUE, Activator.PLUGIN_ID);
          return;
        }
      }
    }
    Questionnaire qnaire = createQnaire();
    if (null != qnaire) {
      openQuestionniare(qnaire);
      this.parentDialog.close();
    }
    else {
      return;
    }
    super.okPressed();
  }

  /**
   * @param questionnaire
   */
  private void openQuestionniare(final Questionnaire qnaire) {
    try {

      final ReviewQuestionaireEditorInput quesInput = new ReviewQuestionaireEditorInput(getWorkingSet(qnaire));
      if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(quesInput,
            ReviewQuestionaireEditor.EDITOR_ID);
      }
    }
    catch (WorkbenchException excep) {
      CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param qnaire Questionnaire
   * @return QuestionnaireVersion
   */
  public QuestionnaireVersion getWorkingSet(final Questionnaire qnaire) {
    QuestionnaireVersion workingSet = null;
    try {
      workingSet = new QuestionnaireServiceClient().getWorkingSet(qnaire.getId());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.cdr.ui.Activator.PLUGIN_ID);
    }
    return workingSet;
  }

  /**
   * create the command for questionnare
   */
  private Questionnaire createQnaire() {
    QnaireCreationModel qnaireCreateModelData = new QnaireCreationModel();
    Questionnaire createdQnaire = null;
    // set the fields for commands
    Questionnaire qnaire = new Questionnaire();
    qnaire.setNameEng(this.engNameText.getText());
    qnaire.setNameGer(this.gerName.getText());
    qnaire.setDescEng(this.descEngTxt.getText());
    qnaire.setDescGer(this.descGerTxt.getText());
    if (null == this.questionnaireDetailSection.getSelectedWpDiv()) {
      WorkPackageDivision wpDiv = new WorkPackageDivision();
      wpDiv.setDivAttrValId(this.questionnaireDetailSection.getSelectedAttrVal().getId());
      wpDiv.setDivName(this.questionnaireDetailSection.getSelectedAttrVal().getName());
      wpDiv.setWpId(this.questionnaireDetailSection.getSelectedWp().getId());
      qnaireCreateModelData.setWpDiv(wpDiv);
    }
    else {
      qnaireCreateModelData.setWpDiv(this.questionnaireDetailSection.getSelectedWpDiv());
      qnaire.setWpDivId(this.questionnaireDetailSection.getSelectedWpDiv().getId());
    }
    qnaire.setDivName(this.questionnaireDetailSection.getSelectedAttrVal().getName());
    qnaireCreateModelData.setQnaire(qnaire);

    QuestionnaireVersion qnaireVersion = new QuestionnaireVersion();
    qnaireVersion.setMajorVersionNum(0L);
    qnaireVersion.setMinorVersionNum(null);
    qnaireVersion.setDescEng("Working Set");
    qnaireVersion.setDescGer(null);
    qnaireVersion
        .setGenQuesEquivalent(getStringType(this.questionnaireDetailSection.getEquiGenQuesChkBtn().getSelection()));
    qnaireVersion.setNoNegativeAnsAllowedFlag(getStringType(this.questionnaireDetailSection.getNoNegativeAnswersChkBox().getSelection()));
    qnaireCreateModelData.setQnaireVersion(qnaireVersion);

    NodeAccess nodeAccess = new NodeAccess();
    nodeAccess.setGrant(true);
    nodeAccess.setOwner(true);
    nodeAccess.setWrite(true);
    nodeAccess.setRead(true);
    nodeAccess.setNodeType(MODEL_TYPE.QUESTIONNAIRE.getTypeCode());
    qnaireCreateModelData.setNodeAccess(nodeAccess);
    try {
      nodeAccess.setUserId(new CurrentUserBO().getUserID());
      qnaireCreateModelData.setNodeAccess(nodeAccess);
      createdQnaire = new QuestionnaireServiceClient().createQnaireAndVersion(qnaireCreateModelData).getDataCreated();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.cdr.ui.Activator.PLUGIN_ID);
    }
    return createdQnaire;
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
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }


}
