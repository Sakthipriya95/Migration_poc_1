/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.actions.CdrActionSet;
import com.bosch.caltool.cdr.ui.sorters.FunctionGridTabViewerSorter;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.sorters.RuleSetTabSorter;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewActionSet;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ruleseditor.table.filters.CDRFunctionFilters;
import com.bosch.caltool.icdm.ruleseditor.table.filters.RuleSetFilter;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FunctionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.QuestionnaireServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextUtil;

/**
 * This class provides a dialog to select a function.
 */
public class FunctionSelectionDialog extends TitleAreaDialog {


  /** Width of main dialog box. */
  private static final int DIALOG_WIDTH = 670;

  /** Height of main dialog box. */
  private static final int DIALOG_HEIGHT = 500;

  /** The Constant QUESTIONNARIES. */
  private static final String QUESTIONNARIES = "Review Questionnaire";

  /** The Constant RULE_SET_TAB. */
  private static final String RULE_SET_TAB = "Rule Set";

  /** The Constant SEARCH_FUNCTIONS. */
  private static final String SEARCH_FUNCTIONS = "Search Functions";

  /** The Constant EDITABLE_FUNCTIONS. */
  private static final String EDITABLE_FUNCTIONS = "Editable Functions";

  /** The form toolkit. */
  private FormToolkit formToolkit;

  /** The func list tab viewer. */
  private GridTableViewer funcListTabViewer;

  /** The filter txt. */
  private Text filterTxt;

  /** The fc filter. */
  private CDRFunctionFilters fcFilter;

  /**
   * The fc filter for search functions tab This is added to avoid conflicts while filtering
   **/
  private CDRFunctionFilters funSearchFilter;

  /** The section one. */
  private Section sectionOne;

  /** The form one. */
  private Form formOne;

  /** The form three. */
  private Form formThree;

  /** The folder. */
  private TabFolder folder;

  /** The ok btn. */
  private Button okBtn;

  /**
   * Gets the ok btn.
   *
   * @return the okBtn
   */
  public Button getOkBtn() {
    return this.okBtn;
  }


  /** The section two. */
  private Section sectionTwo;

  /** The section three. */
  private Section sectionThree;

  /** The form two. */
  private Form formTwo;

  /** The srch list tab viewer. */
  private GridTableViewer srchListTabViewer;

  /** The search txt. */
  private Text searchTxt;

  /** The selected text. */
  private String selectedText;

  /** The search button. */
  private Button searchButton;

  /** The sel functions. */
  // ICDM-853
  private final List<Function> selFunctions = new ArrayList<>();

  /** The funclist. */
  private final List<Function> funclist = new ArrayList<>();

  /** The selected param. */
  private final String selectedParam;

  /** rule Set text. */
  private Text searchRuleSetTxt;

  /** Rule set tab Viewer. */
  private GridTableViewer ruleSetTabViewer;

  /** Rule Set filter. */
  private RuleSetFilter ruleFilter;

  /** Selected rulesets. */
  private final List<RuleSet> selRuleSets = new ArrayList<>();

  /** The ques add button. */
  private Button quesAddButton;

  /** selected Ques. */
  private final List<Questionnaire> selectedQues = new ArrayList<>();

  /** The section. */
  private QuestionareTableSection section;

  private final ParamCollectionDataProvider dataProvider = new ParamCollectionDataProvider();

  protected SortedSet<RuleSet> allRuleSets;

  private SortedSet<Questionnaire> allQnaireSet;

  /**
   * qnaire where the curUser has WriteAccess
   */
  private SortedSet<Questionnaire> qnaireWithWriteAccess;

  private Button showRuleSetsWithWriteAccess;

  private SortedSet<RuleSet> ruleSetsWithWriteAccess;


  /**
   * Gets the selected ques.
   *
   * @return the selectedQues
   */
  public List<Questionnaire> getSelectedQues() {
    return this.selectedQues;
  }


  /** Rule set table width. */
  private static final int RULESET_TAB_WIDTH = 450;


  /**
   * The parameterized constructor.
   *
   * @param parentShell instance
   * @param funcList funcList
   * @param selectedParam selected parameter
   */
  public FunctionSelectionDialog(final Shell parentShell, final List<Function> funcList, final String selectedParam) {

    super(parentShell);
    if (null != funcList) {
      this.funclist.addAll(funcList);
    }
    this.selectedParam = selectedParam;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Title modified
    newShell.setText("Select Function/Ruleset/Questionnarie");
    // Width modified
    int frameX = newShell.getSize().x - newShell.getClientArea().width;
    int frameY = newShell.getSize().y - newShell.getClientArea().height;
    newShell.setSize(DIALOG_WIDTH + frameX, DIALOG_HEIGHT + frameY);
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
   * Creates the dialog's contents.
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {

    final Composite composite = new Composite(parent, SWT.None);
    initializeDialogUnits(composite);

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    parent.setLayout(gridLayout);
    parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    composite.setLayout(gridLayout);
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.folder = new TabFolder(composite, SWT.NONE);
    this.folder.setLayout(gridLayout);
    this.folder.setLayoutData(GridDataUtil.getInstance().getGridData());

    // Tab 1
    final TabItem tab1 = new TabItem(this.folder, SWT.NONE);
    tab1.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNCTION_28X30));
    tab1.setText(EDITABLE_FUNCTIONS);
    createSectionOne(getFormToolkit(), gridLayout);
    tab1.setControl(this.sectionOne);


    if (this.funclist.isEmpty()) {
      // Tab 2
      final TabItem tab2 = new TabItem(this.folder, SWT.NONE);
      tab2.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNC_SEARCH_28X30));
      tab2.setText(SEARCH_FUNCTIONS);
      createSectionTwo(getFormToolkit(), gridLayout);
      tab2.setControl(this.sectionTwo);

      final TabItem tab3 = new TabItem(this.folder, SWT.NONE);
      tab3.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.RULE_SET_16X16));
      tab3.setText(RULE_SET_TAB);
      createSectionThree(getFormToolkit(), gridLayout);
      tab3.setControl(this.sectionThree);


      final TabItem tab4 = new TabItem(this.folder, SWT.NONE);
      tab4.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.QUESTION_RESPONSE_ICON_16X16));
      tab4.setText(QUESTIONNARIES);
      this.section = new QuestionareTableSection(this.folder, this);
      this.section.createSectionThree(getFormToolkit(), gridLayout);
      tab4.setControl(this.section.getQuestionTabSec());
      this.section.getShowQnaireWithWriteAccess().addSelectionListener(new SelectionAdapter() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(final SelectionEvent e) {
          boolean selection = FunctionSelectionDialog.this.section.getShowQnaireWithWriteAccess().getSelection();
          if (selection) {
            FunctionSelectionDialog.this.section.getQuestabViewer()
                .setInput(FunctionSelectionDialog.this.qnaireWithWriteAccess);
          }
          else {
            FunctionSelectionDialog.this.section.getQuestabViewer().setInput(FunctionSelectionDialog.this.allQnaireSet);
          }
        }

      });

    }


    this.folder.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        FunctionSelectionDialog thisDialog = FunctionSelectionDialog.this;
        thisDialog.quesAddButton.setVisible(false);
        if (SEARCH_FUNCTIONS.equals(thisDialog.folder.getSelection()[0].getText())) {
          thisDialog.searchTxt.setFocus();
        }
        else if (EDITABLE_FUNCTIONS.equals(thisDialog.folder.getSelection()[0].getText())) {
          thisDialog.filterTxt.setFocus();
        }
        else if (QUESTIONNARIES.equals(thisDialog.folder.getSelection()[0].getText())) {
          if ((thisDialog.allQnaireSet == null) && (thisDialog.qnaireWithWriteAccess == null)) {
            fetchAndSetQuestionnaires();
          }
          thisDialog.section.getFilterTxt().setFocus();
          thisDialog.quesAddButton.setVisible(true);
        }
        else if (RULE_SET_TAB.equals(thisDialog.folder.getSelection()[0].getText())) {
          if (CommonUtils.isNullOrEmpty(thisDialog.allRuleSets) &&
              CommonUtils.isNullOrEmpty(thisDialog.ruleSetsWithWriteAccess)) {
            fetchAndSetRuleSets(thisDialog);
          }
          thisDialog.searchRuleSetTxt.setFocus();
        }
      }


    });

    createButtonBar(parent);
    return composite;
  }

  private void fetchAndSetRuleSets(final FunctionSelectionDialog thisDialog) {
    Display.getCurrent().asyncExec(() -> {
      try {
        thisDialog.allRuleSets = new RuleSetServiceClient().getAllRuleSets();

        getRuleSetsWithWriteAccess();
        thisDialog.ruleSetTabViewer.setInput(FunctionSelectionDialog.this.ruleSetsWithWriteAccess);

      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e);
      }
    });
  }

  /**
   * @return Comparator
   */
  public Comparator<Questionnaire> getQnaireComparator() {
    return (final Questionnaire obj1, final Questionnaire obj2) -> {
      int compResult = ApicUtil.compare(obj1.getName(), obj2.getName());
      if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
        return ApicUtil.compare(obj1.getId(), obj2.getId());
      }
      return compResult;
    };
  }

  private void fetchAndSetQuestionnaires() {
    SortedSet<Questionnaire> sortedSetOfQnaire = new TreeSet<>(getQnaireComparator());
    Display.getCurrent().asyncExec(() -> {
      try {
        this.allQnaireSet = new TreeSet<>(new QuestionnaireServiceClient().getAll(false, true).values());
        sortedSetOfQnaire.addAll(this.allQnaireSet);

        getQnaireWithWriteAccess();
        this.section.getQuestabViewer().setInput(this.qnaireWithWriteAccess);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e);
      }
    });
  }

  private void getRuleSetsWithWriteAccess() {
    try {
      CurrentUserBO curUserBo = new CurrentUserBO();
      this.ruleSetsWithWriteAccess = new TreeSet<>();
      for (RuleSet ruleSet : this.allRuleSets) {
        if (curUserBo.hasNodeWriteAccess(ruleSet.getId())) {
          this.ruleSetsWithWriteAccess.add(ruleSet);

        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, com.bosch.caltool.cdr.ui.Activator.PLUGIN_ID);
    }
  }

  private void getQnaireWithWriteAccess() {
    try {
      CurrentUserBO curUserBo = new CurrentUserBO();
      this.qnaireWithWriteAccess = new TreeSet<>();
      for (Questionnaire qnaire : this.allQnaireSet) {
        if (curUserBo.hasNodeWriteAccess(qnaire.getId())) {
          this.qnaireWithWriteAccess.add(qnaire);

        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, com.bosch.caltool.cdr.ui.Activator.PLUGIN_ID);
    }
  }

  /**
   * Creates the section three.
   *
   * @param toolkit the toolkit
   * @param gridLayout the grid layout
   */
  private void createSectionThree(final FormToolkit toolkit, final GridLayout gridLayout) {
    this.sectionThree = toolkit.createSection(this.folder, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionThree.setText("Select Rule Set");
    this.sectionThree.setDescription("Select Rule Set to open the editor");
    this.sectionThree.setExpanded(true);
    this.sectionThree.getDescriptionControl().setEnabled(false);
    this.sectionThree.setLayout(gridLayout);
    this.sectionThree.setLayoutData(getGridDataWithOutGrabExcessVSpace());

    createFormThree(toolkit);

    this.sectionThree.setClient(this.formThree);

  }

  /**
   * Creates the form three.
   *
   * @param toolkit the toolkit
   */
  private void createFormThree(final FormToolkit toolkit) {
    this.formThree = toolkit.createForm(this.sectionThree);
    final GridLayout gridLayout = new GridLayout();
    this.formThree.getBody().setLayout(gridLayout);
    this.formThree.getBody().setLayoutData(getGridDataWithOutGrabExcessVSpace());
    // check box to show rule sets with write access
    this.showRuleSetsWithWriteAccess = new Button(this.formThree.getBody(), SWT.CHECK);
    this.showRuleSetsWithWriteAccess.setText("Show Rule sets with 'Write' access");
    this.showRuleSetsWithWriteAccess.setSelection(true);

    this.showRuleSetsWithWriteAccess.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        boolean selection = FunctionSelectionDialog.this.showRuleSetsWithWriteAccess.getSelection();
        if (selection) {
          FunctionSelectionDialog.this.ruleSetTabViewer.setInput(FunctionSelectionDialog.this.ruleSetsWithWriteAccess);
        }
        else {
          FunctionSelectionDialog.this.ruleSetTabViewer.setInput(FunctionSelectionDialog.this.allRuleSets);
        }
      }

    });

    this.ruleFilter = new RuleSetFilter();
    createRuleSetFilter();
    // Icdm-1368 - new table for Rule set
    this.ruleSetTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.formThree.getBody(),
        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, GridDataUtil.getInstance().getGridData());

    this.ruleSetTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    final GridViewerColumn ruleSetName = new GridViewerColumn(this.ruleSetTabViewer, SWT.NONE);
    ruleSetName.getColumn().setText("Rule Set Name");
    ruleSetName.getColumn().setWidth(RULESET_TAB_WIDTH);
    ColumnViewerToolTipSupport.enableFor(this.ruleSetTabViewer, ToolTip.NO_RECREATE);
    ruleSetName.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final ParamCollection item = (ParamCollection) element;
        return item.getName();
      }

      // ICDM-1368
      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        final ParamCollection item = (ParamCollection) element;
        return FunctionSelectionDialog.this.dataProvider.getToolTip(item);

      }
    });

    this.ruleSetTabViewer.addFilter(this.ruleFilter);

    // sorter for ruleset
    RuleSetTabSorter ruleSetSorter = new RuleSetTabSorter();
    this.ruleSetTabViewer.setComparator(ruleSetSorter);

    addRulesTableSelListener(this.ruleSetTabViewer);
    addDoubleClickListener(this.ruleSetTabViewer);

    ruleSetName.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(ruleSetName.getColumn(), 0, ruleSetSorter, this.ruleSetTabViewer));
  }

  /**
   * create filter for Rule set.
   */
  private void createRuleSetFilter() {

    this.searchRuleSetTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.formThree.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "");

    this.searchRuleSetTxt.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {

        final String text = FunctionSelectionDialog.this.searchRuleSetTxt.getText().trim();
        FunctionSelectionDialog.this.ruleFilter.setFilterText(text);
        FunctionSelectionDialog.this.ruleSetTabViewer.refresh();

      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    createQnaire(parent, "Create Questionnaire", false);
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, "Open", false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }


  /**
   * Creates the ques button.
   *
   * @param parent the parent
   * @param label the label
   * @param enabled the enabled
   */
  private void createQnaire(final Composite parent, final String label, final boolean enabled) {
    ((GridLayout) parent.getLayout()).numColumns++;
    this.quesAddButton = new Button(parent, SWT.PUSH);
    this.quesAddButton.setText(label);
    this.quesAddButton.setFont(JFaceResources.getDialogFont());
    this.quesAddButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        AddNewQnaireDialog dialog =
            new AddNewQnaireDialog(Display.getCurrent().getActiveShell(), FunctionSelectionDialog.this);
        dialog.open();
      }
    });
    if (enabled) {
      Shell shell = parent.getShell();
      if (shell != null) {
        shell.setDefaultButton(this.quesAddButton);
      }
    }
    setButtonLayoutData(this.quesAddButton);
    this.quesAddButton.setVisible(false);
    this.quesAddButton.setEnabled(false);
    try {
      if (new CurrentUserBO().hasApicWriteAccess()) {
        this.quesAddButton.setEnabled(true);
      }
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }
  }

  /**
   * Creates the section one.
   *
   * @param toolkit This method initializes sectionOne
   * @param gridLayout the grid layout
   */
  private void createSectionOne(final FormToolkit toolkit, final GridLayout gridLayout) {

    this.sectionOne = toolkit.createSection(this.folder, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionOne.setText("Functions");
    this.sectionOne.setDescription("List of modifiable functions");
    this.sectionOne.setExpanded(true);
    this.sectionOne.getDescriptionControl().setEnabled(false);
    createFormOne(toolkit, gridLayout);
    this.sectionOne.setLayoutData(getGridDataWithOutGrabExcessVSpace());
    this.sectionOne.setClient(this.formOne);

  }

  /**
   * This method initializes formOne.
   *
   * @param toolkit the toolkit
   * @param gridLayout the grid layout
   */
  private void createFormOne(final FormToolkit toolkit, final GridLayout gridLayout) {
    this.formOne = toolkit.createForm(this.sectionOne);
    this.formOne.setLayoutData(getGridDataWithOutGrabExcessVSpace());
    this.fcFilter = new CDRFunctionFilters();


    createFilterTxt();
    // ICDM-853
    this.funcListTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.formOne.getBody(),
        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, GridDataUtil.getInstance().getGridData());

    this.funcListTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    final GridViewerColumn funcName = new GridViewerColumn(this.funcListTabViewer, SWT.NONE);
    funcName.getColumn().setText("Function Name");
    funcName.getColumn().setWidth(450);
    funcName.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final Function item = (Function) element;
        return item.getName();
      }
    });
    if (CommonUtils.isNullOrEmpty(this.funclist)) {
      try {
        FunctionServiceClient client = new FunctionServiceClient();
        SortedSet<Function> paramColSet = client.getAllUserFunctions();
        this.funcListTabViewer.setInput(paramColSet);
      }

      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }

    }
    else {
      this.funcListTabViewer.setInput(this.funclist);
    }
    this.funcListTabViewer.addFilter(this.fcFilter);

    addTableSelectionListener(this.funcListTabViewer);
    // adding double click listener
    addDoubleClickListener(this.funcListTabViewer);

    AbstractViewerSorter functionSorter = new FunctionGridTabViewerSorter();
    this.funcListTabViewer.setComparator(functionSorter);
    // Add column selection listener
    funcName.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(funcName.getColumn(), 0, functionSorter, this.funcListTabViewer));

    this.formOne.getBody().setLayout(gridLayout);


  }

  /**
   * ICDM 574-This method defines the activities to be performed when double clicked on the table.
   *
   * @param tableviewer tableviewer
   */
  protected void addDoubleClickListener(final GridTableViewer tableviewer) {
    tableviewer.addDoubleClickListener(doubleclickevent -> Display.getDefault().asyncExec(this::okPressed));
  }


  /**
   * This method adds selection listener to the TableViewer.
   *
   * @param tableviewer the tableviewer
   */
  private void addTableSelectionListener(final GridTableViewer tableviewer) {
    tableviewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) tableviewer.getSelection();
        // ICDM-853
        if ((CommonUtils.isNotNull(selection)) && (CommonUtils.isNotEmpty(selection.toArray()))) {
          FunctionSelectionDialog.this.selFunctions.clear();
          @SuppressWarnings("unchecked")
          final Iterator<Function> funcs = selection.iterator();
          while (funcs.hasNext()) {
            final Function func = funcs.next();
            FunctionSelectionDialog.this.selFunctions.add(func);
            FunctionSelectionDialog.this.okBtn.setEnabled(true);
          }

        }
        else {
          FunctionSelectionDialog.this.okBtn.setEnabled(false);
          FunctionSelectionDialog.this.selFunctions.clear();
        }
      }
    });
  }

  /**
   * This method adds selection listener to the Rule TableViewer.
   *
   * @param tableviewer the tableviewer
   */
  private void addRulesTableSelListener(final GridTableViewer tableviewer) {
    tableviewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) tableviewer.getSelection();
        // ICDM-853
        if ((CommonUtils.isNotNull(selection)) && (!selection.isEmpty())) {
          FunctionSelectionDialog.this.selRuleSets.clear();
          @SuppressWarnings("unchecked")
          final Iterator<RuleSet> ruleSets = selection.iterator();
          while (ruleSets.hasNext()) {
            final RuleSet ruleSet = ruleSets.next();
            FunctionSelectionDialog.this.selRuleSets.add(ruleSet);
            FunctionSelectionDialog.this.okBtn.setEnabled(true);
          }

        }
        else {
          FunctionSelectionDialog.this.okBtn.setEnabled(false);
          FunctionSelectionDialog.this.selRuleSets.clear();
        }
      }
    });
  }

  /**
   * Creates the section two.
   *
   * @param toolkit This method initializes sectionOne
   * @param gridLayout the grid layout
   */
  private void createSectionTwo(final FormToolkit toolkit, final GridLayout gridLayout) {

    this.sectionTwo = toolkit.createSection(this.folder, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionTwo.setText(SEARCH_FUNCTIONS);
    this.sectionTwo.setDescription("Search functions starting with : ");
    this.sectionTwo.setExpanded(true);
    this.sectionTwo.getDescriptionControl().setEnabled(false);
    this.sectionTwo.setLayout(gridLayout);
    this.sectionTwo.setLayoutData(getGridDataWithOutGrabExcessVSpace());

    createFormTwo(toolkit);

    this.sectionTwo.setClient(this.formTwo);

  }


  /**
   * This method initializes formOne.
   *
   * @param toolkit the toolkit
   */
  private void createFormTwo(final FormToolkit toolkit) {

    this.formTwo = toolkit.createForm(this.sectionTwo);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.formTwo.getBody().setLayout(gridLayout);
    this.formTwo.getBody().setLayoutData(getGridDataWithOutGrabExcessVSpace());

    AbstractViewerSorter functionSorter = new FunctionGridTabViewerSorter();

    this.searchTxt = getFormToolkit().createText(this.formTwo.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.searchTxt.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(427));
    this.searchButton = new Button(this.formTwo.getBody(), SWT.PUSH);
    this.searchButton.setToolTipText("Search");
    this.searchButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNC_SEARCH_28X30));
    this.searchButton.setEnabled(false);
    this.funSearchFilter = new CDRFunctionFilters();
    this.searchTxt.addModifyListener(event -> {
      FunctionSelectionDialog.this.selectedText = FunctionSelectionDialog.this.searchTxt.getText().trim();
      FunctionSelectionDialog.this.funSearchFilter.setFilterText(FunctionSelectionDialog.this.selectedText);
      FunctionSelectionDialog.this.srchListTabViewer.refresh();

      FunctionSelectionDialog.this.searchButton.setEnabled(!FunctionSelectionDialog.this.selectedText.isEmpty());

    });

    enableEnterKey();


    this.searchButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final String searchText = FunctionSelectionDialog.this.selectedText;
        FunctionSelectionDialog.this.srchListTabViewer.getGrid().removeAll();
        try {
          FunctionServiceClient client = new FunctionServiceClient();
          Set<Function> funcSet = client.getSearchFunctions(searchText);
          if (funcSet.isEmpty()) {
            MessageDialog.openWarning(Display.getCurrent().getActiveShell(), " Info:", "No Results found !");
          }
          else {
            SortedSet<Function> funcSetSorted = new TreeSet<>();
            funcSetSorted.addAll(funcSet);
            FunctionSelectionDialog.this.srchListTabViewer.setInput(funcSetSorted);
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error("Error when fetching the functions : " + exp.getMessage(), exp);
        }
      }
    });
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.horizontalSpan = 2;
    // ICDM-853
    this.srchListTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.formTwo.getBody(),
        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, gridData);

    this.srchListTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.srchListTabViewer.addFilter(this.funSearchFilter);
    this.srchListTabViewer.setComparator(functionSorter);
    final GridViewerColumn funcName = new GridViewerColumn(this.srchListTabViewer, SWT.NONE);
    funcName.getColumn().setText("Function Name");
    funcName.getColumn().setWidth(450);
    funcName.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final ParamCollection item = (ParamCollection) element;
        return item.getName();
      }
    });

    addTableSelectionListener(this.srchListTabViewer);
    // adding double click listener
    addDoubleClickListener(this.srchListTabViewer);

    funcName.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(funcName.getColumn(), 0, functionSorter, this.srchListTabViewer));
  }

  /**
   * When Enter key is pressed the search button event is triggered.
   */
  private void enableEnterKey() {

    this.searchTxt.addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(final KeyEvent arg0) {
        if (arg0.character == SWT.CR) {
          FunctionSelectionDialog.this.searchButton.notifyListeners(SWT.Selection, new Event());
        }
      }
    });
  }

  /**
   * This method creates filter text.
   */
  private void createFilterTxt() {
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.formOne.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "");

    this.filterTxt.addModifyListener(event -> {
      final String text = FunctionSelectionDialog.this.filterTxt.getText().trim();
      FunctionSelectionDialog.this.fcFilter.setFilterText(text);
      FunctionSelectionDialog.this.funcListTabViewer.refresh();
    });

    this.filterTxt.setFocus();

  }

  /**
   * Gets the grid data with out grab excess V space.
   *
   * @return the grid data with out grab excess V space
   */
  private GridData getGridDataWithOutGrabExcessVSpace() {
    final GridData gridDataFour = new GridData();
    gridDataFour.horizontalAlignment = GridData.FILL;
    gridDataFour.grabExcessHorizontalSpace = true;
    gridDataFour.verticalAlignment = GridData.FILL;
    return gridDataFour;
  }

  /**
   * This method initializes formToolkit.
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
    ReviewActionSet paramActionSet = new ReviewActionSet();
    // ICDM-853
    for (Function selFunc : this.selFunctions) {
      if (selFunc != null) {
        try {
          selectionOfFuns(paramActionSet, selFunc);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }
    // ICDM-910
    for (RuleSet ruleSet : this.selRuleSets) {
      if (ruleSet != null) {
        try {
          paramActionSet.openRulesEditor(new ReviewParamEditorInput(ruleSet), null);
        }
        catch (PartInitException excep) {
          CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
        }
      }

    }

    for (Questionnaire ques : this.selectedQues) {
      CdrActionSet actionSet = new CdrActionSet();
      try {
        actionSet.openQuestionnaireEditor(new QuestionnaireServiceClient().getWorkingSet(ques.getId()));
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    super.okPressed();
  }

  /**
   * Selection of funs.
   *
   * @param paramActionSet the param action set
   * @param selFunc the sel func
   * @throws ApicWebServiceException
   */
  private void selectionOfFuns(final ReviewActionSet paramActionSet, final Function selFunc)
      throws ApicWebServiceException {
    ReviewParamEditorInput input;
    try {
      // Go the CDR perspective
      input = new ReviewParamEditorInput(selFunc);
      String baseParamName;
      Parameter cdrFuncParam = null;
      if ((this.selectedParam != null) && ApicUtil.isVariantCoded(this.selectedParam)) {
        baseParamName = ApicUtil.getBaseParamName(this.selectedParam);
        cdrFuncParam = new com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient()
            .getParameterOnlyByName(baseParamName);
        input.setCdrFuncParam(cdrFuncParam);
      }
      if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
        paramActionSet.openRulesEditor(input, null);
      }
    }
    catch (PartInitException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

  }


}