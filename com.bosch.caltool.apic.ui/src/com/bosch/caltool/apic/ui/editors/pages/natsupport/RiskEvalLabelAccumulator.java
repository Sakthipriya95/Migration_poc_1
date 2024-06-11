package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import java.util.Map;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByObject;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.apic.ui.editors.pages.RiskEvalNatTableSection;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.RiskEvalCellStyleConfig.RM_STYLE_CODE;
import com.bosch.caltool.icdm.client.bo.apic.PidcRMCharacterMapping;
import com.bosch.caltool.icdm.model.rm.RmRiskLevel.RISK_LEVEL_CONFIG;

/**
 * This class provides labels for body datalayer of the GridLayer of the NatTable.
 */
public class RiskEvalLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /** NAT table Data Layer. */
  private final ILayer layer;

  /** The nat ctrl. */
  private final RiskEvalNatTableSection natCtrl;

  /**
   * Constructor.
   *
   * @param layer data layer
   * @param natCtrl the nat ctrl
   * @param resultParentMap the result parent map
   */
  public RiskEvalLabelAccumulator(final ILayer layer, final RiskEvalNatTableSection natCtrl,
      final Map<Long, PidcRMCharacterMapping> resultParentMap) {
    super(layer);
    this.layer = layer;
    this.natCtrl = natCtrl;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    // get the row object out of the dataprovider
    @SuppressWarnings("unchecked")
    IRowDataProvider<PidcRMCharacterMapping> bodyDataProvider =
        (IRowDataProvider<PidcRMCharacterMapping>) ((DataLayer) this.layer).getDataProvider();
    Object rowObject = bodyDataProvider.getRowObject(rowPosition);
    if (rowObject instanceof GroupByObject) {
      super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
    }
    else if (rowObject instanceof PidcRMCharacterMapping) {
      PidcRMCharacterMapping param = (PidcRMCharacterMapping) rowObject;
      // GREY OUT CHILD NODES
      if ((param.getParentProjCharId() != 0l) && ((this.natCtrl.getParentDataMap() != null) &&
          this.natCtrl.getParentDataMap().containsKey(param.getParentProjCharId()))) {
        boolean isParentNotRelevant = this.natCtrl.getParentDataMap().get(param.getParentProjCharId()).isRelevantNo();
        // If parent is defined not relevant, grey out children
        if (!isParentNotRelevant || !param.isVisible()) {
          setConfigLabelToDisabled(configLabels, columnPosition);
        }
        else {
          setColumnConfig(configLabels, columnPosition, param);
        }
      }
      else {
        setColumnConfig(configLabels, columnPosition, param);
      }
    }
    super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
  }

  /**
   * Sets the column config.
   *
   * @param configLabels the config labels
   * @param columnPosition the column position
   * @param param the param
   */
  public void setColumnConfig(final LabelStack configLabels, final int columnPosition,
      final PidcRMCharacterMapping param) {

    if (columnPosition == RiskEvalNatTableSection.RELEVANT_YES_COLNUM) {
      configLabels.addLabel(RM_STYLE_CODE.RELEVANT_YES.getStyleCode());
    }
    else if (columnPosition == RiskEvalNatTableSection.RELEVANT_NO_COLNUM) {
      configLabels.addLabel(RM_STYLE_CODE.RELEVANT_NO.getStyleCode());
    }
    else if (columnPosition == RiskEvalNatTableSection.RELEVANT_NA_COLNUM) {
      configLabels.addLabel(RM_STYLE_CODE.RELEVANT_NA.getStyleCode());
    }
    else if (columnPosition == RiskEvalNatTableSection.RB_SW_SHARE_COLNUM) {
      if (param.isRelevantYes()) {
        configLabels.addLabel(RiskEvalCellStyleConfig.RB_SHARE_COMBO);
      }
      else {
        setConfigLabelToDisabled(configLabels, columnPosition);
      }
    }
    else if (columnPosition == RiskEvalNatTableSection.RB_INIT_DATA_COLNUM) {
      if (param.isRelevantYes()) {
        configLabels.addLabel(RiskEvalCellStyleConfig.RB_INPUT_DATA_COMBO);
      }
      else {
        setConfigLabelToDisabled(configLabels, columnPosition);
      }
    }
    else if ((param.getRiskImpactMap() != null) && (columnPosition > RiskEvalNatTableSection.RB_INIT_DATA_COLNUM)) {
      String data = param.getRiskImpactMap().get(columnPosition);
      String label = getRiskBckgroundConfig(data);
      configLabels.addLabel(label);
    }
  }

  /**
   * @param data
   * @return
   */
  private String getRiskBckgroundConfig(final String data) {
    if (this.natCtrl.getResultHandler().getAllRiskCodeMap().get(data) != null) {
      return this.natCtrl.getResultHandler().getAllRiskCodeMap().get(data);
    }
    return RISK_LEVEL_CONFIG.RISK_LVL_NA.getCode();
  }

  /**
   * Sets the config label to disabled.
   *
   * @param configLabels the config labels
   * @param columnPosition REMOVE existing checkbox label to add the new one
   */
  private void setConfigLabelToDisabled(final LabelStack configLabels, final int columnPosition) {
    // Grey out all cells
    configLabels.addLabel(RiskEvalCellStyleConfig.ROW_GREYED);
    switch (columnPosition) {
      case RiskEvalNatTableSection.RELEVANT_YES_COLNUM:
        configLabels.removeLabel(RM_STYLE_CODE.RELEVANT_YES.getStyleCode());
        configLabels.addLabel(RM_STYLE_CODE.RELEVANT_YES_DISABLED.getStyleCode());
        break;
      case RiskEvalNatTableSection.RELEVANT_NO_COLNUM:
        configLabels.removeLabel(RM_STYLE_CODE.RELEVANT_NO.getStyleCode());
        configLabels.addLabel(RM_STYLE_CODE.RELEVANT_NO_DISABLED.getStyleCode());
        break;
      case RiskEvalNatTableSection.RELEVANT_NA_COLNUM:
        configLabels.removeLabel(RM_STYLE_CODE.RELEVANT_NA.getStyleCode());
        configLabels.addLabel(RM_STYLE_CODE.RELEVANT_NA_DISABLED.getStyleCode());
        break;

    }
  }
}
