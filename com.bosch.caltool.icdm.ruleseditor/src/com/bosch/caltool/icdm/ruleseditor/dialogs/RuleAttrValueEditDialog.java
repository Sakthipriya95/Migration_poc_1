/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueDontCare;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.table.filters.RuleAttrValueFilter;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizard;
import com.bosch.caltool.icdm.ruleseditor.wizards.pages.SelectAttrValWizardPage;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author bru2cob
 */
public class RuleAttrValueEditDialog<D extends IParameterAttribute, P extends IParameter> extends AbstractDialog {

  /**
   * Value col width
   */
  private static final int VALUE_COL_WIDTH = 200;
  /**
   * Value table height hint
   */
  private static final int VALUE_TAB_HEIGHT_HINT = 200;
  /**
   * Selected attribute
   */
  private final D editableAttr;
  /**
   * Instance of select attr value wizard page
   */
  private final SelectAttrValWizardPage selectAttrValWizardPage;
  /**
   * Ok button label as add
   */
  private static final String ADD_LABEL = "Add";
  /**
   * Add new user button instance
   */
  private Button okBtn;
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
  /**
   * GridTableViewer instance for add values
   */
  private GridTableViewer valuesTabViewer;
  /**
   * RuleAttrValueFilter
   */
  private RuleAttrValueFilter attrValFilter;

  /**
   * @param shell
   * @param selectAttrValWizardPage
   * @param editableAttr2
   */
  public RuleAttrValueEditDialog(final Shell shell, final SelectAttrValWizardPage<D, P> selectAttrValWizardPage,
      final D editableAttr2) {
    super(shell);
    this.editableAttr = editableAttr2;
    this.selectAttrValWizardPage = selectAttrValWizardPage;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Select value/value's");
    // Set the message
    setMessage(ApicUiConstants.ATTR_NAME + this.editableAttr.getName(), IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell name
    newShell.setText("Select value/value's");
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
    // create composite on parent comp
    createComposite();
    return this.top;
  }


  /**
   * This method initializes composite
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    // create section
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "List of values");
    this.section.getDescriptionControl().setEnabled(false);
    // create form
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    createFilterTxt();
    // Create values grid tableviewer
    createValuesGridTabViewer();

    this.form.getBody().setLayout(new GridLayout());

  }

  /**
   * This method creates filter text.
   */
  private void createFilterTxt() {
    this.attrValFilter = new RuleAttrValueFilter();
    Text filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "");

    filterTxt.addModifyListener(event -> {
      final String text = filterTxt.getText().trim();
      this.attrValFilter.setFilterText(text);
      this.valuesTabViewer.refresh();
    });

    filterTxt.setFocus();

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
   * This method creates the addNewUserTableViewer
   *
   * @param gridData
   */
  private void createValuesGridTabViewer() {
    int style;
    if (this.selectAttrValWizardPage.getWizardData().isAttrValMapIncomplete()) {
      style = SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE;
    }
    else {
      style = SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI;
    }
    this.valuesTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(), style,
        GridDataUtil.getInstance().getHeightHintGridData(VALUE_TAB_HEIGHT_HINT));

    this.valuesTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    // Create GridViewerColumns
    final GridViewerColumn valueColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.valuesTabViewer,
        "Attribute Values", VALUE_COL_WIDTH);
    valueColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final AttributeValue item = (AttributeValue) element;
        return item.getName();
      }
    });
    AddNewConfigWizard<D, P> wizard = (AddNewConfigWizard) this.selectAttrValWizardPage.getWizard();

    try {
      SortedSet<com.bosch.caltool.icdm.model.apic.attr.AttributeValue> mappedAttrVal =
          wizard.getParamDataProvider().getMappedAttrVal(this.editableAttr);

      Map<Long, AttributeValueDontCare> dontCareAttrValueMap = this.selectAttrValWizardPage.getWizardData()
          .getParamRulesModel().getParamDataProvider().getParamRulesOutput().getDontCareAttrValueMap();
      if (CommonUtils.isNotEmpty(dontCareAttrValueMap) &&
          dontCareAttrValueMap.containsKey(this.editableAttr.getAttrId())) {
        mappedAttrVal.add(dontCareAttrValueMap.get(this.editableAttr.getAttrId()));
      }

      this.valuesTabViewer.setInput(mappedAttrVal);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    // Add selection listener
    addTableSelectionListener();
    // add double click listener
    addDoubleClickListener();
    // add type filter
    this.valuesTabViewer.addFilter(this.attrValFilter);
  }

  private void addDoubleClickListener() {
    this.valuesTabViewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(final DoubleClickEvent doubleclickevent) {
        Display.getDefault().asyncExec(RuleAttrValueEditDialog.this::okPressed);

      }

    });
  }

  /**
   * This method adds selection listener to the addNewUserTableViewer
   */
  private void addTableSelectionListener() {
    this.valuesTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) RuleAttrValueEditDialog.this.valuesTabViewer.getSelection();
        if (selection != null) {
          RuleAttrValueEditDialog.this.okBtn.setEnabled(true);
        }
      }
    });
  }

  /**
   * Creates the buttons for the button bar
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, ADD_LABEL, false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    final IStructuredSelection selection =
        (IStructuredSelection) RuleAttrValueEditDialog.this.valuesTabViewer.getSelection();
    SortedSet<AttributeValue> selValSet = new TreeSet<>();


    if (selection.getFirstElement() instanceof AttributeValue) {
      final Iterator<AttributeValue> values = selection.iterator();
      if (validateIfMultiSelectContainsDontCareValue(selection)) {
        MessageDialogUtils.getErrorMessageDialog("Invalid selection",
            "Cannot select " + CDRConstants.DONT_CARE_ATTR_VALUE_TEXT + " value in combination with other values.");
        return;
      }
      while (values.hasNext()) {
        final AttributeValue value = values.next();
        selValSet.add(value);
      }
      this.selectAttrValWizardPage.getWizardData().getAttrVals().put(this.editableAttr, selValSet);
      this.selectAttrValWizardPage.getAttrsTableViewer().refresh();
    }
    super.okPressed();
  }

  private boolean validateIfMultiSelectContainsDontCareValue(final IStructuredSelection selection) {
    List<AttributeValue> selectedValList = selection.toList();
    if (CommonUtils.isNotEmpty(selectedValList) && (selectedValList.size() > 1)) {
      for (AttributeValue attributeValue : selectedValList) {
        if (attributeValue instanceof AttributeValueDontCare) {
          return true;
        }
      }
    }
    return false;
  }


}
