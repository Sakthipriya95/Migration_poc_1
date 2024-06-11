/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.ICDMClipboard;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.ui.sorters.WorkpackageTableViewerSorter;
import com.bosch.caltool.icdm.ui.table.filters.WorkpackageTableFilters;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author say8cob
 */
public class PasteQuestionnaireResponseDialog extends AbstractDialog {

  /** Composite instance. */
  private Composite composite;

  /** Composite instance. */
  private Composite top;

  /** FormToolkit instance. */
  private FormToolkit formToolkit;

  /** Section instance. */
  private Section section;

  /** Form instance. */
  private Form form;

  /** Filter text instance. */
  private Text filterTxt;

  /** Filter instance. */
  private WorkpackageTableFilters filters;

  /** GridTableViewer instance for selection of variant or workpackage. */
  private GridTableViewer wrkPkgTableViewer;

  /** instance for Columns sortting. */
  private WorkpackageTableViewerSorter tabSorter;

  /** Constant string. */
  private static final String SELECT_WORK_PACKAGE = "Select Work Package";

  /** Selected icdm wp. */
  private Set<A2lWorkPackage> selectedWorkPackageSet = new HashSet();

  private final PidcTreeNode pidcTreeNode;

  private final Map<Long, A2lWorkPackage> a2lWpMap = new HashMap<>();

  private A2lWorkPackage grayedOutWorkPackage = null;

  /**
   * Instantiates a new questionnaire name sel dialog.
   *
   * @param parentShell the parent shell
   * @param pidcTreeNode pidctreenode
   */
  public PasteQuestionnaireResponseDialog(final Shell parentShell, final PidcTreeNode pidcTreeNode) {
    super(parentShell);
    this.pidcTreeNode = pidcTreeNode;
    fillInputData();
  }

  /**
  *
  */
  private void fillInputData() {
    A2lResponsibility a2lResponsibility = this.pidcTreeNode.getA2lResponsibility();
    PidcTreeNode parentNode = this.pidcTreeNode.getParentNode();
    PidcTreeNode rvwQnaire = parentNode.getParentNode();
    Map<Long, Map<Long, Set<Long>>> respWpMap =
        CommonUtils.isEqual(this.pidcTreeNode.getNodeType(), PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE)
            ? this.pidcTreeNode.getA2lStructureModel().getVarRespWpQniareMap().get(parentNode.getPidcVariant().getId())
            : rvwQnaire.getQnaireInfo().getVarRespWpQniareMap().get(parentNode.getPidcVariant().getId());

    for (Entry<Long, Map<Long, Set<Long>>> respWPEntrySet : respWpMap.entrySet()) {
      if (CommonUtils.isEqual(a2lResponsibility.getId(), respWPEntrySet.getKey())) {
        Map<Long, Set<Long>> wpQnaireRespMap = respWPEntrySet.getValue();
        for (Entry<Long, Set<Long>> wpQnaireRespEntrySet : wpQnaireRespMap.entrySet()) {
          Long wpId = wpQnaireRespEntrySet.getKey();
          A2lWorkPackage a2lWorkPackage =
              CommonUtils.isEqual(this.pidcTreeNode.getNodeType(), PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE)
                  ? this.pidcTreeNode.getA2lStructureModel().getA2lWpMap().get(wpId)
                  : rvwQnaire.getQnaireInfo().getA2lWpMap().get(wpId);
          this.a2lWpMap.put(a2lWorkPackage.getId(), a2lWorkPackage);
        }
      }
    }

    PidcTreeNode copiedObject = (PidcTreeNode) ICDMClipboard.getInstance().getCopiedObject();
    A2lWorkPackage a2lWPCopied = copiedObject.getA2lWorkpackage();
    A2lResponsibility a2lRespCopied = copiedObject.getA2lResponsibility();

    for (Entry<Long, A2lWorkPackage> a2lWpEntry : this.a2lWpMap.entrySet()) {
      if (a2lRespCopied.equals(a2lResponsibility) && a2lWpEntry.getValue().equals(a2lWPCopied)) {
        this.grayedOutWorkPackage = a2lWpEntry.getValue();
      }
    }

  }

  /**
   * Sets the Dialog Resizable.
   *
   * @param newShellStyle the new shell style
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM | SWT.MAX);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }


  /**
   * Creates the dialog's contents.
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);

    // Set the title
    setTitle("Paste to Multiple Work packages");

    // Set the message
    setMessage("Please Select the Work packages.", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(SELECT_WORK_PACKAGE);
    super.configureShell(newShell);
    super.setHelpAvailable(true);
  }

  /**
   * Creates the gray area.
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    // create composite
    createComposite();
    return this.top;
  }

  /**
   * This method initializes composite.
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    // create section
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes formToolkit.
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
   * This method initializes section.
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "List of Work Packages");
    // create form
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form.
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(new GridLayout());
    // Invokde GridColumnViewer sorter
    // Add filters to the TableViewer
    this.tabSorter = new WorkpackageTableViewerSorter();

    // Add filters to the TableViewer
    this.filters = new WorkpackageTableFilters(this);
    // Create Filter text
    createFilterTxt();


    // Create new users grid tableviewer
    createVariantWorkpackageGridTabViewer();

    // set filter
    this.wrkPkgTableViewer.addFilter(this.filters);
    // set table sorter
    this.wrkPkgTableViewer.setComparator(this.tabSorter);


    // Set ContentProvider and LabelProvider to addNewUserTableViewer
    this.wrkPkgTableViewer.setContentProvider(ArrayContentProvider.getInstance());


    // Set input to the addNewUserTableViewer
    setTabViewerInput();

    // set form layout
    this.form.getBody().setLayout(new GridLayout());

  }


  /**
   * This method sets the input to the addNewUserTableViewer.
   */
  private void setTabViewerInput() {
    // set table viewer input
    this.wrkPkgTableViewer.setInput(getA2lWpMap().values());

  }


  /**
   * This method creates filter text.
   */
  private void createFilterTxt() {
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "type filter text");
    this.filterTxt.addModifyListener(event -> {
      String text = PasteQuestionnaireResponseDialog.this.filterTxt.getText().trim();
      PasteQuestionnaireResponseDialog.this.filters.setFilterText(text);
      PasteQuestionnaireResponseDialog.this.wrkPkgTableViewer.refresh();
    });
    this.filterTxt.setFocus();

  }

  /**
   * This method creates the variantTableViewer.
   */
  private void createVariantWorkpackageGridTabViewer() {
    this.wrkPkgTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, GridDataUtil.getInstance().getHeightHintGridData(200));
    // 261043- Resize the Work pkg dialog
    // Create GridViewerColumns
    createGridViewerColumns();
  }


  /**
   * This method adds Columns to the variantTableViewer.
   */
  private void createGridViewerColumns() {
    // create group col
    GridViewerColumn workPkgColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.wrkPkgTableViewer, "Work Package", 350);
    // Add column selection listener
    workPkgColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        workPkgColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_0, this.tabSorter, this.wrkPkgTableViewer));
    setWorkpackageColLabelProvider(workPkgColumn);

    // create name col
    GridViewerColumn selectionColumn =
        GridViewerColumnUtil.getInstance().createGridViewerCheckStyleColumn(this.wrkPkgTableViewer, "", 50);
    // Add column selection listener
    selectionColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        selectionColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_1, this.tabSorter, this.wrkPkgTableViewer));
    setSelectFlagColLabelProvider(selectionColumn);

    selectionColumn.setEditingSupport(new CheckEditingSupport(selectionColumn.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        A2lWorkPackage a2lWorkPackage = (A2lWorkPackage) arg0;
        if (selectionColumn.getColumn().isCheck()) {
          PasteQuestionnaireResponseDialog.this.selectedWorkPackageSet.add(a2lWorkPackage);
        }
        else {
          PasteQuestionnaireResponseDialog.this.selectedWorkPackageSet.remove(a2lWorkPackage);
        }
      }
    });
  }

  /**
   * @param wpCol
   */
  private void setWorkpackageColLabelProvider(final GridViewerColumn wpCol) {
    wpCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return wpColGetText(element);
      }

      @Override
      public Color getForeground(final Object element) {
        return qNaireColGetForeground(element);
      }

      private String wpColGetText(final Object element) {
        A2lWorkPackage workpackage = (A2lWorkPackage) element;
        return workpackage.getName();
      }

      /**
       * @param element
       * @return
       */
      private Color qNaireColGetForeground(final Object element) {
        if (element instanceof A2lWorkPackage) {
          A2lWorkPackage qnaireResp = (A2lWorkPackage) element;
          if (((null != qnaireResp.getId()) &&
              (CommonUtils.isNotNull(PasteQuestionnaireResponseDialog.this.grayedOutWorkPackage)) &&
              qnaireResp.getName().startsWith(PasteQuestionnaireResponseDialog.this.grayedOutWorkPackage.getName())) ||
              CommonUtils.isEqual(ApicConstants.DEFAULT_A2L_WP_NAME, qnaireResp.getName())) {
            return PasteQuestionnaireResponseDialog.this.wrkPkgTableViewer.getGrid().getDisplay()
                .getSystemColor(SWT.COLOR_DARK_GRAY);
          }
        }
        return null;
      }
    });
  }

  /**
   * @param delFlagViewer
   */
  private void setSelectFlagColLabelProvider(final GridViewerColumn selectionFlagViewer) {
    selectionFlagViewer.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        if (element instanceof A2lWorkPackage) {
          A2lWorkPackage a2lWorkPackage = (A2lWorkPackage) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
          gridItem.setChecked(cell.getVisualIndex(), false);
          boolean isGrayed = CommonUtils.isNotNull(PasteQuestionnaireResponseDialog.this.grayedOutWorkPackage) &&
              a2lWorkPackage.getName().equals(PasteQuestionnaireResponseDialog.this.grayedOutWorkPackage.getName());
          gridItem.setGrayed(cell.getVisualIndex(), isGrayed);
          boolean isDefaultWP = CommonUtils.isEqual(ApicConstants.DEFAULT_A2L_WP_NAME, a2lWorkPackage.getName());
          gridItem.setCheckable(
              cell.getVisualIndex(),
              isDefaultWP ? !isDefaultWP : !isGrayed
          );

          gridItem.setGrayed(
              cell.getVisualIndex(),
              isDefaultWP ? isDefaultWP : false
          );
        }
      }


      @Override
      public Color getForeground(final Object element) {
        if (element instanceof A2lWorkPackage) {
          A2lWorkPackage qnaireResp = (A2lWorkPackage) element;
          if ((null != qnaireResp.getId()) &&
              qnaireResp.getName().startsWith(PasteQuestionnaireResponseDialog.this.grayedOutWorkPackage.getName())) {
            return PasteQuestionnaireResponseDialog.this.wrkPkgTableViewer.getGrid().getDisplay()
                .getSystemColor(SWT.COLOR_DARK_GRAY);
          }
        }
        return null;
      }
    });
  }


  /**
   * @return the a2lWpMap
   */
  public Map<Long, A2lWorkPackage> getA2lWpMap() {
    return this.a2lWpMap;
  }


  /**
   * @return the selectedWorkPackageSet
   */
  public Set<A2lWorkPackage> getSelectedWorkPackageSet() {
    return this.selectedWorkPackageSet;
  }


  /**
   * @param selectedWorkPackageSet the selectedWorkPackageSet to set
   */
  public void setSelectedWorkPackageSet(final Set<A2lWorkPackage> selectedWorkPackageSet) {
    this.selectedWorkPackageSet = selectedWorkPackageSet;
  }

}
