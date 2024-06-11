/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.a2lcomparison;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.client.bo.a2l.A2lParamColumnDataMapper;
import com.bosch.caltool.icdm.client.bo.a2l.A2lParamCompareRowObject;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.ui.util.IUIConstants;


/**
 * This class extends {@link ColumnOverrideLabelAccumulator} which is used for registration/addition of labels for a
 * given column.
 *
 * @author bru2cob
 */
class A2lParamCompareLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * Instance variable which holds a reference to bodyDataLayer
   */
  private final ILayer layer;
  /**
   * Instance variable which holds a reference to bodyDataProvider
   */
  private final IRowDataProvider<A2lParamCompareRowObject> bodyDataProvider;
  /**
   * To compare other A2l with the first A2l displayed in the ui
   */
  private static final int REF_COL_NUM = 4;
  /**
   * Column after which column corresponding to other A2l for comparision starts
   */
  private static final int COMPARE_START_COL_NO = 8;

  /**
   * @param bodyDataLayer ILayer instance
   * @param bodyDataProvider IRowDataProvider instance
   */
  public A2lParamCompareLabelAccumulator(final ILayer bodyDataLayer,
      final IRowDataProvider<A2lParamCompareRowObject> bodyDataProvider) {
    super(bodyDataLayer);
    this.layer = bodyDataLayer;
    this.bodyDataProvider = bodyDataProvider;

  }


  /**
   * This method is used to add labels to a cell
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

    A2lParamCompareRowObject compareRowObject = this.bodyDataProvider.getRowObject(rowPosition);
    A2LWpParamInfo paramInfo = null;
    if (columnIndex > 2) {
      A2LWPInfoBO a2lWpInfoBo = compareRowObject.getA2lWpInfoBO(columnIndex);
      paramInfo = compareRowObject.getParamInfo(columnIndex);
      if (paramInfo != null) {
        // set a2l variant group corresponding to the column in a2l wp info BO
        a2lWpInfoBo.setSelectedA2lVarGroup(
            compareRowObject.getA2lColumnDataMapper().getColumnIndexA2lVarGrpMap().get(columnIndex));
        if ((a2lWpInfoBo.isNotAssignedVarGrp() && (null == a2lWpInfoBo.getSelectedA2lVarGroup()) &&
            !isNotAssignedVarGrp(paramInfo, a2lWpInfoBo)) ||
            paramNotMappedToSelectedA2lVarGrp(paramInfo, a2lWpInfoBo)) {
          configLabels.addLabel("PIDC_LEVEL_ASSIGNMENT");
        }
      }
    }

    if (columnIndex > COMPARE_START_COL_NO) {
      addLabelsToConfigBasedOnColName(configLabels, columnIndex, compareRowObject, paramInfo);
    }

    if ((columnPosition == CommonUIConstants.COLUMN_INDEX_0)) {
      addLabelsToConfigBasedOnA2lParamInfo(configLabels, compareRowObject);
    }
  }


  /**
   * @param configLabels
   * @param compareRowObject
   */
  private void addLabelsToConfigBasedOnA2lParamInfo(final LabelStack configLabels,
      final A2lParamCompareRowObject compareRowObject) {
    configLabels.addLabel(CDRConstants.MULTI_IMAGE_PAINTER);
    int colIndex;
    // size gives dynamic cols + 3 static cols = total cols
    int totalColCount = compareRowObject.getA2lColumnDataMapper().getColumnIndexA2lWpDefVersIdMap().size() + 3;
    A2LWpParamInfo a2lParamInfoBo = null;
    for (colIndex = 3; colIndex < totalColCount; colIndex = colIndex + 6) {
      a2lParamInfoBo = compareRowObject.getParamInfo(colIndex);
      if (a2lParamInfoBo != null) {
        break;
      }
    }
    if (a2lParamInfoBo != null) {
      if (a2lParamInfoBo.isComplianceParam()) {
        configLabels.addLabel(CDRConstants.COMPLIANCE_LABEL);
      }
      if (a2lParamInfoBo.isBlackList()) {
        configLabels.addLabel(CDRConstants.BLACK_LIST_LABEL);
      }
      if (a2lParamInfoBo.isQssdParameter()) {
        configLabels.addLabel(CDRConstants.QSSD_LABEL);
      }
      if (a2lParamInfoBo.isReadOnly()) {
        configLabels.addLabel(CDRConstants.READ_ONLY);
      }
    }
  }


  /**
   * @param configLabels
   * @param columnIndex
   * @param compareRowObject
   * @param paramInfo2
   */
  private void addLabelsToConfigBasedOnColName(final LabelStack configLabels, final int columnIndex,
      final A2lParamCompareRowObject compareRowObject, A2LWpParamInfo paramInfo) {
    A2LWPInfoBO a2lWpInfoBo1 = compareRowObject.getA2lWpInfoBO(columnIndex);
    A2LWPInfoBO a2lWpInfoBo2 = compareRowObject.getA2lWpInfoBO(REF_COL_NUM);
    paramInfo = compareRowObject.getParamInfo(columnIndex);
    A2LWpParamInfo paramInfo1 = compareRowObject.getParamInfo(REF_COL_NUM);
    A2lParamColumnDataMapper a2lParamColDataMapper = compareRowObject.getA2lColumnDataMapper();
    A2lVariantGroup a2lVarGrp1 = a2lParamColDataMapper.getColumnIndexA2lVarGrpMap().get(columnIndex);
    A2lVariantGroup a2lVarGrp2 = a2lParamColDataMapper.getColumnIndexA2lVarGrpMap().get(REF_COL_NUM);
    String colName = compareRowObject.getA2lColumnDataMapper().getColumnIndexFlagMap().get(columnIndex);

    switch (colName) {
      case IUIConstants.FUNCTION_NAME:
        if (compareRowObject.isFuncDiff(paramInfo, paramInfo1)) {
          configLabels.addLabel(IUIConstants.STYLE_DIFF);
        }
        break;
      case IUIConstants.FUNCTION_VERS:
        if (compareRowObject.isFuncVerDiff(paramInfo, paramInfo1)) {
          configLabels.addLabel(IUIConstants.STYLE_DIFF);
        }
        break;
      case IUIConstants.BC:
        if (compareRowObject.isBCDiff(paramInfo, paramInfo1)) {
          configLabels.addLabel(IUIConstants.STYLE_DIFF);
        }
        break;
      case IUIConstants.WORK_PACKAGE:

        if (compareRowObject.isWpDiff(a2lParamColDataMapper.getColumnIndexA2lWpDefVersIdMap().get(columnIndex),
            a2lParamColDataMapper.getColumnIndexA2lWpDefVersIdMap().get(REF_COL_NUM), a2lVarGrp1, a2lVarGrp2)) {
          configLabels.addLabel(IUIConstants.STYLE_DIFF);
        }
        break;
      case IUIConstants.RESPONSIBILTY:
        if (compareRowObject.isRespDiff(a2lParamColDataMapper.getColumnIndexA2lWpDefVersIdMap().get(columnIndex),
            a2lParamColDataMapper.getColumnIndexA2lWpDefVersIdMap().get(REF_COL_NUM), a2lVarGrp1, a2lVarGrp2)) {
          configLabels.addLabel(IUIConstants.STYLE_DIFF);
        }
        break;
      case IUIConstants.NAME_AT_CUSTOMER:
        if (compareRowObject.isNameAtCustDiff(paramInfo, paramInfo1, a2lWpInfoBo1, a2lWpInfoBo2)) {
          configLabels.addLabel(IUIConstants.STYLE_DIFF);
        }
        break;
      default:
        // do nothing
    }
  }


  /**
   * @param paramInfo
   * @param a2lWpInfoBo
   * @return
   */
  private boolean paramNotMappedToSelectedA2lVarGrp(final A2LWpParamInfo paramInfo, final A2LWPInfoBO a2lWpInfoBo) {
    return (null != a2lWpInfoBo.getSelectedA2lVarGroup()) && !a2lWpInfoBo.isParamMappedToSelectedVarGrp(paramInfo);
  }


  private boolean isNotAssignedVarGrp(final A2LWpParamInfo a2lWpParamInfo, final A2LWPInfoBO a2lWpInfoBo) {
    A2lWpResponsibility a2lWpResponsibility = a2lWpInfoBo.getWpRespPalForA2lWpMapping(a2lWpParamInfo);
    return (a2lWpResponsibility != null) && (a2lWpResponsibility.getVariantGrpId() == null) &&
        (a2lWpInfoBo.getSelectedA2lVarGroup() == null);
  }
}