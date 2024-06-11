/**
 *
 */
package com.bosch.caltool.icdm.common.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.actions.CDMCommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.sorters.FCBCUsageGridTabViewerSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.FCBCUsageFilter;
import com.bosch.caltool.icdm.model.a2l.FCBCUsage;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author adn1cob * Icdm-521 Moved the class to common UI
 */
public class FCBCUsageViewPart extends AbstractViewPart {


  /**
   * FormToolkit instance
   */
  private FormToolkit toolkit;
  private Form nonScrollableForm;
  private Composite fcComposite;

  private Section section;

  private Form form;
  /**
   * Filter text instance
   */
  private Text filterTxt;

  /**
   * GridTableViewer instance for PIDC attribute
   */
  private GridTableViewer fcBcUsageTabViewer;
  /**
   * sorter instance
   */
  private AbstractViewerSorter fcbcUsageTabSorter;
  private AbstractViewerFilter fcBcUsageFilter;
  private List<FCBCUsage> fcBcUsageDetails;


  /**
   * Defines SeriesStatisticsViewPart id
   */
  public static final String FCBC_USAGE_VIEW_ID = FCBCUsageViewPart.class.getName();

  @Override
  public void createPartControl(final Composite parent) {
    addHelpAction();
    // ICDM-26/ICDM-168
    // Super class implementation creates a scrolled form by default
    // The scrolled form is used to create a managed form

    // Overrode this by creating an ordinary form without scrollable behaviour and a managed form instantiated without a
    // scrolled form

    // Create an ordinary non scrollable form on which widgets are built
    this.toolkit = new FormToolkit(parent.getDisplay());
    this.nonScrollableForm = this.toolkit.createForm(parent);
    createComposite(this.toolkit);
  }

  /**
   * This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {
    this.fcComposite = this.nonScrollableForm.getBody();
    this.fcComposite.setLayout(new GridLayout());
    createFCSection(toolkit);
    this.fcComposite.setLayoutData(GridDataUtil.getInstance().getGridData());

  }

  /**
   * This method initializes section
   */
  private void createFCSection(final FormToolkit toolkit) {
    this.section = SectionUtil.getInstance().createSection(this.fcComposite, toolkit, "");
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    // ICDM-183
    this.section.getDescriptionControl().setEnabled(false);
    createForm(toolkit);
    this.section.setClient(this.form);

  }

  /**
   * This method initializes scrolledForm
   */
  private void createForm(final FormToolkit toolkit) {
    this.form = toolkit.createForm(this.section);
    this.filterTxt = TextUtil.getInstance().createFilterText(toolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "Filter text");
    this.form.getBody().setLayout(new GridLayout());
    addModifyListenerForFilterTxt();
    createPIDCAttrTable();

    addRightClickMenu();
  }

  /**
   * This method adds right click menu for tableviewer
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(final IMenuManager mgr) {
        IStructuredSelection selection =
            (IStructuredSelection) FCBCUsageViewPart.this.fcBcUsageTabViewer.getSelection();
        final Object firstElement = selection.getFirstElement();

        if ((firstElement != null) && (selection.size() != 0)) {
          CDMCommonActionSet cdmCommonActionSet = new CDMCommonActionSet();
          CommonActionSet actionSet = new CommonActionSet();
          cdmCommonActionSet.addAPRJMenuAction(menuMgr, ((FCBCUsage) firstElement).getVcdmAprj(),
              (FCBCUsage) firstElement);
          actionSet.openPidcFromFCBC(menuMgr, firstElement);
        }
      }
    });
    Menu menu = menuMgr.createContextMenu(this.fcBcUsageTabViewer.getControl());
    this.fcBcUsageTabViewer.getControl().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.fcBcUsageTabViewer);
  }

  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        final String text = FCBCUsageViewPart.this.filterTxt.getText().trim();
        FCBCUsageViewPart.this.fcBcUsageFilter.setFilterText(text);
        FCBCUsageViewPart.this.fcBcUsageTabViewer.refresh();
      }
    });
  }

  private void createPIDCAttrTable() {
    this.fcbcUsageTabSorter = new FCBCUsageGridTabViewerSorter();
    this.fcBcUsageTabViewer =
        new GridTableViewer(this.form.getBody(), SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

    this.fcBcUsageTabViewer.setContentProvider(new ArrayContentProvider());
    this.fcBcUsageTabViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());

    this.fcBcUsageTabViewer.getGrid().setLinesVisible(true);
    this.fcBcUsageTabViewer.getGrid().setHeaderVisible(true);
    createdPIDCAttrTabViewerColumns();
    this.fcBcUsageTabViewer.setComparator(this.fcbcUsageTabSorter);

    addFilter();

    this.fcBcUsageTabViewer.setInput("");
  }

  /**
   *
   */
  private void addFilter() {
    this.fcBcUsageFilter = new FCBCUsageFilter();
    this.fcBcUsageTabViewer.addFilter(this.fcBcUsageFilter);

  }

  private void createdPIDCAttrTabViewerColumns() {
    createNameColViewer();
    createFuncVerColViewer();
    createCustNameColViewer();
    createVCDMAPRJViewer();
    createCreatedUserColViewer();
  }

  /**
   * This method creates PIDC attribute name table viewer column
   */
  private void createNameColViewer() {
    final GridViewerColumn attrNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.fcBcUsageTabViewer, "Name", 200);
    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final FCBCUsage item = (FCBCUsage) element;
        return item.getName();
      }
    });
    // Add column selection listener
    attrNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrNameColumn.getColumn(), 0, this.fcbcUsageTabSorter, this.fcBcUsageTabViewer));
  }

  /**
   *
   */
  private void createFuncVerColViewer() {
    final GridViewerColumn attrNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.fcBcUsageTabViewer, "Version", 100);
    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final FCBCUsage item = (FCBCUsage) element;
        return item.getFuncVersion();
      }
    });
    // Add column selection listener
    attrNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrNameColumn.getColumn(), 1, this.fcbcUsageTabSorter, this.fcBcUsageTabViewer));

  }

  /**
   *
   */
  private void createCustNameColViewer() {
    final GridViewerColumn attrNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.fcBcUsageTabViewer, "Customer", 100);
    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final FCBCUsage item = (FCBCUsage) element;
        return item.getCustomerName();
      }
    });
    // Add column selection listener
    attrNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrNameColumn.getColumn(), 2, this.fcbcUsageTabSorter, this.fcBcUsageTabViewer));

  }

  /**
   *
   */
  private void createVCDMAPRJViewer() {
    final GridViewerColumn attrNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.fcBcUsageTabViewer, "vCDM APRJ Name", 260);
    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final FCBCUsage item = (FCBCUsage) element;
        return item.getVcdmAprj();
      }
    });
    // Add column selection listener
    attrNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrNameColumn.getColumn(), 3, this.fcbcUsageTabSorter, this.fcBcUsageTabViewer));

  }

  /**
   *
   */
  private void createCreatedUserColViewer() {
    final GridViewerColumn attrNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.fcBcUsageTabViewer, "Created By", 200);
    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final FCBCUsage item = (FCBCUsage) element;
        ;
        return item.getCreatedUser();
      }
    });
    // Add column selection listener
    attrNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrNameColumn.getColumn(), 4, this.fcbcUsageTabSorter, this.fcBcUsageTabViewer));

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    // TODO Auto-generated method stub

  }

  /**
   * Fills the tableviewer the data and refreshes the section title
   *
   * @param fcbcUsages List<FCBCUsage>
   * @param sectionHeader String
   */
  public void fillUI(final String sectionHeader) {
    this.section.setText(sectionHeader);
    this.section.layout();// To refresh section layout after changing section title
    if (this.fcBcUsageDetails != null) {
      this.fcBcUsageTabViewer.setInput(this.fcBcUsageDetails);
    }
  }

  /**
   * @param fcUsages
   */
  public void setFCBCUsageDetails(final List<FCBCUsage> fcBcUsageDetails) {
    this.fcBcUsageDetails = fcBcUsageDetails == null ? null : new ArrayList<>(fcBcUsageDetails);
  }

}
