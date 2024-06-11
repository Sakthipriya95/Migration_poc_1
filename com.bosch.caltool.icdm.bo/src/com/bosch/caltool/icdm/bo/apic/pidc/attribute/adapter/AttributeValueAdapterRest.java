/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.attribute.adapter;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.pidc.AttributeValue;


/**
 * @author imi2si
 */
public class AttributeValueAdapterRest extends AttributeValue implements IWebServiceAdapter {

  private com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue;
  private final ILoggerAdapter log;

  public AttributeValueAdapterRest(final ILoggerAdapter logger) {
    this.log = logger;
    this.log.debug("AttributeValueAdapter created. No DB-AttributeValue passed in constructor.");
  }

  public AttributeValueAdapterRest(final ILoggerAdapter logger,
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
    this.log.debug("Start adapting DB-AttributeValue to WS-Output for Attribute: " + this.attributeValue.getId() + "/" +
        this.attributeValue.getName());

    super.setChangeNumber(this.attributeValue.getVersion());
    super.setClearingStatus(this.attributeValue.getClearingStatus());
    super.setCreatedDate(this.attributeValue.getCreatedDate());
    String clearingStatus = this.attributeValue.getClearingStatus();
    CLEARING_STATUS clStatus = CLEARING_STATUS.getClearingStatus(clearingStatus);
    super.setCleared(clStatus.equals(CLEARING_STATUS.CLEARED));
    super.setDeleted(this.attributeValue.isDeleted());
    super.setModifyDate(this.attributeValue.getModifiedDate());
    super.setModifyUser(this.attributeValue.getModifiedUser());
    super.setValueDescEng(this.attributeValue.getDescriptionEng());
    super.setValueDescGer(this.attributeValue.getDescriptionGer());
    super.setValueEng(this.attributeValue.getNameRaw());
    super.setValueGer(this.attributeValue.getNameRaw());
    super.setValueId(this.attributeValue.getId());

    this.log.debug("End adapting DB-AttributeValue. Result: " + toString());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "AttributeValueAdapter [attributeValue=" + this.attributeValue + ", localValueID=" + getValueId() +
        ", localAttrID=" + getAttrId() + ", localValueE=" + getValueEng() + ", localValueG=" + getValueGer() +
        ", localValueGTracker=" + (null != getValueGer()) + ", localIsDeleted=" + isDeleted() + ", localCreateDate=" +
        getCreatedDate() + ", localCreateUser=" + getCreatedUser() + ", localModifyDate=" + getModifyDate() +
        ", localModifyDateTracker=" + (null != getModifyDate()) + ", localModifyUser=" + getModifyUser() +
        ", localModifyUserTracker=" + (null != getModifyUser()) + ", localChangeNumber=" + getChangeNumber() +
        ", localClearingStatus=" + getClearingStatus() + ", localIsCleared=" + isCleared() + ", localValueDescE=" +
        getValueDescEng() + ", localValueDescETracker=" + (null != getValueDescEng()) + ", localValueDescG=" +
        getValueDescGer() + ", localValueDescGTracker=" + (null != getValueDescGer()) + "]";
  }

  /**
   * @param attributeValue the attributeValue to set
   */
  public void setAttributeValue(final com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue) {
    this.attributeValue = attributeValue;
  }
}
