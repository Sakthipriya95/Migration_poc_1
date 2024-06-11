/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.editors.QnaireRespEditorInput;
import com.bosch.caltool.cdr.ui.editors.QnaireResponseEditor;
import com.bosch.caltool.cdr.ui.views.QnaireRespOutlinePageCreator;
import com.bosch.caltool.cdr.ui.views.providers.OutlineQnaireRespTreeViewContentProvider;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * ICDM-1983 This class is for showing the details of a question response
 *
 * @author mkl2cob
 */
public class QnaireRespDetailsPage extends AbstractFormPage implements ISelectionListener {

  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * Composite instance
   */
  private Composite composite;

  /**
   * defines the previous review questionnaire answer
   */
  // iCDM-1991
  private Button previousQuestionButton;

  /**
   * defines the next review questionnaire answer
   */
  // iCDM-1991
  private Button nextQuestionButton;

  /**
   * the tree view content provider
   */
  private final OutlineQnaireRespTreeViewContentProvider qRespCntntProvider;

  /**
   * the review questionnaire answer list
   */
  private ArrayList<RvwQnaireAnswer> rvwQnairAnswerList;
  private RvwQnaireAnswer rvwQnaireAns;
  /**
   * data handler for Questionaire Response
   */
  private final QnaireRespEditorDataHandler dataHandler;

  /**
   * the no of columns for the question section in the dialog
   */
  private static final int QUESTN_SECTION_COLUMNS_SIZE = 2;

  /**
   * Constructor
   *
   * @param editor FormEditor
   * @param formID String
   * @param title String
   */
  public QnaireRespDetailsPage(final FormEditor editor, final String formID, final String title) {
    super(editor, formID, title);
    this.qRespCntntProvider = new OutlineQnaireRespTreeViewContentProvider(
        ((QnaireRespEditorInput) editor.getEditorInput()).getQnaireRespEditorDataHandler(), false);
    this.dataHandler = getEditorInput().getQnaireRespEditorDataHandler();
  }

  /**
   * creates the initial part control
   */
  @Override
  public void createPartControl(final Composite parent) {
    this.formToolkit = getEditor().getToolkit();
    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = getEditor().getToolkit().createForm(parent);
    // ICDM-208
    // ICDM-1994, getting formatted name for the title of editor
    this.nonScrollableForm.setText(getEditorInput().getFormattedTitleName());
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }

  /**
   * create form content
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    // create composite
    createComposite();
    // iCDM-1991
    getSite().getPage().addSelectionListener(this);
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {

    GridData gridData = GridDataUtil.getInstance().getGridData();

    // initialising the composite
    this.composite = new Composite(this.nonScrollableForm.getBody(), SWT.NONE);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(gridData);

    // initialising the rvw questionnaire list without heading
    initializeRvwQNaireAnswerList();

    if (!this.rvwQnairAnswerList.isEmpty()) {
      this.rvwQnaireAns = this.rvwQnairAnswerList.get(0);
      createContents();

      // create a group for buttons
      createBtnGroup();

    }

  }

  /**
   * create section and sub section
   */
  private void createContents() {
    // identify parent questions
    Long parentQnId = this.dataHandler.getParentQnMap().get(this.rvwQnaireAns.getQuestionId());
    Deque<Long> parentStack = new ArrayDeque<>();
    while ((parentQnId != null) && !parentStack.contains(parentQnId)) {
      parentStack.push(parentQnId);
      parentQnId = this.dataHandler.getParentQnMap().get(parentQnId);
    }

    // create sections for parent questions
    while (parentStack.peek() != null) {
      parentQnId = parentStack.pop();
      GridData gridData = GridDataUtil.getInstance().getGridData();
      gridData.grabExcessVerticalSpace = false;
      String secDesc = this.dataHandler.getQuestionNumberWithNameByLanguage(parentQnId);
      Section section = SectionUtil.getInstance().createSection(this.composite, this.formToolkit, secDesc);
      section.setLayoutData(gridData);
      increaseFontSize(section, 12);

      // initialising the form
      String formDesc = this.dataHandler.getQuestionDescriptionByLanguage(parentQnId);
      Form form = this.formToolkit.createForm(section);
      form.getBody().setLayout(new GridLayout());
      section.setDescription(formDesc);
      section.setClient(form);
    }

    QnaireResponseEditor responseEditor = (QnaireResponseEditor) getEditor();
    // Create question response section
    new QnaireAnswerEditComposite(this.composite, this.dataHandler, this.rvwQnaireAns, this.formToolkit, false, null,
        responseEditor);

    this.composite.layout();
  }

  /**
   * @param section
   */
  private void increaseFontSize(final Section section, final int fontSize) {
    FontData[] font = section.getFont().getFontData();
    font[0].setHeight(fontSize);
    section.setFont(new Font(section.getDisplay(), font[0]));
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
   * @return QuestionaireResponseEditorInput instance
   */
  @Override
  public QnaireRespEditorInput getEditorInput() {
    return (QnaireRespEditorInput) getEditor().getEditorInput();
  }

  /**
   * create group for buttons
   */
  private void createBtnGroup() {
    Group btnGroup = new Group(this.nonScrollableForm.getBody(), SWT.NONE);
    GridLayout layout = new GridLayout();

    // setting the initial size of the gridlayout for the question section
    layout.numColumns = QnaireRespDetailsPage.QUESTN_SECTION_COLUMNS_SIZE;
    btnGroup.setLayout(layout);
    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    btnGroup.setLayoutData(gridData);

    // creates previous question button
    createPreviousQuestionButton(btnGroup);

    // creates next question button
    createNextQuestionButton(btnGroup);
  }

  /**
   * This method is used to create the Previous button
   *
   * @param btnGroup
   */
  private void createPreviousQuestionButton(final Group btnGroup) {
    // initialising the button
    this.previousQuestionButton = new Button(btnGroup, SWT.PUSH);
    this.previousQuestionButton.setText("< Previous Question");

    // adding the selection listener for the button
    this.previousQuestionButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        // triggers if the user clicks the previous button
        onPreviousPressed();
      }


      /**
       */
      private void onPreviousPressed() {
        int previousIndex =
            QnaireRespDetailsPage.this.rvwQnairAnswerList.indexOf(QnaireRespDetailsPage.this.rvwQnaireAns);
        previousIndex--;
        QnaireRespDetailsPage.this.rvwQnaireAns = QnaireRespDetailsPage.this.rvwQnairAnswerList.get(previousIndex);

        while (!QnaireRespDetailsPage.this.dataHandler.isQuestionVisible(QnaireRespDetailsPage.this.rvwQnaireAns)) {

          previousIndex--;
          QnaireRespDetailsPage.this.rvwQnaireAns = QnaireRespDetailsPage.this.rvwQnairAnswerList.get(previousIndex);
        }


        refreshByDisposeAndCreate();

        // to enable or disable the previous/Next buttons
        enableDisableNavigationButtons();

        // synchronize between Questionnaire Response Details page and the Outline/Filter View
        syncWithOutlinePageTreeViewer(false);
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        /**
         * Not applicable
         */
      }
    });
    this.previousQuestionButton.setEnabled(false);
  }

  /**
   * This method is used to create the Next button
   *
   * @param btnGroup the given button
   */
  private void createNextQuestionButton(final Group btnGroup) {
    // initialising the button
    this.nextQuestionButton = new Button(btnGroup, SWT.PUSH);
    this.nextQuestionButton.setText(" Next Question > ");

    // adding the selection listener for the button
    this.nextQuestionButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        onNextPressed();
      }

      private void onNextPressed() {
        int nextIndex = QnaireRespDetailsPage.this.rvwQnairAnswerList.indexOf(QnaireRespDetailsPage.this.rvwQnaireAns);
        nextIndex++;
        if ((QnaireRespDetailsPage.this.rvwQnairAnswerList.size() > nextIndex) &&
            (null != QnaireRespDetailsPage.this.rvwQnairAnswerList.get(nextIndex))) {
          QnaireRespDetailsPage.this.rvwQnaireAns = QnaireRespDetailsPage.this.rvwQnairAnswerList.get(nextIndex);
          while (!QnaireRespDetailsPage.this.dataHandler.isQuestionVisible(QnaireRespDetailsPage.this.rvwQnaireAns)) {
            nextIndex++;
            QnaireRespDetailsPage.this.rvwQnaireAns = QnaireRespDetailsPage.this.rvwQnairAnswerList.get(nextIndex);
            if (nextIndex == (QnaireRespDetailsPage.this.rvwQnairAnswerList.size() - 1)) {
              QnaireRespDetailsPage.this.nextQuestionButton.setEnabled(false);
              break;
            }
          }
          refreshByDisposeAndCreate();
          // to enable or disable the previous/Next buttons
          enableDisableNavigationButtons();
          // synchronize between Questionnaire Response Details page and the Outline/Filter View
          syncWithOutlinePageTreeViewer(false);
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent exp) {
        /**
         * Not applicable
         */
      }
    });
  }

  /**
   * This method is used for synchronisation between Questionnaire Response Details page and the Outline/Filter View
   * part questions. If the user clicks Next or Previous Button, the corresponding question will be selected in the
   * Outline/Filter View part.
   */
  private void syncWithOutlinePageTreeViewer(final boolean refreshTree) {
    if (null != PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView(ApicUiConstants.OUTLINE_TREE_VIEW)) {
      QnaireResponseEditor editor = (QnaireResponseEditor) getEditor();
      QnaireRespOutlinePageCreator qRespOutlnePge = editor.getQuestionnaireResOutlinePageCreator();
      // refresh the tree viewer only when needed
      if (refreshTree) {
        qRespOutlnePge.getQuestResponseOutlinePage().getViewer().refresh();
      }
      qRespOutlnePge.getQuestResponseOutlinePage().getViewer()
          .setSelection(new StructuredSelection(QnaireRespDetailsPage.this.rvwQnaireAns), true);
    }
  }

  /**
   * This method is used to enable or disable the Next or Previous Buttons
   */
  private void enableDisableNavigationButtons() {
    // this variable is to handle the Next button
    int nextIndex;

    // this variable is to handle the Previous button
    int previousIndex;

    // the current index value
    int currentIndex = QnaireRespDetailsPage.this.rvwQnairAnswerList.indexOf(QnaireRespDetailsPage.this.rvwQnaireAns);
    try {
      previousIndex = currentIndex;
      previousIndex--;

      QnaireRespDetailsPage.this.rvwQnairAnswerList.get(previousIndex);
      this.previousQuestionButton.setEnabled(true);
    }
    catch (IndexOutOfBoundsException exp) {
      // disables the previous button
      this.previousQuestionButton.setEnabled(false);
      CDMLogger.getInstance()
          .info("The Review Questionnaire Answer " + QnaireRespDetailsPage.this.getEditorInput()
              .getQnaireRespEditorDataHandler().getName(this.rvwQnaireAns.getQuestionId()) + " has no previous node.",
              exp);
    }

    try {
      nextIndex = currentIndex;
      nextIndex++;
      while ((nextIndex < (QnaireRespDetailsPage.this.rvwQnairAnswerList.size() - 1)) &&
          !QnaireRespDetailsPage.this.dataHandler
              .isQuestionVisible(QnaireRespDetailsPage.this.rvwQnairAnswerList.get(nextIndex))) {
        nextIndex++;
      }
      this.nextQuestionButton.setEnabled(!(!QnaireRespDetailsPage.this.dataHandler
          .isQuestionVisible(QnaireRespDetailsPage.this.rvwQnairAnswerList.get(nextIndex)) &&
          (nextIndex == (QnaireRespDetailsPage.this.rvwQnairAnswerList.size() - 1))));

    }
    catch (IndexOutOfBoundsException exp) {
      // disables the next button
      this.nextQuestionButton.setEnabled(false);
      CDMLogger.getInstance()
          .info("The Review Questionnaire Answer " + QnaireRespDetailsPage.this.getEditorInput()
              .getQnaireRespEditorDataHandler().getQuestion(this.rvwQnaireAns.getQuestionId()).getName() +
              " has no next node.", exp);
    }
  }

  /**
   * dispose the existing composite and create a new one
   */
  public void refreshByDisposeAndCreate() {
    if (null != QnaireRespDetailsPage.this.composite) {
      Control[] children = QnaireRespDetailsPage.this.composite.getChildren();
      // dispose all children of composite
      for (Control childOfComposite : children) {
        childOfComposite.dispose();
      }
      createContents();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    if ((null != this.rvwQnaireAns) && !this.composite.isDisposed()) {
      initializeRvwQNaireAnswerList();

      // To set the first response of the active questionnaire in Details view
      if (!this.rvwQnairAnswerList.isEmpty()) {
        this.rvwQnaireAns = this.rvwQnairAnswerList.get(0);
      }

      Long qRespId = this.rvwQnaireAns.getId();
      Optional<RvwQnaireAnswer> rvwQAns =
          this.rvwQnairAnswerList.stream().filter(qAns -> qAns.getId().equals(qRespId)).findFirst();
      if (rvwQAns.isPresent()) {
        this.rvwQnaireAns = rvwQAns.get();
        enableDisableNavigationButtons();
        syncWithOutlinePageTreeViewer(true);
      }
    }
  }


  /**
   * get Tool BarManager
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return null;
  }

  /**
   * get Part Control
   */
  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   * This method triggers when user selects a Questionnaire Response Node in the Outline view part
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    IEditorPart activeEditor = getSite().getPage().getActiveEditor();
    if (activeEditor instanceof QnaireResponseEditor) {
      QnaireResponseEditor qrEditor = (QnaireResponseEditor) activeEditor;
      if ((activeEditor == getEditor()) && (part instanceof OutlineViewPart) &&
          (qrEditor.getActivePageInstance().equals(this))) {
        selectionListenerFromOutlineView(selection);
      }
    }
  }

  /**
   * Selection listener implementation for the selection of the questions on outlineFilter
   *
   * @param selection
   */
  // iCDM-1991
  private void selectionListenerFromOutlineView(final ISelection selection) {
    if ((selection != null) && !selection.isEmpty() && (selection instanceof IStructuredSelection)) {
      final Object selctedItem = ((IStructuredSelection) selection).getFirstElement();
      // when user clicks at the Questionnaire Response Node in the Outline View part
      if (selctedItem instanceof RvwQnaireResponse) {
        this.rvwQnaireAns = this.dataHandler.getFirstQuestionResponse();
      }
      else if (selctedItem instanceof RvwQnaireAnswer) {
        this.rvwQnaireAns = this.dataHandler.getFirstQuestionResponse((RvwQnaireAnswer) selctedItem);
      }
      if (null != this.rvwQnaireAns) {
        refreshByDisposeAndCreate();
        enableDisableNavigationButtons();
      }
    }
  }


  /**
   * This method activated if the user selects the Details View and active flag will be true If the user selects the
   * List View, then the active flag will be false
   *
   * @param active flag
   */
  // iCDM-1991
  @Override
  public void setActive(final boolean active) {
    if (active) {
      QnaireResponseEditor editor = (QnaireResponseEditor) getEditor();
      QnaireRespOutlinePageCreator questResponseOutlinePageCreator = editor.getQuestionnaireResOutlinePageCreator();
      if ((questResponseOutlinePageCreator != null) &&
          (questResponseOutlinePageCreator.getQuestResponseOutlinePage() != null) && (null != PlatformUI.getWorkbench()
              .getActiveWorkbenchWindow().getActivePage().findView(ApicUiConstants.OUTLINE_TREE_VIEW))) {
        TreeViewer viewer = questResponseOutlinePageCreator.getQuestResponseOutlinePage().getViewer();
        viewer.setContentProvider(this.qRespCntntProvider);
        viewer.refresh();
        viewer.expandAll();
        // ICDM-2096
        QnaireResponseEditor responseEditor = (QnaireResponseEditor) getEditor();
        QnaireRespSummaryPage quesSummaryPge = responseEditor.getQuesSummaryPge();
        RvwQnaireAnswer summaryPageSelection = quesSummaryPge.getCurrentSelection();
        // To fetch the response of the selected cell from summary page
        if (summaryPageSelection != null/* && !summaryPageSelection.equals(this.rvwQnaireAns) */) {
          this.rvwQnaireAns = this.dataHandler.getFirstQuestionResponse(summaryPageSelection);
        }
        else {
          // To initilaize and set the value of active questionnaire from summary view in Details page
          initializeRvwQNaireAnswerList();
          if (!this.rvwQnairAnswerList.isEmpty()) {
            this.rvwQnaireAns = this.rvwQnairAnswerList.get(0);
          }
        }
        refreshByDisposeAndCreate();
        enableDisableNavigationButtons();
        syncWithOutlinePageTreeViewer(false);
      }
    }
    super.setActive(active);
  }

  /**
   * @return the rvwQnaireAnswer
   */
  public RvwQnaireAnswer getRvwQnaireAnswer() {
    return this.rvwQnaireAns;
  }
}
