/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author bne4cob
 * @param <P> AbstractProjectObject
 */
// ICDM-2247
public class ProjectObjectStatistics<P extends AbstractProjectObject<?>> {

  /**
   * Project object
   */
  private final P projObj;

  /**
   * @param projObj project object
   */
  ProjectObjectStatistics(final P projObj) {
    this.projObj = projObj;
  }

  /**
   * @return total number of attributes
   */
  public int getAllAttributesCount() {
    return getProjectObject().getAttributes().size();
  }

  /**
   * @return number of attributs marked as 'used'
   */
  public int getUsedAttributesCount() {
    return getProjectObject().getAttributesUsed().size();
  }

  /**
   * @return number of attributs marked as 'not used'
   */
  public int getNotUsedAttributesCount() {
    return getProjectObject().getAttributesNotUsed().size();
  }

  /**
   * @return number of attributs marked as 'not defined'
   */
  public int getNotDefinedAttributesCount() {
    return getProjectObject().getAttributesNotDefined().size();
  }

  /**
   * @return last modified of this pidc version
   */
  public Calendar getLastModifiedDate() {
    return this.projObj.getModifiedDate();
  }

  /**
   * @return string representation of last modified date
   */
  public String getLastModifiedDateStr() {
    String modifiedDate = "";
    if (getLastModifiedDate() != null) {
      modifiedDate = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_09, getLastModifiedDate());
    }
    return modifiedDate;
  }

  /**
   * @return Get the project statistics as string
   */
  public String getStatisticsAsString() {

    return getDataProvider().getMessage(ApicConstants.MSGGRP_PIDC_EDITOR, "PROJECT_STATISTICS_COMMON",
        getAllAttributesCount(), getUsedAttributesCount(), getNotUsedAttributesCount(), getNotDefinedAttributesCount(),
        getLastModifiedDateStr());

  }

  /**
   * @return Project Object
   */
  public P getProjectObject() {
    return this.projObj;
  }

  /**
   * @return Apic Data Provider
   */
  protected final ApicDataProvider getDataProvider() {
    return getProjectObject().getDataCache().getDataProvider();
  }

  /**
   * @return Logger
   */
  protected final ILoggerAdapter getLogger() {
    return getProjectObject().getDataCache().getDataProvider().getLogger();
  }

  /**
   * @return return true by default
   */
  public boolean validateStatistics() {
    return true;
  }
}
