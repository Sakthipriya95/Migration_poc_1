/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author pdh2cob
 */
public class RvwExportWPSelectionDialog extends AbstractDialog {


  private FormToolkit formToolkit;

  private Section sectionOne;

  private Form formOne;

  private GridTableViewer workPackagesViewer;

  private Button okButton;

  private List<A2lWorkPackage> wpList = new ArrayList<>();

  private List<A2lWorkPackage> selectedWpList = new ArrayList<>();

  private Text filterTxt;

  private WorkPackageSelectionFilter workPackageFilter;

  /**
   * @param parentShell - shell
   * @param wpList - wp list to display
   * @param selectedWpList - wp list selected
   */
  public RvwExportWPSelectionDialog(final Shell parentShell, final List<A2lWorkPackage> wpList,
      final List<A2lWorkPackage> selectedWpList) {
    super(parentShell);
    this.wpList = new ArrayList<>(wpList);
    if (selectedWpList != null) {
      this.selectedWpList.addAll(selectedWpList);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Title modified
    newShell.setText("Work Package List");
    newShell.setSize(575, 400);
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
   * This method creates filter text
   */
  private void createFilterTxt() {
    this.workPackageFilter = new WorkPackageSelectionFilter();
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.formOne.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "");

    this.filterTxt.addModifyListener(event -> {
      final String text = RvwExportWPSelectionDialog.this.filterTxt.getText().trim();
      this.workPackageFilter.setFilterText(text);
      RvwExportWPSelectionDialog.this.workPackagesViewer.refresh();
    });

    this.filterTxt.setFocus();

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
   * @param toolkit This method initializes sectionOne
   * @param gridLayout
   * @param composite
   */
  private void createSection(final FormToolkit toolkit, final GridLayout gridLayout, final Composite composite) {

    this.sectionOne = toolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionOne.setText("Work Packages");
    this.sectionOne.setDescription("Select work package(s) and click OK");
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
        SWT.V_SCROLL | SWT.H_SCROLL | SWT.FILL | SWT.BORDER | SWT.MULTI, GridDataUtil.getInstance().getGridData());

    addWpListTabSelectionListener();

    final GridViewerColumn workPkg = new GridViewerColumn(this.workPackagesViewer, SWT.NONE);
    workPkg.getColumn().setText("Work Package");
    workPkg.getColumn().setWidth(250);

    WorkPackageSelectionSorter sorter = new WorkPackageSelectionSorter();

    workPkg.getColumn().addSelectionListener(
        GridTableViewerUtil.getInstance().getSelectionAdapter(workPkg.getColumn(), 0, sorter, this.workPackagesViewer));


    final GridViewerColumn descColumn = new GridViewerColumn(this.workPackagesViewer, SWT.NONE);
    descColumn.getColumn().setText("Description");
    descColumn.getColumn().setWidth(250);

    descColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(descColumn.getColumn(), 1, sorter, this.workPackagesViewer));
    this.formOne.getBody().setLayout(gridLayout);

    this.workPackagesViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.workPackagesViewer.setFilters(this.workPackageFilter);

    this.workPackagesViewer.setLabelProvider(new RevResultExportWPTableLabelProvider());

    this.workPackagesViewer.setComparator(sorter);

    this.workPackagesViewer.setInput(this.wpList);

    this.workPackagesViewer.setSelection(new StructuredSelection(this.selectedWpList));
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
        RvwExportWPSelectionDialog.this.okButton.setEnabled(true);
      }
    });

  }

  private void setSelection() {
    IStructuredSelection selection =
        (IStructuredSelection) RvwExportWPSelectionDialog.this.workPackagesViewer.getSelection();
    if (selection != null) {
      this.selectedWpList = selection.toList();
    }
  }

  /**
   * @return selected WPs
   */
  public List<A2lWorkPackage> getSelectedWorkpackages() {
    return new ArrayList<>(this.selectedWpList);
  }

  private class RevResultExportWPTableLabelProvider implements ITableLabelProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(final ILabelProviderListener arg0) {
      // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
      // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLabelProperty(final Object arg0, final String arg1) {
      // TODO Auto-generated method stub
      return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(final ILabelProviderListener arg0) {
      // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Image getColumnImage(final Object arg0, final int arg1) {
      // TODO Auto-generated method stub
      return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnText(final Object element, final int columnIndex) {
      A2lWorkPackage wp = (A2lWorkPackage) element;
      return (columnIndex == 0) ? wp.getName() : wp.getDescription();
    }

  }

  /**
   * @author pdh2cob
   */
  class WorkPackageSelectionFilter extends AbstractViewerFilter {

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean selectElement(final Object element) {
      A2lWorkPackage selWpResp = (A2lWorkPackage) element;
      return matchText(selWpResp.getName());
    }

  }

  class WorkPackageSelectionSorter extends AbstractViewerSorter {

    /**
     * index - Column number
     */
    private int index;
    /**
     * constant for descending sort
     */
    private static final int DESCENDING = 1;
    /**
     * constant for ascending sort
     */
    private static final int ASCENDING = 0;
    // Default is ascending sort dirextion
    private int direction = ASCENDING;


    /**
     * {@inheritDoc} set the direction
     */
    @Override
    public void setColumn(final int index) {
      if (index == this.index) {
        this.direction = 1 - this.direction;
      }
      else {
        this.index = index;
        this.direction = ASCENDING;
      }
    }

    /**
     * Compare method for comparing the objects (AttributeValue) equality.{@inheritDoc}
     */
    @Override
    public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
      int compare = 0;
      A2lWorkPackage wp1 = (A2lWorkPackage) obj1;
      A2lWorkPackage wp2 = (A2lWorkPackage) obj2;
      // wp name col
      if (this.index == 0) {
        compare = CommonUtils.compareToIgnoreCase(wp1.getName(), wp2.getName());
      }
      // wp desc col
      if (this.index == 1) {
        compare = CommonUtils.compareToIgnoreCase(wp1.getDescription(), wp2.getDescription());
      }


      if (this.direction == DESCENDING) {
        compare = -compare;
      }
      return compare;
    }

    /**
     * return the direction. {@inheritDoc}
     */
    @Override
    public int getDirection() {
      return this.direction;
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okButton = createButton(parent, IDialogConstants.OK_ID, "OK", false);
    this.okButton.setEnabled(false);
    Button cancelButton = createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
    cancelButton.setEnabled(true);
  }

}
