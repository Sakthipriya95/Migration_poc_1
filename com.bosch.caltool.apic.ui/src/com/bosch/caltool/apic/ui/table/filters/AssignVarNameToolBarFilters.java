/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridItem;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author dmo5cob
 */

public class AssignVarNameToolBarFilters extends AbstractViewerFilter {


  private boolean notInFile = true;/* By default this flag will switched on */
  private boolean inFile = true;/* By default this flag will switched on */
  private GridTableViewer tableViewer;
  private final PidcVersionBO pidcVersionBO;

  /**
   * @param pidcVersionBO
   */
  public AssignVarNameToolBarFilters(final PidcVersionBO pidcVersionBO) {
    super();
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);
    this.pidcVersionBO = pidcVersionBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFilterText(final String filterText) {

    setFilterText(filterText, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {

    final PidcVariant var = (PidcVariant) element;
    return filterValues(var);
  }


  /**
   * @param variant
   */
  private boolean filterValues(final PidcVariant variant) {


    boolean flag = false;
    if (null != this.tableViewer) {
      GridItem[] itemsFromFile = this.tableViewer.getGrid().getItems();

      try {
        String varCustAttrIdStr = new CommonDataBO().getParameterValue(CommonParamKey.VARIANT_IN_CUST_CDMS_ATTR_ID);
        PidcVariantAttribute varCustCDMAttr = this.pidcVersionBO.getPidcDataHandler().getVariantAttributeMap()
            .get(variant.getId()).get(Long.parseLong(varCustAttrIdStr));
        PidcVariantBO varHandler =
            new PidcVariantBO(this.pidcVersionBO.getPidcVersion(), variant, this.pidcVersionBO.getPidcDataHandler());
        PidcVariantAttributeBO varAttrHandler = new PidcVariantAttributeBO(varCustCDMAttr, varHandler);
        String varNameInCust = varAttrHandler.getDefaultValueDisplayName();
        for (GridItem item : itemsFromFile) {
          if (item.getData().equals(varNameInCust)) {
            flag = true;
            break;
          }
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }

    if (!isNotInFile() && flag) {
      return false;
    }
    if (!isInFile() && !flag) {
      return false;
    }
    return true;
  }


  /**
   * @param attrValNotClr attrValNotClr
   */
  public void setNotInFileSel(final boolean attrValNotClr) {
    this.notInFile = attrValNotClr;

  }

  /**
   * @param inFile boolean
   */
  public void setInFileSel(final boolean inFile) {
    this.inFile = inFile;

  }


  /**
   * @return the notInFile
   */
  public boolean isNotInFile() {
    return this.notInFile;
  }


  /**
   * @param notInFile the notInFile to set
   */
  public void setNotInFile(final boolean notInFile) {
    this.notInFile = notInFile;
  }


  /**
   * @return the inFile
   */
  public boolean isInFile() {
    return this.inFile;
  }


  /**
   * @param inFile the inFile to set
   */
  public void setInFile(final boolean inFile) {
    this.inFile = inFile;
  }


  /**
   * @param tableViewer the tableViewer to set
   */
  public void setTableViewer(final GridTableViewer tableViewer) {
    this.tableViewer = tableViewer;
  }


}
