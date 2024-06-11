/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCSearResToolBarActionSet;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.sorter.PIDCResultSorter;
import com.bosch.caltool.apic.ui.table.filters.PIDCResultFilters;
import com.bosch.caltool.apic.ui.table.filters.PIDCSearchResultToolBarFilters;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.client.bo.apic.PIDCScoutResult;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.text.TextUtil;


/**
 * PIDC search results section
 *
 * @author bru2cob
 */
public class PIDCSearchResultSection {

  /**
   * integer constant for single selection
   */
  private static final int SINGLE_SELECTION = 1;

  /**
   * Pidc name column width
   */
  private static final int PIDC_NAME_COL_WIDTH = 120;

  /**
   * Pidc column Width
   */
  private static final int PIDC_IMG_COL_WIDTH = 40;
  /**
   * column widht-120
   */
  private static final int ATTR_LEVEL_WIDTH = 120;
  /**
   * Section to display pidc search result
   */
  private Section resultSection;
  /**
   * Form to display pidc search result
   */
  private Form resultForm;
  /**
   * Filter text for results table
   */
  private Text resultFilterTxt;
  /**
   * PIDC result filter
   */
  private PIDCResultFilters resultFilters;
  /**
   * Sorter for pidcs
   */
  private PIDCResultSorter resultTabSorter;
  /**
   * Search page instance
   */
  private final PIDCSearchPage searchPg;
  /**
   * Table which displays the pidc search results
   */
  private CustomGridTableViewer resultTable;
  private PIDCSearchResultToolBarFilters toolBarFilters;


  /**
   * @return the resultTable
   */
  public CustomGridTableViewer getResultTable() {
    return this.resultTable;
  }


  /**
   * @param searchPg pidc search page instance
   */
  public PIDCSearchResultSection(final PIDCSearchPage searchPg) {
    super();
    this.searchPg = searchPg;
  }

  /**
   * Create the section to display the search results
   */
  public void createResultsSection() {
    this.resultSection = this.searchPg.getFormToolkit().createSection(this.searchPg.getSashForm(),
        Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.resultSection.setText("Search Results");
    this.resultSection.setExpanded(true);
    this.resultSection.getDescriptionControl().setEnabled(false);
    createResultForm();
    this.resultSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.resultSection.setClient(this.resultForm);

  }

  /**
   * Create result form in the section
   */
  private void createResultForm() {


    this.resultForm = this.searchPg.getFormToolkit().createForm(this.resultSection);
    this.resultForm.getBody().setLayout(new GridLayout());
    this.resultForm.setLayoutData(GridDataUtil.getInstance().getGridData());
    // Create Filter text
    this.resultFilterTxt =
        TextUtil.getInstance().createFilterText(this.searchPg.getFormToolkit(), this.resultForm.getBody(),
            GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    createResultTable();
    createToolBarAction();
    addRightClickMenu();
  }

  // ICDM-1192
  /**
   * Right click context menu for parameters
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(manager -> {
      final IStructuredSelection selection =
          (IStructuredSelection) PIDCSearchResultSection.this.resultTable.getSelection();
      final Object firstElement = selection.getFirstElement();
      if ((firstElement != null) && (!selection.isEmpty())) {
        if (selection.size() > SINGLE_SELECTION) {
          List<IProjectObject> pidVersions = getPidVersionsFromSelection(selection);
          new PIDCActionSet().setCompareAction(manager, pidVersions, null);
        }
        else {
          PIDCScoutResult result = (PIDCScoutResult) firstElement;
          PIDCActionSet actionSet = new PIDCActionSet();
          // copy link to clipBoard
          actionSet.copytoClipBoard(manager, result.getPidcVersion());
          // ICDM-1232 - Send link of PIDC as email
          actionSet.sendPidcVersionLinkInOutlook(manager, result.getPidcVersion());
        }
      }
    });

    final Menu menu = menuMgr.createContextMenu(this.resultTable.getControl());
    this.resultTable.getControl().setMenu(menu);
  }

  /**
   * @param selection selected item
   */
  private List<IProjectObject> getPidVersionsFromSelection(final IStructuredSelection selection) {
    Iterator<?> pidcVers = selection.iterator();
    List<IProjectObject> retList = new ArrayList<>();
    while (pidcVers.hasNext()) {
      PIDCScoutResult result = (PIDCScoutResult) pidcVers.next();
      PidcVersion selVer = result.getPidcVersion();
      retList.add(selVer);
    }

    return retList;

  }


  /**
   * result table which holds the search results
   */
  private void createResultTable() {
    this.resultTabSorter = new PIDCResultSorter();
    this.resultTable = new CustomGridTableViewer(this.resultForm.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
    // initialise the status bar
    this.searchPg.initializeEditorStatusLineManager(this.resultTable);

    this.resultTable.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());

    this.resultTable.getGrid().setLinesVisible(true);
    this.resultTable.getGrid().setHeaderVisible(true);

    this.resultForm.getBody().setLayout(new GridLayout());
    addResultFilters();
    addModifyListenerForFilterTxt();
    createTabColumns();
    this.resultTable.setContentProvider(ArrayContentProvider.getInstance());
    this.resultTable.addDoubleClickListener(evt -> {
      IStructuredSelection sel = (IStructuredSelection) evt.getSelection();
      PIDCScoutResult result = (PIDCScoutResult) sel.getFirstElement();
      final PIDCActionSet actionset = new PIDCActionSet();
      // open the editor with that PIDC
      final PIDCEditor openPIDCEditor = actionset.openPIDCEditor(result.getPidcVersion(), false);
      // ICDM-2182
      if (null != openPIDCEditor) {
        // set focus to the editor opened.
        openPIDCEditor.setFocus();
      }

    });

    // ICDM-1213
    this.resultTable.addSelectionChangedListener(evt -> {
      IStructuredSelection sel = (IStructuredSelection) evt.getSelection();
      PIDCScoutResult result = (PIDCScoutResult) sel.getFirstElement();
      if (result != null) {
        PIDCSearchResultSection.this.searchPg.getDataHandler().setSelectedPidcVersion(result.getPidcVersion());
        CommandState expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
        expReportService.setExportService(true);
      }
    });

    ColumnViewerToolTipSupport.enableFor(this.resultTable, ToolTip.NO_RECREATE);
    invokeResultColumnSorter();
    // ICDM-1158
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.resultTable.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes,
        new CustomDragListener(this.resultTable));
  }

  /**
   * Add sorter for the table columns
   */
  private void invokeResultColumnSorter() {
    this.resultTable.setComparator(this.resultTabSorter);
  }

  /**
   * Add filters to the result table
   */
  private void addResultFilters() {
    this.resultFilters = new PIDCResultFilters(this.searchPg);
    this.resultTable.addFilter(this.resultFilters);

    this.toolBarFilters = new PIDCSearchResultToolBarFilters();
    this.resultTable.addFilter(this.toolBarFilters);

  }

  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {
    this.resultFilterTxt.addModifyListener(event -> {
      final String text = PIDCSearchResultSection.this.resultFilterTxt.getText().trim();
      PIDCSearchResultSection.this.resultFilters.setFilterText(text);
      PIDCSearchResultSection.this.resultTable.refresh();
    });
  }

  /**
   * Create pidc search results table
   */
  private void createTabColumns() {
    createImageCol();
    createNameCol();
    createLevelsCol();

  }

  /**
   * Icdm-1283 new Column for Showing images
   */
  private void createImageCol() {
    final GridViewerColumn imgColumn = new GridViewerColumn(this.resultTable, SWT.NONE);
    imgColumn.getColumn().setWidth(PIDC_IMG_COL_WIDTH);

    imgColumn.setLabelProvider(new ColumnLabelProvider() {

      // iCDM-2255
      /**
       * to get tool tip text for the pidc result
       */
      @Override
      public String getToolTipText(final Object element) {
        String toolTipTextString = "";
        if (element instanceof PIDCScoutResult) {
          final PIDCScoutResult pidcResult = (PIDCScoutResult) element;
          toolTipTextString = pidcResult.getFormattedToolTipText();
        }
        return toolTipTextString;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return null;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Image getImage(final Object element) {
        // ICDM-2255
        final PIDCScoutResult pidcResult = (PIDCScoutResult) element;
        ImageKeys baseImageKey;
        if (pidcResult.hasReviews()) {
          baseImageKey = ImageKeys.RVW_RES_CLOSED_16X16;
        }
        else if (pidcResult.hasA2lFiles()) {
          baseImageKey = ImageKeys.A2LFILE_16X16;
        }
        else {
          baseImageKey = ImageKeys.PIDC_16X16;
        }
        if (pidcResult.hasFocusMatrix()) {
          // ICDM-2255
          return ImageManager.getDecoratedImage(baseImageKey, ImageKeys.MAPPED_FOCUS_MATRIX_ICON_OVERLAY,
              IDecoration.TOP_RIGHT);
        }
        return ImageManager.getInstance().getRegisteredImage(baseImageKey);
      }

    });

    // Add column selection listener
    imgColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(imgColumn.getColumn(), 0, this.resultTabSorter, this.resultTable));

  }


  /**
   * Creates the level columns
   */
  private void createLevelsCol() {
    // Set the level attribute name as column titles
    Map<Long, Attribute> levelAttrMap = this.searchPg.getDataHandler().getLevelAttrs();
    for (long level = 1; level <= levelAttrMap.size(); level++) {
      final GridViewerColumn levelColumn = new GridViewerColumn(this.resultTable, SWT.NONE);

      Attribute attr = levelAttrMap.get(level);
      levelColumn.getColumn().setText(attr.getName());

      levelColumn.getColumn().setWidth(ATTR_LEVEL_WIDTH);

      // Keep level attr id in the column, for later usage
      levelColumn.getColumn().setData("level", level);

      levelColumn.setLabelProvider(new ColumnLabelProvider() {

        @Override
        public String getText(final Object element) {
          final PIDCScoutResult pidcResult = (PIDCScoutResult) element;
          Long lvl = (Long) levelColumn.getColumn().getData("level");
          return pidcResult.getLevelValueText(lvl);
        }
      });
      // Add column selection listener
      levelColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
          .getSelectionAdapter(levelColumn.getColumn(), (int) (level + 1), this.resultTabSorter, this.resultTable));
    }

  }

  /**
   * Create name column for the result table
   */
  private void createNameCol() {
    final GridViewerColumn nameColumn = new GridViewerColumn(this.resultTable, SWT.NONE);
    nameColumn.getColumn().setText("PIDC Name");
    nameColumn.getColumn().setWidth(PIDC_NAME_COL_WIDTH);
    nameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return ((PIDCScoutResult) element).getPidcVersion().getName();
      }


    });
    // Add column selection listener
    nameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(nameColumn.getColumn(), 1, this.resultTabSorter, this.resultTable));

  }

  /**
   * Icdm-1283 tool bar action set for PIDC with A2l filea and with review. This method creates Section ToolBar actions
   */
  private void createToolBarAction() {

    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.resultSection);
    final Separator separator = new Separator();
    PIDCSearResToolBarActionSet toolBarActionSet = new PIDCSearResToolBarActionSet();

    // iCDM-2255 starts
    toolBarActionSet.pidcWithNoFocusMatrix(toolBarManager, this.toolBarFilters, this.resultTable);
    toolBarActionSet.pidcWithFocusMatrix(toolBarManager, this.toolBarFilters, this.resultTable);
    toolBarManager.add(separator);
    // iCDM-2255 ends

    toolBarActionSet.pidcAWithNoA2lFilterAction(toolBarManager, this.toolBarFilters, this.resultTable);
    toolBarActionSet.pidcA2lFilesFilterAction(toolBarManager, this.toolBarFilters, this.resultTable);// ICDM-1291
    toolBarActionSet.pidcReviewFilesFilterAvtion(toolBarManager, this.toolBarFilters, this.resultTable);
    toolBarManager.add(separator);


    toolBarManager.update(true);


    this.resultSection.setTextClient(toolbar);
  }

}
