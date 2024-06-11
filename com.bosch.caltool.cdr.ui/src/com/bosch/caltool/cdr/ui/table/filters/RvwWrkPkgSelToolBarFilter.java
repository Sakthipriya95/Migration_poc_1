/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;

/**
 * @author hnu1cob
 */
public class RvwWrkPkgSelToolBarFilter extends AbstractViewerFilter {

  /**
   *
   */
  public RvwWrkPkgSelToolBarFilter() {
    super();
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);
  }

  /* Robert Bosch Resp type */
  boolean boschRespType = true;
  /* Customer Resp type */
  boolean custRespType = true;
  /* Others Resp type */
  boolean othersRespType = true;

  /**
   * @return the boschRespType
   */
  public boolean isBoschRespType() {
    return this.boschRespType;
  }


  /**
   * @param boschRespType the boschRespType to set
   */
  public void setBoschRespType(final boolean boschRespType) {
    this.boschRespType = boschRespType;
  }


  /**
   * @return the custRespType
   */
  public boolean isCustRespType() {
    return this.custRespType;
  }


  /**
   * @param custRespType the custRespType to set
   */
  public void setCustRespType(final boolean custRespType) {
    this.custRespType = custRespType;
  }


  /**
   * @return the othersRespType
   */
  public boolean isOthersRespType() {
    return this.othersRespType;
  }


  /**
   * @param othersRespType the othersRespType to set
   */
  public void setOthersRespType(final boolean othersRespType) {
    this.othersRespType = othersRespType;
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
    if (element instanceof WpRespModel) {
      final WpRespModel wpRespModel = (WpRespModel) element;
      if (filterBoschResp(getRespTypeName(wpRespModel))) {
        return true;
      }
      if (filterCustResp(getRespTypeName(wpRespModel))) {
        return true;
      }
      if (filterOthersResp(getRespTypeName(wpRespModel))) {
        return true;
      }
    }
    return false;

  }

  /**
   * @param respTypeName
   * @return true, if others type else false
   */
  private boolean filterOthersResp(final String respTypeName) {
    if (isOthersRespType()) {
      return CommonUtils.isEqual(respTypeName, "Others");
    }
    return false;
  }

  /**
   * @param respTypeName @returntrue, if customer type else false
   */
  private boolean filterCustResp(final String respTypeName) {
    if (isCustRespType()) {
      return CommonUtils.isEqual(respTypeName, "Customer");
    }
    return false;
  }

  /**
   * @param respTypeName
   * @return true, if bosch type else false
   */
  private boolean filterBoschResp(final String respTypeName) {
    if (isBoschRespType()) {
      return CommonUtils.isEqual(respTypeName, "Robert Bosch");
    }
    return false;
  }


  /**
   * @param wpRespModel WpRespModel
   * @return resptype_disp_name
   */
  public String getRespTypeName(final WpRespModel wpRespModel) {
    String respType = A2lResponsibilityCommon.getRespType(wpRespModel.getA2lResponsibility()).getDispName();
    return respType != null ? respType : "";
  }

}
