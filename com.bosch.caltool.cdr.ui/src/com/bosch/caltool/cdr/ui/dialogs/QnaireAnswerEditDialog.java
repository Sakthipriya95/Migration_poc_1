/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.QnaireResponseEditor;
import com.bosch.caltool.cdr.ui.editors.pages.QnaireAnswerEditComposite;
import com.bosch.caltool.cdr.ui.editors.pages.QnaireRespSummaryPage;
import com.bosch.caltool.cdr.ui.views.QnaireRespOutlinePage;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;


/**
 * Question response edit dialog
 *
 * @author mkl2cob
 */
public class QnaireAnswerEditDialog extends AbstractDialog {

  /**
   * QuestionResponse private final RvwQnaireAnswer quesResp;
   */
  /**
   * Save button instance
   */
  private Button saveBtn;
  /**
   * Qusetion response composite instance
   */
  private QnaireAnswerEditComposite quesRespComposite;

  public final QnaireRespEditorDataHandler dataHandler;

  private RvwQnaireAnswer rvwQnaireAns;

  final QnaireRespSummaryPage natPage;

  private final QnaireResponseEditor responseEditor;
  private Button nxtQuestionBtn;
  private List<RvwQnaireAnswer> rvwQnairAnswerList;
  private Button prevQuestionBtn;

  /**
   * @param parentShell parent shell
   * @param rvwQnaireAns as input
   * @param dataHandler QnaireRespEditorDataHandler
   * @param natPage qnaireSummarypage
   * @param responseEditor as input
   */
  public QnaireAnswerEditDialog(final Shell parentShell, final RvwQnaireAnswer rvwQnaireAns,
      final QnaireRespEditorDataHandler dataHandler, final QnaireRespSummaryPage natPage,
      final QnaireResponseEditor responseEditor) {
    super(parentShell);
    this.natPage = natPage;
    this.rvwQnaireAns = rvwQnaireAns;
    this.dataHandler = dataHandler;
    this.responseEditor = responseEditor;
  }

  /**
   * create contents
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Question Response Edit Dialog");
    // Set the message
    setMessage("Edit the details and click on Save button");
    return contents;
  }

  /**
   * configure the shell and set the title
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell name
    newShell.setText("Question Response");
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
    Composite top = (Composite) super.createDialogArea(parent);
    top.setLayout(new GridLayout());
    // create the composite
    this.quesRespComposite = new QnaireAnswerEditComposite(top, this.dataHandler, this.rvwQnaireAns,
        new FormToolkit(Display.getCurrent()), true, this, this.responseEditor);
    return top;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    this.prevQuestionBtn = createButton(parent, IDialogConstants.BACK_ID, "< Previous Question", true);
    this.prevQuestionBtn.setToolTipText("Save and Move to Previous Question");
    this.prevQuestionBtn.setEnabled(false);
    this.nxtQuestionBtn = createButton(parent, IDialogConstants.NEXT_ID, "Next Question >", true);
    this.nxtQuestionBtn.setToolTipText("Save and Move to Next Question");
    this.nxtQuestionBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    initializeRvwQNaireAnswerList();
    this.quesRespComposite.enableDisableSaveBtn();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void buttonPressed(final int buttonId) {
    boolean isVersionCreated = false;
    saveRvwAnswer();
    initializeRvwQNaireAnswerList();
    if (IDialogConstants.NEXT_ID == buttonId) {
      processNextQuestion(isVersionCreated);
    }
    else if (IDialogConstants.BACK_ID == buttonId) {
      processBackPressed();
    }
    super.buttonPressed(buttonId);
  }

  /**
   *
   */
  private void processBackPressed() {
    int nextIndex = this.rvwQnairAnswerList.indexOf(this.rvwQnaireAns);
    nextIndex--;
    if ((nextIndex >= 0) && (this.rvwQnairAnswerList.size() > nextIndex) &&
        (null != this.rvwQnairAnswerList.get(nextIndex))) {
      this.rvwQnaireAns = this.rvwQnairAnswerList.get(nextIndex);
      while (!this.dataHandler.isQuestionVisible(this.rvwQnaireAns)) {
        nextIndex--;
        this.rvwQnaireAns = this.rvwQnairAnswerList.get(nextIndex);
        if (nextIndex <= 0) {
          this.prevQuestionBtn.setEnabled(false);
          break;
        }
      }
    }
    if (nextIndex <= 0) {
      this.prevQuestionBtn.setEnabled(false);
    }
    refreshByDisposeAndCreate();
  }

  /**
   * @param versionToBeCreated
   */
  private void processNextQuestion(boolean versionToBeCreated) {
    int nextIndex = this.rvwQnairAnswerList.indexOf(this.rvwQnaireAns);
    nextIndex++;
    if ((this.rvwQnairAnswerList.size() > nextIndex) && (null != this.rvwQnairAnswerList.get(nextIndex))) {
      this.rvwQnaireAns = this.rvwQnairAnswerList.get(nextIndex);
      while (!this.dataHandler.isQuestionVisible(this.rvwQnaireAns)) {
        nextIndex++;
        if ((this.rvwQnairAnswerList.size() > nextIndex) && (null != this.rvwQnairAnswerList.get(nextIndex))) {
          this.rvwQnaireAns = this.rvwQnairAnswerList.get(nextIndex);
        }
        else {
          nextIndex--;
          this.rvwQnaireAns = this.rvwQnairAnswerList.get(nextIndex);
        }
        if (nextIndex == (this.rvwQnairAnswerList.size() - 1)) {
          versionToBeCreated = createQnaireRespVersion();
          nextIndex--;
          this.rvwQnaireAns = this.rvwQnairAnswerList.get(nextIndex);
          break;
        }
      }
    }
    if (nextIndex == (this.rvwQnairAnswerList.size())) {
      versionToBeCreated = createQnaireRespVersion();
    }
    if (!versionToBeCreated) {
      refreshByDisposeAndCreate();
    }
  }

  /**
   * @return
   */
  private boolean createQnaireRespVersion() {
    boolean openConfirm = MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Confirmation Dialog",
        "No more questions to display, Do you want to create a version ?");
    if (openConfirm) {
      this.natPage.createQnaireRespVersion();
      super.close();
    }
    return openConfirm;
  }

  /**
   * dispose the existing composite and create a new one
   */
  public void refreshByDisposeAndCreate() {
    Composite parent = this.quesRespComposite.getParent();
    Control[] children = this.quesRespComposite.getParent().getChildren();
    // dispose all children of composite
    for (Control childOfComposite : children) {
      childOfComposite.dispose();
    }
    // create the composite
    this.quesRespComposite = new QnaireAnswerEditComposite(parent, this.dataHandler, this.rvwQnaireAns,
        new FormToolkit(Display.getCurrent()), true, this, this.responseEditor);
    parent.layout();
  }

  /**
   * This method initializes the review questionnaire answers list
   */
  private void initializeRvwQNaireAnswerList() {
    Predicate<RvwQnaireAnswer> predicate = p -> !this.dataHandler.checkHeading(p);
    this.rvwQnairAnswerList = this.dataHandler.getAllQuestionAnswers().stream().filter(predicate)
        .sorted(this.dataHandler::compareTo).collect(Collectors.toCollection(ArrayList::new));
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    saveRvwAnswer();
    super.okPressed();
  }

  /**
   *
   */
  private void saveRvwAnswer() {
    RvwQnaireAnswer updatedCmdRvwQAns = null;
    RvwQnaireRespVersion selectedQnaireRespVers = this.dataHandler.getQnaireRespModel().getRvwQnrRespVersion();
    if (dataChanged()) {
      RvwQnaireAnswer cmdRvwQAns = this.rvwQnaireAns.clone();
      cmdRvwQAns.setMeasurement(this.quesRespComposite.getMeasuremntString());
      cmdRvwQAns.setRemark(this.quesRespComposite.getRemarksStr());
      cmdRvwQAns.setResult(this.quesRespComposite.getAnswerStrType());
      cmdRvwQAns.setSeries(this.quesRespComposite.getSeriesString());
      cmdRvwQAns.setSelQnaireResultOptID(this.quesRespComposite.getAnswerOPTId());
      cmdRvwQAns.setQnaireRespVersId(selectedQnaireRespVers.getId());
      updatedCmdRvwQAns = this.dataHandler.updateRvwQnaireAns(cmdRvwQAns);
      CommonUtils.shallowCopy(this.rvwQnaireAns, updatedCmdRvwQAns);
    }
    else {
      updatedCmdRvwQAns = this.rvwQnaireAns;
    }
    if (CommonUtils.isNotEmpty(this.quesRespComposite.getLinksList()) && CommonUtils.isNotNull(updatedCmdRvwQAns)) {
      CommonUiUtils.getInstance().createMultipleLinkService(this.quesRespComposite.getLinksList(),
          updatedCmdRvwQAns.getId(), MODEL_TYPE.RVW_QNAIRE_ANS);
      this.quesRespComposite.refreshLinkTabViewer(updatedCmdRvwQAns);
    }
    if (CommonUtils.isNotEmpty(this.quesRespComposite.getOpenPointList())) {
      createMultipleOpenPointService(updatedCmdRvwQAns);
    }
    // updated the Qnaire status, after links are modified. Because Qnaire status calculation includes a check for Links
    // mandatoy and links filled
    this.dataHandler.updateQnaireRespVersStatus();
    // For each answer update check whether all qusetions are answered and refresh the outline view
    QnaireRespOutlinePage questResponseOutlinePage =
        this.responseEditor.getQuestionnaireResOutlinePageCreator().getQuestResponseOutlinePage();
    if (null != PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView(ApicUiConstants.OUTLINE_TREE_VIEW)) {
      questResponseOutlinePage.getViewer().refresh(true);
    }
    String qnaireRespVersStatus = selectedQnaireRespVers.getQnaireRespVersStatus();
    // For each answer update check whether all questions are answered and refresh the qnaire summary page
    if (CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType().equals(qnaireRespVersStatus)) {
      this.natPage.getStatusMsgLabel().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
      this.natPage.getStatusMsgLabel().setText(CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getUiType());
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP.getDbType().equals(qnaireRespVersStatus)) {
      this.natPage.getStatusMsgLabel().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
      this.natPage.getStatusMsgLabel().setText(CDRConstants.QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP.getUiType());
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType().equals(qnaireRespVersStatus)) {
      this.natPage.getStatusMsgLabel().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
      this.natPage.getStatusMsgLabel().setText(CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getUiType());
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getDbType().equals(qnaireRespVersStatus)) {
      this.natPage.getStatusMsgLabel().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
      this.natPage.getStatusMsgLabel().setText(CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getUiType());
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType().equals(qnaireRespVersStatus)) {
      this.natPage.getStatusMsgLabel().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
      this.natPage.getStatusMsgLabel().setText(CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getUiType());
    }
    this.natPage.getStatusMsgLabel().getParent().layout();
  }

  private boolean dataChanged() {
    return !CommonUtils.isEqual(this.quesRespComposite.getMeasuremntString(), this.rvwQnaireAns.getMeasurement()) ||
        validateRemRes() ||
        !CommonUtils.isEqual(this.quesRespComposite.getSeriesString(), this.rvwQnaireAns.getSeries()) ||
        // If this is a dummy answer, insert the record before further editing
        (this.rvwQnaireAns.getId().equals(this.rvwQnaireAns.getQuestionId()));
  }

  /**
   * @return
   */
  private boolean validateRemRes() {
    return !CommonUtils.isEqual(this.quesRespComposite.getRemarksStr(), this.rvwQnaireAns.getRemark()) ||
        (!CommonUtils.isEqual(this.quesRespComposite.getResultStr(), this.dataHandler.getQuestionResultOptionUIString(
            this.rvwQnaireAns.getQuestionId(), this.rvwQnaireAns.getSelQnaireResultOptID())));
  }

  /**
  *
  */
  private void createMultipleOpenPointService(final RvwQnaireAnswer updatedAns) {
    List<RvwQnaireAnswerOpl> addList = new ArrayList<>();
    List<RvwQnaireAnswerOpl> editList = new ArrayList<>();
    List<Long> delList = new ArrayList<>();
    java.text.DateFormat df15 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_15,
        Locale.getDefault(Locale.Category.FORMAT));
    java.text.DateFormat df09 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_09,
        Locale.getDefault(Locale.Category.FORMAT));
    for (OpenPointsData selOpenPoint : this.quesRespComposite.getOpenPointList()) {
      if (selOpenPoint.getOprType() == CommonUIConstants.CHAR_CONSTANT_FOR_ADD) {
        RvwQnaireAnswerOpl opl = new RvwQnaireAnswerOpl();
        opl.setMeasure(selOpenPoint.getMeasures());
        opl.setResult(CommonUtils.getBooleanCode(selOpenPoint.isResult()));
        setCompletionDate(df15, df09, selOpenPoint, opl);
        opl.setOpenPoints(selOpenPoint.getOpenPoint());
        opl.setResponsible(selOpenPoint.getResponsibleId());
        opl.setRvwAnswerId(updatedAns.getId());
        addList.add(opl);
      }
      if (selOpenPoint.getOprType() == CommonUIConstants.CHAR_CONSTANT_FOR_EDIT) {
        RvwQnaireAnswerOpl opl = selOpenPoint.getAnsOpenPointObj();
        opl.setMeasure(selOpenPoint.getMeasures());
        opl.setResult(CommonUtils.getBooleanCode(selOpenPoint.isResult()));
        setCompletionDate(df15, df09, selOpenPoint, opl);
        opl.setOpenPoints(selOpenPoint.getOpenPoint());
        opl.setResponsible(selOpenPoint.getResponsibleId());
        editList.add(opl);
      }
      if (selOpenPoint.getOprType() == CommonUIConstants.CHAR_CONSTANT_FOR_DELETE) {
        delOpl(delList, selOpenPoint);
      }
    }
    this.dataHandler.updateRvwQnaireAnsOpl(addList, editList, delList, updatedAns);
    this.quesRespComposite.getOpenPointList().clear();
    this.quesRespComposite.refreshOplTabViewer();
  }

  /**
   * To add date if itis not null
   *
   * @param df15
   * @param df09
   * @param selOpenPoint
   * @param opl
   */
  private void setCompletionDate(final java.text.DateFormat df15, final java.text.DateFormat df09,
      final OpenPointsData selOpenPoint, final RvwQnaireAnswerOpl opl) {
    if (selOpenPoint.getDate() != null) {
      Date setDate = null;
      String dateOutput = null;
      try {
        if (!validateDF15Format(df15, selOpenPoint.getDate())) {
          setDate = df09.parse(selOpenPoint.getDate());
          dateOutput = df15.format(setDate);
        }
        else {
          dateOutput = selOpenPoint.getDate();
        }
      }
      catch (ParseException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), Activator.PLUGIN_ID);
      }
      opl.setCompletionDate(dateOutput);
    }
  }


  private boolean validateDF15Format(final DateFormat df15, final String date) {
    try {
      df15.parse(date);
    }
    catch (ParseException e) {
      return false;
    }
    return true;
  }

  /**
   * @param delList List<Long>
   * @param selOpenPoint OpenPointsData
   */
  public void delOpl(final List<Long> delList, final OpenPointsData selOpenPoint) {
    RvwQnaireAnswerOpl opl = selOpenPoint.getAnsOpenPointObj();
    if (null != opl) {
      delList.add(opl.getId());
    }
  }

  /**
   * @param enable true if save btn has to be enabled
   */
  public void enableSaveBtn(final boolean enable) {
    if (null != this.saveBtn) {
      this.saveBtn.setEnabled(enable);
      this.nxtQuestionBtn.setEnabled(enable);
      if (this.rvwQnairAnswerList.indexOf(this.rvwQnaireAns) <= 0) {
        this.prevQuestionBtn.setEnabled(false);
      }
      else {
        this.prevQuestionBtn.setEnabled(enable);
      }

    }
  }
}
