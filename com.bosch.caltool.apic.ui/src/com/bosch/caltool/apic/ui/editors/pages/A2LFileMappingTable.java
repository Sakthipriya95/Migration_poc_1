/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;


/**
 * This class creates the a2l file mapping table
 *
 * @author bru2cob
 */
public class A2LFileMappingTable {

  /**
   * A2l file column width
   */
  private static final int A2LFILE_COL_WIDTH = 400;
  /**
   * pver variant column width
   */
  private static final int PVER_VARIANT_COL_WIDTH = 100;
  /**
   * pver name col width
   */
  private static final int PVER_NAME_COL_WIDTH = 100;
  /**
   * version selection col width
   */
  private static final int VERSION_SEL_COL_WIDTH = 25;
  /**
   * pidc version col width
   */
  private static final int PIDC_VERSION_COL_WIDTH = 90;
  /**
   * Date column width
   */
  private static final int DATE_COL_WIDTH = 180;
  /**
   * User col width
   */
  private static final int USER_COL_WIDTH = 200;
  /**
   * GridTableViewer instance
   */
  private CustomGridTableViewer a2lTabViewer;

  private static final int COLWIDTH_ACTIVE = 50;

  private static final int COLWIDTH_PAR2WP = 60;

  private static final int COLWIDTH_PAR2WP_ACTIVE_VERS = 150;


  /**
   * @return the a2lTabViewer
   */
  public CustomGridTableViewer getA2lTabViewer() {
    return this.a2lTabViewer;
  }

  /**
   * Instance of a2l file page
   */
  private final A2LFilePage a2lFilePg;
  private Collection<PidcA2lFileExt> pidcA2lFiles;

  /**
   * @param a2lFilePg
   */
  public A2LFileMappingTable(final A2LFilePage a2lFilePg) {
    super();
    this.a2lFilePg = a2lFilePg;
  }

  /**
   * Create a2l table mapping viewer
   */
  public void createA2lTable() {
    // create the table viewer
    this.a2lTabViewer = new CustomGridTableViewer(this.a2lFilePg.getForm().getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);

    // initialize status line manager
    this.a2lFilePg.initializeEditorStatusLineManager(this.a2lTabViewer);

    // Setting 'Description' column grid item's tooltip to empty string. This is to disable the default tooltip
    GridItem[] items = this.a2lTabViewer.getGrid().getItems();
    for (GridItem gridItem : items) {
      gridItem.setToolTipText(ApicUiConstants.COLUMN_INDEX_1, CommonUIConstants.EMPTY_STRING);
    }
    final GridData gridData = new GridData();
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    this.a2lTabViewer.getGrid().setLayoutData(gridData);
    this.a2lTabViewer.getGrid().setLinesVisible(true);
    this.a2lTabViewer.getGrid().setHeaderVisible(true);
    final GridLayout gridLayout = new GridLayout();
    this.a2lFilePg.getForm().getBody().setLayout(gridLayout);


    // create table columns
    createA2LTabColumns();
    // set the content provider for the table
    this.a2lTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    // set table input
    setTableInput();
    // Invoke TableViewer Column sorters
    this.a2lTabViewer.setComparator(this.a2lFilePg.getRightsTabSorter());


    // add mouse down listener
    this.a2lFilePg.addMouseDownListener();

    this.a2lFilePg.addRightClickListener();
    // add filters
    this.a2lTabViewer.addFilter(this.a2lFilePg.getA2lFileFilters());


    // add selection listener
    this.a2lTabViewer.addSelectionChangedListener(event -> {
      final IStructuredSelection selection =
          (IStructuredSelection) A2LFileMappingTable.this.a2lTabViewer.getSelection();

      if (!selection.isEmpty()) {
        final List<PidcA2lFileExt> listA2lFiles = selection.toList();
        A2LFileMappingTable.this.a2lFilePg.getSelA2LFile().clear();
        A2LFileMappingTable.this.a2lFilePg.getSelA2LFile().addAll(listA2lFiles);
      }
    });

    // set the selection provider
    PIDCEditor pidcEditor = (PIDCEditor) this.a2lFilePg.getEditor();
    SelectionProviderMediator selectionProviderMediator = pidcEditor.getSelectionProviderMediator();
    selectionProviderMediator.addViewer(this.a2lTabViewer);
    this.a2lFilePg.getSite().setSelectionProvider(selectionProviderMediator);

  }

  /**
   * Set the table input
   */
  public void setTableInput() {
    this.pidcA2lFiles = fetchTableData().values();
    this.a2lTabViewer.setInput(this.pidcA2lFiles);
  }


  /**
   *
   */
  public void refreshTable() {
    this.pidcA2lFiles = this.a2lFilePg.getHandler().getAllPidcA2lMap().values();
    this.a2lTabViewer.setInput(this.pidcA2lFiles);
    this.a2lTabViewer.refresh();
  }

  /**
   * Create a2l table columns
   */
  private void createA2LTabColumns() {
    // create version col
    createPidcVersionCol();
    // create version selction column
    createVerSelcCol();
    // create PIDCA2L active/inactive column
    createActiveCol();

    // create Par2WP column
    createPar2WPCol();
    // create Par2WP Active Version column
    createPar2WPActiveCol();
    // create pver name col
    createPverNameCol();
    // create pver variant col
    createPverVariantCol();
    // create a2l file col
    createA2LFileCol();

    createSSDVesionCol();
    // created date col
    createa2lOriginalDateCol();
    // create date column
    createDateCol();
    // create user column
    createUserCol();

  }

  /**
   *
   */
  private void createPar2WPActiveCol() {
    GridColumn par2WPActiveVersGridCol = new GridColumn(this.a2lTabViewer.getGrid(), SWT.CENTER);
    par2WPActiveVersGridCol.setWidth(COLWIDTH_PAR2WP_ACTIVE_VERS);
    par2WPActiveVersGridCol.setSummary(false);
    GridViewerColumn par2WPActiveVersCol = new GridViewerColumn(this.a2lTabViewer, par2WPActiveVersGridCol);
    par2WPActiveVersCol.getColumn().setText("Par2WP Active Version");
    par2WPActiveVersCol.getColumn().setWidth(COLWIDTH_PAR2WP_ACTIVE_VERS);
    par2WPActiveVersCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        PidcA2lFileExt pidcA2lFileExt = (PidcA2lFileExt) element;
        if ((pidcA2lFileExt.getPidcA2l() != null) && pidcA2lFileExt.getPidcA2l().isActiveWpParamPresentFlag()) {
          return "\u2714";// UTF-8 encoding for Tick symbol
        }
        return "";
      }

    });
    // Add column selection listener
    par2WPActiveVersCol.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(par2WPActiveVersCol.getColumn(),
            ApicUiConstants.COLUMN_INDEX_4, this.a2lFilePg.getRightsTabSorter(), this.a2lTabViewer));

  }

  /**
   *
   */
  private void createPar2WPCol() {
    GridColumn par2WPGridCol = new GridColumn(this.a2lTabViewer.getGrid(), SWT.CENTER);
    par2WPGridCol.setWidth(COLWIDTH_PAR2WP);
    par2WPGridCol.setSummary(false);
    GridViewerColumn par2WPCol = new GridViewerColumn(this.a2lTabViewer, par2WPGridCol);
    par2WPCol.getColumn().setText("Par2WP");
    par2WPCol.getColumn().setWidth(COLWIDTH_PAR2WP);
    par2WPCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        PidcA2lFileExt pidcA2l = (PidcA2lFileExt) element;
        if ((pidcA2l.getPidcA2l() != null) && pidcA2l.getPidcA2l().isWpParamPresentFlag()) {
          return "\u2714";// UTF-8 encoding for Tick symbol
        }
        return "";
      }

    });
    // Add column selection listener
    par2WPCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        par2WPCol.getColumn(), ApicUiConstants.COLUMN_INDEX_3, this.a2lFilePg.getRightsTabSorter(), this.a2lTabViewer));

  }

  /**
   *
   */
  private void createActiveCol() {
    GridColumn activeGridCol = new GridColumn(this.a2lTabViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    activeGridCol.setWidth(COLWIDTH_ACTIVE);
    activeGridCol.setSummary(false);
    GridViewerColumn activeColumn = new GridViewerColumn(this.a2lTabViewer, activeGridCol);
    activeColumn.getColumn().setText("Active");
    activeColumn.getColumn().setWidth(COLWIDTH_ACTIVE);

    activeColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        if (element instanceof PidcA2lFileExt) {
          PidcA2lFileExt pidcA2lFileExt = (PidcA2lFileExt) element;
          if (null != pidcA2lFileExt.getPidcA2l()) {
            if (pidcA2lFileExt.getPidcA2l().isActive()) {
              final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
              gridItem.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
              gridItem.setChecked(cell.getVisualIndex(), true);
              isActiveEditable(cell,
                  A2LFileMappingTable.this.a2lFilePg.getDataHandler().getPidcVersionBO().isModifiable() &&
                      (pidcA2lFileExt.getPidcA2l().getPidcVersId() != null),
                  gridItem);
            }
            else {
              final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
              gridItem.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
              gridItem.setChecked(cell.getVisualIndex(), false);
              isActiveEditable(cell,
                  A2LFileMappingTable.this.a2lFilePg.getDataHandler().getPidcVersionBO().isModifiable() &&
                      (pidcA2lFileExt.getPidcA2l().getPidcVersId() != null),
                  gridItem);
            }
          }
          else {
            // A2LFile is never assigned to a PIDC Version
            final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
            gridItem.setChecked(cell.getVisualIndex(), false);
            isActiveEditable(cell, false, gridItem);
          }
        }
      }
    });
    setEditingSupportForActiveColumn(activeColumn);

    // Add column selection listener
    activeColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(activeColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_2, this.a2lFilePg.getRightsTabSorter(), this.a2lTabViewer));
  }

  /**
   * @param activeColumn
   */
  private void setEditingSupportForActiveColumn(final GridViewerColumn activeColumn) {
    activeColumn.setEditingSupport(new CheckEditingSupport(activeColumn.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        PidcA2l pidcA2l = ((PidcA2lFileExt) arg0).getPidcA2l();
        if ((pidcA2l != null) && (pidcA2l.getPidcVersId() != null)) {
          pidcA2l.setActive((boolean) arg1);
          Set<PidcA2l> pidcA2ls = new HashSet<>();
          pidcA2ls.add(pidcA2l);
          A2LFileMappingTable.this.a2lFilePg.getHandler().updatePidcA2l(pidcA2ls);
        }
      }
    });
  }


  private void isActiveEditable(final ViewerCell cell, final boolean isModifiable, final GridItem gridItem) {
    gridItem.setCheckable(cell.getVisualIndex(), isModifiable);
  }


  /**
  *
  */
  private void createSSDVesionCol() {
    GridViewerColumn dateColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.a2lTabViewer,
        "SSD software version", DATE_COL_WIDTH);


    dateColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * method to get the a2l file date string
       */
      @Override
      public String getText(final Object element) {

        PidcA2lFileExt a2lFileData = (PidcA2lFileExt) element;
        String ssdSoftwareVersion = "";
        if (null != a2lFileData.getPidcA2l()) {
          ssdSoftwareVersion = a2lFileData.getPidcA2l().getSsdSoftwareVersion();
        }
        return ssdSoftwareVersion;
      }
    });

    // Add column selection listener
    dateColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(dateColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_8, this.a2lFilePg.getRightsTabSorter(), this.a2lTabViewer));

  }

  /**
   * Create user col viewer
   */
  private void createUserCol() {
    GridViewerColumn dateColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.a2lTabViewer, "Assigned User", USER_COL_WIDTH);
    dateColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String user = "";
        PidcA2lFileExt a2lFileData = (PidcA2lFileExt) element;
        if (null != a2lFileData.getPidcA2l()) {
          user = new PidcA2LBO(a2lFileData.getPidcA2l().getId(), a2lFileData).getAssignedUser();
        }
        return user != null ? user : "";

      }
    });
    // Add column selection listener
    dateColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(dateColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_11, this.a2lFilePg.getRightsTabSorter(), this.a2lTabViewer));

  }


  /**
   * Create date col viewer
   */
  private void createDateCol() {
    GridViewerColumn dateColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.a2lTabViewer, "Assigned Date", DATE_COL_WIDTH);
    dateColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        // assign the latest date
        String latestDate = "";
        PidcA2lFileExt a2lFileData = (PidcA2lFileExt) element;
        if (null != a2lFileData.getPidcA2l()) {
          latestDate = new PidcA2LBO(a2lFileData.getPidcA2l().getId(), a2lFileData).getAssignedDateFormatted();
        }
        return latestDate == null ? "" : latestDate;
      }
    });
    // Add column selection listener
    dateColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(dateColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_10, this.a2lFilePg.getRightsTabSorter(), this.a2lTabViewer));
  }


  /**
   * Create A2L file column
   */
  private void createA2LFileCol() {
    GridViewerColumn a2lFileColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.a2lTabViewer, "A2L File", A2LFILE_COL_WIDTH);
    a2lFileColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        PidcA2lFileExt pidcA2lFileExt = (PidcA2lFileExt) element;

        return ((pidcA2lFileExt.getPidcA2l() == null) || (pidcA2lFileExt.getPidcA2l().getVcdmA2lName() == null))
            ? pidcA2lFileExt.getA2lFile().getFilename() : pidcA2lFileExt.getPidcA2l().getVcdmA2lName();
      }
    });
    // Add column selection listener
    a2lFileColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(a2lFileColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_7, this.a2lFilePg.getRightsTabSorter(), this.a2lTabViewer));
  }

  /**
   * Create over variant column
   */
  private void createPverVariantCol() {
    GridViewerColumn pverVariantColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.a2lTabViewer,
        "PVER Variant", PVER_VARIANT_COL_WIDTH);

    pverVariantColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return CommonUtils.checkNull(((PidcA2lFileExt) element).getA2lFile().getSdomPverVariant());
      }
    });
    // Add column selection listener
    pverVariantColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(pverVariantColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_6, this.a2lFilePg.getRightsTabSorter(), this.a2lTabViewer));
  }

  /**
   * Create Pver name column
   */
  private void createPverNameCol() {
    GridViewerColumn pverNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.a2lTabViewer, "PVER Name", PVER_NAME_COL_WIDTH);
    pverNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return CommonUtils.checkNull(((PidcA2lFileExt) element).getA2lFile().getSdomPverName());
      }
    });
    // Add column selection listener
    pverNameColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(pverNameColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_5, this.a2lFilePg.getRightsTabSorter(), this.a2lTabViewer));
  }

  /**
   * Create version selection column
   */
  private void createVerSelcCol() {
    ColumnViewerToolTipSupport.enableFor(this.a2lTabViewer, ToolTip.NO_RECREATE);
    GridViewerColumn versionSelColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.a2lTabViewer, "", VERSION_SEL_COL_WIDTH);
    versionSelColumn.setLabelProvider(new ColumnLabelProvider() {

      private Image editImage;

      /**
       * Get the value edit image {@inheritDoc}
       */
      @Override
      public Image getImage(final Object element) {
        if (this.editImage == null) {
          this.editImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_12X12);
          return this.editImage;
        }
        return this.editImage;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        return "Select Version to assign this a2l file";
      }
    });
  }

  /**
   * Create pidc version column
   */
  private void createPidcVersionCol() {
    GridViewerColumn pidcVerColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.a2lTabViewer,
        "PIDC Version", PIDC_VERSION_COL_WIDTH);


    pidcVerColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {

        PidcA2lFileExt a2lFileData = (PidcA2lFileExt) element;
        if (null != a2lFileData.getPidcVersion()) {
          return a2lFileData.getPidcVersion().getVersionName();
        }

        return "";
      }
    });


    // Add column selection listener
    pidcVerColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(pidcVerColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_0, this.a2lFilePg.getRightsTabSorter(), this.a2lTabViewer));
  }


  /**
   * Create date col viewer
   */
  private void createa2lOriginalDateCol() {
    GridViewerColumn dateColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.a2lTabViewer,
        "A2L Created Date", DATE_COL_WIDTH);
    dateColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * method to get the a2l file date string
       */
      @Override
      public String getText(final Object element) {
        // ICDM-1671
        PidcA2lFileExt a2lFileData = (PidcA2lFileExt) element;
        return null != a2lFileData.getA2lFile().getFiledate() ? a2lFileData.getA2lFile().getFiledate() : "";
      }
    });
    // Add column selection listener
    dateColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(dateColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_9, this.a2lFilePg.getRightsTabSorter(), this.a2lTabViewer));

  }


  /**
   * @return
   */
  public Map<Long, PidcA2lFileExt> fetchTableData() {
    return this.a2lFilePg.getHandler().getAllA2lByPidc(this.a2lFilePg.getPidcVersion().getPidcId());
  }


  /**
   * @return the pidcA2lFiles
   */
  public Collection<PidcA2lFileExt> getPidcA2lFiles() {
    return Collections.unmodifiableCollection(this.pidcA2lFiles);
  }

}
