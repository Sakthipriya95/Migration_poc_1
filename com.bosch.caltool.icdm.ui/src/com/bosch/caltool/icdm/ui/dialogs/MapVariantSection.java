/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.client.bo.a2l.VariantMapClientModel;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.ui.sorters.MapVariantToVarGrpSorter;
import com.bosch.caltool.icdm.ui.table.filters.MapVaraintTableFilter;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * Creates pidc version details section in PIDCAttrValueEditDialog and AddValueDialog in case of creating a new PIDC
 *
 * @author dmo5cob
 */
public class MapVariantSection {


  /**
   * Number of columns in the section
   */
  private static final int NMBR_COLS_SECTION = 1;
  /**
   * Section version details
   */
  private Section sectionProjVrsn;
  /**
   * Composite version details
   */
  private final Composite composite;
  /**
   * Form version details
   */
  private Form formProjVrsn;


  /**
   * FormToolkit instance
   */
  private final FormToolkit formToolKit;
  /**
   * Ok button
   */
  private Button okBtn;

  private GridTableViewer valTableViewer;
  private MapVariantToVarGrpSorter valTabSorter;

  private final Set<VariantMapClientModel> changedModelList = new HashSet<>();
  private MapVaraintTableFilter valTabFilter;
  private Text filterTxt;


  /**
   * @param composite Composite
   * @param formToolKit FormToolkit
   * @param isEdit update or not
   */
  public MapVariantSection(final Composite composite, final FormToolkit formToolKit) {
    this.composite = composite;
    this.formToolKit = formToolKit;


  }


  /**
   * This method initializes section
   */
  public void createVarMapSection() {

    this.sectionProjVrsn = SectionUtil.getInstance().createSection(this.composite, this.formToolKit,
        GridDataUtil.getInstance().getGridData(), "Attched Variants");
    this.sectionProjVrsn.getDescriptionControl().setEnabled(false);

    createVarGrpForm();
    this.sectionProjVrsn.setClient(this.formProjVrsn);
  }

  /**
   * create the form of version details
   */
  private void createVarGrpForm() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = NMBR_COLS_SECTION;
    this.valTabSorter = new MapVariantToVarGrpSorter();
    this.formProjVrsn = this.formToolKit.createForm(this.sectionProjVrsn);
    this.formProjVrsn.getBody().setLayout(gridLayout);

    createFilterTxt();
    createValGridTabViewer();


  }


  /**
   * This method creates the pidc/variant/sub-variant attribute value gridtableviewer
   *
   * @param gridData
   */
  private void createValGridTabViewer() {
    this.valTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.formProjVrsn.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(200));

    // Create GridViewerColumns
    createValGridViewerColumns();
    addFilters();

  }


  /**
   * This method creates filter text
   */
  private void createFilterTxt() {


    this.filterTxt = TextUtil.getInstance().createFilterText(this.formToolKit, this.formProjVrsn.getBody(),
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      final String text = MapVariantSection.this.filterTxt.getText().trim();
      MapVariantSection.this.valTabFilter.setFilterText(text);
      MapVariantSection.this.valTableViewer.refresh();

    });
    // ICDM-183
    this.filterTxt.setFocus();
  }


  /**
   * This method adds the filter instance to pidc/variant/sub-variant attribute value
   */
  private void addFilters() {
    this.valTabFilter = new MapVaraintTableFilter();
    // Add PIDC Attribute TableViewer filter
    this.valTableViewer.addFilter(this.valTabFilter);
  }


  /**
   * This method adds Columns to the gridtableviewer
   */
  private void createValGridViewerColumns() {
    // Creates PIDC attribute value column
    createVarNameCol();
    // Creates PIDC attribute value description column
    createVarDescCol();
    // create PIDC PIDC attribute value status column
    createisMappedCol();

    // create PIDC attribute Char Val Column
    createOtherVarGrpCol();
  }

  /**
   * This method adds pidc attribute value description column to the gridtableviewer
   */
  private void createVarDescCol() {
    final GridViewerColumn valDescColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.valTableViewer, "Variant Description", 150);
    // Add column selection listener
    valDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(valDescColumn.getColumn(), 1, this.valTabSorter, this.valTableViewer));

    valDescColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof VariantMapClientModel) {
          VariantMapClientModel model = (VariantMapClientModel) element;
          return model.getVariantDesc();
        }
        return "";
      }


    });

  }

  /**
   * Icdm-956 Create a new Column for Attr Char Value
   */
  private void createOtherVarGrpCol() {
    final GridViewerColumn attrCharColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.valTableViewer, "Used in other Varaint group", 250);
    // Add column selection listener
    attrCharColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        attrCharColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_3, this.valTabSorter, this.valTableViewer));

    attrCharColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof VariantMapClientModel) {
          VariantMapClientModel model = (VariantMapClientModel) element;
          return model.getOtherVarGroupName();
        }
        return "";

      }

    });
  }

  /**
   * This method creates PIDCAttrVal Clr Stat Column
   */
  private void createisMappedCol() {
    final GridViewerColumn clrStatusColumn = new GridViewerColumn(this.valTableViewer, SWT.CHECK);
    clrStatusColumn.getColumn().setText("Relevant");
    clrStatusColumn.getColumn().setWidth(100);
    clrStatusColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        Object element = cell.getElement();
        GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
        if (element instanceof VariantMapClientModel) {
          VariantMapClientModel vargrpModel = (VariantMapClientModel) element;
          // Get the user info final
          gridItem.setChecked(cell.getVisualIndex(), vargrpModel.isMapped());

        }
      }
    });
    clrStatusColumn.setEditingSupport(new CheckEditingSupport(clrStatusColumn.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        MapVariantSection.this.okBtn.setEnabled(true);
        VariantMapClientModel vargrpModel = (VariantMapClientModel) arg0;
        vargrpModel.setMapped((Boolean) arg1);
        MapVariantSection.this.changedModelList.add(vargrpModel);

      }
    });
    // Add column selection listener
    clrStatusColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(clrStatusColumn.getColumn(), 2, this.valTabSorter, this.valTableViewer));
  }

  /**
   * This method adds pidc attribute value column to the gridtableviewer
   */
  private void createVarNameCol() {
    final String colName = "Variant Name";
    final GridViewerColumn attrValColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.valTableViewer, colName, 150);
    // Add column selection listener
    attrValColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrValColumn.getColumn(), 0, this.valTabSorter, this.valTableViewer));
    attrValColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof VariantMapClientModel) {
          VariantMapClientModel model = (VariantMapClientModel) element;
          return model.getVariantName();
        }
        return "";
      }

    });
  }


  /**
   * @return the okBtn
   */
  public Button getOkBtn() {
    return this.okBtn;
  }


  /**
   * @param okBtn the okBtn to set
   */
  public void setOkBtn(final Button okBtn) {
    this.okBtn = okBtn;
  }


  /**
   * @return the changedModelList
   */
  public Set<VariantMapClientModel> getChangedModelList() {
    return new HashSet<>(this.changedModelList);
  }


  /**
   * @param varMapModelList varMapModelList
   */
  public void setTabInput(final SortedSet<VariantMapClientModel> varMapModelList) {
    this.valTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.valTableViewer.setInput(varMapModelList);

  }
}
