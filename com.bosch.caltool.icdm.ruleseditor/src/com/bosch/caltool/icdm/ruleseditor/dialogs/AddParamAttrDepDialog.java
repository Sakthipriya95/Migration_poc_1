/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.cdr.IParamAttrProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.pages.DetailsPage;
import com.bosch.caltool.icdm.ruleseditor.pages.ListPage;
import com.bosch.caltool.icdm.ruleseditor.sorters.AddAttrTabViewerSorter;
import com.bosch.caltool.icdm.ruleseditor.table.filters.ParamAttrDepFilter;
import com.bosch.caltool.icdm.ruleseditor.utils.Messages;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * ICDM-1088 This dialog adds the Param Attr mapping
 *
 * @author rgo7cob
 */
public class AddParamAttrDepDialog<D extends IParameterAttribute, P extends IParameter> extends AbstractDialog {

  /**
   * Review Comment Dialog
   */
  private static final String PARAM_ATTR_DEP = "Add Parameter Attribute Dependency";
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


  /**
   * Result Parameter for which comment is to be updated - null in case of multiple params
   */
  private final DetailsPage detailsPage;
  private Text attrFilText;
  private GridTableViewer attrTab;
  private Button saveBtn;

  /**
   * selected Attributes from the table viewer,.
   */
  protected List<Attribute> selectedAttrs;
  private ParamAttrDepFilter attrFilter;
  private AddAttrTabViewerSorter addAttrSorter;
  private final ParameterDataProvider<D, P> parameterDataProvider;

  /**
   * @param parentShell parent shell
   * @param detailsPage Result Parameter for which comment is to be updated - null in case of multiple params
   */
  public AddParamAttrDepDialog(final Shell parentShell, final DetailsPage detailsPage,
      final ParameterDataProvider<D, P> parameterDataProvider) {
    super(parentShell);
    this.detailsPage = detailsPage;
    this.parameterDataProvider = parameterDataProvider;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(PARAM_ATTR_DEP);

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
    this.section.setDescription(
        "If you want to map an attribute that is not part of the list,please press the mail to hotline button.");


    this.section.getDescriptionControl().setFont(new Font(this.top.getDisplay(), "Segoe UI", 10, SWT.BOLD));


    this.section.setLayout(new GridLayout());
    this.section.getDescriptionControl().setEnabled(false);
    createForm();
    this.section.setExpanded(true);
    createParamToolbarAction();
    this.section.setClient(this.form);

  }

  /**
   * create toolbar action for adding
   */
  private void createParamToolbarAction() {


    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = toolBarManager.createControl(this.section);


    toolBarManager.add(new Separator());

    mailToHotLine(toolBarManager);


    toolBarManager.update(true);
    this.section.setTextClient(toolbar);


  }

  /**
   * @param toolBarManager
   */
  private void mailToHotLine(final ToolBarManager toolBarManager) {
    final Action importAction = new Action() {

      @Override
      public void run() {
        UnmappedAttrDialog dialog =
            new UnmappedAttrDialog(Display.getCurrent().getActiveShell(), AddParamAttrDepDialog.this.detailsPage);
        dialog.open();
      }


    };

    importAction.setText("Mail to Hotline");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.SEND_MAIL_16X16);
    importAction.setImageDescriptor(imageDesc);
    toolBarManager.add(importAction);


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
    this.attrFilter = new ParamAttrDepFilter(this.parameterDataProvider); // Add TableViewer filter
    this.attrTab.addFilter(this.attrFilter);

    this.attrFilText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        String text = AddParamAttrDepDialog.this.attrFilText.getText().trim();
        AddParamAttrDepDialog.this.attrFilter.setFilterText(text);
        AddParamAttrDepDialog.this.attrTab.refresh();
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
        IStructuredSelection selection = (IStructuredSelection) AddParamAttrDepDialog.this.attrTab.getSelection();
        if (!selection.isEmpty()) {
          List<Attribute> listAttr = selection.toList();
          AddParamAttrDepDialog.this.selectedAttrs = new ArrayList<Attribute>();
          AddParamAttrDepDialog.this.selectedAttrs.addAll(listAttr);
          AddParamAttrDepDialog.this.saveBtn.setEnabled(true);
        }
      }

    });
  }


  /**
   * set the Tab view Input
   */
  private void setTabViewInput() {

    try {
      Set<Attribute> mappedAttrs = this.parameterDataProvider.getMappedAttributes();
      Set<Attribute> tabInp = new TreeSet<Attribute>();
      tabInp.addAll(mappedAttrs);
      List<IParameter> listParam = new ArrayList<>();
      listParam.add(this.detailsPage.getSelectedParam());
      List<D> paramAttrSet = this.parameterDataProvider.getParamAttrs(this.detailsPage.getSelectedParam());
      if (CommonUtils.isNotNull(paramAttrSet)) {
        for (Object parameterAttribute : paramAttrSet) {
          IParameterAttribute paramAttr = (IParameterAttribute) parameterAttribute;
          tabInp.remove(this.parameterDataProvider.getAttribute(paramAttr));
        }
      }
      this.attrTab.setInput(tabInp);
    }

    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog("Error when fetching mapped Attribute", exp, Activator.PLUGIN_ID);
    }
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
    try {

      IParamAttrProvider<D> paramAttrProvider =
          this.parameterDataProvider.getParamAttrProvider(this.detailsPage.getSelectedParam());

      ParamCollection cdrFunction = this.detailsPage.getCdrFunction();
      Long ruleSetId = null;
      if (cdrFunction instanceof RuleSet) {
        ruleSetId = cdrFunction.getId();
      }
      List<IParameter> selectedParams = this.detailsPage.getSelectedParams();

      // Do the insertion for each parameter selected
      for (IParameter parameter : selectedParams) {
        List<D> iParamAttrs = paramAttrProvider.getParamAttrs(parameter, this.selectedAttrs, ruleSetId);

        List<Object> createParamAttrs = this.parameterDataProvider.createParamAttrs(iParamAttrs);


        List<D> paramAttrs = this.parameterDataProvider.getParamAttrs(parameter);

        if (paramAttrs == null) {
          paramAttrs = new ArrayList<>();
        }

        for (Object obj : createParamAttrs) {
          D paramAttr = (D) obj;
          paramAttrs.add(paramAttr);
        }

        for (Attribute attr : this.selectedAttrs) {
          this.parameterDataProvider.getParamRulesOutput().getAttrObjMap().put(attr.getId(), attr);
        }


        this.parameterDataProvider.getParamRulesOutput().getAttrMap().put(parameter.getName(), paramAttrs);
        this.detailsPage.getParamAttrTab().setInput(paramAttrs);

      }

      ListPage listPage = this.detailsPage.getEditor().getListPage();
      if (null != listPage) {
        listPage.getEditor().refreshSelectedParamRuleData();
        listPage.refreshListPage();
      }
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
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
