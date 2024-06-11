/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridItem;

import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.report.Activator;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author dmo5cob
 */
public class AssignVariantNamesTabSorter extends AbstractViewerSorter {

  /**
   * index - Column number
   */
  private int index;
  /**
   * constant for descending sort
   */
  private static final int DESCENDING = 1;
  /**
   * constant for ascending sort
   */
  private static final int ASCENDING = 0;
  // Default is ascending sort dirextion
  private int direction = ASCENDING;

  private GridTableViewer tableViewer;

  private final PidcVersionBO pidcVersionBO;


  /**
   * @param pidcVersionBO
   */
  public AssignVariantNamesTabSorter(final PidcVersionBO pidcVersionBO) {
    this.pidcVersionBO = pidcVersionBO;
  }

  /**
   * @param tableViewer the tableViewer to set
   */
  public void setTableViewer(final GridTableViewer tableViewer) {
    this.tableViewer = tableViewer;
  }

  /**
   * {@inheritDoc} set the direction
   */
  @Override
  public void setColumn(final int index) {
    if (index == this.index) {
      this.direction = 1 - this.direction;
    }
    else {
      this.index = index;
      this.direction = ASCENDING;
    }
  }

  /**
   * Compare method for comparing the objects (PIDCVariant) equality. {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    int compare = 0;
    if ((obj1 instanceof PidcVariant) && (obj2 instanceof PidcVariant)) {
      PidcVariant var1 = (PidcVariant) obj1;
      PidcVariant var2 = (PidcVariant) obj2;
      String varCustAttrIdStr;
      try {
        varCustAttrIdStr = new CommonDataBO().getParameterValue(CommonParamKey.VARIANT_IN_CUST_CDMS_ATTR_ID);
        switch (this.index) {
          case 0:

            compare = compareCustCDMAtt(var1, var2, varCustAttrIdStr);
            break;
          case 1:
            compare = compVarCustCDMAttr(compare, var1, var2, varCustAttrIdStr);
            break;
          case 2:
            compare = CommonUtils.compareToIgnoreCase(var1.getName(), var2.getName());
            break;
          case 3:
            compare = compVarCustCDMId(var1, var2, varCustAttrIdStr);
            break;
          default:
            compare = 0;
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    else if ((obj1 instanceof String) && (obj2 instanceof String)) {
      String var1 = (String) obj1;
      String var2 = (String) obj2;

      switch (this.index) {
        case 0:
          compare = CommonUtils.compareToIgnoreCase(var1, var2);
          break;
        case 1:
          break;
        default:
          compare = 0;
      }
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      compare = -compare;
    }

    return compare;
  }

  /**
   * @param var1
   * @param var2
   * @param varCustAttrIdStr
   * @return
   */
  private int compVarCustCDMId(final PidcVariant var1, final PidcVariant var2, final String varCustAttrIdStr) {
    int compare;
    boolean flag1 = false;
    boolean flag2 = false;

    PidcVariantBO variantHandler1 =
        new PidcVariantBO(this.pidcVersionBO.getPidcVersion(), var1, this.pidcVersionBO.getPidcDataHandler());
    PidcVariantAttribute varCustCDMAtt1 = variantHandler1.getAttributesAll().get(Long.valueOf(varCustAttrIdStr));
    if (null != varCustCDMAtt1) {
      if (varCustCDMAtt1.getValue() == null) {
        flag1 = false;
      }
      else {
        flag1 = true;
      }
    }
    PidcVariantBO variantHandler2 =
        new PidcVariantBO(this.pidcVersionBO.getPidcVersion(), var2, this.pidcVersionBO.getPidcDataHandler());
    PidcVariantAttribute varCustCDMAtt2 = variantHandler2.getAttributesAll().get(Long.valueOf(varCustAttrIdStr));
    if (null != varCustCDMAtt2) {
      if (varCustCDMAtt2.getValue() == null) {
        flag2 = false;
      }
      else {
        flag2 = true;
      }
    }
    compare = ApicUtil.compareBoolean(flag1, flag2);
    return compare;
  }

  /**
   * @param compare
   * @param var1
   * @param var2
   * @param varCustAttrIdStr
   * @return
   */
  private int compVarCustCDMAttr(int compare, final PidcVariant var1, final PidcVariant var2,
      final String varCustAttrIdStr) {
    if (null != this.tableViewer) {

      boolean flag1 = false;
      boolean flag2 = false;
      GridItem[] itemsFromFile = this.tableViewer.getGrid().getItems();

      PidcVariantBO variantHandler1 =
          new PidcVariantBO(this.pidcVersionBO.getPidcVersion(), var1, this.pidcVersionBO.getPidcDataHandler());
      PidcVariantAttribute varCustCDMAttr1 = variantHandler1.getAttributesAll().get(Long.valueOf(varCustAttrIdStr));
      if (null != varCustCDMAttr1) {
        String varNameInCust = varCustCDMAttr1.getValue();
        for (GridItem item : itemsFromFile) {
          if (item.getData().equals(varNameInCust)) {
            flag1 = true;
            break;
          }
        }
      }

      PidcVariantBO variantHandler2 =
          new PidcVariantBO(this.pidcVersionBO.getPidcVersion(), var2, this.pidcVersionBO.getPidcDataHandler());
      PidcVariantAttribute varCustCDMAttr2 = variantHandler2.getAttributesAll().get(Long.valueOf(varCustAttrIdStr));
      if (null != varCustCDMAttr2) {
        String varNameInCust2 = varCustCDMAttr2.getValue();
        for (GridItem item : itemsFromFile) {
          if (item.getData().equals(varNameInCust2)) {
            flag2 = true;
            break;
          }
        }
      }
      compare = ApicUtil.compareBoolean(flag1, flag2);
    }
    return compare;
  }

  /**
   * @param var1
   * @param var2
   * @param varCustAttrIdStr
   * @return
   */
  private int compareCustCDMAtt(final PidcVariant var1, final PidcVariant var2, final String varCustAttrIdStr) {
    int compare;
    PidcVariantBO variantHandler1 =
        new PidcVariantBO(this.pidcVersionBO.getPidcVersion(), var1, this.pidcVersionBO.getPidcDataHandler());
    PidcVariantAttribute varCustCDMAttr1 = variantHandler1.getAttributesAll().get(Long.valueOf(varCustAttrIdStr));
    PidcVariantBO variantHandler2 =
        new PidcVariantBO(this.pidcVersionBO.getPidcVersion(), var2, this.pidcVersionBO.getPidcDataHandler());
    PidcVariantAttribute varCustCDMAttr2 = variantHandler2.getAttributesAll().get(Long.valueOf(varCustAttrIdStr));
    compare = CommonUtils.compareToIgnoreCase(null == varCustCDMAttr1 ? null : varCustCDMAttr1.getValue(),
        null == varCustCDMAttr2 ? null : varCustCDMAttr2.getValue());
    return compare;
  }

  /**
   * return the direction. {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }
}
