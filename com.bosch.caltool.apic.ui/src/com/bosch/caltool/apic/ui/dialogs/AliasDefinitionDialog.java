package com.bosch.caltool.apic.ui.dialogs;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.AliasDefEditorInput;
import com.bosch.caltool.apic.ui.table.filters.AliasDefinitionFilter;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.ws.rest.client.apic.AliasDefServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * This class provides a dialog to select a function
 */
public class AliasDefinitionDialog extends AbstractDialog {


  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * GridTableViewer
   */
  private GridTableViewer aliasDefTabViewer;
  /**
   * Text
   */
  private Text filterTxt;

  /**
   * Section
   */
  private Section sectionOne;
  /**
   * Form
   */
  private Form formOne;

  /**
   * Button
   */
  private Button okBtn;


  // ICDM-853
  /**
   * list of alias def
   */
  private final List<AliasDef> selAliasDefintions = new ArrayList<>();

  /**
   * alias Definition name Filter
   */
  private AliasDefinitionFilter aliasDefFilter;


  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   */
  public AliasDefinitionDialog(final Shell parentShell) {
    super(parentShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Select Alias Definitions");
    newShell.setSize(500, 500);
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
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

    final Composite composite = new Composite(parent, SWT.None);
    // initailise the size of the dialog
    initializeDialogUnits(composite);

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    parent.setLayout(gridLayout);
    parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    composite.setLayout(gridLayout);
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    // create section
    createSectionOne(getFormToolkit(), gridLayout, composite);

    // create button bar
    createButtonBar(parent);
    return composite;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    // create ok and cancel buttons
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, "Select", false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * create section
   *
   * @param toolkit This method initializes sectionOne
   * @param gridLayout
   * @param composite
   */
  private void createSectionOne(final FormToolkit toolkit, final GridLayout gridLayout, final Composite composite) {
    this.sectionOne = toolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionOne.setText("Alias Definitions");
    this.sectionOne.setDescription("List of Alias Definitions ");
    this.sectionOne.setExpanded(true);
    this.sectionOne.getDescriptionControl().setEnabled(false);
    // create form
    createFormOne(toolkit, gridLayout);
    this.sectionOne.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.sectionOne.setClient(this.formOne);
  }

  /**
   * This method initializes formOne
   *
   * @param gridLayout
   */
  private void createFormOne(final FormToolkit toolkit, final GridLayout gridLayout) {

    this.formOne = toolkit.createForm(this.sectionOne);
    this.formOne.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.aliasDefFilter = new AliasDefinitionFilter();

    // create filter text
    createFilterTxt();
    // ICDM-853
    // create table
    this.aliasDefTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.formOne.getBody(),
        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FILL | SWT.BORDER, GridDataUtil.getInstance().getGridData());

    this.aliasDefTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    // create columns
    final GridViewerColumn funcName = new GridViewerColumn(this.aliasDefTabViewer, SWT.NONE);
    funcName.getColumn().setText("Alias Defintion Name");
    funcName.getColumn().setWidth(450);
    funcName.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return ((AliasDef) element).getName();
      }
    });

    try {
      // call web service to get the alias def list
      SortedSet<AliasDef> aliasDefSet = new TreeSet<>(new AliasDefServiceClient().getAll().values());
      this.aliasDefTabViewer.setInput(aliasDefSet);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    // add filter
    this.aliasDefTabViewer.addFilter(this.aliasDefFilter);
    // add selection listener
    addTableSelectionListener(this.aliasDefTabViewer);
    // adding double click listener
    addDoubleClickListener(this.aliasDefTabViewer);
    // set layout
    this.formOne.getBody().setLayout(gridLayout);
  }

  /**
   * ICDM 574-This method defines the activities to be performed when double clicked on the table
   *
   * @param functionListTableViewer2
   */
  private void addDoubleClickListener(final GridTableViewer tableviewer) {
    tableviewer.addDoubleClickListener(evt -> Display.getDefault().asyncExec(() -> okPressed()));
  }


  /**
   * This method adds selection listener to the TableViewer
   */
  private void addTableSelectionListener(final GridTableViewer tableviewer) {
    tableviewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        // ge teh selection
        final IStructuredSelection selection = (IStructuredSelection) tableviewer.getSelection();
        // ICDM-853
        if (CommonUtils.isNotNull(selection)) {
          AliasDefinitionDialog.this.selAliasDefintions.clear();
          @SuppressWarnings("unchecked")
          final Iterator<AliasDef> definitions = selection.iterator();
          while (definitions.hasNext()) {
            // iterate through the definitions
            final AliasDef aliasDef = definitions.next();
            AliasDefinitionDialog.this.selAliasDefintions.add(aliasDef);
            AliasDefinitionDialog.this.okBtn.setEnabled(true);
          }
        }
        else {
          // if there is no selection
          AliasDefinitionDialog.this.okBtn.setEnabled(false);
          AliasDefinitionDialog.this.selAliasDefintions.clear();
        }
      }
    });
  }


  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.formOne.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "");

    this.filterTxt.addModifyListener(event -> {
      // when there is a modification in the text filter, set the text to the filter
      final String text = AliasDefinitionDialog.this.filterTxt.getText().trim();
      AliasDefinitionDialog.this.aliasDefFilter.setFilterText(text);
      // refresh to filter the table based on table
      AliasDefinitionDialog.this.aliasDefTabViewer.refresh();
    });
    // set focus to type filter
    this.filterTxt.setFocus();

  }


  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      // create the form tool kit
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    // iterate through all the selected alias definitions
    for (AliasDef aliasDef : this.selAliasDefintions) {
      // create editor input
      AliasDefEditorInput editorInp = new AliasDefEditorInput(aliasDef);
      try {
        if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
          // find editor
          IEditorPart aliasEditor =
              PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(editorInp);
          if (aliasEditor == null) {
            // open alias editor
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInp,
                AliasDefEditorInput.EDITOR_ID);
          }
        }
      }
      catch (PartInitException ex) {
        CDMLogger.getInstance().error(ex.getLocalizedMessage(), ex, Activator.PLUGIN_ID);
      }
    }
    super.okPressed();
  }
}