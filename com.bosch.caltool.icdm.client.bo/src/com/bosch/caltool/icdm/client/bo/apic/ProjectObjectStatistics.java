/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bne4cob
 * @param <P> AbstractProjectObject
 */
public class ProjectObjectStatistics<P extends IProjectObject> {


  /**
   * project handler
   */
  private final AbstractProjectObjectBO projObjBO;

  /**
   * Project object
   */
  private final P projObj;

  /**
   * @param projObj project object
   */
  ProjectObjectStatistics(final P projObj, final AbstractProjectObjectBO projObjBO) {
    this.projObj = projObj;
    this.projObjBO = projObjBO;
  }

  /**
   * @return total number of attributes
   */
  public int getAllAttributesCount() {
    if ((this.projObjBO instanceof PidcVariantBO)) {
      return (int) this.projObjBO.getAttributesAll().keySet().stream()
          .filter(attrId -> !((PidcVariantBO) this.projObjBO).getInvisiblePIDCVarAttrSet().contains(attrId)).count();
    }
    else if (this.projObjBO instanceof PidcSubVariantBO) {

      return (int) this.projObjBO.getAttributesAll().keySet().stream()
          .filter(attrId -> !((PidcSubVariantBO) this.projObjBO).getInvisiblePIDCSubVarAttrSet().contains(attrId))
          .count();

    }
    return this.projObjBO.getPidcDataHandler().getPidcVersAttrMapDefined().size();
  }

  /**
   * @return number of attributs marked as 'used'
   */
  public int getUsedAttributesCount() {

    int count = 0;
    // ignore invisible attributes
    Set<Long> invisSet = getInvisibleAttrs();
    for (Long attrId : this.projObjBO.getAttributesUsed().keySet()) {
      if (invisSet.contains(attrId)) {
        continue;
      }
      count++;
    }
    return count;
  }

  /**
   * @return number of attributs marked as 'not used'
   */
  public int getNotUsedAttributesCount() {
    int count = 0;
    // ignore invisible attributes
    Set<Long> invisSet = getInvisibleAttrs();
    for (Long attrId : this.projObjBO.getAttributesNotUsed().keySet()) {
      if (invisSet.contains(attrId)) {
        continue;
      }
      count++;
    }
    return count;
  }

  /**
   * @return number of attributs marked as 'not defined'
   */
  public int getNotDefinedAttributesCount() {

    int count = 0;
    // ignore invisible attributes

    Set<Long> invisSet = getInvisibleAttrs();
    for (Long attrId : this.projObjBO.getAttributesNotDefined().keySet()) {
      if (invisSet.contains(attrId)) {
        continue;
      }
      count++;
    }
    return count;
  }


  private Set<Long> getInvisibleAttrs() {
    Set<Long> invisSet = new HashSet<>();
    if (this.projObjBO instanceof PidcVariantBO) {
      invisSet = this.projObjBO.getPidcDataHandler().getVariantInvisbleAttributeMap()
          .get(this.projObjBO.getProjectObject().getId());
    }
    else if (this.projObjBO instanceof PidcSubVariantBO) {
      invisSet = this.projObjBO.getPidcDataHandler().getSubVariantInvisbleAttributeMap()
          .get(this.projObjBO.getProjectObject().getId());
    }
    else if (this.projObjBO instanceof PidcVersionBO) {
      invisSet = this.projObjBO.getPidcDataHandler().getPidcVersInvisibleAttrSet();
    }
    return invisSet;
  }

  /**
   * @return string representation of last modified date
   * @throws ParseException
   */
  public String getLastModifiedDateStr() throws ParseException {
    String modifiedDate = "";
    if (this.projObj.getModifiedDate() != null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_15);
      Date date = sdf.parse(this.projObj.getModifiedDate());
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      modifiedDate = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_09, cal);
    }
    return modifiedDate;
  }

  /**
   * @return Get the project statistics as string
   * @throws ParseException
   */
  public String getStatisticsAsString() {
    String stat = "";
    try {
      stat = new CommonDataBO().getMessage(ApicConstants.MSGGRP_PIDC_EDITOR, "PROJECT_STATISTICS_COMMON",
          getAllAttributesCount(), getUsedAttributesCount(), getNotUsedAttributesCount(),
          getNotDefinedAttributesCount(), getLastModifiedDateStr());
    }
    catch (ApicWebServiceException | ParseException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return stat;
  }

  /**
   * @return Project Object
   */
  public P getProjectObject() {
    return this.projObj;
  }


  /**
   * @return return true by default
   */
  public boolean validateStatistics() {
    return true;
  }


  /**
   * @return the project Object BO
   */
  public AbstractProjectObjectBO getProjectObjectBO() {
    return this.projObjBO;
  }


}
