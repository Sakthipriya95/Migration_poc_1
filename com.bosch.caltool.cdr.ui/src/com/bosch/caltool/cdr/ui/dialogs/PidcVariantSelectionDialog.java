/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
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
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.cdr.MonicaFileData;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.providers.ReviewDataSelectionLabelProvider;
import com.bosch.caltool.icdm.common.ui.sorter.SelectionGridTabViewerSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.DataFilter;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author bru2cob
 */
public class PidcVariantSelectionDialog extends AbstractDialog {

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
   * Filter instance
   */
  private DataFilter filters;
  /**
   * GridTableViewer instance for selection of variant or workpackage
   */
  private GridTableViewer variantTableViewer;


  /**
   * instance for Columns sortting
   */
  private SelectionGridTabViewerSorter tabSorter;

  private final Long pidcA2lId;
  private Long pidcVersId;

  /**
   * Selected variant
   */
  private PidcVariant selectedVariant;

  private SortedSet<PidcVariant> pidcVarSet = new TreeSet<>();
  /**
   * selected Varaint list
   */
  private boolean multiSel;

  private final Set<PidcVariant> selectedPidcVariants = new HashSet<>();
  private MonicaFileData monicaDataProvider;
  private MonicaReviewDialog monicaRvwDialog;


  /**
   * @param parentShell parent
   * @param pidcA2lId PidcA2l ID
   */
  public PidcVariantSelectionDialog(final Shell parentShell, final Long pidcA2lId) {
    super(parentShell);
    this.pidcA2lId = pidcA2lId;
  }

  /**
   * @param parentShell parent
   * @param pidcVersId pidc Version Id
   * @param pidcA2lId PidcA2l ID
   */
  public PidcVariantSelectionDialog(final Shell parentShell, final Long pidcVersId, final Long pidcA2lId) {
    super(parentShell);
    this.pidcVersId = pidcVersId;
    this.pidcA2lId = pidcA2lId;
  }

  /**
   * @return the variantTableViewer
   */
  public GridTableViewer getVariantTableViewer() {
    return this.variantTableViewer;
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
    setTitle("Select variant");

    // Set the message
    setMessage("select variant", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Select variant");
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
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "List of variants");
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    // Create Filter text
    createFilterTxt();
    // Invokde GridColumnViewer sorter
    invokeColumnSorter();
    // Create new users grid tableviewer
    createVariantWorkpackageGridTabViewer();
    this.variantTableViewer.setComparator(this.tabSorter);
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


    this.form.getBody().setLayout(new GridLayout());

  }

  /**
   * ICDM 574-This method defines the activities to be performed when double clicked on the table
   *
   * @param functionListTableViewer2
   */
  private void addDoubleClickListener() {
    this.variantTableViewer
        .addDoubleClickListener(event -> Display.getDefault().asyncExec(PidcVariantSelectionDialog.this::okPressed));
  }

  /**
   * Add sorter for the table columns
   */
  private void invokeColumnSorter() {
    this.tabSorter = new SelectionGridTabViewerSorter();

  }

  /**
   * This method adds selection listener to the addNewUserTableViewer
   */
  private void addTableSelectionListener() {
    this.variantTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) PidcVariantSelectionDialog.this.variantTableViewer.getSelection();
        if ((selection != null) && (selection.size() == 1)) {
          PidcVariantSelectionDialog.this.selectedVariant = (PidcVariant) selection.getFirstElement();
          PidcVariantSelectionDialog.this.okBtn.setEnabled(true);
        }

        else if ((selection != null) && (selection.size() > 1)) {
          for (Object pidcVariant : selection.toList()) {
            PidcVariantSelectionDialog.this.selectedPidcVariants.add((PidcVariant) pidcVariant);
            PidcVariantSelectionDialog.this.okBtn.setEnabled(true);
          }

        }
        else {
          PidcVariantSelectionDialog.this.okBtn.setEnabled(false);
        }
      }
    });
  }

  /**
   * This method sets ContentProvider & LabelProvider to the addNewUserTableViewer
   */
  private void setTabViewerProviders() {
    this.variantTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.variantTableViewer.setLabelProvider(new ReviewDataSelectionLabelProvider());
  }

  /**
   * This method sets the input to the addNewUserTableViewer
   */
  private void setTabViewerInput() {

    if ((this.pidcVarSet == null) || this.pidcVarSet.isEmpty()) {
      this.pidcVarSet = new TreeSet<>();
      PidcVariantServiceClient service = new PidcVariantServiceClient();
      try {
        Map<Long, PidcVariant> varMap = this.pidcA2lId == null ? service.getVariantsForVersion(this.pidcVersId, false)
            : service.getA2lMappedVariants(this.pidcA2lId);
        this.pidcVarSet.addAll(varMap.values());
        this.variantTableViewer.setInput(this.pidcVarSet);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }

  }

  /**
   * This method adds the filter instance to addNewUserTableViewer
   */
  private void addFilters() {
    this.filters = new DataFilter();
    // Add PIDC Attribute TableViewer filter
    this.variantTableViewer.addFilter(this.filters);

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
   * This method creates filter text
   */
  private void createFilterTxt() {
    final Text filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "type filter text");

    filterTxt.addModifyListener(event -> {
      String text = filterTxt.getText().trim();
      PidcVariantSelectionDialog.this.filters.setFilterText(text);
      PidcVariantSelectionDialog.this.variantTableViewer.refresh();
    });

    filterTxt.setFocus();

  }

  /**
   * This method creates the variantTableViewer
   *
   * @param gridData
   */
  private void createVariantWorkpackageGridTabViewer() {
    int style = SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL;
    if (this.multiSel) {// TODO is this needed?
      style = style | SWT.MULTI;
    }
    this.variantTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(), style,
        GridDataUtil.getInstance().getHeightHintGridData(200));
    // Create GridViewerColumns
    createGridViewerColumns();
  }

  /**
   * This method adds Columns to the variantTableViewer
   */
  private void createGridViewerColumns() {
    createNameColumn();
    createDescriptionColumn();
  }

  /**
   * This method adds name column to the variantTableViewer
   */
  private void createNameColumn() {
    GridViewerColumn nameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.variantTableViewer, "Name", 200);
    // Add column selection listener

    nameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        nameColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_0, this.tabSorter, this.variantTableViewer));
  }

  /**
   * This method adds name column to the variantTableViewer
   */
  private void createDescriptionColumn() {
    GridViewerColumn descriptionColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.variantTableViewer, "Description", 200);
    // Add column selection listener
    descriptionColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        descriptionColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_0, this.tabSorter, this.variantTableViewer));
  }


  /**
   * @return the selectedVariant
   */
  public PidcVariant getSelectedVariant() {
    return this.selectedVariant;
  }


  /**
   * @param multiSel the multiSel to set
   */
  public void setMultiSel(final boolean multiSel) {
    this.multiSel = multiSel;
  }

  @Override
  public void okPressed() {
    if ((null != this.selectedVariant) && (null != this.monicaDataProvider) &&
        this.monicaRvwDialog.checkForDuplicateEntryForVariants(this.selectedVariant, this.monicaDataProvider)) {
      CDMLogger.getInstance().warnDialog("Duplicate entry not allowed in MoniCa Review, So the variant is not added.",
          Activator.PLUGIN_ID);
      return;
    }
    super.okPressed();
  }

  /**
   * @return the selectedPidcVariants
   */
  public Set<PidcVariant> getSelectedPidcVariants() {
    return new HashSet<>(this.selectedPidcVariants);
  }


  /**
   * @param provider MonicaFileData
   */
  public void setDataProvider(final MonicaFileData provider) {
    this.monicaDataProvider = provider;
  }

  /**
   * @param monicaReviewDialog monicaRvwDialog
   */
  public void setMonicaDialog(final MonicaReviewDialog monicaReviewDialog) {
    this.monicaRvwDialog = monicaReviewDialog;

  }

  @Override
  protected void cancelPressed() {
    this.selectedVariant = null;
    super.cancelPressed();
  }
}
