/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.actions.PIDCSearchToolBarActionSet;
import com.bosch.caltool.apic.ui.sorter.PIDCAttrSearchSorter;
import com.bosch.caltool.apic.ui.table.filters.AttributesFilters;
import com.bosch.caltool.apic.ui.table.filters.PIDCSearchToolBarFilters;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.apic.ui.views.providers.PIDCSearchContentProvider;
import com.bosch.caltool.apic.ui.views.providers.PIDCSearchLabelProvider;
import com.bosch.caltool.icdm.common.ui.actions.CollapseAllAction;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.text.TextUtil;


/**
 * PIDC search attributes tree section
 *
 * @author bru2cob
 */
public class PIDCSearchAttrSection {


  /**
   * Attrbiute name col width
   */
  private static final int ATTR_NAME_COL_WIDTH = 230;
  /**
   * Attrbiute info col width
   */
  private static final int ATTR_INFO_COL_WIDTH = 60;

  /**
   * column width-70
   */
  private static final int ATTR_COL_WIDTH = 70;
  /**
   * horizontal span
   */
  private static final int SPAN_2 = 2;
  /**
   * number of columns for search button group
   */
  private static final int SEARCH_BTN_GRP_COLCOUNT = 4;
  /**
   * column width-90
   */
  private static final int SEARCH_BTN_WIDTH = 90;
  /**
   * Width for clear all button
   */
  private static final int CLEAR_BTN_WIDTH = 80;
  /**
   * Btns size
   */
  private static final int BTN_SIZE = 25;
  /**
   * Search page instance
   */
  private final PIDCSearchPage searchPg;

  /**
   * Section to display attrs with value
   */
  private Section attrsSection;
  /**
   * Form to display the attrs
   */
  private Form attsForm;
  /**
   * Filter text instance
   */
  private Text filterTxt;


  /**
   * Instance of tree viewer for attrs
   */
  private CheckboxTreeViewer summaryTreeViewer;

  /**
   * Text for the column Undefined
   */
  private static final String TEXT_UNDEFINED = "Used-???";
  /**
   * Text for Used = Yes column
   */
  private static final String TEXT_USED_YES = "Used-YES";
  /**
   * Text for Used = No column
   */
  private static final String TEXT_USED_NO = "Used-NO";
  /**
   * Text for Attributes column
   */
  private static final String TEXT_ATTRIBUTE_VAL = "Attributes-Values";

  /**
   * @return the summaryTreeViewer
   */
  public CheckboxTreeViewer getSummaryTreeViewer() {
    return this.summaryTreeViewer;
  }

  /**
   * Filtering the attributes
   */
  private AttributesFilters attrFilters;

  /**
   * Sorter for attrs
   */
  private PIDCAttrSearchSorter fcTabSorter;
  /**
   * two columns in attr table
   */
  private static final int ATTR_FORM_COL = 2;
  /**
   * Count of total checked attributes
   */
  private Label count;

  /**
   * Label count
   *
   * @return the count
   */
  public Label getCount() {
    return this.count;
  }


  /**
   * Set the count
   *
   * @param count the count to set
   */
  public void setCount(final Label count) {
    this.count = count;
  }

  /**
   * Actions for attr pre-defined filters
   */
  PIDCSearchToolBarActionSet toolBarActionSet;
  /**
   * attr pre-defined filters
   */
  PIDCSearchToolBarFilters toolBarFilters;

  /**
   * @param searchPg pidc search page instance
   */
  public PIDCSearchAttrSection(final PIDCSearchPage searchPg) {
    super();
    this.searchPg = searchPg;
  }

  /**
   * Create the section to display the attrs and value
   */
  public void createAttributesSection() {
    this.attrsSection = this.searchPg.getFormToolkit().createSection(this.searchPg.getSashForm(),
        Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.attrsSection.setText("Search Conditions");
    this.attrsSection.setDescription("Select attribute value(s) to be included in search");
    this.attrsSection.setExpanded(true);
    this.attrsSection.getDescriptionControl().setEnabled(false);
    // create the attrs form
    createAttrsForm();
    this.attrsSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.attrsSection.setClient(this.attsForm);

  }

  /**
   * Create the attrs form in section
   */
  private void createAttrsForm() {
    this.toolBarActionSet = new PIDCSearchToolBarActionSet();
    this.fcTabSorter = new PIDCAttrSearchSorter();
    this.attsForm = this.searchPg.getFormToolkit().createForm(this.attrsSection);
    GridLayout layout = new GridLayout();
    layout.numColumns = ATTR_FORM_COL;
    this.attsForm.getBody().setLayout(layout);
    this.attsForm.setLayoutData(GridDataUtil.getInstance().getGridData());
    /**
     * Create three composites on the attrs form. 1. one comp for count 2.one composite for buttons 3.one composite for
     * attr table
     */
    createLabelComp();
    createButtonComp();
    createAttrTabComp();

  }

  /**
   * Create attrs table composite
   */
  private void createAttrTabComp() {
    Composite tableComp = new Composite(this.attsForm.getBody(), SWT.NONE);
    tableComp.setLayout(new GridLayout());
    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.horizontalSpan = SPAN_2;
    tableComp.setLayoutData(gridData);
    // Create Filter text
    createFilterTxt(tableComp);
    // Invoke TableViewer Column sorters
    createTableViewer(tableComp);
    // ICDM-1158
    createToolBarAction();
    // add column sorter
    invokeColumnSorter();
  }

  /**
   * Creates the summary tree tableviewer
   *
   * @param attrComp base composite
   */
  private void createTableViewer(final Composite attrComp) {
    this.summaryTreeViewer =
        new CheckboxTreeViewer(attrComp, SWT.CHECK | SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL) {

          /**
           * {@inheritDoc}
           */
          @Override
          public void refresh() {
            // when the tree is refreshed , set the check state of the used flags and att value
            super.refresh();
            PIDCSearchAttrSection.this.searchPg.getAttrTreeUtil().setCheckedStateOnRefresh();
            PIDCSearchAttrSection.this.searchPg.getAttrTreeUtil().setUsedFlagStateOnRefresh();
          }

        };

    this.summaryTreeViewer.getTree().setHeaderVisible(true);
    this.summaryTreeViewer.getTree().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.summaryTreeViewer.setAutoExpandLevel(1);
    ColumnViewerToolTipSupport.enableFor(this.summaryTreeViewer, ToolTip.NO_RECREATE);
    // add check listener
    addCheckListener();
    // set content provider
    this.summaryTreeViewer.setContentProvider(new PIDCSearchContentProvider(this.searchPg));
    // set label provider
    this.summaryTreeViewer.setLabelProvider(new PIDCSearchLabelProvider());

    // add filters
    addFilters();
    // add modify listener
    PIDCSearchAttrSection.this.searchPg.getAttrTreeUtil().addModifyTextListener(this.filterTxt, this.attrFilters,
        this.summaryTreeViewer);
    // create table columns
    createTabViewerColumns();
    this.summaryTreeViewer.setInput(this.searchPg.getDataHandler().getAllAttrsSorted());
    // add mouse listener
    addMouseListener();
  }


  /**
   * adds mouse listener to tah attributes table
   */
  private void addMouseListener() {
    this.summaryTreeViewer.getTree().addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(final MouseEvent mouseevent) {
        // TO-DO
      }

      @Override
      public void mouseDown(final MouseEvent event) {
        final int columnIndex =
            GridTableViewerUtil.getInstance().getTreeColIndex(event, PIDCSearchAttrSection.this.summaryTreeViewer);
        // check for three columns (used-undef,yes,no)
        if ((columnIndex == CommonUIConstants.COLUMN_INDEX_1) || (columnIndex == CommonUIConstants.COLUMN_INDEX_2) ||
            (columnIndex == CommonUIConstants.COLUMN_INDEX_3)) {
          final Point point = new Point(event.x, event.y);
          // Determine which row was selected
          for (TreeItem item : PIDCSearchAttrSection.this.summaryTreeViewer.getTree().getSelection()) {
            PIDCSearchAttrSection.this.searchPg.getAttrTreeUtil().mouseDownOp(columnIndex, point, item);
          }
        }

      }

      @Override
      public void mouseDoubleClick(final MouseEvent mouseevent) {
        // TO-DO
      }
    });
  }

  /**
   * adds check listener to attributes tree
   */
  private void addCheckListener() {
    this.summaryTreeViewer.addCheckStateListener(new ICheckStateListener() {

      @Override
      public void checkStateChanged(final CheckStateChangedEvent checkstatechangedevent) {
        Object element = checkstatechangedevent.getElement();
        PIDCSearchContentProvider tcp =
            (PIDCSearchContentProvider) PIDCSearchAttrSection.this.summaryTreeViewer.getContentProvider();

        // If the item is checked, check all its children
        if (checkstatechangedevent.getChecked()) {
          PIDCSearchAttrSection.this.searchPg.getAttrTreeUtil().setCheckStateVals(checkstatechangedevent, element, tcp);
        }
        // if children is unchecked , set parent as partially checked
        else {
          PIDCSearchAttrSection.this.searchPg.getAttrTreeUtil().setUnCheckStateVals(checkstatechangedevent, element,
              tcp);
        }
        // update the count once attr-val or used flag is checked state is changed
        PIDCSearchAttrSection.this.searchPg.updateCount();
      }


    });
  }

  /**
   * Add filters to the attribute tree
   */
  private void addFilters() {
    // add type filter
    this.attrFilters = new AttributesFilters();
    this.summaryTreeViewer.addFilter(this.attrFilters);

    // add toolbar filter
    // ICDM-1158
    this.toolBarFilters = new PIDCSearchToolBarFilters(this.searchPg.getEditorInp());
    this.summaryTreeViewer.addFilter(this.toolBarFilters);

    // add outline filters
    this.searchPg.initializeOutlineFilter();
    this.summaryTreeViewer.addFilter(PIDCSearchAttrSection.this.searchPg.getOutlineFilter());
  }

  /* *//**
        * Add sorter for the table columns
        */
  private void invokeColumnSorter() {
    this.summaryTreeViewer.setComparator(this.fcTabSorter);
  }

  /**
   * This method creates filter text
   *
   * @param tableComp
   */
  private void createFilterTxt(final Composite tableComp) {
    this.filterTxt = TextUtil.getInstance().createFilterText(this.searchPg.getFormToolkit(), tableComp,
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));

  }

  /**
   * Create the buttons composite
   */
  private void createButtonComp() {
    Composite attrComp = new Composite(this.attsForm.getBody(), SWT.NONE);
    // three buttons are placed hence three cols
    GridLayout btnLayout = new GridLayout();
    btnLayout.numColumns = SEARCH_BTN_GRP_COLCOUNT;
    btnLayout.makeColumnsEqualWidth = false;
    attrComp.setLayout(btnLayout);
    GridData treeData = new GridData(SWT.END);
    treeData.horizontalAlignment = SWT.RIGHT;
    treeData.grabExcessHorizontalSpace = true;
    attrComp.setLayoutData(treeData);
    // create buttons
    createBtns(attrComp);
  }

  /**
   * Create attr table coulmns
   */
  private void createTabViewerColumns() {
    createName();
    createUsedColViewer();
  }

  /**
   * This method creates PIDC attribute used table viewer column
   */
  private void createUsedColViewer() {
    createAttrUnKnownInfoColumn();
    createAttrUsedInfoNoViewerColumn();
    createAttrUsedInfoYesViewerColumn();
  }

  /**
   * Create attr used info yes column
   */
  private void createAttrUsedInfoYesViewerColumn() {
    TreeColumn attrNameCol = new TreeColumn(this.summaryTreeViewer.getTree(), SWT.LEFT);
    this.summaryTreeViewer.getTree().setLinesVisible(true);
    attrNameCol.setText(TEXT_USED_YES);
    attrNameCol.setWidth(ATTR_COL_WIDTH);
    attrNameCol.setAlignment(SWT.LEFT);
    attrNameCol.setToolTipText(TEXT_USED_YES);
  }

  /**
   * Create attr used info no column
   */
  private void createAttrUsedInfoNoViewerColumn() {
    TreeColumn attrNameCol = new TreeColumn(this.summaryTreeViewer.getTree(), SWT.LEFT);
    this.summaryTreeViewer.getTree().setLinesVisible(true);
    attrNameCol.setAlignment(SWT.LEFT);
    attrNameCol.setText(TEXT_USED_NO);
    attrNameCol.setWidth(ATTR_COL_WIDTH);
    attrNameCol.setToolTipText(TEXT_USED_NO);
  }

  /**
   * Create attr unknown column
   */
  private void createAttrUnKnownInfoColumn() {
    TreeColumn attrNameCol = new TreeColumn(this.summaryTreeViewer.getTree(), SWT.LEFT);
    this.summaryTreeViewer.getTree().setLinesVisible(true);
    attrNameCol.setAlignment(SWT.LEFT);
    attrNameCol.setText(TEXT_UNDEFINED);
    attrNameCol.setWidth(ATTR_INFO_COL_WIDTH);
    attrNameCol.setToolTipText(TEXT_UNDEFINED);
  }

  /**
   * Create attr name column
   */
  private void createName() {
    TreeColumn attrNameCol = new TreeColumn(this.summaryTreeViewer.getTree(), SWT.LEFT);
    this.summaryTreeViewer.getTree().setLinesVisible(true);
    this.summaryTreeViewer.getTree().setTouchEnabled(true);
    attrNameCol.setAlignment(SWT.LEFT);
    attrNameCol.setText(TEXT_ATTRIBUTE_VAL);
    attrNameCol.setToolTipText(TEXT_ATTRIBUTE_VAL);
    attrNameCol.setWidth(ATTR_NAME_COL_WIDTH);
    attrNameCol.getParent().setSortColumn(attrNameCol);
    // ICDM-1564
    TreeViewerColumn attrViewerCol = new TreeViewerColumn(this.summaryTreeViewer, attrNameCol);
    ColumnViewerToolTipSupport.enableFor(attrViewerCol.getViewer(), ToolTip.NO_RECREATE);
    attrViewerCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof Attribute) {
          Attribute attr = (Attribute) element;
          return attr.getName();
        }
        else if (element instanceof AttributeValue) {
          AttributeValue attrVal = (AttributeValue) element;
          return attrVal.getName();
        }
        return "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        if (element instanceof Attribute) {
          Attribute attr = (Attribute) element;
          StringBuilder toolTip = new StringBuilder("Name : ");
          toolTip.append(attr.getName());

          String desc = attr.getDescription();
          if (null != desc) {
            toolTip.append("\nDescription : ").append(desc);
          }
          return toolTip.toString();
        }
        return null;
      }
    });

    attrViewerCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(attrNameCol, 0,
        this.fcTabSorter, this.summaryTreeViewer));

  }

  /**
   * Create label composite
   */
  private void createLabelComp() {
    Composite labelComp = new Composite(this.attsForm.getBody(), SWT.NONE);
    labelComp.setLayout(new GridLayout());
    GridData labelData = new GridData(SWT.END);
    labelData.horizontalAlignment = SWT.LEFT;
    labelData.grabExcessHorizontalSpace = true;
    labelComp.setLayoutData(labelData);
    labelComp.setLayoutData(labelData);
    this.count = new Label(labelComp, SWT.NONE);
    this.count.setText("Number of attributes selected: 0");
    this.count.setLayoutData(new GridData());
  }

  /**
   * This method creates Section ToolBar actions
   */
  private void createToolBarAction() {

    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.attrsSection);
    final Separator separator = new Separator();


    this.toolBarActionSet.pidcAttrCheckedFilterAction(toolBarManager, this.toolBarFilters, this.summaryTreeViewer);// ICDM-1291
    this.toolBarActionSet.pidcAttrNotCheckedFilterAction(toolBarManager, this.toolBarFilters, this.summaryTreeViewer);
    toolBarManager.add(separator);

    this.toolBarActionSet.pidcAttrMandatoryFilterAction(toolBarManager, this.toolBarFilters, this.summaryTreeViewer);

    this.toolBarActionSet.pidcAttrNonMandatoryFilterAction(toolBarManager, this.toolBarFilters, this.summaryTreeViewer);
    toolBarManager.add(separator);

    this.toolBarActionSet.pidcAttrBoolFilterAction(toolBarManager, this.toolBarFilters, this.summaryTreeViewer);
    this.toolBarActionSet.pidcAttrDateFilterAction(toolBarManager, this.toolBarFilters, this.summaryTreeViewer);
    this.toolBarActionSet.pidcAttrLinkFilterAction(toolBarManager, this.toolBarFilters, this.summaryTreeViewer);
    this.toolBarActionSet.pidcAttrNumberFilterAction(toolBarManager, this.toolBarFilters, this.summaryTreeViewer);
    this.toolBarActionSet.pidcAttrTextFilterAction(toolBarManager, this.toolBarFilters, this.summaryTreeViewer);

    toolBarManager.update(true);


    this.attrsSection.setTextClient(toolbar);
  }

  /**
   * @param attrComp
   */
  private void createBtns(final Composite attrComp) {
    addCollapseAll(attrComp);
    addClearAll(attrComp);
    addSearchBtn(attrComp);
  }

  /**
   * Add search button
   *
   * @param attrComp composite
   */
  private void addSearchBtn(final Composite attrComp) {
    Button searchBtn = new Button(attrComp, SWT.PUSH);
    searchBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNC_SEARCH_28X30));
    searchBtn.setText("Search");
    GridData gridData = new GridData(SWT.END);
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.widthHint = SEARCH_BTN_WIDTH;
    gridData.heightHint = BTN_SIZE;
    searchBtn.setToolTipText("Search");
    searchBtn.setLayoutData(gridData);
    searchBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        PIDCSearchAttrSection.this.searchPg.pidcSearch();
      }
    });
  }

  /**
   * add clear all button
   *
   * @param attrComp composite
   */
  private void addClearAll(final Composite attrComp) {
    Button clearAllBtn = new Button(attrComp, SWT.PUSH);
    clearAllBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.CLEAR_CHECKBOX_20X18));
    GridData gridData = new GridData(SWT.END);
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.widthHint = CLEAR_BTN_WIDTH;
    gridData.heightHint = BTN_SIZE;
    clearAllBtn.setText("Clear");
    clearAllBtn.setToolTipText("Clear Search");
    clearAllBtn.setLayoutData(gridData);
    clearAllBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        PIDCSearchAttrSection.this.searchPg.clearAll();
      }
    });
  }

  /**
   * Add collpase all button
   *
   * @param attrComp composite
   * @param treeData tree
   */
  private void addCollapseAll(final Composite attrComp) {
    GridData gridData = new GridData(SWT.END);
    gridData.widthHint = BTN_SIZE;
    gridData.heightHint = BTN_SIZE;
    gridData.verticalAlignment = GridData.END;
    gridData.grabExcessVerticalSpace = true;
    Button collapseBtn = new Button(attrComp, SWT.PUSH);
    collapseBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.TREE_COLLAPSE_16X16));
    collapseBtn.setToolTipText("Collapse All");
    collapseBtn.setLayoutData(gridData);
    collapseBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        IAction collapseAllAction = new CollapseAllAction(PIDCSearchAttrSection.this.summaryTreeViewer, 1);
        collapseAllAction.run();
      }
    });
  }

}
