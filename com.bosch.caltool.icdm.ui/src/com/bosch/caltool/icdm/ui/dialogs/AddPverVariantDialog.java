/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
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

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.ui.sorters.PverVariantSorter;
import com.bosch.caltool.icdm.ui.table.filters.PverVariantFilter;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author bru2cob
 */
public class AddPverVariantDialog extends AbstractDialog {

  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * filter text
   */
  private final String TYPE_FILTER_LABEL = "type filter text";
  /**
   * Section instance
   */
  private Section section;
  /**
   * Add new user button instance
   */
  private Button okBtn;

  /**
   * Form instance
   */
  private Form form;
  /**
   * Filter text instance
   */
  private Text filterTxt;
  /**
   * GridTableViewer instance for selection of variant or workpackage
   */
  private GridTableViewer varTbleViewer;
  /**
   * instance for Columns sortting
   */
  private PverVariantSorter tabSorter;

  protected String selectedVariant;
  private PverVariantFilter variantFilters;
  final Set<String> sdomPverVariants;

  /**
   * @param activeShell
   * @param sdomPver2
   */
  public AddPverVariantDialog(final Shell activeShell, final Set<String> sdomPverVariants) {
    super(activeShell);
    this.sdomPverVariants = sdomPverVariants;
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
    setTitle("Select PVER variant");

    // Set the message
    setMessage("select PVER variant", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Select PVER variant");
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
   * This method initializes composite
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
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
   * This method initializes section
   */
  private void createSection() {
    // Create section for list of variants
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "List of variants");
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {

    this.okBtn = createButton(parent, IDialogConstants.OK_ID, "Select", true);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    // Create Filter text
    createFilterTxt();
    // Create new users grid tableviewer
    createVariantGridTabViewer();

    // Add selection listener to the addNewUserTableViewer
    addTableSelectionListener();


    // Adds double click selection listener to the variantTableViewer
    addDoubleClickListener();

    // Create column sorter
    invokeColumnSorter();

    // add coulmn filters
    addFilters();

    this.form.getBody().setLayout(new GridLayout());

  }


  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    final GridData gridData = new GridData();
    gridData.widthHint = 500;
    // Create text field for filter
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(), gridData,
        this.TYPE_FILTER_LABEL);
    this.filterTxt.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        final String text = AddPverVariantDialog.this.filterTxt.getText().trim();
        AddPverVariantDialog.this.variantFilters.setFilterText(text);
        AddPverVariantDialog.this.varTbleViewer.refresh();
      }
    });
    this.filterTxt.setFocus();

  }

  /**
   * This method adds the filter instance to addNewUserTableViewer
   */
  private void addFilters() {
    // Add filter for Pver variants
    this.variantFilters = new PverVariantFilter();
    this.varTbleViewer.addFilter(this.variantFilters);

  }

  /**
   * This method adds selection listener to the addNewUserTableViewer
   */
  private void addTableSelectionListener() {
    this.varTbleViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        // Get the selected Pver variant
        final IStructuredSelection selection =
            (IStructuredSelection) AddPverVariantDialog.this.varTbleViewer.getSelection();
        if ((selection == null) || (selection.size() == 0)) {
          AddPverVariantDialog.this.okBtn.setEnabled(false);
          AddPverVariantDialog.this.selectedVariant = null;
        }
        else {
          AddPverVariantDialog.this.selectedVariant = selection.getFirstElement().toString();
          AddPverVariantDialog.this.okBtn.setEnabled(true);
        }
      }
    });
  }

  /**
   * @return selected variant
   */
  public String getSelectedVar() {
    return this.selectedVariant;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void cancelPressed() {
    this.selectedVariant = null;
    super.cancelPressed();
  }

  /**
   * This method creates the variantTableViewer
   *
   * @param gridData
   */
  private void createVariantGridTabViewer() {
    final GridData gridData = new GridData();
    gridData.grabExcessVerticalSpace = true;
    gridData.grabExcessHorizontalSpace = true;
    gridData.heightHint = 200;

    this.varTbleViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, gridData);
    this.varTbleViewer.setContentProvider(new ArrayContentProvider());
    // Create GridViewerColumns
    createGridViewerColumns();
    this.varTbleViewer.setInput(this.sdomPverVariants);
  }

  /**
   * This method adds Columns to the variantTableViewer
   *
   * @param attrMap
   */
  private void createGridViewerColumns() {
    createNameColumn();
  }

  /**
   * This method adds name column to the variantTableViewer
   *
   * @param attrMap
   */
  private void createNameColumn() {
    final GridViewerColumn nameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.varTbleViewer, "Name", 500);
    nameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return element.toString();
      }
    });
    // Add column selection listener
    nameColumn.getColumn().addSelectionListener(getSelectionAdapter(nameColumn.getColumn(), 0));
  }

  /**
   * Add sorter for the table columns
   */
  private void invokeColumnSorter() {
    this.tabSorter = new PverVariantSorter();
    this.varTbleViewer.setComparator(this.tabSorter);
  }

  /**
   * This method returns SelectionAdapter instance
   *
   * @param column
   * @param index
   * @return SelectionAdapter
   */
  private SelectionAdapter getSelectionAdapter(final GridColumn column, final int index) {
    final SelectionAdapter selectionAdapter = new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        AddPverVariantDialog.this.tabSorter.setColumn(index);
        // Get sorting direction
        int direction = AddPverVariantDialog.this.tabSorter.getDirection();
        for (int i = 0; i < AddPverVariantDialog.this.varTbleViewer.getGrid().getColumnCount(); i++) {
          if (i == index) {
            if (direction == 0) {
              column.setSort(SWT.DOWN);
            }
            else if (direction == 1) {
              column.setSort(SWT.UP);
            }
          }
          if (i != index) {
            AddPverVariantDialog.this.varTbleViewer.getGrid().getColumn(i).setSort(SWT.NONE);
          }
        }
        AddPverVariantDialog.this.varTbleViewer.refresh();
      }
    };
    return selectionAdapter;
  }


  /**
   * This method defines the activities to be performed when double clicked on the table
   *
   * @param functionListTableViewer2
   */
  private void addDoubleClickListener() {
    this.varTbleViewer.addDoubleClickListener(new IDoubleClickListener() {

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


}
