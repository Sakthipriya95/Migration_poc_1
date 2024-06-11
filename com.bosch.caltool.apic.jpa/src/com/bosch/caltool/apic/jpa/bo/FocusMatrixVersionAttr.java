/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersionAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author bne4cob
 */
//ICDM-2569
public class FocusMatrixVersionAttr extends ApicObject {


  /**
   * @param apicDataProvider ApicDataProvider instance
   * @param fmVersID primary key
   */
  protected FocusMatrixVersionAttr(final ApicDataProvider apicDataProvider, final Long fmVersID) {
    super(apicDataProvider, fmVersID);
    getDataCache().addRemoveFocusMatrixVersionAttr(this, true);
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
    return getDataProvider().getAttribute(getEntityObj().getTabvAttribute().getAttrId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ApicDataProvider getDataProvider() {
    return (ApicDataProvider) super.getDataProvider();
  }

  /**
   * @return entity object TFocusMatrixVersionAttr
   */
  private TFocusMatrixVersionAttr getEntityObj() {
    return getEntityProvider().getDbFocuMatrixVersionAttr(getID());
  }

  /**
   * @return the pidcVrsn
   */
  public PIDCVersion getPidcVersion() {
    return getDataCache().getFocusMatrixVersion(getEntityObj().getTFocusMatrixVersion().getFmVersId()).getPidcVersion();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.FOCUS_MATRIX_VERSION_ATTR;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityObj().getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityObj().getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityObj().getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityObj().getModifiedDate());
  }

  /**
   * ICDM-2227
   *
   * @return ApicConstants.PROJ_ATTR_USED_FLAG
   */
  public ApicConstants.PROJ_ATTR_USED_FLAG getUsedFlag() {
    String dbUsedInfo = getEntityObj().getUsed();
    return ApicConstants.PROJ_ATTR_USED_FLAG.getType(dbUsedInfo);
  }

  /**
   * @return attribute value if set
   */
  public AttributeValue getValue() {
    TabvAttrValue dbAttrVal = getEntityObj().getTabvAttrValue();
    return dbAttrVal == null ? null : getDataCache().getAttrValue(dbAttrVal.getValueId());
  }

  /**
   * @return the variant if available
   */
  public PIDCVariant getVariant() {
    TabvProjectVariant dbVar = getEntityObj().getTabvProjectVariant();
    return dbVar == null ? null : getPidcVersion().getVariantsMap().get(dbVar.getVariantId());
  }

  /**
   * @return the sub-variant if available
   */
  public PIDCSubVariant getSubVariant() {
    TabvProjectSubVariant dbSVar = getEntityObj().getTabvProjectSubVariant();
    return dbSVar == null ? null : getVariant().getSubVariantsMap().get(dbSVar.getSubVariantId());
  }


}
