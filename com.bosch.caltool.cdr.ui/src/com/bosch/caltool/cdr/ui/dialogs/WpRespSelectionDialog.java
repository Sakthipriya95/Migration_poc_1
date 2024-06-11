/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.SortedSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
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
import com.bosch.caltool.cdr.ui.sorters.WpRespTableViewerSorted;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author DMR1COB
 */
public class WpRespSelectionDialog extends AbstractDialog {

  /**
   *
   */
  private static final String SELECT_WORKPACKSGE_RESPONSIBILITY = "Select workpacksge responsibility";
  private static final String DEFAULT_WP_RESP = "_DEFAULT_WP_, Robert Bosch";
  private final SortedSet<String> wpRespSet;
  private Composite top;
  private Composite composite;
  private FormToolkit formToolkit;
  private Section section;
  private Form form;
  private AbstractViewerFilter filters;
  private GridTableViewer wpRespTableViewer;
  private WpRespTableViewerSorted tabSorter;
  protected Button okBtn;
  protected String selectedWpResp;

  /**
   * @param parentShell parent shell
   * @param wpRespList wp resp list
   */
  public WpRespSelectionDialog(final Shell parentShell, final SortedSet<String> wpRespList) {
    super(parentShell);
    this.wpRespSet = wpRespList;
  }

  /**
   * Sets the Dialog Resizable
   *
   * @param newShellStyle new style
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM | SWT.MAX);
  }

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
    final Control contents = super.createContents(parent);

    // Set the title
    setTitle(SELECT_WORKPACKSGE_RESPONSIBILITY);

    // Set the message
    setMessage(SELECT_WORKPACKSGE_RESPONSIBILITY, IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(SELECT_WORKPACKSGE_RESPONSIBILITY);
    super.configureShell(newShell);
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
   *
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   *
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "List of variants");
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   *
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    // Create Filter text
    createFilterTxt();
    // Invokde GridColumnViewer sorter
    invokeColumnSorter();
    // Create new users grid tableviewer
    createWpRespSelectionGridTabViewer();
    this.wpRespTableViewer.setComparator(this.tabSorter);
    // Set ContentProvider and LabelProvider to addNewUserTableViewer
    setTabViewerProviders();

    // Set input to the addNewUserTableViewer
    setTabViewerInput();

    // Add selection listener to the addNewUserTableViewer
    addTableSelectionListener();

    // Add filters to the TableViewer
    addFilters();

    // Adds double click selection listener to the variantTableViewer
    addDoubleClickListener();

    setDefaultWPGrey();


    this.form.getBody().setLayout(new GridLayout());

  }

  /**
   *
   */
  private void addDoubleClickListener() {
    this.wpRespTableViewer.addDoubleClickListener(event -> {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      if (selection.size() == 1) {
        Object firstElement = selection.getFirstElement();
        if ((firstElement != null) && !CommonUtils.isEqual(firstElement.toString(),DEFAULT_WP_RESP)) {
          Display.getDefault().asyncExec(WpRespSelectionDialog.this::okPressed);
        }
      }
    });
  }

  /**
   *
   */
  private void addFilters() {
    this.filters = new AbstractViewerFilter() {

      @Override
      protected boolean selectElement(final Object element) {
        if (element instanceof String) {

          final String wpRespName = (String) element;
          // match the entered text with wp resp name
          if (matchText(wpRespName)) {
            return true;
          }
        }
        return false;
      }
    };
    // Add PIDC Attribute TableViewer filter
    this.wpRespTableViewer.addFilter(this.filters);

  }

  /**
   *
   */
  private void addTableSelectionListener() {

    this.wpRespTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) WpRespSelectionDialog.this.wpRespTableViewer.getSelection();
        if ((selection != null) && (selection.size() == 1) &&
            (!CommonUtils.isEqual(DEFAULT_WP_RESP,selection.getFirstElement().toString()))) {
          WpRespSelectionDialog.this.selectedWpResp = (String) selection.getFirstElement();
          WpRespSelectionDialog.this.okBtn.setEnabled(true);
        }
        else {
          WpRespSelectionDialog.this.okBtn.setEnabled(false);
        }
      }
    });

  }

  /**
   *
   *
   */
  private void setTabViewerInput() {
    this.wpRespTableViewer.setInput(this.wpRespSet);
  }

  /**
   *
   */
  private void setTabViewerProviders() {
    this.wpRespTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.wpRespTableViewer.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return (String) element;
      }
    });
  }

  /**
  *
  */
  private void setDefaultWPGrey() {
    this.wpRespTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.wpRespTableViewer.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return (String) element;
      }

      @Override
      public void update(final ViewerCell cell) {
        super.update(cell);
        if (CommonUtils.isEqual(DEFAULT_WP_RESP,cell.getText())) {
          cell.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
        }
      }


    });
  }


  /**
   *
   */
  private void createWpRespSelectionGridTabViewer() {
    int style = SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL;
    this.wpRespTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(), style,
        GridDataUtil.getInstance().getHeightHintGridData(200));
    // Create GridViewerColumns
    createGridViewerColumns();
  }

  /**
   *
   */
  private void createGridViewerColumns() {
    GridViewerColumn nameColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.wpRespTableViewer,
        "Work Package Responsibility", 500);
    // Add column selection listener

    nameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        nameColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_0, this.tabSorter, this.wpRespTableViewer));
  }

  /**
   *
   */
  private void invokeColumnSorter() {
    this.tabSorter = new WpRespTableViewerSorted();
  }

  /**
   *
   */
  private void createFilterTxt() {
    final Text filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "type filter text");

    filterTxt.addModifyListener(event -> {
      String text = filterTxt.getText().trim();
      WpRespSelectionDialog.this.filters.setFilterText(text);
      WpRespSelectionDialog.this.wpRespTableViewer.refresh();
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
   * Creates the buttons for the button bar
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, "Select", false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * @return the selectedWpResp
   */
  public String getSelectedWpResp() {
    return this.selectedWpResp;
  }

  /**
   * @param selectedWpResp the selectedWpResp to set
   */
  public void setSelectedWpResp(final String selectedWpResp) {
    this.selectedWpResp = selectedWpResp;
  }


}
