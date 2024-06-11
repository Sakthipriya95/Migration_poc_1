/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.pidc.adapter;


import com.bosch.caltool.apic.ws.LevelAttrInfo;
import com.bosch.caltool.apic.ws.db.IWebServiceAdapter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;


/**
 * @author imi2si
 */
public class LevelAttrAdapter extends LevelAttrInfo implements IWebServiceAdapter {

  private final Attribute attr;
  private final Long valueId;
  private final ServiceData serviceData;

  /**
   * @param attribute attribute
   * @param attrValuId attrValuId
   * @param serviceData serviceData
   * @throws DataException
   */
  public LevelAttrAdapter(final Attribute attribute, final Long attrValuId, final ServiceData serviceData)
      throws DataException {
    this.attr = attribute;
    this.valueId = attrValuId;
    this.serviceData = serviceData;
    adapt();
  }

  /**
   * {@inheritDoc}
   * 
   * @throws DataException
   */
  @Override
  public void adapt() throws DataException {
    super.setLevelNo(this.attr.getLevel().intValue());
    super.setLevelAttrId(this.attr.getId());
    this.setLevelAttrValueId();
    this.setLevelName();
  }

  /**
   *
   */
  public void setLevelAttrValueId() {
    if (isAttrIdNotNull()) {
      super.setLevelAttrValueId(this.valueId);
    }
    else {
      super.setLevelAttrValueId(-1);
    }
  }

  /**
   * @throws DataException
   */
  public void setLevelName() throws DataException {
    if (isAttrIdNotNull()) {
      super.setLevelName(new AttributeValueLoader(this.serviceData).getDataObjectByID(this.valueId).getName());
    }
    else {
      super.setLevelName("NULL");
    }
  }

  private boolean isAttrIdNotNull() {
    return this.attr.getId() != null;
  }
}
