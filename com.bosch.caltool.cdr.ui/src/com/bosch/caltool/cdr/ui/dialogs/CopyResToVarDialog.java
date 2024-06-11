/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import com.bosch.caltool.cdr.ui.sorters.CopyResToVarTabViewerSorter;
import com.bosch.caltool.cdr.ui.table.filters.CopyResToVarTabFilter;
import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * Class to attach a result to different variant
 *
 * @author bru2cob
 */
public class CopyResToVarDialog extends AbstractDialog {

  /**
   * Instance of selected PIDC version
   */
  PidcVersion selectedPidcVer;
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
  private CopyResToVarTabFilter filters;
  /**
   * GridTableViewer instance for selection of variant or workpackage
   */
  private GridTableViewer variantTableViewer;

  /**
   * @return the variantTableViewer
   */
  public GridTableViewer getVariantTableViewer() {
    return this.variantTableViewer;
  }


  /**
   * instance for Columns sortting
   */
  private CopyResToVarTabViewerSorter tabSorter;

  /**
   * Selected variant
   */
  PidcVariant selectedVariant;

  /**
   * pidc varaints
   */
  private SortedSet<PidcVariant> pidcVarSet = new TreeSet<>();
  /**
   * a2l mapped pidc varaints
   */
  private SortedSet<PidcVariant> a2lMappedVariants = new TreeSet<>();
  /**
   * selected Varaint list
   */
  private List<PidcVariant> selVariants = new ArrayList<>();
  /**
   * boolean for multiple selection
   */
  private boolean multiSel;
  /**
   * Set of variants without dep match
   */
  Set<PidcVariant> varWithoutDepMatch;
  /**
   * Set of linked pidc variants
   */
  Set<PidcVariant> linkedVars;
  /**
   * Dependant vairants map
   */
  Map<PidcVariant, Set<String>> varDependantAttrMap;
  /**
   * Varaints with diff pver
   */
  SortedSet<PidcVariant> varWithDiffPver;
  /**
   * the map contains var id as key and same var group status as value
   */
  private final Map<Long, Boolean> sameVarGrpStatusMap;


  /**
   * @param parentShell parent
   * @param selectedPidcVer selected version
   * @param a2lMappedVariants
   * @param a2lFile
   * @param varWithoutDepMatch
   * @param linkedVars
   * @param varDependantAttr
   * @param varWithDiffPver
   * @param sameVarGrpStatusMap
   */
  public CopyResToVarDialog(final Shell parentShell, final PidcVersion selectedPidcVer,
      final SortedSet<PidcVariant> a2lMappedVariants, final Set<PidcVariant> linkedVars,
      final Set<PidcVariant> varWithoutDepMatch, final Map<PidcVariant, Set<String>> varDependantAttr,
      final SortedSet<PidcVariant> varWithDiffPver, final Map<Long, Boolean> sameVarGrpStatusMap) {
    super(parentShell);
    this.selectedPidcVer = selectedPidcVer;
    this.a2lMappedVariants = a2lMappedVariants;
    this.varWithoutDepMatch = varWithoutDepMatch;
    this.varDependantAttrMap = varDependantAttr;
    this.varWithDiffPver = varWithDiffPver;
    this.linkedVars = linkedVars;
    this.sameVarGrpStatusMap = sameVarGrpStatusMap;
  }

  /**
   * Sets the Dialog Resizable
   *
   * @param newShellStyle
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
    this.variantTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    ColumnViewerToolTipSupport.enableFor(this.variantTableViewer, ToolTip.NO_RECREATE);
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
    this.variantTableViewer.addDoubleClickListener(new IDoubleClickListener() {

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
   * Add sorter for the table columns
   */
  private void invokeColumnSorter() {
    this.tabSorter = new CopyResToVarTabViewerSorter(this);

  }

  /**
   * This method adds selection listener to the addNewUserTableViewer
   */
  private void addTableSelectionListener() {
    this.variantTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) CopyResToVarDialog.this.variantTableViewer.getSelection();
        CopyResToVarDialog.this.okBtn.setEnabled(true);
        if ((selection != null) && (selection.size() != 0)) {
          setSelVaraints(selection);
          if (selection.size() == 1) {
            CopyResToVarDialog.this.selectedVariant = (PidcVariant) selection.getFirstElement();
            setSelectedVariant(CopyResToVarDialog.this.selectedVariant);

            CopyResToVarDialog.this.okBtn
                .setEnabled(!(CopyResToVarDialog.this.linkedVars.contains(CopyResToVarDialog.this.selectedVariant) ||
                    CopyResToVarDialog.this.varWithoutDepMatch.contains(CopyResToVarDialog.this.selectedVariant)));

          }
        }
        else {
          CopyResToVarDialog.this.okBtn.setEnabled(false);
        }
      }
    });
  }

  /**
   * @param selection selection from the table
   */
  protected void setSelVaraints(final IStructuredSelection selection) {
    this.selVariants = new ArrayList<PidcVariant>();
    List<Object> list = selection.toList();
    for (Object object : list) {
      this.selVariants.add((PidcVariant) object);
    }

  }


  /**
   * This method sets the input to the addNewUserTableViewer
   */
  private void setTabViewerInput() {
    if (CommonUtils.isNull(this.pidcVarSet)) {
      this.pidcVarSet = this.a2lMappedVariants;
    }

    this.variantTableViewer.setInput(this.pidcVarSet);
  }

  /**
   * @param pidcVarSet pidcVarSet
   */
  public void setInpData(final SortedSet<PidcVariant> pidcVarSet) {
    if (null != pidcVarSet) {
      this.pidcVarSet.addAll(pidcVarSet);
    }
  }


  /**
   * This method adds the filter instance to addNewUserTableViewer
   */
  private void addFilters() {
    this.filters = new CopyResToVarTabFilter(this);
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
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if (this.okBtn.getEnabled()) {
      super.okPressed();
    }
  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    final Text filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "type filter text");
    filterTxt.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        String text = filterTxt.getText().trim();
        CopyResToVarDialog.this.filters.setFilterText(text);
        CopyResToVarDialog.this.variantTableViewer.refresh();
      }
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
    if (this.multiSel) {
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
    createDetailsColumn();
  }

  /**
   * This method adds name column to the variantTableViewer
   */
  private void createNameColumn() {
    GridViewerColumn nameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.variantTableViewer, "Name", 200);
    nameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof PidcVariant) {
          PidcVariant variant = (PidcVariant) element;
          return variant.getName();
        }
        return "";
      }


    });
    // Add column selection listener

    nameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        nameColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_0, this.tabSorter, this.variantTableViewer));


  }

  /**
   * This method adds name column to the variantTableViewer
   */
  private void createDetailsColumn() {
    GridViewerColumn detailsColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.variantTableViewer, "Details", 280);
    detailsColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof PidcVariant) {
          PidcVariant variant = (PidcVariant) element;
          if (CopyResToVarDialog.this.linkedVars.contains(variant)) {
            return CdrUIConstants.LINKED_RESULT;
          }
          else if (CopyResToVarDialog.this.varWithoutDepMatch.contains(variant)) {
            if (CopyResToVarDialog.this.varWithDiffPver.contains(variant)) {
              return CdrUIConstants.DIFFERENT_SDOM_PVER;
            }
            return CdrUIConstants.DEPENDANT_NOT_SET;
          }
          else if ((null != CopyResToVarDialog.this.sameVarGrpStatusMap) &&
              CopyResToVarDialog.this.sameVarGrpStatusMap.containsKey(variant.getId()) &&
              !CopyResToVarDialog.this.sameVarGrpStatusMap.get(variant.getId())) {
            return CdrUIConstants.DIFFERENT_VAR_GROUP;
          }
        }
        return "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        if (element instanceof PidcVariant) {
          PidcVariant variant = (PidcVariant) element;
          StringBuilder depenAttr = new StringBuilder();
          Set<String> attrSet = CopyResToVarDialog.this.varDependantAttrMap.get(variant);
          if ((attrSet != null) && !attrSet.isEmpty()) {
            depenAttr.append("Dependant attributes : \n");
            for (String attr : attrSet) {
              depenAttr.append(attr).append("\n");
            }
            return depenAttr.toString();
          }
          if (!CopyResToVarDialog.this.sameVarGrpStatusMap.containsKey(variant.getId())) {
            return "The variant group of the target variant is different.  Linking the review is possible, but the responsibilities and WPs of the source variant group is used.";
          }
        }
        return null;
      }
    });
    // Add column selection listener
    detailsColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        detailsColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_2, this.tabSorter, this.variantTableViewer));
  }

  /**
   * This method adds name column to the variantTableViewer
   */
  private void createDescriptionColumn() {
    GridViewerColumn descriptionColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.variantTableViewer, "Description", 200);
    descriptionColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof PidcVariant) {
          PidcVariant variant = (PidcVariant) element;
          return variant.getDescription();
        }
        return "";
      }


    });
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
   * @param selectedVariant the selectedVariant to set
   */
  public void setSelectedVariant(final PidcVariant selectedVariant) {
    this.selectedVariant = selectedVariant;
  }

  /**
   * @return the selVariants
   */
  public List<PidcVariant> getSelVariants() {
    return this.selVariants;
  }

  /**
   * @param multiSel multiSel
   */
  public void setStyle(final boolean multiSel) {
    this.multiSel = multiSel;

  }


  /**
   * @return the varWithoutDepMatch
   */
  public Set<PidcVariant> getVarWithoutDepMatch() {
    return this.varWithoutDepMatch;
  }


  /**
   * @return the linkedVars
   */
  public Set<PidcVariant> getLinkedVars() {
    return this.linkedVars;
  }


  /**
   * @return the varWithDiffPver
   */
  public SortedSet<PidcVariant> getVarWithDiffPver() {
    return this.varWithDiffPver;
  }


  /**
   * @return the sameVarGrpStatusMap
   */
  public Map<Long, Boolean> getSameVarGrpStatusMap() {
    return this.sameVarGrpStatusMap;
  }
}
