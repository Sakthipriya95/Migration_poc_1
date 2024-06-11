/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.editors.pages.QuestionareVersionsPage;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireDefBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextBoxContentDisplay;


/**
 * This class represents the dialog to insert/edit questionnaire version contents
 *
 * @author mkl2cob
 */
public class QuestionnaireVersionEditDialog extends AbstractDialog {

  /**
   * version string
   */
  private static final String VERSION_STR = "  Version ";
  /**
   * top composite
   */
  private Composite top;
  /**
   * save button
   */
  private Button saveBtn;
  /**
   * FormToolkit
   */
  private final FormToolkit formToolkit;
  /**
  */
  private final QnaireDefBO qnaireDefBo;
  /**
   * name text
   */
  private StyledText nameTxt;
  /**
   * big decimal for latest major version number
   */
  private Long latestMajorVersionNum;
  /**
   * big decimal for latest minor version number
   */
  private Long latestMinorVersionNum;
  /**
   * minor version number
   */
  private int minorVersionNum;
  /**
   * major version number
   */
  private int majorVersionNum;
  /**
   * active button
   */
  private Button activeBtn;
  /**
   * text for German description
   */
  private TextBoxContentDisplay txtDescGer;
  /**
   * text for English Description
   */
  private TextBoxContentDisplay txtDescEng;

  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();

  /**
   * Decorator for the Combo.
   */
  private ControlDecoration descEngDec;
  /**
   * true if the dialog is for edit
   */
  private final boolean isEdit;
  /**
   * selected questionnaire version
   */
  private QuestionnaireVersion selectedVersion;
  private final QuestionareVersionsPage questionareVersionsPage;

  /**
   * Creating version
   *
   * @param parentShell parent shell
   * @param formToolkit FormToolkit
   * @param qnaireDefEditorDataHandler Questionnaire
   * @param questionareVersionsPage
   */
  public QuestionnaireVersionEditDialog(final Shell parentShell, final FormToolkit formToolkit,
      final QnaireDefBO qnaireDefEditorDataHandler, final QuestionareVersionsPage questionareVersionsPage) {
    super(parentShell);
    this.formToolkit = formToolkit;
    this.qnaireDefBo = qnaireDefEditorDataHandler;
    this.questionareVersionsPage = questionareVersionsPage;
    this.isEdit = false;
  }

  /**
   * ICDM-2027 Editing version
   *
   * @param activeShell Active shell
   * @param formToolkit2 FormToolkit
   * @param selectedVersion Selected version
   * @param qnaireDefEditorDataHandler
   * @param questionareVersionsPage
   */
  public QuestionnaireVersionEditDialog(final Shell activeShell, final FormToolkit formToolkit2,
      final QuestionnaireVersion selectedVersion, final QnaireDefBO qnaireDefEditorDataHandler,
      final QuestionareVersionsPage questionareVersionsPage) {
    super(activeShell);
    this.formToolkit = formToolkit2;
    this.selectedVersion = selectedVersion;
    this.qnaireDefBo = qnaireDefEditorDataHandler;
    this.questionareVersionsPage = questionareVersionsPage;
    this.isEdit = true;
  }

  /**
   * create contents
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Questionnaire Version");
    // Set the message
    setMessage("Edit the details and click Save button");

    setValuesToFields();
    return contents;
  }

  /**
   * configure the shell and set the title
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell name
    if (this.isEdit) {
      newShell.setText("Edit Version");
    }
    else {
      newShell.setText("Create New Version");
    }
    newShell.setSize(500, 570);
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
    createComposite();
    return this.top;
  }

  /**
   * create Composite
   */
  private void createComposite() {
    Composite composite = this.formToolkit.createComposite(this.top);
    GridLayout layout = new GridLayout();
    composite.setLayout(layout);
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    createSection(composite);
  }

  /**
   * @param composite Composite
   */
  private void createSection(final Composite composite) {
    Section section = this.formToolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    section.setText("Version Details");
    section.setLayoutData(GridDataUtil.getInstance().getGridData());
    section.setLayout(new GridLayout());
    section.getDescriptionControl().setEnabled(false);
    createForm(section);
  }

  /**
   * @param section
   */
  private void createForm(final Section section) {
    Form form = this.formToolkit.createForm(section);
    GridLayout layout = new GridLayout();
    // divide the dialog into 2 columns
    layout.numColumns = 2;
    form.getBody().setLayout(layout);
    form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    createNameFields(form.getBody());
    createImapctFields(form.getBody());
    createActiveFields(form.getBody());
    createDescEngFields(form.getBody());
    createDescGerFields(form.getBody());

    section.setClient(form);
  }

  /**
   * @param composite Composite
   */
  private void createDescGerFields(final Composite composite) {
    LabelUtil.getInstance().createLabel(composite, "Description(German)");
    this.txtDescGer = new TextBoxContentDisplay(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, 4000,
        GridDataUtil.getInstance().getGridData());
  }

  /**
   * @param composite Composite
   */
  private void createDescEngFields(final Composite composite) {
    LabelUtil.getInstance().createEmptyLabel(composite);
    LabelUtil.getInstance().createEmptyLabel(composite);
    LabelUtil.getInstance().createLabel(composite, "Description(English)");
    this.txtDescEng = new TextBoxContentDisplay(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, 4000,
        GridDataUtil.getInstance().getGridData());
    this.descEngDec = new ControlDecoration(this.txtDescEng, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.descEngDec, "");
    this.txtDescEng.getText().addModifyListener(e -> enableDiasableSaveBtn());
    LabelUtil.getInstance().createEmptyLabel(composite);
  }

  /**
   * @param composite Composite
   */
  private void createActiveFields(final Composite composite) {
    LabelUtil.getInstance().createLabel(composite, "Active");
    this.activeBtn = new Button(composite, SWT.CHECK);
    if (!this.isEdit && (VERSION_STR + "0.1").equals(this.nameTxt.getText())) {
      // for first version , set active check box as true
      this.activeBtn.setSelection(true);
    }
    if (this.isEdit && this.qnaireDefBo.isWorkingSet(this.selectedVersion)) {
      // active version cannot be changed for working set
      this.activeBtn.setEnabled(false);
    }
  }

  /**
   * @param composite Composite
   */
  private void createImapctFields(final Composite composite) {
    LabelUtil.getInstance().createLabel(composite, "Impact");
    Composite btnComp = this.formToolkit.createComposite(composite);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    btnComp.setLayout(layout);
    btnComp.setLayoutData(GridDataUtil.getInstance().createGridData());
    Button minorBtn = new Button(btnComp, SWT.RADIO);
    minorBtn.setText("Minor");
    minorBtn.setSelection(true);
    minorBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        QuestionnaireVersionEditDialog.this.majorVersionNum =
            QuestionnaireVersionEditDialog.this.latestMajorVersionNum.intValue();
        QuestionnaireVersionEditDialog.this.minorVersionNum =
            ((QuestionnaireVersionEditDialog.this.latestMinorVersionNum) == null ? 0
                : QuestionnaireVersionEditDialog.this.latestMinorVersionNum.intValue()) + 1;
        QuestionnaireVersionEditDialog.this.nameTxt
            .setText(VERSION_STR + QuestionnaireVersionEditDialog.this.latestMajorVersionNum + "." +
                QuestionnaireVersionEditDialog.this.minorVersionNum);
      }
    });

    Button majorBtn = new Button(btnComp, SWT.RADIO);
    majorBtn.setText("Major");
    majorBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        QuestionnaireVersionEditDialog.this.majorVersionNum =
            QuestionnaireVersionEditDialog.this.latestMajorVersionNum.intValue() + 1;
        QuestionnaireVersionEditDialog.this.minorVersionNum = 0;
        QuestionnaireVersionEditDialog.this.nameTxt
            .setText(VERSION_STR + QuestionnaireVersionEditDialog.this.majorVersionNum + "." +
                QuestionnaireVersionEditDialog.this.minorVersionNum);
      }
    });

    // if it is edit, disable the impact fields
    if (this.isEdit) {
      minorBtn.setEnabled(false);
      minorBtn.setSelection(false);
      majorBtn.setEnabled(false);
    }
  }

  /**
   * create name fields
   *
   * @param composite Composite
   */
  private void createNameFields(final Composite composite) {
    LabelUtil.getInstance().createLabel(composite, "Version Name");
    this.nameTxt = new StyledText(composite, SWT.BORDER);
    // initialising major and minor version numbers
    QuestionnaireVersion latestVersion = this.qnaireDefBo.getLatestVersion();
    this.latestMajorVersionNum = latestVersion.getMajorVersionNum();
    this.latestMinorVersionNum = latestVersion.getMinorVersionNum();
    // major version number is got from the latest version
    QuestionnaireVersionEditDialog.this.majorVersionNum =
        QuestionnaireVersionEditDialog.this.latestMajorVersionNum.intValue();
    // minor version number is got from the latest version +1 or if its null , then 0
    QuestionnaireVersionEditDialog.this.minorVersionNum =
        ((QuestionnaireVersionEditDialog.this.latestMinorVersionNum) == null ? 0
            : QuestionnaireVersionEditDialog.this.latestMinorVersionNum.intValue()) + 1;
    this.nameTxt.setText(VERSION_STR + this.latestMajorVersionNum + "." + this.minorVersionNum);

    GridData createGridData = new GridData();
    createGridData.heightHint = 20;
    createGridData.widthHint = 180;
    this.nameTxt.setLayoutData(createGridData);
    GridLayout layout = new GridLayout();
    this.nameTxt.setLayout(layout);
    this.nameTxt.setEnabled(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    // creating save and cancel buttons
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * enable /disable save button based on mandatory field
   */
  public void enableDiasableSaveBtn() {
    if (CommonUtils.isEmptyString(this.txtDescEng.getText().getText().trim())) {
      this.saveBtn.setEnabled(false);
      // show that the field is required
      this.decorators.showReqdDecoration(this.descEngDec, "");
    }
    else {
      this.saveBtn.setEnabled(true);
      // do not show the control decoration
      this.decorators.showErrDecoration(this.descEngDec, null, false);
    }
  }

  private boolean isChildQuestionValid(final List<Long> childQuestionList) {
    for (Long childQuesId : childQuestionList) {
      if (!this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(childQuesId).getDeletedFlag()) {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {


    Map<Long, List<Long>> childQuestionIdMap = this.qnaireDefBo.getQnaireDefModel().getChildQuestionIdMap();
    for (Question question : this.qnaireDefBo.getAllHeadingQuestions()) {

      if (!question.getDeletedFlag() && childQuestionIdMap.containsKey(question.getId()) &&
          childQuestionIdMap.get(question.getId()).isEmpty()) {
        MessageDialogUtils.getInfoMessageDialog("Information Message",
            "Its not possible to create a version when there are headings that have no attached questions.");
        super.okPressed();
        return;
      }
      if (!question.getDeletedFlag() && !isChildQuestionValid(childQuestionIdMap.get(question.getId()))) {
        MessageDialogUtils.getInfoMessageDialog("Information Message",
            "Its not possible to create a version when there are headings that doesn't have atleast one valid questions.");
        super.okPressed();
        return;
      }

    }
    // get the active version
    checkActiveVersionToInsertOrUpdate();
  }

  /**
   * 
   */
  private void checkActiveVersionToInsertOrUpdate() {
    QuestionnaireVersion activeVersion = this.qnaireDefBo.getQnaireDefModel().getQuestionnaireVersion(); // get the
                                                                                                         // active flag
    boolean activeFlag = this.activeBtn.getSelection();
    boolean confirm;
    if (activeFlag) {
      if ((activeVersion == null) || (this.isEdit && (activeVersion.getId().equals(this.selectedVersion.getId())))) {
        // if active version is null or if its the same version need not show the confirmation dialog
        confirm = true;
      }
      else {
        // when setting to active, show the confirm dialog that the active version's flag will be set to false
        confirm = MessageDialogUtils.getConfirmMessageDialog("Confirm changing active version",
            "The currently active version '" + this.qnaireDefBo.getActiveQnaireVersionName() +
                "' is set to not active because only one version can be active");
      }
    }
    else {
      if (this.isEdit && (activeVersion.getId().equals(this.selectedVersion.getId()))) {
        // when setting to non active, show the confirm dialog that reviews cannot use questionnaire for review
        confirm = MessageDialogUtils.getConfirmMessageDialog("Confirm changing active flag",
            "There's no other active version at the moment. Setting all versions to non active won't allow the user to select this questionnaire for their reviews.");
      }
      else {
        confirm = true;
      }
    }
    if (confirm) {
      if (this.isEdit) {
        updateVersion(activeFlag);
      }
      else {
        insertVersToCreate(activeFlag);
      }
      // add command in case of insert or when there is confirmation(in case of edit)
      super.okPressed();
      this.questionareVersionsPage.refreshPage();
    }
  }

  /**
   * @param activeFlag
   */
  private void updateVersion(boolean activeFlag) {
    // command for update
    QuestionnaireVersion verToUpdate = this.selectedVersion;
    verToUpdate.setQnaireId(this.qnaireDefBo.getQuestionnaire().getId());
    // set active flags and descriptions
    verToUpdate.setActiveFlag(activeFlag ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    setDescFields(verToUpdate);
    this.qnaireDefBo.updateQnaireVersion(verToUpdate);
  }

  /**
   * @param activeFlag
   */
  private void insertVersToCreate(boolean activeFlag) {
    // command for insert
    QuestionnaireVersion verToCreate = new QuestionnaireVersion();
    verToCreate.setQnaireId(this.qnaireDefBo.getQuestionnaire().getId());

    // set major and minor version numbers
    verToCreate.setMajorVersionNum(Long.valueOf(this.majorVersionNum));
    verToCreate.setMinorVersionNum(Long.valueOf(this.minorVersionNum));
    // set active flags and descriptions
    verToCreate.setActiveFlag(activeFlag ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    setDescFields(verToCreate);
    this.qnaireDefBo.createQnaireVersion(verToCreate);
  }

  private void setDescFields(final QuestionnaireVersion version) {
    version.setDescEng(this.txtDescEng.getText().getText());
    version.setDescGer(this.txtDescGer.getText().getText());
  }


  /**
   * ICDM-2027 set values to fields in case of edit
   */
  public void setValuesToFields() {
    if (this.isEdit) {
      // initialise values from selected version
      this.nameTxt.setText(this.qnaireDefBo.getVersionName(this.selectedVersion));
      if (CommonUtils.getBooleanType(this.selectedVersion.getActiveFlag())) {
        // if the version is an active version
        this.activeBtn.setSelection(true);
      }
      this.txtDescEng.getText().setText(this.selectedVersion.getDescEng());
      if (this.selectedVersion.getDescGer() != null) {
        this.txtDescGer.getText().setText(this.selectedVersion.getDescGer());
      }

    }
  }
}
