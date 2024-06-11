/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.table.filters.AttributesFilters;
import com.bosch.caltool.icdm.client.bo.cdr.QuesDepnAttributeRow;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.ruleseditor.sorters.AddAttrTabViewerSorter;
import com.bosch.caltool.icdm.ruleseditor.utils.Messages;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * This dialog adds the Attribute dependency in Question creation dialog
 *
 * @author dmo5cob
 */
public class AddAttributeDepnDialog extends AbstractDialog {

  /**
   * Add Attribute Dependency
   */
  private static final String ATTR_DEP = "Add  Attribute Dependency";
  /**
   * Top composite instance
   */
  private Composite top;
  /**
   * composite instance
   */
  private Composite composite;

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * section instance
   */
  private Section section;

  /**
   * Form instance
   */
  private Form form;

  /**
   * name Col Length
   */
  private static final int NAME_COL_LEN = 200;

  /**
   * Desc col Length
   */
  private static final int DESC_COL_LEN = 300;

  /**
   * Attr tab Height
   */
  private static final int ATTR_TAB_HEIGHT = 150;


  private Text attrFilText;
  private GridTableViewer attrTab;
  private Button saveBtn;

  /**
   * selected Attributes from the table viewer,.
   */

  private AttributesFilters attrFilter;
  private AddAttrTabViewerSorter addAttrSorter;
  private final QuestionDialog questionDialog;
  private final Set<QuesDepnAttributeRow> selectedAttrs;

  /**
   * @param parentShell parent shell
   * @param questionDialog instance
   * @param selectedAttrs
   */
  public AddAttributeDepnDialog(final Shell parentShell, final QuestionDialog questionDialog,
      final Set<QuesDepnAttributeRow> selectedAttrs) {
    super(parentShell);
    this.questionDialog = questionDialog;
    this.selectedAttrs = selectedAttrs;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(ATTR_DEP);

    // Set the message
    setMessage("Select one or more Attributes", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Add Attributes");
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    newShell.setLayout(new GridLayout());
    newShell.setLayoutData(gridData);
    super.configureShell(newShell);
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
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.top.setLayoutData(gridData);
    createComposite();
    return this.top;
  }

  /**
   * create composite
   */
  private void createComposite() {

    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite.setLayoutData(gridData);
    createSection();
  }

  /**
   * create section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Select the Attributes");
    this.section.setLayout(new GridLayout());
    this.section.getDescriptionControl().setEnabled(false);
    createForm();
    this.section.setExpanded(true);
    this.section.setClient(this.form);

  }

  /**
   * create form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    final GridData gridData = GridDataUtil.getInstance().getTextGridData();
    createAttrTable(this.form);
    final GridLayout gridLayout = new GridLayout();
    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setLayoutData(gridData);
  }

  /**
   * @param form
   */
  private void createAttrTable(final Form form) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = true;
    gridData.heightHint = ATTR_TAB_HEIGHT;
    this.addAttrSorter = new AddAttrTabViewerSorter();
    this.attrFilText = this.formToolkit.createText(form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.attrTab =
        new GridTableViewer(form.getBody(), SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
    this.attrTab.setAutoPreferredHeight(true);
    this.attrTab.getGrid().setLayout(new GridLayout());
    this.attrTab.getGrid().setLayoutData(gridData);
    this.attrTab.getGrid().setLinesVisible(true);
    this.attrTab.getGrid().setHeaderVisible(true);
    this.attrTab.setComparator(this.addAttrSorter);
    createTabColumns();
    this.attrTab.setContentProvider(ArrayContentProvider.getInstance());

    setTabViewInput();
    addSelectionListener(this.attrTab);
    addDoubleClickListener(this.attrTab);
    createAttrFilter();
  }


  /**
   * create the Attr Dep Filter for the Param Attribute Dep Table
   */
  private void createAttrFilter() {

    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    this.attrFilText.setLayoutData(gridData);
    this.attrFilText.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.attrFilter = new AttributesFilters(); // Add TableViewer filter
    this.attrTab.addFilter(this.attrFilter);

    this.attrFilText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        String text = AddAttributeDepnDialog.this.attrFilText.getText().trim();
        AddAttributeDepnDialog.this.attrFilter.setFilterText(text);
        AddAttributeDepnDialog.this.attrTab.refresh();
      }
    });

  }

  /**
   * ICDM 574-This method defines the activities to be performed when double clicked on the table
   *
   * @param functionListTableViewer2
   */
  private void addDoubleClickListener(final GridTableViewer tableviewer) {
    tableviewer.addDoubleClickListener(new IDoubleClickListener() {

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
   * @param attrTab
   */
  private void addSelectionListener(final GridTableViewer attrTab) {
    attrTab.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        IStructuredSelection selection = (IStructuredSelection) attrTab.getSelection();
        if (!selection.isEmpty()) {

          AddAttributeDepnDialog.this.saveBtn.setEnabled(true);
        }
      }

    });
  }


  /**
   * set the Tab view Input
   */
  private void setTabViewInput() {
    Map<Long, Attribute> attrMap = getAttributes();
    SortedSet<Attribute> attributes = new TreeSet<>();
    attributes.addAll(attrMap.values());
    Set<Attribute> tabInp = new TreeSet<>();
    tabInp.addAll(attributes);

    this.attrTab.setInput(tabInp);
  }

  /**
   * @return the map of all attributes excluding attrs of type HyperLink and Date
   */
  public Map<Long, Attribute> getAttributes() {
    Map<Long, Attribute> attributes = new ConcurrentHashMap<>();
    Map<Long, Attribute> allAttributes = new HashMap<>();
    try {
      allAttributes = new AttributeServiceClient().getAll();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    for (Attribute attr : allAttributes.values()) {
      if ((!attr.getValueType().equals(AttributeValueType.HYPERLINK.getDisplayText())) &&
          (!attr.getValueType().equals(AttributeValueType.DATE.getDisplayText())) &&
          (!attr.getValueType().equals(AttributeValueType.ICDM_USER.getDisplayText())) && !attr.isDeleted()) {
        attributes.put(attr.getId(), attr);
      }
    }
    return attributes;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Add", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }


  /**
   * create the Table Columns
   */
  private void createTabColumns() {
    final GridViewerColumn attrColumn = new GridViewerColumn(this.attrTab, SWT.NONE);
    attrColumn.getColumn().setText("Attribute Name");
    attrColumn.getColumn().setWidth(NAME_COL_LEN);
    attrColumn.getColumn().setResizeable(true);

    attrColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        Attribute attr = (Attribute) element;
        return attr.getName();
      }
    });

    attrColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrColumn.getColumn(), 0, this.addAttrSorter, this.attrTab));
    // Icdm-1088 new Column added for Attr Description.
    final GridViewerColumn attrDescColumn = new GridViewerColumn(this.attrTab, SWT.NONE);
    attrDescColumn.getColumn().setText("Description");
    attrDescColumn.getColumn().setWidth(DESC_COL_LEN);
    attrDescColumn.getColumn().setResizeable(true);

    attrDescColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        Attribute attr = (Attribute) element;
        return attr.getDescription();
      }
    });

    attrDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrDescColumn.getColumn(), 1, this.addAttrSorter, this.attrTab));
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    List<QuesDepnAttributeRow> listQAttr = new ArrayList<>();
    for (GridItem iterable_element : this.questionDialog.getAttrTab().getGrid().getItems()) {
      QuesDepnAttributeRow row = (QuesDepnAttributeRow) iterable_element.getData();
      listQAttr.add(row);
    }

    IStructuredSelection selection = (IStructuredSelection) this.attrTab.getSelection();
    if (!selection.isEmpty()) {
      List<Attribute> listAttr = selection.toList();

      for (Attribute attribute : listAttr) {
        QuesDepnAttributeRow attrRow = new QuesDepnAttributeRow(this.questionDialog.getSelectedQuestion(), attribute,
            this.questionDialog.isUpdate());
        listQAttr.add(attrRow);
      }
      AddAttributeDepnDialog.this.selectedAttrs.clear();
      AddAttributeDepnDialog.this.selectedAttrs.addAll(listQAttr);
      this.questionDialog.getAttrTab().setInput(this.selectedAttrs);
      this.questionDialog.checkSaveBtnEnable();
    }
    super.okPressed();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE | SWT.MAX);

  }

}
