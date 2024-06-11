/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages.natsupport;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.ui.util.IUIConstants;


/***
 * This class extends {@link ColumnOverrideLabelAccumulator} which is used for registration/addition of labels for a
 * given column.
 *
 * @author apj4cob
 */
public class WPLabelAssignNatPageLabelAccumulator extends ColumnOverrideLabelAccumulator {


  private final ILayer layer;
  private final IRowDataProvider<?> bodyDataProvider;
  private final A2LWPInfoBO a2lWpInfoBo;


  /**
   * @param bodyDataLayer ILayer
   * @param bodyDataProvider IRowDataProvider
   * @param a2lWpInfoBo A2LWPInfoBO
   */
  public WPLabelAssignNatPageLabelAccumulator(final ILayer bodyDataLayer, final IRowDataProvider<?> bodyDataProvider,
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
    A2LWpParamInfo rowObject = (A2LWpParamInfo) this.bodyDataProvider.getRowObject(rowPosition);
    A2lResponsibility a2lResp = null;
    if (rowObject.isWpRespInherited()) {
      A2lWpResponsibility a2lWpResponsibility =
          this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().get(rowObject.getWpRespId());
      a2lResp = this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap()
          .get(a2lWpResponsibility.getA2lRespId());
    }
    else {
      a2lResp = this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(rowObject.getA2lRespId());
    }
    if ((columnPosition == CommonUIConstants.COLUMN_INDEX_0)) {
      configLabels.addLabel(CDRConstants.MULTI_IMAGE_PAINTER);

      if (rowObject.isComplianceParam()) {
        configLabels.addLabel(CDRConstants.COMPLIANCE_LABEL);
      }
      if (rowObject.isBlackList()) {
        configLabels.addLabel(CDRConstants.BLACK_LIST_LABEL);
      }
      if (rowObject.isQssdParameter()) {
        configLabels.addLabel(CDRConstants.QSSD_LABEL);
      }
      if (rowObject.isReadOnly()) {
        configLabels.addLabel(CDRConstants.READ_ONLY);
      }
      if (rowObject.isDependentParameter()) {
        configLabels.addLabel(CDRConstants.DEPENDENT_CHARACTERISTICS);
      }
      if ((null == rowObject.getA2lWpParamMappingId()) && ((null != this.a2lWpInfoBo.getSelectedA2lVarGroup()) &&
          !CommonUtils.isNullOrEmpty(
              this.a2lWpInfoBo.getVirtualRecordsMap().get(this.a2lWpInfoBo.getSelectedA2lVarGroup().getId())) &&
          this.a2lWpInfoBo.getVirtualRecordsMap().get(this.a2lWpInfoBo.getSelectedA2lVarGroup().getId())
              .containsKey(rowObject.getParamId()))) {
        configLabels.addLabel(CDRConstants.VIRTUAL_INDICATOR);
      }
    }
    if ((this.a2lWpInfoBo.isNotAssignedVarGrp() && (null == this.a2lWpInfoBo.getSelectedA2lVarGroup()) &&
        !isNotAssignedVarGrp(rowObject)) ||
        ((null != this.a2lWpInfoBo.getSelectedA2lVarGroup()) &&
            !this.a2lWpInfoBo.isParamMappedToSelectedVarGrp(rowObject))) {
      configLabels.addLabel("PIDC_LEVEL_ASSIGNMENT");
    }
    if ((columnPosition == 8) && (a2lResp != null) && a2lResp.isDeleted()) {
      configLabels.addLabel(IUIConstants.CONFIG_LABEL_DEL_WP_RESPONSIBLE);
    }
    if (((columnPosition == 7) || (columnPosition == 8)) && rowObject.isWpRespInherited()) {
      configLabels.addLabel("INHERITED_VAL");
    }
    if ((columnPosition == 9) && rowObject.isWpNameInherited()) {
      configLabels.addLabel("INHERITED_VAL");
    }
  }


  private boolean isNotAssignedVarGrp(final A2LWpParamInfo a2lWpParamInfo) {
    A2lWpResponsibility a2lWpResponsibility = this.a2lWpInfoBo.getWpRespPalForA2lWpMapping(a2lWpParamInfo);
    return (a2lWpResponsibility != null) && (a2lWpResponsibility.getVariantGrpId() == null) &&
        (this.a2lWpInfoBo.getSelectedA2lVarGroup() == null);
  }
}

