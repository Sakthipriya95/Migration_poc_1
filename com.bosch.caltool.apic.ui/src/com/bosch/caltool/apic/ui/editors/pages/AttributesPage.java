/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.AttributesActionSet;
import com.bosch.caltool.apic.ui.editors.AttributesEditor;
import com.bosch.caltool.apic.ui.editors.AttributesEditorInput;
import com.bosch.caltool.apic.ui.listeners.TableViewerListeners;
import com.bosch.caltool.apic.ui.sorter.AttrDependencyGridTabViewerSorter;
import com.bosch.caltool.apic.ui.sorter.AttributesGridTabViewerSorter;
import com.bosch.caltool.apic.ui.table.filters.AttrOutlineFilter;
import com.bosch.caltool.apic.ui.table.filters.AttrPageToolBarFilters;
import com.bosch.caltool.apic.ui.table.filters.AttributesDependencyFilters;
import com.bosch.caltool.apic.ui.table.filters.AttributesFilters;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.views.providers.AttributesColumnLabelProvider;
import com.bosch.caltool.apic.ui.views.providers.AttributesTableLabelProvider;
import com.bosch.caltool.icdm.client.bo.apic.AttrNValueDependencyClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttrRootNode;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO.CLEARING_STATUS;
import com.bosch.caltool.icdm.client.bo.apic.AttributesDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseRootNode;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.sorter.ValuesGridTabViewerSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.ValueFilters;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author adn1cob
 */
public class AttributesPage extends AbstractFormPage implements ISelectionListener {

  /**
   * ID for AttributesPage
   */
  public static final String INSTANCE_ID = "com.bosch.caltool.apic.ui.editors.pages.AttributesPage";
  private Composite composite;
  private ScrolledComposite scrollComp;
  /**
   * Instance of sashform
   */
  private SashForm mainComposite;
  /**
   * Define sections
   */
  private Section sectionAttributes;
  private Section secAttrDep;
  private Section sectionValues;
  private Section secValDep;

  /**
   * Define Attribute table colums
   */
  private GridTableViewer attrTableViewer;
  /**
   * Define Values table columns
   */
  private GridTableViewer valueTableViewer;
  /**
   * Define Attribute dependency columns
   */
  private GridTableViewer attrDepTabViewer;
  /**
   * Define Value dependency columns
   */
  private GridTableViewer valDepTabViewer;

  private GridViewerColumn depnValAttrCol;
  /**
   * Filter text instances
   */
  private Text attrFilterTxt;
  private Text attrDepFilterTxt;
  private Text valueFilterTxt;
  private Text valueDepFilterTxt;

  /**
   * Define Action sets
   */
  private AttributesActionSet actionSet;

  /**
   * Outline filter
   */
  private AttrOutlineFilter outlineFilter;
  private AttributesFilters attrFilters;
  private ValueFilters attrValueFilters;
  private AttributesDependencyFilters attrValueDepFilters;
  private AttributesDependencyFilters attrDepFilters;
  private AbstractViewerSorter attrTabSorter;
  private AbstractViewerSorter attrDepTabSorter;
  private AbstractViewerSorter valTabSorter;

  /**
   * The selected Attribute Group instance
   */
  private AttrGroup selectedAttrGroup;


  private AttrPageToolBarFilters toolBarFilters;
  /**
   * Instance of form
   */
  private Form nonScrollableForm;

  private SashForm sashFormTop;

  private SashForm sashFormBottom;
  private TableViewerComposite tableViewerComp;

  private TableViewerComposite tableViewerCompForValues;

  private final Map<Attribute, SortedSet<AttributeValue>> selectedValuesMap =
      new HashMap<Attribute, SortedSet<AttributeValue>>();

  private final AttributesDataHandler attrHandler;

  // Constant for the Column Width 50
  private static final int COL_WIDTH_50 = 50;
  // Constant for the Column Width 150
  private static final int COL_WIDTH_150 = 150;
  /**
   *
   */
  private static final int COL_WIDTH_80 = 80;
  /**
   *
   */
  private static final int CONST_1 = 1;
  /**
   *
   */
  private static final int CONST_0 = 0;

  /**
   * @param editor FormEditor
   * @param idText id of the page
   * @param title title of the page
   */
  public AttributesPage(final FormEditor editor, final String idText, final String title) {
    super(editor, idText, title);
    this.attrHandler = new AttributesDataHandler();
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
    this.nonScrollableForm = getEditor().getToolkit().createForm(parent);
    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    setTitleText("Attributes Information");
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    this.mainComposite = new SashForm(this.nonScrollableForm.getBody(), SWT.HORIZONTAL);
    this.mainComposite.setLayout(new GridLayout());
    this.mainComposite.setLayoutData(GridDataUtil.getInstance().getGridData());

    final ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }

  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  @Override
  protected void createFormContent(final IManagedForm managedForm) {

    final FormToolkit formToolkit = managedForm.getToolkit();
    // add toolbar actions
    this.actionSet = new AttributesActionSet(this, getEditorSite().getActionBars().getStatusLineManager());
    createComposite(formToolkit);

    this.actionSet.filterUnclearedValues((ToolBarManager) getToolBarManager(), this.toolBarFilters,
        this.attrTableViewer);
    this.actionSet.filterClearedValues((ToolBarManager) getToolBarManager(), this.toolBarFilters, this.attrTableViewer);

    addResetAllFiltersAction();

    getToolBarManager().update(true);
    // add listeners
    getSite().getPage().addSelectionListener(this);
  }


  /**
   *
   */
  private void addResetAllFiltersAction() {

    getFilterTxtSet().add(this.attrDepFilterTxt);
    getFilterTxtSet().add(this.attrFilterTxt);
    getFilterTxtSet().add(this.valueDepFilterTxt);
    getFilterTxtSet().add(this.valueFilterTxt);

    getRefreshComponentSet().add(this.attrTableViewer);
    getRefreshComponentSet().add(this.attrDepTabViewer);
    getRefreshComponentSet().add(this.valueTableViewer);
    getRefreshComponentSet().add(this.valDepTabViewer);

    addResetFiltersAction();

  }


  /**
   * @return ToolBarManager instance
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.nonScrollableForm.getToolBarManager();
  }

  /**
   * This method initializes composite
   *
   * @param formToolkit
   * @param managedForm
   */
  private void createComposite(final FormToolkit formToolkit) {

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = ApicUiConstants.COLUMN_INDEX_2;
    this.scrollComp = new ScrolledComposite(this.mainComposite, SWT.H_SCROLL | SWT.V_SCROLL);
    this.scrollComp.setLayout(new GridLayout());
    this.composite = new Composite(this.scrollComp, SWT.NONE);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.scrollComp.setContent(this.composite);
    this.scrollComp.setExpandHorizontal(true);
    this.scrollComp.setExpandVertical(true);
    // ICDM-860
    SashForm mainSashForm = new SashForm(this.composite, SWT.VERTICAL | SWT.V_SCROLL | SWT.H_SCROLL);
    mainSashForm.setLayout(new GridLayout());
    mainSashForm.setLayoutData(GridDataUtil.getInstance().getGridData());

    // sashform for top two tables
    this.sashFormTop = new SashForm(mainSashForm, SWT.HORIZONTAL);
    this.sashFormTop.setLayout(new GridLayout());
    this.sashFormTop.setLayoutData(GridDataUtil.getInstance().getGridData());

    // sashform for bottom two tables
    this.sashFormBottom = new SashForm(mainSashForm, SWT.HORIZONTAL);
    this.sashFormBottom.setLayout(new GridLayout());
    this.sashFormBottom.setLayoutData(GridDataUtil.getInstance().getGridData());
    getAttrGroupModel();
    createCompositeAttributes(formToolkit);
    createCompositeAttrDependencies(formToolkit);
    createCompositeValues(formToolkit);
    createCompositeValueDependencies(formToolkit);

    // setting the default selection to the first row of attributes tableviewer
    if (this.attrTableViewer.getGrid().getItems().length > CONST_0) {
      this.attrTableViewer.getGrid().setSelection(0);
      setSelectedAttribute();
      this.attrTableViewer.getGrid().setFocus();
      setDefaultInput();
    }
    // Add filters to the TableViewer
    addOutLineFilters();
    // Invoke TableViewer Column sorters
    invokeColumnSorters();
    addDoubleClickListeners();

    final SelectionProviderMediator selectionProviderMediator =
        ((AttributesEditor) getEditor()).getSelectionProviderMediator();
    selectionProviderMediator.addViewer(this.valDepTabViewer);
    selectionProviderMediator.addViewer(this.attrDepTabViewer);
    selectionProviderMediator.addViewer(this.valueTableViewer);
    selectionProviderMediator.addViewer(this.attrTableViewer);
    getSite().setSelectionProvider(selectionProviderMediator);
  }

  /**
   * This method adds double click listeners to the tableviewers
   */
  private void addDoubleClickListeners() {
    final TableViewerListeners tabListener = new TableViewerListeners(this);
    tabListener.attrTableDoubleClickListener();
    tabListener.attrDepnTableDoubleClickListener();
    tabListener.attrValTableDoubleClickListener();
    tabListener.valDepnTableDoubleClickListener();
    this.attrTableViewer.refresh();
  }

  /**
   * Add sorter for the table columns
   */
  private void invokeColumnSorters() {

    this.attrTableViewer.setComparator(this.attrTabSorter);
    this.attrDepTabViewer.setComparator(this.attrDepTabSorter);
    this.valueTableViewer.setComparator(this.valTabSorter);
    this.valDepTabViewer.setComparator(this.attrDepTabSorter);
  }

  /**
   * This method initializes compositeAttributes
   *
   * @param formToolkit2
   */
  private void createCompositeAttributes(final FormToolkit formToolkit2) {

    this.tableViewerComp = new TableViewerComposite();
    this.tableViewerComp.createComposite(formToolkit2, this.sashFormTop);
    this.sectionAttributes = this.tableViewerComp.getSection();
    this.sectionAttributes.setText("Attributes");
    this.attrTableViewer = this.tableViewerComp.getTableViewer();
    initializeEditorStatusLineManager(this.attrTableViewer);

    this.attrFilterTxt = this.tableViewerComp.getFilterTxt();
    this.attrFilters = new AttributesFilters();
    // Add Attribute TableViewer filter
    this.attrTableViewer.addFilter(this.attrFilters);

    this.toolBarFilters = new AttrPageToolBarFilters();
    this.attrTableViewer.addFilter(this.toolBarFilters);


    addModifyTextListener(this.attrFilterTxt, this.attrFilters, this.attrTableViewer);
    createAttrTabColumns();
    ColumnViewerToolTipSupport.enableFor(this.attrTableViewer, ToolTip.NO_RECREATE);

    getSite().setSelectionProvider(this.attrTableViewer);

    this.attrTableViewer.setInput(this.attrHandler.getAttrSet());

    this.attrHandler.fetchAllAttrNodeAccess(null);

    addAttrTabSelListener();
    addAttrTabSelChangedListener();
    createAttrToolBarAction();
    // ICDM-136
    addAttrTabFocusListener();
  }


  /**
   * This method initializes compositeAttrDependency
   *
   * @param formToolkit2
   */
  private void createCompositeAttrDependencies(final FormToolkit formToolkit2) {

    this.tableViewerComp.createComposite(formToolkit2, this.sashFormTop);
    this.secAttrDep = this.tableViewerComp.getSection();
    this.secAttrDep.setText("Attribute Dependency");
    this.attrDepTabViewer = this.tableViewerComp.getTableViewer();
    initializeEditorStatusLineManager(this.attrDepTabViewer);

    this.attrDepFilterTxt = this.tableViewerComp.getFilterTxt();

    this.attrDepFilters = new AttributesDependencyFilters();
    // Add filter
    this.attrDepTabViewer.addFilter(this.attrDepFilters);

    addModifyTextListener(this.attrDepFilterTxt, this.attrDepFilters, this.attrDepTabViewer);

    createAttrDepnToolBarAction();

    createAttrDepnTabColumns();
    this.attrDepTabViewer.setLabelProvider(new AttributesTableLabelProvider(this));

    this.attrDepTabViewer.setInput("");

    this.attrDepTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        IStructuredSelection selection = (IStructuredSelection) AttributesPage.this.attrDepTabViewer.getSelection();
        if (!selection.isEmpty()) {
          Object element = selection.getFirstElement();
          if (element instanceof AttrNValueDependency) {
            AttrNValueDependency attrDep = (AttrNValueDependency) element;
            AttributesPage.this.attrHandler.setSelectedAttrDep(attrDep);

            AttrNValueDependencyClientBO bo = new AttrNValueDependencyClientBO(attrDep);
            controlAttrDepnToolBarActions(attrDep, bo.getAttribute());
          }
        }
      }
    });
    this.attrDepTabViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(final SelectionChangedEvent event) {
        AttributesPage.this.actionSet.getEditAttrDepAction().setEnabled(false);
        AttributesPage.this.actionSet.getDeleteAttrDepAction().setEnabled(false);
        AttributesPage.this.actionSet.getUndeleteAttrDepAction().setEnabled(false);
      }
    });

    // ICDM-136
    this.attrDepTabViewer.getGrid().addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent fLost) {
        // TO-DO
      }

      @Override
      public void focusGained(final FocusEvent fGained) {
        setStatusBarMessage(AttributesPage.this.attrDepTabViewer);
      }
    });
  }

  /**
   *
   */
  private void addModifyTextListener(final Text filterTxt, final AbstractViewerFilter filter,
      final GridTableViewer tableViewer) {
    filterTxt.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        String text = filterTxt.getText().trim();
        filter.setFilterText(text);
        tableViewer.refresh();

      }
    });
  }

  /**
   * This method initializes compositeValues
   *
   * @param formToolkit2
   */
  private void createCompositeValues(final FormToolkit formToolkit2) {
    this.tableViewerCompForValues = new TableViewerComposite();
    this.tableViewerCompForValues.createComposite(formToolkit2, this.sashFormBottom);
    this.sectionValues = this.tableViewerCompForValues.getSection();
    this.sectionValues.setText("Values");
    this.valueTableViewer = this.tableViewerCompForValues.getTableViewer();

    initializeEditorStatusLineManager(this.valueTableViewer);
    this.valueFilterTxt = this.tableViewerCompForValues.getFilterTxt();
    this.attrValueFilters = new ValueFilters();
    // Add Value TableViewer filter
    this.valueTableViewer.addFilter(this.attrValueFilters);

    addModifyTextListener(this.valueFilterTxt, this.attrValueFilters, this.valueTableViewer);
    createValueToolBarAction();

    // activate the tooltip support for the viewer
    ColumnViewerToolTipSupport.enableFor(this.valueTableViewer, ToolTip.NO_RECREATE);
    createValueTabColumns();
    this.valueTableViewer.setInput("");
    addValTabSelListener();
    addValTabSelChngListener();
    // ICDM-136
    addValTabFocusListener();
    // ICDM-254
    addRightClickMenu();

    addDragDrop();

  }

  /**
   * This method initializes compositeValueDependency
   *
   * @param formToolkit2
   */
  private void createCompositeValueDependencies(final FormToolkit formToolkit2) {

    this.tableViewerComp.createComposite(formToolkit2, this.sashFormBottom);
    this.secValDep = this.tableViewerComp.getSection();
    this.secValDep.setText("Value Dependency");
    this.valDepTabViewer = this.tableViewerComp.getTableViewer();
    initializeEditorStatusLineManager(this.valDepTabViewer);
    this.valueDepFilterTxt = this.tableViewerComp.getFilterTxt();
    this.attrValueDepFilters = new AttributesDependencyFilters();
    // Add Value TableViewer filter
    this.valDepTabViewer.addFilter(this.attrValueDepFilters);

    addModifyTextListener(this.valueDepFilterTxt, this.attrValueDepFilters, this.valDepTabViewer);
    createValueDepnToolBarAction();

    createValueDepnTabColumns();
    this.valDepTabViewer.setLabelProvider(new AttributesTableLabelProvider(this));
    this.valDepTabViewer.setInput("");
    this.valDepTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        IStructuredSelection selection = (IStructuredSelection) AttributesPage.this.valDepTabViewer.getSelection();
        if (!selection.isEmpty()) {
          Object element = selection.getFirstElement();
          if (element instanceof AttrNValueDependency) {
            AttrNValueDependency obj = (AttrNValueDependency) element;
            AttributesPage.this.attrHandler.setSelectedAttrValDep(obj);

            // icdm-253
            AttrNValueDependencyClientBO bo = new AttrNValueDependencyClientBO(obj);
            ((AttributesEditorInput) getEditor().getEditorInput()).setAttrNValDepBO(bo);
            controlValDepnToolBarActions(obj, bo.getAttributeValue(),
                new AttributeValueClientBO(bo.getAttributeValue()).getValueDependencies(false));
          }
        }
      }
    });
    this.valDepTabViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(final SelectionChangedEvent event) {
        AttributesPage.this.actionSet.getEditValDepAction().setEnabled(false);
        AttributesPage.this.actionSet.getDeleteValueDepnAction().setEnabled(false);
        AttributesPage.this.actionSet.getUndeleteValueDepnAction().setEnabled(false);
      }
    });
    // ICDM-136
    this.valDepTabViewer.getGrid().addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent fLost) {
        // TO-DO
      }

      @Override
      public void focusGained(final FocusEvent fGained) {
        setStatusBarMessage(AttributesPage.this.valDepTabViewer);
      }
    });
  }

  /**
   *
   */
  private void addAttrTabFocusListener() {
    this.attrTableViewer.getGrid().addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent fLost) {
        // TO-DO
      }

      @Override
      public void focusGained(final FocusEvent fGained) {
        setStatusBarMessage(AttributesPage.this.attrTableViewer);
      }
    });
  }

  /**
   *
   */
  private void addAttrTabSelChangedListener() {
    this.attrTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(final SelectionChangedEvent event) {
        setDefaultInputTabViewer(AttributesPage.this.valDepTabViewer);
        AttributesPage.this.actionSet.getEditAttrAction().setEnabled(false);
        AttributesPage.this.actionSet.getDeleteAttrAction().setEnabled(false);
      }
    });
  }

  /**
   *
   */
  private void addAttrTabSelListener() {
    this.attrTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) AttributesPage.this.attrTableViewer.getSelection();
        if (!selection.isEmpty()) {
          ArrayList<Attribute> selectedAttributes = new ArrayList<>();
          @SuppressWarnings("unchecked")
          final List<Attribute> listAttrs = selection.toList();
          selectedAttributes.addAll(listAttrs);
          AttributesPage.this.attrHandler.setSelectedAttributes(selectedAttributes);
          if (listAttrs.size() == 1) {
            setAttrActions();
          }
          else {
            setMulAttrActions(listAttrs);
          }
        }
      }
    });
  }

  /**
   *
   */
  private void setDefaultInput() {
    if (this.attrTableViewer.getGrid().getItems().length > CONST_0) {
      final GridItem item = this.attrTableViewer.getGrid().getItem(0);
      if (item != null) {
        final Object data = item.getData();
        if (data instanceof Attribute) {
          final Attribute attr = (Attribute) data;
          setDefaultInputTabViewer(this.attrDepTabViewer);
          AttributesPage.this.attrDepTabViewer.setInput(new AttributeClientBO(attr).getAttrDependencies(true));
          this.attrDepTabViewer.refresh();
          setDefaultInputTabViewer(this.valueTableViewer);
          SortedSet<AttributeValue> attrValues = new AttributeClientBO(attr).getAttrValues();
          this.valueTableViewer.setInput(attrValues);
          this.selectedValuesMap.put(attr, attrValues);

          this.valueTableViewer.refresh();
        }
      }
    }
  }


  /**
   * @param listAttrs
   */
  private void setMulAttrActions(final List<Attribute> listAttrs) {
    // ICDM-257
    AttributesPage.this.actionSet.getEditAttrAction().setEnabled(false);
    setDefaultInputTabViewer(this.attrDepTabViewer);
    if (checkIfAnyAttrIsDeleted(listAttrs)) {
      AttributesPage.this.actionSet.getDeleteAttrAction().setEnabled(false);
      AttributesPage.this.actionSet.getAddAttrDepAction().setEnabled(false);
    }
    else {
      // ICDM-740
      if (checkIfAttrValUsed(listAttrs)) {
        AttributesPage.this.actionSet.getDeleteAttrAction().setEnabled(false);
      }
      else {
        AttributesPage.this.actionSet.getDeleteAttrAction().setEnabled(true);
        AttributesPage.this.actionSet.getAddAttrDepAction().setEnabled(true);
      }
    }
  }

  // ICDM-740
  private boolean checkIfAttrValUsed(final List<Attribute> listAttrs) {
    boolean isDeleted = false;
    for (Attribute attribute : listAttrs) {
      if (attribute.getLevel() != CONST_0) {
        isDeleted = true;
        break;
      }
    }
    return isDeleted;
  }

  private boolean checkIfAnyAttrIsDeleted(final List<Attribute> listAttrs) {
    boolean isDeleted = false;
    for (Attribute attribute : listAttrs) {
      if (attribute.isDeleted()) {
        isDeleted = true;
        break;
      }
    }
    return isDeleted;
  }

  /**
   *
   */
  private void setAttrActions() {
    Attribute attr = this.attrHandler.getSelectedAttributes().get(0);
    AttributeClientBO attrBO = new AttributeClientBO(attr);
    controlToolBarActions(attr);
    AttributeValue attrVal = null;

    // ICDM-740
    if (attr.getLevel() != CONST_0) {
      AttributesPage.this.actionSet.getDeleteAttrAction().setEnabled(false);
    }
    this.attrHandler.setAttrDepnsSet(attrBO.getAttrDependencies(true));
    AttributesPage.this.attrDepTabViewer.setInput(this.attrHandler.getAttrDepnsSet());
    AttributesPage.this.attrDepTabViewer.refresh();
    controlToolBarActions(attrVal, attr);
    controlAttrDepnToolBarActions(null, attr);

    // enabling special access right text display in Values table
    AttributesPage.this.tableViewerCompForValues.getSpecialAccessTextLbl()
        .setVisible(this.attrHandler.getAllNodeAccessMap().get(attr.getId()) != null);

    AttributesPage.this.tableViewerComp.getForm().redraw();

    setDefaultInputTabViewer(this.valueTableViewer);
    this.attrHandler.setAttrValues(attrBO.getAttrValues());
    AttributesPage.this.valueTableViewer.setInput(this.attrHandler.getAttrValues());
    this.selectedValuesMap.put(attr, this.attrHandler.getAttrValues());
    AttributesPage.this.valueTableViewer.refresh();
    AttributesPage.this.attrTableViewer.refresh();
    setStatusBarMessage(AttributesPage.this.attrTableViewer);
  }

  /**
   * @param attr
   */
  private void controlToolBarActions(final Attribute attr) {

    AttributeClientBO attrBO = new AttributeClientBO(attr);
    AttributesPage.this.actionSet.getAddAttrAction().setEnabled(false);
    AttributesPage.this.actionSet.getEditAttrAction().setEnabled(false);
    AttributesPage.this.actionSet.getDeleteAttrAction().setEnabled(false);

    CurrentUserBO userBO = new CurrentUserBO();
    try {
      if (userBO.canCreateAttribute()) {
        AttributesPage.this.actionSet.getAddAttrAction().setEnabled(true);
        if ((attr != null) && !attr.isDeleted()) {
          AttributesPage.this.actionSet.getEditAttrAction().setEnabled(true);
          AttributesPage.this.actionSet.getDeleteAttrAction().setEnabled(true);
        }
      }

      else if (attrBO.isGrantAccessEnabled()) {
        AttributesPage.this.actionSet.getEditAttrAction().setEnabled(true);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param value
   * @param parentAttr
   */
  private void controlToolBarActions(final AttributeValue value, final Attribute parentAttr) {
    AttributesPage.this.actionSet.getAddValueAction().setEnabled(false);
    AttributesPage.this.actionSet.getEditValAction().setEnabled(false);
    AttributesPage.this.actionSet.getDeleteValueAction().setEnabled(false);
    AttributesPage.this.actionSet.getInClearAction().setEnabled(false);
    AttributesPage.this.actionSet.getClearedAction().setEnabled(false);
    AttributesPage.this.actionSet.getUndeleteValueAction().setEnabled(false);
    AttributesPage.this.actionSet.getNotClearAction().setEnabled(false);

    setAttrStatus(value, parentAttr);
    setAddDelValue(value, parentAttr);
    // to allow users to edit but not add values for Not-Normalized attribute
    setNotNormAddDelValue(value, parentAttr);
  }

  /**
   * @param value attr value
   * @param parentAttr attr
   */
  private void setAddDelValue(final AttributeValue value, final Attribute parentAttr) {
    AttributeValueClientBO valBO = new AttributeValueClientBO(value);
    AttributeClientBO attrBO = new AttributeClientBO(parentAttr);
    if (attrBO.canModifyValues() && !parentAttr.isDeleted()) {
      AttributesPage.this.actionSet.getAddValueAction().setEnabled(true);
      if (value == null) {
        return;
      }
      if (valBO.isDeleted()) {
        AttributesPage.this.actionSet.getUndeleteValueAction().setEnabled(true);
      }
      else {
        AttributesPage.this.actionSet.getEditValAction().setEnabled(true);
        AttributesPage.this.actionSet.getDeleteValueAction().setEnabled(true);
      }
    }
    // Icdm-830 New Changes for allowing the user to add Uncleared Values
    else if (!parentAttr.isDeleted() && parentAttr.isNormalized()) {
      AttributesPage.this.actionSet.getAddValueAction().setEnabled(true);
    }
  }

  /**
   * @param value attr value
   * @param parentAttr attr
   */
  private void setNotNormAddDelValue(final AttributeValue value, final Attribute parentAttr) {
    try {
      AttributeValueClientBO valBO = new AttributeValueClientBO(value);
      CurrentUserBO currentUser = new CurrentUserBO();
      if (!parentAttr.isNormalized() && !currentUser.hasApicWriteAccess() &&
          currentUser.hasNodeWriteAccess(parentAttr.getId()) && !parentAttr.isDeleted()) {
        AttributesPage.this.actionSet.getAddValueAction().setEnabled(false);
        if (value == null) {
          return;
        }
        AttributesPage.this.actionSet.getEditValAction().setEnabled(!valBO.isDeleted());
        AttributesPage.this.actionSet.getDeleteValueAction().setEnabled(false);
        AttributesPage.this.actionSet.getUndeleteValueAction().setEnabled(false);

      }
    }

    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param value attr value
   * @param parentAttr attr
   */
  private void setAttrStatus(final AttributeValue value, final Attribute parentAttr) {
    AttributeValueClientBO valBO = new AttributeValueClientBO(value);
    AttributeClientBO attrBO = new AttributeClientBO(parentAttr);
    if ((value != null) && parentAttr.isNormalized() && attrBO.canModifyValues() && validateSelAttr(value)) {
      // Icdm-897 New conditions added to move an attr value to any state
      if (valBO.getClearingStatus() == CLEARING_STATUS.NOT_CLEARED) {
        AttributesPage.this.actionSet.getInClearAction().setEnabled(true);
        AttributesPage.this.actionSet.getClearedAction().setEnabled(true);
      }
      else if (valBO.getClearingStatus() == CLEARING_STATUS.IN_CLEARING) {
        AttributesPage.this.actionSet.getClearedAction().setEnabled(true);
        AttributesPage.this.actionSet.getNotClearAction().setEnabled(true);
      }
      // Icdm-897 New conditions added to move an attr value to any state
      else if (valBO.getClearingStatus() == CLEARING_STATUS.CLEARED) {
        AttributesPage.this.actionSet.getInClearAction().setEnabled(true);
        AttributesPage.this.actionSet.getNotClearAction().setEnabled(true);
      }
    }
  }

  /**
   * @param value
   * @return
   */
  private boolean validateSelAttr(final AttributeValue value) {
    return !value.isDeleted() && !this.attrHandler.getSelectedAttributes().get(0).isDeleted();
  }

  /**
   * @param attrDep
   * @param parentAttr
   * @param sortedSet
   */
  private void controlAttrDepnToolBarActions(final AttrNValueDependency attrDep, final Attribute parentAttr) {
    AttributesPage.this.actionSet.getAddAttrDepAction().setEnabled(false);
    AttributesPage.this.actionSet.getEditAttrDepAction().setEnabled(false);
    AttributesPage.this.actionSet.getDeleteAttrDepAction().setEnabled(false);
    AttributesPage.this.actionSet.getUndeleteAttrDepAction().setEnabled(false);
    AttributeClientBO attrBO = new AttributeClientBO(parentAttr);
    if (attrBO.canModifyDependencies()) {
      boolean flag = false;

      if (CommonUtils.isNotEmpty(getAttrDepTabViewer().getGrid().getItems())) {
        for (GridItem item : getAttrDepTabViewer().getGrid().getItems()) {
          AttrNValueDependency dep = (AttrNValueDependency) item.getData();
          // if dependent attribute value is null this means that the dependency is based on used flag.
          // In this case the add action should be disabled since only one (not deleted) dependency
          // can exist for such a value
          if (dep.getDependentValueId() == null) {
            flag = true;
            break;
          }
        }
      }
      if (flag || parentAttr.isDeleted()) {
        AttributesPage.this.actionSet.getAddAttrDepAction().setEnabled(false);
      }
      else if (!parentAttr.isDeleted()) {
        AttributesPage.this.actionSet.getAddAttrDepAction().setEnabled(true);
      }
      if (attrDep == null) {
        return;
      }

      if (attrDep.isDeleted() && !parentAttr.isDeleted()) {
        AttributesPage.this.actionSet.getUndeleteAttrDepAction().setEnabled(true);
      }
      else if (!parentAttr.isDeleted()) {
        AttributesPage.this.actionSet.getEditAttrDepAction().setEnabled(true);
        AttributesPage.this.actionSet.getDeleteAttrDepAction().setEnabled(true);
      }
    }
  }

  /**
   * @param valDep
   * @param parentValue
   * @param valueDependencies
   */
  private void controlValDepnToolBarActions(final AttrNValueDependency valDep, final AttributeValue parentValue,
      final SortedSet<AttrNValueDependency> valueDependencies) {
    AttributesPage.this.actionSet.getAddValueDepAction().setEnabled(false);
    AttributesPage.this.actionSet.getEditValDepAction().setEnabled(false);
    AttributesPage.this.actionSet.getDeleteValueDepnAction().setEnabled(false);
    AttributesPage.this.actionSet.getUndeleteValueDepnAction().setEnabled(false);
    AttributeClientBO attrBO = new AttributeClientBO(this.attrHandler.getSelectedAttributes().get(0));

    if ((parentValue != null) && attrBO.canModifyDependencies()) {
      boolean flag = false;
      flag = checkValDep(flag, valueDependencies);

      if (flag || parentValue.isDeleted() || (this.attrHandler.getSelectedAttributes().get(0)).isDeleted()) {
        AttributesPage.this.actionSet.getAddValueDepAction().setEnabled(false);
      }
      else if (!parentValue.isDeleted() && !(this.attrHandler.getSelectedAttributes().get(0)).isDeleted()) {
        AttributesPage.this.actionSet.getAddValueDepAction().setEnabled(true);
      }

      if (valDep == null) {
        return;
      }

      if (valDep.isDeleted() && !parentValue.isDeleted()) {
        AttributesPage.this.actionSet.getUndeleteValueDepnAction().setEnabled(true);
      }
      else if (!parentValue.isDeleted()) {
        AttributesPage.this.actionSet.getEditValDepAction().setEnabled(true);
        AttributesPage.this.actionSet.getDeleteValueDepnAction().setEnabled(true);
      }
    }

  }

  /**
   * @param parentValue
   * @param flag
   * @param valueDependencies
   * @return
   */
  private boolean checkValDep(final boolean flag, final SortedSet<AttrNValueDependency> valueDependencies) {
    boolean isValid = flag;
    for (AttrNValueDependency valDependency : valueDependencies) {
      // if dependent attribute value is null this means that the dependency is based on used flag.
      // In this case the add action should be disabled since only one (not deleted) dependency
      // can exist for such a value
      if (valDependency.getDependentValueId() == null) {
        isValid = true;
        break;
      }
    }
    return isValid;
  }

  /**
   * This method sets default input for attribute dependency tableviewer
   *
   * @param tableViewer GridTableViewer
   */
  public void setDefaultInputTabViewer(final GridTableViewer tableViewer) {
    tableViewer.setInput(null);
    tableViewer.getGrid().removeAll();
    tableViewer.refresh();
    tableViewer.setSelection(null);
  }

  /**
   * Attribute Filters
   */
  private void addOutLineFilters() {

    this.outlineFilter = new AttrOutlineFilter(((AttributesEditorInput) getEditorInput()).getOutlineDataHandler());
    this.attrTableViewer.addFilter(this.outlineFilter);
  }

  /**
   * Create Attribute Table Columns
   */
  private void createAttrTabColumns() {
    this.attrTabSorter = new AttributesGridTabViewerSorter();
    // ICDM-860
    createAttNameCol();
    createAttrDescCol();
    createAttrValTypCol();
    createNormCol();
    createMandatoryCol();
    createAttrUnitCol();
    createAttrFormatCol();
    createAttrPartNumCol();
    createAttrSpecLinkCol();
    createAttrCharCol();
    createAttrSecurityCol();
    createAttrValSecurityCol();
    // ICDM-1560
    createEadmNameCol();
    createAddValFlagCol();
  }

  /**
   *
   */
  private void createAddValFlagCol() {
    GridViewerColumn addValFlagColumn = new GridViewerColumn(this.attrTableViewer, SWT.LEFT);
    addValFlagColumn.getColumn().setText(ApicUiConstants.VALUE_CAN_BE_ADDED_BY_USERS);
    addValFlagColumn.getColumn().setWidth(COL_WIDTH_50);
    addValFlagColumn.setLabelProvider(new AttributesColumnLabelProvider(this, ApicUiConstants.COLUMN_INDEX_13));
    addValFlagColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        addValFlagColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_13, this.attrTabSorter, this.attrTableViewer));
  }

  /**
   * Create Eadm name column
   */
  private void createEadmNameCol() {
    GridViewerColumn attrNameColumn = new GridViewerColumn(this.attrTableViewer, SWT.LEFT);
    attrNameColumn.getColumn().setText("EADM Name");
    attrNameColumn.getColumn().setWidth(COL_WIDTH_150);
    attrNameColumn.setLabelProvider(new AttributesColumnLabelProvider(this, ApicUiConstants.COLUMN_INDEX_12));
    attrNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        attrNameColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_12, this.attrTabSorter, this.attrTableViewer));

  }

  /**
   * Icdm-955 create Attr Char Column
   */
  private void createAttrCharCol() {
    final GridViewerColumn charCol = new GridViewerColumn(this.attrTableViewer, SWT.LEFT);
    charCol.getColumn().setText(ApicConstants.CHARACTERISTIC);
    charCol.getColumn().setWidth(COL_WIDTH_50);
    charCol.setLabelProvider(new AttributesColumnLabelProvider(this, ApicUiConstants.COLUMN_INDEX_9));
    charCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(charCol.getColumn(),
        ApicUiConstants.COLUMN_INDEX_9, this.attrTabSorter, this.attrTableViewer));

  }

  /**
   *
   */
  private void createAttrSecurityCol() {
    final GridViewerColumn attrSecCol = new GridViewerColumn(this.attrTableViewer, SWT.LEFT);
    attrSecCol.getColumn().setText(" Is Attribute External");
    attrSecCol.getColumn().setWidth(COL_WIDTH_50);
    attrSecCol.setLabelProvider(new AttributesColumnLabelProvider(this, ApicUiConstants.COLUMN_INDEX_10));
    attrSecCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        attrSecCol.getColumn(), ApicUiConstants.COLUMN_INDEX_10, this.attrTabSorter, this.attrTableViewer));


  }

  /**
   *
   */
  private void createAttrValSecurityCol() {
    final GridViewerColumn attrValSecCol = new GridViewerColumn(this.attrTableViewer, SWT.LEFT);
    attrValSecCol.getColumn().setText("Is Value External");
    attrValSecCol.getColumn().setWidth(COL_WIDTH_50);
    attrValSecCol.setLabelProvider(new AttributesColumnLabelProvider(this, ApicUiConstants.COLUMN_INDEX_11));
    attrValSecCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        attrValSecCol.getColumn(), ApicUiConstants.COLUMN_INDEX_11, this.attrTabSorter, this.attrTableViewer));

  }

  /**
   * This method creates specLink column in attributes table.
   */
  private void createAttrSpecLinkCol() {
    final GridViewerColumn specLinkCol = new GridViewerColumn(this.attrTableViewer, SWT.LEFT);
    specLinkCol.getColumn().setText("Specification");
    specLinkCol.getColumn().setWidth(COL_WIDTH_50);
    specLinkCol.setLabelProvider(new AttributesColumnLabelProvider(this, ApicUiConstants.COLUMN_INDEX_8));

    specLinkCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        specLinkCol.getColumn(), ApicUiConstants.COLUMN_INDEX_8, this.attrTabSorter, this.attrTableViewer));
  }

  /**
   * This method creates partNum column in attributes table.
   */
  private void createAttrPartNumCol() {
    final GridViewerColumn partNumFlgCol = new GridViewerColumn(this.attrTableViewer, SWT.LEFT);
    partNumFlgCol.getColumn().setText("PartNumber");
    partNumFlgCol.getColumn().setWidth(COL_WIDTH_50);
    partNumFlgCol.setLabelProvider(new AttributesColumnLabelProvider(this, ApicUiConstants.COLUMN_INDEX_7));

    partNumFlgCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        partNumFlgCol.getColumn(), ApicUiConstants.COLUMN_INDEX_7, this.attrTabSorter, this.attrTableViewer));

  }


  /**
   * This method creates format column in attributes table.
   */
  private void createAttrFormatCol() {
    final GridViewerColumn formatCol = new GridViewerColumn(this.attrTableViewer, SWT.LEFT);
    formatCol.getColumn().setText("Format");
    formatCol.getColumn().setWidth(COL_WIDTH_80);
    formatCol.setLabelProvider(new AttributesColumnLabelProvider(this, ApicUiConstants.COLUMN_INDEX_6));

    formatCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        formatCol.getColumn(), ApicUiConstants.COLUMN_INDEX_6, this.attrTabSorter, this.attrTableViewer));
  }

  /**
   * This method creates Unit column in attributes table.
   */
  private void createAttrUnitCol() {
    GridViewerColumn unitCol = new GridViewerColumn(this.attrTableViewer, SWT.LEFT);
    unitCol.getColumn().setText("Unit");
    unitCol.getColumn().setWidth(COL_WIDTH_50);
    unitCol.setLabelProvider(new AttributesColumnLabelProvider(this, ApicUiConstants.COLUMN_INDEX_5));

    unitCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(unitCol.getColumn(),
        ApicUiConstants.COLUMN_INDEX_5, this.attrTabSorter, this.attrTableViewer));

  }

  /**
   * This method creates mandatory flag column in attributes table.
   */
  private void createMandatoryCol() {
    GridViewerColumn mandatoryCol = new GridViewerColumn(this.attrTableViewer, SWT.LEFT);
    mandatoryCol.getColumn().setText("Mandatory(Default)");
    mandatoryCol.getColumn().setWidth(COL_WIDTH_50);
    mandatoryCol.setLabelProvider(new AttributesColumnLabelProvider(this, ApicUiConstants.COLUMN_INDEX_4));

    mandatoryCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        mandatoryCol.getColumn(), ApicUiConstants.COLUMN_INDEX_4, this.attrTabSorter, this.attrTableViewer));
  }

  /**
   * This method creates normalized flag column in attributes table.
   */
  private void createNormCol() {
    GridViewerColumn normalizedFlgCol = new GridViewerColumn(this.attrTableViewer, SWT.LEFT);
    normalizedFlgCol.getColumn().setText("Normalized");
    normalizedFlgCol.getColumn().setWidth(COL_WIDTH_50);
    normalizedFlgCol.setLabelProvider(new AttributesColumnLabelProvider(this, ApicUiConstants.COLUMN_INDEX_3));

    normalizedFlgCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        normalizedFlgCol.getColumn(), ApicUiConstants.COLUMN_INDEX_3, this.attrTabSorter, this.attrTableViewer));
  }

  /**
   * This method creates attr value type column in attributes table.
   */
  private void createAttrValTypCol() {
    GridViewerColumn attrValTypeColumn = new GridViewerColumn(this.attrTableViewer, SWT.LEFT);
    attrValTypeColumn.getColumn().setText("Value Type");
    attrValTypeColumn.getColumn().setWidth(COL_WIDTH_80);
    attrValTypeColumn.setLabelProvider(new AttributesColumnLabelProvider(this, ApicUiConstants.COLUMN_INDEX_2));

    attrValTypeColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        attrValTypeColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_2, this.attrTabSorter, this.attrTableViewer));
  }

  /**
   * This method creates attr desc column in attributes table.
   */
  private void createAttrDescCol() {
    GridViewerColumn attrDescColumn = new GridViewerColumn(this.attrTableViewer, SWT.LEFT);
    attrDescColumn.getColumn().setText("Description");
    attrDescColumn.getColumn().setWidth(COL_WIDTH_150);
    attrDescColumn.setLabelProvider(new AttributesColumnLabelProvider(this, ApicUiConstants.COLUMN_INDEX_1));

    attrDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        attrDescColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_1, this.attrTabSorter, this.attrTableViewer));
  }

  /**
   * This method creates attr name column in attributes table.
   */
  private void createAttNameCol() {
    GridViewerColumn attrNameColumn = new GridViewerColumn(this.attrTableViewer, SWT.LEFT);
    attrNameColumn.getColumn().setText("Name");
    attrNameColumn.getColumn().setWidth(COL_WIDTH_150);
    attrNameColumn.setLabelProvider(new AttributesColumnLabelProvider(this, ApicUiConstants.COLUMN_INDEX_0));
    attrNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        attrNameColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_0, this.attrTabSorter, this.attrTableViewer));
  }

  /**
   *
   */
  private void createAttrDepnTabColumns() {
    this.attrDepTabSorter = new AttrDependencyGridTabViewerSorter();
    GridViewerColumn depnAttrNameColumn = new GridViewerColumn(this.attrDepTabViewer, SWT.LEFT);
    depnAttrNameColumn.getColumn().setText("Attribute Name");
    depnAttrNameColumn.getColumn().setWidth(COL_WIDTH_150);
    depnAttrNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(depnAttrNameColumn.getColumn(), 0, this.attrDepTabSorter, this.attrDepTabViewer));

    GridViewerColumn depnValueColumn = new GridViewerColumn(this.attrDepTabViewer, SWT.LEFT);
    depnValueColumn.getColumn().setText("Dependent Value");
    depnValueColumn.getColumn().setWidth(COL_WIDTH_150);
    depnValueColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(depnValueColumn.getColumn(), 1, this.attrDepTabSorter, this.attrDepTabViewer));
  }

  /**
   *
   */
  private void addDragDrop() {
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.valueTableViewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes,
        new CustomDragListener(this.valueTableViewer));
    this.valueTableViewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes,
        new ViewerDropAdapter(this.valueTableViewer) {

          @Override
          public boolean validateDrop(final Object target, final int operation, final TransferData transferType) {
            return true;
          }

          @Override
          public boolean performDrop(final Object data) {
            return true;
          }

          /**
           * {@inheritDoc}
           */
          @Override
          public void drop(final DropTargetEvent event) {

            Runnable runnable = new Runnable() {

              @Override
              public void run() {


                Attribute attr =
                    (Attribute) ((ArrayList<?>) AttributesPage.this.attrHandler.getSelectedAttributes()).get(0);

                if (!new AttributeClientBO(attr).canModifyValues()) {
                  CDMLogger.getInstance().info("Insufficient Privileges", Activator.PLUGIN_ID);
                  return;
                }
                // TODO - Deepthi
              }
            };
            BusyIndicator.showWhile(Display.getDefault(), runnable);
          }
        });
  }


  /**
   *
   */
  private void addValTabFocusListener() {
    this.valueTableViewer.getGrid().addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent fLost) {
        // TO-DO
      }

      @Override
      public void focusGained(final FocusEvent fGained) {
        setStatusBarMessage(AttributesPage.this.valueTableViewer);
      }
    });
  }

  /**
   *
   */
  private void addValTabSelChngListener() {
    this.valueTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(final SelectionChangedEvent event) {
        AttributesPage.this.actionSet.getAddValueDepAction().setEnabled(false);
        AttributesPage.this.actionSet.getEditValDepAction().setEnabled(false);
        AttributesPage.this.actionSet.getDeleteValueDepnAction().setEnabled(false);
        AttributesPage.this.actionSet.getUndeleteValueDepnAction().setEnabled(false);
        AttributesPage.this.actionSet.getEditValAction().setEnabled(false);
        AttributesPage.this.actionSet.getDeleteValueAction().setEnabled(false);
        AttributesPage.this.actionSet.getUndeleteValueAction().setEnabled(false);
      }
    });
  }

  /**
   *
   */
  private void addValTabSelListener() {
    this.valueTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        IStructuredSelection selection = (IStructuredSelection) AttributesPage.this.valueTableViewer.getSelection();
        if (!selection.isEmpty()) {
          @SuppressWarnings("unchecked")
          List<AttributeValue> listAttrVals = selection.toList();// ICDM-257

          ArrayList<AttributeValue> selectedValues = new ArrayList<>();
          selectedValues.addAll(listAttrVals);
          AttributesPage.this.attrHandler.setSelectedValues(selectedValues);
          if (listAttrVals.size() == CONST_1) {
            AttributeValue attributeValue = listAttrVals.get(0);
            AttributeValueClientBO valBO = new AttributeValueClientBO(attributeValue);
            controlToolBarActions(attributeValue, AttributesPage.this.attrHandler.getSelectedAttributes().get(0));

            SortedSet<AttrNValueDependency> tableInput = valBO.getValueDependencies(true);

            SortedSet<AttrNValueDependency> valDependenciesNotDeleted = new TreeSet<>();
            AttributesPage.this.attrHandler.setValDepnSet(tableInput);

            for (AttrNValueDependency valDep : tableInput) {
              if (!valDep.isDeleted()) {
                valDependenciesNotDeleted.add(valDep);
              }
            }

            controlValDepnToolBarActions(null, attributeValue, valDependenciesNotDeleted);
            setDefaultInputTabViewer(AttributesPage.this.valDepTabViewer);

            AttributesPage.this.valDepTabViewer.setInput(AttributesPage.this.attrHandler.getValDepnSet());
            AttributesPage.this.valDepTabViewer.refresh();
            setStatusBarMessage(AttributesPage.this.valueTableViewer);
          }
          else {// ICDM-257
            AttributesPage.this.actionSet.getEditValAction().setEnabled(false);
            setDefaultInputTabViewer(AttributesPage.this.valDepTabViewer);

            AttributesPage.this.actionSet.getClearedAction().setEnabled(false);
            AttributesPage.this.actionSet.getNotClearAction().setEnabled(false);
            AttributesPage.this.actionSet.getInClearAction().setEnabled(false);
            CurrentUserBO user = new CurrentUserBO();
            try {
              if (user.canCreateAttribute()) {
                if (checkIfAnyValueIsDeleted(listAttrVals)) {
                  AttributesPage.this.actionSet.getDeleteValueAction().setEnabled(false);
                  AttributesPage.this.actionSet.getAddValueDepAction().setEnabled(false);
                  AttributesPage.this.actionSet.getUndeleteValueAction()
                      .setEnabled(checkIfAllValuesAreDeleted(listAttrVals));
                }
                else {
                  enableClearingActions(listAttrVals);
                  AttributesPage.this.actionSet.getDeleteValueAction().setEnabled(true);
                  AttributesPage.this.actionSet.getUndeleteValueAction().setEnabled(false);
                  AttributesPage.this.actionSet.getAddValueDepAction().setEnabled(true);
                }
              }
            }
            catch (ApicWebServiceException ex) {
              CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
            }
          }
        }
      }

      // ICDM-257
      private boolean checkIfAnyValueIsDeleted(final List<AttributeValue> listAttrVals) {
        boolean isDeleted = false;
        for (AttributeValue attributeValue : listAttrVals) {
          if (attributeValue.isDeleted()) {
            isDeleted = true;
            break;
          }
        }
        return isDeleted;
      }

      private boolean checkIfAllValuesAreDeleted(final List<AttributeValue> listAttrVals) {
        List<AttributeValue> listOfDelAttrVals = new ArrayList<AttributeValue>();
        for (AttributeValue attributeValue : listAttrVals) {
          if (attributeValue.isDeleted()) {
            listOfDelAttrVals.add(attributeValue);
          }
        }
        return listOfDelAttrVals.size() == listAttrVals.size();
      }
    });
  }

  /**
   * new method to enable or diable the Clearing status action buttons if multiple values are selected
   *
   * @param listAttrVals listAttrVals
   */
  protected void enableClearingActions(final List<AttributeValue> listAttrVals) {
    CLEARING_STATUS firstValStatus = null;
    boolean enableButtons = true;
    for (AttributeValue attributeValue : listAttrVals) {
      AttributeValueClientBO bo = new AttributeValueClientBO(attributeValue);
      if (firstValStatus == null) {
        firstValStatus = bo.getClearingStatus();
      }
      else if (firstValStatus != bo.getClearingStatus()) {
        enableButtons = false;
      }
    }

    if (enableButtons) {
      if (firstValStatus == CLEARING_STATUS.CLEARED) {
        AttributesPage.this.actionSet.getNotClearAction().setEnabled(true);
        AttributesPage.this.actionSet.getInClearAction().setEnabled(true);
      }
      else if (firstValStatus == CLEARING_STATUS.IN_CLEARING) {
        AttributesPage.this.actionSet.getClearedAction().setEnabled(true);
        AttributesPage.this.actionSet.getNotClearAction().setEnabled(true);
      }

      else {
        AttributesPage.this.actionSet.getClearedAction().setEnabled(true);
        AttributesPage.this.actionSet.getInClearAction().setEnabled(true);
      }
    }
  }

  /**
   * // ICDM-254 This method adds right click menu for values tableviewer
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    final CommonActionSet cmnActionSet = new CommonActionSet();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(final IMenuManager mgr) {
        final IStructuredSelection selection =
            (IStructuredSelection) AttributesPage.this.valueTableViewer.getSelection();
        final Object firstElement = selection.getFirstElement();
        if ((firstElement != null) && (selection.size() != 0)) {
          if (firstElement instanceof AttributeValue) {
            AttributeValue attVal = (AttributeValue) firstElement;
            if (selection.size() == 1) {
              cmnActionSet.setAddToScrachPadAction(menuMgr, attVal);
            }
            /* Add copy Action */
            cmnActionSet.setCopyAction(menuMgr, ApicUiConstants.COPY, AttributesPage.this.valueTableViewer, true);
          }
        }

        /* Add Paste Action */
        cmnActionSet.setPasteAction(menuMgr, AttributesPage.this.attrHandler.getSelectedAttributes(),
            ApicUiConstants.PASTE, AttributesPage.this.valueTableViewer);
      }
    });
    // Create menu.
    final Menu menu = menuMgr.createContextMenu(this.valueTableViewer.getGrid());
    this.valueTableViewer.getGrid().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.valueTableViewer);
  }

  /**
   *
   */
  private void createValueTabColumns() {
    this.valTabSorter = new ValuesGridTabViewerSorter();
    createValCol();
    createUnitCol();
    createDescCol();
    // Changes for Clearing status icdm-831
    createStatusCol();
    // Icdm-955 attr Char Val Column
    createCharValCol();
  }

  /**
   * Icdm-955 attr Char Val Column Column for the Char Value
   */
  private void createCharValCol() {
    GridViewerColumn charValColumn = new GridViewerColumn(this.valueTableViewer, SWT.LEFT);
    charValColumn.getColumn().setText(ApicConstants.CHARVAL);
    charValColumn.getColumn().setWidth(COL_WIDTH_150);
    charValColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getBackground(final Object element) {
        return null;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        AttributeValue attrVal = (AttributeValue) element;

        if (attrVal.isDeleted()) {
          return AttributesPage.this.valueTableViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);
        }

        AttributeValueClientBO bo = new AttributeValueClientBO(attrVal);
        if ((bo.getClearingStatus() == CLEARING_STATUS.NOT_CLEARED) ||
            (bo.getClearingStatus() == CLEARING_STATUS.IN_CLEARING)) {
          return ApicUiConstants.NOT_CLEARED_VAL_COLOR;
        }
        return AttributesPage.this.valueTableViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        AttributeValue attrVal = (AttributeValue) element;
        return attrVal.getCharStr();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        AttributeValue attrVal = (AttributeValue) element;
        return attrVal.getCharStr();

      }
    });
    charValColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        charValColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_4, this.valTabSorter, this.valueTableViewer));

  }

  /**
   *
   */
  private void createStatusCol() {
    GridViewerColumn statusColumn = new GridViewerColumn(this.valueTableViewer, SWT.LEFT);
    statusColumn.getColumn().setText("Clearing Status");
    statusColumn.getColumn().setWidth(COL_WIDTH_150);
    statusColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getBackground(final Object element) {
        return null;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        AttributeValue attrVal = (AttributeValue) element;

        if (attrVal.isDeleted()) {
          return AttributesPage.this.valueTableViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);

        }

        AttributeValueClientBO bo = new AttributeValueClientBO(attrVal);
        if ((bo.getClearingStatus() == CLEARING_STATUS.NOT_CLEARED) ||
            (bo.getClearingStatus() == CLEARING_STATUS.IN_CLEARING)) {
          return ApicUiConstants.NOT_CLEARED_VAL_COLOR;
        }
        return AttributesPage.this.valueTableViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        AttributeValue attrVal = (AttributeValue) element;
        AttributeValueClientBO bo = new AttributeValueClientBO(attrVal);
        return bo.getClearingStatus().getUiText();

      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        AttributeValue attrVal = (AttributeValue) element;
        return attrVal.getClearingStatus();

      }
    });
    statusColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(statusColumn.getColumn(), 3, this.valTabSorter, this.valueTableViewer));
  }

  /**
   *
   */
  private void createDescCol() {
    GridViewerColumn descColumn = new GridViewerColumn(this.valueTableViewer, SWT.LEFT);
    descColumn.getColumn().setText("Description");
    descColumn.getColumn().setWidth(150);
    descColumn.getColumn().setWordWrap(true);
    descColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getBackground(final Object element) {
        return null;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        AttributeValue attrVal = (AttributeValue) element;

        if (attrVal.isDeleted()) {
          return AttributesPage.this.valueTableViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);

        }

        // Icdm-831 Changes for color blue
        AttributeValueClientBO valBO = new AttributeValueClientBO(attrVal);
        if ((valBO.getClearingStatus() == CLEARING_STATUS.NOT_CLEARED) ||
            (valBO.getClearingStatus() == CLEARING_STATUS.IN_CLEARING)) {
          return ApicUiConstants.NOT_CLEARED_VAL_COLOR;
        }
        return AttributesPage.this.valueTableViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        AttributeValue attrVal = (AttributeValue) element;
        return attrVal.getDescription();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        AttributeValue attrVal = (AttributeValue) element;
        return attrVal.getDescription();
      }
    });
    descColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        descColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_2, this.valTabSorter, this.valueTableViewer));
  }

  /**
   *
   */
  private void createUnitCol() {
    GridViewerColumn unitColumn = new GridViewerColumn(this.valueTableViewer, SWT.LEFT);
    unitColumn.getColumn().setText("Unit");
    unitColumn.getColumn().setWidth(COL_WIDTH_80);
    unitColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getBackground(final Object element) {
        return null;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        AttributeValue attrVal = (AttributeValue) element;

        if (attrVal.isDeleted()) {
          return AttributesPage.this.valueTableViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);
        }

        // Icdm-831 Changes for color blue
        AttributeValueClientBO valBO = new AttributeValueClientBO(attrVal);
        if ((valBO.getClearingStatus() == CLEARING_STATUS.NOT_CLEARED) ||
            (valBO.getClearingStatus() == CLEARING_STATUS.IN_CLEARING)) {
          return ApicUiConstants.NOT_CLEARED_VAL_COLOR;
        }
        return AttributesPage.this.valueTableViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {

        AttributeValue attrVal = (AttributeValue) element;
        return attrVal.getUnit();

      }
    });
    unitColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(unitColumn.getColumn(), 1, this.valTabSorter, this.valueTableViewer));
  }

  /**
   *
   */
  private void createValCol() {
    GridViewerColumn valueColumn = new GridViewerColumn(this.valueTableViewer, SWT.LEFT);
    valueColumn.getColumn().setText("Value");
    valueColumn.getColumn().setWidth(COL_WIDTH_150);
    valueColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getBackground(final Object element) {
        return null;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        AttributeValue attrVal = (AttributeValue) element;
        if (attrVal.isDeleted()) {
          return AttributesPage.this.valueTableViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);
        }
        AttributeValueClientBO valBO = new AttributeValueClientBO(attrVal);
        // Icdm-831 Changes for color blue
        if ((valBO.getClearingStatus() == CLEARING_STATUS.NOT_CLEARED) ||
            (valBO.getClearingStatus() == CLEARING_STATUS.IN_CLEARING)) {
          return ApicUiConstants.NOT_CLEARED_VAL_COLOR;
        }
        return AttributesPage.this.valueTableViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {

        AttributeValue attrVal = (AttributeValue) element;
        return attrVal.getName();
      }
    });
    valueColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(valueColumn.getColumn(), 0, this.valTabSorter, this.valueTableViewer));
  }

  /**
   *
   */
  private void createValueDepnTabColumns() {

    GridViewerColumn depValAttrNamCol = new GridViewerColumn(this.valDepTabViewer, SWT.LEFT);
    depValAttrNamCol.getColumn().setText("Attribute Name");
    depValAttrNamCol.getColumn().setWidth(COL_WIDTH_150);
    depValAttrNamCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(depValAttrNamCol.getColumn(), 0, this.attrDepTabSorter, this.valDepTabViewer));

    this.depnValAttrCol = new GridViewerColumn(this.valDepTabViewer, SWT.LEFT);
    this.depnValAttrCol.getColumn().setText("Dependent Value");
    this.depnValAttrCol.getColumn().setWidth(COL_WIDTH_150);
    this.depnValAttrCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.depnValAttrCol.getColumn(), 1, this.attrDepTabSorter, this.valDepTabViewer));
  }

  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if (CommonUtils.isEqual(getSite().getPage().getActiveEditor(), getEditor()) && (part instanceof OutlineViewPart)) {
      outLineSelectionListener(selection);
      controlToolBarActionsOnSelectionChanged();
    }
    else {
      setStatusBarMessage(this.attrTableViewer);
    }
  }

  /**
   * This method creates Attributes Section ToolBar actions
   */
  private void createAttrToolBarAction() {
    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = toolBarManager.createControl(this.sectionAttributes);
    this.actionSet.createAttrAddAction(toolBarManager);
    this.actionSet.createAttrEditAction(toolBarManager);
    this.actionSet.deleteAttrAction(toolBarManager);
    toolBarManager.update(true);
    this.sectionAttributes.setTextClient(toolbar);
  }

  /**
   *
   */
  private void createAttrDepnToolBarAction() {
    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = toolBarManager.createControl(this.secAttrDep);
    this.actionSet.createAttrDependencyAddAction(toolBarManager, this);
    this.actionSet.createAttrDependencyEditAction(toolBarManager);
    this.actionSet.deleteAttrDepAction(toolBarManager);
    this.actionSet.undeleteAttrDepAction(toolBarManager);
    toolBarManager.update(true);
    this.secAttrDep.setTextClient(toolbar);
  }

  /**
   *
   */
  private void createValueToolBarAction() {
    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = toolBarManager.createControl(this.sectionValues);
    this.actionSet.createValueAddAction(toolBarManager);
    this.actionSet.createValueEditAction(toolBarManager);
    this.actionSet.deleteValueAction(toolBarManager);
    this.actionSet.unDeleteValueAction(toolBarManager);
    // Icdm-897 new action for setting the Value to Not cleared status
    this.actionSet.moveToNotClearedAction(toolBarManager);
    this.actionSet.moveToInClearingAction(toolBarManager);
    this.actionSet.moveToClearedAction(toolBarManager);
    toolBarManager.update(true);
    this.sectionValues.setTextClient(toolbar);
  }

  /**
   *
   */
  private void createValueDepnToolBarAction() {
    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = toolBarManager.createControl(this.secValDep);
    this.actionSet.createValueDepnAddAction(toolBarManager);
    this.actionSet.createValueDepnEditAction(toolBarManager);
    this.actionSet.deleteValueDepnAction(toolBarManager);
    this.actionSet.undeleteValueDepnAction(toolBarManager);
    toolBarManager.update(true);
    this.secValDep.setTextClient(toolbar);
  }

  private void outLineSelectionListener(final ISelection selection) {
    // Icdm-968 get the Current Selection in the Attr table
    ISelection attrTabSel = AttributesPage.this.attrTableViewer.getSelection();
    if ((selection != null) && !selection.isEmpty() && (selection instanceof IStructuredSelection)) {
      Object first = ((IStructuredSelection) selection).getFirstElement();
      // Check if selection is SuperGroup
      if (first instanceof AttrSuperGroup) {
        this.outlineFilter.setGroup(false);
        this.outlineFilter.setSuperGroup(true);
        this.outlineFilter.setCommon(false);
        AttrSuperGroup attrSuperGroup = (AttrSuperGroup) first;

        this.outlineFilter.setFilterText(attrSuperGroup.getName());
        // Check if selection is Group
        AttributesPage.this.selectedAttrGroup = null;
        // Icdm-968 Modify the Input of the Val and Dep table if a diff Super Group is selected
        // TODO - Deepthi
      }
      else if (first instanceof AttrGroup) {
        this.outlineFilter.setGroup(true);
        this.outlineFilter.setSuperGroup(false);
        AttrGroup attrGroup = (AttrGroup) first;

        AttributesPage.this.selectedAttrGroup = attrGroup;
        this.outlineFilter
            .setParentSuperGroup(getAttrGroupModel().getAllSuperGroupMap().get(attrGroup.getSuperGrpId()).getName());
        this.outlineFilter.setFilterText(attrGroup.getName());

        // Icdm-968 Modify the Input of the Val and Dep table if a diff Group is selected
        // TODO - Deepthi
        // Check if selection is COMMON
      }
      // Icdm-295
      else if ((first instanceof AttrRootNode) || (first instanceof UseCaseRootNode) ||
          (first instanceof com.bosch.caltool.icdm.client.bo.uc.UserFavUcRootNode)) {
        this.outlineFilter.setGroup(false);
        this.outlineFilter.setSuperGroup(false);
        this.outlineFilter.setCommon(true);
        this.outlineFilter.setFilterText("");
        AttributesPage.this.selectedAttrGroup = null;
      }
      // Icdm-295
      else if (first instanceof IUseCaseItemClientBO) {
        final IUseCaseItemClientBO ucItem = (IUseCaseItemClientBO) first;
        this.outlineFilter.setUseCaseItem(ucItem);
        // Icdm-968 Modify the Input of the Val and Dep table if a diff Group is selected
        // TODO - Deepthi
      }
      // ICDM-1029
      else if (first instanceof FavUseCaseItemNode) {
        this.outlineFilter.setFavUseCaseItem((FavUseCaseItemNode) first);
      }
      // clear other tables

      setInputForValAndDepTab();

    }
    if (!AttributesPage.this.attrTableViewer.getGrid().isDisposed()) {
      AttributesPage.this.attrTableViewer.refresh();
      // Icdm-968 Set the Already selected Attr as Selected Element
      AttributesPage.this.attrTableViewer.setSelection(attrTabSel, true);
      initializeViewSiteStatusLineManager(ApicUiConstants.OUTLINE_TREE_VIEW, AttributesPage.this.attrTableViewer);
    }

  }

  /**
   * Icdm-968 Set Empty input for the Value and Dep tab viewer if the Outline Filter Data Changes
   */
  private void setInputForValAndDepTab() {
    AttributesPage.this.valueTableViewer.setInput("");
    AttributesPage.this.valueTableViewer.refresh();
    AttributesPage.this.attrDepTabViewer.setInput("");
    AttributesPage.this.attrDepTabViewer.refresh();
    AttributesPage.this.valDepTabViewer.setInput("");
    AttributesPage.this.valDepTabViewer.refresh();
    AttributesPage.this.tableViewerCompForValues.getSpecialAccessTextLbl().setVisible(false);
    AttributesPage.this.tableViewerComp.getForm().redraw();

  }

  @Override
  public void dispose() {
    getSite().getPage().removeSelectionListener(this);
    super.dispose();
  }

  /**
   *
   */
  private void controlToolBarActionsOnSelectionChanged() {
    CurrentUserBO user = new CurrentUserBO();
    try {
      AttributesPage.this.actionSet.getAddAttrAction().setEnabled(user.canCreateAttribute());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    AttributesPage.this.actionSet.getAddAttrDepAction().setEnabled(false);
    AttributesPage.this.actionSet.getAddValueAction().setEnabled(false);
    AttributesPage.this.actionSet.getAddValueDepAction().setEnabled(false);
    AttributesPage.this.actionSet.getEditAttrAction().setEnabled(false);
    AttributesPage.this.actionSet.getEditAttrDepAction().setEnabled(false);
    AttributesPage.this.actionSet.getEditValAction().setEnabled(false);
    AttributesPage.this.actionSet.getEditValDepAction().setEnabled(false);
    AttributesPage.this.actionSet.getDeleteAttrAction().setEnabled(false);
    AttributesPage.this.actionSet.getDeleteAttrDepAction().setEnabled(false);
    AttributesPage.this.actionSet.getUndeleteAttrDepAction().setEnabled(false);
    AttributesPage.this.actionSet.getDeleteValueAction().setEnabled(false);
    AttributesPage.this.actionSet.getUndeleteValueAction().setEnabled(false);
    AttributesPage.this.actionSet.getDeleteValueDepnAction().setEnabled(false);
    AttributesPage.this.actionSet.getUndeleteValueDepnAction().setEnabled(false);
    AttributesPage.this.actionSet.getInClearAction().setEnabled(false);
    AttributesPage.this.actionSet.getClearedAction().setEnabled(false);
    AttributesPage.this.actionSet.getNotClearAction().setEnabled(false);
  }

  /**
   *
   */
  private void setSelectedAttribute() {
    final IStructuredSelection selection = (IStructuredSelection) AttributesPage.this.attrTableViewer.getSelection();
    if (!selection.isEmpty()) {
      final Object element = selection.getFirstElement();
      if (element instanceof Attribute) {
        final Attribute attr = (Attribute) element;
        ArrayList<Attribute> selectedAttributes = new ArrayList<>();
        selectedAttributes.add(attr);
        this.attrHandler.setSelectedAttributes(selectedAttributes);
        controlToolBarActions(attr);
        controlToolBarActions(null, attr);
        controlAttrDepnToolBarActions(null, attr);
      }
    }
  }

  /**
   * Refresh tables
   */
  // iCDM-241
  public void refresh() {
    this.attrTableViewer.refresh();
    this.attrDepTabViewer.refresh();
    this.valueTableViewer.refresh();
    this.valDepTabViewer.refresh();
    // Trigger notification when new value is added
    this.attrTableViewer.getControl().notifyListeners(SWT.Selection, null);
  }


  // this method is added to prevent
  // "java.lang.RuntimeException: WARNING: Prevented recursive attempt to activate part
  // org.eclipse.ui.views.PropertySheet while still in the middle of activating part"
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
  }

  /**
   * @return the outlineFilter
   */
  public AttrOutlineFilter getOutlineFilter() {
    return this.outlineFilter;
  }

  /**
   * @return the attrTableViewer
   */
  public GridTableViewer getAttrTableViewer() {
    return this.attrTableViewer;
  }


  /**
   * @return the valueTableViewer
   */
  public GridTableViewer getValueTableViewer() {
    return this.valueTableViewer;
  }


  /**
   * @return the attrDepTabViewer
   */
  public GridTableViewer getAttrDepTabViewer() {
    return this.attrDepTabViewer;
  }


  /**
   * @return the valDepTabViewer
   */
  public GridTableViewer getValDepTabViewer() {
    return this.valDepTabViewer;
  }


  /**
   * @return the actionSet
   */
  public AttributesActionSet getActionSet() {
    return this.actionSet;
  }


  /**
   * @return the selectedAttrGroup
   */
  public AttrGroup getSelectedAttrGroup() {
    return this.selectedAttrGroup;
  }


  /**
   * @return the selectedValuesMap
   */
  public Map<Attribute, SortedSet<AttributeValue>> getSelectedValuesMap() {
    return this.selectedValuesMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {

    return this.attrHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    this.attrTableViewer.refresh();
    setSelectedAttribute();
    this.attrTableViewer.getGrid().setFocus();
    this.valueTableViewer.setInput(this.attrHandler.getAttrValues());
    this.valueTableViewer.refresh();
    this.attrDepTabViewer.refresh();
    this.valDepTabViewer.refresh();
    this.valueTableViewer.getControl().notifyListeners(SWT.Selection, null);
  }

  /**
   * @return
   */
  public AttrGroupModel getAttrGroupModel() {
    // 492105 - Reuse AttrGroupmodel in Attributedatahandler to get grp and super grp in Attributes editor
    return getAttrHandler().getAttrGroupModel();
  }


  /**
   * @return the attrHandler
   */
  public AttributesDataHandler getAttrHandler() {
    return this.attrHandler;
  }


}
