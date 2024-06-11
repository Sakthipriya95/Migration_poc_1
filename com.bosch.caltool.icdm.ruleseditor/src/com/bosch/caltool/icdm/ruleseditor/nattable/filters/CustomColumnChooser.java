/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.nattable.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.nebula.widgets.nattable.Messages;
import org.eclipse.nebula.widgets.nattable.columnChooser.ColumnChooserUtils;
import org.eclipse.nebula.widgets.nattable.columnChooser.ColumnEntry;
import org.eclipse.nebula.widgets.nattable.columnChooser.gui.ColumnChooserDialog;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.RuleDependency;
import com.bosch.caltool.icdm.ruleseditor.pages.ParametersRulePage;
import com.bosch.caltool.icdm.ruleseditor.views.providers.AttrValColumnHeaderDataProvider;


/**
 * @author jvi6cob
 */
public class CustomColumnChooser {


  /**
   * FIXED_COLUMN_COUNT
   */
  private static final int FIXED_COLUMN_COUNT = 6;

  private static final Comparator<ColumnEntry> COLUMN_ENTRY_LABEL_COMPARATOR = new Comparator<ColumnEntry>() {

    @Override
    public int compare(final ColumnEntry columnEntry1, final ColumnEntry columnEntry2) {
      return columnEntry1.getLabel().compareToIgnoreCase(columnEntry2.getLabel());
    }
  };

  /**
   * ColumnChooserDialog instance
   */
  protected final ColumnChooserDialog columnChooserDialog;
  /**
   * ColumnHideShowLayer instance
   */
  protected final ColumnHideShowLayer columnHideShowLayer;
  /**
   * DataLayer instance
   */
  protected final DataLayer columnHeaderDataLayer;
  /**
   * ColumnHeaderLayer instance
   */
  protected final ColumnHeaderLayer columnHeaderLayer;
  /**
   * List<ColumnEntry>
   */
  protected List<ColumnEntry> hiddenColumnEntries;
  /**
   * List<ColumnEntry>
   */
  protected List<ColumnEntry> visibleColumnsEntries;
  /**
   * ColumnGroupModel instance
   */
  protected final ColumnGroupModel columnGroupModel;
  /**
   * SelectionLayer instance
   */
  protected final SelectionLayer selectionLayer;
  /**
   * sortAvailableColumns flag
   */
  protected final boolean sortAvailableColumns;
  /**
   * AttrValueCombChooserDialog instance
   */
  private final AttrValueCombChooserDialog attrValueCombChooserDialog;

  /**
   * @param shell Shell
   * @param selectionLayer SelectionLayer
   * @param columnHideShowLayer ColumnHideShowLayer
   * @param columnHeaderLayer ColumnHeaderLayer
   * @param columnHeaderDataLayer DataLayer
   * @param columnGroupModel ColumnGroupModel
   * @param sortAvailableColumns boolean
   * @param combiMap Map<Attribute, String>
   * @param paramRulesPage ParametersRulePage
   */
  public CustomColumnChooser(final Shell shell, final SelectionLayer selectionLayer,
      final ColumnHideShowLayer columnHideShowLayer, final ColumnHeaderLayer columnHeaderLayer,
      final DataLayer columnHeaderDataLayer, final ColumnGroupModel columnGroupModel,
      final boolean sortAvailableColumns, final Map<Attribute, String> combiMap,
      final ParametersRulePage paramRulesPage) {
    this.selectionLayer = selectionLayer;
    this.columnHideShowLayer = columnHideShowLayer;
    this.columnHeaderLayer = columnHeaderLayer;
    this.columnHeaderDataLayer = columnHeaderDataLayer;
    this.columnGroupModel = columnGroupModel;
    this.sortAvailableColumns = sortAvailableColumns;


    this.attrValueCombChooserDialog = new AttrValueCombChooserDialog(shell, combiMap, paramRulesPage);
    this.columnChooserDialog = new ColumnChooserDialog(shell, Messages.getString("ColumnChooser.availableColumns"), //$NON-NLS-1$
        Messages.getString("ColumnChooser.selectedColumns")); //$NON-NLS-1$
  }

  /**
   * @param dialogSettings IDialogSettings
   */
  public void setDialogSettings(final IDialogSettings dialogSettings) {
    this.columnChooserDialog.setDialogSettings(dialogSettings);
  }

  /**
   * open dialog
   */
  public void openDialog() {
    this.attrValueCombChooserDialog.open();

    this.visibleColumnsEntries = ColumnChooserUtils.getVisibleColumnsEntries(this.columnHideShowLayer,
        this.columnHeaderLayer, this.columnHeaderDataLayer);
    this.hiddenColumnEntries = ColumnChooserUtils.getHiddenColumnEntries(this.columnHideShowLayer,
        this.columnHeaderLayer, this.columnHeaderDataLayer);
    List<ColumnEntry> allColumnEntries = new ArrayList<>();
    allColumnEntries.addAll(this.visibleColumnsEntries);
    allColumnEntries.addAll(this.hiddenColumnEntries);

    AttrValColumnHeaderDataProvider attrValColHeaderDataProvider =
        (AttrValColumnHeaderDataProvider) this.columnHeaderDataLayer.getDataProvider();
    Map<Integer, RuleDependency> colCombiMap = attrValColHeaderDataProvider.getParamModel().getRuleDependencyMap();
    List<ColumnEntry> columnEntryToHide = new ArrayList<>();
    List<ColumnEntry> columnEntryToShow = new ArrayList<>();
    Map<Attribute, String> reqCombiMap = this.attrValueCombChooserDialog.getCombiMap();
    for (ColumnEntry visibleColEntry : allColumnEntries) {
      boolean showColumnEntry = true;
      if (visibleColEntry.getIndex() <= FIXED_COLUMN_COUNT) {
        continue;
      }

      Map<Attribute, AttributeValueModel> dpCombiMap =
          colCombiMap.get(visibleColEntry.getIndex()).getAttrAttrValModelMap();
      for (Entry<Attribute, String> reqMapEntry : reqCombiMap.entrySet()) {
        if (checkNotUsed(dpCombiMap, reqMapEntry) || checkUsed(dpCombiMap, reqMapEntry) ||
            checkNotDefined(dpCombiMap, reqMapEntry) || checkDefinedValues(dpCombiMap, reqMapEntry)) {
          if (!this.hiddenColumnEntries.contains(visibleColEntry)) {
            columnEntryToHide.add(visibleColEntry);
          }
          showColumnEntry = false;
          break;
        }
      }
      if (showColumnEntry && !this.visibleColumnsEntries.contains(visibleColEntry)) {
        columnEntryToShow.add(visibleColEntry);
      }
    }
    if (!columnEntryToHide.isEmpty()) {
      ColumnChooserUtils.hideColumnEntries(columnEntryToHide, CustomColumnChooser.this.columnHideShowLayer);
    }
    if (!columnEntryToShow.isEmpty()) {
      ColumnChooserUtils.showColumnEntries(columnEntryToShow, CustomColumnChooser.this.columnHideShowLayer);
    }
  }

  /**
   * Checks whether to hide columns when selected filter is an attr value [<i>true</i> - hides;<i>false</i> - unhides]
   *
   * @param dpCombiMap
   * @param reqMapEntry
   * @return
   */
  private boolean checkDefinedValues(final Map<Attribute, AttributeValueModel> dpCombiMap,
      final Entry<Attribute, String> reqMapEntry) {
    return !reqMapEntry.getValue().equals(ApicConstants.ANY) &&
        !ApicConstants.NOT_USED.equals(reqMapEntry.getValue()) &&
        !ApicConstants.ATTR_NOT_DEFINED.equals(reqMapEntry.getValue()) &&
        ((dpCombiMap.get(reqMapEntry.getKey()) == null) ||
            (!dpCombiMap.get(reqMapEntry.getKey()).getValue().getName().equals(reqMapEntry.getValue())));
  }

  /**
   * Checks whether to hide columns when selected filter is NOT-DEFINED [<i>true</i> - hides;<i>false</i> - unhides]
   *
   * @param dpCombiMap
   * @param reqMapEntry
   * @return
   */
  private boolean checkNotDefined(final Map<Attribute, AttributeValueModel> dpCombiMap,
      final Entry<Attribute, String> reqMapEntry) {
    return ApicConstants.ATTR_NOT_DEFINED.equals(reqMapEntry.getValue()) &&
        (dpCombiMap.get(reqMapEntry.getKey()) != null);
  }

  /**
   * Checks whether to hide columns when selected filter is NOT-USED [<i>true</i> - hides;<i>false</i> - unhides]
   *
   * @param dpCombiMap
   * @param reqMapEntry
   * @return
   */
  private boolean checkNotUsed(final Map<Attribute, AttributeValueModel> dpCombiMap,
      final Entry<Attribute, String> reqMapEntry) {
    return ApicConstants.NOT_USED.equals(reqMapEntry.getValue()) && !((dpCombiMap.get(reqMapEntry.getKey()) != null) &&
        ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID.equals(dpCombiMap.get(reqMapEntry.getKey()).getValue().getId()));
  }

  /**
   * Checks whether to hide columns when selected filter is USED
   *
   * @param dpCombiMap
   * @param reqMapEntry
   * @return
   */
  private boolean checkUsed(final Map<Attribute, AttributeValueModel> dpCombiMap,
      final Entry<Attribute, String> reqMapEntry) {
    return ApicConstants.USED.equals(reqMapEntry.getValue()) && !((dpCombiMap.get(reqMapEntry.getKey()) != null) &&
        ApicConstants.ATTR_VAL_USED_VALUE_ID.equals(dpCombiMap.get(reqMapEntry.getKey()).getValue().getId()));
  }

  /**
   * @return List<ColumnEntry>
   */
  protected List<ColumnEntry> getHiddenColumnEntries() {
    List<ColumnEntry> columnEntries = ColumnChooserUtils.getHiddenColumnEntries(this.columnHideShowLayer,
        this.columnHeaderLayer, this.columnHeaderDataLayer);
    if (this.sortAvailableColumns) {
      Collections.sort(columnEntries, COLUMN_ENTRY_LABEL_COMPARATOR);
    }
    return columnEntries;
  }

}
