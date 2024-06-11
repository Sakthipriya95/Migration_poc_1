/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.fm;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersionAttr;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * @author bne4cob
 */
// ICDM-2569
public class FocusMatrixVersionAttrClientBO implements IDataObject {


  /**
   * FocusMatrixVersionAttr
   */
  private final FocusMatrixVersionAttr focusMatrixVersionAttr;
  /**
   * PidcDataHandler
   */
  private final PidcDataHandler pidcDataHandler;
  /**
   * PidcVersion
   */
  private final PidcVersion pidcVersion;


  /**
   * @param focusMatrixVersionAttr primary key
   * @param pidcDataHandler Attribute
   * @param pidcVersion PidcVersion
   */
  protected FocusMatrixVersionAttrClientBO(final FocusMatrixVersionAttr focusMatrixVersionAttr,
      final PidcDataHandler pidcDataHandler, final PidcVersion pidcVersion) {
    this.focusMatrixVersionAttr = focusMatrixVersionAttr;
    this.pidcDataHandler = pidcDataHandler;
    this.pidcVersion = pidcVersion;

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getAttribute().getName();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getAttribute().getDescription();
  }

  /**
   * @return Attribute
   */
  public Attribute getAttribute() {
    return this.pidcDataHandler.getAttributeMap().get(this.focusMatrixVersionAttr.getAttrId());
  }


  /**
   * @return the pidcVrsn
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * ICDM-2227
   *
   * @return ApicConstants.PROJ_ATTR_USED_FLAG
   */
  public ApicConstants.PROJ_ATTR_USED_FLAG getUsedFlag() {
    String dbUsedInfo = this.focusMatrixVersionAttr.getUsed();
    return ApicConstants.PROJ_ATTR_USED_FLAG.getType(dbUsedInfo);
  }

  /**
   * @return attribute value if set
   */
  public AttributeValue getValue() {

    return this.focusMatrixVersionAttr.getValueId() == null ? null
        : this.pidcDataHandler.getAttributeValueMap().get(this.focusMatrixVersionAttr.getValueId());
  }

  /**
   * @return the variant if available
   */
  public PidcVariant getVariant() {

    return this.focusMatrixVersionAttr.getVariantId() == null ? null
        : this.pidcDataHandler.getVariantMap().get(this.focusMatrixVersionAttr.getVariantId());
  }

  /**
   * @return the sub-variant if available
   */
  public PidcSubVariant getSubVariant() {
    return this.focusMatrixVersionAttr.getSubVariantId() == null ? null
        : this.pidcDataHandler.getSubVariantMap().get(this.focusMatrixVersionAttr.getSubVariantId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.focusMatrixVersionAttr.getId();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    // Not applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.focusMatrixVersionAttr.getVersion();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    // Not applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    // Not applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    // Not applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedUser(final String user) {
    // Not applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    // Not applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedDate() {
    return this.focusMatrixVersionAttr.getCreatedDate();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final String date) {
    // Not applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedDate() {
    return this.focusMatrixVersionAttr.getModifiedDate();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedDate(final String date) {
    // Not applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return this.focusMatrixVersionAttr.getCreatedUser();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return this.focusMatrixVersionAttr.getModifiedUser();
  }


  /**
   * @return String
   */
  public String getRemarks() {
    return this.focusMatrixVersionAttr.getFmAttrRemarks();
  }


}
