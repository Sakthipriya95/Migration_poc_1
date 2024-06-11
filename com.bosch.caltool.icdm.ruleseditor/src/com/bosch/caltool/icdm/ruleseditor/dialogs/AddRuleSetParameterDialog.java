/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.FunctionParamProperties;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.pages.DetailsPage;
import com.bosch.caltool.icdm.ruleseditor.pages.ListPage;
import com.bosch.caltool.icdm.ruleseditor.sorters.ParamFuncViewerSorter;
import com.bosch.caltool.icdm.ruleseditor.table.filters.ParamFuncViewerFilter;
import com.bosch.caltool.icdm.ws.rest.client.a2l.RuleSetParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * This class is to add new parameter to rule set
 *
 * @author mkl2cob
 */
public class AddRuleSetParameterDialog extends AbstractDialog {

  /**
   * sorting direction
   */
  private static final int DIRECTION_UP = 1;
  /**
   * constant for single selection
   */
  private static final int SELECTION_ONE = 1;

  /**
   * width of parameter type column
   */
  private static final int PARAM_TYPE_COL_WIDTH = 20;

  private static final String TITLE = "Add Parameter for Rule Set";

  private static final String FILTER_TEXT = "type filter text";

  /**
   * User name col width
   */
  private static final int USER_NAME_COL_WIDTH = 200;

  /**
   * table grid height
   */
  private static final int TABLE_GRID_HEIGHT = 200;

  /**
   * User id col Width
   */
  private static final int USER_ID_COL_WIDTH = 120;

  /**
   * Dep name col width
   */
  private static final int DEP_COL_WIDTH = 120;


  private final SearchParametersSection searchParamSection =
      new SearchParametersSection(new Decorators(), new Decorators());

  /**
   * Result Form
   */
  private Form resForm;
  /**
   * top composite
   */
  private Composite top;

  /**
   * composite
   */
  private Composite composite;

  /**
   * result section instance
   */
  private Section resSection;

  /**
   * editor instance
   */
  private final ReviewParamEditor reviewParamEditor;
  private final AbstractFormPage formPage;

  /**
   * sorting direction
   */
  private static final int DIRECTION_DOWN = 0;

  /**
   * @param parentShell parent shell
   * @param ruleSet RuleSet
   * @param customNATTable rule set parameter table viewer from details page
   * @param reviewParamEditor ReviewParamEditor
   * @param formPage
   */
  public AddRuleSetParameterDialog(final Shell parentShell, final RuleSet ruleSet,
      final ReviewParamEditor reviewParamEditor, final AbstractFormPage formPage) {
    super(parentShell);
    this.searchParamSection.setRuleSet(ruleSet);
    this.reviewParamEditor = reviewParamEditor;
    this.formPage = formPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(TITLE);

    // Set the message
    setMessage("Find parameters by providing search criteria. \nNote : Only first 100 results will be displayed");
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {

    newShell.setText("Search Parameters");
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);

    this.top.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.top.setLayoutData(gridData);
    createComposite();
    return this.top;
  }

  /**
   * @param top2
   */
  private void createComposite() {

    this.composite = getFormToolkit().createComposite(this.top);
    GridLayout gridLayout = new GridLayout();
    this.composite.setLayout(gridLayout);
    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.composite.setLayoutData(gridData);
    this.searchParamSection.createSearchSection(this.composite);
    createResultSection();

  }


  /**
   * create the Ldap Search result Section
   */
  private void createResultSection() {
    this.resSection =
        SectionUtil.getInstance().createSection(this.composite, this.searchParamSection.getFormToolkit(), "");
    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.resSection.setLayoutData(gridData);
    GridLayout gridLayout = new GridLayout();
    this.resSection.setLayout(gridLayout);
    createResForm();
    this.resSection.getDescriptionControl().setEnabled(false);
    this.resSection.setText("Search Results");
    this.resSection.setClient(this.resForm);
    createParamSearchResTable(this.resForm.getBody());
  }


  /**
   * create the Ldap Search result form
   */
  private void createResForm() {
    this.resForm = this.searchParamSection.getFormToolkit().createForm(this.resSection);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final GridLayout gridLayout1 = new GridLayout();
    this.resForm.getBody().setLayout(gridLayout1);
    this.resForm.getBody().setLayoutData(gridData);

  }


  /**
   * Create the Filter text
   */
  private void createFilterTxt() {
    this.searchParamSection.setParamTableFilter(new ParamFuncViewerFilter());
    this.searchParamSection.setFilterTxt(
        this.searchParamSection.getFormToolkit().createText(this.resForm.getBody(), null, SWT.SINGLE | SWT.BORDER));
    final GridData gridData = getFilterTxtGridData();
    this.searchParamSection.getFilterTxt().setLayoutData(gridData);
    this.searchParamSection.getFilterTxt().setMessage(FILTER_TEXT);

    this.searchParamSection.getFilterTxt().addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        final String text = AddRuleSetParameterDialog.this.searchParamSection.getFilterTxt().getText().trim();
        AddRuleSetParameterDialog.this.searchParamSection.getParamTableFilter().setFilterText(text);
        AddRuleSetParameterDialog.this.searchParamSection.getParamTabViewer().refresh();

      }
    });

  }

  /**
   * This method returns filter text GridData object
   *
   * @return GridData
   */
  private GridData getFilterTxtGridData() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }

  /**
   * @param comp Composite
   */
  private void createParamSearchResTable(final Composite comp) {

    createFilterTxt();
    this.searchParamSection.setParamTabViewer(GridTableViewerUtil.getInstance().createGridTableViewer(comp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        GridDataUtil.getInstance().getHeightHintGridData(TABLE_GRID_HEIGHT)));

    this.searchParamSection.getParamTabViewer().setContentProvider(ArrayContentProvider.getInstance());
    this.searchParamSection.getParamTabViewer().setInput(null);
    createGridViewerColumns();
    this.searchParamSection.getParamTabViewer().addFilter(this.searchParamSection.getParamTableFilter());
    createSelectionListener();
    invokeColumnSorter();
    addDoubleClickListener();
  }

  /**
   * Add sorter for the table columns
   */
  private void invokeColumnSorter() {
    this.searchParamSection.setRuleSetParamTabSorter(new ParamFuncViewerSorter());
    this.searchParamSection.getParamTabViewer().setComparator(this.searchParamSection.getRuleSetParamTabSorter());
  }

  /**
   * create selection listener for parameter search results table
   */
  private void createSelectionListener() {
    this.searchParamSection.getParamTabViewer().addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(final SelectionChangedEvent event) {
        if (CommonUtils.isNotNull(AddRuleSetParameterDialog.this.searchParamSection.getParamTabViewer().getInput())) {
          AddRuleSetParameterDialog.this.searchParamSection.getAddButton().setEnabled(true);
        }
      }
    });

  }


  /**
   * This method adds Columns to the addNewUserTableViewer
   */
  private void createGridViewerColumns() {
    createParamTypeColumn();
    createParamNameColumn();
    createParmDescColumn();
    createFuncNameColumn();
  }

  /**
   * column to display image of parameter type
   */
  private void createParamTypeColumn() {
    final GridViewerColumn paramTypeColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.searchParamSection.getParamTabViewer(), "", PARAM_TYPE_COL_WIDTH);
    // Add column selection listener
    paramTypeColumn.getColumn()
        .addSelectionListener(getSelectionAdapter(paramTypeColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_0));
    paramTypeColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return null;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Image getImage(final Object element) {
        Image img = null;
        if (element instanceof List<?>) {
          String paramType = (String) ((List<Object>) element).get(CommonUIConstants.COLUMN_INDEX_5);
          if (paramType.equalsIgnoreCase(ParameterType.MAP.getText())) {
            img = ImageManager.getInstance().getRegisteredImage(ImageKeys.MAP_16X16);
          }
          else if (paramType.equalsIgnoreCase(ParameterType.CURVE.getText())) {
            img = ImageManager.getInstance().getRegisteredImage(ImageKeys.CURVE_16X16);
          }
          else if (paramType.equalsIgnoreCase(ParameterType.VALUE.getText())) {
            img = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALUE_16X16);
          }
          else if (paramType.equalsIgnoreCase(ParameterType.ASCII.getText())) {
            img = ImageManager.getInstance().getRegisteredImage(ImageKeys.ASCII_16X16);
          }
          else if (paramType.equalsIgnoreCase(ParameterType.VAL_BLK.getText())) {
            img = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALBLK_16X16);
          }
          else if (paramType.equalsIgnoreCase(ParameterType.AXIS_PTS.getText())) {
            img = ImageManager.getInstance().getRegisteredImage(ImageKeys.AXIS_16X16);
          }

        }

        return img;
      }
    });

  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {

    this.searchParamSection.setAddButton(createButton(parent, IDialogConstants.OK_ID, "Add parameter", false));
    this.searchParamSection.getAddButton().setEnabled(false);

    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }


  /**
   * This method adds user name column to the addNewUserTableViewer
   */
  private void createParamNameColumn() {
    final GridViewerColumn paramNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.searchParamSection.getParamTabViewer(), "Parameter", USER_NAME_COL_WIDTH);
    // Add column selection listener
    paramNameColumn.getColumn()
        .addSelectionListener(getSelectionAdapter(paramNameColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_1));
    paramNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String parameterName = "";
        if (element instanceof FunctionParamProperties) {
          parameterName = ((FunctionParamProperties) element).getParamName();
        }
        return parameterName;
      }
    });
  }

  /**
   * This method adds user id column to the addNewUserTableViewer
   */
  private void createParmDescColumn() {
    final GridViewerColumn paramDescColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.searchParamSection.getParamTabViewer(), "Description", USER_ID_COL_WIDTH);
    // Add column selection listener
    paramDescColumn.getColumn()
        .addSelectionListener(getSelectionAdapter(paramDescColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_2));

    paramDescColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String parameterDesc = "";
        if (element instanceof FunctionParamProperties) {
          if (new CommonDataBO().getLanguage() == Language.ENGLISH) {
            parameterDesc = ((FunctionParamProperties) element).getParamLongName();
          }
          else {
            parameterDesc = ((FunctionParamProperties) element).getParamLongNameGer();
          }
        }
        return parameterDesc;
      }
    });


  }

  /**
   * This method adds department column to the addNewUserTableViewer
   */
  private void createFuncNameColumn() {
    final GridViewerColumn funcNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.searchParamSection.getParamTabViewer(), "Function", DEP_COL_WIDTH);
    // Add column selection listener
    funcNameColumn.getColumn()
        .addSelectionListener(getSelectionAdapter(funcNameColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_3));

    funcNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String funcName = "";
        if (element instanceof FunctionParamProperties) {
          funcName = ((FunctionParamProperties) element).getFunctionName();
        }
        return funcName;
      }
    });
  }

  /**
   * This method returns SelectionAdapter instance
   *
   * @param column
   * @param index
   * @return SelectionAdapter
   */
  private SelectionAdapter getSelectionAdapter(final GridColumn column, final int index) {
    return new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        AddRuleSetParameterDialog.this.searchParamSection.getRuleSetParamTabSorter().setColumn(index);
        int direction = AddRuleSetParameterDialog.this.searchParamSection.getRuleSetParamTabSorter().getDirection();
        for (int i = 0; i < AddRuleSetParameterDialog.this.searchParamSection.getParamTabViewer().getGrid()
            .getColumnCount(); i++) {
          if (i == index) {
            if (direction == DIRECTION_DOWN) {
              column.setSort(SWT.DOWN);
            }
            else if (direction == DIRECTION_UP) {
              column.setSort(SWT.UP);
            }
          }
          if (i != index) {
            AddRuleSetParameterDialog.this.searchParamSection.getParamTabViewer().getGrid().getColumn(i)
                .setSort(SWT.NONE);
          }
        }
        AddRuleSetParameterDialog.this.searchParamSection.getParamTabViewer().refresh();
      }
    };
  }

  /**
   * @return
   */
  private FormToolkit getFormToolkit() {
    if (this.searchParamSection.getFormToolkit() == null) {
      this.searchParamSection.setFormToolkit(new FormToolkit(Display.getCurrent()));
    }
    return this.searchParamSection.getFormToolkit();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    IStructuredSelection selection =
        (IStructuredSelection) AddRuleSetParameterDialog.this.searchParamSection.getParamTabViewer().getSelection();

    if (selection.size() > SELECTION_ONE) {
      addMultipleParameters(selection);
    }
    else {
      addSingleParameterToRuleSet(selection);
    }
    super.okPressed();
    refreshPage(this.formPage);

  }


  /**
   * @param abstractFormPage abstractFormPage
   */
  private void refreshPage(final AbstractFormPage abstractFormPage) {
    if (abstractFormPage instanceof ListPage) {
      ListPage page = (ListPage) abstractFormPage;
      page.refreshListPage();
      page.getParamTabSec().getParamTab().updateStatusBar(false);
    }
    if (abstractFormPage instanceof DetailsPage) {
      DetailsPage page = (DetailsPage) abstractFormPage;
      page.getEditor().refreshSelectedParamRuleData();
      page.getFcTableViewer().refresh();
      page.setStatusBarMessage(page.getFcTableViewer());
      ListPage listPage = page.getEditor().getListPage();
      listPage.refreshListPage();
    }
  }


  /**
   * add single parameter and set the focus to newly added parameter
   */
  private void addSingleParameterToRuleSet(final IStructuredSelection selection) {
    RuleSetParameter ruleSetParameter = new RuleSetParameter();
    RuleSetParameterServiceClient ruleSetParamServiceClient = new RuleSetParameterServiceClient();

    createRuleSetParamModel(selection, ruleSetParameter);
    try {
      RuleSetParameter createdRuleSetParam = ruleSetParamServiceClient.create(ruleSetParameter);
      this.reviewParamEditor.getEditorInput().getParamDataProvider().getParamRulesOutput().getParamMap()
          .put(createdRuleSetParam.getName(), createdRuleSetParam);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param selection
   * @param ruleSetParameter
   */
  private void createRuleSetParamModel(final IStructuredSelection selection, final RuleSetParameter ruleSetParameter) {
    FunctionParamProperties functionParamProperties = (FunctionParamProperties) selection.getFirstElement();
    ruleSetParameter.setName(functionParamProperties.getParamName());
    ruleSetParameter.setDescription(functionParamProperties.getParamLongName());
    ruleSetParameter.setLongName(functionParamProperties.getParamLongName());
    ruleSetParameter.setParamId(functionParamProperties.getParamId());
    ruleSetParameter.setFuncId(functionParamProperties.getFuncId());
    ruleSetParameter.setRuleSetId(this.searchParamSection.getRuleSet().getId());
  }

  /**
   * Add multiple parameters to Rule set
   *
   * @param selection IStructuredSelection
   */
  private void addMultipleParameters(final IStructuredSelection selection) {
    RuleSetParameterServiceClient ruleSetParamServiceClient = new RuleSetParameterServiceClient();
    Set<RuleSetParameter> ruleSetParamSet = new HashSet<RuleSetParameter>();
    Iterator<Object> iterator = selection.iterator();
    while (iterator.hasNext()) {
      FunctionParamProperties functionParamProperties = (FunctionParamProperties) iterator.next();
      RuleSetParameter ruleSetParameter = new RuleSetParameter();
      ruleSetParameter.setName(functionParamProperties.getParamName());
      ruleSetParameter.setDescription(functionParamProperties.getParamLongName());
      ruleSetParameter.setLongName(functionParamProperties.getParamLongName());
      ruleSetParameter.setParamId(functionParamProperties.getParamId());
      ruleSetParameter.setFuncId(functionParamProperties.getFuncId());
      ruleSetParameter.setRuleSetId(this.searchParamSection.getRuleSet().getId());
      ruleSetParamSet.add(ruleSetParameter);
    }
    try {
      Set<RuleSetParameter> createdRuleSetParam = ruleSetParamServiceClient.createMultiple(ruleSetParamSet);
      for (RuleSetParameter ruleSetParameter : createdRuleSetParam) {
        this.reviewParamEditor.getEditorInput().getParamDataProvider().getParamRulesOutput().getParamMap()
            .put(ruleSetParameter.getName(), ruleSetParameter);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * This method defines the activities to be performed when double clicked on the table
   */
  private void addDoubleClickListener() {
    this.searchParamSection.getParamTabViewer().addDoubleClickListener(new IDoubleClickListener() {

      /**
       * @param doubleclickevent event
       */
      @Override
      public void doubleClick(final DoubleClickEvent doubleclickevent) {
        Display.getDefault().asyncExec(new Runnable() {

          /**
           * {@inheritDoc}
           */
          @Override
          public void run() {
            okPressed();
          }
        });
      }

    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }
}
