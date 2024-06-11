/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.ui.editors.pages; // NOPMD by dmo5cob on 7/1/14 10:45 AM

import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.comppkg.ui.Activator;
import com.bosch.caltool.comppkg.ui.actions.ComponentDetailsToolBarActionSet;
import com.bosch.caltool.comppkg.ui.editors.ComponentPackageEditor;
import com.bosch.caltool.comppkg.ui.editors.ComponentPackageEditorInput;
import com.bosch.caltool.comppkg.ui.sorters.BCFCSorter;
import com.bosch.caltool.comppkg.ui.table.filters.BCFCTableFilter;
import com.bosch.caltool.icdm.client.bo.comppkg.ComponentPackageEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgData;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author bru2cob
 */
public class ComponentDetailsPage extends AbstractFormPage implements ISelectionListener {

  /**
   * Component name
   */
  private static final String COMPONENTS = "Components";
  /**
   * text feilds width
   */
  private static final int TEXT_WIDTHHINT_2 = 600;
  private static final int TEXT_WIDTHHINT_1 = 300;

  /**
   * Display text indicatiog mutiple fc's are mapped for this bc
   */
  public static final String MULTI_FC_MAPPING = "<PARTIAL>";

  /**
   * Display text indicatiog all fc's are mapped for this bc
   */
  public static final String ALL_FC_MAPPING = "<ALL>";

  /**
   * Instance of sashform
   */
  private SashForm mainComposite;

  /**
   * Instance of form
   */
  private Form nonScrollableForm;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Composite instance
   */
  private Composite compositeTwo;
  /**
   * SashForm instance
   */
  private SashForm rvwSashForm;
  /**
   * ComponentDetailsToolBarActionSet instance
   */
  private ComponentDetailsToolBarActionSet toolBarActionSet;

  /**
   * bc form instance
   */
  private Form bcForm;
  /**
   * bc table viewer
   */
  private GridTableViewer bcTableViewer;
  /**
   * bc-fc form instance
   */
  private Form bcFcForm;

  /**
   * bc-fc grid table
   */
  private GridTableViewer bcFcTableViewer;

  /**
   * instance for Columns sortting
   */
  private BCFCSorter tabSorter;


  /**
   * The selected cmp pkg BC instance
   */
  private Button btDown;
  private Button btUp;
  /**
   * The selected cmp pkg BCFC instance
   */
  private BCFCTableFilter bcFilter;
  private BCFCTableFilter bcFcFilter;
  private StyledText descTextField;
  private StyledText nameTxtField;
  private Text bcFilterTxt;
  private Text bcFcFilterTxt;

  /**
   * Note : This field will be set, only if the page is used outside the CompPkgEditor (e.g. for NE type Comp pkg)<br>
   * For normal comp pkgs, the data handler is set in editor input
   */
  private ComponentPackageEditorDataHandler dataHandler;

  /**
   * Constructor when the page is used in compnent package editor. The data handler will be taken from the editor input
   *
   * @param editor editor intance
   */
  public ComponentDetailsPage(final FormEditor editor) {
    super(editor, COMPONENTS, "Component Package");
  }

  /**
   * Constructor, to be used, when the page is used outside Component Package Editor (e.g. for NE type comp pkgs, where
   * the page is part of rule editor)
   *
   * @param editor parent editor
   * @param selectedCompPkg selected component package
   */
  public ComponentDetailsPage(final FormEditor editor, final CompPackage selectedCompPkg) {
    this(editor);
    this.dataHandler = new ComponentPackageEditorDataHandler(selectedCompPkg);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    // Not applicable

  }

  /**
   * Sets the page title
   *
   * @param title String
   */
  public void setTitleText(final String title) {
    if (this.nonScrollableForm != null) {
      this.nonScrollableForm.setText(title);
    }
  }

  @Override
  public void createPartControl(final Composite parent) {
    this.nonScrollableForm = (getEditor()).getToolkit().createForm(parent);

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);
    setTitleText(getSelectedCmpPkg().getName() + " - Package Configuration");
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    final GridLayout gridLayout = new GridLayout();
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;
    this.mainComposite = new SashForm(this.nonScrollableForm.getBody(), SWT.HORIZONTAL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(gridData);

    final ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }

  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }


  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();

    createComposite();
    // add listeners
    getSite().getPage().addSelectionListener(this);

  }

  /**
   * method creates composite
   */
  private void createComposite() {
    final GridData gridData1 = GridDataUtil.getInstance().getGridData();
    ScrolledComposite scrollComp = new ScrolledComposite(this.mainComposite, SWT.H_SCROLL | SWT.V_SCROLL);
    scrollComp.setLayout(new GridLayout());
    this.compositeTwo = new Composite(scrollComp, SWT.NONE);
    this.compositeTwo.setLayout(new GridLayout());
    createSections();
    this.compositeTwo.setLayoutData(gridData1);
    scrollComp.setContent(this.compositeTwo);
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);

  }

  /**
   * This method initializes group
   */
  private void createSections() {
    createsectionComponentProperties();
    createSectionBCMapping();
  }

  /**
   * Create BC section
   */
  private void createSectionBCMapping() {
    final GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    final Group groupTwo = new Group(this.compositeTwo, SWT.FILL);
    groupTwo.setLayout(new GridLayout());
    groupTwo.setLayoutData(GridDataUtil.getInstance().getGridData());
    groupTwo.setText("BC Mapping");

    groupTwo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    final Form formBCMapping = this.formToolkit.createForm(groupTwo);
    formBCMapping.getBody().setLayout(layout);
    formBCMapping.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.rvwSashForm = new SashForm(formBCMapping.getBody(), SWT.HORIZONTAL);
    this.rvwSashForm.setLayout(new GridLayout());
    this.rvwSashForm.setLayoutData(GridDataUtil.getInstance().getGridData());
    createBC();
    createBCFCMapping();

    FormEditor editor = getEditor();
    SelectionProviderMediator selProvMeditr = null;
    if (editor instanceof ComponentPackageEditor) {
      selProvMeditr = ((ComponentPackageEditor) editor).getSelectionProviderMediator();
    }
    if (selProvMeditr != null) {
      selProvMeditr.addViewer(this.bcFcTableViewer);
      selProvMeditr.addViewer(this.bcTableViewer);
      getSite().setSelectionProvider(selProvMeditr);
    }
    setDefaultSelection();
    this.rvwSashForm.setWeights(new int[] { 1, 1 });
    addResetAllFiltersAction();
  }


  /**
   * Add reset filter button ICDM-1207
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.bcFcFilterTxt);
    getFilterTxtSet().add(this.bcFilterTxt);
    getRefreshComponentSet().add(this.bcTableViewer);
    getRefreshComponentSet().add(this.bcFcTableViewer);
    addResetFiltersAction();
  }

  /**
   * @param sectionBCMapping2
   */
  private void createBC() {
    final Composite bcComp = new Composite(this.rvwSashForm, SWT.NONE);
    bcComp.setLayout(new GridLayout());
    createBcSection(bcComp);
  }

  /**
   * @param sectionBCMapping2
   */
  private void createBCFCMapping() {
    final Composite bcFcComp = new Composite(this.rvwSashForm, SWT.NONE);
    bcFcComp.setLayout(new GridLayout());
    createFCSection(bcFcComp);
  }

  /**
   * @param bcComp
   */
  private void createBcSection(final Composite bcComp) {

    final Section bcSection = createSection("Base Components", false, bcComp);
    this.toolBarActionSet = new ComponentDetailsToolBarActionSet();

    createBCForm(bcSection);
    bcSection.setLayout(new GridLayout());
    bcSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    createToolBarActionsForBCMapping(bcSection);

    bcSection.setClient(this.bcForm);
  }

  /**
   * @param bcFcComp
   */
  private void createFCSection(final Composite bcFcComp) {
    final Section bcFcSection = createSection("Functions", false, bcFcComp);
    createBCFCForm(bcFcSection);
    createToolBarActionsForBCFCMapping(bcFcSection);
    bcFcSection.setLayout(new GridLayout());
    bcFcSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    bcFcSection.setClient(this.bcFcForm);
  }

  /**
   * This method initializes form
   *
   * @param bcComp
   */
  private void createBCForm(final Composite bcComp) {

    this.bcForm = this.formToolkit.createForm(bcComp);
    final GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    this.bcForm.setLayoutData(GridDataUtil.getInstance().getGridData());
    // Create Filter text
    createBCFilterTxt();

    new Label(this.bcForm.getBody(), SWT.NONE);

    createBCGridTabViewer();

    createBCBtn();

    // add column filters
    addBCFilters();

    this.bcForm.getBody().setLayout(layout);
  }

  /**
   * select first element of table viewer by default
   *
   * @throws ApicWebServiceException
   */
  private void setDefaultSelection() {
    // select first element by default
    // webservice call
    if (this.bcTableViewer.getGrid().getItemCount() != 0) {
      this.bcTableViewer.getGrid().select(0);
      getBcFcTableViewer().setInput(getPkgBcFc().getFcMap().get(getSelCompPkgBC().getId()));
      getBcFcTableViewer().refresh();

      updateActionButtonStates();
    }
  }

  /**
   * up and down arrow buttons for BC
   */
  private void createBCBtn() {
    final Composite btnComp = this.formToolkit.createComposite(this.bcForm.getBody());
    btnComp.setLayout(new GridLayout());

    final Image upBtn = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.UP_BUTTON_ICON_16X16);

    this.btUp = new Button(btnComp, SWT.PUSH);
    this.btUp.setImage(upBtn);
    this.btUp.setEnabled(false);
    this.btUp.setToolTipText("Move Sequence Up");
    this.btUp.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        updateSeqNumberOfBC(true);
      }
    });
    this.btDown = new Button(btnComp, SWT.PUSH);
    final Image downBtn = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.DOWN_BUTTON_ICON_16X16);
    this.btDown.setImage(downBtn);
    this.btDown.setEnabled(false);
    this.btDown.setToolTipText("Move Sequence Down");
    this.btDown.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        updateSeqNumberOfBC(false);
      }
    });
  }

  /**
   * @param isUp
   */
  private void updateSeqNumberOfBC(final boolean isUp) {
    CompPkgBc selCompPkgBC = getSelCompPkgBC();
    if (null != selCompPkgBC) {
      try {
        getDataHandler().updateBCSeqNo(selCompPkgBC, isUp);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * Add sorter for the table columns
   */
  private void invokeFCColumnSorter() {
    this.tabSorter = new BCFCSorter();
    this.bcFcTableViewer.setComparator(this.tabSorter);
  }

  /**
   * @param bcFcSection
   */
  private void createBCFCForm(final Section bcFcSection) {
    this.bcFcForm = this.formToolkit.createForm(bcFcSection);
    this.bcFcForm.setLayoutData(GridDataUtil.getInstance().getGridData());
    // Create Filter text
    createBCFCFilterTxt();
    // Create new users grid tableviewer
    createBCFCGridTabViewer();
    // Create column sorter
    invokeFCColumnSorter();

    // add coulmn filters
    addBCFCFilters();

    this.bcFcForm.getBody().setLayout(new GridLayout());

  }

  /**
   * Add BC filters
   */
  private void addBCFCFilters() {
    this.bcFcFilter = new BCFCTableFilter();
    this.bcFcTableViewer.addFilter(this.bcFcFilter);

  }

  /**
   * create bc-fc filter
   */
  private void createBCFCFilterTxt() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    this.bcFcFilterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, this.bcFcForm.getBody(), gridData,
        "type filter text");
    this.bcFcFilterTxt.addModifyListener(event -> {
      final String text = ComponentDetailsPage.this.bcFcFilterTxt.getText().trim();
      ComponentDetailsPage.this.bcFcFilter.setFilterText(text);
      ComponentDetailsPage.this.bcFcTableViewer.refresh();
      setStatusBarMessage(ComponentDetailsPage.this.bcFcTableViewer);
    });
    this.bcFcFilterTxt.setFocus();

  }

  /**
   * Create bc fc Gridtable
   */
  private void createBCFCGridTabViewer() {
    this.bcFcTableViewer = new CustomGridTableViewer(this.bcFcForm.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    initializeEditorStatusLineManager(this.bcFcTableViewer);
    this.bcFcTableViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.bcFcTableViewer.getGrid().setLinesVisible(true);
    this.bcFcTableViewer.getGrid().setHeaderVisible(true);
    this.bcFcTableViewer.getGrid().setToolTipText("");
    createBCFCTabColumns();
    this.bcFcTableViewer.setContentProvider(ArrayContentProvider.getInstance());

    this.bcFcTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        updateActionButtonStates();
        setStatusBarMessage(ComponentDetailsPage.this.bcFcTableViewer);
      }

    });

  }

  /**
   * Create bc-fc table columns
   */
  private void createBCFCTabColumns() {
    createFCNameColumn();
    createFcLongNameColumn();

  }

  /**
   * Create FC Long name column
   */
  private void createFcLongNameColumn() {
    final GridViewerColumn fcLongNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.bcFcTableViewer, "Long Name", 250);
    fcLongNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof CompPkgFc) {
          // change to long name
          return ((CompPkgFc) element).getFcName();
        }
        return "";
      }
    });
    // Add column selection listener
    fcLongNameColumn.getColumn()
        .addSelectionListener(getSelectionAdapter(fcLongNameColumn.getColumn(), 2, this.bcFcTableViewer));

  }

  /**
   * Create fc name column
   */
  private void createFCNameColumn() {
    final GridViewerColumn fcNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.bcFcTableViewer, "FC Name", 200);
    fcNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof CompPkgFc) {
          return ((CompPkgFc) element).getFcName();
        }
        return "";
      }
    });
    // Add column selection listener
    fcNameColumn.getColumn()
        .addSelectionListener(getSelectionAdapter(fcNameColumn.getColumn(), 1, this.bcFcTableViewer));

  }

  /**
   * @param bcFcSection
   */
  private void createToolBarActionsForBCFCMapping(final Section bcFcSection) {

    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(bcFcSection);


    this.toolBarActionSet.addBCFCAction(toolBarManager, this);
    this.toolBarActionSet.deleteBCFCAction(toolBarManager, this);
    toolBarManager.update(true);
    bcFcSection.setTextClient(toolbar);

  }

  /**
   * Refresh BC and Fc Table viewer
   */
  private void refreshBcFcGridTableViewer() {
    CompPkgBc selCompPkgBC = getSelCompPkgBC();
    CompPkgFc selCompPkgFc = getSelCompPkgBCFC();

    SortedSet<CompPkgBc> sortedBc = new TreeSet<>(getPkgBcFc().getBcSet());
    this.bcTableViewer.setInput(sortedBc);
    if ((selCompPkgBC != null) && (getPkgBcFc().getFcMap().get(selCompPkgBC.getId()) != null)) {
      SortedSet<CompPkgFc> sortedFc = new TreeSet<>(getPkgBcFc().getFcMap().get(selCompPkgBC.getId()));
      this.bcFcTableViewer.setInput(sortedFc);
    }
    else {
      this.bcFcTableViewer.setInput(null);
    }
    this.bcTableViewer.refresh();
    this.bcFcTableViewer.refresh();

    if (selCompPkgBC != null) {
      this.bcTableViewer.setSelection(new StructuredSelection(selCompPkgBC));
    }

    if (selCompPkgFc != null) {
      this.bcFcTableViewer.setSelection(new StructuredSelection(selCompPkgFc));
    }

    updateActionButtonStates();
  }

  /**
   * Create BC table
   */
  private void createBCGridTabViewer() {
    this.bcTableViewer =
        new CustomGridTableViewer(this.bcForm.getBody(), SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    this.bcTableViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());
    initializeEditorStatusLineManager(this.bcTableViewer);
    this.bcTableViewer.getGrid().setLinesVisible(true);
    this.bcTableViewer.getGrid().setHeaderVisible(true);
    this.bcTableViewer.getGrid().setToolTipText("");
    createBCTabColumns();
    this.bcTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    SortedSet<CompPkgBc> sortedBc = new TreeSet<>(getPkgBcFc().getBcSet());
    this.bcTableViewer.setInput(sortedBc);
    this.bcTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {

        CompPkgBc selBc = getSelCompPkgBC();
        if (selBc != null) {
          SortedSet<CompPkgFc> sortedFc = new TreeSet<>(getPkgBcFc().getFcMap().get(selBc.getId()));
          ComponentDetailsPage.this.bcFcTableViewer.setInput(sortedFc);
          updateActionButtonStates();
        }

        setStatusBarMessage(ComponentDetailsPage.this.bcTableViewer);
      }


    });

    this.bcTableViewer.getGrid().addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent fLost) {
        // TO-DO
      } // NOPMD by dmo5cob on 7/1/14 10:46 AM

      @Override
      public void focusGained(final FocusEvent fGained) {
        setStatusBarMessage(ComponentDetailsPage.this.bcTableViewer);
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setActive(final boolean active) {
    super.setActive(active);
    setStatusBarMessage(this.bcTableViewer);
    setStatusBarMessage(this.bcFcTableViewer);
  }

  /**
   *
   */
  public void enableDisableUpDownBtns() {
    CompPkgBc selCompPkgBC = getSelCompPkgBC();
    if (null != selCompPkgBC) {
      if ((ComponentDetailsPage.this.bcTableViewer.getGrid().getItems().length != 0) &&
          ((CompPkgBc) (ComponentDetailsPage.this.bcTableViewer.getGrid()
              .getItem(ComponentDetailsPage.this.bcTableViewer.getGrid().getItems().length - 1).getData())).getBcName()
                  .equals(selCompPkgBC.getBcName())) {
        ComponentDetailsPage.this.btDown.setEnabled(false);
      }
      else if (getDataHandler().isModifiable()) {
        ComponentDetailsPage.this.btDown.setEnabled(true);
      }
      if ((ComponentDetailsPage.this.bcTableViewer.getGrid().getItems().length != 0) &&
          ((CompPkgBc) (ComponentDetailsPage.this.bcTableViewer.getGrid().getItem(0).getData())).getBcName()
              .equals(selCompPkgBC.getBcName())) {
        ComponentDetailsPage.this.btUp.setEnabled(false);
      }
      else if (getDataHandler().isModifiable()) {
        ComponentDetailsPage.this.btUp.setEnabled(true);
      }
    }
  }

  private void updateActionButtonStates() {
    boolean stateAddBc = false;
    boolean stateDeleteBc = false;
    boolean stateBcUp = false;
    boolean stateBcDown = false;
    boolean stateAddFc = false;
    boolean stateDeleteFc = false;


    if (!getSelectedCmpPkg().isDeleted() && getDataHandler().isModifiable()) {
      stateAddBc = true;
      CompPkgBc selBc = getSelCompPkgBC();
      if (selBc != null) {
        stateDeleteBc = true;
        stateAddFc = true;

        int bcCount = getDataHandler().getCompPkgData().getBcSet().size();
        if (bcCount > 1) {
          if (selBc.getBcSeqNo() > 1) {
            stateBcUp = true;
          }
          if (selBc.getBcSeqNo() < bcCount) {
            stateBcDown = true;
          }
        }
      }

      CompPkgFc selFc = getSelCompPkgBCFC();
      if (selFc != null) {
        stateDeleteFc = true;
      }

    }

    this.toolBarActionSet.getAddBCDetails().setEnabled(stateAddBc);
    this.toolBarActionSet.getDeleteBCDetails().setEnabled(stateDeleteBc);
    this.toolBarActionSet.getAddBCFCDetails().setEnabled(stateAddFc);
    this.toolBarActionSet.getDeleteBCFCDetails().setEnabled(stateDeleteFc);

    this.btUp.setEnabled(stateBcUp);
    this.btDown.setEnabled(stateBcDown);


  }

  /**
   * Create bc table columns
   */
  private void createBCTabColumns() {
    createBCNumberColumn();
    createBCNameColumn();
    createLongNameColumn();
    createFCMappingColumn();
  }

  /**
   * Create long name column
   */
  private void createLongNameColumn() {
    final GridViewerColumn longNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.bcTableViewer, "Long Name", 150);
    longNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof CompPkgBc) {
          return ((CompPkgBc) element).getDescription();
        }
        return "";
      }
    });

  }

  /**
   * Create FC Mapping column
   */
  private void createFCMappingColumn() {
    final GridViewerColumn fcMappingColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.bcTableViewer, "FC Mapping", 78);
    fcMappingColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof CompPkgBc) {
          return getFCMappingDisplay((CompPkgBc) element);

        }
        return "";
      }
    });

  }


  /**
   * Return the display text for UI ( indicates if 'ALL' Fc's mapped for this BC or 'Multiple' Bcs mapped)
   *
   * @param compPkgBc CompPkgBc
   * @return String 'ALL' or 'MULTIPLE'
   */
  public String getFCMappingDisplay(final CompPkgBc compPkgBc) {

    String dispStr = ALL_FC_MAPPING;
    if (!compPkgBc.getFcList().isEmpty()) {
      dispStr = MULTI_FC_MAPPING;
    }

    return dispStr;

  }

  /**
   * This method adds name column
   *
   * @param attrMap
   */
  private void createBCNumberColumn() {
    final GridViewerColumn nameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.bcTableViewer, "Seq No", 50);
    nameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof CompPkgBc) {
          return ((CompPkgBc) element).getBcSeqNo().toString();
        }
        return "";
      }
    });

  }

  /**
   * This method adds name column
   *
   * @param attrMap
   */
  private void createBCNameColumn() {
    final GridViewerColumn nameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.bcTableViewer, "BC Name", 100);
    nameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof CompPkgBc) {
          return ((CompPkgBc) element).getBcName();
        }
        return "";
      }
    });

  }

  /**
   * This method creates filter text
   */
  private void createBCFilterTxt() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    this.bcFilterTxt =
        TextUtil.getInstance().createFilterText(this.formToolkit, this.bcForm.getBody(), gridData, "type filter text");
    this.bcFilterTxt.addModifyListener(event -> {
      final String text = ComponentDetailsPage.this.bcFilterTxt.getText().trim();
      ComponentDetailsPage.this.bcFilter.setFilterText(text);
      ComponentDetailsPage.this.bcTableViewer.refresh();
      setStatusBarMessage(ComponentDetailsPage.this.bcTableViewer);
    });
    this.bcFilterTxt.setFocus();

  }

  /**
   * This method adds the filter instance to addNewUserTableViewer
   */
  private void addBCFilters() {
    this.bcFilter = new BCFCTableFilter();
    this.bcTableViewer.addFilter(this.bcFilter);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ComponentPackageEditorDataHandler getDataHandler() {
    return this.dataHandler == null ? ((ComponentPackageEditorInput) getEditorInput()).getDataHandler()
        : this.dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    refreshCompPkgProps();
    refreshBcFcGridTableViewer();
  }

  /**
   * This method creates section
   *
   * @param sectionName defines section name
   * @param descControlEnable defines description control enable or not
   * @param bcComp
   * @return Section instance
   */
  private Section createSection(final String sectionName, final boolean descControlEnable, final Composite bcComp) {
    return SectionUtil.getInstance().createSection(bcComp, this.formToolkit, GridDataUtil.getInstance().getGridData(),
        sectionName, descControlEnable);
  }

  /**
   * @param bcSection
   */
  private void createToolBarActionsForBCMapping(final Section bcSection) {
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(bcSection);

    this.toolBarActionSet.addBCAction(toolBarManager, this);
    this.toolBarActionSet.deleteBCAction(toolBarManager, this);
    toolBarManager.update(true);
    bcSection.setTextClient(toolbar);

  }

  /**
   * Create component properties
   */
  private void createsectionComponentProperties() {
    final Section sectionCompProp =
        this.formToolkit.createSection(this.compositeTwo, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 2;
    sectionCompProp.setLayoutData(gridLayout1);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    sectionCompProp.setLayoutData(gridData);
    sectionCompProp.setText("Component Properties");
    final Form formParamProps = this.formToolkit.createForm(sectionCompProp);
    formParamProps.getBody().setLayout(gridLayout1);
    formParamProps.setLayoutData(gridData);
    createNameUIControls(formParamProps);
    createDescriptorUIControls(formParamProps);
    sectionCompProp.setClient(formParamProps);
    sectionCompProp.getDescriptionControl().setEnabled(false);

  }


  /**
   * @param formParamProperties
   */
  private void createDescriptorUIControls(final Form form) {
    createLabelControl(form.getBody(), "Description");
    this.descTextField = createStyledTextField(form.getBody(), TEXT_WIDTHHINT_2, false);
    this.descTextField.setText(getSelectedCmpPkg().getDescEng());
  }

  /**
   * @param formParamProperties
   */
  private void createNameUIControls(final Form form) {
    createLabelControl(form.getBody(), "Name");
    this.nameTxtField = createStyledTextField(form.getBody(), TEXT_WIDTHHINT_1, false);
    this.nameTxtField.setText(getSelectedCmpPkg().getName());
  }

  /**
   * @param comp
   * @param widthHint
   * @param isEditable
   * @return
   */
  private StyledText createStyledTextField(final Composite comp, final int widthHint, final boolean isEditable) {
    final StyledText styledTxt = new StyledText(comp, SWT.SINGLE | SWT.BORDER);
    styledTxt.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(widthHint));
    styledTxt.setEditable(isEditable);
    return styledTxt;
  }


  private void createLabelControl(final Composite composite, final String lblName) {
    LabelUtil.getInstance().createLabel(this.formToolkit, composite, lblName);
  }

  /**
   * This method returns SelectionAdapter instance
   *
   * @param column
   * @param index
   * @return SelectionAdapter
   */
  private SelectionAdapter getSelectionAdapter(final GridColumn column, final int index,
      final GridTableViewer gridTable) {

    return new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        ComponentDetailsPage.this.tabSorter.setColumn(index);
        int direction = ComponentDetailsPage.this.tabSorter.getDirection();
        for (int i = 0; i < gridTable.getGrid().getColumnCount(); i++) {
          if (i == index) {
            if (direction == 0) {
              column.setSort(SWT.DOWN);
            }
            else if (direction == 1) {
              column.setSort(SWT.UP);
            }
          }
          if (i != index) {
            gridTable.getGrid().getColumn(i).setSort(SWT.NONE);
          }
        }
        gridTable.refresh();
      }
    };
  }


  /**
   * @return the selcompPkgBC
   */
  public CompPkgBc getSelCompPkgBC() {
    CompPkgBc ret = null;
    IStructuredSelection sel = (IStructuredSelection) this.bcTableViewer.getSelection();
    if ((sel != null) && (sel.getFirstElement() instanceof CompPkgBc)) {
      ret = (CompPkgBc) sel.getFirstElement();
    }
    return ret;
  }

  /**
   * @return the bcFcTableViewer
   */
  public GridTableViewer getBcTableViewer() {
    return this.bcTableViewer;
  }


  /**
   * @return the bcFcTableViewer
   */
  public GridTableViewer getBcFcTableViewer() {
    return this.bcFcTableViewer;
  }


  /**
   * @return the selcompPkgBCFC
   */
  public CompPkgFc getSelCompPkgBCFC() {

    CompPkgFc ret = null;
    IStructuredSelection sel = (IStructuredSelection) this.bcFcTableViewer.getSelection();
    if ((sel != null) && (sel.getFirstElement() instanceof CompPkgFc)) {
      ret = (CompPkgFc) sel.getFirstElement();
    }
    return ret;
  }

  // this method is added to prevent
  // "java.lang.RuntimeException: WARNING: Prevented recursive attempt to activate part
  // org.eclipse.ui.views.PropertySheet while still in the middle of activating part"
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
  }


  /**
   * @return the descTextField
   */
  public StyledText getDescTextField() {
    return this.descTextField;
  }


  /**
   * @return the nameTxtField
   */
  public StyledText getNameTxtField() {
    return this.nameTxtField;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.nonScrollableForm.getToolBarManager();
  }

  /**
   * @return Selected Comp Package
   */
  public CompPackage getSelectedCmpPkg() {
    return getDataHandler().getComponentPackage();
  }

  /**
   * @return the pkgBcFc
   */
  public CompPkgData getPkgBcFc() {
    return getDataHandler().getCompPkgData();
  }


  private void refreshCompPkgProps() {
    CompPackage selCp = getSelectedCmpPkg();

    setTitleText(selCp.getName() + " - Package Configuration");

    this.nameTxtField.setText(selCp.getName());
    this.descTextField.setText(selCp.getDescription());
  }
}
