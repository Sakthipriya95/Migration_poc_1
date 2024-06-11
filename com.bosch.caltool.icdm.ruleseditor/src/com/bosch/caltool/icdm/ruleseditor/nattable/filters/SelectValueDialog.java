/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.nattable.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.cdr.ParamRulesModel;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.RuleDependency;
import com.bosch.caltool.icdm.ruleseditor.pages.ParametersRulePage;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author jvi6cob
 */
public class SelectValueDialog<D extends IParameterAttribute, P extends IParameter> extends AbstractDialog {

  /**
   * value column width
   */
  private static final int VALUE_COLUMN_WIDTH = 120;
  /**
   * height hint
   */
  private static final int HEIGHT_HINT = 200;
  /**
   * Add new user button instance
   */
  private Button okBtn;
  /**
   * GridTableViewer instance for add new user
   */
  private GridTableViewer tabViewer;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;

  private final String shellTitle = "Select a Value";

  private final String dialogTitle = "Select a Value";

  private final String messageText = "Select a value from the list";

  private final String okButtonLabel = "Select";


  private final Attribute attr;
  protected String attrVal;
  private final AttrValueCombChooserDialog attrValueCombChooserDialog;
  private final ParametersRulePage<D, P> paramRulesPage;

  /**
   * @param shell the parent shell
   * @param attrValueCombChooserDialog
   * @param paramRulesPage
   * @param pidc .
   * @param pidcAttr .
   * @param setValuesForAttrsWizardPage .
   */
  public SelectValueDialog(final Shell shell, final Attribute attr,
      final AttrValueCombChooserDialog attrValueCombChooserDialog, final ParametersRulePage paramRulesPage) {
    super(shell);
    this.attr = attr;
    this.attrValueCombChooserDialog = attrValueCombChooserDialog;
    this.paramRulesPage = paramRulesPage;
  }


  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);

    // Set the title
    setTitle(this.dialogTitle);

    // Set the message
    setMessage(this.messageText, IMessageProvider.INFORMATION);

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(this.shellTitle);
    super.configureShell(newShell);
    // ICDM-153
    super.setHelpAvailable(true);
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
   * This method initializes composite
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "List of values");
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(false);
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    // Create tableviewer
    createTabViewer();

    // Set ContentProvider and LabelProvider to addNewUserTableViewer
    setTabViewerProviders();

    // Set input to the addNewUserTableViewer
    setTabViewerInput();

    // Add selection listener to the addNewUserTableViewer
    addTableSelectionListener();

    // Adds double click selection listener to the addNewUserTableViewer
    addDoubleClickListener();

    this.form.getBody().setLayout(new GridLayout());


  }

  /**
   * ICDM 574-This method defines the activities to be performed when double clicked on the table
   *
   * @param functionListTableViewer2
   */
  private void addDoubleClickListener() {
    this.tabViewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(final DoubleClickEvent doubleclickevent) {
        Display.getDefault().asyncExec(new Runnable() {

          @Override
          public void run() {
            okPressed();
          }
        });
      }

    });
  }

  /**
   * This method adds selection listener to the addNewUserTableViewer
   */
  private void addTableSelectionListener() {
    this.tabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) SelectValueDialog.this.tabViewer.getSelection();
        if (selection == null) {
          SelectValueDialog.this.okBtn.setEnabled(false);
        }
        else {

          final Object element = selection.getFirstElement();
          if (element instanceof String) {
            SelectValueDialog.this.attrVal = (String) element;
            SelectValueDialog.this.okBtn.setEnabled(true);
          }
        }


      }
    });
  }

  /**
   * This method sets the input to the addNewUserTableViewer
   */
  private void setTabViewerInput() {
    Set<String> values = new TreeSet<>();
    ParamRulesModel<D, P> model = this.paramRulesPage.getModel();
    Map<Integer, RuleDependency> ruleDependencyMap = model.getRuleDependencyMap();
    for (RuleDependency ruleDependency : ruleDependencyMap.values()) {
      AttributeValueModel attrModel = ruleDependency.getAttrAttrValModelMap().get(this.attr);
      if (null != attrModel) {
        values.add(attrModel.getValue().getName());
      }
    }
    List<String> valueList = new ArrayList<>();
    valueList.addAll(values);
    // add <NotDefined> and <Any> to end
    valueList.add(ApicConstants.ATTR_NOT_DEFINED);
    valueList.add(ApicConstants.ANY);
    this.tabViewer.setInput(valueList);
  }

  /**
   * This method sets ContentProvider & LabelProvider to the addNewUserTableViewer
   */
  private void setTabViewerProviders() {
    this.tabViewer.setContentProvider(ArrayContentProvider.getInstance());

  }

  /**
   * This method adds Columns to the addNewUserTableViewer
   */
  private void createGridViewerColumns() {
    createValueColumn();
  }

  /**
   * This method adds value column
   */
  private void createValueColumn() {
    final GridViewerColumn valColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tabViewer, "Value", VALUE_COLUMN_WIDTH);
    valColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof String) {
          return (String) element;
        }
        final AttributeValue item = (AttributeValue) element;
        return item.getName() == null ? "" : item.getName();
      }


    });
  }


  /**
   * This method creates the addNewUserTableViewer
   *
   * @param gridData
   */
  private void createTabViewer() {

    this.tabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(HEIGHT_HINT));

    // Create GridViewerColumns
    createGridViewerColumns();
  }

  /**
   * Creates the buttons for the button bar
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, this.okButtonLabel, false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if (null != SelectValueDialog.this.attrVal) {
      this.attrValueCombChooserDialog.getCombiMap().put(this.attr, SelectValueDialog.this.attrVal);
      this.attrValueCombChooserDialog.getTableViewer().refresh();
    }
    super.okPressed();
  }


  /**
   * {@inheritDoc}
   */
  // ICDM-153
  @Override
  protected boolean isResizable() { // NOPMD by dmo5cob on 8/8/14 10:14 AM
    return true;
  }


}
