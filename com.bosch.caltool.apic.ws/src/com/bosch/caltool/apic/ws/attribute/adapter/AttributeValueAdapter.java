/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.attribute.adapter;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.AttributeValue;
import com.bosch.caltool.apic.ws.db.IWebServiceAdapter;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;


/**
 * @author imi2si
 */
public class AttributeValueAdapter extends AttributeValue implements IWebServiceAdapter {

  private com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue;
  //Object for logger
  private final transient ILoggerAdapter log;
  
  //Constructor for the class with logger as an input parameter
  /**
   * @param logger - to log the method flow
   */
  public AttributeValueAdapter(final ILoggerAdapter logger) {
    this.log = logger;
    this.log.debug("AttributeValueAdapter created. No DB-AttributeValue passed in constructor.");
  }

  //Constructor for the class with logger  and attribute value as an input parameter
  /**
   * @param logger - to log the method flow
   * @param attributeValue - contains the values of attributes
   * @throws IcdmException
   */
  public AttributeValueAdapter(final ILoggerAdapter logger,
      final com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue) throws IcdmException {
    this(logger);
    this.log
        .debug("AttributeValueAdapter created. DB-AttributeValue passed in constructor. Adaption happens immediately.");
    this.attributeValue = attributeValue;
    adapt();
  }

  /**
   * {@inheritDoc}
   *
   * @throws IcdmException
   */
  
  @Override
  public void adapt() throws IcdmException {
 // this method is to adapt DB-Attribute to WS-Output
    this.log.debug("Start adapting DB-AttributeValue to WS-Output for Attribute: " + this.attributeValue.getId() + "/" +
        this.attributeValue.getName());
    // setters for setting the attribute object
    super.setChangeNumber(this.attributeValue.getVersion());
    super.setClearingStatus(this.attributeValue.getClearingStatus());
    // conversion of createdDate to yyyy-MM-dd HH:mm:ss SSS format
    super.setCreateDate(
        DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, this.attributeValue.getCreatedDate()));
    String clearingStatus = this.attributeValue.getClearingStatus();
    CLEARING_STATUS clStatus = CLEARING_STATUS.getClearingStatus(clearingStatus);
    super.setIsCleared(clStatus.equals(CLEARING_STATUS.CLEARED));
    super.setIsDeleted(this.attributeValue.isDeleted());
    // conversion of createdDate to yyyy-MM-dd HH:mm:ss SSS format
    super.setModifyDate(
        DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, this.attributeValue.getModifiedDate()));
    super.setModifyUser(this.attributeValue.getModifiedUser());
    super.setValueDescE(this.attributeValue.getDescriptionEng());
    super.setValueDescG(this.attributeValue.getDescriptionGer());
    super.setValueE(this.attributeValue.getNameRaw());
    super.setValueG(this.attributeValue.getNameRaw());
    super.setValueID(this.attributeValue.getId());
    if (AttributeValueType.ICDM_USER.getDisplayText().equals(this.attributeValue.getValueType())) {
      super.setValueE(this.attributeValue.getOtherValue() == null ? "" : this.attributeValue.getOtherValue());
      super.setValueDescE(this.attributeValue.getTextValueEng());
    }
 // print the attribute object in logs
    this.log.debug("End adapting DB-AttributeValue. Result: " + toString());
  }

  /**
   * {@inheritDoc}
   */
  
  @Override
  public String toString() {
 // this method is to display the object in string format
    return "AttributeValueAdapter [attributeValue=" + this.attributeValue + ", localValueID=" + this.localValueID +
        ", localAttrID=" + this.localAttrID + ", localValueE=" + this.localValueE + ", localValueG=" +
        this.localValueG + ", localValueGTracker=" + this.localValueGTracker + ", localIsDeleted=" +
        this.localIsDeleted + ", localCreateDate=" + this.localCreateDate + ", localCreateUser=" +
        this.localCreateUser + ", localModifyDate=" + this.localModifyDate + ", localModifyDateTracker=" +
        this.localModifyDateTracker + ", localModifyUser=" + this.localModifyUser + ", localModifyUserTracker=" +
        this.localModifyUserTracker + ", localChangeNumber=" + this.localChangeNumber + ", localClearingStatus=" +
        this.localClearingStatus + ", localIsCleared=" + this.localIsCleared + ", localValueDescE=" +
        this.localValueDescE + ", localValueDescETracker=" + this.localValueDescETracker + ", localValueDescG=" +
        this.localValueDescG + ", localValueDescGTracker=" + this.localValueDescGTracker + "]";
  }

  /**
   * @param attributeValue the attributeValue to set
   */
  public void setAttributeValue(final com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue) {
    this.attributeValue = attributeValue;
  }
}
