package com.bosch.caltool.icdm.ruleseditor.pages;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.AbstractOverrider;

import com.bosch.caltool.icdm.client.bo.cdr.RuleDefinition;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;

/**
 * This class provides labels for body datalayer of the GridLayer of the NatTable
 *
 * @author dmo5cob
 */
public class RulesLabelAccumulator extends AbstractOverrider {

  /**
   * static cols index
   */
  private static final int STATIC_COLS_INDEX = 6;
  private final IRowDataProvider<RuleDefinition> bodyDataProvider;

  /**
   * Constructor
   *
   * @param bodyDataProvider IRowDataProvider
   */
  RulesLabelAccumulator(final IRowDataProvider<RuleDefinition> bodyDataProvider) {
    this.bodyDataProvider = bodyDataProvider;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    // get the row object out of the dataprovider
    RuleDefinition rowObject = this.bodyDataProvider.getRowObject(rowPosition);
    if (rowObject.isDefaultRule()) {
      if ((columnPosition == CommonUIConstants.COLUMN_INDEX_3) && (null != rowObject.getMaturityLevel())) { // ICDM-1313
        configLabels.addLabel(rowObject.getMaturityLevel());
      }
      configLabels.addLabel("DEFAULT");
      return;
    }
    else if ((columnPosition == CommonUIConstants.COLUMN_INDEX_3) && rowObject.getRuleMapping().isEmpty()) {
      configLabels.addLabel(rowObject.getMaturityLevel());
    }
    if (columnPosition > STATIC_COLS_INDEX) {

      for (Integer colIndex : rowObject.getRuleMappedIndex().keySet()) {
        String maturityLevel = rowObject.getRuleMappedIndex().get(colIndex);

        if (columnPosition == colIndex) {
          configLabels.addLabel(maturityLevel);
          configLabels.addLabel(CDRConstants.TICK);
        }
      }
    }
  }
}
