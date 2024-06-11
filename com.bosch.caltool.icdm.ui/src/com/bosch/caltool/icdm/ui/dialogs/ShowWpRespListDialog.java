/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
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
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefinitionModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.ui.sorters.WpRespListSorter;
import com.bosch.caltool.icdm.ui.table.filters.AvailableWorkPackagesFilter;
import com.bosch.caltool.icdm.ui.views.providers.WpRespListLabelProvider;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author elm1cob
 */
public class ShowWpRespListDialog extends AbstractDialog {

  private FormToolkit formToolkit;

  private GridTableViewer workPackagesViewer;

  private Text filterTxt;

  private Section sectionOne;

  private Form formOne;
  /**
   * Available WorkpackageFilter
   */
  private AvailableWorkPackagesFilter workPackageFilter;


  private final A2lWpDefinitionModel a2lWpDefinitionModel;

  private A2lWpResponsibility a2lWpResp;

  private Button cancelButton;
  private Button okButton;

  private IStructuredSelection selection;

  private final A2lResponsibilityModel a2lRespModel;

  private final A2LWPInfoBO a2lWpInfoBo;
  private final boolean showOnlyNameAtCust;
  private final List<A2lWpParamMapping> selA2lParamList;

  /**
   * Instantiates a new show wp resp list dialog.
   *
   * @param parentShell Shell
   * @param wpVersId A2LWPInfoBO
   * @param a2lWpInfoBo a2lWpInfoBo
   * @param showOnlyNameAtCust - flag to show only the list of name of customer values
   * @param selA2lParamList selected mapping in Label assignment page
   */
  public ShowWpRespListDialog(final Shell parentShell, final Long wpVersId, final A2LWPInfoBO a2lWpInfoBo,
      final boolean showOnlyNameAtCust, final List<A2lWpParamMapping> selA2lParamList) {
    super(parentShell);
    this.a2lWpDefinitionModel = a2lWpInfoBo.getA2lWpDefnModel();
    this.a2lRespModel = a2lWpInfoBo.getA2lResponsibilityModel();
    this.a2lWpInfoBo = a2lWpInfoBo;
    this.showOnlyNameAtCust = showOnlyNameAtCust;
    this.selA2lParamList = new ArrayList<>(selA2lParamList);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Title modified
    newShell.setText("Work Package List");
    newShell.setSize(850, 600);
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
    initializeDialogUnits(composite);

    final GridLayout gridLayout = new GridLayout();
    parent.setLayout(gridLayout);
    parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    composite.setLayout(gridLayout);
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    createSection(getFormToolkit(), gridLayout, composite);
    createButtonBar(parent);
    return composite;
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
   * This method creates filter text
   */
  private void createFilterTxt() {
    this.workPackageFilter = new AvailableWorkPackagesFilter(this.a2lWpInfoBo);
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.formOne.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "");

    this.filterTxt.addModifyListener(event -> {
      final String text = ShowWpRespListDialog.this.filterTxt.getText().trim();
      ShowWpRespListDialog.this.workPackageFilter.setFilterText(text);
      ShowWpRespListDialog.this.workPackagesViewer.refresh();
    });

    this.filterTxt.setFocus();

  }

  /**
   * @param toolkit This method initializes sectionOne
   * @param gridLayout
   * @param composite
   */
  private void createSection(final FormToolkit toolkit, final GridLayout gridLayout, final Composite composite) {

    this.sectionOne = toolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionOne.setText("Work Packages");
    this.sectionOne.setDescription("Select work package");
    this.sectionOne.setExpanded(true);
    this.sectionOne.getDescriptionControl().setEnabled(false);
    createForm(toolkit, gridLayout);
    this.sectionOne.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.sectionOne.setClient(this.formOne);
  }

  /**
   * This method initializes form
   */
  private void createForm(final FormToolkit toolkit, final GridLayout gridLayout) {

    this.formOne = toolkit.createForm(this.sectionOne);
    this.formOne.setLayoutData(GridDataUtil.getInstance().getGridData());

    createFilterTxt();
    this.workPackagesViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.formOne.getBody(),
        SWT.V_SCROLL | SWT.H_SCROLL | SWT.FILL | SWT.BORDER, GridDataUtil.getInstance().getGridData());

    addWpListTabSelectionListener();
    addDoubleClickListener();

    WpRespListSorter wpRespListSorter = new WpRespListSorter(this.a2lWpInfoBo);
    if (!this.showOnlyNameAtCust) {
      this.workPackagesViewer.setComparator(wpRespListSorter);
      final GridViewerColumn workPkg = new GridViewerColumn(this.workPackagesViewer, SWT.NONE);
      workPkg.getColumn().setText("Work Package");
      workPkg.getColumn().setWidth(250);
      workPkg.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
          workPkg.getColumn(), CommonUIConstants.COLUMN_INDEX_0, wpRespListSorter, this.workPackagesViewer));

      final GridViewerColumn resp = new GridViewerColumn(this.workPackagesViewer, SWT.NONE);
      resp.getColumn().setText("Responsibility");
      resp.getColumn().setWidth(220);
      resp.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(resp.getColumn(),
          CommonUIConstants.COLUMN_INDEX_1, wpRespListSorter, this.workPackagesViewer));

      final GridViewerColumn respType = new GridViewerColumn(this.workPackagesViewer, SWT.NONE);
      respType.getColumn().setText("Responsibility Type");
      respType.getColumn().setWidth(80);
      respType.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
          respType.getColumn(), CommonUIConstants.COLUMN_INDEX_2, wpRespListSorter, this.workPackagesViewer));


      final GridViewerColumn varGrp = new GridViewerColumn(this.workPackagesViewer, SWT.NONE);
      varGrp.getColumn().setText("Variant Group");
      varGrp.getColumn().setWidth(100);
      varGrp.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(varGrp.getColumn(),
          CommonUIConstants.COLUMN_INDEX_3, wpRespListSorter, this.workPackagesViewer));

    }
    final GridViewerColumn customerName = new GridViewerColumn(this.workPackagesViewer, SWT.NONE);
    customerName.getColumn().setText("Name at Customer");

    if (!this.showOnlyNameAtCust) {
      customerName.getColumn().setWidth(150);
      customerName.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
          customerName.getColumn(), CommonUIConstants.COLUMN_INDEX_4, wpRespListSorter, this.workPackagesViewer));
    }
    else {
      customerName.getColumn().setWidth(600);
      customerName.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
          customerName.getColumn(), CommonUIConstants.COLUMN_INDEX_4, null, this.workPackagesViewer));
    }

    this.formOne.getBody().setLayout(gridLayout);

    this.workPackagesViewer.setContentProvider(ArrayContentProvider.getInstance());

    this.workPackagesViewer.setFilters(this.workPackageFilter);
    setTableInput();
    // ICDM -588
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.workPackagesViewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes,
        new CustomDragListener(this.workPackagesViewer));
  }

  /**
   * Addition of double click listener in the Workpackage Responsibility List Dialog
   */
  private void addDoubleClickListener() {
    this.workPackagesViewer.addDoubleClickListener(
        doubleClickEvent -> Display.getDefault().asyncExec(ShowWpRespListDialog.this::okPressed));
  }

  private void setTableInput() {
    if (this.a2lWpDefinitionModel != null) {

      if (!this.showOnlyNameAtCust) {
        setInputForWpTable();
      }
      else {
        setInputForNameAtCust();
      }
    }
  }

  /**
   *
   */
  private void setInputForNameAtCust() {
    // Show distinct values for name at customer
    this.workPackagesViewer.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof String) {
          return (String) element;
        }
        return "";
      }
    });
    this.workPackagesViewer.setInput(this.a2lWpInfoBo.getNameAtCustomerSet());
  }

  /**
   *
   */
  private void setInputForWpTable() {
    this.workPackagesViewer.setLabelProvider(new WpRespListLabelProvider(this));
    this.workPackagesViewer.setInput(this.a2lWpInfoBo.getWpsByVarGrpSelection(this.selA2lParamList));
  }


  /**
   * @return the a2lWpDefinitionModel
   */
  public A2lWpDefinitionModel getA2lWpDefinitionModel() {
    return this.a2lWpDefinitionModel;
  }


  /**
   * @return the A2lRespModel
   */
  public A2lResponsibilityModel getA2lRespModel() {
    return this.a2lRespModel;
  }

  /**
  *
  */
  private void addWpListTabSelectionListener() {
    this.workPackagesViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        setSelection();
        ShowWpRespListDialog.this.okButton.setEnabled(true);
      }
    });

  }

  private void setSelection() {
    ShowWpRespListDialog.this.selection =
        (IStructuredSelection) ShowWpRespListDialog.this.workPackagesViewer.getSelection();
    if (!ShowWpRespListDialog.this.selection.isEmpty()) {
      if (!this.showOnlyNameAtCust) {
        ShowWpRespListDialog.this.a2lWpResp =
            (A2lWpResponsibility) ShowWpRespListDialog.this.selection.getFirstElement();
      }
    }
  }


  @Override
  public void okPressed() {
    setSelection();
    super.okPressed();
  }


  /**
   * @return the a2lWpResp
   */
  public A2lWpResponsibility getA2lWpResp() {
    return this.a2lWpResp;
  }

  /**
   * @param a2lWpResp the a2lWpResp to set
   */
  public void setA2lWpResp(final A2lWpResponsibility a2lWpResp) {
    this.a2lWpResp = a2lWpResp;
  }


  /**
   * @return the selection
   */
  public IStructuredSelection getSelection() {
    return this.selection;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    ShowWpRespListDialog.this.okButton = createButton(parent, IDialogConstants.OK_ID, "OK", false);
    ShowWpRespListDialog.this.okButton.setEnabled(false);
    ShowWpRespListDialog.this.cancelButton = createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
    ShowWpRespListDialog.this.cancelButton.setEnabled(true);
  }

  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE);
    setBlockOnOpen(false);

  }


  /**
   * @return the a2lWpInfoBo
   */
  public A2LWPInfoBO getA2lWpInfoBo() {
    return this.a2lWpInfoBo;
  }

}
