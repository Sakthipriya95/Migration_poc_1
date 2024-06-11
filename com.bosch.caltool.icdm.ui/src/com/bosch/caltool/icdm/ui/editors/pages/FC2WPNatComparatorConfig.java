/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.util.Comparator;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;

import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPMappingResult;
import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPMappingResult.SortColumns;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.ui.editors.CompareFC2WPRowObject;
import com.bosch.caltool.icdm.ui.util.IUIConstants;

/**
 * @author BNE4COB
 */
class FC2WPNatComparatorConfig extends AbstractRegistryConfiguration {

  /**
   * CUSTOM_COMPARATOR Label
   */
  private static final String FC2WP_NAT_COMPARATOR_LABEL = "customComparatorLabel";

  private final AbstractLayer columnHeaderDataLayer;

  /**
   * @param columnHeaderDataLayer column Header Data Layer
   */
  FC2WPNatComparatorConfig(final AbstractLayer columnHeaderDataLayer) {
    this.columnHeaderDataLayer = columnHeaderDataLayer;
  }

  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {

    // Add label accumulator
    ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(this.columnHeaderDataLayer);
    this.columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);

    // Register labels
    for (int colIndx = 0; colIndx <= FC2WPNatFormPage.MODIFIED_DATE_COL_NUMBER; colIndx++) {
      labelAccumulator.registerColumnOverrides(colIndx, FC2WP_NAT_COMPARATOR_LABEL + colIndx);
    }

    // Register column attributes
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.FC_NAME_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.FC_LONG_NAME_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.WP_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.RESOURCE_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.WPID_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.BC_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.PTTYPE_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.CONTACT1_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.CONTACT2_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.IS_COCAGREED_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.COC_AGREEDON_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.COC_RESP_AGREED_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.COMMENTS_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.FC2WP_INFO_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.IS_INICDMA2L_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.IS_FC_IN_SDOM_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.IS_FC_WITH_PARAMS_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.ISDELETED_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.CREATED_DATE_COL_NUMBER);
    registerConfigAttribute(configRegistry, FC2WPNatFormPage.MODIFIED_DATE_COL_NUMBER);
  }


  private void registerConfigAttribute(final IConfigRegistry configRegistry, final int columnIndex) {
    configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getColumnComparator(columnIndex),
        NORMAL, FC2WP_NAT_COMPARATOR_LABEL + columnIndex);
  }

  /**
   * Gets the name comparator.
   *
   * @param columnNum the sort columns
   * @return the name comparator
   */
  static Comparator<CompareFC2WPRowObject> getColumnComparator(final int columnNum) {

    return (cmpRowObj1, cmpRowObj2) -> {
      int ret;
      switch (columnNum) {
        case 0:
          ret = ApicUtil.compare(cmpRowObj1.getFuncName(), cmpRowObj2.getFuncName());
          break;
        case 1:
          ret = ApicUtil.compare(cmpRowObj1.getFuncDesc(), cmpRowObj2.getFuncDesc());
          break;
        default:
          ret = compareFC2WP(cmpRowObj1, cmpRowObj2, columnNum);
      }
      return ret;
    };
  }


  private static int compareFC2WP(final CompareFC2WPRowObject cmpRowObj1, final CompareFC2WPRowObject cmpRowObj2,
      final int col) {
    FC2WPMapping fc2wpMapping1 = cmpRowObj1.getColumnDataMapper().getColumnIndexFC2WPMap().get(col);
    FC2WPMapping fc2wpMapping2 = cmpRowObj2.getColumnDataMapper().getColumnIndexFC2WPMap().get(col);
    String columnHeader = cmpRowObj1.getColumnDataMapper().getColumnIndexFlagMap().get(col);
    FC2WPMappingResult fc2wpMappingResult = cmpRowObj1.getColumnDataMapper().getColumnIndexFC2WPMapResult().get(col);
    int returnObj = 0;
    if (columnHeader != null) {
      switch (columnHeader) {

        case IUIConstants.WORK_PACKAGE:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_WP);
          break;
        case IUIConstants.RESOURCE:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_RESOURCE);
          break;
        case IUIConstants.WP_ID_MCR:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_WP_ID);
          break;
        case IUIConstants.BC:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_BC);
          break;
        case IUIConstants.PT_TYPE:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_PT_TYPE);
          break;
        case IUIConstants.FIRST_CONTACT:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_CONTACT_1);
          break;
        case IUIConstants.SECOND_CONTACT:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_CONTACT_2);
          break;
        case IUIConstants.IS_AGREED:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_IS_COC_AGREED);
          break;
        case IUIConstants.AGREED_ON:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_AGREED_ON);
          break;
        case IUIConstants.RESP_FOR_AGREEMENT:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_RESP_AGREEMNT);
          break;
        case IUIConstants.COMMENT:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_COMMENTS);
          break;
        case IUIConstants.FC2WP_INFO:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_FC2WP_INFO);
          break;
        case IUIConstants.IS_IN_ICDM:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_ISINICDM_A2L);
          break;
        case IUIConstants.IS_FC_IN_SDOM:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_IS_FC_IN_SDOM);
          break;
        case IUIConstants.FC_WITH_PARAMS:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_FC_WITH_PARAMS);
          break;
        case IUIConstants.CREATED_DATE:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_CREATED_DATE);
          break;
        case IUIConstants.MODIFIED_DATE:
          returnObj = fc2wpMappingResult.compareTo(fc2wpMapping1, fc2wpMapping2, SortColumns.SORT_MODIFIED_DATE);
          break;
        default:
          returnObj = getDefaultValue(cmpRowObj1, cmpRowObj2, col);
      }
    }
    return returnObj;
  }

  /**
   * @param cmpRowObj1
   * @param cmpRowObj2
   * @param col
   * @param returnValue
   * @return
   */
  private static int getDefaultValue(final CompareFC2WPRowObject cmpRowObj1, final CompareFC2WPRowObject cmpRowObj2,
      final int col) {
    int returnValue = 0;
    Object columnValue1 = cmpRowObj1.getColumnDataMapper().getColumnData(col);
    Object columnValue2 = cmpRowObj2.getColumnDataMapper().getColumnData(col);
    columnValue1 = columnValue1 == null ? "" : columnValue1;
    columnValue2 = columnValue2 == null ? "" : columnValue2;
    if ((columnValue1 instanceof String) && (columnValue2 instanceof String)) {
      returnValue = ((String) columnValue1).compareTo((String) columnValue2);
    }
    return returnValue;
  }

}
