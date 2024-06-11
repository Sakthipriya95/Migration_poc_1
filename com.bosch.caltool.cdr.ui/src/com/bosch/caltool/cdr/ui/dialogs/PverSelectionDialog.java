/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
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

import com.bosch.caltool.cdr.ui.sorters.PverTabViewerSorter;
import com.bosch.caltool.cdr.ui.table.filters.PverSelectionFilter;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CompliReviewUsingHexData;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author bru2cob
 */
public class PverSelectionDialog extends AbstractDialog {

  /**
   * Composite instance for the dialog
   */
  private Composite composite;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;

  /**
   * Composite instance
   */
  private Composite top;
  private GridTableViewer tabViewer;
  /**
   * Filter text instance
   */
  private Text filterTxt;
  private String selectedObject;
  private final SortedSet<String> inputPverNameSet;
  private PverSelectionFilter pverFilter;
  private AbstractViewerSorter pverTabSorter;
  /**
   * Add ok button instance
   */
  private Button okBtn;
  private final PidcVersion selPidcVrsn;
  private boolean pverRvsn;
  private boolean pverVar;
  CompliReviewUsingHexData compliHexData;
  private boolean isSDOM;

  // SDOM Pver Name Set
  Set<String> sdomPverNames;


  /**
   * @param parentShell
   * @param inputPverNameSet
   * @param selPidcVrsn
   * @param compliHexData
   */
  public PverSelectionDialog(final Shell parentShell, final SortedSet<String> inputPverNameSet,
      final PidcVersion selPidcVrsn, final CompliReviewUsingHexData compliHexData) {
    super(parentShell);
    this.inputPverNameSet = inputPverNameSet;
    this.selPidcVrsn = selPidcVrsn;
    this.compliHexData = compliHexData;
    if (compliHexData != null) {
      this.sdomPverNames = this.compliHexData.getPidcSdomPverSet();
    }
    else {
      this.sdomPverNames = this.inputPverNameSet;
    }

  }

  /**
   * @param String
   * @return true or false based on the selected pver name is in the list
   */
  public boolean checkIsSDOM(final String selectedPverName) {
    return this.inputPverNameSet.contains(selectedPverName);
  }

  /**
   * Configures the shell
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Pver value selection");
    super.configureShell(newShell);
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
    setTitle("Pver value");
    // set msg
    setMessage("Select the pver value");
    return contents;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
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
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(gridData);
    this.section.getDescriptionControl().setEnabled(false);
  }

  /**
   * Creates the buttons for the button bar
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Select the value");
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.pverTabSorter = new PverTabViewerSorter();
    final GridLayout gridLayout = new GridLayout();
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    // Create Filter text
    createFilterTxt();

    // Create pver selection grid tableviewer
    createPverTabViewer();

    this.tabViewer.setContentProvider(ArrayContentProvider.getInstance());


    if ((null != this.selPidcVrsn) && !this.pverRvsn && !this.pverVar) {
      filterPVerNamesForPIDCVersion();
    }
    else {
      this.tabViewer.setInput(this.inputPverNameSet);
    }
    this.tabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) PverSelectionDialog.this.tabViewer.getSelection();
        if ((selection != null) && (!selection.isEmpty())) {
          PverSelectionDialog.this.selectedObject = (String) selection.getFirstElement();
          PverSelectionDialog.this.okBtn.setEnabled(true);
        }
        else {
          PverSelectionDialog.this.okBtn.setEnabled(false);
        }


      }
    });

    // Adds double click selection listener to the addNewUserTableViewer
    addDoubleClickListener();

    // Add filters to the TableViewer
    addFilters();

    this.tabViewer.setComparator(this.pverTabSorter);

  }

  /**
   *
   */
  private void filterPVerNamesForPIDCVersion() {
    SortedSet<String> pverNamesSortedSet = new TreeSet<>();
    if ((null != this.selPidcVrsn) && !this.pverRvsn && !this.pverVar) {
      for (String pverName : this.sdomPverNames) {
        pverNamesSortedSet.add(pverName);
      }
      this.tabViewer.setInput(pverNamesSortedSet);

    }
    else {
      this.tabViewer.setInput(this.inputPverNameSet);
    }
  }

  /**
   *
   */
  private void addFilters() {
    this.pverFilter = new PverSelectionFilter();

    this.tabViewer.addFilter(this.pverFilter);

  }

  /**
   *
   */
  private void addDoubleClickListener() {
    this.tabViewer.addDoubleClickListener(new IDoubleClickListener() {

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
   *
   */
  private void createPverTabViewer() {
    this.tabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(200));

    final GridViewerColumn nameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tabViewer, "Name", 200);

    nameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof String) {
          String dispValue = (String) element;
          return dispValue;
        }
        else if (element instanceof Long) {
          Long dispValue = (Long) element;
          return dispValue.toString();
        }
        return "";
      }


    });
    // Add column selection listener
    nameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(nameColumn.getColumn(), ApicConstants.COLUMN_INDEX_0, this.pverTabSorter, this.tabViewer));
  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);
    this.filterTxt.addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void modifyText(final ModifyEvent event) {
        final String text = PverSelectionDialog.this.filterTxt.getText().trim();
        PverSelectionDialog.this.pverFilter.setFilterText(text);
        PverSelectionDialog.this.tabViewer.refresh();
      }
    });
    this.filterTxt.setFocus();

  }


  /**
   * @return the selectedObject
   */
  public String getSelectedObject() {
    return this.selectedObject;
  }


  /**
   * @param selectedObject the selectedObject to set
   */
  public void setSelectedObject(final String selectedObject) {
    this.selectedObject = selectedObject;
  }


  /**
   * @return the pverRvsn
   */
  public boolean isPverRvsn() {
    return this.pverRvsn;
  }


  /**
   * @param pverRvsn the pverRvsn to set
   */
  public void setPverRvsn(final boolean pverRvsn) {
    this.pverRvsn = pverRvsn;
  }


  /**
   * @return the pverVar
   */
  public boolean isPverVar() {
    return this.pverVar;
  }


  /**
   * @param pverVar the pverVar to set
   */
  public void setPverVar(final boolean pverVar) {
    this.pverVar = pverVar;
  }


}
