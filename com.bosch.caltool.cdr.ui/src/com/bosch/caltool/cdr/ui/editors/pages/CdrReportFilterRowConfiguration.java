/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.ICellEditor;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;
import org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;

/**
 * @author mkl2cob
 */
class CdrReportFilterRowConfiguration extends AbstractRegistryConfiguration {

  /**
   * CdrReportListPage - dataRvwReportPage
   */
  private final CdrReportListPage dataRvwReportPage;

  /**
   * numOfColumns - column count
   */
  private final int numOfColumns;

  /**
   * @param noOfColumns       column count
   * @param dataRvwReportPage DataRvwReportPage
   */
  public CdrReportFilterRowConfiguration(final CdrReportListPage dataRvwReportPage, final int noOfColumns) {
    this.dataRvwReportPage = dataRvwReportPage;
    this.numOfColumns = noOfColumns;
  }

  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {
    // override the default filter row configuration for painter
    configRegistry.registerConfigAttribute(CELL_PAINTER,
        new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);

    // enable filter comparator for cols
    for (int index = 0; index <= this.numOfColumns; index++) {
      // register config attr for each col
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + index);
    }

    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultDisplayConverter() {

      @Override
      public Object canonicalToDisplayValue(final Object canonicalValue) {
        if (canonicalValue instanceof Boolean) {
          return "";
        }
        return super.canonicalToDisplayValue(canonicalValue);
      }
    }, NORMAL);


    List<String> comboList = Arrays.asList(CommonUtils.concatenate(ApicConstants.REVIEWED, " "),
        ApicConstants.NOT_REVIEWED, ApicConstants.NEVER_REVIEWED);
    // register a combo box cell editor
    // for the gender column in the filter row
    // the label is set automatically to the value of
    // FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + column position
    ICellEditor comboBoxCellEditor = new ComboBoxCellEditor(comboList);
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
        FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.RVWD_COL_INDEX);
    // Configure Filter Display Converter
    // Display converter used to convert
    // the string typed by the user to the data type of the column
    configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
        new FilterDisplayConverter(), NORMAL,
        FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.RVWD_COL_INDEX);

    // List of all possible Questionnaire Response Status
    List<String> qniareStatusList = Arrays.asList(CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getUiType(),
        CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getUiType(), CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getUiType());


    ComboBoxCellEditor comboBoxCellEditor1 = new ComboBoxCellEditor(qniareStatusList);

    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor1, NORMAL,
        FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.RVW_QNAIRE_STATUS_COL_INDEX);
    // Configure Filter Display Converter
    configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
        new FilterDisplayConverter(), NORMAL,
        FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.RVW_QNAIRE_STATUS_COL_INDEX);
    // List of all possible Review Scores
    List<String> scoreComboList = new ArrayList<>();
    List<DATA_REVIEW_SCORE> scoreEnums = DATA_REVIEW_SCORE.getApplicableScore(null);
    for (DATA_REVIEW_SCORE score : scoreEnums) {
      scoreComboList.add(score.getScoreDisplay());
    }
    for (int i = CdrReportListPage.STATIC_COL_INDEX; i < this.dataRvwReportPage.getPropertyToLabelMap().size(); i++) {
      // register a combo box cell editor
      // for the gender column in the filter row
      // the label is set automatically to the value of
      // FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + column position
      ICellEditor scoreComboBoxCellEditor = new ComboBoxCellEditor(scoreComboList, scoreComboList.size());
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, scoreComboBoxCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + i);
      // Configure Filter Display Converter
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
          new FilterDisplayConverter(), NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + i);
    }
  }


  /**
   * @author jvi6cob
   */
  private final class FilterDisplayConverter extends DisplayConverter {

    @Override
    public Object displayToCanonicalValue(final Object val) {
      // NA
      return null;
    }

    @Override
    public Object canonicalToDisplayValue(final Object val) {
      if ((val instanceof String) && ((String) val).isEmpty()) {
        return ApicConstants.NEVER_REVIEWED;
      }
      return val;
    }
  }
}