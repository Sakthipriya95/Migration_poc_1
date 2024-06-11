/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.OpenPointsData;
import com.bosch.caltool.cdr.ui.dialogs.OpenPointsEditDialog;
import com.bosch.caltool.cdr.ui.dialogs.QnaireAnswerEditDialog;
import com.bosch.caltool.cdr.ui.editors.QnaireResponseEditor;
import com.bosch.caltool.cdr.ui.sorters.OpenPointsTableViewerSorter;
import com.bosch.caltool.cdr.ui.views.providers.OpenPointsTableLabelProvider;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.AddLinkDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.EditLinkDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.caltool.icdm.common.ui.views.providers.LinkTableLabelProvider;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOption;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_RESP_SERIES_MEASURE;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextBoxContentDisplay;


/**
 * ICDM-1984 this is a common compoiste for question response editing
 *
 * @author mkl2cob
 */
public class QnaireAnswerEditComposite extends ScrolledComposite {

  /**
   *
   */
  private static final String EMPTY_STRING = "        ";
  /**
   * QuestionResponse instance
   */
  private final QnaireRespEditorDataHandler quesRespHandler;
  /**
   * FormToolkit
   */
  private final FormToolkit formToolkit;
  /**
   * is the Questionaire response edit dialog opened
   */
  private final boolean isEditDialog;

  /**
   * series maturity
   */
  private String seriesString;
  /**
   * Measurement
   */
  private String measuremntString;
  /**
   * open points
   */
  private String openPointsStr;
  /**
   * remarks
   */
  private String remarksStr;
  /**
   * result
   */
  private String answerStr;

  /**
   * result type
   */
  private String answerStrType;

  /**
   * result Option Ids
   */
  private Long answerOptId;
  /**
   * SortedSet<LinkData>
   */
  private SortedSet<LinkData> linksList = new TreeSet<>();
  /**
   * SortedSet<LinkData>
   */
  private List<OpenPointsData> openPointList;


  /**
   * @return the openPointList
   */
  public List<OpenPointsData> getOpenPointList() {
    return this.openPointList;
  }

  /**
   * QuestionResponseEditDialog
   */
  private final QnaireAnswerEditDialog dialog;
  /**
   * GridTableViewer instance for open points
   */
  private GridTableViewer openPointsTabViewer;

  /**
   * @return the openPointsTabViewer
   */
  public GridTableViewer getOpenPointsTabViewer() {
    return this.openPointsTabViewer;
  }

  private AbstractViewerSorter openPointsTabSorter;
  private OpenPointsEditDialog openPointsDialog;
  private Button editBtn;
  private Button btnDelete;

  /**
   * edit link button
   */
  private Button btnEditLink;
  /**
   * delete link button
   */
  private Button btnDeleteLink;
  private Button btnAddLink;
  private Combo answerCombo;
  private final RvwQnaireAnswer rvwAnsObj;
  private GridTableViewer linkTable;
  private Text calculatedResultTxt;

  /**
   * Constructor
   *
   * @param parent Composite
   * @param quesRespHandler QnaireRespEditorDataHandler
   * @param ansObj RvwQnaireAnswer
   * @param formToolkit FormToolkit
   * @param isEdit if the dialog is editable
   * @param dialog QuestionResponseEditDialog
   * @param responseEditor as input
   */
  public QnaireAnswerEditComposite(final Composite parent, final QnaireRespEditorDataHandler quesRespHandler,
      final RvwQnaireAnswer ansObj, final FormToolkit formToolkit, final boolean isEdit,
      final QnaireAnswerEditDialog dialog, final QnaireResponseEditor responseEditor) {
    super(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
    this.quesRespHandler = quesRespHandler;
    this.rvwAnsObj = ansObj;
    this.formToolkit = formToolkit;
    this.isEditDialog = isEdit;
    this.dialog = dialog;

    createComposite();
  }

  /**
   * create contents of the composite
   */
  private void createComposite() {
    // set layout for composite
    setExpandHorizontal(true);
    setExpandVertical(true);
    setLayout(new GridLayout());
    setLayoutData(GridDataUtil.getInstance().getGridData());

    // create section
    createSection();

  }


  /**
   * create section under composite
   */
  private void createSection() {
    Section section = this.formToolkit.createSection(this, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    section.setText(this.quesRespHandler.getQuestionNumberWithNameByLanguage(this.rvwAnsObj.getQuestionId()));

    section.setLayout(new GridLayout());
    section.setLayoutData(GridDataUtil.getInstance().getGridData());

    section.setDescription(this.quesRespHandler.getDescriptionByLanguage(this.rvwAnsObj));

    // Disable the description control, if the component is editable. In read only mode, allow text selection
    section.getDescriptionControl().setEnabled(!this.isEditDialog);

    increaseFontSize(section, 12);
    createForm(section);


    setContent(section);
    setMinSize(section.computeSize(SWT.DEFAULT, SWT.DEFAULT));
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
   * create toolbar action for editing the question response
   *
   * @param section Section
   */

  /**
   * create Form under Section
   *
   * @param section
   */
  private void createForm(final Section section) {
    Form form = this.formToolkit.createForm(section);
    // setting layout for form
    GridData gridData = GridDataUtil.getInstance().getGridData();
    form.getBody().setLayoutData(gridData);
    form.getBody().setLayout(new GridLayout());

    // Create Result section if it is relevant for questionnaire
    if (this.quesRespHandler.showQnaireVersResult()) {
      createAnswerSection(form);
    }

    // Create series maturity field if it is relevant for questionnaire
    if (this.quesRespHandler.showQnaireVersSeriesMaturity()) {
      createSeriesMaturityField(form);
    }

    // Create measurement field if it is relevant for questionnaire
    if (this.quesRespHandler.showQnaireVersMeasurement()) {
      createMeasurementField(form);
    }

    // Create Links section if it is relevant for questionnaire
    if (this.quesRespHandler.showQnaireVersLinks()) {
      createLinksSection(form);
    }

    // Create Open Issues section if it is relevant for questionnaire
    if (this.quesRespHandler.showQnaireVersOpenPoints()) {
      createOpenIssuesSection(form);
    }

    // Create Comments section if it is relevant for questionnaire
    if (this.quesRespHandler.showQnaireVersRemarks()) {
      createCommentsSection(form);
    }

    // show calculated result based on values filled in mandatory field
    createCalculatedResult(form.getBody());

    section.setClient(form);
  }


  /**
   * @param form
   */
  private void createLineSeperator(final Form form) {
    Label seperator = new Label(form.getBody(), SWT.SEPARATOR | SWT.HORIZONTAL);
    seperator.setLayoutData(GridDataUtil.getInstance().getGridData());
  }


  /**
   * @param form
   */
  private void createAnswerSection(final Form form) {
    createAnswer(form.getBody());
    createLineSeperator(form);
  }

  /**
   * @param suffixLabel
   */
  private void addNotRelevantSuffix(final Label suffixLabel) {
    suffixLabel.setText("(not relevant for this question)");
    suffixLabel.setEnabled(false);
  }

  /**
   * @param form
   */
  private void createCommentsSection(final Form form) {
    Composite commentComp = this.formToolkit.createComposite(form.getBody());
    GridLayout commentLayout = new GridLayout();
    commentLayout.numColumns = 2;
    commentComp.setLayout(commentLayout);
    commentComp.setLayoutData(new GridData());
    createCommentsComposite(commentComp);
    createLineSeperator(form);
  }

  /**
   * @param form
   */
  private void createOpenIssuesSection(final Form form) {
    Composite bottomComp = this.formToolkit.createComposite(form.getBody());
    GridLayout layoutForBtmComp = new GridLayout();
    layoutForBtmComp.numColumns = 2;
    bottomComp.setLayout(layoutForBtmComp);
    bottomComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    createOpenPointsComposite(bottomComp);
    createLineSeperator(form);
  }

  /**
   * @param form
   */
  private void createLinksSection(final Form form) {
    createLinks(form);
    createLineSeperator(form);
  }

  /**
   * @param form
   */
  private void createMeasurementField(final Form form) {
    createMeasurementAvailable(form);
    createLineSeperator(form);
  }

  /**
   * @param form
   */
  private void createSeriesMaturityField(final Form form) {
    createSeriesMaturity(form);
  }

  /**
   * @param body
   */
  private void createCommentsComposite(final Composite body) {

    GridData gridData = new GridData();
    gridData.heightHint = 100;
    gridData.widthHint = 400;
    gridData.verticalSpan = 3;
    Composite labelComp = new Composite(body, SWT.NONE);
    GridLayout labelLayout = new GridLayout();
    labelLayout.numColumns = 2;
    labelComp.setLayout(labelLayout);
    Label commentsLabel = LabelUtil.getInstance().createLabel(labelComp, "Comment");
    Label suffixLabel = addSuffix(labelComp, isRemarksMandatory());


    LabelUtil.getInstance().createEmptyLabel(body);
    TextBoxContentDisplay textBoxContentDisplay =
        new TextBoxContentDisplay(body, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, 4000, gridData);
    textBoxContentDisplay.setEnabled(true);
    textBoxContentDisplay.getText().addModifyListener(event -> {
      QnaireAnswerEditComposite.this.remarksStr = textBoxContentDisplay.getText().getText();
      if (QnaireAnswerEditComposite.this.isEditDialog) {
        enableDisableSaveBtn();
      }
      setCalculatedResultTxtData();
    });

    textBoxContentDisplay.getText().addFocusListener(new FocusListener() {


      @Override
      public void focusLost(final FocusEvent focusevent) {

        // Story 226388
        if (!QnaireAnswerEditComposite.this.isEditDialog) {
          saveAnswerDetails();
        }
      }

      @Override
      public void focusGained(final FocusEvent focusevent) {
        // NA
      }
    });

    // popualte value from db
    this.remarksStr = this.quesRespHandler.getRemark(this.rvwAnsObj);
    textBoxContentDisplay.getText().setText(CommonUtils.checkNull(this.remarksStr));
    textBoxContentDisplay.setEditable(this.quesRespHandler.isModifiable());

    // Disable Remarks section if it is a heading or if it is not relevant for question
    if (!this.quesRespHandler.showRemarks(this.rvwAnsObj.getQuestionId())) {
      addNotRelevantSuffix(suffixLabel);
      textBoxContentDisplay.setEditable(false);
      textBoxContentDisplay.setEnabled(false);
      commentsLabel.setEnabled(false);
    }
  }

  /**
   * @param body
   * @param isMandatory
   * @return
   */
  private Label addSuffix(final Composite body, final boolean isMandatory) {
    Label suffixLabel;
    if (isMandatory) {
      suffixLabel = LabelUtil.getInstance().createLabel(body, "(mandatory)");
      suffixLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
    }
    else {
      suffixLabel = LabelUtil.getInstance().createLabel(body, "(optional)");
      suffixLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
    }
    return suffixLabel;
  }

  /**
   * @param bottomComp
   */
  private void createOpenPointsComposite(final Composite bottomComp) {
    // ICDM-2188
    Composite labelComp = new Composite(bottomComp, SWT.NONE);
    GridLayout labelLayout = new GridLayout();
    labelLayout.numColumns = 2;
    labelComp.setLayout(labelLayout);
    Label openPointsLabel = LabelUtil.getInstance().createLabel(labelComp,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_OPEN_POINTS));
    // check for optional marker
    Label suffixLabel = addSuffix(labelComp, false);
    LabelUtil.getInstance().createLabel(bottomComp, EMPTY_STRING);
    createOPTable(bottomComp);


    // add op button
    Button btnAdd = new Button(bottomComp, SWT.PUSH);
    btnAdd.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ADD_16X16));
    btnAdd.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        QnaireAnswerEditComposite.this.openPointsDialog =
            new OpenPointsEditDialog(Display.getDefault().getActiveShell(),
                QnaireAnswerEditComposite.this.quesRespHandler, QnaireAnswerEditComposite.this, null);
        QnaireAnswerEditComposite.this.openPointsDialog.open();

        if (QnaireAnswerEditComposite.this.isEditDialog) {
          enableDisableSaveBtn();
        }
        else {
          saveAnswerDetails();
        }
      }
    });
    btnAdd.setEnabled(this.quesRespHandler.isModifiable());
    this.editBtn = new Button(bottomComp, SWT.PUSH);
    this.editBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_16X16));
    this.editBtn.setEnabled(false);
    this.editBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        openOPEditDialog();
        if (!QnaireAnswerEditComposite.this.isEditDialog) {
          saveAnswerDetails();
        }
      }
    });

    this.btnDelete = new Button(bottomComp, SWT.PUSH);
    this.btnDelete.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    this.btnDelete.setEnabled(false);
    this.btnDelete.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        IStructuredSelection selection =
            (IStructuredSelection) QnaireAnswerEditComposite.this.openPointsTabViewer.getSelection();

        OpenPointsData opData = (OpenPointsData) selection.getFirstElement();

        QnaireAnswerEditComposite.this.openPointList =
            (List<OpenPointsData>) QnaireAnswerEditComposite.this.openPointsTabViewer.getInput();

        opData.setOprType(CommonUIConstants.CHAR_CONSTANT_FOR_DELETE);

        QnaireAnswerEditComposite.this.openPointsTabViewer.setInput(QnaireAnswerEditComposite.this.openPointList);
        QnaireAnswerEditComposite.this.openPointsTabViewer.refresh();

        QnaireAnswerEditComposite.this.openPointsTabViewer.setSelection(null);
        QnaireAnswerEditComposite.this.editBtn.setEnabled(false);
        QnaireAnswerEditComposite.this.btnDelete.setEnabled(false);

        if (QnaireAnswerEditComposite.this.isEditDialog) {
          enableDisableSaveBtn();
        }
        else {
          saveAnswerDetails();
        }
      }
    });
    // Disable Open Issues section if it is a heading or if it is not relevant for question
    if (!this.quesRespHandler.showOpenPoints(this.rvwAnsObj.getQuestionId())) {
      addNotRelevantSuffix(suffixLabel);
      btnAdd.setEnabled(false);
      openPointsLabel.setEnabled(false);
      this.openPointsTabViewer.getGrid().setEnabled(false);
      this.openPointsTabViewer.getGrid().setHeaderVisible(false);
    }
  }

  /**
   *
   */
  private void createMultipleOpenPointService(final RvwQnaireAnswer updatedAns) {
    List<RvwQnaireAnswerOpl> addList = new ArrayList<>();
    List<RvwQnaireAnswerOpl> editList = new ArrayList<>();
    List<Long> delList = new ArrayList<>();
    java.text.DateFormat df15 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_15);
    java.text.DateFormat df09 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_09);
    for (OpenPointsData selOpenPoint : getOpenPointList()) {
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

    this.quesRespHandler.updateRvwQnaireAnsOpl(addList, editList, delList, updatedAns);
    this.openPointList.clear();
    refreshOplTabViewer();
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
   *
   */
  public void refreshOplTabViewer() {
    setInput();
    this.openPointsTabViewer.refresh();
  }


  /**
   * @param bottomComp
   */
  private void createOPTable(final Composite bottomComp) {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.heightHint = 100;
    gridData.verticalSpan = 3;
    this.openPointsTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(bottomComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, gridData);

    // Invokde GridColumnViewer sorter
    this.openPointsTabSorter = new OpenPointsTableViewerSorter();
    this.openPointsTabViewer.setComparator(this.openPointsTabSorter);
    // override the existing default tooltip
    GridItem[] items = this.openPointsTabViewer.getGrid().getItems();
    for (GridItem gridItem : items) {
      gridItem.setToolTipText(ApicUiConstants.COLUMN_INDEX_0, CommonUIConstants.EMPTY_STRING);
      gridItem.setToolTipText(ApicUiConstants.COLUMN_INDEX_1, CommonUIConstants.EMPTY_STRING);
      gridItem.setToolTipText(ApicUiConstants.COLUMN_INDEX_2, CommonUIConstants.EMPTY_STRING);
      gridItem.setToolTipText(ApicUiConstants.COLUMN_INDEX_3, CommonUIConstants.EMPTY_STRING);
      gridItem.setToolTipText(ApicUiConstants.COLUMN_INDEX_4, CommonUIConstants.EMPTY_STRING);
    }
    // Create GridViewerColumns
    createOpenPointTableCols();
    // Set ContentProvider and LabelProvider to addNewUserTableViewer

    setContentProvider();

    setInput();


    // Add selection listener to the addNewUserTableViewer
    addSelectionListener();

    // add double click listener
    this.openPointsTabViewer.addDoubleClickListener(e -> Display.getDefault().asyncExec(this::openOPEditDialog));
  }

  /**
   *
   */
  private void addSelectionListener() {
    this.openPointsTabViewer.addSelectionChangedListener(event -> {
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      OpenPointsData selected = (OpenPointsData) selection.getFirstElement();
      if (CommonUtils.isNotNull(selected) && (selected.getOprType() != CommonUIConstants.CHAR_CONSTANT_FOR_DELETE) &&
          QnaireAnswerEditComposite.this.quesRespHandler.isModifiable()) {
        QnaireAnswerEditComposite.this.editBtn.setEnabled(true);
        QnaireAnswerEditComposite.this.btnDelete.setEnabled(true);
      }
      else {
        QnaireAnswerEditComposite.this.editBtn.setEnabled(false);
        QnaireAnswerEditComposite.this.btnDelete.setEnabled(false);
      }
      setCalculatedResultTxtData();
    });
  }

  /**
   *
   */
  private void setInput() {
    if ((null != this.quesRespHandler.getOpenPointsList(this.rvwAnsObj)) &&
        !this.quesRespHandler.getOpenPointsList(this.rvwAnsObj).isEmpty()) {
      this.openPointList = new ArrayList<>();
      for (RvwQnaireAnswerOpl openPoint : this.quesRespHandler.getOpenPointsList(this.rvwAnsObj).values()) {

        OpenPointsData openPt = new OpenPointsData(openPoint);
        this.openPointList.add(openPt);
      }
    }
    this.openPointsTabViewer.setInput(this.openPointList);
  }

  /**
   *
   */
  private void setContentProvider() {
    this.openPointsTabViewer.setContentProvider(new IStructuredContentProvider() {

      @SuppressWarnings("unchecked")
      @Override
      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        QnaireAnswerEditComposite.this.openPointList = (List<OpenPointsData>) newInput;
      }

      @Override
      public void dispose() {
        // No Implementation Required
      }

      @SuppressWarnings("rawtypes")
      @Override
      public Object[] getElements(final Object inputElement) {
        if (inputElement instanceof List) {
          return ((List) inputElement).toArray();
        }
        return new Object[0];
      }

    });
  }

  /**
   *
   */
  private void createOpenPointTableCols() {
    ColumnViewerToolTipSupport.enableFor(this.openPointsTabViewer, ToolTip.NO_RECREATE);
    // ICDM-2188
    final GridViewerColumn openPointColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.openPointsTabViewer,
            CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_OPEN_POINTS), 300);
    // Add column selection listener
    openPointColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(openPointColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_0, this.openPointsTabSorter, this.openPointsTabViewer));
    openPointColumn.setLabelProvider(new OpenPointsTableLabelProvider(ApicUiConstants.COLUMN_INDEX_0));
    if (this.quesRespHandler.showMeasures(this.rvwAnsObj.getQuestionId())) {
      final GridViewerColumn measuresColumn =
          GridViewerColumnUtil.getInstance().createGridViewerColumn(this.openPointsTabViewer,
              CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_MEASURE), 250);
      // Add column selection listener
      measuresColumn.getColumn()
          .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(measuresColumn.getColumn(),
              ApicUiConstants.COLUMN_INDEX_1, this.openPointsTabSorter, this.openPointsTabViewer));
      measuresColumn.setLabelProvider(new OpenPointsTableLabelProvider(ApicUiConstants.COLUMN_INDEX_1));
    }
    if (this.quesRespHandler.showResponsible(this.rvwAnsObj.getQuestionId())) {
      final GridViewerColumn responsibleColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(
          this.openPointsTabViewer,
          CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_RESPONSIBLE), 130);
      // Add column selection listener
      responsibleColumn.getColumn()
          .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(responsibleColumn.getColumn(),
              ApicUiConstants.COLUMN_INDEX_2, this.openPointsTabSorter, this.openPointsTabViewer));
      responsibleColumn.setLabelProvider(new OpenPointsTableLabelProvider(ApicUiConstants.COLUMN_INDEX_2));
    }
    if (this.quesRespHandler.showCompletionDate(this.rvwAnsObj.getQuestionId())) {
      final GridViewerColumn dateColumn =
          GridViewerColumnUtil.getInstance().createGridViewerColumn(this.openPointsTabViewer,
              CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_DATE), 150);
      // Add column selection listener
      dateColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
          dateColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_3, this.openPointsTabSorter, this.openPointsTabViewer));
      dateColumn.setLabelProvider(new OpenPointsTableLabelProvider(ApicUiConstants.COLUMN_INDEX_3));
    }
    // OPL Status column(completed)
    GridViewerColumn oplStatusColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.openPointsTabViewer,
            CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_STATUS), 100);
    oplStatusColumn.setLabelProvider(new OpenPointsTableLabelProvider(ApicUiConstants.COLUMN_INDEX_4));

    // Add column selection listener
    oplStatusColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(oplStatusColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_4, this.openPointsTabSorter, this.openPointsTabViewer));

  }

  private void createCalculatedResult(final Composite bottomComp) {
    Composite resultComposite = this.formToolkit.createComposite(bottomComp);
    resultComposite.setLayout(new GridLayout());
    resultComposite.setLayoutData(new GridData());

    LabelUtil.getInstance().createLabel(resultComposite, "Result");

    this.calculatedResultTxt = new Text(resultComposite, SWT.BORDER | SWT.READ_ONLY);
    setCalculatedResultTxtData();
  }


  /**
   * create result field
   *
   * @param bottomComp Composite
   */
  private void createAnswer(final Composite bottomComp) {
    Composite resultComposite = this.formToolkit.createComposite(bottomComp);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    resultComposite.setLayout(gridLayout);
    resultComposite.setLayoutData(new GridData());

    Composite labelComp = new Composite(resultComposite, SWT.NONE);
    GridLayout labelLayout = new GridLayout();
    labelLayout.numColumns = 2;
    labelComp.setLayout(labelLayout);
    Label answerLabel = LabelUtil.getInstance().createLabel(labelComp, "Answer");
    Label suffixLabel = addSuffix(labelComp, isResultMandatory());

    LabelUtil.getInstance().createEmptyLabel(resultComposite);

    this.answerCombo = new Combo(resultComposite, SWT.BORDER | SWT.READ_ONLY);

    this.quesRespHandler.getQuestionResultOptionsMap(this.rvwAnsObj.getQuestionId()).values()
        .forEach(questionResultOption -> this.answerCombo.add(questionResultOption.getQResultName()));

    this.answerCombo.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        String resultOptName = QnaireAnswerEditComposite.this.answerCombo
            .getItem(QnaireAnswerEditComposite.this.answerCombo.getSelectionIndex());

        QuestionResultOption questionResultOptionType =
            QnaireAnswerEditComposite.this.quesRespHandler.getQuestionResultOptionByResultName(resultOptName,
                QnaireAnswerEditComposite.this.rvwAnsObj.getQuestionId());
        QnaireAnswerEditComposite.this.answerStrType = questionResultOptionType.getQResultType();
        QnaireAnswerEditComposite.this.answerStr = questionResultOptionType.getQResultName();
        QnaireAnswerEditComposite.this.answerOptId = questionResultOptionType.getId();

        if (QnaireAnswerEditComposite.this.isEditDialog) {
          enableDisableSaveBtn();
        }
        else {
          saveAnswerDetails();
        }
        setCalculatedResultTxtData();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // No Implementation Required
      }
    });

    this.answerStr = this.quesRespHandler.getQuestionResultOptionUIString(this.rvwAnsObj.getQuestionId(),
        this.rvwAnsObj.getSelQnaireResultOptID());
    this.answerOptId = this.rvwAnsObj.getSelQnaireResultOptID();
    this.answerCombo.setText(this.answerStr == null ? ApicConstants.EMPTY_STRING : this.answerStr);

    this.answerCombo.setEnabled(this.quesRespHandler.isModifiable());
    // Disable result section if it is a heading or if it is not relevant for question
    if (!this.quesRespHandler.showResult(this.rvwAnsObj.getQuestionId())) {
      addNotRelevantSuffix(suffixLabel);
      this.answerCombo.setEnabled(false);
      answerLabel.setEnabled(false);
    }
  }


  /**
   * create links
   *
   * @param form Form
   */
  private void createLinks(final Form form) {
    Composite linkComp = new Composite(form.getBody(), SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    linkComp.setLayout(layout);
    GridData gridDataForComp = GridDataUtil.getInstance().getGridData();
    gridDataForComp.grabExcessVerticalSpace = false;
    linkComp.setLayoutData(gridDataForComp);
    GridData gridData = new GridData();
    gridData.heightHint = 100;
    gridData.verticalSpan = 3;
    Composite labelComp = new Composite(linkComp, SWT.NONE);
    GridLayout labelLayout = new GridLayout();
    labelLayout.numColumns = 2;
    labelComp.setLayout(labelLayout);
    Label linkLabel = LabelUtil.getInstance().createLabel(labelComp, "Link");
    Label suffixLabel = addSuffix(labelComp, isLinkMandatory());
    LabelUtil.getInstance().createLabel(linkComp, EMPTY_STRING);
    this.linkTable = GridTableViewerUtil.getInstance().createGridTableViewer(linkComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, gridData);
    createTabColumns(this.linkTable);
    this.linkTable.setContentProvider(new IStructuredContentProvider() {

      @Override
      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        QnaireAnswerEditComposite.this.linksList = (SortedSet<LinkData>) newInput;
      }

      @Override
      public void dispose() {
        // No Implementation Required
      }

      public Object[] getElements(final Object inputElement) {
        if (inputElement instanceof SortedSet<?>) {
          return ((SortedSet) inputElement).toArray();
        }
        return new Object[0];
      }

    });

    this.linkTable.setLabelProvider(new LinkTableLabelProvider());

    Set<Link> links = this.quesRespHandler.getLinks(this.rvwAnsObj);
    if (links != null) {
      for (Link link : links) {
        this.linksList.add(new LinkData(link));
      }
    }
    this.linkTable.setInput(this.linksList);
    // To Create link Buttons
    createLinkBtns(linkComp);
    // add selection listener to link table
    this.linkTable.getGrid().addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) QnaireAnswerEditComposite.this.linkTable.getSelection();
        if ((selection != null) && (selection.getFirstElement() != null) &&
            QnaireAnswerEditComposite.this.quesRespHandler.isModifiable()) {
          QnaireAnswerEditComposite.this.btnEditLink.setEnabled(true);
          QnaireAnswerEditComposite.this.btnDeleteLink.setEnabled(true);
        }
        else {
          QnaireAnswerEditComposite.this.btnEditLink.setEnabled(false);
          QnaireAnswerEditComposite.this.btnDeleteLink.setEnabled(false);
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // No Implementation Required
      }
    });
    // Disable links section if it is a heading or if it is not relevant for question
    if (!this.quesRespHandler.showLinks(this.rvwAnsObj.getQuestionId())) {
      addNotRelevantSuffix(suffixLabel);
      linkLabel.setEnabled(false);
      this.btnAddLink.setEnabled(false);
      this.linkTable.getGrid().setEnabled(false);
      this.linkTable.getGrid().setHeaderVisible(false);
    }
  }

  /**
   * @param linkComp
   */
  private void createLinkBtns(final Composite linkComp) {
    this.btnAddLink = new Button(linkComp, SWT.PUSH);
    this.btnAddLink.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ADD_16X16));
    this.btnAddLink.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final AddLinkDialog linkDialog =
            new AddLinkDialog(Display.getCurrent().getActiveShell(), QnaireAnswerEditComposite.this.linkTable, false);
        linkDialog.open();
        if (QnaireAnswerEditComposite.this.isEditDialog) {
          enableDisableSaveBtn();
        }
        else {
          // Story 226388
          saveAnswerDetails();
        }
        setCalculatedResultTxtData();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // No Implementation Required
      }
    });
    this.btnAddLink.setEnabled(this.quesRespHandler.isModifiable());
    this.btnEditLink = new Button(linkComp, SWT.PUSH);
    this.btnEditLink.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_16X16));
    this.btnEditLink.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) QnaireAnswerEditComposite.this.linkTable.getSelection();
        final LinkData linkData = (LinkData) selection.getFirstElement();
        final EditLinkDialog linkDialog = new EditLinkDialog(Display.getCurrent().getActiveShell(), linkData,
            QnaireAnswerEditComposite.this.linkTable, false);
        linkDialog.open();
        // Story 226388
        if (!QnaireAnswerEditComposite.this.isEditDialog) {
          saveAnswerDetails();
        }
        setCalculatedResultTxtData();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // No Implementation Required
      }
    });
    this.btnDeleteLink = new Button(linkComp, SWT.PUSH);
    this.btnDeleteLink.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    this.btnDeleteLink.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        IStructuredSelection selection = (IStructuredSelection) QnaireAnswerEditComposite.this.linkTable.getSelection();

        LinkData linkData = (LinkData) selection.getFirstElement();

        QnaireAnswerEditComposite.this.linksList =
            (SortedSet<LinkData>) QnaireAnswerEditComposite.this.linkTable.getInput();

        linkData.setOprType(CommonUIConstants.CHAR_CONSTANT_FOR_DELETE);

        QnaireAnswerEditComposite.this.linkTable.setInput(QnaireAnswerEditComposite.this.linksList);// to invoke input
        // changed
        QnaireAnswerEditComposite.this.linkTable.refresh();

        QnaireAnswerEditComposite.this.linkTable.setSelection(null);
        QnaireAnswerEditComposite.this.btnEditLink.setEnabled(false);
        QnaireAnswerEditComposite.this.btnDeleteLink.setEnabled(false);

        if (QnaireAnswerEditComposite.this.isEditDialog) {
          enableDisableSaveBtn();
        }
        else {
          // Story 226388
          saveAnswerDetails();
        }
        setCalculatedResultTxtData();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // No Implementation Required
      }
    });
    this.btnEditLink.setEnabled(false);
    this.btnDeleteLink.setEnabled(false);
  }

  /**
   * creates the columns of link table viewer
   *
   * @param linkTbl GridTableViewer
   */
  private void createTabColumns(final GridTableViewer linkTbl) {
    GridViewerColumnUtil.getInstance().createGridViewerColumn(linkTbl, "Link", 200);

    GridViewerColumnUtil.getInstance().createGridViewerColumn(linkTbl, "Description", 200);
  }


  /**
   * create measurement available
   *
   * @param form Form
   */
  private void createMeasurementAvailable(final Form form) {
    Composite measureComp = new Composite(form.getBody(), SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 6;
    measureComp.setLayout(layout);
    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    measureComp.setLayoutData(gridData);
    Label measurementLabel = LabelUtil.getInstance().createLabel(measureComp,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_MEASURABLE_Y_N));
    Label suffixLabel = addSuffix(measureComp, isMeasurementMandatory());

    LabelUtil.getInstance().createLabel(measureComp, EMPTY_STRING);
    Button btnMsmntNtDef = new Button(measureComp, SWT.RADIO);
    btnMsmntNtDef.setText(QUESTION_RESP_SERIES_MEASURE.NOT_DEFINED.getUiType());
    btnMsmntNtDef.addSelectionListener(new SelectionListener() {


      @Override
      public void widgetSelected(final SelectionEvent event) {
        QnaireAnswerEditComposite.this.measuremntString = QUESTION_RESP_SERIES_MEASURE.NOT_DEFINED.getDbType();
        if (QnaireAnswerEditComposite.this.isEditDialog) {
          enableDisableSaveBtn();
        }
        else {
          // Story 226388
          saveAnswerDetails();
        }
        setCalculatedResultTxtData();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // No Implementation Required
      }
    });

    Button btnMsmntYes = new Button(measureComp, SWT.RADIO);
    btnMsmntYes.setText(QUESTION_RESP_SERIES_MEASURE.YES.getUiType());
    btnMsmntYes.addSelectionListener(new SelectionListener() {


      @Override
      public void widgetSelected(final SelectionEvent event) {
        QnaireAnswerEditComposite.this.measuremntString = QUESTION_RESP_SERIES_MEASURE.YES.getDbType();
        if (QnaireAnswerEditComposite.this.isEditDialog) {
          enableDisableSaveBtn();
        }
        else {
          // Story 226388
          saveAnswerDetails();
        }
        setCalculatedResultTxtData();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // No Implementation Required
      }
    });
    Button btnMsmntNo = new Button(measureComp, SWT.RADIO);
    btnMsmntNo.setText(QUESTION_RESP_SERIES_MEASURE.NO.getUiType());
    btnMsmntNo.addSelectionListener(new SelectionListener() {


      @Override
      public void widgetSelected(final SelectionEvent event) {
        QnaireAnswerEditComposite.this.measuremntString = QUESTION_RESP_SERIES_MEASURE.NO.getDbType();
        if (QnaireAnswerEditComposite.this.isEditDialog) {
          enableDisableSaveBtn();
        }
        else {
          // Story 226388
          saveAnswerDetails();
        }
        setCalculatedResultTxtData();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // No Implementation Required
      }
    });

    // populate series maturity based on data from database
    String measurement = this.quesRespHandler.getMeasurement(this.rvwAnsObj);
    if (QUESTION_RESP_SERIES_MEASURE.getType(measurement) == QUESTION_RESP_SERIES_MEASURE.YES) {
      this.measuremntString = QUESTION_RESP_SERIES_MEASURE.YES.getDbType();
      btnMsmntYes.setSelection(true);
      btnMsmntNtDef.setSelection(false);
    }
    else if (QUESTION_RESP_SERIES_MEASURE.getType(measurement) == QUESTION_RESP_SERIES_MEASURE.NO) {
      this.measuremntString = QUESTION_RESP_SERIES_MEASURE.NO.getDbType();
      btnMsmntNo.setSelection(true);
      btnMsmntNtDef.setSelection(false);
    }
    else {
      this.measuremntString = QUESTION_RESP_SERIES_MEASURE.NOT_DEFINED.getDbType();
      btnMsmntNtDef.setSelection(true);
    }

    btnMsmntNtDef.setEnabled(this.quesRespHandler.isModifiable());
    btnMsmntYes.setEnabled(this.quesRespHandler.isModifiable());
    btnMsmntNo.setEnabled(this.quesRespHandler.isModifiable());

    // Disable measurement field if it is a heading or if it is not relevant for question
    if (!this.quesRespHandler.showMeasurement(this.rvwAnsObj.getQuestionId())) {
      addNotRelevantSuffix(suffixLabel);
      btnMsmntNtDef.setEnabled(false);
      btnMsmntNo.setEnabled(false);
      btnMsmntYes.setEnabled(false);
      measurementLabel.setEnabled(false);
    }
  }

  /**
   * create series maturity
   *
   * @param form Form
   */
  private void createSeriesMaturity(final Form form) {
    Composite seriesMaturityComp = new Composite(form.getBody(), SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 6;
    seriesMaturityComp.setLayout(layout);
    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    seriesMaturityComp.setLayoutData(gridData);

    Label seriesMaturityLabel = LabelUtil.getInstance().createLabel(seriesMaturityComp,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_SERIES_MAT_Y_N));
    Label suffixLabel = addSuffix(seriesMaturityComp, isSeriesMandatory());

    LabelUtil.getInstance().createLabel(seriesMaturityComp, "                       ");
    Button btnSersMatNtDef = new Button(seriesMaturityComp, SWT.RADIO);
    btnSersMatNtDef.setText(QUESTION_RESP_SERIES_MEASURE.NOT_DEFINED.getUiType());
    btnSersMatNtDef.addSelectionListener(new SelectionListener() {


      @Override
      public void widgetSelected(final SelectionEvent event) {
        QnaireAnswerEditComposite.this.seriesString = QUESTION_RESP_SERIES_MEASURE.NOT_DEFINED.getDbType();

        if (QnaireAnswerEditComposite.this.isEditDialog) {
          enableDisableSaveBtn();
        }
        else {
          // Story 226388
          saveAnswerDetails();
        }
        setCalculatedResultTxtData();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // No Implementation Required
      }
    });

    Button btnSersMatYes = new Button(seriesMaturityComp, SWT.RADIO);
    btnSersMatYes.setText(QUESTION_RESP_SERIES_MEASURE.YES.getUiType());
    btnSersMatYes.addSelectionListener(new SelectionListener() {


      @Override
      public void widgetSelected(final SelectionEvent event) {
        QnaireAnswerEditComposite.this.seriesString = QUESTION_RESP_SERIES_MEASURE.YES.getDbType();
        if (QnaireAnswerEditComposite.this.isEditDialog) {
          enableDisableSaveBtn();
        }
        else {
          // Story 226388
          saveAnswerDetails();
        }
        setCalculatedResultTxtData();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // No Implementation Required
      }
    });

    Button btnSersMatNo = new Button(seriesMaturityComp, SWT.RADIO);
    btnSersMatNo.setText(QUESTION_RESP_SERIES_MEASURE.NO.getUiType());
    btnSersMatNo.addSelectionListener(new SelectionListener() {


      @Override
      public void widgetSelected(final SelectionEvent event) {
        QnaireAnswerEditComposite.this.seriesString = QUESTION_RESP_SERIES_MEASURE.NO.getDbType();
        if (QnaireAnswerEditComposite.this.isEditDialog) {
          enableDisableSaveBtn();
        }
        else {
          // Story 226388
          saveAnswerDetails();
        }
        setCalculatedResultTxtData();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // No Implementation Required
      }
    });

    // populate series maturity based on data from database
    String series = this.quesRespHandler.getSeries(this.rvwAnsObj);
    if (QUESTION_RESP_SERIES_MEASURE.getType(series) == QUESTION_RESP_SERIES_MEASURE.YES) {
      this.seriesString = QUESTION_RESP_SERIES_MEASURE.YES.getDbType();
      btnSersMatYes.setSelection(true);
    }
    else if (QUESTION_RESP_SERIES_MEASURE.getType(series) == QUESTION_RESP_SERIES_MEASURE.NO) {
      this.seriesString = QUESTION_RESP_SERIES_MEASURE.NO.getDbType();
      btnSersMatNo.setSelection(true);
    }
    else {
      this.seriesString = QUESTION_RESP_SERIES_MEASURE.NOT_DEFINED.getDbType();
      btnSersMatNtDef.setSelection(true);
    }

    btnSersMatNtDef.setEnabled(this.quesRespHandler.isModifiable());
    btnSersMatYes.setEnabled(this.quesRespHandler.isModifiable());
    btnSersMatNo.setEnabled(this.quesRespHandler.isModifiable());

    // Disable series maturity field if it is a heading or if it is not relevant for question
    if (!this.quesRespHandler.showSeriesMaturity(this.rvwAnsObj.getQuestionId())) {
      addNotRelevantSuffix(suffixLabel);
      btnSersMatNtDef.setEnabled(false);
      btnSersMatYes.setEnabled(false);
      btnSersMatNo.setEnabled(false);
      seriesMaturityLabel.setEnabled(false);
    }
  }

  /**
   * enables or disables save btn
   */
  public void enableDisableSaveBtn() {

    if ((this.answerCombo != null) && this.answerCombo.getText().equals(
        this.quesRespHandler.getQuestionResultOptionUIString(this.rvwAnsObj.getQuestionId(), this.answerOptId))) {
      saveBtnCheckForEditCase();
    }
    else {
      boolean seriesNotValid = isSeriesMandatory() && CommonUtils.isEmptyString(this.seriesString);
      boolean measurementNotValid = isMeasurementMandatory() && CommonUtils.isEmptyString(this.measuremntString);
      boolean saveDisable = seriesNotValid || measurementNotValid;

      if (this.isEditDialog) {
        this.dialog.enableSaveBtn(!saveDisable);
      }
    }
  }

  private void setCalculatedResultTxtData() {
    // if all the mandatory fields are filled, set the result as 'Finished'
    if (CommonUtils.isNotNull(this.calculatedResultTxt)) {
      String calcResult = checkMandatoryItemsFilled() ? CommonUIConstants.FINISHED : CommonUIConstants.NOT_FINISHED;
      this.calculatedResultTxt.setText(calcResult);
    }
  }

  // condition check to find whether mandatory items are filled
  private boolean checkMandatoryItemsFilled() {
    boolean seriesNotValid = isSeriesMandatory() && CommonUtils.isEmptyString(this.seriesString);
    boolean measurementNotValid = isMeasurementMandatory() && CommonUtils.isEmptyString(this.measuremntString);
    boolean linkValid = false;
    for (LinkData linkData : this.linksList) {
      if (linkData.getOprType() != CommonUIConstants.CHAR_CONSTANT_FOR_DELETE) {
        linkValid = true;
        break;
      }
    }
    boolean linkNotValid = isLinkMandatory() && !linkValid;
    boolean remarksNotValid = isRemarksMandatory() && CommonUtils.isEmptyString(this.remarksStr);
    boolean resultNotValid = isResultMandatory() && CommonUtils.isEmptyString(this.answerCombo.getText());
    boolean mandatoryNotFilled =
        seriesNotValid || measurementNotValid || linkNotValid || remarksNotValid || resultNotValid;

    return !mandatoryNotFilled;
  }


  /**
   *
   */
  private void saveBtnCheckForEditCase() {
    // only in case of edit dialog
    boolean seriesNotValid = isSeriesMandatory() && CommonUtils.isEmptyString(this.seriesString);
    boolean measurementNotValid = isMeasurementMandatory() && CommonUtils.isEmptyString(this.measuremntString);
    boolean linkValid = false;
    for (LinkData linkData : this.linksList) {
      if (linkData.getOprType() != CommonUIConstants.CHAR_CONSTANT_FOR_DELETE) {
        linkValid = true;
        break;
      }
    }
    boolean linkNotValid = isLinkMandatory() && !linkValid;
    boolean remarksNotValid = isRemarksMandatory() && CommonUtils.isEmptyString(this.remarksStr);
    boolean answerNotValid = isResultMandatory() && CommonUtils.isEmptyString(this.answerCombo.getText());
    boolean saveDisable = seriesNotValid || measurementNotValid || linkNotValid || remarksNotValid || answerNotValid;


    if (this.isEditDialog) {
      this.dialog.enableSaveBtn(!saveDisable);
    }
  }


  /**
   * @return
   */
  private boolean isResultMandatory() {
    return this.quesRespHandler.isResultMandatory(this.rvwAnsObj);
  }

  /**
   * @return
   */
  private boolean isRemarksMandatory() {
    return this.quesRespHandler.isRemarksMandatory(this.rvwAnsObj);
  }

  /**
   * @return true if open point is optional field
   */
  public boolean isOpenPointsOptional() {
    return this.quesRespHandler.isOpenPointsOptional(this.rvwAnsObj);
  }

  /**
   * @return
   */
  private boolean isLinkMandatory() {
    return this.quesRespHandler.isLinkMandatory(this.rvwAnsObj);
  }

  /**
   * @return
   */
  private boolean isMeasurementMandatory() {
    return this.quesRespHandler.isMeasurementMandatory(this.rvwAnsObj);
  }

  /**
   * @return
   */
  private boolean isSeriesMandatory() {
    return this.quesRespHandler.isSeriesMandatory(this.rvwAnsObj);
  }

  /**
   * @return true if measurement field is mandatory
   */
  public boolean isMeasuresMandatory() {
    return this.quesRespHandler.isMeasuresMandatory(this.rvwAnsObj);
  }

  /**
   * @return true if Responsible field is mandatory
   */
  public boolean isResponsibleMandatory() {
    return this.quesRespHandler.isResponsibleMandatory(this.rvwAnsObj);
  }

  /**
   * @return true if Date field is mandatory
   */
  public boolean isDateMandatory() {
    return this.quesRespHandler.isDateMandatory(this.rvwAnsObj);
  }


  /**
   * @return the rvwAnsObj
   */
  public synchronized RvwQnaireAnswer getRvwAnsObj() {
    return this.rvwAnsObj;
  }

  /**
   * @return the seriesString
   */
  public String getSeriesString() {
    return this.seriesString;
  }

  /**
   * @return the measuremntString
   */
  public String getMeasuremntString() {
    return this.measuremntString;
  }


  /**
   * @return the openPointsStr
   */
  public String getOpenPointsStr() {
    return this.openPointsStr;
  }

  /**
   * @return the remarksStr
   */
  public String getRemarksStr() {
    return this.remarksStr;
  }

  /**
   * @return the resultStr
   */
  public String getResultStr() {
    return this.answerStr;
  }

  /**
   * @return the linksList
   */
  public SortedSet<LinkData> getLinksList() {
    return this.linksList;
  }

  /**
   *
   */
  private void openOPEditDialog() {
    final IStructuredSelection selection =
        (IStructuredSelection) QnaireAnswerEditComposite.this.openPointsTabViewer.getSelection();
    OpenPointsData opData = (OpenPointsData) selection.getFirstElement();
    QnaireAnswerEditComposite.this.openPointsDialog = new OpenPointsEditDialog(Display.getDefault().getActiveShell(),
        QnaireAnswerEditComposite.this.quesRespHandler, QnaireAnswerEditComposite.this, opData);
    QnaireAnswerEditComposite.this.openPointsDialog.open();
    if (QnaireAnswerEditComposite.this.isEditDialog) {
      enableDisableSaveBtn();
    }
  }

  // Story 226388
  /**
   *
   */
  public void saveAnswerDetails() {
    RvwQnaireAnswer updatedCmdRvwQAns = null;
    if (dataChanged()) {
      RvwQnaireAnswer cmdRvwQAns = this.rvwAnsObj.clone();
      cmdRvwQAns.setMeasurement(getMeasuremntString());
      cmdRvwQAns.setRemark(getRemarksStr());
      cmdRvwQAns.setResult(getAnswerStrType());
      cmdRvwQAns.setSeries(getSeriesString());
      cmdRvwQAns.setSelQnaireResultOptID(this.answerOptId);
      cmdRvwQAns.setQnaireRespVersId(this.quesRespHandler.getQnaireRespModel().getRvwQnrRespVersion().getId());
      updatedCmdRvwQAns = this.quesRespHandler.updateRvwQnaireAns(cmdRvwQAns);
      CommonUtils.shallowCopy(this.rvwAnsObj, updatedCmdRvwQAns);
    }
    else {
      updatedCmdRvwQAns = this.rvwAnsObj;
    }
    // Update link
    if (CommonUtils.isNotEmpty(getLinksList()) && CommonUtils.isNotNull(updatedCmdRvwQAns)) {
      CommonUiUtils.getInstance().createMultipleLinkService(getLinksList(), updatedCmdRvwQAns.getId(),
          MODEL_TYPE.RVW_QNAIRE_ANS);
      refreshLinkTabViewer(updatedCmdRvwQAns);
    }
    // Opl
    if (CommonUtils.isNotEmpty(getOpenPointList())) {
      createMultipleOpenPointService(updatedCmdRvwQAns);
    }
    RvwQnaireAnswer rvwQnaireAnswer =
        this.quesRespHandler.getQnaireRespModel().getRvwQnrAnswrMap().get(updatedCmdRvwQAns.getId());
    CommonUtils.shallowCopy(this.rvwAnsObj, rvwQnaireAnswer);
    this.quesRespHandler.updateQnaireRespVersStatus();
  }

  /**
   * @param updatedCmdRvwQAns RvwQnaireAnswer
   */
  public void refreshLinkTabViewer(final RvwQnaireAnswer updatedCmdRvwQAns) {
    Set<Link> links = this.quesRespHandler.getLinks(updatedCmdRvwQAns);
    this.linksList.clear();
    if (links != null) {
      for (Link link : links) {
        this.linksList.add(new LinkData(link));
      }
      this.linkTable.setInput(this.linksList);
      this.linkTable.refresh();
    }
  }

  private boolean dataChanged() {
    return !CommonUtils.isEqual(getMeasuremntString(), this.rvwAnsObj.getMeasurement()) || validateRemRes() ||
        !CommonUtils.isEqual(getSeriesString(), this.rvwAnsObj.getSeries()) ||
        // If this is a dummy answer, insert the record before further editing
        CommonUtils.isEqual(this.rvwAnsObj.getId(), this.rvwAnsObj.getQuestionId());
  }

  /**
   * @return
   */
  private boolean validateRemRes() {
    return !CommonUtils.isEqual(getRemarksStr(), this.rvwAnsObj.getRemark()) ||
        (!CommonUtils.isEqual(getResultStr(), this.quesRespHandler.getQuestionResultOptionUIString(
            this.rvwAnsObj.getQuestionId(), this.rvwAnsObj.getSelQnaireResultOptID())));
  }


  /**
   * @return the answerStrType
   */
  public String getAnswerStrType() {
    return this.answerStrType;
  }


  /**
   * @param answerStrType the answerStrType to set
   */
  public void setAnswerStrType(final String answerStrType) {
    this.answerStrType = answerStrType;
  }


  /**
   * @return the answerOPTId
   */
  public Long getAnswerOPTId() {
    return this.answerOptId;
  }


  /**
   * @param answerOPTId the answerOPTId to set
   */
  public void setAnswerOPTId(final Long answerOPTId) {
    this.answerOptId = answerOPTId;
  }
}
