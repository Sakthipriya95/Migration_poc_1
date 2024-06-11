/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.attribute.adapter;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.Attribute;


/**
 * @author imi2si
 */
public class AttributeAdapterRest extends Attribute implements IWebServiceAdapter {

  private com.bosch.caltool.icdm.model.apic.attr.Attribute attribute;

  private final ILoggerAdapter log;

  public AttributeAdapterRest(final ILoggerAdapter logger) {
    this.log = logger;
    this.log.debug("AttributeAdapter created. No DB-Attribute passed in constructor.");
  }

  public AttributeAdapterRest(final ILoggerAdapter logger,
      final com.bosch.caltool.icdm.model.apic.attr.Attribute attribute) throws IcdmException {
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
    this.log.debug("Start adapting DB-Attribute to WS-Output for Attribute: " + this.attribute.getId() + "/" +
        this.attribute.getName());

    super.setAttrLevel(this.attribute.getLevel().intValue());
    super.setChangeNumber(this.attribute.getVersion());
    super.setCreateDate(this.attribute.getCreatedDate());
    super.setCreateUser(this.attribute.getCreatedUser());
    super.setDescEng(null != this.attribute.getDescriptionEng() ? this.attribute.getDescriptionEng() : "");
    super.setDescGer(null != this.attribute.getDescriptionGer() ? this.attribute.getDescriptionGer() : "");
    super.setFormat(null != this.attribute.getFormat() ? this.attribute.getFormat() : "");
    super.setGroupId(this.attribute.getAttrGrpId());
    super.setId(this.attribute.getId());
    super.setDeleted(this.attribute.isDeleted());
    super.setMandatory(this.attribute.isMandatory());
    super.setNormalized(this.attribute.isNormalized());
    super.setModifyDate(this.attribute.getModifiedDate());
    super.setModifyUser(this.attribute.getModifiedUser());
    super.setNameEng(null != this.attribute.getNameEng() ? this.attribute.getNameEng() : "");
    super.setNameGer(null != this.attribute.getNameGer() ? this.attribute.getNameGer() : "");
    super.setTypeId(this.attribute.getValueTypeId());
    super.setUnit(this.attribute.getUnit());

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
  @Override
  public String toString() {
    return "AttributeAdapter [attribute=" + this.attribute + ", localId=" + getId() + ", localNameE=" + getNameEng() +
        ", localNameG=" + getNameGer() + ", localNameGTracker=" + (null != getNameGer()) + ", localDescrE=" +
        getDescEng() + ", localDescrG=" + getDescGer() + ", localDescrGTracker=" + (null != getDescGer()) +
        ", localUnit=" + getUnit() + ", localFormat=" + getFormat() + ", localFormatTracker=" + (null != getFormat()) +
        ", localIsNormalized=" + isNormalized() + ", localIsDeleted=" + isDeleted() + ", localIsMandatory=" +
        isMandatory() + ", localAttrLevel=" + getAttrLevel() + ", localAttrLevelTracker=" +
        (getAttrLevel() != java.lang.Integer.MIN_VALUE) + ", localGroupID=" + getGroupId() + ", localTypeID=" +
        getTypeId() + ", localCreateDate=" + getCreateDate() + ", localCreateUser=" + getCreateUser() +
        ", localModifyDate=" + getModifyDate() + ", localModifyDateTracker=" + (null != getModifyDate()) +
        ", localModifyUser=" + getModifyUser() + ", localModifyUserTracker=" + (null != getModifyUser()) +
        ", localChangeNumber=" + getChangeNumber() + "]";
  }
}
