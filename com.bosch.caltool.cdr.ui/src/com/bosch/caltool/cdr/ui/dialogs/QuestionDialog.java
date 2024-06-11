/*
 */
package com.bosch.caltool.cdr.ui.dialogs;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.sorters.AttributesGridTabViewerSorter;
import com.bosch.caltool.icdm.client.bo.cdr.QuesDepnAttributeRow;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireDefBO;
import com.bosch.caltool.icdm.common.ui.combo.BasicObjectCombo;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QS_ASSESMENT_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QUESTION_CONFIG_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestAttrAndValDepModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestDepenValCombination;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionConfig;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionCreationData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttr;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttrValue;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOption;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOptionsModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionUpdationData;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.validators.Validator;


/**
 * @author nip4cob
 */
public class QuestionDialog extends AbstractDialog {


  /**
   * Separator between result and its type
   */
  static final String RESULT_SEPARATOR = " : ";

  /**
   * Generic result option
   */
  private static final String ALL_CONST = " (all)";

  /**
   * ICDM-1966 This class provides a dialog to create / edit a question
   */

  private static final int DESC_LEN = 4000;
  /**
   * Unit Col Width
   */
  private static final int UNIT_COL_WIDTH = 100;


  private FormToolkit formToolkit;


  private TabFolder folder;
  private boolean depLoaded = false;
  private Button okBtn;

  private Combo indentCombo;


  private Combo numberCombo;

  private TabItem tab3;

  private Button deletedChkBox;

  private Button resultRelevantChkBox;

  private SashForm compositeOne;

  private Composite compositeTwo;


  private Text qsEngText;


  private Text qsGerText;


  /**
   * Attr Value Combination table viewer
   */
  private GridTableViewer attrsValueTableViewer;


  private Button headingChkBox;


  private SashForm compositeThree;


  /**
   * Attribute dependency table viewer
   */
  private GridTableViewer attrTab;


  private AttributesGridTabViewerSorter attrDepTabSort;
  private Text qsHintEngText;
  private Text qsHintGerText;
  private Combo comboSeries;
  private Combo comboRemark;
  private Combo comboMeasurement;
  private Combo comboOP;
  private Combo comboMeasures;
  private Combo comboResponsible;
  private Combo comboDate;
  private Combo comboResult;
  private Combo comboLink;
  private Action delAttrsAction;
  /**
   *
   */
  protected List<QuesDepnAttributeRow> listAttr = new ArrayList<>();
  /**
   *
   */
  protected List<QuesDepnAttributeRow> listAttrToBeDel = new ArrayList<>();
  /**
   *
   */
  protected List<QuestDepenValCombination> listAttrVal = new ArrayList<>();
  /**
   *
   */
  protected List<QuestDepenValCombination> listAttrValToBeDel = new ArrayList<>();


  private Action addCombAction;
  /**
   *
   */
  protected ControlDecoration qNameEngDec;
  /**
   *
   */
  protected ControlDecoration qHintEngDec;
  /**
   * Attr Col Width
   */
  private static final int ATTR_COL_WIDTH = 150;

  /**
   * Desc Col Width
   */
  private static final int DESC_COL_WIDTH = 300;
  /**
   * Param Attr Col1
   */
  private static final int PARAM_ATTR_TAB_COL1 = 1;
  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();
  private final Set<QuesDepnAttributeRow> selectedAttrs;
  private final List<QuestDepenValCombination> editQuesValCombList = new ArrayList<>();
  private final boolean update;
  private Action addDepAttrAction;
  private Action delValCombAction;
  private Action editCombAction;
  private boolean isLevelChange;
  private boolean isReorderWithinParent;
  private boolean invalidLevelChange;
  private final boolean isWorkingSet;

  private Composite compositeFour;
  private BasicObjectCombo<Question> comboQuestions;
  private Combo comboQuesResp;

  private Question selectedQuesDep;

  private String selDepQuesResp;

  private Question question;
  private Question parentQuestion;
  private final QnaireDefBO qnaireDefBo;
  private Set<QuesDepnAttributeRow> quesAttrSet;
  private QuestAttrAndValDepModel questAttrAndValDepModel;

  private boolean isAttrDepTabLoaded;

  private Section resultsSection;

  private GridTableViewer resultGridViewer;

  /** input for results grid table **/
  private final List<QuestionResultOptionsModel> resultsModelList = new ArrayList<>();

  private final List<QuestionResultOptionsModel> resultOptionsToBeEdited = new ArrayList<>();

  private final List<QuestionResultOptionsModel> resultOptionsToBeAdded = new ArrayList<>();

  private final List<QuestionResultOptionsModel> resultOptionsToBeDeleted = new ArrayList<>();


  /** selected row in results grid table **/
  private QuestionResultOptionsModel selectedResult;

  /** action to delete result **/
  private Action deleteResultAction;
  private Action addResultAction;
  private final Map<Long, String> depResultOptionsMap = new HashMap<>();

  private int currentIndentSelection;

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param question SelectedQuestion
   * @param qnaireDefBo Questionnaire Definition BO
   * @param isUpdate whether create /update
   */
  public QuestionDialog(final Shell parentShell, final Question question, final QnaireDefBO qnaireDefBo,
      final boolean isUpdate) {

    super(parentShell);
    this.question = question;
    this.qnaireDefBo = qnaireDefBo;
    this.selectedAttrs = new TreeSet<>();
    this.update = isUpdate;
    this.isWorkingSet = qnaireDefBo.isWorkingSet();
    if (!isUpdate) {
      this.parentQuestion = this.question;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Title modified
    newShell.setText("Create/Edit Question");
    // Width modified
    int frameX = newShell.getSize().x - newShell.getClientArea().width;
    int frameY = newShell.getSize().y - newShell.getClientArea().height;
    newShell.setSize(950 + frameX, 650 + frameY);
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
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

    this.folder = new TabFolder(composite, SWT.NONE);
    this.folder.setLayout(new GridLayout());
    this.folder.setLayoutData(GridDataUtil.getInstance().getGridData());

    // Tab 1
    final TabItem tab1 = new TabItem(this.folder, SWT.NONE);
    tab1.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNCTION_28X30));
    tab1.setText("Question Definition");
    this.compositeOne = new SashForm(this.folder, SWT.HORIZONTAL);
    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 3;
    this.compositeOne.setLayout(gridLayout1);
    this.compositeOne.setLayoutData(GridDataUtil.getInstance().getGridData());
    createQsSectionOne();
    createQsSectionTwo();
    createQsSectionThree();
    this.compositeOne.setWeights(new int[] { 1, 1, 1 });
    tab1.setControl(this.compositeOne);

    // Tab 2
    final TabItem tab4 = new TabItem(this.folder, SWT.NONE);
    tab4.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNC_SEARCH_28X30));
    tab4.setText("Question Dependency");
    this.compositeFour = new Composite(this.folder, SWT.None);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    this.compositeFour.setLayout(gridLayout);
    tab4.setControl(this.compositeFour);
    createColSecFour();


    // Tab 2
    final TabItem tab2 = new TabItem(this.folder, SWT.NONE);
    tab2.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNC_SEARCH_28X30));
    tab2.setText("Column Assignment");
    this.compositeTwo = new Composite(this.folder, SWT.None);
    gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.compositeTwo.setLayout(gridLayout);
    tab2.setControl(this.compositeTwo);
    createColSecOne();


    // Tab 3
    initializeAssignmentTab();
    this.tab3.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNC_SEARCH_28X30));
    this.tab3.setText("Attributes Assignment");
    this.compositeThree = new SashForm(this.folder, SWT.None);
    final GridLayout gridLayout2 = new GridLayout();
    gridLayout2.numColumns = 2;
    this.compositeThree.setLayoutData(GridDataUtil.getInstance().getGridData());
    createSectionAttrDep();
    createSectionAttrValComb();
    this.compositeThree.setWeights(new int[] { 1, 1 });
    this.tab3.setControl(this.compositeThree);


    if (this.update && (null != getQuestion())) {
      setExistingValuesIfUpdate();
      enableResultsAction();
    }

    // ICDM-2501
    if (null == getQuestion()) {
      disableIfHeading();
    }

    createButtonBar(parent);
    diableFieldsIfSelQuestDeleted();

    return composite;
  }

  private void diableFieldsIfSelQuestDeleted() {
    boolean questionNotDeleted = this.question != null ? !this.question.getDeletedFlag() : true;
    this.comboLink.setEnabled(questionNotDeleted);
    this.comboMeasurement.setEnabled(questionNotDeleted);
    this.comboOP.setEnabled(questionNotDeleted);
    this.comboMeasures.setEnabled(questionNotDeleted);
    this.comboResponsible.setEnabled(questionNotDeleted);
    this.comboDate.setEnabled(questionNotDeleted);
    this.comboRemark.setEnabled(questionNotDeleted);
    this.comboResult.setEnabled(questionNotDeleted);
    this.comboSeries.setEnabled(questionNotDeleted);

    this.resultRelevantChkBox.setEnabled(questionNotDeleted);

    this.addDepAttrAction.setEnabled(questionNotDeleted);
    this.delAttrsAction.setEnabled(!this.attrTab.getSelection().isEmpty() && questionNotDeleted);


    this.addCombAction.setEnabled(questionNotDeleted);
    this.indentCombo.setEnabled(questionNotDeleted);
    this.numberCombo.setEnabled(questionNotDeleted);
    this.headingChkBox.setEnabled(questionNotDeleted);
    this.qsEngText.setEditable(questionNotDeleted);
    this.qsGerText.setEditable(questionNotDeleted);
    this.qsHintEngText.setEditable(questionNotDeleted);
    this.qsHintGerText.setEditable(questionNotDeleted);
    this.comboQuestions.setEnabled(questionNotDeleted);
    this.comboQuesResp.setEnabled(questionNotDeleted);

    boolean isEnabledForAttrVal = !this.attrsValueTableViewer.getSelection().isEmpty() && questionNotDeleted;
    this.editCombAction.setEnabled(isEnabledForAttrVal);
    this.delValCombAction.setEnabled(isEnabledForAttrVal);

    if ((this.question != null) && (this.question.getParentQId() != null)) {
      validateDeleteCheckBox(this.question.getParentQId());
    }
  }

  private void validateDeleteCheckBox(final Long parenQuesId) {
    Question parentQues = this.qnaireDefBo.getQuestion(parenQuesId);
    if ((parentQues.getParentQId() != null) &&
        this.qnaireDefBo.getQuestion(parentQues.getParentQId()).getDeletedFlag()) {
      validateDeleteCheckBox(parentQues.getParentQId());
    }
    else {
      if (parentQues.getHeadingFlag()) {
        this.deletedChkBox.setEnabled(!parentQues.getDeletedFlag());
        return;
      }
      this.deletedChkBox.setEnabled(true);
    }
  }

  /**
   *
   */
  private void initializeAssignmentTab() {
    this.tab3 = new TabItem(this.folder, SWT.NONE);
  }

  /**
   *
   */
  private void setExistingValuesIfUpdate() {

    populateNumCombo(this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(this.question.getParentQId()), false);
    int qNum = getQuestion().getQNumber().intValue();
    this.numberCombo.select(qNum - 1);
    if (!this.isWorkingSet) {
      this.numberCombo.setEnabled(false);
    }
    this.headingChkBox.setSelection(this.question.getHeadingFlag());
    this.deletedChkBox.setSelection(this.question.getDeletedFlag());
    this.resultRelevantChkBox.setSelection(this.question.getResultRelevantFlag());
    this.headingChkBox.setEnabled(this.isWorkingSet);
    this.qsEngText.setText(this.question.getQNameEng());
    this.qsEngText.setEnabled(this.isWorkingSet);
    if (null != this.question.getQNameGer()) {
      this.qsGerText.setText(this.question.getQNameGer());
    }
    this.qsGerText.setEnabled(this.isWorkingSet);
    this.qsHintEngText.setText(this.question.getQHintEng());
    this.qsHintEngText.setEnabled(this.isWorkingSet);
    if (null != this.question.getQHintGer()) {
      this.qsHintGerText.setText(this.question.getQHintGer());
    }
    this.qsHintGerText.setEnabled(this.isWorkingSet);

    if (!this.question.getHeadingFlag()) {
      setValuesForQuestions();
      fillResultOptions();
    }
    else {
      disableIfHeading();
      QuestionDialog.this.addDepAttrAction.setEnabled(false);
      QuestionDialog.this.delAttrsAction.setEnabled(false);
    }
    if ((null == this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(this.question.getParentQId())) ||
        (this.qnaireDefBo.getQuestionLevel(this.question.getId()) == (this.qnaireDefBo.getMaxLevelsAllowed()))) {
      this.headingChkBox.setEnabled(false);
    }
  }

  private void fillResultOptions() {
    for (QuestionResultOption resultOpt : this.qnaireDefBo.getQnaireResultOptions(this.question.getId()).values()) {
      QuestionResultOptionsModel resOptModel = new QuestionResultOptionsModel();
      resOptModel.setAssesment(resultOpt.getQResultType());
      resOptModel.setResult(resultOpt.getQResultName());
      resOptModel.setQuestionResultOptId(resultOpt.getId());
      resOptModel.setAllowFinishWP(resultOpt.isqResultAlwFinishWP());
      
      this.resultsModelList.add(resOptModel);
    }
    this.resultGridViewer.setInput(this.resultsModelList);
  }


  /**
   *
   */
  private void setValuesForQuestions() {
    if (null != this.qnaireDefBo.getQnaireDefModel().getQuestionConfigMap().get(this.question.getId())) {

      this.comboLink.select(this.comboLink.indexOf(this.qnaireDefBo.getLinkUIString(this.question.getId())));
      this.comboLink.setEnabled(this.isWorkingSet && this.qnaireDefBo.showLink());
      this.comboMeasurement
          .select(this.comboMeasurement.indexOf(this.qnaireDefBo.getMeasurementUIString(this.question.getId())));
      this.comboMeasurement.setEnabled(this.isWorkingSet && this.qnaireDefBo.showMeasurement());
      this.comboOP.select(this.comboOP.indexOf(this.qnaireDefBo.getOpenPointsUIString(this.question.getId())));
      this.comboOP.setEnabled(this.isWorkingSet && this.qnaireDefBo.showOpenPoints());

      this.comboMeasures.select(this.comboMeasures.indexOf(this.qnaireDefBo.getMeasureUIString(this.question.getId())));
      this.comboMeasures.setEnabled(this.isWorkingSet && this.qnaireDefBo.showMeasure());
      this.comboResponsible
          .select(this.comboResponsible.indexOf(this.qnaireDefBo.getResponsibleUIString(this.question.getId())));
      this.comboResponsible.setEnabled(this.isWorkingSet && this.qnaireDefBo.showResponsible());
      this.comboDate.select(this.comboDate.indexOf(this.qnaireDefBo.getCompletionDateUIString(this.question.getId())));
      this.comboDate.setEnabled(this.isWorkingSet && this.qnaireDefBo.showCompletionDate());


      this.comboRemark.select(this.comboRemark.indexOf(this.qnaireDefBo.getRemarkUIString(this.question.getId())));
      this.comboRemark.setEnabled(this.isWorkingSet && this.qnaireDefBo.showRemark());
      this.comboResult.select(this.comboResult.indexOf(this.qnaireDefBo.getResultUIString(this.question.getId())));
      this.comboResult.setEnabled(this.isWorkingSet && this.qnaireDefBo.showResult());
      this.comboSeries.select(this.comboSeries.indexOf(this.qnaireDefBo.getSeriesUIString(this.question.getId())));
      this.comboSeries.setEnabled(this.isWorkingSet && this.qnaireDefBo.showSeries());
    }

  }

  /**
   *
   */
  public void setAttrAndValDep() {
    if (!this.depLoaded) {
      loadAttrAndValDep();
    }
    this.attrTab.setInput(this.quesAttrSet);
    if (null != this.questAttrAndValDepModel) {
      this.attrsValueTableViewer.setInput(createCombinations(this.questAttrAndValDepModel));
    }
    QuestionDialog.this.addDepAttrAction.setEnabled(!this.question.getDeletedFlag() && this.isWorkingSet);
  }


  private void loadAttrAndValDep() {
    Set<Attribute> attrSet = this.qnaireDefBo.getAttributes(this.question.getId());
    if (null != attrSet) {
      this.quesAttrSet = new TreeSet<>();
      for (Attribute attr : attrSet) {
        QuesDepnAttributeRow attrRow = new QuesDepnAttributeRow(this.question, attr, false);
        this.quesAttrSet.add(attrRow);
      }
      this.questAttrAndValDepModel = this.qnaireDefBo.getQuestDependentAttrAndValModel(this.question.getId());
      this.depLoaded = true;
    }
  }

  /**
   * @param questAttrAndValDepModel
   */
  private Set<QuestDepenValCombination> createCombinations(final QuestAttrAndValDepModel questAttrAndValDepModel) {
    Map<Long, QuestDepenValCombination> retMap = new HashMap<>();
    for (QuestionDepenAttr qDepAttr : questAttrAndValDepModel.getqDepAttrMap().values()) {
      for (QuestionDepenAttrValue qDepAttrVal : questAttrAndValDepModel.getqDepattrValueMap().get(qDepAttr.getId())) {
        QuestDepenValCombination combi = null;
        if (!retMap.containsKey(qDepAttrVal.getQCombiNum())) {
          // since it is used only for display purpose only the attributevalue map is inserted for a combination
          combi = new QuestDepenValCombination();
          retMap.put(qDepAttrVal.getQCombiNum(), combi);
        }
        else {
          combi = retMap.get(qDepAttrVal.getQCombiNum());
        }
        if (combi != null) {
          combi.getQuesAttrValMap().put(qDepAttr, qDepAttrVal);
          combi.getAttrValMap().put(questAttrAndValDepModel.getAttributeMap().get(qDepAttr.getAttrId()),
              questAttrAndValDepModel.getAttributeValueMap().get(qDepAttrVal.getValueId()));
          combi.setCombinationId(qDepAttrVal.getQCombiNum());
        }
      }
    }
    return retMap.values().stream().collect(Collectors.toCollection(HashSet::new));
  }

  /**
   * populate number combo
   *
   * @param parentQuestion
   */
  private void populateNumCombo(final Question parentQuestion, final boolean isLevelChange) {
    this.numberCombo.removeAll();
    this.isLevelChange = isLevelChange;
    // ICDM-1972
    Integer size = 0;
    if (CommonUtils.isNotNull(parentQuestion)) {
      if (this.qnaireDefBo.hasChildQuestion(parentQuestion.getId())) {
        size = this.qnaireDefBo.getQnaireDefModel().getChildQuestionIdMap().get(parentQuestion.getId()).size();
      }
      this.numberCombo.add(this.qnaireDefBo.getQuestionNumber(parentQuestion.getId()) + ".1");
    }
    else {
      size = this.qnaireDefBo.getFirstLevelQuestions().size();
      this.numberCombo.add("1");
    }
    if (!this.update || isLevelChange) {
      // add one additional entry for the new one
      size++;
    }
    for (Long qNo = 2L; qNo <= size.longValue(); qNo++) {
      StringBuilder qnoBuilder = new StringBuilder(10);
      if (parentQuestion != null) {
        qnoBuilder.append(this.qnaireDefBo.getQuestionNumber(parentQuestion.getId())).append(".");
      }
      qnoBuilder.append(qNo.toString());
      this.numberCombo.add(qnoBuilder.toString());
    }
    if (isLevelChange) {
      this.numberCombo.select(this.numberCombo.getItemCount() - 1);
    }
    else if (null != this.question) {
      int qNum = this.question.getQNumber().intValue();
      this.numberCombo.select(qNum - 1);
    }
  }


  /**
   * table for attr dep
   */
  private void createSectionAttrDep() {
    final Section sectionAttrDep =
        this.formToolkit.createSection(this.compositeThree, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 2;
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    sectionAttrDep.setText("Dependent Attributes");
    sectionAttrDep.setDescription("List of attributes applicable for dependency definition");
    sectionAttrDep.setExpanded(true);
    sectionAttrDep.getDescriptionControl().setEnabled(false);

    createToolBarAction(sectionAttrDep);
    final Form formParamProperties = this.formToolkit.createForm(sectionAttrDep);
    formParamProperties.getBody().setLayout(gridLayout1);
    formParamProperties.setLayoutData(gridData);
    createEmptyLables(1, formParamProperties.getBody());
    createAttrTable(formParamProperties);
    sectionAttrDep.setClient(formParamProperties);
  }

  /**
   *
   */
  private void createToolBarAction(final Section section) {
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(section);

    addNewAttrDepnAction(toolBarManager);

    addDeleteAttrDepnAction(toolBarManager);


    toolBarManager.update(true);

    section.setTextClient(toolbar);
  }

  /**
   * @param toolBarManager
   */
  private void addDeleteAttrDepnAction(final ToolBarManager toolBarManager) {
    // Create an action to delete attr from table
    this.delAttrsAction = new Action("Delete Dependency", SWT.NONE) {

      @Override
      public void run() {
        if (QuestionDialog.this.attrsValueTableViewer.getGrid().getItems().length != 0) {
          MessageDialogUtils.getErrorMessageDialog("",
              "Deletion cannot be done since existing value combinations exist");
        }
        else {
          List<QuesDepnAttributeRow> existingAttrs = new ArrayList<>();
          for (GridItem iterable_element : QuestionDialog.this.attrTab.getGrid().getItems()) {
            QuesDepnAttributeRow qVal = (QuesDepnAttributeRow) iterable_element.getData();
            existingAttrs.add(qVal);
          }
          for (QuesDepnAttributeRow object : QuestionDialog.this.listAttr) {
            existingAttrs.remove(object);
            QuestionDialog.this.listAttrToBeDel.add(object);
          }

          QuestionDialog.this.attrTab.setInput(existingAttrs);

          checkSaveBtnEnable();

        }
      }
    };
    // Set the image for add link action
    this.delAttrsAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    this.delAttrsAction.setEnabled(false);
    toolBarManager.add(this.delAttrsAction);

  }

  /**
   * @param toolBarManager
   */
  private void addNewAttrDepnAction(final ToolBarManager toolBarManager) {
    // Create an action to add attrs depn
    this.addDepAttrAction = new Action("Add Dependency", SWT.NONE) {

      @Override
      public void run() {

        AddAttributeDepnDialog dialog =
            new AddAttributeDepnDialog(CommonUiUtils.getInstance().getDisplay().getActiveShell(), QuestionDialog.this,
                QuestionDialog.this.selectedAttrs);
        dialog.open();

      }
    };
    // Set the image for add link action
    this.addDepAttrAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    this.addDepAttrAction.setEnabled(true);
    toolBarManager.add(this.addDepAttrAction);
  }

  /**
   * @param form
   * @param sectionAttrDep
   */
  private void createAttrTable(final Form form) {

    this.attrTab =
        new GridTableViewer(form.getBody(), SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
    this.attrTab.setAutoPreferredHeight(true);
    this.attrTab.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.attrTab.getGrid().setLinesVisible(true);
    this.attrTab.getGrid().setHeaderVisible(true);
    this.attrDepTabSort = new AttributesGridTabViewerSorter();
    this.attrTab.setComparator(this.attrDepTabSort);
    createTabColumns2();
    this.attrTab.setContentProvider(ArrayContentProvider.getInstance());
    this.attrTab.setInput(null);
    createAttrsSelLis();
    createTabListener();
  }

  /**
   *
   */
  private void createTabListener() {

    this.folder.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        TabItem[] selection = QuestionDialog.this.folder.getSelection();
        for (TabItem tab : selection) {
          if ((CommonUtils.isEqual(tab, QuestionDialog.this.tab3)) && QuestionDialog.this.update &&
              (null != getQuestion()) && !QuestionDialog.this.isAttrDepTabLoaded) {
            setAttrAndValDep();
            // The content in the tab needs to be loaded only once
            QuestionDialog.this.isAttrDepTabLoaded = true;
          }
        }
      }
    });
  }

  /**
   *
   */
  private void createAttrsSelLis() {
    this.attrTab.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {

        IStructuredSelection selection = (IStructuredSelection) QuestionDialog.this.attrTab.getSelection();

        QuestionDialog.this.listAttr = new ArrayList<>(selection.toList());

        if (!QuestionDialog.this.listAttr.isEmpty()) {
          if (QuestionDialog.this.question != null) {
            QuestionDialog.this.delAttrsAction
                .setEnabled(!QuestionDialog.this.question.getDeletedFlag() && QuestionDialog.this.isWorkingSet);
          }
          else {
            QuestionDialog.this.delAttrsAction.setEnabled(QuestionDialog.this.isWorkingSet);
          }
        }
      }
    });

  }

  /**
   * ICdm-1087 - new Table for Attr dep Defines the columns of the TableViewer
   */
  private void createTabColumns2() {

    final GridViewerColumn attrColumn = new GridViewerColumn(this.attrTab, SWT.NONE);
    attrColumn.getColumn().setText("Attribute");
    attrColumn.getColumn().setWidth(ATTR_COL_WIDTH);
    attrColumn.getColumn().setResizeable(true);

    attrColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QuesDepnAttributeRow attr = (QuesDepnAttributeRow) element;
        return attr.getAttribute().getName();
      }
    });

    attrColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrColumn.getColumn(), ApicConstants.ATTR_TAB_COL0, this.attrDepTabSort, this.attrTab));

    final GridViewerColumn descColumn = new GridViewerColumn(this.attrTab, SWT.NONE);
    descColumn.getColumn().setText("Description");
    descColumn.getColumn().setWidth(DESC_COL_WIDTH);
    descColumn.getColumn().setResizeable(true);

    descColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QuesDepnAttributeRow attr = (QuesDepnAttributeRow) element;
        return attr.getAttribute().getDescription();
      }
    });

    descColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(descColumn.getColumn(), PARAM_ATTR_TAB_COL1, this.attrDepTabSort, this.attrTab));

    final GridViewerColumn unitColumn = new GridViewerColumn(this.attrTab, SWT.NONE);
    unitColumn.getColumn().setText("Unit");
    unitColumn.getColumn().setWidth(UNIT_COL_WIDTH);
    unitColumn.getColumn().setResizeable(true);

    unitColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QuesDepnAttributeRow attr = (QuesDepnAttributeRow) element;
        return attr.getAttribute().getUnit();
      }
    });

    unitColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(unitColumn.getColumn(), 2, this.attrDepTabSort, this.attrTab));
  }


  /**
   * @return the question
   */
  public Question getQuestion() {
    return this.question;
  }


  /**
   * @param question the question to set
   */
  public void setQuestion(final Question question) {
    this.question = question;
  }

  /**
   */
  private void createQsSectionOne() {


    Section sectionOne =
        getFormToolkit().createSection(this.compositeOne, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionOne.setText("Properties");
    sectionOne.setExpanded(true);
    sectionOne.getDescriptionControl().setEnabled(false);

    final GridLayout gridLayout2 = new GridLayout();
    gridLayout2.numColumns = 2;


    final Form formProperties = this.formToolkit.createForm(sectionOne);
    formProperties.getBody().setLayout(new GridLayout());
    formProperties.setLayoutData(GridDataUtil.getInstance().getGridData());

    final Composite composite = new Composite(formProperties.getBody(), SWT.NONE);
    composite.setLayout(gridLayout2);
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());


    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;

    createEmptyLables(2, composite);
    createIntendLevelField(composite, gridData);
    createEmptyLables(2, composite);

    createNumberField(composite, gridData);
    createEmptyLables(2, composite);

    createIsHeadingField(composite);


    final Composite tableComp = new Composite(formProperties.getBody(), SWT.NONE);
    tableComp.setLayout(new GridLayout());
    tableComp.setLayoutData(GridDataUtil.getInstance().getGridData());

    createResults(tableComp);

    final Composite composite2 = new Composite(formProperties.getBody(), SWT.NONE);
    composite2.setLayout(gridLayout2);
    composite2.setLayoutData(GridDataUtil.getInstance().getGridData());

    createEmptyLables(2, composite2);

    createResultRelevantField(composite2);

    createEmptyLables(2, composite2);

    createDeletedField(composite2);
    if (!this.update) {
      populateNumCombo(this.question, false);
      this.numberCombo.setEnabled(true);
      if (null == this.question) {
        enbleDisableCheckbox(false, true);
      }
      else if (this.qnaireDefBo
          .getQuestionLevel(this.question.getId()) == (this.qnaireDefBo.getMaxLevelsAllowed() - 1)) {
        enbleDisableCheckbox(false, false);
      }
      else {
        enbleDisableCheckbox(true, false);
      }
      this.numberCombo.select(this.numberCombo.getItemCount() - 1);


    }

    sectionOne.setClient(formProperties);

  }


  /**
   * @param tableComp
   */
  private void createResults(final Composite tableComp) {


    this.resultsSection =
        getFormToolkit().createSection(tableComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.resultsSection.setExpanded(true);
    this.resultsSection.setText("Answers");
    this.resultsSection.getDescriptionControl().setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    this.resultsSection.setDescription("Answer is mandatory to save the Question");
    this.resultsSection.setLayout(new GridLayout(1, false));
    this.resultsSection.setLayoutData(GridDataUtil.getInstance().getGridData());

    final Composite composite = new Composite(this.resultsSection, SWT.NONE);
    composite.setLayout(new GridLayout());
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());

    createToolbarActionForResults();
    createResultsGridTable(composite);


    this.resultsSection.setClient(composite);
  }

  /**
   * @param composite
   */
  private void createToolbarActionForResults() {
    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = toolBarManager.createControl(this.resultsSection);
    toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));

    addResultsAction(toolBarManager);
    deleteResultAction(toolBarManager);

    toolBarManager.update(true);
    this.resultsSection.setTextClient(toolbar);
  }

  private void addResultsAction(final ToolBarManager toolBarManager) {
    this.addResultAction = new Action("Add Result", SWT.NONE) {

      @Override
      public void run() {
        AddResultDialog addResultDialog = new AddResultDialog(Display.getCurrent().getActiveShell());
        addResultDialog.open();

        QuestionResultOptionsModel resultToAdd = addResultDialog.getResultModel();
        if (resultToAdd != null) {
          if (isDuplicateResult(resultToAdd)) {
            CDMLogger.getInstance().errorDialog("Multiple records with same result is not possible!",
                Activator.PLUGIN_ID);
          }
          else {
            if (QuestionDialog.this.update) {
              QuestionDialog.this.resultOptionsToBeAdded.add(resultToAdd);
            }
            getResultsModelList().add(resultToAdd);
            QuestionDialog.this.resultGridViewer.setInput(QuestionDialog.this.getResultsModelList());
            QuestionDialog.this.resultGridViewer.refresh();
            checkSaveBtnEnable();
          }
        }
      }
    };
    this.addResultAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    toolBarManager.add(this.addResultAction);
  }

  /**
   * @param resultToAdd New Result option
   * @return true,if same result with or without assesment, else false
   */
  protected boolean isDuplicateResult(final QuestionResultOptionsModel resultToAdd) {
    for (QuestionResultOptionsModel qResult : this.resultsModelList) {
      if (resultToAdd.getResult().equalsIgnoreCase(qResult.getResult())) {
        return true;
      }
    }
    return false;
  }

  private void deleteResultAction(final ToolBarManager toolBarManager) {
    this.deleteResultAction = new Action("Delete Result", SWT.NONE) {

      @Override
      public void run() {
        // if result option is already edited before deleting, remove result option from updation list
        if (QuestionDialog.this.getResultOptionsToBeEdited().contains(QuestionDialog.this.selectedResult)) {
          QuestionDialog.this.getResultOptionsToBeEdited().remove(QuestionDialog.this.selectedResult);
        }
        // if result option is newly added(not inserted into db) before deleting, remove result option from creation
        // list
        if (QuestionDialog.this.resultOptionsToBeAdded.contains(QuestionDialog.this.selectedResult)) {
          QuestionDialog.this.resultOptionsToBeAdded.remove(QuestionDialog.this.selectedResult);
        }
        // if result option is available in DB ,add to deletion list
        if (QuestionDialog.this.selectedResult.getQuestionResultOptId() != null) {
          QuestionDialog.this.resultOptionsToBeDeleted.add(QuestionDialog.this.selectedResult);
        }

        QuestionDialog.this.getResultsModelList().remove(QuestionDialog.this.selectedResult);
        QuestionDialog.this.resultGridViewer.setInput(QuestionDialog.this.getResultsModelList());
        QuestionDialog.this.resultGridViewer.refresh();
        checkSaveBtnEnable();
      }
    };

    this.deleteResultAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));

    toolBarManager.add(this.deleteResultAction);
  }

  /**
   * @param composite
   */
  private void createResultsGridTable(final Composite composite) {

    this.resultGridViewer = GridTableViewerUtil.getInstance().createGridTableViewer(composite,
        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, GridDataUtil.getInstance().getGridData());
    this.resultGridViewer.setContentProvider(new ArrayContentProvider());
    this.resultGridViewer.getGrid().setLinesVisible(true);
    this.resultGridViewer.getGrid().setHeaderVisible(true);
    this.resultGridViewer.setAutoPreferredHeight(true);

    createResultColumn();
    createAssesmentColumn();
    createAllowFinishWP();
    addListener();
  }


  /**
   *
   */
  private void addListener() {

    this.resultGridViewer.addSelectionChangedListener(arg0 -> {
      if (!QuestionDialog.this.resultGridViewer.getStructuredSelection().isEmpty()) {
        Object selectedRow = QuestionDialog.this.resultGridViewer.getStructuredSelection().toArray()[0];
        QuestionDialog.this.selectedResult = (QuestionResultOptionsModel) selectedRow;
      }
      else {
        QuestionDialog.this.selectedResult = null;
      }
      enableResultsAction();

    });

  }

  /**
   *
   */
  private void createResultColumn() {
    final GridViewerColumn resultColumn = new GridViewerColumn(this.resultGridViewer, SWT.NONE);
    resultColumn.getColumn().setText("Answer");
    resultColumn.getColumn().setWidth(80);
    ColumnViewerToolTipSupport.enableFor(this.resultGridViewer, ToolTip.NO_RECREATE);

    resultColumn.setEditingSupport(createTextCellEditingSupport(resultColumn.getViewer()));

    resultColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof QuestionResultOptionsModel) {
          QuestionResultOptionsModel results = (QuestionResultOptionsModel) element;
          return results.getResult();
        }
        return "";
      }
    });
  }

  // to create editing support for results column in results grid table
  private EditingSupport createTextCellEditingSupport(final ColumnViewer columnViewer) {
    return new QuestionDialgResultOptionsTextEditingSupport(this, columnViewer);
  }

  /**
   *
   */
  private void createAssesmentColumn() {
    final GridViewerColumn assesmentCol = new GridViewerColumn(this.resultGridViewer, SWT.DROP_DOWN);
    assesmentCol.getColumn().setText("Assesment");
    assesmentCol.getColumn().setWidth(80);
    ColumnViewerToolTipSupport.enableFor(this.resultGridViewer, ToolTip.NO_RECREATE);

    QuestionDialgResultOptionsAssesmentTypeEditingSupport editingSupport =
        new QuestionDialgResultOptionsAssesmentTypeEditingSupport(this, assesmentCol.getViewer());
    assesmentCol.setEditingSupport(editingSupport);
    assesmentCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof QuestionResultOptionsModel) {
          QuestionResultOptionsModel results = (QuestionResultOptionsModel) element;
          return QS_ASSESMENT_TYPE.getTypeByDbCode(results.getAssesment()).getUiType();
        }
        return "";
      }

    });
  }
  
  
  /**
   * Delete flag column creation
   */
  private void createAllowFinishWP() {
    
    final GridViewerColumn allowFinishWPGridViewer = new GridViewerColumn(this.resultGridViewer,SWT.CHECK | SWT.CENTER);
    allowFinishWPGridViewer.getColumn().setText("Answer allowed to finish WP");
    allowFinishWPGridViewer.getColumn().setWidth(100);
    ColumnViewerToolTipSupport.enableFor(this.resultGridViewer, ToolTip.NO_RECREATE);
    
    
    allowFinishWPGridViewer.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        if (element instanceof QuestionResultOptionsModel) {
          QuestionResultOptionsModel questionResultOptionsModel = (QuestionResultOptionsModel) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
          gridItem.setChecked(cell.getVisualIndex(), questionResultOptionsModel.isAllowFinishWP());
        }
      }
    });
    

    allowFinishWPGridViewer.setEditingSupport(new CheckEditingSupport(allowFinishWPGridViewer.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        QuestionResultOptionsModel resOptModel = (QuestionResultOptionsModel) arg0;
        
        // avoids adding multiple same entry into list
        if (getResultOptionsToBeEdited().contains(resOptModel)) {
          getResultOptionsToBeEdited().remove(resOptModel);
        }
        resOptModel.setAllowFinishWP((Boolean) arg1);
        if (resOptModel.getQuestionResultOptId() != null) {
          getResultOptionsToBeEdited().add(resOptModel);
        }
        checkSaveBtnEnable();
      }

    });

  }

  boolean isEditable() {
    return QuestionDialog.this.isWorkingSet && !QuestionDialog.this.deletedChkBox.getSelection() &&
        !QuestionDialog.this.headingChkBox.getSelection();
  }

  private void createResultRelevantField(final Composite composite2) {
    LabelUtil.getInstance().createLabel(composite2, "Result relevant for overall status");
    this.resultRelevantChkBox = new Button(composite2, SWT.CHECK);
    this.resultRelevantChkBox.setSelection(true);
    this.resultRelevantChkBox
        .setToolTipText("This checkbox labels a question as relevant for the overall status of the questionnaire.\r\n" +
            "Default is 'Checked'. It should be disabled just for questions that are only controlling\r\n" +
            "if a part of the questionnaire is shown or not and if a negative answer doesn't have any consequences within the project.\r\n" +
            "E.g. a question like 'Does it concern UMD diagnosis?', which only enabled some other questions.");
    this.resultRelevantChkBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        disableIfHeading();

        if (QuestionDialog.this.update) {
          checkSaveBtnEnable();
        }

      }
    });
  }


  /**
   * @param composite2
   */
  private void createDeletedField(final Composite composite2) {
    LabelUtil.getInstance().createLabel(composite2, "Deleted");
    this.deletedChkBox = new Button(composite2, SWT.CHECK);

    this.deletedChkBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        disableIfHeading();

        if (QuestionDialog.this.update) {
          checkSaveBtnEnable();
        }

      }
    });
  }

  /**
   * @param composite
   */
  private void createIsHeadingField(final Composite composite) {
    LabelUtil.getInstance().createLabel(composite, "Is Heading");
    this.headingChkBox = new Button(composite, SWT.CHECK);

    this.headingChkBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        disableIfHeading();
        QuestionDialog.this.delAttrsAction.setEnabled(false);
        if (QuestionDialog.this.update) {
          checkSaveBtnEnable();
        }

      }
    });
  }

  /**
   * @param composite
   * @param gridData
   */
  private void createNumberField(final Composite composite, final GridData gridData) {
    LabelUtil.getInstance().createLabel(composite, "Number");
    this.numberCombo = new Combo(composite, SWT.READ_ONLY);
    this.numberCombo.setLayoutData(gridData);
    this.numberCombo.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        QuestionDialog.this.isReorderWithinParent = true;
        checkSaveBtnEnable();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // TODO Auto-generated method stub

      }
    });

    this.numberCombo.select(this.numberCombo.getItems().length - 1);
  }

  /**
   * @param composite
   * @param gridData
   */
  private void createIntendLevelField(final Composite composite, final GridData gridData) {
    LabelUtil.getInstance().createLabel(composite, "Indent level");
    this.indentCombo = new Combo(composite, SWT.READ_ONLY);
    this.indentCombo.setLayoutData(gridData);

    this.indentCombo.setEnabled(this.update && this.isWorkingSet);

    // this.indentCombo.add("");// ICDM-1972
    this.indentCombo.add("Level 1");
    this.indentCombo.add("Level 2");
    this.indentCombo.add("Level 3");
    if (null == this.question) {
      this.indentCombo.select(CommonUIConstants.COLUMN_INDEX_0);
    }
    else {
      int questionLevel = this.qnaireDefBo.getQuestionLevel(this.question.getId());
      if (this.update) {
        if (ApicConstants.LEVEL_1 == questionLevel) {
          this.indentCombo.select(CommonUIConstants.COLUMN_INDEX_0);
        }
        else if (ApicConstants.LEVEL_2 == questionLevel) {
          this.indentCombo.select(CommonUIConstants.COLUMN_INDEX_1);
        }
        else {
          this.indentCombo.select(CommonUIConstants.COLUMN_INDEX_2);
        }
      }
      else {
        if (ApicConstants.LEVEL_1 == questionLevel) {
          this.indentCombo.select(CommonUIConstants.COLUMN_INDEX_1);
        }
        else if (ApicConstants.LEVEL_2 == questionLevel) {
          this.indentCombo.select(CommonUIConstants.COLUMN_INDEX_2);
        }
      }
    }
    this.currentIndentSelection = this.indentCombo.getSelectionIndex() + 1;
    this.indentCombo.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        fillNumberCombo();
        checkSaveBtnEnable();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // TODO Auto-generated method stub

      }
    });
  }

  /**
   *
   */
  protected void fillNumberCombo() {
    if (this.update) {
      fillNumComboForUpdate();
    }
    else {
      fillNumComboForAdd();
    }
  }

  /**
   *
   */
  private void fillNumComboForUpdate() {
    int selectedIndex = getSelectedQuestionLevel();
    int questionLevel = this.qnaireDefBo.getQuestionLevel(this.question.getId());
    boolean currentCheckboxSel = this.headingChkBox.getSelection();

    switch (questionLevel) {

      case ApicConstants.LEVEL_1:
        updQuestionFromLevel1(selectedIndex, currentCheckboxSel);
        break;

      case ApicConstants.LEVEL_2:
        updQuestionFromLevel2(selectedIndex, currentCheckboxSel);
        break;

      case ApicConstants.LEVEL_3:
        updQuestionFromLevel3(selectedIndex, currentCheckboxSel);
        break;

      default:
        break;
    }
  }

  /**
   * @param selectedIndex
   * @param isCheckboxSelected
   */
  private void updQuestionFromLevel3(final int selectedIndex, final boolean isCheckboxSelected) {
    if (selectedIndex == ApicConstants.LEVEL_1) {
      addQuestionAtLevel1();
    }
    else if (selectedIndex == ApicConstants.LEVEL_2) {
      enbleDisableCheckbox(true, isCheckboxSelected);
      populateNumCombo(
          this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(
              this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(this.question.getParentQId()).getParentQId()),
          true);
    }
    else {
      enbleDisableCheckbox(true, isCheckboxSelected);
      populateNumCombo(this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(this.question.getParentQId()), false);
    }
  }

  /**
   * @param selectedIndex
   * @param isCheckboxSelected
   */
  private void updQuestionFromLevel2(final int selectedIndex, final boolean isCheckboxSelected) {
    if (selectedIndex == ApicConstants.LEVEL_1) {
      addQuestionAtLevel1();
    }
    else if (selectedIndex == ApicConstants.LEVEL_3) {
      enbleDisableCheckbox(true, isCheckboxSelected);
      moveLevel2To3();
    }
    else {
      enbleDisableCheckbox(true, isCheckboxSelected);
      populateNumCombo(this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(this.question.getParentQId()), false);
    }
  }

  /**
   * @param selectedIndex
   * @param isCheckboxSelected
   */
  private void updQuestionFromLevel1(final int selectedIndex, final boolean isCheckboxSelected) {
    if (selectedIndex == ApicConstants.LEVEL_3) {
      CDMLogger.getInstance().infoDialog("Questions in Level 1 cannot be moved to Level 3", Activator.PLUGIN_ID);
      this.indentCombo.select(CommonUIConstants.COLUMN_INDEX_0);
      populateNumCombo(this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(this.question.getParentQId()), false);
      enbleDisableCheckbox(false, true);
    }
    else if (selectedIndex == ApicConstants.LEVEL_2) {
      enbleDisableCheckbox(true, isCheckboxSelected);
      moveLevel1To2();
    }
    else {
      enbleDisableCheckbox(true, true);
      populateNumCombo(this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(this.question.getParentQId()), false);
    }
  }

  /**
   *
   */
  private void fillNumComboForAdd() {
    if (null == this.question) {

      this.indentCombo.select(CommonUIConstants.COLUMN_INDEX_0);
      CDMLogger.getInstance().infoDialog("Please select a question to add next level questions", Activator.PLUGIN_ID);
    }
    else {
      int questionLevel = this.qnaireDefBo.getQuestionLevel(this.question.getId());
      int selectedIndex = getSelectedQuestionLevel();
      this.parentQuestion = this.question;

      if ((selectedIndex == questionLevel) || (selectedIndex == (questionLevel + 1))) {
        this.currentIndentSelection = selectedIndex;
        switch (selectedIndex) {

          case ApicConstants.LEVEL_1:
            addQuestionAtLevel1();
            break;

          case ApicConstants.LEVEL_2:
            addQuestionAtLevel2(questionLevel, selectedIndex);
            break;

          case ApicConstants.LEVEL_3:
            addQuestionAtLevel3();
            break;

          default:
            break;
        }
      }
      else {
        CDMLogger.getInstance().infoDialog("Questions can be added in the same level or next level",
            Activator.PLUGIN_ID);
        this.indentCombo.select(this.currentIndentSelection - 1);
      }
    }
  }

  /**
   *
   */
  private void addQuestionAtLevel3() {
    enbleDisableCheckbox(true, false);
    populateNumCombo(this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(this.question.getId()), true);
  }

  /**
   * @param questionLevel
   * @param selectedIndex
   */
  private void addQuestionAtLevel2(final int questionLevel, final int selectedIndex) {
    enbleDisableCheckbox(true, false);
    if (selectedIndex == questionLevel) {
      this.parentQuestion = this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(this.question.getParentQId());
      populateNumCombo(this.parentQuestion, true);
    }
    else {
      populateNumCombo(this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(this.question.getId()), true);
    }
  }

  /**
   *
   */
  private void addQuestionAtLevel1() {
    if (!this.headingChkBox.getSelection()) {
      CDMLogger.getInstance().infoDialog("Only headings can be added in first level", Activator.PLUGIN_ID);
      enbleDisableCheckbox(false, true);
      disableIfHeading();
    }
    populateNumCombo(null, true);
  }

  /**
   *
   */
  private void enbleDisableCheckbox(final boolean isEnabled, final boolean isSelected) {
    this.headingChkBox.setSelection(isSelected);
    this.headingChkBox.setEnabled(isEnabled);
  }


  /**
   *
   */
  private void moveLevel2To3() {
    // Check whether the selected question in level 2 has child questions
    if (this.qnaireDefBo.getQnaireDefModel().getChildQuestionIdMap().get(this.question.getId()).isEmpty()) {
      if (this.qnaireDefBo.getPreviousQuestion(this.question.getId()) == null) {
        CDMLogger.getInstance().infoDialog("No previous question available , cannot be moved to third level!",
            Activator.PLUGIN_ID);
        this.invalidLevelChange = true;
        this.indentCombo.select(CommonUIConstants.COLUMN_INDEX_1);

      }
      else {
        populateNumCombo(this.qnaireDefBo.getPreviousQuestion(this.question.getId()), true);
      }
    }
    else {
      CDMLogger.getInstance().infoDialog("Question has maximum levels allowed. Cannot be moved to third level!",
          Activator.PLUGIN_ID);
      this.invalidLevelChange = true;
      this.indentCombo.select(CommonUIConstants.COLUMN_INDEX_1);
    }

  }

  /**
   *
   */
  private void moveLevel1To2() {
    Question previousQs = this.qnaireDefBo.getPreviousQuestion(this.question.getId());
    if (this.qnaireDefBo.hasThirdLevel(this.question.getId())) {
      CDMLogger.getInstance().infoDialog("Question has maximum levels allowed. Cannot be moved to second level!",
          Activator.PLUGIN_ID);
      this.invalidLevelChange = true;
      this.indentCombo.select(CommonUIConstants.COLUMN_INDEX_0);
    }
    else if (null == previousQs) {
      CDMLogger.getInstance().infoDialog("No previous question available , cannot be moved to second level!",
          Activator.PLUGIN_ID);
      this.invalidLevelChange = true;
      this.indentCombo.select(CommonUIConstants.COLUMN_INDEX_0);
    }
    else if (!this.qnaireDefBo.getQnaireDefModel().getChildQuestionIdMap().isEmpty()) {
      for (Long childId : this.qnaireDefBo.getQnaireDefModel().getChildQuestionIdMap().get(this.question.getId())) {
        if (this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(childId).getHeadingFlag()) {
          CDMLogger.getInstance().infoDialog(
              " Cannot be moved to second level , question has headings as children which cannot be added to last level!",
              Activator.PLUGIN_ID);
          this.invalidLevelChange = true;
          this.indentCombo.select(CommonUIConstants.COLUMN_INDEX_0);
          break;
        }
      }
    }
    if (!this.invalidLevelChange) {
      populateNumCombo(previousQs, true);
    }
  }

  /**
   * @return the level number selected in the combo
   */
  public int getSelectedQuestionLevel() {
    int selectedIndex = this.indentCombo.getSelectionIndex();
    if (selectedIndex == CommonUIConstants.COLUMN_INDEX_0) {
      return ApicConstants.LEVEL_1;
    }
    else if (selectedIndex == CommonUIConstants.COLUMN_INDEX_1) {
      return ApicConstants.LEVEL_2;
    }
    else {
      return ApicConstants.LEVEL_3;
    }
  }

  /**
   *
   */
  private void disableIfHeading() {
    final boolean isChecked = QuestionDialog.this.headingChkBox.getSelection();

    boolean enableFields = !isChecked && !this.question.getDeletedFlag();
    QuestionDialog.this.comboLink.setEnabled(enableFields);
    QuestionDialog.this.comboMeasurement.setEnabled(enableFields);
    QuestionDialog.this.comboOP.setEnabled(enableFields);
    QuestionDialog.this.comboMeasures.setEnabled(enableFields);
    QuestionDialog.this.comboResponsible.setEnabled(enableFields);
    QuestionDialog.this.comboDate.setEnabled(enableFields);
    QuestionDialog.this.comboRemark.setEnabled(enableFields);
    QuestionDialog.this.comboResult.setEnabled(enableFields);
    QuestionDialog.this.comboSeries.setEnabled(enableFields);
    QuestionDialog.this.resultRelevantChkBox.setEnabled(enableFields);


    boolean quesVal = this.question != null ? !this.question.getDeletedFlag() : true;
    QuestionDialog.this.addDepAttrAction.setEnabled(quesVal && this.isWorkingSet);
    QuestionDialog.this.delAttrsAction.setEnabled(quesVal && this.isWorkingSet);
    QuestionDialog.this.addCombAction.setEnabled(quesVal && this.isWorkingSet);

    enableResultsAction();

  }

  private void enableResultsAction() {
    boolean enableFlag = isEditable();
    QuestionDialog.this.deleteResultAction.setEnabled(isEditable() && (QuestionDialog.this.selectedResult != null));
    QuestionDialog.this.addResultAction.setEnabled(enableFlag);

  }

  /**
   * @param labelCount no of labels to be created
   * @param composite Composite on which label needs to be created
   */
  public void createEmptyLables(final int labelCount, final Composite composite) {
    for (int i = 0; i < labelCount; i++) {
      LabelUtil.getInstance().createLabel(composite, "");
    }
  }

  /**
   * @param formToolkit2
   */
  private void createQsSectionTwo() {

    Section sectionTwo =
        getFormToolkit().createSection(this.compositeOne, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionTwo.setText("Question");
    sectionTwo.setExpanded(true);
    sectionTwo.getDescriptionControl().setEnabled(false);

    final GridLayout gridLayout = new GridLayout();
    sectionTwo.setLayout(gridLayout);
    sectionTwo.setLayoutData(GridDataUtil.getInstance().getGridData());

    final Form formProperties = this.formToolkit.createForm(sectionTwo);
    formProperties.getBody().setLayout(gridLayout);
    formProperties.setLayoutData(GridDataUtil.getInstance().getGridData());

    LabelUtil.getInstance().createLabel(formProperties.getBody(), "Question(English)");
    TextBoxContentDisplay textBoxObj = new TextBoxContentDisplay(formProperties.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, DESC_LEN, GridDataUtil.getInstance().getGridData());
    textBoxObj.setFocus();


    this.qsEngText = textBoxObj.getText();
    this.qsEngText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent evnt) {

        Validator.getInstance().validateNDecorate(QuestionDialog.this.qNameEngDec, QuestionDialog.this.qsEngText, null,
            null, true);
        checkSaveBtnEnable();
      }
    });
    this.qNameEngDec = new ControlDecoration(this.qsEngText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.qNameEngDec, "This field is mandatory.");

    LabelUtil.getInstance().createLabel(formProperties.getBody(), "Question(German)");
    TextBoxContentDisplay textBoxObj1 = new TextBoxContentDisplay(formProperties.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, DESC_LEN, GridDataUtil.getInstance().getGridData());
    this.qsGerText = textBoxObj1.getText();


    this.qsGerText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent evnt) {
        checkSaveBtnEnable();
      }
    });

    sectionTwo.setClient(formProperties);

  }

  /**
   * @param formToolkit2
   */
  private void createQsSectionThree() {

    Section sectionThree =
        getFormToolkit().createSection(this.compositeOne, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionThree.setText(CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_HINT));
    sectionThree.setExpanded(true);
    sectionThree.getDescriptionControl().setEnabled(false);

    final GridLayout gridLayout = new GridLayout();
    sectionThree.setLayout(gridLayout);
    sectionThree.setLayoutData(GridDataUtil.getInstance().getGridData());

    final Form formProperties = this.formToolkit.createForm(sectionThree);
    formProperties.getBody().setLayout(gridLayout);
    formProperties.setLayoutData(GridDataUtil.getInstance().getGridData());

    LabelUtil.getInstance().createLabel(formProperties.getBody(),
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_HINT) + "(English)");
    TextBoxContentDisplay textBoxObj = new TextBoxContentDisplay(formProperties.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, DESC_LEN, GridDataUtil.getInstance().getGridData());
    this.qsHintEngText = textBoxObj.getText();
    this.qsHintEngText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent evnt) {

        Validator.getInstance().validateNDecorate(QuestionDialog.this.qHintEngDec, QuestionDialog.this.qsHintEngText,
            null, null, true);
        checkSaveBtnEnable();
      }
    });
    this.qHintEngDec = new ControlDecoration(this.qsHintEngText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.qHintEngDec, "This field is mandatory.");

    LabelUtil.getInstance().createLabel(formProperties.getBody(),
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_HINT) + "(German)");
    TextBoxContentDisplay textBoxObj1 = new TextBoxContentDisplay(formProperties.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, DESC_LEN, GridDataUtil.getInstance().getGridData());
    this.qsHintGerText = textBoxObj1.getText();

    this.qsHintGerText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent evnt) {
        checkSaveBtnEnable();
      }
    });

    sectionThree.setClient(formProperties);

  }


  /**
   *
   */
  protected void checkSaveBtnEnable() {
    if (this.invalidLevelChange) {
      this.okBtn.setEnabled(false);
      this.invalidLevelChange = false;
    }
    else {
      if (validateFields() && (this.okBtn != null)) {
        this.okBtn.setEnabled(true);
      }
      else if (this.okBtn != null) {
        this.okBtn.setEnabled(false);
      }
    }
  }

  /**
   * @return
   */
  private boolean validateFields() {
    String nameEng = this.qsEngText.getText();
    String hintEng = this.qsHintEngText.getText();

    // To validate Dependency question and response
    boolean validDep = (CommonUtils.isNull(this.selectedQuesDep) && CommonUtils.isNull(this.selDepQuesResp)) ||
        (CommonUtils.isNotNull(this.selectedQuesDep) && CommonUtils.isNotNull(this.selDepQuesResp));

    // When IsHeading = false , Atleast one result option should be available
    return (isNameAndHintValid(nameEng, hintEng) &&
        (!this.resultsModelList.isEmpty() || this.headingChkBox.getSelection()) && validDep);
  }

  /**
   * @param nameEng
   * @param hintEng
   * @return
   */
  private boolean isNameAndHintValid(final String nameEng, final String hintEng) {
    return !ApicConstants.EMPTY_STRING.equals(nameEng.trim()) && !ApicConstants.EMPTY_STRING.equals(hintEng.trim());
  }

  /**
   *
   */
  private void createSectionAttrValComb() {
    Section sectionOne =
        getFormToolkit().createSection(this.compositeThree, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionOne.setText("Attribute Value Combinations");
    sectionOne.setDescription(
        "Select attribute value combinations for dependency. The question will be shown only for the PIDCs with any one of the combinations below");
    sectionOne.setExpanded(true);
    sectionOne.getDescriptionControl().setEnabled(false);

    sectionOne.setLayout(new GridLayout());
    sectionOne.setLayoutData(GridDataUtil.getInstance().getGridData());

    final Form formProperties = this.formToolkit.createForm(sectionOne);
    formProperties.getBody().setLayout(new GridLayout());
    formProperties.setLayoutData(GridDataUtil.getInstance().getGridData());
    createToolBarActionOnAttrComb(sectionOne);
    createAttrValueTable(formProperties);
    sectionOne.setClient(formProperties);

  }

  /**
   * @param sectionOne
   */
  private void createToolBarActionOnAttrComb(final Section sectionOne) {
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(sectionOne);

    addNewAttrValCombAction(toolBarManager);

    addEditAttrValCombAction(toolBarManager);

    addDeleteAttrValCombAction(toolBarManager);


    toolBarManager.update(true);

    sectionOne.setTextClient(toolbar);
  }

  /**
   * @param toolBarManager
   */
  private void addEditAttrValCombAction(final ToolBarManager toolBarManager) {
    // Create an action to edit a combination
    this.editCombAction = new Action("Edit combination", SWT.NONE) {

      @Override
      public void run() {

        if ((null == QuestionDialog.this.listAttrVal) || QuestionDialog.this.listAttrVal.isEmpty()) {
          MessageDialogUtils.getErrorMessageDialog("", "Please select a combination to edit!");
        }
        else {
          addAttributesToList();
          AddAttributeValueCombDialog dialog =
              new AddAttributeValueCombDialog(CommonUiUtils.getInstance().getDisplay().getActiveShell(),
                  QuestionDialog.this, QuestionDialog.this.selectedAttrs, QuestionDialog.this.listAttrVal);
          dialog.open();
        }
      }
    };
    // Set the image for edit action
    this.editCombAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    this.editCombAction.setEnabled(false);
    toolBarManager.add(this.editCombAction);
  }

  /**
   * @param toolBarManager
   */
  private void addDeleteAttrValCombAction(final ToolBarManager toolBarManager) {
    // Create an action to delete
    this.delValCombAction = new Action("Delete a combination", SWT.NONE) {

      @Override
      public void run() {

        List<QuestDepenValCombination> existingVals = new ArrayList<>();
        for (GridItem iterable_element : QuestionDialog.this.attrsValueTableViewer.getGrid().getItems()) {
          QuestDepenValCombination qVal = (QuestDepenValCombination) iterable_element.getData();
          existingVals.add(qVal);
        }
        for (QuestDepenValCombination object : QuestionDialog.this.listAttrVal) {
          existingVals.remove(object);
          QuestionDialog.this.listAttrValToBeDel.add(object);
        }

        QuestionDialog.this.attrsValueTableViewer.setInput(existingVals);
        QuestionDialog.this.delValCombAction.setEnabled(false);

        checkSaveBtnEnable();
      }
    };
    // Set the image for delete action
    this.delValCombAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));

    this.delValCombAction.setEnabled(false);
    toolBarManager.add(this.delValCombAction);

  }

  /**
   * @param toolBarManager
   */
  private void addNewAttrValCombAction(final ToolBarManager toolBarManager) {
    // Create an action to add a combination
    this.addCombAction = new Action("Add a combination", SWT.NONE) {

      @Override
      public void run() {
        if (QuestionDialog.this.attrTab.getGrid().getItems().length == 0) {
          MessageDialogUtils.getInfoMessageDialog("Missing Attribute Dependency", "Please add dependent attribute(s) ");
        }
        else {
          addAttributesToList();
          AddAttributeValueCombDialog dialog =
              new AddAttributeValueCombDialog(CommonUiUtils.getInstance().getDisplay().getActiveShell(),
                  QuestionDialog.this, QuestionDialog.this.selectedAttrs, null);
          dialog.open();
        }
      }
    };
    // Set the image for add action
    this.addCombAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    this.addCombAction.setEnabled(this.isWorkingSet);
    toolBarManager.add(this.addCombAction);
  }

  /**
   *
   */
  private void addAttributesToList() {
    QuestionDialog.this.selectedAttrs.clear();
    if (QuestionDialog.this.attrTab.getGrid().getItems().length != 0) {
      for (GridItem iterable_element : QuestionDialog.this.attrTab.getGrid().getItems()) {
        QuesDepnAttributeRow attrRow = (QuesDepnAttributeRow) iterable_element.getData();
        QuestionDialog.this.selectedAttrs.add(attrRow);
      }
    }
  }

  /**
   * @param formProperties
   */
  private void createAttrValueTable(final Form formProperties) {
    this.attrsValueTableViewer =
        new GridTableViewer(formProperties.getBody(), SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);


    this.attrsValueTableViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());

    this.attrsValueTableViewer.getGrid().setLinesVisible(true);
    this.attrsValueTableViewer.getGrid().setHeaderVisible(true);

    this.attrsValueTableViewer.setContentProvider(ArrayContentProvider.getInstance());


    createAttrValCol();


    this.attrsValueTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {

        IStructuredSelection selection =
            (IStructuredSelection) QuestionDialog.this.attrsValueTableViewer.getSelection();
        if (!selection.isEmpty()) {

          QuestionDialog.this.listAttrVal = new ArrayList<>(selection.toList());
          if (!QuestionDialog.this.listAttrVal.isEmpty()) {
            if (QuestionDialog.this.question != null) {
              QuestionDialog.this.delValCombAction
                  .setEnabled(!QuestionDialog.this.question.getDeletedFlag() && QuestionDialog.this.isWorkingSet);
              QuestionDialog.this.editCombAction
                  .setEnabled(!QuestionDialog.this.question.getDeletedFlag() && QuestionDialog.this.isWorkingSet);
            }
            else {
              QuestionDialog.this.delValCombAction.setEnabled(QuestionDialog.this.isWorkingSet);
              QuestionDialog.this.editCombAction.setEnabled(QuestionDialog.this.isWorkingSet);
            }
          }
        }
      }
    });
  }


  /**
   * Creates attr column
   */
  private void createAttrValCol() {
    final GridViewerColumn attrValColumn = new GridViewerColumn(this.attrsValueTableViewer, SWT.NONE);
    attrValColumn.getColumn().setText("Attribute Value Combination");
    attrValColumn.getColumn().setWidth(280);

    attrValColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QuestDepenValCombination combi = (QuestDepenValCombination) element;
        return QuestionDialog.this.qnaireDefBo.getDisplayText(combi);
      }
    });
  }

  /**
   *
   */
  private void createColSecOne() {
    Section sectionOne =
        getFormToolkit().createSection(this.compositeTwo, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionOne.setText("Column Settings");
    sectionOne.setDescription("Defines the questions relationship to the available columns");
    sectionOne.setExpanded(true);
    sectionOne.getDescriptionControl().setEnabled(false);

    GridLayout layout = new GridLayout();
    layout.numColumns = 4;
    sectionOne.setLayout(layout);
    sectionOne.setLayoutData(GridDataUtil.getInstance().getGridData());

    final Form formProperties = this.formToolkit.createForm(sectionOne);
    formProperties.getBody().setLayout(layout);
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;

    formProperties.setLayoutData(GridDataUtil.getInstance().getGridData());
    createEmptyLables(8, formProperties.getBody());

    LabelUtil.getInstance().createLabel(formProperties.getBody(),
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_MEASURABLE_Y_N));
    this.comboMeasurement = new Combo(formProperties.getBody(), SWT.READ_ONLY);
    this.comboMeasurement.setLayoutData(gridData);


    LabelUtil.getInstance().createLabel(formProperties.getBody(),
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_SERIES_MAT_Y_N));
    this.comboSeries = new Combo(formProperties.getBody(), SWT.READ_ONLY);
    this.comboSeries.setLayoutData(gridData);

    createEmptyLables(4, formProperties.getBody());

    LabelUtil.getInstance().createLabel(formProperties.getBody(), "Link");
    this.comboLink = new Combo(formProperties.getBody(), SWT.READ_ONLY);
    this.comboLink.setLayoutData(gridData);


    LabelUtil.getInstance().createLabel(formProperties.getBody(),
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_REMARK));
    this.comboRemark = new Combo(formProperties.getBody(), SWT.READ_ONLY);
    this.comboRemark.setLayoutData(gridData);

    createEmptyLables(4, formProperties.getBody());
    // ICDM-2188
    LabelUtil.getInstance().createLabel(formProperties.getBody(),
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_OPEN_POINTS));

    this.comboOP = new Combo(formProperties.getBody(), SWT.READ_ONLY);
    this.comboOP.setLayoutData(gridData);

    LabelUtil.getInstance().createLabel(formProperties.getBody(),
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_MEASURE));
    this.comboMeasures = new Combo(formProperties.getBody(), SWT.READ_ONLY);
    this.comboMeasures.setLayoutData(gridData);


    createEmptyLables(4, formProperties.getBody());

    LabelUtil.getInstance().createLabel(formProperties.getBody(),
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_RESPONSIBLE));
    this.comboResponsible = new Combo(formProperties.getBody(), SWT.READ_ONLY);
    this.comboResponsible.setLayoutData(gridData);

    LabelUtil.getInstance().createLabel(formProperties.getBody(),
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_DATE));
    this.comboDate = new Combo(formProperties.getBody(), SWT.READ_ONLY);
    this.comboDate.setLayoutData(gridData);

    createEmptyLables(4, formProperties.getBody());

    LabelUtil.getInstance().createLabel(formProperties.getBody(),
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_RESULT));
    this.comboResult = new Combo(formProperties.getBody(), SWT.READ_ONLY);
    this.comboResult.setLayoutData(gridData);


    for (QUESTION_CONFIG_TYPE iterable_element : QUESTION_CONFIG_TYPE.values()) {
      if (iterable_element != QUESTION_CONFIG_TYPE.NONE) {

        this.comboLink.add(iterable_element.getUiType());
        this.comboResult.add(iterable_element.getUiType());
        this.comboMeasures.add(iterable_element.getUiType());
        this.comboResponsible.add(iterable_element.getUiType());
        this.comboDate.add(iterable_element.getUiType());
        this.comboMeasurement.add(iterable_element.getUiType());
        this.comboRemark.add(iterable_element.getUiType());
        this.comboSeries.add(iterable_element.getUiType());
      }

      this.comboLink.select(0);
      this.comboResult.select(0);
      this.comboMeasures.select(0);
      this.comboResponsible.select(0);
      this.comboDate.select(0);
      this.comboMeasurement.select(0);
      this.comboRemark.select(0);
      this.comboSeries.select(0);
    }
    this.comboOP.add(QUESTION_CONFIG_TYPE.OPTIONAL.getUiType());
    this.comboOP.add(QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType());
    this.comboOP.select(0);
    this.comboLink.addSelectionListener(comboSelectionListener());
    this.comboResult.addSelectionListener(comboSelectionListener());
    this.comboOP.addSelectionListener(comboSelectionListener());
    this.comboMeasures.addSelectionListener(comboSelectionListener());
    this.comboResponsible.addSelectionListener(comboSelectionListener());
    this.comboDate.addSelectionListener(comboSelectionListener());

    this.comboMeasurement.addSelectionListener(comboSelectionListener());
    this.comboRemark.addSelectionListener(comboSelectionListener());
    this.comboSeries.addSelectionListener(comboSelectionListener());

    sectionOne.setClient(formProperties);

  }

  /**
   * new Section for dependency
   */
  private void createColSecFour() {
    Section sectionOne =
        getFormToolkit().createSection(this.compositeFour, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionOne.setText("Question Dependency");
    sectionOne.setDescription("Defines the question dependency to another question");
    sectionOne.setExpanded(true);
    sectionOne.getDescriptionControl().setEnabled(false);

    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    sectionOne.setLayout(layout);
    sectionOne.setLayoutData(GridDataUtil.getInstance().getGridData());

    final Form formProperties = this.formToolkit.createForm(sectionOne);
    formProperties.getBody().setLayout(layout);
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;

    formProperties.setLayoutData(GridDataUtil.getInstance().getGridData());


    createComboQuestion(formProperties, gridData);
    sectionOne.setClient(formProperties);
  }

  /**
   * @param formProperties
   * @param gridData
   */
  private void createComboQuestion(final Form formProperties, final GridData gridData) {
    LabelUtil.getInstance().createLabel(formProperties.getBody(), "This question is shown in response only if");
    this.comboQuestions = new BasicObjectCombo<Question>(formProperties.getBody(), SWT.READ_ONLY, " ") {

      /**
       * {@inheritDoc}
       */
      @Override
      protected String getName(final Question element) {
        return QuestionDialog.this.qnaireDefBo.getQuestionNumberWithName(element.getId());
      }
    };

    this.comboQuestions.setLayoutData(gridData);
    createEmptyLables(2, formProperties.getBody());
    comboQuestionSelectionListener();


    LabelUtil.getInstance().createLabel(formProperties.getBody(), "is answered");
    this.comboQuesResp = new Combo(formProperties.getBody(), SWT.READ_ONLY);
    this.comboQuesResp.setLayoutData(gridData);

    this.comboQuesResp.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        QuestionDialog.this.selDepQuesResp =
            QuestionDialog.this.comboQuesResp.getItem(QuestionDialog.this.comboQuesResp.getSelectionIndex());

        checkSaveBtnEnable();

      }
    });
    // get all questions for the version
    SortedSet<Question> allQuestions = this.qnaireDefBo.getAllQuestions(false, true);
    Question dependentQuestion = null;
    // remove the current question and set the dependency
    if ((this.question != null) && this.update) {
      allQuestions = this.qnaireDefBo.getQuestionWithoutCyclic(this.question);
      allQuestions.remove(this.question);
      dependentQuestion = this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(this.question.getDepQuesId());
      QuestionDialog.this.selectedQuesDep =
          this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(this.question.getDepQuesId());
    }
    this.comboQuestions.setElements(allQuestions);


    // set the existing values in UI.
    if (isUpdate() && (dependentQuestion != null)) {
      this.comboQuestions.select(dependentQuestion);
      initializeQuesRespCombo(dependentQuestion);

      if (CommonUtils.isNotNull(this.question.getDepQuesResp())) {
        String currentResponse = QS_ASSESMENT_TYPE.getTypeByDbCode(this.question.getDepQuesResp()).getUiType() +
            RESULT_SEPARATOR + ALL_CONST;
        for (String response : this.comboQuesResp.getItems()) {
          if (response.equals(currentResponse)) {
            this.comboQuesResp.setText(response);
            this.selDepQuesResp = this.question.getDepQuesResp();
            break;
          }
        }
      }
      else if (this.question.getDepQResultOptId() != null) {
        String depResponse = this.depResultOptionsMap.get(this.question.getDepQResultOptId());
        this.comboQuesResp.setText(depResponse);
        this.selDepQuesResp = depResponse;
      }
    }
  }

  /**
   * 
   */
  private void comboQuestionSelectionListener() {
    this.comboQuestions.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        Question selectedItem = QuestionDialog.this.comboQuestions.getSelectedItem();
        if (selectedItem != null) {
          QuestionDialog.this.selectedQuesDep = selectedItem;
          initializeQuesRespCombo(QuestionDialog.this.selectedQuesDep);
        }
        else {
          QuestionDialog.this.selectedQuesDep = null;
          QuestionDialog.this.comboQuesResp.removeAll();
          QuestionDialog.this.comboQuesResp.setText(" ");
          QuestionDialog.this.selDepQuesResp = null;
        }
        checkSaveBtnEnable();
      }
    });
  }


  /**
   * @param selectedDepQues selected Dependent Question
   */
  protected void initializeQuesRespCombo(final Question selectedDepQues) {
    this.comboQuesResp.removeAll();
    this.selDepQuesResp = null;
    this.depResultOptionsMap.clear();
    Set<String> depResults = new HashSet<>();

    QuestionDialog.this.qnaireDefBo.getQnaireResultOptions(selectedDepQues.getId()).values().stream()
        .forEach(resultOpt -> {
          String uiType = QS_ASSESMENT_TYPE.getTypeByDbCode(resultOpt.getQResultType()).getUiType();

          depResults.add(uiType + RESULT_SEPARATOR + ALL_CONST);

          String resultOptionStr = uiType + RESULT_SEPARATOR + resultOpt.getQResultName();
          depResults.add(resultOptionStr);
          this.depResultOptionsMap.put(resultOpt.getId(), resultOptionStr);
        });

    List<String> depResultsList = new ArrayList<>();
    depResultsList.addAll(depResults);

    Collections.sort(depResultsList, new QuestionResultOptionComparator());
    this.comboQuesResp.setItems(depResultsList.toArray(new String[0]));
  }

  /**
   * @return
   */
  private SelectionAdapter comboSelectionListener() {
    return new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        if (QuestionDialog.this.update) {
          checkSaveBtnEnable();
        }

      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, "Save", false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
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
  protected void okPressed() {
    if (this.update) {
      QuestionUpdationData updateModel = new QuestionUpdationData();
      if (setFieldsForUpdate(updateModel)) {
        this.qnaireDefBo.updateQuestion(updateModel);
        super.okPressed();
      }
    }
    else {
      QuestionCreationData newData = new QuestionCreationData();
      if (setFieldsForCreation(newData)) {
        this.qnaireDefBo.createQuestion(newData);
        super.okPressed();
      }
    }
  }

  /**
   *
   */
  private boolean setFieldsForCreation(final QuestionCreationData creationData) {
    Question newQuestion = new Question();
    Question parent = null;
    if (!this.headingChkBox.getSelection() ||
        (this.indentCombo.getSelectionIndex() != CommonUIConstants.COLUMN_INDEX_0)) {
      parent = this.parentQuestion == null ? null : this.parentQuestion;
    }
    String qsSel = this.numberCombo.getText();
    Long qNumber;
    // flag to indicate whether assigned question number is to be changed or not
    creationData.setReorderWithinParent(QuestionDialog.this.isReorderWithinParent);

    if (qsSel.contains(".")) {
      qNumber = Long.valueOf(qsSel.substring(qsSel.lastIndexOf('.') + 1, qsSel.length()));
    }
    else {
      qNumber = Long.valueOf(qsSel);
    }
    newQuestion.setQNumber(qNumber);
    newQuestion.setResultRelevantFlag(this.resultRelevantChkBox.getSelection());
    if (parent != null) {
      newQuestion.setParentQId(parent.getId() == null ? null : parent.getId());
    }
    setQDepFieldForQuestion(newQuestion);
    setNameDesc(newQuestion);
    newQuestion.setHeadingFlag(this.headingChkBox.getSelection());
    newQuestion.setQnaireVersId(this.qnaireDefBo.getQnaireVersion().getId());

    creationData.setQuestion(newQuestion);
    if (!this.headingChkBox.getSelection()) {
      creationData.setQuestionConfig(createConfig());
      creationData.setQnaireResultOptionModel(this.resultsModelList);
    }

    if (validateAttrDepnCombinations()) {
      createAttrAndValDep(creationData, null);
    }
    else {
      MessageDialogUtils.getErrorMessageDialog("Validation failed !",
          "The attribute value configuration is not complete. Please add values for all the attributes then continue to save.");
      return false;
    }
    return true;
  }

  /**
   * @param creationData
   */
  private void createAttrAndValDep(final QuestionCreationData creationData, final QuestionUpdationData updationData) {

    GridItem[] attrItems = this.attrTab.getGrid().getItems();
    List<Attribute> listofAttrs = new ArrayList<>();
    for (GridItem gridItem : attrItems) {
      QuesDepnAttributeRow row = (QuesDepnAttributeRow) gridItem.getData();
      if ((this.update && row.isAddedToExistingOnes() &&
          !this.qnaireDefBo.getAttributes(this.question.getId()).contains((row).getAttribute())) || !this.update) {
        listofAttrs.add((row).getAttribute());
      }
    }

    GridItem[] items = this.attrsValueTableViewer.getGrid().getItems();
    List<QuestDepenValCombination> listComb = new ArrayList<>();
    List<QuestDepenValCombination> existingComb = new ArrayList<>();

    for (GridItem gridItem : items) {
      QuestDepenValCombination rowData = (QuestDepenValCombination) gridItem.getData();
      if (rowData.getCombinationId() != 0) {
        existingComb.add(rowData);
      }
      if ((this.update && rowData.isAddedToExistingOnes()) || !this.update) {
        listComb.add(rowData);
      }
    }
    if (validateAttrDepnCombinations()) {
      List<QuestionDepenAttr> qDepAttrList = new ArrayList<>();
      for (Attribute attr : listofAttrs) {
        QuestionDepenAttr qDepAttr = new QuestionDepenAttr();
        qDepAttr.setAttrId(attr.getId());
        qDepAttr.setQId((this.question == null) ? null : this.question.getId());
        qDepAttrList.add(qDepAttr);
      }
      if (isUpdate()) {
        updationData.setqDepAttrToAdd(qDepAttrList);
      }
      else {
        creationData.setAttributes(qDepAttrList);

      }
      if (!listComb.isEmpty()) {
        Map<String, QuestionDepenAttrValue> qDepAttrValMap = new HashMap<>();

        long combinationNum = this.qnaireDefBo.getMaxCombNumber(existingComb) + 1;

        for (QuestDepenValCombination quesDepnValComb : listComb) {
          Map<Attribute, AttributeValue> attrValCombiMap = quesDepnValComb.getAttrValMap();
          quesDepnValComb.setCombinationId(combinationNum);
          for (Entry<Attribute, AttributeValue> attrAndValEntry : attrValCombiMap.entrySet()) {
            QuestionDepenAttrValue qDepAttrVal = new QuestionDepenAttrValue();
            qDepAttrVal.setQCombiNum(quesDepnValComb.getCombinationId());
            qDepAttrVal.setValueId(attrAndValEntry.getValue().getId());
            if (this.question != null) {
              qDepAttrValMap.put(String.valueOf(this.question.getId()) +
                  String.valueOf(attrAndValEntry.getKey().getId()) + String.valueOf(combinationNum), qDepAttrVal);
            }
            else {
              qDepAttrValMap.put(String.valueOf(attrAndValEntry.getKey().getId()) + String.valueOf(combinationNum),
                  qDepAttrVal);
            }
          }
          combinationNum++;
        }
        if (this.update) {
          updationData.setqDepValToAdd(qDepAttrValMap);
        }
        else {
          creationData.setqDepValCombMap(qDepAttrValMap);
        }
      }
    }
  }


  /**
   * @param newQuestion
   */
  private void setQDepFieldForQuestion(final Question newQuestion) {
    if (CommonUtils.isNotNull(this.selectedQuesDep) && CommonUtils.isNotNull(this.selDepQuesResp)) {
      newQuestion.setDepQuesId(this.selectedQuesDep.getId());
      for (Entry<Long, String> entry : this.depResultOptionsMap.entrySet()) {
        if (entry.getValue().equals(this.selDepQuesResp)) {
          newQuestion.setDepQResultOptId(entry.getKey());
          newQuestion.setDepQuesResp(null);
          break;
        }
      }
      if (this.selDepQuesResp.contains(ALL_CONST)) {
        String depResponse = this.selDepQuesResp.substring(0, this.selDepQuesResp.indexOf(RESULT_SEPARATOR));
        CDRConstants.QS_ASSESMENT_TYPE assesmentType = CDRConstants.QS_ASSESMENT_TYPE.getTypeByUiText(depResponse);
        newQuestion.setDepQuesResp(assesmentType.getDbType());
        newQuestion.setDepQResultOptId(null);
      }
    }
    else {
      newQuestion.setDepQuesId(null);
      newQuestion.setDepQuesResp(null);
      newQuestion.setDepQResultOptId(null);

    }
  }


  /**
   * @param updateModel
   * @return
   */
  private boolean setFieldsForUpdate(final QuestionUpdationData updateModel) {
    Question newQuestion = this.question;
    // For saving deleted flag
    newQuestion.setDeletedFlag(this.deletedChkBox.getSelection());
    newQuestion.setResultRelevantFlag(this.resultRelevantChkBox.getSelection());
    Long qNumber;
    updateModel.setQuestion(newQuestion);
    // set the flag for level change and change in ques no. within same parent
    updateModel.setLevelChange(this.isLevelChange);
    updateModel.setReorderWithinParent(this.isReorderWithinParent);
    if (this.isLevelChange) {
      // store the current level of question before changing the level of selected question
      updateModel.setOldQuesLevel(this.qnaireDefBo.getQuestionLevel(newQuestion.getId()));
      // set level to be updated as selected in combo box
      updateModel.setNewLevel(getSelectedQuestionLevel());
      Question levelChgParent = getNewParentForLevelChange();
      updateModel.setNewParent(levelChgParent);
      String qsSel = this.numberCombo.getText();
      if (qsSel.contains(".")) {
        qNumber = Long.valueOf(qsSel.substring(qsSel.lastIndexOf('.') + 1, qsSel.length()));
      }
      else {
        qNumber = Long.valueOf(qsSel);
      }
      // set the question number to be updated from the number combo box
      updateModel.setNewQuesNo(qNumber);
    }
    else {
      int lastIndexOfParentQNo = 0;
      if (this.question.getParentQId() != null) {
        lastIndexOfParentQNo = this.qnaireDefBo.getQuestionNumber(this.question.getParentQId()).length() + 1;
      }
      qNumber = Long.valueOf(this.numberCombo.getText().substring(lastIndexOfParentQNo));
      // set selected question's parent question as new parent when there is no level change
      updateModel.setNewParent(this.qnaireDefBo.getQuestion(this.question.getParentQId()));
      // set the question number to be updated from the number combo box
      updateModel.setNewQuesNo(qNumber);
    }
    setQDepFieldForQuestion(newQuestion);
    setNameDesc(newQuestion);
    newQuestion.setHeadingFlag(this.headingChkBox.getSelection());

    // if it is not a heading, store the configuration
    updateModel.setQuestionConfig(createConfig());

    addListTobeDeleted(updateModel);
    // set the result options to be updated (add,deleted,update)
    addResultOptionsToBeUpdated(updateModel);

    if (validateAttrDepnCombinations()) {
      createAttrAndValDep(null, updateModel);
    }
    else {
      MessageDialogUtils.getErrorMessageDialog("Validation failed !",
          "The attribute value configuration is not complete. Please add values for all the attributes then continue to save.");
      return false;
    }
    if (!this.editQuesValCombList.isEmpty()) {
      createEditList(updateModel);
    }
    return true;
  }


  /**
   * @param updateModel
   */
  private void addResultOptionsToBeUpdated(final QuestionUpdationData updateModel) {
    updateModel.setQnaireResultOptionsToBeAdd(this.resultOptionsToBeAdded);
    updateModel.setQnaireResultOptionsToBeEdit(getResultOptionsToBeEdited());
    updateModel.setQnaireResultOptionsToBeDelete(this.resultOptionsToBeDeleted);
  }

  /**
   * @param newQuestion
   */
  private void setNameDesc(final Question newQuestion) {
    newQuestion.setQNameEng(this.qsEngText.getText());
    newQuestion.setQNameGer(this.qsGerText.getText());
    newQuestion.setQHintEng(this.qsHintEngText.getText());
    newQuestion.setQHintGer(this.qsHintGerText.getText());
  }

  /**
   * @param newQuestionConfig
   */
  private QuestionConfig createConfig() {
    QuestionConfig newQuestionConfig;
    if (null != this.question.getQuestionConfigId()) {
      newQuestionConfig = this.qnaireDefBo.getQnaireDefModel().getQuestionConfigMap().get(this.question.getId());
    }
    else {
      newQuestionConfig = new QuestionConfig();
    }
    String selResult = this.comboResult.getItem(this.comboResult.getSelectionIndex());
    String selOP = this.comboOP.getItem(this.comboOP.getSelectionIndex());
    String selMeasurement = this.comboMeasurement.getItem(this.comboMeasurement.getSelectionIndex());
    String selRemark = this.comboRemark.getItem(this.comboRemark.getSelectionIndex());
    String selSeries = this.comboSeries.getItem(this.comboSeries.getSelectionIndex());
    String selLink = this.comboLink.getItem(this.comboLink.getSelectionIndex());
    String selMeasures = this.comboMeasures.getItem(this.comboMeasures.getSelectionIndex());
    String selResponsible = this.comboResponsible.getItem(this.comboResponsible.getSelectionIndex());
    String selDate = this.comboDate.getItem(this.comboDate.getSelectionIndex());
    newQuestionConfig.setResult(QUESTION_CONFIG_TYPE.getDbType(selResult).getDbType());
    newQuestionConfig.setOpenPoints(QUESTION_CONFIG_TYPE.getDbType(selOP).getDbType());
    newQuestionConfig.setMeasurement(QUESTION_CONFIG_TYPE.getDbType(selMeasurement).getDbType());
    newQuestionConfig.setRemark(QUESTION_CONFIG_TYPE.getDbType(selRemark).getDbType());
    newQuestionConfig.setSeries(QUESTION_CONFIG_TYPE.getDbType(selSeries).getDbType());
    newQuestionConfig.setLink(QUESTION_CONFIG_TYPE.getDbType(selLink).getDbType());
    newQuestionConfig.setMeasure(QUESTION_CONFIG_TYPE.getDbType(selMeasures).getDbType());
    newQuestionConfig.setResponsible(QUESTION_CONFIG_TYPE.getDbType(selResponsible).getDbType());
    newQuestionConfig.setCompletionDate(QUESTION_CONFIG_TYPE.getDbType(selDate).getDbType());
    return newQuestionConfig;
  }

  /**
   * @param levelChgParent
   * @return
   */
  private Question getNewParentForLevelChange() {
    Question levelChgParent = null;
    // moving qs downwards say from level 1 to level 2 or from level 2 to level 3 ( previous qs will be the new parent)
    if (CommonUtils.isNotNull(this.qnaireDefBo.getPreviousQuestion(this.question.getId())) &&
        ((getSelectedQuestionLevel() == ApicConstants.LEVEL_2) ||
            (getSelectedQuestionLevel() == ApicConstants.LEVEL_3))) {
      levelChgParent = this.qnaireDefBo.getPreviousQuestion(this.question.getId());
    }
    else {
      // moving qs at level 3 to level 2,if selected question level is 1 then simply return null as new parent
      if (CommonUtils.isNotNull(
          this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(this.question.getParentQId()).getParentQId()) &&
          (getSelectedQuestionLevel() == ApicConstants.LEVEL_2)) {
        levelChgParent = this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(
            this.qnaireDefBo.getQnaireDefModel().getQuestionMap().get(this.question.getParentQId()).getParentQId());
      }
    }
    return levelChgParent;
  }

  /**
   * @param command
   */
  private void addListTobeDeleted(final QuestionUpdationData updateModel) {
    Map<Long, QuestionDepenAttr> questionDepenAttrMap = this.qnaireDefBo.getQuestionDepenAttr(this.question.getId());
    if (!QuestionDialog.this.listAttrToBeDel.isEmpty()) {
      for (QuesDepnAttributeRow attrRow : QuestionDialog.this.listAttrToBeDel) {
        if (questionDepenAttrMap.containsKey(attrRow.getAttribute().getId())) {
          updateModel.getqDepAttrToDelete().add(questionDepenAttrMap.get(attrRow.getAttribute().getId()).getId());
        }
      }
    }
    if (!this.listAttrValToBeDel.isEmpty()) {
      subOfAddListToBeDeleted(updateModel, questionDepenAttrMap);
    }
  }

  /**
   * @param updateModel
   * @param questionDepenAttrMap
   */
  private void subOfAddListToBeDeleted(final QuestionUpdationData updateModel,
      Map<Long, QuestionDepenAttr> questionDepenAttrMap) {
    for (QuestDepenValCombination valComb : this.listAttrValToBeDel) {
      for (QuestionDepenAttrValue qDepVal : valComb.getQuesAttrValMap().values()) {
        for (QuestionDepenAttr qDepAttr : questionDepenAttrMap.values()) {
          if (qDepVal.getQAttrDepId().equals(qDepAttr.getAttrId())) {
            String id = String.valueOf(this.question.getId()) + String.valueOf(qDepAttr.getAttrId()) +
                String.valueOf(valComb.getCombinationId());
            updateModel.getqDepValToDelete().put(id, qDepVal);
          }
        }
      }
    }
  }


  private void createEditList(final QuestionUpdationData updateModel) {
    Map<Long, QuestionDepenAttr> questionDepenAttrMap = this.qnaireDefBo.getQuestionDepenAttr(this.question.getId());
    QuestAttrAndValDepModel questAttrAndValDepModel =
        this.qnaireDefBo.getQuestDependentAttrAndValModel(this.question.getId());

    Map<Attribute, QuestionDepenAttr> qDepAttrMapRef = new HashMap<>();
    for (QuestionDepenAttr qDepAttr : questionDepenAttrMap.values()) {
      qDepAttrMapRef.put(questAttrAndValDepModel.getAttributeMap().get(qDepAttr.getAttrId()), qDepAttr);
    }
    for (QuestDepenValCombination valComb : this.editQuesValCombList) {

      Set<Attribute> attrSet = new HashSet<>();

      for (QuestionDepenAttr qDepAttr : questionDepenAttrMap.values()) {
        attrSet.add(questAttrAndValDepModel.getAttributeMap().get(qDepAttr.getAttrId()));
      }
      Set<Attribute> diffInAttrSet = CommonUtils.getDifference(valComb.getAttrValMap().keySet(), attrSet);
      for (Attribute attr : diffInAttrSet) {
        AttributeValue attributeValue = valComb.getAttrValMap().get(attr);
        QuestionDepenAttrValue questionDepenAttrValue = new QuestionDepenAttrValue();
        questionDepenAttrValue.setValueId(attributeValue.getId());
        if (qDepAttrMapRef.get(attr) != null) {
          questionDepenAttrValue.setQAttrDepId(qDepAttrMapRef.get(attr).getId());
        }
        questionDepenAttrValue.setQCombiNum(valComb.getCombinationId());
        String id = String.valueOf(this.question.getId()) + String.valueOf(attr.getId()) +
            String.valueOf(valComb.getCombinationId());
        updateModel.getqDepValToAdd().put(id, questionDepenAttrValue);
      }

      Set<Attribute> existingAttrSet = CommonUtils.getDifference(valComb.getAttrValMap().keySet(), diffInAttrSet);
      for (Attribute attr : existingAttrSet) {
        AttributeValue attributeValue = valComb.getAttrValMap().get(attr);
        QuestionDepenAttrValue questionDepenAttrValue = valComb.getQuesAttrValMap().get(qDepAttrMapRef.get(attr));
        questionDepenAttrValue.setValueId(attributeValue.getId());
        String id = String.valueOf(this.question.getId()) + String.valueOf(attr.getId()) +
            questionDepenAttrValue.getQCombiNum();
        updateModel.getqDepValToEdit().put(id, questionDepenAttrValue);
      }
    }
  }

  /**
   * @param listofAttrs
   * @param listComb
   */
  private boolean validateAttrDepnCombinations() {
    boolean validationThru = true;
    if ((this.attrTab.getGrid().getItems().length != 0) &&
        (this.attrsValueTableViewer.getGrid().getItems().length == 0)) {
      validationThru = false;
    }

    for (GridItem attrItem : this.attrTab.getGrid().getItems()) {
      QuesDepnAttributeRow attr = (QuesDepnAttributeRow) attrItem.getData();
      for (GridItem combItem : this.attrsValueTableViewer.getGrid().getItems()) {
        QuestDepenValCombination quesDepnValCombination = (QuestDepenValCombination) combItem.getData();
        Map<Attribute, AttributeValue> attrValMap = quesDepnValCombination.getAttrValMap();
        if (!attrValMap.containsKey(attr.getAttribute())) {
          validationThru = false;
          break;
        }
      }
    }
    return validationThru;

  }

  /**
   * @return the attrTab
   */
  public GridTableViewer getAttrTab() {
    return this.attrTab;
  }


  /**
   * @return the attrsValueTableViewer
   */
  public GridTableViewer getAttrsValueTableViewer() {
    return this.attrsValueTableViewer;
  }


  /**
   * @return the selectedQuestion
   */
  public Question getSelectedQuestion() {
    return this.question;
  }


  /**
   * @return the isUpdate
   */
  public boolean isUpdate() {
    return this.update;
  }


  /**
   * @return the editQuesValCombList
   */
  public List<QuestDepenValCombination> getEditQuesValCombList() {
    return this.editQuesValCombList;
  }

  /**
   * @return the resultsModelList
   */
  public List<QuestionResultOptionsModel> getResultsModelList() {
    return this.resultsModelList;
  }

  /**
   * @return the resultOptionsToBeEdited
   */
  List<QuestionResultOptionsModel> getResultOptionsToBeEdited() {
    return this.resultOptionsToBeEdited;
  }


}