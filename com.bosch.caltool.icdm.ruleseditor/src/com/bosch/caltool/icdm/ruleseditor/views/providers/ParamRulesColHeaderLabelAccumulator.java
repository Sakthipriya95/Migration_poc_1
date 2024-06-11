package com.bosch.caltool.icdm.ruleseditor.views.providers;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.ruleseditor.pages.ParametersRulePage;

/**
 * @author jvi6cob
 */
public final class ParamRulesColHeaderLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * ParametersRulePage instance
   */
  private final ParametersRulePage page;

  /**
   * @param layer ILayer
   * @param page ParametersRulePage
   */
  public ParamRulesColHeaderLabelAccumulator(final ILayer layer, final ParametersRulePage page) {
    super(layer);
    this.page = page;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
    // rules header
    if (rowPosition == (this.page.getRuleFilterGridLayer().getColumnHeaderDataProvider().getRowCount() - 1)) {
      configLabels.addLabel("RULE_HEADER_LABEL");
    }
    // dependency attrs header
    else if ((rowPosition != (this.page.getRuleFilterGridLayer().getColumnHeaderDataProvider().getRowCount() - 1)) &&
        (columnPosition == CommonUIConstants.COLUMN_INDEX_6)) {
      configLabels.addLabel("ATTR_HEADER_LABEL");
    }
    // value header
    else if ((rowPosition != (this.page.getRuleFilterGridLayer().getColumnHeaderDataProvider().getRowCount() - 1)) &&
        (columnPosition > CommonUIConstants.COLUMN_INDEX_6)) {
      configLabels.addLabel("VALUE_HEADER_LABEL");
    }
  }
}