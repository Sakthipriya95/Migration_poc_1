/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.attr;


/**
 * @author rgo7cob
 */
public class AttributeValueDontCare extends AttributeValue {

  /**
   * S-UID
   */
  private static final long serialVersionUID = -2214196145320955822L;

  private Long id;
  private Long attributeId;

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    this.id = objId;

  }

  /**
   * @return the attributeId
   */
  @Override
  public Long getAttributeId() {
    return this.attributeId;
  }

  /**
   * @param attributeId the attributeId to set
   */
  @Override
  public void setAttributeId(final Long attributeId) {
    this.attributeId = attributeId;
  }


}
