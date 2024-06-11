package com.bosch.caltool.icdm.ruleseditor.views.providers;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

import com.bosch.caltool.icdm.client.bo.cdr.RuleDefinition;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.pages.ParametersRulePage;

/**
 * ICDM-1200
 *
 * @author mkl2cob
 */
public class ParamRulesPageNatToolTip extends NatTableContentTooltip {

  /**
   * nattable instance
   */
  private final NatTable natTableObj;
  /**
   * ParametersRulePage instance
   */
  private final ParametersRulePage page;

  /**
   * header size
   */
  private final int headerSize;

  /**
   * @param natTable NatTable
   * @param headerSize int
   * @param page ParametersRulePage
   */
  public ParamRulesPageNatToolTip(final NatTable natTable, final int headerSize, final ParametersRulePage page) {
    super(natTable);
    this.natTableObj = natTable;
    this.headerSize = headerSize;
    this.page = page;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getText(final Event event) {
    int col = this.natTableObj.getColumnPositionByX(event.x);
    int row = this.natTableObj.getRowPositionByY(event.y);
    ILayerCell cellByPosition = this.natTableObj.getCellByPosition(col, row);
    if (cellByPosition != null) {
      LabelStack configLabels = cellByPosition.getConfigLabels();
      if (configLabels.hasLabel("FILTER_ROW")) {
        return null;
      }

      if (row > this.headerSize) {
        String maturityLevelFromRule =
            fetchMaturityLevelFromRule(cellByPosition.getRowIndex(), cellByPosition.getColumnIndex());
        if (null == maturityLevelFromRule) {
          return super.getText(event);
        }
        return maturityLevelFromRule;

      }

    }
    return super.getText(event);
  }

  /**
   * @param col column number
   * @param row row number
   * @return CDRRule
   */
  private String fetchMaturityLevelFromRule(final int row, final int col) {
    RuleDefinition ruleDefinition =
        (RuleDefinition) this.page.getRuleFilterGridLayer().getBodyDataProvider().getRowObject(row);
    ReviewRule cdrRule = ruleDefinition.getRuleMapping().get(String.valueOf(col));
    // If rule exists return maturity level
    if (cdrRule != null) {
      return RuleMaturityLevel.getIcdmMaturityLevelText(cdrRule.getMaturityLevel());
    }
    if ((ruleDefinition.isDefaultRule() || (null == ruleDefinition.getRuleMapping()) ||
        ruleDefinition.getRuleMapping().isEmpty()) && (col == CommonUIConstants.COLUMN_INDEX_2)) {// ICDM-1313
      return RuleMaturityLevel.getIcdmMaturityLevelText(ruleDefinition.getMaturityLevel());
    }
    return null;
  }
}