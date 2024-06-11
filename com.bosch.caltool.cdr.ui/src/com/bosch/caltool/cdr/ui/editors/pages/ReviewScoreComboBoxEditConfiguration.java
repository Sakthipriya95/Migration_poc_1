/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.painter.cell.ComboBoxPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;

import com.bosch.caltool.icdm.client.bo.cdr.DataReviewScoreUtil;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;


/**
 * Review score combo box edit configuration
 *
 * @author bru2cob
 */
public class ReviewScoreComboBoxEditConfiguration extends AbstractRegistryConfiguration {

  /**
   * instance of review type
   */
  private final REVIEW_TYPE reviewType;

  /**
   * @param reviewType Review Type
   */
  public ReviewScoreComboBoxEditConfiguration(final REVIEW_TYPE reviewType) {
    this.reviewType = reviewType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {
    // set the style for editable cell
    Style style = new Style();
    style.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    style.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.DEFAULT_FONT);

    // set the style for read only
    Style styleReadOnly = new Style();
    styleReadOnly.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(204, 198, 176));
    styleReadOnly.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.DEFAULT_FONT);

    // combo box values
    List<String> comboList = new ArrayList<>();
    // get the scores to be set in combo
    List<DATA_REVIEW_SCORE> scoreEnums = DATA_REVIEW_SCORE.getApplicableScore(this.reviewType);
    for (DATA_REVIEW_SCORE score : scoreEnums) {
      comboList.add(DataReviewScoreUtil.getInstance().getScoreDisplayExt(score));
    }
    // set the combobox config for score
    getComboStyle(configRegistry, "COMBO_SCORE", IEditableRule.ALWAYS_EDITABLE, comboList, style);
    // set the combobox config for score readonly
    getComboStyle(configRegistry, "COMBO_SCORE_READONLY", IEditableRule.NEVER_EDITABLE, comboList, style);


  }

  /**
   * Registers the combo box style
   *
   * @param configRegistry
   * @param comboList2
   */
  private void getComboStyle(final IConfigRegistry configRegistry, final String label, final IEditableRule editRule,
      final List<String> comboList, final Style style) {
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, DisplayMode.NORMAL, label);

    ComboBoxCellEditor comboBoxCellEditor = new ComboBoxCellEditor(comboList, 10);
    // register for editable or not editable
    comboBoxCellEditor.setFreeEdit(editRule == IEditableRule.ALWAYS_EDITABLE);


    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultDisplayConverter() {


      /**
       * {@inheritDoc}
       */
      @Override
      public Object canonicalToDisplayValue(final Object canonicalValue) {
        if (canonicalValue instanceof RvwQnaireAnswer) {
          return "";
        }
        return super.canonicalToDisplayValue(canonicalValue);
      }
    }, DisplayMode.NORMAL, label);
    // register the cell properties
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, DisplayMode.EDIT,
        label);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new ComboBoxPainter(), DisplayMode.NORMAL,
        label);

    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, editRule, DisplayMode.NORMAL,
        label);
  }

}
