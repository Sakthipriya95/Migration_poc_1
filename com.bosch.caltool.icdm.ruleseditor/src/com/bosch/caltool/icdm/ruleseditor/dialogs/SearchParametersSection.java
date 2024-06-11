/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.FunctionParamProperties;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.sorters.ParamFuncViewerSorter;
import com.bosch.caltool.icdm.ruleseditor.table.filters.ParamFuncViewerFilter;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.ui.validators.Validator;

/**
 * @author mkl2cob
 */
public class SearchParametersSection {


  /**
   * Search Dialog height
   */
  private static final int SEARCH_DIA_HEIGHT = 25;

  /**
   * Search Dialog width
   */
  private static final int SEARCH_DIA_WIDTH = 92;


  /**
   * search comp num of colums
   */
  private static final int SEARCH_COMP_COL = 4;

  /**
   * search button instance
   */
  private Button searchBtn;


  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * parameter results table viewer
   */
  private GridTableViewer paramTabViewer;

  /**
   * parameter table sorter
   */
  private ParamFuncViewerSorter ruleSetParamTabSorter;

  /**
   * Text for function naem
   */
  private Text functionName;

  /**
   * Text for parameter name
   */
  private Text paramName;

  /**
   * Search section
   */
  private Section searchSection;

  /**
   * Search Form
   */
  private Form searchForm;

  /**
   * Button to add parameter
   */
  private Button addButton;

  /**
   * type filter for parameter table
   */
  private Text filterTxt;

  /**
   * filter instance for parameter search table
   */
  private ParamFuncViewerFilter paramTableFilter;
  /**
   * clear button
   */
  private Button clearBtn;
  /**
   * Decorators instance
   */
  private final Decorators paramDecorators;

  /**
   * ControlDecoration for parameter name
   */
  private ControlDecoration paramNameDec;

  /**
   * ControlDecoration for function name
   */
  private ControlDecoration funcNameDec;

  /**
   * Decorators
   */
  private final Decorators funcDecorators;

  /**
   * RuleSet instance
   */
  private RuleSet ruleSet;

  /**
   * Parameter name
   */
  private String parameterName;
  /**
   * Function name
   */
  private String funcNameStr;

  /**
   * Constructor
   *
   * @param paramDecorators param Decorators
   * @param funcDecorators function Decorators
   */
  public SearchParametersSection(final Decorators paramDecorators, final Decorators funcDecorators) {
    // initailising decorators
    this.paramDecorators = paramDecorators;
    this.funcDecorators = funcDecorators;
  }

  /**
   * create search Section
   *
   * @param composite Composite
   */
  void createSearchSection(final Composite composite) {

    // create search section
    this.searchSection = SectionUtil.getInstance().createSection(composite, this.formToolkit, "");
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    // grid data for search section
    this.searchSection.setLayoutData(gridData);
    GridLayout gridLayout = new GridLayout();
    this.searchSection.setLayout(gridLayout);
    createSearchForm();
    this.searchSection.getDescriptionControl().setEnabled(false);
    this.searchSection.setText("Search Criteria");
    this.searchSection.setClient(this.searchForm);

    // creating parameter and function control
    createParameterControl(this.searchForm.getBody());
    createFunctionControl(this.searchForm.getBody());

    getNewLabel(this.searchForm.getBody(), SWT.NONE);
    getNewLabel(this.searchForm.getBody(), SWT.NONE);
    // creating search button and clear button
    createSearchButton(this.searchForm.getBody());
    createClearButton(this.searchForm.getBody());

  }


  /**
   * @param comp
   */
  private void createFunctionControl(final Composite comp) {
    LabelUtil.getInstance().createLabel(this.formToolkit, comp, "Function");
    this.functionName = TextUtil.getInstance().createTextFileldWithHorzFill(comp, this.formToolkit);
    this.functionName.setEnabled(true);
    this.functionName.setEditable(true);

    // create decorators for func name text
    this.funcNameDec = new ControlDecoration(this.functionName, SWT.LEFT | SWT.TOP);
    this.funcDecorators.showReqdDecoration(this.funcNameDec, IUtilityConstants.MANDATORY_MSG);

    this.functionName.addModifyListener(event -> {
      // validate with decorator
      Validator.getInstance().validateNDecorate(SearchParametersSection.this.funcNameDec,
          SearchParametersSection.this.functionName, true, false);
      SearchParametersSection.this.funcNameStr = SearchParametersSection.this.functionName.getText();
      enableSearch();
    });

  }

  /**
   * @param composite2
   */
  private void createParameterControl(final Composite comp) {
    LabelUtil.getInstance().createLabel(this.formToolkit, comp, "Parameter");
    this.paramName = TextUtil.getInstance().createTextFileldWithHorzFill(comp, this.formToolkit);
    this.paramName.setEnabled(true);
    this.paramName.setEditable(true);

    // create decorators for parameter name text
    this.paramNameDec = new ControlDecoration(this.paramName, SWT.LEFT | SWT.TOP);
    this.paramDecorators.showReqdDecoration(this.paramNameDec, IUtilityConstants.MANDATORY_MSG);

    this.paramName.addModifyListener(event -> {
      // validate with decorator
      Validator.getInstance().validateNDecorate(SearchParametersSection.this.paramNameDec,
          SearchParametersSection.this.paramName, true, false);
      SearchParametersSection.this.parameterName = SearchParametersSection.this.paramName.getText();
      enableSearch();
    });
  }

  /**
   * @param parent Serach Form
   */
  private void createSearchButton(final Composite parent) {

    final Composite buttonGroup = new Composite(parent, SWT.NONE);
    final GridLayout layout = new GridLayout();
    layout.makeColumnsEqualWidth = false;
    layout.marginWidth = 0;
    // grid data for search button
    final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
    gridData.widthHint = SEARCH_DIA_WIDTH;
    gridData.heightHint = SEARCH_DIA_HEIGHT;
    buttonGroup.setLayout(layout);
    this.searchBtn = new Button(buttonGroup, SWT.NONE);
    this.searchBtn.setLayoutData(gridData);
    this.searchBtn.setText("Search");
    this.searchBtn.setEnabled(false);

    this.searchBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
        dialog.create();
        final List<FunctionParamProperties> searchResults = new ArrayList<>();
        try {
          // non-cancellable progress monitor
          dialog.run(true, false, monitor -> {
            monitor.beginTask("Fetching Parameters...", IProgressMonitor.UNKNOWN);
            ParameterServiceClient parameterServiceClient = new ParameterServiceClient();
            SortedSet<FunctionParamProperties> searchParamResults = null;
            try {
              searchParamResults =
                  parameterServiceClient.getsearchParameters(SearchParametersSection.this.parameterName,
                      SearchParametersSection.this.funcNameStr, SearchParametersSection.this.ruleSet.getId());
            }
            catch (ApicWebServiceException e) {
              CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
            }
            searchResults.addAll(searchParamResults);
            monitor.done();

          });
        }
        catch (InvocationTargetException | InterruptedException e) {
          // log the exception
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
        // set the input for search results table
        SearchParametersSection.this.paramTabViewer.setInput(searchResults);
      }
    });
  }

  /**
   * @param parent Serach Form
   */
  private void createClearButton(final Composite parent) {

    final Composite buttonGroup = new Composite(parent, SWT.NONE);
    final GridLayout layout = new GridLayout();
    layout.makeColumnsEqualWidth = false;
    layout.marginWidth = 0;
    // grid data for clear button
    final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
    gridData.widthHint = SEARCH_DIA_WIDTH;
    gridData.heightHint = SEARCH_DIA_HEIGHT;
    buttonGroup.setLayout(layout);
    this.clearBtn = new Button(buttonGroup, SWT.NONE);
    this.clearBtn.setLayoutData(gridData);
    this.clearBtn.setText("Clear All");
    this.clearBtn.setEnabled(false);

    this.clearBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        // clear the table and texts
        SearchParametersSection.this.paramTabViewer.setInput("");
        SearchParametersSection.this.functionName.setText("");
        SearchParametersSection.this.paramName.setText("");

      }
    });
  }


  /**
   * create the Search Form
   */
  private void createSearchForm() {
    // creating form with four columned layout
    this.searchForm = this.formToolkit.createForm(this.searchSection);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = SEARCH_COMP_COL;
    this.searchForm.getBody().setLayout(gridLayout1);
    this.searchForm.getBody().setLayoutData(gridData);
  }

  /**
   * the method that enables or disables
   */
  private void enableSearch() {
    if (SearchParametersSection.this.paramName.getText().trim().isEmpty() ||
        SearchParametersSection.this.functionName.getText().trim().isEmpty()) {
      // disable buttons if parameter name or function name is empty
      SearchParametersSection.this.searchBtn.setEnabled(false);
      SearchParametersSection.this.clearBtn.setEnabled(false);
    }
    else {
      // enable buttons otherwise
      SearchParametersSection.this.searchBtn.setEnabled(true);
      SearchParametersSection.this.clearBtn.setEnabled(true);
    }

  }


  /**
   * @return the ruleSet
   */
  public RuleSet getRuleSet() {
    return this.ruleSet;
  }


  /**
   * @param ruleSet the ruleSet to set
   */
  public void setRuleSet(final RuleSet ruleSet) {
    this.ruleSet = ruleSet;
  }


  /**
   * @return the formToolkit
   */
  public FormToolkit getFormToolkit() {
    return this.formToolkit;
  }


  /**
   * @param formToolkit the formToolkit to set
   */
  public void setFormToolkit(final FormToolkit formToolkit) {
    this.formToolkit = formToolkit;
  }

  /**
   * @return the paramTabViewer
   */
  public GridTableViewer getParamTabViewer() {
    return this.paramTabViewer;
  }


  /**
   * @return the paramTableFilter
   */
  public ParamFuncViewerFilter getParamTableFilter() {
    return this.paramTableFilter;
  }


  /**
   * @param paramTableFilter the paramTableFilter to set
   */
  public void setParamTableFilter(final ParamFuncViewerFilter paramTableFilter) {
    this.paramTableFilter = paramTableFilter;
  }


  /**
   * @return the filterTxt
   */
  public Text getFilterTxt() {
    return this.filterTxt;
  }


  /**
   * @param filterTxt the filterTxt to set
   */
  public void setFilterTxt(final Text filterTxt) {
    this.filterTxt = filterTxt;
  }


  /**
   * @param paramTabViewer the paramTabViewer to set
   */
  public void setParamTabViewer(final GridTableViewer paramTabViewer) {
    this.paramTabViewer = paramTabViewer;
  }


  /**
   * @return the ruleSetParamTabSorter
   */
  public ParamFuncViewerSorter getRuleSetParamTabSorter() {
    return this.ruleSetParamTabSorter;
  }


  /**
   * @param ruleSetParamTabSorter the ruleSetParamTabSorter to set
   */
  public void setRuleSetParamTabSorter(final ParamFuncViewerSorter ruleSetParamTabSorter) {
    this.ruleSetParamTabSorter = ruleSetParamTabSorter;
  }


  /**
   * @return the addButton
   */
  public Button getAddButton() {
    return this.addButton;
  }


  /**
   * @param addButton the addButton to set
   */
  public void setAddButton(final Button addButton) {
    this.addButton = addButton;
  }

  /**
   * @param parent lable
   * @param style style
   * @return the label of given style
   */
  private Label getNewLabel(final Composite parent, final int style) {
    return new Label(parent, style);
  }

}