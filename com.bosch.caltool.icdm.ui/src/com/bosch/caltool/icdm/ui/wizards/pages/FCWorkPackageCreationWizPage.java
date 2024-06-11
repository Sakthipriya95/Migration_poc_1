/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.wizards.pages;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.model.wp.WorkPkg;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.dialogs.AddEditWorkPackageDialog;
import com.bosch.caltool.icdm.ui.dialogs.WPDivDetailsDialog;
import com.bosch.caltool.icdm.ui.sorters.WPDivGridTabViewerSorter;
import com.bosch.caltool.icdm.ui.sorters.WPTabViewerSorter;
import com.bosch.caltool.icdm.ui.sorters.WorkPackageDetailsWrapper;
import com.bosch.caltool.icdm.ui.table.filters.WPDivTabFilter;
import com.bosch.caltool.icdm.ui.table.filters.WPTabFilter;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPDefinitionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivisionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextUtil;

/**
 * The page to create workpackages
 *
 * @author bru2cob
 */
public class FCWorkPackageCreationWizPage extends WizardPage {

  /**
   * Defines AbstractViewerSorter - Wp GridTableViewer sorter
   */
  private AbstractViewerSorter wpDivTabSorter;
  /**
   * GridTableViewer instance for FC2WP
   */
  private GridTableViewer wpDivTabViewer;
  /**
   * Defines AbstractViewerSorter - Wp division GridTableViewer sorter
   */
  private AbstractViewerSorter wpTabSorter;
  /**
   * GridTableViewer instance for FC2WP
   */
  private GridTableViewer wpTabViewer;
  /**
   * Filter text instance
   */
  private Text wpFilterTxt;
  /**
   * FormToolkit instance
   */
  private FormToolkit toolkit;
  /**
   * Filter text instance
   */
  private Text wpDivFilterTxt;
  private WPTabFilter wpTabFilters;
  private Action editWpAction;
  private Action actnDeleteWp;
  protected WPDivTabFilter wpDivTabFilters;
  private WorkPkg selWP;
  private Action actnAddWpDiv;
  private Action editWpDivAction;
  private Action actnDeleteWpDiv;
  protected WorkPackageDetailsWrapper selWPDiv;
  private boolean canCurrUserCreateWp;
  private Set<WorkPackageDivision> wpDivDetails;

  /**
   * @param pageName
   */
  public FCWorkPackageCreationWizPage(final String pageName) {
    super(pageName);
    setTitle(pageName);
    setDescription("Enter Workpackage details");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {


    initializeDialogUnits(parent);
    final Composite workArea = new Composite(parent, SWT.NONE);
    // create layout for composite
    workArea.setLayout(new GridLayout());
    workArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    // set control
    setControl(workArea);
    this.toolkit = new FormToolkit(parent.getDisplay());
    // create wp section
    final Section sectionWP = this.toolkit.createSection(workArea, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);

    sectionWP.getDescriptionControl().setEnabled(false);
    sectionWP.setText("WorkPackages");
    sectionWP.setLayoutData(GridDataUtil.getInstance().getGridData());
    // create wp composite
    final Composite createFc2wpComp = this.toolkit.createComposite(sectionWP, SWT.NONE);
    createFc2wpComp.setLayout(new GridLayout());
    createFc2wpComp.setLayoutData(GridDataUtil.getInstance().getGridData());


    // Create Filter text
    createWPTabFilterTxt(createFc2wpComp);

    // create wp table
    this.wpTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(createFc2wpComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(100));

    this.wpTabFilters = new WPTabFilter();
    // Add PIDC Attribute TableViewer filter
    this.wpTabViewer.addFilter(this.wpTabFilters);
    this.wpTabSorter = new WPTabViewerSorter();
    this.wpTabViewer.setComparator(this.wpTabSorter);

    // Create GridViewerColumns
    createWPGridViewerColumns();


    // Set content provider
    this.wpTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    addWpTabSelectionListener();

    createWpToolBarAction(sectionWP);

    sectionWP.setClient(createFc2wpComp);

    // create assisgnment to division section
    final Section sectionWPDiv =
        this.toolkit.createSection(workArea, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionWPDiv.setText("Assignment to Division");
    sectionWPDiv.getDescriptionControl().setEnabled(false);

    sectionWPDiv.setLayoutData(GridDataUtil.getInstance().getGridData());
    // create wp div composite
    final Composite chooseFc2wpComp = this.toolkit.createComposite(sectionWPDiv, SWT.NONE);
    chooseFc2wpComp.setLayout(new GridLayout());
    chooseFc2wpComp.setLayoutData(GridDataUtil.getInstance().getGridData());

    sectionWPDiv.setClient(chooseFc2wpComp);

    createWpDivToolBarAction(sectionWPDiv);

    this.wpDivTabSorter = new WPDivGridTabViewerSorter();

    // Create Filter text
    createWPDivTabFilterTxt(chooseFc2wpComp);
    this.wpDivTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(chooseFc2wpComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(100));
    // Create GridViewerColumns
    createWPDivGridViewerColumns();
    this.wpDivTabFilters = new WPDivTabFilter();

    this.wpDivTabViewer.addFilter(this.wpDivTabFilters);

    this.wpDivTabViewer.setComparator(this.wpDivTabSorter);
    // Set content provider
    this.wpDivTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    addWpDivTabSelectionListener();


  }

  /**
   *
   */
  private void addWpTabSelectionListener() {
    this.wpTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final IStructuredSelection selection =
            (IStructuredSelection) FCWorkPackageCreationWizPage.this.wpTabViewer.getSelection();
        if (!selection.isEmpty()) {
          FCWorkPackageCreationWizPage.this.selWP = (WorkPkg) selection.getFirstElement();

          setInputToWpDivTableViewer();

          if (!FCWorkPackageCreationWizPage.this.selWP.isDeleted()) {
            FCWorkPackageCreationWizPage.this.editWpAction.setEnabled(isWrkPkgModifiable());
            FCWorkPackageCreationWizPage.this.actnDeleteWp.setEnabled(isWrkPkgModifiable());
            FCWorkPackageCreationWizPage.this.actnAddWpDiv.setEnabled(isWrkPkgModifiable());
          }
          else {
            FCWorkPackageCreationWizPage.this.selWP = null;
            FCWorkPackageCreationWizPage.this.editWpAction.setEnabled(false);
            FCWorkPackageCreationWizPage.this.actnDeleteWp.setEnabled(false);
            FCWorkPackageCreationWizPage.this.actnAddWpDiv.setEnabled(false);
          }
        }
      }
    });

  }

  /**
   * @return
   */
  private Set<WorkPackageDivision> invokeServiceToFetchWpDetails() {
    WorkPackageDivisionServiceClient servClient = new WorkPackageDivisionServiceClient();

    try {
      return servClient.getByWpId(FCWorkPackageCreationWizPage.this.selWP.getId());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog("Error while fetching workpackage details : " + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
      return Collections.emptySet();
    }
  }

  /**
  *
  */
  private void setInputToWpDivTableViewer() {
    this.wpDivDetails = invokeServiceToFetchWpDetails();
    SortedSet<WorkPackageDetailsWrapper> wpwDetails = new TreeSet<>();
    for (WorkPackageDivision workPackageDetails : this.wpDivDetails) {
      WorkPackageDetailsWrapper wpObj = new WorkPackageDetailsWrapper(workPackageDetails);
      wpwDetails.add(wpObj);
    }
    FCWorkPackageCreationWizPage.this.wpDivTabViewer.setInput(wpwDetails);
  }

  /**
  *
  */
  private void addWpDivTabSelectionListener() {
    this.wpDivTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final IStructuredSelection selection =
            (IStructuredSelection) FCWorkPackageCreationWizPage.this.wpDivTabViewer.getSelection();
        if (!selection.isEmpty()) {
          FCWorkPackageCreationWizPage.this.selWPDiv = (WorkPackageDetailsWrapper) selection.getFirstElement();
          if ((null != FCWorkPackageCreationWizPage.this.selWPDiv) &&
              ((null == FCWorkPackageCreationWizPage.this.selWPDiv.getDeleted()) ||
                  !FCWorkPackageCreationWizPage.this.selWPDiv.getDeleted().equals(ApicConstants.CODE_YES))) {
            FCWorkPackageCreationWizPage.this.editWpDivAction.setEnabled(isWrkPkgModifiable());
            FCWorkPackageCreationWizPage.this.actnDeleteWpDiv.setEnabled(isWrkPkgModifiable());
          }
          else {
            FCWorkPackageCreationWizPage.this.editWpDivAction.setEnabled(false);
            FCWorkPackageCreationWizPage.this.actnDeleteWpDiv.setEnabled(false);
          }
        }

      }
    });

  }

  /**
   * Create wp div table filter text
   *
   * @param createFc2wpComp
   */
  private void createWPDivTabFilterTxt(final Composite createFc2wpComp) {
    this.wpDivFilterTxt = TextUtil.getInstance().createFilterText(this.toolkit, createFc2wpComp,
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);
    this.wpDivFilterTxt.addModifyListener(event -> {
      final String text = FCWorkPackageCreationWizPage.this.wpDivFilterTxt.getText().trim();
      FCWorkPackageCreationWizPage.this.wpDivTabFilters.setFilterText(text);
      FCWorkPackageCreationWizPage.this.wpDivTabViewer.refresh();
    });
    this.wpDivFilterTxt.setFocus();

  }

  /**
   * Create wp table filter text
   *
   * @param createFc2wpComp
   */
  private void createWPTabFilterTxt(final Composite createFc2wpComp) {
    this.wpFilterTxt = TextUtil.getInstance().createFilterText(this.toolkit, createFc2wpComp,
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);
    this.wpFilterTxt.addModifyListener(event -> {
      final String text = FCWorkPackageCreationWizPage.this.wpFilterTxt.getText().trim();
      FCWorkPackageCreationWizPage.this.wpTabFilters.setFilterText(text);
      FCWorkPackageCreationWizPage.this.wpTabViewer.refresh();
    });
    this.wpFilterTxt.setFocus();

  }

  /**
   * Create wp div table toolbar action
   *
   * @param sectionWPDiv
   */
  private void createWpDivToolBarAction(final Section sectionWPDiv) {
    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = toolBarManager.createControl(sectionWPDiv);

    // create add action
    this.actnAddWpDiv = new Action("Add Workpackage Division") {

      @Override
      public void run() {
        WPDivDetailsDialog addWpDialog = new WPDivDetailsDialog(Display.getDefault().getActiveShell(),
            FCWorkPackageCreationWizPage.this, FCWorkPackageCreationWizPage.this.selWP, true);
        addWpDialog.open();
        setInputToWpDivTableViewer();

      }
    };
    // Image for add action
    this.actnAddWpDiv.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    toolBarManager.add(this.actnAddWpDiv);
    this.actnAddWpDiv.setEnabled(false);
    // create edit action
    this.editWpDivAction = new Action("Edit Workpackage Division") {

      @Override
      public void run() {
        final IStructuredSelection selection =
            (IStructuredSelection) FCWorkPackageCreationWizPage.this.wpDivTabViewer.getSelection();
        if ((null != selection) && (!selection.isEmpty())) {
          FCWorkPackageCreationWizPage.this.selWPDiv = (WorkPackageDetailsWrapper) selection.getFirstElement();
          WPDivDetailsDialog addWpDialog = new WPDivDetailsDialog(Display.getDefault().getActiveShell(),
              FCWorkPackageCreationWizPage.this, FCWorkPackageCreationWizPage.this.selWP, false);
          addWpDialog.open();
          setInputToWpDivTableViewer();
        }
      }
    };
    // Image for add action
    this.editWpDivAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    toolBarManager.add(this.editWpDivAction);
    this.editWpDivAction.setEnabled(false);
    // create delete action
    this.actnDeleteWpDiv = new Action("Delete Workpackage Division") {

      @Override
      public void run() {
        // Service for WorkpackageDivision update
        try {
          final IStructuredSelection selection =
              (IStructuredSelection) FCWorkPackageCreationWizPage.this.wpDivTabViewer.getSelection();
          if ((null != selection) && (!selection.isEmpty())) {
            FCWorkPackageCreationWizPage.this.selWPDiv = (WorkPackageDetailsWrapper) selection.getFirstElement();
            WorkPackageDivisionServiceClient servClient = new WorkPackageDivisionServiceClient();
            WorkPackageDetailsWrapper wpDetWrappr = getSelWPDiv();
            WorkPackageDivision workPkgDetails = wpDetWrappr.getWorkPkgDetails();
            workPkgDetails.setDeleted(ApicConstants.CODE_YES);
            servClient.update(workPkgDetails);
            FCWorkPackageCreationWizPage.this.editWpDivAction.setEnabled(false);
            FCWorkPackageCreationWizPage.this.actnDeleteWpDiv.setEnabled(false);
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(
              "Error while updating deleted flag of workpackage details :" + exp.getMessage(), exp,
              Activator.PLUGIN_ID);
        }
        finally {
          setInputToWpDivTableViewer();
        }
      }
    };
    // Image for add action
    this.actnDeleteWpDiv.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    toolBarManager.add(this.actnDeleteWpDiv);
    this.actnDeleteWpDiv.setEnabled(false);
    toolBarManager.update(true);
    sectionWPDiv.setTextClient(toolbar);

  }

  /**
   * Create wp table toolbar action
   *
   * @param sectionWP
   */
  private void createWpToolBarAction(final Section sectionWP) {

    final FC2WPDefinitionServiceClient fc2wpClient = new FC2WPDefinitionServiceClient();
    // Load the contents using webservice calls
    Set<FC2WPDef> fc2wpDefSet = null;
    try {
      fc2wpDefSet = fc2wpClient.getAll();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    setCanCurrUserCreateWp(false);
    currentUserAccessRightsCheck(fc2wpDefSet);

    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = toolBarManager.createControl(sectionWP);
    // create add action
    Action actnAddAttr = new Action("Add Workpackage") {

      @Override
      public void run() {
        AddEditWorkPackageDialog addWpDialog = new AddEditWorkPackageDialog(Display.getDefault().getActiveShell(),
            FCWorkPackageCreationWizPage.this, null);
        addWpDialog.open();
      }
    };
    actnAddAttr.setEnabled(isWrkPkgModifiable());
    // Image for add action
    actnAddAttr.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    toolBarManager.add(actnAddAttr);
    this.editWpAction = new Action("Edit Workpackage") {

      @Override
      public void run() {
        final IStructuredSelection selection =
            (IStructuredSelection) FCWorkPackageCreationWizPage.this.wpTabViewer.getSelection();
        if (!selection.isEmpty()) {
          FCWorkPackageCreationWizPage.this.selWP = (WorkPkg) selection.getFirstElement();
        }
        AddEditWorkPackageDialog addWpDialog = new AddEditWorkPackageDialog(Display.getDefault().getActiveShell(),
            FCWorkPackageCreationWizPage.this, FCWorkPackageCreationWizPage.this.selWP);
        addWpDialog.open();
      }
    };
    // Image for add action
    this.editWpAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    this.editWpAction.setEnabled(false);
    toolBarManager.add(this.editWpAction);
    this.actnDeleteWp = new Action("Delete Workpackage") {

      @Override
      public void run() {
        WorkPackageServiceClient servClient = new WorkPackageServiceClient();
        try {
          WorkPkg wpkg = servClient.findById(FCWorkPackageCreationWizPage.this.selWP.getId());
          wpkg.setDeleted(true);
          servClient.update(wpkg);
          Set<WorkPkg> wpSet = servClient.findAll();
          setTableInput(wpSet);
          for (WorkPkg selWpKg : wpSet) {
            if (selWpKg.getName().equalsIgnoreCase(wpkg.getWpNameEng())) {
              GridTableViewerUtil.getInstance().setSelection(FCWorkPackageCreationWizPage.this.wpTabViewer, selWpKg);
              break;
            }
          }
          FCWorkPackageCreationWizPage.this.actnDeleteWp.setEnabled(false);
        }
        catch (Exception excep) {
          CDMLogger.getInstance().errorDialog("Error while deleting work package Data. " + excep.getMessage(), excep,
              Activator.PLUGIN_ID);
        }
      }
    };
    // Image for add action
    this.actnDeleteWp.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    this.actnDeleteWp.setEnabled(false);
    toolBarManager.add(this.actnDeleteWp);

    toolBarManager.update(true);
    sectionWP.setTextClient(toolbar);

  }

  /**
   * @param fc2wpDefSet
   */
  private void currentUserAccessRightsCheck(final Set<FC2WPDef> fc2wpDefSet) {
    if ((fc2wpDefSet != null) && !fc2wpDefSet.isEmpty()) {
      for (FC2WPDef fc2wp : fc2wpDefSet) {
        NodeAccess curUserAccRight;
        try {
          curUserAccRight = new CurrentUserBO().getNodeAccessRight(fc2wp.getId());
          if ((null != curUserAccRight) && curUserAccRight.isOwner()) {
            setCanCurrUserCreateWp(curUserAccRight.isOwner());
            break;
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    }
  }

  /**
   * create wp table columns
   */
  private void createWPGridViewerColumns() {
    final GridViewerColumn nameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.wpTabViewer, "Name", 200);
    nameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        WorkPkg wp = (WorkPkg) element;
        return wp.getName();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        WorkPkg wp = (WorkPkg) element;
        if (wp.isDeleted()) {
          return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        }
        return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
      }

    });
    // Add column selection listener
    nameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(nameColumn.getColumn(), 0, this.wpTabSorter, this.wpTabViewer));


    final GridViewerColumn descColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.wpTabViewer, "Desciption", 200);

    descColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        WorkPkg wp = (WorkPkg) element;
        return wp.getDescription();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        WorkPkg wp = (WorkPkg) element;
        if (wp.isDeleted()) {
          return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        }
        return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
      }
    });
    // Add column selection listener
    descColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(descColumn.getColumn(), 1, this.wpTabSorter, this.wpTabViewer));

  }

  /**
   * create wp div table columns
   */
  private void createWPDivGridViewerColumns() {

    final GridViewerColumn divisionCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.wpDivTabViewer, "Division", 100);

    divisionCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        WorkPackageDetailsWrapper wpDetails = (WorkPackageDetailsWrapper) element;
        return wpDetails.getDivisionName();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        return getWpDetailsColor(element);
      }


    });
    // Add column selection listener
    divisionCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(divisionCol.getColumn(), 0, this.wpDivTabSorter, this.wpDivTabViewer));


    final GridViewerColumn resourceColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.wpDivTabViewer, "Resource", 100);

    resourceColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        WorkPackageDetailsWrapper wpDetails = (WorkPackageDetailsWrapper) element;
        return wpDetails.getResourceName();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        return getWpDetailsColor(element);
      }
    });
    // Add column selection listener
    resourceColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(resourceColumn.getColumn(), 1, this.wpDivTabSorter, this.wpDivTabViewer));

    final GridViewerColumn wpIDColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.wpDivTabViewer, "WP-ID (MCR)", 100);

    wpIDColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        WorkPackageDetailsWrapper wpDetails = (WorkPackageDetailsWrapper) element;
        return wpDetails.getMCR();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        return getWpDetailsColor(element);
      }
    });
    // Add column selection listener
    wpIDColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(wpIDColumn.getColumn(), 2, this.wpDivTabSorter, this.wpDivTabViewer));


    final GridViewerColumn primaryContactColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.wpDivTabViewer, "Primary Contact", 120);

    primaryContactColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        WorkPackageDetailsWrapper wpDetails = (WorkPackageDetailsWrapper) element;
        return wpDetails.getPrimaryContact();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        return getWpDetailsColor(element);
      }
    });
    // Add column selection listener
    primaryContactColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(resourceColumn.getColumn(), 3, this.wpDivTabSorter, this.wpDivTabViewer));


    final GridViewerColumn secContactColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.wpDivTabViewer, "Secondary Contact", 120);

    secContactColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        WorkPackageDetailsWrapper wpDetails = (WorkPackageDetailsWrapper) element;
        return wpDetails.getSecondaryContact();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        return getWpDetailsColor(element);
      }
    });
    // Add column selection listener
    secContactColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(resourceColumn.getColumn(), 4, this.wpDivTabSorter, this.wpDivTabViewer));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {
    return false;
  }

  /**
   *
   */
  public void nextPressed() {
    /**
     * Method not implemented
     */
  }

  /**
   * @param element
   * @return
   */
  private Color getWpDetailsColor(final Object element) {
    WorkPackageDetailsWrapper wp = (WorkPackageDetailsWrapper) element;
    if ((null == wp.getDeleted()) || !wp.getDeleted().equals(ApicConstants.CODE_YES)) {
      return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
    }
    return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
  }

  /**
   *
   */
  public void backPressed() {
    /**
     * Method not implemented
     */
  }

  /**
   * @param wpSet
   */
  public void setTableInput(final Set<WorkPkg> wpSet) {
    this.wpTabViewer.setInput(wpSet);
    this.wpTabViewer.refresh();
  }


  /**
   * @return the wpTabViewer
   */
  public GridTableViewer getWpTabViewer() {
    return this.wpTabViewer;
  }


  /**
   * @return the selWPDiv
   */
  public WorkPackageDetailsWrapper getSelWPDiv() {
    return this.selWPDiv;
  }

  /**
   * @return true if the user has APIC_WRITE access
   */

  public boolean isWrkPkgModifiable() {
    try {
      if (new CurrentUserBO().hasApicWriteAccess() || this.canCurrUserCreateWp) {
        return true;
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return false;

  }


  /**
   * @return the wpDivTabViewer
   */
  public GridTableViewer getWpDivTabViewer() {
    return this.wpDivTabViewer;
  }

  /**
   * @return the canCurrUserCreateWp
   */
  public boolean isCanCurrUserCreateWp() {
    return this.canCurrUserCreateWp;
  }

  /**
   * @param canCurrUserCreateWp the canCurrUserCreateWp to set
   */
  public void setCanCurrUserCreateWp(final boolean canCurrUserCreateWp) {
    this.canCurrUserCreateWp = canCurrUserCreateWp;
  }

  /**
   * @return the wpDivDetails
   */
  public Set<WorkPackageDivision> getWpDivDetails() {
    return this.wpDivDetails;
  }

}
