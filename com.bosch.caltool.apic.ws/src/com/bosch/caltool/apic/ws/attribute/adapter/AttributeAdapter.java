/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.attribute.adapter;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.Attribute;
import com.bosch.caltool.apic.ws.db.IWebServiceAdapter;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.DateFormat;


/**
 * @author imi2si
 */
public class AttributeAdapter extends Attribute implements IWebServiceAdapter {

  private com.bosch.caltool.icdm.model.apic.attr.Attribute attribute;

  //  Object for logger 
  private final transient ILoggerAdapter log;

  // Constructor of the class with logger as input paramater
  /**
   * @param logger- to log the method flow
   */
  public AttributeAdapter(final ILoggerAdapter logger) {
    this.log = logger;
    this.log.debug("AttributeAdapter created. No DB-Attribute passed in constructor.");
  }

  //Constructor of the class with logger and attribute as input paramater
  /**
   * @param logger - to log the method flow
   * @param attribute - contains the values of attributes
   * @throws IcdmException
   */
  public AttributeAdapter(final ILoggerAdapter logger, final com.bosch.caltool.icdm.model.apic.attr.Attribute attribute)
      throws IcdmException {
    this(logger);
    this.log.debug("AttributeAdapter created. DB-Attribute passed in constructor. Adaption happens immediately.");
    this.attribute = attribute;
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
    this.log.debug("Start adapting DB-Attribute to WS-Output for Attribute: " + this.attribute.getId() + "/" +
        this.attribute.getName());
    
    // setters for setting the attribute object
    super.setAttrLevel(this.attribute.getLevel().intValue());
    super.setChangeNumber(this.attribute.getVersion());
    // conversion of createdDate to yyyy-MM-dd HH:mm:ss SSS format
    super.setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, this.attribute.getCreatedDate()));
    super.setCreateUser(this.attribute.getCreatedUser());
    super.setDescrE(null != this.attribute.getDescriptionEng() ? this.attribute.getDescriptionEng() : "");
    super.setDescrG(null != this.attribute.getDescriptionGer() ? this.attribute.getDescriptionGer() : "");
    super.setFormat(null != this.attribute.getFormat() ? this.attribute.getFormat() : "");
    super.setGroupID(this.attribute.getAttrGrpId());
    super.setId(this.attribute.getId());
    super.setIsDeleted(this.attribute.isDeleted());
    super.setIsMandatory(this.attribute.isMandatory());
    super.setIsNormalized(this.attribute.isNormalized());
    // conversion of createdDate to yyyy-MM-dd HH:mm:ss SSS format
    super.setModifyDate(
        DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, this.attribute.getModifiedDate()));
    super.setModifyUser(this.attribute.getModifiedUser());
    super.setNameE(null != this.attribute.getNameEng() ? this.attribute.getNameEng() : "");
    super.setNameG(null != this.attribute.getNameGer() ? this.attribute.getNameGer() : "");
    super.setTypeID(this.attribute.getValueTypeId());
    super.setUnit(this.attribute.getUnit());
    // print the attribute object in logs
    this.log.debug("End adapting DB-Attribute. Result: " + toString());
  }

  /**
   * @param attribute the attribute to set
   */
  public void setAttribute(final com.bosch.caltool.icdm.model.apic.attr.Attribute attribute) {
    this.attribute = attribute;
  }

  /**
   * {@inheritDoc}
   */
  // this method is to display the object in string format
  @Override
  public String toString() {
    return "AttributeAdapter [attribute=" + this.attribute + ", localId=" + this.localId + ", localNameE=" +
        this.localNameE + ", localNameG=" + this.localNameG + ", localNameGTracker=" + this.localNameGTracker +
        ", localDescrE=" + this.localDescrE + ", localDescrG=" + this.localDescrG + ", localDescrGTracker=" +
        this.localDescrGTracker + ", localUnit=" + this.localUnit + ", localFormat=" + this.localFormat +
        ", localFormatTracker=" + this.localFormatTracker + ", localIsNormalized=" + this.localIsNormalized +
        ", localIsDeleted=" + this.localIsDeleted + ", localIsMandatory=" + this.localIsMandatory +
        ", localAttrLevel=" + this.localAttrLevel + ", localAttrLevelTracker=" + this.localAttrLevelTracker +
        ", localGroupID=" + this.localGroupID + ", localTypeID=" + this.localTypeID + ", localCreateDate=" +
        this.localCreateDate + ", localCreateUser=" + this.localCreateUser + ", localModifyDate=" +
        this.localModifyDate + ", localModifyDateTracker=" + this.localModifyDateTracker + ", localModifyUser=" +
        this.localModifyUser + ", localModifyUserTracker=" + this.localModifyUserTracker + ", localChangeNumber=" +
        this.localChangeNumber + "]";
  }
}
