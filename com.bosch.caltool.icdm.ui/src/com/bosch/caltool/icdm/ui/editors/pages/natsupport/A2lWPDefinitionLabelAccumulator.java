package com.bosch.caltool.icdm.ui.editors.pages.natsupport;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.ui.util.IUIConstants;


/**
 * Label Accumulator class for nattable
 *
 * @author pdh2cob
 */
public class A2lWPDefinitionLabelAccumulator extends ColumnOverrideLabelAccumulator {

  private final ILayer layer;
  private final IRowDataProvider bodyDataProvider;
  private final A2LWPInfoBO a2lWpInfoBo;

  /**
   * @param bodyDataLayer
   * @param bodyDataProvider
   */
  public A2lWPDefinitionLabelAccumulator(final ILayer bodyDataLayer, final IRowDataProvider bodyDataProvider,
      final A2LWPInfoBO a2lWpInfoBo) {
    super(bodyDataLayer);
    this.layer = bodyDataLayer;
    this.bodyDataProvider = bodyDataProvider;
    this.a2lWpInfoBo = a2lWpInfoBo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    int columnIndex = this.layer.getColumnIndexByPosition(columnPosition);
    List<String> overrides = getOverrides(Integer.valueOf(columnIndex));
    if (overrides != null) {
      for (String configLabel : overrides) {
        configLabels.addLabel(configLabel);
      }
    }

    A2lWpResponsibility rowObject = (A2lWpResponsibility) this.bodyDataProvider.getRowObject(rowPosition);

    if (checkDefaultWp(rowObject)) {
      configLabels.addLabel(IUIConstants.CONFIG_LABEL_DEFAULT_WORK_PACKAGE_DISABLED);
      checkSetRespColumn(configLabels, columnPosition);
      return;
    }
    if (this.a2lWpInfoBo.isNotAssignedVarGrp() && !isNotAssignedVarGrp(rowObject)) {
      configLabels.addLabel(IUIConstants.CONFIG_LABEL_VAR_GRP_NOT_MAPPED);
      return;
    }
    if (!this.a2lWpInfoBo.isMappedToSelectedVarGrp(rowObject)) {
      configLabels.addLabel(IUIConstants.CONFIG_LABEL_VAR_GRP_NOT_MAPPED);
      checkSetRespColumn(configLabels, columnPosition);
      return;
    }

    switch (columnPosition) {
      case IUIConstants.COLUMN_INDEX_WP:
        configLabels.addLabel(IUIConstants.CONFIG_LABEL_WP_NAME);
        break;
      case IUIConstants.COLUMN_INDEX_VAR_GROUP:
        configLabels.addLabel(IUIConstants.CONFIG_LABEL_VAR_GRP);
        break;
      case IUIConstants.COLUMN_INDEX_RESP_TYPE:
        configLabels.addLabel(IUIConstants.CONFIG_LABEL_RESP_TYPE);
        break;
      case IUIConstants.COLUMN_INDEX_WP_CUST:
        configLabels.addLabel(IUIConstants.CONFIG_LABEL_WP_NAME_CUST);
        break;
      case IUIConstants.COLUMN_INDEX_RESP:
        addConfigLabelForResp(configLabels, rowObject);
        break;
      case IUIConstants.COLUMN_INDEX_SET_RESP:
        configLabels.addLabel(IUIConstants.CONFIG_LABEL_SET_RESPONSIBLE);
        break;
      default:
        break;
    }


  }

  /**
   * @param configLabels
   * @param rowObject
   */
  private void addConfigLabelForResp(final LabelStack configLabels, A2lWpResponsibility rowObject) {
    configLabels.addLabel(IUIConstants.CONFIG_LABEL_WP_RESPONSIBLE);
    A2lResponsibility a2lResp =
        this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(rowObject.getA2lRespId());
    if ((a2lResp != null) && a2lResp.isDeleted()) {
      configLabels.addLabel(IUIConstants.CONFIG_LABEL_DEL_WP_RESPONSIBLE);
    }
  }

  /**
   * @param configLabels
   * @param columnPosition
   */
  private void checkSetRespColumn(final LabelStack configLabels, final int columnPosition) {
    if (columnPosition == IUIConstants.COLUMN_INDEX_SET_RESP) {
      configLabels.addLabel(IUIConstants.CONFIG_LABEL_SET_RESPONSIBLE_DISABLED);
    }
  }

  /**
   * @param rowObject
   * @return
   */
  private boolean checkDefaultWp(final A2lWpResponsibility rowObject) {
    if (rowObject.getName() != null) {
      return rowObject.getName().equals(ApicConstants.DEFAULT_A2L_WP_NAME);
    }
    return false;
  }

  private boolean isNotAssignedVarGrp(final A2lWpResponsibility a2lWpResponsibility) {
    return (a2lWpResponsibility != null) && (a2lWpResponsibility.getVariantGrpId() == null) &&
        (this.a2lWpInfoBo.getSelectedA2lVarGroup() == null);
  }

}
