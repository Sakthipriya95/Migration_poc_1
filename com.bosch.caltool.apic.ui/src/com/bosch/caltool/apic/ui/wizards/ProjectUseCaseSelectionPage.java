/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.table.filters.UseCaseItemsFilter;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.apic.ui.views.providers.OutlinePIDCTreeViewContentProvider;
import com.bosch.caltool.apic.ui.views.providers.OutlinePIDCTreeViewLabelProvider;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author mkl2cob
 */
public class ProjectUseCaseSelectionPage extends WizardPage {

  /**
   * Composite
   */
  private Composite composite;
  /**
   * FormToolkit
   */
  private FormToolkit formToolkit;
  /**
   * Section
   */
  private Section section;
  /**
   * Form
   */
  private Form form;
  /**
   * Text
   */
  private Text filterTxt;
  /**
   *
   */
  private CheckboxTreeViewer projectUCTreeViewer;

  private final List<IUseCaseItemClientBO> checkedItems = new ArrayList<>();
  /**
   *
   */
  private OutlinePIDCTreeViewContentProvider contentProvider;

  /**
   * @param pageName Page name
   */
  protected ProjectUseCaseSelectionPage(final String pageName) {
    super(pageName);
    setTitle("Project Use Cases");
    setDescription("Choose the use cases that are relevant for your project");
    // disable Next button
    setPageComplete(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite comp) {
    initializeDialogUnits(comp);
    final Composite workArea = new Composite(comp, SWT.NONE);
    createComposite(workArea);
    final GridLayout layout = new GridLayout();
    workArea.setLayout(layout);
    workArea.setLayoutData(GridDataUtil.getInstance().createGridData());
    workArea.layout();
    setControl(workArea);
  }

  /**
   * This method initializes composite
   *
   * @param workArea
   */
  private void createComposite(final Composite workArea) {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(workArea);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(gridData);
    this.section.getDescriptionControl().setEnabled(false);
  }

  /**
   *
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Choose Project Use Cases");
    this.section.setDescription(
        "The project use cases define your project extend. If you wish to add the use cases later on, you can skip this step by pressing 'Finish'.");
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * create form
   */
  private void createForm() {
    GridLayout gridLayout = new GridLayout();
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    // Create Filter text
    createFilterTxt(this.form.getBody());
    // Invoke TableViewer Column sorters
    createTableViewer(this.form.getBody());
  }

  /**
   * This method creates filter text
   *
   * @param tableComp
   */
  private void createFilterTxt(final Composite tableComp) {
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), tableComp,
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));

  }

  /**
   * Creates the summary tree tableviewer
   *
   * @param attrComp base composite
   */
  private void createTableViewer(final Composite attrComp) {
    this.projectUCTreeViewer =
        new CheckboxTreeViewer(attrComp, SWT.CHECK | SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL) {

          /**
           * {@inheritDoc}
           */
          @Override
          public void refresh() {
            // when the tree is refreshed , set the check state of the used flags and att value
            super.refresh();
            // when the tree is refreshed , set the check state of the used flags and att value
            setCheckedStateOnRefresh();
          }
        };

    this.projectUCTreeViewer.getTree().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.projectUCTreeViewer.setAutoExpandLevel(1);
    ColumnViewerToolTipSupport.enableFor(this.projectUCTreeViewer, ToolTip.NO_RECREATE);
    // add check listener
    addCheckListener();
    OutLineViewDataHandler dataHandler = new OutLineViewDataHandler(null);
    this.contentProvider = new OutlinePIDCTreeViewContentProvider(null, dataHandler);
    // set content provider
    this.projectUCTreeViewer.setContentProvider(this.contentProvider);
    // set label provider
    this.projectUCTreeViewer.setLabelProvider(new OutlinePIDCTreeViewLabelProvider());

    AbstractViewerFilter ucItemFilter = new UseCaseItemsFilter();
    // add filters
    this.projectUCTreeViewer.addFilter(ucItemFilter);
    // add modify listener
    addModifyTextListener(ucItemFilter);
    this.projectUCTreeViewer.setInput(dataHandler.getUseCaseRootNode());
    addTreeListener();
  }

  /**
   * sets the checked state of attr
   */
  public void setCheckedStateOnRefresh() {
    TreeItem[] items = ProjectUseCaseSelectionPage.this.projectUCTreeViewer.getTree().getItems();
    // all the items in the tree's checked state is checked
    for (TreeItem item : items) {
      Object obj = item.getData();
      IUseCaseItemClientBO ucItem = (IUseCaseItemClientBO) obj;
      if (ProjectUseCaseSelectionPage.this.checkedItems.contains(ucItem)) {
        item.setChecked(true);
      }
    }

    if (CommonUtils.isNotEmpty(ProjectUseCaseSelectionPage.this.checkedItems)) {
      for (IUseCaseItemClientBO item : ProjectUseCaseSelectionPage.this.checkedItems) {
        ProjectUseCaseSelectionPage.this.projectUCTreeViewer.setChecked(item, true);
      }
    }
  }

  /**
   *
   */
  private void addCheckListener() {
    this.projectUCTreeViewer.addCheckStateListener(event -> {
      Object element = event.getElement();
      if (event.getChecked()) {
        ProjectUseCaseSelectionPage.this.checkedItems.add((IUseCaseItemClientBO) element);
      }
      else {
        ProjectUseCaseSelectionPage.this.checkedItems.remove(element);
      }
    });

  }

  /**
  *
  */
  private void addTreeListener() {
    this.projectUCTreeViewer.addTreeListener(new ITreeViewerListener() {


      @Override
      public void treeExpanded(final TreeExpansionEvent event) {
        if (CommonUtils.isNotEmpty(ProjectUseCaseSelectionPage.this.checkedItems)) {
          for (IUseCaseItemClientBO element : ProjectUseCaseSelectionPage.this.checkedItems) {
            ProjectUseCaseSelectionPage.this.projectUCTreeViewer.setChecked(element, true);
          }
        }
        final Object element = event.getElement();
        final Object[] children = ProjectUseCaseSelectionPage.this.contentProvider.getChildren(element);
        for (Object child : children) {
          if (ProjectUseCaseSelectionPage.this.checkedItems.contains(child)) {
            ProjectUseCaseSelectionPage.this.projectUCTreeViewer.setChecked(child, true);
          }
        }
      }

      @Override
      public void treeCollapsed(final TreeExpansionEvent arg0) {
        // No action required
      }
    });

  }

  /**
   * Add modify listener to the attr tree
   */
  void addModifyTextListener(final AbstractViewerFilter filter) {
    this.filterTxt.addModifyListener(event -> {
      String text = ProjectUseCaseSelectionPage.this.filterTxt.getText().trim();
      filter.setFilterText(text);
      ProjectUseCaseSelectionPage.this.projectUCTreeViewer.refresh();
      if (!CommonUtils.isEmptyString(text)) {
        // ICDM-2390
        ProjectUseCaseSelectionPage.this.projectUCTreeViewer.expandAll();
      }
    });
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  protected FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * Get selected items
   *
   * @return list of selected use case items
   */
  public List<IUseCaseItemClientBO> getSelectedUCItems() {
    return new ArrayList<>(this.checkedItems);
  }
}
