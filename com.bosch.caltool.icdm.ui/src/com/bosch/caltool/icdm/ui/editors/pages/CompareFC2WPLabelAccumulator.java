/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPMappingResult;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.ui.editors.CompareFC2WPRowObject;
import com.bosch.caltool.icdm.ui.util.IUIConstants;

/**
 * @author bru2cob
 */
public class CompareFC2WPLabelAccumulator extends ColumnOverrideLabelAccumulator {


  /**
   * Column Number where static column ends
   */
  private static final int REF_COL_NUM = 2;
  /**
   * Column number where next version's column starts
   */
  private static final int COMPARE_START_COL_NO = 20;
  /**
   * Instance variable which holds a reference to bodyDataLayer
   */
  private final ILayer layer;
  /**
   * Instance variable which holds a reference to bodyDataProvider
   */
  private final IRowDataProvider<CompareFC2WPRowObject> bodyDataProvider;

  /**
   * @param bodyDataLayer
   * @param bodyDataProvider2
   */
  public CompareFC2WPLabelAccumulator(final DataLayer bodyDataLayer,
      final IRowDataProvider<CompareFC2WPRowObject> bodyDataProvider2) {
    super(bodyDataLayer);
    this.layer = bodyDataLayer;
    this.bodyDataProvider = bodyDataProvider2;
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
    if (columnIndex > COMPARE_START_COL_NO) {
      // get the row object out of the dataprovider
      CompareFC2WPRowObject compareRowObject = this.bodyDataProvider.getRowObject(rowPosition);
      FC2WPMapping fc2wpMapping = compareRowObject.getColumnDataMapper().getColumnIndexFC2WPMap().get(columnIndex);

      FC2WPMappingResult fc2wpMappingResult =
          compareRowObject.getColumnDataMapper().getColumnIndexFC2WPMapResult().get(columnIndex);

      FC2WPMapping refFc2wpMapping = compareRowObject.getColumnDataMapper().getColumnIndexFC2WPMap().get(2);
      FC2WPMappingResult refFc2wpMappingResult =
          compareRowObject.getColumnDataMapper().getColumnIndexFC2WPMapResult().get(REF_COL_NUM);
      String key = null;
      if (fc2wpMapping != null) {
        key = fc2wpMapping.getFunctionName();
      }
      if (refFc2wpMapping != null) {
        key = refFc2wpMapping.getFunctionName();
      }
      String colName = compareRowObject.getColumnDataMapper().getColumnIndexFlagMap().get(columnIndex);
      if (null != key) {
        addColLabel(configLabels, fc2wpMapping, fc2wpMappingResult, refFc2wpMapping, refFc2wpMappingResult, key,
            colName, compareRowObject);
      }
    }

  }

  /**
   * @param configLabels
   * @param fc2wpMapping
   * @param fc2wpMappingResult
   * @param refFc2wpMapping
   * @param refFc2wpMappingResult
   * @param key
   * @param colName
   * @param compareRowObject
   */
  private void addColLabel(final LabelStack configLabels, final FC2WPMapping fc2wpMapping,
      final FC2WPMappingResult fc2wpMappingResult, final FC2WPMapping refFc2wpMapping,
      final FC2WPMappingResult refFc2wpMappingResult, final String key, final String colName,
      final CompareFC2WPRowObject compareRowObject) {
    switch (colName) {
      case IUIConstants.WORK_PACKAGE:
        if (compareRowObject.isWpNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
          configLabels.addLabel(IUIConstants.WORKPACKAGE_DIFF_LABEL);
        }
        break;
      case IUIConstants.RESOURCE:
        if (compareRowObject.isResourceNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
          configLabels.addLabel(IUIConstants.RESOURCE_DIFF_LABEL);
        }
        break;
      case IUIConstants.WP_ID_MCR:
        if (compareRowObject.isWpIdNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
          configLabels.addLabel(IUIConstants.WPID_DIFF_LABEL);
        }
        break;
      case IUIConstants.BC:
        if (compareRowObject.isBcNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
          configLabels.addLabel(IUIConstants.BC_DIFF_LABEL);
        }
        break;
      case IUIConstants.PT_TYPE:
        if (compareRowObject.isPTTypeNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
          configLabels.addLabel(IUIConstants.PT_TYPE_DIFF_LABEL);
        }
        break;
      case IUIConstants.FIRST_CONTACT:
        if (compareRowObject.isFcNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
          configLabels.addLabel(IUIConstants.FIRST_CONTACT_DIFF_LABEL);
        }
        break;
      case IUIConstants.SECOND_CONTACT:
        if (compareRowObject.isSCNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
          configLabels.addLabel(IUIConstants.SECOND_CONTACT_DIFF_LABEL);
        }
        break;
      case IUIConstants.IS_AGREED:
        if (compareRowObject.isAgreedNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
          configLabels.addLabel(IUIConstants.IS_AGREED_DIFF_LABEL);
        }
        break;
      case IUIConstants.AGREED_ON:
        if (compareRowObject.isAgreedOnNotEqual(fc2wpMapping, refFc2wpMapping)) {
          configLabels.addLabel(IUIConstants.AGREED_ON_DIFF_LABEL);
        }
        break;
      case IUIConstants.RESP_FOR_AGREEMENT:
        if (compareRowObject.isRespNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
          configLabels.addLabel(IUIConstants.RESP_AGREE_DIFF_LABEL);
        }
        break;
      case IUIConstants.COMMENT:
        if (compareRowObject.isCommentNotEqual(fc2wpMapping, refFc2wpMapping)) {
          configLabels.addLabel(IUIConstants.COMMENT_DIFF_LABEL);
        }
        break;
      case IUIConstants.FC2WP_INFO:
        if (compareRowObject.isFc2WpInfoNotEqual(fc2wpMapping, refFc2wpMapping)) {
          configLabels.addLabel(IUIConstants.FC2WP_INFO_DIFF_LABEL);
        }
        break;
      case IUIConstants.IS_IN_ICDM:
        if (compareRowObject.isInIcdmNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
          configLabels.addLabel(IUIConstants.IS_IN_ICDM_DIFF_LABEL);
        }
        break;
      case IUIConstants.IS_FC_IN_SDOM:
        if (compareRowObject.isFcInSdomNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
          configLabels.addLabel(IUIConstants.IS_FC_IN_SDOM_DIFF_LABEL);
        }
        break;
      case IUIConstants.FC_WITH_PARAMS:
        if (compareRowObject.isFcWithParamsNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
          configLabels.addLabel(IUIConstants.IS_FC_WITH_PARAMS_DIFF_LABEL);
        }
        break;
      case IUIConstants.DELETED:
        if (compareRowObject.isDeletedNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
          configLabels.addLabel(IUIConstants.DELETED_DIFF_LABEL);
        }
        break;
      default:
        // do nothing

    }
  }


}
